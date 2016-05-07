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
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

/**
 * DrawingContext is a very simple abstraction above the Java2D API. It provides elementary drawing
 * elements needed to draw UML diagrams. It also hides information about the style with which the
 * elements are drawn. Diagram elements render themselves by invoking methods on this interface.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface DrawingContext {

    /**
     * Sets the Graphics2D object and bounds.
     *
     * @param g2d    the Graphics2D object
     * @param bounds the bounding rectangle
     */
    void setGraphics2D(Graphics2D g2d, Rectangle bounds);

    /**
     * Draws a dashed line with the default line color.
     *
     * @param x0 the x0 coordinate
     * @param y0 the y0 coordinate
     * @param x1 the x1 coordinate
     * @param y1 the y1 coordinate
     */
    void drawDashedLine(double x0, double y0, double x1, double y1);

    /**
     * Draws a grid line with the default grid line color.
     *
     * @param x0 the x0 coordinate
     * @param y0 the y0 coordinate
     * @param x1 the x1 coordinate
     * @param y1 the y1 coordinate
     */
    void drawGridLine(double x0, double y0, double x1, double y1);

    /**
     * Draws a line with the default line color.
     *
     * @param x0 the x0 coordinate
     * @param y0 the y0 coordinate
     * @param x1 the x1 coordinate
     * @param y1 the y1 coordinate
     */
    void drawLine(double x0, double y0, double x1, double y1);

    /**
     * Draws a line with the default line color.
     *
     * @param p0 the origin point
     * @param p1 the end point
     */
    void drawLine(Point2D p0, Point2D p1);

    /**
     * Draws a line with a specific line color.
     *
     * @param p0    the origin point
     * @param p1    the end point
     * @param color the color of line
     */
    void drawLine(Point2D p0, Point2D p1, Color color);

    /**
     * Draws a rectangle with the default border color and fills it with a background color.
     *
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param width     the width
     * @param height    the height
     * @param fillColor the fill color
     */
    void drawRectangle(double x, double y, double width, double height, Color fillColor);

    // *************************************************************************
    // ****** Rectangles
    // **********************************

    /**
     * Fills the specfied rectangle.
     *
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param width     the width
     * @param height    the height
     * @param fillColor the fill color
     */
    void fillRectangle(double x, double y, double width, double height, Color fillColor);

    /**
     * Draws the specified rectangle with the given fill color and stroke color.
     *
     * @param x           the x coordinate
     * @param y           the y coordinate
     * @param width       the width
     * @param height      the height
     * @param strokeColor the stroke color
     * @param fillColor   the fill color
     */
    void drawRectangle(double x, double y, double width, double height, Color strokeColor,
        Color fillColor);

    /**
     * Draws a "rubber band", a dashed rectangle.
     *
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param width  the width
     * @param height the height
     */
    void drawRubberband(double x, double y, double width, double height);

    /**
     * Returns the clip bounds for this DrawingContext.
     *
     * @return the clip bounds
     */
    Rectangle getClipBounds();

    /**
     * Draws the outline of the specified Shape object.
     *
     * @param shape     the Shape object
     * @param fillColor the fill color
     */
    void draw(Shape shape, Color fillColor);

    /**
     * @param p0        the specific point
     * @param d0        the specific dimension
     * @param fillColor the fill color
     */
    void drawEllipse(Point2D origin, Dimension2D dimension, Color fillColor);

    // ***********************************************************************
    // ***** Drawing elipse
    // ******************************************

    /**
     * @param p0          the specific point
     * @param d0          the specific dimension
     * @param strokeColor the stroke color
     * @param fillColor   the fill color
     */
    void drawEllipse(Point2D p0, Dimension2D d0, Color strokeColor, Color fillColor);

    /**
     * Draws the given text at the specified position.
     *
     * @param text     the text
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param fontType the FontType
     */
    void drawLabel(String text, double x, double y, FontType fontType);

    // ***********************************************************************
    // ***** Drawing text
    // ******************************************

    /**
     * Returns the font for the specified font type.
     *
     * @param fontType the font type
     * @return the font
     */
    Font getFont(FontType fontType);

    /**
     * Returns the font for the specified font type.
     *
     * @param fontType the font type
     * @return the FontMetrics
     */
    FontMetrics getFontMetrics(FontType fontType);

    /**
     * Exposes the Graphics2D object.
     *
     * @return the Graphics2D object
     */
    Graphics2D getGraphics2D();

    /**
     * The available FontTypes.
     */
    public enum FontType {
        DEFAULT, ELEMENT_NAME, ABSTRACT_ELEMENT
    }
}
