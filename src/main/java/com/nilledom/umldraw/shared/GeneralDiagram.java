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
package com.nilledom.umldraw.shared;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nilledom.draw.AbstractCompositeNode;
import com.nilledom.draw.CompositeNode;
import com.nilledom.draw.Connection;
import com.nilledom.draw.Diagram;
import com.nilledom.draw.DiagramElement;
import com.nilledom.draw.DiagramOperations;
import com.nilledom.draw.DrawingContext;
import com.nilledom.draw.DrawingContext.FontType;
import com.nilledom.draw.Label;
import com.nilledom.draw.LabelSource;
import com.nilledom.draw.LineConnectMethod;
import com.nilledom.draw.Node;
import com.nilledom.draw.NodeChangeListener;
import com.nilledom.draw.NullElement;
import com.nilledom.draw.Selection;
import com.nilledom.draw.SimpleLabel;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.AbstractUmlModelElement;
import com.nilledom.model.ElementType;
import com.nilledom.model.NameChangeListener;
import com.nilledom.model.PackageListener;
import com.nilledom.model.PackageableUmlModelElement;
import com.nilledom.model.Relation;
import com.nilledom.model.RelationType;
import com.nilledom.model.UmlModel;
import com.nilledom.model.UmlModelElement;
import com.nilledom.ui.ElementNameGenerator;
import com.nilledom.ui.diagram.DiagramEditor;

/**
 * This class implements the effective layout area. It shows the boundaries of the diagram and also
 * the grid lines.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class GeneralDiagram extends AbstractCompositeNode
    implements NodeChangeListener, LabelSource, Diagram, DiagramElementFactory {

    private static final int ADDITIONAL_SPACE_RIGHT = 10;
    private static final int ADDITIONAL_SPACE_BOTTOM = 10;
    private transient DiagramEditor editor;
    protected transient UmlModel umlmodel;
    private int gridSize = 7;
    private String name;
    private List<Connection> connections = new ArrayList<Connection>();
    private Label nameLabel = new SimpleLabel();
    private boolean gridVisible = true, snapToGrid = true;
    private transient Collection<NameChangeListener> nameChangeListeners =
        new HashSet<NameChangeListener>();
    private transient Set<NodeChangeListener> nodeChangeListeners =
        new HashSet<NodeChangeListener>();

    // the prototype maps
    private transient Map<ElementType, UmlDiagramElement> elementPrototypes;
    private transient Map<RelationType, UmlConnection> connectionPrototypes;

    /**
     * Constructor.
     *
     * @param aModel the UmlModel
     */
    public GeneralDiagram(UmlModel aModel) {
        initialize(aModel);
    }
    public void initialize(UmlModel aModel){
        initializeNameLabel();
        setSize(1000, 550);
        umlmodel = aModel;
        elementPrototypes = setupElementPrototypeMap();
        connectionPrototypes = setupConnectionPrototypeMap();


    }
    public GeneralDiagram() {
    }

    public UmlModel getUmlmodel() {
        return umlmodel;
    }



    /**
     * Returns the element factory.
     *
     * @return the element factory
     */
    public DiagramElementFactory getElementFactory() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override public Selection getSelection(DiagramOperations operations) {
        return new DiagramSelection(operations, this);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String aName) {
        name = aName;
        for (NameChangeListener l : nameChangeListeners) {
            l.nameChanged(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override public String toString() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    public String getLabelText() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    public void setLabelText(String aText) {
        setName(aText);
    }

    /**
     * Returns the state of the gridVisible flag.
     *
     * @return true if grid visible, false otherwise
     */
    public boolean isGridVisible() {
        return gridVisible;
    }

    /**
     * Sets the visibility flag of the grid.
     *
     * @param flag true if grid should be visible, false otherwise
     */
    public void setGridVisible(boolean flag) {
        gridVisible = flag;
    }

    /**
     * Returns the grid size.
     *
     * @return the grid size
     */
    public int getGridSize() {
        return gridSize;
    }

    /**
     * Sets the grid size.
     *
     * @param size the new grid size
     */
    public void setGridSize(int size) {
        gridSize = size;
    }

    /**
     * Returns the status of the snapToGrid property.
     *
     * @return the status of the snapToGrid property
     */
    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    /**
     * Sets the snapping flag.
     *
     * @param flag true to snap, false to ignore snapping
     */
    public void setSnapToGrid(boolean flag) {
        snapToGrid = flag;
    }

    /**
     * {@inheritDoc}
     */
    @Override public CompositeNode getParent() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setParent(CompositeNode parent) {
    }

    /**
     * {@inheritDoc}
     */
    @Override public double getAbsoluteX1() {
        return getOrigin().getX();
    }

    /**
     * {@inheritDoc}
     */
    @Override public double getAbsoluteY1() {
        return getOrigin().getY();
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setAbsolutePos(double xpos, double ypos) {
        setOrigin(xpos, ypos);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(DrawingContext drawingContext) {
        Rectangle bounds = drawingContext.getClipBounds();
        drawBackground(drawingContext, bounds);
        if (gridVisible)
            drawGrid(drawingContext);
        drawBorder(drawingContext);
        drawNameLabel(drawingContext);

        // Draw container children
        super.draw(drawingContext);

        // Draw associations
        for (Connection assoc : connections) {
            assoc.draw(drawingContext);
        }
    }

    /**
     * Returns the drawing grid size.
     *
     * @return the drawing grid size
     */
    private double getDrawGridSize() {
        return gridSize * 5;
    }

    /**
     * Draws the background of the diagram.
     *
     * @param drawingContext the DrawingContext
     * @param bounds         the bounding Rectangle
     */
    private void drawBackground(DrawingContext drawingContext, Rectangle bounds) {
        // System.out.println("drawBackground(), clipBounds: " + bounds);
        double x1 = Math.max(getAbsoluteX1(), bounds.getX());
        double y1 = Math.max(getAbsoluteY1(), bounds.getY());
        double x2 =
            Math.min(bounds.getX() + bounds.getWidth(), getAbsoluteX1() + getSize().getWidth());
        double y2 =
            Math.min(bounds.getY() + bounds.getHeight(), getAbsoluteY1() + getSize().getHeight());
        drawingContext.fillRectangle(x1, y1, x2 - x1, y2 - y1, Color.WHITE);
    }

    /**
     * Draws the diagram border.
     *
     * @param drawingContext the DrawingContext
     */
    private void drawBorder(DrawingContext drawingContext) {
        drawingContext.drawRectangle(getOrigin().getX(), getOrigin().getY(), getSize().getWidth(),
                getSize().getHeight(), null);
    }

    /**
     * Draws the grid lines.
     *
     * @param drawingContext the DrawingContext
     */
    private void drawGrid(DrawingContext drawingContext) {
        double drawingGridSize = getDrawGridSize();

        // Draw vertical lines
        double x1 = getOrigin().getX();
        double x2 = x1 + getSize().getWidth();
        double y1 = getOrigin().getY();
        double y2 = y1 + getSize().getHeight();

        // Start at a visible portion
        double x = x1;
        while (x <= x2) {
            drawingContext.drawGridLine(x, y1, x, y2);
            x += drawingGridSize;
        }

        // Draw horizontal lines
        double y = y1;
        while (y <= y2) {
            drawingContext.drawGridLine(x1, y, x2, y);
            y += drawingGridSize;
        }
    }

    /**
     * Draws the name label in the left upper corner.
     *
     * @param drawingContext the DrawingContext
     */
    private void drawNameLabel(DrawingContext drawingContext) {
        nameLabel.recalculateSize(drawingContext);
        double x = getAbsoluteX1();
        double y = getAbsoluteY1();
        double height = nameLabel.getSize().getHeight() + 6;
        double width = nameLabel.getSize().getWidth() + 10;

        GeneralPath mainShape = new GeneralPath();
        mainShape.moveTo(x, y);
        mainShape.lineTo(x, y + height);
        mainShape.lineTo(x + width, y + height);
        mainShape.lineTo(x + width + 5, y + height - 5);
        mainShape.lineTo(x + width + 5, y);
        mainShape.closePath();
        drawingContext.draw(mainShape, Color.WHITE);
        nameLabel.draw(drawingContext);
    }

    /**
     * Returns the grid position which is nearest to the specified position.
     *
     * @param pos the position
     * @return the nearest grid point
     */
    private double getNearestGridPos(double pos) {
        return Math.round(pos / gridSize) * gridSize;
    }

    /**
     * {@inheritDoc}
     */
    public void snap(Point2D point) {
        if (snapToGrid) {
            point.setLocation(getNearestGridPos(point.getX()), getNearestGridPos(point.getY()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void nodeMoved(Node node) {
        resizeToNode(node);
    }

    /**
     * {@inheritDoc}
     */
    public void nodeResized(Node node) {
        resizeToNode(node);
    }


    @Override public List<UmlDiagramElement> getElements() {
        return getElementFrom(this);
    }
    private List<UmlDiagramElement> getElementFrom(AbstractCompositeNode node){
        List<DiagramElement> children = node.getChildren();
        List<UmlDiagramElement> elements = new ArrayList<>();
        for(DiagramElement child : children) {
            if (child instanceof AbstractCompositeNode)
                elements.addAll(getElementFrom((AbstractCompositeNode) child));
            if (child instanceof UmlDiagramElement)
                elements.add((UmlDiagramElement) child);
        }
        return elements;

    }
    /**
     * {@inheritDoc}
     */
    @Override public List<DiagramElement> getChildren() {
        List<DiagramElement> result = new ArrayList<DiagramElement>();
        result.addAll(super.getChildren());
        result.addAll(connections);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void addChild(DiagramElement child) {
        if (child instanceof Connection) {
            connections.add((Connection) child);
            child.setParent(this);
        } else {
            super.addChild(child);
            resizeToNode((Node) child);
        }


    }

    /**
     * Initializes the name label.
     */
    private void initializeNameLabel() {
        nameLabel.setSource(this);
        nameLabel.setParent(this);
        nameLabel.setOrigin(5, 3);
        nameLabel.setSize(10, 10);
        nameLabel.setFontType(FontType.ELEMENT_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void removeChild(DiagramElement child) {
        if (child instanceof Connection) {
            connections.remove((Connection) child);
        } else {
            super.removeChild(child);
        }


    }

    /**
     * {@inheritDoc}
     */
    @Override public DiagramElement getChildAt(double x, double y) {
        DiagramElement child = super.getChildAt(x, y);

        //Check for connections if no node is selected
        if(child == NullElement.getInstance()){
            for (Connection conn : connections) {
                if (conn.contains(x, y))
                    return conn;
            }

        }

        return child;
    }

    /**
     * Updates this element's bounds according to the specified node. This will happen if the node
     * exceeds the diagram's bounds.
     *
     * @param node the Node to check against
     */
    private void resizeToNode(Node node) {
        // see if the element needs to be resized
        double diffx = node.getAbsoluteX2() - getAbsoluteX2();
        double diffy = node.getAbsoluteY2() - getAbsoluteY2();
        if (diffx > 0 || diffy > 0) {
            setSize(getSize().getWidth() + (diffx > 0 ? (diffx + ADDITIONAL_SPACE_RIGHT) : 0),
                getSize().getHeight() + (diffy > 0 ? (diffy + ADDITIONAL_SPACE_BOTTOM) : 0));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Label getLabelAt(double mx, double my) {
        if (nameLabel.contains(mx, my))
            return nameLabel;
        return null;
    }

    /**
     * Adds a label change listener that listens to changes to the name label.
     *
     * @param l the listener to add
     */
    public void addNameChangeListener(NameChangeListener l) {
        nameChangeListeners.add(l);
    }

    /**
     * Removes a label change listener from the name label.
     *
     * @param l the listener to remove
     */
    public void removeNameChangeListener(NameChangeListener l) {
        nameChangeListeners.remove(l);
    }

    // *************************************************************************
    // ****** NodeChangeListeners of diagrams are usually user interface
    // ****** elements. User interfaces are not part of the persistence model
    // ****** so the listeners are redefined as transient list.
    // **************************************************************************

    /**
     * {@inheritDoc}
     */
    @Override protected Collection<NodeChangeListener> getNodeChangeListeners() {
        return nodeChangeListeners;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void addNodeChangeListener(NodeChangeListener l) {
        nodeChangeListeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void removeNodeChangeListener(NodeChangeListener l) {
        nodeChangeListeners.remove(l);
    }

    // *************************************************************************
    // ****** DiagramElementFactory
    // ************************************

    /**
     * Initializes the element map with the element prototypes.
     *
     * @return the initialized map
     */
    protected abstract Map<ElementType, UmlDiagramElement> setupElementPrototypeMap();

    /**
     * Initializes the map with the connection prototypes.
     *
     * @return the initialized map
     */
    protected abstract Map<RelationType, UmlConnection> setupConnectionPrototypeMap();

    /**
     * {@inheritDoc}
     */
    public UmlNode createNode(ElementType elementType) {
        UmlNode umlnode = (UmlNode) elementPrototypes.get(elementType).clone();
        if (umlnode.getModelElement() != null) {
            UmlModelElement model = umlnode.getModelElement();
            String name = ElementNameGenerator.getName(elementType);
            umlnode.getModelElement().setName(name);
            umlnode.setModelElement(model);
            if(model instanceof PackageableUmlModelElement && umlnode instanceof PackageListener)
                ((PackageableUmlModelElement) model).addPackageListener((PackageListener) umlnode);
        }
        umlnode.addNodeChangeListener(this);
        return umlnode;
    }

    public UmlNode createNodeFromModel(AbstractUmlModelElement modelElement){

        UmlDiagramElement proto =  elementPrototypes.get(modelElement.getElementType());
        if (proto==null)
            throw new IllegalArgumentException("There is no element for the supplied model");
        UmlNode umlnode= (UmlNode) proto.clone();
        umlnode.setModelElement(modelElement);
        umlnode.addNodeChangeListener(this);
        if(modelElement instanceof PackageableUmlModelElement && umlnode instanceof PackageListener)
            ((PackageableUmlModelElement) modelElement).addPackageListener((PackageListener) umlnode);
        return umlnode;

    }

    /**
     * {@inheritDoc}
     */
    public UmlConnection createConnection(RelationType relationType, UmlNode node1, UmlNode node2) throws AddConnectionException {
        UmlConnection prototype = connectionPrototypes.get(relationType);
        UmlConnection conn = null;
        if (prototype != null) {
            conn = (UmlConnection) prototype.clone();
            bindConnection(conn, node1, node2);
        }
        return conn;
    }

    /**
     * {@inheritDoc}
     */
    public LineConnectMethod getConnectMethod(RelationType relationType) {
        UmlConnection conn = connectionPrototypes.get(relationType);
        return (conn == null) ? null : conn.getConnectMethod();
    }

    /**
     * Binds the UmlConnection to the nodes.
     *
     * @param conn  the Connection
     * @param node1 the Node 1
     * @param node2 the Node 2
     */
    private void bindConnection(UmlConnection conn, UmlNode node1, UmlNode node2) throws AddConnectionException {
        conn.setNode1(node1);
        conn.setNode2(node2);

        Relation relation = (Relation) conn.getModelElement();
        if (relation != null) {
            relation.setElement1(node1.getModelElement());
            relation.setElement2(node2.getModelElement());
        }

        node1.addConnection(conn);
        node2.addConnection(conn);

    }

    @Override public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public void setUmlmodel(UmlModel umlmodel) {
        this.umlmodel = umlmodel;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(Label nameLabel) {
        this.nameLabel = nameLabel;
    }

    public DiagramEditor getEditor() {
        return editor;
    }

    public void setEditor(DiagramEditor editor) {
        this.editor = editor;
    }

    public DiagramElement findElementFromModel(UmlModelElement modelElement) {
        List<UmlDiagramElement> elements = getElements();
        for(UmlDiagramElement element : elements){
            if(element.getModelElement()==modelElement)
                return element;
        }
        return null;
    }
}
