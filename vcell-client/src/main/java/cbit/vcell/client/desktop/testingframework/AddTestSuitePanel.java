/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.testingframework;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestSuiteInfoNew;

/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 1:12:40 PM)
 * @author: Anuradha Lakshminarayana
 */
 
public class AddTestSuitePanel extends javax.swing.JPanel{
	private javax.swing.JLabel ivjVersionLabel = null;
	private javax.swing.JTextField ivjVersionTextField = null;
	private cbit.vcell.client.TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private javax.swing.JButton ivjApplyButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JTextField ivjNumericsVersionTextField = null;
	private javax.swing.JLabel ivjNumVersLabel = null;
	private javax.swing.JLabel ivjVcellVersLabel = null;
	private javax.swing.JTextField ivjVCellVerTextField = null;
	private cbit.vcell.numericstest.TestSuiteInfoNew fieldTestSuiteInfo = null;  //  @jve:decl-index=0:
	private JLabel jLabelAnnot = null;
	private JTextArea jTextAreaAnnot = null;
	private final JRadioButton copyRegressionReferencesRadioButton = new JRadioButton();
	private final JRadioButton assignOriginalAsRadioButton = new JRadioButton();
	private final JRadioButton assignNewAsRadioButton = new JRadioButton();
	private ButtonGroup regressionButtonGroup = new ButtonGroup();
	private JPanel regressionInfoPanel = new JPanel();



/**
 * AddTestSuitePanel constructor comment.
 */
public AddTestSuitePanel() {
	super();
	initialize();
}

/**
 * Comment
 */
private void applyTestSuite() throws Exception{
	String testSuiteVersionID = getVersionTextField().getText();
	String vCellBuildVersion = getVCellVerTextField().getText();
	String numericsBuildVersion = getNumericsVersionTextField().getText();
	if (testSuiteVersionID == null || testSuiteVersionID.length() == 0 || 
		vCellBuildVersion == null || vCellBuildVersion.length() == 0 || 
		numericsBuildVersion == null || numericsBuildVersion.length() == 0) {
		throw new Exception("TestSuite must have Version no./VCell Version no./Numerics Version no.");
	}

	TestSuiteInfoNew newTSInfo = new TestSuiteInfoNew(null, testSuiteVersionID, vCellBuildVersion, numericsBuildVersion, null,getJTextAreaAnnot().getText(),false);
	setTestSuiteInfo(newTSInfo);
}


/**
 * connEtoC2:  (ApplyButton.action.actionPerformed(java.awt.event.ActionEvent) --> AddTestSuitePanel.applyTestSuite(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.applyTestSuite();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 5;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints.fill = GridBagConstraints.NONE;
			gridBagConstraints.gridy = 4;

			final GridBagLayout gridBagLayout_1 = new GridBagLayout();
			gridBagLayout_1.rowHeights = new int[] {0,7,7};
			regressionInfoPanel.setLayout(gridBagLayout_1);
			regressionInfoPanel.setBorder(new LineBorder(Color.black, 2, false));
			jLabelAnnot = new JLabel();
			jLabelAnnot.setText("Test Suite Annotation");
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			final java.awt.GridBagLayout gridBagLayout = new java.awt.GridBagLayout();
			gridBagLayout.rowHeights = new int[] {0,0,0,7,0,0};
			ivjJPanel1.setLayout(gridBagLayout);
			
			final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
			gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints_1.weightx = 0;
			gridBagConstraints_1.gridwidth = 2;
			gridBagConstraints_1.gridy = 3;
			gridBagConstraints_1.gridx = 0;
			ivjJPanel1.add(regressionInfoPanel, gridBagConstraints_1);

			copyRegressionReferencesRadioButton.setSelected(true);
			copyRegressionReferencesRadioButton.setText("Copy Source Regression References to New TestSuite");
			final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
			gridBagConstraints_2.anchor = GridBagConstraints.WEST;
			gridBagConstraints_2.weightx = 1;
			gridBagConstraints_2.gridx = 0;
			gridBagConstraints_2.gridy = 0;
			gridBagConstraints_2.insets = new Insets(0, 0, 0, 0);
			regressionInfoPanel.add(copyRegressionReferencesRadioButton, gridBagConstraints_2);

			assignOriginalAsRadioButton.setText("Set Source as Regression Reference for New TestSuite");
			final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
			gridBagConstraints_3.weightx = 1;
			gridBagConstraints_3.anchor = GridBagConstraints.WEST;
			gridBagConstraints_3.gridy = 1;
			gridBagConstraints_3.gridx = 0;
			regressionInfoPanel.add(assignOriginalAsRadioButton, gridBagConstraints_3);

			assignNewAsRadioButton.setText("Set New as Regression Reference for New TestSuite");
			final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
			gridBagConstraints_4.weightx = 1;
			gridBagConstraints_4.anchor = GridBagConstraints.WEST;
			gridBagConstraints_4.gridy = 2;
			gridBagConstraints_4.gridx = 0;
			regressionInfoPanel.add(assignNewAsRadioButton, gridBagConstraints_4);
			ivjJPanel1.add(jLabelAnnot, gridBagConstraints);
			JScrollPane jscrollPane = new JScrollPane(getJTextAreaAnnot(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJPanel1.add(jscrollPane, gridBagConstraints1);

			java.awt.GridBagConstraints constraintsVersionLabel = new java.awt.GridBagConstraints();
			constraintsVersionLabel.gridx = 0; constraintsVersionLabel.gridy = 0;
			constraintsVersionLabel.ipadx = 55;
			constraintsVersionLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getVersionLabel(), constraintsVersionLabel);

			java.awt.GridBagConstraints constraintsVcellVersLabel = new java.awt.GridBagConstraints();
			constraintsVcellVersLabel.gridx = 0; constraintsVcellVersLabel.gridy = 1;
			constraintsVcellVersLabel.ipadx = 25;
			constraintsVcellVersLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getVcellVersLabel(), constraintsVcellVersLabel);

			java.awt.GridBagConstraints constraintsNumVersLabel = new java.awt.GridBagConstraints();
			constraintsNumVersLabel.gridx = 0; constraintsNumVersLabel.gridy = 2;
			constraintsNumVersLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getNumVersLabel(), constraintsNumVersLabel);

			java.awt.GridBagConstraints constraintsVersionTextField = new java.awt.GridBagConstraints();
			constraintsVersionTextField.gridx = 1; constraintsVersionTextField.gridy = 0;
			constraintsVersionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsVersionTextField.weightx = 1.0;
			constraintsVersionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getVersionTextField(), constraintsVersionTextField);

			java.awt.GridBagConstraints constraintsVCellVerTextField = new java.awt.GridBagConstraints();
			constraintsVCellVerTextField.gridx = 1; constraintsVCellVerTextField.gridy = 1;
			constraintsVCellVerTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsVCellVerTextField.weightx = 1.0;
			constraintsVCellVerTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getVCellVerTextField(), constraintsVCellVerTextField);

			java.awt.GridBagConstraints constraintsNumericsVersionTextField = new java.awt.GridBagConstraints();
			constraintsNumericsVersionTextField.gridx = 1; constraintsNumericsVersionTextField.gridy = 2;
			constraintsNumericsVersionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNumericsVersionTextField.weightx = 1.0;
			constraintsNumericsVersionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getNumericsVersionTextField(), constraintsNumericsVersionTextField);
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
 * Return the NumericsVersionTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNumericsVersionTextField() {
	if (ivjNumericsVersionTextField == null) {
		try {
			ivjNumericsVersionTextField = new javax.swing.JTextField();
			ivjNumericsVersionTextField.setName("NumericsVersionTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNumericsVersionTextField;
}


/**
 * Return the NumVersLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNumVersLabel() {
	if (ivjNumVersLabel == null) {
		try {
			ivjNumVersLabel = new javax.swing.JLabel();
			ivjNumVersLabel.setName("NumVersLabel");
			ivjNumVersLabel.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjNumVersLabel.setText("Numerics Version No.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNumVersLabel;
}


/**
 * Gets the testingFrameworkWindowManager property (cbit.vcell.client.TestingFrameworkWindowManager) value.
 * @return The testingFrameworkWindowManager property value.
 * @see #setTestingFrameworkWindowManager
 */
public cbit.vcell.client.TestingFrameworkWindowManager getTestingFrameworkWindowManager() {
	return fieldTestingFrameworkWindowManager;
}


/**
 * Gets the testSuiteInfo property (cbit.vcell.numericstest.TestSuiteInfoNew) value.
 * @return The testSuiteInfo property value.
 * @see #setTestSuiteInfo
 */
public TestingFrameworkWindowManager.NewTestSuiteUserInformation getTestSuiteInfo() throws Exception{
	applyTestSuite();
	return
		new TestingFrameworkWindowManager.NewTestSuiteUserInformation(
				fieldTestSuiteInfo,
				(copyRegressionReferencesRadioButton.isSelected()?TestingFrameworkWindowManager.COPY_REGRREF:0)+
				(assignOriginalAsRadioButton.isSelected()?TestingFrameworkWindowManager.ASSIGNORIGINAL_REGRREF:0)+
				(assignNewAsRadioButton.isSelected()?TestingFrameworkWindowManager.ASSIGNNEW_REGRREF:0)
		);
}


/**
 * Return the VcellVersLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getVcellVersLabel() {
	if (ivjVcellVersLabel == null) {
		try {
			ivjVcellVersLabel = new javax.swing.JLabel();
			ivjVcellVersLabel.setName("VcellVersLabel");
			ivjVcellVersLabel.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjVcellVersLabel.setText("VCell Version No.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVcellVersLabel;
}


/**
 * Return the VCellVerTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getVCellVerTextField() {
	if (ivjVCellVerTextField == null) {
		try {
			ivjVCellVerTextField = new javax.swing.JTextField();
			ivjVCellVerTextField.setName("VCellVerTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVCellVerTextField;
}


/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getVersionLabel() {
	if (ivjVersionLabel == null) {
		try {
			ivjVersionLabel = new javax.swing.JLabel();
			ivjVersionLabel.setName("VersionLabel");
			ivjVersionLabel.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjVersionLabel.setText("Version No.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVersionLabel;
}

/**
 * Return the JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getVersionTextField() {
	if (ivjVersionTextField == null) {
		try {
			ivjVersionTextField = new javax.swing.JTextField();
			ivjVersionTextField.setName("VersionTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVersionTextField;
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
	regressionButtonGroup.add(copyRegressionReferencesRadioButton);
	regressionButtonGroup.add(assignOriginalAsRadioButton);
	regressionButtonGroup.add(assignNewAsRadioButton);	
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AddTestSuitePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(468, 226);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

 		initConnections();
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AddTestSuitePanel aAddTestSuitePanel;
		aAddTestSuitePanel = new AddTestSuitePanel();
		frame.setContentPane(aAddTestSuitePanel);
		frame.setSize(aAddTestSuitePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * Sets the testSuiteInfo property (cbit.vcell.numericstest.TestSuiteInfo) value.
 * @param testSuiteInfo The new value for the property.
 * @see #getTestSuiteInfo
 */
public void resetTextFields(String tsAnnotation,boolean bNeedsRegressionInfo) {
	getVersionTextField().setText("");
	getVCellVerTextField().setText("");
	getNumericsVersionTextField().setText("");
	getJTextAreaAnnot().setText(tsAnnotation);
	copyRegressionReferencesRadioButton.setSelected(true);
	regressionInfoPanel.setVisible(bNeedsRegressionInfo);
}


/**
 * Sets the testingFrameworkWindowManager property (cbit.vcell.client.TestingFrameworkWindowManager) value.
 * @param testingFrameworkWindowManager The new value for the property.
 * @see #getTestingFrameworkWindowManager
 */
public void setTestingFrameworkWindowManager(cbit.vcell.client.TestingFrameworkWindowManager testingFrameworkWindowManager) {
	cbit.vcell.client.TestingFrameworkWindowManager oldValue = fieldTestingFrameworkWindowManager;
	fieldTestingFrameworkWindowManager = testingFrameworkWindowManager;
	firePropertyChange("testingFrameworkWindowManager", oldValue, testingFrameworkWindowManager);
}

/**
 * Sets the testSuiteInfo property (cbit.vcell.numericstest.TestSuiteInfoNew) value.
 * @param testSuiteInfo The new value for the property.
 * @see #getTestSuiteInfo
 */
private void setTestSuiteInfo(cbit.vcell.numericstest.TestSuiteInfoNew testSuiteInfo) {
	cbit.vcell.numericstest.TestSuiteInfoNew oldValue = fieldTestSuiteInfo;
	fieldTestSuiteInfo = testSuiteInfo;
	firePropertyChange("testSuiteInfo", oldValue, testSuiteInfo);
}


/**
 * This method initializes jTextAreaAnnot	
 * 	
 * @return javax.swing.JTextArea	
 */
private JTextArea getJTextAreaAnnot() {
	if (jTextAreaAnnot == null) {
		jTextAreaAnnot = new JTextArea();
		jTextAreaAnnot.setLineWrap(true);
	}
	return jTextAreaAnnot;
}

}  //  @jve:decl-index=0:visual-constraint="10,10"
