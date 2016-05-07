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
package com.nilledom.ui.diagram;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.nilledom.draw.DiagramElement;
import com.nilledom.model.*;
import com.nilledom.ui.AppFrame;
import com.nilledom.umldraw.clazz.ClassElement;
import com.nilledom.umldraw.shared.Association;
import com.nilledom.umldraw.shared.GeneralDiagram;
import com.nilledom.util.MethodCall;

/**
 * This class implements the most common setup for a diagram editor component.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ClassDiagramEditor extends DiagramEditor {


    private static Map<String, MethodCall> selectorMap = new HashMap<String, MethodCall>();

    static {
        initSelectorMap();
    }

    /**
     * Constructor.
     *
     * @param aDiagram the diagram
     */
    public ClassDiagramEditor( GeneralDiagram aDiagram) {
        super( aDiagram);
    }

    /**
     * Initializes the selector map.
     */
    private static void initSelectorMap() {
        try {
            selectorMap.put("CREATE_PACKAGE", new MethodCall(
                ClassDiagramEditor.class.getMethod("setCreationMode", ElementType.class),
                ElementType.PACKAGE));
            selectorMap.put("CREATE_CLASS", new MethodCall(
                ClassDiagramEditor.class.getMethod("setCreationMode", ElementType.class),
                ElementType.CLASS));
            selectorMap.put("CREATE_BOUNDARY", new MethodCall(
                    ClassDiagramEditor.class.getMethod("setCreationMode", ElementType.class),
                    ElementType.BOUNDARY));
            selectorMap.put("CREATE_CONTROL", new MethodCall(
                    ClassDiagramEditor.class.getMethod("setCreationMode", ElementType.class),
                    ElementType.CONTROL));
            selectorMap.put("CREATE_ENTITY", new MethodCall(
                    ClassDiagramEditor.class.getMethod("setCreationMode", ElementType.class),
                    ElementType.ENTITY));
            selectorMap.put("CREATE_DEPENDENCY", new MethodCall(ClassDiagramEditor.class
                .getMethod("setCreateConnectionMode", RelationType.class),
                RelationType.DEPENDENCY));
            selectorMap.put("CREATE_ASSOCIATION", new MethodCall(ClassDiagramEditor.class
                .getMethod("setCreateConnectionMode", RelationType.class),
                RelationType.ASSOCIATION));
            selectorMap.put("CREATE_COMPOSITION", new MethodCall(ClassDiagramEditor.class
                .getMethod("setCreateConnectionMode", RelationType.class),
                RelationType.COMPOSITION));
            selectorMap.put("CREATE_AGGREGATION", new MethodCall(ClassDiagramEditor.class
                .getMethod("setCreateConnectionMode", RelationType.class),
                RelationType.AGGREGATION));
            selectorMap.put("CREATE_INHERITANCE", new MethodCall(ClassDiagramEditor.class
                .getMethod("setCreateConnectionMode", RelationType.class),
                RelationType.INHERITANCE));
            selectorMap.put("CREATE_INTERFACE_REALIZATION", new MethodCall(
                ClassDiagramEditor.class
                    .getMethod("setCreateConnectionMode", RelationType.class),
                RelationType.INTERFACE_REALIZATION));
            selectorMap.put("CREATE_NEST", new MethodCall(ClassDiagramEditor.class
                    .getMethod("setCreateConnectionMode", RelationType.class),
                    RelationType.NEST));

        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void editProperties(DiagramElement element) {
        Window window = ((Window) AppFrame.get()) ;
        if (element instanceof ClassElement) {
            ClassElement classElement = (ClassElement) element;
            UmlClass umlclass = (UmlClass) classElement.getModelElement();
            ClassDialog dialog = new ClassDialog(window, classElement);
            dialog.setLocationRelativeTo(AppFrame.get());
            dialog.setVisible(true);
            if (dialog.isOk()) {
                redraw();
            }
        } else if (element instanceof Association) {
            Association association = (Association) element;
            EditAssociationDialog dialog = new EditAssociationDialog(window, association, true);
            dialog.setLocationRelativeTo(AppFrame.get());
            dialog.setVisible(true);
            redraw();
        }
    }



    /**
     * {@inheritDoc}
     */
    @Override public void handleCommand(String command) {
        MethodCall methodcall = selectorMap.get(command);
        if (methodcall != null)
            methodcall.call(this);
        else
            super.handleCommand(command);
    }
}
