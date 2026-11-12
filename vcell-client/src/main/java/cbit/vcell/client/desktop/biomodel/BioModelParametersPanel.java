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

import cbit.vcell.client.laf.VCellFonts;
import cbit.vcell.client.laf.VCellColors;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.GuiUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.client.BioModelWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.mapping.AssignmentRule;
import cbit.vcell.mapping.RateRule;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.SimulationContextParameter;
import cbit.vcell.model.EditableSymbolTableEntry;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;

@SuppressWarnings("serial")
public class BioModelParametersPanel extends DocumentEditorSubPanel {
	
	Icon downArrow = null;
	
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
	
	public class ApplicationComboBoxRenderer implements ListCellRenderer<Object> {
		
		@Override
		public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			
			JLabel label = new JLabel();
			ApplicationSelection as = (ApplicationSelection)value;
			SimulationContext simContext = as.getSimulationContext();
			Icon icon = null;
			int left = 34;
			if(simContext != null) {
				left = 1;
				if(simContext.isRuleBased()) {
					if(simContext.getGeometry().getDimension() == 0) {
						icon = VCellIcons.appRbmNonspIcon;
					}
				} else if(simContext.isStoch()) {
					if(simContext.getGeometry().getDimension() == 0) {
						icon = VCellIcons.appStoNonspIcon;
					} else {
						icon = VCellIcons.appStoSpatialIcon;
					}
				} else if(simContext.getApplicationType() == Application.SPRINGSALAD) {
					icon = VCellIcons.appSpringSaLaDSpatialIcon;
				} else {
					if(simContext.getGeometry().getDimension() == 0) {
						icon = VCellIcons.appDetNonspIcon;
					} else {
						icon = VCellIcons.appDetSpatialIcon;
					}
				}
			}
			label.setText(as.toString());
			label.setIcon(icon);
			label.setBorder(new EmptyBorder(1, left, 1, 1));
			return label;
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
				if( bioModel.getNumSimulationContexts() == 0) {
					addNewButton2.setEnabled(false);
					downArrow = null;
					addNewButton2.setIcon(downArrow);
				} else if (bioModel.getNumSimulationContexts() == 1) {
					addNewButton2.setEnabled(true);
					downArrow = null;
					addNewButton2.setIcon(downArrow);
				} else {
					addNewButton2.setEnabled(true);
					downArrow = new DownArrowIcon();
					addNewButton2.setIcon(downArrow);
				}
				
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
				filterButton.setApplications(java.util.Arrays.asList(bioModel.getSimulationContexts())); // to choose among in Filters
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
	
	// what the table lists, chosen from a drop-down (the checkboxes it replaced crowded the panel)
	private final ParametersFilterButton filterButton = new ParametersFilterButton(this::applyFilter);
	private final JLabel shownLabel = new JLabel();
	private final javax.swing.JToggleButton columnsButton = new javax.swing.JToggleButton("Columns \u25B8"); // opens the column arrangement beside the table
	private org.vcell.util.gui.TableColumnsPanel columnsPanel = null;
	private JComboBox<ApplicationSelection> applicationComboBox = null; // not shown: its model tracks the applications
	private ApplicationComboBoxModel applicationComboBoxModel = new ApplicationComboBoxModel();
	private InternalEventHandler eventHandler = new InternalEventHandler();

	private final UnitSystemChooserPanel unitSystemChooserPanel = new UnitSystemChooserPanel(UnitSystemLibrary.getShared(), this::convertUnits);
	private JButton unitSystemButton = null; // opens the Model Unit System window
	private static final String UNIT_SYSTEM_WINDOW = "ModelUnitSystemWindow"; // its window, among the model window's children
	private JPanel buttonPanel = null;
	private BioModelWindowManager bioModelWindowManager = null;
	
	private JButton addNewButton = null;
	private JButton addNewButton2 = null;
	private JButton deleteButton = null;
	private EditorScrollTable parametersFunctionsTable;
	private BioModelParametersTableModel parametersFunctionsTableModel = null;
	private BioModel bioModel;
	private JTextField textFieldSearch = null;
	private JPanel parametersFunctionsPanel = null;
	private EditorScrollTable modelUnitSystemTable;
	private ModelUnitSystemTableModel modelUnitSystemTableModel = null;
	private JPanel modelUnitSystemPanel = null;
	
	private class InternalEventHandler implements ActionListener, ChangeListener, ListSelectionListener, DocumentListener {		
				
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addNewButton) {
				newGlobalButtonPressed();
			} else if (e.getSource() == addNewButton2) {
				newApplicationButtonPressed();
			} else if (e.getSource() == deleteButton) {
				deleteButtonPressed();
			} else if (e.getSource() == unitSystemButton) {
				showModelUnitSystem();
			}
		}

		public void stateChanged(ChangeEvent e) {
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

	// the unit system chooser's conversion: the model, rescaled to these units, replaces the one open
	private boolean convertUnits(ModelUnitSystem units) {
		try {
			BioModel newBioModel = ModelUnitConverter.createBioModelWithNewUnitSystem(bioModel, units);
			this.bioModelWindowManager.resetDocument(newBioModel); // closes the model window's children, this one's among them
			SwingUtilities.invokeLater(this::showModelUnitSystem); // so it's opened again, on the converted model
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this, "This model couldn't be converted to those units: " + e.getMessage(), e);
			return false;
		}
	}
	
	private void initialize() {
		addNewButton = new JButton("New Global Parameter");
		addNewButton.addActionListener(eventHandler);
		addNewButton2 = new JButton("New Application Parameter");
//		addNewButton2.setIcon(downArrow);
		addNewButton2.setHorizontalTextPosition(SwingConstants.LEFT);
		DownArrowIcon.keepPushButtonLook(addNewButton2); // it gets the menu arrow when there are several applications
		addNewButton2.addActionListener(eventHandler);
		deleteButton = new JButton("Delete");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(eventHandler);

		textFieldSearch = new JTextField(10);
		textFieldSearch.getDocument().addDocumentListener(eventHandler);
		textFieldSearch.putClientProperty("JTextField.variant", "search");
		textFieldSearch.setName("ParametersSearchTextField");
		textFieldSearch.setToolTipText("Search the table showing by name, expression or description");
		parametersFunctionsTable = new EditorScrollTable();
		parametersFunctionsTable.setName("ParametersFunctionsTable");
		parametersFunctionsTable.getAccessibleContext().setAccessibleName("Parameters and Functions");
		parametersFunctionsTableModel = new BioModelParametersTableModel(parametersFunctionsTable);
		parametersFunctionsTable.setModel(parametersFunctionsTableModel);
			
		applicationComboBox = new JComboBox<ApplicationSelection>();
		applicationComboBox.setSelectedItem("All");
		applicationComboBox.addActionListener(eventHandler);
		applicationComboBox.setModel(applicationComboBoxModel);
		applicationComboBox.setRenderer(new ApplicationComboBoxRenderer());
		applicationComboBox.setName("ParametersApplicationComboBox");
		applicationComboBox.setToolTipText("Which applications' parameters to list");
		
		modelUnitSystemTable = new EditorScrollTable();
		modelUnitSystemTable.setName("ModelUnitSystemTable");
		modelUnitSystemTable.getAccessibleContext().setAccessibleName("Model Unit System");
		modelUnitSystemTableModel = new ModelUnitSystemTableModel(modelUnitSystemTable);
		modelUnitSystemTable.setModel(modelUnitSystemTableModel);
		GuiUtils.flexResizeTableColumns(modelUnitSystemTable);

		unitSystemButton = new JButton("Model Unit System...");
		unitSystemButton.setName("ModelUnitSystemButton");
		unitSystemButton.setToolTipText("The model's units: see them, or choose another unit system");
		unitSystemButton.addActionListener(eventHandler);
		shownLabel.setName("ParametersShownLabel");

		// a row that wraps rather than insisting on its width, which would squeeze the navigation column beside it
		buttonPanel = org.vcell.util.gui.ButtonRowLayout.withSearch(java.util.List.of(addNewButton, addNewButton2, deleteButton, unitSystemButton), textFieldSearch);
		
		setLayout(new BorderLayout());
		add(getParametersFunctionsPanel(), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		applyFilter(filterButton.getFilter());

		parametersFunctionsTable.getSelectionModel().addListSelectionListener(eventHandler);
		parametersFunctionsTableModel.addTableModelListener(e -> updateShownLabel());
		parametersFunctionsTable.setDefaultRenderer(NameScope.class, new DefaultScrollTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (value instanceof NameScope) {
					NameScope nameScope = (NameScope)value;
					String text = nameScope.getPathDescription();
					setText(text);
				}
				return this;
			}
			
		});
		
		DefaultScrollTableCellRenderer nameTableCellRenderer = new DefaultScrollTableCellRenderer() {
			int PADDING = 3;
			
			@Override
			public void setBounds(int x, int y, int width, int height) {
				super.setBounds(x, y, width, height);
				if (getIcon() != null) {
					int textWidth = getFontMetrics(getFont()).stringWidth(getText());
					Insets insets = getInsets();
					// TODO: check that the text width is not bigger than the cell width and truncate the name
					int iconTextGap = width - textWidth - getIcon().getIconWidth() - insets.left - insets.right - PADDING;
					setIconTextGap(iconTextGap);
				} else {
				setIconTextGap(0);
				}
			}
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if(table.getModel() instanceof BioModelParametersTableModel) {
					BioModelParametersTableModel tableModel = (BioModelParametersTableModel)table.getModel();
					Object selectedObject = tableModel.getValueAt(row);
					setToolTipText(null);
					if(selectedObject instanceof ModelParameter || selectedObject instanceof SpeciesContext) {
						SymbolTableEntry ste = (SymbolTableEntry)selectedObject;
						
						RateRule rr = null;
						AssignmentRule ar = null;
						for (SimulationContext simContext : bioModel.getSimulationContexts()) {
							rr = simContext.getRateRule(ste);
							ar = simContext.getAssignmentRule(ste);
							if(rr != null || ar != null) {
								break;
							}
						}
						Icon icon = null;
						if(rr != null) {
							String text = "<html>The default value of this entity is being overriden by a Rate Rule in one or more applications</html>";
							icon = VCellIcons.ruleRateIcon;
							setToolTipText(text);
						} else if(ar != null) {
							String text = "<html>The default value of this entity is being overriden by an Assignment Rule in one or more applications</html>";
							icon = VCellIcons.ruleAssignIcon;
							setToolTipText(text);
						}
						setIcon(icon);
						this.setHorizontalTextPosition(SwingConstants.LEFT);
						if(isSelected) {
							setBackground(VCellColors.getSoftSelectionBackground());
							setForeground(table.getForeground());
						}
					}
				}
				
//				this.setHorizontalTextPosition(SwingConstants.TRAILING);
//				this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
//				this.setHorizontalAlignment(SwingConstants.LEADING);



				return this;
			}
		};
		parametersFunctionsTable.getColumnModel().getColumn(BioModelParametersTableModel.COLUMN_NAME).setCellRenderer(nameTableCellRenderer);

		// what's looked for first leads: the name and its value, then where it's defined, what it is, and its units
		columnsPanel = new org.vcell.util.gui.TableColumnsPanel(parametersFunctionsTable, java.util.List.of(
				BioModelParametersTableModel.COLUMN_NAME, BioModelParametersTableModel.COLUMN_EXPRESSION, BioModelParametersTableModel.COLUMN_PATH,
				BioModelParametersTableModel.COLUMN_DESCRIPTION, BioModelParametersTableModel.COLUMN_UNIT));
		columnsPanel.setName("ParametersColumnsPanel");
		columnsPanel.setVisible(false);
		getParametersFunctionsPanel().add(columnsPanel, BorderLayout.EAST);
	}
	
	private JPanel getModelUnitSystemPanel() {
		if (modelUnitSystemPanel == null) {
			modelUnitSystemPanel = new JPanel();
			modelUnitSystemPanel.setLayout(new BorderLayout());
			
			modelUnitSystemPanel.add(unitSystemChooserPanel, BorderLayout.WEST); // choosing the units, beside the table of them
			modelUnitSystemPanel.add(modelUnitSystemTable.getEnclosingScrollPane(), BorderLayout.CENTER);
		}
		return modelUnitSystemPanel;
	}

	private JPanel getParametersFunctionsPanel() {
		if (parametersFunctionsPanel == null) {
			parametersFunctionsPanel = new JPanel(new BorderLayout(0, 4));
			JPanel filterRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 8, 0));
			filterRow.add(filterButton);
			columnsButton.setName("ParametersColumnsButton");
			columnsButton.setToolTipText("Choose which columns show, and their order");
			columnsButton.addActionListener(e -> {
				columnsPanel.setVisible(columnsButton.isSelected());
				columnsButton.setText("Columns " + (columnsButton.isSelected() ? "\u25BE" : "\u25B8"));
				parametersFunctionsPanel.revalidate();
			});
			filterRow.add(columnsButton);
			filterRow.add(shownLabel);
			parametersFunctionsPanel.add(filterRow, BorderLayout.NORTH);
			parametersFunctionsPanel.add(parametersFunctionsTable.getEnclosingScrollPane(), BorderLayout.CENTER);
			parametersFunctionsPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		}
		return parametersFunctionsPanel;
	}

	// the filter chosen, applied to the table
	private void applyFilter(ParametersFilterButton.Filter filter) {
		if (parametersFunctionsTableModel == null) {
			return; // the button announces its first filter before the table exists; it's applied once it does
		}
		parametersFunctionsTableModel.setIncludeGlobal(filter.global());
		parametersFunctionsTableModel.setIncludeReactions(filter.reactions());
		parametersFunctionsTableModel.setIncludeApplications(filter.applications());
		parametersFunctionsTableModel.setApplicationSelection(filter.application() == null ? ApplicationSelection.All : new ApplicationSelection(filter.application()));
		parametersFunctionsTableModel.setIncludeConstants(filter.parameters());
		parametersFunctionsTableModel.setIncludeFunctions(filter.functions());
		parametersFunctionsTableModel.setIncludedOrigins(filter.userDeclared(), filter.autoGenerated(), filter.preexisting());
		updateShownLabel();
	}

	private void updateShownLabel() {
		int rows = parametersFunctionsTableModel == null ? 0 : parametersFunctionsTableModel.getRowCount();
		shownLabel.setText("Showing " + rows + (rows == 1 ? " entry" : " entries"));
	}

	/** The Model Unit System window: the model's units, and the unit system chooser. Made once, brought forward after. */
	public void showModelUnitSystem() {
		ChildWindowManager childWindowManager = ChildWindowManager.findChildWindowManager(this);
		if (childWindowManager == null) {
			return;
		}
		ChildWindow window = childWindowManager.getChildWindowFromContext(UNIT_SYSTEM_WINDOW);
		if (window == null) {
			window = childWindowManager.addChildWindow(getModelUnitSystemPanel(), UNIT_SYSTEM_WINDOW, "Model Unit System", true);
			window.setSize(860, 480);
			window.setIsCenteredOnParent();
		}
		window.show();
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
				if (parameter instanceof ModelParameter || parameter instanceof SimulationContextParameter) {
					deleteButton.setEnabled(true);
					return;
				}
			}
		}
		deleteButton.setEnabled(false);
	}

	protected void newGlobalButtonPressed() {
		ModelParameter modelParameter = bioModel.getModel().createModelParameter();
		setTableSelections(new Object[]{modelParameter}, parametersFunctionsTable, parametersFunctionsTableModel);
	}
	protected void newApplicationButtonPressed() {
		if(bioModel.getNumSimulationContexts() == 1) {
			SimulationContext simContext = bioModel.getSimulationContext(0);
			SimulationContextParameter newObject = simContext.createSimulationContextParameter();
//			setTableSelections(new Object[]{modelParameter}, parametersFunctionsTable, parametersFunctionsTableModel);
			if (newObject != null) {
				for (int i = 0; i < parametersFunctionsTableModel.getRowCount(); i ++) {
					if (parametersFunctionsTableModel.getValueAt(i) == newObject) {
						parametersFunctionsTable.setRowSelectionInterval(i, i);
						break;
					}
				}
			}
		} else if(bioModel.getNumSimulationContexts() > 1) {
			final JPopupMenu menu = new JPopupMenu("Choose Application");
			for(int i=0; i<bioModel.getNumSimulationContexts(); i++) {
				SimulationContext simContext = bioModel.getSimulationContext(i);
				String sName = simContext.getName();
				JMenuItem menuItem = new JMenuItem("In " + sName);
				Icon icon = null;
				if(simContext.isRuleBased()) {
					if(simContext.getGeometry().getDimension() == 0) {
						icon = VCellIcons.appRbmNonspIcon;
					}
				} else if(simContext.isStoch()) {
					if(simContext.getGeometry().getDimension() == 0) {
						icon = VCellIcons.appStoNonspIcon;
					} else {
						icon = VCellIcons.appStoSpatialIcon;
					}
				} else {		// deterministic
					if(simContext.getGeometry().getDimension() == 0) {
						icon = VCellIcons.appDetNonspIcon;
					} else {
						icon = VCellIcons.appDetSpatialIcon;
					}
				}
				if(icon != null) {
					menuItem.setIcon(icon);
				}
				menu.add(menuItem);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SimulationContextParameter newObject = simContext.createSimulationContextParameter();
						if (newObject != null) {
							for (int i = 0; i < parametersFunctionsTableModel.getRowCount(); i ++) {
								if (parametersFunctionsTableModel.getValueAt(i) == newObject) {
									parametersFunctionsTable.setRowSelectionInterval(i, i);
									break;
								}
							}
						}
					}
				});
			}
			menu.show(addNewButton2, 0, addNewButton2.getHeight());
		}
	}

	protected void deleteButtonPressed() {
		try {
			int[] rows = parametersFunctionsTable.getSelectedRows();
			if (rows == null) {
				return;
			}
			String deleteListText = "";
			ArrayList<Parameter> deleteList = new ArrayList<Parameter>();
			for (int r : rows) {
				if (r < parametersFunctionsTableModel.getRowCount()) {
					EditableSymbolTableEntry parameter = parametersFunctionsTableModel.getValueAt(r);
					if (parameter instanceof ModelParameter) {
						ModelParameter modelParameter = (ModelParameter)parameter;
						deleteList.add(modelParameter);
						deleteListText += "\tGlobal Parameter " + modelParameter.getName() + "\n"; 
					}
					if (parameter instanceof SimulationContextParameter) {
						SimulationContextParameter simulationContextParameter = (SimulationContextParameter)parameter;
						deleteList.add(simulationContextParameter);
						deleteListText += "\tApplication("+simulationContextParameter.getSimulationContext().getName()+") Parameter " + simulationContextParameter.getName() + "\n"; 
					}
				}
			}	
			String confirm = PopupGenerator.showOKCancelWarningDialog(this, "Deleting global and/or application parameters", "You are going to delete the following global/application parameter(s):\n\n " + deleteListText + "\n Continue?");
			if (confirm.equals(UserMessage.OPTION_CANCEL)) {
				return;
			}
			for (Parameter param : deleteList) {
				if (param instanceof ModelParameter){
					bioModel.getModel().removeModelParameter((ModelParameter)param);
				}
				if (param instanceof SimulationContextParameter){
					SimulationContextParameter simContextParameter = (SimulationContextParameter) param;
					SimulationContext simContext = simContextParameter.getSimulationContext();
					simContext.removeSimulationContextParameter(simContextParameter);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			DialogUtils.showErrorDialog(this, ex.getMessage());
		}		
	}
	
	private void searchTable() {
		parametersFunctionsTableModel.setSearchText(textFieldSearch.getText());
	}

	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		setTableSelections(selectedObjects, parametersFunctionsTable, parametersFunctionsTableModel);
	}
	
	public void setBioModel(BioModel newValue) {
		if (newValue == bioModel) {
			return;
		}
		bioModel = newValue;		
		parametersFunctionsTableModel.setBioModel(bioModel);
		modelUnitSystemTableModel.setBioModel(bioModel);
		applicationComboBoxModel.setBioModel(bioModel);
		unitSystemChooserPanel.setModelUnits(bioModel == null ? null : bioModel.getModel().getUnitSystem());
		applyFilter(filterButton.getFilter());
	}
	
	@Override
	protected void setSelectedObjects(Object[] selectedObjects) {
		if (selectionManager != null && selectedObjects != null && selectedObjects.length > 0) {
			selectionManager.setSelectedObjects(selectedObjects);
		}
	}
	
	@Override
	protected void onActiveViewChange(ActiveView activeView) {
		super.onActiveViewChange(activeView);
		DocumentEditorTreeFolderClass folderClass = activeView.getDocumentEditorTreeFolderClass();
		if (activeView.getSimulationContext() != null || folderClass == null) {
			return;
		}
		if (folderClass == DocumentEditorTreeFolderClass.MODEL_UNIT_SYSTEM_NODE) { // its own window now
			showModelUnitSystem();
		} else if (folderClass == DocumentEditorTreeFolderClass.PREDEFINED_SYMBOLS_NODE && !filterButton.getFilter().preexisting()) {
			ParametersFilterButton.Filter f = filterButton.getFilter(); // VCell's own symbols, now rows of this table
			filterButton.setFilter(new ParametersFilterButton.Filter(f.global(), f.reactions(), f.applications(), f.application(), f.parameters(), f.functions(), f.userDeclared(), f.autoGenerated(), true));
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
