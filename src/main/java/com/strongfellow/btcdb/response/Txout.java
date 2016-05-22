package com.strongfellow.btcdb.response;

import java.security.DigestException;

import com.strongfellow.btcdb.logic.Hashes;

public class Txout {

    private String address;
    private Long value;
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
}
