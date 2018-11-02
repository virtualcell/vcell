/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */


package cbit.vcell.solver.ode.gui;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.vcell.chombo.ChomboMeshValidator;
import org.vcell.chombo.ChomboMeshValidator.ChomboMeshRecommendation;
import org.vcell.chombo.ChomboSolverSpec;
import org.vcell.chombo.gui.ChomboDeveloperToolsPanel;
import org.vcell.chombo.gui.ChomboTimeBoundsPanel;
import org.vcell.solver.nfsim.gui.NFSimSimulationOptionsPanel;
import org.vcell.solver.smoldyn.gui.SmoldynSimulationOptionsPanel;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.ClientTaskManager;
import cbit.vcell.geometry.ChomboInvalidGeometryException;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner.UnitInfo;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverDescription.SolverFeature;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solvers.mb.gui.MovingBoundarySolverOptionsPanel;

/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:25 PM)
 * @author: John Wagner
 */
@SuppressWarnings("serial")
public class SolverTaskDescriptionAdvancedPanel extends javax.swing.JPanel {
	private final static String SELECT_PARAMETER = "select parameter";
	private javax.swing.JLabel ivjJLabelTitle = null;
	private javax.swing.JPanel ivjSolverPanel = null;
	private javax.swing.JLabel ivjIntegratorLabel = null;
	private javax.swing.JComboBox sensitivityAnalysisComboBox = null;

	private ErrorTolerancePanel ivjErrorTolerancePanel = null;
	private TimeBoundsPanel ivjTimeBoundsPanel = null;
	private TimeStepPanel ivjTimeStepPanel = null;
	private SolverTaskDescription fieldSolverTaskDescription = null;
	private boolean ivjConnPtoP1Aligning = false;
	private SolverTaskDescription ivjTornOffSolverTaskDescription = null;

	private javax.swing.JComboBox ivjSolverComboBox = null;
	private javax.swing.JButton ivjQuestionButton = null;
	private javax.swing.DefaultComboBoxModel<String> fieldSolverComboBoxModel = null;
	private boolean ivjConnPtoP2Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private Object ivjSolverComboBoxModel = null;
	private OutputOptionsPanel ivjOutputOptionsPanel = null;
	private StochSimOptionsPanel stochSimOptionsPanel = null;
	private SmoldynSimulationOptionsPanel smoldynSimulationOptionsPanel = null;
	private NFSimSimulationOptionsPanel nfsimSimulationOptionsPanel = null;
	private SundialsPdeSolverOptionsPanel sundialsPdeSolverOptionsPanel = null;

	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private CollapsiblePanel generalOptionsPanel;
	private ChomboTimeBoundsPanel chomboTimeBoundsPanel;
	private CollapsiblePanel miscPanel = null;
	private StopAtSpatiallyUniformPanel stopAtSpatiallyUniformPanel = null;
	private DataProcessingInstructionPanel dataProcessingInstructionPanel = null;
	private JCheckBox serialParameterScanCheckBox = null;
	private CollapsiblePanel sensitivityAnalysisCollapsiblePanel;
	private JCheckBox performSensitivityAnalysisCheckBox;
	private JButton sensitivityAnalysisHelpButton;
	private UnitInfo unitInfo;
	private ChomboDeveloperToolsPanel chomboDeveloperToolsPanel;
	private MovingBoundarySolverOptionsPanel movingBoundarySolverOptionsPanel;
	private JCheckBox timeoutDisabledCheckBox = null;
	private JButton timeoutDisabledHelpButton = null;

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if(e.getSource() == getQuestionButton()) {
				displayHelpInfo();
			} else if (e.getSource() == performSensitivityAnalysisCheckBox) {
				performSensitivityAnalysisCheckbox_actionPerformed();
			} else if (e.getSource() == sensitivityAnalysisComboBox) {
				sensitivityAnalysisComboBox_actionPerformed();
			} else if (e.getSource() == sensitivityAnalysisHelpButton) {
				showSensitivityAnalysisHelp();
			} else if (e.getSource() == timeoutDisabledHelpButton) {
				DialogUtils.showInfoDialog(SolverTaskDescriptionAdvancedPanel.this, "Disable forced timeout for very long Simulations", 
						"<html>By default, Simulations running for a month are automatically terminated. The reason for "
						+ "this is to free hardware resources locked by long forgotten / crashed simulations in a "
						+ "consistent manner. <br>"
						+ "However, we are allowing our power users to bypass this rule and allow very long simulations "
						+ "to run indefinitely. If you need to run such a simulation, please contact us to be added to "
						+ "the power user list."
						+ "</html>");
			}
		}

		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SolverTaskDescriptionAdvancedPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) {
				connPtoP1SetTarget();
				managePanels();
			}
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
		}

		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == getSolverComboBox()) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					connEtoM6(e);
				}
			}
			else if (e.getSource() == serialParameterScanCheckBox) {
				getSolverTaskDescription().setSerialParameterScan(serialParameterScanCheckBox.isSelected());
			}
			else if (e.getSource() == timeoutDisabledCheckBox) {
				getSolverTaskDescription().setTimeoutDisabled(timeoutDisabledCheckBox.isSelected());
			}
		}
	}

public SolverTaskDescriptionAdvancedPanel() {
	super();
	initialize();
}

/**
 * connEtoC6:  (TornOffSolverTaskDescription.this --> SolverTaskDescriptionAdvancedPanel.updateSolverNameDisplay(Ljava.lang.String;)V)
 * @param value cbit.vcell.solver.SolverTaskDescription
 */
private void connEtoC6(SolverTaskDescription value) {
	try {
		if ((getTornOffSolverTaskDescription() != null)) {
			this.refresh();
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
			SolverDescription solverDescription = getSolverDescriptionFromDisplayLabel((String)getSolverComboBox().getSelectedItem());
			validateChomboExtentAR(solverDescription);
			getTornOffSolverTaskDescription().setSolverDescription(solverDescription);
		} catch (ChomboInvalidGeometryException ivjExc) {
			// set solver back to what it was
			fieldSolverComboBoxModel.setSelectedItem(fieldSolverTaskDescription.getSolverDescription().getDisplayLabel());
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
private void managePanels() {
	if(this.getSolverTaskDescription().getSimulation().getMathDescription().isRuleBased()) {
		if(sensitivityAnalysisCollapsiblePanel != null) sensitivityAnalysisCollapsiblePanel.setVisible(false);
		if(nfsimSimulationOptionsPanel != null) nfsimSimulationOptionsPanel.expand(true);
	} else {
		if(sensitivityAnalysisCollapsiblePanel != null) {
			sensitivityAnalysisCollapsiblePanel.setVisible(!getSolverTaskDescription().getSimulation().getMathDescription().isSpatial() && !getSolverTaskDescription().getSimulation().getMathDescription().isNonSpatialStoch());
		}
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
				ClientTaskManager.changeEndTime(this, getTornOffSolverTaskDescription(), getTimeBoundsPanel().getTimeBounds().getEndingTime());
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
private javax.swing.DefaultComboBoxModel<String> createSolverComboBoxModel(SolverTaskDescription newSolverTaskDescription) {
	if (fieldSolverComboBoxModel == null) {
		fieldSolverComboBoxModel = new DefaultComboBoxModel<String>();
	}
	// remember cuurent solver so we can put it back as the selected one after creating the list
	// otherwise, iterating while adding elements will fire events that will change it on the TornoffSolverTaskDescription...
	SolverDescription currentSolverDescription = null;
	if (newSolverTaskDescription != null && newSolverTaskDescription.getSolverDescription() != null) {
		currentSolverDescription = newSolverTaskDescription.getSolverDescription();
	}
	//
	fieldSolverComboBoxModel.removeAllElements();
	if(getSolverTaskDescription() != null) {
		MathDescription mathDescription = getSolverTaskDescription().getSimulation().getMathDescription();
		for (SolverDescription sd : SolverDescription.getSupportingSolverDescriptions(mathDescription)) {
			if (!sd.deprecated) {
				fieldSolverComboBoxModel.addElement(sd.getDisplayLabel());
			}
		}
	}
	//
	if (currentSolverDescription != null) {
		fieldSolverComboBoxModel.setSelectedItem(currentSolverDescription.getDisplayLabel());
	}
	return (fieldSolverComboBoxModel);
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
			ivjJLabelTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 0));
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

private StochSimOptionsPanel getStochSimOptionsPanel() {
	if (stochSimOptionsPanel == null) {
		try {
			stochSimOptionsPanel = new StochSimOptionsPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return stochSimOptionsPanel;
}

private NFSimSimulationOptionsPanel getNFSimSimulationOptionsPanel() {
	if (nfsimSimulationOptionsPanel == null) {
		try {
			nfsimSimulationOptionsPanel = new NFSimSimulationOptionsPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return nfsimSimulationOptionsPanel;
}

private SmoldynSimulationOptionsPanel getSmoldynSimulationOptionsPanel() {
	if (smoldynSimulationOptionsPanel == null) {
		try {
			smoldynSimulationOptionsPanel = new SmoldynSimulationOptionsPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return smoldynSimulationOptionsPanel;
}

private SundialsPdeSolverOptionsPanel getSundialsPdeSolverOptionsPanel() {
	if (sundialsPdeSolverOptionsPanel == null) {
		try {
			sundialsPdeSolverOptionsPanel = new SundialsPdeSolverOptionsPanel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return sundialsPdeSolverOptionsPanel;
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
	serialParameterScanCheckBox.addItemListener(ivjEventHandler);
	timeoutDisabledCheckBox.addItemListener(ivjEventHandler);
	timeoutDisabledHelpButton.addActionListener(ivjEventHandler);
	sensitivityAnalysisComboBox.addActionListener(ivjEventHandler);
	performSensitivityAnalysisCheckBox.addActionListener(ivjEventHandler);
	sensitivityAnalysisHelpButton.addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP7SetTarget();
	connPtoP2SetTarget();
}

private ChomboTimeBoundsPanel getChomboTimeBoundsPanel()
{
	if (chomboTimeBoundsPanel == null) {
		chomboTimeBoundsPanel = new ChomboTimeBoundsPanel();
		chomboTimeBoundsPanel.setVisible(false);
	}

	return chomboTimeBoundsPanel;
}

private JPanel getGeneralAndDeverloperToolsPanel()
{
	JPanel panel = new JPanel(new GridBagLayout());
	
	GridBagConstraints gbc = new java.awt.GridBagConstraints();
	gbc.gridx = 0; 
	gbc.gridy = 0;
	gbc.fill = java.awt.GridBagConstraints.BOTH;
	gbc.weightx = 1.0;
	gbc.weighty = 1.0;
	gbc.insets = new java.awt.Insets(4, 4, 4, 4);
	panel.add(getGeneralOptionsPanel(), gbc);
	
	gbc = new java.awt.GridBagConstraints();
	gbc.gridx = 1; 
	gbc.gridy = 0;
	gbc.fill = java.awt.GridBagConstraints.BOTH;
	gbc.weightx = 1.0;
	gbc.weighty = 1.0;
	gbc.insets = new java.awt.Insets(4, 4, 4, 4);
	panel.add(getChomboDeveloperToolsPanel(), gbc);
	
	return panel;
}

private CollapsiblePanel getGeneralOptionsPanel() {
	if (generalOptionsPanel == null) {
		generalOptionsPanel = new CollapsiblePanel("General");
		generalOptionsPanel.getContentPanel().setLayout(new GridBagLayout());

		java.awt.GridBagConstraints constraintsTimeBoundsPanel = new java.awt.GridBagConstraints();
		constraintsTimeBoundsPanel.gridx = 0;
		constraintsTimeBoundsPanel.gridy = 0;
		constraintsTimeBoundsPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeBoundsPanel.weightx = 1.0;
		constraintsTimeBoundsPanel.weighty = 1.0;
		constraintsTimeBoundsPanel.gridwidth = 2;
		constraintsTimeBoundsPanel.insets = new java.awt.Insets(3, 4, 4, 4);
		generalOptionsPanel.getContentPanel().add(getTimeBoundsPanel(), constraintsTimeBoundsPanel);

		timeoutDisabledCheckBox = new JCheckBox("Disable Simulation Run Timeout");
		timeoutDisabledCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(3, 6, 5, 0);
		generalOptionsPanel.getContentPanel().add(timeoutDisabledCheckBox, gbc);
		
		timeoutDisabledHelpButton = new JButton("<html><b>&nbsp;&nbsp;?&nbsp;&nbsp;</b></html>");
		Font font = timeoutDisabledHelpButton.getFont().deriveFont(Font.BOLD);
		Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		timeoutDisabledHelpButton.setFont(font);
		timeoutDisabledHelpButton.setBorder(border);
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(3, 0, 6, 6);
		generalOptionsPanel.getContentPanel().add(timeoutDisabledHelpButton, gbc);
		
		java.awt.GridBagConstraints constraintsTimeStepPanel = new java.awt.GridBagConstraints();
		constraintsTimeStepPanel.gridx = 2;
		constraintsTimeStepPanel.gridy = 0;
		constraintsTimeStepPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeStepPanel.weightx = 1.0;
		constraintsTimeStepPanel.weighty = 1.0;
		constraintsTimeStepPanel.gridheight = 2;
		constraintsTimeStepPanel.insets = new java.awt.Insets(3, 4, 4, 4);
		generalOptionsPanel.getContentPanel().add(getTimeStepPanel(), constraintsTimeStepPanel);

		java.awt.GridBagConstraints constraintsErrorTolerancePanel = new java.awt.GridBagConstraints();
		constraintsErrorTolerancePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsErrorTolerancePanel.gridx = 3;
		constraintsErrorTolerancePanel.gridy = 0;
		constraintsErrorTolerancePanel.weightx = 1.0;
		constraintsErrorTolerancePanel.weighty = 1.0;
//		constraintsErrorTolerancePanel.gridheight = 2;
		constraintsErrorTolerancePanel.insets = new java.awt.Insets(3, 4, 4, 4);
		constraintsErrorTolerancePanel.anchor = GridBagConstraints.FIRST_LINE_START;
		generalOptionsPanel.getContentPanel().add(getErrorTolerancePanel(), constraintsErrorTolerancePanel);
	}
	return generalOptionsPanel;
}

private CollapsiblePanel getSensitivityAnalysisPanel() {
	if (sensitivityAnalysisCollapsiblePanel == null) {
		sensitivityAnalysisComboBox = new javax.swing.JComboBox();
		performSensitivityAnalysisCheckBox = new JCheckBox("Perform sensitivity analysis");
		sensitivityAnalysisHelpButton = new JButton(" ? ");
		Font font = sensitivityAnalysisHelpButton.getFont().deriveFont(Font.BOLD);
		Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		sensitivityAnalysisHelpButton.setBorder(border);
		sensitivityAnalysisHelpButton.setFont(font);

		sensitivityAnalysisCollapsiblePanel = new CollapsiblePanel("Local Sensitivity Analysis", false);
		sensitivityAnalysisCollapsiblePanel.getContentPanel().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		sensitivityAnalysisCollapsiblePanel.getContentPanel().add(performSensitivityAnalysisCheckBox, gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		sensitivityAnalysisCollapsiblePanel.getContentPanel().add(sensitivityAnalysisHelpButton, gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		sensitivityAnalysisCollapsiblePanel.getContentPanel().add(sensitivityAnalysisComboBox, gbc);
	}
	return sensitivityAnalysisCollapsiblePanel;
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setLayout(new java.awt.GridBagLayout());
		setSize(607, 419);

		int gridy = 0;
		java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
		constraintsJLabelTitle.gridx = 0;
		constraintsJLabelTitle.gridy = gridy;
		constraintsJLabelTitle.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelTitle.insets = new java.awt.Insets(4, 4, 0, 4);
		add(getJLabelTitle(), constraintsJLabelTitle);

		gridy ++;
		java.awt.GridBagConstraints constraintsPanel2 = new java.awt.GridBagConstraints();
		constraintsPanel2.gridx = 0;
		constraintsPanel2.gridy = 1;
		constraintsPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPanel2.weightx = 1.0;
		constraintsPanel2.insets = new java.awt.Insets(0, 4, 3, 4);
		add(getSolverPanel(), constraintsPanel2);

		gridy ++;
		java.awt.GridBagConstraints gbc1 = new java.awt.GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = gridy;
		gbc1.fill = java.awt.GridBagConstraints.BOTH;
		gbc1.weightx = 1.0;
		gbc1.weighty = 1.0;
		gbc1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getChomboTimeBoundsPanel(), gbc1);

		gridy ++;
		gbc1 = new java.awt.GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = gridy;
		gbc1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc1.weightx = 1.0;
		gbc1.insets = new java.awt.Insets(3, 4, 3, 4);
		add(getGeneralAndDeverloperToolsPanel(), gbc1);
		
		gridy ++;
		GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSensitivityAnalysisPanel(), gbc);

		gridy ++;
		java.awt.GridBagConstraints constraintsJPanelStoch = new java.awt.GridBagConstraints();
		constraintsJPanelStoch.gridx = 0;
		constraintsJPanelStoch.gridy = gridy;
		constraintsJPanelStoch.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelStoch.weightx = 1.0;
		constraintsJPanelStoch.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getStochSimOptionsPanel(), constraintsJPanelStoch);

		gridy ++;
		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0;
		constraintsJPanel1.gridy = gridy;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getOutputOptionsPanel(), constraintsJPanel1);

		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSundialsPdeSolverOptionsPanel(), gbc);

		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getNFSimSimulationOptionsPanel(), gbc);

		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSmoldynSimulationOptionsPanel(), gbc);
		
		gridy ++;
		movingBoundarySolverOptionsPanel = new MovingBoundarySolverOptionsPanel();
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(movingBoundarySolverOptionsPanel, gbc);

		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getMiscPanel(), gbc);

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
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

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
		frame.pack();
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
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
			connEtoC6(ivjTornOffSolverTaskDescription);
			getTimeStepPanel().setSolverTaskDescription(ivjTornOffSolverTaskDescription);
			getErrorTolerancePanel().setSolverTaskDescription(ivjTornOffSolverTaskDescription);
			getStochSimOptionsPanel().setSolverTaskDescription(ivjTornOffSolverTaskDescription);
			getNFSimSimulationOptionsPanel().setSolverTaskDescription(ivjTornOffSolverTaskDescription);
			getSmoldynSimulationOptionsPanel().setSolverTaskDescription(ivjTornOffSolverTaskDescription);
			getOutputOptionsPanel().setSolverTaskDescription(ivjTornOffSolverTaskDescription, unitInfo);
			getSundialsPdeSolverOptionsPanel().setSolverTaskDescription(ivjTornOffSolverTaskDescription);
			stopAtSpatiallyUniformPanel.setSolverTaskDescription(ivjTornOffSolverTaskDescription);
			dataProcessingInstructionPanel.setSolverTaskDescription(ivjTornOffSolverTaskDescription);
			getTimeBoundsPanel().setTimeBounds(getTornOffSolverTaskDescription().getTimeBounds());
			getChomboTimeBoundsPanel().setSolverTaskDescription(getTornOffSolverTaskDescription());
			getChomboDeveloperToolsPanel().setSolverTaskDescription(getTornOffSolverTaskDescription());
			movingBoundarySolverOptionsPanel.setSolverTaskDescription(getTornOffSolverTaskDescription());
			updateSensitivityAnalysisComboBox();
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
private void refresh() {
	if (getSolverTaskDescription() == null) {
		return;
	}
	SolverDescription solverDescription = getSolverTaskDescription().getSolverDescription();
	if (solverDescription == null){
		getSolverComboBox().setEnabled(false);
	}else{
		getSolverComboBox().setEnabled(true);
		//
		// if already selected, don't reselect (break the loop of events)
		//
		if (getSolverComboBox().getSelectedItem()==null || !getSolverComboBox().getSelectedItem().equals(solverDescription.getDisplayLabel())){
			if (getSolverComboBox().getModel().getSize()>0){
				getSolverComboBox().setSelectedItem(solverDescription.getDisplayLabel());
			}
		}
	}
	Set<SolverFeature> supportedFeatures = ivjTornOffSolverTaskDescription.getSolverDescription().getSupportedFeatures();
	if (supportedFeatures.contains(SolverFeature.Feature_SerialParameterScans)) {
		serialParameterScanCheckBox.setVisible(true);
		boolean bSerialParameterScan = ivjTornOffSolverTaskDescription.isSerialParameterScan();
		if (bSerialParameterScan) {
			serialParameterScanCheckBox.setSelected(bSerialParameterScan);
		}
	} else {
		serialParameterScanCheckBox.setVisible(false);
	}
	
	timeoutDisabledCheckBox.setVisible(true);
	timeoutDisabledCheckBox.setEnabled(false);
	boolean bTimeoutDisabled = ivjTornOffSolverTaskDescription.isTimeoutDisabled();
	timeoutDisabledCheckBox.setSelected(bTimeoutDisabled);
	
	managePanels(); // sensitivity panel's visibility
	getMiscPanel().setVisible(supportedFeatures.contains(SolverFeature.Feature_SerialParameterScans)
			|| supportedFeatures.contains(SolverFeature.Feature_StopAtSpatiallyUniform)
			|| supportedFeatures.contains(SolverFeature.Feature_DataProcessingInstructions));
	if (getSolverTaskDescription().getSolverDescription().isChomboSolver())
	{
		getTimeBoundsPanel().setVisible(false);
		getTimeStepPanel().setVisible(false);
		getChomboTimeBoundsPanel().setVisible(true);
		getChomboDeveloperToolsPanel().setVisible(true);
	}
	else
	{
		getTimeBoundsPanel().setVisible(true);
		getTimeStepPanel().setVisible(true);
		getChomboTimeBoundsPanel().setVisible(false);
		getChomboDeveloperToolsPanel().setVisible(false);
	}
	movingBoundarySolverOptionsPanel.setVisible(getSolverTaskDescription().getSolverDescription().isMovingBoundarySolver());
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
	DialogUtils.showInfoDialog(this, "Solver Help", getTornOffSolverTaskDescription().getSolverDescription().getFullDescription());
}

private void onPropertyChange_solverDescription() {
	try {
		refresh();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private CollapsiblePanel getMiscPanel() {
	if (miscPanel == null) {
		miscPanel = new CollapsiblePanel("Miscellaneous", false);
		miscPanel.setName("MiscPanel");
		miscPanel.getContentPanel().setLayout(new java.awt.GridBagLayout());

		int gridy = 0;
		// 0
		serialParameterScanCheckBox = new JCheckBox("Run Parameter Scan Serially");
		GridBagConstraints gridbag1 = new java.awt.GridBagConstraints();
		gridbag1.gridx = 0;
		gridbag1.gridy = gridy;
		gridbag1.weightx = 1.0;
		gridbag1.fill = GridBagConstraints.HORIZONTAL;
		gridbag1.insets = new java.awt.Insets(0, 0, 5, 0);
		miscPanel.getContentPanel().add(serialParameterScanCheckBox, gridbag1);

		// 1
		gridy ++;
		stopAtSpatiallyUniformPanel = new StopAtSpatiallyUniformPanel();
		gridbag1 = new java.awt.GridBagConstraints();
		gridbag1.gridx = 0;
		gridbag1.gridy = gridy;
		gridbag1.fill = GridBagConstraints.HORIZONTAL;
		gridbag1.weightx = 1.0;
		gridbag1.insets = new java.awt.Insets(0, 0, 5, 0);
		miscPanel.getContentPanel().add(stopAtSpatiallyUniformPanel, gridbag1);

		// 2
		gridy ++;
		dataProcessingInstructionPanel = new DataProcessingInstructionPanel();
		gridbag1 = new java.awt.GridBagConstraints();
		gridbag1.gridx = 0;
		gridbag1.gridy = gridy;
		gridbag1.fill = GridBagConstraints.HORIZONTAL;
		gridbag1.weightx = 1.0;
		gridbag1.insets = new java.awt.Insets(0, 0, 0, 10);
		miscPanel.getContentPanel().add(dataProcessingInstructionPanel, gridbag1);
	}
	return miscPanel;
}

private void updateSensitivityAnalysisComboBox() {
	//Inhibit actionEvents from ComboBox during comboBoxModel update.
	sensitivityAnalysisComboBox.removeActionListener(ivjEventHandler);
	//
	try{
		//clear comboBoxModel
		((javax.swing.DefaultComboBoxModel)(sensitivityAnalysisComboBox.getModel())).removeAllElements();
		//
		if (getSolverTaskDescription() != null && getSolverTaskDescription().getSimulation() != null) {
			MathDescription mathDescription = getSolverTaskDescription().getSimulation().getMathDescription();
			if (mathDescription != null) {
				Enumeration<Constant> enum1 = mathDescription.getConstants();
				if (enum1.hasMoreElements()){
					((javax.swing.DefaultComboBoxModel)(sensitivityAnalysisComboBox.getModel())).addElement(SELECT_PARAMETER);
				}

				//Sort Constants, ignore case
				TreeSet<String> sortedConstants = new TreeSet<String>(
					new Comparator<String>(){
						public int compare(String o1, String o2){
							int ignoreCaseB = o1.compareToIgnoreCase(o2);
							if (ignoreCaseB == 0){
								return o1.compareTo(o2);
							}
							return ignoreCaseB;
						}
					}
				);
				while (enum1.hasMoreElements()) {
					Constant constant = (Constant) enum1.nextElement();
					sortedConstants.add(constant.getName());
				}
				String[] sortedConstantsArr = new String[sortedConstants.size()];
				sortedConstants.toArray(sortedConstantsArr);
				for(int i=0;i<sortedConstantsArr.length;i+= 1){
					((javax.swing.DefaultComboBoxModel)(sensitivityAnalysisComboBox.getModel())).addElement(sortedConstantsArr[i]);
				}
			}
		}
	}finally{
		updateSensitivityParameterDisplay((getSolverTaskDescription() != null ? getSolverTaskDescription().getSensitivityParameter() : null));
		//Re-activate actionEvents on ComboBox
		sensitivityAnalysisComboBox.addActionListener(ivjEventHandler);
	}
}

private void updateSensitivityParameterDisplay(Constant sensParam) {
	Simulation simulation = getTornOffSolverTaskDescription().getSimulation();
	if(simulation.isSpatial() || simulation.getMathDescription().isNonSpatialStoch()) {
		sensitivityAnalysisComboBox.setVisible(false);
		performSensitivityAnalysisCheckBox.setVisible(false);
	} else {
		sensitivityAnalysisComboBox.setVisible(true);
		performSensitivityAnalysisCheckBox.setVisible(true);
	}
	if (sensParam == null){
		if (performSensitivityAnalysisCheckBox.isSelected()){
			performSensitivityAnalysisCheckBox.setSelected(false);
		}
		sensitivityAnalysisComboBox.setEnabled(false);
	}else{
		sensitivityAnalysisCollapsiblePanel.expand(true);
		if (!performSensitivityAnalysisCheckBox.isSelected()){
			performSensitivityAnalysisCheckBox.setSelected(true);
		}
		sensitivityAnalysisComboBox.setEnabled(true);
		if (sensitivityAnalysisComboBox.getModel().getSize()>0){
			sensitivityAnalysisComboBox.setSelectedItem(sensParam.getName());
		}
	}
}

private Constant getSelectedSensitivityParameter(String constantName) {
	if (getSolverTaskDescription()!=null &&
		getSolverTaskDescription().getSimulation()!=null &&
		getSolverTaskDescription().getSimulation().getMathDescription()!=null){

		if (constantName != null){
			Enumeration<Constant> enum1 = getSolverTaskDescription().getSimulation().getMathDescription().getConstants();
			while (enum1.hasMoreElements()){
				Constant constant = enum1.nextElement();
				if (constant.getName().equals(constantName)){
					return constant;
				}
			}
		}
	}
	return null;

}

private void sensitivityAnalysisComboBox_actionPerformed() {
	try {
		getSolverTaskDescription().setSensitivityParameter(getSelectedSensitivityParameter((String)sensitivityAnalysisComboBox.getSelectedItem()));
	} catch (Exception ex) {
		DialogUtils.showErrorDialog(this, ex.getMessage(), ex);
	}
}

private void performSensitivityAnalysisCheckbox_actionPerformed() {
	try {
		if (performSensitivityAnalysisCheckBox.isSelected()){
			sensitivityAnalysisComboBox.setEnabled(true);
			getSolverTaskDescription().setSensitivityParameter(getSelectedSensitivityParameter((String)sensitivityAnalysisComboBox.getSelectedItem()));
		} else {
			sensitivityAnalysisComboBox.setEnabled(false);
			getSolverTaskDescription().setSensitivityParameter(null);
		}
	} catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
	}
}

public void showSensitivityAnalysisHelp(){
	DialogUtils.showInfoDialog(this, "Sensitivity Analysis Help", VCellErrorMessages.SensitivityAnalysis_Help);
}

public void setUnitInfo(UnitInfo unitInfo) {
	this.unitInfo = unitInfo;
}

private ChomboDeveloperToolsPanel getChomboDeveloperToolsPanel() {
	if (chomboDeveloperToolsPanel == null) {
		try {
			chomboDeveloperToolsPanel = new ChomboDeveloperToolsPanel();
			chomboDeveloperToolsPanel.setName("TimeBoundsPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return chomboDeveloperToolsPanel;
}

	private void validateChomboExtentAR(SolverDescription solverDescription) throws ChomboInvalidGeometryException {
		if (solverDescription != null && solverDescription.isChomboSolver()) {
			Simulation sim = fieldSolverTaskDescription.getSimulation();
			Geometry geometry = sim.getMathDescription().getGeometry();
			ChomboMeshValidator meshValidator = fieldSolverTaskDescription.getChomboSolverSpec() == null 
					? new ChomboMeshValidator(geometry.getDimension(), geometry.getExtent(), ChomboSolverSpec.DEFAULT_BLOCK_FACTOR)
					: new ChomboMeshValidator(geometry, fieldSolverTaskDescription.getChomboSolverSpec()); 
			ChomboMeshRecommendation chomboMeshRecommendation = meshValidator.computeMeshSpecs();
			if (!chomboMeshRecommendation.validate())
			{
				String option = DialogUtils.showWarningDialog(this, "Warning", chomboMeshRecommendation.getErrorMessage(), chomboMeshRecommendation.getDialogOptions(), ChomboMeshRecommendation.optionClose);
				if (ChomboMeshRecommendation.optionSuggestions.equals(option))
				{
					DialogUtils.showInfoDialog(this, ChomboMeshRecommendation.optionSuggestions, chomboMeshRecommendation.getMeshSuggestions());
				}
				throw new ChomboInvalidGeometryException(chomboMeshRecommendation);
			}
		}
	}
}
