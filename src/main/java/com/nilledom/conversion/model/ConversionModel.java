package com.nilledom.conversion.model;

import java.util.HashSet;
import java.util.Set;

public class ConversionModel {

    private Set<Boundary> boundaries = new HashSet<>();
    private Set<Control> controls = new HashSet<>();
    private Set<Entity> entities= new HashSet<>();
    private Set<SimpleRelation> relations = new HashSet<>();
    private String name;

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public Set<Boundary> getBoundaries() {
        return boundaries;
    }

    
    public Set<Control> getControls() {
        return controls;
    }

    
    public Set<Entity> getEntities() {
        return entities;
    }

    
    public Set<SimpleRelation> getRelations() {
        return relations;
    }
    
    
    public void addBoundary(Boundary boundary){
        boundaries.add(boundary);
    }
    
    
    public void addControl(Control control){
        controls.add(control);
    }
    
    
    public void addEntity(Entity entity){
        entities.add(entity);
    }
    
    
    public void addRelation(SimpleRelation relation){
        relations.add(relation);
        relation.getClass1().addRelation(relation);
        relation.getClass2().addRelation(relation);
    }


    public Boundary getBoundary(String name) {
        Boundary boundary = new Boundary();
        boundary.setName(name);
        for(Boundary b : boundaries)
            if(b.equals(boundary))
                return b;
        addBoundary(boundary);
        return boundary;
    }
    public Entity getEntity(String name) {
        Entity entity = new Entity();
        entity.setName(name);
        for(Entity e : entities)
            if(e.equals(entity))
                return e;
        addEntity(entity);
        return entity;
    }
    public Control getControl(String name) {
        Control control = new Control();
        control.setName(name);
        for(Control c : controls)
            if(c.equals(control))
                return c;
        addControl(control);
        return control;
    }
}
