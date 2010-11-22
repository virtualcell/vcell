package cbit.vcell.client.desktop.biomodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;

@SuppressWarnings("serial")
public abstract class BioModelEditorRightSidePanel<T> extends JPanel {
	protected static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	
	protected JButton addButton = null;
	protected JButton deleteButton = null;
	protected EditorScrollTable table;
	protected BioModelEditorRightSideTableModel<T> tableModel = null;
	protected BioModel bioModel;
	protected JTextField textFieldSearch = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, DocumentListener, ListSelectionListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorRightSidePanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				tableModel.setModel(bioModel.getModel());
			}
		}
		
		public void insertUpdate(DocumentEvent e) {
			searchTable();
		}

		public void removeUpdate(DocumentEvent e) {
			searchTable();
		}

		public void changedUpdate(DocumentEvent e) {
			searchTable();
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addButton) {
				newButtonPressed();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			}
		}
		
		public void valueChanged(ListSelectionEvent e) {
			if (bioModel == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				int[] rows = table.getSelectedRows();
				deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || rows[0] < bioModel.getModel().getNumSpeciesContexts()));
			}
			
		}
	}
	
	public BioModelEditorRightSidePanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}

	private void initialize(){
		addButton = new JButton("New");
		deleteButton = new JButton("Delete");
		textFieldSearch = new JTextField(10);
		table = new EditorScrollTable();
		
		addButton.addActionListener(eventHandler);
		deleteButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		table.getSelectionModel().addListSelectionListener(eventHandler);
	}
	
	public void setBioModel(BioModel newValue) {
		BioModel oldValue = bioModel;
		bioModel = newValue;		
		firePropertyChange(PROPERTY_NAME_BIO_MODEL, oldValue, newValue);
	}
	
	private void searchTable() {
		String text = textFieldSearch.getText();
		tableModel.setSearchText(text);
	}
	
	protected abstract void newButtonPressed();
	protected abstract void deleteButtonPressed();

	public boolean isTableEditing() {
		return table.getCellEditor() != null;
	}
	
	public boolean stopTableEditing() {
		if (table.getCellEditor() != null) {
			return table.getCellEditor().stopCellEditing();
		}
		return true;
	}
}
