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


import java.util.List;
import java.util.Set;

/**
 * The UmlModel holds the logical entities of a model. It is the repository that diagrams retrieve
 * their data objects from. The UmlModel objects are shareable, so they are can be used from every
 * diagram.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface UmlModel {

    /**
     * Adds the specified element.
     *
     * @param anElement the element to add
     */
    void addElement(UmlModelElement anElement);

    /**
     * Adds the specified element in the specified diagram.
     *
     * @param anElement the element to add
     * @param diagram   the diagram where the element is added
     */
    void addElement(UmlModelElement anElement, UmlDiagram diagram);


    /**
     * Removes the specified element in the specified diagram.
     *
     * @param anElement the element to remove
     * @param diagram   the diagram where the element is removed
     */
    void removeElement(UmlModelElement anElement, UmlDiagram diagram);


    /**
     * Determines if the specified model element is in the model.
     *
     * @param anElement the model element to look up
     * @return true if the element exists, false otherwise
     */
    boolean contains(UmlModelElement anElement);

    /**
     * Returns all elements.
     *
     * @return the list of all elements
     */
    Set<UmlModelElement> getElements();

    /**
     * Adds the specified diagram.
     *
     * @param diagram the diagram to add
     */
    void addDiagram(UmlDiagram diagram);

    /**
     * Removes the specified diagram.
     *
     * @param diagram the diagram to remove
     */
    void removeDiagram(UmlDiagram diagram);

    /**
     * Returns all diagrams.
     *
     * @return the diagrams
     */
    List<UmlDiagram> getDiagrams();

    /**
     * Adds the specified model listener.
     *
     * @param l the model listener
     */
    void addModelListener(UmlModelListener l);

    /**
     * Removes the specified model listener.
     *
     * @param l the model listener
     */
    void removeModelListener(UmlModelListener l);

    /**
     * Return whether the element named name exist
     *
     * @param name
     * @return
     */
    boolean exist(String name);

    Set<? extends UmlModelElement> getAll(Class<? extends UmlModelElement> clazz);

    void reset();

    UmlModelElement getElement(String name, Class<? extends UmlModelElement> umlBoundaryClass);
}
