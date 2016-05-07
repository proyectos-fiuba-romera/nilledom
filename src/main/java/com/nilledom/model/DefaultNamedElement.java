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

import java.util.Collection;
import java.util.HashSet;

import com.nilledom.exception.ElementNameAlreadyExist;

/**
 * This class provides a default implementation of the NamedElement interface that can be either
 * subclassed or embedded.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DefaultNamedElement implements NamedElement,Comparable<DefaultNamedElement> {

    private transient Collection<NameChangeListener> nameChangeListeners =
        new HashSet<NameChangeListener>();

    private String name= super.toString(); //Default name avoids null pointers

    /**
     * {@inheritDoc}
     */
    @Override public Object clone() {
        DefaultNamedElement cloned = null;
        try {
            cloned = (DefaultNamedElement) super.clone();
            cloned.nameChangeListeners = new HashSet<>();
        } catch (CloneNotSupportedException ignore) {
            ignore.printStackTrace();
        }
        return cloned;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String aName) {
        String oldName = name;
        name = aName;

        try {
            for (NameChangeListener l : nameChangeListeners) {
                l.nameChanged(this);
            }
        } catch (ElementNameAlreadyExist e) {
            name = oldName;
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName();
    }


    /**
     * Adds a label change listener that listens to changes to the name label.
     *
     * @param l the listener to add
     */
    public void addNameChangeListener(NameChangeListener l) {
        nameChangeListeners.add(l);
    }

    /**
     * Removes a label change listener from the name label.
     *
     * @param l the listener to remove
     */
    public void removeNameChangeListener(NameChangeListener l) {
        nameChangeListeners.remove(l);
    }

    @Override
    public int compareTo(DefaultNamedElement o) {
        return this.name.compareTo(o.name);
    }
}
