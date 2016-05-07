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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class implements a method call by reflection. It is used to implement
 * pluggable selectors.
 *
 * @author Wei-ju Wu
 * @version 1.0
 */
public class MethodCall {

    private final Object[] methodParameters;
    private Method method;

    /**
     * Constructor.
     *
     * @param theMethod  the method to call
     * @param parameters the parameter list
     */
    public MethodCall(final Method theMethod, final Object... parameters) {
        method = theMethod;
        methodParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            methodParameters[i] = parameters[i];
        }
    }

    /**
     * Returns the method.
     *
     * @return the method.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Call the method on the specified target.
     *
     * @param target the target object
     */
    public void call(Object target) {
        try {
            method.invoke(target, methodParameters);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}
