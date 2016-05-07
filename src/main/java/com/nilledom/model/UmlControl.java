package com.nilledom.model;


public class UmlControl extends UmlStereotypedClass {

    private static final String CONTROL_NAME = "<<control>>";
    private static UmlControl prototype;
    /**
     * Constructor.
     */
    private UmlControl() {
        super();
    }

    @Override
    public String getStereotype() {
        return CONTROL_NAME;
    }

    /**
     * Returns the Prototype instance of the UmlClass.
     *
     * @return the Prototype instance
     */
    public static UmlClass getPrototype() {
        if (prototype == null)
            prototype = new UmlControl();
        return prototype;
    }



}
