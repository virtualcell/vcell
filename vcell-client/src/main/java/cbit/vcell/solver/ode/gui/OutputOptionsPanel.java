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

import java.awt.GridBagConstraints;
import java.beans.PropertyVetoException;
import java.util.Objects;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.vcell.chombo.gui.ChomboOutputOptionsPanel;
import org.vcell.util.BeanUtils;
import org.vcell.util.NumberUtils;
import org.vcell.util.Range;
import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SimulationOwner.UnitInfo;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;

@SuppressWarnings("serial")
public class OutputOptionsPanel extends CollapsiblePanel {
	private javax.swing.JTextField ivjOutputTimesTextField = null;
	private javax.swing.JRadioButton ivjDefaultOutputRadioButton = null;
	private javax.swing.JRadioButton ivjExplicitOutputRadioButton = null;
	private javax.swing.JRadioButton ivjUniformOutputRadioButton = null;
	private javax.swing.JPanel ivjDefaultOutputPanel = null;
	private javax.swing.JPanel ivjExplicitOutputPanel = null;
	private javax.swing.JPanel ivjUniformOutputPanel = null;
	private javax.swing.JTextField ivjOutputTimeStepTextField = null;
	private javax.swing.JLabel ivjTimeStepUnitsLabel = null;
	
	private javax.swing.JTextField ivjKeepEveryTextField = null;	
	private javax.swing.JTextField ivjKeepAtMostTextField = null;
	private javax.swing.JLabel ivjKeepAtMostLabel = null;
	
	private javax.swing.ButtonGroup ivjbuttonGroup1 = null;
	
	private javax.swing.JLabel ivjPointsLabel = null;
	
	private SolverTaskDescription solverTaskDescription = null;
	private ChomboOutputOptionsPanel chomboOutputOptionsPanel = null;
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == OutputOptionsPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) {
				refresh();
			}
			if (evt.getSource() == solverTaskDescription && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION))) {
				refresh();
			}
			if (evt.getSource() == solverTaskDescription && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_TIME_STEP))) {
				refresh();
			}
			if (evt.getSource() == solverTaskDescription && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_TIME_BOUNDS))) {  
				onPropertyChange_TimeBounds();			
			}
			if (evt.getSource() == solverTaskDescription && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_OUTPUT_TIME_SPEC))) {
				refresh();
			}
			if (evt.getSource() == solverTaskDescription && evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_STOCH_SIM_OPTIONS)) {
				refresh();
			}
			if (evt.getSource() == solverTaskDescription && evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_NFSIM_SIMULATION_OPTIONS)) {
				refresh();
			}
		}
		
		public void actionPerformed(java.awt.event.ActionEvent e) {			
			if (e.getSource() == getDefaultOutputRadioButton() ||
					e.getSource() == getUniformOutputRadioButton() ||
					e.getSource() == getExplicitOutputRadioButton()) { 
					actionOutputOptionButtonState(e);
			}
		}
		
		public void focusGained(java.awt.event.FocusEvent e) {
		}

		/**
		 * Method to handle events for the FocusListener interface.
		 * @param e java.awt.event.FocusEvent
		 */
		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.isTemporary()) {
				return;
			}
			if (e.getSource() == getOutputTimeStepTextField() ||				
				e.getSource() == getKeepEveryTextField() ||
				e.getSource() == getKeepAtMostTextField()|| 
				e.getSource() == getOutputTimesTextField()) {
				setNewOutputOption();
			}
		}
	}
	public OutputOptionsPanel() {
		super("Output Options");
		addPropertyChangeListener(ivjEventHandler);
		initialize();
	}
	
	/**
	 * Return the Panel3 property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getDefaultOutputPanel() {
		if (ivjDefaultOutputPanel == null) {
			try {
				ivjDefaultOutputPanel = new javax.swing.JPanel();
				ivjDefaultOutputPanel.setName("DefaultOutputPanel");
				ivjDefaultOutputPanel.setOpaque(false);
				ivjDefaultOutputPanel.setLayout(new java.awt.GridBagLayout());

				java.awt.GridBagConstraints constraintsKeepAtMostLabel = new java.awt.GridBagConstraints();
				constraintsKeepAtMostLabel.gridx = 2; constraintsKeepAtMostLabel.gridy = 0;
				constraintsKeepAtMostLabel.insets = new java.awt.Insets(3, 4, 3, 4);
				getDefaultOutputPanel().add(getKeepAtMostLabel(), constraintsKeepAtMostLabel);

				java.awt.GridBagConstraints constraintsKeepEveryTextField = new java.awt.GridBagConstraints();
				constraintsKeepEveryTextField.gridx = 0; constraintsKeepEveryTextField.gridy = 0;
				constraintsKeepEveryTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsKeepEveryTextField.insets = new java.awt.Insets(3, 4, 3, 4);
				getDefaultOutputPanel().add(getKeepEveryTextField(), constraintsKeepEveryTextField);


				java.awt.GridBagConstraints constraintsKeepAtMostTextField = new java.awt.GridBagConstraints();
				constraintsKeepAtMostTextField.gridx = 3; constraintsKeepAtMostTextField.gridy = 0;
				constraintsKeepAtMostTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsKeepAtMostTextField.insets = new java.awt.Insets(3, 4, 3, 4);
				getDefaultOutputPanel().add(getKeepAtMostTextField(), constraintsKeepAtMostTextField);

				java.awt.GridBagConstraints constraintsPointsLabel = new java.awt.GridBagConstraints();
				constraintsPointsLabel.gridx = 4; constraintsPointsLabel.gridy = 0;
				constraintsPointsLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsPointsLabel.weightx = 1.0;
				constraintsPointsLabel.insets = new java.awt.Insets(3, 4, 3, 4);
				getDefaultOutputPanel().add(getPointsLabel(), constraintsPointsLabel);

				java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
				constraintsJLabel4.gridx = 1; constraintsJLabel4.gridy = 0;
				constraintsJLabel4.insets = new java.awt.Insets(3, 4, 3, 4);
				JLabel jlabel4 = new javax.swing.JLabel("time samples");
				getDefaultOutputPanel().add(jlabel4, constraintsJLabel4);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDefaultOutputPanel;
	}
	
	/**
	 * Return the PointsLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getPointsLabel() {
		if (ivjPointsLabel == null) {
			try {
				ivjPointsLabel = new javax.swing.JLabel();
				ivjPointsLabel.setName("PointsLabel");
				ivjPointsLabel.setText("time samples");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjPointsLabel;
	}
	
	/**
	 * Return the Panel4 property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getUniformOutputPanel() {
		if (ivjUniformOutputPanel == null) {
			try {
				ivjUniformOutputPanel = new javax.swing.JPanel();
				ivjUniformOutputPanel.setName("UniformOutputPanel");
				ivjUniformOutputPanel.setLayout(new java.awt.GridBagLayout());

				java.awt.GridBagConstraints constraintsTimeStepUnitsLabel = new java.awt.GridBagConstraints();
				constraintsTimeStepUnitsLabel.gridx = 1; constraintsTimeStepUnitsLabel.gridy = 0;
				constraintsTimeStepUnitsLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsTimeStepUnitsLabel.weightx = 1.0;
				constraintsTimeStepUnitsLabel.fill = GridBagConstraints.HORIZONTAL;
				constraintsTimeStepUnitsLabel.insets = new java.awt.Insets(3, 4, 3, 4);
				getUniformOutputPanel().add(getTimeStepUnitsLabel(), constraintsTimeStepUnitsLabel);

				java.awt.GridBagConstraints constraintsOutputTimeStepTextField = new java.awt.GridBagConstraints();
				constraintsOutputTimeStepTextField.gridx = 0; constraintsOutputTimeStepTextField.gridy = 0;
				constraintsOutputTimeStepTextField.insets = new java.awt.Insets(3, 4, 3, 4);
				getUniformOutputPanel().add(getOutputTimeStepTextField(), constraintsOutputTimeStepTextField);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUniformOutputPanel;
	}

	/**
	 * Return the JRadioButton2 property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getUniformOutputRadioButton() {
		if (ivjUniformOutputRadioButton == null) {
			try {
				ivjUniformOutputRadioButton = new javax.swing.JRadioButton();
				ivjUniformOutputRadioButton.setName("UniformOutputRadioButton");
				ivjUniformOutputRadioButton.setText("Output Interval");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUniformOutputRadioButton;
	}
	
	/**
	 * Return the TimeStepUnitsLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getTimeStepUnitsLabel() {
		if (ivjTimeStepUnitsLabel == null) {
			try {
				ivjTimeStepUnitsLabel = new javax.swing.JLabel();
				ivjTimeStepUnitsLabel.setName("TimeStepUnitsLabel");
				ivjTimeStepUnitsLabel.setPreferredSize(new java.awt.Dimension(28, 14));
				ivjTimeStepUnitsLabel.setText("secs");
				ivjTimeStepUnitsLabel.setMaximumSize(new java.awt.Dimension(200, 14));
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjTimeStepUnitsLabel;
	}

	/**
	 * Return the TimeStepTextField property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getOutputTimeStepTextField() {
		if (ivjOutputTimeStepTextField == null) {
			try {
				ivjOutputTimeStepTextField = new javax.swing.JTextField();
				ivjOutputTimeStepTextField.setName("OutputTimeStepTextField");
				ivjOutputTimeStepTextField.setMinimumSize(new java.awt.Dimension(60, 20));
				ivjOutputTimeStepTextField.setColumns(10);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjOutputTimeStepTextField;
	}

	/**
	 * Return the JTextField1 property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getOutputTimesTextField() {
		if (ivjOutputTimesTextField == null) {
			try {
				ivjOutputTimesTextField = new javax.swing.JTextField();
				ivjOutputTimesTextField.setName("OutputTimesTextField");
				ivjOutputTimesTextField.setColumns(20);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjOutputTimesTextField;
	}
	
	/**
	 * Return the JPanel2 property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getExplicitOutputPanel() {
		if (ivjExplicitOutputPanel == null) {
			try {
				ivjExplicitOutputPanel = new javax.swing.JPanel();
				ivjExplicitOutputPanel.setName("ExplicitOutputPanel");
				ivjExplicitOutputPanel.setLayout(new java.awt.GridBagLayout());

				java.awt.GridBagConstraints constraintsOutputTimesTextField = new java.awt.GridBagConstraints();
				constraintsOutputTimesTextField.gridx = 0; constraintsOutputTimesTextField.gridy = 0;
				constraintsOutputTimesTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsOutputTimesTextField.weightx = 1.0;
				constraintsOutputTimesTextField.insets = new java.awt.Insets(3, 4, 3, 4);
				ivjExplicitOutputPanel.add(getOutputTimesTextField(), constraintsOutputTimesTextField);

				java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
				constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 1;
				constraintsJLabel3.gridwidth = 2;
				constraintsJLabel3.insets = new java.awt.Insets(3, 4, 3, 4);
				JLabel jlabel3 = new javax.swing.JLabel("(Comma or space separated numbers, e.g. 0.5, 0.8, 1.2, 1.7)");
				ivjExplicitOutputPanel.add(jlabel3, constraintsJLabel3);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjExplicitOutputPanel;
	}

	/**
	 * Return the JRadioButton3 property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getExplicitOutputRadioButton() {
		if (ivjExplicitOutputRadioButton == null) {
			try {
				ivjExplicitOutputRadioButton = new javax.swing.JRadioButton();
				ivjExplicitOutputRadioButton.setName("ExplicitOutputRadioButton");
				ivjExplicitOutputRadioButton.setText("Output Times");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjExplicitOutputRadioButton;
	}
	
	/**
	 * Return the buttonGroup1 property value.
	 * @return javax.swing.ButtonGroup
	 */
	private javax.swing.ButtonGroup getbuttonGroup1() {
		if (ivjbuttonGroup1 == null) {
			try {
				ivjbuttonGroup1 = new javax.swing.ButtonGroup();
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjbuttonGroup1;
	}

	private void initialize() {
		try {
			getContentPanel().setLayout(new java.awt.GridBagLayout());
	
	     	// 0
			java.awt.GridBagConstraints constraintsDefaultOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsDefaultOutputRadioButton.gridx = 0; 
			constraintsDefaultOutputRadioButton.gridy = 0;
			constraintsDefaultOutputRadioButton.insets = new java.awt.Insets(3, 4, 1, 4);
			constraintsDefaultOutputRadioButton.anchor = GridBagConstraints.LINE_START;
			getContentPanel().add(getDefaultOutputRadioButton(), constraintsDefaultOutputRadioButton);
	
			java.awt.GridBagConstraints constraintsDefaultOutputPanel = new java.awt.GridBagConstraints();
			constraintsDefaultOutputPanel.gridx = 1; 
			constraintsDefaultOutputPanel.gridy = 0;
			constraintsDefaultOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDefaultOutputPanel.weightx = 1.0;
			constraintsDefaultOutputPanel.weighty = 1.0;
			constraintsDefaultOutputPanel.insets = new java.awt.Insets(3, 4, 1, 4);
			getContentPanel().add(getDefaultOutputPanel(), constraintsDefaultOutputPanel);
	
			// 1
			java.awt.GridBagConstraints constraintsUniformOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsUniformOutputRadioButton.gridx = 0; 
			constraintsUniformOutputRadioButton.gridy = 1;
			constraintsUniformOutputRadioButton.insets = new java.awt.Insets(1, 4, 2, 4);
			constraintsUniformOutputRadioButton.anchor = GridBagConstraints.LINE_START;
			getContentPanel().add(getUniformOutputRadioButton(), constraintsUniformOutputRadioButton);
	
			java.awt.GridBagConstraints constraintsUniformOutputPanel = new java.awt.GridBagConstraints();
			constraintsUniformOutputPanel.gridx = 1; 
			constraintsUniformOutputPanel.gridy = 1;
			constraintsUniformOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsUniformOutputPanel.weightx = 1.0;
			constraintsUniformOutputPanel.weighty = 1.0;
			constraintsUniformOutputPanel.insets = new java.awt.Insets(1, 4, 2, 4);
			getContentPanel().add(getUniformOutputPanel(), constraintsUniformOutputPanel);
	
			// 2
			java.awt.GridBagConstraints constraintsExplicitOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsExplicitOutputRadioButton.gridx = 0; 
			constraintsExplicitOutputRadioButton.gridy = 2;
			constraintsExplicitOutputRadioButton.insets = new java.awt.Insets(1, 4, 2, 4);
			constraintsExplicitOutputRadioButton.anchor = GridBagConstraints.FIRST_LINE_START;
			getContentPanel().add(getExplicitOutputRadioButton(), constraintsExplicitOutputRadioButton);
	
			java.awt.GridBagConstraints constraintsExplicitOutputPanel = new java.awt.GridBagConstraints();
			constraintsExplicitOutputPanel.gridx = 1; 
			constraintsExplicitOutputPanel.gridy = 2;
			constraintsExplicitOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsExplicitOutputPanel.anchor = GridBagConstraints.PAGE_START;
			constraintsExplicitOutputPanel.weightx = 1.0;
			constraintsExplicitOutputPanel.weighty = 1.0;
			constraintsExplicitOutputPanel.insets = new java.awt.Insets(0, 4, 2, 4);
			getContentPanel().add(getExplicitOutputPanel(), constraintsExplicitOutputPanel);
				
			chomboOutputOptionsPanel = new ChomboOutputOptionsPanel();
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = 3;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.PAGE_START;
			gbc.gridwidth = 2;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.insets = new java.awt.Insets(0, 4, 4, 4);
			getContentPanel().add(chomboOutputOptionsPanel, gbc);
			
			getbuttonGroup1().add(getDefaultOutputRadioButton());
			getbuttonGroup1().add(getUniformOutputRadioButton());
			getbuttonGroup1().add(getExplicitOutputRadioButton());
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}


	/**
	 * Return the JRadioButton1 property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getDefaultOutputRadioButton() {
		if (ivjDefaultOutputRadioButton == null) {
			try {
				ivjDefaultOutputRadioButton = new javax.swing.JRadioButton();
				ivjDefaultOutputRadioButton.setName("DefaultOutputRadioButton");
				ivjDefaultOutputRadioButton.setText("Keep Every");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjDefaultOutputRadioButton;
	}

	/**
	 * Return the PointsLabel property value.
	 * @return java.awt.Label
	 */
	private javax.swing.JLabel getKeepAtMostLabel() {
		if (ivjKeepAtMostLabel == null) {
			try {
				ivjKeepAtMostLabel = new javax.swing.JLabel();
				ivjKeepAtMostLabel.setName("KeepAtMostLabel");
				ivjKeepAtMostLabel.setText("and at most");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjKeepAtMostLabel;
	}

	/**
	 * Return the JTextField property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getKeepAtMostTextField() {
		if (ivjKeepAtMostTextField == null) {
			try {
				ivjKeepAtMostTextField = new javax.swing.JTextField();
				ivjKeepAtMostTextField.setName("KeepAtMostTextField");
				ivjKeepAtMostTextField.setText("");
				ivjKeepAtMostTextField.setMinimumSize(new java.awt.Dimension(66, 21));
				ivjKeepAtMostTextField.setColumns(6);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjKeepAtMostTextField;
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
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjKeepEveryTextField;
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
	
	private boolean checkExplicitOutputTimes(ExplicitOutputTimeSpec ots) {
		boolean bValid = true;
		
		double startingTime = solverTaskDescription.getTimeBounds().getStartingTime();
		double endingTime = solverTaskDescription.getTimeBounds().getEndingTime();
		double[] times = ((ExplicitOutputTimeSpec)ots).getOutputTimes();
		
		if (times[0] < startingTime || times[times.length - 1] > endingTime) {
			bValid = false;
			String ret = PopupGenerator.showWarningDialog(OutputOptionsPanel.this, 
					"Output times should be within [" + startingTime + "," + endingTime + "].\n\nChange ENDING TIME to " +
							times[times.length - 1] + "?", 
					new String[]{ UserMessage.OPTION_YES, UserMessage.OPTION_NO}, UserMessage.OPTION_YES);
			if (ret.equals(UserMessage.OPTION_YES)) {
				try {
					solverTaskDescription.setTimeBounds(new TimeBounds(startingTime, times[times.length - 1]));
					bValid = true;
				} catch (PropertyVetoException e) {
					e.printStackTrace(System.out);
					DialogUtils.showErrorDialog(OutputOptionsPanel.this, e.getMessage());
				}
			}
		}
		if (bValid) {
			getOutputTimesTextField().setBorder(UIManager.getBorder("TextField.border"));
		} else {
			getOutputTimesTextField().setBorder(GuiConstants.ProblematicTextFieldBorder);
			javax.swing.SwingUtilities.invokeLater(new Runnable() { 
			    public void run() { 
			          getOutputTimesTextField().requestFocus();
			    } 	
			});
		}		
		return bValid;
	}
	/**
	 * Comment
	 */
	private void setNewOutputOption() {
		try {
			OutputTimeSpec ots = null;
			if(getDefaultOutputRadioButton().isSelected()){
				int keepEvery = Integer.parseInt(getKeepEveryTextField().getText());
				if (solverTaskDescription.getSolverDescription().isSemiImplicitPdeSolver()) {
					ots = new DefaultOutputTimeSpec(keepEvery);
				} else {
					int keepAtMost = Integer.parseInt(getKeepAtMostTextField().getText());
					ots = new DefaultOutputTimeSpec(keepEvery, keepAtMost);
				}
			} else if(getUniformOutputRadioButton().isSelected()) {
				double outputTime = Double.parseDouble(getOutputTimeStepTextField().getText());
				ots = new UniformOutputTimeSpec(outputTime);		
			} else if (getExplicitOutputRadioButton().isSelected()) {
				ots = ExplicitOutputTimeSpec.fromString(getOutputTimesTextField().getText());
			}	

			try  {
				solverTaskDescription.setOutputTimeSpec(ots);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException(e.getMessage());
			}
		} catch (Exception e) {
			DialogUtils.showErrorDialog(this, e.getMessage(), e);
		}
	}

	/**
	 * Comment
	 */
	public void onPropertyChange_TimeBounds() {
		try {
			OutputTimeSpec ots = solverTaskDescription.getOutputTimeSpec();
			if (ots.isExplicit()) {
				checkExplicitOutputTimes((ExplicitOutputTimeSpec)ots);
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}			
	}
	
	private void actionOutputOptionButtonState(java.awt.event.ActionEvent actionEvent) {
		try {
			if (solverTaskDescription == null){
				return;
			}
	
			OutputTimeSpec outputTimeSpec = solverTaskDescription.getOutputTimeSpec();
			if(actionEvent.getSource() == getDefaultOutputRadioButton() && !outputTimeSpec.isDefault()){
				solverTaskDescription.setOutputTimeSpec(new DefaultOutputTimeSpec());
			} else if(actionEvent.getSource() == getUniformOutputRadioButton() && !outputTimeSpec.isUniform()){
				double outputTime = 0.0;
				if (solverTaskDescription.getSolverDescription().isSemiImplicitPdeSolver()) {
					String floatStr = "" + (float)(((DefaultOutputTimeSpec)outputTimeSpec).getKeepEvery() * solverTaskDescription.getTimeStep().getDefaultTimeStep());
					outputTime = Double.parseDouble(floatStr);
				} else {
					TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
					Range outputTimeRange = NumberUtils.getDecimalRange(timeBounds.getStartingTime(), timeBounds.getEndingTime()/100, true, true);
					outputTime = outputTimeRange.getMax();
				}
				solverTaskDescription.setOutputTimeSpec(new UniformOutputTimeSpec(outputTime));
			} else if(actionEvent.getSource() == getExplicitOutputRadioButton() && !outputTimeSpec.isExplicit()){
				TimeBounds timeBounds = solverTaskDescription.getTimeBounds();
				solverTaskDescription.setOutputTimeSpec(new ExplicitOutputTimeSpec(new double[]{timeBounds.getStartingTime(), timeBounds.getEndingTime()}));
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}	
	
	/**
	 * Comment
	 */
	private void refresh() {
		if (solverTaskDescription == null) {
			return;
		}
		// enables the panel where the output interval is set if the solver is IDA
		// Otherwise, that panel is disabled. 

		getUniformOutputRadioButton().setEnabled(false);
		BeanUtils.enableComponents(getUniformOutputPanel(), false);
		
		if (solverTaskDescription.getSolverDescription().equals(SolverDescription.Smoldyn) ||
				solverTaskDescription.getSolverDescription().equals(SolverDescription.NFSim) ) {
			getDefaultOutputPanel().setVisible(false);
			getDefaultOutputRadioButton().setVisible(false);
		} else if (solverTaskDescription.getSolverDescription().isChomboSolver()) {
				getDefaultOutputPanel().setVisible(false);
				getDefaultOutputRadioButton().setVisible(false);
				getUniformOutputPanel().setVisible(false);
				getUniformOutputRadioButton().setVisible(false);
		} else {
			getDefaultOutputPanel().setVisible(true);
			getDefaultOutputRadioButton().setVisible(true);
			getDefaultOutputRadioButton().setEnabled(false);
			getUniformOutputPanel().setVisible(true);
			getUniformOutputRadioButton().setVisible(true);
			BeanUtils.enableComponents(getDefaultOutputPanel(), false);
		}
		
		if (solverTaskDescription.getSimulation().getMathDescription().getGeometry().getDimension() > 0
				|| solverTaskDescription.getSimulation().getMathDescription().isNonSpatialStoch() ) {
			getExplicitOutputPanel().setVisible(false);
			getExplicitOutputRadioButton().setVisible(false);
		} else if(solverTaskDescription.getSolverDescription().isNFSimSolver() ) {
			getExplicitOutputPanel().setVisible(false);
			getExplicitOutputRadioButton().setVisible(false);
		} else {
			getExplicitOutputPanel().setVisible(true);
			getExplicitOutputRadioButton().setVisible(true);
			getExplicitOutputRadioButton().setEnabled(false);
			BeanUtils.enableComponents(getExplicitOutputPanel(), false);
		}

		if (solverTaskDescription==null || solverTaskDescription.getSolverDescription()==null){
			// if solver is not IDA, if the output Time step radio button had been set, 
			// change the setting to the 'keep every' radio button and flush the contents of the output timestep text field. 
			// Also, disable its radiobutton and fields.		
			return;
		}
		
		SolverDescription solverDesc = solverTaskDescription.getSolverDescription();
		
		//Amended June 2009, no output option for stochastic gibson multiple trials
		if(solverTaskDescription.getStochOpt()!= null && solverTaskDescription.getStochOpt().getNumOfTrials()>1
		   && solverTaskDescription.getSolverDescription().equals(SolverDescription.StochGibson))
		{
			return;
		}
		OutputTimeSpec ots = solverTaskDescription.getOutputTimeSpec();
		
		if (ots.isDefault()) {
			// if solver is not IDA, if the output Time step radio button had been set, 
			// change the setting to the 'keep every' radio button and flush the contents of the output timestep text field. 
			// Also, disable its radiobutton and fields.
			getDefaultOutputRadioButton().setSelected(true);
			getKeepEveryTextField().setText(((DefaultOutputTimeSpec)ots).getKeepEvery() + "");
			if (solverTaskDescription.getSolverDescription().isSemiImplicitPdeSolver()) {
				getKeepAtMostTextField().setText("");
			} else {
				getKeepAtMostTextField().setText(((DefaultOutputTimeSpec)ots).getKeepAtMost() + "");
			}
			getOutputTimeStepTextField().setText("");
			getOutputTimesTextField().setText("");
		} else if (ots.isUniform()) {
			getUniformOutputRadioButton().setSelected(true);
			getKeepEveryTextField().setText("");
			getKeepAtMostTextField().setText("");
			getOutputTimeStepTextField().setText(((UniformOutputTimeSpec)ots).getOutputTimeStep() + "");
			getOutputTimesTextField().setText("");
		} else if (ots.isExplicit()) {
			getExplicitOutputRadioButton().setSelected(true);
			getKeepEveryTextField().setText("");
			getKeepAtMostTextField().setText("");
			getOutputTimeStepTextField().setText("");
			getOutputTimesTextField().setText(((ExplicitOutputTimeSpec)ots).toCommaSeperatedOneLineOfString() + "");
			getOutputTimesTextField().setCaretPosition(0);
		}
		
	
		DefaultOutputTimeSpec dots = new DefaultOutputTimeSpec();
		UniformOutputTimeSpec uots = new UniformOutputTimeSpec(0.05);
		ExplicitOutputTimeSpec eots = new ExplicitOutputTimeSpec(new double[] {0.1});
		
		if (solverDesc.supports(dots)) {
			if (!solverDesc.isSemiImplicitPdeSolver() || ots.isDefault()) {
				getDefaultOutputRadioButton().setEnabled(true);
				if (getDefaultOutputRadioButton().isSelected() || ots.isDefault()) {
					BeanUtils.enableComponents(getDefaultOutputPanel(), true);
				}
			}
		}
		if (solverDesc.supports(uots)) {
			getUniformOutputRadioButton().setEnabled(true);
			if (getUniformOutputRadioButton().isSelected() || ots.isUniform()) {
				BeanUtils.enableComponents(getUniformOutputPanel(), true);
			}
		}
		if (solverDesc.supports(eots)) {
			getExplicitOutputRadioButton().setEnabled(true);
			if (getExplicitOutputRadioButton().isSelected() || ots.isExplicit()) {
				BeanUtils.enableComponents(getExplicitOutputPanel(), true);
			}
		}
		if (solverDesc.isSemiImplicitPdeSolver()){
			getKeepAtMostTextField().setText("");
			getKeepAtMostTextField().setEnabled(false);
		}
	}	

	/**
	 * set solver description. The contained {@link SolverTaskDescription#getSimulation()}
	 * is usually a temporary cloned object with  a null {@link SimulationOwner}  ; therefore
	 * we required the {@link UnitInfo} of the actual {@link Simulation} owner as a parameter
	 * @param newValue
	 * @param unitInfo not null
	 */
	public final void setSolverTaskDescription(SolverTaskDescription newValue, UnitInfo unitInfo) {
		Objects.requireNonNull(unitInfo);
		SolverTaskDescription oldValue = solverTaskDescription;
		/* Stop listening for events from the current object */
		if (oldValue != null) {
			oldValue.removePropertyChangeListener(ivjEventHandler);
		}
		solverTaskDescription = newValue;

		/* Listen for events from the new object */
		if (newValue != null) {
			newValue.addPropertyChangeListener(ivjEventHandler);
		}		
		solverTaskDescription = newValue;
		chomboOutputOptionsPanel.setSolverTaskDescription(solverTaskDescription);
		firePropertyChange("solverTaskDescription", oldValue, newValue);

		getOutputTimeStepTextField().addFocusListener(ivjEventHandler);
		getDefaultOutputRadioButton().addActionListener(ivjEventHandler);
		getUniformOutputRadioButton().addActionListener(ivjEventHandler);
		getExplicitOutputRadioButton().addActionListener(ivjEventHandler);
		getKeepEveryTextField().addFocusListener(ivjEventHandler);
		getKeepAtMostTextField().addFocusListener(ivjEventHandler);
		getOutputTimesTextField().addFocusListener(ivjEventHandler);
		
		getOutputTimeStepTextField().setInputVerifier(new InputVerifier() {

			@Override
			public boolean verify(JComponent input) {
				return false;
			}

			@Override
			public boolean shouldYieldFocus(JComponent input) {
				boolean bValid = true;
				try {
					double outputTime = Double.parseDouble(getOutputTimeStepTextField().getText());
					if (solverTaskDescription.getOutputTimeSpec().isUniform() && !solverTaskDescription.getSolverDescription().hasVariableTimestep()) {
						double timeStep = solverTaskDescription.getTimeStep().getDefaultTimeStep();

						double suggestedInterval = outputTime;
						if (outputTime < timeStep) {
							suggestedInterval = timeStep;
							bValid = false;
						} else if (!BeanUtils.isIntegerMultiple(outputTime, timeStep)) {
							double n = outputTime/timeStep;
							int intn = (int)Math.round(n);
							if (intn != n) {
								bValid = false;
								suggestedInterval = (intn * timeStep);
							}
						} 

						if (!bValid) {		
							String ret = PopupGenerator.showWarningDialog(OutputOptionsPanel.this, "Output Interval", "Output Interval must " +
									"be integer multiple of time step.\n\nChange Output Interval to " + suggestedInterval + "?", 
									new String[]{ UserMessage.OPTION_YES, UserMessage.OPTION_NO}, UserMessage.OPTION_YES);
							if (ret.equals(UserMessage.OPTION_YES)) {
								getOutputTimeStepTextField().setText(suggestedInterval + "");
								bValid = true;
							} 
						}
					}
				} catch (NumberFormatException ex) {
					DialogUtils.showErrorDialog(OutputOptionsPanel.this, "Wrong number format " + ex.getMessage().toLowerCase());
					bValid = false;
				}
				if (bValid) {
					getOutputTimeStepTextField().setBorder(UIManager.getBorder("TextField.border"));
				} else {
					getOutputTimeStepTextField().setBorder(GuiConstants.ProblematicTextFieldBorder);
					SwingUtilities.invokeLater(new Runnable() { 
						public void run() { 
							getOutputTimeStepTextField().requestFocus();
						}
					});
				}
				return bValid;
			}

		});

		getOutputTimesTextField().setInputVerifier(new InputVerifier() {

			@Override
			public boolean verify(JComponent input) {				
				return false;
			}

			@Override
			public boolean shouldYieldFocus(JComponent input) {				
				ExplicitOutputTimeSpec eots = ExplicitOutputTimeSpec.fromString(getOutputTimesTextField().getText());
				return checkExplicitOutputTimes(eots);
			}
		});	
		
		//set time label units
		JLabel timeUnitsLabel = getTimeStepUnitsLabel();
		timeUnitsLabel.setText(unitInfo.getTimeUnitString());
	}
}
