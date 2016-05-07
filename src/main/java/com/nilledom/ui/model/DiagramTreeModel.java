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
package com.nilledom.ui.model;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.nilledom.model.*;
import com.nilledom.umldraw.clazz.ClassDiagram;
import com.nilledom.umldraw.shared.GeneralDiagram;
import com.nilledom.umldraw.shared.PackageElement;
import com.nilledom.umldraw.shared.UmlConnection;
import com.nilledom.umldraw.shared.UmlDiagramElement;
import com.nilledom.umldraw.usecase.UseCaseDiagram;
import com.nilledom.util.Msg;

/**
 * This class implements a TreeModel to display the diagrams.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class DiagramTreeModel extends DefaultTreeModel
    implements UmlModelListener,PackageListener, NameChangeListener {


    private UmlModel model;
    private DefaultMutableTreeNode classFolder, useCaseFolder, modelFolder;

    /**
     * Constructor.
     */
    public DiagramTreeModel() {
        super(new DefaultMutableTreeNode("Root node"));
        classFolder =
            new DefaultMutableTreeNode(Msg.get("stdcaption.classdiagrams"));
        useCaseFolder = new DefaultMutableTreeNode(Msg.get("stdcaption.usecasediagrams"));
        modelFolder = new DefaultMutableTreeNode(Msg.get("stdcaption.modelfolder"));

        insertNodeInto(useCaseFolder, (DefaultMutableTreeNode) getRoot(), 0);
        insertNodeInto(classFolder, (DefaultMutableTreeNode) getRoot(), 1);
        insertNodeInto(modelFolder, (DefaultMutableTreeNode) getRoot(), 2);
    }


    /**
     * Sets the UmlModel.
     *
     * @param aModel the UmlModel
     */
    public void setModel(UmlModel aModel) {
        cleanupOldStructure();
        buildNewStructure(aModel);
    }

    /**
     * Removes the old structures.
     */
    private void cleanupOldStructure() {
        if (model != null) {
            model.removeModelListener(this);
            for (UmlDiagram diagram : model.getDiagrams()) {
                ((GeneralDiagram) diagram).removeNameChangeListener(this);
            }
        }
        classFolder.removeAllChildren();
        useCaseFolder.removeAllChildren();
        modelFolder.removeAllChildren();
        nodeStructureChanged(classFolder);
        nodeStructureChanged(useCaseFolder);
        nodeStructureChanged(modelFolder);
    }

    /**
     * Build the new tree structure.
     *
     * @param aModel the model
     */
    private void buildNewStructure(UmlModel aModel) {
        model = aModel;
        aModel.addModelListener(this);
        for (UmlDiagram diagram : model.getDiagrams()) {
            insertToFolder(diagram);
            addNameChangeListener((GeneralDiagram) diagram);
        }
        nodeStructureChanged(classFolder);
        nodeStructureChanged(useCaseFolder);
        reload();
    }

    @Override public void elementAdded(UmlModelElement element, UmlDiagram diagram) {
        if(!element.canBeInsertedOnTree())
            return;
        insertToFolder(element, diagram);
        insertToModelFolder(element);
        addNameChangeListener( element);

    }


    private void insertToModelFolder(UmlModelElement element) {
        if(nodeContainsElement(modelFolder,element))
            return;
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(element);
        insertNodeInto(child, modelFolder, modelFolder.getChildCount());
    }



    private void addNameChangeListener(NamedElement element) {
        element.addNameChangeListener(this);
    }

    private void insertToFolder(UmlModelElement element, UmlDiagram diagram) {

        DefaultMutableTreeNode diagramNode = null;
        if (diagram instanceof ClassDiagram) {
            diagramNode = getElementNode(classFolder, diagram);
        } else if (diagram instanceof UseCaseDiagram) {
            diagramNode = getElementNode(useCaseFolder, diagram);
        }

        if( nodeContainsElement(diagramNode,element) )
            return;


        DefaultMutableTreeNode child = new DefaultMutableTreeNode(element);
        insertNodeInto(child, diagramNode, diagramNode.getChildCount());


    }

    private boolean nodeContainsElement(DefaultMutableTreeNode folder, UmlModelElement element) {
        if(folder.isLeaf())
            return false;
        Enumeration e = folder.children();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject().equals(element)) {
                return true;
            }
            if(nodeContainsElement(node,element))
                return true;
        }
        return false;
    }

    private DefaultMutableTreeNode getElementNode(DefaultMutableTreeNode father,
        Object element) {
        for (int i = 0; i < father.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) father.getChildAt(i);
            if (node.getUserObject() == element) {
                return node;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void diagramAdded(UmlDiagram diagram) {
        insertToFolder(diagram);
        addNameChangeListener((GeneralDiagram) diagram);
    }

    /**
     * Inserts the specified diagram to the correct folder.
     *
     * @param diagram the diagram
     */
    private void insertToFolder(UmlDiagram diagram) {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(diagram);
        if (diagram instanceof ClassDiagram) {
            insertNodeInto(child, classFolder, classFolder.getChildCount());
        } else if (diagram instanceof UseCaseDiagram) {
            insertNodeInto(child, useCaseFolder, useCaseFolder.getChildCount());
        }

        for(UmlDiagramElement diagElement : diagram.getElements()){
            if(diagElement.getModelElement()==null || !diagElement.getModelElement().canBeInsertedOnTree())
                continue;
            insertToFolder(diagElement.getModelElement(), diagram);
            insertToModelFolder(diagElement.getModelElement());

        }

        for(UmlDiagramElement diagElement : diagram.getElements()){
            if(diagElement.getModelElement() instanceof PackageableUmlModelElement){
                UmlPackage pkg = ((PackageableUmlModelElement) diagElement.getModelElement()).getPackage();
                if(pkg != null )
                    addToPackage(pkg, (PackageableUmlModelElement) diagElement.getModelElement());
            }

        }

    }

    /**
     * Adds a name change listener to the specified diagram.
     *
     * @param diagram the diagram
     */
    private void addNameChangeListener(GeneralDiagram diagram) {
        diagram.addNameChangeListener(this);
    }



    @Override public void elementRemoved(UmlModelElement element, UmlDiagram diagram) {

        if (diagram == null) {
            removeFromFolder(modelFolder, element);
            return;
        }
        for(UmlDiagramElement diagramElement: diagram.getElements())
            if(diagramElement.getModelElement()!= null && diagramElement.getModelElement().equals(element))
                return; //still exists in the diagram

        DefaultMutableTreeNode diagramNode = null;
        if (diagram instanceof ClassDiagram) {
            diagramNode = getElementNode(classFolder, diagram);
        } else if (diagram instanceof UseCaseDiagram) {
            diagramNode = getElementNode(useCaseFolder, diagram);
        }

        removeFromFolder(diagramNode, element);


    }


    /**
     * {@inheritDoc}
     */
    public void diagramRemoved(UmlDiagram diagram) {
        removeFromFolder(classFolder, diagram);
        removeFromFolder(useCaseFolder, diagram);
    }




    /**
     * Removes the specified element from the folder if it is found.
     *
     * @param folder  the folder
     * @param element the element
     */
    private void removeFromFolder(DefaultMutableTreeNode folder, Object element) {
        if(folder.isLeaf())
            return;
        for (int i = 0; i < folder.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) folder.getChildAt(i);
            if (node.getUserObject() == element) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                removeNodeFromParent(node);
                for(int j=0; j< node.getChildCount();j++){
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(j);
                    insertNodeInto(child, parent, parent.getChildCount());
                }

            }
            removeFromFolder(node,element);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void nameChanged(NamedElement element) {
        searchNodeInFolder(classFolder, element);
        searchNodeInFolder(useCaseFolder, element);
        searchNodeInFolder(modelFolder, element);
    }

    private void searchNodeInFolder(DefaultMutableTreeNode node, NamedElement element) {
        if(node.isLeaf()) {
            if (node.getUserObject() == element) {
                nodeChanged(node);
            }
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) node.getChildAt(i);
            searchNodeInFolder(treenode, element);
            if (node.getUserObject() == element) {
                nodeChanged(node);
            }
        }
    }
    private DefaultMutableTreeNode findNodeInFolder(DefaultMutableTreeNode node, NamedElement element) {
        if(node.isLeaf()) {
            if (node.getUserObject() == element) {
                return node;
            }
            return null;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode found = findNodeInFolder(treenode, element);
            if(found!=null) return  found;
            if (node.getUserObject() == element) {
                return node;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean isLeaf(Object object) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
        if (!(node.getUserObject() instanceof UmlModelElement || node
            .getUserObject() instanceof UmlDiagram)) {
            return false;
        }

        return super.isLeaf(node);
    }

    public TreePath getModelPath() {
        return new TreePath(this.modelFolder.getPath());
    }

    @Override
    public void removeFromPackage(UmlPackage umlPackage, PackageableUmlModelElement packageableUmlModelElement) {
        for (int i = 0; i < classFolder.getChildCount(); i++) {
            DefaultMutableTreeNode diagNode = (DefaultMutableTreeNode) classFolder.getChildAt(i);

            if (nodeContainsElement(diagNode, packageableUmlModelElement)) {
                removeFromFolder(diagNode, packageableUmlModelElement);

                DefaultMutableTreeNode child = new DefaultMutableTreeNode(packageableUmlModelElement);
                insertNodeInto(child, diagNode, diagNode.getChildCount());

            }
        }
        for (int i = 0; i < useCaseFolder.getChildCount(); i++) {
            DefaultMutableTreeNode diagNode = (DefaultMutableTreeNode) useCaseFolder.getChildAt(i);

            if (nodeContainsElement(diagNode, packageableUmlModelElement)) {
                removeFromFolder(diagNode, packageableUmlModelElement);

                DefaultMutableTreeNode child = new DefaultMutableTreeNode(packageableUmlModelElement);
                insertNodeInto(child, diagNode, diagNode.getChildCount());

            }
        }

        if (nodeContainsElement(modelFolder, packageableUmlModelElement)) {
            removeFromFolder(modelFolder, packageableUmlModelElement);

            DefaultMutableTreeNode child = new DefaultMutableTreeNode(packageableUmlModelElement);
            insertNodeInto(child, modelFolder, modelFolder.getChildCount());

        }

    }

    @Override
    public void addToPackage(UmlPackage umlPackage, PackageableUmlModelElement packageableUmlModelElement) {
        for (int i = 0; i < classFolder.getChildCount(); i++) {
            DefaultMutableTreeNode diagNode = (DefaultMutableTreeNode) classFolder.getChildAt(i);
            DefaultMutableTreeNode pkg = findNodeInFolder(diagNode, umlPackage);
            if (pkg!=null) {
                removeFromFolder(diagNode, packageableUmlModelElement);

                DefaultMutableTreeNode child = new DefaultMutableTreeNode(packageableUmlModelElement);
                insertNodeInto(child, pkg, pkg.getChildCount());

            }
        }
        for (int i = 0; i < useCaseFolder.getChildCount(); i++) {
            DefaultMutableTreeNode diagNode = (DefaultMutableTreeNode) useCaseFolder.getChildAt(i);
            DefaultMutableTreeNode pkg = findNodeInFolder(diagNode, umlPackage);
            if (pkg!=null) {
                removeFromFolder(diagNode, packageableUmlModelElement);

                DefaultMutableTreeNode child = new DefaultMutableTreeNode(packageableUmlModelElement);
                insertNodeInto(child, pkg, pkg.getChildCount());

            }
        }

        DefaultMutableTreeNode pkg = findNodeInFolder(modelFolder, umlPackage);
        if (pkg!=null) {
            removeFromFolder(modelFolder, packageableUmlModelElement);

            DefaultMutableTreeNode child = new DefaultMutableTreeNode(packageableUmlModelElement);
            insertNodeInto(child, pkg, pkg.getChildCount());

        }
    }
}
