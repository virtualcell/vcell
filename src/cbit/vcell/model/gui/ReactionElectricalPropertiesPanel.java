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
	D0CB838494G88G88GA6FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD8D45715B612AD6952565624217DECCB9353E5B7346BB6E6176DE7DB1B757BE2D33ACE37A6353BE6E3D63E563636210936D4E97298C098C5C5C5A589AAC69287A548BF88E8D4308103BA1191B17E40E366818373674C9B9884431EF3FF5E3C19F943A8ED793E63FB6F5CF36EBD675CF34FBD77E7948A8FA464E46A8B85A1B3C7E0FF8D1902302AD3909AB77CE4BD4789AFADB3882A3F03GBB041D
	F95970AC00323CE61D21C0D86B49036FD278CEBA2EB36C0577DD426FBC136760A5C2BBE6A82B6E6B50CD5F4E56733C1DDC721D62490767B6GA36070A94333C0C87594B1FCA743F348548B4275B424F94DD34CF049C02383A882FCF99A25EF03A795607E26C6C6F77DF309423AE7BC7A2DD80F2ACE8E73996FD276AC91B6E43AFF07ED993D790CC7006FC6GD4BECB78573196F83618C686CFDA7AEDF24528C5325BAB9CAE2BE42F9874591A1AAEC922C536391CE76D12C57658AC225D64F139A50FEC133CA651A9
	5957127A5D5EBCD21FA0AC04EF7F4B081BB0E21B15705E8E5077B247FF7A886297417BF20035AFF3BDCF9CEEE1FA74CC7C1B303C5A113DBD021E592FD07D4ADFD16969FCDE7FBA691373975076C3D0968710G3084A095A0FB5DB2431B44974C670F4073F43F5C6AF2B8DC4E53EE77993B55F39E75067726A6A8E138CB124565318A826D6F8AEF3906BC538165EB39E7BEEA13449A5E77C9173EA92CFC628B7ADC8D1B2CCC1C50972F233628DD973089ED5B9731ED733AE05F8CEE7B0A20EEFB61971E4B4A51E8DB
	B77B392CA1D64F242A6D64F55857AF919F18876F192CDE0A4FA0788C951E7A69A7889E3F0B56AFB3CC2E673E51330D4B52197D08D0D07530269D621F0A532FDACFE5C8DF9FAA4BB70EE25D253F27ED16A932BCC37033D4F8AA4BDC6227783D924A0C694B8C543F3ACDB8EE65BFAEB34C81C884D88C308AA08320E0837731AF1FBFF617BEE692BD72991F4DEE15BC82656D3E30871E12DF76089DCE2FACBAADD2376831097DF649ABB01A5E8BE8037EA0BEAB71FD8B34713E6414BCA28EDD28DDF4C8326471D29A47
	059DC86311C829596610208107DB60757AAE6C0227DD744A175DD6D1168294347C4E856C13D1319BEA048A601D5AE57607E43C037E6DGBD0A9D52C9FD9FC89E0896CDCD1DAE57084FEDC6CD04448DE867B892BB6C709E33015BF86A92625642F776C6DE4F310BBBD5753459C8DC92BD63FC0C2DB796205FGFDBBC09FC06CC66EBF3F6D617E5336759BC279338FE97ACFDB4CF3D973B7D23FD934B1F49C694B30FE14B1954A32B31699CAB378BC70C9E38FBCE399CF5F464038EA5F98BE460AB4F051E3C882E943
	204271729C8D1E2CFB28FF1B52073F6942BEEC833D7AGE6E5F05B8FDFAC213EFCC674CA5CFBAFFA4071A4EB20A7281CCF9CB531768F3371F39460ED00E6G9C93D5G57GBAGDCGB7G9AGE6836C83B8A05079769040F1000B0A8DF4246EF810EBB1409A0002BF41FC865087908B30GE0850070AA0CD9G93009FA081E0A9C09AC051EB4B8C27G06G663C467B700BC7CEE54E4CF65BB464FEBBC47FEDAA5DB9CF83532FF91AFABBD46F97A350DCE3B60CA6236BAEF519BD837D03FDF56DDFE44BD0BBEE91
	280FE35D5C7F3713F682BCEFB2FBEF1D265E5D2A773288B4982BDE3F8B99775E25AE7B7E82DB523157E7C2DD25D149A937C9EE49E90597AFAB9BB421BD58BCB160D2F89C846F06F925958239D741FBCDE8B3D106D42E5FA7CBDD12AC5B1C03017289521657576F8ECCC93C2D7BCC58872EB118D7947ACF1AB0A6DBDCCE27C4020042424A9FA0F302E4179CA0F42B4B6714A97E339FE29EBDA20D33B9E96E071B841CD36CBE1C5FE8EE3AC044FEF7FBEC8E88ADB8C58AF98266760FFA0BD4B6CA06710BB976CAF826
	3D46637D58A19E6FCBEF7F233038572D574ADD629F7B2C5E84F5623B15BDB15ED319E2AEE94B7EA9B2B7B13C174ADADF0E7E9AB5D9E663E11770478A14F3446614E41B45DB25609F7E985BE89D92BD03B0615BA51877A1FB68F71DF179C962D04568F26A372A5A6B12A5F71540E378BB17D16E9850A79EE081G15FBD39716737913856FD6175DCE3B8E4A5208FD5B4B8A19FD164277AAG3E06194C4472DFAB65880D1901F24A3F7788AFDF82E5ABD565D40ED73AF09E75CA37FC289EA6829FA8FE3021EBE7C8D9
	B77389DA2EB723CF58D5B9C43B9E5BACAC67EB29FCBDED2F941EB8CFB742F3BF6053895D7CE31C6EC1C617844FA5C82B277DBFAB8B69FC151C2E8D3EF12E398ECF1C735D702CD06AFB370A5325E451765C0CAEB50B56C76952953AF4C6D7102DA37983BE836DBED54D69CAG0F735AF5C65F182DEEF732065319GBF84904B6ADB426AAB4DC13A1EDACE378AE9D8FD28F7FA36DA3E8D8A1D9B78E2F3A25537380E53A542778A0D7AEAB7A1DDFFBD27DB1CC36BD911C369566428695EE860F433B6855AD353D13B7C
	27C2978FF4B827AF58942837C229AF3ED1E9F793359BEFF745A6F53B1D47B9DDC1AE350BB1D72B5D5FAAF46DD00E79C3DF2ED63B49CD0A7DF229DD346B7B28195345C2B966A1891A751536F03A852CFCE9EE309E343E97943AD4A847FCA6A3176A1D1FC76B23716915B20C19E41DBFAC0E0A7E8A6F98447D2626F32AB57CD6B90F519783AFDD2B9F9F43D8E206EFBBC08CG0D2FBF5E4A576A8F0EA48AF39EFEB6AB521E443CBCAA6F623CC0FC9D7A59D8960DD56EEEECD3B000AE869E276E18556B0553DE2F4BE2
	A3CB9596834AAF2B63AF45D5DD5E8C4F811B47ABF740846967712261B24A0BEB9906E6EBBBD7D9BE0B3925506EF50087D186A1298C67A50C9DD4CFCB8D57F34E6D47043E073E245F92C14F9586B61E8C81BD9B2F54E5D17B97355689536E33C48E7982DF0F5F79335A660B365F09A8CB65E6AA03E9F3C016D86F9F32286EA70F703D20B9D03E94004A68BEF7A8120C01FDA01CEA188DCF14F39B8ADBA0D68150396929B26C9FBE37D15C52F2342BBCE473D2DDDEBEFD0A474DEFBEA2A46746693576FA067EA1DE6F
	5EC2F5085F925045147A3C5E8AC0657851B1AE479A20A94967F254D6A25E663449B651EE3BAD994B687C35532C1E3FFA00FE9640FC056FE6A55A4391E47BEF6CD74B1BA75E2FA9EF5CAF87754979D44ED57981F9D31EFDDE3F6AD9AE6FD7AA393C462DBAC35FD6EE7F421378A4B9104073013DD541F61B3C4A65181532D0A85A75E9CDB9083D3632F137B5A0C77C6A6775C92B399C5DD579EC3C97EC53995A37F17DDF2CD2779F25BDD84DE51E85F4498AEDECB5170F527DDA21CB839A63F6CEE72DC13CC765C414
	7E319AAEC39F50468350BECA9BD377118E70386ECED76A4B2F453514CD169C9578CF17AC4A928CA16720E4B5E2002B9047648A12B2D7F4C079D9FCABE3B6FD2F16446921F1AF28F7510D82F9157D27DAAECFFE014E50D6407BE6DED389BC87A55924E67B3347656812D87EE4AC6B20FD34F19F6F23523679C2633A1108FDB4D9C07BE6FEC1200F12FFDE171582C065B9D44765D987B465BB38FD1E34055A87FD588A65146F178A1F9FF08B947C5F57F37C9A40156C6476FC391E77A355372DGF570CA32221E9715
	8D525E5899964A54D122392C1B5A6311DD6A3153B2F5D3539E315B0774BDBB595C36B3E08FAB0C9B3FB2F6DA9B387CA9C0133D0B6B7507866EF7AD0013F7A9E33991750250DAC5693E5F48791300A6F5976F5F2F1D40BDD24909A9E52B4BA9FBDCF62F06FE9F30C1813A517AFC47F9FD4505BA03391057776896DCE7C9FE0BC4CCF551E271F9A05B96381F6DED627246814F12C2AE6F47CD41723E58446B4FG1A5A5DDC5F594D1C5F8E38793BB97F2F1A03794F37F07E15C0D3380753ADEC09BC8672CE703ABB01
	BEC661F11D60F533354A896EB3F44D3FD38E5DE7DE34C7276CF369D46F4A5A0D6E4B129CA3954AB3192C941FCC7069AABC156F3DB1E50C6E85FBG50DC62052B1863AC076FB4003CA270EF00D3GFDC5BC37785E659AA14A1EEC07D30654C334F785AD02795EE8B76A8E6BD676BD5E2D0E59EC3F3D8C6DB0D9942EEFA420FC33895F037740C747D759C76A73216C9D6774D991624D92D6777222407822361BF38D63C9616BBAC3A5C0BBC09F402C57395DDE6DAE0FE6B73EBF4D8CC66B7D7DF52CB7816AD984309C
	E0ADC07E9B3C5E76ABD17B632CDF33BF284D5A0E4C6CBC66528C79DA4B2371153F915CFF146F4328FC2690BEEA3F4E1B185F8FC1D98C40BC00E40015G69G45EFF2BB46DFB9934D0E2779260F59253C3606EE5CF0F9FFF2ECE6FA3E98152F764DE0BE9AAF876D2807467A042742EC6F47BF923A7703F9C1A7545987C057B19D66BCB6B67CEF7258707DDE34638278CE81C885900AE1DD85F08A40DA4C6D782BAB4DD17D91F38930A1B1DB3881299D66D74E4CFE5F0A4A97D32C65A727C554EF9E14A58368G5682
	946C031C8140340F6B57529355CF1463223B71136BD521728E6D8B16CFDB4F61B03E7D21FA6E5362BBDCBDB33B56C5651B27595E6FE6585EEBD17996EA3677F84D4C6C796FE1FC95A15FA97B34465D496EC863EEC8320C603823E36A30BF0F35158A7530B32F5A19497AD098DF282C457B35FC7CFB7D686335D0E68298820881C884D88230E6BF7771B9BD276F4147DB815F6EF20DE838B813372CEEE6FAB60471056A19F7A0180F5AFA2D0D5B3A64G3775CBB31461CFD1E568B9204577397A19357755B03E50F1
	6DBE20553775D66C5BD8A81B8F3088E085C086C0D1094E50D2427BF6F54F09E8FD4B62F374FE6DE818190ED74278C2EDFA3DC433AF4278C2C13B3DAD0D5178E44D761E8E633BBB7D1E8D638B6D43B84D76A60E4F2C3D874278C26DB9DF333DFDCDB333675128FC0BB45B7BC5985F5D6937BE0CAFD43F651A6DBD50BCB37D9E0E4A37D6333D2E161969A70671056A17FDD00BEF5B091969D79C152FB824BDFA4F240C6CEF57C2D9BBG45ED9AA5F7CF60BB66A00F35CF0CA1AE913E17AA386F929C1E3360BE8B451D
	9D437B98CBD5EDA506347B17GED63A6E9C3FD3FE3A6676911004654B5D7A31FAF3FF4D57BFCF97DD56D73650DD7F7D0BC54213E55444B33BE1A7E7CF94BC7DA674F7B085E4F69499E224FA95B9C92E91666B0129F4FDEA172DF72B9693400ADFBC35725821FEBC6A55C37D8F9104FA9DF7B28DA881FD34EDAED324B43ABB1CB22676240001290F739A58B17B97EDAE47BFD751A367D9E3B26ED3F472FCDEF3FA72ECDEF3FD72FC716A5670636ACDBEEE84B32634674323CFEA312AC343FFEA163FD0B303B8197A0
	7392787E766DF28323B149AED335D2F7774D48FA5C38292D0765A61EC1F9DDBE0FC58A15F578E611D2964888F0EB9845E1F7A3883F0C67B0E13A5EA67C34AC927F6C1BF89E53EF335ABC6CC41D6A3839126BF8D6F61D47F3FA1A67AD986167ED698779F9DB469039C3779644A8003E37F82C39E066B14F7FD6F00C09036F840005A1F8AA538FFBA35B713FFA356D3822B7329DDF680DEE47B53D116D78D2EFF4BB2677C672ABEA4F7F295C42FDE6G77246BD85D9F0811F53DA4EA6B5AA3C65635D70C2E6B1098D9
	D71798DDD73F986AB3F46C7CE04FF6B2763CDE49CB925AB6D196197EF32B547ED42F5865E6359A5F20600D640CA5E02F2ACA0A7F3F1A4DEA3F2C62F3CE39DFEBFEDD4A7CEBA5FB26297C0D56775D5A20FEE92DE6F678C1FF64FE5175EB774BF37D117B65797E687D72737E487D721B7E687D727B7E69FD302FAE48F6554AFE01CD4BF65907282D0A58337CD0286D5E298F36DDAD43AF2DD775E9EB0D2243A9EBE41B3EEF553669F9EBE41B1A2D51EDFA439A5926D6EBF41BBA2C535BF4D603DA57DA85DF5B98BC5E
	799D28D79B5574558A7D370E2B71013BD5662660FA2A99FEFF331A3ECE21DF5C9CCCDF43701FEAD15387465F599341743C8FCF49F8374DAE0E3BFC32A0A49D42FD4CABB25F47BC855FBDG33G9EBCC44FB9E9DE73AFE3D85F00C7F40019C84291DB0A3C7BC6B96FBA204F831C91209DE028141F5594937FBD6F32CB961FDD74105D1CFE975EFF20F56F3120CC675825B4B647C9DB557D4366E96A57CF0E047A352EB46043292561F9AFBD43F15B4899CEA9BD4349D5701384DF70F6GCF4FF674840F5FC6A80BFB
	1B6769925953CD036F7CF7B86E091308AB016F9685776AC91E4F1B945C2E6308CB006FE48557CA7633D260FB25023BB94EF957846A9BC6DC63E11D41FC186372882E8F3E5D8A4EB14CF9A7955C67ED1CB765B0BF371B12795D0B9500ABB07273422ABE5C7FD0766170783A2969FD0F4DFAC69C6CA41EC3F30627499A86795B0DBCE7D8CF6A1C855F71G8B0C7C6E0A7F9DFEDFA4696B0F88B1F3356F682CB25F51AFB5527EDBEB8C1C8B45B81F5753B6DBC7F91B46B248C94A5087EF0D2A4F1F5167BEA750F59FAD
	FFB4289CFD31AC287C5920F21437ABCBDD1E9BF47E1F2362273A7D70ED2EDB468F978876A7E3A27EA6AA310C6A14421EF3D23F1819F4F4A20B76D18A4941320F688CC5C7F83FFD6060FD59F20467DF2344AF631086A0C5415FC770F8DFAE6D883DC7C871F9361E907A3F561AAD30B6BCC85A489282F7237F1AED50B8B2E9C63B3ABD5200046757DD365B98935A0FE29FBDC446E4813C979F65F1671913B8AFB9C43F4D61F3DCF20D11EB330CFEB5991B38A608036FD4025B7DDE9BEBEF5E51F09B71BB3BD98C96G
	4D92005D2CEC45D19ABFB295F90F9C47188981935BA75252364F1098D0DE2EB3B416F3D9BF721373EB18A82D00930497703A0FC065720DF339924B43654A8C11EB915068G8E31FEC8AD8F15EB7788092D6C4C4861901DF89700363B57A9A8F4870603E56AF585E40A9966B2255D05CCCBD436C2FC763B8119E85D7761CFFE18AF5D9FF6F6FFF7BEB8F706FCA9774CC7670D27FDE45ED069C271BA02AFD661E93C5D2844DBA314453D4B4F8325DBB8072641F7FE054ED08A5082F09D40DF417785748315C2143DE6
	334B5D89C918DD35BC267A350F4E6C77FE5F4F65F1ED61377FC9C87E3B3608F77C62AA288FA43127F17597B371C975CBA73FBFDD89E569G4547F406DA00F6003EE3DC3FF3836CCC9F37BACE4B32E899A2B7AA150D931BB496C0E382C2FDF475C8F8BE33C1B917150F51FCA68E1E894768FE99755FFE87775F64E35A7E4BFD175C6F811AD547287FA2EE5D3150B175ED325EEB450BB5CED949F058DD157D787B0C20CB15E61BEC17BA45FEC8F8A84D17255D21B44AE9B33F6B1EB32C1E0704CA2ACFA9BC6998DAAD
	8B0ABC09A7F11F5CA90D15A8B7505AECE0BA7C111C3136323C243A260C77F342187A4EC663667BB4FF1F0050D8C9ED55491E993B3F14C95B5BF0D28F4F2ACA3E1F70677778FEC28C509EA0B402C06E57123F347742F1BC961C9F95949FFCB11F7BE071630F8AEDBFED539F0AA05BA2A6D3FED5A0EF4817CB32E81DA9E3815B5CB45E3DED16DDD35B2660CBF6FDE604765BD83BA6D57B5496DF9FD7ECD1F52F36289D47F7DA4F13433C1EC5D53C4FDF3E0D65F41C7F4897F8FFCE751E696B5110772F85340DB77E26
	ED5098613C95F91D693B253D4E1C38253D1F7809DB536FDDBEF0AB72EF0F665CD27F7608463827A6F0EC2E03FEA928067886508EF03D1A47377D1E3784925FB4E25BD96BA02EF3711735B2EFBF539BD957BCEF745F636E70621EC03FCB16DD8E2C1F6377127BF1324BCD1094D7CC6E73FAEC03C3B2A5E5783768EFE125810226F358DBF6BE0779C13785557C1E4B301D6765EB2A79DAA01E501654C01E83E02A6134F39D5C0F07EA6EF5BCDC3EA3884A6F8886A27C0E0084D65AD64BB03FFF13D04367DB7D4708DB
	815F6BEAF87F98FD783B48EEF1B8FC479D106C17D36394CBFE85447C7E037754F3EBE1AD2CFFEB274F2F298CA727F07EE9815A9E00C900878192819683AC87D8831081D0D827B314831C82B0838C81788162G926A387C1F9FAD1BC6FE36F8A3CE6691ED68E6CC0E794436340E827F5B116AGAFBD87A63E58BFACD9E433B8280C7B4747557AAF84F91656CD2FBF65AB312B79D682CFDA94BEAA63B3931147450FA7220F0B7F1EA03F57GFFB60F3B59DE508B9318C33886863C12B27ED2A78A94DC075764725AB0
	DB26329B6DB34BB36BE748673AE7BE1A0766BBF07CE5570766273B893E20BEB4BF7D0AB2360B21ECD6BDDFBF46BB546D2782BE3D7EEE7A787430165C798DECFEE44FDAF83633F7EA7B5C0F71DD094DF4383132ED9FE31CD5FEC7A1528B5E9E9877F43D5CCF720E6B8DD4B70A9BA4B81961589E7CD4E4BF3ABA955D0F7A2734636B50D4F8FCF5CCE947D76F14BA3E32BD78A90CED03B0DEFB943ABB8A6E2A023B0F540938ABB62BBC2460E309DC08EF1730C10E7F8C11EB0C60FA98EE2E8AF7151F9FCE61D8G1507
	DC9E4E7B65A9DC878E586CF6F6C6C9E8ECCE989A0ABD3ECEE43439C5155C0FAB382BCADFBCB59E5EBFCF0F2B7B07629A6C61F4ADF6B59D3B676D901477B7D56F736DFF7B1C67BBFF53B6284E7F6C883743C2C7383DFA48BCB787F0898DFC6F68BB4ABEE012026B69C2DC86FC97B7F2DC9241D5423739116F810A6F28FE2FC4DC0C620B7DDB83F86EE2FC1D9332DF100DCB8236DF30DC59AF98EA8C0F9D7C0EF8247D0249465075162B8B7383F0D25BED17D3966D276D36C1A7E6D5EC0F6501AE1E0F44B672BD167F
	685A9344E386C9F9E4677BC87F5B45730564C61E2F64A8E723290DBC6FBECCFEEAC66BCD6842B3FFD58AD251057F72144EDB51FA7602796CA505FEFDD749747417BA5EEFB7D39F5B9C3457DDFCDCE728BCBEFD6C25FDFAF0A45CEF5E99D17B8D7DFF286EECB241C752EF049CD958E3F3DA6CBE2BD46260DB75A583F61FDF968C4A5E3DAC94F1A2097D2E3C44C2973D32104B163FDE0F30B3FCE5F57AB3BF7BE9D2EFDB21DE50F7881B06086BF488B9E368DBFEB79348883C5B955E8E92E2D60D56B4E52DFEB4CBAF
	E483A38D399DC2B646CD607750FA32A06412DA3255603DA5FCCC5EE85D78350915658551EC52648C434A42F68BD922170C52B5FA13B69D7BAD4414FA6D18FE3FA5623A3A331976E9DFF368EFA18C8694F49E60131BF92EF7B56FDDA1F21E5665906DF6706E1153CEAB66ECE746E5A9302E59C66A77F29AD6C84BF6133D7AE1954F7F83D0CB8788BCFB9CA6D999GG88CDGGD0CB818294G94G88G88GA6FBB0B6BCFB9CA6D999GG88CDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2
	A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1399GGGG
**end of data**/
}
}
