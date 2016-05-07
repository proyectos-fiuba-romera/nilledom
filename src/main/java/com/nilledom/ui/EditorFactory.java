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
package com.nilledom.ui;

import javax.swing.*;

import com.nilledom.model.NameChangeListener;
import com.nilledom.model.NamedElement;
import com.nilledom.model.UmlModel;
import com.nilledom.ui.diagram.ClassDiagramEditor;
import com.nilledom.ui.diagram.DiagramEditor;
import com.nilledom.ui.diagram.UseCaseDiagramEditor;
import com.nilledom.umldraw.clazz.ClassDiagram;
import com.nilledom.umldraw.shared.GeneralDiagram;
import com.nilledom.umldraw.usecase.UseCaseDiagram;
import com.nilledom.util.Msg;

import java.awt.*;

/**
 * A manager class for the available class diagrams in the application. It maintains the UI
 * context for in order to properly create the editor.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class EditorFactory {

    private ApplicationState appState;
    private JTabbedPane tabbedPane;
    private int classCounter = 1, useCaseCounter = 1;

    /**
     * Constructor.
     *
     */
    public EditorFactory() {
        appState = AppFrame.get().getAppState();
        tabbedPane = appState.getTabbedPane();
    }

    /**
     * Resets the internal counter.
     */
    public void reset() {
        classCounter = 1;
        useCaseCounter = 1;
    }

    /**
     * Opens a new class editor and displays it in the editor area.
     *
     * @param umlModel the UmlModel the diagram belongs to
     * @return the editor panel
     */
    public EditorPanel openNewClassEditor(UmlModel umlModel) {
        GeneralDiagram diagram = new ClassDiagram(umlModel);
        diagram.setLabelText(
            Msg.get("stdcaption.classdiagram") + " "
                + (classCounter++));
        umlModel.addDiagram(diagram);
        return createEditorPanel(new ClassDiagramEditor( diagram),
            new ClassEditorToolbarManager());
    }

    /**
     * Opens a class editor for an existing diagram.
     *
     * @param diagram the diagram
     * @return the editor panel
     */
    public EditorPanel openClassEditor(GeneralDiagram diagram   ) {
        return createEditorPanel(new ClassDiagramEditor(diagram),
            new ClassEditorToolbarManager());
    }

    /**
     * Opens a use case editor for an existing diagram.
     *
     * @param diagram the diagram
     * @return the editor panel
     */
    public EditorPanel openUseCaseEditor(UseCaseDiagram diagram) {
        return createEditorPanel(new UseCaseDiagramEditor(diagram),
                new UseCaseEditorToolbarManager());
    }

    /**
     * Creates an editor for the specified diagram and adds it to the tabbed pane.
     *
     * @param diagramEditor  the diagram editor
     * @param toolbarManager the ToolbarManager
     * @return the panel
     */
    private EditorPanel createEditorPanel(DiagramEditor diagramEditor,
        ToolbarManager toolbarManager) {
        EditorPanel editor = new EditorPanel(diagramEditor, toolbarManager);
        GeneralDiagram diagram = diagramEditor.getDiagram();
        final Component comp = tabbedPane.add(diagram.getLabelText(), editor);
        int index = tabbedPane.indexOfComponent(comp);
        tabbedPane.setToolTipTextAt(index, diagram.getLabelText());
        diagram.addNameChangeListener(new NameChangeListener() {
            /** {@inheritDoc} */
            public void nameChanged(NamedElement element) {
                int index = tabbedPane.indexOfComponent(comp);
                tabbedPane.setTitleAt(index, element.getName());
                tabbedPane.setToolTipTextAt(index, element.getName());
            }
        });
        tabbedPane.setSelectedComponent(editor);
        tabbedPane.setTabComponentAt(index, new ClosableTabComponent(tabbedPane));
        editor.getDiagramEditor().addFocusListener(appState);
        editor.getDiagramEditor().addUndoableEditListener(appState.getUndoManager());
        return editor;
    }



    /**
     * Creates a new Use Case editor.
     *
     * @param umlModel the UmlModel
     * @return the editor panel
     */
    public EditorPanel openNewUseCaseEditor(UmlModel umlModel) {
        GeneralDiagram diagram = new UseCaseDiagram(umlModel);
        diagram.setLabelText(
            Msg.get("stdcaption.usecasediagram") + " "
                + (useCaseCounter++));
        umlModel.addDiagram(diagram);
        return createEditorPanel(new UseCaseDiagramEditor( diagram),
            new UseCaseEditorToolbarManager());
    }


}
