package cbit.vcell.simdata.gui;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.DataIdentifier;
import cbit.vcell.math.VariableType;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.*;

import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import java.awt.*;
import cbit.util.*;
import cbit.vcell.math.Function;
import cbit.vcell.simdata.FunctionFileGenerator;

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
	private cbit.vcell.simdata.DisplayAdapterService fieldDisplayAdapterService = new cbit.vcell.simdata.DisplayAdapterService();
	private boolean ivjConnPtoP3Aligning = false;
	private cbit.vcell.simdata.DisplayAdapterService ivjdisplayAdapterService1 = null;
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
		IExpression funcExp = null;
		try {
			funcExp = ExpressionFactory.createExpression(getFunctionExpressionTextField().getText());
		} catch (org.vcell.expression.ExpressionException e) {
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
			VariableType funcType = FunctionFileGenerator.getFunctionVariableType(function, dataIdNames, dataIdVarTypes, !getpdeDataContext1().getIsODEData());
			AnnotatedFunction newFunction = new AnnotatedFunction(funcName, funcExp, "", funcType, true);
			getpdeDataContext1().addFunction(newFunction);
			getpdeDataContext1().refreshIdentifiers();
			getpdeDataContext1().refreshTimes();
			if (!functionsList.contains(newFunction)) {
				functionsList.addElement(newFunction);
			}
		} catch (cbit.util.DataAccessException e) {
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
				} catch (cbit.util.DataAccessException e) {
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
public cbit.vcell.simdata.DisplayAdapterService getDisplayAdapterService() {
	return fieldDisplayAdapterService;
}


/**
 * Return the displayAdapterService1 property value.
 * @return cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simdata.DisplayAdapterService getdisplayAdapterService1() {
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
		} catch (cbit.util.DataAccessException e) {
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
public void setDisplayAdapterService(cbit.vcell.simdata.DisplayAdapterService displayAdapterService) {
	cbit.vcell.simdata.DisplayAdapterService oldValue = fieldDisplayAdapterService;
	fieldDisplayAdapterService = displayAdapterService;
	firePropertyChange("displayAdapterService", oldValue, displayAdapterService);
}


/**
 * Set the displayAdapterService1 to a new value.
 * @param newValue cbit.image.DisplayAdapterService
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setdisplayAdapterService1(cbit.vcell.simdata.DisplayAdapterService newValue) {
	if (ivjdisplayAdapterService1 != newValue) {
		try {
			cbit.vcell.simdata.DisplayAdapterService oldValue = getdisplayAdapterService1();
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
	D0CB838494G88G88G750171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8FDCD4D57638DB181A28D4E4DA6CAE95955BD2D151C6E5C505E5C5451A95959636D814E8383165163BBD8195838D4B4AAD36584DB6ACA4D4C8725F02E2010E05B20E3422B04C3C015101191DF97CD1D0FF675E7B4EFDEF5E3C19C136FE1F2F1F4F714DBD771CFB4EBD775CFB4F7D739E422ACFA297AF4AD9A5880B968B723FCD0B84A1AADC905CCB32F6A0AE2C5BD4A0287E7D83E02590551F874FC8
	48EB49B9D8D0A4CCBE158F69B8C8A77F55D470817CFED7D8322FA88CFE781113B1C1908E0FB3BD94D8CEDD994AD9CA5375AB8B613982A09EF0641967B4017C36152BE5FC2A0CC7D814A388E914E6E0E5310C2B07F49BC084C02C1351CF0367B2AAF96466EA192E653CE8A17B81F74EF2D20E2ACC04D29763B5484F926175C559AEA2AB0A6AC9F8C2A0BD8500D4BED77843218AF8EEEDBC56315D52EE174AFBAD2243D16E31F7D94D12393C23473EF9F3E35D2EC607D35A696C165CCEC72339DBF4387ECB4A4CDC19
	CF4B9004EAC8AF24383643C4CE9C7CCE82C867786AD6022FF11B8A1A81DCEE2C5B364F374A3AB78F5CAC24F7CCDD76361F3A0DF633BA453A153ACDDA5B1E7BB75A8EA16B083DB3A12F48E3AAD883D08F5089B09A60A35A7EE34B3E00E7EDBBD4252B4B59DD6BF26DF0D85DFB1DD651813FB7EF069C9937DF34B85DD6C1E0EDFC754AB29DFD8281630BBFE3BEE613680D585EB17B2F95D2EF1C1833D447A6295183B9119EE60BF80FE293A6FB3EDF59A99EEFFF7416FDD0D04B96DE5C30F4310E6C6247B7AC4D144B
	91A4C5F61DC45ABA057AC0897CA6FC44D7997E850A9F2D42B35F34D1BCC947C05E6A9E740D6695280BA16FB221E87DC53AF618F4F7E8CEC88F53A122C72BCBF29BA9FBDA8F13390D6B729A459BFA95BC53E5BB75D312CE063CA97D268266DF633EA2FDB57A0429A091A095A09BE0550029208220EEGFDECC659D7C37431C633DB5A51E3F7D8C53740F8472DFB9F1EE23F64B6B7F4FBA4F337C5B4195DF6F33BC37488B24D44F544866DDEFC17D05F37000C6F44EE51ED166C4EEEA85D5CA5CA225B43E822562DA4
	B4EE11669A6DDDA28868F289D8EE4C3AF76169B0FB2483AE687A22C24172635611B669B51B20C4A8GFEB33B5CDAC16CE5057A8F8204F2BBFCD5CE4A5BA33AE134583C5960F49E6BF199C9CD045481E2677CF7485891893F638750463FDBCFF0A50326026AC1AC47D67E0E2A1CBA3B0528EFF69F47BE764CE1D2F78350BBGC6834482A482E48C229F4DEBC6BF2ADBFE0DD07658053AFE9486BE1DF71279CF71C9EDFF1ACB6590DD6BA04F8A9082908510F89267824761B639AC228BF9E68C6A77B5BE266964237E
	FA74241ECC56A8AEEB317CDC9EC08E0EC9FAFD3E408FFDAEEF63B59BC99BE7C0BD73CE4158FA8A5B66527292666BBB4C9E913D7B87B7B826E8D5DA0A697BF8DB3FEC73DCD95737A9DE90E8FF21658942FBGE42E7E9460B300CDGFB81BE177B648FG5FG188156816C84D88F3091200A7889C08F4096520FG46901F9750D77EC2F5588A7ADB81C28162GD28172CE1B8A4AG0CG03GD1GC9G59G45A3204D819CG61GD3G52818AFFE1AA2884E8818881088118813090E04DD92682830084908110F896
	36DBDDEB5AA2458EDA1B2B6D223607DE7B200DBED761564976D45BE413EC33AD328DD748765E29A9EF0FEC6B9F0278518A597E9659460168FA06D0568A391D4EE41E7C3F8E2C2D6F24EDCD5AF9319F9A6CFF6A7656AB0F78G690B1F28F0EB8566AF6AB6A77D927B4DBFE4BA62CFDFEA4AA33E71F70179D0303AE05F7BA688DD5590ED33657FC07B7C144046420B3EA2F696FB45EE29CEF4095DD678611173FE7595E9E7F9DE37B9B9CF94453BA08E5889932F07632F7E0A7803D9125C76769EC9EC92A5495E5D21
	645FC0E5F9FA5ADDCA880132EE790A781A338F62GCEFF47D7E48E35B83B3BC5BAA8F396B9BF014E6122C36C8225F7BAFB3AA5063F37022CF50E0947659862010AA58209819CBDA49EE16B07D8B371DF175B5E85C3BD89E9042DE713797D13F7D62BEC1489639FD98795C15E1A33F1DE6E7B946765B5A7FEA3A4343AF274E24DC9D7CC4889B91B4D07134ED666E52653ED6B75F53F0B4692B25E43F4FD53C47A184EF2067651F7054A81E2D717D52C03358F4907A6DBAF67F59C79901EF5224D5C63108CF60FC4C3
	3A1D76DE3B43FEC2342E97E4BA43FEE223AE1249F19F7711384EEA77389C666335D6338B4232A6515DEB37081C66E4BD694B5F76F4DBD874C634427977F0A529E3BC54BD89A07DEC1CFF37EDA079989B49936FCE2743411A1958B50434C55511D532AD97826F2A905696A49DC9731F6279849C2AFC463F249573B3A0AD0C54667F066797C1DE29AA1F79C2E31B1EAFD4831DE1A456650B0DC54427457F7608907612C0F18F773B759B5F516419E49FE4795FECA4BE68D0451851A3095CBDE6DCDFDBE51D86C73278
	ED92BCBF86BC23FB2F9D6932C732580E64971E83B141B90CAE6B9CC2174669EAA05D8C50FF8EABB77E9CD6DFC697E5C13A2D2318BC63A8C66798456892C7913AA3D6248B9D45621128D12C5CB8F816933DC7933AB291690AC63372CAC73358339A1E0DG0C2E4306F431B25DB4F826GACGA89CE3AAE07ADD5A01F4A5E398DD7598BD7DFE6C647B93E3D8EC54A6533B462875DBEBC73A512A72089DA347286D124D69624630FAA60D71369F137B3B23C817F7AE0B430A4FE574E567B2392C3CF3FB391D4FE57138
	71DCD6DE78392CBC563E5F77A1DD7C394C9E49B2FD7A392CFD195C557D5C8F4EE5F1DF61D8C6D7BCD6AD7785CED7B11679C97DD8D96ED8A61759655A63C817BA1659A3DB262F8E85DD81185C57CEA0DDE7A80BBDC704B2BB0607B2392C9EB78C70768DE5722605B2BFA07E2078E97FA052E506AAFEC2687346295B236CA452D50EE3F406F10C2E530B6E85CE978B78E9G99B2DD69F8B55DEFCFA1DD9D60DB46ABF40363556D5BFA9A69A2463338B9EEBC33CB62F835DFD588C817B61E59E341F8E647D593546565
	ACC23A5289AC7EC67B55CCD0375B6D1C2EE58213A7CDE07AA5CCD057A3A4876932E43A7CB0C6E78CD3376F1EDC246B8FE3724242189DA33D68C6FD08F489E1ECBD10A253E5413370BC5C0BFBE5AD99A769DE5CD1F32F393F5C5387737E664D3BD5FBEEE3683A190CF7A567615E5A6BCDE4FEE806348BE074F9B83F7EFEB96E2D059F0B96D2DFDC3F545F3EE164F932BD4FD366D761655AA5EC7E392A0A484CBB9F6CFEBE0E57F19B556B7BDA0F47E931532D85F9CC36D42A67DF066B2CFC8B1EB63B5BA3B5C00054
	0F63F7F7A551176CBD48E8F9AF2665C821406647D2105B86B069FC021FFC0444A5E4ACE77534EC44FA061D38C2E83B70621C82BF75CCBD1F55EF6179CABD4BCEADD84C6C7F4B12D2C13D97CAF7BF7DCE79CC7649EF55B61E7276C93F32ABAFE0B20D97A832939C46252C2E6BDA312E61101FF4815675E8AB29AB992F98DDB2274B8BB795EC8DC73A65ED040E0C874CCFC637917B13FAC96168A77BAC04B7865209G2961D826E446B23381D7F621290045A4579DA1FAFA47C3982F3CE9A67224CE3B0759610F35B5
	F2BD132E3DCC08DF9A1A23370F5BF955249C6B052C7E2197AAF60809EC4FCDG1077605AD11FD420D9B5917A930DB3AE302A63948360DDG6193D15F7B5BC9FDE96C45FDE8992DB341C783DD9AAFCB2AD417357AA298ABG2C97E1D9AE4B86F81AE9A0D54E9E0D225B66F4F70956D252BB4B4DFDD2B90D774BEBE976AE72FBAD4645917B487A4CA3CAE4A36DDB37334B88467B56AEBA2C25864A6EB0F7F714B7C1085F5D31E52DC9407D574B5ED55BF16B69A33AF69C7DF6E7CE7FC5F28CF7114A1F4EEB4F1DF19E
	5A71A1DE77CC20D9B3896BD6E9A57DD082459A1DF632CC11F55E3D0D44D95DE21F11E7159E2AA34A3172C6D931DDDA202CF0DE5EBCAB7185333BE3BDCB3FAFD25F008A33BA8D7E08F59A1194A72CFEF71CEE1DE81DA7313AE4CFD21DDF7C3ABDB71F7B4681916BD4B799661549284394917D112553C5A25B5CA3B91BACE6875927E07DE2298D4FAC62A66378F14D47EA1B671BC76A6A977AE2C7CE66E426572A0B957DCA4FEF4F5DFABE6A97E6C37DDA00A67CE2546FB59B6A477CEF0B17AFA781DDB6406ACB107E
	138ED286DDEBF15DEBBBD07732CB306FBE5001722C000B607C6BBBD19E23BB2B935BEE865094C6A05D0B1D5EED37522E758D463F5F0E7CF540BB4879E75A09CE96B2B8GB5237D7BD154A98E68B2B9ED48D1F27EE2F3DAFABC6574FF0353A3E9BB5337A4837B92AB6F5923A8FB4DAFA12E78A516E7687556DD6AC53FE3FCB776229E6140134479BE6CA3FD5B5289BDD0AC77C8E6C95CC9FF330EADCF3B32BAAC473BEF97FF7A1EF75FEEF258E16DD55AC07BB1135D57474769DF41B87DAB14BD22D5BDC6B1DCCE3F
	F7DB5576A32F84FCB11CB73A5F3B3E497DEA9FE13CC547314E6940D77CEB646DBBCEFC5C8133D813A82F8F4BA9277F2A9B54444C822C7E13F6EF25F25951447766AEDE08E1A82540F047F4FD026BEA84BDC3392E5BCFEC12F5A593CFB938A1AC164D0EDA2B552F1E04D0EEA4D6066744F25AC6C430E845380275230BCEE0BFCA7CB5762312813479C240D5C6E2CC36EA3B5A462C6C49834BB57577F07BBF4D4BF1C099D111D827CF5B084F686E81F05ECB87519E2940376AD254E1430EB57EF85737566290D77A9F
	346B0113D80E814A8839140F11A7499A9B3A1784AECBD87613D6E3B2461D586ADF46CE8DCB290146C776AEB3F4A00A61EDDAF48AE5CF8139591762FCDBAE2A473B324BA01686683F0C7B67297FED3E3D380514EB36DAF18B25D41937E6F01DC2C1DE62E528D318D5CF270517215D176EA773A174D4D192F9318DCA3923CFE3DB97DD8EEB404B31AEAF1F766E279F88FA3E484A78D140B2C6GFFBCAFA3016E5B12C378863215E8375949069D4B1BA3780E39DFAE42F232200C52A8ACA7EA11FFDFFDFE915A258D68
	A7F11E3DB9BF5DD871D67FBF59D8F940A33AA11636F70B56EF3BF7753B5C22079CA49A4C6D22C355D6576420CE4920CF7E9528532A5C1FCE271CE524AF0A5D44A2F534F5F774C8126CB2329ED2AE5A3386F46867FA34FE08E3B5AB6B70BA62C39607E8F66FB53B0F0956A6B21FF8CA31BF44FE08750905B2B22EC05F5A3E0F603B413F4D0E7D2287D6982C7C359F6975D1B6EE6D7D88472D12AB71BCE97CBBF84E5FFCA50BC99CD7AAE707A360F7B8C0348A474E3F3B0DC47E14AB597977E20EBFCE7149AABC2B07
	E064738A6455DF05E736F7D713B5EB8C2427812481ACGD89DEDAA280E4635622B15643CA16019F88399633B4D0EA62FCDEDBC1B2EA2F5BEA69E175331D56AB11959217D883103B1BA70F92B9A981F0B72B94E000F450CEB7F233E131337F2F78E51D1AF260D104B0E09D6E2C6E6BB4FE662BB19004F7F0D292084209A2079B7E8372A2F4B02598D6F874886E365BE3905145B8F65048344GA4822473F2CFFF9D34BDF6756B36873359465661593A3675CCEDAD1F9D8FD3DE7AB0650DEB535E1D493BDAA9E37555
	3E65313EB3152E374A2EE6FDE79947CF2778FA951E75290394CF52D6481BF6B576699366E077064AFE6B2D83639B9D142FD143477C652BEDE4FF2A9372C2GA2G6281D28196G9447205FBC72750EE0FED30B07D6C6A77F39D3FB70047A56358F2F1E7B0372D546F87331B1774135649CD5E7FF0D4C9472D9C50728BE2BB0C0B97DG91G31B1383772009577D62645E01B3DED516AA4DC632D03FEDD7E6E43D72EC997DF23D7174FCA0B756B427799D8FD466C561E3DD4C2F94DG0EEB30BE3F2D44F9A5649A9C
	572336137227C0BA85A08B2070DAD3C185008140F5AD7A44BFB7ED8B6693F46AB5BAF7822E5E69BC26638E32AD9E9D268DE77970FD2CC907DD2B47B74EBABC7F3BA4A8DF7435FA7DECCD8D31E982642582E48394DD876D815088A0DD07B66D5994341F716BDDC369E707446159556E4327754D506B74788AEDC33977982562E57619DCCB6C938F7899G19G79312602B200FAGC7AC5A673ECDDB0668F3EC2560EB982667C49FBD07E617A89FBE9F3B446A755962EF7E265FE7597E010067250756EA7BAC591B0C
	02B263E3314F3A6998C456884931B886FD5BB1943BD7DE2F68467812FD7806E607E7FC7834F6283FDEF71E6A9CDE3F1B98142FCDD7DE33FDF872ECC1797AF565FDFAF4F876DC6F432735E7182E3C59BEFCC32B5FABC17922F5655D52BB3C7A5D6743272DDF022E3C13FD432B5F5820FCA93A723E6B9F1E3C83C17932F4653DFFFCF876DC6343271D7772EF50631BB9CCF9F3FC78346DD722ABAF6244D046256A9B3463523F8606578E15C1798C3AFA4E9A263C170372D9F565DDB5B83CF6384907CF5B6E033A728E
	1D9CE69C60432715972EAB6F7DD34374EB9FBE2D1F45E8643179756DA6BF73AB5B5E91F0FEBDEE534E2F64DEF7A21419F2834E2F2208676CD9B7607C7A54B06B139134BE25B7FA73B19D6E636B0C1A9B79FE4F696179E8C4D03E7E9B7578DA0561496B8A4A9726ABEF7D2261497BF7D03EE8DDF919BEFCC3EB5357FD7834ED1A20AB6F461C61493B4B07CFABAFC5D75E095CE16EA584654B5215773B8F07D73FC49FBEED7D7263F47782BE9A4A5CD22262E56F1FDCC16F51D4823E910061EE20E743D6C877F35C04
	CE020B00F49C474552B34F94C8E7F15C475F51BBB2B7198A0AEF423EAA99892E8252759CF71A62489A1C1CB5B05C92E373906A208724ECFFF975F29E13714930E5683D270000775A593A6C41EA7FF72027D76B5F01CE29563F837DCC75CA0607B254EFCAE17E7355016FC84F2B563BA37DF7DA1F476999033B271B9CF3D331EC7E907B491DF9D8BD6E6F69E6CBCBA259235DEB95F03D7A648EF29650F9932ECBC33E59A0782ECBF7D96D12530D0598C5337B871B0DAF5A1BDC2285F59E586C5FFEE7ED56375F684D
	7A769B3FB9307DA6EE8EEC3FD9DB7C6B1231C5DF17F9DB74F5495E92D81705DB7C69427AD6943D4B9482F61EF4936FEF6D58AF27703E7520051C1D24C1DA781D29E08DC07D6FB0AF056639A09D86908B104C738EDB34FD0370856B97028E8D1119F6135EB8556BA3E359907A1E3F77C634FD1279758B92B957723967CF8E7D84BC3B3E5E3CCC26E9141C0D784E483B42B75B7C37F74DB67D767EF69BBD57F47638AD2236CD77EEDB4D73147D1786C96CA26AB09A9365174C6E8E5147A78ED3FE16670F5F360D44E7
	6DF62B5DAD5FF8E7F59CE8D757F1030071E147AEE2A73D7D970B5B69202F828898A36E76099149181D856D1BF7B346083F26B1A2E90F621B710E5DB3F4CF5F88E987C0484D68E30F845CB30C38595BE7E2A14D5A747E7769BB89501DC4F6AB065CF4106B792D856B39CBF26E5C40F53F5E2856BD71E6965FE642D308C75D6F5A0D3A2F0EC75D25ADC477C6C8F7820C08C75DE3AB8369BEA95E379F307639F63BFF1F3AE93B3ECF5D365D3FCF5D3DBD38CFBD385D3FCFCD5F9E5C271E5E6EEFACE2B6FF4B2A3679C6
	0ECF3496204F595C4EAE70BAD6DEF90DFF9BD455685BE0CB0DFF9B6C2889EE03BDB57EED303FA6388D8E5568772B73C5D55D77FEAD60787CA90D1B481D6B18F81C8BAF2F75DF6F2BEB756B9DDB6B3F5EB757862FF7C22D7FFA5FD79B3C5E4935015B7EA11B3A7E640E926BCB972C453E14407BD28E3D2738G52C537188ACAEF413E54E7A573C04DADCA1FB15E625DFFBAB569C1D5BACC75BBCAC397FF8B4E0D07BB30BD12EE41FB8BCBBB55FEDB496BF4E727375FEE1471936C6A3AAAFE7E1F236AF23E667862E3
	5E65FCAD6333FA55746BB9FDDC1FFA0CD674B9556FDDCE150C5FFBDCDDCEB527DFF5C22DE7A577499BCF208D52388D8E8FF817DDA91731FAD02D0BD257E78655B23F617873CF2A71D59C3F77149A2F78477534FF50F35FDB50BF9A69BD5132DBCD85F5GAD3722FF9C65E7AE522DCA7B0E16FFC740B396A0D115A75F5BBC7D165A4E9598F7A4694EBBA9B2FF662D3EE3B7ABEF6AE9EFDB61FBF09385353D15F668DC64CD3FC946FF1A632597FF5F6DA5EF7C869E1F642A4BD77CE56E07EA7C865E4E760F301D055B
	78BD37F748B860F20BB6516D962DCD7693E4DE2F3F0D50BFC657FDC5703B18535FEFA36F6CB94C471DBD9274D9CA373D836750D0C8C781A481245D4666D296CFDC5FC76AECF31B3BE01820017C0CA904774ACE642D013491C0828887081B02B26F33935EAEF33F3D2B270BEA48743B70A8512F78F698B3EEC75A2A6FA82D3DDBC33B172EB90DC05746E9D3E9BF5C5D64F258A59250E03BF5AEA6E39B390425FD47100DFDB3F7901B71314FABE6BCA07A5A67A17ECEDFA8480E82C882C83B1DFD8F0159212A9DE996
	G3E708ED3C199008140F1876A3B1A2E63D0A7A2165E886332FB4D9F2872B57746E41AEBAC6FE94A6007603C1CB67ADE28EFB98AA53B93D868F5A7B0748ED61F58BB704E6024B63D7CC41E7F5FD66DFB85A4BF0567D76B66E75E01F159D9FC2C906E4431E2991DCB2AA15D8850F9A70E95D1FC2C98F127EFBF8E87DC34864F6E236C277778275C496E2364F2FC8B45A729706C1ECA3A053F0F81F9B589B84FBCEF45B5D894472D247ACF03F4FA827613CB4A7D5CD560DE4A6A7E229177DC97A4E05D27B7905995F7
	41DA8E206DAE2C7B3D3B3563DA7FDDCADDC361F764DDFAF54F20B1EE5CDD2C0ECBB9BE1B6213D4F8D6779394CF5269C4073B311E77D5E25DE3G477A63E67EEE41423BD14FC9EB35FA965E2368D5823FAB6F5153331736F97DBDDABD4F227836FB34FA660B28E7BF64255E03FACE26EDD413E8AAE8CEC4DCBA45B9A0BD0263565135CCB824E3B82E0F6292C81A636E2638ACC867CFC5DC218DEDD14CF199144E81691029E89F0946A5C46FC99C77408944C5CFC51BEDF5204D6227B25BA4CDD5EC14863F33276A59
	AC114E297977EAED16CC71A577EAEDE632214D2AA1AF62DE547D97A26E8D8A77A16EE28B412D02749A0EFB5302752E6038F9D65C9B9C41F1C7A8DDB824E3B86E8FD6ACAF01636AC4ACAF89F0E55388EE44C7E40F2998FE3B26116F11A5B488FCCC3B4AD6A25077F5CA788569BAFB1758CD46355275D595E5A5DFD7D42CE57795A37B5477952B5FBACB77DDFB82F15318ED27494F50B7A74B5F903351F7CD880FF03F29207AFE6C5F4F17FF246E5FC4114D1BF7B8497B6886B6E9307EFDADEFFF577D583FD3E91951
	10CEGC8399F5F49697F973ED797F365E5C272EC7DF797530CA7F3326E175F49F9C039DF993336BD1749CC6AC4190D10BF7A8102FB3DD3BD8693DB4E49E37B4DAC3F4C2E4EA773530D02BA7F08D7BE51779F0B5479E1DE7944C677487C2CEE8FFE06F5DB78E02C6038A9446F775C1265BAE5E35D9E1E3C28652246A54C975276A83E706EF781FCE1C38578C2B57882634B5D2B704DEC8A64C39B2A4ACAB6829FEB7FBF35E17B17BDE8AA30BE086D3F3E4DEBFCE76D7F1D5BEE5DE16EE08E405AE21B995BA262C1EC
	7FE3745D4399104E8448CB42F970D0BB5296A7E13B0DE957365B03DE6DF2DB3B365D6E49D1674FED5736DBD72EBA7F930B365DAE12736577F3EDFE5E4F25418853FBA7BF33B3723ABC425F458C653868D61C6793B94E49F18B1378F71E78DD234A07188F54BF240CFDAD705B75105ED8F8124E87A18FE96FF70F2678C98FE96FF73F4D5F2B0B013C2207F1CC3A0D0F0592470571F9FFB4479D3060381772B06A5ECC5F8B4C6469F61AA66FD33374CBBEFB224385A2C30BE3B2F22DBF158C3DF39096677E450AF12E
	700829E095C0A50091C0FA84635CD21B728E88F4ADF2D9CD40B5C92D8DFDBD7C91344DBCA336FB72A33EF5CB875C820042E4F9DC00A7EB5FEFF713F62CCC56367BBE0A2FCF5636FBCA8336FB8B64A5A4E3FB4E5C0DE7E1EBFE0F380386EC77FA0E1BCEF19224C3FF4FF527B1D3A42463G12B87EF50A4F78BD7B1683F95F1161C7EC5136916153EB03A495BE528F0D3E9FCC76112137370E74015ADC8F9A27533D5F4ABAB91DBF5DD7B70C29974B4FE22029GF88F5BE9BAEB9B24FBD758475E99F577084C565751
	FD26C9C097B39D6DDF7EA536C9B2473566516F68BD8AF37423E863C15A3F8D1036828CF2BC9B8F42A19D77A87BB686432F9083759BD2C6A0FB8C7A49B7BC2ABE137CBC208CFF6D2357CEFA7D1459B0166E4B3A4C76EE090E529E394FCD33E21F1B71A87651B7A84E5E5DAB3AA5514A685E91B1264F60F4F562DBD82651EEB96641FE5C42E997F05A71A2B9F76AB29FF53A89ED134BEC01B90351DF456973E7A07D13A2515753EDF6991D2CF0C6BB1F532E86DA66EF1F5A505FCAE7687B1B5A6736923E994C5F086D
	2CB3347E76A6156DF45B45EE09FD4B01497E002FEBDCDC4F319D042F02734EBA01E96603CD8E6A2BC09FB7837DF23B0867F853B86EF63EF6486438D7A9EE75E350BF9EC3FFFC00466AF510368204F07C63949F75987BAE8979668843375808EF913A7FF55C2FD315E1FDCC6D3FBDD406BFBFBC93D00FB368774C76D7D31BA5025C6447507657DB70BDE7964FED7C9E127298743E35AE089C9CFF68BA954AA4701E6C87C50FABBEA137BD15DF8C785247D1FE16D5FFBFEA940D810CC05776B82ED7CCBFA83A9D9203
	6936B400EE210FEB7DF5A2DD9BF577B89C723704C5759A27E24060771D2CC90F33F60CFEDCD96330B29EB924AE2368E2C74EA2BFE524483C690FAB6B8EF6AFE0ECA3755FA7A08EF8824FC3966FA65F60B3190F7A5E8AGE4235B6902C167B843522FDB49BE6698521DEE6D3838D2B8B1CC3E9B4F180F45970D747B8246A714B6E2789FA93ECD05E7F147F3FCAD69003C64A7703D13A97BC8ACD51982E395C0A7408800C800A9A9E84B6FF713F1A6607DFF2353E590FBC5076A3A812B5F6F035EA15782730B3B1662
	1AAD6E562B0578F36A7CFE67A5B90579C3167C0CCF1A3C08BCD97DFEE8A275ABF952D43095208D209FA094A06AC92C5F445D72FBFF449DEAA549EC6924DF516197D16CE21F02A69B4F2CEEEF783C37B634B6DF96743DB1FD3E9DE74C47FC62D93A660BFFD26BAB99941F782456D77EEBC6DFC9063C4A27F00CEF8B7A9E183E5E753AE7A2131EE26D9573143F35448CDD7BA64874B37C72DD530AFC99BAB48B81D774B44459CFEB6FBFCB1C2F7AE9DFBE83605AGA4D59E1BBFFFC5674F51000FFA9A6754B68B4673
	719C7794DF4725F21C090E777929260212D4347359EC8D8869FA0E9BCFF1ED296C9BBC8C77DE40F8EEA800B27C37DD797FAC8367C03DBCB60F25533A0DGBDC2D3B1563A0A4F2B71BA7A611C041084B4E929EC6E227B68295AF97594ED8FE1A62C61E6623C7AC099B9E721EFA32E473343CFC55CBB281E097BA007C46DBE48CC2F7D27F122F69F0448D5726F94357BA0CB3D78670B5AFD9064E7FAB65B4877E655AF5CFB3DEACF87627226066F3FB36CEAED5AE55835535870437732EFAEBE00F121EBA67A5C2681
	7443900EF3D1DCAC2413B86E36810C8B53B82E2B016032099D1FC1BFB45035EB8924ABB96EA0451177287A79BB1B4FB48472A5424F7D7219B30FA3190C57824A500B7B4E8458D95C12BD387FD07F8CBB0BEB0327748CFBEFAC7699C253EDA811E9C2F56A92A963D87B3EE8500F6F966DA5B6DCG34F94F224F966DD9CAFD162C2D702CF17B9EFA86490F56909F7D9ED98B110F6630975E58F76D5839583A2D5AF3B522FB453338B776F6957603BA40FDC24B9368F7B0583F2CFF7B62208F5198F45D81C140F9FCD6
	A14E63453F3DDC28FB22AE67D3C1FF9E9FFCD6BEFBF8D6096F6A0D154BD8DD6EDD6BFBA6BA133F371717465A21981E95E9EC2F030DA33B3E44F124BECD7F6CDD9D1F37G0DABCDD9330524E9E3605EBC7D369ACF77G4200BEA28D5B6A3D26E53C2D765A3BF9999507D5F8F3BF47FF3C021DA55346A24DA61F115EA6EA5B0A54EFDA9A1EF14E77B9C3A57929DC0FE72D240EB63343231CB12F95F12F0325BD1A749DA22933F29FA01F6F0DB07FB05A50BF3252747C03FDBBDB6BB34CBFE248E7BF64F24B69B7D455
	E31BDC7FF2532E7D46061D3586793E4B817FF78C86695A37F4960CBFG4CB79C7C3D7236D94CB77A6199BA0B5D830F1FC5E8E650739CFFFDBDEA163A0FCE9F50EF77CF68990B709C0C074F213D5D0769993B4F69B916D5E8A079354A37F3787BB2F2FB51FD73BA2F6F60B092565E4BBA346DCD6AD47D9C2E819FB92858F7020534035F730675E5A59B2A566A34957665F1BFAA3FE37F23145BB424F2995F230794BE0FF568FC2F3690BE9647DFFE18781F61B90C59A77FC03EF363EFFDC75EFDE75F7AC37E8B0E10
	32643B97BFFC660F97227D5D20578F6DC7E1EEB51ABBB8CFD8A331B53F2F41160C78AD72C6529E609A125D6C301F20DBB9CCBA1B13CC1DC4367CFE0F6AF7C647CF35D76EFFBEDC5479F34AE0751F3A4F7F5D4324FD7AF78F276F533F3B1E32AF70BD7934FD7E3F457E42BE753758D93BC7527B9FED60B703G11GF1G53389F756CA7FE443E2421DD776D32F6103B0F64AF43C8A83F3949FFDDDB1B027FBD194EA63217596E045E5CC54A675FE4EDA26DA5B9DD9449F09EBAFF386D9D1D92A31571471B68D93FE8
	23E8791DE43362D773D43F7B4CBF3F0F057D2CB264BDF6FEFE1F7E9C462289FCBD1545F1F35A102E668F08B318114E40F1092D9897C7F05C7BEDC8974BF163DBB186965291D74FE4C03A0C63467272EAB8EEBDAF4F48F11735A2EF94479DEEC3997169B8075CE473BF5FBDCC675EA420CDC94779EE0C0547C23365D8881E9533D97C4DE2527956921926FE36EFAC54B2DBBD5FF5097A735DC83ABEF2816D60EC5475DFEDCACC3AC1667F8D7D9B872A1B7E6B595C1560934B907D22E7635C35117AAEABE3A455575F
	99FBE5C5D949FAF246CE631194FE8EBCE576D063D50CC7CC6DA445FE5FEED1645FE589A43F6A9BA61F7501D22FEFF7E4038E794F87DE43B0FBD65175D9B15016BE0FF1497783A581780C4F2B5BE954G7DFE19339B06499EE70F071D7F3332A387709C2173F93C7F73924575C3BA64F97E1D50867DF3080886BC3309653453695FE7E2AB89DCE7F4EFD0F09B64BD2472BD3807148E3C45AF603A22F78F16E9FC816FA4C4D15AD0C8C781CC6134938D5826B3DEC0798F53F89046C9F9FDF134C83D3EC8BDEE4A4175
	3756EE05994CEE55994A7AE285EF774FC5255D8FEE899023D4D7C233D7D52C15778B2CDBD1C7D7862EF3D7D529FDA19A70D3B2827B821B2B5E2367CB49C01B89D074A2788440D6GA3C0A740A0C098C0B4C082408C008C0005G05AF198ACAGAADF42394DFD00FC9359DF0CA4DF9821931C1B1C2D89387F37788BEC3BFAA8EB44CB589EFB3F40360BFF895BEE0052E6C1BAFF8E481F435B79CB3E0FB267CC7B637C8E25BF2E32AB6DB2BBD06C3869EB889DAB2A31DD7E4877F1DCDC27D20E0B1E03ED75235737E5
	52G1FBDA7F0DBB13DB69DD5745AA8167857EBC365C6B6CEB05B060BE85B7CCC18ABB2D137D491ED5B1F0936FD0B1E3545C2FA9AC0DAA6760179B6D207A66867A418147F360B286E87451F54786D878BB2D97D564C556B873FBE262A17AD003DE93D3E667D2026836F12B64FC57F3B6AB0D933FB64B81C6122DB7D47CF5735860F1F62697CA9C25CE3BC6E125FA3393D1548F15AEC9E1147E9F73796F1DC032751693113010D5571EE3294134F481E607B0CE1F3831F699208829A82F8C696BFD7599FE0E54F6ED2
	4A3618316CC4BFE56786A8BBE52EF68FF3AE2DFBBA603366627E463AB614D1BCCFFFFFC3AD27E21EA2036E6D4C534A38194AE884FC4BBC7E8D4EB67D3B73FDB4EE998DF46173F0FE09237C8910CE1A07BA56F39D977851D18D05AFFB6BD87232D64787290CB240D73F0CF24F213AD8A16DFA99E309FC27624B87824E65DFEFE203BA6B2357DB300FC6C0D9B3DE46BE7AE78B765142AC6C231F53BB5CF510EE8110321076EFFC2C894DC2BB6E23777AD96C0F7EF25025681899E8FC2B2A721AF7DE6365A7E661D8B6
	536B1CE5A1600B66879E4BD87C79EFDA569A20A57B526A3B31DB81C7763A192E275D0A2EE583816D893151060DD5EB996D3181D26F1DA27966A8725FBC407713E85917F914325B9B824D3FDF3322E55A9B9B48DA5EFB7F88EF3973AD245626C6C303D1605F2B59B34C7337E1728D1CB19FBB1FDBEDA03E55BCDFFBEE778F0A374E571E5BDD4DE3AE97644D180FFEF109C1AD3F7A0F308E7A63D06211D192E963B6209D8408840887C882C887D8GD0748A0E75B956C0676D3CC9D451067C2D1B2DFA36A9FB0569D7
	730A2F1E4CBF3F2CC21FDD44F195D56833844774DAEB73BF5FD458024FB75F5874576B7F36792E576BEC7A6B755DB675FA1D61FE20F3F88744E04D1C4E48F1BFF25C0F34CC02BBE8375279DE5EEB20FA91FC3DC884A2DE24FA75D1DC330CBB2A427DA8631C749DAC28F227530D3C925D6335599D8E166E27B476EE18C239BDCED29D6DAE33CA6F33B86EC76E7385DF8C2F2F7CED18FC7B4F180F752517E97CEAFCC55B4796D0FC5BAB5ABE768B9E9BBBA0AF79956CE383DF2A65D7E6433C149D380F4967D5BE5FFC
	A450A2733AF44AE0FC6BF479C2E47AC9BAFC72B91F4D375F7458547D0615FF31EE79416B93BDCC3ED9E74C475A69940D11E332356DB70A626333356DD7E44376CB043C52BF61BD1879BDA5708C05F4B4C092C0BA40820022D7CD85EBG363E0AE35D4051D2417FD847DE44D086BAD63FDD7C1ECF732BCA5BB8645FA360998E902D4AE373FCBE1F67273C2AFF4E42664FAB7A844E93EEC61E6CD7B1961A562F504EB9CEFE33793320ADD00CCF5EE35848E30EA37CDEFD59EB7AEBBAD67E0A81C5D66F20A2EB5B50E4
	D13E49A7953E0FDA074EB7651442772FB3285BF1DEB7EB403ABDFBDA299B3D2AA64B5AFF86F57BAC67CC6B46FC68E61F6F6E8E2DEFCD9DA65FDF4F180F753DCBE8CC9C711A36CFC6D1FC74EB5ABE394E0AFDB28E72D6AF40B175E32FB1E19070E18B06B226AE549DCB22E45EF81DB258B818ED759D9B7FECD50F0D6C2C587CAF222FFC5EC327E579FB337D4B95BCCE4B32CE9F9B847E7BF3556F58BDBF7F7E7C7DBF2B8C79DBDE86345DB63E0638590036FBDA40D861FC49FFBCB6D98A9E0FDDA66947E35112EFBC
	F60D249F0F5DA87946E37134DC3758A13F514C7F162FC45A145DBB58EB6EB6F75033A5D63F91F46FAF897CA9F5816E654F5E0AFB7E995C0F7F7405525EFFD57D9E75454F5F7611BF2B8CD667733F4436FF17377DC5DFE25B1F47F1ABE81CD17DE7080D7E0C7672707B68C60EE3EBCFC29745F1D770F7B5E37F0CF68D31A9369C277A7D3E6D67376BD7BF2B8C79BBDDBD7E7BCAEEEF703ED2582B5FD75E69756DAB9F746A77153F777A7615FF4A653A79472E983E3457FFDFE96940775A045731CDE3E81B2E02749A
	0EDB497D21026386A90E3E634BF1CF52F1193C639B7EBA7A43F22FF1BF9A70D3DE9F4AFCB1596BDEEDB270248F094F62F5BFF58170943EB1943ED3DEFCA540D3B9A43ECB2D0A0FDF2D7A3D71A7FB674E3FEC434FAA0335E91568BB07EC667BD0DD1C6EDD6A8F75E03366B7509F7A395FD85F407D360B36FA3F37B11A20653D5905DBF1CFB2CE4776981F605D308A553BE384BF6D8D6D3D304FEC5E32FE1B2748BA173FB7B4C3C716F60F163CB7C460EFB2BE638D6D9E73430B15322770F7BC327D141D2BD376BF64
	727297EA77D6C752FE3A8A70A58B510E77085EF51B3FC411DFEA8B760E099A346F1898FC64DFC15B319970560538374B56C0E1100ED8086D3DC6740D55560A6AD8CD7EF62859176EA633AF5D09B6DF3A53ED3EF4976A50DD6CC547EC7605469FD665ABB61B40EDB6EDE170B3872D6F252AEC4656AE6B36615AA5E3A12E77B651E3A116FF9B3F2BD772974C7F4FDBCA7EDC3E46EC6679A78B846EEF11A45C156BF529B8747BDB77ACD74AF8FE05D24643E7F087ECD9114237F2E832E91D92F81D43F81D4A481118BC
	1EED58269E032781CD6ADF06B286A7F81D97E681CF5EDF074277C72F39E9B570148D09EF2A173C9A60B18661E3BE59304D57CF3F5F666B4FCF686C87255AFC691669502D5021CB55211B65C57781ED4B33FBCFC223464C9596FB5C42BB3E6DD9BB7E49A7E2DA6BD66588B98D4212CE3A18EA9096771155D63F8B565EE4BA1A68792F40567BA52491B1C5BF95FDD2928AEB2D56923CC0A989EF339B159C83025F66029B68AEE7DA6F464D39FF38BCB7C7488329ACF2EA9072C87883425DCC09DC081C088AC21E9A28
	70A53AD8720F0C664707CCB3D48E9284E48527D9144BD393810CC1B812818CDEB2DE61A9F24A7C0AA4ACC7A31610GD0925E65E965522BDA662282AF8DB671549C48DB2ECE8926G5A795779E36EGC3218914BAA3FEC948A3973561D1D1DED1A1890B573327A08979BC9EBF537A7CFF72A2017FED3D536AF7B0970C34787DD6CDE44EC1BA764449CF659D4CCB0E13BE1E8578425C0372BE691C7EF5027FFB36CDDDE607C3B2BB0E55F6DB491D5B9D47A5D1396799F91C146FC19AB9D33ED7C03FCDF8D445737FG
	D0CB87886F8B554F6EADGG4C14GGD0CB818294G94G88G88G750171B46F8B554F6EADGG4C14GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA8AEGGGG
**end of data**/
}
}