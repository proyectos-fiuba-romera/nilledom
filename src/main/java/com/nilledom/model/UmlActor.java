package com.nilledom.model;

import java.util.HashSet;
import java.util.Set;

import com.nilledom.ui.AppFrame;

/**
 * This class represents an UML Actor
 *
 * @author Juan Manuel Romera
 */
public class UmlActor extends PackageableUmlModelElement{

    private static UmlActor prototype;
    private String description;
    private InheritanceRelation parentRel;
    private Set<InheritanceRelation> children = new HashSet<>();

    /**
     * Constructor.
     */
    public UmlActor() {

    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static UmlActor getPrototype() {
        if (prototype == null)
            prototype = new UmlActor();
        return prototype;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addParent(InheritanceRelation rel){
        UmlActor relParent= (UmlActor) rel.getParent();


        if(this.equals(relParent) || (this.parentRel !=null && this.parentRel.equals(rel)))
            return;

        if(this.parentRel !=null){
            UmlActor parent = (UmlActor)this.parentRel.getParent();
            parent.removeChild(this.parentRel);
        }
        this.parentRel = rel;
        relParent.addChild(rel);

    }
    public void removeParent(){
        if(this.parentRel ==null)
            return;
        UmlActor parent = (UmlActor)this.parentRel.getParent();
        parent.removeChild(this.parentRel);
        this.parentRel =null;
    }
    private void addChild(InheritanceRelation rel){
        this.children.add(rel);

    }

    private void removeChild(InheritanceRelation rel){
       this.children.remove(rel);
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof UmlActor && ((UmlActor) obj).getName().equals(this.getName()))
            return true;
        return false;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.ACTOR;
    }

    public UmlActor getParent() {
        if(parentRel==null)
            return null;
        return (UmlActor) parentRel.getParent();
    }

    public Set<UmlActor> getChildren() {
        Set<UmlActor>  actorChildren = new HashSet<>();
        for(InheritanceRelation rel: children)
            actorChildren.add((UmlActor) rel.getChild());

        return actorChildren;
    }
}
