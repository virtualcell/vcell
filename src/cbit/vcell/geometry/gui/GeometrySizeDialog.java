package cbit.vcell.geometry.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import java.awt.event.*;
import java.beans.*;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.*;
/**
 * This type was created in VisualAge.
 */
public class GeometrySizeDialog extends javax.swing.JDialog implements ActionListener, PropertyChangeListener {
	private javax.swing.JButton ivjButton1 = null;
	private javax.swing.JButton ivjButton2 = null;
	private javax.swing.JPanel ivjContentsPane = null;
	private cbit.vcell.geometry.Geometry ivjGeometry = null;
	private javax.swing.JTextField ivjOriginXTextField = null;
	private javax.swing.JTextField ivjOriginYTextField = null;
	private javax.swing.JTextField ivjOriginZTextField = null;
	private javax.swing.JPanel ivjPanel = null;
	private javax.swing.JPanel ivjPanel2 = null;
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
	private boolean bUpdating = false;
	private javax.swing.JPanel ivjJPanel1 = null;
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
			PopupGenerator.showErrorDialog("Error\n"+e.getMessage());
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
			cbit.gui.BevelBorderBean ivjLocalBorder1;
			ivjLocalBorder1 = new cbit.gui.BevelBorderBean();
			ivjLocalBorder1.setColor(new java.awt.Color(160,160,255));
			ivjButton1 = new javax.swing.JButton();
			ivjButton1.setName("Button1");
			ivjButton1.setBorder(ivjLocalBorder1);
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
			cbit.gui.BevelBorderBean ivjLocalBorder;
			ivjLocalBorder = new cbit.gui.BevelBorderBean();
			ivjLocalBorder.setColor(new java.awt.Color(160,160,255));
			ivjButton2 = new javax.swing.JButton();
			ivjButton2.setName("Button2");
			ivjButton2.setBorder(ivjLocalBorder);
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
 * Return the ContentsPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getContentsPane() {
	if (ivjContentsPane == null) {
		try {
			ivjContentsPane = new javax.swing.JPanel();
			ivjContentsPane.setName("ContentsPane");
			ivjContentsPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsPanel2 = new java.awt.GridBagConstraints();
			constraintsPanel2.gridx = 0; constraintsPanel2.gridy = 0;
			constraintsPanel2.gridwidth = 2;
			getContentsPane().add(getPanel2(), constraintsPanel2);

			java.awt.GridBagConstraints constraintsPanel = new java.awt.GridBagConstraints();
			constraintsPanel.gridx = 0; constraintsPanel.gridy = 1;
			constraintsPanel.gridwidth = 2;
			constraintsPanel.insets = new java.awt.Insets(10, 0, 10, 0);
			getContentsPane().add(getPanel(), constraintsPanel);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 2;
			constraintsJPanel1.gridwidth = 2;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.insets = new java.awt.Insets(15, 4, 4, 4);
			getContentsPane().add(getJPanel1(), constraintsJPanel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjContentsPane;
}
/**
 * Return the Geometry property value.
 * @return cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.Geometry getGeometry() {
	// user code begin {1}
	// user code end
	return ivjGeometry;
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

			java.awt.GridBagConstraints constraintsButton2 = new java.awt.GridBagConstraints();
			constraintsButton2.gridx = 1; constraintsButton2.gridy = 1;
			constraintsButton2.ipadx = 20;
			constraintsButton2.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanel1().add(getButton2(), constraintsButton2);

			java.awt.GridBagConstraints constraintsButton1 = new java.awt.GridBagConstraints();
			constraintsButton1.gridx = 2; constraintsButton1.gridy = 1;
			constraintsButton1.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanel1().add(getButton1(), constraintsButton1);
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
 * Return the OriginLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOriginLabel() {
	if (ivjOriginLabel == null) {
		try {
			ivjOriginLabel = new javax.swing.JLabel();
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
			ivjOriginXUnitLabel.setText("µm");
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
			ivjOriginYUnitLabel.setText("µm");
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
			ivjOriginZUnitLabel.setText("µm");
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
 * Return the Panel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getPanel() {
	if (ivjPanel == null) {
		try {
			ivjPanel = new javax.swing.JPanel();
			ivjPanel.setName("Panel");
			ivjPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOriginXLabel = new java.awt.GridBagConstraints();
			constraintsOriginXLabel.gridx = 1; constraintsOriginXLabel.gridy = 0;
			constraintsOriginXLabel.insets = new java.awt.Insets(0, 15, 0, 5);
			getPanel().add(getOriginXLabel(), constraintsOriginXLabel);

			java.awt.GridBagConstraints constraintsOriginXUnitLabel = new java.awt.GridBagConstraints();
			constraintsOriginXUnitLabel.gridx = 3; constraintsOriginXUnitLabel.gridy = 0;
			constraintsOriginXUnitLabel.insets = new java.awt.Insets(0, 5, 0, 15);
			getPanel().add(getOriginXUnitLabel(), constraintsOriginXUnitLabel);

			java.awt.GridBagConstraints constraintsOriginXTextField = new java.awt.GridBagConstraints();
			constraintsOriginXTextField.gridx = 2; constraintsOriginXTextField.gridy = 0;
			constraintsOriginXTextField.ipadx = 70;
			getPanel().add(getOriginXTextField(), constraintsOriginXTextField);

			java.awt.GridBagConstraints constraintsOriginLabel = new java.awt.GridBagConstraints();
			constraintsOriginLabel.gridx = 0; constraintsOriginLabel.gridy = 0;
			constraintsOriginLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOriginLabel.weightx = 1.0;
			constraintsOriginLabel.insets = new java.awt.Insets(0, 15, 0, 5);
			getPanel().add(getOriginLabel(), constraintsOriginLabel);

			java.awt.GridBagConstraints constraintsOriginYLabel = new java.awt.GridBagConstraints();
			constraintsOriginYLabel.gridx = 4; constraintsOriginYLabel.gridy = 0;
			constraintsOriginYLabel.insets = new java.awt.Insets(0, 5, 0, 5);
			getPanel().add(getOriginYLabel(), constraintsOriginYLabel);

			java.awt.GridBagConstraints constraintsOriginYTextField = new java.awt.GridBagConstraints();
			constraintsOriginYTextField.gridx = 5; constraintsOriginYTextField.gridy = 0;
			constraintsOriginYTextField.ipadx = 70;
			getPanel().add(getOriginYTextField(), constraintsOriginYTextField);

			java.awt.GridBagConstraints constraintsOriginYUnitLabel = new java.awt.GridBagConstraints();
			constraintsOriginYUnitLabel.gridx = 6; constraintsOriginYUnitLabel.gridy = 0;
			constraintsOriginYUnitLabel.insets = new java.awt.Insets(0, 5, 0, 15);
			getPanel().add(getOriginYUnitLabel(), constraintsOriginYUnitLabel);

			java.awt.GridBagConstraints constraintsOriginZLabel = new java.awt.GridBagConstraints();
			constraintsOriginZLabel.gridx = 7; constraintsOriginZLabel.gridy = 0;
			constraintsOriginZLabel.insets = new java.awt.Insets(0, 5, 0, 5);
			getPanel().add(getOriginZLabel(), constraintsOriginZLabel);

			java.awt.GridBagConstraints constraintsOriginZTextField = new java.awt.GridBagConstraints();
			constraintsOriginZTextField.gridx = 8; constraintsOriginZTextField.gridy = 0;
			constraintsOriginZTextField.ipadx = 70;
			getPanel().add(getOriginZTextField(), constraintsOriginZTextField);

			java.awt.GridBagConstraints constraintsOriginZUnitLabel = new java.awt.GridBagConstraints();
			constraintsOriginZUnitLabel.gridx = 9; constraintsOriginZUnitLabel.gridy = 0;
			constraintsOriginZUnitLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOriginZUnitLabel.weightx = 1.0;
			constraintsOriginZUnitLabel.insets = new java.awt.Insets(0, 5, 0, 15);
			getPanel().add(getOriginZUnitLabel(), constraintsOriginZUnitLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPanel;
}
/**
 * Return the Panel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getPanel2() {
	if (ivjPanel2 == null) {
		try {
			ivjPanel2 = new javax.swing.JPanel();
			ivjPanel2.setName("Panel2");
			ivjPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSizeXLabel = new java.awt.GridBagConstraints();
			constraintsSizeXLabel.gridx = 1; constraintsSizeXLabel.gridy = 0;
			constraintsSizeXLabel.insets = new java.awt.Insets(0, 5, 0, 5);
			getPanel2().add(getSizeXLabel(), constraintsSizeXLabel);

			java.awt.GridBagConstraints constraintsSizeXUnitLabel = new java.awt.GridBagConstraints();
			constraintsSizeXUnitLabel.gridx = 3; constraintsSizeXUnitLabel.gridy = 0;
			constraintsSizeXUnitLabel.insets = new java.awt.Insets(0, 5, 0, 15);
			getPanel2().add(getSizeXUnitLabel(), constraintsSizeXUnitLabel);

			java.awt.GridBagConstraints constraintsSizeXTextField = new java.awt.GridBagConstraints();
			constraintsSizeXTextField.gridx = 2; constraintsSizeXTextField.gridy = 0;
			constraintsSizeXTextField.ipadx = 70;
			getPanel2().add(getSizeXTextField(), constraintsSizeXTextField);

			java.awt.GridBagConstraints constraintsSizeLabel = new java.awt.GridBagConstraints();
			constraintsSizeLabel.gridx = 0; constraintsSizeLabel.gridy = 0;
			constraintsSizeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSizeLabel.weightx = 1.0;
			constraintsSizeLabel.ipadx = 10;
			constraintsSizeLabel.insets = new java.awt.Insets(0, 15, 0, 15);
			getPanel2().add(getSizeLabel(), constraintsSizeLabel);

			java.awt.GridBagConstraints constraintsSizeYLabel = new java.awt.GridBagConstraints();
			constraintsSizeYLabel.gridx = 4; constraintsSizeYLabel.gridy = 0;
			constraintsSizeYLabel.insets = new java.awt.Insets(0, 5, 0, 5);
			getPanel2().add(getSizeYLabel(), constraintsSizeYLabel);

			java.awt.GridBagConstraints constraintsSizeYTextField = new java.awt.GridBagConstraints();
			constraintsSizeYTextField.gridx = 5; constraintsSizeYTextField.gridy = 0;
			constraintsSizeYTextField.ipadx = 70;
			getPanel2().add(getSizeYTextField(), constraintsSizeYTextField);

			java.awt.GridBagConstraints constraintsSizeYUnitLabel = new java.awt.GridBagConstraints();
			constraintsSizeYUnitLabel.gridx = 6; constraintsSizeYUnitLabel.gridy = 0;
			constraintsSizeYUnitLabel.insets = new java.awt.Insets(0, 5, 0, 15);
			getPanel2().add(getSizeYUnitLabel(), constraintsSizeYUnitLabel);

			java.awt.GridBagConstraints constraintsSizeZLabel = new java.awt.GridBagConstraints();
			constraintsSizeZLabel.gridx = 7; constraintsSizeZLabel.gridy = 0;
			constraintsSizeZLabel.insets = new java.awt.Insets(0, 5, 0, 5);
			getPanel2().add(getSizeZLabel(), constraintsSizeZLabel);

			java.awt.GridBagConstraints constraintsSizeZTextField = new java.awt.GridBagConstraints();
			constraintsSizeZTextField.gridx = 8; constraintsSizeZTextField.gridy = 0;
			constraintsSizeZTextField.ipadx = 70;
			getPanel2().add(getSizeZTextField(), constraintsSizeZTextField);

			java.awt.GridBagConstraints constraintsSizeZUnitLabel = new java.awt.GridBagConstraints();
			constraintsSizeZUnitLabel.gridx = 9; constraintsSizeZUnitLabel.gridy = 0;
			constraintsSizeZUnitLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSizeZUnitLabel.weightx = 1.0;
			constraintsSizeZUnitLabel.insets = new java.awt.Insets(0, 5, 0, 15);
			getPanel2().add(getSizeZUnitLabel(), constraintsSizeZUnitLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPanel2;
}
/**
 * Return the SizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSizeLabel() {
	if (ivjSizeLabel == null) {
		try {
			ivjSizeLabel = new javax.swing.JLabel();
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
			ivjSizeXUnitLabel.setText("µm");
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
			ivjSizeYUnitLabel.setText("µm");
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
			ivjSizeZUnitLabel.setText("µm");
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
		setContentPane(getContentsPane());
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

	double worldExtentX = Double.valueOf(getSizeXTextField().getText()).doubleValue();
	double worldExtentY = Double.valueOf(getSizeYTextField().getText()).doubleValue();
	double worldExtentZ = Double.valueOf(getSizeZTextField().getText()).doubleValue();

	double worldOriginX = Double.valueOf(getOriginXTextField().getText()).doubleValue();
	double worldOriginY = Double.valueOf(getOriginYTextField().getText()).doubleValue();
	double worldOriginZ = Double.valueOf(getOriginZTextField().getText()).doubleValue();

	bUpdating = true;
	try {
		GeometrySpec geometrySpec = getGeometry().getGeometrySpec();
		geometrySpec.setExtent(new cbit.util.Extent(worldExtentX,worldExtentY,worldExtentZ));
		geometrySpec.setOrigin(new cbit.util.Origin(worldOriginX,worldOriginY,worldOriginZ));
	}finally{
		bUpdating = false;
	}

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
	
/*
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
*/
}
/**
 * Set the Geometry to a new value.
 * @param newValue cbit.vcell.geometry.Geometry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setGeometry(cbit.vcell.geometry.Geometry newValue) {
	if (ivjGeometry != newValue) {
		try {
			ivjGeometry = newValue;
			// user code begin {1}
			ivjGeometry.getGeometrySpec().removePropertyChangeListener(this);
			ivjGeometry.getGeometrySpec().addPropertyChangeListener(this);
			refreshSize();
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC7FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBD8DD89C553528287109863FC7ADDADF251AD8DAE9CAEB7AB5C67A243E3426B6D6CAD153E77423B53651220D1A5752BE7ABA7C85B6A1C21AC431E245A7AA9A8472DBA4C493A089B1A41030A1CBA0898BBB7B476CCFF607G8904F76E3DF36F4C4E5EE1A14F186FBB5F4CBD3F77671CFB4F1C19A5424637928AF373B68AC2EE21207C7BE72EA0945B856101CDCF18A92E617DF413207977B640FA616D0B
	85F0ED831AB3A65DD4A62C3AD08C6DBEE8A74CCAB73D86779B04B7869A2CF0E3E0278CE876B585DF1B5A0E231D5AA9426D17AE1440759500FE4021EB527B487E1A71F2859F2D60A9646689422CEA44736EF80502DB89BC4500EB8530D493FEBFDCC9872EEEECD0787AEECA94D6BD9448DB0774E8F4D2C8D3E4D7A957CA614F3937FD00EC9D99AFD1E4EA20ED83C0034F97EC076B615A54B96478E4F040A5571E9B94A52956A17ABC229C982BF58C3B9A9B8FA82DAE57F93155E515FC0E728F114E1B470B318EC148
	84FDF3B0EE33046C74413D8C9073A1453BBD883F826E054D6926724DF4EC3B5FEFD27AEE3970FDA1E32B272054E0EC751B4918AC1B5531C5F91E4CFB9D2F43578F23790E835A8200A5GAB814AEA52CD75GEF60751F577EA1DC778E48ADBE0F47675D6B77B7CB3640E11FCD14603E3191A88A6E18B8688B5884012C31F93C06531F2900488D4CD80E4CC962B63A5EC94766894B6E39B92F08B3A74B92AF6435551039682FD16704587E0A216D281AD0FF8C35FDC2505AAED877D8FEA147F6651F9F4D0FD374A4EA
	ECAF2BC1EBFDBB760145F00F6410BC41FF9363ABBFD27144B737E0BCEAEF87DA7CC754B7AC2F503E34975CA114ED79B2F79E629F08494B790874E1E32D3EAFCFE13F5BD3CBEC96313EBC0771968D1E74450F71286D87DA86G712F648E942B8DF569A6B30084908DB087A095A0230E7A589F5B6B2669E31D56005CBC6C12ECE2C0A0324907DF052BB8AA872C873DC1596A9D943B2D8117F5C092030242F36FE15437019039FF473EBF88B68E08DEB1E015DDBEAFE837C2500A01A061F97070FA449390B1556C7208
	E0406397285EC78EEF002BE48D4A477DB62BAC2A9C047E78E134A6672C5D20919440BD1917A7BB51FC2D02711754271BAA6A69BC9CBB02749D9283DED1EAECEC777906067DE6B492C12AC7737CDD3CF75443FDDBBD1D63DF9EC538E4E8AFE6FA2EBB72B70D1ED657A06A3EB5B0C6E3EC2E04E41681FFCEBD751B97AC54EFDA57FDCB28F974CBDC3F09839FAE5BC27C25FA0BBEFE1231DE54374EADF42F7F27E401AB32B313181E8E207E939B1D58C661672C9FC1BEDB3383DDE84D7CB02E980024ADF42E979D29A4
	3E5BEC8D0A54DBBB8260E822CD1DF9524F65B8CEDED3F4EE9268FA2C4478C520B38BE063D6BA2EF39E4B8C7A3AC923FB7A7B6D6C9959A8D266E43AF3CD66E8E187BA1F44F322D7EE957D225786B7C10576438E64FFCA7C5AFDCC66C10C77C33C37C00085997E618E9413D6D98E388606E531CB14E5175721529F4536024383FEF52B203616F520FE7BC6A05E997F2FBAD02C8C7A3CDE91AF9693D168CF63D895A551831DEE718DFBE502FF369365B4C3621832D7BC5F39DAC031AE8D23FD076489495635E88C8117
	87DCGEDDDC26CD69447BFE1F98E02A64021FC478657512DB49EC75E2171D8F57E9BC26A99FF9E6FCC091FBBBBEF69D692DBD9DB55F8A4FDFA6AA83F6F4B711E216003242F330B50BC04E7ADE43DB7881B4B0AB5F49293D1E6243FE4DB3A299220E19B0D097EE308CE779FA598DAFC12C4269875FB9B4EB7250D4AB8BA216D5CC666C1751B43DDE5E8AD453343A2EC6BE8A3BC44665B5C75B79D2DDB19FBC26F6BC2F3AFE977506D4866BBDE1ABFE6023DEC00726D442EE5BB31AD6CC0FC3FE5FC8BG0FE2B3EDBB
	59F7D70470DD6723FC6D50366DA07A34FCE4FE0F1DC5F302F3C637751CF534B6B882FE5B5878991DE60164075BA650BCA39F085EC173405FCC22F9CE01F686C0568E6AA3BFDFC773403821C4A1F35D2379C6B9EE41CEE54F5E29B96B3FBD12CF56712163482693E823BB699ABE5E25BD0B7686033EC197BE8615793D462C75A1023B5E3C862EF6D7A0A89F04A09F25EBF1139975971D138ADA499B5E407B9E722F98303BE8A74D2F3FD9703F02F1FE2D6656B47F9A5CC667A1767CDC217FCB37662DB518079CE57C
	957FD467A169239D7924BF6DDE1AD319019E83D0550070BF9F5A2851318870D9GE5C0A331F10FF7AB5A1330F35712CB2798307B829E51D60DD63B56BAA257627D2FF6AFA66FC3779FD05FF8E69D3AF68CD5FF50AD50B15D31E10FE2EF493CBB04264953F93C9CFFD629B3CFEAA0E309EBD047A4E7BC19B78B000C2B4CC767B993F84ABF26737C895E9F3C50CF2BF4CC8C8ECBB2415F759ADAC71BAB687785C55CA95227EF7DC35B27E26B555CBE45BC6B486B7C1874E574E30D3FC1FFD632BEFD1775A995F832BF
	26F3DDA471663A3E111E098D93A8C7EFED6467939685AFA957A838125C7524006C45B5125C9559A5F89B464F5160C97F32A6E97F66832DFC974D37DEBC01766EF1E84783A483AC86D881D05CC4E36775635B0488796CC1AF2419DE2B5495F2D0513C72B8328BE70B527E45F134AE78EC9168397D36874DC3D5537473AAA2D707651AE6A0C7465D5D0356C58239D9G89G8BG1632B1DB3AEBA20D1966654AE0C9FF0EF9754FDC593A3EED5C9D5AA6F27DE1F2353AF603CE0E78F6E91741BE5CBCAC4B78ECA2E7F4
	0ACBFBC6F702AE99A09EA089006C5545B85FC57DD9341B6E49937B35FBF2CA690471DE24742F6293B5C696BD7B87E5CF4EBE094E15BE200D824482A4812481E48114FDCA67FDEE776EC8730EB79B332F85F0EDF09E3668F39DB6272F7AA2BDFB735722AA22DC7D2721F2E40C3EBEB446B62059G46816281D2G16826430B17E21FB57B447C8F657709112FEAE3844717DB822DC451ED0B9629F1FB07F588EF4728C57014FF7895A318CF797464D01F68A433DBB09F01950DE45F0DE0CAB5E8B4F0CFB69FE58AF20
	BC341A355D380DFCD52935CD4EA43F1F8A108D7E1E32D6783CEC64F3F044877EFC427899A25742FF06A832701FA156D95693BC6850D694A8FD13E56AE70CB7AD3CE70CB7F17F9F4BC367C6GF2ED17C744E6493EA30E3ECDFC7858301738B1329C546F0D4C46699E63710ABD7C718E757047FB36E76A710E75980D170C69A72F16E279E0D08C62C0EB354AD60576DB99BDF706BDBF9D31FAC401BEBBDC67B5A9BC7BE4DF8B4A15481C3C7C815AE3B9FBF1279532CE328FFFC5506F43A8375E833EE85ECB7761D52E
	E2C5273417664CFF9F4267BA34D3GB2D8FC2C1334311C257834502C54421A43FD1C6CD5FFB8054E3FEA203782748148GF14DF40FAABB99714C5FB7BA45191F57EB3C5E26DE7EFA177622FCBF689B8E8C0A7AB5FD35371C51547D7020ACFA5052901E37303CEC8DB844B01FF88F4B931A11FCDD2F115F10F5BF6C532C7B611D2A3F75990FB52A0FBF56EB7A0C47FAC3DF64315E5CE7BC56FF6B0BBC563B7A269E6B6FC3463AC320FBE3CB083F4DD77CEB31F2DD4671B7224FBE01624AA65A2D101637C81008F778
	15B2A57243966463D7CCD03FDF59CCF37A6F0C20FC439E30FA20FBDDFEF19012E5447B03899AC79248C681A4G2482E43650671826C924436333D9A5E623EB123E2FE8EB25FCD7E3BE59A5637D4A520A5E79BCEF93D85F5FFE87459E4C240C969685B309536BF2E9FFEFEB25F15A1D0FF8B3201D85D0300F46691FDC7AB337E21FBAD7757B42670D6473B76238EF5D272F235F027116FD7ABAFA6590FD66130016360F1E35D5786C5E3E9F761B7D947710971F5D509EE738BA9FBD3BE76D27FB417F1CB2766F4253
	FC7FDEFB1A3FF72FBFBD755E7D7AE9A33FA47DBB4946311475B9070DA30761CE31F1949F206B9C0F1F35B4DBF36D891757669B0115C515F6B9D85BFA302B3323EB5F190EA3E7DA5A6106644089A35AF7EF8DEB2E64562E90D88EA84FEB4AB5E7532D3944D71E463E0DE466GED59810473DD54BE9B22BE9E4AA5F9BF21FFAD040E6665DF85AD7D97A1F454575C82AD3DF0D2CBC7FD5E214813F17D74DD1A5B677CB4D9103E97ED78CE312A0D0C27CD3946AE3FA5B77E2F6773493C3AA7F10E969896095EC7FA3573
	D5F62B1417EB20B7CA519753262F3D5F182B0F1754B6B5C6B25A0C6265A19CFFAB5A746FFA9E43F861207EDDCFDFBEFD57D38E34710354FF4A5CB8DF0576AA061BBDC4FD2A7CB39A57F198670476B8435D0EF12896076AE784775E10C5611BEE6EB8BE83DEE263D3EC0317F39A41CC5EB110355EA5213A7000AFC00276901A17E3925D0BE3214F09G99GABBEA375C8A277ABAF769158A8487E7A349DAD07934B6F7735C6302B05B610C130C97129BE387FC039A77D390A75A77990694FE238AEBBC4560358FF43
	295A172734DF98417E2AC3442790105CEA31932D3F508E4FB56DF48F5DAAEE06EB37552DE4D27BECAE5997402994A0BB83BEB88D65B1024515DFE5CD77DF3C347747BFAC2231BDFF61B7058557349ABE37B73593BF6BD32E55CF5F127B8A9B4BD6B71A4B9820A582AC81D88E108DD0F6180EEDCCD4EAA5287BFBE559BA6844D5D476B06092C7D4B4BA44096E63C32827C4477D623BC673839ABE03BD3BE3408D090C590AEA4BA49E676273378A7A51F0181DF379B4C67B8E5358CB43B19E856DB800C406DF0671A9
	504EBCCC6AF24ABB7B7C291E7F103C91ADEA8A9A02B8E67B4214B60AA244C32418A6B1773E9B452AE49D738D4BB4B6BEF15358A8B8827E89508AE0B9C246CF62612EA19A8F7EA3616321798635938DBC71C7C8BC20F65291B59EC8BF229CE8BC24A035D541B56ED3F1DB143D783CC4EB148BC0474AA3B487FC706F7F903475F37CC1CAB364A3D2B35E071AC52B1756A89E10E8FED75DC1F33257F09D3C9F5A23G319D3436B57A9E0D11243B6E90B2CC7C778D1966093C448EB28FCBBA54F3AA4E73E45EDBF8AC02
	0063DD79C77617AD9EF4CF6C7C2A045A29387BCE217571563CF7B8F69094F7AA5F37F42AF6161E2DD46A79F7337909857A62CE7A3C78B29B33F0140E79A09EF38334BB811CC7693E1769C5EC4A3BC7A73241F2AD326DA8348CA74A9DDCDE176C32CA2E73B8D16F82C62FC37996420768D12077B600D4008C0095C7D97D54055EF920AF9D427703FDB607C8F3AEEAEF175BB847ECF6C77EA662109B557887FC60CF9E241F620F3A111F49BEBFC692DC379B2DCB4065F04A04153EE343E73EA45AB11A78E9CAEEF88C
	7DA2174550B192C395F02DBFC6EA2AED47904F2EFC6DF3BE7FAC0D33A25D38B8301546536036C20AE3FC97F621B110AA4296251EDBDA402B6746026DB9G044765607124GBD8D0078145DCD3F89DB81384AAE9AF3DB7AAB84CDAD833D71E9ECBC90F0591A2D0EF6BC9B44776639E9DEEB69223E378C6B0C05F6A2C0EA9775534707687352D24071E247B81EDA4E22FB32F7C6C9114E44B2B32F6EF9E3C439FF685AC46EC12EDC0D59F82FA7B96775B8867718557D106063B03EDD03A7EB7126172E0585E8A9E67A
	0E663DGDA474A636926A6GB30084908310F81C465BAF076A0588759A334F5F0EB6CD4DE39751FFFD906D77A920EBA9C08E40466EF4D3B5409EGB30093E09CA08EA089A095A083E085C0B6C04989701D9334AF67BCA89FB34AF594A7429BC0406AC2DB00B24F5F3944F53D7F9257B51BAB377DC424F57D81DE2736937AF57DB1461BCF685735032DEBBF509631F9DA60B35E736E77C55E739663E7DC9176AA7318DF296BBC6CC3FB0A4FEE8F0AECEFCD7715B15C41E027AF68C23BB9695BFF67523ED5DB52CD7D
	969A2BADF9B42E63ADB42E4F62DAF09A34D7G88BD749CF58FE94FB7F4E699BDC716771039ED6AD14FB70C477A8F94507DE13C075A1CF78E591C8F6DA5G4BFBE87F3E66203CB90C57E5C73C5BCF261B5A81ECA7E92EA539AF6D5B4D7317A8F74F0C65087FF4625A4D78C93DDF75E07C2C13FA3F7AB55B3B6300164D463BECC8EB3F2FB75D646F1DBAAFA5356F548DE85DDCDE496595DB481704824D351F42B9C6B468096F25F6EE423984393FD5F37F0E7773FAFFE2BC47CD175506D28F8E9847674EG3F6E5594
	60573D1A83D33F0379ACE07C1D5751C078F7DE37870D7BF6D7B0725E716DA0BFDF7ADEB0BCDFDA986467CB29C1ED3EC4F0BF426F079C3047DB985FC38C5743F00FE01D88F742E5131D8C7FA86E9742370948A045BF017BB502F1968517294175A838E702E87F03A1BBFD81AA7BDC90559E6CAEC9A26D97B00F4B8B5BA11B0F17F19FDDFE2B265F598C5743622054C363A0094541FFE8FCFF09667E789790875EAF40C6EE5E256D032517A857B6E3B932CFBE034F2754DE7D7E798246AF69556F1FA3F9F47FDC8A34
	26BE1AEF8506D13E15G6D9400CC00EC0012D369262AD3746C3EDEC66F470CF21CE3BE1F0C52F5744EGD29CB12049F648985B8B74E3ECB8256E516D270C6A1B5DA378D97694994BA606F7E1FC1C86CF46B89F1F1F283D98E872E96A33F7B97476AB4F28B6371FB1323F5E01F31FB3FA7BD598BFFEC6EF5FE7277613015657CF6DF7635A8F39BF2339DF617E827CF968F25AA07B444B78D92758CA66036032B12E522AFE1B35C178AF896DB103BE89EDD74ABB2A923C47B481DF9BG41B5E01C8C6DA8AB1D435F65
	297336CAF3BF11F7796770664BEA03445805E163B36E4A6148E75C75437CB36E266170B36E66E17E99F75B30760CA36BB8087354A4D807D4AB2DBB7E3A10569D173375699B29066B768148C7811CG51G37814C87C8834882A89804E727C1AA6343B2CD506E839887388DA085E099C08EC0058D76AA00BE9B15D9F8AEE10A39C4F675386DBA9C6D0396B71FA3572E74BF1AC3B302ACE52C242F4FCC59D7DE5DD3DF6FC47501CDBA5CBA0E9C0AABD473B64D406FD00E06FC0E2C698F70B346B84CF32C0D2EF3130B
	2E73FCB67F2FB9501A950830B6G8DGE6G89A09AA081A085A083E025C8E5BE44B2A576F4D38DC0BB0084908310843084A08B204481F4871599366B670E5AC07AD15B2C585172D408D32F55CA8DBDA1822F969AB43C851AFB525704303E2ED556E93D52B6A9EB29DF3329ECEA573DF4866B2A752BFC1D3DFC417A9EBA2314DAA7CEC7893EE2F41D0A2769A82D6F224F3E15BD7C4F9ED437720AA3156C6368963421D7EF59D9DF03E06B0E9D9FA8F2DF9ED365EA3C5311A33C678B426B10B7319AE03B0354A1EDF0
	9D85C06FB4C8AD7FCF0534169F6B605772F3853516BF87F896B854F7DBCB9C7ADAFE43C93427225FD3D85A2D8328C8C970FB563DC27147E142C2E83F5946E8E65831773BC449C6E9653BCC8ACD91D170BF5ACA6D7404682ABDC6F57504697AFB1051427AE05AC2751D8C41FF6227B2A74374D55B99CD230FFC577D0D916D3B75254A1C67B868F75F27C6F974B2A7255F6752522B1C045E4E683BBAF8727D0CBE2F09C717D579BA1E7ED90C7E5CEA1E7CED0CFE3D0DC7CFE6749FB9F87A97B17AD5A2CFFE29133E8F
	4EF0047B736FD83DBA4BC97C394015EEAAF71177F3441F3BDC541F6BDDFCFF263F67C257D6603138D4FF16DCFAFF961AD16CF740416F72920FD63EFF5ADA2662596A935A35139A9A739AC2FB563416510843A86F914E2AF6FAB43A2E9CD1F57568F4BD3FC3C35375A1F9C855F7D203BF7F21AAF3D2276FC819064674117532B9746B0566FC54C5573356452347B07ABDE17E0068890CBE1BABBF1F51CB1CBC7AE2C60F8E0B97C4DF4668DF6576FFA5238F59F97482B725E759F87A4B995D41152FF15377A8779650
	E749BD009BF553BAE5EEA12D23A63BE91D322E90FF2382EDE1887806A86F072BE9CD33FEE8266FC7961E97185F3FECB7788E952FB73115B9C2EDB9197D38D10ACB98226F015E2D2E606BE23EC5E4DFF251F710CB193EC08755D7A6D15CB3CD945746F001BAAA9B45F0EF97D13E858C77000DFD9F48F0B98E763E57C3F18BC54A576F216B3341C957A79EF0E90C3757C9572758CB5767BA4C3B875A96G3F1772C6336FF2E7F9E73ABECFDEC077C44FD7D86DBA156996196EEC06FB0E0D2D5EC7F1BFE6FCCE06EBF1
	32F5E3387B5475E038C7195DB2BF455DE1E76B41F06F08ECBD986E2B0CEF011F6623770755B02613072D979E3EC439550517D6973A93475BCA3F3EAE140471B9FEFDDDE8C7A17515623310171F25632DDB2D351F867865E7274E1715E71E91DE3F3395591200516F90EE996549D5AA7C5B8D657E98762D29965A8C65C6BAF8F2FD8A3F5FD06E39A61EDC34429FEFEC2FEE2AFEA6994A3DD74413CBD578B38C65EE882BF9A1D8217067984A65BA276AE7D950C86EFE11A7D72570B7984A3DBE25BDE3BFFB7F92657A
	E7ACC76267AFF81FEC8F6A63EA358BFF1B9C544755EC36875B0036B8C8636A3A90FF2D1153CDFB646944D582F7BD4D0A2C1323C379CD91B7BE46957ED8C3395FF36DD118E3A877D32E3D858A7F92C339B66E3AE4AA7C2B8C65D2270C7F62E1A3397F640E2FC2612FB7147B3A1DA7572A70DB8C652A3971C1C1B2143B13EBAFCA610FB3147B867EDBCF647E3B1A7BDD05173FDEEA3E2CB6C8EDEA7F6A701A7321555A1A33723DBC4E7D92E11E968C533A5603E3342E35086112F1BE38719C644067A8EE93632BE738
	D75C949745F0B7625CAF8D5A198C77F287654BE2385C5D3856BD824F3EA394E75FCD79AC8CF7E39D45A5B25CEE1CA3E6C33BF814626EA822FC9B99EE8E4EFBEC5016994E64E4FDE638F206CBE33847F0AED4B986639D2338DDF64A57B4C67730FC176ACFA51A7BAFB9AF3FEF4D3B2CB6481893DC613EB557956EDB5F47F39D8373F29B1B2F8AB62F098CF785164D06F671F90A7B8D2B2DEEE43855CC5646F03F46FC73213D08618EB09FCCE338B3D83672822C5D850ADB4474B5B15C634CEE8C433D0847318C5AD9
	8C574DE433996E5F31AF340D032F0ED35CBB8E56E7067BD4E4F5E306DB08E58BA6E03C9394C77E1691622B1C2035128D23215F4E37CEA07FA27510274658373A937C5FDE51DA877ED697F862A754FAC852043E9E72204E166322EA2B0659DA4031D524333584F816E8ECE5EAEC91FD6F3A293EAC0E3E42887A040B69269BC27ABE37A3346F7FB22976FDCDA735D5F251388ECAEDD5DED4ED613FC9F6D1352B54ADF7075A7A16205A1A58CDED6D6158B2C598177922FE1E6656D1FD7D9CFDEBA36813436649DC945A
	7734DC356F7796D1DBD19CDB6BA2580A3B2877277B9C2136DE4ED3EDD5BA292D840E2D52883666074DD39D53174A5137BE023E0C30F9FADE8C6D7B2B85EA5F0F59292DCC0E2D8DBADB2BF47ED4BC291F271F69666963C25556B6B62E0D1311E33C9AF81AA655F1F506590AF7055A6ADB2D5A7A8B2B2F76F1EC69E35C8FBCFE0D2D68C97D1A3C497A9E4B51270FF13D3EB9137AB579132E6FF79729FDBF4D7613E40EADFD0CAF5258425FA4074D53B9E7282D1F952B361EE063DA4E31250FF17D383243666985B66F
	85D111E3DC2F2FBCCABFCF453A3E3FE8D27B6EE1FD2F62585247385ED6C314FE1E9E35075AFA26D33555682036DAB93674B12E37E509524FD337C87559B87A74B12E57B79AB6CFBF5775FDDD175A7794095A0A66585247F8FCD428BFA5E97ACEEAE82567D98DADEA267543FA7C37EF091ED3E32C8EEA200748F49DD7E53A5DD4267A8A7ADB107B3AD57A2ECED6B33D0227F36527C46629932A4C3C5DD4A61E694C6DD1690F55D1FA9A23D71DD4694F319AEF711514FE20D7259FE5F54DBD0C6E6ED3697D0CBE4A68
	8F1FD269D7311AE57215D30DE965E9D54646F42E604ACC35C69B7AD5BD35EC3EAB2F1AEA0DF6DAD519A34C3665AABA1E1E8115BE1B690C656A2463999FD4E5CAD83DF8B153F93BCD253F45F48A51147E0028523F4A7A5440684F58D57A902BA97B993D5829522FE53578C4C65F6CD2694FCB141E99BD55180E38D10BBC6FECBC2FFD7EAF3FBA5DD4F375F46AC583E3DA39BDA0E31E16DCEF48775CCE10991F165C1ECE2DDCAC484C19165CBD3B35F28BC0E64934641E882927E6024C2AE9497DBE241EDAFCCD3A29
	621A6948F50754276AC126F5DAF2E2081C85E424E9494D8A297BC401CC5C3464DCA17692C1A6E5DAF25BC256BD8DE416CFCB2EAB44DEB64814DCBB9D393890FB15A033FDDAF26BC36AFDEDA053B7AD392A90FBFE10091E165C1DA1630B871924E94979C36A0B29A013B1AD391810BA618A104919165CCBA13F6FA91B15EE2A1AB5359CF9865FFCBE3C56D1FFBE3C56B1B6964EF7F1AC1CAF608E679BF107731D688C676B6B8C67FBE8F7B85FA33B4379DE2A8B677BE3DDB85F6A62F03E354561FC9EE7B8DF40994E
	B74E613B0243F73BBD1CEF0EBD1CAF0A236FEA0E3E4FB8735C4119E7B7C71F0F236FEE0E3EE40E3EEACEED6CA3CEEDEC97C75F9E0E3E051C7E5D4F695F350EF03E98C7B85F1DE2B8DF22984EE77204733D625172117FC361458B77C2421B743CD0A88B6B689FAF2DC45FC949C2DE47A1D9D85D023E471502812114FD59F6907F5CA2735C36467C276F4C4F938A8E8A2B49273987058274FD6DC12190FFFEFBD04817FD7ED174E74951BF7AD3F3A1F8D638C8FE644C4596E82094AC3F927ACDDD6E5C572EAA1B3DED
	B8CFA85EAF2C36DBA53F538A96719F21EC97568EE05368BBFDC9964A5A11EC9D4856E159DD6DC25EE0D0965ED0717803EDC26D72DB87C5E0B1592C01A12C81F52938D7B059830E81ECG2625F7FFAF1845ADC4DE53AE2C111D7877817068A18B2F292ABB512F849442AEB28DFFCDC8782B9F7F4343AD6260684430FF8C3BC603DCA38B3996D948FF60FB9ED9A8A0AB165BC39139A7599DE3DACDBE976231C5D8900AAD8D430677D1D07842F979FA799A78567E7F9ACABF1731CD47093F103189746F92FCB0297DBB
	BD59D78F9A7EDD232498F2EE2646283F5BA43F15A947FFCFAB877065B7505F46B8AFFEA4987FAD0FAE0FD512E42BB43457EBC3FF5723F9CC96B5FF0BF092698FD29E05C8E8D513285FEE0D4C7F81D0CB878813CD9BB81C9EGGB0E6GGD0CB818294G94G88G88GC7FBB0B613CD9BB81C9EGGB0E6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG569EGGGG
**end of data**/
}
}
