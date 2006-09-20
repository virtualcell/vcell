package cbit.vcell.export.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.export.ExportConstants;
import cbit.vcell.export.MovieSpecs;
import cbit.vcell.export.server.*;
import cbit.vcell.simdata.DisplayPreferences;
/**
 * This type was created in VisualAge.
 */
public class MovieSettingsPanel extends javax.swing.JPanel implements ExportConstants, java.awt.event.ActionListener {
	private javax.swing.JLabel ivjJLabelDataFormat = null;
	private javax.swing.JButton ivjJButtonSet = null;
	private int selectedDuration = 10000; // milliseconds
	private javax.swing.JTextField ivjJTextFieldInput = null;
	protected transient cbit.vcell.export.gui.MovieSettingsPanelListener fieldMovieSettingsPanelListenerEventMulticaster = null;
	private javax.swing.JButton ivjJButtonOK = null;
	private javax.swing.JLabel ivjJLabelDuration = null;
	private javax.swing.JRadioButton ivjJRadioButtonCompressed = null;
	private javax.swing.JRadioButton ivjJRadioButtonUncompressed = null;
	private javax.swing.ButtonGroup ivjButtonGroup1 = null;
	private javax.swing.JComboBox ivjJComboBox1 = null;
	private javax.swing.JLabel ivjJLabelComposition = null;
	private javax.swing.JLabel ivjJLabelMirroring = null;
	private javax.swing.JRadioButton ivjJRadioButtonOverlay = null;
	private javax.swing.JRadioButton ivjJRadioButtonSeparate = null;
	private javax.swing.ButtonGroup ivjButtonGroup2 = null;
	private int fieldEncodingFormat = RAW_RGB;
	private DisplayPreferences[] fieldDisplayPreferences = null;
	private javax.swing.JRadioButton ivjJRadioButtonHideMembraneOutlines = null;
	private boolean fieldHideMembraneOutline = true;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public MovieSettingsPanel() {
	super();
	initialize();
}
/**
 * MovieSettingsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public MovieSettingsPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * MovieSettingsPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public MovieSettingsPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * MovieSettingsPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public MovieSettingsPanel(boolean isDoubleBuffered) {
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
	if (e.getSource() == getJTextFieldInput()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonSet()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonOK()) 
		connEtoC3(e);
	if (e.getSource() == getJRadioButtonHideMembraneOutlines()) 
		connEtoC5(e);
	if (e.getSource() == getCancelJButton()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener cbit.vcell.export.MovieSettingsPanelListener
 */
public void addMovieSettingsPanelListener(cbit.vcell.export.gui.MovieSettingsPanelListener newListener) {
	fieldMovieSettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.MovieSettingsPanelListenerEventMulticaster.add(fieldMovieSettingsPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JTextFieldInput.action.actionPerformed(java.awt.event.ActionEvent) --> MovieSettingsPanel.setFramesPerImageFromInput()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JButtonSet.action.actionPerformed(java.awt.event.ActionEvent) --> MovieSettingsPanel.setFramesPerImageFromInput()V)
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
 * connEtoC3:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> MovieSettingsPanel.fireJButtonOKAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (MovieSettingsPanel.initialize() --> MovieSettingsPanel.initMirrorChoices()V)
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
 * connEtoC5:  (JRadioButtonHideMembraneOutlines.action.actionPerformed(java.awt.event.ActionEvent) --> MovieSettingsPanel.setHideMembraneOutline(Z)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setHideMembraneOutline(getJRadioButtonHideMembraneOutlines().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (CancelJButton.action.actionPerformed(java.awt.event.ActionEvent) --> MovieSettingsPanel.fireJButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
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
		getButtonGroup1().add(getJRadioButtonUncompressed());
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
		getButtonGroup1().add(getJRadioButtonCompressed());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (MovieSettingsPanel.initialize() --> ButtonGroup2.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup2().add(getJRadioButtonSeparate());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (MovieSettingsPanel.initialize() --> ButtonGroup2.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup2().add(getJRadioButtonOverlay());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM5:  (MovieSettingsPanel.initialize() --> JRadioButtonHideMembraneOutlines.selected)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
	try {
		// user code begin {1}
		// user code end
		getJRadioButtonHideMembraneOutlines().setSelected(this.getHideMembraneOutline());
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
	if (fieldMovieSettingsPanelListenerEventMulticaster == null) {
		return;
	};
	fieldMovieSettingsPanelListenerEventMulticaster.JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonOKAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldMovieSettingsPanelListenerEventMulticaster == null) {
		return;
	};
	fieldMovieSettingsPanelListenerEventMulticaster.JButtonOKAction_actionPerformed(newEvent);
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
 * Return the ButtonGroup2 property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup2() {
	if (ivjButtonGroup2 == null) {
		try {
			ivjButtonGroup2 = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup2;
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
 * Gets the displayPreferences property (cbit.vcell.simdata.gui.DisplayPreferences[]) value.
 * @return The displayPreferences property value.
 * @see #setDisplayPreferences
 */
public DisplayPreferences[] getDisplayPreferences() {
	return fieldDisplayPreferences;
}
/**
 * Gets the encodingFormat property (int) value.
 * @return The encodingFormat property value.
 * @see #setEncodingFormat
 */
public int getEncodingFormat() {
	return fieldEncodingFormat;
}
/**
 * Gets the hideMembraneOutline property (boolean) value.
 * @return The hideMembraneOutline property value.
 * @see #setHideMembraneOutline
 */
public boolean getHideMembraneOutline() {
	return fieldHideMembraneOutline;
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
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSet() {
	if (ivjJButtonSet == null) {
		try {
			ivjJButtonSet = new javax.swing.JButton();
			ivjJButtonSet.setName("JButtonSet");
			ivjJButtonSet.setPreferredSize(new java.awt.Dimension(60, 27));
			ivjJButtonSet.setText("Set");
			ivjJButtonSet.setMinimumSize(new java.awt.Dimension(25, 25));
			ivjJButtonSet.setMaximumSize(new java.awt.Dimension(300, 25));
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
 * Return the JLabelComposition property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelComposition() {
	if (ivjJLabelComposition == null) {
		try {
			ivjJLabelComposition = new javax.swing.JLabel();
			ivjJLabelComposition.setName("JLabelComposition");
			ivjJLabelComposition.setPreferredSize(new java.awt.Dimension(147, 27));
			ivjJLabelComposition.setText("Select composition mode:");
			ivjJLabelComposition.setMaximumSize(new java.awt.Dimension(147, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelComposition;
}
/**
 * Return the JLabelDataFormat property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDataFormat() {
	if (ivjJLabelDataFormat == null) {
		try {
			ivjJLabelDataFormat = new javax.swing.JLabel();
			ivjJLabelDataFormat.setName("JLabelDataFormat");
			ivjJLabelDataFormat.setPreferredSize(new java.awt.Dimension(108, 27));
			ivjJLabelDataFormat.setText("Select data format:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDataFormat;
}
/**
 * Return the JLabelFramesPerImage property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDuration() {
	if (ivjJLabelDuration == null) {
		try {
			ivjJLabelDuration = new javax.swing.JLabel();
			ivjJLabelDuration.setName("JLabelDuration");
			ivjJLabelDuration.setPreferredSize(new java.awt.Dimension(50, 27));
			ivjJLabelDuration.setText("Movie duration (seconds):");
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
private javax.swing.JRadioButton getJRadioButtonCompressed() {
	if (ivjJRadioButtonCompressed == null) {
		try {
			ivjJRadioButtonCompressed = new javax.swing.JRadioButton();
			ivjJRadioButtonCompressed.setName("JRadioButtonCompressed");
			ivjJRadioButtonCompressed.setText("Compressed (many formats)");
			ivjJRadioButtonCompressed.setEnabled(false);
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
 * Return the JRadioButtonHideMembraneOutlines property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonHideMembraneOutlines() {
	if (ivjJRadioButtonHideMembraneOutlines == null) {
		try {
			ivjJRadioButtonHideMembraneOutlines = new javax.swing.JRadioButton();
			ivjJRadioButtonHideMembraneOutlines.setName("JRadioButtonHideMembraneOutlines");
			ivjJRadioButtonHideMembraneOutlines.setText("Hide Membrane Outline");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonHideMembraneOutlines;
}
/**
 * Return the JRadioButtonOverlay property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonOverlay() {
	if (ivjJRadioButtonOverlay == null) {
		try {
			ivjJRadioButtonOverlay = new javax.swing.JRadioButton();
			ivjJRadioButtonOverlay.setName("JRadioButtonOverlay");
			ivjJRadioButtonOverlay.setText("Overlay variables (single movie)");
			ivjJRadioButtonOverlay.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonOverlay;
}
/**
 * Return the JRadioButtonSeparate property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonSeparate() {
	if (ivjJRadioButtonSeparate == null) {
		try {
			ivjJRadioButtonSeparate = new javax.swing.JRadioButton();
			ivjJRadioButtonSeparate.setName("JRadioButtonSeparate");
			ivjJRadioButtonSeparate.setSelected(true);
			ivjJRadioButtonSeparate.setText("One movie for each variable");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonSeparate;
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
			ivjJRadioButtonUncompressed.setText("Uncompressed (32 bit RGB)");
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
 * Return the JTextField1 property value.
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
 * This method was created in VisualAge.
 * @return cbit.vcell.export.server.MovieSpecs
 */
public MovieSpecs getMovieSpecs() {
	return new MovieSpecs(
		getSelectedDuration(),
		getJRadioButtonOverlay().isSelected(),
		getDisplayPreferences(),
		getEncodingFormat(),
		getJComboBox1().getSelectedIndex(),
		getHideMembraneOutline()
	);
}
/**
 * This method was created in VisualAge.
 * @return int
 */
private int getSelectedDuration() {
	return selectedDuration;
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
	getJTextFieldInput().addActionListener(this);
	getJButtonSet().addActionListener(this);
	getJButtonOK().addActionListener(this);
	getJRadioButtonHideMembraneOutlines().addActionListener(this);
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
		setLayout(new java.awt.GridBagLayout());
		setSize(219, 372);

		java.awt.GridBagConstraints constraintsJLabelDataFormat = new java.awt.GridBagConstraints();
		constraintsJLabelDataFormat.gridx = 0; constraintsJLabelDataFormat.gridy = 0;
		constraintsJLabelDataFormat.gridwidth = 2;
		constraintsJLabelDataFormat.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelDataFormat.insets = new java.awt.Insets(10, 5, 0, 5);
		add(getJLabelDataFormat(), constraintsJLabelDataFormat);

		java.awt.GridBagConstraints constraintsJLabelDuration = new java.awt.GridBagConstraints();
		constraintsJLabelDuration.gridx = 0; constraintsJLabelDuration.gridy = 3;
		constraintsJLabelDuration.gridwidth = 2;
		constraintsJLabelDuration.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelDuration.insets = new java.awt.Insets(10, 5, 0, 5);
		add(getJLabelDuration(), constraintsJLabelDuration);

		java.awt.GridBagConstraints constraintsJButtonSet = new java.awt.GridBagConstraints();
		constraintsJButtonSet.gridx = 1; constraintsJButtonSet.gridy = 4;
		constraintsJButtonSet.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJButtonSet.weightx = 0.5;
		constraintsJButtonSet.insets = new java.awt.Insets(5, 5, 0, 80);
		add(getJButtonSet(), constraintsJButtonSet);

		java.awt.GridBagConstraints constraintsJTextFieldInput = new java.awt.GridBagConstraints();
		constraintsJTextFieldInput.gridx = 0; constraintsJTextFieldInput.gridy = 4;
		constraintsJTextFieldInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldInput.weightx = 0.5;
		constraintsJTextFieldInput.insets = new java.awt.Insets(5, 10, 0, 5);
		add(getJTextFieldInput(), constraintsJTextFieldInput);

		java.awt.GridBagConstraints constraintsJRadioButtonUncompressed = new java.awt.GridBagConstraints();
		constraintsJRadioButtonUncompressed.gridx = 0; constraintsJRadioButtonUncompressed.gridy = 1;
		constraintsJRadioButtonUncompressed.gridwidth = 2;
		constraintsJRadioButtonUncompressed.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonUncompressed.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getJRadioButtonUncompressed(), constraintsJRadioButtonUncompressed);

		java.awt.GridBagConstraints constraintsJRadioButtonCompressed = new java.awt.GridBagConstraints();
		constraintsJRadioButtonCompressed.gridx = 0; constraintsJRadioButtonCompressed.gridy = 2;
		constraintsJRadioButtonCompressed.gridwidth = 2;
		constraintsJRadioButtonCompressed.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonCompressed.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getJRadioButtonCompressed(), constraintsJRadioButtonCompressed);

		java.awt.GridBagConstraints constraintsJLabelComposition = new java.awt.GridBagConstraints();
		constraintsJLabelComposition.gridx = 0; constraintsJLabelComposition.gridy = 5;
		constraintsJLabelComposition.gridwidth = 2;
		constraintsJLabelComposition.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelComposition.insets = new java.awt.Insets(10, 5, 0, 5);
		add(getJLabelComposition(), constraintsJLabelComposition);

		java.awt.GridBagConstraints constraintsJRadioButtonOverlay = new java.awt.GridBagConstraints();
		constraintsJRadioButtonOverlay.gridx = 0; constraintsJRadioButtonOverlay.gridy = 7;
		constraintsJRadioButtonOverlay.gridwidth = 2;
		constraintsJRadioButtonOverlay.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonOverlay.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getJRadioButtonOverlay(), constraintsJRadioButtonOverlay);

		java.awt.GridBagConstraints constraintsJRadioButtonSeparate = new java.awt.GridBagConstraints();
		constraintsJRadioButtonSeparate.gridx = 0; constraintsJRadioButtonSeparate.gridy = 6;
		constraintsJRadioButtonSeparate.gridwidth = 2;
		constraintsJRadioButtonSeparate.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonSeparate.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getJRadioButtonSeparate(), constraintsJRadioButtonSeparate);

		java.awt.GridBagConstraints constraintsJLabelMirroring = new java.awt.GridBagConstraints();
		constraintsJLabelMirroring.gridx = 0; constraintsJLabelMirroring.gridy = 9;
		constraintsJLabelMirroring.gridwidth = 2;
		constraintsJLabelMirroring.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelMirroring.insets = new java.awt.Insets(10, 5, 0, 5);
		add(getJLabelMirroring(), constraintsJLabelMirroring);

		java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
		constraintsJComboBox1.gridx = 0; constraintsJComboBox1.gridy = 10;
		constraintsJComboBox1.gridwidth = 2;
		constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBox1.insets = new java.awt.Insets(5, 10, 5, 5);
		add(getJComboBox1(), constraintsJComboBox1);

		java.awt.GridBagConstraints constraintsJRadioButtonHideMembraneOutlines = new java.awt.GridBagConstraints();
		constraintsJRadioButtonHideMembraneOutlines.gridx = 0; constraintsJRadioButtonHideMembraneOutlines.gridy = 8;
		constraintsJRadioButtonHideMembraneOutlines.gridwidth = 2;
		constraintsJRadioButtonHideMembraneOutlines.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonHideMembraneOutlines.insets = new java.awt.Insets(15, 10, 0, 5);
		add(getJRadioButtonHideMembraneOutlines(), constraintsJRadioButtonHideMembraneOutlines);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 11;
		constraintsJPanel1.gridwidth = 2;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoC4();
		connEtoM3();
		connEtoM4();
		connEtoM5();
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
		MovieSettingsPanel aMovieSettingsPanel;
		aMovieSettingsPanel = new MovieSettingsPanel();
		frame.add("Center", aMovieSettingsPanel);
		frame.setSize(aMovieSettingsPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * 
 * @param newListener cbit.vcell.export.MovieSettingsPanelListener
 */
public void removeMovieSettingsPanelListener(cbit.vcell.export.gui.MovieSettingsPanelListener newListener) {
	fieldMovieSettingsPanelListenerEventMulticaster = cbit.vcell.export.gui.MovieSettingsPanelListenerEventMulticaster.remove(fieldMovieSettingsPanelListenerEventMulticaster, newListener);
	return;
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
 * Sets the encodingFormat property (int) value.
 * @param encodingFormat The new value for the property.
 * @see #getEncodingFormat
 */
private void setEncodingFormat(int encodingFormat) {
	fieldEncodingFormat = encodingFormat;
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
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setSelectedDuration(int newValue) {
	this.selectedDuration = newValue;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4D5D55AD8D8B85108060946A499E31428D4D4D8D6146858E4132D6514B3B133EC726F27FE1B7CE718FF2C51556121C278204024916BEAD6E88C8262E38CA79FE8D82428F8C3C5AE876E01FB653EBC77A08F917FEFBF4F39771EFBAFB22B354635BE4F595F7EDE7B5BFBFFFB1FEF6F0BD078C94C0A0C4CC2C148D8A150FFFBB284E135A8880F17EF7D9943C5B80CF902665FE600770507FA336199
	83F5A93FB266658B2FDF4E01F2A214774EB166ED007782E1FFF34FD9F87123279E6AFE33FB796C40FABEBE4E742C4265343E5570DC8B1084B8744CF6A27D4215A20ACF25F88699190043B49B2F94D3DCAD14DBG22G621D04BE851E39D87370FDFBA9DD5328B8A17D17F2669AA4C7A31341D697612D25CF0370CE46929752D5F3E5B5658901F292GEAFC1670C66EF6F8D657F7F67C2B2D552A14DDEE13EC36B22947651415FD7B3EF7DE36CA27A5C531BABA5C7522C33289026D921297FEA587738BC2951417E2
	5C4D925211G6FF3G96F27CFD6D88DFAD9B739AGECB2EB577EAF2A295D0DD79E9196F54C4ADD6727DD21B2E9CF3C2C364B303DB56BA35C8756934857E9D0375AED4CB3G54G1881C281B6623E170E23F69EECD58EBB6DF62763204BF548E6163FF71AA59B3C6F5B87B594F7CAEAF34AE6C1A07D7B6D15D29DFB828161EB3CEEBE6213381D2C2F63CFCD95D29F3CBDF3150ECFD2632EE4C63B09AF925D2ACF086ED1FEF54FF7FB0EC5CF5DBF88DA5D426B152BD66868AEFE31F2D59A1533CC233BCAC1FD7DB7BC
	860A61BD037A0260D761B150234113F1F9AB26C7658828CBD75858E8DC4BEC294B1EA864D70C5575C3545361190DDD4486D71737AD13BA106C68AE2273D3EECBB246AF566009AD8958C6D42E3EEC4CEB6CB6661171A51FC473D469B166C5G4481A483AC84D886105F4B46585463BB86B9466AC5D9B954E53519A5D9A03C72098F61A975A832F84C61D6C4C71BE494E52B58EA135C822539F28279205503AF840F7DB650710D6410E4D131BA9DA0DD34CB0AA43B894D08936FA39AD942358DD63B848A6CAE01499D
	F522G1EB6512D1CF119C5C5D2A9C87D389328CFAE0BC6108882601D78E542A9642FF2E8FFADC0A377C3E1BD12771DA4C33C58372F4E696C6CF2B5201688F13D484FF1B8F65860BD341779780E538837984A59FDCC4EF7759FE86454D85B1079225C4B66D8866E2FA2205F8BD05F47464E9F9B595829D9B3C5A87D75985D319381635852C746CCC81F779CDA03E5A37B6220EECE9F0B7737C90D705C5B27BF0F98948529E7D06FC5C7F444E39DAB0270E68DC2BE1329AD93FF671EC16DCB837D59D70CF9DB2FB01F
	5FD4EFA0E3781068165828BDA14300134CEA8FD0BBBBEA28DF33283D880F7AGAD1CE80DDA076910DCC1D88F30016A5F84708940CE012C2BC45EECAC2F9AECB1830483A4822482E4779B73CA819AG7A81E2816682248394DF05F88FE083088418F1157555436D7999C333EF2D46464D0370714EC17645FF82905FAC473E39DE3FEC74724D270350B7987F7D27G19837DA7D17B254B12C32911DC12438CAFEEDAF763C964A79A275B1D1CE784463BA02E9F06C06A667811A7119FC5C5112D2DDD8A5FCB717A31D8
	173B2B5525AE89CC571DA7D15FB83BA12EF37A3BCF22185866F4B8A4BC99B98B2D7FB90E4912CD320351071DDD8E0560A71FC27B56CE29172E8953CE2D94D0CC37F52175056C8593C4547FAE59EA07A90E16A821EAG456BA779DE9641A218DFE8CF1B83F54583AC66F6FF4AE26E563EFB05E413ABD3EF6F90757311197D83A466C58C28B1175894F3DA5F76C9F8ED20F8B73175C5B372035E7EB48B739476A0D9073A94456978C6F6F639AA885E7DF7AFFCA50907070CF86F8AB625G248E30F83825815533B508
	864243CE1B0D389F59FF8D7968D4FB21261D698344C7EBF97D005FFA22E75D19FCB48E24CBDD92AC7DE8317C0E775507E7BE702AB352FEA3759F1FC17DE6532C334B304ED59676FDE101F288C04CB522F761B53AFF43F44FF23A5A90788E81888951239BE9E5F449D03FGE0A9255BBB8C56F2002543905DEFAFB23A28E124BEE198DDC70791F951B7A03A48CEC6D7F3830975CD703481048314811E48EB35B13A349B82497B504E68049B491AD1844F129BD5F9249D8BB9DD6D0DC4DE8BFD765CC864913D639D3C
	9D9A3AB99A3D046E381351A57821A376E53A985D1C9B49BA36901ECBG72C309FD04EE9E27B3041235EEAFBCEBC3D5BAEA5FA55E3F21C4EF88BC23G12C2357D56A9B33A3934FE91255FBADCEB5F8EB7232B9BCE56D473F0D51EEA5F5B1CCE99CE565F7061C4DE4CF02D1F9FD278F8817C82002514EE6FCD463C301B58B7681F372178023FC1AF0A17451EB2F7B74C67FD7B3E45B31AFCEFEE6BC7B1G51C75F443EAB4FDCC3F3F6BE149783E45F4CE2518BEB58F7E5E4E71C107AFA45AAFF5F4B45B713769F38D9
	0DC525CB8E2EA4B1A17C8724B3846AA2EEE671E05C996D5E76205B6DEC3362EDB51D0B35C6ED2CA2386F0D6F41335DAA3B15E330187430F9FB5A086CC57BEE0A26F1F8F959A6DDFFE82216403E592E9E5536F5463A2B19F9FE5A9ACB5B1822E96B82FB43AA726DD3EED9CD75E70799732A81C85CFA57220DDBCA985933C7431344533116CAF80AB8981611C73DA437BBE53BE4AEC15617095DCA99DECB4B8E626AA368FD9BEB6B0AC3EBF1ECD3EA3A4847CB49B6A3EB5B440283EC5CCE1DA8D4DFEB4E544B8F042D
	33E4A60411B6AD88D35BD6BB3AB52B890034AF1D37EF6B8808B3A3D87BD287346D0B9CC15AB7E384EB5FF96BD05BC774A6D9D78BE4CD1A87B2F3FE42743E516C2D77G5419FF4274FEF2715F736B175F6DA7E3CE7A168C1B935F91B2133730BAAC2DCB315A4A3008932D97E1ED7B8AF5834D75B4DFE4391E9E300F34AB2B1335AB81F0E95C6E0B98EFF5D8952BE83376C9A55B08BD650EAD28BDE62A0F0E6932032DEE58C935A944462F543E1F7231366FF344613AFD9F7EFAC72661961A733AC56D7B846877945E
	770FF3DBA300E64EAD4C562DB634A792650E8A52A7499EFDA2041331D09A4E686BB03DE2313A090D7DE7190DA1F393052202DBF5ED4471A51C58E689D7ED6C0FE94D0A08E1B65EE2E3B6A6814D12F066672F6D8CDFF2AB2CAB37B2DBA659BDED7F6385BD5B23EEE5F2F29DEC7C4783EE911763F26877570B9D05947F3DD5161D72E10B535AA639E1F692F9B39D4C2E621F42F759CF191EEEA7430783AE05633FF4B17CB240D50DE47AC73BBC5B51A46A354346699FF531BEA0F25F66F2C300A6095335DC7214FB55
	24A7F749C8E65F4CCBCCCEF684443F88A6E73B6C6D9FA26FE5DDF98A67EB10F3F0DCC37BC449CCF4C44A2C8F4200AE19537ECD7E77667A04CF7F0EF5A935D633743DE4EF15E15DBC5125582C8E29649C1AD3C4FF231B35F1A968AE9F4574BF69E63EA2F4DB94C6E7811A18D1EC1DAE7C97FA62EF86016D336FD050FAE871D5CCF57EDEE1EDDEGF272C773385B26977FEAC7B31B0EF47DFBBEF9EB5BC14F78F7982554ECC30E0121DDAC86B60DE6790532FE6FBCEB64E8B51F93B75AB787C4720F1B8564139923C9
	7EF1A3476F4178E59ABC7161F93C4FC1656A5BE0CF9B49F2E6F967503EA99D4A79E3E01F8BF0G2085A0E48C5B2F3CECC4390900B949E38E8536B3226D3447C7A84B8D7E005A8C5F0D34BC66876D98A0F6741EC7FD998DBA13G66812481645C4EECA8EE8CEA43119EDD9B081FF7DB3C7DDCF23B27EFEBEE574B2355787015F915ADDEFCE46CFCD602762DBA7B5186786EBFEA15ECE6B2A6F6DBF3282E105BD91FBC5F04F81320BC9FE0B1C04ED8D8C3GEA81ECE319CFBA9B778775899A098D4E43002B05EF3B43
	5E5F7C3C1D6F8C51BFB956E07E899F2B671FF527BF52778F19E9FC4E94DCC37EC17B515831EC4EBC6123539B74ED1D9B14AF554BD6123BB8556C1D3BC0F1BA9DE8F32220BF301D1BCD4CCEC39433336DA2525798B538BC2EEAE7EE67505A37A1A81F4D4B96B25E0EB62371968AF5B1GC9G73811682940E036F2CF1EC3C4DE87CEA10630D04C85F51C66C1CB944763D90142FF11C5ED87B7050477AE36D14E836BA5900A37D7857E6B69FDD6358FC1CEDC27E0903F28AC0AA40D2006271609B0026714CBFEB4F9E
	8A669FFE2C82BEE279BC1DC9C94F60ECC373530420FC4AF8BD3EA3762169BB9D14AF5CCB9F7175E75C573163591CD96798DA9C5A6443D766D5CE55ED73B3BEFC03EB73CBC17952F575DDF58ECD5FADC179D65F2147D7619A1A3E2F0372ED3DC32FCFEF93D91FD65F417A745DA15A3066D250F12CEBC3220959904EEDF8A628ACBF7D9C14AFC557068FDAD82EA9155BB0DE9E5A580E7361739E5B0551FAFC4D6E21355996142FD4D7DF09B2B4FD3B0373696ADB6443B7B8FF7E49074F5B1FCD51FAFDDAE0E6FD2AC4
	33BE9D5105E445C78FFE0DA76757C7F0AEB299786683905C168104DB8A657C1FB179DF88D5702C02F2BD008DA094A086A085A08DA067CE583783548218817A81626EE47C7BB17F8CA8275EC9737257C8FE9C6534175DC9F242A8FFDCBC813ED9A710DCAD7A0EA27C6235F99A5B118EFF6DC23685ED3B063F5E0B3E0456456848C9216DB4FB69C7ED8D2574D5BAFCE9FEECA2ED0B72E85B7AGF6FB1FFF6B41C64D3B7739FB81FD6669702D9148B96986AFBC5AFB6452F73DF37AF5816CA8908A4F79BFFF5CF8CE7F
	7CF173B97D7347AF4E3DCF70A0C3FB6B04556FB89778FCF24FB97F6713434E7B37B36C3C3E1DBFBD2FEFE7647940F6C61F77E767A66C63E7BEDC07795DEE490DB732B522A2523A7FD6507870B9B3BD8E3BB90165B16C16DCCAF3C4F19E2E60636CAB7E5D51B4C19D0FAE7A9EBA41F70C92FBFF53645FB78B1B74FD73DA939ACFEEE7175CA6F9377F4D26A2DE27EEBA0FA9129DB503507C9F66D7C439C372715FF24CCF6A7C712F3C604F4744C795D62D0FAA390F0A2EB19FC5E97C91CF5F1361B99FA0CDD3C7647D
	45C39E4A9110D87D13E6962B17CDE0E7BDBB1BD18E24AA4618D787E00EE1B16EF393535D8F38089822A316BE1360B9B746330FC83C6E43B96D05D037A406651C8EF4EA6C793E027B2E3E59FFDF7E502C5F17971A7D772539B9F8DF5A1B7D7725529C3CAF2FB6876E4BF7BA73E8DB6B5BE5273D3E02766532E664CF61AECF1F9552F249DD7E467BBC13FF9F3DE45277516FCC7EFD3448945CC7EF1A7C7BA85D945CC74BCD48872DD633D526277944B7F3EC5AF1D049FD56EF7774D9A5755976E63DBDD0B575D5BDBC
	4DDEFEA3720EBA34633F0A638527D67FCE0ECF74402B74961727DDBB599AA9A23BFA3CF40753F20CCEDF92F93BAFF94A2B2272261B745A19C8654C01E72AEEBB85D9EB77AE9EB7B6346849DBC2E5E4CF74E75F8BDAF907F7F17C9837275D3BA85E24F862F7D37C46AE2D1C5D5C2EA2331EDDC5541ED29D3BC8CC0941B96283D0DFBF11451426FEF44F46A676BA3B94C118BB9151397BD9CE2A854ABDGB1G0993C9EE0A64BD6E6FC6B6374B229D266DE91754A6445D0DF87FB940F88B21DC82D08BE081883B1B69
	9476223BA45F56092D128DAD03C71D32DDD4C85D1E4DEB55BAFA644A7DFA961DD353AC9EC48C065FD80B66361A7DBC66F0C1E3C85D48BD489EEDB6660C234DE9F749922C44E6C213DD7521974DE1CE41F4C4995F67F621DA275BCA6F57107A3ED385DEB2CEDC16E470AB2DCF59D068D5FFDA025D0428C824BE8B6FE308FCF22285FE253C9FBAF03BC1ED2B731033270269DCFD4260A5D3670045CDE8BFB809468E3931D0C16F7C74895AB30EA5F7137EAD0CE5E7A0AB4F695517707A7BCCFA75553C7E46963D7AFA
	DE7F3147D91AB91654074732EF16D3B8FF9D8365999CA7DCC038F9D0DE48F1528576ED331863F6DCE33C55BFE73844DE042B07F28B472D6FE33CB60EBB093F1FE6C0F9BE4755E13AB4A88B13585ABE52A72E96CED267DE49A45FF9C84ED16E41FB056AC9649CE58547BF0071759ABCB9DF79E7B3BB23B2C35D1CC94C267DB897D1FA8F4C31FB986E8846B5C15946F1FBF271BD91A8C7F0DCF4AEEBF7B447E53563BB4ED04E0EE3F3F554C5F676D69307ED476522B846D3007505C1B91A631AB0AE814A499C771409
	691B4BF1871B99DD713D8CB731195115F05CF14C5B8365F00E1B06F3C1B1D0CE64382598B7874A8BB86EECAB13370863661998DD55FD8CD75142688EF0DC8616978165D80EAB43F4C9D01E43F137F15E799C3783FF4FEF1D8CBAA6B3DC24196B184C7C7BC81727FF1F436508496C5C770EFE763B189900CB1F4C62EFC50B479990BAED5C376F9B59EABEA4F65461E8CE762A2F62310C78CB6359DE757D2BC826994A7DG11716CDED24F676C2ECE7C2409423C7F523F17343061EAE6FCBC995B7362B53FE9585E1A
	C5F41A86184E55D30CF9B5D3706FA8BC6E3120363F1EC9F2A3247EC90FFA94EB3F9134757F6BD10F6CFDA4C3DB7F69B5EDBD7251CEDACF5A766CE72CED4B1ECD90EC8F057A7D0DD278945226847AB47CEADCC6D8D47DCAFA972C5D4FDDB09C39C97B6F97D80EB0ED8ABB4359FF0E618CD3996E95BC8E11FD0D9C77E88B43C5CCE5FD5DE15B282F132D88743B640C48F867CCE563EB914E8140E256AE4932E4BEED6DC35F14FB27A17AABB83E8A703EFA9A235FF0815FDF002534FF9A7AFD59058EC4121300687BAF
	54D09FA5CF730DED99D4F7960579C01386301E764732E9A42EE5F07D0DB8C657036C2684267FF013277E900B2A3F342FC6706DAF6D18526A8F831E6884D5FFC2022A1F6ECB7B519E86B6B0D6FB179DFB065895C262BF504F61F63575E1DA31470B768ADE975201CE381F4D73C9B80615C279G47957098E13E1F2D9FD3B0DDA21467F2DC9246213B2B68BEA24D17B7B786E8379EA0F90321CB64FA9B2FDBC7305FFFE87B055EB9B1799F0BF6BC9752401E747B795E540446C23B5321909A9953989EB06615BE40E8
	0E67FA0E173B2D6AF8F9B0B750F8510E85FCEF82E4A2D8CF712E873C474B5F5B7547C0A5DEB77B01BE0C5B7578C5747B830754ED503D3AD102EE88ED2331E93F247D7DE479153A4CACC1BFB6A5BEC06FC852E7783B63B268BDA01376BE8F316B20095D89C967F639CD1E7EBA5E297AEB010979AB7BC17D794DFE0F011EC5C003004D2F5287D5FF513E30293227B7B359FB7D48DE91C0F65D035EFDA1195038159C68BA391976C33F092DBB0D8F32764E96FD7D72BB1179454669F6B6FB7AE536DD355D554AEC6F77
	E37B9A0D6D91C0932131BD5647AFE5CED5F6ED8B131D60C7763AG32D3FC7CD2A47A7AE587EF6FBC5E5EA3AD1E6DBD69D2ED3A0F5B34500FCD6FEBECCA871AF40DCDB9095EB69DB5FB6A9AF1C955B5594CF495A66AFFCF871A23A5BE3ADE6D9A4C3CA3B16FAE1F7BD1410070CD9BA2DF5AF573116F02D771FE2DCA53D602DF0C7187B4F832E75CF0156D996B20AEA1115DB53049E86FD27810B12F9C20962085A084A07AA1F64F604E16F2A148BD0386272BCE3AAC59B4E9F052BE734050FED37C54AA36B7CBFC74
	BEA169269A3FF744939E227BCE7ACC1ABBAE83BDC97B5EF52376898F437784408100C6G9BC068432CFD2F611C8E3ACDE236AA8795C5EC33601B702CF1C62B542D22D1E2037ED6CF60B96407B5B9E47A1E7C306F58A5FC074EB13E799A1AB47A3E4CAF5FEBBC17157F08CA33153E6F05E75DA31E3C046FC91E33EA5154AB74BD8C1E513AFC67C4461770082FCDA900DBG30D8D3C7667254AE64F21AAFEFC2F7E1785CC397A0EB2C60C674235C121D6525064ABD5BD81F47F4EB573C3D6F5D207BFBAC84C29251D9
	18242EF9C446736735B272475BB2B37C4828233C0DC96A778A59B3CF3940F27961D07709C04E53703E987FDB7835AF0E357D8F5D829F432F2CE6E338F8725DC24DCBB5191F7A31A50535E73AEACBF2CBD5AE11D9572B7A735E5EG7E5C4D7CF9FDB6BB7B547751D7D4DD877B82682A5C853AF6EDE3FCEF772BFCFF30FC621FEFE7F9B9E124FB22E65FDC63FCFE7F2DEEBA49B51A51137A062C915B79FA94B1DDFFBD5AA0286BD1AC50A4CDD7572339533D570837D622BE633FBB60786E6DE89DA797F1553B02244D
	4583EA1B77198278EAD705072F70B2CB793E4C5D624DF794B637A5DB4CD651666C58D2EAG5E0ACA5DBEE572A29B5477C74E28321FEC8B508F95289FF615EF23F152ECC4C6AD184EE3229559E594AF52E80FC223D34691911068FE67E98527A3292EC63207143BA4B207F279FD144553756690337717BF20F7427386BFC35B7A28B10F543F59283667B6932AEB97EDEE758CB4DD7454FBD7AB135174E8A03D7967D43D3D5C56C82EF767F9D56FD1AF9DAFF37A3981F5981BD49D297CAEC776E3CCC76F85D4CFE26D
	6BD75577BFE95EB7DD3D5EEF306B5F036C7ED1F5107154A57BBFEB6B17754F5AEE10754F5D436440677F91327F737F3132767749E46C9F433F212906FEE9C4FD839081907B981B8B1F3951DCC0FF8943F7EFF0445C014E7A50DF3FD0187E9C45FFDB57A9417FE6C61102F27B2DCE88B6F6A41F7FE6D6C17D25B8DD98C9F01FAA48C73235432290D20A5F26603FD7A135E3B449B7BCA7307CF772E3AC87D15A44725F73B9EED4934BCBAC6438336798EF798C06DBBB40F8EBB84E496F9455F35CF27C8D968365999C
	D75C4C722D73B86E9B117126F25C9BF82E15BE8EBA9EE73897B12E914A8A47D5E35EB0A8C7F15CDDDCDEAC475D09F953213C7A89063BA4B27B8C9CB71D67262BB86EF3CC978A65E80E9BC6725AD01E73845BD337E35C82A827BF4146501F3B3E947C4753FA596912E42517E071AF25695894DB845E277F3C7C19BF99B0923F0598CFBEE89A448EBE379F38205DFF97BEE94CABF9B2F09E087E0ED444FF5782E89B817A81A28162G128166832481AC83A8CCB66615829CGE8G30G74G048344A43376FF5133AD
	C07BE9129ACFA4D9342229C43FDB16B5336F16C41035B81965C4EB78F77D5627D88EDBB6A15AA6A8ABG61CFB15A73BCDE47BE25972F1784589BD95AD47FDB0CE82F62266B9E716FDA0F7360D4103F6429407EA5FCEFF91C93E5BFED4CABFAFAB0FC13DA35FC2540F3E0D0FC839EF6B6G0FE5D0FC8BBD787A01A7E2D0FC2FF81CC74782CFD290BE626313816E2DB4B6870F1DA20EAB92443C06DE973DB3510E77184E76F63744E3F4E7F3BE479DF3575323DB129BD90E4741DC30F74153EC2D3E450C468159FB46
	B58DA60748761E59B321EFE7EAF2E2B33DF35EB760583A9770B5B3193E29122A6F2601E07AD6E97439C0C628C6DF54CC6F3DEEE10B7F1C6BBF70DC09851E84EE4B8B6D2AAD4DA233A5F5267E3EBBCF701F1BDA62E3CB06281FBF7D88475AE5C01F134272D3EF8630FBA5262F825A831C7E346819E34A511C9BA471F658D2745BD120E9C78850A0E06D08CC71EE47C3AD7A6DF88E7BB38668638188EEBC36F58114978190FB6671BCEB7EACFDFB568B7E7D3AF51637BD7BC57DF38CF29ED48E745533181F22F05CE4
	79D0F45F081CBBA660BD3DE5967F7D1789594277DDE419E2FF8B4C44FEFB0BFFF30A6FE810AF8432CEBE033F2995EAA7417D8A6342E7293F9DAC90E2C47F737F9EB1787CCF907577CE8F0B3EFB2747C47D3D53D322F66FC4F033C564678ED8279AB95D339CF7166366E119887703552CD8B87EC5EC974257CAC8A14327E23B3AB12E1162DE51604E32FB16A20AE150E40BD3E63C7F0347F73B55E6A36537B00D5581A10D7BA39D5BE8F5099A3B5F663833820B57193A77240247797CA1729D3BEEBE125FFC8D0F55
	38D95EF94F37B0BEF116F75E334744768F49D037F5B6BB5BEA71D85742G9FBDBB707AC4466CE7AD3E63F8FB0BF69C935CCA13AFDD1E49176E71D6DF3A27DBFD694668689D2F23772FBAF44BF468161AFD69161B35F4A4E6FE094F4EC81C585C94B01F3017A41F7476D27E77D77197557767BBD5DDD9830172A97E73DC01F47D49263E2FF7283A5EEF8E542E2ABDC49959CB0E9459DEB2E1B6DB9F77BAD53967DD2A5C9653E064923EACD9652B9087EF4F6B5C1E345901F2911F3B5577E30A2AABE3D03A881FCD13
	571D9A30EDBBE90E0F581841775F06DFB01F0D3C2C5A73C037AA77F2C039153B35394359A6C1E0EB22783966EF9E6050CB7045BDEBD4BC8B3DB486AD467C647DAF1A7781530F1FF3997B236AA07339DBA79674FB4482E2CB24280D050D50C736DF8CE60F7FC78F3E9060099C945FCA0F589B87BCB30270915BA70B3E6DC990FDE35B8A1D187A1ECECC5D24631FAD9E7EA1FF03F97E6590D8D8635F94D6A8C2E6C539A2E421141310F962BBC578C8FBF554D032335250EAD58C273EB9348D6A8EEBEA6C222357500E
	EF353A21EE638907E43023BF85071086C9EC3398AE534D0CA29868C5D10EF29BCA5C301732D19E90309E5D6BB4300B1D86FA33D391D612DC35DB96BE701DC187FF7A1B1762CDB50519C266B1E12585CF07E3420AEEB4DFFA589FD77D98F857F15EE3F8BB3170724EFDD92F5E1D15A9E483A359DE9D9332519E8978E5A2A78B36D7D88A7A779E1D1AAB5C170481FCA002AFAC61927B9B55283E5E0386A59FBE5216B2CABD6AF602EDEBBCB265336E5A755A5C5F9B5DD9424AE342C768E248E9C9A6FF77440CAE9253
	BFGFBD448F5B92DD089F8B05C0D9FAB5CCA2FCD2A938A5BCF21BFD467065DAF7F15349BDC0C7F44468EF4DE3C23D6F4DB945493DF5589598E741BEA6183D2ED939D9DBB48D677AB5C7A0C0387F06B7046836CADD0CF1D3914024756FD36795BC5C9D9C266D161BD1BD3B49F955B9427FCCC48D5ACB0E4ACCE1B394775B59114530D46A4F2DAB6085B481B0F9ADB06EEB8F323BBFBFE963EA6ED4D51CCC16811059D1892FD7F14F18A73E10B087EF811A443BEDF96C165D6DEB76CE36573673E595014A9E450C6A2
	579776389462B0BEAED0134B0E02554AEF5FFCE6624B55CB96834BD1E1857E939B20B8DF25BA5EE5EF15641B6247EC4A0BDCF88B324B055C03BB3588C6EB3BE8375AFA61051CF7F0BC597D62E2B172822B48054D3F430D6691D8E370F2852B62ADAA3B6D1972B1773EF38E37C8E5D504CFC85F816B0E8E49E95FF1303D9DE68F97G9D8962B7F17108264C0B66031ED16B3F7D753B2DB070EA04ACFBF9B97E3F827FDF097E2F40188A0C2928045EDCA2847C67DD5F53035A3C3907CF19ECAD70E901FE6BE17618D6
	EC6A89BF92F6DDG0BFC300AD0F09AFFD99A4CB09BE9845C8741917F9DCC03195E670778A635B99DE677B628AB229CED6A25FA08FBE6892AB6223F3A62B09B2CF6318322E2EB2F414EEE466F7308B4B61FC80360BD8F1A4AC1505CB6E81A21F12028F68367D8A168C5CEC4B3C04E1437DD531EEFAF9D5E66770CBC67F9322696BF2F1EA9135CEEBC3E1F5888F865F976ED3F2C779F027F5C72E93BE833A9222D7320430C724C07FA95C9BDEF393F8F49F7B39ADAC96A127A105D97B5BC7F8FD0CB8788D4C2FC5355
	9FGGB4E1GGD0CB818294G94G88G88G5D0171B4D4C2FC53559FGGB4E1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8FA0GGGG
**end of data**/
}
}
