package cbit.vcell.modelopt.gui;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.desktop.VCellCopyPasteHelper;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Variable;
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
public class ParameterMappingPanel extends javax.swing.JPanel {
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private org.vcell.util.gui.sorttable.JSortTable ivjScrollPaneTable = null;
	private ParameterMappingTableModel ivjparameterMappingTableModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.vcell.modelopt.ParameterEstimationTask fieldParameterEstimationTask = null;
	private ParameterMappingTableCellRenderer ivjParameterMappingTableCellRenderer1 = null;
	private javax.swing.JMenuItem ivjJMenuItemCopy = null;
	private javax.swing.JMenuItem ivjJMenuItemCopyAll = null;
	private javax.swing.JMenuItem ivjJMenuItemPaste = null;
	private javax.swing.JMenuItem ivjJMenuItemPasteAll = null;
	private javax.swing.JPopupMenu ivjJPopupMenuCopyPaste = null;
	private org.vcell.util.gui.sorttable.JSortTable ivjJSortTableThis = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ParameterMappingPanel.this.getJMenuItemCopy()) 
				connEtoC3(e);
			if (e.getSource() == ParameterMappingPanel.this.getJMenuItemCopyAll()) 
				connEtoC4(e);
			if (e.getSource() == ParameterMappingPanel.this.getJMenuItemPaste()) 
				connEtoC5(e);
			if (e.getSource() == ParameterMappingPanel.this.getJMenuItemPasteAll()) 
				connEtoC6(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == ParameterMappingPanel.this.getJSortTableThis()) 
				connEtoC1(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == ParameterMappingPanel.this.getJSortTableThis()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ParameterMappingPanel.this && (evt.getPropertyName().equals("parameterEstimationTask"))) 
				connEtoM1(evt);
		};
	};

/**
 * ModelParameterPanel constructor comment.
 */
public ParameterMappingPanel() {
	super();
	initialize();
}

/**
 * ModelParameterPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ParameterMappingPanel(java.awt.LayoutManager layout) {
	super(layout);
}


/**
 * ModelParameterPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ParameterMappingPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}


/**
 * ModelParameterPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ParameterMappingPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}


/**
 * connEtoC1:  (this12.mouse.mousePressed(java.awt.event.MouseEvent) --> ParameterMappingPanel.popupCopyPaste(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		this.popupCopyPaste(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (this12.mouse.mouseReleased(java.awt.event.MouseEvent) --> ParameterMappingPanel.popupCopyPaste(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
	try {
		this.popupCopyPaste(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JMenuItemCopy.action.actionPerformed(java.awt.event.ActionEvent) --> ParameterMappingPanel.jMenuItemCopy_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		this.jMenuItemCopy_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JMenuItemCopyAll.action.actionPerformed(java.awt.event.ActionEvent) --> ParameterMappingPanel.jMenuItemCopy_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		this.jMenuItemCopy_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JMenuItemPaste.action.actionPerformed(java.awt.event.ActionEvent) --> ParameterMappingPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		this.jMenuItemPaste_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JMenuItemPasteAll.action.actionPerformed(java.awt.event.ActionEvent) --> ParameterMappingPanel.jMenuItemPaste_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		this.jMenuItemPaste_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (ParameterMappingPanel.initialize() --> ParameterMappingPanel.parameterMappingPanel_Initialize()V)
 */
private void connEtoC7() {
	try {
		this.parameterMappingPanel_Initialize();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (ParameterMappingPanel.parameterEstimationTask --> parameterMappingTableModel.parameterEstimationTask)
 * @param arg1 java.beans.PropertyChangeEvent
 */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		getparameterMappingTableModel().setParameterEstimationTask(this.getParameterEstimationTask());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (ParameterMappingPanel.initialize() --> ScrollPaneTable.setDefaultRenderer(Ljava.lang.Class;Ljavax.swing.table.TableCellRenderer;)V)
 */
private void connEtoM2() {
	try {
		getScrollPaneTable().setDefaultRenderer(Double.class, getParameterMappingTableCellRenderer1());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetTarget:  (modelParameterTableModel.this <--> ScrollPaneTable.model)
 */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getScrollPaneTable().setModel(getparameterMappingTableModel());
		getScrollPaneTable().createDefaultColumnsFromModel();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetTarget:  (ScrollPaneTable.this <--> this12.this)
 */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		setJSortTableThis(getScrollPaneTable());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Return the JMenuItemCopy property value.
 * @return javax.swing.JMenuItem
 */
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
private javax.swing.JMenuItem getJMenuItemCopyAll() {
	if (ivjJMenuItemCopyAll == null) {
		try {
			ivjJMenuItemCopyAll = new javax.swing.JMenuItem();
			ivjJMenuItemCopyAll.setName("JMenuItemCopyAll");
			ivjJMenuItemCopyAll.setText("Copy All");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCopyAll;
}


/**
 * Return the JMenuItemPaste property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemPaste() {
	if (ivjJMenuItemPaste == null) {
		try {
			ivjJMenuItemPaste = new javax.swing.JMenuItem();
			ivjJMenuItemPaste.setName("JMenuItemPaste");
			ivjJMenuItemPaste.setText("Paste");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPaste;
}


/**
 * Return the JMenuItemPasteAll property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemPasteAll() {
	if (ivjJMenuItemPasteAll == null) {
		try {
			ivjJMenuItemPasteAll = new javax.swing.JMenuItem();
			ivjJMenuItemPasteAll.setName("JMenuItemPasteAll");
			ivjJMenuItemPasteAll.setText("Paste All");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPasteAll;
}


/**
 * Return the JPopupMenuCopyPaste property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getJPopupMenuCopyPaste() {
	if (ivjJPopupMenuCopyPaste == null) {
		try {
			ivjJPopupMenuCopyPaste = new javax.swing.JPopupMenu();
			ivjJPopupMenuCopyPaste.setName("JPopupMenuCopyPaste");
			ivjJPopupMenuCopyPaste.add(getJMenuItemCopy());
			ivjJPopupMenuCopyPaste.add(getJMenuItemCopyAll());
			ivjJPopupMenuCopyPaste.add(getJMenuItemPaste());
			ivjJPopupMenuCopyPaste.add(getJMenuItemPasteAll());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenuCopyPaste;
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}


/**
 * Return the this12 property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
private JSortTable getJSortTableThis() {
	return ivjJSortTableThis;
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
 * Return the ParameterMappingTableCellRenderer1 property value.
 * @return cbit.vcell.modelopt.gui.ParameterMappingTableCellRenderer
 */
private ParameterMappingTableCellRenderer getParameterMappingTableCellRenderer1() {
	if (ivjParameterMappingTableCellRenderer1 == null) {
		try {
			ivjParameterMappingTableCellRenderer1 = new ParameterMappingTableCellRenderer();
			ivjParameterMappingTableCellRenderer1.setName("ParameterMappingTableCellRenderer1");
			ivjParameterMappingTableCellRenderer1.setText("ParameterMappingTableCellRenderer1");
			ivjParameterMappingTableCellRenderer1.setBounds(78, 491, 225, 16);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjParameterMappingTableCellRenderer1;
}

/**
 * Return the parameterMappingTableModel property value.
 * @return cbit.vcell.modelopt.gui.ParameterMappingTableModel
 */
private ParameterMappingTableModel getparameterMappingTableModel() {
	if (ivjparameterMappingTableModel == null) {
		try {
			ivjparameterMappingTableModel = new ParameterMappingTableModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjparameterMappingTableModel;
}


/**
 * Return the ScrollPaneTable property value.
 * @return cbit.vcell.messaging.admin.sorttable.JSortTable
 */
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
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
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ModelParameterPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(655, 226);
		add(getJScrollPane1(), "Center");
		initConnections();
		connEtoM2();
		connEtoC7();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void jMenuItemCopy_ActionPerformed(java.awt.event.ActionEvent actionEvent) throws Exception{
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
				MathMapping mm = new cbit.vcell.mapping.MathMapping(sc);
				msm = mm.getMathSymbolMapping();
			}catch (Exception e){
				e.printStackTrace(System.out);
				DialogUtils.showWarningDialog(this, "current math not valid, some paste operations will be limited\n\nreason: "+e.getMessage());
			}
			boolean bInitialGuess = (getScrollPaneTable().getSelectedColumn() == ParameterMappingTableModel.COLUMN_CURRENTVALUE);
			cbit.vcell.modelopt.ParameterMappingSpec[] parameterMappingSpecs = new cbit.vcell.modelopt.ParameterMappingSpec[rows.length];
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
				ParameterMappingSpec pms = (ParameterMappingSpec)getparameterMappingTableModel().getData().get(rows[i]);
				parameterMappingSpecs[i] = pms;
				primarySymbolTableEntriesV.add(pms.getModelParameter());
				if(msm != null){
					alternateSymbolTableEntriesV.add(msm.getVariable(pms.getModelParameter()));
				}else{
					alternateSymbolTableEntriesV.add(null);
				}
				Double resolvedValue = null;
				if(!bInitialGuess){
					resolvedValue = ((Double)getparameterMappingTableModel().getValueAt(rows[i],ParameterMappingTableModel.COLUMN_SOLUTION));
					if(resolvedValue == null){
						resolvedValue = ((Double)getparameterMappingTableModel().getValueAt(rows[i],ParameterMappingTableModel.COLUMN_CURRENTVALUE));
					}
				}else{
					resolvedValue = (Double)getparameterMappingTableModel().getValueAt(rows[i],ParameterMappingTableModel.COLUMN_CURRENTVALUE);
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
				MathMapping mm = new MathMapping(sc);
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
				ParameterMappingSpec pms = (ParameterMappingSpec)getparameterMappingTableModel().getData().get(rows[i]);
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
private void parameterMappingPanel_Initialize() {
	
	getScrollPaneTable().setDefaultRenderer(cbit.gui.ScopedExpression.class,new cbit.vcell.model.gui.ScopedExpressionTableCellRenderer());
	
	getparameterMappingTableModel().addTableModelListener(
		new javax.swing.event.TableModelListener(){
			public void tableChanged(javax.swing.event.TableModelEvent e){
				cbit.vcell.model.gui.ScopedExpressionTableCellRenderer.formatTableCellSizes(getScrollPaneTable(),null,null);
			}
		}
	);
}


/**
 * Comment
 */
private void popupCopyPaste(java.awt.event.MouseEvent mouseEvent) {
	if(mouseEvent.isPopupTrigger()){
		Object obj = cbit.vcell.desktop.VCellTransferable.getFromClipboard(cbit.vcell.desktop.VCellTransferable.OBJECT_FLAVOR);
		boolean bPastable =
			obj instanceof VCellTransferable.ResolvedValuesSelection;

		boolean bInitGuessSelected = getScrollPaneTable().getSelectedColumn() == ParameterMappingTableModel.COLUMN_CURRENTVALUE;
		bPastable = bPastable && bInitGuessSelected;
		boolean bSolutionSelected = getScrollPaneTable().getSelectedColumn() ==  ParameterMappingTableModel.COLUMN_SOLUTION;
		boolean bSomethingSelected =
			//getScrollPaneTable().getSelectedRowCount() > 0 &&
			(bInitGuessSelected || bSolutionSelected);

		if(bInitGuessSelected){
			getJMenuItemPaste().setVisible(true);
			getJMenuItemPasteAll().setVisible(true);
			getJMenuItemCopy().setText("Copy 'Initial Guess'");
			getJMenuItemCopyAll().setText("Copy All 'Initial Guess'");
			getJMenuItemPaste().setText("Paste 'Initial Guess'");
			getJMenuItemPasteAll().setText("Paste All 'Initial Guess'");
		}else if(bSolutionSelected){
			getJMenuItemPaste().setVisible(false);
			getJMenuItemPasteAll().setVisible(false);
			getJMenuItemCopy().setText("Copy 'Solution'");
			getJMenuItemCopyAll().setText("Copy All 'Solution'");
			getJMenuItemPaste().setText("Paste");
			getJMenuItemPasteAll().setText("Paste All");
		}else{
			PopupGenerator.showInfoDialog(this, "For Copy/Paste select a cell in the \"Initial Guess\" or \"Solution\" column");
			return;
		}
		
		getJMenuItemPaste().setEnabled(bPastable && bSomethingSelected);
		getJMenuItemPasteAll().setEnabled(bPastable);
		getJMenuItemCopy().setEnabled(bSomethingSelected);
		getJMenuItemCopyAll().setEnabled(bSomethingSelected);
		getJPopupMenuCopyPaste().show(getScrollPaneTable(),mouseEvent.getX(),mouseEvent.getY());
	}
}


/**
 * Set the this12 to a new value.
 * @param newValue cbit.vcell.messaging.admin.sorttable.JSortTable
 */
private void setJSortTableThis(org.vcell.util.gui.sorttable.JSortTable newValue) {
	if (ivjJSortTableThis != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjJSortTableThis != null) {
				ivjJSortTableThis.removeMouseListener(ivjEventHandler);
			}
			ivjJSortTableThis = newValue;

			/* Listen for events from the new object */
			if (ivjJSortTableThis != null) {
				ivjJSortTableThis.addMouseListener(ivjEventHandler);
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
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