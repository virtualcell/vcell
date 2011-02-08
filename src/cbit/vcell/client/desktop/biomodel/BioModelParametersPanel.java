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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.NameScope;

@SuppressWarnings("serial")
public class BioModelParametersPanel extends BioModelEditorRightSidePanel<ApplicationParameter> {
	private JCheckBox includeGlobalParametersCheckBox = null;
	private JCheckBox includeSimlationContextsCheckBox = null;
	private JCheckBox includeReactionsCheckBox = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	
	private class InternalEventHandler implements ActionListener {		
				
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == includeGlobalParametersCheckBox) {
				((BioModelParametersTableMode)tableModel).setIncludeGlobalParameters(includeGlobalParametersCheckBox.isSelected());
			} else if (e.getSource() == includeSimlationContextsCheckBox) {
				((BioModelParametersTableMode)tableModel).setIncludeSimulationContextParameters(includeSimlationContextsCheckBox.isSelected());
			} else if (e.getSource() == includeReactionsCheckBox) {
				((BioModelParametersTableMode)tableModel).setIncludeReactionParameters(includeReactionsCheckBox.isSelected());
			}
		}
	}
	
	public BioModelParametersPanel() {
		super();
		initialize();
	}

	private void initialize(){
		addNewButton.setText("Add New Global Parameter");
		includeGlobalParametersCheckBox = new JCheckBox("Global Parameters");
		includeGlobalParametersCheckBox.setSelected(true);
		includeGlobalParametersCheckBox.addActionListener(eventHandler);
		includeReactionsCheckBox = new JCheckBox("Reaction Parameters");
		includeReactionsCheckBox.setSelected(true);
		includeReactionsCheckBox.addActionListener(eventHandler);
		includeSimlationContextsCheckBox = new JCheckBox("Parameters from All Applications");
		includeSimlationContextsCheckBox.addActionListener(eventHandler);

		setLayout(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints  gbc = new GridBagConstraints();
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
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel tabPanel = new JPanel();
		tabPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		tabbedPane.addTab("<html><b>BioModel Parameters</b></html>", tabPanel);
		add(tabbedPane, gbc);
		
		tabPanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(includeGlobalParametersCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(includeReactionsCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(includeSimlationContextsCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(table.getEnclosingScrollPane(), gbc);		

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
		super.tableSelectionChanged();
		int[] rows = table.getSelectedRows();
		if (rows == null) {
			return;
		}
		for (int r : rows) {
			if (r < tableModel.getDataSize()) {
				ApplicationParameter appParameter = tableModel.getValueAt(r);
				if (appParameter.getParameter() instanceof ModelParameter) {
					deleteButton.setEnabled(true);
					return;
				}
			}
		}
		deleteButton.setEnabled(false);
	}

	@Override
	protected BioModelEditorRightSideTableModel<ApplicationParameter> createTableModel() {		
		return new BioModelParametersTableMode(table);
	}

	@Override
	protected void newButtonPressed() {
		ModelParameter modelParameter = bioModel.getModel().createModelParameter();
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
					ApplicationParameter appParameter = tableModel.getValueAt(r);
					Parameter parameter = appParameter.getParameter();
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
				bioModel.getModel().removeModelParameter(param);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}		
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setSelectedObjectsFromTable(table, tableModel);
	}
}
