package com.nilledom.exception;

public class ObjectSerializerException extends Exception {
    public ObjectSerializerException(String msg) {
        super(msg);
    }

    public ObjectSerializerException(String msg, Throwable e) {
        super(msg, e);
    }
}
