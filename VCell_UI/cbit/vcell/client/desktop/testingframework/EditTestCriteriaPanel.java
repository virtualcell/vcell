package cbit.vcell.client.desktop.testingframework;

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.simulation.SimulationInfo;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.numericstest.*;
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
	private javax.swing.JPanel ivjJPanel2 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField ivjAbsErrTextField = null;
	private javax.swing.JTextField ivjRelErrTextField = null;
	private cbit.vcell.client.TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private java.lang.String fieldSolutionType = null;
	private cbit.vcell.numericstest.TestCriteriaNew fieldExistingTestCriteria = null;
	private cbit.vcell.numericstest.TestCriteriaNew fieldNewTestCriteria = null;
	private cbit.vcell.simulation.SimulationInfo fieldReferenceSimInfo = null;
	private org.vcell.util.document.MathModelInfo fieldReferenceMathModelInfo = null;
	private javax.swing.JButton ivjSelectRefBMAppJButton = null;
	private javax.swing.JButton ivjSelectRefSimJButton = null;
	private org.vcell.util.document.BioModelInfo ivjbioModelInfo = null;
	private javax.swing.JLabel ivjBioModelAppLabel = null;
	private cbit.vcell.simulation.SimulationInfo ivjbmAppSimInfo = null;
	private javax.swing.JLabel ivjBmAppSimLabel = null;
	private String ivjappName = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == EditTestCriteriaPanel.this.getRefMathModelButton()) 
				connEtoC1(e);
			if (e.getSource() == EditTestCriteriaPanel.this.getRefSimButton()) 
				connEtoC2(e);
			if (e.getSource() == EditTestCriteriaPanel.this.getApplyButton()) 
				connEtoC3(e);
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
private void applyTestCriteriaInfo(java.awt.event.ActionEvent actionEvent) {
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
 * connEtoC3:  (ApplyButton.action.actionPerformed(java.awt.event.ActionEvent) --> EditTestCriteriaPanel.applyTestCriteriaInfo(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.applyTestCriteriaInfo(arg1);
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
private cbit.vcell.simulation.SimulationInfo getbmAppSimInfo() {
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.FlowLayout());
			getJPanel2().add(getApplyButton(), getApplyButton().getName());
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
 * Gets the newTestCriteria property (cbit.vcell.numericstest.TestCriteriaNew) value.
 * @return The newTestCriteria property value.
 * @see #setNewTestCriteria
 */
public cbit.vcell.numericstest.TestCriteriaNew getNewTestCriteria() {
	return fieldNewTestCriteria;
}
/**
 * Gets the referenceMathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The referenceMathModelInfo property value.
 * @see #setReferenceMathModelInfo
 */
public org.vcell.util.document.MathModelInfo getReferenceMathModelInfo() {
	return fieldReferenceMathModelInfo;
}
/**
 * Gets the referenceSimInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @return The referenceSimInfo property value.
 * @see #setReferenceSimInfo
 */
public cbit.vcell.simulation.SimulationInfo getReferenceSimInfo() {
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
	getApplyButton().addActionListener(ivjEventHandler);
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

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 1;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);
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
	org.vcell.util.document.BioModelInfo bmInfo = getTestingFrameworkWindowManager().selectBioModelInfo();
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
private void setbioModelInfo(org.vcell.util.document.BioModelInfo newValue) {
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
private void setbmAppSimInfo(cbit.vcell.simulation.SimulationInfo newValue) {
	if (ivjbmAppSimInfo != newValue) {
		try {
			cbit.vcell.simulation.SimulationInfo oldValue = getbmAppSimInfo();
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
public void setReferenceMathModelInfo(org.vcell.util.document.MathModelInfo referenceMathModelInfo) {
	org.vcell.util.document.MathModelInfo oldValue = fieldReferenceMathModelInfo;
	fieldReferenceMathModelInfo = referenceMathModelInfo;
	firePropertyChange("referenceMathModelInfo", oldValue, referenceMathModelInfo);
}
/**
 * Sets the referenceSimInfo property (cbit.vcell.solver.SimulationInfo) value.
 * @param referenceSimInfo The new value for the property.
 * @see #getReferenceSimInfo
 */
public void setReferenceSimInfo(cbit.vcell.simulation.SimulationInfo referenceSimInfo) {
	cbit.vcell.simulation.SimulationInfo oldValue = fieldReferenceSimInfo;
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
	D0CB838494G88G88G6E0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D5D5367444A2C3A3A57FE2C67227A1A5C323E27AFC5F23F9B60FC63E321719FD59B7B44FCA5F700A9C5F0B57674CBB8286068AA6BA740612192C34B172073E6111D328202097C138927E241CFB6F81AE1C7B633D07BFC1FDEB6FFD56BE675EFB6E85199A3FEFF54ED9BFFB2D3D76DAFB2F3D77A521E8D76C468DD9C50230E123207E7B4B86C108BDA9880B46BC77A062221CAD79026E5F9F8136
	893B3BF260998B345624167CC261DDA18F3E9361FB4FA3AD793F07776D4225331DA760A5081EAA205D7642413650FAA6D6231EDC7ABDF8E2B3BC3782A4810EBCF3DCC47F249345AAFE250AC75810A5881914E77E099295D78B5FEDG09GA9AE461F864FCDD473584A8A156F62DDF1423ADFF8323610F6F4EDA2D439192CCDFD168AEFEF58FC0568FA1E5AC9E4E200368C00F4BEDB9848BB8E4F430D5D9DDFDBADF6252C57AA49F219D536CBCE254CA6F93B95173BCC113C0A5D59516E919DD21F4B53DDD9D9E333
	ABE6409E73589549E3979BC527A48BC28455352BB60F36AD880D70DD64A1384BB622BF953ED7812C6778896D845F8A6FEE00888F76792F1F9ED67B54BA7828302A6309CD85C17A9C6BE1FDCD71E8FDAE7D4C127DBE9D1FFF2DA163106305B8GB88C50869086908B70879A97AFD4FF864FA39665184B61F0B90F385DC7E51B275EE513E4F82F2C840A0AEB122CAE0FCD9058583FF4E21F01BD2100497D7A266518CF628EE19C44B74D97D6BEB4B9AB5740A7AB638633123C4C97CB3D1ACF186E5D3541F4E7F8FD63
	54D7773702DE373066E06EC6835DA54B8F6666A82C1DE2C553EDD648D8BFCBE320825EB7286DB2FC9A45CF5561D94CCA94CF3E9301D65C0331513A95EDB1654C948A8FCCB17443541FC5E68D75B09B22FB7CED496AA0EDA777B01D795C16C20ACF536119AD51B4CE6904505B12EF83E07175A24D619478DE85309E20282FA53F9C2091405607B176271AAFC698E30D22C7B95AE317ED12C7E032AF566C0427542FF844BA27D7911DD629C50424334812D7D0F9D655909FD8FC6456505837020E9312D372080A5D65
	0456A109A1E73D0CE7ED4DB6426311A855ECF7C0D20B8E37006D3EDD339D1E3268D54E3AED22A2E99C0C1EF51C0CC92F5882ADC283704E7C12D7C77C958E7D0F810867FE38F61C34F7CA7240FCD1D9E9F2393AFB5CE65293E1FD9F7173D7F46EC8067795FD68631C1384D7558FF3FBBF3613F87CBDDDBB55F6ABB1DF748CE00E3DE0ABA4F683FF94C09CC0F2BF464F6B2D98BF55DB9E9076BDF737E17CC4C1ACAF6BE7F1135E6F1FC72F5276090DC583B05EG6681DC933AED2D86F9A4777B66D7145FF7DC3FF17E
	690174016988B754116D773D516FBBF71876B15F195FBD274898AA502FA8000481747D9B47CBD9AC9F953D92C6EF038782CF32E9A3416C4C6BA8D2FD1B2D5AC35A7FC040F15AC969A950F686C07155167CAA004E2B584F696D2DFE72E863C8664D8F0621B39D8FD19D58FE6ECD6A489F064EFC764A8932FEC93D302CD6CBEE49E903972FCACBBFC1F429795D6E62B2EBA95E8D7341B1C8C0AF47E71EA0B9ABAA0A47EE69D124E6C9A12B33C697EA09AEEF0F452DCDA528AB3716745155877381675FDCCBF2496AF2
	BAA5BAF8DCC425EF2539AC4912830CBE666AF1AA8CDFD4C7EA21EEE9C01DCB0A6B5E9148DCA07710F909559789A2710F5BE3F7C0C81029CDC83BCA723C2936D86723A84011BAA9911EA9D7B1CF7BBE42BC5DFD75FEA17932BB4BE84D19BA674E2CBD03ACF78E8FEAF94AEC5AF452587682BA2728F8AF33B52BFB87BC0314B6B4BE368B3D1D24BD0B5DC527EEB2DC8CFF2E04629D108E4DF607060FECA371A43A5D27E15AD573EA1E0970DE84DB5DG910318D7F71CA4F41C535404BA6612E5B6ACC0CB9FA43E5BA7
	9529FD0F056F24C1663B2D1CBE13534B07DA72CDC37E7462F6240781ED2A0E4E6C68BFC566B62FF425C7022506CC4E2778985FB8751E9F2DC59DEFC63F6D9499EFD9B72FAB57084ED39DD86BAE82FD4BG5629FA4B2FB15D0C2F2093791261FB45B50DAF53076F1FB9DF79F56863F50DCF3E2E676B33A3DFBC6097832C3A4E78F65F5073FD5505FC55003FF8C3639BD279D80C7CE6AF09DBDAAFF7093DE2FF19378FF22F3272A48D93D67FAE22EB98611FFA836B5C55F4AEDD8A5F2B8104B00C71E736E01D9B5D9D
	A72CDCF3A0B7D8EDDF9C2646F818966309EF9CF9074554FF56931DC3C00B8C43F84AB269575AA3DE2F4BEA274B3CBAD68BCC7A9CE038C7CC6F42335D6E71AAF5B0C9756338AEB291FBC99D2022553A28D942FDC5AC68DD91067B0AE31BBF9182779510B78E12B71515C7F59904B52675907AA26A6A9C216D6E69D979C1FCD1F88B73417EDBB4DF6C3B1E39115974A437299368313720CDE3C5A31B3CAE39D772D4D6C25A764854BD4CA65656B76D58568AE827709F302D674E6D5447C119AC12A8E806A959590132
	8A2DA589FFAD480581940FA178F9F2112EAFC900CF8348819A1BB3DE6CB8C8668C1A74E56C51A8F95ADD9E07E45BC33509FDCA99DDCB4A0ED0F28DF95F0BB1BA49F20066A819B71A2476FAD169243E5EE38AAE7C555E968147E16676AA55365479B30543B7AEE5996D3DA28ABA335A46B07FC70C514621F622A57BA2G73C1C5A77AE09970EC8EC79FFCEAB372C1F5B87A377864977ABE28F36AF0BDE0BA53394ED6E8AF0A6B6C317A6BCC81DABA57F939221C4D557200FE819801E226B7560EFA0531AD7995E3D1
	6F5CCBC6FDF50F45714E6D7A5B463BEBE89B775551FA36826D2163496C32F521DD6120F39137EB0945482EF5E3519F0776EE75ED17B64AEA2647837611F937EA75EB492D0135AC5BDFDD24EB5F7EDB597EEAA347F7D0FC358E4FECFF7D949FCB20AD3A957784298DE49EAE3D8D7674GE6G99A092A07EB61C7F2A6B8F89436C396A1CB044CED1EE76A916F06FD3CF46876A9B757BF775448EDA5F886807F352684EGBAEEDA0E757B4099DAAFC39FD7812C87A80A00B5B4827BBC66743E617A0CFBC735334C1E27
	C7590F17C6A9D753BEBA3966D14ADD6D7017B3C77846285B6F1B49C58448FD68779D6DA747F22720190C17C11DF034C7D1E8AD476A6099A67FBA186CA963214DE4GD6CF7CCA46FAE2C584568D570E6B6B06C58557022F15372BEB656D5A9C3D5BE14EE57174EC9329FB5CC00B84188D108C308AC09887B552B80C2B25277FBADCDC51C9496CBA8638DA287F0E796FA738CF3F6E9C5DD81882647C4722FA1C2F9C735FA59B7A6F62B85CD72E9D258D02FD58F87033010D7312B643F1865E64F1B84E71D6BE4E5C4E
	7B82740D4C4E44G39B27F38384348D7AFDFC29B765C01B6F4F50D4E067EGB9FF9B5A8CED4862FE6887BABB97DAC1EB6A8478DE4CF18F53BD5CEA7896A251D68B2DE9F743F78547B9A8AECDDD6F99EE5DA923739384A20F6F951101EB5948661CAD26D0BA74E7B57E67B4A38116174FB584BFD7D859E0FC2E702B8663F305558D5B989E5A501FC2A2BD23A17439C3E6C370F3077FBB935C4E2AB346F69EBFE3EC2769CCE8BB4DE702597981757B13BB8B283C57ABF96924D5ADAA22CAFBCDA1E7G81E7A1F4770E
	E7G8BBAB629BCB50A6B58819E374F1BB0EECD11386F7BF1B7BD87056F9800C49E132F49C66BD82A9AEBE986B147666B73E6327E2F83FA6178167CBDGD5GED63F11E7E7519E1EB1E1A7E90B54FCBCD4147E9D51371B87DFB935933FADDBD9E2B64BF96EFB695F31A36A254A9120338D42D33283CA2FABA2400315C50DC4CE941645FED8EB65EEC3CFABA7563F510672755E6B486E16355BB037101E3405A5BEB5737F7080FFF3608639FB79E47BF59C26F9360BB9320F0820E7F822B116EBD9354BD7E04E03ADF
	72517DA56F4B2B170C5AEBD45B3185ED6F5EAEFDFB653C3DDCC37B7A55F6A2033677C6ADB983144581D70F82B57C1D44876F54615A978BF2C9GE9G6BA630B51055CB8B7AC87DC2EE98E1681A5D12D5D8CDE5D3CF222C993EBB81A2G62GD26E447D531C2344E6F682F3C03563310FB45CC135CFABCD7EFD5A9F257523BAAA581E6512CC6FA823585E64FD0E77D2FC348E4F76AC33E94E136F25C00B398B572B5F51BA2EF3E2CBFE7FC444D99D380605CF449C36180367205DEC1C03AE3371DC59E38EBDD7DEB3
	874B9DE65FD5DABBAD835BD6F11BFFE9C31BB3A6E2BC67097E3E2D1A247953BCA918EF4B2D44D71D13188F33B97EEB0A6F576119EFFFCE7309FCC7GADFD925A14C2EB0646E8188B239177B937D366385F527AA68A3EE3B94EF2997962B96EC35A5EFA785EFCB76246D8112F98F04C1E32BAFE568438583BB11E8F34113ADABB9F029DFCE565890F5DF6D46CB0516CE0F3C6EFDD1EAA3F62EE1CB3E653B68BA7437C8ED0B599EB7E7ECF30660F3FEF26307495633342B473352C56494C5FC313351A3F7CB3CBB653
	3976A46ACC82FA46E402ABBD25BF8BA07E2D4FE2759A23B77950C94C5CAB686961BEF4E26F21ECBD7D919FBA7151372ABC6B5B120F31EF6B17A488724361C1FF63D0B1057569227AACFDEA5A0642290D6F30B17A8DDD03230196B78547EDCF830EDB6A940CD71B3C9F1EA5D3E16F8CE08268848887188D3098E0A540BA0022E9D0CB821827216CG15F543F794C0824052E96CCCBBE39ABB33AE1C4E4E1A49B972FEF8EF1C0E32F33B138D7AC4F461BB51E7D46F19D49EE24B6C294357246BC270046BE824DF0B55
	6FF29D3EC4FD6FB4E8C75D7B9B76E51B6EFDC7907DF847C6FE1733E518FE90589E02263FFFA3F3EE01AE4674710CF1E93CEE6DF0103BAC85A6D677F4723B1F57AF1028DCBD1D4E010EEA15B7F1FA20AF703CAADB059460A130C3252D1C4E66308DDC57DDB6DFDD1FFC27697AAF9B6A5AFF0F312EDC1D2EEA60A1003ADA6F51F431585FA5E2BE586E41FC18E641FC083887633252CAECDA413F2B69B739D7E15F2916D0FB191B0195867DE2BADE795EF4785F03EB637CCB2B2F6F6BAE915FE75CC37CD2EDAB9E264D
	6DC251E5DF793BAEEBE3B7F099472EA246F86C72F5E3E7829E82B8F6EDB17EF172251F2D0F37E93A26DBD1179CC457A61D2EB0608953690A5669E23EAF2F9B5D395A51D14A8D5E349C2B8BFA69FDEE2C4EFE063FC17171BABCDB1F677275B989E8A5BF42736359A2996BB0780E81C884C885C887487BB156E15B4F12F9BE64DE4A6CF21B20D013F525956B5FDBA7C7779B2F47F3F19DCCFCEC2E10F4EBF550F33D129F2BFB85751914BAED834E015B043FD8C97FE420C5G4483AC86D88110417BF7612CFAD6CC2E
	3C0FA80AE86D24B7017C2058AE75E9E8D2143338FC37BE30467FE3BD66CE4E8CD6631740F37F8CF62740E2761B860C595A9946B10B31CF60A27038E73018A55F61B37C7363973B73297D5E9A0F47A4DAA4D9457FFEA741B7C9329FBE7643824EEF062CBAEE17E41BCA1B72F581177127FDF37DBDCA53EE52709017519BCB37795119D624FFB63419511B6D8EA6497033CE67F33C5E4E17BFA66BDD336E6E8BCF0D99FD2DE3071ECE2FE634F3E546535EC76E70711695982882C67BFC97D9F30F2A3FD5C0DB591D50
	43BEF713D16A78A54E40BBA3CF1791BD05534F1E5353D72A741CD824DFBFE3C4AF667405066D6F637441B6A3FA95274B06F4B327AF970D681D31D8E3BFFDC17FDB5B4269F296461CFF3C8E452AF71F31DA0D4D6A6BF98EBC87D88434DD822BDF72846D9F1B4F4F13A3A5B56FDF5E0CF9DFB2EF16D07DFCF556C7C1742ED375D55F2B698D3FD83E09697ED4C25DC37706523D381D3C335C2D95B1F793C1A695E09DC05ECCF6DE4AF2B7410239DBBA53B8F733F5D08EBC35B3355C3DB853BFF75F267B7DA3642ED50D
	DC6607146F747B0052BFB7986E83B23F3B1A55295A91B9D373031B7BE16EE574C3724CD0FEB852C95E194C92FE6E1C99D2E60D1D3C33F9FE682476BED6771ED077FD55B2415750A7FFD09DEC0DF8C08CFE9611A89A1FC5BCA69A1FC5A40B214F0FD344606747CF0B7A5F2D31B56E27F42E58BF8B6AA8GB7C0B84054D9383EFDE77913C057B703352D4656C14EA3492FE99554DFE58D5E57935661FF035BE8A53F1D3138A01C9D24FD4437D849F8A9AEB7C52AEB2F157848E36F68D4982B0A97E98C4AD2BBC5339A
	66FF6A3106C9043E254F427B229327F19F3FFBB61E9D741C263FAD03EF85A0F2B6723A9BB036E74FB60A6D0CEF0CF0AC5EADE4F9405FBF3587797D93DDC6182EF4FEDF39126B3F3F8BF1C5F3700C6463BDA546ED7185D8BD2FB80FFFCFE01A036DBDFC86F1619C37066BD844F18B5B90370E63E6F15C3E7B90374502B85BFDD8A396B884BEAEEFDABF06E70B5825D60124E4F2F9B01ACE02798D16AED80F4DE2874F4F84073E860C01F6936E8B3DEFE7FAAB1DE42EDD8C3CE9GB9BF01DA8AE0BFC0AD40C5GB7C0
	B8C08CC0A2C08A40CA008CGA18EE281E0CF9C66418CE9EFB05B81299ECA51E47008F6128E2A7D7BFC6E768FC3FB0DF1216DE7716A96B1DEED409F9347FFCFF25067CE1C1D099D17DDFDA63514D46761A49163FAD99C46752B344D4D7737646F86B8FCBF0E5B4BFC5C4C775FECDC6FA4BFF3D7FD1E61A6F19D89ED448124822C824884A81C0BFE4C6B5893420F2C931A9B198F9F3C2C7761EEE82BE2EEE89FB23925BE676FA610E99BC60E5978F8D770B9AC25EB78B96C991AC3924CBD668137FA3F323C0B547B2E
	76F62F44674A973B8AB92E4E5B68725A493D8C33FD6369516D89370FD24EF453F2EC4F38064E154ADC7F3D64DA0A8F1B6B3F179CBC0DF19D89340C39F8B652532057EF1E8775643C110C711B06F700C32AEC14C19BCCEEFE403DBA0159AAFFD2D039374E0772D3EAD039474E0412CB8BAA773A211D6BD47E3C78E0F20F36994915287C7B034A45994AD52B7C2DC1655E33984949AAFF18019CBBC77B97DA67C74733B8E138D40A0BD7F1ACD7CEBB036764D96770B9F949E9DCD7581C01F5055DE9DCD7381C7A3A02
	6194A719B7BBE07EEF65FC839CF70E632E53B6896EDB3BCD696478F0EA974157CAC4A16247D13B7AA82ED5455D29431DD3F151CEB22FC017BBDD9E141D66A4E7DE6DF6D9E65FBF22BCF6A7CCB35C9FB3290DF63728337BA79CF70E0F45B1A9F0FCEAA57D7830716F8D383B9A593C93BECA39FF3CE9B9B6AF1D27F7DFA9717E7315156217457B4FD769FC9DCE83DA7583B8DF3D6A936FF100DF7440C856167FF6870FE3614A70F13C690AF19C97DC890C6322AB46F17C3FD774F14C467467178347F949E57DB8B35C
	EB56C03EF4EBE0BC3CF1DA606F193A77DBCE7F70FB3A69BF288E56674806C0BF5C5590682FBCBA17AD0558C8FBG6F8E9C7C3EBA1D631E20B5436179308F1B0F384F3A112F156336DEC0DC9C47BDF386F1199C37114A56BE88328FA22E0B37E7633827E89D1F885F0BB96E1008FCCBB96E1FA8DFE982545089085B4D798EF35CDEDACBC642F78C47CD60F7A5F189D8470A1F103C32BB216491E5BAA5B2FCC97F968D0FD322BA2EED96AD0E6C3A7735628F9FD3795F5B3DCA70187A2DA5B02632FCF20B1D7B6C723B
	E73B1DEC6755BB0DF0FE4F1619B07C79706685AD798470CE23F4017F99534AAEDFDD71DECD5711EE5435EF417067D9D540D32553E55669E26DFDFC815BEBB3E8AF5B2FCD85F8A25476C86C448434377A8C36979724BDBDAC821E253A76D6857822245B5797CFAB1AAFEE613E488822CBFF3F1933907624GF8BED93C50DF57ABED3E3A5676E83ACE0A28EB77C2E3DD7A7B4D8A60295069B28568DA6E276BDC1F266BAB2E2BB508AE7D9D198CBC32CED7D8002EAA0B2F2E21FECD579CFEBE9A99C4177E6EAF96F8E2
	F53A92F53A54738DFE061C3CB054D9640C81014BF83A7979E5C8191FDD55E4DAAE204C7E07C63AFF46F6DE99545AF93F895B693C69F6722EE96DAC64FD08B5EC87E57EFCDD1399643F3BDB91D266748DCDC666B2050921E4FAB7E8B24B79D9C3EDC819A732483B3AF772B9838A83396844116C8D7B3AFDEAB410D9B4A2398B9774F2CBC1E6550864BEEC524BE5024C6607C7A277280F1D25A0D3BEA239B01F9A3896E4AE0EC86E0A0F1C9BE442C7A47722CF4DBB95E4620711D36B9303BD5EF21FBD1EDAA7F48772
	CD6A8E6473DC88646B3B904877E5D3A0DFE5D3A05FD386FA1FB550BBC58C640B918379A28C78469B702DB5D86F5F326879587FF367FC67C7108D717FA1ECD4049D6C4A3234C9EAAFAB65F7250A30D30F577E8CD391B6527BA1BF062375DF9CE9ECA4FC0AD06CC3D0AFBB3FGA92FC7A860FFB9D7C7F70FE93D07AA337FEDD6F61610D3A73C43F6A1F5C28E59CA54899B69CE23CE4806DDC8BFB9964C5103770AF01D667D5F852B88C575E2FFD969910B57A577A8D2E90D476372D4A23AC9A26E69555059E5E5E501
	7F7D7B5A7C37E3557B3F3DB774772F19E32D06FF73CFA0A60965EFC212F67F474EF27FC063BF9D70F9BF45335B6E2E4F056067774D8ED11695D16EBE623411337C23830A245DCB15F613763D482392996D53EEE2F717CE667F81D0CB8788A2FFCF352097GG48CAGGD0CB818294G94G88G88G6E0171B4A2FFCF352097GG48CAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5A97GGGG
**end of data**/
}
}
