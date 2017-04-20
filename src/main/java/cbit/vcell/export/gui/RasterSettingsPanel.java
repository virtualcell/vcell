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

import cbit.vcell.export.server.*;
/**
 * This type was created in VisualAge.
 */
public class RasterSettingsPanel extends javax.swing.JPanel implements ExportConstants, java.awt.event.ActionListener {
	private javax.swing.JButton ivjJButtonOK = null;
	private javax.swing.ButtonGroup ivjButtonGroup1 = null;
	private javax.swing.JLabel ivjJLabelDataType = null;
	protected transient cbit.vcell.export.gui.ASCIISettingsPanelListener fieldASCIISettingsPanelListenerEventMulticaster = null;
	private javax.swing.JLabel ivjJLabelAdditional = null;
	private javax.swing.JRadioButton ivjJRadioButtonByTime = null;
	private javax.swing.JRadioButton ivjJRadioButtonByVariable = null;
	private javax.swing.JRadioButton ivjJRadioButtonSingle = null;
	private javax.swing.JCheckBox ivjJCheckBoxSeparateHeader = null;
	protected transient cbit.vcell.export.gui.RasterSettingsPanelListener fieldRasterSettingsPanelListenerEventMulticaster = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public RasterSettingsPanel() {
	super();
	initialize();
}
/**
 * MovieSettingsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public RasterSettingsPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * MovieSettingsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public RasterSettingsPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * MovieSettingsPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public RasterSettingsPanel(boolean isDoubleBuffered) {
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
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener cbit.vcell.export.RasterSettingsPanelListener
 */
public void addRasterSettingsPanelListener(cbit.vcell.export.gui.RasterSettingsPanelListener newListener) {
	fieldRasterSettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.RasterSettingsPanelListenerEventMulticaster.add(fieldRasterSettingsPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> RasterSettingsPanel.fireJButtonOKAction_actionPerformed(Ljava.util.EventObject;)V)
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
 * connEtoC2:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> RasterSettingsPanel.fireJButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
		getButtonGroup1().add(getJRadioButtonSingle());
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
		getButtonGroup1().add(getJRadioButtonByTime());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (RasterSettingsPanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup1().add(getJRadioButtonByVariable());
		// user code begin {2}
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
	if (fieldRasterSettingsPanelListenerEventMulticaster == null) {
		return;
	};
	fieldRasterSettingsPanelListenerEventMulticaster.JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldRasterSettingsPanelListenerEventMulticaster == null) {
		return;
	};
	fieldRasterSettingsPanelListenerEventMulticaster.JButtonOKAction_actionPerformed(newEvent);
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
private javax.swing.JCheckBox getJCheckBoxSeparateHeader() {
	if (ivjJCheckBoxSeparateHeader == null) {
		try {
			ivjJCheckBoxSeparateHeader = new javax.swing.JCheckBox();
			ivjJCheckBoxSeparateHeader.setName("JCheckBoxSeparateHeader");
			ivjJCheckBoxSeparateHeader.setText("separate header file(s)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSeparateHeader;
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
			ivjJLabelDataType.setText("Select export format:");
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
			constraintsJButtonOK.gridx = 1; constraintsJButtonOK.gridy = 1;
			constraintsJButtonOK.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJButtonOK(), constraintsJButtonOK);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 2; constraintsCancelJButton.gridy = 1;
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
private javax.swing.JRadioButton getJRadioButtonByTime() {
	if (ivjJRadioButtonByTime == null) {
		try {
			ivjJRadioButtonByTime = new javax.swing.JRadioButton();
			ivjJRadioButtonByTime.setName("JRadioButtonByTime");
			ivjJRadioButtonByTime.setText("Fileset by Time");
			ivjJRadioButtonByTime.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonByTime;
}
/**
 * Return the JRadioButtonByVariable property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonByVariable() {
	if (ivjJRadioButtonByVariable == null) {
		try {
			ivjJRadioButtonByVariable = new javax.swing.JRadioButton();
			ivjJRadioButtonByVariable.setName("JRadioButtonByVariable");
			ivjJRadioButtonByVariable.setText("Fileset by Variable");
			ivjJRadioButtonByVariable.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonByVariable;
}
/**
 * Return the JRadioButtonUncompressed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonSingle() {
	if (ivjJRadioButtonSingle == null) {
		try {
			ivjJRadioButtonSingle = new javax.swing.JRadioButton();
			ivjJRadioButtonSingle.setName("JRadioButtonSingle");
			ivjJRadioButtonSingle.setSelected(true);
			ivjJRadioButtonSingle.setText("Single NRRD file");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonSingle;
}
/**
 * Gets the asciiSpecs property (cbit.vcell.export.server.ASCIISpecs) value.
 * @return The asciiSpecs property value.
 */
public RasterSpecs getRasterSpecs() {
	// nrrd raster format
	int format = -1;
	if (getJRadioButtonSingle().isSelected()) {format = NRRD_SINGLE;}
	if (getJRadioButtonByTime().isSelected()) {format = NRRD_BY_TIME;}
	if (getJRadioButtonByVariable().isSelected()) {format = NRRD_BY_VARIABLE;}
	return new RasterSpecs(format, getJCheckBoxSeparateHeader().isSelected());
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
	getCancelJButton().addActionListener(this);
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

		java.awt.GridBagConstraints constraintsJRadioButtonSingle = new java.awt.GridBagConstraints();
		constraintsJRadioButtonSingle.gridx = 0; constraintsJRadioButtonSingle.gridy = 1;
		constraintsJRadioButtonSingle.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonSingle.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getJRadioButtonSingle(), constraintsJRadioButtonSingle);

		java.awt.GridBagConstraints constraintsJRadioButtonByTime = new java.awt.GridBagConstraints();
		constraintsJRadioButtonByTime.gridx = 0; constraintsJRadioButtonByTime.gridy = 2;
		constraintsJRadioButtonByTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonByTime.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getJRadioButtonByTime(), constraintsJRadioButtonByTime);

		java.awt.GridBagConstraints constraintsJRadioButtonByVariable = new java.awt.GridBagConstraints();
		constraintsJRadioButtonByVariable.gridx = 0; constraintsJRadioButtonByVariable.gridy = 3;
		constraintsJRadioButtonByVariable.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonByVariable.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getJRadioButtonByVariable(), constraintsJRadioButtonByVariable);

		java.awt.GridBagConstraints constraintsJLabelAdditional = new java.awt.GridBagConstraints();
		constraintsJLabelAdditional.gridx = 0; constraintsJLabelAdditional.gridy = 4;
		constraintsJLabelAdditional.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelAdditional.weightx = 1.0;
		constraintsJLabelAdditional.insets = new java.awt.Insets(10, 5, 0, 5);
		add(getJLabelAdditional(), constraintsJLabelAdditional);

		java.awt.GridBagConstraints constraintsJCheckBoxSeparateHeader = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSeparateHeader.gridx = 0; constraintsJCheckBoxSeparateHeader.gridy = 5;
		constraintsJCheckBoxSeparateHeader.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCheckBoxSeparateHeader.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getJCheckBoxSeparateHeader(), constraintsJCheckBoxSeparateHeader);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 6;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM3();
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
 * 
 * @param newListener cbit.vcell.export.RasterSettingsPanelListener
 */
public void removeRasterSettingsPanelListener(cbit.vcell.export.gui.RasterSettingsPanelListener newListener) {
	fieldRasterSettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.RasterSettingsPanelListenerEventMulticaster.remove(fieldRasterSettingsPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAEFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D45715EC56246E46EE70DB525A54F629B5A9EDE9BF5B58DDB7CD9B36B17B11063606B506243825095B587DCCE23736CBDD5BDE86B4CC44C0948DE9CC1CD813C0A44888AA8EA4020C884328A3A0C2A04A03F973433C19B74CBCF4C8C85CF37F5E3C19F9834AFEDD3E9C5F3B671EFB6EB9671EFB6E39775D892A5C173E3548D009D051D6447E0E95A1343183212F6C4F3F13631A5E4AB5A24D5F6B
	GAF22FB46CB605989F58EFF2E31829DC025D09E06325513EBFC895EF7211A7E7CD6F8C9524FF2285BF5E76E1BB377B35D457BF90E14B7874B60798240886070B373AD5C7F4E09AA061F47709C0A8C002B46B46FCC6CE5380DC0D38A38D6003EEACAAF41138AF0332509518DAF4CC01BFE98B26C40FCB4BCB96430369B595304B697AD2A41FD75CF1431B655D09E81404A972374378E42334561F33FB7B66AD56A2E0C0912D4A7C602F2C831D8FA0430A206FAC5C571865CE1079090A5D479B666774509D2529E21
	FC6015CEF05B3C380F81F88F824CFF1B637F076057417B96004A03DC2FF75FEAE1F28FFC74CFE875E1FFC9F9923DEA8FD2FD7A8EC6753286569ADEA6E3F02F8D5BBA956A1683E483ECGA82B4DB556823CC2467E7E2E376159B62A344BFE3F9CE88B86CFCA4E509959A9CA70EE31C08D437508E3F24809909D5F6109DA9DF9E6825AEE7C065BD11BE4B470314E6C79A64A3F7BB30667F4EC121F711141DACBEDB1DC9B35896D7B4BC97B1E2E0D7545583EAFA2ED5F11401A622DBAFD278D2DA9CEE5FC16EA7A4E2B
	45E37DA1996B15700E5B617694FFB3416FFDA70A27FED9C67038DC8FF5E96FF05F98F8014BE2AFD902AA4C1F553543229FACB0943EC3E528280B176556F14C3B250E767922AA4BE20277E870D416A2024765B428B39D4AB5D27FFA400E67E9A61433818AG8A812A6AF30D0DG1D755C474AEC072E53479CC2C8B9B969151CE28851368F587640D30CA8A121AB90D604401858AF043C4228A406912379318D5BE0B4265D3F915F9F03BECE0B81B1A4A8DEB9G5C853F8893B7CCE972EDD8273110C8EA7B3CFE91BA
	708791673B4E368B1E924C758BC1272008D18ADA3F410647640A508F9C01813CD33B7C5799E2C750BF85A0D535033B9B736B96C390AEAC963BAC7BA603FDD893D4D00F6DFC8F099DCB617D5EFAEE63E74EE25C81F32E31454C79FC31FB27060F55BB064597C2D3FC0E25F8F19B8750874D5CEF1E9960FEE35D718DD47B486D3AFED3BA3C26F83E197ACB3AB9FE7E5CCA78E259324DBC4E6F748E40B3E8561FBF9C9C33544F9CB38E12BE364EC2DBBC877EAF28E3B66C40E3369E64ACB91CEBBCF0185B7A31EE9375
	5D13C2D8645EFAAE840EA6BA231627F2AE9CB7B119314D37836CA0BC905A0D68BAD92E6A34045036C0BFCE0085G4B817281CA9AA0BE8374B5F09BBFB71E22E397ED9F658C4F7B52537375B95AA7F6AC5ECE1025180D8D1769C50667326971AA1FC37FDA20E37393BB66A3DE91830AD58C0A81A73C04D95D9ABB1605458D172C3649A778A04419F61858E1957F049D4BADA8CA48BBBA2908FCE9D76B7F1374951E9C8DC6C3946F6BD7F62C3FFC95620CCA7FDBBB1E23E3F2A0A092A7D11B307AC292A3C4C9740350
	6D72E4C02178BF1C41F914CF1CE2B14AF0F69B42B1C61A44710E66A64B84EC5BE0486B875743A19365B4607871301ADB11B58DF0B847CA076772869E872E7E194701839FFE95E5DD8E9A745632C5F75EE628EC24F3363AB19A8728CC3FBE2BAF7B6FC82CE278B015F50D935B610CFC45AB4626CB44EFF6214A8866F5F2D2D164406910BC99B45379F87BF90C6F043E078114C6BE9FA74F61FA9E7B58C4EC17A5091A996AD6B4E2DB7C44DB2951E7C1A335C554F7C69D95F8BC4509C99116949C043BD51BFB9DBB63
	6A7A197DE97D04835BDF52446F11A3384F4747F95E0A0E507E2A0E50D8D9FF0476DDF294532579B8DDA660F18CD8894F55G1BG6AGCF790DCA1C4EF1F4A6FEFB7C1C4EF314461294F8269E0D67D72052ADE37C320F6A713BA340698A0E5218D4884F52E339466AE314DFC5932673481C2E89708E0060B14AF799BC37GD03A3B031CEE984A8AC0DA9325DB8D4F8347F94E7D6B9A6C3FA4679E972E88113A70D570970B45C6BC0666574D21D296EB1A0E73BC7A47A4BF89C2F9BE469D673E7E708E1EC72779B2D0
	1F7CC8F1327D410A63D47F7C63D1DFAF4939DACCFD71E7BD384F3D96084B966E079B9C5A353CAD9C16473CA40DE0BE7279735A39C0F169671F07274B9B8AABDD902CA25C1F6EBA0F6545F986C333F97E7B3AD7F56D2119AD0867289FF7E8F53D377CE34376A43A36DA280EA316282E766E7FAE2639DE312F8C5139198E75F9GF41E0E933CB760D53C02647DD02C2E21B2568776633869F4D65BB5B256350D06A1720EA9D454661AFEAE67B7DEEBE5F264FCF389AA95EEB6686DED967C52ED28E82672B5B5C765CC
	FFE42DE1B9G1575FB2A2CA9C03322194BFAC042B1D488394DB4963C695646020D4DB43F5A5B42696D04DE71F843D44669C1AEE3CA4EF2D4356B533AB2E228EF21328D34C4E5141EDCEBC8F9124BF82B44E54CG1A82G0ABF6167782AF7E16E3C4BE5394B9FAB7B532775E41F7FAE67E38CF0BE0B8137DA65938C440F97253FDF25AFFBAF57E8FD0F533FAD47767BA70BDE3F53AAFD0F4CEDC679AE14B95FD420498620FC3E6E5272C1A7A006G58CFF0BE0572E15CAF895EF5746190C3AEB964971D5558236A04
	2BCA9DD9636B5AC875A97CDE436748416EF7691C92ED54574EF5D3324B714C08FF56CDAADE290E30B8B7BA8EEB86F1CD36F7BF9664FB771193BC06AC5925755716EB17F4FDE1FE394730688475012C93D1DFE882DF352B7E1A9F64FB3A32D630FDAB375166CF74ECB45D4AED54BE71FF335133B5ED31B6EA475BA8E94EF63AC9BD63C8ED65B9EAEB086F2D33DBD9ACED0D667C9BDB9373FF3A67ACA57323240D6EB94BD5FC954157EA7054062BC92C4765BE28DB554677C917AE603543FEB2576881C881D8843082
	A06FA40F59F5BD8DE816FDE8D7G360781C16A0DC97478FE30876B8C3999AB2F6F4176A53999627B081CFEBC161B204F0AF698C700D60011F6AE035CBB2B8C27A23AB2D0BB9BFD331D1F2434475A1A36FBE956F6CB635AD1FD768F60357BDE284B835888D0E605FC81408E9034F23D3E575BBC2BDE581B7AE46AF66D7119B113737EB96A77702C6DD22D316D685C58F372B5241BF37488CE2FCC278B777B8F5CBC774830F23FDBB2086DD3G65ADG152740B6GFDGC10034D35CBE65FDA7E7330FFABC81B6627B
	10C4B3B1FD97CAF33353BF4C5AAE7314DE3BD37E397557BBEB3B15F17DD1DB9F6864364EBF45E3CCF9E0EEB23CBAEB3B4A8E3DF68F4E313FC7E7EDD75B212773F98B57595A41F57E2CBCB7996E1C355DF4075EFCA86F45FE2DB39F78D4E0FB4DDF3862771AB8BFC8831E99GD41FA7DCD89F1C07E6297A348431DC059D096BC1B220736C25A1BC4F4ACEC3CC85E8859886C881D88C10F51A4F3351BE1C8FDCCF9CEA87DCA76C61F4269833619341F89B961C0E15DB5F769B935A554515371C569BFF7335D2B68E15
	1D5CDE1FA2E756764E6B3797BDCB3CC876C9A350EE9A00623ECF62D7AA14172A384F13B33DECDCD67BFC016CBBCB3AF20DA600D6009100F9G69GAB3BB85DEB04AE2F0B6EDDD3599E31904AD5B61A43D03A474703F1B2F25E3C1C5EF5FD7A0DB03AF9C968DB19FC513178DDC25F7C3C0A17131DD1450311BD37A729477D71F3ADF65EFBA17979D460027E7914F2C17FFC6A438BAFD2BC70501E12737A1BAE4CFCFE753797121FDF2D6ECFAE67CF7B75657CF93F3E1CCF754FAC67337D4964FC150C4DD38ABE3FCC
	B8B7BB8BABA362E7C62DBEA323B92548E74CAA6F5A0164FAB48C686BF1FCG0FFFD81E8C0D097132368ED429F551A8512508FEAC8E2569A26D9558B809893ABAC8FBDA172C7D60C0B2FBD0DDB77B36B3DD9D2E106CF71819DF0F3AF1DCE832454E83BBAB0F589267876DAFF3B0390D6E9E543751BF8FA637D156606CB6FAF0B0390DD68D4EEE234786318DC63DCE584150D3BEEA1BECC96B8707D51BCD7BE3EDF618596C36AE3DB5D1E1361A8F4F45F1F6237CBA825AFE9AD4BC12E37BE9E47D74DB747AD946F82F
	04E71EEEBFBF16B57D34E33F266B42EBEE3EAEEC3071B32FCDA25E879D684EB536G74F573D87B140B77AD81AE251B7613461E99703C37BB36EF3A8E74123D6FAA28AB6866EB696BC349FD25FAC85FD76A06127B4A112159FD653D21643EF2EAE8F6DFB9BBB4F3FCF9F0CFB909CF613098A60900D5D084E67F17A73476477EC4EDFA741A5EF8EEE4F6AC31A74EB5EAD3FA97208A6A2B6DFCAF9F88E17D02A151A506C22233577BA10EEBC1BB267F94D9331B605D2A52BF6246677F12B0A5CFAA88194EE0BA1B0767
	DE4B01AE87E093C069991A0351BC6EDBD731EF3AE0C7816A778645B13405347D36173775C0F99AE0B1EE8710FF0677F967B42E375905D1D14266691B8A0A5CC7BDFF42E7635A5DD1AFF4A029755B4C7175A72770F7D3DE3F7C541E04FA7EC117537406AA547E5B1CCEAF7948A6513A05DD784E0A2D5DA30E79CE4A11DE91169AC191BBC581FB8A257911830F9BBDB5B6336F8197A46D990B75AC351567ACBF033118756A27557AAAD1DB1F4A6AD71C65F954941923B9382C62FEC0F01B20DCF20E635E7270FC2BC2
	455D554569C6D45CF3B6CEA729387B0E136F2FD0DE25627CE44F308E4A1BD4DC2E0537C58E0E7BD9BD79A68CE52703470ABF3B637DBA62087A7282C722DF5333990F8B471EF4C77CDD06B041AFF7445FE558AC72F32FEC282BBD4FE57A83599F24C2B9D345BDCAEC168565D5AA6E1E8622A314B72938FD0D2A0EBD9C57D0CDF4043227C7BD378E72F34D0C9ECC13C24A53EA1BB3A4675EG65ADAA6E5B57B86F52DE4E67BB9331FC9EA26581281F570B6FC359C7119A476FF61B6099902F1A5443B9ABCC01G3EA4
	D0EDBE5AD8EBB29F2B2F21670B69D735F7181A1E3FC977FB9C066CDEF62E461EDBF65F416EB005BC7C8ED3E5DF2E515E47E300F98467676ADE8E8BE2311C8EF91DA785371DC4943A3E5CC3FC92371F574757177504673DD0DE8D30210F1F07C65E645F99B26FDA02BAFD7A5FD48A7ABEB614DCE05F8FAFC44FC38782EB8D344FBCAF6F5389750BAF10FBD2635AB906ED7905A23AF72275CDB175B84E5F06347563B175D85E2918760BE36A310DF631FA2A5BC3EFF05D36BC348CC95F191774BE59BA26D3E9BFFD26
	2E7BDCD156AB9F95336FD82EA45F31C8C4237AAF1078DE3F351F6F17A74C9C976967E378DE7FAB7A3CF884E4B942B91167884B7A394F26123994967137D151C957182F3879596DFA156EB1B7B94B07451C7A32644456283D08695F73D8194E057D097120087162D086EDB06CE63218AE52D8C0FC0B703ECD0C72FE4D46F9AB9775F9EF1D0177FC8DEF2A471A6358F6E2G2FA5CE3626BEFD1C6B1BF6116BBB4F922B6FFD2E28CC3F34F01916A61169B90DCCAB00E605C6261C8419FEDB1FA8538EB317A96FA20FB9
	96B2AE6B213CC545FDC5DDABCC83BCE687C84C8CC2F93E0A1BA2B87C7D95FFDB25B8649A50715B19G733B9E3A205A6F4BB75C47ECF7F834F31F5AEF37BBF93ED4C7EC369464D9B640477699624BAEB920D01A92E243FC28DF2F52BC5C90BB7ECF382363FF3451FA9DF2F1289A4CB5E2584D463CF6B0FE7CED55B88F738B912FFF52CF2427F20D12352A89682D03DC2E4F8477A14D3A917F89269AFFED2BE1316ADDD13BEE54FFE4B794A37DD8959C24631AB298DDB7E8FF4EEB31F67834B7EA07872FF1BBAC9BCC
	748B6D3C5433434A84BB7CFC627AF57B4910D6370ABBA4836795AF47AAA6DB7EE0F40D216705574631FDAB5E07FC63FD3E1FD9B80A6F74768B636CD470942416F2089C8B8252911203E2C81922D8F26303657F914F5C6E3A5EB247F6775DF0BB1AFF8D13BB237677233627F8115EBF5660E95E700C1AB7B821AE7BFD7ECDE3198F2FDF35C33946CE0091008840A20065C35C160F0F5623D94ED97B64209DB60F126698086A3756BB37BB407F729CDF1F175F73B5346296EB52BB8E59C3549F5633E775131FAB42CF
	2ADF3B9F6BD7B51CEBEC8498G88824C83D8BC4C755BBFD6CB75436E5026A84218075C64604A75FB452BD1B45EE045FF17D9B69C1DB7AB59FB5EF062DC226D2E18F93B8D9A9A7481FD2F7CA0D93BBED14F16550C3EC5279D1D0F5F27EBFEE8D22479036C617903835A6CC374AEE1A90A7E5198712BGD2476367E5FCBC76FE7D4B487A28557067A463B11F491177C1F41E2E8B1B0AD96EE263FDD7DC62797490BE36E53216D8E2E57DD3B3276F3CB4132CAF0722BC8E18B98FDABE2F332F7638795EB865925DD7AF
	02E766A5AAB30D0B5FD7732115177473039DA89A977300E64325E8FE00AE474745E737E1DB2AF797D47C8747B09F7A21BE7A6D056A55920E6A1563465FAF136CB50E18EBCD070F5470F60FCCC55BFD33E1FFFC3B8ED8B42B77BB3D02A43B77439E857EBB8CED135B376923687B50F414775ACF76A51749DCDFDFEBBAD2DF4366E26947F8DC4AAE73F97706874B15ACC663EFB63DCA88B2E0FEB636D97DE6E23A4CFDA77089160572FF659AFE273C9F763E110CB74CE89B48CA9723BE412D3619B70E47D5BDCF224B
	822B3B156CA754BBB6647A97150E1D511275143E7BB46F1BBCB71AB75D789A32437B176C031D8B7B121F8FE679744F877F55277F5DA047B773770B553E645FAFFE6A535E3F2563DE0D70588C00CFA8G0BGB28132D4BF73CA584F70AF8F92E37BA9279B1FCF62DF9BA83CFF3BBF392E3D7E59FF23B06C47F77FC6E51848FE4CDF3D076A47632548C102E45F0D7C58C6A12F5B23D0D206777B312FCB220B20699EA1036CA373C12F424BFC5F301D64BEE5A339463DA39C979C67FB09EA9577E9328F09C0B9CD4535
	17E2DC8614D728380CD25EF6250A73117DE62510EBBCA0F05CAA1153558BFC0F204464B2A900CF93E65EE750713B258847598C20DD89309EG0D42BE9920962095208F408310823088A093A08BE0B540BA00C20032B1BE5EC7651A99628ABB94A1039E923CF858797AEA534AEF82FE0DE3B34BCF5B6DEB56366B04B643333423B9D92A1B67E4C1204F9863E783F9EEFEDED2B0267E2E0C64EFD5CE30B7C0271353BE2B2E65A3CE3D75F1630964B1FD9407F066EFE59F63B5B24C62AD556B0FB1E7610B00FF26F3E6
	3DA82FFA14FC7E9CC3334F1FD6A22B88FE0FCF235977G046513DD2E30284ED39B2AD0F1DDE1079CA62747F4DD3ACFCA3E174CA373A88BF449F1727D587E71587D514D38487200CF0D4F34CF542E71988A01E71928B9979163730025666432FD0F4C53DAE853A4F2595EAF0D15ADE3EBD4367BCA39ECCED1BFC7B1EAE40BG4D820DEC6989325DA44676755036E8DFCF093C2F4CA4FDED57741585B4D91A3ED6EB7A22B164D6623B858CCFF12984D748F0F41C6FA8CA6ECFDFAA1A5D1F3ED6249F0F1795A546636F
	946947636F96E963B1DB77480FE75D90CF86D43A87D45C200A7BA16109F1973DCE452362D79139B03ED3449DF27C9AA257D5029B606B1E86B748F085C5F8F0C0E50F9C62ED7F23081C09F8A50916FFC9E83C8118A22ABD1EA1B2FA0302C66EDF2B38C1F5ACFC364471895A3463C3F1C71B93691A1B35F4B41EFCA6619B447565B7CB6758EE6D8D37E33F5FA4313652953F377EEB02B739627756E56A5E3A966AD2DDFCBD3CA7661E62A640173AE70E1F54F6FBD022BDDFC1DAFB5239798C6E06FF1F9E1FB18FEF02
	4D41319A1D7CFE267561C5E3743D7179E8DFCD25575397DD1FB6167275E92F7BC657275F5871BB65D36562FC7AF47970B6D5E79162E7D6421B1AFB71E46AD17C5E480EA81ECFBD7EFBCABCBD597B5FE85EF77EBF6471877F22FDD0BFFA581768DBEBFC09F375B725DA5FDDGB6CF1F45F7597DCB17365DF2E813FDDDBEDFE6CC146BC5E3E22CE9F4A5529DF7E969687F67603867779088B31FC6DB43A134B351CF5B7E6E31C7B3AFDBAB8D4850854995F102AE3475AA761288BE0F789A40D285D550CF5E2633BDBD
	D69304D548BE4ABB60817154B4BAE5421F3B9534CB03612B0F028C673A95340DEE6BC108F2D508AE920D8B2EB4D80A1F7CF23181150084F4F56AC2A5F80981C1C2D420E2D81D08B8786FF96659A8BC01BEA1763EDEAC7E4339F6834831A366906001AF9D79C54E3F77070B5136AE74B2BECB6F95C374E7A4CEFC9F007D5E39839903B2644AB8D781A1436431B52CCCC9229DA2E58F7E7DDCD8911D6AE5663002F613DF979C42473F07BA0530C741E6E9362312G3E7A0CF652EAC988388F511DF4B35134282D1548
	CBE2AA483BABFA082CF259755D72B7DE372DDBD10C8C9D68F9C9961C9D42986478DD4828F8C2E258A3CB4EC87084E5D4FA15DFCDAD81F62F286AE3E56B7AA0F5D00576C53E30E0477A9D9D864814C26890214429D01DCA61EC77887837CEE228CB413FB417C387543A3FFACDF973C727DF9AB620A226A4B67D8B11A0F801CB70FB25A9F821C7AEE4D0F6C211AE682AF7E0E354F500BE4A63CFBF386427AD9BB7G338E34157C6EGC42A08D21D1D740F0A21DBB2EFFFF5FBDA412DD862E0AB9FEE60EC0404A89046
	BF73G6626B81DF915AAFA5D5FBFD8FF7BD7B73FCFC40FB6D550BEBAC8507410DB147D075ADCAEEFC0D4994008817BD7D5761826AE0EE6E7E461EE5BA3FF044D46B6ABAA76575713FF4D645F4378DFB341188946FC9806EDA31A71AF3EBE42F5A6EEC98AAFE39F0A6037BD91F5DA14E8C11D96786A0FF3861A84ACB8F6AF794CEA227F079093D6D2D0AC8A5A933D40421078DC83AADE8A332BABA68F393BC282C9F538C6958267E0D24C7C5D4A60796B26C91879081F5D56DC53FEE3A93CF9AC693D03B4BF3BBB66
	0F7F4672AD32C62C83BC8A70FDF6116F9D147C0C2D57AFC812A2C83E3600931F371D1CD24468994FBF7AB07FB027E1156C77A5645CE8DC5366FF81D0CB878842716B189199GG98C7GGD0CB818294G94G88G88GAEFBB0B642716B189199GG98C7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCB99GGGG
**end of data**/
}
}
