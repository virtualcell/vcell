package cbit.vcell.export.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.simdata.DisplayPreferences;
import cbit.vcell.export.ExportConstants;
import cbit.vcell.export.ImageSpecs;
import cbit.vcell.export.server.*;
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
	protected transient cbit.vcell.export.gui.ImageSettingsPanelListener fieldImageSettingsPanelListenerEventMulticaster = null;
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
	private DisplayPreferences[] fieldDisplayPreferences = null;
	private org.vcell.util.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
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
public void addImageSettingsPanelListener(cbit.vcell.export.gui.ImageSettingsPanelListener newListener) {
	fieldImageSettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.ImageSettingsPanelListenerEventMulticaster.add(fieldImageSettingsPanelListenerEventMulticaster, newListener);
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
private org.vcell.util.gui.ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new org.vcell.util.gui.ButtonGroupCivilized();
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
public DisplayPreferences[] getDisplayPreferences() {
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
public void removeImageSettingsPanelListener(cbit.vcell.export.gui.ImageSettingsPanelListener newListener) {
	fieldImageSettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.ImageSettingsPanelListenerEventMulticaster.remove(fieldImageSettingsPanelListenerEventMulticaster, newListener);
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
public void setDisplayPreferences(DisplayPreferences[] displayPreferences) {
	DisplayPreferences[] oldValue = fieldDisplayPreferences;
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
	D0CB838494G88G88G510171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DD8D5D556301996CDD4D6D8D8D418B643949695A599959596A64DD0E1DAD15164BBE37350BB4CBB3EB34E0CB39D409F94CDD3B227A82928408881B149287CC1C245A4E5480A721EFB6F39779E38FF5DFB90B04BF76D3D4F5A675CF34F656F7D7A3E0F67D91C3357DEFB6F3557DEFB6D3F354F95CAB745AFADA8AC95040225027A373DC090D6FDAF887F782D77FA44CD70F616883A3F5781DE9026AC
	D9824FF808CBDF50D932D6902D4590CE06F045D31DA5AF417BFA41E1358760A5C2B9CD909778CBD96660657C76EDACE7B98DDF764DAAF8BE8F1082B872DC62A3655F744DC6951F2362918A8A81C7E9323EA9D3F12D9036G4482A47A98FDBABCD75012476FE8D4693A4ECF90965E97A8DCCD72516509D061E7E9DB55E739703702FCBFA96B2F14CF12E68C04D3GC8650B04165E4D70ECEA6FF1FEEC334ACA4DF11B64F657C87DFEDFC05931E31FC7F4CAC7A4C5113D4EE03B68155C02607E0E64376B5BE21ADE90
	9AA03C0862ECA2A9A3895EB3GF2B97E2815601B821DA59DG6EG566B23F71AD43E0FFEBFDD186F1C39E2CD04FA0D8B307AA48634FA35EF36967D0B36415B15C4568BA0EED53033249C2099408EB08E60955A766F3DC36A394B2A6C71F9BCBE6FAE3FFF375B9EB86833CBEEF85F3183E2D45CE149668B588501356FF35FD41B70B39830F42B3F9DE9BAA613042D585609072F95F2EE3C28F03909CCF2923EAF0C8BB2D9A487B5193032DBA2161D9D8C554550323F9074E58B4F54AFDFEAD2F65943754B972879AC
	5615DD2B10361ECBF5208C5EC9BA121E61FFC3710A8E4F74D226F8920E0238BC85F563687348CB5B12AB0535F5971B4AA1762E684276DE46032B5748CB130D649D534B4A5C44F96920789C9D1E7172AD55D3922EB88EFA5457D94274EB4BE652CF4B7ABBCB9A815A81DCGD1GD381D27AD1470E3F33E518BA56AE86145D3D325BAE858416F6CB6586F8CA7DCAC05C678DAA2257A6F50A81D9343A25202052B4D69299D8C352FDC4F55F86E5FCAAF925002848BEAF64AEFAA4C58A8499CDCB658B04A6A051588E59
	A3C1819E3F0079FED6399E1EEEB1287C5BEF9795C923E0711D1524CD0E0B1D10A3E4G6FCCAEDD6F92F94D037A67838883A80787E8FE8724G580B9DBB5AFC3E1EDEFF87290950BEC0647C8D359D2570DEBD00B2BEDACDF0B190CE647994D72D536553AC5B887BE2E0G7B5863A2C9138674398328B77FF9947526F975B421FA4EC4D33D19GBA3C648453173293467E330066CBF8EBBE01F65EA79EB569B724ECFDB8F7403CFF8D4FE6C41916D1E488AF9DC57E4F73B63B67BD52E6F6285798007893A86BBDD565
	CCF7F70BC1893575D0G94CD32EB12E7FC6E36913BCD64CD86C8B2962D21F8128F1375D235CE04779781DED24BA7E53D4A6B7B954DA785F84885A87E3E33249660A84098008400AC00C5G65A7BBCBDA817CG31GE9G8BCEE25B582D79EA39C53A32A371E7464FE864797F9AD85D27503A2FD16B05F50CA487E33DB77DFFD00F51G53598FB613FACB47A52F52AC79A52F9DDE02EA5C4E4D245E2ABDF578F81A960A77037D5D8386AF4871ED1B096CC4C5894856DE054FF9F8FC87ADAB58EB75EB269B4B7AFAB3
	11352F8F6CAF2737ECA6364B6673FAA55AF9F892B55ECAED276416BC40749EDF2FD7E1F8573BE4FE59A38D28365B7D6EB20158DEF7AF9987581CADC9A46D698F489E6812E4A891ACBF903B2AFC3BD1A7233500A3F34F86F8367E0076316FCD340F95A7FEA9249D73971A0D7131D71DD718708333E529BFE876117174CD35B96FB6EA43D5FC1071DAE8A1F2B01BC79651B4F787095E6E6ED5941F7753002F573FC7BEAE3B6593123DCEC01B75DC8D9D2F010FFC00629F51E6BDD9CD62F1FCD00D559E1F5B4DC48EF1
	6DBF9239D4DBCBF5F5AB7F1149C553234C77561236153E6B15E058A5835581AE7F075EDBE7086BD45B0245BF769EE98B37EE0C731FA2E5CE32615CBE964ACB8248D24BDD8B71DDG0CEE3E9D69D2A11CFDCA23DBF80A714868E2A524DBBB2633249220F50C99DD178369225478F87519B2C6CF374109F4735478FC75D9FC1A1EAE0B53B5G3E6DB40DBF37CA17F99A219B67C23AF127E9651271B3AE042ED1C63A0DE34D692246923ABCCED7BB160D832D70BC8A508FD04369E6F4A3DD76D8B6E66441F3310ECE65
	2F87694ACED77B85BC5BG5C276B4B7D379B69461C4E461CD815BE71F4169F134B8B9E244BD473192F52D50C5367B70753350DE3E317FD1CD62E165FD583C827C0FCB4C062B816DF9EBC1B4663FA6DCFD5240F5375DA37F8DC6C2F8976C11F5A31E3BF6DD5ECED76841DDF117E5EBE9E57E06581520722A09C8F10B29E6D41832BF18D965313A064BCD337BC525AB2EBBC2B5742711ABDF0656FDA467A683EDAD2E643995016E7E07F6C2C514F83F78503BE1BCC2720EA5FD93AC5EFAF98EE65161570F448012032
	8F8CFABF763335DB883FE40E2A22D5DB7873B75F94746BD7E72F3CE30719B9D157B8BF34686B1B3A660742128875BDFA862BE77F99DAFD633D9D4B59DA61AE3A06A0B6AC85627381182DD9E4515B1A4AB3818F20GB0FBD7E52F0727C88DD48DFB34CB8107AF60116C1524C5EB44BE25060EE9B53BE874DE72DE0575FD7740C7CCBE52FED64BC3879859B1E3E6EDB437DEC5F65750AC8ED93B41AEFED855A9609A5EE547B5FC5419288FD72E5F09FAFD6D15C25329EF8B4D5664D1EBDC05E9E7B239ACB8D313CF7F
	855622E8GA62355922E6D9A22C05F23D0C6858E230C26C2DCC6946A78525D4FD31B2BB4773285CDA5E11A65F9A74FB38768CB4F42BC6BC4FD1EEDE731BC1533D06E150E514A1D15BB5E41572910E79A4FB70F62E52F2C48A251334AAAD607071D25AA7E209C887882FBDCBE59A68521A6AC3FCF1C185FA2482B76E7185F4FA91E6D3F34GF72242727BF7B7192B75527599439F71CBB659A15BF0D8A5C2D277FAF85EAE48B77EE7A8239C3BDEC673FE46E4A41C0DF2FF39EDA513BBAC597CBA19CFF3212E1782ED
	9B272F753EC1E4EAD7F5CD35C5B53B2CC118F55994261FD46118EECDFBCD2FDB4562F8D35D0AFE46D9B86EECF53F67ECCD37F2C12F96F25D92E564E9A150D4C7C39F2478973B91EF87DCDCB44A75E4B719D70981E79D134583AE3DAC3201CEB887EC77B9C8BF1552ABAEB948F8BF79A572BEA6B3D958387E9CD35E89B41E43F8361C23711E98EFADCA0BC75E1F66BCA6814D82DE667E1ED09EAF934CF8ACBF976BBA2D8773E9G1C7BDC4C2754ED54C9165F39F6337C2672F49F3B0BE97B137913E4E7E57C6866F6
	0E14FBAE36FD694764C96725824E657E62A6765EA55B250312479A008169D02F62163D122AC7BBBC28171567F516D84E43F2A7FB7EF7B6710AB7DFE6FA5B9ADEF465D71A9EAF99403A44C259D93C7C1E8114BB237B55G72D9BC2133E467846CBF5926363DFF8266336D447F2E9EFF285AA5045876BDE49B48BDEA7B2E1CC07B9EBD81574ACF733D417489CCBF67CD5076AC7260DDB8BFF48F036D19455079C3697946FD3DABA83E72FC633E5E5B8114E19344451F0F7BBC9975E47E32648250E300C600F6GBF40
	048BF05E903BE52BB044BE5ABE2F82538A51FDA4E4C1067BD935244E3006D24307EB753A4A78F83F0134E5A2141981108B3088E063054843A335C37230375F1487A6678C5BD07B3F0D97064A1925FBF848F4DD06F4CCF7569CA175B21997B2CDA276082E917BAC46B5A231818A6499854074E68B5D7FA27322788BD1EFBCF642573C8B073F3F45649C3D0D4CEB73A0DDF18C34B9C093008560A4C0FC8C4A3B326E23A165CD343C433787F02D305E5AE3DCDBF3992E170CB2CC0B89655BDC762F07252BB104F38C79
	B019AE920DB2DD88F49BA74238B491E55A68A0B2A5F36B6609A853FBC749671CA179CC18E826A3EFD516196BC807542F3448125B2E4E8BC5DCBF24F1DE4FF60C0E57C9E1690C3CAE1AE816EE2FF3F4659D89CBE7ACEF6DC5E6691E9BE5F92BDDC315D7F911D9DBFC5C790AF9DB6CF1C9361E5D3EFE55062F1730ADF6DE04EDF1E3D819C6B067754EA153759BF8E5FD38E8BB511B89901788108E108B3064E2684FG4D97E39F9E287BD098DE9FE6C3DAF88FE6FCBA6551552FF748F4760B4D523D52BD3A722A06CC
	F772E233365F307BB57336BFAC5AE59F9AECE6DB66860C36051CCB44C03E899723EDA9160B553C53AE46F17674C652E60BA0DC9A8BE9GDA815CGD1G093158E6336BF68F55E67CB8895A8D77C7CD0CAF1349B3231465DF06CC179E9B1A0E55BDDBA8D6734809657D23E7147DE348F425134CF818E9C79E2AA7A18F635C23636102A15359A719255B69991D7D7AACACDD0DD107CD4BDBB9B03A7AED9CB2DD0CE9F959E16906D73F5F0425B356AF5134CDCBC46CD3693CCD87CE103C969B688783F65E79B73A9F37
	76926807G8C77F50060F6C23803639A95026B0770840E7B32177A31C0B873926463921AB697428BB96EAA0AA3F3B9B277E0386A0071BC115063FB6EA5432F07992863F4D8997A73C9635965C8C1BDCF2E0FFC4EB32F5E7C1C6709FA73F31E7975AFB0BC6421BF0547783C7A414F019656C7BE877A38A1B21FFB9B4C79BC50E04E67678D037379E5C3A4BEDF25F27F1DC24ED5424E1FBE83CBAE60FA7DB95B8A15E62F625BD347F57FBA8B6A7E2ACBF17FF817052C671AA15C8520DC0ABA56A0E27F0A86DC7C252A
	CF0A7A4C00E74E2521FA4674FA9C35197990A74461D8F4513648723BEC1B397C26ECA3F36A202FB7E0130CB23AFA5BC69E278DD87B9449C34440E8EE246995D87CCBE1B21EC1533338C8696F5EB6F8BB4C5A300636E3B0A885696427D9D4C4D57E7BA53D7C495E819B733B2DA1E3FE9C9B731B60599E07E3FE2D9567052EB8EC2309F4EFB49E42A9GD9F158C69FB94C7C8A964429BEBCF166F6E06DE561788A40B582346962D8DD7E99D2172D5CBEF838FED8F4E99475BD8A1EF106F2D8FEF7B97479B5F07CC927
	9E3F0D635FF769716F7372B75235D212210CF4B5BC8F1EF926657FDE1F5F41BA2E932B37C75651526D66BA7A727648BA7A7A7621F5F4737648BADA37FDE89D5D31BD120E323ADEAE6B6BDA4F71073BCBD4FC3BA3607334572B76E1CD906D03F0B96ADE8ABD8FE904F0BB006BF2543D5526F372B117B31947DC9E2EE30C67F39AA34BB92651DC4E17B4C6167364462165FCF5E3E4B9DF5FB8341CE7B492F9D9E53B9CD0CF20199C4BFBC265D8276A66A38211CD02C18629EAB84BC4B62AEE76685BCB6BEB7759D156
	73F5E997296F2B2E08145F2D6ED07E362A78BE0F3ECFED67743B86C2699BD4FC4989BDFDA367EB2DE8268365AABF8DA6FC31B1E2825D3FEC057823D7609E6B8B47C89B798312C38A84A47B917984994F622FA0741B0F91FA973CAB1CBE90A07E93EEF140572BC03E71046EF7FCCDB59F689683D4G3EB11E59D936C63B3E0F545181AB9F68AA6428C5281ECC529ED2B0EDAC50A782E48264812C1A0CE57AFBC9DAC8A8FBFABD14C3465F474789FF8A500D613437FB080C5869ED1DAA476B84521776F708D62BE4A7
	B1F5CC0E4996239C93A7EB32CB1B9CAEC736C7FB939D3B33A79B77EE53A8FE41E4635E6D27965C3BDD8CF116ABF1AEFA004E0953A11C4BF1C7A9EEA10417CCC15CA16A87329142159C17DC0DF35B860E1BDCC4F063A09CB78565F1299D7762F326901A6BE9B8E98A364B3B45E4DCB55B43625292F09EF32595773F4B27221D7A047A1B9E05309FA0EAAA5A29EDD623EC6327EA72CC06778C009C9D0E49F085BD7F4B1F4AE4380263B7D07C2A2BB4BC136D25F40CA661CA080B3E8AE5F10C6E8F66C3D878B96252A8
	EE9504ABB82E0A629AA15C41F12FCAA8DB9747C914AE8542D99CB71E4E3966C3F891470D93B13FD53FC05C87CE02AB07F0AD47DD6E44B21AB86EEA89730B6638AD94978F61E40E6B27388C884F633847B94F73B9EEAE4F2FA1815B25B4E8EC1756842D8D3A92A269FC131D48591F40643F06635BA8BED607E76DF2BB9D3FC8B88D621AFE09BCD5503D159488E7F05CF899F94F6638FC3A8FD1F1B558112B399C8BB0ED9B47DDDE00E90FF25CD334DD92A01C4EF1FF2736EC9E0497F01C4502E997F25CBB347D1A2F
	81FD3886F151A252D92E41FE750B93217D6AB69ACE02781CEB48BD071BBF9184346F6D01F2F8FA253EF2FEE855ACFBA4AFF17E2D2CEF282DAE5F5A30350A1D0B4677696FA6B42E9CEB6ACFC66DFD22EA6F55E774F313543B89EB02F8B7418DF1D393312F57D9C2762B89A3BBF6FC9A106D3BC5E79B35682C1FFFC167A3A4FDCEA2767371B44F52E9E053811A2721FFC37F5B687F117873AB052C7FB077F7496D782130EB9A63F34CB46D9C573E59DA444A6CD5304CD408DFB80D6026A87A73C1A24B01A2365F4062
	67044413B95900200FDF5E2B0FA77C5ED2200F7FA0A41E4828CF0DE7F51B7D9656ED716CA441FD53380877C41A2ED54F2855E7792FA695244736AFE33A7070AECD970A0656055A6DD5CC6FE6D37D0C05BC932F65FB9AD654452CEB31CF5FCA694A2F033EF29D626E21B862A7C7FC1F986E2045EC5DB298107C06C3D74E4BF50C380C617ADF97F199DA4548F3166FE9DF6DGFEAC57E1DF750924EFBAFCDE05510C27B451909F4BE99C5554BF81A6412C4D7ECA36F105855751F628EE9E86DF88991006400B2A2E64
	DC47ECE38167BF25084EE744FE43FCA60B0E5F35C9E0AF13102FFB6D1B747AE2BC1E2EA49E95D52A3EFEA469EDC76D77ED05C591745515247AE2A5E93603355FAF4246EDBDA4A4C59A9FEE24730054A4D657659CFF8745E76B704CC66D25F8924E0338766BD13F5B2C380F174CF17758D06767F15C9F290DDF8C6135B7A0EE879F6FAAEEC0FDDC63A03E280D906EG7083C47138B29AB77586667FC9FCC509BFA60B1BAC8DD70FC9FE4F2BE510A716B3D89AD6460CE11731C227EB58E7168F233F2C974E95C3755A
	CDD4D90D1B6BB05743BF5039CEBA54A16B865443990E48FD6EB025DF8C3445B74278CA7BCC03B412BE53G69DAEFE4FD06542D6BC6E31FB969B0DF83445032DDC02F5C083CFEAE9159C8DE62BEECD7692E356378928FF48CB70D5A01E4882771346D4ED0F91D3717480B5569A667E0F5AAB054A98F72941235BA15A6EBF5E265BFA4A1CF6549D8FE0B247799E2F45FC87CEEA027BBCF8A657306960D4F05034A9E57A6CB55BC1BA14F23BABE5DE1FC8A8E6433BF997B5A3BC16C27519CF7955FEFCFCA46FE341B7A
	E38BB7F5161483B48338GE2G12GB2EEC23A4314AE67A666EBCC7C1209CF6FE288175D4C7C0298DD3A4D2F2B8B49875FB35477721BA277BDC1AD5F08EF0E10A6C9452B3EBEA1E517685E5788C30FE5C456ABCC70117A71EADDBA504B0250761E7329565EF172F05A9B7532094873E62D3D6DB79B7B5A7202F09DFC2DGF550FFB34EF18F6F53750D0211740DB110C70C0E0704B09E9659A35B1B97E8FFC806B4E9B7E3FFD8E78915CF6FFE0D37BE8B7216F7F3F8BB6338043C8953BBCBCA27EB3CD5CEB7727631
	4126DEFBC0ABEB0A08E5B5CC8FAFCB5F8EE6F3055630329EBAB17C717F57DF6B477F3517388BB1AFA39F9DAAEF16695ADC1C1DF3473751FB0300CF1A0E6788FF7F64DDF8F60A5D6AE976DE3B2C788274B89B106D811FDF8AA8838CCBEFF3287D6545303566D040523DB94AF4DDA3CE4766A8FF25EB1BAC1D6C993ED02177E4F4F8B6F7B91B2F2F96C0DCDB8A7A88BCF0106CE5A5C1B893E0BE40E20032DBE0DEFE8B4A32F9E72DB004FFC0074F5FA69D175C3AE3AAD63F934A686EC05F399C57B149B32E96D24EE8
	0E68235FF68B5387377ACC491CD4C01E2CFE76C324FEF19017821083309020F886587699D83F681D552CFEC49DF6A90AE8F3515B88D839CED96A5350E4A3106979EFAC617B0AFF653EF58DB3583EE29BBCADB3D4BFF95A5F8ED9313F754FB06F5B2BF57DAD9AE862E7E8FDBBF906313FB509C4E67CAEG47E7B8C9BE4C01D47381E47D7F9F6D8227BBA83EA6C4DC5FED2B2BAE2F5FD60569B6F4EA6976D43FE1CC57828B164AB76C326876B95F28AE0734F575D56C0EA1718B2667536A9F4BEF608B013779D32B30
	4D4B2E19A2B44FEDAEFCB3C21B2729F2DBFB2BE68BAC360695AC4F85DFE9BCCE37ECB27228552D2E96562E5BEA2BD4DD1978B531112D3722DEB46DA1750BE4C3088F6291A5A0FB1D38D7FDBBDF67F65D0AE37FADF44D108861D400CC0E1FC9714477BE87F0C2AA628F507B3F29EA3DB2EF9D5D79FCE204F46ADDD9DA46D29DEEB4E5C4BA57E77D20171FDFD62432FE50844F76D4F65F006D23BFEEC39A9760CF824481248264246298D9CC6745476520CC6E553131F4931FDBAECE45F9310B63D65D06E99F5FC672
	5E5FA6DAA5B7DEA3C13D78B3BDE7563C9E77F97D5009D95C1A77D7EA7D690824E01A1D1BC9FF625EF964060400778B5BC36E33145D466A3973B61CF77C3DCB5349579C036864D62213244331F42FFD2B25DBA78D52CF5FA749361B76355CCFA2773FDD4734F73BC5AB6B04FD10326A3729B601514E3493DDF2086E204473BAD354722AF18E1257565171FD35D54B7F77123EFC06D358DAA350AB196591B95F7BEDDA3EAFB97479B23D6E88224EF65C46745A8D4FB137B19FEAE65FD315114CABE3A1ED62ED4C3E13
	F05AEDC67BFE2B65852E47B0844384B328FE0301454F21E30E5EF3705FDE1BCAA75999CD3EE50301E68F2740FC966F5DE420B139CC0167FC629A4E933B5DC48CA10B2B90E97F01822CBE68A7F5187FD74FEB67DC75EA3FB94BEE6CB7C40E193761FD0BCBC43378DC9E7F6D377A7805EAFC596D1DA5A3EB77FC3B566E5B8AC627BB6B9D5A7B7BCEAD3F7CE3030EC10D2C8330BD65846EF7D3BB62BAFC69A26F2ACFA477DDF35D0EB66824CC6259D8D755CD5E59B877DF875E92A20CF3B07B598FBC3309FB076864E9
	2EEAA372667E84EC8A2D46A536613DB7FA77085ADDB6D22AFE56446DC09D0B0A956D7D7C60C877E1C7BE5F3E7AA7AD03557F5103117D961EBAE86E37703B0366BEE04F9E9C5C976D3F8FC676C5DBFCD07FCD82754ECB8FE91BF85009D400DC0005G45F7601C67E71F11B98F79CACD78BCF82F5DC97CA64817E994AC5FF3A8F2DD15C3C3FF4F6607C344D6D8FDE0EDBCA4FF441FFE0834176273D3A443C59DA2B28A48CE1742C8D57C3907082E3BA587C533754E6BA22EF7AA20EEDDF7204F14AC623ED04C9DF8CE
	F4B1254D0470FC00451CF60C957BD0E91AD9DF4C9FE4FC1A65567A5AF48F998F5AA08F97C0B440D4009400AC0079G0BGD65D59D9D2815084508EE087688708820883C83A935B6876AF2AA275D7C02A07E334218222CC1ACA1553242F49FAA5284ED959D8B5072FC5526FB4DF0B68472BF9C013F727B6D689F799472AAFBEA74FCF773528E157D6E2CBBB7635343070069FC9F8FF7B5ECF191FDB0D436C5E4CAA48379C005184DCE6B40D905FF6975E033C4C0A6DED87DC5CDD58067FE145764E3E8B5BFBBDBD67
	DDF5B7489B2069EE24AD32E3FBF75C0D67F99F5A0D67F9CF8B7A73B80F5DF81ED79092FF055DF81E774B10733C876C4673BCCC2F7A3471F98C8B1F62E175DBG0E885F8220A3B32B57198E74930C3E9B65F5126F872483EEB117C1BABFC7EDC8C7F97D9725F5C3F89CC0DC7AC87B4205444C2963D29A7713CFCE47B29F60F70D7252715C57690EF4B7C61DD52879CD645FB9281809EB28E9A1BEB2DD00774F9C4E5946A3A1BECA63A0CD6C9069D41F5F1EC1FCFEFB0636011F7550EFA0006D6A98702BFEE4C7FAC8
	DF75B99CC1095B5A23BDEBB9EEDF305D971449484AF8FF47543FEA68BAEF9BE5BA6F0853317D29925ACF93E79A772DCAA9BEE526F15F6AB2EE4753A12E729EEC6371A1E7FE51000F3FE7B8EDB55B93392D9E748C5DD60FF94C472B27BC616355EFBD6663D51EC7BFDEB15C9FBDC46F1DE03B0FF23ABFF35C179C77F71AA741FDA15B95974797F8D72A78D60994087865D4BF7AA86E280ADB2543FD2962DE7092FD03AA3BFC81CC3B41CB4F20E537DB75E125B43297540F4BE3131770A87BC59D5F959CC778E67DF4
	198DEDEB72BD43359F791F28FEA9E4192D6663F060BABF6F9E34CFF750EF9DD45C8B369C2063DE34ABD73B708E2B7BDE24FD0E5E3B1F8A61D4006CFBD11F268CFA069C399FDCBF4AF43F9FF1BA56CF4E24E3D75E3D467EB30162975DEB6CBF7F32E17FD91281730F8C2CEF31DDDFFEA660F3B306E3A32379DD4E05BA7A62D96C3D8C1E35334C6E600C9D657E7804D1261BB54AFDF5875DBFEF1EE5142F0F625BE799653B080F23DD9017B60B6F6FF179D65C274923D1FDEF3BAFDC566A5DEF4DD7DEC723286FD170
	0C3B4FCC3E1770BB7DC9A6F92783EE9EC01EAE0E6DD926537526B01B550B61E6D15C4690DC3E98CE775F221E0E7171825D17E16FAF6B5E2718FE57707F9430B2D2FF52B2D81D632C61F218EC8D17C3C9489D7E8608EB1DBD1C7E5593F6D7DA8FDDA6F9B07D5CCA3F4F621FED545BA60ABFB95B2837B31D2837D19017B79B75763A307B416D192CCCBBBC7B81228162B30DBE30C17A9D1264CC238F6C980A4F5261D979CBFB5087B68F625A6F47724F31E9BAF3116EFD136D27571F461F348C26AB1B6C28BFE49D20
	7A175AD1FF1E66F4D3380FEF727D682B50A722FFF0864765F0BF4FEC0EFB5442FD0AFE05F2F56B76D9FAF56F7F7C3F3067721272534BB5074A30916A5B71AB14C346A96AE78361FE0EFBCFC0F90D6338E7298C33A11C4FF1E3296F6D12DFF716147E9AF13FBB06E94BB9EEB5CD6B07F09447DDCF57A4F190CE6138055CEFB713634E207915E5F516B4E4F1BFEECA578AE1CB962E054437F55F722153BE06AF6BDF2D61F15A274A638DDB381DFA4796EE5747BA10AFFF966A4A87AECDBFF66B5EE7BBFFFADD197F13
	16217A6651F9E18C54B7014BFDEFB741E5C2F8BE4755F29F6FC59C77B525ABFBG66B98FA0EEAA45B5C37868837C1EB0DD2B39A19C85B095A015471DC16332A13C902074C1E87F87B12E24C7DFFF12EE28BAC58DC263D663D29F507B84D50414B11C733F21FC0448937D04D4BFB12AD79DD0AF4B03387F7C6CF7C417583E531F44214EC874BEC05110C77C033AB370870D7EDEC5FCDFAF6DC1BC7F7B53A07EB82B68F9E1B65066F27AA7CF057A482C8DEA7CB68B4361977749D6BD84B662A10D5F06070C7BE4CB2C
	11F9FB157913C21A36071037C947C2F9BBDF51F87B673121F8DB21632D9F720C5671969F46DB05E86EE359C4E51C8874A91C2F0CEFA35763A9EA876783ED7EC3F8B6D6B2E43B1768F8AD4B06FE162D715A1CED647532E366BE16B7D15B5A8E74DD5948EB833D5304BEE2645E9E3B8751C67D30223271DCFA925F130C81DC82C08640820062B97CEC59956E7B31106FD3D64EE1E703BB6159B1071D6F33F4DD5DF47FC03475B8833EDE2F9DE525D09B62865A7EB9484727B266B795F0A9G73G9681ECFC185FED6B
	8E779BBD2B9B57480D1C6E566E70F37487B9DDAB27DB7FB1BD63263B071F770AEED99960325E6BA43C6CBF62166DD200C732B84B0A95BCDD1B2F0F27797C4D159C5F8AA3941E174F6889BB5783999DFD984F754E8DB9CFF7BF4C62E3F87CDDE1676DA4BE01479F30184527BE0C36A839D760BC3A8775FF09FCBEBD581E675C63DA7EAF0B616763FF6B5362EFBBB51C7341111D853EBA203D6FBC211555B768D9E4437651143560FB6D3D64872D2CDD5F8E27AC162E66C7AD5DB3037A6CECD57DA3C6569E1F1F525E
	2969C41F0AC14B2AFF1F1545E82D96120E6DAD5D61407D2C2C07712C60AF8E63D941B502FE2F3F4AB978D94157CE63D901B3642C609CE72433823626AA13F1CDD7BC97EC4FDC5C87334B5C97FAAE6E03DDC0E913A01C8110BB977B4523851AEC5E1243E43347677685AA6BEB9B2AD540F3EB8F7F7E502239A80F2BE423BCCE0554E72EBC38BCD6F48FFEF672E17760E7A7715D21E7A74F70B063790FBDF8F6D0BB8F675AE59FC478069E33FA3C7DE75276A7BC3B66211C7F6A40330DCC0E9BEBC15C12C738BFF848
	9A3F89706D0F8CE7EF600DA13FEDE51E2EF61469649127E3EB7EE5F40F557E08F1AFE28D457B9FB16EC544F23DBD89F139DCCE0110721B9F057161516148694F265FF0F22BE94740B386A0C11717FAF71EFEA3A355240CAC95B79F1E0BF4712B9E8B25ADD7438D70ECFD4CEC1F6E1F267BBBDDAA2DDFFD0EFB4C485B19E1773E89442AF4096AB36DB1637E5EE38EDC87BA79FEC32E83570157707CD7D1FF905587DA77FE397C532F79267F24E530BA1FA5076F3B1CAB239CCEF15CEA7A7D5BEC10637C47F0FD97F0
	63BAB01F6366CB388E5C190338CA3666013042F1F994978561F80E5B6E4435E6B247DDCE572499904E65388594379042450FA32E0E72DC8661C60EBBC9F1ED9036F05CCC3EF6D5B84E6DC01E53B96EC5A756F79E479D67773C67F35CFF9E433AB5BC41F936E05AD60EFB0E6FF3F4F05CB29153CEFD826719956E50754CBF8AC97330B542D96E112C35D2A04F4CA7347B8839CF9857062B35B3A3DE7EA78E7A1DE2DFC0BE6173AA22FB17DBF6FA09370B1A66CBFEFFE8B1CFD364243F39C1FCAA68FCB628523E62C4
	5A6AA7519F55C2F1BBA15C76A4263F49359253F748361EA072F73F8B796B67344F39745F2BE5F49BE81B1279F99427EB1549DE02C76C7685C81EC77C228D46ABC6AF4A18EFAC273FD0DEAE70351D1A6F55140E48AF61C9DC27658CF9BFC76F43188269B21F5456E939CF9A5BE0225686CF62DC23FC095666678CF9CFCDFF8FE5B16431B63733847D43ABF20D653D24795165E2F9FB16EA65ED98F29D2AAF2F8B727067B27F9B92B7AE2C3CAD5C07E1C26EC87D769CE4A909EBEFC7681C456FF89D1E7ACF2B9FA17F
	518DABD833E76FE79DFB8F7374754E7035C666F2AD7F8BDD431953B15A792ED0DE5E13C9FD5F67F97553F09D8F5FA913CEDCD7BB5235529F0AB57E929D23733DDC37D24B23064E698711D95B5E160E912EF13615E8793708650367FFF85F272D9D6C77A6FFA8088169A7BEABAC8D8604F561EB0CDD67BEB2B771D8F3E921D038CFD866221B387B0425FDE417375F8F9AC66EC764873F93687A2800FC68A7DF918A8F9DD004A2628CA8945655AA429A374F67AF173D8E3299AC398794E125665E2B882B747E400A30
	0138751623DFEF396A589BD404E56C2E8816C65D5736825F2BC36EF74C1C3C6D37E0F003C54232FD423F489575A3D200FDE55ECE3EEC267EC4DC0B30426F83D3C7B6E57689CB027431B4288C3825B62154F1187C1CCED0116C7CFB2240400B740366DB480D31AD2DE2502510ED0D8F5B04A5DE7275D1E19D0BF60BDE679666D07AA11111D030EBA76517CEA3015F755A3DB31E4B7ADB573C757A7E79A9C5C2E10B30526D936DAD22CD71857689AB9497C84665F35B7B7D1F300C0A7B50F3E089E4778A2FBE29EC8D
	39B340196E693FACFA750255AD20797D81E18B25A423DA8D27306FF1096427902440BE057CB81FAFD04163CEFBCDF97B7ECFDF6AAA948A54CA9251BF5F6FD718402070B5B4CBD2651A9660DAF97459D9D7BE56141F87C9DA0425F400000257EAD41F75FA2CD2600C44092F1644641ECD7872937150C6DD87ED6C90BDB25110E56C168E47B3FF909A5CC0240091AB945012A07960BCF0D3EE9085C6719A55DCB82BF662AF7F76952D1116D491B631360324DB1C124F33E5178314D56299C0C3C2762F726C89CD0D01
	E6DD7F79AF6E1F731C95942FD9A8725456527FF574FFBD79DFC7B1F594D3D78F2D19AF8C7AE70C6F6FA7F5FE251F0B1DF0320F7A3B649E5F3A236869A9C505429268274C7F87341AB87100D687D837ADD2FCFE2A10CB74403B93792A1ADD5F398A2AF908FA3B51505B02F65719DC52DA9A529D7F951262351094ABB1E5D8BE21D8C1C7AB8C0245D25C26B4EBB8B63C361131A1FF66340A706AAE2F6C21EE6365F6755ECEF9E5D032793C76E0558EC558F8046E61165B344B90659E1FDD0228D74897793D76F2197C
	5AD8305CBAD06E415B89BBC2AAD59F42928AE965A095B72739F058B45804E387E9B4247991D4707950913A602A17CEDFFB5E565EC221380574E2375FA56ED3ADC01BD0E225BAA88500D69156EA1D0D268593DAE8837B7A0A063791BC0BA513C6E2E5D75845C08F4D0130527235304291F0DAE981B04EFC8D08E58E9AA2512DED42CA45C57C410939D30417342C5B498CD70DE086DAF8AEB3B353CF7F94E1B57DA538726F5886B20CE3EEDF9F58BB9770EF2AA6C5943BB7B41B90CA1C26E98A45B1E1D824D76F9CD6
	1D525FBFDEB45E96715BC5454F28BECE4F986F9F2F23672AC7812FBC03BE5AD66BFBC264BB95C7BC225B8D2B0E1EDDDEBB39DF31FBC011B41F7E17ECA47FA0522811AC6EB57A3D56EEDD1A7F81D0CB87881F93270C7BA7GGB4FBGGD0CB818294G94G88G88G510171B41F93270C7BA7GGB4FBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB5A8GGGG
**end of data**/
}
}
