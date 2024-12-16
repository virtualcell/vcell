package cbit.vcell.solver.ode.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;

import cbit.vcell.solver.NFsimSimulationOptions;
import org.vcell.util.gui.CollapsiblePanel;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.solver.LangevinSimulationOptions;
import cbit.vcell.solver.SolverTaskDescription;
import org.vcell.util.gui.DialogUtils;

@SuppressWarnings("serial")
public class LangevinOptionsPanel  extends CollapsiblePanel {

	private SolverTaskDescription solverTaskDescription = null;	

	private javax.swing.JRadioButton trajectoryRadioButton = null;
	private javax.swing.JRadioButton multiRunRadioButton = null;
	private javax.swing.ButtonGroup buttonGroupTrials = null;

	private JLabel numOfTrialsLabel = null;
	private JTextField ivjJTextFieldNumOfTrials = null;
	private JLabel numOfParallelLocalRuns = null;
	private JTextField ivjJTextFieldNumOfParallelLocalRuns = null;

	private JTextField numPartitionsXTextField = null;
	private JTextField numPartitionsYTextField = null;
	private JTextField numPartitionsZTextField = null;
	private JLabel numPartitionsXLabel = null;
	private JLabel numPartitionsYLabel = null;
	private JLabel numPartitionsZLabel = null;
	private JLabel numPartitionsLabel = null;

	private JCheckBox randomSeedCheckBox;
	private JTextField randomSeedTextField;
	private JButton randomSeedHelpButton = null;


//	private int[] npart = {LangevinSimulationOptions.DefaultNPart[0], LangevinSimulationOptions.DefaultNPart[1], LangevinSimulationOptions.DefaultNPart[2]};	// number of partitions on each axis

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
				getJTextFieldNumOfParallelLocalRuns().setEnabled(false);
				setNewOptions();
			} else if (e.getSource() == getMultiRunButton()) {
				getJTextFieldNumOfTrials().setEnabled(true);
				getJTextFieldNumOfParallelLocalRuns().setEnabled(true);
				setNewOptions();
			} else if(e.getSource() == randomSeedCheckBox) {
				randomSeedTextField.setEditable(randomSeedCheckBox.isSelected());
				if(randomSeedCheckBox.isSelected()) {

					Integer rs = solverTaskDescription.getLangevinSimulationOptions().getRandomSeed();
					if(rs == null) {
						rs = LangevinSimulationOptions.DefaultRandomSeed;
						solverTaskDescription.getLangevinSimulationOptions().setRandomSeed(rs);
					}
					randomSeedTextField.setText(rs.toString());
				} else {
					solverTaskDescription.getLangevinSimulationOptions().setRandomSeed(null);
					randomSeedTextField.setText("");
				}

			}
		}
		
		public void focusGained(java.awt.event.FocusEvent e) {
		}

		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.isTemporary()) {
				return;
			}
			if (e.getSource() == getJTextFieldNumOfTrials() ||
				e.getSource() == getJTextFieldNumOfParallelLocalRuns() ||
				e.getSource() == getJTextFieldIntervalImage() ||
				e.getSource() == getJTextFieldIntervalSpring() ||
				e.getSource() == getNumPartitionsXTextField() ||
				e.getSource() == getNumPartitionsYTextField() ||
				e.getSource() == getNumPartitionsXTextField()) {
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
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,22,1,6);
			trialPanel.add(getNumOfParallelLocalRunsLabel(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			trialPanel.add(getJTextFieldNumOfParallelLocalRuns(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5,5,1,1);
			trialPanel.add(getNumPartitionsLabel(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 3;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5,22,1,6);
			trialPanel.add(getNumPartitionsXLabel(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 3;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5,5,3,1);
			trialPanel.add(getNumPartitionsXTextField(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 4;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,22,1,6);
			trialPanel.add(getNumPartitionsYLabel(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 4;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			trialPanel.add(getNumPartitionsYTextField(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 5;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,22,1,6);
			trialPanel.add(getNumPartitionsZLabel(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 5;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			trialPanel.add(getNumPartitionsZTextField(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 6;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5,5,3,1);
			trialPanel.add(getRandomSeedCheckBox(), gbc);


//			gbc = new GridBagConstraints();		// --- empty panel (filler)
//			gbc.gridx = 3;
//			gbc.gridy = 1;
//			gbc.anchor = GridBagConstraints.EAST;
//			gbc.fill = GridBagConstraints.HORIZONTAL;
//			gbc.weightx = 1.0;
//			trialPanel.add(new JLabel(""), gbc);
			
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

			gbc = new GridBagConstraints();		// --- empty panel (filler)
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.fill = GridBagConstraints.VERTICAL;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			centerPanel.add(new JLabel(""), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.weightx = 1;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(0, 0, 6, 6);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.LINE_START;
			centerPanel.add(getRandomSeedTextField(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 3;
			gbc.anchor = GridBagConstraints.LINE_END;
			gbc.insets = new Insets(0, 0, 6, 6);
			centerPanel.add(getRandomSeedHelpButton(), gbc);


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
			getJTextFieldNumOfParallelLocalRuns().setEnabled(false);

			getNumPartitionsXTextField().setEnabled(true);
			getNumPartitionsYTextField().setEnabled(true);
			getNumPartitionsZTextField().setEnabled(true);

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
	private JCheckBox getRandomSeedCheckBox() {
		if(randomSeedCheckBox == null) {
			randomSeedCheckBox = new JCheckBox();
			randomSeedCheckBox.setName("RandomSeedCheckBox");
			randomSeedCheckBox.setText("Set a seed to Langevin random number generator.");
		}
		return randomSeedCheckBox;
	}
	private JTextField getRandomSeedTextField() {
		if(randomSeedTextField == null) {
			randomSeedTextField = new JTextField();
			randomSeedTextField.setName("RandomSeedTextField");
			randomSeedTextField.setText("");
		}
		return randomSeedTextField;
	}
	private JButton getRandomSeedHelpButton() {
		if(randomSeedHelpButton == null) {
			Border border = BorderFactory.createEmptyBorder(1, 1, 1, 1);
			randomSeedHelpButton = new JButton();
			Font font = randomSeedHelpButton.getFont().deriveFont(Font.BOLD);
			randomSeedHelpButton.setName("RandomSeedHelpButton");
			randomSeedHelpButton.setBorder(border);
			randomSeedHelpButton.setFont(font);
			randomSeedHelpButton.setText(" ? ");
		}
		return randomSeedHelpButton;
	}

// ======================================================================= Number of Partitions
	private javax.swing.JTextField getNumPartitionsXTextField() {
		if (numPartitionsXTextField == null) {
			try {
				numPartitionsXTextField = new javax.swing.JTextField();
				numPartitionsXTextField.setName("NumPartitionsXTextField");
//				numPartitionsXTextField.setColumns(3);
				numPartitionsXTextField.setText(LangevinSimulationOptions.DefaultNPart[0]+"");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numPartitionsXTextField;
	}
	private javax.swing.JLabel getNumPartitionsXLabel() {
		if (numPartitionsXLabel == null) {
			try {
				numPartitionsXLabel = new javax.swing.JLabel();
				numPartitionsXLabel.setName("NumPartitionsXLabel");
				numPartitionsXLabel.setText("X-axis");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numPartitionsXLabel;
	}
	private javax.swing.JTextField getNumPartitionsYTextField() {
		if (numPartitionsYTextField == null) {
			try {
				numPartitionsYTextField = new javax.swing.JTextField();
				numPartitionsYTextField.setName("NumPartitionsYTextField");
//				numPartitionsYTextField.setColumns(3);
				numPartitionsYTextField.setText(LangevinSimulationOptions.DefaultNPart[1]+"");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numPartitionsYTextField;
	}
	private javax.swing.JLabel getNumPartitionsYLabel() {
		if (numPartitionsYLabel == null) {
			try {
				numPartitionsYLabel = new javax.swing.JLabel();
				numPartitionsYLabel.setName("NumPartitionsYLabel");
				numPartitionsYLabel.setText("Y-axis");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numPartitionsYLabel;
	}
	private javax.swing.JTextField getNumPartitionsZTextField() {
		if (numPartitionsZTextField == null) {
			try {
				numPartitionsZTextField = new javax.swing.JTextField();
				numPartitionsZTextField.setName("NumPartitionsZTextField");
//				numPartitionsZTextField.setColumns(3);
				numPartitionsZTextField.setText(LangevinSimulationOptions.DefaultNPart[2]+"");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numPartitionsZTextField;
	}
	private javax.swing.JLabel getNumPartitionsZLabel() {
		if (numPartitionsZLabel == null) {
			try {
				numPartitionsZLabel = new javax.swing.JLabel();
				numPartitionsZLabel.setName("NumPartitionsZLabel");
				numPartitionsZLabel.setText("Z-axis");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numPartitionsZLabel;
	}
	private javax.swing.JLabel getNumPartitionsLabel() {
		if (numPartitionsLabel == null) {
			try {
				numPartitionsLabel = new javax.swing.JLabel();
				numPartitionsLabel.setName("NumPartitionsLabel");
				numPartitionsLabel.setText("Partitions number");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numPartitionsLabel;
	}

	private javax.swing.JTextField getJTextFieldNumOfParallelLocalRuns() {
		if (ivjJTextFieldNumOfParallelLocalRuns == null) {
			try {
				ivjJTextFieldNumOfParallelLocalRuns = new javax.swing.JTextField();
				ivjJTextFieldNumOfParallelLocalRuns.setName("JTextFieldNumOfParallelLocalRuns");
				ivjJTextFieldNumOfParallelLocalRuns.setColumns(3);
				ivjJTextFieldNumOfParallelLocalRuns.setText("1");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldNumOfParallelLocalRuns;
	}
	private javax.swing.JLabel getNumOfParallelLocalRunsLabel() {
		if (numOfParallelLocalRuns == null) {
			try {
				numOfParallelLocalRuns = new javax.swing.JLabel();
				numOfParallelLocalRuns.setName("NumOfParallelLocalRuns");
				numOfParallelLocalRuns.setText("Parallel Local Runs");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numOfParallelLocalRuns;
	}

	private JTextField getJTextFieldIntervalImage() {
		if (intervalImageTextField == null) {
			try {
				intervalImageTextField = new javax.swing.JTextField();
				intervalImageTextField.setName("IntervalImageTextField");
				intervalImageTextField.setColumns(9);
//				intervalImageTextField.setText("1.00E-4");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return intervalImageTextField;
	}

	private JTextField getJTextFieldIntervalSpring() {
		if (intervalSpringTextField == null) {
			try {
				intervalSpringTextField = new javax.swing.JTextField();
				intervalSpringTextField.setName("IntervalSpringTextField");
				intervalSpringTextField.setColumns(9);
//				intervalSpringTextField.setText("1.00E-9");
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
		getJTextFieldNumOfParallelLocalRuns().addFocusListener(ivjEventHandler);
		getJTextFieldIntervalImage().addFocusListener(ivjEventHandler);
		getJTextFieldIntervalSpring().addFocusListener(ivjEventHandler);
		getNumPartitionsXTextField().addFocusListener(ivjEventHandler);
		getNumPartitionsYTextField().addFocusListener(ivjEventHandler);
		getNumPartitionsZTextField().addFocusListener(ivjEventHandler);
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
		int numTrials = lso.getNumTrials();
		int numOfParallelLocalRuns = lso.getNumOfParallelLocalRuns();
		if(numTrials == 1) {
			getTrajectoryButton().setSelected(true);
			getJTextFieldNumOfTrials().setEnabled(false);
			getJTextFieldNumOfParallelLocalRuns().setEnabled(false);
		} else {
			getMultiRunButton().setSelected(true);
			getJTextFieldNumOfTrials().setEnabled(true);
			getJTextFieldNumOfTrials().setText(numTrials+"");
			getJTextFieldNumOfParallelLocalRuns().setEnabled(true);
			getJTextFieldNumOfParallelLocalRuns().setText(numOfParallelLocalRuns + "");
		}

		getNumPartitionsXTextField().setText(lso.getNPart(0) + "");
		getNumPartitionsYTextField().setText(lso.getNPart(1) + "");
		getNumPartitionsZTextField().setText(lso.getNPart(2) + "");

		getJTextFieldIntervalImage().setText(lso.getIntervalImage()+"");
		getJTextFieldIntervalSpring().setText(lso.getIntervalSpring()+"");

		// TODO: temporarily disable the button
		// UNDO THIS WHEN DEVELOPMENT IS COMPLETE
		if(solverTaskDescription.getSolverDescription().isLangevinSolver()) {
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
		int numOfParallelLocalRuns = 1;
		double intervalImage = solverTaskDescription.getLangevinSimulationOptions().getIntervalImage();
		double intervalSpring = solverTaskDescription.getLangevinSimulationOptions().getIntervalSpring();
		int[] npart = solverTaskDescription.getLangevinSimulationOptions().getNPart();

		if(getMultiRunButton().isSelected()) {
			numTrials = Integer.parseInt(getJTextFieldNumOfTrials().getText());
			numOfParallelLocalRuns = Integer.parseInt(getJTextFieldNumOfParallelLocalRuns().getText());
		}

		Integer randomSeed = null;
		if (randomSeedCheckBox.isSelected()) {
			try {
				randomSeed = Integer.valueOf(randomSeedTextField.getText());
			} catch (NumberFormatException ex) {
				randomSeed = solverTaskDescription.getLangevinSimulationOptions().getRandomSeed();
				if(randomSeed == null) {
					randomSeed = LangevinSimulationOptions.DefaultRandomSeed;
				}
				randomSeedTextField.setText(randomSeed.toString());
			}
		}

		intervalImage = Double.parseDouble(getJTextFieldIntervalImage().getText());
		intervalSpring = Double.parseDouble(getJTextFieldIntervalSpring().getText());

		npart[0] = Integer.parseInt(getNumPartitionsXTextField().getText());
		npart[1] = Integer.parseInt(getNumPartitionsYTextField().getText());
		npart[2] = Integer.parseInt(getNumPartitionsZTextField().getText());

		// make a copy
		LangevinSimulationOptions lso = new LangevinSimulationOptions(sso);
		lso.setNumTrials(numTrials);
		lso.setNumOfParallelLocalRuns(numOfParallelLocalRuns);
		lso.setRandomSeed(randomSeed);
		lso.setIntervalImage(intervalImage);
		lso.setIntervalSpring(intervalSpring);
		lso.setNPart(npart);
		solverTaskDescription.setLangevinSimulationOptions(lso);
		
		} catch(Exception e) {
			PopupGenerator.showErrorDialog(this, e.getMessage(), e);
		}
	}
	
}
