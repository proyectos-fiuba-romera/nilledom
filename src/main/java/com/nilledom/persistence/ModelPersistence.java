package com.nilledom.persistence;






import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import com.nilledom.model.UmlModelElement;



public class ModelPersistence {

    private Set<UmlModelElement> elements;

    public Set<UmlModelElement> getElements() {
        return elements;
    }

    public void setElements(Set<UmlModelElement> elements) {
        this.elements = elements;
    }


}
