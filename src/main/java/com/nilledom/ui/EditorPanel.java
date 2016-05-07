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
package com.nilledom.ui;

import javax.swing.*;

import com.nilledom.ui.diagram.DiagramEditor;
import com.nilledom.ui.diagram.EditorMouseEvent;
import com.nilledom.ui.diagram.EditorStateListener;
import com.nilledom.ui.diagram.SelectionListener;
import com.nilledom.util.AppCommandListener;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A generic editor panel which groups a DiagramEditor and a ToolbarManager.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class EditorPanel extends JPanel implements EditorStateListener {

    private transient ToolbarManager toolbarManager;
    private DiagramEditor diagramEditor;

    /**
     * Constructor.
     *
     * @param aDiagramEditor  the wrapped diagram editor
     * @param aToolbarManager a toolbar manager that creates the tool bar
     */
    public EditorPanel(DiagramEditor aDiagramEditor, ToolbarManager aToolbarManager) {
        setLayout(new BorderLayout());
        diagramEditor = aDiagramEditor;
        diagramEditor.addEditorStateListener(this);
        toolbarManager = aToolbarManager;
        JToolBar toolbar = toolbarManager.getToolbar();
        JScrollPane spane = new JScrollPane(diagramEditor);
        spane.getVerticalScrollBar().setUnitIncrement(10);
        spane.getHorizontalScrollBar().setUnitIncrement(10);
        add(spane, BorderLayout.CENTER);
        add(toolbar, BorderLayout.NORTH);
    }


    /**
     * Returns the wrapped DiagramEditor instance.
     *
     * @return the DiagramEditor instance
     */
    public DiagramEditor getDiagramEditor() {
        return diagramEditor;
    }

    /**
     * Adds an editor state listener.
     *
     * @param l the editor state listener
     */
    public void addEditorStateListener(EditorStateListener l) {
        diagramEditor.addEditorStateListener(l);
    }

    /**
     * Adds an AppCommandListener.
     *
     * @param l the AppCommandListener
     */
    public void addAppCommandListener(AppCommandListener l) {
        toolbarManager.addCommandListener(l);
        diagramEditor.addAppCommandListener(l);
    }

    /**
     * Adds a SelectionListener.
     *
     * @param l the SelectionListener
     */
    public void addSelectionListener(SelectionListener l) {
        diagramEditor.addSelectionListener(l);
    }

    /**
     * {@inheritDoc}
     */
    public void mouseMoved(EditorMouseEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(DiagramEditor editor) {
    }

    /**
     * {@inheritDoc}
     */
    public void elementAdded(DiagramEditor editor) {
        // spring loading is implemented here
        toolbarManager.doClick("SELECT_MODE");
    }

    /**
     * {@inheritDoc}
     */
    public void elementRemoved(DiagramEditor editor) {
    }
}
