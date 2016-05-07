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

import javax.swing.*;

import com.nilledom.ui.diagram.DiagramEditor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * This class starts TinyUML as a browser applet.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class TinyApplet extends JApplet implements ApplicationShell {

    private transient ApplicationState appState;


    /**
     * {@inheritDoc}
     */
    @Override public void init() {
        super.init();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }

    /**
     * Initialize the user interface.
     */
    private void createGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        appState = new ApplicationState();
    }

    /**
     * {@inheritDoc}
     */
    public Component getShellComponent() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DiagramEditor getCurrentEditor() {
        return appState.getCurrentEditor();
    }

    /**
     * {@inheritDoc}
     */
    public MenuManager getMenuManager() {
        return appState.getMenuManager();
    }

    /**
     * {@inheritDoc}
     */
    public void setTitle(String projectName) {
    }

    /**
     * {@inheritDoc}
     */
    public void quitApplication() {
    }
}
