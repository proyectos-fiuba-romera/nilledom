package com.nilledom.persistence;


import java.util.List;

import com.nilledom.model.UmlDiagram;
import com.nilledom.ui.ElementNameGenerator;
import com.nilledom.umldraw.usecase.ActorElement;

public class ViewPersistence {

     private List<UmlDiagram> umlDiagrams;
    private ElementNameGenerator elementNameGenerator = new ElementNameGenerator();
    private List<UmlDiagram> openedDiagrams;

     public List<UmlDiagram> getUmlDiagrams() {
         return umlDiagrams;
     }

     public void setUmlDiagrams(List<UmlDiagram> umlDiagrams) {
         this.umlDiagrams = umlDiagrams;
     }

    public List<UmlDiagram> getOpenedDiagrams() {
        return openedDiagrams;
    }

    public void setOpenedDiagrams(List<UmlDiagram> openedDiagrams) {
        this.openedDiagrams = openedDiagrams;
    }
}
