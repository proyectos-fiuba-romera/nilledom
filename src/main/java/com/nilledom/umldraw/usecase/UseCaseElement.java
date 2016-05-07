package com.nilledom.umldraw.usecase;

import java.awt.*;
import java.awt.geom.Dimension2D;

import com.nilledom.draw.*;
import com.nilledom.draw.DrawingContext.FontType;
import com.nilledom.draw.Label;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.*;
import com.nilledom.umldraw.shared.*;
import com.nilledom.util.Msg;

/**
 * This class represents a UseCase element in the editor. It is responsible for rendering the
 * information in the editor.
 *
 * @author Juan Manuel Romera
 * @version 1.0
 */
public final class UseCaseElement extends AbstractCompositeNode
    implements LabelSource, UmlModelElementListener,PackageListener,UmlNode {

    private static final Color BACKGROUND = Color.WHITE;
    private static final double DEFAULT_HEIGHT = 40;
    private static final double DEFAULT_WIDHT = 70;
    private static UseCaseElement prototype;
    private UmlUseCase useCase;
    private Label label;
    private Compartment mainCompartment;

    /**
     * Private constructor.
     */
    private UseCaseElement() {
        mainCompartment = new EllipseCompartment();
        mainCompartment.setParent(this);
        mainCompartment.setBackground(BACKGROUND);
        label = new SimpleLabel();
        label.setParent(this);
        label.setSource(this);
        label.setFontType(FontType.ELEMENT_NAME);
        mainCompartment.addLabel(label);

        setSize(DEFAULT_WIDHT, DEFAULT_HEIGHT);
        mainCompartment.setSize(getSize().getWidth(), getSize().getHeight());

    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static UseCaseElement getPrototype() {
        if (prototype == null)
            prototype = new UseCaseElement();
        return prototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override public Object clone() {
        UseCaseElement cloned = (UseCaseElement) super.clone();
        cloned.label = (Label) label.clone();
        cloned.label.setSource(cloned);
        cloned.label.setParent(cloned);

        cloned.mainCompartment = (Compartment) mainCompartment.clone();
        cloned.mainCompartment.setParent(cloned);
        cloned.mainCompartment.removeAllLabels();
        cloned.mainCompartment.addLabel(cloned.label);


        if (useCase != null) {
            cloned.useCase = (UmlUseCase) useCase.clone();
        }

        return cloned;

    }


    @Override
    public boolean isEditable() {
        return true;
    }


    /**
     * Returns the main label for testing purposes.
     *
     * @return the main label
     */
    public Label getMainLabel() {
        return label;
    }

    /**
     * {@inheritDoc}
     */
    public UmlModelElement getModelElement() {
        return useCase;
    }

    @Override
    public void setModelElement(UmlModelElement model) {
        if(model instanceof UmlUseCase)
            this.useCase = (UmlUseCase) model;
        else throw new IllegalArgumentException("UmlUseCase expected");
    }

    /**
     * {@inheritDoc}
     */
    public String getLabelText() {
        return getModelElement().getName();
    }

    /**
     * {@inheritDoc}
     */
    public void setLabelText(String aText) {
        getModelElement().setName(aText);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void recalculateSize(DrawingContext drawingContext) {
        mainCompartment.recalculateSize(drawingContext);
        setSize(new DoubleDimension(Math.max(super.getSize().getWidth(),mainCompartment.getSize().getWidth()),getSize().getHeight()));
        mainCompartment.setSize(new DoubleDimension(getSize().getWidth(),getSize().getHeight()));
        notifyNodeResized();
    }

    /**
     * {@inheritDoc}
     */
    @Override public void draw(DrawingContext drawingContext) {
        if (!isValid()) {
            recalculateSize(drawingContext);
        }
        mainCompartment.draw(drawingContext);

    }


    /**
     * {@inheritDoc}
     */
    @Override public Dimension2D getMinimumSize() {
        Dimension2D minMainSize = mainCompartment.getMinimumSize();
        return minMainSize;
    }


    /**
     * {@inheritDoc}
     */
    @Override public void setMinimumSize(double width, double height) {
        throw new UnsupportedOperationException("setMinimumSize() not supported");
    }


    /**
     * {@inheritDoc}
     */
    @Override public boolean isValid() {
        return mainCompartment.isValid();
    }

    /**
     * {@inheritDoc}
     */
    @Override public void invalidate() {
        mainCompartment.invalidate();
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setSize(double width, double height) {
        mainCompartment.setSize(width, height);
        super.setSize(width,height);
        invalidate();
    }

    @Override public Label getLabelAt(double mx, double my) {
        if (inLabelArea(mx, my))
            return label;
        return null;
    }

    private boolean inLabelArea(double mx, double my) {
        double horizontalMargin = (getSize().getWidth() - label.getSize().getWidth()) / 2;
        double verticalMargin = (getSize().getHeight() - label.getSize().getHeight()) / 2;

        double labelX1 = getAbsoluteX1() + horizontalMargin;
        double labelX2 = getAbsoluteX2() - horizontalMargin;
        double labelY1 = getAbsoluteY1() + verticalMargin;
        double labelY2 = getAbsoluteY2() - verticalMargin;

        return mx >= labelX1 && mx <= labelX2 && my >= labelY1 && my <= labelY2;
    }


    @Override public void elementChanged(UmlModelElement element) {
        // TODO Auto-generated method stub

    }


    @Override
    public void addConcreteConnection(Nest nest)  {
        useCase.setPackageRelation((NestRelation) nest.getModelElement());
    }


    @Override
    public void addConcreteConnection(Association association)  {
        Relation relation = (Relation) association.getModelElement();

        UmlModelElement element1 = relation.getElement1();
        UmlModelElement element2 = relation.getElement2();

        if (element1 != this.useCase)
            this.useCase.addUmlActor((UmlActor) element1);
        else
            this.useCase.addUmlActor((UmlActor) element2);

    }

    @Override
    public void addConcreteConnection(Extend extend)  {
        Relation relation = (Relation) extend.getModelElement();

        UmlModelElement element1 = relation.getElement1();

        if(element1==this.useCase)
            this.useCase.addExtend((ExtendRelation) extend.getModelElement());

    }

    @Override
    public void addConcreteConnection(Include include)  {
        Relation relation = (Relation) include.getModelElement();

        UmlModelElement element1 = relation.getElement1();

        if(element1==this.useCase )
            this.useCase.addInclude((IncludeRelation) include.getModelElement());

    }

    @Override public void removeConcreteConnection(Include include) {

        Relation relation = (Relation) include.getModelElement();

        UmlModelElement element1 = relation.getElement1();
        if(this.useCase==element1)
            useCase.removeInclude((IncludeRelation) include.getModelElement());

    }
    @Override public void removeConcreteConnection(Extend extend) {

        Relation relation = (Relation) extend.getModelElement();

        UmlModelElement element1 = relation.getElement1();
        if(this.useCase==element1)
            useCase.removeExtend((ExtendRelation) extend.getModelElement());
    }


    @Override public void removeConcreteConnection(Association association) {

        Relation relation = (Relation) association.getModelElement();

        UmlModelElement element1 = relation.getElement1();
        UmlModelElement element2 = relation.getElement2();

        if (element1 != this.useCase)
            this.useCase.removeUmlActor((UmlActor) element1);
        else
            this.useCase.removeUmlActor((UmlActor) element2);


    }

    @Override
    public boolean acceptsConnectionAsSource(RelationType relationType) {
        switch(relationType){
            case ASSOCIATION:
            case EXTEND:
            case INCLUDE:
            case NOTE_CONNECTOR:
                return true;
            default: return false;
        }
    }
    @Override
    public void validateConnectionAsTarget(RelationType relationType,UmlNode source) throws AddConnectionException {
        if(!source.acceptsConnectionAsSource(relationType))
            throw new AddConnectionException(Msg.get("error.connection.invalidSource"));
        switch(relationType){
            case ASSOCIATION:
                if(source instanceof ActorElement)
                    break;
                throw new AddConnectionException(Msg.get("error.connection.usecase.association.withoutactor"));
            case EXTEND:
                if(!(source instanceof UseCaseElement))
                    throw new AddConnectionException(Msg.get("error.connection.usecase.extend.withoutUsecase"));
                if(source.getModelElement() != getModelElement())
                    break;
                throw new AddConnectionException(Msg.get("error.connection.usecase.extend.selfreferential"));

            case INCLUDE:
                if(!(source instanceof UseCaseElement))
                    throw new AddConnectionException(Msg.get("error.connection.usecase.include.withoutUsecase"));
                if(source.getModelElement() != getModelElement())
                    break;
                throw new AddConnectionException(Msg.get("error.connection.usecase.include.selfreferential"));
            case NEST:
                if(source instanceof PackageElement)
                    break;
                throw new AddConnectionException(Msg.get("error.connection.nest.withoutPkg"));
            case NOTE_CONNECTOR:
                if( source instanceof NoteElement)
                    break;
                throw new AddConnectionException(Msg.get("error.connection.noteConnection.withoutNote"));
            default:    throw new AddConnectionException(Msg.get("error.connection.invalidConnection"));
        }
    }




    @Override
    public void addToPackage(UmlPackage umlPackage, PackageableUmlModelElement packageableUmlModelElement) {

    }

    @Override
    public void removeFromPackage(UmlPackage umlPackage, PackageableUmlModelElement packageableUmlModelElement) {
        if(this.getModelElement()==packageableUmlModelElement)
            removeExistingConnection(Nest.class);
    }

    @Override
    public boolean isConnectionSource() {
        return true;
    }
}
