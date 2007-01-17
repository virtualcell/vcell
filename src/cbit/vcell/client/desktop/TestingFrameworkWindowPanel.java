package cbit.vcell.client.desktop;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.testingframework.TestingFrameworkPanel;
import cbit.vcell.numericstest.TestCriteriaNew;
import java.awt.event.ActionEvent;
import cbit.util.AsynchProgressPopup;
import cbit.vcell.client.task.TFRefresh;
import cbit.vcell.client.task.TFAddTestSuite;
import java.util.Vector;
import cbit.vcell.client.task.TFUpdateRunningStatus;
import cbit.vcell.client.task.TFRunSims;
import cbit.vcell.client.task.TFAddTestCases;
import cbit.vcell.client.task.TFDuplicateTestSuite;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.client.task.TFRemove;
import cbit.vcell.client.task.TFGenerateReport;
import cbit.vcell.client.task.TFUpdateTestCriteria;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
/**
 * Insert the type's description here.
 * Creation date: (7/15/2004 2:36:18 PM)
 * @author: Anuradha Lakshminarayana
 */
public class TestingFrameworkWindowPanel extends javax.swing.JPanel {
	
	//
	//
	private TFRefresh tfRefreshTreeTask;
	//
	//
	private cbit.vcell.client.TestingFrameworkWindowManager fieldTestingFrameworkWindowManager = null;
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	private cbit.vcell.client.desktop.testingframework.TestingFrameworkPanel ivjtestingFrameworkPanel = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JDesktopPane ivjJDesktopPane1 = null;
	private javax.swing.JSplitPane ivjJSplitPane1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TestingFrameworkWindowPanel.this.gettestingFrameworkPanel()) 
				connEtoC1(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == TestingFrameworkWindowPanel.this && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP1SetTarget();
			if (evt.getSource() == TestingFrameworkWindowPanel.this.gettestingFrameworkPanel() && (evt.getPropertyName().equals("documentManager"))) 
				connPtoP1SetSource();
			if (evt.getSource() == TestingFrameworkWindowPanel.this && (evt.getPropertyName().equals("testingFrameworkWindowManager"))) 
				connPtoP2SetTarget();
			if (evt.getSource() == TestingFrameworkWindowPanel.this.gettestingFrameworkPanel() && (evt.getPropertyName().equals("testingFrameworkWindowManager"))) 
				connPtoP2SetSource();
		};
	};

/**
 * TestingFrameworkWindowPanel constructor comment.
 */
public TestingFrameworkWindowPanel() {
	super();
	initialize();
}

/**
 * connEtoC1:  (testingFrameworkPanel.action.actionPerformed(java.awt.event.ActionEvent) --> TestingFrameworkWindowPanel.testingFrameworkPanel_actionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.testingFrameworkPanel_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetSource:  (TestingFrameworkWindowPanel.documentManager <--> testingFrameworkPanel.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			this.setDocumentManager(gettestingFrameworkPanel().getDocumentManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP1SetTarget:  (TestingFrameworkWindowPanel.documentManager <--> testingFrameworkPanel.documentManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			gettestingFrameworkPanel().setDocumentManager(this.getDocumentManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetSource:  (TestingFrameworkWindowPanel.testingFrameworkWindowManager <--> testingFrameworkPanel.testingFrameworkWindowManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			this.setTestingFrameworkWindowManager(gettestingFrameworkPanel().getTestingFrameworkWindowManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connPtoP2SetTarget:  (TestingFrameworkWindowPanel.testingFrameworkWindowManager <--> testingFrameworkPanel.testingFrameworkWindowManager)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			gettestingFrameworkPanel().setTestingFrameworkWindowManager(this.getTestingFrameworkWindowManager());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}


/**
 * Return the JDesktopPane1 property value.
 * @return javax.swing.JDesktopPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JDesktopPane getJDesktopPane1() {
	if (ivjJDesktopPane1 == null) {
		try {
			ivjJDesktopPane1 = new javax.swing.JDesktopPane();
			ivjJDesktopPane1.setName("JDesktopPane1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDesktopPane1;
}

/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			getJSplitPane1().add(gettestingFrameworkPanel(), "left");
			getJSplitPane1().add(getJDesktopPane1(), "right");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}


/**
 * Return the testingFrameworkPanel property value.
 * @return cbit.vcell.client.desktop.TestingFrameworkPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.client.desktop.testingframework.TestingFrameworkPanel gettestingFrameworkPanel() {
	if (ivjtestingFrameworkPanel == null) {
		try {
			ivjtestingFrameworkPanel = new cbit.vcell.client.desktop.testingframework.TestingFrameworkPanel();
			ivjtestingFrameworkPanel.setName("testingFrameworkPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjtestingFrameworkPanel;
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
	this.addPropertyChangeListener(ivjEventHandler);
	gettestingFrameworkPanel().addPropertyChangeListener(ivjEventHandler);
	gettestingFrameworkPanel().addActionListener(ivjEventHandler);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TestingFrameworkWindowPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(545, 612);
		add(getJSplitPane1(), "Center");
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
		TestingFrameworkWindowPanel aTestingFrameworkWindowPanel;
		aTestingFrameworkWindowPanel = new TestingFrameworkWindowPanel();
		frame.setContentPane(aTestingFrameworkWindowPanel);
		frame.setSize(aTestingFrameworkWindowPanel.getSize());
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
public void refreshTree() {
	gettestingFrameworkPanel().refreshTFTree();
}


/**
 * Sets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @param documentManager The new value for the property.
 * @see #getDocumentManager
 */
public void setDocumentManager(cbit.vcell.clientdb.DocumentManager documentManager) {
	cbit.vcell.clientdb.DocumentManager oldValue = fieldDocumentManager;
	fieldDocumentManager = documentManager;
	firePropertyChange("documentManager", oldValue, documentManager);
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
private void testingFrameworkPanel_actionPerformed(final ActionEvent e) {

	if(tfRefreshTreeTask == null){
		tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager());
	}
	
	java.util.Hashtable hash = new java.util.Hashtable();
	Vector tasksV = new java.util.Vector();
	
	try{
		Object selectedObj = gettestingFrameworkPanel().getTreeSelection();
		if (e.getActionCommand().equals("Add TestSuite")) {
			TestSuiteInfoNew tsin = getTestingFrameworkWindowManager().getNewTestSuiteInfoFromUser();
			tasksV.add(new TFAddTestSuite(getTestingFrameworkWindowManager(),tsin));
			//getTestingFrameworkWindowManager().addNewTestSuiteToTF();
		}else if (e.getActionCommand().equals("Refresh All")) {
			tasksV.add(new TFUpdateRunningStatus(getTestingFrameworkWindowManager()));
			tasksV.add(tfRefreshTreeTask);
			//getTestingFrameworkWindowManager().updateSimRunningStatus();
		} else if (e.getActionCommand().equals("Add TestCase")) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				TestSuiteInfoNew tsInfo = (TestSuiteInfoNew)selectedObj;
				TestCaseNew tcn = getTestingFrameworkWindowManager().getNewTestCase();
				tasksV.add(new cbit.vcell.client.task.TFAddTestCases(getTestingFrameworkWindowManager(),tsInfo,new TestCaseNew[] {tcn}));
				//getTestingFrameworkWindowManager().addTestCases(tsInfo,new TestCaseNew[] {tcn});
			} else {
				throw new Exception("Selected Object is not a test suite! Cannot add a test case");
			}
		}
		
		else if (e.getActionCommand().equals("Run All")) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				TestSuiteInfoNew tsInfo = (TestSuiteInfoNew)selectedObj;
				tasksV.add(new TFRunSims(getTestingFrameworkWindowManager(),tsInfo));
				//getTestingFrameworkWindowManager().startTestSuiteSimulations(tsInfo);			
			} else {
				throw new Exception("Selected Object is not a test suite!");
			}
		}
		
		else if (e.getActionCommand().equals("Run Simulations")) {
			if (selectedObj instanceof TestCaseNew) {
				TestCaseNew testCase = (TestCaseNew)selectedObj;
				tasksV.add(new TFRunSims(getTestingFrameworkWindowManager(),testCase));
				//getTestingFrameworkWindowManager().startSimulations(testCase.getTestCriterias());
			} else {
				throw new Exception("Selected Object is not a TestCase! Cannot run simulations");
			}
		} else if (e.getActionCommand().equals("Run")) {
			//if (selectedObj instanceof cbit.vcell.numericstest.TestCriteriaNew) {
				//TestCriteriaNew tCriteria = (TestCriteriaNew)selectedObj;
				//tasksV.add(new TFRunSims(getTestingFrameworkWindowManager(),tCriteria));
				////getTestingFrameworkWindowManager().startSimulations(new TestCriteriaNew[] {tCriteria});			
			//} else {
				//throw new Exception("Selected Object is not a TestCriteria!");
			//}
			Object[][] objArr = gettestingFrameworkPanel().getMultiTreeSelection();
			for(int i=0;i<objArr.length;i+= 1){
				//if (objArr[i] instanceof cbit.vcell.numericstest.TestCriteriaNew) {
					//TestCaseNew testCase = (TestCaseNew)objArr[i][0];
					TestCriteriaNew tCriteria = (TestCriteriaNew)objArr[i][1];
					tasksV.add(new TFRunSims(getTestingFrameworkWindowManager(),tCriteria));
				//} else {
					//throw new Exception("Selected Object is not a TestCriteria!");
				//}
			}
			
			
			
			
			
			
			
			
		}
		
		
		
		
		
		else if (e.getActionCommand().equals("View Results")) {
			if (selectedObj instanceof cbit.vcell.numericstest.TestCriteriaNew) {
				TestCriteriaNew tCriteria = (TestCriteriaNew)selectedObj;
				getTestingFrameworkWindowManager().viewResults(tCriteria);			
			} else {
				PopupGenerator.showErrorDialog("Selected Object is not a TestCriteria!");
			}
		} else if (e.getActionCommand().equals("Compare With Regression")) {
			if (selectedObj instanceof cbit.vcell.numericstest.TestCriteriaNew) {
				TestCriteriaNew tCriteria = (TestCriteriaNew)selectedObj;
				if (tCriteria.getRegressionSimInfo() == null) {
					PopupGenerator.showErrorDialog("Either the selected simulation does not belong to a REGRESSION test or the regression simInfo is not set!");
				}
				getTestingFrameworkWindowManager().compare(tCriteria);			
			} else {
				PopupGenerator.showErrorDialog("Selected Object is not a TestCriteria!");
			}
		}
		
		
		else if (e.getActionCommand().equals("Duplicate TestSuite")) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				TestSuiteInfoNew tsInfoOriginal = (TestSuiteInfoNew)selectedObj;
				TestSuiteInfoNew tsInfoNew = getTestingFrameworkWindowManager().getNewTestSuiteInfoFromUser();
				tasksV.add(new TFDuplicateTestSuite(getTestingFrameworkWindowManager(),tsInfoOriginal,tsInfoNew));
				tasksV.add(tfRefreshTreeTask);
				//getTestingFrameworkWindowManager().duplicateTestSuite(tsInfo);			
			} else {
				throw new Exception("Selected Object is not a test suite! Cannot duplicate");
			}		
		}
		
		else if (e.getActionCommand().equals("Remove")) {
			if (selectedObj instanceof TestCaseNew) {
				TestCaseNew testCase = (TestCaseNew)selectedObj;
				String confirm =
					PopupGenerator.showWarningDialog(
						this,
						UserMessage.warn_deleteDocument.getMessage(testCase.getVersion().getName()),
						UserMessage.warn_deleteDocument.getOptions(),
						UserMessage.warn_deleteDocument.getDefaultSelection());
				if (confirm.equals(UserMessage.OPTION_DELETE)) {
					tasksV.add(new TFRemove(getTestingFrameworkWindowManager(),testCase));
				}else{
					throw UserCancelException.CANCEL_GENERIC;
				}
				//getTestingFrameworkWindowManager().removeTestCase(testCase);
			} else {
				throw new Exception("Selected Object is not a TestCase, cannot remove selection!");
			}
		}
		
		else if (e.getActionCommand().equals("Remove TestSuite")) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				TestSuiteInfoNew tsInfo = (TestSuiteInfoNew)selectedObj;
				String confirm =
					PopupGenerator.showWarningDialog(
						this,
						UserMessage.warn_deleteDocument.getMessage(tsInfo.getTSID()),
						UserMessage.warn_deleteDocument.getOptions(),
						UserMessage.warn_deleteDocument.getDefaultSelection());
				if (confirm.equals(UserMessage.OPTION_DELETE)) {
					tasksV.add(new TFRemove(getTestingFrameworkWindowManager(),tsInfo));
				}else{
					throw UserCancelException.CANCEL_GENERIC;
				}
				//getTestingFrameworkWindowManager().removeTestSuite(tsInfo);
			} else {
				throw new Exception("Selected Object is not a test suite! Cannot add a test case");
			}
		}





		
		else if (e.getActionCommand().equals("Generate TestSuite Report")) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				TestSuiteInfoNew tsInfo = (TestSuiteInfoNew)selectedObj;
				tasksV.add(new cbit.vcell.client.task.TFUpdateRunningStatus(getTestingFrameworkWindowManager()));
				tasksV.add(new TFGenerateReport(getTestingFrameworkWindowManager(),tsInfo));
				//String tsr = getTestingFrameworkWindowManager().generateTestSuiteReport(tsInfo);
				//System.out.print(tsr);
				//cbit.vcell.client.PopupGenerator.showReportDialog(getTestingFrameworkWindowManager(),tsr);
			} else {
				throw new Exception("Selected Object is not a test suite!");
			}
		}else if (e.getActionCommand().equals("Generate TestCase Report")) {
			if (selectedObj instanceof TestCaseNew) {
				TestCaseNew testCase = (TestCaseNew)selectedObj;
				tasksV.add(new cbit.vcell.client.task.TFUpdateRunningStatus(getTestingFrameworkWindowManager()));
				tasksV.add(new TFGenerateReport(getTestingFrameworkWindowManager(),testCase));
				//String tsr = getTestingFrameworkWindowManager().generateTestCaseReport(testCase,null);
				//System.out.print(tsr);
				//cbit.vcell.client.PopupGenerator.showReportDialog(getTestingFrameworkWindowManager(),tsr);
			} else {
				throw new Exception("Selected Object is not a TestCase");
			}
		}else if (e.getActionCommand().equals("Generate TestCriteria Report")) {
			Object[][] objArr = gettestingFrameworkPanel().getMultiTreeSelection();
			tasksV.add(new cbit.vcell.client.task.TFUpdateRunningStatus(getTestingFrameworkWindowManager()));
			for(int i=0;i<objArr.length;i+= 1){
				//if (objArr[i] instanceof cbit.vcell.numericstest.TestCriteriaNew) {
					TestCaseNew testCase = (TestCaseNew)objArr[i][0];
					TestCriteriaNew tCriteria = (TestCriteriaNew)objArr[i][1];
					tasksV.add(new TFGenerateReport(getTestingFrameworkWindowManager(),testCase,tCriteria));
				//} else {
					//throw new Exception("Selected Object is not a TestCriteria!");
				//}
			}
		}






		
		
		else if (e.getActionCommand().equals("Edit Test Criteria")) {
			if (selectedObj instanceof TestCriteriaNew) {
				TestCriteriaNew tCriteria = (TestCriteriaNew)selectedObj;
				TestCaseNew testCase = gettestingFrameworkPanel().getTestCaseOfSelectedCriteria();
				TestCriteriaNew tcritNew = getTestingFrameworkWindowManager().getNewTestCriteriaFromUser(testCase.getType(),tCriteria);
				tasksV.add(new TFUpdateTestCriteria(getTestingFrameworkWindowManager(),tCriteria,tcritNew));
				//getTestingFrameworkWindowManager().editTestCriteria(testCase, tCriteria);
			} else {
				throw new Exception("Selected Object is not a TestCriteria! Cannot edit test criteria.");
			}
		} 
		
		else if (e.getActionCommand().equals("Load Model")) {
			if (selectedObj instanceof TestCaseNew) {
				TestCaseNew testCase = (TestCaseNew)selectedObj;
				getTestingFrameworkWindowManager().loadModel(testCase);
				return;
			} else {
				throw new Exception("Selected Object is not a TestCase, cannot load corresponding mathModel!");
			}
		} 

		//tasksV.add(tfRefreshTreeTask);
		AsynchClientTask[] tasksArr = new AsynchClientTask[tasksV.size()];
		tasksV.copyInto(tasksArr);
		ClientTaskDispatcher.dispatch(this, hash, tasksArr, true);
		
	}catch(Throwable exc){
		if(!(exc instanceof UserCancelException)){
			PopupGenerator.showErrorDialog(exc.getClass().getName()+" "+exc.getMessage());
		}
	}


}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB3FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13599EDECD357159FF4E2925B3A0D2E29C6A5C659C85794B1152AEC6BA4D651150ECCE299A026050DF56907AA0AD04B2A34C36A0B938A268182E4108FA718A623C9C9CD8221644389F16CA4F6E272D15290D6C4F9319F090963675AAFA48C266E1CFB5E35DF1C67B820E169683EFB6E4739677B5CEBA17F5822DDD906FCC1485AA5283FF3D902B07811A03C77F26A039CD777D11AD1507C0E839C945EE948
	015689E33BD9DFB166C23B9F609A6030CD01B6CF18EFACD071F3D5BC07AC03A0AC2840B9CB0DA695379D666C81DCAF003702665F01168E70C0C31DBA6F4AF713059D3F8D997661BE1ABDB9242BEB05CAEA4D423BD94915C8EB9DBBA72E2903FE90G194F9616F89B2135760E8EB7FB06FC0A6526C7727BAD9E3FCF8AA8962F949ED564E0C3C33F94D6FC01617610B8A6CD482151CB3E00D71E68958312DFA87F9877AF583B076DA788DBE16F658CD7F68CE90E40779C00240FB97EF5065F815F39A7530C65A7B91F
	CD9FD9D5BE86EFBFA32CBFBD16F3A08E1F5613441F77E414CFC7E033211069245E0A32DF84E32B81B6816C84B0D525992DG45B0F6D8E8E97C98DA5B10E21747466400AD98EC75FBC397E42F64076F068698D1F11FC99EB96495845277C6E3154EF9E682DA17F1576BC8A649E7386ED3BEDBA624BF758343EE9D1924A75FB674D711AC7CD5D119906D123D71E84F2F1AEA1BD3E9DF923434A7839B33F76950DE7045466CC56ABEABB434B32AD0578FB3DD2F076FAC55BE887FA3062F78A40AA7BB2DE4F86C574158
	42CF38ED8C6E67E7F167AC9672EA9E5615C352F3738D02054ED0E009BD4BD5B36E6D30904D5D11335CE078AB9ABC1DE5BBBB8B7695985B84C076555A0CFEEBBD15E69C8488824C83C886D88D3069943731EFB61E1A250D750AA125F55C6777CAA1015636DA0FC0ABCDAAA1519588ABE240A38D08A11FB864174202BA274B0AE79B1A322E17593E87E8F4C881A9A4AABEB9G3B035FAAD2A8CCF3AEDB8F621C1044C67BFDE3605D62D8D0607B0A5643E8C7E2D879BC689595A9BA0346FDD65449CDF1GF6048D601B
	64A21FC7F9E5827FF955E9C6F3B517C3865B2FCB8AC138E8E8F04B7268F8309FB9910255A867DA96BBEA615BDD4DE5BC5A02389550CF0D6CD35CF4C8330F436741630B21DB5C479EBD0631F9CBB53719ED035CE69C7B1E942AB6BC24EBB3FB2EEC4C5ED3235ACACD2C6FACE5FB623928FF6A5860DD7A71FE75CC330F954EFB2211BD4BF9A45FBF5AD0D7CE60578BB037064BF82049CCB65BAA06A5EE25BDA1B0B0499B15B81D53EB66B985E96F0A1CBF48708BE14F55GDBEB386C5EE6FCE9675FAD0F09E5F740BC
	98F31E7FA78D12113B997312F493D224C38ACA81AFFC0455310B4DE8FB2A5FDE17A3EBAEB3FC907C5C8E0E950E603FECC6DF9495A5649B9AD7240B120209B6BABE4CE805470702519041E9851A11C7F9827CBCB2BF5C0CBE6211838109A9AB32C49D9FE7BEAA7925B1B834DD9E8FA8043FFD9E6DFCD43A2546082F4F3FAF200F7B47B15ED02D30DC7CGF98879464084B0E4895E53683FDDFB8BB4B24A879C56BCF5503ACFF3DF1CA8633ED87E1F4705D5DF868DFA39A469475FB1241CA6DFD9F5BA6A0BF426E00B
	7E59EF32D8216243F4564CFF6358CCF58B7941A1BB6E3985E86DG48BD437D604D969C6731C6F5G3B6C7713D8A3727E0BAD8F75A3FDB5AEC108462056951161AB36C3B1E3832ABCE9FC1B8D656957444324B3A84F474C3CAEACBFC33234C2BB88F0872094654466CD9C6773B254F9BB55F99535B4EFE7AD4E7BF9991F57DFCB637EDA1A1FD2CBB2A6193DD509FAE435608D7126B8E989CF00709A9ABA1940284E5BF7E40FBA5FF91657F37505A843B950CFC25CD92E735F6F63755C0251E421DF5E109D2FEEDDF3
	164E3175ECD467B92993592423AB8E24D975A94861D32E9FDF2BB6275842E15963E369CC15ED01DDEB9304AB316F05763AAF94D6DC603413DC8FE5F6BCAF66BB952D667FD4B32FC5FA016EBC00ECB67F4CF174C92F6C99474DAF0881F198BC10FC615DE22DAF2C02B579674097G5C67787A2B3A6BC9E6EF5ADA5435294B968BABF74FB7685540A3CB138C5EF3A42B796722B2AB4F58EC28832073AFBA4E4F1F8EF3726BB87D313292E895DD5F602721F5070505331ACB74DEAA4BD5ED438D3466C668394ACFE30C
	E30EE32126D78ADD17C3E3123782AD4DA2CEA89696CFADB6B65C065F155CAE9716B9F44E400C724B586D5C7177EB2C9CA099AFBE2C1531756B2B3AB21EF7E04470EC9D49B623AEAA633A8D1B8DEEG4AB7070E445656557551DC6F281F1E772966FC12794CE0FDECDD7C8C430F54475645678B39AEA7E1AC3D1E57C9675B515EDA9A520CD7G940079GA9G298D5CD71E301F919254212EG14078151FFF1CA226175208369C2EED07B12837542F2034073EE1AB9517D663506293220F5DB922E4BED1C3A0EECA2
	5958AEA43A37DAD413394ECDE67AC516D2016A3B5B8A353EDB5EC875289B5AEBG947FCE1B78BDF73211473FBF19D0B74B213F86E0CBA3574D239D98F7B3211FEFCDB3D681B8817CG73GD22CDCBF5B9D2D0974E36385C23F9C793447A6F9AE535C32FB53C5D142F52B2DFA6BDE1B36AEBDE61EBE3D37272DAB0D69E7466C2366EC974AB52F89E48A60G3886B097E091406AA6AED7FBDBD3A239324050AF5B816704BC2CA3D2750ED24972DE866C1D89C038778B90E7EACEB356B6739A7729C22C2386A37DDF31
	BE66G6A37955ECBED3FDF5DB3519C22F1751EE94C668EC132F536472F01FB5A75EB604F5B75EB60AF5A8F929E76505E04793827FD669AF934DD2FC6AEE17C3C2A7CD35029073B417F851E630C470D6A1CDEC56E2D91F80C7921055D732757C39703FE1F022103E24209A29E93024DBCA6580BB1A6A4C3FF95402608BD1CB34D64E35BF5F4CCF9212784774BB9CFF9A1AB029FE478BC8D1E7245DFCD7CEEE60231B96739CFAC6D082F37E59D7AFADB5101F5DBD89E8FF924D85D7C3223A0B296759E17A20D2108E9
	4EEA36DE91C34352B45D2EE56BE9AC5E7A759DD8A78F793C3E10DAFD135E6CE5DA3DE10DC1BAFF714881E6CF613094E68E6E9095D1DDF32C1C2FE9D3E4FBCD649CA6D7FC3914397465D2690AAF17EAD7E239543B624B254515D8AEAED7BC3FA0DEDB0E609D4EAF5E12479528E1DB50AEAE9D61B9AE83ECE2A7C0B9606BDAA857115D7FEC82650DD993363B9814BCC2158D5716947035C9B0BF85A095E0ABC02E0D57FA2FB8F1ED54C3EA5418F432E8A63BD7EC716C7E9966C773ECE45F3BA2786798FE2186CFF66F
	AA66B568F2984BEF65313A5B44E375E4AB77454F51DCF84C54FC3FEA3A5778B99BA09AC2497D24C132787A880F81CB006774D62E5FD7ACE8BF11380655F2C3C32B0CF7DCB7998B453597D84E437579F69E577E4E76F4C35F8BF0474E6FFA13A778DDAF65A70B8567287EDDEFCB7FFF8D8B6C246B1576E8CDBD9858ECA01A7682CEF3870CD7B99077D5C13EE68F343397A129DA20257147260C23CFDC9434639B260C63F96DD3566FAE540E230C5A5475445B5AFFF15EF62EDDAE781F1E9B777F9745C1BCA5296D03
	990FE42DAA3E1DCDB658DA44EDF01DA3D29F94A3EEA7744DED9C370661701EEE0D60FA0AE7EBAFBB9DD3FD89771D1DCD0E4C1AC6EC1DB01B373C39EDA88349E4D67B57F44E2528B8BA4F83536ED63363637B7738AE752E57D1ACFA61A87AC4EADBECEEDE47701B5AE2F373BFA2FE1981E34EF6FEDFA86C42781A827DB5GDBGB2818ABA203E6C6079FB2B2BDAC8D053764BC1B7A4B23F2614D25F158B6E6DBF28DF6F66763F6297BF95D6FE43E178C05037FFE78769EFC4EDAB7E76C8963644DF0A9B79CB023195
	G1BG368364B8530CA6A7672F36330A78EB737A7056A3FAC6588B8EE7EE40A7CDC451181C9CCE146965A2738CFCD4BB69BC1423FA3A30A654FDCF084CD9DA00BE5E69201BA4E5B2FECF7CF609761EB85FC9EFBCAB21CDF572FB6237CC7C1DAC4349F36373A63C8B78256B8A4975C69E172B6909470474DB830603F164EAF251791D2EE84C0C5A6D6F0E8A116F0D1A6F2C2377BF0F955DD79AE4939FF6452F4DAA3B74EB33D3DD7AF70D33DDB35FA51A3A623F37BB3A346F6D6AFF4D5D18BB6600DE96822C82D88F
	701A0B5BF3DA5707E44FBA365C669D465A907F5DD3B87D275D71F9FD561D78BF41675D7846B9A4AB0ABC067BC76AECB77B9FD38EB2A4615639D1C6A15F7008C2D3D57CC6779E55CE91CD36FB3388EBC93B046FC4BCAECCC8DA7BB59DEB09EB3FB91DE437551DD36D176A1123963C776B5C33B45EA728B549EFCA78DD4B5B49EB12E0A92B85213F82A03513674C6FCD39EBE5GBE33F366DCC8BA7DC30F995A5C2EB4E385009B408BB08970A0409200E73B38FE07AF1C04F6C03C21C6E28CDBF208E9981011B21829
	B9A47AC051344E569DDF4F4E6E44FA6E6BE66F5B201F7EDBC175BEB0500DBA122FDF8FCB91FB7A22BBAF02F305FB65308F6F9144E7D39F723989F85981D060CEB3D683B8G2EG84816639B91FEFF4D74C40A7950FD1A6C97EEBCBE64AB7CB5C71EA7E8C264F156E58FC7A86432FF64766531B25BC1F2E03315A8B3C66FF1C4560A468FF12435FF116B15CF2409D0C587B1FFB626B62651E443A5856236FF3BBFA267B5CBBBD7ABE77DE0F56678817530B7B0624E15098BDD890FEEFAF66A63A57C55F60D55B691B
	61DD26AFB1AF03FD7A3CDC6D1B4E0B27CF1F97DF5FF4DE826A3E2148638B6115BEBDDEC8D7AF1570FAB97582776957B1D4283967AD4D771D527B1F6B9E3A2FB4A8960AA75016FD81F0D351FF4967D5C688EF1A5C9745BBA5143940EB6AF7CA4C6A9E1997267BD4EEB761A8FE9EAF65F509391B57A517CBF7F1BDC6F8BDD12A755DC1186B6F1EB9F6124E1ACA394FBD1D4DF12D255C671E87BF7C9FD0CB87883749752A398DGG00A7GGD0CB818294G94G88G88GB3FBB0B63749752A398DGG00A7GG8C
	GGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG738DGGGG
**end of data**/
}
}