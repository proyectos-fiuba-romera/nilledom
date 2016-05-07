package com.nilledom.persistence.serializer;


import java.util.List;
import java.util.Map;

import com.nilledom.model.ElementType;
import com.nilledom.model.UmlDiagram;
import com.nilledom.persistence.ViewPersistence;
import com.nilledom.ui.ElementNameGenerator;
import com.nilledom.ui.model.Project;


public class ViewSerializer implements Serializer {

    private ObjectSerializer serializer;


    public ViewSerializer(ObjectSerializer serializer ) {
        this.serializer = serializer;

    }



    @Override public void write(Project project) throws Exception {


        List<UmlDiagram> umlDiagrams = project.getModel().getDiagrams();
        ViewPersistence viewPersistence = new ViewPersistence();
        viewPersistence.setUmlDiagrams(umlDiagrams);
        viewPersistence.setOpenedDiagrams(project.getOpenDiagrams());
        serializer.writeObject(viewPersistence);


    }

    @Override public Object read() throws Exception {
        ViewPersistence viewPersistence = (ViewPersistence)serializer.readObject();
        Map<ElementType, Integer> map = ElementNameGenerator.getNameMap();


        return viewPersistence;
    }


}
