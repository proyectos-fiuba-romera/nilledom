package com.nilledom.model;

import java.util.ArrayList;
import java.util.List;



public class UmlAlternativeStep extends UmlStep {

  public UmlAlternativeStep(String description) {
    super(description);
  }

  @Override
  public String showDescription() {
    return super.getCompleteIndex() + ". " + super.getDescription();
  }


  public UmlStep clone(UmlStep father) {

    UmlStep cloned = new UmlAlternativeStep(this.description);
    cloned.father = father;
    cloned.index = this.index;

    List<UmlStep> cloneChildren = new ArrayList<UmlStep>(this.children.size());
    for (UmlStep step : this.children) {
      if (step instanceof UmlAlternativeStep) {
        UmlAlternativeStep umlAltStep = (UmlAlternativeStep) step;
        cloneChildren.add(umlAltStep.clone(cloned));
      }
    }


    cloned.children = cloneChildren;
    return cloned;
  }

  @Override
  public UmlStep clone() {
    throw new RuntimeException("Not Implented");
  }

}
