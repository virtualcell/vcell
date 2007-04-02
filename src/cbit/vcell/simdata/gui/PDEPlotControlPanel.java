package cbit.vcell.simdata.gui;
import cbit.vcell.math.AnnotatedFunction;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.*;
import java.awt.*;
import cbit.util.*;
import cbit.vcell.math.Function;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.parser.Expression;
import cbit.vcell.solvers.FVSolver;
import java.util.Vector;
import java.awt.GridBagConstraints;
/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 10:29:53 PM)
 * @author: Ion Moraru
 */
public class PDEPlotControlPanel extends JPanel {
	//
	//private class GetVariableName implements cbit.vcell.desktop.controls.ClientTask {
		//String variableName;
		//GetVariableName(String argVariableName){
			//variableName = argVariableName;
		//}
		//public String getTaskName() { return "Get Variable Name";	}
		//public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_BLOCKING; }
		//public void run(java.util.Hashtable hash) {
			//getPdeDataContext().setVariableName(variableName);
		//}
	//}
	////
	//private class GetTimepoint implements cbit.vcell.desktop.controls.ClientTask {
		//double timepoint;
		//GetTimepoint(double argTimepoint){
			//timepoint = argTimepoint;
		//}
		//public String getTaskName() { return "Get Variable Name";	}
		//public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_BLOCKING; }
		//public void run(java.util.Hashtable hash) {
			//getPdeDataContext().setTimePoint(timepoint);
		//}
	//}
	//
	private boolean bFallbackInProgress = false;
	private JLabel ivjJLabel1 = null;
	private JTextField ivjJTextField1 = null;
	private cbit.vcell.simdata.PDEDataContext fieldPdeDataContext = null;
	private boolean ivjConnPtoP1Aligning = false;
	private cbit.gui.DefaultListModelCivilized ivjDefaultListModelCivilized1 = null;
	private JLabel ivjJLabel2 = null;
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
	private JButton ivjDeleteFunctionButton = null;
	private JLabel ivjFunctionExpressionLabel = null;
	private JTextField ivjFunctionExpressionTextField = null;
	private JLabel ivjFunctionNameLabel = null;
	private JTextField ivjFunctionNameTextField = null;
	private JPanel ivjFunctionPanel = null;
	private JPanel ivjUserFunctionPanel = null;
	private JLabel ivjFnExpressionLabel = null;
	// Having a local functionsList in this panel to avoid having to get the functionsList from
	// the server each time a delete or enableDeleteButton, etc. is called. When the list is null,
	// it gets updated from the server. Then, depending on whether the function is in this list or
	// not, it is added or removed depending on the functionality invoked.
	
	private Vector functionsList = new Vector();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener, javax.swing.event.ListDataListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PDEPlotControlPanel.this.getJTextField1()) 
				connEtoC2(e);
			if (e.getSource() == PDEPlotControlPanel.this.getAddFunctionButton()) 
				connEtoC7(e);
			if (e.getSource() == PDEPlotControlPanel.this.getDeleteFunctionButton()) 
				connEtoC10(e);
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
			if (e.getSource() == PDEPlotControlPanel.this.getJList1()) 
				connEtoC11(e);
			if (e.getSource() == PDEPlotControlPanel.this.getJList1()) 
				connEtoC12(e);
		};
	};

/**
 * PDEPlotControlPanel constructor comment.
 */
public PDEPlotControlPanel() {
	super();
	initialize();
}

/**
 * Comment
 */
private void addFunction() {

	// Get a default name for the user defined function
	String[] existingFunctionsNames = getpdeDataContext1().getVariableNames();
	String defaultName = null;
	int count = 0;
	boolean nameUsed = true;
	while (nameUsed) {
		boolean matchFound = false;
		count++;
		defaultName = "Function" + count;
		for (int i = 0; existingFunctionsNames != null && i < existingFunctionsNames.length; i++){
			if (existingFunctionsNames[i].equals(defaultName)) {
				matchFound = true;
			}
		}
		nameUsed = matchFound;
	}

	//
	// Initialize fields
	//
	javax.swing.JPanel FnPanel = getFunctionPanel();
	getFunctionNameTextField().setText(defaultName);
	getFunctionExpressionTextField().setText("0.0");

	//
	// Show the editor with a default name and default expression for the function. If the OK option is chosen, 
	// get the new name and expression for the function and add it to the list of columns. 
	// Else, pop-up an error dialog indicating that function cannot be added.
	//
	boolean bFnNameHasSpace = true;
	int ok = -1;
	while (bFnNameHasSpace) {
		// User-defined Function name cannot have spaces; if user inputs a name with spaces, 
		// pop up the 'Add Function' dialog to prompt the user to input a function name without spaces.
		ok = JOptionPane.showOptionDialog(this, FnPanel, "Add Function" , 0, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel"}, null);
		String fnName = getFunctionNameTextField().getText();
		if (fnName.indexOf(" " ) > 0) {
			cbit.vcell.client.PopupGenerator.showErrorDialog("Function name cannot have spaces, please change function name.");
			bFnNameHasSpace = true;
		} else {
			bFnNameHasSpace = false;
		}
	}
	if (ok == javax.swing.JOptionPane.OK_OPTION) {
		String funcName = getFunctionNameTextField().getText();
		cbit.vcell.parser.Expression funcExp = null;
		try {
			funcExp = new Expression(getFunctionExpressionTextField().getText());
		} catch (cbit.vcell.parser.ExpressionException e) {
			e.printStackTrace(System.out);
		}

		DataIdentifier[] dataIdentifiers = getpdeDataContext1().getDataIdentifiers();
		String[] dataIdNames = new String[dataIdentifiers.length];
		VariableType[] dataIdVarTypes = new VariableType[dataIdentifiers.length];
		
		for (int i = 0; i < dataIdNames.length; i++){
			dataIdNames[i] = dataIdentifiers[i].getName();
			dataIdVarTypes[i] = dataIdentifiers[i].getVariableType();
		}

		try {
			Function function = new Function(funcName, funcExp);
			VariableType funcType = FVSolver.getFunctionVariableType(function, dataIdNames, dataIdVarTypes, !getpdeDataContext1().getIsODEData());
			AnnotatedFunction newFunction = new AnnotatedFunction(funcName, funcExp, "", funcType, true);
			getpdeDataContext1().addFunction(newFunction);
			getpdeDataContext1().refreshIdentifiers();
			getpdeDataContext1().refreshTimes();
			if (!functionsList.contains(newFunction)) {
				functionsList.addElement(newFunction);
			}
		} catch (cbit.vcell.server.DataAccessException e) {
			javax.swing.JOptionPane.showMessageDialog(this, e.getMessage()+". "+funcName+" not added.", "Error Adding Function ", javax.swing.JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(System.out);
		}
	}
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
 * connEtoC10:  (DeleteFunctionButton.action.actionPerformed(java.awt.event.ActionEvent) --> PDEPlotControlPanel.deleteFunction(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.deleteFunction(getJList1().getSelectedIndex());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC11:  (JList1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> PDEPlotControlPanel.setUserDefinedFnExpressionLabel(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setUserDefinedFnExpressionLabel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC12:  (JList1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> PDEPlotControlPanel.enableDeleteButton(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.enableDeleteButton();
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
		// user code begin {1}
		// user code end
		if ((getpdeDataContext1() != null)) {
			getDefaultListModelCivilized1().setContents(getpdeDataContext1().getVariableNames());
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
		// user code begin {1}
		// user code end
		if ((getpdeDataContext1() != null)) {
			getDefaultListModelCivilized1().setContents(getpdeDataContext1().getVariableNames());
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
private void deleteFunction(int listSelectionIndex) {
	//
	// Get current selection in list of variables/functions, 
	// Get list of functions in the pdeDataContext(DataManager), 
	// Check if user defined and delete.
	//
	String choice = getDefaultListModelCivilized1().getElementAt(listSelectionIndex).toString();

	Vector functionsVector = getFunctionsList();
	AnnotatedFunction[] functions = new AnnotatedFunction[functionsVector.size()];
	functionsVector.copyInto(functions);

	for (int i = 0; i < functions.length; i++){
		if (functions[i].getName().equals(choice)) {
			if (((AnnotatedFunction)functions[i]).isUserDefined()) {
				try {
					getpdeDataContext1().removeFunction(functions[i]);
					getpdeDataContext1().refreshIdentifiers();
					getpdeDataContext1().refreshTimes();
					functionsList.removeElement(functions[i]);
				} catch (cbit.vcell.server.DataAccessException e) {
					e.printStackTrace(System.out);
				}				
			} 
		}
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
private void displayAdapterService1_CustomScaleRange(cbit.util.Range arg1) {
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
 * Comment
 */
private void enableDeleteButton() {
	if(getPdeDataContext() == null){
		return;
	}
	//
	// Get the current selected index/choice in the list.  
	// If it is a userDefined function, enable Delete button, else disable it.
	//
	int selectedIndex = getJList1().getSelectedIndex();
	String varName = (String)getJList1().getSelectedValue();

	Vector functionsVector = getFunctionsList();
	AnnotatedFunction[] functions = new AnnotatedFunction[functionsVector.size()];
	functionsVector.copyInto(functions);
	
	boolean bIsFunction = false;
	for (int i = 0; i < functions.length; i++){
		if (functions[i].getName().equals(varName)) {
			bIsFunction = true;
			if (((AnnotatedFunction)functions[i]).isUserDefined()) {
				getDeleteFunctionButton().setEnabled(true);
			} else {
				getDeleteFunctionButton().setEnabled(false);
			}
		}
	}

	if (!bIsFunction) {
		getDeleteFunctionButton().setEnabled(false);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (7/2/2003 7:15:19 PM)
 */
private void fallback(String failedVarName) {
	//
	try{
		getpdeDataContext1().refreshIdentifiers();
		getPdeDataContext().refreshTimes();
		getPdeDataContext().setVariableName(failedVarName);
		if(getPdeDataContext().getSourceDataInfo() == null){
			throw new Exception("Refresh failed");
		}
	}catch(Throwable exc){
		try{
			getJList1().clearSelection();
		}finally{
			cbit.vcell.client.PopupGenerator.showErrorDialog(this,exc.getMessage()+"\nUnable to retrieve Data from Server\nTry refreshing data");
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
			ivjAddFunctionButton.setText("Add Function");
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
 * Return the DeleteFunctionButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDeleteFunctionButton() {
	if (ivjDeleteFunctionButton == null) {
		try {
			ivjDeleteFunctionButton = new javax.swing.JButton();
			ivjDeleteFunctionButton.setName("DeleteFunctionButton");
			ivjDeleteFunctionButton.setText("Delete Function");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteFunctionButton;
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
 * Return the FnExpressionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFnExpressionLabel() {
	if (ivjFnExpressionLabel == null) {
		try {
			ivjFnExpressionLabel = new javax.swing.JLabel();
			ivjFnExpressionLabel.setName("FnExpressionLabel");
			ivjFnExpressionLabel.setPreferredSize(new java.awt.Dimension(121, 25));
			ivjFnExpressionLabel.setText("");
			ivjFnExpressionLabel.setMaximumSize(new java.awt.Dimension(121, 25));
			ivjFnExpressionLabel.setMinimumSize(new java.awt.Dimension(121, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFnExpressionLabel;
}

/**
 * Return the FunctionExpressionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFunctionExpressionLabel() {
	if (ivjFunctionExpressionLabel == null) {
		try {
			ivjFunctionExpressionLabel = new javax.swing.JLabel();
			ivjFunctionExpressionLabel.setName("FunctionExpressionLabel");
			ivjFunctionExpressionLabel.setText("Function Expression");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionExpressionLabel;
}


/**
 * Return the FunctionExpressionTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFunctionExpressionTextField() {
	if (ivjFunctionExpressionTextField == null) {
		try {
			ivjFunctionExpressionTextField = new javax.swing.JTextField();
			ivjFunctionExpressionTextField.setName("FunctionExpressionTextField");
			ivjFunctionExpressionTextField.setPreferredSize(new java.awt.Dimension(200, 30));
			ivjFunctionExpressionTextField.setMaximumSize(new java.awt.Dimension(200, 30));
			ivjFunctionExpressionTextField.setMinimumSize(new java.awt.Dimension(200, 30));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionExpressionTextField;
}


/**
 * Return the FunctionNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFunctionNameLabel() {
	if (ivjFunctionNameLabel == null) {
		try {
			ivjFunctionNameLabel = new javax.swing.JLabel();
			ivjFunctionNameLabel.setName("FunctionNameLabel");
			ivjFunctionNameLabel.setText("Function Name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionNameLabel;
}


/**
 * Return the FunctionNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFunctionNameTextField() {
	if (ivjFunctionNameTextField == null) {
		try {
			ivjFunctionNameTextField = new javax.swing.JTextField();
			ivjFunctionNameTextField.setName("FunctionNameTextField");
			ivjFunctionNameTextField.setPreferredSize(new java.awt.Dimension(200, 30));
			ivjFunctionNameTextField.setMinimumSize(new java.awt.Dimension(200, 30));
			ivjFunctionNameTextField.setMaximumSize(new java.awt.Dimension(200, 30));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionNameTextField;
}


/**
 * Return the FunctionPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getFunctionPanel() {
	if (ivjFunctionPanel == null) {
		try {
			ivjFunctionPanel = new javax.swing.JPanel();
			ivjFunctionPanel.setName("FunctionPanel");
			ivjFunctionPanel.setLayout(new java.awt.GridBagLayout());
			ivjFunctionPanel.setBounds(459, 353, 438, 101);

			java.awt.GridBagConstraints constraintsFunctionNameLabel = new java.awt.GridBagConstraints();
			constraintsFunctionNameLabel.gridx = 0; constraintsFunctionNameLabel.gridy = 0;
			constraintsFunctionNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getFunctionPanel().add(getFunctionNameLabel(), constraintsFunctionNameLabel);

			java.awt.GridBagConstraints constraintsFunctionExpressionLabel = new java.awt.GridBagConstraints();
			constraintsFunctionExpressionLabel.gridx = 0; constraintsFunctionExpressionLabel.gridy = 1;
			constraintsFunctionExpressionLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getFunctionPanel().add(getFunctionExpressionLabel(), constraintsFunctionExpressionLabel);

			java.awt.GridBagConstraints constraintsFunctionNameTextField = new java.awt.GridBagConstraints();
			constraintsFunctionNameTextField.gridx = 1; constraintsFunctionNameTextField.gridy = 0;
			constraintsFunctionNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFunctionNameTextField.weightx = 1.0;
			constraintsFunctionNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getFunctionPanel().add(getFunctionNameTextField(), constraintsFunctionNameTextField);

			java.awt.GridBagConstraints constraintsFunctionExpressionTextField = new java.awt.GridBagConstraints();
			constraintsFunctionExpressionTextField.gridx = 1; constraintsFunctionExpressionTextField.gridy = 1;
			constraintsFunctionExpressionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFunctionExpressionTextField.weightx = 1.0;
			constraintsFunctionExpressionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getFunctionPanel().add(getFunctionExpressionTextField(), constraintsFunctionExpressionTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFunctionPanel;
}

/**
 * Insert the method's description here.
 * Creation date: (3/3/2004 5:29:59 PM)
 * @return cbit.vcell.math.AnnotatedFunction[]
 */
private Vector getFunctionsList() {
	if (functionsList == null || functionsList.size() == 0) {
		try {
			 AnnotatedFunction[] functions = getpdeDataContext1().getFunctions();
			 for (int i = 0; i < functions.length; i++) {
				 functionsList.addElement(functions[i]);
			 }
		} catch (cbit.vcell.server.DataAccessException e) {
			e.printStackTrace(System.out);
		}
	}
	return functionsList;
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
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Variable");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
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
//			constraintsJLabel1.gridwidth = 2;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
			constraintsJTextField1.gridx = 0; constraintsJTextField1.gridy = 1;
//			constraintsJTextField1.gridwidth = 2;
			constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField1.weightx = 1.0;
			constraintsJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJTextField1(), constraintsJTextField1);

			java.awt.GridBagConstraints constraintsTimeSliderJPanel = new java.awt.GridBagConstraints();
			constraintsTimeSliderJPanel.gridx = 0; constraintsTimeSliderJPanel.gridy = 2;
			constraintsTimeSliderJPanel.fill = java.awt.GridBagConstraints.BOTH;
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
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());
			ivjJPanel2.setMinimumSize(new java.awt.Dimension(55, 150));

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
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
			constraintsJLabelMin.anchor = GridBagConstraints.NORTHWEST;
			constraintsJLabelMin.weightx = 0.0;
//			constraintsJLabelMin.fill = GridBagConstraints.NONE;
			constraintsJLabelMin.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsJLabelMax = new java.awt.GridBagConstraints();
			constraintsJLabelMax.gridx = 1; constraintsJLabelMax.gridy = 2;
			constraintsJLabelMax.anchor = GridBagConstraints.SOUTHWEST;
			constraintsJLabelMax.weightx = 0.0;
			constraintsJLabelMax.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjTimeSliderJPanel.add(getJLabelMin(), constraintsJLabelMin);
			java.awt.GridBagConstraints constraintsJSliderTime = new java.awt.GridBagConstraints();
			constraintsJSliderTime.gridx = 0; constraintsJSliderTime.gridy = 0;
constraintsJSliderTime.gridheight = 3;
			constraintsJSliderTime.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsJSliderTime.weighty = 1.0;
			constraintsJSliderTime.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjTimeSliderJPanel.add(getJLabelMax(), constraintsJLabelMax);
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
 * Return the UserFunctionPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getUserFunctionPanel() {
	if (ivjUserFunctionPanel == null) {
		try {
			ivjUserFunctionPanel = new javax.swing.JPanel();
			ivjUserFunctionPanel.setName("UserFunctionPanel");
			ivjUserFunctionPanel.setPreferredSize(new java.awt.Dimension(243, 100));
			ivjUserFunctionPanel.setLayout(new java.awt.GridBagLayout());
			ivjUserFunctionPanel.setMinimumSize(new java.awt.Dimension(243, 60));

			java.awt.GridBagConstraints constraintsFnExpressionLabel = new java.awt.GridBagConstraints();
			constraintsFnExpressionLabel.gridx = 1; constraintsFnExpressionLabel.gridy = 1;
			constraintsFnExpressionLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getUserFunctionPanel().add(getFnExpressionLabel(), constraintsFnExpressionLabel);

			java.awt.GridBagConstraints constraintsAddFunctionButton = new java.awt.GridBagConstraints();
			constraintsAddFunctionButton.gridx = 1; constraintsAddFunctionButton.gridy = 2;
			constraintsAddFunctionButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getUserFunctionPanel().add(getAddFunctionButton(), constraintsAddFunctionButton);

			java.awt.GridBagConstraints constraintsDeleteFunctionButton = new java.awt.GridBagConstraints();
			constraintsDeleteFunctionButton.gridx = 1; constraintsDeleteFunctionButton.gridy = 3;
			constraintsDeleteFunctionButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getUserFunctionPanel().add(getDeleteFunctionButton(), constraintsDeleteFunctionButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUserFunctionPanel;
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
	getDeleteFunctionButton().addActionListener(ivjEventHandler);
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
		setName("PDEPlotControlPanel");
		setPreferredSize(new java.awt.Dimension(150, 600));
		setLayout(new java.awt.BorderLayout());
		setSize(144, 629);
		setMaximumSize(new java.awt.Dimension(200, 800));
		setMinimumSize(new java.awt.Dimension(125, 300));
		add(getJSplitPane1(), "Center");
		add(getUserFunctionPanel(), "South");
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PDEPlotControlPanel aPDEPlotControlPanel;
		aPDEPlotControlPanel = new PDEPlotControlPanel();
		frame.setContentPane(aPDEPlotControlPanel);
		frame.setSize(aPDEPlotControlPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
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
	Container c = null;
	// this normally is part of another panel that sits in an internal frame
	//JInternalFrame iframe = BeanUtils.internalFrameParent(this);
	JInternalFrame iframe = (JInternalFrame)BeanUtils.findTypeParentOfComponent(this,JInternalFrame.class);
	if (iframe != null) {
		c = (Container)iframe;
	} else {
		// just in case it will be used outside a desktop
		Window window = SwingUtilities.windowForComponent(this);
		c = (Container)window;
	}
	if (c != null) {
		BeanUtils.setCursorThroughout(c, cursor);
	}
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
		getJTextField1().setText(Double.toString(timepoint));

		if (! getJSliderTime().getValueIsAdjusting()) {
			try {
				setCursorForWindow(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				//Mark state if conditions met
				//if(getDisplayAdapterService() != null && !getDisplayAdapterService().getAutoScale()){
					//if(!getDisplayAdapterService().hasStateID(getPdeDataContext().getVariableName())){
						////getDisplayAdapterService().setCustomScaleRange(null);
						//getDisplayAdapterService().setDefaultScaleRange(null);
						//getDisplayAdapterService().markCurrentState(getPdeDataContext().getVariableName());
					//}else if(getDisplayAdapterService().getCustomScaleRange() != null){
						//getDisplayAdapterService().setDefaultScaleRange(null);
						//getDisplayAdapterService().markCurrentState(getPdeDataContext().getVariableName());
					//}
				//}
				//
				getpdeDataContext1().setTimePoint(timepoint);
				//SwingUtilities.invokeLater(
						//new Runnable(){
							//public void run(){
								//getpdeDataContext1().setTimePoint(timepoint);
							//}
						//}
				//);
				//cbit.vcell.desktop.controls.ClientDisplayManager.getClientDisplayManager().performClientTasks(
							//"Timepoint","Timepoint",
							//new cbit.vcell.desktop.controls.ClientTask[] {new GetTimepoint(timepoint)},null);
				//
				//getDisplayAdapterService().activateMarkedState(getPdeDataContext().getVariableName());
				//
			} catch (Exception exc) {
				fallback((String)getJList1().getSelectedValue());
				return;
			} finally {
				setCursorForWindow(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
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
		getJTextField1().setText(Double.toString(times[oldVal]));
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
	getJTextField1().setText(Double.toString(times[val]));
	getJSliderTime().setValue(val);
}


/**
 * Comment
 */
private void setUserDefinedFnExpressionLabel() {
	if(getPdeDataContext() == null){
		return;
	}
	// Get the current selected index/choice in the list. If it is a userDefined function, 
	// set label text to function's expression. Else leave it blank.
	int selectedIndex = getJList1().getSelectedIndex();
	String varName = (String)getJList1().getSelectedValue();

	Vector functionsVector = getFunctionsList();
	AnnotatedFunction[] functions = new AnnotatedFunction[functionsVector.size()];
	functionsVector.copyInto(functions);
	
	boolean bIsFunction = false;
	for (int i = 0; i < functions.length; i++){
		if (functions[i].getName().equals(varName)) {
			bIsFunction = true;
			if (((AnnotatedFunction)functions[i]).isUserDefined()) {
				getFnExpressionLabel().setText(functions[i].getExpression().infix());
			} else {
				getFnExpressionLabel().setText("");
			}
		}
	}

	if (!bIsFunction) {
		getFnExpressionLabel().setText("");
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
		int selectedIndex = getJList1().getSelectedIndex();
		String oldVariableName = null;
		final String newVariableName;
		String[] variableNames = getPdeDataContext().getVariableNames();
		int oldIndex = -1;
		if(variableNames != null){
			if(selectedIndex == listSelectionEvent.getFirstIndex()){
				oldIndex = listSelectionEvent.getLastIndex();
				// Before adding the add/remove user-defined functions, the list was static, hence the code in 'else'
				// loop was valid. But with the add/remove functionality, the list now becomes dynamic, so only the
				// 'else' part can throw exceptions, hence the change to include the check for oldIndex.
				// ** This situation arises when the selection deleted is the last item in the JList,
				// ** When oldIndex (which no longer exists in 'variableNames') is used to access variableNames, it throws
				// ** an exception.
				if (variableNames.length <= oldIndex) {
					oldVariableName = variableNames[variableNames.length-1];
				} else {
					oldVariableName = variableNames[oldIndex];
				}
			}else{
				oldIndex = listSelectionEvent.getFirstIndex();
				oldVariableName = variableNames[oldIndex];
			}
		}
		int newIndex = getJList1().getSelectedIndex();
		newVariableName = (String)getJList1().getSelectedValue();
		if(newVariableName != null){
			try {
				//
				if(getDisplayAdapterService() != null){
					getDisplayAdapterService().activateMarkedState(newVariableName);
				}
				//
				setCursorForWindow(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				//
				getPdeDataContext().setVariableName(newVariableName);
				//SwingUtilities.invokeLater(
						//new Runnable(){
							//public void run(){
								//getPdeDataContext().setVariableName(newVariableName);
							//}
						//}
				//);
				//cbit.vcell.desktop.controls.ClientDisplayManager.getClientDisplayManager().performClientTasks(
							//"Variable Name","Variable name",
							//new cbit.vcell.desktop.controls.ClientTask[] {new GetVariableName(newVariableName)},null);
				//
				if(getPdeDataContext().getSourceDataInfo() == null){
					fallback((String)getJList1().getSelectedValue());
					return;
				}
			} catch (IllegalArgumentException e) {
				getJList1().setSelectedIndex(oldIndex);
				return;
			} finally {
				setCursorForWindow(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
}
}