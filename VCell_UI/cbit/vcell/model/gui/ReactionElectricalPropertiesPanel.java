package cbit.vcell.model.gui;

/**
 * Insert the type's description here.
 * Creation date: (11/18/2002 2:01:41 PM)
 * @author: Jim Schaff
 */
public class ReactionElectricalPropertiesPanel extends javax.swing.JPanel {
	private cbit.vcell.model.Kinetics fieldKinetics = null;
	private javax.swing.JLabel ivjChargeValenceTitleLabel = null;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjJPanel = null;
	private cbit.vcell.model.Kinetics ivjkinetics1 = null;
	private ChargeValenceComboBoxModel ivjChargeValenceComboBoxModel1 = null;
	private javax.swing.JComboBox ivjChargeValenceComboBox = null;
	private cbit.vcell.model.ReactionStep ivjReactionStep1 = null;
	private javax.swing.JCheckBox ivjCurrentCheckbox = null;
	private javax.swing.JCheckBox ivjMolecularCheckbox = null;

class IvjEventHandler implements java.awt.event.ItemListener, java.beans.PropertyChangeListener {
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == ReactionElectricalPropertiesPanel.this.getMolecularCheckbox()) 
				connEtoM11(e);
			if (e.getSource() == ReactionElectricalPropertiesPanel.this.getCurrentCheckbox()) 
				connEtoM12(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ReactionElectricalPropertiesPanel.this && (evt.getPropertyName().equals("kinetics"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == ReactionElectricalPropertiesPanel.this.getReactionStep1() && (evt.getPropertyName().equals("physicsOptions"))) 
				connEtoC4(evt);
			if (evt.getSource() == ReactionElectricalPropertiesPanel.this.getReactionStep1() && (evt.getPropertyName().equals("physicsOptions"))) 
				connEtoC6(evt);
		};
	};
/**
 * ReactionElectricalPropertiesPanel constructor comment.
 */
public ReactionElectricalPropertiesPanel() {
	super();
	initialize();
}
/**
 * ReactionElectricalPropertiesPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ReactionElectricalPropertiesPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ReactionElectricalPropertiesPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ReactionElectricalPropertiesPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ReactionElectricalPropertiesPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ReactionElectricalPropertiesPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * connEtoC1:  ( (MolecularCheckbox,item.itemStateChanged(java.awt.event.ItemEvent) --> ReactionStep1,physicsOptions).exceptionOccurred --> ReactionElectricalPropertiesPanel.setSelectionsFromPhysicsOption(Lcbit.vcell.model.ReactionStep;)V)
 * @param exception java.lang.Throwable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.lang.Throwable exception) {
	try {
		// user code begin {1}
		// user code end
		if ((getReactionStep1() != null)) {
			this.setSelectionsFromPhysicsOption(getReactionStep1());
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
 * connEtoC2:  (kinetics1.this --> ReactionElectricalPropertiesPanel.kinetics1_This(Lcbit.vcell.model.Kinetics;)V)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(cbit.vcell.model.Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		this.enableControls(getkinetics1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ReactionStep1.this --> ReactionElectricalPropertiesPanel.setSelectionsFromPhysicsOption(Lcbit.vcell.model.ReactionStep;)V)
 * @param value cbit.vcell.model.ReactionStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(cbit.vcell.model.ReactionStep value) {
	try {
		// user code begin {1}
		// user code end
		this.setSelectionsFromPhysicsOption(getReactionStep1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ReactionStep1.physicsOptions --> ReactionElectricalPropertiesPanel.setSelectionsFromPhysicsOption(Lcbit.vcell.model.ReactionStep;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getReactionStep1() != null)) {
			this.setSelectionsFromPhysicsOption(getReactionStep1());
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
 * connEtoC5:  ( (CurrentCheckbox,item.itemStateChanged(java.awt.event.ItemEvent) --> ReactionStep1,physicsOptions).exceptionOccurred --> ReactionElectricalPropertiesPanel.setSelectionsFromPhysicsOption(Lcbit.vcell.model.ReactionStep;)V)
 * @param exception java.lang.Throwable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.lang.Throwable exception) {
	try {
		// user code begin {1}
		// user code end
		if ((getReactionStep1() != null)) {
			this.setSelectionsFromPhysicsOption(getReactionStep1());
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
 * connEtoC6:  (ReactionStep1.physicsOptions --> ReactionElectricalPropertiesPanel.enableControls(Lcbit.vcell.model.Kinetics;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		if ((getkinetics1() != null)) {
			this.enableControls(getkinetics1());
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
 * connEtoM1:  (ReactionElectricalPropertiesPanel.initialize() --> JComboBox1.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getChargeValenceComboBox().setModel(getChargeValenceComboBoxModel1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM11:  (MolecularCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> ReactionStep1.physicsOptions)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getReactionStep1().setPhysicsOptions(this.getPhysicsOptionFromSelection());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		connEtoC1(ivjExc);
	}
}
/**
 * connEtoM12:  (CurrentCheckbox.item.itemStateChanged(java.awt.event.ItemEvent) --> ReactionStep1.physicsOptions)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getReactionStep1().setPhysicsOptions(this.getPhysicsOptionFromSelection());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		connEtoC5(ivjExc);
	}
}
/**
 * connEtoM6:  (kinetics1.this --> ChargeValenceComboBoxModel1.kinetics)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(cbit.vcell.model.Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		getChargeValenceComboBoxModel1().setKinetics(getkinetics1());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM7:  (kinetics1.this --> ReactionStep1.this)
 * @param value cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(cbit.vcell.model.Kinetics value) {
	try {
		// user code begin {1}
		// user code end
		if ((getkinetics1() != null)) {
			setReactionStep1(getkinetics1().getReactionStep());
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
 * connPtoP2SetSource:  (ReactionElectricalPropertiesPanel.kinetics <--> kinetics1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((getkinetics1() != null)) {
				this.setKinetics(getkinetics1());
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
 * connPtoP2SetTarget:  (ReactionElectricalPropertiesPanel.kinetics <--> kinetics1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			setkinetics1(this.getKinetics());
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
 * Comment
 */
private void enableControls(cbit.vcell.model.Kinetics kinetics) {
	cbit.vcell.model.Kinetics kt = kinetics;
	boolean bEnableValence = true;
	boolean bEnableMolecular = true;
	boolean bEnableCurrent = true;

	if (kt != null){
		if (kt.getKineticsDescription().isElectrical()){
			bEnableCurrent = false;
		}else{
			bEnableMolecular = false;
		}	
		if (!getMolecularCheckbox().isSelected() && getCurrentCheckbox().isSelected() &&
			kt.getKineticsDescription().isElectrical() && !kt.getKineticsDescription().needsValence()){
			bEnableValence = false;
		}
		if (getMolecularCheckbox().isSelected() && !getCurrentCheckbox().isSelected()){
			bEnableValence = false;
		}
	}else{
		bEnableCurrent = false;
		bEnableMolecular = false;
		bEnableValence = false;
	}
	
	getChargeValenceTitleLabel().setEnabled(bEnableValence);
	getChargeValenceComboBox().setEnabled(bEnableValence);
	getMolecularCheckbox().setEnabled(bEnableMolecular);
	getCurrentCheckbox().setEnabled(bEnableCurrent);
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getChargeValenceComboBox() {
	if (ivjChargeValenceComboBox == null) {
		try {
			ivjChargeValenceComboBox = new javax.swing.JComboBox();
			ivjChargeValenceComboBox.setName("ChargeValenceComboBox");
			ivjChargeValenceComboBox.setPreferredSize(new java.awt.Dimension(40, 23));
			ivjChargeValenceComboBox.setMaximumRowCount(10);
			ivjChargeValenceComboBox.setMinimumSize(new java.awt.Dimension(40, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChargeValenceComboBox;
}
/**
 * Return the ChargeValenceComboBoxModel1 property value.
 * @return cbit.vcell.model.gui.ChargeValenceComboBoxModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ChargeValenceComboBoxModel getChargeValenceComboBoxModel1() {
	if (ivjChargeValenceComboBoxModel1 == null) {
		try {
			ivjChargeValenceComboBoxModel1 = new cbit.vcell.model.gui.ChargeValenceComboBoxModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChargeValenceComboBoxModel1;
}
/**
 * Return the ChargeValenceTitleLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getChargeValenceTitleLabel() {
	if (ivjChargeValenceTitleLabel == null) {
		try {
			ivjChargeValenceTitleLabel = new javax.swing.JLabel();
			ivjChargeValenceTitleLabel.setName("ChargeValenceTitleLabel");
			ivjChargeValenceTitleLabel.setText("charge valence:");
			ivjChargeValenceTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjChargeValenceTitleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChargeValenceTitleLabel;
}
/**
 * Return the CurrentOnlyRadioButton property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getCurrentCheckbox() {
	if (ivjCurrentCheckbox == null) {
		try {
			ivjCurrentCheckbox = new javax.swing.JCheckBox();
			ivjCurrentCheckbox.setName("CurrentCheckbox");
			ivjCurrentCheckbox.setText("include electric current");
			ivjCurrentCheckbox.setActionCommand("Current");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurrentCheckbox;
}
/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel() {
	if (ivjJPanel == null) {
		try {
			ivjJPanel = new javax.swing.JPanel();
			ivjJPanel.setName("JPanel");
			ivjJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCurrentCheckbox = new java.awt.GridBagConstraints();
			constraintsCurrentCheckbox.gridx = 0; constraintsCurrentCheckbox.gridy = 0;
			constraintsCurrentCheckbox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel().add(getCurrentCheckbox(), constraintsCurrentCheckbox);

			java.awt.GridBagConstraints constraintsChargeValenceTitleLabel = new java.awt.GridBagConstraints();
			constraintsChargeValenceTitleLabel.gridx = 1; constraintsChargeValenceTitleLabel.gridy = 0;
			constraintsChargeValenceTitleLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel().add(getChargeValenceTitleLabel(), constraintsChargeValenceTitleLabel);

			java.awt.GridBagConstraints constraintsChargeValenceComboBox = new java.awt.GridBagConstraints();
			constraintsChargeValenceComboBox.gridx = 2; constraintsChargeValenceComboBox.gridy = 0;
			constraintsChargeValenceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsChargeValenceComboBox.weightx = 1.0;
			constraintsChargeValenceComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel().add(getChargeValenceComboBox(), constraintsChargeValenceComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel;
}
/**
 * Gets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @return The kinetics property value.
 * @see #setKinetics
 */
public cbit.vcell.model.Kinetics getKinetics() {
	return fieldKinetics;
}
/**
 * Return the kinetics1 property value.
 * @return cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.Kinetics getkinetics1() {
	// user code begin {1}
	// user code end
	return ivjkinetics1;
}
/**
 * Return the MolecularOnlyRadioButton property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getMolecularCheckbox() {
	if (ivjMolecularCheckbox == null) {
		try {
			ivjMolecularCheckbox = new javax.swing.JCheckBox();
			ivjMolecularCheckbox.setName("MolecularCheckbox");
			ivjMolecularCheckbox.setText("include molecular flux");
			ivjMolecularCheckbox.setActionCommand("Molecular");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMolecularCheckbox;
}
/**
 * Comment
 */
private int getPhysicsOptionFromSelection() {
	boolean bMolecularSelected = getMolecularCheckbox().isSelected();
	boolean bElectricSelected = getCurrentCheckbox().isSelected();
	if (bMolecularSelected && !bElectricSelected){
		return cbit.vcell.model.ReactionStep.PHYSICS_MOLECULAR_ONLY;
	}else if (bMolecularSelected && bElectricSelected){
		return cbit.vcell.model.ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL;
	}else if (!bMolecularSelected && bElectricSelected){
		return cbit.vcell.model.ReactionStep.PHYSICS_ELECTRICAL_ONLY;
	}else{
		throw new RuntimeException("must select at least one mechanism");
	}
}
/**
 * Return the ReactionStep1 property value.
 * @return cbit.vcell.model.ReactionStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.model.ReactionStep getReactionStep1() {
	// user code begin {1}
	// user code end
	return ivjReactionStep1;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION in ReactionElectricalPropertiesPanel ---------");
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
	this.addPropertyChangeListener(ivjEventHandler);
	getMolecularCheckbox().addItemListener(ivjEventHandler);
	getCurrentCheckbox().addItemListener(ivjEventHandler);
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
		setName("ReactionElectricalPropertiesPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(425, 72);

		java.awt.GridBagConstraints constraintsMolecularCheckbox = new java.awt.GridBagConstraints();
		constraintsMolecularCheckbox.gridx = 0; constraintsMolecularCheckbox.gridy = 0;
		constraintsMolecularCheckbox.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsMolecularCheckbox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsMolecularCheckbox.weightx = 1.0;
		constraintsMolecularCheckbox.insets = new java.awt.Insets(4, 8, 4, 4);
		add(getMolecularCheckbox(), constraintsMolecularCheckbox);

		java.awt.GridBagConstraints constraintsJPanel = new java.awt.GridBagConstraints();
		constraintsJPanel.gridx = 0; constraintsJPanel.gridy = 1;
constraintsJPanel.gridheight = 2;
		constraintsJPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel.weightx = 1.0;
		constraintsJPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel(), constraintsJPanel);
		initConnections();
		connEtoM1();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
		ReactionElectricalPropertiesPanel aReactionElectricalPropertiesPanel;
		aReactionElectricalPropertiesPanel = new ReactionElectricalPropertiesPanel();
		frame.setContentPane(aReactionElectricalPropertiesPanel);
		frame.setSize(aReactionElectricalPropertiesPanel.getSize());
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
 * Sets the kinetics property (cbit.vcell.model.Kinetics) value.
 * @param kinetics The new value for the property.
 * @see #getKinetics
 */
public void setKinetics(cbit.vcell.model.Kinetics kinetics) {
	cbit.vcell.model.Kinetics oldValue = fieldKinetics;
	fieldKinetics = kinetics;
	firePropertyChange("kinetics", oldValue, kinetics);
}
/**
 * Set the kinetics1 to a new value.
 * @param newValue cbit.vcell.model.Kinetics
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setkinetics1(cbit.vcell.model.Kinetics newValue) {
	if (ivjkinetics1 != newValue) {
		try {
			cbit.vcell.model.Kinetics oldValue = getkinetics1();
			ivjkinetics1 = newValue;
			connPtoP2SetSource();
			connEtoM6(ivjkinetics1);
			connEtoM7(ivjkinetics1);
			connEtoC2(ivjkinetics1);
			firePropertyChange("kinetics", oldValue, newValue);
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
 * Set the ReactionStep1 to a new value.
 * @param newValue cbit.vcell.model.ReactionStep
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setReactionStep1(cbit.vcell.model.ReactionStep newValue) {
	if (ivjReactionStep1 != newValue) {
		try {
			/* Stop listening for events from the current object */
			if (ivjReactionStep1 != null) {
				ivjReactionStep1.removePropertyChangeListener(ivjEventHandler);
			}
			ivjReactionStep1 = newValue;

			/* Listen for events from the new object */
			if (ivjReactionStep1 != null) {
				ivjReactionStep1.addPropertyChangeListener(ivjEventHandler);
			}
			connEtoC3(ivjReactionStep1);
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
private void setSelectionsFromPhysicsOption(cbit.vcell.model.ReactionStep rs) {
	if (rs.getPhysicsOptions() == cbit.vcell.model.ReactionStep.PHYSICS_MOLECULAR_ONLY){
		if (getMolecularCheckbox().isSelected()==false) getMolecularCheckbox().setSelected(true);
		if (getCurrentCheckbox().isSelected()==true) getCurrentCheckbox().setSelected(false);
	}else if (rs.getPhysicsOptions() == cbit.vcell.model.ReactionStep.PHYSICS_MOLECULAR_AND_ELECTRICAL){
		if (getMolecularCheckbox().isSelected()==false) getMolecularCheckbox().setSelected(true);
		if (getCurrentCheckbox().isSelected()==false) getCurrentCheckbox().setSelected(true);
	}else if (rs.getPhysicsOptions() == cbit.vcell.model.ReactionStep.PHYSICS_ELECTRICAL_ONLY){
		if (getMolecularCheckbox().isSelected()==true) getMolecularCheckbox().setSelected(false);
		if (getCurrentCheckbox().isSelected()==false) getCurrentCheckbox().setSelected(true);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G360171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D45515517093B5AA1428C0A3069FB1C01428C1D245FE34E57BD165DBE92BED5CE55B3425AE36341B6E327D22353BAFFFA498FE02C40DEB14D8D0C30DE900D452C8ADA189A490C89A06E1A2D1B2193C493C4C1BF9434C1BE4C290765C1F73664D4C1B8C24ED3E6F70661DFB4E3D671CFB6E39677EBC04725DA9C5F979650210D7A4703FC3F902105DA1887D733E67C35CB43739D4507D3D8330D3
	A84EAB04E78A144DFC54DCDAA67C48D9826F9970FE78AB6652576177AE61B967659370A3CABBA6A8FB4A7D4C7D633773E0AB36D3CC5F95F5ABBC3783E4820EBC8B95527ED435026357F1BCC2DE3EA0EC24B47354CA0EEB0377BE00D400958A23FF9A1EDBE84B139B9BB8DD6F74D4E153935E7CED249EDD1D88759E46EB614FAA6145BC61A2E96BC9AAA7611986E52B8108728542A3A30761F9384BB978092DDFD2EB07ED22AC573A1481D12E9D74CB0D0D5D225526CA0A3BD596ED2AD732D965AE2F62913D2AA47A
	3A2CEED13ECC6B5F65AF217589C28B3C17F8892E7882E9F3A53CE78164E878B72C845F813F87GAEFBD14FBF3FFF186BE199DBAED4787EE94B0EA8FAA6F919FEAB3CC1BD658F7A8B5E20FD722BE3447639D0D669B317568118GD400A400B729AF64367E9E1ECD7DEA3362F2A96EA60F6728BC60BDC574065F0D0DD042F15D22CD718E88826B6FEF2BB586720C870C6F07574D47EC12FA907BBE2D7B81E15D43F766979B58E4DD6AD8FE1A0F59E215AFE89356F605BFDA5B6BFD213E995A76B9C15F36707C0162A2
	0336AB3FF720B8175733D58D365D26123E9E23BED0833F73F83D8C1FC07109BABC7353ED94CF5ED321EC2B9FFD43329DE569A81CA7146D3F4B508EB33F1E186F71B399927C61327C291F541D61E7ED6E53E4B1D3FC59F0904FE459CD7D143C4BD016B9E2AEE57E75E29B993705236652AA008600AEG8FC0A2C04AA87AD85331BFDC230FF5D93D6AD13FA48F08DE01713EF86CB5F80A8155EBEDF77BD42B5BA61A2DDE495AAF0B908999CD41F1E2037E903E12634477ED5046F151ADFA2DE468C26DD617280ADE9F
	2359F9FCA721710A3454A439C4E8406591305EB70E6FA2BADBFD6AD94F00D59503942C7C1D6324CF062DE62891AA005F4CAE6F1F20639D744F82582059A11956F7D274C234E8EC6CD094275FE3A21A888D17081D7FC0E347CBC0DFFE89ED3C67A441DD067724CBD84FF70F3F222B27C522F1496A9D45B1F64305B262B7C01F851083D0B1067E73CB8B7ACF4B367B051AE76EB0741FE9604B75E34CEFBA46424751ED34FEA2238AE5C9G99E3B88F6C3AE001E78567A1EDE31DD917A247D82A81AEF68C2921EDEC51
	61303C48002760BA6A5F2E7561748E5207CF039EB9GE51751765B5B2A18AF9F357AC4745ECEAFB81EB8906C89A667DF7A3BF87B6F73717381403BG9F8310B1319F60BCC0BB0082709940A100F80037GF68BEC3E5D8370A700CE4D863D346EEA102B8BA0G1082309AA08720628BF36991G8740B400CC00EC0012ABB08FGD8G62GD28156826482D4DD45BEFC57FAA4EFE2365BEEA0773BE17AEF57690EBC0738FE9F0FD3EF3B6EF7E7941A735C0631E4D42ED1177889689F6A2B013F491661F6A409D8912F
	9B7DFF87EDA74873A6377736F16AFDD577FBDF949A922B5E388699DF3FC6DD5E7A9BEC4946DEE1BB51D59C965DEA0B68915D837043474BCA5B09BD78BCE1D7B41E1D946F01F925998239CF433F5ECEECE6D5A1156B772B6299D1D5A577E030FCB7ED4B676F778427A4EC6B3DF652874A884CAB9A7D876DA4A65B9437DB24C1C0E361659F52B9C114C597885D2C785DAA439FBCC172E827B84A6724C3A7B68BE4CE117DE4FEE339E93A15743B47AB39A03410A9D218F9154C97477C95BA9B35810E6458B2BC632EE2
	3C9F590371FE6F25C5424A8B1EFC235CE5663D3767E7DFE531F85355E03CE7B2D51FB4163D164ECD9C6FE332FED4C37CB5E6324C4743AE6105DAA227D3F20B2AE4736D5770F7DDA1EDB4BB2C5EC11870E59166FD489E7A1523CA00A68E7BB9DD51C75BF46D1DD1C54FFE81E3789327085C65F16652EA00A3F19843971FA465B8FF72605D2C48B26BBAA80B0BA376FD7AC2B9370F855EBDF14C3E64FD6B8D24FC242FDCE743553AF246BFDFABB7813DFCC3301C49F1D78719C7FD62C5BFD10FA482A7B5BF185371CA
	D819197B84ABDF58C1FCC2566590D9D42614FEDCCBA57376B2EEE07374EAF83E8D78049B89DD1D9D69765E48688E435384205E48E4AC0BA7F4F903C877740DEC2E59F8A31B7385A82F556A7B0AC6579B4F5AA36504CE0D67F9A8251B64C03A84CE17924F720714F8FD3B27A524CB0FE7739AE9176A9152EE499052E583BE9720E2922B4FB4095517B109502D5768BC13587C38116B1DB0C9AF5F94A752891301EEF2347ABAE424EB184C6659707AD6CDA6F43F54683AA633FA9C1399DDE0321EEE218B694A26845B
	5353B13BFC6AC63A6AA9ECCEBFB2A5D8EF2DD65FDEC5EBF78A339B366B1822EFB7CB23CB19426C12B945285DCB9E244B1A4272079C43F6DBAEEA761B4A6CE2DC5FF39ADD45D4160754CDB52AAF5D0BF4C7F839E5EA289E2CBE278F695429AC1FC91C4A74CE1E4A6AE371693F761118C95779C356E1EB2056B782F13F317104EE8D1FCE73F0C21FB695576A6B82A4162C077717G4A93B03EFEFB9B2E55131C29423A677797C75B1328C9E072F6A584636B261C264DACD615F712B627C1D9CA824629F7CE69578BCD
	BE1FE21368D20547003527747117611EB975B2BC6D1257273643849940F8712F27083CE4AD4351FCED779B3A4EA431B4835A5D883077A602EF68A37392099DCCCF5BC154F35A25FB053EBBE66717C65153F1939FCFB785752C3912DB446CDF517CC7E15CFD1668A1DF407578974774B6DF31630B28322C64B2E46BE449D6CC45CC77A77AF0AF286AE6702B1BF1AF48F3E2CFB499037BC0E42A61B67CD6BF5AF09A54319A004DCD277BC87F605C46F0A69B312B6A10FCCC177FECBA42E5DD7340BCA133B8B15FE82F
	47F15F4CFC619626C375ADC1DD62667597A481B0B9FA6CA8C7GE852EFC1B9D6D23C6416D449AACB17446AFDEC7EEA68514FDF9B003EAC51DCDA1F08FCBF3393FB38C2ECFF7F5BFAF9CB2C138D65CDFCFEB03FAD114969C98C4A7B520C7E0232A414777703A8EFA65064A4227D4BBFA1CF1A8389188F4C9F8C355B659E14A3EECD06D0316BD6C3B9283DEE65636E56209C2B6776972C1B0BF27C184AC15ACC811A2CDBD1FF17C35FFF0C76FE874ADCF61B39346DB624AD17D0BEC6B7A2A1DD9C50E4EAF4B9946F55
	BB91255FBF04B264GED756D66D256C7B3C675FD54F7BB5B9BC8389D6B1BB9C456D212AA3AEA49BFE7D42BAA4290F28F0A8355A44055DAC754DA1AB25736C3F9AB79350F5B7449A15ABF0ED19F285769A18279B0AEECF322BC4950565337E35FA4B7D641F3D0D43B74EC473C0A6B0C4873276AFD6D2C0FFE739676515E1685C243A6E754BE5ABA0D75CD7D34E09F25EDFB21E8A5G13E729A64FA420D9B18D6D73D5C738FD088FEF02F2466717112FE4BA4C1553917F3A8B71814025CFC7FBDEF1E1BFB2FDDB4A08
	8EBED155547391E5C3342F6E0888E57AA87171BEB333473CDD7A31F3786A670676C85861485FB01D4FEDDF8A5AA38946CE6A8C745927B479FB01A669CB28D73C9B7DEE8D608AE7E8E359CD7402503A1F511DD2107FB05028B330FF677E196C110AEE12D2B6ABEE552B48BE837DCE72C1813A317A72BCD8DF9A5435DE2BEFFEA9D9E709811BC8CD55E933793D10ED8B68E773BDA8EFE59264BCC9A86F4B97C365F5DD447A9301E6D5926A3B430B7CAF812E7E8E649F760672BF6BC5FE99E896E8F4C7FC5147C0128F
	6BDE877465F7A24FEFFCD8B7FB5F23224F30B57F3208FD660EBB037BDCFD3A5F5A5A0D6E3FF60D127654BB597EEB1106BFCF7189BABC0FDB81ED0CC219F0976E9946F7139CE7G5E63GE68224832C8548398BF38B5B6903C20CBD59F6378A2907D5BE933288463D51CE22BB2CDB797BDB1D7A184D6C1057C76C30F566356F71B13E9D14EF6FF5703171356FD37D79D0614E93798586630B000957BDB0B3B83E186D6ACDA41EACG7CCA00AC009C0032D9E837F91DB5316C067B535CE02C5E59E7C93DF5D0CF8740
	G40E500E42D5E0D1DB17B23B5E0589F4CE6EB7BA6F69E73038972BD559F0BEF452C507EE3FC7F9613AFBB0C0F59AF47CC727BDCA8AB1FEDAE2D81E883108192G52E6239D8FF79D0DE547A65C74B1A95A4F66700D9B147702FDE2FA3AE2722D1A9D4A47626520CC74B0D81FE0AA4C77FEBEF2066F7D103CE09D5419834056B1CACF899F9BA5DF4658A0DA089D0F40FBAF008AB08DA083E0AD4026AF239DEF6B7AB826BF12DC82ECC84D96E9C0E6073A4109596F1318FC6549C6FE72539E22DF8D1435817481DC86
	C881D881101D0C7AE54776936D3868DA7CE4C3043C39E172996B796B883E3743753C5B086FE147446C7A3518FCB5066DFDA1CD2C3DDBE272359836F7E8E8E276EC0D602B8DFB6F3D5BE85CFDE20EB66E9C224DC9469D9BD3CF0C167036543B35B3AF6744E4FDA702AFDC56343B0DFC5CF61E7878AAA84B864885A81FE3AEEDGB0818466200F577F3569BAFC3C9970ED0A62B4F0F1AE6F32896A790D18FA4E1C934A47EC7D5F8E34F57A9C347518BCB199EE0A608B17E1439CA33E37DC93EB6F03883E70F1AD5CE3
	54377F52CB7A36824A6A81BAG9CG09G29GEB6E413E1D7A57BF476ADB9E1FE3776BB3935471A791FC61B65DF80FE1DF38A796CB6E0A49D718E244F7C61918FE56883E70BE2CB4EC2F58B33176DE0D608B37E73DE1FB0BA2782E4D1E4BE372F59836673BB8B17DEE08608B57EF40303DD73D9353EFCFCC3E4B066D7DB0026F5A747BC584DF38FEC9066D4D76CDCC3F5418FCE9E16D31FBA6EB687E76AAA84B82E038C5A3746E893C174F45D8EB26F3DD833CDBB41C156248B98B59E76139CF005C47A0B4DABBF3
	C35B7DDB002FA5C7C99B7A7B9D93B9CF0F86AC26A6F6C7BFDF1E51EDFC3EBC2B5B78FC791E6E1D8C8FF568EFB5E179FD5D631FBFAF6DB6BAFFFE0B6A7D3DFC3A0768F72B12CB244D329CC68C1033D7087C5DFEB71B96C84B3E70F52900F34D7FFDCE76AD6439B827341EB9A0C44EA92D83122AF8319213E875F65A6DDAC2FC46A35AD06643266876EBB6995B2F5DE4EC3FCE537876BBE79A5FFE734EC617E551D9E3D9161EB516E5595971E5F97CECB4D9D8FF7D3BCA6EDBC45C8DB88D1917007B5B1BEDDBB8CD17
	2AF49D506AFE519CDD0F3CF346FA941FA3E7D0BE456F35096132EEBFD7211585B38232B5CC4461F7A3A83FCA4EE1A2F4FD1B723332E87C6F1FA367B17D52006465A76ACC47BB6D28E32B2A1CA26774AC4FF3BB713CADE1AE1E379551FD7615701E851023459F3587E3DE493C5098D3896FF5GADE1F8A613E209EE47E10B319DAFDB225BB15E925B0E091668F61CE109ED47E4CBB43FE2769C9CACC51F3113BD69BF723A7F23A73A2E3F6EB15675251E683A96FC9ADB572D1FC657F5572731F52D7CB45CE7585879
	56EBBB68587379C49FCDE8DB2C2A156B3F5B2177270FB43B7CAC84FFC843E7503317203D7657B13CFB68E53DDF6E577638C60D66D78B77AF19BF63F47E466ABB618C691766835C8E2E73517BC5BDEF5CAF174EC76F179B4E476E171B4FC76F176967E3774B6C73637B60C6E7086D8E206DDEB45CE7C86236CA654F959136FBC88E35DDBD471BDC3ABEEDBE2869303EB73ACDFF5EEBEC53DF75C637E9EEEFEC1B66FD965D26A51F4536694E4F463769D637DE57FA8D3F529DBA5E718E544D0A1E7E00C67F09C70F8F
	5E2D4A760456F3006397DF54537FD1236F7006529F647857FCFA7A60787B36AF149E7B70A7FEF237CD360EAAFED5908E4FA7F17EC56D8E5BDA70058DGE5005FBB1F6DE7323C664191D21F5DEBF50119E842D13100702E9CC15EC9C0BF93A093A08BA0F7BE1E55D47693DB1FD2E45166172DDE3A1B532F107B8F2C6E57ECC426937CD29A1F63BE5F266F9FBECF7335CF44DEC65F02208F2B8BA273DEF606B3CA673304856C8C27D843CF2678949D1E1D6D14D2BCF94F04324AFBB1CF3F056E694641FB3206735889
	AE9D5E57E838476C184FE7EB38CA3A1FD23750DC5A3690F14F51FC3E975EE58D17B7063C818D37E20860D6437BFA8D37D3A2389CF8976EC35C30043CDBB55C898772765E0767F63F9B463B978E40255C07FD3D3F0F6CFFE87BF064783A317138D79AB8EA9D6C201E43F2062161924E1FF59F668CB3E91DE5296652EA00A329F8F7A5709E5E97C9DBB8CFD87BD363BBBA5926AF72AD292C7FAE276A6ED8FC50DF405AFCEE845B4C04721CD402AB8F684F1F094FCD97583A0F159F8BA9A73E38AF24BC90D2CE64BDD3
	20AFDF9CD2DE2463E73ABD79AE6A76521369023CECD254EF229A96B11DFA79336A1FE765F54D6A5A4C7A68E61A03A5C1D96AA26C37ADAE6C4BB50BB07F3ACA7D32F2B150G74AEC67CAD94CF6E4B45ADE6778899FE274392C67F77DA338557063F27ED9488413B51FF4FB6D89C19C773A30FD7340B647C7A0CF40944042C45F42C51B119823F5396E35C19EAA773124B9A10DCFED737B2C22F4DF27A94BAB6491A20F20939D4DDC2F05F31377076EA16C45A886F6C96F0B882B4A600D7F919E3890B9FF91A3C879D
	A4E6C240A46DD3E9D95BA769D8DC81742B17202C3F9D256757B0D1EEDAC23E750BA72A89CC2E0AB1142BA1ADD22E3CB039BA00268FE08F6F87B5ADDC2E6F8E51584A4F0CDCAE2B1B5C85E06DBEE697B43AEAA9D42682C7D026DBA514A96E9AE4B269ECC570C9BA19D85D6FC90266CB554393BB8F7DE802FC4AF57331F9E3E8044E9BBADD985EC771E9BABC0B37F334F81B89E5157763F96016A1B2074641FBB2C086409A000DGA58F603E40424F6B0498FB4DA6455381C9182CDB9EB37DFEBEB2316F7D3ED60CF1
	AD635145C266141628F77CAA9FE0BEF018BFB3574C4AA3CF26DFB2BDA71521AC81A08DE095C096C00E265FAFBF67E77AE42B23C9D52DB6873DD129ED1CC862C890CD92906623F31D1179CC26F6AEDB381465B315702CDB4A764B187FD63950FF5B169A7BAF7AAE3D5F83B41E254CFF89EE525270B1752815441AE6F231462DEA998EABD35EA65FE704DC2AB4C92AACF6D87BA161E1B4F70B2F0653E8274DF8577D7110FBD65338BE9970E4E3E82ACB5064916DE41F5CAD0ED4E9B750DAA4B09D79C82E3A3E2E266A
	4041FD584FA9A37ABB9B8DAF5FE878FD8201555CD66B78B371FFE7653176165B7361393F8E77938A86F1BF21BC5DDC3A1B5288C20900FFB9FF0944E1AC7815DB50FC70C7DB5187AB174C97DA3E5F123FA70AEC9D69CC266474E05E60393BFE8B2B5363895A66E71FDF3BED1EBA2F37CD59ECB9BFAFCA7BCF73F633F56DB3DB340FE936F870FAED11F5117CE675FCD542FABA9E44BE3F13FE0B4E4639E7A478FBD477FB413045C05E3F9730B69E7F073641E244ABC3515719EF8C992FB3F78F996FA73EBBB47E5E65
	07C3513FBD2A9F52FFFB44E21CDDA5E3F39274CB8A40AA00AC000D8FE2FC7B7A506F849A5F8CE2DB6B40A0D96712AFEBD5EC7FDEE7F4DD531C313F47DDC677847A95D5D5DC24FE44BF6624B9016221C806DB69A4B672CA038E1511F27CB7E9BEAA0BF60626BE186D44B92C64A1486F9F42FBAE55B266650107F0ADE02677BE5361FDB5C036C67BD7977AF16EC357BB9EDE708B02769D01BD4AF784B4303236B6B9707ECD5D43B85FAE246B96873CCFFA987BE70D0BFC97E9368EC56E388312FFB9B54A3074ABA06E
	77A5A1F7C796C0FD198F0F1FDFB399FEBCC2661FB5C03B81E0EB862C37819AG3AG86G82G09G8BGD6G2C85D88F108BD0328C66B4003AE5A87FC74A3EF164670BB76AE4DE2BC45C0C4B51B6AAE87D1D61F9B7DA9D6025A740441D7DC322CDB5D98735F1FFFACC2FFF8B48E3D9B63E7E0CEFDF489ADE861E3898FCCCC6379AFDDC78555863620ACA3F57GFFB60DFA78DED03CCAF28845EE77095A78CBD04BB4DC3B2FCB71C9A4DBE632BF97317FF0EDB1F2538479BE0C38B7918BD89E7A849DFFC94B427353
	6FD2FC4A32707C34D746311D86E5E50FE8E710A1F78CD440A7BCF2ADFD7C02617EE3B267CD674FD5704CFAC47FCD5E12E172DB0B4DEC3871320706C91C553E2330328B5EDE9877EC3D3C1B669D9B9FE13AB15CDE0AABDC4EF0FC8FFEA43A9F7DF6A4369FED8E9847572D0148783AB3E09CDFDF8F6863AB5F038F1058B688635522513D2361FAB45CFB34CE02BBA78D288E8DDFC365A278B611B4087883D42E910A33F05CA19D2E874F8F83E4AC004A8E450B3CCD81328E34CB324C4FA8A90D6406212159638415D1
	72D8F5F2F7EA389E2DAF4431487E999A53778F439DF1C65235BA75F47C4EAD79AC067FFED6773BDF7E4767BCE37F50B6184EAED7249D3C2EC8FB15515C308AF0F54BF16F482F6D839E56F0D7AC8417886FE99A6EBB9437925E57AF47BDC06BFB3A6F15280BB1FCE5E0DB900FAE46736A7743768BD2AF117EE06B2DC4ED3FA0F7F9E46C40BB6251768B36E6062F376E30107CG1CD43A2438D52B5CA4CB03EE12D571BD1625964CC7AAB2F10F65F94BEBA1BCA61094A3BB6EA395EA77F55BB2B1DFF9DC4B3B55CC4C
	3B1F9C93B4D93EE9A1E77E3A9424768C7997D3BADFEDF36BE9D3EB37C67FE6CF5578745D6D475BCC4C473E9EF2F7B38D5ADF19B9FE6CE5FD7A2C9469B7BF1074FE437E9FAA6F55AEF8C97B05D0248A2FC9EE1B6C9F902BDC38D5DFE5177D81D558226D5D2BC28592097C3B72AA9BDB742AC2B1DF7E7A3C102AC72C2C1AEEFB767BE997DA4A73057CF6E133033ACE3BD0B4C2FCAB606182D5836F8E0D371D06186C61030D85BF19DF10AF9482A38B396DC2A1091B406FE57594C0482535946A41F7D138C2FF313A49
	5BE6DED692C2335910B3822B8ABBECF409DEB54C56680D46F47CDB082B7A35E36EE4DB54F5753AC7D91F66BC9A7EAD447F6491C1EB9EB31736BD06395E693C8F0568F95A9917D516413B1DCD6E8112339D9DD545603A26A01F546FC39ADE484ADE4E5F427D92F97E9FD0CB8788DDF2E697BF99GG88CDGGD0CB818294G94G88G88G360171B4DDF2E697BF99GG88CDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF999GGG
	G
**end of data**/
}
}
