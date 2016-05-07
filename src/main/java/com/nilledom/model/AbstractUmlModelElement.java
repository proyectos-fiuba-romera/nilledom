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

import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements a base UmlModelElement class.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class AbstractUmlModelElement extends DefaultNamedElement
    implements UmlModelElement {

    private Collection<UmlModelElementListener> modelListeners =
        new HashSet<UmlModelElementListener>();

    /**
     * {@inheritDoc}
     */
    public void addModelElementListener(UmlModelElementListener l) {
        modelListeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeModelElementListener(UmlModelElementListener l) {
        modelListeners.remove(l);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<UmlModelElementListener> getModelElementListeners() {
        return modelListeners;
    }

    /**
     * Notifies the listeners that this element has changed in some way.
     */
    protected void notifyElementChanged() {
        for (UmlModelElementListener l : modelListeners) {
            l.elementChanged(this);
        }
    }

    public abstract ElementType getElementType();

    /**
     * {@inheritDoc}
     */
    @Override public Object clone() {
        AbstractUmlModelElement element = (AbstractUmlModelElement) super.clone();
        element.modelListeners = new HashSet<UmlModelElementListener>();

        return element;
    }
    @Override
    public boolean canBeInsertedOnTree(){
        return false;
    }
}
