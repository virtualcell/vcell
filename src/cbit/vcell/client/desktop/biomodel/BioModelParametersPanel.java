package cbit.vcell.client.desktop.biomodel;

import java.awt.Component;
import java.awt.Font;
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
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.NameScope;

@SuppressWarnings("serial")
public class BioModelParametersPanel extends BioModelEditorRightSidePanel<Parameter> {
	private JCheckBox globalParametersCheckBox = null;
	private JCheckBox applicationsCheckBox = null;
	private JCheckBox reactionsCheckBox = null;
	private JCheckBox constantsCheckBox = null;
	private JCheckBox functionsCheckBox = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private JTabbedPane tabbedPane;
	private int selectedIndex = -1;
	private String selectedTabTitle = null;

	private class InternalEventHandler implements ActionListener, ChangeListener {		
				
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == globalParametersCheckBox) {
				((BioModelParametersTableMode)tableModel).setIncludeGlobal(globalParametersCheckBox.isSelected());
			} else if (e.getSource() == applicationsCheckBox) {
				((BioModelParametersTableMode)tableModel).setIncludeApplications(applicationsCheckBox.isSelected());
			} else if (e.getSource() == reactionsCheckBox) {
				((BioModelParametersTableMode)tableModel).setIncludeReactions(reactionsCheckBox.isSelected());
			} else if (e.getSource() == constantsCheckBox) {
				((BioModelParametersTableMode)tableModel).setIncludeConstants(constantsCheckBox.isSelected());
			} else if (e.getSource() == functionsCheckBox) {
				((BioModelParametersTableMode)tableModel).setIncludeFunctions(functionsCheckBox.isSelected());
			}
		}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == tabbedPane) {
				tabbedPaneSelectionChanged();
			}			
		}
	}
	
	public BioModelParametersPanel() {
		super();
		initialize();
	}

	private void initialize(){
		addNewButton.setText("Add New Global Parameter");
		globalParametersCheckBox = new JCheckBox("Global");
		globalParametersCheckBox.setSelected(true);
		globalParametersCheckBox.addActionListener(eventHandler);
		reactionsCheckBox = new JCheckBox("Reactions");
		reactionsCheckBox.setSelected(true);
		reactionsCheckBox.addActionListener(eventHandler);
		applicationsCheckBox = new JCheckBox("Applications");
		applicationsCheckBox.setSelected(true);
		applicationsCheckBox.addActionListener(eventHandler);
		constantsCheckBox = new JCheckBox("Parameters");
		constantsCheckBox.setSelected(true);
		constantsCheckBox.addActionListener(eventHandler);
		functionsCheckBox = new JCheckBox("Functions");
		functionsCheckBox.setSelected(true);
		functionsCheckBox.addActionListener(eventHandler);

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
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(eventHandler);
		JPanel tabPanel = getParametersPanel();
		tabPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		tabbedPane.addTab("Parameters and Functions", tabPanel);
		add(tabbedPane, gbc);	

		table.setDefaultRenderer(NameScope.class, new DefaultScrollTableCellRenderer(){

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				if (value instanceof NameScope) {
					NameScope nameScope = (NameScope)value;
					setText(nameScope.getPathDescription());
				}
				return this;
			}
			
		});
	}
	
	private JPanel getParametersPanel() {
		JPanel tabPanel = new JPanel();
		tabPanel.setLayout(new GridBagLayout());
		
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		JLabel label = new JLabel("Defined In:");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		tabPanel.add(label, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(globalParametersCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(reactionsCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(applicationsCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		label = new JLabel("Type:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		tabPanel.add(label, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 5;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(constantsCheckBox, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 6;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(functionsCheckBox, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 7;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		tabPanel.add(table.getEnclosingScrollPane(), gbc);		
		return tabPanel;
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
	protected BioModelEditorRightSideTableModel<Parameter> createTableModel() {		
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
				bioModel.getModel().removeModelParameter(param);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}		
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, table, tableModel);
	}
	
	public void tabbedPaneSelectionChanged() {
		int oldSelectedIndex = selectedIndex;
		selectedIndex = tabbedPane.getSelectedIndex();
		if (oldSelectedIndex == selectedIndex) {
			return;
		}
		if (oldSelectedIndex >= 0) {
			tabbedPane.setTitleAt(oldSelectedIndex, selectedTabTitle);
		}
		selectedTabTitle = tabbedPane.getTitleAt(selectedIndex);
		tabbedPane.setTitleAt(selectedIndex, "<html><b>" + selectedTabTitle + "</b></html>");
	}
}
