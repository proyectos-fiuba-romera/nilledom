package com.nilledom.umldraw.usecase;


import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.nilledom.draw.Defaults;
import com.nilledom.draw.DrawingContext;
import com.nilledom.draw.ExtentionPointLabel;
import com.nilledom.draw.Label;
import com.nilledom.model.ExtendRelation;
import com.nilledom.model.UmlStep;
import com.nilledom.ui.diagram.ElementInserter;
import com.nilledom.umldraw.shared.NoteElement;
import com.nilledom.util.Msg;

public class ExtentionPointNote extends NoteElement{


    private static final double DISTANCE_TO_EXTEND = 50.0;
    private final Extend extend;
    private Label label = new ExtentionPointLabel();
    private boolean refresh=false;

    public ExtentionPointNote(Extend extend)
    {
        super();
        this.extend = extend;
        setSize(120, 40);

        label.setSource(this);
        label.setParent(this);
        label.setOrigin(Defaults.getInstance().getMarginLeft(), super.MARGIN_TOP);
    }
    public void initialize(){

        setParent(extend.getDiagram());
        Point2D initialPos = calculateIntialPosition();
        ElementInserter.insert(this,extend.getDiagram().getEditor(),initialPos);
        refresh();


    }

    private Point2D calculateIntialPosition() {
        double point1X=extend.getEndPoint1().getX();
        double point1Y=extend.getEndPoint1().getY();
        double point2X=extend.getEndPoint2().getX();
        double point2Y=extend.getEndPoint2().getY();

        double middleX= point1X + (point2X-point1X)/2.0;
        double middleY= point1Y + (point2Y-point1Y)/2.0;

        double angle = Math.atan((point2Y-point1Y)/(point2X-point1X));
        double perpendicularAngle = angle - Math.PI / 2.0;

        Point2D perpendicularDir = new Point2D.Double(Math.cos(perpendicularAngle),-Math.abs(Math.sin(perpendicularAngle)));
        double initialCenterX = middleX+perpendicularDir.getX()*DISTANCE_TO_EXTEND;
        double initialCenterY = middleY+perpendicularDir.getY()*DISTANCE_TO_EXTEND;
        double initialX = initialCenterX - getSize().getWidth()/2.0;
        double initialY = initialCenterY - getSize().getHeight()/2.0;


        return new Point2D.Double(initialX,initialY);
    }


    public void draw(DrawingContext drawingContext){
        if(!extend.getShow())
            return;
        double point1X=extend.getEndPoint1().getX();
        double point1Y=extend.getEndPoint1().getY();
        double point2X=extend.getEndPoint2().getX();
        double point2Y=extend.getEndPoint2().getY();

        double middleX= point1X + (point2X-point1X)/2.0;
        double middleY= point1Y + (point2Y-point1Y)/2.0;
        Line2D lineToCenter = new Line2D.Double(middleX, middleY, getAbsCenterX(), getAbsCenterY());
        Point2D intersection = new Point2D.Double(middleX,middleY);
        calculateIntersection(lineToCenter,intersection);
        drawingContext.drawDashedLine(middleX, middleY,intersection.getX(),intersection.getY());

        //label.invalidate();
        double width = getSize().getWidth(), height = getSize().getHeight();
        double marginSide = Defaults.getInstance().getMarginSide();
        double marginBottom = Defaults.getInstance().getMarginBottom();
        if (refresh) {
            label.recalculateSize(drawingContext);
            // Set a new width if it is the old one
            setMinimumSize(label.getSize().getWidth() + marginSide,
                    label.getSize().getHeight()+super.MARGIN_TOP+marginBottom);

            setSize(Math.max(getMinimumSize().getWidth(),width), Math.max(getMinimumSize().getHeight(),height));

        }
        super.drawNote(drawingContext);

        label.draw(drawingContext);
        refresh=false;

    }


    @Override
    public void setLabelText(String aText) {
        //label is not editable

    }
    @Override
    public String getLabelText(){
        UmlStep extentionPoint = ((ExtendRelation) extend.getModelElement()).getExtentionPoint();
        String condition = ((ExtendRelation)extend.getModelElement()).getCondition();

        if(condition.isEmpty())
            return Msg.get("ExtentionPointNote.label.ep") +" "+ extentionPoint.toString().trim();
        return Msg.get("ExtentionPointNote.label.ep") +" "+ extentionPoint.toString().trim() +"\n"+
               Msg.get("ExtentionPointNote.label.condition")+" "+condition;




    }

    public void refresh(){
        refresh=true;
    }

    public void delete(){
        extend.hideExtentionPoint();
    }


}
