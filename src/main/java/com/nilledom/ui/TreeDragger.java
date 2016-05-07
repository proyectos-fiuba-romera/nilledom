package com.nilledom.ui;

import java.awt.*;
import java.awt.dnd.DragSource;

import javax.swing.*;

import com.nilledom.draw.Node;
import com.nilledom.model.*;
import com.nilledom.ui.diagram.DiagramEditor;
import com.nilledom.umldraw.shared.GeneralDiagram;
import com.nilledom.util.Msg;

public class TreeDragger implements TreeDraggerListener {

    private AbstractUmlModelElement draggerElement;


    @Override public void setDraggerElement(AbstractUmlModelElement element) {
        this.draggerElement = element;
        if(element!=null) {
            AppFrame.get().setCursor(DragSource.DefaultMoveDrop);
            ApplicationState.TREE_DRAGING=true;
        }
    }


    @Override public void setReleasePoint(double x, double y) {
        if (draggerElement == null)
            return;
        AppFrame.get().setCursor(Cursor.getDefaultCursor());
        ApplicationState.TREE_DRAGING=false;
        DiagramEditor currentDiagramEditor = AppFrame.get().getCurrentEditor();
        if(currentDiagramEditor == null)
            return;

        GeneralDiagram diagram = currentDiagramEditor.getDiagram();
        Point origin = currentDiagramEditor.getLocationOnScreen();

        if (isInside(currentDiagramEditor,x,y)) {
            Node element;
            try {
                currentDiagramEditor.setCreationModeFromModel(draggerElement);
                //element = diagram.createNodeFromModel(draggerElement);
                //element.addNodeChangeListener(diagram);
            }catch (IllegalArgumentException e){
                JOptionPane.showMessageDialog(AppFrame.get(), Msg.get("error.dragger.wrongDiagram.message"),
                        Msg.get("error.dragger.wrongDiagram.title"), JOptionPane.ERROR_MESSAGE);
                return;
            }

           // AddNodeCommand createCommand =
             //   new AddNodeCommand(currentDiagramEditor, diagram, element, x - origin.getX(),
                //    y - origin.getY());
               //
            //currentDiagramEditor.execute(createCommand);

        }


    }

    private boolean isInside(DiagramEditor diagramEditor,double x,double y){
        GeneralDiagram diagram = diagramEditor.getDiagram();
        Point origin = diagramEditor.getLocationOnScreen();

        double x1 = diagram.getAbsoluteX1() + origin.getX();
        double x2 = diagram.getAbsoluteX2() + origin.getX();
        double y1 = diagram.getAbsoluteX1() + origin.getY();
        double y2 = diagram.getAbsoluteY2() + origin.getY();



        return x > x1 && x < x2 && y > y1 && y < y2;

    }


}
