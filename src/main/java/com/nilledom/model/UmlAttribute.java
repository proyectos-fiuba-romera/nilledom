package com.nilledom.model;


public class UmlAttribute extends DefaultNamedElement {

    private UmlAttribute() {
    }
    private static UmlAttribute prototype;

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static UmlAttribute getPrototype() {
        if (prototype == null)
            prototype = new UmlAttribute();
        return prototype;
    }

    public boolean equals(Object o){
        if(getName()==null ||!(o instanceof UmlAttribute) || ((UmlAttribute)o).getName()==null)
            return false;
        return this.getName().equals(((UmlAttribute)o).getName());
    }


    @Override
    public void setName(String name){
        String properName=name;
        if(name == null || name.isEmpty())
            return;
        if(!properName.substring(0,1).equals("-"))
            properName="-"+name;
        if(!properName.contains(":")||
                properName.indexOf(":")!=properName.lastIndexOf(":") ||
                properName.split(":").length !=2||
                properName.split(":")[0].length() < 2 ||
                properName.split(":")[1].isEmpty() )
            return;
        super.setName(properName);

    }
}
