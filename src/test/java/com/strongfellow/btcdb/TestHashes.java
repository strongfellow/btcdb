package com.strongfellow.btcdb;

import java.security.DigestException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.strongfellow.btcdb.logic.Hashes;

@Test
public class TestHashes {

    public static void testBase58CheckExample() throws DecoderException, DigestException {
        String key = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6";
        byte[] bytes = Hex.decodeHex(key.toCharArray());
        byte[] hash160 = Hashes.hash160(bytes);

        System.out.println(Hex.encodeHex(hash160));

        String base58Check = Hashes.publicKeyHashAddressToBase58(hash160);
        System.out.println(base58Check);
        Assert.assertEquals(base58Check, "16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM");
    }

}
