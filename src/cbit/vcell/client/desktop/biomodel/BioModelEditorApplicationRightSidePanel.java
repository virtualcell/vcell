package cbit.vcell.client.desktop.biomodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.EditorScrollTable;

import cbit.vcell.mapping.SimulationContext;

@SuppressWarnings("serial")
public abstract class BioModelEditorApplicationRightSidePanel<T> extends DocumentEditorSubPanel {
	protected static final String PROPERTY_NAME_SIMULATION_CONTEXT = "simulationContext";
	
	protected JButton addButton = null;
	protected JButton deleteButton = null;
	protected JButton searchButton = null;
	protected JButton showAllButton = null;
	protected EditorScrollTable table;
	protected BioModelEditorApplicationRightSideTableModel<T> tableModel = null;
	protected SimulationContext simulationContext;
	protected JTextField textFieldSearch = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == BioModelEditorApplicationRightSidePanel.this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXT)) {
				simulationContextChanged();
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addButton) {
				newButtonPressed();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			} else if (e.getSource() == searchButton || e.getSource() == textFieldSearch) {
				String text = textFieldSearch.getText();
				tableModel.setSearchText(text);
			} else if (e.getSource() == showAllButton) {
				textFieldSearch.setText(null);
				tableModel.setSearchText(null);
			}
		}
		
		public void valueChanged(ListSelectionEvent e) {
			if (simulationContext == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == table.getSelectionModel()) {
				tableSelectionChanged();
			}
			
		}
	}
	
	public BioModelEditorApplicationRightSidePanel() {
		super();
		addPropertyChangeListener(eventHandler);
		initialize();
	}

	private void initialize(){
		addButton = new JButton("New");
		deleteButton = new JButton("Delete");
		searchButton = new JButton("Search");
		showAllButton = new JButton("Show All");
		textFieldSearch = new JTextField(10);
		table = new EditorScrollTable();
		tableModel = createTableModel();
		table.setModel(tableModel);

		addButton.addActionListener(eventHandler);
		deleteButton.addActionListener(eventHandler);
		deleteButton.setEnabled(false);
		searchButton.addActionListener(eventHandler);
		showAllButton.addActionListener(eventHandler);
		textFieldSearch.addActionListener(eventHandler);
		table.getSelectionModel().addListSelectionListener(eventHandler);
	}
	
	public void setSimulationContext(SimulationContext newValue) {
		SimulationContext oldValue = simulationContext;
		simulationContext = newValue;		
		firePropertyChange(PROPERTY_NAME_SIMULATION_CONTEXT, oldValue, newValue);
	}
	
	protected abstract BioModelEditorApplicationRightSideTableModel<T> createTableModel();
	protected abstract void newButtonPressed();
	protected abstract void deleteButtonPressed();
	
	protected void simulationContextChanged() {
		tableModel.setSimulationContext(simulationContext);
	}

	protected void tableSelectionChanged() {
		int[] rows = table.getSelectedRows();
		deleteButton.setEnabled(rows != null && rows.length > 0 && (rows.length > 1 || rows[0] < tableModel.getDataSize()));
		setSelectedObjectsFromTable(table, tableModel);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, table, tableModel);		
	}
}
