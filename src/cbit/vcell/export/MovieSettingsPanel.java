package cbit.vcell.export;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cbit.vcell.export.server.*;
import cbit.image.*;
import javax.swing.JCheckBox;
import javax.swing.JSlider;

import org.vcell.util.gui.DialogUtils;
/**
 * This type was created in VisualAge.
 */
public class MovieSettingsPanel extends javax.swing.JPanel implements ExportConstants, java.awt.event.ActionListener {
	private javax.swing.JLabel ivjJLabelDataFormat = null;
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
	private JCheckBox ivjJCheckBoxHideMembraneOutlines = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private JPanel panel;
	private JLabel lblImageScale;
	private JLabel lblMembrScale;
	private JComboBox imageScaleComboBox;
	private JComboBox membrScaleComboBox;
//	private JPanel panel;
//	private JLabel lblImageScale;
//	private JComboBox imageScaleComboBox;
//	private JLabel lblMembrScale;
//	private JComboBox membrScaleComboBox;

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
	if (e.getSource() == getJButtonOK()) 
		connEtoC3(e);
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
 * connEtoC3:  (JButtonOK.action.actionPerformed(java.awt.event.ActionEvent) --> MovieSettingsPanel.fireJButtonOKAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		try {
			setDuration();
		} catch (Exception e) {
			e.printStackTrace();
			//reset value to last good
			DialogUtils.showErrorDialog(this,"'Duration' value error: ('"+getJTextFieldInput().getText()+
					"')\nRe-enter a 'Duration' value between "+Double.toString(MOVIE_DURATION_MIN_SECONDS)+" and "+Double.toString(MOVIE_DURATION_MAX_SECONDS));
			//Do not continue with OK operation
			return;
		}
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
			ivjJRadioButtonCompressed.setText("Compression");
			ivjJRadioButtonCompressed.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					compressionSliderEnable(e.getStateChange() == ItemEvent.SELECTED);
					//getJSliderCompression().setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				}
			});
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
private JCheckBox getJCheckBoxHideMembraneOutlines() {
	if (ivjJCheckBoxHideMembraneOutlines == null) {
		try {
			ivjJCheckBoxHideMembraneOutlines = new JCheckBox();
			ivjJCheckBoxHideMembraneOutlines.setName("JRadioButtonHideMembraneOutlines");
			ivjJCheckBoxHideMembraneOutlines.setText("Hide Membrane Outline");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxHideMembraneOutlines;
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
			ivjJRadioButtonOverlay.setText("Multiple variables (single movie)");
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
private static final String MESH_MODE_TEXT = "from 'View Data' zoom";
private JSlider jSliderCompression;
public MovieSpecs getMovieSpecs() {
	int imageScale = 0;
	int scaleMode = ImagePaneModel.MESH_MODE;
	if(!getImageScaleComboBox().getSelectedItem().equals(MESH_MODE_TEXT)){
		imageScale = Integer.valueOf(getImageScaleComboBox().getSelectedItem().toString());
		scaleMode = ImagePaneModel.NORMAL_MODE;
	}
	return new MovieSpecs(
		getSelectedDuration(),
		getJRadioButtonOverlay().isSelected(),
		getDisplayPreferences(),
		getEncodingFormat(),
		getJComboBox1().getSelectedIndex(),
		getJCheckBoxHideMembraneOutlines().isSelected(),
		imageScale,
		Integer.valueOf(getMembrScaleComboBox().getSelectedItem().toString()),
		scaleMode,
		(getJRadioButtonCompressed().isSelected()?FormatSpecificSpecs.CODEC_JPEG:FormatSpecificSpecs.CODEC_NONE),
		(getJRadioButtonCompressed().isSelected()?(float)getJSliderCompression().getValue()/(float)getJSliderCompression().getMaximum():1.0f)
		
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
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0};
		setLayout(gridBagLayout);
		setSize(291, 471);

		java.awt.GridBagConstraints constraintsJLabelDataFormat = new java.awt.GridBagConstraints();
		constraintsJLabelDataFormat.gridx = 0; constraintsJLabelDataFormat.gridy = 0;
		constraintsJLabelDataFormat.gridwidth = 2;
		constraintsJLabelDataFormat.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelDataFormat.insets = new Insets(10, 5, 5, 0);
		add(getJLabelDataFormat(), constraintsJLabelDataFormat);
		GridBagConstraints gbc_jSliderCompression = new GridBagConstraints();
		gbc_jSliderCompression.fill = GridBagConstraints.HORIZONTAL;
		gbc_jSliderCompression.insets = new Insets(0, 0, 5, 0);
		gbc_jSliderCompression.gridx = 1;
		gbc_jSliderCompression.gridy = 2;
		add(getJSliderCompression(), gbc_jSliderCompression);

		java.awt.GridBagConstraints constraintsJLabelDuration = new java.awt.GridBagConstraints();
		constraintsJLabelDuration.gridx = 0; constraintsJLabelDuration.gridy = 3;
		constraintsJLabelDuration.gridwidth = 2;
		constraintsJLabelDuration.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelDuration.insets = new Insets(10, 5, 5, 0);
		add(getJLabelDuration(), constraintsJLabelDuration);

		java.awt.GridBagConstraints constraintsJTextFieldInput = new java.awt.GridBagConstraints();
		constraintsJTextFieldInput.gridx = 0; constraintsJTextFieldInput.gridy = 4;
		constraintsJTextFieldInput.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldInput.weightx = 0.5;
		constraintsJTextFieldInput.insets = new Insets(5, 10, 5, 5);
		add(getJTextFieldInput(), constraintsJTextFieldInput);

		java.awt.GridBagConstraints constraintsJRadioButtonUncompressed = new java.awt.GridBagConstraints();
		constraintsJRadioButtonUncompressed.gridx = 0; constraintsJRadioButtonUncompressed.gridy = 1;
		constraintsJRadioButtonUncompressed.gridwidth = 2;
		constraintsJRadioButtonUncompressed.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonUncompressed.insets = new Insets(0, 10, 5, 0);
		add(getJRadioButtonUncompressed(), constraintsJRadioButtonUncompressed);

		java.awt.GridBagConstraints constraintsJRadioButtonCompressed = new java.awt.GridBagConstraints();
		constraintsJRadioButtonCompressed.gridx = 0; constraintsJRadioButtonCompressed.gridy = 2;
		constraintsJRadioButtonCompressed.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonCompressed.insets = new Insets(0, 10, 5, 5);
		add(getJRadioButtonCompressed(), constraintsJRadioButtonCompressed);

		java.awt.GridBagConstraints constraintsJLabelComposition = new java.awt.GridBagConstraints();
		constraintsJLabelComposition.gridx = 0; constraintsJLabelComposition.gridy = 5;
		constraintsJLabelComposition.gridwidth = 2;
		constraintsJLabelComposition.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelComposition.insets = new Insets(10, 5, 5, 0);
		add(getJLabelComposition(), constraintsJLabelComposition);

		java.awt.GridBagConstraints constraintsJRadioButtonOverlay = new java.awt.GridBagConstraints();
		constraintsJRadioButtonOverlay.gridx = 0; constraintsJRadioButtonOverlay.gridy = 7;
		constraintsJRadioButtonOverlay.gridwidth = 2;
		constraintsJRadioButtonOverlay.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonOverlay.insets = new Insets(0, 10, 5, 0);
		add(getJRadioButtonOverlay(), constraintsJRadioButtonOverlay);

		java.awt.GridBagConstraints constraintsJRadioButtonSeparate = new java.awt.GridBagConstraints();
		constraintsJRadioButtonSeparate.gridx = 0; constraintsJRadioButtonSeparate.gridy = 6;
		constraintsJRadioButtonSeparate.gridwidth = 2;
		constraintsJRadioButtonSeparate.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJRadioButtonSeparate.insets = new Insets(0, 10, 5, 0);
		add(getJRadioButtonSeparate(), constraintsJRadioButtonSeparate);

		java.awt.GridBagConstraints constraintsJLabelMirroring = new java.awt.GridBagConstraints();
		constraintsJLabelMirroring.gridx = 0; constraintsJLabelMirroring.gridy = 9;
		constraintsJLabelMirroring.gridwidth = 2;
		constraintsJLabelMirroring.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelMirroring.insets = new Insets(10, 5, 5, 0);
		add(getJLabelMirroring(), constraintsJLabelMirroring);

		java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
		constraintsJComboBox1.gridx = 0; constraintsJComboBox1.gridy = 10;
		constraintsJComboBox1.gridwidth = 2;
		constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBox1.insets = new Insets(5, 10, 5, 0);
		add(getJComboBox1(), constraintsJComboBox1);

		java.awt.GridBagConstraints constraintsJRadioButtonHideMembraneOutlines = new java.awt.GridBagConstraints();
		constraintsJRadioButtonHideMembraneOutlines.gridwidth = 2;
		constraintsJRadioButtonHideMembraneOutlines.anchor = GridBagConstraints.WEST;
		constraintsJRadioButtonHideMembraneOutlines.gridx = 0; constraintsJRadioButtonHideMembraneOutlines.gridy = 8;
		constraintsJRadioButtonHideMembraneOutlines.insets = new Insets(15, 4, 4, 4);
		add(getJCheckBoxHideMembraneOutlines(), constraintsJRadioButtonHideMembraneOutlines);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 5, 0);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 11;
		add(getPanel(), gbc);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 12;
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
private static final double MOVIE_DURATION_MIN_SECONDS = .1;
private static final double MOVIE_DURATION_MAX_SECONDS = 1000.0;
public void setDuration() {
	double seconds;
	String typed = getJTextFieldInput().getText();
		seconds = new Double(typed).doubleValue();
		if (seconds < MOVIE_DURATION_MIN_SECONDS) seconds = MOVIE_DURATION_MIN_SECONDS;
		if (seconds > MOVIE_DURATION_MAX_SECONDS) seconds = MOVIE_DURATION_MAX_SECONDS;
		int milliseconds = (int)(seconds * 1000);
		seconds = (double) milliseconds / 1000;
		getJTextFieldInput().setText(Double.toString(seconds));
		setSelectedDuration(milliseconds);
}

/**
 * This method was created in VisualAge.
 * @param newValue int
 */
private void setSelectedDuration(int newValue) {
	this.selectedDuration = newValue;
}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[]{46, 0, 0};
			gridBagLayout.rowHeights = new int[]{14, 0, 0};
			gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
			gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			panel.setLayout(gridBagLayout);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(4,4,4,4);
			gbc.anchor = GridBagConstraints.NORTHEAST;
			gbc.gridx = 0;
			gbc.gridy = 0;
			panel.add(getLblImageScale(), gbc);
			GridBagConstraints gbc_2 = new GridBagConstraints();
			gbc_2.insets = new Insets(4,4,4,4);
			gbc_2.fill = GridBagConstraints.HORIZONTAL;
			gbc_2.gridx = 1;
			gbc_2.gridy = 0;
			panel.add(getImageScaleComboBox(), gbc_2);
			GridBagConstraints gbc_1 = new GridBagConstraints();
			gbc_1.anchor = GridBagConstraints.EAST;
			gbc_1.insets = new Insets(4,4,4,4);
			gbc_1.gridx = 0;
			gbc_1.gridy = 1;
			panel.add(getLblMembrScale(), gbc_1);
			GridBagConstraints gbc_3 = new GridBagConstraints();
			gbc_3.fill = GridBagConstraints.HORIZONTAL;
			gbc_3.insets = new Insets(4,4,4,4);
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
	private JLabel getLblMembrScale() {
		if (lblMembrScale == null) {
			lblMembrScale = new JLabel("<html>Membrane<br> Thickness</html>");
		}
		return lblMembrScale;
	}
	private JComboBox getImageScaleComboBox() {
		if (imageScaleComboBox == null) {
			imageScaleComboBox = new JComboBox();
		}
		return imageScaleComboBox;
	}
	private JComboBox getMembrScaleComboBox() {
		if (membrScaleComboBox == null) {
			membrScaleComboBox = new JComboBox();
		}
		return membrScaleComboBox;
	}
	
	
	public void setSingleMovieOnly(boolean bSingleMovieOnly){
		if(bSingleMovieOnly){
			getJRadioButtonOverlay().setSelected(true);
			getJRadioButtonSeparate().setEnabled(false);
		}else{
			getJRadioButtonSeparate().setEnabled(true);
		}
	}
	private JSlider getJSliderCompression() {
		if (jSliderCompression == null) {
			jSliderCompression = new JSlider();
			jSliderCompression.setValue(10);
			jSliderCompression.setMajorTickSpacing(1);
			jSliderCompression.setMaximum(10);
			jSliderCompression.setPaintTicks(true);
			Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put( new Integer( 0 ), new JLabel("high") );
			labelTable.put( new Integer( jSliderCompression.getMaximum()/2 ), new JLabel("medium") );
			labelTable.put( new Integer( jSliderCompression.getMaximum() ), new JLabel("lossless") );
			jSliderCompression.setLabelTable( labelTable );
			jSliderCompression.setPaintLabels(true);
			jSliderCompression.setEnabled(false);
			compressionSliderEnable(false);
		}
		return jSliderCompression;
	}
	private void compressionSliderEnable(boolean bEnable){
		getJSliderCompression().setEnabled(bEnable);
		Dictionary<Integer, JLabel> labeltable = getJSliderCompression().getLabelTable();
		Enumeration<JLabel> jLabelEnum = labeltable.elements();
		while(jLabelEnum.hasMoreElements()){
			jLabelEnum.nextElement().setEnabled(bEnable);
		}
		getJSliderCompression().repaint();
	}
}
