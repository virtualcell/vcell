package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DefaultScrollTableActionManager;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class InitialConditionsPanel extends DocumentEditorSubPanel {
	private SimulationContext fieldSimulationContext = null;
	private JRadioButton conRadioButton = null; //added in July, 2008. Enable selection of initial concentration or amount
	private JRadioButton amtRadioButton = null; //added in July, 2008. Enable selection of initial concentration or amount
	private JPanel radioButtonPanel = null; //added in July, 2008. Used to accomodate the two radio buttons
	private ButtonGroup radioGroup = null; //added in July, 2008. Enable selection of initial concentration or amount
	private JSortTable ivjScrollPaneTable = null;
	private SpeciesContextSpecsTableModel ivjSpeciesContextSpecsTableModel = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JMenuItem ivjJMenuItemPaste = null;
	private javax.swing.JMenuItem ivjJMenuItemCopy = null;
	private javax.swing.JMenuItem ivjJMenuItemCopyAll = null;
	private javax.swing.JMenuItem ivjJMenuItemPasteAll = null;

	private class InternalScrollTableActionManager extends DefaultScrollTableActionManager {

		InternalScrollTableActionManager(JTable table) {
			super(table);
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
			}
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == InitialConditionsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
			{
				SimulationContext oldValue = (SimulationContext) evt.getOldValue();
				if (oldValue != null) {
					oldValue.removePropertyChangeListener(ivjEventHandler);
				}			
				SimulationContext newValue = (SimulationContext) evt.getNewValue();
				if (newValue != null) {
					newValue.addPropertyChangeListener(ivjEventHandler);
				}
				getSpeciesContextSpecsTableModel().setSimulationContext(getSimulationContext());
				updateTopScrollPanel();
			}
			
			if (evt.getSource() == getSimulationContext() && evt.getPropertyName().equals(SimulationContext.PROPERTY_NAME_USE_CONCENTRATION)) {
				updateTopScrollPanel();
			}
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getScrollPaneTable().getSelectionModel()) 
				setSelectedObjectsFromTable(getScrollPaneTable(), getSpeciesContextSpecsTableModel());
		};
	};

public InitialConditionsPanel() {
	super();
	initialize();
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
private JPanel getRadioButtonPanel()
{
	if(radioButtonPanel == null)
	{
		JLabel label = new JLabel("Initial Condition: ");
		radioButtonPanel = new JPanel(new FlowLayout());
		radioButtonPanel.add(label);
		getButtonGroup();
		radioButtonPanel.add(getConcentrationRadioButton());
		radioButtonPanel.add(getAmountRadioButton());
	}
	return radioButtonPanel;
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
	if (getSimulationContext().isStoch()) {
		getRadioButtonPanel().setVisible(true);
		boolean bUsingConcentration = getSimulationContext().isUsingConcentration();
		getConcentrationRadioButton().setSelected(bUsingConcentration);
		getAmountRadioButton().setSelected(!bUsingConcentration);
	} else {
		getRadioButtonPanel().setVisible(false);
	}
}

/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setScrollTableActionManager(new InternalScrollTableActionManager(ivjScrollPaneTable));
			ivjScrollPaneTable.setName("ScrollPaneTable");
			ivjScrollPaneTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
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
 * Return the SpeciesContextSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SpeciesContextSpecsTableModel getSpeciesContextSpecsTableModel() {
	if (ivjSpeciesContextSpecsTableModel == null) {
		try {
			ivjSpeciesContextSpecsTableModel = new SpeciesContextSpecsTableModel(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpeciesContextSpecsTableModel;
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
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
	getJMenuItemPaste().addActionListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopyAll().addActionListener(ivjEventHandler);
	getJMenuItemPasteAll().addActionListener(ivjEventHandler);
	getAmountRadioButton().addActionListener(ivjEventHandler);
	getConcentrationRadioButton().addActionListener(ivjEventHandler);
	
	getScrollPaneTable().setModel(getSpeciesContextSpecsTableModel());
	getScrollPaneTable().createDefaultColumnsFromModel();	
	DefaultTableCellRenderer renderer = new DefaultScrollTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof Species) {
				setText(((Species)value).getCommonName());
			} else if (value instanceof SpeciesContext) {
				setText(((SpeciesContext)value).getName());
			} else if (value instanceof Structure) {
				setText(((Structure)value).getName());
			}
			return this;
		}
	};
	getScrollPaneTable().setDefaultRenderer(SpeciesContext.class, renderer);
	getScrollPaneTable().setDefaultRenderer(Structure.class, renderer);
	getScrollPaneTable().setDefaultRenderer(Species.class, renderer);
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
		add(getRadioButtonPanel(), BorderLayout.NORTH);
		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
		//setSize(456, 539);

		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
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
				SpeciesContextSpec scs = getSpeciesContextSpecsTableModel().getValueAt(rows[i]);
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
					SpeciesContextSpec scs = getSpeciesContextSpecsTableModel().getValueAt(rows[i]);
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
											localMathVariable = msm.findVariableByName(pastedMathVariable.getName()+"_init");
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
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		InitialConditionsPanel aInitialConditionsPanel;
		aInitialConditionsPanel = new InitialConditionsPanel();
		frame.setContentPane(aInitialConditionsPanel);
		frame.setSize(aInitialConditionsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}

/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
}

@Override
protected void onSelectedObjectsChange(Object[] selectedObjects) {
	setTableSelections(selectedObjects, getScrollPaneTable(), getSpeciesContextSpecsTableModel());
}
}