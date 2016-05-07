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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;

import com.nilledom.model.*;
import com.nilledom.persistence.Constants;
import com.nilledom.ui.diagram.DiagramEditor;
import com.nilledom.ui.diagram.EditorMouseEvent;
import com.nilledom.ui.diagram.EditorStateListener;
import com.nilledom.ui.diagram.SelectionListener;
import com.nilledom.ui.model.DiagramTreeModel;
import com.nilledom.ui.model.Project;
import com.nilledom.umldraw.clazz.ClassDiagram;
import com.nilledom.umldraw.shared.GeneralDiagram;
import com.nilledom.umldraw.usecase.UseCaseDiagram;
import com.nilledom.util.Command;
import com.nilledom.util.Msg;

/**
 * This class holds the common elements that the application consists of.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ApplicationState
    implements EditorStateListener, SelectionListener, ChangeListener, FocusListener,
    TreeSelectionListener {

    private JTabbedPane tabbedPane;
    private JLabel coordLabel = new JLabel("    ");
    private JLabel memLabel = new JLabel("    ");
    private UmlModel umlModel;
    private DiagramEditor currentEditor;
    private Timer timer = new Timer();
    private ApplicationCommandDispatcher appCommandDispatcher;
    private EditorCommandDispatcher editorDispatcher;
    private MainToolbarManager toolbarmanager;
    private MenuManager menumanager;
    private File currentFile;
    private EditorFactory editorFactory;
    private TreeDragger treeDragger = new TreeDragger();
    private DiagramTreeModel treeModel = new DiagramTreeModel();
    private DiagramTree tree;
    private Component currentFocusedComponent;



    // The command processor to hold this application's operations.
    private UndoManager undoManager;

    public static boolean TREE_DRAGING = false;

    /**
     * Constructor.
     *
     */
    public ApplicationState( ) {

    }
    public void init(){
        undoManager = new UndoManager();
        appCommandDispatcher = new ApplicationCommandDispatcher(this);
        editorDispatcher = new EditorCommandDispatcher(AppFrame.get());
        AppFrame.get().getContentPane().add(createEditorArea(), BorderLayout.CENTER);
        editorFactory = new EditorFactory( );
        tree.addTreeDraggerListener(treeDragger);
        installMainToolbar();
        installMenubar();
        installStatusbar();
        newProject();
    }

    /**
     * Returns the UML model.
     *
     * @return the UML model
     */
    public UmlModel getUmlModel() {
        return umlModel;
    }



    /**
     * Returns the MenuManager.
     *
     * @return the MenuManager
     */
    public MenuManager getMenuManager() {
        return menumanager;
    }

    /**
     * Returns the UndoManager.
     *
     * @return the UndoManager
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * Returns the currently focused component.
     *
     * @return the currently focused component
     */
    public Component getCurrentFocusedComponent() {
        return currentFocusedComponent;
    }

    /**
     * {@inheritDoc}
     */
    public DiagramEditor getCurrentEditor() {
        return currentEditor;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * Creates the tabbed pane for the editor area.
     *
     * @return the tabbed pane
     */
    private JComponent createEditorArea() {
        JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
        tabbedPane.setPreferredSize(new Dimension(800, 600));
        tabbedPane.addChangeListener(this);
        splitpane.setRightComponent(tabbedPane);

        tree = new DiagramTree(this, treeModel);
        tree.addFocusListener(this);
        tree.getSelectionModel().addTreeSelectionListener(this);

        JScrollPane spane = new JScrollPane(tree);
        spane.setPreferredSize(new Dimension(250, 600));
        splitpane.setLeftComponent(spane);
        splitpane.setOneTouchExpandable(true);
        return splitpane;
    }

    /**
     * Adds the tool bar.
     */
    private void installMainToolbar() {
        toolbarmanager = new MainToolbarManager();
        toolbarmanager.addCommandListener(appCommandDispatcher);
        toolbarmanager.addCommandListener(editorDispatcher);
        AppFrame.get().getContentPane().add(toolbarmanager.getToolbar(), BorderLayout.NORTH);
    }

    /**
     * Adds the menubar.
     */
    private void installMenubar() {
        menumanager = new MenuManager();
        menumanager.addCommandListener(appCommandDispatcher);
        menumanager.addCommandListener(editorDispatcher);
        AppFrame.get().setJMenuBar(menumanager.getMenuBar());
    }

    /**
     * Adds a status bar.
     */
    private void installStatusbar() {
        JPanel statusbar = new JPanel(new BorderLayout());
        statusbar.add(coordLabel, BorderLayout.WEST);
        statusbar.add(memLabel, BorderLayout.EAST);
        AppFrame.get().getContentPane().add(statusbar, BorderLayout.SOUTH);
    }

    /**
     * Stops all threads that were started in this object.
     */
    public void stopThreads() {
        timer.cancel();
        timer.purge();
    }

    /**
     * Sets up and starts the timer task.
     */
    public void scheduleMemTimer() {
        TimerTask task = new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        memLabel.setText(getMemString());
                    }
                });
            }
        };
        // every 5 seconds
        timer.schedule(task, 2000, 5000);
    }

    /**
     * Returns the current file.
     *
     * @return the current file
     */
    public File getCurrentFile() {
        return currentFile;
    }

    /**
     * Sets the current file.
     *
     * @param file the current file
     */
    public void setCurrentFile(File file) {
        currentFile = file;
        if(file!=null) {
            String name = file.getName();
            AppFrame.get().setTitle(name.substring(0, name.length() - Constants.PROJECT_EXTENTION.length() - 1));
        }else
            AppFrame.get().setTitle(Msg.get("project.untitled"));
    }

    /**
     * Creates a new project.
     */
    protected void newProject() {
        setCurrentFile(null);

        undoManager.discardAllEdits();
        tabbedPane.removeAll();
        if(umlModel==null)
            umlModel=new UmlModelImpl();
        else
            umlModel.reset();
        ElementNameGenerator.reset();
        ElementNameGenerator.setModel(umlModel);
        editorFactory.reset();
        openNewUseCaseEditor();
        treeModel.setModel(umlModel);
        tree.expandPath(treeModel.getModelPath());
    }

    /**
     * Restores the state from the specified project.
     *
     * @param project the project
     */
    protected void restoreFromProject(Project project) {

        undoManager.discardAllEdits();
        umlModel = project.getModel();
        treeModel.setModel(umlModel);
        tabbedPane.removeAll();
        for (UmlDiagram diagram : project.getOpenDiagrams()) {
            openExistingEditor((GeneralDiagram) diagram);
        }
    }

    /**
     * Prepares and creates a project object for writing.
     *
     * @return the project object
     */
    protected Project createProjectForWrite() {
        Project result = new Project(umlModel);
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(i);
            result.addOpenDiagram(editorPanel.getDiagramEditor().getDiagram());
        }
        return result;
    }

    // ***********************************************************************
    // ****** Event handling
    // **************************************

    /**
     * Creates the memory information string.
     *
     * @return the memory status string
     */
    private String getMemString() {
        long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();
        long used = total - free;
        used /= (1024 * 1024);
        total /= (1024 * 1024);
        return String.format("used: %dM total: %dM   ", used, total);
    }

    /**
     * Query the specified editor state and set the menu and the toolbars accordingly.
     */
    protected void updateMenuAndToolbars() {
        menumanager.enableMenuItem("UNDO", canUndo());
        menumanager.enableMenuItem("REDO", canRedo());
        toolbarmanager.setEnabled("UNDO", canUndo());
        toolbarmanager.setEnabled("REDO", canRedo());
        // dependent on the component that has the focus, enable the
        // delete options and the view menu options
        selectionStateChanged();
        menumanager.enableViewMenuItems(getCurrentEditor() != null);
    }

    // ************************************************************************
    // **** EditorStateListener
    // *****************************************

    /**
     * {@inheritDoc}
     */
    public void mouseMoved(EditorMouseEvent event) {
        coordLabel.setText(String.format("(%.1f, %.1f)", event.getX(), event.getY()));
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(DiagramEditor editor) {
        updateMenuAndToolbars();
    }

    /**
     * {@inheritDoc}
     */
    public void elementAdded(DiagramEditor editor) {
        updateMenuAndToolbars();
    }

    /**
     * {@inheritDoc}
     */
    public void elementRemoved(DiagramEditor editor) {
        updateMenuAndToolbars();
    }

    // ************************************************************************
    // **** SelectionListener
    // *****************************************

    /**
     * {@inheritDoc}
     */
    public void selectionStateChanged() {
        boolean hasSelection = false;
        if (currentFocusedComponent instanceof DiagramTree) {
            DiagramTree tree = (DiagramTree) currentFocusedComponent;
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();

                hasSelection = (userObject instanceof UmlModelElement &&
                                    !((DefaultMutableTreeNode)node.getParent().getParent()).isRoot()
                                ) ||  userObject instanceof GeneralDiagram;
            }
        } else {
            hasSelection = getCurrentEditor() != null && getCurrentEditor().canDelete();
        }
    /*
     * menumanager.enableMenuItem("CUT", hasSelection); menumanager.enableMenuItem("COPY",
     * hasSelection);
     */
        menumanager.enableMenuItem("DELETE", hasSelection);
    /*
     * toolbarmanager.enableButton("CUT", hasSelection); toolbarmanager.enableButton("COPY",
     * hasSelection);
     */
        toolbarmanager.setEnabled("DELETE", hasSelection);
    }

    /**
     * Returns the modification state.
     *
     * @return the modification state
     */
    protected boolean isModified() {
        return canUndo();
    }

    // ************************************************************************
    // ***** Visible Editor management
    // ****************************************



    /**
     * Opens a new class editor.
     */
    public void openNewClassEditor() {
        EditorPanel editorPanel = editorFactory.openNewClassEditor(umlModel);
        currentEditor = editorPanel.getDiagramEditor();
        addDiagramEditorEvents(editorPanel);
    }

    /**
     * Opens a new use case editor.
     */
    protected void openNewUseCaseEditor() {
        EditorPanel editorPanel = editorFactory.openNewUseCaseEditor(umlModel);
        currentEditor = editorPanel.getDiagramEditor();
        addDiagramEditorEvents(editorPanel);
    }


    /**
     * Opens an existing editor.
     *
     * @param diagram the diagram
     */
    protected void openExistingEditor(GeneralDiagram diagram) {
        if(diagram instanceof ClassDiagram)
            openExistingClassEditor((ClassDiagram) diagram);
        else if(diagram instanceof UseCaseDiagram)
            openExistingUseCaseEditor((UseCaseDiagram) diagram);

        tabbedPane.setSelectedIndex(getIndexOfDiagram(diagram));
    }


    /**
     * Opens an existing class editor.
     *
     * @param diagram the diagram
     */
    protected void openExistingClassEditor(ClassDiagram diagram) {
        if (!isAlreadyOpen(diagram)) {
            EditorPanel editorPanel = editorFactory.openClassEditor(diagram);
            currentEditor = editorPanel.getDiagramEditor();
            addDiagramEditorEvents(editorPanel);
        }
    }

    /**
     * Opens an existing use case editor.
     *
     * @param diagram the diagram
     */
    protected void openExistingUseCaseEditor(UseCaseDiagram diagram) {
        if (!isAlreadyOpen(diagram)) {
            EditorPanel editorPanel = editorFactory.openUseCaseEditor(diagram);
            currentEditor = editorPanel.getDiagramEditor();
            addDiagramEditorEvents(editorPanel);
        }

    }

    private int getIndexOfDiagram(GeneralDiagram diagram) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(i);
            if (editorPanel.getDiagramEditor().getDiagram() == diagram)
                return i;
        }
        return -1;
    }


    /**
     * Determines whether the specified diagram is already opened in the editor.
     *
     * @param diagram the diagram
     * @return true if already openend, false otherwise
     */
    private boolean isAlreadyOpen(GeneralDiagram diagram) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(i);
            if (editorPanel.getDiagramEditor().getDiagram() == diagram)
                return true;
        }
        return false;
    }

    /**
     * Associates the events to the specified editor.
     *
     * @param editorPanel the editor panel
     */
    private void addDiagramEditorEvents(EditorPanel editorPanel) {
        editorPanel.addEditorStateListener(this);
        editorPanel.addSelectionListener(this);
        editorPanel.addAppCommandListener(editorDispatcher);
        editorPanel.addAppCommandListener(appCommandDispatcher);
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        EditorPanel editorPanel = (EditorPanel) tabbedPane.getSelectedComponent();
        currentEditor = editorPanel == null ? null : editorPanel.getDiagramEditor();
        if (currentEditor != null) {
            currentEditor.requestFocusInWindow();
        }
        updateMenuAndToolbars();
    }

    /**
     * Returns the canUndo status.
     *
     * @return true if can undo, false otherwise
     */
    private boolean canUndo() {
        return undoManager.canUndo();
    }

    /**
     * Returns the canRedo status.
     *
     * @return true if can redo, false otherwise
     */
    private boolean canRedo() {
        return undoManager.canRedo();
    }

    /**
     * {@inheritDoc}
     */
    public void focusGained(FocusEvent e) {
        currentFocusedComponent = e.getComponent();
        updateMenuAndToolbars();
    }

    /**
     * {@inheritDoc}
     */
    public void focusLost(FocusEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void valueChanged(TreeSelectionEvent e) {
        selectionStateChanged();
    }

    /**
     * Runs the specified command by this editor's CommandProcessor, which makes the operation
     * reversible.
     *
     * @param command the command to run
     */
    public void execute(Command command) {
        UndoableEditEvent event = new UndoableEditEvent(this, command);
        undoManager.undoableEditHappened(event);
        // We need to run() after notifying the UndoManager in order to ensure
        // correct menu behaviour
        command.run();
    }

    /**
     * {@inheritDoc}
     */
    public void diagramAdded(UmlDiagram diagram) {
    }

    /**
     * {@inheritDoc}
     */
    public void diagramRemoved(UmlDiagram diagram) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            EditorPanel editorPanel = (EditorPanel) tabbedPane.getComponentAt(i);
            if (editorPanel.getDiagramEditor().getDiagram() == diagram) {
                tabbedPane.removeTabAt(i);
            }
        }
    }


    public DiagramTreeModel getTreeModel() {
        return treeModel;
    }

    public ApplicationCommandDispatcher getAppCommandDispatcher() {
        return appCommandDispatcher;
    }
}
