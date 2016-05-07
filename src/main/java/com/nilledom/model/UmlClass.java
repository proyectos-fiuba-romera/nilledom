/**
 * Copyright 2007 Wei-ju Wu
 * <p/>
 * This file is part of TinyUML.
 * <p/>
 * TinyUML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.nilledom.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the UML model class for a Class.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class UmlClass extends PackageableUmlModelElement {

    private static UmlClass prototype;
    private boolean isAbstract;
    private List<UmlAttribute> attributes = new ArrayList<UmlAttribute>();
    private List<UmlMethod> methods = new ArrayList<UmlMethod>();
    private List<UmlStereotype> stereotypes = new ArrayList<UmlStereotype>();
    private String documentation ="";
    /**
     * Constructor.
     */
    protected UmlClass() {
    }

    /**
     * Returns the Prototype instance of the UmlClass.
     *
     * @return the Prototype instance
     */
    public static UmlClass getPrototype() {
        if (prototype == null)
            prototype = new UmlClass();
        return prototype;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.CLASS;
    }

    /**
     * {@inheritDoc}
     */
    @Override public Object clone() {
        UmlClass cloned = (UmlClass) super.clone();
        cloned.attributes = new ArrayList<UmlAttribute>();
        for (UmlAttribute attribute : attributes) {
            cloned.attributes.add(attribute);
        }
        cloned.methods = new ArrayList<UmlMethod>();
        for (UmlMethod method : methods) {
            cloned.methods.add(method);
        }
        cloned.stereotypes = new ArrayList<UmlStereotype>();
        for (UmlStereotype stereotype : stereotypes) {
            cloned.stereotypes.add(stereotype);
        }
        return cloned;
    }

    /**
     * Returns the isAbstract attribute.
     *
     * @return the isAbstract attribute
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Sets the isAbstract attribute.
     *
     * @param flag the isAbstract attribute
     */
    public void setAbstract(boolean flag) {
        isAbstract = flag;
        notifyElementChanged();
    }

    /**
     * Returns the methods.
     *
     * @return the methods
     */
    public List<UmlMethod> getMethods() {
        return methods;
    }

    /**
     * Sets the method list.
     *
     * @param methodList the method list
     */
    public void setMethods(List<UmlMethod> methodList) {
        methods = methodList;
        notifyElementChanged();
    }

    /**
     * Returns the attributes.
     *
     * @return the attributes
     */
    public List<UmlAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Sets the attribute list.
     *
     * @param attributeList the attribute list
     */
    public void setAttributes(List<UmlAttribute> attributeList) {
        attributes = attributeList;
        notifyElementChanged();
    }

    /**
     * Returns the stereotypes.
     *
     * @return the stereotypes
     */
    public List<UmlStereotype> getStereotypes() {
        return stereotypes;
    }

    /**
     * Sets the stereotypes.
     *
     * @param stereotypeList the stereotypes
     */
    public void setStereotypes(List<UmlStereotype> stereotypeList) {
        stereotypes = stereotypeList;
        notifyElementChanged();
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}
