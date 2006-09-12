package cbit.vcell.client.desktop.testingframework;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.AddTestSuiteOP;
import cbit.vcell.numericstest.AddTestCasesOP;
import cbit.vcell.client.PopupGenerator;

/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 1:12:40 PM)
 * @author: Anuradha Lakshminarayana
 */
 
public class AddTestSuitePanel extends javax.swing.JPanel {
	private javax.swing.JLabel ivjVersionLabel = null;
	private javax.swing.JTextField ivjVersionTextField = null;
	private cbit.vcell.client.TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private javax.swing.JButton ivjApplyButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JTextField ivjNumericsVersionTextField = null;
	private javax.swing.JLabel ivjNumVersLabel = null;
	private javax.swing.JLabel ivjVcellVersLabel = null;
	private javax.swing.JTextField ivjVCellVerTextField = null;
	private cbit.vcell.numericstest.TestSuiteInfoNew fieldTestSuiteInfo = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddTestSuitePanel.this.getApplyButton()) 
				connEtoC2(e);
		};
	};

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
private void applyTestSuite() {
	String testSuiteVersionID = getVersionTextField().getText();
	String vCellBuildVersion = getVCellVerTextField().getText();
	String numericsBuildVersion = getNumericsVersionTextField().getText();
	if (testSuiteVersionID == null || testSuiteVersionID.length() == 0 || 
		vCellBuildVersion == null || vCellBuildVersion.length() == 0 || 
		numericsBuildVersion == null || numericsBuildVersion.length() == 0) {
		PopupGenerator.showErrorDialog("TestSuite must have Version no./VCell Version no./Numerics Version no.");
		return;
	}

	TestSuiteInfoNew newTSInfo = new TestSuiteInfoNew(null, testSuiteVersionID, vCellBuildVersion, numericsBuildVersion, null);
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
public cbit.vcell.numericstest.TestSuiteInfoNew getTestSuiteInfo() {
	return fieldTestSuiteInfo;
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
	// user code begin {1}
	// user code end
	getApplyButton().addActionListener(ivjEventHandler);
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
		setSize(468, 167);

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
public void resetTextFields(boolean letUserAddTestCase) {
	getVersionTextField().setText("");
	getVCellVerTextField().setText("");
	getNumericsVersionTextField().setText("");
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
public void setTestSuiteInfo(cbit.vcell.numericstest.TestSuiteInfoNew testSuiteInfo) {
	cbit.vcell.numericstest.TestSuiteInfoNew oldValue = fieldTestSuiteInfo;
	fieldTestSuiteInfo = testSuiteInfo;
	firePropertyChange("testSuiteInfo", oldValue, testSuiteInfo);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD4D45719E8D0B1D244848D9A5A60CFBDA4A5AD3BB5DB4F4626B4CBDB1A10566618441E129E1A3ADBF3B6493AA76E891B4D6EDE7E9403C60CA6528DE9E823D9CC8DC12589075A482FB200CAF5B2CC9250F9B37318F9B24CCCE69EB2430F6C775D7B6E1BB7B3EFGBD3A1C731D776EF76F775D6FFE777B3B778D6440BB69E5C5458788A9AAA34A5F47C50444B592722B744E87B9AE59ED2AA01A3F5F
	836CA33609D2F826C3DF63DAD3C5A599F015C3FB9D34DFD9E32AB8886F7B0955F572A3F809B14F8168731C7EF1427473F4FC426759C95B87833B61799A40FA406133540373FF9828D270798A1EC3D1B1A15B69182EC035026B043685A08BA04743466743F3971DF9DED323B22EFFC986597E082FF88F72517064F0524BE8AD4A33063CDCD47EA54E6527F2A2CDAA74ED844045171021432DC857BB647844E615643A2BB651652A3339A451AD5759C57F106C71564922DF165C0EC11FB0AC0EFAFCC3CDCDADF63B91
	1097C7A4D96C955C220B10F8BA4F0301F24A17109EEC7B9057EB4739F321DDGD028628F0A08B7423B8BA05E47577B177FBD292C47BC7E9D32457103DDFBE32CB74D475619638B2D3766FDEB49EF695E4CBB05FBC07C260AC3G0DG7DG93GE9GEFD11BC87C64FDF836D865B64F7030475D6275363A6C3E339E3B6802F7D828D5D6F097C41B47E7A7046DFBB0F0D4C71E690051AD38EEBA26130C935C86B2AFFC1364FFFBD971CE9D1D64E70C972F73B3DD6479C3BAE1F3E7471C7BB9FF380D064F5DC734F313
	E70F6FAC53193B7A4963BB094C789C10C3F3374B3857B954869A603DC8318F067F8945A7E8704CDECD940F6DD468FBC56636E1FE0D4B52D33A0AD49E3BCBD78F298FA796DBC6188C0111C8D996D13BDBB392A94BD20ADFB792A94B5BA217A5877AEA2F1AAA18FDADEFC17FCD04F686C0B640E600C2008323B0EE145B58931FFCB8CB9B6B95FCF26B0864320BBE42E813CE3D89CFB1A07B84035BAF8BEE1BE892FC12E0F509FE220C396B946A409AC617C6ED5F86F31C915D22CF10A50F9B3803834B224F4F46FC7D
	54BE9C6393E92FD19A860F96063D04733D7F54FEF83A843F7C2957AE48E2E8846BFF60946E49D541849C01813CB33D7C30957555896B37GF8D5BD54B7A33FEE5187712229294763999A719AF1A5A4FB94757C940D9D71701EB64AF57CD89B62CA83260A6AG6763E8FEDD4327DD32217802AF48FD6C91BB46687AG3719974C5CE65A775CCF0EBE31D457E61241FEFB834CD61C01C85F59C4F922DC2C5DEFB7DF279FCF877501F07FE3F30C5C54B9CAA25ACC3FFF6E403DCA0679B38172D49D5F5FDC43EC36D570
	0B5CCA4F7B4040C4FBC863CC4EA5E229421F356F266DAD812E2BF7C5F38C99EE8630B9CE5E52B918CED637E09E922FC2EAEC973D225B8EAFFE256F3E9634B545CF87BDAACD9645FB412F5B40117CAA7E6FDA507784D976C95691D93CA84A18E1C37D8F51397CA3D6EFA8A47039FE50027157B38AFE2D0EFF2485FD4266F13BC53AB9AA09521FC7FDD2F40943A0F41BE744ADB37C632D38D7C3E2D00989CF366EA068532E910CAF2CC648923090727A24E158F28CD124BF087E7A02DA63A06C869C56BA7570EC8FF2
	5F9BFD177B5E2131FBC976E5EF31DE6EC87D7A6262B5C166836B03A15FD3E4EC5317FDB30D8D8A5E4FE46D2AC53D6A14A70A3D4B271157E61863B90052B1EE6F46F66C67B1C5B174B60F4B4554296A3925239277C57CF2C404D00C412BDB55DDE7476B91FDA6C50F2C3F3783756852443DF5E32847BC1157015550EEGB00EB1DD260CB1DD30353DF88475CDEB35AB42D5A1D0679F851BE9EA6A22CBE47517B9D82E0CA7633C4E0A1F40B5B7C23B9740B94E7766E7FBF81D15B214C1721FBD36B3D6DD99B74E64D8
	B39E5A1BF57F523203697421B3B86796685BBE4E7579E807B65637787D9E1BC4530C220B31535A3DE33829532F42F3D07279E583B8D7006BED5EE91497731002D672723F513C0CEBAD1FB0D5B483EC1EC0FC3A6500C676G605281F2GCAA83F7E416368FBF4E36B5823D7748DFAFC43223D96B5DBA70C4AF5544F6BDAE8F7873E9F617B70B4213E6B753A022A15559EB191DEAF2D5A5F2C4C1D774DD564645425E23D7AB5E12F3338E6126933F3B224574EBB2CA57DGAC2E8D84A3EB361449D02E48180C2E5BD8
	ADD3C877FC43A42BE54AD4FC49A462B7EA70CC1737CFF0DDEE013E4EEBBC7F1EB5204DE5C2BB9720G209020EA8A6A69A93E57FB4E1CA0B354B786B7149DEE41F5B1AC20A97B7B8FE7CAD88CD25ABFBAD346E39061717DBF44C8BD982742576E1D524B213B86A3696AA27599C14776F86F455F6A7BDA6B08AC53F840E2604716489808B5C2A6704C86E0BE7914057B6466A96EFB1327353E37E16FE4F1C58C5F2B0CE372554705EC645030F1A75B1F173AB0F6F821AF91E08DC0B64096GB2874EBDF3789E7D3573
	AFB36D91B5F223278DF01D90235AA2F3895033BACD26B1269DF81B81986E2F63081B00F64A9C1E777FEB82F16B211D2762F6CDA22EE08E73C906CB1E182E4EC1FA7E1EB7C77F1CB2F3FDF06F34F3E8EB06326B649F2A8FCC0658754100C13FBE3099746B8349300F61010776D4407B3FB4CCDFBF848DFA75435BF42DBFFEF3AF656D770BFE3A696D02ACA8FD3F115FA0BAF544B9B8B8A8FA5ACF0E8F6ED246F4481EB60C254C4EBD2A1D174E6539671FAFE3DCB989EDA300EBAE5F73FC0B5ED9B4FEAE5B5F543951
	7B4C6C7DDA8F4676CC684F85A8GA884283A0D5B795E2E99E3D1C7E01AD8D4549DFB8F4B3B75777035EE4C1BFE4F084FA6C66E539B5DD5EADF48238C32B80CAAE5E3FEC769E5416790237679B025E7FD31686B3AE359825B2FC5C12C69DCC250B3A2C38E3F8D77E5D510472DC6E8778284G92EFE3710B45403F9945180437BE406E22D7341138F824F50EF35A67E0BC81DC8DC0B300A51E67D8319D7719D5A64794B91E7CD388F7DC091FFF1E540B1F3940A7BF1E5B0A9CE5AB5B63C376D1BEAFD68E3CECC15B2B
	1E97F94E17A87E683C48F37E3FDE66677C135017BE0F47369729DD174E07F37DFC0E7B1D1D473B1A795C76667744361D2F74685B4E1DBD7A7E3F3CE7FA7FDF5993EB4F59DA9E89727B3EF610EFE2BE5F53E3964C97EADE43CA2729690CCF32378A0E9EEAA14C17DF9FABD768574D673E5CBC06BC1F03F66982B80FAF6039AC709E4FE519EBD7110D7F28DFC79698A70B9B96303DB2AF88653206772DA5EC4E24F1BEE78A7467ADC05C06F1EDAD07FBF33E0865F0567FC2D8BF6AE48E517657CCE87BD15EFBCB347D
	97427AD1C7638ABDDB5B2307795ADEF9B40B389E080FF977FAA0012D29D1F9567CE4F9D1E5EA6F8E36B67FA40F0D5E842E4F86876AB3835A59G1B9338EDAD2276368D5A258BB92E4C4A6D2DF6A1778B5102E737005A7650B61E9FD87B254BB76B7EG67504F2B15161BB5075E391D450DD785DC178C4E133C906F64DF3E08BB33E9A16A65CF762AE9F8B299B7C65550332B99FEF903F487472E170E45255747E93C3A1D4525A2957F964557EA704C47CF0FF19FEF003E545BF94D7E3491F5728A342B9641F99C20
	934089903F08472A0F4F561399722451636D01E06652049F363EBB47EF6C8E7B7BBB39AF2D7B7B7B487A796DB1EB5E54C54ADDGFF66ADAF42A7DB1F57046B5B8AFD2509608FG4DGC6GD7A2DF5F545923ECFD9DF6C9EE11E5416624A7CD3EB813A40E0650185C585A821311EB0BCB8C59FECAE22CDC1349FCB9116D519E957FA04567E970EC6F1AA89E5B5B202F7FABBCEF2E1D0C4E556B54B3C6AE0CABGA8CF02781B44EA6809A49CD3EDEDD746B4A4C54B48739C07CE98538F70065276A605EC0B49E1AA45
	3DE941B32EF262E1FB7853AF345F22AAD7380AB9DD641CB90A9C9B13C2311E71489D5046541ABF1E570D290583E345250B99ED6D62908FEFFF43AE36D7D5982A951F7B3D663DE76CE65E1B6A7B38F8CB67E0B67CCF4658754473C67DFA62C523FEBD716F46694F8BC51F463EEFAC7FD4FB5F48FC30590CF9339F76A5G108610851033187B5F32CF7FC0287F6978DE075D0135B0FE4D1055730AA976DA63CCB3FF83D9E8423BA32B87CCF59879F37CE2936E176C71D2A4432518D0C7BE49611459D0853F4204B956
	A58ED2B44B3B0BA7F9AEAED84CF3F14E944F452549BC577E2D83FD5488ED97C082C0BA408600FCF54C7769983284EAC600E6G8BC09CC07A92BE2659E109308564B7133D24ABE372B5E3931479A3479AD5F93E081AEBB6E75FDD4AF3CF04CFD4680C8D555679961E2B32E12D05CBF8BDFA62F858BD8B2BC7CF3BBC23BDCA88D44E160784DE9B565F416B278B8272F4C2BB8EA0758EFE5FF2564AEB2DAC403DC3779664A07CEF5BA9BD9C2BAD9ABEA704672BDF6D66F12A7A9B2BC97BD36D456F927DD837758E2576
	3BB3942762A1CEB1DF297D0228B676FE5FE1F81A04ABCA4645746471F14F6882951C37DE01C31ED1F0283EF62A9F6D468B3C938132GF2817281365F497D4D53D79B0BAFA019B2294F7984095F1FF71AE37B5CF9734CBEE7B653BBDC7095E3502B1CC5874C183FBC0303FED175ED2B39D245997C3D9E3F04E7D8165B3E2B1E7F4AD358B934861E8DGF8C744F25BAF26F8EE6BC9514FED5A7CE601B1F2CAA837A524C46636CD2F215DA8DFADFB842B68E278399FA1BF937E8282BBE98F27F9530AB467C606559E06
	FF79E8E508179174F5DA92DDF65637FF92E32E290D71D3BBD5B99E2635AB7094FD124D9F49035DE53F64501ECFD2943D2CCF61F75D0522DEFF1E5A7F1EAEFD015A3F2CCB5B3FDD692FDE4A7B7F502757DF2F769F326A7537AF65B166995DFB1E19736B369B24BBA25CD86D7DC39ACB4CCBA3EB6F1FD23CFDE9E46DED96F8BC73C25FA6F53D972C5A799B1719AAFA16CD9F3F190F2F9EC09B3740588900F40075GF9GDBG8A81F65F853E815088508BE08788GA4822481E481645C4563C17073A353448365409F
	8A88EAFC6A6C1FA6A6744FA2A6746B6761CB7D51F958562F1F0725FEED9EE67A3E7B9A5777E6D8E3E5AA0F753F3E4673C2E7AA4F8B7B26F0EC9C34D3813254313B894F8B3929571B9726AE9322FE079B0C719D0E4685B6D71D33DC31C9321C4F7F61A047B52A38818747F917731CF83836DA1F3F9A9B986DE3DD7C37D699AA3F4F7AB82EC045FDE165382A955CCE7384227A4C931A77CB422D2F1DC7EF699C2CC613681AFBD7B03FE5B8B745B9959C33356F8C4436770786E636779F8D685B7BE38351767E78003E
	3DEF9E505ABB43BDBD003660GBFB52B631ED1F11F29385FD01E086B136C32D345BFCF65C2FC2708932A6783AA57A8451995DC21867719027B4F814C6330E4274747E90BAF61BD4C2064F231760ECBB8C6F2C3DAD775D1F189E5143C02C66ECA957719EA83AB2F5DD8FC7F568D52ED3DEEBA967F6751D8923FA2B2AFA4D1FC720A483CF0F00A4729B468A3F7F3FF5BC734735B819F38FB7A3C4068CEB835F4098ACD3A72DC27430351B504FD774CD54665AB4FEDB1692C8EADDDE99A9BD725BC0F264522FB3CEBBA
	7DB647243B54B79D1DB1A61D45BA9D1DD3070E45D661BD5C9F498DA5286022AE43705501BDA1BCF799E52EC5D3C4FDCF513C3FBDF56BE365C737F48E9697EBC9F42C7C00E8E3A543F5511CB6813ACDC963E761B40A4B0076FA95D7A47173F10E0A6B26396F40D7CD95C73F4AF1A51436995A3DAA4E6964347DAAAEC8F3E4A634F3D45C239437995ADBD5DC3DCA3BDD45BD5B45F16DDF633811BE75DB350A9BD56F5737FC0D5F4589CE0C2102CBF238717E82B6A2F08F15074E53G6F1D7770315FF6065FD1FF4B02
	F6424EAFD592BF3FEC38E766734BC69833659EF6FEC1FCA13CFB08767CE29D5417EB0C6A38B45DD4D1154E657A1F88395E3106641AF2F239BA53E73EB36C07B188DCAEAF3C1F8813AB5921AFD7865D2B89981F2C4A750CA3DC2ED6DBC82EB32ADC59B164AA5348358946EC524835B5BD941399BF524D79ED5761D79231567215F07ED9497861BC75ABA379A576F1FEA7D76A7353C2AF0C31EB78852278A529F73771B178E9571B8AE3B2B57C32B57C746AD7E5EF1CF89F277E38294DB36C75387187CE978CE7BA0D
	17DBFA8CE74E1D6DB8E764F79F69235AFB5E46D7676A7EEE8FA1CF1139C0F9A67E7772A2D6F79AA4DEEFD72D522B377564051CCA6DA1532E5F4F5733E4F0366BE132E4B939AC3DB1E4E173EE91271F77FB0E6B1BB7204E1BB06D3CBB713AC721B9564DE932F5E9744EA91CCF33B344673BFD1CCF61F57331C9A1BE5FD36F536AD7DFAF1F3706701D1DF1F307E8CE819E712B79B9F5016D0FA476B9355767710ABEB94830745714CADEBCA7E9737F9A6037FE75EC6A2987426A29BC20A918955DE4985DF620A9DFB3
	9B3A0F3A35F455C0D3BFAB3A07436A22F620B14F0AAEA72CAEF281CD5C8CF44AF9E5885F556F83CC55CA5F26A13C63D1EAA3E90C7E0C73A28CF4BBD86E9425687A610A94DDBF64B923477D4C99BDAEC5E75CF21DF1E73B2347DD680E9E77733E68F13F6C0B9E778B1DB3E3C15819117D9F5913E39FC0A373F9D2A613B27A094C6FA3FB5518E0208722022BA71ACAFE3D3A2418149A488EF6C0B410D2BC6599C899BD849AC8899C9083788F9725DA70FFC92E51D09B0615C905F2C5D8F34ED3A713FD74E231A68CF7
	00DFA6EA517A6C7EFF31046715A3D35A3CD2B84F96B32FCC2CE5F61B3CB6F4274E624867741BCC9E60372E65F1643E098FC86CFB750B43024BA58B2E2196379D6F58DB0332987A76F5F992797B79982513758DCE225CD7B4B47F87D0CB8788014797C07192GG28B8GGD0CB818294G94G88G88G6D0171B4014797C07192GG28B8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAB93GGGG
**end of data**/
}
}