package com.nilledom.ui.diagram;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.nilledom.model.AlternativeFlow;
import com.nilledom.model.Flow;
import com.nilledom.model.UmlActor;
import com.nilledom.model.UmlMainStep;
import com.nilledom.model.UmlStep;
import com.nilledom.model.UmlUseCase;
import com.nilledom.ui.AppFrame;
import com.nilledom.ui.diagram.commands.StepCRUD;
import com.nilledom.umldraw.usecase.UseCaseElement;
import com.nilledom.util.Msg;
import com.nilledom.util.PDControlScrollPane;


/**
 * An edit dialog for use cases.
 *
 * @author Fernando Romera Ferrio
 * @version 1.0
 */
public class EditUseCaseDialog extends javax.swing.JDialog {


  private UseCaseElement useCaseElement;

  private boolean isOk;
  private JTextField name;
  private JTextField preconditionsTextField;
  private JTextField postconditionTextField;
  private JTextPane description;
  private JList<String> mainEntities;
  private JList<UmlActor> mainActors;
  private JList<UmlActor> secondaryActors;
  private JComboBox<String> comboMainEntity;
  private JComboBox<UmlActor> comboMainActor;
  private JComboBox<UmlActor> comboSecActors;
  private JList<AlternativeFlow> alternativeFlows;
  private JList<String> postconditions;
  private JList<String> preconditions;
  private JList<String> mainFlowStepList;
  private Flow mainFlow;

  private EditListAction preconditionEditListAction;
  private EditListAction postconditionEditListAction;

  private Window parent;
  private StepCRUD stepCRUD;


  /**
   * Creates new form EditUseCaseDialog
   *
   * @param parent the parent frame
   * @param anUseCase the edited Use Case
   * @param modal whether the dialog is to be modal
   */
  public EditUseCaseDialog(Window parent, UseCaseElement anUseCase, boolean modal) {
    super(parent, ModalityType.APPLICATION_MODAL);
    this.parent = parent;
    this.useCaseElement = anUseCase;
    initComponents();
    myPostInit();

  }

  public String getDescription() {
    return description.getText();
  }

  public String getName() {
    return name.getText();
  }

  public Set<UmlActor> getMainActors() {
    DefaultListModel<UmlActor> model = (DefaultListModel<UmlActor>) mainActors.getModel();
    Enumeration<UmlActor> elements = model.elements();
    Set<UmlActor> set = new HashSet<UmlActor>(Collections.list(elements));
    return set;
  }

  public Set<UmlActor> getSecondaryActors() {
    DefaultListModel<UmlActor> model = (DefaultListModel<UmlActor>) secondaryActors.getModel();
    Enumeration<UmlActor> elements = model.elements();
    Set<UmlActor> set = new HashSet<UmlActor>(Collections.list(elements));
    return set;
  }

  public List<String> getPreconditions() {
    DefaultListModel<String> listmodel = (DefaultListModel<String>) preconditions.getModel();
    Enumeration<String> elements = listmodel.elements();
    return Collections.list(elements);
  }

  public List<String> getPostconditions() {
    DefaultListModel<String> listmodel = (DefaultListModel<String>) postconditions.getModel();
    Enumeration<String> elements = listmodel.elements();
    return Collections.list(elements);
  }


  public Flow getMainFlow() {
    return mainFlow;
  }


  public String getMainEntity() {
    if (mainEntities.getModel().getSize() != 0)
      return mainEntities.getModel().getElementAt(0);
    else
      return null;
  }

  public boolean isOk() {
    return isOk;
  }


  private void myPostInit() {
    UmlUseCase umlUseCase = (UmlUseCase) useCaseElement.getModelElement();
    name.setText(umlUseCase.getName());
    description.setText(umlUseCase.getDescription());
    List<UmlActor> umlActors = new ArrayList<UmlActor>(umlUseCase.getUmlActors());
    Collections.sort(umlActors, new Comparator<UmlActor>() {
      @Override
      public int compare(UmlActor o1, UmlActor o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });


    String[] actorsNames = new String[umlActors.size()];
    int index = 0;
    for (UmlActor umlActor : umlActors) {
      actorsNames[index] = umlActor.getName();
      index++;
    }


    updateEntityPanel(true);

    comboMainActor.setModel(new DefaultComboBoxModel(umlActors.toArray()));
    comboSecActors.setModel(new DefaultComboBoxModel(umlActors.toArray()));

    DefaultListModel<UmlActor> mainActorsModel = new DefaultListModel<UmlActor>();
    for (UmlActor actor : umlUseCase.getMainActors())
      mainActorsModel.addElement(actor);
    mainActors.setModel(mainActorsModel);

    DefaultListModel<UmlActor> secActorsModel = new DefaultListModel<UmlActor>();
    for (UmlActor actor : umlUseCase.getSecondaryActors())
      secActorsModel.addElement(actor);
    secondaryActors.setModel(secActorsModel);

    DefaultListModel<String> preconditionsModel = new DefaultListModel<String>();
    for (String precondition : umlUseCase.getPreconditions()) {
      preconditionsModel.addElement(precondition);
    }
    preconditions.setModel(preconditionsModel);
    preconditionEditListAction.setList(preconditions);

    DefaultListModel<String> postconditionsModel = new DefaultListModel<String>();
    for (String postconditions : umlUseCase.getPostconditions()) {
      postconditionsModel.addElement(postconditions);
    }
    postconditions.setModel(postconditionsModel);
    postconditionEditListAction.setList(postconditions);


    DefaultListModel<AlternativeFlow> alternativeFlowListModel = new DefaultListModel<>();

    for(AlternativeFlow currentAlternativeFlow :umlUseCase.getAlternativeFlows()){
      AlternativeFlow cloned = (AlternativeFlow) currentAlternativeFlow.clone();
      alternativeFlowListModel.addElement(cloned);
    }
    alternativeFlows.setModel(alternativeFlowListModel);

    mainFlow = (Flow) umlUseCase.getMainFLow().clone();

    DefaultListModel<String> mainFlowStepModel = new DefaultListModel<String>();
    mainFlowStepList.setModel(mainFlowStepModel);

    stepCRUD = new StepCRUD(
            umlUseCase, mainFlow, parent,mainFlowStepList);


    validateUseCase(umlUseCase);
    stepCRUD.read();

  }

  private void updateEntityPanel(Boolean init) {

    UmlUseCase useCase = (UmlUseCase) useCaseElement.getModelElement();
    List<String> umlEntities;
    String mainEntity;
    if (init) {
      umlEntities = new ArrayList<>(useCase.getAllEntities());
      mainEntity = useCase.getMainEntity();
    } else {
      umlEntities = new ArrayList<>(mainFlow.getAllEntities());
      for(int i = 0 ; i < alternativeFlows.getModel().getSize();i++ )
        umlEntities.addAll(alternativeFlows.getModel().getElementAt(i).getAllEntities());
      mainEntity = useCase.getMainEntityByFlow(mainFlow);
    }

    Collections.sort(umlEntities);
    DefaultComboBoxModel entityComboBoxModel = new DefaultComboBoxModel(umlEntities.toArray());
    comboMainEntity.setModel(entityComboBoxModel);

    DefaultListModel<String> mainEntityModel = new DefaultListModel<String>();

    if (mainEntity != null) {
      mainEntityModel.addElement(mainEntity);
      entityComboBoxModel.removeElement(mainEntity);
    }

    mainEntities.setModel(mainEntityModel);

  }

  ///////////////////////  ACTION LISTENERS BEGIN ///////////////////////////////////////////

  private void onAddMainActor() {

    UmlActor selectedActor = (UmlActor) comboMainActor.getSelectedItem();
    DefaultListModel<UmlActor> mainActorModelList =
            (DefaultListModel<UmlActor>) mainActors.getModel();

    if (!mainActorModelList.contains(selectedActor)) {
      ((DefaultListModel<UmlActor>) mainActors.getModel()).addElement(selectedActor);
      ((DefaultListModel<UmlActor>) secondaryActors.getModel()).removeElement(selectedActor);
    }
  }

  private void onDeleteMainActor() {

    DefaultListModel<UmlActor> listModelActors =
            ((DefaultListModel<UmlActor>) mainActors.getModel());

    if (listModelActors.isEmpty())
      return;

    int selectedActor = mainActors.getSelectedIndex();

    if (selectedActor == -1) {
      selectedActor = listModelActors.getSize() - 1;
    }

    listModelActors.remove(selectedActor);

  }

  private void onAddSecondaryActor() {
    UmlActor selectedActor = (UmlActor) comboSecActors.getSelectedItem();

    DefaultListModel<UmlActor> secondaryActorModelList =
            (DefaultListModel<UmlActor>) secondaryActors.getModel();

    if (!secondaryActorModelList.contains(selectedActor)) {
      ((DefaultListModel<UmlActor>) secondaryActors.getModel()).addElement(selectedActor);
      ((DefaultListModel<UmlActor>) mainActors.getModel()).removeElement(selectedActor);
    }
  }


  private void onDeleteSecondaryActor() {

    DefaultListModel<UmlActor> listModelActors =
            ((DefaultListModel<UmlActor>) secondaryActors.getModel());

    if (listModelActors.isEmpty())
      return;

    int selectedActor = secondaryActors.getSelectedIndex();

    if (selectedActor == -1) {
      selectedActor = listModelActors.getSize() - 1;
    }

    listModelActors.remove(selectedActor);

  }


  private void onAddPrecondition() {

    String precondition = preconditionsTextField.getText();
    if (!precondition.isEmpty()) {
      ((DefaultListModel<String>) preconditions.getModel()).addElement(precondition);
      preconditionsTextField.setText("");
    }
  }


  private void onDeletePrecondition() {

    DefaultListModel<String> listModel = ((DefaultListModel<String>) preconditions.getModel());
    if (listModel.isEmpty())
      return;

    int selectedPrecondition = preconditions.getSelectedIndex();

    if (selectedPrecondition == -1) {
      selectedPrecondition = listModel.getSize() - 1;
    }
    listModel.remove(selectedPrecondition);
  }

  private void onAddStep() {
    stepCRUD.add(this.hasAMainActor());
    updateEntityPanel(false);
  }

  private void onDeleteStep() {
    stepCRUD.remove();
    updateEntityPanel(false);

  }

  private void onEditStep() {
    stepCRUD.edit(this.hasAMainActor());
    updateEntityPanel(false);

  }
  private boolean hasAMainActor(){
    return this.mainActors.getModel().getSize()!=0;
  }

  private void onAddAlternativeFlow() {

    AlternativeFlow newAlternativeFlow = new AlternativeFlow();
    AlternativeFlowDialog dialog =
            new AlternativeFlowDialog(parent,newAlternativeFlow , (UmlUseCase) useCaseElement.getModelElement(),mainFlow,this.hasAMainActor());
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.isOk()) {
      ((DefaultListModel)alternativeFlows.getModel()).addElement(newAlternativeFlow);

      updateEntityPanel(false);
    }
  }
  private void onEditAlternativeFlow() {


    if (alternativeFlows.getModel().getSize()==0)
      return;

    int selectedIndex = alternativeFlows.getSelectedIndex();

    if (selectedIndex == -1) {
      selectedIndex = alternativeFlows.getModel().getSize() - 1;
    }

    AlternativeFlow selectedFlow = alternativeFlows.getModel().getElementAt(selectedIndex);

    AlternativeFlowDialog dialog =
            new AlternativeFlowDialog(parent,selectedFlow , (UmlUseCase) useCaseElement.getModelElement(),mainFlow,this.hasAMainActor());
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
    if (dialog.isOk()) {
      updateEntityPanel(false);
    }

  }

  private void onDeleteAlternativeFlow(){
    alternativeFlows.remove(alternativeFlows.getSelectedIndex());
    updateEntityPanel(false);
  }

  private void onAddPostcondition() {
    String postCondition = postconditionTextField.getText();
    if (!postCondition.isEmpty()) {
      ((DefaultListModel<String>) postconditions.getModel()).addElement(postCondition);
      postconditionTextField.setText("");
    }
  }

  private void onDeletePostcondition() {

    DefaultListModel<String> listModel = ((DefaultListModel<String>) postconditions.getModel());
    if (listModel.isEmpty())
      return;

    int selected = postconditions.getSelectedIndex();

    if (selected == -1) {
      selected = listModel.getSize() - 1;
    }
    listModel.remove(selected);
  }

  private void onSelectMainEntity() {

    String selectedEntity = (String) comboMainEntity.getSelectedItem();
    DefaultListModel<String> mainEntityModelList =
            (DefaultListModel<String>) mainEntities.getModel();

    if (comboMainEntity.getModel().getSize() == 0)
      return;

    String changeEntity = mainEntityModelList.get(0);
    comboMainEntity.addItem(changeEntity);
    comboMainEntity.removeItem(selectedEntity);
    mainEntityModelList.clear();
    mainEntityModelList.addElement(selectedEntity);
  }

  private void onOk() {
    UmlUseCase umlUseCase = (UmlUseCase) useCaseElement.getModelElement();
    validateUseCase(umlUseCase);
    umlUseCase.setName(this.getName());
    umlUseCase.setDescription(this.getDescription());
    umlUseCase.setMainActors(this.getMainActors());
    umlUseCase.setSecondaryActors(this.getSecondaryActors());
    umlUseCase.setPreconditions(this.getPreconditions());
    umlUseCase.setPostconditions(this.getPostconditions());
    umlUseCase.setMainFLow(this.getMainFlow());
    umlUseCase.setAlternativeFlows(this.getAlternativeFlows());
    umlUseCase.setMainEntity(this.getMainEntity());
  }

  private List<AlternativeFlow> getAlternativeFlows() {
    List<AlternativeFlow> alternativeFlowList = new ArrayList<>();
    for(int i = 0 ; i < alternativeFlows.getModel().getSize();i++)
      alternativeFlowList.add(alternativeFlows.getModel().getElementAt(i));
    return alternativeFlowList;
  }

  private void validateUseCase(UmlUseCase umlUseCase) {
    for(UmlStep step: mainFlow.getAllSteps()){
      validateStep(step,umlUseCase);
    }
    for(int i=0 ; i < alternativeFlows.getModel().getSize();i++) {
      AlternativeFlow alternativeFlow = alternativeFlows.getModel().getElementAt(i);
      validateAlternativeFlow(alternativeFlow, umlUseCase);
    }
  }

  private void validateAlternativeFlow(AlternativeFlow alternativeFlow, UmlUseCase umlUseCase) {
    alternativeFlow.setEntryStep(validateStepExistance(alternativeFlow.getEntryStep(),mainFlow.getAllSteps()));
    alternativeFlow.setReturnStep(validateStepExistance(alternativeFlow.getReturnStep(),mainFlow.getAllSteps()));
    for (UmlStep step : alternativeFlow.getFlow()) {
      validateStep(step, umlUseCase);
    }
  }

  private UmlStep validateStepExistance(UmlStep step, List<UmlStep> allSteps) {
    for(UmlStep mainStep :allSteps){
      if(mainStep.toString().equals(step.toString()))
        return step;
    }
    return UmlStep.ANY;
  }

  private void validateStep(UmlStep step, UmlUseCase umlUseCase) {
    if(!(step instanceof UmlMainStep))
      return;
    UmlMainStep mainStep = (UmlMainStep) step;
    if(mainStep.getActor()==null)
      return;
    if(mainStep.getActor().equals(Msg.get("editstepmainflow.system.actor")))
      return;

    for(UmlActor actor : umlUseCase.getUmlActors()){
      if(actor.getName().equals(mainStep.getActor()))
        return;
    }
    mainStep.setActor(Msg.get("editstepmainflow.default.actor"));
  }

  ///////////////////////  ACTION LISTENERS END   ///////////////////////////////////////////


  private void initComponents() {
    setResizable(false);
    setSize(new Dimension(741, 700));
    setTitle(Msg.get("editUseCaseDialog.title"));
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    JScrollPane mainScrollPanel = new PDControlScrollPane();
    mainScrollPanel.getVerticalScrollBar().setUnitIncrement(32);
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
        UmlStep lastStep;
        List<UmlStep> allSteps = mainFlow.getAllSteps();
        if(!allSteps.isEmpty()) {
          lastStep = allSteps.get(allSteps.size() - 1);
          if(lastStep instanceof UmlMainStep)
            switch(((UmlMainStep) lastStep).getType()){
              case IF:
              case ELSE:
              case WHILE:
              case FOR:
                JOptionPane.showMessageDialog(AppFrame.get().getShellComponent(), Msg.get("error.editusecase.orphanstep.message"),
                        Msg.get("error.editusecase.orphanstep.title"), JOptionPane.ERROR_MESSAGE);
              return;
            }
        }
        isOk = true;
        onOk();
        dispose();
      }
    });
    GroupLayout groupLayout = new GroupLayout(getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap(607, Short.MAX_VALUE)
          .addComponent(btnOk)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnCancel)
          .addGap(6))
        .addComponent(mainScrollPanel, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addComponent(mainScrollPanel, GroupLayout.PREFERRED_SIZE, 633, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(btnCancel)
            .addComponent(btnOk))
          .addGap(6))
    );

    final JPanel panel = new JPanel();
    mainScrollPanel.setViewportView(panel);

    JLabel lblName = new JLabel(Msg.get("editUseCaseDialog.label.name"));

    name = new JTextField();
    name.setColumns(10);

    JLabel lblDescription = new JLabel(Msg.get("editUseCaseDialog.label.description"));

    JScrollPane scrollPane = new PDControlScrollPane();

    description = new JTextPane();
    scrollPane.setViewportView(description);

    JPanel mainEntityPanel = new JPanel();
    mainEntityPanel.setBorder(new TitledBorder(new EmptyBorder(1,1,1,1), Msg.get("editUseCaseDialog.border.mainentity"), TitledBorder.LEFT,
        TitledBorder.TOP, null, null));

    JPanel mainActorsPanel = new JPanel();
    mainActorsPanel.setBorder(new TitledBorder( new EmptyBorder(1,1,1,1), Msg.get("editUseCaseDialog.border.mainActors"), TitledBorder.LEFT,
            TitledBorder.TOP, null, null));

    JPanel panelSecondaryActors = new JPanel();
    panelSecondaryActors.setBorder(new TitledBorder( new EmptyBorder(1,1,1,1),
       Msg.get("editUseCaseDialog.border.secActors"), TitledBorder.LEFT, TitledBorder.TOP, null, null));

    JScrollPane scrollPane_2 = new PDControlScrollPane();

    comboSecActors = new JComboBox();

    JButton addSecondaryActors = new JButton(Msg.get("editUseCaseDialog.add"));
    addSecondaryActors.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onAddSecondaryActor();

      }
    });

    JButton removeSecondaryActors = new JButton(Msg.get("editUseCaseDialog.delete"));
    removeSecondaryActors.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onDeleteSecondaryActor();
      }
    });
    GroupLayout gl_panelSecondaryActors = new GroupLayout(panelSecondaryActors);
    gl_panelSecondaryActors.setHorizontalGroup(gl_panelSecondaryActors.createParallelGroup(
        Alignment.TRAILING).addGroup(
        gl_panelSecondaryActors
            .createSequentialGroup()
            .addGap(4)
            .addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
            .addPreferredGap(ComponentPlacement.UNRELATED)
            .addGroup(
                gl_panelSecondaryActors
                    .createParallelGroup(Alignment.TRAILING)
                    .addGroup(
                        gl_panelSecondaryActors
                            .createSequentialGroup()
                            .addComponent(comboSecActors, GroupLayout.PREFERRED_SIZE, 130,
                                GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(addSecondaryActors, GroupLayout.DEFAULT_SIZE, 10,
                                Short.MAX_VALUE))
                    .addComponent(removeSecondaryActors, GroupLayout.DEFAULT_SIZE, 140,
                        Short.MAX_VALUE)).addGap(6)));
    gl_panelSecondaryActors.setVerticalGroup(gl_panelSecondaryActors.createParallelGroup(
        Alignment.LEADING).addGroup(
        gl_panelSecondaryActors
            .createSequentialGroup()
            .addGap(6)
            .addGroup(
                gl_panelSecondaryActors
                    .createParallelGroup(Alignment.LEADING)
                    .addGroup(
                        gl_panelSecondaryActors
                            .createSequentialGroup()
                            .addGroup(
                                gl_panelSecondaryActors
                                    .createParallelGroup(Alignment.BASELINE)
                                    .addComponent(comboSecActors, GroupLayout.DEFAULT_SIZE,
                                        28, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(addSecondaryActors, GroupLayout.DEFAULT_SIZE,
                                            28, GroupLayout.PREFERRED_SIZE))
                            .addGap(6)
                            .addComponent(removeSecondaryActors))
                    .addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 62,
                        62)).addContainerGap(19, Short.MAX_VALUE)));

    secondaryActors = new JList();
    secondaryActors.setVisibleRowCount(3);
    scrollPane_2.setViewportView(secondaryActors);
    panelSecondaryActors.setLayout(gl_panelSecondaryActors);

    JPanel panelPreconditions = new JPanel();
    panelPreconditions.setBorder(new TitledBorder(new EmptyBorder(1,1,1,1),
        Msg.get("editUseCaseDialog.border.precondition"), TitledBorder.LEFT, TitledBorder.TOP, null, null));

    JScrollPane scrollPanePreconditions = new PDControlScrollPane();

    JButton addPreconditions = new JButton(Msg.get("editUseCaseDialog.add"));
    addPreconditions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onAddPrecondition();
      }
    });

    JButton deletePreconditions = new JButton(Msg.get("editUseCaseDialog.delete"));
    deletePreconditions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        onDeletePrecondition();
      }
    });

    preconditionsTextField = new JTextField();
    preconditionsTextField.setColumns(10);

    JButton editPreconditions = new JButton(Msg.get("editUseCaseDialog.edit"));
    preconditionEditListAction = new EditListAction(Msg.get("editUseCaseDialog.edit"));
    editPreconditions.setAction(preconditionEditListAction);

    GroupLayout gl_panelPreconditions = new GroupLayout(panelPreconditions);
    gl_panelPreconditions.setHorizontalGroup(gl_panelPreconditions.createParallelGroup(
        Alignment.TRAILING)
        .addGroup(
            gl_panelPreconditions
                .createSequentialGroup()
                .addGap(6)
                .addGroup(
                    gl_panelPreconditions
                        .createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPanePreconditions, GroupLayout.DEFAULT_SIZE, 450,
                            Short.MAX_VALUE)
                        .addComponent(preconditionsTextField, GroupLayout.DEFAULT_SIZE, 450,
                            Short.MAX_VALUE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(
                    gl_panelPreconditions
                        .createParallelGroup(Alignment.TRAILING)
                        .addComponent(deletePreconditions, GroupLayout.PREFERRED_SIZE,
                            50, Short.MAX_VALUE)
                        .addComponent(editPreconditions, GroupLayout.DEFAULT_SIZE, 50,
                            Short.MAX_VALUE)
                        .addComponent(addPreconditions, GroupLayout.DEFAULT_SIZE, 50,
                            Short.MAX_VALUE)).addGap(6)));
    gl_panelPreconditions.setVerticalGroup(gl_panelPreconditions.createParallelGroup(
        Alignment.LEADING).addGroup(
        gl_panelPreconditions
            .createSequentialGroup()
            .addGap(6)
            .addGroup(
                gl_panelPreconditions
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(addPreconditions, GroupLayout.PREFERRED_SIZE, 28,
                            GroupLayout.PREFERRED_SIZE)
                    .addComponent(preconditionsTextField, GroupLayout.PREFERRED_SIZE,
                        28, GroupLayout.PREFERRED_SIZE))
            .addGap(6)
            .addGroup(
                gl_panelPreconditions
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(scrollPanePreconditions, GroupLayout.PREFERRED_SIZE, 62,
                        GroupLayout.PREFERRED_SIZE)
                    .addGroup(
                        gl_panelPreconditions.createSequentialGroup()
                            .addComponent(editPreconditions, GroupLayout.PREFERRED_SIZE, 28,
                                    GroupLayout.PREFERRED_SIZE)
                            .addGap(6)
                            .addComponent(deletePreconditions, GroupLayout.PREFERRED_SIZE, 28,
                                    GroupLayout.PREFERRED_SIZE)))
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    preconditions = new JList();
    scrollPanePreconditions.setViewportView(preconditions);
    panelPreconditions.setLayout(gl_panelPreconditions);

    JPanel panelMainFlow = new JPanel();
    panelMainFlow.setBorder(new TitledBorder(new EmptyBorder(1,1,1,1),
        Msg.get("editUseCaseDialog.border.mainflow"), TitledBorder.LEFT, TitledBorder.TOP, null, null));

    JScrollPane mainFlowStepPane = new PDControlScrollPane();
    JButton addMainFlowButton = new JButton(Msg.get("editUseCaseDialog.add"));






    addMainFlowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onAddStep();
      }

    });

    JButton editStepMainFlowButton = new JButton(Msg.get("editUseCaseDialog.edit"));
    editStepMainFlowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onEditStep();


      }
    });

    JButton deletStepMainFlowButton = new JButton(Msg.get("editUseCaseDialog.delete"));
    deletStepMainFlowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onDeleteStep();
      }
    });



    GroupLayout gl_panelMainFlow = new GroupLayout(panelMainFlow);
    gl_panelMainFlow.setHorizontalGroup(gl_panelMainFlow.createParallelGroup(Alignment.TRAILING)
        .addGroup(
            gl_panelMainFlow
                .createSequentialGroup()
                .addGap(6)
                .addComponent(mainFlowStepPane, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addGap(10)
                .addGroup(
                    gl_panelMainFlow
                        .createParallelGroup(Alignment.LEADING)
                        .addComponent(editStepMainFlowButton, Alignment.TRAILING,
                            GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                        .addComponent(deletStepMainFlowButton, GroupLayout.DEFAULT_SIZE, 50,
                            Short.MAX_VALUE)
                        .addComponent(addMainFlowButton, GroupLayout.DEFAULT_SIZE, 50,
                            Short.MAX_VALUE))
        ).addGap(6));
    gl_panelMainFlow.setVerticalGroup(gl_panelMainFlow.createParallelGroup(Alignment.LEADING)
        .addGroup(
            gl_panelMainFlow
                .createSequentialGroup()
                .addGap(11)
                .addGroup(
                    gl_panelMainFlow
                        .createParallelGroup(Alignment.BASELINE)
                        .addComponent(mainFlowStepPane, GroupLayout.PREFERRED_SIZE, 150,
                            GroupLayout.PREFERRED_SIZE)
                        .addGroup(
                            gl_panelMainFlow.createSequentialGroup()
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(addMainFlowButton).addGap(7)
                                .addComponent(editStepMainFlowButton).addGap(8)
                                .addComponent(deletStepMainFlowButton)
                                .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));


    mainFlowStepList = new JList();
    mainFlowStepPane.setViewportView(mainFlowStepList);
    panelMainFlow.setLayout(gl_panelMainFlow);

    JPanel panelPostconditions = new JPanel();
    panelPostconditions.setBorder(new TitledBorder(new EmptyBorder(1,1,1,1),
        Msg.get("editUseCaseDialog.border.postcondition"), TitledBorder.LEFT, TitledBorder.TOP, null, null));

    postconditionTextField = new JTextField();
    postconditionTextField.setColumns(10);

    JScrollPane scrollPanePostconditions = new PDControlScrollPane();

    JButton editPostConditions = new JButton(Msg.get("editUseCaseDialog.edit"));
    postconditionEditListAction = new EditListAction(Msg.get("editUseCaseDialog.edit"));
    editPostConditions.setAction(postconditionEditListAction);

    JButton addPostconditions = new JButton(Msg.get("editUseCaseDialog.add"));
    addPostconditions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onAddPostcondition();
      }
    });

    JButton deletePostoconditions = new JButton(Msg.get("editUseCaseDialog.delete"));
    deletePostoconditions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        onDeletePostcondition();
      }
    });


    GroupLayout gl_panelPostconditions = new GroupLayout(panelPostconditions);
    gl_panelPostconditions.setHorizontalGroup(gl_panelPostconditions.createParallelGroup(
        Alignment.TRAILING).addGroup(
        gl_panelPostconditions
            .createSequentialGroup()
            .addGap(6)
            .addGroup(
                gl_panelPostconditions
                    .createParallelGroup(Alignment.LEADING)
                    .addComponent(scrollPanePostconditions, GroupLayout.DEFAULT_SIZE, 450,
                        Short.MAX_VALUE)
                    .addComponent(postconditionTextField, GroupLayout.DEFAULT_SIZE, 450,
                        Short.MAX_VALUE))
            .addGap(10)
            .addGroup(
                gl_panelPostconditions
                    .createParallelGroup(Alignment.TRAILING)
                    .addComponent(deletePostoconditions, GroupLayout.PREFERRED_SIZE, 50,
                        Short.MAX_VALUE)
                    .addGroup(
                        gl_panelPostconditions
                            .createSequentialGroup()
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(addPostconditions, GroupLayout.DEFAULT_SIZE, 50,
                                Short.MAX_VALUE))
                    .addComponent(editPostConditions, GroupLayout.DEFAULT_SIZE, 50,
                        Short.MAX_VALUE)).addGap(6)));
    gl_panelPostconditions.setVerticalGroup(gl_panelPostconditions.createParallelGroup(
        Alignment.LEADING).addGroup(
        gl_panelPostconditions
            .createSequentialGroup()
            .addGap(6)
            .addGroup(
                gl_panelPostconditions
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(addPostconditions, GroupLayout.PREFERRED_SIZE, 28,
                            GroupLayout.PREFERRED_SIZE)
                    .addComponent(postconditionTextField, GroupLayout.PREFERRED_SIZE,
                        28 , GroupLayout.PREFERRED_SIZE))
            .addGap(6)
            .addGroup(
                gl_panelPostconditions
                    .createParallelGroup(Alignment.LEADING)
                    .addGroup(
                        gl_panelPostconditions.createSequentialGroup()
                            .addComponent(editPostConditions, GroupLayout.PREFERRED_SIZE, 28,
                                    GroupLayout.PREFERRED_SIZE)
                            .addGap(6)
                            .addComponent(deletePostoconditions, GroupLayout.PREFERRED_SIZE, 28,
                                    GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollPanePostconditions, GroupLayout.PREFERRED_SIZE, 62,
                        GroupLayout.PREFERRED_SIZE))
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    postconditions = new JList();
    scrollPanePostconditions.setViewportView(postconditions);
    panelPostconditions.setLayout(gl_panelPostconditions);

    JPanel panelAlternativeFlow = new JPanel();
    panelAlternativeFlow.setBorder(new TitledBorder(new EmptyBorder(1,1,1,1),
        Msg.get("editUseCaseDialog.border.alternativeFlow"), TitledBorder.LEFT, TitledBorder.TOP, null, null));

    JButton editAlternativeFlowButton = new JButton(Msg.get("editUseCaseDialog.edit"));

    JScrollPane scrollPaneAlternativeFlow = new PDControlScrollPane();

    JButton deleteAlternativeFlowButton = new JButton(Msg.get("editUseCaseDialog.delete"));

    JButton addAlternativeFlowButton = new JButton(Msg.get("editUseCaseDialog.add"));
    addAlternativeFlowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onAddAlternativeFlow();
      }
    });
    editAlternativeFlowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onEditAlternativeFlow();
      }
    });
    deleteAlternativeFlowButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onDeleteAlternativeFlow();
      }
    });



    GroupLayout gl_panelAlternativeFlow = new GroupLayout(panelAlternativeFlow);
    gl_panelAlternativeFlow.setHorizontalGroup(gl_panelAlternativeFlow.createParallelGroup(
        Alignment.TRAILING)
        .addGroup(
            gl_panelAlternativeFlow
                .createSequentialGroup()
                .addGap(6)
                .addComponent(scrollPaneAlternativeFlow, GroupLayout.DEFAULT_SIZE, 450,
                    Short.MAX_VALUE)
                .addGap(10)
                .addGroup(
                    gl_panelAlternativeFlow
                        .createParallelGroup(Alignment.TRAILING)
                        .addComponent(editAlternativeFlowButton, Alignment.TRAILING,
                            GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                        .addComponent(deleteAlternativeFlowButton, GroupLayout.DEFAULT_SIZE, 50,
                            Short.MAX_VALUE)
                        .addComponent(addAlternativeFlowButton, GroupLayout.DEFAULT_SIZE,
                            50, Short.MAX_VALUE)
                    ).addGap(6)));
    gl_panelAlternativeFlow.setVerticalGroup(gl_panelAlternativeFlow.createParallelGroup(
        Alignment.LEADING).addGroup(
        gl_panelAlternativeFlow
            .createSequentialGroup()
            .addGap(11)
            .addGroup(
                gl_panelAlternativeFlow
                    .createParallelGroup(Alignment.BASELINE)
                    .addComponent(scrollPaneAlternativeFlow, GroupLayout.PREFERRED_SIZE, 150,
                        GroupLayout.PREFERRED_SIZE)
                    .addGroup(
                        gl_panelAlternativeFlow.createSequentialGroup()
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(addAlternativeFlowButton).addGap(7)
                            .addComponent(editAlternativeFlowButton).addGap(8)
                            .addComponent(deleteAlternativeFlowButton)
                            .addGap(0, 0, Short.MAX_VALUE)))
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    alternativeFlows = new JList();
    scrollPaneAlternativeFlow.setViewportView(alternativeFlows);
    panelAlternativeFlow.setLayout(gl_panelAlternativeFlow);
    GroupLayout gl_panel = new GroupLayout(panel);
    gl_panel.setHorizontalGroup(
      gl_panel.createParallelGroup(Alignment.TRAILING)
        .addGroup(gl_panel.createSequentialGroup()
          .addGap(6)
          .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
            .addComponent(mainEntityPanel, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
            .addComponent(panelAlternativeFlow, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
            .addComponent(panelPostconditions, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
            .addComponent(panelMainFlow, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
            .addComponent(panelPreconditions, GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
            .addGroup(gl_panel.createSequentialGroup()
                  .addComponent(mainActorsPanel, GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                  .addComponent(panelSecondaryActors, GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                  .addPreferredGap(ComponentPlacement.UNRELATED)
            )
            .addGroup(gl_panel.createSequentialGroup()
              .addGap(6)
              .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                .addComponent(lblDescription)
                .addComponent(lblName))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
                .addComponent(scrollPane)
                .addComponent(name, GroupLayout.DEFAULT_SIZE, 606, Short.MAX_VALUE))
              .addGap(8)))
          .addGap(31))
    );
    gl_panel.setVerticalGroup(
      gl_panel.createParallelGroup(Alignment.LEADING)
        .addGroup(gl_panel.createSequentialGroup()
          .addGap(12)
          .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblName)

            .addComponent(name, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(12)
          .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
            .addComponent(lblDescription)

            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
          .addGap(22)
                .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
               .addComponent(mainActorsPanel, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)

          .addComponent(panelSecondaryActors, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE))
                .addGap(10)
          .addComponent(panelPreconditions, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                .addGap(10)
          .addComponent(panelMainFlow, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)
          .addGap(10)
          .addComponent(panelPostconditions, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
          .addGap(10)
          .addComponent(panelAlternativeFlow, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE)
          .addGap(10)
          .addComponent(mainEntityPanel, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
          .addGap(10))
    );

    /* Main Entity */

    JScrollPane scrollPaneEntity = new PDControlScrollPane();
    comboMainEntity = new JComboBox();

    JButton selectMainEntity = new JButton(Msg.get("editUseCaseDialog.select"));
    selectMainEntity.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        onSelectMainEntity();
      }
    });


    GroupLayout gl_mainEntityPanel = new GroupLayout(mainEntityPanel);
    gl_mainEntityPanel.setHorizontalGroup(
      gl_mainEntityPanel.createParallelGroup(Alignment.TRAILING)
        .addGroup(gl_mainEntityPanel.createSequentialGroup()
          .addGap(6)
          .addComponent(scrollPaneEntity, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(gl_mainEntityPanel.createParallelGroup(Alignment.LEADING)
            .addComponent(selectMainEntity, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addComponent(comboMainEntity, 0, 180, Short.MAX_VALUE))
          .addGap(6))
    );
    gl_mainEntityPanel.setVerticalGroup(
      gl_mainEntityPanel.createParallelGroup(Alignment.LEADING)
          .addGroup(gl_mainEntityPanel.createParallelGroup(Alignment.LEADING)
            .addGroup(gl_mainEntityPanel.createSequentialGroup()
              .addComponent(comboMainEntity, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
              .addGap(6)
              .addComponent(selectMainEntity, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
            )
            .addComponent(scrollPaneEntity, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
          )
    );

    mainEntities = new JList();
    mainEntities.setVisibleRowCount(3);

    scrollPaneEntity.setViewportView(mainEntities);
    mainEntityPanel.setLayout(gl_mainEntityPanel);


    JScrollPane scrollPane_1 = new PDControlScrollPane();
    comboMainActor = new JComboBox();

    JButton addMainActor = new JButton(Msg.get("editUseCaseDialog.add"));
    addMainActor.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        onAddMainActor();
      }
    });

    JButton deleteMainActor = new JButton(Msg.get("editUseCaseDialog.delete"));
    deleteMainActor.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        onDeleteMainActor();
      }
    });
    GroupLayout gl_mainActorsPanel = new GroupLayout(mainActorsPanel);
    gl_mainActorsPanel.setHorizontalGroup(gl_mainActorsPanel
        .createParallelGroup(Alignment.TRAILING).addGroup(
            gl_mainActorsPanel
                .createSequentialGroup()
                .addGap(4)
                .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(
                    gl_mainActorsPanel
                        .createParallelGroup(Alignment.LEADING)
                        .addGroup(
                            gl_mainActorsPanel
                                .createSequentialGroup()
                                .addComponent(comboMainActor, GroupLayout.PREFERRED_SIZE, 130,
                                    GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(addMainActor, GroupLayout.DEFAULT_SIZE, 10,
                                    Short.MAX_VALUE))
                        .addComponent(deleteMainActor, GroupLayout.DEFAULT_SIZE, 140,
                            Short.MAX_VALUE)).addGap(6)));
    gl_mainActorsPanel.setVerticalGroup(gl_mainActorsPanel.createParallelGroup(Alignment.LEADING)
        .addGroup(
            gl_mainActorsPanel
                .createSequentialGroup()
                .addGap(6)
                .addGroup(
                    gl_mainActorsPanel
                        .createParallelGroup(Alignment.BASELINE)
                        .addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 62,
                            GroupLayout.PREFERRED_SIZE)
                        .addGroup(
                            gl_mainActorsPanel
                                .createSequentialGroup()
                                .addGroup(
                                    gl_mainActorsPanel
                                        .createParallelGroup(Alignment.BASELINE)
                                        .addComponent(comboMainActor, GroupLayout.PREFERRED_SIZE,
                                            28, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(addMainActor, GroupLayout.PREFERRED_SIZE,
                                                28, GroupLayout.PREFERRED_SIZE))
                                .addGap(6)
                                .addComponent(deleteMainActor)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    mainActors = new JList();
    mainActors.setVisibleRowCount(3);

    scrollPane_1.setViewportView(mainActors);
    mainActorsPanel.setLayout(gl_mainActorsPanel);
    panel.setLayout(gl_panel);
    getContentPane().setLayout(groupLayout);

  }




}
