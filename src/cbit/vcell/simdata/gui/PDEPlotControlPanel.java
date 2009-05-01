package cbit.vcell.simdata.gui;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.VariableType;

import javax.swing.*;

import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.NumberUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cbit.gui.DialogUtils;
import cbit.gui.ZEnforcer;
import cbit.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 10:29:53 PM)
 * @author: Ion Moraru
 */
public class PDEPlotControlPanel extends JPanel {
	private JComboBox filterComboBox;
	private JLabel ivjJLabel1 = null;
	private JTextField ivjJTextField1 = null;
	private cbit.vcell.simdata.PDEDataContext fieldPdeDataContext = null;
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.gui.DefaultListModelCivilized ivjDefaultListModelCivilized1 = null;
	private JList ivjJList1 = null;
	private JPanel ivjJPanel1 = null;
	private JPanel ivjJPanel2 = null;
	private JScrollPane ivjJScrollPane1 = null;
	private cbit.vcell.simdata.PDEDataContext ivjpdeDataContext1 = null;
	private JLabel ivjJLabelMax = null;
	private JLabel ivjJLabelMin = null;
	private JSlider ivjJSliderTime = null;
	private JSplitPane ivjJSplitPane1 = null;
	private boolean ivjConnPtoP4Aligning = false;
	private BoundedRangeModel ivjmodel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private cbit.image.DisplayAdapterService fieldDisplayAdapterService = new cbit.image.DisplayAdapterService();
	private boolean ivjConnPtoP3Aligning = false;
	private cbit.image.DisplayAdapterService ivjdisplayAdapterService1 = null;
	private JPanel ivjTimeSliderJPanel = null;
	private JButton ivjAddFunctionButton = null;
	private Vector<AnnotatedFunction> functionsList = new Vector<AnnotatedFunction>();  //  @jve:decl-index=0:

	public static interface DataIdentifierFilter{
		boolean accept(String filterSetName,DataIdentifier dataidentifier);
		String[] getFilterSetNames();
		String getDefaultFilterName();
		boolean isAcceptAll(String filterSetName);
	};
	
	DataIdentifierFilter DEFAULT_DATAIDENTIFIER_FILTER =
		new DataIdentifierFilter(){
			private String ALL = "All Variables";
			private String VOLUME_FILTER_SET = "Volume Variables";
			private String MEMBRANE_FILTER_SET = "Membrane Variables";
			private String USER_DEFINED_FILTER_SET = "User Functions";
			private String[] FILTER_SET_NAMES = new String[] {ALL,VOLUME_FILTER_SET,MEMBRANE_FILTER_SET,USER_DEFINED_FILTER_SET};
			public boolean accept(String filterSetName,DataIdentifier dataidentifier) {
				if(filterSetName.equals(ALL)){
					return true;
				}else if(filterSetName.equals(VOLUME_FILTER_SET)){
					return dataidentifier.getVariableType().equals(VariableType.VOLUME);
				}else if(filterSetName.equals(MEMBRANE_FILTER_SET)){
					return dataidentifier.getVariableType().equals(VariableType.MEMBRANE);
				}else if(filterSetName.equals(USER_DEFINED_FILTER_SET)){
					if(functionsList != null){
						for (int i = 0; i < functionsList.size(); i++) {
							if(functionsList.elementAt(i).isUserDefined() && functionsList.elementAt(i).getName().equals(dataidentifier.getName())){
								return true;
							}
						}
					}
					return false;
				}
				throw new IllegalArgumentException("PDEPlotControlPanel.DEFAULT_DATAIDENTIFIER_FILTE: Unknown Filter name "+filterSetName);
			}
			public String getDefaultFilterName() {
				return ALL;
			}
			public String[] getFilterSetNames() {
				return FILTER_SET_NAMES;
			}
			public boolean isAcceptAll(String filterSetName){
				return filterSetName.equals(ALL);
			}
		};
	
	DataIdentifierFilter dataIdentifierFilter;
	
	private ActionListener filterChangeActionListener =
		new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				filterVariableNames();
			}
	};
	
class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener, javax.swing.event.ListDataListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getJTextField1()) 
				connEtoC2(e);
			if (e.getSource() == PDEPlotControlPanel.this.getAddFunctionButton()) 
				connEtoC7(e);
		};
		public void contentsChanged(javax.swing.event.ListDataEvent e) {};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getJTextField1()) 
				connEtoC6(e);
		};
		public void intervalAdded(javax.swing.event.ListDataEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getDefaultListModelCivilized1()) 
				connEtoM4(e);
		};
		public void intervalRemoved(javax.swing.event.ListDataEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == PDEPlotControlPanel.this && (evt.getPropertyName().equals("pdeDataContext"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == PDEPlotControlPanel.this.getpdeDataContext1() && (evt.getPropertyName().equals("timePoints"))) 
				connEtoC3(evt);
			if (evt.getSource() == PDEPlotControlPanel.this.getJSliderTime() && (evt.getPropertyName().equals("model"))) 
				connPtoP4SetTarget();
			if (evt.getSource() == PDEPlotControlPanel.this && (evt.getPropertyName().equals("displayAdapterService"))) 
				connPtoP3SetTarget();
			if (evt.getSource() == PDEPlotControlPanel.this.getdisplayAdapterService1() && (evt.getPropertyName().equals("autoScale"))) 
				connEtoC9(evt);
			if (evt.getSource() == PDEPlotControlPanel.this.getdisplayAdapterService1() && (evt.getPropertyName().equals("customScaleRange"))) 
				connEtoC5(evt);
			if (evt.getSource() == PDEPlotControlPanel.this.getpdeDataContext1() && (evt.getPropertyName().equals("dataIdentifiers"))) 
				connEtoM8(evt);
		};
		public void stateChanged(javax.swing.event.ChangeEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getmodel1()) 
				connEtoC1(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getJList1()) 
				connEtoC8(e);
		};
	};

/**
 * PDEPlotControlPanel constructor comment.
 */
public PDEPlotControlPanel() {
	super();
	initialize();
	setDataIdentifierFilter(DEFAULT_DATAIDENTIFIER_FILTER);
}

public void setDataIdentifierFilter(DataIdentifierFilter dataIdentifierFilter) {
	this.dataIdentifierFilter = dataIdentifierFilter;
	filterComboBox.removeActionListener(filterChangeActionListener);
	filterComboBox.removeAllItems();
	if(dataIdentifierFilter != null){
		String[] filterSetNames = this.dataIdentifierFilter.getFilterSetNames();
		for (int i = 0; i < filterSetNames.length; i++) {
			filterComboBox.addItem(filterSetNames[i]);
		}
		filterComboBox.setSelectedItem(dataIdentifierFilter.getDefaultFilterName());
		filterComboBox.addActionListener(filterChangeActionListener);
	}else{
		filterComboBox.addItem("All Variables");
		filterComboBox.setSelectedIndex(0);
	}
	filterVariableNames();
}


/**
 * Comment
 */
private void addFunction() {

	initFunctionsList();
	
	cbit.vcell.simdata.gui.FunctionSpecifierPanel fsp =
		new cbit.vcell.simdata.gui.FunctionSpecifierPanel();
	fsp.initFunctionInfo(
			getJList1().getSelectedValue().toString(),
			getPdeDataContext().getDataIdentifiers(),
			functionsList.toArray(new AnnotatedFunction[0]));
	final javax.swing.JDialog jd = new javax.swing.JDialog();
	jd.setTitle("View/Add/Delete/Edit Functions");
	jd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	jd.setModal(true);
	jd.getContentPane().add(fsp);
//	jd.pack();
	jd.setSize(450,250);
	BeanUtils.centerOnComponent(jd, this);
	
	fsp.addActionListener(
		new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				jd.dispose();
			}
		}
	);

	while(true){
		ZEnforcer.showModalDialogOnTop(jd);
		if(fsp.getFunctionOp() == FunctionSpecifierPanel.FUNC_OP_CANCEL){
			break;
		}
		AnnotatedFunction newFunction = null;
		try{
			if(fsp.getFunctionOp() == FunctionSpecifierPanel.FUNC_OP_ADDNEW){
				newFunction = fsp.getNewUserCreatedAnnotatedFunction(true);
				getPdeDataContext().addFunctions(
						new AnnotatedFunction[] {newFunction}, new boolean[] {false});
				functionsList.add(newFunction);
			}else if(fsp.getFunctionOp() == FunctionSpecifierPanel.FUNC_OP_DELETE){
				getPdeDataContext().removeFunction(fsp.getSelectedAnnotatedFunction());
				functionsList.removeElement(fsp.getSelectedAnnotatedFunction());
			}else if(fsp.getFunctionOp() == FunctionSpecifierPanel.FUNC_OP_REPLACE){
				newFunction = fsp.getNewUserCreatedAnnotatedFunction(true);
				getPdeDataContext().addFunctions(
						new AnnotatedFunction[] {newFunction}, new boolean[] {true});
				functionsList.remove(fsp.getSelectedAnnotatedFunction());
				functionsList.add(newFunction);
				getPdeDataContext().externalRefresh();
			}
			break;
		}catch(Exception e){
			e.printStackTrace();
			PopupGenerator.showErrorDialog("Error "+e.getMessage());
			continue;
		}finally{
			new Thread(
				new Runnable(){
					public void run(){
						try{
							BeanUtils.setCursorThroughout(PDEPlotControlPanel.this, Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							getPdeDataContext().refreshIdentifiers();
						}finally{
							BeanUtils.setCursorThroughout(PDEPlotControlPanel.this, Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
					}
				}
			).start();
		}
	}
	
//	if(bReplaceFunc){
//		functionsList.remove(defaultFunction);
//		getpdeDataContext1().addFunctions(new AnnotatedFunction[] {newFunction},new boolean[] {true});
//		functionsList.add(newFunction);
//	}else{
//		getpdeDataContext1().addFunctions(new AnnotatedFunction[] {newFunction},new boolean[] {false});
//		functionsList.add(newFunction);
//	}

	
	
	
	
//	// Get a default name for the user defined function
//	String[] existingVariableNames = getpdeDataContext1().getVariableNames();
//	String defaultName = null;
//	int count = 1;
//	boolean nameUsed = true;
//	while (nameUsed) {
//		nameUsed = false;
//		defaultName = getpdeDataContext1().getVariableName()+"_"+count;
//		for (int i = 0; existingVariableNames != null && i < existingVariableNames.length; i++){
//			if (existingVariableNames[i].equals(defaultName)) {
//				nameUsed = true;
//				break;
//			}
//		}
//		count++;
//	}
//
//	initFunctionsList();
//	defaultName = 
//		(isDeletableFunction(getpdeDataContext1().getVariableName())?getpdeDataContext1().getVariableName():defaultName);
//	String defaultExpression = "0.0";
//	AnnotatedFunction defaultFunction = null;
//	for(int i=0;functionsList != null && i<functionsList.size();i+= 1){
//		if(functionsList.elementAt(i).getName().equals(getpdeDataContext1().getVariableName())){
//			defaultFunction = functionsList.elementAt(i);
////			defaultExpression = functionsList.elementAt(i).getSimplifiedExpression().infix();
//			defaultExpression = functionsList.elementAt(i).getExpression().infix();
//		}
//	}
//	javax.swing.JPanel fnPanel = getFunctionPanel();
//	getFunctionNameTextField().setText(defaultName);
//	getFunctionExpressionTextField().setText(defaultExpression);
//	fnPanel.setSize(400,250);
//
//	//
//	// Show the editor with a default name and default expression for the function. If the OK option is chosen, 
//	// get the new name and expression for the function and add it to the list of columns. 
//	// Else, pop-up an error dialog indicating that function cannot be added.
//	//
//	int ok = -1;
//	boolean bReplaceFunc = false;
//	while (true) {
//		// User-defined Function name cannot have spaces; if user inputs a name with spaces, 
//		// pop up the 'Add Function' dialog to prompt the user to input a function name without spaces.
//		ok = PopupGenerator.showComponentOKCancelDialog(this,fnPanel,"Add Function");
//		//ok = JOptionPane.showOptionDialog(this, fnPanel, "Add Function" , 0, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel"}, null);
//		if (ok != javax.swing.JOptionPane.OK_OPTION) {
//			break;
//		}
//		String fnName = getFunctionNameTextField().getText();
//		if (fnName.indexOf(" " ) > 0) {
//			cbit.vcell.client.PopupGenerator.showErrorDialog("Function name cannot have spaces, please change function name.");
//			continue;
//		}
//		try{
//			//preliminary expression check
//			new Expression(getFunctionExpressionTextField().getText()).flatten();
//		}catch(Exception e){
//			PopupGenerator.showErrorDialog("Error \n"+getFunctionExpressionTextField().getText()+"\n"+e.getMessage());
//			continue;
//		}
//		if(isDeletableFunction(getPdeDataContext().getVariableName()) && fnName.equals(defaultName)){
//			JTextArea jta = new JTextArea("Replace function '"+defaultName+"'\n"+
//					"original= '"+defaultExpression+"'\n"+
//					"new     = '"+getFunctionExpressionTextField().getText()+"'");
//			jta.setEditable(false);
//			int confirm = PopupGenerator.showComponentOKCancelDialog(this,jta,"Confirm Expression Replacement");
//			if(confirm == JOptionPane.OK_OPTION){
//				bReplaceFunc = true;
//			}else{
//				continue;
//			}
//		}else{
//			boolean bIsNameLegal = true;
//			for (int i = 0; existingVariableNames != null && i < existingVariableNames.length; i++) {
//				if(existingVariableNames[i].equals(fnName)){
//					PopupGenerator.showErrorDialog("Error: function name '"+
//							fnName+"' is already used by a non-replaceable variable name");
//					bIsNameLegal = false;
//					break;
//				}
//			}
//			if(!bIsNameLegal){
//				continue;
//			}
//		}
//		break;
//	}
//	if (ok == javax.swing.JOptionPane.OK_OPTION) {
//		
//		String funcName = null;
////		String tempFuncName = null;
//		AnnotatedFunction newFunction = null;
//		try {
//			DataIdentifier[] dataIdentifiers = getpdeDataContext1().getDataIdentifiers();
//			String[] dataIdNames = new String[dataIdentifiers.length];
//			VariableType[] dataIdVarTypes = new VariableType[dataIdentifiers.length];
//			
//			for (int i = 0; i < dataIdNames.length; i++){
//				dataIdNames[i] = dataIdentifiers[i].getName();
//				dataIdVarTypes[i] = dataIdentifiers[i].getVariableType();
//			}
//
////			AnnotatedFunction tempOldFunction = null;
////			if(bReplaceFunc){
////				tempFuncName = defaultName+"_old";
////				while(true){
////					boolean bOK = true;
////					for(int i=0;i<existingFunctionsNames.length;i+= 1){
////						if(existingFunctionsNames[i].equals(tempFuncName)){
////							tempFuncName = TokenMangler.getNextEnumeratedToken(tempFuncName);
////							bOK = false;
////							break;
////						}
////					}
////					if(bOK){
////						break;
////					}
////				}
////				Expression funcExp = new Expression(defaultExpression);
////				Function function = new Function(tempFuncName,funcExp );
////				VariableType funcType = FVSolver.getFunctionVariableType(function, dataIdNames, dataIdVarTypes, !getpdeDataContext1().getIsODEData());
////				tempOldFunction = new AnnotatedFunction(tempFuncName, funcExp, "", funcType, true);
////			}
//		
//		
//		
//			funcName = getFunctionNameTextField().getText();
//			cbit.vcell.parser.Expression funcExp = null;
//			funcExp = new Expression(getFunctionExpressionTextField().getText());
//
//
//			Function function = new Function(funcName, funcExp);
//			VariableType funcType = FVSolver.getFunctionVariableType(function, dataIdNames, dataIdVarTypes, !getpdeDataContext1().getIsODEData());
//			newFunction = new AnnotatedFunction(funcName, funcExp, "", funcType, true);
//			if(bReplaceFunc){
//				functionsList.remove(defaultFunction);
//				getpdeDataContext1().addFunctions(new AnnotatedFunction[] {newFunction},new boolean[] {true});
//				functionsList.add(newFunction);
//			}else{
//				getpdeDataContext1().addFunctions(new AnnotatedFunction[] {newFunction},new boolean[] {false});
//				functionsList.add(newFunction);
//			}
//		} catch (Exception e) {
//			newFunction = null;
//			e.printStackTrace(System.out);
//			PopupGenerator.showErrorDialog("Error adding/editing function\n"+e.getMessage());
//		}finally{
//			final AnnotatedFunction finalNewFunc = newFunction;
//			AsynchClientTask funcUpdate = new AsynchClientTask() {
//				public String getTaskName() { return "function update"; }
//				public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_BLOCKING; }
//				public void run(java.util.Hashtable hash) throws Exception{
//					getpdeDataContext1().refreshIdentifiers();
//					getpdeDataContext1().refreshTimes();
//					if (finalNewFunc == null){
//						functionsList = null;
//						initFunctionsList();
//					}else{
//						ListModel dlm = getJList1().getModel();
//						for(int i=0;i<dlm.getSize();i+= 1){
//							if(dlm.getElementAt(i).equals(finalNewFunc.getName())){
//								if(getJList1().getSelectedIndex() != i){
//									getJList1().setSelectedIndex(i);
//								}else{
//									ListSelectionEvent lse = new ListSelectionEvent(this,i,i,false);
//									setUserDefinedFnExpressionLabel(lse);
//									getPdeDataContext().refreshData();
//								}
//								break;
//							}
//						}
//					}
//				}
//				public boolean skipIfAbort() {
//					return false;
//				}
//				public boolean skipIfCancel(UserCancelException e) {
//					return false;
//				}
//			};
//			AsynchClientTask[] tasks = new AsynchClientTask[] {funcUpdate};
//			cbit.vcell.client.task.ClientTaskDispatcher.dispatch(this, new Hashtable(), tasks, false);
//
//		}
//	}
}


/**
 * connEtoC1:  (model1.change.stateChanged(javax.swing.event.ChangeEvent) --> PDEPlotControlPanel.setTimeFromSlider(I)V)
 * @param arg1 javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.ChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getmodel1() != null)) {
			this.setTimeFromSlider(getmodel1().getValue());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC2:  (JTextField1.action.actionPerformed(java.awt.event.ActionEvent) --> PDEPlotControlPanel.setTimeFromTextField()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField1().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (pdeDataContext1.timePoints --> PDEPlotControlPanel.newTimePoints([D)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newTimePoints(getpdeDataContext1().getTimePoints());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (pdeDataContext1.this --> PDEPlotControlPanel.newTimePoints([D)V)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(cbit.vcell.simdata.PDEDataContext value) {
	try {
		// user code begin {1}
		// user code end
		if ((getpdeDataContext1() != null)) {
			this.newTimePoints(getpdeDataContext1().getTimePoints());
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (displayAdapterService1.customScaleRange --> PDEPlotControlPanel.displayAdapterService1_CustomScaleRange(Lcbit.image.Range;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.displayAdapterService1_CustomScaleRange(getdisplayAdapterService1().getCustomScaleRange());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JTextField1.focus.focusLost(java.awt.event.FocusEvent) --> PDEPlotControlPanel.setTimeFromTextField(Ljava.lang.String;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setTimeFromTextField(getJTextField1().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (AddFunctionButton.action.actionPerformed(java.awt.event.ActionEvent) --> PDEPlotControlPanel.addFunction(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addFunction();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC8:  (JList1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> PDEPlotControlPanel.variableNameChanged(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.variableNameChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (displayAdapterService1.autoScale --> PDEPlotControlPanel.displayAdapterService1_AutoScale(Z)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.displayAdapterService1_AutoScale(getdisplayAdapterService1().getAutoScale());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (pdeDataContext1.this --> displayAdapterService1.clearMarkedStates()V)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.simdata.PDEDataContext value) {
	try {
		// user code begin {1}
		// user code end
		getdisplayAdapterService1().clearMarkedStates();
		connEtoM2();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  ( (pdeDataContext1,this --> displayAdapterService1,clearMarkedStates()V).normalResult --> displayAdapterService1.customScaleRange)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getdisplayAdapterService1().setCustomScaleRange(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (pdeDataContext1.this --> DefaultListModelCivilized1.contents)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.simdata.PDEDataContext value) {
	try {
		filterVariableNames();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM4:  (DefaultListModelCivilized1.listData.intervalAdded(javax.swing.event.ListDataEvent) --> JList1.selectedIndex)
 * @param arg1 javax.swing.event.ListDataEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(javax.swing.event.ListDataEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJList1().setSelectedIndex(0);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM6:  (pdeDataContext1.this --> displayAdapterService1.autoScale)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.simdata.PDEDataContext value) {
	try {
		// user code begin {1}
		// user code end
		getdisplayAdapterService1().setAutoScale(true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM7:  (pdeDataContext1.this --> JSliderTime.value)
 * @param value cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.simdata.PDEDataContext value) {
	try {
		// user code begin {1}
		// user code end
		getJSliderTime().setValue(0);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM8:  (pdeDataContext1.dataIdentifiers --> DefaultListModelCivilized1.contents)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.beans.PropertyChangeEvent arg1) {
	try {
		filterVariableNames();
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

private void filterVariableNames(){
	if ((getpdeDataContext1() != null)) {
		Object oldselection = getJList1().getSelectedValue();
		if(dataIdentifierFilter == null || dataIdentifierFilter.isAcceptAll((String)filterComboBox.getSelectedItem())){
			getDefaultListModelCivilized1().setContents(getpdeDataContext1().getVariableNames());
		}else{
			initFunctionsList();
			ArrayList<String> displayVarNames = new ArrayList<String>(); 
			if(getpdeDataContext1().getDataIdentifiers() != null && getpdeDataContext1().getDataIdentifiers().length > 0){
				TreeSet<DataIdentifier> dataIdentifierTreeSet =
					new TreeSet<DataIdentifier>(new Comparator<DataIdentifier>(){
						public int compare(DataIdentifier o1, DataIdentifier o2) {
							if(o1.getName().compareToIgnoreCase(o2.getName()) == 0){
								return o1.getName().compareTo(o2.getName());
							}
							return o1.getName().compareToIgnoreCase(o2.getName());
						}});
				DataIdentifier[] dataIdentifierArr = getPdeDataContext().getDataIdentifiers();
				dataIdentifierTreeSet.addAll(Arrays.asList(dataIdentifierArr));
				DataIdentifier[] sortedDataIdentiferArr = dataIdentifierTreeSet.toArray(new DataIdentifier[0]);

				for(int i=0; i < sortedDataIdentiferArr.length; i++){
					if(dataIdentifierFilter.accept((String)filterComboBox.getSelectedItem(), sortedDataIdentiferArr[i])){
						displayVarNames.add(sortedDataIdentiferArr[i].getName());
					}
				}
			}
			if(displayVarNames.size() == 0){
				Object emptyFilter = filterComboBox.getSelectedItem();
//				filterComboBox.setSelectedItem(dataIdentifierFilter.getDefaultFilterName());
				System.err.println("No Variables matching filter '"+emptyFilter+"' found");
//				return;
			}
			String[] displayNames = displayVarNames.toArray(new String[displayVarNames.size()]);
			getDefaultListModelCivilized1().setContents((displayNames.length == 0?null:displayNames));
		}
		if(getJList1().getModel().getSize() > 0){
			if(oldselection == null){
				getJList1().setSelectedIndex(0);
			}else{
				boolean bFound = false;
				for (int i = 0; i < getJList1().getModel().getSize(); i++) {
					if(oldselection.equals(getJList1().getModel().getElementAt(i))){
						getJList1().setSelectedIndex(i);
						bFound = true;
						break;
					}
				}
				if(!bFound){
					getJList1().setSelectedIndex(0);
				}
			}
			
		}
	}
}

/**
 * connPtoP1SetSource:  (PDEPlotControlPanel.pdeDataContext <--> pdeDataContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getpdeDataContext1() != null)) {
				this.setPdeDataContext(getpdeDataContext1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP1SetTarget:  (PDEPlotControlPanel.pdeDataContext <--> pdeDataContext1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setpdeDataContext1(this.getPdeDataContext());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP2SetTarget:  (DefaultListModelCivilized1.this <--> JList1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		getJList1().setModel(getDefaultListModelCivilized1());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (PDEPlotControlPanel.displayAdapterService <--> displayAdapterService1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getdisplayAdapterService1() != null)) {
				this.setDisplayAdapterService(getdisplayAdapterService1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (PDEPlotControlPanel.displayAdapterService <--> displayAdapterService1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			setdisplayAdapterService1(this.getDisplayAdapterService());
			// user code begin {2}
			// user code end
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetSource:  (JSliderTime.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getmodel1() != null)) {
				getJSliderTime().setModel(getmodel1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (JSliderTime.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			setmodel1(getJSliderTime().getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void displayAdapterService1_AutoScale(boolean arg1) {
	if(getDisplayAdapterService() != null && getPdeDataContext() != null && getPdeDataContext().getVariableName() != null){
		String varName = (String)getJList1().getSelectedValue();
		if(varName != null){
			if(!arg1){
				getDisplayAdapterService().setCustomScaleRange(getDisplayAdapterService().getActiveScaleRange());
				getDisplayAdapterService().markCurrentState(varName);
			}else{
				getDisplayAdapterService().clearMarkedState(varName);
				getDisplayAdapterService().setCustomScaleRange(null);
			}
		}
	}
}


/**
 * Comment
 */
private void displayAdapterService1_CustomScaleRange(org.vcell.util.Range arg1) {
	String varName = (String)getJList1().getSelectedValue();
	if(varName != null){
		if(arg1 == null){
			getDisplayAdapterService().clearMarkedState(varName);
		}else{
			getDisplayAdapterService().markCurrentState(varName);
		}
	}
}


/**
 * Return the AddFunctionButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddFunctionButton() {
	if (ivjAddFunctionButton == null) {
		try {
			ivjAddFunctionButton = new javax.swing.JButton();
			ivjAddFunctionButton.setName("AddFunctionButton");
			ivjAddFunctionButton.setPreferredSize(new java.awt.Dimension(121, 25));
			ivjAddFunctionButton.setText("Functions...");
			ivjAddFunctionButton.setMaximumSize(new java.awt.Dimension(121, 25));
			ivjAddFunctionButton.setMinimumSize(new java.awt.Dimension(121, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddFunctionButton;
}

/**
 * Return the DefaultListModelCivilized1 property value.
 * @return cbit.gui.DefaultListModelCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.DefaultListModelCivilized getDefaultListModelCivilized1() {
	if (ivjDefaultListModelCivilized1 == null) {
		try {
			ivjDefaultListModelCivilized1 = new cbit.gui.DefaultListModelCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultListModelCivilized1;
}


/**
 * Gets the displayAdapterService property (cbit.image.DisplayAdapterService) value.
 * @return The displayAdapterService property value.
 * @see #setDisplayAdapterService
 */
public cbit.image.DisplayAdapterService getDisplayAdapterService() {
	return fieldDisplayAdapterService;
}


/**
 * Return the displayAdapterService1 property value.
 * @return cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.image.DisplayAdapterService getdisplayAdapterService1() {
	// user code begin {1}
	// user code end
	return ivjdisplayAdapterService1;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/2004 5:29:59 PM)
 * @return cbit.vcell.math.AnnotatedFunction[]
 */
private void initFunctionsList() {
	if (functionsList == null){
		functionsList = new Vector<AnnotatedFunction>();
	}
	if (functionsList.size() == 0) {
		try {
			 AnnotatedFunction[] functions = getpdeDataContext1().getFunctions();
			 for (int i = 0; i < functions.length; i++) {
				 functionsList.addElement(functions[i]);
			 }
		} catch (org.vcell.util.DataAccessException e) {
			e.printStackTrace(System.out);
		}
	}
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Time");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the JLabelMax property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMax() {
	if (ivjJLabelMax == null) {
		try {
			ivjJLabelMax = new javax.swing.JLabel();
			ivjJLabelMax.setName("JLabelMax");
			ivjJLabelMax.setText("10.00");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMax;
}


/**
 * Return the JLabelMin property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMin() {
	if (ivjJLabelMin == null) {
		try {
			ivjJLabelMin = new javax.swing.JLabel();
			ivjJLabelMin.setName("JLabelMin");
			ivjJLabelMin.setText("0.00");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMin;
}


/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJList1() {
	if (ivjJList1 == null) {
		try {
			ivjJList1 = new javax.swing.JList();
			ivjJList1.setName("JList1");
			ivjJList1.setBounds(0, 0, 131, 238);
			ivjJList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJList1;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());
			ivjJPanel1.setMinimumSize(new java.awt.Dimension(84, 150));

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.gridwidth = 2;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
			constraintsJTextField1.gridx = 0; constraintsJTextField1.gridy = 1;
			constraintsJTextField1.gridwidth = 2;
			constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField1.weightx = 1.0;
			constraintsJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextField1(), constraintsJTextField1);

			java.awt.GridBagConstraints constraintsTimeSliderJPanel = new java.awt.GridBagConstraints();
			constraintsTimeSliderJPanel.gridx = 1; constraintsTimeSliderJPanel.gridy = 2;
			constraintsTimeSliderJPanel.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsTimeSliderJPanel.weightx = 1.0;
			constraintsTimeSliderJPanel.weighty = 1.0;
			constraintsTimeSliderJPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getTimeSliderJPanel(), constraintsTimeSliderJPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}

/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setPreferredSize(new java.awt.Dimension(267, 150));
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			gridBagLayout.rowHeights = new int[] {7};
			ivjJPanel2.setLayout(gridBagLayout);
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(55, 150));

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);

			filterComboBox = new JComboBox();
			filterComboBox.insertItemAt("All Variables", 0);
			filterComboBox.setSelectedIndex(0);
			final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(4, 4, 0, 4);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			ivjJPanel2.add(filterComboBox, gridBagConstraints);
			getJPanel2().add(getJScrollPane1(), constraintsJScrollPane1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setPreferredSize(new java.awt.Dimension(240, 100));
			ivjJScrollPane1.setMaximumSize(new java.awt.Dimension(259, 100));
			getJScrollPane1().setViewportView(getJList1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}

/**
 * Return the JSlider1 property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getJSliderTime() {
	if (ivjJSliderTime == null) {
		try {
			ivjJSliderTime = new javax.swing.JSlider();
			ivjJSliderTime.setName("JSliderTime");
			ivjJSliderTime.setPaintLabels(false);
			ivjJSliderTime.setInverted(true);
			ivjJSliderTime.setPaintTicks(true);
			ivjJSliderTime.setMajorTickSpacing(1);
			ivjJSliderTime.setSnapToTicks(true);
			ivjJSliderTime.setOrientation(javax.swing.JSlider.VERTICAL);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSliderTime;
}

/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			ivjJSplitPane1.setPreferredSize(new java.awt.Dimension(269, 235));
			ivjJSplitPane1.setContinuousLayout(true);
			ivjJSplitPane1.setMinimumSize(new java.awt.Dimension(86, 235));
			getJSplitPane1().add(getJPanel1(), "top");
			getJSplitPane1().add(getJPanel2(), "bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField1() {
	if (ivjJTextField1 == null) {
		try {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField1;
}


/**
 * Return the model1 property value.
 * @return javax.swing.BoundedRangeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.BoundedRangeModel getmodel1() {
	// user code begin {1}
	// user code end
	return ivjmodel1;
}


/**
 * Gets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @return The pdeDataContext property value.
 * @see #setPdeDataContext
 */
public cbit.vcell.simdata.PDEDataContext getPdeDataContext() {
	return fieldPdeDataContext;
}


/**
 * Return the pdeDataContext1 property value.
 * @return cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simdata.PDEDataContext getpdeDataContext1() {
	// user code begin {1}
	// user code end
	return ivjpdeDataContext1;
}


/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTimeSliderJPanel() {
	if (ivjTimeSliderJPanel == null) {
		try {
			ivjTimeSliderJPanel = new javax.swing.JPanel();
			ivjTimeSliderJPanel.setName("TimeSliderJPanel");
			ivjTimeSliderJPanel.setLayout(new java.awt.GridBagLayout());
			ivjTimeSliderJPanel.setMinimumSize(new java.awt.Dimension(76, 208));

			java.awt.GridBagConstraints constraintsJLabelMin = new java.awt.GridBagConstraints();
			constraintsJLabelMin.gridx = 1; constraintsJLabelMin.gridy = 0;
			constraintsJLabelMin.anchor = java.awt.GridBagConstraints.NORTHEAST;
			constraintsJLabelMin.insets = new java.awt.Insets(4, 4, 4, 4);
			getTimeSliderJPanel().add(getJLabelMin(), constraintsJLabelMin);

			java.awt.GridBagConstraints constraintsJLabelMax = new java.awt.GridBagConstraints();
			constraintsJLabelMax.gridx = 1; constraintsJLabelMax.gridy = 1;
			constraintsJLabelMax.anchor = java.awt.GridBagConstraints.SOUTHWEST;
			constraintsJLabelMax.insets = new java.awt.Insets(4, 4, 4, 4);
			getTimeSliderJPanel().add(getJLabelMax(), constraintsJLabelMax);

			java.awt.GridBagConstraints constraintsJSliderTime = new java.awt.GridBagConstraints();
			constraintsJSliderTime.gridx = 0; constraintsJSliderTime.gridy = 0;
constraintsJSliderTime.gridheight = 2;
			constraintsJSliderTime.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsJSliderTime.weighty = 1.0;
			constraintsJSliderTime.insets = new java.awt.Insets(4, 4, 4, 4);
			getTimeSliderJPanel().add(getJSliderTime(), constraintsJSliderTime);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeSliderJPanel;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(ivjEventHandler);
	getJTextField1().addActionListener(ivjEventHandler);
	getJTextField1().addFocusListener(ivjEventHandler);
	getJSliderTime().addPropertyChangeListener(ivjEventHandler);
	getJList1().addListSelectionListener(ivjEventHandler);
	getDefaultListModelCivilized1().addListDataListener(ivjEventHandler);
	getAddFunctionButton().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
	connPtoP4SetTarget();
	connPtoP3SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridy = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.ipady = 0;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints.gridx = 0;
		setName("PDEPlotControlPanel");
		setPreferredSize(new java.awt.Dimension(150, 600));
		setLayout(new GridBagLayout());
		setSize(144, 643);
		setMaximumSize(new java.awt.Dimension(200, 800));
		setMinimumSize(new java.awt.Dimension(125, 300));
		this.add(getJSplitPane1(), gridBagConstraints);
		this.add(getAddFunctionButton(), gridBagConstraints1);
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
private void newTimePoints(double[] newTimes) {
	//
	getJSliderTime().setSnapToTicks(true);//So arrow keys work correctly with no minor tick marks
	//
	if (newTimes == null || newTimes.length == 1) {
		getJSliderTime().setMinimum(0);
		getJSliderTime().setMaximum(0);
		getJLabelMin().setText((newTimes == null?"":newTimes[0]+""));
		getJLabelMax().setText((newTimes == null?"":newTimes[0]+""));
		getJTextField1().setText((newTimes == null?"":newTimes[0]+""));
		if(getJSliderTime().isEnabled()){
			BeanUtils.enableComponents(getTimeSliderJPanel(),false);
		}
	} else {
		if(!getJSliderTime().isEnabled()){
			BeanUtils.enableComponents(getTimeSliderJPanel(),true);
		}
		int sValue = getJSliderTime().getValue();
		getJSliderTime().setMinimum(0);
		//getJSliderTime().setExtent(1);//Can't do this because of bug in JSlider won't set last value
		getJSliderTime().setMaximum(newTimes.length - 1);
		if(sValue >= 0 && sValue < newTimes.length){
			getJSliderTime().setValue(sValue);
		}else{
			getJSliderTime().setValue(0);
		}
		getJSliderTime().setMajorTickSpacing((newTimes.length < 10?1:newTimes.length/10));
//		getJSliderTime().setMinorTickSpacing(getJSliderTime().getMajorTickSpacing());//hides minor tick marks
		getJSliderTime().setMinorTickSpacing(1);// testing....
		//
		getJLabelMin().setText(NumberUtils.formatNumber(newTimes[0],8));
		getJLabelMax().setText(NumberUtils.formatNumber(newTimes[newTimes.length - 1],8));
	}
}


/**
 * Comment
 */
private void setCursorForWindow(Cursor cursor) {
	
	BeanUtils.setCursorThroughout(this, cursor);
	
//	Container c = null;
//	// this normally is part of another panel that sits in an internal frame
//	//JInternalFrame iframe = BeanUtils.internalFrameParent(this);
//	JInternalFrame iframe = (JInternalFrame)BeanUtils.findTypeParentOfComponent(this,JInternalFrame.class);
//	if (iframe != null) {
//		c = (Container)iframe;
//	} else {
//		// just in case it will be used outside a desktop
//		Window window = SwingUtilities.windowForComponent(this);
//		c = (Container)window;
//	}
//	if (c != null) {
//		BeanUtils.setCursorThroughout(c, cursor);
//	}
}


/**
 * Sets the displayAdapterService property (cbit.image.DisplayAdapterService) value.
 * @param displayAdapterService The new value for the property.
 * @see #getDisplayAdapterService
 */
public void setDisplayAdapterService(cbit.image.DisplayAdapterService displayAdapterService) {
	cbit.image.DisplayAdapterService oldValue = fieldDisplayAdapterService;
	fieldDisplayAdapterService = displayAdapterService;
	firePropertyChange("displayAdapterService", oldValue, displayAdapterService);
}


/**
 * Set the displayAdapterService1 to a new value.
 * @param newValue cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdisplayAdapterService1(cbit.image.DisplayAdapterService newValue) {
	if (ivjdisplayAdapterService1 != newValue) {
		try {
			cbit.image.DisplayAdapterService oldValue = getdisplayAdapterService1();
			/* Stop listening for events from the current object */
			if (ivjdisplayAdapterService1 != null) {
				ivjdisplayAdapterService1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjdisplayAdapterService1 = newValue;

			/* Listen for events from the new object */
			if (ivjdisplayAdapterService1 != null) {
				ivjdisplayAdapterService1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP3SetSource();
			firePropertyChange("displayAdapterService", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Set the model1 to a new value.
 * @param newValue javax.swing.BoundedRangeModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setmodel1(javax.swing.BoundedRangeModel newValue) {
	if (ivjmodel1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjmodel1 != null) {
				ivjmodel1.removeChangeListener(ivjEventHandler);
			}
			ivjmodel1 = newValue;

			/* Listen for events from the new object */
			if (ivjmodel1 != null) {
				ivjmodel1.addChangeListener(ivjEventHandler);
			}
			connPtoP4SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Sets the pdeDataContext property (cbit.vcell.simdata.PDEDataContext) value.
 * @param pdeDataContext The new value for the property.
 * @see #getPdeDataContext
 */
public void setPdeDataContext(cbit.vcell.simdata.PDEDataContext pdeDataContext) {
	cbit.vcell.simdata.PDEDataContext oldValue = fieldPdeDataContext;
	fieldPdeDataContext = pdeDataContext;
	firePropertyChange("pdeDataContext", oldValue, pdeDataContext);
}


/**
 * Set the pdeDataContext1 to a new value.
 * @param newValue cbit.vcell.simdata.PDEDataContext
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setpdeDataContext1(cbit.vcell.simdata.PDEDataContext newValue) {
	if (ivjpdeDataContext1 != newValue) {
		try {
			cbit.vcell.simdata.PDEDataContext oldValue = getpdeDataContext1();
			/* Stop listening for events from the current object */
			if (ivjpdeDataContext1 != null) {
				ivjpdeDataContext1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjpdeDataContext1 = newValue;

			/* Listen for events from the new object */
			if (ivjpdeDataContext1 != null) {
				ivjpdeDataContext1.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM6(ivjpdeDataContext1);
			connEtoM7(ivjpdeDataContext1);
			connEtoC4(ivjpdeDataContext1);
			connEtoM3(ivjpdeDataContext1);
			connEtoM1(ivjpdeDataContext1);
			firePropertyChange("pdeDataContext", oldValue, newValue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}

/**
 * Comment
 */
private void setTimeFromSlider(int sliderPosition) {
	if (getpdeDataContext1() != null && getpdeDataContext1().getTimePoints() != null) {
		final double timepoint = getpdeDataContext1().getTimePoints()[sliderPosition];
		

		if (! getJSliderTime().getValueIsAdjusting()) {
			try {
				setCursorForWindow(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				getpdeDataContext1().setTimePoint(timepoint);
				getJTextField1().setText(Double.toString(getPdeDataContext().getTimePoint()));
				synchronizeView();
			} catch (final Exception exc) {
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
					PopupGenerator.showErrorDialog("Error updating timepoint '"+timepoint+"'\n"+exc.getMessage());
					}});
				int index = -1;
				if(getPdeDataContext() != null && getPdeDataContext().getTimePoints() != null){
					double[] timePoints = getPdeDataContext().getTimePoints();
					for(int i=0;i<timePoints.length;i+= 1){
						if(timePoints[i] == getPdeDataContext().getTimePoint()){
							index = i;
							break;
						}
					}
				}
				if(index != -1){
					getJSliderTime().setValue(index);
				}else{
					getJTextField1().setText("-Error-");
				}
			} finally {
				setCursorForWindow(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}else{
			getJTextField1().setText(timepoint+"");
		}
	} else {
		getJTextField1().setText("");
	}
}


/**
 * Comment
 */
private void setTimeFromTextField(String typedValue) {
	int oldVal = getJSliderTime().getValue();
	double[]times = getpdeDataContext1().getTimePoints();
	double time = 0;
	try {
		time = Double.parseDouble(typedValue);
	} catch (NumberFormatException e) {
		// if typedTime is crap, put back old value
		updateTimeTextField(times[oldVal]);
		return;
	}
	// we find neighboring time value; if out of bounds, it is set to corresponding extreme
	// we correct text, then adjust slider; change in slider will fire other updates
	int val = 0;
	if (time > times[0]) {
		if (time >= times[times.length - 1]) {
			val = times.length - 1;
		} else {
			for (int i=0;i<times.length;i++) {
				val = i;
				if ((time >= times[i]) && (time < times[i+1]))
					break;
			}
		}
	}
	updateTimeTextField(times[val]);
	getJSliderTime().setValue(val);
}


private void synchronizeView() throws DataAccessException{
	if(getPdeDataContext() == null &&
			getpdeDataContext1().getTimePoints() != null){
		return;
	}
	double[] timePoints = getpdeDataContext1().getTimePoints();
	int timeIndex = getJSliderTime().getValue();
	double currentTimePoint = (timeIndex>0 && timeIndex < timePoints.length?timePoints[timeIndex]:-1);
	if(currentTimePoint == -1){
		return;
	}
	if(getpdeDataContext1().getTimePoint() != currentTimePoint){
		updateTimeTextField(currentTimePoint);
		if(getPdeDataContext().getTimePoint() != currentTimePoint){
			getPdeDataContext().setTimePoint(currentTimePoint);
		}
	}
	String currentVariableName = (String)getJList1().getSelectedValue();
	if(getJList1().getSelectedValue() == currentVariableName){
		return;
	}
	if(!currentVariableName.equals(getPdeDataContext().getVariableName())){
		getPdeDataContext().setVariableName(currentVariableName);
	}
}


/**
 * Comment
 */
private void variableNameChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
	if(getPdeDataContext() == null){
		return;
	}
	if(listSelectionEvent != null && !listSelectionEvent.getValueIsAdjusting()){
		final String newVariableName = (String)getJList1().getSelectedValue();
		if(newVariableName != null){
			try {
				setCursorForWindow(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if(getDisplayAdapterService() != null){
					getDisplayAdapterService().activateMarkedState(newVariableName);
				}
				getPdeDataContext().setVariableName(newVariableName);
				synchronizeView();
			} catch (final Exception e) {
				e.printStackTrace();
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						PopupGenerator.showErrorDialog("Error setting variable name '"+newVariableName+"'\n"+e.getMessage());
					}});
				int index = -1;
				if(getPdeDataContext() != null && getPdeDataContext().getVariableName() != null){
					for(int i=0;i<getJList1().getModel().getSize();i+= 1){
						if(getPdeDataContext().getVariableName().equals(getJList1().getModel().getElementAt(i))){
							index = i;
							break;
						}
					}
				}
				if(index != -1){
					getJList1().setSelectedIndex(index);
				}else{
					getJList1().clearSelection();
				}
			} finally {
				setCursorForWindow(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}else if(getPdeDataContext() != null && getPdeDataContext().getVariableName() != null){
			for (int i = 0; i < getJList1().getModel().getSize(); i++) {
				if(getPdeDataContext().getVariableName().equals(getJList1().getModel().getElementAt(i))){
					getJList1().setSelectedIndex(i);
					break;
				}
			}
		}
	}
}
private void updateTimeTextField(double newTime){
	getJTextField1().setText(Double.toString(newTime));
}


}