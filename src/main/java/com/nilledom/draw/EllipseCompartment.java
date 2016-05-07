package com.nilledom.draw;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

public class EllipseCompartment extends Compartment {


    private Alignment alignment = Alignment.CENTERED;

    /**
     * Constructor.
     */
    public EllipseCompartment() {
        super();
    }



    /**
     * {@inheritDoc}
     */
    @Override public void draw(DrawingContext drawingContext) {
        if (!isValid()) {
            recalculateSize(drawingContext);
        }

        Point2D origin = new Point2D.Double(getAbsoluteX1(), getAbsoluteY1());
        Dimension2D dimension = new DoubleDimension(getSize().getWidth(), getSize().getHeight());

        drawingContext.drawEllipse(origin, dimension, Color.WHITE);
        for (Label label : super.getLabels()) {
            // for now, we always center the labels
            if (alignment == Alignment.CENTERED) {
                label.centerHorizontally();
                label.centerVertically();
            } else if (alignment == Alignment.LEFT) {
                label.setOrigin(Defaults.getInstance().getMarginLeft(), label.getOrigin().getY());
            }
            label.draw(drawingContext);
        }
    }
}
