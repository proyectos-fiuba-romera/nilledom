package com.nilledom.ui.diagram;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.nilledom.model.Flow;
import com.nilledom.model.UmlStep;

public class EditListAction extends AbstractAction {


  private JList<String> list;

  private JPopupMenu editPopup;
  private JTextField editTextField;
  private Class<?> modelClass;

  private int row;

  public EditListAction(String text) {
    super(text);
    setModelClass(DefaultListModel.class);
  }

  protected void setModelClass(Class modelClass) {
    this.modelClass = modelClass;
  }

  protected void applyValueToModel(String value, ListModel<String> model, int row) {
    DefaultListModel<String> dlm = (DefaultListModel<String>) model;
    if (value.isEmpty()) {
      dlm.remove(row);
    } else {
      dlm.set(row, value);
    }

  }

  /*
   * Display the popup editor when requested
   */
  @SuppressWarnings("unchecked")
  public void actionPerformed(ActionEvent e) {
    ListModel<String> model = list.getModel();

    if (model.getSize() == 0)
      return;

    if (!modelClass.isAssignableFrom(model.getClass()))
      return;

    // Do a lazy creation of the popup editor

    if (editPopup == null)
      createEditPopup();

    // Position the popup editor over top of the selected row

    row = list.getSelectedIndex();

    if (row == -1) {
      row = model.getSize() - 1;
    }

    Rectangle r = list.getCellBounds(row, row);

    String selectValue = model.getElementAt(row);

    editPopup.setPreferredSize(new Dimension(r.width, r.height));
    editPopup.show(list, r.x, r.y);

    // Prepare the text field for editing

    editTextField.setText(selectValue);
    editTextField.selectAll();
    editTextField.requestFocusInWindow();
  }

  /*
   * Create the popup editor
   */
  private void createEditPopup() {
    // Use a text field as the editor

    editTextField = new JTextField();
    Border border = UIManager.getBorder("List.focusCellHighlightBorder");
    editTextField.setBorder(border);

    // Add an Action to the text field to save the new value to the model

    editTextField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String value = editTextField.getText();
        ListModel<String> model = list.getModel();
        applyValueToModel(value, model, row);
        editPopup.setVisible(false);
      }
    });

    // Add the editor to the popup

    editPopup = new JPopupMenu();
    editPopup.setBorder(new EmptyBorder(0, 0, 0, 0));
    editPopup.add(editTextField);
  }

  public void setList(JList<String> list) {
    this.list = list;
  }
}
