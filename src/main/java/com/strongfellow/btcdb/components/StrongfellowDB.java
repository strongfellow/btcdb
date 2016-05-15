package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Input;
import com.strongfellow.btcdb.protocol.Transaction;
import com.strongfellow.btcdb.script.UnknownOpCodeException;

@Repository
public class StrongfellowDB {

    @Autowired
    NamedParameterJdbcTemplate template;

    private final String ensureBlocks;
    private final String insertBlockchain;
    private final String insertBlocksDetails;

    private final String ensureTransactions;

    private String loadQuery(String path) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("queries/" + path + ".sql"), "ascii");
    }

    public StrongfellowDB() throws IOException {
        ensureBlocks = loadQuery("block/00100_ensure_blocks");
        insertBlockchain = loadQuery("block/00200_insert_blockchain");
        insertBlocksDetails = loadQuery("block/00300_insert_blocks_details");
        ensureTransactions = loadQuery("block/00500_ensure_transactions");
    }

    private void insertBlockchain(Block block) {
        Map<String, Object> map = new HashMap<>();
        map.put("previous", block.getHeader().getPreviousBlock());
        map.put("hash", block.getMetadata().getHash());
        template.update(ensureBlocks, map);
        template.update(insertBlockchain, map);

        map.put("size", block.getMetadata().getSize());
        map.put("version", block.getHeader().getVersion());
        map.put("merkle", block.getHeader().getMerkleRoot());
        map.put("timestamp", block.getHeader().getTimestamp());
        map.put("bits", block.getHeader().getBits());
        map.put("nonce", block.getHeader().getNonce());
        template.update(insertBlocksDetails, map);
    }

    private void ensureTransactionsAndTransactionReferences(Block block) {
        int max = 999;
        Map<String, Object> map = new HashMap<>();
        List<Object[]> rows = new ArrayList<>();
        map.put("tx_hashes", rows);
        for (Transaction t : block.getTransactions()) {
            rows.add(new Object[] {t.getMetadata().getHash()});
            if (rows.size() == max) {
                template.update(ensureTransactions, map);
                rows.clear();
            }
            for (Input input : t.getInputs()) {
                rows.add(new Object[] { input.getHash()});
                if (rows.size() == max) {
                    template.update(ensureTransactions, map);
                    rows.clear();
                }
            }
        }
        if (rows.size() > 0) {
            template.update(ensureTransactions, map);
            rows.clear();
        }
    }

    @Transactional
    public void addBlock(Block block) throws UnknownOpCodeException {
        insertBlockchain(block);
        ensureTransactionsAndTransactionReferences(block);
        //        Map<String, Object> map = new HashMap<>();
        //
        //
        //
        //        template.update(ensureParent, map);
        //        template.update(updateBlock, map);
        //        template.update(insertBlock, map);
        //        template.update(insertBlockchain, map);
        //
        //
        //        List<Object[]> txHashes = new ArrayList<>();
        //        map.put("tx_hashes", txHashes);
        //
        //        int i = 0;
        //        for (Transaction t : block.getTransactions()) {
        //            Object[] txHash = new Object[] { i++, t.getMetadata().getHash()};
        //            txHashes.add(txHash);
        //            if (i == block.getTransactions().size() || txHashes.size() == 100) {
        //                template.update(ensureTransactions, map);
        //                template.update(associateTransactions, map);
        //                txHashes.clear();
        //            }
        //        }
        //
        //        List<Object[]> txOuts = new ArrayList<>();
        //        map.put("txouts", txOuts);
        //        int tCount = 0;
        //        for (Transaction t : block.getTransactions()) {
        //            tCount++;
        //            i = 0;
        //            for (Output output : t.getOutputs()) {
        //                Object[] row = new Object[] { t.getMetadata().getHash(), i++, output.getValue() };
        //                txOuts.add(row);
        //                if ((tCount == block.getTransactions().size() && i == t.getOutputs().size())
        //                        || txOuts.size() == 100) {
        //                    template.update(insertCompleteTransactions, map);
        //                    txOuts.clear();
        //                }
        //            }
        //        }
        //
        //        List<Object[]> rows = new ArrayList<>();
        //        map.put("tx_hashes", rows);
        //        tCount = 0;
        //        for (Transaction t : block.getTransactions()) {
        //            ++tCount;
        //            i = 0;
        //            for (Input input : t.getInputs()) {
        //                i++;
        //                byte[] bs = input.getHash();
        //                rows.add(new Object[] { -1, bs });
        //                if ((tCount == block.getTransactions().size() && i == t.getInputs().size()) || rows.size() == 100) {
        //                    template.update(ensureTransactions, map);
        //                    rows.clear();
        //                }
        //            }
        //        }
        //
        //        tCount = 0;
        //        for (Transaction t : block.getTransactions()) {
        //            tCount++;
        //            i = 0;
        //            for (Input input : t.getInputs()) {
        //                i++;
        //                Object[] row = new Object[] {
        //                        input.getHash(), input.getIndex()
        //                };
        //                txOuts.add(row);
        //                if ((tCount == block.getTransactions().size() && i == t.getInputs().size())
        //                        || txOuts.size() == 100){
        //                    template.update(insertIncompleteTransactions, map);
        //                    txOuts.clear();
        //                }
        //            }
        //        }
        //
        //        List<Object[]> spends = new ArrayList<>();
        //        map.put("spends", spends);
        //        tCount = 0;
        //        for (Transaction t :block.getTransactions()) {
        //            i = 0;
        //            for (Input input : t.getInputs()) {
        //                Object[] spend = new Object[] {
        //                        t.getMetadata().getHash(), i++, input.getHash(), input.getIndex()
        //                };
        //                spends.add(spend);
        //                if ((tCount == block.getTransactions().size() && i == t.getInputs().size())
        //                        || spends.size() == 100) {
        //                    template.update(insertSpends, map);
        //                    spends.clear();
        //                }
        //            }
        //        }

    }

    public BlockSummary getBlockSummary(String block) {
        String sql = null;
        Map<String, Object> map = new HashMap<>();
        BlockSummary result = template.queryForObject(sql, map, new RowMapper<BlockSummary>() {

            @Override
            public BlockSummary mapRow(ResultSet rs, int arg1) throws SQLException {
                BlockSummary result = new BlockSummary();
                result.size = rs.getInt("size");
                return result;
            }

        });
        return result;
    }
}

