package com.nilledom.conversion.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nilledom.conversion.ClassModelBuilder;
import com.nilledom.conversion.model.*;
import com.nilledom.model.*;
import com.nilledom.util.Msg;
import com.nilledom.util.StringHelper;

public class ClassModelBuilderImpl implements ClassModelBuilder {


    @Override
    public ConversionModel buildConversionModel(Map<String, List<UmlUseCase>> mainEntityMap) {
        ConversionModel conversionModel = new ConversionModel();
        for(String mainEntity: mainEntityMap.keySet()){
            Control control = conversionModel.getControl(StringHelper.toUpperCamelCase(mainEntity) + Msg.get("conversion.names.control"));
            for(UmlUseCase useCase : mainEntityMap.get(mainEntity)){
                control.addMethod("+"+StringHelper.toLowerCamelCase(useCase.getName())+"()");
                for(String entityName : useCase.getAllEntities()){
                    Entity entity = conversionModel.getEntity(StringHelper.toUpperCamelCase(entityName));
                    conversionModel.addRelation(new SimpleRelation(control,entity));
                }
                for(UmlActor mainActor : useCase.getMainActors()){
                    Boundary boundary = conversionModel.getBoundary(StringHelper.toUpperCamelCase(mainActor.getName()) + Msg.get("conversion.names.boundary"));
                    boundary.addMethod("+"+StringHelper.toLowerCamelCase(useCase.getName())+"()");
                    conversionModel.addRelation(new SimpleRelation(boundary,control));
                }
            }
        }
        return conversionModel;
    }

    @Override
    public Map<Class<? extends UmlClass>, List<UmlClass>> buildUmlModel(ConversionModel model) {
        Map<Class<? extends UmlClass>, List<UmlClass>> umlModel = new HashMap<>();
        umlModel.put(UmlBoundary.class, new ArrayList<UmlClass>());
        umlModel.put(UmlEntity.class, new ArrayList<UmlClass>());
        umlModel.put(UmlControl.class, new ArrayList<UmlClass>());

        List<SimpleClass> simpleClasses = new ArrayList<>();
        simpleClasses.addAll(model.getBoundaries());
        simpleClasses.addAll(model.getControls());
        simpleClasses.addAll(model.getEntities());

        for (SimpleClass simpleClass : simpleClasses) {
            UmlClass umlClass = toUmlClass(simpleClass);
            umlClass.setName(simpleClass.getName());
            umlModel.get(umlClass.getClass()).add(umlClass);

            List<UmlMethod> umlMethods = new ArrayList<>();
            for (String method : simpleClass.getMethods()) {
                UmlMethod umlMethod = (UmlMethod) UmlMethod.getPrototype().clone();
                umlMethod.setName(method);
                umlMethods.add(umlMethod);
            }
            umlClass.setMethods(umlMethods);
        }
        return umlModel;
    }

    private UmlClass toUmlClass(SimpleClass simpleClass) {
        if(simpleClass instanceof Boundary)
            return (UmlClass) UmlBoundary.getPrototype().clone();

        if(simpleClass instanceof Control)
            return (UmlClass) UmlControl.getPrototype().clone();

        if(simpleClass instanceof Entity)
            return (UmlClass) UmlEntity.getPrototype().clone();

        return null;
    }
}
