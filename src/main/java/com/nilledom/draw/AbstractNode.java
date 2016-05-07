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
package com.nilledom.draw;




import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.PackageListener;
import com.nilledom.model.PackageableUmlModelElement;
import com.nilledom.model.RelationType;
import com.nilledom.model.UmlPackage;
import com.nilledom.ui.diagram.commands.DeleteElementCommand;
import com.nilledom.umldraw.clazz.Dependency;
import com.nilledom.umldraw.shared.*;
import com.nilledom.umldraw.usecase.Extend;
import com.nilledom.umldraw.usecase.Include;
import com.nilledom.util.Msg;

/**
 * This class implements an abstract Node class.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public abstract class AbstractNode implements Node, ConnectionVisitor{

    private Point2D origin = new Point2D.Double(0, 0);
    private Dimension2D size = new DoubleDimension(40, 20);
    private Dimension2D minimumSize = new DoubleDimension(40, 20);
    private CompositeNode parent;
    private transient Collection<NodeChangeListener> changeListeners =
        new ArrayList<NodeChangeListener>();
    private List<Connection> connections = new ArrayList<Connection>();
    private transient NodeSelection selection = null;
    private GeneralDiagram diagram;

    public AbstractNode() {

    }

    @Override
    public GeneralDiagram getDiagram() {
        return diagram;
    }

    public boolean isEditable() {
        return false;
    }

    public void setDiagram(GeneralDiagram diagram) {
        this.diagram = diagram;
    }


    /**
     * {@inheritDoc}
     */
    @Override public Object clone() {
        try {
            AbstractNode node = (AbstractNode) super.clone();
            node.origin = (Point2D) origin.clone();
            node.size = (Dimension2D) size.clone();
            node.minimumSize = (Dimension2D) minimumSize.clone();
            node.connections = new ArrayList<Connection>();
            node.changeListeners = new ArrayList<NodeChangeListener>();
            // just copy the the parent to avoid the recursion
            node.selection = null; // do not copy the selection
            return node;
        } catch (CloneNotSupportedException ignore) {
            ignore.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public CompositeNode getParent() {
        return parent;
    }


    /**
     *
     * @return  the top-level parent that it's not a diagram if any or the diagram otherwise
     */
    public CompositeNode getLastParent(){
        CompositeNode lastParent=getParent();
        while(lastParent!=null && !(lastParent.getParent() instanceof GeneralDiagram))
            lastParent=lastParent.getParent();
        return lastParent;
    }


    /**
     * {@inheritDoc}
     */
    public void setParent(CompositeNode aParent) {
        parent = aParent;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAncestor(DiagramElement element) {
        CompositeNode tmp = getParent();
        while (tmp != null) {
            if (tmp == element)
                return true;
            tmp = tmp.getParent();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Point2D getOrigin() {
        return origin;
    }

    public void setOrigin(Point2D origin) {
        this.origin = origin;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrigin(double xpos, double ypos) {
        origin.setLocation(xpos, ypos);
    }

    /**
     * {@inheritDoc}
     */
    public double getAbsoluteX1() {
        return parent.getAbsoluteX1() + getOrigin().getX();
    }

    /**
     * {@inheritDoc}
     */
    public double getAbsoluteY1() {
        return parent.getAbsoluteY1() + getOrigin().getY();
    }

    /**
     * {@inheritDoc}
     */
    public Dimension2D getMinimumSize() {
        return minimumSize;
    }

    /**
     * {@inheritDoc}
     */
    public void setMinimumSize(double width, double height) {
        minimumSize.setSize(width, height);
    }


    public void setMinimumSize(Dimension2D minimumSize) {
        this.minimumSize = minimumSize;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension2D getSize() {
        return size;
    }

    public void setSize(Dimension2D size) {
        this.size = size;
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(double w, double h) {
        setSizePlain(w, h);
        invalidate();
        notifyNodeResized();
    }

    /**
     * {@inheritDoc}
     */
    public void setWidth(double width) {
        setSize(width, getSize().getHeight());
    }

    /**
     * {@inheritDoc}
     */
    public void setHeight(double height) {
        setSize(getSize().getWidth(), height);
    }

    /**
     * Sets the internal size, without notification and without invalidating the object.
     *
     * @param width  the width the width
     * @param height the height the height
     */
    protected void setSizePlain(double width, double height) {
        size.setSize(width, height);
    }

    public Collection<NodeChangeListener> getChangeListeners() {
        return changeListeners;
    }

    public NodeSelection getSelection() {
        return selection;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(double xcoord, double ycoord) {
        double absx = getAbsoluteX1(), absy = getAbsoluteY1();
        return xcoord >= absx && xcoord <= absx + getSize().getWidth() && ycoord >= absy
            && ycoord <= absy + getSize().getHeight();
    }

    /**
     * {@inheritDoc}
     */
    public void setAbsolutePos(double xpos, double ypos) {
        // Only change the values if position was changed
        if (!GeometryUtil.getInstance().equals(xpos, getAbsoluteX1()) || !GeometryUtil.getInstance()
            .equals(ypos, getAbsoluteY1())) {
            origin.setLocation(xpos - parent.getAbsoluteX1(), ypos - parent.getAbsoluteY1());
            notifyNodeMoved();
        }
    }

    /**
     * {@inheritDoc}
     */
    public double getAbsCenterX() {
        return getAbsoluteX1() + getSize().getWidth() / 2;
    }

    /**
     * {@inheritDoc}
     */
    public double getAbsCenterY() {
        return getAbsoluteY1() + getSize().getHeight() / 2;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible(Rectangle2D clipBounds) {
        return clipBounds.intersects(DrawingShapeFactory.getInstance()
            .createRect2d(getAbsoluteX1(), getAbsoluteY1(), getSize().getWidth(),
                getSize().getHeight()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean intersects(Line2D line) {
        return getAbsoluteBounds().intersectsLine(line);
    }

    /**
     * {@inheritDoc}
     */
    public boolean intersects(Rectangle2D bounds) {
        return getAbsoluteBounds().intersects(bounds);
    }

    /**
     * Returns the absolute bounding box for this node.
     *
     * @return the absolute bounds for this node
     */
    public Rectangle2D getAbsoluteBounds() {
        return new Rectangle2D.Double(getAbsoluteX1(), getAbsoluteY1(), getSize().getWidth(),
            getSize().getHeight());
    }

    /**
     * {@inheritDoc}
     */
    public void calculateIntersection(Line2D line, Point2D intersectionPoint) {

        // check every of the four sides
        Line2D side = new Line2D.Double();

        // top line
        side.setLine(getAbsoluteX1(), getAbsoluteY1(), getAbsoluteX1() + getSize().getWidth(),
            getAbsoluteY1());
        if (line.intersectsLine(side)) {
            GeometryUtil.getInstance().computeLineIntersection(line, side, intersectionPoint);
            return;
        }
        // right line
        side.setLine(getAbsoluteX1() + getSize().getWidth(), getAbsoluteY1(),
            getAbsoluteX1() + getSize().getWidth(), getAbsoluteY1() + getSize().getHeight());
        if (line.intersectsLine(side)) {
            GeometryUtil.getInstance().computeLineIntersection(line, side, intersectionPoint);
            return;
        }
        // bottom line
        side.setLine(getAbsoluteX1(), getAbsoluteY1() + getSize().getHeight(),
            getAbsoluteX1() + getSize().getWidth(), getAbsoluteY1() + getSize().getHeight());
        if (line.intersectsLine(side)) {
            GeometryUtil.getInstance().computeLineIntersection(line, side, intersectionPoint);
            return;
        }
        // left line
        side.setLine(getAbsoluteX1(), getAbsoluteY1(), getAbsoluteX1(),
            getAbsoluteY1() + getSize().getHeight());
        if (line.intersectsLine(side)) {
            GeometryUtil.getInstance().computeLineIntersection(line, side, intersectionPoint);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Selection getSelection(DiagramOperations operations) {
        if (selection == null) {
            selection = new NodeSelection(operations, this);
        } else {
            selection.updateDimensions();
        }
        return selection;
    }

    /**
     * A method for testing purposes only. Invocations to the Selection can then be registered.
     *
     * @param aSelection the testing selection
     */
    protected void setSelection(NodeSelection aSelection) {
        selection = aSelection;
    }

    /**
     * {@inheritDoc}
     */
    public double getAbsoluteX2() {
        return getAbsoluteX1() + getSize().getWidth();
    }

    /**
     * {@inheritDoc}
     */
    public double getAbsoluteY2() {
        return getAbsoluteY1() + getSize().getHeight();
    }

    /**
     * {@inheritDoc}
     */
    public void addNodeChangeListener(NodeChangeListener l) {
        changeListeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeNodeChangeListener(NodeChangeListener l) {
        changeListeners.remove(l);
    }

    /**
     * Returns this object's NodeChangeListeners. Can be overridden.
     *
     * @return the change listeners
     */
    protected Collection<NodeChangeListener> getNodeChangeListeners() {
        return changeListeners;
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValid() {
        return true;
    }

    /**
     * Notifies the listeners that this node has moved.
     */
    protected void notifyNodeMoved() {
        for (NodeChangeListener l : getNodeChangeListeners()) {
            l.nodeMoved(this);
        }
    }

    /**
     * Notifies all listeners that this Node was resized.
     */
    protected void notifyNodeResized() {
        for (NodeChangeListener l : getNodeChangeListeners()) {
            l.nodeResized(this);
        }
    }

    // *************************************************************************
    // ***** Connections
    // ********************

    /**
     * {@inheritDoc}
     */
    public Collection<? extends Connection> getConnections() {
        return connections;
    }

    @Override
    public void addConnection(Connection conn) throws AddConnectionException {
        conn.acceptNode(this);
        connections.add(conn);
    }

    @Override
    public void removeConnection(Connection connection){
        connection.cancelNode(this);
        connections.remove(connection);
    }



    @Override public void addConcreteConnection(Connection connection)  {}
    @Override public void addConcreteConnection(Nest connection)  {}
    @Override public void addConcreteConnection(Inheritance connection)  {}
    @Override public void addConcreteConnection(Association connection)  {}
    @Override public void addConcreteConnection(Extend connection)  {}
    @Override public void addConcreteConnection(Include connection)  {}
    @Override public void addConcreteConnection(NoteConnection connection)  {}
    @Override public void addConcreteConnection(Dependency connection) {}


    @Override public void removeConcreteConnection(Connection connection){}
    @Override public void removeConcreteConnection(Nest connection){}
    @Override public void removeConcreteConnection(Inheritance connection){}
    @Override public void removeConcreteConnection(Association connection){}
    @Override public void removeConcreteConnection(Extend connection){}
    @Override public void removeConcreteConnection(Include connection){}
    @Override public void removeConcreteConnection(NoteConnection connection){}
    @Override public void removeConcreteConnection(Dependency connection){}

    @Override
    public boolean acceptsConnectionAsSource(RelationType relationType) {
        return false;
    }

    @Override
    public void validateConnectionAsTarget(RelationType relationType, UmlNode node) throws AddConnectionException {
        throw new AddConnectionException(Msg.get("error.connection.invalidConnection"));
    }



    // *************************************************************************
    // ***** Nesting
    // ********************


    public void removeExistingConnection(Class clazz) {
        Connection todelete =null;
        for(Connection conn : getConnections()){
            if(clazz.isInstance(conn) && (conn.getNode1()==this || conn.getNode2()==this )){
                todelete=conn;
                break;
            }
        }
        if(todelete==null)
            return;
        List<DiagramElement> conns=new ArrayList<>(); conns.add(todelete);
        DeleteElementCommand command = new DeleteElementCommand(getDiagram().getEditor(),conns);
        getDiagram().getEditor().execute(command);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNestable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canNestElements() {
        return false;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public void setChangeListeners(Collection<NodeChangeListener> changeListeners) {
        this.changeListeners = changeListeners;
    }

    @Override
    public boolean isConnectionSource() {
        return false;
    }

    @Override
    public boolean isInBack() {
        return false;
    }
}
