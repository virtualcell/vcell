package cbit.vcell.client.desktop;

/**
 * Insert the type's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @author: Anuradha Lakshminarayana
 */
import cbit.vcell.client.PopupGenerator;
public class TestingFrameworkWindow extends javax.swing.JFrame implements TopLevelWindow {
	private cbit.vcell.client.TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JMenuItem ivjAddTSMenuItem = null;
	private javax.swing.JMenu ivjFileMenu = null;
	private javax.swing.JMenuBar ivjTestingFrameworkWindowJMenuBar = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JMenuItem ivjExitMenuItem = null;
	private javax.swing.JSeparator ivjJSeparator1 = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TestingFrameworkWindow.this.getAddTSMenuItem()) 
				connEtoC1(e);
			if (e.getSource() == TestingFrameworkWindow.this.getExitMenuItem()) 
				connEtoC3(e);
		};
	};
/**
 * TestingFrameworkWindow constructor comment.
 */
public TestingFrameworkWindow() {
	super();
	initialize();
}
/**
 * Comment
 */
private void addTestSuite() {
	try {
		getTestingFrameworkWindowManager().addNewTestSuiteToTF();
	} catch (Exception e) {
		PopupGenerator.showErrorDialog(e.getMessage());
	}
}
/**
 * connEtoC1:  (AddTSMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkWindow.addTestSuite(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addTestSuite();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ExitMenuItem.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkWindow.exitApplication()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.exitApplication();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
private void exitApplication() {
	getTestingFrameworkWindowManager().exitApplication();
}
/**
 * Return the AddTSMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAddTSMenuItem() {
	if (ivjAddTSMenuItem == null) {
		try {
			ivjAddTSMenuItem = new javax.swing.JMenuItem();
			ivjAddTSMenuItem.setName("AddTSMenuItem");
			ivjAddTSMenuItem.setText("Add Test Suite");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddTSMenuItem;
}
/**
 * Return the ExitMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getExitMenuItem() {
	if (ivjExitMenuItem == null) {
		try {
			ivjExitMenuItem = new javax.swing.JMenuItem();
			ivjExitMenuItem.setName("ExitMenuItem");
			ivjExitMenuItem.setText("Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExitMenuItem;
}
/**
 * Return the FileMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getFileMenu() {
	if (ivjFileMenu == null) {
		try {
			ivjFileMenu = new javax.swing.JMenu();
			ivjFileMenu.setName("FileMenu");
			ivjFileMenu.setText("File");
			ivjFileMenu.add(getAddTSMenuItem());
			ivjFileMenu.add(getJSeparator1());
			ivjFileMenu.add(getExitMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileMenu;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}
/**
 * Return the TestingFrameworkWindowJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getTestingFrameworkWindowJMenuBar() {
	if (ivjTestingFrameworkWindowJMenuBar == null) {
		try {
			ivjTestingFrameworkWindowJMenuBar = new javax.swing.JMenuBar();
			ivjTestingFrameworkWindowJMenuBar.setName("TestingFrameworkWindowJMenuBar");
			ivjTestingFrameworkWindowJMenuBar.add(getFileMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTestingFrameworkWindowJMenuBar;
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
 * Insert the method's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @return cbit.vcell.client.desktop.TopLevelWindowManager
 */
public cbit.vcell.client.TopLevelWindowManager getTopLevelWindowManager() {
	return getTestingFrameworkWindowManager();
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
	getAddTSMenuItem().addActionListener(ivjEventHandler);
	getExitMenuItem().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TestingFrameworkWindow");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(616, 603);
		setJMenuBar(getTestingFrameworkWindowJMenuBar());
		setContentPane(getJFrameContentPane());
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
		TestingFrameworkWindow aTestingFrameworkWindow;
		aTestingFrameworkWindow = new TestingFrameworkWindow();
		aTestingFrameworkWindow.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aTestingFrameworkWindow.show();
		java.awt.Insets insets = aTestingFrameworkWindow.getInsets();
		aTestingFrameworkWindow.setSize(aTestingFrameworkWindow.getWidth() + insets.left + insets.right, aTestingFrameworkWindow.getHeight() + insets.top + insets.bottom);
		aTestingFrameworkWindow.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
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
 * Sets the workArea property (java.awt.Component) value.
 * @param workArea The new value for the property.
 */
public void setWorkArea(java.awt.Component c) {
	getContentPane().add(c, java.awt.BorderLayout.CENTER);
}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @param connectionStatus cbit.vcell.client.server.ConnectionStatus
 */
public void updateConnectionStatus(cbit.vcell.client.server.ConnectionStatus connectionStatus) {}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @param freeBytes long
 * @param totalBytes long
 */
public void updateMemoryStatus(long freeBytes, long totalBytes) {}
/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 12:00:01 PM)
 * @param i int
 */
public void updateWhileInitializing(int i) {}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GBDFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8DD0D457954ED813B88D13685424465299EACDEB152434B509EDC8EABACC35061990CA2CCDCDEBA6CEE2BBA4E3DBBBB199E672D8D8D992FFD009DAE39AD2CD4408AB82D1DCD7434FA2A030AC328296DAFD308FDCF93BEFF96FAD8B08C94F39775D5D473ACF22C6E64E5CF74F7DBF67BBBF77AEDC61078966ECD3A147E51BB96D6FD8B647A56C67385FC933F6B1DE657E74FCCE77779150D66E1EE0AE
	148D5866CC4FAF601CDFD8205E83753416747C1D703D1DEB7F6252A770E130CE95346DDE7261026B2F639159BAF9247ED473C6A8B783DD849E16097BF17D654D3BB47ED40D4FA85B44F1530B314FDF1B77E83C355047823CE6200B45347F8814F483F75AAA35FEBDB366F16B164926CDB80FEECEC6AB3431D9DAD9443D15BD77G2E353BE523B626826AA2909EBE075BB4F694CA3BEBF060D4DF2FD7358E778922E86D933D02DF35FA84E5D01582B61BDBD0D42FFF60344C7B0410A48FF6FA7D9EA98473FF0AF377
	36D848DC9C378666CDA43C9FF761FA9E789E871A79A9638F1FC77EDA78369CCC4FAFBA484EF8F23FDDBBC3571547390CB2DF6E9603B3D69E24E73BF8B0F246A45F0B26FF92FD4C75215C6321EDB1D08650FA20DDA5697915C06F939CAC90BF05323AD72D15FCBE49DF9D8854089E39C57288A2FC5BEC5022714E8AFD126C61B82A6B221612987B399E51F1569B9EC7E5B22F1C69BD716CA35C4A1FBDE04A0BA11315732E18DCA5D49617CAA2B221EB8F982EBD2DE4A2AEA72E5D4969575ED33FBC479CE36D4A0A65
	B9715ABCC93A35D715202E7792DD2741F736068F4ABFC4787B8EC57894230F924CE03D825AE69DE258685A4C76521CBB07ABB8726D18F218752BB8D356A13A07C2EB74DEDC5DB8F715152EE98E6F251B705DBABE5D4BB859A356AFC1DBAA90455743925AEC69E130D5A08F50B8D0BC50A22054438CE36B4443DF91E3AEDED6EB02DE51A3489C9D7B30EF87144208AA730DFEC5657DFDC287AFFB79DED1D0B82D4FE3BE14C16F04F1CF906C77419A7502DF10F955AB79E1F630D9D510955AA74537957B4882E9F5FB
	FDE0593CAF4031F9D3FD5B2194F9C5BD9770702A9069C15BB3FC2813E13E83E60489601B4A6505G4AAB934E1FDB1A1EDFD84A64506A47791A8459AF08B6DB33A48D8683EEBC8967A9C5B9FFC1FCC7B1FCD715B299AF9FC25EBC28AF8E4FF317FF1BEE9E07378F374F4B234C468E1CC73F1CD14AB073FA97430CE35343DC4973774744CCA260F7FDA945CA4111E85BA9A7F3623EE87D092EAE83DB4AA80DEDCBB76EABB28C5748394579B707F565D0D0D715F0DEB710FA044978E9FF9145EC8D2F888C252DB2GCC
	70C4A4CE775951ED5164E98E6F1DA73CB818EF6191A633A9673BF4F2B7FF8DE738D6E65FA1EB645C26B528EC9E17B09E894390969DC2C070FB60C3515A1E1290F31A3D76CB61B1A9049FG7B2E8503D2427CE5925AA02F2A3237B7288A6D020A41B5521ECE56D2023D0108EBE0EB7DDE4273C9A1306FF07FBFCAE89BFD125FAF90A50507E86D2B08ED8A226003CD57CAC13FCA792F84905F034228669B7E92584021ED0BC174B3B4BFC8625F45B348DE9F289EDD95D7D206F6FBFF4BAE1D0CD6023EB14F61202D30
	0C59E0E8AF3341FDE3BF6296DF8818E24510D98F5DE7D24B287DCCAB0B58A05D539F06E26F7DA562A3B43EC277AAA968B70DF2950A7D2BAA4EB78F56C9864AA8E3587F2F0C6D4C3FE8202F15C4110AB4AC6BB3CA816AC6988A8A60165111B505655726EC0BEA6B50E4C95B4FABA8CBD167836FA8C7D9FE2B1B650119301FDC606DAB27727414D3F950FE1DBD2CDFAA54D78365D650FE0E8A5A0F4A614D5D289B1253DD660779912B92820158ECE708CCE84E76B6713BE8BF1E8A161B551CC35944C3FDA1D0EA8553
	63F31BD8EEB6F3F09E373E7E799C237CF335368F4BE791BD0E7FB214C3E53FA408EBB6C0DB57E7CC6E990ABEBED4AB0A5467A521C91359B7D43D1EA9EF1A7A8E147DDED9D19B4190C718FC6FD5F13F983BB436964B133BD9AC97E15D39C0762348D75B8B759886FEAE700B01F248FCFBFB4A50CE89G2C34F089F23FA47B84CFB1CA564A07D4AB718956EA52DC075F87189E0A0F229D739E8FC232BD68D50562839DDAFEB5E7FB955365A3F3B87B177FB3454AF727ED39E41AFAD43BD79C55657499AF1A96815173
	5D51434E379E7A149EE3676BF747BA5F25E36CFC2F5E62792A5B36913F6745D4C67476913D6191B53F1C6B445CF37CD86C58B8BD0ABF976AB4A7FB0C60F05131683CF1B161AFBD961DB7B61CE3F9638ABCFBA54BA3BAC2E88789D0CF864AG4A845AF8BCBD3F64B8435F0EE0B9B7C91E56680774494F0B6D939CB24B1702B95407EA7534201979D00E4961D75D51B9F8437109E777C455693834EB46D9237191B50E6AE529797D58765FA278030D2A6053FC382DCD6F4393E02E9420B5C0D9C054CFACE8E7F73BC2
	9B7387D7CF6B7DC172162B26F78D7CC1330D6ECF34C5303BF26F9B1ABF60C6511FA5C25BD220D5C0D9C0C5A7C007C097CFB09D7DE77864E4BAA240F4CB3540EBG7FDA9B9D8B43B23D2B6766F4B1731AF1513A38634444F1D4FE5FF5B3794D02F61A733E6FC4DE9A545705F98A61D96CF0FEBB637D2D95F98EBB558B5DC772B90C59DDF6D67FB5290F046B4EF3C679A3A39CFBEB79564024EB5CCA2ECAF5FEA1E41CEF7507E267DBE2A8F63EA50736D2BE4C213FED31763150757331A921D8795887641C3F593105
	4C2DA802C2C06860D5DEEBFBD5FD0F0B1117B9A188EBB25A4E3D591D2F7529D3255AA3E19B4E5F3953580639F5F644C7110B59EFBC5497EAFADDE927F7A90ABF390D614FFE124579A74E223FD4219E87B477A443CED2FB2C3702641346F821F6FC77984619B468178914FBAABDFF8F107D94335FDDA3137A583A116B7858F7C60D3130F5B4B696F60EE20E22C8C139CF0856773FC6F7055BA21E829DA32A067659CF462B3CBCA0DC0397AB99CF5B0C469F9BB54294557B13BDFA3D1707EDEB0EBB168EFACE996B00
	4ED766C42CF904FEBEA8827CA5C5E88DE8CFG40CFBC05FAEFF132BBDB6094FB7F7BC988FDF7BFA406303D76005047CD7D9C7BFE53493035967A6682EF9F10834873B94B9F72DDB89FC1E48DAFD35E0186BCB74DB4C1B6AAB1B9BF621D461CB74E69E3CE6A67942BEB214C2D82AC52F86EE2EF15FB00672EE2FB1DF2FC3731CD408E601C845FF71271F1B89FD0F2950BDBA31F3038157803B9DCE6ED6CBCF6157B2AA9230A4AFBFDD5A4EE2578DEB4D19D055AE26928225AD8C7B4FFB95C0E7BEA282679CBDE18FF
	0270AF6A78D48ECF93BB457A88342D2CE6F1E1890949D5B569792E9AE6E3770D995B4883E331EDA4FEAC363F1CB3F6FDFFB9FF4C885BF4FFBFA7B12F8776A656B02CF81C789E26D31B155E4BGA3784225AAD6C76B85E72B3B2151D9FF2155F9213639353DCE4B1593C27A776F4AF726443CC7A20D54D09D44E9E556FB3333299FCC8AE3BF855A32EA10E7EF556747384F47F3E9DC276D7E896DF8E644F124BDE1C2BB6E7539896353A7341BF56369391E791861B16B19A4CEFCF42A613B3E23D6FB5F554A692B9F
	4C2E5CF2A5075A455F8F985C775072C4FA7E439D96ED8F8935AC8EE4F5A23ED6C0FDAD1045914EA1DCB7774E5DF34363A87E5FA476DA64084E77F35A10DF6A084E776FF131FC3F8A5A66BAD83E3FF09C65E02983DF85E487F28329C0536B181DD4DDA965A609C5EEA9508C06A46A20CFF1FDEF9B333B39F5CCD61F915BCD02FAAA506AB03F1A7071AE1FD5C76F6A143F642CBEF7427EFAF9A4555DDCEE162A0D23EB2C1A30C6DED45F1B4D4D4C3A715B39075AB06FD441296CB90D3F137D9459DADB1AFBE25B73D8
	55F205A9E61461C5600F1FE6BA19F665A0149D7CE5CD9BF59E2FAA49C49D40F44992C4B2F514F2490B0AF64E6D2DB7773B4A53F94C6E967EE2BE3768AE07617DA57EB415EB32D6963F7CE0766670D9B2B7A05EB2214DD28FF7CAA087D097D0201E1D6D59ABA594EF387DEAD5657BAE11178C86368E2F900A30B1G5333D5F4C41FEDEAFD849B336A2F45893519G798DA5311E5ACCF618BFCA780BF4FCEACB26AE96EBD3202D38016174ED97AAADAEDC4FA77A467783DA1F611A6C8EF02304EBCD74A7733F76B574
	58277AF9F25CB8EE260C470E1B4B46E3474D34716B5FA3D60C9B3F6B7EE9DC7F2ECB6DE636A5E2D37387B0B6D4FBBC6E7670AD1E35395C085FB36D824C4B030D9C51781925285B3A912F4A0650F7A15B04F72104867AAE94310FE73B757136E036E8E218091667D2CDE7998D516F36F71BB76BE5C14D163D5319F1FD2F5F2BFAF951BBC61254F668689F20F6754C9036DB9A410F83B5G75G859A19CDED5B70EFEAD3B16C294EB3A0301C022D770F8D46BA4E5AB079EFED66BCFCEF6C15D4D572617C0C7FEE9E62
	D4158204C9F985F9A8C359BBF0C925DDB57E0EBCD426A87493B60DA7CDA4A6474139929AD9EE372C133D8324B4B2DB9BC6B350EC62CB5D37457037222F03689A9F5D56B5A8CE5F5804E7BE6D958544A94DE9E62878B694A32789231CE2E58D1A0BA66337829FF386F18410F18626E96D3D8C17B5B7BF909E735BA1E3EC3CB0B4B9B6DE263FC300CE5D23816D6E37E688FFC7117A7B95A10C4157068A423CC645A5A9DEC4BDBDFFEF87769779D1A9887D695DFE34035D1356G8E38B3601B016CC0785663BE03FDDE
	6A5CF59D59D27F7E3D8E665F63E1CC4699F617FA45BA610D90DFFAED369A89FF77E8269B21F9631FBBD85E3C3109650DBBC95E6800FA8F105A446ED71787D9DC5D33607B4497986533F1CD5A7F8CB4C57CC684F3BF3CA676FDB52CBEFA1363DE3F61F1B4265EC3643036A9BA564EA47C2C2668D87BC1A7530525996EC64DACEFEDD7D10FE9D04F84AAE881DD8359015CAD4C6709CA39913601F9D61248B59BEF5C3C97E288F101B2FC2A5A99E7F4F261734E56FD6F6D3C7DBE44FEDB57207269D10D6D38CF1D5C0E
	C5B5360F1755EBFDFCC80D6D632F2AFA9FCF7F676CBE4BDEA893DF634CAA379942F7917E96D4C4FE8CD239DC0C4DD0206B53E26F01AF7531F77D1DFD06F7DD07135ACE17B3E2C3DA0EBD0C7B18897CA4A743CFFA701091FE40B7367BF8D1D4F9F1305A6FC1BFD9B32A8A113C656EE11CDFE1FD34C6AD168F633EAF6B467C9FD0CB87887372138CE58EGG48A7GGD0CB818294G94G88G88GBDFBB0B67372138CE58EGG48A7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4
	E1D0CB8586GGGG81G81GBAGGG1F8EGGGG
**end of data**/
}
}
