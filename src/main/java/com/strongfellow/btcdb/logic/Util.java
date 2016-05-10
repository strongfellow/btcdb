package com.strongfellow.btcdb.logic;

import org.apache.commons.codec.binary.Hex;

public class Util {
    
    public static String bigEndianHash(byte[] bs) {
        byte[] sb = new byte[bs.length];
        for (int i = 0; i < bs.length; i++) {
            sb[i] = bs[bs.length - (i + 1)];
        }
        return Hex.encodeHexString(sb);
    }
}
