package com.nilledom.conversion.impl;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

import com.nilledom.conversion.ClassDiagramBuilder;
import com.nilledom.conversion.model.*;
import com.nilledom.draw.DoubleDimension;
import com.nilledom.draw.DrawingContext;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.*;
import com.nilledom.ui.AppFrame;
import com.nilledom.ui.ApplicationState;
import com.nilledom.ui.diagram.ClassDiagramEditor;
import com.nilledom.ui.diagram.ElementInserter;
import com.nilledom.umldraw.clazz.ClassDiagram;
import com.nilledom.umldraw.clazz.ClassElement;
import com.nilledom.umldraw.shared.UmlNode;
import com.nilledom.util.Msg;

public class ClassDiagramBuilderImpl implements ClassDiagramBuilder {

    private static final String CREATE_BOUNDARY = Msg.get("classtoolbar.boundary.command");
    private static final String CREATE_CONTROL = Msg.get("classtoolbar.control.command");
    private static final String CREATE_ENTITY = Msg.get("classtoolbar.entity.command");
    private static final double V_MARGIN = 10;
    private static final double H_MARGIN = 250;
    private static final double DIAGRAM_H_MARGIN = 30;
    private static final double DIAGRAM_V_MARGIN = 20;

    @Override
    public void buildClassDiagram(Map<Class<? extends UmlClass>, List<UmlClass>> classModel, String diagramName, ConversionModel conversionModel, ConverterImpl.ConversionTask conversionTask) {
        ApplicationState appState = AppFrame.get().getAppState();
        appState.openNewClassEditor();
        ClassDiagramEditor diagramEditor = (ClassDiagramEditor) appState.getCurrentEditor();
        ClassDiagram diagram = (ClassDiagram) diagramEditor.getDiagram();
        diagram.setName(diagramName);

        List<ClassElement> controls = toElements(classModel,conversionModel.getControls(),diagramEditor);
        List<ClassElement> entities = toElements(classModel,conversionModel.getEntities(),diagramEditor);
        List<ClassElement> boundaries = toElements(classModel,conversionModel.getBoundaries(),diagramEditor);
        Map<ClassElement,List<ClassElement>> controlEntitiesMap = new HashMap<>();
        Map<ClassElement,List<ClassElement>> controlBoundariesMap = new HashMap<>();
        Map<ClassElement,List<ClassElement>> controlControlsMap = new HashMap<>();

        initControlMaps(controlBoundariesMap,controlEntitiesMap,controlControlsMap,controls,entities,boundaries,conversionModel.getRelations());
        controls = sortControls(controls,conversionModel.getRelations(),controlControlsMap);

        conversionTask.step1();

        Map<ClassElement,Point2D> positions = calculatePositions(controlBoundariesMap,controlControlsMap,controlEntitiesMap,controls);

        conversionTask.step2();

        for(ClassElement element : positions.keySet()){
            Point2D pos = positions.get(element);
            ElementInserter.insert(element,diagramEditor,pos);
        }
        for(SimpleRelation relation:conversionModel.getRelations()){
            UmlNode source;
            if (relation.getClass1() instanceof Control)
                source = findElement(controls,relation.getClass1().getName());
            else if (relation.getClass1() instanceof Boundary)
                source = findElement(boundaries,relation.getClass1().getName());
            else
                source = findElement(entities,relation.getClass1().getName());

            ClassElement elem;
            if (relation.getClass2() instanceof Control)
                elem = findElement(controls,relation.getClass2().getName());
            else if (relation.getClass1() instanceof Boundary)
                elem = findElement(boundaries,relation.getClass2().getName());
            else
                elem = findElement(entities,relation.getClass2().getName());

            try {
                ElementInserter.insertConnection(source,elem,diagramEditor,RelationType.ASSOCIATION,new Point2D.Double(source.getAbsCenterX(),source.getAbsCenterY()),new Point2D.Double(elem.getAbsCenterX(),elem.getAbsCenterY()));
                conversionTask.step3(conversionModel.getRelations().size());
            } catch (AddConnectionException e) {
                    e.printStackTrace();
            }
        }

    }

    private List<ClassElement> sortControls(List<ClassElement> controls, final Set<SimpleRelation> relations, Map<ClassElement, List<ClassElement>> controlControlsMap) {
        Collections.sort(controls, new Comparator<ClassElement>() {
            @Override
            public int compare(ClassElement c1, ClassElement c2) {
                int c1Relations = 0;
                int c2Relations = 0;
                for(SimpleRelation relation: relations){
                    if(relation.isControlControl()){
                        if(c1.getLabelText().equals(relation.getClass1().getName()) ||
                                c1.getLabelText().equals(relation.getClass2().getName()))
                            c1Relations++;
                        if(c2.getLabelText().equals(relation.getClass1().getName()) ||
                                c2.getLabelText().equals(relation.getClass2().getName()))
                            c2Relations++;
                    }
                }

                return c2Relations-c1Relations;
            }
        });
        Map<ClassElement, List<ClassElement>> controlControlsTree = new HashMap<>();
        for(ClassElement control :controls) {
            controlControlsTree.put(control,new ArrayList<ClassElement>());
            for (ClassElement relatedControl : controlControlsMap.get(control)) {
                if (controlControlsTree.containsKey(relatedControl) &&
                        controlControlsTree.get(relatedControl).contains(control))
                    continue;
                controlControlsTree.get(control).add(relatedControl);
            }
        }
        List<ClassElement> sortedControls = new ArrayList<>();
        if(controls.isEmpty())
            return sortedControls;
        Stack<ClassElement> current = new Stack<>();
        Stack<Integer> currentIndex = new Stack<>();
        currentIndex.push(0);
        int controlIndex =0;
        while(sortedControls.size()!=controls.size()){
            if(current.isEmpty()) {
                while(sortedControls.contains(controls.get(controlIndex)))
                    controlIndex++;
                current.push(controls.get(controlIndex));
            }
            if(currentIndex.peek()==1 || controlControlsTree.get(current.peek()).isEmpty())
                sortedControls.add(current.peek());
            if(controlControlsTree.get(current.peek()).size()==currentIndex.peek()) {
                current.pop();
                currentIndex.pop();
                if(currentIndex.isEmpty())
                    currentIndex.push(0);
                else
                    currentIndex.push(currentIndex.pop()+1);
            }else{

                current.push(controlControlsTree.get(current.peek()).get(currentIndex.peek()));
                currentIndex.push(0);
            }
        }

        return sortedControls;
    }

    private void initControlMaps(Map<ClassElement, List<ClassElement>> controlBoundariesMap,
                                 Map<ClassElement, List<ClassElement>> controlEntitiesMap,
                                 Map<ClassElement, List<ClassElement>> controlControlsMap,
                                 List<ClassElement> controls,
                                 List<ClassElement> entities,
                                 List<ClassElement> boundaries,
                                 final Set<SimpleRelation> relations) {



        for(ClassElement control:controls){
            controlBoundariesMap.put(control,new ArrayList<ClassElement>());
            List<ClassElement> boundaryList = controlBoundariesMap.get(control);
            controlEntitiesMap.put(control,new ArrayList<ClassElement>());
            List<ClassElement> entityList = controlEntitiesMap.get(control);
            controlControlsMap.put(control,new ArrayList<ClassElement>());
            List<ClassElement> controlList = controlControlsMap.get(control);
            for(SimpleRelation relation : relations) {
                if(!control.getLabelText().equals(relation.getClass1().getName()) &&
                        !control.getLabelText().equals(relation.getClass2().getName())) {
                    continue;
                }SimpleClass theRelated;
                if(control.getLabelText().equals(relation.getClass1().getName()) ){
                    theRelated=relation.getClass2();
                }else{
                    theRelated=relation.getClass1();
                }

                if (theRelated instanceof Boundary)
                    boundaryList.add(findElement(boundaries, theRelated.getName()));
                else if(theRelated instanceof Entity)
                    entityList.add(findElement(entities, theRelated.getName()));
                else
                    controlList.add(findElement(controls,theRelated.getName()));
            }
        }

    }

    private List<ClassElement> toElements(Map<Class<? extends UmlClass>,List<UmlClass>> classModel,Set<? extends SimpleClass> simpleClasses, ClassDiagramEditor diagramEditor) {
        ClassDiagram diagram = (ClassDiagram) diagramEditor.getDiagram();
        List<ClassElement> elements = new ArrayList<>();
        for(SimpleClass simpleClass : simpleClasses){
            UmlClass umlClass = null;
            if(simpleClass instanceof  Boundary) {
                for(UmlClass umlBoundary : classModel.get(UmlBoundary.class))
                    if(umlBoundary.getName().equals(simpleClass.getName()))
                        umlClass=umlBoundary;
            }else if (simpleClass instanceof Control) {
                for(UmlClass umlControl : classModel.get(UmlControl.class))
                    if(umlControl.getName().equals(simpleClass.getName()))
                        umlClass=umlControl;
            }else {
                for(UmlClass umlEntity : classModel.get(UmlEntity.class))
                    if(umlEntity.getName().equals(simpleClass.getName()))
                        umlClass=umlEntity;
            }


            ClassElement element = (ClassElement) diagram.getElementFactory().createNodeFromModel(umlClass);
            ArrayList<Boolean> methodVisibility = new ArrayList<>();
            for(UmlMethod method : umlClass.getMethods()){
                if(simpleClass.getMethods().contains(method.getName()))
                    methodVisibility.add(true);
                else
                    methodVisibility.add(false);
            }
            element.setMethodVisibility(methodVisibility);
            element.setParent(diagram);
            DrawingContext drawingContext = diagramEditor.getDrawingContext();
            Rectangle clipBounds = new Rectangle();
            Graphics g = AppFrame.get().getGraphics();
            g.getClipBounds(clipBounds);
            drawingContext.setGraphics2D((Graphics2D) g,clipBounds );
            element.recalculateSize(drawingContext);

            elements.add(element);
        }
        return elements;
    }

    private Map<ClassElement,Point2D> calculatePositions(Map<ClassElement,List<ClassElement>> boundaryMap,
                                                         Map<ClassElement,List<ClassElement>> controlMap,
                                                         Map<ClassElement,List<ClassElement>> entityMap,
                                                         List<ClassElement> controls){

        Map<ClassElement,Point2D> positions = new HashMap<>();

        Point2D boundaryOffset = new Point2D.Double(DIAGRAM_H_MARGIN,DIAGRAM_V_MARGIN);
        Point2D controlOffset;
        Point2D entityOffset;

        Set<ClassElement> allEntities = new HashSet<>();
        Set<ClassElement> allBoundaries = new HashSet<>();
        for(ClassElement control : controls) {
            List<ClassElement> boundaries = boundaryMap.get(control);
            allBoundaries.addAll(boundaries);
            List<ClassElement> entities = entityMap.get(control);
            allEntities.addAll(entities);
        }
        Dimension2D allBoundaryDimension = calculateDimension(allBoundaries);
        controlOffset = new Point2D.Double(boundaryOffset.getX()+allBoundaryDimension.getWidth()+H_MARGIN,DIAGRAM_V_MARGIN);
        Dimension2D allControlDimension = calculateDimension(controls);
        entityOffset = new Point2D.Double (controlOffset.getX()+allControlDimension.getWidth() + H_MARGIN,DIAGRAM_V_MARGIN);
        Dimension2D allEntitiesDimension = calculateDimension(allEntities);
        for(ClassElement control : controls){
            List<ClassElement> boundariesRelated = boundaryMap.get(control);
            List<ClassElement> entitiesRelated = entityMap.get(control);
            List<ClassElement> controlsRelated = controlMap.get(control);
            //Remove the elements that have already been positioned
            for(ClassElement element : positions.keySet()){
                boundariesRelated.remove(element);
                entitiesRelated.remove(element);
                controlsRelated.remove(element);
            }
            Dimension2D controlDimension = control.getSize();
            Dimension2D boundariesDimension = calculateDimension(boundariesRelated);
            Dimension2D entitiesDimension = calculateDimension(entitiesRelated);
            Dimension2D groupDimension = new DoubleDimension(0,0);
            double maxHeight = controlDimension.getHeight();
            if(boundariesDimension.getHeight() > maxHeight)
                maxHeight = boundariesDimension.getHeight();
            if(entitiesDimension.getHeight() > maxHeight)
                maxHeight = entitiesDimension.getHeight();
            groupDimension.setSize( boundariesDimension.getWidth() +
                            controlDimension.getWidth() + entitiesDimension.getWidth()+ 2*H_MARGIN,
                    maxHeight);
            double boundaryVPadding = (maxHeight - boundariesDimension.getHeight() )/2.0;
            boundaryOffset.setLocation(boundaryOffset.getX(),boundaryOffset.getY()+boundaryVPadding);
            double controlVPadding = (maxHeight - controlDimension.getHeight() )/2.0;
            controlOffset.setLocation(controlOffset.getX(),controlOffset.getY()+controlVPadding);
            double entityVPadding = (maxHeight - entitiesDimension.getHeight() )/2.0;
            entityOffset.setLocation(entityOffset.getX(),entityOffset.getY()+entityVPadding);

            ////// Setting control position
            Point2D controlPos = (Point2D) controlOffset.clone();
            double controlHPadding =  (controlDimension.getWidth() - allControlDimension.getWidth())/2.0;
            controlPos.setLocation(controlPos.getX()+controlHPadding,controlPos.getY());
            positions.put(control, controlPos);
            controlOffset.setLocation(controlOffset.getX(),controlOffset.getY()+controlDimension.getHeight()+controlVPadding+V_MARGIN);

            ////// Setting boundaries position
            for(ClassElement boundary: boundariesRelated){
                Point2D boundaryPos = (Point2D) boundaryOffset.clone();
                double boundaryHPadding =  (boundary.getSize().getWidth() - allBoundaryDimension.getWidth())/2.0;
                boundaryPos.setLocation(boundaryPos.getX()+boundaryHPadding,boundaryPos.getY());
                positions.put(boundary, boundaryPos);
                boundaryOffset.setLocation(boundaryOffset.getX(),
                        boundaryOffset.getY()+boundary.getSize().getHeight()+V_MARGIN);
            }
            if(!boundariesRelated.isEmpty())
                boundaryOffset.setLocation(boundaryOffset.getX(),boundaryOffset.getY() - V_MARGIN);

            boundaryOffset.setLocation(boundaryOffset.getX(),boundaryOffset.getY()+boundaryVPadding+V_MARGIN);

            ////// Setting entities position
            for(ClassElement entity: entitiesRelated){
                Point2D entityPos = (Point2D) entityOffset.clone();
                double entityHPadding =  (entity.getSize().getWidth() - allEntitiesDimension.getWidth())/2.0;
                entityPos.setLocation(entityPos.getX()+entityHPadding,entityPos.getY());
                positions.put(entity, entityPos);
                entityOffset.setLocation(entityOffset.getX(),
                        entityOffset.getY()+entity.getSize().getHeight()+V_MARGIN);
            }
            if(!entitiesRelated.isEmpty())
                entityOffset.setLocation(entityOffset.getX(),entityOffset.getY() - V_MARGIN);

            entityOffset.setLocation(entityOffset.getX(),entityOffset.getY()+entityVPadding+V_MARGIN);

        }
        return  positions;
    }

    private ClassElement findElement(List<ClassElement> elements, String name) {
        for(ClassElement element : elements){
            if(element.getLabelText().equals(name))
                return element;
        }
        return null;
    }

    private Dimension2D calculateDimension(Collection<ClassElement> elements) {
        Dimension2D dimension = new DoubleDimension(0,0);
        if(elements.isEmpty())
            return dimension;
        for(ClassElement element : elements){
            double elementWidth = element.getSize().getWidth();
            double elementHeight = element.getSize().getHeight();
            double dimensionWidth = dimension.getWidth();
            double dimensionHeight = dimension.getHeight();

            if(elementWidth > dimensionWidth)
                dimension.setSize(elementWidth,dimensionHeight);

            dimension.setSize(dimension.getWidth(),dimensionHeight + elementHeight + V_MARGIN);

        }

        dimension.setSize(dimension.getWidth(),dimension.getHeight()-V_MARGIN);
        return dimension;
    }
}
