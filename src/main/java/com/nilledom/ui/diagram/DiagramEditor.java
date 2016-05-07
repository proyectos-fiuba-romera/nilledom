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
package com.nilledom.ui.diagram;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import com.nilledom.draw.*;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.*;
import com.nilledom.ui.AppFrame;
import com.nilledom.ui.diagram.commands.*;
import com.nilledom.umldraw.shared.*;
import com.nilledom.umldraw.usecase.Extend;
import com.nilledom.umldraw.usecase.ExtentionPointNote;
import com.nilledom.util.AppCommandListener;
import com.nilledom.util.Command;
import com.nilledom.util.MethodCall;

/**
 * This class represents the diagram editor. It mainly acts as the component to draw the diagram and
 * to handle the events from the input system. The actual drawing is handled by the Diagram class
 * and its sub elements.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class DiagramEditor extends JComponent
    implements DiagramEditorNotification, DiagramOperations, NodeChangeListener,
    AppCommandListener {

    // For now, we define the margins of the diagram as constants
    private static final double MARGIN_TOP = 10;
    private static final double MARGIN_LEFT = 10;
    private static final double MARGIN_RIGHT = 10;
    private static final double MARGIN_BOTTOM = 10;
    private static Map<String, MethodCall> selectorMap = new HashMap<String, MethodCall>();

    static {
        initSelectorMap();
    }

    protected transient SelectionHandler selectionHandler;
    // We need this object to display dialogs

    private transient EditorMode editorMode;
    private transient List<UndoableEditListener> editListeners =
        new ArrayList<UndoableEditListener>();
    private transient DiagramEditorEditHelper editHelper;
    private transient DiagramEditorRenderHelper renderHelper;
    /**
     * It is nice to report the mapped coordinates to listeners, so it can be used for debug output.
     */
    private List<EditorStateListener> editorListeners = new ArrayList<EditorStateListener>();
    /**
     * This is the root of the shape hierarchy.
     */
    private GeneralDiagram diagram;

    /**
     * Initializes the internal default selector map.
     */
    /**
     * Empty constructor for testing. Do not use !
     */
    public DiagramEditor() {
    }

    /**
     * Constructor. Basic setup of the layout area.
     *
     * @param aDiagram the diagram
     */
    public DiagramEditor( GeneralDiagram aDiagram) {
        setFocusable(true);
        diagram = aDiagram;
        diagram.addNodeChangeListener(this);
        diagram.setEditor(this);
        // Make sure the this component has no layout manager, is opaque and has
        // no double buffer
        setLayout(null);
        initEditorMembers();

        setOpaque(true);
        setDoubleBuffered(true);
        diagram.setOrigin(MARGIN_LEFT, MARGIN_TOP);

        installHandlers();
        setToDiagramSize();
    }

    /**
     * Initializes the selector map.
     */
    private static void initSelectorMap() {
        try {
            selectorMap.put("SELECT_MODE",
                new MethodCall(DiagramEditor.class.getMethod("setSelectionMode")));
            selectorMap.put("REDRAW", new MethodCall(DiagramEditor.class.getMethod("redraw")));
            selectorMap.put("ZOOM_50",
                new MethodCall(DiagramEditor.class.getMethod("setScaling", Scaling.class),
                    Scaling.SCALING_50));
            selectorMap.put("ZOOM_75",
                new MethodCall(DiagramEditor.class.getMethod("setScaling", Scaling.class),
                    Scaling.SCALING_75));
            selectorMap.put("ZOOM_100",
                new MethodCall(DiagramEditor.class.getMethod("setScaling", Scaling.class),
                    Scaling.SCALING_100));
            selectorMap.put("ZOOM_150",
                new MethodCall(DiagramEditor.class.getMethod("setScaling", Scaling.class),
                    Scaling.SCALING_150));
            selectorMap.put("BRING_TO_FRONT",
                new MethodCall(DiagramEditor.class.getMethod("bringToFront")));
            selectorMap
                .put("PUT_TO_BACK", new MethodCall(DiagramEditor.class.getMethod("putToBack")));
            selectorMap.put("EDIT_PROPERTIES",
                    new MethodCall(DiagramEditor.class.getMethod("editProperties")));
            selectorMap.put("UNPACK",
                    new MethodCall(DiagramEditor.class.getMethod("unpack")));

            selectorMap.put("CREATE_NOTE",
                new MethodCall(DiagramEditor.class.getMethod("setCreationMode", ElementType.class),
                    ElementType.NOTE));
            selectorMap.put("CREATE_NOTE_CONNECTION", new MethodCall(
                DiagramEditor.class.getMethod("setCreateConnectionMode", RelationType.class),
                RelationType.NOTE_CONNECTOR));

            selectorMap.put("RESET_POINTS",
                    new MethodCall(DiagramEditor.class.getMethod("resetConnectionPoints")));
            selectorMap.put("RECT_TO_DIRECT",
                    new MethodCall(DiagramEditor.class.getMethod("rectilinearToDirect")));
            selectorMap.put("DIRECT_TO_RECT",
                    new MethodCall(DiagramEditor.class.getMethod("directToRectilinear")));
            selectorMap.put("SHOW_EXTENTION_POINT",
                    new MethodCall(DiagramEditor.class.getMethod("showExtentionPoint")));
            selectorMap.put("HIDE_EXTENTION_POINT",
                    new MethodCall(DiagramEditor.class.getMethod("hideExtentionPoint")));
            selectorMap.put("NAVIGABLE_TO_SOURCE", new MethodCall(
                    DiagramEditor.class.getMethod("setNavigability", RelationEndType.class),
                    RelationEndType.SOURCE));
            selectorMap.put("NAVIGABLE_TO_TARGET", new MethodCall(
                    DiagramEditor.class.getMethod("setNavigability", RelationEndType.class),
                    RelationEndType.TARGET));



        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Initializes the transient editor members.
     */
    private void initEditorMembers() {
        renderHelper = createRenderHelper();
        editHelper = createEditHelper();
        selectionHandler = createSelectionHandler();
        editorMode = selectionHandler;
    }

    /**
     * Creates the selection handler.
     *
     * @return the selection handler
     */
    private SelectionHandler createSelectionHandler() {
        return new SelectionHandler(this);
    }

    /**
     * Creates the render helper.
     *
     * @return the render helper
     */
    private DiagramEditorRenderHelper createRenderHelper() {
        return new DiagramEditorRenderHelper(this);
    }

    /**
     * Creates the edit helper.
     *
     * @return the edit helper
     */
    private DiagramEditorEditHelper createEditHelper() {
        return new DiagramEditorEditHelper(this);
    }

    /**
     * Returns the current editor mode.
     *
     * @return the current editor mode
     */
    protected EditorMode getEditorMode() {
        return editorMode;
    }

    /**
     * Returns this editor's scaling.
     *
     * @return the scaling
     */
    protected Scaling getScaling() {
        return renderHelper.getScaling();
    }

    /**
     * Rescales the view.
     *
     * @param aScaling a Scaling object
     */
    public void setScaling(Scaling aScaling) {
        renderHelper.setScaling(aScaling);
        repaint();
    }

    /**
     * Returns this component's DrawingContext.
     *
     * @return the DrawingContext
     */
    public DrawingContext getDrawingContext() {
        return renderHelper.getDrawingContext();
    }

    /**
     * Returns this object's editor state listeners.
     *
     * @return the editor state listeners
     */
    protected Collection<EditorStateListener> getEditorListeners() {
        return editorListeners;
    }

    /**
     * Adds the specified UndoableEditListener.
     *
     * @param l the UndoableEditListener to add
     */
    public void addUndoableEditListener(UndoableEditListener l) {
        editListeners.add(l);
        editHelper.addUndoableEditListener(l);
    }

    /**
     * Adds an EditorStateListener.
     *
     * @param l a listener
     */
    public void addEditorStateListener(EditorStateListener l) {
        editorListeners.add(l);
    }

    /**
     * Adjusts this component's preferredSize attribute to the diagram's size. This also influences
     * the scroll pane which the component is contained in.
     */
    private void setToDiagramSize() {
        setPreferredSize(
            new Dimension((int) (diagram.getSize().getWidth() + MARGIN_RIGHT + MARGIN_LEFT),
                (int) (diagram.getSize().getHeight() + MARGIN_BOTTOM + MARGIN_TOP)));
        invalidate();
    }

    /**
     * Adds the event handlers.
     */
    private void installHandlers() {
        addMouseListener(editHelper);
        addMouseMotionListener(editHelper);

        // install Escape KeyBinding
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelEditing");
        getActionMap().put("cancelEditing", new AbstractAction() {
            /** {@inheritDoc} */
            public void actionPerformed(ActionEvent e) {
                cancelEditing();
            }
        });
    }

    /**
     * Cancels the current edit action.
     */
    private void cancelEditing() {
        editHelper.cancelEditing();
        editorMode.cancel();
        redraw();
    }

    /**
     * Removes the current selection.
     */
    public void deleteSelection() {
        Collection<DiagramElement> elements = getSelectedElements();
        elements.remove(diagram); // prevent that the diagram is deleted
        execute(new DeleteElementCommand(this, elements));
    }

    // *************************************************************************
    // ***** Drawing the component
    // *******************************************

    /**
     * Determines whether there are selected elements in the diagram that can be deleted.
     *
     * @return true if elements can be delete, false otherwise
     */
    public boolean canDelete() {
        Collection<DiagramElement> elements = getSelectedElements();
        return !(elements.size() == 0 || elements.size() == 1 && elements.contains(diagram));
    }

    /**
     * {@inheritDoc}
     */
    @Override public void paintComponent(Graphics g) {
        Rectangle clipBounds = new Rectangle();
        g.getClipBounds(clipBounds);
        renderHelper.paintComponent(g, clipBounds, true);
    }

    // ************************************************************************
    // ***** Editor information
    // ************************************************************************

    /**
     * Paints the component into a non-screen Graphics object.
     *
     * @param g the Graphics object
     */
    public void paintComponentNonScreen(Graphics g) {
        Dimension canvasSize = getTotalCanvasSize();
        Rectangle clipBounds = new Rectangle(0, 0, canvasSize.width, canvasSize.height);
        g.setClip(clipBounds);
        renderHelper.paintComponent(g, clipBounds, false);
    }

    /**
     * Returns the diagram.
     *
     * @return the diagram
     */
    public GeneralDiagram getDiagram() {
        return diagram;
    }

    /**
     * Returns the current selection.
     *
     * @return the selected element
     */
    public List<DiagramElement> getSelectedElements() {
        return selectionHandler.getSelectedElements();
    }

    // ************************************************************************
    // ***** Editor commands. These are invoked by external clients, the main
    // ***** purpose is to provide the external interface for menu commands
    // ***** and sorts.
    // ************************************************************************

    /**
     * Returns the total canvas size for export functions. The total size includes the margins
     *
     * @return the total canvas size
     */
    public Dimension getTotalCanvasSize() {
        Dimension2D diagramSize = diagram.getSize();
        Dimension result = new Dimension();
        result.width = (int) (diagramSize.getWidth() + MARGIN_LEFT + MARGIN_RIGHT);
        result.height = (int) (diagramSize.getHeight() + MARGIN_TOP + MARGIN_BOTTOM);
        return result;
    }

    /**
     * Sets the editor into selection mode.
     */
    public void setSelectionMode() {
        editorMode = selectionHandler;
    }

    /**
     * Switches the editor into creation mode.
     *
     * @param elementType the ElementType that indicates what to create
     */
    public void setCreationMode(ElementType elementType) {
        editorMode = createCreationHandler(elementType);
    }
    /**
     * Switches the editor into creation from model mode.
     *
     * @param model the model that indicates what to create
     */
    public void setCreationModeFromModel(AbstractUmlModelElement model) {
        editorMode = createCreationHandlerFromModel(model);
    }

    /**
     * Creates the CreationHandler for Element types.
     *
     * @param elementType the element type to create
     * @return the CreationHandler
     */
    private CreationHandler createCreationHandler(ElementType elementType) {
        CreationHandler result = new CreationHandler(this);
        result.setElementType(elementType);
        return result;
    }

    /**
     * Creates the CreationHandler for Element types.
     *
     * @param model the model to create
     * @return the CreationHandler
     */
    private CreationHandler createCreationHandlerFromModel(AbstractUmlModelElement model) {
        CreationHandler result = new CreationHandler(this);
        result.setElementFromModel(model);
        return result;
    }


    /**
     * Switches the editor into connection creation mode.
     *
     * @param relationType the RelationType to create
     */
    public void setCreateConnectionMode(RelationType relationType) {
        editorMode = createLineHandler(relationType);
    }

    /**
     * Creates the LineHandler object to create connections.
     *
     * @param relationType the RelationType
     * @return the LineHandler object
     */
    private LineHandler createLineHandler(RelationType relationType) {
        LineHandler result = new LineHandler(this);
        result.setRelationType(relationType,
                getDiagram().getElementFactory().getConnectMethod(relationType));
        return result;
    }

    /**
     * Immediate redraw of the view.
     */
    public void redraw() {
        paintImmediately(0, 0, getWidth(), getHeight());
    }

    /**
     * Sets the grid to visible.
     *
     * @param flag true for visible grid, false otherwise
     */
    public void showGrid(boolean flag) {
        diagram.setGridVisible(flag);
        repaint();
    }

    /**
     * Activates grid snapping.
     *
     * @param flag true if snapping should be supported, false otherwise
     */
    public void snapToGrid(boolean flag) {
        diagram.setSnapToGrid(flag);
    }

    /**
     * Resets the current connection's points.
     */
    public void resetConnectionPoints() {
        DiagramElement elem = selectionHandler.getSelectedElements().get(0);
        if (elem instanceof Connection) {
            execute(new ResetConnectionPointsCommand(this, (Connection) elem));
        }
    }

    /**
     * Brings the current selection to the front.
     */
    public void bringToFront() {
        if (selectionHandler.getSelectedElements().size() > 0) {
            diagram.bringChildToFront(selectionHandler.getSelectedElements().get(0));
            redraw();
        }
    }

    /**
     * Puts the current selection to the back.
     */
    public void putToBack() {
        if (getSelectedElements().size() > 0) {
            diagram.putChildToBack(getSelectedElements().get(0));
            redraw();
        }
    }

    /**
     * Edits the current selection's properties.
     */
    public void editProperties() {
        if (getSelectedElements().size() > 0) {
            editProperties(getSelectedElements().get(0));
        }
    }

    /**
     * Unpacks the current selection's model..
     */
    public void unpack() {
        if (getSelectedElements().size() > 0) {
            DiagramElement selected = getSelectedElements().get(0);
            if(selected instanceof UmlNode){
                UmlModelElement model = ((UmlNode) selected).getModelElement();
                if(model instanceof PackageableUmlModelElement)
                    ((PackageableUmlModelElement) model).unpack();
            }
        }
    }

    /**
     * Switches a rectilinear connection to a direct one.
     */
    public void rectilinearToDirect() {
        if (getSelectedElements().size() > 0 && getSelectedElements()
                .get(0) instanceof UmlConnection) {
            UmlConnection conn = (UmlConnection) getSelectedElements().get(0);
            execute(new ConvertConnectionTypeCommand(this, conn, new SimpleConnection()));
            // we can only tell the selection handler to forget about the selection
            selectionHandler.deselectAll();
        }
    }

    public void showExtentionPoint() {
        if (getSelectedElements().size() > 0 && getSelectedElements()
                .get(0) instanceof Extend) {
            Extend extend = (Extend) getSelectedElements().get(0);
            extend.setShow(true);
            repaint();
        }
    }
    public void hideExtentionPoint() {
        if (getSelectedElements().size() > 0 && getSelectedElements()
                .get(0) instanceof Extend) {
            Extend extend = (Extend) getSelectedElements().get(0);
            extend.setShow(false);
            repaint();
        }

    }
    /**
     * Switches a direct connection into a rectilinear one.
     */
    public void directToRectilinear() {
        if (getSelectedElements().size() > 0 && getSelectedElements()
                .get(0) instanceof UmlConnection) {
            UmlConnection conn = (UmlConnection) getSelectedElements().get(0);
            execute(new ConvertConnectionTypeCommand(this, conn, new RectilinearConnection()));
            // we can only tell the selection handler to forget about the selection
            selectionHandler.deselectAll();
        }
    }

    /**
     * Sets the end type navigability of the current selected connection.
     *
     * @param endType the RelationEndType
     */
    public void setNavigability(RelationEndType endType) {
        if (getSelectedElements().size() > 0 && getSelectedElements()
                .get(0) instanceof UmlConnection) {
            UmlConnection conn = (UmlConnection) getSelectedElements().get(0);
            Relation relation = (Relation) conn.getModelElement();
            // Setup a toggle
            if (endType == RelationEndType.SOURCE) {
                execute(new SetConnectionNavigabilityCommand(this, conn, endType,
                        !relation.isNavigableToElement1()));
            }
            if (endType == RelationEndType.TARGET) {
                execute(new SetConnectionNavigabilityCommand(this, conn, endType,
                        !relation.isNavigableToElement2()));
            }
        }
    }

    /**
     * Runs the specified command by this editor's CommandProcessor, which makes the operation
     * reversible.
     *
     * @param command the command to run
     */
    public void execute(Command command) {
        UndoableEditEvent event = new UndoableEditEvent(this, command);
        for (UndoableEditListener l : editListeners) {
            l.undoableEditHappened(event);
        }
        // We need to run() after notifying the UndoManager in order to ensure
        // correct menu behaviour

        command.run();

    }

    /**
     * Notifies the listeners about a state change.
     */
    private void notifyStateChanged() {
        for (EditorStateListener l : editorListeners) {
            l.stateChanged(this);
        }
    }

    // *************************************************************************
    // ***** Editor callbacks
    // *********************************

    /**
     * Adds the specified SelectionListener.
     *
     * @param l the SelectionListener to add
     */
    public void addSelectionListener(SelectionListener l) {
        selectionHandler.addSelectionListener(l);
    }

    /**
     * Adds the specified AppCommandListener.
     *
     * @param l the AppCommandListener to add
     */
    public void addAppCommandListener(AppCommandListener l) {
        selectionHandler.addAppCommandListener(l);
    }

    // *************************************************************************
    // ***** DiagramEditorNotification
    // *********************************

    /**
     * Update method called after a state change from a Command. Such state changes include move
     * operations.
     */
    public void notifyElementsMoved() {
        editorMode.stateChanged();
        notifyStateChanged();
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    public void notifyElementAdded(DiagramElement element) {
        element.setDiagram(getDiagram());
        for (EditorStateListener l : editorListeners) {
            l.elementAdded(this);
        }
        if (element instanceof UmlDiagramElement) {
            UmlDiagramElement diagramElement = (UmlDiagramElement) element;
            if (diagramElement.getModelElement() != null) {
                AppFrame.get().getAppState().getUmlModel().addElement(diagramElement.getModelElement(), getDiagram());
            }
        }
        if(element.isInBack())
            diagram.putChildToBack(element);
        repaint();
    }


    /**
     * {@inheritDoc}
     */
    public void notifyElementRemoved(DiagramElement element) {
        element.setDiagram(null);
        for (EditorStateListener l : editorListeners) {
            l.elementRemoved(this);
        }
        selectionHandler.elementRemoved(element);
        if (element instanceof UmlDiagramElement) {
            UmlDiagramElement diagramElement = (UmlDiagramElement) element;
            if (diagramElement.getModelElement() != null) {
                AppFrame.get().getAppState().getUmlModel().removeElement(diagramElement.getModelElement(), getDiagram());
            }
        }

        if (element instanceof ExtentionPointNote)
            ((ExtentionPointNote) element).delete();
        if (element instanceof Extend)
            ((Extend) element).deleteExtentionPoint(this);

        repaint();
    }

    /**
     * {@inheritDoc}
     */
    public void notifyElementResized(DiagramElement element) {
        editorMode.stateChanged();
        notifyStateChanged();
        repaint();
    }

    // *************************************************************************
    // ***** DiagramEditorOperations
    // *********************************

    /**
     * {@inheritDoc}
     */
    public abstract void editProperties(DiagramElement element);

    /**
     * {@inheritDoc}
     */
    public void moveElements(Command[] moveOperations) {
        MoveElementCommand cmd = new MoveElementCommand(this, moveOperations);
        execute(cmd);
    }

    /**
     * {@inheritDoc}
     */
    public void setNewConnectionPoints(Connection conn, List<Point2D> points) {
        execute(new EditConnectionPointsCommand(this, conn, points));
    }

    /**
     * {@inheritDoc}
     */
    public void resizeElement(Node element, Point2D newpos, Dimension2D size) {
        ResizeElementCommand cmd = new ResizeElementCommand(this, element, newpos, size);
        execute(cmd);
    }

    /**
     * Open an editor for the specified Label object.
     *
     * @param label the Label object
     */
    public void editLabel(Label label) {
        editHelper.editLabel(label);
    }

    /**
     * {@inheritDoc}
     */
    public void nodeResized(Node node) {
        setToDiagramSize();
    }

    /**
     * {@inheritDoc}
     */
    public void nodeMoved(Node node) {
    }

    /**
     * {@inheritDoc}
     */
    public void handleCommand(String command) {
        MethodCall methodcall = selectorMap.get(command);
        if (methodcall != null)
            methodcall.call(this);
    }

    public void addNestConnectionToParent(Node node,CompositeNode nesting) {
        if(!(node instanceof AbstractNode))
            return;

        for(Connection connection: node.getConnections()){
            if(connection instanceof Nest){
                Nest nest = (Nest) connection;
                if(nest.getNode1().equals(nesting) &&
                nest.getNode2().equals(node))
                    return;
            }
        }

        UmlConnection conn = null;
        try {
            conn = getDiagram().getElementFactory().createConnection(RelationType.NEST, (UmlNode) nesting, (UmlNode) node);
        } catch (AddConnectionException e) {
            e.printStackTrace();
        }
        LineConnectMethod connectMethod = getDiagram().getElementFactory().getConnectMethod(RelationType.NEST);
        Point2D dest= new Point2D.Double(node.getAbsCenterX(),node.getAbsCenterY());
        Point2D orig= new Point2D.Double(nesting.getAbsCenterX(),nesting.getAbsCenterY());
        connectMethod.generateAndSetPointsToConnection(conn, nesting, node, orig, dest);
        AddConnectionCommand command = new AddConnectionCommand(this, getDiagram(), conn);
        execute(command);
        redraw();
    }
}
