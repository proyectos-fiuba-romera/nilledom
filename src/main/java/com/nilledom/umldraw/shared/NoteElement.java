/**
 * Copyright 2007 Wei-ju Wu
 * <p/>
 * This file is part of TinyUML.
 * <p/>
 * TinyUML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * TinyUML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with TinyUML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.nilledom.umldraw.shared;

import java.awt.Color;
import java.awt.geom.GeneralPath;

import com.nilledom.draw.AbstractCompositeNode;
import com.nilledom.draw.Defaults;
import com.nilledom.draw.DrawingContext;
import com.nilledom.draw.Label;
import com.nilledom.draw.LabelSource;
import com.nilledom.draw.MultiLineLabel;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.RelationType;
import com.nilledom.model.UmlModelElement;
import com.nilledom.util.Msg;

/**
 * This class represents a Note element in the UML diagram. This is in general
 * a multiline element which is flexible in resizing.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class NoteElement extends AbstractCompositeNode implements UmlNode, LabelSource {

    private static final int CORNER_SIZE = 10;
    protected static final double MARGIN_TOP = CORNER_SIZE + 2;
    private static final Color FILL_COLOR = new Color(249, 249, 145);
    private static NoteElement prototype;
    private String content;
    private Label label = new MultiLineLabel();

    /**
     * Constructor.
     */
    protected NoteElement() {
        setSize(180, 60);
        label.setSource(this);
        label.setParent(this);
        label.setOrigin(Defaults.getInstance().getMarginLeft(), MARGIN_TOP);
    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static NoteElement getPrototype() {
        if (prototype == null)
            prototype = new NoteElement();
        return prototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override public Object clone() {
        NoteElement cloned = (NoteElement) super.clone();
        cloned.label = (Label) label.clone();
        cloned.label.setSource(cloned);
        cloned.label.setParent(cloned);
        return cloned;
    }

    /**
     * {@inheritDoc}
     */
    public String getLabelText() {
        return content;
    }

    /**
     * {@inheritDoc}
     */
    public void setLabelText(String aText) {
        content = aText;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void invalidate() {
        label.invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public Label getLabelAt(double mx, double my) {
        if (inInnerArea(mx, my))
            return label;
        return null;
    }

    /**
     * Returns true if the specified point is in the inner Note area. It keeps
     * the margins from reacting to mouse clicks in order to improve usability.
     *
     * @param mx the mapped mouse x position
     * @param my the mapped mouse y position
     * @return true if in label area, false otherwise
     */
    private boolean inInnerArea(double mx, double my) {
        return mx >= (getAbsoluteX1() + Defaults.getInstance().getMarginLeft()) &&
            mx <= (getAbsoluteX2() - Defaults.getInstance().getMarginRight()) &&
            my >= (getAbsoluteY1() + MARGIN_TOP) &&
            my <= (getAbsoluteY2() - Defaults.getInstance().getMarginBottom());
    }

    /**
     * {@inheritDoc}
     */
    @Override public void draw(DrawingContext drawingContext) {
        double width = getSize().getWidth(), height = getSize().getHeight();
        double marginSide = Defaults.getInstance().getMarginSide();
        double marginBottom = Defaults.getInstance().getMarginBottom();
        if (!label.isValid()) {
            label.setSize(width - marginSide, height);
            label.recalculateSize(drawingContext);
            // Set a new height if it is the old one
            if ((label.getSize().getHeight() + MARGIN_TOP + marginBottom) > height) {
                height = label.getSize().getHeight() + (MARGIN_TOP + marginBottom);
                setSize(width, height);
            }
        }
        drawNote(drawingContext);
        label.draw(drawingContext);
    }
    protected void drawNote(DrawingContext drawingContext){
        double width = getSize().getWidth();
        double height = getSize().getHeight();

        double x = getAbsoluteX1(), y = getAbsoluteY1();
        GeneralPath mainShape = new GeneralPath();
        mainShape.moveTo(x, y);
        mainShape.lineTo(x + width - CORNER_SIZE, y);
        mainShape.lineTo(x + width, y + CORNER_SIZE);
        mainShape.lineTo(x + width, y + height);
        mainShape.lineTo(x, y + height);
        mainShape.closePath();
        GeneralPath corner = new GeneralPath();
        corner.moveTo(x + width - CORNER_SIZE, y);
        corner.lineTo(x + width - CORNER_SIZE, y + CORNER_SIZE);
        corner.lineTo(x + width, y + CORNER_SIZE);
        corner.closePath();
        drawingContext.draw(mainShape, FILL_COLOR);
        drawingContext.draw(corner, FILL_COLOR);

    }

    /**
     * {@inheritDoc}
     */
    public UmlModelElement getModelElement() {
        return null;
    }

    @Override
    public void setModelElement(UmlModelElement model) {

    }

    @Override
    public boolean acceptsConnectionAsSource(RelationType relationType) {
        switch(relationType){
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
            case NOTE_CONNECTOR:
                    break;
            default:    throw new AddConnectionException(Msg.get("error.connection.invalidConnection"));
        }
    }

    @Override
    public boolean isConnectionSource() {
        return true;
    }

    public Label getLabel() {
        return label;
    }

    public void validate() {
        label.validate();
    }

    protected void setLabel(Label label) {
        this.label = label;
    }
}
