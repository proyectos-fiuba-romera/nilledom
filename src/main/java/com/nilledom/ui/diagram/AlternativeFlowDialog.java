package com.nilledom.ui.diagram;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.nilledom.model.*;
import com.nilledom.ui.AppFrame;
import com.nilledom.ui.diagram.commands.StepCRUD;
import com.nilledom.util.Msg;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class AlternativeFlowDialog extends JDialog {

    private final UmlStep anyStep;
    private final boolean hasAMainActor;
    private StepCRUD stepCrud;
    private UmlUseCase usecase;
    private DefaultListModel<UmlStep> stepsListModel;
    private DefaultComboBoxModel<UmlStep> returnComboModel;
    private DefaultComboBoxModel<UmlStep> entryComboModel;
    private AlternativeFlow alternativeFlow;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField nameField;
    private JComboBox entryCombo;
    private JList stepsList;
    private JButton addStepButton;
    private JButton editStepButton;
    private JButton removeStepButton;
    private JPanel mainPanel;
    private JPanel namePanel;
    private JLabel nameLabel;
    private JTextField conditionField;
    private JPanel entryPanel;
    private JPanel flowPanel;
    private JPanel stepsPanel;
    private JScrollPane stepsPane;
    private JPanel flowButtons;
    private JPanel returnPanel;
    private JComboBox returnCombo;
    private JLabel returnLabel;
    private JPanel conditionPanel;
    private JLabel conditionLabel;
    private JLabel entryLabel;
    private boolean ok = false;

    public AlternativeFlowDialog(Window window, final AlternativeFlow alternativeFlow, UmlUseCase usecase, Flow mainFlow, boolean hasAMainActor) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.hasAMainActor = hasAMainActor;


        this.alternativeFlow = alternativeFlow;
        this.usecase = usecase;

        nameLabel.setText(Msg.get("editstepalternativeflow.label.name"));
        entryLabel.setText(Msg.get("editstepalternativeflow.label.entry"));
        conditionLabel.setText(Msg.get("editstepalternativeflow.label.condition"));
        returnLabel.setText(Msg.get("editstepalternativeflow.label.return"));

        nameField.setText(alternativeFlow.getName());
        conditionField.setText(alternativeFlow.getEnrtyCondition());
        entryComboModel = new DefaultComboBoxModel<>();
        returnComboModel = new DefaultComboBoxModel<>();

        anyStep = UmlStep.ANY;
        entryComboModel.addElement(anyStep);
        returnComboModel.addElement(anyStep);


        for (int i = 0; i < mainFlow.getAllSteps().size(); i++) {
            entryComboModel.addElement(mainFlow.getAllSteps().get(i));
            returnComboModel.addElement(mainFlow.getAllSteps().get(i));
        }


        entryCombo.setModel(entryComboModel);
        returnCombo.setModel(returnComboModel);

        if (alternativeFlow.getEntryStep() != null)
            for (int i = 0; i < entryComboModel.getSize(); i++)
                if (entryComboModel.getElementAt(i).toString().equals(alternativeFlow.getEntryStep().toString()))
                    entryCombo.setSelectedIndex(i);
        if (alternativeFlow.getReturnStep() != null)
            for (int i = 0; i < returnComboModel.getSize(); i++)
                if (returnComboModel.getElementAt(i).toString().equals(alternativeFlow.getReturnStep().toString()))
                    returnCombo.setSelectedIndex(i);

        stepsListModel = new DefaultListModel<>();

        stepsList.setModel(stepsListModel);


        stepCrud = new StepCRUD(usecase, alternativeFlow, this, stepsList);
        stepCrud.read();

        addStepButton.setText(Msg.get("stdcaption.add"));
        editStepButton.setText(Msg.get("stdcaption.edit"));
        removeStepButton.setText(Msg.get("stdcaption.delete"));
        buttonOK.setText(Msg.get("stdcaption.ok"));
        buttonCancel.setText(Msg.get("stdcaption.cancel"));

        buttonOK.addActionListener(new ActionListener() {
            @SuppressWarnings("Duplicates")
            public void actionPerformed(ActionEvent e) {
                UmlStep lastStep;
                List<UmlStep> allSteps = alternativeFlow.getAllSteps();
                if (!allSteps.isEmpty()) {
                    lastStep = allSteps.get(allSteps.size() - 1);
                    if (lastStep instanceof UmlMainStep)
                        switch (((UmlMainStep) lastStep).getType()) {
                            case IF:
                            case ELSE:
                            case WHILE:
                            case FOR:
                                JOptionPane.showMessageDialog(AppFrame.get().getShellComponent(), Msg.get("error.editusecase.orphanstep.message"),
                                        Msg.get("error.editusecase.orphanstep.title"), JOptionPane.ERROR_MESSAGE);
                                return;
                        }
                }
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        pack();

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        addStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddStep();

            }
        });
        editStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEditStep();
            }
        });
        removeStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemoveStep();
            }
        });
    }

    private void onRemoveStep() {
        stepCrud.remove();
    }

    private void onEditStep() {
        stepCrud.edit(hasAMainActor);
    }

    private void onAddStep() {
        stepCrud.add(hasAMainActor);
    }

    private void onOK() {
        ok = true;
        alternativeFlow.setName(nameField.getText());


        alternativeFlow.setEntryStep((UmlStep) entryCombo.getSelectedItem());


        alternativeFlow.setReturnStep((UmlStep) returnCombo.getSelectedItem());

        alternativeFlow.setEnrtyCondition(conditionField.getText());


        dispose();
    }

    private void onCancel() {
        ok = false;
        dispose();
    }


    public boolean isOk() {
        return ok;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(mainPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(700, 500), null, 0, false));
        namePanel = new JPanel();
        namePanel.setLayout(new GridLayoutManager(1, 2, new Insets(10, 0, 5, 0), -1, -1));
        mainPanel.add(namePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        nameLabel = new JLabel();
        nameLabel.setText("Label");
        namePanel.add(nameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameField = new JTextField();
        namePanel.add(nameField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        entryPanel = new JPanel();
        entryPanel.setLayout(new GridLayoutManager(1, 2, new Insets(5, 0, 5, 0), -1, -1));
        mainPanel.add(entryPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        entryLabel = new JLabel();
        entryLabel.setText("Label");
        entryPanel.add(entryLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        entryCombo = new JComboBox();
        entryPanel.add(entryCombo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        flowPanel = new JPanel();
        flowPanel.setLayout(new GridLayoutManager(1, 2, new Insets(5, 0, 5, 0), -1, -1));
        mainPanel.add(flowPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        stepsPanel = new JPanel();
        stepsPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        flowPanel.add(stepsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        stepsPane = new JScrollPane();
        stepsPanel.add(stepsPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        stepsList = new JList();
        stepsPane.setViewportView(stepsList);
        flowButtons = new JPanel();
        flowButtons.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        flowPanel.add(flowButtons, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addStepButton = new JButton();
        addStepButton.setText("Button");
        flowButtons.add(addStepButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editStepButton = new JButton();
        editStepButton.setText("Button");
        flowButtons.add(editStepButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeStepButton = new JButton();
        removeStepButton.setText("Button");
        flowButtons.add(removeStepButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        flowButtons.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        returnPanel = new JPanel();
        returnPanel.setLayout(new GridLayoutManager(1, 2, new Insets(5, 0, 5, 0), -1, -1));
        mainPanel.add(returnPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        returnLabel = new JLabel();
        returnLabel.setText("Label");
        returnPanel.add(returnLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        returnCombo = new JComboBox();
        returnPanel.add(returnCombo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(-1, 25), 0, false));
        conditionPanel = new JPanel();
        conditionPanel.setLayout(new GridLayoutManager(1, 2, new Insets(5, 0, 5, 0), -1, -1));
        mainPanel.add(conditionPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        conditionLabel = new JLabel();
        conditionLabel.setText("Label");
        conditionPanel.add(conditionLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        conditionField = new JTextField();
        conditionPanel.add(conditionField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
