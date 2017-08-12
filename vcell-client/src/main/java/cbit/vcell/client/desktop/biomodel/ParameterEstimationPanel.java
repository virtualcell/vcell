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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.util.Hashtable;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vcell.optimization.gui.AnalysisTaskComboBoxModel;
import org.vcell.optimization.gui.ParameterEstimationRunTaskPanel;
import org.vcell.optimization.gui.ParameterMappingPanel;
import org.vcell.optimization.gui.ReferenceDataMappingSpecTableModel;
import org.vcell.optimization.gui.ReferenceDataPanel;
import org.vcell.util.TokenMangler;
import org.vcell.util.UtilCancelException;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveView;
import cbit.vcell.client.desktop.biomodel.SelectionManager.ActiveViewID;
import cbit.vcell.client.server.UserPreferences;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.modelopt.AnalysisTask;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.modelopt.gui.SymbolTableEntryListCellRenderer;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.parser.SymbolTableEntry;

@SuppressWarnings("serial")
public class ParameterEstimationPanel extends ApplicationSubPanel {

	private JPanel taskTablePanel;
	private SimulationContext simulationContext;
	private JButton deleteTaskButton = null;
	private JButton newTaskButton = null;
	private IvjEventHandler eventHandler = new IvjEventHandler();
	private JButton copyTaskButton;
	private JComboBox taskComboBox = null;
	private AnalysisTaskComboBoxModel analysisTaskComboBoxModel;	
	
	private ParameterMappingPanel parameterMappingPanel = null;
	private ParameterEstimationRunTaskPanel runTaskPanel = null;
	private ReferenceDataPanel referenceDataPanel = null;
	private UserPreferences fieldUserPreferences = null;
	private javax.swing.JPanel dataMappingPanel = null;
	private ReferenceDataMappingSpecTableModel referenceDataMappingSpecTableModel = null;
	private javax.swing.JButton mapButton = null;
	private ScrollTable dataModelMappingTable = null;
	private ParameterEstimationTask fieldParameterEstimationTask = null;
	private JButton evaluateConfidenceIntervalButton = null;
	
	public enum ParameterEstimationPanelTabID {
		parameters(						cbit.vcell.client.constants.GuiConstants.PARAMETER_ESTIMATION_TAB_PARAMETERS),
		experimental_data_import(		cbit.vcell.client.constants.GuiConstants.PARAMETER_ESTIMATION_TAB_EXPDATAIMPORT),
		experimental_data_mapping(		cbit.vcell.client.constants.GuiConstants.PARAMETER_ESTIMATION_TAB_EXPDATAMAPPING),
		run_task(						cbit.vcell.client.constants.GuiConstants.PARAMETER_ESTIMATION_TAB_RUNTASK);
		
		private String title = null;
		ParameterEstimationPanelTabID(String name) {
			this.title = name;
		}
		public String getTitle() {
			return title;
		}
	}
	
	private class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener, MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getNewAnalysisTaskButton()) 
				newParameterEstimationTaskButton_ActionPerformed();
			else if (e.getSource() == getDeleteAnalysisTaskButton()) 
				deleteAnalysisTaskButton_ActionPerformed();
			else if (e.getSource() == getCopyButton()) 
				copyAnalysisTaskButton_ActionPerformed();
			else if (e.getSource() == getAnalysisTaskComboBox()) 
				analysisTaskComboBox_ActionPerformed();
			else if (e.getSource() == getMapButton()) 
				mapButton_ActionPerformed();
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (fieldParameterEstimationTask != null) {
				if (evt.getSource() == fieldParameterEstimationTask && (evt.getPropertyName().equals(ParameterEstimationTask.PROPERTY_NAME_OPTIMIZATION_RESULT_SET))) { 
					optimizationResultSet_This(fieldParameterEstimationTask.getOptimizationResultSet());
				}
			}
		}
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == getDataModelMappingTable().getSelectionModel()) 
				getMapButton().setEnabled(dataModelMappingTable.getSelectedRowCount() == 1);
		}
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				if (e.getSource() == dataModelMappingTable) {
					int clickColumn = dataModelMappingTable.columnAtPoint(e.getPoint());
					if (clickColumn == ReferenceDataMappingSpecTableModel.COLUMN_MODELENTITY) {
						mapButton_ActionPerformed();
					}
				}
			}
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
	}
	
	public ParameterEstimationPanel() {
		super();
		initialize();
	}
	
	@Override
	public ActiveView getActiveView() {
		return new ActiveView(simulationContext, DocumentEditorTreeFolderClass.PARAMETER_ESTIMATION_NODE, ActiveViewID.parameter_estimation);
	}

	@Override
	protected void initialize() {
		super.initialize();
		
		setName("Parameter Estimation");
		setLayout(new java.awt.BorderLayout());
		
		referenceDataPanel = new ReferenceDataPanel();
		runTaskPanel = new ParameterEstimationRunTaskPanel();
		
		getparameterMappingPanel().setBorder(GuiConstants.TAB_PANEL_BORDER);
		referenceDataPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		getDataMappingPanel().setBorder(GuiConstants.TAB_PANEL_BORDER);
		runTaskPanel.setBorder(GuiConstants.TAB_PANEL_BORDER);
		tabbedPane.addTab(ParameterEstimationPanelTabID.parameters.title, getparameterMappingPanel());
		tabbedPane.addTab(ParameterEstimationPanelTabID.experimental_data_import.title, referenceDataPanel);
		tabbedPane.addTab(ParameterEstimationPanelTabID.experimental_data_mapping.title, getDataMappingPanel());
		tabbedPane.addTab(ParameterEstimationPanelTabID.run_task.title, runTaskPanel);
		
		add(tabbedPane, BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.NORTH);		
		
		getNewAnalysisTaskButton().addActionListener(eventHandler);
		getDeleteAnalysisTaskButton().addActionListener(eventHandler);
		getCopyButton().addActionListener(eventHandler);
		getAnalysisTaskComboBox().addActionListener(eventHandler);
		
		getMapButton().addActionListener(eventHandler);
		getEvaluateConfidenceIntervalButton().addActionListener(eventHandler);
		getDataModelMappingTable().getSelectionModel().addListSelectionListener(eventHandler);
		dataModelMappingTable.addMouseListener(eventHandler);
		
		getDataModelMappingTable().setDefaultRenderer(SymbolTableEntry.class, new DefaultScrollTableCellRenderer() {
			public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (value == null) {
					setText("unmapped");
					return this;
				}
				SymbolTableEntry ste = (SymbolTableEntry)value;
				if (ste instanceof Model.ReservedSymbol){
					setText(ste.getName());
				}else if (ste instanceof SpeciesContext){
					setText("["+ste.getName()+"]");
				}else if (ste instanceof KineticsParameter){
					setText(ste.getNameScope().getName()+":"+ste.getName());
				}else if (ste instanceof ModelParameter){
					setText(ste.getName());
				}else if (ste instanceof ReservedVariable) {
					setText(ste.getName());
				}else{
					setText(ste.getNameScope().getAbsoluteScopePrefix()+ste.getName());
				}
				return this;
			}
		});
		
		getDataModelMappingTable().setModel(getreferenceDataMappingSpecTableModel());
		getDataModelMappingTable().createDefaultColumnsFromModel();
	}
	
	private javax.swing.JPanel getButtonPanel() {
		if (taskTablePanel == null) {
			taskTablePanel = new javax.swing.JPanel();
			taskTablePanel.setName("ButtonPanel");
						
			taskTablePanel.setLayout(new FlowLayout());
			JLabel label = new JLabel("Select a Task: ");
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			taskTablePanel.add(label);
			getAnalysisTaskComboBox().setPreferredSize(new Dimension(200, (int)getAnalysisTaskComboBox().getPreferredSize().getHeight()));
			taskTablePanel.add(getAnalysisTaskComboBox());
			taskTablePanel.add(getNewAnalysisTaskButton());			
			taskTablePanel.add(getCopyButton());			
			taskTablePanel.add(getDeleteAnalysisTaskButton());
		}
		return taskTablePanel;
	}
	
	@Override
	public void setSimulationContext(SimulationContext newValue) {
		if (simulationContext == newValue) {
			return;
		}
		simulationContext = newValue;
		
		analysisTaskComboBoxModel.setSimulationContext(simulationContext);
		refreshAnalysisTaskEnables();
	}
	
	private AnalysisTask getSelectedAnalysisTask() {
		return (AnalysisTask)getAnalysisTaskComboBox().getSelectedItem();
	}
	
	private void copyAnalysisTaskButton_ActionPerformed() {
		try {
			AnalysisTask taskToCopy = getSelectedAnalysisTask();
			if (simulationContext != null && taskToCopy != null){
				AnalysisTask newAnalysisTask = simulationContext.copyAnalysisTask(taskToCopy);
				getAnalysisTaskComboBox().setSelectedItem(newAnalysisTask);
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this,e.getMessage(), e);
		}
	}
	
	private javax.swing.JComboBox getAnalysisTaskComboBox() {
		if (taskComboBox == null) {
			taskComboBox = new javax.swing.JComboBox();
			taskComboBox.setRenderer(new DefaultListCellRenderer() {
				public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
					if (value instanceof AnalysisTask){
						AnalysisTask analysisTask = (AnalysisTask)value;
						setText(analysisTask.getName());
					} else{
						setText((value == null) ? "" : value.toString());
					}
					return this;
				}
			});
			taskComboBox.setEnabled(false);
			analysisTaskComboBoxModel = new AnalysisTaskComboBoxModel();
			taskComboBox.setModel(analysisTaskComboBoxModel);
		}
		return taskComboBox;
	}
		
	private void deleteAnalysisTaskButton_ActionPerformed() {
		AnalysisTask taskToDelete = getSelectedAnalysisTask();
		if (taskToDelete != null && simulationContext != null) {
			try {
				simulationContext.removeAnalysisTask(taskToDelete);
			} catch (PropertyVetoException e) {
				e.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this, e.getMessage());
			}
		}
		return;
	}
	
	private javax.swing.JButton getDeleteAnalysisTaskButton() {
		if (deleteTaskButton == null) {
			deleteTaskButton = new javax.swing.JButton("Delete");
			deleteTaskButton.setName("DeleteAnalysisTaskButton");
			deleteTaskButton.setEnabled(false);
			deleteTaskButton.setActionCommand("DeleteModelOptSpec");
		}
		return deleteTaskButton;
	}
	
	private javax.swing.JButton getCopyButton() {
		if (copyTaskButton == null) {
			copyTaskButton = new javax.swing.JButton("Copy...");
			copyTaskButton.setName("CopyButton");
		}
		return copyTaskButton;
	}

	private void newParameterEstimationTaskButton_ActionPerformed() {
		try {
			String parameterEstimationName = "task0";
			if (simulationContext==null){
				return;
			}

			AnalysisTask analysisTasks[] = simulationContext.getAnalysisTasks();
			boolean found = true;
			while (found) {
				found = false;
				parameterEstimationName = TokenMangler.getNextEnumeratedToken(parameterEstimationName);
				for (int i = 0;analysisTasks!=null && i < analysisTasks.length; i++){
					if (analysisTasks[i].getName().equals(parameterEstimationName)){
						found = true;
						continue;
					}
				}
			}

			String newParameterEstimationName = null;
			try {
				newParameterEstimationName = DialogUtils.showInputDialog0(this,"name for new parameter estimation set",parameterEstimationName);
			} catch (UtilCancelException ex) {
				// user canceled; it's ok
			}

			if (newParameterEstimationName != null) {
				if (newParameterEstimationName.length() == 0) {
					PopupGenerator.showErrorDialog(this,"Error:\n name for new parameter estimation can't be empty" );
				} else {
					final String finalname = newParameterEstimationName;
					AsynchClientTask task1 = new AsynchClientTask("init task", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
						
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							ParameterEstimationTask newParameterEstimationTask = new ParameterEstimationTask(simulationContext);
							newParameterEstimationTask.setName(finalname);
							hashTable.put("newParameterEstimationTask", newParameterEstimationTask);
						}
					};
					AsynchClientTask task2 = new AsynchClientTask("add task", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
						@Override
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							ParameterEstimationTask newParameterEstimationTask = (ParameterEstimationTask)hashTable.get("newParameterEstimationTask");
							simulationContext.addAnalysisTask(newParameterEstimationTask);
							getAnalysisTaskComboBox().setSelectedItem(newParameterEstimationTask);
							refreshAnalysisTaskEnables();
						}
					};
					
					ClientTaskDispatcher.dispatch(this, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2});
				}
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(this,e.getMessage(), e);
		}
	}
	
	private void analysisTaskComboBox_ActionPerformed() {
		ParameterEstimationTask selectedItem = (ParameterEstimationTask)getAnalysisTaskComboBox().getSelectedItem();
		setParameterEstimationTask(selectedItem);
		setSelectedObjects(new Object[] {selectedItem});
	}
	
	private void refreshAnalysisTaskEnables() {
		boolean bHasTasks = getAnalysisTaskComboBox().getItemCount() > 0;
		getNewAnalysisTaskButton().setEnabled(simulationContext != null && simulationContext.getGeometry().getDimension() == 0);
		getAnalysisTaskComboBox().setEnabled(bHasTasks);
		getDeleteAnalysisTaskButton().setEnabled(bHasTasks);
		getCopyButton().setEnabled(bHasTasks);
	}
	
	private javax.swing.JButton getNewAnalysisTaskButton() {
		if (newTaskButton == null) {
			newTaskButton = new javax.swing.JButton("New...");
			newTaskButton.setName("NewAnalysisTaskButton");
			newTaskButton.setActionCommand("NewModelOptSpec");
		}
		return newTaskButton;
	}
	
	@Override
	protected void onSelectedObjectsChange(Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length != 1 || !(selectedObjects[0] instanceof ParameterEstimationTask)) {
			return;
		}
		ParameterEstimationTask parameterEstimationTask = (ParameterEstimationTask) selectedObjects[0];
		setParameterEstimationTask(parameterEstimationTask);
	}
	
	/**
	 * Comment
	 */
	private java.lang.String displayResults(OptimizationResultSet optResultSet) {
		if (optResultSet==null){
			return "no results";
		}
		StringBuffer buffer = new StringBuffer();

		buffer.append("\n-------------Optimizer Output-----------------\n");
		buffer.append(optResultSet.getOptSolverResultSet().getOptimizationStatus() + "\n");
		buffer.append("best objective function :"+optResultSet.getOptSolverResultSet().getLeastObjectiveFunctionValue()+"\n");
		buffer.append("num function evaluations :"+optResultSet.getOptSolverResultSet().getObjFunctionEvaluations()+"\n");
		if (optResultSet.getOptSolverResultSet().getOptimizationStatus().isNormal()){
			buffer.append("status: complete\n");
		}else{
			buffer.append("status: aborted\n");
		}
		for (int i = 0; optResultSet.getOptSolverResultSet().getParameterNames()!=null && i < optResultSet.getOptSolverResultSet().getParameterNames().length; i++){
			buffer.append(optResultSet.getOptSolverResultSet().getParameterNames()[i]+" = "+optResultSet.getOptSolverResultSet().getBestEstimates()[i]+"\n");
		}

		return buffer.toString();
	}


	/**
	 * Return the ScrollPaneTable1 property value.
	 * @return javax.swing.JTable
	 */
	private ScrollTable getDataModelMappingTable() {
		if (dataModelMappingTable == null) {
			try {
				dataModelMappingTable = new ScrollTable();
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return dataModelMappingTable;
	}

	/**
	 * Return the JPanel8 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getDataMappingPanel() {
		if (dataMappingPanel == null) {
			try {
				dataMappingPanel = new javax.swing.JPanel();
				dataMappingPanel.setLayout(new java.awt.GridBagLayout());

				java.awt.GridBagConstraints constraintsDataModelMappingScrollPane = new java.awt.GridBagConstraints();
				constraintsDataModelMappingScrollPane.gridx = 0; constraintsDataModelMappingScrollPane.gridy = 0;
				constraintsDataModelMappingScrollPane.fill = java.awt.GridBagConstraints.BOTH;
				constraintsDataModelMappingScrollPane.weightx = 1.0;
				constraintsDataModelMappingScrollPane.weighty = 1.0;
				constraintsDataModelMappingScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
				dataMappingPanel.add(getDataModelMappingTable().getEnclosingScrollPane(), constraintsDataModelMappingScrollPane);

				java.awt.GridBagConstraints constraintsMapButton = new java.awt.GridBagConstraints();
				constraintsMapButton.gridx = 0; constraintsMapButton.gridy = 1;
				constraintsMapButton.insets = new java.awt.Insets(4, 4, 4, 4);
				dataMappingPanel.add(getMapButton(), constraintsMapButton);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return dataMappingPanel;
	}

	/**
	 * Return the MapButton property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JButton getMapButton() {
		if (mapButton == null) {
			try {
				mapButton = new javax.swing.JButton();
				mapButton.setName("MapButton");
				mapButton.setText("Map Experimental Data...");
				mapButton.setEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return mapButton;
	}

	/**
	 * Gets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
	 * @return The parameterEstimationTask property value.
	 * @see #setParameterEstimationTask
	 */
	public ParameterEstimationTask getParameterEstimationTask() {
		return fieldParameterEstimationTask;
	}

	/**
	 * Return the parameterMappingPanel property value.
	 * @return cbit.vcell.modelopt.gui.ParameterMappingPanel
	 */
	private ParameterMappingPanel getparameterMappingPanel() {
		if (parameterMappingPanel == null) {
			try {
				parameterMappingPanel = new ParameterMappingPanel();
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return parameterMappingPanel;
	}


	private JButton getEvaluateConfidenceIntervalButton() {
		if ( evaluateConfidenceIntervalButton == null) {
			try {
				evaluateConfidenceIntervalButton = new javax.swing.JButton("Confidence Interval");
				evaluateConfidenceIntervalButton.setEnabled(false);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return evaluateConfidenceIntervalButton;
	}

	/**
	 * Return the referenceDataMappingSpecTableModel property value.
	 * @return cbit.vcell.modelopt.gui.ReferenceDataMappingSpecTableModel
	 */
	private ReferenceDataMappingSpecTableModel getreferenceDataMappingSpecTableModel() {
		if (referenceDataMappingSpecTableModel == null) {
			try {
				referenceDataMappingSpecTableModel = new ReferenceDataMappingSpecTableModel();
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return referenceDataMappingSpecTableModel;
	}

	/**
	 * Gets the userPreferences property (cbit.vcell.client.server.UserPreferences) value.
	 * @return The userPreferences property value.
	 * @see #setUserPreferences
	 */
	public UserPreferences getUserPreferences() {
		return fieldUserPreferences;
	}

	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}


	/**
	 * Comment
	 */
	private void mapButton_ActionPerformed() {

		if (getParameterEstimationTask()==null){
			return;
		}

		SymbolTableEntry symbolTableEntries[] = getParameterEstimationTask().getModelOptimizationSpec().calculateTimeDependentModelObjects(getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext());

		java.util.Comparator<SymbolTableEntry> steComparator = new java.util.Comparator<SymbolTableEntry>() {
			private Class<?>[] classOrder = new Class<?>[] { Model.ReservedSymbol.class, SpeciesContext.class, Model.ModelParameter.class, Kinetics.KineticsParameter.class };
			public int compare(SymbolTableEntry ste1, SymbolTableEntry ste2){
				int ste1Category = 100;
				int ste2Category = 100;
				for (int i=0;i<classOrder.length;i++){
					if (ste1.getClass().equals(classOrder[i])){
						ste1Category = i;
					}
					if (ste2.getClass().equals(classOrder[i])){
						ste2Category = i;
					}
				}
				if (ste1Category < ste2Category){
					return 1;
				}else if (ste1Category > ste2Category){
					return -1;
				}else{
					return ste1.getName().compareTo(ste2.getName());
				}
			}
		};

		java.util.Arrays.sort(symbolTableEntries,steComparator);

		SymbolTableEntry ste = (SymbolTableEntry)DialogUtils.showListDialog(this,symbolTableEntries,"Map Experimental Data",new SymbolTableEntryListCellRenderer());

		if (ste!=null && getDataModelMappingTable().getSelectionModel().getMaxSelectionIndex()>=0){
			ReferenceDataMappingSpec refDataMappingSpec = getParameterEstimationTask().getModelOptimizationSpec().getReferenceDataMappingSpecs()[getDataModelMappingTable().getSelectionModel().getMaxSelectionIndex()];
			try {
				refDataMappingSpec.setModelObject(ste);
			}catch (java.beans.PropertyVetoException e){
				e.printStackTrace(System.out);
				DialogUtils.showErrorDialog(this,e.getMessage());
			}
		}
		return;
	}


	/**
	 * Comment
	 */
	private void optimizationResultSet_This(OptimizationResultSet optResultSet) {
		String message = displayResults(optResultSet);
		getParameterEstimationTask().appendSolverMessageText("\n"+message);
	}

	/**
	 * Sets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
	 * @param newValue The new value for the property.
	 * @see #getParameterEstimationTask
	 */
	private void setParameterEstimationTask(ParameterEstimationTask newValue) {
		ParameterEstimationTask oldValue = fieldParameterEstimationTask;
		/* Stop listening for events from the current object */
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(eventHandler);
			if (oldValue.getModelOptimizationSpec() != null) {
				oldValue.getModelOptimizationSpec().removePropertyChangeListener(eventHandler);
			}
		}
		fieldParameterEstimationTask = newValue;
		/* Listen for events from the new object */
		if (newValue != null) {
			newValue.addPropertyChangeListener(eventHandler);
			if (newValue.getModelOptimizationSpec() != null) {
				newValue.getModelOptimizationSpec().addPropertyChangeListener(eventHandler);
			}
		}
		
		getreferenceDataMappingSpecTableModel().setModelOptimizationSpec(fieldParameterEstimationTask == null ? null : fieldParameterEstimationTask.getModelOptimizationSpec());
		referenceDataPanel.setParameterEstimationTask(fieldParameterEstimationTask);		
		getparameterMappingPanel().setParameterEstimationTask(fieldParameterEstimationTask);
		getAnalysisTaskComboBox().setSelectedItem(fieldParameterEstimationTask);
		runTaskPanel.setParameterEstimationTask(fieldParameterEstimationTask);
	}

	/**
	 * Sets the userPreferences property (cbit.vcell.client.server.UserPreferences) value.
	 * @param userPreferences The new value for the property.
	 * @see #getUserPreferences
	 */
	public void setUserPreferences(UserPreferences userPreferences) {
//		UserPreferences oldValue = fieldUserPreferences;
		fieldUserPreferences = userPreferences;		
		referenceDataPanel.setUserPreferences(fieldUserPreferences);
	}
}
