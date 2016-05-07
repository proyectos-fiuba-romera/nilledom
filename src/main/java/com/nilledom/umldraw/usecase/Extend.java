package com.nilledom.umldraw.usecase;


import java.awt.geom.Line2D;
import java.util.ArrayList;

import com.nilledom.draw.*;
import com.nilledom.model.ExtendRelation;
import com.nilledom.ui.diagram.DiagramEditor;
import com.nilledom.ui.diagram.commands.DeleteElementCommand;
import com.nilledom.umldraw.shared.ArrowConnection;
import com.nilledom.umldraw.shared.ConnectionNameLabel;

public class Extend extends ArrowConnection {

    private static final String LABEL = "<<extend>>";
    private static final int VERTICAL_DISTANCE = 10;
    private static Extend prototype;
    private ConnectionNameLabel nameLabel;
    private boolean show = false;
    private ExtentionPointNote extentionPointNote ;


    /**
     * Private constructor.
     */
    private Extend() {
        setConnection(new SimpleConnection());
        setIsDashed(true);
        setOpenHead(true);
        relation=new ExtendRelation();
        setupNameLabel();
        extentionPointNote = new ExtentionPointNote(this);
    }
    @Override
    public Extend clone(){
        Extend cloned= (Extend) super.clone();
        cloned.setupNameLabel();
        cloned.nameLabel.setParent(nameLabel.getParent());
        cloned.show = show;
        cloned.extentionPointNote= new ExtentionPointNote(cloned);
        if(show)
            cloned.extentionPointNote.initialize();
        return cloned;
    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static Extend getPrototype() {
        if (prototype == null)
            prototype = new Extend();
        return prototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void setParent(CompositeNode parent) {
        super.setParent(parent);
        nameLabel.setParent(parent);
        extentionPointNote.setParent(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void draw(DrawingContext drawingContext) {
        super.draw(drawingContext);
        nameLabel.recalculateSize(drawingContext);
        positionNameLabel();
        nameLabel.draw(drawingContext);

    }

    private ExtentionPointNote buildExtentionPointNote() {
        return null;
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

    @Override public void acceptNode(ConnectionVisitor node) {
        node.addConcreteConnection(this);
    }
    @Override public void cancelNode(ConnectionVisitor node){
        node.removeConcreteConnection(this);
    }

    public boolean getShow() {
        return show;
    }

    public void setShow(boolean show) {
        if(!this.show && show)
            extentionPointNote.initialize();
        if(this.show && !show)
            deleteExtentionPoint(getDiagram().getEditor());
        this.show = show;
    }


    public void hideExtentionPoint(){
        this.show=false;
    }

    public void deleteExtentionPoint(DiagramEditor editor){

        ArrayList<DiagramElement> elements = new ArrayList<>();
        elements.add(extentionPointNote);
        DeleteElementCommand command = new DeleteElementCommand(editor, elements);
        editor.execute(command);
    }
    @Override
    public boolean isEditable() {
        return true;
    }

    public void refresh() {
        extentionPointNote.refresh();
    }
}