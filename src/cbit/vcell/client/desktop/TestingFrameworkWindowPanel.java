/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;

import org.vcell.util.Compare;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DialogUtils.TableListResult;
import org.vcell.util.gui.UtilCancelException;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.desktop.testingframework.TestingFrameworkPanel;
import cbit.vcell.client.desktop.testingframework.TestingFrmwkTreeModel;
import cbit.vcell.client.desktop.testingframework.TestingFrmwkTreeModel.LoadTestTreeInfo;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.client.task.TFAddTestSuite;
import cbit.vcell.client.task.TFDuplicateTestSuite;
import cbit.vcell.client.task.TFGenerateReport;
import cbit.vcell.client.task.TFRefresh;
import cbit.vcell.client.task.TFRemove;
import cbit.vcell.client.task.TFRemoveTestCriteria;
import cbit.vcell.client.task.TFRunSims;
import cbit.vcell.client.task.TFUpdateRunningStatus;
import cbit.vcell.client.task.TFUpdateTestCriteria;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.modeldb.MathVerifier;
import cbit.vcell.numericstest.LoadTestInfoOP;
import cbit.vcell.numericstest.LoadTestInfoOP.LoadTestOpFlag;
import cbit.vcell.numericstest.LoadTestInfoOpResults;
import cbit.vcell.numericstest.LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestCaseNewMathModel;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.SimulationInfo;
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
	private javax.swing.JSplitPane ivjJSplitPane1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TestingFrameworkWindowPanel.this.gettestingFrameworkPanel()) 
				connEtoC1(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == TestingFrameworkWindowPanel.this && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))){
				connPtoP1SetTarget();
				getJSplitPane1().setDividerLocation(500);
			}
			if (evt.getSource() == TestingFrameworkWindowPanel.this.gettestingFrameworkPanel() && (evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name))) 
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
			getJSplitPane1().add(new JPanel(),"right");  //
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

public void refreshTree(LoadTestInfoOpResults loadTestInfoOpResults){
	gettestingFrameworkPanel().refreshTFTree(loadTestInfoOpResults);
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
	firePropertyChange(CommonTask.DOCUMENT_MANAGER.name, oldValue, documentManager);
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


private TFGenerateReport.VCDocumentAndSimInfo getUserSelectedSimulationInfo() throws Exception{
	final String CANCEL_STRING =  "Cancel";
	final String BM_STRING = "BioModel";
	final String MM_STRING = "MathModel";
	String result = PopupGenerator.showWarningDialog(this, "Choose Reference Model Type.", new String[]{BM_STRING,MM_STRING,CANCEL_STRING},CANCEL_STRING);
	if(result == null || result.equals(CANCEL_STRING)){
		throw UserCancelException.CANCEL_GENERIC;
	}
	VCDocumentInfo userDefinedRegrRefModel = null;
	VCDocument vcDocument = null;
	if(result.equals(BM_STRING)){
		userDefinedRegrRefModel = getTestingFrameworkWindowManager().getRequestManager().selectBioModelInfo(getTestingFrameworkWindowManager());
		vcDocument = getTestingFrameworkWindowManager().getRequestManager().getDocumentManager().getBioModel(userDefinedRegrRefModel.getVersion().getVersionKey());
	}else{
		userDefinedRegrRefModel = getTestingFrameworkWindowManager().getRequestManager().selectMathModelInfo(getTestingFrameworkWindowManager());					
		vcDocument = getTestingFrameworkWindowManager().getRequestManager().getDocumentManager().getMathModel(userDefinedRegrRefModel.getVersion().getVersionKey());
	}
	SimulationInfo simInfo =
		getTestingFrameworkWindowManager().getUserSelectedRefSimInfo(getTestingFrameworkWindowManager().getRequestManager(), userDefinedRegrRefModel);
	TFGenerateReport.VCDocumentAndSimInfo vcDocumentAndsimInfo = new TFGenerateReport.VCDocumentAndSimInfo(simInfo,vcDocument);
	return vcDocumentAndsimInfo;
}

private class EnterDBAndSoftwareVersPanel extends JPanel {
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JPasswordField textField_4;
	public EnterDBAndSoftwareVersPanel() {
		setLayout(new GridLayout(0, 2, 5, 0));
		
		JLabel label_1 = new JLabel("Software Version (No DB update if empty):");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		add(label_1);
		
		textField = new JTextField();
		add(textField);
		textField.setColumns(10);
		
		JLabel label = new JLabel("Database Host:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setHorizontalTextPosition(SwingConstants.LEADING);
		add(label);
		
		textField_1 = new JTextField("");
		add(textField_1);
		textField_1.setColumns(10);
		
		JLabel label_2 = new JLabel("Database name");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setHorizontalTextPosition(SwingConstants.LEADING);
		add(label_2);
		
		textField_2 = new JTextField("");
		add(textField_2);
		textField_2.setColumns(10);
		
		JLabel label_3 = new JLabel("Database Schema");
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);
		label_3.setHorizontalTextPosition(SwingConstants.LEADING);
		add(label_3);
		
		textField_3 = new JTextField("");
		add(textField_3);
		textField_3.setColumns(10);
		
		JLabel label_4 = new JLabel("Database password");
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);
		label_4.setHorizontalTextPosition(SwingConstants.LEADING);
		add(label_4);
		
		textField_4 = new JPasswordField();
		add(textField_4);
		textField_4.setColumns(10);
	}

	public String getSoftwareVersion(){
		return textField.getText();
	}
	public String getDBHost(){
		return textField_1.getText();
	}
	public String getDBName(){
		return textField_2.getText();
	}

	public String getDBSchema(){
		return textField_3.getText();
	}

	public String getDBPassword(){
		return textField_4.getText();
	}

}
private EnterDBAndSoftwareVersPanel enterDBAndSoftwareVersPanel = new EnterDBAndSoftwareVersPanel();
/**
 * Comment
 */
private void testingFrameworkPanel_actionPerformed(final ActionEvent e) {

	if(tfRefreshTreeTask == null){
		tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager());
	}
	
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	Vector<AsynchClientTask> tasksV = new Vector<AsynchClientTask>();
	
	try{
		final TreePath[] selectedTreePaths = gettestingFrameworkPanel().getSelectedTreePaths();
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
		final Object selectedObj = gettestingFrameworkPanel().getTreeSelection();
		if(e.getActionCommand().equals(TestingFrameworkPanel.DELETE_XML_LOAD_TEST)){
			int result = DialogUtils.showComponentOKCancelDialog(this, new JLabel("Delete "+selectedTreePaths.length+" Load Tests?"), "Confirm Load Test Delete");
			if(result != JOptionPane.OK_OPTION){
				return;
			}
			AsynchClientTask deleteLoadTestTask = new AsynchClientTask("Deleting Load Test...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					LoadTestInfoOP deleteLoadTestInfoOP = 
						new LoadTestInfoOP(LoadTestOpFlag.delete,null,null);
					LoadTestSoftwareVersionTimeStamp[] deleteTheseversTimestamps = 
						new LoadTestSoftwareVersionTimeStamp[selectedTreePaths.length];
					for (int i = 0; i < selectedTreePaths.length; i++) {
						deleteTheseversTimestamps[i] = 
							((LoadTestTreeInfo)((BioModelNode)selectedTreePaths[i].getLastPathComponent()).getUserObject()).loadTestSoftwareVersionTimeStamp;
					}
					deleteLoadTestInfoOP.setDeleteInfo(deleteTheseversTimestamps);
					getTestingFrameworkWindowManager().getRequestManager().getDocumentManager().doTestSuiteOP(deleteLoadTestInfoOP);
					final ActionEvent refreshAction = new ActionEvent(this, 0, TestingFrameworkPanel.REFRESH_XML_LOAD_TEST);
					new Thread(new Runnable() {public void run() {testingFrameworkPanel_actionPerformed(refreshAction);}}).start();}
			};
			tasksV.add(deleteLoadTestTask);
		}else if(e.getActionCommand().equals(TestingFrameworkPanel.RUN_XML_LOAD_TEST_All) ||
				e.getActionCommand().equals(TestingFrameworkPanel.RUN_XML_LOAD_TEST_MODELS) ||
				e.getActionCommand().equals(TestingFrameworkPanel.RUN_XML_LOAD_TEST_USERS)){

			final Date[] beginDate = new Date[1];
			final Date[] endDate = new Date[1];

			final TreeSet<String> uniqueUserIDTreeSet = new TreeSet<String>();
			final Vector<KeyValue> bioAndMathModelKeyValueV = new Vector<KeyValue>();
			String typeMsg = "All";
			if(!e.getActionCommand().equals(TestingFrameworkPanel.RUN_XML_LOAD_TEST_All)){
				if(selectedTreePaths != null && selectedTreePaths.length > 0){
					Object refObj = ((BioModelNode)selectedTreePaths[0].getLastPathComponent()).getUserObject();
					if(refObj instanceof TestingFrmwkTreeModel.LoadTestTreeInfo){
						for (int i = 0; i < selectedTreePaths.length; i++) {
							refObj = ((BioModelNode)selectedTreePaths[i].getLastPathComponent()).getUserObject();
							uniqueUserIDTreeSet.add(((TestingFrmwkTreeModel.LoadTestTreeInfo)refObj).userid);
							if(!e.getActionCommand().equals(TestingFrameworkPanel.RUN_XML_LOAD_TEST_USERS)){
								bioAndMathModelKeyValueV.add(((TestingFrmwkTreeModel.LoadTestTreeInfo)refObj).bioOrMathModelKey);								
							}
						}
					}
				}
			}else{
//				TreeSet<VCDocumentInfo> dateRangeDocInfoTreeSet =
//					new TreeSet<VCDocumentInfo>(new Comparator<VCDocumentInfo>() {
//						public int compare(VCDocumentInfo o1, VCDocumentInfo o2) {
//							return o1.getVersion().getDate().compareTo(o2.getVersion().getDate());
//						}
//					});
//				BioModelInfo[] allBioModelInfos = getDocumentManager().getBioModelInfos();
//				dateRangeDocInfoTreeSet.addAll(Arrays.asList(allBioModelInfos));
//				MathModelInfo[] allMathModelInfos = getDocumentManager().getMathModelInfos();
//				dateRangeDocInfoTreeSet.addAll(Arrays.asList(allMathModelInfos));
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//				Date firstDate = simpleDateFormat.parse(simpleDateFormat.format(dateRangeDocInfoTreeSet.first().getVersion().getDate()));
//				Date lastDate = simpleDateFormat.parse(simpleDateFormat.format(dateRangeDocInfoTreeSet.last().getVersion().getDate()));
				Date firstDate = simpleDateFormat.parse("2000-01-01");
				Date lastDate = simpleDateFormat.parse(simpleDateFormat.format(Calendar.getInstance().getTime()));
				String allDateRangeString =
					simpleDateFormat.format(firstDate)+
					","+
					simpleDateFormat.format(lastDate);
				while(beginDate[0]==null || endDate[0] == null){
					try{
						String dateRangeString =
							DialogUtils.showInputDialog0(this,
								"Enter Date Range (begin,end) to include (e.g. '"+allDateRangeString+"')",
								allDateRangeString);
						beginDate[0] = simpleDateFormat.parse(dateRangeString.substring(0, dateRangeString.indexOf(",")));
						endDate[0] = simpleDateFormat.parse(dateRangeString.substring(dateRangeString.indexOf(",")+1));
//						if(beginDate.compareTo(firstDate) != 0 ||
//							endDate.compareTo(lastDate) != 0){
//							Iterator<VCDocumentInfo> vcDocIter = dateRangeDocInfoTreeSet.iterator();
//							while(vcDocIter.hasNext()){
//								VCDocumentInfo vcDocInfo = vcDocIter.next();
//								Date docDate = simpleDateFormat.parse(simpleDateFormat.format(vcDocInfo.getVersion().getDate()));
//								if(docDate.compareTo(beginDate) < 0 ||
//										docDate.compareTo(endDate) > 0){
//									continue;
//								}
//								uniqueUserIDTreeSet.add(vcDocInfo.getVersion().getOwner().getName());
//								bioAndMathModelKeyValueV.add(vcDocInfo.getVersion().getVersionKey());
//							}
//						}
					}catch(UtilCancelException uce){
						return;
					}catch(Exception e2){
						DialogUtils.showErrorDialog(this, e2.getMessage());
					}
				}
				if(beginDate[0].compareTo(firstDate) == 0 && endDate[0].compareTo(lastDate) == 0){
					beginDate[0] = null;
					endDate[0] = null;
				}else{
					typeMsg = "between "+simpleDateFormat.format(beginDate[0])+","+simpleDateFormat.format(endDate[0]);					
				}
			}

			if(e.getActionCommand().equals(TestingFrameworkPanel.RUN_XML_LOAD_TEST_MODELS)){
				typeMsg = "Models ("+bioAndMathModelKeyValueV.size()+")";
			}else if(e.getActionCommand().equals(TestingFrameworkPanel.RUN_XML_LOAD_TEST_USERS)){
				typeMsg = "Users ("+uniqueUserIDTreeSet.size()+")";
			}

			int result =
				DialogUtils.showComponentOKCancelDialog(TestingFrameworkWindowPanel.this, new JLabel("Run "+typeMsg+" Load Tests?"), "Confirm Load Test Run");
			if(result != JOptionPane.OK_OPTION){
				return;
			}

			result = DialogUtils.showComponentOKCancelDialog(this, enterDBAndSoftwareVersPanel, "Enter Software Version running load test");
			if(result != JOptionPane.OK_OPTION){
				return;
			}
			AsynchClientTask runXMLLoadTestTask = new AsynchClientTask("Running XML Load Test...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {

					String[] uniqueUserIDArr = null;
					KeyValue[] bioAndMathModelKeyValueArr = null;
					if(beginDate[0] != null && endDate[0] != null){
						LoadTestInfoOpResults loadTestInfoOpResults =
							getTestingFrameworkWindowManager().getLoadTestInfoBetweenDates(beginDate[0], endDate[0]);
						uniqueUserIDArr = loadTestInfoOpResults.getUniqueUserIDsBetweenDates();
						bioAndMathModelKeyValueArr = loadTestInfoOpResults.getKeyValuesBetweenDates();
					}else{
						uniqueUserIDArr = (uniqueUserIDTreeSet.size()==0?null:uniqueUserIDTreeSet.toArray(new String[0]));
						bioAndMathModelKeyValueArr = (bioAndMathModelKeyValueV.size()==0?null:bioAndMathModelKeyValueV.toArray(new KeyValue[0]));
					}
					MathVerifier mathVerifier = MathVerifier.createMathVerifier(
							enterDBAndSoftwareVersPanel.getDBHost(),
							enterDBAndSoftwareVersPanel.getDBName(),
							enterDBAndSoftwareVersPanel.getDBSchema(),
							enterDBAndSoftwareVersPanel.getDBPassword());
					mathVerifier.runLoadTest(
							uniqueUserIDArr,
							bioAndMathModelKeyValueArr,
							enterDBAndSoftwareVersPanel.getSoftwareVersion(),
							(enterDBAndSoftwareVersPanel.getSoftwareVersion().length()==0?false:true),
							(enterDBAndSoftwareVersPanel.getSoftwareVersion().length()==0?Compare.DEFAULT_COMPARE_LOGGER:null));
				}
			};
			tasksV.add(runXMLLoadTestTask);
		}else if(e.getActionCommand().equals(TestingFrameworkPanel.REFRESH_XML_LOAD_TEST) /*||
				e.getActionCommand().equals(TestingFrameworkPanel.REFRESH_INCLUDE_SLOW_XML_LOAD_TEST)*/){
			final String LOADTESTDETAILS_KEY = "LOADTESTDETAILS_KEY";
			AsynchClientTask getFailedLoadTest =
				new AsynchClientTask("Getting FailedLoadTests...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					@Override
					public void run(Hashtable<String, Object> hashTable) throws Exception {
/*						Integer slowLoadThreshold = null;
						if(e.getActionCommand().equals(TestingFrameworkPanel.REFRESH_INCLUDE_SLOW_XML_LOAD_TEST)){
							String result = DialogUtils.showInputDialog0(TestingFrameworkWindowPanel.this, "Enter load time threshold (millseconds)","10000");
							slowLoadThreshold = new Integer(result);
						}
*/
						if(gettestingFrameworkPanel().getSlowLoadThreshold() != null &&
								gettestingFrameworkPanel().getLoadTestSQLCondition() != null){
							throw new IllegalArgumentException(
								"SlowLoadThreshold and 'SQL Condition' cannot both be non-null at the same time.");
						}
							hashTable.put(
							LOADTESTDETAILS_KEY,
							getTestingFrameworkWindowManager().getLoadTestDetails(
								gettestingFrameworkPanel().getSlowLoadThreshold(),
								gettestingFrameworkPanel().getLoadTestSQLCondition()));
					}
				};
			AsynchClientTask refreshFailedLoadTest = new AsynchClientTask("Refreshing FailedLoadTests...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					getTestingFrameworkWindowManager().refreshLoadTest((LoadTestInfoOpResults)hashTable.get(LOADTESTDETAILS_KEY));
				}
			};
			tasksV.add(getFailedLoadTest);
			tasksV.add(refreshFailedLoadTest);
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.QUERY_TCRITVAR_CROSSREF)) {
			if (selectedObj instanceof TestingFrmwkTreeModel.TestCriteriaVarUserObj && selectedTreePaths.length == 1) {
				final TestingFrmwkTreeModel.TestCriteriaVarUserObj tcritVaruserObj = (TestingFrmwkTreeModel.TestCriteriaVarUserObj)selectedObj;
				final TestSuiteInfoNew tsInfoNew = gettestingFrameworkPanel().getTestSuiteInfoOfTreePath(selectedTreePaths[0]);
				AsynchClientTask queryTCritCrossRef =
					new AsynchClientTask("Query TCrit Var Cross Ref", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING){						
						public void run(Hashtable<String, Object> hashTable) throws Exception {
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
					new AsynchClientTask("Query TCrit Cross Ref", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING){						
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							getTestingFrameworkWindowManager().queryTCritCrossRef(tsInfoNew,tcritNew,null);
						}
				};
				tasksV.add(queryTCritCrossRef);
			}else{
				throw new Exception("Selected Object is not a TestCriteria! Failed to Query Cross Ref.");				
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.LOCK_TESTSUITE)) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				final TestSuiteInfoNew tsInfoNew = (TestSuiteInfoNew)selectedObj;
				AsynchClientTask lockTestSuiteTask =
					new AsynchClientTask("Lock TestSuite", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING){
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							getTestingFrameworkWindowManager().lockTestSuite(tsInfoNew);
						}
					};
				tasksV.add(lockTestSuiteTask);
				tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfoNew);
				tasksV.add(tfRefreshTreeTask);
			}else{
				throw new Exception("Selected Object is not a TestSuite! Failed to Lock TestSuite.");				
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.EDIT_ANNOT_TESTSUITE)) {
			if (selectedObj instanceof TestSuiteInfoNew) {
				final TestSuiteInfoNew tsInfoNew = (TestSuiteInfoNew)selectedObj;
				String newAnnotation;
				// initialize fields
				String oldAnnotation = tsInfoNew.getTSAnnotation();
				// show the editor
				newAnnotation = org.vcell.util.gui.DialogUtils.showAnnotationDialog(this, oldAnnotation);
				final String finalAnnotation = newAnnotation;
				AsynchClientTask editTestSuiteAnnotation =
					new AsynchClientTask("Edit TestSuite Annotation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING){
						public void run(Hashtable<String, Object> hashTable) throws Exception {
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
				newAnnotation = org.vcell.util.gui.DialogUtils.showAnnotationDialog(this, oldAnnotation);
				final String finalAnnotation = newAnnotation;
				AsynchClientTask editTestCaseAnnotation =
					new AsynchClientTask("Edit TestCase Annotation", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING){						
						public void run(Hashtable<String, Object> hashTable) throws Exception {
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
						new AsynchClientTask("Toggle Steady State", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING){							
							public void run(Hashtable<String, Object> hashTable) throws Exception {
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
		} else if (e.getActionCommand().equals("View Results")) {
			if (selectedObj instanceof cbit.vcell.numericstest.TestCriteriaNew) {
				TestCriteriaNew tCriteria = (TestCriteriaNew)selectedObj;
				getTestingFrameworkWindowManager().viewResults(tCriteria);			
			} else {
				PopupGenerator.showErrorDialog(TestingFrameworkWindowPanel.this, "Selected Object is not a TestCriteria!");
			}
		} else if (e.getActionCommand().equals(TestingFrameworkPanel.COMPARERREGR_INTERNALREF_TESTCRITERIA) ||
				e.getActionCommand().equals(TestingFrameworkPanel.COMPARERREGR_USERDEFREF_TESTCRITERIA)) {
			if (selectedObj instanceof cbit.vcell.numericstest.TestCriteriaNew) {
				TestCriteriaNew tCriteria = (TestCriteriaNew)selectedObj;
				SimulationStatus simStatus = getTestingFrameworkWindowManager().getRequestManager().getServerSimulationStatus(tCriteria.getSimInfo());					
				if (simStatus.isRunning()) {
					PopupGenerator.showErrorDialog(TestingFrameworkWindowPanel.this, "Selected simulation is still running!");
					return;
				}
				SimulationInfo simulationInfo = tCriteria.getRegressionSimInfo();
				if(e.getActionCommand().equals(TestingFrameworkPanel.COMPARERREGR_USERDEFREF_TESTCRITERIA)){
					try{
						simulationInfo = getUserSelectedSimulationInfo().getSimInfo();
					}catch(UserCancelException e2){
						return;
					}
				}
				if (simulationInfo == null) {
					PopupGenerator.showErrorDialog(TestingFrameworkWindowPanel.this, "Either the selected simulation does not belong to a REGRESSION test or the regression simInfo is not set!");
					return;
				}
				getTestingFrameworkWindowManager().compare(tCriteria,simulationInfo);			
			} else {
				PopupGenerator.showErrorDialog(TestingFrameworkWindowPanel.this, "Selected Object is not a TestCriteria!");
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
									tasksVLocal.add(new AsynchClientTask(END_NOTIFIER, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING, false, false){
										public void run(Hashtable<String, Object> hashTable) throws Exception {
											hashTable.put(END_NOTIFIER, END_NOTIFIER);
										}
										
									});
									tfRefreshTreeTask = new TFRefresh(getTestingFrameworkWindowManager(),tsInfo);
									tasksVLocal.add(tfRefreshTreeTask);

									AsynchClientTask[] tasksArr = new AsynchClientTask[tasksVLocal.size()];
									tasksVLocal.copyInto(tasksArr);
									Hashtable<String, Object> hashLocal = new Hashtable<String, Object>();
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
							PopupGenerator.showErrorDialog(TestingFrameworkWindowPanel.this, "Error GenerateTestSuiteReport\n"+e.getMessage(), e);
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
			TFGenerateReport.VCDocumentAndSimInfo userDefinedRegrRef = null;
			if(e.getActionCommand().equals(TestingFrameworkPanel.GENTCRITREPORT_USERDEFREF_TESTCRITERIA)){
				try{
					userDefinedRegrRef = getUserSelectedSimulationInfo();
				}catch(UserCancelException e2){
					return;
				}
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
		else if (e.getActionCommand().equals(TestingFrameworkPanel.REMOVE_DIFF_TESTCRITERIA)) {
			final int OLDER = 0;
			final int NEWER = 1;
			final TestSuiteInfoNew[] testSuiteInfoHolder = new TestSuiteInfoNew[2];

			if(selectedTreePaths.length == 2 &&
				((BioModelNode)selectedTreePaths[0].getLastPathComponent()).getUserObject() instanceof TestSuiteInfoNew &&
				((BioModelNode)selectedTreePaths[1].getLastPathComponent()).getUserObject() instanceof TestSuiteInfoNew){
				//do outside task because its quick
				TestSuiteInfoNew testSuiteInfoOlder = (TestSuiteInfoNew)((BioModelNode)selectedTreePaths[0].getLastPathComponent()).getUserObject();
				TestSuiteInfoNew testSuiteInfoNewer = (TestSuiteInfoNew)((BioModelNode)selectedTreePaths[1].getLastPathComponent()).getUserObject();
				if(testSuiteInfoOlder.getTSDate().compareTo(testSuiteInfoNewer.getTSDate()) > 0){
					TestSuiteInfoNew temp = testSuiteInfoOlder;
					testSuiteInfoOlder = testSuiteInfoNewer;
					testSuiteInfoNewer = temp;
				}
				testSuiteInfoHolder[OLDER] = testSuiteInfoOlder;
				testSuiteInfoHolder[NEWER] = testSuiteInfoNewer;
				
				AsynchClientTask showDiffTask = new AsynchClientTask("Show Differential TestCriteria...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						ArrayList<TestSuiteNew> bothTestSuites = new ArrayList<TestSuiteNew>();
						if(getClientTaskStatusSupport() != null){getClientTaskStatusSupport().setProgress(25);getClientTaskStatusSupport().setMessage("Getting TestSuiteInfo '"+testSuiteInfoHolder[OLDER].getTSID()+"'");}
						bothTestSuites.add(getDocumentManager().getTestSuite(testSuiteInfoHolder[OLDER].getTSKey()));
						if(getClientTaskStatusSupport() != null){getClientTaskStatusSupport().setProgress(50);getClientTaskStatusSupport().setMessage("Getting TestSuiteInfo '"+testSuiteInfoHolder[NEWER].getTSID()+"'");}
						bothTestSuites.add(getDocumentManager().getTestSuite(testSuiteInfoHolder[NEWER].getTSKey()));
						HashMap<String, TestCriteriaNew> olderTestCritHashMap = new HashMap<String, TestCriteriaNew>();
						ArrayList<String> olderTestCaseBaseNames = new ArrayList<String>();
						TreeMap<String, TestCriteriaNew> newDiffTestCriteria = new TreeMap<String, TestCriteriaNew>();
						int excludedCount = 0;
						for(TestSuiteNew currentTestSuite:bothTestSuites){
							BigDecimal currentTestSuiteKey = currentTestSuite.getTSInfoNew().getTSKey();
							for(TestCaseNew tcn:currentTestSuite.getTestCases()){
								String prefixInfo = "TS='"+currentTestSuite.getTSInfoNew().getTSID()+"' Type='"+tcn.getType()+"' ";
								String baseName =
									(tcn instanceof TestCaseNewBioModel?
										"BioModel='"+((TestCaseNewBioModel)tcn).getBioModelInfo().getVersion().getName()+"' App='"+((TestCaseNewBioModel)tcn).getSimContextName()+"'":
										"MathModel='"+((TestCaseNewMathModel)tcn).getMathModelInfo().getVersion().getName())+"'";
								if(currentTestSuite.getTSInfoNew().getTSKey().equals(testSuiteInfoHolder[OLDER].getTSKey())){
									if(!olderTestCaseBaseNames.contains(baseName)){
										olderTestCaseBaseNames.add(baseName);
									}else{
										throw new Exception("Old testcase names duplicated.");
									}
								}
								for(TestCriteriaNew tcrit:tcn.getTestCriterias()){
									String name = baseName+" Sim='"+tcrit.getSimInfo().getName()+"'";
									if(olderTestCritHashMap.containsKey(name)){
										if(currentTestSuiteKey.equals(testSuiteInfoHolder[OLDER].getTSKey())){throw new Exception("---Problem--- Older names not unique");}
										continue;
									}else if(currentTestSuiteKey.equals(testSuiteInfoHolder[NEWER].getTSKey())){
										if(!olderTestCaseBaseNames.contains(baseName)){
											excludedCount+= 1;//this happens when new TestSuite has added TestCase after duplication
										}else if(newDiffTestCriteria.put(prefixInfo+name,tcrit) != null){
											throw new Exception("---Problem--- Newer added names not unique");
										}
										continue;
									}
									olderTestCritHashMap.put(name,tcrit);
								}

							}
						}
						if(newDiffTestCriteria.size() > 0){
							if(getClientTaskStatusSupport() != null){getClientTaskStatusSupport().setMessage("Showing Differential list...");}
							String[][] rowData = new String[newDiffTestCriteria.size()][1];
							String[] addedNamesArr = newDiffTestCriteria.keySet().toArray(new String[0]);
							for (int i = 0; i < addedNamesArr.length; i++) {
								rowData[i][0] = addedNamesArr[i];
							}
							final String DELETE = "Delete";
							TableListResult result = 
								DialogUtils.showComponentOptionsTableList(
									gettestingFrameworkPanel(),
									"Remove TestCriteria in TS='"+testSuiteInfoHolder[NEWER].getTSID()+"' that were not in TS='"+testSuiteInfoHolder[OLDER].getTSID()+"' (count="+rowData.length+" of "+olderTestCritHashMap.size()+", excluded="+excludedCount+")",
									new String[] {"Diff TestCriteria"},
									rowData,
									ListSelectionModel.MULTIPLE_INTERVAL_SELECTION,
									null,
									new String[] {DELETE,"Cancel"},
									DELETE,
									null);
							if(result != null && result.selectedOption != null && result.selectedOption.equals(DELETE) && result.selectedTableRows != null && result.selectedTableRows.length > 0){
								TestCriteriaNew[] allNewDiffTeestCritArr = newDiffTestCriteria.values().toArray(new TestCriteriaNew[0]);
								TestCriteriaNew[] selTestCritsArr = new TestCriteriaNew[result.selectedTableRows.length];
								for (int i = 0; i < result.selectedTableRows.length; i++) {
									selTestCritsArr[i] = allNewDiffTeestCritArr[result.selectedTableRows[i]];
//									System.out.println("Selected= "+rowData[result.selectedTableRows[i]][0]+"  --  SimName="+selTestCritsArrHolder[0][i].getSimInfo().getVersion().getName());
								}
								hashTable.put(TFRemoveTestCriteria.REMOVE_THESE_TESTCRITERIAS, selTestCritsArr);
							}else{
								throw UserCancelException.CANCEL_GENERIC;
							}
						}else{
							throw new Exception("No differential TestCriteria found");
						}
					}
				};
				AsynchClientTask shouldRefreshTask = new AsynchClientTask("",AsynchClientTask.TASKTYPE_SWING_NONBLOCKING) {//Prevent annoying refresh if cancel
					public void run(Hashtable<String, Object> hashTable) throws Exception {
						new TFRefresh(getTestingFrameworkWindowManager(),testSuiteInfoHolder[NEWER]).run(hashTable);
					}
				};
				tasksV.add(showDiffTask);
				tasksV.add(new TFRemoveTestCriteria(getTestingFrameworkWindowManager()));
				tasksV.add(shouldRefreshTask);
//				tasksV.add(new TFRefresh(getTestingFrameworkWindowManager(),testSuiteInfoHolder[NEWER]));
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.REMOVE_TESTCRITERIA)) {
			if (selectedObj instanceof TestCriteriaNew) {
				ArrayList<TestCriteriaNew> selTestCritsArr = new ArrayList<TestCriteriaNew>();
				for(int i=0;selectedTreePaths != null &&  i<selectedTreePaths.length;i+= 1){
					Object selTreeNode = ((BioModelNode)selectedTreePaths[i].getLastPathComponent()).getUserObject();
					if (selTreeNode instanceof TestCriteriaNew) {
						selTestCritsArr.add(((TestCriteriaNew) selTreeNode));
					}
				}
				final String DELETE = "Delete";
				String response = DialogUtils.showWarningDialog(gettestingFrameworkPanel(), "Delete "+selTestCritsArr.size()+" TestCriterias?",new String[] {DELETE,"Cancel"},DELETE);
				if(response != null && response.equals(DELETE)){
					tasksV.add(new TFRemoveTestCriteria(getTestingFrameworkWindowManager(), selTestCritsArr.toArray(new TestCriteriaNew[0])));
					TestSuiteInfoNew tsInfo = gettestingFrameworkPanel().getTestSuiteInfoOfSelectedTestCriteria();
					tasksV.add(new TFRefresh(getTestingFrameworkWindowManager(),tsInfo));					
				}else{
					throw UserCancelException.CANCEL_GENERIC;
				}
			} else {
				throw new Exception("Selected Object is not a TestCriteria! Cannot remove test criteria.");
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.EDIT_TESTCRITERIA)) {
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
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.COPY_TCRIT_SIMID)) {
			if (selectedObj instanceof TestCriteriaNew) {
				TestCriteriaNew tCriteria = (TestCriteriaNew)selectedObj;
				KeyValue testCritSimID = tCriteria.getSimInfo().getVersion().getVersionKey();
				VCellTransferable.sendToClipboard(testCritSimID.toString());
				return;
			} else {
				throw new Exception("Selected Object is not a TestCriteria! Copy simid failed.");
			}
		}else if (e.getActionCommand().equals(TestingFrameworkPanel.LOAD_MODEL)) {
			final String LOAD_THIS_MODEL	 = "LOAD_THIS_MODEL";
			AsynchClientTask modelInfoTask = new AsynchClientTask("Finding Model Info...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					VCDocumentInfo vcDocumentInfo = null;
					if (selectedObj instanceof TestCaseNew) {
						TestCaseNew testCase = (TestCaseNew)selectedObj;
						if (testCase instanceof TestCaseNewMathModel) {
							vcDocumentInfo = ((TestCaseNewMathModel)testCase).getMathModelInfo();
						} else if (testCase instanceof TestCaseNewBioModel) {
							vcDocumentInfo = ((TestCaseNewBioModel)testCase).getBioModelInfo();
						}else{
							throw new IllegalArgumentException("Unexpected TestCase type="+testCase.getClass().getName());
						}
						hashTable.put(LOAD_THIS_MODEL, vcDocumentInfo);
					}else if(selectedObj instanceof LoadTestTreeInfo){
						throw new Exception("Not yet implemented for LoadTest.");
//						LoadTestTreeInfo loadTestTreeInfo = (LoadTestTreeInfo)selectedObj;
//						if(loadTestTreeInfo.modelType.equals(LoadTestInfoOpResults.MODELTYPE_BIO)){
//							vcDocumentInfo = getDocumentManager().getBioModelInfo(loadTestTreeInfo.bioOrMathModelKey);
//						}else if(loadTestTreeInfo.modelType.equals(LoadTestInfoOpResults.MODELTYPE_MATH)){
//							vcDocumentInfo = getDocumentManager().getMathModelInfo(loadTestTreeInfo.bioOrMathModelKey);
//						}else{
//							throw new IllegalArgumentException("Unexpected LoadTestTreeInfo type="+loadTestTreeInfo.modelType);
//						}
//						hashTable.put(LOAD_THIS_MODEL, vcDocumentInfo);
					}
				}
			};
			AsynchClientTask openModelTask = new AsynchClientTask("Opening model...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					if (selectedObj instanceof TestCaseNew) {
						getTestingFrameworkWindowManager().loadModel((VCDocumentInfo)hashTable.get(LOAD_THIS_MODEL));
					}else if(selectedObj instanceof LoadTestTreeInfo){
						throw new Exception("Not yet implemented for LoadTest.");
					}else {
						throw new Exception(
							"Load Model expecting TestCaseNew or LoadTestTreeInfo but got "+selectedObj.getClass().getName());
					}
				}
			};
			
			tasksV.add(modelInfoTask);
			tasksV.add(openModelTask);
		} 

		//tasksV.add(tfRefreshTreeTask);
		AsynchClientTask[] tasksArr = new AsynchClientTask[tasksV.size()];
		tasksV.copyInto(tasksArr);
		ClientTaskDispatcher.dispatch(this, hash, tasksArr, true);
		
	}catch(Throwable exc){
		if(!(exc instanceof UserCancelException) && !(exc instanceof UtilCancelException)){
			exc.printStackTrace(System.out);
			PopupGenerator.showErrorDialog(TestingFrameworkWindowPanel.this, exc.getMessage(), exc);
		}
	}


}

}
