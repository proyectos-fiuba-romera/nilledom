package com.nilledom.exception;

public class XmlPersisterException extends ProjectSerializerException {
    public XmlPersisterException(String msg) {
        super(msg);
    }

    public XmlPersisterException(String msg, Throwable e) {
        super(msg, e);
    }
}
