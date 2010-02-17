package cbit.vcell.solver.ode.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.vcell.util.BeanUtils;
import org.vcell.util.NumberUtils;
import org.vcell.util.Range;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;

public class OutputOptionsPanel extends JPanel {
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
	
	private JPanel stopSpatiallyUniformPanel = null;
	private JCheckBox stopSpatiallyUniformCheckBox = null;
	private JCheckBox dataProcessorCheckBox = null;
	private JButton editDataProcessorButton = null;
	private javax.swing.JLabel ivjPointsLabel = null;
	
	private SolverTaskDescription solverTaskDescription = null;
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();

	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == OutputOptionsPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) { 
				refresh();
			}
			if (evt.getSource() == getSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_SOLVER_DESCRIPTION))) {
				refresh();
			}
			if (evt.getSource() == getSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_TIME_BOUNDS))) {  
				onPropertyChange_TimeBounds();			
			}
			if (evt.getSource() == getSolverTaskDescription() && (evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_OUTPUT_TIME_SPEC))) {
				refresh();
			}
			if (evt.getSource() == getSolverTaskDescription() && evt.getPropertyName().equals(SolverTaskDescription.PROPERTY_STOCH_SIM_OPTIONS)) {
				refresh();
			}
		}
		
		public void actionPerformed(java.awt.event.ActionEvent e) {			
			if (e.getSource() == stopSpatiallyUniformCheckBox) {
				getSolverTaskDescription().setStopAtSpatiallyUniform(stopSpatiallyUniformCheckBox.isSelected());
				if (stopSpatiallyUniformCheckBox.isSelected()) {
					try {
						getSolverTaskDescription().setErrorTolerance(ErrorTolerance.getDefaultSpatiallyUniformErrorTolerance());
					} catch (PropertyVetoException e1) {
						e1.printStackTrace();
					}
				}
			} else if (e.getSource() == dataProcessorCheckBox) {
				if (dataProcessorCheckBox.isSelected()) {
					editDataProcessor(false);
					if (getSolverTaskDescription().getSimulation().getDataProcessingInstructions() == null) {
						editDataProcessorButton.setEnabled(false);
						dataProcessorCheckBox.setSelected(false);
					} else {
						editDataProcessorButton.setEnabled(true);
					}
				} else {
					editDataProcessorButton.setEnabled(false);
					getSolverTaskDescription().getSimulation().setDataProcessingInstructions(null);
				}
			} else if (e.getSource() == editDataProcessorButton) {
				editDataProcessor(true);
				editDataProcessorButton.setEnabled(true);
			} else if (e.getSource() == getDefaultOutputRadioButton() ||
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
		super();
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
				constraintsKeepAtMostLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getDefaultOutputPanel().add(getKeepAtMostLabel(), constraintsKeepAtMostLabel);

				java.awt.GridBagConstraints constraintsKeepEveryTextField = new java.awt.GridBagConstraints();
				constraintsKeepEveryTextField.gridx = 0; constraintsKeepEveryTextField.gridy = 0;
				constraintsKeepEveryTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsKeepEveryTextField.insets = new java.awt.Insets(4, 4, 4, 4);
				getDefaultOutputPanel().add(getKeepEveryTextField(), constraintsKeepEveryTextField);


				java.awt.GridBagConstraints constraintsKeepAtMostTextField = new java.awt.GridBagConstraints();
				constraintsKeepAtMostTextField.gridx = 3; constraintsKeepAtMostTextField.gridy = 0;
				constraintsKeepAtMostTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsKeepAtMostTextField.insets = new java.awt.Insets(4, 4, 4, 4);
				getDefaultOutputPanel().add(getKeepAtMostTextField(), constraintsKeepAtMostTextField);

				java.awt.GridBagConstraints constraintsPointsLabel = new java.awt.GridBagConstraints();
				constraintsPointsLabel.gridx = 4; constraintsPointsLabel.gridy = 0;
				constraintsPointsLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsPointsLabel.weightx = 1.0;
				constraintsPointsLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getDefaultOutputPanel().add(getPointsLabel(), constraintsPointsLabel);

				java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
				constraintsJLabel4.gridx = 1; constraintsJLabel4.gridy = 0;
				constraintsJLabel4.insets = new java.awt.Insets(4, 4, 4, 4);
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
				constraintsTimeStepUnitsLabel.insets = new java.awt.Insets(4, 4, 4, 4);
				getUniformOutputPanel().add(getTimeStepUnitsLabel(), constraintsTimeStepUnitsLabel);

				java.awt.GridBagConstraints constraintsOutputTimeStepTextField = new java.awt.GridBagConstraints();
				constraintsOutputTimeStepTextField.gridx = 0; constraintsOutputTimeStepTextField.gridy = 0;
				constraintsOutputTimeStepTextField.insets = new java.awt.Insets(4, 4, 4, 4);
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
				constraintsOutputTimesTextField.insets = new java.awt.Insets(4, 4, 4, 4);
				getExplicitOutputPanel().add(getOutputTimesTextField(), constraintsOutputTimesTextField);

				java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
				constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 1;
				constraintsJLabel3.gridwidth = 2;
				constraintsJLabel3.insets = new java.awt.Insets(4, 4, 4, 4);
				JLabel jlabel3 = new javax.swing.JLabel("(Comma or space separated numbers, e.g. 0.5, 0.8, 1.2, 1.7)");
				getExplicitOutputPanel().add(jlabel3, constraintsJLabel3);
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
			setLayout(new java.awt.GridBagLayout());
			TitledBorder tb=new TitledBorder(new EtchedBorder(),"Output Options", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, getFont());
	     	setBorder(tb);
	
	     	// 0
			java.awt.GridBagConstraints constraintsDefaultOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsDefaultOutputRadioButton.gridx = 0; constraintsDefaultOutputRadioButton.gridy = 0;
			constraintsDefaultOutputRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsDefaultOutputRadioButton.anchor = GridBagConstraints.LINE_START;
			add(getDefaultOutputRadioButton(), constraintsDefaultOutputRadioButton);
	
			java.awt.GridBagConstraints constraintsDefaultOutputPanel = new java.awt.GridBagConstraints();
			constraintsDefaultOutputPanel.gridx = 1; constraintsDefaultOutputPanel.gridy = 0;
			constraintsDefaultOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDefaultOutputPanel.weightx = 1.0;
			constraintsDefaultOutputPanel.weighty = 1.0;
			constraintsDefaultOutputPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			add(getDefaultOutputPanel(), constraintsDefaultOutputPanel);
	
			// 1
			java.awt.GridBagConstraints constraintsUniformOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsUniformOutputRadioButton.gridx = 0; constraintsUniformOutputRadioButton.gridy = 1;
			constraintsUniformOutputRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsUniformOutputRadioButton.anchor = GridBagConstraints.LINE_START;
			add(getUniformOutputRadioButton(), constraintsUniformOutputRadioButton);
	
			java.awt.GridBagConstraints constraintsUniformOutputPanel = new java.awt.GridBagConstraints();
			constraintsUniformOutputPanel.gridx = 1; constraintsUniformOutputPanel.gridy = 1;
			constraintsUniformOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsUniformOutputPanel.weightx = 1.0;
			constraintsUniformOutputPanel.weighty = 1.0;
			constraintsUniformOutputPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			add(getUniformOutputPanel(), constraintsUniformOutputPanel);
	
			// 2
			java.awt.GridBagConstraints constraintsExplicitOutputRadioButton = new java.awt.GridBagConstraints();
			constraintsExplicitOutputRadioButton.gridx = 0; constraintsExplicitOutputRadioButton.gridy = 2;
			constraintsExplicitOutputRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsExplicitOutputRadioButton.anchor = GridBagConstraints.FIRST_LINE_START;
			add(getExplicitOutputRadioButton(), constraintsExplicitOutputRadioButton);
	
			java.awt.GridBagConstraints constraintsExplicitOutputPanel = new java.awt.GridBagConstraints();
			constraintsExplicitOutputPanel.gridx = 1; constraintsExplicitOutputPanel.gridy = 2;
			constraintsExplicitOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsExplicitOutputPanel.anchor = GridBagConstraints.PAGE_START;
			constraintsExplicitOutputPanel.weightx = 1.0;
			constraintsExplicitOutputPanel.weighty = 1.0;
			constraintsExplicitOutputPanel.insets = new java.awt.Insets(0, 4, 4, 4);
			add(getExplicitOutputPanel(), constraintsExplicitOutputPanel);
			
			// 3
			stopSpatiallyUniformPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));		
			stopSpatiallyUniformCheckBox = new JCheckBox("Stop at Spatially Uniform");
			stopSpatiallyUniformPanel.add(stopSpatiallyUniformCheckBox);
			java.awt.GridBagConstraints gridbag1 = new java.awt.GridBagConstraints();
			gridbag1.gridx = 0; gridbag1.gridy = 3;
			gridbag1.fill = GridBagConstraints.HORIZONTAL;
			gridbag1.gridwidth = 4;
			gridbag1.insets = new java.awt.Insets(0, 0, 0, 0);
			add(stopSpatiallyUniformPanel, gridbag1);
	
			// 4
			JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));		
			dataProcessorCheckBox = new JCheckBox("Data Processing Script");
			panel1.add(dataProcessorCheckBox);
			editDataProcessorButton = new JButton("Edit...");
			panel1.add(editDataProcessorButton);
			
			gridbag1 = new java.awt.GridBagConstraints();
			gridbag1.gridx = 0; gridbag1.gridy = 4;
			gridbag1.fill = GridBagConstraints.HORIZONTAL;
			gridbag1.gridwidth = 4;
			gridbag1.insets = new java.awt.Insets(0, 0, 0, 0);
			add(panel1, gridbag1);
			
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
	 * Method to handle events for the ActionListener interface.
	 * @param e java.awt.event.ActionEvent
	 */
	private void editDataProcessor(boolean bEdit) {
		DataProcessingInstructions dpi = getSolverTaskDescription().getSimulation().getDataProcessingInstructions();
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel nameLabel = new JLabel("Name");			
		panel.add(nameLabel);
		JTextField nameField = new JTextField();
		if (dpi != null) {
			nameField.setText(dpi.getScriptName());
		} else {
			nameField.setText("VFRAP");
		}
		nameField.setColumns(20);
		panel.add(nameField);
		mainPanel.add(panel, BorderLayout.NORTH);
		
		panel = new JPanel(new GridBagLayout());			
		JLabel label = new JLabel("Text");
		GridBagConstraints cbc = new GridBagConstraints();
		cbc.gridx = 0;
		cbc.gridy = 0;
		cbc.insets = new Insets(4,4,4,8);
		panel.add(label, cbc);
		
		JScrollPane sp = new JScrollPane();
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JTextArea textArea = new JTextArea();
		if (dpi != null) {
			textArea.setText(dpi.getScriptInput());
		}
		textArea.setColumns(20);
		textArea.setRows(10);
		sp.setViewportView(textArea);			
		
		cbc = new GridBagConstraints();
		cbc.gridx = 1;
		cbc.gridy = 0;
		cbc.weightx = 1;
		cbc.weighty = 1;
		cbc.fill = GridBagConstraints.BOTH;
		panel.add(sp, cbc);			
		mainPanel.add(panel, BorderLayout.CENTER);
		
		mainPanel.setMinimumSize(new Dimension(300,200));
		mainPanel.setSize(new Dimension(300,200));
		mainPanel.setPreferredSize(new Dimension(300,200));
		
		int ok = DialogUtils.showComponentOKCancelDialog(this, mainPanel, "Add Data Processor");
		if (ok == JOptionPane.OK_OPTION && nameField.getText().length() > 0 && textArea.getText().length() > 0) {
			getSolverTaskDescription().getSimulation().setDataProcessingInstructions(new DataProcessingInstructions(nameField.getText(), textArea.getText()));
		} else {
			if (!bEdit) {
				getSolverTaskDescription().getSimulation().setDataProcessingInstructions(null);
			}
		}
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
	private void initConnections() {
		getOutputTimeStepTextField().addFocusListener(ivjEventHandler);
		getDefaultOutputRadioButton().addActionListener(ivjEventHandler);
		getUniformOutputRadioButton().addActionListener(ivjEventHandler);
		getExplicitOutputRadioButton().addActionListener(ivjEventHandler);
		getKeepEveryTextField().addFocusListener(ivjEventHandler);
		getKeepAtMostTextField().addFocusListener(ivjEventHandler);
		getOutputTimesTextField().addFocusListener(ivjEventHandler);
		stopSpatiallyUniformCheckBox.addActionListener(ivjEventHandler);
		dataProcessorCheckBox.addActionListener(ivjEventHandler);
		editDataProcessorButton.addActionListener(ivjEventHandler);
				
		getOutputTimeStepTextField().setInputVerifier(new InputVerifier() {
			
			@Override
			public boolean verify(JComponent input) {
				return false;
			}

			@Override
			public boolean shouldYieldFocus(JComponent input) {
				boolean bValid = true;
				double outputTime = Double.parseDouble(getOutputTimeStepTextField().getText());
				if (getSolverTaskDescription().getSolverDescription().isSemiImplicitPdeSolver()) {
					double timeStep = getSolverTaskDescription().getTimeStep().getDefaultTimeStep();
					
					String suggestedInterval = outputTime + "";
					if (outputTime < timeStep) {
						suggestedInterval = timeStep + "";
						bValid = false;
					} else {
						float n = (float)(outputTime/timeStep);
						if (n != (int)n) {
							bValid = false;
							suggestedInterval = ((float)((int)(n + 0.5) * timeStep)) + "";
						}
					} 
					
					if (!bValid) {					
						String ret = PopupGenerator.showWarningDialog(OutputOptionsPanel.this, "Output Interval must " +
								"be integer multiple of time step.\n\nChange Output Interval to " + suggestedInterval + "?", 
								new String[]{ UserMessage.OPTION_YES, UserMessage.OPTION_NO}, UserMessage.OPTION_YES);
						if (ret.equals(UserMessage.OPTION_YES)) {
							getOutputTimeStepTextField().setText(suggestedInterval + "");
							bValid = true;
						} 
					}
				}
				if (bValid) {
					getOutputTimeStepTextField().setBorder(UIManager.getBorder("TextField.border"));
				} else {
					getOutputTimeStepTextField().setBorder(BorderFactory.createLineBorder(Color.red));
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
	}
	
	private boolean checkExplicitOutputTimes(ExplicitOutputTimeSpec ots) {
		boolean bValid = true;
		
		double startingTime = getSolverTaskDescription().getTimeBounds().getStartingTime();
		double endingTime = getSolverTaskDescription().getTimeBounds().getEndingTime();
		double[] times = ((ExplicitOutputTimeSpec)ots).getOutputTimes();
		
		if (times[0] < startingTime || times[times.length - 1] > endingTime) {
			bValid = false;
			String ret = PopupGenerator.showWarningDialog(OutputOptionsPanel.this, 
					"Output times should be within [" + startingTime + "," + endingTime + "].\n\nChange ENDING TIME to " +
							times[times.length - 1] + "?", 
					new String[]{ UserMessage.OPTION_YES, UserMessage.OPTION_NO}, UserMessage.OPTION_YES);
			if (ret.equals(UserMessage.OPTION_YES)) {
				try {
					getSolverTaskDescription().setTimeBounds(new TimeBounds(startingTime, times[times.length - 1]));
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
			getOutputTimesTextField().setBorder(BorderFactory.createLineBorder(Color.red));
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
				if (getSolverTaskDescription().getSolverDescription().isSemiImplicitPdeSolver() ||
						getSolverTaskDescription().getSolverDescription().equals(SolverDescription.StochGibson)) {
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
				getSolverTaskDescription().setOutputTimeSpec(ots);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException(e.getMessage());
			}
		} catch (Exception e) {
			DialogUtils.showErrorDialog(this, e.getMessage());
		}
	}

	/**
	 * Comment
	 */
	public void onPropertyChange_TimeBounds() {
		try {
			OutputTimeSpec ots = getSolverTaskDescription().getOutputTimeSpec();
			if (ots.isExplicit()) {
				checkExplicitOutputTimes((ExplicitOutputTimeSpec)ots);
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}			
	}
	
	private void actionOutputOptionButtonState(java.awt.event.ActionEvent actionEvent) {
		try {
			if (getSolverTaskDescription()==null){
				return;
			}
	
			OutputTimeSpec outputTimeSpec = getSolverTaskDescription().getOutputTimeSpec();
			if(actionEvent.getSource() == getDefaultOutputRadioButton() && !outputTimeSpec.isDefault()){
				getSolverTaskDescription().setOutputTimeSpec(new DefaultOutputTimeSpec());
			} else if(actionEvent.getSource() == getUniformOutputRadioButton() && !outputTimeSpec.isUniform()){
				double outputTime = 0.0;
				if (getSolverTaskDescription().getSolverDescription().isSemiImplicitPdeSolver()) {
					String floatStr = "" + (float)(((DefaultOutputTimeSpec)outputTimeSpec).getKeepEvery() * getSolverTaskDescription().getTimeStep().getDefaultTimeStep());
					outputTime = Double.parseDouble(floatStr);
				} else {
					TimeBounds timeBounds = getSolverTaskDescription().getTimeBounds();
					Range outputTimeRange = NumberUtils.getDecimalRange(timeBounds.getStartingTime(), timeBounds.getEndingTime()/100, true, true);
					outputTime = outputTimeRange.getMax();
				}
				getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(outputTime));
			} else if(actionEvent.getSource() == getExplicitOutputRadioButton() && !outputTimeSpec.isExplicit()){
				TimeBounds timeBounds = getSolverTaskDescription().getTimeBounds();
				getSolverTaskDescription().setOutputTimeSpec(new ExplicitOutputTimeSpec(new double[]{timeBounds.getStartingTime(), timeBounds.getEndingTime()}));
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}	
	
	/**
	 * Comment
	 */
	private void refresh() {
		if (getSolverTaskDescription() == null) {
			return;
		}
		// enables the panel where the output interval is set if the solver is IDA
		// Otherwise, that panel is disabled. 

		getDefaultOutputRadioButton().setEnabled(false);
		getUniformOutputRadioButton().setEnabled(false);	
		getExplicitOutputRadioButton().setEnabled(false);
		BeanUtils.enableComponents(getDefaultOutputPanel(), false);
		BeanUtils.enableComponents(getUniformOutputPanel(), false);
		BeanUtils.enableComponents(getExplicitOutputPanel(), false);

		SolverTaskDescription solverTaskDescription = getSolverTaskDescription();
		if (solverTaskDescription==null || solverTaskDescription.getSolverDescription()==null){
			// if solver is not IDA, if the output Time step radio button had been set, 
			// change the setting to the 'keep every' radio button and flush the contents of the output timestep text field. 
			// Also, disable its radiobutton and fields.		
			return;
		}
		
		SolverDescription solverDesc = solverTaskDescription.getSolverDescription();
		if (solverDesc.equals(SolverDescription.FiniteVolumeStandalone)) {
			stopSpatiallyUniformPanel.setVisible(true);
			stopSpatiallyUniformCheckBox.setSelected(solverTaskDescription.isStopAtSpatiallyUniform());
			dataProcessorCheckBox.setVisible(true);
			editDataProcessorButton.setVisible(true);
			DataProcessingInstructions dpi = solverTaskDescription.getSimulation().getDataProcessingInstructions();
			if (dpi != null) {
				dataProcessorCheckBox.setSelected(true);
				editDataProcessorButton.setEnabled(true);
			} else {
				editDataProcessorButton.setEnabled(false);
			}
		} else {
			dataProcessorCheckBox.setVisible(false);
			editDataProcessorButton.setVisible(false);
			stopSpatiallyUniformPanel.setVisible(false);
		}
		
		//Amended June 2009, no output option for stochastic gibson multiple trials
		if(solverTaskDescription.getStochOpt()!= null && solverTaskDescription.getStochOpt().getNumOfTrials()>1
		   && solverTaskDescription.getSolverDescription().equals(SolverDescription.StochGibson))
		{
			return;
		}
		OutputTimeSpec ots = getSolverTaskDescription().getOutputTimeSpec();
		
		if (ots.isDefault()) {
			// if solver is not IDA, if the output Time step radio button had been set, 
			// change the setting to the 'keep every' radio button and flush the contents of the output timestep text field. 
			// Also, disable its radiobutton and fields.
			getDefaultOutputRadioButton().setSelected(true);
			getKeepEveryTextField().setText(((DefaultOutputTimeSpec)ots).getKeepEvery() + "");
			if (getSolverTaskDescription().getSolverDescription().isSemiImplicitPdeSolver()) {
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
		UniformOutputTimeSpec uots = new UniformOutputTimeSpec(0.1);
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
		if (solverDesc.equals(SolverDescription.StochGibson) 
				|| solverDesc.isSemiImplicitPdeSolver()){
			getKeepAtMostTextField().setText("");
			getKeepAtMostTextField().setEnabled(false);
		}
	}	

	public final SolverTaskDescription getSolverTaskDescription() {
		return solverTaskDescription;
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
	
	
}
