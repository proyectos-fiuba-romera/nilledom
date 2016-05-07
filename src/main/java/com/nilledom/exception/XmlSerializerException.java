package com.nilledom.exception;


public class XmlSerializerException extends Exception {
    public XmlSerializerException(String msg) {
        super(msg);
    }

    public XmlSerializerException(String msg, Throwable e) {
        super(msg, e);
    }
}
