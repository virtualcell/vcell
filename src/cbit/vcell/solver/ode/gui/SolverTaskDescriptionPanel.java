package cbit.vcell.solver.ode.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.*;
import cbit.vcell.math.Constant;
import java.awt.Color;
import java.util.*;
import cbit.vcell.solver.*;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:36 PM)
 * @author: John Wagner
 */
public class SolverTaskDescriptionPanel extends javax.swing.JPanel {
	private final static String SELECT_PARAMETER = "select parameter";
	private javax.swing.JComboBox ivjConstantChoice = null;
	private javax.swing.JLabel ivjEndTimeLabel = null;
	private javax.swing.JTextField ivjEndTimeTextField = null;
	private javax.swing.JLabel ivjTimeStepLabel = null;
	private javax.swing.JTextField ivjTimeStepTextField = null;
	private java.awt.Panel ivjPanel2 = null;
	private java.awt.Panel ivjPlotSpecificationPanel = null;
	private javax.swing.JCheckBox ivjPerformSensitivityAnalysisCheckbox = null;
	private int fieldODESolverIndex = 0;
	private javax.swing.JLabel ivjStartTimeLabel = null;
	private javax.swing.JTextField ivjStartTimeTextField = null;
	private cbit.vcell.solver.SolverTaskDescription fieldSolverTaskDescription = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabelStartUnits = null;
	private javax.swing.JPanel ivjJPanelKeepEvery = null;
	private javax.swing.JTextField ivjJTextFieldKeepEvery = null;
	private javax.swing.JLabel ivjJLabelKeepEvery = null;
	private javax.swing.JLabel ivjJLabelTimeSamples = null;
	private TimeBounds ivjtimeBounds1 = null;
	private TimeBounds ivjTimeBoundsFactory = null;
	private TimeStep ivjtimeStep1 = null;
	private TimeStep ivjTimeStepFactory = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private SolverTaskDescription ivjTornOffSolverTaskDescription = null;
	private javax.swing.JLabel ivjJLabelTitle = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SolverTaskDescriptionPanel.this.getConstantChoice()) 
				connEtoM15(e);
		};
		public void focusGained(java.awt.event.FocusEvent e) {};
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.getSource() == SolverTaskDescriptionPanel.this.getStartTimeTextField()) 
				connEtoM6(e);
			if (e.getSource() == SolverTaskDescriptionPanel.this.getTimeStepTextField()) 
				connEtoM2(e);
			if (e.getSource() == SolverTaskDescriptionPanel.this.getEndTimeTextField()) 
				connEtoM7(e);
			if (e.getSource() == SolverTaskDescriptionPanel.this.getJTextFieldKeepEvery()) 
				connEtoC9(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == SolverTaskDescriptionPanel.this.getPerformSensitivityAnalysisCheckbox()) 
				connEtoC1(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SolverTaskDescriptionPanel.this.getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("timeBounds"))) 
				connEtoC2(evt);
			if (evt.getSource() == SolverTaskDescriptionPanel.this.getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("timeBounds"))) 
				connEtoM11(evt);
			if (evt.getSource() == SolverTaskDescriptionPanel.this.getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("timeStep"))) 
				connEtoM13(evt);
			if (evt.getSource() == SolverTaskDescriptionPanel.this.getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("sensitivityParameter"))) 
				connEtoC3(evt);
			if (evt.getSource() == SolverTaskDescriptionPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) 
				connEtoM17(evt);
			if (evt.getSource() == SolverTaskDescriptionPanel.this.getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("solverDescription"))) 
				connEtoC5(evt);
			if (evt.getSource() == SolverTaskDescriptionPanel.this.getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("simulation"))) 
				connEtoC6(evt);
			if (evt.getSource() == SolverTaskDescriptionPanel.this.getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("outputTimeSpec"))) 
				connEtoC8(evt);
			if (evt.getSource() == SolverTaskDescriptionPanel.this.getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("solverDescription"))) 
				connEtoC10(evt);
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public SolverTaskDescriptionPanel() {
	super();
	initialize();
}


/**
 * connEtoC1:  (PerformSensitivityAnalysisCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> ODESolverTaskDescriptionPanel.performSensitivityAnalysisCheckbox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.performSensitivityAnalysisCheckbox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC10:  (TornOffSolverTaskDescription.solverDescription --> SolverTaskDescriptionPanel.updateTimeStepDisplay()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateTimeStepDisplay();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (TornOffSolverTaskDescription.this --> SolverTaskDescriptionPanel.updateTimeStepDisplay()V)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(cbit.vcell.solver.SolverTaskDescription value) {
	try {
		// user code begin {1}
		// user code end
		this.updateTimeStepDisplay();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (SolverTaskDescriptionPanel.initialize() --> SolverTaskDescriptionPanel.makeBold()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12() {
	try {
		// user code begin {1}
		// user code end
		this.makeBold();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (TornOffSolverTaskDescription.timeBounds --> SolverTaskDescriptionPanel.updateSensitivityParameterDisplay(Lcbit.vcell.math.Constant;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSolverTaskDescription() != null)) {
			this.updateSensitivityParameterDisplay(getTornOffSolverTaskDescription().getSensitivityParameter());
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
 * connEtoC3:  (TornOffSolverTaskDescription.sensitivityParameter --> SolverTaskDescriptionPanel.updateSensitivityParameterDisplay(Lcbit.vcell.math.Constant;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSensitivityParameterDisplay(getTornOffSolverTaskDescription().getSensitivityParameter());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  ( (SolverTaskDescriptionPanel,solverTaskDescription --> TornOffSolverTaskDescription,this).normalResult --> SolverTaskDescriptionPanel.updateConstantChoiceComboBox()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.updateConstantChoiceComboBox();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (TornOffSolverTaskDescription.solverDescription --> SolverTaskDescriptionPanel.updateConstantChoiceComboBox()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateConstantChoiceComboBox();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (TornOffSolverTaskDescription.simulation --> SolverTaskDescriptionPanel.updateConstantChoiceComboBox()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateConstantChoiceComboBox();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (TornOffSolverTaskDescription.this --> SolverTaskDescriptionPanel.setKeepEvery(Lcbit.vcell.solver.SolverTaskDescription;)V)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(cbit.vcell.solver.SolverTaskDescription value) {
	try {
		// user code begin {1}
		// user code end
		this.setKeepEvery(getTornOffSolverTaskDescription());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (TornOffSolverTaskDescription.outputTimeSpec --> SolverTaskDescriptionPanel.setKeepEvery(Lcbit.vcell.solver.SolverTaskDescription;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSolverTaskDescription() != null)) {
			this.setKeepEvery(getTornOffSolverTaskDescription());
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
 * connEtoC9:  (JTextFieldKeepEvery.focus.focusLost(java.awt.event.FocusEvent) --> SolverTaskDescriptionPanel.updateKeepEvery()V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateKeepEvery();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (timeStep1.this --> TimeStepTextField.text)
 * @param value cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(cbit.vcell.solver.TimeStep value) {
	try {
		// user code begin {1}
		// user code end
		if ((gettimeStep1() != null)) {
			getTimeStepTextField().setText(String.valueOf(gettimeStep1().getDefaultTimeStep()));
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
 * connEtoM10:  (TornOffSolverTaskDescription.this --> timeBounds1.this)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(cbit.vcell.solver.SolverTaskDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSolverTaskDescription() != null)) {
			settimeBounds1(getTornOffSolverTaskDescription().getTimeBounds());
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
 * connEtoM11:  (TornOffSolverTaskDescription.timeBounds --> timeBounds1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		settimeBounds1(getTornOffSolverTaskDescription().getTimeBounds());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM12:  (TornOffSolverTaskDescription.this --> timeStep1.this)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(cbit.vcell.solver.SolverTaskDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSolverTaskDescription() != null)) {
			settimeStep1(getTornOffSolverTaskDescription().getTimeStep());
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
 * connEtoM13:  (TornOffSolverTaskDescription.timeStep --> timeStep1.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		settimeStep1(getTornOffSolverTaskDescription().getTimeStep());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM15:  (ConstantChoice.action.actionPerformed(java.awt.event.ActionEvent) --> TornOffSolverTaskDescription.sensitivityParameter)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTornOffSolverTaskDescription().setSensitivityParameter(this.getSelectedSensitivityParameter((String)getConstantChoice().getSelectedItem(), getPerformSensitivityAnalysisCheckbox().isSelected()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM17:  (SolverTaskDescriptionPanel.solverTaskDescription --> TornOffSolverTaskDescription.this)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM17(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		setTornOffSolverTaskDescription(this.getSolverTaskDescription());
		connEtoC4();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (TimeStepTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffSolverTaskDescription.timeStep)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		getTornOffSolverTaskDescription().setTimeStep(localValue = new cbit.vcell.solver.TimeStep(gettimeStep1().getMinimumTimeStep(), new Double(getTimeStepTextField().getText()).doubleValue(), gettimeStep1().getMaximumTimeStep()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setTimeStepFactory(localValue);
}


/**
 * connEtoM3:  (timeBounds1.this --> StartTimeTextField.text)
 * @param value cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(cbit.vcell.solver.TimeBounds value) {
	try {
		// user code begin {1}
		// user code end
		if ((gettimeBounds1() != null)) {
			getStartTimeTextField().setText(String.valueOf(gettimeBounds1().getStartingTime()));
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
 * connEtoM5:  (timeBounds1.this --> EndTimeTextField.text)
 * @param value cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(cbit.vcell.solver.TimeBounds value) {
	try {
		// user code begin {1}
		// user code end
		if ((gettimeBounds1() != null)) {
			getEndTimeTextField().setText(String.valueOf(gettimeBounds1().getEndingTime()));
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
 * connEtoM6:  (StartTimeTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffSolverTaskDescription.timeBounds)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.TimeBounds localValue = null;
	try {
		// user code begin {1}
		// user code end
		getTornOffSolverTaskDescription().setTimeBounds(localValue = new cbit.vcell.solver.TimeBounds(new Double(getStartTimeTextField().getText()).doubleValue(), new Double(getEndTimeTextField().getText()).doubleValue()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setTimeBoundsFactory(localValue);
}


/**
 * connEtoM7:  (EndTimeTextField.focus.focusLost(java.awt.event.FocusEvent) --> TornOffSolverTaskDescription.timeBounds)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.awt.event.FocusEvent arg1) {
	cbit.vcell.solver.TimeBounds localValue = null;
	try {
		// user code begin {1}
		// user code end
		getTornOffSolverTaskDescription().setTimeBounds(localValue = new cbit.vcell.solver.TimeBounds(new Double(getStartTimeTextField().getText()).doubleValue(), new Double(getEndTimeTextField().getText()).doubleValue()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
	setTimeBoundsFactory(localValue);
}

/**
 * Disable the time step in solver task description panel.
 * Creation date: (10/4/2006 5:19:25 PM)
 */
public void disableTimeStep() 
{
	getTimeStepLabel().setEnabled(false);
	getTimeStepTextField().setBackground(new java.awt.Color(220,220,220));
	getTimeStepTextField().setEnabled(false);
	getJLabel2().setEnabled(false);
}

/**
 * Enable the time step in solver task description panel.
 */
public void enableTimeStep()
{
	getTimeStepLabel().setEnabled(true);
	getTimeStepTextField().setBackground(Color.white);
	getTimeStepTextField().setEnabled(true);
	getJLabel2().setEnabled(true);
}

/**
 * Return the ConstantChoice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getConstantChoice() {
	if (ivjConstantChoice == null) {
		try {
			ivjConstantChoice = new javax.swing.JComboBox();
			ivjConstantChoice.setName("ConstantChoice");
			ivjConstantChoice.setBackground(java.awt.Color.white);
			ivjConstantChoice.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConstantChoice;
}

/**
 * Return the EndTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEndTimeLabel() {
	if (ivjEndTimeLabel == null) {
		try {
			ivjEndTimeLabel = new javax.swing.JLabel();
			ivjEndTimeLabel.setName("EndTimeLabel");
			ivjEndTimeLabel.setText("End Time");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndTimeLabel;
}


/**
 * Return the EndTimeTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getEndTimeTextField() {
	if (ivjEndTimeTextField == null) {
		try {
			ivjEndTimeTextField = new javax.swing.JTextField();
			ivjEndTimeTextField.setName("EndTimeTextField");
			ivjEndTimeTextField.setText("");
			ivjEndTimeTextField.setBackground(java.awt.Color.white);
			ivjEndTimeTextField.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndTimeTextField;
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
			ivjJLabel2.setText("seconds");
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
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("seconds");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKeepEvery() {
	if (ivjJLabelKeepEvery == null) {
		try {
			ivjJLabelKeepEvery = new javax.swing.JLabel();
			ivjJLabelKeepEvery.setName("JLabelKeepEvery");
			ivjJLabelKeepEvery.setText("Keep every");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKeepEvery;
}

/**
 * Return the JLabelStartUnits property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartUnits() {
	if (ivjJLabelStartUnits == null) {
		try {
			ivjJLabelStartUnits = new javax.swing.JLabel();
			ivjJLabelStartUnits.setName("JLabelStartUnits");
			ivjJLabelStartUnits.setText("seconds");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartUnits;
}


/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTimeSamples() {
	if (ivjJLabelTimeSamples == null) {
		try {
			ivjJLabelTimeSamples = new javax.swing.JLabel();
			ivjJLabelTimeSamples.setName("JLabelTimeSamples");
			ivjJLabelTimeSamples.setText("time samples");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTimeSamples;
}

/**
 * Return the JLabelTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			cbit.gui.EmptyBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.EmptyBorderBean();
			ivjLocalBorder.setInsets(new java.awt.Insets(10, 0, 10, 0));
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setBorder(ivjLocalBorder);
			ivjJLabelTitle.setText("Specify time conditions and optional sensitivity analysis:");
			ivjJLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
}

/**
 * Return the JPanelKeepEvery property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelKeepEvery() {
	if (ivjJPanelKeepEvery == null) {
		try {
			ivjJPanelKeepEvery = new javax.swing.JPanel();
			ivjJPanelKeepEvery.setName("JPanelKeepEvery");
			ivjJPanelKeepEvery.setLayout(new java.awt.FlowLayout());
			getJPanelKeepEvery().add(getJLabelKeepEvery(), getJLabelKeepEvery().getName());
			getJPanelKeepEvery().add(getJTextFieldKeepEvery(), getJTextFieldKeepEvery().getName());
			getJPanelKeepEvery().add(getJLabelTimeSamples(), getJLabelTimeSamples().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelKeepEvery;
}

/**
 * Return the JTextFieldKeepEvery property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldKeepEvery() {
	if (ivjJTextFieldKeepEvery == null) {
		try {
			ivjJTextFieldKeepEvery = new javax.swing.JTextField();
			ivjJTextFieldKeepEvery.setName("JTextFieldKeepEvery");
			ivjJTextFieldKeepEvery.setColumns(4);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldKeepEvery;
}

/**
 * Gets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @return The mathDescription property value.
 * @see #setMathDescription
 */
private cbit.vcell.math.MathDescription getMathDescription() {
	if (getSolverTaskDescription() == null) return (null);
	if (getSolverTaskDescription().getSimulation() == null) return (null);
	return (getSolverTaskDescription().getSimulation().getMathDescription());
}


/**
 */
private int getODESolverIndex () {
	return (fieldODESolverIndex);
}

/**
 * Return the Panel2 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getPanel2() {
	if (ivjPanel2 == null) {
		try {
			ivjPanel2 = new java.awt.Panel();
			ivjPanel2.setName("Panel2");
			ivjPanel2.setLayout(new java.awt.GridBagLayout());
			ivjPanel2.setBackground(java.awt.Color.gray);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPanel2;
}

/**
 * Return the PerformSensitivityAnalysisCheckbox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPerformSensitivityAnalysisCheckbox() {
	if (ivjPerformSensitivityAnalysisCheckbox == null) {
		try {
			ivjPerformSensitivityAnalysisCheckbox = new javax.swing.JCheckBox();
			ivjPerformSensitivityAnalysisCheckbox.setName("PerformSensitivityAnalysisCheckbox");
			ivjPerformSensitivityAnalysisCheckbox.setText("Perform sensitivity analysis on parameter:");
			ivjPerformSensitivityAnalysisCheckbox.setContentAreaFilled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPerformSensitivityAnalysisCheckbox;
}

/**
 * Return the Panel4 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.Panel getPlotSpecificationPanel() {
	if (ivjPlotSpecificationPanel == null) {
		try {
			ivjPlotSpecificationPanel = new java.awt.Panel();
			ivjPlotSpecificationPanel.setName("PlotSpecificationPanel");
			ivjPlotSpecificationPanel.setLayout(new java.awt.GridBagLayout());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotSpecificationPanel;
}

/**
 * Comment
 */
private cbit.vcell.math.Constant getSelectedSensitivityParameter(String constantName, boolean bPerformSensAnal) {
	if (getSolverTaskDescription()!=null &&
		getSolverTaskDescription().getSimulation()!=null &&
		getSolverTaskDescription().getSimulation().getMathDescription()!=null){
		
		if (constantName != null && bPerformSensAnal){
			Enumeration enum1 = getSolverTaskDescription().getSimulation().getMathDescription().getConstants();
			while (enum1.hasMoreElements()){
				Constant constant = (Constant)enum1.nextElement();
				if (constant.getName().equals(constantName)){
					return constant;
				}
			}
			if (constantName.equals(SELECT_PARAMETER)){
				return null;
			}else{
				throw new RuntimeException("parameter "+constantName+" not found");
			}
		}else{
			return null;
		}
	}else{
		return null;
	}
}


/**
 * Gets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @return The solverTaskDescription property value.
 * @see #setSolverTaskDescription
 */
public cbit.vcell.solver.SolverTaskDescription getSolverTaskDescription() {
	return fieldSolverTaskDescription;
}


/**
 * Return the StartTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartTimeLabel() {
	if (ivjStartTimeLabel == null) {
		try {
			ivjStartTimeLabel = new javax.swing.JLabel();
			ivjStartTimeLabel.setName("StartTimeLabel");
			ivjStartTimeLabel.setText("Start Time");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartTimeLabel;
}


/**
 * Return the StartTimeTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStartTimeTextField() {
	if (ivjStartTimeTextField == null) {
		try {
			ivjStartTimeTextField = new javax.swing.JTextField();
			ivjStartTimeTextField.setName("StartTimeTextField");
			ivjStartTimeTextField.setText("");
			ivjStartTimeTextField.setBackground(java.awt.Color.white);
			ivjStartTimeTextField.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartTimeTextField;
}


/**
 * Return the timeBounds1 property value.
 * @return cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.TimeBounds gettimeBounds1() {
	// user code begin {1}
	// user code end
	return ivjtimeBounds1;
}

/**
 * Return the TimeBoundsFactory property value.
 * @return cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.TimeBounds getTimeBoundsFactory() {
	// user code begin {1}
	// user code end
	return ivjTimeBoundsFactory;
}


/**
 * Return the timeStep1 property value.
 * @return cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.TimeStep gettimeStep1() {
	// user code begin {1}
	// user code end
	return ivjtimeStep1;
}

/**
 * Return the TimeStepFactory property value.
 * @return cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.TimeStep getTimeStepFactory() {
	// user code begin {1}
	// user code end
	return ivjTimeStepFactory;
}


/**
 * Return the TimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimeStepLabel() {
	if (ivjTimeStepLabel == null) {
		try {
			ivjTimeStepLabel = new javax.swing.JLabel();
			ivjTimeStepLabel.setName("TimeStepLabel");
			ivjTimeStepLabel.setText("Time Step");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeStepLabel;
}


/**
 * Return the TimeStepTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTimeStepTextField() {
	if (ivjTimeStepTextField == null) {
		try {
			ivjTimeStepTextField = new javax.swing.JTextField();
			ivjTimeStepTextField.setName("TimeStepTextField");
			ivjTimeStepTextField.setText("");
			ivjTimeStepTextField.setBackground(java.awt.Color.white);
			ivjTimeStepTextField.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeStepTextField;
}


/**
 * Return the TornOffSolverTaskDescription property value.
 * @return cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.SolverTaskDescription getTornOffSolverTaskDescription() {
	// user code begin {1}
	// user code end
	return ivjTornOffSolverTaskDescription;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in OdePanel");
	exception.printStackTrace(System.out);
	if (!(exception instanceof NullPointerException)){
		java.awt.Point location = new java.awt.Point(200, 200);
		try {
			location = getLocationOnScreen();
		} catch (java.awt.IllegalComponentStateException e) {
		}
		cbit.gui.DialogUtils.showWarningDialog(this, "Error in value : " + exception.getMessage(), new String[] {"Ok"}, "Ok");
		// javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
	}
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPerformSensitivityAnalysisCheckbox().addItemListener(ivjEventHandler);
	getStartTimeTextField().addFocusListener(ivjEventHandler);
	getTimeStepTextField().addFocusListener(ivjEventHandler);
	getConstantChoice().addActionListener(ivjEventHandler);
	getEndTimeTextField().addFocusListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
	getJTextFieldKeepEvery().addFocusListener(ivjEventHandler);
}

/**
 * Initialize class
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PreviewPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(373, 253);
		setEnabled(true);

		java.awt.GridBagConstraints constraintsPanel2 = new java.awt.GridBagConstraints();
		constraintsPanel2.gridx = 0; constraintsPanel2.gridy = 6;
		constraintsPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPanel2.weightx = 1.0;
		add(getPanel2(), constraintsPanel2);

		java.awt.GridBagConstraints constraintsPlotSpecificationPanel = new java.awt.GridBagConstraints();
		constraintsPlotSpecificationPanel.gridx = 0; constraintsPlotSpecificationPanel.gridy = 7;
		constraintsPlotSpecificationPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPlotSpecificationPanel.weightx = 1.0;
		constraintsPlotSpecificationPanel.insets = new java.awt.Insets(0, 4, 0, 4);
		add(getPlotSpecificationPanel(), constraintsPlotSpecificationPanel);

		java.awt.GridBagConstraints constraintsPerformSensitivityAnalysisCheckbox = new java.awt.GridBagConstraints();
		constraintsPerformSensitivityAnalysisCheckbox.gridx = 0; constraintsPerformSensitivityAnalysisCheckbox.gridy = 5;
		constraintsPerformSensitivityAnalysisCheckbox.gridwidth = 3;
		constraintsPerformSensitivityAnalysisCheckbox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPerformSensitivityAnalysisCheckbox.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsPerformSensitivityAnalysisCheckbox.weightx = 1.0;
		constraintsPerformSensitivityAnalysisCheckbox.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPerformSensitivityAnalysisCheckbox(), constraintsPerformSensitivityAnalysisCheckbox);

		java.awt.GridBagConstraints constraintsConstantChoice = new java.awt.GridBagConstraints();
		constraintsConstantChoice.gridx = 0; constraintsConstantChoice.gridy = 6;
		constraintsConstantChoice.gridwidth = 3;
		constraintsConstantChoice.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsConstantChoice.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsConstantChoice.weightx = 1.0;
		constraintsConstantChoice.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getConstantChoice(), constraintsConstantChoice);

		java.awt.GridBagConstraints constraintsTimeStepTextField = new java.awt.GridBagConstraints();
		constraintsTimeStepTextField.gridx = 1; constraintsTimeStepTextField.gridy = 2;
		constraintsTimeStepTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTimeStepTextField.weightx = 1.0;
		constraintsTimeStepTextField.ipadx = 100;
		constraintsTimeStepTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTimeStepTextField(), constraintsTimeStepTextField);

		java.awt.GridBagConstraints constraintsTimeStepLabel = new java.awt.GridBagConstraints();
		constraintsTimeStepLabel.gridx = 0; constraintsTimeStepLabel.gridy = 2;
		constraintsTimeStepLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsTimeStepLabel.weightx = 1.0;
		constraintsTimeStepLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTimeStepLabel(), constraintsTimeStepLabel);

		java.awt.GridBagConstraints constraintsEndTimeLabel = new java.awt.GridBagConstraints();
		constraintsEndTimeLabel.gridx = 0; constraintsEndTimeLabel.gridy = 3;
		constraintsEndTimeLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsEndTimeLabel.weightx = 1.0;
		constraintsEndTimeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getEndTimeLabel(), constraintsEndTimeLabel);

		java.awt.GridBagConstraints constraintsEndTimeTextField = new java.awt.GridBagConstraints();
		constraintsEndTimeTextField.gridx = 1; constraintsEndTimeTextField.gridy = 3;
		constraintsEndTimeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsEndTimeTextField.weightx = 1.0;
		constraintsEndTimeTextField.ipadx = 100;
		constraintsEndTimeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getEndTimeTextField(), constraintsEndTimeTextField);

		java.awt.GridBagConstraints constraintsStartTimeLabel = new java.awt.GridBagConstraints();
		constraintsStartTimeLabel.gridx = 0; constraintsStartTimeLabel.gridy = 1;
		constraintsStartTimeLabel.anchor = java.awt.GridBagConstraints.EAST;
		constraintsStartTimeLabel.weightx = 1.0;
		constraintsStartTimeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getStartTimeLabel(), constraintsStartTimeLabel);

		java.awt.GridBagConstraints constraintsStartTimeTextField = new java.awt.GridBagConstraints();
		constraintsStartTimeTextField.gridx = 1; constraintsStartTimeTextField.gridy = 1;
		constraintsStartTimeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStartTimeTextField.weightx = 1.0;
		constraintsStartTimeTextField.ipadx = 100;
		constraintsStartTimeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getStartTimeTextField(), constraintsStartTimeTextField);

		java.awt.GridBagConstraints constraintsJLabelStartUnits = new java.awt.GridBagConstraints();
		constraintsJLabelStartUnits.gridx = 2; constraintsJLabelStartUnits.gridy = 1;
		constraintsJLabelStartUnits.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelStartUnits.weightx = 1.0;
		constraintsJLabelStartUnits.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelStartUnits(), constraintsJLabelStartUnits);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 2; constraintsJLabel2.gridy = 2;
		constraintsJLabel2.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel2.weightx = 1.0;
		constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel2(), constraintsJLabel2);

		java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
		constraintsJLabel3.gridx = 2; constraintsJLabel3.gridy = 3;
		constraintsJLabel3.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel3.weightx = 1.0;
		constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabel3(), constraintsJLabel3);

		java.awt.GridBagConstraints constraintsJPanelKeepEvery = new java.awt.GridBagConstraints();
		constraintsJPanelKeepEvery.gridx = 0; constraintsJPanelKeepEvery.gridy = 4;
		constraintsJPanelKeepEvery.gridwidth = 3;
		constraintsJPanelKeepEvery.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelKeepEvery.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsJPanelKeepEvery.weightx = 1.0;
		constraintsJPanelKeepEvery.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelKeepEvery(), constraintsJPanelKeepEvery);

		java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
		constraintsJLabelTitle.gridx = 0; constraintsJLabelTitle.gridy = 0;
		constraintsJLabelTitle.gridwidth = 3;
		constraintsJLabelTitle.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelTitle.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelTitle(), constraintsJLabelTitle);
		initConnections();
		connEtoC12();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	//Plot2DCanvas' statusArea went away...put one in the container...
	//getPlot2DCanvas().setStatusArea(getPlotStatusLabel());
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SolverTaskDescriptionPanel aSolverTaskDescriptionPanel;
		aSolverTaskDescriptionPanel = new SolverTaskDescriptionPanel();
		frame.setContentPane(aSolverTaskDescriptionPanel);
		frame.setSize(aSolverTaskDescriptionPanel.getSize());
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
private void makeBold() {
	getJLabelTitle().setFont(getJLabelTitle().getFont().deriveFont(java.awt.Font.BOLD));
}


/**
 * Comment
 */
private void performSensitivityAnalysisCheckbox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if (getPerformSensitivityAnalysisCheckbox().isSelected()){
		getConstantChoice().setEnabled(true);
		if (getConstantChoice().getSelectedIndex()<0 && getConstantChoice().getModel().getSize()>0){
			getConstantChoice().setSelectedIndex(0);
		}
	}else{
		getConstantChoice().setEnabled(false);
		try {
			getSolverTaskDescription().setSensitivityParameter(null);
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
		}
	}
}


/**
 * Comment
 */
public void setKeepEvery(cbit.vcell.solver.SolverTaskDescription arg1) {
	if (arg1 == null || !arg1.getOutputTimeSpec().isDefault()) {	
		getJTextFieldKeepEvery().setText("");	
		BeanUtils.enableComponents(getJPanelKeepEvery(), false);
	} else {
		getJTextFieldKeepEvery().setText(((DefaultOutputTimeSpec)arg1.getOutputTimeSpec()).getKeepEvery() + "");	
		BeanUtils.enableComponents(getJPanelKeepEvery(), true);
	}
}


/**
 */
private void setODESolverIndex (int odeSolverIndex) {
	fieldODESolverIndex = odeSolverIndex;
}

/**
 * Sets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @param solverTaskDescription The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSolverTaskDescription
 */
public void setSolverTaskDescription(cbit.vcell.solver.SolverTaskDescription solverTaskDescription) {
	SolverTaskDescription oldValue = fieldSolverTaskDescription;
	fieldSolverTaskDescription = solverTaskDescription;
	firePropertyChange("solverTaskDescription", oldValue, solverTaskDescription);
}


/**
 * Set the timeBounds1 to a new value.
 * @param newValue cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void settimeBounds1(cbit.vcell.solver.TimeBounds newValue) {
	if (ivjtimeBounds1 != newValue) {
		try {
			ivjtimeBounds1 = newValue;
			connEtoM3(ivjtimeBounds1);
			connEtoM5(ivjtimeBounds1);
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
 * Set the TimeBoundsFactory to a new value.
 * @param newValue cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTimeBoundsFactory(cbit.vcell.solver.TimeBounds newValue) {
	if (ivjTimeBoundsFactory != newValue) {
		try {
			ivjTimeBoundsFactory = newValue;
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
 * Set the timeStep1 to a new value.
 * @param newValue cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void settimeStep1(cbit.vcell.solver.TimeStep newValue) {
	if (ivjtimeStep1 != newValue) {
		try {
			ivjtimeStep1 = newValue;
			connEtoM1(ivjtimeStep1);
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
 * Set the TimeStepFactory to a new value.
 * @param newValue cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTimeStepFactory(cbit.vcell.solver.TimeStep newValue) {
	if (ivjTimeStepFactory != newValue) {
		try {
			ivjTimeStepFactory = newValue;
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
 * Set the TornOffSolverTaskDescription to a new value.
 * @param newValue cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTornOffSolverTaskDescription(cbit.vcell.solver.SolverTaskDescription newValue) {
	if (ivjTornOffSolverTaskDescription != newValue) {
		try {
			cbit.vcell.solver.SolverTaskDescription oldValue = getTornOffSolverTaskDescription();
			/* Stop listening for events from the current object */
			if (ivjTornOffSolverTaskDescription != null) {
				ivjTornOffSolverTaskDescription.removePropertyChangeListener(ivjEventHandler);
			}
			ivjTornOffSolverTaskDescription = newValue;

			/* Listen for events from the new object */
			if (ivjTornOffSolverTaskDescription != null) {
				ivjTornOffSolverTaskDescription.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoM12(ivjTornOffSolverTaskDescription);
			connEtoM10(ivjTornOffSolverTaskDescription);
			connEtoC7(ivjTornOffSolverTaskDescription);
			connEtoC11(ivjTornOffSolverTaskDescription);
			firePropertyChange("solverTaskDescription", oldValue, newValue);
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
 * Gets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @return The solverTaskDescription property value.
 * @see #setSolverTaskDescription
 new javax.swing.DefaultComboBoxModel()
 */
private void updateConstantChoiceComboBox() {
	//Inhibit actionEvents from ComboBox during comboBoxModel update.
	getConstantChoice().removeActionListener(ivjEventHandler);
	//
	try{
		//clear comboBoxModel
		((javax.swing.DefaultComboBoxModel)(getConstantChoice().getModel())).removeAllElements();
		//
		if (getSolverTaskDescription() != null && getSolverTaskDescription().getSimulation() != null) {
			cbit.vcell.math.MathDescription mathDescription = getSolverTaskDescription().getSimulation().getMathDescription();
			if (mathDescription != null) {
				java.util.Enumeration enum1 = mathDescription.getConstants();
				if (enum1.hasMoreElements()){
					((javax.swing.DefaultComboBoxModel)(getConstantChoice().getModel())).addElement(SELECT_PARAMETER);
				}
				
				//Sort Constants, ignore case
				java.util.TreeSet sortedConstants = new java.util.TreeSet(
					new java.util.Comparator(){
						public int compare(Object o1, Object o2){
							int ignoreCaseB = ((String)o1).compareToIgnoreCase((String)o2);
							if(ignoreCaseB == 0){
								return ((String)o1).compareTo((String)o2);
							}
							return ignoreCaseB;
						}
					}
				);
				while (enum1.hasMoreElements()) {
					cbit.vcell.math.Constant constant = (cbit.vcell.math.Constant) enum1.nextElement();
					sortedConstants.add(constant.getName());
				}
				String[] sortedConstantsArr = new String[sortedConstants.size()];
				sortedConstants.toArray(sortedConstantsArr);
				for(int i=0;i<sortedConstantsArr.length;i+= 1){
					((javax.swing.DefaultComboBoxModel)(getConstantChoice().getModel())).addElement(sortedConstantsArr[i]);
				}
			}
		}
	}finally{
		updateSensitivityParameterDisplay((getSolverTaskDescription() != null ? getSolverTaskDescription().getSensitivityParameter() : null));
		//Re-activate actionEvents on ComboBox
		getConstantChoice().addActionListener(ivjEventHandler);
	}
}


/**
 * Comment
 */
public void updateKeepEvery() {
	SolverTaskDescription std = getSolverTaskDescription();
	if (std != null && std.getOutputTimeSpec().isDefault()) {
		try {
			std.setOutputTimeSpec(new DefaultOutputTimeSpec(Integer.parseInt(getJTextFieldKeepEvery().getText())));
		} catch (java.beans.PropertyVetoException ex) {
			ex.printStackTrace(System.out);
			cbit.vcell.client.PopupGenerator.showErrorDialog(ex.getMessage());
		}
	}
}


/**
 * Comment
 */
private void updateSensitivityParameterDisplay(cbit.vcell.math.Constant sensParam) {
	if(getTornOffSolverTaskDescription().getSimulation().getIsSpatial() || getTornOffSolverTaskDescription().getSimulation().getMathDescription().isStoch())
		getPerformSensitivityAnalysisCheckbox().setEnabled(false);
	else getPerformSensitivityAnalysisCheckbox().setEnabled(true);
	if (sensParam == null){
		if (getPerformSensitivityAnalysisCheckbox().isSelected()){
			getPerformSensitivityAnalysisCheckbox().setSelected(false);
		}
		getConstantChoice().setEnabled(false);
	}else{
		if (!getPerformSensitivityAnalysisCheckbox().isSelected()){
			getPerformSensitivityAnalysisCheckbox().setSelected(true);
		}
		getConstantChoice().setEnabled(true);
		if (getConstantChoice().getModel().getSize()>0){
			getConstantChoice().setSelectedItem(sensParam.getName());
		}

	}
}


/**
 * Comment
 */
public void updateTimeStepDisplay() {
	if (getTornOffSolverTaskDescription().getSolverDescription().hasVariableTimestep() || getTornOffSolverTaskDescription().getSolverDescription().equals(SolverDescription.StochGibson))
	{
		getTimeStepLabel().setEnabled(false);
		getTimeStepTextField().setEnabled(false);
		getTimeStepTextField().setBackground(new java.awt.Color(220,220,220));
		getJLabel2().setEnabled(false);
	} else {
		getTimeStepLabel().setEnabled(true);
		getTimeStepTextField().setEnabled(true);
		getTimeStepTextField().setBackground(Color.white);
		getJLabel2().setEnabled(true);
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G44DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFFDC9C5575A869B7D62C29E29B95B55AE82322E2451A98CCD0D12922929B15AAC5FC25ADEDD3DF2A34667B0AADED079F91C8E202A6AAEAB4ABC6A59689A1449092A389900922A18193E2C842428E6C42323B591D05A5A149BB774739B3BBBB4B06FD4D677D71DEBE1F1359B9771CFBFE5CF36FBD774E1DC1DA35E67A623C7CD5121437D8627FB667C9126982C99ADDDFB78BF1B9FB53CBA44D3FF781
	DE16FEB9D2885762FD69A58D3FCAAFA915665A0A61FEB55CA7BD15DE72BA7CDEA9E56E1DF69E7C88A2C7013207B27ED8B33E1C4FB7201CA2FA7FF567B238AE87B0810EDC6D7B087C3E4EB20EEF62F8043CFCC96AE8A7B413BAD7F3DC9C50A493BE402FEDE7745B604A94B8273E0E53F5DD94A3ADFA481D7F9229C7D3A742B44E1B482FA669EFF9568EA26BC62AA761490132EA4091638B24BF755593D9ED037D5BFBFBECCA55F02FEC37D7F91C76E159DD653448D57DDEDBFDFDBB3D6FB0FB861BE4CF2F5B66D2EC
	CEC71B59A15BA5A90B567F174EE2DA1FA445423D6B9B023B53CEE4D6406F86004EEF901F6CA4781970BB85A06B9B3473538F37F1BBBACF4C1646E6EDDA3AA2081D395FB07B566ED76DCC38A2A37F8D5AA66756905FF7C359E400E80084008C00DC0037E8ACDC3061A3386E68D19A1DC3C3CE478E17EB275D627E8AAC36436F7AFAA86138FDF22F53ED11A4565EF11D15867A0C870C6F2E8973B11F44EC44360F5D7793A96336CB720B8CFC1291F3A2FF5581660B4A832ACF186C3F86155DFA40BFB67DE59F103432
	EB47D28B969B48EE332596F473FA940D6C7883242DB78D11361E8A3F899F09DB06EF2278DC8D1E4569FDB4E6483D29B33DE4CAA746C667F2542535709A29F4432506FE083EF7CAFECEA753217420DE17D5EED2773603CC66A1214B97941F2B41B3DD9E21F1CA6E933E05B8BA1CDE4262AB2A1674DB25BB3DA48AA086A091A093A09720540CB176640675E798E3EDE6373253EB33DBE43744F823EADE032B6CD35C665D8E0FE2F6744A7B4DEE1B3947AEFBA4CEF3D98D71C10F9F5FD5B476FBC1466732C3F61BD9C7
	F51B07E4C5F6FB984D75B5AF939A37CCCBBBECC3B28898F2C9D86FADB5AB61EAB7FB14EFDC9633A22B942CFCF68DE913E173FE2891AA005F4CAFF7EEA27E2A867BDBGBA059FEAB6127A3E145DB0DE545737BA1D03DED78731C40AB193BFAFBFD8CC65C1FFB5230F6F7804609642FDE18F56E35B780A261EA6DBAFD15F6C9E45BE36F50858DE867455G1DGAE00A9GB3FAB00E1E6D44B8EAFA69E6297297979B46D19244F4D28F0B1F0C9EFDFF7A024AA13A967642F88D5084E081185C0B734203766E907DFB8A
	57F3B957190EFB7C6A6A716F17B3FA0C7BA9131595D256D200457CF70106977B3A167625A0F530F679300E34718C30B391A033975BE646C6930B751DE60F0C513D478D01A9DB5416E27A3E691E4975F91972323975BD007781BE84D8477B5D2BG1F813413F885B88830C7A2A393448140DE005DGDBGB6137E82E08368877886004C7B4DG249FFE8CF098605FG0DC4D700CDG03G90C252890013G97811C86188470C300EB812E8798927EEE237A67006DA68B44BA008BA09AA091E081C0291CDED287508D
	908990871086A0752517D4G348178G2683A483E483147543BC89E085088208874884A836C21CGF482C4G44G4C874881B059C0BE008BA09AA091E081C069G4887688608840883C89B4058CCF54E4FD75B9563C28F58B62FF2BF6A4B0D5A6C5F5C4F6BB5F41BF85BEC5160BE637EEF646D331C3763177C779E5E366D3C9D8F7036BF445B906B6965ED5E4F5BFD10371311BD5AB646769D2F4FDC445BBF54BC0D71B111397D7F43FF9ED8EC1F6F5246F6C1905A451C1E4476A603F2B2A612B1130C05ABA45697
	C84CEE157C631844AE0979F59A9C69ABB83EEC66B852BF5E633F57C82C4F1071F423447A515B926BEB1FEA6AA163AA69CB9BA4561FBE17D87C9B5903FD67ABC96DB763790A742FEF4E4027586F7EEF376D7F6B4066584BEBC9AC4A4332C3E912DD324382BFBC3C6C6ADA9233BCDF6CF38A1E6BA95E8579E5A3A4F49E013F39164434D9D15C369E2FA2374B0AE2F3742B653328AC0F3747252626A8AB2116749F67886417027E1EDA121B75BA9D8E19CE760205175FC7F3C359AE8F01520DCE2FC3E17887B7117574
	20BC4AF353F91BDE14C8EEE97712BC172DD1634C24AF3A5C36A1C8A1C82AAC358D123C717D03E59A9FC900A3EB6D35F02D9B44FCEF64BD4C77569E3FC1CABC6A4AB7DA43C4DFFBE1FE74A04B31E28755FC0F69F457A7463AA7519C1563BDCC577A8304364DAD8F5B64913AC2AE20348F5035FE07536D585357E7381CE6F4D774913D9548385B955935C1E2E358CA69F38FE90F8E0E5FE56ED51C10783232E65FD24E33931433F8B6F07CEFE91E5FA170480539DC779622EB8A581B8910B30839DC43E6D20EF9B6CF
	629A1DF6BBEBDAA83358097FFFB0340A7B2F946EAB6C4C7F4BC5F931834BE361BEC9D3DEBBC44ACF09F282593C5C84E50CFF01A89F835C54A1BD7F912791FF52905A71F1DDA909E17918D7066593D9F0FCA9622C366E95DD59FE9EF32CFCDB9D7129DD3BD6F19019B73BF14FA68D64E493BD9CCCEE2B0369566AA4F4AE8F52ADF230B542B2402F865866E4F41914EE3B02F451CE16CF46B999FD92DC2BC4FDAF883ACA97DBEB107A1ADC7A7A6E76A21D454572D2D29F219FF3E96B9B9DC63A291A7AE2836A7BEC84
	69D2DCAC3F457A327C6AFBC150653A18DE2B0E3175D16BB1D69F735F63BE219FAF9FBB467CB7751856FF5F9FC53A4CE34CEFD8DF0E9FDD67F124DB66D665917D563A35F465A7102E165311723581F48B84DDAB60ADG13B9FD129F5D7DE3C81765E6FE535B4B68261C94FE51488D54EF5FA924DB65D16D546A477CD7F6DA448127EB72787B0F35DB2A2033F8581A03349B6AC75AAD41C368A2A5249B7330758921A36B58299EEDFD9F65A1DD314256ACA6C50D17AAC137C850D5F33A96C50DD3C21722903A3B7211
	2ED3E1EBA3A2175861D234F23B8B102E4C4B56C755DE566F9A3CDA396B8B112E0353593D2C7D467C687EA56826FA593A8C7D126367175B96A3DD2C172D6328FEA48E3CDA7D3EF99169323DFC3DB62C76F3D56E1AA2E147B0232B9EE66D5BB02C254B96F4DD1CCE61F4137D686EA9C63A6943EC2D99BF2C4603EA4779A5C817BC4C5625466D7B7192515F06599A5338FDCF3E03F4B91CCE5F3E0C2EF18D52150E98533138AFB1A1DD4588EB7F0691E607E5C49B779902CE99E1EB5EA91CFE069F5D29F711EEBE2FE7
	01A1DDFD3908E79F23B3790C6896893ABACE5746693AFDDA3A1FD6A15D8CCE172023E3FE99DAAF668F9FDB27E7EB6A2D9275ED2EC6BAE9B43D248C20EE14770FD12D5C9784DD54A82BA70653A57B514D5A00F48BB9DD61F1A33A219AB18E7172CAFEEDBA4E68D81E737CBA12FB5167868366E1332F4AB38279E3FD7D97B467E14F84AE247BFAE41D55F59C777E9F6E227B41F09F8B10F49C7334C7DF423D7F2903B1D26949478B02BD634860FAC8A754BCAD2B4DDB40F21A9B6B094C86A8EBBB01F9406CBA6D7E63
	8E0F4759EB23DB1FFC2E6F5B224D6398EEF04B923876595C9EE5B7A45ABE4C8B0EEDA17A123DD10E66FB45F78C6173958B481DF6821F2F6CFC7DE3A9587315A04FD5A45CFF6F5D08BE09BAFE2D54FD716579CB0378A4157BE21146A7134F5BD840F4DB6FD8A631BC32F28C7A55986A7658D653B83AE12E49E8D7FFD4AEE81DC3D5361E21AA6F70D01539C72912ADB6253EDEA4172C6DE7B970F95034B1EC63133B34ED1C3062E4F0FB4638BDE32ABD16627F66762CF022BD35A7E11CBF09765C54D69E429E168723
	0E97BBD14799D0872B7B897AAC0054178138D52752CBD82EB97B10B657ECBD45762A953832DC7BD38FD9235B9499FC837F35ABE6C5EE341A9D7D322502F410AA7388F80AAC5D2AF6C3F9B379350E77253D05EDF0F5496EBE27FB28DDF6F8EC0AED58260C6EF0186D239E1B2751AA778E76B8FDC7F76BEBEE8DD67556F57B194F2FD95940F5CE7949B5523653C7720D1E29C52E3066C71EE23E0EBB257ABCAE7F697CA4G661B4BBC6877DC621753E83B2410B51CE235F9182F1E28517A4AFE1A79EA2A202FE07430
	D8E5BA5E7C0ED647E273B906BACE79E3FFFE4CE9265B7C532A0EE585CF67D796200E7D8A6AD89681E3C08446462A6D64CA5715922E29B295A247A277193DF685431D773D310328D3C44ACC29EC65778DF5223E0B60FE0B500CC725CF67671422CE65DE54C91A846378A40C29672DC6FEF2CD42D8DCA078A683AEE1927A4FBCCC709E85D642B0669245997DB543C83F88E8AB3F03F236984A31FC8765D8C7104F8738386F201CDF0F10B1D0F6D838949C6B4AFD58C796G2D693F107EBC9FF9265A676C75FA2A687F
	2DCE0F220F7EDD240007BFEF0739BEA2C75174CFA6A7D748698499D1C2CE6F281E1E3571B14BF638FA69B3ABCDB7920F3F1AEC9E175DBCDA512A999D064C0A352A51C99F3111DE0332EF9FC55949A0D71A0C32F79CAF165475B9231DF49CE96B004EA5E8171D40FE41683EBC01FE0E811A4C49687F1747C44C1E83B1FB0E98F336E85BAD729C56EEF167E07D3EB1F2D841CC9765D56C5246861140C1E787AD7677FB6C98295FE360A92657F3E3E8D792484CBD87751DF5D20C3F5F0571773B08BFF18A69E7GAEC3
	60F71CC67C2AF3E17DF1AE6A9FF3FAFC797FB8AD629FF8924E457AEE15C41C832EB2927B38377ACDC93B5FD1E1F1FAE165DEE9E257F5B82FAAF55837E39F1BA5F58EDC9034EFDBA2D91F1E9229766DCAC7CAF183G5325BD8FF5C9831A52735036B9F9246D4D6E7E9AC677F6BE5235GCD04201B1C4F69B6B03ABF64232F6201A67BBC346DF26F57F06D1715F619EC43489623G6773019DA6072AF6458D5DF66B376B02C7BB7A636FEF23BF56B64D106A968D8675C74577189F5A3E277A23700D5A02B2G267FF9
	85E867F420C97B9E5A19DD007EE077E50545346D19B10C373390F9CB4F079C7FFC643D3B90FD446E7FC06910174CF90C3FE2B172C7GEF7C7998AF99821F8D380AA96853ABACA6C95DD353C50B089AD6472997310EEE600F1E023A7D6EC57FF6EEA9C23A79C0D37CFD243B250853EDE477CF96919F8F19FD36A16F1018FBD89DEF94E39D8D406F93F5C4947B7BA1261856E1F39854710C28A38E789708BA761678475B4C9224B3DD10DE52F9817AEC7F9244C781AED9602FFBC74CE59742DCF6A1626DEB906F82DC
	0C405718B01EB381D79605FADCE6A24FDEBCC172DFC6F33F49FF7CDC7CAE56558675C4C6E1BBEE5FCB708E98774C76FD3287A6F33EEF5CCA1E353079G033E516A3475425DD00FF32753D78183BE2B7BFAD1F7A2541BA374DCDE7E325053DFC74657DB0EFC5597014D97A15FC365E40E72C04B5859D203533F7D9E52CF875AB4C17F3DAA341569AE3DD6C7E3DC79D216DDB0EC3B7D27ADFEC04BF07DG33981375D4954AAA7D8174279F202C1D6BC95FF5FA95171725BDAE3997753BE43D68C3C09FAFF87E315EBF
	EE3A2B316EEC202978A14ED90F8F68F30DEEA81B7CC32C67016A0966883CAD6D2F0836D4DD224E57659BD01FE810B5DF48BBEFC33076E3751A3FA89375E29742BC817D78BFC45D45D353CB9A26E25D4DB57E71F9C98D527900A6CE507D0362211BAAB6335DF6DC2ED847E40FFE3D140E8103B0DF59AD4498768CF36AC17DF92D85D355739F39703B74E27F73A06C9C53AD07091C359733F3CCEB847E810A37EB70CC5FC769DA1C5C4F043238CB706C4D6B5B09CFE2AF05FE8F10891083D0968D7DBD9A5754AFECA1
	4FAE47BD5B345B81D392ACE15A7D9EA2619923FA621BC1F9145F5FD62F4D4F199F6E9E8A6F2C5D3C8973B13B47BEA3315B81B63AGA2812683A48A1B6B36D60632994FE8F1E319BE1B9C04AFB35A788CCFF0BB9AA9DF4E047966B80259DFF119F1DD4C7E5FB4907B3781CDA7008BA092A066B2347FBBDBC336F9334F304D19DE2FBA437343FB4E707CF039B3189F1203780171E53875FC99BA7A9C5DBD63FBA6006F9D5D7D6A4B7D79787C351F7857E0AF0DAE7CE11A627B954F9E427D345A4B312F566DA47BF5F6
	380F84188E10G1089108BE03A825B6E512D1F06ECBBB260F6B89B815762F48EB66A1F658ABBFF6489553F0C7DF3F3C83E06AB0C7C73FE45EAE37FF448BEE517CD36DB18FFDE5AC87CC3F25A4EAB50BF37B4925EA8380F85C8824884A81C863E81E81A067EA95F36758C7D43D6D2015EE1B68EA9617966C4C8BE4BB4FFBEE65F7AE6E25F9814C583448124GE483AC3BB23D247ACA34CF5936B314FDE29981B66239G03A0E07AD6F84333731310FCADD77A7331367D4806ED5BFDA51EE3FBB6CC9DF2C372C5DFE9
	4497B39C1E3C5BC37245995A6C32224D4942E647C8F8BA0C0564132EB2622B7005B796FE9240D7253BAFB31417BF9A1E3C52GBE3D3CEAC3F989E14AFBA8243C96C3F9679E8FCF5E4581FC7A39255BD05E5E9361454B5110FCBEC3F92F0F05A76F7D10FCD1D7994D8D7D76E0F3A7DF62615C3985674E182B30AF7DF7004CB3EB0B0213215ABE5350B7F383784E4CB78F0666B314B769D4F876DD9840270F35529F9971FDFEBABCF95F84706965D5984AAB104273674BA1799A8C654D8ED35E4FC272F56A6431397D
	66DDE4EEF7C159940099G09GD9G527474123553F1EEFFE09BBDCFFD26F37B38794B413C70EC6C8F4957B45D086F3D7C7064ED884957E5A86FCF81FCE79623FF8F602B525DAB530D5A7063965206D320AC86A091A083C03ABA3DE4B5C043555806EFFE3AE382ED98BAC73F30A0BC3FDE99122F7BEAA33EA305614933076473994A2BDA9C1E3CEDA179228C657DA6CCF959A179E28C65CDFEB1BCF9BF884917E8A86F6B227064F50564CBB3143726B8BCF99F07644BB614179D263CFF0664AB3E4608EFCEC9F872
	12C372198C65F5AD89CF5E2237C37155994A0BFDA7BCF9F3C27235994AF32F89CFDEC4C8BE2B213CBAD3F873442EGBEFDAE93E1A8EF693B6149FBA3002FCAF79FEDA86F66B0654D8DA9AF4ED01E2DBCBCF9A381FCFAF949067256D505A7EFF3G1F3E7DB28D657DBACCF94F8570696D4BB114B7EDFDF87D614610FC25BFB6626B2B8E4FBEA500CFEFDF05CE9EFB376379BE7D3B9D646CC38350F6G30B55D12C13226A36BC07B0FF1CDD731A14C7D0AGBE3D1EB38C7D1295263C3F041417E0A8EFDACDF872EE8C
	60534BCB5549E36FE65FC44FF0AE04B2E9866430947767EF89EEAD5CEF93B8D79741F5433DB2835BE256E1F25EAF926E2781A482E483AC3BB63D24922085209BE08CA09AA09EA095E081C0AEC059F5D84FBCDACFB55CF7DC474EFE13F35D640C358F6E232FE36714632FE3670B53601A83D0F6BDFB76D8F7BD5673E4D7B6DCAB398CFD5C937D04AF8C4A89646A70C447E07D2805530EE9F816715F59B38279087D44D75D5C9F538CE84281E67BEB07096D64CC6DDBC1E870779BC16AF9DBF73FE69C19AB78F52586
	3714DF756F1B93A06FCC96C86CBDCBF2AEA058BB4993853636B2ED8F7E5E5C075B0D5F1B7BF83B71FBF39B373F4C70D007762B8DD83EF57B786F55B5EE8F7EDE5D644F026BF97EE746FADE7419311E17FEB63E1EBF7AAC181EEF5336F8021EC3F2FB9D64289B2D1A6D3749BE723EAA2CC677F99DEC29CAEA77681FFFCAB8FEBCCA4F81F7DE4F5E7FE2321FE988EE671F9B0C6D7CEB03311D7FE8985F4E45BB025949743B55097A11774B186D8F3E3602566971486C94C213D9B1F33F7CC1A17199701E635766A175
	DC69CACF8927E9D61C0D9BC49FFC7E10515CEB875991C47E75017D1D5930F3E7F0FF6D5EE96C2FBDBBC9DF77B83D6EDED96F137DBB4BC4193A03C04ED191B3984DD14A2F185D7DF200CF7BA8BFAB8B466F5C994C6F4CC7CFE8FD74156A23D7B69A79A886FC13GB0DF67232C6BB1266EE88C6E2344C6E39F5D5F985CC7739AC37B68094660BEFA3AB1340FFE5FB83E0FE6A8CB390F5A7A5C4E21368D3C6EAD4D41EDEDE8B636F5D7F3F0DB3FEA8EED6B7E6660369EEE8EEDEBDFB3312547E6313979DB244C4623DE
	ED9CEC94F1305EE69487B9505E4BE252CBCCB17EF1D09B03E7ED5E9F767759A6AE67D743DAB91B049C2F55C8CE8B54578560524919AC64DCBB62AF6793344727157309E84BAD235AB121C6601F57623FD271F79DDF226D9F9B909FF9428FDF03782F477C701B441833A5D83E9F8D364482A46AECE475D51E546A35D9607FF252CFCEB56263CF7961B7A27E54E92D5D9BC5BD75748C2FA636793948E7A5BF3FC2C470F7F8F6850F793BF69947FC522E60B17F702E50B11F3AABF84CBF35ABF44CE76D9A3FFFC76569
	FC404FF5FE1B2F43D7B37C5A82BF5FC094B1B91BDB027BE6FB0B31EF1ADA027B664B16503E69E8896E1BC3AD21FDE3E9995FB7779768FCD0477097976A701B99DEDE2C4357B27C9BAF7A47933E9BBF579F5FD64B7167947963B7F27C7EE2FFFC8D47FFD822137B89437F3944DFAE523F7D0EFFBDF5782E3E8E3F1963EFD8630F5F4271CE13DFFF5B0CFE5B792E363F558AFFE67961B7897C74F2ADDE9D2F3A2B34F8357F172D57626B847E7175BABFECE178AB2B3574645BA9EC6D7E6FFEDC3B2746603B0135B6D2
	47329B52CB5682EC3B8157EDAB86475B33683841FF7C325E00E374518D9A9FB52AE355DBB5FEF8B136BE6E0F97E36B3D8749B7A96C66D127D781FDEFA03A7ED2FCFBE28C6E27F29D92EEE07B94ACD73E154E91FDEE739004B6BDE49BF7A361ADBE043C2B613E9A208D408590F5A36AFEEE9731DDF610E45AA2955FC4782E6A427755F2845DB10568C6DF3A947A461413EF4735591D8A91EA6B33751AB55FEBE4B4993B493BF2EDA15FB9C37A3D6F91FD7DCFD1E359778F63F7A64869D9F1A8CB4446BFD63C24A9EF
	B57790B5F859ABEF13B6EFF6D8C8B1AD62B97476D295AF2A646FA53590DD5A497BCF028B6B7BDFCD2FE84BB44A307201F5245EAFA88F257A46E1D3BCFCEE5DDDA44A303F2E6ED1F1B8964C79100C376CCDD7F1961917F549CB85FD07CD0115905B4FDAF6C43B1F55FA930B93BBDCA3G984DA343C6B451F00D3D894FD7AF7EC0FB3EBA0953AC90658BA42372DCD13E275F283CB4964BAF359A152F956523FDC665F5223C49103FD5143F64B6AA6F0E45BE93E5A57E9EB68FF40C3A707B00CBE85CABC0B3B996771B
	1C94978577F1821750057BD2C982370E1E89DCF5B358F0B36266D2DC9D5C37895C2B3548EB91389B5FA73819F01FACF03B68B9348C38DFA8F0E994A77DA43D246CA708FB4E0675D58A5CB6FA1EEB8C6E2384EE0A95751BA1F0EF5AD03FF801D3C4FDA98277C29F6A377A9644AD29C75EEA011336A2EF13409D2FA13829647E969CE72B7B7563EC42AD6A58BA7F9603BDABFA3EBC060E5BD937307365C582BF1362F3B5F8F66E3C1E62497D2A387492DF9C6A7441A06A19A1F03BA8CE3AB53DE4552D78CEC95E3704
	1F54D981386EDBB1F6B6F47B1D93A5AF945457FF6E36D9F61A7BDB69684D66196D14BF9A7862G12EF457713FD9F607B493157DDA3950E9A3F031E59F1B2BF73D6660B421F32ABF98FEA7E959979ECEE1BC24F69923E86A8777D146066783DBFCC460A3F6731BDD8D63E502F1C0C27E44ED54B5F69521693FD7FE73E363C4D2F1C78E3AE67E736BD7CBE5A167BF01CE41FB5B968B7049339CDD97C9A15F5D95E32D1DF819B5FE6F551F79E5CDE1955BBFDC47BED623AA55FB17C3E921D6BEEE375D573EB6E2B1767
	31F67BE5452A40F653FCC72069881E5D325E063942DB74DD5769F09F8FB07FB6FCA670628866958B84ED61A1C2EB1A896394C05BCC0C97760C41606C989D3DD9FD5BBE407A2CB3D1461ABC44CD97386F0AFCA6CD609E31A22EF49662627A9057A2F0A784DD0440B539716CE77CAC1C6B5F6D42772633E711EFD39FA12F0D73F9673BDD741B2ECE0782497F8E37EC5EE533134CC162394374AE0C495A5BE17C391D3E2B5355445B266576403ECDE817F23F9399DDC0633A1DFDCB1B68BA79F6562F73048E8D9BD1BF
	A6F3BF9DE75300AE6BF6E1C3BD4931FA4C3D037DEE7212A66A17C5E997811DB49B752B2CC57DCC338375AB10708CA0FF9795E8DAGF07F388BFEBF68275FBD6F97D19F593DC38E8F171BC647F842E31D0DBA9E36795B6121E32F8F4AA78B1A67EC46F6948D50F19568268BBBDE9CC0BB9203582105D420D92031A3B7400EF6EB209D83D63423B8BE3D0461E6D1DC995C2F9538C69B7A39B69E75BB5F067AF5449B6BD724514F9A6F6F6731F8D5BFA6233C9FF50994F23754235CE8A177752DA8B75EC0AEFE979AE5
	27447B47DFE63CBE7EAE2A894C19E3EAB06E9746631C72B955CF3A836614BB90F73F95671E3ABBF0BE7C891D27A261FE86C012404FE1735D9D6C3BAC05F3905FE06B5468CF68032DD902814AB2AA6367737CE494CD30FEED3FB1AADBBB077862534132907C50C78683E3D09944B14BAEFCD2746DC45F45626F8ACD180F659CBF25F37B589CB5B698FEAE45C7EA70AC17E8923944D4A84B19036F0D6C58CBFC50B59766EF00A8009800E4002C393867DC349B1614631F596BF03ADA65E1592EF9B4416C3B4870D940
	F840665BFB0AF09E1FB967C6A97E3BCDC13F2112B31745D1D9823BD67C7632BCF2E576256EA37635C01985A092E0BA40CC00148434EF4F6ECAE6DF3345266CD094F32F15FEA1C7BC6C3149A3AA1A2CB5D95DFFEDA7F1108975640A3AAE59CD3E133A5FBC403DC42AF43AE9CD00ECF3BBDD32DB99E5D87AA5BE9E877FBA2277536ABBF99E815726BB03652497527C2073CEFD9CC4D33C65CEFD9C3CBB0CF160023254BBB1FE17863CB7D1F717AA33752EE072FDB467683ECBAFFF92455B6F524BDFF288657B20AC63
	AE143F638323B8E9381B496D38BBD0BE63DBEBF89E574A694702721D9F3086A0904569E78465FB5CF09FBD1E532784651B55E7441749699785650BB014D71C4868D7A786635B6D770EDEB527EB6257CEFE35EB78D93F7B79A1ED1EDBFA39BD9F5BCFAFA70273CEC9D45786EC4C1C5DC5465DBE335DC31E9532BC2F70B066FE09C03F0652019D927E4B7EAC9087D81A7FBDC6BE614B757B75B29C97D65F74E3296949267C770268174375EA39C755EFED1E290055B909FC72170F7133B74292C3AAAFC7F9979CD44B
	DE2AA5B93BF8EF3B51E9F73A2BC62CB6C5E6F69D2E95B96DBD93356B01EFD5B94F3DCFF4A0FBF72C6C19C3EAD92A4DDF3F3643EA599FEC636B37F9G750B1F30FE1EA32A1CC9D6FF7DAEB92A163DA674E3777690BAA558D0276CA0BAB199F7F42BB2367423FCF61FD3BF3E0C162DA823625E71E418FAD499F757E86316610E74876E09FA7BB1EF68063AFDG5381E65E4B4E61301C6FE9AB66FCA9779A67FCA41EB1674B841AC5772AB9DF71FD7A1CAF051E19E0ABB67DDEDC5CA9E21B7A16283E7C1A13AFCB6ADE
	5D105CEE9EF251BF2F413FAF7BA71BFE2F0A58316ABE5C4B3AFA50283C62BE5CCFC8BE2CDDF31B7E3D47F04D1DF378F87E367B18AF3A6FD37BE4A974C926677903F45DA6619896CF7B877BFD0F6677975FF6CA017D7DBF85CCC66FD91541F205AF7686FF1E35F72F71732C83FB0D4F079C5EBB7EF99CF9EF7073B8763D5A6FDC335CE6D3AFE9732950CEF1G29G8BGF26F43BC67EFFB493EBA794BAE0179D23325DFFC07C41C9358975C562CFD217F864C1F7651F528D3D11CC324FE44BF3F0F341762F4D1A443
	656CA3BEF25B7A2D8AA36578FF6E23FFFFC56E23E8064BEDDF2E759BCB49B8FDC1877101781E85FD3840BEB74456D22F9D256B4E2474124AA4DCDFDD5CCDF0EDF0EF9538A5BEDCF30DA5E13EF371309A4FD7E9FEFFB8FC76E33B612C4AE0B617537D299430B7D37861EA6107421F219FCE9DD2EDBFD7733B74505977433A33AA03595CC97755BA40DE4B4F508F8BBFC0BF7884EEEB9E52A58B5C43F9E21FD8602E21EB7A4A7B53CB365D0F38A23126EF9138E7A8AE1A5C8B5C9E3137BED360D27A102E708144FD5C
	0FF42582F729282FDB60F204DC3B40F5537D3C84381FAFF0AF507572A238AFCCC65C913B10A1F087A82E9B6EA312F1FD3A0FEC6D717EF8DE8779FB9206EBAB98F53E00DCE0CF4F005C2BF4187BC59B179C566641D3092C647177AB185C13B2193F9300B68DA097E05503B06781B4GF4GD88146G26824482A4822482ACG4881D876D0FA0969A19CAFDF5F3FAE186E00641B77F450F41BEDE458647A7754EA752F057ADA9E9ADFFF7E7E5700162F8BF8DCE744F70F5FF924494093FDC6FCDFED55724582CFE288
	BE369E3C329F570329C03F6CE15C335ED8C35AC137D73E4B6E9CE9652998DFD77C2E9F773D1A9E46FD7015D63AB78277D381E29F463D6665A26FCCFAF8223970C192D2DC66DA11D3670A3A5344997D5A940C5D0DD612A75A61BE92A096A089A08DA0BB85634252D3B1CEDCB0E35530E0BC1F76861FCB1BFAC34F25DF75526FG439CC89EBEB25C3EDE1267BA7B7ABC3218338F74168A5CEECF1B13BCBB473F5F707151403C3869A86645057358D90155F02DA6F09D6EA747F69B6FA7A751F93385E8BB66E18E7D2E
	8FF3E8A340FDD35C2FD500AFF21E3ADFBBED1EBE075EB29228F35B886A9C43F5CE04EB6ABCF6861F6991AF74D8B84FB817DF2151C3FA84460AC7D4BDAA9E516B617C40FF5F7D9CBA9F5482DD43A3A83360C31459790831CC6DB3817BA3EC2F7AADDE96211149E4D868DCB2857051C246EF72D0C6DC90995A7D70A41D0C34G3B6E6877FFEE13C27ED41544E43DA356C3D9C1E4E91F8D64G4D321F2B32CCBF574B4A33794BFAEEC01555A77679ABFF9EA8EB31CED68350F4G60BE02D5A30B557D7A10DAF7D3BF56
	2D8429DBDBFF242E6EE981F557BB543AD7883DE302542DED0F84DD5D7383FCB462A6715E63F453C3EBAC5EAFF0E33CE7G7DA2G53FCD8674DE77298DF295D5F37071D2A0EDEBB6A58BA5F389D35F1538DB48430AFA87375BABA6CE43C35BA5D3663CE07E2366F305B7A9DE4B54163B6520E63FB44FC4C93CCF4AFA90E5745F06FD3DCB24771F786646063E4349CFA1C3CDAB6DEF3DCAF872EB9E2E563B547CF6540B547EC1978229F667CCEC117A0F087856E9EDAA7419D30D9942B40BFC075A27896198894E739
	29DEA3945749F10FE9F087B96EC9194485188CEEC75E8C1934F91F4DEEE777191446660021DF7863F7D4C71B4B2C517B9901BBA85AE234B630FDCE55EA5B07615A878269BA878269583993FF3A27ED01F4162D01F4362DDABA162B7C58F06FB7F40EF3EB18FC4FCC180F6D211FCB73182C797A3D75A80A4F1E2F5FDBFF430A39D4AE69F70F629AEB25DFCE17847834C74FA427DBB7E02477C24EABBD96EC6F79E943F30C2BB8FD05819F7FDBC456607D35519A3A3F36DA0D7B6BBEEBE0FFBDE0B56E2F072D5A7E4A
	3F79FA947D3A8DF4D79E431CEF66D19C1BE29F435C73B714B68B6EF381D6BD8E399E9DC36FA3FFF6160FF979BEB2666976AE7B5D66D1E9E2B9696F0FC9226DFDA328E3A54834BC0EBAA60E200E539EC79D1F253429F03F9020B095E97F68439CF6F56AC47363CD9E75F727225A7A7B8F48F33BF2326DE8A227A24BF98E7D37F5A82BA308ACD657892FDA6F0D43EA3DBF4BA3EDDD5E6C3018C85D65181B2F4C47FA270CDB6F3C91F247F87608F3A9C99336BB5B2756F329D8DF64CE381E154755FA3C620CCD6DAF46
	754D891527C148360F4BF3559839635FFE27FF830278F38670E47C8257DECB1713F741553FAFC17FB4FA7310CB995DC9F3091D325901F27F6946333A2B1E40786A9CA47536423D85E06C890C2F6B6C286354A70CF4C4DF5CF3D2ED679F5949FBF81AE3CBD56D64FF5CDE74D4B5B6FF5D513CCF925F397D004866FBF6F45A64FF574377120A47E9130FD75FA7D3AB7FFD0966779A6B595F032ABB2BB2589C7811417C395EE07EEC9840BD1E14A7441E5DG6E5324895C22C1244B9538270EA1DD7113B86F380F06B7
	DFC60449F747047958FC3A3F1B44CC65137AF976A84557BD291FE797087920894AE2043D27C2FEFB50D86FA9E1727DEC42FC4C2E9E7A77A61625696D9D247832B43D3D0B445C32964AA25344F7A97D3EFD38907052AF4FA42F7857BAAD5FAA6029B8A33EB23FEFD3ED831E36B3626B70FB06EE819E5F9971CD734B1B26G4F74B362BB6E176F4D841E64B362EB76131781BC59E744179070BDDEBD94269BFDAB612110FC7AEF02B03E827AFDDBD37A783A8546DD8A1D97EA5375F1778B0AEFC857475D11C10C3BD6
	A80BCB47383B496E97BFCFC17CBCB53EAEECDE49BA85BF79592F7801714EFEB1FAA28D697FF2ECBCFA7EED22EEC97C16B53F1FBDFA766711226E33AF436AD3EDBA26797D371133AFFB256F6C4F130BC7836749A523017364EB9F85523D75D1A05D7A7CC03A5A7CC03AC1EBA09D7BA62EBF5DED8673F83C41BCFED1FFA05DA57D01F4DD8675197D6AE35F82C8DC49F2A73B4DA113AF72FDAEBB847D1B9EC23F8D7ADF5BD358BF9F9BD4E363A94DEF4B60590F1313E7D5863379DEFB205F9E30EB7D46F2578D9E6D9E
	8A9BD3BEA24574BBA0AB2584AF59CC0F7D1334D811BE622F2819B4FFA648E466AF29191C8E138B3F0AD86FF1CBAB4462F1B72DBDF3F8E3FD41EFFFDC10AF956E16DEE4AB605DD2A1D9466E169653D56EEE29GD640BE328CAD54822441D061F27F55E85E352F7FD769059B3D79D271AE6945BE335DE5B5C3C5744F9634CACBFBE80D329BE89529147EB11E75403B1E72EEED1572FBBD0A74160A6FA5F8D65A6EB2774AC0D2E2B13B87E98D4724D35262C3D2C91F3B3F078AD0247CC33B8E01D8FAC70A0BDA25A50A
	151CA9A01B120A743ADAF59BF9AC428B36D26B24FF41BF977D67A14CF9C69613024209908794CC842BC8CB68BBF8F4292AC8C59E39576930F80CFF55331799C7CD640BA4A602240FDDBC908D9613137ED58F335DB0CC6AB5E6C5C4C4684C727B6707C5BDA5C5AA918B75E0169463127B7F5CAD7F91471243CEA6191CF6D224E554E59EF63CC9F0AC51583D844F912EBB2DBD6718F3CEEF5077F9A67F0A6596513FD24FAC3167FB7369FEC9AA6033FF054F73CCBE72F7B003BD23EE9FB25B6D0A59BE3843E1A14F2B
	F70EAA32FAC66797BED23F87E9F8A15F1F255FDB9850707CEFD0CB87883F62EE5F9FAAGGC80EGGD0CB818294G94G88G88G44DAB1B63F62EE5F9FAAGGC80EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD9AAGGGG
**end of data**/
}
}