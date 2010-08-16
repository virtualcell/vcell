/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

package cbit.vcell.solver.ode.gui;
import java.awt.Font;

import javax.swing.BorderFactory;

import org.vcell.solver.smoldyn.SmoldynSimulationOptionsPanel;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;

/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:25 PM)
 * @author: John Wagner
 */
public class SolverTaskDescriptionAdvancedPanel extends javax.swing.JPanel {
	private javax.swing.JLabel ivjJLabelTitle = null;
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
	private boolean ivjConnPtoP4Aligning = false;
	private boolean ivjConnPtoP7Aligning = false;
	private Object ivjSolverComboBoxModel = null;
	private OutputOptionsPanel ivjOutputOptionsPanel = null;
	private StochSimOptionsPanel stochSimOptionsPanel = null;
	private SmoldynSimulationOptionsPanel smoldynSimulationOptionsPanel = null;
	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	
	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if(e.getSource() == getQuestionButton())
			{
				displayHelpInfo();
			}			
		}
		
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SolverTaskDescriptionAdvancedPanel.this && (evt.getPropertyName().equals("solverTaskDescription"))) { 
				connPtoP1SetTarget();
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
			if (e.getSource() == getSolverComboBox()) 
				connEtoM6(e);
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
			if (getSolverTaskDescription().getSimulation().getMathDescription().isSpatialStoch()) {
				solverDescriptions = SolverDescription.getSpatialStochasticSolverDescriptions();
			} else {
				if (getSolverTaskDescription().getSimulation().getMathDescription().hasFastSystems()) { // PDE with FastSystem
					solverDescriptions = SolverDescription.getPDEWithFastSystemSolverDescriptions();
				} else {
					solverDescriptions = SolverDescription.getPDESolverDescriptions();
				}
			}
		} else if (getSolverTaskDescription().getSimulation().getMathDescription().isNonSpatialStoch()) {
			solverDescriptions = SolverDescription.getStochasticNonSpatialSolverDescriptions();
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
	connPtoP1SetTarget();
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
		add(getStochSimOptionsPanel(), constraintsJPanelStoch);
		
		// 4
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 3;
		gbc.gridwidth = 4;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSmoldynSimulationOptionsPanel(), gbc);
		
		// 5
		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 5;
		constraintsJPanel1.gridwidth = 4;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getOutputOptionsPanel(), constraintsJPanel1);
		
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
			getSmoldynSimulationOptionsPanel().setSolverTaskDescription(ivjTornOffSolverTaskDescription);
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

private void onPropertyChange_solverDescription() {	
	try {
		if (getSolverTaskDescription() == null) {
			return;
		}
		SolverDescription solverDescription = getSolverTaskDescription().getSolverDescription();
		updateSolverNameDisplay(solverDescription);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}	
}

}