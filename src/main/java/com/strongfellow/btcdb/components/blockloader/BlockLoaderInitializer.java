package com.strongfellow.btcdb.components.blockloader;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlockLoaderInitializer {

    private static Logger logger = LoggerFactory.getLogger(BlockLoaderInitializer.class);

    @Autowired
    private BlockLoader blockLoader;

    @PostConstruct
    public void postConstruct() throws Exception {
        logger.info("begin postconstruct");
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    blockLoader.slurpBlocks();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        logger.info("end postconstruct");
    }

}
