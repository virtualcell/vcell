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
import java.awt.GridBagConstraints;

import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestCaseNewMathModel;
/**
 * Insert the type's description here.
 * Creation date: (7/22/2004 9:21:05 AM)
 * @author: Anuradha Lakshminarayana
 */
public class TestCaseAddPanel extends javax.swing.JPanel {
	private cbit.vcell.client.TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private String solutionType = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JLabel ivjAnnotationLabel = null;
	private javax.swing.ButtonGroup ivjbuttonGroup1 = null;  //  @jve:decl-index=0:
	private javax.swing.JRadioButton ivjConstructedRadioButton = null;
	private javax.swing.JRadioButton ivjExactRadioButton = null;
	private javax.swing.JLabel ivjMathmodelLabel = null;
	private javax.swing.JRadioButton ivjRegressionRadioButton = null;
	private javax.swing.JButton ivjSelectMModelButton = null;
	private javax.swing.JLabel ivjSolutionTypeLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextArea ivjAnnotationTextArea = null;
	private cbit.vcell.numericstest.TestCaseNew[] fieldNewTestCaseArr = null;  //  @jve:decl-index=0:
	private MathModelInfo fieldMathModelInfo = null;
	private javax.swing.JLabel ivjBioModelAppLabel = null;
	private javax.swing.JButton ivjSelectBMAppButton = null;
	private String[] ivjappNameArr = null;
	private org.vcell.util.document.BioModelInfo ivjbioModelInfo = null;  //  @jve:decl-index=0:
	private JRadioButton ivjExactRadioButtonSteady = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TestCaseAddPanel.this.getSelectMModelButton()) 
				connEtoC5(e);
			if (e.getSource() == TestCaseAddPanel.this.getSelectBMAppButton()) 
				connEtoC7(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == TestCaseAddPanel.this.getExactRadioButton()) 
				connEtoC2(e);
			if (e.getSource() == TestCaseAddPanel.this.getConstructedRadioButton()) 
				connEtoC3(e);
			if (e.getSource() == TestCaseAddPanel.this.getRegressionRadioButton()) 
				connEtoC4(e);
		};
	};

/**
 * TestCaseAddPanel constructor comment.
 */
public TestCaseAddPanel() {
	super();
	initialize();
}

/**
 * Comment
 */
private void applyTestCaseInfo() throws Exception{
	fieldNewTestCaseArr = null;
	if (getMathModelInfo() == null && getbioModelInfo() == null) {
		throw new Exception("Nothing selected, choose either a MathModel or a BioModel/App combination before setting TestCaseInfo");
//		return;
	}
	if (getMathModelInfo() != null && getbioModelInfo() != null) {
		throw new Exception("must choose either a MathModel or a BioModel/App combination (BUT NOT BOTH)");
//		return;
	}
	
	// testcaseinfo TYPE
	String type = null;
	if (getExactRadioButton().isSelected()) {
		type = TestCaseNew.EXACT;
	} else if (getIvjExactRadioButtonSteady().isSelected()) {
		type = TestCaseNew.EXACT_STEADY;
	}  else if (getConstructedRadioButton().isSelected()) {
		type = TestCaseNew.CONSTRUCTED;
	} else if (getRegressionRadioButton().isSelected()) {
		type = TestCaseNew.REGRESSION;
	} else {
		throw new Exception("Solution Type Not Chosen");
	}
	String annotation = null;
	annotation = getAnnotationTextArea().getText();

	if (getMathModelInfo() != null) {
		//
		// math model selected
		//
		TestCaseNewMathModel mathTestCase = new TestCaseNewMathModel(null, getMathModelInfo(), type, annotation, null);
		setNewTestCaseArr(new TestCaseNewMathModel[] {mathTestCase});
	} else {
		//
		// bio model selected
		//
		if (getappNameArr()==null){
			throw new Exception("no application(s) selected");
		}
		if (!type.equals(TestCaseNew.REGRESSION)){
			throw new Exception("BioModel/App test case must be of type 'REGRESSION'");
		}

		org.vcell.util.document.KeyValue[] appKeyArr = null;
		try {
			for (int i = 0; i < getappNameArr().length; i++) {
				if(appKeyArr == null){
					appKeyArr = new KeyValue[getappNameArr().length];
				}
				appKeyArr[i] = getTestingFrameworkWindowManager().getSimContextKey(getbioModelInfo(),getappNameArr()[i]);				
			}
		}catch (org.vcell.util.DataAccessException e){
			e.printStackTrace(System.out);
			throw new Exception("Exception while retrieving BioModel SimContextKey");
		}
		if (appKeyArr==null){
			throw new Exception("BioModel/App test case cannot be created, selected application(s), not found in BioModel");
		}
		TestCaseNewBioModel[] bioTestCaseArr = new TestCaseNewBioModel[appKeyArr.length];
		for (int i = 0; i < bioTestCaseArr.length; i++) {
			bioTestCaseArr[i] = new TestCaseNewBioModel(null, getbioModelInfo(), getappNameArr()[i], appKeyArr[i], type, annotation, null);
		}
		setNewTestCaseArr(bioTestCaseArr);
	}	
}


/**
 * Comment
 */
private void button_stateChanged(java.awt.event.ItemEvent itemEvent) {
	if(itemEvent.getStateChange() != java.awt.event.ItemEvent.SELECTED){
		return;
	}
	
	if(itemEvent.getSource() == getExactRadioButton()){
		setSolutionType(TestCaseNew.EXACT);
	}else if(itemEvent.getSource() == getConstructedRadioButton()){
		setSolutionType(TestCaseNew.CONSTRUCTED);
	}else if(itemEvent.getSource() == getRegressionRadioButton()){
		setSolutionType(TestCaseNew.REGRESSION);
	}
}


/**
 * connEtoC1:  (TestCaseAddPanel.initialize() --> TestCaseAddPanel.testCaseAddPanel_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.testCaseAddPanel_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (ExactRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> TestCaseAddPanel.button_stateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.button_stateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (ConstructedRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> TestCaseAddPanel.button_stateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.button_stateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (RegressionRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> TestCaseAddPanel.button_stateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.button_stateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (SelectMModelButton.action.actionPerformed(java.awt.event.ActionEvent) --> TestCaseAddPanel.selectMathModel(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.selectMathModel(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (SelectBMAppButton.action.actionPerformed(java.awt.event.ActionEvent) --> TestCaseAddPanel.selectBMApp()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
 * connEtoC8:  (bioModelInfo.this --> TestCaseAddPanel.refreshBioModelAppLabel()V)
 * @param value cbit.vcell.biomodel.BioModelInfo
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(org.vcell.util.document.BioModelInfo value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshBioModelAppLabel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (appName.this --> TestCaseAddPanel.refreshBioModelAppLabel()V)
 * @param value java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.lang.String[] value) {
	try {
		// user code begin {1}
		// user code end
		this.refreshBioModelAppLabel();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the AnnotationLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAnnotationLabel() {
	if (ivjAnnotationLabel == null) {
		try {
			ivjAnnotationLabel = new javax.swing.JLabel();
			ivjAnnotationLabel.setName("AnnotationLabel");
			ivjAnnotationLabel.setText("Annotation:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnnotationLabel;
}


/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getAnnotationTextArea() {
	if (ivjAnnotationTextArea == null) {
		try {
			ivjAnnotationTextArea = new javax.swing.JTextArea();
			ivjAnnotationTextArea.setName("AnnotationTextArea");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnnotationTextArea;
}

/**
 * Return the appName property value.
 * @return java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private String[] getappNameArr() {
	// user code begin {1}
	// user code end
	return ivjappNameArr;
}


/**
 * Return the BioModelAppLabel property value.
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
 * Return the buttonGroup1 property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getbuttonGroup1() {
	if (ivjbuttonGroup1 == null) {
		try {
			ivjbuttonGroup1 = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbuttonGroup1;
}


/**
 * Return the ConstructedRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getConstructedRadioButton() {
	if (ivjConstructedRadioButton == null) {
		try {
			ivjConstructedRadioButton = new javax.swing.JRadioButton();
			ivjConstructedRadioButton.setName("ConstructedRadioButton");
			ivjConstructedRadioButton.setText("Constructed     ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConstructedRadioButton;
}


/**
 * Return the ExactRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getExactRadioButton() {
	if (ivjExactRadioButton == null) {
		try {
			ivjExactRadioButton = new javax.swing.JRadioButton();
			ivjExactRadioButton.setName("ExactRadioButton");
			ivjExactRadioButton.setText("Exact             ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExactRadioButton;
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

			java.awt.GridBagConstraints constraintsSelectMModelButton = new java.awt.GridBagConstraints();
			constraintsSelectMModelButton.gridx = 0; constraintsSelectMModelButton.gridy = 0;
			constraintsSelectMModelButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSelectMModelButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSelectMModelButton(), constraintsSelectMModelButton);

			java.awt.GridBagConstraints constraintsMathmodelLabel = new java.awt.GridBagConstraints();
			constraintsMathmodelLabel.gridx = 1; constraintsMathmodelLabel.gridy = 0;
			constraintsMathmodelLabel.gridwidth = 3;
			constraintsMathmodelLabel.ipadx = 300;
			constraintsMathmodelLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getMathmodelLabel(), constraintsMathmodelLabel);

			java.awt.GridBagConstraints constraintsSelectBMAppButton = new java.awt.GridBagConstraints();
			constraintsSelectBMAppButton.gridx = 0; constraintsSelectBMAppButton.gridy = 1;
			constraintsSelectBMAppButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSelectBMAppButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getSelectBMAppButton(), constraintsSelectBMAppButton);

			java.awt.GridBagConstraints constraintsBioModelAppLabel = new java.awt.GridBagConstraints();
			constraintsBioModelAppLabel.gridx = 3; constraintsBioModelAppLabel.gridy = 1;
			constraintsBioModelAppLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBioModelAppLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getBioModelAppLabel(), constraintsBioModelAppLabel);
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 0;
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsExactRadioButton = new java.awt.GridBagConstraints();
			constraintsExactRadioButton.gridx = 1; constraintsExactRadioButton.gridy = 0;
			constraintsExactRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getExactRadioButton(), constraintsExactRadioButton);

			java.awt.GridBagConstraints constraintsSolutionTypeLabel = new java.awt.GridBagConstraints();
			constraintsSolutionTypeLabel.gridx = 0; constraintsSolutionTypeLabel.gridy = 0;
			constraintsSolutionTypeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getSolutionTypeLabel(), constraintsSolutionTypeLabel);

			java.awt.GridBagConstraints constraintsAnnotationLabel = new java.awt.GridBagConstraints();
			constraintsAnnotationLabel.gridx = 0; constraintsAnnotationLabel.gridy = 1;
			constraintsAnnotationLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getAnnotationLabel(), constraintsAnnotationLabel);

			java.awt.GridBagConstraints constraintsConstructedRadioButton = new java.awt.GridBagConstraints();
			constraintsConstructedRadioButton.gridx = 3;
 	constraintsConstructedRadioButton.gridy = 0;
			constraintsConstructedRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			java.awt.GridBagConstraints constraintsRegressionRadioButton = new java.awt.GridBagConstraints();
			constraintsRegressionRadioButton.gridx = 4;
 	constraintsRegressionRadioButton.gridy = 0;
			constraintsRegressionRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanel2.add(getConstructedRadioButton(), constraintsConstructedRadioButton);
			java.awt.GridBagConstraints constraintsAnnotationTextArea = new java.awt.GridBagConstraints();
			constraintsAnnotationTextArea.gridx = 1; constraintsAnnotationTextArea.gridy = 1;
			constraintsAnnotationTextArea.gridwidth = 4;
constraintsAnnotationTextArea.gridheight = 2;
			constraintsAnnotationTextArea.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAnnotationTextArea.weightx = 1.0;
			constraintsAnnotationTextArea.weighty = 1.0;
			constraintsAnnotationTextArea.insets = new java.awt.Insets(4, 4, 4, 4);
			ivjJPanel2.add(getRegressionRadioButton(), constraintsRegressionRadioButton);
			ivjJPanel2.add(getAnnotationTextArea(), constraintsAnnotationTextArea);
			ivjJPanel2.add(getIvjExactRadioButtonSteady(), gridBagConstraints);
		}catch(Exception e){
			handleException(e);
		}
	}
	return ivjJPanel2;
}

/**
 * Gets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The mathModelInfo property value.
 * @see #setMathModelInfo
 */
private MathModelInfo getMathModelInfo() {
	return fieldMathModelInfo;
}


/**
 * Return the MathmodelLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMathmodelLabel() {
	if (ivjMathmodelLabel == null) {
		try {
			ivjMathmodelLabel = new javax.swing.JLabel();
			ivjMathmodelLabel.setName("MathmodelLabel");
			ivjMathmodelLabel.setText("...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathmodelLabel;
}


/**
 * Gets the newTestCase property (cbit.vcell.numericstest.TestCaseNew) value.
 * @return The newTestCase property value.
 * @see #setNewTestCase
 */
public TestCaseNew[] getNewTestCaseArr() throws Exception{
	applyTestCaseInfo();
	return fieldNewTestCaseArr;
}


/**
 * Return the RegressionRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getRegressionRadioButton() {
	if (ivjRegressionRadioButton == null) {
		try {
			ivjRegressionRadioButton = new javax.swing.JRadioButton();
			ivjRegressionRadioButton.setName("RegressionRadioButton");
			ivjRegressionRadioButton.setText("Regression Test");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRegressionRadioButton;
}


/**
 * Return the SelectBMAppButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSelectBMAppButton() {
	if (ivjSelectBMAppButton == null) {
		try {
			ivjSelectBMAppButton = new javax.swing.JButton();
			ivjSelectBMAppButton.setName("SelectBMAppButton");
			ivjSelectBMAppButton.setText("Select BioModel/App");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectBMAppButton;
}


/**
 * Return the SelectMModelButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getSelectMModelButton() {
	if (ivjSelectMModelButton == null) {
		try {
			ivjSelectMModelButton = new javax.swing.JButton();
			ivjSelectMModelButton.setName("SelectMModelButton");
			ivjSelectMModelButton.setText("Select MathModel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectMModelButton;
}


/**
 * Insert the method's description here.
 * Creation date: (7/22/2004 9:28:12 AM)
 * @return java.lang.String
 */
private java.lang.String getSolutionType() {
	return solutionType;
}


/**
 * Return the SolutionTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSolutionTypeLabel() {
	if (ivjSolutionTypeLabel == null) {
		try {
			ivjSolutionTypeLabel = new javax.swing.JLabel();
			ivjSolutionTypeLabel.setName("SolutionTypeLabel");
			ivjSolutionTypeLabel.setText("Solution Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolutionTypeLabel;
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
	getExactRadioButton().addItemListener(ivjEventHandler);
	getConstructedRadioButton().addItemListener(ivjEventHandler);
	getRegressionRadioButton().addItemListener(ivjEventHandler);
	getSelectMModelButton().addActionListener(ivjEventHandler);
	getSelectBMAppButton().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TestCaseAddPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(563, 291);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 1;
constraintsJPanel2.gridheight = 3;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);

 		initConnections();
		connEtoC1();
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
		TestCaseAddPanel aTestCaseAddPanel;
		aTestCaseAddPanel = new TestCaseAddPanel();
		frame.setContentPane(aTestCaseAddPanel);
		frame.setSize(aTestCaseAddPanel.getSize());
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
 * Comment
 */
private void refreshBioModelAppLabel() {
	if (getbioModelInfo()!=null && getappNameArr()!=null){
		org.vcell.util.document.Version bmVersion = getbioModelInfo().getVersion();
		getBioModelAppLabel().setText(bmVersion.getName()+" "+bmVersion.getDate()+" App="+(getappNameArr().length == 1?getappNameArr()[0]:"Multi-Select"));
	}else{
		getBioModelAppLabel().setText("");
	}
}

private void refreshMathModelAppLabel() {
	if (getMathModelInfo()!=null){
		org.vcell.util.document.Version bmVersion = getMathModelInfo().getVersion();
		getMathmodelLabel().setText(getMathModelInfo().getVersion().getName());
	}else{
		getMathmodelLabel().setText("");
	}
}


/**
 * Comment
 */
public void resetTextFields() {
	// Initialize the Textfields here, so that every time the testCaseInfoPanel is invoked, the fields are not filled in
	getMathmodelLabel().setText("");
	if (getExactRadioButton().isSelected()) {
		getExactRadioButton().setSelected(false);
	} else if (getIvjExactRadioButtonSteady().isSelected()) {
		getIvjExactRadioButtonSteady().setSelected(false);
	} else if (getConstructedRadioButton().isSelected()) {
		getConstructedRadioButton().setSelected(false);
	} else if (getRegressionRadioButton().isSelected()) {
		getRegressionRadioButton().setSelected(false);
	}
	getAnnotationTextArea().setText("");
}


/**
 * Comment
 */
private void selectBMApp() {
	org.vcell.util.document.BioModelInfo bmInfo = getTestingFrameworkWindowManager().selectBioModelInfo();
	if (bmInfo != null) {
		getBioModelAppLabel().setText(bmInfo.getVersion().getName());
		String simContextNames[] = bmInfo.getBioModelChildSummary().getSimulationContextNames();
		Object[][] rowData = new String[simContextNames.length][1];
		for (int i = 0; i < rowData.length; i++) {
			rowData[i][0] = simContextNames[i];
		}
//		String selection = (String)cbit.vcell.client.PopupGenerator.showListDialog(this,simContextNames,"choose application for testCase");
		try{
			int[] selectionIndexes = DialogUtils.showComponentOKCancelTableList(this, "choose one or more applications for testCase",
					new String[] {"Applications"}, 
					rowData,
					ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			if (selectionIndexes!=null && selectionIndexes.length > 0){
				setbioModelInfo(bmInfo);
				String[] selectionArr = new String[selectionIndexes.length];
				for (int i = 0; i < selectionIndexes.length; i++) {
					selectionArr[i] = simContextNames[selectionIndexes[i]];
				}
				setappNameArr(selectionArr);
				setMathModelInfo(null);
			}
		}catch(UserCancelException e){
			//ignore
		}
	} // else {
	  // 	PopupGenerator.showErrorDialog("Selected Mathmodel is null!");
	// }
}


/**
 * Comment
 */
private void selectMathModel(java.awt.event.ActionEvent actionEvent) {
	org.vcell.util.document.MathModelInfo mmInfo = getTestingFrameworkWindowManager().selectMathModelInfo();
	if (mmInfo != null) {
		getMathmodelLabel().setText(mmInfo.getVersion().getName());
		setMathModelInfo(mmInfo);
		setbioModelInfo(null);
		setappNameArr(null);
	} // else {
	  // 	PopupGenerator.showErrorDialog("Selected Mathmodel is null!");
	// }
}


/**
 * Set the appName to a new value.
 * @param newValue java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setappNameArr(java.lang.String[] newValue) {
	if (ivjappNameArr != newValue) {
		try {
			ivjappNameArr = newValue;
			connEtoC9(ivjappNameArr);
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
private void setbioModelInfo(org.vcell.util.document.BioModelInfo newValue) {
	if (ivjbioModelInfo != newValue) {
		try {
			ivjbioModelInfo = newValue;
			if(newValue != null){
				getRegressionRadioButton().setSelected(true);
			}
			connEtoC8(ivjbioModelInfo);
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
 * Sets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @param mathModelInfo The new value for the property.
 * @see #getMathModelInfo
 */
private void setMathModelInfo(org.vcell.util.document.MathModelInfo mathModelInfo) {
	org.vcell.util.document.MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
	refreshMathModelAppLabel();
}


/**
 * Sets the newTestCase property (cbit.vcell.numericstest.TestCaseNew) value.
 * @param newTestCase The new value for the property.
 * @see #getNewTestCase
 */
private void setNewTestCaseArr(cbit.vcell.numericstest.TestCaseNew[] newTestCaseArr) {
//	cbit.vcell.numericstest.TestCaseNew oldValue = fieldNewTestCase;
	fieldNewTestCaseArr = newTestCaseArr;
//	firePropertyChange("newTestCase", oldValue, newTestCase);
}


/**
 * Insert the method's description here.
 * Creation date: (7/22/2004 9:28:12 AM)
 * @param newSolutionType java.lang.String
 */
private void setSolutionType(java.lang.String newSolutionType) {
	solutionType = newSolutionType;
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
 * Comment
 */
private void testCaseAddPanel_Initialize() {
	//cbit.util.BeanUtils.enableComponents(this,false);
	getbuttonGroup1().add(getExactRadioButton());
	getbuttonGroup1().add(getIvjExactRadioButtonSteady());
	getbuttonGroup1().add(getConstructedRadioButton());
	getbuttonGroup1().add(getRegressionRadioButton());
}	

/**
 * This method initializes ivjExactRadioButtonSteady	
 * 	
 * @return javax.swing.JRadioButton	
 */
private JRadioButton getIvjExactRadioButtonSteady() {
	if (ivjExactRadioButtonSteady == null) {
		ivjExactRadioButtonSteady = new JRadioButton();
		ivjExactRadioButtonSteady.setName("ExactRadioButton");
		ivjExactRadioButtonSteady.setText("Exact Steady");
	}
	return ivjExactRadioButtonSteady;
}
}  //  @jve:decl-index=0:visual-constraint="10,10"
