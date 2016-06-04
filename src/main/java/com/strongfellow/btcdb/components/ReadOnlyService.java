package com.strongfellow.btcdb.components;

import java.io.IOException;

import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.strongfellow.btcdb.response.BlockSummary;
import com.strongfellow.btcdb.response.TransactionSummary;

@Component
public class ReadOnlyService {

    @Autowired
    private ReadOnlyRepository db;

    public BlockSummary getBlockSummary(String block) throws IOException, DecoderException {
        BlockSummary bs = new BlockSummary();
        bs.setHash(block);
        db.setBlockSummaryDetails(bs);
        db.addTransactions(bs);
        db.addOutputs(bs);
        db.addInputs(bs);
        return bs;
    }

    public TransactionSummary getTransactionSummary(String hash) throws DataAccessException, DecoderException, IOException {
        TransactionSummary ts = new TransactionSummary();
        ts.setHash(hash);
        db.getTransactionSummmary(ts);
        db.addOutputs(ts);
        db.addInputs(ts);
        db.addOutputAddresses(ts);
        db.addInputAddresses(ts);
        db.addBlocks(ts);
        return ts;
    }

}
