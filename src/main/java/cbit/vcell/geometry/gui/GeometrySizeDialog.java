/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Hashtable;

import javax.swing.JPanel;

import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.util.gui.GuiUtils;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class GeometrySizeDialog extends javax.swing.JDialog implements ActionListener, PropertyChangeListener {
	private javax.swing.JButton ivjButton1 = null;
	private javax.swing.JButton ivjButton2 = null;
	private cbit.vcell.geometry.Geometry ivjGeometry = null;
	private javax.swing.JTextField ivjOriginXTextField = null;
	private javax.swing.JTextField ivjOriginYTextField = null;
	private javax.swing.JTextField ivjOriginZTextField = null;
	private javax.swing.JTextField ivjSizeXTextField = null;
	private javax.swing.JTextField ivjSizeYTextField = null;
	private javax.swing.JTextField ivjSizeZTextField = null;
	private javax.swing.JLabel ivjOriginLabel = null;
	private javax.swing.JLabel ivjOriginXLabel = null;
	private javax.swing.JLabel ivjOriginXUnitLabel = null;
	private javax.swing.JLabel ivjOriginYLabel = null;
	private javax.swing.JLabel ivjOriginYUnitLabel = null;
	private javax.swing.JLabel ivjOriginZLabel = null;
	private javax.swing.JLabel ivjOriginZUnitLabel = null;
	private javax.swing.JLabel ivjSizeLabel = null;
	private javax.swing.JLabel ivjSizeXLabel = null;
	private javax.swing.JLabel ivjSizeXUnitLabel = null;
	private javax.swing.JLabel ivjSizeYLabel = null;
	private javax.swing.JLabel ivjSizeYUnitLabel = null;
	private javax.swing.JLabel ivjSizeZLabel = null;
	private javax.swing.JLabel ivjSizeZUnitLabel = null;
	private javax.swing.JPanel buttonPanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GeometrySizeDialog(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GeometrySizeDialog(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
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
	if (e.getSource() == getButton2()) 
		connEtoC2(e);
	if (e.getSource() == getButton1()) 
		connEtoM5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC2:  (Button2.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySizeDialog.Ok()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		try {
			this.Ok();
			connEtoM6();
		} catch (Exception e) {
			e.printStackTrace();
			PopupGenerator.showErrorDialog(this, "Error\n"+e.getMessage(), e);
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM5:  (Button1.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySizeDialog.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM6:  ( (Button2,action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySizeDialog,Ok()V).normalResult --> GeometrySizeDialog.dispose()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM6() {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the Button1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getButton1() {
	if (ivjButton1 == null) {
		try {
			ivjButton1 = new javax.swing.JButton();
			ivjButton1.setName("Button1");
			ivjButton1.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButton1;
}
/**
 * Return the Button2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getButton2() {
	if (ivjButton2 == null) {
		try {
			ivjButton2 = new javax.swing.JButton();
			ivjButton2.setName("Button2");
			ivjButton2.setText("OK");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButton2;
}
/**
 * Return the Geometry property value.
 * @return cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private Geometry getGeometry() {
	// user code begin {1}
	// user code end
	return ivjGeometry;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getButtonPanel() {
	if (buttonPanel == null) {
		try {
			buttonPanel = new javax.swing.JPanel();
			buttonPanel.setName("JPanel1");
			buttonPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsButton2 = new java.awt.GridBagConstraints();
			constraintsButton2.gridx = 1; constraintsButton2.gridy = 1;
			constraintsButton2.ipadx = 20;
			constraintsButton2.insets = new java.awt.Insets(0, 5, 0, 5);
			getButtonPanel().add(getButton2(), constraintsButton2);

			java.awt.GridBagConstraints constraintsButton1 = new java.awt.GridBagConstraints();
			constraintsButton1.gridx = 2; constraintsButton1.gridy = 1;
			constraintsButton1.insets = new java.awt.Insets(0, 5, 0, 5);
			getButtonPanel().add(getButton1(), constraintsButton1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return buttonPanel;
}
/**
 * Return the OriginLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOriginLabel() {
	if (ivjOriginLabel == null) {
		try {
			ivjOriginLabel = GuiUtils.createBoldJLabel();
			ivjOriginLabel.setName("OriginLabel");
			ivjOriginLabel.setText("Origin");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginLabel;
}
/**
 * Return the OriginXLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOriginXLabel() {
	if (ivjOriginXLabel == null) {
		try {
			ivjOriginXLabel = new javax.swing.JLabel();
			ivjOriginXLabel.setName("OriginXLabel");
			ivjOriginXLabel.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginXLabel;
}
/**
 * Return the OriginXTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOriginXTextField() {
	if (ivjOriginXTextField == null) {
		try {
			ivjOriginXTextField = new javax.swing.JTextField();
			ivjOriginXTextField.setName("OriginXTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginXTextField;
}
/**
 * Return the OriginXUnitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOriginXUnitLabel() {
	if (ivjOriginXUnitLabel == null) {
		try {
			ivjOriginXUnitLabel = new javax.swing.JLabel();
			ivjOriginXUnitLabel.setName("OriginXUnitLabel");
			ivjOriginXUnitLabel.setText("\u03BCm");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginXUnitLabel;
}
/**
 * Return the OriginYLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOriginYLabel() {
	if (ivjOriginYLabel == null) {
		try {
			ivjOriginYLabel = new javax.swing.JLabel();
			ivjOriginYLabel.setName("OriginYLabel");
			ivjOriginYLabel.setText("Y");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginYLabel;
}
/**
 * Return the OriginYTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOriginYTextField() {
	if (ivjOriginYTextField == null) {
		try {
			ivjOriginYTextField = new javax.swing.JTextField();
			ivjOriginYTextField.setName("OriginYTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginYTextField;
}
/**
 * Return the OriginYUnitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOriginYUnitLabel() {
	if (ivjOriginYUnitLabel == null) {
		try {
			ivjOriginYUnitLabel = new javax.swing.JLabel();
			ivjOriginYUnitLabel.setName("OriginYUnitLabel");
			ivjOriginYUnitLabel.setText("\u03BCm");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginYUnitLabel;
}
/**
 * Return the OriginZLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOriginZLabel() {
	if (ivjOriginZLabel == null) {
		try {
			ivjOriginZLabel = new javax.swing.JLabel();
			ivjOriginZLabel.setName("OriginZLabel");
			ivjOriginZLabel.setText("Z");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginZLabel;
}
/**
 * Return the OriginZTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOriginZTextField() {
	if (ivjOriginZTextField == null) {
		try {
			ivjOriginZTextField = new javax.swing.JTextField();
			ivjOriginZTextField.setName("OriginZTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginZTextField;
}
/**
 * Return the OriginZUnitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOriginZUnitLabel() {
	if (ivjOriginZUnitLabel == null) {
		try {
			ivjOriginZUnitLabel = new javax.swing.JLabel();
			ivjOriginZUnitLabel.setName("OriginZUnitLabel");
			ivjOriginZUnitLabel.setText("\u03BCm");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOriginZUnitLabel;
}
/**
 * Return the SizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeLabel() {
	if (ivjSizeLabel == null) {
		try {
			ivjSizeLabel = GuiUtils.createBoldJLabel();
			ivjSizeLabel.setName("SizeLabel");
			ivjSizeLabel.setText("Size");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeLabel;
}
/**
 * Return the SizeXLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeXLabel() {
	if (ivjSizeXLabel == null) {
		try {
			ivjSizeXLabel = new javax.swing.JLabel();
			ivjSizeXLabel.setName("SizeXLabel");
			ivjSizeXLabel.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeXLabel;
}
/**
 * Return the SizeXTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSizeXTextField() {
	if (ivjSizeXTextField == null) {
		try {
			ivjSizeXTextField = new javax.swing.JTextField();
			ivjSizeXTextField.setName("SizeXTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeXTextField;
}
/**
 * Return the SizeXUnitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeXUnitLabel() {
	if (ivjSizeXUnitLabel == null) {
		try {
			ivjSizeXUnitLabel = new javax.swing.JLabel();
			ivjSizeXUnitLabel.setName("SizeXUnitLabel");
			ivjSizeXUnitLabel.setText("\u03BCm");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeXUnitLabel;
}
/**
 * Return the SizeYLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeYLabel() {
	if (ivjSizeYLabel == null) {
		try {
			ivjSizeYLabel = new javax.swing.JLabel();
			ivjSizeYLabel.setName("SizeYLabel");
			ivjSizeYLabel.setText("Y");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeYLabel;
}
/**
 * Return the SizeYTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSizeYTextField() {
	if (ivjSizeYTextField == null) {
		try {
			ivjSizeYTextField = new javax.swing.JTextField();
			ivjSizeYTextField.setName("SizeYTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeYTextField;
}
/**
 * Return the SizeYUnitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeYUnitLabel() {
	if (ivjSizeYUnitLabel == null) {
		try {
			ivjSizeYUnitLabel = new javax.swing.JLabel();
			ivjSizeYUnitLabel.setName("SizeYUnitLabel");
			ivjSizeYUnitLabel.setText("\u03BCm");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeYUnitLabel;
}
/**
 * Return the SizeZLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeZLabel() {
	if (ivjSizeZLabel == null) {
		try {
			ivjSizeZLabel = new javax.swing.JLabel();
			ivjSizeZLabel.setName("SizeZLabel");
			ivjSizeZLabel.setText("Z");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeZLabel;
}
/**
 * Return the SizeZTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSizeZTextField() {
	if (ivjSizeZTextField == null) {
		try {
			ivjSizeZTextField = new javax.swing.JTextField();
			ivjSizeZTextField.setName("SizeZTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeZTextField;
}
/**
 * Return the SizeZUnitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeZUnitLabel() {
	if (ivjSizeZUnitLabel == null) {
		try {
			ivjSizeZUnitLabel = new javax.swing.JLabel();
			ivjSizeZUnitLabel.setName("SizeZUnitLabel");
			ivjSizeZUnitLabel.setText("\u03BCm");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSizeZUnitLabel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION in GeometrySizeDialog ---------");
	exception.printStackTrace(System.out);
	if (exception instanceof PropertyVetoException){
		javax.swing.JOptionPane.showMessageDialog(this, exception.getMessage(), "input error", javax.swing.JOptionPane.ERROR_MESSAGE);
	}
}
/**
 * This method was created in VisualAge.
 * @param geometry cbit.vcell.geometry.Geometry
 */
public void init(Geometry geometry) {
	setGeometry(geometry); 
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getButton2().addActionListener(this);
	getButton1().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GeometrySizeDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(512, 165);
		setModal(true);
		setTitle("Geometry Size");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		
		int gridy = 0;
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 10, 4, 15);
		gbc.anchor = GridBagConstraints.LINE_END;
		mainPanel.add(getSizeLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 0);
		mainPanel.add(getSizeXLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 2);
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(getSizeXTextField(), gbc);
		
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 0, 4, 15);
		mainPanel.add(getSizeXUnitLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 0);
		mainPanel.add(getSizeYLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 2);
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(getSizeYTextField(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 0, 4, 15);
		mainPanel.add(getSizeYUnitLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 0);
		mainPanel.add(getSizeZLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 2);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		mainPanel.add(getSizeZTextField(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 0, 4, 15);
		mainPanel.add(getSizeZUnitLabel(), gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 10, 4, 15);
		mainPanel.add(getOriginLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new java.awt.Insets(4, 4, 4, 0);
		mainPanel.add(getOriginXLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 2);
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(getOriginXTextField(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 0, 4, 15);
		mainPanel.add(getOriginXUnitLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 0);
		mainPanel.add(getOriginYLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE; 
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;		
		gbc.insets = new java.awt.Insets(4, 4, 4, 2);
		mainPanel.add(getOriginYTextField(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 0, 4, 15);
		mainPanel.add(getOriginYUnitLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 4, 4, 0);
		mainPanel.add(getOriginZLabel(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;		
		gbc.insets = new java.awt.Insets(4, 4, 4, 2);
		mainPanel.add(getOriginZTextField(), gbc);

		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = gridy;
		gbc.insets = new java.awt.Insets(4, 0, 4, 15);
		mainPanel.add(getOriginZUnitLabel(), gbc);
		
		gridy ++;
		gbc = new java.awt.GridBagConstraints();
		gbc.gridx = 0; 
		gbc.gridy = gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new java.awt.Insets(10, 4, 4, 4);
		mainPanel.add(getButtonPanel(), gbc);
		
		add(mainPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
private void Ok() throws PropertyVetoException {

	final double worldExtentX = Double.valueOf(getSizeXTextField().getText()).doubleValue();
	final double worldExtentY = Double.valueOf(getSizeYTextField().getText()).doubleValue();
	final double worldExtentZ = Double.valueOf(getSizeZTextField().getText()).doubleValue();

	final double worldOriginX = Double.valueOf(getOriginXTextField().getText()).doubleValue();
	final double worldOriginY = Double.valueOf(getOriginYTextField().getText()).doubleValue();
	final double worldOriginZ = Double.valueOf(getOriginZTextField().getText()).doubleValue();

	final GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
	AsynchClientTask extentOriginTask = new AsynchClientTask("Changing domain",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			geometrySpec.setExtent(new Extent(worldExtentX,worldExtentY,worldExtentZ));
			geometrySpec.setOrigin(new Origin(worldOriginX,worldOriginY,worldOriginZ));
			getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
		}
	};
	ClientTaskDispatcher.dispatch(this.getParent(), new Hashtable<String, Object>(), new AsynchClientTask[] {extentOriginTask}, false);
}
/**
 * Insert the method's description here.
 * Creation date: (6/5/00 3:00:37 PM)
 * @param event java.beans.PropertyChangeEvent
 */
public void propertyChange(PropertyChangeEvent event) {
	if (event.getSource() == getGeometry().getGeometrySpec() && event.getPropertyName().equals("extent")){
		refreshSize();
	}else if (event.getSource() == getGeometry().getGeometrySpec() && event.getPropertyName().equals("origin")){
		refreshSize();
	}
}
/**
 * This method was created in VisualAge.
 */
private void refreshSize() {
	int dim = getGeometry().getDimension();

	getSizeXTextField().setText(Double.toString(getGeometry().getExtent().getX()));
	getSizeYTextField().setText(Double.toString(getGeometry().getExtent().getY()));
	getSizeZTextField().setText(Double.toString(getGeometry().getExtent().getZ()));

	getOriginXTextField().setText(Double.toString(getGeometry().getOrigin().getX()));
	getOriginYTextField().setText(Double.toString(getGeometry().getOrigin().getY()));
	getOriginZTextField().setText(Double.toString(getGeometry().getOrigin().getZ()));

	getSizeLabel().setEnabled(dim>0);
	getSizeXLabel().setEnabled(dim>0);
	getSizeXTextField().setEnabled(dim>0);
	getSizeXUnitLabel().setEnabled(dim>0);
	
	getSizeYLabel().setEnabled(dim>1);
	getSizeYTextField().setEnabled(dim>1);
	getSizeYUnitLabel().setEnabled(dim>1);
	
	getSizeZLabel().setEnabled(dim>2);
	getSizeZTextField().setEnabled(dim>2);
	getSizeZUnitLabel().setEnabled(dim>2);
	
	getOriginLabel().setEnabled(dim>0);
	getOriginXLabel().setEnabled(dim>0);
	getOriginXTextField().setEnabled(dim>0);
	getOriginXUnitLabel().setEnabled(dim>0);
	
	getOriginYLabel().setEnabled(dim>1);
	getOriginYTextField().setEnabled(dim>1);
	getOriginYUnitLabel().setEnabled(dim>1);
	
	getOriginZLabel().setEnabled(dim>2);
	getOriginZTextField().setEnabled(dim>2);
	getOriginZUnitLabel().setEnabled(dim>2);

}
/**
 * Set the Geometry to a new value.
 * @param newValue cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometry(Geometry newValue) {
	try {
		if (ivjGeometry != newValue) {
			ivjGeometry = newValue;
			// user code begin {1}
			ivjGeometry.getGeometrySpec().removePropertyChangeListener(this);
			ivjGeometry.getGeometrySpec().addPropertyChangeListener(this);
			// user code end
		}
		if (ivjGeometry != null) {
			refreshSize();
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

}
