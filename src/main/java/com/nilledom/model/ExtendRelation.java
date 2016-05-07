package com.nilledom.model;


public class ExtendRelation extends UmlRelation {

    private UmlStep extentionPoint=UmlStep.UNDEFINED;
    private String condition="";

    public ExtendRelation(){
        super();
        this.setCanSetElement1Navigability(false);
        this.setCanSetElement2Navigability(false);
        this.setNavigableToElement1(false);
        this.setNavigableToElement2(true);

    }


    @Override
    public String getName(){
        return "Extend("+super.getName()+")";
    }

    
    public UmlStep getExtentionPoint() {
        return extentionPoint;
    }

    public void setExtentionPoint(UmlStep extentionPoint) {
        this.extentionPoint = extentionPoint;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
