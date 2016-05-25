package com.strongfellow.btcdb.response;

import com.strongfellow.btcdb.logic.Hashes;

public class BlockPointer {

    private String hash;
    private Integer height;
    public String getHash() {
        return hash;
    }
    public void setHash(byte[] hash) {
        this.hash = Hashes.toBigEndian(hash);
    }
    public Integer getHeight() {
        return height;
    }
    public void setHeight(Integer height) {
        this.height = height;
    }


}
