package com.nilledom.model;

import java.util.ArrayList;
import java.util.List;

import com.nilledom.exception.ElementNameAlreadyExist;

public abstract class UmlStereotypedClass extends UmlClass implements NameChangeListener{



    public UmlStereotypedClass(){
        UmlStereotype stereotype = new UmlStereotype();
        stereotype.setName(getStereotype());
        stereotype.addNameChangeListener(this);
        ArrayList<UmlStereotype> list = new ArrayList<>();
        list.add(stereotype);
        super.setStereotypes(list);
    }

    public abstract String getStereotype();



    public void nameChanged(NamedElement element) throws ElementNameAlreadyExist{
        if(!element.getName().equals(getStereotype()))
            element.setName(getStereotype());
    }


    @Override
    public void setStereotypes(List<UmlStereotype> stereotypes){
        UmlStereotype myStereotype = new UmlStereotype();
        myStereotype.setName(getStereotype());
        if(!stereotypes.contains(myStereotype)){
            myStereotype.addNameChangeListener(this);
            stereotypes.add(0,myStereotype);
        }else{
            stereotypes.get(stereotypes.indexOf(myStereotype)).addNameChangeListener(this);
        }
        super.setStereotypes(stereotypes);
    }

}
