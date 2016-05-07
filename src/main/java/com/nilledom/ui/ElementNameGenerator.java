package com.nilledom.ui;


import java.util.HashMap;
import java.util.Map;

import com.nilledom.model.ElementType;
import com.nilledom.model.UmlModel;

public class ElementNameGenerator {



    private static transient UmlModel umlModel;
    private static Map<ElementType, Integer> nameMap = new HashMap<ElementType, Integer>();

    public static String getName(ElementType elementType) {

        if (!nameMap.containsKey(elementType)) {
            nameMap.put(elementType, 0);
        }

        String label;

        do {
            Integer index = nameMap.get(elementType);
            index = index + 1;
            nameMap.put(elementType, index);
            String name = elementType.getName();
            label = name + " " + index;
        } while (umlModel.exist(label));

        return label;

    }

    public static void setModel(UmlModel model) {
        umlModel = model;
    }

    public static Map<ElementType, Integer> getNameMap() {
        return nameMap;
    }

    public static void reset(){
        umlModel=null;
        nameMap = new HashMap<ElementType, Integer>();
    }
}
