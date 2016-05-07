package com.nilledom.model;

public class NestRelation extends UmlRelation{

    public NestRelation(){
        super();
        this.setCanSetElement1Navigability(false);
        this.setCanSetElement2Navigability(false);
        this.setNavigableToElement1(false);
        this.setNavigableToElement2(false);

    }

    @Override
    public String getName(){
        return "Nest("+super.getName()+")";
    }


    public UmlModelElement getNesting(){return getElement1();}
    public UmlModelElement getNested(){return getElement2();}



}
