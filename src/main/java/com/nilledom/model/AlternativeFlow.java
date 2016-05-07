package com.nilledom.model;


import java.util.ArrayList;

import com.nilledom.util.Msg;

public class AlternativeFlow extends Flow {

    private UmlStep entryStep=UmlStep.ANY;
    private String enrtyCondition="";
    private UmlStep returnStep=UmlStep.ANY;
    private String name= Msg.get("alternativeFlow.defaultName");



    @Override
    public String toString(){
        return getName();
    }


    public UmlStep getEntryStep() {
        return entryStep;
    }

    public void setEntryStep(UmlStep entryStep) {
        this.entryStep = entryStep;
    }

    public String getEnrtyCondition() {
        return enrtyCondition;
    }

    public void setEnrtyCondition(String enrtyCondition) {
        this.enrtyCondition = enrtyCondition;
    }

    public UmlStep getReturnStep() {
        return returnStep;
    }

    public void setReturnStep(UmlStep returnStep) {
        this.returnStep = returnStep;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object clone() {
        AlternativeFlow cloned = new AlternativeFlow();
        cloned.root = root.clone();
        if(entryStep!=null)
            cloned.entryStep= entryStep.clone();
        cloned.name = name;
        cloned.enrtyCondition = enrtyCondition;
        if(returnStep!=null)
            cloned.returnStep = returnStep.clone();
        return cloned;

    }
}
