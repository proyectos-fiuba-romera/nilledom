package com.nilledom.conversion.model;


import com.nilledom.umldraw.clazz.ClassElement;

public class SimpleRelation {

    public SimpleClass class1;
    public SimpleClass class2;


    public SimpleRelation(SimpleClass class1,SimpleClass class2){
        this.class1=class1;
        this.class2=class2;
    }
    public SimpleClass getClass1() {
        return class1;
    }

    public void setClass1(SimpleClass class1) {
        this.class1 = class1;
    }

    public SimpleClass getClass2() {
        return class2;
    }

    public void setClass2(SimpleClass class2) {
        this.class2 = class2;
    }

    @Override
    public int hashCode(){
        return class1.getName().length()+class2.getName().length();
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof SimpleRelation))
            return false;
        SimpleRelation rel = (SimpleRelation) object;
        return  (rel.class1.equals(class1) && rel.class2.equals(class2)) ||
                (rel.class1.equals(class2) && rel.class2.equals(class1));
    }

    public boolean isControlControl() {
            return (class1 instanceof Control) && (class2 instanceof Control);
    }
}
