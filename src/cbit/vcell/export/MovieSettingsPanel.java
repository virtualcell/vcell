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
public class MovieSettingsPanel extends javax.swing.JPanel implements ExportConstants, java.awt.event.ActionListener {
	private javax.swing.JLabel ivjJLabelDataFormat = null;
	private javax.swing.JButton ivjJButtonSet = null;
	private int selectedDuration = 10000; // milliseconds
	private javax.swing.JTextField ivjJTextFieldInput = null;
	protected transient cbit.vcell.export.MovieSettingsPanelListener fieldMovieSettingsPanelListenerEventMulticaster = null;
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
	private cbit.vcell.simdata.gui.DisplayPreferences[] fieldDisplayPreferences = null;
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
public void addMovieSettingsPanelListener(cbit.vcell.export.MovieSettingsPanelListener newListener) {
	fieldMovieSettingsPanelListenerEventMulticaster = cbit.vcell.export.MovieSettingsPanelListenerEventMulticaster.add(fieldMovieSettingsPanelListenerEventMulticaster, newListener);
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
public cbit.vcell.simdata.gui.DisplayPreferences[] getDisplayPreferences() {
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
public void removeMovieSettingsPanelListener(cbit.vcell.export.MovieSettingsPanelListener newListener) {
	fieldMovieSettingsPanelListenerEventMulticaster = cbit.vcell.export.MovieSettingsPanelListenerEventMulticaster.remove(fieldMovieSettingsPanelListenerEventMulticaster, newListener);
	return;
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
	D0CB838494G88G88GDAFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D4571524A509691A14A4A6CBDA525804CDCDCA9B5AEAEB9BF65734361F26F4CB89B6E9EB76A38DCDCD2B9B1B585626F67398A081030A910D0918E05484A2C17ED402E8D4C6C5C5A5028808401BF9EFFEE07E18F9400028FB6E6FFBB373E68669577D6A779D5F3B671E3FFB6E3D675EF76E9D0462F7E766E59B0A85A1BBCF207F76E58BC221A8888FE67D71C5062B7FA03DC0507C5B8630DE7849
	15DCF8B6C31D63667402A26156AB79D06E01F2E6CCFA411B703EC1B8EF72F741CB98BD2DD0E7FF2C68E0E4BDFB4FB1BD2F61F215D8884F35G7D00C34F248F107EE6F1B34547D2BC03EC83604A900DD9AC213865C0138F38C3G6DE5045E81CFE2400D8D75142E6776D9420A473D06B5C80EC6A603D44A3B1CBECB051733634B112E6958CE44D38665FEG54789C61F7859F423331F550FA50EC32AB15A3E6596128147D9E37D7E9E8B86D9E314BED322258DDD6DF2B68129D603E8F113C8CB19F738B42E210B593
	63466CC8C7A73CFBG26FD48708983881F856F2BG0AF731F69D7820115A5DF979BBC2C60DB3F7DD18F6D56CA26DE95F2536EB2963E943DB388FC65A102F6320EEB640820025G0595698595GDBF05FDFB9075AF95824B4391DCE376B3047F344A1F9CF3BA559816F8D8DD0C3F167E4335BAB8982695FBB448A9DFBA281611BF95DFC44A733EAD9DFA71D7B3A3078DBF79BDE53714962D9178D468A620B1E8A55A7C477AF426A1E28889C0B013AAF88DA5D953FD8141327233B79C70BF262281CC40D6E4595282F
	1761B1B09F5E3329AF887EAF98DF7A110AA763729A4623F2BD54A5FC4446C667DAE6CBCB6E7DC2D1753F6A7AA17E7B538DC2A531E1F3E530AD7103C8363112685C4EED79A6464F506009AD7763F10A4AD9D0A7D425971071F56DBC1A27A555698575G2DGB600E9G09GF32B59987B51395D139CE32D22D7B9B2ECF7C832D7A03C575AB641D376ABDE71044B2708AE335CA1FA6D2249A17B84CAB32D8D79409440F7AB9E7BE650F1DCF649DED1313BDDA0DDF44A0A6C75911A3B5B56A39A2F0CEB5B6DCE9994B8
	BD82137B25368D70F408BE653CC79295D925A0758F3621BE9991BBC0A28800F76217248E642F27207D4B81049A6607ED6DC85EA9598B71222121456D9E9C76342316882DB5484F4BF16CA806770A9A6663D99790EE861413381C4E76B7B4F20CF6B3B2DF740E31B976929EBB7300BE93E0E58D9BBB7F5349460EF14D57040AC5F7690E1D949847C535E44C1455864F21D730ECE4DFAB54F9EAD93C3F515E89CF24AF52DC1E97251E414AA0BA2263CBD8C7DE945E1CC948E7B235E5626FCDDD28FDF1502EA40054DA
	6673BB5BCB4998BEA27AE4B6EA4FFAE14049125A83544EC1A375EB8E359761D19F3C8E005628F5188E4995040DGEFD27DDB815E852895483ACA644D4572324016423A7402BA009E00D800A4008C00D5GBB76249734GA8G89G73811682946FCDAFE8841076323E1AB5D014BDB57B56EAEC5CB6899F57CE32AF7E99007866856C1B6B754B16A05FEC1F043E49786F1F85489C3865BCEA3FBCA23B94236C11DD923C78E8DD5CF964A79A27ADEE4EF3B746FBA02EB7C1A075F1FC42F964C7D1D13CF653304277D2
	3CBE916B728D1BBC6A1240F4FD75BC6A9B77A844F5CE1FFC9E45C4335B651271E464AC347E1BB8A64B8E5989C6B73907DD8A414F6DC07B56C1F90C2E89A99D2F8AA826BB06517AC2760249A26AFF0F576E04A90E16A8E171DE942FDFB3ED5678E8BA60501EB6991E73773218BB3A1D455C9D638F89737ABC863D3DC37C3FFD5E30E39F09F9757B5418CBEC7A5A85FD5B3F0557860A7791DBFFAEA1BF686DCFF3B0CF319F49BAB22CA8EE57F12FFB58D3CD703E370370B5A49E1E6D46FBD7304981903B0F45435D1D
	281E2DC1B490B6399D8E62FE28DB308F7948B8D02CE9E742BE6223353C5E93361E68A969AAC263C09E9A16E169C70B65A95ED75B3B5E882A6B207DC66AF7F521FEF3E8565944FA3C679AE45F97C5D0AE83E82EA7FA2755537D9B267BF7877F8E01F27E47C07B319E5D8DCEC6E7037A98001914AE931E958DD0EEC0F429AEC6F7280154F7B650F5340148B36EC7F4B739995D5286926BF381DF8CD087D089F8A22F5D43686276C71217BF4468E66DA7EB46BCF826E96411F624F33A65D4DEFEA3F916B692F9C46FF4
	AFEFC723CA675168A5F407FC0C2EB38C9D31EF852773B412F5EC9ABC638166B6927B885D7795C63720112CF51970DC2E2123768D737EBDC07416417390C07F81ED3FDD9AE1F48A2D1FCE69D38FE86D5BB64A68D69CA0EBEA61C1D51EEA5F6FB9DD49C1327E569DA4721A8FEA7D7C95BF9FAF000F8118C969B261D975897B86FD299C45977C8DBAA00E087ECA5FA84C67060613F8C6136F4DA1930A81085E78897B2E3CD7C6F3F6824AB3G12BEE13168E7EB58F7650C41D9C27EAFB3F242FDAF4F7F0434FF49A7EA
	AC1AFD77E88E098977DCC4BA4B8E413E7F900B878FF6E9773607FDBE37590E3755F4AE1E6F5646AA026B6EDE8DCF0B5D6BD3CE40E262E773363F9B590B765D94CD63705FAA376A7AC3933584764DF66518362DA96B2E98DE8F535616C3B42FF0C8738DBD67CFB9645B67BDC7A1551F8475D9GA4EEAD32E963D649E132E7B742134453DB9CB57094F1B02CA40FD659EBF1FB1D32D4062C2F94C715CA3C16D69E4655C751FBB9EBEB5E1135B836A946E17271D2D65E415AF67F06C3D4EF6A576F979A2F759A747283
	5356598C1D07C91BE28E2BED4BFC62E9438AG523E67F87BD201A67788EB5F9D0236FD0DC7C87B24A32CFD271DD3ED9F517B3033D0A0EB129FE4A6F33D374941FA17G94B6B13D6BDDFF1FDFF71DBAC0461CFC128C1B332788D9DF303096ACEDD831BBAA3108332681D85B7623EE20391ED6974B751402FD24DDFFF431F6F582AE0E5B5D63C6F83B4B2E58C507FDDCAEABA776D4395EC36D1128BEBA26AB8F1BFC3013B2AB44467DEA5FFF6D9DED5F670BB76A767D74672D0685CDB467552476FDF34653061E8C56
	770F38192D75C60849C6E66BA68F5A9309DEEBB5691305A6ED1F4CB212311041698FE2FA45E677919BA73A180DB12933054D9BEE5535914797A3312D6828EAE3712F1FB6147D1A59F84543EC6C871A78234C4F55C38C1F86387CE34C167821C05B6F1074ECBFF40C49796B909B7F6D001B4E65183C7A7D75F3EBB1451F36FB3DEEEF134DEDB74BBE181DC45E9CAF33EBBE48DA457558FD8CDFF79C76D24799FE2B42700900DBFC1C690FD5825B91A36B3523381951A7A92C8F085C5FF339E5C0534F694E8D874A5D
	E651139B5F4C6C1BB34C64A481AE0B4BA99989768F11F7CDD7DE498946F7ECA49F47B534CF14A5226326915687D5C0E763344BC67E3E39FE5F7637C98CED36CB72E959E972423AF9F6D8F158DDF2D9B71AD3C47F49D1564619207BA92E7F9B234CD7040E6C1DD1DD51C9D853CF32F53A78A0FA62EF86016D33EF75237550962A186ACC7333B647001C39A7D97C3BF3C0AF7EADBF49EC5AB77677796405724301712F8925549CD30E01A3E3AC86661EE2790591D3F01E3571141A4FE9BD951A83A279C73B8579C4BA
	C5720FDBB87E9A46A7EA7044078F60FD8EAAE7C15D3C9616B35B5C0B76CD8927412FGE9GCBG724F40BE708C5B2FAC69C6390908B94993AE8536B32223AD60A3146586AF22B643F7A3AD5FFFD1BB86089DB775213EB4024EFEGBFC09CC0B237213CA72A8DC77D3AB690BF17B9027D1CF6A65037CB4F686551760670D586150B4E8672113133338C6DDBF576236D705DFF4CAEBBA4B2A68A6D79D4D759D956A70B7B91EFBF14A7GE6GA483E4GAC87A8EEE5BE71779C086A93B4925B5DCD00EB06EF3B2660EF
	FE5E4E25D3744F4BD17DD3572A671FF5EDEF697B074CB4BEE7968A48BFE8BF5A524A66CC32B35A39083E2D7F9115AFB648D6123B088D49DD20B81DG3449G444E9F4A4C4E855C4E73AE24CF78F4F2F9DC554E3F3A26563E82F7B43E62A0DB48F8EBB32171D681F54DG7DG93GB381D2GB2BFE563EDE14F7EC90EB792A2C3C79B31F3CED4BB755B770328FC42B93D31366948BB7AE36D1CA8595DEC4011FE944CECBEEEBE47666393E6641FD6A8BBGE281E6824C87488448EDE37E59F269C8B47F70E3957091
	4B6769CCCA525E4FFA2666275B2372153469716D9B1A1A3E23D1796A02745139ADB1DF373431B9136D1DDA9CDA9742D799D40E55ED7323A1FC13EB734228FC893A7A5C3E29693B9215EF362E3E6D4A5474D5C765CB556D536F482CCF33F81F7EE10AB63C92152F24DD4F06CE8B33212E1D59706870947BB9AA1FC35706EFDBD82EA9165BB0FDE4EAE37B8BA1FC41E3BB25DD0F2FF5F4EAED6E0D4A17212B6FED7F547415C56753557773903E4979B3AB04AF581F3967757A7471815627A567D91FDE9EC3325A4FCF
	FE0DA767573762DC248D78A6G88EEG474319D01E4B653BADF570DC8C6515G459D3077GE886F0G4481A4832481AC87A83C10DE3083207582BFEB467C9214E3AF503CFCBD490F231CF662851293C6796379701EF50164EA51F79461FFC8CE53580EF404EBD7F104BA5E768BAA4D4AA01F25D17EE69DB98E5A4E428B01F5282D9514FE31CE9F4405310934ADB320ED9BA3589DFC7E2D87DBB46F41676E9B6873F59D3EB582B9A7FDB3880F769E8574DD6F1CFEDD84BBB6885BFA431FBF16756A1FBFD6766A1FBF
	56752EA7F810213DF5426A9BFBA31FCF9AFB431FCF5E56975E4E3B7A746D7CE21F3E1DDF6E0BEC67C3FD616C5C0AFD3CF053BA4C6F7349BE3C11B50A0AC86BFE2B207191F2E6FA86F6F3824BE374B98AA84DD1455DD44D4759FABB0BB7391D6AF85CCC5FABBAC347A83177397E703ED9562F6F1B177A51F87239073DE6B9387DAF77EF66F56A267304A2BBD1B388CD0E08789551EB15C37C379A5FC7A4F56178B70861FCCCFC345D2975D18D7751FF894CC707B47EE8276FB6F8CEG44E96A083C5F8648C3B98292
	2BD378D9CFE2A7BB6BF9D2C2B91045D0DE81D05845E25C63B2533D83F075DDC4C78BFD7643D3698A6CA3922F9DB827BD8D6A623BD84E29462D3167F4B5775DA5D3783E14CC7AFDB9E08A5F17C32668FDB9EE8A5F17B71822776567CC117BF2297BF55A56D60B576DEC2D26FD19E7C67E1C95643394DACE6B8AB75E1FB60777D116D95FC73FB30777519F4C51FD7432B93C0F322568BEDAADA19F186C125DCBCF7309EF3E67510E039A6EB357D0204FEA5878177476C0D954D7AB61D9589D68B7A22F5E2B9D7FF59C
	7F2296FF3A16639F74E971AAFD3792E8D7AD35AB854F0D52A05DF5345C5C9D5A17C4DE59F0203CBAAA6F12C52F1DBDD40E871E313A6DFCF1D8EB779E9EB79E551597CFE5A405356F87A3DA3F6D61781BC7836D5EC3711F7A8371FBA9FE4D18D64EDEEE57CF87746C1AC76D4950310B4414BBF00EF889542F6CE6B1A551046E59B844B17730826371A222FB1467247221DC8A508C50F3116426C85E639B38AD962F6804E95B66114DC2EB8F3E03A1B15E9420CF83D80EE4C2DDD58F53A954233BA4A7DBC4136CC04B
	60B13757A9AAB42F3EED2DDAC70FDC197FFE56054E29E9968FA28643EFE9C6F3DB4DFE1EF0F920B1246E737B10BD5AEC4CF917596D74F8E5D809A5C213DB37A9082609D3B09D719D6B39DD28566D33537BB524FE7C5C06A099E7C7E4AF7815564FFF33B8283ECD065D04284824BE27FB9D17CFCE34402F14F7138B379B541A5CC75C7EEA263370ECE910CC1D83969F21FD63D3B4F6480D05EAFA67677B81E7C471BD24FFD3FA5899C8E207DEFD9A2F7F36D92FBE0B57FFE0552BDF496BBF8EB8CBAB3CC46A6BAE31
	EF16B21C3FEE063244F153F18E4D8F65E99C7787B37B361941F13F9198EF96477D7E9A422D04F2FEAF4335880C3718633AB1CE02728447A53991AE8E4A33FA595A7ED84879CACA2FBA7752FAC367A1B9C7B9054FCB32FA49B9CA9E47F7E07CCA8D1E1C2FBCAD31B32A423E7482CF9F3369991C8F490072F20EBB03FD115B8F6D69E7B843AEFCCF844A759CE75D455AED643867DF43F71C211C544F666AE7DD6C6CEDE9BF2239871767F11E922C2FCA84B9A24349985789E59B47BD4E7BC861385FC80CEEBE479D13
	99DD9A47F5E35ED2930C8D9343BD01691A215C43F1A5B8AF6201F28C47DD31B0F953B9CE61F40BB9EE9127DB42F1BB310EFAF3FAC10B196152B1DDBF14BD9C374F4AF8A7B86EB97CBD1F8A65459CF7EA106BB0B37FBEB496685F7F4465FA091D7BBEEBE23F0B11G17A03178DB5D9FF08604CE9B9B9A0EFB6D5291515A0223B9592B6E423EC37C99925B2BF619114CC2993E67819AE5F6AF497FBE3B2B1374607D422AC57A7712B25B2F985AE5B2367D32FADFC0F0BEEDA0BA3FA2B11D3321FE290C7D9BF07E0F5A
	7E3C0164C6C87DEB8131G455A630236BEB0D6A0FB3F132D2D9F8C28C7BE2A257524EDBF5E495A366A474902E3CEEC585FA855D9C81BBA69B3AE6B1EEC4FF363B974AE18A54CDDB09C39C97B7F42F304F196F606732B8E06DB40F14BF05FE07B2C8C57EBE538FAAB6BEB2BE30B3ECE36A2506F920F4CF8BDD6B63EAA2C281D3018D9E42FD7165A6C63681BB2530A68BF4571F5963C4F66748F18717D85D8CAF758506F4B868611F949B6FCFFD1B2D29F59EC2131AD1B6A4E21B081B4F1G9BE9FFA45AC8DC4B667A
	57E663F88C78DCBB537F24A9D07F2D8ED53FD9B08A217D259DD3DA7DD5A053E8D775F75AD57DC457904E194086466E9CF6E24F903BEEA1719F68BD5C2E9F606F9127688F227D95DE979200EE161D4D73277077E1861417F05CD11EFFAB9CE06B4727383FFA202CF0DC8F46213B2B68BEA241BDABF5C6E8379EA0F913216B61FAD7DD370EE83F7F50768B7161CAB97CD8DC0F67CC9C5813B040FCAE6331E8F13B94C2F3994F1785D01F41E95E5A95B8DE121D6AF8393AAB52F8510E85FCEFE2904661A099AF883FF9
	B0F83C88797AE3E0F3BE32EB8750D78DB23B9EF1215F9F386451D25D2B9BE56806D0B90DCD87E46D6FA72BAE3798F2847D5854B3C86FC852672A0DDF48A67AEA4D5A7BBC442E13E6F6A7A4015BB591B43FAF3AD5FFBDE3E67ECA9A541F5F6C779868B98FE8668D2A73ABC363AF5A979ED576A519494E8CA3BBAF026C95A1FDB1E4C663D6F6216B64926D07E9FC2D939C2C3D3F13C37D12EDE1FEA966F429D220DF1E98D2ED1FE1E1366FF0685B3EC6E3FBBD50A0E03637B8027D325F274ADE4AE5F706113DAE02EC
	07A358AF0DF228DF5AE456DEBFEF6F7316407676AA2ACDE72C4C26E9E1ECDA2F31A981E892B4B6A50758743701C0DDF78F2B3ACC03CCD70A0E2E9CA1729CCD8B5135E8ECB2730E443C67C3766F5100707DF90AFCD5666B65A35F853BC8EED153D6022F4378A59ABC59B37619591EF18554F5BA59DD03319134F7C90172D3G4B8172DD6985E5GC6973BE770D529CA08F24F205D6DE911C7E407A68DCE5A17241BE70A84A47EBD769A5B1B4D7E5E43425C1B0CE16F08F73A683E13BE4B1E3DA79BBDC97B8A7D28FD
	3320EEBE4092G418D7BAA008AB7EB5FCB58C6F41BC432AB0795C5B45B70CDF856388E3BBC2A22D1E203346DFFF9AE6810DB13C3266FB6F768582567C09D0CEFC2C393C75F9343723D48474ADC8DCDAAFD4F04670AA0DE42E736B23EFC0FDADFC25F2B60E97468718D73BCF527A754A68760E2GE6E86A48DCFEF48C75A019FBB71851DD98BE775085C8239D5C08FE14DBD6DBD5D1DA332F1C75794CD16D1AD73F7AB33A3F47C2B00B6ACC7128EB9E1171530BDA99C5DFF098324348D8C1F905A14D7DE63CE77ED8
	E2397CBA28FBD7A067E979827B1779C9A80E357D054B829F434F9432B1DC725587846313C64376B036B8288DA99ADBD639CBF3084C2EAB2ABFB32FC57067DE664F6B33F9622A7AFE5FB5D557175D91F4556C81DDFB4A995F2B59AA5FDC5B3B6179EA2B2A88A321FDC1A757782A19658BD78C11DCE321971EDE629B32C674706F0EFA2F7EFA7426202EC7ADC0536FD557A3459B3CC63C70AA6AB37E3B830E9F7D902D6364A22EFAD71034395C203639A952B85FD39D60ABE73E4A77278F5F8B66BB861B5B3277A43B
	68F0DB5F2BA8855E6A9A5DBEE5726AAF2A6F97FBD459A383917A219A75431E2AF29AA773AF2175B8464BE322935955A18E50E80FC2235B0BA3A2A0517D4EB68527A329AE116CA13D43B219C3EF70B9B4432BB707183D2EDE74CEF8EE1399CFAA70107A5B7B54765CE7C6F5965161D34FC0B7DB82757E105F034975C552BB3BDF553B554AF89AFDCC6F13222AF7C08E54F10D53AB91F53CECD2F55CB948F812380E2D922A27771FB46F8D1AF759FC3D5FE0573F87397A8F55C14653E7C6421F355DB222FF56767991
	7DF3773BC6A21F7F5FBB927E7CBFF1C47B7BE4B2761761BCD496740B20C05FG548334A8ECAE540C2239007E92C6685E6028E4C5E7FD682FDFA8CC7FC6FF78361678237F4D0C1DFE145BB739A158B811FC7E1BD9BF6AAF456D41C8022B72A39FF96DD61BC2C8A9FE2F9F0DB707EC41E812EF58E8E579EF1B42F29073CCAC7FBD41F1BBF9DEF69A477D390371BE45F155630CF7A9475DB14EF8D7F2DC810E854D43698552B0434D11D83E554FF119BCE79A4BF1FF42F499D0DE4AF16318CE98C9AFA899E1B812A329
	0272A10EFB0C670DDBB8EEAF66CD0072EC0E1BAEB37B96F01C0367FE97F3DC95262B98057D52A87795266B0432E7146D29D7E3DC8C1493C659989246F7896163E92B576D113D4A984162DFCA533119E795F81F9E9B5F99CE860C441390634987CD3BE865F37BE0407EBB856CC99B0D1C87A2FA2BEC786FDAG2D6007B9815088508AE08318G0883C884C88148GD882308A20E88C56EA00BA0066B1567E3A0972886D27C9EABC113C229DCDA57A5DD2493FDBFAC0560CB116939D615F7529E3AC071D08E9F347E1
	DCG540DB35ADB79DA54B22E972F17C7589B958E287EAF3C0476AABE3A6E917F3E99605FD8109FBF9E593F046FCED94B1784BC73A64577F84079F38670AC19945FFBD6AD5FAA60A93ABC993E58G3E9D40D3BFA93E78C1ADDF8B7074C761A3BE7E2394BEF62E1222474EBCBCC7E508F96DE39EF6E7C2C67D6636D8FCB20F516B64A20EBB61EB25C737A4B7F2944788856C0D394C566A5D1A1CF723F9B2B9C43677CC829973AEEBF2E21703F35E2578EFC5E482FEA957F7CE1363FEE8BC1A3E57B47AB6CF40376D04
	2A6F50C470DE37D48A1FF3A55FBEAD4053B941EC995664CF7F4C73C031937A7B6E57057039297890DB56487A79D3F24E1688744993ACBFF5FB04F30B07F11FAD865AA51C5EA787661812DCEABB1E62394762AB7A6D5820E9C79950A0E06DE83C925C0E53D67DF648E44D857AF6G021BC37279D01E8EC06C79AC4F794D8DE34FC6A13CDFD3C36CB9AE6B1FE374E15DCF81FD56956627C67C9BBE168FC5770D4839E3BB5EDB97DDE5B1FC1D85594277DDE4192273E913057D76967F66945F51A0DF88E41DBC0F47D5
	49D5E2A7BD4345383A2B6AEF87B788C6DB7879FF4A96FD7E375B7477CE5D36503DD31FCDFF6FA45B34FBA7FAB74E067CEC05F52A135339B92E0B6394AC9361AE58A54546716358AE04EF1611C2060F41F60DE2DCA7455D2841F5D15C67ECA806C113EDEEAF5F9F5B70D9085D6120FF878B5358DD905238BF62310DF60F28317BDE0E6B92D83C2E3B6EBCA461BBB2C53E4BD34CFBEE46794D562B41F94FED985FF3B5B86F79B05FBF5820AE75AABB5B3AA3E05D2E3A86FB3AEB1157A7B2E6ABF546F1ED40B8A63807
	65D03AC764D03AE7AC21F44FD9C269BE3606529D340652FDD107EE268E5DA30321F4338735F4A4E67E941F8511B8B14794B11FD0CF12CFFAFB29707B2B56A1755D67D5F53DBF9EA91F92BE4F95C9575DBE757D3B0A2A6BE1A9D23B6A7691E5E4AFD9E6E3FB494EEBECFDDCBC2C4AFDE5C4151BEB190CDC4277953F4A7729BCF9FB8AF83EA36EDA24DC4463E36A7B32F1D5D74924F4913E228915CF0C5836DA1A63A3B64E66B6AE60BE2B3D225A53F1D5157BF6C439B5FB353943810BA030B5D1FCDF73B78FF068A5
	78927F9A954FC2AF0DC16FE872AC9FE85E137F9FF2AE8B7F21BA48FCFEC8A7D6FDBDA0D691DBBE32E9E32190835FCDB11359632F8D7896A9831E46C971FDB7002F95F824A8FC44F623AD34BDA7EC2131ED27CECC7DD0A726266950AD8A20A3FF03F969F88CAC2CC94B04BCC5B0D4D7A9C28ECAB9890633279461AD6D555152325A1AD213DDA9BDF77CC8B954B5E9EA1C22EB2C5402EF357A20EE4BD917DC6AC4FF8A8EA1CBE551ECAB9D211B99C5A82597C5B94AD7DA66033D14037200000D68DEE7A93B58D9CAEF
	F6AA422BA4D78D31740D5099F47836DFBC1954E7ACB6880693422BB6BC9DCE88F923E83E78599FD7FD87F857F15E93F8BB11B9D25B10736C83B986A1979849766A04100B76C8406FA5F2F2E0FB052520FF2B69544C738D89D77101883E3004CB2FD39AD45F9AC003128FEFE9CB599581F535E05B1A00CC798F3F3C6739547F6E7065882F1E905EC297C35AE4AF793BA7923AC8CC7FG6CB121406336C3A56041F09FFE647914B1075CA294DB4E21BFD467035DAF7F15348FDC0C7F4446EEF4DE3C3BD97459945493
	7BDB04DC977ACD35708629F608AE6BEE32555D0FDB1FFD7890EE9D5EF8003D9B54D3E7AEE543236BF6EEBB19B5B7C7B09C93D6BB5C22F4CCB4ABEE6F8921C031411031399D125F73899114BF0A46A4F2DAAE085B421B0F9ADB09EEB8F323877D77CEDF33E44DB103A0783D42EECC093EFFAAB90554E493519FAF123D304F770A20F2872F3B619D657D1F9CFF3347A0E453C6A257977BBDB02AAC22536E980397F2AE01BBE5B39449AE150F98640C4AE3509E65174B965E7F2B4665CBC158B1A18F7F718DB029C825
	BAB36CB4495E1B126E5A7A7A0C4CFFC196FBD0F319648258653BFCE84200705220B633AAEEFA651D8B2B6EFA6865EEEC3A4A2A886F12CE82565DD6596D5CFD58E201E94285C00F01782DDCBC22298C22F943FF7B46130BDEB14188B38AB94E2AAA7CFFB57E3F867DDF0DB15598D3DD835D36DC0878AF385E4F5A0C07A5AE3C0B46109F3D157879144955821F9668379ED24034E2D3CF7887E157C530A8842B889B5A7017E529843311C64086880E786FE016CA74BEBF44B7596CF6C93EF2285BCCB94C6A25FA08FB
	128CD5DB50DFDDF1C925F627E80528E89AABF5321B718D8111469192E9902C8E202919844D1D1326199A870AEA1F619CF902DE64C4B4D74919F279B56D79764A9B4DE14F4813EFA6EB6A7C1B55B3E5125B1DFF956F8F27259714CCE35F7645D7BF924267165B1C224321080E4143AE8965190F0CA932FA5E3270AA126FE3B434125425E13B87B4BC7F87D0CB8788812FB834209FGGB4E1GGD0CB818294G94G88G88GDAFBB0B6812FB834209FGGB4E1GG8CGGGGGGGGGGGGGGGGG
	E2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5A9FGGGG
**end of data**/
}
}
