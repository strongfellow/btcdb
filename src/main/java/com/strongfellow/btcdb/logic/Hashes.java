package com.strongfellow.btcdb.logic;

import java.math.BigInteger;
import java.security.DigestException;
import java.security.MessageDigest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

public class Hashes {

    public static byte[] hash160(byte[] bytes) {
        return hash160(bytes, 0, bytes.length);
    }
    public static byte[] hash160(byte[] bytes, int offset, int length) {
        MessageDigest md = DigestUtils.getSha256Digest();
        md.update(bytes, offset, length);
        byte[] sha256 = md.digest();
        RIPEMD160Digest ripemd = new RIPEMD160Digest();
        ripemd.update(sha256, 0, sha256.length);
        byte[] hash = new byte[20];
        ripemd.doFinal(hash, 0);
        return hash;
    }

    private static String symbols = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static BigInteger n58 = new BigInteger("58", 10);

    /**
     *
     * @param addressHash must be the result of rimemd(sha256(public_key))
     * @return
     * @throws DigestException
     */
    public static String publicKeyHashAddressToBase58(byte[] addressHash) throws DigestException {
        if (addressHash.length != 20) {
            throw new RuntimeException("we expect this to be an address that has already been hashed"
                    + "via ripemd(sha256(address))");
        }
        byte[] payload = new byte[addressHash.length + 1];
        for (int i = 0; i < addressHash.length; i++) {
            payload[i + 1] = addressHash[i];
        }
        return base58Check(payload);
    }

    public static String base58Check(byte[] payload) throws DigestException {
        byte[] result = new byte[payload.length + 4];
        for (int i = 0; i < payload.length; i++) {
            result[i] = payload[i];
        }
        byte[] tmp = new byte[32];
        MessageDigest digest = DigestUtils.getSha256Digest();
        digest.update(payload);
        digest.digest(tmp, 0, 32);
        digest.update(tmp);
        digest.digest(tmp, 0, 32);
        for (int i = 0; i < 4; i++) {
            result[payload.length + i] = tmp[i];
        }
        return base58(result);
    }

    public static String base58(byte[] payload) {
        StringBuilder result = new StringBuilder();
        BigInteger n = new BigInteger(1, payload);
        while (n.signum() != 0) {
            BigInteger[] bs = n.divideAndRemainder(n58);
            result.append(symbols.charAt(bs[1].intValue()));
            n = bs[0];
        }
        for (int i = 0; i < payload.length; i++) {
            if (payload[i] == 0) {
                result.append('1');
            }
        }
        return result.reverse().toString();
    }

    public static byte[] fromBigEndian(String hex) throws DecoderException {
        byte[] result = Hex.decodeHex(hex.toCharArray());
        ArrayUtils.reverse(result);
        return result;
    }


    public static String toBigEndian(byte[] hash) {
        byte[] tmp = new byte[hash.length];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = hash[hash.length - (i + 1)];
        }
        return Hex.encodeHexString(tmp);
    }

}
