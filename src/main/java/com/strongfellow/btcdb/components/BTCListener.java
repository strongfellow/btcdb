package com.strongfellow.btcdb.components;

import com.strongfellow.btcdb.protocol.Block;
import com.strongfellow.btcdb.protocol.Transaction;

public interface BTCListener {

    void processBlock(Block block) throws Exception;
    void processTransaction(Transaction tx) throws Exception;
    void processHeader(Block block) throws Exception;
}
