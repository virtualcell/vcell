/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;
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
	D0CB838494G88G88GF8FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DBD8BF8D45519C8030B9AB55628A0342694ED2A5126B53558323B314BD634F49BD32C58469ADB2C6846B6EE51C51B6E5E3CA041GC102E2C1890A92B4443CF898021097A492A0A4A18489101B4C4D4C10F9E566A619C0827B1FF36EFF6E1DBBF7B2A13B716BFE3BFC5F4F1D731F7FBF7F633C7E7338B7C25E66084C5434BCC1C84D9414FF3BD385E166D502D0FC3DD3C65C1EED715902665F9600354256
	7E8CF856C3DE4A376233F305370EE7C13A835231F744E73F853F57891B0E37FB61C7GB94510777CE78BDE9FDDCE4AB9143302263F5714834F55G1D00A34F28EDC47E23CD9B94FC0802C7C8CD9384390050BC5B34C941A583CD96600A81EA8B98FD87BC1982D3AA76A8F49DB7C58ACB9FF5272DA265E84AC4182B70A6AA4FFC613554295B092C5547F3941E8D10EE82A046278B0D439B61D95954EB7E2C3B4BAA97D96D22D92A28E86996EDD2136810ECC246C7043F0D7AB39DE0817006D1DC350314D98F3FBB81
	3C9FA1DEF2927CC2781D841071B15A31EFDB2522E77B459F888BCB6D99EB82583169E326FF6D472A9D2673CF253DCDFDDEA9925FCE023C59GF3G92GD2GB69546E7EF24F5DD537991BC2B3A649A275D6EF4D439DC55B6137B0853A45960F7C58564A838E6295B69B689822B4F3C6305867A0C860C6F3DAB66E3BE09AC433A0DEA3ECFC8387F56349586BEC9083C18D6DE48FC51D8287A0449BE9DD0362D50376D794ABEA9E8E5079CFDBCBD53C0F6445E4753A7A96504EBE4AFA8A4F57DGED83B170BBD57185
	437F1862F3F728F8568E6BA91E248BA0AFF487360D765528CBE346ACA13764B6C3BFCC7BF1E8DA728E26C3D611DE173ABED2F6F991135940F5F1D3FC1C864FF469256D1424ED1FC0BBAA094FE66D2B2E1B744B0C72786CFC00BDGCDGAE00D00008F2ECE3211D1F0C310DB509EE393A5FEAB3C9EE01715609EF42D3724AEE31416111C5C73754A63A2DE217CD72888A4DD11178204B072F05367DEE10F1C8F2C8EED136BA9DD03AE817E4496DE1B49D62DAC26316E8EE2B55AE01G3BCB40F2CD62BAE2336811CF
	38CC22AC2994AC5FA612BA99905B20C4A8GFEB33F38CD44DF31E07FA200A55C8F4F51728ECBEE98AEAAAA9A1D4E5EFED7AB31C45833137A1F0E9DA9C01F3793FD6C1088EE985261BB311CF73B5E5014D3EB6DA66A0B6EA16CE3F3E95B09847AD800C5BB316D7C219D5BCE6D2AFB054247EFB1ECBBF9150F27A76FD45A4EAEFD9F1ACF4BA67A95C3DE53AE9C5F0FBA5A094DBB0D7B91C2E410FC04C5BABAA643CCE5E48661CD9FC379D826B64D7C1D50C36CF301DD21GD13B5067273A72D99B2E96BD92365AE3EE
	E8F012C92D812667613EB0F81E82B884F0846098E943GE600960063G3B81BE85B8A11091C6906CG278136D1F9023099E0874047GE4DEA673AA69C76FD399023097E03F22F78940BE008FG568354G3C83D086D0C77466BE74D25D6601DDC9GF93B6333AB81CCG21GB1G89G99FB62338B81DA810681E683ACGD88A3069D318FB81ECG61GF381968364D444E71783F4G04GC481AC84C881583AB7BE3B91C0861889B08FA089A02F9264D7E29B6AF39EC88BDEEFEB957F2CD77C723602FFC7715996
	45C79FA83EDC2D78F007623F9245AF2BB5F530D7715FFE45D7B50A5F8EE964E2BD9ED76A906B6D14D2EFFABD313EB1ED9FE33B7CFF786F81EBCB992E3134A5529636A8753EDDE06DE135D27F248D11F6C35A8CE99B1B15BC5226C85BA26D09341DBA850FED1234AD521EF68AEC4EA46D0934A36D38C5F44C96D8DBA6319D36E3B2F63DE320A78936D7E9529B7F867C7CFF815858FF2C1B5411B4A0B964DA49A5B9CC7043236435F513FAD462139EA7676920F89744B3B590C0F8B83E2B1B5439A84BEEEBD73FAC35
	C832ECF518557C73D416273F4B2506C2A82B2F1B34A167A044B31CFE201B44825DCE07C3221390E7D1723DB49611EC129D142EF176BBE4063FE4A26B33DEE9C80905A61B168BA416317513380A2DF922C5526EDCEED86B3807C8E8A644D412B8651A639BB4BEB2810E2C5D2642F3E6A5469A036FE32C317542370558F32EB42318F95A1DB72625D43239BEF71F9AEBB01DBC12316EC3B4A6D2709E26EB24EDA5BCB54BB15A175689E535240C6AFED9F6BA8E391D7D2E9206BF502043173279FF3D05600BC117DA00
	0EFDB87F3FAC11FC0C3914093F46E933B137C3DE44BE621BD54EBC0DFD72BE661B55BC7F530079CC4EEFFBF2C97DCBFD7D92043AA4B8BC4C6B68791EB7F4F9EDCAFD317C17FAC8FD59B4F1E57DE7C46602BEDCBFAFD164891F3167064F18ECC6F7359B6942157CA865B98F6976933A839EDE1E12AF6CE73160067DDA3A57B8DD787E51686E1311AECAC3D76CC757550FF473C625FBF3G6992A03D94E04381C65F8ACF9BG239B9AC43AC9001BF64017EEAE27FB580BF4F100DB8210D345686A6159D105F4B99710
	4E853829D53EF4D1G4B2A885DE79711AE360A45DD0B2AD839CB60D984F8C617B30CF43555ACC66B28E6657A527D1253592AD9CC97D24D6C76250B986175D14D623E28EAE6B752B1BB4EDFC23A3955CC2F05704C2D01782EC66B677D17112E3E06692527E3F2B385246B2CE17123D7218F2D516A77A0270B28E131261E0E493527A25D1C9AD53FB8BF7D4A5210EE29C63FB8BF7DDE65F4B935AC1EB5566F5EF4245BDA4BE2DFE37D448C242B2CD575EB2D556B37AD93693C352AFEBEF434DF3E486942EAD9FEE4AD
	EBD7CBEA35F6FCF7B952955731983C3E0E513757E96D383C8269ACF5ACDEB73637AC0B770FBA965B7B59CB75FB0E53CD833A68BAFF7D183DB33211EEE11D6A97C26F6B97F1A552A55529FE5152B17D6EFF9769B28EB2FBB79DE4F43E760EECC63A4203EC8DE2ECEFC9BE6FEF8759FA45186ED9CE57F1102DEDE4C3395F5902F4A18A5D4C033E7541662EBFEDA773A95DDBBCAF8E085EA24FA0448495951F53190C6DAB7E096E8D907A280338FF387984192B96C3BA85A06F904E3D0F2D427D43705EC82173584274
	C07B20050718BF1B8E29F36F34130369ECAEBCE7A5B243A0AF62904E03B633F68F234A63F1F6DB697609B287ED34E867E6064B37103939476A76488D90BCF9F13E7A50C274A57BAB8ADA7147DF0A5EB17407E63696F0EFEE6420565639EBC652DE8FE0EB0CE263C20D2DE19F3D124E7638DE26FBDFC4FEDEBD54BDG0B93BA5CC4F78F09AB1AC507D9C29D1F5A3C951EEE49A3492D907D9D34CAB6132720D15D9BAE2264FB3737B1BDE72DBB206831603ED9C2656533E9C6FB3AD357D852C26A157D71FAD54F7647
	1ECA3381B0DD3F63C6DD1781CDF1836A7A260748B2093228282A683AEA471BD4579ED057F2446AB8A2FA9B45AE4966A9585E06FBFCD6CF0E40E2A68B1497414B0C23F82B43AADBC51B7502D4301D15D96CF88F1E2249D450282923222AAE8FC4425DB22B2A3D24FCE67F3D6FEA6D4F9227985A9F7A3CB9EDD18333DB78DC35BF96ECDF486DFFDFC6DD6B01E692G43BF4171F3G174C715775A33E60F0FCF667E1446F9FC0BFCE83DC9CG0BEF852BB63E4DEAE47BF165702CEFC43F5CB5C05605D6D93297117FDA
	E4D116EAAC446D26826210A2F1D0AE22CB062286482FA33F36ABB169138344A75DCE1B13EE0212710B76671481BCBB68ECC49DE78DF29D81975708BAC61A0DF44CBA82E3A0GAB6FF3AF16D7FE844BCB76E2F9B6404DBE0265B537991517G7949C7506613C36337795AA1220B58AFBB0949CC3F28A154AF6BA86A3773826AF7GF05E23285F0B9EA37D22A1FF5ED13477298BD8DE82AF4FFC916BBE6F98746BE3E84B3FDCA46B9F51EDD656832F5034EC31FAD8FB9DBE056DF5520298E1433A6B8D5BAB9D5B0FA9
	7D75185ADEC31FFEAAAD62E9EC2FBB07D1079420A9EFC29DE68E238E0C6EBFB91D8DE8E6F33A1E91DF3AC7C610AE81E8720EA3DD71A5DF3A1BAF21AF9B01A604533DF6E9BC75486A6141F2EDBD4CBD4E6AA1911ECBF97913CF137D8D49C116A0A6E5FC31DCC61DF31AE1ED550C75D3A4A0FE98F0B14DF85E167799F952752800EB393FEFA3756B11481ACBB2B17E5FF27EA4605D5A02655E1A0A36F7GAE3C855B519F4B0C74DFG79091C37A38D4BDC863862D6443F174E47C840C5F07CAFB9FE9160F2CFA07E5A
	8CBECE81EE528954A1DF566AB0670462C3F8AE653CCD195CDFED602FB6443F399C6D9A86DCCC9B7AFCE4F9A929FB3A98ADE20FA6495D63F45B7DAB350AE6AB552A4C911DA31FD25F4A4A929ACFCA5870DEE40356D154A23BE10C5F6B0FA1433CD24F3FDA4E6B83F45BFA92752BDA615B5F2CB4AD43544978A2D7A0DF8770041F64F511057885004BE8C7FCEEB67A2192F0F2BB7AF67BD9A35FC6C1FEAC671D174D6B8BF0392738EF5F65758538C99C1F4071F3G17FC8AED2A592C58D44A686E5A4C6719AFE01E79
	82793F48C77CB44045F17C6AAD084FBA9D1FFD60B4165B3F45375CC51C4E8BB4511CEE7FFB68D33667394C6FFCF349E9759CA565346F198A1D0B69395F3F53B8B237031D7B3D43712B5B883ED503E77AFCC069C9FA9A64051C41332A5F74925F3B4EC23C8CB09BE0AEC082C04AD90C9F6F33107D51D14F829B9C50709C222D45E7938C4F642C4476DEE9C8C9DB2C5AF1026951E8A7FBAC9B4E41D88B5088E08288B907BA7C519AD407BA2F218E4C4FFF6E537BF976B9DF5F466A520CAF5B2D67FBD717DE2C63E376
	4CF210F5C48A64ED6804718820914085908A905D09F6FDF6FEDFD03BC827EFF556G2E5E696C2D516F9BF2DD3F6F276B58EC7CA7BF3EA2DDBA2E53084F6989F6AF40D85EC8D03EA51DC6BEFD5BC5FCAA0890AF811483B48148GE1GB1A27A744673FB4768D3B62A7AFB146939DD9E1FFD3B0372AD940D781E9A273CFF8D4A17E4A86F267E71493BA3A8DFC61791DF5D40785AE70B9F1F3EFDEE5249E373631B55242F9A2C511BC513554916BB98476F216BE6A22F3C8B576AAF53F3E59B24C3GE6F66119E76A60
	D86E4344E8F4E2FC11FEFCE3337F81BFBE3D7D73F576B31B924D387F1048ED7AA95D1F5F508DE392C0FDB75AF44CBB961BBA3B75B63D6247B7B61BB2060259A4F79B59F4ED9B56D3E8B75AD4C1573B73A01DG30145B74DDBF99C610E3525B34774278ECEA7063535B14EFB2326983996BE90F89ED32D31BDC101E8A90E1C21BFE6BA74388667859E43FB83E3EFEB1A85F821351D8BD1D1EDFAC06BCC102B990209C209DC08618A661D87D4C79EAA148D84D83DB982F718C4EE092D46AEEF8FCF6B684650B160C78
	B2C746A7EFDDD03E790672FEBECEF9CF8565CBB414B77D52787A42377C7874FDE119E4B4E67F29A7403EAA5D7DE27D6525F252DF481ABF37877BCBE5993D2F8969CEGEF8F769747657179EBB8A8DFD80F91DF39B0BEF955C179A28D65AD99273CD70273994A1B19BA3E76F0379F1F3EBDAC6AB19A9B37713ACE66F53D381454F5019956DFG6DE62C6B2E3471794295144FE5B662ABC89F1F3CDDC179269A4ACB98273C970272C5984A8B4B981F3C2F87651BE3B66A7B9F9410B943206F735DFA56A65AE50C8162
	4C58A6864E10B611E5094F4E8758E341B65116B9BE9BCCC1791AACC6FC1B160F2FFFFC6447276F9F96C3F909FEFCE3336F8FC179A6994A3BEF4578644D8D4AB74DD0DED756B87BF4D03EE8C3F9EFE60F2F7E367A71696BEF3E45E8FC934FE2DBCE34E0DBDEDECEE3F4AB4468G75D6EC4B0F7A49981BAF9685656B349A3665F747A76F2320FCDEC3F93F9D273C6420FCE1067222B60FCF5E8F0372C59A4A6B4C9F1FBCC7F0BEC3F9F9DB46A76F3D20FC0B8C657DE61C72120272A56B64313B77EB1AC95F49BA9F1F
	3D89006112DA886EG245B4FE33F794B89F22FED985261G51G738192811682E475421C813087209D408B908A908190530BE5643511B2487D87F237005C87A0F787483D99F2B7E5BE50A575327BBB64EECE2E8D56BBG6DG43B6AC63639309BA3B08AE5A74D2CD1A48475F43BABABDE4A93461BA3A44D17872FBFD73C2FB8353921F901F45E8E850DFFADAE6EB0F1F2D7A3B7B016E4686FB97E00D72543FD3707AA8BCEBF56937C721A577FAC93C293D530B770475344ABBA23D016F1A7E2157782E6952DE633B
	262F752EE5F8A8C37BE68D662759C63F0B3A529678AEEA2BBD301E276D46FA0AF6E3BD4D765175F45A8369798E2D63C75EDCC379BD9E49C3B7E3EBC5D9D4721E13092F7D6E479E956D6A39E229BBDB2129131DB565DCEE14A3300D77BB0CEDFC50C15A2247596F6E1674F644BAB670BCF553019C3192F5984DA314DF965DE6494F8FF1141F658562FF529148D74C56A49FDBF7F27CDD9E8D7EC88917793EAB308FBEF2997B20589558873BDD41FDF04095588707DC41FD50649A5D871D325656D20E4F6BDF297541
	CE444F77456FC27C7000369CF22644462BFDD6B287C558FC473398A5BD5F66BF4E3173B62B1944EA89B6F65E164A71EE0ADF224133F338BF53BDE512DECA6687BB4EDB9B86493CC56E2E4D3563D9615303BEB614202E4F1A0D664B79F626E302BD102E7B296CE43BDE57FA0ACF316BF5FDCCC2DD339C7159B6876A5A63C5DD639C286B0EA19FDD4BD0579B5B082E898EDF1D1215F406B3102E77517D4F8DCE3D2EBF20782DCE3D2E87BC28EBB16405BBD15779C3286BB2A76A6A3E6023EBB90FDB8D4FD1F2DC4A9D
	91D7A0DD1F21324BDDFADDDF207883AE3D2E9E2EEBA364C53BD057D7AF202EF9FD286B6C0B2FAB3AB675381D76269225CFFD33AFF09F3F3B4F380FC777856E63BF688B5E47E37B827771077B02777138BEE2CB1755E4F5ABB770157A98763531D441D70E78624B957CF2FAC7C66313D2655E5BA51FFAAD66FBDD655AFAAD68E375D829BC1B60E96A732DDBD6FF51F4CF515B272F5787A9BE24CFDF2F55E5D82FE110371457611D1735E30EBAFE778A3E766DD2701BD3FD71FB947CAFD2FD6CABC57B8AFD6CEBF7B3
	DBEC4AF392BC435DC67635D3FDA35DFA7BC40A0FF16B6DFB21946D0B053C2D9E34EFFA1A563E5D5C3EF3693EF66CD6706BB2FC711FAA7807B3346510F81B59F7E654734D292047CC0FEADFB47CD67A9F5D931A67515B77A70A0F73686D9BBE0376A5C05E81996D3BBED32BD7B9372FE539F6DED6672437D6685AE79943BF3B42576EB205FEFA160E3E18610FE4EB4BD7672A1E332379E3B668BDC7D67D31C0C6FFDC207B8A09325E9FDF2178A4D96F0F1CF27447B2486B68C7FF242E547A63D3559F6FEA713BB87E
	6D4DBA7BF6B27C6FB67B7AE327C2FFDB3E0EDE39FFF4F80B8E3F0B61573F274317B37C074D647DA81BB8646C178521F00058342F996F9A4502BD0BG8440678D30BBC7EC9F723B03C42F9E37E807902445A5F58BD90304B73D95F9E5201F8A9089B09FE049G7AA65FC5F42931C85D3DDDCEEFD53F6CE478E9EF2E56600F080EFE51067E2978185E5941CD4EA3D68743EF4873450BDE0697F711F9029E08F05A38AC9507F43F4E4DC1DC33A42A340F3C6E0BC77A1FECDF2B517110DB9C22F728F9BDFED6204D2F36
	75CB2D4EE64944737753F75359C9CD991E41DFDC4EF1C8775896152EC4416D78C44515AA7796EFB33C775784F5E19A443BE3F7DA0CE806A1BF0C53DC6AB12209047C391C46AE9951AC047CA51CA65D309C418BCFAF52AC31995194C27E81AF4E732D742E81F9BF211D6326D11C8D52439C772A9577B226FAF18F41C9DECF467742B53FD34D6D230C897FDDE0B27EBA21B296F949B7E93ECDDEA9D076F7D24C6813CDC36813B39674C941907A242EC77543D14D6FA7240977C972044AE0BED9B0247A642EB6ECBBDE
	6E13BBCF221FC2B9EEE5BB7AE99A77D30CC7754D5FEBFE9F74CC3C1F4ECD280CE5C3446627795D74A55C8FD5B27A26728262E649681B7A8B681B67B47EC8567C9E7A92FC7355AF41B73997094DE4AE1F467D30EAA741C5C1FAAE477DC7AB4145C1BA11633ACF203F12B86E772584C75E6D2A3C08BEFC20CC755BBFEAFE5717CD3C8F45891541EC2EA9C7FFB5DDC4BFECBB0C7EB2F15C544368AF97475D407D954B7DF5D663A33366F7F269447BAB7BCB70174C7B59C26E075E5368072D43085BF8066076C03AF198
	FD13FCC675472B1A5F134FCC3CEFE6CC288CE67348D974CDA777C3EF997AA61A63224B50B77338EFA6152B7E38C6737B0D7209774D4793AA032DBB24E6DCF7AC821BF3C7B0AEAF6974399FC05E0E2828B864361A2AC5F3A30D7259196A3D4DB856550F60196A2FE91913A0BD8DA0FA845F3B74FE086FA2C65DB5CBE8BDEC7C5EE5E26BC85A7C913626CA9AD15F99721EFFAA0D495C5E0AB28BAEC11DDEA238132D5A6F8610F9690F296CDC1865DF6313CFE26BC1C11B7FA31FFC226FCBBE79AF7A64939F9DD17219
	EDBF7DGEDCB79E934E07BFEC840EF6BC5DEE2B6ADD01EE10B272736FE6BC2BA336D67348D127D260C4B684FFF237B1B96C8CF8218F6193F4FE5C15AE8CEFB04FE1FA1EF924441G35131076019E3C7758B189E9FFC83FFD918E69A800F91C76CE896F81A5F05AA63ABF19F795148BD0FB95526E6DC15A0E2B10F6270D168B69A800F91CB65F0EBAA4F05A9B6D345C49D0AEC0ADG1BC3C72C98DFF4CC46755E5C1378CE8C0B55EE39C8BC9FB1194E3967EB957F5AA67B6F9D26AAF51E2914B989E8882CD7F06113
	5575B82B4F1F1F56FEABB177F6DB9A666B4BDE2248DBBAD9ED2BCC3F3F8C2B7A4996542F60AB467A2D9BC53F4A2F287AB17FACECC37F54C39E13376312AAEFCBBB4AEB8FA0AFDDA34F86B4845ED7F013B47214777F06286F5D7D9273CF442056BFFBD6CEB67CFE8901D0C5FE0472CCD9FF3B621F3917D5FD9F14D15FC581744D9EC55FA4BFFD37DDA4789E5166D15754DBF7929BFA1C8E1979300246E44B00B7A3845B587A933EED6CE9C1557142895431BC44D8475795D9FA9D697898222F43B5E5D8072DA1586E
	EDB46EB1C15A9502751A16266AF047E154A1A2008E2BC651A146CF079F14228E311047E4DC476BA24EC0C63A4E4E45C47790F6471878BCE70ADE467453B8DEEF1802FEAEBDC36638EE272D5F6E70A8F48DB4B6A17D34F08A6A92FE96F5291CE22C0B565E26A94CDE54456427CBF8996A621A02BEFF328C6506CC41F93F37F9ACF73F7D676C21F172FD6F0A7958FE623DADF42C1D225FE7FC0062A3266877991F6173FD946465DE0D6F26BD1BC64F94A09D869089908B108810F2B55E9FDFF721D888F2FF3C5569EA
	1486A41B66181A59D754FA257631F1662195B82F467C70EEE14E5535813FDF107BF72CFD94AB4F025FCDCFA5CFE61FB703585789F943G91GF3G96GAC7EBB346FB43D6FCF5ED8B2D965AAD9963BAD74AB8DE8DC1BD59AD451E43312593650B03EDF266811B355782EC7BE604BF5F975106EGF0E9702CBEFB683C9EB2D5DF4FCE0A8F1B2A2F67E5E62C671910A7DC435FE9309869EA3A064973DEE32CEBA860A3F4F9B1101E8F10F00DDE570728CE495768F5FD1462D32E516B5AE261E71F57C27F3D96F57DD1
	0FD11B093B1649DBFCED20B342A29A2BAC3BD6AFFFB745E7DC27179FEBC2793910A7DF07726F16347297DD2744244AD3880D4F4E8BB5BA6B19C76594046A657F0C624BC375725B78596F81481B9D0A728F78581FF5BD1335C9F996433376FAA3795F2671D77B75FA795F23F85375FA79FBEDA85F85F98B2FC7799F580D7C3F6786A6337106C07ECF275FC06D3CC1AFBF0762EDB768655FE2C779DE48CB38816708F0AB469CACFE2CBA0F7163019B6333B78BACE6A2B87C47664BDE328D2E0C9F4F64607831691EEF
	8A35CF542661DC28575DFB23F27F60C6B50E4B33676337DE08CB147357A7AF126F9B3609671591108C97CEB70E921F43F269D857F929DBEE9549263192D3DBD0F7A18C677547296A4C56EF1C743535391D3F2794B61A2DD751BDD826E3915D43F581FDE8980EE96ECB9F87529510CDEE27CBF24BC38CCB3F1626687B82FFCFAA0A6BFB07C515FB2BDD557D5FDA4937CE9C52E0BEFF117EA084D9856F192C224DE9FE2FB03F38B03F24F4BB7A621E93D876E2DE76038E353C8F4B5097AC6F8B0F1A37717015490ABB
	0C32B65D0432C6E4353CFFA975757BDCEEF76BCD46FE8FDC97917DEA3951277D634F4EB3245D6D61ED621F4F222CD0C3D9497BD9397FB42016FBCF19FF3949E5D8AE6B3777134F2F62DDF04D6F671AA7FE4FC2E819C8994ABD3F344077B1DECEB33E0F7167B4633BF52969235F714BCE8FFC47AFB7DD7B3DC95687EF9EA47B847320BE9381B23E9A1F3D8120702B589F3F1ECE5AAF792237FFFCD1E7B2137BA064AB5EB24A3FAAA3302DD3B302FF7B7B268CF20F214BA94BCEBBA99F7137E5107A121DAE0AE43819
	74CE005BEA36480CD4414F4A207B7AD28FC5333D32D7E84CDC8FF635FF95774F366C26EB97C88FF35CAF8A69B732A09D45F1836938379B4BF1E7CB787EA547C552F3562DB7C37AE6443D444EDEA1ED3A9967A91BC5ED637D1A5F7F71A51C533DE598CF7DCF8133B9430A3E76F2BFC8D674F588473DEDC5DFCF6338C8B37ABA0A63FE160B3E0E6538B59674F5EEB862DAAC686B2D61686B07B57E0D537CBEB521FEE0B25CDF022F6369FE569E30371E7BE137997D554A7DD0AE29366F537C7E09E9627D7074044AE0
	B6BFA4E13B194D7DF0478A021B8369799C1711457BAD473DEC22ED6996C85F425F27517868971A5F9DDF4239F17F044AE0B61F6CC1FFB55E02FED8CBFD5389E997475D1C077E9A6638DF75925C82C8A7F2FFFD47267AE80E66F715ED627D7545044AE0B6D7523D6025E0EF462D6807EF5851B73937229F329D2A6DEFE8FE4F30CF3C9FE23E8499874FEB57D81DE037FC6B686F1F30B863A5AF59971B8A3433816683A482AC854839ADBEBB9F209C209E2093408B908A90819083B08FE091C052ED9817C4AFA75FF7
	8F34CED08EFEE8F062962DA4BC41F70DADDA7D05E930A61EB63A7E0C6FE43B4FBDD160298C424774ACB3135FBCDEE3CB96A52F42CCE2AF7EADC67A59CD7685AEA6730FBEF79B1BC01EE9CC3AC61F5072F901A7F4CCFC0F9F5672C5GCF4C18789E7251F3BE70A40C096FD11F3B0A494013B1FDACBE1DB198B8461CB598BC463C075E2717A0B6EC9DF2A9770F238749A25C59536311F8AC7B7D41DC0EEB70B4B9BDD6D2C3EC8D332A5CF75F7DE797C8F3E2F9FBF613BDBF3B68355A7B6DAD568B1232175BC4634D8D
	E063566938E770C2A1598FF639259E496D16CC149A77FE53F7907AA6206D180EFB3FF750D8F4AA244327631EF3FF896EB94FB3701F765C04FEA38DE88860D9CD52F47DD944BFE6117A416F7DB1393F4C42FD77E5DC772CECEAA7B4E0DFBB7333693BF737C35F3A9D573049746C0B655F406E2EC1DE636DE8D7B545F9A19D42F10F51B8A69A52F3EFC7DB9767222DC9379B5B3AE294DB05997A7D75BD9634ABE7864A9D2638CD10AE180136BE5F63DB5F6BC6C8F1CC2723E65429F5067199C72A268EACC0B3BC0369
	C452E1FEBA3DC0E31919000F64729FD861ABFF17666CECE396BF0B8AA0DFDB7FC9C0C3G771504197A7A4F117C6B7FFD097BE9A67AE9920DA1B6C13AE0A66AD910652B67A94D19595DF97C0CEF26311E5A3A33818D8154F3121F1EAF757A6B19E5C3BDC339CEADF6DF1D68B12722539CBB6AB4B300CEEBB47E0B861AF9B3553ADB2451C93903C8777C96ABF8066B223894854746164C2140E3D84ED070B1EC7D1071BAF963107FBAB9FF48F81D7C6110F61D4CF09F8F91DB4DB0FF36F33AE20EBB45F1BBE91984F7
	52EA12AD9C3F176AC5707592910878AA2A57A04535AB38039A5CA985F7F8080C43E0324569C65EA6FA165BE3355914F795A90D558143B27747A92A2355A5EA74BE4BF127F8DDBCE4712F1F1FD8347543F00EF6FFBACF3BBF5D72FEFF3A577B7D699EBE61CF376004BFDD52E1FF3A178E7B53AD127D691E127D691EB96BCF777B33DABAE52F148E854A7BF10564DDCB1FBD4BDA2BDDF210BF0AD6D0D618DF7269F64E7763B4152FB7FD6CFCE119AADFF349156D115ED5027B109B3EB65ADE737D4BD599AFE6796E51
	7EF2051A7738F96CFA3F2A5983DFED9DBB5FFE334A7735AB10B7432A72BD14FBE5FEDA3F967D5474B51C077F613CDA5EB10B2FCF1E69D5730AADE3511149394C3FA5B75530BE1277872E238C3B7A3B53294A3FF94515595A1805BA440E5AA63E62D2E5D4C83E76AF566CDBE7E6DD197CDF67217CE5234ADF61D5E5EC36796E65BF4F6F9B95FF7DCA7D78FDFA8F1C4DD3620764FCCAD9D750611961B7F9D729F89C1E15D87C1E92EDACEE817943DF9FCB8C7F5735DA3EB06019BDA63E901F77D467G4F02B1719D77
	79F6CBA270AC9D93DFE21E4F59649D71591B6E988B5F9FFC4E940B0127B6889F9BE3FFDC62BF6EBEDC62BFBE172C75275B35561FEE06591FAE426CCFE749722733E779537DFB1EBFDDCA1EBFDD26431FEE25C3CB477E467093FDE43192752210A98B59071A2BCE3554D4B55649426B550DA76A4E359EBB57DCD7CBF27045A11283CB61FC59195FAC19E4A1033C8EA58B4B595BCF10BEE2F5D41007682D703805B53C97B650E9BFF1202CA27DF75FCCCF93B29A0465AC8CEA90B2C8AC53A0E452D027C1C807B0486B
	D23A49CA25FB881EBE6112009D4D9FEBCC6B0BF58B2BFCC605FF7E464E5FAF78CD1BA7DDD85EA03CCDAEEC35C8EE767795CC645B854A9F94BDA8E43B1CD64884BCA86B210FCC0FBCE4139A053C1EE6720720BC32E462DFB2744842FA7AF56ECF7AE52B6D13FA51E311C9943837D148F0106F9E8AEF30EC72716ECF140FF7D30BD32B8ED01DF394FD57D1FDC9A9C53C14F5BFDA7341164F974FC997528E8AABEDCE51F4D06C161D6E86A1DB3610BFAD6134193C2E7D2C20AC3AE626772820380D5CFCE2EC91F98D0D
	AB5D6B7DDA682AA52B8EC2E060F58B1FD0CA92FB97F18AD30DC5A4FFD4C2F2B74864CF39BA5DDBF95ED56F4A9F7E6C50DB9DE9C22AE2A42902B5DE97340D9E51EE358D418FB686534AD9870D8A62E40707FC9A9DB44B57E906D9DCC0514D0F945F726D573E2082D4D6D9584CDC892C1F18A5277D132A1E9E2BC3628500DF2178F7F8710426C8C773067726751FBF7E1FDD508EEA05F4FBF1B17D3F047EDFCA7EAF2118920AA9A98567A68B237E5367FB51E65AF8F83F205FEEBF88B5A87F6A45C7E67D3AB2B9892C
	BBA8E4524FE8C3A5642AD4C77B6DDD127B6A28DB5EF9BDBC71BAD2C7AE52D43C245C373CBE6D9D97BE2C972C72EBD5843B563FF99074FADEC9A43501127E157E7F4E951BCC38B6D35EADDC53ED23E92A1D9C7DFBBC474AC2EABE03AC720DFC72B5B95FE1C796D654103F6961A183509B7EE1D0558DCFBE91F52EB6AFCDC803B148A231C1A7F310CC3834931219BA1D8AAF77C97DCD3972D36FA8CE7355FB34545F14CDB5E337A9CB13CF61EF4F1A467FB55D32E5AC5D92BDCD7E31BBCF5BAFEB6FE6AF1B529D70EE
	7601C896FB35C62AF71E587942C67A37F642G9F9509E7843D7D64EF3A863A7754E297EDB6D93475D6B9CC648ED47510AC2977A87287C8799E24D1B2D95EB67A1D18739A1E7F82D0CB87887FE2846C1DA5GG4401GGD0CB818294G94G88G88GF8FBB0B67FE2846C1DA5GG4401GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG57A5GGGG
**end of data**/
}
}
