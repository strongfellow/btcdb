package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.security.DigestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.strongfellow.btcdb.logic.Util;
import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;
import com.strongfellow.btcdb.script.UnknownOpCodeException;

@Component
public class DatabaseBTCListener implements BTCListener {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBTCListener.class);

    @Autowired
    private StrongfellowDB database;

    @Override
    @Transactional
    public void processBlock(Block block) throws DigestException, UnknownOpCodeException {
        String hash = Util.bigEndianHash(block.getMetadata().getHash());
        logger.info("begin processing block hash {}", hash);
        try {
            database.insertBlock(block);
            database.insertBlockchain(block);
            database.insertBlockDetails(block);
            database.ensureTransactionsAndTransactionReferences(block);
            database.ensureTransactionDetails(block.getTransactions());
            database.associateTransactionsWithBlock(block);
            database.ensureTxouts(block);
            database.ensureTxins(block);
            database.ensureSpends(block);
            database.ensureValues(block);
            database.updateDescendents(block.getHeader().getPreviousBlock());
            database.insertCoinbase(block);
            database.addHash160s(block.getTransactions());
            database.addScripts(block.getTransactions());
        } catch(UnknownOpCodeException | IOException e) {
            throw new RuntimeException();
        }
        logger.info("finished processing block hash {}", hash);
    }

    @Override
    @Transactional
    public void processTransaction(Transaction tx) {
        String hash = Util.bigEndianHash(tx.getMetadata().getHash());
        logger.info("begin processing tx hash {}", hash);
        //        database.addTransaction(tx);
        logger.info("finished processing tx hash {}", hash);
    }

}
