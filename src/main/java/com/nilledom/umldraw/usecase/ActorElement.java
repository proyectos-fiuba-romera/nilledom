package com.nilledom.umldraw.usecase;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import com.nilledom.draw.AbstractCompositeNode;
import com.nilledom.draw.DoubleDimension;
import com.nilledom.draw.DrawingContext;
import com.nilledom.draw.Label;
import com.nilledom.draw.LabelSource;
import com.nilledom.draw.SimpleLabel;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.InheritanceRelation;
import com.nilledom.model.NestRelation;
import com.nilledom.model.PackageListener;
import com.nilledom.model.PackageableUmlModelElement;
import com.nilledom.model.Relation;
import com.nilledom.model.RelationType;
import com.nilledom.model.UmlActor;
import com.nilledom.model.UmlModelElement;
import com.nilledom.model.UmlModelElementListener;
import com.nilledom.model.UmlPackage;
import com.nilledom.umldraw.shared.Inheritance;
import com.nilledom.umldraw.shared.Nest;
import com.nilledom.umldraw.shared.NoteElement;
import com.nilledom.umldraw.shared.PackageElement;
import com.nilledom.umldraw.shared.UmlNode;
import com.nilledom.util.Msg;



/**
 * This class represents a Actor element in the editor. It is responsible for rendering the
 * information in the editor.
 *
 * @author Juan Manuel Romera
 * @version 1.0
 */
public final class ActorElement extends AbstractCompositeNode
    implements LabelSource, UmlNode, UmlModelElementListener,PackageListener {

    private static final double DEFAULT_WIDHT = 40;
    private static final double DEFAULT_HEIGHT = 70;
    private static final double LABEL_MARGIN_TOP = 5;

    private static final Color BACKGROUND = Color.WHITE;

    private static final double MIN_WIDTH = 30;
    private static final double MIN_HEIGHT = 50;
    private static ActorElement prototype;
    private UmlActor actor;
    private Label label;

    public ActorElement() {
        setSize(DEFAULT_WIDHT, DEFAULT_HEIGHT);
        setMinimumSize(MIN_WIDTH, MIN_HEIGHT);
        label = new SimpleLabel();
        label.setSource(this);
        label.setParent(this);
    }

    /**

     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static ActorElement getPrototype() {
        if (prototype == null)
            prototype = new ActorElement();
        return prototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override public Object clone() {
        ActorElement cloned = (ActorElement) super.clone();
        cloned.label = (Label) label.clone();
        cloned.label.setSource(cloned);
        cloned.label.setParent(cloned);


        if (actor != null) {
            cloned.actor = (UmlActor) actor.clone();
        }

        return cloned;

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
    @Override
    public UmlModelElement getModelElement() {
        return actor;
    }

    @Override
    public void setModelElement(UmlModelElement model) {
        if(model instanceof UmlActor)
            this.actor = (UmlActor) model;

        else throw new IllegalArgumentException("UmlActor expected");
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
  /*
   * (non-Javadoc)
   * 
   * @see org.tinyuml.draw.AbstractCompositeNode#draw(org.tinyuml.draw.DrawingContext )
   */
  /*
   * (non-Javadoc)
   * 
   * @see org.tinyuml.draw.AbstractCompositeNode#draw(org.tinyuml.draw.DrawingContext )
   */
    @Override public void draw(DrawingContext drawingContext) {

        double width = getSize().getWidth(), height = getSize().getHeight();
        double x = getAbsoluteX1(), y = getAbsoluteY1();
        double figureHeight = height - label.getSize().getHeight() - LABEL_MARGIN_TOP;

        label.recalculateSize(drawingContext);

        double xLabelOrigin = (width / 2) - (label.getSize().getWidth() / 2);
        double yLabelOrigin = figureHeight + LABEL_MARGIN_TOP;

        label.setOrigin(xLabelOrigin, yLabelOrigin);
        label.draw(drawingContext);

        // Drawing Head
        double headDiameter = figureHeight / 3; // The diameter of the head is
        // 1/3 of the
        // figure height.
        double headRadius = headDiameter / 2;
        double xHeadOrigin = x + (width / 2) - headRadius;
        Point2D headOrigin = new Point2D.Double(xHeadOrigin, y);
        Dimension2D headDimension = new DoubleDimension(headDiameter, headDiameter);

        drawingContext.drawEllipse(headOrigin, headDimension, BACKGROUND);


        // Drawing Body
        double bodyStartX = x + (width / 2);
        double bodyStartY = y + (figureHeight / 3);
        Point2D bodyOrigin = new Point2D.Double(bodyStartX, bodyStartY);
        double bodyEndX = bodyStartX;
        double bodyEndY = y + (figureHeight * 2 / 3);
        Point2D bodyEnd = new Point2D.Double(bodyEndX, bodyEndY);
        drawingContext.drawLine(bodyOrigin, bodyEnd);

        // Drawing Arms
        double armsStartX = x + (0.15 * width);
        double armsStartY = y + (figureHeight * 4 / 9);
        Point2D armsOrigin = new Point2D.Double(armsStartX, armsStartY);
        double armsEndX = x + (0.85 * width);
        double armsEndY = armsStartY;
        Point2D armsEnd = new Point2D.Double(armsEndX, armsEndY);
        drawingContext.drawLine(armsOrigin, armsEnd);

        // Drawing Left Leg
        double leftLegStartX = x + (width / 2);
        double leftLegStartY = y + (figureHeight * 2 / 3);
        Point2D leftOrigin = new Point2D.Double(leftLegStartX, leftLegStartY);
        double leftLegEndX = x + (0.15 * width);
        double leftLegEndY = y + figureHeight;
        Point2D leftLegEnd = new Point2D.Double(leftLegEndX, leftLegEndY);
        drawingContext.drawLine(leftOrigin, leftLegEnd);

        // Drawing Right Leg
        double rightLegStartX = x + (width / 2);
        double rightLegStartY = y + (figureHeight * 2 / 3);
        Point2D rightOrigin = new Point2D.Double(rightLegStartX, rightLegStartY);
        double rightLegEndX = x + (0.85 * width);
        double rightLegEndY = y + figureHeight;
        Point2D rightLegEnd = new Point2D.Double(rightLegEndX, rightLegEndY);
        drawingContext.drawLine(rightOrigin, rightLegEnd);

    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean isValid() {
        return label.isValid();
    }

    @Override public Label getLabelAt(double mx, double my) {
        if (inInnerArea(mx, my))
            return label;
        return null;
    }

    /**
     * Returns true if the specified point is in the inner Actor area. It keeps the margins from
     * reacting to mouse clicks in order to improve usability.
     *
     * @param mx the mapped mouse x position
     * @param my the mapped mouse y position
     * @return true if in label area, false otherwise
     */
    private boolean inInnerArea(double mx, double my) {
        double figureHeight =
            getSize().getHeight() - label.getSize().getHeight() - LABEL_MARGIN_TOP;

        return mx >= (getAbsoluteX1()) && mx <= (getAbsoluteX2()) && my >= (getAbsoluteY1()
            + figureHeight) && my <= (getAbsoluteY2());
    }



    @Override public void elementChanged(UmlModelElement element) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override public void addConcreteConnection(Nest connection){
        actor.setPackageRelation((NestRelation) connection.getModelElement());
    }
    @Override public void addConcreteConnection(Inheritance inheritance){
        Relation relation = (Relation) inheritance.getModelElement();

        UmlModelElement e1 = relation.getElement1();
        if(this.actor!=e1)
            return;
        removeExistingConnection(Inheritance.class);
        actor.addParent((InheritanceRelation) inheritance.getModelElement());
    }


    @Override public void removeConcreteConnection(Inheritance connection){
        if(connection.getNode1()==this)
            this.actor.removeParent();
    }

    @Override
    public boolean acceptsConnectionAsSource(RelationType relationType) {
        switch(relationType){
            case ASSOCIATION:
            case INHERITANCE:
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
                if(source instanceof UseCaseElement)
                    break;
                throw new AddConnectionException(Msg.get("error.connection.actor.associationWithoutUseCase"));
            case INHERITANCE:
                if(source.getModelElement() == actor)
                    throw new AddConnectionException(Msg.get("error.connection.actor.selfref"));
                if(source instanceof ActorElement)
                    break;
                throw new AddConnectionException(Msg.get("error.connection.actor.error.connection.actor.noactorinheritance"));
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



    public UmlActor getActor() {
        return actor;
    }

    public void setActor(UmlActor actor) {
        this.actor = actor;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
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
