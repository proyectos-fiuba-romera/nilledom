package com.nilledom.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IncludeStep extends UmlMainStep {

    private static final java.lang.String INCLUDE_PATTERN = " | INCLUDE ";
    private UmlUseCase included;


    @Override
    public UmlStep clone() {

        Set<String> cloneEntities = new HashSet<String>(getEntities().size());
        for (String entity : getEntities()) {
            cloneEntities.add(entity);
        }

        UmlStep cloned = new IncludeStep(this.description, getActor(), cloneEntities);
        cloned.index = this.index;

        List<UmlStep> cloneChildren = new ArrayList<UmlStep>(this.children.size());
        for (UmlStep step : this.children) {
            if (step instanceof UmlMainStep) {
                UmlMainStep umlMainStep = (UmlMainStep) step;
                cloneChildren.add(umlMainStep.clone(cloned));
            }
        }

        cloned.children = cloneChildren;
        ((IncludeStep)cloned).included = (UmlUseCase) this.included.clone();

        return cloned;
    }
    @Override
    public UmlStep clone(UmlStep father) {

        Set<String> cloneEntities = new HashSet<String>(getEntities().size());
        for (String entity : getEntities()) {
            cloneEntities.add(entity);
        }

        UmlStep cloned = new IncludeStep(this.description, getActor(),cloneEntities);
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

        ((IncludeStep)cloned).included = (UmlUseCase) this.included.clone();
        return cloned;
    }

    protected IncludeStep(String description, String actor) {
        super(description, actor, StepType.INCLUDE);
    }

    public IncludeStep(String description, String actor, Set<String> entities) {
        super(description, actor, StepType.INCLUDE, entities);
    }

    public IncludeStep(String description) {
        super(description, StepType.INCLUDE);
    }

    public UmlUseCase getIncluded() {
        return included;
    }

    public void setIncluded(UmlUseCase included) {
        this.included = included;
    }

    @Override
    public String showDescription() {
        return getIndexAndSpaces() + ". " + getActor() + ": " + getDescription().replace("@", "") + " | INCLUDE \""+ getIncluded() + "\"";
    }

    public UmlMainStep convertToRegular(){
        UmlMainStep regular = new UmlMainStep(super.getDescription(), getActor(), StepType.REGULAR, getEntities());
        regular.setFather(getFather());
        regular.setIndex(getIndex());
        regular.setChildren(getChildren());
        regular.index = this.index;
        return regular;
    }
}
