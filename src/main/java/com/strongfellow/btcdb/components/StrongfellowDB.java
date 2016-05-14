package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;
import com.strongfellow.btcdb.script.UnknownOpCodeException;

@Repository
public class StrongfellowDB {

    @Autowired
    NamedParameterJdbcTemplate template;

    private final String ensureParent;
    private final String updateBlock;
    private final String insertBlock;
    private final String insertBlockchain;
    private final String ensureTransactions;
    private final String associateTransactions;

    private String loadQuery(String path) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("queries/" + path + ".sql"), "ascii");
    }

    public StrongfellowDB() throws IOException {
        ensureParent = loadQuery("block/00100_ensure_parent");
        updateBlock = loadQuery("block/00200_update_block");
        insertBlock = loadQuery("block/00300_insert_block");
        insertBlockchain = loadQuery("block/00400_insert_blockchain");
        ensureTransactions = loadQuery("block/00500_ensure_transactions");
        associateTransactions = loadQuery("block/00600_associate_transactions_with_block");

    }

    public void addBlock(Block block) throws UnknownOpCodeException {

        Map<String, Object> map = new HashMap<>();


        map.put("hash", block.getMetadata().getHash());
        map.put("size", block.getMetadata().getSize());
        map.put("version", block.getHeader().getVersion());
        map.put("previous", block.getHeader().getPreviousBlock());
        map.put("merkle", block.getHeader().getMerkleRoot());
        map.put("timestamp", block.getHeader().getTimestamp());
        map.put("bits", block.getHeader().getBits());
        map.put("nonce", block.getHeader().getNonce());

        template.update(ensureParent, map);
        template.update(updateBlock, map);
        template.update(insertBlock, map);
        template.update(insertBlockchain, map);


        List<Object[]> txHashes = new ArrayList<>();
        map.put("tx_hashes", txHashes);

        int i = 0;
        for (Transaction t : block.getTransactions()) {
            Object[] txHash = new Object[] { i++, t.getMetadata().getHash()};
            txHashes.add(txHash);
            if (i == block.getTransactions().size() || txHashes.size() == 100) {
                template.update(ensureTransactions, map);
                template.update(associateTransactions, map);
                txHashes.clear();
            }
        }


    }


}
