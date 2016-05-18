package com.strongfellow.btcdb.components;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void processBlock(Block block) {
        String hash = Util.bigEndianHash(block.getMetadata().getHash());
        logger.info("begin processing block hash {}", hash);
        try {
            database.addBlock(block);
        } catch(UnknownOpCodeException | IOException e) {
            throw new RuntimeException();
        }
        logger.info("finished processing block hash {}", hash);
    }

    @Override
    public void processTransaction(Transaction tx) {
        String hash = Util.bigEndianHash(tx.getMetadata().getHash());
        logger.info("begin processing tx hash {}", hash);
        //        database.addTransaction(tx);
        logger.info("finished processing tx hash {}", hash);
    }

}
