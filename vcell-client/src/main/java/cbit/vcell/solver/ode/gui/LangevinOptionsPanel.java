package cbit.vcell.solver.ode.gui;

import java.awt.*;
import java.math.BigInteger;

import javax.swing.*;
import javax.swing.border.Border;

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

	private JLabel totalNumberOfJobsLabel = null;
	private JTextField totalNumberOfJobsJTextField = null;
	private JLabel numberOfConcurrentJobsLabel = null;
	private JTextField numberOfConcurrentJobsJTextField = null;

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
				getTotalNumberOfJobsJTextField().setEnabled(false);
				getNumberOfConcurrentJobsJTextField().setEnabled(false);
				getNumberOfConcurrentJobsJTextField().setText("");
				getTotalNumberOfJobsJTextField().setText("");
				solverTaskDescription.getLangevinSimulationOptions().setNumberOfConcurrentJobs(1);
				solverTaskDescription.getLangevinSimulationOptions().setTotalNumberOfJobs(1);
			} else if (e.getSource() == getMultiRunButton()) {
				getTotalNumberOfJobsJTextField().setEnabled(true);
				getNumberOfConcurrentJobsJTextField().setEnabled(true);
				int totalNumberOfJobs = solverTaskDescription.getLangevinSimulationOptions().getTotalNumberOfJobs();
				if(totalNumberOfJobs > 1) {		// a multi-trial number is already set
					getTotalNumberOfJobsJTextField().setText(totalNumberOfJobs+"");
					getNumberOfConcurrentJobsJTextField().setText(solverTaskDescription.getLangevinSimulationOptions().getNumberOfConcurrentJobs()+"");
				} else {
					solverTaskDescription.getLangevinSimulationOptions().setTotalNumberOfJobs(LangevinSimulationOptions.DefaultTotalNumberOfJobs);
					solverTaskDescription.getLangevinSimulationOptions().setNumberOfConcurrentJobs(LangevinSimulationOptions.DefaultNumberOfConcurrentJobs);
					getTotalNumberOfJobsJTextField().setText(solverTaskDescription.getLangevinSimulationOptions().getTotalNumberOfJobs()+"");
					getNumberOfConcurrentJobsJTextField().setText(solverTaskDescription.getLangevinSimulationOptions().getNumberOfConcurrentJobs()+"");
				}
			} else if(e.getSource() == randomSeedCheckBox) {
				randomSeedTextField.setEditable(randomSeedCheckBox.isSelected());
				if(randomSeedCheckBox.isSelected()) {

					BigInteger rs = solverTaskDescription.getLangevinSimulationOptions().getRandomSeed();
					if(rs == null) {
						rs = LangevinSimulationOptions.DefaultRandomSeed;
						solverTaskDescription.getLangevinSimulationOptions().setRandomSeed(rs);
					}
					randomSeedTextField.setText(rs.toString());
				} else {
					solverTaskDescription.getLangevinSimulationOptions().setRandomSeed(null);
					randomSeedTextField.setText("");
				}
			} else if(e.getSource() == randomSeedHelpButton) {
				DialogUtils.showInfoDialogAndResize(LangevinOptionsPanel.this, "Set specific seed",
					"<html>Provide a seed to the Langevin solver's random number generator so exact "
							+ "trajectories can be reproduced. If this value is not entered, the solver will "
							+ "generate different sequences for each run.</html>");
			}
		}
		
		public void focusGained(java.awt.event.FocusEvent e) {
		}

		public void focusLost(java.awt.event.FocusEvent e) {
			if (e.isTemporary()) {
				return;
			}
			if (e.getSource() == getJTextFieldIntervalImage() ||
				e.getSource() == getJTextFieldIntervalSpring() ||
				e.getSource() == getNumPartitionsXTextField() ||
				e.getSource() == getNumPartitionsYTextField() ||
				e.getSource() == getNumPartitionsXTextField() ||
				e.getSource() == getRandomSeedTextField() ) {
				setNewOptions();
			} else if(e.getSource() == getTotalNumberOfJobsJTextField()) {
				int totalNumberOfJobs;
				try {
					totalNumberOfJobs = Integer.parseInt(getTotalNumberOfJobsJTextField().getText());
					if(totalNumberOfJobs < 2) {
						totalNumberOfJobs = LangevinSimulationOptions.DefaultTotalNumberOfJobs;
					}
				} catch(NumberFormatException ex) {
					totalNumberOfJobs = solverTaskDescription.getLangevinSimulationOptions().getTotalNumberOfJobs();
					if(totalNumberOfJobs < 2) {
						totalNumberOfJobs = LangevinSimulationOptions.DefaultTotalNumberOfJobs;
					}
				}
				solverTaskDescription.getLangevinSimulationOptions().setTotalNumberOfJobs(totalNumberOfJobs);
				getTotalNumberOfJobsJTextField().setText(totalNumberOfJobs+"");
			} else if(e.getSource() == getNumberOfConcurrentJobsJTextField()) {
				int numberOfConcurrentJobs;
				try {
					numberOfConcurrentJobs = Integer.parseInt(getNumberOfConcurrentJobsJTextField().getText());
					if(numberOfConcurrentJobs < 2) {
						numberOfConcurrentJobs = LangevinSimulationOptions.DefaultNumberOfConcurrentJobs;
					}
				} catch(NumberFormatException ex) {
					numberOfConcurrentJobs = solverTaskDescription.getLangevinSimulationOptions().getNumberOfConcurrentJobs();
					if(numberOfConcurrentJobs < 2) {
						numberOfConcurrentJobs = LangevinSimulationOptions.DefaultNumberOfConcurrentJobs;
					}
				}
				solverTaskDescription.getLangevinSimulationOptions().setNumberOfConcurrentJobs(numberOfConcurrentJobs);
				getNumberOfConcurrentJobsJTextField().setText(numberOfConcurrentJobs+"");
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
			JPanel bottomPanel = new JPanel(new GridBagLayout());
			bottomPanel.setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.insets = new Insets(1,1,1,1);
			getContentPanel().add(trialPanel, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.insets = new Insets(1,1,1,1);
			getContentPanel().add(centerPanel, gbc);
			
			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 0.0;
			gbc.weighty = 1.0;
			gbc.insets = new Insets(1,1,1,1);
			getContentPanel().add(rightPanel, gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth = 3;
//			gbc.weightx = 1.0;
//			gbc.weighty = 1.0;
			gbc.insets = new Insets(1,1,1,1);
			getContentPanel().add(bottomPanel, gbc);

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
			trialPanel.add(getTotalNumberOfJobsLabel(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			gbc.weightx = 1.0;
			trialPanel.add(getTotalNumberOfJobsJTextField(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,22,1,6);
			trialPanel.add(getNumberOfConcurrentJobsLabel(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			gbc.weightx = 1.0;
			trialPanel.add(getNumberOfConcurrentJobsJTextField(), gbc);

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
			gbc.weightx = 1.0;
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
			gbc.weightx = 1.0;
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
			gbc.weightx = 1.0;
			trialPanel.add(getNumPartitionsZTextField(), gbc);

			// ----- centerPanel -----------------------------------------------------
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,26,1,5);
			gbc.weightx = 0.0;
			centerPanel.add(new JLabel("Spring Interval"), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			gbc.weightx = 1.0;
			centerPanel.add(getJTextFieldIntervalSpring(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(0,6,1,0);
			gbc.weightx = 0.0;
			centerPanel.add(new JLabel("s"), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,26,1,5);
			gbc.weightx = 0.0;
			centerPanel.add(new JLabel("Image Interval"), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(0,5,3,1);
			gbc.weightx = 1.0;
			centerPanel.add(getJTextFieldIntervalImage(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 2;
			gbc.gridy = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(0,6,1,0);
			gbc.weightx = 0.0;
			centerPanel.add(new JLabel("s"), gbc);

			gbc = new GridBagConstraints();		// --- empty panel (filler)
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.fill = GridBagConstraints.VERTICAL;
			gbc.weightx = 0.0;
			gbc.weighty = 1.0;
			centerPanel.add(new JLabel(""), gbc);

			// ----- rightPanel ----------------------------------------------------
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 1.0;
			gbc.insets = new Insets(0,22,1,6);
			rightPanel.add(new JLabel(""), gbc);	// fake, just for looks

			// ----- bottomPanel ------------------------------------------------------
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5,2,6,5);
			bottomPanel.add(getRandomSeedCheckBox(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(2, 2, 6, 6);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.anchor = GridBagConstraints.LINE_START;
			bottomPanel.add(getRandomSeedTextField(), gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 3;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(2, 2, 6, 6);
			bottomPanel.add(getRandomSeedHelpButton(), gbc);

			// ----------------------------------------------------------------------
			getButtonGroupTrials().add(getTrajectoryButton());
			getButtonGroupTrials().add(getMultiRunButton());
			getButtonGroupTrials().setSelected(getTrajectoryButton().getModel(), true);

			getTotalNumberOfJobsJTextField().setEnabled(false);
			getNumberOfConcurrentJobsJTextField().setEnabled(false);

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
	private javax.swing.JTextField getTotalNumberOfJobsJTextField() {
		if (totalNumberOfJobsJTextField == null) {
			try {
				totalNumberOfJobsJTextField = new javax.swing.JTextField();
				totalNumberOfJobsJTextField.setName("JTextFieldNumOfTrials");
				totalNumberOfJobsJTextField.setColumns(9);
				totalNumberOfJobsJTextField.setText("");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return totalNumberOfJobsJTextField;
	}
	private javax.swing.JLabel getTotalNumberOfJobsLabel() {
		if (totalNumberOfJobsLabel == null) {
			try {
				totalNumberOfJobsLabel = new javax.swing.JLabel();
				totalNumberOfJobsLabel.setName("TotalNumberOfJobs");
				totalNumberOfJobsLabel.setText("Total Num. Of Jobs");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return totalNumberOfJobsLabel;
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
			randomSeedHelpButton.setText("<html><b>&nbsp;&nbsp;?&nbsp;&nbsp;</b></html>");
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

	private javax.swing.JTextField getNumberOfConcurrentJobsJTextField() {
		if (numberOfConcurrentJobsJTextField == null) {
			try {
				numberOfConcurrentJobsJTextField = new javax.swing.JTextField();
				numberOfConcurrentJobsJTextField.setName("NumberOfConcurrentJobsJTextField");
				numberOfConcurrentJobsJTextField.setColumns(3);
				numberOfConcurrentJobsJTextField.setText("");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numberOfConcurrentJobsJTextField;
	}
	private javax.swing.JLabel getNumberOfConcurrentJobsLabel() {
		if (numberOfConcurrentJobsLabel == null) {
			try {
				numberOfConcurrentJobsLabel = new javax.swing.JLabel();
				numberOfConcurrentJobsLabel.setName("NumberOfConcurrentJobsLabel");
				numberOfConcurrentJobsLabel.setText("Num. Concurrent Jobs");
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return numberOfConcurrentJobsLabel;
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
		getRandomSeedCheckBox().addActionListener(ivjEventHandler);
		getRandomSeedHelpButton().addActionListener(ivjEventHandler);

		getTotalNumberOfJobsJTextField().addFocusListener(ivjEventHandler);
		getNumberOfConcurrentJobsJTextField().addFocusListener(ivjEventHandler);
		getJTextFieldIntervalImage().addFocusListener(ivjEventHandler);
		getJTextFieldIntervalSpring().addFocusListener(ivjEventHandler);
		getNumPartitionsXTextField().addFocusListener(ivjEventHandler);
		getNumPartitionsYTextField().addFocusListener(ivjEventHandler);
		getNumPartitionsZTextField().addFocusListener(ivjEventHandler);
		getRandomSeedTextField().addFocusListener(ivjEventHandler);
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

		getMultiRunButton().setEnabled(true);

		LangevinSimulationOptions lso = solverTaskDescription.getLangevinSimulationOptions();
		int totalNumberOfJobs = lso.getTotalNumberOfJobs();
		int numberOfConcurrentJobs = lso.getNumberOfConcurrentJobs();
		if(totalNumberOfJobs == 1) {
			getTrajectoryButton().setSelected(true);
			getTotalNumberOfJobsJTextField().setEnabled(false);
			getTotalNumberOfJobsJTextField().setText("");
			getNumberOfConcurrentJobsJTextField().setEnabled(false);
			getNumberOfConcurrentJobsJTextField().setText("");
		} else {
			getMultiRunButton().setSelected(true);
			getTotalNumberOfJobsJTextField().setEnabled(true);
			getTotalNumberOfJobsJTextField().setText(totalNumberOfJobs+"");
			getNumberOfConcurrentJobsJTextField().setEnabled(true);
			getNumberOfConcurrentJobsJTextField().setText(numberOfConcurrentJobs + "");
		}

		getNumPartitionsXTextField().setText(lso.getNPart(0) + "");
		getNumPartitionsYTextField().setText(lso.getNPart(1) + "");
		getNumPartitionsZTextField().setText(lso.getNPart(2) + "");

		getJTextFieldIntervalImage().setText(lso.getIntervalImage()+"");
		getJTextFieldIntervalSpring().setText(lso.getIntervalSpring()+"");

		BigInteger randomSeed = lso.getRandomSeed();
		if (randomSeed == null) {
			randomSeedTextField.setEditable(false);
			randomSeedCheckBox.setSelected(false);
			randomSeedTextField.setText("");
		} else {
			randomSeedTextField.setEditable(true);
			randomSeedCheckBox.setSelected(true);
			randomSeedTextField.setText(randomSeed.toString());
		}
	}

	private void setNewOptions() {
		if(!isVisible()) {
			return;
		}
		try {
		LangevinSimulationOptions sso = solverTaskDescription.getLangevinSimulationOptions();
		int totalNumberOfJobs = 1;
		int numberOfConcurrentJobs = 1;
		double intervalImage = solverTaskDescription.getLangevinSimulationOptions().getIntervalImage();
		double intervalSpring = solverTaskDescription.getLangevinSimulationOptions().getIntervalSpring();
		int[] npart = solverTaskDescription.getLangevinSimulationOptions().getNPart();

		if(getMultiRunButton().isSelected()) {	// we can get here only on FocusLost event in the totalNumberOfJobs text field
			try {
				totalNumberOfJobs = Integer.parseInt(getTotalNumberOfJobsJTextField().getText());
			} catch (NumberFormatException ex) {
				totalNumberOfJobs = sso.getTotalNumberOfJobs();
			}
			try {
				numberOfConcurrentJobs = Integer.parseInt(getNumberOfConcurrentJobsJTextField().getText());
			} catch (NumberFormatException ex) {
				numberOfConcurrentJobs = sso.getNumberOfConcurrentJobs();
			}
		}

		BigInteger randomSeed = null;
		if (randomSeedCheckBox.isSelected()) {
			try {
				randomSeed = new BigInteger(randomSeedTextField.getText());
			} catch (NumberFormatException ex) {
				randomSeed = solverTaskDescription.getLangevinSimulationOptions().getRandomSeed();
				if(randomSeed == null) {
					randomSeed = LangevinSimulationOptions.DefaultRandomSeed;
				}
//				randomSeedTextField.setText(randomSeed.toString());
			}
		}

		intervalImage = Double.parseDouble(getJTextFieldIntervalImage().getText());
		intervalSpring = Double.parseDouble(getJTextFieldIntervalSpring().getText());

		npart[0] = Integer.parseInt(getNumPartitionsXTextField().getText());
		npart[1] = Integer.parseInt(getNumPartitionsYTextField().getText());
		npart[2] = Integer.parseInt(getNumPartitionsZTextField().getText());

		// make a copy
		LangevinSimulationOptions lso = new LangevinSimulationOptions(sso);
		lso.setTotalNumberOfJobs(totalNumberOfJobs);
		lso.setNumberOfConcurrentJobs(numberOfConcurrentJobs);
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
