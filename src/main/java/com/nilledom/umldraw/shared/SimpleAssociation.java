package com.nilledom.umldraw.shared;

import com.nilledom.draw.SimpleConnection;
import com.nilledom.model.UmlModelElement;

public class SimpleAssociation extends Association {


    private static Association prototype;

    /**
     * Constructor.
     */
    public SimpleAssociation() {
        setConnection(new SimpleConnection());
        setupMultiplicityLabels();
        setupNameLabel();
    }

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static Association getPrototype() {
        if (prototype == null)
            prototype = new SimpleAssociation();
        return prototype;
    }

    @Override
    public void setModelElement(UmlModelElement model) {

    }
}
