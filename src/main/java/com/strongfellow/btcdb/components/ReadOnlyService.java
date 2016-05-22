package com.strongfellow.btcdb.components;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadOnlyService {

    @Autowired
    private StrongfellowDB db;

    public BlockSummary getBlockSummary(String block) throws IOException, DecoderException {
        return this.db.getBlockSummary(block);
    }

    public TransactionSummary getTransactionSummary(String hash) {
        // TODO Auto-generated method stub
        return null;
    }

}
