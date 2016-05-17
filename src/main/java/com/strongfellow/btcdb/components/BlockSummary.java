package com.strongfellow.btcdb.components;

import java.util.ArrayList;
import java.util.List;

public class BlockSummary {

    public int size;
    public long timestamp;
    public long bits;
    public long version;
    public long nonce;

    public int numTx;

    public String merkle;
    public final List<String> children = new ArrayList<>();
    public String parent;
}
