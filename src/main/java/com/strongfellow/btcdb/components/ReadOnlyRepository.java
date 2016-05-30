
package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.security.DigestException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.codahale.metrics.annotation.Timed;
import com.strongfellow.btcdb.logic.Hashes;
import com.strongfellow.btcdb.response.BlockPointer;
import com.strongfellow.btcdb.response.BlockSummary;
import com.strongfellow.btcdb.response.Spend;
import com.strongfellow.btcdb.response.TransactionSummary;
import com.strongfellow.btcdb.response.Txin;
import com.strongfellow.btcdb.response.Txout;

@Repository
public class ReadOnlyRepository {

    @Autowired
    NamedParameterJdbcTemplate template;

    @Autowired
    public void setQueryCache(QueryCache qc) {
        this.readQueries = new ReadQueries(qc);
    }

    private ReadQueries readQueries;

    public void setBlockSummaryDetails(BlockSummary result) throws IOException, DecoderException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(result.getHash()));
        template.query(readQueries.getBlockDetails(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                result.setSize(rs.getInt("size"));
                result.setBits(rs.getLong("bits"));
                result.setTimestamp(rs.getLong("timestamp"));
                result.setVersion(rs.getLong("version"));
                result.setNonce(rs.getLong("nonce"));
                byte[] merkle = rs.getBytes("merkle");
                result.setMerkle(rs.wasNull() ? null : Hashes.toBigEndian(merkle));
            }
        });
        template.query(readQueries.getNumTx(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet r) throws SQLException {
                result.setNumTx(r.getInt("count"));
            }
        });

        template.query(readQueries.getParent(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] parent = rs.getBytes("parent");
                result.setParent(rs.wasNull() ? null : Hashes.toBigEndian(parent));
                int h = rs.getInt("height");
                if (!rs.wasNull()) {
                    result.setHeight(h);
                }
            }
        });

        template.query(readQueries.getChildren(),  map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] child = rs.getBytes("child");
                result.addChild(rs.wasNull() ? null : Hashes.toBigEndian(child));
            }
        });

        template.query(readQueries.getSumOfTxOuts(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("sum");
                result.setSumOfTxOuts(rs.wasNull() ? null : v);
            }
        });

        template.query(readQueries.getSumOfTxIns(),  map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("sumOfTxins");
                result.setSumOfTxins(rs.wasNull() ? 0 : v);
            }
        });

        template.query(readQueries.getCoinbaseValue(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("coinbase");
                result.setCoinbaseValue(rs.wasNull() ? null : v);
            }
        });

        template.query(readQueries.getCoinbaseScript(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] script = rs.getBytes("coinbase");
                result.setCoinbaseScript(rs.wasNull() ? null : script);
            }
        });

        template.query(readQueries.getTip(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {

                byte[] tip = rs.getBytes("tip");
                if (rs.wasNull()) {
                    throw new SQLException("we expect to have a tip");
                }
                int depth = rs.getInt("depth");
                if (rs.wasNull()) {
                    throw new SQLException("we expect to have depth");
                }
                result.setDepth(depth);
                result.setTip(Hashes.toBigEndian(tip));
            }
        });
    }

    @Timed
    public void getTransactionSummmary(TransactionSummary transactionSummary) throws DecoderException, DataAccessException, IOException {
        String hash = transactionSummary.getHash();
        Map<String, Object> params = new HashMap<>();
        params.put("hash", Hashes.fromBigEndian(hash));

        template.query(readQueries.getTransactionSummary(), params, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long size = rs.getLong("size");
                transactionSummary.setSize(rs.wasNull() ? null : size);
                long version = rs.getLong("version");
                transactionSummary.setVersion(rs.wasNull() ? null : version);
                long lockTime = rs.getLong("lock_time");
                transactionSummary.setLockTime(rs.wasNull() ? null : lockTime);
                long outputs = rs.getLong("output");
                transactionSummary.setOutputValue(rs.wasNull() ? null : outputs);
            }
        });
    }

    public void addOutputs(TransactionSummary transactionSummary) throws DecoderException, DataAccessException, IOException {
        String hash = transactionSummary.getHash();
        Map<String, Object> params = new HashMap<>();
        params.put("hash", Hashes.fromBigEndian(hash));

        Map<Integer, Txout> m = new HashMap<>();
        template.query(readQueries.getTxouts(), params, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int row = rs.getInt("row");
                if (!m.containsKey(row)) {
                    m.put(row, new Txout());
                }
                Txout t = m.get(row);
                t.setValue(rs.getLong("value"));
                Spend spend = null;
                byte[] tx = rs.getBytes("tx");
                if (!rs.wasNull()) {
                    spend = new Spend();
                    spend.setTx(Hashes.toBigEndian(tx));
                    spend.setIndex(rs.getInt("index"));
                    t.addSpend(spend);
                }
            }
        });

        List<Txout> outputs = new ArrayList<Txout>(m.size());
        for (int i = 0; i < m.size(); i++) {
            if (!m.containsKey(i)) {
                throw new RuntimeException();
            } else {
                outputs.add(i, m.get(i));
            }
        }
        transactionSummary.setOutputs(outputs);
    }

    public void addOutputAddresses(TransactionSummary transactionSummary) throws DecoderException, DataAccessException, IOException {
        String hash = transactionSummary.getHash();
        Map<String, Object> params = new HashMap<>();
        params.put("hash", Hashes.fromBigEndian(hash));

        template.query(readQueries.getOutputAddresses(), params, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int row = rs.getInt("row");
                if (rs.wasNull()) {
                    throw new RuntimeException();
                } else {
                    byte[] address = rs.getBytes("address");
                    if (!rs.wasNull()) {
                        try {
                            transactionSummary.getOutputs().get(row).setAddress(address);
                        } catch (DigestException e) {
                            throw new SQLException(e);
                        }
                    }
                }
            }
        });
    }

    public void addInputs(TransactionSummary transactionSummary) throws DecoderException, DataAccessException, IOException {
        String hash = transactionSummary.getHash();
        Map<String, Object> params = new HashMap<>();
        params.put("hash", Hashes.fromBigEndian(hash));

        Map<Integer, Txin> m = new HashMap<Integer, Txin>();
        template.query(readQueries.getTxins(), params, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int row = rs.getInt("row");
                if (rs.wasNull()) {
                    throw new SQLException();
                }
                Txin t = new Txin();
                t.setValue(rs.getLong("value"));
                byte[] tx = rs.getBytes("tx");
                if (!rs.wasNull()) {
                    t.setTxout(Hashes.toBigEndian(tx));
                    t.setIndex(rs.getInt("index"));
                }
                m.put(row, t);
            }
        });

        List<Txin> inputs = new ArrayList<Txin>(m.size());
        for (int i = 0; i < m.size(); i++) {
            if (!m.containsKey(i)) {
                throw new RuntimeException();
            }
            inputs.add(i, m.get(i));
        }
        transactionSummary.setInputs(inputs);
    }

    public void addInputAddresses(TransactionSummary transactionSummary) throws DataAccessException, IOException, DecoderException {
        String hash = transactionSummary.getHash();
        Map<String, Object> params = new HashMap<>();
        params.put("hash", Hashes.fromBigEndian(hash));

        template.query(readQueries.getInputAddresses(), params, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int row = rs.getInt("row");
                if (rs.wasNull()) {
                    throw new RuntimeException();
                } else {
                    byte[] address = rs.getBytes("address");
                    if (!rs.wasNull()) {
                        try {
                            transactionSummary.getInputs().get(row).setAddress(address);
                        } catch (DigestException e) {
                            throw new SQLException(e);
                        }
                    }
                }
            }
        });
    }

    public void addBlocks(TransactionSummary transactionSummary) throws DataAccessException, IOException, DecoderException {
        String hash = transactionSummary.getHash();
        Map<String, Object> params = new HashMap<>();
        params.put("hash", Hashes.fromBigEndian(hash));
        List<BlockPointer> blocks = new ArrayList<BlockPointer>();
        template.query(readQueries.getBlocksForTransaction(), params, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                BlockPointer blockPointer = new BlockPointer();
                blocks.add(blockPointer);
                byte[] block = rs.getBytes("block");
                if (rs.wasNull()) {
                    throw new SQLException("we expect to get a block back");
                } else {
                    blockPointer.setHash(block);
                    int height = rs.getInt("height");
                    blockPointer.setHeight(rs.wasNull() ? null : height);
                }
            }
        });
        transactionSummary.setBlockPointers(blocks);
    }

    public void addTransactions(BlockSummary bs) throws DecoderException, DataAccessException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("hash", Hashes.fromBigEndian(bs.getHash()));
        List<TransactionSummary> transactions = template.query(
                readQueries.getTransactionsForBlock(), params, new RowMapper<TransactionSummary>() {

                    @Override
                    public TransactionSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
                        byte[] tx = rs.getBytes("tx");
                        if (rs.wasNull()) {
                            throw new SQLException("we expect tx");
                        }
                        TransactionSummary ts = new TransactionSummary();
                        ts.setHash(Hashes.toBigEndian(tx));
                        return ts;
                    }
                });
        bs.setTransactions(transactions);
    }

}
