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
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import com.nilledom.conversion.impl.ConverterImpl;
import com.nilledom.draw.DiagramElement;
import com.nilledom.exception.ConversionCanceledException;
import com.nilledom.exception.ConversionException;
import com.nilledom.exception.ObjectSerializerException;
import com.nilledom.exception.ProjectSerializerException;
import com.nilledom.model.UmlDiagram;
import com.nilledom.model.UmlModelElement;
import com.nilledom.persistence.Constants;
import com.nilledom.persistence.serializer.ProjectSerializer;
import com.nilledom.persistence.xml.XmlProjectSerializer;
import com.nilledom.ui.commands.*;
import com.nilledom.ui.diagram.commands.DeleteElementCommand;
import com.nilledom.ui.model.Project;
import com.nilledom.umldraw.shared.GeneralDiagram;
import com.nilledom.umldraw.shared.UmlDiagramElement;
import com.nilledom.util.AppCommandListener;
import com.nilledom.util.MethodCall;
import com.nilledom.util.Msg;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.*;

/**
 * This class implements the application global command handling. This was outfactored from
 * ApplicationState in order to keep it smaller.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ApplicationCommandDispatcher implements AppCommandListener {

    private ApplicationState appState;
    private Map<String, MethodCall> selectorMap = new HashMap<String, MethodCall>();

    /**
     * Constructor.
     *
     * @param anAppState the application state this dispatcher belongs to
     */
    public ApplicationCommandDispatcher(ApplicationState anAppState) {
        appState = anAppState;
        initSelectorMap();
    }

    /**
     * Initializes the selector map.
     */
    private void initSelectorMap() {
        try {
            selectorMap.put("NEW_MODEL", new MethodCall(getClass().getMethod("newModel")));
            selectorMap.put("NEW_CLASS_DIAGRAM",
                new MethodCall(getClass().getMethod("openNewClassEditor")));
            selectorMap.put("NEW_USE_CASE_DIAGRAM",
                new MethodCall(getClass().getMethod("openNewUseCaseEditor")));
            selectorMap.put("OPEN_MODEL", new MethodCall(getClass().getMethod("openModel")));
            selectorMap.put("SAVE_AS", new MethodCall(getClass().getMethod("saveAs")));
            selectorMap.put("SAVE", new MethodCall(getClass().getMethod("save")));
            selectorMap.put("EXPORT_GFX", new MethodCall(getClass().getMethod("exportGfx")));
            selectorMap.put("REDO", new MethodCall(getClass().getMethod("redo")));
            selectorMap.put("UNDO", new MethodCall(getClass().getMethod("undo")));
            selectorMap.put("DELETE", new MethodCall(getClass().getMethod("delete")));
            selectorMap.put("CONVERT", new MethodCall(getClass().getMethod("convert")));
            selectorMap.put("EDIT_SETTINGS", new MethodCall(getClass().getMethod("editSettings")));
            selectorMap.put("QUIT", new MethodCall(getClass().getMethod("quitApplication")));
            selectorMap.put("ABOUT", new MethodCall(getClass().getMethod("about")));
            selectorMap
                .put("HELP_CONTENTS", new MethodCall(getClass().getMethod("displayHelpContents")));
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void handleCommand(String command) {
        MethodCall methodcall = selectorMap.get(command);
        if (methodcall != null) {
            methodcall.call(this);
        }
    }

    /**
     * Creates a new model.
     */
    public void newModel() {
        if (canCreateNewModel()) {
            appState.newProject();
        }
    }

    /**
     * Determines if a new model can be created.
     *
     * @return true the model can be created, false otherwise
     */
    private boolean canCreateNewModel() {
        if (appState.isModified()) {
            return JOptionPane.showConfirmDialog(getShellComponent(),
                Msg.get("confirm.new.message"),
                Msg.get("confirm.new.title"),
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        }
        return true;
    }

    /**
     * Opens a new class editor.
     */
    public void openNewClassEditor() {
        appState.openNewClassEditor();
    }


    /**
     * Opens a new use case editor.
     */
    public void openNewUseCaseEditor() {
        appState.openNewUseCaseEditor();
    }

    /**
     * Undo operation.
     */
    public void undo() {
        appState.getUndoManager().undo();
    }

    /**
     * Redo operation.
     */
    public void redo() {
        appState.getUndoManager().redo();
    }

    /**
     * Quits the application.
     */
    public void quitApplication() {
        AppFrame.get().quitApplication();
    }

    /**
     * Opens the settings editor.
     */
    public void editSettings() {
        System.out.println("EDIT SETTINGS");
    }

    /**
     * Exports graphics as SVG.
     */
    public void exportGfx() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Msg.get("dialog.exportgfx.title"));
        FileNameExtensionFilter svgFilter =
            new FileNameExtensionFilter("Scalable Vector Graphics file (*.svg)", "svg");
        FileNameExtensionFilter pngFilter =
            new FileNameExtensionFilter("Portable Network Graphics file (*.png)", "png");
        fileChooser.addChoosableFileFilter(svgFilter);
        fileChooser.addChoosableFileFilter(pngFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(getShellComponent()) == JFileChooser.APPROVE_OPTION) {
            if (fileChooser.getFileFilter() == svgFilter) {
                try {
                    SvgExporter exporter = new SvgExporter();
                    exporter.writeSVG(appState.getCurrentEditor(), fileChooser.getSelectedFile());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(getShellComponent(), ex.getMessage(),
                        Msg.get("error.exportgfx.title"), JOptionPane.ERROR_MESSAGE);
                }
            } else if (fileChooser.getFileFilter() == pngFilter) {
                try {
                    PngExporter exporter = new PngExporter();
                    exporter.writePNG(appState.getCurrentEditor(), fileChooser.getSelectedFile());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(getShellComponent(), ex.getMessage(),
                        Msg.get("error.exportgfx.title"), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Returns the FileFilter for the serialized project files.
     *
     * @return the FileFilter
     */
    private FileNameExtensionFilter createModelFileFilter() {
        return new FileNameExtensionFilter(Msg.get("application.title")+" project file(*."+ Constants.PROJECT_EXTENTION+")", Constants.PROJECT_EXTENTION);
    }

    /**
     * Opens a MdaUML model.
     */
    public void openModel() {
        if (canOpen()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(Msg.get("dialog.openmodel.title"));
            fileChooser.addChoosableFileFilter(createModelFileFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
            if (fileChooser.showOpenDialog(getShellComponent()) == JFileChooser.APPROVE_OPTION) {
                openModelFromFile(fileChooser.getSelectedFile());
            }
        }
    }

    public void openModelFromFile(File currentFile) {
        try {
            Project openProject = (Project) new XmlProjectSerializer(currentFile.getAbsolutePath())
                    .read();
            appState.restoreFromProject(openProject);
            appState.setCurrentFile(currentFile);

        } catch (ProjectSerializerException e) {
            JOptionPane.showMessageDialog(getShellComponent(), e.getMessage(),
                    Msg.get("error.loadproject.title"), JOptionPane.ERROR_MESSAGE);
        } catch (ObjectSerializerException e) {
            JOptionPane.showMessageDialog(getShellComponent(), e.getMessage(),
                    Msg.get("error.loadproject.title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Checks if application can be quit safely.
     *
     * @return true if can quit safely, false otherwise
     */
    private boolean canOpen() {
        if (appState.isModified()) {
            return JOptionPane.showConfirmDialog(getShellComponent(),
                Msg.get("confirm.open.message"),
                Msg.get("confirm.open.title"),
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        }
        return true;
    }

    /**
     * Saves the model with a file chooser.
     */
    public void saveAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Msg.get("dialog.saveas.title"));
        fileChooser.addChoosableFileFilter(createModelFileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(getShellComponent()) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            File currentFile = saveModelFile(selectedFile);
            appState.setCurrentFile(currentFile);

        }
    }

    /**
     * Saves immediately if possible.
     */
    public void save() {
        if (appState.getCurrentFile() == null) {
            saveAs();
        } else {
            saveModelFile(appState.getCurrentFile());
        }
    }

    /**
     * Writes the current model file. The returned file is different if the input file does not have
     * the proper extension.
     *
     * @param file the file to write
     * @return the file that was written
     */
    private File saveModelFile(File file) {
        try {
            ProjectSerializer serializer = new XmlProjectSerializer(file.getAbsolutePath());

            serializer.write(appState.createProjectForWrite());
            appState.getUndoManager().discardAllEdits();
            appState.updateMenuAndToolbars();
            String projectPath = serializer.getProjectPath(file.getAbsolutePath());

            return new File(projectPath);

        } catch (ProjectSerializerException e) {
            JOptionPane.showMessageDialog(getShellComponent(), e.getMessage(),
                Msg.get("error.saveproject.title"), JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (ObjectSerializerException e) {
            JOptionPane.showMessageDialog(getShellComponent(), e.getMessage(),
                    Msg.get("error.saveproject.title"), JOptionPane.ERROR_MESSAGE);
        }
        return file;
    }

    /**
     * Deletes the current selection.
     */
    public void delete() {
        if (appState.getCurrentFocusedComponent() instanceof DiagramTree) {
            DiagramTree tree = (DiagramTree) appState.getCurrentFocusedComponent();
            if (tree.getSelectionPath() != null) {
                DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();

                Object nodeObject = node.getUserObject();
                if(nodeObject instanceof UmlDiagram){
                    DeleteDiagramCommand command = new DeleteDiagramCommand(appState.getUmlModel(),
                            (UmlDiagram) node.getUserObject());
                    appState.execute(command);
                }
                if(nodeObject instanceof UmlModelElement) {
                    Object parentObj = ((DefaultMutableTreeNode) node.getParent()).getUserObject();
                    if(parentObj instanceof GeneralDiagram){
                        GeneralDiagram diagram = (GeneralDiagram) parentObj;
                        ArrayList<DiagramElement> elementList = new ArrayList<>();
                        for(UmlDiagramElement diagElement : diagram.getElements()){
                            if(diagElement.getModelElement().equals(nodeObject))
                                elementList.add(diagElement);
                        }

                        DeleteElementCommand command = new DeleteElementCommand( diagram.getEditor(),elementList);

                        diagram.getEditor().execute(command);
                    }

                }

            }
        } else if (appState.getCurrentEditor() != null) {
            appState.getCurrentEditor().deleteSelection();
        }
    }

    /**
     * Shows the about dialog.
     */
    public void about() {
        JOptionPane.showMessageDialog(getShellComponent(), Msg.get("dialog.about.text"),
            Msg.get("dialog.about.title"), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the help contents.
     */
    public void displayHelpContents() {
        try {
            URI helpUri = new URI("http://www.tinyuml.org/Wikka/UserDocs");
            Desktop.getDesktop().browse(helpUri);
        } catch (IOException ex) {
            JOptionPane
                .showMessageDialog(getShellComponent(), Msg.get("error.nohelp.message"),
                    Msg.get("error.nohelp.title"), JOptionPane.ERROR_MESSAGE);
        } catch (URISyntaxException ignore) {
            ignore.printStackTrace();
        }
    }

    public void convert(){
        save();
        try {
              new ConverterImpl().convert(appState.createProjectForWrite());
        } catch (ConversionException e) {
            e.printStackTrace();
            Throwable cause = e;
            while(cause != null ){
                if(cause.getMessage()!= null && !cause.getMessage().isEmpty())
                    break;
                cause = e.getCause();
            }

            openModelFromFile(appState.getCurrentFile());
            JOptionPane
                    .showMessageDialog(getShellComponent(), cause.getMessage(),
                            Msg.get("error.conversion.title"), JOptionPane.ERROR_MESSAGE);
        } catch (ConversionCanceledException e) {
            openModelFromFile(appState.getCurrentFile());
        }
    }

    // ************************************************************************
    // ******* General helpers
    // *************************************

    /**
     * Returns the shell component.
     *
     * @return the shell component
     */
    private Component getShellComponent() {
        return AppFrame.get();
    }


}
