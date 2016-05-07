package com.nilledom.persistence.xml;

import com.nilledom.exception.ObjectSerializerException;
import com.nilledom.persistence.serializer.ObjectSerializer;
import com.nilledom.persistence.serializer.ProjectSerializer;

/**
 * Created by ferro on 5/9/2015.
 */
public class XmlProjectSerializer extends ProjectSerializer {


    public XmlProjectSerializer(String path) throws ObjectSerializerException {
        super(path);
    }

    @Override
    protected ObjectSerializer buildObjectSerializer(String path ) throws ObjectSerializerException {
        return new XmlObjectSerializer(path);
    }



}
