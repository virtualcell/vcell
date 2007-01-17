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
	int ok = JOptionPane.showOptionDialog(this, FnPanel, "Add Function" , 0, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel"}, null);
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
		getJLabelMin().setText(Double.toString(newTimes[0]));
		getJLabelMax().setText(Double.toString(newTimes[newTimes.length - 1]));
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


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DF8D44555B0EA5028D1228D98B0EAD40428310DB5D82A3425ED5A26153698E30BB536E965E9E925EDDAE34BE75337B7FF10E000A831C60D90ABEA509093C8A0A481728B89C4D812088B8449123DC9B6FB77075D8D84C860BDB3F34F5C3BF76F6E06D43F6765F98EF76F19F3661CB9F3E6664C5F0DD034B1BAB7B32BC890B2F385795F0ECCC1189DA18852355F7D8362EA5FCF4A97D47F5E86F8D9D8
	B016834FF6C84BF9B4A93FD0E8484D0377DEF89F7DDAD27EEB70FB3D30611342F07861C74E32E902507D03AF59834BD93695652C226F573CD9G4F35GFD00A34F5877097CBB5EAC1671A1B29EA1B3CB90C24B884DA3EF164838B4204983DCAB00310C51BB61499418DAD7AB53755E94A3247F4815350664234A93E1314C1BAEBFCB0597B323B693D94FD2BD89CFB93C1B81C8613305E70FD7423341E09D5C5DFF4A6229B853AFCAD2055BE2B31BBC260A4191CBDD1D21354DA0B9BCAD8E3B4765108CA63BA895FF
	C0727C30A40F66A18829105FEC0A5BE2A6F2FA613787A06C83447FC6A478E570DB78B0A93F78C3AC5B2E779BE45D0D973EA6A4EF35652C73D3364A8FD9190C9FAAE5B38FBF1375BA2D0732EAE26F88C81B8F3088A08D2030BCA93F9260CDDA7F9FED7BG1ECD2720A8B61B435E64F4B6CBE657478E33A8416F3ABAC811F1DDE23F43E59684D6473FFF33DCC71FC040787EFE59FC4CA6B15B303EE33B3EAC24BCB4A3EB150ECDD2E2AEE43517B3DB7415ABB6E132ABCB7C491ED26E6D0F5E32BF955432C38EBE191D
	2BA3BB3A7E496C88B91F98156C6565242E6F21BE10883F899F71D5060F27784AAD8A1E7966B60AA76FBD1036F08B7A06F1AD6A5219F317D0D8F50B2E9DA23F9316D5DE41F4E82D506AF2FE10646D2CE0B2F7F1DD6E2478F4951E69320C7AA9F99F7D885AC9D5D2BE732F2A6D242D9A36A565CBGA1GD1G8BG16GAC5F06BEB63A6D23897A18416472B40FD8A433689298EFD5753F61A90EFADC268E3B5BE332770BC7CCAE0B6914A43A8519262E1A586014975F9E6A7B7DA0E31FE897DDA60F45E1075CCDB651
	A33A5C0C263D7AE5C26392E9EA0F45A602G1BD340FC8D556B61A9195C1ECF1C5074C50502259B2BC91D1CB19D019CA183784D6C72598EE22F026A247CCDG5555E807A7E8FE87C4977496F5F51D8E07F54459C3CAA204D493BB6F23FDC7BB7C6E2BC69B9F2BA538C4F8CF65792C2EF9C515CF2B251F28EFF21D43B6F63F1914BD9D680BEA202D839881A400709A7423BF99510FDA57BCA014BFB9DD570F0A9A1E4C0E2DE17E13D023EDCF0FD0B9C4576510D630BDA93F9C2093C05A0EE341CEF39F66A573075768
	37B55E276924237EFA74241CCCD68F1715ABBF57841003FD12DE1BFF498FFDB62F63441D240E43211C31G0B36E35D345514B2DFEFB639C5746EC3AEF0CC512C549453F74A6028EC73ECD957F594AF88343D507C8461D5GB2D63F8370AE408E000F815E175B64A1001DGA600ADGADGD5G5BGEA081FG0CG5413F684F0AD71F981FDE5AE55E1A968DF3083FC952097A084A096A099A083E0D3AD54AB0087A08AA081E0B9C0514E247C86GB3C098C0BCC08AC0CE9D7882C08F409840EC00C5G69GA575B0
	2683C875D8EFFF92D3B395BBE8ED2E360B5A9EFA7503B6FADF055BA25BD3ED139D324D6AE59B2E156D5D22496F00EC6BC3C17CE82DEC7FFE59460168C6A61057DA391EAEE71C7C3F8E2C2EAD83242EC9BD677A214176272EEF3D7C088F103638D1055BACB0FFD157B9E91758EE5E166908BFFD28490F7846DB827321E0E5413637B388DD4D84EDD37FFF20FEBECFE0FDE17DF6E2E7710CE877340ACE51EE069FEEB92DF9BB29E7F9DC9FF0F01EF60AF7C29C5082832F1B638FEEA77EE072F8DC16D3A39E31DB74F8
	AC76C1A57D8815659EB965D4C28814F5E2BB71B547D90883B87D296DE48C6DF7586DA26D14B90B1CEE26E338A809B6D03A45B1E277B07C708EB257310A676498423EE325C0E2GE90444A3EC7E90E7A27E6BF4D9EC50551310C6D8DACF46774FCA0AD5B60AG9C19874D076722FA9C174F3E0363722673778A8BCFBA3374E24D48FB26E515B730713031C1991719CEA7EB75F597E9ACA1635DCC57FF9DA1EDCCE7BAC35B68FA217A82312B53AC36425C0724C315D549E9031F3D814FD6F140B4A2F9BAADEE8F8D69
	DAACE7AC12653CE82E92E43A4EAEE2A39BC964388DE7C9DCE736381D1269DC13596404302CDBF41D31740B1CE63C1D34653DA376FE963D912DF07C4D2CA7E56C0332FBG429AF07C7DF5ADC94758C89EF8DB9C12442A995296B5103A48920BE4DB4E0677858D2CAE48FB7BAE12BE172793A85A2524B37EA7F8FAB8244578241F994074791036D8154EFC612DC1BDDFC8853A74DDD8161F6CACA4BEAD1E9E91A16CA50162816EF7CF6EFCC513F6C476C1167E4CCE620312AA46B46CA6F2DF984479F541EEA6F353EE
	963FB542F3837185CA772DA1240B5A4DE2BB129E8F10AC53C56EA1F457D910EE99608540156EE179766DE165E5F4870711EE699EA6AFC326ABEAE4F4D2A3214B33A2DDF5A30BC7BA9BD93E3D702CA0FAB7913A1FF13A790DAC3F450DAC76CC05678AGC677328D690CCD0C4E894FA9G51G71GCC3F83F624CB1469D21B74744BF6707509A6969B65B5B37A62E635FECFF03A4AE6A5BFE24776E635DDEEF7A2DDEFB3AB2727595BFECCEE5FE9240BEDE6F1D802CC1FAC4BE579D50FF3BBB733F8BCC34E2F210565
	476AF785276BEBE17698EDE174E1AD2CFE195C6FDC64FE5042623EF819AE21C5AD7726CBC837240579C9DA0BAC371549E5F6B18AC897524A6C91554A68D361198140645E1909F405EDAC76ACEBE3F6ECE8E3F2D9B9FA33F87D36B1F94EB66687449F94BF7DCFB652C534A9FEC268E35B547571B3CE17A253254B79966ED5535D144365825E89903E1751AD76227BA497691683BEE71FC237E91F3AFE73D7A2DD6BBE96B7776EE3F61176297D6A971CAEF49F33C774BEE64785DE79452EC23A457BD87C0D76DB36CF
	DDEF03F9C817534E641534B37D065A55652848C73AC819AECE264BE8D7576F8B1C2E3403492B6DE0F6EC6FD053FDED83528DF53079409419AE921E719D3896777CE652CF523538E1539953E8057BAC0C7BF5F57BD5EBEE09F45ECC7A3B448EDCDB2BB21271C1588F6D9220F2BF0E2F0F2F41353588EB0C50F7A8B95B5F3AE17BFE591E7B1571B572533359EC7CB950C0E446C25A027D58DF9B6B54737BA6375B51EF21CB8BF21F3C2ADEBD7EB2DCC17DEAF88ED8DCEECF8784C82358FF3FD2CF74A5EB8FB2DADE0B
	59CC57E24878389864669D00FE79G418F08A4AEA1FDB9ABE77FB6ACE77879FB043E693332DE72D34E10832CFC338FA865CCFF32BA1B597F3652B2C13D96CAD7BF7D8E79CC7678DE350D972C9B77ABBBC5161921129D7E415F32D9D9DF64E5ED6804BE2F934B3AE300141574D70C6E3681240B851A251C6E97140E74874CCFBE99A476A765AA79987DE443B0616D01F789A064E34CF3439066998538E4G96130C09C4CF6FF8886315FB0708BC4F10454D6C705726C6391C0B3EFC17B0FFD5D8165EBA6E505C482C
	02032C7C558795BB3426BE13E58410576038BEA1C033G0045A9D1C36AB8A59D704507200FBC047A3AAD243CB47662BE741025C036038168C28FE1DE2B6A55F9AD847CB200828366754670D6F81AE8A0D5419E8651B560F059C4F399E91D9526331E8A9A6FD7B45164B672FBB34645D107497C4CADFA48C25ADE17435683465BEB91A5F3D9A7E517CC76410AEE88716D03751B0F88387EFA57FA359D9BAEFD26EB4750F5C3D92586B906B3A8F6B4BE7ECC1674B85A716CB016BD82E89671327DF21834C38FA8E6F0
	D848B4C556F97FAE92E7594533BDBC296CF8ABD10E65D7E545FA49B98CF6BF0C79C5DA09AF18DC03D56CFDA1FDA75E424AB4F6944BB4E5D13CD03C7E865DB291709CE6E509BA2C14A96CD74FE4C57F8A4B74AFAB16E9B950EC6AC29D86A474C776FE23C4E41BC6BC0E6EFE13C456A9D83BF8D042BD0B5EAE6CBF9E582036F91EE92A2EFEE13F9F4C0A68E2FAAD68D274DB8876CE66B62F3321FEB95DE023EE546FCE9B6A477C6F09E1357FF900AE8AE0A1277F210D64C167DADC7767EC28FBF2B736DD0F9D659574
	405852037CCF5BD19E23ABF7E05D0D81CDBC277B12433B6E9EF5E8FD0371E7F07E65403B6993643F46C9F46AA71D83D0B35A9FB8D127DE200B60349527497E4B0023FF44DDC17F6FF438BD5A463417A4E0DBE279DDFB9AE5AF023CF20EE0FEFF9B7356FD43B87A9D63BBB10EFAB4G0F0773A50C13365DBF84ADD02CF0FBCC9E31057EE68DDB9EF6E5F5D80AF75BAEF967D56F365DADD9E06ED556C15BB1137D76C55ECF035C25DC7647836ABE0A61E2AFFA5755F31C3764D3080FBEC55E2ECB5E65BDFFC96DA30C
	7711CBD866B060CB603CEF8B4447A598453AC5F9FED8C1B97D973DD3CD4CAC404A9F393F014AE5DB9387CDB61EC967C4F2016E0E69FA05003AE6001E55C654F599DD3FA23A120127825C90A64BA62949EC762BA7A114AB096571D6669ADAC7C430E8463802352326CCECC712915B510FB25166338117E244182CE8375A46AC6F963A7F2AAE3F1B5BFFEA9666D3F494E23C23D826348162B33AEBG1C77C0B65AA38478969CC59D36B6EF7247DBF532893B3832E3E857FF71FC52A10F72E33C0F4CA1F3ECE8DE9EF0
	D94252C5EA0D493861FC03FF99AD9A1632CE9A9FD9ECA6E8C094436B74119C14ED8639D147F03C6D30287B3BE440E7G149E677E197B5F0D3733FAC93EA6339917D04A14F16BE2AE6AD48D7224632853DDC3FABA4DBE0EF6DF55C546C3E82922C76459F6A879EEDD09F5BD9FF852F8D9E62E74EE275FDE29670BAC0F6CD518C7D9AF4C03FBB10F61D5E48CA21B709DE4A951B2E0A18BF6ACED7AAA5FBE77C91ECFA464311867F3B04F3F2F066721DD72CEC09CF982F97E1E777975952BC77F437A4ACF5C228BE2E9
	0BDDB46F35370DBADD221BECA4F61ACE09122A2EBE4DC71DC6C11FB82E53B773BFBF1D3272C9DB946D44A22D34F61BC7BC9E59E5E4BDB62CC6FBAE839DCABFC3BD26ED403E1A65F5E28B7121FEC9B439BEB6392C22391B0CA76EB2EC8F299B30BCC648A37CB3742D5D07895E8E7EED123AC4B74CB0D87EC7B76A35D156EF0DEF44FEAB71B35CCF7AB15F6797CE321824682432F7D8863F9BG8CAA1C3CC7CF670C6613EC7FBB176363A8FED405E76558E164638A24251D44BD5B01BDE44E5A5317146F8488850886
	D888105A07F345D975E43FA1601EF88769636DA6295BEBD19B77269BC8192D62B9793D27C15DA7B3BBFCA492BBE474855EEFD583635BC5790ACC9367E3B16366E36AB3B9B9AF6F4FA2BA6A45342DF25EBDA6A5E6E436FB2D0978CE846063G9281D28104D3E837279A4A03598D4F874886E3790EB713FCCBA11F86009EG89A00C677B4E2E207551B62ADB9F4CE6424064EC3DF660F2ED4D786E1E243C87A7A96F5820766CCC6CA9A50F0527FC73E3ED67E6BA5FCABE455ACEBE475FC171E9AABCEBD31BA81E3C97
	74C37B6947B63DE9A858392164FEEF9D985F5620FCABB4FC4CDF1EE8A16BD305E60895GDA817AGC2G2281924C68B76EDD4D417C2689B72DFA9C7CE70BF66389757D53A44B199114AF456C4D477A5C47B613FDD41D75B5B2D248FB9599D675DEC5BA64D3AA02CDG0CA22E2DBCB104EBABCE916B6C6B962DCEB122378E7AE579018FDF0566BDC154AB4B3BE5A57AE5616B8C2CBC6F9F566E3D24C0FE42C0D2FE51G16E7F7BD0EAB6583582F3737117C4D70BE85A092A09EE089C0BA619BC41F787E2EDD41FC02
	8E3DBD0E964035BB9CD69DF710EDB1BEBCB99BDE6F4337C173DEBB28473743BAB97FEB8B4AE79854EBE709FB09CD07A0AD84A08AE0BEC08A408A0012A134696B3B0336B3FE3CEBA26DACD71A1CDD577B70E9FD33FAC80F6FE19FBEBD68D471B27B6CEAA77669837C98C084C09CC0B2C09AC011856DE35DDDBFC11FE3B385DF43B0BD9BED13334BC19FBE9F3BD8745AEC494E57755BACDBBF90F03FF46D96ED1BA5EB131D10E71F855B6CEAABE933E40EB0EA41BEA84DC7AF5D3ED1259B63BBEF1F1C9DC2FD7834F6
	C833680ED30E49353B4620FCF943FAFCD94E4949AB8C4AD72AAB6F47BEFC93336753BEFCDAFB566A4A1BF6FAF2653BADA81FC1D7DE7F78644AF75A07CFDB3EA1DDF96FDF1CDC79EA0272CD51157717C94A7BD7D03EC8DDF98BAFCD4E1EBF7161530EFBF13A722E91A6A7EF3A8F1F367E92F5656D4D1CC83F14BA2C6D1796876553577317C17952F5753CBEEBF272E68665AB306A719D4E1EDCBD1C746153567BA6DDF939B9130C83FC7834729AF465AD1C243C1F7870E97D2CC7A30F0D2F6B3A7D0C2FECF9C74071
	754AE16D78CA4EF5CB106794AB0E2FD3AC384F9EE94571B5A4F7F265395907CFDB1E451A72B09D1E62730CE5DC075A151373513DC179CAA5BD3E7CD5131377DAD03EDADDF9CFCFD25E6F037299F4654D481BDC1D4E7661535669102E3C93791313E77561534A1B22ABEF536AC92EA584650B5415775B8D13ABDF3A8F1F36FCF13A722E5CB81131A5D1454B6E1FAC2567E8D2G3F820061729D84D7E003B93F8DF13F257B1B2D705E4BF12BDC84B7855EA3B9EE1D011E110177849B36557B0D8437845E53B86EE10A
	A3F3F03257A02FC19A0D93A8039E103C7D252549E9CC4611C94B503B270000675A593C4C31477F9968B3FB744FC00F6D51BF83FD651E17999E72D05F1442746B7684BEA3FD739E3DB352EF51727C0C6E5139C66CE41B1B0AE56307B8CA4E4C436C31EB444E2616C432DB3B56AA60FCF53C036C85945AF1DEDA5638D5701D173619AD9E078BB36991CD2EC38383FC525E6D947BD1670DCD7E6D77DE133E7D4A1B746D3735A9307D6A1A825BEFEA33FFDD421A75F53939D9DF174866403AC4B77B5305352D5374ACD3
	B95839510E6D6D39A1EC17E6BB362348E132F7928A6FB1G0BG52F85ABDB42D581114DF8BE08498F5E05A87435A36C17802350B989D9AA2B3542E57CF357848481FC05B73F7EFC45BA619DF7F4EC37635FC4E7913CDBF81772E0F8F654BB4860F4300F7C6568B4F34782F6F25AD7A757D07963A2F6998F1750B5ABA7DCBCBB1CFD356DFBABC220D2843E8DE247C9E13EBD07471094C56E21E660FFFF5AB094FCED94C9617FC621D1571DD0B3A0CDB850C8F875B081D7456DF7ADD8E09DE8590B0C6EC750991C91F
	9D89BE936B4098311A1EA5A275116040B3F63F2377CAB3603D481914DF6EC49FDB92F04D3A55696DB3C6F8E7F57A43FF53BB89501CC4F6AA061CF4104B19464B5966F134EC653AAFB32AF5171CAC3E0D00E70C93F5BFF998F5DF68C45D676CA13A2F00774253C979E527D177167AC03AB71E76ED872CFE3A5A7C7B5451B6FD1FBA59665F27865A027B14234D3FCF1DE98B6ED3175A7C75C54C667143EA1BEF63F83B75A5743981174386DE4772CB5A6B5F86CF6D5537412F767A3741EF778637411F777A37415F76
	8637413F766A372B86C9D5760F378B58BF7F183683F2663A67B40E057B5A7D177BE03BFE39FB5A7D177BF8FB70F21B5B7D175B5A9E3C5C1E7640F5FF5626AEBFB923445A525EAD581606CEE3DB3A021ED30C067779G0BF9DBDAE7A56340B2D51B4950341FC217777BA655FB2D6AF727062E4F4563D3BB5607470567969E342B7D361A17496270765BEDB2FE27D3DDD6454FF3CE2B73594E715FBF6D1D4FF699BFEBDCCDDF4569BF3B286E2395FD4AAEF967D3A3637FFEC91DCFAD277F262056331A7B6409CC34C1
	A837412AAC6F3C2B65BC3E132556C5A9EBE836DA66CE0EEF48D163EBB87E6FDEF845BF9E99A63A50FDDF977A87BB1B1B8C6F4B81F25C689FD379FEE009DB295FCA79F7ABBC0DG12AACDBE37193BDAED67CA3E9626BB6ECC1179A35C3EFDB74B4F31525BD6F88F2EF1155A5ECABD3C1267CD3FC3467FB84FCBAFFE5FADA25F8B3F1547A72B5579AB7E7256C6B5FEAB2F67199B311EE35CD84F7FA8A17D0053A58E08AE17E86E361CA763FA1A1B50973DC56867436F84CEDF6AA0F776A453B94708875A2C0750CDF7
	6098DA8D6F1DG9E00D08F9BCBD9BC7160D9D266811749865D848D6447C6886FBF1D483B8C68B3GCAG5FG50BB02B2B738882F4DB4EA310D582806CC3F65F4EE19GF40BB96DFB86CAEB31EBE8F753B9E78650651DC15A94BA8E6D6FF6CA968F89E8305E06F2C95FC68EE1E96F98323E6F0A8EE2B35E77F9450CCFDBFD6DB30B5F692B86591DG9E0050B36CFB884C8ED996240986FCBCC0B2C0BAC051D9543738166805BA913174C49817FD46741AAADDF3EECC26F9207FD5CD9EFC931C6753C76F057A6623D0
	32B301CFF91D4B2EBE4B4AE3BC0BE7861FB46B25CBBCFD730076DE81C91F4253FFA36A25C71C4538EC0C7795B1E7312FA826F798D261FD85C061A87F5E845FE7AD9B75ED478D00B3E8706CBC4AF1FA3F42BC4A4E23E4F33CC8719E951E1DD37963A03F0F81E94BC6F11C79B31F03F51EC35C9BD4FFA73C071D43F6F2EB051F338A5CCBD959CB0F621AEB74B9AC3B53C0E4AF0177B4003C73D8F673E1ED3FD6FADEA9EBB57CEEBF2FD776B79AC9FE3D67D999D7F17CFB946FD161D959E3E9CCCC5E43A0AD6DBC1673
	D8BD163D678260E9FBDC4A6F964C3E00FA9E582255B37E0222D7A27CCE392027678BB4FEC83B2055B3D3A2783CB12D1E57D8D14FD2C88B99C3BD2B68BE71B2F8974691B7C6F1C570DE46F15F233886F86F6138F534BCC3641D630ED1DCA43C47F15CEC89ED1140F13F216B67C597E18EF4916D330146A5C46FC60E73E4A24EF091ED564677C17BAEB25BF8AEAAB68A055FD19775EC769C9DD363AEEAED76F70ACF3C28355978B05AAC9552DAAF216ED196DC9B0C6138A66A578B60FD91474D9946F2AF6138D7AC38
	B6D8B685F17FC3F18D705E43F1AF8CE1FEC39CF7440279F9G17BC051E475CC856189260F7719564FBE4E603407B347F31178A743ECEA9BFA05DEA3109F6522F15D555D41617EE2F2C5C4C4EABC61FD51FD72CDDFD256EDDFB1AFE85332DD3FEE63CBACB7E0658B7689D90429383E929D7E07B7EED451B6A76CD94292BEBF6107B681DEC50E06DBB5709F5DDFCA5366FED742E08815E07G26DC09F7F2C65F43FBF531F36E92FA8E685FDDCC6D994F0A3CD23E13F325F23EF2F47819ACA6731D53A8F38524D7DEC5
	F007CF2B7BE0E24B67F258FAB3CB3F4AAB1D0CCF8F896A74F9DE69C45F37B355693F73CAA7B67A2E4C4F4A76583BD8360C476284E9DE085F6F390DDEA55F07121F61CBE7E6764CBD1F4DFCA124CB71059CC3GDF58DA893ED08B3E407866F6ABFCC546C0BE3435263CF49B70317A7F4EA056FFB268D3900275DF5567553F337A5F6732181BCD034C8164B3ABC3D8972DA1D87F35F44C990377880058909C874BACC81B900275F644222D37473C6AA554222D376FE629539F31E86B4D162DCEDFEE51565BADF23AFC
	BFF7404F7DDC9A0CB03D8B2D38E717414BD2A0A22EFAAA627A86F01C17B86EF50E1BBD957B42AB78593614294C875226AAFDDF4E5540FB35DEDF98C6E306722B35673BEF2178462B35673BB770FB12BD10B67FEA6C138C927605A557A06E9ABE6ED7F25C733C5F1BF28D6AEE22F7C4A3783B153E137B546C7D7941496EC9E801485062180C02214FCB065EBE880BF3D772B8B78674D8G10821081D0920AF1EE5530F287841A96B92CA6601CE47FB07AFAC3A85A665D23D86F23213EE58B83DCB4C03C1C1688CFD6
	3FCBE93C14922A2D77BFD3FCDA28365E4F98305EF32E05BE78DA2C4F8B07F1AFEC91473D5005751E46F14EEE02AB39AEA93F7ABA543D1446D26D705E8B606178F20A8F3F0EFD4B015CF7E4782F6E51569161532B830F8A5F6E07C65F8F9E73112137360E74016ADC8FD6DCC777A96ADB6577B89D5DB0264E151F89C0338460D52C276BD85DA09D4CE3591DD6570848ECFD0A2EB3B5DE8FF14475E87FDFD7E01D0CF2DC6ECA7A9DBDF8CF3D9EED7C8F9A9F2643FBC1D8D27E26B0442F2271E4833C770631EFE3B07C
	8D56C06D066491489E1B7C249379CA1D4C89A843DF7D685513DEBBE5B6EC2773E12749E27750DE5AAD3739BEAB363931B0FE971662AC76B3224BA31A995DFDD60C69434314B976EA4C334752EFF5E3BBCE6734511C76BD096CBB59CC438E97216DF61A7AE14CE074FB78FCA10E531F16083EEE3B495963E019B35AEBB96DC220E57E7628047E36D8475EEADFA236DCCA7842183F915B955C2075377FE7A1329DAE0BE87730EFB9B059AF8F236C629BD04F9ABB612B643C57E762BB7341DE9A6BB582FD6F8D681789
	D65C47F3F2DC0B8D7DB7026346A8EEA13CAF39817D71AFB4D6DF8E6F85B7421C62C6447F1362BBEFE45FA5A15F9CE1782B68FE9B217BEF7BFDA2137B7D0DEA7F0D22B27C796165003A1FC13FE7367F9E9DD7A510BBFAA35A5EB80C771CD9BC77739EF8157B20EF8C378611037D8F1D27C21E84DE157DE07E0D0ACFB07975D4FE826097F37991D67D752877291FE4GDD5EB41C2F3C7809221B4D92CC37D581F42B1E2675576F533911FDC41264EF890B6AB9CE6505CE3FF7329A2731FAB4CCD366B8AC0F1F9ED767
	D1B8CB4A4A7413479419B7EC1AB26FE06782BE7A147AAF60D32661FE483D86728D3EA326E15FD38100B4389CCE68F44EB1AC7D3A156CE35F8DFA66D92FDFFCD9F8FC12FC2B031EF976E99FB43E18CB67F1992ABA126F93D2FCDE3802E7F147CEA74EA50AA0EDB49C6F1D74513D4B94F8DF81D0F8934C23815A814CB721AD7FEAA07DCC40737FBD8EE727F8C614D4478DD8794A021EF95682730BEF2F42B9DB7CA37789732FEE757B1D17511B18BFC44A4F32E7E7E612A7AB5F73C7C97992A1EDA9C05E4DC97925G
	55G1DB7E3796A8E4B777E08BBB4F9BC267EA17A959DFE9045A21ED550E4611915AD51675E5A446A7CE71364DB97749E163E2F4452B9DF5F4DDADF19C771524DDADF59BD043EB28AE9A9B7E39F3F45676E544474FED5777ECE6317D8FD75FC495FDC62E5D10FEFC8269F734BA78EA0DF388E4DEC404D87D824CAE3FC9BB8DF2A8EDFBA6072A2E00E9121BD5B96C97B61CA40F7C660187A2C856379BE0EBBE14531B704631E2238B8F8CF0CC0BB5F4C564360BD0D63EE23383C696C9BBC72D9C34BFFBB57C2997E6B
	EE57FFAD8347C03DB4B60E1DE37B59D0366A6998EB5DBE04E3D75FF47F31BD0287E8C22733310B2E23CF570E2BA5340EE2G9FBF9D47559F1513FD96FA9B318A778E4BAD38F610BA9D57C14CBE6BA04FF82DBF4D70D987A1F21574C41FF510D5DE7CAF7A2C03A0BF5353B4CC3EB72B3EF06FF5551EF644955D9D8777F5361D6CEE6BECEB6969B8F4D0764D06239897964FC01F3BAF937D301C631E25B8A33CFBB82EA28B6342D00EFB096E01C541FB7C8C7443BFD0DCA23C27F05C8B94C76ED11572BB1BE306C03E
	C47839DF4E387CB8D23EB391D006DE5CF7B9407662E673BB1EE9B358DEDC5EAD50AF5C426E0D99EF21EBD05D25B2CD75AD3EE5E917F12CFE2F69560F6F6E20F37DE8200D3D85FD367040AA6A33E4EE05FB0D3B8F50BDC83E35067818D749DC08FCB407DDF8E35F35E37BE2A79B357BEAC477A537605A58DD75588E1683EEA34D8F7A9F817F256D7145C19B22B168634785814771DF9760B8DEF27F5DC26B4FDB335E91744771CD11725EC324925F9559CA33D9D94EED715D938D677776E2A3D9BDA440F3C9A4DB6B
	E07D480A8A6CC752A275775E5571F94ECCA87FCCE54ED6BED39B83172C542F2BEA3A86D08B742DB3312EDE6D4E67F57531454E7328BC21421BC6B9FE43DA3617CCAB0BD41B3CC7DAA7E96B0A144FB99377B8850BDEFA8857632C1514F140A43915BD66EFDBF12D033D3FA4F93F9F11C8191573G51BC1D7947F2897DA3F2261EFF30EFE7EBFD06790761044073AD235FEED577EDF279AB0E34F575F434B4F5B23A6E237E4F98CC25F35F4520C78AG730D3F72FB65F93318EF144233FA96BB875EB70B6E2D59CAF5
	7C9021F3163A0D36E569577BBDF46E9C83347133505E2E93F40F5DE7779C73AA68A469CD4A37F378FD99167E2BD324BEDB3D3E0343C8D8FDD7DA3575CD4A14BA8B67006E4F947BEE9FA67560F73F212A3CF4EB4DE61D3A4236DC55277C6EB1A979BEEF19C83E0C6F6CA905EF6A504479E67593BE96476FB3937FCB1F05B17B2EA364BBB77E66F7646EBB7B569F726FA06E02E7AF0E3C6B0F97227D7D205721D343B036761886B9CF752744567C3C861BB2623748BFA575812E613118A44BF93A14432433B1698172
	E7BF702E366A779E676735D66EFFBCB4FE21B2D879ADC67FE78F1DC67D3307E70C7AE757470C014F4907987DFF0B7DFA237ADB6C2C5EDB6979103CDB1372B78134837482B8EF65F77F0F92BFE2DF52504E7B5A4C03646CA3794BB09E141FF154FFD9330F857FFBB2854748DA66A98734E69B491FFF13759829AF0F43C9118C775AB1E2A317E5F04843C8E57CDB47689A15B8C051721A7E1062D70F2AFE37FEEE7BB17EFD6C4497AA03451D65D65C3F8F3B95E3519B78FCAA12634AC424DB46F1570AC8174EF1E383
	989737C671339BE624B3F25C6D83988347F0DC9725DBG6F499CD74373DB46F11DBC3F8C0EB38EA0EF67ED08134CA8236FB69CC33EB26CFF3CCB24F3998F50CE398D473B82FEFEBE76B6B9960267125BD87C4DE2D20F35D426C93B4DB7964A39DDBD5E2579D9F34C2363FEB150EE3A1D2F3977A9B169D6197F5ECA320620BA69DF45462E1F7A44AAC4BF436DB8F67D1CF439F29EE3C3C4DFFFFB6C55156525D5E40F1D46A3BF65F7B74D37CFB4DE45F864C55ADE58EF5330A27FC7030164576CE472D99B2872DA9B
	09829D62EE8FBC07E17614687CAC81E8975F0EF14937B3CB8370E5F855530B19747BE58EBBF413A30E91B75B7FE7F92F4F42FD0442BB707C4F890AAB057772BB307E7EE3505F0728B3601E0D1153A6513F4F44E692B84F30EFD5F0DB65B524C507F18DA98CF8936E40F9C5D1976619F1871EC9E86EA23455516037G66E8FE5F239B6BF4AC9A65CB5DF43E6FF04A730B61C2757CA2655C11AC1CFFEB6D969F4D6C169A2D4CAF5672FAFF4C2A547B35FB8244A83555D06DB5151B657502990D28E3711DB84F1D6BF5
	FF43GF8731D01FD010DD5DF277BCB23C09B81B09FE0B140D2008C00423B204D81548298G06G46G4281E6834483A482245C05E35B3FFB49B7117D45C8720191BA4039485E1A006BFF7337E05D25C1DEE5F7E3FD8895D8F7FDF7E35DD5D4905AC8F80F83C864341BBF626BA8F7DFEEFB1CEED75A63FC07D2AFE6EB00FA59319DC2474ADA2C1753FCFED2BC9BF55A4157F68C3379BE175737F3C2819FB5BBF0DDB13D1EF6AAFAFD1D762BFE7B29ED2C1FE036FDC3C25B46011CE5DC37D3925A36749E34EDB825B5
	423B93A0749EBE1F235FAA6E06F6CEBAAE79EF3B086AF6D0323151EFBB083E0715EF51BDFA6D204525146BD8203983AD57F65E8E862DF816D418037EF740CC666CEEB98EE7384E817F71D377C0707829170E1FA244BDBD671C72BD9253G11639898F00BBCCE9B98A864388E37416136100E0D15F1000413729E59ECAB2E8F544E893C27CB2093E888609ED95F9CE5FD0065FD2DC749BBDA443CA5BFF9E78748FB4A9C6D9A66BD346CE1000F1C036B9B6DE6141120A3A3DBA3E709CA86DD5B71117183AAE38560F3
	66223FD94C7AE7672F20F1CBA550B54C457165B34ABF846F1E3928E3BF57B1FA2E3E0EEA081F6B2DE362DC2D0ED7D0994900CF65F227D3DD8AE220BD45E0AC91BB22787292CB20B63AFD876B54D99B2D9E46B65A8AF90D45F81F95A7787898EC237731FDF2F84F39B7A93F64DE240D6267522A6FC5BBA653F37DAC76C7FF794799C547FF841A9B5754F80DBBAB07B1FF69DE6C4B123D7607E683FE7E3D017BB296FF3653FEF19149A793F19FD35CD2401135EE266BBB6794DD6F488CE8CF080D36EE2B594CE8FF19
	C94A5DA212EF0EA27F87D9FCBD09663D773C1237E088B47EEEE7D94B34078DE4AE6F3DFE04275C79925249EEC3E7875934E7F6CA72B94BBE31F9CE6AA479DE6F1A5C3E5D43B4AE926E536E5BFD0B628B6E536E5B35F7215F96C35A58FD7C5EC237DAFEAA60536F1BC8BC726185D247F977C35C81508E508760818883088618FFBF7675F306836D3773AAD1C59BCC37DB9B75EC13FCBF53EF597D3EFAB27F7CE6BD7AECA647FD379EFD166018DEBF167C0FB749D270716699C9FF3E7E2C64BBDFDFA6694F577FA429
	676B8C773CC45A6EA044E0C6CE3702630EF25CBFE91E84772945CC47FBF92D4136DA46370BC4A06273EDC42F3394E714F1EBD53823B2EE3D0D34C9A8721043053C4574EFA68CD8A4093D17D09A0B9D06D0EE0F37E96CE1F11AD4FA3F47F1C7394FFF53679E46445A4A22C97265D6CC2E0D5DC563570C7B35ED6CFE0A4F0B5536316A8AFE0E8252C6E3310DD5FC24161F8278345840ED0C711D517DF6E34E830C377881FFFB5C8D3AFB7165B2FD238E9FEBA3F9B65FF6334626EEB772195289FDD352D72F0AC9728D
	DEB69F2B279C7A77BFFA9E50565F9A0A6FFBC0DBFFB3F87CADC15A6287709C4C7431D2F8D6FF9966D6G9E00B000E80079G0BG16FE997B3A0D274B847FFD9D3B0821F4F42CFC92BF83A1FCC5291BA279F799BC9BG8C2AB4B64EBF49638873D77477D9587859F1D160BC0F73736FD1DF41D8C83A2450CE23FC725A0DB9D00CCF6EB1EC63B147B9B3661B2C2B8B4E5F3E1A2948FAA3DB1135EFE232A85F2E9C056FFB6244797AF2953E264BA8DB84AFDBC1DC2032DD35D2A95B1CD50AAC53E5146D07F917DBB666
	C3F5BE5F5D1DD85BEA1FA45F95174D475A5E86FA7639B5CE5BA65F27F8C31C36CD4671F83B97529646E11FFA27573921CD8FA66557BEB811BE75A5DD3DBBE55EBE1DBC545F38716E9B530654FDA35BABB63DC7741577FB6830ACFFEFF6F40D0247E1D956E9813DBB44FEFFCF757BE057973FBEBF78054A106F2DF4236DF671B944A75DE83B27850C9536DF709F0F6D3A90BC9E5BFBC1BF9E6B3C609B0F9D3E209F0FFDFA41B79E6B25793A44C179C6B37FDB3E97C81D32F3879F1B6C26C13A37444A77D53A766781
	FF8AF9905772DFEB42B57F7087510FE3AA147AFED0753B2E620B2F7B6EAFD486ABF35BC7D8776BF95D6F7F886B7EA60E3B01464129E017B4EE2F3F723B2C999C57EDC13A4E2FA2AE1E5F55B4FE956D3A5A2658F23D6AF71474455BF55E97AAC37ECE57187F36B2F7ACF8DB099B53EFAB8F0F793615C747745B4A3747FC5B4A7765FCDD7CE3D78C3FE84CFFDB390A46B1740EAF2F53EEDA778B60FD91474D677E3004636E26EB9D740E6FC3089BE7772727C05C74907A43AF3C468B83604D8FCDE43C781957B953D1
	60891B905FC5331AAF9AF862A744B7530BAF91F8D2A64437492A787896556F07FD3EF576797B7BCF3FD099F25DDBFD4710EFD8319F31F13A7FD0FFC8831B89717C0EA43F9BDE904F7F26DF23773D0DF26236725AECC5932EC976467BDF774533E1CBD4F747885E992FBD97B6DB7216F568A5C5D63E8DE50D6948522E5112FBC384DE177161715AB5E6D7011277E37C0EC7141F3C33F572FEDB4EAFAEDE3B369A4B7659811F486DB82CA95B74B50A7C1FCA416E1828C1FB47A45DC77EA2DA0F42BC2847F938364BF4
	2A057756F9D85FFF317A46EAFF332AE3B5067B2368CB77D751170E2D9FFB534DB27B524569504D7322E3B65B2939B3B4FF2DE2335B791DA1673C60FB8EDA5F8B1927728776ED39A61C3B044F43795E53E46810536BEC181E48537F27D0C94F607BE24243187E76AB827737D6B295427BFD52444FEF8D96A9F9DC772A1207AB50BE1EA60F877E2D7015CF6C4C99BB8F405747EBF9199613CF5B487D1939C95D87BB01A66461097441FB3C7E66C3A47044CE08AF53EBCE3290F812A744176C45378CF8B20270497709
	1BFC7DF4CA132FBF9F51D98FBAEE7325AB9E7625FBEB58176EB99D3AB4AF3A57E8DD5EFAFE9CAAB5760FC22E5BA53C62DB1FCDB7BE757358132DC5D9C2D60730F208CE26BA045C33E436B56A9404ACB29C4DF01F96683AC7F6A9F1EEFC231F239F778885CDE6F3A99E2074886B5809CA0E81416B38608E3A4A19FAE6DBDD7633F7E7E789B9A015C5CE9DC28E89FFC0380BA9118D1193D1C148D183953ED297CB7E115E7C5C04E9A64AC13E3714961CA6B31B3F4DG68894231982053CB46737C0D6CB2BF6F915620
	91CBC9G68915673F765502BDAE666CBDE9A6C60EF4FC15A9A751BF0A400F67EF55E409DE0A2B4015EAE0B5FA36410035A70282C282C748839D56CA9F804BC9E0FDFEEF97EBFF911407F365EA5759D4C95D37B7DFE2B267DD1567774BE2A3D037950B8E96311000FFF9457C9334737887E4F59F65BCC1264B1C956A63B191C39EDBE679115F31E7347C97EEE241193D95A3768B78E07D5BC7F8BD0CB87880840334340ADGG4C14GGD0CB818294G94G88G88GF9FBB0B60840334340ADGG4C14GG8CG
	GGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7AADGGGG
**end of data**/
}
}