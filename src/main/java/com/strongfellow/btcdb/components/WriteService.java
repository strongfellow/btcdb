package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.security.DigestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.strongfellow.btcdb.logic.Util;
import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;

@Component
public class WriteService implements BTCListener {

    private static final Logger logger = LoggerFactory.getLogger(WriteService.class);

    private StrongfellowDB database;

    @Autowired
    public void setDatabase(StrongfellowDB database) {
        this.database = database;
    }

    @Override
    public void processBlock(Block block) throws DigestException, DataAccessException, IOException {
        String hash = Util.bigEndianHash(block.getMetadata().getHash());
        logger.info("begin processing block hash {}", hash);

        byte[] h = block.getMetadata().getHash();
        byte[] parent = block.getHeader().getPreviousBlock();
        database.insertBlock(h, parent);
        database.updateDescendents(parent);
        database.updateTips(h);

        database.insertBlockDetails(block);
        database.ensureTransactionsAndTransactionReferences(block);
        database.ensureTransactionDetails(block.getTransactions());
        database.associateTransactionsWithBlock(block);
        database.ensureTxouts(block);
        database.ensureTxins(block);
        database.ensureSpends(block);
        database.ensureValues(block);
        database.insertCoinbase(block);
        database.addHash160s(block.getTransactions());
        database.addScripts(block.getTransactions());
        logger.info("finished processing block hash {}", hash);
    }

    @Override
    @Transactional
    public void processTransaction(Transaction tx) {
        String hash = Util.bigEndianHash(tx.getMetadata().getHash());
        logger.info("begin processing tx hash {}", hash);
        //        database.addTransaction(tx);
        throw new RuntimeException("not implemented");
        // logger.info("finished processing tx hash {}", hash);
    }

}
