
package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.security.DigestException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
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


    private static final Logger logger = LoggerFactory.getLogger(ReadOnlyRepository.class);
    private MetricRegistry metricRegistry;

    @Autowired
    public void setMetricsRegistry(MetricRegistry r) {
        this.metricRegistry = r;
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(r)
                .outputTo(logger)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(60, TimeUnit.SECONDS);
    }

    @Autowired
    NamedParameterJdbcTemplate template;

    @Autowired
    public void setQueryCache(QueryCache qc) {
        this.readQueries = new ReadQueries(qc);
    }

    private ReadQueries readQueries;

    @Timed(name="read.block.summary")
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
    }

    @Timed(name="read.block.numtx")
    public void setBlockNumTx(BlockSummary result) throws DecoderException, DataAccessException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(result.getHash()));
        template.query(readQueries.getNumTx(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet r) throws SQLException {
                result.setNumTx(r.getInt("count"));
            }
        });
    }

    @Timed(name="read.block.parent")
    public void setBlockParent(BlockSummary result) throws DecoderException, DataAccessException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(result.getHash()));
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
    }

    @Timed(name="read.block.children")
    public void setChildren(BlockSummary result) throws DecoderException, DataAccessException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(result.getHash()));
        template.query(readQueries.getChildren(),  map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] child = rs.getBytes("child");
                result.addChild(rs.wasNull() ? null : Hashes.toBigEndian(child));
            }
        });
    }

    @Timed(name="read.block.sumoftxouts")
    public void setSumOfTxouts(BlockSummary result) throws DataAccessException, IOException, DecoderException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(result.getHash()));

        template.query(readQueries.getSumOfTxOuts(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("sum");
                result.setSumOfTxOuts(rs.wasNull() ? null : v);
            }
        });
    }

    @Timed(name="read.block.sumoftxins")
    public void setSumOfTxins(BlockSummary result) throws DataAccessException, IOException, DecoderException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(result.getHash()));
        template.query(readQueries.getSumOfTxIns(),  map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("sumOfTxins");
                result.setSumOfTxins(rs.wasNull() ? 0 : v);
            }
        });
    }

    @Timed(name="read.block.coinbase.value")
    public void setCoinbaseValue(BlockSummary result) throws DataAccessException, IOException, DecoderException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(result.getHash()));
        template.query(readQueries.getCoinbaseValue(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("coinbase");
                result.setCoinbaseValue(rs.wasNull() ? null : v);
            }
        });
    }

    @Timed(name="read.block.coinbase.script")
    public void setCoinbaseScriipt(BlockSummary result) throws DataAccessException, IOException, DecoderException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(result.getHash()));
        template.query(readQueries.getCoinbaseScript(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] script = rs.getBytes("coinbase");
                result.setCoinbaseScript(rs.wasNull() ? null : script);
            }
        });
    }

    @Timed(name="read.block.tip")
    public void setTip(BlockSummary result) throws DataAccessException, IOException, DecoderException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(result.getHash()));
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

    @Timed(name="read.block.txouts")
    public void addOutputs(BlockSummary bs) throws DecoderException, DataAccessException, IOException {
        String hash = bs.getHash();
        Map<String, Object> params = new HashMap<>();
        params.put("hash", Hashes.fromBigEndian(hash));

        template.query(readQueries.getBlockTxouts(), params, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int i = rs.getInt("transaction_index");
                if (rs.wasNull()) {
                    throw new SQLException("we expect an index");
                }
                TransactionSummary transactionSummary = bs.getTransactions().get(i);
                int j = rs.getInt("txout_index");
                if (rs.wasNull()) {
                    throw new SQLException("we expect a txout index");
                }
                if (j == transactionSummary.getOutputs().size()) {
                    transactionSummary.getOutputs().add(new Txout());
                }
                Txout txout = transactionSummary.getOutputs().get(j);
                byte[] tx = rs.getBytes("tx");
                if (!rs.wasNull()) {
                    Spend spend = new Spend();
                    spend.setTx(Hashes.toBigEndian(tx));
                    spend.setIndex(rs.getInt("index"));
                    txout.addSpend(spend);;
                }
                byte[] address = rs.getBytes("address");
                try {
                    txout.setAddress(rs.wasNull() ? null : address);
                } catch (DigestException e) {
                    throw new SQLException(e);
                }
                txout.setValue(rs.getLong("value"));
            }
        });
    }

    @Timed(name="read.transaction.summary")
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

    @Timed(name="read.transaction.txouts")
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

    @Timed(name="read.transaction.summary")
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

    @Timed(name="read.transaction.summary")
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

    @Timed(name="read.transaction.input.addresses")
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

    @Timed(name="read.transaction.blocks")
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

    @Timed(name="read.block.transactions")
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


    @Timed(name="read.block.txins")
    public void addInputs(BlockSummary bs) throws DecoderException, DataAccessException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("hash", Hashes.fromBigEndian(bs.getHash()));
        template.query(readQueries.getBlockTxins(), params, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                TransactionSummary ts = bs.getTransactions().get(
                        rs.getInt("transaction_index"));
                Txin txin = new Txin();
                ts.getInputs().add(txin);
                byte[] address = rs.getBytes("address");
                try {
                    txin.setAddress(rs.wasNull() ? null : address);
                } catch (DigestException e) {
                    throw new SQLException(e);
                }
                txin.setTxout(Hashes.toBigEndian(rs.getBytes("tx")));
                txin.setIndex(rs.getInt("index"));
                txin.setValue(rs.getLong("value"));
            }
        });
    }

}
