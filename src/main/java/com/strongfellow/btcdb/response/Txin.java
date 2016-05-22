package com.strongfellow.btcdb.response;

import java.security.DigestException;

import com.strongfellow.btcdb.logic.Hashes;

public class Txin {

    private String address;
    private Long value;
    private String txout;
    private Integer index;

    public String getAddress() {
        return address;
    }

    public void setAddress(byte[] address) throws DigestException {
        if (address != null) {
            this.address = Hashes.publicKeyHashAddressToBase58(address);
        }
    }
    public Long getValue() {
        return value;
    }
    public void setValue(Long value) {
        this.value = value;
    }
    public String getTxout() {
        return txout;
    }
    public void setTxout(String txout) {

        this.txout = txout;
    }
    public Integer getIndex() {
        return index;
    }
    public void setIndex(Integer index) {
        this.index = index;
    }

}
