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
        blockLoader.slurpBlocksInBackground();
        logger.info("end postconstruct");
    }

}
