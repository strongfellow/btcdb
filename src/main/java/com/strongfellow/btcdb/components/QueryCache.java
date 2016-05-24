
package com.strongfellow.btcdb.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryCache {

    private static final Logger logger = LoggerFactory.getLogger(QueryCache.class);
    private final Map<String, String> cachedQueries = new HashMap<>();

    public void clear() {
        logger.info("clearing cache");
        this.cachedQueries.clear();
        logger.info("cleared cache");
    }

    public String loadQuery(String path) throws IOException {
        String result = cachedQueries.get(path);
        if (result == null) {
            String hot = IOUtils.toString(getClass().getResourceAsStream("queries/" + path + ".sql"), "ascii");
            cachedQueries.put(path, hot);
            return hot;
        } else {
            return result;
        }
    }

}
