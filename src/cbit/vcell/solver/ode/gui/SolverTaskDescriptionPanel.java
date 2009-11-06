package cbit.vcell.solver.ode.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Color;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.vcell.util.BeanUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.math.Constant;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
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
	private JPanel ivjPanel2 = null;
	private JPanel ivjPlotSpecificationPanel = null;
	private javax.swing.JCheckBox ivjPerformSensitivityAnalysisCheckbox = null;
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
			org.vcell.util.gui.EmptyBorderBean ivjLocalBorder;
			ivjLocalBorder = new org.vcell.util.gui.EmptyBorderBean();
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
 * Return the Panel2 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JPanel getPanel2() {
	if (ivjPanel2 == null) {
		try {
			ivjPanel2 = new JPanel();
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
private JPanel getPlotSpecificationPanel() {
	if (ivjPlotSpecificationPanel == null) {
		try {
			ivjPlotSpecificationPanel = new JPanel();
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
			Enumeration<Constant> enum1 = getSolverTaskDescription().getSimulation().getMathDescription().getConstants();
			while (enum1.hasMoreElements()){
				Constant constant = enum1.nextElement();
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
private TimeBounds gettimeBounds1() {
	// user code begin {1}
	// user code end
	return ivjtimeBounds1;
}

/**
 * Return the timeStep1 property value.
 * @return cbit.vcell.solver.TimeStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TimeStep gettimeStep1() {
	// user code begin {1}
	// user code end
	return ivjtimeStep1;
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
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in SolverTaskDescriptionPanel");
	exception.printStackTrace(System.out);
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
		frame.setVisible(true);
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
public void setKeepEvery(SolverTaskDescription arg1) {
	getJTextFieldKeepEvery().setText("");
	BeanUtils.enableComponents(getJPanelKeepEvery(), false);
	if (arg1 == null) { 
		return;	
	}
	if (arg1.getOutputTimeSpec().isDefault()) {
		getJTextFieldKeepEvery().setText(((DefaultOutputTimeSpec)arg1.getOutputTimeSpec()).getKeepEvery() + "");
		BeanUtils.enableComponents(getJPanelKeepEvery(), true);
	} else if (arg1.getOutputTimeSpec().isUniform()) {
		getJTextFieldKeepEvery().setText(((UniformOutputTimeSpec)arg1.getOutputTimeSpec()).getOutputTimeStep() + "");
		getJLabelTimeSamples().setText("seconds");
		BeanUtils.enableComponents(getJPanelKeepEvery(), true);
	}	
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
				Enumeration<Constant> enum1 = mathDescription.getConstants();
				if (enum1.hasMoreElements()){
					((javax.swing.DefaultComboBoxModel)(getConstantChoice().getModel())).addElement(SELECT_PARAMETER);
				}
				
				//Sort Constants, ignore case
				TreeSet<String> sortedConstants = new TreeSet<String>(
					new Comparator<String>(){
						public int compare(String o1, String o2){
							int ignoreCaseB = o1.compareToIgnoreCase(o2);
							if(ignoreCaseB == 0){
								return o1.compareTo(o2);
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
	if (std != null) {
		if (std.getOutputTimeSpec().isDefault()) {
			try {
				std.setOutputTimeSpec(new DefaultOutputTimeSpec(Integer.parseInt(getJTextFieldKeepEvery().getText())));
			} catch (java.beans.PropertyVetoException ex) {
				ex.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(this, ex.getMessage());
			}
		} else if (std.getOutputTimeSpec().isUniform()) {
			try {
				double outputTime = Double.parseDouble(getJTextFieldKeepEvery().getText());
				if (getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume) ||
						getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolumeStandalone)) {
					double timeStep = getTornOffSolverTaskDescription().getTimeStep().getDefaultTimeStep();
					boolean bValid = true;
					String suggestedInterval = outputTime + "";
					if (outputTime < timeStep) {
						suggestedInterval = timeStep + "";
						bValid = false;
					} else {
						float n = (float)(outputTime/timeStep);
						if (n != (int)n) {
							bValid = false;
							suggestedInterval = ((float)((int)(n + 0.5) * timeStep)) + "";
						}
					}
					if (!bValid) {
						String ret = PopupGenerator.showWarningDialog(this, "Output Interval must be integer multiple of time step. " 
								+ "OK to change Output Interval to " + suggestedInterval + "?", 
								new String[]{ UserMessage.OPTION_YES, UserMessage.OPTION_NO}, UserMessage.OPTION_YES);
						if (ret.equals(UserMessage.OPTION_YES)) {
							outputTime = Double.parseDouble(suggestedInterval);
						} else {
							SwingUtilities.invokeLater(new Runnable() { 
							    public void run() { 
							    	getJTextFieldKeepEvery().requestFocus();
							    }
							});
						}
					}
				}
				UniformOutputTimeSpec ots = new UniformOutputTimeSpec(outputTime);	
				std.setOutputTimeSpec(ots);
			} catch (java.beans.PropertyVetoException ex) {
				ex.printStackTrace(System.out);
				PopupGenerator.showErrorDialog(this, ex.getMessage());
			}
		}
	}
}


/**
 * Comment
 */
private void updateSensitivityParameterDisplay(Constant sensParam) {
	Simulation simulation = getTornOffSolverTaskDescription().getSimulation();
	if(simulation.isSpatial() || simulation.getMathDescription().isStoch())
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
	if (getTornOffSolverTaskDescription().getSolverDescription().hasVariableTimestep())
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

}