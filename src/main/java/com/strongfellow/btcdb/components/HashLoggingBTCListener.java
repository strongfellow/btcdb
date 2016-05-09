
package com.strongfellow.btcdb.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;

@Component
public class HashLoggingBTCListener implements BTCListener {

    private static final Logger logger = LoggerFactory.getLogger(HashLoggingBTCListener.class);
    
    @Override
    public void processBlock(Block block) {
        logger.info("block hash {}", block.getMetadata().getHash());
    }

    @Override
    public void processTransaction(Transaction tx) {
        logger.info("tx hash {}", tx.getMetadata().getHash());
    }

}
