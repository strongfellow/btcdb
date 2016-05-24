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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
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

    private static final Logger logger = LoggerFactory.getLogger(StrongfellowDB.class);

    @Autowired
    NamedParameterJdbcTemplate template;

    @Autowired
    ReadQueries readQueries;

    @Autowired
    WriteQueries writeQueries;

    @Autowired
    public void setMetricsRegistry(MetricRegistry r) {
        this.metricRegistry = r;
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(r)
                .outputTo(logger)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS);
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

    private final Map<String, Timer> timers = new HashMap<>();

    private synchronized Timer getTimer(String...names) {
        String name = metricRegistry.name(getClass(), names);
        if (timers.containsKey(name)) {
            return timers.get(name);
        } else {
            Timer timer = this.metricRegistry.timer(name);
            timers.put(name, timer);
            return timer;
        }
    }
    public void insertBlock(Block block) throws DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "block").time();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("previous", block.getHeader().getPreviousBlock());
            map.put("hash", block.getMetadata().getHash());
            template.update(writeQueries.ensureBlocks(), map);
        } finally {
            context.stop();
        }
    }

    public void insertBlockchain(Block block) throws DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "chain").time();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("previous", block.getHeader().getPreviousBlock());
            map.put("hash", block.getMetadata().getHash());
            template.update(writeQueries.insertBlockchain(), map);
        } finally {
            context.stop();
        }
    }

    public  void insertBlockDetails(Block block) throws DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "blockdetails").time();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("hash", block.getMetadata().getHash());
            map.put("size", block.getMetadata().getSize());
            map.put("version", block.getHeader().getVersion());
            map.put("merkle", block.getHeader().getMerkleRoot());
            map.put("timestamp", block.getHeader().getTimestamp());
            map.put("bits", block.getHeader().getBits());
            map.put("nonce", block.getHeader().getNonce());
            template.update(writeQueries.insertBlocksDetails(), map);
        } finally {
            context.stop();
        }
    }

    public void ensureTransactionsAndTransactionReferences(Block block) throws DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "transactions").time();
        try {
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
        } finally {
            context.stop();
        }
    }

    public void associateTransactionsWithBlock(Block block) throws DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "transaction", "associations").time();
        try {
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
        } finally {
            context.stop();
        }
    }

    public void ensureTxouts(Block block) throws DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "block", "txouts").time();
        try {
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
        } finally {
            context.stop();
        }
    }

    public void ensureTxins(Block block) throws DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "block", "txins").time();
        try {
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
        } finally {
            context.stop();
        }
    }

    public void ensureTransactionDetails(List<Transaction> transactions) throws DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "transaction", "details").time();
        try {
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
        } finally {
            context.stop();
        }
    }

    public void insertCoinbase(Block block) throws DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "block", "coinbase").time();
        try {
            Transaction t = block.getTransactions().get(0);
            Map<String, byte[]> params = new HashMap<>();
            params.put("tx", t.getMetadata().getHash());
            params.put("coinbase", t.getInputs().get(0).getScript());
            template.update(writeQueries.insertCoinbase(), params);
        } finally {
            context.stop();
        }
    }

    public void updateDescendents(byte[] previous) throws IOException {
        Timer.Context context = getTimer("insert", "block", "descendents").time();
        try {
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
        } finally {
            context.stop();
        }
    }

    public void ensureSpends(Block block) throws IOException {
        Timer.Context context = getTimer("insert", "block", "spends").time();
        try {
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
        } finally {
            context.stop();
        }
    }

    public void ensureValues(Block block) throws IOException {
        Timer.Context context = getTimer("insert", "block", "values").time();
        try {
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
        } finally {
            context.stop();
        }
    }

    public void addHash160s(List<Transaction> transactions) throws UnknownOpCodeException, DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "block", "addresses").time();
        try {
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
        } finally {
            context.stop();
        }
    }

    public void addScripts(List<Transaction> transactions) throws UnknownOpCodeException, DigestException, DataAccessException, IOException {
        Timer.Context context = getTimer("insert", "block", "scripts").time();
        try {
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
        } finally {
            context.stop();
        }
    }

}
