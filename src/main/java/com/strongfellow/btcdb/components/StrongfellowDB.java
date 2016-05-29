package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.security.DigestException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.strongfellow.btcdb.logic.Hashes;
import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Input;
import com.strongfellow.btcdb.protocol.Output;
import com.strongfellow.btcdb.protocol.Transaction;
import com.strongfellow.btcdb.script.ParsedScript;
import com.strongfellow.btcdb.script.UnknownOpCodeException;

@Repository
public class StrongfellowDB {

    private MetricRegistry metricRegistry;
    private NamedParameterJdbcTemplate template;

    private ReadQueries readQueries;
    private WriteQueries writeQueries;

    private static final Logger logger = LoggerFactory.getLogger(StrongfellowDB.class);

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Autowired
    public void setQueryCache(QueryCache queryCache) {
        this.readQueries = new ReadQueries(queryCache);
        this.writeQueries = new WriteQueries(queryCache);
    }

    @Autowired
    public void setMetricsRegistry(MetricRegistry r) {
        /**
        this.metricRegistry = r;
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(r)
                .outputTo(logger)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS);
         */
    }

    public StrongfellowDB() {
    }


    private void maybeUpdate(String query, Map<String, Object> map, List<Object[]> rows, Object[] row) {
        if ((rows.size() + 1) * row.length >= 1000) {
            template.update(query, map);
            rows.clear();
        }
        rows.add(row);
    }

    @Timed(name="insert.block")
    public void insertBlock(byte[] hash, byte[] previous) throws DataAccessException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("previous", previous);
        map.put("hash", hash);
        template.update(writeQueries.ensureBlocks(), map);
    }

    @Timed(name="insert.block.chain")
    public void insertBlockchain(byte[] hash, byte[] parent) throws DataAccessException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("parent", parent);
        map.put("hash", hash);
        template.update(writeQueries.insertBlockchain(), map);
    }

    @Timed(name="insert.block.details")
    public  void insertBlockDetails(Block block) throws DataAccessException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", block.getMetadata().getHash());
        map.put("size", block.getMetadata().getSize());
        map.put("version", block.getHeader().getVersion());
        map.put("merkle", block.getHeader().getMerkleRoot());
        map.put("timestamp", block.getHeader().getTimestamp());
        map.put("bits", block.getHeader().getBits());
        map.put("nonce", block.getHeader().getNonce());
        template.update(writeQueries.insertBlocksDetails(), map);
    }

    @Timed(name="insert.block.transactions")
    public void ensureTransactionsAndTransactionReferences(Block block) throws DataAccessException, IOException {
        String query = writeQueries.ensureTransactions();
        Map<String, Object> map = new HashMap<>();
        List<Object[]> rows = new ArrayList<>();
        map.put("tx_hashes", rows);

        for (Transaction t : block.getTransactions()) {
            Object[] row = new Object[] { t.getMetadata().getHash() };
            maybeUpdate(query, map, rows, row);
            for (Input input : t.getInputs()) {
                row = new Object[] { input.getHash() };
                maybeUpdate(query, map, rows, row);
            }
        }
        if (rows.size() > 0) {
            template.update(query, map);
        }
    }

    @Timed(name="insert.block.transactions.associations")
    public void associateTransactionsWithBlock(Block block) throws DataAccessException, IOException {
        String query = writeQueries.associateTransactionsWithBlocks();
        Map<String, Object> map = new HashMap<>();
        map.put("hash", block.getMetadata().getHash());
        List<Object[]> rows = new ArrayList<>();
        map.put("tx_hashes", rows);

        int i = 0;
        for (Transaction t : block.getTransactions()) {
            Object[] row = new Object[] { i++, t.getMetadata().getHash()};
            maybeUpdate(query, map, rows, row);
        }
        if (rows.size() > 0) {
            template.update(query, map);
        }
    }

    @Timed(name="insert.block.txouts")
    public void ensureTxouts(Block block) throws DataAccessException, IOException {
        String query = writeQueries.ensureTxouts();
        List<Object[]> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("txouts", rows);
        for (Transaction t : block.getTransactions()) {
            for (Input input : t.getInputs()) {
                Object[] row = new Object[] { input.getIndex(), input.getHash() };
                maybeUpdate(query, map, rows, row);
            }
            for (int i = 0; i < t.getOutputs().size(); i++) {
                Object[] row = new Object[] { i, t.getMetadata().getHash()};
                maybeUpdate(query, map, rows, row);
            }
        }
        if (rows.size() > 0) {
            template.update(query, map);
        }
    }

    @Timed(name="insert.block.txins")
    public void ensureTxins(Block block) throws DataAccessException, IOException {
        String query = writeQueries.ensureTxins();
        List<Object[]> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("txins", rows);
        for (Transaction t : block.getTransactions()) {
            byte[] txHash = t.getMetadata().getHash();
            int index = 0;
            for (Input input : t.getInputs()) {
                Object[] row = new Object[] { txHash, index, input.getSequence() };
                maybeUpdate(query, map, rows, row);
                index++;
            }
        }
        if (rows.size() > 0) {
            template.update(query, map);
        }
    }

    @Timed(name="insert.block.transaction.details")
    public void ensureTransactionDetails(List<Transaction> transactions) throws DataAccessException, IOException {
        String query = writeQueries.ensureTransactionDetails();
        List<Object[]> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("details", rows);

        for (Transaction t : transactions) {
            Object[] row = new Object[] {
                    t.getMetadata().getHash(), t.getMetadata().getSize(), t.getVersion(), t.getnLockTime()
            };
            maybeUpdate(query, map, rows, row);
        }
        if (rows.size() > 0) {
            template.update(query,  map);
        }
    }

    @Timed(name="insert.block.coinbase")
    public void insertCoinbase(Block block) throws DataAccessException, IOException {
        Transaction t = block.getTransactions().get(0);
        Map<String, byte[]> params = new HashMap<>();
        params.put("tx", t.getMetadata().getHash());
        params.put("coinbase", t.getInputs().get(0).getScript());
        template.update(writeQueries.insertCoinbase(), params);
    }

    @Timed(name="insert.block.descendents")
    public void updateDescendents(byte[] previous) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", previous);

        try {
            Integer h = template.queryForObject(readQueries.getBlockHeight(), map, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt("height");
                }
            });
            while (true) {
                map.put("height", h++);
                int n = template.update(writeQueries.updateDescendents(), map);
                if (n == 0) {
                    break;
                } else {
                    logger.info("we updated {} descendents", n);
                }
            }
        } catch(EmptyResultDataAccessException e) {
            logger.info("we failed to get the blockHeight for block {};"
                    + " this is a normal thing if blocks are received out of order",
                    Hashes.toBigEndian(previous));
        }
    }

    @Timed(name="insert.block.spends")
    public void ensureSpends(Block block) throws IOException {
        String sql = writeQueries.ensureSpends();
        List<Object[]> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("spends", rows);
        for (Transaction t : block.getTransactions()) {
            byte[] txHash = t.getMetadata().getHash();
            for (int i = 0; i < t.getInputs().size(); i++) {
                Input input = t.getInputs().get(i);
                Object[] row = new Object[] {
                        txHash, i,
                        input.getHash(), input.getIndex()
                };
                maybeUpdate(sql, map, rows, row);
            }
        }
        if (rows.size() > 0) {
            template.update(sql, map);
        }
    }

    @Timed(name="insert.block.values")
    public void ensureValues(Block block) throws IOException {
        String sql = writeQueries.ensureValues();
        List<Object[]> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("values", rows);
        for (Transaction t : block.getTransactions()) {
            for (int i = 0; i < t.getOutputs().size(); i++) {
                Object[] row = new Object[] {
                        t.getMetadata().getHash(), i, t.getOutputs().get(i).getValue()
                };
                maybeUpdate(sql, map, rows, row);
            }
        }
        if (rows.size() > 0) {
            template.update(sql, map);
        }
    }

    @Timed(name="insert.block.addresses")
    public void addHash160s(List<Transaction> transactions) throws UnknownOpCodeException, DataAccessException, IOException {
        String sql = writeQueries.insertPublicKeys();
        Map<String, Object> map = new HashMap<>();
        List<Object[]> rows = new ArrayList<>();
        map.put("addresses", rows);
        for (Transaction transaction : transactions) {
            for (Output txout : transaction.getOutputs()) {
                byte[] address = null;
                byte[] script = txout.getScript();
                ParsedScript parsedScript = ParsedScript.from(script);
                if (parsedScript.isPayToPublicKey()) {
                    address = parsedScript.getPublicKey();
                } else if (parsedScript.isPayToPubKeyHash()) {
                    address = parsedScript.getPublicKey();
                }
                if (address != null) {
                    maybeUpdate(sql, map, rows, new Object[] {address});
                }
            }
        }
        if (rows.size() > 0) {
            template.update(sql, map);
        }
    }

    @Timed(name="insert.block.scripts")
    public void addScripts(List<Transaction> transactions) throws UnknownOpCodeException, DigestException, DataAccessException, IOException {
        String pkQuery = writeQueries.insertPublicKeyTxouts();
        List<Object[]> pkRows = new ArrayList<>();
        Map<String, Object> pkMap = new HashMap<>();
        pkMap.put("pks", pkRows);

        String pkHashQuery = writeQueries.insertPublicKeyHashTxouts();
        List<Object[]> pkHashRows = new ArrayList<>();
        Map<String, Object> pkHashMap = new HashMap<>();
        pkHashMap.put("pks", pkHashRows);

        for (Transaction t : transactions) {
            byte[] tx = t.getMetadata().getHash();
            int i = 0;
            for (Output txout : t.getOutputs()) {
                byte[] script = txout.getScript();
                ParsedScript parsedScript = ParsedScript.from(script);
                if (parsedScript.isPayToPublicKey()) {
                    byte[] pk = parsedScript.getPublicKey();
                    Object[] row = new Object[] { tx, i, pk };
                    maybeUpdate(pkQuery, pkMap, pkRows, row);
                    // String base58check = Hashes.publicKeyHashAddressToBase58(pk);
                    // logger.info("public key: {}", base58check);
                } else if (parsedScript.isPayToPubKeyHash()) {
                    byte[] pk = parsedScript.getPublicKey();
                    Object[] row = new Object[] { tx, i, pk };
                    maybeUpdate(pkHashQuery, pkHashMap, pkHashRows, row);
                    // String base58check = Hashes.publicKeyHashAddressToBase58(pk);
                    // logger.info("public key: {}", base58check);
                }
                i++;
            }
        }
        if (pkRows.size() > 0) {
            template.update(pkQuery, pkMap);
        }
        if (pkHashRows.size() > 0) {
            template.update(pkHashQuery, pkHashMap);
        }
    }

    public void updateTips(byte[] hash) throws DataAccessException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("hash", hash);

        Integer tip = template.queryForObject(readQueries.chooseTip(), params, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                int result = rs.getInt("tip");
                if (rs.wasNull()) {
                    throw new SQLException("expected to find tip");
                } else {
                    return result;
                }
            }
        });
        params.clear();
        params.put("tip", tip);
        while (true) {
            int n = template.update(writeQueries.extendChain(), params);
            if (n == 0) {
                break;
            } else {

            }
        }
    }
}
