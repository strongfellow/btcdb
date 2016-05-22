package com.strongfellow.btcdb.response;

import java.security.DigestException;
import java.util.ArrayList;
import java.util.List;

import com.strongfellow.btcdb.logic.Hashes;

public class Txout {

    private String address;
    private Long value;
    private final List<Spend> spends = new ArrayList<>();

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
    public List<Spend> getSpends() {
        return spends;
    }
    public void addSpend(Spend spend) {
        this.spends.add(spend);
    }
}
