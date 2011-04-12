package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.GuiConstants;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.NameScope;

@SuppressWarnings("serial")
public class BioModelParametersPanel extends DocumentEditorSubPanel {
	private JCheckBox globalParametersCheckBox = null;
	private JCheckBox applicationsCheckBox = null;
	private JCheckBox reactionsCheckBox = null;
	private JCheckBox constantsCheckBox = null;
	private JCheckBox functionsCheckBox = null;
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private JTabbedPane tabbedPane;
	private int selectedIndex = -1;
	private String selectedTabTitle = null;

	private JButton addNewButton = null;
	private JButton deleteButton = null;
	private EditorScrollTable parametersFunctionsTable;
	private BioModelParametersTableModel parametersFunctionsTableModel = null;
	private BioModel bioModel;
	private JTextField textFieldSearch = null;
	private JPanel parametersFunctionsPanel = null;
	private EditorScrollTable predefinedSymbolsTable;
	private PredefinedSymbolsTableModel predefinedSymbolsTableModel = null;
	private JPanel predefinedSymbolsPanel = null;
	
	private enum ParametersPanelTabID {
		parameters_functions("Parameters and Functions"),
		predefined("Predefined Constants and Math Functions");
		
		String title = null;
		ParametersPanelTabID(String name) {
			this.title = name;
		}
	}
	
	private class ParametersPanelTab {
		ParametersPanelTabID id;
		JComponent component = null;
		Icon icon = null;
		ParametersPanelTab(ParametersPanelTabID id, JComponent component, Icon icon) {
			this.id = id;
			this.component = component;
			this.icon = icon;
		}		
	}
	
	private class InternalEventHandler implements ActionListener, ChangeListener, ListSelectionListener, DocumentListener {		
				
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addNewButton) {
				newButtonPressed();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			} else if (e.getSource() == globalParametersCheckBox) {
				parametersFunctionsTableModel.setIncludeGlobal(globalParametersCheckBox.isSelected());
			} else if (e.getSource() == applicationsCheckBox) {
				parametersFunctionsTableModel.setIncludeApplications(applicationsCheckBox.isSelected());
			} else if (e.getSource() == reactionsCheckBox) {
				parametersFunctionsTableModel.setIncludeReactions(reactionsCheckBox.isSelected());
			} else if (e.getSource() == constantsCheckBox) {
				parametersFunctionsTableModel.setIncludeConstants(constantsCheckBox.isSelected());
			} else if (e.getSource() == functionsCheckBox) {
				parametersFunctionsTableModel.setIncludeFunctions(functionsCheckBox.isSelected());
			}
		}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == tabbedPane) {
				tabbedPaneSelectionChanged();
			}			
		}
		
		public void valueChanged(ListSelectionEvent e) {
			if (bioModel == null || e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == parametersFunctionsTable.getSelectionModel()) {
				tableSelectionChanged();
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
	}	
	
	public BioModelParametersPanel() {
		super();
		initialize();
	}

	private void initialize(){
		addNewButton = new JButton("Add New Global Parameter");
		addNewButton.addActionListener(eventHandler);
		deleteButton = new JButton("Delete Selected Global Parameter(s)");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(eventHandler);
		textFieldSearch = new JTextField(10);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		parametersFunctionsTable = new EditorScrollTable();
		parametersFunctionsTableModel = new BioModelParametersTableModel(parametersFunctionsTable);
		parametersFunctionsTable.setModel(parametersFunctionsTableModel);
			
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
		predefinedSymbolsTable = new EditorScrollTable();
		predefinedSymbolsTableModel = new PredefinedSymbolsTableModel(predefinedSymbolsTable);
		predefinedSymbolsTable.setModel(predefinedSymbolsTableModel);
		GuiUtils.flexResizeTableColumns(predefinedSymbolsTable);

		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(eventHandler);
		
		ParametersPanelTab parametersPanelTabs[] = new ParametersPanelTab[ParametersPanelTabID.values().length]; 
		parametersPanelTabs[ParametersPanelTabID.parameters_functions.ordinal()] = new ParametersPanelTab(ParametersPanelTabID.parameters_functions, getParametersFunctionsPanel(), null);
		parametersPanelTabs[ParametersPanelTabID.predefined.ordinal()] = new ParametersPanelTab(ParametersPanelTabID.predefined, getPredefinedSymbolsPanel(), null);
		
		for (ParametersPanelTab tab : parametersPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}
				
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints  gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,50,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(addNewButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(deleteButton, gbc);
				
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		parametersFunctionsTable.getSelectionModel().addListSelectionListener(eventHandler);
		parametersFunctionsTable.setDefaultRenderer(NameScope.class, new DefaultScrollTableCellRenderer(){

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
	
	private JPanel getPredefinedSymbolsPanel() {
		if (predefinedSymbolsPanel == null) {
			predefinedSymbolsPanel = new JPanel();
			predefinedSymbolsPanel.setLayout(new BorderLayout());
			
			predefinedSymbolsPanel.add(predefinedSymbolsTable.getEnclosingScrollPane(), BorderLayout.CENTER);
		}
		return predefinedSymbolsPanel;
	}
	private JPanel getParametersFunctionsPanel() {
		if (parametersFunctionsPanel == null) {
			parametersFunctionsPanel = new JPanel();
			parametersFunctionsPanel.setLayout(new GridBagLayout());
			
			int gridy = 0;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			JLabel label = new JLabel("Defined In:");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			parametersFunctionsPanel.add(label, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(globalParametersCheckBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(reactionsCheckBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(applicationsCheckBox, gbc);
			
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
			parametersFunctionsPanel.add(label, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 5;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(constantsCheckBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 6;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(functionsCheckBox, gbc);
			
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
			parametersFunctionsPanel.add(parametersFunctionsTable.getEnclosingScrollPane(), gbc);
		}
		return parametersFunctionsPanel;
	}
	
	protected void tableSelectionChanged() {
		setSelectedObjectsFromTable(parametersFunctionsTable, parametersFunctionsTableModel);
		int[] rows = parametersFunctionsTable.getSelectedRows();
		if (rows == null) {
			return;
		}
		for (int r : rows) {
			if (r < parametersFunctionsTableModel.getRowCount()) {
				Parameter parameter = parametersFunctionsTableModel.getValueAt(r);
				if (parameter instanceof ModelParameter) {
					deleteButton.setEnabled(true);
					return;
				}
			}
		}
		deleteButton.setEnabled(false);
	}

	protected void newButtonPressed() {
		ModelParameter modelParameter = bioModel.getModel().createModelParameter();
		setTableSelections(new Object[]{modelParameter}, parametersFunctionsTable, parametersFunctionsTableModel);
	}

	protected void deleteButtonPressed() {
		try {
			int[] rows = parametersFunctionsTable.getSelectedRows();
			if (rows == null) {
				return;
			}
			String deleteListText = "";
			ArrayList<ModelParameter> deleteList = new ArrayList<ModelParameter>();
			for (int r : rows) {
				if (r < parametersFunctionsTableModel.getRowCount()) {
					Parameter parameter = parametersFunctionsTableModel.getValueAt(r);
					if (parameter instanceof ModelParameter) {
						deleteList.add((ModelParameter)parameter);
						deleteListText += "\t" + ((ModelParameter)parameter).getName() + "\n"; 
					}
				}
			}	
			String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting global parameters", "You are going to delete the following global parameter(s):\n\n " + deleteListText + "\n Continue?");
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
	
	private BioModelEditorRightSideTableModel<?> currentSelectedTableModel = null;
	private void computeCurrentSelectedTable() {
		currentSelectedTableModel = null;
		int selectedIndex = tabbedPane.getSelectedIndex();
		if (selectedIndex == ParametersPanelTabID.parameters_functions.ordinal()) {
			currentSelectedTableModel = parametersFunctionsTableModel;
		} else if (selectedIndex == ParametersPanelTabID.predefined.ordinal()) {
			currentSelectedTableModel = predefinedSymbolsTableModel;			
		}
	}

	private void searchTable() {		
		String text = textFieldSearch.getText();
		computeCurrentSelectedTable();
		currentSelectedTableModel.setSearchText(text);
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, parametersFunctionsTable, parametersFunctionsTableModel);
	}
	
	public void tabbedPaneSelectionChanged() {
		int oldSelectedIndex = selectedIndex;
		selectedIndex = tabbedPane.getSelectedIndex();
		if (selectedIndex < 0) {
			return;
		}
		if (oldSelectedIndex == selectedIndex) {
			return;
		}
		if (oldSelectedIndex >= 0) {
			tabbedPane.setTitleAt(oldSelectedIndex, selectedTabTitle);
		}
		selectedTabTitle = tabbedPane.getTitleAt(selectedIndex);
		tabbedPane.setTitleAt(selectedIndex, "<html><b>" + selectedTabTitle + "</b></html>");

		ActiveView activeView = null;
		if (selectedIndex == ParametersPanelTabID.parameters_functions.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.BIOMODEL_PARAMETERS_NODE, ActiveViewID.parameters_functions);
		} else if (selectedIndex == ParametersPanelTabID.predefined.ordinal()) {
			activeView = new ActiveView(null, DocumentEditorTreeFolderClass.BIOMODEL_PARAMETERS_NODE, ActiveViewID.predefined_symbols);
		}
		if (activeView != null) {
			setActiveView(activeView);
		}
		
		if (selectedIndex == ParametersPanelTabID.parameters_functions.ordinal()) {
			addNewButton.setVisible(true);
			deleteButton.setVisible(true);
		} else {
			addNewButton.setVisible(false);
			deleteButton.setVisible(false);			
		}
	}
	
	public void setBioModel(BioModel newValue) {
		if (newValue == bioModel) {
			return;
		}
		bioModel = newValue;		
		parametersFunctionsTableModel.setBioModel(bioModel);
	}
	
	@Override
	protected void onActiveViewChange(ActiveView activeView) {
		super.onActiveViewChange(activeView);
		if (DocumentEditorTreeFolderClass.BIOMODEL_PARAMETERS_NODE == activeView.getDocumentEditorTreeFolderClass()) {
			if (activeView.getActiveViewID() != null) {
				if (activeView.getActiveViewID().equals(ActiveViewID.parameter_estimation)) {
					tabbedPane.setSelectedIndex(ParametersPanelTabID.parameters_functions.ordinal());
				}
			}
		}
	}
	
	@Override
	public void setIssueManager(IssueManager issueManager) {
		super.setIssueManager(issueManager);
		parametersFunctionsTableModel.setIssueManager(issueManager);
	}
}