package cbit.vcell.solver.ode.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.*;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.Function;
import cbit.vcell.server.VCellConnection;
import java.awt.event.*;
import java.util.*;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.*;
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
	private Thread savingThread = null;
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
	D0CB838494G88G88GD0FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFDFDFCD45535E8D4ECE3C50D0A9A915B9422069AA1FA63EDEA531676D2454AEDE90C572878FE2834CDEF294DFD65FAE9CBEFCFA6C192C0089AB537069A83EAA2B1A6A40490C28879A201G01C488881AC966A4B349FCB1F37285815E5A9FEB1FB3E74EE4483C725E9F77727BAD4E1C355757DEFB6D3D575EE71F93A9773D28B56926DCC9CADFA371FFBB53A52960C6C9FA6DC466AA44D5FF1414ADE97E
	3D8F703AF4E6BC932E2DD096961F141DA36D36E6417DE9384FFCB0A97B9D787D26E4691CF59D7C8820C70132F52B8A4FCE2E671FBEC5BDEB697D5F7AB640F5A3C08F6048B576A3223F2AAF0F6327F1BCC23AC912E694911A23FD799C1786B4D9006BG3094B17A8938B2832E2D2966F427EF0E11D67E44E3FA0D485148C4D84AF925E2F6AD107E189ED3CCF41D23F6921EEA38F783104AE7C83F6D2F01EBED4750C0DDDF2FCDA99D69136D76D22F4BBEA2FBCADD9639F4E058D6D353C96F3B4C5E21A6595B673139
	951B4B59E1F64AF6C95A7AB1117F233EACAACF12D6006CB80A0B9BA2BA2D70BB8CA072E344FF57C970AB617706EDC959DB37E1BD77FCD44B6B51FD7E5BD272F6C76626G752C5D466AE7592656F3057DF953FF51B649A8A73E0F023205GA9G2B8172CB1232EB815E2531302E6CE3386E6BD59ADD8E074B394F6DEE30DBBC072046F678DDD383A59CF7CC6EF3F9AC12445ABB2C2F44401E4900715DB8E5BE6613188AEC7B58E37324257FF03BE92D01CF16461CB7F515B0DF58CBD41FB05D0F8554BD3D44B7B6FD
	F51F10343ACB1EF9AAE30D016E56C71F4A0862F262B53AD7151036AE321336CE065F040F44AD436F24785ACFD4BC0B53FF24B1C36EFB20EC71A7989B5D9B5116764C59D2CE799D06FE087C51F4D3F5A9332123D4EF4B5A33C476C4A953793930E58F4557FE2A6219ADBF24F1CA6ED7C3D94A7624EC96DF9BAAC83F0D5F11143D84A095C02ACA4AAEG2885682842986BAC7B74B2E32C436CD19A06EDF60B6C11986F06723761AA0FA99EF31B532B181DFD72F13347E66E354BDE09533CD5CEFC506B4377D79A7BFD
	20E33F6C14BDE656D1BDE6072C489EAF2359D27EBA21714834344B6610C10143AD215CED65EF12B8B2FB154F5C9633A22B942C3C221C34490879B8C88481701B79E5D7A571179B6A9F8E90A97CB00F4ABBA8FBE03C2829E9F73906065DDD24A6D2EA9571737F22E3C7AC7CDED805BE5E310360CAF6A6E5B76DC4B93FA8FFC3A3274956C74CB7FB46310F155AC95D3B015E8D908990873098E079CE0C23173BB10E1ADEFBC0AAF96AB643B8CA04184E2CE67113DF2D6FCFFB289EE2EB9314D9G2681C483AC2C46F9
	A1F128A7C87FDE4C6D5C48ED26714B2FF1BBFD7B65721D467D1469FABA282E75GEB786F8C8DAF76F5AD6DEB81E430F619D3C55AF8B954D35A85B13F8B5B26E0FB810B758633D74668BE62014014ADEACBB1FBDFBB9B4F6DF91D723239F58B4087G9F819473FE779640DE00CE922FGA7810EC8E4E402B8GB88A5086308BE0A769AFGB60081004FG483C5F8CC07A61A7GE7G3681B492DB81F6G8C81C088C96781AEG5C8CF08960AA00DB816E81388F40A17C5DC86D2F063A77G0483448124GC8B5C959
	C5G9DGE3GD1G0BG52G72F6A7E55783D881A2G92G1681E455A6E51781F483048144GAC81D885D030A7A93B95408D908930GA095A027AEA93B9A2087A09CA08EA085C05A8B7A81BAG46G22G968124816455E3ECBE66DCE2D25B9563C28F58B6EFF1BF6A4B0D5AEC9B7773279A3A9D3CEDF6E9F0FB397F9BF97BEC646DF8107FBE425B36133763895E761F73B6C4B9FD3C4D87F83B8F71F6B22A0F360D31FDA76BB3B773768FB6CFE3FCCCE5EE7F9F787B830B6D0BAEEDECE78420DD4369C9EC6FB0A8
	A7E3A299B349D838C9E2FD01446CEE49B70EC96C1218AF5660C8DF4171E5A747117E31057FFECFE2FD060C2795926BC71BA5565776E86410F1157425F20975277D920BFF237AE05FB9A4297DE6B2DF117E7559E578947B5D7F6F367D6F8EEC0EFD3B024422BCA2BB15A659ADBBAD70434B4BB6D710186579E23FCB70EC21F8B764170D1050F9853E38024434D9D1BC365EE1C56E149545669CD04B4B29AE6FF02FDBCDCDD1D7F58569BF2ED148AF857D1E8A121B75391CCE19CE760205176F253921EC179DE0F423
	EB5829B0FCF3A5D9CF8F4963BCB7ED2BFCD5A23925FD186439EC0D9AE7A6FD516D31B9A005A0293294D1CF7246FF97EBEC827500A3EBED8BDC476AB15F9B5D02795E56F3F32585DF3ACDC6EB1848FBEEB22554339CEBC53D1A6FB11BEAF6985BDECFF3D40E77B2DBEBCE905A8E0FBCE213C7698AB90352BE56CD568CDDAE0F73C8FF3F61F21A515D55CF6CD6A0636ED4E4F739444630B7257DDE529EDD9C5FE26ED3DC107832326631751C27810C33F84BB97E256D2FF39E06C7AE4C65166EA436EE5887F986C075
	BE4C6592F710F24C33F9925768325BD953C2D974BE627FEB6D395CFF9DF0EF5D477C3FD1147FC9142F007B4C8635DCE9A065E79DD8CE200C1777401571BFA74A9381176C47FF54E9441F59087518DB15C3E2D8BEBBAC437209ACB88E0AB80B2BFAC3D7F61C479CABCF28A2BE35EB57AA0DC4E754D95C3349037BB2000EC626F7FAA333EDFA93216B73A0DDE593DBA3344375B440C4930F2FE6C2D76EC53A14A616CF26F17ACCA8AF9572D688BAFBB3DBEB90F9531A75729ED210AE3A196525C49E21CF7411E79FC6
	3AE40D3C95FE72360FA05D0696165F223C2DADBE76893A5A96E6D7D78BDB9FCDEFE172187F9E9D957671724496663F64962D7F4246102EE0BF739B4A2B5E2F25BBB80EF46D7BD5FD44BE0B8F5DDB6710CE61F4245C62C7771C201B8E78E800051CBE33D5CB771D73C837351579CDDFDFC6F7E9C27825D5556BEFDF7385246BEAD56B29350F79EF7DC591871CEEDA2B2F7FD83BBDA66822DB591A03349B5AC75AEDF5AB21BBFF896992DB597A045011F5ECF22BD65EE68969DA5B581A2527CD0D17D2C177ABC16766
	F461EDEA1C923A8D87885D0369C897594656C6C4AF29C7DC1BD6EF2789693A8F3075117BG6BF7E18734FA8BB310EE86270BBD405AAF51076EFF8B3A6483ECDD06FEA17ED67DF2EFA6522DB84056F154BE928787FD6CDB03F4E58779FA6D205A4FD53D1BDF957560F46E032CFD438EEA69DE96F433B8DDBC27DB68C3770335C8376CA0DBEB2EBA284603DA0F6B3210AE2B1D2DCB0D5B777E77C4FFEBE7EBCC6376BDDB00F4551CCE5F3E0CEE677BC85791000E457D1F851D351D35FFD8BB2BC7F43BB66E1794A2DD
	FCBBDB73AE66744BFD68BCDB10AE679013D3F448082EF42B08E7CE57E3C817AA6846B8DD84270BBA24253B378C6916F33A55BABA6697F33918BF8E33F5FA59E1D5EE291037EDBB5255C3F9B74058E15EBF8EEB757ED650AD61F2D2B9DD5691AD5D5C8A24ABB942681A0E985119AB45B8444B6D7CBA0D5331BC677745A477224F8D864DA36631D26FA8640FB5B587E84E431E89FCE9A17991D9E74DBA02FB7FF78D507DE038DF81105901F95A4FDF433D7F99C3B1D256334999011EF164F7B0BB6ABB54BCAD7E7651
	8C1653FC380B688C0332088E4C83AAF6EA779F77F93D2EBE9B5D7A64F37D0BBB35F99C437DEA67BA38765BBCDE258D926DB14C8BFE3B1358CB76C6B91A6F95475A7179CAB468DD5A014FD79A5E79C48A74FCA540F39589775F7BAA50A791676E11FAEE1BE9DA9F40A739C719AFAA0F2ABE19163BA30359D66858A031BC528E6551C75136245D8513580639A6234D7F38D0503A9C2536DEC769700823545C2B144A961BD2D3A312CB5676B18EFC9E3474A836710596ED9BA7EE3A9038BE47F8FD0EE91E6FBC7C8A2F
	0F490975D120BC769856677E0E42A075E1F9B05A986EC49B17038CA67BF17AAC00484B6F04B99D00651A273B35396674CE36D79D8FD716EB17F9489A5D2648609B782FD3B1ABF223556C9C10ADC52403141AC741D3E469D65A86654D64D7B16FCBC7B3BB606A16BD7DAE0F23D3F6FAED0AED44260C6FF31A6D63DE1B3751AA778D753A463EEC53CBEE8FA4FAF771F1667359EF56F31B974F1BAD55DE7A42E474CCADFC135534281379BA2DD3757932A71FB72D84E03E196EC13F57F601DF3A306E3F7710B51CE235
	F9192F4CF5DADF45F6B1DFA58B7AF73D149E962B4C46877E2635B14BFC2D210D53FFBDE0CA6DE23665FC265A581A7C3C69F4B25AF84A0BB6F683CD44E7989B39F564CA5715922E291E24EBAA0B5CEF9E36AB986E3C6FCD1CC41B429647CBF9EF5EE0E89301C55C16B40DCD39AFBEEFAAFA91EDFACBC11B6A0F43B8FE9CE34ABEE064273863980B4F893E0500DBFD9C7D57B5CC70DE85D642B0669243997DED43C8DFF982466D9328670F12111E68932867739164CBGDC5A895413BCC246C059E961DAF02CFBEB94
	7BC8D1B74C1B5DC8BFB1CA1E29763B7A063D25747FF617D751C7FF8BA960614F5BE15EA85123E87AA75373BB21A792F4AC91FA0E0F6969D99B1F355441F518BE3352F4A371782B4966F55B4D63C56D1A5141E1D62C250DAE7A080D749A54BDF78CF5E71D043C78A46A2E9C4F125475B923D54611F68C6862846DDF4631DFB03A3AF36867D420A9B8057E4FBCAFE296F09127305D1232346D366894EB3734D3A8FF687CF6381A69223C14DDBA58A062BF686C23453EFE7F46F9A25FEB60A9E6570B6731DE191FA7E5
	57FE0E7646CC087197F031826F3C00744B8117FF9A711597915F85386927517E3BAECE2EBF6D2208FF60D9AD64C5DF92F1FE86627C8C767161323FCA5A7D0EA20BEB98D66EA5856CDA0C732AD20DFDBBB669E129FB704600FDBB7A8C6B530B4F28FDBBA76D3135DB8118ADAD925A12778564F3DFE05D9E10C85B1BBD835B99DDCEBA5205834D82C1B71A4E694A995D3369682BD5C0D376A556ED6670E1388E48CA27CC36E1E40BD1007379408E13C3E9276201EE3B7BD4F160E8C7FF7CE9B37AE3EBD334D43DF2A8
	20BF2CDFB2BFC4FC297AA3B3FAC7C6DEB4669A93A62C67B220496B41FA3EE4C2FF307B7599D9346DD9E5986F418C646DG3EF0417BD0867A085DBFCB6910174CF90C7F5DCC64DFG3C2BFAB0DE1E9578B2B32C054D68533BAC85123A27260B9691B53CDEEBD0C694702718C53D567836F34B2BC817538BEB41DE918F2FF23A8AF67F542B44478E73184DB16C90F38F13714EDA1491867C89C24645353EFE38F7AD15E1F39A48C895B252003F288FE59C49720D370732102E87E8A27B50E7496FA1FE896032AC081F
	D9A066B2400D89FC4F7B080F83DC2A40FFD80871DCA043BAC6C6BBEEA8A44FDE3C8172DFC673C8216F787987A1AB8264AC12319D6B0E923C9346BD337D186C05491C6F9B3713E7ADECBE4020EF343AECFDF06768F5B538460AE040E7326F5A0232257E246C6AFE34533475F5E1272F0D0C6F44D664F383CF0C607B7ED6B2C7F921E56CEC294169DF7FG691781ED5E00E85BB22CAB33DDFA3B1A4638F2D0165DB0ECFBFC27ADFEC04BF07DG3398537533B2545581FA42052E2AF252F7DD430AFB1825BDEE398F6D
	7BDA3968C3C03FCA707CDB39EF5CF4EDC759E5D668D3D61C33765B7439C69414AD34221C6FEE1FEA0E405B527E06E8CB55A56AFC7DF6855A13823AF2EC286FC2C52076E3F24D877204DC6CC218A7209F13046CD6109BA6E46F2A740D4F2FD5A2DD825024893AFF23F868260A4DEC371D130B0A196E71436B6998B08473155DC2AA431EE1DAAC7A73DAC5036A790FDA785D2139A7404EB1DD3292BD16C1F60E69BD013F0362E3B5F8E66FB769DA1C5C2F043234A1BCFBF3EBAD71498ABB74FB8764A9G55G5DGE3
	8EDCD38FD713E717131EEDEAF3421484CB18CE1F07C8F846E897714D10BC4E6FBF5D254D4F199F9E3207F656EE411479D83DD76DA131BB836A98873088E099006444BA476FAA89D6E7BC2345AB4B6CA9F2903E822771991E40752822FC55D366FB4091287E56G32D87D3F58CB6ABF81B411GF1G0BGD2C57D7FF3D750B6EF9EB3ECF3E6D716B3B4BF3C6D8C4D8FB7B88379A14BB559F92A1F1D5573656B682BF5770C6FF9BF3E3F696ECF6B78787CF51C7857E0AF0DAE7CE11A627B9537F663FE1A62423E9ADF
	CF766BE261FE9140B20055G85EE485781FA5C58F607EA7684EDBBB260F6399A81576AF28DB56A1F650AFA5E6289563F0C7D939514AF4CED641F8F0A720D7D53A50FA9ADB659EEE17E092CA37EA1B9ED249B7D332D0170AE017B95G19E741B7GCDGBDG534E22FF3EDE337BB27D43D6D27E5EE1F56C7106669BE7D03E68333EFC2CFE77B7127AA5C2D98AC09A40868F2C9BG5A815C9E2CDFDACDC3307A09E58454914F859884813377DDA534FA9687658B77787231361DB300ED9B6541F3ECAF04E8436F0272
	25F80C786E9A8ECD5F5C20FCE906F5AECF47BAE7F9314E661150ECF085652B779A713DBB9A5AD8D86C47D72A3B6FB654776FA16A4B988B264FED28EF3E9F5F65697BDED0FD61067A4E0D0726AF5C0FCFBF37C4996AEBBA97DA3C9C8B4A17E028EF5D795074658565DB62B51A9B866C0166CE3E4463FDA9BB9B674ED45117D27DF4DEDEDB3C624727EF7B824508EF5EC4E83EC98C4EE728CF3990DA7D2E76635347DA07213E1A0B2169EB7563536B339A6AFB450F6F727C19F9A9E89EE2286FDBC1790C75A584650B
	5469E3F3FBF1B3195B6320ECB140F2GE9B8A9FBABC0BD00E5986776465D74BC75654E6D1366AF6DD2E8F5BC9514EF5A30915F5B6921695B9A14EF16213EE5FEFC179723AF7B71156A6E63070D5AF0EE8BE94345D0168AA00DA4E5678354831C86889B41B63CFD773EA934E1709C7DEAD3E8FE0D884A97B5E244F7B4A3B4FDDF8665CBB05477FEE6E87ABE8D4A3744D05F3290753D9C14AF55D05F55EBC2EC3F20FC522891DF7B2BA1767B20FCF9067AB62F8DCD5F3620FCE5067ADE8ED15F6A20FC2D067A9232C2
	5377E8D03E9EC3FD4F3E971A3E5420FCE3067A228AC253B7AFA8DF04213E7E77C3532784650BB154D7D2985ABCD16347274FE596986A7BCB087ADE5B92ECDECAB15477CDBF3E4B53B7BF283EB4C3FDE7360626EF480FCF2FAFEB4CF0FCA98BCD5FB6BFBEFD7B95986AFBAAC4FDAF7971696BD7ED28AF22BC347E707520FC9D067ACEED8F2DFED6BFBEFD7D2CBAFD6C5D0E9B2D7AF7BB485907B0201D814056F497EDE4CDC7560131E338264B2F884D4E0F7D7874F6AEB774CBCA087AFE99D45FEA63F62F8CCD5F57
	7D78747AF247FD79583B596769994E92A82B87E0389F4A84E7017B89012B277BF3D1F09FBF0EEDF12D151C77DB8477CB8124F3C959E5G6DGF600F000A800C4009400D5G396713320BGEA813A4F231C3B299CB75C4FB84F4EFEC773F7829260BE65BCBB273C6ABCBBDF1CB71114DD8D50BD411EBD0ECD201C7B865260CAF4939D7A38A77689DF9814932855613B83509188677292B5F25B796F32F37EFC247E44D7D15C9FCB47834B1E8A70F823F5A7E7EA5F8DC0033F7FAB001C4D3A7B77A6513909DF5F5460
	56732B7EFDF382641D498C093DE7C94E8584FAA7F92A4056D633EA833FB7B72756783D393935466F4D4D2FFD1D61C106762B8DD87E6D5A495F2B7BC1ED60776A243A40F62E2DB3367335BAE3BB5F2C1B5C4EF76B8259391936453F50F3BC1EE1A7B96AC6C5337DB6F90C3C2F8A2B51E343CE36D4A5523D7A671F920E9F8B69B9604889767E97535D3FB7F0BD9DFB0D6B695DEBDC4F713D1357732A3D016A496C0BF622FD647DB2D677475F5EC4E5FA3DB2BB0550E4D64C5CAF3FD2C8FC7A3D67F8586CD04F153E6A
	4966B44D0A2B31DC744148EE2339B796F4AFA07AA77C7BBB2B43776B837B6B4775467EFA221E74F52FEB5853A76BFD12D41FA74A549D84F20E0AD40351BCC7799533E7C07673690B141F1585627FCDFDA03FB39FBD2E7551A155C7F7579979A895FC339AA067026EB949850C299D8D01FDD45BE06C23060640BEEAEB886E23E38D01FDF432A1380FFA9BA677519D5E755CC79D7D9E1723231C4BFE38B1F0DD939B0D6B7A234640F5FD3CB1F8DD139A835775194660F5FD3111542557E631F978DB242C0E4794ED9C
	D408B80899B00A03EAE86FF6009EDD9CA897702CCD5E302F4FF6F0BDC943DABDBB041EAA43FD44F010B78BA0CE27E7215093B9622B278A6BB32A55D3A55A32E4CCBBA6EC9778DFEA7107D47C03636B347D239C71674F796037A3FE5FF99F7C8E2C5F466CC079FE8A54E58500F451370ECC5EFB93DA3BF68A7CAFA6FC7414A17E7E8BBE788A441F3D282DF705107389BD432B09EDFEAE7205CBBEFE0508606F70B4870E791AE663182FEF8E9C737B1B0347FCC7F36018BF519CBC66FB1AA76F5F57C8BA9F70F31D07
	52F578B206FF4764639B08A226679FDA827B661196E35FACE8896C1B47DA027BE6C9CBE05FBC5D925CB74BDAA6774DF7CCBA9FD4B3FCF8068E3F13613BB3F578CA6E33B53E71046F46FF47975FD109ED722AAF3E02633B567A6237F37C87D9BA3DD58C6FFA4FD7AF523FDE60AB271A631F5661F7F27C5D6F7B62F7F1FCDF21CFFF5B09FE2B5A226DEF15421FA9BE789D82BFE32B962F0ED7DDE5DA3C5A7FB714EB7155827FD8394E8F3B987E666DDAFA726D9436F6FF540AEB775C0B78EEE08CBD0F588E77960009
	0B38EE3B46B6591E458C5D7895F39147684E8A0D0F9A5531EAD3258FDE0C2D0F79624558FA0B05FC1342EE9EF78DABE06FA57AFE088577A792C1DFB237E175C536CF41F26587C7C95B75FB4C8E88EDFA48B60D72264848FB9A6E5DG91GF1GCBAE216DA786C85DE5A7C926ADD2EB98FD3FE2G5FD72B8EC33A338A310D3EF4A96C0DA9A45F0E6B303B94225456EF6BB3EB3E5748E8163611F765BA023EF30674C73790FBFDCFD1E3598DE770BB9364742CB814A5E2634F1BDE5314371BFB09993C6C0D4D244D1B
	1D96D2CC0B987E4D3A9C95AFC472B922155852C95EFF92DCA86F1526B734E59AE3D879E0B111FB0072D02A4F1CB6454B47163635228C7BEBFE2F0A433160110F48F84B5EF495E711F959E9F93D206F32A93092E27BD9CBFD7633260731B809056B82GC67313BEA31A94382E884373551FEC571E2F4E3C0A5194DD05657FD4E4D4DEAB4A9F339A15F7087277870C4AAD227C0F0665E322BC3A5F28FC3AA83F4AEDD49EF595761988AB717708F930EB5C0D5F871CCF4FBD46935A2BF03FA977A441AD017BB401B31F
	44FD294C2B9197CF4F84F6413DC560DA76925C985CC788DC54DE640D963824F202DB8977D957A06E87348F6643FD09409D25E3D9BD5CF78B1CF7886559856E51FE02CB047BA582F7A89D6DDBAEF04F2523FD2B84EE078965ED18063833D6346F3440FD100D3CEE016BEDC05EE98237067AA5195CCF43F176892BFE1CDDBDCD9DDBF32EB5583322674B736979722D57327365EB853E0862EBB5F8F66EFC9E9D6749FD9714A5DC0BB64D34211D79DFC15CA394D78F77DDDF41F7CAFEEEA17CC42695F0D1DF4158A96F
	71B9A7CADEA82829596F31D99A4C836DF474E6734CC6197027GDF9AC056D7717D64318F717D6458FBE7CB2B1FB2FE87FDD95785D341D719AF1A78153C87A5B91EB731396DB4EDE74297866589DF256F8678748BB2D67CA91D6D413272C41FF2B21E12B9D7ADFF59271C587B9B13363C4027DF92FFFC1773333ABD718156ED7593F1127D61E9813FA1AC0533BAED65570865F7263BFFF9AE030DEF578E50F79EBC43B2139BB52A7DB6F1753A2B8D3F2FC44B39BCB73F2EFEEBE6BAEB37E7681825EBB74DF784F2ED
	F8F6ABA69CF305F768770C17417DAA001C6B7019004902F9C551F5C8BB17FE1329876E47GA22E43F879FDFF00EF9850511B4933ECC7F9B1C2479F0B9037CC607ED364B3F9DFC3DC77G62BA842EDC50058B5CF37D08DBA0F089EEBC7B396AEBB857BF5A0D6FCD17DDCF3ECD7D6CE1C942F9ED5BC97ACDD717D30164FF1FC7B637586CA4F310F86E50FC92E3D2815EA557935C5849A65EB661577B77EDC23B1E7B1D681885B4F15733EFE993DB97DE4F7AF53A306109BA340F69FC010EB1F95321CE53318E3559A4
	476AB5778D8DF848CB1AE81F0B0E3F15C0D7BF9D6D7B76DE342FE73A3FFD99921E81646F2282CDB8G6E9F4F025F0F7B5877EB7A3D5CBE17FD586174F23D92EDDF4293A3EC7C17815FFA2C206D18G658B850D55EADC0FB05A3E49C037CC5463EBC3D88F698663FAE8A197E80AEED06BD1FB033E9E4F777B57E3E5BF5623956899EE044EA35DF0EF9138CC937AD939816DEBB721FDB3825837D6E3DF4C8D3EFECE545847F44C3422AD0B045E2EEC541BA2740EB7205ED586FA713B5028FB430D3E71D7F023BE7E72
	6B7CF3660ABA0C7B129BF1CEF9114D57F05FA5F066C11CFB46EE447930104EDF0B60FEB9C066CD08AFE5735DCD6C3BACCD829FBF502D311F5087DA33848254E5D446F47C330F0E35D3142F6DB7C6E5161B08AF9E34658561FFD37A394DBF86D3EDB8E6458A1FBC6D77AEDBB0E0FC3F18B29F4BB92E26F3FB62CDEAECB07C75943FC803E739C42E0C39C4B214D5C760FBA35FBBC2FCB08B6E93G16G2482E45D8CE3594D3867FC4DFED8D2CEFEE62F4B65EE17C7E43B665184BF475FBF557A31797607EBF19E0FFF
	64DBD242D71A82FEC3257AE696C75D7CDA74521D69644A6A57F11454AF9C4A22819681AC83D8893061962C5F937BCBD87D1AADB6E51F22187B2C748BB962E10FCD9ED551E42D49E4FB0F11B8A8GB935C2569B2D64BB29474D035CCBC4244BC3A581324363F24B9EE51CE169177870DDAC3F357F69DBF89E8157E937844AC92F2779C164AD7AB818C17151376863E033787B88F1D016FBAB466FDA3FB89C3BD555B97D56C07AB7F793F9D1376A75FFC87131376A754FBF017A9320ACFF866A37791D1FA090B60369
	1DB143DFBFBFCFD4E444974369938372E5986E35AC61744B83721DB15C2FDF4569B75C96082F4AEF4DC1200053D78664DBE658FFDAB97D6900FC5FF3EB795C1CEE9A3FC672EB2C061F753B1FFD2E4DF3F3E65ACD58FEFABD8BB86F625B5435815F9BB8C9465DFE335DCB1E9532BCEF6EC94C7D245B13325F23F41214A5613F343D7EB8C9E2795F61AEC9587742869C97726F1FA3B5BD5DE45A92403E6A5B19DD61372B762DF695E4B019FFA61F8566E3FC43DED8E2C8050528EFDD37DA96C95F95956FEDB73A6CAE
	CF69285526482CDE29FBC5CEBB65FAB51ED4753C331D58C0766ED8D97FA9356C1055573E451F2BE5C3C3135B775DA134EF55146D7B5DE9D54F7A81DF7B5E78C2ADFBBA9D6D634FBCCC135BF44104B6155DE1EC93533163CBD54703D6544F6ECFB5CC2E63DB8D2843BA290ED9BD2A0E83F55A38E538BFD87D77C4F3C45E90853293G16812C3C031D4361EB0EC14C79B6C49A67FCA41EB167ABG1A4AC8B567EB0D5467FC0B69D98236E2536F4545DDA4F5D35F9255174F3E703A246E55B964CE3343CD7F3C867F3E
	2C59EF2F0C5423AB9277325E9AB4AA37C662FE4222B35AB5F74136A306EB6ED5E74E19A638AF22A255BE198AFD125979979B612914443BE02499786F1BB53F7FAAF7CB7E7D7D6F85CCC745955541F205C70F84FE1E754FC70C1FE7BDF94478FC48B3C7A6BF0F7342114067F1D20FE83FF34DF21B874D244D13211D52GF26F04359AC06D1D18670C50EF50113F6C621FAFB5DB8644F7C844B90123016B5AF3B4785F00319D256BD01722389CC4BE62DDC7C9FBA9AEB7C5B21CF2147848E39B30AA0C14634F9DA57D
	5FAE77D3B4430D9D5B28759BCB49B87D25E3448762FB967461827B5C90DBCB7D122E833A41A776BBF1FDA55371B6826EE3846E75B1DCF3A55E09794E4DFDEABC4F547C5E52F765E33B760A6AE0F57E9B5D1F5AB0B3A93BE0A67A61EEF11E32E9A67AE1CB37DA77924D6F07CFDCF9BF7C740A6AE0F58E237BAEB3203E51428F399568078401FB3B9869326EC25C3D45E21FD8607ECAE34E8E779382F7C96CA7078B1CC277F2D2483D40BDB300F4AB856E1095691AE6A1EE1E9569BA846EBF7A11AECA603A053CD801
	CB267BF92B61BE67EE44DD24E763AA613EC960DEF2889D82F700761DA838DFF0B72ECFFF5ACBE2027547B51D646FC9982E2DE054B9G3940115EC139CF69B28F08B61EF7D21B87A793DDF7CF3EDF41747E210F4C5F2B01B66F6BB06E81F481D88142GE6G44GA482A4832CG103E8163A4C091C0B5C0BBC04FB7F03C3C352BB8106D00641B77F4507418EDE45864767FF62F56FE856405FFE3F27B995F02A1AD5FAC60093BACBE5F77699682CF4AE5714DEF50722DG9EA9EAF2BE369E2C3661FAB097685B23F0
	4F3AE2BBE9875DDEF90B5DB55A4ED3B13E2E8833613E5734A85C871FC7E5A642FDB2400AA85CEB1EBB08B9E166B7271A8BBFC5738F7E7D8D3137DD7BCD145DAEE4AB5F44581DCF775DE361FE91408A004C5990CFGE533B1AE167594CD92972C32EAD8B01E6F18834F258B4C416752474D74BB40B08712070F8C77D3B349F3DD7D7DDED94C59CF1AF3842E4D5B61A24F4E716FB77C59661F97678EE2DE5CB41B1D95B88DD7B70173625B19C3467B49B3E85F8F875A9933B107AE9A439C5A88F05F9477EB6301EF51
	ECF53FF669ECFD8E3D4360FCC33BB85F104AED1666C0BF1E434E60B3BB62E53423E40EF1AE3FC9E3C7BD50F44DD16D304E515BF147F65FFD7787689C21GDD589C54392D82F5C6865029FDA690BB076DD53F4B4B96E8F4B29DFB69DC329870A9C247770AD1C7DAG9D5A7D704CE8DF9DF9517AFA155BFC1F5B34DAC91FE13AEE37212E2D51463A344F862A0126BDDA555563276B7A81DFDDC3B6D5D75AG6A329B68DA23539586B4B3GF09FA1C6230B493EF5C8157DE3AB4A0E8FA0DBABFF11CE76D2BF597FE8D7
	E53B045D2981E4EB5BE335CEF64EBDFA9F7D46CD623D5765210756D83C1B4EE23C6783FDA5C08F00F28F534778EEF3787A769907EA639F9DE86374FB0C5BD19BB7D1C0C3G7BC23C1F0D66A1B25EDADD9E5BB917D3B15B7759ED83CE321A60F1FBFE8847778577E01E70E33A171446E5B15CCFA8AE6BDE06636F8CD8820F133916606364E60B711AE30B45FF4DD1E4B1DEF314DA7C579C9596620B8118733B85DD35401D9438BDD4A6411D30D9942B40B7D23B883ED5A68A45D9EEEA57A845F5F35CE18D6EA447F5
	52F1902A8CEEC75EEE8BE973FE1B5D4E6ECFD39A1B9306FE618FB3355166B6EB6C6E97381322ADB26B7D5BA73BDE5BBE8C777410BF5D52A1FF3ABBCD7EF4DFB77953BD5A60CF77C403160E65AAF5131E338B1C63340748F76D1479589EFA61A0FD7EF62FFEEFFD9B45175D2B5FDB0F9739D4AD14C55D0BEB2CF903DA7D1977C18EF25F6564F4F79B6ED917F05EFA83997C9B8903C6FCDD1C5EEA40477F96D1FF607E7A037E607DF5D13FF1FF7DE93FFFFFFD325F383FBE532F6D2F4C2F350368578930BD7EBE4C79
	268D62583462BE4CBD9FE2ED9683EB8600AEGB6066EA5DBB8FC4C4B9EA3E31EEE6FF240E39E172616137612BFE54B5BDE91E7056C20B3BA86EDCC34200DCBE35046FF253439F31332CBG1A66A26DAFE44CE1CF4F1DEAFE7C60D975F732C72D6B468A725C2E10ECBB9610D31105BC07AE2CC2DDB38268E2327E432B4A7DF0D8159BD5CC5A3A3059E9A9A0328BB1B7FF399865AE1ED4EE5B8839E3BC0F0977EBB33FB555FA5BC7D5B96F2B6B02A94B39E9CC15730A1023841043FDB32E72C409B3B83113727C75BC
	39E3B173B17D9B90441F4B01A77FFEDCFB2D5FC85E85D77F3E847D53684D8E37B25EC0F30986596CC43D1E33D9DCC6577D98DF4F533F91B29D6E2381926F47785A66C49B136FB73291FD31E7C2ED67DB0648FBF81AE3CB251D64FF5CDE7416B6B69F6EEABEA6096F5CFEC8F473BDBBBAED723F6BB1761A0A47E9130FD7C5031278DD2E797D086D4A6FC1BDF9C5F570FCE950A0DF9A741FBF6345DE5006D84C355E92FBCBF982E79DC43ADA01BB62C63A56D81CF79A8C670160BE6988116F3AA97371E75574EF315B
	E375736C2794BF962B1FE79344FCB00D54DF5477DC08E7C042C3647BE108E7C73E20E7C15A9F5057F700623B9F5057B7CD4CAD96A8DB74G5677DFE52D7E12F9C9597573AEA72F7828CA4B5785BC564B627BFD3116EF82F8A2AE0BEF31CF7E958DBC8917457721CF7E3398F816DD965F1FFC78D682CF567C4B613B4767ECC1BE7014DD96DF195F77F87550241343736020FC7AEF02B03ED14A579344B67F38BBCA7FBE13B2DF9FF727A8BEEC3EBE6EDE92FFEBEABA14254D4738AB73795EF1D79C44CF5C643630F9
	25077CC9ECFE762BE45233DF0C7E1A0BAAFD3FFBB2FA7E7C026E93305FBBB43F3FBFF8656711277F9F6830E81E41BAB43FDF31DCF95D1B2E6873D8B60725777B4F132F767B4F13F5D57EF40DD57EF46BBE76275B7431BF5DCBB6FFBAF6465017EE0F551F2E416ACF7706013CF78C64A58E78537DF0C0CB473E853060CD16BB59EDCE19FC11EF3F6C9474B7527CEF827ADFC49C764F938EB5B6FAB43FFFED3F72F1E2F2DC79B8596F7477DB3BD36BB7163B5EF3D63B07424614FBAF91FF3CC34B6BC749E6FA6CEF25
	B50A74B1FFC52DC073E7028A4C7CA535821733400DDFC52C71FA24CDE27158C625AF9B292849F8E9CE06C94AEC13DEE5AB60B6A913ACE35B24B5F4155BA6E5408AF80CAC99B335GE9B0885C683B9ACD3F671DEBF2EE2A98B6C9D9AD50854CF637558C02681FADE8175677D2093287E895A9077EB11ECF0177D34A3B3BDDB275F99569DD955FC7702C3453ED6E1301A45BE2768CD189E7250B521A4F256CFE4FC0AFD520C8264FDBBE8735740E942FED1756A9D6F22600ECCAAA52BB2A688E72D804976C2635137E
	827F5C741F17B0279B551894E4CE05582FE0AAD8C5DAC75F4123CBD5C5DA6B157BDCCE0B5778D78DFB19F13C00FC112400A069E397AFC4032540C57F2A0759EE98A6B51AEA050505692A65734F870BF6CA0A14AD966A01EA1205CB6E7FFB377CDD9CCB8EBB9548643413A2ED20AE733263CD02E31D265E6B709CE171A56DB947D5577685FC1FE7E1BC4BADD26255B3CB6C79DE57B95267F39F06BC6DE1FC1E97BDCE7E8EE620E7541D8E335D2E186DC37B1C96723C3AE1DC1155B3BAA74F9179DE2461057C0C4CB9
	E2772006677FGD0CB87883BE75CB18DAAGGC80EGGD0CB818294G94G88G88GD0FBB0B63BE75CB18DAAGGC80EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC7AAGGGG
**end of data**/
}
}