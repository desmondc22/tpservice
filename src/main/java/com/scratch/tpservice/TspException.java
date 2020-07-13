package com.scratch.tpservice;

public class TspException extends Exception{

    public TspException(ErrorCode errorCode) {
        super("TpsError: "+errorCode.name());
    }

}
