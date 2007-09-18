package cbit.vcell.client.desktop.testingframework;
import cbit.vcell.numericstest.AddTestCasesOP;
import cbit.vcell.numericstest.TestCaseNewMathModel;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.client.PopupGenerator;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
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
	private cbit.vcell.numericstest.TestCaseNew fieldNewTestCase = null;  //  @jve:decl-index=0:
	private cbit.vcell.mathmodel.MathModelInfo fieldMathModelInfo = null;
	private javax.swing.JLabel ivjBioModelAppLabel = null;
	private javax.swing.JButton ivjSelectBMAppButton = null;
	private String ivjappName = null;
	private cbit.vcell.biomodel.BioModelInfo ivjbioModelInfo = null;  //  @jve:decl-index=0:
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
	fieldNewTestCase = null;
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
		setNewTestCase(mathTestCase);
	} else {
		//
		// bio model selected
		//
		if (getappName()==null){
			throw new Exception("no application selected");
		}
		if (!type.equals(TestCaseNew.REGRESSION)){
			throw new Exception("BioModel/App test case must be of type 'REGRESSION'");
		}

		cbit.sql.KeyValue appKey = null;
		try {
			appKey = getTestingFrameworkWindowManager().getSimContextKey(getbioModelInfo(),getappName());
		}catch (cbit.vcell.server.DataAccessException e){
			e.printStackTrace(System.out);
			throw new Exception("Exception while retrieving BioModel SimContextKey");
		}
		if (appKey==null){
			throw new Exception("BioModel/App test case cannot be created, selected application='"+getappName()+"' not found in BioModel");
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
private void connEtoC8(cbit.vcell.biomodel.BioModelInfo value) {
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
private cbit.vcell.biomodel.BioModelInfo getbioModelInfo() {
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
private cbit.vcell.mathmodel.MathModelInfo getMathModelInfo() {
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
public cbit.vcell.numericstest.TestCaseNew getNewTestCase() throws Exception{
	applyTestCaseInfo();
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
	if (getbioModelInfo()!=null && getappName()!=null){
		cbit.sql.Version bmVersion = getbioModelInfo().getVersion();
		getBioModelAppLabel().setText(bmVersion.getName()+" "+bmVersion.getDate()+" App="+getappName());
	}else{
		getBioModelAppLabel().setText("");
	}
}

private void refreshMathModelAppLabel() {
	if (getMathModelInfo()!=null){
		cbit.sql.Version bmVersion = getMathModelInfo().getVersion();
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
	cbit.vcell.biomodel.BioModelInfo bmInfo = getTestingFrameworkWindowManager().selectBioModelInfo();
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
	cbit.vcell.mathmodel.MathModelInfo mmInfo = getTestingFrameworkWindowManager().selectMathModelInfo();
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
private void setbioModelInfo(cbit.vcell.biomodel.BioModelInfo newValue) {
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
private void setMathModelInfo(cbit.vcell.mathmodel.MathModelInfo mathModelInfo) {
	cbit.vcell.mathmodel.MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
	refreshMathModelAppLabel();
}


/**
 * Sets the newTestCase property (cbit.vcell.numericstest.TestCaseNew) value.
 * @param newTestCase The new value for the property.
 * @see #getNewTestCase
 */
private void setNewTestCase(cbit.vcell.numericstest.TestCaseNew newTestCase) {
//	cbit.vcell.numericstest.TestCaseNew oldValue = fieldNewTestCase;
	fieldNewTestCase = newTestCase;
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GDAFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD4D45719EECADA4E9E53F533A4B52DE757CD529E123A9E5A5AD35AE53B3A35D91B1A2DA5F8EAF3EC8FEDCD6359125433E5D35AA55B470011C10CA45A14ECEC432679C1A594948C81D47ED4509189A08EAABA0F19474C03B7BF4CBCB208516CF7FF3E7B5E4C3C8124CD4FFA4E67FB6F3B5F5F7D6EF76F7D6E4FA06DFDE57976A25BDEC9AA5AAE71FFEF97C9D228D712B62DFCB2023826B7336D1269
	5F7F82BCAFFD6C5992F8F6C35986BD5BDEA1ED8C154277E5784E89E75BFF836FFB2447C25A99F8C922278B4A32FF7A24BA3B1ED74F201E9D743B28359C1E4F8138GC71EAB5EA47ADFEA2D6478940EC7A832C95232EAC253503A0F633681CDA9601CGDAB523CF01A7B3607666A6CEF7792F53257C074336DDC40EC9A642964EDB3A1FBD2B24DF96E56CA73AC6281D04278D3E2781C8650B252B136D70EC6D1B709EF30F2AFA6DFBEEC553EA5D1A2A8474DA0F9219500321DADD0968EA40BB96167DCAB4981EE8EE
	9E84CC279CD15ABD1EBEB920E8A02366G51736956D2AAD71272C1C7A645752BC4F7885ED381169FC07C5B9384DF886FFB8FE65BEB8EE2FD0F3E594A6B633C7EA5A967103FE4F7127AF69DE475548E9A754D533EE7FB09364D280334C1BA142D874885A82949363F86508570B20D89754C81F8360F6A1DC13FBF98E88F05BAB4CF78EC5023E8705E5C8CA59C572F3803E10FA431F6F73756D858B39BB03E49DB66E3BEC9BF0CB13022FF2534798BF75BF6D878E4F37AF55B659A668B3D467089537DE0D25DE9B531
	B19A2B7B02E456DD735D0D455BADF4F7BF3431B81D4B49B269AE28A1ED7D900D014D70DE4465B27C23945F7A1601E7716A24F8726D04322C37B0B61C4F21AD0E12FB250A7ACFD87AE1693FAC36D555B29B1AEA63EDF914461D2716695CA3ECF91C6253CCF8E64BD10AA75FEB214C7507ECBB0B2F09DE52FF8B613B32AE5BDE87508D6083C881D8DA07B17679B3FF18E70C7549E13DE3CA55BCCAD8E23C930E976129CC6BE139A790516500DB9912432ABC2AA9B0A2B29A5DC1FCB09A43F70D463E9BF41CD282CAD8
	5655E0G24C3875615F004515C66F81E500495DABA287A21D34B7E1004F27F52C16A2C49917DFC48A36B0AC1414A6FF410B6F9CF9E8209A0G5E19DF161EA37E4A047AEFG4895FE58F51648BB2304E140E8EEF6840393D321C1D293292E1E78F98D9DBB72013E3C9EFD1C56C7F0BAFCAF2EC7B9DDE7DFB04969D25D44FCB9FC8D7B58FA35821E4B01BE8BA087A02F9E6367A9A746CF572EFF10EAB65EE599BFD9904B52A196B71507627B51F729FCE2E39B1439G969D4279600A6A1C25AF93FB481358343C5E3A
	4FA583D28726A3BA2B0E6D12B15E945F02FC6C6F4C6F36FE52064B21DED9G1B8F216FBFB8DB45E239030C7EBCFA4F05A170940F51925CCF932FF15F96F33BF6F09DBB81489C361B52E13B7D045267032EFD07E10C8650G52G32G36G14B7E45B9B9A508F1D93CB66A97FD6FC70478233ED1C5AB617DDFB7E4C3631B61C6AA57A1577E00A6FD2C2CA4083AF91DE76FEAF31130FB7E3C141F39B4507E0FC6A0481A1A270293D249E322E075551A9DD99D0F412A99865CB282E4854E848985AD057A7FA09EF02D1
	981F847D32DE52375D41C0C02141A4D8F8795F5231C55194BF985D991C8A688CFF7FB91217CDA857785816FE6ED9090CCD5A9499A7D92E13A1937F07422A9FC2148C35520A86B26EEC92399A014B00A3B95BA2F826B56038917DBD0E9B2F4DBCA02D3E9A32D94D01CBBF7DD7367C861E03B59A638633691EBEEB5B7F0E0EF19C9FE136FE27090CB171E9D6B125DF63A7F2405DFAB0F0AA9C1C8A55B37CD70AA9DE8D522905B49F432716137813C321DE987EF97FEE99A03455E0E3AB40E0A3766F175FA565B83672
	0E5D9954B4569C240E0D44E72F0C6FB555DBEBE4BEA36FB5C7C87999D11E8BE5858965EBC4398E5F0B0F98654C0E52FED27F08B2B9254014C7A609B322EDF7773F90D7B6445B19157F261F3433E61ADF4A5FA6BADF53B05F4E607A5671E76E91261B51FDDF50B961BB843064EDC6377EEDB35D5F7B112E34A95BDE55140C6EFCG695A004619144E9EC43AE9C2831051446836B519699E91F4B56F40B8790EC167F947CC7797A124DB8E78CC00CD6FB03A4AE6B35D09C924EBE8E67319231951DD0E21FBC65025B4
	33B9AF1E0E456E4F76137EC457946372FB72F4EDA48AE3C2F373E99A43EC1D50F1341453AFEB4675C0200D44E48EFC67811434E05F7B76AEDC8F24CD24CB250F6594A7DB7F6CEBE1F6343598FDEF555D51E29673378F921D0B20ECC98B467B1D7D661C24BD92893AD51A8E71D8EA9FB077CD06BBB9D0864FB1B59C51FBE0701C4638F38C90FBC93E44515C9F4F547E56529F9DC68F16B0573EF152DC572C5DB7ECBB135475FEDE4735263A66FD7169E216330647F13DD7528AFD3C9557FB1D65EFC8096BBD98B37C
	E44CG1BCC23875A64BE0CB6AD197934643A6B13C9ED52DB19ADE92D06CDA9FB9B39CDDB05CD6B213C64A85A34F178C533FFEAB519F8E7G26140097F37033DAB977E7AB7069GECECDBC571EAC055D5D9D3E7146A7DCC66A565F7441E387173EA0F09F0886BF76F1EB6EE7F7A1577CA2D9FDC31D92DB7D3F77BECAB0E727C6D28D13F5CC73FE74B87E0F66E70231DD547202F83307172F823F93CDC8278AC005CE3D80F001F2C91D4DD7157127F86F44837BBFD608845D3CD1DA2C775DABAE55776C0798972361F
	4717B2D0A761FCF0B5E266F4A4E3ED59BF446CFDD8581B8F36549CC7FBB395ABFBBDD04E78EE8610EF1A60845F53BEAB3E2D026FD090798A81575006FCAB6DB173896053GB25B50BF77848F41D32613D0ADFB74A96131E0581F6820F6DA4CDD44FD34FA38D6A2F38619474E4A3A0F86F902FFCC1CADA4BE184DCF8B1B5703BDA56DE833660B3739954ADC6D9857CF3EC05A8566DD6D9ACE6424DB4D211569FCB004BAF51037C268F4DD32725316F674537313FF1C1F8E6FFFCE782963ACA47855069F8213E8D381
	682B6BC01D9BA7C96E257BD41E3B543D7ABF708CABE3E1A562438185C4B964D17039A16F0DB04A73012C659D18571FBA9A3FCF312943D8AB65F5A42E1B587A7D07F47E10BA597AFD0740BFC57115A6BC532FD3BC792E033234CEDCF3268F11792BG3E77F6C11FG688270G24F4613C11B3F0D81AE3CD5F930025F6C05686E212DFDCDB8F925FC13E4A3FBFBEC87CC073D58957E0AFBAC9DBAF871DD9GB9GF9G65A75006DF1F1F5306935316B6B0BFCF0FAFEC8F6B23374C476A9399A67529817B3B81DCG53
	GCBC5FD2E1E2F192BBE387741AB426CF9EA0175A8DCA05F00964F17F9A2B6A6F3623E991F1C40D79B9F47F1FC4CEF5F38C8722E0A13900BGDDGAE00940065GEBCF227FD61CBFBAE7BC104EBE986C84DCB764471D716BACE16BAE7F426A78F2825F6F623E371C0C65E3E3460B9D044EA2E76C17BDEA10E5CA92DF2F8DA52C5748DECCA1482DB885BE223964FB0D18DFD61F42F1E543826B747DB96B64BA954B47EC782902B6CC8B9BD2828B33614E843EF89BB2ACEDF8CBC59B56899B1A028B3361E482DF3C8D
	D237D55B6E9EF8493AED31D9F93B3E59EE552E15A033269B5B75F1BB56272B9B6BD338407A1405662A4F7BF175E1F65E613332B38DE853051D7779504E55424E0C84FD7333732B89FC8963C637D55BFF62925AD05103B668138B336136843EF89B3AFB2C78EA428B53571C40972F4F95270F6DA1DF216B4AE9A8DB8C40F0AD4789AE833E5789BFEC25F3FF9EFCCB27E11EBF0D78FC0AA7B99549DBC9BE4770B3ED336D396619ECA17248F36F69796F6FB29DFFB32B8E73DE2EF90FF73EBA589C71392164FBFF19C3
	56FBFFDF9D325E7BDBBB74BC43038C734985162F9F1AFDEFB0E7A8795E606F1C496DFC5DE9ED67C127351D754E596DFC47B93B1D5707135BF25B3035AD29435636FCFCF8F6DB6E9ECEE64BEFE98CFC63455D14BF92D1A2F4B26F12F51917BD217FDA3258BB253BFEACFEA06F546C1C66049E6C2C97FA9F3D183C0E0FDD342EE36EC5328F9389CE055DCAFCBD1E38D8A94A0CAC032CBF09B90CE69B65576530D7C970434FA9BFABCB46AFDDCA66ABD657AE3F392EE4DD4446450F370A5C60B46EBD3D5FC07A3D8B3E
	278116087160FA23D50E184E7BF616C59FE73AF7844C3A8F0BBE1E2FD8495B4065ECC9AA6F9FE364B588F907D4ABF985DCCE7919E47202C1333CC6611B2B6D681B2AB3681B1F15925FB861BB8410FA86FD736FF6AB5D4B394ED5C9F59F8E19F59F913A3FAE664CF5C277173CF4CD6700BCG204E013A53FDD63A3B9DCC67E5C7B25DCF4468FEDB7851B6E2A5AF446524A4157759C9333CA601F7074D78F784FE6AA8B9175064EB41A9DD12CA4E12BAAEBE0A7944D25013813089A04F41728A16C3FDAECA765C488D
	8C887101106216F2A9EF7FF164F540378BE09140F20055E7F1FD9EFD0554116D3E56F3BBFEF949409D6238B53FB1F007F1CECA38B3D05DEB782235B751AFEC4D1DCF7BD3DFAFDBF36F927862C60257CDF836963F0D52136F25D0D6F28E676EBBE99E9F026FC5827733C61C4F978B5C4EB1245BACF05A9852ED95389E95691A7A9077200AF4DDFD98DF1E7678FA277599F5CD6FCBD6EF2F1D54A32BAF7E1E03CE711B7A626FB97C22946FB964C31973DD3429288C6D4C923831B211473C0BF6BE1C508FAA7B8D5BEA
	7A1359F950C7743675B3FB0A853E11627BCCF8E667BF51FEC83EDDD036369FED7AE79F5AD9BD0038AA614F8601FBE904601C706D93381BA3C8278F607E602A2338C73C96F00583987F75AE3286B6760865285E5CFCAA2CFABAE42F0376A6B60694527C0F70B78C6298F26098112941F78A4032C1BCD718FE837735D7FC66DE29F0237539C26E608D5B2AC16647CD03463E2F647F1E0D691CBE0EBA771E4F36BB4E935CE70E1B7769085FCE59D88E4B4AB7451413F96C9149DCDE56E6AEA776FE3758DC5E9AD3CEFC
	F4BB67E7F57B666BD83742EFE6C85A97D3125E99CBBB4F6A14491FCB365CD3947A71CCB16F9BF42E5C86E5A5C358EE2F9D43F62B9E42B87CE58399072661BB8DE095408600FC004A8BC8E3EFA4B464BC2D89F04E8B6C4C0C1C07CD8B1A5486174936E9AEEB2E5CF60309A6AD89BD3103BC73054D774568A2EDB41FFC5A9E67F7AB1ADD7C39D342F13F320C6E11C29235748239D7B8B3CADA2942C95B7AC897275FFC41FA1E413EC8E09B509078B54717B8D9DFA540E4FF5EED487E48984AF6BA93E5EF1FC33616A0
	FB2347103DC6C559539632F74CA1FB31C9B60B2B6B6D98EBCB1D98EB991D98EBD9CEECB73F1D5E3F990635B6C0B700EFD82C53E899B9BBCD99E6E71E770B32DF154E363EB2C38D17990FAF677A2CF8180ECA7BFCF5EC0F0B257944A1EB035F0E99EDF0238C5BE02D05DD68DF2BB6A07AB78D4737418FFC5886DB06318DAE0DE39B14DEC4DF567BC8BCBB45F78B7DA6E7516C7BEB5E797AE1AEA0BA2CFD3D79CF26436A1E947375D9AF71F568A23D2F2AD64EA1E30FD4B7925B4FC3BE232D6E9A41364A342813391D
	882C879A8258D639970D36E2757FCA42B949DC407856AD102F68582D72311C22104E3FF9A67B99FE7BF102AFB061D98ED0F7CC1CDDDF02FEF8894FE586287F574377D6G69B24CADGCDG0317F18F7C3ECB644CED56BD7041E048818BDD4D347CE6753BF1FCE1F7F3576C4039F655D79E14B2BF5A1574FC5CF71935FBAAFFD67F681EA272E4757B71B829DFA61465G94GD40CE45BDF83E89D417A7D6492BFA3B961D175F6DD175DBEFAB3C19C1028CA54C01305820B4BBB5B93579D0F0AFD743E91366E704173
	7D91367745E236659846EC5A486CB1CBFC18CEF8C7D84C925C069123ED199DA7DEA0637309E9592D1BF676D95913DE12878D8435A9FA9F75DAC821A70602F7E3B35923E88F8402BA3D2241CAD95997E7489D024EE0A02207275C3A6209B9B7606B4E5709DD7D0AB72CC44805559892CE730B63AF446898041E5C9ED6E43C7714315F1CF7ED663E122E60796C83C5D665FBC579F3DEF3F97595D65EF785F352EFDDB25F752F7824E6C37F457B5EF30535C7680A111332FC3465882E236F0732D7A4F64F30D4B27E31
	31496E17C48C7F209CE3F85FE76F133A3E53E57BFD923D1B393E1A2B065E42E0D5B1533DEA8CF5FB201C69C9A56E65EB7C7F9067914BC479E74916920F49874499C04ED59613F97014DC6C1C1B45643FF6E2CCD63A661E73EA00264DE5446420ABBEA6CF5611B69FE0F78368D0919BB7F71CA6F2492D81FAC90687A5ABBB62FEDE7052737238F85A54C262BD7E901C45420D4978D8A1F5F439B0D69ABA2C4AF5971EB12CACC3DF263A5017DBC206AF4B44FC359A4AB781C8B244224C763519AF4363684B9A595A17
	F613AF5B00E6D0B6FC691363FDD9B1CE463EF6F26701FB0245573A9173FA246A60B94B75C841480CCD67F6AC150D785AAA626B21912C73FAF936587EB659CA443D1D63467BCE537B11E3FF2A7CA1791CF96EC3554166044BEA723DDB37EA3DF72B2A567B53C1F5763D70281AFC4F7E26EA3E4F4B66343F0F1098AE9905359EC0970093C09B45792CFC7CD5094EE796F359890FD721A33A9C50D17FCF47135775697139FFAB712BF1F2F7EFB4882169A772913FFD0234179E8CD1A44315CD909F05D52FCFE7249CDF
	B1C162CDD346A81A65AC7FA5FEDF158AF5DBBD0AFD34D36CC7940C62FE44A45D6328F343DA824065C6DA6D18B8AFF6DB45763663D6B8966F8DD312A46EDF0EA539FFC9479E266B097D28ABC3684FADC25CD6B76E3BBCF263E5EBD9E2E2E43CA73C783B2FAA8F66EBBF20B6B11FAD1FF89D1EC372B84F48C87A928CE3CB1F865167C647E18C9D143D226F349DB167E3CE102BF9E65F8BE0FA4B23E4CCDB84344BGD683EC824883A8D1A0E7842881E883F08204GD2G16822CG488258G30C541986D756FCFE6BB
	A079A694CBBDE41504AA377F03183D798210D7BEB63B7D0C6F67B1FCD54053B0879F0B410FF5E08CF6837D22B1EC57EFF5E08CE60EE18CBEC3E93741F729977A2697E98BBAB1861A3C379A03DB4915D79E8F2BA6D1CEC8481E68C0DC3A975709DFA8B357B59770795E795475100AF5AD75C1DB7AB0DE8F9F0A39EB407689CFEA412803273E5C3E9995FDB26DC31F24D31999703D8EA057A74E7247516E825F2D7AE4670CA46272719952AF6B54EC3B83A084108A308CE0150A31D6BDD9BDCB2C31CA982146FC784D
	18330BF5A0EB33BA3B8F192E87A24947520C485CE36917A374EEB40C01A4D767E74591324E880E0DC594B1E6FFADD2A1F0BD113EE0C4A519B5DFD3AEF0AD3AFB01FC5D374C47FF9BC8476CEDEA7C9A76A90AAFD4635730D1310FDDBA8E6356B8467BE44C9AFA8360370C4FE7CC485BEFF94E47F94BA7126D11BCDEE445D745699B12721D764E6627EE8BBE36D7F4012EBDAECFB0FFB05CE50A53B90E45C4D5B4F96C3D991DBB766A2256737811E862BC5E92351E475B2366F91C5F6D0F12FE6E05B15DA9684E885C
	304075D11984F7C17568BE019F22F691FC37C2940A9C0D5A9525B8A74749A65CB0470DC5C97F01AA7B02E1641D0812BD25B1D553F80EC6E954GF4A763B72054C6B5A41B6C3EA6F04322AD6ED6935B67D32A39FD986E3971C43ABD63E6BA96A7F7F4AC2C9FFEEA01FCB9374C477A69E4A7690FCBA6627B6F8C45AF1B086F3F451D58FF53212CDC437E7BCC27D9FF8870A95AFC7A6F17936ECF91D84AF9D7D848E0FCA14B7A2E6674B996FC2C2D9EAECBEC3FEF15A536DF13657D01396BF3F201FC9F5CB29FEB27FD
	7437A3DB357876FB1D6273357876DBA97283491FED7778453D0118BB3F6BG3F59BF1F39722919646355FF4E4CBDDE7D770C75F8D5F2BDF13C32DF379E2FF6DFB70FD72CCDFFB812584ECF0CA43673CF4855E03C03E2FA3FF17443DF835F7D216AE0F5CEBD166807BB0EA50EEB4587703CFA1B9F4FB7BC8770FC23D0608A0B104E91C05C088D691C82A7FB91179E409CD4FE43741BA4BABDB07C3E69DD869E27876E23E82751A60BCC6F764E8F3FFDDE7DD0F5B0BF6D6DCAEC1F173A925BE743A47A3DC07838BE0C
	BEAE89A26EE39DF81FE0D06036F6A01DAB087D7D55F1431787CD6F2BE67D8D7D1F462F0FFC28BA787D8B0BF9FAED4CBC4D56A1B75FA7F5E7FBFE578F601EDFC670564E8C693D0CE07C197063B78C593A8DE5EFC8A23BF89659DBCC32193CE9AF4A5BE6A14FFC76C07AD8C9885677A1E3FF32A694EFEB6987062D5DE1145D94CAFE6E3EA3092D8E60993619E59F148C593F6A90676E9632D1A602960ABD6BDBE4321B2DC1D79C40B56862906E757652690D7F5D9D9B162F956513B6237C81AF169706EEF58D1BD6EC
	483996C6B98D132867CBA5C6F91D58BF50A6E75B1FFD0C7E6DA556CFBEF740BCA75F8FFC19137349299E3419795683CF6E3C78D6462CED720127B4BC9F3E1BB1F7527781CF5D3C789AE3F246AE60F14E41477A713A8309FD7B118309E3669ADBA25DC336C43A2FFB93699E76A6525D95CE247BE4B8114E5119C877EE27190E7D5D2D07CB3641470AA7256D3A7482BD7A2BB27F53258A53399DC76DB60E692A480FA701089D32D409DFEF6A52F6FA4E20CBFBF8919E337C8170113034DB7C5E31072E15F25FBB5CDC
	7C237B0AEDD2C90F74AC4B45FA249212D075C85BE93E55A395C3AEB6CD8ED1CA4C901914EECA4F7EFF446A52F3F8E2DAC536E11AF5E927F1FC89DF453535356C7FBF2FE16C5CE77F8766734C025B5D16FFB703405EA9FEA6BEE51C7B30FD331BBBC8CCF9G3FE88A77497E6D593724647B4983FED953F4D91BE88FF8485EF247B5DDB14EB0BE3203480FA08DAF643F4B5CC16C9EB7717C9FD0CB87882DA4BC0A2698GG18CEGGD0CB818294G94G88G88GDAFBB0B62DA4BC0A2698GG18CEGG8CGGG
	GGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6098GGGG
**end of data**/
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