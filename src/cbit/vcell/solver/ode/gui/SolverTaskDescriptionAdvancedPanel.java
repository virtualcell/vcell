/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

package cbit.vcell.solver.ode.gui;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.stoch.StochHybridOptions;
import cbit.vcell.solver.stoch.StochSimOptions;

/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:25 PM)
 * @author: John Wagner
 */
public class SolverTaskDescriptionAdvancedPanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjSolverPanel = null;
	private javax.swing.JLabel ivjIntegratorLabel = null;
	
	private ErrorTolerancePanel ivjErrorTolerancePanel = null;
	private TimeBoundsPanel ivjTimeBoundsPanel = null;
	private TimeStepPanel ivjTimeStepPanel = null;
	private SolverTaskDescription fieldSolverTaskDescription = null;
	private boolean ivjConnPtoP1Aligning = false;
	private SolverTaskDescription ivjTornOffSolverTaskDescription = null;

	private javax.swing.JComboBox ivjSolverComboBox = null;
	private javax.swing.JButton ivjQuestionButton = null;
	private javax.swing.DefaultComboBoxModel fieldSolverComboBoxModel = null;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP3Aligning = false;
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private Object ivjSolverComboBoxModel = null;
	private OutputOptionsPanel ivjOutputOptionsPanel = null;

	private javax.swing.JLabel ivjJLabelTitle = null;
	private javax.swing.JRadioButton trajectoryRadioButton = null;
	private javax.swing.JRadioButton histogramRadioButton = null;
	private javax.swing.ButtonGroup buttonGroupTrials = null;
	private javax.swing.JLabel numOfTrialsLabel = null;
	private javax.swing.JTextField ivjJTextFieldNumOfTrials = null;
	private javax.swing.JRadioButton ivjCustomizedSeedRadioButton = null;
	private javax.swing.JRadioButton ivjRandomSeedRadioButton = null;
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
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	
	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getCustomizedSeedRadioButton()){
				connEtoC23(e);
			}
			if (e.getSource() == getRandomSeedRadioButton()) {
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
		}
		
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SolverTaskDescriptionAdvancedPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) { 
				connPtoP1SetTarget();
				solverTaskDescriptionAdvancedPanel_SolverTaskDescription();
			}
			if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_TIME_STEP))) 
				connPtoP3SetTarget();
			if (evt.getSource() == getTimeStepPanel() && (evt.getPropertyName().equals("timeStep"))) 
				connPtoP3SetSource();
			if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_ERROR_TOLERANCE))) 
				connPtoP4SetTarget();
			if (evt.getSource() == getErrorTolerancePanel() && (evt.getPropertyName().equals("errorTolerance"))) 
				connPtoP4SetSource();
			if (evt.getSource() == getSolverComboBox() && (evt.getPropertyName().equals("model"))) 
				connPtoP7SetSource();
			if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION))) {
				onPropertyChange_solverDescription();
			}
			if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_TIME_BOUNDS))) { 
				connPtoP2SetTarget();
			}
			if (evt.getSource() == getTimeBoundsPanel() && (evt.getPropertyName().equals("timeBounds"))) 
				connPtoP2SetSource();
			
			if (evt.getSource() == getTornOffSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_STOP_AT_SPATIALLY_UNIFORM))) { 
				BeanUtils.enableComponents(getErrorTolerancePanel(), getSolverTaskDescription().isStopAtSpatiallyUniform());
			}			
		}
		
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == getSolverComboBox()) 
				connEtoM6(e);
		}
		
		/**
		 * Method to handle events for the FocusListener interface.
		 * @param e java.awt.event.FocusEvent
		 */
		public void focusGained(java.awt.event.FocusEvent e) {
		}


		/**
		 * Method to handle events for the FocusListener interface.
		 * @param e java.awt.event.FocusEvent
		 */
		public void focusLost(java.awt.event.FocusEvent e) {
			if (!e.isTemporary()) {
				//Stoch options
				if (e.getSource() == getJTextFieldCustomSeed() 
						|| e.getSource() == getJTextFieldNumOfTrials()
						|| e.getSource() == getEpsilonTextField() 
						|| e.getSource() == getLambdaTextField()
						|| e.getSource() == getMSRToleranceTextField()
						|| e.getSource() == getSDEToleranceTextField()) { 
					updateStochOptions();
				}
			}
		}
	}
	
public SolverTaskDescriptionAdvancedPanel() {
	super();
	initialize();
}

/**
 * connEtoC1:  (TornOffSolverTaskDescription.this --> SolverTaskDescriptionAdvancedPanel.enableTimeStep()V)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
private void connEtoC1(SolverTaskDescription value) {
	try {
		this.enableVariableTimeStepOptions();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC23:  (CustomizedSeed.action.actionPerformed(java.awt.event.ActionEvent) --> SolverTaskDescriptionAdvancedPanel.customizedSeed_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC23(java.awt.event.ActionEvent arg1) {
	try {
		this.customizedSeed_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC24:  (RandomSeed.action.actionPerformed(java.awt.event.ActionEvent) --> SolverTaskDescriptionAdvancedPanel.randomSeed_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC24(java.awt.event.ActionEvent arg1) {
	try {
		this.randomSeedRadioButton_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}



/**
 * connEtoC6:  (TornOffSolverTaskDescription.this --> SolverTaskDescriptionAdvancedPanel.updateSolverNameDisplay(Ljava.lang.String;)V)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
private void connEtoC6(SolverTaskDescription value) {
	try {
		if ((getTornOffSolverTaskDescription() != null)) {
			this.updateSolverNameDisplay(getTornOffSolverTaskDescription().getSolverDescription());
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoM13:  (TornOffSolverTaskDescription.this --> SolverComboBoxModel.this)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
private void connEtoM13(SolverTaskDescription value) {
	try {
		setSolverComboBoxModel(this.createSolverComboBoxModel(getTornOffSolverTaskDescription()));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (SolverComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> TornOffSolverTaskDescription.solverDescription)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoM6(java.awt.event.ItemEvent arg1) {
	try {
		getTornOffSolverTaskDescription().setSolverDescription(this.getSolverDescriptionFromDisplayLabel((String)getSolverComboBox().getSelectedItem()));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (SolverTaskDescriptionAdvancedPanel.solverTaskDescription <--> TornOffSolverTaskDescription.this)
 */
private void connPtoP1SetSource() {
	try {
		if (ivjConnPtoP1Aligning == false) {
			ivjConnPtoP1Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				this.setSolverTaskDescription(getTornOffSolverTaskDescription());
			}
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (SolverTaskDescriptionAdvancedPanel.solverTaskDescription <--> TornOffSolverTaskDescription.this)
 */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			ivjConnPtoP1Aligning = true;
			setTornOffSolverTaskDescription(this.getSolverTaskDescription());
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (TornOffSolverTaskDescription.timeBounds <--> TimeBoundsPanel.timeBounds)
 */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			ivjConnPtoP2Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTornOffSolverTaskDescription().setTimeBounds(getTimeBoundsPanel().getTimeBounds());
			}
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (TornOffSolverTaskDescription.timeBounds <--> TimeBoundsPanel.timeBounds)
 */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			ivjConnPtoP2Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTimeBoundsPanel().setTimeBounds(getTornOffSolverTaskDescription().getTimeBounds());
			}
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetSource:  (TornOffSolverTaskDescription.timeStep <--> TimeStepPanel.timeStep)
 */
private void connPtoP3SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP3Aligning == false) {
			ivjConnPtoP3Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTornOffSolverTaskDescription().setTimeStep(getTimeStepPanel().getTimeStep());
			}
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP3SetTarget:  (TornOffSolverTaskDescription.timeStep <--> TimeStepPanel.timeStep)
 */
private void connPtoP3SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP3Aligning == false) {
			ivjConnPtoP3Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTimeStepPanel().setTimeStep(getTornOffSolverTaskDescription().getTimeStep());
			}
			ivjConnPtoP3Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP3Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetSource:  (TornOffSolverTaskDescription.errorTolerance <--> ErrorTolerancePanel.errorTolerance)
 */
private void connPtoP4SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP4Aligning == false) {
			ivjConnPtoP4Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getTornOffSolverTaskDescription().setErrorTolerance(getErrorTolerancePanel().getErrorTolerance());
			}
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP4SetTarget:  (TornOffSolverTaskDescription.errorTolerance <--> ErrorTolerancePanel.errorTolerance)
 */
private void connPtoP4SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP4Aligning == false) {
			ivjConnPtoP4Aligning = true;
			if ((getTornOffSolverTaskDescription() != null)) {
				getErrorTolerancePanel().setErrorTolerance(getTornOffSolverTaskDescription().getErrorTolerance());
			}
			ivjConnPtoP4Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP4Aligning = false;
		handleException(ivjExc);
	}
}


/**
 * connPtoP7SetSource:  (SolverComboBox.model <--> model1.this)
 */
private void connPtoP7SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP7Aligning == false) {
			ivjConnPtoP7Aligning = true;
			setSolverComboBoxModel(getSolverComboBox().getModel());
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
		handleException(ivjExc);
	}
}

/**
 * connPtoP7SetTarget:  (SolverComboBox.model <--> model1.this)
 */
private void connPtoP7SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP7Aligning == false) {
			ivjConnPtoP7Aligning = true;
			if ((getSolverComboBoxModel() != null)) {
				getSolverComboBox().setModel((javax.swing.ComboBoxModel)getSolverComboBoxModel());
			}
			ivjConnPtoP7Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP7Aligning = false;
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
		if (getSolverTaskDescription().getSimulation().isSpatial()) 
		{
			if (getSolverTaskDescription().getSimulation().getMathDescription().hasFastSystems()) { // PDE with FastSystem
				solverDescriptions = SolverDescription.getPDEWithFastSystemSolverDescriptions();
			} else {
				solverDescriptions = SolverDescription.getPDESolverDescriptions();
			}
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
	
	if (solverDescription.isSemiImplicitPdeSolver()) {
		getErrorTolerancePanel().setupForSemiImplicitSolver();
	} else {
		BeanUtils.enableComponents(getErrorTolerancePanel(), solverDescription.hasErrorTolerance() || getSolverTaskDescription().isStopAtSpatiallyUniform());
	}
	//Hybrid solvers should show default time step
	if (solverDescription.compareEqual(SolverDescription.HybridEuler)||
		solverDescription.compareEqual(SolverDescription.HybridMilstein)||
		solverDescription.compareEqual(SolverDescription.HybridMilAdaptive))
	{
		getTimeStepPanel().enableComponents(false); //force using default time step only
	}
	else
	{
		getTimeStepPanel().enableComponents(bHasVariableTS);
	}
	if (solverDescription.compareEqual(SolverDescription.StochGibson))
	{
		getTimeStepPanel().disableMinAndMaxTimeStep();// gibson doesn't min and max time step
	}
	if (solverDescription.isSundialsSolver()) {
		getTimeStepPanel().disableMinTimeStep();
	}
}



/**
 * Return the ButtonGroupSeed property value.
 * @return javax.swing.ButtonGroup
 */
private javax.swing.ButtonGroup getButtonGroupTrials() {
	if (buttonGroupTrials == null) {
		try {
			buttonGroupTrials = new javax.swing.ButtonGroup();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return buttonGroupTrials;
}

/**
 * Return the CustomizedSeed property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getTrajectoryButton() {
	if (trajectoryRadioButton == null) {
		try {
			trajectoryRadioButton = new javax.swing.JRadioButton();
			trajectoryRadioButton.setName("Trajectory");
			trajectoryRadioButton.setText("Single Trajectory");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return trajectoryRadioButton;
}

/**
 * Return the CustomizedSeed property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getHistogramButton() {
	if (histogramRadioButton == null) {
		try {
			histogramRadioButton = new javax.swing.JRadioButton();
			histogramRadioButton.setName("Histogram");
			histogramRadioButton.setText("Histogram (last time point only)");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return histogramRadioButton;
}


/**
 * Return the ButtonGroupSeed property value.
 * @return javax.swing.ButtonGroup
 */
private javax.swing.ButtonGroup getButtonGroupSeed() {
	if (ivjButtonGroupSeed == null) {
		try {
			ivjButtonGroupSeed = new javax.swing.ButtonGroup();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupSeed;
}


/**
 * Return the CustomizedSeed property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getCustomizedSeedRadioButton() {
	if (ivjCustomizedSeedRadioButton == null) {
		try {
			ivjCustomizedSeedRadioButton = new javax.swing.JRadioButton();
			ivjCustomizedSeedRadioButton.setName("CustomizedSeed");
			ivjCustomizedSeedRadioButton.setText("Customized Seed");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCustomizedSeedRadioButton;
}

/**
 * Return the ErrorTolerancePanel property value.
 * @return cbit.vcell.solver.ode.gui.ErrorTolerancePanel
 */
private ErrorTolerancePanel getErrorTolerancePanel() {
	if (ivjErrorTolerancePanel == null) {
		try {
			ivjErrorTolerancePanel = new ErrorTolerancePanel();
			ivjErrorTolerancePanel.setName("ErrorTolerancePanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjErrorTolerancePanel;
}

/**
 * Return the Label4 property value.
 * @return java.awt.Label
 */
private javax.swing.JLabel getIntegratorLabel() {
	if (ivjIntegratorLabel == null) {
		try {
			ivjIntegratorLabel = new javax.swing.JLabel();
			ivjIntegratorLabel.setName("IntegratorLabel");
			ivjIntegratorLabel.setText("Integrator");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjIntegratorLabel;
}

/**
 * Return the JLabelTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
			ivjJLabelTitle.setText("Choose solver algorithm and fine-tune time conditions:");
			ivjJLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabelTitle.setFont(ivjJLabelTitle.getFont().deriveFont(java.awt.Font.BOLD));
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
private OutputOptionsPanel getOutputOptionsPanel() {
	if (ivjOutputOptionsPanel == null) {
		try {
			ivjOutputOptionsPanel = new OutputOptionsPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutputOptionsPanel;
}

/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
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
			panelb.add(getRandomSeedRadioButton());
			seedPanel.add(panelb);
			panelb = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelb.add(getCustomizedSeedRadioButton());
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelStoch;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getJTextFieldCustomSeed() {
	if (ivjJTextFieldCustomSeed == null) {
		try {
			ivjJTextFieldCustomSeed = new javax.swing.JTextField();
			ivjJTextFieldCustomSeed.setName("JTextFieldCustomSeed");
			ivjJTextFieldCustomSeed.setEnabled(false);
			ivjJTextFieldCustomSeed.setColumns(9);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCustomSeed;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getJTextFieldNumOfTrials() {
	if (ivjJTextFieldNumOfTrials == null) {
		try {
			ivjJTextFieldNumOfTrials = new javax.swing.JTextField();
			ivjJTextFieldNumOfTrials.setName("JTextFieldNumOfTrials");
			ivjJTextFieldNumOfTrials.setColumns(9);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldNumOfTrials;
}



/**
 * Return the NumOfTrials property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getNumOfTrialsLabel() {
	if (numOfTrialsLabel == null) {
		try {
			numOfTrialsLabel = new javax.swing.JLabel();
			numOfTrialsLabel.setName("NumOfTrials");
			numOfTrialsLabel.setText("Num. Of Trials");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return numOfTrialsLabel;
}

/**
 * Return the Panel2 property value.
 * @return java.awt.Panel
 */
private javax.swing.JPanel getSolverPanel() {
	if (ivjSolverPanel == null) {
		try {
			ivjSolverPanel = new javax.swing.JPanel();
			ivjSolverPanel.setName("Panel2");
			ivjSolverPanel.setOpaque(false);
			ivjSolverPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsIntegratorLabel = new java.awt.GridBagConstraints();
			constraintsIntegratorLabel.gridx = 0; constraintsIntegratorLabel.gridy = 0;
			constraintsIntegratorLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsIntegratorLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIntegratorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjSolverPanel.add(getIntegratorLabel(), constraintsIntegratorLabel);

			java.awt.GridBagConstraints constraintsSolverComboBox = new java.awt.GridBagConstraints();
			constraintsSolverComboBox.gridx = 1; constraintsSolverComboBox.gridy = 0;
			constraintsSolverComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSolverComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsSolverComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjSolverPanel.add(getSolverComboBox(), constraintsSolverComboBox);
			
			java.awt.GridBagConstraints constraintsQuestionButton = new java.awt.GridBagConstraints();
			constraintsQuestionButton.gridx = 2; constraintsQuestionButton.gridy = 0;
			constraintsQuestionButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsQuestionButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsQuestionButton.insets = new java.awt.Insets(6, 6, 6, 6);
			ivjSolverPanel.add(getQuestionButton(), constraintsQuestionButton);
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSolverPanel;
}

/**
 * Return the RandomSeed property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getRandomSeedRadioButton() {
	if (ivjRandomSeedRadioButton == null) {
		try {
			ivjRandomSeedRadioButton = new javax.swing.JRadioButton();
			ivjRandomSeedRadioButton.setName("RandomSeed");
			ivjRandomSeedRadioButton.setSelected(true);
			ivjRandomSeedRadioButton.setText("Random Seed");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRandomSeedRadioButton;
}

/**
 * Return the Choice1 property value.
 * @return java.awt.Choice
 */
private javax.swing.JComboBox getSolverComboBox() {
	if (ivjSolverComboBox == null) {
		try {
			ivjSolverComboBox = new javax.swing.JComboBox();
			ivjSolverComboBox.setName("SolverComboBox");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSolverComboBox;
}

/**
 * Return the model1 property value.
 * @return javax.swing.ComboBoxModel
 */
private java.lang.Object getSolverComboBoxModel() {
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
private SolverTaskDescription getSolverTaskDescription() {
	return fieldSolverTaskDescription;
}


/**
 * Return the TimeBoundsPanel property value.
 * @return cbit.vcell.solver.ode.gui.TimeBoundsPanel
 */
private TimeBoundsPanel getTimeBoundsPanel() {
	if (ivjTimeBoundsPanel == null) {
		try {
			ivjTimeBoundsPanel = new TimeBoundsPanel();
			ivjTimeBoundsPanel.setName("TimeBoundsPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTimeBoundsPanel;
}


/**
 * Return the TimeStepPanel property value.
 * @return cbit.vcell.solver.ode.gui.TimeStepPanel
 */
private TimeStepPanel getTimeStepPanel() {
	if (ivjTimeStepPanel == null) {
		try {
			ivjTimeStepPanel = new TimeStepPanel();
			ivjTimeStepPanel.setName("TimeStepPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTimeStepPanel;
}

/**
 * Return the TornOffSolverTaskDescription property value.
 * @return cbit.vcell.solver.SolverTaskDescription
 */
private SolverTaskDescription getTornOffSolverTaskDescription() {
	return ivjTornOffSolverTaskDescription;
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
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getTimeStepPanel().addPropertyChangeListener(ivjEventHandler);
	getErrorTolerancePanel().addPropertyChangeListener(ivjEventHandler);
	getSolverComboBox().addPropertyChangeListener(ivjEventHandler);
	getSolverComboBox().addItemListener(ivjEventHandler);
	getQuestionButton().addActionListener(ivjEventHandler);
	getTimeBoundsPanel().addPropertyChangeListener(ivjEventHandler);
	getCustomizedSeedRadioButton().addActionListener(ivjEventHandler);
	getRandomSeedRadioButton().addActionListener(ivjEventHandler);
	getTrajectoryButton().addActionListener(ivjEventHandler);
	getHistogramButton().addActionListener(ivjEventHandler);
	getJTextFieldCustomSeed().addFocusListener(ivjEventHandler);
	getJTextFieldNumOfTrials().addFocusListener(ivjEventHandler);
	getEpsilonTextField().addFocusListener(ivjEventHandler);
	getLambdaTextField().addFocusListener(ivjEventHandler);
	getMSRToleranceTextField().addFocusListener(ivjEventHandler);
	getSDEToleranceTextField().addFocusListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP3SetTarget();
	connPtoP4SetTarget();
	connPtoP7SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setLayout(new java.awt.GridBagLayout());
		setSize(607, 419);

		// 0
		java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
		constraintsJLabelTitle.gridx = 0; constraintsJLabelTitle.gridy = 0;
		constraintsJLabelTitle.gridwidth = 4;
		constraintsJLabelTitle.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelTitle.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelTitle(), constraintsJLabelTitle);
		
		// 1
		java.awt.GridBagConstraints constraintsPanel2 = new java.awt.GridBagConstraints();
		constraintsPanel2.gridx = 0; constraintsPanel2.gridy = 1;
		constraintsPanel2.gridwidth = 4;
		constraintsPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPanel2.weightx = 1.0;
		constraintsPanel2.weighty = 1.0;
		constraintsPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSolverPanel(), constraintsPanel2);

		// 2
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

		// 3
		java.awt.GridBagConstraints constraintsJPanelStoch = new java.awt.GridBagConstraints();
		constraintsJPanelStoch.gridx = 0; constraintsJPanelStoch.gridy = 3;
		constraintsJPanelStoch.gridwidth = 4;
		constraintsJPanelStoch.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelStoch.weightx = 1.0;
		constraintsJPanelStoch.weighty = 1.0;
		constraintsJPanelStoch.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanelStoch(), constraintsJPanelStoch);
		
		// 4
		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 4;
		constraintsJPanel1.gridwidth = 4;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getOutputOptionsPanel(), constraintsJPanel1);

		getButtonGroupSeed().add(getRandomSeedRadioButton());
		getButtonGroupSeed().add(getCustomizedSeedRadioButton());
		
		//trial radio button group
		getButtonGroupTrials().add(getTrajectoryButton());
		getButtonGroupTrials().add(getHistogramButton());
		BeanUtils.enableComponents(getJPanelStoch(),false);
		
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
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
	if(!getJPanelStoch().isVisible()){
		return;
	}
	try{
		if(getSolverTaskDescription().getSolverDescription().equals(SolverDescription.StochGibson))
		{
			StochSimOptions newsso = new StochSimOptions();			
			newsso.setUseCustomSeed(getCustomizedSeedRadioButton().isSelected());
			int trials = Integer.parseInt(getJTextFieldNumOfTrials().getText());
			if(getHistogramButton().isSelected() && (trials <= 1))
			{
				throw new Exception("Number of trials should be greater than 1 for histogram.");
			}
			newsso.setNumOfTrials(trials);
			if(getCustomizedSeedRadioButton().isSelected())
				newsso.setCustomSeed(Integer.parseInt(getJTextFieldCustomSeed().getText()));
			getSolverTaskDescription().setStochOpt(newsso);
		}
		else
		{
			StochHybridOptions newsho = new StochHybridOptions();
			newsho.setUseCustomSeed(getCustomizedSeedRadioButton().isSelected());
			int trials = Integer.parseInt(getJTextFieldNumOfTrials().getText());
			if(getHistogramButton().isSelected() && (trials <= 1))
			{
				throw new Exception("Number of trials should be greater than 1 for histogram.");
			}
			newsho.setNumOfTrials(trials);
			if(getCustomizedSeedRadioButton().isSelected())
				newsho.setCustomSeed(Integer.parseInt(getJTextFieldCustomSeed().getText()));
			if(getEpsilonTextField().isEnabled() && !getEpsilonTextField().getText().equals(""))
				newsho.setEpsilon(Double.parseDouble(getEpsilonTextField().getText()));
			if(getLambdaTextField().isEnabled() && !getLambdaTextField().getText().equals(""))
				newsho.setLambda(Double.parseDouble(getLambdaTextField().getText()));
			if(getMSRToleranceTextField().isEnabled() && !getMSRToleranceTextField().getText().equals(""))
				newsho.setMSRTolerance(Double.parseDouble(getMSRToleranceTextField().getText()));
			if(getSDEToleranceTextField().isEnabled() && !getSDEToleranceTextField().getText().equals(""))
				newsho.setSDETolerance(Double.parseDouble(getSDEToleranceTextField().getText()));
			getSolverTaskDescription().setStochOpt(newsho);
		}
	}catch(Exception e){
		PopupGenerator.showErrorDialog(this, "Error setting stochastic options\n"+e.getMessage());
	}
}

/**
 * Comment
 */
private void randomSeedRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getJTextFieldCustomSeed().setEnabled(false);
	updateStochOptions();
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
public void setSolverTaskDescription(SolverTaskDescription solverTaskDescription) throws java.beans.PropertyVetoException {
	SolverTaskDescription oldValue = fieldSolverTaskDescription;
	fireVetoableChange("solverTaskDescription", oldValue, solverTaskDescription);
	fieldSolverTaskDescription = solverTaskDescription;
	firePropertyChange("solverTaskDescription", oldValue, solverTaskDescription);
}


/**
 * Set the TornOffSolverTaskDescription to a new value.
 * @param newValue cbit.vcell.solver.SolverTaskDescription
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setTornOffSolverTaskDescription(SolverTaskDescription newValue) {
	if (ivjTornOffSolverTaskDescription != newValue) {
		try {
			SolverTaskDescription oldValue = getTornOffSolverTaskDescription();
			/* Stop listening for events from the current object */
			if (ivjTornOffSolverTaskDescription != null) {
				ivjTornOffSolverTaskDescription.removePropertyChangeListener(ivjEventHandler);
			}
			ivjTornOffSolverTaskDescription = newValue;

			/* Listen for events from the new object */
			if (ivjTornOffSolverTaskDescription != null) {
				ivjTornOffSolverTaskDescription.addPropertyChangeListener(ivjEventHandler);
			}
			connPtoP1SetSource();
			connEtoM13(ivjTornOffSolverTaskDescription);
			connPtoP3SetTarget();
			connPtoP4SetTarget();
			connEtoC6(ivjTornOffSolverTaskDescription);
			connEtoC1(ivjTornOffSolverTaskDescription);
			getOutputOptionsPanel().setSolverTaskDescription(ivjTornOffSolverTaskDescription);
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
private void solverTaskDescriptionAdvancedPanel_SolverTaskDescription() {
	try {
		if (getSolverTaskDescription() != null && getSolverTaskDescription().getSolverDescription() != null) {
			boolean isStoch = getSolverTaskDescription().getSolverDescription().isSTOCHSolver();
			if (isStoch) {
				getJPanelStoch().setVisible(true);
				updateStochOptionsDisplay();
			} else {
				getJPanelStoch().setVisible(false);
			}
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}	
}


/**
 * Comment
 */
private void updateSolverNameDisplay(SolverDescription argSolverDescription) {
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
	DialogUtils.showInfoDialog(this, getTornOffSolverTaskDescription().getSolverDescription().getFullDescription());
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
	BeanUtils.enableComponents(getJPanelStoch(),true);
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
		getCustomizedSeedRadioButton().setSelected(true);
		getJTextFieldCustomSeed().setEnabled(true);		
	}else{
		getRandomSeedRadioButton().setSelected(true);
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

private void onPropertyChange_solverDescription() {	
	try {
		if (getSolverTaskDescription() == null) {
			return;
		}
		SolverDescription solverDescription = getSolverTaskDescription().getSolverDescription();
		updateSolverNameDisplay(solverDescription);
		enableVariableTimeStepOptions();
		
		enableVariableTimeStepOptions();
		if(solverDescription.isSTOCHSolver())
		{
			updateStochOptionsDisplay();
		}	
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}	
}

}