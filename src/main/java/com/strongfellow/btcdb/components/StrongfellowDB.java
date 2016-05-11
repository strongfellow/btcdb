package com.strongfellow.btcdb.components;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;

@Repository
public class StrongfellowDB {

    @Autowired
    NamedParameterJdbcTemplate template;

    public void addBlock(Block block) {

        List<String> fields = Arrays.asList(new String[] {
                "hash", "size", "version", "merkle", "timestamp", "bits", "nonce", "tx_count" });

        String insertParent = "INSERT OR IGNORE INTO blocks(`hash`) VALUES(:previous)";
        String updateBlock = "UPDATE `blocks` SET "
                + fields.stream().map(s -> "`" + s + "`=:" + s).collect(Collectors.joining(", "))
                +" WHERE `hash`=:hash";

        String insertBlock = "INSERT OR IGNORE "
                + "INTO blocks(`hash`, `size`, `version`, `merkle`, `timestamp`, `bits`, `nonce`, `tx_count`)"
                + "VALUES(:hash, :size, :version, :merkle, :timestamp, :bits, :nonce, :tx_count)";
        String insertBlockchain = "INSERT OR IGNORE INTO `block_chain`(`child_id`, `parent_id`) "
                + "SELECT `child`.`id`, `parent`.`id` "
                + "FROM `blocks` `child` JOIN `blocks` `parent` "
                + "WHERE `child`.`hash` = :hash AND `parent`.`hash` = :previous";



        Map<String, Object> map = new HashMap<>();
        map.put("hash", block.getMetadata().getHash());
        map.put("size", block.getMetadata().getHash());
        map.put("version", block.getHeader().getVersion());
        map.put("previous", block.getHeader().getPreviousBlock());
        map.put("merkle", block.getHeader().getMerkleRoot());
        map.put("timestamp", block.getHeader().getTimestamp());
        map.put("bits", block.getHeader().getBits());
        map.put("nonce", block.getHeader().getNonce());
        map.put("tx_count", block.getTransactions().size());

        template.update(insertParent, map);
        template.update(updateBlock, map);
        template.update(insertBlock, map);
        template.update(insertBlockchain, map);
    }

    public void addTransaction(Transaction tx) {

    }

}
