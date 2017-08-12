/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import cbit.vcell.export.server.ASCIISpecs;
import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportSpecs;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
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
	private DataType fieldExportDataType;
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
	return new ASCIISpecs(ExportFormat.CSV, getExportDataType(), getSwitchRowsColumns(),
			(simulationSelector==null?null:simulationSelector.getSelectedSimDataInfo()),(simulationSelector==null?null:simulationSelector.getselectedParamScanIndexes()),
			(isCSVExport && getTimeSimVarChkBox().isSelected()?ASCIISpecs.csvRoiLayout.time_sim_var:ASCIISpecs.csvRoiLayout.var_time_val));
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
private ExportConstants.DataType getExportDataType() {
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
private int getSimDataType() {
	return fieldSimDataType;
}
/**
 * Gets the switchRowsColumns property (boolean) value.
 * @return The switchRowsColumns property value.
 * @see #setSwitchRowsColumns
 */
private boolean getSwitchRowsColumns() {
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
		setPreferredSize(new Dimension(215, 345));
		setLayout(new java.awt.GridBagLayout());
		setSize(188, 206);

		java.awt.GridBagConstraints constraintsJLabelDataType = new java.awt.GridBagConstraints();
		constraintsJLabelDataType.gridx = 0; constraintsJLabelDataType.gridy = 0;
		constraintsJLabelDataType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelDataType.weightx = 1.0;
		constraintsJLabelDataType.insets = new Insets(10, 5, 5, 0);
		add(getJLabelDataType(), constraintsJLabelDataType);

		java.awt.GridBagConstraints constraintsJRadioButtonVariables = new java.awt.GridBagConstraints();
		constraintsJRadioButtonVariables.gridx = 0; constraintsJRadioButtonVariables.gridy = 1;
		constraintsJRadioButtonVariables.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonVariables.weightx = 1.0;
		constraintsJRadioButtonVariables.insets = new Insets(0, 5, 5, 0);
		add(getJRadioButtonVariables(), constraintsJRadioButtonVariables);

		java.awt.GridBagConstraints constraintsJRadioButtonParticles = new java.awt.GridBagConstraints();
		constraintsJRadioButtonParticles.gridx = 0; constraintsJRadioButtonParticles.gridy = 2;
		constraintsJRadioButtonParticles.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonParticles.weightx = 1.0;
		constraintsJRadioButtonParticles.insets = new Insets(0, 5, 5, 0);
		add(getJRadioButtonParticles(), constraintsJRadioButtonParticles);

		java.awt.GridBagConstraints constraintsJLabelAdditional = new java.awt.GridBagConstraints();
		constraintsJLabelAdditional.gridx = 0; constraintsJLabelAdditional.gridy = 3;
		constraintsJLabelAdditional.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelAdditional.weightx = 1.0;
		constraintsJLabelAdditional.insets = new Insets(0, 5, 5, 0);
		add(getJLabelAdditional(), constraintsJLabelAdditional);

		java.awt.GridBagConstraints constraintsJCheckBoxSwitch = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSwitch.gridx = 0; constraintsJCheckBoxSwitch.gridy = 4;
		constraintsJCheckBoxSwitch.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxSwitch.insets = new Insets(0, 5, 5, 0);
		add(getJCheckBoxSwitch(), constraintsJCheckBoxSwitch);
		GridBagConstraints gbc_chckbxExportMultipleSimulations = new GridBagConstraints();
		gbc_chckbxExportMultipleSimulations.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxExportMultipleSimulations.insets = new Insets(0, 5, 5, 0);
		gbc_chckbxExportMultipleSimulations.gridx = 0;
		gbc_chckbxExportMultipleSimulations.gridy = 5;
		add(getChckbxExportMultipleSimulations(), gbc_chckbxExportMultipleSimulations);
		GridBagConstraints gbc_simSelectorButton = new GridBagConstraints();
		gbc_simSelectorButton.insets = new Insets(0, 0, 5, 0);
		gbc_simSelectorButton.gridx = 0;
		gbc_simSelectorButton.gridy = 6;
		add(getSimSelectorButton(), gbc_simSelectorButton);
		GridBagConstraints gbc_timeSimVarChkBox = new GridBagConstraints();
		gbc_timeSimVarChkBox.insets = new Insets(0, 0, 5, 0);
		gbc_timeSimVarChkBox.gridx = 0;
		gbc_timeSimVarChkBox.gridy = 7;
		add(getTimeSimVarChkBox(), gbc_timeSimVarChkBox);
		GridBagConstraints gbc_chckbxExportMultiParamScan = new GridBagConstraints();
		gbc_chckbxExportMultiParamScan.anchor = GridBagConstraints.WEST;
		gbc_chckbxExportMultiParamScan.insets = new Insets(0, 5, 5, 0);
		gbc_chckbxExportMultiParamScan.gridx = 0;
		gbc_chckbxExportMultiParamScan.gridy = 8;
		add(getChckbxExportMultiParamScan(), gbc_chckbxExportMultiParamScan);
		GridBagConstraints gbc_paramScanSelectorButton = new GridBagConstraints();
		gbc_paramScanSelectorButton.insets = new Insets(0, 0, 5, 0);
		gbc_paramScanSelectorButton.gridx = 0;
		gbc_paramScanSelectorButton.gridy = 9;
		add(getParamScanSelectorButton(), gbc_paramScanSelectorButton);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 10;
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
///**
// * 
// * @param newListener cbit.vcell.export.ASCIISettingsPanelListener
// */
//public void removeASCIISettingsPanelListener(cbit.vcell.export.gui.ASCIISettingsPanelListener newListener) {
//	fieldASCIISettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.ASCIISettingsPanelListenerEventMulticaster.remove(fieldASCIISettingsPanelListenerEventMulticaster, newListener);
//	return;
//}
/**
 * Sets the exportDataType property (int) value.
 * @param odeVariableData The new value for the property.
 * @see #getExportDataType
 */
private void setExportDataType(DataType odeVariableData) {
	DataType oldValue = fieldExportDataType;
	fieldExportDataType = odeVariableData;
	firePropertyChange("exportDataType", oldValue, odeVariableData);
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
private void setSwitchRowsColumns(boolean switchRowsColumns) {
	boolean oldValue = fieldSwitchRowsColumns;
	fieldSwitchRowsColumns = switchRowsColumns;
	firePropertyChange("switchRowsColumns", new Boolean(oldValue), new Boolean(switchRowsColumns));
}

private boolean isCSVExport = false;
public void setIsCSVExport(boolean isCSVExport){
	this.isCSVExport = isCSVExport;
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
private void updateChoices(int dataType) {
	switch (dataType) {
		case ODE_SIMULATION:
			getJRadioButtonParticles().setEnabled(false);
			getJRadioButtonVariables().setSelected(true);
			setExportDataType(ExportConstants.DataType.ODE_VARIABLE_DATA);
			break;
		case PDE_SIMULATION_NO_PARTICLES:
			getJRadioButtonParticles().setEnabled(false);
			getJRadioButtonVariables().setSelected(true);
			setExportDataType(ExportConstants.DataType.PDE_VARIABLE_DATA);
			break;
		case PDE_SIMULATION_WITH_PARTICLES:
			getJRadioButtonParticles().setEnabled(true);
			getJRadioButtonParticles().setSelected(true);
			setExportDataType(ExportConstants.DataType.PDE_PARTICLE_DATA);
			break;
	}
	return;
}
/**
 * Comment
 */
private void updateExportDataType() {
	if (getJRadioButtonVariables().isSelected()) setExportDataType(ExportConstants.DataType.PDE_VARIABLE_DATA);
	if (getJRadioButtonParticles().isSelected()) setExportDataType(ExportConstants.DataType.PDE_PARTICLE_DATA);
	return;
}


	private ExportSpecs.SimulationSelector simulationSelector;
	private JButton simSelectorButton;
	private JCheckBox chckbxExportMultipleSimulations;
	private JCheckBox chckbxExportMultiParamScan;
	private JButton paramScanSelectorButton;
	private JCheckBox timeSimVarChkBox;
	public void setSimulationSelector(ExportSpecs.SimulationSelector simulationSelector){
		this.simulationSelector = simulationSelector;
		getChckbxExportMultipleSimulations().setEnabled(simulationSelector != null && simulationSelector.getNumAvailableSimulations()>1);
		getChckbxExportMultipleSimulations().setSelected(false);
		getSimSelectorButton().setEnabled(false);
		
		getChckbxExportMultiParamScan().setEnabled(simulationSelector != null && simulationSelector.getNumAvailableParamScans()>1);
		getChckbxExportMultiParamScan().setSelected(false);
		getParamScanSelectorButton().setEnabled(false);

	}
	
	private JButton getSimSelectorButton() {
		if (simSelectorButton == null) {
			simSelectorButton = new JButton("Select Simulations...");
			simSelectorButton.setEnabled(false);
			simSelectorButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(simulationSelector != null){
						simulationSelector.selectSimulations();
					}
				}
			});
			simSelectorButton.setEnabled(false);
		}
		return simSelectorButton;
	}
	private JCheckBox getChckbxExportMultipleSimulations() {
		if (chckbxExportMultipleSimulations == null) {
			chckbxExportMultipleSimulations = new JCheckBox("Export multiple simulations together");
			chckbxExportMultipleSimulations.setEnabled(false);
			chckbxExportMultipleSimulations.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getSimSelectorButton().setEnabled(getChckbxExportMultipleSimulations().isSelected());
					getTimeSimVarChkBox().setEnabled(getChckbxExportMultipleSimulations().isSelected() && isCSVExport);
				}
			});
		}
		return chckbxExportMultipleSimulations;
	}
	private JCheckBox getChckbxExportMultiParamScan() {
		if (chckbxExportMultiParamScan == null) {
			chckbxExportMultiParamScan = new JCheckBox("Export parameter scans together");
			chckbxExportMultiParamScan.setEnabled(false);
			chckbxExportMultiParamScan.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getParamScanSelectorButton().setEnabled(getChckbxExportMultiParamScan().isSelected());
				}
			});
		}
		return chckbxExportMultiParamScan;
	}
	private JButton getParamScanSelectorButton() {
		if (paramScanSelectorButton == null) {
			paramScanSelectorButton = new JButton("Select Param Scans...");
			paramScanSelectorButton.setEnabled(false);
			paramScanSelectorButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					simulationSelector.selectParamScanInfo();
				}
			});
		}
		return paramScanSelectorButton;
	}
	private JCheckBox getTimeSimVarChkBox() {
		if (timeSimVarChkBox == null) {
			timeSimVarChkBox = new JCheckBox("CSV Time-Sim-Var Layout");
			timeSimVarChkBox.setEnabled(false);
		}
		return timeSimVarChkBox;
	}
}
