package com.nilledom.umldraw.usecase;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import com.nilledom.draw.AbstractNode;
import com.nilledom.draw.DrawingContext;
import com.nilledom.draw.Label;
import com.nilledom.model.UmlModelElement;
import com.nilledom.umldraw.shared.UmlNode;

public class SystemElement extends AbstractNode implements UmlNode{


    private static final float STROKE = 2.0f;
    private static SystemElement prototype;

    private SystemElement(){
        super();
        setSize(400,400);
    }

    public static SystemElement getPrototype(){

        if(prototype==null)
            prototype=new SystemElement();


        return  prototype;
    }

    @Override
    public boolean isInBack() {
        return true;
    }

    @Override
    public void draw(DrawingContext drawingContext) {
        Stroke originalStroke = drawingContext.getGraphics2D().getStroke();
        drawingContext.getGraphics2D().setStroke(new BasicStroke(STROKE));
        drawingContext.getGraphics2D().setBackground(new Color(0,0,0,0));
        drawingContext.getGraphics2D().setColor(Color.black);
        drawingContext.getGraphics2D().draw(new Rectangle2D.Double(getAbsoluteX1(), getAbsoluteY1(), getSize().getWidth(), getSize().getHeight()));

    }

    @Override
    public void recalculateSize(DrawingContext drawingContext) {

    }

    @Override
    public Label getLabelAt(double mx, double my) {
        return null;
    }

    @Override
    public UmlModelElement getModelElement() {
        return null;
    }

    @Override
    public void setModelElement(UmlModelElement model) {

    }
    @Override
    public boolean contains(double x, double y){
        //contains only the border
        return  (x >= getAbsoluteX1() && x <= getAbsoluteX1()+STROKE && y >= getAbsoluteY1() && y <= getAbsoluteY2() )||
                (x >= getAbsoluteX2()-STROKE && x <= getAbsoluteX2() && y >= getAbsoluteY1() && y <= getAbsoluteY2() )||
                (x >= getAbsoluteX1() && x <= getAbsoluteX2() && y >= getAbsoluteY1() && y <= getAbsoluteY1()+STROKE )||
                (x >= getAbsoluteX1() && x <= getAbsoluteX2() && y >= getAbsoluteY2()-STROKE && y <= getAbsoluteY2() );
    }

    @Override
    public boolean intersects(Rectangle2D bounds){
        //only intersects with the border
        double myLeft = getAbsoluteX1();
        double myRight = getAbsoluteX2();
        double myTop = getAbsoluteY1();
        double myBottom = getAbsoluteY2();

        double hisLeft = bounds.getMinX();
        double hisRight = bounds.getMaxX();
        double hisTop = bounds.getMinY();
        double hisBottom = bounds.getMaxY();


        return  (myLeft <= hisRight &&
                hisLeft <= myRight &&
                myTop <= hisBottom &&
                hisTop <= myBottom)&&
                (outLF(myLeft,myTop,hisLeft,hisTop)||
                outRB(myRight,myBottom,hisRight,hisBottom));


    }

    private boolean outRB(double myRight, double myBottom, double hisRight, double hisBottom) {
        return hisRight >= myRight - STROKE || hisBottom >= myBottom - STROKE;
    }


    private boolean outLF(double myLeft, double myTop, double hisLeft, double hisTop) {
        return hisLeft <= myLeft + STROKE || hisTop <= myTop + STROKE;
    }
}
