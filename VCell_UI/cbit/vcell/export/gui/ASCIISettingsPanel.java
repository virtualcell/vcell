package cbit.vcell.export.gui;

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
	protected transient cbit.vcell.export.gui.ASCIISettingsPanelListener fieldASCIISettingsPanelListenerEventMulticaster = null;
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
public void addASCIISettingsPanelListener(cbit.vcell.export.gui.ASCIISettingsPanelListener newListener) {
	fieldASCIISettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.ASCIISettingsPanelListenerEventMulticaster.add(fieldASCIISettingsPanelListenerEventMulticaster, newListener);
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
public void removeASCIISettingsPanelListener(cbit.vcell.export.gui.ASCIISettingsPanelListener newListener) {
	fieldASCIISettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.ASCIISettingsPanelListenerEventMulticaster.remove(fieldASCIISettingsPanelListenerEventMulticaster, newListener);
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
	D0CB838494G88G88G460171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D455152081C2C10D4ACF90500081828609ADAD7936F40BAD6D07B56E5256AA5DE23FF425DBF1E9CDDB7C4A3A58BEA68102818992B52EA051C6CDEC9A928CAD852C04A410C0004144C96C26E45E4C3C493CE47EF873C2A692E04F39FF73E672A6836CD7BE8E6F3DF34F39775C73F76FBD6F8DD2493E344D1BACA51234E933447E7CF913A4F9DBA4295FD6F91463D27C36A2497067ED00DD52E7EA
	81DC53202DFC092D28D8BA19DF884F0B6139790B36225761FE37F430BD3981EE620CE30736B9CBDEF8F864F1DEEF6463ECA54F6997374375E500EC406135A00063AF3DD84A702B991E43A68B60884D139777B0DCB3BC5B81D281B28314FEB9DC371111479CBE44683A6E4C1056FFC7336C40FE8CFDF2A88FD25EE6F6AD13DE5814974431A4EDBB6399854F59GB879FC691F3BBF006BD1EBDF4FDF1D8ED52F3E64D43C5EEAA59C8CE87A614375ED8DADADED0A2E2B7E1E10D576ABDE3047C56C2FD9ABA47C12D487
	4F9B886E85994748027B95G39827F1C8371C7B5DBD1BB00D763737A687D23CC6E4E4BCB24B5BD5F5A36B34E3C12B4BA1FCCADB22F248F9C79EF909BF4B4212E1F01366DA1DBD199C0A3008BA08960CDE27B8B0DB84FFA075E907079827E7AE0703857251D893894AF5C43CC9DBA43FD22B8831ACB1228FD37E8D5A6720C84142F64067928CEB2BE64364E7C64C1E9751726D8361A68E4F546E54B0C90554562D0C4A7F46CB3F147FEBC946D0B51E3F7C8463125358736EEB699FB4F938736BE437A59E8983BC6C7
	DB7B092D7740BD72A1BF458F923CEE40B33FA4F8FCCE0636F5BA770D4E1739AC2D853325625A2926FAC87D46C40B351F4A60690F15E52A937B1E54CF47FCD548320060D79870D4166709AC783C915AB2AE590A28FF59CFE21C368F580A02G49GE9G4BGD683648DF09F1B57387FBAFD4CAAEB7A71FE556BD2B409725A1BDE032B9256B539459F52E53FD331491AAABB3CCAC8E2B4EA936A40914567A73E6F04B1CEAAFEC513F5B560075EE51F22ABDA08528CB46DC29ACDA12D6D2ACF0181FCC10977FB2DE9B7
	DC3DF2C87FB46812F5A5C2C15B47B521CDAE49B66891BA00FB2A175B1AD1DF45E1DBD1854021B05703658476F7DA51A0DF9CBE5C9A887475875BF1A6D2F298756CA23923956EDDE12E6371AD084B0167B5221F3F1CF84550CF236AC471E5ED1047186EC01E8DC0DFBA487D66971D5CEF9AF7AC142AFEB05954EFD24007EB86D92E9C0C0D1FEB24DF14ADB848737C96C7A7DCCB874D6307834AF2A3B19F1DB37611B1B6A7204D3F097EDF96B67B4DA934D98A4CA393A0E7106BDABFD1C6FD77389CD238371E534051
	94D7C473D44E194E8AA6B36A9C97C9DC0F76CAF40DFC97E0A72143FE795C32884F9A98EF7B673622BA00AE00A400CC004781B68214DF369535DE66BA7FEFC71A091E0CE39A47091D6FDEF6FD1BDD5F3DC93DDD1F6D5EF3464A1A4FEC49E566F23EEA52C7913B6EE0571D7FA0D9292DFBCFE27F4AA5452FB7AAC1456F021B90EB3BF892E5E5F9489D90BC83849F043C55G09A2A470D7CE623CE4DD57D4C73F2E703D02E89FC3468A75BB02111447471AF892759398003CA5686FBC09B16F8C787D8AF1BA41425A6F
	A6B9C771AABE903AA1506F57A9FEDAB36E4B7A14C11673E6B6EF11B0E7F97BB1FF523DCE160CFA8EEA2A8FDC99D330347CB266234BDA29C1C7BA60F04F168A574C4BBC2F8C7C1E6715724F67CB4B3A03963335B1F54E9D166DC3AC1F8CC5728A15697696F359A7115C4770A1AA6B93AE54431940A5D50959FF65931E12B076F53CDF57837E13DA20BFD8CB633B26957147E06CF6G6F900F6F62D3584EF3A98B6C060057CB558CEDD9C3280BEFBACA8C73C99A223A08784E0B270A511E4A45FE8516A8CC6A27054E
	8BCE3F9253E6E37A276D5BCF237E3D067520738A0E792C136F03B730710A2F505CDBF1056DB388DD3A0B532543B3661225F04D81D887D08DF85A1F2608FD7555117AFB4F4D696CD7E9CE8A43B579EA243FD15710AECF50E5C05BB200DC56EF9934FB81A85D918F274B39C665DAF30D510D2263E60DC23A5F8A3AC6F86E82989AC569D2607A16987757AA275B485ACBC653EB55E863BC9676F23A5651B40F3AC6C768A373986863F4BA60A782E4B23AF5F0BDFA8BBF937C3A9263011C89FA65CBF238BAB4G7EF778
	70A96201F47F7FD33206E3EE335E4277795FA57B7FE4F8CE83483E05474EF7F770FD7E243E8CE9755A5A2D714EAFABEF2173DAFFCBA4F6D2726A37D05F7E7C8C0ED9F7AB6C6FEF65FEBD6E34F12FD19F8A851CAA5966B01F7BD32BB136A86EC86BCBF0F52BDAC8EF016497667ED95F0A7262BE0821D95EF831FA2F29BE8C51A771BD7415934639AE5DF945D294E72E1D3752B906EF0D4C75D820FDAB5D0B6EF6ED17E82CE7C3FB9EG0D7B4F89DE752B3AAAFB554F150ACAAAE30D7F9D384AAED7C52BC1466AFAC7
	88B239D327229E2934F1B9973EF50C7B6A033325C2F90C456C6CB5F1ED0F25A2094AE7CD0A48D9F237A33F82004A7A1810B595E8D6A6F1D96B944C493256D3CBF34B4BF6E3EEA99CC377FFF5E3B8FD972157BDEA084AB8749917F1D44EE229F477ED26B212B89FC3E58B0E0948181B6648DF1F46E51C2EF09917814D86G0ABF6766781A3136A24FD8AE4BD75C5132DFBAEBA6FB5AD85E4F9BBD3C1F4500DB87C0793E94B56772F1303F81880E63630C6AB9006310A4DCCDAFD6C5F387B41F622AC0CBD64B83FAB5
	D92B2B6BC9F3935ED7F25F7C43690F28AFAB27280D4F1D26E45D311D913F286E57D5EFB569621C2397F2BFF189F6267FFA8FBF53A70D6331BBFB3751CF0EDE3BE0EA03641D9E4B52F1D477EB46C5EC10F917A3FF59DD5C86AE8FBF6B55A4032E13398E9E756039C95595DFB57E532643992441A37BFB062B20855A1982687CFF78BE56F67A49392509D4AD9AE5DDEE9F8CC2E45858396EFDF5BB4BD5E9B0662AE4EE9BE714ED0A47D31B9E9A4F654AD2D19FA18557C845C5F1BFD4F13F834125BBBD1F8486C23004
	767B7CA1917F3F2AB76AABFB6BC4D3FDF96625DADA47D3BD6963A37ACA1D65484F1C45753527176BEBB950EC94F25D5A0BB9A5247A78D419AE1AC33B04AE9ABC81552904AADA08FDE9FFCF74F29F2D7B822CDBDF607DFD5447FD1D52257571F15301E67597383E26B84CFC39E4826FE72B777F674B4FD556C77BF2839E033DB76D4FA73C5C1FABA670B3453322B6E21F4074BFA1F2E6CB1EB07C7CC6EB863FA335019993E84DE007403FCCEA894B8CF82A43A749DA084FEB204DBA111FF34FD8F1EDDD8A4F2BG72
	G8AEF03FD89C073EDFCCDFB2475C3A9C19D21458F47FB3F6CED0B5AD873737C991CB36C0559732DE7D03FE4AFAC71F360D74F23ADDDB066A800D400AC0015C206174EA61421A9ECAA8355739B2EC4752F3C5B22F5CD795ECF48D7F2FBB49F1D4F2EB65C5BD4C1DBB3009DE088A08DE0A9C06E6DFCDE1DE70FA41C97FAD3FB003ADDC36CC90449797D1B1C5FCF9272C9F7C4735158F86DB86E8BCD76E41F48AEB5C043057BFD131D6F4DCA6F60FEF7E7BB6A478A4FDE00A400B40065G39G85A9DCBF8B4E9ECF24
	9FD1DE829D71F35FF0B531794ED2EECECF0B92726DC9B1637B54FDF363B9937255450CC7F53D70AC57F5EB8A4FB17BFAEECE06BFA664CBCAB1730D1DEDEF187B86F78BF64E7DDAF76CB997573B9950E796G1D4F3344F7F0FDD9A166331C48A55DF97D35AD6AF3DBEC68F3A540D783508C6082C886C887D8F1A77739974FFE144867D8CCB6G2E994E0FA66E46F4586B095561B3B1F21B6BBEBC0C2FBA663960AED31B29A359EC8F7060F8478C3C146F4808FCAE20CF87D8EA6023367E2B6DCDF3DBB7F894E75F71
	C0187965BD440E4813F3974F81D7BA509E85F743F9946090C0BBC098E092C0765D5C9E56F389F300887C6B49816F745E1CCDEA0671455AE4655DE6FC2B0771DDDF7CBD1B106F99537166765D5C7C968F630B1D1FB449AC67BC666036AD1D44E37434977B32CE3A7E98253578CDA1D26B863ED1G9477708544CD02678C013B1778D38EBC2F91E3EEF6E2BEA91CECABAA87E8867082A48324CF66B4A504A6FBB22D07E0EDE555E4DA73403DA325F9CA3643355C40FB3DF3E8E6340589F83CC21E8DE4AC549D2FA1DF
	6FD8DB59B5D17D9D4F81B40EEC5678F5563FDB4D6B2CCE2BF91DD5356E22F8684378760837DF340EDC079D34462F43FE73FCFCB93FF35EDC4E15674D65FC627C48F27E78FCBCB977925D7ECC477A78307A6FD958F1C83C76E9F5EDE3B4CDFA60CC2D687B4D367873F8274DFC9E95ED688321C03F66D4E2E55D5FD6AA5AA2498D0F20A88E25B9C478F5B8BCA943667AB16127ED71781B5B6269034E75A525084D556A5682BEEBAD4B9D276C986F9BE27CFE7B94F6161CB2BC9E68F8535B636BE8F63B390EB25A636B
	E8D1FBE29DE537475751575B936BE8C5BB6A40213A60E4C82B55D4B72B94239F9C90BA1B680E5659812633D22BD9FE2FE33AEA06EBD70C5EE8FF369E43B88D68EFB48FBF5F4D73F0F08A2F01EE6C46F3DF86BCAF83D8B505671D676CFC6CBC4095CC25631432EB95DC0FCD0D9E1B665D9F111ACCBB3439260ADA3EAD3E8DDF37195BF02FAD3E8D4BED09ED78C7DBFC9B5659925B70A35B48F17F48EBBBC95E88051490596CE1ED0369FF1647287F8302276C5A6BC2EEDA823824E81A6AD2C83620B4CF7BDF97328D
	27217D3BD46C53C9AACE5521586A93AF25523A85ED61F58B4ABFD66572D9750035D648372D896BAA7E00DF21BEF01F5D4C7FF4E677642971E2F6DCC7FCFB5F5EE1EE6FC99D716DBD3DA3313D53BB625B7B010E4476DE5CE19E331F759AED792108315DFD51B17BA143BF55E7242F13785E69FB8EB3DD4EE0BA4CB251A50DA7872989AD07764727723AD5E988679A5494B7380662EAD3BF4735E6CFAA5257127D519A204D93742BAE603BC52FBC986857E1CFC4684E0833159D1E43GE9G0BD369990B6E5D9F9A40
	B93A61748C7E5496D41CD246B4647D321D731640F385C0B30087A0F99A9FF34E90361FEA159D0AD7147D180E267D8557CCE3A54070698823B95897CB032B336AB45234E945E20CFA17CBA5AF7D3D342D258FFDD01CB25AC8811471F93622CD685B21DA769E711453D8AB4C1BC675D0F28F2FA5FE314B2C3D629E3E5F1449FE73A8BCDB85EE81413960D997381DB25F170E92387DD6CE37D260D22C1CEE3540593A90D7B59DF26FF41E27DF680E752B766991DF72CC9F6ED7348E9834E35E9F1A9E7B5D53E502CF19
	9E7B5D1345AEEA2C50D6B8034B34054C470B4F8257CD561999701CA9F075D508DB864FAB85EEEE151823404DA15FC1D64D3495B54E643E742B9EDE6FCD1A09B4059EFC6E1C49F93EC2743C821ED7895C1BC24FEB84EEADB907545D8BFD5F4BF1E71D1C4EFAAF9FEF509BBD5E9D64B98D5AF36E45EFAC33AEC842873FF2218CF326B2D0A68A460D2ACF716307C79535FF2A2BAA2B7DF3CDA52D35278D983F0BBC7452AD266F64910A6F23F6ABE757093F1B463E0B6C6F665FC5F6C15B247BF82C565A712CAC4E59A8
	486143A7E161B8AE7734124827FB002F915BA0FF4EFDFC8F701F244F02B4DB519E003AB47EAEA57C9EFFB718B9F736347227666FC1F35B2FD8DA53587B2CB4433B018F9C79F44C9F59791819503EA68DF15BE4E3BC21AEE7EE2267C95AFEA42A9D5762BBA4E33BAF2A9D659D0C623FAF2A9DF5341B355339BD7AAE1F5B46C733A46F171362FE23DAB50B3DCFE45732FF1936E9DD2AF58BFB776C0E736E19E4A5BA7F51CEDEF3CB1E456BD6EB3BB8EE59AC6EF7E13B717D8D4D67F75879BB018D026EE1BBF96F880B
	A8754715F61C11F5B679C6C0EEE4F2974E9E9E7B1BD8DF9C76GCD8D402BCCC70D33E95C937FA0FD7FDA0E74BD4F4A7BCE0D5377E6C35F19B1FDAFB374CD67616B4279ABFE4C7CAE36FEA5F5897F1C4DE3F49751C1AEBC2F97382BA23ECB6F6739709221734073A8019BCDF0788D84FEBFC0F10576CE93BB0F84585F7550F94438EF5F7098093E23B3468A555FDEB97EBE60CFC4BFB3C01E0C7B395FDC6AC63FF1877CBA2519C8F24163501EABE8BA2B22FD6B774E087D3FD655B802DCC65BA2942783CFBA35BF62
	4B53E36D3FB49F7797BEB92C7A7AFDC4FAAA57EF0BD02E9A20BF1A4E65FA4B33CFB264595857E89578963E124576C70AB14F56DCEE3564CB66316DC9E75FB424C772AC9D6F3118989B6F0A68610008310CF473B858E65043D220D91A9E0903154374A0B923477A259299CBF17231D61B0C15AF0D2C733CE1E34D71DE3F9E7F75FF0DFAAC3E47EB61FD454A311149D6B8A7127FE98D77E9B75997813EF38EBF8FBEFF618FF035493D2CD25B84DB3700C6CA350034EA012022690394CB3E48E2FB684B4376BA090072
	0D3FC93EEF5CB09F5DC3F5133DD2F8CEC477946FA178A4831E2E693F94EBFA8A342D1F436B5FA59EDCDBBA67427E91E0A2C0BA40F2005C39DC17FF6D2E1192543E5B8341D6B80CF98D65ADBA3FDC53735DC8C07D62612DFC6DDC7CCF8B246C310DF13F99DABF177AC349BCFA4D4E1936892FF4FE73D41CDFA334598112GE6GE4812C184767B71658G5F2800BB546B3A6C7410AF237864EC2AB290C163A1054EED2C134FED753CC85C64317B020C6131C479FE514579CA8DB4D56C7E18899F0D2B39B4DFE87D8A
	DD5FBBE4DEEBE9871EFD927D1636D062FF72BE9E0E63EB6E27FD125075CF36F3DD6FF960FE297149C64B6F6368FAA213EDF5C6A486BB5DF55B28BC2B2CDC1E1279667250313F6E15441C26F7A13DDB7606A2354C8A7B7033644762BC58BA1F1EA5DDF81D4F76CDA40FCD96B9B3E53EF94EAC12A2F9AC9DE83267C7F2E64E7C58BC765C96540F7806C8609FB904EBA27D90A372BE114EED03AFB23776EEFCBF9DE75FFE30362A6C4041CA4E37AB9461BBD875CEAC5F89D8D0AB5EF12932B750738E6C77616F81600D
	EF5F3FDC0A5C4F980874BD685897DF265A1A1A2A3203B515ACF6DA4268EF2B6673B8F1BBD12EF8B9955FC336699A6CCCF9AD50A97CF31D29BFF079968D62BD65196264BC658B78D968FB1FE3BB15695AE53C27729CF43F9BCF9E085AD3B0BF3A60344BBD026799B77A02283951544F5A1EF363FEDEFCC7C8BE1924B322F12B58F1ECFA6FB75C7F267BC677E1B73ECE945F70DE6FC60055C3BD71EBEA659E731A5A879E73F79EB51E115F3D9C7244FF77723147788DBC357B44A134CD9778C498E086C0964072855C
	B79F7320EF52373031793B495503B5BD7C85114E474FD2634FF5091A78F7C74BD47CDE5691006077E17F9C7FED956D25870284C9F10F12DA282676F8F4CA4A705FA55F52F995B7C153B347CC627F39B02F0D8B78B9241044D1718336223287B84EA74EA6D58237796F089B0267D4017BB129C7E44273D201EB907508958F7033C417ECDC27CBB3215F4C114FA354AE4FDE411CFB8CE83BGC6812482E482AC87D885308EC0DA88E7A8009A00C600CEGAF4090C08AC07AC2EE47FB7AAA6345B4A0D9310198D313D5
	B4A70B0F27225EE3E4C3FFB98BC7161F6E0BFA6CFCDF140BB2BE484F4E0BC4BD417AA02FE1BCC5E81361B98DA0DB50FEDB6439158F5E68BA7C6DEB127007B3E1DC77C2AC0752F9F9226CD23848D634E751487322FC531DC63E9A60E9CC4047EA64C37163E27AD06238C89FA25F1603BFE3B51662668F613C82EEF7C891717760D0314035042C0190292C52B5EA31B37E1973B1623B1DB09757A2FEEE5BA7C71FED56927D52BDC112FCA367499468B3FDD1E4CF103DA8F6CF30676F71E5BBC862EFB9702C9432BD6F
	0816ED3BA5A25B5F4439EB2309EDB6B1DB0AF3D7162D28BCABA25B212C08ECB42EC3448F9B991E6242845745F0D4773F3D925F461B2FA636714BD74DF3DF495561396F0D2B6639EF5FD5E36EE3EB4CD51CD78F44F827202B94384F842E1A7409388E5525FB843E0E480578E68587647843C42E81026B64EB0C81779943B5DEC5BF05A9FB829A67ED39CA6A992A574BE2145028FEF0DB210FCF080CEAD0B6485DA1F01F89DB2C378E374FFFD90D7621316B3B61B393BB3B5EA45F629B6623E74D7364FB5EF0D66C99
	74EF841F1495FB86FDDA645A94E8DB1F45571EC723F2DC67C310D79F9AB9D7D15DDDB3717724A8FF2738AFBA0753ADF19A69E8EC6E47B0E3F17E22FD44FD72A1583C7F3952E47FBDD22EFF21B5F23F77ECE42C6F0F3CA7E7E35135E602589F0FFA68C6571AE367A40957CF65778C3F19A1E1C471FB42BBA2F89EC64CCFBAC81D165E5B8D77EB7F21FBD3BA06C57EC70EC1FD625FCD7C69E7727078CC09DADB33418EB957652F73CC7CB0B34A8F697F4931463D9212CE664F254DA1CDFAE538CF545F7E43A7B33B9B
	CBAC1225C55A62A1C6EA11B68F20954378332B85G693A3413E76EB27A1BCFDD5A41DFF01639E4DD56A54B39533A341B7EF223CC8B8C0476BB69EFB7F4E98BBD5F0284BB0584ADA46D65DE7A70F07E537767DB2482981EAE83ADD28166F210C223526443B2C0E461AEC77EDF1150C569AAD162F0AC7E41AD6507B0620E28F36F37E69D7C0F1C9F5BC279521696698DACAD37A99A7D25178BDF7B321F601F1036858330A544A5994489114B6610BE68D5DA259277A778934C102E38443719B049D7498FA676E305F2
	FF339C7268282DA32DD2019F3F6616DE214DDE595F331F9E840F10B9EDA2BFE801459C6FC15E5D11BA2768E577D7F73E7B7629B559791265047412B7A03BCE48CE584A36C85BF40F2604BC812FAB9C7C18F6D4B8403FB0AE006E5E94534749D63743EAAC046E8B4F1C3863199DA7E09B9156247D0492F7975502425560117167ED0A562263FFFE90504AC55B683774779EBB79FA17C55A44A6092A5F918E6AD4E1425AB8656A93A0357EEFBFFFE4760F0E662D83169352E662C8B0F0F1046AEC3F4F21E8E3B3A76F
	AD1A14BB81658A22FA08D1F7019F39E51F6A9D049BDA3D90F83A761247D70FC99106ED307570077087ADA0CDD90CAAF81318D1755D0F54CC1E7F425F480CA22C3A340F5A8ED87777A8815F7EFA37DB75AB2283B0A4F43FD7F40FB455B1B42F046FFC75548FFE67G47EB1472FDB5B5645FDA726F817C3716E0EA892676GD8B3CF9A71CFECFBB80CF37E1F30880B82A3083040AFF1DCA3040EB967B02CAE3D59C65E7B1160AF53E1FFDE26874A587FEB23CB2FC53EEFA843694B6448FED808019B9B2F2998AFDD87
	8D2BB5D6DEB3567BB70CF146FDBF6DD9C2F36A501258FA3F359723A59B700FAF616746B4750FD27CDAD01BCF76FAF5595BD76FF7E1DD6878202EC4EA911F76E27FA1CE439AE9DB9779B627574073FFD0CB878807751FB06D99GG14C9GGD0CB818294G94G88G88G460171B407751FB06D99GG14C9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA79AGGGG
**end of data**/
}
}
