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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.nilledom.model.AbstractUmlModelElement;
import com.nilledom.model.UmlDiagram;
import com.nilledom.model.UmlModelElement;
import com.nilledom.ui.model.DiagramTreeModel;
import com.nilledom.umldraw.shared.GeneralDiagram;

/**
 * A specialized tree component to display diagrams.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DiagramTree extends JTree implements MouseListener, MouseMotionListener {

    private transient ApplicationState appState;

    private transient Collection<TreeDraggerListener> treeDraggerListener =
        new HashSet<TreeDraggerListener>();

    /**
     * Constructor.
     *
     * @param anAppState the ApplicationState
     * @param treeModel  the tree model
     */
    public DiagramTree(ApplicationState anAppState, DiagramTreeModel treeModel) {
        super(treeModel);
        appState = anAppState;
        addMouseListener(this);
        addMouseMotionListener(this);
        setRootVisible(false);
        setShowsRootHandles(true);
        setCellRenderer(new DiagramTreeRenderer());
    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            TreePath path = getPathForLocation(e.getX(), e.getY());
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.getUserObject() instanceof GeneralDiagram)
                    appState.openExistingEditor((GeneralDiagram) node.getUserObject());

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {

        TreePath path = getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (!((DefaultMutableTreeNode)node.getParent()).isRoot() && !( node.getUserObject() instanceof UmlDiagram )) {
                    for (TreeDraggerListener l : treeDraggerListener) {
                        l.setDraggerElement((AbstractUmlModelElement) node.getUserObject());
                    }
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {
        for (TreeDraggerListener l : treeDraggerListener) {
            l.setReleasePoint(e.getX() + this.getLocationOnScreen().getX(),
                e.getY() + this.getLocationOnScreen().getY());
        }
        for (TreeDraggerListener l : treeDraggerListener) {
            l.setDraggerElement(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void mouseExited(MouseEvent e) {
    }



    public void addTreeDraggerListener(TreeDraggerListener l) {
        treeDraggerListener.add(l);
    }

    public void removeTreeDraggerListener(TreeDraggerListener l) {
        treeDraggerListener.remove(l);
    }

    @Override public void mouseDragged(MouseEvent arg0) {


    }

    @Override public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }




}
