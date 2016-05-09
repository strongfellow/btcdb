package com.strongfellow.btcdb.protocol;

import java.util.Collections;
import java.util.List;

public class Block {

    private final Metadata metadata;
    private final BlockHeader header;
    private final List<Transaction> transactions;

    public Block(Metadata bm, BlockHeader bh, List<Transaction> txs) {
        this.metadata = bm;
        this.header = bh;
        this.transactions = Collections.unmodifiableList(txs);
    }
    
    public Metadata getMetadata() {
        return metadata;
    }

    public BlockHeader getHeader() {
        return header;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
