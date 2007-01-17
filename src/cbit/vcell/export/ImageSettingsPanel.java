package cbit.vcell.export;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.simdata.gui.*;
import cbit.vcell.export.server.*;
import cbit.image.*;
/**
 * This type was created in VisualAge.
 */
public class ImageSettingsPanel extends javax.swing.JPanel implements ExportConstants, java.awt.event.ActionListener, java.beans.PropertyChangeListener {
	private javax.swing.JButton ivjJButtonOK = null;
	private javax.swing.JComboBox ivjJComboBox1 = null;
	private javax.swing.JLabel ivjJLabelCompression = null;
	private javax.swing.JLabel ivjJLabelMirroring = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JRadioButton ivjJRadioButtonCompressed = null;
	private javax.swing.JRadioButton ivjJRadioButtonUncompressed = null;
	private javax.swing.JButton ivjJButtonSet = null;
	private javax.swing.JCheckBox ivjJCheckBoxLoop = null;
	private javax.swing.JLabel ivjJLabelDuration = null;
	private javax.swing.JLabel ivjJLabelHigh = null;
	private javax.swing.JLabel ivjJLabelLow = null;
	private javax.swing.JLabel ivjJLabelQuality = null;
	private javax.swing.JSlider ivjJSliderQuality = null;
	private javax.swing.JTextField ivjJTextFieldInput = null;
	protected transient cbit.vcell.export.ImageSettingsPanelListener fieldImageSettingsPanelListenerEventMulticaster = null;
	private int fieldExportFormat = 0;
	private int fieldSelectedDuration = 10000; //milliseconds
	private int fieldImageFormat = 0;
	private ImageSpecs fieldImageSpecs = null;
	private int fieldCompression = 0;
	private int fieldLoopingMode = 0;
	private javax.swing.JPanel ivjGIF = null;
	private javax.swing.JPanel ivjJPEG = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private javax.swing.JPanel ivjTIFF = null;
	private cbit.vcell.simdata.gui.DisplayPreferences[] fieldDisplayPreferences = null;
	private cbit.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private boolean fieldHideMembraneOutline = true;
	private javax.swing.JRadioButton ivjJRadioButtonHideMembraneOutline = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JPanel ivjJPanel2 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ImageSettingsPanel() {
	super();
	initialize();
}
/**
 * ImageSettingsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ImageSettingsPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ImageSettingsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ImageSettingsPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ImageSettingsPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ImageSettingsPanel(boolean isDoubleBuffered) {
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
	if (e.getSource() == getJButtonSet()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldInput()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxLoop()) 
		connEtoC6(e);
	if (e.getSource() == getJRadioButtonHideMembraneOutline()) 
		connEtoC7(e);
	if (e.getSource() == getCancelJButton()) 
		connEtoC8(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener cbit.vcell.export.ImageSettingsPanelListener
 */
public void addImageSettingsPanelListener(cbit.vcell.export.ImageSettingsPanelListener newListener) {
	fieldImageSettingsPanelListenerEventMulticaster = cbit.vcell.export.ImageSettingsPanelListenerEventMulticaster.add(fieldImageSettingsPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> ImageSettingsPanel.fireJButtonOKAction_actionPerformed(Ljava.util.EventObject;)V)
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
 * connEtoC2:  (JButtonSet.action.actionPerformed(java.awt.event.ActionEvent) --> ImageSettingsPanel.setDuration()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setDuration();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JTextFieldInput.action.actionPerformed(java.awt.event.ActionEvent) --> ImageSettingsPanel.setDuration()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setDuration();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ImageSettingsPanel.initialize() --> ImageSettingsPanel.initMirrorChoices()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.initMirrorChoices();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (ImageSettingsPanel.exportFormat --> ImageSettingsPanel.updateFormatSpecificSettings()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateFormatSpecificSettings();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JCheckBoxLoop.action.actionPerformed(java.awt.event.ActionEvent) --> ImageSettingsPanel.setLoop()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setLoop();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JRadioButtonHideMembraneOutline.action.actionPerformed(java.awt.event.ActionEvent) --> ImageSettingsPanel.setHideMembraneOutline(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setHideMembraneOutline(getJRadioButtonHideMembraneOutline().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> ImageSettingsPanel.fireJButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
 * connEtoM1:  (ImageSettingsPanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonUncompressed());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (ImageSettingsPanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonCompressed());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (ImageSettingsPanel.initialize() --> JRadioButtonHideMembraneOutline.selected)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getJRadioButtonHideMembraneOutline().setSelected(this.getHideMembraneOutline());
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
	if (fieldImageSettingsPanelListenerEventMulticaster == null) {
		return;
	};
	fieldImageSettingsPanelListenerEventMulticaster.JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldImageSettingsPanelListenerEventMulticaster == null) {
		return;
	};
	fieldImageSettingsPanelListenerEventMulticaster.JButtonOKAction_actionPerformed(newEvent);
}
/**
 * Return the ButtonGroupCivilized1 property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new cbit.gui.ButtonGroupCivilized();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroupCivilized1;
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
 * Gets the compression property (int) value.
 * @return The compression property value.
 * @see #setCompression
 */
public int getCompression() {
	return fieldCompression;
}
/**
 * Gets the displayPreferences property (cbit.vcell.simdata.gui.DisplayPreferences[]) value.
 * @return The displayPreferences property value.
 * @see #setDisplayPreferences
 */
public cbit.vcell.simdata.gui.DisplayPreferences[] getDisplayPreferences() {
	return fieldDisplayPreferences;
}
/**
 * Gets the exportFormat property (int) value.
 * @return The exportFormat property value.
 * @see #setExportFormat
 */
public int getExportFormat() {
	return fieldExportFormat;
}
/**
 * Return the JPanelGIF property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGIF() {
	if (ivjGIF == null) {
		try {
			ivjGIF = new javax.swing.JPanel();
			ivjGIF.setName("GIF");
			ivjGIF.setLayout(new java.awt.GridBagLayout());
			ivjGIF.setBounds(0, 0, 212, 113);
			ivjGIF.setVisible(true);
			ivjGIF.setEnabled(true);

			java.awt.GridBagConstraints constraintsJLabelDuration = new java.awt.GridBagConstraints();
			constraintsJLabelDuration.gridx = 0; constraintsJLabelDuration.gridy = 0;
			constraintsJLabelDuration.gridwidth = 2;
			constraintsJLabelDuration.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelDuration.weightx = 1.0;
			constraintsJLabelDuration.insets = new java.awt.Insets(10, 5, 0, 5);
			getGIF().add(getJLabelDuration(), constraintsJLabelDuration);

			java.awt.GridBagConstraints constraintsJTextFieldInput = new java.awt.GridBagConstraints();
			constraintsJTextFieldInput.gridx = 0; constraintsJTextFieldInput.gridy = 1;
			constraintsJTextFieldInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldInput.weightx = 0.5;
			constraintsJTextFieldInput.insets = new java.awt.Insets(5, 10, 5, 5);
			getGIF().add(getJTextFieldInput(), constraintsJTextFieldInput);

			java.awt.GridBagConstraints constraintsJButtonSet = new java.awt.GridBagConstraints();
			constraintsJButtonSet.gridx = 1; constraintsJButtonSet.gridy = 1;
			constraintsJButtonSet.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJButtonSet.weightx = 0.5;
			constraintsJButtonSet.insets = new java.awt.Insets(5, 5, 5, 80);
			getGIF().add(getJButtonSet(), constraintsJButtonSet);

			java.awt.GridBagConstraints constraintsJCheckBoxLoop = new java.awt.GridBagConstraints();
			constraintsJCheckBoxLoop.gridx = 0; constraintsJCheckBoxLoop.gridy = 2;
			constraintsJCheckBoxLoop.gridwidth = 2;
			constraintsJCheckBoxLoop.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJCheckBoxLoop.insets = new java.awt.Insets(5, 5, 5, 5);
			getGIF().add(getJCheckBoxLoop(), constraintsJCheckBoxLoop);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGIF;
}
/**
 * Gets the hideMembraneOutline property (boolean) value.
 * @return The hideMembraneOutline property value.
 */
public boolean getHideMembraneOutline() {
	return fieldHideMembraneOutline;
}
/**
 * Gets the imageFormat property (int) value.
 * @return The imageFormat property value.
 * @see #setImageFormat
 */
public int getImageFormat() {
	return fieldImageFormat;
}
/**
 * This method was created in VisualAge.
 * @return ImageSpecs
 */
public ImageSpecs getImageSpecs() {
	return new ImageSpecs(
		getDisplayPreferences(),
		getImageFormat(),
		getCompression(),
		getJComboBox1().getSelectedIndex(),
		getSelectedDuration(),
		getLoopingMode(),
		getHideMembraneOutline());
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
 * Return the JButtonSet property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSet() {
	if (ivjJButtonSet == null) {
		try {
			ivjJButtonSet = new javax.swing.JButton();
			ivjJButtonSet.setName("JButtonSet");
			ivjJButtonSet.setText("Set");
			ivjJButtonSet.setMaximumSize(new java.awt.Dimension(300, 25));
			ivjJButtonSet.setPreferredSize(new java.awt.Dimension(60, 27));
			ivjJButtonSet.setMinimumSize(new java.awt.Dimension(25, 25));
			ivjJButtonSet.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSet;
}
/**
 * Return the JCheckBoxLoop property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxLoop() {
	if (ivjJCheckBoxLoop == null) {
		try {
			ivjJCheckBoxLoop = new javax.swing.JCheckBox();
			ivjJCheckBoxLoop.setName("JCheckBoxLoop");
			ivjJCheckBoxLoop.setSelected(true);
			ivjJCheckBoxLoop.setText("loop infinitely");
			ivjJCheckBoxLoop.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxLoop;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBox1() {
	if (ivjJComboBox1 == null) {
		try {
			ivjJComboBox1 = new javax.swing.JComboBox();
			ivjJComboBox1.setName("JComboBox1");
			ivjJComboBox1.setPreferredSize(new java.awt.Dimension(130, 25));
			ivjJComboBox1.setMinimumSize(new java.awt.Dimension(126, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBox1;
}
/**
 * Return the JLabelCompression property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCompression() {
	if (ivjJLabelCompression == null) {
		try {
			ivjJLabelCompression = new javax.swing.JLabel();
			ivjJLabelCompression.setName("JLabelCompression");
			ivjJLabelCompression.setPreferredSize(new java.awt.Dimension(108, 27));
			ivjJLabelCompression.setText("Select compression mode:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCompression;
}
/**
 * Return the JLabelDuration property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDuration() {
	if (ivjJLabelDuration == null) {
		try {
			ivjJLabelDuration = new javax.swing.JLabel();
			ivjJLabelDuration.setName("JLabelDuration");
			ivjJLabelDuration.setPreferredSize(new java.awt.Dimension(50, 27));
			ivjJLabelDuration.setText("Animation duration (seconds):");
			ivjJLabelDuration.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDuration;
}
/**
 * Return the JLabelHigh property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHigh() {
	if (ivjJLabelHigh == null) {
		try {
			ivjJLabelHigh = new javax.swing.JLabel();
			ivjJLabelHigh.setName("JLabelHigh");
			ivjJLabelHigh.setText("high");
			ivjJLabelHigh.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHigh;
}
/**
 * Return the JLabelLow property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLow() {
	if (ivjJLabelLow == null) {
		try {
			ivjJLabelLow = new javax.swing.JLabel();
			ivjJLabelLow.setName("JLabelLow");
			ivjJLabelLow.setText("low");
			ivjJLabelLow.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLow;
}
/**
 * Return the JLabelMirroring property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMirroring() {
	if (ivjJLabelMirroring == null) {
		try {
			ivjJLabelMirroring = new javax.swing.JLabel();
			ivjJLabelMirroring.setName("JLabelMirroring");
			ivjJLabelMirroring.setPreferredSize(new java.awt.Dimension(147, 27));
			ivjJLabelMirroring.setText("Extend images by mirroring:");
			ivjJLabelMirroring.setMaximumSize(new java.awt.Dimension(147, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMirroring;
}
/**
 * Return the JLabelQuality property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelQuality() {
	if (ivjJLabelQuality == null) {
		try {
			ivjJLabelQuality = new javax.swing.JLabel();
			ivjJLabelQuality.setName("JLabelQuality");
			ivjJLabelQuality.setText("Image quality");
			ivjJLabelQuality.setEnabled(true);
			ivjJLabelQuality.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelQuality;
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

			java.awt.GridBagConstraints constraintsJLabelCompression = new java.awt.GridBagConstraints();
			constraintsJLabelCompression.gridx = 0; constraintsJLabelCompression.gridy = 0;
			constraintsJLabelCompression.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelCompression.insets = new java.awt.Insets(10, 5, 0, 5);
			getJPanel1().add(getJLabelCompression(), constraintsJLabelCompression);

			java.awt.GridBagConstraints constraintsJRadioButtonUncompressed = new java.awt.GridBagConstraints();
			constraintsJRadioButtonUncompressed.gridx = 0; constraintsJRadioButtonUncompressed.gridy = 1;
			constraintsJRadioButtonUncompressed.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJRadioButtonUncompressed.insets = new java.awt.Insets(0, 10, 0, 5);
			getJPanel1().add(getJRadioButtonUncompressed(), constraintsJRadioButtonUncompressed);

			java.awt.GridBagConstraints constraintsJRadioButtonCompressed = new java.awt.GridBagConstraints();
			constraintsJRadioButtonCompressed.gridx = 0; constraintsJRadioButtonCompressed.gridy = 2;
			constraintsJRadioButtonCompressed.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJRadioButtonCompressed.insets = new java.awt.Insets(0, 10, 0, 5);
			getJPanel1().add(getJRadioButtonCompressed(), constraintsJRadioButtonCompressed);

			java.awt.GridBagConstraints constraintsJRadioButtonHideMembraneOutline = new java.awt.GridBagConstraints();
			constraintsJRadioButtonHideMembraneOutline.gridx = 0; constraintsJRadioButtonHideMembraneOutline.gridy = 3;
			constraintsJRadioButtonHideMembraneOutline.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJRadioButtonHideMembraneOutline.insets = new java.awt.Insets(15, 10, 0, 5);
			getJPanel1().add(getJRadioButtonHideMembraneOutline(), constraintsJRadioButtonHideMembraneOutline);

			java.awt.GridBagConstraints constraintsJLabelMirroring = new java.awt.GridBagConstraints();
			constraintsJLabelMirroring.gridx = 0; constraintsJLabelMirroring.gridy = 4;
			constraintsJLabelMirroring.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelMirroring.insets = new java.awt.Insets(10, 5, 0, 5);
			getJPanel1().add(getJLabelMirroring(), constraintsJLabelMirroring);

			java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
			constraintsJComboBox1.gridx = 0; constraintsJComboBox1.gridy = 5;
			constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBox1.weightx = 1.0;
			constraintsJComboBox1.insets = new java.awt.Insets(5, 10, 5, 5);
			getJPanel1().add(getJComboBox1(), constraintsJComboBox1);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 6;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel2.weightx = 1.0;
			constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJPanel2(), constraintsJPanel2);
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonOK = new java.awt.GridBagConstraints();
			constraintsJButtonOK.gridx = 1; constraintsJButtonOK.gridy = 1;
			constraintsJButtonOK.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJButtonOK(), constraintsJButtonOK);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 2; constraintsCancelJButton.gridy = 1;
			constraintsCancelJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getCancelJButton(), constraintsCancelJButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JPanelQuality property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPEG() {
	if (ivjJPEG == null) {
		try {
			ivjJPEG = new javax.swing.JPanel();
			ivjJPEG.setName("JPEG");
			ivjJPEG.setLayout(new java.awt.GridBagLayout());
			ivjJPEG.setBackground(new java.awt.Color(204,204,204));
			ivjJPEG.setBounds(0, 0, 212, 191);
			ivjJPEG.setEnabled(true);
			ivjJPEG.setVisible(true);

			java.awt.GridBagConstraints constraintsJLabelQuality = new java.awt.GridBagConstraints();
			constraintsJLabelQuality.gridx = 0; constraintsJLabelQuality.gridy = 0;
			constraintsJLabelQuality.insets = new java.awt.Insets(3, 0, 0, 0);
			getJPEG().add(getJLabelQuality(), constraintsJLabelQuality);

			java.awt.GridBagConstraints constraintsJSliderQuality = new java.awt.GridBagConstraints();
			constraintsJSliderQuality.gridx = 0; constraintsJSliderQuality.gridy = 1;
			constraintsJSliderQuality.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJSliderQuality.weightx = 1.0;
			constraintsJSliderQuality.insets = new java.awt.Insets(0, 3, 0, 3);
			getJPEG().add(getJSliderQuality(), constraintsJSliderQuality);

			java.awt.GridBagConstraints constraintsJLabelLow = new java.awt.GridBagConstraints();
			constraintsJLabelLow.gridx = 0; constraintsJLabelLow.gridy = 2;
			constraintsJLabelLow.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLow.insets = new java.awt.Insets(0, 3, 3, 0);
			getJPEG().add(getJLabelLow(), constraintsJLabelLow);

			java.awt.GridBagConstraints constraintsJLabelHigh = new java.awt.GridBagConstraints();
			constraintsJLabelHigh.gridx = 0; constraintsJLabelHigh.gridy = 2;
			constraintsJLabelHigh.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJLabelHigh.insets = new java.awt.Insets(0, 0, 3, 3);
			getJPEG().add(getJLabelHigh(), constraintsJLabelHigh);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPEG;
}
/**
 * Return the JRadioButtonCompressed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonCompressed() {
	if (ivjJRadioButtonCompressed == null) {
		try {
			ivjJRadioButtonCompressed = new javax.swing.JRadioButton();
			ivjJRadioButtonCompressed.setName("JRadioButtonCompressed");
			ivjJRadioButtonCompressed.setText("Compressed");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonCompressed;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonHideMembraneOutline() {
	if (ivjJRadioButtonHideMembraneOutline == null) {
		try {
			ivjJRadioButtonHideMembraneOutline = new javax.swing.JRadioButton();
			ivjJRadioButtonHideMembraneOutline.setName("JRadioButtonHideMembraneOutline");
			ivjJRadioButtonHideMembraneOutline.setPreferredSize(new java.awt.Dimension(108, 27));
			ivjJRadioButtonHideMembraneOutline.setText("Hide Membrane Outlines");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonHideMembraneOutline;
}
/**
 * Return the JRadioButtonUncompressed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonUncompressed() {
	if (ivjJRadioButtonUncompressed == null) {
		try {
			ivjJRadioButtonUncompressed = new javax.swing.JRadioButton();
			ivjJRadioButtonUncompressed.setName("JRadioButtonUncompressed");
			ivjJRadioButtonUncompressed.setSelected(true);
			ivjJRadioButtonUncompressed.setText("Uncompressed");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonUncompressed;
}
/**
 * Return the JSliderQuality property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getJSliderQuality() {
	if (ivjJSliderQuality == null) {
		try {
			ivjJSliderQuality = new javax.swing.JSlider();
			ivjJSliderQuality.setName("JSliderQuality");
			ivjJSliderQuality.setPaintLabels(false);
			ivjJSliderQuality.setBackground(new java.awt.Color(204,204,204));
			ivjJSliderQuality.setPaintTicks(true);
			ivjJSliderQuality.setValue(6);
			ivjJSliderQuality.setMajorTickSpacing(1);
			ivjJSliderQuality.setMaximum(10);
			ivjJSliderQuality.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSliderQuality;
}
/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("GIF", null, getGIF(), null, 0);
			ivjJTabbedPane1.insertTab("TIFF", null, getTIFF(), null, 1);
			ivjJTabbedPane1.insertTab("JPEG", null, getJPEG(), null, 2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}
/**
 * Return the JTextFieldInput property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldInput() {
	if (ivjJTextFieldInput == null) {
		try {
			ivjJTextFieldInput = new javax.swing.JTextField();
			ivjJTextFieldInput.setName("JTextFieldInput");
			ivjJTextFieldInput.setPreferredSize(new java.awt.Dimension(30, 27));
			ivjJTextFieldInput.setText("10");
			ivjJTextFieldInput.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldInput;
}
/**
 * Gets the loopingMode property (int) value.
 * @return The loopingMode property value.
 * @see #setLoopingMode
 */
public int getLoopingMode() {
	return fieldLoopingMode;
}
/**
 * Gets the selectedDuration property (int) value.
 * @return The selectedDuration property value.
 * @see #setSelectedDuration
 */
public int getSelectedDuration() {
	return fieldSelectedDuration;
}
/**
 * Return the TIFF property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTIFF() {
	if (ivjTIFF == null) {
		try {
			ivjTIFF = new javax.swing.JPanel();
			ivjTIFF.setName("TIFF");
			ivjTIFF.setBounds(0, 0, 179, 85);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTIFF;
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
	getJButtonSet().addActionListener(this);
	getJTextFieldInput().addActionListener(this);
	this.addPropertyChangeListener(this);
	getJCheckBoxLoop().addActionListener(this);
	getJRadioButtonHideMembraneOutline().addActionListener(this);
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
		setName("ImageSettingsPanel");
		setPreferredSize(new java.awt.Dimension(240, 343));
		setLayout(new java.awt.GridBagLayout());
		setSize(220, 444);
		setMinimumSize(new java.awt.Dimension(240, 307));

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJTabbedPane1 = new java.awt.GridBagConstraints();
		constraintsJTabbedPane1.gridx = 0; constraintsJTabbedPane1.gridy = 0;
		constraintsJTabbedPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJTabbedPane1.weightx = 1.0;
		constraintsJTabbedPane1.weighty = 1.0;
		constraintsJTabbedPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTabbedPane1(), constraintsJTabbedPane1);
		initConnections();
		connEtoC4();
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
 * Comment
 */
public void initMirrorChoices() {
	getJComboBox1().addItem("No mirroring");
	getJComboBox1().addItem("Mirror left");
	getJComboBox1().addItem("Mirror top");
	getJComboBox1().addItem("Mirror right");
	getJComboBox1().addItem("Mirror bottom");
	return;
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
		ImageSettingsPanel aImageSettingsPanel;
		aImageSettingsPanel = new ImageSettingsPanel();
		frame.add("Center", aImageSettingsPanel);
		frame.setSize(aImageSettingsPanel.getSize());
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
	if (evt.getSource() == this && (evt.getPropertyName().equals("exportFormat"))) 
		connEtoC5(evt);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener cbit.vcell.export.ImageSettingsPanelListener
 */
public void removeImageSettingsPanelListener(cbit.vcell.export.ImageSettingsPanelListener newListener) {
	fieldImageSettingsPanelListenerEventMulticaster = cbit.vcell.export.ImageSettingsPanelListenerEventMulticaster.remove(fieldImageSettingsPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * This method was created in VisualAge.
 * @param enabled boolean
 */
private void setAnimationEnabled(boolean enabled) {
	getJTextFieldInput().setEnabled(enabled);
	getJButtonSet().setEnabled(enabled);
	getJLabelDuration().setEnabled(enabled);
	getJCheckBoxLoop().setEnabled(enabled);
}
/**
 * Sets the compression property (int) value.
 * @param compression The new value for the property.
 * @see #getCompression
 */
private void setCompression(int compression) {
	fieldCompression = compression;
}
/**
 * Sets the displayPreferences property (cbit.vcell.simdata.gui.DisplayPreferences[]) value.
 * @param displayPreferences The new value for the property.
 * @see #getDisplayPreferences
 */
public void setDisplayPreferences(cbit.vcell.simdata.gui.DisplayPreferences[] displayPreferences) {
	cbit.vcell.simdata.gui.DisplayPreferences[] oldValue = fieldDisplayPreferences;
	fieldDisplayPreferences = displayPreferences;
	firePropertyChange("displayPreferences", oldValue, displayPreferences);
}
/**
 * Comment
 */
public void setDuration() {
	double seconds;
	String typed = getJTextFieldInput().getText();
	try {
		seconds = new Double(typed).doubleValue();
		if (seconds < 0.1) seconds = 0.1;
		if (seconds > 1000) seconds = 1000;
		int milliseconds = (int)(seconds * 1000);
		seconds = (double) milliseconds / 1000;
		getJTextFieldInput().setText(Double.toString(seconds));
		setSelectedDuration(milliseconds);
	} catch (NumberFormatException e) {
		// if typedTime is crap, put back old value
		seconds = getSelectedDuration() / 1000;
		getJTextFieldInput().setText(Double.toString(seconds));
	}
	return;
}
/**
 * Sets the exportFormat property (int) value.
 * @param exportFormat The new value for the property.
 * @see #getExportFormat
 */
public void setExportFormat(int exportFormat) {
	int oldValue = fieldExportFormat;
	fieldExportFormat = exportFormat;
	firePropertyChange("exportFormat", new Integer(oldValue), new Integer(exportFormat));
}
/**
 * Sets the hideMembraneOutline property (boolean) value.
 * @param hideMembraneOutline The new value for the property.
 * @see #getHideMembraneOutline
 */
private void setHideMembraneOutline(boolean hideMembraneOutline) {
	fieldHideMembraneOutline = hideMembraneOutline;
}
/**
 * Sets the imageFormat property (int) value.
 * @param imageFormat The new value for the property.
 * @see #getImageFormat
 */
private void setImageFormat(int imageFormat) {
	fieldImageFormat = imageFormat;
}
/**
 * Comment
 */
public void setLoop() {
	if (getJCheckBoxLoop().isSelected()) setLoopingMode(0);
	else setLoopingMode(1);
	return;
}
/**
 * Sets the loopingMode property (int) value.
 * @param loopingMode The new value for the property.
 * @see #getLoopingMode
 */
private void setLoopingMode(int loopingMode) {
	fieldLoopingMode = loopingMode;
}
/**
 * Sets the selectedDuration property (int) value.
 * @param selectedDuration The new value for the property.
 * @see #getSelectedDuration
 */
private void setSelectedDuration(int selectedDuration) {
	fieldSelectedDuration = selectedDuration;
}
/**
 * Comment
 */
public void updateFormatSpecificSettings() {
	for (int i=0;i<getJTabbedPane1().getTabCount();i++) getJTabbedPane1().setEnabledAt(i, false);
	switch(getExportFormat()) {
		case FORMAT_GIF:
			setImageFormat(GIF);
			setCompression(COMPRESSED_GIF_DEFAULT);
			getJTabbedPane1().setSelectedIndex(0);
			getJTabbedPane1().setEnabledAt(0, true);
			setAnimationEnabled(false);
			getJRadioButtonCompressed().setSelected(true);
			getJRadioButtonCompressed().setText("Compressed (indexed 256 colors)");
			getJRadioButtonCompressed().setEnabled(true);
			getJRadioButtonUncompressed().setEnabled(false);
			break;
		case FORMAT_ANIMATED_GIF:
			setImageFormat(ANIMATED_GIF);
			setCompression(COMPRESSED_GIF_DEFAULT);
			getJTabbedPane1().setSelectedIndex(0);
			getJTabbedPane1().setEnabledAt(0, true);
			setAnimationEnabled(true);
			getJRadioButtonCompressed().setSelected(true);
			getJRadioButtonCompressed().setText("Compressed (indexed 256 colors)");
			getJRadioButtonCompressed().setEnabled(true);
			getJRadioButtonUncompressed().setEnabled(false);
			break;
/*		case FORMAT_TIFF:
			getJTabbedPane1().setSelectedIndex(1);
			getJTabbedPane1().setEnabledAt(1, true);
			break;
		case FORMAT_JPEG:
			getJTabbedPane1().setSelectedIndex(2);
			getJTabbedPane1().setEnabledAt(1, true);
			getJRadioButtonCompressed().setSelected(true);
			getJRadioButtonCompressed().setText("Compressed (lossy)");
			getJRadioButtonCompressed().setEnabled(true);
			getJRadioButtonUncompressed().setEnabled(false);
			break;
*/	}
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCAFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DD8D45735A8C9C8CA14C4135006245E56A4A62509CD4D2DC9CC6357EBEFEDCBDA5AD81EE9FC6FE2BF5A1056765A065EF8FBE9AF77668828609FA41204C42244E84408481F0A088685919495E1D4E2C8E4E04E7CB0E7668CB3879995752D3D77D97B1CB9F3063FFBF35F737B16676C3557DEFB6D3557DE7BEF1DC1A85CBCFD45722CC2C1D83EC2D07F6DDBAE88EB2F8A421E1F455E0F385A6D49F902
	6E5F7BG2F8B73F2F3605982F9528349F98542F5FBAE243BA11DF4DFF25E5B703EC13845EE7343CB04FA6AA16F610FEB87C72EA7E7A756330A2687725742F3BDC08F6048F366F6D27F6D79C5AABEDA45A3AC4F82DCA9211911DF2C625201A697F08DG16D2C6AF411389F0EBDD2DCA57FDD702306CA77E2CF5040F0EA7C212DAB6DDFD1688FFDB9E3F035475B3AAA7A9D38A699EG5278EC21F970A3F856373B9D077AFADDCA7925BED1124A4520CF76ABF5F52D9E2BC36C94954565F5845A2DDED18275FDC4783D
	D910CB4B8BC28A701ACEF13709240EAEF8778144FC04782BF602CF0377CC00421D582E035B6BD5393B2EBEADAC2C726464C7E8D759CE569E4BCE2DDDA51E45D96F50BEE8AAA33A1E8AF93381928116822CADCB4EAB8358C87B7E44CE524E433DCA236C71485E43BE5F914966BFA95BC4895E6B6AA0C745F508FD325FA6882CFF7FD7FE1909BCA381AB776BF117E3BAC928463E1E59713810721D2FE42DB251C9CA42552C26B2260B6EB2CDA72C6EF78BA255BDDC96EA0B21F51F9774F5CFFD65056C95A6F54FCCF9
	A1FB2A4AE7062E6EC5E5242F7FC6EDE0BE3C13F224BC432F20B6D03CCB43B33B0C2574A4DD81F9F13B50B63A5623ACEDB98F8A8515771A6AA17E9FE333B2F6B1998A4A0D32DCF4905E75652C4E4DDC16A10A0F566119ACB7D199C9BA897292F7A767B17BBAD9CE46697C0A643CD4008C00024A643C8A00B6001ECA3431381D3B47E8E36DD63FF2E450A559C43F404A1EACFB8B1EE2D071DBDB3D81456A6D934FD97DAEEB2FA486841566DC9951C1EFC839CF296D77C19D47C42F6837AAAE598B5C2D9ED1917D81C6
	E3ABFB1D5078C51AEBF1F9C428406393102FD436811E12B5201C75592C0A28D130FC250C7449A56BB960888C601D69654AEE222FA8E8FFBCC0825743AB145F89518F7E222E2ECD165D03BE8BE90910D1C974DCC9FD47DCF8DFD809BA3ED4C1F075D5607F2A104F073B5E5071E9F27591712D7E4BB846846A631DC09FD305F67327AE341B26F55F924ADE7832295D14ACFEA1FBE695331779D54671330672A532AD2DC2BFFF3758E5B2EE1CD52163A9264AFCFC0D4DE7BCE6DAC736A13DE2827C5773BECB2BA4FD36
	36BAB92F9420259AF5ED5DD542EC7708B5A022351E76032109B6CD73CCCE51C17CB651779AGB29765337E90B6283ADE213609487EA6405BEA7D242E77F8FB2FD1BEBDA0C38C40AC00C5GC28D4886508E9084188E1088108ED0348766C8GA74054BD58B7DF32272B75E66B6A0EA41F19BC9351677FEBE0ED7F91EDFB3E5AAEECE3A4BD985B3D797F03F6CC84184D1EAFA76D96AF09DE25C97409DE9B3C84543C4F4BC93BD5FFEA17F9999B457B407FB602438BF0FCFFB9511DD5D17C2E5EC1052FF9F83E1F5695
	986C75E92E9B6B3ADCCEF4AD8F017F6574574B096F6A133DDE118E9EDEC44D0F223ED314C48F885DA88FFA95063FEDB7D9DF3A454B2A6F1E34FB25C0FC2FB4C8668136E61BE5A57D69733BBCB0A449D4A2646EA5FE352520C82723B9B0BE485AB3851E69FB50BF8EFD007EF16B15EF8A73AE7A324C6678780727E4356FE53E4C36D7730FCC262B9566325FC4FD380A8FB0D933FA089E4C561159344C8F8244EE0F8CAA0A6CBD661787FD0D2ECBAE49F5C534D58A68335E2B2673B548918FB0EBAF7A2C7F28A079B8
	BF284E2AD116A426F2484B20FAB9EDAF5435ADF1AF530BE6C7FF28ACA0FDAB8E8C0AB06D12096A84577F1FAB5FB0641DD37B02657F35127405241B630AEAC91DF39D3836EF58171C5785905C476A1D834F9C20E1F42BDCC8E70374F02DC6B72D164948681E65F4F3A03DGA03D560C6EC6BF52D56CE779AD6A33E73F1EEE3F9B6922547CF875B9AB046ED5CE178278E57BB5798A6B981DD2C7683EA1A1DDD91DD6AF19BF1BC268BE75A05D3C88F4958788DD3E9769965531F9A09D1E826415G14F33A451CEE78G
	1BB36260B9C3C7274AA7A35D7C836A380067B20042FAFD3DC19F52155633B927211E51DB6A99BF26171A8124D36A991FD815AEA904DF8627DBD64F662E358735FAB5FE0957102E9872EBGAC87993FB8F8269D447D5A2BBB4898277B35FE6BA5EB30BCB084E32A2E6EB89DD5ECEF7677F4FDC546FB46C15C03EDF211B1D4F188748D50F3887D41AF56619EAC4E1DA0C8698B33A36DAD0307D83B269D527CC1E242D0B69B230E9AD2E78A64ADBD0463D329522F838F8782F21F0BAEC1553173C1355EDFB0DCE975EA
	F85ADD7E00528A8EBD08632C3C1A48CB5628AADA75050FFC7001205F3FBA86DDF5F5E66EC45D63DCEB5637F7EE7E352CB5915AABFC2C3673E32D3D6DC9FF49E6FB0517699E0278309E480F87E03E664F8E3D2FD9C070G458D49F94C5F35B92B60E9258E2A1CBD5AC53FDD76FBC4DBA96951F26B10D2CE67347243B47BA8F95F016D5DF962A0530FF81C35727489C6F651482C0DF29BD4DCD2B9E5F13A379F7C62019D67845C43CFF6629E3E22816D61418D8D2A6CC90FBFA8545F78BC4BECCF9E136F4CF2B6B03D
	CCED5074D338F0F1D6A9G53D1058B77F6A9C0D3F898F57400E454D19B64798E230D2FB8321E7ADC25E910EDE8CA09500CE79A67998D74F3B94F64903DAFBB4CF8969FC13D1F681F285ED93D897DFC1F8ABC1D1CEF3E1B60DDDE17623292BBAB5D415A70CBAA8F411FF4797D323F51A93B7A44G3404716BF5A33F6940EB9167779C45337317E6104E2AB0FEE77B49DAED106E4F983E53A776396C2EBE1CD60912986FE289F997B442F8EFC49D795DFA9DC5B5B29DA5B4225E5FEDDB4D748EDBB61FCE678BA43475
	39C03B0C53D7F837901D5AD4DBD3FDD179615EG2C3A7A94E61F546018EDFDEB135E36F22D371A5AD66C6F9DD9E5CD6AF9CF13E6DB43E0D7D339EDC5FBD026E9C033900061EB3D08DFFB9466142328572FFB493A4A6AF7D4B2DDECBF285745B050A5GA4F17A9FD3FA45698AB05907BFC1592312E68BC59B6EB0151DC06AD1A6F3EE33A6FB4BEF96E7F57F86E5DFA9230CDDC0B335996B744821B2C6DB4CE4CCEC4636AE60FCD2GD7F88C796C7399ED1271DB38470CDF9BAFE77565527EA76BA751466AF840477D
	9C50459F433EAFBCC41EF4DDAA60DA6E5D81626F1DAE1BF8D274747AE1C2BABD28C8AE2F285A51E783E8178B00CFEE8B56BBFF603F66933F7E413B4CEEDB422BAE3D20597176EB58168628BB486B3F739A6A1D517D799A4AB98BE81634607809B6756DA52D48276B7AFF2D9D2F6CB8AC047876C6F28CA4CD583F5FFB9D7DFBCDAB6E151F66E703D2AB334F28D66D4CA28E5E93DAC34FB05819595D546F4EEDB51E6B7D9D45AFE8B51E6BED65FEBA8D72DA0E63B94FEF7712754BCCC8A7822482E4G941DC84E2BBD
	016B06272A2B05D14E51DA3D8AACAB2CD2E748068C4F33EAC81BE18F2526FDB5FADBE5F2D8EAC9DFDA20CE9FC08C40F400F9DC06FF2B99D50623C1D399181E7F59B15A79EF6A09D0BD33F2FF9D35DCCEDBE8B9E6BB791D24DDA66BC2E6C9449FD19B7E394D38C7A4BE2098F8D6GB03BA9256B8232AEEAE9C33B196AA4F2C5350D7DFC0B6979517DE4DD9B8765E681A4822481641ECC4E5B8A50F292755D3C6760287AA6D6EE119B8157827B2DC6635E1A6BF02F4B28C36749D0394DF55F94D62E5C100EB670E1BA
	FD58E95469B4201B873070A46AF411C4F4CA5656CBCF22CE7FB0C1B9B3C615337D14190DECABADB637910B98D41ADD22E4E33266B9F17F60BC0532BE5EBFB1D9670615B34ABA7D14D9B927FBE275DD8AABE72CEF0EE9FDDB240955D791D64ED85F82533EB8F4EE23F9DFB4BA45BE7791B9287A70EF3A31AF16703E78E5D81DC6B017B5ED54F2A527C34B31B17C7E81E2B73510E78110GE2GE682A482ACBD0DE3785EBD8704310DE1B625050FE0A667A44F445A97BFEA39356DE6658EF9A7D6DF5B286536361B75
	7DDBC7B61977FD0755661251E1B35F72024B68DB483DC4BD70EDEFC75F727941DC1537339D6759876AC91FCD0774DC0005G69047E8C78F9G4B996C335F6FB9B2DA1F716BA468B7BC9FB5F13ECCA7EB650969725DD14BC9E7C24B3136BFEB49D5F9C41F6163E302B2240DDAEE2E298CEB76228C8B398C5F70CDCC06A7C7AD3736432CDC5F40447C57C0D839F2238D1B56376B5A445AF7E054F27526757DDBD8393135EF4575515AE769B035AB870EA929837BF49A65B543C0BF92307B4E1F537338B9D0EEA1G43
	5DB4C4F0CBA01D49F11FDFA63812CED88BF7A26E46959A4782E925936508F791DC8C2427F15CC394C756F253787A62A31771BE115063FBCC67585BE1862C0E4630BA747713463B4B7102FA1F3CB772BD4FDF761A5F73E46EB53F67495A77BA43838F7DADBC662F5EB772BD509B7BA25F83F557C616332F56DCCED72D391CF26D48F28E55C61273BD2A775FA964DEA56C7E6994F8F281776BDB1CF9AA4DD1C5EE2C64363FD2C45B1F5D096747F5743CF3A924F3AC30DF30200D6DF262782A81DC0BC50DC9D11FBEF8
	C6DBC26D0C5975B7689AB39E7292ACB8974D5E9FD9FF4F6EB7575F777713B5F5C09E747709C69DA56EAF62F95A04552A089E2286C6738BDADE014D3F982663C534BC4B0BD47E577BC76E07675E4A277D988808813A78E932AAD6D57F9E17DE7F646C004D79C37610B95F42667CB4F8E6D8F04EDF4C576885E7310F0E50F33A96C877G844FE29FAD164C628A261ED5E3F84E1A7B01B9A678A4402582AC536531363C5F2FEFCBB5778F6B39FD641E534A942B6F9570ECBA97DA8F6377F2883F9A0E7F3ADB0F5F4371
	1FC8FA7CDEDE7F63F4BF50E528C3D253D1700CB32DFF1DC7476FE4A537091D87A25BE85581F39B2DBD9059C6BFBEB03A0D369C08EC23278F0CEE23DD87A259A8EB6B77C35ADA4571035EB5AA3E5D6E17BD6DD52AFFD8570F7EA1619C5ADEF19B313DD4C8E7G941CC75B6BBBE8B65F171EE7BA2FBF9FEEE3CC66476AA36B796F6B4D75BC27BE321E7F21FEF4BDA756C756737375236BF9D1BD51D72F4B66722BB750CC0F0DF2289EABD55B1CE7A13AE9B76840262603A63AD1ED53276FAFED2C956CC5DD476A4ACE
	D75FE7C76477ABDF28FC55AA7E7E817D185A4769ED57C269EBD4FC79F5BDFDAD17EB1E6919D622AACF0A09DCEC0E788EBD3FCC07FC218B4FD8DFAEA0FD64730BF6516F97ED1D2EABE4BEEB69A2747FC957D98570DE4C696307C87C04E43DAC8FAA4017522D8B22FF0D05748C00A40054AE66E7599E6D09A152C6BB6CFCE028102B96E161A7246C60E5AC5BGE98B00829087B07B932C73ABD7C8D9A868728CFA2804CCBE5BD5A2DF7105643C528BC87BBD8F51913B3D2DD47578ED010C256396EBEF2FE8A3B915EA
	DC1AE85423650226BB6705F0BD32B35A47695CBDFC41F8F67B8C45CF3DE0BC3BEDF060596D8C484B7D945722CDF44E11A09D43F14F533542B4C84F643827E89C48BCC8AF60380D153836CD6138ED3984D7568DF3C0B75707934F62233B691D85CDF7F5E33F6C4CA5732A5999965716006B18EF5B717CBB319B7D54D25AB66133643CA2000A4F50CF7D46E954ED4BE71ABE3B615D87902D43B19DBEC5675C784F188E73B87E8794BFDB07E73AED21F812DEGF9B51F23AEDE2367037110CE60389E8F414D06F49247
	35D25F1B8A69CC0E7B1E9BF5DBF091F157A8DD8F24039C37115245C2FABA47EDF3A23F599CF71C52A5C2FA9147FD1B57112651C9482F2687F1BF24F4AD106E663875144E8769A80E8BC848AF1663DEF1A13F149E6C17B7424E8F53FB34BE4831C6327969F4BEAA32B27D67F37CAC0AEF5061D93FEC21F812F6C2DE1A95E53AC24FD6FAFA217FFB9177CF87D176E10EFB111EC3A4C1BA1563CA4EE259E59CF76BD9ACAB74A1EEBBBDBFE8073444F1CFECA738A8C8CF653837CA316CB40EFB116ED317C2BA13636A1D
	C817EB43F175136B2163AA15263BA0BF5AC63EF3D858A888685FAF8D154053AB8E1570CB2BA617C77412605F522A1A0A32126A1A6A9D6CDEF47A107E5B045A55B71B4613D1FFEFD37D3D7A4CFC73FE755B04BDC17CB621D0CC4EEB93F12CD776041CD793C16A6A0E79DD36A3D6C79B7568EC1CDF8B662A6523C59C670FD11EF3A13D90E02908718D418FB17EE366A38F8ABDCB4D63DDD2AD5732F26CCC4ED23BF60FDB63D91C456A7C6AE52C538679536C84775CE57D7DA0516565ECF65E40727FBDA41F2C49AE8B
	7A7CDDD77479C45EE7166B73AF0664939D8D29792CEDBF5D06ED4B7C69ACC1FAB2BA62F7A2E9EA1BF29DEA8CC75AFD4B23B32EE4B3DBD8F6D43305652ED1ED21E25F8EE6F7DB68DC50GBCAD8E3445A9F6344520834774ECEA330910CE6538FF20B892A7C7E21F982E59E136EF9989D29DE3BB53C86475DE9CF79DE30D3F4E66BA7C5499F94D2250311A897264BAF12C7ED6A4E353AEFB95C613C975DA83798D1C66E795B4BE8196412C4F5EBEC67AEC2A1350D7D5B60DC1AE849F14A17026EAAB51CE669B17F379
	6BF369FA469AB42CE7ACF47EDE8474E9DC2EBFB8B76B6D45F8BDDDCAA2AAF62876FAD0547B0E0A2BEDD959916C3540250EC11766BBD4DB320FF41E586E0AB4BF74B809FF3039D8DBD7F13CC47143BABC73D17FC2578DA49D87F999AE346F3FBA719C2F3B1F4FED7DE873D19C77B5BAAF4E00749C0E7B9D1F0F137A519E67CAA496B5955219GC5EED82338B16FB91A5766E6711FA4D61444E3323CD326FBFCB3A07C562BF5D0DD2A750CD406556198F39DF9BADB43B133EA8C63E503700498EA5713DA841EB764B1
	3743A9F41DA4C19B02EE34C307A7721873537E19813433G5818F941BD1EB1138265525DEC4C103665C846B19357EF3E8718C96BAEG7AE289E57DCFB7510D68A56143B6152E490D73CB8B50B15CA9EAEB5D10F67232D6F7283E3E55CA74455A7423915B345C5026B86019A0E9ED1A2BEB932B1F2D59C8BA11571F6256478CB13AD75D7C5BC0CE37CD8A15F341F1CD4EC952E8F22E90706E8C56BA40D370E8F296FA0CF29675231CA59E9CEBBB7AF11C56F05CC3FCCD5B6541F174C89BD9F3A4C0BA91E0A9C001B7
	B92F9E208B4067C53A27A8DD3417459A8BEAAC718CC84F773238A0C6D75A665335057041F71F7ACE6A0954768435FEA3FEE904B2DDAAAD2B7BD8C85DEBF46F793AF1B9124D671960A30D63F53AF29B04FDE7C37B7B5593DAFF7F4B413174B75AE59A5127D76B6F3532F12C7D4992EE03AB4E228D96493846751D54E418F6F6BCE32394F8544B1A8C6DE1B29C5A9B595F1C276B7BEEA86314F1BCA4EE8F554FBD27B459F61522ECF1F2F8BF633C0432A5G4DDC1DEC8B427563882DAB6934D6D727936BCAB129CB5F
	8FE6EB0574303A7EF2FD6C737F731F6A677F027B25AC64E514A3D315AD57272D4559BD77775A69F703006F7261BD42BBCDBB61F94E5A2F5EE69F35399459CF2F3381596E17FD22DF394C3074EB8E3C1B8CCE6CDB5F5689163BF95C65589AE573E53296897AB45DB3FCA945C76B70EC6D72845FDFCD053CE5BE0C91F8658CB94B6A9AC84ED3GE281E6G4C87C89DC0DD76B6D588234487D8E4DF1BF8C914F457D42CFD53AFCF6C9B686F2F42FD4C6C6FBEAA4C392DA9E20C7E3281E68F05FEF52F7652FD4B491335
	6F66CE523EA6486B8108861886B08BA0510F6DFB34290C350F1843E1C53176B96957885838F3AEF1C8C31303C0E6677B4570F345F6914F86D37C6CDCF199BCF3836A9E0B0E37C76CB85ECA8266E3FB1DEE3C55GCDCBC09B5B5D0130353915680CFF8B4071098E42078510EAB100EC7CEFBCABF03A7962A6A1627EEECFE5D9C9551E9DD82E6602D66E510AAD46F24D30E1A95DE2F3D9A55931252C844AD6D66DE05F98123860DCC17B47785D7B29407B7C57EB314F0B9FFBC8E8FA31A96B0388FD6ED475B6A72079
	022D83A56A5EF565C5CDC607E333D1C62DED159530F75DD331C335156FDCA4BEB2BD00F6F16198E9DFA49FC2E290BB953F4B6B40336A1F72FDEE0E02F33F1D6EA5AC103681A89C6F27F892FB9F8D38840E4F20FBD1C2CB5A25A8933B1F37C4A8476AF85D59A5E8737EE8EB72C89069DE1F0D03ADBCE6A9C9E163A08D1E998A7B5E001D23CF6AC71A0241643C2DGCDGB60068C11CA31F246B62CB2E000BFCD7476652A43E361CB1086B62B7A5444D66E5976FA13C0F37D9FBC589BFA361F3549E327E51229EDB3D
	BE98446A779CFBD7EB6329D3D430CC43C7E4BC7168BC720504005F977EC74877AC7387D9BB178C623A639D2BE613077AC73049EAE213E44031F2753DDA396F38C79827FBC931FD26E3AD756348636FE21F76FE33CD2B2BE0243A2A7628BEC1BD5BF093DB32DB250048F9BDACEA3CBE9E09D7754464CE34EB7CA7C97A7A99EEB55BEB7887C5B39E11797E33C3633B3BDF4F1759F58737594CC1E65705171273CAAF3198EA665F3FBB2EBDD7831435DCE27E1D241D170C7E7D591E57399D43948C8B4C007A9B8CAC7F
	059EA213BEF2702C37CF2593ED0CA63D67AD83CDA327C0BE19C7B79BE8CCBE26D0E957D87339CC6C6BA66288D95ED6AB9DBFD0C12F8C63249279DFD82F5DF3D52963660FED46F1C37428DC426FADECB74C72E3F8FEEF48F73D5354FE188F4F71757BFA17566F45E7A7E6BB157D5A3B452D71FBE47B08F3D0AD9BGEA9CE7A95E2BAF9AF79B06A4724E78C4BB11CF41907A207BBDA41F4DF547087928775A856D5B0488739C2CFE0E034CEC61EE31BAF8199F3493FD73F8823604D673FE520E5F3D51EF0F285FE5B3
	A51BFF3611A3EFF5AEAA573DBF9D9C6FB96C78575B8B3E50BAD87BB34EC40EDBF86D0CF95C420A8E739830B59DA34722ED68089C0B76EE077EB78954EFDEE452B7ADE093B6009800E9G3306F04D1350C156BC64D7EA425741C7ED8E92B7C1FE19C6417AA7F7C6EE6B5D1D237F1E4DD7BB092F6815415BF888FF44BF50C97ACB11FD9449F00FF4929D79DD8E2742C8D57C4CCEFA76A75AA91A6DF702A26EF71220EDB9C10C191AA76239D0FD906F097EC4E995C847824C6034AF5AF18C4D8D1A0D45749166275F79
	34317642G198F16810F024B49F9B5GEDGBDGC100D80069G338112G52GB2G56DEC94EAB8128GE882683A02FD1472690EC86395106A65986DA83F55C53ACA5553539749FEA5202ED959DC759B3E97112E186FC57473D5945044DD51662A04AB46396A7C9972BC565A2C261D554417DADA1B1BD97AADFA67F83C7D68B19667565CEF0CEDA7312B3301EFA2G2319A4195124C27E32AB789D64F19776775A2B202F2B5807F73B303F072FE2FF4F233433A11D841046E9E77118264C2BF81F37245FF81F7712
	203F0FFB57ED3C4FDB9E125F66B65E67FDB364BE6F3A5BF81F0765193EDEF263DC4452E5FCDD237E9600BD42EF81501919352B4603F102B543282FAB7D28AF89F0B306D1877F484FCDD306D1DF69143670DAF2DE99C05335710E05A7083BD267252DA7B1CE3E7B9A5679559BA8DF5CB53C77F5C8113E0DD1D795AA3F7B6CD8B6699A0FB7AB551FB964DCCF4EAB3ABE163B0D5BC362BF4A20CC43A8655418DFF90418DFF9F49FA84974B79040F7D9AE7B54B83281190CD559EE8F085C57DE128BB82EB550AE87DCE4
	E6E532DF8F0B8B9A5BFC98BB41F2BF9CF7B9F6BE55CBFFDF4DF25DF8EE6526781E6B46F32BE55C0FCB103760BA76F1CA481DDF4D8D58875E98CBDF7DFEA0F2DF258F0C5ED7FF9DB01F2FDE9B881F2FF27C667355EA3FFE3EE238FCBF31FB87786EAECED748F11FF05CBB14A7411DF75994A747EF762FD671ADA22990715B68B95C9045F529380FF438CFD45CEEBF31B7E832D376E3599ABF3D03F6C9129A43CAE9DCDEB0BF2E0F03D4C6174F2A137B8847913959B85D403F25683EB1D67F117E319A17C226B0154F
	75FE5C67475DC07F74A27D3D04A4C82781E45E603ECC42EFD88B2310B60EFEFF5E86E99B40F0945AD3FB587714E39B871FCF305C3D63AE4746C98D7545F1D14671F3106227C799474F0F783CB893728AEE4276BE9992D325GBE66263178487D7C7B3AE9BA7AD96A7BFCF8AEB270E1652242E238462627BBA7D86E4763AE4774E823363474A623FEBD941FF113D13FFF6173E84E4D49F94E1BD13FBBCEE27DC9B7EB7AC8D55F175D9C2EEB753C7C860F153FC597AB2F3ED740336996B37DFE0D7B432EDB42F9CB00
	0B820853652931EDF43F19F08BEB177A3D91454D8B41CDF70653BD6C5453295F6C52F399753BB25D7B5FCCFF57703F8BD89D9B6CDFE49D2C4D2F5A437510E18F57C3D1C83CF38A64251B74CBF89BEE9951EEF322A345321C20B125C551C63B35D07C56E8235DAEF6235DD6C0DEDCB45A6D0F433E8F4ED06BDCFBAB6CC7GEAGDAEEB54640667A893F6EDB0DB130EFD2FCD007E7755FBF00B130F11017F1AB56DF572F59CC236EBDA96C9B337FFE7BF9798B2D0359CAD2BF5A8F5987B05CAF7A51FEDE62F46D9E0C
	496D3E8DE3956E97B1BE5847F12F09BC8E15632EBAF8CC51ED2857FE37264B006EBD337F0B576B1B6EAFDE2F8DF4DF118A6D4D64FA3851C163EC3E84F67BA54425F5203E4AB86EE93A7F9806F4BC472D27BA1C8969399C97F0E059C40E5BCC75DF94837B749844BDC97DC19324FBB8EE8D6FA705638AA83F7910CE61B845C63FF700F46E6D389732FE287BAD9F3A6CE3786260BA8D0F4BBED59F3B9D61FE2A46916E576F67F1EAC537232D1C11B47B3820FBFF698B6DC7559E7F87EC65D7F4DDD88F6DED3F9D757E
	19176094C847F25C8FE5544DF40ECB273F0FB49F520BB8EE7FG0D3F0634B089F5385ECF76EA05102EGE88330713C0DB4EF985253G668224703C69837A7613F2233529E2841AC2B54FB6C99F9374CCC89DE3397F9BADC608BCB1CE085D47C5D17F15897566CE467367B51708AD31F327376C235D1168E300EA00C74BE45D9D78E4E33C578C871EDBB8A7637D5F79131163F1FC54DF8C83ED8C277FCAC7E80CCC7910A66F3FF70CC5DEBCA71B8DBC93F57226CCB61E137D7CC6E459FEC77D5192A8330C4B364D90
	3F736D20A65BA447E8326569E4AB0905B9BCD6132DA556A85BDE51BC46729475E196206F09C539126C115B71B2750331C09B9F0BF7E3DB4451E4DD2313F5BE14DB2413F5E9182C5B9D66B1169FD3FB48G7A1CBBD05649B662C7B1C60CFC37473E03F848C6F4DBF1875ECBBF414FA46B81578E6083188AB00B737AD7A9BC76E32D874BAD38035D8DAE01E7669D6CFE1F15D33C747C405A67F67865C12F8DF595CB7DCF611DB0D75D09F22448482F8DF0BDGD1G5381665D097CD68D04470DEE9A40BDF2AA27EB9F
	883FC7F7F03AF4CE3761903D63262707E7862D12CB394CF5FD54C1E4B95EA939EC221FE7323C9E7AED85AB57A68F71B2E7BED84D71ADB0C3715F6375075D6B010E04A9F82F77C44839EB619416DF4F73DF6EB7AB5F4E73D36DE67936A9680B12693D9313F14A0871AF116F27C7BA737C57AB9A7FE79D617763C543DA3E52B1167B41715D859E38263DFFFEDD2BABFB44F8181AFD9329EB256EFD47F22D2E23763154454A35E5E96584F124F255EAFC44787AC34E565E6F4E516AF259C62A2BEAAF2B0B51BEC363A3
	543BA1FED7901C02F78579EE63DD41E3027E2C7F70A8F78501303B82C748DD4143916F8A581E2A4603FB3AD9A05312A9F88EB66C613150D3719C6CD1CA5B85E99FC04CD49C9799ABB45DFCD78E53CD23AC497E522A0A1AB295705EBA4B077A18BE9575D167B36A63C6C8FBA45F487A781AEF643B13853E116FCE768C045E1DDC67E9A673EDFE3CBBD8B495575A457BA37C069E73FA3C7F17507EA7B2975C05FAAEF6635D0642F1EFBB90B773AEFEF69D3247CF83FC46DDE3B99B289B75372D4C4B35CC305CCD63AE
	47767C1B686F2F2E3D5BF896311D620B6EB61EC54C62F63B9572E26EC6BD2D8A799D242500976E990B1EA67B4D64AED44B1642339E20DD470B157BA35D8B5BCC6A882A38D8F8CE57654FB650A62A6994F8265FE3F6CE77FE58EF53914809E334C56A33AC4EA8DB316977738DAA1DC5FDBA630C67FB2F72738687BFEF4860678D0FF17EDB3D82FF5F25FBFF4A73456F791E7FC26BE0ED5E648BBFF7794007FA3841F18FD21FB08CFA0C0D437D5D2AG6E8363B5DCBF6E8317F05C890A4B04F471179117CFFDC28524
	DBB86E029B771A5D9C57CA771ABEC847F05CBD94B78D5233B8AE056266C3BA15633653F6AC03F46ED7906773214C459CB7054BACF1DC2D9B5B9B45F17771EFE2E2B96E96BB36AD05632A9DD8B61D63627899C9A6476D9031EC5B3D384EFC469F3A1F59C8CCC15D4FEC8A0CE72F5583BC15FB356F91E26EB56E8DAB34BBA3DEFFEFBF7D1DE2596F3AA2FB952BF4D8F2B93CA45AC5ADB34C6F41E770B29F3A695F5CA0B195F4BD9BD0E98F716F34965E0B712837D05C92C8AF6365FB6455D85E626AF387D03E200C72
	1544A36D23327E77EA995D77A99DD91FD7F03AD7691D30475AAF7B894FCE1F358F66ABC63F5607FC9BB87DA15FAA016F6DD43EE7A99D51DFFBBC6E533E649BCF8CE38F14D36235FDDACC3C318F66E8FD900FEB0DC339DA1F4F9AB5E6D27F9D4A8C60B187G634313426A1B49E3F796717AE8183CDA5FE3236E197575655C171CD7F49F0B3FA1F9E577996B6B90F1ADD1FB5FF863762209D928EB16E37D21EB16FB7A5F07277E2755BB497F980695A8EFBCFA4AF2340317BFEB920B7B07B59A7FC3E3DA53297BA2B9
	D416C5B2E96FDE4E6B839FC9D7723464A30B784A0A716E15562D5364FB3EFFE23117956BB59EBF207B0E91F456F63459B24EBD8E8DF343BD03D8B2B27F0E56E3AD967677A66FDF918783EE669F0595813F70C6789E63701D7F7B4519971B8A33042CD6E125139E62368AAB0648A9EF508796F61EA8AFB0A050BD5CF272C3BF690A10F57A04A2E413E0C0A12B32C29172A5D97615383CF6F298ACCA1795E135965E2B88EB7571400A7096896BAD41385E92B530B72088AB5937A2D89B8DDF2B86395705FC5F714387
	767CB669D7678259424AD6619D7209FA2768E73FB2EFA33FEC267E0938E6A14FA7032BA307B22DC2CE00BED684144B1258A6945ABB481F5389A8220D7F1EA88870A67DC1735D640B315DAD5600D3A1479A875A049CAF7975D1618D16ADD93D0E5DAC2074G5111307CF0831517AEA3C15E8D5AF7E71C4B06E773373DFFBCEDCE361055AC2C16E42B2D595A27487ED6A1CFF102E61C32E48B7ABEE60CF207B0F2A08758ED644DA70DADA75F8CF0215D413F0BDD37E4DDB3D8FE50AF6C2614E4D6AB679436C627157C
	A984515F2A10BF4EA77B37723C1BB6A99F7E6C585B5DD942F23511C4756B03BE688B3B5563A2BD3912FDCDC3BB658DC832388D90AB1FD6C614D15E8C6DD17E4F9F1FFB701F6A53178233E6E1851DBAC024820D6A542027D7745FB6734B6F2D09CB1DCCA4761166A267BC854CA1C0FED99E1817985A0CD9DC7472FB1E2B7872B77FF6010A2E95D5044D2C132068EE07A8FBF69F3603D50A1C81749830FF0F33A7B465861AB702F73DF97C0557FA41421A04ECCFC5857D3F127EDFC57E2F2418CA0A292C02EECB97C6
	7CE74C8FE21B29D95244BB440602646D2DA0D7AE913015463B24DE2A2E4BFE69216CACA18746A90B7F812BA6C1BCE055FEB6EC339559C78DB2C78FFCB811DFD53369875772B21E2251EEB4752120FD6B5CCE1EA143711D10921BC2CA2C4612E1FCC231020ED698810B35C826B4799C9B5E5A485810FF66340A705EE12F4BC34346CBEC6AF7BBA5258131CF765A82BB6A942128131E611674E99FC314F8E41B88D99B49AF72FBEDA5AE72574682A53D17CBBC78F5C2DDC8232AC2C4C2A52D9E216166B4770C19863B
	706691BA8DE92E03892E8F1D2117BF7C76AD85D32A8733045CE6629DA41F535A2A0E73B6E1CDAF35C1518F340AD0200DC1DA96DCE8D69F78570D9A3E0F60D9AED9B492AF1BE7337A5D1483912579D3A14F6EF774528AE01E799490AB6DB4C532DB5A04550A13440393F72788EFEB2C5B498AD74DE08EDAF8ADA9A949C77FA942BA7A1760CA865881B24CE312BC84A35189721B1AC9B6459E8DE593C2094BB44D20B8A68C0B746AB707BBEE683FBF4E3835AF62EF974DFAC00DF1FA40787D71CFE80C3170A06C359E
	44986DF7DD3B04485FD4F4FA2C12843B8E77E12F0DFCDFF16432A2EAB17D4F3B887FG522819AC6F177477C27BF5E57EAFD0CB878897EED1275FA7GGB4FBGGD0CB818294G94G88G88GCAFBB0B697EED1275FA7GGB4FBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG99A8GGGG
**end of data**/
}
}
