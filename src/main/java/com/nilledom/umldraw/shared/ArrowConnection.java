package com.nilledom.umldraw.shared;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import com.nilledom.draw.DrawingContext;

/**
 * implements drawing method for diferent kinds of arrows
 */

public abstract class ArrowConnection extends BaseConnection{

    private boolean openHead = true;
    private Color headColor = Color.WHITE;

    public ArrowConnection(){}

    public boolean isOpenHead() {
        return openHead;
    }

    public void setOpenHead(boolean openHead) {
        this.openHead = openHead;
    }

    public Color getHeadColor() {
        return headColor;
    }

    public void setHeadColor(Color headColor) {
        this.headColor = headColor;
    }

    @Override
    public void draw(DrawingContext context){
        super.draw(context);
        if(relation.isNavigableToElement2())
            drawHead(context,calculateRotationInEndPoint2(),getEndPoint2());
        if(relation.isNavigableToElement1())
            drawHead(context,calculateRotationInEndPoint1(),getEndPoint1());

    }

    private void drawHead(DrawingContext context,AffineTransform rotation,Point2D point){
        double x = point.getX(), y = point.getY();
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo(x - 9, y - 5);
        arrow.lineTo(x, y);
        arrow.lineTo(x - 9, y + 5);
        if(!openHead)
            arrow.closePath();
        arrow.transform(rotation);
        context.draw(arrow, headColor);
    }

}
