package cbit.vcell.client.desktop.testingframework;

import cbit.vcell.numericstest.*;
import cbit.vcell.solver.test.VariableComparisonSummary;
import java.util.Vector;
import java.util.Enumeration;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.clientdb.DatabaseListener;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.server.DataAccessException;
import org.jdom.JDOMException;
import java.io.IOException;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (7/23/2004 10:10:20 AM)
 * @author: Anuradha Lakshminarayana
 */
public class TestingFrmwkTreeModel
				extends javax.swing.tree.DefaultTreeModel
				implements java.beans.PropertyChangeListener {


		
	protected transient java.beans.PropertyChangeSupport propertyChange;
	public static final String FAILED_VARIABLE_MAE_MRE = "FV_MAE_MRE";
	public static final String SIMULATIONS_NO_REPORT = "SIM_NR";
	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	//
	private TestSuiteGroup testSuiteGroup;
	//
	class TestSuiteGroup{
		public TestSuiteInfoNew[] latestTestSuiteInfos = null;
		public TestSuiteNew[] latestTestSuites = null;
		public boolean hadUpdateTSError = false;
		public TestSuiteGroup(){
		}
	};
	//
	class GetTestSuites implements Runnable{
		cbit.util.AsynchProgressPopup pp = 
			new cbit.util.AsynchProgressPopup(null,"Refresh Testing FrameWork Display","starting...",false,true);
		public GetTestSuites(){
		}
		public void run(){
			pp.start();
			TestSuiteGroup tsg = new TestSuiteGroup();
			try{
				TestSuiteInfoNew[] latestTestSuiteInfos = null;
				TestSuiteNew[] latestTestSuites = null;
				boolean hadUpdateTSError = false;
				if(getDocumentManager() != null){
					TestSuiteInfoNew[] testSuiteInfos = null;
					try{
						pp.setMessage("Getting TestsuiteInfos");
						testSuiteInfos = getDocumentManager().getTestSuiteInfos();
					}catch(Throwable e){
						hadUpdateTSError = true;
					}
					if(testSuiteInfos != null){
						latestTestSuiteInfos = testSuiteInfos;
						latestTestSuites = new TestSuiteNew[testSuiteInfos.length];
						for (int i=0;i<testSuiteInfos.length;i++){
							try {
								pp.setProgress(i*100/(testSuiteInfos.length+1));
								pp.setMessage("Getting Testsuite "+testSuiteInfos[i].getTSID());
								latestTestSuites[i] = getDocumentManager().getTestSuite(testSuiteInfos[i].getTSKey());
							} catch (cbit.vcell.server.DataAccessException e) {
								latestTestSuites[i] = null;
							}
						}
					}
				}
				
				tsg.latestTestSuiteInfos = latestTestSuiteInfos;
				tsg.latestTestSuites = latestTestSuites;
				tsg.hadUpdateTSError = hadUpdateTSError;
			}finally{
				if(tsg.latestTestSuiteInfos != null){
					pp.setProgress(tsg.latestTestSuiteInfos.length*100/(tsg.latestTestSuiteInfos.length+1));
				}
				pp.setMessage("Updating Display");
				updateTree(tsg);
				pp.stop();
			}
		}
	};
/**
 * TestingFrmwkTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TestingFrmwkTreeModel() {
	super(new BioModelNode("empty",false),true);
	addPropertyChangeListener(this);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 1:06:51 PM)
 * @return cbit.vcell.desktop.BioModelNode
 * @param docManager cbit.vcell.clientdb.DocumentManager
 */
private synchronized BioModelNode createBaseTree(TestSuiteGroup tsg) throws DataAccessException, java.io.IOException, org.jdom.JDOMException{
	boolean isTFUser = getDocumentManager().getUser().isTestAccount();
	if (isTFUser && tsg != null && !tsg.hadUpdateTSError){
		BioModelNode rootRootNode = new BioModelNode("TestSuites",true);
		//
		// get list of test suites
		//
		//TestSuiteInfoNew[] testSuiteInfos = getDocumentManager().getTestSuiteInfos();
		//
		// for each test suite, get test cases
		//
		for (int i=0;tsg.latestTestSuites!=null && i<tsg.latestTestSuites.length;i++){
			try {
				if(tsg.latestTestSuites[i] != null){
					rootRootNode.add(createTestSuiteSubTree(tsg.latestTestSuites[i]));
				}else{
					rootRootNode.add(new BioModelNode("TestSuite Not Updated "+tsg.latestTestSuiteInfos[i].getTSID(),false));
				}
			} catch (cbit.vcell.server.DataAccessException e) {
				// temporary fix ...
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
                String varInfo = "Var = " + var1.getName() + " MAE = " + var1.getAbsoluteError() + " MRE = " + var1.getRelativeError();
                BioModelNode varInfoNode = new BioModelNode(varInfo, true);
                if (tcInfo != null
                    && (tcInfo.getMaxAbsError().doubleValue() < var1.getAbsoluteError().doubleValue()
                    || tcInfo.getMaxRelError().doubleValue() < var1.getRelativeError().doubleValue())) {
                    varInfoNode.setRenderHint(FAILED_VARIABLE_MAE_MRE, Boolean.TRUE);
                    hasFailedVars = true;
                }

                varInfo = "Mean Square Error = " + var1.getMeanSqError();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "minRef = " + var1.getMinRef();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "maxRef = " + var1.getMaxRef();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "timeAbsError = " + var1.getTimeAbsoluteError();
                varInfoNode.add(new cbit.vcell.desktop.BioModelNode(varInfo, false));
                varInfo = "indexAbsError = " + var1.getIndexAbsoluteError();
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
			new java.util.Comparator (){
					public int compare(Object o1,Object o2){
						TestCriteriaNew tc1 = (TestCriteriaNew)o1;
						TestCriteriaNew tc2 = (TestCriteriaNew)o2;
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
				tcInfoStr = "TestCriteria : MaxAbsError = "+tcInfo.getMaxAbsError()+"; MaxRelError = "+tcInfo.getMaxRelError()+"; ";
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
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the documentManager property (cbit.vcell.clientdb.DocumentManager) value.
 * @return The documentManager property value.
 * @see #setDocumentManager
 */
private cbit.vcell.clientdb.DocumentManager getDocumentManager() {
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
		if (evt.getSource() == this && evt.getPropertyName().equals("documentManager")){
			refreshTree();
		}
	}catch (Throwable e){
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/01 3:50:24 PM)
 */
public void refreshTree() {
	
	if (getDocumentManager() != null){
		if(getDocumentManager().getUser().isTestAccount()){
			(new Thread(new GetTestSuites())).start();
		}else{
			setRoot(new BioModelNode(getDocumentManager().getUser().getName()+" Not TestAccount User"));
		}
	}else{
		setRoot(new BioModelNode("No DocumentManager"));
	}
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
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
	 * Invoked when an action occurs.
	 */
private void updateTree(TestSuiteGroup tsg) {
	
	if(tsg != null){
		BioModelNode finalNode = null;
		Throwable error = null;
		try {
			finalNode = createBaseTree(tsg);
			final BioModelNode ffNode = finalNode;
			javax.swing.SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						setRoot(ffNode);
					}
				}
			);
		}catch (Throwable exc){
			exc.printStackTrace();
			error = exc;
		}finally{
			if(finalNode == null){
				setRoot(new BioModelNode("Error Creating TF Tree"+(error != null?error.getClass().getName():""),false));
			}
		}
	}
	
}
}
