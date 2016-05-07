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

import java.awt.*;

import com.nilledom.draw.ConnectionVisitor;
import com.nilledom.draw.DrawingContext;
import com.nilledom.draw.RectilinearConnection;
import com.nilledom.exception.AddConnectionException;
import com.nilledom.model.InheritanceRelation;

/**
 * An inheritance connection.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class Inheritance extends ArrowConnection {

    private static Inheritance prototype;

    /**
     * Private constructor.
     */
    private Inheritance() {
        setConnection(new RectilinearConnection());
        setIsDashed(false);
        setOpenHead(false);
        setHeadColor(Color.WHITE);
        relation=new InheritanceRelation();
    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static Inheritance getPrototype() {
        if (prototype == null)
            prototype = new Inheritance();
        return prototype;
    }

    /**
     * {@inheritDoc}
     */
    @Override public void draw(DrawingContext drawingContext) {
        super.draw(drawingContext);
    }

    @Override public void acceptNode(ConnectionVisitor node)  {
        node.addConcreteConnection(this);
    }
    @Override public void cancelNode(ConnectionVisitor node){
        node.removeConcreteConnection(this);
    }
}
