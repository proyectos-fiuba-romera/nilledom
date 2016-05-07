/**
 * Copyright 2007 Wei-ju Wu
 * <p/>
 * This file is part of TinyUML.
 * <p/>
 * TinyUML is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * <p/>
 * TinyUML is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with TinyUML; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */
package com.nilledom.model;

/**
 * An element with a name. This is the root of UML model elements.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface NamedElement extends  Cloneable {

    /**
     * Returns the name attribute.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets the name attribute.
     *
     * @param aName the name
     */
    void setName(String aName);


    /**
     * Adds a label change listener that listens to changes to the name label.
     *
     * @param l the listener to add
     */
    public void addNameChangeListener(NameChangeListener l);

    /**
     * Removes a label change listener from the name label.
     *
     * @param l the listener to remove
     */
    public void removeNameChangeListener(NameChangeListener l);
}
