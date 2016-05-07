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
package com.nilledom.umldraw.clazz;

import java.util.HashMap;
import java.util.Map;

import com.nilledom.model.ElementType;
import com.nilledom.model.Relation;
import com.nilledom.model.RelationType;
import com.nilledom.model.UmlBoundary;
import com.nilledom.model.UmlClass;
import com.nilledom.model.UmlControl;
import com.nilledom.model.UmlEntity;
import com.nilledom.model.UmlModel;
import com.nilledom.model.UmlPackage;
import com.nilledom.model.UmlRelation;
import com.nilledom.umldraw.shared.Association;
import com.nilledom.umldraw.shared.Association.AssociationType;
import com.nilledom.umldraw.shared.GeneralDiagram;
import com.nilledom.umldraw.shared.Inheritance;
import com.nilledom.umldraw.shared.Nest;
import com.nilledom.umldraw.shared.NoteConnection;
import com.nilledom.umldraw.shared.NoteElement;
import com.nilledom.umldraw.shared.PackageElement;
import com.nilledom.umldraw.shared.RectilinearAssociation;
import com.nilledom.umldraw.shared.SimpleAssociation;
import com.nilledom.umldraw.shared.UmlConnection;
import com.nilledom.umldraw.shared.UmlDiagramElement;

/**
 * This class specializes the GeneralDiagram, at the same time it implements the
 * DiagramElementFactory interface, as an efficient method to share the state of factory and
 * diagram.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class ClassDiagram extends GeneralDiagram {

    /**
     * Constructor.
     *
     * @param umlModel the UmlModel
     */
    public ClassDiagram(UmlModel umlModel) {
        super(umlModel);
    }

    /**
     * {@inheritDoc}
     */
    protected Map<ElementType, UmlDiagramElement> setupElementPrototypeMap() {
        Map<ElementType, UmlDiagramElement> elementPrototypes =
            new HashMap<ElementType, UmlDiagramElement>();

        NoteElement notePrototype = (NoteElement) NoteElement.getPrototype().clone();
        elementPrototypes.put(ElementType.NOTE, notePrototype);

        // Add package prototype
        UmlPackage pkg = (UmlPackage) UmlPackage.getPrototype().clone();
        PackageElement pkgPrototype = (PackageElement) PackageElement.getPrototype().clone();
        pkgPrototype.setModelElement(pkg);
        elementPrototypes.put(ElementType.PACKAGE, pkgPrototype);


        // Add class prototype
        UmlClass clss = (UmlClass) UmlClass.getPrototype().clone();
        ClassElement classElem = (ClassElement) ClassElement.getPrototype().clone();
        classElem.setModelElement(clss);
        classElem.addNodeChangeListener(this);
        elementPrototypes.put(ElementType.CLASS, classElem);

        // Add boundary prototype
        UmlBoundary boundary = (UmlBoundary) UmlBoundary.getPrototype().clone();
        ClassElement boundaryElem = (ClassElement) ClassElement.getPrototype().clone();
        boundaryElem.setModelElement(boundary);
        boundaryElem.addNodeChangeListener(this);
        elementPrototypes.put(ElementType.BOUNDARY, boundaryElem);

        // Add control prototype
        UmlControl control = (UmlControl) UmlControl.getPrototype().clone();
        ClassElement controlElem = (ClassElement) ClassElement.getPrototype().clone();
        controlElem.setModelElement(control);
        controlElem.addNodeChangeListener(this);
        elementPrototypes.put(ElementType.CONTROL, controlElem);

        // Add entity prototype
        UmlEntity entity = (UmlEntity) UmlEntity.getPrototype().clone();
        ClassElement entityElem = (ClassElement) ClassElement.getPrototype().clone();
        entityElem.setModelElement(entity);
        entityElem.addNodeChangeListener(this);
        elementPrototypes.put(ElementType.ENTITY, entityElem);

        return elementPrototypes;
    }

    /**
     * {@inheritDoc}
     */
    protected Map<RelationType, UmlConnection> setupConnectionPrototypeMap() {
        Map<RelationType, UmlConnection> connectionPrototypes =
            new HashMap<RelationType, UmlConnection>();

        UmlRelation notnavigable = new UmlRelation();
        notnavigable.setCanSetElement1Navigability(false);
        notnavigable.setCanSetElement2Navigability(false);
        UmlRelation fullnavigable = new UmlRelation();
        fullnavigable.setCanSetElement1Navigability(true);
        fullnavigable.setCanSetElement2Navigability(true);
        UmlRelation targetnavigable = new UmlRelation();
        targetnavigable.setCanSetElement1Navigability(false);
        targetnavigable.setCanSetElement2Navigability(true);

        Dependency depPrototype = (Dependency) Dependency.getPrototype().clone();
        depPrototype.setRelation((Relation) notnavigable.clone());
        connectionPrototypes.put(RelationType.DEPENDENCY, depPrototype);

        Association assocPrototype = (Association) SimpleAssociation.getPrototype().clone();
        assocPrototype.setRelation((Relation) fullnavigable.clone());
        connectionPrototypes.put(RelationType.ASSOCIATION, assocPrototype);

        Association compPrototype = (Association) RectilinearAssociation.getPrototype().clone();
        compPrototype.setAssociationType(AssociationType.COMPOSITION);
        compPrototype.setRelation((Relation) targetnavigable.clone());
        connectionPrototypes.put(RelationType.COMPOSITION, compPrototype);

        Association aggrPrototype = (Association) RectilinearAssociation.getPrototype().clone();
        aggrPrototype.setAssociationType(AssociationType.AGGREGATION);
        aggrPrototype.setRelation((Relation) targetnavigable.clone());
        connectionPrototypes.put(RelationType.AGGREGATION, aggrPrototype);

        Inheritance inheritPrototype = (Inheritance) Inheritance.getPrototype().clone();
        connectionPrototypes.put(RelationType.INHERITANCE, inheritPrototype);

        Inheritance interfRealPrototype = (Inheritance) Inheritance.getPrototype().clone();
        interfRealPrototype.setRelation((Relation) notnavigable.clone());
        interfRealPrototype.setIsDashed(true);
        connectionPrototypes.put(RelationType.INTERFACE_REALIZATION, interfRealPrototype);

        connectionPrototypes.put(RelationType.NOTE_CONNECTOR, NoteConnection.getPrototype());

        Nest nestPrototype = (Nest) Nest.getPrototype().clone();
        connectionPrototypes.put(RelationType.NEST, nestPrototype);

        return connectionPrototypes;
    }
}
