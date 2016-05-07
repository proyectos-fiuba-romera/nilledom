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
package com.nilledom.ui.commands;

import javax.swing.undo.AbstractUndoableEdit;

import com.nilledom.model.UmlDiagram;
import com.nilledom.model.UmlModel;
import com.nilledom.util.Command;

/**
 * An undoable command that removes diagrams from a model.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DeleteDiagramCommand extends AbstractUndoableEdit implements Command {

    private UmlModel model;
    private UmlDiagram diagram;

    /**
     * Constructor.
     *
     * @param aModel   the model
     * @param aDiagram the diagram
     */
    public DeleteDiagramCommand(UmlModel aModel, UmlDiagram aDiagram) {
        model = aModel;
        diagram = aDiagram;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void undo() {
        super.undo();
        model.addDiagram(diagram);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void redo() {
        super.redo();
        run();
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        model.removeDiagram(diagram);
    }
}
