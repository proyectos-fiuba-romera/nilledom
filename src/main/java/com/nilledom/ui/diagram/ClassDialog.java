package com.nilledom.ui.diagram;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.nilledom.model.*;
import com.nilledom.ui.AppFrame;
import com.nilledom.umldraw.clazz.ClassElement;
import com.nilledom.util.Msg;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ClassDialog extends JDialog {
    private static final int CLASS_INDEX = 0;
    private static final int MEMBERS_INDEX = 1;
    private DefaultTableModel stereotypeTableModel;
    private UmlClass classModel;
    private ClassElement classElement;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel footer;
    private JPanel mainPanel;
    private JTabbedPane tabbedPanel;
    private JPanel classPanel;
    private JPanel membersPanel;
    private JPanel methodsPanel;
    private JPanel namePanel;
    private JLabel classNameLabel;
    private JTextField classNameField;
    private JCheckBox classAbstractCheckbox;
    private JPanel stereotypePanel;
    private JButton addStereotypeButton;
    private JButton removeStereotypeButton;
    private JPanel stereotypesLeft;
    private JPanel stereotypesRight;
    private JButton upStereotypeButton;
    private JButton downStereotypeButton;
    private JPanel docPanel;
    private JTextArea documentationArea;
    private JTable attributesTable;
    private JButton addAttributeButton;
    private JButton deleteAttributeButton;
    private JButton moveUpAttributeButton;
    private JButton moveDownAttributeButton;
    private JPanel attributesPanel;
    private JTable methodsTable;
    private JButton addMethodButton;
    private JButton deleteMethodButton;
    private JButton moveUpMethodButton;
    private JButton moveDownMethodButton;
    private JPanel attributesButtonPanel;
    private JPanel attributesTablePanel;
    private JPanel methodsTablePanel;
    private JPanel methodsButtonPanel;
    private JTable stereotypeTable;
    private JScrollPane stereotypeScroll;
    private boolean ok = false;


    public ClassDialog(Window parent, ClassElement aClassElement) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setContentPane(contentPane);
        setModal(true);
        this.classElement = aClassElement;
        this.classModel = (UmlClass) aClassElement.getModelElement();
        getRootPane().setDefaultButton(buttonOK);

        tabbedPanel.setTitleAt(CLASS_INDEX, Msg.get("classeditor.tab.class"));

        tabbedPanel.setTitleAt(MEMBERS_INDEX, Msg.get("classeditor.tab.members"));

        classNameLabel.setText(Msg.get("classeditor.className"));

        classAbstractCheckbox.setText(Msg.get("classeditor.isAbstract"));

        ((TitledBorder) stereotypePanel.getBorder()).setTitle(Msg.get("classeditor.stereotypes"));

        addStereotypeButton.setText(Msg.get("classeditor.add"));

        removeStereotypeButton.setText(Msg.get("classeditor.remove"));

        upStereotypeButton.setText(Msg.get("classeditor.moveUp"));

        downStereotypeButton.setText(Msg.get("classeditor.moveDown"));

        ((TitledBorder) docPanel.getBorder()).setTitle(Msg.get("classeditor.documentation"));

        stereotypeTableModel = new DefaultTableModel();
        stereotypeTableModel.addColumn("");
        for (int i = 0; i < classModel.getStereotypes().size(); i++) {
            UmlStereotype umlStereotype = classModel.getStereotypes().get(i);
            stereotypeTableModel.addRow(new Object[]{umlStereotype.getName().substring(2, umlStereotype.getName().length() - 2)});
        }
        stereotypeTable.setModel(stereotypeTableModel);

        stereotypeTable.setTableHeader(null);

        classNameField.setText(classModel.getName());
        classAbstractCheckbox.setSelected(classModel.isAbstract());
        documentationArea.setText(classModel.getDocumentation());

        /////////////////////  ATTRIBUTES  //////////////////////////

        ((TitledBorder) attributesPanel.getBorder()).setTitle(Msg.get("classeditor.attributes"));

        String typeAttColumnName = Msg.get("classeditor.type");
        String nameAttColumnName = Msg.get("classeditor.nameColumn");
        String visibleAttColumnName = Msg.get("classeditor.visible");

        final DefaultTableModel attributesTableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int index) {
                if (index == 2)
                    return Boolean.class;

                return String.class;
            }
        };

        attributesTableModel.addColumn(typeAttColumnName);
        attributesTableModel.addColumn(nameAttColumnName);
        attributesTableModel.addColumn(visibleAttColumnName);

        attributesTable.setModel(attributesTableModel);
        attributesTable.getColumn(typeAttColumnName).setPreferredWidth(100);
        attributesTable.getColumn(nameAttColumnName).setPreferredWidth(400);
        attributesTable.getColumn(visibleAttColumnName).setPreferredWidth(60);

        List<UmlAttribute> attributes = classModel.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            Object[] row = new Object[3];
            row[0] = attributes.get(i).getName().split(":")[1];
            row[1] = attributes.get(i).getName().split(":")[0].substring(1);
            row[2] = classElement.getAttributesVisibility().get(i);
            attributesTableModel.addRow(row);
        }

        addAttributeButton.setText(Msg.get("classeditor.add"));
        deleteAttributeButton.setText(Msg.get("classeditor.remove"));
        moveUpAttributeButton.setText(Msg.get("classeditor.moveUp"));
        moveDownAttributeButton.setText(Msg.get("classeditor.moveDown"));

        /////////////////////  METHODS  //////////////////////////

        ((TitledBorder) methodsPanel.getBorder()).setTitle(Msg.get("classeditor.methods"));

        String returnTypeColumnName = Msg.get("classeditor.returnType");
        String nameMethodColumnName = Msg.get("classeditor.nameColumn");
        String argsMethodColumnName = Msg.get("classeditor.arguments");
        String visibleMethodColumnName = Msg.get("classeditor.visible");

        final DefaultTableModel methodsTableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int index) {
                if (index == 3)
                    return Boolean.class;

                return String.class;
            }
        };

        methodsTableModel.addColumn(returnTypeColumnName);
        methodsTableModel.addColumn(nameMethodColumnName);
        methodsTableModel.addColumn(argsMethodColumnName);
        methodsTableModel.addColumn(visibleMethodColumnName);

        methodsTable.setModel(methodsTableModel);
        methodsTable.getColumn(returnTypeColumnName).setPreferredWidth(80);
        methodsTable.getColumn(nameMethodColumnName).setPreferredWidth(120);
        methodsTable.getColumn(argsMethodColumnName).setPreferredWidth(300);
        methodsTable.getColumn(visibleMethodColumnName).setPreferredWidth(60);

        List<UmlMethod> methods = classModel.getMethods();
        for (int i = 0; i < methods.size(); i++) {
            Object[] row = new Object[4];
            String[] splitted = methods.get(i).getName().split(":");
            row[0] = splitted[splitted.length - 1];
            row[1] = methods.get(i).getName().split("\\(")[0].substring(1);
            String args = methods.get(i).getName().split("[\\(\\)]")[1];
            row[2] = args == null ? "" : args;
            row[3] = classElement.getMethodVisibility().get(i);
            methodsTableModel.addRow(row);
        }


        addMethodButton.setText(Msg.get("classeditor.add"));
        deleteMethodButton.setText(Msg.get("classeditor.remove"));
        moveUpMethodButton.setText(Msg.get("classeditor.moveUp"));
        moveDownMethodButton.setText(Msg.get("classeditor.moveDown"));


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

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
        addStereotypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stereotypeTableModel.addRow(new Object[]{"new stereotype"});
                stereotypeTable.setRowSelectionInterval(stereotypeTableModel.getRowCount() - 1, stereotypeTableModel.getRowCount() - 1);
            }
        });
        removeStereotypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFromTable(stereotypeTable);
            }
        });
        upStereotypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveUpFromTable(stereotypeTable);
            }
        });
        downStereotypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveDownFromTable(stereotypeTable);
            }
        });
        addAttributeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attributesTableModel.addRow(new Object[]{"int", "newAttribute", true});
            }
        });
        deleteAttributeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFromTable(attributesTable);
            }
        });
        moveUpAttributeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveUpFromTable(attributesTable);

            }
        });
        moveDownAttributeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveDownFromTable(attributesTable);
            }
        });
        addMethodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                methodsTableModel.addRow(new Object[]{"int", "newMethod", "value1:int,value2:int", true});
            }
        });
        deleteMethodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFromTable(methodsTable);
            }
        });
        moveUpMethodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveUpFromTable(methodsTable);
            }
        });
        moveDownMethodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveDownFromTable(methodsTable);
            }
        });
        pack();
    }

    private void deleteFromTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selected = table.getSelectedRow();
        if (selected == -1)
            return;
        model.removeRow(selected);

    }

    private void moveUpFromTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selected = table.getSelectedRow();
        if (selected == -1 || selected == 0)
            return;
        Vector rows = model.getDataVector();
        Object rowSelected = rows.get(selected);
        Object rowAbove = rows.get(selected - 1);
        rows.setElementAt(rowSelected, selected - 1);
        rows.setElementAt(rowAbove, selected);
        table.setRowSelectionInterval(selected - 1, selected - 1);
    }

    private void moveDownFromTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selected = table.getSelectedRow();
        if (selected == -1 || selected == model.getRowCount() - 1)
            return;
        Vector rows = model.getDataVector();
        Object rowSelected = rows.get(selected);
        Object rowBelow = rows.get(selected + 1);
        rows.setElementAt(rowSelected, selected + 1);
        rows.setElementAt(rowBelow, selected);
        table.setRowSelectionInterval(selected + 1, selected + 1);

    }

    private void onOK() {
        ArrayList<UmlAttribute> attributes = new ArrayList<>();
        ArrayList<Boolean> attributesVisibility = new ArrayList<>();
        ArrayList<UmlMethod> methods = new ArrayList<>();
        ArrayList<Boolean> methodsVisbility = new ArrayList<>();
        Vector attributesMatrix = ((DefaultTableModel) attributesTable.getModel()).getDataVector();
        for (Vector row : (Vector<Vector>) attributesMatrix) {
            UmlAttribute attribute = (UmlAttribute) UmlAttribute.getPrototype().clone();
            String attName;
            if (row.get(1) == null || ((String) row.get(1)).isEmpty() || row.get(0) == null || ((String) row.get(0)).isEmpty())
                continue;
            attName = "-" + row.get(1) + ":" + row.get(0);
            attribute.setName(attName);
            attributes.add(attribute);
            attributesVisibility.add((Boolean) row.get(2));
        }
        Vector methodsMatrix = ((DefaultTableModel) methodsTable.getModel()).getDataVector();
        for (Vector row : (Vector<Vector>) methodsMatrix) {
            UmlMethod method = (UmlMethod) UmlMethod.getPrototype().clone();
            String methodName;
            if (row.get(1) == null || ((String) row.get(1)).isEmpty())
                continue;

            methodName = "+" + row.get(1) + "(";
            if (row.get(2) != null)
                methodName += row.get(2);
            if (row.get(0) == null || ((String) row.get(0)).isEmpty())
                methodName += "):void";
            else
                methodName += "):" + row.get(0);
            method.setName(methodName);
            methods.add(method);
            methodsVisbility.add((Boolean) row.get(3));
        }

        classModel.setAttributes(attributes);
        classModel.setMethods(methods);
        classElement.setAttributeVisibility(attributesVisibility);
        classElement.setMethodVisibility(methodsVisbility);
        classModel.setName(classNameField.getText());
        classModel.setAbstract(classAbstractCheckbox.isSelected());

        ArrayList<UmlStereotype> stereotypes = new ArrayList<>();
        for (int i = 0; i < stereotypeTableModel.getRowCount(); i++) {
            UmlStereotype stereotype = (UmlStereotype) UmlStereotype.getPrototype().clone();
            stereotype.setName("<<" + ((Vector<Vector>) stereotypeTableModel.getDataVector()).elementAt(i).elementAt(0) + ">>");
            stereotypes.add(stereotype);
        }
        classModel.setStereotypes(stereotypes);
        classModel.setDocumentation(documentationArea.getText());
        ok = true;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        ClassElement element = (ClassElement) ClassElement.getPrototype().clone();
        UmlModelElement model = (UmlModelElement) UmlBoundary.getPrototype().clone();
        model.setName("MiClase");
        element.setModelElement(model);
        ClassDialog dialog = new ClassDialog(AppFrame.get(), element);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
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
        footer = new JPanel();
        footer.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(footer, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        footer.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        footer.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel1.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel1.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(mainPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(700, 500), null, 0, false));
        tabbedPanel = new JTabbedPane();
        mainPanel.add(tabbedPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        classPanel = new JPanel();
        classPanel.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        tabbedPanel.addTab("Untitled", classPanel);
        namePanel = new JPanel();
        namePanel.setLayout(new GridLayoutManager(1, 3, new Insets(20, 20, 20, 20), -1, -1));
        classPanel.add(namePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        namePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(namePanel.getFont().getName(), namePanel.getFont().getStyle(), namePanel.getFont().getSize()), new Color(-4473925)));
        classNameLabel = new JLabel();
        classNameLabel.setText("Label");
        namePanel.add(classNameLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        classNameField = new JTextField();
        namePanel.add(classNameField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        classAbstractCheckbox = new JCheckBox();
        classAbstractCheckbox.setText("CheckBox");
        namePanel.add(classAbstractCheckbox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stereotypePanel = new JPanel();
        stereotypePanel.setLayout(new GridLayoutManager(1, 2, new Insets(10, 20, 10, 20), -1, -1));
        stereotypePanel.setEnabled(true);
        classPanel.add(stereotypePanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 100), null, 0, false));
        stereotypePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-16777216)));
        stereotypesLeft = new JPanel();
        stereotypesLeft.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        stereotypePanel.add(stereotypesLeft, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 70), new Dimension(-1, 70), 0, false));
        stereotypeScroll = new JScrollPane();
        stereotypeScroll.setAutoscrolls(true);
        stereotypesLeft.add(stereotypeScroll, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 60), new Dimension(-1, 60), 0, false));
        stereotypeTable = new JTable();
        stereotypeTable.setShowVerticalLines(false);
        stereotypeScroll.setViewportView(stereotypeTable);
        stereotypesRight = new JPanel();
        stereotypesRight.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        stereotypePanel.add(stereotypesRight, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, 60), new Dimension(-1, 60), 0, false));
        addStereotypeButton = new JButton();
        addStereotypeButton.setText("Button");
        stereotypesRight.add(addStereotypeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeStereotypeButton = new JButton();
        removeStereotypeButton.setText("Button");
        stereotypesRight.add(removeStereotypeButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        upStereotypeButton = new JButton();
        upStereotypeButton.setText("Button");
        stereotypesRight.add(upStereotypeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        downStereotypeButton = new JButton();
        downStereotypeButton.setText("Button");
        stereotypesRight.add(downStereotypeButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        docPanel = new JPanel();
        docPanel.setLayout(new GridLayoutManager(1, 1, new Insets(10, 20, 10, 20), -1, -1));
        classPanel.add(docPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        docPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JScrollPane scrollPane1 = new JScrollPane();
        docPanel.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        documentationArea = new JTextArea();
        scrollPane1.setViewportView(documentationArea);
        membersPanel = new JPanel();
        membersPanel.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        tabbedPanel.addTab("Untitled", membersPanel);
        attributesPanel = new JPanel();
        attributesPanel.setLayout(new GridLayoutManager(2, 2, new Insets(5, 5, 5, 5), -1, -1));
        membersPanel.add(attributesPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        attributesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "title"));
        attributesTablePanel = new JPanel();
        attributesTablePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        attributesPanel.add(attributesTablePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        attributesTablePanel.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        attributesTable = new JTable();
        attributesTable.setCellSelectionEnabled(true);
        attributesTable.setEditingRow(-1);
        attributesTable.setFocusCycleRoot(false);
        scrollPane2.setViewportView(attributesTable);
        attributesButtonPanel = new JPanel();
        attributesButtonPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        attributesPanel.add(attributesButtonPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(100, -1), null, 0, false));
        addAttributeButton = new JButton();
        addAttributeButton.setText("Button");
        attributesButtonPanel.add(addAttributeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        attributesButtonPanel.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        deleteAttributeButton = new JButton();
        deleteAttributeButton.setText("Button");
        attributesButtonPanel.add(deleteAttributeButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        moveUpAttributeButton = new JButton();
        moveUpAttributeButton.setText("Button");
        attributesButtonPanel.add(moveUpAttributeButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        moveDownAttributeButton = new JButton();
        moveDownAttributeButton.setText("Button");
        attributesButtonPanel.add(moveDownAttributeButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        methodsPanel = new JPanel();
        methodsPanel.setLayout(new GridLayoutManager(1, 2, new Insets(5, 5, 5, 5), -1, -1));
        membersPanel.add(methodsPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        methodsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "title"));
        methodsTablePanel = new JPanel();
        methodsTablePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        methodsPanel.add(methodsTablePanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane3 = new JScrollPane();
        methodsTablePanel.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        methodsTable = new JTable();
        scrollPane3.setViewportView(methodsTable);
        methodsButtonPanel = new JPanel();
        methodsButtonPanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        methodsPanel.add(methodsButtonPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(100, -1), null, 0, false));
        addMethodButton = new JButton();
        addMethodButton.setText("Button");
        methodsButtonPanel.add(addMethodButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteMethodButton = new JButton();
        deleteMethodButton.setText("Button");
        methodsButtonPanel.add(deleteMethodButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        moveUpMethodButton = new JButton();
        moveUpMethodButton.setText("Button");
        methodsButtonPanel.add(moveUpMethodButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        moveDownMethodButton = new JButton();
        moveDownMethodButton.setText("Button");
        methodsButtonPanel.add(moveDownMethodButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        methodsButtonPanel.add(spacer3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
