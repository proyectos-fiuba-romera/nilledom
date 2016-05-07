package com.nilledom.model;

public class UmlBoundary extends UmlStereotypedClass{

    private static final String BOUNDARY_NAME = "<<boundary>>";
    private static UmlBoundary prototype;
    /**
     * Constructor.
     */
    private UmlBoundary() {
        super();
    }

    @Override
    public String getStereotype() {
        return BOUNDARY_NAME;
    }

    /**
     * Returns the Prototype instance of the UmlClass.
     *
     * @return the Prototype instance
     */
    public static UmlClass getPrototype() {
        if (prototype == null)
            prototype = new UmlBoundary();
        return prototype;
    }





}
