package com.nilledom.model;

import java.util.ArrayList;
import java.util.List;

import com.nilledom.util.Msg;


public class UmlStep {

  public static final UmlStep ANY = new UmlStep(Msg.get("umlStep.anyStep"));
  public static final UmlStep UNDEFINED = new UmlStep(Msg.get("umlStep.undefined"));
  protected UmlStep father;
  protected List<UmlStep> children;
  protected Integer index;

  protected String description;

  public UmlStep(String description) {
    this.description = description;
    this.children = new ArrayList<UmlStep>();
  }

  public void setFather(UmlStep father) {
    this.father = father;
  }

  public UmlStep getFather() {
    return father;
  }

  public Integer getIndex() {
    return index;
  }

  public Integer getRealIndex() {
    if (father != null) {
      return father.getRealIndex() + index;
    } else {
      return index;
    }
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void incrementIndex() {
    this.index++;
  }

  public void decrementIndex() {
    this.index--;
  }

  public String getCompleteIndex() {
    if (!father.isRoot()) {
      return father.getCompleteIndex() + "." + index.toString();
    } else {
      return index.toString();
    }
  }


  public void setIndex(int i) {
    this.index = i;

  }

  public void addChild(UmlStep step) {
    step.setFather(this);
    int size = children.size();
    step.setIndex(size + 1);
    children.add(step);
  }

  public void setChildren(List<UmlStep> children) {
    this.children.clear();
    for(UmlStep child : children)
      addChild(child);
  }

  public void addChild(UmlStep step, int selectedStep) {
    step.setFather(this);
    step.setIndex(selectedStep + 1);
    children.add(selectedStep, step);

    for (int i = selectedStep + 1; i < children.size(); i++) {
      children.get(i).incrementIndex();
    }
  }

  public void removeChild(UmlStep step) {
    int index = children.indexOf(step);
    for (int i = index + 1; i < children.size(); i++) {
      children.get(i).decrementIndex();
    }

    children.remove(step);
  }

  public void removeChildAt(int selectedAlternativeStep) {
    UmlStep umlStep = children.get(selectedAlternativeStep);
    this.removeChild(umlStep);
  }

  public UmlStep getChild(int index) {
    return children.get(index);
  }

  public List<UmlStep> getChildren() {
    return children;
  }

  public List<UmlStep> getDescendants() {
    List<UmlStep> result = new ArrayList<UmlStep>();

    for (UmlStep step : children) {
      result.add(step);
      result.addAll(step.getDescendants());
    }

    return result;
  }

  public  String showDescription(){
    return description;
  }

  public List<String> getCompleteDescription() {
    List<String> result = new ArrayList<String>();
    result.add(showDescription());
    for (UmlStep umlStep : children) {
      result.addAll(umlStep.getCompleteDescription());
    }

    return result;

  }

  public UmlStep clone(){
    try {
      if(this.equals(UmlStep.ANY))
        return ANY;
      if(this.equals(UmlStep.UNDEFINED))
        return UNDEFINED;

      return (UmlStep) super.clone();
    } catch (CloneNotSupportedException e) {
      return ANY;
    }
  }

  public UmlStep find(int index, int count) {
    if (index == count) {
      return this;
    }

    count++;

    for (UmlStep step : children) {
      int totalSize = count + step.getSize();
      if (totalSize <= index) {
        count = totalSize;
        continue;
      }
      return step.find(index, count);
    }

    return null;
  }

  public int getSize() {
    int size = 1;
    for (UmlStep step : children) {
      size += step.getSize();
    }

    return size;
  }

  @Override
  public String toString(){
    return showDescription();
  }


  public boolean isRoot() {
    return father==null;
  }
}
