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

import com.nilledom.draw.Connection;
import com.nilledom.model.Relation;

/**
 * The base interface for UML connections.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public interface UmlConnection extends Connection, UmlDiagramElement {

    /**
     * Sets the Relation object.
     *
     * @param aRelation the Relation object
     */
    void setRelation(Relation aRelation);

    /**
     * Returns the embedded connection.
     *
     * @return the embedded connection
     */
    Connection getConnection();

    /**
     * Sets the Connection object.
     *
     * @param aConnection the Connection object
     */
    void setConnection(Connection aConnection);
}
