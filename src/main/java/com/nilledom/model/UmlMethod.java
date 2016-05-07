package com.nilledom.model;

public class UmlMethod extends DefaultNamedElement {

    private UmlMethod() {
    }
    private static UmlMethod prototype;

    /**
     * Returns the prototype instance.
     *
     * @return the prototype instance
     */
    public static UmlMethod getPrototype() {
        if (prototype == null)
            prototype = new UmlMethod();
        return prototype;
    }

    public boolean equals(Object o){
        if(getName()==null ||!(o instanceof UmlMethod) || ((UmlMethod)o).getName()==null)
            return false;
        return this.getName().equals(((UmlMethod)o).getName());
    }


    @Override
    public void setName(String name){
        if(name==null || name.isEmpty())
            return;
        String properName=name;
        if(!properName.substring(0,1).equals("+"))
            properName="+"+name;
        if(!properName.contains("(") ||
                properName.indexOf("(")!=properName.lastIndexOf("(")||
                !properName.contains(")") ||
                properName.indexOf(")")!=properName.lastIndexOf(")") ||
                properName.indexOf("(") > properName.indexOf(")")
                )
            return;
        if(!properName.contains(":"))
            properName= properName + ":void";

        if(properName.indexOf(")")!=properName.lastIndexOf(":")-1)
            return;


        if (properName.lastIndexOf(":") == properName.length()-1)
            properName= properName + "void";

        if(properName.indexOf("(")==1)
            return;
        super.setName(properName);
    }
}
