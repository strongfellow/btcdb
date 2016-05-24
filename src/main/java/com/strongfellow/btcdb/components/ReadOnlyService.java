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
        return this.db.getBlockSummary(block);
    }

    public TransactionSummary getTransactionSummary(String hash) throws DataAccessException, DecoderException, IOException {
        return this.db.getTransactionSummmary(hash);
    }

}
