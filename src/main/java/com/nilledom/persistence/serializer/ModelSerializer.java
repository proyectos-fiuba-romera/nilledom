package com.nilledom.persistence.serializer;

import java.beans.XMLDecoder;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;





import com.nilledom.model.*;
import com.nilledom.persistence.ModelPersistence;
import com.nilledom.persistence.xml.XmlObjectSerializer;
import com.nilledom.ui.model.Project;

public class ModelSerializer implements Serializer {

    private ObjectSerializer serializer;

    public ModelSerializer(ObjectSerializer serializer) {
        this.serializer=serializer;
    }



    @Override public void write(Project project) throws Exception {


        Set<UmlModelElement> elements = project.getModel().getElements();
        ModelPersistence modelPersistence = new ModelPersistence();

        modelPersistence.setElements(new HashSet<>(elements));


        serializer.writeObject(modelPersistence);


    }


    @Override public Object read() throws Exception {
        ModelPersistence modelSerializer = (ModelPersistence) serializer.readObject();
        return modelSerializer;
    }


}
