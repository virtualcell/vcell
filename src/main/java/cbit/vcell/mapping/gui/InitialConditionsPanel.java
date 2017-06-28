/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.BeanUtils;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DefaultScrollTableActionManager;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable.ScrollTableBooleanCellRenderer;
import org.vcell.util.gui.sorttable.JSortTable;
import org.vcell.util.gui.sorttable.SortTableModel;

import cbit.gui.ScopedExpression;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.ApplicationSpecificationsPanel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorSpeciesTableModel;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.biomodel.IssueManager;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.graph.SmallShapeManager;
import cbit.vcell.graph.SpeciesPatternSmallShape;
import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.gui.StructureMappingTableRenderer.TextIcon;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class InitialConditionsPanel extends DocumentEditorSubPanel implements ApplicationSpecificationsPanel.Specifier {
	private SimulationContext fieldSimulationContext = null;
	private JRadioButton conRadioButton = null; //added in July, 2008. Enable selection of initial concentration or amount
	private JRadioButton amtRadioButton = null; //added in July, 2008. Enable selection of initial concentration or amount
	private ButtonGroup radioGroup = null; //added in July, 2008. Enable selection of initial concentration or amount
	private JPanel radioButtonandCheckboxPanel = null; //added in July, 2008. Used to accomodate the two radio buttons
	private JCheckBox randomizeInitCondnCheckBox = null;	//added in Feb, 2013. Enable randomization of initial concentration or amount
	private JSortTable table = null;
	private SpeciesContextSpecsTableModel tableModel = null;
	private SmallShapeManager shapeManager = new SmallShapeManager(false, false, false, false);
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JMenuItem ivjJMenuItemPaste = null;
	private javax.swing.JMenuItem ivjJMenuItemCopy = null;
	private javax.swing.JMenuItem ivjJMenuItemCopyAll = null;
	private javax.swing.JMenuItem ivjJMenuItemPasteAll = null;

	private class InternalScrollTableActionManager extends DefaultScrollTableActionManager {

		InternalScrollTableActionManager(JTable table) {
			super(table);
			ApplicationSpecificationsPanel asp;
		}

		@Override
		protected void constructPopupMenu() {
			if (popupMenu == null) {
				super.constructPopupMenu();
				int pos = 0;
				popupMenu.insert(getJMenuItemCopy(), pos ++);
				popupMenu.insert(getJMenuItemCopyAll(), pos ++);
				popupMenu.insert(getJMenuItemPaste(), pos ++);
				popupMenu.insert(getJMenuItemPasteAll(), pos ++);
				popupMenu.insert(new JSeparator(), pos++);
			}
			Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);	
			boolean bPastable = obj instanceof VCellTransferable.ResolvedValuesSelection;
			boolean bSomethingSelected = getScrollPaneTable().getSelectedRows() != null && getScrollPaneTable().getSelectedRows().length > 0;
			getJMenuItemPaste().setEnabled(bPastable && bSomethingSelected);
			getJMenuItemPasteAll().setEnabled(bPastable);
			getJMenuItemCopy().setEnabled(bSomethingSelected);
		}
	}
	
	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == InitialConditionsPanel.this.getJMenuItemPaste()) 
				jMenuItemPaste_ActionPerformed(e);
			else if (e.getSource() == InitialConditionsPanel.this.getJMenuItemCopy()) 
				jMenuItemCopy_ActionPerformed(e);
			else if (e.getSource() == InitialConditionsPanel.this.getJMenuItemCopyAll()) 
				jMenuItemCopy_ActionPerformed(e);
			else if (e.getSource() == InitialConditionsPanel.this.getJMenuItemPasteAll()) 
				jMenuItemPaste_ActionPerformed(e);
			else if (e.getSource() == getAmountRadioButton()) {
				amountRadioButton_actionPerformed();
			} else if (e.getSource() == getConcentrationRadioButton()) {
				concentrationRadioButton_actionPerformed();
			} else if (e.getSource() == getRandomizeInitCondnCheckbox()) {
				// only need to set simContext.isRandomizeInitCondn? 
				getSimulationContext().setRandomizeInitConditions(getRandomizeInitCondnCheckbox().isSelected());
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_USE_CONCENTRATION)) {
				updateTopScrollPanel();
			}
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel()) 
				setSelectedObjectsFromTable(getScrollPaneTable(), tableModel);
		};
	};

public InitialConditionsPanel() {
	super();
	initialize();
}

@Override
public ActiveViewID getActiveView() {
	return ActiveViewID.species_settings; 
}

public void setSearchText(String searchText){
	tableModel.setSearchText(searchText);
}
/**
 * Return the JMenuItemCopy property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCopy() {
	if (ivjJMenuItemCopy == null) {
		try {
			ivjJMenuItemCopy = new javax.swing.JMenuItem();
			ivjJMenuItemCopy.setName("JMenuItemCopy");
			ivjJMenuItemCopy.setText("Copy");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopy;
}

/**
 * Return the JMenuItemCopyAll property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCopyAll() {
	if (ivjJMenuItemCopyAll == null) {
		try {
			ivjJMenuItemCopyAll = new javax.swing.JMenuItem();
			ivjJMenuItemCopyAll.setName("JMenuItemCopyAll");
			ivjJMenuItemCopyAll.setText("Copy All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopyAll;
}


/**
 * Return the JMenuItemPaste property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPaste() {
	if (ivjJMenuItemPaste == null) {
		try {
			ivjJMenuItemPaste = new javax.swing.JMenuItem();
			ivjJMenuItemPaste.setName("JMenuItemPaste");
			ivjJMenuItemPaste.setText("Paste");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPaste;
}


/**
 * Return the JMenuItemPasteAll property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPasteAll() {
	if (ivjJMenuItemPasteAll == null) {
		try {
			ivjJMenuItemPasteAll = new javax.swing.JMenuItem();
			ivjJMenuItemPasteAll.setName("JMenuItemPasteAll");
			ivjJMenuItemPasteAll.setText("Paste All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPasteAll;
}

//added in july 2008, to accommodate two radio buttons with flow layout.
private JPanel getRadioButtonAndCheckboxPanel()
{
	if(radioButtonandCheckboxPanel == null)
	{
		JLabel label = new JLabel("Initial Condition: ");
		radioButtonandCheckboxPanel = new JPanel(new FlowLayout());
		radioButtonandCheckboxPanel.add(label);
		getButtonGroup();
		radioButtonandCheckboxPanel.add(getConcentrationRadioButton());
		radioButtonandCheckboxPanel.add(getAmountRadioButton());
		radioButtonandCheckboxPanel.add(getRandomizeInitCondnCheckbox());
	}
	return radioButtonandCheckboxPanel;
}

public void concentrationRadioButton_actionPerformed() {
	AsynchClientTask task1 = new AsynchClientTask("converting to count", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {	
			boolean bUsingConcentration = getSimulationContext().isUsingConcentration();
			if(!bUsingConcentration)//was using amount, then it's going to change.
			{
				if (getSimulationContext().getGeometry().getDimension() == 0 && !getSimulationContext().getGeometryContext().isAllSizeSpecifiedPositive()){
					throw new Exception("\nStructure sizes are required to convert number of particles to concentration.\nPlease go to StructureMapping tab to set valid sizes.");
				}
				//set to use concentration
				getSimulationContext().setUsingConcentration(true);
				getSimulationContext().convertSpeciesIniCondition(true);
				// force propertyChange(by setting old value to null), inform other listeners that simulation contect has changed.
				//firePropertyChange("simulationContext", null, getSimulationContext());
			}
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("in case of failure", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, true) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) != null) {
				getSimulationContext().setUsingConcentration(false);
				updateTopScrollPanel();
			}
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[]{task1, task2});
}

//following functions are added in July 2008. To enable selection of concentration or particles as initial condition
//for deterministic method the selection should be disabled (use concentration only). 
//for stochastic it should be enabled.
private JRadioButton getConcentrationRadioButton()
{
	if(conRadioButton == null) {
		conRadioButton = new JRadioButton("Concentration", true);
	}
	return conRadioButton;
}

private void amountRadioButton_actionPerformed() {
	AsynchClientTask task1 = new AsynchClientTask("converting to count", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			boolean bUseConcentration = getSimulationContext().isUsingConcentration();
			if(bUseConcentration)//was using concentration, then it's going to change.
			{
				if (getSimulationContext().getGeometry().getDimension() == 0 && !getSimulationContext().getGeometryContext().isAllSizeSpecifiedPositive()){
					throw new Exception("\nStructure sizes are required to convert concentration to number of paticles.\nPlease go to StructureMapping tab to set valid sizes.");
				}
				//set to use number of particles
				getSimulationContext().setUsingConcentration(false);
				getSimulationContext().convertSpeciesIniCondition(false);
				// force propertyChange(by setting old value to null), inform other listeners that simulation context has changed.
				//firePropertyChange("simulationContext", null, getSimulationContext());				
			}
		}
	};
	AsynchClientTask task2 = new AsynchClientTask("in case of failure", AsynchClientTask.TASKTYPE_SWING_BLOCKING, false, true) {
		
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR) != null) {
				getSimulationContext().setUsingConcentration(true);
				updateTopScrollPanel();
			}
		}
	};
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[]{task1, task2});
}

private JRadioButton getAmountRadioButton()
{
	if(amtRadioButton == null)
	{
		amtRadioButton = new JRadioButton("Number of Particles");
	}
	return amtRadioButton;
}

private JCheckBox getRandomizeInitCondnCheckbox()
{
	if(randomizeInitCondnCheckBox == null)
	{
		randomizeInitCondnCheckBox = new JCheckBox("Randomize Initial Condition");;
	}
	return randomizeInitCondnCheckBox;
}


private ButtonGroup getButtonGroup()
{
	if(radioGroup == null)
	{
		radioGroup = new ButtonGroup();
		radioGroup.add(getConcentrationRadioButton());
		radioGroup.add(getAmountRadioButton());
	}
	return radioGroup;
}

private void updateTopScrollPanel()
{
	switch (getSimulationContext().getApplicationType()){
	case NETWORK_STOCHASTIC: {
		getRadioButtonAndCheckboxPanel().setVisible(true);
		boolean bUsingConcentration = getSimulationContext().isUsingConcentration();
		getConcentrationRadioButton().setSelected(bUsingConcentration);
		getAmountRadioButton().setSelected(!bUsingConcentration);
		// ' make randomizeInitialCondition' checkBox visible only if application is non-spatial stochastic
		getRandomizeInitCondnCheckbox().setVisible(getSimulationContext().getGeometry().getDimension() == 0);
		getRandomizeInitCondnCheckbox().setSelected(getSimulationContext().isRandomizeInitCondition());
		break;
	}
	case RULE_BASED_STOCHASTIC: {
		getRadioButtonAndCheckboxPanel().setVisible(true);
		boolean bUsingConcentration = getSimulationContext().isUsingConcentration();
		getConcentrationRadioButton().setSelected(bUsingConcentration);
		getAmountRadioButton().setSelected(!bUsingConcentration);
		// ' make randomizeInitialCondition' checkBox invisible for now
		getRandomizeInitCondnCheckbox().setVisible(false);
		break;
	}
	default:{
		getRadioButtonAndCheckboxPanel().setVisible(false);
		break;
	}
	}
}

/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
private JSortTable getScrollPaneTable() {
	if (table == null) {
		try {
			table = new JSortTable();
			table.setName("spceciesContextSpecsTable");
			tableModel = new SpeciesContextSpecsTableModel(table);
			table.setModel(tableModel);
			table.setScrollTableActionManager(new InternalScrollTableActionManager(table));
			table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return table;
}

/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in cbit.vcell.mapping.InitialConditionPanel");
	exception.printStackTrace(System.out);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("InitialConditionsPanel");
		setLayout(new BorderLayout());
		add(getRadioButtonAndCheckboxPanel(), BorderLayout.NORTH);
		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);

		getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
		getJMenuItemPaste().addActionListener(ivjEventHandler);
		getJMenuItemCopy().addActionListener(ivjEventHandler);
		getJMenuItemCopyAll().addActionListener(ivjEventHandler);
		getJMenuItemPasteAll().addActionListener(ivjEventHandler);
		getAmountRadioButton().addActionListener(ivjEventHandler);
		getConcentrationRadioButton().addActionListener(ivjEventHandler);
		getRandomizeInitCondnCheckbox().addActionListener(ivjEventHandler);
			
		DefaultTableCellRenderer renderer = new DefaultScrollTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setIcon(null);
				defaultToolTipText = null;

				if (value instanceof Species) {
					setText(((Species)value).getCommonName());
					defaultToolTipText = getText();
					setToolTipText(defaultToolTipText);
				} else if (value instanceof SpeciesContext) {
					setText(((SpeciesContext)value).getName());
					defaultToolTipText = getText();
					setToolTipText(defaultToolTipText);
				} else if (value instanceof Structure) {
					setText(((Structure)value).getName());
					defaultToolTipText = getText();
					setToolTipText(defaultToolTipText);
				} else if (value instanceof ScopedExpression) {
					SpeciesContextSpec scSpec = tableModel.getValueAt(row);
					VCUnitDefinition unit = null;
					if (table.getColumnName(column).equals(SpeciesContextSpecsTableModel.ColumnType.COLUMN_INITIAL.label)) {
						SpeciesContextSpecParameter initialConditionParameter = scSpec.getInitialConditionParameter();
						unit = initialConditionParameter.getUnitDefinition();
					} else if (table.getColumnName(column).equals(SpeciesContextSpecsTableModel.ColumnType.COLUMN_DIFFUSION.label)) {
						SpeciesContextSpecParameter diffusionParameter = scSpec.getDiffusionParameter();
						unit = diffusionParameter.getUnitDefinition();
					}
					if (unit != null) {
						setHorizontalTextPosition(JLabel.LEFT);
						setIcon(new TextIcon("[" + unit.getSymbolUnicode() + "]", DefaultScrollTableCellRenderer.uneditableForeground));
					}
					int rgb = 0x00ffffff & DefaultScrollTableCellRenderer.uneditableForeground.getRGB();
					defaultToolTipText = "<html>" + StringEscapeUtils.escapeHtml4(getText()) + " <font color=#" + Integer.toHexString(rgb) + "> [" + unit.getSymbolUnicode() + "] </font></html>";
					setToolTipText(defaultToolTipText);
					if(unit != null) {
						setText(defaultToolTipText);
					}
				}
				
				TableModel tableModel = table.getModel();
				if (tableModel instanceof SortTableModel) {
					DefaultScrollTableCellRenderer.issueRenderer(this, defaultToolTipText, table, row, column, (SortTableModel)tableModel);
					setHorizontalTextPosition(JLabel.TRAILING);
				}
				return this;
			}
		};
		DefaultTableCellRenderer rbmSpeciesShapeDepictionCellRenderer = new DefaultScrollTableCellRenderer() {
			SpeciesPatternSmallShape spss = null;
			
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, 
					boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (table.getModel() instanceof VCellSortTableModel<?>) {
					Object selectedObject = null;
					if (table.getModel() == tableModel) {
						selectedObject = tableModel.getValueAt(row);
					}
					if (selectedObject != null) {
						if(selectedObject instanceof SpeciesContextSpec) {
							SpeciesContextSpec scs = (SpeciesContextSpec)selectedObject;
							SpeciesContext sc = scs.getSpeciesContext();
							SpeciesPattern sp = sc.getSpeciesPattern();		// sp may be null for "plain" species contexts
							Graphics panelContext = table.getGraphics();
							spss = new SpeciesPatternSmallShape(4, 2, sp, shapeManager, panelContext, sc, isSelected, issueManager);
						}
					} else {
						spss = null;
					}
				}
				setText("");
				return this;
			}
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(spss != null) {
					spss.paintSelf(g);
				}
			}
		};

		getScrollPaneTable().setDefaultRenderer(SpeciesContext.class, renderer);
		getScrollPaneTable().setDefaultRenderer(Structure.class, renderer);
		getScrollPaneTable().setDefaultRenderer(SpeciesPattern.class, rbmSpeciesShapeDepictionCellRenderer);

		getScrollPaneTable().setDefaultRenderer(Species.class, renderer);
		getScrollPaneTable().setDefaultRenderer(ScopedExpression.class, renderer);
		getScrollPaneTable().setDefaultRenderer(Boolean.class, new ScrollTableBooleanCellRenderer());
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

@Override
public void setIssueManager(IssueManager issueManager) {
	super.setIssueManager(issueManager);
	tableModel.setIssueManager(issueManager);
}

/**
 * Comment
 */
private void jMenuItemCopy_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	if(actionEvent.getSource() == getJMenuItemCopy() || actionEvent.getSource() == getJMenuItemCopyAll()){
		
		try{
			//
			//Copy Symbols and Values Init Conditions
			//
			int[] rows = null;
				if(actionEvent.getSource() == getJMenuItemCopyAll()){
					rows = new int[getScrollPaneTable().getRowCount()];
					for(int i=0;i<rows.length;i+= 1){
						rows[i] = i;
					}
				}else{
					rows = getScrollPaneTable().getSelectedRows();
				}

			MathSymbolMapping msm = null;
			try {
				msm = getSimulationContext().createNewMathMapping().getMathSymbolMapping();
			}catch (Exception e){
				e.printStackTrace(System.out);
				DialogUtils.showWarningDialog(this, "current math not valid, some paste operations will be limited\n\nreason: "+e.getMessage());
			}
			StringBuffer sb = new StringBuffer();
			sb.append("initial Conditions Parameters for (BioModel)"+getSimulationContext().getBioModel().getName()+" (App)"+getSimulationContext().getName()+"\n");
			java.util.Vector<SymbolTableEntry> primarySymbolTableEntriesV = new java.util.Vector<SymbolTableEntry>();
			java.util.Vector<SymbolTableEntry> alternateSymbolTableEntriesV = new java.util.Vector<SymbolTableEntry>();
			java.util.Vector<Expression> resolvedValuesV = new java.util.Vector<Expression>();
			for(int i=0;i<rows.length;i+= 1){
				SpeciesContextSpec scs = tableModel.getValueAt(rows[i]);
				if(scs.isConstant()){
					primarySymbolTableEntriesV.add(scs.getInitialConditionParameter());//need to change
					if (msm!=null){
						alternateSymbolTableEntriesV.add(msm.getVariable(scs.getSpeciesContext()));
					}else{
						alternateSymbolTableEntriesV.add(null);
					}
					resolvedValuesV.add(new Expression(scs.getInitialConditionParameter().getExpression()));
					sb.append(scs.getSpeciesContext().getName()+"\t"+scs.getInitialConditionParameter().getName()+"\t"+scs.getInitialConditionParameter().getExpression().infix()+"\n");
				}else{
					for(int j=0;j<scs.getParameters().length;j+= 1){
						SpeciesContextSpec.SpeciesContextSpecParameter scsp = (SpeciesContextSpec.SpeciesContextSpecParameter)scs.getParameters()[j];
						if(VCellCopyPasteHelper.isSCSRoleForDimension(scsp.getRole(),getSimulationContext().getGeometry().getDimension())){
							Expression scspExpression = scsp.getExpression();
							sb.append(scs.getSpeciesContext().getName()+"\t"+scsp.getName()+"\t"+(scspExpression != null?scspExpression.infix():"")+"\n");
							if(scspExpression != null){// "Default" boundary conditions can't be copied
								primarySymbolTableEntriesV.add(scsp);
								if (msm != null){
									alternateSymbolTableEntriesV.add(msm.getVariable(scsp));
								}else{
									alternateSymbolTableEntriesV.add(null);
								}
								resolvedValuesV.add(new Expression(scspExpression));
							}
						}
					}
				}
			}
			//
			//Send to clipboard
			//
			VCellTransferable.ResolvedValuesSelection rvs =
				new VCellTransferable.ResolvedValuesSelection(
					(SymbolTableEntry[])BeanUtils.getArray(primarySymbolTableEntriesV,SymbolTableEntry.class),
					(SymbolTableEntry[])BeanUtils.getArray(alternateSymbolTableEntriesV,SymbolTableEntry.class),
					(Expression[])BeanUtils.getArray(resolvedValuesV,Expression.class),
					sb.toString());

			VCellTransferable.sendToClipboard(rvs);
		}catch(Throwable e){
			PopupGenerator.showErrorDialog(InitialConditionsPanel.this, "InitialConditionsPanel Copy failed.  "+e.getMessage(), e);
		}
	}
}


/**
 * Comment
 */
private void jMenuItemPaste_ActionPerformed(final java.awt.event.ActionEvent actionEvent) {
	
	final Vector<String> pasteDescriptionsV = new Vector<String>();
	final Vector<Expression> newExpressionsV = new Vector<Expression>();
	final Vector<SpeciesContextSpec.SpeciesContextSpecParameter> changedParametersV = new Vector<SpeciesContextSpec.SpeciesContextSpecParameter>();
	AsynchClientTask task1 = new AsynchClientTask("validating", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			
			if(actionEvent.getSource() == getJMenuItemPaste() || actionEvent.getSource() == getJMenuItemPasteAll()){
				Object pasteThis = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
				
				MathSymbolMapping msm = null;
				Exception mathMappingException = null;
				try {
					MathMapping mm = null;
					mm = getSimulationContext().createNewMathMapping();
					msm = mm.getMathSymbolMapping();
				}catch (Exception e){
					mathMappingException = e;
					e.printStackTrace(System.out);
				}
				
				int[] rows = null;
				if(actionEvent.getSource() == getJMenuItemPasteAll()){
					rows = new int[getScrollPaneTable().getRowCount()];
					for(int i=0;i<rows.length;i+= 1){
						rows[i] = i;
					}
				}else{
					rows = getScrollPaneTable().getSelectedRows();
				}
	
			
				//
				//Check paste
				//
				StringBuffer errors = null;
				for(int i=0;i<rows.length;i+= 1){
					SpeciesContextSpec scs = tableModel.getValueAt(rows[i]);
					try{
						if(pasteThis instanceof VCellTransferable.ResolvedValuesSelection){
							VCellTransferable.ResolvedValuesSelection rvs = (VCellTransferable.ResolvedValuesSelection)pasteThis;
							for(int j=0;j<rvs.getPrimarySymbolTableEntries().length;j+= 1){
								SpeciesContextSpec.SpeciesContextSpecParameter pasteDestination = null;
								SpeciesContextSpec.SpeciesContextSpecParameter clipboardBiologicalParameter = null;
								if(rvs.getPrimarySymbolTableEntries()[j] instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
									clipboardBiologicalParameter = (SpeciesContextSpec.SpeciesContextSpecParameter)rvs.getPrimarySymbolTableEntries()[j];
								}else if(rvs.getAlternateSymbolTableEntries() != null &&
										rvs.getAlternateSymbolTableEntries()[j] instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
									clipboardBiologicalParameter = (SpeciesContextSpec.SpeciesContextSpecParameter)rvs.getAlternateSymbolTableEntries()[j];
								}
								if(clipboardBiologicalParameter == null){
									Variable pastedMathVariable = null;
									if(rvs.getPrimarySymbolTableEntries()[j] instanceof Variable){
										pastedMathVariable = (Variable)rvs.getPrimarySymbolTableEntries()[j];
									}else if(rvs.getAlternateSymbolTableEntries() != null &&
											rvs.getAlternateSymbolTableEntries()[j] instanceof Variable){
										pastedMathVariable = (Variable)rvs.getAlternateSymbolTableEntries()[j];
									}
									if(pastedMathVariable != null){
										if (msm == null){
											throw mathMappingException;
										}
										Variable localMathVariable = msm.findVariableByName(pastedMathVariable.getName());
										if(localMathVariable == null){
											// try if localMathVariable is a speciesContext init parameter
											String initSuffix = DiffEquMathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX + TokenMangler.fixTokenStrict(scs.getInitialConcentrationParameter().getUnitDefinition().getSymbol());
											localMathVariable = msm.findVariableByName(pastedMathVariable.getName()+ initSuffix);
										}
										if(localMathVariable != null){
											SymbolTableEntry[] localBiologicalSymbolArr =  msm.getBiologicalSymbol(localMathVariable);
											for(int k =0;k<localBiologicalSymbolArr.length;k+= 1){
												if(localBiologicalSymbolArr[k] instanceof SpeciesContext && scs.getSpeciesContext() == localBiologicalSymbolArr[k]){
													pasteDestination = scs.getInitialConditionParameter();//need to change
												}else if(localBiologicalSymbolArr[k] instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
													for(int l=0;l<scs.getParameters().length;l+= 1){
														if(scs.getParameters()[l] == localBiologicalSymbolArr[k]){
															pasteDestination = (SpeciesContextSpec.SpeciesContextSpecParameter)localBiologicalSymbolArr[k];
															break;
														}
													}
												}
												if(pasteDestination != null){
													break;
												}
											}
										}
									}
								}else{
									for(int k=0;k<scs.getParameters().length;k+= 1){
										SpeciesContextSpec.SpeciesContextSpecParameter scsp =
											(SpeciesContextSpec.SpeciesContextSpecParameter)scs.getParameters()[k];
										if(scsp.getRole() == clipboardBiologicalParameter.getRole() &&
											scs.getSpeciesContext().compareEqual(
											((SpeciesContextSpec)clipboardBiologicalParameter.getNameScope().getScopedSymbolTable()).getSpeciesContext())){
											pasteDestination = (SpeciesContextSpec.SpeciesContextSpecParameter)scsp;
										}
									}
								}
	
								if(pasteDestination != null){
									changedParametersV.add(pasteDestination);
									newExpressionsV.add(rvs.getExpressionValues()[j]);
									pasteDescriptionsV.add(
										VCellCopyPasteHelper.formatPasteList(
											scs.getSpeciesContext().getName(),
											pasteDestination.getName(),
											pasteDestination.getExpression().infix(),
											rvs.getExpressionValues()[j].infix())
									);
								}
							}
						}
					}catch(Throwable e){
						if(errors == null){errors = new StringBuffer();}
						errors.append(scs.getSpeciesContext().getName()+" ("+e.getClass().getName()+") "+e.getMessage()+"\n\n");
					}
				}
				if(errors != null){
					throw new Exception(errors.toString());
				}
	
			}
		}
	};
	
	AsynchClientTask task2 = new AsynchClientTask("pasting", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {

		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {

			//Do paste
			if(pasteDescriptionsV.size() > 0){
				String[] pasteDescriptionArr = new String[pasteDescriptionsV.size()];
				pasteDescriptionsV.copyInto(pasteDescriptionArr);
				SpeciesContextSpec.SpeciesContextSpecParameter[] changedParametersArr = 
					new SpeciesContextSpec.SpeciesContextSpecParameter[changedParametersV.size()];
				changedParametersV.copyInto(changedParametersArr);
				Expression[] newExpressionsArr = new Expression[newExpressionsV.size()];
				newExpressionsV.copyInto(newExpressionsArr);
				VCellCopyPasteHelper.chooseApplyPaste(InitialConditionsPanel.this, pasteDescriptionArr,changedParametersArr,newExpressionsArr);
			}else{
				PopupGenerator.showInfoDialog(InitialConditionsPanel.this, "No paste items match the destination (no changes made).");
			}
		}
	};
	
	ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});

}


/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext newValue) {
	SimulationContext oldValue = fieldSimulationContext;
	if (oldValue != null) {
		oldValue.removePropertyChangeListener(ivjEventHandler);
	}
	fieldSimulationContext = newValue;
	if (newValue != null) {
		newValue.addPropertyChangeListener(ivjEventHandler);
	}
	tableModel.setSimulationContext(fieldSimulationContext);
	updateTopScrollPanel();
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	setTableSelections(selectedObjects, getScrollPaneTable(), tableModel);
}
}
