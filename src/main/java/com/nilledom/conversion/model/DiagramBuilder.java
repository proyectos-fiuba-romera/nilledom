package com.nilledom.conversion.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nilledom.model.UmlActor;
import com.nilledom.model.UmlPackage;
import com.nilledom.model.UmlUseCase;
import com.nilledom.util.Msg;
import com.nilledom.util.StringHelper;

public class DiagramBuilder {

    private String name;
    private boolean packageRelated=false;
    private UmlPackage umlPackage;
    private List<String> mainEntities=new ArrayList<>();

    public ConversionModel build(Map<String,List<UmlUseCase>> mainEntityMap){
        ConversionModel conversionDiagram = new ConversionModel();
        conversionDiagram.setName(name);

        for(String mainEntity: mainEntities){
            Control control = conversionDiagram.getControl(StringHelper.toUpperCamelCase(mainEntity) + Msg.get("conversion.names.control"));
            for(UmlUseCase useCase : mainEntityMap.get(mainEntity)){
                if(isPackageRelated() && (useCase.getPackage()==null || !useCase.getPackage().equals(umlPackage)))
                    continue;
                control.addMethod("+"+StringHelper.toLowerCamelCase(useCase.getName())+"():void");
                for(String entityName : useCase.getAllEntities()){
                    Entity entity = conversionDiagram.getEntity(StringHelper.toUpperCamelCase(entityName));
                    conversionDiagram.addRelation(new SimpleRelation(control,entity));
                }
                for(UmlActor mainActor : useCase.getMainActors()){
                    Boundary boundary = conversionDiagram.getBoundary(StringHelper.toUpperCamelCase(mainActor.getName()) + Msg.get("conversion.names.boundary"));
                    boundary.addMethod("+"+StringHelper.toLowerCamelCase(useCase.getName())+"():void");
                    conversionDiagram.addRelation(new SimpleRelation(boundary,control));
                }
                addControlControlRelatioin(conversionDiagram,control,mainEntity,useCase.getIncluded());
                addControlControlRelatioin(conversionDiagram,control,mainEntity,useCase.getExtended());
            }
        }
        return conversionDiagram;
    }

    private void addControlControlRelatioin(ConversionModel conversionDiagram, Control control, String mainEntity, List<UmlUseCase> useCases) {
        for(UmlUseCase relatedUseCase : useCases){
            if(isPackageRelated() && (relatedUseCase.getPackage()==null || !relatedUseCase.getPackage().equals(umlPackage)))
                continue;
            String relatedMainEntity=relatedUseCase.getMainEntity();
            if(relatedMainEntity.equals(mainEntity))
                continue;
            Control relatedControl = conversionDiagram.getControl(StringHelper.toUpperCamelCase(relatedMainEntity) + Msg.get("conversion.names.control"));
            conversionDiagram.addRelation(new SimpleRelation(control,relatedControl));
        }
    }

    public UmlPackage getUmlPackage() {
        return umlPackage;
    }

    public void setUmlPackage(UmlPackage umlPackage) {
        this.umlPackage = umlPackage;
    }

    public boolean isPackageRelated() {
        return packageRelated;
    }

    public void setPackageRelated(boolean packageRelated) {
        this.packageRelated = packageRelated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMainEntities() {
        return mainEntities;
    }

    public void setMainEntities(List<String> mainEntities) {
        this.mainEntities = mainEntities;
    }
}
