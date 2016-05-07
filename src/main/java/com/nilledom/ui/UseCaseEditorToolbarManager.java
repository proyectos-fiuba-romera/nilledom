package com.nilledom.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A toolbar manager for the use case diagram editor.
 *
 * @author Juan Manuel Romera
 * @version 2.0
 */
public class UseCaseEditorToolbarManager extends AbstractToolbarManager {

    private ButtonGroup buttongroup;

    /**
     * Constructor.
     */
    public UseCaseEditorToolbarManager() {
        buttongroup = new ButtonGroup();
        JToolBar toolbar = getToolbar();
        toolbar.add(createToggleButtonWithName(buttongroup, "select"));
        toolbar.add(createToggleButtonWithName(buttongroup, "actor"));
        toolbar.add(createToggleButtonWithName(buttongroup, "usecase"));
        toolbar.add(createToggleButtonWithName(buttongroup, "package"));
        toolbar.add(createToggleButtonWithName(buttongroup, "system"));
        toolbar.addSeparator(new Dimension(10, 10));
        toolbar.add(createToggleButtonWithName(buttongroup, "association"));
        toolbar.add(createToggleButtonWithName(buttongroup, "inheritance"));
        toolbar.add(createToggleButtonWithName(buttongroup, "extend"));
        toolbar.add(createToggleButtonWithName(buttongroup, "include"));
        toolbar.add(createToggleButtonWithName(buttongroup, "nest"));
        toolbar.addSeparator(new Dimension(10, 10));
        toolbar.add(createToggleButtonWithName(buttongroup, "note"));
        toolbar.add(createToggleButtonWithName(buttongroup, "noteconnector"));
        doClick("SELECT_MODE");
    }

    /**
     * Creates the specified toggle button.
     *
     * @param aButtonGroup an optional ButtonGroup to add to
     * @param name         the toggle button name
     * @return the toggle button
     */
    private JToggleButton createToggleButtonWithName(ButtonGroup aButtonGroup, String name) {
        return createToggleButton(aButtonGroup, "usecasetoolbar." + name);
    }
}
