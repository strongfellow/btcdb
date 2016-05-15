package com.strongfellow.btcdb.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadOnlyService {

    @Autowired
    private StrongfellowDB db;

    public BlockSummary getBlockSummary(String block) {
        return this.db.getBlockSummary(block);
    }

}
