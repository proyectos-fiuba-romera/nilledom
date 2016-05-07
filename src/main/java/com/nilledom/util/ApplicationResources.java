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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This class holds the application resource strings. UI labels are never
 * defined directly in the source code, but defined in locale-sensitive
 * property files. Even though other languages than English are not supported
 * here, a german localization would definitely be feasible.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public final class ApplicationResources {

    private static ApplicationResources instance = new ApplicationResources();
     private Properties configuration;

    // The resource bundle this is based on.
    private ResourceBundle resources ;
    /**
     * Private constructor to enforce Singleton.
     */
    private ApplicationResources() {
        int i = 1;
        configuration = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");

        if (inputStream != null) {
            try {
                configuration.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("property file config.properties not found in the classpath");
        }


        resources=ResourceBundle.getBundle("com.nilledom.ui.tinyuml-captions", new Locale(configuration.getProperty("language")));

    }

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static ApplicationResources getInstance() {
        return instance;
    }

    /**
     * Returns the value of the specified property.
     *
     * @param property the property name
     * @return the value
     */
    public String getString(String property) {
        try {
            return resources.getString(property);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Returns the first character of a resource property.
     *
     * @param property the property to retrieve
     * @return the first character as an int
     */
    public int getChar(String property) {
        String str = getString(property);
        return str == null ? 0 : str.charAt(0);
    }
}
