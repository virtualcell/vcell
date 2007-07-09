package cbit.image.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.*;

import org.vcell.util.Range;
/**
 * This type was created in VisualAge.
 */
public class ScalePanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.beans.PropertyChangeListener {
	private javax.swing.JRadioButton ivjCheckboxAuto = null;
	private javax.swing.JRadioButton ivjCheckboxManual = null;
	private javax.swing.JLabel ivjLabelMax = null;
	private javax.swing.JLabel ivjLabelMin = null;
	private javax.swing.JTextField ivjTextFieldMax = null;
	private javax.swing.JTextField ivjTextFieldMin = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private boolean fieldAutoMode = true;
	private javax.swing.JLabel ivjLabelRealMax = null;
	private javax.swing.JLabel ivjLabelRealMin = null;
	private double[] fieldRealMinMax = new double[2];
	private double[] fieldInputMinMax = new double[2];
	private Range fieldScaleRange = new Range();
	private Range fieldDataRange = new Range();
	private String fieldColorMode = new String();
	private javax.swing.JRadioButton ivjCheckboxGrayscale = null;
	private javax.swing.JRadioButton ivjCheckboxBlueToRed = null;
	private javax.swing.ButtonGroup ivjButtonGroup1 = null;
	private javax.swing.ButtonGroup ivjButtonGroup2 = null;
	private javax.swing.JLabel ivjJLabel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ScalePanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getTextFieldMin()) 
		connEtoC2(e);
	if (e.getSource() == getTextFieldMax()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * connEtoC2:  (TextFieldMin.action.actionPerformed(java.awt.event.ActionEvent) --> ScalePanel.setScaleParameters(Ljava.lang.String;Ljava.lang.String;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setScaleParameters(getTextFieldMin().getText(), getTextFieldMax().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (TextFieldMax.action.actionPerformed(java.awt.event.ActionEvent) --> ScalePanel.setScaleParameters(Ljava.lang.String;Ljava.lang.String;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setScaleParameters(getTextFieldMin().getText(), getTextFieldMax().getText());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (ScalePanel.scaleRange --> ScalePanel.resetTextFields(Lcbit.image.Range;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.resetTextFields(this.getScaleRange());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (ScalePanel.dataRange --> ScalePanel.refreshMinMaxLabels()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refreshMinMaxLabels();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (ScalePanel.initialize() --> ButtonGroup2.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup2().add(getCheckboxGrayscale());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM10:  (CheckboxAuto.item.itemStateChanged(java.awt.event.ItemEvent) --> LabelMin.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM10(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getLabelMin().setEnabled(getCheckboxManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM11:  (CheckboxBlackAndSpectrum.item.itemStateChanged(java.awt.event.ItemEvent) --> ScalePanel.colorMode)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM11(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setColorMode("BLUE_TO_RED");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM12:  (CheckboxAuto.item.itemStateChanged(java.awt.event.ItemEvent) --> LabelMax.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM12(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getLabelMax().setEnabled(getCheckboxManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM13:  (CheckboxAuto.item.itemStateChanged(java.awt.event.ItemEvent) --> ScalePanel.autoMode)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM13(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setAutoMode(true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM14:  (CheckboxManual.item.itemStateChanged(java.awt.event.ItemEvent) --> ScalePanel.autoMode)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM14(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setAutoMode(false);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM15:  (ScalePanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM15() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup1().add(getCheckboxManual());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM16:  (ScalePanel.initialize() --> ButtonGroup1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM16() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup1().add(getCheckboxAuto());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM2:  (ScalePanel.initialize() --> ButtonGroup2.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup2().add(getCheckboxBlueToRed());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM3:  (CheckboxGrayscale.item.itemStateChanged(java.awt.event.ItemEvent) --> ScalePanel.colorMode)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.setColorMode("GRAYSCALE");
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM4:  (CheckboxManual.item.itemStateChanged(java.awt.event.ItemEvent) --> TextFieldMin.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTextFieldMin().setEnabled(getCheckboxManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM5:  (CheckboxManual.item.itemStateChanged(java.awt.event.ItemEvent) --> LabelMin.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getLabelMin().setEnabled(getCheckboxManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM6:  (CheckboxManual.item.itemStateChanged(java.awt.event.ItemEvent) --> TextFieldMax.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTextFieldMax().setEnabled(getCheckboxManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM7:  (CheckboxManual.item.itemStateChanged(java.awt.event.ItemEvent) --> LabelMax.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM7(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getLabelMax().setEnabled(getCheckboxManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM8:  (CheckboxAuto.item.itemStateChanged(java.awt.event.ItemEvent) --> TextFieldMin.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM8(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTextFieldMin().setEnabled(getCheckboxManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM9:  (CheckboxAuto.item.itemStateChanged(java.awt.event.ItemEvent) --> TextFieldMax.enabled)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM9(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getTextFieldMax().setEnabled(getCheckboxManual().isSelected());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Gets the autoMode property (boolean) value.
 * @return The autoMode property value.
 * @see #setAutoMode
 */
public boolean getAutoMode() {
	return fieldAutoMode;
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
 * Return the CheckboxAuto property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCheckboxAuto() {
	if (ivjCheckboxAuto == null) {
		try {
			ivjCheckboxAuto = new javax.swing.JRadioButton();
			ivjCheckboxAuto.setName("CheckboxAuto");
			ivjCheckboxAuto.setSelected(true);
			ivjCheckboxAuto.setText("Auto");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCheckboxAuto;
}


/**
 * Return the CheckboxBlueToRed property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCheckboxBlueToRed() {
	if (ivjCheckboxBlueToRed == null) {
		try {
			ivjCheckboxBlueToRed = new javax.swing.JRadioButton();
			ivjCheckboxBlueToRed.setName("CheckboxBlueToRed");
			ivjCheckboxBlueToRed.setText("Blue to Red");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCheckboxBlueToRed;
}


/**
 * Return the CheckboxGrayscale property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCheckboxGrayscale() {
	if (ivjCheckboxGrayscale == null) {
		try {
			ivjCheckboxGrayscale = new javax.swing.JRadioButton();
			ivjCheckboxGrayscale.setName("CheckboxGrayscale");
			ivjCheckboxGrayscale.setSelected(true);
			ivjCheckboxGrayscale.setText("Grayscale");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCheckboxGrayscale;
}


/**
 * Return the CheckboxManual property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCheckboxManual() {
	if (ivjCheckboxManual == null) {
		try {
			ivjCheckboxManual = new javax.swing.JRadioButton();
			ivjCheckboxManual.setName("CheckboxManual");
			ivjCheckboxManual.setText("Manual");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCheckboxManual;
}


/**
 * Gets the colorMode property (java.lang.String) value.
 * @return The colorMode property value.
 * @see #setColorMode
 */
public String getColorMode() {
	return fieldColorMode;
}


/**
 * Gets the dataRange property (cbit.image.Range) value.
 * @return The dataRange property value.
 * @see #setDataRange
 */
public Range getDataRange() {
	return fieldDataRange;
}


/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel() {
	if (ivjJLabel == null) {
		try {
			ivjJLabel = new javax.swing.JLabel();
			ivjJLabel.setName("JLabel");
			ivjJLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabel.setText("Min:");
			ivjJLabel.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabel1.setText("Max:");
			ivjJLabel1.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Colors:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}


/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Scaling:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}


/**
 * Return the LabelMax property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabelMax() {
	if (ivjLabelMax == null) {
		try {
			ivjLabelMax = new javax.swing.JLabel();
			ivjLabelMax.setName("LabelMax");
			ivjLabelMax.setFont(new java.awt.Font("dialog", 0, 12));
			ivjLabelMax.setText("Max:");
			ivjLabelMax.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabelMax;
}

/**
 * Return the LabelMin property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabelMin() {
	if (ivjLabelMin == null) {
		try {
			ivjLabelMin = new javax.swing.JLabel();
			ivjLabelMin.setName("LabelMin");
			ivjLabelMin.setFont(new java.awt.Font("dialog", 0, 12));
			ivjLabelMin.setText("Min:");
			ivjLabelMin.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabelMin;
}

/**
 * Return the LabelRealMax property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabelRealMax() {
	if (ivjLabelRealMax == null) {
		try {
			ivjLabelRealMax = new javax.swing.JLabel();
			ivjLabelRealMax.setName("LabelRealMax");
			ivjLabelRealMax.setText(" ");
			ivjLabelRealMax.setMaximumSize(new java.awt.Dimension(3, 19));
			ivjLabelRealMax.setPreferredSize(new java.awt.Dimension(3, 19));
			ivjLabelRealMax.setFont(new java.awt.Font("dialog", 0, 12));
			ivjLabelRealMax.setEnabled(true);
			ivjLabelRealMax.setMinimumSize(new java.awt.Dimension(3, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabelRealMax;
}

/**
 * Method generated to support the promotion of the labelRealMaxText attribute.
 * @return java.lang.String
 */
public String getLabelRealMaxText() {
		return getLabelRealMax().getText();
}


/**
 * Return the LabelRealMin property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabelRealMin() {
	if (ivjLabelRealMin == null) {
		try {
			ivjLabelRealMin = new javax.swing.JLabel();
			ivjLabelRealMin.setName("LabelRealMin");
			ivjLabelRealMin.setText(" ");
			ivjLabelRealMin.setMaximumSize(new java.awt.Dimension(3, 19));
			ivjLabelRealMin.setPreferredSize(new java.awt.Dimension(3, 19));
			ivjLabelRealMin.setFont(new java.awt.Font("dialog", 0, 12));
			ivjLabelRealMin.setEnabled(true);
			ivjLabelRealMin.setMinimumSize(new java.awt.Dimension(3, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabelRealMin;
}

/**
 * Method generated to support the promotion of the labelRealMinText attribute.
 * @return java.lang.String
 */
public String getLabelRealMinText() {
		return getLabelRealMin().getText();
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the scaleRange property (cbit.image.Range) value.
 * @return The scaleRange property value.
 * @see #setScaleRange
 */
public Range getScaleRange() {
	return fieldScaleRange;
}


/**
 * Return the TextFieldMax property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTextFieldMax() {
	if (ivjTextFieldMax == null) {
		try {
			ivjTextFieldMax = new javax.swing.JTextField();
			ivjTextFieldMax.setName("TextFieldMax");
			ivjTextFieldMax.setEnabled(false);
			ivjTextFieldMax.setColumns(8);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextFieldMax;
}


/**
 * Return the TextFieldMin property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTextFieldMin() {
	if (ivjTextFieldMin == null) {
		try {
			ivjTextFieldMin = new javax.swing.JTextField();
			ivjTextFieldMin.setName("TextFieldMin");
			ivjTextFieldMin.setEnabled(false);
			ivjTextFieldMin.setColumns(8);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextFieldMin;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addPropertyChangeListener(this);
	getCheckboxGrayscale().addItemListener(this);
	getCheckboxBlueToRed().addItemListener(this);
	getCheckboxAuto().addItemListener(this);
	getCheckboxManual().addItemListener(this);
	getTextFieldMin().addActionListener(this);
	getTextFieldMax().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ScalePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(120, 247);

		java.awt.GridBagConstraints constraintsCheckboxAuto = new java.awt.GridBagConstraints();
		constraintsCheckboxAuto.gridx = 0; constraintsCheckboxAuto.gridy = 1;
		constraintsCheckboxAuto.gridwidth = 2;
		constraintsCheckboxAuto.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsCheckboxAuto.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCheckboxAuto.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getCheckboxAuto(), constraintsCheckboxAuto);

		java.awt.GridBagConstraints constraintsCheckboxManual = new java.awt.GridBagConstraints();
		constraintsCheckboxManual.gridx = 0; constraintsCheckboxManual.gridy = 4;
		constraintsCheckboxManual.gridwidth = 2;
		constraintsCheckboxManual.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsCheckboxManual.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCheckboxManual.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getCheckboxManual(), constraintsCheckboxManual);

		java.awt.GridBagConstraints constraintsTextFieldMin = new java.awt.GridBagConstraints();
		constraintsTextFieldMin.gridx = 1; constraintsTextFieldMin.gridy = 5;
		constraintsTextFieldMin.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTextFieldMin.anchor = java.awt.GridBagConstraints.EAST;
		constraintsTextFieldMin.insets = new java.awt.Insets(0, 0, 0, 5);
		add(getTextFieldMin(), constraintsTextFieldMin);

		java.awt.GridBagConstraints constraintsTextFieldMax = new java.awt.GridBagConstraints();
		constraintsTextFieldMax.gridx = 1; constraintsTextFieldMax.gridy = 6;
		constraintsTextFieldMax.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTextFieldMax.anchor = java.awt.GridBagConstraints.EAST;
		constraintsTextFieldMax.weightx = 1.0;
		constraintsTextFieldMax.insets = new java.awt.Insets(0, 0, 5, 5);
		add(getTextFieldMax(), constraintsTextFieldMax);

		java.awt.GridBagConstraints constraintsLabelMin = new java.awt.GridBagConstraints();
		constraintsLabelMin.gridx = 0; constraintsLabelMin.gridy = 5;
		constraintsLabelMin.anchor = java.awt.GridBagConstraints.WEST;
		constraintsLabelMin.insets = new java.awt.Insets(0, 15, 0, 5);
		add(getLabelMin(), constraintsLabelMin);

		java.awt.GridBagConstraints constraintsLabelMax = new java.awt.GridBagConstraints();
		constraintsLabelMax.gridx = 0; constraintsLabelMax.gridy = 6;
		constraintsLabelMax.anchor = java.awt.GridBagConstraints.WEST;
		constraintsLabelMax.insets = new java.awt.Insets(0, 15, 0, 5);
		add(getLabelMax(), constraintsLabelMax);

		java.awt.GridBagConstraints constraintsLabelRealMin = new java.awt.GridBagConstraints();
		constraintsLabelRealMin.gridx = 1; constraintsLabelRealMin.gridy = 2;
		constraintsLabelRealMin.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsLabelRealMin.insets = new java.awt.Insets(0, 0, 0, 5);
		add(getLabelRealMin(), constraintsLabelRealMin);

		java.awt.GridBagConstraints constraintsLabelRealMax = new java.awt.GridBagConstraints();
		constraintsLabelRealMax.gridx = 1; constraintsLabelRealMax.gridy = 3;
		constraintsLabelRealMax.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsLabelRealMax.insets = new java.awt.Insets(0, 0, 0, 5);
		add(getLabelRealMax(), constraintsLabelRealMax);

		java.awt.GridBagConstraints constraintsCheckboxGrayscale = new java.awt.GridBagConstraints();
		constraintsCheckboxGrayscale.gridx = 0; constraintsCheckboxGrayscale.gridy = 8;
		constraintsCheckboxGrayscale.gridwidth = 2;
		constraintsCheckboxGrayscale.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsCheckboxGrayscale.insets = new java.awt.Insets(0, 10, 0, 5);
		add(getCheckboxGrayscale(), constraintsCheckboxGrayscale);

		java.awt.GridBagConstraints constraintsCheckboxBlueToRed = new java.awt.GridBagConstraints();
		constraintsCheckboxBlueToRed.gridx = 0; constraintsCheckboxBlueToRed.gridy = 9;
		constraintsCheckboxBlueToRed.gridwidth = 2;
		constraintsCheckboxBlueToRed.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsCheckboxBlueToRed.insets = new java.awt.Insets(0, 10, 5, 5);
		add(getCheckboxBlueToRed(), constraintsCheckboxBlueToRed);

		java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
		constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 0;
		constraintsJLabel3.gridwidth = 2;
		constraintsJLabel3.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJLabel3.weightx = 1.0;
		constraintsJLabel3.insets = new java.awt.Insets(5, 5, 0, 5);
		add(getJLabel3(), constraintsJLabel3);

		java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
		constraintsJLabel.gridx = 0; constraintsJLabel.gridy = 2;
		constraintsJLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel.insets = new java.awt.Insets(0, 15, 0, 5);
		add(getJLabel(), constraintsJLabel);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 3;
		constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel1.insets = new java.awt.Insets(0, 15, 0, 5);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
		constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 7;
		constraintsJLabel2.gridwidth = 2;
		constraintsJLabel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJLabel2.weightx = 1.0;
		constraintsJLabel2.insets = new java.awt.Insets(0, 5, 0, 5);
		add(getJLabel2(), constraintsJLabel2);
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM15();
		connEtoM16();
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
	if (e.getSource() == getCheckboxGrayscale()) 
		connEtoM3(e);
	if (e.getSource() == getCheckboxBlueToRed()) 
		connEtoM11(e);
	if (e.getSource() == getCheckboxAuto()) 
		connEtoM13(e);
	if (e.getSource() == getCheckboxManual()) 
		connEtoM14(e);
	if (e.getSource() == getCheckboxAuto()) 
		connEtoM10(e);
	if (e.getSource() == getCheckboxManual()) 
		connEtoM5(e);
	if (e.getSource() == getCheckboxAuto()) 
		connEtoM12(e);
	if (e.getSource() == getCheckboxManual()) 
		connEtoM7(e);
	if (e.getSource() == getCheckboxAuto()) 
		connEtoM8(e);
	if (e.getSource() == getCheckboxManual()) 
		connEtoM4(e);
	if (e.getSource() == getCheckboxAuto()) 
		connEtoM9(e);
	if (e.getSource() == getCheckboxManual()) 
		connEtoM6(e);
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new Frame();
		}
		ScalePanel aScalePanel;
		aScalePanel = new ScalePanel();
		frame.add("Center", aScalePanel);
		frame.setSize(aScalePanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Panel");
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
	if (evt.getSource() == this && (evt.getPropertyName().equals("scaleRange"))) 
		connEtoC4(evt);
	if (evt.getSource() == this && (evt.getPropertyName().equals("dataRange"))) 
		connEtoC5(evt);
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void refreshMinMaxLabels() {
	getLabelRealMin().setText(String.valueOf(getDataRange().getMin()));
	getLabelRealMax().setText(String.valueOf(getDataRange().getMax()));
	return;
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Comment
 */
private void resetTextFields(Range scaleRange) {
	getTextFieldMin().setText(String.valueOf(scaleRange.getMin()));
	getTextFieldMax().setText(String.valueOf(scaleRange.getMax()));
	return;
}


/**
 * Sets the autoMode property (boolean) value.
 * @param autoMode The new value for the property.
 * @see #getAutoMode
 */
public void setAutoMode(boolean autoMode) {
	boolean oldValue = fieldAutoMode;
	fieldAutoMode = autoMode;
	firePropertyChange("autoMode", new Boolean(oldValue), new Boolean(autoMode));
}


/**
 * Sets the colorMode property (java.lang.String) value.
 * @param colorMode The new value for the property.
 * @see #getColorMode
 */
public void setColorMode(String colorMode) {
	String oldValue = fieldColorMode;
	fieldColorMode = colorMode;
	firePropertyChange("colorMode", oldValue, colorMode);
}


/**
 * Sets the dataRange property (cbit.image.Range) value.
 * @param dataRange The new value for the property.
 * @see #getDataRange
 */
public void setDataRange(Range dataRange) {
	Range oldValue = fieldDataRange;
	fieldDataRange = dataRange;
	firePropertyChange("dataRange", oldValue, dataRange);
}


/**
 * Method generated to support the promotion of the labelRealMaxText attribute.
 * @param arg1 java.lang.String
 */
public void setLabelRealMaxText(String arg1) {
		getLabelRealMax().setText(arg1);
}


/**
 * Method generated to support the promotion of the labelRealMinText attribute.
 * @param arg1 java.lang.String
 */
public void setLabelRealMinText(String arg1) {
		getLabelRealMin().setText(arg1);
}


/**
 * This method was created in VisualAge.
 * @param minmax int
 * @param inputValue java.lang.String
 */
public void setScaleParameters(String inputMin, String inputMax) {
	try {
		double min = Math.max(0, Double.valueOf(inputMin).doubleValue());
		double max = Math.max(0, Double.valueOf(inputMax).doubleValue());
		if (min > max) min = 0;
		setScaleRange(new Range(min, max));
	} catch (NumberFormatException e) {
		// put back existing values
		resetTextFields(getScaleRange());
	}
	return;
}


/**
 * Sets the scaleRange property (cbit.image.Range) value.
 * @param scaleRange The new value for the property.
 * @see #getScaleRange
 */
public void setScaleRange(Range scaleRange) {
	Range oldValue = fieldScaleRange;
	fieldScaleRange = scaleRange;
	firePropertyChange("scaleRange", oldValue, scaleRange);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G740171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DD8D5D55630D35620E2D9E9E9D911BA8D969AD318CCB1E50525C90905E5C5939AB5763ECEB1CF763E54584C61020ADED4ACADB4ACAACB9C91D129CC2D7C419F948515900B12433D876E0D7B673D878105FC575E7B2CFD4EBD77DCAE71BE744C77FCAF4F33B8E72F3D56DEBF7BEF1D3D77B9D7D871C1447C8C538AC14818AFA8FFDFE48842705F88423F5CE31EC35CA0D70DD9507CFD8830DC88F3E7
	41B582722ADF29B1678A678FE7C3BA8652F9FF29B13F8B77EF8BFD8E573641CD90B9A51057BA645A11DD4BC97E8E65AC2469BB4AF3603A94A096F0641A65A672271467A978E4850F10E1920484CA73E2F93E02BB8869FA00B00061EEC69F8357C5D472A55B37A9F4F5D7C48AF39F761A1610F2B4E5A22C70B05EED4A35C0F8A3A345C3E4ADBB142370F8A09D89C00C4F94AABA5E036B4E2A66262FAD8DF639586E941B246D5BCFD8C407D4A53AA407D0F706701FBC14CD6985E1AD24E7D35C4DB6D2E6845C4782A4
	F27CE4BB4197F9EB4CE5GF5DE34632BF5BB95BDEB4F0F97D21BA6AEDA96448E594B749F6ED56D983A3EA1F39575F9FE9D716D8C481B87106B2BB11782D48348G6F513A7E787BFF41F5F7033C576DF43ADD3BBD1EBD8E2B77085BAAB960FE7BF648D1F047A50B5BEB9584D61FAB8F9599685395B03E0FFFB19F73C964D62C5B2863E30564BB2EB6ADB470C9F264F9D3380F79E214CF758913DD97D4F63C4F3F6D794BBEA9E8E58BAFEDD9B85FC0F67E93DB964ED04A1923113DD6A6F53D1A56F58E5CE7A83EE078
	CFA95E264133F698C55B8CC977013CD4995BC66DD25425A2EB04103B799AC3BF8C3DBF5CF4300569D05F2257E5F4B3A9BB3C0549AC673ABCC17105E7D5BC53E5AA55112463A1AF373D464C5A57A7BF12FED9F73E465C86B088A092E092C0AAC07AF9ECE30E6FB7F5330DD509DEF9CF0B5DE1153C82637D246E9D38CAED32D7ACF779E451E511EAC42FDDECF0C8BEC121A92EA3BEE870635BCA5B3E85E49C10DC12D7146DEE9714AEBAA5D9727A984D4E3A650446AB515CEA3BD38281CE0F006516553D8DD7076813
	3F75D8C5D9D2A9D8FEC59D291333E28D14888540BD734B715344DFF99DB5669200DD9D680707E8F907A5AF8C975B37D7385D4DAD1EEAE209B028037859C7470EEA38F7F4200F0F5693DC92246770F27ED17716261CB23B0528AFFA5B310F95507A92BAE1CC87A869443673F2AD361D32A5E3042269C38C5B4EA0E84787BB153653296FC3C534EC22DF9864C5F66278FE3B2D16585CE95C0F9004907988A5BABAA6E38A15B1BF84EFE6B74A47B235E9666FB81158B789642782E47D0CBE7F72078256067708BE89DB
	ED25979A1CE4D5EB0069F9DB73A0381E82B8GF084201234E100A60093G47G3EG789260DB010CB0026084789EE09D15A7889FGEC8458GC066E5B22F12FE74B115A188BBG3ED1745E8C709540A7GAB8176822C81588A300F684DFD18CCF54B873B4AG9CG038162GD281F2AE409C81D0875097A08AA089E09E405ABEB5668AG99E0B8C0BC409C00953F29B16F843082048344GA483E4DDD4E3AE82288668G988510GB097A07FE2108F60G98FCB136216976DD26507536DC714FCA45AF2B947C9A45
	E79FAABE7AC47165D245079B957FEDD67C32D4D387BB947FFD2378EA2F6237839A39D80F4714BA44FABB25541BDECF2CEFCCBB3B59AE7F8F7EF7405A126D276E34A55296BED46AFD3D405A43D2257EC99BA26D06349952B6BED072C81BA2ED0B34A752F676A9F8EC1324ED117674194066CC521EC8BB520EDBC447C582EB4BA43643F6CC462EB586FA12GFB09A67D5E3F011F7FFFGB676EF7A115411F4D6F249E512C7F2D961462764FD7EA329C7A5BEE9F4F31E1D946F01F8E6AF8490BE0E5F73A329F3D1163D
	760696D9BAA149325D5524669F2232FCAD8D9EB594C2D947FEA4ED485D8A718C272F7911448296374BA551C90833A87935B496119C1293145E6BEEF1498C7F63E972FC56AC35AB31D04369858209E59CADA42EE24FBC51A2E9F79EAFBC6BF85BC9E8A66474A5F1CA7FC3F99A9F454178CD1E5DD260BA77E20CB5DABF46D8E36D39514204539E13D14CBC74F7171B2A7B32395E56D70DB518CEA76A0DF5BFC5E3A2856FE33AC6BA9643D573B8C67B924401E5240CBDAD326CF69D703ADBBC1B99FED7398E3F054D7F
	2FB590FC9868B29CA026AF4E7F536BC9BE46DC4A443F576DF0B037C3DEBA754D997B8A0DFD89FD19EF16727C6B0366B3B9770B39247E25B3AD92043AA4B8BC4C6BE8327816AE2FC629AF16BF0D5617C393D7C6DCC2E416B87079F957A5CCDE2DF275DC42E4B33A7F60F4F3147C2CCB59B57FD2056ED2C2B74A494BD372EBAFE53120470F6EC797AF2FCB3A375CC81775DB15AEAC006EC1CE175FA55DB98F521582DD8500C7210F02EBBCG23FB768C524DGDC1A0EEEC59852ED70A1DDA160F681D84398DD84DCE3
	B81DC7C63AC900CB5151E5752BB1D775A3F4A3DA10AE2F9F0B3BCA7A31F2F7413598700C4EF396690677E3B1DACCBFD62EBF5D76D6240B6F47E23A64FE4CEEFF3A2CB6DE9F7DD85C17551F590DF44C0EC7B95D0A7ECC2FA2385AG427BEB7DBC329D69A27AB33D74F4CC2E7D9C524576E771E322C21F5ADF2B5F1A73C8175E1F451AFABAA6F79A274B9D206AD7B8C02F5FB59DC8D7B1C055CFCB47647E5009F45681AC1EB556EF794FC857B100453E467ACD64F403B57AC58568B7608252A5EA747323237D721800
	F4338730FCA11C352BDD61DABBD6E6A0DDD8B80B41A34299FDF438560E67B95D04F0962F9B5BFB0D09770FF0965B8758CB75BB1C09F4E9C017BDB0D0BFE66FBFB9DD51C055AF045E5FAF9333102EEC206A97AD9D536F2AD5C8D7B71059AB8FE4F47E767E309A697A8EE44FA0C16A77BD5E5F86326795E33A091CAEE6A0FB36C9B014FBF9BE52A5ABF4F3877A57871B3BDEDDCF66D33A367813F8D6ECAB7635C2CC30FD7BA1BA1331F54539F4ED0050E7DD066B0785C749DC35935255G0E4BF06E1D3684578F87B7
	C78A49AFEDDE98EC9D346FE54C1F111729F32F704A6E85ECAE7C46CAE44E063C744BF09E2CE8502EE16C76795C96BBDDBED166203FB7E867E60693ACE4EEEE34FBFDF2B984CFEDB8DFAD3090FD497A0A02D67C71E6719AC3FFE8E6EB815766BA77EBED0DDB56E9B28731B567F2E6E351652A2D939C558B599A57A9C70EA25F817923GD81CB0014695BE92D79C97DDCD926A786C87EB616A15FC12DC8D515FFE3B64307A8AAB543561E2CA3EE3FD8D53F3445B3B94BD92460E90F6DE7851E434269B364CE6CAD674
	4B9A2451730A064CA4G266B5BCE54358AE84286212E671CC416D514C5C5D5C557A59B5F213AB6023A36A3F65791312DC2EC109C3E4275B538467788AD1314B3814ACB67E5163A885E6E324BF651E1BFA7952EE7E51638BE022BE83596D6E86A28F8F7038FA2E10B4C2AEA87A91F59BF66FD2D7D5962A50676073F54E4AA390259DDFB05EAFF61158D19BB2FC47BFF6BC2DDA300E686G436FF0A3BE774A9A7341AB917F8A477781DCAC470F74209F53GD7F8D50D19453722C59B5F56DF45566342619AF1957AE5
	719972DCE817A5E7B179F7C296E5E92F0D385DDAC89CD2AC364A45741121389C7277113B75CACC7A559962930B5B61260B20E47C227D397E8C6E9D44DE05BAAE70F29D81D7B898F58CEBB052319E72E5GD65ED8AF1697BE984B2B76E1F971001B474BBBD1E9D4DE69907063903479EEDF4FEDFE59C7F491DBE4B7B1196917AB23FE75C3D03FE1B26AB794F009C3D03F3FBB0C744B3E9AE2532B515E5DADD8DE6955D85E14962CFB8760C6DD0D36ECB9CB1EFFC4EF1372BC709DCD4BB63B0F35570ED358DE7BA444
	88F9EF8FB4EC2FF4EC3FDA692F57286DF56E0D8D19599158DE071DC59D2A01A67C9A54C1E8C59D981D2595696201E69E27FBD8C737318D69CA07029F06A25D60B6FF3A7FEEC3DF0E821AE4CE77E3FBCF6A1155430ACD5AFAD8B10C5543B638D68C4372B7FECB56B7A497F98431AA63CBC2BB6AEC853A614330FE2EBC0778A44065DC0B7BEDAB3EA6D77ABCAA60335CBD1524FEFD92F966122C0CFF7FF964AF835E0EEB315CB74E236DB1001BF3AD3623E945C67A2F3D8EEC388EF9A7F4E019D5008B6378CBBB7998
	8938F40E5F7EB362CB06C37CBB9C71AFF3FC8460E68CC79D2EF3EAF5483DDE71A1DCAB2EC75E188B5CDF009B4E71678434AB89F0B9B7204FE78ADBC85D530751E2F629123C0DEE2FB330D2F753EC25DA15B9223E73CB6ADBD9F904461D92B63C97BB20F5941F103DB0466F884410E1DE29672FB3F8FD00EE9DDC3F5FE5787737C7E8DA06291371651810AF86F8665C406B0263575E887D7CC6444B1968074100CB3891FD1BF4524837D991B5663C8864AD4C627585B89B477FD79E2FAF404D607838D5FCDE398946
	431B50260FD7ABB6EDD15A47EABE4FGCD6CCD3CFD3C0778B440950E606D03636B81B7F484167BED3EFF393179C81788B459A3116E03B568D3366679FA407E662E116ABECA75C87FBD9582EC5FAF1D46113611EC5FEF8D47AF2BA478A88D1E6973A925A769B448CB9E05FBD577B4925FCF7A9D444BG73GD65C8CF388C0754D98BFDEE6A16B23DD6E85163B206139C447893FC5B05C1333925B1B25F6A5FD502A9DA7189EC5B6324662811961G23G62G12398ED32DA1F55857E628837373E9075E4F73EE7677
	ED5E6F0D762F1C81FC6F6B52BBF5FC4C1E0B6C64B9229A72BCGE1G23GA6812482E4C722DD6FCBDF05340BF47AEA77DE409DF43B1B776A578D392E2B1CBD33F1DDGDF31AEDD98E94477F8G1F9E0C654D8C4937AB5248272F7FC4FCDA8BF9B2C0B8C0A4C08240EC001C5168D33734231BBEE523EA20C7191E83DDBD336F3A10FCC5230D78F63BFBA62FB2A4DF19213C57FAA8AF53930A2F4ED05E68G3E6E354F7181FC7A76A96B643179711DBD242F9ABC239F972DF6B7FB5C41B8FE18A5DB1197BE9A1F5527
	53FD65F8C8A7834C9D0DFB1E52196E1C074939C555097165F8FBE67F6AGBE3D7D85377873B11BA6B4607A4336DB787A83DD1F77C0BA8CA06296FE56ABC00691448658741D2FE7B659827874B6A5985AB420926BA9155BF4102E83645F8A7396C0452DE853CA39BBB6D9EF555BF4E3GDF77EC9A93402737296DD6A31B36BA311E865D0AB64D26B6CD02F48AC0BA37E9FFCBF7EC4A0D525BB4ADA41F31CD4F0564DB9BE5B4D63753FD091D10D78B608188870886C8GC80B42317ABEE90F90E22C6601AD0C573887
	E7B089AAF5F736E7F60E894917BD46084F5E5AB3F92DA1798A8C65EDED6B193C5DA17936994AFBB3002FFBFD415C9E2AAFD40DB19A33DFED8C322ECAD73FD8FF2958C47A8BF966370D417E72FEB1BD2F8969D80044B158DF9E8B5023FB7ECA8949B75B50DFC34F75CC5E4DA1790431C6FC9567FBA66F3B50FC0672040E1E35072581FC7A76D0B256E8ECDC446B7A60D8FEB6F7A3296BBE109E8A90BD966B7A41G995D73C5D2C83EC9063E886F6C193CEBC37225984A5B75F34F649D8D4917EEA8EFEE8F6519AE04
	624B3D4D286FFFD2C8670C403E4FD76BD91BF85C09B1C061ED58A6BEBCC95AC4BD245BG865D06ED622E103A985BB0A9A4DF64EDC6FCFD041E750F4B8278747DE302213C320C1E59F7BCA45F8CC3F9EF19FAA62FA0A4DF1A213C87FBA8AFA9A4DFF634915F394C1E55DFD8G1F3E7E8A220D46371CDAEC4B5B2231AD0BF4AE73C0BA8CA0A29A5BF2F1D64FFC31B3A4DF2C21AF666575CC5E62D52178928D655D9F124FD8DEE2C83E59067242D777F07C8E49A77C41F07CFE2F0763F7E8BEC3F9F3FBA84F149F0A2F
	44D05EBDA1790C65A50464BB2813474E5E675277206AA1CF86E0383FD6905CD0C8C77F817B4DBF0E12F3EDC9101E8310FDFB0D39GE09BC085C0BDC09B40A000E800C400D400F4001CBB7833E4A5A9E317F2C6081C87A0E787EC4A59148220AB3B031D5FA1E7F3ECF09F8E908D10444BA8BA1A22330B6822CDD7E852691A7BA41D7DFA28D7E8676868365D9E1C2FCD17175A85ED2262B3628FDE870A3F74344C5626GDB75E777031D0D8D75AE40B2652AFF27405C854FF2DDFAD5973464DCAF09B735E7FA711C30
	1ED6F9C724B178D953C79B0D4F1AA6B59A1FB5FD3AF1B943C3995AB7EBB0FFD6E357E7D17F23B178D954D2DBF0BDF7580C755CEDB35673002DEBBDABED4174DCC36BF872BB4BA83F4FA77968E2EC19A80BCA5EDFE4626B007331C7C5273A2FF835E311C233CFF66FAD65F2436D41ED3C4AEEEC63B0BBE90BBEF70B57A2696D08306771BCF55101EC3192F5984DE84AAF0B5EA6A9408F51141F6585637F23BD182F182DD5CE2D2D1FF17CE21786FFE4B31719599C5C870B1B0DFD105B9C5C876FB60776C1C1F3F09F
	2CEB8E6D0312662EFD906FE76B960EEFF3AF567A60B34497F97C701FA3BE4523AD076CA931716A868B1903526F709F4FF2E2147381B14176614A6959635298365F16417147A9FE17864F76611E21EB4AA4DD81F951B1B8EFF550F3A4646C5A0AF138D738576BE743E654F5E20351FCD9B00E69D8BAAE182E03695A6F41F1FADD2F27786AF1FADDBF2DC7DD6BA1AFFE9C6A7A3097F5AD3C93F53D4267276BD654F5D0A5513574CEFF1D8EAA693ABB03699AC757BFBDF76AF51DC8719DF76AF5BD6AC0DD43A0EF4E1D
	28EB110C3AD60DC7DD1F107DF4ADC5DD37996ECBD947B39D5B468753350E1E438A9F2F57D522782163753A3E4CF59D85F95931286B8FAD282BA396F54DBEEBD6F42DEA743A1DD51B15BEF531A3F89F6F6FB06E63039C417B78B50E50FDBC42913C0F7F5E913A0FC7BB08AD8DF62B5D2B1C40D76AC3E76396859F516A0FAFD570BF51B3B29A1FECD14E3D357B55EB89566B31CD5AFA6D934B6AF130F20D04EBDC2CFF5D327A3B12BE07A5466A6B75BA0ACF0E55576B5AE22C575910D771C72C43C56734E30EBAFECF
	BD67EF5F678A7E62737E78ED8A7E0B8EBF7B367035B5BF7B227F48EC09D72EB360BA670FC6762D267A8AF7696D7B1862F36E525B9735916D4B033C0E3B503EB7BB3476FD416DFB20535F0EAF94FC6B4F7E78AF957C068B5AF2C83C4D6C1B5165FEF18A68B177AE553E6C3BEB4C4C0EA1F4CDA87FEE3DFDB7D0FC615DFA7B561DC47BCAA1EF685DE85F5F7C74AA65760D97347332BAA71D4F50354F2D8CDFEE7237FB2BC27F26C9C7DF4270FF4854162F4ED54BEB3B7247BC50BBB7CE75475AB87447F4DA2F5B6274
	7E78B3451745697D51445BF39564454421BF24AC2DBF3E64F6EFDD2545FF4E712F2F5259771952BED67B7B63B3EC9F6F69681573C79B72F5784F997EB5BD3E1461579DA66FC7B944F6F70BAC88FD7FC44FBA9D4633C6F910AE812885F07C091DB9E26B10FF2063CE23D7F4C288F242A3D9047AFB683B98C710B78168D3G844097G6C3A87FDD3C04F276E35C9166686F75B6E96594D70C35FD92A419F91DDAD22837D33FD831DFFF11173085D4570F9AB7C71E29B430B1F13F902EE08F05A44EC9507F44F6466A0
	6E38A42A34134D7EF824FFF07DF20D0E873CE2BBBDC34D6B716BC2ED7E9EC70BD46DBEAED9F97EB7745DF436D3339577604FAF60B8241B7621CA37D941ED5C246236A86796CF991E7B0B84DF475D03E747FEEBB122C9027C591C6671FAA39A61DE08356EC51AC306B4C5103F0B533CE6C8D38B798ECE73C01391CDDF489FFAAF4E736368D98372FEC2B4473DCEF17110CE62386F2C381611F2AF2EA1D4B688FC9C28555C4F94EB3B98937E3740E43C512BB2CAA610EF5274A3A13F323E135C00BE11A720CF3E3220
	CF7ACCC01FDCDB2F7AE124663E283E77FD322FD7E5B01FC4492ACFFEDF09EDA7117B6466AA74D3AA47190F211F52381F6EF7283E1922392FF6743E1F1C3DAA236ABEE273DEFEB6FD57FD6807CAA77AE6B047C5BB51B7917721EF9A1C2ABF1AB577F3FF855FACF1763EEFE47AECC16672B46E879B5DDB4D3A9F46417B913700362FC2C8EF633866CA74D79947CD5AC8F0645D2E4177238F07962BFE3BC9F37F2F625E77615EDE1541EC7E047BAB127B6172FD682FB80ECB5B077E1A44F1D55CDFF98F203F8AB4BEDA
	2F393FFDE36F7BEBCA2F4AE0B63F427BD951836807F73EC5BFF4F05C03B584B788522338EF2EBB297AE31466FE63495E774D7EDE1541ECAE2CC55F442AFE60EDA4BB1E0F6345681B7CF8744DB31A36122A39F77E8A7D6C52DE15411EBB9A8F63F3C78958EC0B4738FCF33D5F79G72F644766D873CF66B9E312902C6794ABBD407F12C0B0847BD55E4DA668CC82781E4CF4477AE5BBE45F79123EE9EA1A43EE07C5EE5CAF52729E0A2FB26AA1B283EB394353EA113497C579114598772E3A792DC6D916DF783483C
	74CA865B97E6797D7D72C9EC5DAAE873637C72093EFF734BFF59AF1F786808124FEC1B72895AB6EFCA34609857B7683775208BD21B56AA5702C707E554DDD3350059F685ED03E43D296EC174E792DD5F1CG6999GE98F627BDC77DA10B67BA1249DCBE99D106E8BB07CA124B50BF86EB10653DEC33FFDB18752D913204DCFC25A0B6A719CD069A4245DC357A79D106E8BB01C53EE9411B6065366B651F2A11DB5994A1D0C34FFEFC29DCAA7A36D2B145681693EG4381589C7AE78B4697B11371F96FCFD578CE8C
	0B55DA4E92F726CFA674E72CE50AFF63A7872E9DE6A8F5BEDFA9F3865090D8296066CCD61F47D9FDCE7DDE7B2D445CEB9DA64C57173D6BE1A62F62E1352DB27D1EEDD5752B3620FEFD9EB6566F6DAE749B7C302A9F734F74CA74CF8464B1F9CB5AD5F99F9DC3F951C164E5EA6445838D010F955C8C0DBC657DBF197A5E5BA2B17FC434EA7D33ED71C5065FAFA1102A48CFD72E617F9C2678E778B9D55F071C28EF4994E3FD97F521EF59943D3E83DA883ED1F47854E76A8B1B088D0DEE174CFCB8044698D540DBB7
	855B581ACA7FB6F66FF9D5470BAAD14770A0BA1A95D9FA9D6978B8C5DF07FF5B08F598B5855B7DD12AD39C24A7CD41FA1D55216A30F0BF6A109EC407A5DD6810132057211D6B1087F9CC4682974AA8CC8814112933F3A750D4A530B346446756G99E55F62F86DC9C0BFBFDBC366B80B5B5162F479943A39B4B6A17D34AF57651BDA54E5F090DD3476C6A6B0FBD11738GDDDE59083ACCCAC01F1796235C64841C779D073BF376BBF04EBE57C33EBBFEB19FDBCF9CDBC1475A847DBA63F80ACFCF502FB34E6273FD
	56A3B05FBF026F26CD6CA4EBA989101E8DA0BC8A31B340B6006AC7717C782BF20990627CF8355BD3A11D159C1AEDEAE65F26A33F54BEB64E5C37906755183BEE91E2AFAD8B7A7D825B232CFD04A9575804E199644A6C2B3DC06C0B053CA400F4005C29B56635GBB2722FDBB6879FB72461255AE6F16E551E223DFE9C063EA6CD22B0AA60B154C362B8D63342A29CC8F6BD463339EED008F5765C5C0BA86E012864F6A7320056E03CC555773B10A1FBDD5DF4FCFB7E0BD4F053C5AC4EC577196A3DD6392193C44C4
	E3DDD3811F2E4B4B19D6E3AEGA81D265775A32A5341E9FADDB7D0FC75B43D2EB1DC57FA480B1F063A660AC6ED2670B1A6EF67E34176884DB4D629FACCAFFF854557BD2617FF7DE914EF033C0447D07EEF6B3572CB9ED7E2D265DA8BD74763C6FBBDF7D2B9FD9E574B3F1762439F574BBF4877FE07C25E3C47D17E96BF7B6B1318ACD9390641F5F812117C3EB4260ACE524B9FC871F1C9FA796FB4217CC910D7B49D658BB6A37F8F1A4EE40E1A9E4C7FCD745B3C31537572BD949FBFDDAF7FF5AEBF9172CA1F40B9
	62B58B469CACFE9CE14578F1A850FCA030180960700F4D1707C9D14A78B1B387470F7CDBC78AE5CF1619F0AE546B1E7804923FBD2946F1872BCB97A95FFAB1115214B3A4E749778DEB441F1491108C97EEAF0E920760F1293261A749A2D70BCD821EBD3D44023A57BE09737AB8E226921B0C2872377544B17E1E5213DD597AC2BD39E7BADEC9E371C9C01F7AA40EE9475BB784539510D5DE37C7724A6D8CCB3F1626681B4E5F134A1A017AE6DAD4392FB4293A3FF114FC6B44A5359670976977C310D5781155AEBA
	5CCD9F9595149495EC5E329EFDF1C7A5163D13177D16DDADAF3B98FD41726EF02AF9976F7BE532366DC7D9B2177504CBADEF50C6FF3FFF61C27A289946FE8FDE972F3B55F23F7FB6B07EB41FA46DEE9BEF931EDA1415EAA8AB6D9BD6EE7E99355C854501659EAE46F2D93F0D2990787D1FB477338F777E1AC5C6C5EF4AD04E79F586BF0FB13D5378BC46B31D46E76BE6F6F6FD466F254E60E77C52BA355F1BE4FDF04E9932CE107F9444CEGF5G9E003ECFE1FF7C79E752FE49973D83630BFD56A6F29E04FC45DB
	C679620560365AAE047E7677198B649CC303DB165DCED2BE62DBAF107A125D9E0AE4388E3A776E35B759E4C62A60AF3AC05A24C3EA24E836D696CBE3668830AB7AA9DCBFFBFEB3FDF601F49247EDDCC73F1185692C2791B72D9357F673B86E068DFC7D126312CD8457C1529C77C9A6410D02F45C53B8CF9D3528ED7C2466BE2521775B7B6B3DAA03597C2285FD1D487DF0400A3ECE66B8B64790DF27F15CEF9A5057D94FA0EE0C99FD1D47F1A98D68EB9B472D35202FBB1EC1DF97EA7C3BD9F3FF572F606B47FF85
	DF2F23F368A030B7027BA1DCC4FFC5F1BFAC2FD7EDDF2D399FF93A777D90572BB2147ED80F6DE69E7743FFE790DCEEB2447D49082BB371FE4BF1FFB9CD5B92C9A7233F62B4BE1A2839BF7AAB6C9BB7752A8CE6F339087E9A457D50C2FD938B69C99C67DB067ECA6238F10D843776D918B31EC5FF75EFD2FDF41566BE3F29777D75D92F4AE0B63FEB23E7E8405E3AE77939DC9B7A46467D603429361F555C7F73D770C31E2D77E57C5E2AFD460A853B931E6D7A7D9396E79C7211F53194201D87D070E7E8BBG95G
	D600B600F0008800D800C400D400F4001C67EA4C79GA5GE54FE1DCB2A803FC5FBD58F3023271C303932FE8A7610922FFFF0BD67FDAA84F71DC577AAB67E90E791D93851E41A178143333A23967719AFBE4D172EE90C96C453F45C8BF3B493E4045E43E69F736B1926445F5CB5758CAADDFA27024F60B6F2B7DDA3EF46049C9698E5FCE3FEF5A94GCFE93778DA7C4EAA9E841E3A90FC4CEFF34F840FB17FFEA6F40C19C54F28CB909BD637FB1473470B3C64AC1A3B3151A771D8F629B717634AFDD5EE1F1D5410
	F2BE3758FF5D7D5396C8627365E6326667945B6C4E9667897BB9C9D94BFDEB8B116B819BBBD2F04D20EC9DD98F76F825C6496B152C149A57FEFF2031E8A4504624605A6F9BB496CD01741C94DCF39E3781571C73E79A2FB963BE847DC69A5090403D1A32197A3D08D769D9BD7C5E9F133BB893575D2BE6226EBBB3291D50007D6D2C263475C06718094F3065F46F0B65FB5859B5489BB5936D9ACBE337C4C8A7F35CADB4464B1ED5E3DEB18BED0DB7232DE5330CEDDD58052D3533746B6B0B783E1DF5964ABDC2F1
	B2247B4CC2DB3F967D6B5B2159AB9BA922CED186BA21BC2C0389C013B40B69C4523383F4FA0846B2F381AFBC0F72171A7C65DF5E264A771978DE547346723575DF86B484F0DD2976F9FD7D7F63F4E07D2F2C67FEFA9E7D6425B1048C69BEDC4FF01D1E37EB76787AAF63FBFCC1745456DDBC5044EB741C9120E7C2E3201E4FB7221E29DC27E21B3FCE53B57BF8D759D02739C1F4DA2671DFF6AA4CC529EA5D15242ABA3118609E5A36F7AAF806FB00622A951C72FB8D3E60E318648BBD0639FC464F4932AF70B939
	5DE77C1CFC4127FDCEE6383E7477971AE07E2C65F47DB86E9447DDC64BA43813F62BEC637841D4AF02BFA89101083F166A55CAF1358A6EC68D6E14029B65A363B018ECF3FB11F7340F6E035A9D8E169EC3E96CAE981639BFEE27BA5ABD22C66F719CF70A5745CD16407A3959222D9F06F39F8B24130F85522DF085522DF28552CD2D8C24CB2A8C242B5E9FC8F7EAFFA05D8983393586F243EB83692E28555231BE11CE770DD9FF79E89DF95752EF4D324C6E14DC64C7518A379695EC7EF2BD67FB2FD365FB30337B
	FCAFDDD0792E5C704B56C8DF5C006B101E542E561AF3B2D4999FE67A2F51EEB229F9639B3A2F77F14D9A78B9CB7779BEE8D0794E7E82F9672DAA5FCD66DF6627574C682748D41C07EF1054728AAC7EBE392FD14D3B22DBBAB2B9C97C2CCE0AE1FD24FD933C0ED21B547BDDBF29727FB3631759BA4A04BA643D50D51B30B42BB20A6A7D6D0F572CDB5BCD3FCC7E5D4BD0FED517727F62D5E53C51683F169FEF43B2428C4B684A0FC368B9F0B6CF091F127DA96539028E4F8C1F5F36C44563702C44627F39C19B0BCF
	G79C9AFF4A706FF432C651B8DBC733A4577145FDEE16E0BB56635AFF607EF345F37DB3681CFC53778A6AC735B1B849E39DBFC137D76944300E7F888BEB6461A0A82475D05C5016373721CC03AF7F282695244C03A5744C03A8926C03A0926C03AA7178552A5AF8B24CB3185523DE05352315F985E61A087D5227EAA4C1705C5870E6FBEF5E26F6E0AFD32E05ED3716D3E535515270F6FABA3B97862904901C7618259DDF0DC324AC296F99DCA969630371FA0FD446E5ACEAEE25BF61FD7D846FBE1B91D76D34EEE
	5D1E797C48CC1310D5AEACE0E1D03910C5E219F2E1BE8DF54A05CC88035ABCCAB7D92CF48F41F7C678D9400E9608B5267547FA05A5FE2342441BBEFBB1614FB53ECCE1C139300A9C58BAA1F9596FABD849378B149F945DAFAC72386D1089F8D056C7AF73FDF23BC32A90D6B49EA7BF846513A5AB7F1221CF96D6522FF3EFEA116D0ECD87C51FCDA6D1600E8AA14BC53EFBA83C453249473BB7A99F6F2696E76C5EC5F54ED174FD1B6ACBCAA966253CFD7732CFBEBC149A1BA918768B0B9DEE513ADF3448EEEF3930
	C836111F16F0BB2CED1EEFD8C1592D448D749C9594779EB71F98DBCCDEC363CAB737DD9F3EE476127DB04136F905CD1412445E451C423A57A6129F15103C65B279A9D737F7AD4F7B4D7B72270F9CF8374EA4E4A8C612AAD85666111943F8DB205FAB5F8FDA4BCF7FF57208E7F6264D81167D42FC7A69E8901C2BD29DEDF1B6C85ECB23062CB18FCE99C074729077504AFD9B5ADC236834BB5A61060D6D08DF8471374BC7BE398EC28BF496E396D73C782A49A5C3C63F719DD5DCE515058FD895816B26A6496D5C34
	3B31516E12F881D0DFD07C9ADEBC21A956513C55F6454AC3537F59G6D2BCC48F41614507F1B697FAD647FE60A59CCB11B37C0252589DD7E69735B5A084D79EDFE6D9D9FFC1823168434AA02DD9E58BC88FAA54DFC1710131B555C37A43506E6FEC07FFF54B3CD8B3B52D4BBB98676F80E15050C828659649B7964EBF27E430EACAC5CCBFE5343C78620378243205D17BD75E454693295A64184E310CDE2034E7CD6B26152CEC8E66ACCAA3C54AF75EF654A6DEDDDB84DDF6F2ED27FD6B615F55F26ECCDBE05FFBF
	EB0E7CBF53AD2B3B53AD5153640F1DF9DAFFC1FBB6BB7D92CB503359C3DFE131D754AB6A19A7363FF041C37712811F1546FF9740CDFE53B558392793CE516110C5C773EE17151C015A53AECB6AB90A3E9ED23E8FE914CC16B700FEA766A78D4F7FGD0CB8788763738A238A5GG4401GGD0CB818294G94G88G88G740171B4763738A238A5GG4401GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG72A5GGGG
**end of data**/
}
}