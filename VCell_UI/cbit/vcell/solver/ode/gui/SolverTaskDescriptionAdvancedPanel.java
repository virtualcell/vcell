package cbit.vcell.solver.ode.gui;
import cbit.vcell.simulation.*;
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
	private cbit.vcell.simulation.SolverTaskDescription fieldSolverTaskDescription = null;
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
public void checkTimeBounds(cbit.vcell.simulation.TimeBounds arg1) {
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
private void connEtoC1(cbit.vcell.simulation.SolverTaskDescription value) {
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
private void connEtoC18(cbit.vcell.simulation.SolverTaskDescription value) {
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
private void connEtoC6(cbit.vcell.simulation.SolverTaskDescription value) {
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
private void connEtoC8(cbit.vcell.simulation.SolverTaskDescription value) {
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
private void connEtoM13(cbit.vcell.simulation.SolverTaskDescription value) {
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
public cbit.vcell.simulation.SolverDescription getSolverDescriptionFromName(String argSolverName) {
	return cbit.vcell.simulation.SolverDescription.fromName(argSolverName);
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
private cbit.vcell.simulation.SolverTaskDescription getTornOffSolverTaskDescription() {
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
					new String[]{ cbit.vcell.client.server.UserMessage.OPTION_YES, cbit.vcell.client.server.UserMessage.OPTION_NO}, cbit.vcell.client.server.UserMessage.OPTION_YES);
				if (ret.equals(cbit.vcell.client.server.UserMessage.OPTION_YES)) {
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
public void setOutputOptionFields(cbit.vcell.simulation.OutputTimeSpec arg1) {

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
public void setSolverTaskDescription(cbit.vcell.simulation.SolverTaskDescription solverTaskDescription) throws java.beans.PropertyVetoException {
	cbit.vcell.simulation.SolverTaskDescription oldValue = fieldSolverTaskDescription;
	fireVetoableChange("solverTaskDescription", oldValue, solverTaskDescription);
	fieldSolverTaskDescription = solverTaskDescription;
	firePropertyChange("solverTaskDescription", oldValue, solverTaskDescription);
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
private void updateSolverNameDisplay(cbit.vcell.simulation.SolverDescription argSolverDescription) {
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
	D0CB838494G88G88G530171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8DDCD4D5766EAE39142CD1D1D14B9615ED54D251CA1B9595355432C5E59B1595969A19951536D436317578D2947CC8A929D0292845A262CB454FD0D131A8A98928C89918B7B0BADF4DBC949375FF6E47396F4D1BB783523A3FFF3F0EEF5E39675CF36EB967FE5F7B100AD6C466E5E597C9D2D61E447FDB13A5C966BF822633EFA662C29DED8512663FF781DE175E2F4A05E7A42415943597AC122664
	6443FBAC3CA7AFECAFD88A3F17C853F20AAE009FFE6424C7CAD2F96EB51F871693D70DF2665077344AC2F8AEG18GB8724CF536017C4C4AE20ECF61F8042CECC94A20B4CBBFAD6138A6F86F820881C8F0B27A94F84E2512CFDAD74F69BACF0B12B2EEF7E74FA779E872C45860E23CDD7CD9AA7DABAB7FA7A22B6153C24E938EE99381C861F3247BAA37903E167D3D1BFA3A2DCA65019E59E62B74B8ED87E4F72553ACD77676DB572D5BCD5FDBCD1E7D0D322747EDF5A9D627E33379004951A31BDBCC8E59A6C9C1
	D4CE702779B4DFC9EAA66FEE02EB69A2329361BD95E016401F51CD702D705B8690644672EE7C4F86DE1E0E4357C8E93D374EDD68273C91EED64E8437DA5E160F3AF35E223E492CA1BE10BCED85E5G75G1DG03G91G6F5018482979881E1B3B152DCE3B9D4A64F2ED31195DDFC249ED70FB5DBAC86138EF649E275BACC94C6F5BBF2DB050A790B03E566366E3B6092A4598087E668AA9E57CD959F38CEC1292F5B8BB5643EC1164D1ED42E47F4E2F6CF40FF70CFA4BEE17343225A7EB66649948AE393FE60E24
	30FC0A940DBF94626B849A83F570BB0B674B7077D0FC18864F62350D6249FB8C249575E3ECF4ACC0DD1AF3AF1496D51FEDE80770DBC232877A198E217DFADDFA4DA46F38FEA6F30750658C0AEFBD206219AE6750B8A56F6910D6FE282D0045D75EAAD2FF4D075B8AC681048344GCC84C8871086B1461AEBBE9DE60C35185C4A16FE2B4DAC3BA5461BD77BA6BC658145ED5A6970A8244235195CD6D337CD76C81CE6C1AD31C137975F12DA927BBDA0E3076C105DA6D2E1A1F713DDD6E437075114543ECEE85CB2CD
	ED355AE590E0F7C918EFD96D92F85ACC9E65DB17592448AA85CB2F28A5BEB9E0EA039CA183784D6CD2331A582B944ADF8F50A46CF0894D6F8B598D6D453AF54DCE677EFED7ABA90994BEC86CBC0E369D1D70DB99C49BD72EA13894F84F9079BCD23BD813CF23350728EFF29F42BAF6FB57A2623BA3D06F811A81ECC7B0FE1E6D4078E91CFF39D4F15F190671938A319CF40445CD4491FDBDFA0066CFF4CC0034B4005C23589FF4F6F584284BE5DC177CA346754D9FB4F3FAA623BF200CBCDEEFF20EA3FF4CFB0170
	E1C4BD71E199142B81206BA85A7E2D3AD2964BDBCC9E9923F7179B82CFB62B1EE0FA5A4DA1DC0F9E0025BC6F2DGCE009F098F818E8274831C8F7035C4DA9BD657FE829883308B60B3A293E0B700956087003DGA781EC84D8CF6A8840DA0071G908AD2AF4037GEFG6C8758A66CF78E55EBG4A94891088B0832078981495208FA094E082C0AAC07E283602AA008E00D1GD1G9381E681147E8A629B4085908E908FB09DE0512FA176813AG0281E2GA681C83F0176832085E0GA092A071B798BB67F7D7E5
	B39F8C47F7EFE8ECFABCFE5643DAEE3F0D9ABBEF65765A41E93E604FDD5CC73B39ED5BB97E876E4F3D7C3D077B23177BEBBF47BB39DF97F01F9F643EB2526BA44D6FB143AC4B7943249B7F8BED767F89D86CD4769CCF6CEC65FCF3FF21EC92BB2BGEA81BEG788FG99B41338D946E9DE674F77A5962BABA596E36FF07CA7GE4FC77A9FFA76D579A09456247920BC302A7E35E95129A7B24AF5968C72FEA4D6FF707D916254324DB7F7F40677FADE0ED7A42D5447E72815921B44AAE59E1069F9E1E7666AA92A3
	FC7CE1F18A1E920AF741F8E5AB8C90BC827F5EAA62EF1322382D5D7D0A3CDBD6942B23D7CD7F084A7274F73B54218E4A2ADEC5FC6FBC886395C13FFA95696BFB1C8E07CCBB974142536B69D8C33649F6D0FA2B335F21B07C2655E41E36DFBE4447BADBD64F1648D84556CF46CDEC6E93E3A2316AF2DB6D50E51121175490C446A157FFDA2C31518CE81F499C2E88524A03F09CF170FD9CC7147DFC29943F5715EDB4A68E3F78546C60A0BE0E88D24791CC275AB5463A2F25E39E0E77B0DD9F29A4E30EDD0D5B3CE6
	DDB914FEC0A16350D6275B314BE2B11C2AB1BA4FFAD2E798818CC53B1DDB1C83F494AA3174A75B08BE60B645695861F6763B2A59D8603E35F43C8C7ACF84189E04E3012B5710F49C0771C140D6274D46DC85E965A791FBAEB395F1FBC870DEFC92336782111EA052DD709E2CC9CF20693D5DC5DA1BEA52199E9756935BF8641F7AE5308C99D0FEA17CFEE97DE2DDDA9B0F8116FED5BD09811BE6AC5AB11A48CCB7635C3CF0B413D7B61A0D771A60399C70A9146EAFB2520D6369F1235918B0E9B453116597E4C13A
	725FB23A86F836828C7C16654768DAFB11AE04530D03E7BCC01A97DDF99F52C941CCDEB1BC4B811A0299DDEAB021FBDE50F586337C14E0D61E30E0269F233B420AF495A7B33A46133D69D8F9AD7B102E75E4A64FF6321AAF41B71DC268566FC73A281359B8AB1E6717FCB2230B23F41385DD58A9EC6C95FD0A1ADF25486F929B52650FE163B34C2FF40CB63F1DF6248B9A4346EE91E3181E51E3D8FE0C6EAD8752A50CE1E33D14B1CCEEBA27E3F6F9C250E5727422909E8FA1DA7B1D6FC43A1690C657E7C8E7BA0C
	F4411CAE125345FB51AD9BC43A691CCE7A1D915DFD02EE91CFAF674F065FE969CEBD02F4FD009F85904969A6F951FDFD94690A463695D4GB40FE5F441E335F4C54710EEA260277B25CB94F47927022FG9ACEB5229BA3A15DA0240781441EEA245F17D9C8178E6939217E683232112E8CE8B678253BDA50F50672388BB5725BCFB9A2CEB9DDB427CB8C5552D5648AFF1C46684ACFB372DB66BBC8574869BACF734E0F45E9EC895259CEE3F308A0CD3EEA1C9EDDA67463695106792DDB0EF4C9273139C81AAEBF56
	3E642EC03A8C1EDEF8BA58842069F4ED7BF23F206BBA1D518D1C4E64061E2EADEFE8A952A57274D47E1C4569D8BF774F15246F256B117BCC87CC83151E03B0FED8376EF35A5F31B5468F2A72B97D22B3F0AD716BDA52C7B541FB9740409958CF5FB39F57924376C7C9A9CFD64F7137F69AFA8653A3768C351F6E1B31F9B66B83D72CA7B2B3A0ADB78C7B3F0A355A750C4D9E0F3347CA17D2F85F7270DAEDBF4EF029EB674153E2F5FB141DB0509A40FEE85ADA22AFD9EB61E83E7654E8AA1458183E986436G8407
	917C2FCCE4FCC37AA4D64E1EDAACE7684F97CBDDE71E1BDD60271CC9E12CFC69E1EAB9AB0E4E4AE3721E96724A4E0418BC936515F793F924EFE1FE7AF765B2C3BFEDD1C78F922E9F9E592E75C15C42A3FEF58BBB137B60CCD537C8C76B9C265B9BB26A16816995E7359530F14B59A6B29F70988DFCB87D92A9D2A67AABFDD68F5369794D8DE80FABAE14A64C8949B6DAE36E3BA4BC5BFC9653A564ACD5A773855DB983GCC2FC729DE44AEA9C0D3F4B66A355D02B2593B43622D43654B35BA641BCEB254A164495E
	6C663319EC65ECD50754533BF3B2CEC79DAE3720ED6200A6C368D054CBE2D0315A655D0A6CE2635B38966D7836A21C2D377541F3B09C79B6FA71B199C7FAD1C69850A5895A34DE120F6CF6BB5D2DCE1B6CA663D3090F255FB1EBE5651E43E4B54233639C64DF58E7444FE4FE5DC7E412392A8B6823844F04BE34A59BA30EEE530E91D301AE7FDC0811F311FED3B7710F1D0EF6B16FF72C683786209B90343F363EAE6249BB16D86C9FDCCA66267DF4CD140D263FB25965C62B47E5B39DAAEF765DC7592D4FEA7D4A
	B6DEB7AEDC220D018D47769846C070423E6C18F3196FA71D2B4640G78BFC444408322BCE53F073A7BFBD176FDE8ABC6F76ABE248B831AA4C17762BEB28737AA323D127C33DB0172ED6DB3B9FAE5F3B92964152603CAA51DA6D66E0474ED6457CAEECB73FE4CB3B7827A5B88ECA70AB611A71DF7897BFDD6EB11783A33ECB09B71EFC1DD65DE3F32D95376D4B64A96D33FCD714AEBBD36C12FACC3BB17B50E136AB3769B5A19563188BEDF0BD06D9CBD7F653CF8GD65659B62C8769C0D37687341F5DC66CECF277
	D6B319035F214CD1C931D271125F7915597987A6AB688F9A19115DB97111685B09B634EFB250941E0776AD7F182C43754A0ACF0CEDF7BB6DA4AE4B2911AAED60474A5DB0ADF774025D069323221E386DD85EC610BBF89E1677C1BB16173DE7B9881DC7A673BF59CCC20471EFF1A0FFAC70268B7ECB9D5E7C77B83C63F411D3447379904F67A35FC1A752B19B04541412764341E6F7ECF3241537DC3BE8D9BC65AB5BD81E09A24FB048AFC964396A303FBA4F78CEB9AC62BB9262BB9279E67A65E33A05BB5E963A6D
	6AD7DC7D8A53088EE7C0AB4CFF7BA0662FC05E51A27F4B873DED722A20CB831A128B10CEBEC270D607D5311AEC561F657215CC7ED55F12710BE6CE3FF7270626CD14EF42914C3795728C81E06DEA14D95B2EA6GBE9DE05105A8F745D132FFEAF17674FBAA693F4DCE0F22EFAD3613845EDCF03B1867D651D8D13E128FEA6D42235508F7BD7A706723A2AEC00F4E8BD1573BACDADD43G9F8F10A6F41DF2F4A46D9B537732775611D8258B9BDAFD59F807E665233A48E43D6A670D47D077D920D755C5287B45E6A3
	5D7BA01D714D94FC03000B95FC2F74987125DF04E56E10C65E2647CAA8D39A57D6D0BF8EE546D874B161023430F1A8731D2C630D89A66FC0964A0B023CD2053C0D32113C620B51B69FE5A3DF85604C97A35F41AEBDDF8424458B3EC784DFB2608A7F087AEF4B895C961C1B03FC0D40B3A87832F2099CA70D8E5A7E38649E0C77395F2EE3712E8D1E6DD659E6769875F73B3CB251444F3739A8BB9664268B59F10632994F87A5223EDC827565926489A971EEDBA61708719D50A4893A46E558CEB33ACB160936B08A
	5A42A824DB30BC305DFA16E37E8A70C48B3E5B17FB677F498A24CB831A12CB11AEEC8519BB11B16996E73F436C41B166D33B35E34C56CB5998B3941E63846F0DBE3CCC56EBAB30ACF1C01BA168AD25FAFA6647CB0E1058D6822C356EED95CC1A767D5AD2912317C10CDE06F2CA5FB5EE378FFDC566C8F653FE484AE6A69931FD1E0F2A74E7B8A2AED37712E3B43F915859063F5379F142E56CEC43FC011FC47153B5F826EFBB45137742BF415878CF389FBFF69331F59A3CCB51D087812AGDAGDC51B8AFF62CA3
	7BC1814FBB6CF4A8B0EDB659F6FBED846039037544B67B65C37C3DFC3DF68C496C70390958A1A4FA787B680C2F03720DBB8EBEB616DB793D768CD66E6B1FE7939D0D46F249BC6F34E8F5AC47EC77D983E90F4AAEEFABE8G68GF08104DE0EF63BFCFD45D0F643618DB798AB534C915A6235633685AB47451BC9B9E2C06FA400B400CC0012AB309CAF2D9F527F5B868C7D4F74FAC79E6A0C5406AB3CF5E6FCAB07646B527131723C32052CCD8DC0DA98C0B4C0A2C0BAC07E15B0073C924B35E34316214A3599B73E
	DA1D6267D67D66956A7B380F3E0DD7FA6BE7DC4EFF7A70D56A4BF925D1B9DD0D341C109686908D1088108E109F83650C41F21E33E17D107EA39DB71411964B3700CC4F711611797166A1791AE33C79D81B39360C6CA58FF51691E7867C58A2BF3718510267964DB138265857C35A4088F80F85C80A41F39A3F699DD939CE9B12AF4D30DC9FEC2C9DC63936F92D1F305231326DB6E359B2C5592E25EBGF5D73595B483182F423235778552F19068C859A22FD275E4FCF2C0BE8289D799152D57760EE4385E0B53F8
	5E472F28536F4912BEBE85721C81404AF9C39B16B37F4FD81EF72DA32BE31F7A70AD573DB77C59280EBD310D54318EC8D3GC2G22G12G2683940E47BA7654060D432CE3DB815764F46EB7E8C6382E13C7D84627FD78746DC845F8A33EB3770DCC5EC5BEFCFAF90D0672766D9F193C819FBE3D3C4E71C6BE6C5DC1FC28C0DAA8C094C08240F4005C58360272D87461460D1B077221B8F2B99CBF965AC6562E3CBDA4DFD32CE12CB611F2F6C15AA0C098C08CC0B2408C00222B311C27ED9C325F5314F368FE6FFE
	9FFD07674FB49FBE1FF83D5AB08E6CA3336B68A1799A8D65B5B9C6A62FEDC83ECEC3F9EFBAC7E64FB29FBE3DBD95C3F949BF0FCC5ED49FBE3D3C90C3F92107C7A66FBC9FBE3D3CF106721A87C7A66FFB9FBE3D3CB8C3F939C7C6A6EF318F1FDEDE32213C9BC7A8AF4907CF5FA7266B647171CBB9E9E78C7A78D6F9C021EBA02C0F1FEA567771E4ADAB77AFB04F7C8B76710F52318C51276AAF58470FBABA32720C75615317276FAFC665F9F34BF26372FCE3B2DB1DECC59057A6EC7B0D46AD23A05FB0D1263CDEAC
	D334A85356E3A3AB5357BEFC7AB225694A44F4C8B1631EFA26506125916A10A78D25C35DB5C6BA54889D1A2FC19D2E7349EBF8BAA48E29C310CE87662FCC43980CG5A18EB50DFCFD8081EE4DDA0D168B918B5B2BDCF7161536BA9CDB03215A2238E4593D0072A6C1169304907CF2FC34B84A3DB356FB532D59F508ECEC0DBDD5105E372B021E75A8875FC5E072FD2771EB44108AFB2E7E4722E70617329CB0672FE489D19BC2B8F1F3EFC190672BEAE99193C35BEFCFAF9C5579A71BD66432787E3F9B307642BB0
	14F7793211156FFA9FBEFD799A8D65D9170F2CFC8707646BB414D73BE2E472B68F49276864317EEBE0B759939A4EBA89DB46562EA53C570A75B964DADCCB185BC2571661BD85A063DA2C67F925A3735B929FBE3D5F8A2FB3623BE904726E9CD2DE19CE9E3B2F36191EC35BGE9ADG8C373C061E1B0177106B508EF7533BF8B1701EG108A1089D0F2FDDBC15575C8738825A1E7D648993BD6FE66173427CD0266475AAE0D8E04BFC627D7A0C8505192BD08FC72BBD3134667CCDE328A24633BCF1627FBA7F94E67
	3F35F7CE587C726CCD7E6F9F1C3749787E4138CD46778FAE5B74BA43C39E5A5B14187E67CD016FA7DC374968FE42B2DA7E7B69FE213B5FC1360528D8D61F6481F24784E6415F74BB589419C87668576CA51CF7FF70B9596F6A3B9E6757AD1F55C83E736BEDE62B62F4E3A62D32493D4BE29113EB363F4757259B7C5B6F2B86E37B35B6985B6F7B0640766BEE88EC3F4BB77B57A5F63331AE57EDB656A5FEF3E0DD92B77B5305796B89051C8B7439BBC28E45C8F8AE709BF9AE27E9D11CAD55A26F3C2D7E4BD13855
	389C0B37129877B87B5DBD32DE573736960BB4F5E104EC3993F5785D994A2F185C3D32CFD9D7D2FE16660F3FE6AB29275DD633554DEFE0B0BB7C7D4D05540E9E0F4CF65C9BCD0A09173F44B20F17FF1B627C323A96657D5C683F7C3FEAB4AE7FEF9B7D17FFEC635065BF3B51FF794FEB9C3A7C17B47A0B87D65627ACDADF570A366E6516C07DE850757E76D4D3687EC07875EC6FB4CB609F2778F19ABC5BD3F5D0BCF90F013422B85C833E2ED72BD71D502B439CC82F01B8FFFAFDC24F6205446975DACD7161F1FA
	3DEE5A07FA0D03347C9B5A8AD85C149EDBAAFCC663E6978C905CD633CCEB9023F94C31D47845170615EFEC1F36FC64EE9F9BCB3457613C57F6830EA54277D05B41FB9CC0728D58775D5296489669B7785A021FFF3122EC086F2D2BC4BC4D5C66BF3EE7EDB30E6FD737790F6F3C6DC34777026D7E637B0D6DC34777726D01633BE81F362C2B85BEC90B7FD245DF303FG7DE2F1BB6DDBEB38AEBF6D70EF1B019D4636B93A433FEDCE5AB134ED466E70EF1BB3F78CED1B73F684364DD61BD7D9DB6AB18E1A8244C113
	1FB8E88A9087BB079187BB8344414EE14441CEFDBB4F4AB85F6E6D4FBA0E3F529B8FB510610FB93C71B59C7F25D39BC7554206850735789A017F1B97FE0D407FE6D00B2F977875C7347835827F4211F9DA39D558CE3CCD57BA6847F8EE44F6628B7A2D0EFAF8EF81683BD12CAD84ECB3C781DD580DEADB91F5A33641579C556AD4AB5A28F17BF1CDAACE48AE24F7A4B2603D70A6982F5F043267517D5D8DB7F937C5AD7C5DFC133FB66A67E3DA59EA7BBF4530AC83BC1F903F79D5CADEF62C457C56986697497309
	751B5FCC6F7C6A50B6F5B2365F09B721EDF269DD88A93E2D209820AA9EED13EE38275F944FE4F64253952F4A8F0AC73F0C4F72123D8A4BF248702CC7B84FA39A1E711A7C12C5FE3D59DE792D41321C3A977DBCBD9E4BF2856D0BCAEFEEAB2887E839994BB23EABD00C755D6C4F0E6F64F8492EC67C835E789A441F1E6B05DF0D786E92AFFCBD625FDE66DD1F2BB9BEED19977DDAF1A7E639B7FDAD476FDB216D2FEBC4BC7E27B56059G03F233F147913ABE90FA33FEBCB21A62A3EE560FC7963660F8A48A528AEF
	C15FEDA8551623268A4B7122B7DE5835230A5C7936198EB97B95C9CA3905646D2C42B5CB973C878344GA45C4256657919690344AE9637498EDD881DE8CD78AB61DDD5033CE570DE8F5081B0G907ED754B1381A4824ABA6420F070F10BEC63F1E42527E30F8BECF531EC9E1E9EB66127BE5C667BA38DF0F903F301BF1686B8CFA277D73E6D337ECEB35AAB06BE4E7E1AB3C4E42CA892C3CE589B887DD79B951C3741FFC0E4456B496FD0DEB9A7589D8872612CFFC60B9824DEF25A75037E9FEA9706FD81DD0992C
	4CCEB316B9D81439F63FDA4E8D1A5F376EBB71E5FE64FFA023338E6B772484D617539354BAAD5D6A2FFE976EA5753568D6D60F7384FEA94517EB702CFE1F37874F42EE003448DBF16D6C41B634F96EEDE87395221DEF85DC9F4060ED2A9E2170FB5CED3A7E164A3E090E8FA65C46E44F9178BBA8FE22864F6F19D1BCF91F8EE94DFFC31DDE91F190A3F05594A7A5C29F1708384CBD84D7816F8D09287B25BE67CFDA93D5DD7B927D59B3140EAF8693190E8B85BE126247E970CC774594CF5E13A02D696F28533BF4
	DFB1815EA7895CBE0ACB0777CC013B3B974B18FFBB62E619112EC3603E13114EACF0DFD13AD8F8CF9038AB054DA65E0E36D8AB7CD8FA87ABF75D9DEA791B6E70E70BC1BA1E683CC3FFEEF98C450F3AC3FFEEF91E0C671647C15A22A454A90716F1905EC3842ECF14BBDC601E21E51C8E6F521D08AB267BFDC570DEAEF02114EE833CB78B1CE4417CBA842ED36497A5F01EEE020B037709F722FD42766AE3A56DCE55A6336E74E71F77E89DAB3CCB9F671594DFF617BE4EE377E01CEF0034483BD0275B3BC45D3B9B
	F11BDA9157FA37B87B58A20976A2C4737B0D16935FBED51ED0994CBEE7D7093590A8F34A5DB8A6286E72BA43C0AE322CDB3743EDB5EFB175B6539186BF1FC0778488FF51C49CC74E2FA6F9B6433B99E0F0A25E599E78906FC5C77F71C2E9626346F753D3DB0FE407CDE47E1F401F748E72C75DB9CC668FB5A8B38352AB6EA13813EB347DBD999B1C1A4D76B5D87A75DE69E46ECC3E6D2526BF6315CE743DA9D71B3E22D61BCEEC748967E7E53B6383ACDB669DB1126D6AA03F5FACD56EE1E58A674F523B4E49CA88
	EF194D3F87E07173BDGBA42E1656F69423D700910C74BBF886EBFDFB722DF7F61DBF772A45C8BE3D082B4D5GEF707746FFE8462AB43F51E64CAF5CA03FB93A7C2201A6DE13DF32A6BF5E8650352FD440E77CC3348B2B88AE7F5E360212FB91777CEA2C038D77E29DDCCC67AA21709EADF04BA8AE71DE762D93069B37E738750564F5BCF5C515FB4330E59C6F778873046DE734216D279B680977B0507619C0C3609D1E4722E43D6DD753B6398470D549E867D19457886FDD8277A66B6F60BDD860C2C59B9FADF0
	BB45F8A0B3996DF2AB9DCFAC3A8F6AA3C073FD083F0762DD709EFA9F7B8E8BBFF7ED565A91F97E9B6D5AA2A15B65A5A3CFD23F7978CBA1C778EA310C3E0AB150DF6F2B842019F49F2BA7841FFE1F6AABB61F69B253FBD0D607555EEF5FED7DD9162A6EA7F23E27B61F85743977635CEB49DED21E9E272D5F6E70F03A0FE83DA079AB77233F627723DF03846EB3BB7AB56AFE3457D9A652664F02774287A0D6GDA818681A2G629EC03ABFD23A14875877F24837F048F7EE489D45F4FE671051951BDC5C8EA4BF72
	2C62CFA283ED547A002F5DA28CF088F1815286050EEB04EC5C1BC55FEB6B255177G677B491B4042GE938E73CC42A2763AC3C974E7C322B974F91C81350FF3F6A2577916581E551A4726DEDBB7954A40F2BE93D98D72D13FC4B2B0DA98213F8DCAD65E9FD135438E2F907FE23666D92B12B7849BBAFC05E411374B1BB255B3B9C63F72B32EE937DC8189FD9F3B432220006G4A0A1B24EF4BDAE46CC792A7E1AC1FC9F1A970BEC3600A44D83768C10C09DF4B24EFB5437BA800C800B80094008CC1F39A2549FF08
	FD13297CA1764D25A6F876BD04B40B642EGF1C0G73A679EA71E65DFB4A034631BCCA68737110326670B856472D3E7FA970430F3EDEA23D2D0B477BDBD5BF9E9371987C50506DDC845044BC445AB912FF42C37A1849359837F3A5343F198874A98FE1BD7954625B4EED27F4F49E74B052DDA2FB6BBF3BCD55FF298575AFFE58D77F39BA7DAB002661E1D57F5607D57D197C13E84CF5815EA6641F526D2B67855D28E77843981BFF22EDF4943C47895C87A60C575407517765F4CED31552D65088E0CEC1FC9D458F
	24306FFC058B7CF5439E1B988111C1E451B8CDB19E733DAE5D7B0BE4E02CE95BE3ADB0DFCD69C2DFCDB050C39FEBC9A92C4DC0DF25266847325F36E2FE698679695BBBE9B23499135531EC79E4B5BFE603BF785CC99E8A985F1FC64817F65CFCECCE7CA81D0755CD562FC3CF277806497AF568A555B897EC06341849F8877A49B652A696BD82718150845085B08AA06291DCCF3D6AF372FD0B00F7E6DA1D2EE67900EC539CEDE1656B2AB95E7231B9574DF3F04E95FB5DE55204510DFE3F33957388737BC47E1C10
	F4CE96F932728DF6503F1B70A84CC3G9AGBAGECGC10FE279DE7E3C02150F9CD35A2CA8261EBE7A15B3F16849AA9FD451E4911D152D4FEB6FA47CD13EDF431F717C197C28E6AF087A2263EB31BF83E9AB288E60A7897F1B7119910E35FB035FCB42C6D38A51C6A5FF3AC8EAFC20B17BFDBFB6AACDE5BA38D255B9F7D7CF5DDC264FB5622CFAD46A716AB30DFCAE1B377B2754117D82274B7493395C4E5B48BA1C4F27260A36F4156FDE46852BF0BFA2A3156DE594CE01316794766DABD657672E413ADEBF457F
	38893F975F8CB4DDD3543A2ECC5177D3EF6DA6E3BEF2FD3F57EDD21CEEBA8D67E921CDA4CF6FAFBA61B7DCF76D556FBF909DC7CD41F3626F3623BD432698599347ED4BFFD4ED77256CEBBB330C36CB1AA23E8FA7137E466516AD325BAD1BE96F0A74B1EC7C843469027E017D3E367ECCC6DB97CFE5362E02E763D4B646E7363EC474291DD30D5BE9ED3B6A821A20292A2D43276A7B54819AE363G9FB395F52BDFC36C3F4BC5BF20F040E4535A3FAC0FFC6B9677123EF5D8958FCB6669FD3349B8D273E9912092D7
	F0381FF2E53D1FC879D22662B7D51A4CC669B326229F374A68475C470C7D685F37E76ED1FF5F35D775734CBD645C3DC3BED8AA3EA153886ABB489F1DA8DFDDD5D1DAD335926BF764C16DF79A6A677D5A703B4C84B6BC46FC54421FA12F1D134564ED5E1B8DCFF27E00797ED9F107A778B1667BC8F846BE464E67B25F37595177C90F799F57E3BDCB851A0C47D45F6727696B594FAB543DBD5C174D27F72079E74E189BB5B70E984D0AAD0486FCEFF13A6DFEE8DEDDCD5A79ED83AE1B35476A1B1150610D050B7572
	7886A5CBFFFF53E23DAC2F740B8FAE71114388587D1149A6237BA3C5E00B72B4719D96B686F7774B4C3FF77F20756F22F3ED59E8B73D4D9B52189F1A5354761B456852BE0C51D11046626D0809644E52AF90B1BCC124C7F62B69AF0936FFC6DA20366A2FF4DF145565C322DDAAFF9C4B96AD7B36B3FF956D4C0647D92C3542336FF1361E40E26DE3B177987538F1BB2305B02009FADC0D353847753176D196E9D33E10E557E665CB27C75136A96FFD3A1827ED03D67D105ADCE07FE5D920CD77EAEF463E3D44ABDF
	15196F5B9FA06313962755018D9543FFEC2631CFD1F53CDD7A1BCF3BC46C1178B836CB9E0BD1FA0AC83F2B5BA8FD06C8FF46B07F5CE9582E4574224F0B279DEF3BB64D2C7E1EC1BEFD086735AC815A351AEAE857EAD6A26D0F96622F55A2861BAC28CF4BF16B3324CF7D3D5A2A6A53579D281DDD8D7AD48BFDA6F423BE6C3D295BB71613457D3060E93C5D04E76CB436A646E2F903C9341B530CE3D93B9E128AB49953B46D66937AD8FE5A21468E1EE7FB74DD62DB355F7308C0E46937B62BBC35784DF91F718A51
	3968890C17E9FB0C524B1F40F8D9ECC27FECF842F8FC486C7F5DBEC978F4F4AB72A8FEF8820CF1B567919E2FD6FFBF2B797DD675095F232BB921B2585C6379B67FE7B6DFEEB3BE3379DA1B719D033C7640779966377B7FFB95C56D5A3FD74166CE4DB6929751603FC400F4GE9BA444DF41CB7DD587E1EC467CD86F326ED66DEF2E6147C15AFC51C1D6D70DF56100E217FB658199D64ECD43793BAF6BB499F713F6FA07ED21CAE0AE438488EE2A3373537CFE1249C7F478E92EFB659C251ECFD66985DF32B02F2B5
	CE47B51B9F69BACE9F3C8F885CADFBF19DA7C8601E2238E4F84F18AE6EB6FD2D1D9766BF89F34FA7836FFFB0FBDFC647DDD5C05B8C6082888608G0885C884C8811881107F944C0DG6AG1AGBA81ECG23G421EC27F5C7CBD797BB4ED26FD3E37F0G49B7FC29135CA6ABF1930F7BB82F3315D110DF5CD30175E7ED0F037E4D930B4966A176E66B8EFFDF056B8E93A10F7C2731CDF9FDB50EA19A1E463D654F685FBF990077D000A8C13BEE8D36A571CF9FEFDBB21AD49BEC13443948D9CF232FBEEC571635AA
	9DE2A0FDB8E5BDACEB4B4AE4DDCA36A570FC31CCF472537F5455108E289E3FCD59AB230DBAC0F6E8BA167BCAB14ECACCC79BCDE6E7D21FE9ABA88628FA86E9A709BE3469196335D19279B3D63C8C4F3955B2D41A8315213A2E223496A6ABCC7EFAB14E9CA5F4BAE6463D6C894F203D1B3C4E3A4EGFC6E3343299B919E12D7B15056BD0B713D246B63G715D62F63AE437F208E169DFCA6176FE530676EE053CC21EC5FD7758505E894F223DC3A8EDA63C97BD57D6D0719C527E560E65EDFC6EF86D3D36DF0D49B7
	3AB11F4167D066AD748CD4AC3CA78124BD07EBF553A89FAB7BF7DD9F78ABBB343E1F033FF6F56F13FB14D6D32F04753A446B3BD619C45E0C403667FB02DEBEAB831E7AE171E5FA35A34D4053B5849FDFFB11FD5B1127C59DD1A00F68996807DA8B7AACED865A4FC43F9FDFB293E28520F9A652F60AF12FF966717AEC7BC155E72F0AD80F90F9DFA66ADF32406575A02E70F94415893A466731CEDC6D75AD35C14007BEBF9C7BFE69F58EFD9C70CC98020F59B2F3AF5AB28968F37F093AB5F621ADB77C93ED6926FF
	3BD401779000F10256AE4604F17FBCDEDBDA87D4DB1EA75A648C1177B2012B48C09BADB5E94BEA867CC046F0ECB4466B1CF78870C48E41476F145A7C0FD5FE308DBDD6B15336C406B1C66BA1973F6BE23551B5BE0B45A30BB1114336C860F6FADA1C9E2B7AB72E4E6AA2745DCEB73DD44366A117F761BCA4964A1184B08B207085D6A646276C755EAB4BA0FF16106F17BC389767A8E5AF982F6D9040351DFA20A1007BAF4DAF6867A8D9DD4447FDCE3775E727C3B159B65B2C3D8EB29264EB856F08186AF88147CC
	8ADD7F8E06773DD93877FB0862A281B7D37860C63BFF9F5CEA9F5A87F7590D470B49F65F716203F663716223F66DF81161526C44963DB0FE6A90F4CF8A5CF78277AC4D13605A2DE625CF60B328DE845FA49301087F97556BA045F560D8DF037B0E63F29DA4AE2048E0F6641DCB3FB7EF315AECFC2CCFE92C8E88ABE10F458E222355E552683DD460085E4C97771A51BF1B05FFA619357E615F82D8B532BD2F49A364FBF37548764A4653F1E55C8B7A3D32B80ACFFCC13FD736EDB5360F49A47EDF44B66833B5DA79
	91000FF9F1D8ED104FF9D882891CF712C19E4C669B5B518F6A9909AD6D6887F54F52BE6437E20D75BABCC23E586366E3767E1A764B69AF6A7D70BD454FFAD16F0747C41F1F7BD2DBC15FCB680749DE7DDE9260D3DF9A0E9F9A8C754E603C79337C6D0B8709F1CC0906260A7FEE04E7070E1779EA2F6C6BBF3627037EE3F570828F619173F6B6D465E9D19EB287975F5426FF12097D658BBE269B72FB0946E57AE804FCBD474D47FCAC513F3BE11B25777DDC0A9F1825777D69E20C9E84E953E7216FC33C3EE3587C
	B20CA9DF9E0E6F5F6E566B2DE878028D7260774C695FCB0FF8196917A3708D949F2D41733F49A17E66D49C24157E8B7B3C5ABE5FD8286F534682435DE090B337483EF4CB8C680AE55F36E166886B78EBA3642B9F72FB4746F63E0F3681C17F524747A3949F7AAFFDFC74DAB0BEA2A0CDFA85634364B516B683FE60156144C70E215EA11CB752A08F46F725A1DFAC27CF744BB72F4708AF05534F704B3752D0DEEEA623AF4E745717A4757A464BBD3D3EF1E5367852D9AD3EF1159D705E28FFBBAF9AA15F4E6366E3
	71B30D4EDFAAB275F1759C4557E76A63EA208B632A915222B2B12E5CDE730F42FFC35D7E77F0626AA2433E3F0E73B6996441785EB3646B6474AE83BE662BCA132F7FEACD3EFE3E3055F70CF7C92BF60C476CF1DF9736E7C1A0B36E5FF80724E00178EEA47D6E967BEC375D259C5AC267ABDBE4138367F84BCCF8BEB6635FB82F0C23F7F7AADEEDABE88468FC9567FE733A443E412B0176A3179E16A41C0354F43D8BCF6D1F4F5ACD7E4565E7CF6556EDDF35EE7BC642B38B268F09EC5E275221B943178C4CD771B8
	B4677E3816EC8F715F37E8FEFF317A446FC318CF288C667B36B53EF173439A033821F373E870CD5C2B58374D91772D92DF95F7GAC2A0D3AB43F1F14CF3C3D32ADA75EDE99866D644B96DFFB3553BE195CBDACFE8D6DB5C15CBDAC93385F1B714E06A2F04B84EF30409D953CE182779665CD03774C57507E336D2A4D97EAFE075BCE3C7D2FBA21B2D819AF327B5ABF4A6EEB7F6AEEBC0F8B3E60763A3387EDD887B8FE871A1EF38F926F5B69FB38F83F4BB412B2117C06EF37A7C6A4E338F711180C07B5F5B1CD73
	5BFEC26B8C13B17AFFD0AF878C46B9C78C46B9EF533ABA817C13A8626218388F15ACF037D1DCD9D6FBC1DD9662BE96778F9A846EDD9AD321709EA9F005D6240B9638861A9F39E31CA9F0978839795998EF69DD2AEFDE507CBE5AF5627DF4768915414AFC3241B8E52C41B8653A3DE82F0EEC3457BB223FB18B5CDAC197ABF0670A7D5AF8E15707079CC7AE4A712E333C5F9A12CF7FBDDDBE0E2363460A1C613583845878340A2E51B7G1F7738F5AD45B7EB70EC1CF64B9E310788E9F1B9D85E05DEFA1766C29C67
	8656050D292E552DA19FBEC2E2102DA1AF97E7781B8C72522E8FEB6FB0123349E41C55192B6A4E72BE6F281A775D627ED25FB072467BCBF867EED013B7DBFFCEE0EB19008F8360F7C7A4D55E19C2DE3C013CBC1D3CE420A17230AC53B572185D2A589DBF53006E6ECB9355A38368253C768236163F379B5772ABG5788E08398C5529F40B3C5E9BD622EEE1EFFFB60D95CE820098740757A64BC3DFEF7D85473C1557CFC50C11F73E4C4FEAA70B21EEA0BF7AC2C4CD26DF716B8A71CE1205F9C1D7DE2B4F707E86C
	4E566BB7D9A72BB9DB1535C51C95AC1ABDF4EC44686EA9157B482A6F7116654CD1E53DEFC5D9F586328AF4321A002693G4F5F3BE66BE37C74DCB56F8942E60386F96BE38E6F4D214DC2FD723EBACF4D3BC6544D08E158C81FF70CCF5E45F3543C2F92FBC7F1FE72561E8B5767BDD113B77B5EE27C92B65F32D99DB2790BB2BBE407046D7EDA5A9724G4F0C5958162D26672879DA3C66F762AF3A37B494B099D3CE288CD6668F3BFC7B440F3B34FDA21BA36F92F3644C5958262D27D3E416AE09BD663AB9183E07
	9EB9E63FFFC5361FF89DD8AC87BA37D9DFD1DADD3792733DCD6CFB5BA0DF7F7B3B638AD4D9FFABD4E50D89F41E24262A0A89E37D59EF3B4537ABC0D68AG0B2B5A9AF296C2776D0A6DB66741E6FEDC136BFAF7B72EB994653773B5073769B7CB1B615D8CB088404FE008B350E179466572DF5627662BF63F229B4F3BA4093C7FA6FC11BB97FDB1FF214A6391E792B608742AC5EAFA2BD52C074C9D2EEEE87B5D2F2B79BCA9740896F28EACD653FBC4CC250974F3162869D3451EFC49BCCC0FABD2FD5BE2526EB732
	74C7C95567752755EB9C528AF9744D8BBC8EE1FCF9DE6B1D034093BAAC3EBB3C562F47814F04E1717D4EE71FE0F86D436FC748176C4517B4040E8458B8F0AC0D6554F97A716159941FBECFBFBEAC955F6B1D85E91D8558263E6DF5C6A79E704985433153F84372CE673C197C3928303D202C107DE6FCC9DE7E2C0734664261489B70DA576F829EE5D8FC5FF97DDD38E0600998965F33DEFA46GCF4230783C6F84CE821E74E171BD61355EAB4DEFAFA81A9F180F758F7B8D7A8CD7176FBC2A44E03D6ADD03752A29
	867BCACF986CBFBDCD5B3A72796D1ABD01B0FAC7045F23557C7E70FF709D3D4F7E875F512BB67B5AE6355957B637F763DC34E1BE4EC5DF906BF64D827786353F8B5E43840E6D35C441FB2240BDE6603B69863EBB55E06DA34CE06D43E575256B377A52659964B74FA03FE386B198E490032F6E75254B7122E3FDC9F68F69C3CD62A49176A759644A1F1482F6D9A495DA34772D744072293010B3C26A59A4F2D582CFD8617CF00768974B9660FCE714A5701926F3557BAA40436EF2A57E287DC6D469473B8C3F91B5
	6B471F33879730FA9D491F64AED7F3CF5DDCA6FB0F0574678176B21A37EDEF65653BEAD8346C6C683B4544DE49A0F386C0514276028A0086008EGF656E07B52F2497FF9DAB640D20F0BB31E0F0A7D1F512AAE9E7A0C563AE263B3DA8D453EE73436959B1F517A22D8FBC6EBA9455D7B49DA18F1C5BFA365C17CE87F77382505E27839130ED7D28F542E4BF96C221CECA9F727B41B6932D34AA5996E1472283C1DD28E68B2C08EF867EA417313F4140D6EC60CF5CB0BFD07431B47BE78C0745E4622ECA99BD46A13
	198EF98749E69555028C3DB67C97247B47923F907F3DB1848D9964ED79AF4AAC01EAF35990B4B0E749BA6F174BD42455DB7B1CCE0FDC4A7EE0D02949568B35DE6933171A9C66D28B4CCBBFD17A9DF2A979A3A925BDCE0719369D1EF57E321B27DEA6D724056C16D6A9DEEFD6C08D394723C885A4F313D2EAF7FA9460A1178ACBE5F263D0118A29A40F496E02BA627B4AF324973F94E9FDB979A003295469AE7538CCBDC088B590044B66D2C73F3DDBF6FBAACA654A5E4A522A4AD595708F194DD55610FF562C9C11
	EBC8F3D5B28C1A44939A187F9DAC1D83737F66A95282EE5ADDAE6AE1896FE02FBC263D23BB6B249E3FF7303B0AD8FB2E94296DBAEBFB5F2EA66DD38C60130A302D5DD7C53EB1686F7C7EEE3B49E6031EE77FE607191C655FF2C81155FBDCAB2AC97E9E246109AC6D43EA2277BE8D4F7F81D0CB8788E55FD6F137B0GG0C26GGD0CB818294G94G88G88G530171B4E55FD6F137B0GG0C26GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBA
	GGG71B0GGGG
**end of data**/
}
}