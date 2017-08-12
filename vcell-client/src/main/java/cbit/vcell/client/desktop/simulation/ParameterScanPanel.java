/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.simulation;
import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.vcell.util.gui.ButtonGroupCivilized;

import cbit.vcell.math.Constant;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ConstantArraySpec;
/**
 * Insert the type's description here.
 * Creation date: (9/13/2005 9:38:19 AM)
 * @author: Ion Moraru
 */
public class ParameterScanPanel extends JPanel {
	private ButtonGroupCivilized ivjButtonGroupCivilized1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JCheckBox ivjJCheckBoxLog = null;
	private JLabel ivjJLabelMax = null;
	private JLabel ivjJLabelMin = null;
	private JLabel ivjJLabelNumber = null;
	private JLabel ivjJLabelValues = null;
	private JRadioButton ivjJRadioButtonList = null;
	private JRadioButton ivjJRadioButtonRange = null;
	private JTextField ivjJTextFieldMax = null;
	private JTextField ivjJTextFieldMin = null;
	private JTextField ivjJTextFieldNumber = null;
	private JTextField ivjJTextFieldValues = null;
	private ConstantArraySpec fieldConstantArraySpec = ConstantArraySpec.createIntervalSpec("", 0, 0, 0, false);

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ParameterScanPanel.this.getButtonGroupCivilized1() && (evt.getPropertyName().equals("selection"))) 
				connEtoC1(evt);
			if (evt.getSource() == ParameterScanPanel.this && (evt.getPropertyName().equals("constantArraySpec"))) 
				connEtoC2(evt);
		};
	};

public ParameterScanPanel() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (9/23/2005 2:25:09 PM)
 */
public void applyValues() throws ExpressionException{
	if (getJRadioButtonList().isSelected()) {
		java.util.StringTokenizer tokens = new java.util.StringTokenizer(getJTextFieldValues().getText(), ", ");
		String[] values = new String[tokens.countTokens()];
		int i = 0;
		while (tokens.hasMoreElements()) {
			values[i] = tokens.nextToken();
			i++;
		}
		setConstantArraySpec(ConstantArraySpec.createListSpec(getConstantArraySpec().getName(), values));
	} else if (getJRadioButtonRange().isSelected()) {
		setConstantArraySpec(ConstantArraySpec.createIntervalSpec(
			getConstantArraySpec().getName(),
			Double.parseDouble(getJTextFieldMin().getText()),
			Double.parseDouble(getJTextFieldMax().getText()),
			Integer.parseInt(getJTextFieldNumber().getText()),
			getJCheckBoxLog().isSelected()
			));
	}
}
	
/**
 * connEtoC1:  (ButtonGroupCivilized1.selection --> ParameterScanPanel.enableComponents()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.enableComponents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ParameterScanPanel.constantArraySpec --> ParameterScanPanel.initFields(Lcbit.vcell.solver.ConstantArraySpec;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.initFields(this.getConstantArraySpec());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (ParameterScanPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonList());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoM2:  (ParameterScanPanel.initialize() --> ButtonGroupCivilized1.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroupCivilized1().add(getJRadioButtonRange());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
private void enableComponents() {
	if (getJRadioButtonRange().isSelected()) {
		getJLabelMin().setEnabled(true);
		getJLabelMax().setEnabled(true);
		getJLabelNumber().setEnabled(true);
		getJTextFieldMin().setEnabled(true);
		getJTextFieldMax().setEnabled(true);
		getJTextFieldNumber().setEnabled(true);
		getJCheckBoxLog().setEnabled(true);
		getJLabelValues().setEnabled(false);
		getJTextFieldValues().setEnabled(false);
	} else {
		getJLabelMin().setEnabled(false);
		getJLabelMax().setEnabled(false);
		getJLabelNumber().setEnabled(false);
		getJTextFieldMin().setEnabled(false);
		getJTextFieldMax().setEnabled(false);
		getJTextFieldNumber().setEnabled(false);
		getJCheckBoxLog().setEnabled(false);
		getJLabelValues().setEnabled(true);
		getJTextFieldValues().setEnabled(true);
	}		
}


/**
 * Return the ButtonGroupCivilized1 property value.
 * @return cbit.gui.ButtonGroupCivilized
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ButtonGroupCivilized getButtonGroupCivilized1() {
	if (ivjButtonGroupCivilized1 == null) {
		try {
			ivjButtonGroupCivilized1 = new ButtonGroupCivilized();
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
 * Gets the constantArraySpec property (cbit.vcell.solver.ConstantArraySpec) value.
 * @return The constantArraySpec property value.
 * @see #setConstantArraySpec
 */
public ConstantArraySpec getConstantArraySpec() {
	return fieldConstantArraySpec;
}


/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxLog() {
	if (ivjJCheckBoxLog == null) {
		try {
			ivjJCheckBoxLog = new javax.swing.JCheckBox();
			ivjJCheckBoxLog.setName("JCheckBoxLog");
			ivjJCheckBoxLog.setText("Log");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxLog;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMax() {
	if (ivjJLabelMax == null) {
		try {
			ivjJLabelMax = new javax.swing.JLabel();
			ivjJLabelMax.setName("JLabelMax");
			ivjJLabelMax.setText("Max");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMax;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMin() {
	if (ivjJLabelMin == null) {
		try {
			ivjJLabelMin = new javax.swing.JLabel();
			ivjJLabelMin.setName("JLabelMin");
			ivjJLabelMin.setText("Min");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMin;
}

/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNumber() {
	if (ivjJLabelNumber == null) {
		try {
			ivjJLabelNumber = new javax.swing.JLabel();
			ivjJLabelNumber.setName("JLabelNumber");
			ivjJLabelNumber.setText("# of values");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNumber;
}

/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelValues() {
	if (ivjJLabelValues == null) {
		try {
			ivjJLabelValues = new javax.swing.JLabel();
			ivjJLabelValues.setName("JLabelValues");
			ivjJLabelValues.setText("Values");
			ivjJLabelValues.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelValues;
}

/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonList() {
	if (ivjJRadioButtonList == null) {
		try {
			ivjJRadioButtonList = new javax.swing.JRadioButton();
			ivjJRadioButtonList.setName("JRadioButtonList");
			ivjJRadioButtonList.setSelected(false);
			ivjJRadioButtonList.setText("List");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonList;
}

/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonRange() {
	if (ivjJRadioButtonRange == null) {
		try {
			ivjJRadioButtonRange = new javax.swing.JRadioButton();
			ivjJRadioButtonRange.setName("JRadioButtonRange");
			ivjJRadioButtonRange.setSelected(true);
			ivjJRadioButtonRange.setText("Range");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonRange;
}

/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMax() {
	if (ivjJTextFieldMax == null) {
		try {
			ivjJTextFieldMax = new javax.swing.JTextField();
			ivjJTextFieldMax.setName("JTextFieldMax");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMax;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMin() {
	if (ivjJTextFieldMin == null) {
		try {
			ivjJTextFieldMin = new javax.swing.JTextField();
			ivjJTextFieldMin.setName("JTextFieldMin");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMin;
}

/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldNumber() {
	if (ivjJTextFieldNumber == null) {
		try {
			ivjJTextFieldNumber = new javax.swing.JTextField();
			ivjJTextFieldNumber.setName("JTextFieldNumber");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldNumber;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldValues() {
	if (ivjJTextFieldValues == null) {
		try {
			ivjJTextFieldValues = new javax.swing.JTextField();
			ivjJTextFieldValues.setName("JTextFieldValues");
			ivjJTextFieldValues.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldValues;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getButtonGroupCivilized1().addPropertyChangeListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
}

/**
 * Comment
 */
private void initFields(ConstantArraySpec spec) {
	switch (spec.getType()) {
		case ConstantArraySpec.TYPE_LIST: {
			getJRadioButtonList().setSelected(true);
			Constant[] cs = spec.getConstants();
			String list = "";
			for (int i = 0; i < cs.length; i++){
				list += cs[i].getExpression().infix() + ", ";
			}
			list = list.substring(0, list.length() - 2);
			getJTextFieldValues().setText(list);
			break;
		}
		case ConstantArraySpec.TYPE_INTERVAL: {
			getJRadioButtonRange().setSelected(true);
			getJTextFieldMin().setText(""+spec.getMinValue());
			getJTextFieldMax().setText(""+spec.getMaxValue());
			getJTextFieldNumber().setText(""+spec.getNumValues());
			getJCheckBoxLog().setSelected(spec.isLogInterval());
			break;
		}
	}
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ParameterScanPanel");
		setLayout(new java.awt.GridBagLayout());
		setPreferredSize(new Dimension(250, 130));
		setMinimumSize(getPreferredSize());


		java.awt.GridBagConstraints constraintsJRadioButtonList = new java.awt.GridBagConstraints();
		constraintsJRadioButtonList.gridx = 0; constraintsJRadioButtonList.gridy = 0;
		constraintsJRadioButtonList.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonList.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJRadioButtonList(), constraintsJRadioButtonList);

		java.awt.GridBagConstraints constraintsJRadioButtonRange = new java.awt.GridBagConstraints();
		constraintsJRadioButtonRange.gridx = 0; constraintsJRadioButtonRange.gridy = 1;
		constraintsJRadioButtonRange.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonRange.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJRadioButtonRange(), constraintsJRadioButtonRange);

		java.awt.GridBagConstraints constraintsJTextFieldValues = new java.awt.GridBagConstraints();
		constraintsJTextFieldValues.gridx = 2; constraintsJTextFieldValues.gridy = 0;
		constraintsJTextFieldValues.gridwidth = 2;
		constraintsJTextFieldValues.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldValues.weightx = 1.0;
		constraintsJTextFieldValues.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextFieldValues(), constraintsJTextFieldValues);

		java.awt.GridBagConstraints constraintsJTextFieldMin = new java.awt.GridBagConstraints();
		constraintsJTextFieldMin.gridx = 2; constraintsJTextFieldMin.gridy = 1;
		constraintsJTextFieldMin.gridwidth = 2;
		constraintsJTextFieldMin.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMin.weightx = 1.0;
		constraintsJTextFieldMin.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextFieldMin(), constraintsJTextFieldMin);

		java.awt.GridBagConstraints constraintsJCheckBoxLog = new java.awt.GridBagConstraints();
		constraintsJCheckBoxLog.gridx = 3; constraintsJCheckBoxLog.gridy = 3;
		constraintsJCheckBoxLog.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJCheckBoxLog(), constraintsJCheckBoxLog);

		java.awt.GridBagConstraints constraintsJTextFieldMax = new java.awt.GridBagConstraints();
		constraintsJTextFieldMax.gridx = 2; constraintsJTextFieldMax.gridy = 2;
		constraintsJTextFieldMax.gridwidth = 2;
		constraintsJTextFieldMax.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMax.weightx = 1.0;
		constraintsJTextFieldMax.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextFieldMax(), constraintsJTextFieldMax);

		java.awt.GridBagConstraints constraintsJTextFieldNumber = new java.awt.GridBagConstraints();
		constraintsJTextFieldNumber.gridx = 2; constraintsJTextFieldNumber.gridy = 3;
		constraintsJTextFieldNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldNumber.weightx = 1.0;
		constraintsJTextFieldNumber.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJTextFieldNumber(), constraintsJTextFieldNumber);

		java.awt.GridBagConstraints constraintsJLabelMin = new java.awt.GridBagConstraints();
		constraintsJLabelMin.gridx = 1; constraintsJLabelMin.gridy = 1;
		constraintsJLabelMin.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMin.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelMin(), constraintsJLabelMin);

		java.awt.GridBagConstraints constraintsJLabelMax = new java.awt.GridBagConstraints();
		constraintsJLabelMax.gridx = 1; constraintsJLabelMax.gridy = 2;
		constraintsJLabelMax.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMax.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelMax(), constraintsJLabelMax);

		java.awt.GridBagConstraints constraintsJLabelNumber = new java.awt.GridBagConstraints();
		constraintsJLabelNumber.gridx = 1; constraintsJLabelNumber.gridy = 3;
		constraintsJLabelNumber.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelNumber.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelNumber(), constraintsJLabelNumber);

		java.awt.GridBagConstraints constraintsJLabelValues = new java.awt.GridBagConstraints();
		constraintsJLabelValues.gridx = 1; constraintsJLabelValues.gridy = 0;
		constraintsJLabelValues.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelValues.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJLabelValues(), constraintsJLabelValues);
		initConnections();
		connEtoM1();
		connEtoM2();
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
		JFrame frame = new javax.swing.JFrame();
		ParameterScanPanel aParameterScanPanel;
		aParameterScanPanel = new ParameterScanPanel();
		frame.setContentPane(aParameterScanPanel);
		frame.setSize(aParameterScanPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Sets the constantArraySpec property (cbit.vcell.solver.ConstantArraySpec) value.
 * @param constantArraySpec The new value for the property.
 * @see #getConstantArraySpec
 */
public void setConstantArraySpec(ConstantArraySpec constantArraySpec) {
	ConstantArraySpec oldValue = fieldConstantArraySpec;
	fieldConstantArraySpec = constantArraySpec;
	firePropertyChange("constantArraySpec", oldValue, constantArraySpec);
}

}
