package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.NameScope;

@SuppressWarnings("serial")
public class BioModelEditorApplicationPanel extends BioModelEditorApplicationRightSidePanel<Parameter> {
	private JCheckBox includeGlobalParametersCheckBox = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener {		
				
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == includeGlobalParametersCheckBox) {
				((ApplicationParameterTableMode)tableModel).setIncludeReactionParameters(includeGlobalParametersCheckBox.isSelected());
			}
		}
	}
	
	public BioModelEditorApplicationPanel() {
		super();
		initialize();
	}

	private void initialize(){
		includeGlobalParametersCheckBox = new JCheckBox("Show Reaction Parameters");
		includeGlobalParametersCheckBox.addActionListener(eventHandler);
		addNewButton.setText("Add New Global Parameter");
		
		setLayout(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,50,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(addNewButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(deleteButton, gbc);

		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(includeGlobalParametersCheckBox, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);		
		
		includeGlobalParametersCheckBox.addActionListener(eventHandler);

		table.setDefaultRenderer(NameScope.class, new DefaultScrollTableCellRenderer(){

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value instanceof NameScope) {
					NameScope nameScope = (NameScope)value;
					setText(nameScope.getConextDescription());
				}
				return this;
			}
			
		});
	}
	
	@Override
	protected void tableSelectionChanged() {
		int[] rows = table.getSelectedRows();
		if (rows == null) {
			return;
		}
		for (int r : rows) {
			if (r < tableModel.getDataSize()) {
				Parameter parameter = tableModel.getValueAt(r);
				if (parameter instanceof ModelParameter) {
					deleteButton.setEnabled(true);
					return;
				}
			}
		}
		deleteButton.setEnabled(false);
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
//		setTableSelections(selectedObjects, table, tableModel);		
		SimulationContext simulationContext = null;
		if (selectedObjects != null && selectedObjects.length == 1 && selectedObjects[0] instanceof SimulationContext) {
			simulationContext = (SimulationContext) selectedObjects[0];
		}
		setSimulationContext(simulationContext);
	}

	@Override
	protected BioModelEditorApplicationRightSideTableModel<Parameter> createTableModel() {		
		return new ApplicationParameterTableMode(table, false);
	}

	@Override
	protected void newButtonPressed() {
		ModelParameter modelParameter = simulationContext.getModel().createModelParameter();
		setTableSelections(new Object[]{modelParameter}, table, tableModel);
	}

	@Override
	protected void deleteButtonPressed() {
		try {
			int[] rows = table.getSelectedRows();
			if (rows == null) {
				return;
			}
			String deleteListText = "";
			ArrayList<ModelParameter> deleteList = new ArrayList<ModelParameter>();
			for (int r : rows) {
				if (r < tableModel.getDataSize()) {
					Parameter parameter = tableModel.getValueAt(r);
					if (parameter instanceof ModelParameter) {
						deleteList.add((ModelParameter)parameter);
						deleteListText += "\t" + ((ModelParameter)parameter).getName() + "\n"; 
					}
				}
			}	
			String confirm = PopupGenerator.showOKCancelWarningDialog(this, "You are going to delete the following global parameter(s):\n\n " + deleteListText + "\n Continue?");
			if (confirm.equals(UserMessage.OPTION_CANCEL)) {
				return;
			}
			for (ModelParameter param : deleteList) {
				simulationContext.getModel().removeModelParameter(param);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}		
	}
}
