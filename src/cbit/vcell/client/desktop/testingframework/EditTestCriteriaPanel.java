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

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestCriteriaNewBioModel;
import cbit.vcell.numericstest.TestCriteriaNewMathModel;
import cbit.vcell.solver.SimulationInfo;
/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 1:55:07 PM)
 * @author: Anuradha Lakshminarayana
 */
public class EditTestCriteriaPanel extends javax.swing.JPanel {
	private javax.swing.JLabel ivjAbsErrLabel = null;
	private javax.swing.JButton ivjRefMathModelButton = null;
	private javax.swing.JLabel ivjRefMathModelLabel = null;
	private javax.swing.JButton ivjRefSimButton = null;
	private javax.swing.JLabel ivjRefSimLabel = null;
	private javax.swing.JLabel ivjRelErrLabel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField ivjAbsErrTextField = null;
	private javax.swing.JTextField ivjRelErrTextField = null;
	private TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private java.lang.String fieldSolutionType = null;
	private TestCriteriaNew fieldExistingTestCriteria = null;
	private TestCriteriaNew fieldNewTestCriteria = null;  //  @jve:decl-index=0:
	private SimulationInfo fieldReferenceSimInfo = null;
	private MathModelInfo fieldReferenceMathModelInfo = null;
	private javax.swing.JButton ivjSelectRefBMAppJButton = null;
	private javax.swing.JButton ivjSelectRefSimJButton = null;
	private BioModelInfo ivjbioModelInfo = null;
	private javax.swing.JLabel ivjBioModelAppLabel = null;
	private SimulationInfo ivjbmAppSimInfo = null;
	private javax.swing.JLabel ivjBmAppSimLabel = null;
	private String ivjappName = null;

	private class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == EditTestCriteriaPanel.this.getRefMathModelButton()) 
				connEtoC1(e);
			if (e.getSource() == EditTestCriteriaPanel.this.getRefSimButton()) 
				connEtoC2(e);
			if (e.getSource() == EditTestCriteriaPanel.this.getSelectRefSimJButton()) 
				connEtoC5(e);
			if (e.getSource() == EditTestCriteriaPanel.this.getSelectRefBMAppJButton()) 
				connEtoC4(e);
		};
	};
/**
 * EditTestCriteriaPanel constructor comment.
 */
public EditTestCriteriaPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
private void applyTestCriteriaInfo() {
	TestCriteriaNew newTestCriteria = null;
	Double relErr = Double.valueOf(getRelErrTextField().getText());
	Double absErr = Double.valueOf(getAbsErrTextField().getText());
	
	if (getSolutionType().equals(TestCaseNew.REGRESSION)) {
		// THIS CHECK FOR REF MATH & SIMINFO IS NOT NECESSARY IF USER WANTS TO CHANGE THE ERRS ONLY.
		//if(getReferenceMathModelInfo() == null || getReferenceSimInfo() == null){
			//PopupGenerator.showErrorDialog("Reference MathModel and Simulation Infos cannot be null");
			//return;
		//}
		// ************ NEED TO CHECK *************
		//MathModelInfo checkMMInfo = getTestingFrameworkWindowManager().getMMInfoFromSimKey(getReferenceSimInfo().getVersion().getVersionKey());
		//if(checkMMInfo != null && checkMMInfo.getVersion().getVersionKey().compareEqual(getReferenceSimInfo().getVersion().getVersionKey())){
			//PopupGenerator.showErrorDialog("Reference SimInfo and Reference MathModel are not related");
			//return;
		//}
		
		//newTestCriteria = new TestCriteriaNew(null, getExistingTestCriteria().getSimInfo(), getReferenceMathModelInfo(), getReferenceSimInfo(), relErr, absErr, null);
		if(getExistingTestCriteria() instanceof TestCriteriaNewMathModel){
			newTestCriteria =
				new TestCriteriaNewMathModel(
					null,
					getExistingTestCriteria().getSimInfo(),
					getReferenceMathModelInfo(),
					getReferenceSimInfo(),
					relErr, absErr, null,TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null);
		}else if(getExistingTestCriteria() instanceof TestCriteriaNewBioModel){
			newTestCriteria =
				new TestCriteriaNewBioModel(
					null,
					getExistingTestCriteria().getSimInfo(),
					getbioModelInfo(),
					getappName(),
					getbmAppSimInfo(),
					relErr, absErr, null,TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null);
		}
	} else {  // CONSTRUCTED SOLN OR EXACT SOLUTION
		if(getExistingTestCriteria() instanceof TestCriteriaNewMathModel){
			newTestCriteria =
				new TestCriteriaNewMathModel(
					null,
					getExistingTestCriteria().getSimInfo(),
					null,null,
					relErr, absErr, null,TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null);
		}else if(getExistingTestCriteria() instanceof TestCriteriaNewBioModel){
			newTestCriteria =
				new TestCriteriaNewBioModel(
					null,
					getExistingTestCriteria().getSimInfo(),
					null,null,null,
					relErr, absErr, null,TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null);
		}
	}
	setNewTestCriteria(newTestCriteria);
}
/**
 * connEtoC1:  (RefMathModelButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditTestCriteriaPanel.selectRefMathModel(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.selectRefMathModel(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (RefSimButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditTestCriteriaPanel.selectRefSimInfo(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.selectRefSimInfo(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (SelectRefBMAppJButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditTestCriteriaPanel.selectBMApp()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.selectBMApp();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (SelectRefSimJButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditTestCriteriaPanel.selectBMAppSim()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.selectBMAppSim();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AbsErrLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAbsErrLabel() {
	if (ivjAbsErrLabel == null) {
		try {
			ivjAbsErrLabel = new javax.swing.JLabel();
			ivjAbsErrLabel.setName("AbsErrLabel");
			ivjAbsErrLabel.setText("Max. Absolute Error:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAbsErrLabel;
}
/**
 * Return the JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAbsErrTextField() {
	if (ivjAbsErrTextField == null) {
		try {
			ivjAbsErrTextField = new javax.swing.JTextField();
			ivjAbsErrTextField.setName("AbsErrTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAbsErrTextField;
}
/**
 * Return the appName property value.
 * @return java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.String getappName() {
	// user code begin {1}
	// user code end
	return ivjappName;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBioModelAppLabel() {
	if (ivjBioModelAppLabel == null) {
		try {
			ivjBioModelAppLabel = new javax.swing.JLabel();
			ivjBioModelAppLabel.setName("BioModelAppLabel");
			ivjBioModelAppLabel.setText("...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBioModelAppLabel;
}
/**
 * Return the bioModelInfo property value.
 * @return cbit.vcell.biomodel.BioModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private org.vcell.util.document.BioModelInfo getbioModelInfo() {
	// user code begin {1}
	// user code end
	return ivjbioModelInfo;
}
/**
 * Return the bmAppSimInfo property value.
 * @return cbit.vcell.solver.SimulationInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.SimulationInfo getbmAppSimInfo() {
	// user code begin {1}
	// user code end
	return ivjbmAppSimInfo;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBmAppSimLabel() {
	if (ivjBmAppSimLabel == null) {
		try {
			ivjBmAppSimLabel = new javax.swing.JLabel();
			ivjBmAppSimLabel.setName("BmAppSimLabel");
			ivjBmAppSimLabel.setText("...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBmAppSimLabel;
}
/**
 * Gets the existingTestCriteria property (cbit.vcell.numericstest.TestCriteriaNew) value.
 * @return The existingTestCriteria property value.
 * @see #setExistingTestCriteria
 */
private TestCriteriaNew getExistingTestCriteria() {
	return fieldExistingTestCriteria;
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

			java.awt.GridBagConstraints constraintsAbsErrLabel = new java.awt.GridBagConstraints();
			constraintsAbsErrLabel.gridx = 0; constraintsAbsErrLabel.gridy = 0;
			constraintsAbsErrLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getAbsErrLabel(), constraintsAbsErrLabel);

			java.awt.GridBagConstraints constraintsRelErrLabel = new java.awt.GridBagConstraints();
			constraintsRelErrLabel.gridx = 0; constraintsRelErrLabel.gridy = 1;
			constraintsRelErrLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getRelErrLabel(), constraintsRelErrLabel);

			java.awt.GridBagConstraints constraintsAbsErrTextField = new java.awt.GridBagConstraints();
			constraintsAbsErrTextField.gridx = 1; constraintsAbsErrTextField.gridy = 0;
			constraintsAbsErrTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAbsErrTextField.weightx = 1.0;
			constraintsAbsErrTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getAbsErrTextField(), constraintsAbsErrTextField);

			java.awt.GridBagConstraints constraintsRelErrTextField = new java.awt.GridBagConstraints();
			constraintsRelErrTextField.gridx = 1; constraintsRelErrTextField.gridy = 1;
			constraintsRelErrTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRelErrTextField.weightx = 1.0;
			constraintsRelErrTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getRelErrTextField(), constraintsRelErrTextField);

			java.awt.GridBagConstraints constraintsRefMathModelButton = new java.awt.GridBagConstraints();
			constraintsRefMathModelButton.gridx = 0; constraintsRefMathModelButton.gridy = 2;
			constraintsRefMathModelButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getRefMathModelButton(), constraintsRefMathModelButton);

			java.awt.GridBagConstraints constraintsRefMathModelLabel = new java.awt.GridBagConstraints();
			constraintsRefMathModelLabel.gridx = 1; constraintsRefMathModelLabel.gridy = 2;
			constraintsRefMathModelLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRefMathModelLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getRefMathModelLabel(), constraintsRefMathModelLabel);

			java.awt.GridBagConstraints constraintsRefSimButton = new java.awt.GridBagConstraints();
			constraintsRefSimButton.gridx = 0; constraintsRefSimButton.gridy = 3;
			constraintsRefSimButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getRefSimButton(), constraintsRefSimButton);

			java.awt.GridBagConstraints constraintsRefSimLabel = new java.awt.GridBagConstraints();
			constraintsRefSimLabel.gridx = 1; constraintsRefSimLabel.gridy = 3;
			constraintsRefSimLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRefSimLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getRefSimLabel(), constraintsRefSimLabel);

			java.awt.GridBagConstraints constraintsSelectRefSimJButton = new java.awt.GridBagConstraints();
			constraintsSelectRefSimJButton.gridx = 0; constraintsSelectRefSimJButton.gridy = 5;
			constraintsSelectRefSimJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSelectRefSimJButton(), constraintsSelectRefSimJButton);

			java.awt.GridBagConstraints constraintsSelectRefBMAppJButton = new java.awt.GridBagConstraints();
			constraintsSelectRefBMAppJButton.gridx = 0; constraintsSelectRefBMAppJButton.gridy = 4;
			constraintsSelectRefBMAppJButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSelectRefBMAppJButton(), constraintsSelectRefBMAppJButton);

			java.awt.GridBagConstraints constraintsBioModelAppLabel = new java.awt.GridBagConstraints();
			constraintsBioModelAppLabel.gridx = 1; constraintsBioModelAppLabel.gridy = 4;
			constraintsBioModelAppLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBioModelAppLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getBioModelAppLabel(), constraintsBioModelAppLabel);

			java.awt.GridBagConstraints constraintsBmAppSimLabel = new java.awt.GridBagConstraints();
			constraintsBmAppSimLabel.gridx = 1; constraintsBmAppSimLabel.gridy = 5;
			constraintsBmAppSimLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBmAppSimLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getBmAppSimLabel(), constraintsBmAppSimLabel);
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
 * Gets the newTestCriteria property (cbit.vcell.numericstest.TestCriteriaNew) value.
 * @return The newTestCriteria property value.
 * @see #setNewTestCriteria
 */
public TestCriteriaNew getNewTestCriteria() {
	applyTestCriteriaInfo();
	return fieldNewTestCriteria;
}
/**
 * Gets the referenceMathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The referenceMathModelInfo property value.
 * @see #setReferenceMathModelInfo
 */
private MathModelInfo getReferenceMathModelInfo() {
	return fieldReferenceMathModelInfo;
}
/**
 * Gets the referenceSimInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The referenceSimInfo property value.
 * @see #setReferenceSimInfo
 */
private SimulationInfo getReferenceSimInfo() {
	return fieldReferenceSimInfo;
}
/**
 * Return the RefMathModelButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRefMathModelButton() {
	if (ivjRefMathModelButton == null) {
		try {
			ivjRefMathModelButton = new javax.swing.JButton();
			ivjRefMathModelButton.setName("RefMathModelButton");
			ivjRefMathModelButton.setText("Select Ref. MathModel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefMathModelButton;
}
/**
 * Return the RefMathModelLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRefMathModelLabel() {
	if (ivjRefMathModelLabel == null) {
		try {
			ivjRefMathModelLabel = new javax.swing.JLabel();
			ivjRefMathModelLabel.setName("RefMathModelLabel");
			ivjRefMathModelLabel.setText("...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefMathModelLabel;
}
/**
 * Return the RefSimButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRefSimButton() {
	if (ivjRefSimButton == null) {
		try {
			ivjRefSimButton = new javax.swing.JButton();
			ivjRefSimButton.setName("RefSimButton");
			ivjRefSimButton.setText("Select Ref. Simulation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefSimButton;
}
/**
 * Return the RefSimLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRefSimLabel() {
	if (ivjRefSimLabel == null) {
		try {
			ivjRefSimLabel = new javax.swing.JLabel();
			ivjRefSimLabel.setName("RefSimLabel");
			ivjRefSimLabel.setText("...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefSimLabel;
}
/**
 * Return the RelErrLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRelErrLabel() {
	if (ivjRelErrLabel == null) {
		try {
			ivjRelErrLabel = new javax.swing.JLabel();
			ivjRelErrLabel.setName("RelErrLabel");
			ivjRelErrLabel.setText("Max. Relative Error:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelErrLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getRelErrTextField() {
	if (ivjRelErrTextField == null) {
		try {
			ivjRelErrTextField = new javax.swing.JTextField();
			ivjRelErrTextField.setName("RelErrTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelErrTextField;
}
/**
 * Return the SelectRefBMAppJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSelectRefBMAppJButton() {
	if (ivjSelectRefBMAppJButton == null) {
		try {
			ivjSelectRefBMAppJButton = new javax.swing.JButton();
			ivjSelectRefBMAppJButton.setName("SelectRefBMAppJButton");
			ivjSelectRefBMAppJButton.setText("Select Ref. BioModel/App");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectRefBMAppJButton;
}
/**
 * Return the SelectRefSimJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSelectRefSimJButton() {
	if (ivjSelectRefSimJButton == null) {
		try {
			ivjSelectRefSimJButton = new javax.swing.JButton();
			ivjSelectRefSimJButton.setName("SelectRefSimJButton");
			ivjSelectRefSimJButton.setText("Select Ref. BM/APP Sim");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectRefSimJButton;
}
/**
 * Gets the solutionType property (java.lang.String) value.
 * @return The solutionType property value.
 * @see #setSolutionType
 */
private java.lang.String getSolutionType() {
	return fieldSolutionType;
}
/**
 * Gets the testingFrameworkWindowManager property (cbit.vcell.client.TestingFrameworkWindowManager) value.
 * @return The testingFrameworkWindowManager property value.
 * @see #setTestingFrameworkWindowManager
 */
private TestingFrameworkWindowManager getTestingFrameworkWindowManager() {
	return fieldTestingFrameworkWindowManager;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getRefMathModelButton().addActionListener(ivjEventHandler);
	getRefSimButton().addActionListener(ivjEventHandler);
	getSelectRefSimJButton().addActionListener(ivjEventHandler);
	getSelectRefBMAppJButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("EditTestCriteriaPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(409, 294);

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
		EditTestCriteriaPanel aEditTestCriteriaPanel;
		aEditTestCriteriaPanel = new EditTestCriteriaPanel();
		frame.setContentPane(aEditTestCriteriaPanel);
		frame.setSize(aEditTestCriteriaPanel.getSize());
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
 * Sets the testSuiteInfo property (cbit.vcell.numericstest.TestSuiteInfo) value.
 * @param testSuiteInfo The new value for the property.
 * @see #getTestSuiteInfo
 */
public void resetTextFields() {
	// If the chosen simInfo already has test criteria defined, set the fields with the appropriate values
	// Else set them to blank.
	
	getRelErrTextField().setText("");		
	getAbsErrTextField().setText("");
	
	if (getExistingTestCriteria() != null) {
		Double relErr = getExistingTestCriteria().getMaxRelError();
		Double absErr = getExistingTestCriteria().getMaxAbsError();
		getRelErrTextField().setText((relErr != null?relErr.toString():null));		
		getAbsErrTextField().setText((absErr != null?absErr.toString():null));
	} 

	// Depending on test case soultion type (which is set by testingFrameworkWindowManager based on the test case
	// the simulation belongs to), enable apprpriate (ref MathModel, ref simInfo buttons) fields.
	//if (!getSolutionType().equals(TestCaseNew.REGRESSION)) {
		getRefMathModelLabel().setText(null);
		getRefSimLabel().setText(null);
		getBioModelAppLabel().setText(null);
		getBmAppSimLabel().setText(null);
		
		getRefMathModelButton().setEnabled(false);
		getRefMathModelLabel().setEnabled(false);
		getRefSimButton().setEnabled(false);
		getRefSimLabel().setEnabled(false);

		getSelectRefBMAppJButton().setEnabled(false);
		getBioModelAppLabel().setEnabled(false);
		getSelectRefSimJButton().setEnabled(false);
		getBmAppSimLabel().setEnabled(false);

	//} else {
		if(getExistingTestCriteria() instanceof TestCriteriaNewMathModel && getSolutionType().equals(TestCaseNew.REGRESSION)){
			getRefMathModelButton().setEnabled(true);
			getRefMathModelLabel().setEnabled(true);
			getRefSimButton().setEnabled(true);
			getRefSimLabel().setEnabled(true);
		}else if (getExistingTestCriteria() instanceof TestCriteriaNewBioModel && getSolutionType().equals(TestCaseNew.REGRESSION)){
			getSelectRefBMAppJButton().setEnabled(true);
			getBioModelAppLabel().setEnabled(true);
			getSelectRefSimJButton().setEnabled(true);
			getBmAppSimLabel().setEnabled(true);
		}
		
		if (getExistingTestCriteria() != null && getExistingTestCriteria().getRegressionSimInfo() != null) {
			if(getExistingTestCriteria() instanceof TestCriteriaNewMathModel){
				MathModelInfo refMMInfo =
					((TestCriteriaNewMathModel)getExistingTestCriteria()).getRegressionMathModelInfo();
				if(refMMInfo == null){
					PopupGenerator.showErrorDialog(this, "No MathModelInfo found for SimInfo Key "+getExistingTestCriteria().getRegressionSimInfo().getVersion().getVersionKey());
				}
				SimulationInfo refSimInfo = null;
				refSimInfo =  getExistingTestCriteria().getRegressionSimInfo();
				if(refSimInfo == null){
					PopupGenerator.showErrorDialog(this, "No SimulationInfo found for simInfo Key "+getExistingTestCriteria().getRegressionSimInfo().getVersion().getVersionKey());
				}
				setReferenceMathModelInfo(refMMInfo);
				setReferenceSimInfo(refSimInfo);
				getRefMathModelLabel().setText("<html>" + refMMInfo.getVersion().getName()+"<br>(MathModelKey="+refMMInfo.getVersion().getVersionKey()+")<br>"+refMMInfo.getVersion().getDate() + "<html>");
				getRefSimLabel().setText("<html>" + refSimInfo.getVersion().getName()+"<br>(SimulationKey:"+refSimInfo.getVersion().getVersionKey()+")</html>");
			}else if(getExistingTestCriteria() instanceof TestCriteriaNewBioModel){
				BioModelInfo refBMInfo =
					((TestCriteriaNewBioModel)getExistingTestCriteria()).getRegressionBioModelInfo();
				if(refBMInfo == null){
					PopupGenerator.showErrorDialog(this, "No bioModelInfo found for SimInfo Key "+getExistingTestCriteria().getRegressionSimInfo().getVersion().getVersionKey());
				}
				SimulationInfo refSimInfo = null;
				refSimInfo =  getExistingTestCriteria().getRegressionSimInfo();
				if(refSimInfo == null){
					PopupGenerator.showErrorDialog(this, "No SimulationInfo found for simInfo Key "+getExistingTestCriteria().getRegressionSimInfo().getVersion().getVersionKey());
				}
				setbioModelInfo(refBMInfo);
				setbmAppSimInfo(refSimInfo);
				setappName(((TestCriteriaNewBioModel)getExistingTestCriteria()).getRegressionApplicationName());
				getBioModelAppLabel().setText("<html>" + refBMInfo.getVersion().getName()+"/"+getappName()+"<br>(BioModelKey="+refBMInfo.getVersion().getVersionKey()+")<br>"+refBMInfo.getVersion().getDate() + "</html>");
				getBmAppSimLabel().setText("<html>" + refSimInfo.getVersion().getName()+"<br>(SimulationKey:"+refSimInfo.getVersion().getVersionKey()+")</html>");
			}
		} else {
			setReferenceMathModelInfo(null);
			setReferenceSimInfo(null);
			getRefMathModelLabel().setText("");
			getRefSimLabel().setText("");

			setbioModelInfo(null);
			setbmAppSimInfo(null);
			setappName(null);
			getBioModelAppLabel().setText(null);
			getBmAppSimLabel().setText(null);
		}
	//}
}
/**
 * Comment
 */
private void selectBMApp() {
	BioModelInfo bmInfo = getTestingFrameworkWindowManager().selectBioModelInfo();
	if (bmInfo != null) {
		//getRefMathModelLabel().setText(null);
		//getRefSimLabel().setText(null);
		//getBioModelAppLabel().setText(null);
		String simContextNames[] = bmInfo.getBioModelChildSummary().getSimulationContextNames();
		String selection = (String)cbit.vcell.client.PopupGenerator.showListDialog(this,simContextNames,"choose application for testCase");
		if (selection!=null){
			setbioModelInfo(bmInfo);
			setappName(selection);
			setbmAppSimInfo(null);
			getBmAppSimLabel().setText(null);
			getBioModelAppLabel().setText("<html>" + 
				getbioModelInfo().getVersion().getName()+
				"/"+getappName()+"<br>(BioModelKey="+
				getbioModelInfo().getVersion().getVersionKey()+")<br>"+
				getbioModelInfo().getVersion().getDate() + "</html>");
			//getBioModelAppLabel().setText(bmInfo.getVersion().getName()+"/"+getappName());
			//setReferenceMathModelInfo(null);
			//setReferenceSimInfo(null);
		}
	}
}
/**
 * Comment
 */
private void selectBMAppSim() {
	try{
		Object[] bmAppNameAndSimInfo =
			getTestingFrameworkWindowManager().selectRefSimInfo(getbioModelInfo(),getappName());
		if (bmAppNameAndSimInfo != null) {
			setbmAppSimInfo((SimulationInfo)bmAppNameAndSimInfo[1]);
			setappName((String)bmAppNameAndSimInfo[0]);
			getBmAppSimLabel().setText("<html>" + getbmAppSimInfo().getVersion().getName()+"<br>(SimulationKey:"+getbmAppSimInfo().getVersion().getVersionKey()+")</html>");
			getBioModelAppLabel().setText("<html>" + getbioModelInfo().getVersion().getName()+"/"+getappName()+"<br>(BioModelKey="+getbioModelInfo().getVersion().getVersionKey()+")<br>"+getbioModelInfo().getVersion().getDate() + "</html>");
		} else {
			PopupGenerator.showErrorDialog(this, "Reference BMAppSimInfo not selected!");
		}
	}catch(org.vcell.util.DataAccessException e){
		cbit.vcell.client.PopupGenerator.showErrorDialog(this, "Error selecting Biomodel/App Sim "+e.getMessage());
	}
}
/**
 * Comment
 */
private void selectRefMathModel(java.awt.event.ActionEvent actionEvent) {
	MathModelInfo mmInfo = getTestingFrameworkWindowManager().selectMathModelInfo();
	if (mmInfo != null) {
		getRefMathModelLabel().setText("<html>" + mmInfo.getVersion().getName()+"<br>(MathModelKey="+mmInfo.getVersion().getVersionKey()+")<br>"+mmInfo.getVersion().getDate() + "</html>");
		setReferenceSimInfo(null);
		getRefSimLabel().setText(null);
		setReferenceMathModelInfo(mmInfo);
	} else {
		PopupGenerator.showErrorDialog(this, "Selected MathModel is null!");
	}

}
/**
 * Comment
 */
private void selectRefSimInfo(java.awt.event.ActionEvent actionEvent) {
	SimulationInfo simInfo = getTestingFrameworkWindowManager().selectRefSimInfo(getReferenceMathModelInfo());
	if (simInfo != null) {
		setReferenceSimInfo(simInfo);
		getRefSimLabel().setText("<html>" + simInfo.getVersion().getName()+"<br>(SimulationKey:"+simInfo.getVersion().getVersionKey()+")</html>");
	} else {
		PopupGenerator.showErrorDialog(this, "Reference SimInfo not selected!");
	}
}
/**
 * Set the appName to a new value.
 * @param newValue java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setappName(java.lang.String newValue) {
	if (ivjappName != newValue) {
		try {
			String oldValue = getappName();
			ivjappName = newValue;
			firePropertyChange("solutionType", oldValue, newValue);
			// user code begin {1}
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
 * Set the bioModelInfo to a new value.
 * @param newValue cbit.vcell.biomodel.BioModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setbioModelInfo(BioModelInfo newValue) {
	if (ivjbioModelInfo != newValue) {
		try {
			ivjbioModelInfo = newValue;
			// user code begin {1}
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
 * Set the bmAppSimInfo to a new value.
 * @param newValue cbit.vcell.solver.SimulationInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setbmAppSimInfo(SimulationInfo newValue) {
	if (ivjbmAppSimInfo != newValue) {
		try {
			SimulationInfo oldValue = getbmAppSimInfo();
			ivjbmAppSimInfo = newValue;
			firePropertyChange("referenceSimInfo", oldValue, newValue);
			// user code begin {1}
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
 * Sets the existingTestCriteria property (cbit.vcell.numericstest.TestCriteriaNew) value.
 * @param existingTestCriteria The new value for the property.
 * @see #getExistingTestCriteria
 */
public void setExistingTestCriteria(TestCriteriaNew existingTestCriteria) {
	TestCriteriaNew oldValue = fieldExistingTestCriteria;
	fieldExistingTestCriteria = existingTestCriteria;
	firePropertyChange("existingTestCriteria", oldValue, existingTestCriteria);
}
/**
 * Sets the newTestCriteria property (cbit.vcell.numericstest.TestCriteriaNew) value.
 * @param newTestCriteria The new value for the property.
 * @see #getNewTestCriteria
 */
public void setNewTestCriteria(TestCriteriaNew newTestCriteria) {
	TestCriteriaNew oldValue = fieldNewTestCriteria;
	fieldNewTestCriteria = newTestCriteria;
	firePropertyChange("newTestCriteria", oldValue, newTestCriteria);
}
/**
 * Sets the referenceMathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @param referenceMathModelInfo The new value for the property.
 * @see #getReferenceMathModelInfo
 */
public void setReferenceMathModelInfo(MathModelInfo referenceMathModelInfo) {
	MathModelInfo oldValue = fieldReferenceMathModelInfo;
	fieldReferenceMathModelInfo = referenceMathModelInfo;
	firePropertyChange("referenceMathModelInfo", oldValue, referenceMathModelInfo);
}
/**
 * Sets the referenceSimInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @param referenceSimInfo The new value for the property.
 * @see #getReferenceSimInfo
 */
public void setReferenceSimInfo(SimulationInfo referenceSimInfo) {
	SimulationInfo oldValue = fieldReferenceSimInfo;
	fieldReferenceSimInfo = referenceSimInfo;
	firePropertyChange("referenceSimInfo", oldValue, referenceSimInfo);
}
/**
 * Sets the solutionType property (java.lang.String) value.
 * @param solutionType The new value for the property.
 * @see #getSolutionType
 */
public void setSolutionType(java.lang.String solutionType) {
	String oldValue = fieldSolutionType;
	fieldSolutionType = solutionType;
	firePropertyChange("solutionType", oldValue, solutionType);
}
/**
 * Sets the testingFrameworkWindowManager property (cbit.vcell.client.TestingFrameworkWindowManager) value.
 * @param testingFrameworkWindowManager The new value for the property.
 * @see #getTestingFrameworkWindowManager
 */
public void setTestingFrameworkWindowManager(TestingFrameworkWindowManager testingFrameworkWindowManager) {
	TestingFrameworkWindowManager oldValue = fieldTestingFrameworkWindowManager;
	fieldTestingFrameworkWindowManager = testingFrameworkWindowManager;
	firePropertyChange("testingFrameworkWindowManager", oldValue, testingFrameworkWindowManager);
}

}
