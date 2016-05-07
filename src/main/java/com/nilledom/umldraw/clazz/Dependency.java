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
package com.nilledom.umldraw.clazz;

import com.nilledom.draw.ConnectionVisitor;
import com.nilledom.draw.DrawingContext;
import com.nilledom.draw.SimpleArrowTip;
import com.nilledom.draw.SimpleConnection;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.umldraw.shared.BaseConnection;

/**
 * This class implements a dependency. It is implemented by inheritance and
 * overrides the draw() method completely.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class Dependency extends BaseConnection {

    private static Dependency prototype;

    /**
     * Constructor.
     */
    private Dependency() {
        setConnection(new SimpleConnection());
        setIsDashed(true);
    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static Dependency getPrototype() {
        if (prototype == null)
            prototype = new Dependency();
        return prototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void draw(DrawingContext drawingContext) {
        super.draw(drawingContext);
        // Draw the arrow here, has to be rotated around the tip
        new SimpleArrowTip().draw(drawingContext, getEndPoint2(), calculateRotationInEndPoint2());
    }

    @Override
    public void acceptNode(ConnectionVisitor node) {
        node.addConcreteConnection(this);
    }
    @Override public void cancelNode(ConnectionVisitor node){
        node.removeConcreteConnection(this);
    }
}
