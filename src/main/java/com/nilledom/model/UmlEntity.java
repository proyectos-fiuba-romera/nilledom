package com.nilledom.model;


public class UmlEntity extends UmlStereotypedClass {

    private static final String ENTITY_NAME = "<<entity>>";
    private static UmlEntity prototype;
    /**
     * Constructor.
     */
    private UmlEntity() {
        super();
    }

    @Override
    public String getStereotype() {
        return ENTITY_NAME;
    }

    /**
     * Returns the Prototype instance of the UmlClass.
     *
     * @return the Prototype instance
     */
    public static UmlClass getPrototype() {
        if (prototype == null)
            prototype = new UmlEntity();
        return prototype;
    }


}
