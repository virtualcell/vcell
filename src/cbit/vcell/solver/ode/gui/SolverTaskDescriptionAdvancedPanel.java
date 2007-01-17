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
		if (getSolverTaskDescription().getSimulation().getIsSpatial()) 
		{
			solverDescriptionNames = SolverTypes.getPDESolverDescriptions();
		}
		else if (getSolverTaskDescription().getSimulation().getMathDescription().isStoch()) //amended Sept.27, 2006
		{
			solverDescriptionNames = SolverTypes.getStochSolverDescriptions();
		} 
		else 
		{
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
public TimeStepPanel getTimeStepPanel() {
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
	D0CB838494G88G88G42DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8DDCD4D57656DF4BAD3628252234B4232422220D0AB6AA5A3050282835A222220DADED2DF0575D58DDF67789180C1F143452BFEA5924440D88958595B7BFB0D1B1A9D15944E4E45EB08373654CC350A87F67FE1C7B5E3CF9B303346E6F5F2F631BF76EB9771CFB4EF9777BDE24523F0F1FB33B20D412E64F1178FF7533A549F20DA43D547F71C84465FD19E912B47F7D8360CD690135C570AC7EAA53
	B47275CCD309B4EADEB13C1743FB63EFB3CDCB617792691CF925D7420FA0F2E60C1724AB1EFD674D50F2262EC1B9F369FB471A8570DC84D081B872F4FCC564ABEB4AB83E096391E697C8D247DEC2F37E1AF20E0B871AD400B240D76FE574CD70E48A0CDA5F4069BAAE0815E6CD7196ACA479E872C408613C297CD9A17DF136270D48CA24FA929E895E9BGC8618B2547EB3640333135377BDFDDC76CCA4D31AE59612871399D47E4EF0D5BA257F47759572F5FCB5F5B4C3E5EA659576535FB943B5B3559F24C6C6A
	12AD2DE6176CG7BD1B9525AE21A2FA4A5407B40BE0233D9086CDA785D8C50398F7157DB89BE89FECF8518318F4B3BF1F9A3AFCF7B3737CB03372E193FB8C8F90B7633F2D66FD74B1BB4A62B609D6A1B52FA62839B24C5G0C87C8814881A8BA10E9FA1746443BEB7F894F4DC7142DEE27934A6471ECF1D83C3B21648E783DFEBD24F05CD7F2175BEB11A466773B57D49B68938A98DF5AA973B11B442E4698087B6AC6A96B160B8B669A58A4AB765B0272834C96F587D41BB059F35686135DF640BFC67DE59F1034
	326B8627964EB1105DEA1FDAE863798CEAE4A79EA03E4E22B1B096FE4F6679B27CF40A4F5760D93C3AA81E3C1735E71A865BB1B65A9721AEADC59324121517985AA17A679185596DCC073CF63DAEA7EDA46F4AFFB3195B05AE57D3FC12864FF4391946A9F9EF7BBA53945911E9E271755EBA727D26FD13E91A86A09D066F972081208D40F698E34C327653A146D82B592BEC6933BBAC32D7E23C6F557F8D1E720062B56FF0799472416DB7FB6D66A38E59A7F11A0F6A098D0E7871FDCCE33F8BE4EC17DD3257CCBE
	D8485D6C1495596BE3B4F57524326B724AB4354D6E14C10053A3E13E0D75CB6069B07B14FD9E0BD911D58A165ED4CFFCF24C3C9FF2048C60B7334B4E7544DE23217C638162059D6E21796D123DD0DF2CDF5F62F67776F95AC8C9A4291358391C569DA9C01FF598ED7C798602EB02740ECE4C677775EFE972E932F791754D5E63780D7D52D2C2FC877491G89GD3BBB1FEDEED4778E9DAF803D47D78C50671138231BC231345CD11D97F9D3DC673A7BAD6C3DA8B00430C6D01E2E98F71ADC7F0DDBC1D465FDBB0C8
	6074CC460FC34A18433F1B42D3489F73DEA4FC1850C8FC9881650A85C8B5236DD7B6D430D85EE27649983DFB3C90F832C5758453F314AD026B51853014673D95408DF0087890209F208FE09C40179229ED5837F69460DC00BDG1F9119GFB816CG87818E830C8258883001FCA3G6BGEE8100D0103A8176813C8D508B30CD586FE62AD7B6142978C8262996209DE084C0ACC0BAC09EC0C59734D7G9E00E800E400E9GA596784681BA81C6834483E4GC8F226298A2095E0GE0BCC0AAC0AEC019B553348940
	8690891088108DD05C8D723BB1F6EE375696B09F8C45F7EFEBECFAAAFE5643BAEE3F0D9ABBEF65765A4EE9F671679E6E233D5C3687B87EA07767E17E5E457D514D7D554B71EE6E57C55C677D5CD7C6FA0D527CBEF708E599B7C43ADBFE204D7EBF810B1D4F3BCFA5F636F2BE538F14CDE2E78D40EA008F8116834C17D85C3C47E95E644FE5920B5595920B31F7B97E93G523F7B143F137A2BDEE231783144621060C91F776F129A7B24AD5998C42F151A5F7F98E2D9168E11EE437F831F7F270055694B57917B4B
	47E41752A4FBE41785FE78F8DA4DBA92A33C7FE1F58B1EBA0A77C0FFE5ABF490FC823FFE9D7137D9D13C76A3FD0A3CD7D6943B2BDBCD5FC2E5797A0EF8542E8E4A5A310E785E5D8F7D95C17F45BA5256F739DDAE19B6AE02052737523E066C101D2074D6F71FCBE1787D6B49B82DD7BE4E7BBADF2FFFC3A2FD95C79F69B73131CF3C19442A47EBF7C213C53ADED2ECB769072CDEDB2631D1BB604898EE901E115D580F68DF067D084A9357CA4907BD85C6FD62682B4FAF486D667D881B5A0FE0BA6D5AE02C7B1734
	4F4371BE266B33B5244F3127E91B5F282B10528FA8249F5A66763A76D82D06C3B5C6675BC03E99C6GDD51A36EAD6E815A8B15D87A747DC49FF01B62F6ED773A7BBCABD9DF60150D343F8C7AB7G345A30AF705086120E7DB05E89586AF6B8182BA0AD52C66C39C6AE657630417B889B3367A2111EA552B360BDD713DEED277DF5EB29462612DDCDE7FA7C2C115846A79F6D1341B224C339CB787D5E4637F4E97BF98C3074490DA4869C1A3EE8B215D9E44731394265C558D9FFAF9E1E6F833E290750BDAC689674
	30744A9E56A72C6BE1BA327C4676A0DDA4270B05E792C0F68F4B0F51B9FA11EE9627DB508B7D8A0016DEAD5DA6875259FA193C91700C840867F44D8EC2B74F09F4A93DAC3F2CDED61E7CDE269F233BDF50C5B998DD1C431F0E1577FB9752A5B9183C298EB5DF020FF7923A3DEE24ABF132FED61513655768E4F415AEC277BBC1176FE4FD2FD2171ADF0D486F9E8F52F9DC2CFF06790DF6E973B39FC53A99AE56F7ABF2B3BDCB5DACBFC637520BF455EE5657EBF2B339ED1C0E59652F024E4253875DBC9E5CDA7B5D
	6EC33AC4CE17EEC8F77CA45265F23AE28F232B72E86956C8C8576AE1F4B6C33AD7845DGCF0F644FD8BF3A093311AE9D7053G0A0FB23ACDC735F47682249B84FC94C082274B75232BAEC43A86AF54895EE0F459024E83B42381E23DC6F4D795A1DD8E246783147B0C741367A0DD9B24BBFC41685EF98369A2A0BDA6A85DC302AE4547634EE764379F4D95F14A69CA95C6D72BE8691A0A05BF94C697299879ED7C87C8974769D2F47931B8EDDF06F4D395B606182149D70D538FAA05FEBC3D344FA83FE785DDDD9F
	9B0B34747967476A17588F112E0327AB700CG086F5356AFA7BEC23AD4CE174D656675E94BDB379C69EA0F3174E67E6CBC4668D8BB775B95246D257311BD66E366019ADFBF749F562F5FC95BBBB647F8B91DA7A0748347F0AEF172BA52C64543FBAAC076B1EC279FDD08F309D13D31D249F73FA88CB6F71A4775A86FD75B691C563EC2568656FFC6E4F6C01A239F5B3F260D5A790C4DBE1F3B4BCE27D2F85B725B0D5AF61C6172B64E0327556E75A9BB2023B5006D50DFB692FD49DC8BC77339278EF901447A74A3
	C0EEA2C0EEBF41DFAA137E8DE913D8B93BD6E3B9A3CFDCADF5DEF4D9417CA0652C9BE065EB9BD04BB972C72B8B193CC2AB4A0B00741481143749CA6411360579694FB56F997AE90B5AFB10F07E703B4F35BEC8DA7CDDD05D7239CE6547D55DAC453F633AFDE2C75DBAA0BD8A0075DBEE1249F840E754716174CB24C4BB51DF3159FDCC275FEC5E44E5265DB8C1CA1C9BD1E0B447EC1B98DD1046F5192551E96C1B4F944483B03DDE27FA913BB41D007661846A75CD8F4AE46FE775786BF0437BDA9D0A4D238CF508
	185EDD10F002494EBA216AD0D678CCC1F5A16AB02987EDD379ED262963DB54213A174420E2F74AFB9559437A37DD87347D5B28EF59FCDBBABCF3845FBEBFBEA66322DE14118FF4F503C81B5FCB72113DDE37374D6D103D24FFAA713EF41DCDAB4BB148E44541B3D970AFF79871B319F68711C9462A99C0DF729D7224BB50162C0FD8FEC85BC7EC86BA8FC014205FEFA57EF1525EAE663D5A09FE0B853AECC1FB1973CD91CF7E3144E23FFFA9991B7651B9D1561B7E426C141B6CBE0F43FC3C2AA5F09DE52FBE2B8D
	AB7673EFE34292ED8CB41E7C46B086C6AF369514FD4FFC3F69FBB586628B1EA9C8A94098F8CD14A782E8D23E97E5F7212D985DC497526583CD5DC924DB64A6E3F03BA2BBEB48BFFB95A85FD61B5955ADDB2A48C7DEE36ED7EA68B031E68724EFA33FD6F0DB8E3AB1CF876497F3926B0952FF11A79DF7897BFD365AAA71F9E759E0B4925C023A0FFB430A96CD5DD353A4DB4DFD8E45AF2F8DD8877D69BD34F3E5D30C54B02B57504E749BBB4947EBA3D4BB973DD3D7D886404A7A3E873F03B620099801769B65A1F6
	B6FB3BD7B219037FC619A352922432A5BF8EAAB3054B1A211119D372CCC1DE897AF61A876D5BF8863445E720FD2BBEA673F05D329290E31FFB5DCE9217D554C8B58E70E34DDE98163B3A41EEC309D1711D1CF3944B9B87F2F34E40724EBC0A65E56F6FF8891DCFA663BF59C2C20471FF6DC57E72B321FFFDA6725F6B75671F6E750F53AA1F08E760C991FC677B100E59A0E2D58529BFDCECF44796C75AF84D35071645D735E2BF4BB3DB64198F79557D8F66397BE430EF1E71CDB8A9629BF8E2841FC98ADDD7C43B
	7ED7683627CF7174A9CCA35A1D812DB07FEFA44CBF8B72AE9D0979CF127CED723759C85782B4A3855DF794EFF759953B59E1BFA1D72DE072EF5EC77AAF1AB17D619D9A1A7D22FC69A25FA448F396G2BD7EF36E96B556AD160BF0001D1A8373E002C1FDA5DDDFD3E9A7AEF0B5B2768EB0B4FC9822FAE38DDAC26DA9AAB4A97F23F56AEBCDA0DF8B7208FAFAC90F181FA240CC2DDBB1DDADD7381DFF59674EF4FC2DD7FD0B01C7A0D69FB5D876BC92C52098D2D3E2C3FC3338AD0DDE432C175733EC2543D93740ABE
	8BF53F41E624FBBAG63B395A1DF8E604A4FC63E11BDC6FCEDE7E319BDC5432F535304CC9B64B7DE48CC6D5147C48624658B192B671CEACCB0F9674DC1F9A523E1FCB09A653D52EBA4EF44E8344D16B710AF8AF0E902EF402267AB7A915867C74877FB415788B865C7287F2139216B021F4EC53EB8604991FC6F9493B9EE9A9D347E7148DD986F26FD6BD93CEB0367F33B6C30780C5A3BBDFE19E86247D90C324B4F81BF1F0332279A4AE6BC13BF905F8B5027881E5AE57EF54B994BC47F8EE86A4EC53AE94B301E
	E6F43BAAC5DD88B4B1026E2715216DB677C34CBF8BF8CAA3104F7521FF7E0F8A3A962099A9689ABEA2E3A752A75D626EF3D9FC58479C7D35360F1994417A18F970DC70E364BD92404BE4DD7591162592E8BB847D3C65FAFA6647095F1158D6C24C359EEE93CC1A7A7D50F29123A0A3CD48C9D9EEDCEF9F7F020C111C66DE484AE1A19931F51E8981FBB80A4ED357124BB43F91585E064DF41E3C7ABC3637E12140337973D68D1E697B889DCF13F78552324F437578EB3690DB371C1FE932810CG0886C88448B89F
	4745E77D0B2C87055C6F3043254030596C586B3790007B8EBEA33669150F7377CD1FE97B104C8EC7E4E207D9678FFD9D1D71F9A85F02482173313E5C0A2F35FB300A5E5CD9C0F4B46A4BB5723CDBA2553E9C335D01A6D29FC5GBE96A099A083A0AF926DB669336AF0F6436E8DB798AB1349BABCDBAC351E2AADD8B96E5AC64AD1F6815491GADG96001197E0B996EF8A6B7FED83067EE7FA2D36075BA395F3013F4E0CEFF3D83ED49D9FAB4F126DE4EEAA9B527281CAAF4CB455823481F8G22AE44F29D5E34A5
	DC39B66342D71BDB7C5C2ADF3CC2FD7F9C20EF5C057E7A9917F3FE0F1E2FC6DF4E8B0D4AB93A191693527281CAFF826584E88370GC47D844BF973268DE17DC79AEEA8A3ADD6E081191E8F8668B9B4BFBE9516AF61A77EFC2C4EDCD7C95612436DE54411815F364877AD0EB9007B9653FE02F30AE7F413BA30A88A7ADBGF5D138CFE3CC6F704AF5EDD83E16A823F2FD38F175904A354DEF7E04150E154DE14332D92230ECD6BA269B8B6F89GE922ECEEC7A89DF3000E142D78A2D5CF4677DDC8BE82559799152D
	5B712EE4B85F0B43F85E46C72C572F4912B63E8972EC87E0655CFB884B6939884B334EB93CEFECFBG5F7B3A77580B0C3E313F6EA05FD8B224E5814C82A83998E2872095C039983F31024D9B07780DED85DC335B5DEBD00DF0DDFFB74CB2963A4255A3D1979B715D9040B7B4F9F7061597E7A86FCC777064C58670696525DCEC6443F3F6919FE6C1DA9EC049A5B01EGE885F0GC4DE02BE5C37F9F3B89F0AAD17C371E325E7F8754A2A30FC71179815F35EEED24ED4C84B814887A80B4EB4B58234838CC6E3B92F
	5D9C365D5314B3FC3B77EA003EC373E7FEGDFC03CC69B4641516159750A30FCF10672BA3D4313678A4B17E2A82F46B7BCFBB68670696D19E5A8EF467743137747GBE3D3CD90672E2CF8ECF5EED81FCFAF98BAEB56233C84313678D60534B2BB414D7BEFBF8727E9940271757E8A86F09E14A4B8960533709EDBAF93C7FD2C56A9903B63ECD9ED0689C88EB634F6A553771E4AE4B81F90E38945B78E934AFC374093E945B7868026115671AGBEFDF9528D4B7337AD6F9B1767AB33456EE6B302B8B7710E57285F
	B28D724D97E53A4409E5AA3D8C4BF430F0F8E5328770694B54F219BF9F53A14706EB6A9621436222616970DEG1FDE073106BA5C5903BAA4889D9E9B268E59E1F518215301796BCD3BD18C960D0136F18C7A6B2FD4CFB2AFD0BB86750C1AB3BCBDA784706975340DB132D52583F598A1F4E8FEE3F8BA6C8F60536B10B84648D616AEA3DB2583ED0E3055CD966C13678BBD730729673C39616A3B3A31C6FC0981FCC313F7FFGDF4037E4A84FD7BCBCF9E78470694BE7B114774887431377CCG1FDE5E20213C3317
	8D2FFFF3D1D83EA8C3F9DFD68E2FFC07837874650BB31437704361156F5D30FCA906721E9C263CDF05654B5249E36D5740DE32A6B414F992B60D2D1DCBC87E9A3F67D9E3F1AEA11E6E9B2E3E9C4663G9D1763F7FE6DC743735B6D81FCFA3FA9179B71F5AD9F1E3C2381FCFAF991BAF96C3C5A9DF49FDA8C24A582B05C458D8417856F33049D3653390B32ABE09C89508CE081988990FD85526C27B4E44F8A59E3C7768513BD3F24BE0D97B40F2F6B5468C078C91E217C2F05EA9DEDB317CFFEDBB4E9CCD62E1F2C
	7952291DA71B23FBA7F9AE643F35E7CE58787226AD414F9F5C3645787CC15296637387BF5F72A643C39E5A531418BEE5CB6873890FEDB1BA1F709EAD7F93F45D595B67A24BC2D4AC7B1E6481F24684C641DF7539589019C87669676CA59CF7B7FFC1563B522F407135ED6BAAA9F0FC3D4DE2D75CDE4C24CDB6FB77D82DE2F04D5677783CF4D3F07BD91B0C6D67E8B2361F37A9347DCEB405365F24ED41F5C95BE62C4BE35B0CF5495896DA176CED41F4E17EFAC9A17B82834E0E10CDB1926E8B6C311BB8CD2B62EE
	DDA972FEEFFB70F2D4EEB7AE47BF37139877397B3CDD32DE57155B4BC41ABAB1C2165C09BA7C6C8C65D74C5EEEB9202C1BA9BFCB8B463FF3BB79CE0F58ADF6AFBF01416CB079EF0B299DFDBE192D38B71995B3AFFFDD4FBCDE7EED0AFB774A55A86F426660653F3459387C631A03177F1A6670653F29B9F879EFEB8EDF7EFB1A034583ABEBC12F56572BC5DDB77EC028F6F406C19D4B56CEFFC7578724F1EC6DF436407F056297E870ECCD75EC0AA76FE510B6B88E57009F73532BCE68F55496CA2F6CF141744A24
	F37B338274FA01622571FA3D9CC7D12F85106681E0F1D3F1F22970990D1BBD50C1705AADB27D02984D8B2E2542AF01B42CFC57B83465A3E77BD8DFA2E5BD0EFB270E473E4417745C7F02ABB3CD15G0DD7E25B57FEA814AD5A2E8C34855F7F62C459905FDB5708F8B26D8C9E5FA5BB0D637BEFBB034777FBBB434777C7BB034777A7BB4347775A1D21633B5A25AD6BDA014F5162F72B78BB5C7351AFD62F5B892D90139335AB38ED466CB2364D153B025BA6F6D7F85B5C3CAB38ED92F705374D3D3BC25B6620472F
	2C2D8DBC6F525D414B7A6EEE63327EE3F77032D66D8EDF56153B031735E1F77832EE5E2D2F67D999BFBC6A6F4FBA0EFF409F8FDFA043DF6275472F62F859270D231542064BCEEA712B847EB9BFFC3D400F11347886015FBBDB0BDFA7708B8B66E96556E2BDF1B75D0BCEF435DD0975041B5E55B19E5E938152C5BD71CA48BAF39A5065EB6A0A1289D887BFD22055E93528233E74621CD46584147DBEBD1351816F8AC0448414BD162E4345CC702F0B9279FB5A04E0F5540505DA59EA7DBF5D30AC59BC1FD9C1737B
	3C484F0E2BB13FE443F575622B78190B2B0265E71A6317DF9D5AE6079D6B6F5A2B50B61752B382B6F89F8190FD955A2648F06DBB1E4BCC01E706C67E0C2B50AF8F7A4BDE03E5D9E1385EAC4530BCCA61D9952366579803791DFC43AF3FFAAC4B58AE74F3EB8C16E59F7573E8F89F8F109803E5C930040A317418E0F6DCBD57CF76CA443F660FDF05786B0A7D70EB917F46B2BFFC83626FDE667FBD2F64785115FE746B103E69C3FF7A559C3F68A3EDFB3DCA4463242FC36E8DB0A8B76BF7D49EA0F64C0B5177C7BE
	2178222B757D115B76E3FF248452142B51F7599FE94B312A964BF159F2BF3C30EB7ADAF2665BE1BE6E6ED3A429699A1277CB62ECF786641B8BD086786AEB583CBC5FB35DCF6CE2751A1D50045001D645C43AC73781F9A300FEBCC0B2C0B600B491F59C3D124824B3A6420F5FFEC75A987DFC8ACB3B7C2D05BCCD3BA7056F2DB61173E5C67BBAF83B7E9D718BBB99073E1EC54F346FECB19F119DEDF685C61DECAF6CC63F3D3036092C3C9193F18C5A728551C3341FFC0C4466B4EEBF00F39A63A762B770A7F23587
	0F0BA21B7A7B475D216E2679214057EDCE2B8CD66651B6ACF32EA8F376D1351C53B53F3DC7CFFF194F7DAF48183C9E3F6FCD316CDBEE0BD53FE9DBEC306FFB926D338F46326FF80E4027D3FC24864F3E6F83965C8B9B83E94557625C197B905A5CF1AD5A7CBE51EEA481AE9DA067DAD50FBC783D60BADDFB4B46BA34FFD0F19D13BDD760DF2378868D1E6954C771643D9552922EC31D0A6C28D3597508FB0C2E7F5860FD044019E87B91856F31570BB93D00757C246BD5DD532F8FE64F89345D49391E6938D860FF
	CA718B62D4BC533D126249FB9D24454721CE3750F5456A9BB2CD1BEEC0DCBE4535413BC5604CAEAC23C760A4BB52A52BF44296E98277A435C5790DD0075E083806DE24EB3891ED7105586BB87AC6D66E31B72A650F3FB118AD5E24677FD2EE546FDB2E2078E9B76A77AD0F6B45FD4B8BEE02F67CA65469DD2AD38E3C4F923891C2CFA99EF1D751F535D6F837895CE7340C03709EA9F01BA8DD8C3CA7885C558E4CAFD960FCF64C2F64E6444D2738CAF8EF38996DF3E517BED6DAEED6ED52F9F3B07BCC263ED7EE56
	477963949FF133BE4E8F4A9867B110D67CD35469C10B787684AE6BEB44A57D94757CA73972897769EAFEA76DBF7D75536327D586334F62FAB197F2CB262969966C932C6C745B43C08E322CDF3F5DEB37ECB1F737509E866BC79E28AF667C0337E0BFF2944DB3815E53GF2EE41B35B839F61396838EBA6C8C5470D4F26E737FDD710FF8B73FFC582FFCAFF1C1DBEA62B00497CC5834A6C00742884021B3DCE5B5E133E417985ECDD03252F76CBA7E3E7F237171AAE7B2593FD6FA95226DF6217CEEC748967E7E5FB
	70C3ACDB7E037112635611C16FAC4D62E512EEE54F481CCBE7AFB8BED0486F833086398F0076F0D879958B2E05B7C09E0937925C43871A502F3786FEBBF3A4DC8BE3B092E822815E666FF137EA7A2AB43FF1B64CCF3AADB03F393A7CCA01266AB6B53F465B547C583715CD673E1A815FF19BFEEF59745E9F8F3C0F9438439B719B0C3D8D3FC1762D6641FB696D08FB0262EAEFE7F71DB05C21107784EAA14F20CC21C015FBEC48B2CE75BE42B94276E79DC25B375EEEBC361A2D313D85E8883C4B739838DDEF7BCC
	DA6F0F84FC746DE8E7361FA28E5ED385EE97EB6F60BDD760A67520BFCA9391773C688FD89251AE53E9FFE2G5E23G92847EF7941F816FF9096C9E96067F562635A3727CA76A3581A1BB4ACF469CC93D737107C22170553BF674D5599D61FDD58DB41B6EE05F8941375D217A0A0DE772FA68B9A83B4B6E6CF36E351F102568BB081CBFD11BF742EF479DB8762A68A26569F2BB7A1CAE9F272B277DB212FF569D68AF27977DBAC360E678502FA5BFC3FB454A244E6F04F785A09AA089A08720684ECCD3651DC817C8
	691A6EE477653472FBEE489945B6FE6610371D32075B0164C71E517C5979B355D6C9BF8B349B11974C7615A152F2C4D9F6885938B60B3E57FE17C67781AE8C12B70145A152F04DF809B40576B971DCB8734B73CE5CC7E03B937D37045DD3A08FA883F7123B37A34951F69ED783AE0C2BA40372EAE30A40A69ED7CBF9DA7A1DEADC313CEF592F49DB44ECD6103C670448BB77CEFD4C7A6C7E65F86200AAEBCFAF4A4A8FA2EB2EC6D6C99270A42932AA1374F519EC47F624B689E379BA0AEB0277F601DBA17A3A03C9
	9893CB9D24EDCD0377E9G45F7C1CC83B481F45C05B49FD19A4FDD6CCE26483B581DCB7170CC97B4D777F60608838218B749D70BA732356FCDF7994772B421F3F2D8D9F3F99C6B63D65F7E4C8F420F3EDEA22D6A7577639F5AD5BFDEA27C18EB20333E1EAB3A9B460FF733FA0E64DFFD37BEE6FE66B02E679E236DD28350B75DAD6E7368892C671C34BE2463A0C1F740612F7F0A2FD57D139C287F083B8375B769740F821AD80D7EC99A7D197C31B466D281BFD5487F409E2867BABB6AA95D033119C86B6892F82F
	9438D5B246EB73BD687BC76858A59A5E63G52847EE90A4F3E075D73A5A5A35EAC7F10718211C1E451B83D47384F7726F462874940D8535647DAE03E7A2585FDD5119CBE566A12D91D013EEACE5677E5FF7BB56657E6101F3E3E33814DE0325A170D546447EC70F6FD38FDE84636DBB6CC3E0ED366E3E362838D744CDB32FE9E3A1362E313757350670A31E08224155D0BE720979CA4F562A03CC78344832482CC83A87AB94E27CE7D025CEF91724CCC1B5B53A29F139D1A2DAD2CFC998D27DABEB6663AF7AE0E39
	92FEF61D14F8D6D350FB364AFE4E7C5E401FD5AFDCBA1BBCD97946F5503F1BGE9A381E281128126824C9065AB5FD34D4AC736A9EDD694F3170D5EF2A6B6BD5965FE95CDA651D9590E7B2D1DC87771759A7E2C624F467BB4EBC1EC4F758131BE83E9FF27BAC03BA261FF391F9961D83DB76E9BC958687985E823726B2F121A1EECAAD89644C623398E997729E36E4A599505CC1FF431DF3EA465D475B1F512B7D6C73ECE6F25F5FB4CC74961F6D6C7AE5A00637966942CCBDF5F90381631289157A3BAD258DA0682
	4F51A96C6EAB762D9F5B085F7A7814607DA63CAFBE81E8D2D354EFBDABC55FCE3D3317747948717DEE2FD9F1FB69B01C27C5B613BC7DEFF442BBDC0BE57D7A8351F1DA8A6E933F7D905AB35F501E58EF5BECD6EDB733B750F6458E34DD5DA4345D8687ED973D32D576FAE58BEDDD113E0D52B783ED1B209F698D3435A24E850C1844EC9D8D4F38C92C0F4FEC1D550336CE19E4DCCFEB6B558C2019B1C93535F43F3ECDAD21FD368500AF3BDF5C01D1CF6C3F47C3AFD0B8E6F6E86DDFB9075CF50BEBC97BDCF64547
	12F93A6D8D520F54DCAD82D462888E7753EA3B5ECF24FCCD77631DAAEF749A25375F0FFEEC31239F9D779B7BB138EF930F283FE7F629FE7E4B91326F5EA577D708BBE41AC0FD97792393D5EBEB2BABD6552E406FFBFC3F761E0606F9E79A5E4BCCA066FE6623C47E4CFF7B3259CC5E97DD8570A47B8F186F4708B3BC3977B35F97BF10E9AAFF006D4FE53EAFD45077F58F846F5763F7568CB49D8F283E77BC207F4ECE7CDDDD5B43F55962BA22873F660C39D1F36208517C1D5E4F813E373A3D4EA0B4FFD9CB6A79
	ED839E073D4B9E181150616D45EF69657185CA3E9F64DFEF69E57925DF5D3FA4C08EA3E067C7DE130D4E0F8C02ADA29F40F84FF352BE2C37CFE67EFD6420563FA517B98A50EEFA1B47F2BFA4BC20565FACC62FF5E10CCE03B496EF6369F2A7CF97B1DC110A6977D955F436EEC0525BD3C355D5AF52B5D276AD1FE747FAA9B2D55CFFE68F2CE7B2C4BD93134AE2AD891E6929ECBE01455AA6B1761816EADC4FE8A19FE8CAA62B31D6B9D99FEB7F1CCD6A14DD326C592C6CF67B94ED1D724127EF7134EDD02B9FD72B
	8B1EFE0EF511B65D2F3EB96FFF1778652BB233745F9FA37D13D6375D05959543FFEC2131CFD1F53CDE6AF0686B9DE20F5A49D8AFDD56E3145EA4522D81759AC9EF9769F38C799D1331DECB95BE9FB179D46BB513DD7D3D047CA98C3177B6C43D36EAA554EB2BD6A06D489E622F35A286DFF120BE09272C4FDA077A7B5FCED59F499E2A1EDD8B7A2C947A345AD19F767E35B5B0161F976743F2A773FAF38A541BD3581C980B659D3228B727984732F6BE2499E8BA26E86A4DA97AD87E35CB0D9D5C4F765CBF08EF55
	F64FA7821125CFEAD1F9D673F8DBE455478351F9F08A464BB9817D99129EB98563E5090C7E0919E25CBFE4769FA455AD1E69BB08BCD9C1F8C274F14976C4BEBEBA54207E36EAFEDFD0FF7A57682EBB2DB25858636D034177EC169FB45E337941C163B386D587C31FE728BD987C6FD5549F547E3D8AB6F6F2FBC8DC14BE88F593C09B008DE070C1F14753418FA4BAEEB298B3ED33F413BD23642FFCA9E26FECC770325E54917EEF03A5F6103DD1C75C5030BBC97E083F3B0378CBF1FBA812616E6BA0B6725A3BED8A
	A3453F1150C1624DA1DB991A4E4FC4527526E8A8D75C03B8E7D3E2A538F4F84F9638BEAB4E634C9038824A5B18865FF21A589F582E9D97FAGBFB2AD747A873377C3C7C97D918D3489G99G39GC58FC1BD83D08B5084508E6081988DB096A09EA085E0AA40B4007C0750BFB95F103FCF335F5C93F88A8710FC4117BA49EB3693B7615A345FDE4D1207B3CD158F07561F55BDCFEFA0B1E4B5BBFC445EEC5EE1EDA34EBBB4C09E1E0731CE793E917B90318F635AF29C254D0677BC0012C710F662C62CCB2A9EB955
	3A64DA927E3CCE7ACC54E31D0F202F127C7662C783BE6E112114756F8EEDD9192CB4E2C21E3E49CEF48A527E542DA28DD0835ECDB9560EB6CA8659F9225CB9221FD51B0EB6FA0B5E0DED037791G5169C83BD03469716927EA2357481F2F62E5F82BDFAD43D736D0E5D8D9D7DD319A86ABCCFE0F68E7CE93BAED6C45356C0AC7515EDF7A5D93568EF84723C379B66E69A7F90DG5A310FE2FC2F35FC9CA23EDB3DEE0F6CD50EB3AC7DCBA95C5E77F9515EC910572CC7D15F79BE34F775E3E86FF5740EDD8B3C8F82
	C4BD0634F58A16B76E31D335F747F1B5A617DBB11F9CA1330D2E2B167FA253D48750728B1C2BB31DA0FC2C6C03168F03159DEA5F1D602FBDC7FA64AE254DCCBA6E4C76B77A5DD9E401FC87FE915A761C4F4FE79140B3FEC8FC237C76C9A7GCFEA98BEBE56328756A3D28F7AAC8B72A81D0AFEE86EC11F35CCC57B9D2334A361FDACC00220BDAA3E113429276AB37B372A4FFEA27A2BC50FE35E0F0A7B9E9A856EF271CDA88257A868629E47EF62A1BFBB65GBE6F71215877B7FE7B4697BC11E92AF8A2B49F3FFF
	5702362C83FA4793285B179634E54C93E84B1314B68B5EE781ACF892E9CF083A347249D335E54420EA4BEDA27EBBC45E770A3C231EC49B15495A322681BE7B492158E845C1AD5FAC60A94E884D473E2FFECF703E4A771E70FD1533BD248CB274B15A0EFB78D917888F1D6333DAFD326893DD60A991389D3ED6374F2E7E0D2B0996C2FF446D2507EA58B864EE8B0EC34A219CF5G1DGCA86BBDB4D7816DB7D574A16FECF6C4D46A891D69C23C4985881479FB837B39EE888607ACBC2C6409C2A05785866765ACF38
	DD0A593159E16FF6111EA01FABD8ADE2AAB9C36CDFECA738DCF8BFCC92A82E02620A1F4AB44D94BEF87EE8F09FCCBB9A5E87B30F9A7797DFBF9A58DF4CBFEA5CDFAC70EA7B0B8CB757CBEC518D7D27F6C137C0607EADF0EFFACD9CF740EED1EC823F54BB0F631BE5A2D01C3B23EBA67D94570EFDFD8D6E5F78B76E681CAE94994C0E3C55743EF92B5D6160FDFDCAE3F7C1D889FB2C21BA5ABDE60D5E6B850E684D770F5850BF1B05FFE65A347EE15FD6E36370563CF68E136FFFCE190F6F2FE5FF736DA97DDA598A
	0A2FFDCA3FD676D351E7ED043471CFE19D9437D1AB3FA8B353D416B914BA28C9B6523B1A73EEB24803597C9707508F6A9E090CC36887F54DF2C2583BE20D750A9FA65F74D366E376BE0B364BED19FABF1CCF711D19FABF1409B65F81E969196807377C5A4B3A27B3CD4DCF8F458FEEC33DBBB82F47A08F4637C5489B292109663F6360192C63E53E3259837D6734EB7D473E4114FE42A3466D2C2B4A539E6CA7E3F0F127B67D13CC6CAFDF70F38EE16F59B3AE53134364AB8938E7EFE83E3F01762327BE2D777DED
	941F7D345E771F795077B3A02DB5CB5C012A7875A9811F1AB5945FD7DB75FAE7E978F28D72E07A25523EF851B3CC3FC201FF0C62CBB5F87E77C1A81E4ECDC35A68E744DECBD7E0ACCCF1E9E3015399444C94FBA05D585EC03AABFB836B06B7425EBFECEC2F52E1726DBEE5BEE64F176937BE6399FDFC7C1662731E51474720289B0A1E4DB4591E95FBF87D6A06B440E7BFBB147808B6BC03B90B7396BF972CEEF842303C651C3EB6A81FF11D5D44695B0372EDB614676074A38C78D8EC6498444B745E4038B2B983
	6996B98363EAF148F323416D7C5EB079761FB29F0F9FBAFE09FACE9FD77994BF7EB9FDDC0D92E323B8C8AB494638BA695706AB009F1DBD14385A99D06F90984BF9638D72E0FC779B7699D2B8FD06819F735527F2207F6A64C0BFEFBA9858475BF6D05B47E376F8510275598C10D979BC1EA1193FC85C9BC96F5DE257F6BBBD4A71ADF43C32C5B63BF00CD7A3637E580E67F1DCF9086EF30B0277B8001467F16CF717988FE6BD9FEABDF27DC9C942B148CE4BBF60297D73D9FB493FB87D6C2B593A6D0B36EDDFC938
	E741CC7FCE86EFD3E9571C614B87962AF86C1AF3FFFCC42E38663FBF557CCEECBC7D6B900F1ED6994C7777ED8C0C1B540D01F1F3107A327417908B3F4436ADD96CEF2C7DA5FEAF91BD2A0DAE527CFE3F67745B2B7E344A506EC17637D70DC1FDFA16B8FBB8C2586BA5F176B0C2607E5D0BE7B63284EE13604D9538AB85EF3EEAFFBA275472C2264972825A7F961FEA73A44D6FED3E53EF7FEFCE2B8CD666D6A5507E7B14C07BEF3562FE5C019750DE4FF5238D478AFBBDECA363679962BD033ECBB9785EE99BCE19
	C8FEC3375B71E149986AD9A4A6A3D773AD7EC1737B4C7F42F7B9663F70DD7E58603B3C50603B3C094E61D600FFEBF3B0AECA1D98970D82778A7D36A260FD2C40FDA54E9F448A5C9D743B4C037762DFA1AE460DF4258257AE4E98DB846E8EF18E4B73AB0C37BFD8D45F94486A6FF307FCC6ED78FE0AB92DB2785D12867D14B1867D9433956D15AC6C75F3318E1BA6F04F48C8D77EA2627EA5682ADEC43B4E885B0F9CF85168DE4DBF0665535F274B4F9553FEDC540BC32B8788307E692334FF947B223E5F1AC97189
	AF6A7B2DDD32D8C304344A17303CBF776BFF0E867C581706328663157D67102F20AEE1F348CF0AFDAE7186F9E9670735E798495EE4524FCAF9C9FFFE695E82B56F3F0873CB69C3489B4FAF61193B9CCD5EEC7E79CCD69FGBE7FA55C7F3622C81517A864D5CDB3BEDB2B1557B80D49433234CE534F07BF494E781987F4E7DFDE235FF887505BG58DC7ED1AB4E65C781AE8EE0AA40B412FEA76EA97AFD37B82BBBBD38BDF0AFEEA950D4CDD7676B9B276B75FB502A6E8FDA4977871982761391794D53F1BD61FE5D
	591B16B92A7DBEF622FE9D867A4D55592FCCF3F60846EE00FEC53AF3CA6EB7D4D9F631D7F0B004AD0CE49105A383E4256B4E36DDB8D715F5279BE50DB510B5DFA7AB9EE8D2263338A0780C697A983F35D84DBBDD6C2D4EB148DB9FF3F8EE8EED169710F706C94D7BA9313ED374F2F89B6973AEFBD91F770679EA5E7BC45E15C172566E8B57675D20491B5D37183C040D379CF617CC7E224CF659C504513AFBA72D4F1B00277DE52C4B1E543403591A5F769FF4EEA99C707E51E915414A7C14B530CDFC562AED9359
	9839D80C11ADAFE31D768D297EF97A27E20DF92CC8BF039ED3E33FE328C8768D2C8935CFF2D5C3F5454A3A95186FF4311EBD7565D07BG9FD82C4AFA39C4159595EABF492A5ADAA60C35E717DA455D953F067A6857B86F30FA95598B213B3B62F3073B3F05EF57643ABEEF45B90741DF631CC3B53D33B4815E53GF2FE0DF38E4B4579177CDF1F6ADA764237D43B33719A79986BE6E05E9DCE4C5BB183FDD13BC46539CC6CC9089169FBCB557499EEB19FB2E3283A216D0F3D2D66B3DB48A9FD8565DC7AB7B5FDDE
	B7263708742425EA7AD9A266C60A74674AD45F1AE56DFAA3CBAFA0FFAE9A6F21776B07A4C19E692F8CE57E2C4EEF3EB387F8720644772A5F7C6A02D7B3CD952F8E05EF1C61FCDDF83E9B064977229FDFDD989D8930FE60F89A4B4D2F6A7B0757D2FC5B2B7A7E61AAF1DFEFA724253C0AF5EA355FDE3F2A57E06C735AD06C344DF07E3A1573DA78F3G1E917CB7BF476A77775D46C3DA4210640D731B57CF851E2CA1714D747B3BE93940D314BB943EB77C641581CF75107896F7EB79B681CF5B10784E750B839B70
	8C066163F7FD983499AB2C016328DDBD01F42DBD01F4F38C686699507D194A084C552E89DCC34F08305FB7E9FEB77C976E517B723FF00F5ED6DB20ED1AED0136693063D8B4B6974722E70B7B349284EEA245E540FB3E407D0962CAE6E61AEAE7A2EE40E0CD66BBE7205C5B8C66BE6EB4187BA8768452AD7484523DE7105F878679FD549DC877F1F7A05D8403D81D68972B2CADA96DA6ED28D96CA4427664C332A4AEB54DA4FCD7B834672D744072395BC19AB8F5EF92B9AA01BB2CF0FC7814584B97B19347BBEFB9
	C36FE92A96EDFA62CC3C7FAA7510760E280A0F77985E9115F768C4C14ECC3E26789B75ACD7496C0AC2A6BB57C15A7390EB99AD5BBEEF63655B6A9C8AAD5BBB5AD4CE6F1E82196DG03GD1G31G493F417D3476F72B24607BE9D987CB5DAE4EF8F617875F2355D69EFE0F5621F263BDDADD6501FB34EC6546FB34DC655ABDDACBA92E3FEE9D8C6E62DE1166C07CE87F77F92545227B390376D7320F2DDEDF7842D50585D2518E698D264B8E2908E438C31AC3656D108AC11781F26FCC1196FCC7256F59CE50E1E3
	3D52DB015D614D67BD75E45C6126528229GD43249CC07B97DE4310AEAC13A080D7F8169413144AF44FFEF0721A11D4CAD7FC119657059DC97068646AC332F7861B295E96DD61B5B6D13AB589F8C2AB0BB3A612BD7EC4E8A334BD2E105F169A7CA1FCB2EA0FFA425224B6D32503A4337BED8F6735443640A34181D522A40634D8A28A1F77994E9BE495C2CD4B85DBE85F84821428A191CB8D42485D4124F6C7440B792784AF324873F94E9C395391041DC6176D678BC66AEA004AF90044B168AD71F73086C75D5D7
	48B55DB59535B5EB2A619FB21A2BD9C57E29DFB1AC57106A2AFC88B42927B5B07FB3D822AB7EB7CF1196F1536E71D08FCBF886FB45C96D995D3CD1DDC14FE0273E4E6A732C5755FA1D553DF753FB944B72B2CDF5F9D857AE2AA7F78C865B3F3F57E9F6B82065695D6C32103D7CDB0EAB32FA0E6B3EFA123F8FE9F8A27F1BC075C46F9E8D4F7F81D0CB8788C1DF3C2A4DB0GG0C26GGD0CB818294G94G88G88G42DAB1B6C1DF3C2A4DB0GG0C26GG8CGGGGGGGGGGGGGGGGGE2F5E9
	ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG87B1GGGG
**end of data**/
}
}