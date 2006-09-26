package cbit.vcell.client.desktop;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import cbit.vcell.simulation.SimulationInfo;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.desktop.testingframework.TestingFrameworkPanel;
import cbit.vcell.numericstest.TestCriteriaNew;
import java.awt.event.ActionEvent;
import cbit.util.AsynchProgressPopup;
import cbit.util.MathModelInfo;
import cbit.util.UserCancelException;
import cbit.vcell.client.server.UserMessage;
import cbit.vcell.client.task.TFRefresh;
import cbit.vcell.client.task.TFAddTestSuite;
import java.util.Vector;
import cbit.vcell.client.task.TFUpdateRunningStatus;
import cbit.vcell.client.task.TFRunSims;
import cbit.vcell.client.task.TFAddTestCases;
import cbit.vcell.client.task.TFDuplicateTestSuite;
import cbit.vcell.client.task.TFRemove;
import cbit.vcell.client.task.TFGenerateReport;
import cbit.vcell.client.task.TFUpdateTestCriteria;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.controls.AsynchClientTask;
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
	private cbit.vcell.client.database.DocumentManager fieldDocumentManager = null;
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
public cbit.vcell.client.database.DocumentManager getDocumentManager() {
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
public void setDocumentManager(cbit.vcell.client.database.DocumentManager documentManager) {
	cbit.vcell.client.database.DocumentManager oldValue = fieldDocumentManager;
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
	D0CB838494G88G88G420171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359AFFF09465B545009103848C0AA3D3A3261A36485091EB2693E6E845A9EAC6F186ADB3CD1D7C01D4DB5A0916E9191619EEAE01848398045234C68D958181A3A10944A32117DC929289F8157308CA45CDEE131C593B3D6C6E65AEBF403E37EF3F3D4DE5AF97181219B75FFE6F7B713E777BFD5F055B7BF64656C25BDE0EAB5C4A69FFA78BB92E7B90474D39F37FA8432585BCE51C69EFBF40EEEEC3CDB1
	3499B0D622750F0ABB205D85108DB8EC0BA5CFD9B96794ABF4FC1E0EE7D0E863380D5A1C2F44CA9D57817D2F8132GF2A41A3F965A6D9A65197686FD5E25F9D95C26A7E45BCE5C4734A7033AA02D6D565BAA6E4F055C90523AD9BBA72EC9033155G48FC911769BD85ED23EB307FF4EF0FCF2D9E6E95C4313AD7748981355AABA8032A94345B5D02227A827D6DB26F974212BCF8519770CAE1979F90C4EEDC5B7FE931D45B0F63DA21DFAAA3EE46B052DC897DF5G85863EB30C78AE7876820C4B0C4F26C30DBA9F
	5DE30FF06B7B9F5B7EDA9CBE53E562AFC70E7219FD2427689F1ACE8E56226CB743D82562A9BB8E6086D08152815E0031BD5C7BA70EC05B5C23BAA53FDF8AB48703AD22D7BEA7F985913E6DF69851F11F8A3D126C65B852779C711845F92682DAF777B52FA319E455B25DAF79F4A91777509D36928B1964E50D5916A8A40BD5CAD4A6C4FBEDDC5A6B150936B91176C54ECC1BFB69C449D68B5A154F1DA8592C6F33C30D52EED4D157FF54EC6098FC976A76C1F8CE3301A4931E6CB4C51B0F7DB4985B24B25B685E45
	4E52D53C18AB2F3953D28E8BFF16EA6B8E5199022158332C50E8AE8A914D9263AC0FEB7865A6BC1D65938D0F7DDC98BBBE6CA9A37B3257235F26C3FF9940EA00FCGAE8C7686F0BC4CECEC7844875334B197AF2BADA11F6895E40E56FED03B8FDAA1224AFCE7C0D179C02F6061E59F5FA38A8A274FE928453375CCD8572459FEAF50B8A3848419D7FDD2GF6873FD585D921B96D353BF10EACE823EE1F9F3C1B7787B9366F795ABD500A3C22FE9674722A901DC1631EDA5449B06F019DE18378A63974FF0872F281
	7F83G91C38E4FE87B1D95E48897F6FB17A48D0602EE6404DB9DC6B97FD60B9D29701D99E6B26EBD09389D91CF5901885BE7CF5D6B26FDDAFD3DF8FCDE9EE1BE36E598E3F3C304594C6F3B194D346EFC10BB766C82CB1BC9837B7DBAC2369209447A4E36B06E0967227EED435D5768473B74B3CDBFD6BCF0DDB40A26B90F647BCC836AAA834E158310E74878FCDD9559EC8B2F884CCABF11414084EFD462F44EC743AC27A06D2D46791F56701BE04F4A91CFD96B88135DE7616E1879574AE3E25985B49A66737CBF
	E9100CDA6BB1AF894310A2DB052090704207220F1D2DC75B537D36CFB2567CC74387414F1D60D80A013FD80F3E482B2A6C6B89294285C145C49B9D3F2C51D2C2BD41E808E0347A6A11C7A98CFEEE4C9F2CC79F69158281C1D31631C49F77EBBEAA08029F8E6D14C28115704A07E8670342089EA3429FEE6350474590469B2A951671D89085E51F9FCCGC39617B3027EEB73D718E4A4828EEB1EB4E8B3C718AF065FE13EF8E0747B5C4A4BC11BD5AED9783D393662D172150A5128AF521986CEDA1FBD20458A9D2F
	50D9B7FF01E3D355AD64872F5AF14F86205585609DE5FE701B13B84EE20D6EGCEC994C92C063CFF51D00E7A9106C28204E88CEAE78D99BE5F70FA4C18C717A70DEFE8C0F90A26F8D8B02A49B3426A4271D112E5BA344BG56823C85F81A771CB12FF10C663947E8DE52984DF30F633C0AB1B6EF09BE3ECA1FDFBCCEB2A6197D61BD5423D68BFE438F7311EAA58C42335BBF5684C6F51E68AF5567E70C33FAEEE190E5388E7A85083B42F47E4CCED64F258FE6F1F9AF5514442BDB2B2E50B9DA2FC4F51EDB503C0D
	F464BE05B413E1AC7D8A538F7F11B927B4AB0A546B535219AE5BF23B59A68877B77B2B507679E4C56D84270DB0BD3CE14773E23E5351FA7EBF9AE135C896504D87A852664F0F20CFFA255E90EEFE0E8F707D6001648B1FABE6DF28386AA9938152GB22F3275ABAC57134CDEE9F668EBF317AE6632CBD2EDD6B57040838BEDB9D7C9D66B2F1AE4F6EFCFD1BEG1D7F2F46796B3E05F3FC4B68670E3E892DEA699B6CB434EE8F7717E94DA5FA4DE3BBF45B4884DAF9863D2C31939863B447292646A548FD126C973C
	0751522A7930DA2D45536AE6ED388D3F5FE3F639681DD60BB3E8C6F9B9F63B2E787B1DFA4FC3B2DE3C47AC6346EF3F3214F14AEB83363DC9A45B4624280C73677794ED8220FCB3640F2D2DD3122239FED152643CCFB5670EA114553224583AF81F06DF1994DB97FFB748F43986466A3E436A2413CDE8EF8B213F9CA097E0BDC0718C281BE6B0DF394FDE4BA528C3BB83D09E86F8714204C4237B47672710AE64863D7F5FD32897ADB7F0AC6F56C4925DEF9CB3A64A02567DBB61BAEF4CBA32092C6D6DDC22FBEB35
	EEB2FD4CE4A6DFE4A995683EFBEE486C3B63B3289E4D04B69B00625FBC055DF3574CE0714F2E22EECAEF7214D581B45C44F43360B446DD9774C500E4008C00D5G79G4549CCBFAF1EEAC9241FE6D6A038A56353991B6419CC87C72FCF97A38957D5A6DB2DBBB3963B2EAEE61EB53D4FA62DFBAB266F0A59C74F598E146BG0CA583AC8248864883588CD0B9134975D4E3D3A239EA0141ADB9815781F958C2247A9DA5C54BFB0D30378B00F00204B8957A29B3D90D7B7A9056D14B0CFE2556479CC07D4C60755476
	3B74BD934DA19AAB2E1B46F46E90A4DBC7D37C9A78CC13F58DFC2E493A863E50341B70300779A64C463FEC1A3AC66EED322A115F547879357AB74E229EBE8F7E4F319CB790592E4FF129122B06E3B1666EEA34B30BFA68C2D0742998BAA8A64C9CE2B1E175CC9693964B989338D91E328A006333187E6FD02672310ED9717245D2AD46DC1AC5F9215040BF226187CCF84A978E155D4DD498DBB70B7944BD27636B6D7E5356FADBF29A6BB6C58A493DC22CEE9EBADDE10CC53D27D3957CA8E21A13232DD7F939DF18
	245BC73575B496EFFD6EE92C13FBFCDE1F2CD75F24374523E63DE10DC1BAFFFC5FEB1ABDA90A20E88E5E4A2B3C3E666A98DB5326CA4E9A639CFB9D7165720643DAAE7B9D7165F25811D8AEB50E78F2E9F0A416CB33A31EDF902F7778718EA772A3D2C8059A76E6340B9F9BF735C6683B81468152EE26DCC7F67F23B04A9B33A6ECF7A1A874F249A938F6DCE2EB8BE0FEB1608EG3482F8D3D82D37218357C6BD24C60FC9330726327B5C14F8F63FCFC1BB4ECFA17B5EEA6077EB78CDA6BC59FD164CEA5052DBA06F
	5E42E275038A0B55EBEEE13E38DC45E5FA4CB4FD372B579BBF2783C463728D25C132F85E4FE240966039EEB6536F06EA349FA32EE135EC3737C8F8476DA2E32138F6B0D02A2F97E73338668E601E19504F81D8BB1B5D75A28759DDEF497D0B39B5AFD85F75725DD7EC9BE7132E4BEFB55DDB0E7494914D4592235985634937A2EE0D345734875A596310D42B2125F1DB50BC0EBEF101B30F57CD984773BAA72C6F1BB00EB2EA535793EFCF3E4BF85B7264B2CEFCB8B96E6FAF39BACF850C37276EAABCFC17EB9B59
	603DC34C868F279A7501E617EE68C78C5CA18D077774F48337D41E2E3D60DE66FEA4F53AF5434F27CDA336CE184EDBDEDEAA4AE015D2257733AD4E152B6368BC6BA75D2D264747AB57396EE860DA57D1ACBAA821CF9C18931B1BBF50704767446666AF824CAF9BE1ACE38E3BAF6CEE4178DAFC9B5CA3G9AGDCGC100545BD87EDE6FB84EA528E95DD2308B9219E8AA2574F7E5697AFE037AE989337F65BF798117BD2B55D646D95BFF46ED243F15FA1B1DFBD7A13644DF269379AB00319DF321A68268G388420
	4EE57C9DEBBEC67C35F9FDF86B61FB8734979C461C47A70423E8CCCE0B6622CC33072A2660A3E5AE1D07F254A7E731A634FCCFB066BCD001BE7EF1AB5DA4A91331FB626FD473BDF17DDCFA63A9CF03FC18466E099B9576CE561846F26381D53B8B88FDAA49751BF2A6574A9F5E47650DF8EC3B6348D5CD2373E724C5E3E654EE0FE1C922FF571B3E3F8E5C78BCB6F2C3E910CD7C33A5FEED363F453AB6BB54E2FD57F83FE56A3BC4FDCB7C7776469673FBBB59F4CD9B660EF52017CDG9573BCE547G9C7318BDBF
	5172AF32E78BDBEE7376E3ED083F6E290C7E034E783CAEF7A67ECDF005935FB8FBA4D5157C383FD1E7BBD1DFE04A9A12F02B1DA8A35957BF2052D49D7F1433D437D3C4136DBEBB043524D340F7A29697420259FEAB5FF644355FCB7348EED366CD34DF2AC77EDE0D77FE0BFB16497BB83DA6F9B7446EDAB973D8CD72F4C82B85213FE3BE7860FC16B3DF1EF057EA843CEB7E543910F47AE4FB9534DE181B8410891083308660C500ADGFBEFE77A3D5CFA94DA8F7F0D9E09B1ECC93226E1C09AE53026E619770122
	E95DC7ED71756CE8CB2C674EB66DFD9B74639E896A770173ED28A3292FCF918CFB3A50D6EE60BA95172478709EC1FC56F7A01F470117AE00A0C08A40A20065G2B81728DBEDFE8BBBC851FD4BCC619A4799F89CD15EF365C9E2F66F7EA7F83D01E9E1BCF3BB4FCE5FAECBEFDB6447269E198CBCDE7B5FF290AB2AA007E7B456C9DE72706ABDD60A95BED587BF36D71F5712B76443A5850EE6DF39B5BA77B5C4B6D56BE7727F6334F91EE0B362FAC7403466841027036B3181B68DE97FD0357ED27E30AF7190E443C
	38BA2CF971F4CC66654B8EEBDE780E493C74697B4A46638B61458EABDEC8D74DA1D6AF9FD840FC3AF39855CE5F1F1A3EFF993A7139EE538D25C13110BF08366C8B001B7262C51FD79DA0FCE5E4E7945FA1204CB9D6D3F7072A74BDDC8BA67B14D747D17CCC8A313AA4320055A559212DCC0F862FB3A7786EB2183B4AE2EFB3104E9605184FBDDC44F07706184FBD86FE78BFD0CB878851DBB3C5308DGG00A7GGD0CB818294G94G88G88G420171B451DBB3C5308DGG00A7GG8CGGGGGGGGG
	GGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6A8DGGGG
**end of data**/
}
}