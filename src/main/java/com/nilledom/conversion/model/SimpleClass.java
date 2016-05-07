package com.nilledom.conversion.model;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleClass {
    private String name;
    private List<String> methods = new ArrayList<>();
    private Set<SimpleRelation> relations = new HashSet<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public Set<SimpleRelation> getRelations() {
        return relations;
    }

    public void setRelations(Set<SimpleRelation> relations) {
        this.relations = relations;
    }

    public void addRelation(SimpleRelation relation){
        relations.add(relation);
    }
    public void addMethod(String method){
        methods.add(method);
    }

    @Override
    public int hashCode(){
        return name.length();
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof SimpleClass))
            return false;
        return  ((SimpleClass) object).getName().equals(this.getName());
    }
}
