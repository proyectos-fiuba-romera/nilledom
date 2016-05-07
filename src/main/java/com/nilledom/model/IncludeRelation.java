package com.nilledom.model;

public class IncludeRelation extends UmlRelation {

    public IncludeRelation(){
        super();
        this.setCanSetElement1Navigability(false);
        this.setCanSetElement2Navigability(false);
        this.setNavigableToElement1(false);
        this.setNavigableToElement2(true);

    }


    @Override
    public String getName(){
        return "Include("+super.getName()+")";
    }
}

