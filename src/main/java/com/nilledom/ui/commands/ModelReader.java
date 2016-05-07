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
package com.nilledom.ui.commands;



/**
 * Reads a model from a file. Models are stored and retrieved using
 * Serialization.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class ModelReader extends FileHandler {

    private static ModelReader instance = new ModelReader();

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static ModelReader getInstance() {
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public String getSuffix() {
        return ".tsm";
    }
}
