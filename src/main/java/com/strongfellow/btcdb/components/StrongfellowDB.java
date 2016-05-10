package com.strongfellow.btcdb.components;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;

@Repository
public class StrongfellowDB {
	
	@Autowired
	NamedParameterJdbcTemplate template;
	
    public void addBlock(Block block) {
        String query = "INSERT OR IGNORE "
                + "INTO blocks(hash, block_size, version, previous, merkle, block_timestamp, bits, nonce, tx_count)"
                + "VALUES(:hash, :size, :version, :previous, :merkle, :timestamp, :bits, :nonce, :tx_count)";
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
        template.update(query, map);
    }

    public void addTransaction(Transaction tx) {
        
    }

}
