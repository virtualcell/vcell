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
		geometrySpec.setExtent(new org.vcell.util.Extent(worldExtentX,worldExtentY,worldExtentZ));
		geometrySpec.setOrigin(new org.vcell.util.Origin(worldOriginX,worldOriginY,worldOriginZ));
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
private void setGeometry(cbit.vcell.geometry.Geometry newValue) {
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
