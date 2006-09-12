package cbit.vcell.geometry.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import java.awt.event.*;
import java.beans.*;
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
		this.Ok();
		connEtoM6();
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
	D0CB838494G88G88G500171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBD8DD8D5D536D8EAD4CCD2E2E1B9CACA0E25562D3048F8739823E20A4A3E180619673CE75FD0C3BDACAAE71E5373FB9DFED4948DAA1A18494A99AD319872879D7C29D0B1D1D191905118647E9C6062655E4B3D87397C18EF6D3D4F5A675C734385E79C3FEFFD676C7533575AFB2F3577BA6B1C0BC27147B14B32F30A85A1FB19A07F7BDB36A0643789026FFFC69DC1DC243B29C0D07D7BB3405B4218C0
	9EDCE3001678CCD3C11110FABE9F5AF150DE72D4D3417BF07F2EF03125528AB7A6FA0A00767C78CD9B06567378B728E785EDC7F62F066BDBG7100A357BC8F51BF233BC446AF10718859B902301072BC543DD64655C03B95E0920045437813613A12EA9E332BD266EB39FE063078B1DF4E9A520F2ACF04D2AF132D152F6B04B73297FB092ECCEAA711998D6D84GB278DC41F1F4B3DC77541DEB7F4AEEF3CA65676D224BD55EAEFA3AC44957D75E5E635C356B205CEAF0760B55CE2B4B536E6DA6FDD67872E99F02
	D0896D2594F7FB9B51938777A9G699C1F51CE70D53E2602E6G2F8F47766546BD326D4D83778B696D8F2FACB499DB380F0D4962D346D67719AD770FF49D8AEA48FCE7822D485FD4308120862083A09C60833A7E6F9C7C8C2EFBED52FECFD717473D576B5D67F2780EFA9C228B6EF76D820A0C2B976D9E1FC390589A3F56DDE6E04FD04064845FC865581C4C58066BBD2B7ECEE1413DB7662CB0181385B386F2E27CECAE627D4A1CB05DBBCDF52779037DB1D877A9C12DDBF8F16B0AE5863A57BE3BF5C5265C0FA0
	A93AABA432569F5235DE8777C40E48B3FCA94587D4F8661BF7D11FA16D8820E5C9689B4DEF21AD35F9D30522ADB7994EC3743C081C469EE6032BC7EBCBCB8769BB3A0769DC41ED6920F80B8A4FECF9195AC85A49C0ABBB5FD4407C2B78B00955C8E84F82C882D8G309420249778FA51473EBD7845B0FD2C4E6A137675B8DD8E51A7B059621ABF40D58CC8BE6BA137DF323A6DE2135567345ADC22DF10F9BE2CA1365902647EC2FD5F8EBA8E0AEE51E7151C9EB774EE052095FDFE4673F94D5B0447A7D2EA2333CB
	8485DDDE817B5DD673AE19BF2BDFBA69F5D8A5D161E0745DB5E4CD4EDB1B20C768G6E593C6CBDC2662B96466FG1078BC5CC77BBBA27A5C22EB572EDA0F67DC0F37110CC4C86AA573DCC7770EF0380F6945B97E3216607283CD856B82584F221AF7D47DD4BB6D44FC2B2F8FE3ECFDBB1129G7E46G7A4D4B4D68B755EBE68BE5BF1BE068B71160439D8166AFE181ED7C1453FE09EDB38238574FE9EB06EBE340B8FE7E912860BA1E24BA167D137BA7104B57EC66D132E64920B39DA02F8F673A7450BA663B7B2C
	FE913D75388F9CCDF4A8B34F6C345178F9DF6E73BD815743CD71EB214FEAGD79F0EEBF1FB7388ECFDCF557770775BB59DA35131C21E13614EB51B230F8E1373C9BCAF3A25EA51AB3A9DF06317E91F9CA67EA747EF1B074BFCCE71DE08777D90E0FE0E5FF2184424D512FCCEDB0FA4B6081264F437AB741DD4173F4766D5368A54D5F518586D6905F8677C878E13D831FB5CEE11AE9697116987E92C0AAE318B0C5E6F69F1CB8CFF748849E94E09FD72DEF16248F201443A2B076CBBACCF30D8C9B26475B93B4085
	4856A5E47411B8EE77152866A88AF0A45FC9006B7CBE0C475E75980F9B7AEF97924FFAF30C4E14685B4665EC6EE731D55D2F44A333E9DF2D316DB5F44F1071FEE66B3895E49E74D98BDBEFD80F22FC951D45C47AB1523F837486GA27BB1A612EA899D779FB99876FBDCAEB645C04B6AA77360EBAF1647B1835A097DEC9E943F397BE891D9CB313BC704ED1DEC04C778FC479FFDC7C3EB12671E51930F1239F7297750812273568E4C9FAB21DD87609DE0FAAD83CCF773A061931D48D784ED921B2503EC5F2D9AD4
	732D68C43ED8E8A78C327E54FCECFE6B3B491C501C3153FA5E9AA87777025F6E5AF59827D9E0796153F40FA7BE30E090734051BDE41E0BAF401989D0FD81FD6469B51887C61D1BA1ACF8F14B8A339C3765023CE7DFD0FC24A3EB6FF2360E65F5C4E7A25066DF40B55CFDCCFD966D757BBDF6A7BD866579FD69185A07986E15E32B605A66747925C3906481DC0B5F9EA37612F3D2C64BF943C41AB7907FCA873DEB3F477C7A0E3C3F8866793512DBE37EE55F06739059FF1B50BA61661C8213F9E87C1E0DDF7ADE19
	07326F17ACE376BC5601B955AC2027838CDEA4784F5A0AD5FD2C85DCB5C087G0B0DAD4E2DE4CF224EDD4EAEF5222F4D636B929D25E4354B2D3DD2B95D7F4A77D27281F23F89FD6379B564FA7CDC6926A68147B4755DAAD9DF4A1DD305BD973F4BB14A71438BBBF212AE3231E4DED446943841169B814046F51D936739B28C7CB38C67792B93846F86BB2D2EFA515F631298FE7A7BE49D9DCE3F57639729D14C26599F29ED4A370EB134A96245761C99E14C1679E10ACD8B09BD5C264D1DE8D349A80843D1B8576D
	06F39DBE8A4F44E7E90EBEE914F1BEE11171C972B58D2EACF7DDC2E3A8F3944BDD09DE064FB9CF70CBD5F8E65FB5BDE85F6AABE04E2E40FC6BE4BD593BD3211D8510FFA564DDGD5G2DD7E26CDCB83ECD88114F9EF2C31A69363A9A028EAA4CAB6B08DEB8DB64765F6A483A5033C540F3FBCABB190741AB071FD7B13959D4AEEA84F2EC5CF39B483AA4015CC200A5GC5231B8AB60F46B1A75415059AB3666572E019BD73BB344FDCF52303EDF3E95ACC6EBFF5F2651AF624C60E79F6E10349BE3C2FC712685944
	4E68C98E7599BD837ACA814882489B837EC077E5EBDB3EEC4F5AB138A7DF78C63DA7A794DEB05F0B4648FB5198A5C6E25C0DAB58FC371CA467CA9C506683E4G640D05B388209A20E3AC4E7B4A93DF061AF73A59B4FA76832E864E437D5ADC074FE914B354333771DACC8DA997BEB6D80E0D7119A6B2469820A5G2482E48194DF85FE8550F8950E714C095D439CA35BDD75A3E4F66E683C3471D50714132E8A16E37EB1197BC7845059B35CA2FA3EA7C1BB03632A7D8437945A456108FBC8A238CAE857F25C6A9E
	02EB0536940E7B617F7510BCB40C37734F13B671D5395656B3127CFEA8A0BA0C77145B7FE9BAF28DF04C873E2BB7FF06906B0D1FA14E559BBFC3F4573F4D7050073A2200743E7A211FB1C6559BBDE3FCC86DFFB6071C9BBE48351DDDA2D54B769DB170E7664375BDEE6646C433DF3BB7F29D739A4C477B53866371BE55E0BC5E1FB58CBD5EDFB5180D170D6927FFA82472FE3F682701D6ED152CB26DB792F96E54BDBF9D33F60982BEBB2CF02E14F98EC81E7DA4D7E2F3727AA632479A6C45F5D648BA59BE3C733C
	F69FA639F5B478622CF05C07D39D79F21FC96118B37BC5FA2EC33B78EA481B2F46F8E8EBD347F27555F2AEAFDFDDD76BFD1C6DD5E71A4979978674C9GF1GA9G19D7639EA5B506BC738F8406B8735BCE1A2FF757C96375761FA4793E5F5363330B5AB56DBFD942E94AFEF8C8923B4852B01EAB293CE475350BBA1F38064AB31A197C7813E6FE43567DF1753A9F5D2178DB1379D8531B0C477AD21379D8DFED8ABD56B71A4C471AFDAA74D8D71D9AFA2CDD1D6A31EE97F0EF1C9664EF2B2F114FFA79DAF10D5E5F
	D8FFF9FE92D78E314D8AE979FE97A462473DF219127861B54447BF76235F57DC03B97D5D3DA45FE873D93B403C862FE8971A2FA53C5F7AB10E1200B78DE089C08950AA2F456718DF527C3F4B63303A380ED7A5FCDF914379567790BE49A9517D4AF2ADF967F3622C40ED7F73FA92FBB01392D9D8924CACCEB776223D0B2F45B8DD9CA03CE5911057G34C4E01C7E4221BDF32588E52E42A374734672795F50381F94212D233FC6711688ED9D3DCF44E73EA420157EG4F5A8B746C0E00F68C47CDF152339B5A299C
	175709E777429F60DEE0EBB677EFE73331FFFB1A0D776E1E6621776E8B4DE6FE496C1B4B473179BA3479BF78B89AB9EE8F9FC76BF5384EBB7CE48DD4DBF379A9275B61690515A515F649DFDEFD28216EF8430133470F1D5DDF8BB7AC870E69D53FFB2BDCF505E16D0A0065BA79F9CD3EC63CB9B11B794AA86A5BC4E6A950AA46915C9AC97DECC8ECBC124D72FEC67FBA08CE66E53C2026F78751092D59F9EA7A34A0BA31F93BAC4F467578A7185BAFFD5CA238660CB6FD27B8B80E0DA7C63E2EFBF2E2F6D2F45DF2
	B62F79B4E713FCBDA26B7729EF5573D5F433ABA75B245FB4393F74F14AB3830B1732DEED3C14C4AAB1D296E996AF9DFD749DE32476DD0FC4714D115AF7BDEF8470DD0F97E8291168BFD9748C2E3C9E72556B9167E9C31F72DE0FF13DC82435A6E827F25C920AA335B8D2BFE3380BE2334CB75C5CB1F5843CCCC7B455E114F31A41C85EB130350EEAA3F5E11B47C703367986B2AFB35BF0AF4EG1B85401581D45D406A11CC6EBEA7759158A8587EFA103C059792297C871D55A1742AA186E4883CA77BD49C5CEF12
	6F19BD3F6676640FE7762C03EB45F8369ECC7FC51B223FE1C87D4BC2682F9D4FFC0AG4B2D4A6CE47D1B81AF0D47BD7446331F4335495AA9E7D2879CCE4963232994A06BFC9EB88D25BE062515DFF9CD537D17767E78A7ABB03663665EA1440F2DB6FDEE0F9A4F7CACCE3E4627CC4CFE0B0F65C6DAB7C8A7739855D43081E08FC09DC0C7940E6DD7E765DA89B1FF2FA4D96D9D340A4A9F861CE22F02A607B86B7B81F25C4A7B7E7A56CF4C6687FAB88CFB76F1DBA7A4B20DD6D2DBE671381F1E3F03E0C7E4944668
	3CG46E8DC94461E1B46FE9A34B3810489086F2778E2E8D7CEE0F5B9791DFD5FD04FFFC45E0CC6F48C35B6195C26BF8E29E3C508F888955372F30AC8E255E56D7374C8989B9F09989BADB08E89E09200C59EBF0B0731ED988F4986E345FC8375ACG1E2C89AC9EC8BB6FC6A59E189DE16DE4BC2CA035D5466DBA20603648FB71B3EDD823AC02BEEAEE449C7051BFFDA4286B67740314FD100F387651FDE81FE8F5E30D6203F64C6F42EE441CAC1356416321BD9FA063C62CED85BE459819B5FD2AB07FF963778D8B
	9BAF6488B731F958F013F2CEB9BE3365FECC47827B0C207CE37B4B846226F2ACBE379A752C1DB9CD287EF9F54EFA83BD84DA657EADAABD2D760A15ECFE8A787B058C202F0B46674586BE6666E89CF3A92D11C7C2FB86C0E2B46EFB55CEE2117C6E51CEF470DC0BEDBBB22D52CEF287275BA9B92DAEE7BFCD549B00515DAEFF8B51C968E9506FE2001209306783D4CD6475D3BBF967C13EF4506F87879C6DA266DC28EF0A43BC471C6E887DCD44DD8ED26337F9401F3AC87F081F63A0FEA6F93C9449F0F39DE4DDFC
	4E768E093162BBB6FA663B44B60AE6FEFA26D79FC35D3DBC06A6329812609ABE1155D4E3A6921EDF84544F79A6E7291574CD0B83DBB11E6C5B16A10E738D6CA6E3E0D504ADF2BDF7F61FD1BDB783F4AF85E0BC732DC6BC45B743F381G7329EFC47CA62C8AF0011BB16636342E95D4358C7246E7572E03BE27E31F353D164E86733D6BC44CEBADB7236FA550BEB320AD5C823EF18B7A697DED783C3499F0C631E39ECFBFBAC76E65E72936D0E7E247ADC6F54F5F04147BC853E6F26B5A0D64C65FE23E57331C3315
	46E074AD4AFE4870CE0A0FD56159DA3C5601EBE181DA71A4FCC7F30D0B2CE3G5AD1G33G12G5281049FE23CFD5936D988D12FE974F8EB492629FA6CE27D3F52C5767BE268EBB3C0A3008BA08CA09AE096C0A2C0AAC0A6C05EE4D8D300B200AA00BAG87C0E0B25A724BF6120F1965BA3293518D40E7F512ADC01E67378D67B9743AFE9CD24EF8DD1B8D65A2A607DA57C9F47DE2A6EB5775B60A1FB5D93B2E65CEDC57F8202D1D0273343653FC4FDB5F99FA4FAB234F38A26CD50DFDDE392E3325136CA91E36B6
	3F4877569D1DC59CF748DF6771BB49EEAE3F336EC55B42402E78A9982B4F75E2DCE7CD4138AE244FCA25B1306EG4DB1F80EF61ED31FEF644CB2FB0E744630390D0AD14EB70A277DBF5F077BC3EA8C6A2CF4931D2BEF055C93E04F2DE8DF3E8DF99BEFC55E93D6429B816DD80004DBB1572A95AF6D5B4DB317A8B7FE44F24CFFB6537CA775D62DDF6D24780537EA7DEA9E5F3BB301D6B795471B90344F4581BEF96A50F9A92BFDA73ECB564D69F6B95D62FE76A5210039765FE80E31G7A4962FA5AE8AE416EBD2A
	7BA5A37A86EB24A06F954E4B29C32E873B4C6373E617F15DAB46E5DC771A6E9A7A9D4C1DAE736F3C6638745FF9653A4DEDDB658E3DF73C6BB64E177E685667CB9F390D73258DEEF53E44F01B5C4456F658631BB9DFB9471D6638ED34CF02BB65F4C89D9C3F135AC570B5A2D108782F29DD3D9457AC632AD53853B26E101B6CFFB0648E0F8FE50F39C96D214D69F231F6BD65F13AE1BB6473F10A5A6874DAD5F637F05CE99E879E9E87F953B08E262AFCFF266A3E62327AA853F174DF20E30A2E96B7BC395917A877
	42086558BEF9A5BD1FCA26E9774F880A5FB0CD3BFF3E97407DF3B3502226E13E752C0764DBCB20DD9C5BD4D089D08760G980C45337B95B7F9BFE6166354FBBC92C95749BB83C8F1C41FAA5BE1E3FC29CFBB4648D8E50F0E0DB52BEFAE2467E1FCAC9B4BFB9C7FBA45E72A70EC0CDFD1BCE92F3B8D727F5B50E75758347A8353951D91534D747F1E1E31B153357AD75B6859B5DD2B7F149575677F2829A06EC728FFC71B40577BEB55FD32F8797D79394B2A036D934F50E71D5699ECBE98AE0D6282B314EF33E0EF
	EAA7FB0C5D63A25B157C0ECA24FBCC947045GB0DCB44525C0BBED864E615581E55EEED05DFF90387CF3D8F1D9F530987BAF0F79997702A77499770A47780C7B2DC7FF463D61B1BE63842F7A0CE36B185DCFF0F93743F3506DD8F78C9B403A631E5BF1FDDEF01742B5825A31G09G8BG96832C3E831E41812A81DAG82F7204C6F29CC943463GD2819683944FECAA28GE88410G22G62E622CC35BBE60839A4FA3538888D8EEDD063D65F21170B156DDFE0502799D44BE3157344A1EDB52AFBEA6B1D24BE70
	1E863746C00E449529F92F9C015F119C0D789CDB538D7499A39566B9E3A62E73B4BB2E736AD9B826B72DE44DDA20AD81C4824C82C882D8G308420F8B6BCAF83544CC619F7ECC446816D51G31GC9G69GF9F742338EC0B50083E0741DA873F72BF66ED087691F34095E325941EBC3EC9AEA4CB5AA7EA5A1F855902962EDD15DB3DB43F53696486B74365CDEA92F25F64D065229DE7742912C2B5A2FF2B57AF2856B27640C12EB1DB49DE5783501B58A9E53D12C6F126DDA5E431F6AA0F5AB37583B0EFF9C3D1F
	EC6825DBF6ECAEA330F57B76CD325C7F7A95398267F06498EFC93F3E8E79C5BF5677E26FE4F54884384E87A06FB4D8AD7F26812C65E75CE9DC4B4F9614DA7ED260A93ACBF9373561AEEDAD3F728C59D3496FA91AEB2DB6D224E4782AB5EFA17EA4CCD8906D05ED1C5688BB76B7CE5165C0DA496E15B2CD9611718FEFC5BD27037AAA2F473ECE6B7A7A131F53F4B62C5C027D1D8942FF65C519B33A7ECA5BB8CD559F7B2EFB1BC77DEEFD73DDEC4E9B6F426F3E6FB524F7F07AE3B6B5FDD02647F27AB3DF9B4947F3
	7A0E6DC67494CEBF3C51287F051C7E6A77C6720BB95DFD56081EEFC1FA25217DEBB97DD42B117CE68B3E8F5EED537BF33D8D7D395A427C39852EDE8BFBBF477CF92E9D7DB95CE26C4F78FBAEF21D84BC960B624FC9962DBF8B7BC86C9F07035F69E69E4D702FECADD270FC75996D2A0BAA9A779AC6FBF1E58127B107E17804EEC54FE9D5DFD774AAFD1D567435E83B0A263141F2CE696F0C8A5F7F19A2F3C6535F31A2950D7747562B5626DDAFB2677379FACE309B5153B9FD344E9F88FD8927E7984A2F3E9B6919
	067A57F17AE23BD17F951CBEE0A8DF4369A9BAFFA47496CECFBBEB543F17531FB2149FFDB73EC7B9550F4F1251001BFFB756A92774E39DB57F9E2CD3BEC1F92B205D8C603D87F91387302699FE4FC85F0FFC54A3F03FFF3D4D64BBD43A5ECCD72787F5A5F27DAFF15C12FB70BD50A725EB0D7B623E45E4336D788EF2733D585F2AAA6C2F03633A36A3AE0663C6EDC459B40EAB3D08FCC5F1083B6DAC7FBE1063BCB67E3E17632EEBC53E78B8DC1FFE9B2ECF9660CA67A06FBC9B2ECF6B9CDC1FAC4A9B8DED8BC0B2
	677DEFBB763BF04EC857E76FF9F24F7A3912572ECB6E433ED3F85FF59C77A09FDBB847FD4C3F8DCE643875DCF68947FD6E606BF1BF626E61FABBB8AE574A5703638ADB79FAF05CF24ED7944F73517ECB2B6F1C3BC43958914B315A445E81321EB5715A3A50E10AEF0C5756051E61315C8A34A4BE5EA786547ACB9F00E73D87064E1765E79E0F115DF5322C43208FA6770A21DCC0660FB015DBEE9FEA1EE2CC658A2B0C6462E47EE4D3B95FF6A33985B2FF16295C580DC35919B757CC6E2F970D64CAE47EB2D3B92F
	2E66C520CA66EFB415138C6590BACC6546379A498D4A7C1126F2E1C36AB377336BAED16E51914B3178F15869FB6C395A386A22F84BDCEDDC7D0E6F4189C0DB77A046D5D6103F0E86FC740343092BF0C33BE749320986FDB039278D63A3D5664FB015CB9CF21E161A4AED50FD7BCD20A8C17E26A841CC2E54504ECA193F56D4EE3321BE04D6D3397B8D47A7497C6126F279D6A339C9B23F45D42E58B0BE9012CC658A8C7525497C1926F2878784FEDF273A7FC57F652F173EF6D9F5305AD47A003E667C62003A664C
	F0E3E91EA77C9B5C7F9B5635CEF4E3DDEBAD47ADE735CFE88FF25CB9AF7205F35C073CA61646F11F525C2F744710A77F98F157D6A1DFB547DDCD7341A8E847F25C4B5B114F42F1BF5C08B8615F796F8CE90ED8876DD60E3B7DA27239B86EAA1A77A4C0BB0563B2ED5CE60E3B1663CA9391F7874D0582508E6738C2AB72C5A5629E56E9D77C492F3ADFEA3B7C3E751E7D727B568A075E37D6BB743EB5054EE1BA4C4BE2BEDFF95CBF041FA0AE03626A205D4AF1C79DFC6DB86E67FCCD92B8EEB765DBBD8FFCF59E62
	E238ECA947FDCDF584209D4EF1315C16A80EBB48E553B96EC92A3762A170550790370D4B56F15C475497E220ED6138515CBF92B86E73D6DEB7CEC25CDFA8DF8B34DD9C37269D7982C9D8AB7921A7785B79993D443FD8BD24231BFF2B1BE47C5BAB2CF550EFF501A7ABC9290764BD2C2D07FC688D5675F8C051F50597F595BDEC3C472AF5EDG9E82282BD2250B75D741EBB955867DAD8B51DFB35C7FA04876AF2B02EDFF39CF31FDEA956AF298684A56688A28F4513FC926524BF445EC8F5675760022EB598E5495
	ED20EBE508F14D524DD35CC66CAF5E203F02907D256866695B0B4136D78CAA36A7DEC4DDE9863A560450152973274936E0DD27AEA83AF2797AAFB150D598C2576AC73473B455067D15BC226F6F6D907D15BD221D271F34865B5E7F3DE27B27D654D5E9206BDD0D2E5AC7027D2975916DBC5D2719279FDED4F4FD406749E520CB9B63E140937508B22E99BADDEF5A03F5A58A0AAE0783F54599685246F8B270A42BF4AD502D494BFCCDB28C7A534638363F253AB529F7845BDE182358FEBF37BD7F5150B13E76D1C5
	977DA679D16DBCB5E966E9EB2E226BC13EA6FB8CF4E9E3DCBB2E3AC73573749D373D4520BFED0CEB7B73BE2A1D2767B57EF4B2CF317DB037FD50C017B646353AA2F57334439A2C6B61A30A2EDBF81CCCB250250DF12DAE0BEE1E7635E2FF89867DE9E3DC5B5FFC5DBC6D54583E6828E27B06F6543540C017B64633B47E14172C584EEAE8933BF98DADF92475432263826F67DEAF2F031A7443E43655A9B2A22F9106A563EFC1EEB921501F61355AF843BE333EE6B2BF2ED7E4F66CC019AC5E67F38D8A7D790DC8AF
	7DA95273CEAA74D7F90D371553B7B6A9748AEEF3B4279FBE25321957B367F37A6CE605FE0A57AC739F9BEACC73CFABB235DCE715214CD0EB747A99251F893CCE9BB06907497C21C51179186B36BC06635979F7051E417B4C98F2BCAD5FA9B2193C4FF50F735FBE1ED56863F81F4D1CBE35D5218FF079C8CEFF582A50D3789CA7F3FA06CD217BF9ADDEC8C1FA01C3211FECC7FAE54AD0E3AA273F95E44FBB13037E962F9764C6278C27DE946FD54BC5034C2CE149F58655E192C1A6F5D8F2E9C175548C10D9BAAC39
	5DBB54F2C5CFC06E7044F064DE8A2A27D602CC6D3064FE97D4CFED8599E9D8F2BB0366A59CE4A68DCBAEAD284EE4811924E149358455FD52C0A6F3D8F2A7026C941EECAAA8FEF2B8F2B7875551CAC1E64F3064B684692B83994730649685698B00CC443064DE8D529783B2F143120B8A52178CB28B06A5979624AF8BE4721E9A0E5C53C16BD782B2E543128B8455892BC026F1D8F2E702FE5F5381B203A16458B3781CEEFD2DE3EE373E567114D74F176A55730D326B79465A75FC2FD4697996D769796AF76879CE
	6D5073E5176A7916176A795688FA3EF784BDDF1D41B8CE9A0D43E6B08E1B1E4FEF5573852CFA3E8E833DDD86FA6FB628D15DE7D0235AED50DF15C1FFF9221EEF2528674BB7503B46C06FCC83BE0B815FCD8676CDB230AF43E07E96994C5F38D6BDDFD42B1E2F3BDD4FF73EDD4D477E8F0513679F0004F756A2E119A42C41BFDE3A0EFCA7A589B9470FC842727D64FBDC175FA79472AF5B8E511FDBACBC3FEDD76E2F27656688F90704656C535CC3C29E793E7610300CFEFEFBC84815BC5EG793364649F7E54DC70
	F78B5F53C45B981B2702C2507CD670B7F559373DFFE55138EDBDB9C27EB74272B62B4B5BE1850D740FD0568A85B62A1AFC276F120422DAA27B8548FEC1E5F7578AB9F63FA4FC206069875B0C5A60355AC5E0D969307A4E519E08C9795F8AAB5BFC6DB62AG26655BEF3E853534C5482BEA05D5D2877DFDGBCFAC8427BCA57F564D782B2E1B71B06B7E3E25E7452FFF438AB8CF0B8B17C9F472ED2A1D7C9C2F633A4644E1B53A589F9EC4532CFA3A27B8C3F63CC4B5967C2C6ECA196843105BA2C5EC6C16653739A
	77EB5C033136FF3C0742FF4A580663447F12318978F789B6DDD47F1D1EA5E36C26FF57A8AF151D1BA5294A6FB659EFE58669EFB69B816FCD455F4624847EAA187FAD0F06AE2B4BA5D9DD67763A9D646FFA6C6B13C4555FA26CA37D7B11C7A6B2DAF89F313BD3A5737FD0CB8788C09FDC0E379EGGB0E6GGD0CB818294G94G88G88G500171B4C09FDC0E379EGGB0E6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG719EGGG
	G
**end of data**/
}
}
