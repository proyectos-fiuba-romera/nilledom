/**
 * Copyright 2007 Wei-ju Wu
 * <p/>
 * This file is part of TinyUML.
 * <p/>
 * TinyUML is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * <p/>
 * TinyUML is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with TinyUML; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */
package com.nilledom.draw;

import java.awt.*;

import com.nilledom.draw.DrawingContext.FontType;

/**
 * Default label implementation.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class SimpleLabel extends AbstractNode implements Label {

    private LabelSource source;
    private FontType fontType = FontType.DEFAULT;
    private boolean valid;

    /**
     * {@inheritDoc}
     */
    public LabelSource getSource() {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public void setSource(LabelSource aSource) {
        source = aSource;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return source.getLabelText();
    }

    /**
     * {@inheritDoc}
     */
    public void setText(String text) {
        source.setLabelText(text);
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void setFontType(FontType aFontType) {
        fontType = aFontType;
    }

    /**
     * {@inheritDoc}
     */
    public void draw(DrawingContext drawingContext) {
        if (!isValid()) {
            recalculateSize(drawingContext);
        }
        drawingContext.drawLabel(getText(), getAbsoluteX1(),
            getAbsoluteY1() + drawingContext.getFontMetrics(fontType).getMaxAscent(), fontType);
    }

    /**
     * {@inheritDoc}
     */
    public void recalculateSize(DrawingContext drawingContext) {
        FontMetrics fm = drawingContext.getFontMetrics(fontType);
        setSize(fm.stringWidth(getText()), fm.getHeight());
        valid = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean isValid() {
        return valid;
    }

    /**
     * {@inheritDoc}
     */
    protected void setValid(boolean flag) {
        valid = flag;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void invalidate() {
        valid = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override public Selection getSelection(DiagramOperations operations) {
        return NullSelection.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    public void centerHorizontally() {
        double lwidth = getSize().getWidth();
        double centerx = getParent().getSize().getWidth() / 2;
        setOrigin(centerx - (lwidth / 2), getOrigin().getY());
    }

    /**
     * {@inheritDoc}
     */
    public void centerVertically() {
        double lheight = getSize().getHeight();
        double centery = getParent().getSize().getHeight() / 2;
        setOrigin(getOrigin().getX(), centery - (lheight / 2));
    }

    @Override
    public void validate() {
        valid=true;
    }

    /**
     * {@inheritDoc}
     */
    public Label getLabelAt(double mx, double my) {
        if (contains(mx, my))
            return this;
        return null;
    }

    public FontType getFontType() {
        return fontType;
    }
}
