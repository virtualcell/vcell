package cbit.vcell.client.desktop.testingframework;
import cbit.util.document.KeyValue;
import cbit.util.document.Version;
import cbit.vcell.numericstest.AddTestCasesOP;
import cbit.vcell.numericstest.TestCaseNewMathModel;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.client.PopupGenerator;
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
	private javax.swing.JPanel ivjJPanel3 = null;
	private javax.swing.JLabel ivjAnnotationLabel = null;
	private javax.swing.JButton ivjApplyButton = null;
	private javax.swing.ButtonGroup ivjbuttonGroup1 = null;
	private javax.swing.JRadioButton ivjConstructedRadioButton = null;
	private javax.swing.JRadioButton ivjExactRadioButton = null;
	private javax.swing.JLabel ivjMathmodelLabel = null;
	private javax.swing.JRadioButton ivjRegressionRadioButton = null;
	private javax.swing.JButton ivjSelectMModelButton = null;
	private javax.swing.JLabel ivjSolutionTypeLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextArea ivjAnnotationTextArea = null;
	private cbit.vcell.numericstest.TestCaseNew fieldNewTestCase = null;
	private cbit.util.document.MathModelInfo fieldMathModelInfo = null;
	private javax.swing.JLabel ivjBioModelAppLabel = null;
	private javax.swing.JButton ivjSelectBMAppButton = null;
	private String ivjappName = null;
	private cbit.util.document.BioModelInfo ivjbioModelInfo = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TestCaseAddPanel.this.getSelectMModelButton()) 
				connEtoC5(e);
			if (e.getSource() == TestCaseAddPanel.this.getApplyButton()) 
				connEtoC6(e);
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
public void applyTestCaseInfo(java.awt.event.ActionEvent actionEvent) {
	if (getMathModelInfo() == null && getbioModelInfo() == null) {
		PopupGenerator.showErrorDialog("Nothing selected, choose either a MathModel or a BioModel/App combination before setting TestCaseInfo");
		return;
	}
	if (getMathModelInfo() != null && getbioModelInfo() != null) {
		PopupGenerator.showErrorDialog("must choose either a MathModel or a BioModel/App combination (BUT NOT BOTH)");
		return;
	}
	
	//
	// math model selected
	//
	if (getMathModelInfo() != null) {
		String type = null;
		String annotation = null;
		// testcaseinfo TYPE
		if (getExactRadioButton().isSelected()) {
			type = TestCaseNew.EXACT;
		} else if (getConstructedRadioButton().isSelected()) {
			type = TestCaseNew.CONSTRUCTED;
		} else if (getRegressionRadioButton().isSelected()) {
			type = TestCaseNew.REGRESSION;
		} else {
			PopupGenerator.showErrorDialog("Solution Type Not Chosen");
			return;
		}
		annotation = getAnnotationTextArea().getText();

		TestCaseNewMathModel mathTestCase = new TestCaseNewMathModel(null, getMathModelInfo(), type, annotation, null);
		setNewTestCase(mathTestCase);
	//
	// bio model selected
	//
	} else {
		if (getappName()==null){
			PopupGenerator.showErrorDialog("no application selected");
			return;
		}
		String type = null;
		String annotation = null;
		// testcaseinfo TYPE
		if (getExactRadioButton().isSelected()) {
			type = TestCaseNew.EXACT;
		} else if (getConstructedRadioButton().isSelected()) {
			type = TestCaseNew.CONSTRUCTED;
		} else if (getRegressionRadioButton().isSelected()) {
			type = TestCaseNew.REGRESSION;
		} else {
			PopupGenerator.showErrorDialog("Solution Type Not Chosen");
			return;
		}
		if (!type.equals(TestCaseNew.REGRESSION)){
			PopupGenerator.showErrorDialog("BioModel/App test case must be of type 'REGRESSION'");
			return;
		}
		annotation = getAnnotationTextArea().getText();

		KeyValue appKey = null;
		try {
			appKey = getTestingFrameworkWindowManager().getSimContextKey(getbioModelInfo(),getappName());
		}catch (cbit.util.DataAccessException e){
			e.printStackTrace(System.out);
			PopupGenerator.showErrorDialog("Exception while retrieving BioModel");
			return;
		}
		if (appKey==null){
			PopupGenerator.showErrorDialog("BioModel/App test case cannot be created, selected application='"+getappName()+"' not found in BioModel");
			return;
		}
		TestCaseNewBioModel bioTestCase = new TestCaseNewBioModel(null, getbioModelInfo(), getappName(), appKey, type, annotation, null);
		setNewTestCase(bioTestCase);
		
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
 * connEtoC6:  (ApplyButton.action.actionPerformed(java.awt.event.ActionEvent) --> TestCaseAddPanel.applyTestCaseInfo(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.applyTestCaseInfo(arg1);
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
private void connEtoC8(cbit.util.document.BioModelInfo value) {
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
private void connEtoC9(java.lang.String value) {
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
 * Return the ApplyButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getApplyButton() {
	if (ivjApplyButton == null) {
		try {
			ivjApplyButton = new javax.swing.JButton();
			ivjApplyButton.setName("ApplyButton");
			ivjApplyButton.setText("Apply");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjApplyButton;
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
private cbit.util.document.BioModelInfo getbioModelInfo() {
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
			constraintsConstructedRadioButton.gridx = 2; constraintsConstructedRadioButton.gridy = 0;
			constraintsConstructedRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getConstructedRadioButton(), constraintsConstructedRadioButton);

			java.awt.GridBagConstraints constraintsRegressionRadioButton = new java.awt.GridBagConstraints();
			constraintsRegressionRadioButton.gridx = 3; constraintsRegressionRadioButton.gridy = 0;
			constraintsRegressionRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getRegressionRadioButton(), constraintsRegressionRadioButton);

			java.awt.GridBagConstraints constraintsAnnotationTextArea = new java.awt.GridBagConstraints();
			constraintsAnnotationTextArea.gridx = 1; constraintsAnnotationTextArea.gridy = 1;
			constraintsAnnotationTextArea.gridwidth = 3;
constraintsAnnotationTextArea.gridheight = 2;
			constraintsAnnotationTextArea.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAnnotationTextArea.weightx = 1.0;
			constraintsAnnotationTextArea.weighty = 1.0;
			constraintsAnnotationTextArea.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getAnnotationTextArea(), constraintsAnnotationTextArea);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}

/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.FlowLayout());
			getJPanel3().add(getApplyButton(), getApplyButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}

/**
 * Gets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The mathModelInfo property value.
 * @see #setMathModelInfo
 */
private cbit.util.document.MathModelInfo getMathModelInfo() {
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
public cbit.vcell.numericstest.TestCaseNew getNewTestCase() {
	return fieldNewTestCase;
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
	getApplyButton().addActionListener(ivjEventHandler);
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
		setSize(492, 291);

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

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 4;
		constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel3.weightx = 1.0;
		constraintsJPanel3.weighty = 1.0;
		constraintsJPanel3.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel3(), constraintsJPanel3);
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
	if (getbioModelInfo()!=null && getappName()!=null){
		Version bmVersion = getbioModelInfo().getVersion();
		getBioModelAppLabel().setText(bmVersion.getName()+" "+bmVersion.getDate()+" App="+getappName());
	}else{
		getBioModelAppLabel().setText("");
	}
	return;
}


/**
 * Comment
 */
public void resetTextFields() {
	// Initialize the Textfields here, so that every time the testCaseInfoPanel is invoked, the fields are not filled in
	getMathmodelLabel().setText("");
	if (getExactRadioButton().isSelected()) {
		getExactRadioButton().setSelected(false);
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
	cbit.util.document.BioModelInfo bmInfo = getTestingFrameworkWindowManager().selectBioModelInfo();
	if (bmInfo != null) {
		getBioModelAppLabel().setText(bmInfo.getVersion().getName());
		String simContextNames[] = bmInfo.getBioModelChildSummary().getSimulationContextNames();
		String selection = (String)cbit.vcell.client.PopupGenerator.showListDialog(this,simContextNames,"choose application for testCase");
		if (selection!=null){
			setbioModelInfo(bmInfo);
			setappName(selection);
			setMathModelInfo(null);
		}
	} // else {
	  // 	PopupGenerator.showErrorDialog("Selected Mathmodel is null!");
	// }
}


/**
 * Comment
 */
private void selectMathModel(java.awt.event.ActionEvent actionEvent) {
	cbit.util.document.MathModelInfo mmInfo = getTestingFrameworkWindowManager().selectMathModelInfo();
	if (mmInfo != null) {
		getMathmodelLabel().setText(mmInfo.getVersion().getName());
		setMathModelInfo(mmInfo);
		setbioModelInfo(null);
		setappName(null);
	} // else {
	  // 	PopupGenerator.showErrorDialog("Selected Mathmodel is null!");
	// }
}


/**
 * Set the appName to a new value.
 * @param newValue java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void setappName(java.lang.String newValue) {
	if (ivjappName != newValue) {
		try {
			ivjappName = newValue;
			connEtoC9(ivjappName);
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
private void setbioModelInfo(cbit.util.document.BioModelInfo newValue) {
	if (ivjbioModelInfo != newValue) {
		try {
			ivjbioModelInfo = newValue;
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
private void setMathModelInfo(cbit.util.document.MathModelInfo mathModelInfo) {
	cbit.util.document.MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
}


/**
 * Sets the newTestCase property (cbit.vcell.numericstest.TestCaseNew) value.
 * @param newTestCase The new value for the property.
 * @see #getNewTestCase
 */
public void setNewTestCase(cbit.vcell.numericstest.TestCaseNew newTestCase) {
	cbit.vcell.numericstest.TestCaseNew oldValue = fieldNewTestCase;
	fieldNewTestCase = newTestCase;
	firePropertyChange("newTestCase", oldValue, newTestCase);
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
	getbuttonGroup1().add(getConstructedRadioButton());
	getbuttonGroup1().add(getRegressionRadioButton());
}	


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D45515C0230D12CA6427848D92A1C8D45000A8E91B2F45AE5D26B55638DF2B2CC69737F42BEB5C4D77095BACF56B4BA4A1E18392A1DA54A891A2858CA1614F90D18984C8E044E012B154A8F9B37312CCB21999E75E10047033677E1C7B5E4C3C49DF355F667B8E6F3DF34F5FBD775CFB4F7D992462ADF1F95926E2C94A4E13785F7B591264B9A3C903AF791EC4DC344BD2A8697E5E81F8C5EA4B
	4E05E79C146DC8369496C9F774944077E278AE7F2125704F703EC93A3B47F99ADE426829033201577BB68F2D47F4827564536F6EDD6B6079B2C0926048B357CD74CF5835196353B99EA15BA4C92BA84D4CDDA59CD78F5F6DG09G4B5C0CBE9D1EEB2966EBEA2AB9DD5B0D89522ADFFACD6B099C1DCC04439E465B4E1F25526A6C022F092EFF24F6921E98A8CB83A0154F114EFFDDCB789AFBBABF32D99DEA45851B62F4D6581C8E4525D65895DF0F6A76D4280ACFF538BABB3CF22F526776765454B48366186CD3
	EA6D76C65925B8C1C684557372FB85D42EA41949371760365B096E9478DE8110A570059D845F8C6FCE0088AF5677433F9C6675E99DDCA22D6C7C595A8DE16A9B6BE575DC66556ADB3A4B1A73BAED1B23A7E9E37AAC05E5G55GEDG17GE2815E24B151F0E297BCEB2D6AB1F7EF2F5BD56B719CF55A3D1F386D0A935EEBEA200463BED3ECEE2FDD12D83B276EAAB730E7A8E0FC0F0D1A0F79A4E19F46407C4F6E12526F793EA95F40A76989032645BE660BD41F669326FB53FB61F4E77882E3B4D0776712DE3774
	4C5E7CBC835DA50F6C4D17D4A627D855F4B72A242DEF22B1309F5E3339DC061FC771D3F5F8962F6594CF3E93212C580F31517AB25AE24E1DAD95D5CDB774C34CCF23CC7DFEE6C334BF5816F9B46E127DCC67A6E14B8FA8BEC307E7363CC271643B640225B089004557698652FF1B7BAC059E00C800B80025G69G19FD98E313CF6C99E10CB54ADE75285F6134ABDE09711EBE799ABC15FE55ABB738FC2A6C32A996596B102DCE45A7F11A6613448756G3EF3B476ED20632462D23C326AF03BC0BAF4E0D5717A98
	0D7564AB04462B5052E6C7AFF4EA3957A3215C6E13244ECE592736F86C322AE8942C5CFB12344985598292C1G3CB33F741FA27EAA6A073198203A9F7D709C15F7DA714200D1D3E3F63BFB7C1EE6D293A9321F78F9979DBB4C70EE6FC79F2B2789AE953ED78AB9E5759BF5F26A9CB6E23E6C9D40BE96E7AF02E79650978F40788FD0B70071739F2D98BFF56B97C8653F19E698BF51904B2D83ACEEBC83417DE88115CFEC0C0132A4006583B89F6C35378E51170B39ACE29331EFB463C01D50F1FAC89DF912B65E64
	0CC2BE76F76677674C248D33C0E771C5CB617E0B687B737525AC160F12511FC76F27DE88BC452E358477D3C79977ED8E37AB1F6BA093AB1943B6D0BAEC371FD0FAB368D281E2811681E4G948FDA8A8F8358812286518FFF6E089E217C517860EF85E65BC75436616C5A74F7360D35E1CB83512FDC00A93ECE71A8AEBB3C78F8D9DB8331130FB79DEE41E325F88F0CCF47E0C07089FCD78329072C2ADE07552FAACD0ACAB2852D5CC3F5797CD60FB634212E3E86629BF79F0CCF027ED283695BB6374B2550E092AC
	3C7C8A9DDB9427528BC69FF37BDDAA43C71CA2F9D90FB240473648D3EBA4B2B6B97DE41CE439CE22CC7C6F71BAFAA1C449D0AB65DEA263CE6DFB1BF5BEDA8C71C5F23665704C9844F123EF9B0E9BE597EF17161E77180C6640183913CC66CBBC873B240D9B4C2601534636DF25E39C477B182D0FD613B1A6B84D4A2174777792B960EE556DBA69F57BBDD58C7F439C0AF7386954C21A0F61A357117813BD1EB3B07C737E3D7D8C219D87B6CE8518FF897B779AB3A9473115F76CE3EE2713B58714E5D21FBDEFAF56
	55FB59A566B3729EF11914EFD1303C9A3E9BAF8717CF936529703DD2D74E6CF85ECC6A6FD33E76ABB06511C96234E85B55660DC1E5965E4E2C5C7489E9E727EEFE31DFA1BA33BAB05FAE3842741572E77595261B51AD6AC43AC478CE81F81A5315DD5553793B102E9D707DD743516DF4A0DD8C60934352BDA16852888DC041B8C6D7BFCECFF7EBB752C5GBEF61CC6179CC0575203F4D900AF9AEFA92C9C4F68BC6375F42FBB11AEEABC1B4F62B95D62G3A0785DD7AF8B66785533158FDFEA769CFF4CD51ADDF10
	7BABFCFDB0A654541C22B14C5689F752391E502F9A0F6B01C7ABC8CC16CF00799A20ED82763D075763FAE0EACF02147ECCD5FE38750FBA015991B3C16BFB651935EBD84CFBBEA5BA17C3595393B05EAFFF224FC9EAFDBE374DC153A19ECB65E774FD13612A4E7CAFBCBB9CDE1F5AG03E7BF465D7BE7083DA4DF62E86E0F7F29F84B509FC7359EACE12EFD79043E2E499BAE1B8A435475C5DE47126FE8F5DD5E5B1C4FF256938A2E775A20FC6AF7F03DF7EC5DF6A9F43D87E3C6AF99B340A65D6801B6597621CD51
	9767CA6D53EE8AEBD3AA37A5C3E7D379152CBCE6D3323029AC825AB782ED7A4D3957747E29F04A44BBCDB02538BAB187FF27F39D7767D460CB8560E39B45BBDC8E55A1BB9D97159DBB194CAF1437C9BC840D1F679BF404962C5F6CCDC7507E3BE6CB072FFEE5B2DAEFC6EE68B265DE437337EB347A55CF36663481B0BB9F97F67683CDA2G9BAFF3F66B474B2781DFFCAD0CBB57E2BD0EF611B502C3D5FAAB48BFCDAA645B473A40910AFD87F50A5C27D650A93B22814A0F13371DBC3E1426CA89670373BEBD27B9
	9C6B87BBAD4C5EDB3A50DEB35892F1AD5A9B55EEE4EFB2G637B4C01FCE98477DD6413EDC6FCC73E0BFCEB3A112F99F0D102AFB9BBE0BE81FC86C0D1A47A2737FBAFBCE5BA89D530C723626DF0FBFBC39DD4CB0B390B380F161E2B10481CC16631CFE4350B86F908FFF41C9F1078E0B67FD258DC867634C5224DC7EC41B6CF053224C80C6B67B612F601F957B90093B969D643E8E5BA2F74204ED41017FB9D6A0CE9B27253616B50CF3F6B795B7C34EF674B42CFC7BF0184FF07660723CE34299174C58A1D37BBC9
	6E25F6B9F86ED239758DF8FA158E2F626B428185C419E5AB78DC1337DA48DB8A32322E433CBEB1E41F2272FAED2DD4F7FD683A092D5FCF6FA172DA2FE76B77FC017F1C62BDBABC537F841DEF48F7A414E5DC0FEB4EA9E7497C55B851D2688408G0885C886C81F087346CF4E6C1306D953B738E0296D121DCD8149AF1FAB7C1F925FC13E4A3F87BEA5FE20792A04EB30EC5A56D92033B88A5631GF5G76A834C1EA9C5606637D06B6B0BF1B15316DE17DF554FC2CBEDFB5137AC4007DF1GC9GE9G1922BE279A
	4B072B8F6EFD700AB0FBFEBA46FA240D116FAF1D41FCC55F8B0C497220EF46F7A0042FA2B80E0378185F9637103C2B8B4AA2GE28112G528132GCAEEC07FCDEF7CF058F8A01D3D59FD8CF075109F9F8BDEE789DB1F9AE39D7FB3046F6D206F43B7847231B1633523044EA0E77CCC36BB5CACD31278FAED657E60759A590BE98639DDGAC3F2C590D7965389BF0DC096F9ADB1D9605708557A9A928CE4C06FFE8C79B52048D2D0E315950914297ECC341A4A39BE2ECE8C369A434E1DD7758ECF8A304AF580656C9
	C6ED3B2169F5633645E6656D9A3D4728DDBDA0B3E292366B7359D81FD8D11F34B1566749E16B736B207AB0BB8BECC6F6E6G2D940DF676893FEF0EC6BBA3FA46E66764903E10F1A35A286D9FEAC29B3A048D754E3159E089618B36A1AE5A08CF6A9D1B3E17C3780275A58569E3FB48BF276B4AB4A8DB8940F071F4ACA83891567BB7229F4E521C208E3EDB811C827FA545131C0A642DA41FE3785FD68C3567CA6408BE405F1DB70666A9610069F8F1C89D7A3DDC7D9E6FC8F530B9A276EC783D3FB9E70D777EEEBF
	EB3C77F77759D7989EE468CFAE30FC4959217786FFF2B67C5E605A2670F696B5995B79DA13311DA5CDC35BD956B4341D9D4D61ED69EDB6364557ECEC4BC07350360CEF8EE74BDBB486FE715A864A6F73A9BEBA1957492A4C4BFE2F3EAA996C1D52DDBF96BF1B242FBA57F21A632A7BD815507B631670F57CD90BF19D9FE8A17BB0BE375FEBD3026B71CFAD1BC51916E51075A7B10751BCCE79D5595B29047861DFA9BFAB8B477FECCBB8DF313AEE6E5257152C0B5838D8F2C06486B7625E5311F2526F1360BB8D60
	E9B19EFC3C5BA8C714A673F34E4921FD1C69FEBCC077BE5147D35A0D646D60F28E0715B749211737DF481BE9B31257486558434ABB562D17F7C078A6E58F7A26FFB27AA6BB1B78A6913ED3GD6CCC65FE4E59B694E62BA57CD8927BB3FC72F7B2050FDC44BD526206E09D63A2603EF8FC0649454FD49305EF1DC6762303AFF94207BFD61471A96A3F9A9DCCEFAD8F9D71DFAF955827FFE2F9EFFC8601FFC0F1CCBB8658137DF15243629240EAF087307CC50D3G38CA003A29AC2FE0B95442BE3267C6EEE0C008B7
	F9941BD4BD0D707EE8B7724683FD9240F200AC004D53F0FD5E370554116D3ED6F1BBD6FF216176F25C7DFF56F07B38DF5EB9906C17F153B4DFCC1D966A973666CE227DA9E19ADBF32F97781FD3FC2A8E4F77ABA91EFCE7C2D95B77F16E365039BB853E178B5C92B11F2F9438FFE9C73A7D5391F7C4C63AA382375806F45182772A9D69E227E3FCBD38A7385E9953353ACAB1616A6D27FD25B8A6781E43840A2F0C893E67101F0D779C4CD0169803B6EDB0211D45B390271AC49EB3836DBC9E528FBCB3B45BA2E604
	3373A55A2FE2E6B0FBF284FE2D0D361B8E4F6C3C1152136FA4A8AB3989ED1AA27CB9CE60D659504EA801BB5BC2F009703DD4606EEBC13A541BF0FF702DF25CA3AE399956F4B7E37CD73513B53036C7AC7729B5B5A73D8E7BD13953CCFB939BC31472824E9FF5B30EA1D3284CE5701D8E306AE6BCD7685F0E7B5A73EF1BAD257D4E78DCE1C573E5533AD866474AD8ED5FF77FAEEB8E537954EE5469047278D85AFE957AFDBA623713A6164332725A00F2B20FBDA8694BFD8165445EFF4E51174F8DA8A7BE3A067333
	3ABD70AE566D458792A5673D91E16F0CE570BA954D64F57B550C6C481846B52C0D2A69DCD98FE5EDB3315D969D44F69BB79363704765E49CCA036F8C00F537D88AF7G1881BC3708BEC5E948F9DAB46092EFE1E7E664BCACCD50184B5BF53625F1D9436536BBF4B499B30D69BD1C46ACF4F58668A2EDB412FCFAED105F0DE85673E72104637EF6935DA305A4AA7396F22FD0791234D257AC624FDF6D2E63747BE7994FB358978954838D01D7B93EED966B2B84186C19DFE932BF17D1F622016C3CE1E4AF8B113D74
	3CA63B5C0E32538CE4678FA3FB25CEB60B2B15FBB056B2E7E12C3D3E97E32DB88E5BEDB01B5E3F016F8800B8002522AC121611335374B8F6667922A8DB1FBD547ACA8F91DCE6B05E4E759971B09D5BC72CA3AFA816C692072C8D8AE42D8DA61A308DCAEE8D358B7DEB5486C4FF652D41ED50E643B6B8FCAB3641A3E2FEEC3F95FD19E7A3711CA83EB750EFF2964D3E27DBC76A0761A05120DE7C1C609B53E1F4CF0A793A5ACAFC1DF2AB3D2FEA5FBC0C0CCDD2D7CBE0BF7F4226355503AD58D6C5330D5BAAC787E5
	C0C3G5B2AFA3656D62C7E5B4A4736FFDDB5C63E6ED173311CC259CD77DAF476B33C0B629BF5F816834C90B9C09B14AD1D0D67B29FD17F174D01FC8E20954083908DB0FF8E6E015FE0A1E7EEC36E01B73BBDE6D868BAF54BEFD63FFFB3DC6B8D85EC2E3DBF9F675A45BF3CC3CA3A36AE6C797852B92C5DD770E7D26A0CEC72E475FBD8A175AB0A37941683B482F481DC82189A0F75FB44424FC80E5B9DEA2D2A4A36AEFAB3C19C10B814BE8DCD968AACAE7F38A7F45DA19F40F8CE08E76B0E25707CF5BC5B7BE2B1
	7B0B0398339971C347AC7121B497F8673218A5389DF33536E5F69C5FC84667637D32CD556D6C333267BAC99E5464F67A69FD54810FC2CF8C856FEFEA489EC52D4B65D669958DD64A4A6E3DC86E909CF33BFC2A57EFD395FB403981235977AE316BB325532B78480555G92CE735F9FEF8C50518CBD39562B48F86F4935D51FF76D1F4BFC55BA974FE7AFDCB4AAF70A7255323EFC9CAFCF180BB969C3DF686F7A975D64B4217F02FD1FBC1735C74ADCADA7E57968CB626C63C5A85BA2713B4C1276474626D51D1208
	612756E18C175CB9C72AFB344E34AD0C5E7D3771FBDC37E9FA17366FDF4BF4C74A28BB994A199E1F03E8E065DF5A453D9FD17E7DEED24EE2F2E3A546E479BC9613F570EC1D474E39D9CCD66E4518744C9BFE4E0BG1A18F9DACC4E1F979C13A7AAC91BB7313B81F428880C1B6F1DA2F2492D81FAC90687A5ABBBE8FBC57052737220F8DA7E81097760C3F0968B4FF687478A29E352BC0C1554EA237254F9F84610ECC2DF2E18073EFC28C77365B5621CE2F3022530922095401940763519AF1F93B9DCC40231AF57
	6AFC9983B47393B4DFAECD8876E5D1B7997BEA491D876E8996DFA9DF6A57A3256FFDEA389E497A7222A9155B1119204557FC91DFFFEA41BA175DBED4ECA77712F7B62E776C565ED55D7B2C72EFAAFF88BFE7A6FD2BBA581CF0529EFE6F760C5DF86F3659EE3CBF7DD77B50FB61D6FB78BDFB07DDFF1F174DE9D1AE9243ED50D67DG31G09G4BEE47796C8F762D921D4F8C66326376CE050E6832CBC57D0FA9616B7A14B27CEFA57EDDA1F7772CEE884DDEA29F71198AE9AF556D21C806FBC1A1BE72BABA3BD4C6
	4A71FFD4C83CB9158E0AE6B94BF587B0E7D981F55BFC877651078F605EC0639D389F7192250D046FB800A4C13B7A20B8AF3E43A836B3BFB64231F85FC22E90627D4B0EB077AF695843F44D5D0A3A8A6EC47D8BAEA26E481D386F726065B70DE509091171D6D971F7DF7DF7E23E16CAEDE2BE1B5871AEBCADF2B74F48C87A6276E2CB1F82511FDA3BE18CED16BBC55F492D5067E309A0F7591DC36F85B03DFF781A0CE94B01F695406679B0768154813481F8GA2GE2G9281D2G5281B281F29740FC8C30836070
	820C51C3DDBB43598EC83E8945D28F59C1C2155B5FE4575B5F88726C8B06361F71D983787A01A7EA98BE960319159803F1C03FFC8136EBF59546E051DD98039DD504369E3E5B817A6FC2DAF9AF46C074DD230D41A749B04163E1EB8F4AC99132F38EA1CE3A9B5709771B74F52D863C796E1154F5018D6B5A8E74B1F7E33C6E5B9BF057006D931EF03A7B4CBC7565763DE0C31F245D0DBE59C6FFF3D610E8A9AC8528CEC43B5FD6506E4644517AA45BAF0938FC5CCF7AE5A4480887C881D881308AE05DC20C35CD4E
	9DC3449A2B0496EA4C07B9963D8FCBC1567E05C37B10691A6A8ABF165E6C9AFEAC0DF7513B51B08612DC1D1F953B48BA435D5161D34418FD172BC8609AFC0DEE1F03E4564C76C9A1678BA31BD7E70E116F11D17331352A6FA06DAF8B03573017A83EF9E1709AB66F2008CFA8DB3690637D2503FA7DBB9641B833E8A4E3429DDB8D4F79B82F5DC0863F3BF451082F1F53C70565BBE89D4ACFF186FCEC2F684D8A7A3B6BC54C9F8CD7CAF1299C47E222509BBE76B6FA070F3DB73D467378BB5E50F9FC3B57F89EAF77
	6A67F1FE375FCB7AF9A70C692D026E20401D93388F28CC027B5CE1D73B843E165AC570758AD1A8F2B4EAD79F4535F21CD907BB47F1E73D247FC0153B5CDE6435F8491ED207436964B99A25F13820BB89FF1C27B6BABC324EEE3B401D93ED51ED8FED9F37DD5FBE8CB7E0C0F7A5000E4549FFD50E2D9F66D60D0D6F43D17331FE7A981D6F1EDE945CFFFFCB712B968577DFE79576DF699E18376F417E5B31D72FBF857069770C247F7EA5648E9701CC4E1B3BB8DCBFACB8E444371953179B703136DAEE8AED3FA7CC
	216DF70F61B97A707539FF0CFC2BC74D475AE9BA1D1B0FAC8EEE3FB80AB7AF8EEE3F1DF6EC3FD6A8CBDE0C6D37AD604EEF693DB0D75EBB1239F225BF7CF8752CFF7871EA155FF83CDA6D8F9D2FDE729B0FD7F997746395EB53B7AC216D3C45925A4E83641A85FF1F20FBAFBC786D2F014B3ED59D2C4E2F3D9F6A07B75F8F9D576E5F0A6755757762794656EDF83E51ACF03F3808F4718237EB906992856E93AB62247BB08715376BFE13C4278706AF69DF2F61F1FA60BEDAD1253549B33AF7EF55375FBE933FD59D
	4CCF177706364F043D216D73D18F7A3D71BE74712C9E74F11B40EDB9047789668B5C38C3C817F49F767719F64D1737695E778C799B7AEF462F755F2A8ED6679AA5542F9FABFA3F32F5085AC76A4E767C8EED43BD3F02A523BBB32477B216841F89BF5E2F492E9DC459BB42484E99C276E11DECA64FEEC5F975867274E78F240F35810DE70936BF9991E26B1F86B4DB6F6BC15951E1E46378E9E4EBBC701CB369E53FB328491EFEC81C3B9B484E17827D30ECC960D95FF21D5DEC8D5A3D8D5720AB1760DEEF5DA5C9
	14FB87313CA4894BBBAFEB65532CD85E1CB45AB56C75D7B4B93F976B7AA821E741D52DBCC52C7117996AC1DF2551F756CF5C5B74F3720B40D774031164D4FD03FA3EB260299E915FCD81EB9BB370340F08AFA3A007D301A7F2C4FC8F8664F0314013B88C9F6B4757EE8B6D5B9337050E19B78C0652CD998C25DBE88D253B4F9ACA17638C25ABF006522DBC94CA774CA1BD9D7BFF37E6DD4D040F794FC9F92A34119E7D156A7FD429C8F7EE47D19B34E33AD27263C9A0E207AC2562571B2A14C74F99D4E993AF42E3
	16BD0077F9258D62770E8DF42D3462423E1A1C5F4E49B1C9398D529A160BB5C839A421EA1072E83E55A065C0AE56CFB6AEF375607BDA3AA22D797F08D5251771443414EC435428D221F6FC89DFB99595956C5F3F2FE16C5CE767D57DF9E656B5B6437FB70300B3197509F1495A398F5BB7EB226B16E440AFCF46FD32B95964A0A75CFEF2D32F6CF42A32332756E5A7FB4BC787D4C5BB43E8B59179BE246105FC9DEDA2F6F76BF87E8FD0CB878826CD2BC43798GG18CEGGD0CB818294G94G88G88G5D0171
	B426CD2BC43798GG18CEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7198GGGG
**end of data**/
}
}