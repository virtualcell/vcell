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
	D0CB838494G88G88GF0FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBBEDD0DB5715CE6B6E7043C95C0C13F5DB4F8EF55CACC9E9CA26C96349FAA63443F4E9626EBA544E5246EE69585D629DA7CBDBEF16F559666A0398E2BB46062424F59B52608647D8E07C05815BFC088F01GA1B0968647125083E41E749469E1B07E5AF33F1E9E5213409EFB19B9735EBD771EF34FBD77FC5D7B84AA79B0B1DF27AFC1C8170F585FA99DC20BEA915AF2B075A79CD77BC9C6A1D27DFD84
	308F6DB498615981FDD91FE59496A1DD28G5AA350DEF5A923708F70DE0C5E8EB51C0417987314C0DF65576B6D7167F92D194F33133437DE5B8D4FF7811C00434F64CF487C33258C3F1861B96874882D2840E38E4C9EE0389C98D3GB88700D8C147AF06A79560CBF535EC5C481713507617027ABD180F0AA707AD0C366090FD16219D3A14C3F82E56595D0C26915AB3GF87186B4F930991E8D3649713323EE2FEC3ABAAA0822E9D4748AFE596491C2133294B049C2C8767A474702AE1FB0AD85A76B6A1ABC1E81
	C076CFF9E54166728BA2686ED3BC4F03338504AFC25BE10E5584F7E4BF1EBBG6F89G091FF27CF6024F037712439905150779FA4FFC52405663387EBCDADF63B36E0D31DE73E13ACE71F0F83DEB2FEC52FFC07666CFE63C87C9503796A08B40D819D1D88EE086789331093F36FC8A4FA6375CA279FC123FA990E896BD41EE49A308708E8BF54B8CE797C6252087A13A6FDD33159A724483CAB7F447F4D4A7C947388DA45B1FC69B3F7318FE2706CEB6A6DD570FD4D2DD4815E11D50399F09B977324A39B6BAF76E
	8BC8BDF77A679B8C799AF3EF79EC03A10971D9231AFBDBA55E6B2F133D5E886FBAE69F947FCD022FBE9246D3FBBDC670385D88FD2B0EF05BF03C4BE5319AD7222223FF2F2907655FDF22AFB0B199CC1132CC94E35ED6D324ACC1029FB1C54A32C311C5043EF5G543E4A5B307F9E2F4AA83481CCGAC82C884D883302E0A5B58E4CB5582ED4C668A4A4DD3DE51A3849125ADB73F8FCFE1C68E3AAC7E106C720F8A032E205765960590E2E30E18318E5CF368EA086D0F429C6D02DF883AE42F64876E604032908C51
	B17566FDF8CCD0A03D83DE9FF8344B97C01CEF0B39982F5995124F87BCAED9880F207DDDE63CA7D7DD0340919840BB55CBDFBB56D78E2C3F20BA2330341A6B618704DF179004F8D1D7E71524492940GDE891A2846FAFE12440ECAF8B7D7F39DF7F7E0DCB2345394BE7FEB5E2F62E3760EE271DD41EB5C477EF1BF0E5119555CE65EF0F01BB16F79962A5C7028264DB8AEEFB064D6D3DBA9BA9A69BB4F901ED8AE5ABE3D5FF107FE9C8FB22B677A9F1D43FECF67B0C43429FE7F4902772A9656BBGA09F65BA2EED
	AD23B65B6C8A895CCAFB03E0E002A72CF1AA273F58487853768CE9AFB94AF5050A9DB1E43897C06778603E4EC1F5E2EA43F9C8388A2951AC84843F87DEC22C6FC49B36B56627E312C2D3CF7081706B96F0241002EFEA433E6712652057BDA58B7D020CB3EC385FC2668ACD39836110406732356178ABCD03DFAB634F37E11F98157CFE01EC0EC2427AAF901F94C44187C237C8D3FE1962AF35633D1A942E311860EAFF9BE11F9627F0FC21B5C20AEB97DEC3506B03AD47A18A99EB303F26AAB58E06E7E13FF12D13
	894FEDC7396FCDFF4CFD2FFC76C914FAB920574A9D4B3F71305EDAC3FD40D99376BDAA133DC3DBF687098D8C9F2232F6D6E03DEA14A74C5E37F7E2DE0BE00EE5G49B55C5E57F760FE9ED3182137C822C855296879FB16A23CAF4267D38204E29C3C3A945D25DB76C7748DB2BD527EF5962CC7D19577C60E1138D74C6B40B410E7A3C0DE8D55E543B12A8B3A365F9E427AA6355A9557D5570CA9B48DB6D3D757C916C86B2FCF2F973071C947F81DB5F88B2FB98B5A39F83E63FCEFDE59436B2CE513C9E8D5F03DA1
	D6DDD9F11C4AE1BD9E5E1BCA6734016A742C954F39847AD69C677A6C6AD4477A26D0C89A7512B443F43123D33DF79477D667BB709C7386C332851CEB066BAD3F8B4B0B7390C3333C1CC172B5DEEB8A4C1B8D306884467FF2E709CA76B240F5G08G86426FF4C98D76BD3231A67A308941B1A96893BC95D833A65734ECA2FEEEEAA25D2D787D905F07CD0878EEA0A0DED32C2C6250A06275524A62C6B6775A27D722065B1F69356A57043D937A7493D41FB9A742FACDFDE513FEBDG0DEBD557A3EB36061361DCE1
	BB99DD3751DA66A532671E13341649D7709B88FEC605273A9C3945F539847AF2CE727C7B23EEECF38327B28A83G89G09G29GD92778DEBF58F58C4DD35FD87CD0F678DDE27F1C00466B8C2B0146A056EE3766739804F8FC4FAC0E54C35E2939EBAF2D554A217F9EC5E70A54E7849D5D633D7D9FE87BDA7314AC13F8C0E360BED3E48C44B542G701CG20BEF920007B6422DA6EFBB75B543E37E66FCD7D2E983E371A4917D99B3611221EB7196FCD776258D1FABA2370B80095E082E089C092405A53FC0FDE31
	1E19EF0F08118FC8AD006B0098559219CB001E56E9EDA446EC835E288E66A3380FEFE2DCB9349B6AF85EDFF78B631C5016955CEB8417D0C7FD12625C376255B9181E3F4BF55A6714796B036BF167D0578C79F748BFDC9F3C5A9D3BBE58542DDD9FEC6E562E8F36F66F23F86021BE95707EDFF7472F9F7E27DB2BFE78B3D96B8F5F5FCBF807C2C208EC3A59A53BD85F2F647710C69D518387872627E2343724100DE91525969CCB291DDBCC5C4E136BF86E295E09634A96E86781145473BD7F28C06BACDAD9CF7737
	31BEFA1F293D7F5E06E37BG7487G92G9281D26B391DBF5CB3EFACEA1D09930B9E680D3D07893D5AFB78D0AF4E1BA1E9AAB8AAC46E53233D25CADF5823AC326043AA25E37E01504B2E603890354F5FA07434AF967D533D31EC016E57256B382693DD5724A999F2F8835E9779BA0FDBD9203BDC00B2409FEF20710B4640E726F1CC4237BE402EBFA00C220AB318366EA627DD866313G528132818A4E709CAB10B3A92DCC0EB2B9DEBD9146553078396736D67C8CG1F45E7382D7418A2EDE5451930FD241C0915
	83D7915BCBBB93F94EFF1A60571F09BC671F5C494F79DB202F63AC0FED35A4FEA7C3FB0D027B5DBE9E6F524FF25BFB3BAF366D6C6953361D62BEED7F7F20AF3E7FFF5497EB4F69DAD6DC6777FD5BC03E72F3FCCF0FBAF13ED0729A2EF46A6A5A03DECF33EB5CCAAC047A72969263B17D48B96E4B7BEFE01E4B201D8C10F60E6732193F71DC167C44CA345C2FDDC7E68D5C54EFBCC7778AB506F3D95615CDFABA2773A61F33817A65C60CFB68263A1643FB532B23B91C76274E6947BAF9G297BFFB3279F4B7B24C1
	5DDFFECB5D0FF5F41D5153353DFC102FAD6F6594A4BE37B8665D6B9A3626AC76DC3A65AB3A6CEF4F9A685ADAEF7358D85A4475196A437A34C1FB82E0D19337AD0144516550CED6F0BF2F6276362E097B45D3BB71592D2C19371FA5EDFCFE206D061D776A7EG4F211DD74F5F33B9344E6DB4EE749660F54960BC354D78CEFE96CF09EEB4931B5CDF9A07A715F1E5D48D3D301AA165AE69365EB19D0DCBDB08FF25B5533824D3702F937CBA951E7AF8698D6E639B212F3105576C170630CED6C1BB95A093A0872048
	8C752819472A677B2A51BCF9F2C08AD8A118092A70C357A75C3C3BBB6C6F6D643E746C8BCF2155FFE70ED973B61A59DDGFBD6EC7E0A8EBF697AFEF7912FEFA974A5832C8348864883A8E9656BFB2B2F122E2F5563151BE459B5BAC1CE1AFCF103DEE1BA0C46490D2E2D73F6645AAADA43365F509AAB57547A09AF3752BD5A23605B89DED661695E7D0078BEEEAF07BEE39B4F1BB33723F375C34A99A3G6392GD2G525AE88DDD5E0E473CD9E5E6E3B636C54B48739C079C98E3843A77D83B34BDECDBD40EC1A3
	5E1BA6FC46E5A79E3A077FBA2C7E96D574D5D14F69A267945B299CB36D61D8CFF924DFD2475432433D1AB1B577522CBE19512ED371D86DABB3503D7A0F9BC871395F2A5E7FF263DE5E1BEA7B78297BBA8735613121587504EFC83B1E888DE957935706621F979E980AFD5F18B0243EEF24BE7872884E1B468E289B814CG8EG31037B5F7B977F0A087FE978DE2BE79C574278EB064C67B78E47DE6B2E61793F01148C633BA33784266A437CB97E0F43F83FE4A9C010947761B056D150BBBEA153218CFFF09867D8
	D198A3E81AF7DD37F9AECE68603978B19D4F45499DBC571E75E39F4D03F609A523309A2083408330584247F413B1C950CE874886A8684CA82CG686864E3567A1C913600794DE7AF9DEC4CE25558EAB6FF6458BCC566DFC54D351033EFA1FB6E097009DD9AE3433575F7CDBCD7CD40DA93BBF9BDFA2CE64EBD8B2DC75BC4E95A4AC2A0BBDB7E5E44EB434CCEDEBFD5D6E11EC5DD20C30046AEFE5F72C9B52F359C007B106CADC298C77FF24EE961E8ED7162E78871FC750B5DBCCE9D786663487CEFE67D47C8BB56
	AD6DE235DF176ABBB444A96AAB5F390C949BFBEE78A0BC87DDD7D8464569C98AF24F688495743A2F40A1EF40B52E785ACBCEECB725D6B81F81B8G82G0B81D6D8393F6D982E08459710D419446702AEAF3FBF7F71C8EC1F7B69487CBE37F9045C61022F8CDC8B3033E876884EDF5258D8C8D0FC7B7511A285E789592410971FE1E9EEFBD8B97F25D869B9B49D1E9B81709D914DED5F52715C365D2A1D5B54792D20BB2370C0F7B837D5F7C76636756FE23BE1DFAD2DAE37A0D27C97CEE2FE03789790381374F01A
	775D1826E74A2774D07C0E4A22B02F8150D71BD790BD342F78A60E3903AD141F52294871FDCFA965A9843D2321C89E74AE7B0BFE757924211B6A45594D6F3A9F95357AE525FFCA13BEC169BFEDD7772FE07DE9CAFF02CD2BBFD369FF24DE2BFFDBB70FB1EB224E6E8B4B2FAF5DA51D216A6EEA6FC492CBD0CFE46D7DA4416F6E092C3DEB2AF8BCAB053E9BBDFC3D15556A7933G3F3DA7FE7C26BEEEF2E39BAF68851B8768GF08248GCBG92811E85C8874882488558ED035C8BF09C408C60G90EDBC9E98AF9F
	0A938F5801BF9C90147874E3E71C1860DCC0CCF0EA67612D4E68BC7C06D3BB8FEFF72A73B055378FF1FDAF02B52E3671D87F9C62F9A147467342E6B236228FF28200230F0F7D190E6705C05F1D6605F7305B726FF0E3B13E431138C067FA41DF40ECB2C919FF2D48F1D98AAED999D7EA67B971E04581ED7ECAEC203481BB7F2D154D4E793DE8633884857743FA0ECB35F3BBFD827FA405794C37D56F35D5773FF66E3B2FF3509A2D0554G39F66A379457C1F0C57D94C7ED2D599D5B5EAD6E796D5D6E5636F707BB
	5A5EAF39356DFD542D36F70A1BF0E3DB9887BFF5A863FC8AEEC8418589CF0C3B607548938AFE064805719D821E1063EF913926894E41F0DFD46106F84D6F46F99C16BCA185B96D03EEFC8FB36695C55A7EB2996375C3DAD774719811519BF029647E1A029BD2ECA0046EAE3EBFF017F4AF5CB19D0D7F97F4B816D476C76685A7415776C766052DCA1CB2C3DFD2BF77374DBA757C3B87A0F68F444F8B146EC53F1A6EB82369E04F918D9E146E95D1CD97E0639633677218F44F4C19AF190DCBE54F75B169266C7174
	1B9D13AE4D960FAEAFA65D3F544723ABBA9FCDC7E32B6BEFF8FF3CFEA8C1DDA2F1990ABFB033A70C67AE43663A0437133D0F295E7F935D7FD8D9203B7F31F207AEBAD63E25D347CA0AFB15642AF250ED43F9FE962E10B04E86ED2702FBD06267E3D141E5135C37865A6B95DCA261178D6DDC85172F509A87B9AE1D0C9B003628607EC846AD0276D285F7DB21DD21601658B9EE1B02FB4D26FC2B3E40F13F2C6738A59778DD5C6BFE9CC3DD22F75C0F6FAFE0A34A9CE199B742389CE56CB9E96E9DF59D8E6F081EDF
	D6C97C7C62F14CFFFE1901B1CB9C747C02710970AEA1757965ADD1DB2EBF7848F7AAF8CFF5F0391E774F156B293130DC879439F2E248253EB3B48E411E8C0565AA0577E3F3647AF98CFD7DB7359B98DFBB4465BA9A212F9F0D0765FACC11EBE2C8DB2EFC15DCB7E04C8D15DCCB0742B11972FB5C4E792D5060E708D8EB8A0CC9E37C70BC19D17CFEE3637C3644602706DC98337BE218DF5945C8FEF9751CDF65C5EDFE6A75B6421881953F8995BF2D7A156A7B00046F63149FB735C83E006447BFF032DB5ABB86AE
	B7D9AD6DBD5D2DBD837C6EA3F1DAFD4FDB7B4E97B4FF3707C1E6B2A730E75EFBDF55513AF303446B6D540BDA7536163C10D309BD1C72EA77737518FC8BDD8F1525DB11A5B706ACF4DE69CA7CF95FDD309E693C7F2C4CDBBD9CEF5EC7A671BB25D9EF67B4931AB4DA67944E67E5B14C6763BE4EA7710E793C618B73B9524079E45EB11FA7F068E2E75C7EG49A9A3F0AE996167547D9307D16CF32AADA8850420FC0DE2492FA9D9DE7C29244E7FD660679CD9C8BDE51FC3A783CD420268564E21DB81B4A98B227B49
	1C3AA88DE8B297C4D75127265B86B46852C268EA9A54F4A5C0D3B18F9DBB2F84703B72FD002A1A75DD8E60BB9ED69BF9E7494FB87BE120FF1C664EDFCB5175431BD2F47D60559867579837D9E3DC3646389CFB74387F32C70FBB5797BD4E5C97BD4E5290BD2E27C1BD0E7E9F592C6E88B412DFC779B24AA71F48C2C134D7098996F2A04A3AFA2C4E3079F1039E99AD68EDFAC034A0A3BE65D9D0BEB984DA10818E08B3789FAE0CEA88FD0EEE11D0BB87AB23DD6C0A302CC7B249E89F39D8AC1B03AB611709EA34B6
	3B7FDFAC62F96550EDF5DE497D52E84C3CD2FE195AED6D65701DBA7B8D18816F098C782517F99C69B19CC1316F557BFDAED114DD62E4135F036F581B2F49C2785B1741087907789856C97B5EB1E2392F28E87E8FD0CB8788041F2B286A92GG28B8GGD0CB818294G94G88G88GF0FBB0B6041F2B286A92GG28B8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA493GGGG
**end of data**/
}
}