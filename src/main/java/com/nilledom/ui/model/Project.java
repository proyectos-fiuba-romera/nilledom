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
package com.nilledom.ui.model;

import java.util.LinkedList;
import java.util.List;

import com.nilledom.model.UmlDiagram;
import com.nilledom.model.UmlModel;

/**
 * A serializable project class to store model information and open diagrams
 * in order to persist and restore a configuration.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class Project {

    private UmlModel model;
    private List<UmlDiagram> openDiagrams;

    /**
     * Constructor.
     *
     * @param aModel the UmlModel object
     */
    public Project(UmlModel aModel) {
        model = aModel;
        openDiagrams = new LinkedList<UmlDiagram>();
    }

    /**
     * Returns the model.
     *
     * @return the model
     */
    public UmlModel getModel() {
        return model;
    }

    /**
     * Returns the open diagrams.
     *
     * @return the open diagrams
     */
    public List<UmlDiagram> getOpenDiagrams() {
        return openDiagrams;
    }

    /**
     * Adds an open diagram.
     *
     * @param diagram the diagram to add
     */
    public void addOpenDiagram(UmlDiagram diagram) {
        openDiagrams.add(diagram);
    }

    public void setOpenDiagrams(List<UmlDiagram> openDiagrams) {
        this.openDiagrams = openDiagrams;
    }
}
