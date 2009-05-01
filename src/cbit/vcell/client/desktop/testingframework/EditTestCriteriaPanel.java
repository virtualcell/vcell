package cbit.vcell.client.desktop.testingframework;

import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.numericstest.*;
import cbit.vcell.biomodel.BioModelInfo;
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
	private javax.swing.JButton ivjApplyButton = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField ivjAbsErrTextField = null;
	private javax.swing.JTextField ivjRelErrTextField = null;
	private cbit.vcell.client.TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private java.lang.String fieldSolutionType = null;
	private cbit.vcell.numericstest.TestCriteriaNew fieldExistingTestCriteria = null;
	private cbit.vcell.numericstest.TestCriteriaNew fieldNewTestCriteria = null;  //  @jve:decl-index=0:
	private cbit.vcell.solver.SimulationInfo fieldReferenceSimInfo = null;
	private cbit.vcell.mathmodel.MathModelInfo fieldReferenceMathModelInfo = null;
	private javax.swing.JButton ivjSelectRefBMAppJButton = null;
	private javax.swing.JButton ivjSelectRefSimJButton = null;
	private cbit.vcell.biomodel.BioModelInfo ivjbioModelInfo = null;
	private javax.swing.JLabel ivjBioModelAppLabel = null;
	private cbit.vcell.solver.SimulationInfo ivjbmAppSimInfo = null;
	private javax.swing.JLabel ivjBmAppSimLabel = null;
	private String ivjappName = null;

class IvjEventHandler implements java.awt.event.ActionListener {
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
private cbit.vcell.biomodel.BioModelInfo getbioModelInfo() {
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
public cbit.vcell.numericstest.TestCriteriaNew getExistingTestCriteria() {
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
public cbit.vcell.numericstest.TestCriteriaNew getNewTestCriteria() {
	applyTestCriteriaInfo();
	return fieldNewTestCriteria;
}
/**
 * Gets the referenceMathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The referenceMathModelInfo property value.
 * @see #setReferenceMathModelInfo
 */
public cbit.vcell.mathmodel.MathModelInfo getReferenceMathModelInfo() {
	return fieldReferenceMathModelInfo;
}
/**
 * Gets the referenceSimInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The referenceSimInfo property value.
 * @see #setReferenceSimInfo
 */
public cbit.vcell.solver.SimulationInfo getReferenceSimInfo() {
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
public java.lang.String getSolutionType() {
	return fieldSolutionType;
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
					PopupGenerator.showErrorDialog("No MathModelInfo found for SimInfo Key "+getExistingTestCriteria().getRegressionSimInfo().getVersion().getVersionKey());
				}
				SimulationInfo refSimInfo = null;
				refSimInfo =  getExistingTestCriteria().getRegressionSimInfo();
				if(refSimInfo == null){
					PopupGenerator.showErrorDialog("No SimulationInfo found for simInfo Key "+getExistingTestCriteria().getRegressionSimInfo().getVersion().getVersionKey());
				}
				setReferenceMathModelInfo(refMMInfo);
				setReferenceSimInfo(refSimInfo);
				getRefMathModelLabel().setText(refMMInfo.getVersion().getName()+" (MathModelKey="+refMMInfo.getVersion().getVersionKey()+") "+refMMInfo.getVersion().getDate());
				getRefSimLabel().setText(refSimInfo.getVersion().getName()+" (SimulationKey:"+refSimInfo.getVersion().getVersionKey()+")");
			}else if(getExistingTestCriteria() instanceof TestCriteriaNewBioModel){
				BioModelInfo refBMInfo =
					((TestCriteriaNewBioModel)getExistingTestCriteria()).getRegressionBioModelInfo();
				if(refBMInfo == null){
					PopupGenerator.showErrorDialog("No bioModelInfo found for SimInfo Key "+getExistingTestCriteria().getRegressionSimInfo().getVersion().getVersionKey());
				}
				SimulationInfo refSimInfo = null;
				refSimInfo =  getExistingTestCriteria().getRegressionSimInfo();
				if(refSimInfo == null){
					PopupGenerator.showErrorDialog("No SimulationInfo found for simInfo Key "+getExistingTestCriteria().getRegressionSimInfo().getVersion().getVersionKey());
				}
				setbioModelInfo(refBMInfo);
				setbmAppSimInfo(refSimInfo);
				setappName(((TestCriteriaNewBioModel)getExistingTestCriteria()).getRegressionApplicationName());
				getBioModelAppLabel().setText(refBMInfo.getVersion().getName()+"/"+getappName()+" (BioModelKey="+refBMInfo.getVersion().getVersionKey()+") "+refBMInfo.getVersion().getDate());
				getBmAppSimLabel().setText(refSimInfo.getVersion().getName()+" (SimulationKey:"+refSimInfo.getVersion().getVersionKey()+")");
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
	cbit.vcell.biomodel.BioModelInfo bmInfo = getTestingFrameworkWindowManager().selectBioModelInfo();
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
			getBioModelAppLabel().setText(
				getbioModelInfo().getVersion().getName()+
				"/"+getappName()+" (BioModelKey="+
				getbioModelInfo().getVersion().getVersionKey()+") "+
				getbioModelInfo().getVersion().getDate());
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
			getBmAppSimLabel().setText(getbmAppSimInfo().getVersion().getName()+" (SimulationKey:"+getbmAppSimInfo().getVersion().getVersionKey()+")");
			getBioModelAppLabel().setText(getbioModelInfo().getVersion().getName()+"/"+getappName()+" (BioModelKey="+getbioModelInfo().getVersion().getVersionKey()+") "+getbioModelInfo().getVersion().getDate());
		} else {
			PopupGenerator.showErrorDialog("Reference BMAppSimInfo not selected!");
		}
	}catch(org.vcell.util.DataAccessException e){
		cbit.vcell.client.PopupGenerator.showErrorDialog("Error selecting Biomodel/App Sim "+e.getMessage());
	}
}
/**
 * Comment
 */
private void selectRefMathModel(java.awt.event.ActionEvent actionEvent) {
	MathModelInfo mmInfo = getTestingFrameworkWindowManager().selectMathModelInfo();
	if (mmInfo != null) {
		getRefMathModelLabel().setText(mmInfo.getVersion().getName()+" (MathModelKey="+mmInfo.getVersion().getVersionKey()+") "+mmInfo.getVersion().getDate());
		setReferenceSimInfo(null);
		getRefSimLabel().setText(null);
		setReferenceMathModelInfo(mmInfo);
	} else {
		PopupGenerator.showErrorDialog("Selected MathModel is null!");
	}

}
/**
 * Comment
 */
private void selectRefSimInfo(java.awt.event.ActionEvent actionEvent) {
	SimulationInfo simInfo = getTestingFrameworkWindowManager().selectRefSimInfo(getReferenceMathModelInfo());
	if (simInfo != null) {
		setReferenceSimInfo(simInfo);
		getRefSimLabel().setText(simInfo.getVersion().getName()+" (SimulationKey:"+simInfo.getVersion().getVersionKey()+")");
	} else {
		PopupGenerator.showErrorDialog("Reference SimInfo not selected!");
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
private void setbioModelInfo(cbit.vcell.biomodel.BioModelInfo newValue) {
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
private void setbmAppSimInfo(cbit.vcell.solver.SimulationInfo newValue) {
	if (ivjbmAppSimInfo != newValue) {
		try {
			cbit.vcell.solver.SimulationInfo oldValue = getbmAppSimInfo();
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
public void setExistingTestCriteria(cbit.vcell.numericstest.TestCriteriaNew existingTestCriteria) {
	cbit.vcell.numericstest.TestCriteriaNew oldValue = fieldExistingTestCriteria;
	fieldExistingTestCriteria = existingTestCriteria;
	firePropertyChange("existingTestCriteria", oldValue, existingTestCriteria);
}
/**
 * Sets the newTestCriteria property (cbit.vcell.numericstest.TestCriteriaNew) value.
 * @param newTestCriteria The new value for the property.
 * @see #getNewTestCriteria
 */
public void setNewTestCriteria(cbit.vcell.numericstest.TestCriteriaNew newTestCriteria) {
	cbit.vcell.numericstest.TestCriteriaNew oldValue = fieldNewTestCriteria;
	fieldNewTestCriteria = newTestCriteria;
	firePropertyChange("newTestCriteria", oldValue, newTestCriteria);
}
/**
 * Sets the referenceMathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @param referenceMathModelInfo The new value for the property.
 * @see #getReferenceMathModelInfo
 */
public void setReferenceMathModelInfo(cbit.vcell.mathmodel.MathModelInfo referenceMathModelInfo) {
	cbit.vcell.mathmodel.MathModelInfo oldValue = fieldReferenceMathModelInfo;
	fieldReferenceMathModelInfo = referenceMathModelInfo;
	firePropertyChange("referenceMathModelInfo", oldValue, referenceMathModelInfo);
}
/**
 * Sets the referenceSimInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @param referenceSimInfo The new value for the property.
 * @see #getReferenceSimInfo
 */
public void setReferenceSimInfo(cbit.vcell.solver.SimulationInfo referenceSimInfo) {
	cbit.vcell.solver.SimulationInfo oldValue = fieldReferenceSimInfo;
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
public void setTestingFrameworkWindowManager(cbit.vcell.client.TestingFrameworkWindowManager testingFrameworkWindowManager) {
	cbit.vcell.client.TestingFrameworkWindowManager oldValue = fieldTestingFrameworkWindowManager;
	fieldTestingFrameworkWindowManager = testingFrameworkWindowManager;
	firePropertyChange("testingFrameworkWindowManager", oldValue, testingFrameworkWindowManager);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF1FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DF8D4D5F658224D374B3725ADF6A39F5DC645ADD53438CB153A34DFF4F10B9ABFB122269AF72326EB5C254AF6234B07596D4B1FA49AA5C040D8D15395A5C088C90890E2C0C88688A4A1C3B2A1139FCCA619B7B3AFF973131997929025675E7B4EFDEFE65E1B046C3AFC5FF1661E1FFB4EBD775CFB4FBDF722D072E1C2FEF6CE09A0E4678B6A3F03590210F0DA9096B4AC3A97F1F5BB138B855D3F7F
	8358A4A47A736033195046128B0B055D7985506E0776AADFF261BB70FD0B303E57F3923E1868B98A346BFE56950A2DA76EB46A59C05B83A3C57079B640A0604867621DC47F44C8290A1F2D629132F3E0CC6504672FC7362B383540D3G38FE00C0B9630F03CFE6403575F5AADF7FDFAD92B29F8C663CC57A51750910214A966DE21FE5427A6C253B082E9FD1BB090C855A17814860F3050B85A7603341BA6ABEB2BCA4A9159706C5D92E9C16A55127D4BA455028628FD4AAE2C811FCEED7506195A77C41517A7A63
	CEC9318136A9A8A9E2D0F2D89DBED196042A5DC4D76EE8816DDB9056011E6594771AC474AB70FD8E40425D085FAD933C30A739309420EA8F0E79704E86F5CC76AF6F96D657F873B61A0C39F98F9BEBE00FB6662411E7F25E25733332054C43E2202D82C887A828C8AEAC87E886F80F4645A327F7436731A125496F757AFD478201C65999EC73BBC5993E575783C545F508437E20D390585C5FBBD2E1E0CFACE0F20FDC359C7349227D98870BBB6E90D2FF7837B99B8CFC123A684B1C418A660B498A4DA7CCF74128
	196E780A70B88D575DAD68F52F9CFBBCB75FC0F77A970F67AED67BC954694E2AA0F36D27B118865F3355FE997EB2459F5D2B61D94CE6D3BCE97783ADF1AF46067DED3425B56FA621387A3B06FE083FEFCE4E0ECAE6C3C3E524AD5F23F16729E4BA5F6436AC2678B89D1E59D2C571243D88E8C57B128BD9FCBD5AC656F0G5AF3G9682AC87C885D887D0D405B1D65A32EF1AB1E6F5841546F1C9F60AC10149BE5A328DBE45C9A568B8658BA98E5F30F84E810BEEC896E137E2BCCF35909F8C05497D0C467EB068B8
	A97A4420C3117CBE689D96B12C59906349E859C4F802A2255AA4AFACEA07B7A0E03FEBDB3640276C88A9DD8127C391B58EC6DF57C2666402639C74889D40F766175F1DA17E2A0071DBGEC5C8F21D652DF0B98047D223E3E556F9F9D8F5848C80405D5444F9F523D43835FE7D5210F573793DC863405EA6C6756564D3AFEAC52B0B15F913C08EBECFC2498BECB003F8E408A60294678F9490E71E3F96BF62162716B8D6347BE70F86E65EA96B7732AA357511F517E090D4B01168A10D50DE74206913B41BAAA298E
	DFDFF591EDEB35717A5303076BA8B354119B514E0FE8EF1822FFDC6F4C6F1E33E48E3757008F816CB5687B7FECADE3315C688809983D6DC188BC51294D8433F321DC227AB6D73507747F3E00737483CA8FC05F71G09G99G457BF11C87E4FB04BC5AB81DFD737DA968CCC7BB55017DEF38CA9DEFCEC1E7BEFB3A0D1CDF6285B8D6ADE2C074B961CBC825BD57C6F4296B5B6567B299949F007D208996E00863DFEAA3EB5621A8C1E9E8DC91BBC5051C4E9A7DD52AABB4BE9450369254755BB6B2C67F846C871CBF
	678CD9CB43FE1FCF241347C5D4FADEBB0983D1963DE0F413FF5C27B07C9B6DA4979A95AF2AFB495B6D2F8BE4AF1047493E44720BA58E621FC0D072C2C8102DCD085BCF56790151D21D0F6AGC77224FE788C6C47F5BA71912E539D177ED1C89C8864981DB971377CE54E2A7DEC6D246F5756A93369FFCCEC4F3796E8789033B5E7F4ABFC1A24B6B4BE368897BC243FA149CF37EEB2DD8C5F331D623D309CBAA52F061FB3C8624991881C01EDD7DDD7B7F6905E3CDA48F9G2AEAF1DDDDE3A5F45C535485556417E5
	B6ADC01BD7CBFC37F224C49DFBB33487EB196F5E6674A323C8CF05F6E694FDA92717FF1ADCF874D30D4E6C701DA5FBDBC89C9B976128A11BF38B1F6371331BA3E8675479E6742F4E127916F57B7A7683C4678BB2663ACE50F799E0012AB775D3261B715D66C5BE8F34E79D5078928E687986FC48178A6DCC9DDF49C1BD5F96BF7259GAF834CB948781242781E64FCEBG1FD727716D28E3FCACC6FE3D0B44AD4D17C79C979C13152189D8FB757527E918307CB7011EE1047FE89D6639C9BEB26713501E8B30288E
	E37C5137B04F1DB73AC8D898DC1DEB165BA72AF62457E9B1DEB1B8114BE26A171DC4670EC349053507B01E561D551F3547C2A17F30C40FF9F52E7E21C33F8698EEF1479B70691202A16594ECD213B82FBF68A07612BCC0C52BF9514B7CDE518CFAE79D42FBC5D351A7C2743D82560D172C1B7A7AC65D8A429CF3F8BF7AE26E25DB04416B67673CE9620BE507188FD28EE93EA87EE9EDAE33C99CC51B0A6BA14F2FC71B2EF5985994724B9744E0FDBDAC5BF1193A0759447AFA1A77B58B7AD9467BFA3CE71BBE8EAA
	E5870902CE5812FDEE14AD769629F13096644ABF03736DB302BF58D0229B4BA06062G968330BDE342DBC3768C3A68AB5907D58C3A7CC12F68AC275A9C93CAA5BDCBAA0FD172F172FD9746685F8CD553B5CA760D8E515566D0BC5457652D66421F6DBAA760BC5C346528EADB529DB7898DD73E48B13AFB45ED746494B4B07FD7B5E873107868B3B92B81188FD6F39FDC861E258D680357BEB172411A8674EF6969FD7AB128FB6AD4A3E0BA63FC28D3B88C394BE15439C90A5499G5A3C4328F3202E166D5572C57D
	81B08D45CCEF039F75AE02BE53385ED71CC6E3ADBD0273FD337F8F1B6F114B1B382F9A5B5889D4CE671359A584502E8A5069BC02F60DF80C6CDAF0847D31FF575B617D52CED94EF4CB54BDF24991ADFFDDF1A4BA17E5772B2354C7A9C7587DAA1F63DBA8FE0D8E4FECCF566672F3307DF33C935C57C576611550CE874882A8B98AFB9F006DA86EFF751D7B05A96EDC27FCB045BE075C9916AC615D27134C8F64B7EABB331358C1739B817D704A8CEBG392677F0B3E0635E59CD73E598639C0005G4B81D27918AF
	5AAA269AB35E9D5541B2FBC62F5A9EA6F7F106F24F4ED06E65994AED73C64AE59D8D0F5152E361EDA637A3CA6E0308F6C3049CDBBB9BBB49FC9964890D630AC2F3B916877F39BFB28FA6F7CA9B746981E079042F817309D947B0EF786A04BEEFD83E71AB7333D235AF65186E2CBC732A1AB7BCE0A7F9CFE9A32C9D00D6G8F409C00C5GC90D98D7BF319D1EAA2E6826E473B7812E99723F26487B847769D3B31C0B0CA967E2CDE3389C73DF4DCE74DFDE935EAB3F631B198D8B2264226221A9DC0E4D3345E5B84F
	406BE942F9EE1678BCF3BB9B7DB333539AA5D7999997CDC63E2A905106D55C065783B33321A4CAAE5206820B118DB7FA5006B220333A50F21AD35B21ADF35C8DDE021B8B6DC5963475041F6012201D46F1E7A9AECEBD6F99AE45E7D4BFC1C8525915E609BE4B2637677CAA268EFD2DA632CEB39DE06B7221AE733AC2F217F1DDA1254B382E70532ECD8C8FFD682B10C87FF9D76C3A43CBDD66F507FD5D66F69E68B63633215B584E466E58F61E6EB63373FD6A778736ED2472211098221B164521B8D45A8B8A2981
	C455C2686D9DEBGED5EC215673862EF2A66F17BA3BF46ED26856FFD73E8DED3F1BC39508250FF9C63AF54E0F40EA94755371F6351B14776EBEBAFB97F97GFD9940AA008C00029338CFBF5BBDE54EF3FCB2C64E73185DFC1E1E329B4FD31A1D5CD9C37E71603098B997AF58CBB9CDBB91CEA9221738D44D33283C62883A4528397C8D15E7B4B3F92147EC3E59FCED7269672B062F4F03BB0D66207C047AE6F0A2FA8ED8FF770775371F4F7FE5974E3F75844EFFBF2D9745C1BB81E059891C7FB649C877AAD5E73A
	296E0BFE3D6ECF79D82AC5237E56297D941DB46B6FF0C05FDFAD6F6F7B9E237E4A54FEEACD7BFBEC1454GE547C57F3882B97CC96203DF70DADFB334878162G961CE4E7A04B176E1CA0798BF9E104296B8C0843425CE6A23B440B32D924AF40D581D881824DF8FF3A2511584CAAB0552A9D77FC2461EA54B1A5C61D09A94D5AB856B41B5DD9EC747EA41CE2F713F7B9DE2278869D1E5DD96653B5CF5A13C0331C46736ADBB40FABEEC9AEACEBC15C4B8DF806D534609A36771A2F4101DE63B5A8769A6F15233D31
	774AD02F595AE1761D213953E530ED8E377907BB516678960C67EBDC113E4DE8517C1955E2665BD5F4FD9437B29F66F27CD394DF2643B35F8ED1BCE9D781EDDEAB5A64243953BAE89734A16E81896DAC6138E61A5F54C13B1963EE97114F46F17FCAF3100550DE4AF13F677DA592BC35E7334CEBCDE7204FB3984F5503A42F566AC3F0032F2FBF99141C0D8EF7ABDD9DEC4F68158BD479D9E7F04F0823FDAE0376AA000CB31873CFFE02B97F626F5FA4447B0CEB05E9362FF204F6666F9D6DDA4E1FB672CC8E5379
	0497F58E82BD3E1D6072FC7ADAG71EFDB8E4B57987DC0981D444C0D021E6E892393FB77676A6973436844C75D2ABC9B5BC39F6358329EDAA2487FBC5B74B78EE96A18722C6CF3EE7A8D59997FF4A9174D5113748CEEG1A550A73D69444F9D32C982F3D01AA78DC816DD400CC006233308681DA81E40059G8BG16832481E41DC5D9BF15AD6DC8AE2C833083CCF6301AF6FC872BD9AF6BE035E6D2C7CE016F6BBAD0B6411FE8B0A6228B3FAFBFEB1CEFE62A63CDD3ED1CAABFDC900327C2C74BD247CB3E27DAF5
	B59C75FB31C1BFECAC0F990EE5136E7BD6937D7846C6FE1773569463A030A586CD7F7EC6765C0D3A985347B346257139F5E383F94BD2E0E3AD6DA43F7BC9F710281C5BC9626775439615373FB35A97D82F4AD5A1GBC84362A34591DEC8F4B663A441D613A368FEB3A9E5C093AD2CCF4ED5069DA83BCEBF43A841B260B45FE3C8B57C3118D5743E68F2E07AA9B4665939EE253AC5ECE23ED722E425A9ECF2C3B4C55G51E19CCF577C51F4C43E03EB737C25946E7B4F1D44ED71B66217B429F40ABE617C9543657F
	C25466EE0508F317E6B0C67CBD894EDDA670904039AB680A0C13551EF0DDF73BB4DD07A454D552E52C2BD0272B9CF888202E861DAE667BD2F9E6F535CFE6A8B7F055F2ACAF68256F894DBA7B99FE1862EDBABCBB1F3F434F67C1202D68427A71FCB7196B72F3101F817483A8G73G161C43BC2C300F6C73B16FD2B6FF209592B5D917DA3171BD9DD52B1A8A58B9F86F86BC87175EF32B306CBA0BE9DDEF45B9752E20FE16BFFFC3B66E011B043DB416CB3AE10D8358GE400D9G715DB83E33FDEA2D18BCF99FD3
	944730073E8472C231A4CEE8E812143338FC499F1D636787F06DAC6EE6B97E8A78CC69E6EF8AACE6BF8AE24C2E6DB60ED90CFD82F9766442D2BB0BD9522E30C72E0F87F73CC96D8F9D8F86DB9DC3222C625F59C6709D229C01CF78E0A36737412ABAA10932D325FD7748C6AE93C97B7C6B4D14263D24E19117512DE51BA268CCAB52F7DFAEE274CE494BA455F71FB3EFF23C5E4EE7BFA667DD276E6D8B2B460C7E5F5E2DFABAFD1A516A4A0C47B5C15E70711595982882C65B7BA1B9F39B555FAA202D6CCD683600
	BECF2B3333796B37631B512B0674G27D775686933D57AE2CE3F54E4C4CF647478EA237ED7F37A0BDF9B51B3B87DB951081E456943EEA3FAF18F66580F7469FFEBDBBCDF4E41180B0C579DBD6A5BE70F16E3337C7A60E12C83B801762140721782C17B4776F36B2840577D33C5386E375FF633E0F952127311095E852A3EB5BA3DC95E32DC267BB5DE6B5E519BCB77BDE44A5535FBAF2FCD77030C8230GE0C9AF2B1732353B4B03EBF7E52F715A4D55C1AA702C6D5556EEDEDF645ADDCF6F7B4748DB2B9A394C8F
	AB4F6B6F81E5FB5A8D6F816B4EDF4AA96ED3EB91FD1A9F16F1BF3C426B581E3ED8FEE8A4CBC62D5F0C707AFBC2CC19DF10EB37FA0E8DFA356F126E7BBC790F154B181F2137FF23BA5899F1335B3C96F12B5B38963144EDDC0B384B9D3BFE7CEFEE737A717DEE7D6F565899F78F5D0BD2E02EB281CA7B617E86F0349F4F37CE4F6F85FA3E991CED471DEED20FA43F26D5D07F8149FC2C072529FF03FBC2A23F1D9972C3B8FBC97F08EF1548FCA97EGC52AE72FC4FC94145C9E05312A78EE9A03326822E81643BC9F
	409C269F46B62F9F5F0B760C61BDBE299FEB87BDE37437E567138B3783541EC7DEDB90E33B753CD1EC2F7D5C884762FD18389CFF7F64B2797D93BDC6182E678228EBB6573FF78CF14B4FE30D6463726D46FD71831849BE5C0BFFCF10497B3B3389F195DFA06E11EA3E6FF2DC599544AD6038F7F91DF6B5474DF7A32EE8GF344E27A37A0EA9DDB7A98BE4FB9C654AC1024CC7EA0C653E9B03FFDE8844EE31B434D57E743E1FD8EE801FE6D83316F6DCC6FE78A59EBE5600D83D88C3082A085E0ADC05EA044BAC085
	0085209FA0GB09BA09EE09140F200D503388E2E9F5DE5E6BBA0552294DD8CC107C41603EA3FD02E37BF9D7ADBB7985BFE962FAF3AB0DE0B9CE023037F1E24A66CCD1C5544CE487E09D6B515D47761EAB74675E5874675001B7439945AC9G698E1C37DE7ED61865385A38FE670240FD7E2B8BA42EEB07128B2DG8A409C008400E5C37CBE60AD0F61C7B6884D0D4C076B436A72C950D75AD0EC9F2AB526307701CC10A9980EAD47EC3CAFE03E07BD9018FA8FCB26EBD7043D47F6B1203E2FBC99A0793E5F658A09
	FC2FCC8D94F35C2910559F12483B8C337D4528F75F691DE73F1921DC55584C6E128F533DF27BF064DD72890AAF9F0E3CCB8E0CE1DC5782ADFE98EBA3BDC13D7EAC4097BB27B347AF980EF707AADBE75087132BB19CEF2B4ABFE8AA175C9B4BCF0A295CDDCD316462CC65D6D79B49ADD07917184AFDFC45C8EE054A1FE2AA77C1549B8401B5AA3FA01A497D3D5BC82EC465AFB710E3F534CE1A67B708ACCE984ECEF1B6954756CA3BE23EA64FA9D32F498145B82F9015683CE2C4B14EAB820ABE2FD0FF4B2D10FD53
	8D7B3F1D73DD62389E0E3BC27BA438EE4929F8B87EDAEA9741B70BC4A1623FCD6D1A20B83B0A1B2B437528386B95322F4010BD7EA04A4ED7C84D4BA549B2EBFF0F72C8BE58E638BFEE26B6CA81074E6EC59C574367621D72687959DE2E1F9F75B7E506F1B3757A7D6D8C658E98465B547B55E3748C8C08117BD5AA45DF96A377AB510D7BD59C5056087CEFD6435EF62D2E64C227EBBAE74B7A8B66F11CBDB1F59C3FB1E19C47459351F13CF542B80E5F1B5047B11B53CD2E68F95E66524FB343DDA7C573FDDB0A0E
	074746847EBDD577DD9C7B666FF4DFFF23BA58188341E8BF0C87237D75BB1A8BCCC2EC4439706DE0F88C5F8E66F15CA90ACB07F6A6475D417984B762DE6CC51C15639EEEC2DC82477D126671EB21ADF890E72DC13EA20EEB2039FDBF34E50E3B718A72CDF25C27B496D6C2BB0563663B10AF1D636E22F9E72D84B92B04387778DB09D542BC567189D9D7128FD29E07CC37C4065FBE79160647ADD11D577FF0EBF1741C6E7B287B1B0F29595F288E66278B1E68187A52230FA9D67759BA967E2671AD7227A46A1B46
	25B12CB1A5C8D3570717828F81FC53D8A9C5561886A3F45DFED153F5BF57355AC4173E1E1581BC99BADDD9BADD2CBF218F7BAB98B17EED0C3E4F6D40D3B5427AA331E399096CAF3D897B331A742787A770CC6A7A1BB392690B1C1AF0DF2C3A247942DD033A62CDF4695FB797838F812CCFA6C6696A3C922E6B57DFEA3A16DDC1DDC9A63A746F1BE9401326531599252B55952E2B77ABCD571DAE54A50C9A6B523F1115G8F8154D5BE9A296BE1F7382E2F3E56F455707AE82D09AE7D5BDFB370B46BF4756BF431FB
	6DED3CDE62990DD50BCC38A2F019D739CCC2CC199F8B1ACCF59F4A24984A985D1F311F1FE7EB7DEC35E0BF45725576C3FF46257693DF037DB49B7603B295F91A4C0B3C46B3AB26CCFB3EA6739C2F81AD0BA9C3D393D5E618579A5646147989C5313DF7C958DD2D5C1BDC58601D4E5D7035B0B9AB48B827A5F720CFAFB789B2F326A5773F963DDC8248AC1D165C5F556865D602CC6A3464DE8A3B632D8519BC5F346E4CE1B9F7A948D4CCCB4E9316739E85995B94F26C8C7B773168F3ADE9ACBAD75AEC10D33DE310
	D33575C573F574C573D5DB22798ED822791656C4737DCBCDB4DF3E904D7706E01073996452396EE83EAB86677D6C30731E7DBFF7DE75FE1483E746FF89790A3015BDD916F5082E4AB27ED62A885B74F86D4FB095A11F3E8FC5B0B4366DBBE63592BE85561B1E20BEF66E8329D0D058487FF26E943DBD26DD58DF1F7B7C4D39B9C25EA961F5F68BB9A564112B44A9A11F5EB4CE8939F08B19A47F770BBCBD0446042F595F0F7CA9300AD05266182CACBBB694724B630AD8F6BC9874876B915DA19277DC5050391515
	15517F7D535A7C07E355773FDDD7746F2F6B2E9DB67C1BFF8216B136FE6DE35A7B9F2B655ECD6FAD73G3FE48CEB379B82FB85737AFD2757A14B0AC39EBD66F312DAFE63C5C5545E251687C87FA164D1090C76E37A7B69911D4C7F83D0CB8788B06B2D782297GG48CAGGD0CB818294G94G88G88GF1FBB0B6B06B2D782297GG48CAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5C97GGGG
**end of data**/
}
}
