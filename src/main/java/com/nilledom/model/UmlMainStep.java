package com.nilledom.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class UmlMainStep extends UmlStep {

  private Set<String> entities;
  private String actor;
  private StepType type=StepType.REGULAR;

  protected UmlMainStep(String description) {
    super(description);
    this.setEntities(new HashSet<String>());
  }

  protected UmlMainStep(String description, String actor, StepType type) {
    this(description);
    this.setType(type);
    this.setActor(actor);
  }


  public UmlMainStep(String description, String actor, StepType type, Set<String> entities) {
    this(description, actor, type);
    this.setEntities(entities);
  }

  public UmlMainStep(String description, StepType type) {
    this(description);
    this.setType(type);
  }

  public Set<String> getEntities() {
    return entities;
  }

  public void setEntities(Set<String> entities) {
    this.entities = entities;
  }

  public String getActor() {
    return actor;
  }

  public void setActor(String actor) {
    this.actor = actor;
  }

  public StepType getType() {
    return type;
  }

  public void setType(StepType type) {
    this.type = type;
  }

  @Override
  public String showDescription() {
    if(isRoot())
      return super.getDescription();

    switch (type) {
      case IF:
      case WHILE:
      case FOR:
      case ELSE:
        return getIndexAndSpaces() + ". " + type.toString() + " " + super.getDescription();
      case REGULAR:
        return getIndexAndSpaces() + ". " + actor + ": " + getDescription().replace("@", "");
      default:
        return "";
    }
  }

  protected String getIndexAndSpaces() {
    String completeIndex = super.getCompleteIndex();
    int countMatches = StringUtils.countMatches(completeIndex, ".");
    return StringUtils.leftPad(completeIndex, completeIndex.length() + countMatches * 3, " ");

  }

  @Override
  public UmlStep clone() {

    Set<String> cloneEntities = new HashSet<String>(this.entities.size());
    for (String entity : this.entities) {
      cloneEntities.add(entity);
    }

    UmlStep cloned = new UmlMainStep(this.description, this.actor, this.type, cloneEntities);
    cloned.index = this.index;

    List<UmlStep> cloneChildren = new ArrayList<UmlStep>(this.children.size());
    for (UmlStep step : this.children) {
      if (step instanceof UmlMainStep) {
        UmlMainStep umlMainStep = (UmlMainStep) step;
        cloneChildren.add(umlMainStep.clone(cloned));
      }
    }

    cloned.children = cloneChildren;
    return cloned;
  }

  public UmlStep clone(UmlStep father) {

    Set<String> cloneEntities = new HashSet<String>(this.entities.size());
    for (String entity : this.entities) {
      cloneEntities.add(entity);
    }
    
    UmlStep cloned = new UmlMainStep(this.description, this.actor, this.type, cloneEntities);
    cloned.father = father;
    cloned.index = this.index;

    List<UmlStep> cloneChildren = new ArrayList<UmlStep>(this.children.size());
    for (UmlStep step : this.children) {
      if (step instanceof UmlMainStep) {
        UmlMainStep umlMainStep = (UmlMainStep) step;
        cloneChildren.add(umlMainStep.clone(cloned));
      }
    }


    cloned.children = cloneChildren;
    return cloned;
  }

  public boolean isFatherType() {
    if (type.equals(StepType.IF) || type.equals(StepType.WHILE) || type.equals(StepType.FOR)
        || type.equals(StepType.ELSE)) {
      return true;
    }

    return false;
  }
}
