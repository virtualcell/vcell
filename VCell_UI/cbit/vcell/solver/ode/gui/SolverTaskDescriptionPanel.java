package cbit.vcell.solver.ode.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.*;
import cbit.vcell.math.Constant;
import cbit.vcell.simulation.*;

import java.util.*;
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
	private cbit.vcell.simulation.SolverTaskDescription fieldSolverTaskDescription = null;
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
private void connEtoC11(cbit.vcell.simulation.SolverTaskDescription value) {
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
private void connEtoC7(cbit.vcell.simulation.SolverTaskDescription value) {
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
private void connEtoM1(cbit.vcell.simulation.TimeStep value) {
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
private void connEtoM10(cbit.vcell.simulation.SolverTaskDescription value) {
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
private void connEtoM12(cbit.vcell.simulation.SolverTaskDescription value) {
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
	cbit.vcell.simulation.TimeStep localValue = null;
	try {
		// user code begin {1}
		// user code end
		getTornOffSolverTaskDescription().setTimeStep(localValue = new cbit.vcell.simulation.TimeStep(gettimeStep1().getMinimumTimeStep(), new Double(getTimeStepTextField().getText()).doubleValue(), gettimeStep1().getMaximumTimeStep()));
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
private void connEtoM3(cbit.vcell.simulation.TimeBounds value) {
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
private void connEtoM5(cbit.vcell.simulation.TimeBounds value) {
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
	cbit.vcell.simulation.TimeBounds localValue = null;
	try {
		// user code begin {1}
		// user code end
		getTornOffSolverTaskDescription().setTimeBounds(localValue = new cbit.vcell.simulation.TimeBounds(new Double(getStartTimeTextField().getText()).doubleValue(), new Double(getEndTimeTextField().getText()).doubleValue()));
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
	cbit.vcell.simulation.TimeBounds localValue = null;
	try {
		// user code begin {1}
		// user code end
		getTornOffSolverTaskDescription().setTimeBounds(localValue = new cbit.vcell.simulation.TimeBounds(new Double(getStartTimeTextField().getText()).doubleValue(), new Double(getEndTimeTextField().getText()).doubleValue()));
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
 * Insert the method's description here.
 * Creation date: (10/4/2006 5:19:25 PM)
 */
public void disableTimeStep() 
{
	getTimeStepLabel().setEnabled(false);
	getTimeStepTextField().setText("");
	getTimeStepTextField().setBackground(new java.awt.Color(235,235,235));
	getTimeStepTextField().setEnabled(false);
	getJLabel2().setEnabled(false);
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
public cbit.vcell.simulation.SolverTaskDescription getSolverTaskDescription() {
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
private cbit.vcell.simulation.TimeBounds gettimeBounds1() {
	// user code begin {1}
	// user code end
	return ivjtimeBounds1;
}

/**
 * Return the TimeBoundsFactory property value.
 * @return cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simulation.TimeBounds getTimeBoundsFactory() {
	// user code begin {1}
	// user code end
	return ivjTimeBoundsFactory;
}


/**
 * Return the timeStep1 property value.
 * @return cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simulation.TimeStep gettimeStep1() {
	// user code begin {1}
	// user code end
	return ivjtimeStep1;
}

/**
 * Return the TimeStepFactory property value.
 * @return cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.simulation.TimeStep getTimeStepFactory() {
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
private cbit.vcell.simulation.SolverTaskDescription getTornOffSolverTaskDescription() {
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
public void setKeepEvery(cbit.vcell.simulation.SolverTaskDescription arg1) {
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
public void setSolverTaskDescription(cbit.vcell.simulation.SolverTaskDescription solverTaskDescription) {
	SolverTaskDescription oldValue = fieldSolverTaskDescription;
	fieldSolverTaskDescription = solverTaskDescription;
	firePropertyChange("solverTaskDescription", oldValue, solverTaskDescription);
}


/**
 * Set the timeBounds1 to a new value.
 * @param newValue cbit.vcell.solver.TimeBounds
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void settimeBounds1(cbit.vcell.simulation.TimeBounds newValue) {
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
private void setTimeBoundsFactory(cbit.vcell.simulation.TimeBounds newValue) {
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
private void settimeStep1(cbit.vcell.simulation.TimeStep newValue) {
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
private void setTimeStepFactory(cbit.vcell.simulation.TimeStep newValue) {
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
private void setTornOffSolverTaskDescription(cbit.vcell.simulation.SolverTaskDescription newValue) {
	if (ivjTornOffSolverTaskDescription != newValue) {
		try {
			cbit.vcell.simulation.SolverTaskDescription oldValue = getTornOffSolverTaskDescription();
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
	getPerformSensitivityAnalysisCheckbox().setEnabled(! getTornOffSolverTaskDescription().getSimulation().getIsSpatial());
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
	if (getTornOffSolverTaskDescription().getSolverDescription().hasVariableTimestep()) {
		getTimeStepLabel().setEnabled(false);
		getTimeStepTextField().setEnabled(false);
		getJLabel2().setEnabled(false);
	} else {
		getTimeStepLabel().setEnabled(true);
		getTimeStepTextField().setEnabled(true);
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
	D0CB838494G88G88G560171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFDDCD4E55AB03BEBC52509CAC9C52DEBD8ECD151E6C5C99B96DBD4D4ECE9D14BBE522E35FE5532657630456E9E8193C30D52CDCB5CD8338D95913F9211D4948D93950590950B79B84044B0B34E9C909475396E0F6BBEE74E1CE1E45E75F97FF8DFFF3F4BB3673A2F6F7B3ABF4FFD8E5222CFC74D1E153DC812E64D16783F4D33A42968AAC9BA50751711088BF3B466CB1AFF7F8278C03A5F1D8B57D1
	D0167ACEE3FE0134561A8777E3613EB933B1FFB17CDEA8A554C6DE8EBF7C68D120ECE15C95AF772F67365528E78E3D5F57B88F2E8BG62GC72E39CE225F5238046353B89EE1D636A44D20B4679A8BB92E866EDBG2281621D0CFE82DC67D24D17D414F33A66E151524CA75C5973099C0DCC040DAE465B442FC5525B332413C457AD54CE429386E5C9G44799C6935568A38D656F534ED3318EDCAE937C5365BCBBDCEFB376CAEF5DA655236AEDBC5C5BD3DEFB0F9BA2AE50F45EDF3A9B627234E64106D1254C765
	7F31B10F4A1324EA384FF3935CBF5B084E8438CF8548907815BF90FCAD7C36827439514F2F3E2864FEB41D39CF1A5C762B396F7B71B35C4D7C0BF72BFE962DB267FCCC6BE4FA8909FDA61495FA9A734BG9AG9400F00025B4975ED83D8A2ED5E6E5173333536928F239F65A2D6E7D60319DFED7D4C089479D16ADCE37D512D8FD5F5ED8E2E0CFFF4078468D180F45A4FA8356FD4C61BB24343B2F491EE390133468B359B19E960BC40F9A9326FB26DF5D13BD5E39692D7B082455ADCDDDBFE736016E4267574F49
	64F266A92A6EBA055475B5B487CA60B761A3F94B70E3A8BEDC03E7F93A026249FD9C142D68425CE8DA003654660E168A560DB40CC344430333433A188D51DDFADB3A6CC47604AE2673383065B60A8F6FD6714C1635B4CF49FD43A94823530D79AC3FE61612F6BB6FECE3FEB1408E00A6009E00F000683318E3DBCB56DEE00E55195C4A4EAE1B5DAA3BA546BB3364A3384ABD0A5B345761D1CC8E0B5CE8F25BCCE63B6C11B84DDFCBC88C4CDEFC59EBC86EDBC0475732C3F61BD8C3F51BBAE5C5F6FB98CD7E1A8F88
	0DDB26258D36CE9994F43AA4143BF04DC2385ACD9E65DB17552448AA85ABDF3A0654C9372991A402G784D62722FB2922FA470BF9DA0C344619AAAEF1F6C067E22222256696C68F2B590CF249D67C81C730F103EA397FE971E4398AFDBC7F00360BE729C4AF9EA4D079AB955B68BB15F646E45B69659C6FC0F857AA4008C003C7310C3G1567B10FDEEF42BC2A1EFF3BD4B271EA43BC8A031CEEBE4F72C7B92FEFCF37D1BD4456C8A80B87C8834882A88C41F1E1D5DBCB0076CD6CA2F6AE60B613EB863F661D77EE
	1715670D5BA95335BB20AEB2104D663FF3B43C58563534737D48E075F265FAD2471560E793C0CF8856CDDEE9914B751DA60F0C59FD508D09A9DB551AE2761E310F6576FCC0F959587A994067GDFG2C646D6E9FG5B816AC93E829C85B8A8111E8972G609040DE00ADG1BC9FB8130813481FC8BC0467D5DG249D2E81B881309AE0973195E093C087G2410F48660AC40B00073GBF829881B086609600CE916F3B287DE160FB9CC0BAC05E0F20FFGE88288810886C8814884A87AF1E3FE8D008BA082A081E0
	8AC041CF9A734B81DAGC281E281D28124C1502FG548174G0C82C88248GD8F28974B7G6DGE1GF1G69GF91702FE00A6009000E8009400CC00224BC0BF006BB24C4DCDBF24E42B750AF921872C1BFF70B86A4B0D6AECB50F73DA8D5DA6DEB7DBB4386DBC7E3BF87DAC6075380F7FBE486B361E576391DE7747F99D229C8B2F73B6DE6F9D3C1E0C7C5156B156EFFFEDE6982F7FC063B46647C046767F8F7FF9E039BD43AB37F37C504E6674A437B7991413BE11741924AFFCDFE2ED01646CD649BB0FC96E12
	1CDF29411136027D4BE60EA36D63B37E7BD3093599521FEE10D8BBDAA631367615C68E69D7C9DBDAA73176743544725F489FECBB7BA5355D7497AB523E3E3D0018E23B7B3FDD377F2F839BE37FD6CAF2D16E169DCA356C129DD6786161E53357121C657345D627604927F8974CAFF741044EA3709F2CA5B9EDD294374D5C2548753222589CEDEA7947EB09AECF175925CECDD1D751DA52FE1C27E0FEA968BFDFCB66E6962743A15341DE307072AF685CD0364B1DE074AEE717C3E178B5E5E4BD5DA1777239693A32
	F7A5B23734F711F9AEDB23461AC8DBF439ED1DB005A0D3E5E94AE5E45E387C4892CD0CA2GC7565A71F0CD398C67FB27BE43795E0A53BF17923EF7E59B2DE1A2468C4DAE8BE5F32C6AD0F53E47ECDA3E4E5876E2BAC765F88F33356288212DF34B5DB67994DDA167D05A471A481A214169F69CECEDB5DCCEB33A6BDB095D8A4C386B955935CEE2FD58C269EB8F290F860E5FE332A8CE187832325DBD791CE7A798E7752C63785F2F7F0073B0BCF261DCAEF18331558A7E76G040562DCAEEA9DA947F9B61F446DF2
	5A6D2CEA21ACAB14447F4D36C5BCFE51F01F904A623FC0145B5A313C7A72467C664B5572644BC979A4D1CEA01417474115716F35E17922AB9A73CB2E5073BFE4B362EF3E827D98363E006430FC32CB066593D9F06C93F996317EC3DDD9A34FB9D6BEEABD0929DD3BD6391268DC5009FBB6AE50938A904D6D1AFC8533EDB2257B35209BFCA5DBA3C441F5AC4004AB99DD4FE0C277D38752158DE673491A410C3E992E25C25EB1A752A58EE6EB8DA2AFED30DEDE118B69328633F9E98D27DFB4C4AB6FA5C1D7B2C415
	D7BDC4AFEF54C924338EE173DB145767A56F189B694207B03BE20630755164A1CC9E0B5FC60F300F17AF3A0A452F64AAED7CDE97F4BDD73138213CB0AF3A7B95240B3ACA55C76C0B77227BC99752A5F3BAD29E6FC337379B69A683BE8B20F0A823EF9E2A257B629452758DE5F1537B4B68DE93F461C3D53D3E765D5D03F4B1C3D5BF3576317875760ABC60F4E9C33D63476AED53E9916721EC4DC16A8D6DA3755690C668268A3AC5E1ECFDC26848BA36A4CCAB6F4AB3C8B7AA0C2DD9624254FCA995F45F76A1DD92
	27CB8FD3731450D907913ACF4FA2DDC698DB9B913D440F3CE1DA3D6385DD6CB036BECA9A465ADD6AB02D5E30F3C8B70353658EE77537E838162E653C487B61ECDD06F1A171D66332C8C23A6A61EC9DC76DA3F9B0DCEB5F3D02AEF4B8DF2F8DD75B392AB7E416700353A58DE7751B6AE55F21EC241B4969660DE0F405A334F44BF3102EFC84DBEB560DD073C175E30620EB994156254675FBD52EE8EFA3589A5338FECFFC0CF4E11CCEDF3F0C6E33CF10AE5A8F9D4B7B2902AEE1842B7FD46EC75688ED5E47AFC53A
	F961EC4D3BA21C51D706EB693EABC43A76F0A6A744106E13E5A21FB9DD1CA15D84C117426926F03ACCAF3A61AB112E72EAC657F035B79D0B4B7ED5E27C381A2D53C32FD665168AF90BD7A3DD8414478224F0F9F957E8753EA8680A2FE1F2F6DC4368DA3C682EAEC13AC11CAE5210EE7F9A518F7172C4FECD63F4EC1E7366CAB277224F8DFEB0F51BFACABD27E07ED8D1718D1D7330E78203693E9ED9E74D3C86777E130E517D601110DBG4DA3F11E766B7938779F5E91AD25CDDDB7475FB38EE5A433A3E224BACF
	AB4928FA174DE996ECA2BAD321EC4AC81C87AC5D205DFF2C72F81C969B5D7A64E37D939B3473B806FBE643FBF0ED3539BD4ADE18E877603CE06286E2AF599B65E83ED77CC9BBBEDF49823D9BA370794A4E45EBA4FF4FD77CBCD711F07F5D3281E392F6FA0C54F275F55979FEE2E20FE031989C210949394C594C36B0EA9B1993A6C2F916306D592DC57D5806F3CDC6DB38EA3920F5F6165A4C1D25DD5D1D25A633D2AADBEDCAC50518DC323A7F48064F03B6DE0BF5FCF60F360E635FBF6B5F1FEB39BF572A7E24F6
	B64CE17E38EC68CFB21467DE077E5CD637BC00BFEC9E0CB63EA5EC2C8499CC76664EF9BC47954045G30396664BA6DDCF372F5EC2FFA5E750D79EC2EFDE3A7D9235B9499E2837F55ABA6C55E55EEF2344956E252C0CACD27A0D2E469D63A974AF713DFABF9DBBA14DBC77A83595D6AF4F7564B8E0FCD31F55B145EAA07495E6B31F9F6354B168E3333677B3DFA49357EC4EFDD5948E2BEFA618EEEF3729D23254A735FE59BBDD38BFD3FBD3B68FA966B1A6B5518578CB767B48330583C5D09F18F871AE4613B49C1
	56F0CA3B4D43E27559C9ED2CF2A3D92CCAA2117EF2C68F0BD5E6636D7F545A18E73A44504641D35B32F7C4B25B5AA3D59BE3413EC4E1E31283ED0C851AA911189B0B3611ABDDD7CA38262AF292BDD6395554E5D7B05DF95B6BBB0AB605A40F1516AC9CE2E8933561869E379BD41B7AC619F342C621CDBFF122CD91C013F2836654910BD11C72EE44DC5C6BC23EC240B55C0871FB1862BD8A2C04214FA506B37A1C13C8BF98E893EFC4BD8F190D74E489BD0F8B3E021F427C602728E7271B740132430ADAF8DF7793
	B7361190200D9374535C6419EA2B535265A9257F57BABD0ABE7B7710821E7E3C9E8ABDC40F22E91FCC4F898F6A49G9D4523D0CF228FBD2B631356EDF06D224F2CB44DC8BC7E2A36F9DCF6D3EFF12D26F768B4A96D253B1C74919BE9B528FB31023ADBC0EF0450BDDA49135475B9237D1D20CD813A3C1FA1EDE797368BC6975305F15E81B4BDBF43783B3BC54E82EE4A4F305E5EB92F2D3722512C5EEAC6237C973B574355C49765256CD247BA915FCE270A96FB47FD7EA9A25FE390A9E6D75DA9742B99F4060FC6
	FBBF6A917DAF60F2EFC2FCDA8F52D782CE9178513D080F815C641B507E39277B577F5DE9117FD1107FD1A86F43B3A24F81179805ED3C2B6C93C93B5FD1ECF5F6414A3D2408DDD762382A14E35B0EF9769E2969072B7C366D2CA85626D70CD15B7620115332A3G18ADE305ADAE20099E033EAD6DA3F5EFF2372DE7F4677A10AE9DE8165C0CF42FA05DBAC6D7F396E3D587B421B723EF57F59D00EB1B2C544BE49BC6369AA5B89F8F6CB0B81456ABEEE836DB0F2D741F6D980FF716E1BCD6D4C7C965B3BB7C46A361
	E69607A9B72B7108197F175989G4C7EE9E7514F72DBE0DDF78B7AF9689C46035D771E4B23F54F1CE13C771FC35EE860CB973CC567B1C66C3E667CBC8DAF9973987F656711FFC9B42C0722B1DF3E12909F8A3804E80C697556A2C95DD353E50B1AB5D446A4A1A393784BFE0E366D1965DD4FBF1F05F46DC0B3CA5095E4F33A8D6C3E1C46385354E36B6C6A94E38F13F1BA9BE52482FF412DA86365EC6FB84C4D21B2EC8E83997BF2D0C68D700788993F48714E37FF64A2DD9C50E45C0AB13BCF600BEF03B542ED08
	3F74E3B11681AEC560EBBFC1FCDE8C2CC1E2C4DBDD0A795C833858983423F7A9F9766271B37FE5B4B7AF756EBFDF913226001C225B319E379DA2F88774FBA67BE1598303B95FB72EA54FDA58F800C93F2B5DE93340DD2759395359D3CCBAFCAA7B32C2145D84F243EEC7BBDFAF7CC05869EDA3635B358C791200C73A8379EEDDC646A88F540C5D4499A87DDF85FDB9503A847D8FAB51D7E63B74D1B94DF1E51FAC3B205BF6FB8FDB7C0016617A010CE2D457381528AB9A74248BDD4BD711366B6CD2DCDDEC5A6312
	ADE8DF77AA51066E04B6F4A7727CF615F75E6CD80D32C301A661CE9C33A22C7A39C6A6149546221CDBD68FF40E406B527E2128CBB5A46AF81DDD027614012EF6214FD6622F7E18DC53B7CB04DCECC2B8CF40B87ED2489E85F2D3056C4F57F867E7779A24AB388B46693BC46CA89E1A29E2B359ED27656215CCF76F01FC5A87F440F8E53792F56C996630A37A73DAA1F72967BF4261F734661EGBB4794F31C68093F0B1DE37AD46093A9BE7797AA1E597BB4DD0B137BDAA82B399B4F5E1CA9A7B1299E8B6D9E2087
	A08CA096A0E5AC2E29CFECA44FAE7BBD5B345781C392ACE16A3D9EA2619923CDA4B69DF2AF3FDF3CC9BBBFE7F1A8EA8F6E2CDD690079185F69DBC86E4EG9F736E017E85209C20699E74B9F2D3C9A01F710C96F71659F38D3527679E63B3BC7E7D0822FCE18366DBEA7367FF029FD94C7FBDDB097F9300A683A06FDE0881400EFB517FD7B7852C735DBD06F54E6C6A36859707C1366062700E5FB8345C5B5FF92A2FBA75FC0A0EBECCF74F4F827B707DD3F7BFD64747472FC692DF033DB43A7007E10A6FD7ACBD04
	7BE94977E2DB0D2CA47BF53977C1DD811483B4G74G0483445D07F537694B2F82569D69F09B1C3BGD763F4F66C52BF4B95FECA0EC06D4BB8BE8B8272255EE7941F4F0B8B0D6353A077A8FBEC325D4A62F36CA4098F1953E65C0771D9380D709647B566D783B48374GC48244812445E1FCAE7DF26B8546072D24FC2343FCFCBA48582485644B0A7366E37E0D58CE7CDBF4BF4CC981EAG2CG21GD1GC97723FF29DF6E8C641FD8C6000FF8AE40A089183D17BB0373F3E4C03E747B3D79D85DDEE5413A4D3C9F
	4F319DF485E7430900FCE58F98714DBD991C3E458179EA9EB07279ADB37A5C72G7A7CCC10B67CB6A0DF04214F173B036B8BC77A70156A6EE38D75D9BC4169BB6943275717E428EF19921C3E55BEFCFAFD69067A2684296FAD9FBE7D5812E9286F97DD4165CBC2C03E02F1C6FCBD5D41693BB4A0DF71B82331214D6EEF6C64CBBC5E1656CBB8F66E9807ED2971D4F0F5E175615357FD0FE1EC8AFB020B4D2A40FC067AFE6743F7E17EE5787069F3AD5AD0DFF4EFF07A6E7561536BCBB054E7BD9DDCBC5D8179D28D
	75ADB9931C3EFF87644B5069E3E37B87D5E4EC4F0BEF4CDF81D089508450879081909F0FE37B6A4D74BC75050E6D7D4EDF9E8852472400FCE971C6FC037A0253B7BCA05FCCC3FD874E8617234DBEFC253A7BF98F9A5561301D248ED7C0598E00A6G85A082E0ACC06A03D80767B6D78D208E834F51B3FC6C3D30387EADA0DF6603C6FC8F1D8BCE5FD381798AFEE944B774FCF07AEE8848D7EC28EF17941C3EC38179F6986AFBA5C8FDEF8664EBB254F7672C6074BD98104FE528AFA4BBB8FDD785648BB554F7A8A7
	B8FD5F87649BE528EFE9EEF07AD686640BB35477E0107AA6AC894417E228EF704741693BAEA05F94C3FD07BF89CEDFCBC0BE69A14331FEE9F063443FFD7874F319A5067A2687296FAFBEFC253A7BB2C3FDC38A0353F73D8F1FDEDF0D213E3D4B0253F74407CF2F2F45D05F5C154169DB6C43272F3F9EC3FD630354770C8F1F5E3FB0C3FDFD2B02EB8FD784640BB65437E7F5F07EB57870697DCB5069E36FF6DCE6523F5BC14EBE24826D8CG3626FB06FE63012C83F393F0CD17D7921C1D9F7970696D2CCCB062CB
	8CD25F338175B5986A6BDB931C3EABFC78747A6CBAFD6C5D6CE8FA06F39014C5GB05C6BCD84978F7793842E739841E5427D3C71D8973FB8CE4E7B95417DC600A600D000A800C400F400CC00C58FB766178154815881C2G4281E29FC6B9CFD0B9C9F0BF63E1F676BB0B3F93D0708870BE424EA957BD424E973B609A86907B88FB761872884A19F0AC832EC4B751214FFBE21F0805C1B901F09DBE568F9D01F4AEEF1106A60A63C2477B72917F0BF8CCC8BCB69A5084834C770547096F64CC6DD2BFB4787BE3BFF2
	16696EBF6DC7677B7C3AD0034B67D77D7B668448BB13B992FB4F121C8B7077CE72C0012D2D86157BFFEF6E4AF263776606159B3FB7B7327C830687995A2FB6E079CF4B7BFF2F6E16F27F6F55CD59625F4EBFEEB136734FDB0C6DFCE7CB7FF666D47833F3992D0B67699994F717039CF5232259FE1B5CC35ED705556861AE87DB2A92699E7D73CF897B0F75740CED46A36C7DAF267B40D67FFEFE3B55584F63DB0D7DB4ED6D5FCF5BD6FFFEB27BBE7C816DA36F17B15F9F7F68FDAA53631159A904EA13E262F17903
	C2725367BD478326CE75DC2952B917536CD61C3B5609B6D8F448E86C4DCDEC4CDF82D012685B5E198FB7D77A0F576D1546713A3B1234F50F334BED1175B1F920F209A8D3F79048B9AA628623F90472ABA6F71B6C9353A7A8BFAB73477FDC253F383398EDF6E8E234DF0D5177064FA9F6C0EC9AG5AF5B16ACB441CDA364DFF0CD6ECB30E512AED7EE334EEDB6098D5EC739F232AED01E3744D367EE3B45B194FE3D4576AF6F6562D6332EF586E5F57286D463E5E3A5D3F2FF7ED8F6C6B835B7D7BBAFEFBE0DF1F58
	CEFCB15B2CB6B7FF0B14791860546641861187A3ACC6F99086759D8590274B0364C4BCEBF3294BBBE61B381E9DA735FAB689BD6F180D7424033C19GF93F7256D378AB54732E5BDB4F17680FDB2B67CBD11757F934FD42FA01EFD03474AA7E532E77346DE39D6227FA6357A37E4EEEAF7CA674EF335F7D20B270259A20C967A313B76414562E4D82DF5F6325278C719F77FA61B7A0FED22F566F8DC24E7574EC2FA63779394803E73C628A99415F6129721F734B2B0CF33E384AFF4E2F2D8A1C7365D57EF3FEFBD5
	601C2F296A3FFD7F690CAE867CDC6738BE9D3E0C61AFB96B959B48A226677A1D7EE3B3FA27F1EC22F77A0F4D1DBB8347A6EE277F58BC34B3F0EC12F6769F1BAF4E69E2D04E70EF69711B997E5173BA7CC606BFADF967933E9BDFB84B3B7DEE6478D7F5788D9CFFDF36B7FEBD4707666874FE497007F33D75A27DDF17F84BA96778C7BE7646EF66784BBF7146EF617803CB3D5A5BE60C5B72C2EDFB5BA8621968055FA470A14B34F8353F5A31D20BD75BFF26973EDC606FDE250B439606BF3BDACBCF3E1D42566E8F
	19F06DEE7F953E9B7897B31191857771G93FEA556ED567E76ACE6687AAF69D16C233717E8E234CB6D2B5E71460B3E756EB5DEF85137BEF404FC1342EE6AF5F6A9F0FD14587A3C787644A238AFF914595070A85B27E0F3653BCE113AEBF51BBAA13569A15B1A47886F6C2348BB966813G26G64C1D971E3E87B20E344F759C1A653D6E994653B7E983E2F96A668CEAA44B67A522530B7FAB979F6DC1D5D29902536D61B452479DEA323C95BCB5E152B8B784E99529F7A0C586BFD0ADA0C95A770BB9364742CB814
	A5F2632F6F4F5714571A4C448CDE76A1BD8F3D5BE1A54534086197EEABD071C2A4BF87DFC3EC29A76FBF89AE1477D675075AB20DB1F84E1A487D0672D02AEF9DB645435B725EB9228C5BEB21D945E1DF704097243FE5EF3A0A33483C2CD94E97748DB685D6C2ECBFEBEE33F6BFEB72E3ACCFF213E06E8F40E8369CB522A903726AA4BCDFFDDB1B76FCF5F39223899165ABCDC6656122FC3D5928BCDA144FB0AC0F9765750672D3C47925067C13C579339DC66519C958E6425AC93C3BCDBFB4743A707B001B693947
	F90F836D63385F94C9F145F0DFA3F0D38F613ED43340F553B301B1F09FAFF0F9EE02CB017BA982E7F6A3EF1640553591DC6D930D79ADCFA06ED6FA8ECD017BC182D7C977A1A360BED660585EA41117A8F04368F92EC5CFC2BBFF92F19FDA503ECA019BE5C17B6A846EED8B4A338ADC1A896D9BABF087A464CD9238395D481BA6F00F39882EA4996E13311FCDB4697B5906E435EFEDCFB6583322674BBBAD248D77A5337365F3843E176243B5F8F66E7C6F341FA777B1D0D670945AB4DD44CC9138FC1946966EE31E
	42F7CA7EC63F31C3E4A6GAE73A94C1DF5ADDE67C4498B859595DF3BED561D2636DA5AFB33F1E6EB9361AFFB9AF2872065E9FCBF39675F78FEF24C4D232514D70C5FC1CFEFB81B5D73B40BC5A43F127720B62EB26730316D7223F91CAF954A8B1E216F539C5566BD69AB5E194576E0D979D42FF2521F12B1D7ADDFFACCDBCE6C1D1E2DAD5F6FD5CE62B10E73B35F1E7C9CFD4BFAB2D6325FB3486FB7041B1EE1BE7571EB5153574E8A0D28FB173FBFF40C3E73606E12195CD127345FA6AEFF6F47065FD722E39D17
	17442F037FFE6DACD6EFAF96AF722DB74DF7842A1A716C161402F305CBE99D1643FD9DC0FB8ABE93B8F9946795A102F6DD9D210D037B9400A9A918AFEF367A7906816D3D193C5F3521BC69572823C54CDD4A854EE6C61CCB60BE33A0AEDA602E903C6982772060DD72AC62665A716CE75D33B8569FB8046FCD07BECB3ECD7D70B712A466AE0768B7DD1D8E85A67FD5EE593447E6A7B387094F9D1EBA04B9198C3C454F911CF4389AF37AD95F36CDE873F95C090E19C013779C7B16B6313570B9562EE7899B061CC4
	7B184E1BE85F6B823A3E675087D9A2F3AC3349525166A6AFE922FD6BE87FBB9868A21EC37BFA5DE8DF5CF33E7665C8F886103F0B8AB469G38FFBC93FEBF6EE5DFDB5B9C9AA3FBD7274343751E2375CBF82489E863F80B379FAF50FAAC0072C2C1D3EFB576230376E1A5C0D7BE817DF8550AFEB4CDB076C38BF6208919207A91BEC16FC73459570F074C6847A820E7382D546ED8380F973847AD9867E4E15F87423E99FE6C1B2331CF1A689D67C593D57B180E594216220928772C04FA4BA6225E22EE54DBB751B8
	7F66EBF4DBA7FA67DF4FC4FD7E7D174BF74E7CE797667D2009B8267C06364188380F9138E3E2FCCD19086321070EDFC54F43588C507CBC624F33716EF976DD16C8011FEDE95258CF687D2DD97C816AB2AAE3BA7E6925E34EG65EB5B0DD1D97C7374DDBAF9C9GFED8E74A3EB938C546BEAB77850CC9EE53C05F45E2FC9F8E180F4DB96E2463422297545CE078B80AAF526059DC222A896792A5D09676823EB7B2E69F0941CC38AFC805FC855881508250170AFB4E9D3BE0C9597F193D86272BD66E166D1AC7937C
	9C5351017A47465B71F3F09C9F7B402DD25C2555FE3F2192164A72A816DF63122F1DC52E4C3F727D443FF4A84B82A8FA917AAB00DAG6B0B685F5D3BCA18FF3B2DB625CAD1CC16F67A059C713047A61FD251E42D49E49FBFC072208764048BD9EF6FA25FC9EDB47D4023C4C4BA5DD492A06B5CCE176CD6FA9916FE090F67C1D633BECEE3DF64738838263D68EFCE7AB62D570C9775F910F3146033DE546741E01187F9AFB5665BDF427C6D7629271417D41D13DF7227FF8C1DF3E43E2457FF8745677EC62FFFF39D
	6AAF00B265B7287F8FEDC6F9127A9B26F746EFFC75B3BE59E7AECFC0CA634F24537C71B98C77430BB9FD25DF3EE2437D7ABACEEF754BB754504E9ECEBF58AF5F4306F60E6274E37D72AD30EB7912B8DD9A3FE670EB6EEFD5FE566E1EB92E1D6796DCE74F467A536BD942F9D77CD6DD9B30BEF372A1526F361A6C9E722C104D73569D4639DF9350FFCA69A4BAD632FF995BFDF1124466FFDF124F86F17BDE1E077DC261EDB7C955AFD4E7FF66473EB0EED73A463E04160DF3194CD7486703F99F1F6B06A50634FCB9
	6AFB23DEADBB66A6F3F6715E76AE275D69AEBD55EED3E466572DEEB1277D2F017A357ADBD54F55ED448632F7474A8EB42AE565D6EF7B469ED14B9E3376EF5FE2996D2B9B30FDAFB429FA1EB7FB5B7776D135AC520276317BB7AC7D5B34D31CE78A75E39353316C382A23D068E777333A7B57F1289BF5A4742BE3D0332AE35EC9ED5EF2DFCC3EFB22F34DB8EF4884598513E0CC81281D444E6130B95FC98B4E792C130C67FCA41FF14E5783B403A729F33ED11374F33EE47AAC142D5874FBF1316708EF6ADB227A72
	51E7BF10543D3ACE395E546922FFDE03FFDF7697967DDE9571A3E6926EE5354AC6658913F0BFA16904F64DDD347A20611ABB7344696C89BC96191354B69983ED1259D9A7931E0D9276E177507CE33FFF29797DF5D313645B5E7FD3404F53DED49DEC2EF047BE7F4F336E59E77CBCEB5CBE6373A1637775FF9E6771FD7E4F63246C53FE671A4DED2EBDC16A3CA43DB13F86408E9082901E0E731C6FEA493EBA794BAE3E73255D56B6719D92F1CEE03FFFDFEB76877E9BB0077653F528D3D11C1DC4BE620F6CA77525
	B8DD9449F04D7BC90C5C3636F60511F23CF9BFE97FF6391522992E654082ED5C58140C53379DA0B1905F33208F9758670658DAEA61893A6E0418A426637AEA78F784B7856E2517459E2E58F3DD74B24EF7686766718C2F6677707F055C3E6D226AE0BE8F217BD3D67037C744E15EF10CC3240843EDF52A6FE3B53F3F2A3B78F1E83C28BA184FFF237BAEB3405FAC9107616D9807025FA12E4F04F4AD82B74FAC7609856E920ACB047B8982B7CD50258B5CF63A4EAF7BBD5C7F9EF1D1E26DDFABF0B71B10AED26066
	88F9518277229969B2852EC2484B1D0C384F69FEDE835C378B5C41D63AA782771182B736D5689038B80A4B047BA52F607A745E6FC8CE3076785A817277A48C57D6506BFC83F30103669FE40B52E0EA93F57C65E16DBC3884646EF8257F7D8A2677656F49785DG34AE00F00098008400D40099G522B501E81CAG2A811AG6CGA1GE1GD1GF12FE2FFF926EE25BF5B8149B76FE92769B659C837096FABF93DDF1E8C7252DF6D5FFE7E8E2E2C651B89BCF9FF38903E2F3D1EC19782CF598571B5FA3D57DC8DBC
	CD817858FAF01D995703F6200F7A836ED9EFD8CF6AC137D73E476EBCD54B27E2FCDD51EB46FD2F34BF60BE78ADD46622A9D0B7G55D3F02FF9345827EE1EB250397003A6C9681CAA64048B59B708F9F672944C5D11F41E18BB35B13F88209A2099408590BA9573A22939381F3CE04E2AE941F8FEF6427FD89AFDA270D8FA979D37E498834943C7063B77841967BADBDBBD329833471DA890383D1EBAA7F9F60EFF3FE155895FF9F155891C97C7CEE5E7854642B5094043381F7C736F0C77134753F1B39DE8E7CC45
	B9745DE2DF5C88F05F9477EB67CD03B8CFD377EBB7CE534F21230E7A5ABCCEBC0B5EB10D595C84D77BB4F6861F597159F134E350B463397C7B9ABBA200A6C6E3C7020F9D7FE8775EF7DFCD4703E420CB1D06BAFF5E0EBAB37C6854BE93481D4E762A177232A553D51DCC47985A4F2FGFC59F4611799F554CCB756215D8FEF5669F0CD577B95E976FEEED3C69E4BC8CC57AC319E6A7323CB7BECA08CE822262B3A62FCF4B5DA3CF5554B2A2EF1C2D722012E59BADD29C0B383G779124992AAEA67BCC1BAA7B1FA2
	E673E6984B564AAF1A61ADFB230F6C9B5BD55907045DBB7C485656C703CEF67B8CFD0CAA6DA45F4DCEB7BD3446723D5E0E792EG7DE000B800E42E0F713536FA47B659265A78E4AB5AB85940C6540FF6E6828D81EC8B73DE535B78CCAB69EF5B1DEE5BE927C3B1592B6C36B687D9CD703C1D5A0A7D7B1257F01E301F6EA555F0D98CF71862DAB80E3FB3703D7FFEB26B7B407D646C9663B5C7FE0B6F1A232045F84D71D10B6F1AE3E98B09C59B0C79CD022EC8600E8A5C67D4A6419D31D915F601DFCD6DA2789A19
	A894E73929DD27A82E09633E54600EF25C5696129760B2049DF9F73410BAEF35596D6C3E1A52589C50750BF86C25B65ADCA60D5D87846E28280BC75D3E757324DBDBBF8C37C17625AB17FD693E337A52D92C3EF41D5D3EF46EEEAD9DBFABEC38279AF80E33BBC83EF38366E3FB68CB5973375774FB6BABA8BE74B57D5E7A6DE2AE958EE5192F619A6B962FBD6B6657E18E727A054C69DEB75C6B9E44F9A38CE4B03E513291DF8C27CFB06063FF0B486C3F3D5EE28E5CDEE34D466D75DE33EFFB9DE7B6EE2F634D5A
	764A62EABB01F11DG364F7BA34E7958BC0A602B7F08F34FC914368F6E4381E2GD89F3A1E7C49C65E67053BC91F275B3BECF31BFA25014DC93F35CB226EFFFF94EDCC841DD94246554750460D99E863D17ABDCCBB5C8F82084CC05A0663B8079D1BB15079F1D4277A7BB1076A6B60F6725CEEB959F6ACA227A21773B974288E54B5430FAEA6EB1AD3157B71C9D56E3B342E176FF6D80B086C65B8B7EF90E7CCD67C29BF3915E42B8B3F19242E8B7C7078777BFB0FAAE7063AAE98301C9FAB2A1C03420764FEFD18
	5625727CD8684EFD23BF1E792768AD65B9C37F9EB409E7A570A8EF605AABFF81F997DC7D7B9274CF236F6EF4A93DBB69DCE227ECF2205EC768FA1F4808F983736B8E19481D8C77D9G0B5E447C4AEAC59BCB5EB43291E3D15623563354C65E43539CDBAA2DA77F63762227F4576E838D3B8FCB62BB377FA63A791E9D9DB6795F7568192F62F158647D553FAC12783DCA737BEE73455F03CA3E28BA589878CB0B6F38780845F77C1CED45BD9E6B1BB857EA95E795DD82F73F0CF46182475AB3219B75A60EBB4E9341
	0D17E703643BF740FCECBC2D7F0E64CC621B7AF1361962D35E540F332F0871A00D78BF937D1DBA60338E0C6F3F0364ABBB961C3FCF517EBFEA265E5F67A9BEF6265E5F6F0F213F71D0D674E7747768F12D7EC1000F7873054CAB46F4E879E200A76102780EFA4D23A6G4F148B625B6CB50F49821E0237AE046FCFDEFCAB00277C02789A3C6C2C851E168B623B4CCB1F82BC21974477A3FB20BC08544961E7928272693F89427812A8DFDCG5BFC73EEF9AB4923643774F9371A62D35F52675DD89973EEB214557C
	B7665DAC2F33A2B100CF786F7EED61FB8464CF027233DFE72D7D1D7DE2745D27D57A7BBA7A236779C9772A59EF336677749397FF9C497D6E626BF86E186AD31A6637FC6C626BBEF7D1F530B14CF55CF71C6CBA6EBBCE46F778520D6F7025F3198D6419FD698E980C4F758663F31E41BAF83E41BA3843C02F4BC06F0D86FAEF72524B3E851030104D1D6CB607CC3E4877356C907413687CEF82343FA922FD96372A3931CE737B8179FFA1C7AF2A8E667312B65F38ADEB53460D3F23E5576E2130BE655AB3A49E0BE9
	F9240BEC26473CA64DD624D57C9535A24D1F89AAB27117540A1C0EA297FE95314263165E970B473DD4FAFA77060A1C5F5F1413AD656E052591DD816F15F249B2F62FB41B2EF277CAB930826EF10166DCAD40B49884AE70DE0D4E9A3378A785C3B7F4E5CBF9FB24F7DBCDF6D73B898451BFDBD0AB651B29C4598D340AD4C07F984FDA60DDCBF93756CA59960FA2ADD5719602E72575AE13C5861239D6133B03CAB8A91D13E69F1766363A5B4CD401A2E59F5FF39C5452BBD2BC27D6FACFE9A7E78A482624A2ADD6C5
	571147A23CE0AB75CE7ABB7CF351FF9E42BC4B48E3D210BB90E21F0201E09569BD7A8E9EDD2AAA529C0FECF1BA2C9E63DF956CE5465EA272C512A202240FDDBC108D56A2A77D2B9EA63BE11AD4E85C8A8989513965754F8B0BF6CA0AB4D7AC547DF91207CB6E7F73307CC782CB8EBB9549643413A24D23A1733063CD0263BD0D5F6F61B942156735679CB3AF3178FD1F27B00B4DAD4A3254B3CB6C79DE2C076E9581BEB48B1F67C99E72F7B07DBD232E6FB4596D0A495ED16530126755BBFB95D9BD23F30F074877
	A08DAFE4E571743B8ABFE8F87E87D0CB8788FFA76AB094AAGGC80EGGD0CB818294G94G88G88G560171B4FFA76AB094AAGGC80EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCEAAGGGG
**end of data**/
}
}