package com.strongfellow.btcdb.components.blockloader;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.strongfellow.btcdb.components.BTCListener;
import com.strongfellow.btcdb.logic.BlockReader;
import com.strongfellow.btcdb.protocol.Block;

@Component
public class BlockLoader {

    @Autowired
    private BTCListener listener;

    private Map<String, String> md5s = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(BlockLoader.class);
    private String blocksDirectory = null;

    @Value("${blocks.dir}")
    public void setBlocksDirectory(String s) {
        this.blocksDirectory = s;
    }

    @Async
    public void slurpBlocksInBackground() throws Exception {
        logger.info("begin slurping blocks");
        int backoffMinutes = 1;
        while (true) {
            int n = 0;
            logger.info("begin iteration");
            File dir = new File(this.blocksDirectory);
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith("blk") && name.endsWith(".dat");
                }
            });
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    String md5 = DigestUtils.md5Hex(new FileInputStream(file));
                    if (md5.equals(md5s.get(file.getName()))) {
                        logger.info("we've already read file {} with md5 {}", file, md5);
                    } else {
                        n += loadBlocks(file);
                        md5s.put(file.getName(), md5);
                    }
                }
            }
            logger.info("This iteration, we read {} blocks", n);
            if (n == 0) {
                backoffMinutes = 2 * backoffMinutes;
                if (backoffMinutes > 15) {
                    backoffMinutes = 15;
                }
            } else {
                backoffMinutes = 1;
            }
            // we sleep for at least 1 minute, at most 15
            logger.info("sleeping for {} minutes", backoffMinutes);
            Thread.sleep(backoffMinutes * 60 * 1000);
        }
    }

    private int loadBlocks(File file) throws Exception {
        logger.info("begin loading blocks from file {}", file.getName());
        try (InputStream in = new FileInputStream(file)){
            for (int count = 0; true; count++) {
                byte[] ignored = new byte[8];
                for (int i = 0; i < 8; ) {
                    int x = in.read(ignored, i, ignored.length - i);
                    if (x == -1) {
                        if (i == 0) {
                            logger.info(
                                    "finished; loaded {} blocks from file {}", count, file.getName());
                            return count;
                        } else {
                            throw new EOFException();
                        }
                    } else {
                        i += x;
                    }
                }
                Block block = new BlockReader(in).readBlock();
                listener.processBlock(block);
                logger.info(
                        "loaded {} blocks from file {}", count + 1, file.getName());

            }

        }

    }
}
