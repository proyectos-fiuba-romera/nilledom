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
package com.nilledom.util;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class accesses images for icons from the class path.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class IconLoader {

    private static IconLoader instance = new IconLoader();
    private Map<IconType, String> urlMap = new HashMap<IconType, String>();
    private Map<IconType, Icon> iconMap = new HashMap<IconType, Icon>();
    private Map<String, IconType> iconTypeMap = new HashMap<String, IconType>();

    /**
     * Private constructor.
     */
    private IconLoader() {
        for (IconType iconType : IconType.values()) {
            iconTypeMap.put(iconType.toString(), iconType);
        }
        urlMap
            .put(IconType.MOUSE_POINTER, "com/nilledom/ui/mousepointer.png");
        urlMap.put(IconType.CLASS, "com/nilledom/ui/class.png");
        urlMap.put(IconType.BOUNDARY, "com/nilledom/ui/boundary.png");
        urlMap.put(IconType.CONTROL, "com/nilledom/ui/control.png");
        urlMap.put(IconType.ENTITY, "com/nilledom/ui/entity.png");
        urlMap.put(IconType.CLOSE_TAB, "com/nilledom/ui/close-tab.png");
        urlMap.put(IconType.PACKAGE, "com/nilledom/ui/package.png");
        urlMap.put(IconType.DEPENDENCY, "com/nilledom/ui/dependency.png");
        urlMap.put(IconType.ASSOCIATION, "com/nilledom/ui/association.png");
        urlMap.put(IconType.NOTE, "com/nilledom/ui/note.png");
        urlMap.put(IconType.NOTE_CONNECTOR,
            "com/nilledom/ui/note-connector.png");
        urlMap.put(IconType.AGGREGATION, "com/nilledom/ui/aggregation.png");
        urlMap.put(IconType.COMPOSITION, "com/nilledom/ui/composition.png");
        urlMap.put(IconType.INHERITANCE, "com/nilledom/ui/inheritance.png");
        urlMap.put(IconType.INTERFACE_REALIZATION,
            "com/nilledom/ui/interface-realization.png");
        urlMap.put(IconType.EXTEND, "com/nilledom/ui/extend.png");
        urlMap.put(IconType.INCLUDE, "com/nilledom/ui/include.png");
        urlMap.put(IconType.NEST, "com/nilledom/ui/nest.png");
        urlMap.put(IconType.MESSAGE, "com/nilledom/ui/message.png");
        urlMap.put(IconType.ACTOR, "com/nilledom/ui/actor.png");
        urlMap.put(IconType.USE_CASE, "com/nilledom/ui/usecase.png");

        urlMap.put(IconType.SYSTEM, "com/nilledom/ui/system.png");

        urlMap.put(IconType.NEW, "org/fife/plaf/Office2003/new.gif");
        urlMap.put(IconType.OPEN, "org/fife/plaf/Office2003/open.gif");
        urlMap.put(IconType.SAVE, "org/fife/plaf/Office2003/save.gif");
        urlMap.put(IconType.CUT, "org/fife/plaf/Office2003/cut.gif");
        urlMap.put(IconType.COPY, "org/fife/plaf/Office2003/copy.gif");
        urlMap.put(IconType.PASTE, "org/fife/plaf/Office2003/paste.gif");
        urlMap.put(IconType.DELETE, "org/fife/plaf/Office2003/delete.gif");
        urlMap.put(IconType.UNDO, "org/fife/plaf/Office2003/undo.gif");
        urlMap.put(IconType.REDO, "org/fife/plaf/Office2003/redo.gif");
        urlMap.put(IconType.ABOUT, "org/fife/plaf/Office2003/about.gif");
        urlMap.put(IconType.CONVERT, "com/nilledom/ui/convert.png");

        urlMap.put(IconType.APP, "com/nilledom/ui/nilledom.png");
    }

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static IconLoader getInstance() {
        return instance;
    }

    /**
     * Returns the icon for the specified icon type.
     *
     * @param type the icon type
     * @return the icon
     */
    public Icon getIcon(IconType type) {
        if (!iconMap.containsKey(type)) {
            String urlstr = urlMap.get(type);
            if (urlstr != null) {
                iconMap.put(type, new ImageIcon(getClass().getClassLoader().getResource(urlstr)));
            }
        }
        return iconMap.get(type);
    }

    /**
     * Returns the icon for the specified icon type name.
     *
     * @param typeName the icon type name
     * @return the icon
     */
    public Icon getIcon(String typeName) {
        return getIcon(iconTypeMap.get(typeName));
    }

    /**
     * This enum type lists the available icon types.
     */
    public enum IconType {
        CLOSE_TAB,
        NEW, OPEN, SAVE, CUT, COPY, PASTE, DELETE, UNDO, REDO,
        ABOUT,
        MOUSE_POINTER,
        CLASS, PACKAGE, DEPENDENCY, ASSOCIATION, AGGREGATION,
        COMPOSITION, INHERITANCE, INTERFACE_REALIZATION, NOTE, NOTE_CONNECTOR,
        LIFELINE, MESSAGE, ACTOR, EXTEND, INCLUDE, USE_CASE, CONVERT, BOUNDARY, CONTROL, ENTITY, SYSTEM, APP, NEST
    }
}
