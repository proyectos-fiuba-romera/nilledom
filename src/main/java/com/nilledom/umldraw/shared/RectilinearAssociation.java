package com.nilledom.umldraw.shared;

import com.nilledom.draw.RectilinearConnection;
import com.nilledom.model.UmlModelElement;

public class RectilinearAssociation extends Association {

    private static Association prototype;

    /**
     * Constructor.
     */
    private RectilinearAssociation() {
        setConnection(new RectilinearConnection());
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
            prototype = new RectilinearAssociation();
        return prototype;
    }

    @Override
    public void setModelElement(UmlModelElement model) {

    }
}
