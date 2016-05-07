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

import com.nilledom.draw.DiagramElement;
import com.nilledom.model.UmlModelElement;

/**
 * An interface to specialize on DiagramElement. It exposes the UML model
 * element and declares it specifically for use as a main element in UML
 * diagrams.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface UmlDiagramElement extends DiagramElement {
    /**
     * Returns the UmlModelElement that belongs to this DiagramElement.
     *
     * @return the model element
     */
    UmlModelElement getModelElement();
    void setModelElement(UmlModelElement model);

}
