package com.strongfellow.btcdb.components;

import java.util.ArrayList;
import java.util.List;

public class BlockSummary {

    public Integer size;
    public Long timestamp;
    public Long bits;
    public Long version;
    public Long nonce;

    public Integer numTx;

    public String merkle;
    public String parent;
    public final List<String> children = new ArrayList<>();
    public Integer height;
}
