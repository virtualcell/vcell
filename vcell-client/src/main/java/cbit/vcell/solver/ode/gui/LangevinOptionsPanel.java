package cbit.vcell.solver.ode.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.vcell.util.gui.CollapsiblePanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.solver.LangevinSimulationOptions;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.SolverTaskDescription;

@SuppressWarnings("serial")
public class LangevinOptionsPanel  extends CollapsiblePanel {

	private SolverTaskDescription solverTaskDescription = null;	

	private javax.swing.JRadioButton trajectoryRadioButton = null;
	private javax.swing.JRadioButton multiRunRadioButton = null;
	private javax.swing.ButtonGroup buttonGroupTrials = null;

	private JLabel numOfTrialsLabel = null;
	private JTextField ivjJTextFieldNumOfTrials = null;
	
	private JTextField intervalImageTextField = null;
	private JTextField intervalSpringTextField = null;

	private IvjEventHandler ivjEventHandler = new IvjEventHandler();


	private class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == LangevinOptionsPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) { 
				refresh();
			}
			if (evt.getSource() == solverTaskDescription && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION))) {
				refresh();
			}
			if (evt.getSource() == solverTaskDescription && evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_LANGEVIN_SIMULATION_OPTIONS)) {
				refresh();
			}
		}
		
		public void actionPerformed(java.awt.event.ActionEvent e) {
//			if (e.getSource() == getCustomizedSeedRadioButton() || e.getSource() == getRandomSeedRadioButton()) {
//				setNewOptions();
//			}
			if (e.getSource() == getTrajectoryButton()) {
				getJTextFieldNumOfTrials().setEnabled(false);
				setNewOptions();
			} else if (e.getSource() == getMultiRunButton()) {
				getJTextFieldNumOfTrials().setEnabled(true);
				setNewOptions();
			}
		}
		
		public void focusGained(java.awt.event.FocusEvent e) {
		}

		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.isTemporary()) {
				return;
			}
			if (e.getSource() == getJTextFieldNumOfTrials()) { 
				setNewOptions();
			}
		}
	}

	public LangevinOptionsPanel() {
		super("Langevin Options Panel");
		addPropertyChangeListener(ivjEventHandler);
		initialize();		
	}
	
	private void initialize() {
		try {

			getContentPanel().setLayout(new GridBagLayout());
			
			JPanel trialPanel = new JPanel(new GridBagLayout());		// left panel
			trialPanel.setLayout(new GridBagLayout());
			JPanel centerPanel = new JPanel(new GridBagLayout());
			centerPanel.setLayout(new GridBagLayout());
			JPanel rightPanel = new JPanel(new GridBagLayout());		// fake panel, for looks
			rightPanel.setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
//			gbc.weightx = 1.0;
//			gbc.weighty = 1.0;
			gbc.insets = new Insets(1,1,1,1);
			getContentPanel().add(trialPanel, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
//			gbc.weightx = 1.0;
//			gbc.weighty = 1.0;
			gbc.insets = new Insets(1,1,1,1);
			getContentPanel().add(centerPanel, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.insets = new Insets(1,1,1,1);
			getContentPanel().add(rightPanel, gbc);

			// ----- trialPanel --------------------------------------------------------------
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth = 4;
			gbc.insets = new Insets(2,1,0,1);
			trialPanel.add(getTrajectoryButton(), gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,1,1,1);
			trialPanel.add(getMultiRunButton(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,22,1,6);
			trialPanel.add(getNumOfTrialsLabel(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			trialPanel.add(getJTextFieldNumOfTrials(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			trialPanel.add(new JLabel(""), gbc);
			
			// ----- centerPanel -----------------------------------------------------
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,26,1,5);
			centerPanel.add(new JLabel("Spring Interval"), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			centerPanel.add(getJTextFieldIntervalSpring(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(0,6,1,22);
			centerPanel.add(new JLabel("s"), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,26,1,5);
			centerPanel.add(new JLabel("Image Interval"), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			centerPanel.add(getJTextFieldIntervalImage(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(0,6,1,22);
			centerPanel.add(new JLabel("s"), gbc);

			// ----- rightPanel ----------------------------------------------------
			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.insets = new Insets(0,22,1,6);
			rightPanel.add(new JLabel(""), gbc);	// fake, just for looks

			// ----------------------------------------------------------------------
			getButtonGroupTrials().add(getTrajectoryButton());
			getButtonGroupTrials().add(getMultiRunButton());
			getButtonGroupTrials().setSelected(getTrajectoryButton().getModel(), true);

			getJTextFieldNumOfTrials().setEnabled(false);

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);		
		}
	}
	
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
	private javax.swing.JRadioButton getMultiRunButton() {
		if (multiRunRadioButton == null) {
			try {
				multiRunRadioButton = new javax.swing.JRadioButton();
				multiRunRadioButton.setName("MultiRun");
				multiRunRadioButton.setText("Multiple Runs");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return multiRunRadioButton;
	}
	private javax.swing.JTextField getJTextFieldNumOfTrials() {
		if (ivjJTextFieldNumOfTrials == null) {
			try {
				ivjJTextFieldNumOfTrials = new javax.swing.JTextField();
				ivjJTextFieldNumOfTrials.setName("JTextFieldNumOfTrials");
				ivjJTextFieldNumOfTrials.setColumns(9);
				ivjJTextFieldNumOfTrials.setText("100");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldNumOfTrials;
	}
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
	private Component getJTextFieldIntervalImage() {
		if (intervalImageTextField == null) {
			try {
				intervalImageTextField = new javax.swing.JTextField();
				intervalImageTextField.setName("IntervalImageTextField");
				intervalImageTextField.setColumns(9);
				intervalImageTextField.setText("1.00E-4");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return intervalImageTextField;
	}

	private Component getJTextFieldIntervalSpring() {
		if (intervalSpringTextField == null) {
			try {
				intervalSpringTextField = new javax.swing.JTextField();
				intervalSpringTextField.setName("IntervalSpringTextField");
				intervalSpringTextField.setColumns(9);
				intervalSpringTextField.setText("1.00E-9");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return intervalSpringTextField;
	}


	private void handleException(java.lang.Throwable exception) {
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}

	public final void setSolverTaskDescription(SolverTaskDescription newValue) {
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
		firePropertyChange("solverTaskDescription", oldValue, newValue);
		
		initConnections();
	}
	private void initConnections() {		
		getTrajectoryButton().addActionListener(ivjEventHandler);
		getMultiRunButton().addActionListener(ivjEventHandler);
		getJTextFieldNumOfTrials().addFocusListener(ivjEventHandler);
		
		
	}
	
	private void refresh() {
		if (solverTaskDescription == null) {
			return;
		}
		if(!solverTaskDescription.getSolverDescription().isLangevinSolver()) {
			setVisible(false);
			return;
		}
		setVisible(true);
		
		LangevinSimulationOptions lso = solverTaskDescription.getLangevinSimulationOptions();
		
		// TODO: temporarily disable the button
		// UNDO THIS WHEN DEVELOPMENT IS COMPLETE
		if(solverTaskDescription.getSolverDescription().isNFSimSolver()) {
			getMultiRunButton().setEnabled(false);
			return;
		}

		
	}

	private void setNewOptions() {
		if(!isVisible()) {
			return;
		}
		try {
			LangevinSimulationOptions sso = solverTaskDescription.getLangevinSimulationOptions();
		int numTrials = 1;

		if(getMultiRunButton().isSelected()) {
			numTrials = Integer.parseInt(getJTextFieldNumOfTrials().getText());
		}
		sso.setNumOfTrials(numTrials);
		
		// make a copy
		LangevinSimulationOptions lso = new LangevinSimulationOptions(sso);
		solverTaskDescription.setLangevinSimulationOptions(lso);
		
		} catch(Exception e) {
			PopupGenerator.showErrorDialog(this, e.getMessage(), e);
		}
	}
	
}
