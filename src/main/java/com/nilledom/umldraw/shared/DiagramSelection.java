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
package com.nilledom.umldraw.shared;

import com.nilledom.draw.DiagramOperations;
import com.nilledom.draw.Node;
import com.nilledom.draw.NodeSelection;

/**
 * This class implements a DiagramSelection. Diagrams can be resized, but
 * not moved.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DiagramSelection extends NodeSelection {

    /**
     * Constructor.
     *
     * @param anEditor the editor
     * @param aNode    the node
     */
    public DiagramSelection(DiagramOperations anEditor, Node aNode) {
        super(anEditor, aNode);
    }

    /**
     * {@inheritDoc}
     */
    @Override protected void moveSelection(double diffx, double diffy) {
    }

    /**
     * {@inheritDoc}
     */
    @Override protected void resizeNw(double diffx, double diffy) {
    }

    /**
     * {@inheritDoc}
     */
    @Override protected void resizeNe(double diffx, double diffy) {
    }

    /**
     * {@inheritDoc}
     */
    @Override protected void resizeSw(double diffx, double diffy) {
    }
}
