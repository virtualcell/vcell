package cbit.vcell.client.desktop;
import cbit.gui.DialogUtils;
import cbit.gui.UtilCancelException;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.client.desktop.testingframework.TestingFrameworkPanel;
import cbit.vcell.client.desktop.testingframework.TestingFrmwkTreeModel;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.solver.SimulationInfo;

import java.awt.event.ActionEvent;
import cbit.vcell.client.task.TFRefresh;
import cbit.vcell.client.task.TFAddTestSuite;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.TreePath;

import cbit.vcell.client.task.TFUpdateRunningStatus;
import cbit.vcell.client.task.TFRunSims;
import cbit.vcell.client.task.TFDuplicateTestSuite;
import cbit.vcell.client.task.UserCancelException;
import cbit.vcell.client.task.TFRemove;
import cbit.vcell.client.task.TFGenerateReport;
import cbit.vcell.client.task.TFUpdateTestCriteria;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.document.VCDocumentInfo;
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
			if (evt.getSource() == TestingFrameworkWindowPanel.this && (evt.getPropertyName().equals("documentManager"))){
				connPtoP1SetTarget();
				getJSplitPane1().setDividerLocation(500);
			}
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
		frame.setVisible(true);
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
public void refreshTree(TestSuiteInfoNew tsin) {
	gettestingFrameworkPanel().refreshTFTree(tsin);
}

public void selectInTreeView(BigDecimal testSuiteKey,BigDecimal testCaseKey,BigDecimal testCriteriaKey){
	gettestingFrameworkPanel().selectInTreeView(testSuiteKey,testCaseKey,testCriteriaKey);
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
	Vector<AsynchClientTask> tasksV = new java.util.Vector<AsynchClientTask>();
	
	try{
		TreePath[] selectedTreePaths = gettestingFrameworkPanel().getSelectedTreePaths();
		if(selectedTreePaths != null && selectedTreePaths.length > 1){
			Object refObj = ((BioModelNode)selectedTreePaths[0].getLastPathComponent()).getUserObject();
			for (int i = 1; i < selectedTreePaths.length; i++) {
				Object currentObj = ((BioModelNode)selectedTreePaths[i].getLastPathComponent()).getUserObject();
				if((refObj == null && currentObj == null) || 
					((refObj != null && currentObj != null) && (refObj.getClass().equals(currentObj.getClass()))) ||
					(refObj instanceof TestCaseNew && currentObj instanceof TestCaseNew)
				){
					continue;
				}else{
					throw new IllegalArgumentException("Multiple selections must all be of same type");
				}
			}
		}
		Object selectedObj = gettestingFrameworkPanel().getTreeSelection();
		if (e.getActionCommand().equals(TestingFrameworkPanel.QUERY_TCRITVAR_CROSSREF)) {
			if (selectedObj instanceof TestingFrmwkTreeModel.TestCriteriaVarUserObj && selectedTreePaths.length == 1) {
				final TestingFrmwkTreeModel.TestCriteriaVarUserObj tcritVaruserObj = (TestingFrmwkTreeModel.TestCriteriaVarUserObj)selectedObj;
				final TestSuiteInfoNew tsInfoNew = gettestingFrameworkPanel().getTestSuiteInfoOfTreePath(selectedTreePaths[0]);
				AsynchClientTask queryTCritCrossRef =
					new AsynchClientTask(){
						public boolean skipIfAbort() {
							return true;
						}
						public boolean skipIfCancel(UserCancelException exc) {
							return true;
						}
						public String getTaskName() {
							return "Query TCrit Var Cross Ref";
						}
						public int getTaskType() {
							return TASKTYPE_NONSWING_BLOCKING;
						}
						public void run(Hashtable hashTable) throws Exception {
							getTestingFrameworkWindowManager().queryTCritCrossRef(tsInfoNew,tcritVaruserObj.getTestCriteria(),tcritVaruserObj.getVariableComparisonSummary().getName());
						}
				};
				tasksV.add(queryTCritCrossRef);
			}else{
				throw new Exception("Selected Object is not a TestCriteria Variable! Failed to Query Var Cross Ref.");				
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.QUERY_TCRIT_CROSSREF)) {
			if (selectedObj instanceof TestCriteriaNew) {
				final TestCriteriaNew tcritNew = (TestCriteriaNew)selectedObj;
				final TestSuiteInfoNew tsInfoNew = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCriteria();
				AsynchClientTask queryTCritCrossRef =
					new AsynchClientTask(){
						public boolean skipIfAbort() {
							return true;
						}
						public boolean skipIfCancel(UserCancelException exc) {
							return true;
						}
						public String getTaskName() {
							return "Query TCrit Cross Ref";
						}
						public int getTaskType() {
							return TASKTYPE_NONSWING_BLOCKING;
						}
						public void run(Hashtable hashTable) throws Exception {
							getTestingFrameworkWindowManager().queryTCritCrossRef(tsInfoNew,tcritNew,null);
						}
				};
				tasksV.add(queryTCritCrossRef);
			}else{
				throw new Exception("Selected Object is not a TestCriteria! Failed to Query Cross Ref.");				
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.EDIT_ANNOT_TESTSUITE)) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				final TestSuiteInfoNew tsInfoNew = (TestSuiteInfoNew)selectedObj;
				String newAnnotation;
				// initialize fields
				String oldAnnotation = tsInfoNew.getTSAnnotation();
				// show the editor
				newAnnotation = cbit.gui.DialogUtils.showAnnotationDialog(this, oldAnnotation);
				final String finalAnnotation = newAnnotation;
				AsynchClientTask editTestSuiteAnnotation =
					new AsynchClientTask(){
						public boolean skipIfAbort() {
							return true;
						}
						public boolean skipIfCancel(UserCancelException exc) {
							return true;
						}
						public String getTaskName() {
							return "Edit TestSuite Annotation";
						}
						public int getTaskType() {
							return TASKTYPE_NONSWING_BLOCKING;
						}
						public void run(Hashtable hashTable) throws Exception {
							getTestingFrameworkWindowManager().updateTestSuiteAnnotation(tsInfoNew, finalAnnotation);
						}
				};
				tasksV.add(editTestSuiteAnnotation);
				tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfoNew);
				tasksV.add(tfRefreshTreeTask);
				
			}else{
				throw new Exception("Selected Object is not a TestSuite! Failed to Edit TestSuite Annotation.");				
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.EDIT_ANNOT_TESTCASE)) {
			if (selectedObj instanceof TestCaseNew) {
				final TestCaseNew tcNew = (TestCaseNew)selectedObj;
				String newAnnotation;
				// initialize fields
				String oldAnnotation = tcNew.getAnnotation();
				// show the editor
				newAnnotation = cbit.gui.DialogUtils.showAnnotationDialog(this, oldAnnotation);
				final String finalAnnotation = newAnnotation;
				AsynchClientTask editTestCaseAnnotation =
					new AsynchClientTask(){
						public boolean skipIfAbort() {
							return true;
						}
						public boolean skipIfCancel(UserCancelException exc) {
							return true;
						}
						public String getTaskName() {
							return "Edit TestCase Annotation";
						}
						public int getTaskType() {
							return TASKTYPE_NONSWING_BLOCKING;
						}
						public void run(Hashtable hashTable) throws Exception {
							getTestingFrameworkWindowManager().updateTestCaseAnnotation(tcNew, finalAnnotation);
						}
				};
				tasksV.add(editTestCaseAnnotation);
				TestSuiteInfoNew tsInfo = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCase();
				tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
				tasksV.add(tfRefreshTreeTask);
				
			}else{
				throw new Exception("Selected Object is not a TestCase! Failed to Edit TestCase Annotation.");				
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.TOGGLE_STEADYSTATE)) {
			if (selectedObj instanceof TestCaseNew) {
				final TestCaseNew testCase = (TestCaseNew)selectedObj;
				String okString = "Ok";
				String confirm =
					PopupGenerator.showWarningDialog(
						this,
						"Toggle TestCase '"+testCase.getVersion().getName()+"'\nfrom "+testCase.getType()+" to "+
						(testCase.getType().equals(TestCaseNew.EXACT)?TestCaseNew.EXACT_STEADY:TestCaseNew.EXACT),
						new String[] {okString,"Cancel"},okString);
				if (confirm.equals(okString)) {
					AsynchClientTask toggleSteadyState =
						new AsynchClientTask(){
							public boolean skipIfAbort() {
								return true;
							}
							public boolean skipIfCancel(UserCancelException exc) {
								return true;
							}
							public String getTaskName() {
								return "Toggle Steady State";
							}
							public int getTaskType() {
								return TASKTYPE_NONSWING_BLOCKING;
							}
							public void run(Hashtable hashTable) throws Exception {
								getTestingFrameworkWindowManager().toggleTestCaseSteadyState(new TestCaseNew[] {testCase});
							}
					};
					tasksV.add(toggleSteadyState);
					TestSuiteInfoNew tsInfo = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCase();
					tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
					tasksV.add(tfRefreshTreeTask);
				}else{
					throw UserCancelException.CANCEL_GENERIC;
				}
			} else {
				throw new Exception("Selected Object is not a TestCase! Cannot Toggle SteadyState");
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.ADD_TESTSUITE)) {
			TestingFrameworkWindowManager.NewTestSuiteUserInformation newTestSuiteUserInfo =
				getTestingFrameworkWindowManager().getNewTestSuiteInfoFromUser(null,null);
			tasksV.add(new TFAddTestSuite(getTestingFrameworkWindowManager(),newTestSuiteUserInfo.testSuiteInfoNew));
			tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),newTestSuiteUserInfo.testSuiteInfoNew);
			tasksV.add(tfRefreshTreeTask);
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.REFRESH_TESTSUITE)) {
			TestSuiteInfoNew tsin = null;
			if(selectedObj instanceof TestSuiteInfoNew){
				tsin = (TestSuiteInfoNew)selectedObj;
			}else if(selectedObj instanceof TestCaseNew){
				tsin = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCase();
			}else if(selectedObj instanceof TestCriteriaNew){
				tsin = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCriteria();
			}
			if(tsin == null){
				throw new IllegalArgumentException("Refresh error: Unexpected tree selection type="+selectedObj.getClass().getName());
			}
			tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsin);
			tasksV.add(new TFUpdateRunningStatus(getTestingFrameworkWindowManager(),tsin));
			tasksV.add(tfRefreshTreeTask);
			//getTestingFrameworkWindowManager().updateSimRunningStatus();
		} else if (e.getActionCommand().equals(TestingFrameworkPanel.ADD_TESTCASE)) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				TestSuiteInfoNew tsInfo = (TestSuiteInfoNew)selectedObj;
				TestCaseNew[] tcnArr = getTestingFrameworkWindowManager().getNewTestCaseArr();
				tasksV.add(new cbit.vcell.client.task.TFAddTestCases(getTestingFrameworkWindowManager(),tsInfo,tcnArr));					
				tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
				tasksV.add(tfRefreshTreeTask);
			} else {
				throw new Exception("Selected Object is not a test suite! Cannot add a test case");
			}
		}
		
		else if (e.getActionCommand().equals("Run All")) {
			if(selectedTreePaths == null || selectedTreePaths.length != 1){
				throw new IllegalArgumentException("Command '"+e.getActionCommand()+"' Single Selection Only!");
			}
			if (selectedObj instanceof TestSuiteInfoNew) {
				TestSuiteInfoNew tsInfo = (TestSuiteInfoNew)selectedObj;
				String okString = "Ok";
				String confirm =
					PopupGenerator.showWarningDialog(
						this,
						"Run All Sims for TestSuite '"+tsInfo.getTSID()+"'?",
						new String[] {okString,"Cancel"},okString);
				if(confirm.equals(okString)){
					tasksV.add(new TFRunSims(getTestingFrameworkWindowManager(),tsInfo));
					tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
					tasksV.add(tfRefreshTreeTask);
				}
			} else {
				throw new Exception("Selected Object is not a test suite!");
			}
		}
		
		else if (e.getActionCommand().equals("Run Simulations")) {
			if(selectedTreePaths == null || selectedTreePaths.length != 1){
				throw new IllegalArgumentException("Command '"+e.getActionCommand()+"' Single Selection Only!");
			}
			if (selectedObj instanceof TestCaseNew) {
				TestCaseNew testCase = (TestCaseNew)selectedObj;
				String okString = "Ok";
				String confirm =
					PopupGenerator.showWarningDialog(
						this,
						"Run All Sims for TestCase '"+testCase.getVersion().getName()+"'?",
						new String[] {okString,"Cancel"},okString);
				if(confirm.equals(okString)){
					tasksV.add(new TFRunSims(getTestingFrameworkWindowManager(),testCase));
					TestSuiteInfoNew tsInfo = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCase();
					tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
					tasksV.add(tfRefreshTreeTask);
				}
			} else {
				throw new Exception("Selected Object is not a TestCase! Cannot run simulations");
			}
		} else if (e.getActionCommand().equals("Run")) {
			if(selectedTreePaths != null &&
				selectedTreePaths.length > 0 &&
				selectedTreePaths[0].getLastPathComponent() instanceof BioModelNode &&
				((BioModelNode)selectedTreePaths[0].getLastPathComponent()).getUserObject() instanceof TestCriteriaNew){

				for(int i=0;i<selectedTreePaths.length;i+= 1){
						TestCriteriaNew tCriteria = (TestCriteriaNew)((BioModelNode)selectedTreePaths[i].getLastPathComponent()).getUserObject();
						tasksV.add(new TFRunSims(getTestingFrameworkWindowManager(),tCriteria));
				}
				TestSuiteInfoNew tsInfo = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCriteria();
				tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
				tasksV.add(tfRefreshTreeTask);
			
			} else {
				throw new Exception("Selected Object is not a TestCriteria!");
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
		
		
		else if (e.getActionCommand().equals(TestingFrameworkPanel.DUPLICATE_TESTSUITE)) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				TestSuiteInfoNew tsInfoOriginal = (TestSuiteInfoNew)selectedObj;
				TestingFrameworkWindowManager.NewTestSuiteUserInformation newTestSuiteUserInfo =
					getTestingFrameworkWindowManager().getNewTestSuiteInfoFromUser(tsInfoOriginal.getTSAnnotation(),tsInfoOriginal.getTSID());
				tasksV.add(new TFDuplicateTestSuite(getTestingFrameworkWindowManager(),tsInfoOriginal,newTestSuiteUserInfo.testSuiteInfoNew,newTestSuiteUserInfo.regrRefFlag));
				tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),newTestSuiteUserInfo.testSuiteInfoNew);
				tasksV.add(tfRefreshTreeTask);
				//getTestingFrameworkWindowManager().duplicateTestSuite(tsInfo);			
			} else {
				throw new Exception("Selected Object is not a test suite! Cannot duplicate");
			}		
		}
		
		else if (e.getActionCommand().equals(TestingFrameworkPanel.REMOVE_TESTCASE)) {
			if (selectedObj instanceof TestCaseNew) {

				String[][] rowData = new String[selectedTreePaths.length][3];
				TestSuiteInfoNew tsInfo = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCase();
				for(int i=0;i<selectedTreePaths.length;i+= 1){
					TestCaseNew tCase = (TestCaseNew)((BioModelNode)selectedTreePaths[i].getLastPathComponent()).getUserObject();
					rowData[i][0] = tsInfo.getTSID();
					tasksV.add(new TFRemove(getTestingFrameworkWindowManager(),tCase));
					if(tCase instanceof TestCaseNewBioModel){
						rowData[i][1] = "BM "+tCase.getVersion().getName();
						rowData[i][2] = ((TestCaseNewBioModel)tCase).getSimContextName();
					}else{
						rowData[i][1] = "MM "+tCase.getVersion().getName();
						rowData[i][2] = "N/A";
					}
				}
				DialogUtils.showComponentOKCancelTableList(this, "Confirm Remove TestCase(s)", new String[] {"Test Suite","Test Case","App"}, rowData, null);
				tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
				tasksV.add(tfRefreshTreeTask);
			} else {
				throw new Exception("Selected Object is not a TestCase, cannot remove selection!");
			}
		}
		
		else if (e.getActionCommand().equals(TestingFrameworkPanel.REMOVE_TESTSUITE)) {
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
					tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
					tasksV.add(tfRefreshTreeTask);
				}else{
					throw UserCancelException.CANCEL_GENERIC;
				}
				//getTestingFrameworkWindowManager().removeTestSuite(tsInfo);
			} else {
				throw new Exception("Selected Object is not a test suite! Cannot add a test case");
			}
		}





		
		else if (e.getActionCommand().equals("Generate TestSuite Report")) {
			final TreePath[] selectedTreePathsLocal = selectedTreePaths;
			new Thread(
				new Runnable(){
					public void run() {
						try{
							for(int i=0;selectedTreePathsLocal != null &&  i<selectedTreePathsLocal.length;i+= 1){
								Object selTreeNode = ((BioModelNode)selectedTreePathsLocal[i].getLastPathComponent()).getUserObject();
								if (selTreeNode instanceof TestSuiteInfoNew) {
									Vector<AsynchClientTask> tasksVLocal = new java.util.Vector<AsynchClientTask>();
									TestSuiteInfoNew tsInfo = (TestSuiteInfoNew)selTreeNode;
									tasksVLocal.add(new cbit.vcell.client.task.TFUpdateRunningStatus(getTestingFrameworkWindowManager(),tsInfo));
									tasksVLocal.add(new TFGenerateReport(getTestingFrameworkWindowManager(),tsInfo));
									final String END_NOTIFIER = "END NOTIFIER";
									tasksVLocal.add(new AsynchClientTask(){
										public boolean skipIfAbort() {
											return false;
										}
										public boolean skipIfCancel(UserCancelException exc) {
											return false;
										}
				
										public String getTaskName() {
											return END_NOTIFIER;
										}
				
										public int getTaskType() {
											return TASKTYPE_NONSWING_BLOCKING;
										}
				
										public void run(Hashtable hashTable) throws Exception {
											hashTable.put(END_NOTIFIER, END_NOTIFIER);
										}
										
									});
									tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
									tasksVLocal.add(tfRefreshTreeTask);

									AsynchClientTask[] tasksArr = new AsynchClientTask[tasksVLocal.size()];
									tasksVLocal.copyInto(tasksArr);
									java.util.Hashtable hashLocal = new java.util.Hashtable();
									ClientTaskDispatcher.dispatch(TestingFrameworkWindowPanel.this, hashLocal, tasksArr, true);
									//Wait for each report to complete before going on to next because report methods are not thread safe?
									while(!hashLocal.contains(END_NOTIFIER)){
										Thread.sleep(100);
									}
								} else {
									throw new Exception("Error GenerateTestSuiteReport\nSelected Object is not a TestSuite");
								}
							}
						}catch(Exception e){
							e.printStackTrace();
							PopupGenerator.showErrorDialog("Error GenerateTestSuiteReport\n"+e.getMessage());
							return;
						}
					}
				}
			).start();
			return;
		}else if (e.getActionCommand().equals("Generate TestCase Report")) {
			Vector<TestSuiteInfoNew> tsinV = new Vector<TestSuiteInfoNew>();
			for(int i=0;selectedTreePaths != null &&  i<selectedTreePaths.length;i+= 1){
				Object selTreeNode = ((BioModelNode)selectedTreePaths[i].getLastPathComponent()).getUserObject();
				if (selTreeNode instanceof TestCaseNew) {
					TestSuiteInfoNew tsInfo = gettestingFrameworkPanel().getTestSuiteInfoOfTreePath(selectedTreePaths[i]);
					if(!tsinV.contains(tsInfo)){
						tsinV.add(tsInfo);
						tasksV.add(new cbit.vcell.client.task.TFUpdateRunningStatus(getTestingFrameworkWindowManager(),tsInfo));
					}
					tasksV.add(new TFGenerateReport(getTestingFrameworkWindowManager(),(TestCaseNew)selTreeNode));
				} else {
					throw new Exception("Selected Object is not a TestCase");
				}
			}
			tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),(tsinV.size() == 1?tsinV.elementAt(0):null));
			tasksV.add(tfRefreshTreeTask);
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.GENTCRITREPORT_INTERNALREF_TESTCRITERIA) ||
				e.getActionCommand().equals(TestingFrameworkPanel.GENTCRITREPORT_USERDEFREF_TESTCRITERIA)) {
			SimulationInfo userDefinedRegrRef = null;
			if(e.getActionCommand().equals(TestingFrameworkPanel.GENTCRITREPORT_USERDEFREF_TESTCRITERIA)){
				final String CANCEL_STRING =  "Cancel";
				final String BM_STRING = "BioModel";
				final String MM_STRING = "MathModel";
				String result = PopupGenerator.showWarningDialog(this, "Choose Reference Model Type.", new String[]{BM_STRING,MM_STRING,CANCEL_STRING},CANCEL_STRING);
				if(result == null || result.equals(CANCEL_STRING)){
					return;
				}
				VCDocumentInfo userDefinedRegrRefModel = null;
				if(result.equals(BM_STRING)){
					userDefinedRegrRefModel = getTestingFrameworkWindowManager().getRequestManager().selectBioModelInfo(getTestingFrameworkWindowManager());
				}else{
					userDefinedRegrRefModel = getTestingFrameworkWindowManager().getRequestManager().selectMathModelInfo(getTestingFrameworkWindowManager());					
				}
				userDefinedRegrRef = TestingFrameworkWindowManager.getUserSelectedRefSimInfo(getTestingFrameworkWindowManager().getRequestManager(), userDefinedRegrRefModel);
			}
			Vector<TestSuiteInfoNew> tsinV = new Vector<TestSuiteInfoNew>();
			for(int i=0;selectedTreePaths != null &&  i<selectedTreePaths.length;i+= 1){
				Object selTreeNode = ((BioModelNode)selectedTreePaths[i].getLastPathComponent()).getUserObject();
				if (selTreeNode instanceof TestCriteriaNew) {
					TestSuiteInfoNew tsInfo = gettestingFrameworkPanel().getTestSuiteInfoOfTreePath(selectedTreePaths[i]);
					if(!tsinV.contains(tsInfo)){
						tsinV.add(tsInfo);
						tasksV.add(new cbit.vcell.client.task.TFUpdateRunningStatus(getTestingFrameworkWindowManager(),tsInfo));
					}
					tasksV.add(new TFGenerateReport(getTestingFrameworkWindowManager(),(TestCaseNew)((BioModelNode)selectedTreePaths[i].getParentPath().getLastPathComponent()).getUserObject(),(TestCriteriaNew)selTreeNode,
							userDefinedRegrRef));
				} else {
					throw new Exception("Selected Object is not a TestCriteria");
				}
			}
			tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),(tsinV.size() == 1?tsinV.elementAt(0):null));
			tasksV.add(tfRefreshTreeTask);
		}

		else if (e.getActionCommand().equals(TestingFrameworkPanel.EDIT_TESTCRITERIA)) {
			if (selectedObj instanceof TestCriteriaNew) {
				TestCriteriaNew tCriteria = (TestCriteriaNew)selectedObj;
				TestCaseNew testCase = gettestingFrameworkPanel().getTestCaseOfSelectedCriteria();
				TestCriteriaNew tcritNew = getTestingFrameworkWindowManager().getNewTestCriteriaFromUser(testCase.getType(),tCriteria);
				tasksV.add(new TFUpdateTestCriteria(getTestingFrameworkWindowManager(),tCriteria,tcritNew));
				TestSuiteInfoNew tsInfo = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCriteria();
				tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
				tasksV.add(tfRefreshTreeTask);
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
		if(!(exc instanceof UserCancelException) && !(exc instanceof UtilCancelException)){
			PopupGenerator.showErrorDialog(exc.getClass().getName()+"\n"+exc.getMessage());
		}
	}


}

}