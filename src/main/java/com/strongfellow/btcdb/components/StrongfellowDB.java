package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Output;
import com.strongfellow.btcdb.protocol.Transaction;
import com.strongfellow.btcdb.script.ParsedScript;
import com.strongfellow.btcdb.script.UnknownOpCodeException;

@Repository
public class StrongfellowDB {

    @Autowired
    NamedParameterJdbcTemplate template;

    private final String insertParent;
    private final String updateBlock;
    private final String insertBlock;
    private final String insertBlockchain;

    private String loadQuery(String path) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("queries/" + path + ".sql"), "ascii");
    }

    public StrongfellowDB() throws IOException {
        insertParent = loadQuery("insert_previous_block");
        updateBlock = loadQuery("update_block");
        insertBlock = loadQuery("insert_block");
        insertBlockchain = loadQuery("insert_blockchain");
    }

    public void addBlock(Block block) throws UnknownOpCodeException {

        Map<String, Object> map = new HashMap<>();
        map.put("hash", block.getMetadata().getHash());
        map.put("size", block.getMetadata().getHash());
        map.put("version", block.getHeader().getVersion());
        map.put("previous", block.getHeader().getPreviousBlock());
        map.put("merkle", block.getHeader().getMerkleRoot());
        map.put("timestamp", block.getHeader().getTimestamp());
        map.put("bits", block.getHeader().getBits());
        map.put("nonce", block.getHeader().getNonce());

        template.update(insertParent, map);
        template.update(updateBlock, map);
        template.update(insertBlock, map);
        template.update(insertBlockchain, map);

        for (Transaction t : block.getTransactions()) {
            for (Output txout : t.getOutputs()) {
                byte[] bs = txout.getScript();
                ParsedScript ps = ParsedScript.from(bs);
                System.out.println(ps);
            }
        }
    }

    public void addTransaction(Transaction tx) {

    }

}
