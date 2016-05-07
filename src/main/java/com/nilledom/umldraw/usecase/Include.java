package com.nilledom.umldraw.usecase;

import java.awt.geom.Line2D;

import com.nilledom.draw.CompositeNode;
import com.nilledom.draw.ConnectionVisitor;
import com.nilledom.draw.DrawingContext;
import com.nilledom.draw.SimpleConnection;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.ExtendRelation;
import com.nilledom.model.IncludeRelation;
import com.nilledom.umldraw.shared.ArrowConnection;
import com.nilledom.umldraw.shared.ConnectionNameLabel;


public class Include extends ArrowConnection {

    private static final String LABEL = "<<include>>";
    private static Include prototype;
    private ConnectionNameLabel nameLabel;

    private static final int VERTICAL_DISTANCE = 10;

    /**
     * Private constructor.
     */
    private Include() {
        setConnection(new SimpleConnection());
        setIsDashed(true);
        setOpenHead(true);
        relation=new IncludeRelation();
        setupNameLabel();
    }
    @Override
    public Include clone(){
        Include cloned= (Include) super.clone();
        cloned.setupNameLabel();
        cloned.nameLabel.setParent(nameLabel.getParent());
        return cloned;
    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static Include getPrototype() {
        if (prototype == null)
            prototype = new Include();
        return prototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setParent(CompositeNode parent) {
        super.setParent(parent);
        nameLabel.setParent(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void draw(DrawingContext drawingContext) {
        super.draw(drawingContext);
        positionNameLabel();
        nameLabel.draw(drawingContext);
    }


    /**
     * Sets the name label.
     */
    public void setupNameLabel() {
        nameLabel = new ConnectionNameLabel();
        nameLabel.setLabelText(LABEL);

    }

    /**
     * Sets the position for the name label.
     */
    private void positionNameLabel() {
        // medium segment
        java.util.List<Line2D> segments = getSegments();
        Line2D middlesegment = segments.get(segments.size() / 2);
        int x = (int) (middlesegment.getX2() + middlesegment.getX1()) / 2;
        int y = (int) (middlesegment.getY2() + middlesegment.getY1()) / 2;
        double middleSegmentAngle = Math.atan2(middlesegment.getY2()-middlesegment.getY1(),middlesegment.getX2()-middlesegment.getX1());
        double sign=-1.1;
        if((middleSegmentAngle > 0 && middleSegmentAngle < Math.PI /2.0)||
                (middleSegmentAngle > -Math.PI && middleSegmentAngle < -Math.PI /2.0))
            sign=1.1;
        nameLabel.setAbsolutePos(x - nameLabel.getSize().getWidth()*(1+sign*Math.abs(Math.sin(middleSegmentAngle))),
                y +VERTICAL_DISTANCE* Math.abs(Math.cos(middleSegmentAngle)) );

    }

    @Override public void acceptNode(ConnectionVisitor node){
        node.addConcreteConnection(this);
    }
    @Override public void cancelNode(ConnectionVisitor node){
        node.removeConcreteConnection(this);
    }

}