package com.nilledom.umldraw.shared;

import java.awt.*;
import java.awt.geom.*;

import com.nilledom.draw.*;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.NestRelation;

public class Nest extends BaseConnection {

    private static Nest prototype;

    /**
     * Private constructor.
     */
    private Nest() {
        setConnection(new SimpleConnection());
        setIsDashed(false);
        relation=new NestRelation();
    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static Nest getPrototype() {
        if (prototype == null)
            prototype = new Nest();
        return prototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void draw(DrawingContext drawingContext) {

        Node nesting = getConnection().getNode1();
        Node nested = getConnection().getNode2();


        if(nesting.contains(nested.getAbsCenterX(),nested.getAbsCenterY()) || nesting.intersects(nested.getAbsoluteBounds()))
            return;
        super.draw(drawingContext);
        drawAnchor(drawingContext);

    }

    @Override
    public boolean isInBack() {
        return false;
    }

    private void drawAnchor(DrawingContext drawingContext) {
        Point2D endingPoint = getEndPoint1();
        Point2D next = getPoints().get(1);
        double radius = 6;
        double tg = (next.getY() - endingPoint.getY())/(next.getX()- endingPoint.getX());


        Point2D translation;
        //follows the direction until +- 35 degrees
        double tgLimit = Math.tan(Math.toRadians(35));
        double cotgLimit = 1/tgLimit;
        if(isRigth(endingPoint)) {
            if(tg > tgLimit)
                tg=tgLimit;
            if(tg < -tgLimit )
                tg = -tgLimit;
            translation = new Point2D.Double(radius, tg * radius);
        }
        else if(isLeft(endingPoint)) {
            if(tg > tgLimit)
                tg=tgLimit;
            if(tg < -tgLimit )
                tg = -tgLimit;
            translation = new Point2D.Double(-radius, -tg * radius);
        }
        else if(isUp(endingPoint)) {
            if(tg > 0 && tg < cotgLimit)
                tg=cotgLimit;
            if(tg < 0 && tg > -cotgLimit)
                tg=-cotgLimit;
            translation = new Point2D.Double(-radius / tg, -radius);
        }
        else {
            if(tg > 0 && tg < cotgLimit)
                tg=cotgLimit;
            if(tg < 0 && tg > -cotgLimit)
                tg=-cotgLimit;
            translation = new Point2D.Double(radius / tg, radius);
        }
        Point2D center =  new Point2D.Double(endingPoint.getX()+translation.getX(),endingPoint.getY()+translation.getY());



        drawingContext.drawEllipse(new Point2D.Double(center.getX() - radius, center.getY() - radius), new DoubleDimension(2*radius,2*radius),Color.white);
        drawingContext.drawLine(center.getX(),center.getY()-radius,center.getX(),center.getY()+radius);
        drawingContext.drawLine(center.getX()-radius,center.getY(),center.getX()+radius,center.getY());


    }

    private boolean isRigth(Point2D endpoint) {
        Rectangle2D bounds = getNode1().getAbsoluteBounds();
        return doubleEqual(endpoint.getX(),bounds.getMaxX());
    }
    private boolean isLeft(Point2D endpoint) {
        Rectangle2D bounds = getNode1().getAbsoluteBounds();
        return doubleEqual(endpoint.getX(),bounds.getMinX());
    }
    private boolean isUp(Point2D endpoint) {
        Rectangle2D bounds = getNode1().getAbsoluteBounds();
        return doubleEqual(endpoint.getY(),bounds.getMinY());
    }
    private boolean isDown(Point2D endpoint) {
        Rectangle2D bounds = getNode1().getAbsoluteBounds();
        return doubleEqual(endpoint.getY(),bounds.getMaxY());
    }

    private boolean doubleEqual(double d1, double d2){
        return Math.abs(d1-d2) <  0.2;
    }


    @Override public void acceptNode(ConnectionVisitor node) {
        node.addConcreteConnection(this);
    }
    @Override public void cancelNode(ConnectionVisitor node){
        node.removeConcreteConnection(this);
    }


}
