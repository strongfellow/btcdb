package com.strongfellow.btcdb.response;

public class Spend {

    private String tx;
    private Integer index;
    public String getTx() {
        return tx;
    }
    public void setTx(String tx) {
        this.tx = tx;
    }
    public Integer getIndex() {
        return index;
    }
    public void setIndex(Integer index) {
        this.index = index;
    }
}
