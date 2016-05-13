package com.strongfellow.btcdb.script;

public class UnknownOpCodeException extends Exception {

    public UnknownOpCodeException(int index, Integer b) {
        super("unrecognized opCode " + b + " at index " + index);
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

}
