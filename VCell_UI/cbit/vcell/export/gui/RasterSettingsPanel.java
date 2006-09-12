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
	D0CB838494G88G88G3C0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D44535D1A3268272DF02848998B1EAC0DE9B5BFC6F59D7DE1F760B95DB34D8531676CB3FE2DF6CCBDBEAE99F4F07F532899088C894C42CD1D6251AC89A92881A9721A46407848204E4095146123B3BF7331B5C6C2E3BB7E473833EF3E66E4C5E5D3D1B855E57FC9E6E1DB3E7E64EB9F3664C19B3F7A53B77A5EF4AB36DA6A4EF93517EBE4CA3E4DDB3A18D16378EF15CF4F7D791517D3D857092
	F9D3AE00E7B2542526F7959513BF0E96C2B99D4AC977F7953D8A6F3B485476B5F57092E51CF428FBD86DFAE162F1329B78B8DBE87900EBBBBCDF8448G9CBE8BBCB8FE03EB2F06DF2361B96419GC7E97E6EAA51F0ADD06E85C884C873B07A076039150EFCF3ED0DC657B3A3152C7F0E4F34837B517549E13F1735ED511EE672DC5EBAAF0EF58B65935B44C1B983G054FA7496F9D00675176013E3F5A2CAE356212CDD6148AB9607578545A5A8E492F4A3ECED9D5DD6EBEFF3B64169522FC0E7DFD4FD5C85B93D2
	8D658D9457E743B11643FBA6C03640775891FF5457D5E481D0FCDC2EE36F9D55786E9E7BBAD95B7770561DD1640A77B1F952FCC139B25E37663FC667405C043A4E013A6D7E2EA2B3C0A3009DA09E60F5BA776FB53E8F4FFA2B5A60999C74386B3D5E930A5DF746E31795F82F2D059A8D57A15BBCBEBBA1ECFE273A4A8D781988D83B79575C0E69A475B01F6B340EE5E44DD73FEC5AE22013B529E326A4BF53C53ABF2893B6766A28E32F76075AE268589F937D586419C3DBB6990CDD7203C3DBF234FEB66A462ED4
	F12EFFCCED20845E319D36E778DFD03C2A43B33B9C24F8ACA7C0DD2E4AED237BC54ECBDB41E2D2DCB557D08F095F1AE2EA9FE2BCB80742F94925F6B7FB080D79126065F90ADF2143B3DE2C940F65DC288BDC6AAAE276D5F69257E941B05896C08DC0BB0097E08AC072B037B1EF63412B3431F649271E98F2A9F659C7D85B3226BD7014832ACFEAF57BD549ED133BA41FCB32AA321FE8B4071AD0875610F69FD25B374198A7E5376C13D417478D3DCB03B2ACDCBF2329EBC219ECBE1956DADC03B28CB068A53C5F16
	26DD70D4E02DDF705AA5D58ED2307A76A61C13CBD29774889D40BB53CBD78B55A3481F8510A37470A86D6F346C83F7D1DB5B66718C8CF9ADA8892989201E7F137A0E0DC03FBB40F55C510A38F1A84F8E70FECC4DAF6B7AE9F45910FD49B74257584FEC58A69568B3835CEEFE5D4D6D26F147D2D27E649CC33B198EB61C9DE07632A1903EFEF2E93F481BF9047BF93F2D9B1E1981637543A1B5C67D44BEE38A9DE3D38C5A7C6B687FC5B1E7ABCE611C5501DCBDG63A3DC57471A4D4CF6CFC8FE19DB6BB99F981AEC
	8FEA1E71792C5D2C710CBA47CDF2876D0310D7885BA7F78A1972E5241D8D63BC883096E07BE8D7D1B5C08FC0BCC05AA85731539EE720977D98BBB5BC9F4BC84E372ED3BF21F3B1B90217FCEDEEB8CFAFE9F84E1BD1DFBB2FF16CF0E0F3F378A476A3DF125DEA236C155DF6F871EBF535A711974DEFB8BC22CD9D45FB414FB44042768BFC73C964DBD2D51F4BBA244AFCEB9775E769D87EA12BB7682278D897CE227C1EE170B3027E531338C6ED9E37DB26C6A21AE875BD54C7480ABC88CCB7F8065CAA43DBDBB00E
	9A10C7B49F65E859CC5047A8C3686FD8EC32DCC25DFAFD2EC1B0BDF419E47F987A8FD37FDE1D0ED6037DE00C35811E5B47389F98FE077B017D23771095973DA6233DAC712EE9A6E50C2D593831209FE0BCFD5CEA4C7BE754D7E9F8BF637587F65443994FA5179C9AAED13B59855E847BBAB1242A9E77C91FE748DB455663FF1FC6FCB20C1D8E30F20C2F479F36E2BD77FD5AC2EC70A88AD3B354950F23AEAA6D3BF5722C9DE33A885A4E37CF9563FC4A1F8F4930252093BEADF47E582917436A3AB47D337AA7CF21
	7E951D7F4E20E3FEA4733835FB1C0D679DE73EB2E11C0D5DF39969FEAF688A200CBE20841E65GEDG9500E77D7D3B035325DE1E283F61BECE3762B273A5D9704C09682F5E49698A2F307E4CD70C7A7B9FC1D7F3057924F6F8768244DDE17DB93FC03AC7C41CBE9D7029G19DA3F05D0EF81E0F43B87B8DDBA14D78264FE41684A6340574671187BF7E5E83FB4666E17AEC9018A7FB058CBED6DA9EAB1AC3ED67A8BB5DFB3BD0E47517FCAE325CCA8E7A3AE0E5B7A63BBF89CBDFBA0152CF926EACB3473C1718DCC
	7E6A9B02363EF2DD7DE6E60B8DE7F0CC956A62EF60F658FEDA3F17577B7D9E1B0B06911A0D3CF0DA3F96982E20ED9BBC9DAE1FDFED85E7956076342D8D7945B8C3C3EB6B7C790AB78C7521DBAD044728171B7532BE38733229A80A2C091A0C99BAD9B3BC16ADAC56334B5B89DB1B9B203E72462EA236CE9F25F8175B253AA445B5AA1716B19EAB5DEF235F345BCB5BF4BCD654DB7D60F9EDAAE3752332AE4E6752B76BB4BED6AEDBCC8A251BCDC6E71BA94F74191CB7B27E265F986433E516B53F8700717A260373
	1A85B445B7F1DEEFF6208F15FCFDD54C974C3D287785ADB73178CA9574CBA93D6AF479990F631FF09E63D626133D3B269A720810F09363ED79CDC19EF7AF326617AE62BC668A9EC9BC4448G8C1F5C4771DE402546F3DE36B8C3F91FFF4C0877EC51CF3F1377339EF065B773FE9EF7064F97235F6F6274F620CD927413DD2163BED7EDB46EEAC17FD59757996B77D951EF8E5018A7810E7613A0697B6986FC9CC04AA45E4FA77D18401028732EE00FF65967707886E5FBA9DAD405B42CD650BD3E221ED6B761FB99
	DFA387CE9FE3EBCABE45EC6D5CE9C6F6B13CB3EA1F95C32ACB2920DD1C3376431EC1CDD3BB3BA7767333FB46A46EC3966F525B6B51AF7EEEE88B89BB1D26F513188D6C3DA5E88B59B32D796BE7F2DBB8B6404FF4F620C93A056B686991A39D2D3E056BA8E5607F27235F1455076A28810FD14AF56B696783DCCFB93770987573FEFE36B6A7E83EB4A1987337A4C4467F6C4C39011EADFB925819F3274097D2FF932F43B39DBEC4FDBD1653202E74CB7C1C742DF338F724C0F985C09640BA0062DB41075D4AFD764C
	3643A446B934558D47C33724F40684FA7CBCF886E50658CCAB371DC17D52580C70F3C465F91C4BB69853899087108810A1F8787E1918BCB4858CF9E0FA6E17E365CF32EE8D55B5EB970859EEE3D8BBA64F248E5C3BF7CF86BD8234G588192GD2GB2A7F3395EBA7BD1CC39501AAC9EE6F68D6111314667FE477549F7A8E63B1C49216D585A58F362CDE298F3F4C8F61707AD97EE775F3D48E38FB2055B5DE68B6A27864A96G85A081A08DA093A0F78A570F7F6C09D87A9169895091BF07C42AC91377596B5493
	690B552EE02AD13BA5B15B990F179E33DDC958F8CC57CF9C633A2E1E4AFD0C57F9FDBCDC0959CEB114797D1839CA6371EAE2360BB714396A8817B9C9483C61BAF9585C9F2B5D6A29C66BE1E7A75A3541FA60CBC1BBEBD6CB61E7CD0C8FF221CFF29B2C83AA4FB7A5148763503D37F1F996D03E5AEF3B7AFC90DBE7E397F01D5921DD9CC0A2C0BAC09640FA003D5378BA7B76D90C872E468FB5G2E854EF086CBCC53E143C0388EEB260572ED2C7B73916DAA424A16E9C673DFC26FE6F09E14E9DCDFB9342F14E9D7
	2FAF16CBCC2031CC8634DB8D40F04BFA911783650D82B7590AB873F4A8CF67E37E510E3A6E01F2GA091A083E08D40860012991C6EC5FA0E2D1C414E2EF85E44B3E2BB143DB3D88C43683EE3730671487B66658D532FCE3E8C0DEECD947AC40D3F60DCBC93B1B64FD771F2349CD5B8EC551EC5D16AF1BC1E5752723D672267271EB8E71C1F4ABAE71C1F7A693917989E7A50E749F97DAF4ECD1C3F7A753968792B1A735179BCFE5E184F267346FC1EBEBFB11F1D67237179861D1B7FD0B1FF9911B7BB8BBBA361B9
	23C572D60D26C9751C2992FDCF6F08AE475C8EE3B996F4607C7BBDC3BE1B9C4EEBCA47DED19774922D2ABC086CB01A25343D8A87A7B9C256F45A1E55C5EB7F0D0EE87AE0327E4DD124495A6E70F9865B2BB43B7E7EC5748B53E7042E03942D1CB1A3F2FD30710AAD51F5742A45D8C7EFD8226BE83FA5360E7EE2092E23EACBEC9D9D33208E2CAEBB1CE0D8160F69262CCFEF8707044EFE52972A33C31A4E0E9DB75A93D7EA3A4A06677AB03D317E6EF66A47B9AC7012ABF41CEAED1C8F0E980DD3B81375DD824F4A
	19C6639C6C570D5300F62D653CA43EAFB44E64B92F47FB719CB48E6559GE9B3392FFDC862E3BF8438ACED1CDC6DC9E641BEBDABF4EC368F24537D22946AEAE6713D74668B51EDE54A85E3DB19F9A13A2D4C3B905BD696DF08EEAB77DC08EDABDF39B031FFF9E44FCE6A1F7CFE59CF8301C6C915B47D0F8E68750F7644F47A562851FC36E8FA6C1995395618CEDF2067E4AF54474D62E779057DA81F57A7BBE41FCF36F73AC6512FE54EC27A8CBA875361BDC950BF5C0B79FFC59A718C290484E6A3DD2D083D36C3
	F9BFC09BC06FEC960331B86E1F0651B69DF022G71BB3D320DD866E05B45D65EF685502F86D80F6D202EFA8E9F732EF12CBF55A6D9E58555E399714A5CC61DFF425C387EF4548983A822FEF3D5F87D09913CB765756974BEB7341EDF68F21ACEDF31983F5EEEF751CBB605554DE845EFD6CEB5B8E55B4089CF20D3062DC6D26596D9C2CBE1B40F35633C312CF115F69F50A46BF3ACC9F3182ED64C61B9186483C6752BC57D13A1B9449C2D3E784BBC0E7AA5BD9B6E4732409D2671D69B14FB84AE311747DBCE0133
	9C67F499827740F1CE771040AD2C26772FF33B0ACA67F25C99FAE6B88A65B6017B4A915E36DB601A8E50BBE1A8BFB8D764EC24F03BDEB5B7E84BEB67C65AB54B4DB4C968FBB64C8D7F16211D6237A706FF4B70D4AF4FFB1921AEBE117374E36A57F220DCB00F63CE51F8F4AF14CB852E24024A8865B6013B21C248A8F03B5E21B2C2F945BC11376E67F9CDF2BB527C0C16D70BB6B5B466EE04BA4B6D9C37E014775DFBBB6FE70F925ACFB9ADAF0777B5376377D04BFB886176FBE72F991EEEF958AC12F30D30845C
	7811C0E955875565662A8FAB4BD8FEB1F9D87F8DD34D369B8D6F63904C73353C1A761C7242BC6D9BA62B443FE1D220AEE5BE7781D53D981F0B339CB2D2DBFB5267321F107A5A28C7E17B4B3DF49E307D1A79FCFF4922FD6ECE02589E20B10967C3836F72FB0634A50B492A270D6FD432AD17CDBDC95A7DE1126ECE65FDEBBE9BB3534A47FC906A57A7A16E8FD67D9AC3DDAE48E3E7A7D6DFEA5357231F1FC674751F0654A33FA3A16D270454230EF6E975CC36C77F4CE55B7868F222BC909F75FB32238B18CC3D5A
	537C5DF9F925735AB7EB77D80EA877D85423B17947E5FE56CFDC404F4BCF9D6038D58B789C7E356BF5633E388754E2046D3DBCC6A83C035BEC9E4579E53C9B156DEC0F595B4BF337F5026ED4AF4D6543E64EECF9555FD19B71F7A07D7D3D0D1A4C6DF7C47A03BC2DAF8EF620C1F8C563A5F08773855436E85F3F3B986CFB65F15E774AA8FDEF1A206FECDD5FCC0E2FD5236EE4B76EA5F6EDCFFD2C1A4B1BAB64359F8915779DA94853A3C7B8CF9B237034C547D371C2E833B048537E0561BC759C086449FF0073D4
	39107B1C843A866B20EC91385F0B33F9E0A1775927A9DDA61433852E0B62707E956FD6996EA73D5D86F6BB91E0FFD7C317A9467D43B50F916B9B9E7D5AE77AFBD60A9EAF95D17DEC84FE8A13795CCAD61CDB07472DB21AE1CAD38D75F50266F0C5687CB7DB03731FD851F895FCF170C21F882FE8F39E1F9CBE7F6F3F03F1582094F08D8E8DD26E99DFA768DEB59D681384DF7F553F0F68760D70AB18D23CEDAB53FC55B1D93FEFD40E35197209312F4ACCE6731A159C5CB758F867C6C37570DBDBD08F6923DC8F05
	0BA26DC23FAE0D74D032A8DC8FA7142B176DFBFF534BD6FC3BE262FD0573D12A71D63DA8384730FC614F6D28DFA760639771734C6FFB701B5EAE29DF4B8AB6C1E869715134A0A05BFD9E2F6CD3C79816FE7121457F4E08B8A99630F6172E335D7D575C0E45DFEDD65C33D3F43AE7788B943FDC07E7F143CFC55C70A054199773BB0D5DF47F0A07F2B2C08640AA00F5G5B6F643A7C6BE715A4C61E556271364161D151250118FC0FD92F6F9B60FF5B42776774FF3E17E4CCEA0C7A0D0379CEE68FF55AB3E3653CBC
	FCB2796E6EC3793CD0B785E0B9C0A6409A0075C23EC91F15B379509C6AD5D532B969179CDC38AE17BC9CC463810B49F60338E7ABCC892E1B926D3DB2A5F2AD31F6EB8E70F60DBA1AEE6DDD095A6E25DEA3DD46E974338D5AE9398636677B06E496BFE40B78A1955A6CA36CDB42C2927CE3BE62020B08797859F6BE9FA5775DC99A1FEAB43D93E5BE32397CF78557E9292BFAAB9B7B7963FCEC67DDBC1E7EE6BF913C2E2D8E657539A31CBE792E09F83D2C847B3875G6FC3BB07CB11676A2F09F3F556DD6CDC3D8E
	1E85CB984F4CAFEE37F13FD83244B8BE58C102FE3192E89A178463036EA561FE71B71BD117625B85017F6CC36C07DD54876FDE18DC4903C1390EDB717EB24AD963C8D539795011B25E6EFF55E03B620A37435BB54326D97A365DA5A91E3E37610C827F9D023651751BB494FC7F6625E05F19A37B2273D4D5D9D9EEBED2D9262DC5E59867453E042F3BAFD9112FE8BE9A6FECBAD59FC440BCB7763438B389AC61367384FEC225757F45883E333E0F587F9C2DEFD8512700D73699D924BE51A6470E73AA72C9ECDB50
	6AFEC33FB1945F58504F3F98F7ECEDF65038023D7FCD773ED63A5638695A7710677F21E3E8F9E1C7747C60FE07F1FE70FD07713DC125E3627B0B9AC7747B0B638E7D7737EC5E270C625CAC871BD889308E20606E2E223DF7F3BBFB4C01F6063FBC08746DCD76BE4CCF622F8DD4BE7E723E6832FE3DAF76EF94D6746137FFD68FAC64C16CDFFC07CAEDC575F8A9D23BB722F9ED1F2B4F29B2D28D7FF89F5A3AA2BBA8DA4BA751F3E4B54855FEB7BFB7EC24B12F9D4A2A40C9B6FE160893385F51F348AAA8678A5C92
	93DD6729701E4AF1C5793CED094015527DC201723840CD95392D04D4FEC6F8BAE4FF4A82FCEE6A44678CB6FF3F9CC3BFCB6E01B1816AG3A819400F800C400B40095GD9G6BG8A6E057985A887B88A508EE03F174F776D2E32897C0A169421136E13DCB86D5ABAB25569798FC0FFD36E1D18FF566E0510BB00E4E8139E23DDF8CC16897464BE1E9BF8D764CBEA6E63B99A8B0D6B3CD0CEGC8963447EC5CA7E65CE734BF2EBB9E5D27FF7BB29176669D46BD522F79DBA657AFC3F2616B207F02340965E2FD4D9E
	0D3EFE660F46DEBFA9544F4BE077180D566E83C611BF0F43611745BADDB6DAACF02D7EF60F1FE60F593EF43F9C7DAC19C95751DE10E5FF9ABF0F6D32051E0F1E39023AE1F1C040B651B9D13F47A334C31F76B4DDDEA4ADBC8E58D69E1D37F74A1137F8E8B3DD70768DD3A8EF5BC910373773B9EF8F9A4C8D0E39D5475BAA20D92B63EDC384EF474372C3877302E325087CD041D263310AF4E36D859A84BED6795260D81A6F22BE24C643B3DC9E4DEF34EBB8B64FFF9C0BEECF1B46E35B530B63467EF877F824BFFE
	ED5C589F6F9B577BE3ED5F9BC7397A401FF48B3AB2017BC460AAE81F087B58E5D71D82DFCD79C2FC0B0C83F2FCAD65EB18623A793E2743FD22619A47F1CD0048CE0F0F37ED9D27B9911722307299CA63F24392917A6820BC3A3C120E6F0F856E93B1972F55C54E4F1BF57A79E138BD9FC4523D7E011E0E47C257F7C6FE76BA5B9D386633A7BBC3E7D2DF2BAC8DBFDBBFCE71012561E76BCF2D5C0F47C3DD4ED23E9F9606FC3757362C2B28F759447ED3DB83E3117A5CBC2657A7DB1B9DA6C2783A7D2DF542B83C86
	8E879F16994477936D8FC3794177440260D873CDD7B3965B1FBA73797E24AE3B567D694913784E7AF9D21CD9538C7B61EDB6349042F3D6523B3A6F626952E37812400EA01EAFBD4DE63E2B0B5D1F523D77DA7F71F17C48BFF48CE6C77B9C1136355F9139D63B7375363B96F43EA1066D32F6BF8A0981375F5FD5E43E7FEAEC5E139F49171A9F69EBFEEB30E77C21D7CF477EBF878B2C4F0345267D0AEC727B484B11F6DAFF5B8F1FCA3B58385BC4CC2DE433139AC1AB59B40CD692404F486E85C8D1C9B13B72B61F
	6D68E8B403DB0568E3E7B3BC401F1A2DA3E63C6ED649AE9D066FBEAAB11DBB2D124D6CD88FCC6C94CC34D2EF1CFD69F0ED7E4F6F4CB7118260006DCE2D24G3798E04447984A075D093203FF5BB44BA67E4F499576ED72D5E2718FE36D4340470E10A440430B0E7CE265CF3B7C79E4F3ABF98DF3691D320F7D0C440E5F83E83FF7EEA6DB3D9E0815B1D681A67D7431492F0EA8F29B596D68405F4F79D559AEBEE6762B64957A6B0203187EBD58A2791DAA2A6523B6D26046CF1F494B2CDA115CFD8759C97AA3AAE9
	DEFD9D65177AD460F7D7B009ACFA5975AFBB7F7C56293599794454CC36A99E495EAC59A046EFA5DBD527CF76BBBD0ABD60BD4EBAAA9C661F2696C0F72F8B71D1588A8B048E0269014002A9BBF2F6B403DF8D7848C1CA0921D0052030B7B8A57C2D136CEBD57117669E5FFED1F7431B6A3B0F1DFC3547C472B4A1D175AF863CAAD31830819439229938D6FF742BC7967F78683ADCE852CCB651DF97404045C12A33C303D659B7A9ED4E9BC53333A7A3DFDED48F1D5417413A9C5220CB990197164A91F896A850621E
	BA92EC309562A4379FFF7D815C1843D4412B04C495339E291CF34FF31FD20902CDD5320F4D9DB4BD58A7FB868F56BB9CAE37ACBA0009046E5F905DA3CDC5984D4B01992F1CFA7285AB98DEA3499F2C2C247FD651FF8F613FD594D3C5B1D507E0B65711897F426B838114797500D89685FA904B82BF7D31CFC0930185436E24572CE676FF88B123B412DA2B12BD418FD8B4A466B52062D53F76690A59C93FDD210E2454DFA6184098CC89D93F1BB458F655B491AB1F705CED59977ABB168DB75B22FEF7101B2EFDBB
	76C0789D4BABF40FB88A786E8778B9DB167FC2226758BA87A5C5D1A5E5205EED47FC5B0991D58E66F87E64407E7D1CC62B54FEDFC273C67D3AB67F87D0CB87886A58546F9299GG98C7GGD0CB818294G94G88G88G3C0171B46A58546F9299GG98C7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCC99GGGG
**end of data**/
}
}
