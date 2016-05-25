package com.strongfellow.btcdb.components;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.strongfellow.btcdb.response.BlockSummary;
import com.strongfellow.btcdb.response.TransactionSummary;

@Component
public class ReadOnlyService {

    @Autowired
    private ReadOnlyRepository db;

    @Transactional(readOnly=true)
    public BlockSummary getBlockSummary(String block) throws IOException, DecoderException {
        return this.db.getBlockSummary(block);
    }

    @Transactional(readOnly=true)
    public TransactionSummary getTransactionSummary(String hash) throws DataAccessException, DecoderException, IOException {
        TransactionSummary ts = new TransactionSummary();
        ts.setHash(hash);
        db.getTransactionSummmary(ts);
        db.addOutputs(ts);
        db.addInputs(ts);
        db.addOutputAddresses(ts);
        db.addInputAddresses(ts);
        return ts;
    }

}
