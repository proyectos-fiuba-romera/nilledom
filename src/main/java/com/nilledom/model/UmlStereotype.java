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

/**
 * This class represents an UML stereotype.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class UmlStereotype extends AbstractUmlModelElement {

    private static UmlStereotype prototype;

    /**
     * Private constructor.
     */
    protected UmlStereotype() {
    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static UmlStereotype getPrototype() {
        if (prototype == null)
            prototype = new UmlStereotype();
        return prototype;
    }

    @Override
    public ElementType getElementType() {
        return ElementType.STEREOTYPE;
    }

    @Override
    public void setName(String name){
        if(name.length() < 4 || !name.substring(0,2).equals("<<")||!name.substring(name.length()-2,name.length()).equals(">>"))
            super.setName( "<<" + name + ">>" );
        else
            super.setName(name);
    }


    @Override
    public boolean equals(Object o){
        if(o==null || !(o instanceof UmlStereotype))
            return false;
        return  ((UmlStereotype)o).getName().equals(getName());
    }
}
