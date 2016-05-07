package com.nilledom.conversion.dialog;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.nilledom.conversion.model.DiagramBuilder;
import com.nilledom.model.UmlPackage;
import com.nilledom.util.Msg;
import com.nilledom.util.StringHelper;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class DiagramResolverDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList allEntities;
    private JComboBox comboBox;
    private JTextField newDiagramField;
    private JButton newDiagramButton;
    private JButton addButton;
    private JList diagramEntities;
    private JButton removeButton;
    private JLabel description;
    private JLabel diagramLabel;
    private JLabel selectLabel;
    private JLabel allEntitiesLabel;
    private JButton deleteDiagram;
    private JButton renameButton;
    private JCheckBox mapToPkg;
    private JComboBox packagesBox;
    private Map<String, DiagramBuilder> diagramMap;
    private List<String> mainEntities;
    private List<UmlPackage> packages;
    private boolean canceled = false;

    public DiagramResolverDialog(Map<String, DiagramBuilder> diagramMap, List<String> mainEntities, List<UmlPackage> packages) {
        setContentPane(contentPane);
        setModal(true);
        this.diagramMap = diagramMap;
        this.mainEntities = mainEntities;
        this.packages = packages;
        getRootPane().setDefaultButton(buttonOK);
        setTitle(Msg.get("conversion.dialog.resolver.title"));

        DefaultListModel<String> allEntitiesModel = new DefaultListModel<>();
        for (int i = 0; i < mainEntities.size(); i++)
            allEntitiesModel.add(i, mainEntities.get(i));
        allEntities.setModel(allEntitiesModel);

        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        List<String> diagrams = new ArrayList<>(diagramMap.keySet());
        for (String diagram : diagrams)
            comboBoxModel.addElement(diagram);
        comboBoxModel.setSelectedItem(comboBoxModel.getElementAt(0));
        comboBox.setModel(comboBoxModel);


        DefaultListModel<String> diagramEntitiesModel = new DefaultListModel<>();
        List<String> selectedDiagramEntities = diagramMap.get(diagrams.get(0)).getMainEntities();
        for (int i = 0; i < selectedDiagramEntities.size(); i++)
            diagramEntitiesModel.add(i, selectedDiagramEntities.get(i));
        diagramEntities.setModel(diagramEntitiesModel);

        description.setText(Msg.get("conversion.dialog.resolver.description"));
        selectLabel.setText(Msg.get("conversion.dialog.resolver.select"));
        allEntitiesLabel.setText(Msg.get("conversion.dialog.resolver.allEntities"));
        diagramLabel.setText(Msg.get("conversion.dialog.resolver.diagramEntities"));

        addButton.setText(Msg.get("conversion.dialog.resolver.addButton"));
        removeButton.setText(Msg.get("conversion.dialog.resolver.removeButton"));
        newDiagramButton.setText(Msg.get("conversion.dialog.resolver.newDiagramButton"));
        renameButton.setText(Msg.get("conversion.dialog.resolver.renameButton"));
        deleteDiagram.setText(Msg.get("conversion.dialog.resolver.deleteDiagram"));
        mapToPkg.setText(Msg.get("conversion.dialog.resolver.pkgcheckbox"));

        boolean pkgRelated = diagramMap.get((String) comboBox.getSelectedItem()).isPackageRelated();

        packagesBox.setEnabled(pkgRelated);
        if (diagramMap.get(comboBoxModel.getSelectedItem()).getUmlPackage() != null) {
            DefaultComboBoxModel<UmlPackage> packageBoxModel = new DefaultComboBoxModel<>();
            packageBoxModel.addElement(diagramMap.get(comboBoxModel.getSelectedItem()).getUmlPackage());
            packagesBox.setModel(packageBoxModel);
            packagesBox.setSelectedItem(diagramMap.get(comboBoxModel.getSelectedItem()).getUmlPackage());
        }
        mapToPkg.setSelected(pkgRelated);
        if (packages.isEmpty())
            mapToPkg.setEnabled(false);
        if (diagramMap.keySet().size() == 1)
            deleteDiagram.setEnabled(false);

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
        newDiagramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNewDiagram();
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAdd();
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemove();

            }
        });
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSelect();
            }
        });
        deleteDiagram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDeleteDiagram();
            }
        });
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRename();
            }
        });
        pack();
        setResizable(false);
        mapToPkg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCheckbox();
            }


        });
        packagesBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPackageSelection();
            }
        });
    }

    private void onPackageSelection() {
        UmlPackage selectedPackage = (UmlPackage) packagesBox.getSelectedItem();
        String diagram = (String) comboBox.getSelectedItem();
        diagramMap.get(diagram).setUmlPackage(selectedPackage);
    }

    private void onCheckbox() {
        boolean checked = mapToPkg.isSelected();
        packagesBox.setEnabled(checked);
        if (diagramMap.get(comboBox.getSelectedItem()).getUmlPackage() != null)
            packagesBox.setSelectedItem(diagramMap.get(comboBox.getSelectedItem()).getUmlPackage());
        String diagram = (String) comboBox.getSelectedItem();
        diagramMap.get(diagram).setPackageRelated(checked);
        if (checked)
            diagramMap.get(diagram).setUmlPackage((UmlPackage) packagesBox.getSelectedItem());
        else
            diagramMap.get(diagram).setUmlPackage(null);
    }

    private void onRename() {
        String diagram = (String) comboBox.getSelectedItem();
        DiagramBuilder builder = diagramMap.get(diagram);

        String renameDiagram = newDiagramField.getText().trim();
        if (renameDiagram.isEmpty())
            return;
        Set<String> diagrams = diagramMap.keySet();
        if (diagrams.contains(renameDiagram))
            return;
        diagramMap.remove(diagram);
        builder.setName(renameDiagram);
        diagramMap.put(renameDiagram, builder);
        List<String> newDiagrams = new ArrayList<>(diagramMap.keySet());
        Collections.sort(newDiagrams);
        comboBox.setModel(new DefaultComboBoxModel(newDiagrams.toArray()));
        newDiagramField.setText("");
        comboBox.setSelectedItem(renameDiagram);

    }

    private void onDeleteDiagram() {
        if (diagramMap.keySet().size() == 1)
            return;
        String diagram = (String) comboBox.getSelectedItem();
        diagramMap.remove(diagram);
        ((DefaultComboBoxModel) comboBox.getModel()).removeElement(diagram);
        if (diagramMap.keySet().size() == 1)
            deleteDiagram.setEnabled(false);
    }

    private void onSelect() {

        String diagram = (String) comboBox.getSelectedItem();
        ((DefaultListModel) diagramEntities.getModel()).clear();
        for (String entity : diagramMap.get(diagram).getMainEntities())
            ((DefaultListModel) diagramEntities.getModel()).addElement(entity);

        mapToPkg.setSelected(diagramMap.get(diagram).isPackageRelated());
        packagesBox.setEnabled(diagramMap.get(diagram).isPackageRelated());
        List<UmlPackage> unselectedPackages = new ArrayList<>(packages);
        for (DiagramBuilder builder : diagramMap.values()) {
            if (builder.getUmlPackage() != null && !builder.getName().equals(diagram))
                unselectedPackages.remove(builder.getUmlPackage());
        }
        DefaultComboBoxModel<UmlPackage> packageBoxModel = new DefaultComboBoxModel<>();
        for (UmlPackage aPackage : unselectedPackages)
            packageBoxModel.addElement(aPackage);
        packagesBox.setModel(packageBoxModel);
        if (unselectedPackages.isEmpty()) {
            mapToPkg.setEnabled(false);
            packagesBox.setEnabled(false);
        } else {
            mapToPkg.setEnabled(true);
            packagesBox.setEnabled(mapToPkg.isSelected());
        }


        if (diagramMap.get(diagram).getUmlPackage() != null)
            packagesBox.setSelectedItem(diagramMap.get(diagram).getUmlPackage());

    }

    private void onRemove() {
        List<String> selected = diagramEntities.getSelectedValuesList();
        if (selected.isEmpty())
            return;
        for (String entity : selected) {
            ((DefaultListModel) diagramEntities.getModel()).removeElement(entity);
        }
        String diagram = (String) comboBox.getSelectedItem();
        diagramMap.get(diagram).getMainEntities().removeAll(selected);
    }

    private void onAdd() {

        List<String> newDiagramEntities = allEntities.getSelectedValuesList();
        if (newDiagramEntities.isEmpty())
            return;
        Enumeration enumeration = ((DefaultListModel) diagramEntities.getModel()).elements();
        while (enumeration.hasMoreElements()) {
            String entity = (String) enumeration.nextElement();
            if (newDiagramEntities.contains(entity))
                continue;
            newDiagramEntities.add(entity);
        }
        Collections.sort(newDiagramEntities);
        ((DefaultListModel) diagramEntities.getModel()).clear();
        for (String entity : newDiagramEntities)
            ((DefaultListModel) diagramEntities.getModel()).addElement(entity);

        String diagram = (String) comboBox.getSelectedItem();
        diagramMap.get(diagram).setMainEntities(newDiagramEntities);

    }

    private void onNewDiagram() {
        String diagram = newDiagramField.getText().trim();
        if (diagram.isEmpty())
            return;
        Set<String> diagrams = diagramMap.keySet();
        if (diagrams.contains(diagram))
            return;
        List<String> newDiagrams = new ArrayList<>(diagrams);
        newDiagrams.add(diagram);
        Collections.sort(newDiagrams);
        comboBox.setModel(new DefaultComboBoxModel(newDiagrams.toArray()));
        diagramMap.put(newDiagramField.getText(), new DiagramBuilder());
        newDiagramField.setText("");
        comboBox.setSelectedItem(diagram);
        mapToPkg.setSelected(diagramMap.get(newDiagramField.getText()).isPackageRelated());
        packagesBox.setEnabled(diagramMap.get(newDiagramField.getText()).isPackageRelated());

        if (diagramMap.get(comboBox.getSelectedItem()).getUmlPackage() != null)
            packagesBox.setSelectedItem(diagramMap.get(comboBox.getSelectedItem()).getUmlPackage());
        deleteDiagram.setEnabled(true);

    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
        canceled = true;
        dispose();
    }

    public boolean hasCanceled() {
        return canceled;
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
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(20, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
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
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel3.putClientProperty("html.disable", Boolean.FALSE);
        contentPane.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 300), null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel4.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        allEntities = new JList();
        allEntities.setDragEnabled(false);
        allEntities.setEnabled(true);
        allEntities.setFixedCellWidth(150);
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        allEntities.setModel(defaultListModel1);
        scrollPane1.setViewportView(allEntities);
        allEntitiesLabel = new JLabel();
        allEntitiesLabel.setText("Label");
        panel4.add(allEntitiesLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(12, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel5.add(spacer2, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel5.add(spacer3, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        newDiagramButton = new JButton();
        newDiagramButton.setText("Button");
        panel5.add(newDiagramButton, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newDiagramField = new JTextField();
        panel5.add(newDiagramField, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 25), new Dimension(150, -1), null, 0, false));
        comboBox = new JComboBox();
        panel5.add(comboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectLabel = new JLabel();
        selectLabel.setText("Label");
        panel5.add(selectLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addButton = new JButton();
        addButton.setText("Button");
        panel5.add(addButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeButton = new JButton();
        removeButton.setText("Button");
        panel5.add(removeButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteDiagram = new JButton();
        deleteDiagram.setText("Button");
        panel5.add(deleteDiagram, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        renameButton = new JButton();
        renameButton.setText("Button");
        panel5.add(renameButton, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapToPkg = new JCheckBox();
        mapToPkg.setText("CheckBox");
        panel5.add(mapToPkg, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        packagesBox = new JComboBox();
        panel5.add(packagesBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 1, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel6.add(scrollPane2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        diagramEntities = new JList();
        diagramEntities.setFixedCellWidth(150);
        final DefaultListModel defaultListModel2 = new DefaultListModel();
        diagramEntities.setModel(defaultListModel2);
        diagramEntities.setSelectionMode(2);
        scrollPane2.setViewportView(diagramEntities);
        diagramLabel = new JLabel();
        diagramLabel.setText("Label");
        panel6.add(diagramLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(20, 0, 20, 0), -1, -1));
        contentPane.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        description = new JLabel();
        description.setText("Label");
        panel7.add(description, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
