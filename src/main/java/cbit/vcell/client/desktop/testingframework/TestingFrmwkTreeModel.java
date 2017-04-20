/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.testingframework;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.vcell.util.DataAccessException;
import org.vcell.util.NumberUtils;
import org.vcell.util.document.KeyValue;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.CommonTask;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.numericstest.LoadTestInfoOpResults;
import cbit.vcell.numericstest.LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestCriteriaNewBioModel;
import cbit.vcell.numericstest.TestCriteriaNewMathModel;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.test.VariableComparisonSummary;
/**
 * Insert the type's description here.
 * Creation date: (7/23/2004 10:10:20 AM)
 * @author: Anuradha Lakshminarayana
 */
public class TestingFrmwkTreeModel
				extends javax.swing.tree.DefaultTreeModel
				implements java.beans.PropertyChangeListener {


	public static final String TS_NODE_REFRESH = "TS_NODE_REFRESH";
	
	protected transient java.beans.PropertyChangeSupport propertyChange;
	public static final String FAILED_VARIABLE_MAE_MRE = "FV_MAE_MRE";
	public static final String SIMULATIONS_NO_REPORT = "SIM_NR";
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	//
	public static class TestCriteriaVarUserObj{
		private VariableComparisonSummary varCompSummary;
		private TestCriteriaNew tCrit;
		private TestCriteriaVarUserObj(TestCriteriaNew argTCrit,VariableComparisonSummary argvarCompSummary){
			varCompSummary = argvarCompSummary;
			tCrit = argTCrit;
		}
		public TestCriteriaNew getTestCriteria(){
			return tCrit;
		}
		public VariableComparisonSummary getVariableComparisonSummary(){
			return varCompSummary;
		}
		public String toString(){
			return "Var = " + varCompSummary.getName() + " MAE = " + varCompSummary.getAbsoluteError() + " MRE = " + varCompSummary.getRelativeError();
		}
	}
	//
	class TestSuiteGroup{
		public TestSuiteInfoNew[] latestTestSuiteInfos = null;
//		public TestSuiteNew[] latestTestSuites = null;
		public TestSuiteNew latestTestSuite = null;
		public boolean hadUpdateTSError = false;
		public TestSuiteGroup(){
		}
	};

	private JTree treeOwner = null;
/**
 * TestingFrmwkTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TestingFrmwkTreeModel(JTree owner) {
	super(new BioModelNode("empty",false),true);
	treeOwner = owner;
	addPropertyChangeListener(this);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

public static final String LOAD_TEST_SUBTREE_NAME = "LoadTests";
//public static final String LOAD_TEST_INFO_NAME = "Load Tests Info";
public static final String LOAD_TEST_FAIL_NAME = "Failed To Load";
public static final String LOAD_TEST_SLOW_NAME = "Slow To Load";
public static final String LOAD_TEST_USERQUERY_NAME = "User Query";

private BioModelNode createLoadTestTree(){
	BioModelNode rootRootNode = new BioModelNode(LOAD_TEST_SUBTREE_NAME,true);
//	BioModelNode loadInfo = new BioModelNode(LOAD_TEST_INFO_NAME,true);
//	rootRootNode.add(loadInfo);
//	BioModelNode loadFail = new BioModelNode(LOAD_TEST_FAIL_NAME,true);
//	rootRootNode.add(loadFail);
	return rootRootNode;
}

public static final String TEST_SUITE_SUBTREE_NAME = "TestSuites";
private synchronized BioModelNode createTestSuiteTree(TestSuiteGroup tsg) throws DataAccessException, java.io.IOException, org.jdom.JDOMException{
	boolean isTFUser = getDocumentManager().getUser().isTestAccount();
	if (isTFUser && tsg != null && !tsg.hadUpdateTSError){
		BioModelNode rootRootNode = new BioModelNode(TEST_SUITE_SUBTREE_NAME,true);
//		if(tsg.latestTestSuites != null){
//			//
//			// get list of test suites
//			//
//			//TestSuiteInfoNew[] testSuiteInfos = getDocumentManager().getTestSuiteInfos();
//			//
//			// for each test suite, get test cases
//			//
//			for (int i=0;tsg.latestTestSuites!=null && i<tsg.latestTestSuites.length;i++){
//				try {
//					if(tsg.latestTestSuites[i] != null){
//						rootRootNode.add(createTestSuiteSubTree(tsg.latestTestSuites[i]));
//					}else{
//						rootRootNode.add(new BioModelNode("TestSuite Not Updated "+tsg.latestTestSuiteInfos[i].getTSID(),false));
//					}
//				} catch (cbit.vcell.server.DataAccessException e) {
//					// temporary fix ...
//				}
//			}
//		}else
		if(tsg.latestTestSuiteInfos != null){
			Arrays.sort(tsg.latestTestSuiteInfos,
				new Comparator<TestSuiteInfoNew>(){
					public int compare(TestSuiteInfoNew o1, TestSuiteInfoNew o2) {
						return o1.getTSDate().compareTo(o2.getTSDate());
					}
				}
			);
			for (int i = 0; i < tsg.latestTestSuiteInfos.length; i++) {
				BioModelNode tsNodeNoDetail = new BioModelNode(tsg.latestTestSuiteInfos[i],true);
				tsNodeNoDetail.add(new BioModelNode(null,false));
				rootRootNode.add(tsNodeNoDetail);
			}
		}
		return rootRootNode;
	}else{
		BioModelNode rootRootNode = new BioModelNode("No TestSuites"+(!isTFUser?" - Not TFuser":"")+(tsg.hadUpdateTSError?" ERROR":""),false);
		return rootRootNode;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createSimulationSubTree(TestCriteriaNew tcInfo, BioModelNode simInfoNode, boolean refSimNull) throws DataAccessException {
    //
    // add 'reportInfo' node (with nodes for status and comparison summaries for variables) - sim results
    //
    VariableComparisonSummary[] varCompSummaries = tcInfo.getVarComparisonSummaries();

    BioModelNode reportInfoNode = null;
    boolean hasFailedVars = false;

    if (tcInfo != null) {
        //Calculate actual MAE,MRE
        double actualMAE = Double.NEGATIVE_INFINITY;
        double actualMRE = Double.NEGATIVE_INFINITY;
        if (varCompSummaries != null) {
            for (int i = 0; i < varCompSummaries.length; i++) {
				VariableComparisonSummary var1 = varCompSummaries[i];
                actualMAE = Math.max(actualMAE, var1.getAbsoluteError().doubleValue());
                actualMRE = Math.max(actualMRE, var1.getRelativeError().doubleValue());
            }
	        reportInfoNode = new BioModelNode("Report Info: maxActualMAE=" + actualMAE + " maxActualMRE=" + actualMRE,true);
	        // reportInfoNode.add(new BioModelNode("TBI", false));
            for (int i = 0; i < varCompSummaries.length; i++) {
                // For each Variable, get the corresponding varComparisonSummary,
                // Create a node varNode for varName, minRef,maxRef.
                // Create nodes for errors and add them as children to varNode
                // To enhance readability of errors in ResultSetsubtree on panel ...
				VariableComparisonSummary var1 = varCompSummaries[i];
				TestCriteriaVarUserObj tcritVarUserObj = new TestCriteriaVarUserObj(tcInfo,var1);
//                String varInfo = "Var = " + var1.getName() + " MAE = " + var1.getAbsoluteError() + " MRE = " + var1.getRelativeError();
                BioModelNode varInfoNode = new BioModelNode(tcritVarUserObj, true);
                if(VariableComparisonSummary.isFailed(var1)){
                    varInfoNode.setRenderHint(FAILED_VARIABLE_MAE_MRE, Boolean.TRUE);
                    hasFailedVars = true;
                }

                String varInfo = "Mean Square Error = " + var1.getMeanSqError();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "minRef = " + var1.getMinRef();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "maxRef = " + var1.getMaxRef();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "timeAbsError = " + var1.getTimeAbsoluteError();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "indexAbsError = " + var1.getIndexAbsoluteError();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "timeRelError = " + var1.getTimeRelativeError();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "indexRelError = " + var1.getIndexRelativeError();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                reportInfoNode.add(varInfoNode);
            }
			if (hasFailedVars) {
				reportInfoNode.insert(new BioModelNode("FAILED", false), 0);
			} else {
				reportInfoNode.insert(new BioModelNode("PASSED", false), 0);
			}
        } else {
	        String status = "NO DATA";
	        if (refSimNull) {
		        status = status+" : REFERENCE SIMULATION NOT SPECIFIED";
	        }
	   		reportInfoNode = new BioModelNode(status, false);
	   		reportInfoNode.setRenderHint(SIMULATIONS_NO_REPORT, Boolean.TRUE);
        } 
    }

    if (hasFailedVars) {
        simInfoNode.setRenderHint(FAILED_VARIABLE_MAE_MRE, Boolean.TRUE);
    } else {
        simInfoNode.setRenderHint(FAILED_VARIABLE_MAE_MRE, Boolean.FALSE);
    }

    return reportInfoNode;

}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createTestCaseSubTree(TestCaseNew testCase) throws DataAccessException {
	//
	// add 'test case' node
	//
	BioModelNode testCaseNode = new BioModelNode(testCase,true);
		
	TestCriteriaNew[] tCriteria = testCase.getTestCriterias();
	//Sort
	if(tCriteria != null && tCriteria.length > 0){
		java.util.Arrays.sort(tCriteria,
			new java.util.Comparator<TestCriteriaNew> (){
					public int compare(TestCriteriaNew tc1,TestCriteriaNew tc2){
						return tc1.getSimInfo().getVersion().getName().compareTo(tc2.getSimInfo().getVersion().getName());
					}
					public boolean equals(Object obj){
						return false;
					}
				}
		);
		//
		boolean bRefSimNull = false;  // False => sim has not been run; True => for a regression test, the ref SimInfo is not set.
		for(int t = 0; t < tCriteria.length ; t += 1){
			TestCriteriaNew tcInfo = tCriteria[t];
			BioModelNode simInfoNode = new BioModelNode(tcInfo, true);
			String tcInfoStr = null;
			if (tcInfo != null) {
				tcInfoStr = "TestCriteria : LimitAbsError = "+tcInfo.getMaxAbsError()+"; LimitRelError = "+tcInfo.getMaxRelError()+"; ";
				if (testCase.getType().equals(TestCaseNew.REGRESSION)) {
					if (tcInfo.getRegressionSimInfo() != null) {
						SimulationInfo refSimInfo = tcInfo.getRegressionSimInfo();
						if (refSimInfo != null) {
							if(tcInfo instanceof TestCriteriaNewMathModel){
								tcInfoStr = tcInfoStr+"MathModel : " + ((TestCriteriaNewMathModel)tcInfo).getRegressionMathModelInfo().getVersion().getName() + "; " ;
								tcInfoStr = tcInfoStr+"SimInfo : "+refSimInfo.getName();
							}else if(tcInfo instanceof TestCriteriaNewBioModel){
								tcInfoStr = tcInfoStr+"BioModel : " + ((TestCriteriaNewBioModel)tcInfo).getRegressionBioModelInfo().getVersion().getName() + "; " ;
								tcInfoStr = tcInfoStr+"SimInfo : "+refSimInfo.getName();
							}
						}
					} else {
						tcInfoStr = tcInfoStr+" REFERENCE SIMULATION NOT SET YET";
						bRefSimNull = true;
					}
				}
				BioModelNode testCriteriaNode = new BioModelNode(tcInfoStr, false);
				simInfoNode.add(testCriteriaNode);
			}

			// Add sim results if present (by adding the reportInfo subtree ...)
			BioModelNode reportInfoNode = createSimulationSubTree(tcInfo,simInfoNode, bRefSimNull);
			if (reportInfoNode != null) {
				simInfoNode.add(reportInfoNode);
			}
			testCaseNode.add(simInfoNode);
		}
	}
	return testCaseNode;
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private BioModelNode createTestSuiteSubTree(TestSuiteNew testSuite) throws DataAccessException, java.io.IOException, org.jdom.JDOMException{

	TestCaseNew[] testCases = testSuite.getTestCases();

	BioModelNode rootNode = new BioModelNode(testSuite.getTSInfoNew(),true);
	if (testCases != null && testCases.length > 0) {
		for (int i=0;i<testCases.length;i++){
			rootNode.add(createTestCaseSubTree(testCases[i]));
		}
	}
	return rootNode;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
private DocumentManager getDocumentManager() {
	return fieldDocumentManager;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Insert the method's description here.
 * Creation date: (4/28/2003 2:37:05 PM)
 * @param evt java.beans.PropertyChangeEvent
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	try {
		if (evt.getSource() == this && evt.getPropertyName().equals(CommonTask.DOCUMENT_MANAGER.name)){
			refreshTree((TestSuiteInfoNew)null);
		}
	}catch (Throwable e){
		e.printStackTrace();
	}
}

public static class LoadTestTreeInfo{
	public String userid;
	public KeyValue bioOrMathModelKey;
	public String modelType;
	public String treeDisplayText;
	public LoadTestSoftwareVersionTimeStamp loadTestSoftwareVersionTimeStamp;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return treeDisplayText;
	}
	
}
public void refreshTree(LoadTestInfoOpResults loadTestInfoOpResults) {
	//Create Load Test Info Tree
//	BioModelNode loadTestNode = new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_SUBTREE_NAME, true);
	BioModelNode[] infoNodeArr = new BioModelNode[loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps().length];
	for (int i = 0; i < loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps().length; i++) {
		LoadTestTreeInfo loadTestTreeInfo = new LoadTestTreeInfo();
		loadTestTreeInfo.loadTestSoftwareVersionTimeStamp = loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i];
		loadTestTreeInfo.userid = null;
		loadTestTreeInfo.bioOrMathModelKey = null;
		String progress = "";
		if(loadTestInfoOpResults.getLoadTestInfoEmptyCounts()[i] != null){
			double progPercent = 
				(double)(loadTestInfoOpResults.getLoadTestInfoCounts()[i]-loadTestInfoOpResults.getLoadTestInfoEmptyCounts()[i])/
					(double)loadTestInfoOpResults.getLoadTestInfoCounts()[i];
			progress = " [working "+NumberUtils.formatNumber(progPercent*100, 3)+"%]";
		}
		loadTestTreeInfo.treeDisplayText = 
			loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i].getSoftwareVersion()+
			" "+loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i].getRunTimeStamp()+
			" testSize="+loadTestInfoOpResults.getLoadTestInfoCounts()[i]+" (all BM/MM ="+
			loadTestInfoOpResults.getTotalBioAndMathmodelCount()+")"+
			progress;

		BioModelNode detailsNode =
			new BioModelNode(loadTestTreeInfo,true);
		//
		//Create Fail subtree
		//
		Vector<LoadTestInfoOpResults.LoadTestFailDetails> failDetailsV = loadTestInfoOpResults.getLoadTestFailHash().get(loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i]);
		BioModelNode loadTestFailNode = new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_FAIL_NAME+
				(failDetailsV==null?" (0)":" ("+failDetailsV.size()+")"), failDetailsV != null);
		if(failDetailsV != null){
			LoadTestInfoOpResults.LoadTestFailDetails[] sortedDetails =
				failDetailsV.toArray(new LoadTestInfoOpResults.LoadTestFailDetails[0]);
			Arrays.sort(sortedDetails,new Comparator<LoadTestInfoOpResults.LoadTestFailDetails>() {
				public int compare(LoadTestInfoOpResults.LoadTestFailDetails o1, LoadTestInfoOpResults.LoadTestFailDetails o2) {
					return o1.errorMessage.compareToIgnoreCase(o2.errorMessage);
				}
			});
			for (int j = 0; j < sortedDetails.length; j++) {
				LoadTestTreeInfo loadTestFailTreeInfo = new LoadTestTreeInfo();
				loadTestFailTreeInfo.loadTestSoftwareVersionTimeStamp = loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i];
				loadTestFailTreeInfo.userid = sortedDetails[j].userid;
				loadTestFailTreeInfo.bioOrMathModelKey = sortedDetails[j].modelKeyValue;
				loadTestFailTreeInfo.modelType = sortedDetails[j].modelType;
				loadTestFailTreeInfo.treeDisplayText =
					sortedDetails[j].userid+" ["+sortedDetails[j].modelType+"] '"+
					sortedDetails[j].modelName+"'     "+sortedDetails[j].errorMessage;
	
				BioModelNode detailNode = new BioModelNode(loadTestFailTreeInfo, false);
				loadTestFailNode.add(detailNode);
			}
		}
		//Add fail nodes to detailsNode
		detailsNode.add(loadTestFailNode);

		//
		//Create Slow subtree (may not have node if user not specify threshold)
		//
		if(loadTestInfoOpResults.getLoadTestSlowHash() != null){
			Vector<LoadTestInfoOpResults.LoadTestSlowDetails> slowDetailsV = loadTestInfoOpResults.getLoadTestSlowHash().get(loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i]);
			if(slowDetailsV != null){
				BioModelNode loadTestSlowNode =
					new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_SLOW_NAME+" ("+slowDetailsV.size()+" loaded slower than "+loadTestInfoOpResults.getSlowLoadThresholdMillisec()+" miilsecs)", true);
				LoadTestInfoOpResults.LoadTestSlowDetails[] sortedDetails =
					slowDetailsV.toArray(new LoadTestInfoOpResults.LoadTestSlowDetails[0]);
				Arrays.sort(sortedDetails,new Comparator<LoadTestInfoOpResults.LoadTestSlowDetails>() {
					public int compare(LoadTestInfoOpResults.LoadTestSlowDetails o1, LoadTestInfoOpResults.LoadTestSlowDetails o2) {
						return o1.loadTime.compareTo(o2.loadTime);
					}
				});
				for (int j = 0; j < sortedDetails.length; j++) {
					LoadTestTreeInfo loadTestSlowTreeInfo = new LoadTestTreeInfo();
					loadTestSlowTreeInfo.loadTestSoftwareVersionTimeStamp = loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i];
					loadTestSlowTreeInfo.userid = sortedDetails[j].userid;
					loadTestSlowTreeInfo.bioOrMathModelKey = sortedDetails[j].modelKeyValue;
					loadTestSlowTreeInfo.modelType = sortedDetails[j].modelType;
					loadTestSlowTreeInfo.treeDisplayText =
						sortedDetails[j].userid+" ["+sortedDetails[j].modelType+"] '"+
						sortedDetails[j].modelName+"'     millisec="+sortedDetails[j].loadTime;
		
					BioModelNode detailNode = new BioModelNode(loadTestSlowTreeInfo, false);
					loadTestSlowNode.add(detailNode);
					//Add slow load nodes to detailsNode
					detailsNode.add(loadTestSlowNode);
				}
			}else{
				BioModelNode loadTestSlowNode =
					new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_SLOW_NAME+" (All loaded in < "+loadTestInfoOpResults.getSlowLoadThresholdMillisec()+" miilisecs)", false);
				detailsNode.add(loadTestSlowNode);
			}
		}else{
			BioModelNode loadTestSlowNode =
				new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_SLOW_NAME+" (No Threshold)", false);
			detailsNode.add(loadTestSlowNode);
		}
		
		//
		//Create User Query subtree (may not have node if user not specify query)
		//
		if(loadTestInfoOpResults.getLoadTestUserQueryHash() != null){
			Vector<LoadTestInfoOpResults.LoadTestDetails> userQueryDetailsV = loadTestInfoOpResults.getLoadTestUserQueryHash().get(loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i]);
			if(userQueryDetailsV != null){
				BioModelNode loadTestUserQueryNode =
					new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_USERQUERY_NAME+" ("+userQueryDetailsV.size()+" records found)", true);
				LoadTestInfoOpResults.LoadTestDetails[] sortedDetails =
					userQueryDetailsV.toArray(new LoadTestInfoOpResults.LoadTestDetails[0]);
				Arrays.sort(sortedDetails,new Comparator<LoadTestInfoOpResults.LoadTestDetails>() {
					public int compare(LoadTestInfoOpResults.LoadTestDetails o1, LoadTestInfoOpResults.LoadTestDetails o2) {
						return o1.userid.compareToIgnoreCase(o2.userid);
					}
				});
				for (int j = 0; j < sortedDetails.length; j++) {
					LoadTestTreeInfo loadTestUserQueryTreeInfo = new LoadTestTreeInfo();
					loadTestUserQueryTreeInfo.loadTestSoftwareVersionTimeStamp = loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i];
					loadTestUserQueryTreeInfo.userid = sortedDetails[j].userid;
					loadTestUserQueryTreeInfo.bioOrMathModelKey = sortedDetails[j].modelKeyValue;
					loadTestUserQueryTreeInfo.modelType = sortedDetails[j].modelType;
					loadTestUserQueryTreeInfo.treeDisplayText =
						sortedDetails[j].userid+" ["+sortedDetails[j].modelType+"] '"+
						sortedDetails[j].modelName+"' id="+sortedDetails[j].modelKeyValue.toString();
		
					BioModelNode detailNode = new BioModelNode(loadTestUserQueryTreeInfo, false);
					loadTestUserQueryNode.add(detailNode);
					//Add user query nodes to detailsNode
					detailsNode.add(loadTestUserQueryNode);
				}
			}else{
				BioModelNode loadTestSlowNode =
					new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_USERQUERY_NAME+" (No Results For Query)", false);
				detailsNode.add(loadTestSlowNode);
			}
		}else{
			BioModelNode loadTestUserQueryNode =
				new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_USERQUERY_NAME+" (No User Query)", false);
			detailsNode.add(loadTestUserQueryNode);
		}

		//Save detail nodes for adding to versionTimestamp node
		infoNodeArr[i] = detailsNode;

	}
	//Add versionTimestamp nodes to LoadTest tree
	BioModelNode loadXMLTestRoot = (BioModelNode)getLoadTestRoot(TestingFrmwkTreeModel.this);
	while(loadXMLTestRoot.getChildCount() != 0){
		removeNodeFromParent((MutableTreeNode)loadXMLTestRoot.getLastChild());
	}
	for (int j = 0; j < infoNodeArr.length; j++) {
		insertNodeInto(infoNodeArr[j], loadXMLTestRoot, j);
	}
}

//public void refreshTree(LoadTestInfoOpResults loadTestInfoOpResults) {
//	BioModelNode rootNode = getLoadTestRoot();
//	//Create Load Test Info Tree
//	BioModelNode loadTestInfoNode = new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_INFO_NAME, true);
//	for (int i = 0; i < loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps().length; i++) {
//		LoadTestTreeInfo loadTestTreeInfo = new LoadTestTreeInfo();
//		loadTestTreeInfo.loadTestSoftwareVersionTimeStamp = loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i];
//		loadTestTreeInfo.userid = null;
//		loadTestTreeInfo.bioOrMathModelKey = null;
//		String progress = "";
//		if(loadTestInfoOpResults.getLoadTestInfoEmptyCounts()[i] != null){
//			double progPercent = 
//				(double)(loadTestInfoOpResults.getLoadTestInfoCounts()[i]-loadTestInfoOpResults.getLoadTestInfoEmptyCounts()[i])/
//					(double)loadTestInfoOpResults.getLoadTestInfoCounts()[i];
//			progress = " [working "+NumberUtils.formatNumber(progPercent*100, 3)+"%]";
//		}
//		loadTestTreeInfo.treeDisplayText = 
//			loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i].getSoftwareVersion()+
//			" "+loadTestInfoOpResults.getLoadTestSoftwareVersionTimeStamps()[i].getRunTimeStamp()+
//			" "+loadTestInfoOpResults.getLoadTestInfoCounts()[i]+" ("+
//			loadTestInfoOpResults.getTotalBioAndMathmodelCount()+")"+
//			progress;
//
//		BioModelNode infoNode =
//			new BioModelNode(loadTestTreeInfo,false);
//		loadTestInfoNode.add(infoNode);
//	}
//	for (int i = 0; i < rootNode.getChildCount(); i++) {
//		BioModelNode childNode = (BioModelNode)rootNode.getChildAt(i);
//		if(childNode.getUserObject().equals(TestingFrmwkTreeModel.LOAD_TEST_INFO_NAME)){
//			removeNodeFromParent(childNode);
//			insertNodeInto(loadTestInfoNode, rootNode, i);
//		}
//	}
//
//	//Create Load Test failed tree
//	BioModelNode loadTestFailNode = new BioModelNode(TestingFrmwkTreeModel.LOAD_TEST_FAIL_NAME, true);
//	Enumeration<LoadTestSoftwareVersionTimeStamp> failEnum = loadTestInfoOpResults.getLoadTestFailedHash().keys();
//	while(failEnum.hasMoreElements()){
//		LoadTestSoftwareVersionTimeStamp loadTestSoftwareVersionTimeStamp =
//			failEnum.nextElement();
//		Vector<LoadTestFailedDetails> faildetailsV = loadTestInfoOpResults.getLoadTestFailedHash().get(loadTestSoftwareVersionTimeStamp);
//		BioModelNode loadTestSoftwareVersionTimeStampNode = 
//			new BioModelNode("("+faildetailsV.size()+") "+loadTestSoftwareVersionTimeStamp.getSoftwareVersion()+" "+
//					loadTestSoftwareVersionTimeStamp.getRunTimeStamp(),true);
//		loadTestFailNode.add(loadTestSoftwareVersionTimeStampNode);
//		LoadTestFailedDetails[] sortedDetails = faildetailsV.toArray(new LoadTestFailedDetails[0]);
//		Arrays.sort(sortedDetails,new Comparator<LoadTestFailedDetails>() {
//			public int compare(LoadTestFailedDetails o1, LoadTestFailedDetails o2) {
//				// TODO Auto-generated method stub
//				return o1.errorMessage.compareToIgnoreCase(o2.errorMessage);
//			}
//		});
//		for (int i = 0; i < sortedDetails.length; i++) {
//			LoadTestTreeInfo loadTestTreeInfo = new LoadTestTreeInfo();
//			loadTestTreeInfo.loadTestSoftwareVersionTimeStamp = loadTestSoftwareVersionTimeStamp;
//			loadTestTreeInfo.userid = sortedDetails[i].userid;
//			loadTestTreeInfo.bioOrMathModelKey = sortedDetails[i].modelKeyValue;
//			loadTestTreeInfo.treeDisplayText =
//				sortedDetails[i].userid+" ["+sortedDetails[i].modelType+"] '"+
//				sortedDetails[i].modelName+"'     "+sortedDetails[i].errorMessage;
//
//			BioModelNode detailNode = new BioModelNode(loadTestTreeInfo, false);
//			loadTestSoftwareVersionTimeStampNode.add(detailNode);
//		}
//	
//	
//	}
//	for (int i = 0; i < rootNode.getChildCount(); i++) {
//		BioModelNode childNode = (BioModelNode)rootNode.getChildAt(i);
//		if(childNode.getUserObject().equals(TestingFrmwkTreeModel.LOAD_TEST_FAIL_NAME)){
//			removeNodeFromParent(childNode);
//			insertNodeInto(loadTestFailNode, rootNode, i);
////			firePropertyChange(TS_NODE_REFRESH, childNode, finalNode);
//		}
//	}
//
//}
/**
 * Insert the method's description here.
 * Creation date: (2/14/01 3:50:24 PM)
 */
public void refreshTree(final TestSuiteInfoNew tsin) {
	if (getDocumentManager() != null && getDocumentManager().getUser() != null){
		if(getDocumentManager().getUser().isTestAccount()){
			AsynchClientTask GetTestSuites = new AsynchClientTask("Refresh Testing FrameWork Display", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
				
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					TestSuiteGroup tsg = new TestSuiteGroup();
					boolean bRemove = false;
					try{
						TestSuiteInfoNew[] latestTestSuiteInfos = null;
						TestSuiteNew latestTestSuite = null;
						boolean hadUpdateTSError = false;
						if(getDocumentManager() != null){
							TestSuiteInfoNew[] testSuiteInfos = null;
							getClientTaskStatusSupport().setMessage("Getting TestsuiteInfos");
							try{								
								testSuiteInfos = getDocumentManager().getTestSuiteInfos();
							}catch(Throwable e){
								e.printStackTrace();
								hadUpdateTSError = true;
							}
							if(testSuiteInfos != null){
								latestTestSuiteInfos = testSuiteInfos;
								if(tsin != null){
									try {
										getClientTaskStatusSupport().setProgress(50);
										if(tsin.getTSKey() != null){//from Refesh or remove TestSuite
											bRemove = true;
											for (int i=0;i<testSuiteInfos.length;i++){
												if(testSuiteInfos[i].getTSKey().equals(tsin.getTSKey())){
													bRemove = false;
													break;
												}
											}
											if(!bRemove){
												getClientTaskStatusSupport().setMessage("Getting Testsuite "+tsin.getTSID());
												latestTestSuite = getDocumentManager().getTestSuite(tsin.getTSKey());
											}else{
												getClientTaskStatusSupport().setMessage("Remove Testsuite "+tsin.getTSID());
												latestTestSuiteInfos = new TestSuiteInfoNew[] {tsin};
											}
										}else{//from Duplicate TestSuite
											for (int i=0;i<testSuiteInfos.length;i++){
												if(testSuiteInfos[i].getTSID().equals(tsin.getTSID())){
													latestTestSuite = getDocumentManager().getTestSuite(testSuiteInfos[i].getTSKey());
													break;
												}
											}
										}
									} catch (DataAccessException e) {
										tsg = new TestSuiteGroup();
										throw e;
									}
								}
							}
						}
						
						tsg.latestTestSuiteInfos = latestTestSuiteInfos;
						tsg.latestTestSuite = latestTestSuite;
						tsg.hadUpdateTSError = hadUpdateTSError;
					}finally{
						if(tsg.latestTestSuiteInfos != null){
							getClientTaskStatusSupport().setProgress(tsg.latestTestSuiteInfos.length*100/(tsg.latestTestSuiteInfos.length+1));
						}						
						hashTable.put("tsg", tsg);
						hashTable.put("bRemove", bRemove);
					}
				}		
			};
			AsynchClientTask updateTreeTask = new AsynchClientTask("Refresh Testing FrameWork Display", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
				
				@Override
				public void run(Hashtable<String, Object> hashTable) throws Exception {
					TestSuiteGroup tsg = (TestSuiteGroup)hashTable.get("tsg");
					boolean bRemove = (Boolean)hashTable.get("bRemove");
					updateTree(tsg,bRemove);
				}
			};
			ClientTaskDispatcher.dispatch(treeOwner.getParent(), new Hashtable<String, Object>(), new AsynchClientTask[] { GetTestSuites, updateTreeTask }, false);
		}else{
			setRoot(new BioModelNode(getDocumentManager().getUser().getName()+" Not TestAccount User"));
		}
	}else{
		setRoot(new BioModelNode("empty"));
	}
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
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
	 * Invoked when an action occurs.
	 */
private void updateTree(final TestSuiteGroup tsg,boolean bRemove) throws Exception {
	
	if(tsg != null){
		if(tsg.latestTestSuite == null && tsg.latestTestSuiteInfos != null && !bRemove){
			BioModelNode finalNode = new BioModelNode("VCell Testing Framework");
			BioModelNode testSuiteNode = null;
			try {
				testSuiteNode = createTestSuiteTree(tsg);
			}finally{
				if(testSuiteNode == null){
					testSuiteNode = new BioModelNode("Error Creating TestSuite Tree",false);
				}
			}
			
			BioModelNode loadTestNode = null;
			try {
				loadTestNode = createLoadTestTree();
			}finally{
				if(loadTestNode == null){
					loadTestNode = new BioModelNode("Error Creating LoadTest Tree",false);
				}
			}

			finalNode.add(loadTestNode);
			finalNode.add(testSuiteNode);
			setRoot(finalNode);

		}else if(tsg.latestTestSuite != null && !bRemove){
			BioModelNode finalNode = null;
			BioModelNode rootNode = getTestSuiteRoot(TestingFrmwkTreeModel.this);
				

			finalNode = createTestSuiteSubTree(tsg.latestTestSuite);
			for (int i = 0; i < rootNode.getChildCount(); i++) {
				BioModelNode childNode = (BioModelNode)rootNode.getChildAt(i);
				TestSuiteInfoNew childtsin = (TestSuiteInfoNew)childNode.getUserObject();
				if(childtsin.getTSKey().equals(tsg.latestTestSuite.getTSInfoNew().getTSKey())){
					removeNodeFromParent(childNode);
					insertNodeInto(finalNode, rootNode, i);
					firePropertyChange(TS_NODE_REFRESH, childNode, finalNode);
					return;
				}
			}
			//Must be NEW TeestSuite
			//insert at top
			
			insertNodeInto(finalNode, rootNode, rootNode.getChildCount());
		}else if(tsg.latestTestSuiteInfos != null && bRemove){//Remove tree nodes that aren't in DB
			BioModelNode rootNode = getTestSuiteRoot(TestingFrmwkTreeModel.this);
			for (int j = 0; j < tsg.latestTestSuiteInfos.length; j++) {
				for (int i = 0; i < rootNode.getChildCount(); i++) {
					BioModelNode childNode = (BioModelNode)rootNode.getChildAt(i);
					TestSuiteInfoNew childtsin = (TestSuiteInfoNew)childNode.getUserObject();
					if(childtsin.getTSKey().equals(tsg.latestTestSuiteInfos[j].getTSKey())){
						removeNodeFromParent(childNode);
						break;
					}
				}
			}
		}
	}
}
public static BioModelNode getTestSuiteRoot(DefaultTreeModel defaultTreeModel){
	return ((BioModelNode)defaultTreeModel.getRoot()).findMatchingNode(new BioModelNode.NodeMatcher() {
		public boolean match(Object obj) {
			return obj.equals(TestingFrmwkTreeModel.TEST_SUITE_SUBTREE_NAME);
		}
	});
}
private static BioModelNode getLoadTestRoot(DefaultTreeModel defaultTreeModel){
	return ((BioModelNode)defaultTreeModel.getRoot()).findMatchingNode(new BioModelNode.NodeMatcher() {
		public boolean match(Object obj) {
			return obj.equals(TestingFrmwkTreeModel.LOAD_TEST_SUBTREE_NAME);
		}
	});
}
}
