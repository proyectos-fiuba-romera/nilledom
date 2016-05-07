package com.nilledom.model;


public interface PackageListener {


    void removeFromPackage(UmlPackage umlPackage,PackageableUmlModelElement packageableUmlModelElement);
    void addToPackage(UmlPackage umlPackage,PackageableUmlModelElement packageableUmlModelElement);

}
