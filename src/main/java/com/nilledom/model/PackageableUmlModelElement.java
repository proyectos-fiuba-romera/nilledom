package com.nilledom.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nilledom.ui.AppFrame;
import com.nilledom.umldraw.shared.GeneralDiagram;

public abstract class PackageableUmlModelElement extends AbstractUmlModelElement {


    public PackageableUmlModelElement(){
        addPackageListener(AppFrame.get().getAppState().getTreeModel());
    }


    private NestRelation packageRelation;
    transient private Set<PackageListener> listeners  = new HashSet<>();

    private boolean isPackaged(){
        return packageRelation!=null;
    }

    public void setPackageRelation(NestRelation nestRelation){
        if(nestRelation.equals(packageRelation))
            return;
        unpack();
        packageRelation=nestRelation;
        notifyPackaged();

    }
    public UmlPackage getPackage(){
        if(!isPackaged())
            return null;
        return (UmlPackage) packageRelation.getNesting();
    }

    public void unpack(){
        if(isPackaged()){
            UmlPackage oldPkg = (UmlPackage) packageRelation.getNesting();
            packageRelation = null;
            notifyUnpackaged(oldPkg);
        }
    }

    private void notifyPackaged() {
        for(PackageListener l : listeners){
            l.addToPackage((UmlPackage) packageRelation.getNesting(),this);
        }

    }

    private void notifyUnpackaged(UmlPackage pkg){
        for(PackageListener l : listeners){
            l.removeFromPackage(pkg, this);
        }
    }

    public void addPackageListener(PackageListener listener) {
        listeners.add(listener);
    }

    public void removePackageListener(PackageListener listener){
        listeners.remove(listener);
    }

    @Override
    public boolean canBeInsertedOnTree(){
        return true;
    }

}
