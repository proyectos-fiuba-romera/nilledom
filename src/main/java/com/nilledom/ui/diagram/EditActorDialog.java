package com.nilledom.ui.diagram;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import com.nilledom.model.UmlActor;
import com.nilledom.umldraw.usecase.ActorElement;

import java.awt.Dimension;

/**
 * An edit dialog for actors.
 *
 * @author Fernando Romera Ferrio
 * @version 1.0
 */

public class EditActorDialog extends javax.swing.JDialog {

    private ActorElement actor;
    private JTextField actorName;
    private boolean isOk;
    private JTextPane actorDescription;

    /**
     * Creates new form EditActorDialog
     *
     * @param parent  the parent frame
     * @param anActor the edited Actor
     * @param modal   whether the dialog is to be modal
     */
    public EditActorDialog(java.awt.Window parent, ActorElement anActor, boolean modal) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setSize(new Dimension(475, 268));
        setTitle("Edit actor properties");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        actor = anActor;
        initComponents();
        myPostInit();
    }

    private void myPostInit() {
        UmlActor umlActor = (UmlActor) actor.getModelElement();
        actorName.setText(umlActor.getName());
        actorDescription.setText(umlActor.getDescription());

    }

    public boolean isOk() {
        return isOk;
    }

    public String getName() {
        return actorName.getText();
    }

    public String getDescription() {
        return actorDescription.getText();
    }

    private void initComponents() {
        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                isOk = true;
                dispose();
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isOk = false;
                dispose();
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
            groupLayout.createSequentialGroup().addGap(341).addComponent(btnOk)
                .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnCancel))
            .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 471, GroupLayout.PREFERRED_SIZE));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
            groupLayout.createSequentialGroup()
                .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 204,
                    GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnOk)
                    .addComponent(btnCancel)).addGap(86)));

        JPanel panel = new JPanel();
        tabbedPane.addTab("Properties", null, panel, null);

        JLabel lblNewLabel = new JLabel("Description:");

        JLabel lblNombre = new JLabel("Name:");

        JScrollPane scrollPane = new JScrollPane();

        actorName = new JTextField();
        actorName.setColumns(10);
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup().addContainerGap()
                .addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
                    .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 446,
                        Short.MAX_VALUE).addComponent(lblNewLabel, Alignment.LEADING)
                    .addGroup(Alignment.LEADING,
                        gl_panel.createSequentialGroup().addComponent(lblNombre)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(actorName, GroupLayout.DEFAULT_SIZE, 405,
                                Short.MAX_VALUE))).addContainerGap()));
        gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
            gl_panel.createSequentialGroup().addContainerGap().addGroup(
                gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(lblNombre)
                    .addComponent(actorName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(lblNewLabel).addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap()));

        actorDescription = new JTextPane();
        scrollPane.setViewportView(actorDescription);
        panel.setLayout(gl_panel);
        getContentPane().setLayout(groupLayout);
        //pack();

    }
}
