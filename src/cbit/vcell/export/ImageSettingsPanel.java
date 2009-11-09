package cbit.vcell.export;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.simdata.gui.*;
import cbit.vcell.export.server.*;
import cbit.image.*;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.Dimension;
import javax.swing.JCheckBox;
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
	private org.vcell.util.gui.ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	private JCheckBox ivjJCheckBoxHideMembraneOutline = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private JPanel panel;
	private JLabel lblImageScale;
	private JComboBox imageScaleComboBox;
	private JLabel lblMembrScale;
	private JComboBox membrScaleComboBox;
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
private static final String MESH_MODE_TEXT = "from 'View Data' zoom";
public ImageSpecs getImageSpecs() {
	int imageScale = 0;
	int scaleMode = ImagePaneModel.MESH_MODE;
	if(!getImageScaleComboBox().getSelectedItem().equals(MESH_MODE_TEXT)){
		imageScale = Integer.valueOf(getImageScaleComboBox().getSelectedItem().toString());
		scaleMode = ImagePaneModel.NORMAL_MODE;
	}
	return new ImageSpecs(
		getDisplayPreferences(),
		getImageFormat(),
		getCompression(),
		getJComboBox1().getSelectedIndex(),
		getSelectedDuration(),
		getLoopingMode(),
		getJCheckBoxHideMembraneOutline().isSelected(),
		imageScale,
		Integer.valueOf(getMembrScaleComboBox().getSelectedItem().toString()),
		scaleMode);
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
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0};
			gridBagLayout.columnWeights = new double[]{1.0};
			ivjJPanel1.setLayout(gridBagLayout);

			java.awt.GridBagConstraints constraintsJLabelCompression = new java.awt.GridBagConstraints();
			constraintsJLabelCompression.gridx = 0; constraintsJLabelCompression.gridy = 0;
			constraintsJLabelCompression.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelCompression.insets = new Insets(10, 5, 5, 0);
			getJPanel1().add(getJLabelCompression(), constraintsJLabelCompression);

			java.awt.GridBagConstraints constraintsJRadioButtonUncompressed = new java.awt.GridBagConstraints();
			constraintsJRadioButtonUncompressed.gridx = 0; constraintsJRadioButtonUncompressed.gridy = 1;
			constraintsJRadioButtonUncompressed.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJRadioButtonUncompressed.insets = new Insets(0, 10, 5, 0);
			getJPanel1().add(getJRadioButtonUncompressed(), constraintsJRadioButtonUncompressed);

			java.awt.GridBagConstraints constraintsJRadioButtonCompressed = new java.awt.GridBagConstraints();
			constraintsJRadioButtonCompressed.gridx = 0; constraintsJRadioButtonCompressed.gridy = 2;
			constraintsJRadioButtonCompressed.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJRadioButtonCompressed.insets = new Insets(0, 10, 5, 0);
			getJPanel1().add(getJRadioButtonCompressed(), constraintsJRadioButtonCompressed);

			java.awt.GridBagConstraints constraintsJRadioButtonHideMembraneOutline = new java.awt.GridBagConstraints();
			constraintsJRadioButtonHideMembraneOutline.gridx = 0; constraintsJRadioButtonHideMembraneOutline.gridy = 3;
			constraintsJRadioButtonHideMembraneOutline.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJRadioButtonHideMembraneOutline.insets = new Insets(15, 10, 5, 0);
			getJPanel1().add(getJCheckBoxHideMembraneOutline(), constraintsJRadioButtonHideMembraneOutline);

			java.awt.GridBagConstraints constraintsJLabelMirroring = new java.awt.GridBagConstraints();
			constraintsJLabelMirroring.gridx = 0; constraintsJLabelMirroring.gridy = 4;
			constraintsJLabelMirroring.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelMirroring.insets = new Insets(10, 5, 5, 0);
			getJPanel1().add(getJLabelMirroring(), constraintsJLabelMirroring);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.ipady = 10;
			gbc.insets = new Insets(0, 0, 5, 0);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = 7;

			java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
			constraintsJComboBox1.gridx = 0; constraintsJComboBox1.gridy = 6;
			constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBox1.weightx = 1.0;
			constraintsJComboBox1.insets = new Insets(5, 10, 5, 0);
			getJPanel1().add(getJComboBox1(), constraintsJComboBox1);
			ivjJPanel1.add(getPanel(), gbc);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 8;
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
private JCheckBox getJCheckBoxHideMembraneOutline() {
	if (ivjJCheckBoxHideMembraneOutline == null) {
		try {
			ivjJCheckBoxHideMembraneOutline = new JCheckBox();
			ivjJCheckBoxHideMembraneOutline.setName("JRadioButtonHideMembraneOutline");
			ivjJCheckBoxHideMembraneOutline.setPreferredSize(new java.awt.Dimension(108, 27));
			ivjJCheckBoxHideMembraneOutline.setText("Hide Membrane Outlines");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxHideMembraneOutline;
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
		setPreferredSize(new Dimension(240, 430));
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
		
		getImageScaleComboBox().setModel(new DefaultComboBoxModel(new String[] {MESH_MODE_TEXT,"1","2","3","4","5","6","7","8","9","10"}));
		getMembrScaleComboBox().setModel(new DefaultComboBoxModel(new String[] {"1","2","3","4","5","6","7","8","9","10"}));
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
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[]{0, 0};
			gridBagLayout.rowHeights = new int[]{0, 0};
			gridBagLayout.columnWeights = new double[]{0.0, 1.0};
			gridBagLayout.rowWeights = new double[]{0.0, 0.0};
			panel.setLayout(gridBagLayout);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.EAST;
			gbc.gridx = 0;
			gbc.gridy = 0;
			panel.add(getLblImageScale(), gbc);
			GridBagConstraints gbc_1 = new GridBagConstraints();
			gbc_1.insets = new Insets(4,4,4,4);
			gbc_1.fill = GridBagConstraints.HORIZONTAL;
			gbc_1.gridx = 1;
			gbc_1.gridy = 0;
			panel.add(getImageScaleComboBox(), gbc_1);
			GridBagConstraints gbc_2 = new GridBagConstraints();
			gbc_2.anchor = GridBagConstraints.EAST;
			gbc_2.insets = new Insets(4,4,4,4);
			gbc_2.gridx = 0;
			gbc_2.gridy = 1;
			panel.add(getLabel_1(), gbc_2);
			GridBagConstraints gbc_3 = new GridBagConstraints();
			gbc_3.insets = new Insets(4,4,4,4);
			gbc_3.fill = GridBagConstraints.HORIZONTAL;
			gbc_3.gridx = 1;
			gbc_3.gridy = 1;
			panel.add(getMembrScaleComboBox(), gbc_3);
		}
		return panel;
	}
	private JLabel getLblImageScale() {
		if (lblImageScale == null) {
			lblImageScale = new JLabel("Image Scale");
		}
		return lblImageScale;
	}
	private JComboBox getImageScaleComboBox() {
		if (imageScaleComboBox == null) {
			imageScaleComboBox = new JComboBox();
		}
		return imageScaleComboBox;
	}
	private JLabel getLabel_1() {
		if (lblMembrScale == null) {
			lblMembrScale = new JLabel("<html>Membrane<br> Thickness</html>");
		}
		return lblMembrScale;
	}
	private JComboBox getMembrScaleComboBox() {
		if (membrScaleComboBox == null) {
			membrScaleComboBox = new JComboBox();
		}
		return membrScaleComboBox;
	}
}
