package com.nilledom.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Flow {

  UmlStep root = new UmlMainStep("ROOT");

  public UmlStep getRoot() {
    return root;
  }

  public void setRoot(UmlStep root) {
    this.root = root;
  }


  /**
   * Agrega el step como hijo de root
   * @param step
     */
  public void addStep(UmlStep step) {
    root.addChild(step);
  }


  public void addStep(UmlStep step, int indexReal, int indexFlow) {
    step.setIndex(indexReal);
    root.getChildren().add(indexFlow, step);

    UmlStep father = step.getFather();
    for (int i = indexFlow + 1; i < root.getChildren().size(); i++) {
      // Si tienen Padre solo incremento el index de
      // los que son hijos del mismo padre.
      if (father == null) {
        UmlStep umlStep = root.getChildren().get(i);
        if (umlStep.getFather() == null) {
          umlStep.incrementIndex();
        }
      }
    }

  }

  public int getSize(){
    return root.getSize()-1;
  }

  public UmlStep getStep(int index) {
    return root.find(index,-1);
  }

  public void removeStep(UmlStep step) {
    step.getFather().removeChild(step);

  }

  @Override
  public Object clone() {
    Flow cloned = new Flow();
    cloned.root = this.root.clone();

    return cloned;

  }

  public List<UmlStep> getAllSteps(){
    return root.getDescendants();
  }

  public void addChildrenStep(UmlStep fatherStep, UmlStep childrenStep, int selectedStep) {
    fatherStep.addChild(childrenStep, selectedStep);
  }


  public Set<String> getAllEntities() {
    return getAllEntities(root.getChildren());
  }

  public Set<String> getAllEntities(List<UmlStep> steps) {
    Set<String> allEntities = new HashSet<>();
    for (UmlStep step : steps) {
      if (step instanceof UmlMainStep) {
        Set<String> stepEntites = ((UmlMainStep) step).getEntities();
        allEntities.addAll(stepEntites);
      }
      allEntities.addAll(getAllEntities(step.getChildren()));
    }
    return allEntities;
  }

  public void replaceEntity(String original, String replacement) {
    replaceEntity(original, replacement, root.getChildren());
  }

  public void replaceEntity(String original, String replacement, List<UmlStep> steps) {
    for (UmlStep step : steps) {
      if (step instanceof UmlMainStep) {
        Set<String> replacedEntities = new HashSet<>();
        for (String entity : ((UmlMainStep) step).getEntities()) {
          if (entity.equals(original))
            replacedEntities.add(replacement);
          else
            replacedEntities.add(entity);
        }
        ((UmlMainStep) step).setEntities(replacedEntities);
      }
      replaceEntity(original, replacement, step.getChildren());
    }
  }

  public void replaceInclude(UmlModelElement included){
    List<UmlStep> replacedFlow = new ArrayList<>();
    replaceInclude(included,root.getChildren(),replacedFlow);
    this.root.setChildren(replacedFlow);
  }
  public void replaceInclude(UmlModelElement included, List<UmlStep> steps, List<UmlStep> newSteps) {

    for (UmlStep step : steps) {
      UmlStep newStep = step.clone();
      if (step instanceof IncludeStep) {
        if(((IncludeStep) step).getIncluded().equals(included)) {
          newStep =((IncludeStep) step).convertToRegular();
        }
      }
      newSteps.add(newStep);
      List<UmlStep> newChildren = new ArrayList<>();
      newStep.setChildren(newChildren);
      replaceInclude(included,step.getChildren(),newStep.getChildren());

    }

  }

  public List<String> getDescription() {
    List<String> completeDescription = root.getCompleteDescription();
    return completeDescription.subList(1,completeDescription.size());
  }

  public List<UmlStep> getFlow() {
    return root.getChildren();
  }
}
