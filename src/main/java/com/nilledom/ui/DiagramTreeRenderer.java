package com.nilledom.ui;

import sun.swing.SwingLazyValue;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.nilledom.model.*;
import com.nilledom.umldraw.clazz.ClassDiagram;
import com.nilledom.umldraw.usecase.UseCaseDiagram;

import java.awt.*;

public class DiagramTreeRenderer extends DefaultTreeCellRenderer {

    private static final String ACTOR_ICON = "actorNode.png";
    private static final String USECASE_ICON = "usecaseNode.png";
    private static final String CLASS_ICON = "classNode.png";
    private static final String BOUNDARY_ICON = "boundaryNode.png";
    private static final String CONTROL_ICON = "controlNode.png";
    private static final String ENTITY_ICON = "entityNode.png";
    private static final String USECASE_DIAG_ICON = "usecaseDiagramNode.png";
    private static final String CLASS_DIAG_ICON = "classDiagramNode.png";

    private Icon actorIcon = new ImageIcon(DiagramTreeRenderer.class.getResource(ACTOR_ICON));
    private Icon usecaseIcon = new ImageIcon(DiagramTreeRenderer.class.getResource(USECASE_ICON));
    private Icon classIcon = new ImageIcon(DiagramTreeRenderer.class.getResource(CLASS_ICON));
    private Icon boundaryIcon = new ImageIcon(DiagramTreeRenderer.class.getResource(BOUNDARY_ICON));
    private Icon controlIcon = new ImageIcon(DiagramTreeRenderer.class.getResource(CONTROL_ICON));
    private Icon entityIcon = new ImageIcon(DiagramTreeRenderer.class.getResource(ENTITY_ICON));
    private Icon usecaseDiagramIcon = new ImageIcon(DiagramTreeRenderer.class.getResource(USECASE_DIAG_ICON));
    private Icon classDiagramIcon = new ImageIcon(DiagramTreeRenderer.class.getResource(CLASS_DIAG_ICON));


    @Override
    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected,expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object object = node.getUserObject();
        if (object instanceof UmlActor ) {
            setIcon(actorIcon);
        } else if (object instanceof UmlUseCase) {
            setIcon(usecaseIcon);
        } else if (object instanceof UseCaseDiagram) {
            setIcon(usecaseDiagramIcon);
        } else if (object instanceof UmlBoundary) {
            setIcon(boundaryIcon);
        } else if (object instanceof UmlControl) {
            setIcon(controlIcon);
        } else if (object instanceof UmlEntity) {
            setIcon(entityIcon);
        } else if (object instanceof UmlClass) {
            setIcon(classIcon);
        } else if (object instanceof ClassDiagram) {
            setIcon(classDiagramIcon);
        } else if (object instanceof UmlPackage) {
            setIcon(javax.swing.plaf.metal.MetalIconFactory.getTreeFolderIcon());
        }
        return this;
    }
}
