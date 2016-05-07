package com.nilledom.ui.diagram.commands;


import javax.swing.*;

import com.nilledom.model.*;
import com.nilledom.ui.diagram.EditStepDialog;
import com.nilledom.util.Msg;

import java.awt.*;
import java.util.*;

public class StepCRUD {


    private UmlUseCase useCase;
    private Flow flow;
    private Stack<UmlStep> lastOpenedStep;
    private Window parent;
    private JList<String> stepList;
    private DefaultListModel<String> stepsModel;


    public StepCRUD(UmlUseCase usecase, Flow flow, Window window, JList<String> stepList) {
        this.useCase = usecase;
        this.flow = flow;
        this.lastOpenedStep = new Stack<>();
        lastOpenedStep.push(flow.getRoot());
        this.parent = window;
        this.stepList = stepList;
        this.stepsModel = (DefaultListModel<String>) stepList.getModel();
    }

    public void add( boolean hasAMainActor){

        UmlStep father = lastOpenedStep.peek();
        
        EditStepDialog dialog = new EditStepDialog(parent, useCase, father,hasAMainActor);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        if (dialog.isOk()) {

            StepType stepType = dialog.getStepType();
            String stepDescription = dialog.getDescription();

            UmlStep step = null;

            switch (stepType) {
                case INCLUDE:{
                    String actor = dialog.getActor();
                    Set<String> entities = dialog.getEntities();
                    step = new IncludeStep(stepDescription, actor,  entities);
                    ((IncludeStep)step).setIncluded(dialog.getIncluded());
                    addNewStep(step);
                    break;
                }
                case REGULAR: {
                    String actor = dialog.getActor();
                    Set<String> entities = dialog.getEntities();
                    step = new UmlMainStep(stepDescription, actor, stepType, entities);
                    addNewStep(step);
                    break;
                }
                case IF:
                case WHILE:
                case FOR: {
                    step = new UmlMainStep(stepDescription, stepType);
                    addNewStep(step);
                    break;
                }
                case ELSE: {
                    step = new UmlMainStep(stepDescription, stepType);
                    lastOpenedStep.pop();
                    addNewStep(step);
                    break;
                }
                case ENDIF:
                case ENDWHILE:
                case ENDFOR: {
                    lastOpenedStep.pop();
                    break;
                }
                default:
                    break;
            }
        }
        read();

    }


    @SuppressWarnings("Duplicates")
    public void edit(boolean hasAMainActor) {

        if (stepsModel.isEmpty())
            return;

        int selectedStep = stepList.getSelectedIndex();

        if (selectedStep == -1) {
            selectedStep = stepsModel.getSize() - 1;
        }

        UmlMainStep step = (UmlMainStep) flow.getStep(selectedStep);
        UmlMainStep father = (UmlMainStep) step.getFather();

        EditStepDialog dialog = new EditStepDialog(parent, useCase, father, step,hasAMainActor);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);


        if (!dialog.isOk())
            return;

        step.setDescription( dialog.getDescription());
        step.setActor( dialog.getActor());
        step.setEntities( dialog.getEntities());

        if(step instanceof IncludeStep)
            ((IncludeStep) step).setIncluded(dialog.getIncluded());

        read();

    }
    private void addNewStep(UmlStep step) {
        UmlStep father = lastOpenedStep.peek();

        father.addChild(step);

        UmlMainStep umlMainStep = (UmlMainStep) step;
        if (umlMainStep.isFatherType()) {
            lastOpenedStep.push(umlMainStep);
        }

        stepsModel.addElement(step.showDescription());
    }


    public void remove(){

        if (stepsModel.isEmpty())
            return;

        int selectedStep = stepList.getSelectedIndex();

        if (selectedStep == -1) {
            selectedStep = stepsModel.getSize() - 1;
        }

        UmlStep step = flow.getStep(selectedStep);

        UmlStep father = step.getFather();
        if (father.getChildren().size() == 1 && father!=lastOpenedStep.peek()) {

            String msg = null;
            switch (((UmlMainStep) father).getType()) {
                case ELSE:
                    msg = Msg.get("editstepmainflow.error.delete.else.step.text");
                    break;
                case FOR:
                    msg = Msg.get("editstepmainflow.error.delete.for.step.text");
                    break;
                case IF:
                    msg = Msg.get("editstepmainflow.error.delete.if.step.text");
                    break;
                case WHILE:
                    msg = Msg.get("editstepmainflow.error.delete.while.step.text");
                    break;
            }

            JOptionPane.showMessageDialog(parent, msg, Msg.get("editstepmainflow.error.title"),
                    JOptionPane.INFORMATION_MESSAGE);

            return;
        }

        flow.removeStep(step);
        removeFromOpenedSteps((UmlMainStep)step);
        read();

    }

    private void removeFromOpenedSteps(UmlMainStep step) {
        if (step.isFatherType()) {
            lastOpenedStep.remove(step);
        }

        for (UmlStep child : step.getChildren()) {
            removeFromOpenedSteps((UmlMainStep) child);
        }

    }

    public void read() {
        stepsModel.clear();
        for (String element : flow.getDescription())
            stepsModel.addElement(element);

    }
}


