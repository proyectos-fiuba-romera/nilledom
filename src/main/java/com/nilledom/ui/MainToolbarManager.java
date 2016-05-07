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
package com.nilledom.ui;

import java.awt.event.MouseListener;

import javax.swing.*;

/**
 * This class manages and creates the application's main toolbar.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class MainToolbarManager extends AbstractToolbarManager {

    /**
     * Constructor.
     */
    public MainToolbarManager() {
        addFileButtons();
        getToolbar().addSeparator();
        addEditButtons();
        getToolbar().addSeparator();
        createButtonWithName("convert");

        setEnabled("UNDO", false);
        setEnabled("REDO", false);
        setEnabled("CUT", false);
        setEnabled("COPY", false);
        setEnabled("PASTE", false);
        setEnabled("DELETE", false);
        setEnabled("CONVERT",true);
    }

    /**
     * Creates the buttons in the File section.
     */
    private void addFileButtons() {
        createButtonWithName("new");
        createButtonWithName("open");
        createButtonWithName("save");
    }

    /**
     * Creates the buttons in the Edit section.
     */
    private void addEditButtons() {
        createButtonWithName("cut");
        createButtonWithName("copy");
        createButtonWithName("paste");
        createButtonWithName("delete");
        createButtonWithName("undo");
        createButtonWithName("redo");

    }

    /**
     * Creates the button with the specified name.
     *
     * @param name the resource name
     * @return the button
     */
    private JButton createButtonWithName(String name) {
        return createButton("maintoolbar." + name);
    }

    public void addMouseListener(MouseListener l) {
        this.getToolbar().addMouseListener(l);

    }
}
