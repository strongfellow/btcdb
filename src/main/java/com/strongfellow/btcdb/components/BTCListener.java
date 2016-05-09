package com.strongfellow.btcdb.components;

import java.io.InputStream;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;

public interface BTCListener {

    void processBlock(Block block);
    void processTransaction(Transaction tx);
}
