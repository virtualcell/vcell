/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DefaultScrollTableActionManager;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.VCellIcons;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.desktop.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Variable;
import cbit.vcell.model.Kinetics.UnresolvedParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ParameterMappingSpec;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;

/**
 * Insert the type's description here.
 * Creation date: (9/23/2003 12:23:30 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ParameterMappingPanel extends javax.swing.JPanel {
	private JSortTable parameterMappingTable = null;
	private ParameterMappingTableModel parameterMappingTableModel = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private ParameterEstimationTask fieldParameterEstimationTask = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem copyAllMenuItem = null;
	private JMenuItem pasteMenuItem = null;
	private JMenuItem pasteAllMenuItem = null;
	private JPopupMenu copyPastePopupMenu = null;
	private JButton addButton;
	private JButton deleteButton;

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

			boolean bSelected = getScrollPaneTable().getSelectedRowCount() > 0;
			bPastable = bPastable && bSelected;

			if(bSelected){
				getJMenuItemCopy().setText("Copy 'Initial Guess'");
				getJMenuItemCopyAll().setText("Copy All 'Initial Guess'");
				getJMenuItemPaste().setText("Paste 'Initial Guess'");
				getJMenuItemPasteAll().setText("Paste All 'Initial Guess'");
			}
			
			getJMenuItemPaste().setEnabled(bPastable);
			getJMenuItemPasteAll().setEnabled(bPastable);

		}
	}
	
	private class IvjEventHandler implements java.awt.event.ActionListener, /*java.awt.event.MouseListener, */java.beans.PropertyChangeListener, ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == addButton) {
				addParameter();
			} else if (e.getSource() == deleteButton) {
				deleteParameter();
			}
			if (e.getSource() == ParameterMappingPanel.this.getJMenuItemCopy()) {
				jMenuItemCopy_ActionPerformed(e);
			} else if (e.getSource() == ParameterMappingPanel.this.getJMenuItemCopyAll()) {
				jMenuItemCopy_ActionPerformed(e);
			} else if (e.getSource() == ParameterMappingPanel.this.getJMenuItemPaste()) { 
				jMenuItemPaste_ActionPerformed(e);
			} else if (e.getSource() == ParameterMappingPanel.this.getJMenuItemPasteAll()) {
				jMenuItemPaste_ActionPerformed(e);
			}
		}
//		public void mouseClicked(java.awt.event.MouseEvent e) {};
//		public void mouseEntered(java.awt.event.MouseEvent e) {};
//		public void mouseExited(java.awt.event.MouseEvent e) {};
//		public void mousePressed(java.awt.event.MouseEvent e) {
//			if (e.getSource() == ParameterMappingPanel.this.getScrollPaneTable()) {
//				popupCopyPaste(e);
//			}
//		};
//		public void mouseReleased(java.awt.event.MouseEvent e) {
//			if (e.getSource() == ParameterMappingPanel.this.getScrollPaneTable()) {
//				popupCopyPaste(e);
//			}
//		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ParameterMappingPanel.this && (evt.getPropertyName().equals("parameterEstimationTask"))) {
				parameterMappingTableModel.setParameterEstimationTask(getParameterEstimationTask());
			}
		}
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			
			deleteButton.setEnabled(getScrollPaneTable().getSelectedRowCount() > 0);
		}
	};

/**
 * ModelParameterPanel constructor comment.
 */
public ParameterMappingPanel() {
	super();
	initialize();
}

public void deleteParameter() {
	ArrayList<ParameterMappingSpec> list = new ArrayList<ParameterMappingSpec>();
	for (int row: getScrollPaneTable().getSelectedRows()) {
		ParameterMappingSpec pms = parameterMappingTableModel.getValueAt(row);
		list.add(pms);
	}
	for (ParameterMappingSpec pms : list) {
		pms.setSelected(false);
	}
}

private static class SelectParameterTableModel extends VCellSortTableModel<ParameterMappingSpec> implements PropertyChangeListener {

	private final static int COLUMN_NAME = 0;
	private final static int COLUMN_SCOPE = 1;
	private final static String LABELS[] = { "Parameter", "Context"};
	
	private ParameterEstimationTask parameterEstimationTask = null;

	private class ParameterColumnComparator implements Comparator<ParameterMappingSpec> {
		private int index;
		private int scale = 1;

		public ParameterColumnComparator(int index, boolean ascending){
			this.index = index;
			scale = ascending ? 1 : -1;
		}
		
		/**
		 * Compares its two arguments for order.  Returns a negative integer,
		 * zero, or a positive integer as the first argument is less than, equal
		 * to, or greater than the second.<p>
		 */
		public int compare(ParameterMappingSpec pms1, ParameterMappingSpec pms2){
			
			Parameter parm1 = pms1.getModelParameter();
			Parameter parm2 = pms2.getModelParameter();
			
			switch (index){
				case COLUMN_NAME:{
					return scale * parm1.getName().compareToIgnoreCase(parm2.getName());
				}
				case COLUMN_SCOPE:{
					return scale * parm1.getNameScope().getName().compareToIgnoreCase(parm2.getNameScope().getName());
				}
			}
			return 1;
		}
	}
	
	public SelectParameterTableModel(ScrollTable table, ParameterEstimationTask task) {
		super(table, LABELS);
		parameterEstimationTask = task;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2/24/01 12:24:35 AM)
	 * @return java.lang.Class
	 * @param column int
	 */
	public Class<?> getColumnClass(int column) {
		switch (column){
			case COLUMN_NAME:{
				return String.class;
			}
			case COLUMN_SCOPE:{
				return String.class;
			}
		}
		return null;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/23/2003 1:24:52 PM)
	 * @return cbit.vcell.model.Parameter
	 * @param row int
	 */
	private void refreshData() {
		ArrayList<ParameterMappingSpec> list = new ArrayList<ParameterMappingSpec>();
		if (parameterEstimationTask != null) {
			for (ParameterMappingSpec pms : parameterEstimationTask.getModelOptimizationSpec().getParameterMappingSpecs()) {
				if (!pms.isSelected()) {
					list.add(pms);
				}
			}
		}
		setData(list);
	}
	
	
	/**
	 * getValueAt method comment.
	 */
	public Object getValueAt(int row, int col) {
		ParameterMappingSpec parameterMappingSpec = getValueAt(row);
		switch (col){
			case COLUMN_NAME:{
				return parameterMappingSpec.getModelParameter().getName();
			}
			case COLUMN_SCOPE:{
				if (parameterMappingSpec.getModelParameter() instanceof UnresolvedParameter){
					return "unresolved";
				}else if (parameterMappingSpec.getModelParameter().getNameScope()==null){
					return "null";
				} if (parameterMappingSpec.getModelParameter() instanceof ModelParameter) {
					return "Model";
				} else{
					return parameterMappingSpec.getModelParameter().getNameScope().getName();
				}
			}
			default:{
				return null;
			}
		}
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2/24/01 12:27:46 AM)
	 * @return boolean
	 * @param rowIndex int
	 * @param columnIndex int
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	
	/**
	 * isSortable method comment.
	 */
	public boolean isSortable(int col) {
		return true;
	}

	protected Comparator<ParameterMappingSpec> getComparator(int col, boolean ascending) {		
    	return new ParameterColumnComparator(col, ascending);
  	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
}

public void addParameter() {
	JPanel panel = new JPanel(new BorderLayout());
	ArrayList<ParameterMappingSpec> dataList = new ArrayList<ParameterMappingSpec>();
	if (getParameterEstimationTask() != null) {
		for (ParameterMappingSpec pms : getParameterEstimationTask().getModelOptimizationSpec().getParameterMappingSpecs()) {
			if (!pms.isSelected()) {
				dataList.add(pms);
			}
		}
	}
	ScrollTable table = new ScrollTable();
	SelectParameterTableModel model = new SelectParameterTableModel(table, getParameterEstimationTask());
	table.setModel(model);
	model.refreshData();
	
	panel.add(new JScrollPane(table), BorderLayout.CENTER);
	panel.setPreferredSize(new Dimension(400,300));
	int returnCode = DialogUtils.showComponentOKCancelDialog(this, panel, "Select Parameters");
	if (returnCode == JOptionPane.OK_OPTION) {
		for (int row : table.getSelectedRows()) {
			ParameterMappingSpec pms = model.getValueAt(row);
			pms.setSelected(true);
		}
	}
}

/**
 * Return the JMenuItemCopy property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemCopy() {
	if (copyMenuItem == null) {
		try {
			copyMenuItem = new javax.swing.JMenuItem();
			copyMenuItem.setName("JMenuItemCopy");
			copyMenuItem.setText("Copy");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return copyMenuItem;
}


/**
 * Return the JMenuItemCopyAll property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemCopyAll() {
	if (copyAllMenuItem == null) {
		try {
			copyAllMenuItem = new javax.swing.JMenuItem();
			copyAllMenuItem.setName("JMenuItemCopyAll");
			copyAllMenuItem.setText("Copy All");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return copyAllMenuItem;
}


/**
 * Return the JMenuItemPaste property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemPaste() {
	if (pasteMenuItem == null) {
		try {
			pasteMenuItem = new javax.swing.JMenuItem();
			pasteMenuItem.setName("JMenuItemPaste");
			pasteMenuItem.setText("Paste");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pasteMenuItem;
}


/**
 * Return the JMenuItemPasteAll property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemPasteAll() {
	if (pasteAllMenuItem == null) {
		try {
			pasteAllMenuItem = new javax.swing.JMenuItem();
			pasteAllMenuItem.setName("JMenuItemPasteAll");
			pasteAllMenuItem.setText("Paste All");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pasteAllMenuItem;
}


/**
 * Return the JPopupMenuCopyPaste property value.
 * @return javax.swing.JPopupMenu
 */
private JPopupMenu getJPopupMenuCopyPaste() {
	if (copyPastePopupMenu == null) {
		try {
			copyPastePopupMenu = new javax.swing.JPopupMenu();
			copyPastePopupMenu.setName("JPopupMenuCopyPaste");
			copyPastePopupMenu.add(getJMenuItemCopy());
			copyPastePopupMenu.add(getJMenuItemCopyAll());
			copyPastePopupMenu.add(getJMenuItemPaste());
			copyPastePopupMenu.add(getJMenuItemPasteAll());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return copyPastePopupMenu;
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
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
private JSortTable getScrollPaneTable() {
	if (parameterMappingTable == null) {
		try {
			parameterMappingTable = new JSortTable();
			parameterMappingTable.setName("ScrollPaneTable");
			parameterMappingTableModel = new ParameterMappingTableModel(parameterMappingTable);
			parameterMappingTable.setScrollTableActionManager(new InternalScrollTableActionManager(parameterMappingTable));
			parameterMappingTable.setModel(parameterMappingTableModel);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return parameterMappingTable;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getJMenuItemCopy().addActionListener(ivjEventHandler);
	getJMenuItemCopyAll().addActionListener(ivjEventHandler);
	getJMenuItemPaste().addActionListener(ivjEventHandler);
	getJMenuItemPasteAll().addActionListener(ivjEventHandler);
//	getScrollPaneTable().addMouseListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ModelParameterPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(655, 226);
		addButton = new JButton(VCellIcons.addIcon);
		deleteButton = new JButton(VCellIcons.deleteIcon);
		deleteButton.setEnabled(false);
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add(addButton);
		toolBar.add(deleteButton);
		add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
		add(toolBar, BorderLayout.NORTH);
		
		addButton.addActionListener(ivjEventHandler);
		deleteButton.addActionListener(ivjEventHandler);
		getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);
		initConnections();
		getScrollPaneTable().setDefaultRenderer(Double.class,new DefaultScrollTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus, int row,
					int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setHorizontalAlignment(RIGHT);
				return this;			
			}
			
		});
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void jMenuItemCopy_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(actionEvent.getSource() == getJMenuItemCopy() || actionEvent.getSource() == getJMenuItemCopyAll()){
		try{
			//
			//Copy Optimization Parameters (Initial Guess or Solution)
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

			SimulationContext sc = getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext();
			MathSymbolMapping msm = null;
			try {
				MathMapping mm = sc.createNewMathMapping();
				msm = mm.getMathSymbolMapping();
			}catch (Exception e){
				e.printStackTrace(System.out);
				DialogUtils.showWarningDialog(this, "current math not valid, some paste operations will be limited\n\nreason: "+e.getMessage());
			}
			boolean bInitialGuess = (getScrollPaneTable().getSelectedColumn() == ParameterMappingTableModel.COLUMN_CURRENTVALUE);
			ParameterMappingSpec[] parameterMappingSpecs = new ParameterMappingSpec[rows.length];
			java.util.Vector<SymbolTableEntry> primarySymbolTableEntriesV = new java.util.Vector<SymbolTableEntry>();
			java.util.Vector<SymbolTableEntry> alternateSymbolTableEntriesV = new java.util.Vector<SymbolTableEntry>();
			java.util.Vector<Expression> resolvedValuesV = new java.util.Vector<Expression>();

			//
			//Create formatted string for text/spreadsheet pasting.
			//
			StringBuffer sb = new StringBuffer();
			sb.append("\"Parameters for (Optimization Task)"+getParameterEstimationTask().getName()+" -> "+
					"(BioModel)"+getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext().getBioModel().getName()+" -> "+
					"(App)"+getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext().getName()+"\"\n"
			);
			sb.append("\"Parameter Name\"\t\""+(bInitialGuess?"Initial Guess":"Solution")+"\"\n");
			for(int i=0;i<rows.length;i+= 1){
				ParameterMappingSpec pms = parameterMappingTableModel.getValueAt(rows[i]);
				parameterMappingSpecs[i] = pms;
				primarySymbolTableEntriesV.add(pms.getModelParameter());
				if(msm != null){
					alternateSymbolTableEntriesV.add(msm.getVariable(pms.getModelParameter()));
				}else{
					alternateSymbolTableEntriesV.add(null);
				}
				Double resolvedValue = null;
				if (!bInitialGuess){
					resolvedValue = getParameterEstimationTask().getCurrentSolution(pms);
				}
				if (resolvedValue == null){
					resolvedValue = new Double(pms.getCurrent());
				}
				resolvedValuesV.add(new Expression(resolvedValue.doubleValue()));
				sb.append(
					"\""+parameterMappingSpecs[i].getModelParameter().getName()+"\"\t"+resolvedValue+"\n"
				);
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
			PopupGenerator.showErrorDialog(this, "ParameterMappingPanel copy failed.  "+e.getMessage(), e);
		}
	}
}


/**
 * Comment
 */
private void jMenuItemPaste_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	java.util.Vector<String> pasteDescriptionsV = new java.util.Vector<String>();
	java.util.Vector<Expression> newExpressionsV = new java.util.Vector<Expression>();
	java.util.Vector<ParameterMappingSpec> changedParametersV = new java.util.Vector<ParameterMappingSpec>();
	try{
		if(actionEvent.getSource() == getJMenuItemPaste() || actionEvent.getSource() == getJMenuItemPasteAll()){
			Object pasteThis = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);

			SimulationContext sc = getParameterEstimationTask().getModelOptimizationSpec().getSimulationContext();
			MathSymbolMapping msm = null;
			Exception mathMappingException = null;
			try {
				MathMapping mm = sc.createNewMathMapping();
				msm = mm.getMathSymbolMapping();
			}catch (Exception e){
				mathMappingException = e;
				e.printStackTrace(System.out);
			}
			//if(msm == null){
				//try{
					//getParameterEstimationTask().refreshMappings();
					//msm = getParameterEstimationTask().getMathSymbolMapping();
				//}catch(Exception e){
					//e.printStackTrace();
				//}
			//}
			
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
				ParameterMappingSpec pms = parameterMappingTableModel.getValueAt(rows[i]);
				try{
					if(pasteThis instanceof VCellTransferable.ResolvedValuesSelection){
						VCellTransferable.ResolvedValuesSelection rvs =
							(VCellTransferable.ResolvedValuesSelection)pasteThis;
						for(int j=0;j<rvs.getPrimarySymbolTableEntries().length;j+= 1){
							ParameterMappingSpec pasteDestination = null;
							Parameter clipboardBiologicalParameter = null;
							if(rvs.getPrimarySymbolTableEntries()[j] instanceof Parameter){
								clipboardBiologicalParameter = (Parameter)rvs.getPrimarySymbolTableEntries()[j];
							}else if(rvs.getAlternateSymbolTableEntries() != null &&
									rvs.getAlternateSymbolTableEntries()[j] instanceof Parameter){
								clipboardBiologicalParameter = (Parameter)rvs.getAlternateSymbolTableEntries()[j];
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
									if(localMathVariable != null){
										SymbolTableEntry[] localBiologicalSymbolArr =  msm.getBiologicalSymbol(localMathVariable);
										for(int k=0;k<localBiologicalSymbolArr.length;k+= 1){
											if(localBiologicalSymbolArr[k] == pms.getModelParameter()){
												pasteDestination = pms;
												break;
											}
										}
									}
								}
							}else{
								if(pms.getModelParameter().getName().equals(clipboardBiologicalParameter.getName()) &&
									pms.getModelParameter().getClass().equals(clipboardBiologicalParameter.getClass()) &&
									pms.getModelParameter().getNameScope().getName().equals(clipboardBiologicalParameter.getNameScope().getName())){
									pasteDestination = pms;
								}
							}

							if(pasteDestination != null){
								changedParametersV.add(pasteDestination);
								newExpressionsV.add(rvs.getExpressionValues()[j]);
								pasteDescriptionsV.add(
									VCellCopyPasteHelper.formatPasteList(
										pms.getModelParameter().getNameScope().getName(),
										pms.getModelParameter().getName(),
										pasteDestination.getCurrent()+"",
										rvs.getExpressionValues()[j].infix())
								);
							}
						}
					}
				}catch(Throwable e){
					if(errors == null){errors = new StringBuffer();}
					errors.append(pms.getModelParameter().getName()+" ("+e.getClass().getName()+") "+e.getMessage()+"\n");
				}
			}
			if(errors != null){
				throw new Exception(errors.toString());
			}

		}
	}catch(Throwable e){
		PopupGenerator.showErrorDialog(this, "Paste failed during pre-check (no changes made).\n"+e.getMessage(), e);
		return;
	}

	//Do paste
	try{
		if(pasteDescriptionsV.size() > 0){
			String[] pasteDescriptionArr = new String[pasteDescriptionsV.size()];
			pasteDescriptionsV.copyInto(pasteDescriptionArr);
			ParameterMappingSpec[] changedParametersArr = new ParameterMappingSpec[changedParametersV.size()];
			changedParametersV.copyInto(changedParametersArr);
			Expression[] newExpressionsArr = new Expression[newExpressionsV.size()];
			newExpressionsV.copyInto(newExpressionsArr);
			VCellCopyPasteHelper.chooseApplyPaste(this, pasteDescriptionArr,changedParametersArr,newExpressionsArr);
		}else{
			PopupGenerator.showInfoDialog(this, "No paste items match the destination (no changes made).");
		}
	}catch(Throwable e){
		PopupGenerator.showErrorDialog(this, "Paste Error\n"+e.getMessage(), e);
	}

}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ParameterMappingPanel aParameterMappingPanel;
		aParameterMappingPanel = new ParameterMappingPanel();
		frame.setContentPane(aParameterMappingPanel);
		frame.setSize(aParameterMappingPanel.getSize());
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
 * Comment
 */
private void popupCopyPaste(java.awt.event.MouseEvent mouseEvent) {
	if(mouseEvent.isPopupTrigger()){
		Object obj = VCellTransferable.getFromClipboard(VCellTransferable.OBJECT_FLAVOR);
		boolean bPastable =
			obj instanceof VCellTransferable.ResolvedValuesSelection;

		boolean bInitGuessSelected = getScrollPaneTable().getSelectedColumn() == ParameterMappingTableModel.COLUMN_CURRENTVALUE;
		bPastable = bPastable && bInitGuessSelected;
//		boolean bSolutionSelected = getScrollPaneTable().getSelectedColumn() ==  ParameterMappingTableModel.COLUMN_SOLUTION;
//		boolean bSomethingSelected = (bInitGuessSelected || bSolutionSelected);

		if(bInitGuessSelected){
			getJMenuItemPaste().setVisible(true);
			getJMenuItemPasteAll().setVisible(true);
			getJMenuItemCopy().setText("Copy 'Initial Guess'");
			getJMenuItemCopyAll().setText("Copy All 'Initial Guess'");
			getJMenuItemPaste().setText("Paste 'Initial Guess'");
			getJMenuItemPasteAll().setText("Paste All 'Initial Guess'");
//		}else if(bSolutionSelected){
//			getJMenuItemPaste().setVisible(false);
//			getJMenuItemPasteAll().setVisible(false);
//			getJMenuItemCopy().setText("Copy 'Solution'");
//			getJMenuItemCopyAll().setText("Copy All 'Solution'");
//			getJMenuItemPaste().setText("Paste");
//			getJMenuItemPasteAll().setText("Paste All");
		}else{
//			PopupGenerator.showInfoDialog(this, "For Copy/Paste select a cell in the \"Initial Guess\" or \"Solution\" column");
			return;
		}
		
		getJMenuItemPaste().setEnabled(bPastable/* && bSomethingSelected*/);
		getJMenuItemPasteAll().setEnabled(bPastable);
//		getJMenuItemCopy().setEnabled(bSomethingSelected);
//		getJMenuItemCopyAll().setEnabled(bSomethingSelected);
		getJPopupMenuCopyPaste().show(getScrollPaneTable(),mouseEvent.getX(),mouseEvent.getY());
	}
}

/**
 * Sets the parameterEstimationTask property (cbit.vcell.modelopt.ParameterEstimationTask) value.
 * @param parameterEstimationTask The new value for the property.
 * @see #getParameterEstimationTask
 */
public void setParameterEstimationTask(ParameterEstimationTask parameterEstimationTask) {
	ParameterEstimationTask oldValue = fieldParameterEstimationTask;
	fieldParameterEstimationTask = parameterEstimationTask;
	firePropertyChange("parameterEstimationTask", oldValue, parameterEstimationTask);
}

}
