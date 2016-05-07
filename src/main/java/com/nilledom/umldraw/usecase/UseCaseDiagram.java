package com.nilledom.umldraw.usecase;

import java.util.HashMap;
import java.util.Map;

import com.nilledom.model.*;
import com.nilledom.umldraw.shared.*;

/**
 * This class specializes on GeneralDiagram, providing the elements available in a use case diagram.
 *
 * @author Juan Manuel Romera
 * @version 2.0
 */
public class UseCaseDiagram extends GeneralDiagram {


    public UseCaseDiagram() {
    }

    /**
     * Constructor.
     *
     * @param umlModel the Uml model
     */
    public UseCaseDiagram(UmlModel umlModel) {
        super(umlModel);
    }

    /**
     * {@inheritDoc}
     */
    protected Map<ElementType, UmlDiagramElement> setupElementPrototypeMap() {
        Map<ElementType, UmlDiagramElement> elementPrototypes =
            new HashMap<ElementType, UmlDiagramElement>();

        // Add a note
        NoteElement notePrototype = (NoteElement) NoteElement.getPrototype().clone();
        elementPrototypes.put(ElementType.NOTE, notePrototype);

        // Add actor prototype
        UmlActor actor = (UmlActor) UmlActor.getPrototype().clone();
        ActorElement actorPrototype = (ActorElement) ActorElement.getPrototype().clone();
        actorPrototype.setModelElement(actor);
        elementPrototypes.put(ElementType.ACTOR, actorPrototype);

        // Add useCase prototype
        UmlUseCase useCase = (UmlUseCase) UmlUseCase.getPrototype().clone();
        UseCaseElement useCasePrototype = (UseCaseElement) UseCaseElement.getPrototype().clone();
        useCasePrototype.setModelElement(useCase);
        elementPrototypes.put(ElementType.USE_CASE, useCasePrototype);

        // Add package prototype
        UmlPackage pkg = (UmlPackage) UmlPackage.getPrototype().clone();
        PackageElement pkgPrototype = (PackageElement) PackageElement.getPrototype().clone();
        pkgPrototype.setModelElement(pkg);
        elementPrototypes.put(ElementType.PACKAGE, pkgPrototype);


        SystemElement sytemElement = (SystemElement) SystemElement.getPrototype().clone();
        elementPrototypes.put(ElementType.SYSTEM, sytemElement);

        return elementPrototypes;
    }

    /**
     * {@inheritDoc}
     */
    protected Map<RelationType, UmlConnection> setupConnectionPrototypeMap() {
        Map<RelationType, UmlConnection> connectionPrototypes =
            new HashMap<RelationType, UmlConnection>();

        UmlRelation fullnavigable = new UmlRelation();
        fullnavigable.setCanSetElement1Navigability(true);
        fullnavigable.setCanSetElement2Navigability(true);

        UmlRelation notnavigable = new UmlRelation();
        notnavigable.setCanSetElement1Navigability(false);
        notnavigable.setCanSetElement2Navigability(false);

        UmlRelation targetnavigable = new UmlRelation();
        targetnavigable.setCanSetElement1Navigability(false);
        targetnavigable.setCanSetElement2Navigability(true);

        Association assocPrototype = (Association) SimpleAssociation.getPrototype().clone();
        assocPrototype.setRelation((Relation) fullnavigable.clone());
        connectionPrototypes.put(RelationType.ASSOCIATION, assocPrototype);


        Inheritance inheritPrototype = (Inheritance) Inheritance.getPrototype().clone();
        connectionPrototypes.put(RelationType.INHERITANCE, inheritPrototype);

        Extend extendPrototype = (Extend) Extend.getPrototype().clone();
        connectionPrototypes.put(RelationType.EXTEND, extendPrototype);

        Include includePrototype = (Include) Include.getPrototype().clone();
        connectionPrototypes.put(RelationType.INCLUDE, includePrototype);

        Nest nestPrototype = (Nest) Nest.getPrototype().clone();
        connectionPrototypes.put(RelationType.NEST, nestPrototype);


        connectionPrototypes.put(RelationType.NOTE_CONNECTOR, NoteConnection.getPrototype());

        return connectionPrototypes;
    }
}
