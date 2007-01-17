package cbit.vcell.solver.ode.gui;
import cbit.vcell.solver.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.math.Constant;
import java.util.Enumeration;
import cbit.vcell.solver.ode.*;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:25 PM)
 * @author: John Wagner
 */
public class SolverTaskDescriptionAdvancedPanel extends javax.swing.JPanel implements java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
	private final static String SELECT_PARAMETER = "select parameter";
	private javax.swing.JPanel ivjPanel2 = null;
	private javax.swing.JLabel ivjIntegratorLabel = null;
	private javax.swing.JLabel ivjKeepEveryLabel = null;
	private javax.swing.JTextField ivjKeepEveryTextField = null;
	private javax.swing.JLabel ivjPointsLabel = null;
	cbit.vcell.math.Constant fieldSensitivityParameter = null;
	private javax.swing.JTextField ivjKeepAtMostTextField = null;
	private ErrorTolerancePanel ivjErrorTolerancePanel = null;
	private TimeBoundsPanel ivjTimeBoundsPanel = null;
	private TimeStepPanel ivjTimeStepPanel = null;
	private cbit.vcell.solver.SolverTaskDescription fieldSolverTaskDescription = null;
	private boolean ivjConnPtoP1Aligning = false;
	private SolverTaskDescription ivjTornOffSolverTaskDescription = null;
	private javax.swing.JLabel ivjKeepAtMostLabel = null;
	private javax.swing.JComboBox ivjSolverComboBox = null;
	private javax.swing.DefaultComboBoxModel fieldSolverComboBoxModel = null;
	private javax.swing.DefaultComboBoxModel fieldConstantComboBoxModel = null;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private Object ivjSolverComboBoxModel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjTimeStepUnitsLabel = null;
	private javax.swing.ButtonGroup ivjbuttonGroup1 = null;
	private javax.swing.JLabel ivjOpIntervalLabel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private java.awt.FlowLayout ivjJPanel3FlowLayout = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JTextField ivjOutputTimesTextField = null;
	private javax.swing.JRadioButton ivjDefaultOutputRadioButton = null;
	private javax.swing.JRadioButton ivjExplicitOutputRadioButton = null;
	private javax.swing.JRadioButton ivjUniformOutputRadioButton = null;
	private javax.swing.JPanel ivjDefaultOutputPanel = null;
	private javax.swing.JPanel ivjExplicitOutputPanel = null;
	private javax.swing.JPanel ivjUniformOutputPanel = null;
	private javax.swing.JTextField ivjOutputTimeStepTextField = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabelTitle = null;

/**
 * ODEAdvancedPanel constructor comment.
 */
public SolverTaskDescriptionAdvancedPanel() {
	super();
	initialize();
}

/**
 * Comment
 */
private void actionOutputOptionButtonState(java.awt.event.ItemEvent itemEvent) throws Exception {
	
	if(itemEvent.getStateChange() != java.awt.event.ItemEvent.SELECTED){
		return;
	}
	
	if (getSolverTaskDescription()==null){
		return;
	}

	if(itemEvent.getSource() == getDefaultOutputRadioButton() && !getSolverTaskDescription().getOutputTimeSpec().isDefault()){
		getSolverTaskDescription().setOutputTimeSpec(new DefaultOutputTimeSpec());
	} else if(itemEvent.getSource() == getUniformOutputRadioButton() && !getSolverTaskDescription().getOutputTimeSpec().isUniform()){
		TimeBounds timeBounds = getTornOffSolverTaskDescription().getTimeBounds();
		cbit.util.Range outputTimeRange = cbit.util.NumberUtils.getDecimalRange(timeBounds.getStartingTime(), timeBounds.getEndingTime()/100, true, true);
		double outputTime = outputTimeRange.getMax();
		getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(outputTime));
	} else if(itemEvent.getSource() == getExplicitOutputRadioButton() && !getSolverTaskDescription().getOutputTimeSpec().isExplicit()){
		TimeBounds timeBounds = getTornOffSolverTaskDescription().getTimeBounds();
		getSolverTaskDescription().setOutputTimeSpec(new ExplicitOutputTimeSpec(new double[]{timeBounds.getStartingTime(), timeBounds.getEndingTime()}));
	}
}


/**
 * Comment
 */
private void buttonGroup_Initialize() {
	getbuttonGroup1().add(getDefaultOutputRadioButton());
	getbuttonGroup1().add(getUniformOutputRadioButton());
	getbuttonGroup1().add(getExplicitOutputRadioButton());
}


/**
 * Comment
 */
public void checkTimeBounds(cbit.vcell.solver.TimeBounds arg1) {
	OutputTimeSpec ots = getSolverTaskDescription().getOutputTimeSpec();
	if (ots.isExplicit()) {
		double[] times = ((ExplicitOutputTimeSpec)ots).getOutputTimes();
			
		if (times[0] < arg1.getStartingTime() || times[times.length - 1] > arg1.getEndingTime()) {
			cbit.vcell.client.PopupGenerator.showErrorDialog("Output time should be within (" + arg1.getStartingTime() + "," + arg1.getEndingTime() + ")");
		}
	}
}


/**
 * connEtoC1:  (TornOffSolverTaskDescription.this --> SolverTaskDescriptionAdvancedPanel.enableTimeStep()V)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(cbit.vcell.solver.SolverTaskDescription value) {
	try {
		// user code begin {1}
		// user code end
		this.enableVariableTimeStepOptions();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC10:  (TimeStepTextField.focus.focusLost(java.awt.event.FocusEvent) --> SolverTaskDescriptionAdvancedPanel.outputTimeIntervalForTimeStep(Ljava.awt.event.FocusEvent;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setNewOutputOption(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC11:  (TornOffSolverTaskDescription.solverDescription --> SolverTaskDescriptionAdvancedPanel.enableErrorTolerance()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.enableVariableTimeStepOptions();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC12:  (TornOffSolverTaskDescription.outputTimeSpec --> SolverTaskDescriptionAdvancedPanel.setOutputOptionFields(Lcbit.vcell.solver.OutputTimeSpec;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setOutputOptionFields(getTornOffSolverTaskDescription().getOutputTimeSpec());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (ExplicitOutputRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> SolverTaskDescriptionAdvancedPanel.actionTimeIntervalButtonState(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.actionOutputOptionButtonState(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC14:  (KeepEveryTextField.focus.focusLost(java.awt.event.FocusEvent) --> SolverTaskDescriptionAdvancedPanel.setNewOutputOption(Ljava.awt.event.FocusEvent;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setNewOutputOption(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC15:  (KeepAtMostTextField.focus.focusLost(java.awt.event.FocusEvent) --> SolverTaskDescriptionAdvancedPanel.setNewOutputOption(Ljava.awt.event.FocusEvent;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setNewOutputOption(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC16:  (TornOffSolverTaskDescription.outputTimeSpec --> SolverTaskDescriptionAdvancedPanel.enableOutputOptionPanel()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.enableOutputOptionPanel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC17:  (OutputTimesTextField.focus.focusLost(java.awt.event.FocusEvent) --> SolverTaskDescriptionAdvancedPanel.setNewOutputOption(Ljava.awt.event.FocusEvent;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setNewOutputOption(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC18:  (TornOffSolverTaskDescription.this --> SolverTaskDescriptionAdvancedPanel.setOutputOptionFields(Lcbit.vcell.solver.OutputTimeSpec;)V)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(cbit.vcell.solver.SolverTaskDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSolverTaskDescription() != null)) {
			this.setOutputOptionFields(getTornOffSolverTaskDescription().getOutputTimeSpec());
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
 * connEtoC19:  (TornOffSolverTaskDescription.solverDescription --> SolverTaskDescriptionAdvancedPanel.setOutputOptionFields(Lcbit.vcell.solver.OutputTimeSpec;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSolverTaskDescription() != null)) {
			this.setOutputOptionFields(getTornOffSolverTaskDescription().getOutputTimeSpec());
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
 * connEtoC2:  (TornOffSolverTaskDescription.solverDescription --> SolverTaskDescriptionAdvancedPanel.enableTimeStep()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.enableVariableTimeStepOptions();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC20:  (TornOffSolverTaskDescription.timeBounds --> SolverTaskDescriptionAdvancedPanel.tornOffSolverTaskDescription_TimeBounds()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.tornOffSolverTaskDescription_TimeBounds();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC21:  (SolverTaskDescriptionAdvancedPanel.initialize() --> SolverTaskDescriptionAdvancedPanel.makeBold()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21() {
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
 * connEtoC3:  (JRadioButton1.item.itemStateChanged(java.awt.event.ItemEvent) --> SolverTaskDescriptionAdvancedPanel.actionTimeIntervalButtonState(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.actionOutputOptionButtonState(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC4:  (JRadioButton2.item.itemStateChanged(java.awt.event.ItemEvent) --> SolverTaskDescriptionAdvancedPanel.actionTimeIntervalButtonState(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.actionOutputOptionButtonState(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC5:  (TornOffSolverTaskDescription.solver --> SolverTaskDescriptionAdvancedPanel.updateSolverNameDisplay(Ljava.lang.String;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateSolverNameDisplay(getTornOffSolverTaskDescription().getSolverDescription());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (TornOffSolverTaskDescription.this --> SolverTaskDescriptionAdvancedPanel.updateSolverNameDisplay(Ljava.lang.String;)V)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(cbit.vcell.solver.SolverTaskDescription value) {
	try {
		// user code begin {1}
		// user code end
		if ((getTornOffSolverTaskDescription() != null)) {
			this.updateSolverNameDisplay(getTornOffSolverTaskDescription().getSolverDescription());
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
 * connEtoC7:  (TornOffSolverTaskDescription.solverDescription --> SolverTaskDescriptionAdvancedPanel.enableOutputIntevalPanel(Lcbit.vcell.solver.SolverTaskDescription;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.enableOutputOptionPanel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC8:  (TornOffSolverTaskDescription.this --> SolverTaskDescriptionAdvancedPanel.enableOutputIntevalPanel(Lcbit.vcell.solver.SolverDescription;)V)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(cbit.vcell.solver.SolverTaskDescription value) {
	try {
		// user code begin {1}
		// user code end
		this.enableOutputOptionPanel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC9:  (SolverTaskDescriptionAdvancedPanel.initialize() --> SolverTaskDescriptionAdvancedPanel.buttonGroup_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9() {
	try {
		// user code begin {1}
		// user code end
		this.buttonGroup_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM13:  (TornOffSolverTaskDescription.this --> SolverComboBoxModel.this)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(cbit.vcell.solver.SolverTaskDescription value) {
	try {
		// user code begin {1}
		// user code end
		setSolverComboBoxModel(this.createSolverComboBoxModel(getTornOffSolverTaskDescription()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (SolverComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> TornOffSolverTaskDescription.solverDescription)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTornOffSolverTaskDescription().setSolverDescription(this.getSolverDescriptionFromName((String)getSolverComboBox().getSelectedItem()));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (SolverTaskDescriptionAdvancedPanel.solverTaskDescription <--> TornOffSolverTaskDescription.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				this.setSolverTaskDescription(getTornOffSolverTaskDescription());
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
 * connPtoP1SetTarget:  (SolverTaskDescriptionAdvancedPanel.solverTaskDescription <--> TornOffSolverTaskDescription.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			setTornOffSolverTaskDescription(this.getSolverTaskDescription());
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
 * connPtoP2SetSource:  (TornOffSolverTaskDescription.timeBounds <--> TimeBoundsPanel.timeBounds)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTornOffSolverTaskDescription().setTimeBounds(getTimeBoundsPanel().getTimeBounds());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (TornOffSolverTaskDescription.timeBounds <--> TimeBoundsPanel.timeBounds)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTimeBoundsPanel().setTimeBounds(getTornOffSolverTaskDescription().getTimeBounds());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (TornOffSolverTaskDescription.timeStep <--> TimeStepPanel.timeStep)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTornOffSolverTaskDescription().setTimeStep(getTimeStepPanel().getTimeStep());
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
 * connPtoP3SetTarget:  (TornOffSolverTaskDescription.timeStep <--> TimeStepPanel.timeStep)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP3Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTimeStepPanel().setTimeStep(getTornOffSolverTaskDescription().getTimeStep());
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
 * connPtoP4SetSource:  (TornOffSolverTaskDescription.errorTolerance <--> ErrorTolerancePanel.errorTolerance)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTornOffSolverTaskDescription().setErrorTolerance(getErrorTolerancePanel().getErrorTolerance());
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
 * connPtoP4SetTarget:  (TornOffSolverTaskDescription.errorTolerance <--> ErrorTolerancePanel.errorTolerance)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP4Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getErrorTolerancePanel().setErrorTolerance(getTornOffSolverTaskDescription().getErrorTolerance());
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
 * connPtoP7SetSource:  (SolverComboBox.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			setSolverComboBoxModel(getSolverComboBox().getModel());
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connPtoP7SetTarget:  (SolverComboBox.model <--> model1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP7Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP7Aligning = true;
			if ((getSolverComboBoxModel() != null)) {
				getSolverComboBox().setModel((javax.swing.ComboBoxModel)getSolverComboBoxModel());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Gets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @return The solverTaskDescription property value.
 * @see #setSolverTaskDescription
 new javax.swing.DefaultComboBoxModel()
 */
private javax.swing.DefaultComboBoxModel createConstantComboBoxModel() {
	if (fieldConstantComboBoxModel == null) {
		fieldConstantComboBoxModel = new javax.swing.DefaultComboBoxModel();
	}
	fieldConstantComboBoxModel.removeAllElements();
	//
	if (getTornOffSolverTaskDescription() != null && getTornOffSolverTaskDescription().getSimulation() != null) {
		cbit.vcell.math.MathDescription mathDescription = getTornOffSolverTaskDescription().getSimulation().getMathDescription();
		if (mathDescription != null) {
			java.util.Enumeration enum1 = mathDescription.getConstants();
			if (enum1.hasMoreElements()){
				fieldConstantComboBoxModel.addElement(SELECT_PARAMETER);
			}
			while (enum1.hasMoreElements()) {
				cbit.vcell.math.Constant constant = (cbit.vcell.math.Constant) enum1.nextElement();
				fieldConstantComboBoxModel.addElement(constant.getName());
			}
		}
	}
	//
	return (fieldConstantComboBoxModel);
}


/**
 * Gets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @return The solverTaskDescription property value.
 * @see #setSolverTaskDescription
 new javax.swing.DefaultComboBoxModel()
 */
private javax.swing.DefaultComboBoxModel createSolverComboBoxModel(SolverTaskDescription newSolverTaskDescription) {
	if (fieldSolverComboBoxModel == null) {
		fieldSolverComboBoxModel = new javax.swing.DefaultComboBoxModel();
	}
	// remember cuurent solver so we can put it back as the selected one after creating the list
	// otherwise, iterating while adding elements will fire events that wil change it on the TornoffSolverTaskDescription...
	String currentSolverDescriptionName = null;
	if (newSolverTaskDescription != null && newSolverTaskDescription.getSolverDescription() != null) {
		currentSolverDescriptionName = newSolverTaskDescription.getSolverDescription().getName();
	}
	//
	fieldSolverComboBoxModel.removeAllElements();
	if(getSolverTaskDescription() != null) {
		String[] solverDescriptionNames = new String[0];
		if (getSolverTaskDescription().getSimulation().getIsSpatial()) {
			solverDescriptionNames = SolverTypes.getPDESolverDescriptions();
		} else {
			solverDescriptionNames = SolverTypes.getODESolverDescriptions();
		}
		for (int i = 0; i < solverDescriptionNames.length; i++) {
			fieldSolverComboBoxModel.addElement(solverDescriptionNames[i]);
		}
	}
	//
	if (currentSolverDescriptionName != null) {
		fieldSolverComboBoxModel.setSelectedItem(currentSolverDescriptionName);
	}
	return (fieldSolverComboBoxModel);
}


/**
 * Comment
 */
private void enableOutputOptionPanel() {
	// enables the panel where the output interval is set if the solver is IDA
	// Otherwise, that panel is disabled. That option is available only for IDA solver.

	SolverTaskDescription solverTaskDescription = getSolverTaskDescription();
	
	if (solverTaskDescription==null || solverTaskDescription.getSolverDescription()==null){
		// if solver is not LSODA, if the output Time step radio button had been set, 
		// change the setting to the 'keep every' radio button and flush the contents of the output timestep text field. 
		// Also, disable its radiobutton and fields.		
		getDefaultOutputRadioButton().setEnabled(false);
		getUniformOutputRadioButton().setEnabled(false);	
		getExplicitOutputRadioButton().setEnabled(false);
		cbit.util.BeanUtils.enableComponents(getDefaultOutputPanel(), false);
		cbit.util.BeanUtils.enableComponents(getUniformOutputPanel(), false);
		cbit.util.BeanUtils.enableComponents(getExplicitOutputPanel(), false);
	} else if (solverTaskDescription.getSolverDescription().equals(SolverDescription.LSODA)){
		getDefaultOutputRadioButton().setEnabled(true);
		getUniformOutputRadioButton().setEnabled(true);	
		getExplicitOutputRadioButton().setEnabled(true);
		cbit.util.BeanUtils.enableComponents(getDefaultOutputPanel(), true);
		cbit.util.BeanUtils.enableComponents(getUniformOutputPanel(), true);
		cbit.util.BeanUtils.enableComponents(getExplicitOutputPanel(), true);
		if (solverTaskDescription.getOutputTimeSpec().isDefault()){
			getKeepAtMostTextField().setEnabled(true);
			getKeepEveryTextField().setEnabled(true);
			getOutputTimeStepTextField().setEnabled(false);
			getOutputTimesTextField().setEnabled(false);
		}else if (solverTaskDescription.getOutputTimeSpec().isUniform()){
			getKeepAtMostTextField().setEnabled(false);
			getKeepEveryTextField().setEnabled(false);
			getOutputTimeStepTextField().setEnabled(true);
			getOutputTimesTextField().setEnabled(false);
		}else if (solverTaskDescription.getOutputTimeSpec().isExplicit()){
			getKeepAtMostTextField().setEnabled(false);
			getKeepEveryTextField().setEnabled(false);
			getOutputTimeStepTextField().setEnabled(false);
			getOutputTimesTextField().setEnabled(true);
		}
	} else {
		getDefaultOutputRadioButton().setEnabled(true);
		getUniformOutputRadioButton().setEnabled(false);	
		getExplicitOutputRadioButton().setEnabled(false);
		cbit.util.BeanUtils.enableComponents(getDefaultOutputPanel(), true);
		cbit.util.BeanUtils.enableComponents(getUniformOutputPanel(), false);
		cbit.util.BeanUtils.enableComponents(getExplicitOutputPanel(), false);
		if (solverTaskDescription.getSolverDescription().equals(SolverDescription.FiniteVolume)) {
			getKeepAtMostLabel().setEnabled(false);
			getPointsLabel().setEnabled(false);
			getKeepAtMostTextField().setEnabled(false);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2004 9:24:43 PM)
 */
private void enableVariableTimeStepOptions() {

	boolean bHasVariableTS = false;
	if(getSolverTaskDescription() != null && getSolverTaskDescription().getSolverDescription() != null){
		bHasVariableTS = getSolverTaskDescription().getSolverDescription().hasVariableTimestep();
	}
	//cbit.util.BeanUtils.enableComponents(getTimeStepPanel(),bEnableTimeStep);
	cbit.util.BeanUtils.enableComponents(getErrorTolerancePanel(),bHasVariableTS);
	getTimeStepPanel().enableComponents(bHasVariableTS);
}


/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void focusGained(java.awt.event.FocusEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void focusLost(java.awt.event.FocusEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getOutputTimeStepTextField()) 
		connEtoC10(e);
	if (e.getSource() == getKeepEveryTextField()) 
		connEtoC14(e);
	if (e.getSource() == getKeepAtMostTextField()) 
		connEtoC15(e);
	if (e.getSource() == getOutputTimesTextField()) 
		connEtoC17(e);
	// user code begin {2}
	// user code end
}

/**
 * Return the buttonGroup1 property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getbuttonGroup1() {
	if (ivjbuttonGroup1 == null) {
		try {
			ivjbuttonGroup1 = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbuttonGroup1;
}


/**
 * Return the Panel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDefaultOutputPanel() {
	if (ivjDefaultOutputPanel == null) {
		try {
			ivjDefaultOutputPanel = new javax.swing.JPanel();
			ivjDefaultOutputPanel.setName("DefaultOutputPanel");
			ivjDefaultOutputPanel.setOpaque(false);
			ivjDefaultOutputPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsKeepAtMostLabel = new java.awt.GridBagConstraints();
			constraintsKeepAtMostLabel.gridx = 3; constraintsKeepAtMostLabel.gridy = 0;
			constraintsKeepAtMostLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDefaultOutputPanel().add(getKeepAtMostLabel(), constraintsKeepAtMostLabel);

			java.awt.GridBagConstraints constraintsKeepEveryTextField = new java.awt.GridBagConstraints();
			constraintsKeepEveryTextField.gridx = 1; constraintsKeepEveryTextField.gridy = 0;
			constraintsKeepEveryTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeepEveryTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getDefaultOutputPanel().add(getKeepEveryTextField(), constraintsKeepEveryTextField);

			java.awt.GridBagConstraints constraintsKeepEveryLabel = new java.awt.GridBagConstraints();
			constraintsKeepEveryLabel.gridx = 0; constraintsKeepEveryLabel.gridy = 0;
			constraintsKeepEveryLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeepEveryLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDefaultOutputPanel().add(getKeepEveryLabel(), constraintsKeepEveryLabel);

			java.awt.GridBagConstraints constraintsKeepAtMostTextField = new java.awt.GridBagConstraints();
			constraintsKeepAtMostTextField.gridx = 4; constraintsKeepAtMostTextField.gridy = 0;
			constraintsKeepAtMostTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeepAtMostTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getDefaultOutputPanel().add(getKeepAtMostTextField(), constraintsKeepAtMostTextField);

			java.awt.GridBagConstraints constraintsPointsLabel = new java.awt.GridBagConstraints();
			constraintsPointsLabel.gridx = 5; constraintsPointsLabel.gridy = 0;
			constraintsPointsLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointsLabel.weightx = 1.0;
			constraintsPointsLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDefaultOutputPanel().add(getPointsLabel(), constraintsPointsLabel);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 2; constraintsJLabel4.gridy = 0;
			constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
			getDefaultOutputPanel().add(getJLabel4(), constraintsJLabel4);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultOutputPanel;
}

/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDefaultOutputRadioButton() {
	if (ivjDefaultOutputRadioButton == null) {
		try {
			ivjDefaultOutputRadioButton = new javax.swing.JRadioButton();
			ivjDefaultOutputRadioButton.setName("DefaultOutputRadioButton");
			ivjDefaultOutputRadioButton.setSelected(true);
			ivjDefaultOutputRadioButton.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultOutputRadioButton;
}

/**
 * Return the ErrorTolerancePanel property value.
 * @return cbit.vcell.solver.ode.gui.ErrorTolerancePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ErrorTolerancePanel getErrorTolerancePanel() {
	if (ivjErrorTolerancePanel == null) {
		try {
			ivjErrorTolerancePanel = new cbit.vcell.solver.ode.gui.ErrorTolerancePanel();
			ivjErrorTolerancePanel.setName("ErrorTolerancePanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjErrorTolerancePanel;
}


/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getExplicitOutputPanel() {
	if (ivjExplicitOutputPanel == null) {
		try {
			ivjExplicitOutputPanel = new javax.swing.JPanel();
			ivjExplicitOutputPanel.setName("ExplicitOutputPanel");
			ivjExplicitOutputPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getExplicitOutputPanel().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsOutputTimesTextField = new java.awt.GridBagConstraints();
			constraintsOutputTimesTextField.gridx = 1; constraintsOutputTimesTextField.gridy = 0;
			constraintsOutputTimesTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOutputTimesTextField.weightx = 1.0;
			constraintsOutputTimesTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getExplicitOutputPanel().add(getOutputTimesTextField(), constraintsOutputTimesTextField);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 1;
			constraintsJLabel3.gridwidth = 2;
			constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getExplicitOutputPanel().add(getJLabel3(), constraintsJLabel3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExplicitOutputPanel;
}

/**
 * Return the JRadioButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getExplicitOutputRadioButton() {
	if (ivjExplicitOutputRadioButton == null) {
		try {
			ivjExplicitOutputRadioButton = new javax.swing.JRadioButton();
			ivjExplicitOutputRadioButton.setName("ExplicitOutputRadioButton");
			ivjExplicitOutputRadioButton.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExplicitOutputRadioButton;
}

/**
 * Return the Label4 property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getIntegratorLabel() {
	if (ivjIntegratorLabel == null) {
		try {
			ivjIntegratorLabel = new javax.swing.JLabel();
			ivjIntegratorLabel.setName("IntegratorLabel");
			ivjIntegratorLabel.setText("Integrator");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIntegratorLabel;
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
			ivjJLabel1.setText("Output Options");
			ivjJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
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
			ivjJLabel2.setText("Output Times");
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
			ivjJLabel3.setText("(Comma or space seperated numbers, e.g. 0.5, 0.8, 1.2, 1.7)");
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
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("time samples");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
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
			ivjJLabelTitle.setText("Choose solver algorithm and fine-tune time conditions:");
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

			java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
			constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 0;
			constraintsJPanel3.gridwidth = 2;
			constraintsJPanel3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel3.weightx = 1.0;
			constraintsJPanel3.weighty = 1.0;
			constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJPanel3(), constraintsJPanel3);

			java.awt.GridBagConstraints constraintsDefaultOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsDefaultOutputRadioButton.gridx = 0; constraintsDefaultOutputRadioButton.gridy = 1;
			constraintsDefaultOutputRadioButton.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsDefaultOutputRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getDefaultOutputRadioButton(), constraintsDefaultOutputRadioButton);

			java.awt.GridBagConstraints constraintsUniformOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsUniformOutputRadioButton.gridx = 0; constraintsUniformOutputRadioButton.gridy = 2;
			constraintsUniformOutputRadioButton.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsUniformOutputRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getUniformOutputRadioButton(), constraintsUniformOutputRadioButton);

			java.awt.GridBagConstraints constraintsExplicitOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsExplicitOutputRadioButton.gridx = 0; constraintsExplicitOutputRadioButton.gridy = 3;
			constraintsExplicitOutputRadioButton.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsExplicitOutputRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getExplicitOutputRadioButton(), constraintsExplicitOutputRadioButton);

			java.awt.GridBagConstraints constraintsDefaultOutputPanel = new java.awt.GridBagConstraints();
			constraintsDefaultOutputPanel.gridx = 1; constraintsDefaultOutputPanel.gridy = 1;
			constraintsDefaultOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDefaultOutputPanel.weightx = 1.0;
			constraintsDefaultOutputPanel.weighty = 1.0;
			constraintsDefaultOutputPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getDefaultOutputPanel(), constraintsDefaultOutputPanel);

			java.awt.GridBagConstraints constraintsUniformOutputPanel = new java.awt.GridBagConstraints();
			constraintsUniformOutputPanel.gridx = 1; constraintsUniformOutputPanel.gridy = 2;
			constraintsUniformOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsUniformOutputPanel.weightx = 1.0;
			constraintsUniformOutputPanel.weighty = 1.0;
			constraintsUniformOutputPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getUniformOutputPanel(), constraintsUniformOutputPanel);

			java.awt.GridBagConstraints constraintsExplicitOutputPanel = new java.awt.GridBagConstraints();
			constraintsExplicitOutputPanel.gridx = 1; constraintsExplicitOutputPanel.gridy = 3;
			constraintsExplicitOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsExplicitOutputPanel.weightx = 1.0;
			constraintsExplicitOutputPanel.weighty = 1.0;
			constraintsExplicitOutputPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getExplicitOutputPanel(), constraintsExplicitOutputPanel);
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
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(getJPanel3FlowLayout());
			getJPanel3().add(getJLabel1(), getJLabel1().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}


/**
 * Return the JPanel3FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel3FlowLayout() {
	java.awt.FlowLayout ivjJPanel3FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel3FlowLayout = new java.awt.FlowLayout();
		ivjJPanel3FlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel3FlowLayout;
}


/**
 * Return the PointsLabel property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeepAtMostLabel() {
	if (ivjKeepAtMostLabel == null) {
		try {
			ivjKeepAtMostLabel = new javax.swing.JLabel();
			ivjKeepAtMostLabel.setName("KeepAtMostLabel");
			ivjKeepAtMostLabel.setText("and at most");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeepAtMostLabel;
}

/**
 * Return the JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeepAtMostTextField() {
	if (ivjKeepAtMostTextField == null) {
		try {
			ivjKeepAtMostTextField = new javax.swing.JTextField();
			ivjKeepAtMostTextField.setName("KeepAtMostTextField");
			ivjKeepAtMostTextField.setText("");
			ivjKeepAtMostTextField.setMinimumSize(new java.awt.Dimension(66, 21));
			ivjKeepAtMostTextField.setColumns(6);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeepAtMostTextField;
}

/**
 * Return the KeepEveryLabel property value.
 * @return java.awt.Label
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeepEveryLabel() {
	if (ivjKeepEveryLabel == null) {
		try {
			ivjKeepEveryLabel = new javax.swing.JLabel();
			ivjKeepEveryLabel.setName("KeepEveryLabel");
			ivjKeepEveryLabel.setText("Keep every");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeepEveryLabel;
}

/**
 * Return the KeepEveryTextField property value.
 * @return java.awt.TextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeepEveryTextField() {
	if (ivjKeepEveryTextField == null) {
		try {
			ivjKeepEveryTextField = new javax.swing.JTextField();
			ivjKeepEveryTextField.setName("KeepEveryTextField");
			ivjKeepEveryTextField.setText("");
			ivjKeepEveryTextField.setMinimumSize(new java.awt.Dimension(21, 22));
			ivjKeepEveryTextField.setColumns(6);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeepEveryTextField;
}

/**
 * Return the TimeStepLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOpIntervalLabel() {
	if (ivjOpIntervalLabel == null) {
		try {
			ivjOpIntervalLabel = new javax.swing.JLabel();
			ivjOpIntervalLabel.setName("OpIntervalLabel");
			ivjOpIntervalLabel.setText("Output Interval");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpIntervalLabel;
}

/**
 * Return the TimeStepTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOutputTimeStepTextField() {
	if (ivjOutputTimeStepTextField == null) {
		try {
			ivjOutputTimeStepTextField = new javax.swing.JTextField();
			ivjOutputTimeStepTextField.setName("OutputTimeStepTextField");
			ivjOutputTimeStepTextField.setMinimumSize(new java.awt.Dimension(60, 20));
			ivjOutputTimeStepTextField.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputTimeStepTextField;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOutputTimesTextField() {
	if (ivjOutputTimesTextField == null) {
		try {
			ivjOutputTimesTextField = new javax.swing.JTextField();
			ivjOutputTimesTextField.setName("OutputTimesTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputTimesTextField;
}

/**
 * Return the Panel2 property value.
 * @return java.awt.Panel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getPanel2() {
	if (ivjPanel2 == null) {
		try {
			ivjPanel2 = new javax.swing.JPanel();
			ivjPanel2.setName("Panel2");
			ivjPanel2.setOpaque(false);
			ivjPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsIntegratorLabel = new java.awt.GridBagConstraints();
			constraintsIntegratorLabel.gridx = 0; constraintsIntegratorLabel.gridy = 0;
			constraintsIntegratorLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsIntegratorLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIntegratorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getPanel2().add(getIntegratorLabel(), constraintsIntegratorLabel);

			java.awt.GridBagConstraints constraintsSolverComboBox = new java.awt.GridBagConstraints();
			constraintsSolverComboBox.gridx = 1; constraintsSolverComboBox.gridy = 0;
			constraintsSolverComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSolverComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsSolverComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getPanel2().add(getSolverComboBox(), constraintsSolverComboBox);
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
 * Return the PointsLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointsLabel() {
	if (ivjPointsLabel == null) {
		try {
			ivjPointsLabel = new javax.swing.JLabel();
			ivjPointsLabel.setName("PointsLabel");
			ivjPointsLabel.setText("time samples");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointsLabel;
}

/**
 * Comment
 */
public cbit.vcell.math.Constant getSelectedSensitivityParameter(String constantName, boolean bPerformSensAnal) {
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
 * Return the Choice1 property value.
 * @return java.awt.Choice
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getSolverComboBox() {
	if (ivjSolverComboBox == null) {
		try {
			ivjSolverComboBox = new javax.swing.JComboBox();
			ivjSolverComboBox.setName("SolverComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolverComboBox;
}

/**
 * Return the model1 property value.
 * @return javax.swing.ComboBoxModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.Object getSolverComboBoxModel() {
	// user code begin {1}
	// user code end
	return ivjSolverComboBoxModel;
}

/**
 * Comment
 */
public cbit.vcell.solver.SolverDescription getSolverDescriptionFromName(String argSolverName) {
	return cbit.vcell.solver.SolverDescription.fromName(argSolverName);
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
 * Return the TimeBoundsPanel property value.
 * @return cbit.vcell.solver.ode.gui.TimeBoundsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TimeBoundsPanel getTimeBoundsPanel() {
	if (ivjTimeBoundsPanel == null) {
		try {
			ivjTimeBoundsPanel = new cbit.vcell.solver.ode.gui.TimeBoundsPanel();
			ivjTimeBoundsPanel.setName("TimeBoundsPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeBoundsPanel;
}


/**
 * Return the TimeStepPanel property value.
 * @return cbit.vcell.solver.ode.gui.TimeStepPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TimeStepPanel getTimeStepPanel() {
	if (ivjTimeStepPanel == null) {
		try {
			ivjTimeStepPanel = new cbit.vcell.solver.ode.gui.TimeStepPanel();
			ivjTimeStepPanel.setName("TimeStepPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeStepPanel;
}


/**
 * Return the TimeStepUnitsLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimeStepUnitsLabel() {
	if (ivjTimeStepUnitsLabel == null) {
		try {
			ivjTimeStepUnitsLabel = new javax.swing.JLabel();
			ivjTimeStepUnitsLabel.setName("TimeStepUnitsLabel");
			ivjTimeStepUnitsLabel.setPreferredSize(new java.awt.Dimension(28, 14));
			ivjTimeStepUnitsLabel.setText("secs");
			ivjTimeStepUnitsLabel.setMaximumSize(new java.awt.Dimension(200, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeStepUnitsLabel;
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
 * Return the Panel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getUniformOutputPanel() {
	if (ivjUniformOutputPanel == null) {
		try {
			ivjUniformOutputPanel = new javax.swing.JPanel();
			ivjUniformOutputPanel.setName("UniformOutputPanel");
			ivjUniformOutputPanel.setOpaque(false);
			ivjUniformOutputPanel.setPreferredSize(new java.awt.Dimension(426, 28));
			ivjUniformOutputPanel.setLayout(new java.awt.GridBagLayout());
			ivjUniformOutputPanel.setMinimumSize(new java.awt.Dimension(414, 30));

			java.awt.GridBagConstraints constraintsOpIntervalLabel = new java.awt.GridBagConstraints();
			constraintsOpIntervalLabel.gridx = 0; constraintsOpIntervalLabel.gridy = 0;
			constraintsOpIntervalLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getUniformOutputPanel().add(getOpIntervalLabel(), constraintsOpIntervalLabel);

			java.awt.GridBagConstraints constraintsTimeStepUnitsLabel = new java.awt.GridBagConstraints();
			constraintsTimeStepUnitsLabel.gridx = 2; constraintsTimeStepUnitsLabel.gridy = 0;
			constraintsTimeStepUnitsLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTimeStepUnitsLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTimeStepUnitsLabel.weightx = 1.0;
			constraintsTimeStepUnitsLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getUniformOutputPanel().add(getTimeStepUnitsLabel(), constraintsTimeStepUnitsLabel);

			java.awt.GridBagConstraints constraintsOutputTimeStepTextField = new java.awt.GridBagConstraints();
			constraintsOutputTimeStepTextField.gridx = 1; constraintsOutputTimeStepTextField.gridy = 0;
			constraintsOutputTimeStepTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getUniformOutputPanel().add(getOutputTimeStepTextField(), constraintsOutputTimeStepTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUniformOutputPanel;
}

/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getUniformOutputRadioButton() {
	if (ivjUniformOutputRadioButton == null) {
		try {
			ivjUniformOutputRadioButton = new javax.swing.JRadioButton();
			ivjUniformOutputRadioButton.setName("UniformOutputRadioButton");
			ivjUniformOutputRadioButton.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUniformOutputRadioButton;
}

/**
 * Comment
 */
public boolean getUseSymbolicJacobianEnabled(SolverTaskDescription solverTaskDescription) {
	if (solverTaskDescription==null){
		return false;
	}
	if (solverTaskDescription.getSolverDescription()==null){
		return false;
	}
	if (solverTaskDescription.getSolverDescription().equals(SolverDescription.LSODA)){
		return true;
	}
	return false;
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
	this.addPropertyChangeListener(this);
	getTimeStepPanel().addPropertyChangeListener(this);
	getErrorTolerancePanel().addPropertyChangeListener(this);
	getSolverComboBox().addPropertyChangeListener(this);
	getSolverComboBox().addItemListener(this);
	getOutputTimeStepTextField().addFocusListener(this);
	getDefaultOutputRadioButton().addItemListener(this);
	getUniformOutputRadioButton().addItemListener(this);
	getExplicitOutputRadioButton().addItemListener(this);
	getKeepEveryTextField().addFocusListener(this);
	getKeepAtMostTextField().addFocusListener(this);
	getOutputTimesTextField().addFocusListener(this);
	getTimeBoundsPanel().addPropertyChangeListener(this);
	connPtoP1SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP7SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ODEAdvancedPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(607, 327);

		java.awt.GridBagConstraints constraintsPanel2 = new java.awt.GridBagConstraints();
		constraintsPanel2.gridx = 0; constraintsPanel2.gridy = 1;
		constraintsPanel2.gridwidth = 4;
		constraintsPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPanel2.weightx = 1.0;
		constraintsPanel2.weighty = 1.0;
		constraintsPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPanel2(), constraintsPanel2);

		java.awt.GridBagConstraints constraintsTimeBoundsPanel = new java.awt.GridBagConstraints();
		constraintsTimeBoundsPanel.gridx = 0; constraintsTimeBoundsPanel.gridy = 2;
		constraintsTimeBoundsPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeBoundsPanel.weightx = 1.0;
		constraintsTimeBoundsPanel.weighty = 1.0;
		constraintsTimeBoundsPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTimeBoundsPanel(), constraintsTimeBoundsPanel);

		java.awt.GridBagConstraints constraintsTimeStepPanel = new java.awt.GridBagConstraints();
		constraintsTimeStepPanel.gridx = 2; constraintsTimeStepPanel.gridy = 2;
		constraintsTimeStepPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeStepPanel.weightx = 1.0;
		constraintsTimeStepPanel.weighty = 1.0;
		constraintsTimeStepPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTimeStepPanel(), constraintsTimeStepPanel);

		java.awt.GridBagConstraints constraintsErrorTolerancePanel = new java.awt.GridBagConstraints();
		constraintsErrorTolerancePanel.gridx = 3; constraintsErrorTolerancePanel.gridy = 2;
		constraintsErrorTolerancePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsErrorTolerancePanel.weightx = 1.0;
		constraintsErrorTolerancePanel.weighty = 1.0;
		constraintsErrorTolerancePanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getErrorTolerancePanel(), constraintsErrorTolerancePanel);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 3;
		constraintsJPanel1.gridwidth = 4;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
		constraintsJLabelTitle.gridx = 0; constraintsJLabelTitle.gridy = 0;
		constraintsJLabelTitle.gridwidth = 4;
		constraintsJLabelTitle.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelTitle.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelTitle(), constraintsJLabelTitle);
		initConnections();
		connEtoC9();
		connEtoC21();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getSolverComboBox()) 
		connEtoM6(e);
	if (e.getSource() == getDefaultOutputRadioButton()) 
		connEtoC3(e);
	if (e.getSource() == getUniformOutputRadioButton()) 
		connEtoC4(e);
	if (e.getSource() == getExplicitOutputRadioButton()) 
		connEtoC13(e);
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
		SolverTaskDescriptionAdvancedPanel aSolverTaskDescriptionAdvancedPanel;
		aSolverTaskDescriptionAdvancedPanel = new SolverTaskDescriptionAdvancedPanel();
		frame.setContentPane(aSolverTaskDescriptionAdvancedPanel);
		frame.setSize(aSolverTaskDescriptionAdvancedPanel.getSize());
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
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals("solverTaskDescription"))) 
		connPtoP1SetTarget();
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("timeStep"))) 
		connPtoP3SetTarget();
	if (evt.getSource() == getTimeStepPanel() && (evt.getPropertyName().equals("timeStep"))) 
		connPtoP3SetSource();
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("errorTolerance"))) 
		connPtoP4SetTarget();
	if (evt.getSource() == getErrorTolerancePanel() && (evt.getPropertyName().equals("errorTolerance"))) 
		connPtoP4SetSource();
	if (evt.getSource() == getSolverComboBox() && (evt.getPropertyName().equals("model"))) 
		connPtoP7SetSource();
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("solverDescription"))) 
		connEtoC5(evt);
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("solverDescription"))) 
		connEtoC2(evt);
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("solverDescription"))) 
		connEtoC7(evt);
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("solverDescription"))) 
		connEtoC11(evt);
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("outputTimeSpec"))) 
		connEtoC12(evt);
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("outputTimeSpec"))) 
		connEtoC16(evt);
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("solverDescription"))) 
		connEtoC19(evt);
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("timeBounds"))) 
		connPtoP2SetTarget();
	if (evt.getSource() == getTimeBoundsPanel() && (evt.getPropertyName().equals("timeBounds"))) 
		connPtoP2SetSource();
	if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals("timeBounds"))) 
		connEtoC20(evt);
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void setNewOutputOption(java.awt.event.FocusEvent focusEvent) {
	try {
		OutputTimeSpec ots = null;
		if(getDefaultOutputRadioButton().isSelected()){
			int keepEvery = Integer.parseInt(getKeepEveryTextField().getText());
			if (getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)) {
				ots = new DefaultOutputTimeSpec(keepEvery);
			} else {
				int keepAtMost = Integer.parseInt(getKeepAtMostTextField().getText());
				ots = new DefaultOutputTimeSpec(keepEvery, keepAtMost);
			}
		} else if(getUniformOutputRadioButton().isSelected()) {
			double outputTime = Double.parseDouble(getOutputTimeStepTextField().getText());
			ots = new UniformOutputTimeSpec(outputTime);		
		} else if (getExplicitOutputRadioButton().isSelected()) {
			String line = getOutputTimesTextField().getText();
			double startingTime = getTornOffSolverTaskDescription().getTimeBounds().getStartingTime();
			double endingTime = getTornOffSolverTaskDescription().getTimeBounds().getEndingTime();
			ots = ExplicitOutputTimeSpec.fromString(line);
			double[] times = ((ExplicitOutputTimeSpec)ots).getOutputTimes();
			
			if (times[0] < startingTime || times[times.length - 1] > endingTime) {
				String ret = cbit.vcell.client.PopupGenerator.showWarningDialog(this, "Output times should be within [" + startingTime + "," + endingTime + "], OK to change END TIME?", 
					new String[]{ cbit.vcell.client.UserMessage.OPTION_YES, cbit.vcell.client.UserMessage.OPTION_NO}, cbit.vcell.client.UserMessage.OPTION_YES);
				if (ret.equals(cbit.vcell.client.UserMessage.OPTION_YES)) {
					getTornOffSolverTaskDescription().setTimeBounds(new TimeBounds(startingTime, times[times.length - 1]));
				} else {
					javax.swing.SwingUtilities.invokeLater(new Runnable() { 
					    public void run() { 
					          getOutputTimesTextField().requestFocus();
					    } 
		
					});					
					
				}
			}

		}	

		try  {
			getTornOffSolverTaskDescription().setOutputTimeSpec(ots);
		} catch (java.beans.PropertyVetoException e) {
			throw new RuntimeException("Problems while setting the output options " + e.getMessage());
		}
	} catch (Exception e) {
		cbit.gui.DialogUtils.showErrorDialog("Error in Value : " + e.getMessage());
	}
}


/**
 * Comment
 */
public void setOutputOptionFields(cbit.vcell.solver.OutputTimeSpec arg1) {

	if (arg1.isDefault()) {
		// if solver is not LSODA, if the output Time step radio button had been set, 
		// change the setting to the 'keep every' radio button and flush the contents of the output timestep text field. 
		// Also, disable its radiobutton and fields.
		getDefaultOutputRadioButton().setSelected(true);
		getKeepEveryTextField().setText(((DefaultOutputTimeSpec)arg1).getKeepEvery() + "");
		if (getTornOffSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)) {
			getKeepAtMostTextField().setText("");
		} else {
			getKeepAtMostTextField().setText(((DefaultOutputTimeSpec)arg1).getKeepAtMost() + "");
		}
		getOutputTimeStepTextField().setText("");
		getOutputTimesTextField().setText("");
	} else if (arg1.isUniform()) {
		getUniformOutputRadioButton().setSelected(true);
		getKeepEveryTextField().setText("");
		getKeepAtMostTextField().setText("");
		getOutputTimeStepTextField().setText(((UniformOutputTimeSpec)arg1).getOutputTimeStep() + "");
		getOutputTimesTextField().setText("");
	} else if (arg1.isExplicit()) {
		getExplicitOutputRadioButton().setSelected(true);
		getKeepEveryTextField().setText("");
		getKeepAtMostTextField().setText("");
		getOutputTimeStepTextField().setText("");
		getOutputTimesTextField().setText(((ExplicitOutputTimeSpec)arg1).toCommaSeperatedOneLineOfString() + "");
	}

	// If the solver is LSODA and if the output time step has been set, set/enable the appropriate radio buttons and text fields
	// If the output time step hasn't been set (for LSODA solver), set the field to blank; and select 'keep every' radio button.
	// This will also hold for the case when solver is not LSODA.		
}


/**
 * Set the model1 to a new value.
 * @param newValue javax.swing.ComboBoxModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setSolverComboBoxModel(java.lang.Object newValue) {
	if (ivjSolverComboBoxModel != newValue) {
		try {
			ivjSolverComboBoxModel = newValue;
			connPtoP7SetTarget();
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
 * Sets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @param solverTaskDescription The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSolverTaskDescription
 */
public void setSolverTaskDescription(cbit.vcell.solver.SolverTaskDescription solverTaskDescription) throws java.beans.PropertyVetoException {
	cbit.vcell.solver.SolverTaskDescription oldValue = fieldSolverTaskDescription;
	fireVetoableChange("solverTaskDescription", oldValue, solverTaskDescription);
	fieldSolverTaskDescription = solverTaskDescription;
	firePropertyChange("solverTaskDescription", oldValue, solverTaskDescription);
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
				ivjTornOffSolverTaskDescription.removePropertyChangeListener(this);
			}
			ivjTornOffSolverTaskDescription = newValue;

			/* Listen for events from the new object */
			if (ivjTornOffSolverTaskDescription != null) {
				ivjTornOffSolverTaskDescription.addPropertyChangeListener(this);
			}
			connPtoP1SetSource();
			connEtoM13(ivjTornOffSolverTaskDescription);
			connPtoP3SetTarget();
			connPtoP4SetTarget();
			connEtoC6(ivjTornOffSolverTaskDescription);
			connEtoC1(ivjTornOffSolverTaskDescription);
			connEtoC8(ivjTornOffSolverTaskDescription);
			connEtoC18(ivjTornOffSolverTaskDescription);
			connPtoP2SetTarget();
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
 * Comment
 */
public void tornOffSolverTaskDescription_TimeBounds() {
	SolverTaskDescription std = getTornOffSolverTaskDescription();
	OutputTimeSpec ots = std.getOutputTimeSpec();
	if (ots.isExplicit()) {
		double startingTime = getTornOffSolverTaskDescription().getTimeBounds().getStartingTime();
		double endingTime = getTornOffSolverTaskDescription().getTimeBounds().getEndingTime();
		double[] times = ((ExplicitOutputTimeSpec)ots).getOutputTimes();
			
		if (times[0] < startingTime || times[times.length - 1] > endingTime) {
			cbit.vcell.client.PopupGenerator.showErrorDialog("Output times should be within [" + startingTime + "," + endingTime + "]");
			javax.swing.SwingUtilities.invokeLater(new Runnable() { 
			    public void run() { 
			          getOutputTimesTextField().requestFocus();
			    } 

			});		
		}
	}
	return;
}


/**
 * Comment
 */
private void updateSolverNameDisplay(cbit.vcell.solver.SolverDescription argSolverDescription) {
	if (argSolverDescription == null){
		getSolverComboBox().setEnabled(false);
	}else{
		getSolverComboBox().setEnabled(true);
		//
		// if already selected, don't reselect (break the loop of events)
		//
		if (getSolverComboBox().getSelectedItem()!=null && getSolverComboBox().getSelectedItem().equals(argSolverDescription.getName())){
			return;
		}
		if (getSolverComboBox().getModel().getSize()>0){
			getSolverComboBox().setSelectedItem(argSolverDescription.getName());
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
	D0CB838494G88G88GCCFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DDCD5D53638D3E4CC115104939979D1D6D4D4B4D1D9D1D1D9631B21F98EC3134D33F24A770F66CFB34C8C53781F674C7F480702D2E1DAB905050993AAA1A0AAA28842C5D1D1B149A8A939F28FF061FEF16FC150F0FCEBFF2CFD4EBD775CFB014677FB7DDA1EFB56DEEB2F3557DA7BFB6F03347A236959CBB2D6CB5212EC097F37E309A4D9EE16244713372FC0DC55A7C93912663FF5GAB25435B32
	60598CE9339313F273253C5C9CF86F007711B9C9396F416FD5524A5C55B360079FB92953A5690F73FEBDB1301C769DA8E799FD4F2F4A03671BG1D00A34F18CF087C622AB59C9F427188CBB2A4A92A1850B4D695F0DC9A5064G2E9D40D64C68C360499438243A0A53F5DC95AD257F441D718649C713A742C24E1B3311BD8B253FAC095DC8E459281E04279E5E0781C861B325E7CA9B60D9535A5F333B6B14D5A9BD5DA55BEC259E276D346CAEF5DA64521EC1EBF575B17A5EE676741BE4CF175B6AD22CCE479E4B
	E93323CB36341A9D32CD12CAB691B9F1D5B9B4DFC9CA879933A8AED5A632DD70BB94E07AA644EF69A5784570FB7566245C124DD85E5ACFEAF8F95A3F3DDFCA2E30E73D65273C264D2C1C364DEAF98B6D4FE77C1D7AA6127AA09A52928196GE415A465968118GBE20B1B1ED47A6F86EB925B4BA6DF6A8134B55E033388FC149ED703B3A9AD2B86EB3394B6936C892733B3B2A44C01FC04078A60C190F59A4FAAB46C04CE7F7CA736F79E146B2831B4C0F7EB623230459C2A9D1ED42E4BF60D7F6C409F70CFA4B7E
	C2524A8E7F5DB319598632E366BF13994D73095748DED4C2FC5D59CDFCBD9FFEAF6179B23C1D62EBBED571ACDE7FC0E3063C37C3DA7C27989B6DEF22AEADD9B7C8796557985AA17271300C42D226C3D529DE174CBE12372514495CA7F459C2710BB5F8264BB69A2764BDE2CBD26E5C7224DC96DFB32AC97D4D2BCC4AAD862887E887988608G082E4498CB5931E514B156EAF6AB8D03D61BC5F6CB0CF7C6553B7014079537F93F4323108AF75C6C361ACF59E40F44E9EE2BA2B6B86545F7970D7DAE1031CFF648EE
	B32930103B59AEAB325B43E8E6D52DA4B4EE192636D96DB288303BA44CB721EA9529DBE60F72394BE2D6E41502254F29A2BEB9EDBE8EB9C286701B5965E73B083D66C07997G2489BBB46EA4799D145D50DED4D737B81D7D032EB6D29229FE9B3173DC5AF6E4416F02EDE8631F5490DC883CC7ED43FC4E6EFCDB130F495AC554B73B4FE09D6B6E4EA73E837AC400F400555BB1FEFE5B0E71E3FA638E296419490671D3787CB319A55BD95C18366B6B110766CFF434C1DA98C04CF66C8F96496D816AF2A2570568B6
	16B6A01D53B399AB3B8349486675A6F38C79E35EEF8A9FD66DA6BECC84DDD3GF2F6206D6F5DD948E239416C11B1FA0F38A170E40B6A8926E7EEDF9857238B60BD1EF7A3009360EB62C300A100C100E9GC7A5525A303AB6GF0B9409100BAA29360980095608440C900CBGEA81F611BA823093609EG8885298760F300F7G7A811A047D36D13D8A21CC4DGAE00C800840094007C2A245CAA00CE00D000D800F9G52CE2867G2DG43G538166G24812C29067A8F508B908EB08BE081C04E2E245CB200F600
	89G51GF3819681945600FCGD78D464E2E5E328C660351786E9D0DCD4762E7BD6C64762B555839115BEB9F27B9481FC7380F0EF15BFE4171A738BFCF7277AE6E0F9E6E2FFE0EF7F23F3E49FDBE44FDE52457A51A5F170F32AC53C6C9F74FF734597FA6E0317313B145CEA3674B7D0E32C96CEC83588A30816093GB2D0A7F13316532C644F75920B550D920B318FB87ED3GB23E5B425FC97B35C3E23138D9E2F1C870E44C7B112446BE69CBEA7D68D52E793DEE14E5F9EF14F43B7E97787CDF852CCD3F3D1A58
	DFBEADBB94136C129D96786161E9BF2AA6B142479F5DCE41B30B62DDB0DEE9048102C7609F29A67EB6AB0A5BFAEAD0110F490AE2F5742869CFD1D91E41D3AEF52803321E2EA63EF78E41F8C550A7D5133E3E4B69F04834F391ACBCBD190EB5E41BEC87259B1D038E0561FF350B4C537A65B3FC2C73422E2592992B58864938094DFDE24DA4D6DDEE2B9D3AACB27412D2EA48B86495B1F7A39089B8B2070B07675C9A9CC78C2D47F1C45159DB2504132E8C23B1F164CDD7E6145572F1C42DBA0EE0BA7DFB0D316E4F
	50B18F47FB182EAF1412B14791D313572CAB13528FABE49C5A66F4BB0EF4F79BCE55981DE79729B30CG0622271C8D4EE1BA8A15D87A2B4709BE60B645695867F68E3A4A59D8E0200E0E17C17FE10070DA9C8B9C2DA569B88E630300C6274D46DC85E9F3EB09BD9F68DE4D6D918D6F89354C1EEF0A744E9ECCDF339B7A4B5DEA3AEDB79D2F772E565834DE134E74583D1B5846A38F8C4AE099B220BCA87C5E347BEDDD5AF19E83AC7D50EE9283B64DD8D42AA3B2C37BF0EE9E4765A56EE6633DB4F8FE887890CA77
	D9BF52355431F4CB9D9B93AAF5CCC716DF218D6966F23A94F8AE86A82CE779B13AB4C1D7D94F68DA60598B9066C5B75B0EF45175CCDE82BC678224F13A50BD04EE1283693276307C8A763072546CE17AB13A83CE24CB66F429BABAD65E8C9752AD5E43642DEED073A5783486C2772C20EBEDE0632C5E86165FC8832333B4923AB383C8D7534846DEED0DEAFE25A23FC6B75245B632719966B727D11B5FFF8A3A92939B3B19CCCC4FB6934B0F515D6BC1BA1B090D75C2CCCCEE8427E3F639D8C13A19BCBD5E446341
	24355F2EC124DB4469721B0C687EAAA1DDD99323EB66F43DCDDA3A8716A0DDB8270BB6249B48C03AD9BCFDAEFF26F851ED4EC43A7C3DB00E85E85E4B68A66C55523DA66862811F8C104E694A76E969E23310EE9870617B7C51D932055FGBF87A0E51F915DA7CB112E2899E28E202359C83FD785DD84244778253BFE9952A5C27AC23FF4C7F3C47D584F63EE3F115F5E4895F14A695AB81DEB3F16EE0E201B4569666EB772DB41BA24CB65F4D9873C73E3F11AAC68D69FE0F3081283EA3EEA1CCE7DD86847535B8C
	737BEABD52A98758DCA4CC179FEBDF0A0A10AE0A274741B391A06D0036FDF9DD50659CE4F40587195C2A03DE71328169DCBCBD14BF27F3BA564F7DF1A369FB69FAE41F7934F93854B38463076A6A83343FE3EB0CC7F766F07AD987F1AD719C6DCB5360BD2785F4E841FE7A67EF60DAE2C4FF34E4CBCB4E7437F6DA55427468E8D17B69B951C3192C8FFC281E480C023418966C7F666C562EE76C71F81CDDD63A14427B1621BAEDBF4EF0A3F54B6159EDF5FB147DB0509A46FE6822BA22AFD9EB61E83E7634002E0F
	117EB98164AE82A8BBC470EBFB48780674C92C1CDDDB311C61E7EF12BAA7CF49D861271C4AA1D63E08C3EAB9A37F31AD13493B348765A5C2FA56E114B71B4AA3FD8B7353DFCB579A7A29C19DBDC838FEF8EE2F568771EF1D732BDB4DE16E03432AEE2D092FF35D6E6EC35D22A0BD99000DDB367610790047E8604369D7C9B57DC4FF25576AE1BA7DE1CFBD368FF75EA04DDA9616E134465CFBCBE4C65E9126CB6591D5A7696567B3D683B03D3E6D4F63719882B471C7D02F1704CC763E5C662D439D9FEAF548B1DF
	E228C3582BBD9969DCF6C12B2A43C872739961492843FE9B5A4682B4D12D28C32C0D4420E2354B479459454637AF64E84737492DEC3DAD7FE8D2EE51D164FB4E0B0F49783B9DE554G1DA2E8A758C9BE325B6DF437B9ED321B0CCFA5BE164E28534A0AB94AE42542D37A8C79EFB764E7B2DF23B249DCF58D50378A1EB6875A120D916F78CABBC68C853AD800E4C17F2B9E629FBB9D6DE25E77BB50EFA9C0D7F88CE9BFF42E9471649DCBAC76075EA3F353C13AA64AC653074DF659E47538EC66B345AD3E7BA84774
	D96D5AF81C570D9BD6E9E320667CB706B1907AD6EFC67BB1667B89475498D88D7EAF96B160F1E2F99201A62BCD145D0936E2F41BDDC8D783B40A207B010B4C412D0AECAFA57F9CD320FC0D3DE6C70FECA9A6153C54BC241452E9E269FEC8EFA23FB6F2DBCA8318E78C6437308D5B09553B49134E3B047D6A36F6CBFC5DD9B6180D7837202EF26F5A5822E9FBCACDF237F95026F865358B5B207FDA0BF6AEB24D142A527B8D6DCC6B5867FC3E7639EA67B577ED4BAC398F5B489B86309EC4GCD6267E83FDCB73133
	595DD34EE40EFC09B2A7A446C9EBD6DD61D7E656F1A62B6438AA33792567B3BADEC25F5A5CE85F912009BB0E76AD5ECC5661FAE445A746763A1DF692174554C825B670E369B11816BBFA40EE2309D1D1CF5E74E0F9D3C1EE5197D85EC18F16173DDFCF69BCB2197F499692A20C7F85415F813C91023FC9716637AB5EF19A2308F8861E2CF664DBB508F44C86E19505247DF030599D5B9CE963AD5791DA96CF714663ACCF4B2008FD48CF91F97EDC72D767995FBF160878861E05026F0AA5015B0AC8477BC237A303
	0AEBD0E19A51618CE805793FA472AF789246EEDFE27E7BB33CEDB2A583694200E63620DB12C970D607D5311AED5633F271C6A67F6E4F4978C5B327BF39DFC3F3DC142FAD9373DD8CF9D6FE85E3B95A2EEE6B53362BB640C7G4C7A8A65BE14C976CF3B1DDD031ED27AEF0B532368DB0B3DA401B7975CAE165CB29AAB4AE1F9C8EB979E2DC63C3B50076FE60938GBD32CE202E7714EAF52D81FCAFC058895475DC56F85AB7266FED9FD713D8258B9BDAFD59F807E665233A48E4176A6767046E53C12FF9C2771CE5
	C63A67F7C0BFC6792E48C63EA240F5F4A05F07D6A33E088EACF3FA76785B745625A8B39A72DBA0E4CE3169E3E24D57B0E77B9AE55E3FF42CB141643D3D8C6535C2DE21DF233C8D7DC672923EC65BBCA578128117778D72AD6B567319A02D779B64BB1BA35AD44045FD037A7FBAA7F0DB30BD9779D2012768A472DDCF5BEEA70D8E5A7E3864AE0C775C4F2BD93CEB03E72FD536D9BCC67D5D912FCCB47173FBA13B8364C688596D4B0DE4B31E26F5223EGFDD6A772442C73EEDB7E3ACE0C6F00C691F4160F311DE6
	F4BF7ED83405C033D050152E8FEC370B57E37E85E6E8634C48771B755E799FAAC23AB02019AD68620A485C090CC99B1C038E0B874718F7FD219DE3AEB633B1E65529245C16D3487B028FAF13755186AC0B85E823847D448DFAFA6647DB4E1158D6822C351EEC93CC1A767DB9A1A799E464F5211C7AFF98375BE78E13B9125D5C8FD959ACA4A3364FF3FC277E8C0729CB5DCBEE577CC6E0E79B7EC4674736AEF636618D014F2AA578F08D1E7B0A521377B8C8AB3460FE7CA69331F5983CC783A4G4C83D88430C646
	F97172FA329F94703C43FE078253E6336D1857C6G1EBB28A73669174F707759755AB1A433437F69A6F62814C73F0F4E7852A9DF4B987858D8EE63D75AB3D8D9AB8FE4909D0D46F2A3BC6FB0D99D4BB15B7DC793E90F92811F82A0F5034DG2A3A51EE7B771484339B8EEF3841D8192E68991FAD2E6B992BADD8B9766CA365E8873D9500B00019G33C5B92E5E93547FCD43067EE7FA5D5F976C0C5442EEEF1D995F13C179F2FA3C79D8F92EE9A6EBD30510D6835086608208G0885C86E41F23D3C27A1D8397660
	46D71BD37CEC54EFDE213E5F6B576B1B2A534F381CD77A70156A4B59EBD44E3C83341C10D6835086608208G0885C86E45F2EEEB589554FF246306B252E279961069F944B6BEBFFE9D14AFDDD7BE56E66EACA2FB49414EB2624C009FDB646796BF2843F30BF9D6DCD3BCC24F4018603D83C03162B90D0F6C63AB5727C179422CC6655AD03BF59465EA72DABFE125E3E52B96E51BA94A7692ED5F6743FBBAC0DE9F166D0F81F5AC82BAD23666BED5CF463744912CEC36BE23327558BE108C57FBF19A4F7B787ADA7D
	1EAC6963C3A04FC8GD64E05DFE1B9E3C5F99E7651EBF4F56C27BEFC9F6A5ED37A0C6A584587C99D137AC1BF00CA00D6G85A09CA02E9F6B5844065AD15631C640B5BB1D7D864D8857750CF3FCE53C5407CF5F0EA4779B71153B46A72F4E07CFAFAF55D05E52017149DB6D43271717E5B372E166A1624382C82B82E8853081048344G4C35218F1FEB58934C07624865E87C986D236F685A157B0272259916736243241CB9F61843G54G34830CGC482445B311C1FB6866D77B4658C5E6F395D637367849FBE1F
	F8359B4601E7FCF67DB8A8DF2A213C056314770720FCD98EA33E9FA96333E7028F1F5E1E850672DC5278647D5307CFAF2F52D0DE491271492B7661534BEBB11417B2CEF92F8715E7B114B7BDE3FC726E7461534B9BB11477D5667864757A70697B44881DBCBEFEA9A66D0CC19F5FA68FABF48D04757159FD7ABE1E2CE545C01E898E6C63EDFDB9DC1FF98E6C638B3246D71ECDBEFC7A7264BB0D4A73EE43074665794CEC31BA590AA02ECDCCB69C3794C33EB5CEAC53EFCA31CCEDCEAC530B63AC53EBC14B9426AB
	935321EE996E294F94BADC1DBDBE9DE6787069F518EF284367D654A1DD68F0FC6978F430787069F5A8F1F973B1FFC97DC6B1E8825AF6977A6B67F4EF141EFFF4211EEB160DCF4FFF787069750CF699592A239FF5C890BAA40ED307DF8555E111212D52ACC6364A9F007144G5AEAF7B70E49EB86D04F89B96353F3128FDF296EDD99B0622B499D1F3C83BEFCBEF549D05E1F47A9AFFBF930724DB414F7F05D7864FD6943271797EFA8AF7363710DEFD68665CBB61477348F5F684A779FBEFC7A72259A4A7B7E7A71
	156F1A20FCD9EEA33EE3C56313E78E4AD72013477A2F61E3E4CFE8B46BA4EC99DB3B16704A97D81FAB5D381630196E235860BD84A04A0D75FC43067179ED2B8F1F5EEFF106F6FCF11C72DE8BAAAFD1A70F5DD73353F3E88BA1ED91G439D58C5F0859E187BFB508E7FD5C35A49F6F8378104824C84188DB0CF50AC276384F2E6051C31A367024919DF521E26891A6F57F4EAF4A07CA44FC07E5702CDC79B4A65135FB3B5E9CC56CCAFD9AB24315DA74B563D13BC5F603F35F7CE587CF22B497F7D03EA13717D03FA
	13717D033D26158C8FF9E8EFD3E27AE1D3607B8947CDC677935652727F126E3D3B879DE4DB080AE575C99EA6F7CCE0967C592003CD1109E40FFE4DDE42F977E3C748FED73E02736BD4D30564BB3FEE32D895279BB3E9134D6EA35D5DE2F24D7677783AF413FF7B7D264958FEE9CD4676FB3DA9307D7E569458FE7B77795725F51F31AE4777996B5231AF30AE16FD7EF4E17EFAC5A16782FD6E0E10C3B1921E8B7CEDDFAE27E9D51C2D65A26F994D7E4B915DECDC0EBB1BC90CFB1C036EAED92F6B3D4DEBC41A3AB0
	C2365C09BA7C6E8C65D74C6E9E59272CCFD2FE16660F7FE74D241E1E32DA2CEEFE830359615F5EFD0B5A516311590E3B492C18F979E775AF6765EFD21C074A37223CF78F78AF7F5A834665DFFF40FF79B79D88DE7E2D877C173F7AC07072B79E70978FFCCF57267575D651569556856AC7CB94FFFB2ACF507D01FA056D1DAE9178C40AEF5160591E6A6794CF5E5BA1ADDE41BD6063F62DDE15C22F3A00FA958E7A536BB85D532D9C546BF512626B8775FA255420DEAD10968B406226707CFB42E7B4EE0E40G41ED
	35483486B11A179D6F893F78523072EDF2E84BC76E76313144F0AD4EFBD71F46314473A7296D605D82B0F29A7B3ED73F8AE40B08533E366067DF04EC086F46EDA21E2EB868BF3EA38E9A477714037E63FB46416071FD5BC17F71FD774160717D604140719D6B5416F53B40773AB478C3AA3EF6E0857A255B6D34B7D6F0DD5EB8645FB62B8F995B667DC37EED336ED0F05BECBA645FB66507025B267AD0E05B3C68DD5656AA0C0343816260301FB8B89CA08E8E0FA28E8E87080343230803437AF61E1571D63737BF
	ABB97E20473B6CBBB87E83459BDF0177G95ED9C158B9B5EA2E97195825F35C40B5FA1709FE5E871D5827F2C97FE2740DF1539DCAB378C5B09DA3A66C7F40D966D44ABD652CEAC0077C5G79C358CE8C84BC83D08CF4B5C3EADB51BA04ED7031AC2DCEDBC59B75F4AD2EC9D906D0761DDB086CA8F80F83C89432FFD7CAE4AF9C72EE0B9671773CE1FFED543B59DA59EA7BEF72D917216DBE4F2752EFFEBF49763263D6316EEBB54A2F19675361B73FAB16FA65D70936296A47765BB50C36D9C76FE8C443FB82403C
	E1344DAF8D777453384C2CB3C939EB4E2872CB4E20DF0EAC73123DCD4CFDFD4EC050F9994F238D1E3D1A7CC6C4FE19B9DE796D4032944B686770B3D816273BC8D966407B8200C5E730AC0D5D01E2AC7F2CBFBB5E6FAD3B9C711EDCAFFC8562CB17FB6137A37E22F5DE78AA44D7FF6CDD1F4BB9BEEB3D977DCE24CFD86FCD3F1563AFAB52765795A29ED33F88F8B640205CEC5CB10D2E8FD41D550FC722A95EF4D6BF9EA94F4171C8AB24451DC55FF5EC50162322CC541FFFF86105DDC3F612BB5FB673996720A2C9
	A15F123C2F5B09EB16EB603D8C209D4076ADDB1767E7260708DD3A5DE6BBF4A1F42255B9C2F84D3B10B7916897GC800AF84289FC19DC34B09CC3AE2A27C786DB952476857D3781951375F60E95AB3A9ACEDC7AE39DFE6F42E0325AFBCC77C42EE46212F53691D7683AD66D3322D4D2A402C131D051DABEB4F42C60F3072A60E609C7449A3C48F51FF72B992DB5358D107EB9A8BC6308E7C5BEEC944C5126677095D013ECD73DD0149D0AE288CD66663224CE567304C9D35EAB9E54D6F5F56DC78B2AF2B3D70B24E
	55E27D1EF00E556508F3EA1D0EBE672FFE9F6DA475B57E9C2B4759825FC171F3B5F8D63FDFBE09E7E197C2DA73BFF16DCCBA01B60F79A75A3CC174670B8117FFBEA937683C2AC7957CEEB92F6BEF296C93349F6ABC4FE4AF93789E0A9F566079BDB3BA1EA06F611016FE9EF58A6DC31D5AA7A06EA85D270A067784011BECA138E4F8CF19003AA7556A5B4B4593D4DD733F674F1E95E5C417226FB19D5F9278860AEF5160196EF1F4BCC25E95C8CB7B9E6A34F48B5149F6D1D26E040B90F70462A260FD264045ED41
	B2468A5C09E5C8A7DD0C3878E5C817A7F08354969D70EE9338A4AB528DDF0C3628957D751C0BD93967DF2C16BF6DE2FF3638167AABABC4FFEEB91A620BC3746716BF6943F34BAD10B6AB84F5FA0D6E5995DD1214DBF98962BE33231E758237060EE94261BDDA6072A8EFBC3C4F9538E1CA37905E53856E79FE4CCF1AA86CED437CDA856E93EA9F8B3C8FCFC47BFCAA6BE3A5EC22EA1369937D59E70F0514B7EE22BE4E8FD1FC62C4FD1C7F228B63FCA124B5DF0ABAFD55AD6A1E40E536A3EE71252867CD8405678F
	B43FF764DC78762975024AE076F9E227D88B89CD4A8D8945B1C1F927579986F211253AFA1F5BEAE9B077345091869BC72E284E617C7121B80E5CD9CD72CC07773C6FC37B75FD3C33BD7C8F3C979DF3738DD2E72A715D7485ED67B2EA3E4F7C5F491F64CCF9257D798CA67352DDA8B38A52133FCFF0776E527677E4ECF0E5865B57E0692FF8251339B37936171A7E779AEDBA517751ACED7AC12FF4E223CFB9BFAB5BCFB7E059967FB4D6325D9B62771B25851731B25573E77842EB1704ACBA1B493F87506D67FBG
	F404434A3F2C9B774207A10FC517915C61BA937A75B25F3A13AD61DE980359C0B38F609D7E1EFA19E62CCA73330B7C6AAF774DEF19AE3FB620693DDC4DEF64F2B5BFD637A2683CB29470D117E3FD0B58CDF03170BEDB60D655E39DCC399C6B60971D84D79516145B960638D394678AE35FBA61EBE6A7C7DBDFC8DEE329AB2A5C0C4E514A986B778833056D5FBB01368FB750936FE1206DE7828D018FF89E334274367F1276693381BFAF8C6DFCA3452542FB4E95083B17757D70DEA6F0DBEC680FB601D344F8E066
	95E897B36D07E641FBB2C03A405BA9FE4D24245C2AC96CBBAC7C5C35D5EBC76479D734EB33046C3CBE2D0CECC97D6663F705CC612BE9FD682B76C941FDE5831A8913D8BDA17808C92A2F58FC66F2AB3D87E5F5D86D0376E3563332B4EF923D3BC7EDBE9DFE47CC927759AC24BCDDCE5B205D6161F4CF50F199493F60CA74572B35685792016B95637D56AB51DED5E5244D1F8E6FF1G73G96839405A7651AGAC61C857CC69C2425977F24279F7EE489D4588FE671051BD3845456DC072A34FF97CB97DCA55D60B
	2F74359B11674F7616GE9C5C2476704EC5C1BC55FEB6B255177G5F7013B701378224611E71AAE9AD9DDF623DF066174AADF80EA0BA9C7D573D055EC7140715D961645B5B39647312BC2EFE3A8563EA31C1F935B1C5E0820F2B77F8DA7ED5EADC313CCB5A553CFF318C73AE384AB86F6CGF917DD250F5927B6F9176348172A2CD72DA82B460F2CE59AD92DC0C3GE5D92E5237E5ED7D580F382E42D83E1662C260BDD260560831EE7CD598935368F94B3C9FC0BB84508CE0818881087A81525CC9E9E2FF403E49
	B477876C1BCBE9704C3FDA0CDD7BBA834481814C1B642B4567695EC3FEE09C4B45C21F6620321671B856472D3E7FD961079FFD3DCA4A32FB7B517D156AC72B9D7DD8F6F570F64E84B46DD733F60E64EF3BDA9FB3DB7A0D5B39FD541743C09FF2B5561375B65FF62E1A76CBF49EA4682E6C77567F06932A7E697D28FF02017E39BA7D1301A6C5237FE20D7ECC7EB0151F9311143BBA82656F367A6A494EF892BD6BA3B0B6CDF46D35955EAD827708180F04C6206F7751B94DBCF8CF85481B0C78E394DFB819FD672B
	DE601F6B7AAE7385A2034822F1BA59F84C37D27A43F712013126ED0F3540FCB5500D3E6AB450C39FEB4AE456E6202FC2A76B47326F37E3FE918679695B3BE820091F2C0EE567EA7263EB01BEF712038163FBF61CFC7955E365E3F3629574DB2173A76B5721D7D3FC4AE47DBAF4DDB54E8553A12D7D07F8877A128E52A646437BBC00B4001CEB208D85B0DD036B2907DA4977AD825E19E9F33ADA645332CDF3340515AFEC57D84B4766DC0FAD43B9D75C0337C933A61A7CFEE72B7D9A6677E17EACFE795AA564494A
	37669B7AF793A0EDB6C08A009489759A20A4924BB7393504150F9CD35A23A8662EDE7A15B3F16849AA8F29E832084E4720DEFBA77511FC3F06BFFB79F3A4D23397C467F8676B447E8C24FDC4F50076C6427F526A0CF02C5DDB53A9899B7DBA8FEDD4F07B0D1269D9D346FABFB61A43F5D8F32DBA67AE9AA84CE47ACC957A34DEBBD6FDAEB213B756C65EDECB768B1CAE73G395C4E5A4847EBF1BE9FFA2D78C656EE5F3D0C47F763FEC45435ECAFA38E1EF32EE55F3EE2F5FDD3BD567585577A9FB76177625301A6
	E70ADA578B26687B293F9FA3E3BEF2FD3F47EDD61CEEBA8D67E9614DA4CF6FAFBA61B7DCA3BA757B8FC44762A9F8CE3C79845A33E60A11BDF15C7660A955F62EBEDF5BDD5A0736D326206D9E6CA37D0D4BADF74BEE37EC213DAB527F02520782ED0420178CEC7D02381B12B00559FA9EBCD32730B1BE3375DE9B5ABA6BBA63F6DA5B2E2E811A126BD4DB57DF276FD3EF24FDE58B605B2FC35D2AF6907B9FF1518FA81CB65B3476AF4AA65F3A453D244F9DD6454312719C48D2B20E54FCDA842844959C6E2769FDFA
	BF117205DC075FD469379A25C7DE07FE1C5B07FE0C394E580F7EFDDBEED1FF77492A1FAF3310F3778EF928D0FCC346846ABB489F1DA85EDED6D2D8D136916B7774A16DF79A2A16DFE478DDE6828B390F96716762F726ACE172FEE54900A7B9FF40FC7F1E28E7E5D14C774D706C08E267F3196F4F0B75A1A54A7F389E6BD9A850C4C5293E0F0D525733339F29FBFB38AF1BD3C974601FB9E3EE545CB862E7AE9A888D783E5B69367B21795BF6524EB78D3BEC56AE2BEFC6C207F75EFADBAF0FEFD072F1A87D363217
	AC2F741B06D67948E1846C7E4879B2237BA371E00B39D1986F87591C50BDA8B37F7E7B892DFF732758B250EEFA1B27F0BF24C7296DB70B5145DBB0C60B2FCF4AE57176779E12BBCB4F4A4174CE11DE532B263FAFE2BC727AC0ED55C972278FF8DDBE570F6D525C6B4537997BFD5B199F0B7BC78B2FE73136981E79D359FA820B35ADE26ED1BC55381D51C28D5034CED5E34DB2D59FEB1B1610B66520AC3B76A8071C9EC55B26FC3C65ED1E56842D7A993539403A597D26B65D2B3D19747EAA2FFCD5E6167E7AE9B2
	BEE9F5DA9D58D0B17CE63A0E43D6F8ABF93B345027DDA276F0CD45F62947E6149EA252AF72E937C8FA24C83F5710BFE6AA36EBD36C68730429E3ED57AE6AD3FF07116DFDDE4EA536G6DDAC5B934EB959B1176280D78EB3B08418DB654E7511875394B2E7ECEF2287A94D88335335BC11FF2210F4D0A7A30775F14794632DD966D66B45EEE4233E39ADB93E3317C0C185BA8530CE3D93B9E928AB4D153B46D66B4FDAC7FDF079ABBF81E6D45F54437EA3F67910148521FECD1F93672F81BE851478351B9FE9A464B
	1F3B0C5267CE43F839219B7D33F01A717810597FCF64D8A1776941F664A918BE66B1AE7933AAF84FEA177A7BFD4D6F266A8B3FC7774D851541669E13BA7C1F591C5CE1FCE6733A8E63BB86B3BA825FE73835437F5F2B086D507E3D8AB6F7CA99A0F151867EF381C4GC48344CF47F9536E2FBF16683C49E04E54E469A1E7C649DF79D2445959EF7C1735781B60FF9B2C7C9BF2B66A1493BAF6BB499F71BB3EA17ED21CAE0AE4381AEF080D5C561EDE0511F2FC43B7749C015CCD51ECFDE6BD0D6FF9D02E546938E6
	F3162E35674FC84AAD1C013897AD380ED3A2F09369FA4F883CC74D40351A902F336E31001FBDA3707E873377DE8FE9BF6681EDBA401A9B201D81B081F4G38GC2GA281E28166G4C87C883483A916634G45GB5B722FF3E6EA4FF1F6638394F77968EA07906AFF5125BECA5EE62F1FF23177E2D101F6546407A73B5BA7AFD5CEE334DC36C4D569DFEA36E898CC39E31B7E21BD2D107E308149BF1EF39137EED0E4219C939D5G2DB31176C4BD36A53DB3475A16FCCC7EB493EF13FE5A0979CC1F093EBAF4C2DB
	56F900CF1DB91A32CE6D53161549EABAAD09745B7A08CEFE7A1F4A8A5281D56137A91776230D241B206CB7E1398F0BF1166BA6345188250D06778400F902F6C87421E9B70D55C63D64CFD771B2DCBE2C1661A756C0E5A82FACA95C8A1395A67FD9B10EAC3ED9749BD65C4B6E3C996D7D0C571966C840475CBC1A3AD1F5066415G3473EF46783ECB5E9CA03EDB5DCE176CD64EB0AC7DCBA93896A6E2F2B164D5F98B6AFB69EE34376D9634773D14F6A63C4783A48B5AD831E612FA4BD86D7D4CD9B5A66FAF43FC0A
	22D166AD9B094C8EF8D7G4222F12D6E22EF899FAB7B1F648D7E4A8E2D6F81705711D3FDF21752E6A61381E67BA5E5DA5B4FA47222835B1E7165FA5DD5CF841E8523627B484B57694013F36BE86AD6C43FEFBB72BBD1C78AA00F36DB518FBBEC683330DB51FEB27DFE7CECF81F8F10AEE8BB4538B76F36317A6C259155E79F74E3BE265BB06F29227E0D885C83629B90F18217AA68D2EF93FBD7DE5FA9AB3A9D5A005BC7E35FCDDE6750DB0027B3889F3365E499ED29GFD4C6D285B73B25AF2616DE84B9574EFB6
	9444C0BD81E809C15A25BD62AEC14CD8ED197DCF55168FDBB01FA811F72940A547200DEE71DAEB4D3B837A65BBC6E32356F6ADDFA570B4876163F7CA877C0FD57EBC90FC2CA239C999E498E3341DF1713BAE59EE3A46575D6D114518E839BBDF6076FBDA1D9E2B7AB72E2A3B897DA9271BDE2AE17310235DB88F6900F2A8G538162F819985FD5966F3D327073445EEC0E723A8567A80986F64079872E6DACG9A82387F12FE07FE0EF23DCCFC5C6BF4DB4FBA9D0A593647E66DF11011A0DFAB38DF4414F4A70E19
	2E23EDE7993C1FDC02FB3F53A92E99F03F97BE784A635F87E6CFF09F74F90C470BAE0F6FF8F150E3BCDE7C56239DAFB25C84FAD73A8746CF6D026E92017BD260AE23F9925C97D60B52AB7061D4AF02EF1609C0447F106AB5C4F16DB85657603E6438699E9297D0E4B0BB725EC43FDF5FED355978D81F52D89D90D6429EF7D29D2DAE33C66FFB840E684DFC7120704F9E611FC73D7C436AD65A6E716DF97DED1CFCBB825EA1B58236A7164C7E665B1D7A3D3267A95EF527FE2F2C3B8E5B4791C8DBF0A736C11D75DA
	79263B12F25B6F9ACD9BB43753C8EF9B671DE01087333965847AC1BDA3E1BD01FED077AC738C6F8F85576B7DF1729D9BB39F33F79A6D43A36E527B617FD17C743B74FE38C87479B110169F0BFE98716A77944007460E468F738D6F53C5F15ED803BCF85FA36E1A4D56504C633FD361A95DED74CD0D54BEDF7F7D3ECF6BBFD687EB4F909EB1EFE7C3D51EE6BAC36660621B5A74CFB2313FFC4157C146D9170E0D13AFEC4CFC4C479BE91DDAFD375E7795EC4EF7375E778F09B1FA8924055F0D3E3F572B8DC887FC4E
	0FC6637B78B23D5E859A3EB203BC18FE9FEC22773AFE44744B94788D945F2641B33D6F20E3F73AB68DE9F3FEA44EAC157946425AB2EDACB0DCE10EAF5D069CDF3A6D8631352B4F37ED7853B86BF8E6506F969B73550C190F5973B9BAE6AF39C79F9FAFD1FC55BD7A786837E1FC18A0AD7A9E0C8F59EBAC1D97876344385144C721211DAAB9EF33C19EFC2D44303C9D1C5E65176F91437B2BA11CBE52AF5F6F8D6545F07A8483BE969B7759FD6365A13BEFDC6D3179526D35794655F5BEF7BCC6979F370E136F25B1
	733178B9CF67AF49F17A388A257885F17A38DA590DF1158AE92D77E2DC2D68514A0F837C1CFBC793D70FDB0C741E4FF9538C72E0FCDB8D79326E6367B86E7367E7F6564C5BFFCFF47B7A39275DF70CE7EB570E7118BD4E083F59D782B2AD77619D1295EF0A6FC6526FEE314FF65BDD4A1986BADFE9104D8E1C63BDA63E1B97F59F4EAB1F23F7F71261BD95A06BFE1C7BFDA26EAE975C9FE8BF729E7A1B4DC1FEA62F0327764FE79DA37F62723327343169F0DB53E7921ED9B0EB7FCE859D1AB3FC41709BAA9E0766
	78DDC172E752786F17B53FEDF597FE9F62128BAA0379FE285EB7EEC66AFD6366953A065A863E315C0FFDDB0B995745DD77E3FDF93DDF35517F577C8E6D3F70761AF1C1E530B2DFED50CEC65AFC6D75C75AA7133B078942DEED62EEDD2240BDE245BB9B85339097A5F84B846E2F628EE70D409D233CE170BEF3965AFF0BA6C62BB43F9F7B9F38537C2B7F01BB4D09F53E767FF91D2F7DFFD10667F1E789FBB9AB500673053D5651F36EA58F607BE67ADEAF5E6F59B81EB2117CC6EF3744F149986DDDA4A663C51B9A
	8BA91A5F83368B9F9717DDD0993C4DB29867FC4FE01C33035641CE700F6B81D15FC43D9C913887E91DCE04777982771138FF10A2F04F50BAD875E0D2EE730308CBF0A2DD1B4065D0DEF247F82640D9C5FE318FE23CDD522D7AE6126677FB5D975ECFE597D486AB7347BD3EFE7A244757CF4FD950DE52C3E82FEA996D15A7F08784DD07405DAE76EBFB9FC23B1E8F783D9382339EB27A2E6695C179745F53E5FCCFD13E6407C6578E90505EF9CEF9C8BFEEBDCE71698F69472DAFF609BD44F8681B63313CA5DE6366
	B9001F9FBF1ABD183FF5FA2FA13FC750FC8D394B0CEB48E986F9E9570735F7984959E4B24E4AFAD8FFFF29BACB4D3BDF5C6548FFB8F85EF8FF896F5C95E972E66B4F2F50FAD986781A07717C5B8D592A3C55C2DE2F013CEC1D3C110719BCACCB38C69E33DB9F3B63E79E565DFD39086A91857451GECADFFC9B92E65A783AE95E075A3C93945G647E9D53EFE205382B7B08FFFB60D95CB62069FDC4DD2F9FF9C42F5FCF3B5573C1657CFCD047A67D799FA2BF7491BC5BF7D8F7776649252A7D6E91760BB250EF19
	4EFE6D1A3BC3B4F6FD747B394DDBD64AB2D556F6F1D6B0BE00AD0CE491DB4C751115EA751635A2D715752C93E54DB71035C2A7AB8DE8329EE5F1C170EB9E5547F869F2B56F89229CC50F860FB93CB707B62B72497B688AB56F23B266EDB248DBEFA3FD5E6DBEF9476529F9BFAE760EACFE72561E8B5767BD2C491BFDEFB1E1951BEF592C8E197CC519FD32C342F67FF9BAE78C811E48C7553BD112046DEA076677FFFE27FBCB41006FD9DCD0992C4C0D32EF1F58ACEB7BC4B6C70EB0639CF966235826BDCBCC4953
	23447E74FC117E97A2067BF4AD59F2443D423EC067B62BCA8A4BABB7E23ECDA25F5589014E81562FD4E5F53D2D4ADA9368CCCCC5D99993467A330D65625B95A0ABA481579D36D61033903AEFD76C35B907DA78F1CD2E6BC0B92EB944A7601AC3BC4DB39D5E739ECB4AADFA8C579C6E2F40F255BCB6563D6C092BD43BDF3D894F3BA8A26F0542E6B10F21AFEEDD2D727408B3898BC5FA62BBEAFA0ECB2C07BCBEDA5D50763FFBD74D67EEA1276DF11473769AB57DA2316E92A652377DDDCD7F2358131FAD525B5FD7
	FD1B5E2D5DEFE4695F12AD8CDEFF2EB5EB47A10BA10F7CA7C633FEB643EBDD3688F82AC645F7400B2F85F8BAC7453725FCFC6B13356364BB6D4527845111G9B8716D510D88EFDC2BFBE5CC97191CF6847070F0B7AB49D5232E6E31B9ADF21155F8B7811592331D364A6237206F35E197CB98B1E097CB763FB554BAF8BA0ADFDD472ECDE6B7AB9CFA66596BCB99A3EB73C7ECEDB997018C645F71F173CF660310D0A6FD22F717684600998955FA22F755EE860098F42477A07D833EF1FF12F59F79EB52B5F176E61
	FEDFBA3381DD37815D9534FD1D7B24F6CFE0BB793CA2FE6FD973FBCE79056F1FFFFDC1E57035248ADF5B4C2B70354DDD1BF0AE1A72A44EC5FF75A94EC553856EAF943766A9181BBC053843746FA735423BCB60A4033546EC0335460B8D56BEC28D56BE0ADC3EF49BDD3EF4CF9B6417E4105FD403F14B0D32AF5DDB86F42B3D68D8DFB2331C7421E6F112887B1329744FD106BCC57751BF555E3752834BE7E5A969G5533C9642A841E304279E1C9A976EB09CF61FCC7AE8DFC26693C60D97494FE7FEA4E575AEFC4
	95EEBEE2780D28C5DF1F4DA87AB15FD37C31FA17ABFF20B01349EEACA57DF9003D0C16263DED3CFCBF9C95ADBBBB7A70DAE22F91109989908F108C1082A04D417334AF9594CB7E4F53328116FADC1C71BC35567F992D275786BF23754B3546E734662F75BD23756B3546E734DED92BBD23759E457D2DE2A7CC6EE2DE1332A1FE347FFB5C52DBE278391F0ED7961C5EDA1D79720D1999D256FEE9A953E53F14C5B25CAFE5D3F97B25CC50E518FCF7A6CB8B1E81691FECD5E35CD83774366FF0F84F24671E0DB9E9DA
	1DA1E500CA3DB253A1FB08ECD6D1AD48903266DFA05DBF167805786F1DA0B4E4305A70AF148915FC49EDC1E8E04E32E46AF71729C85B9BFB1DCE0FDC487EE0D02159568335DE6935971A9D1642EE1817FE2A8CBA64C272C7D28A3B1C8E8BEDBBBC557E32DB2EDEA6D7243758AD2DC23C5E2C009AF217C711D6104C4DCA215D69D10007DCAAAC14490DC3C54A2312BCE63B8B6A086FAB4F11DE7CD224DD4564038C66C2273B5063B2F781A154C090AEDB8A9D0376D3325BD3D2A8177614961615EEAB01FF48EC2E34
	027C33E36338DCC31A2B02D1504C392001792F415275987E5FF2C5FA131B76080BFAD842BB589B4FEB6F68AE3A244B6F9D6C1C27D9FBDE70345A2E333637E9BBE91F5A812FBCAD3EB9310DFCE3505F797DE3F6334D86BDCF7F9E07051C65EFB823486ABD2E835BC97E9E246109AC6D68F622F71F06673F81D0CB8788E858FA2A3EB0GG0C26GGD0CB818294G94G88G88GCCFBB0B6E858FA2A3EB0GG0C26GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GG
	GG81G81GBAGGG78B0GGGG
**end of data**/
}
}