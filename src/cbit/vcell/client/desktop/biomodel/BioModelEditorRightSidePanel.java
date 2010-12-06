package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.BioModelEditor.SelectionEvent;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public abstract class BioModelEditorRightSidePanel<T> extends JPanel implements Model.Owner {
	protected static final String PROPERTY_NAME_BIO_MODEL = "bioModel";
	
	protected JButton newButton = null;
	protected JButton deleteButton = null;
	protected EditorScrollTable table;
	protected BioModelEditorRightSideTableModel<T> tableModel = null;
	protected BioModel bioModel;
	protected JTextField textFieldSearch = null;
	protected SelectionEvent selectionEvent = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, DocumentListener, ListSelectionListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorRightSidePanel.this && evt.getPropertyName().equals(PROPERTY_NAME_BIO_MODEL)) {
				bioModelChange();
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
			if (e.getSource() == newButton) {
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
				tableSelectionChanged();
			}
		}
	}
	
	public BioModelEditorRightSidePanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}

	private void initialize(){
		newButton = new JButton("New");
		deleteButton = new JButton("Delete");
		textFieldSearch = new JTextField(10);
		table = new EditorScrollTable();
		tableModel = createTableModel();
		table.setModel(tableModel);
		table.setDefaultRenderer(Structure.class, new DefaultScrollTableCellRenderer(){

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value instanceof Structure) {
					setText(((Structure)value).getName());
				}
				return this;
			}			
		});
		
		newButton.addActionListener(eventHandler);
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
	
	public Model getModel() {
		return bioModel.getModel();
	}
	
	private void searchTable() {
		String text = textFieldSearch.getText();
		tableModel.setSearchText(text);
	}
	
	protected abstract BioModelEditorRightSideTableModel<T> createTableModel();
	protected abstract void newButtonPressed();
	protected abstract void deleteButtonPressed();
	
	protected void bioModelChange() {
		tableModel.setBioModel(bioModel);
	}

	protected void tableSelectionChanged() {
		int[] rows = table.getSelectedRows();
		deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || rows[0] < tableModel.getDataSize()));
		if (rows != null && rows.length == 1 && rows[0] < tableModel.getDataSize()) {
			setSelectionEvent(new SelectionEvent(tableModel.containedByModel() ? bioModel.getModel() : bioModel, tableModel.getValueAt(rows[0])));
		} else {
			setSelectionEvent(new SelectionEvent(tableModel.containedByModel() ? bioModel.getModel() : bioModel, null));
		}
	}
	
	public void select(T selection) {
		for (int i = 0; i < tableModel.getDataSize(); i ++) {
			if (tableModel.getValueAt(i) == selection) {
				table.changeSelection(i, 0, false, false);
				break;
			}
		}
	}

	public final void setSelectionEvent(SelectionEvent newValue) {
		SelectionEvent oldValue = this.selectionEvent;
		this.selectionEvent = newValue;
		firePropertyChange(BioModelEditor.PROPERTY_NAME_SELECTION_EVENT, oldValue, newValue);
	}
}
