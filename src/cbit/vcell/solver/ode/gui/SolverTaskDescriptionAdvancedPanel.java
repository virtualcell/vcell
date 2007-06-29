package cbit.vcell.solver.ode.gui;
import cbit.vcell.solver.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.math.Constant;
import java.util.Enumeration;
import cbit.vcell.solver.ode.*;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:25 PM)
 * @author: John Wagner
 */
public class SolverTaskDescriptionAdvancedPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
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
	private javax.swing.JRadioButton ivjCustomizedSeed = null;
	private javax.swing.JLabel ivjNumOfTrials = null;
	private javax.swing.JRadioButton ivjRandomSeed = null;
	private javax.swing.ButtonGroup ivjButtonGroupSeed = null;
	private javax.swing.JTextField ivjJTextFieldCustomSeed = null;
	private javax.swing.JTextField ivjJTextFieldNumOfTrials = null;
	private javax.swing.JPanel ivjJPanelStoch = null;

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
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getCustomizedSeed()){
		connEtoC23(e);
	updateStochOptions();
	}
	if (e.getSource() == getRandomSeed()) {
		connEtoC24(e);
	updateStochOptions();
	}
	//Stoch Options
//	if (e.getSource() == this && (e.getSource() == getRandomSeed()) && getRandomSeed().isSelected()) 
//		updateStochOptions();
//	if (e.getSource() == this && (e.getSource() == getCustomizedSeed()) && getCustomizedSeed().isSelected())
//		updateStochOptions();
	
	// user code begin {2}
	// user code end
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
 * connEtoC22:  (SolverTaskDescriptionAdvancedPanel.initialize() --> SolverTaskDescriptionAdvancedPanel.solverTaskDescriptionAdvancedPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC22() {
	try {
		// user code begin {1}
		// user code end
		this.solverTaskDescriptionAdvancedPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC23:  (CustomizedSeed.action.actionPerformed(java.awt.event.ActionEvent) --> SolverTaskDescriptionAdvancedPanel.customizedSeed_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC23(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.customizedSeed_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC24:  (RandomSeed.action.actionPerformed(java.awt.event.ActionEvent) --> SolverTaskDescriptionAdvancedPanel.randomSeed_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC24(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.randomSeed_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC25:  (SolverTaskDescriptionAdvancedPanel.solverTaskDescription --> SolverTaskDescriptionAdvancedPanel.solverTaskDescriptionAdvancedPanel_SolverTaskDescription(Lcbit.vcell.solver.SolverTaskDescription;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC25(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.solverTaskDescriptionAdvancedPanel_SolverTaskDescription(this.getSolverTaskDescription());
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
private void customizedSeed_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getJTextFieldCustomSeed().setEnabled(true);
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
	} else if (solverTaskDescription.getSolverDescription().equals(SolverDescription.LSODA))
	{ 
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
	}else if ((solverTaskDescription.getSolverDescription().equals(SolverDescription.HybridEuler))||(solverTaskDescription.getSolverDescription().equals(SolverDescription.HybridMilstein))){
		//amended June 5th, 2007 to display uniformOutputTimeSpec for Hybrid methods
		getDefaultOutputRadioButton().setEnabled(false);
		getUniformOutputRadioButton().setEnabled(true);	
		getExplicitOutputRadioButton().setEnabled(false);
		cbit.util.BeanUtils.enableComponents(getDefaultOutputPanel(), false);
		cbit.util.BeanUtils.enableComponents(getUniformOutputPanel(), true);
		cbit.util.BeanUtils.enableComponents(getExplicitOutputPanel(), false);
		getKeepAtMostTextField().setEnabled(false);
		getKeepEveryTextField().setEnabled(false);
		getOutputTimeStepTextField().setEnabled(true);
		getOutputTimesTextField().setEnabled(false);
	}else {
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
	if(getSolverTaskDescription().getSolverDescription().equals(SolverDescription.StochGibson)||getSolverTaskDescription().getSolverDescription().equals(SolverDescription.HybridEuler)||getSolverTaskDescription().getSolverDescription().equals(SolverDescription.HybridMilstein))
		cbit.util.BeanUtils.enableComponents(getErrorTolerancePanel(), true);
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
	//Stoch options
	if (e.getSource() == getJTextFieldCustomSeed() && !e.isTemporary()) 
		updateStochOptions();
	if (e.getSource() == getJTextFieldNumOfTrials() && !e.isTemporary()) 
		updateStochOptions();
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
 * Return the ButtonGroupSeed property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroupSeed() {
	if (ivjButtonGroupSeed == null) {
		try {
			ivjButtonGroupSeed = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupSeed;
}


/**
 * Return the CustomizedSeed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCustomizedSeed() {
	if (ivjCustomizedSeed == null) {
		try {
			ivjCustomizedSeed = new javax.swing.JRadioButton();
			ivjCustomizedSeed.setName("CustomizedSeed");
			ivjCustomizedSeed.setText("Customized Seed");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCustomizedSeed;
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
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelStoch() {
	if (ivjJPanelStoch == null) {
		try {
			ivjJPanelStoch = new javax.swing.JPanel();
			ivjJPanelStoch.setName("JPanelStoch");
			ivjJPanelStoch.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRandomSeed = new java.awt.GridBagConstraints();
			constraintsRandomSeed.gridx = 0; constraintsRandomSeed.gridy = 0;
			constraintsRandomSeed.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelStoch().add(getRandomSeed(), constraintsRandomSeed);

			java.awt.GridBagConstraints constraintsCustomizedSeed = new java.awt.GridBagConstraints();
			constraintsCustomizedSeed.gridx = 1; constraintsCustomizedSeed.gridy = 0;
			constraintsCustomizedSeed.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelStoch().add(getCustomizedSeed(), constraintsCustomizedSeed);

			java.awt.GridBagConstraints constraintsJTextFieldCustomSeed = new java.awt.GridBagConstraints();
			constraintsJTextFieldCustomSeed.gridx = 2; constraintsJTextFieldCustomSeed.gridy = 0;
			constraintsJTextFieldCustomSeed.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCustomSeed.weightx = 1.0;
			constraintsJTextFieldCustomSeed.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelStoch().add(getJTextFieldCustomSeed(), constraintsJTextFieldCustomSeed);

			java.awt.GridBagConstraints constraintsNumOfTrials = new java.awt.GridBagConstraints();
			constraintsNumOfTrials.gridx = 3; constraintsNumOfTrials.gridy = 0;
			constraintsNumOfTrials.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelStoch().add(getNumOfTrials(), constraintsNumOfTrials);

			java.awt.GridBagConstraints constraintsJTextFieldNumOfTrials = new java.awt.GridBagConstraints();
			constraintsJTextFieldNumOfTrials.gridx = 4; constraintsJTextFieldNumOfTrials.gridy = 0;
			constraintsJTextFieldNumOfTrials.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldNumOfTrials.weightx = 1.0;
			constraintsJTextFieldNumOfTrials.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanelStoch().add(getJTextFieldNumOfTrials(), constraintsJTextFieldNumOfTrials);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelStoch;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCustomSeed() {
	if (ivjJTextFieldCustomSeed == null) {
		try {
			ivjJTextFieldCustomSeed = new javax.swing.JTextField();
			ivjJTextFieldCustomSeed.setName("JTextFieldCustomSeed");
			ivjJTextFieldCustomSeed.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCustomSeed;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldNumOfTrials() {
	if (ivjJTextFieldNumOfTrials == null) {
		try {
			ivjJTextFieldNumOfTrials = new javax.swing.JTextField();
			ivjJTextFieldNumOfTrials.setName("JTextFieldNumOfTrials");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldNumOfTrials;
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
 * Return the NumOfTrials property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNumOfTrials() {
	if (ivjNumOfTrials == null) {
		try {
			ivjNumOfTrials = new javax.swing.JLabel();
			ivjNumOfTrials.setName("NumOfTrials");
			ivjNumOfTrials.setText("Num. Of Trials");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNumOfTrials;
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
 * Return the RandomSeed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getRandomSeed() {
	if (ivjRandomSeed == null) {
		try {
			ivjRandomSeed = new javax.swing.JRadioButton();
			ivjRandomSeed.setName("RandomSeed");
			ivjRandomSeed.setSelected(true);
			ivjRandomSeed.setText("Random Seed");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRandomSeed;
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
	getCustomizedSeed().addActionListener(this);
	getRandomSeed().addActionListener(this);
	getJTextFieldCustomSeed().addFocusListener(this);
	getJTextFieldNumOfTrials().addFocusListener(this);
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
		setSize(607, 419);

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
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 4;
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

		java.awt.GridBagConstraints constraintsJPanelStoch = new java.awt.GridBagConstraints();
		constraintsJPanelStoch.gridx = 0; constraintsJPanelStoch.gridy = 3;
		constraintsJPanelStoch.gridwidth = 4;
		constraintsJPanelStoch.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelStoch.weightx = 1.0;
		constraintsJPanelStoch.weighty = 1.0;
		constraintsJPanelStoch.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelStoch(), constraintsJPanelStoch);
		initConnections();
		connEtoC9();
		connEtoC21();
		connEtoC22();
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
	if (evt.getSource() == this && (evt.getPropertyName().equals("solverTaskDescription"))) 
		connEtoC25(evt);
	// user code begin {2}
	// user code end
}


private void updateStochOptions(){
	if(!getJPanelStoch().isEnabled()){
		return;
	}
	try{
	StochSimOptions sso = new StochSimOptions();
	sso.setUseCustomSeed(getCustomizedSeed().isSelected());
	sso.setNumOfTrials(Integer.parseInt(getJTextFieldNumOfTrials().getText()));
	sso.setCustomSeed(Integer.parseInt(getJTextFieldCustomSeed().getText()));
	System.out.println(getSolverTaskDescription().toString());
	getSolverTaskDescription().setStochOpt(sso);
	}catch(Exception e){
		PopupGenerator.showErrorDialog("Error setting stochastic options\n"+e.getMessage());
	}
}
/**
 * Comment
 */
private void randomSeed_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getJTextFieldCustomSeed().setEnabled(false);
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
private void solverTaskDescriptionAdvancedPanel_Initialize() {
	getButtonGroupSeed().add(getRandomSeed());
	getButtonGroupSeed().add(getCustomizedSeed());
	cbit.util.BeanUtils.enableComponents(getJPanelStoch(),false);
}


/**
 * Comment
 */
private void solverTaskDescriptionAdvancedPanel_SolverTaskDescription(cbit.vcell.solver.SolverTaskDescription arg1) {
	if(getSolverTaskDescription() != null && getSolverTaskDescription().getSolverDescription() != null){
		boolean isStoch = getSolverTaskDescription().getSolverDescription().isSTOCHSolver();
		if(isStoch)
		{
			cbit.util.BeanUtils.enableComponents(getJPanelStoch(),true);
			StochSimOptions sso = arg1.getStochOpt();
			getJTextFieldNumOfTrials().setText(sso.getNumOfTrials()+"");
			getJTextFieldCustomSeed().setEnabled(true);
			getJTextFieldCustomSeed().setText(sso.getCustomSeed()+"");
			if(sso.isUseCustomSeed()){
				getCustomizedSeed().doClick();
			}else{
				getRandomSeed().doClick();
				getJTextFieldCustomSeed().setEnabled(false);
			}
			
		}
		else
		{
			cbit.util.BeanUtils.enableComponents(getJPanelStoch(),false);
		}
	}
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
	D0CB838494G88G88GCD01B8B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8DDCD45715B80DA62109C9B0A109B1B4B106A4A4217BB7896D3ADDF7CBB734EBD336CBDB33352D8D36E13766DF5AA5A9C9EDEA378FC4C5C1C5C5C19D15C4B4D089C13E644B080A8A0A02020A06C4C7E60051E1E61CF94088E8765C0FF35F1BB7EFBEA4EB7F7B7B6717631BF76EB9771CFB4E39775DEF245CED5316242567CAD25A92097F37A7CD12C2E6CA12A132AF85F16EB7DF4B12D47F3D8730C6
	7AECF786BCC3A12D6059442C9C29DF4A047788F8AF0CCC4C4A035F6B2481E96CC57861C3CEA724ADBA7F12453F1CB48B4AD9C65F5FF4E543F39540B44011E7B2BCF32465F2BE4747F1BCC2DA3AA44DFB0B50944A860EAB0377E600A9G51EFB17AB8F8AE2712EF2F2B61F45D13232454D78652D712FCD4F9A2947E0E71B673E70174F6DA4A6F082C63F2B66789017719G24700B25D7CB8F4033217DCA5F47BD17ACF269D00F59EAADF5592DC3E6E7295DE4AE6D9B345455F55077CE236BCA1359556334B8E40B5D
	365FB4E4347518CD6DC61B59AAC956D4A267C1B91366ABC915703E1062522A086C9970FBB640FC01F76EA1780637DF4B6A84303E0D655D7BC183AFCF57685FCA052B9FDF3E5AC7F9A73E4D4A997D36D25E1C172216EE243E59FF1978E08124E53F83F1815084E0821888301946C43365AF705CFFC9BEE89F9800B2B99C872CA667F1A83995FE5755C18A471DB277581DA6C9E2FE6F10CBF47471870C4FF853FC4CA6D195988351277EC61A77526474E5BAB61997B5149E718E33C54CBB0ACD186C29BEE54FF947B3
	C6BDE51F15543267CB9169CBF4E4AF7A4D23698BF8BE0BD4329BFECFFCBDC8E340G3F899F29D88CBF0162C3D4F8962F4BA81E3CCF03340C05989BDD2BD017560C69D24E6E7BF56DB0651F6ECC6FDF48F488D92855E52816649D3D1049EC933ABCCD711DFFD070CC97B70DD3723E88525C0BDE4BE271F554CA6AEF6AFF814D1FDF4BAA82E8843888B0869076E70C3149160F020C31F623D3BEB0E8311A4CCE09719E3D3C811EE6376CB43658DCB229F0E70CCE0B7112556C12B8CD47E5E203CB9EFCDDB476FBC046
	91334D6CB4128A8B399B874C325969E2B4464BEB880D53CCD3BBAD83E690B06010B0DF4B65F570349ADD72E90749A81B958A16FE75B2714910718C64889940EFE6176181DA5F217C3381668ABBA4537C0E191D50DE5455355A6DD7869D1D24A4D211C45A6C97E85B118C7499704E72F259884E8A6F9381D8BE5B2F2CD56553E469A16A9B1D57300E0DD6651298G7A9800F9G0BGF25392F98C7D368BE328E965D72512D743F4E348715BC7534B000736277C295425AFD399C4CF872405814CGC8GE069AB2B3A
	7C5469982E935191F139E9093AF55023FEF3FAA6E327DF99CBF87DDD2C42E585489F73DEA5FC798BBB71E58A68161D1E18D5120EBE786CCA810B6983C6179923384D8981E8B6A99EE1FA0E55C6F0BDFAG72F85E87816CG1F92DF828C838C82BC8EF0D2A22D0EA49D81388AF0B72FFF7B08CC008EG72217E846082406DGFB816AC9DD812885F889GC2C26A83B88D309E608A40A10089GADGD0052463421E51D44FA6A82395A09CE096C092C07662442CCA00EE0009G51GF3G968194E6A4E6358248G91
	GF1G49G39CB92339AGCCG13GE2G6681E4AC85BB82F4820C81C482448324829816A5E6B581D8814281E681A481E4E7027CCC0C2D9BFB9C694CC7EA5F7A0275AA1B5F4CF7C78B355C3EFBD5FEB8486DF9045B739837E79B77E187377DD91E47A75C5F9778FB8F77D79F7767950E37F33F3BF8CC8CF3DFEAF51A40FD0F6FF787D916470324FB698B5A6C7FA7E031372566E6E26FA067DB7E85E513582B8228G58817001445AB292F7DBA496F764DBC662EE3B44E23DD8E2B13A196771A1G69C7FE445FC97B
	38C7E2313CCBE2F1CC7024EF3DCDE2F1CC6A8E79E66D555109447BEE557BFBC116A5AFC83A7A7F85BE7F5F826C1B53E9A57EB38F19EDF21359E1361960070B27FDE2A5B1467BC93DF641E324F887742B8EC2C746A570FDD692AFC6D9F6DAAE8D4A668E33ACDBECFDCA3A1D4AF28DDEF2A8DDB214B5E4A531E39F06FE15209F3512BEC90F5DE6B3530F1FE0616957E91F48ECB58F0052876D03B61961A78C1071649573B55EA73BF3E029C47AD456C1523FE3E33499C6926B8E27E5GBE29240BA8C5E7127E52ECB1
	46A4508DB8B2568C01E7F8A676F50637E3DF27F064D9A97642955D3E7B14276ECACF4DE4FD0B4C65CADF07696436696BFE03764DB85E45F4FD2DB4871EEDCD07BCC6070BD9BEF43C5BE9F75A5AFAFBF507140C4ED5CF6A9CA300AE73A57B813B1B7616A5167E4699228F38CD365B0EB86D030E5D8CFF3A00786C00026FB01BCD3C9F3369AA61A90232B5GF4AE47FE4C3BB6120EFDC95E01B9E837DA199BA1ADFCB931F5826D6F913BD861FD62F2E66BD5A2FD389A536741FB2AAA3DEC85C95F34A7D7E56F0C955A
	7443BE53191E3F32933B3A4CD7874DE0D552E9BEA6E2663776351A34B3BCFED87ABBF692BFD6D5FFBB0E4ADCDC07738FEEAEAFEC856B4F4640F3AB601B33885D3F883A1CAC16DE14457A3CB5D9ACD6D8FE0F56A3DDB8270B06E7ACC0D2964B0F51DDE9C03A051CAEA79B7AC4G6D59EA3AC34DC8E74DE672A642B39CA00653352EA4F42BDB10EEF6B64BEFFEB6AB0F34126947683EA768262CE4F4B3D6FA523172CEB80AF431AB193C39AB15FC89BEE695216BBA06F4392BD81F31E4954B2FF19523ABDACD685E95
	F452EA560F4CDF2D64D7AA720BEBC5BAF9B56BEBE2FE13D62B736BBD0EF4A92BD9BFB4B30769191F4372E3F455A7447CD28E6B37B667B039DD1C0E59A5238D697AF9FAC88E0F079C357DE6893AD91CEE0EAE1D3B9D69D2B9DD769AC6D732C6CDD7F99269BA57B0BA2BAEDD32209B6369617C996DC17774A9241B8378E4006C350C2EE92D1A2E3F8369C2GBF85E0A627CB75205B5509F48D6B204DD8670BAED15049C0B389A0FA1D9E5D7453C837G52255C442C02DCBD7DCCE7102E8B529DBE69724FA2DD9824C7
	79240B97F433F3F95C656A79AD34CB4469FAC6173F1E51D52ED753BDA16CB7466942576B792DDB58E586271B2D490F45E92E304B5C75ECBC14224AD7095357841D34016B37C1AF3F2942FEB59B58382AFD03E7FE2CFDB1893BDC6469EEF8068144ECD037AF5542AE711CAE094BDD34C1DD5E94C1D7194752DB7953142726FB6E1C309FCF8F674FE88FBA53F991AF001772413F790C6EE23E1A2E6893240BA669AA3AA48F3AF9022EE4A30F937E6C5F486858B77CF74524DFC16704AF9B070C6ED257B0740D6A6A0E
	52EFB91B67AD9F4C6474F301174D67FE66A65F581CCD70FDGE85C04FD109F2C44795C70ABD1522C34C7FD4EDFF7EDE2FA0CEDD27AA0565F75AEE65FF01313481C85E9711B707BFD55211ED35A6FF259FBACF4BA0BFF7B8A2E2A7BA88C37636A8AF876DA1CAE3985BA11EE7CCE6E3ACA74A573DD9C4D677FFECF67A2C99FE3BE484D5F8C71341936DF55246FC63E192C1CBD95D84E301127240B8FBE181E65231C611B79F7E433D24E08677B96B3F90FD5233CE4C8AFB4203C1FD1F9645B477C742752AD3AFED275
	00A41C433DFE5843872B5DBEF56BB6F09F98945D663C98351469D6D2073A4D0274D4G5677BADACD46CAAE3DCE9D27DFA77DED9D51DF6E373818CEEF6DEF44FA75B75325194B6ECC571B676FFF66017412ADCC174EAD0ACEB1B1BFCF1F53B5EBB9536B9D2A9731CBA4504C5B02FAFDDA0FB2597B6D751EBAFCF52BDA07CCE3082E8E135E38141E3B1549EE582A6860183EA3F3A2G53A12E9EEDB391E8E6EDC59DF6B5109814AD83668E596CE0FD777EFCF55FBD95E849DCE25936442C26ED4857694147E404B720
	0CEE208B9734FFECA079181DCE3B3353EEB5BBC95FDB626304B50755326237B1D9598550CE94A07F4E3DFA7CCC262D1948A463789A20F7881ED71B51162C8F7B1B8D6ABEEC9450A5G248A7AB72B09FF86E8CF9E732EE9C13FE53E87ED46FBC87BF84B9A91CF1E3144E2FFB80F0C5B87693CB49BA91CB08E181BACAE0755F82D2855FBAD2BC31BD5FD7199DEB7262FD347C0436767F4E3A0F4F5CF3A7BBD667BE96FA9B1B0517AD86694GD61ED4D11EE420A9FC9F4B7349D13495230BBE0AF45DC093AE68569DA3
	73939659BCD0CA7E691021FC877B0D36BE332908D472D26330DCCA074025AD10FE087CAA66367C52B14CB39E724B580E6DC46E4764C9471442FE7BAAFAA5BE77EF5699E979362026F25797372A5A1E52A6F32FF150AAFB64D50FED509F37201D8B1BA2251A54DEDDBB53BA361D595734DD31F3B254F5699C56776D2DD88FE200A6F9BB5A6F2ED6E2E72333EFB713B9F68EE5064447C8796BC2FD4AACAC645F4AC2C5E6A834B191GCC66EFDA513E91C0B327906DDB340B4CF17619E52F98BB6C348F1038AC22C6AA
	35029FCBBBE4A7D88A6C96CC0C0AFAF25FF1ACEF768EE8A3F6E0F95FB90E65E56FDBCF90BA17190CDF4DA692A20C7F4289649F835E98417F1D931E7C3FBD6199279F3609F8861E421D48975E06F44C86134A8BC87BE1E323CF36C05549DB2EB6DA96D7D1719916673F0BBC3BA13FF011E7EB3B2FBA4F781EE897718DBC999FA0DF66C97FED45945BA621DB5B206C981419C634BB83DAE17E1F1E447C9BA0EFD9649FF75253A66BCFA1DDB450A4893AB10A3758AC3245E8350C180B0A197C97CE137E0BEA3E62C20B
	0A660CA85F3F0AFC8DC5B05EGE06D6A4EDAF53BBA8970B1GF30BD0EEF587D9436E35778C3ACA693F2DF6172CEDAD8E13845EDCF03B18161751D811CF180755F661512A47DB0FBE1C5CA1628274A8ACC6DD0B362AF56D86FCA8C0F4B16A3A30E3BC6D9B537739776BC82C520999353E2C3FC33372D2DDE4D22F78393393F50F853D968A5D37566A69DE7697980FD03E4C5348578438313FA0DF5FE6BD3E183FE0196D27475F26FFCF481C8D79C93BD06676FAEDCC54C0DA77AE14D9FE66E6E302493B678C4AF3C0
	DED1C2DE02C1CF5E7CDDE81B7DE711AF95F0A5A5487723CAAD1F8952C2CB106FED41978138B9A528FFF7177F366005AE644B7E905A438F11EF280348315368206D0F435C03713E7CF49D0BF7F5709C36182DA6175E772E4DA393D57C3CA2E2FF8C644688593BBB75E4B31E5BC54CA583FDE1A972A4F7FA36ADFBCF0B7E9D50048B3A485358CE737D9142CF71C013719152594E78375B4BE7B07F866011855FDF4EFA66FF4FD9240B861AA4C117FA160C1DC81F7400FD50E6F2E19F335B23BFEBA8E3FD4CAEF8DA4B
	10F7EB171617FF1B04AFA7826DACC17F9DAFFA6647E72E1358167D4CA3DF6894CC2A76FD63B99123A023E4B74A89B9275FEEDFBBC146C88346AB101555C4B2E2797CC864E302BCA2C4BEF5677573F9392DCC72B1B67218A5777CACF0BF1CC7D9F3C0CEF6B9562BB72A34EDCE932475178BBF1DAF97EDE5A9FB341B1D3DF66700F723331FA6FBFE9B9EBEC5563C20F9106D83200F09CC25DF582F49C75B582AB22AD7FD57CEFEA26A8B6897A34A7055BC3DB2A417E33F6A6BE7483A9F8CC5CC7601AFA2FFC9B74A17
	AA205D2AC09BD93B7D0DD9F95F3956941C6FF46370A63A51E7587A6A1EC16D9EADC70532AFE4226A3768AB533DCB3F24F3AFD3A01D74EDD78B7CDB949F2B42B31B18A81E3CAF00B4D3A56E377951A029477361FD91C0EE95549300D6GEB954E393CF4152C437A5D4F54E2134DCE1B515A613100067B0A1C447ED74C57787B00D3BDBEE1F678D39531C3E8D560BDB1880CAF2B1A70CD3B89BEB6CEA8BE2F5EE3193166D0BA51D1EF1C1040731EDF250C93186DA68E13F8AE28CE4CEAG68843082CC2AC63B3D65AC
	89E4B76CBAF30331B2C5D60F4F96AF5E34ADD8B97E2F1B14A39A741E8DB09FE0A1C07E9EACC703AB207F8F39F57D4F74AA2F8B348732E60F274E0C2FB1A0DF37060F1527F1044CFB4A1096869085908730GA023A6B12B28864BB549F5A0D039766302F127DD7CBC28DD74C5FD97F9695BD863291FFEB9D754EB79CA35652C51AB6757C6E9B9A1AD8CA08AA08EE081C0C6AD1433964B791A2BBE207FC89B8AE5244572AEA0537395AFBD0373635C00FC4D351EFC2CAD2EADA4FBB8826DD546D1A75F164C77A5CF4B
	47FD4997EB45FC75A15AE6427B8C005935383FEAEA43784A959510EF3EEE39F66C2D8822DC07BC6666D869D859DA8EE359960A32D5ECA7E5AB2BCB4CEA8638D807E5131B7D696886BAD23608BAC5CF46F71BDFBE8231F5FAE56B33EE16F45792F00A08779FBF3A265D4BC07A0FF3A14FE4GD64E42BCACE7C6BD16676316715531D6AF3E2D1A7706FA3DBAD6F21D6EE100B487C0A8C0A440EC00A4004C862CE39F49FB032CE3878157EC37DF51E9C6382E6F0E330C19C78335A3C58DFAFCAFFA7185A7AFB6203CC6
	DDF9F79E9B1F3C873D7834723A9A74FC98FD03785081E913GA281E28112G243DB0B65B0BBE3CCD5E9F4807E2CBF5B0FEACEE9DDF3B32A7A0DF53DE5DD815C8B93BA14D8D9086908D10G108C1073B116735F64005FBDD5B983FF77FE6725EFF07ECCBB9EB0DEBF560D83AF3E606C7AE4C03EC6DDF966936313678A485725AB2F22EDFC765C6F45273527C3D75E2F47A96F8F8165056A4AFB26FDFC723E66452715B7CDD7DE4F4971493B6A452715B7D3D75E66D36313D76C4527151720AB6F55F14AFB5D0BCF7B
	CDDC2011477BAFC5241D517946F71A5DB21DDFE35F78D5866DB71E4C13CA7BE01C398F3F711F56E6F2FDCA7661B77E610E711567A9AFBEEDF9CC7B744A336140D67D721CB21AACF6B65B0C73DE7F31DD2F5FB286790609B2DD580AE50A92E5BA5FB93EB275FB71E94BB4DFD3A6264369DA5C2F31D068306A7478F4B0F871E9F5A8EB545361314D28C3F3A36A7003F16A1098D007900D8E4CDF76DA3D981C8234510D682F7375C4CFB2AF90A774FC604C7874FC420BCF2B67A2DDDBAD5B02BA646EC79D0E1C9D1F8E
	273D7834BA346E573355BFD668594A84346E7DE82B95D558A78F93FA7EF11CFAAE6F8A545E4D5E2F477737DEFC41497B36971FD7DD521517B04E7668B5AFBEED79966A4A3BFB1CED45A3DEFCDAF9B98774788EFA55ADAD684BBB99102FC8D75E4AF1562D4DDEFC5A72B56A4A7B2E97DFF0657BF1C03EAEDDF98FF855076064BD91104F2111473EDFEE3A469A4CBC89DBA2D14FA5040BF9125083B8177035BCBA078C6FF381D28EE0BDB70F335E398256734C03FAFC5B4E0DCF5E07DEFCDAF9853A72FEB2CEF93F8A
	A82FC6D75E836747790D71625336E36D9AF92CFD3FD12957F769875A3103583E97D463B7BB7CA07A7D6CA76353536C452755B3DE23A753A1A98F7BCFC9C2072C6E716930510BCF2B032149130F1DCDBEC1773BD6C2DAB3G436D9A26E71361FDE2936A3540CD7A42D170BE8BE0AEC0AAC06EA16081E8BC04F48BA99D599FC7769B13B392647C83793EF69DE26BE00C6E325B2152E516CAB7121736BD185B640D7385291CB6CA4503FA72B34E9E321728FE2F5649EFD910F28960F9C06D59E432AF184DC94C9B74FD
	566B75C17D33DEEF8C6A1F757A4F41B58C8FF928CF58E37A6F877D1F857B73205ED930ADD47FFFCBA7ED23F350C6162929D856861B5D64BC607ECB7229C19B1BD6A112DD5AF59E8967EAAE271175F777A11C13F9F028DC721E13B9E43248F6A7E652E9B6BA5BFAFB45048C5BEF4057B206FD5B6F01E1FD7BBDB22CEF3F47077D5B6F19E17F76FB4B6DDB17BF3875F57933DBDF17A5577C6B32721AAFDD183FFEA513B5DF2FF3FAE4131E047B14ADF54BB9CD3BECEF5FAD72BEBA623B9CA7C7744BF1F60444344BBE
	686CB1EBF57DECA4DF24A913E9E48B90D1071FD3247C325159E776AA2B0D7233B4DF7C43A324DEDD3218ACCEFE220D59613B9BD6D3BB3ADCE636832849A89BF979AB6AD770729F126D47F7D7203CEF0E7AAE7FF7C6754B9FBF6A3B7CBF9A8DDC7EF9233E4B7F7AE86072A70F7A0A87D6563486352FABA4B1F7106F2F6F952E53763275769F52B52528C3EC3DBDCD60FFC671B1AABCDF0727F8729E8BE90507F1EF428FBC742A94FA1DB86CCF2F50433E74FA062E87C59C566AB50362238EEB752A5D0EFA45C0DA7E
	1144AC96B7851F67891F5138E903CE2553E2B2539A44E8DE3765893FF8533072BD5F2CAE9FB9074D3E75FD5770DBBFF6847B1FBF5ACCED876F71GC9C7707BD41C674F960B0EF85B02499EECC159905F872BC4BCD5DF779D5FFB2F6B4777416B3E637B687540715DF15DF7FC1F3F9EB83EFB2E7B0F6F5DC755E52D9678B75478638A7E9F0FE521DFFA1D76010365DC172F5F70ED1BFF3821EF1B17EF78364DABB7825B66C7B7FC5B6627B7825B66759B7EEDF33155232C6DB59887121FB810FC440164A78E24A062
	C0729387D290F1A0E95BF93E2F6F3827BFABB9BE5E938FB51061A3CEF8624BB97EF21BBA0EF68B9B3E5F2E46178B7CEB9E78BD82BF75249ADFA37027CE297135821F5D31C2AD378C5B09438652CE90DDF31A311D78D68DE9A71A617DA2003B995B09CB357E5A09B0200BECD65A0A194D5886FF3FC32DD305E82336966218A8DE487E1C1E914BE8C94CB2GD43620EC53D6A23B354533AD3A485F6596DFED5464CE35EC257DBF2FDB16D01ECF044F7C8E1D7630E305D8835B2C175F8C1ECF1C4F7CB24FF864D70936
	F9C64C5F4FEDC15BDCB3905B649C053185C063D1344D855DFDA0DDC719CCABBCC30EAA72430F22DFDE7114DD05E5495E2217DF944FE396BC93D479A5097CEE1C7548EF8F16651D8AB1E7FE944B1235016E2BB88663C0000B4730AC2B2B7C6E2BB8664B0E65DD9E32F7A37E371E78F244AF6B7440D7A37E1FBC71B5089FB96DD91FF7F3FC13278DEB11DEBA6BC9DF41712F1CD5FF2F4BC5BCBE62371FB4C9275C2C5FD11CCF6CB86D1836BFD2CE715147347D113F4F437E48CCC8B334226FA2BC6CD4DE0665B8F34E
	83AF6C1AF35E83AF6C7A0AA73E82E3606B159883A12D9883A9B486E642FB824002D60C019D55FA36100EB39B649E77958377FE62A13B92ED7B866D5F94E9789A78FB3B4F7C8EF4FB64D705F873A039DB44EA3CE69F14A5A961B81943989B44B9A0933C0F81C4824CBC4E6602787915E192933DCE63GFCBE69A0B37A8461BDB00C3C7970DE82508AE08518F4827D933A1B4824B30C425623341F251DFFE4E9DFD93B12272977F03134BD4B49D9DF3DFDD0ACBD69BA319BBB250CF11ECA6FCEB95AEA3CE436F6DAE4
	AB76BF43CFE6895A8E595E534F6F616870B82F10FA0259A13F8D476536B4221F68D370F1A31B8F1A150F73C1A5ED9893F34935C25C1F3FD07D3EFC485F5DEDDF94180C8937D486AB3379B016F9CC14792B5B15F2FEDD75FB5F76DBDF662E3F028C6BB52C6771ED2C8E4EEFD36AE2EA1B2FB66F887D8EE6363336ED09401F22F8038A4F5A3C1FEC46FD6BE510965E0E730E0DF9E873056DE873E386542979E4E2D6B70063242247C478BD6524269D21322B58DD18A7196CE5823F1F6263D4F82653BF53BE96F91F87
	E90D27D027EB3528D324404550755ED4F84F6EC0DC8F7D269640FBE5876A3E3DD05BEEB6F7A83AF6F7783267E2FACE5E51215D733E0E6227F4E8773C5FCE7BE864BD96529ABAD127D13ABEBF935E6385AE0E626643FB0A4095EF43B2AE92382B35C857FA9AF1BF9436689238EFD0DBC4417BCC015BECC03A3853E80BE8513F4BBD434ADDF4C6A9FF4399DF36F81346D07B99D6661582AFEDA1F8D905E736781C521377A91016F196F59A22EB5F8EF88F91381F55231E1384EE142ECF4F0377D4011BDAC7F019DD70
	FD6EC25CF6CAD7866F0D82973A8573EB953851DA4CAFC26026EFA43899709E57A55616AB3431B237CB31C9F217AF7BFCCE3F5952B9ED1C5FCD71796734F13EE68346F99924051FC31DF6D6093AA7F0B3C43B5FFC9E757C08FC9AF9FBD1237A7D2D3CDB5FBE7D742FA0636FAB35FE18F2DE31FD74F9DFFEB031354473091AFB001D94BFDF056777BAD0BF500B38BFC94CB2FD02B6EF206BD23361FD2E40FD259A7D10A4F09275CDE5B778269BF1AD79485BADF0FFA3FC585F0DE793A387C51F13603A31BF347B2247
	FEA7F2G292E6E0853E2BAE06CEB253DAB56F7CC26EBC6043F68D36CBBEE9AA2F9DE04F7B7C05827F8F708FBA75E4F917D74F4E9461F266A5E11B23F531D9E75A933EDBCFF1273A705AFC5ADE5B27B07D1E666E7D067BEA3E5FBE8D85D27A17D1FA30B591AA4CB1F6D110E77A8AA696FFA2493FD3F1921CEAFF12B53090DBE647C2CEC5F5B01E5DB743D991275EB1F7B3C7FA574B3D626E87E0CFDBABCADB2A3EFA93F172657473DB434F7474AFFD7A56E1BC902BCCC976899547CA61E3F6102F7DCAE11F05D1CC1
	9950B4812C676FDD971431884BEF6CB066972D135FB2CDFE31C0B3C7155F82D5FEAC76A650B94FD440E7DE44F81CB0C2F086F8AF933874B10C514E0B584E9C275F10C8F80F9538AE0A1BF7115D894677CBED8E36CDA0F95DCCFB2048DD98340CE06E8E557A08597EEC9E5AFE110E1EF8E68BED1FE3849EA36B3B13BC8A0DDA5BDF2236AA83FC13916D9CCDF1DD70AE8B1C11FE1FC261BDC260FED6077E0895389F0BBECF4EA534CBBD6DCB9542FBA340C501BFC2719361BD72923B2F0C617F25C6EDC764791FE83B
	8B056C7F701031C43A39755BC03EE53E8A2BC5DF4569682F7555DCE203CB2C1E903C5423780A4F09EC2667F1ADB64B4060C007E544ACB57590B9BF27B64F065F79BDB86E9C29A0656931DB8787ECAECEB71976BDC97E21BD682F3AC2746B94017B3B9D6857D9BDE82F6555244D4FB6A5E695GB481748304814CGC8B0A15DC6CA17E2E2774AF5727B6048D97998FE761D6F692A4235F5129FF9B671E736C931D53F495BEEB3F4F08889FE5242040E4F895938260F3ED757CB3DBBFBD77A713B5E1EG04AC61437B
	E93F9A6FA7E1FEF9F6AB6EB94AB5237FFE36151E0BB73B65C240C9520B040447D563B60C2BFE33F7F955B1C5A01947D59ECF9BB3ABF14572FEF53912779BA2E6C3FD643D44CF5E91E6ED4C56EF72AC4742950A2CF2834A0A72A1EB19CA56AC20A10032924C5A366C65DA7C0E4CB3E3ACE752B6AA855EB3FA91F727684F9775E2CC7C1E6E4DF643FBB8C08CC082C08AC0E69F52AC5DC2E88CFD6C6E4206BEF6B7E1B7BC47844D6D060BFE6200G66CD72D56309EC75FBCA2FFEAC078B1D278514350C4731B6EE355F
	9FDF7762232F57C97754FB7A118E4D399F5F2AC7BFC67485EE67E6GCDDC9FEB67687DF9FD5A1839FF0BFEBB779C9DB7A581FDCA9F5613976A3C5B39595467244C06FEF1B74196CF7D0F2DD474FFFC8B6ADF526F2D7FF20D7E0DC053592F685F5F2F684F64C752B814819FA2647F5DC6EFBD93B6221E517D981BEBE97FED963CA7885CA4B166CA6D97F1C64785CD9698E38338AD081FCC7113AC6CBE4CE8014F5B70C546C4C48611C56354225F67DBA315FDA19998EB6A76D88D4CD7FB2B50D771BAFAE8E3ED3E05
	3599682BD40B36AF7B4F22EFACDD8E5C5E6582CD51E525AF5BF0D95B867D107A2F99705D1751FFA74538AC64325857C66FFEC8007785G99D792338A819A2EE03A1B26133BAC493D1797819F82B0CD24AF5235B549A3C1279C010048DF4079949D566B0E2335FFD3A3D840F18136B7FC8DBA5F33DEC6D2D7B05F44D4B77949CF6B6BD8501BG761F66DC5136ABF9795E5F5ED2315FF7BE718155611FD7ABBAFCBB5F1F8EE981F4C8D169407B9EDE77DB84E337B5524CF1723DF953FCECBEA2190E5397DD512ECF3D
	CD7119D66D7AD441904E9564C21A5B0A77E9BC17C33E197360FDA1C04EG548900E6G53G2EA93C13C66E0771FB7E32536EE8B58F192D2AADEF2CFC8343B7DBBEB6A67F56B29C1347FC63B9E9669D7BFC5E876A9EE075E28AFFF6BD951EC61E2CFCFB5712724D0634A400CC9B740F81AA81DAEDD83E0F53CBD8794876457D32EC6C692737310A4D1096733002A68BCC2CEC77F94CB1DD3471B5F37E8C654F881BD25F599CC046C6DC331B81E95B288E927DFBF06C3F14FDFAB8967B27C93343ED740BEC34116179
	A725269F6FCB5F6E43C68B388EB5F6E5CEA6E14FBB19CC1F2F9EC6FD9C761B55671B24AA615E09EBE4AD4D6EB0DEA59725307A7B42B51C6F09326337F66A0877BA5F8BA338D6B74B4E56796640F3011D5D51496A7A46B171ADF0786ED7E37B178BB4C58E55374021EDEFB6F6100604DC855367B44AF6A71D2661E9E14DA4CF4F1BA771AE7C3B8C5AB5B822E333837724076621BD3B9DFA7644FE7D428D0A6D2E993CED97E6C05B05BB50F6AF9BC8FF4461B4771A1DCE3309763E10FE9E250F825A98C1FF32505B56
	F709B3E6739D4C568B6119FD150F81D91F318EEDDDF8D57FBB2E7E6E56GCD6BD54556972FEA7BDCCB59DA82605DD7D1371ABD447EED8EFA994F10512A36FF619272B783F01D75344DA23BD8B2CF6FDFCA46992AAB5000CA9C6764FE9A29557A0914AF72AA5E6F9433D9AFFD66D5746337441944782B7AFE746D5B4D794A6F0FB7AAFE6E2AA07B79ED666182F1B7D49328EFA3FF202C283A2C24203C2C986B7734E1751DBFB5AB2E6B7EED8C5ABFF0B29F6573E7495C70B4A6AF2AA29D1EE4DF9273FDFFA17A3E5D
	49FC5FCF1ECEFEF60E7ABEE1877ABE5C29DF4F162B6AD994504CF2AA3ECFF0EA6B5948B6E55D9B77EBE452339D7CBAD666C65569D5C6336DG21815F136B34FC507CD7B5B16EA13743EA6931F8E7A4F4D83FFA2DD69EDF3CE7695BBFDE2B1565117E5470BAAFB90C001DD5E92F56BB0BB88FEC116244F80FE5F3864EC1B3736F3FFE2276EF4E543EF4341B566619AE3EC74325345FACC62FEE93E7D6DD58BFF2124F9A7F56EC2A937BC3C47A0386A53D470069B3DD7E5A2AC5E46BAC2F4B493558AE253830EC5B6A
	3C5B196ABA34C9064CE24DG4FB2194DB731D8FBCC0CCD1BE57DF6C68D5DC0631015D81BA8EBE36DAFE924CDB9E6B6BB764B476DAED95D263C7F51DA1EF688DA75EBCAF341536F6ADD25CE77E8EF6E5D344EA3DF0519253FB3C47AA76DF60B8D9BAA065FE52231CFD1153CDDEA5822EDF708BD42E4EC175E2E53CB0F9469FF5A2817BED324676B72474B582EEDEFC01F4F17EF36DD5B36CD79FD20C092F63F2F4ECF3BD63E9B5A3572E224CD2CA37E2A96B118308575499F3CD9FD4C6FA93FC35FD774F9F8233FF6
	369A7459AD747955C654073D7FFD35F7AC570B33E86D033C5DA44FC1B6E74AE279C9B1768D9F540FE575FCD994504C9AD4351B035AD87E35CD099D5C671A789E712D725DF309C0E4695FEED5F8AAF83C45ED50468351F95EA046CB2BEEFA4AA0460B2D8A7D13B1245FBFE476FF1E6E25E1BC8B73112741870F6FFC3EDF2878710DCAEFDFC8D5688BF90879E292BC278D31F3E94C977DE2ECBAF348775CA5FE436201E67E10620B54A12DAFEE2BA693232744DD0F08FF7CAC496F2047BD1418B6340958D539E502D1
	918A16EE5FC65A0C930383ED3D1DCE0B516A62FC5FBF345E03CFCD01FD50BFFB5CCDC96E2DA5E51706512736EA2DCFC9FA2EC86F5365AF9A76778DF8F3076217C37978ADE3E9399FC8920EE95F9AD2FE7FD175FB67502DDF775FF7CBE5707BA5F3FC6F3F7FC60E7E7E7B589C7D73E25F4971FFB66D7BB93E7F4E5B2BB96A3F7346463BC99F903FB6009F3BG46G42GA207F12C5B3306B4126C26C06DB87710290F6C7FA7FF45D7C67907577AAE6B7135017F766F1935245EDC32C3E7EC00640F786E3544DF325D
	C1117CEF612DA5B6F2DA7A7AE5C60AFF8BEFAD9DBB18FBA91A4D59BDCD6771E221DCF306F19E6F2B9B88EEA13CE73A45FC2D580B16ABF0791457846F935DE27DEC23FAACBF857051EE7F73F44C5E870AC9BB938B3473G24EB09D979GA5GCDGDDGD6009000A9GB3G62G6682A483AC82489901FE59887AE7EAAE793B10E70C173DCFD48212EF6220CEF29AAD44CDF8A71A47392B9A482FF5443F7E2C9E5F98A1B1548BCD8B31B75F57B702F3C597A10FA9A3789DF8EF947BFD89A338DF24ED144EB70DA6E6
	95G540CA26D51B1EC7F5BC7EF367DBFCA7E5CA57F0EFCCB74CFC3C751D777F81C2B0A857C1C51E04A7A0CC1DDD65ED7A6DBC1F0CFF6AD5149C71F21321CF49AEA705E7397EB51C68BC85947305C6F0A3EF157985A28087E6D17B0F80F820895345B6B31ECF347EE56C68F16CA228CF19FA9E5082C71D7065D15A58595B040E472DF90635B1C6B28532FB7637E14666BE86F6FF81C698881FC7875E06A4655B2BA9F8134F157B13EA7EC5C65A73E5B1DF60759A9DFE3D87AD782393D438A515EC9101761866ABB37
	906D5DF9836D3DF487DDE30177C800D902B6EDA716F74E0D1B3577AFF7ABB199AA6E9C487E9CE59AB652759EF86F8610BF4779557B48D1845E0EFDF7638EDFE5075677A8782B6D52E5F30F5CE96C1330DE8FEDD35BFE9264BB6DF37F3667F7D1F85C6D9A83BC3303626BB728796681CFCAGBE96F70B6B3C5B114F6A50E799A109D98DA16807276B50E7F2885AEF8E250D0677B800F90276DFC59DC98D39D91F7DE70562B31798E314FC8973AE91F91B842EF29362A2842ECD0C2167FC896BC41147DD2A5937C19B
	F0DBB076FD55638CD0A570B48760E3366C29C4DBF683FD78ED285B7315E84B785B5016BF21349993201EGD4CEC05AB9229F5FBA61E6ED3936CA31659FB7E0BE93C55E9384EE568434114B634CD1AA60B3A786E323BFF91C59B1GCFD9GBEE62322AA34D193500FCDC45D9CD5E8231809E82347A8EDB23CE75C8EF63A9DE99F91F53B72761B3551C835E223EAB17E310A3C1F92F9C78A5CE3624E58F937E39B197C016FBE576FBE885C67DA748149538CFD254EEB8EFE7EB22D084EAF77763A4C22EF3734A8C760
	DADC6DF617C5799B377FC047DD176CCEFA50130D3B6608B1F06E9D5047816882305E416E84E1FCF19BBC578B7FC00ED672353AE231B7EE629D7AE3B21CDBA4F9CD819A8238B69CFD07F6CC76D1A571C33F5DE999315BE423F53F5552E7A3BDDABECF55A26A464CBB306FD7C057DE96407B05B4DC935EC1F1521D09D9EFCA225FDA6C5B871F968776C1EF31FE3F57DA6C5D6F3DDA2C5F6F9DAED677FB99EE0C7E4D06BE6887F6893AAF895CB9013B0366C9F0E7ADA639DF60A7D13D883E59CC84A27EFE2A57B045F5
	F15C43AA5CB90E0BA8A6F181C58633A36F934544673D962B153DBFC5E9ACB688ABE10F67280E9607D125777F91B822B77345F78F23FF768B7FFC7F305ABFFC3F68487856DBFFB3CE3E1D2363DB27FD19760F8DF7EA57E963A93E68CE6DBA6D69D1EC43AAA1ED4A1D5816360D2965AF82FCCEE8B0ED69F78CFAFA97F25E9A1DBC184DA373508F4A5E09E773508F4AFA79CC3FE7177D68B5CE3EBF5FB49F33F7846DDF3406EA7D709445F705EA7DD0AA7AAEA6C81B9DAA3E7B9EE702CB3E1C18557865E07C70F5DD3D
	BBB9EF3FCE9E0CAFC51CFF9ED35104715F11701C2961E53E7AF92D377FFED92B769F7FDB71E504C74CBF30AEB77ECD78B2B217A07EDE8C7D33256C2F43B15D9E72BA97951C8FA3474977731B66E3BE9E21630178AFEBFDBF016267FED96B7B6C9DE22C81E94DF7097D05BB557223819FFBD7B03E3F476B5C6D9C955F821DBC18FEE1F4CC3168AE265FE2011FCA7159F7ABF826777B94CF5E8D1096F2B7FE736E59669D8B77EFD3478243D557F9535557F953FDEE70261BE870EE9BCC817F9E063E3D1C6364FB7E26
	7918BD1B695FC5CD3ADB9B9FA7A9BE65EEEDFC7CD4348D0BA06D62A40C0FB99EEDC39C6067CE8AA6BE5C3A7782A4F3DE699EDFED43EE5D7B89F2B9FD11CF3E5ACDFAFC8D1C3E5DA7DF272EFDCD1C5E2D4347E2E3DF03F73CB4B5F847D57296EF3AB737F84715456BECD7F071B1BCCE3E99B74D476267B89DE3CC3AC79BD7E7A8FE4ABD5A38CA94E33CC8C84B3C97636AA79E63A8AB60C36E8DA62EA45D7B0742B9EF14CE9E0CAFF4039E5FACCE1F204347FCF5D715377F6E2B7276F36EC66FBE5E460D5EFD3CD23F
	F7D678AEF743B87986EF1A0F79F3ADBD4F1BF42F564F1BA9BE65DE2D1FA345B8F59124DD3C8F7D7C1847F7A28E70F36F8B464F75FE6F38C806BC24B04FFC78DEC75D7BA0F2B9EDD1182F78F8526F3DD68DBAFC2C5C0BAA717B548EB421E1F8DEB1EB15384F1C5E874B7E1C4C00C33EF6000EBF8F180DB69CD7B7097B1CE343F0AC7F6EFA126FC2F84F198C5F3C49621EEC318F2BF1323FF5AD83394E87773BD61285E5751F8C6EA07F6232082B746021931D07CEC9387F4938134866FDA4BA54E2F803FB25024721
	965E6FBBAA095FBFD47D7EE474562F0F4937D4862B4B3DE35E6D4015B16FF66067344F60865F04CE463E4A81F176BAFCB2560B69F50A0D1ED77D2E2C3B7576BAFECBE5706FE33D4E77315E5BDE7FA24E72A789FB150AB3FEA98237CC1C8FEC3CDF1CE9973C1D8277063887602240BDCF677FE7407B6C7B517EA1BB941BFFD975FB6D0EDBEF7FDD37D486AB73161D5E76FFFF27377DA32B716C47BCE12F469A342174G5A6B210DE4BE24DD3CCF236FA671DE34F1BCE5A27985EF377D6312916C3981A663C43D928B
	E7D53F677F956A659F7E8A757257BA75B2C527DE5EC75B2C09601F7087B0AED2C5BBB6CD601AA9EF8A3CCB61E24DDA1CF54B9638F63A86518D6FD6012BAFC0BA3740DD21F51F5C59B1DB6062C5FBB0A79C632D23CA71CD376A773FD75EFABF7D57AD1541FD22536FFCDB275F19170776AAFC906D65967B834A846E718DC897A2F012201B74A05AB53FDA3165FB2A5F4FD55DFA3B3EFCCBE530B2FF2D5A5B2E5F2876366BA9F1B745BCE12F35142E64A17886BD0438EF09BBB1BA856E7DBD629BA4F05F90361E7190
	5ABABDE05FDCFAD82F0F1B9310CFFB3FBA63EB21FC060703EBF38930BE3F0D5D3F7730F6ACB0CC718D8FEB478252869C8BB4C3DA7443626EC10FBEF64E940859A9412C43269ABC57DF76517E92DBFFB9A74ED156686425BDAB05F79310CDFDF44DF00A76DC7225FAA56FC3623CC0D790F9A7EB4E52DBD5F933351BB256B7847C44A938EF797686C51EA4644D501137C4A3AFEE8A1307E5192B12476C76CF6C6C3E512DB9537AD35A9FCA827A94G368E76C331575B70C8E2D6A5C0B70083001C2B6777C05408F35A
	0F7836871E2108G1A990FA8EBDDF10FE8757BDE2F322FF3B75F8378832FFD40C4FE42A33896773466CC6D023D0A7D323622FEC9BA7AAD535ACFF5A6186097FA69F7C1F37EB8EB1FA2ABCE6C714E189AB8B662B4670F7327EAE575E94E2C17B6AA32CE9508F3ABBA3232B432EA0026F5AA0B8B3A5FE92AB646CF6ED772FEDD441CD5A7EFED4C61F9F834D908D75E744F9A723C6F93EB2313023011B66FE9DEF9BFFCD049BBCF649D6DA3EF75F99EED5E312A3CD9FE963FE7AE35F6081F2A1C3924FB4E3D7CD9DCCD
	624DECA38B28A6DE6F9A2BF16DB6F9AA567FEFB4A9E52A5D63CF87ED7CEA4FFDCA0FEA4B7403BCFF79E94F18E7BF6AD92682D5FE6C0E7158F5EC7CEF35584C64AFEF9EB15BA47CDEDE24FD139260E9FCD47C5DDBFA960C7D3E2E7A3D748B1D598E84FC2F50AD15416FED53D9CF9C75D8CF6477A309FDBA2D0F225FFFCE6F25E469C3E2AF4EC411AE9DA1696C7776E6C9445575DAFFE792EACA8AF6D796E33E3BC53E339E7537477DF00BA24BF2D411355C5F5E417232B2A60CFD735F2D46EFFEAA48AA0940F9300A
	F232E7CCF3EF57E12BFD3815EFFF673A9E5803F3E026881C833BE78F49B3825EE3G62A344D9449AACD7D2444D6EE719FCDC31FB66A65C97E8788A665D3095736E7C8A7A621BA7941E37457E2AC9A27D17EDCA7A0482CC0F7BCA303A216D171CD4724995F29689B9C5271474D8B18FD87998269F6BD0521BC53AD5245F56C952FD5B6629530A6F4FD6FB6FAB5DF956BFFFCB1742BFDC255EAF4152DD2AFD7377F92C8FC5020EB39F8BE6DEF854E33DA69EF8668745D76D41178ABC190F87355E30E7FC736E2B4649
	F744034F90C0C7822CAF1EC66F88ADFBDC5BC7DFC1718D0FEB7B688F6C91FDF4C80BFEDC6CC32E716823CF035819960C1DD66A2E3F55F05ED67E3C88CF197F66F3BADB55FC21109691143CF7BD5689E7GCFDCD0FC2185EA3E3940139C945FC68FF9529309D939CF844377ED0FB1CF9170B48445F75C032F9DF8CC81787899821D754667BD569B79DA394E3A7843BA6B6247F568CE6950FDC65BEF77936AB54DE564A8A80ED9D53F9F59F36B3F7FAF5ED299FC6E34C6E7BFD10D37ED8AB7615CCB5893B89F50A666
	52A2844ECEF131701EA4F0AFD35CC2F84F1F0E382E2D5EF2BF5D6AADF70D4E9A75861DB56A478B3C69A28B3C694AF4668E2BF4668E6769740BFE1167CDE7522133F85031EF49609E720DB60A1D1078BDF9E8181016CC276BB3DB54E71535407239E38B6904AAFBAB49E1B45CA10AE374A7C4DFCD1E0EE34E1FEE713FA7B3D570CC1B0EF70B4E7ED4FD7FE6412E233A77EFAE7CF4B0BDFEBA2B57A95315F35033763C1349E44F5CC23E37FE566EDA8F9D6E64654B5A9A8CAD5B437FB5B731D7611350DF87B0810C81
	0481C4BD09FBF6FFBADCA479BE5740BAF04A319D46730FFE7E6E58375D017718FE5F2D3F47F40E5BFB0F69CF5C7AFBCC935D6ABD26F994770B525F42GBB7AB75292089F4F7FD7ABA79D8B48C1C6D94AE2E7A2691B4BA92D965D5F965A1F19BFD4D1377875A79727CB99AD52D226EB0B14C18436C8CB28BEAD52E250157EAD3F8CB5382ECAB7E88BB5FE2CD3DA6B5D9D5FFF6FCFFE9CFD21A9B7DDCA87157A4DCC07A543E47116EAC1CF507D8FC8770DA57D89625F7581E848787240AD554437EC83D44167825040
	78AA6D31DB2509ACD59F6C375BDD66827627E18B0C56BEE8D7647E0182883902DE98D9FFA88F5A4C8564CF9B967458EDA65ABA396AFCE537C2396A85A2191D21AE404BC7E4D0435C630200A6199B6502813BCB869EF2643F40CC6E831025ECAA49E59CF0C0AD74FE65B952E35932D4DFC42ECBB296581D85AE07318788210E03F028B9364101CBE6272B2440DC5AD7DAD0D6DAD5827F1071E8E9B979E7CF31AC2DBAB1B8D0DA5056DB404E7D0E4B032405B484C1B37B7FC1047D753174E2CB7E5F8AB0B077D61B03
	868D5E39D27C397ACE0E0537FF66734E955333FC5F5B334A3708FDAFCE1713B6B58670894F627761295D6420212F33DF9D83C62B953E16D7765BCC649C5801EB32D9B983FC3E1C646FC29A1E484F4C16933DAF2BF87E9BD0CB87880E5EDDF5CDB5GG2838GGD0CB818294G94G88G88GCD01B8B60E5EDDF5CDB5GG2838GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG07B5GGGG
**end of data**/
}
}