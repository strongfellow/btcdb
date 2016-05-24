
package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.security.DigestException;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.strongfellow.btcdb.logic.Hashes;
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
    private ReadQueries readQueries;

    public BlockSummary getBlockSummary(String block) throws IOException, DecoderException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", Hashes.fromBigEndian(block));
        String sql = readQueries.getBlockDetails();
        final BlockSummary blockSummary = template.queryForObject(sql, map, new RowMapper<BlockSummary>() {

            @Override
            public BlockSummary mapRow(ResultSet rs, int arg1) throws SQLException {
                BlockSummary result = new BlockSummary();
                result.setSize(rs.getInt("size"));
                result.setBits(rs.getLong("bits"));
                result.setTimestamp(rs.getLong("timestamp"));
                result.setVersion(rs.getLong("version"));
                result.setNonce(rs.getLong("nonce"));
                byte[] merkle = rs.getBytes("merkle");
                result.setMerkle(rs.wasNull() ? null : Hashes.toBigEndian(merkle));
                return result;
            }

        });

        template.query(readQueries.getNumTx(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet r) throws SQLException {
                blockSummary.setNumTx(r.getInt("count"));
            }
        });

        template.query(readQueries.getParent(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] parent = rs.getBytes("parent");
                blockSummary.setParent(rs.wasNull() ? null : Hashes.toBigEndian(parent));
                int h = rs.getInt("height");
                if (!rs.wasNull()) {
                    blockSummary.setHeight(h);
                }
            }
        });

        template.query(readQueries.getChildren(),  map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] child = rs.getBytes("child");
                blockSummary.addChild(rs.wasNull() ? null : Hashes.toBigEndian(child));
            }
        });

        template.query(readQueries.getSumOfTxOuts(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("sum");
                blockSummary.setSumOfTxOuts(rs.wasNull() ? null : v);
            }
        });

        template.query(readQueries.getSumOfTxIns(),  map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("sumOfTxins");
                blockSummary.setSumOfTxins(rs.wasNull() ? 0 : v);
            }
        });

        template.query(readQueries.getCoinbaseValue(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("coinbase");
                blockSummary.setCoinbaseValue(rs.wasNull() ? null : v);
            }
        });

        template.query(readQueries.getCoinbaseScript(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] script = rs.getBytes("coinbase");
                blockSummary.setCoinbaseScript(rs.wasNull() ? null : script);
            }
        });

        return blockSummary;
    }

    public TransactionSummary getTransactionSummmary(String hash) throws DecoderException, DataAccessException, IOException {
        TransactionSummary transactionSummary = new TransactionSummary();
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


        Map<Integer, Txout> m = new HashMap<>();
        template.query(readQueries.getTxouts(), params, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Spend spend = null;
                byte[] tx = rs.getBytes("tx");
                if (!rs.wasNull()) {
                    spend = new Spend();
                    spend.setTx(Hashes.toBigEndian(tx));
                    spend.setIndex(rs.getInt("index"));
                }

                int row = rs.getInt("row");
                if (!rs.wasNull() && m.containsKey(row)) {
                    m.get(row).getSpends().add(spend);
                } else {
                    Txout t = new Txout();
                    transactionSummary.addOutput(t);
                    m.put(row, t);
                    byte[] address = rs.getBytes("address");
                    try {
                        t.setAddress(rs.wasNull() ? null : address);
                    } catch (DigestException e) {
                        throw new SQLException(e);
                    }
                    t.setValue(rs.getLong("value"));
                    if (spend != null) {
                        t.addSpend(spend);
                    }
                }
            }
        });


        List<Txin> txins = template.query(readQueries.getTxins(), params, new RowMapper<Txin>() {

            @Override
            public Txin mapRow(ResultSet rs, int rowNum) throws SQLException {
                Txin t = new Txin();
                byte[] address = rs.getBytes("address");
                try {
                    t.setAddress(rs.wasNull() ? null : address);
                } catch (DigestException e) {
                    throw new SQLException(e);
                }
                t.setValue(rs.getLong("value"));
                byte[] tx = rs.getBytes("tx");
                t.setTxout(rs.wasNull() ? null : Hashes.toBigEndian(tx));
                t.setIndex(rs.getInt("index"));
                return t;
            }
        });
        for (Txin i : txins) {
            transactionSummary.addInput(i);
        }
        return transactionSummary;
    }


}
