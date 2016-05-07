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
package com.nilledom.ui.diagram.commands;

import javax.swing.undo.AbstractUndoableEdit;

import com.nilledom.draw.*;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.PackageListener;
import com.nilledom.model.PackageableUmlModelElement;
import com.nilledom.model.UmlModelElement;
import com.nilledom.ui.diagram.DiagramEditor;
import com.nilledom.umldraw.shared.UmlNode;
import com.nilledom.util.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A command class to remove elements from a diagram.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DeleteElementCommand extends AbstractUndoableEdit implements Command {

    private Collection<DiagramElement> elements;
    private DiagramEditor editor;
    private List<ParentChildRelation> parentChildRelations = new ArrayList<ParentChildRelation>();


    /**
     * Constructor.
     *
     * @param aNotification the DiagramEditorNotification object
     * @param theElements   the DiagramElements to remove, each must have a parent
     */
    public DeleteElementCommand(DiagramEditor aNotification,
        Collection<DiagramElement> theElements) {
        editor = aNotification;
        elements = theElements;
        for (DiagramElement elem : elements) {
            parentChildRelations.add(new ParentChildRelation(elem, elem.getParent()));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        for (DiagramElement element : elements) {
            if (element instanceof Connection) {
                detachConnectionFromNodes((Connection) element);
            } else if (element instanceof Node) {
                detachNodeConnections((Node) element);
                if(element  instanceof UmlNode){
                    UmlModelElement model = ((UmlNode) element).getModelElement();
                    if(model instanceof PackageableUmlModelElement && element instanceof PackageListener)
                        ((PackageableUmlModelElement) model).removePackageListener((PackageListener) element);
                }
            }


            element.getParent().removeChild(element);
            editor.notifyElementRemoved(element);



            if (element instanceof AbstractCompositeNode){
                editor.execute(new DeleteElementCommand(editor, ((AbstractCompositeNode) element).getChildren()));
            }



        }
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
    @Override public void undo() {
        super.undo();
        for (ParentChildRelation relation : parentChildRelations) {
            if (relation.element instanceof Connection) {
                reattachConnectionToNodes((Connection) relation.element);
            } else if (relation.element instanceof Node) {
                reattachNodeConnections((Node) relation.element);
            }
            relation.parent.addChild(relation.element);
            editor.notifyElementAdded(relation.element);
        }
    }

    /**
     * Called when a node is removed.
     * Detach the connections associated with this node from the other end nodes
     * and remove them from their parents.
     *
     * @param node the node that is removed
     */
    private void detachNodeConnections(Node node) {
        for (Connection conn : node.getConnections()) {
            if (conn.getNode1() != node)
                conn.getNode1().removeConnection(conn);
            if (conn.getNode2() != node)
                conn.getNode2().removeConnection(conn);
            conn.getParent().removeChild(conn);
        }
    }

    /**
     * Called when a remove node operation is undone.
     * Re-attaches the connections associated with the specified node to the
     * former other end nodes and readd them to their parents.
     *
     * @param node the node that is readded
     */
    private void reattachNodeConnections(Node node) {
        try {
            for (Connection conn : node.getConnections()) {
                if (conn.getNode1() != node)
                    conn.getNode1().addConnection(conn);
                if (conn.getNode2() != node)
                    conn.getNode2().addConnection(conn);
                conn.getParent().addChild(conn);
            }
        }catch (AddConnectionException e){
            e.printStackTrace();
        }
    }

    /**
     * Called when a connection is removed.
     * Detaches the connection from its associated nodes, but keeps them in the
     * connection to restore them in the undo operation.
     *
     * @param conn the connection that is removed
     */
    private void detachConnectionFromNodes(Connection conn) {
        conn.getNode1().removeConnection(conn);
        conn.getNode2().removeConnection(conn);
    }

    /**
     * Called when a remove connection operation is undone.
     * Reattaches the connection to its associated nodes.
     *
     * @param conn the connection that is readded
     */
    private void reattachConnectionToNodes(Connection conn) {

        try {
            conn.getNode1().addConnection(conn);
            conn.getNode2().addConnection(conn);


        } catch (AddConnectionException e) {
            e.printStackTrace();
        }
    }


    /**
     * A helper class to store the original parent child relation.
     */
    private static class ParentChildRelation {
        DiagramElement element;
        CompositeNode parent;

        /**
         * Constructor.
         *
         * @param anElement the element
         * @param aParent   the element's parent
         */
        public ParentChildRelation(DiagramElement anElement, CompositeNode aParent) {
            parent = aParent;
            element = anElement;
        }
    }
}
