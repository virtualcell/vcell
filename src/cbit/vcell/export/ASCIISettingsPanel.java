package cbit.vcell.export;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.server.*;
import cbit.image.*;
/**
 * This type was created in VisualAge.
 */
public class ASCIISettingsPanel extends javax.swing.JPanel implements ExportConstants, java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener, javax.swing.event.ChangeListener {
	private javax.swing.JButton ivjJButtonOK = null;
	private javax.swing.ButtonGroup ivjButtonGroup1 = null;
	private javax.swing.JLabel ivjJLabelDataType = null;
	private javax.swing.JRadioButton ivjJRadioButtonParticles = null;
	private javax.swing.JRadioButton ivjJRadioButtonVariables = null;
	protected transient cbit.vcell.export.ASCIISettingsPanelListener fieldASCIISettingsPanelListenerEventMulticaster = null;
	private javax.swing.JCheckBox ivjJCheckBoxSwitch = null;
	private javax.swing.JLabel ivjJLabelAdditional = null;
	private boolean fieldSwitchRowsColumns = false;
	private int fieldSimDataType = 0;
	private int fieldExportDataType = 0;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ASCIISettingsPanel() {
	super();
	initialize();
}
/**
 * MovieSettingsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ASCIISettingsPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * MovieSettingsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ASCIISettingsPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * MovieSettingsPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ASCIISettingsPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonOK()) 
		connEtoC1(e);
	if (e.getSource() == getCancelJButton()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener cbit.vcell.export.ASCIISettingsPanelListener
 */
public void addASCIISettingsPanelListener(cbit.vcell.export.ASCIISettingsPanelListener newListener) {
	fieldASCIISettingsPanelListenerEventMulticaster = cbit.vcell.export.ASCIISettingsPanelListenerEventMulticaster.add(fieldASCIISettingsPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> ASCIISettingsPanel.fireJButtonOKAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonOKAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JRadioButtonVariables.item.itemStateChanged(java.awt.event.ItemEvent) --> ASCIISettingsPanel.updateDataType()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateExportDataType();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ASCIISettingsPanel.simDataType --> ASCIISettingsPanel.updateChoices(I)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateChoices(this.getSimDataType());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ASCIISettingsPanel.fireJButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonCancelAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (MovieSettingsPanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup1().add(getJRadioButtonVariables());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (MovieSettingsPanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup1().add(getJRadioButtonParticles());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (JCheckBoxSwitch.selected <--> ASCIISettingsPanel.switchRowsColumns)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		this.setSwitchRowsColumns(getJCheckBoxSwitch().isSelected());
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldASCIISettingsPanelListenerEventMulticaster == null) {
		return;
	};
	fieldASCIISettingsPanelListenerEventMulticaster.JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldASCIISettingsPanelListenerEventMulticaster == null) {
		return;
	};
	fieldASCIISettingsPanelListenerEventMulticaster.JButtonOKAction_actionPerformed(newEvent);
}
/**
 * Gets the asciiSpecs property (cbit.vcell.export.server.ASCIISpecs) value.
 * @return The asciiSpecs property value.
 */
public ASCIISpecs getAsciiSpecs() {
	return new ASCIISpecs(FORMAT_CSV, getExportDataType(), getSwitchRowsColumns());
}
/**
 * Return the ButtonGroup1 property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup1() {
	if (ivjButtonGroup1 == null) {
		try {
			ivjButtonGroup1 = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup1;
}
/**
 * Return the CancelJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelJButton() {
	if (ivjCancelJButton == null) {
		try {
			ivjCancelJButton = new javax.swing.JButton();
			ivjCancelJButton.setName("CancelJButton");
			ivjCancelJButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelJButton;
}
/**
 * Gets the exportDataType property (int) value.
 * @return The exportDataType property value.
 * @see #setExportDataType
 */
public int getExportDataType() {
	return fieldExportDataType;
}
/**
 * Return the JButtonOK property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOK() {
	if (ivjJButtonOK == null) {
		try {
			ivjJButtonOK = new javax.swing.JButton();
			ivjJButtonOK.setName("JButtonOK");
			ivjJButtonOK.setPreferredSize(new java.awt.Dimension(51, 25));
			ivjJButtonOK.setFont(new java.awt.Font("dialog", 1, 12));
			ivjJButtonOK.setText("OK");
			ivjJButtonOK.setMaximumSize(new java.awt.Dimension(100, 50));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOK;
}
/**
 * Return the JCheckBoxSwitch property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSwitch() {
	if (ivjJCheckBoxSwitch == null) {
		try {
			ivjJCheckBoxSwitch = new javax.swing.JCheckBox();
			ivjJCheckBoxSwitch.setName("JCheckBoxSwitch");
			ivjJCheckBoxSwitch.setText("switch rows/columns");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSwitch;
}
/**
 * Return the JLabelAdditional property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAdditional() {
	if (ivjJLabelAdditional == null) {
		try {
			ivjJLabelAdditional = new javax.swing.JLabel();
			ivjJLabelAdditional.setName("JLabelAdditional");
			ivjJLabelAdditional.setPreferredSize(new java.awt.Dimension(108, 27));
			ivjJLabelAdditional.setText("Additional formatting:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAdditional;
}
/**
 * Return the JLabelDataFormat property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDataType() {
	if (ivjJLabelDataType == null) {
		try {
			ivjJLabelDataType = new javax.swing.JLabel();
			ivjJLabelDataType.setName("JLabelDataType");
			ivjJLabelDataType.setPreferredSize(new java.awt.Dimension(108, 27));
			ivjJLabelDataType.setText("Select data type to export:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDataType;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonOK = new java.awt.GridBagConstraints();
			constraintsJButtonOK.gridx = 0; constraintsJButtonOK.gridy = 0;
			constraintsJButtonOK.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButtonOK(), constraintsJButtonOK);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 1; constraintsCancelJButton.gridy = 0;
			constraintsCancelJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getCancelJButton(), constraintsCancelJButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JRadioButtonCompressed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonParticles() {
	if (ivjJRadioButtonParticles == null) {
		try {
			ivjJRadioButtonParticles = new javax.swing.JRadioButton();
			ivjJRadioButtonParticles.setName("JRadioButtonParticles");
			ivjJRadioButtonParticles.setText("Particle data");
			ivjJRadioButtonParticles.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonParticles;
}
/**
 * Return the JRadioButtonUncompressed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonVariables() {
	if (ivjJRadioButtonVariables == null) {
		try {
			ivjJRadioButtonVariables = new javax.swing.JRadioButton();
			ivjJRadioButtonVariables.setName("JRadioButtonVariables");
			ivjJRadioButtonVariables.setSelected(true);
			ivjJRadioButtonVariables.setText("Variable values");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonVariables;
}
/**
 * Gets the simDataType property (int) value.
 * @return The simDataType property value.
 * @see #setSimDataType
 */
public int getSimDataType() {
	return fieldSimDataType;
}
/**
 * Gets the switchRowsColumns property (boolean) value.
 * @return The switchRowsColumns property value.
 * @see #setSwitchRowsColumns
 */
public boolean getSwitchRowsColumns() {
	return fieldSwitchRowsColumns;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonOK().addActionListener(this);
	getJRadioButtonVariables().addItemListener(this);
	getJCheckBoxSwitch().addChangeListener(this);
	this.addPropertyChangeListener(this);
	getCancelJButton().addActionListener(this);
	connPtoP1SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MovieSettingsPanel");
		setPreferredSize(new java.awt.Dimension(164, 170));
		setLayout(new java.awt.GridBagLayout());
		setSize(188, 206);

		java.awt.GridBagConstraints constraintsJLabelDataType = new java.awt.GridBagConstraints();
		constraintsJLabelDataType.gridx = 0; constraintsJLabelDataType.gridy = 0;
		constraintsJLabelDataType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelDataType.weightx = 1.0;
		constraintsJLabelDataType.insets = new java.awt.Insets(10, 5, 0, 5);
		add(getJLabelDataType(), constraintsJLabelDataType);

		java.awt.GridBagConstraints constraintsJRadioButtonVariables = new java.awt.GridBagConstraints();
		constraintsJRadioButtonVariables.gridx = 0; constraintsJRadioButtonVariables.gridy = 1;
		constraintsJRadioButtonVariables.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonVariables.weightx = 1.0;
		constraintsJRadioButtonVariables.insets = new java.awt.Insets(0, 5, 0, 5);
		add(getJRadioButtonVariables(), constraintsJRadioButtonVariables);

		java.awt.GridBagConstraints constraintsJRadioButtonParticles = new java.awt.GridBagConstraints();
		constraintsJRadioButtonParticles.gridx = 0; constraintsJRadioButtonParticles.gridy = 2;
		constraintsJRadioButtonParticles.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonParticles.weightx = 1.0;
		constraintsJRadioButtonParticles.insets = new java.awt.Insets(0, 5, 0, 5);
		add(getJRadioButtonParticles(), constraintsJRadioButtonParticles);

		java.awt.GridBagConstraints constraintsJLabelAdditional = new java.awt.GridBagConstraints();
		constraintsJLabelAdditional.gridx = 0; constraintsJLabelAdditional.gridy = 3;
		constraintsJLabelAdditional.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelAdditional.weightx = 1.0;
		constraintsJLabelAdditional.insets = new java.awt.Insets(0, 5, 0, 5);
		add(getJLabelAdditional(), constraintsJLabelAdditional);

		java.awt.GridBagConstraints constraintsJCheckBoxSwitch = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSwitch.gridx = 0; constraintsJCheckBoxSwitch.gridy = 4;
		constraintsJCheckBoxSwitch.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxSwitch.insets = new java.awt.Insets(0, 5, 0, 5);
		add(getJCheckBoxSwitch(), constraintsJCheckBoxSwitch);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 5;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
		connEtoM1();
		connEtoM2();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJRadioButtonVariables()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		ASCIISettingsPanel aASCIISettingsPanel;
		aASCIISettingsPanel = new ASCIISettingsPanel();
		frame.add("Center", aASCIISettingsPanel);
		frame.setSize(aASCIISettingsPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == this && (evt.getPropertyName().equals("simDataType"))) 
		connEtoC3(evt);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener cbit.vcell.export.ASCIISettingsPanelListener
 */
public void removeASCIISettingsPanelListener(cbit.vcell.export.ASCIISettingsPanelListener newListener) {
	fieldASCIISettingsPanelListenerEventMulticaster = cbit.vcell.export.ASCIISettingsPanelListenerEventMulticaster.remove(fieldASCIISettingsPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * Sets the exportDataType property (int) value.
 * @param exportDataType The new value for the property.
 * @see #getExportDataType
 */
public void setExportDataType(int exportDataType) {
	int oldValue = fieldExportDataType;
	fieldExportDataType = exportDataType;
	firePropertyChange("exportDataType", new Integer(oldValue), new Integer(exportDataType));
}
/**
 * Sets the simDataType property (int) value.
 * @param simDataType The new value for the property.
 * @see #getSimDataType
 */
public void setSimDataType(int simDataType) {
	int oldValue = fieldSimDataType;
	fieldSimDataType = simDataType;
	firePropertyChange("simDataType", new Integer(oldValue), new Integer(simDataType));
}
/**
 * Sets the switchRowsColumns property (boolean) value.
 * @param switchRowsColumns The new value for the property.
 * @see #getSwitchRowsColumns
 */
public void setSwitchRowsColumns(boolean switchRowsColumns) {
	boolean oldValue = fieldSwitchRowsColumns;
	fieldSwitchRowsColumns = switchRowsColumns;
	firePropertyChange("switchRowsColumns", new Boolean(oldValue), new Boolean(switchRowsColumns));
}
/**
 * Method to handle events for the ChangeListener interface.
 * @param e javax.swing.event.ChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void stateChanged(javax.swing.event.ChangeEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJCheckBoxSwitch()) 
		connPtoP1SetTarget();
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void updateChoices(int dataType) {
	switch (dataType) {
		case ODE_SIMULATION:
			getJRadioButtonParticles().setEnabled(false);
			getJRadioButtonVariables().setSelected(true);
			setExportDataType(ODE_VARIABLE_DATA);
			break;
		case PDE_SIMULATION_NO_PARTICLES:
			getJRadioButtonParticles().setEnabled(false);
			getJRadioButtonVariables().setSelected(true);
			setExportDataType(PDE_VARIABLE_DATA);
			break;
		case PDE_SIMULATION_WITH_PARTICLES:
			getJRadioButtonParticles().setEnabled(true);
			getJRadioButtonParticles().setSelected(true);
			setExportDataType(PDE_PARTICLE_DATA);
			break;
	}
	return;
}
/**
 * Comment
 */
public void updateExportDataType() {
	if (getJRadioButtonVariables().isSelected()) setExportDataType(PDE_VARIABLE_DATA);
	if (getJRadioButtonParticles().isSelected()) setExportDataType(PDE_PARTICLE_DATA);
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB9FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8935711DCCBD35ABAAD79CA9A07AF395204B424F5D2F2255FB98D4DF1B96ECA9A5F1523CEC32F66CA8BDFCBDB27F1DB2E7575635235E440A6A6B641847782091BC2E2A746D8E002E3C3639F813611C15832113189DEDBABE96D15D6C8EBAC82A4B773FED6ABE9E58177050FE1F7674DFBEF5E3C19F9B333AB044AD767EDAC32D48AC251C6017DB9DCA488CA17A0EC78E3FBAF47B53D3122D4B07C
	79B340B661F230982E1D5016232EA82D905614144073B0BC970C2FA85D89775B057E41D99DF013E21E91E8FBB66319CFCFBF4F61AEBE4FE672BC50D3865797GC6G07572CB7F07EE0CF95434FE4F88EC5964055A04D27FAF6B1DC815014G2E9540D5C3699538D286BE515C446806EFDBA02C7FCE58329547B10C49A10775ADE057EA618FC51935B8575F91BE31CF8DBC0FG60622D42E3975E046B9167046F2FE32332D6FFE9CCD214FAA99AD243DAF3F3DBDFC7D7D71F24E9F25097F10AC1C981713D09637D21
	2704749704BC98EB9E413D6043B95CF09F821875A647DB7D08DF8B779BGAA5F626BBA7A469146377B4A5F8B3987824565A956D57796DD0F6B2D583A9686D6D97EC476E02A9BE5BD9B5A9681AC834887A82BDBD1DA8770B2597B8F3AF01DED23DA079A88284136D028DD7104FBD40F2440BD2CF4D4E338DEE9CC8DFB84016EEFF6CF1D89BF53816D77AFB75C0F4AE441C13E57D93D8F89F9DF7F02E53309CC7296DC31586B28AC066BE2B221F37FB1655CD76B62F5B1FE6E3302F16EDDA73EEF5DE8B2F7D363772D
	3359B8738DF32F2C433D7E98510125F00F7D30BF455FCA703B764570D4AFAB899E1F9B20ED4EBE2E9B6E97B8AF0E62FB048A5B9D26F248FCBC43D2380F72D0D11F484BD7095E9D2927F36E50F9F90C60C386BC6525146071F9BE34B9772FA8257AB54301F63A811E2B9AD614B6GF4827881E682E4B6F09D7BF6577E6B54B12798565AA7E545A305855AF7C677CBF01522DAD86C8AC6B4B1B8A68D08E1D99CD5240840E8BE550DB2980D6B77D9227BE3B04789A9A805C5CDD603B03A9810B4A99C21B4F7F4EFC31A
	30C4DADDF2C0028982A1010F7B456E6DF0D54408569F72081A9423206D77F7631EDC9287E0C498G6E29DC9EA4BEA59B563F9CE035AE0757CE62F8A725B0780B66E6072ACECC06DC3892214106F2FE69947A0E75C0DFE663B2FE0060B4F84E3071F186CF3EE89847AE0FA17BE278B23731D9BE6CB38F681758385E7C4A4D75463E75C1216E295BCD75A68BF4F8250D79CADB227DFC1E0C0B3CD59D60FE7EFFFDEE38AE31195B8F07F9E95A27779987499C9B5350DAEFE27C9774BD5B6944BDEB02F539G3483DC56
	F3CED6D35DED97A392575653E1D0B4499313BC6573E1FF8D6399E57EBCG1EC73B85FAC66E85A8A7F4B8AEDF5BD252A78366DB841087D0FC907CB3008B60AA40FC009C007587394C4B7D73CC64E41C53B8CF62FAF7336B1F59F56FCD4A6D7A762EB909D7AB5BCB4EB367F30749984F336BD6F6AD7F08F825FB7DE9870EAFDD12021ADD8AC9C18F5CC4D85BED8E641579A12F2A77390360C36037BA40D1C4F47C5D8EDC1728E9E1F9F4D213F82C20375FC7660ACC0E06E2AE0F4F1565C07928D36037F47A079DE873
	E3EAB0A89125533B30762F931FA3A9D2G186ED0A7039A45BF5203F15904F419793CEF75EC9250E7A91368BFE92C33D0C4B90742F2GD499DD3020B422BF5AF72A4AA023DD0043182D952E2EC66ED7267E427D4A1E779F9016DC88D94C4E464C7BBEE7D954C8FD40324618DF21BCBDF44A1C77C54477B1FC04727AFD8F4A21C73DA4CB8971171574290C62D86D131A2686CF045549100D5A37738C6257415C9BGAA8FF17BEEB80D6D5C17B2436ED095050A995A5C07D096CF7BAA8D6B29BBC4E591531D5D4E8A5C
	CF69622484C794BA75133A4C77B8DFCCE89BE072276DF5CE143FE2B88F0437F14E677CBC8E1E47664BBEC4FD6F72C3AC4EA0F459B227F340B37A928FDCB500B9G7538CD046E16F1BD2EFEFB3A711AA7B8DD49E16A132A615AF0B8B6DECD935295AB1C4E89EDFE00D90769384B60DA89B4146EF40053E9CD142F0CA6C657C467F53F03F4AF86B9DDBE600B8137679DCA578457D774F9B72B1CEEBEEBDF4C2E396F9857710FBA5D7AF7289FADEB0E514756711990275B856D0DGAEC6B7872EEB1BF9CE705BDA3487
	12930C0B1744E8FDE48A742F39391BE8A00D7F6FA0BA0B3E2D3019477985B26ADEC38B44C7GA3AD5CF67EFDAB0F7367CCAC908ACFBCE5CD153FC4DB683A6EEA09594E55FF1832D25D1E5B0BF366C1DBFE8B576B7B1D46D823AD92D147E49266B01D9BB8E334AD0A9BBA33852EDEB99C513A4079C539FE0AE710DF0C03981A790D7F295FEDAA8F037589BC063EF65C3856456557AC4F27D82BF0042D71C8EC2DF9FF793D154622EF90D9222D0FC0FBA6G357B4F923C9C14B5D9D46477251ADA4AE3C370B5380A9E
	CF0D4340E3FD5BE884BC7918C6D9ED299D60FCBE78CAAB63A36721FB0492719396335CAB6367BE4BF246DF21014F3227D6D92A81A82FBF56F9EDBD3A22B4FA14735AB90EBED98C7BEC5437B4F80D3EE561D19A7F656974A1C22F79658865716AA067F1C64EA221EA7B2D26BC92BBBFCAF92B7A2BA1877849AACB7ECFB80FDF9B67BC7A01E69EG450FCDF07CCA40D53C4BF9794EC4BC6F0F771B715E79AE9FE71F424799865C9CG5A6FDC5C1AF3G3F9E202A154FB3C7B9007390A7DCCFAFCEA96CD5438149D303
	BBD9AFCEE975642C2EEFA34D47703E166B66DBA70FD2DD163A699E1FBEC949AEA48EC67422FED21315FAB2446951F1707DC4A5D8CE7FBD056774F52D5CF66F59EE5413A39F3EE73A87334A7D96CFAB15FDC6EBEC8F66672E32E46772BD0886F82E3792E8AA5A388CFE94403CC95624C0BD7E5327C18E526197033EE491F4C1BB93GDD7F8F5E405A4EA449DB0E112A05DD54C4576590D846G4B6BDA02E54CD7F5421CB35A785E944B463D49EE23FB3ADA67EBE990659111700C14BC9477CC90639DB0AEED4C5F2B
	CEC560881D8C84A33A7D7F264DA82F6C4D992672725F1FE9D95FC665342BBDA62F59EBD6D9662F6172EAD0393C9420195F4E793AC3C51F921183FC29CC961D11ED3AACBA7C2ABCA6C5EA3A487E5271FE21F29D4D0331AABB78F8E7C2DC57A95D22901F5781B4B3BB383C8E794DF4F931BE4E4B977FFF3A7CECEDDB3CAEF7E09A2C5C34BE1F3B4875F9F9874FA96EBA45EBA3A5F6A6FFFBACE7EB30A767EF34E6708A7193F6BB2D99EC55717B885EEF40D3997E0E78C4FC4E0036C2BB4FF3837DF836FA0E019E82E4
	82AC84D88ED0F00C1FE9BFBFF3D0C8D3C7688AC2FA9F94153E38401A67733D38E6080559733D3DA8DF928B8BBC8F7C61G6EE559F108DDGDA815CG51631C07B77B5272F0ACEA4A831573BEB9DD7DAB73F83C2CE93F16347D96A774236B3139B1364905368200129390D381F482F8GE61D606B0A763524DD97EA13CB25EA57111809B0BEFFF6136B7B2F347D961C086FC7ED6325F60C8BCDE232DE51A32B54DC385E7B3CBCB6DBF2026B5D4303A81FC2F82E6C043C9B2093C081188510554965731D3E76F47251
	4BCBA0A31E77A50B092D779B63B7A727FFCA5BEFE927D9BFF96266661BCA5BAFAFE1BEAA6B7F6E6732DE5F49FD4CC16566F8E8CB5B2F2E4BCCB74A7B7EE42E9BDCADD81E3B5A13186762F9E707B15DGF4BD37915DA1F9F297DF4FCA4257022EE45F188A284E3DBE04BA37987A2D84A8GA86B067C8A4081906A66BA375BF5B41D4EB11B6CGDCA7640FA66A46E478C1A0D106333B6379B617FDC6D23F7A04672CEE53BD8BCE37E7CB218F4E374E501776BBBDED3F3213A0A3G4F49D8BF3A57FF9DF859FC2FBB7C
	5258C43B9AE5FA59A21730BE5AC96E836E9E467D480267E5G2B81B6GD4BBD6149E8198F1707D8839527AG5D702F47873C2D5E5C1E58137AA56EC954E1562FA0295F75595FF3E97B4DB61D6F1B211BDB5F3224FE096BDB60B073B90A1F6F6D928737518B97F12CC247755BA82D45FF1D6471E5BD9087GD05CEA62830E4033D347FD0B4C2941F3C68F1FF34F847A1305701C83D0GD0F98A68819C27F4DFC8E8C6CE51FA8856D6E61C22B58F0C9DA94D86F9842EB8866FFB3DEB40B9DFE0BCCCC7D7297333F3FC
	04490E57102FF72E4D6C1A2E7E0EF9G3523CB7D296B2C577A4D6B2C9F6FB72F33FE2AFF9B4543984637C73C7D367E696B30F776272E432E99C84D67AF864C79AC98B06773F7835373795CC0AABEF793597ECC437AF8D27D7794C49C822FFDFA64D2C6F3CCD3FBEC7A58AD6E546BE8F71B2F23530DBA90D1A743E3D2A22F275DD5FADB4C39E18A0A6CD09AB7692FC172A4A52D758269CF5BD275773BD349032E7515716759DA1D5E309AF05A186FD03CE86F7392ECE591FBDEE6E2C3F43EC787D34B6871C1F399AD
	9BCCAD236F8E2617514A4154B27A51E0FA992D9BC4990C4A9E488CE9351A4AA6FF5C288787F4194D1B08175981A6B34FD9B37F1E47E4D500FE69F43C5C68F8630AE11E8E54B76A072BA46E072BCE739A683D9266FDCEF876834CB84D7D4E973CFC6ECC40E531F91630EBAEDC57A54CCD7D6E3324A633815A4A1CFAADFFA8759E9E9EB25F4323C3297750BE14FE8FFB06D26F21EBA87D9E1E9F1A5E6E1FF8291C780DC8C40A10E08FEB9BCC7E5F889865FFC06FD37D61CE1DEFDA8238A4054332C7A25E02522C896E
	54F9CB262163C703B8669829B855C7922BCF3C14CA6B96340557ADE87F39C14E1FD3D31DB61D3F52E3D8D7892AC1096AC02BE96E374BC9773D41194AE66F9FCE3D5F8F8D1B6F772261547B7D68F07A7DDEBA1CFA3F1F9CCE3F5F394366B69BD00DFBF9D03731B7C271B6FB1061FF9DB252B78ABCF63A68B713251D4950EDA2CBEACF17C8CDC801762BCE3D56FB8A579A8ACBDED08D4953A73F0FE74DD2A7790E0C44C799F01F29537F5E0B6F96957132BA29019C4E10184C47F32B92F82E86688498BEC3F3AC9A3B
	BFBC05EB74C2768C7A5497124684E7AF764D75733E0B01FEB9C0810E89ED8D3DFC4E7B2EE2FB37C39C15943D6C47E4F467BBF8E69AAB8106CFC7984D2109C49ABC1D65B1A3CDDF38C21F234D631149CBFF0536F5CD208E6AD9C69FA90032FE7E4D38A7746D100D3DC714A70C35424CDEAA07453D3C1638E3522CFDF9AF0FB7BFCEE2503570DC2863F2882E2CEFC5692EBE0E3379F8DCDA23632E1C65F4D19D17F716534D54F10FE8084B0567F5FD5CCF6F169275EAC3DFCC17AADC296A03BB3D68777738923FFBFA
	0D601BDC095FBDBDA069B5D6E8DB6862BCBDC856D8598F4F7D9CF71B87F1F6F8F66938C19B627C709C55F10F5974B56A386564BB48DCF84E6F673A34A9406B3DF583C8330BBC8B833C4FD912BB0660F9060EDBAC6B713F0E1BC5F202BCF84E57F14F68F9C261G1F6F560B71735DCB1EBB4FC2CEF0963F31DCBEA668BA7814379AFD26B4D52D970C6DF2C08A6207C7B536379B6B2AED079BEAE92DFD5E14713B4826AD9FB3FDA70F10FD166EDB8E3BEE58B117FD97B947473F0BACF6C39E6C6636EA9B41DCD94F33
	1111666693F0F0340BBE8731FC9A8364917D437E1A1B47GDB48F7E4D9703C94A04F4D5F25C4DF676F86333EFC0F105FE17E9EF4356B1AE53D1B3D4F9A0C3D9BD89AD8E5E15FDE7A791CAEE84F98C4DCCD5C3B8C14655DC5B41F246D67625A71AC7E1CE0EC4F08EBC7FEAF4775FFAC2E9DE5341D355335BD39172FED43138B85659BB3D3FE231A4B56D4482E33575ED954D4FE454A5EBDFBD33CFBA6DE092EFF5E842F39B51C63F5AB47A447794FF13D1B6BB53E3F217E7CE1AFFFA7B0CF27DB63A56F9D6190257A
	D8006EDFA8BC07741DBEBB63FB6139E45BAFE2E3F1D88AB4AB81F6B099651F23F6CF74030C3D5D979B7B37E779582DC366E3EFB40C6D9A0A9F5BBF949B1B2EE339066B17026879BD6C7C7A19266B67903751C692374E02673BF45C173C5CEE178C693E1060AA0641378EF3DC8D4161B790787DG45FDCDF21B6C73F40063DD8FDD05BE6F329B1EA35DF7F4C6DBE16F7B3C296300D5E45DF66047B94C75E6018775462B86B5CA73987153D721FD16CE9332456BD607BF367FCF596C5370E55CDB04EC9893E1874367
	8CA76E7F1CED98DF8444289C188C906EA9DFD95B102F15C03FD6676BC060D54160E793DF235560DB78DAE65BC7A5231FED386230D885F35B2EB84F7C5579181F25738565F8B9BCA0476470BD194B41F95E5C8ECA8DF270G8D8237036879C4B9FC519FBF57360958DC457ADCB3CD66328A534BBCB3E92EC7AEDE3F9C3FBBE414E345DC454247CA64E3BE63ED6179187F27B5DCAB49875782DEF80F6703BBBCEF41F5C09CE7155AE3903A29E1D22A8524B32C062430F619E24997D9AC061E6B3B39EF5B975CE43F9F
	5FF0BF9AC3FDC04E536A77E2322778CF92FC1D81CF4F748ABFBF531B206D2E77F87D3BB100E70BF08162C700C6G870082B06B821765FB1E86A1CD6D5B25069C100CA90672967B6E5EB4A7198E28DE7C63E6FEF6AE7A66D7046CDB6CA93F993A6B825507456CDA33664EA23C5275FDBB086B4B073612119525F5GF6GB7C0E8042F6F4B1EBA3ABED407B6CD93477C644BA83E3881D91A0A21B1C921EB3BCFFFBFB6F3A4E6B7196CBEEBA45916E83FB3133C5F9283CDAE3BDFE7520F5A55B2FAA607A7A5FA3EDF76
	71DA4B8668732AC03F25AD91781F02F713F17C4CA51FE7B1D97F380C4BFA57D76F956CCF5BADFFC9A16BC61172B6D30C59E0CE385ACA795963667CAC964D7921F37F6822202F69B98D693D22920955B2CFCA4939642844734175A24DA54BC661BA4A62A66247323D5C0FB50D1A7B4C6705189FF3G4D48E84CE7EA2309FE6C59CDA89F7D9BA29D7FC4931E0974C30C587BC43A362AC8ECED37F8707DF40A387D102D2E7A4021DA5E4F960D756B313D16586FB89C28B52FF9E4D1D1FD2FC13C8FFF8FC05F547BBBF8
	A5F63F78EAEC6CAA7F2B29F932B5B454D59FEA28E536A3DFC3FD1BB14A6D6496BF72154A2762FB48BEAD8C11A92F85960FF3FD18B3EA268F1C3F6FFE0077344FA17D9DDD4EA84F05FE73A136D31E3EC87AD1FE7AA7762662872C369B56C78F9C1768537B14A8288BFA4D0D3AFE56D62EE0BC2FFFC7C8BE1924AB22F6BB1344A9747ED6437DCB7E9B0D43EE7C1C587F114E416A210154B5B5FB403C26569DB0FF67618CCC7F6E459DC87D6E65C240788DBC5D772CA25C1B62B130F9GBB009BC099633A79CCGF513
	3E05CD745F47BCBE2C6961AF08B4BE7F72E06A35BE9DCC7F3B2355C17CDEF6D485638F60789C7F53A06E172606881262FEC9EA21E1596757A8A9437F1AFCCB27C8DE02E6B58512474D02F54D9F63F9486FC81C1D8D4F4BF45C10DE634955F1FE126F6D712CA8ED75F05C71G62DC706C51F1DF8D703EA18F4FA5BE080BC316GBE57B3FDBEC277E50B95FD6EBA20AD16A0A782E885F081A8GB3G66GACGD8823092A09FC070C20E8A308720894061657B784DD0EDAA1B86A4ABB6104D8C0BB2EEA7330FDA3711
	7F9198CF73CE4FBF0D0BBEAD7138E89650AF71725CF90544F344C2AF2FE18824EE5560DBD15A89B0626334BF77F21F95755D68B97CD3D2E622EB8FDC43F3AF42FCA8DDD7E95CB7BF8BE17C253E695745FEFB93D7D7DF89FD725374E3B5722254F671E8D1FA3BD8DAC43EAD87FD46EAAC45BDD1046BD23D5E08245B5F3F95D568382E08D30D104AAABD233EB711BA674CA776A178617C77733CED2BAFBE37A94569D8CC90765DC8BE5984E3BA7C3118E0441F9893FCB8191A37CCE2FF8A74096A3C1D8F4473D637B9
	465B63814E5BFC3FF93C325940DBB6506498F8DBED600D5A756D44A764B3BC454DA538E21962286CDF31265E633D5674FBDCEFB577FD0D56E45F770E555C773DEBB57ABEF64610FA178FEC5C2D53F56938C19DF70A0C09383332C7736BF897618B711D92CE48716788DFD3846766E70C81B748F09EABAA8EAC592F06F9DF59CA6A993222B09BA5B4F29054D617C71870A807C4835FD3BAEED05F0BD6F7727E9CF39B77075A6E6C24FA7B754588FFFB137D1E3E61FEB4575408BF2C1693F350EB84DFA7A766201B3C
	FA8E8AEDF7497C6C791557B83FB08EFEF5FCFADFC5E5D7BD1EAC4F3D63C6F9D25C99B919EEC0B652D15BBCCDDC8B355BDFFA270D131BA0F8BFDCEB92FFCF676B3F509B3BFF24AFB6D7CB60FA6622E74DEF837C2C29993F5133A64FA5883CFEAA3EEE784D8CB1A30A5F955D9A43F3B3E2FA723ED7506FBFEE38AF73FE74716F5E0FF48E2A939BCD7C6796DF32FD9646FD97B9827B20DD173E168713472F8C9A47277FA7C731329C1CCE56B34246C8D8F8B1D9A75ABE7B032733AE58ABAD0225CB5864A71B54A5EC1C
	42DD0CE2DE7F9500791AD04EBDF7B57D4D27A6EC65AFB82BBD22A6EA026574C9CD58CEFF39D19DD627A27B4768EFB7B4E1934DEF0103F21D03AE6276D6DFBA58ECDDF32F55A2944374749868822641178397E14A0D950E81428BD7B9727F0AC4AE8A9F90A1A6E3718F061487E1462DF1F96FBFFF6950CFF37EF3A0E295B6F589FF4252F21F94263F747260EBDF76937C63C2E9C80510900FE4E0A7C2AE9BA35AE5C5F288155EDE7C89E6C413BC7A371930489D6487937B31C2393FD30C78B514D60BC3A88E62575C
	420B34D991033E7DB491ECA1EBAAA2BFE89184624E005F6D31BA27BE4A76C74B777E39FBED36D5309C9736A82A68B9AE0EC1A85BA514EA7E3094712B0AA79AFA178ED4B2453FB0AE0661DE56170F0B2DF741E92CB3BD913DBBE36B3A2D47AD90EC06057D0492230BFA1D425361977167EDD238CB437F7CC08D6F515B7E66956D75FFBD31F358A29431C5226837C6C3305FDEB1A0AB176106D6994826EC03C7FAC66AFA004228BF8E6B517E6319A76E79611102FC986C383011289830D49123BAB59998154237E45D
	3E7B79B92BBF039C07DA79F643482590E384A3784B95983CBAE14D3CC9E73D7E73CFB45C7E409F4E9156E3DDB561D53AC950F53FCFD2837B5B3CDEB9A86983400E41703B75611126BE01664568EDBB3A1F7A63A8E818DD30869A9A483FB6726F817C57C6B0B602319D00EDAB90267D13589E65EBA6EAC99E2AD00722F837A32A1BC53191F43340AFF1BC53180EF94FA42CA63C5CC75E7B91632F56A0BE2F5654EA767F5AE842CB316F9B2AD1AAA2C9591BF5B6B030D1CC5958F29DB42C56D87B21315EDF78093114
	6F27ABC254276E89A5567B2F28E8ADA3003F9A62F9631341FDC26ADAD0DFC0D494CDD4A65A029E2C8B35DF5624D8ADE2060A63C7B88DEB24ED1FA45F660C9B7A7C9FD0CB8788C999F90D6099GG14C9GGD0CB818294G94G88G88GB9FBB0B6C999F90D6099GG14C9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9A9AGGGG
**end of data**/
}
}
