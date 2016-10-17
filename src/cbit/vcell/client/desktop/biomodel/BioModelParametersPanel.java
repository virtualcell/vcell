/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.VCAssert;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.JTabbedPaneEnhanced;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.EditableSymbolTableEntry;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.NameScope;

@SuppressWarnings("serial")
public class BioModelParametersPanel extends DocumentEditorSubPanel {
	
	public static class ApplicationSelection {
		boolean isAll;
		SimulationContext simulationContext = null;
		
		public static ApplicationSelection All = new ApplicationSelection();

		private ApplicationSelection(){
			isAll = true;
			simulationContext = null;
		}
		private ApplicationSelection(SimulationContext simContext){
			isAll = false;
			simulationContext = simContext;
		}
		
		public boolean isAll(){
			return isAll;
		}
		
		public SimulationContext getSimulationContext(){
			return simulationContext;
		}
		
		@Override
		public int hashCode() {
			return Boolean.hashCode(isAll)+((simulationContext!=null)?(simulationContext.hashCode()):(0));
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ApplicationSelection){
				ApplicationSelection other = (ApplicationSelection) obj;
				return (isAll == other.isAll) && (simulationContext == other.simulationContext);
			}
			return false;
		}

		public String toString(){
			if (isAll){
				return "All Applications";
			}else{
				return simulationContext.getName();
			}
		}
	}
	
	public class ApplicationComboBoxModel implements ComboBoxModel, PropertyChangeListener {
		ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();
		ApplicationSelection selectedApplicationSelection = null;
		ArrayList<ApplicationSelection> applicationSelectionList = new ArrayList<ApplicationSelection>();
		private BioModel bioModel = null;
		
		public void update(){
			if (bioModel==null){
				selectedApplicationSelection = null;
				applicationSelectionList.clear();
			}else {
				ArrayList<ApplicationSelection> desiredItems = new ArrayList<ApplicationSelection>();
				desiredItems.add(ApplicationSelection.All);
				for (SimulationContext simContext : bioModel.getSimulationContexts()){
					desiredItems.add(new ApplicationSelection(simContext));
				}
				applicationSelectionList.retainAll(desiredItems);
				for (ApplicationSelection desiredItem : desiredItems){
					if (!applicationSelectionList.contains(desiredItem)){
						applicationSelectionList.add(desiredItem);
					}
				}
				if (selectedApplicationSelection == null || !applicationSelectionList.contains(selectedApplicationSelection)){
					selectedApplicationSelection = applicationSelectionList.get(0);
				}
			}
			ListDataEvent e = new ListDataEvent(applicationComboBox,ListDataEvent.CONTENTS_CHANGED,0,Math.max(0,applicationSelectionList.size()-1));
			for (ListDataListener l : listeners){
				l.contentsChanged(e);
			}
		}
		
		public void setBioModel(BioModel bioModel){
			if (this.bioModel!=null){
				this.bioModel.removePropertyChangeListener(this);
				for (SimulationContext sc : this.bioModel.getSimulationContexts()){
					sc.removePropertyChangeListener(this);
				}
			}
			this.bioModel = bioModel;
			if (this.bioModel!=null){
				this.bioModel.addPropertyChangeListener(this);
				for (SimulationContext sc : this.bioModel.getSimulationContexts()){
					sc.addPropertyChangeListener(this);
				}
			}
			update();
		}
		
		@Override
		public int getSize() {
			return applicationSelectionList.size();
		}

		@Override
		public ApplicationSelection getElementAt(int index) {
			return applicationSelectionList.get(index);
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}

		@Override
		public void setSelectedItem(Object anItem) {
			if (anItem instanceof ApplicationSelection){
				ApplicationSelection item = (ApplicationSelection) anItem;
				if (applicationSelectionList.contains(item)){
					selectedApplicationSelection = item;
				}
			}
		}

		@Override
		public ApplicationSelection getSelectedItem() {
			return selectedApplicationSelection;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == bioModel && evt.getPropertyName().equals("simulationContexts")){
				for (SimulationContext sc : this.bioModel.getSimulationContexts()){
					sc.removePropertyChangeListener(this);
					sc.addPropertyChangeListener(this);
				}
				update();
			}
			if (evt.getSource() instanceof SimulationContext){
				update();
			}
		}
		
	}
	
	private JCheckBox globalParametersCheckBox = null;
	private JCheckBox applicationsCheckBox = null;
	private JCheckBox reactionsCheckBox = null;
	private JCheckBox constantsCheckBox = null;
	private JCheckBox functionsCheckBox = null;
	private JComboBox<ApplicationSelection> applicationComboBox = null;
	private ApplicationComboBoxModel applicationComboBoxModel = new ApplicationComboBoxModel();
	private InternalEventHandler eventHandler = new InternalEventHandler();
	private JTabbedPane tabbedPane;

	private JButton changeUnitsButton = null;
	private BioModelWindowManager bioModelWindowManager = null;
	
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
	private EditorScrollTable modelUnitSystemTable;
	private ModelUnitSystemTableModel modelUnitSystemTableModel = null;
	private JPanel modelUnitSystemPanel = null;
	
	private enum ParametersPanelTabID {
		parameters_functions("Parameters and Functions"),
		predefined("Predefined Constants and Math Functions"),
		modelUnitSystem("Model Unit System");
		
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
			} else if (e.getSource() == changeUnitsButton) {
				changeUnitsButtonPressed();
			} else if (e.getSource() == globalParametersCheckBox) {
				parametersFunctionsTableModel.setIncludeGlobal(globalParametersCheckBox.isSelected());
			} else if (e.getSource() == applicationsCheckBox) {
				parametersFunctionsTableModel.setIncludeApplications(applicationsCheckBox.isSelected());
				applicationComboBox.setEnabled(applicationsCheckBox.isSelected());
			} else if (e.getSource() == applicationComboBox) {
				parametersFunctionsTableModel.setApplicationSelection((ApplicationSelection)applicationComboBox.getSelectedItem());
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

	public void changeUnitsButtonPressed() {
		UnitSystemSelectionPanel unitSystemSelectionPanel = new UnitSystemSelectionPanel();
		unitSystemSelectionPanel.initialize(bioModel.getModel().getUnitSystem());
		int retcode = DialogUtils.showComponentOKCancelDialog(this, unitSystemSelectionPanel, "select new unit system");
		while (retcode == JOptionPane.OK_OPTION){
			ModelUnitSystem forcedModelUnitSystem;
			try {
				forcedModelUnitSystem = unitSystemSelectionPanel.createModelUnitSystem();
				BioModel newBioModel = ModelUnitConverter.createBioModelWithNewUnitSystem(bioModel, forcedModelUnitSystem);
				this.bioModelWindowManager.resetDocument(newBioModel);
				break;
			} catch (Exception e) {
				e.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, e.getMessage(), e);
				retcode = DialogUtils.showComponentOKCancelDialog(this, unitSystemSelectionPanel, "select new unit system");
			}
		}
	}
	
	private void initialize(){
		addNewButton = new JButton("New Global Parameter");
		addNewButton.addActionListener(eventHandler);
		deleteButton = new JButton("Delete Global Parameter(s)");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(eventHandler);

		changeUnitsButton = new JButton("Change Unit System");
		changeUnitsButton.addActionListener(eventHandler);
		
		textFieldSearch = new JTextField(10);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		textFieldSearch.putClientProperty("JTextField.variant", "search");
		parametersFunctionsTable = new EditorScrollTable();
		parametersFunctionsTableModel = new BioModelParametersTableModel(parametersFunctionsTable);
		parametersFunctionsTable.setModel(parametersFunctionsTableModel);
			
		globalParametersCheckBox = new JCheckBox("Global");
		globalParametersCheckBox.setSelected(true);
		globalParametersCheckBox.addActionListener(eventHandler);
		reactionsCheckBox = new JCheckBox("Reactions and Rules");
		reactionsCheckBox.setSelected(true);
		reactionsCheckBox.addActionListener(eventHandler);
		applicationsCheckBox = new JCheckBox("Applications");
		applicationsCheckBox.setSelected(true);
		applicationsCheckBox.addActionListener(eventHandler);
		applicationComboBox = new JComboBox<ApplicationSelection>();
		applicationComboBox.setSelectedItem("All");
		applicationComboBox.addActionListener(eventHandler);
		applicationComboBox.setModel(applicationComboBoxModel);
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
		modelUnitSystemTable = new EditorScrollTable();
		modelUnitSystemTableModel = new ModelUnitSystemTableModel(modelUnitSystemTable);
		modelUnitSystemTable.setModel(modelUnitSystemTableModel);
		GuiUtils.flexResizeTableColumns(modelUnitSystemTable);

		tabbedPane = new JTabbedPaneEnhanced();
		tabbedPane.addChangeListener(eventHandler);
		
		ParametersPanelTab parametersPanelTabs[] = new ParametersPanelTab[ParametersPanelTabID.values().length]; 
		parametersPanelTabs[ParametersPanelTabID.parameters_functions.ordinal()] = new ParametersPanelTab(ParametersPanelTabID.parameters_functions, getParametersFunctionsPanel(), null);
		parametersPanelTabs[ParametersPanelTabID.predefined.ordinal()] = new ParametersPanelTab(ParametersPanelTabID.predefined, getPredefinedSymbolsPanel(), null);
		parametersPanelTabs[ParametersPanelTabID.modelUnitSystem.ordinal()] = new ParametersPanelTab(ParametersPanelTabID.modelUnitSystem, getModelUnitSystemPanel(), null);
		
		for (ParametersPanelTab tab : parametersPanelTabs) {
			tab.component.setBorder(GuiConstants.TAB_PANEL_BORDER);
			tabbedPane.addTab(tab.id.title, tab.icon, tab.component);
		}
				
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints  gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,4,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(addNewButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(deleteButton, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		buttonPanel.add(changeUnitsButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,50,4,4);
		buttonPanel.add(new JLabel("Search"), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		buttonPanel.add(textFieldSearch, gbc);
		
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
		{ //make double click on units panel bring up editing box
			JPanel p = getModelUnitSystemPanel();
			VCAssert.assertValid(p);
			EditorScrollTable est = GuiUtils.findFirstChild(p, EditorScrollTable.class);
			VCAssert.assertValid(est);
			MouseListener ml = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						changeUnitsButtonPressed();
					}
				}
			};
			est.addMouseListener(ml);
		}
	}
	
	private JPanel getModelUnitSystemPanel() {
		if (modelUnitSystemPanel == null) {
			modelUnitSystemPanel = new JPanel();
			modelUnitSystemPanel.setLayout(new BorderLayout());
			
			modelUnitSystemPanel.add(modelUnitSystemTable.getEnclosingScrollPane(), BorderLayout.CENTER);
		}
		return modelUnitSystemPanel;
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
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(applicationComboBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 5;
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
			gbc.gridx = 6;
			gbc.gridy = gridy;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.LINE_END;
			parametersFunctionsPanel.add(constantsCheckBox, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 7;
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
			gbc.gridwidth = 8;
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
				EditableSymbolTableEntry parameter = parametersFunctionsTableModel.getValueAt(r);
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
					EditableSymbolTableEntry parameter = parametersFunctionsTableModel.getValueAt(r);
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
		} else if (selectedIndex == ParametersPanelTabID.modelUnitSystem.ordinal()) {
			currentSelectedTableModel = modelUnitSystemTableModel;			
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
		int selectedIndex = tabbedPane.getSelectedIndex();
		if (selectedIndex < 0) {
			return;
		}

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
		if (selectedIndex == ParametersPanelTabID.modelUnitSystem.ordinal()) {
			changeUnitsButton.setVisible(true);
		} else {
			changeUnitsButton.setVisible(false);
		}
	}
	
	public void setBioModel(BioModel newValue) {
		if (newValue == bioModel) {
			return;
		}
		bioModel = newValue;		
		parametersFunctionsTableModel.setBioModel(bioModel);
		predefinedSymbolsTableModel.setBioModel(bioModel);
		modelUnitSystemTableModel.setBioModel(bioModel);
		applicationComboBoxModel.setBioModel(bioModel);
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

	public void setBioModelWindowManager(BioModelWindowManager bioModelWindowManager) {
		this.bioModelWindowManager = bioModelWindowManager;
	}
}
