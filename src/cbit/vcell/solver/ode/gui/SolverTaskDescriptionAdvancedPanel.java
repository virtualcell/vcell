/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

package cbit.vcell.solver.ode.gui;
import cbit.gui.DialogUtils;
import cbit.util.BeanUtils;
import cbit.vcell.solver.*;
import cbit.vcell.solver.stoch.StochHybridOptions;
import cbit.vcell.solver.stoch.StochSimOptions;
import cbit.vcell.client.PopupGenerator;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.beans.PropertyVetoException;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:25 PM)
 * @author: John Wagner
 */
public class SolverTaskDescriptionAdvancedPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
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
	private javax.swing.JButton ivjQuestionButton = null;
	private javax.swing.DefaultComboBoxModel fieldSolverComboBoxModel = null;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private Object ivjSolverComboBoxModel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjTimeStepUnitsLabel = null;
	private javax.swing.ButtonGroup ivjbuttonGroup1 = null;
	private javax.swing.JLabel ivjOpIntervalLabel = null;
	private javax.swing.JLabel ivjJLabel2 = null;
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
	private javax.swing.JRadioButton trajectoryRadioButton = null;
	private javax.swing.JRadioButton histogramRadioButton = null;
	private javax.swing.ButtonGroup buttonGroupTrials = null;
	private javax.swing.JLabel numOfTrialsLabel = null;
	private javax.swing.JTextField ivjJTextFieldNumOfTrials = null;
	private javax.swing.JRadioButton ivjCustomizedSeed = null;
	private javax.swing.JRadioButton ivjRandomSeed = null;
	private javax.swing.ButtonGroup ivjButtonGroupSeed = null;
	private javax.swing.JTextField ivjJTextFieldCustomSeed = null;
	
	private javax.swing.JPanel ivjJPanelStoch = null;
	
	private javax.swing.JLabel ivjEpsilonLabel = null;
	private javax.swing.JTextField ivjEpsilonTextField = null;
	private javax.swing.JLabel ivjLambdaLabel = null;
	private javax.swing.JTextField ivjLambdaTextField = null;
	private javax.swing.JLabel ivjMSRToleranceLabel = null;
	private javax.swing.JTextField ivjMSRToleranceTextField = null;
	private javax.swing.JLabel ivjSDEToleranceLabel = null;
	private javax.swing.JTextField ivjSDEToleranceTextField = null;
	
	private JPanel stopSpatiallyUniformPanel = null;
	private JCheckBox stopSpatiallyUniformCheckBox = null;
	
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
	}
	if (e.getSource() == getRandomSeed()) {
		connEtoC24(e);
	}
	if (e.getSource() == getTrajectoryButton()){
		trajectoryButton_ActionPerformed(e);
	}
	if (e.getSource() == getHistogramButton()){
		histogramButton_ActionPerformed(e);
	}
	if(e.getSource() == getQuestionButton())
	{
		displayHelpInfo();
	}
	if (e.getSource() == stopSpatiallyUniformCheckBox) {
		getSolverTaskDescription().setStopAtSpatiallyUniform(stopSpatiallyUniformCheckBox.isSelected());
		if (stopSpatiallyUniformCheckBox.isSelected()) {
			try {
				BeanUtils.enableComponents(getErrorTolerancePanel(), true);
				getSolverTaskDescription().setErrorTolerance(ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance());
			} catch (PropertyVetoException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			BeanUtils.enableComponents(getErrorTolerancePanel(), false);
		}
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
		this.enableVariableTimeStepOptions();
		if(getTornOffSolverTaskDescription()!= null && getTornOffSolverTaskDescription().getSolverDescription() != null)
		{
			if(getTornOffSolverTaskDescription().getSolverDescription().isSTOCHSolver())
			{
				updateStochOptionsDisplay();
			}
		}
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
		this.solverTaskDescriptionAdvancedPanel_SolverTaskDescription();
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
		getTornOffSolverTaskDescription().setSolverDescription(this.getSolverDescriptionFromDisplayLabel((String)getSolverComboBox().getSelectedItem()));
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
private javax.swing.DefaultComboBoxModel createSolverComboBoxModel(SolverTaskDescription newSolverTaskDescription) {
	if (fieldSolverComboBoxModel == null) {
		fieldSolverComboBoxModel = new javax.swing.DefaultComboBoxModel();
	}
	// remember cuurent solver so we can put it back as the selected one after creating the list
	// otherwise, iterating while adding elements will fire events that wil change it on the TornoffSolverTaskDescription...
	SolverDescription currentSolverDescription = null;
	if (newSolverTaskDescription != null && newSolverTaskDescription.getSolverDescription() != null) {
		currentSolverDescription = newSolverTaskDescription.getSolverDescription();
	}
	//
	fieldSolverComboBoxModel.removeAllElements();
	if(getSolverTaskDescription() != null) {
		SolverDescription[] solverDescriptions = new SolverDescription[0];
		if (getSolverTaskDescription().getSimulation().getIsSpatial()) {
			solverDescriptions = SolverDescription.getPDESolverDescriptions();
		} else if (getSolverTaskDescription().getSimulation().getMathDescription().isStoch()) {
			solverDescriptions = SolverDescription.getStochSolverDescriptions();
		} else {
			if (getSolverTaskDescription().getSimulation().getMathDescription().hasFastSystems()) { // ODE with FastSystem
				solverDescriptions = SolverDescription.getODEWithFastSystemSolverDescriptions();
			} else {
				solverDescriptions = SolverDescription.getODESolverDescriptions();
			}
		}
		for (int i = 0; i < solverDescriptions.length; i++) {
			fieldSolverComboBoxModel.addElement(solverDescriptions[i].getDisplayLabel());
		}
	}
	//
	if (currentSolverDescription != null) {
		fieldSolverComboBoxModel.setSelectedItem(currentSolverDescription.getDisplayLabel());
	}
	return (fieldSolverComboBoxModel);
}


/**
 * Comment
 */
private void customizedSeed_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getJTextFieldCustomSeed().setEnabled(true);
	if(getSolverTaskDescription() != null && getSolverTaskDescription().getStochOpt() != null)
	{
		getJTextFieldCustomSeed().setText(getSolverTaskDescription().getStochOpt().getCustomSeed()+"");
	}
	updateStochOptions();
}

private void trajectoryButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getJTextFieldNumOfTrials().setText("1");
	getJTextFieldNumOfTrials().setEnabled(false);
	updateStochOptions();
}

private void histogramButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getJTextFieldNumOfTrials().setEnabled(true);
	if(getSolverTaskDescription() != null && getSolverTaskDescription().getStochOpt() != null)
	{
		if(getSolverTaskDescription().getStochOpt().getNumOfTrials() >1)
		{
			getJTextFieldNumOfTrials().setText(getSolverTaskDescription().getStochOpt().getNumOfTrials()+"");
		}
		else
		{
			getJTextFieldNumOfTrials().setText("100");
		}
	}
	updateStochOptions();
}
/**
 * Comment
 */
private void enableOutputOptionPanel() {
	// enables the panel where the output interval is set if the solver is IDA
	// Otherwise, that panel is disabled. 

	SolverTaskDescription solverTaskDescription = getSolverTaskDescription();
	if (solverTaskDescription==null || solverTaskDescription.getSolverDescription()==null){
		// if solver is not IDA, if the output Time step radio button had been set, 
		// change the setting to the 'keep every' radio button and flush the contents of the output timestep text field. 
		// Also, disable its radiobutton and fields.		
		getDefaultOutputRadioButton().setEnabled(false);
		getUniformOutputRadioButton().setEnabled(false);	
		getExplicitOutputRadioButton().setEnabled(false);
		cbit.util.BeanUtils.enableComponents(getDefaultOutputPanel(), false);
		cbit.util.BeanUtils.enableComponents(getUniformOutputPanel(), false);
		cbit.util.BeanUtils.enableComponents(getExplicitOutputPanel(), false);
		return;
	}

	SolverDescription solverDesc = solverTaskDescription.getSolverDescription();
	
	if (solverDesc.supportsUniformExplicitOutput()) {
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
	}else if ((solverDesc.equals(SolverDescription.HybridEuler))
			||(solverDesc.equals(SolverDescription.HybridMilstein))
			||(solverDesc.equals(SolverDescription.HybridMilAdaptive))){
	
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
	}else if (solverDesc.equals(SolverDescription.StochGibson)){
		//amended July 9th, 2007 to enable uniformaOutputTimeSpec for gibson method
		getDefaultOutputRadioButton().setEnabled(true);
		getUniformOutputRadioButton().setEnabled(true);	
		getExplicitOutputRadioButton().setEnabled(false);
		cbit.util.BeanUtils.enableComponents(getDefaultOutputPanel(), true);
		cbit.util.BeanUtils.enableComponents(getUniformOutputPanel(), true);
		cbit.util.BeanUtils.enableComponents(getExplicitOutputPanel(), false);
		if (solverTaskDescription.getOutputTimeSpec().isDefault()){
			getKeepAtMostTextField().setText("");
			getKeepAtMostTextField().setEnabled(false);
			getKeepEveryTextField().setEnabled(true);
			getOutputTimeStepTextField().setEnabled(false);
			getOutputTimesTextField().setEnabled(false);
		}else if (solverTaskDescription.getOutputTimeSpec().isUniform()){
			getKeepAtMostTextField().setEnabled(false);
			getKeepEveryTextField().setEnabled(false);
			getOutputTimeStepTextField().setEnabled(true);
			getOutputTimesTextField().setEnabled(false);
		}
	}else {
		getDefaultOutputRadioButton().setEnabled(true);
		getUniformOutputRadioButton().setEnabled(false);	
		getExplicitOutputRadioButton().setEnabled(false);
		cbit.util.BeanUtils.enableComponents(getDefaultOutputPanel(), true);
		cbit.util.BeanUtils.enableComponents(getUniformOutputPanel(), false);
		cbit.util.BeanUtils.enableComponents(getExplicitOutputPanel(), false);
		if (solverDesc.equals(SolverDescription.FiniteVolume)
			|| solverDesc.equals(SolverDescription.FiniteVolumeStandalone)) {
			getKeepAtMostLabel().setEnabled(false);
			getPointsLabel().setEnabled(false);
			getKeepAtMostTextField().setEnabled(false);
		}
	}
	
	if (solverDesc.equals(SolverDescription.FiniteVolume)
			|| solverDesc.equals(SolverDescription.FiniteVolumeStandalone)) {
		stopSpatiallyUniformPanel.setVisible(true);
		stopSpatiallyUniformCheckBox.setSelected(solverTaskDescription.isStopAtSpatiallyUniform());
	} else {
		stopSpatiallyUniformPanel.setVisible(false);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2004 9:24:43 PM)
 */
private void enableVariableTimeStepOptions() {

	if (getSolverTaskDescription() == null || getSolverTaskDescription().getSolverDescription() == null) {
		return;
	}
	boolean bHasVariableTS = false;
	SolverDescription solverDescription = getSolverTaskDescription().getSolverDescription(); 
	bHasVariableTS = solverDescription.hasVariableTimestep();
	
	cbit.util.BeanUtils.enableComponents(getErrorTolerancePanel(),bHasVariableTS || getSolverTaskDescription().isStopAtSpatiallyUniform());
	//for gibson method, we even don't need default time step.
	getTimeStepPanel().enableComponents(bHasVariableTS);
	if(solverDescription.equals(SolverDescription.StochGibson))
	{
		getTimeStepPanel().disableTimeStep();
	}
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
	//Stoch hybrid options
	if (e.getSource() == getEpsilonTextField() && !e.isTemporary()) 
		updateStochOptions();
	if (e.getSource() == getLambdaTextField() && !e.isTemporary()) 
		updateStochOptions();
	if (e.getSource() == getMSRToleranceTextField() && !e.isTemporary()) 
		updateStochOptions();
	if (e.getSource() == getSDEToleranceTextField() && !e.isTemporary()) 
		updateStochOptions();
	
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
private javax.swing.ButtonGroup getButtonGroupTrials() {
	if (buttonGroupTrials == null) {
		try {
			buttonGroupTrials = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return buttonGroupTrials;
}

/**
 * Return the CustomizedSeed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getTrajectoryButton() {
	if (trajectoryRadioButton == null) {
		try {
			trajectoryRadioButton = new javax.swing.JRadioButton();
			trajectoryRadioButton.setName("Trajectory");
			trajectoryRadioButton.setText("Single Trajectory");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return trajectoryRadioButton;
}

/**
 * Return the CustomizedSeed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getHistogramButton() {
	if (histogramRadioButton == null) {
		try {
			histogramRadioButton = new javax.swing.JRadioButton();
			histogramRadioButton.setName("Histogram");
			histogramRadioButton.setText("Histogram (last time point only)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return histogramRadioButton;
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
			ivjJLabel3.setText("(Comma or space separated numbers, e.g. 0.5, 0.8, 1.2, 1.7)");
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

			java.awt.GridBagConstraints constraintsDefaultOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsDefaultOutputRadioButton.gridx = 0; constraintsDefaultOutputRadioButton.gridy = 0;
			constraintsDefaultOutputRadioButton.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsDefaultOutputRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getDefaultOutputRadioButton(), constraintsDefaultOutputRadioButton);

			java.awt.GridBagConstraints constraintsUniformOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsUniformOutputRadioButton.gridx = 0; constraintsUniformOutputRadioButton.gridy = 1;
			constraintsUniformOutputRadioButton.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsUniformOutputRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getUniformOutputRadioButton(), constraintsUniformOutputRadioButton);

			java.awt.GridBagConstraints constraintsExplicitOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsExplicitOutputRadioButton.gridx = 0; constraintsExplicitOutputRadioButton.gridy = 2;
			constraintsExplicitOutputRadioButton.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsExplicitOutputRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getExplicitOutputRadioButton(), constraintsExplicitOutputRadioButton);

			java.awt.GridBagConstraints constraintsDefaultOutputPanel = new java.awt.GridBagConstraints();
			constraintsDefaultOutputPanel.gridx = 1; constraintsDefaultOutputPanel.gridy = 0;
			constraintsDefaultOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDefaultOutputPanel.weightx = 1.0;
			constraintsDefaultOutputPanel.weighty = 1.0;
			constraintsDefaultOutputPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getDefaultOutputPanel(), constraintsDefaultOutputPanel);

			java.awt.GridBagConstraints constraintsUniformOutputPanel = new java.awt.GridBagConstraints();
			constraintsUniformOutputPanel.gridx = 1; constraintsUniformOutputPanel.gridy = 1;
			constraintsUniformOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsUniformOutputPanel.weightx = 1.0;
			constraintsUniformOutputPanel.weighty = 1.0;
			constraintsUniformOutputPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getUniformOutputPanel(), constraintsUniformOutputPanel);

			java.awt.GridBagConstraints constraintsExplicitOutputPanel = new java.awt.GridBagConstraints();
			constraintsExplicitOutputPanel.gridx = 1; constraintsExplicitOutputPanel.gridy = 2;
			constraintsExplicitOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsExplicitOutputPanel.weightx = 1.0;
			constraintsExplicitOutputPanel.weighty = 1.0;
			constraintsExplicitOutputPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getExplicitOutputPanel(), constraintsExplicitOutputPanel);
			
			stopSpatiallyUniformPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));		
			stopSpatiallyUniformCheckBox = new JCheckBox("Stop at Spatially Uniform");
			stopSpatiallyUniformPanel.add(stopSpatiallyUniformCheckBox);
			java.awt.GridBagConstraints gridbag1 = new java.awt.GridBagConstraints();
			gridbag1.gridx = 0; gridbag1.gridy = 3;
			gridbag1.fill = GridBagConstraints.HORIZONTAL;
			gridbag1.gridwidth = 4;
			gridbag1.insets = new java.awt.Insets(0, 0, 0, 0);
			getJPanel1().add(stopSpatiallyUniformPanel, gridbag1);
			
			TitledBorder tb=new TitledBorder(new EtchedBorder(),"Output Options", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));
     	    getJPanel1().setBorder(tb);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
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
			ivjJPanelStoch.setLayout(new java.awt.GridLayout(0,1));
			
			// 1
			JPanel trialPanel = new JPanel(new GridLayout(0,1));
			trialPanel.add(getTrajectoryButton());
			trialPanel.add(getHistogramButton());	
			JPanel panela = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panela.add(getNumOfTrialsLabel());
			panela.add(getJTextFieldNumOfTrials());
			trialPanel.add(panela);
			trialPanel.setBorder(new EtchedBorder());
			
			// 2
			JPanel seedPanel = new JPanel(new GridLayout(0,1));
			JPanel panelb = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelb.add(getRandomSeed());
			seedPanel.add(panelb);
			panelb = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelb.add(getCustomizedSeed());
			panelb.add(getJTextFieldCustomSeed());
			seedPanel.add(panelb);
			seedPanel.setBorder(new EtchedBorder());
			
			//combine 1 and 2
			JPanel paneld=new JPanel(new GridLayout(0,2));
			paneld.add(trialPanel);
			paneld.add(seedPanel);
			getJPanelStoch().add(paneld);			
			// 3
			JPanel panelc = new JPanel(new GridLayout(0,2));
			JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));			
			panel1.add(getEpsilonLabel());
			panel1.add(getEpsilonTextField());
			panelc.add(panel1);
			panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel1.add(getLambdaLabel());
			panel1.add(getLambdaTextField());
			panelc.add(panel1);
			
			panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel1.add(getMSRToleranceLabel());
			panel1.add(getMSRToleranceTextField());
			panelc.add(panel1);
			panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel1.add(getSDEToleranceLabel());
			panel1.add(getSDEToleranceTextField());
			panelc.add(panel1);
			panelc.setBorder(new EtchedBorder());
			getJPanelStoch().add(panelc);
		
			TitledBorder tb=new TitledBorder(new EtchedBorder(),"Stochastic Options", TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION, new Font("Tahoma", Font.PLAIN, 11));
		    getJPanelStoch().setBorder(tb);
						
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
			ivjJTextFieldCustomSeed.setColumns(9);
			
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
			ivjJTextFieldNumOfTrials.setColumns(9);
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
private javax.swing.JLabel getNumOfTrialsLabel() {
	if (numOfTrialsLabel == null) {
		try {
			numOfTrialsLabel = new javax.swing.JLabel();
			numOfTrialsLabel.setName("NumOfTrials");
			numOfTrialsLabel.setText("Num. Of Trials");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return numOfTrialsLabel;
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
			ivjOutputTimesTextField.setColumns(20);
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
			
			java.awt.GridBagConstraints constraintsQuestionButton = new java.awt.GridBagConstraints();
			constraintsQuestionButton.gridx = 2; constraintsQuestionButton.gridy = 0;
			constraintsQuestionButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsQuestionButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsQuestionButton.insets = new java.awt.Insets(6, 6, 6, 6);
			getPanel2().add(getQuestionButton(), constraintsQuestionButton);
			
		} catch (java.lang.Throwable ivjExc) {
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
private SolverDescription getSolverDescriptionFromDisplayLabel(String argSolverName) {
	return SolverDescription.fromDisplayLabel(argSolverName);
}


/**
 * Gets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @return The solverTaskDescription property value.
 * @see #setSolverTaskDescription
 */
public SolverTaskDescription getSolverTaskDescription() {
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
	if (solverTaskDescription.getSolverDescription().equals(SolverDescription.IDA)){
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
	getQuestionButton().addActionListener(this);
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
	getTrajectoryButton().addActionListener(this);
	getHistogramButton().addActionListener(this);
	getJTextFieldCustomSeed().addFocusListener(this);
	getJTextFieldNumOfTrials().addFocusListener(this);
	getEpsilonTextField().addFocusListener(this);
	getLambdaTextField().addFocusListener(this);
	getMSRToleranceTextField().addFocusListener(this);
	getSDEToleranceTextField().addFocusListener(this);
	stopSpatiallyUniformCheckBox.addActionListener(this);
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
}

/**
 * Update parameters for stochastic simulations,
 * including using custermized seed or not, custermized seed, using tractory or histogram, number of trials (for all, and below four paras for hybrid only)
 * Epsilon : minimum number of molecus required for approximation as a continuous Markow process,
 * Lambda : minimum rate of reaction required for approximation to a continuous Markov process,
 * MSR Tolerance : Maximum allowed effect of slow reactions per numerical integration of the SDEs,
 * SDE Tolerance : Maximum allowed value of the drift and diffusion errors
 */
private void updateStochOptions(){
	if(!getJPanelStoch().isEnabled()){
		return;
	}
	try{
		StochSimOptions sso = getSolverTaskDescription().getStochOpt();
		if(getSolverTaskDescription().getSolverDescription().equals(SolverDescription.StochGibson))
		{
			if(sso == null)
			{
				sso = new StochSimOptions();
			}
			sso.setUseCustomSeed(getCustomizedSeed().isSelected());
			int trials = Integer.parseInt(getJTextFieldNumOfTrials().getText());
			if(getHistogramButton().isSelected() && (trials <= 1))
			{
				throw new Exception("Number of trials should be greater than 1 for histogram.");
			}
			sso.setNumOfTrials(trials);
			if(getCustomizedSeed().isSelected())
				sso.setCustomSeed(Integer.parseInt(getJTextFieldCustomSeed().getText()));
			getSolverTaskDescription().setStochOpt(sso);
		}
		else
		{
			StochHybridOptions sho = null;
			if(sso == null || !(sso instanceof StochHybridOptions))
			{
				sho = new StochHybridOptions();
			}
			else
			{
				sho = (StochHybridOptions)sso;
			}
			sho.setUseCustomSeed(getCustomizedSeed().isSelected());
			int trials = Integer.parseInt(getJTextFieldNumOfTrials().getText());
			if(getHistogramButton().isSelected() && (trials <= 1))
			{
				throw new Exception("Number of trials should be greater than 1 for histogram.");
			}
			sho.setNumOfTrials(trials);
			if(getCustomizedSeed().isSelected())
				sho.setCustomSeed(Integer.parseInt(getJTextFieldCustomSeed().getText()));
			if(getEpsilonTextField().isEnabled() && !getEpsilonTextField().getText().equals(""))
				sho.setEpsilon(Double.parseDouble(getEpsilonTextField().getText()));
			if(getLambdaTextField().isEnabled() && !getLambdaTextField().getText().equals(""))
				sho.setLambda(Double.parseDouble(getLambdaTextField().getText()));
			if(getMSRToleranceTextField().isEnabled() && !getMSRToleranceTextField().getText().equals(""))
				sho.setMSRTolerance(Double.parseDouble(getMSRToleranceTextField().getText()));
			if(getSDEToleranceTextField().isEnabled() && !getSDEToleranceTextField().getText().equals(""))
				sho.setSDETolerance(Double.parseDouble(getSDEToleranceTextField().getText()));
			getSolverTaskDescription().setStochOpt(sho);
		}
	}catch(Exception e){
		PopupGenerator.showErrorDialog("Error setting stochastic options\n"+e.getMessage());
	}
}

/**
 * Comment
 */
private void randomSeed_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getJTextFieldCustomSeed().setEnabled(false);
	updateStochOptions();
}


/**
 * Comment
 */
private void setNewOutputOption(java.awt.event.FocusEvent focusEvent) {
	try {
		OutputTimeSpec ots = null;
		if(getDefaultOutputRadioButton().isSelected()){
			int keepEvery = Integer.parseInt(getKeepEveryTextField().getText());
			if (getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)||getSolverTaskDescription().getSolverDescription().equals(SolverDescription.StochGibson)) 
			{
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
		// if solver is not IDA, if the output Time step radio button had been set, 
		// change the setting to the 'keep every' radio button and flush the contents of the output timestep text field. 
		// Also, disable its radiobutton and fields.
		getDefaultOutputRadioButton().setSelected(true);
		getKeepEveryTextField().setText(((DefaultOutputTimeSpec)arg1).getKeepEvery() + "");
		if (getTornOffSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume) 
				|| getTornOffSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolumeStandalone)) {
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
		getOutputTimesTextField().setCaretPosition(0);
	}

	// If the solver is IDA and if the output time step has been set, set/enable the appropriate radio buttons and text fields
	// If the output time step hasn't been set (for IDA solver), set the field to blank; and select 'keep every' radio button.
	// This will also hold for the case when solver is not IDA.		
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
	//seed radio button group
	getButtonGroupSeed().add(getRandomSeed());
	getButtonGroupSeed().add(getCustomizedSeed());
//	cbit.util.BeanUtils.enableComponents(getJPanelStoch(),false);
	//trial radio button group
	getButtonGroupTrials().add(getTrajectoryButton());
	getButtonGroupTrials().add(getHistogramButton());
	cbit.util.BeanUtils.enableComponents(getJPanelStoch(),false);
}

/**
 * Comment
 */
private void solverTaskDescriptionAdvancedPanel_SolverTaskDescription() {
	if (getSolverTaskDescription() != null && getSolverTaskDescription().getSolverDescription() != null) {
		boolean isStoch = getSolverTaskDescription().getSolverDescription().isSTOCHSolver();
		if (isStoch) {
			getJPanelStoch().setVisible(true);
			updateStochOptionsDisplay();
		} else {
			getJPanelStoch().setVisible(false);
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
		if (getSolverComboBox().getSelectedItem()!=null && getSolverComboBox().getSelectedItem().equals(argSolverDescription.getDisplayLabel())){
			return;
		}
		if (getSolverComboBox().getModel().getSize()>0){
			getSolverComboBox().setSelectedItem(argSolverDescription.getDisplayLabel());
		}
	}
}

private javax.swing.JLabel getEpsilonLabel() {
	if (ivjEpsilonLabel == null) {
		try {
			ivjEpsilonLabel = new javax.swing.JLabel();
			ivjEpsilonLabel.setName("EpsilonLabel");
			ivjEpsilonLabel.setText("Epsilon");
			ivjEpsilonLabel.setPreferredSize(new Dimension(75, 20));
			//ivjEpsilonLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			//ivjEpsilonLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEpsilonLabel;
}

private javax.swing.JTextField getEpsilonTextField() {
	if (ivjEpsilonTextField == null) {
		try {
			ivjEpsilonTextField = new javax.swing.JTextField();
			ivjEpsilonTextField.setName("JTextFieldEpsilon");
			//ivjEpsilonTextField.setPreferredSize(new java.awt.Dimension(70, 20));
			ivjEpsilonTextField.setColumns(10);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjEpsilonTextField;
}

private javax.swing.JLabel getLambdaLabel() {
	if (ivjLambdaLabel == null) {
		try {
			ivjLambdaLabel = new javax.swing.JLabel();
			ivjLambdaLabel.setName("LambdaLabel");
			ivjLambdaLabel.setText("Lambda");
			ivjLambdaLabel.setPreferredSize(new java.awt.Dimension(75, 20));
			//ivjLambdaLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			//ivjLambdaLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLambdaLabel;
}

private javax.swing.JTextField getLambdaTextField() {
	if (ivjLambdaTextField == null) {
		try {
			ivjLambdaTextField = new javax.swing.JTextField();
			ivjLambdaTextField.setName("JTextFieldLambda");
			//ivjLambdaTextField.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjLambdaTextField.setColumns(10);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLambdaTextField;
}

private javax.swing.JLabel getMSRToleranceLabel() {
	if (ivjMSRToleranceLabel == null) {
		try {
			ivjMSRToleranceLabel = new javax.swing.JLabel();
			ivjMSRToleranceLabel.setName("MSRLabel");
			ivjMSRToleranceLabel.setText("MSR Tolerance");
			ivjMSRToleranceLabel.setPreferredSize(new java.awt.Dimension(75, 20));
			//ivjMSRToleranceLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			//ivjMSRToleranceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMSRToleranceLabel;
}

private javax.swing.JTextField getMSRToleranceTextField() {
	if (ivjMSRToleranceTextField == null) {
		try {
			ivjMSRToleranceTextField = new javax.swing.JTextField();
			ivjMSRToleranceTextField.setName("JTextFieldMSRTolerance");
			//ivjMSRToleranceTextField.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjMSRToleranceTextField.setColumns(10);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMSRToleranceTextField;
}

private javax.swing.JLabel getSDEToleranceLabel() {
	if (ivjSDEToleranceLabel == null) {
		try {
			ivjSDEToleranceLabel = new javax.swing.JLabel();
			ivjSDEToleranceLabel.setName("SDELabel");
			ivjSDEToleranceLabel.setText("SDE Tolerance");
			ivjSDEToleranceLabel.setPreferredSize(new java.awt.Dimension(75, 20));
			//ivjSDEToleranceLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			//ivjSDEToleranceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSDEToleranceLabel;
}

private javax.swing.JTextField getSDEToleranceTextField() {
	if (ivjSDEToleranceTextField == null) {
		try {
			ivjSDEToleranceTextField = new javax.swing.JTextField();
			ivjSDEToleranceTextField.setName("JTextFieldSDETolerance");
			//ivjSDEToleranceTextField.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjSDEToleranceTextField.setColumns(10);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSDEToleranceTextField;
}

private javax.swing.JButton getQuestionButton() {
	if (ivjQuestionButton == null) {
		try {
			ivjQuestionButton = new javax.swing.JButton();
			ivjQuestionButton.setName("QuestionButton");
			ivjQuestionButton.setText("?");
			ivjQuestionButton.setFont(new Font("SansSerif", Font.BOLD, 12));
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjQuestionButton;
}

private void displayHelpInfo()
{
	DialogUtils.showInfoDialog(SolverDescription.getFullDescription(getTornOffSolverTaskDescription().getSolverDescription()));
}

private void setHybridOptions(boolean b)
{
	getEpsilonLabel().setEnabled(b);
	getEpsilonTextField().setEnabled(b);
	getLambdaLabel().setEnabled(b);
	getLambdaTextField().setEnabled(b);
	getMSRToleranceLabel().setEnabled(b);
	getMSRToleranceTextField().setEnabled(b);
	getSDEToleranceLabel().setEnabled(b);
	getSDEToleranceTextField().setEnabled(b);
}
/*
 * This method is called when user shifts to another new solver.
 */
private void updateStochOptionsDisplay()
{
	//cbit.util.BeanUtils.enableComponents(getJPanelStoch(),true);
	StochSimOptions sso = getSolverTaskDescription().getStochOpt();
	if(getTornOffSolverTaskDescription().getSolverDescription().equals(SolverDescription.StochGibson))
	{
		if(sso == null)
		{
			sso = new StochSimOptions();
		}
		else if(sso instanceof StochHybridOptions)
		{
			sso = new StochSimOptions(sso.isUseCustomSeed(), sso.getCustomSeed(), sso.getNumOfTrials());
		}
 	}
	else
	{		
		if(sso == null)
		{
			sso = new StochHybridOptions();
		}
		else if(!(sso instanceof StochHybridOptions))
		{
			sso = new StochHybridOptions(sso.isUseCustomSeed(), sso.getCustomSeed(), sso.getNumOfTrials());
		}
	}
	getSolverTaskDescription().setStochOpt(sso);
	displayStochPanel();
}

private void displayStochPanel() {
	cbit.util.BeanUtils.enableComponents(getJPanelStoch(),true);
	StochSimOptions sso = getSolverTaskDescription().getStochOpt();	
	
	long numTrials = sso.getNumOfTrials();
	getJTextFieldNumOfTrials().setText(numTrials+"");
	if(numTrials == 1){ // 1 trial
		getJTextFieldNumOfTrials().setEnabled(false);
		getTrajectoryButton().setSelected(true);
	}else{//more than 1 trial
		getJTextFieldNumOfTrials().setEnabled(true);
		getHistogramButton().setSelected(true);
	}
	boolean isUseCustomSeed = sso.isUseCustomSeed();
	int customSeed = sso.getCustomSeed();
	
	getJTextFieldCustomSeed().setEnabled(isUseCustomSeed);
	if(isUseCustomSeed){
		getCustomizedSeed().setSelected(true);
		getJTextFieldCustomSeed().setEnabled(true);		
	}else{
		getRandomSeed().setSelected(true);
		getJTextFieldCustomSeed().setEnabled(false);
	}
	getJTextFieldCustomSeed().setText(customSeed+"");

	boolean bHybrid = sso instanceof StochHybridOptions;
	setHybridOptions(bHybrid);
	if (bHybrid)
	{
		StochHybridOptions sho = (StochHybridOptions)sso;
		if(!getTornOffSolverTaskDescription().getSolverDescription().equals(SolverDescription.HybridMilAdaptive))
		{
			getSDEToleranceTextField().setEnabled(false);
		}
		getEpsilonTextField().setText(sho.getEpsilon()+"");
		getLambdaTextField().setText(sho.getLambda()+"");
		getMSRToleranceTextField().setText(sho.getMSRTolerance()+"");
		getSDEToleranceTextField().setText(sho.getSDETolerance()+"");
	}
}
}