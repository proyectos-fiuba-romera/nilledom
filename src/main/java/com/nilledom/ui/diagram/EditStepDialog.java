package com.nilledom.ui.diagram;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.nilledom.model.*;
import com.nilledom.util.Msg;

public class EditStepDialog extends javax.swing.JDialog {


  /**
   * 
   */
  private static final long serialVersionUID = -7124053842249491277L;
  private final boolean hasAMainActor;

  private Boolean isOk = Boolean.FALSE;
  private JList<String> entities;
  private JComboBox<String> comboEntities;
  private JComboBox<StepType> comboTypesStep;
  private JComboBox<UmlUseCase> includeCombo;
  private JTextPane stepDescription;
  private UmlMainStep step;
  private UmlStep father;
  private UmlUseCase umlUseCase;

  private JComboBox<String> comboActorsStep;
  private JTextField textFieldCondition;

  private String[] charactersForbidden = {".", ";", ",", "(", ")", "\"", "<", ">"};

  private JLabel lblCondition;
  private JPanel entitiesPanel;
  private JButton editStep;
  private JButton saveStep;

  private Boolean isEditMode = Boolean.FALSE;
  private JPanel stepPanel;
  private JScrollPane scrollPaneStep;
  private JLabel includedLabel;

  /**
   * Creates new form EditUseCaseDialog
   *
   * @param parent the parent frame
   * @wbp.parser.constructor
   */
  public EditStepDialog(java.awt.Window parent, UmlUseCase umlUseCase, UmlStep father, boolean hasAMainActor) {
    super(parent, ModalityType.APPLICATION_MODAL);
    this.father = father;
    this.umlUseCase = umlUseCase;
    this.hasAMainActor = hasAMainActor;
    initComponents();

  }

  public EditStepDialog(java.awt.Window parent, UmlUseCase umlUseCase, UmlStep father,
                        UmlMainStep step, boolean hasAMainActor) {
    super(parent, ModalityType.APPLICATION_MODAL);
    this.father = father;
    this.step = step;
    this.umlUseCase = umlUseCase;
    this.hasAMainActor = hasAMainActor;
    this.isEditMode = Boolean.TRUE;
    initComponents();
    myPostInit();
  }

  private void myPostInit() {

    String actor = step.getActor();
    StepType type = step.getType();

    // Marco el actor seleccionado
    comboActorsStep.setSelectedItem(actor);
    // Marco el tipo seleccionado
    comboTypesStep.setSelectedItem(type);
    comboTypesStep.setEnabled(false);

    setDescription(step);
    setEntities(step.getDescription(), step.getEntities());

  }

  private void initComponents() {
    setResizable(false);
    setSize(new Dimension(529, 400));
    setTitle(Msg.get("editstepmainflow.title"));
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    JScrollPane mainScrollPanel = new JScrollPane();
    mainScrollPanel.getVerticalScrollBar().setUnitIncrement(16);
    mainScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    JButton btnCancel = new JButton(Msg.get("stdcaption.cancel"));
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isOk = false;
        dispose();
      }
    });

    JButton btnOk = new JButton(Msg.get("stdcaption.ok"));
    btnOk.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        isOk = true;
        onSave();
        dispose();
      }
    });

    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(groupLayout
        .createParallelGroup(Alignment.TRAILING)
        .addGroup(
            groupLayout.createSequentialGroup().addContainerGap(394, Short.MAX_VALUE)
                .addComponent(btnOk).addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(btnCancel).addContainerGap()).addComponent(mainScrollPanel));
    groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
        groupLayout
            .createSequentialGroup()
            .addComponent(mainScrollPanel, GroupLayout.PREFERRED_SIZE, 322,
                GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(
                groupLayout.createParallelGroup(Alignment.LEADING).addComponent(btnCancel)
                    .addComponent(btnOk)).addContainerGap()));

    JPanel generalPanel = new JPanel();
    mainScrollPanel.setViewportView(generalPanel);

    stepPanel = new JPanel();
    stepPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), Msg
        .get("editstepmainflow.step.label"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

    scrollPaneStep = new JScrollPane();

    saveStep = new JButton(Msg.get("stdcaption.save"));
    saveStep.addActionListener(new ActionListener() {

      @SuppressWarnings({"unchecked", "rawtypes"})
      public void actionPerformed(ActionEvent arg0) {
        onSave();


      }
    });

    editStep = new JButton(Msg.get("stdcaption.edit"));
    editStep.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        StepType stepType = getStepType();

        switch (stepType) {
          case INCLUDE:
          case REGULAR: {
            stepDescription.setEnabled(true);
            break;
          }
          default: {
            break;
          }

        }
      }
    });

    JLabel typeLabel = new JLabel(Msg.get("editstepmainflow.type.label"));

    ComboBoxModel<StepType> typeComboBoxModel;

    if (isEditMode) {
      StepType[] values = new StepType[] {step.getType()};
      typeComboBoxModel = new DefaultComboBoxModel<>(values);
    } else {
      boolean hasChildren = father != null && !father.getChildren().isEmpty();
      StepType[] validTypes= StepType.getValidTypesFor(father,hasChildren,umlUseCase.isIncluding());

      typeComboBoxModel = new DefaultComboBoxModel<>(validTypes);
    }


    comboTypesStep = new JComboBox<>();
    comboTypesStep.setModel(typeComboBoxModel);
    comboTypesStep.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        StepType selectedType = (StepType) comboTypesStep.getSelectedItem();

        switch (selectedType) {
          case IF:
          case WHILE:
          case FOR:
            comboActorsStep.setEnabled(false);
            includeCombo.setVisible(false);
            includedLabel.setVisible(false);
            stepDescription.setVisible(false);
            lblCondition.setVisible(true);
            textFieldCondition.setVisible(true);
            textFieldCondition.setEnabled(true);
            scrollPaneStep.setVisible(false);
            saveStep.setVisible(false);
            editStep.setVisible(false);
            entitiesPanel.setVisible(false);
            break;

          case ELSE:
          case ENDIF:
          case ENDWHILE:
          case ENDFOR: {
            comboActorsStep.setEnabled(false);
            includeCombo.setVisible(false);
            includedLabel.setVisible(false);
            stepDescription.setVisible(false);
            lblCondition.setVisible(false);
            textFieldCondition.setVisible(false);
            entitiesPanel.setVisible(false);
            scrollPaneStep.setVisible(false);
            saveStep.setVisible(false);
            editStep.setVisible(false);
            break;
          }
          case INCLUDE:
            comboActorsStep.setEnabled(true);
            includeCombo.setVisible(true);
            includedLabel.setVisible(true);
            stepDescription.setVisible(true);
            lblCondition.setVisible(false);
            textFieldCondition.setVisible(false);
            entitiesPanel.setVisible(true);
            scrollPaneStep.setVisible(true);
            saveStep.setVisible(true);
            editStep.setVisible(true);
            break;
          default: {
            comboActorsStep.setEnabled(true);
            includeCombo.setVisible(false);
            includedLabel.setVisible(false);
            stepDescription.setVisible(true);
            lblCondition.setVisible(false);
            textFieldCondition.setVisible(false);
            entitiesPanel.setVisible(true);
            scrollPaneStep.setVisible(true);
            saveStep.setVisible(true);
            editStep.setVisible(true);
          }

        }

      }
    });


    includedLabel = new JLabel(Msg.get("editstepmainflow.included.label"));
    UmlUseCase [] includedUseCases =   new UmlUseCase[umlUseCase.getIncluded().size()];
    for(int i=0 ; i < includedUseCases.length ; i++){
      includedUseCases[i] = umlUseCase.getIncluded().get(i);
    }
    ComboBoxModel<UmlUseCase> includeComboBoxModel= new DefaultComboBoxModel<>(includedUseCases);
    includeCombo = new JComboBox<>(includeComboBoxModel);
    includedLabel.setVisible(false);
    includeCombo.setVisible(false);

    // String[] actorItems =
    // {Msg.get("editstepmainflow.user.actor"), Msg.get("editstepmainflow.system.actor")};

    List<String> actorItems = new ArrayList<String>();
    Set<UmlActor> umlActors = this.umlUseCase.getUmlActors();
    for (UmlActor umlActor : umlActors) {
      actorItems.add(umlActor.getName());
    }
    actorItems.add(Msg.get("editstepmainflow.system.actor"));
    if(!hasAMainActor||
            (this.step!=null && this.step.getActor().equals(Msg.get("editstepmainflow.default.actor")))
            )
      actorItems.add(Msg.get("editstepmainflow.default.actor"));


    JLabel actorLabel = new JLabel(Msg.get("editstepmainflow.actor.label"));
    String[] arrayActorItems = new String[actorItems.size()];
    actorItems.toArray(arrayActorItems);
    ComboBoxModel<String> actorComboBoxModel = new DefaultComboBoxModel<String>(arrayActorItems);
    comboActorsStep = new JComboBox<String>();
    comboActorsStep.setModel(actorComboBoxModel);

    lblCondition = new JLabel(Msg.get("editstepmainflow.condition.label"));
    lblCondition.setVisible(false);
    textFieldCondition = new JTextField();
    textFieldCondition.setColumns(10);
    textFieldCondition.setVisible(false);

    GroupLayout gropuLayoutStepPanel = new GroupLayout(stepPanel);
    gropuLayoutStepPanel.setHorizontalGroup(gropuLayoutStepPanel.createParallelGroup(
        Alignment.TRAILING).addGroup(
        gropuLayoutStepPanel
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
                gropuLayoutStepPanel
                    .createParallelGroup(Alignment.LEADING)
                    .addGroup(
                        gropuLayoutStepPanel
                            .createSequentialGroup()
                            .addComponent(scrollPaneStep, GroupLayout.DEFAULT_SIZE, 337,
                                Short.MAX_VALUE)
                            .addGap(10)
                            .addGroup(
                                gropuLayoutStepPanel
                                    .createParallelGroup(Alignment.TRAILING)
                                    .addComponent(saveStep, GroupLayout.DEFAULT_SIZE, 111,
                                        Short.MAX_VALUE)
                                    .addComponent(editStep, Alignment.LEADING,
                                        GroupLayout.PREFERRED_SIZE, 111, Short.MAX_VALUE)))
                    .addGroup(
                        gropuLayoutStepPanel
                            .createSequentialGroup()
                            .addGroup(
                                gropuLayoutStepPanel
                                    .createParallelGroup(Alignment.LEADING)
                                    .addGroup(
                                        gropuLayoutStepPanel
                                            .createParallelGroup(Alignment.TRAILING, false)
                                            .addComponent(typeLabel, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(actorLabel, GroupLayout.DEFAULT_SIZE, 54,
                                                Short.MAX_VALUE))
                                            .addComponent(includedLabel, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(lblCondition))
                                .addGap(18)
                            .addGroup(
                                gropuLayoutStepPanel
                                    .createParallelGroup(Alignment.LEADING)
                                    .addComponent(includeCombo, 0, 386, Short.MAX_VALUE)
                                    .addComponent(comboActorsStep, 0, 386, Short.MAX_VALUE)
                                    .addComponent(comboTypesStep, 0, 386, Short.MAX_VALUE)
                                    .addComponent(textFieldCondition, GroupLayout.DEFAULT_SIZE,
                                        386, Short.MAX_VALUE)))).addContainerGap()));
    gropuLayoutStepPanel.setVerticalGroup(gropuLayoutStepPanel.createParallelGroup(
        Alignment.LEADING).addGroup(
        gropuLayoutStepPanel
            .createSequentialGroup()
            .addGroup(
                gropuLayoutStepPanel
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(comboTypesStep, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(
                gropuLayoutStepPanel
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(actorLabel)
                    .addComponent(comboActorsStep, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
            .addGroup(
                gropuLayoutStepPanel
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(includedLabel)
                    .addComponent(includeCombo, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
            .addGroup(
                gropuLayoutStepPanel
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(textFieldCondition, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCondition))
            .addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
            .addGroup(
                gropuLayoutStepPanel
                    .createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(scrollPaneStep, GroupLayout.PREFERRED_SIZE, 52,
                        GroupLayout.PREFERRED_SIZE)
                    .addGroup(
                        gropuLayoutStepPanel
                            .createSequentialGroup()
                            .addComponent(saveStep)
                            .addPreferredGap(ComponentPlacement.RELATED, 10,
                                Short.MAX_VALUE).addComponent(editStep))).addContainerGap()));

    stepDescription = new JTextPane();
    stepDescription.setToolTipText(Msg.get("editstepmainflow.description.tooltip"));
    scrollPaneStep.setViewportView(stepDescription);
    stepPanel.setLayout(gropuLayoutStepPanel);

    entitiesPanel = new JPanel();
    entitiesPanel.setBorder(new TitledBorder(null, Msg.get("editstepmainflow.entities.label"),
        TitledBorder.LEADING, TitledBorder.TOP, null, null));

    GroupLayout firstLayout = new GroupLayout(generalPanel);
    firstLayout.setHorizontalGroup(firstLayout.createParallelGroup(Alignment.TRAILING).addGroup(
        firstLayout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(
                firstLayout
                    .createParallelGroup(Alignment.TRAILING)
                    .addComponent(entitiesPanel, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addComponent(stepPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 490,
                        Short.MAX_VALUE)).addGap(18)));
    firstLayout.setVerticalGroup(firstLayout.createParallelGroup(Alignment.LEADING).addGroup(
        firstLayout.createSequentialGroup().addGap(6)
            .addComponent(stepPanel, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
            .addGap(18).addComponent(entitiesPanel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
            .addContainerGap()));

    JScrollPane scrollPaneEntity = new JScrollPane();

    comboEntities = new JComboBox();

    JButton addEntity = new JButton(Msg.get("stdcaption.add"));
    addEntity.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        String selectedEntity = (String) comboEntities.getSelectedItem();
        DefaultListModel<String> entityModelList = (DefaultListModel<String>) entities.getModel();

        if (!entityModelList.contains(selectedEntity)) {
          ((DefaultListModel<String>) entities.getModel()).addElement(selectedEntity);
        }
      }
    });

    JButton deleteEntity = new JButton(Msg.get("stdcaption.delete"));
    deleteEntity.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {

        DefaultListModel<String> entityModelList = ((DefaultListModel<String>) entities.getModel());

        if (entityModelList.isEmpty())
          return;

        int selectedEntity = entities.getSelectedIndex();

        if (selectedEntity == -1) {
          selectedEntity = entityModelList.getSize() - 1;
        }

        entityModelList.remove(selectedEntity);

      }
    });
    GroupLayout groupLayoutEntityPanel = new GroupLayout(entitiesPanel);
    groupLayoutEntityPanel.setHorizontalGroup(groupLayoutEntityPanel.createParallelGroup(
        Alignment.TRAILING)
        .addGroup(
            groupLayoutEntityPanel
                .createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneEntity, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(
                    groupLayoutEntityPanel
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(
                            groupLayoutEntityPanel
                                .createSequentialGroup()
                                .addComponent(comboEntities, GroupLayout.PREFERRED_SIZE, 120,
                                    GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(addEntity, GroupLayout.DEFAULT_SIZE, 69,
                                    Short.MAX_VALUE))
                        .addComponent(deleteEntity, GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
    groupLayoutEntityPanel
        .setVerticalGroup(groupLayoutEntityPanel.createParallelGroup(Alignment.LEADING).addGroup(
            groupLayoutEntityPanel
                .createSequentialGroup()
                .addGap(6)
                .addGroup(
                    groupLayoutEntityPanel
                        .createParallelGroup(Alignment.BASELINE)
                        .addComponent(scrollPaneEntity, GroupLayout.PREFERRED_SIZE, 69,
                            GroupLayout.PREFERRED_SIZE)
                        .addGroup(
                            groupLayoutEntityPanel
                                .createSequentialGroup()
                                .addGroup(
                                    groupLayoutEntityPanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(comboEntities, GroupLayout.PREFERRED_SIZE,
                                            GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(addEntity)).addGap(8)
                                .addComponent(deleteEntity))).addContainerGap(15, Short.MAX_VALUE)));

    entities = new JList<>(new DefaultListModel<String>());
    entities.setVisibleRowCount(3);

    scrollPaneEntity.setViewportView(entities);
    entitiesPanel.setLayout(groupLayoutEntityPanel);
    generalPanel.setLayout(firstLayout);
    getContentPane().setLayout(groupLayout);

  }

  private void onSave() {

    StepType stepType = getStepType();
    switch (stepType) {
      case INCLUDE:
      case REGULAR: {
        entities.setModel(new DefaultListModel<String>());

        List<String> entityModel = new ArrayList<String>();
        String description = stepDescription.getText();
        if (description.trim().isEmpty()) {
          comboEntities.removeAllItems();
          break;
        }
        String[] split = description.split(" ");

        List<String> entitiesList = Arrays.asList(split);
        List<String> entitiesSelected = new ArrayList<String>();

        for (String entity : entitiesList) {
          entity = entity.trim();

          for (String character : charactersForbidden) {
            entity = entity.replace(character, "");
          }


          if (entity.startsWith("@")) {

            if(entity.length() < 3)
              continue;
            String entityFormatedSelected =
                    entity.substring(1, 2).toUpperCase() + entity.substring(2);
            if (!entitiesSelected.contains(entityFormatedSelected)) {
              entitiesSelected.add(entityFormatedSelected);
            }
          } else {

            if(entity.length() < 2)
              continue;
            String entityFormated = entity.substring(0, 1).toUpperCase() + entity.substring(1);

            if (!entityModel.contains(entityFormated)) {
              entityModel.add(entityFormated);
            }
          }
        }

        Collections.sort(entityModel);
        Collections.sort(entitiesSelected);
        comboEntities.setModel(new DefaultComboBoxModel(entityModel.toArray()));

        for (String entitySelect : entitiesSelected) {
          ((DefaultListModel<String>) entities.getModel()).addElement(entitySelect);
        }
        stepDescription.setEnabled(false);
        break;
      }
      default: {
        break;
      }
    }
  }

  private void setDescription(UmlMainStep step) {
    StepType stepType = getStepType();

    switch (stepType) {
      case IF:
      case WHILE:
      case FOR: {
        textFieldCondition.setText(step.getDescription());
        break;
      }
      case INCLUDE:
      case REGULAR: {
        stepDescription.setText(step.getDescription());
        break;
      }
      default:
        break;

    }
  }

  private void setEntities(String description, Set<String> entities) {

    StepType stepType = getStepType();

    switch (stepType) {
      case IF:
      case WHILE:
      case FOR:
      case ELSE: {
        return;
      }
      case INCLUDE:
      case REGULAR: {
        // Guardo lista de Entidades Posibles
        if(description.trim().isEmpty()){
          comboEntities.removeAllItems();
          break;
        }
        String[] split = description.split(" ");
        List<String> entitiesList = Arrays.asList(split);
        List<String> entityListFinal = new ArrayList<String>();
        for (String entity : entitiesList) {

          entity = entity.trim();
          if (entity.startsWith("@")|| entity.length() < 2) {
            continue;
          }

          String entityFormated = entity.substring(0, 1).toUpperCase() + entity.substring(1);
          if (!entityListFinal.contains(entityFormated)) {
            entityListFinal.add(entityFormated);
          }
        }
        Collections.sort(entityListFinal);
        comboEntities.setModel(new DefaultComboBoxModel(entityListFinal.toArray()));

        // Guardo las entidades seleccionadas
        DefaultListModel<String> model = (DefaultListModel<String>) this.entities.getModel();
        for (String entity : entities) {
          model.addElement(entity);
        }
        break;
      }
      default:
        break;

    }
  }

  public String getDescription() {
    StepType stepType = getStepType();

    switch (stepType) {
      case IF:
      case WHILE:
      case FOR: {
        return this.textFieldCondition.getText();
      }
      case INCLUDE:
      case REGULAR: {
        return this.stepDescription.getText();
      }
      default:
        return "";

    }
  }

  public Set<String> getEntities() {
    DefaultListModel<String> model = (DefaultListModel<String>) entities.getModel();
    Enumeration<String> elements = model.elements();
    Set<String> set = new HashSet<String>(Collections.list(elements));
    return set;
  }

  public StepType getStepType() {
    return (StepType) this.comboTypesStep.getSelectedItem();
  }

  public String getActor() {
    return (String) this.comboActorsStep.getSelectedItem();
  }

  public boolean isOk() {
    return isOk;
  }

  public UmlUseCase getIncluded() {
    return (UmlUseCase) includeCombo.getSelectedItem();
  }
}
