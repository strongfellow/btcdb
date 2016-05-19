package com.strongfellow.btcdb.components;

import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Input;
import com.strongfellow.btcdb.protocol.Transaction;
import com.strongfellow.btcdb.script.UnknownOpCodeException;

@Repository
public class StrongfellowDB {

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
        boolean firstInput = true;
        for (Transaction t : block.getTransactions()) {
            for (Input input : t.getInputs()) {
                if (firstInput) { // this is just coinbase
                    firstInput = false;
                } else {
                    rows.add(new Object[] {  input.getIndex(), input.getHash()});
                    if (rows.size() == n) {
                        template.update(ensureTxouts(), map);
                        rows.clear();
                    }
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
        boolean coinbase = true;
        for (Transaction t : block.getTransactions()) {
            int index = 0;
            for (Input input : t.getInputs()) {
                index++;
                if (coinbase) {
                    coinbase = false;
                } else {
                    Object[] row = new Object[] {
                            t.getMetadata().getHash(), index, input.getSequence()
                    };
                    rows.add(row);
                    if (rows.size() == n) {
                        template.update(ensureTxins(), map);
                        rows.clear();
                    }
                }
            }
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
            for (int i = 0; i < t.getInputs().size(); i++) {
                Input input = t.getInputs().get(i);
                rows.add(new Object[] {
                        t.getMetadata().getHash(), i,
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
                result.size = rs.getInt("size");
                result.bits = rs.getLong("bits");
                result.timestamp = rs.getLong("timestamp");
                result.version = rs.getLong("version");
                result.nonce = rs.getLong("nonce");
                byte[] merkle = rs.getBytes("merkle");
                ArrayUtils.reverse(merkle);
                result.merkle = Hex.encodeHexString(merkle);
                return result;
            }

        });

        template.query(getNumTx(), map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet r) throws SQLException {
                blockSummary.numTx = r.getInt("count");
            }
        });

        template.query(getParent(), map, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] parent = rs.getBytes("parent");
                ArrayUtils.reverse(parent);
                blockSummary.parent = Hex.encodeHexString(parent);
                int h = rs.getInt("height");
                if (!rs.wasNull()) {
                    blockSummary.height = h;
                }
            }
        });

        template.query(getChildren(),  map, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                byte[] child = rs.getBytes("child");
                ArrayUtils.reverse(child);
                blockSummary.children.add(Hex.encodeHexString(child));
            }
        });

        return blockSummary;
    }
}
