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
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.strongfellow.btcdb.logic.Hashes;
import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Input;
import com.strongfellow.btcdb.protocol.Output;
import com.strongfellow.btcdb.protocol.Transaction;
import com.strongfellow.btcdb.script.ParsedScript;
import com.strongfellow.btcdb.script.UnknownOpCodeException;

@Repository
public class StrongfellowDB {

    private static final Logger logger = LoggerFactory.getLogger(StrongfellowDB.class);

    @Autowired
    NamedParameterJdbcTemplate template;

    private final Map<String, String> cachedQueries = new HashMap<>();

    private String loadQuery(String path) throws IOException {
        String result = cachedQueries.get(path);
        if (result == null) {
            String hot = IOUtils.toString(getClass().getResourceAsStream("queries/" + path + ".sql"), "ascii");
            cachedQueries.put(path, hot);
            return hot;
        } else {
            return result;
        }
    }

    private String associateTransactionsWithBlocks() throws IOException {
        return loadQuery("block/associate_transactions_with_block");
    }

    private String ensureBlocks() throws IOException {
        return loadQuery("block/ensure_blocks");
    }

    private String ensureTransactions() throws IOException {
        return loadQuery("block/ensure_transactions");
    }

    private String insertBlockchain() throws IOException {
        return loadQuery("block/insert_blockchain");
    }

    private String insertBlocksDetails() throws IOException {
        return loadQuery("block/insert_blocks_details");
    }

    private String updateDescendents() throws IOException {
        return loadQuery("block/update_descendents");
    }

    private String ensureSpends() throws IOException {
        return loadQuery("transaction/ensure_spends");
    }

    private String ensureTxins() throws IOException {
        return loadQuery("transaction/ensure_txins");
    }

    private String ensureTxouts() throws IOException {
        return loadQuery("transaction/ensure_txouts");
    }
    private String ensureValues() throws IOException {
        return loadQuery("transaction/ensure_values");
    }

    private String getBlockDetails() throws IOException {
        return loadQuery("reads/get_block_details");
    }

    private String getChildren() throws IOException {
        return loadQuery("reads/get_children");
    }

    private String getNumTx() throws IOException {
        return loadQuery("reads/get_num_tx");
    }

    private String getParent() throws IOException {
        return loadQuery("reads/get_parent");
    }

    private String getSumOfTxIns() throws IOException {
        return loadQuery("reads/get_sum_of_txins");
    }
    private String getSumOfTxOuts() throws IOException {
        return loadQuery("reads/get_sum_of_txouts");
    }
    private String getCoinbaseValue() throws IOException {
        return loadQuery("reads/get_coinbase_value");
    }

    private String insertCoinbase() throws IOException {
        return loadQuery("block/insert_coinbase");
    }

    private String insertPublicKeyTxouts() throws IOException {
        return loadQuery("transaction/ensure_pks");
    }

    private String getCoinbaseScript() throws IOException {
        return loadQuery("reads/get_coinbase_script");
    }

    private void insertBlockchain(Block block) throws DataAccessException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("previous", block.getHeader().getPreviousBlock());
        map.put("hash", block.getMetadata().getHash());
        template.update(ensureBlocks(), map);
        int x = template.update(insertBlockchain(), map);

        map.put("size", block.getMetadata().getSize());
        map.put("version", block.getHeader().getVersion());
        map.put("merkle", block.getHeader().getMerkleRoot());
        map.put("timestamp", block.getHeader().getTimestamp());
        map.put("bits", block.getHeader().getBits());
        map.put("nonce", block.getHeader().getNonce());
        template.update(insertBlocksDetails(), map);
    }

    private void ensureTransactionsAndTransactionReferences(Block block) throws DataAccessException, IOException {
        int max = 999;
        Map<String, Object> map = new HashMap<>();
        List<Object[]> rows = new ArrayList<>();
        map.put("tx_hashes", rows);
        for (Transaction t : block.getTransactions()) {
            rows.add(new Object[] {t.getMetadata().getHash()});
            if (rows.size() == max) {
                template.update(ensureTransactions(), map);
                rows.clear();
            }
            for (Input input : t.getInputs()) {
                rows.add(new Object[] { input.getHash()});
                if (rows.size() == max) {
                    template.update(ensureTransactions(), map);
                    rows.clear();
                }
            }
        }
        if (rows.size() > 0) {
            template.update(ensureTransactions(), map);
            rows.clear();
        }
    }

    private void associateTransactionsWithBlock(Block block) throws DataAccessException, IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", block.getMetadata().getHash());
        List<Object[]> txHashes = new ArrayList<>();
        map.put("tx_hashes", txHashes);

        int i = 0;
        for (Transaction t : block.getTransactions()) {
            Object[] row = new Object[] { i++, t.getMetadata().getHash()};
            txHashes.add(row);
            if (i == block.getTransactions().size() || txHashes.size() == 400) {
                template.update(associateTransactionsWithBlocks(), map);
                txHashes.clear();
            }
        }
    }

    private void ensureTxouts(Block block) throws DataAccessException, IOException {
        int n = 449;
        List<Object[]> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("txouts", rows);
        for (Transaction t : block.getTransactions()) {
            for (Input input : t.getInputs()) {
                rows.add(new Object[] {  input.getIndex(), input.getHash()});
                if (rows.size() == n) {
                    template.update(ensureTxouts(), map);
                    rows.clear();
                }
            }
            for (int i = 0; i < t.getOutputs().size(); i++) {
                rows.add(new Object[] { i, t.getMetadata().getHash()});
                if (rows.size() == n) {
                    template.update(ensureTxouts(), map);
                    rows.clear();
                }
            }
        }
        if (rows.size() > 0) {
            template.update(ensureTxouts(), map);
        }
    }

    private void ensureTxins(Block block) throws DataAccessException, IOException {
        int n = 249;
        List<Object[]> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("txins", rows);
        for (Transaction t : block.getTransactions()) {
            byte[] txHash = t.getMetadata().getHash();
            int index = 0;
            for (Input input : t.getInputs()) {
                Object[] row = new Object[] {
                        txHash, index, input.getSequence()
                };
                rows.add(row);
                if (rows.size() == n) {
                    template.update(ensureTxins(), map);
                    rows.clear();
                }
            }
            index++;
        }
        if (rows.size() > 0) {
            template.update(ensureTxins(), map);
        }
    }

    public void addBlock(Block block) throws UnknownOpCodeException, IOException {
        insertBlockchain(block);
        ensureTransactionsAndTransactionReferences(block);
        associateTransactionsWithBlock(block);
        ensureTxouts(block);
        ensureTxins(block);
        ensureSpends(block);
        ensureValues(block);
        updateDescendents(block.getHeader().getPreviousBlock());
        insertCoinbase(block);
    }

    private void insertCoinbase(Block block) throws DataAccessException, IOException {
        Transaction t = block.getTransactions().get(0);
        Map<String, byte[]> params = new HashMap<>();
        params.put("tx", t.getMetadata().getHash());
        params.put("coinbase", t.getInputs().get(0).getScript());
        template.update(insertCoinbase(), params);
    }

    private void updateDescendents(byte[] previous) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", previous);

        Integer h = null;
        List<Map<String, Object>> rows = template.queryForList(
                getBlockHeight(), map);

        for (Map<String, Object> m : rows) {
            h = (Integer) m.get("height");
        }
        if (h != null) {
            while (true) {
                map.put("height", h++);
                int n = template.update(updateDescendents(), map);
                if (n == 0) {
                    break;
                }
            }
        }
    }

    private String getBlockHeight() throws IOException {
        return loadQuery("reads/get_block_height");
    }

    private void ensureSpends(Block block) throws IOException {
        int n = 249;
        String sql = ensureSpends();
        List<Object[]> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("spends", rows);
        for (Transaction t : block.getTransactions()) {
            byte[] txHash = t.getMetadata().getHash();
            for (int i = 0; i < t.getInputs().size(); i++) {
                Input input = t.getInputs().get(i);
                rows.add(new Object[] {
                        txHash, i,
                        input.getHash(), input.getIndex()
                });
                if (rows.size() == n) {
                    template.update(sql, map);
                    rows.clear();
                }
            }
        }
        if (rows.size() > 0) {
            template.update(sql, map);
        }
    }

    private void ensureValues(Block block) throws IOException {
        int n = 333;
        String sql = ensureValues();
        List<Object[]> rows = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("values", rows);
        for (Transaction t : block.getTransactions()) {
            for (int i = 0; i < t.getOutputs().size(); i++) {
                rows.add(new Object[] {
                        t.getMetadata().getHash(), i, t.getOutputs().get(i).getValue()
                });
                if (rows.size() == n) {
                    template.update(sql, map);
                    rows.clear();
                }
            }
        }
        if (rows.size() > 0) {
            template.update(sql, map);
        }

    }

    private static byte[] hash(String hash) throws DecoderException {
        byte[] result = Hex.decodeHex(hash.toCharArray());
        ArrayUtils.reverse(result);
        return result;
    }

    public BlockSummary getBlockSummary(String block) throws IOException, DecoderException {
        Map<String, Object> map = new HashMap<>();
        map.put("hash", hash(block));
        String sql = getBlockDetails();
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
                ArrayUtils.reverse(merkle);
                result.setMerkle(Hex.encodeHexString(merkle));
                return result;
            }

        });

        template.query(getNumTx(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet r) throws SQLException {
                blockSummary.setNumTx(r.getInt("count"));
            }
        });

        template.query(getParent(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] parent = rs.getBytes("parent");
                ArrayUtils.reverse(parent);
                blockSummary.setParent(Hex.encodeHexString(parent));
                int h = rs.getInt("height");
                if (!rs.wasNull()) {
                    blockSummary.setHeight(h);
                }
            }
        });

        template.query(getChildren(),  map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] child = rs.getBytes("child");
                ArrayUtils.reverse(child);
                blockSummary.addChild(Hex.encodeHexString(child));
            }
        });

        template.query(getSumOfTxOuts(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("sum");
                blockSummary.setSumOfTxOuts(rs.wasNull() ? null : v);
            }
        });

        template.query(getSumOfTxIns(),  map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("sumOfTxins");
                blockSummary.setSumOfTxins(rs.wasNull() ? 0 : v);
            }
        });

        template.query(getCoinbaseValue(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long v = rs.getLong("coinbase");
                blockSummary.setCoinbaseValue(rs.wasNull() ? null : v);
            }
        });

        template.query(getCoinbaseScript(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] script = rs.getBytes("coinbase");
                blockSummary.setCoinbaseScript(rs.wasNull() ? null : script);
            }
        });

        return blockSummary;
    }

    public void addScripts(List<Transaction> transactions) throws UnknownOpCodeException, DigestException, DataAccessException, IOException {
        List<Object[]> pks = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("pks", pks);
        for (Transaction t : transactions) {
            byte[] tx = t.getMetadata().getHash();
            int i = 0;
            for (Output txout : t.getOutputs()) {
                byte[] script = txout.getScript();
                ParsedScript parsedScript = ParsedScript.from(script);
                if (parsedScript.isPayToPublicKey()) {
                    byte[] pk = parsedScript.getPublicKey();
                    Object[] row = new Object[] {
                            tx, i, pk
                    };
                    pks.add(row);
                    if (pks.size() == 200) {
                        template.update(insertPublicKeyTxouts(), params);
                        params.clear();
                    }
                    String base58check = Hashes.publicKeyHashAddressToBase58(pk);
                    logger.info("public key: {}", base58check);
                }
                i++;
            }
        }
        if (pks.size() > 0) {
            template.update(insertPublicKeyTxouts(), params);
        }
    }
}
