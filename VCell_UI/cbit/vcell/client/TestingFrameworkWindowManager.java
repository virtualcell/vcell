package cbit.vcell.client;
import cbit.vcell.math.AnnotatedFunction;
import javax.swing.SwingUtilities;

import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.MergedData;
import cbit.vcell.simdata.ODESolverResultSet;
import cbit.gui.PropertyLoader;
import javax.swing.JFrame;

import cbit.vcell.server.SimulationStatus;
import cbit.util.SessionLog;
import cbit.util.StdoutSessionLog;
import cbit.util.User;
import cbit.util.UserCancelException;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.SimulationInfo;
import cbit.vcell.simulation.VCSimulationIdentifier;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import cbit.vcell.client.desktop.simulation.SimulationCompareWindow;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.DynamicDataManager;
import cbit.vcell.client.database.ClientDocumentManager;
import cbit.vcell.client.database.DocumentManager;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.biomodel.BioModel;
import cbit.sql.*;
import java.math.*;
import javax.swing.JDialog;
import java.awt.Component;
import javax.swing.JComponent;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.util.VCDataIdentifier;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
import org.jdom.JDOMException;
import cbit.util.KeyValue;
import java.util.Enumeration;
import java.util.Vector;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.client.desktop.TestingFrameworkWindowPanel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.util.BeanUtils;
import cbit.util.DataAccessException;
import cbit.vcell.client.desktop.testingframework.EditTestCriteriaPanel;
import cbit.vcell.client.desktop.testingframework.AddTestSuitePanel;
import cbit.vcell.client.desktop.testingframework.TestCaseAddPanel;
import cbit.vcell.export.ExportEvent;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solvers.VCSimulationDataIdentifier;

import javax.swing.JOptionPane;
import cbit.vcell.numericstest.*;
import cbit.util.AsynchProgressPopup;
/**
 * Insert the type's description here.
 * Creation date: (7/15/2004 11:44:12 AM)
 * @author: Anuradha Lakshminarayana
 */
public class TestingFrameworkWindowManager extends TopLevelWindowManager implements DataViewerManager {
	private TestingFrameworkWindowPanel testingFrameworkWindowPanel;
	private EditTestCriteriaPanel editTestCriteriaPanel =
		new EditTestCriteriaPanel();
	private TestCaseAddPanel testCaseAddPanel = new TestCaseAddPanel();
	private Vector dataViewerPlotsFramesVector = new Vector();
	private AddTestSuitePanel addTestSuitePanel = new AddTestSuitePanel();
	private JOptionPane addTestSuiteDialog =
		new JOptionPane(
			null,
			JOptionPane.PLAIN_MESSAGE,
			0,
			null,
			new Object[] { "OK", "Cancel" });
	private JOptionPane addTestCaseDialog =
		new JOptionPane(
			null,
			JOptionPane.PLAIN_MESSAGE,
			0,
			null,
			new Object[] { "OK", "Cancel" });
	private JOptionPane editTestCriteriaDialog =
		new JOptionPane(
			null,
			JOptionPane.PLAIN_MESSAGE,
			0,
			null,
			new Object[] { "OK", "Cancel" });

/**
 * TestingFrameworkWindowManager constructor comment.
 * @param requestManager cbit.vcell.client.RequestManager
 */
public TestingFrameworkWindowManager(TestingFrameworkWindowPanel testingFrameworkWindowPanel, RequestManager requestManager) {
	super(requestManager);
	setTestingFrameworkWindowPanel(testingFrameworkWindowPanel);
}


/**
 * Add a cbit.vcell.desktop.controls.DataListener.
 */
public void addDataListener(cbit.vcell.desktop.controls.DataListener newListener) {}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void addNewTestSuiteToTF() throws UserCancelException,DataAccessException {

	getAddTestSuitePanel().resetTextFields(true);
	Object choice = showAddTestSuiteDialog(getAddTestSuitePanel(), null);

	if (choice != null && choice.equals("OK")) {
		// set the newly defined TestSuite in the newTestSuite object.
		TestSuiteInfoNew newTestSuiteInfo = getAddTestSuitePanel().getTestSuiteInfo();
		if (newTestSuiteInfo != null) {
			// check if the newly defined testSuite already exists.
			TestSuiteInfoNew[] testSuiteInfos = null;
			
			testSuiteInfos = getRequestManager().getDocumentManager().getTestSuiteInfos();
			
			for (int i = 0; i < testSuiteInfos.length; i++) {
				if (newTestSuiteInfo.getTSID().equals(testSuiteInfos[i].getTSID())) {
					PopupGenerator.showErrorDialog("TestSuite Version "+newTestSuiteInfo.getTSID()+" already exists, Choose another version no. for new TestSuite");
					return;
				}
			}
			//addTestSuite(newTestSuiteInfo);
			AddTestSuiteOP testSuiteOP =
				new AddTestSuiteOP(
					newTestSuiteInfo.getTSID(),
					newTestSuiteInfo.getTSVCellBuild(),
					newTestSuiteInfo.getTSNumericsBuild(),
					null);
			getRequestManager().getDocumentManager().doTestSuiteOP(testSuiteOP);
		}
	} else if (choice.equals("Cancel")) {
		throw UserCancelException.CANCEL_DB_SELECTION;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */
public String addTestCases(final TestSuiteInfoNew tsInfo, final TestCaseNew[] testCases,AsynchProgressPopup pp){
		
	if (testCases == null || testCases.length == 0 || tsInfo == null) {
		throw new IllegalArgumentException("TestCases and TestSuiteInfo cannot be null");
	}

	StringBuffer errors = new StringBuffer();

	// When a testCase (mathmodel/biomodel) is added to a testSuite, a new version of the mathModel/biomodel should be created.
	// Also, the simulations in the original mathmodel/biomodel should be rid of their parent simulation reference.

	pp.setMessage("Getting testSuite");
	pp.setProgress(1);
	TestSuiteNew testSuite = null;
	try{
		testSuite = getRequestManager().getDocumentManager().getTestSuite(tsInfo.getTSKey());
	}catch(Throwable e){
		throw new RuntimeException("couldn't get test suite "+tsInfo.getTSID()+"\n"+e.getClass().getName()+" mesg="+e.getMessage()+"\n");
	}
	
	if(testSuite != null){
		//Saving BioModels
		TestCaseNew existingTestCases[] = testSuite.getTestCases();
		java.util.HashMap bioModelHashMap = new java.util.HashMap();
		//if(existingTestCases != null){
			// Find BioModels, Using the same BM reference for sibling Applications
			for (int i = 0; i < testCases.length; i++){
				pp.setProgress(Math.max(1,((int)(((double)i/(double)(testCases.length*3))*100))));
				pp.setMessage("Checking "+testCases[i].getVersion().getName());
				try{
					if (testCases[i] instanceof TestCaseNewBioModel) {
						TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCases[i];
						//
						// re-save only once for each BioModel/TestSuite combination
						//
						if (bioModelHashMap.get(bioTestCase.getBioModelInfo().getVersion().getVersionKey())==null){
							pp.setMessage("Getting BM "+testCases[i].getVersion().getName());
							BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bioTestCase.getBioModelInfo().getVersion().getVersionKey());
							if (!bioModel.getVersion().getOwner().equals(getRequestManager().getDocumentManager().getUser())) {
								throw new Exception("BioModel does not belong to VCELLTESTACCOUNT, cannot proceed with test!");
							}
							//
							// if biomodel already exists in same testsuite, then use this BioModel edition
							//
							BioModel newBioModel = null;
							if(existingTestCases != null){
								for (int j = 0; newBioModel==null && j < existingTestCases.length; j++){
									if (existingTestCases[j] instanceof TestCaseNewBioModel){
										TestCaseNewBioModel existingTestCaseBioModel = (TestCaseNewBioModel)existingTestCases[j];
										//
										// check if BioModel has same BranchID (an edition of same BioModel)
										//
										if (existingTestCaseBioModel.getBioModelInfo().getVersion().getBranchID().equals(bioTestCase.getBioModelInfo().getVersion().getBranchID())){
											//
											// check if BioModel has same Key (same edition)
											//
											if (existingTestCaseBioModel.getBioModelInfo().getVersion().getVersionKey().equals(bioTestCase.getBioModelInfo().getVersion().getVersionKey())){
												//
												// same, store this "unchanged" in bioModelHashMap
												//
												newBioModel = bioModel;
											}else{
												//
												// different edition of same BioModel ... not allowed
												//
												throw new Exception("can't add new test case using ("+bioTestCase.getBioModelInfo().getVersion().getName()+" "+bioTestCase.getBioModelInfo().getVersion().getDate()+")\n"+
													                           "a test case already exists with different edition of same BioModel dated "+existingTestCaseBioModel.getBioModelInfo().getVersion().getDate());
											}
										}
									}
								}
							}

							if (newBioModel==null){
								pp.setMessage("Saving BM "+testCases[i].getVersion().getName());
								cbit.vcell.mapping.SimulationContext[] simContexts = bioModel.getSimulationContexts();
								for (int j = 0; j < simContexts.length; j++){
									simContexts[j].clearVersion();
									cbit.vcell.geometry.surface.GeometrySurfaceDescription gsd =
										simContexts[j].getGeometry().getGeometrySurfaceDescription();
									if(gsd != null){
										cbit.vcell.geometry.surface.GeometricRegion[] grArr = gsd.getGeometricRegions();
										if(grArr == null){
											gsd.updateAll();
										}
									}
									cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContexts[j]);
									simContexts[j].setMathDescription(mathMapping.getMathDescription());
								}
								Simulation[] sims = bioModel.getSimulations();
								String[] simNames = new String[sims.length];
								for (int j = 0; j < sims.length; j++){
									// prevents parent simulation (from the original mathmodel) reference connection
									// Otherwise it will refer to data from previous (parent) simulation.
									sims[j].clearVersion();
									simNames[j] = sims[j].getName();
								}

								newBioModel = getRequestManager().getDocumentManager().save(bioModel, simNames);
							}
							
							bioModelHashMap.put(bioTestCase.getBioModelInfo().getVersion().getVersionKey(),newBioModel);
						}
					}
				}catch(Throwable e){
					errors.append("Error collecting BioModel for TestCase "+
						(testCases[i].getVersion() != null?"Name="+testCases[i].getVersion().getName():"TCKey="+testCases[i].getTCKey())+"\n"+
							e.getClass().getName()+" "+e.getMessage()+"\n");
				}
			}
		//}
		// then process each BioModelTestCase individually
		//if(bioModelHashMap != null){
		for (int i = 0; i < testCases.length; i++){
			pp.setProgress(Math.max(1,((int)(((double)(i+testCases.length)/(double)(testCases.length*3))*100))));
			pp.setMessage("Checking "+testCases[i].getVersion().getName());
			try{
				AddTestCasesOP testCaseOP = null;
				if (testCases[i] instanceof TestCaseNewBioModel) {
					pp.setMessage("Processing BM "+testCases[i].getVersion().getName());
					TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCases[i];
					
					BioModel newBioModel = (BioModel)bioModelHashMap.get(bioTestCase.getBioModelInfo().getVersion().getVersionKey());
					if(newBioModel == null){
						throw new Exception("BioModel not found");
					}
					cbit.vcell.mapping.SimulationContext simContext = null;
					for (int j = 0; j < newBioModel.getSimulationContexts().length; j++){
						if (newBioModel.getSimulationContexts(j).getName().equals(bioTestCase.getSimContextName())){
							simContext = newBioModel.getSimulationContexts(j);
						}
					}
					
					Simulation[] newSimulations = simContext.getSimulations();
					AddTestCriteriaOPBioModel[] testCriteriaOPs = new AddTestCriteriaOPBioModel[newSimulations.length];
					for (int j = 0; j < newSimulations.length; j++){
						TestCriteriaNewBioModel tcritOrigForSimName = null;
						for(int k=0;bioTestCase.getTestCriterias() != null && k < bioTestCase.getTestCriterias().length;k+= 1){
							if(bioTestCase.getTestCriterias()[k].getSimInfo().getName().equals(newSimulations[j].getName())){
								tcritOrigForSimName = (TestCriteriaNewBioModel)bioTestCase.getTestCriterias()[k];
								break;
							}
						}
						
						testCriteriaOPs[j] =
							new AddTestCriteriaOPBioModel(testCases[i].getTCKey(),
								newSimulations[j].getVersion().getVersionKey(),
								(tcritOrigForSimName != null && tcritOrigForSimName.getRegressionBioModelInfo() != null?tcritOrigForSimName.getRegressionBioModelInfo().getVersion().getVersionKey():null),
								(tcritOrigForSimName != null && tcritOrigForSimName.getRegressionSimInfo() != null?tcritOrigForSimName.getRegressionSimInfo().getVersion().getVersionKey():null),
								(tcritOrigForSimName != null?tcritOrigForSimName.getMaxAbsError():new Double(0)),
								(tcritOrigForSimName != null?tcritOrigForSimName.getMaxRelError():new Double(0)),
								null);
					}

					testCaseOP =
						new AddTestCasesOPBioModel(
							new BigDecimal(tsInfo.getTSKey().toString()),
							newBioModel.getVersion().getVersionKey(),
							simContext.getKey(),
							bioTestCase.getType(), bioTestCase.getAnnotation(), testCriteriaOPs);

					getRequestManager().getDocumentManager().doTestSuiteOP(testCaseOP);
				}
			}catch(Throwable e){
				errors.append("Error processing Biomodel for TestCase "+
							(testCases[i].getVersion() != null?"Name="+testCases[i].getVersion().getName():"TCKey="+testCases[i].getTCKey())+"\n"+
							e.getClass().getName()+" "+e.getMessage()+"\n");
			}
		}
		//}
		
		// Process MathModels
		for (int i = 0; i < testCases.length; i++){
			pp.setProgress(Math.max(1,((int)(((double)(i+testCases.length+testCases.length)/(double)(testCases.length*3))*100))));
			pp.setMessage("Checking "+testCases[i].getVersion().getName());
			try{
				AddTestCasesOP testCaseOP = null;
				if (testCases[i] instanceof TestCaseNewMathModel) {
					TestCaseNewMathModel mathTestCase = (TestCaseNewMathModel)testCases[i];
					pp.setMessage("Getting MathModel "+testCases[i].getVersion().getName());
					MathModel mathModel = getRequestManager().getDocumentManager().getMathModel(mathTestCase.getMathModelInfo().getVersion().getVersionKey());
					if (!mathModel.getVersion().getOwner().equals(getRequestManager().getDocumentManager().getUser())) {
						throw new Exception("MathModel does not belong to VCELLTESTACCOUNT, cannot proceed with test!");
					}
					Simulation[] sims = mathModel.getSimulations();
					String[] simNames = new String[sims.length];
					for (int j = 0; j < sims.length; j++){
						// prevents parent simulation (from the original mathmodel) reference connection
						// Otherwise it will refer to data from previous (parent) simulation.
						sims[j].clearVersion();
						simNames[j] = sims[j].getName();
					}

					pp.setMessage("Saving MathModel "+testCases[i].getVersion().getName());
					MathModel newMathModel = getRequestManager().getDocumentManager().save(mathModel, simNames);
					Simulation[] newSimulations = newMathModel.getSimulations();
					AddTestCriteriaOPMathModel[] testCriteriaOPs = new AddTestCriteriaOPMathModel[newSimulations.length];
					for (int j = 0; j < newSimulations.length; j++){
						TestCriteriaNewMathModel tcritOrigForSimName = null;
						for(int k=0;mathTestCase.getTestCriterias() != null && k < mathTestCase.getTestCriterias().length;k+= 1){
							if(mathTestCase.getTestCriterias()[k].getSimInfo().getName().equals(newSimulations[j].getName())){
								tcritOrigForSimName = (TestCriteriaNewMathModel)mathTestCase.getTestCriterias()[k];
								break;
							}
						}
						
						testCriteriaOPs[j] =
						new AddTestCriteriaOPMathModel(
							testCases[i].getTCKey(),
							newSimulations[j].getVersion().getVersionKey(),
							(tcritOrigForSimName != null && tcritOrigForSimName.getRegressionMathModelInfo() != null?tcritOrigForSimName.getRegressionMathModelInfo().getVersion().getVersionKey():null),
							(tcritOrigForSimName != null && tcritOrigForSimName.getRegressionSimInfo() != null?tcritOrigForSimName.getRegressionSimInfo().getVersion().getVersionKey():null),
							(tcritOrigForSimName != null?tcritOrigForSimName.getMaxAbsError():new Double(0)),
							(tcritOrigForSimName != null?tcritOrigForSimName.getMaxRelError():new Double(0)),
							null);
					}

					testCaseOP =
						new AddTestCasesOPMathModel(
							new BigDecimal(tsInfo.getTSKey().toString()),
							newMathModel.getVersion().getVersionKey(),
							mathTestCase.getType(), mathTestCase.getAnnotation(),
							testCriteriaOPs);
						
					getRequestManager().getDocumentManager().doTestSuiteOP(testCaseOP);
				}
			}catch(Throwable e){
					errors.append("Error processing MathModel for TestCase "+
								(testCases[i].getVersion() != null?"Name="+testCases[i].getVersion().getName():"TCKey="+testCases[i].getTCKey())+"\n"+
								e.getClass().getName()+" "+e.getMessage()+"\n");
			}
		}
	}
	
	if(errors.length() > 0){
		return errors.toString();
	}
	return null;

}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void checkNewTestSuiteInfo(TestSuiteInfoNew newTestSuiteInfo) throws DataAccessException{

	if(newTestSuiteInfo == null){
		throw new IllegalArgumentException("TestSuiteInfo is null");
	}

	// check if the newly defined testSuite already exists.
	TestSuiteInfoNew[] testSuiteInfos = null;
	
	testSuiteInfos = getRequestManager().getDocumentManager().getTestSuiteInfos();
	if(testSuiteInfos != null){
		for (int i = 0; i < testSuiteInfos.length; i++) {
			if (newTestSuiteInfo.getTSID().equals(testSuiteInfos[i].getTSID())) {
				throw new RuntimeException("TestSuite Version "+newTestSuiteInfo.getTSID()+" already exists");
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:52:18 AM)
 * @return boolean
 * @param mathDesc cbit.vcell.math.MathDescription
 * 
 */
public void compare(TestCriteriaNew testCriteria){
	
	// create the merged data for the simulationInfo in testCriteria and the regression simInfo
	SimulationInfo simInfo = testCriteria.getSimInfo();
	SimulationInfo regrSimInfo = testCriteria.getRegressionSimInfo();

	if (regrSimInfo == null) {
		return;
	}
	
	VCDataIdentifier vcSimId1 = new VCSimulationDataIdentifier(simInfo.getAuthoritativeVCSimulationIdentifier(), 0); 
	VCDataIdentifier vcSimId2 = new VCSimulationDataIdentifier(regrSimInfo.getAuthoritativeVCSimulationIdentifier(), 0);
	User user = simInfo.getOwner();
	MergedDataInfo mergedDataInfo = new MergedDataInfo(user, new VCDataIdentifier[] {vcSimId1, vcSimId2});

	// get the data manager and wire it up
	try {
		DataManager mergedDataManager = getRequestManager().getDataManager(mergedDataInfo, false);

		//
		// get all "Data1.XXX" data identifiers ... and remove those which are functions
		// add functions of the form DIFF_XXX = (Data1.XXX - Data2.XXX) for convenience in comparing results.
		//
		Simulation sim1 = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(simInfo);
		Simulation sim2 = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(regrSimInfo);
		DataManager data1Manager = getRequestManager().getDataManager(vcSimId1, sim1.getIsSpatial());
		DataManager data2Manager = getRequestManager().getDataManager(vcSimId2, sim2.getIsSpatial());
		
		Vector functionList = new Vector();
		cbit.vcell.math.AnnotatedFunction data1Functions[] = data1Manager.getFunctions();
		cbit.vcell.math.AnnotatedFunction existingFunctions[] = mergedDataManager.getFunctions();
		cbit.vcell.math.DataIdentifier data1Identifiers[] = data1Manager.getDataIdentifiers();
		cbit.vcell.math.DataIdentifier data2Identifiers[] = data2Manager.getDataIdentifiers();
		for (int i = 0; i < data1Identifiers.length; i++){
			//
			// make sure dataIdentifier is not already a function
			//
			boolean bIsFunction = false;
			for (int j = 0; j < data1Functions.length; j++){
				if (data1Identifiers[i].getName().equals(data1Functions[j].getName())){
					bIsFunction = true;
				}
			}
			if (bIsFunction){
				continue;
			}
			//
			// make sure corresponding identifier exists in "Data2"
			//
			boolean bIsInData2 = false;
			for (int j = 0; j < data2Identifiers.length; j++){
				if (data2Identifiers[j].getName().equals(data1Identifiers[i].getName())){
					bIsInData2 = true;
				}
			}
			if (!bIsInData2){
				continue;
			}
			
			//
			// create "Diff" function
			//
			String data1Name = "Data1."+data1Identifiers[i].getName();
			String data2Name = "Data2."+data1Identifiers[i].getName();
			String functionName = "DIFF_"+data1Identifiers[i].getName();
			cbit.vcell.math.VariableType varType = data1Identifiers[i].getVariableType();
			cbit.vcell.parser.Expression exp = new cbit.vcell.parser.Expression(data1Name+"-"+data2Name);
			cbit.vcell.math.AnnotatedFunction newFunction = new cbit.vcell.math.AnnotatedFunction(functionName,exp,"",varType,true);
			
			//
			// make sure new "Diff" function isn't already in existing function list.
			//
			boolean bDiffFunctionAlreadyHere = false;
			for (int j = 0; j < existingFunctions.length; j++){
				if (newFunction.getName().equals(existingFunctions[j].getName())){
					bDiffFunctionAlreadyHere = true;
				}
			}
			if (bDiffFunctionAlreadyHere){
				continue;
			}
			
			functionList.add(newFunction);
		}
		if (functionList.size()>0){
			AnnotatedFunction[] newDiffFunctions = (AnnotatedFunction[])BeanUtils.getArray(functionList,AnnotatedFunction.class);
			mergedDataManager.addFunctions(newDiffFunctions);
		}

		
		// make the viewer
		DynamicDataManager dynamicMergedDataMgr = getRequestManager().getDynamicDataManager(mergedDataInfo);
		addDataListener(dynamicMergedDataMgr);
		DataViewer viewer = dynamicMergedDataMgr.createViewer(mergedDataManager.getIsODEData());
		viewer.setDataViewerManager(this);
		addExportListener(viewer);
		
		// create the simCompareWindow - this is just a lightweight window to display the simResults. 
		// It was created originally to compare 2 sims, it can also be used here instead of creating the more heavy-weight SimWindow.
		SimulationCompareWindow simCompareWindow = new SimulationCompareWindow(mergedDataInfo, viewer);
		if (simCompareWindow != null) {
			// just show it right now...
			final JInternalFrame existingFrame = simCompareWindow.getFrame();
			DocumentWindowManager.showFrame(existingFrame, getTestingFrameworkWindowPanel().getJDesktopPane1());
			
			//SwingUtilities.invokeLater(new Runnable() {
				//public void run() {
					//DocumentWindowManager.showFrame(existingFrame, desktopPane);
				//}
			//});
		}

	} catch (Throwable e) {
		PopupGenerator.showErrorDialog(e.getMessage());
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2006 4:13:02 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(cbit.vcell.simdata.DataJobEvent event) {
	
	// just pass them along...
	fireDataJobMessage(event);
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public String duplicateTestSuite(
		final TestSuiteInfoNew testSuiteInfo_Original,
		final TestSuiteInfoNew newTestSuiteInfo,
		AsynchProgressPopup pp) throws DataAccessException{
	
	if (testSuiteInfo_Original == null || newTestSuiteInfo == null) {
		throw new IllegalArgumentException(this.getClass().getName()+"duplicateTestSuite_Private: TestSuite cannot be null");
	}
	
	checkNewTestSuiteInfo(newTestSuiteInfo);

	TestSuiteNew testSuite_Original = getRequestManager().getDocumentManager().getTestSuite(testSuiteInfo_Original.getTSKey());
	if(testSuite_Original == null){
		throw new DataAccessException("Couldn't get TestSuite for tsInfo "+testSuiteInfo_Original.getTSID());
	}
	AddTestSuiteOP testSuiteOP =
		new AddTestSuiteOP(
			newTestSuiteInfo.getTSID(),
			newTestSuiteInfo.getTSVCellBuild(),
			newTestSuiteInfo.getTSNumericsBuild(),null);
		
	getRequestManager().getDocumentManager().doTestSuiteOP(testSuiteOP);

	TestSuiteInfoNew[] tsinArr = getRequestManager().getDocumentManager().getTestSuiteInfos();
	TestSuiteInfoNew tsin = null;
	for(int i=0;i<tsinArr.length;i+= 1){
		if(tsinArr[i].getTSID().equals(newTestSuiteInfo.getTSID())){
			tsin = tsinArr[i];
			break;
		}
	}
	if(tsin == null){
		throw new DataAccessException("couldn't find new TestSuiteInfo "+newTestSuiteInfo.getTSID()+" in DB");
	}

	TestCaseNew[] originalTestCases = testSuite_Original.getTestCases();
	TestCaseNew[] newTestCases = null;
	if(originalTestCases != null && originalTestCases.length > 0){
		newTestCases = new TestCaseNew[originalTestCases.length];
		for(int i=0;i<originalTestCases.length;i+= 1){
			if(originalTestCases[i] instanceof TestCaseNewMathModel){
				TestCaseNewMathModel tcnmm = (TestCaseNewMathModel)originalTestCases[i];
				TestCriteriaNew[] tcritnmm = (TestCriteriaNew[])tcnmm.getTestCriterias();
				TestCriteriaNewMathModel[] newTCrits = null;
				if(tcritnmm != null && tcritnmm.length > 0){
					//Copy regression and errors
					newTCrits = new TestCriteriaNewMathModel[tcritnmm.length];
					for(int j=0;j<tcritnmm.length;j+= 1){
						newTCrits[j] =
							new TestCriteriaNewMathModel(
								null,
								tcritnmm[j].getSimInfo(),
								((TestCriteriaNewMathModel)tcritnmm[j]).getRegressionMathModelInfo(),
								tcritnmm[j].getRegressionSimInfo(),
								tcritnmm[j].getMaxRelError(),
								tcritnmm[j].getMaxAbsError(),
								null,
								TestCriteriaNew.TCRIT_STATUS_NODATA,null//new will have no data
							);
					}
				}
				//copy mathmodel,type and annotation and copied tcrits
				newTestCases[i] =
					new TestCaseNewMathModel(
						null,
						tcnmm.getMathModelInfo(),
						tcnmm.getType(),
						tcnmm.getAnnotation(),
						newTCrits
					);
			}else if(originalTestCases[i] instanceof TestCaseNewBioModel){
				TestCaseNewBioModel tcnbm = (TestCaseNewBioModel)originalTestCases[i];
				TestCriteriaNew[] tcritnbm = (TestCriteriaNew[])tcnbm.getTestCriterias();
				TestCriteriaNewBioModel[] newTCrits = null;
				if(tcritnbm != null && tcritnbm.length > 0){
					//Copy regression and errors
					newTCrits = new TestCriteriaNewBioModel[tcritnbm.length];
					for(int j=0;j<tcritnbm.length;j+= 1){
						newTCrits[j] =
							new TestCriteriaNewBioModel(
								null,
								tcritnbm[j].getSimInfo(),
								((TestCriteriaNewBioModel)tcritnbm[j]).getRegressionBioModelInfo(),
								((TestCriteriaNewBioModel)tcritnbm[j]).getRegressionApplicationName(),
								tcritnbm[j].getRegressionSimInfo(),
								tcritnbm[j].getMaxRelError(),
								tcritnbm[j].getMaxAbsError(),
								null,
								TestCriteriaNew.TCRIT_STATUS_NODATA,null//new will have no data
							);
					}
				}
				//copy mathmodel,type and annotation and copied tcrits
				newTestCases[i] =
					new TestCaseNewBioModel(
						null,
						tcnbm.getBioModelInfo(),
						tcnbm.getSimContextName(),
						tcnbm.getSimContextKey(),
						tcnbm.getType(),
						tcnbm.getAnnotation(),
						newTCrits
					);
			}else{
				throw new RuntimeException("Unsupported TestCase type "+originalTestCases[i].getClass().getName());
			}
		}
	}
	
	//Add the new TestCases
	if(newTestCases != null && newTestCases.length > 0){
		 return addTestCases(tsin,newTestCases,pp);
	}else{
		return null;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/15/2004 2:55:34 AM)
 * @param exportEvent cbit.rmi.event.ExportEvent
 */
public void exportMessage(ExportEvent exportEvent) {
	// just pass them along...
	fireExportMessage(exportEvent);
	/*
	if (exportEvent.getEventTypeID() == ExportEvent.EXPORT_COMPLETE) {
		// try to download the thing
		downloadExportedData(exportEvent);
	}
	*/
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 5:36:47 PM)
 */
public String generateTestCaseReport(TestCaseNew testCase,TestCriteriaNew onlyThisTCrit,cbit.util.AsynchProgressPopup pp) {

	StringBuffer reportTCBuffer = new StringBuffer();
	if (testCase == null) {
		reportTCBuffer.append("\n\tTEST CASE : "+testCase.getAnnotation()+"\n"+"\tERROR: Test Case is NULL\n");
	}else{

		pp.setMessage(testCase.getVersion().getName()+" "+testCase.getType()+" Getting Simulations");
		// Get the Simulations
		Simulation[] sims = null;
		reportTCBuffer.append("\n\tTEST CASE : "+(testCase.getVersion() != null?testCase.getVersion().getName():"Null")+" "+testCase.getAnnotation()+"\n");
		try{
			if(testCase instanceof TestCaseNewMathModel){
				MathModelInfo mmInfo = ((TestCaseNewMathModel)testCase).getMathModelInfo();
				MathModel mathModel = getRequestManager().getDocumentManager().getMathModel(mmInfo);
				sims = mathModel.getSimulations();
				reportTCBuffer.append(
					"\tMathModel : "+mmInfo.getVersion().getName()+", "+mmInfo.getVersion().getDate().toString()+"\n");
			}else if(testCase instanceof TestCaseNewBioModel){
				TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCase;
				//bioTestCase.
				BioModelInfo bmInfo = bioTestCase.getBioModelInfo();
				BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bmInfo);
				cbit.vcell.mapping.SimulationContext[] simContextArr = bioModel.getSimulationContexts();
				if(simContextArr != null && simContextArr.length > 0){
					cbit.vcell.mapping.SimulationContext simContext = null;
					for(int i=0;i<simContextArr.length;i+= 1){
						if(simContextArr[i].getVersion().getVersionKey().compareEqual(bioTestCase.getSimContextKey())){
							simContext = simContextArr[i];
							break;
						}
					}
					if(simContext != null){
						sims = bioModel.getSimulations(simContext);
						reportTCBuffer.append(
							"\tBioModel : "+bmInfo.getVersion().getName()+", "+bmInfo.getVersion().getDate().toString()+"\n");
					}
				}
			}
			if(sims == null || sims.length == 0){
				reportTCBuffer.append("\tERROR "+"No sims found for TestCase "+
							(testCase.getVersion() != null?"name="+testCase.getVersion().getName():"key="+testCase.getTCKey())+"\n");
			}
			
			if(testCase.getTestCriterias() == null || sims.length != testCase.getTestCriterias().length){
				reportTCBuffer.append("\tERROR "+"Num sims="+sims.length+" does not match testCriteria length="+
							(testCase.getTestCriterias() != null?testCase.getTestCriterias().length+"":"null")+
					" for TestCase "+(testCase.getVersion() != null?"name="+testCase.getVersion().getName():"key="+testCase.getTCKey())+"\n");
			}
			
			//Sort
			if(sims.length > 0){
				java.util.Arrays.sort(sims,
					new java.util.Comparator (){
							public int compare(Object o1,Object o2){
								Simulation si1 = (Simulation)o1;
								Simulation si2 = (Simulation)o2;
								return si1.getName().compareTo(si2.getName());
							}
							public boolean equals(Object obj){
								return false;
							}
						}
				);
			}

			TestCriteriaNew[] testCriterias = (onlyThisTCrit != null?new TestCriteriaNew[] {onlyThisTCrit}:testCase.getTestCriterias());
			
			for (int k = 0;k < sims.length; k++) {
				TestCriteriaNew testCriteria = getMatchingTestCriteria(sims[k],testCriterias);
				if(testCriteria != null){
					//if(testCriteria.getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_PASSED) ||
						//testCriteria.getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_FAILEDVARS)){
							//continue;
					//}
					pp.setMessage((testCase instanceof TestCaseNewMathModel?"(MM)":"(BM)")+" "+
						(onlyThisTCrit == null?"sim "+(k+1)+" of "+sims.length:"sim="+onlyThisTCrit.getSimInfo().getName())+"  "+testCase.getVersion().getName()+" "+testCase.getType());
					reportTCBuffer.append(generateTestCriteriaReport(testCase,testCriteria,sims[k]));
				}
			}
		}catch(Throwable e){
			e.printStackTrace();
			reportTCBuffer.append("\tERROR "+e.getClass().getName()+" mesg="+e.getMessage()+"\n");
			try{
				if(onlyThisTCrit != null){
					updateTCritStatus(onlyThisTCrit,TestCriteriaNew.TCRIT_STATUS_RPERROR,"TestCase Error "+e.getClass().getName()+" "+e.getMessage());
				}else if(testCase.getTestCriterias() != null){
					for(int i=0;i<testCase.getTestCriterias().length;i+= 1){
						updateTCritStatus(testCase.getTestCriterias()[i],TestCriteriaNew.TCRIT_STATUS_RPERROR,"TestCase Error "+e.getClass().getName()+" "+e.getMessage());
					}
				}
			}catch(Throwable e2){
				//
			}
		}
	}

	return reportTCBuffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 5:36:47 PM)
 * 
 */
private String generateTestCriteriaReport(TestCaseNew testCase,TestCriteriaNew testCriteria,Simulation sim) {

	String simReportStatus = null;
	String simReportStatusMessage = null;
	
	StringBuffer reportTCBuffer = new StringBuffer();
	VariableComparisonSummary failVarSummaries[] = null;
	VariableComparisonSummary allVarSummaries[] = null;
	double absErr = 0;
	double relErr = 0;
	if (testCriteria != null) {
		absErr = testCriteria.getMaxAbsError().doubleValue();
	 	relErr = testCriteria.getMaxRelError().doubleValue();
	}
	
	reportTCBuffer.append("\t\t"+sim.getName() + " : "+"\n");
	try {		
		SimulationInfo refSimInfo = testCriteria.getRegressionSimInfo();
		if (testCase.getType().equals(TestCaseNew.REGRESSION) && refSimInfo == null) {
			reportTCBuffer.append("\t\t\tNo reference SimInfo, SimInfoKey="+testCriteria.getSimInfo().getVersion().getName()+". Cannot perform Regression Test!\n");
			simReportStatus = TestCriteriaNew.TCRIT_STATUS_NOREFREGR;
		}else{
			VCDataIdentifier vcdID = new VCSimulationDataIdentifier(sim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), 0);
			DataManager dataManager = getRequestManager().getDataManager(vcdID, sim.getIsSpatial());
			
			double timeArray[] = dataManager.getDataSetTimes();
			if (timeArray == null || timeArray.length == 0) {
				reportTCBuffer.append("\t\t\tNO DATA : Simulation not run yet.\n");
				simReportStatus = TestCriteriaNew.TCRIT_STATUS_NODATA;
			} else {
				// SPATIAL simulation
				if (sim.getMathDescription().isSpatial()){
					// Get EXACT solution if test case type is EXACT, Compare with numerical
					if (testCase.getType().equals(TestCaseNew.EXACT)) {
						SimulationComparisonSummary simCompSummary = MathTestingUtilities.comparePDEResultsWithExact(sim, dataManager);
						// Failed var summaries
						failVarSummaries = simCompSummary.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					// Get CONSTRUCTED solution if test case type is CONSTRUCTED, Compare with numerical
					} else if (testCase.getType().equals(TestCaseNew.CONSTRUCTED)) {
						SimulationComparisonSummary simCompSummary = MathTestingUtilities.comparePDEResultsWithExact(sim, dataManager);
						// Failed var summaries
						failVarSummaries = simCompSummary.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					} else if (testCase.getType().equals(TestCaseNew.REGRESSION)) {
						Simulation refSim = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(refSimInfo);
						VCDataIdentifier refVcdID = new VCSimulationDataIdentifier(refSimInfo.getAuthoritativeVCSimulationIdentifier(), 0);
						DataManager refDataManager = getRequestManager().getDataManager(refVcdID, refSim.getIsSpatial());
						String varsToCompare[] = getVariableNamesToCompare(sim,refSim);
						SimulationComparisonSummary simCompSummary = MathTestingUtilities.comparePDEResults(sim, dataManager, refSim, refDataManager, varsToCompare);
						// Failed var summaries
						failVarSummaries = simCompSummary.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}							
					}
				}else{  // NON-SPATIAL CASE
					ODESolverResultSet numericalResultSet = dataManager.getODESolverResultSet();
					// Get EXACT result set if test case type is EXACT, Compare with numerical
					if (testCase.getType().equals(TestCaseNew.EXACT)) {
						ODESolverResultSet exactResultSet = MathTestingUtilities.getExactResultSet(sim.getMathDescription(), timeArray, sim.getSolverTaskDescription().getSensitivityParameter());
						String varsToCompare[] = getVariableNamesToCompare(sim,sim);
						SimulationComparisonSummary simCompSummary_exact = MathTestingUtilities.compareResultSets(numericalResultSet,exactResultSet,varsToCompare);

						// Get all the variable comparison summaries and the failed ones to print out report for EXACT solution comparison.
						failVarSummaries = simCompSummary_exact.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary_exact.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}							
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					// Get CONSTRUCTED result set if test case type is CONSTRUCTED , compare with numerical
					} else if (testCase.getType().equals(TestCaseNew.CONSTRUCTED)) {
						ODESolverResultSet constructedResultSet = MathTestingUtilities.getConstructedResultSet(sim.getMathDescription(), timeArray);
						String varsToCompare[] = getVariableNamesToCompare(sim,sim);
						SimulationComparisonSummary simCompSummary_constr = MathTestingUtilities.compareResultSets(numericalResultSet,constructedResultSet,varsToCompare);

						// Get all the variable comparison summaries and the failed ones to print out report for CONSTRUCTED solution comparison.
						failVarSummaries = simCompSummary_constr.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary_constr.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}							
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					} else if (testCase.getType().equals(TestCaseNew.REGRESSION)) {
						Simulation refSim = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(testCriteria.getRegressionSimInfo());
						String varsToTest[] = getVariableNamesToCompare(sim,refSim);
						
						VCDataIdentifier refVcdID = new VCSimulationDataIdentifier(refSimInfo.getAuthoritativeVCSimulationIdentifier(), 0);
						DataManager refDataManager = getRequestManager().getDataManager(refVcdID, refSim.getIsSpatial());
						ODESolverResultSet referenceResultSet = refDataManager.getODESolverResultSet();
						double refTimeArray[] = refDataManager.getDataSetTimes();
						SimulationComparisonSummary simCompSummary_regr = null;							
						if (timeArray.length != refTimeArray.length) {
							simCompSummary_regr = MathTestingUtilities.compareUnEqualResultSets(numericalResultSet, referenceResultSet,varsToTest);
						} else {
							simCompSummary_regr = MathTestingUtilities.compareResultSets(numericalResultSet, referenceResultSet, varsToTest);
						}
						// Get all the variable comparison summaries and the failed ones to print out report for CONSTRUCTED solution comparison.
						failVarSummaries = simCompSummary_regr.getFailingVariableComparisonSummaries(absErr, relErr);
						allVarSummaries = simCompSummary_regr.getVariableComparisonSummaries();
						if (failVarSummaries.length>0){
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_FAILEDVARS;
							// Failed simulation
							reportTCBuffer.append("\t\tTolerance test FAILED \n");
							reportTCBuffer.append("\t\tFailed Variables : \n");
							for (int m = 0; m < failVarSummaries.length; m++){
								reportTCBuffer.append("\t\t\t"+failVarSummaries[m].toShortString()+"\n");
							}							
						} else {
							simReportStatus = TestCriteriaNew.TCRIT_STATUS_PASSED;
							reportTCBuffer.append("\t\tTolerance test PASSED \n");
						}

						reportTCBuffer.append("\t\tPassed Variables : \n");
						// Check if varSummary exists in failed summaries list. If not, simulation passed.
						for (int m = 0; m < allVarSummaries.length; m++) {
							if (!cbit.util.BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}													
					}
				}
			}
		}
	} catch (Throwable e) {
		simReportStatus = TestCriteriaNew.TCRIT_STATUS_RPERROR;
		simReportStatusMessage = e.getClass().getName()+" "+e.getMessage();
		reportTCBuffer.append("\t\t"+simReportStatusMessage+"\n");
		e.printStackTrace(System.out);
	}

	try{			
		// Remove any test results already present for testCriteria
		RemoveTestResultsOP removeResultsOP = new RemoveTestResultsOP(new BigDecimal[] {testCriteria.getTCritKey()});
		//testResultsOPsVector.add(removeResultsOP);
		getRequestManager().getDocumentManager().doTestSuiteOP(removeResultsOP)		;
		// Create new AddTestREsultsOP object for the current simulation./testCriteria.
		if(allVarSummaries != null){
			AddTestResultsOP testResultsOP = new AddTestResultsOP(testCriteria.getTCritKey(), allVarSummaries);
			//testResultsOPsVector.add(testResultsOP);			
			// Write the testResults for simulation/TestCriteria into the database ...
			getRequestManager().getDocumentManager().doTestSuiteOP(testResultsOP);
		}
		//Update report status
		updateTCritStatus(testCriteria,simReportStatus,simReportStatusMessage);
	}catch(Throwable e){
		reportTCBuffer.append("\t\tUpdate DB Results failed. "+e.getClass().getName()+" "+e.getMessage()+"\n");
		//try{
			//getRequestManager().getDocumentManager().doTestSuiteOP(
				//new EditTestCriteriaOPReportStatus(testCriteria.getTCritKey(),TestCriteriaNew.TCRIT_STATUS_RPERROR,e.getClass().getName()+" "+e.getMessage())
				//);
		//}catch(Throwable e2){
			////Nothing more can be done
		//}
	}
		
	//}

	return reportTCBuffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 5:36:47 PM)
 */
public String generateTestSuiteReport(TestSuiteInfoNew testSuiteInfo,AsynchProgressPopup pp) {

	if (testSuiteInfo == null) {
		return "Test Suite is null";
	}
	StringBuffer sb = new StringBuffer();
	try{
		//Iterate thro' testSuiteInfo to add annotation to reportTSBuffer and get all test cases
		// Get TestSuite corresponding to argument testSuiteInfo
		TestSuiteNew testSuite = getRequestManager().getDocumentManager().getTestSuite(testSuiteInfo.getTSKey());
		// Get MathmodelInfos associated with each test suite
		TestCaseNew[] testCases  = null;
		testCases = testSuite.getTestCases();
		if (testCases == null) {
			return testSuiteInfo.toString()+" has not testcases";
		}

		// Iterate thro' test cases to get all simulations and get the variableSimulationSummary, add to buffer
		sb.append("\n"+testSuiteInfo.toString()+"\n");
		for (int j = 0; j < testCases.length; j++) {
			pp.setProgress(1+(int)((((double)j/(double)testCases.length)*100)));
			sb.append(generateTestCaseReport(testCases[j],null,pp));
		}
		
	}catch(Throwable e){
		e.printStackTrace();
		sb.append("ERROR "+e.getClass().getName()+" mesg="+e.getMessage());
	}finally{
		return sb.toString();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getAddTestCaseDialog() {
	return addTestCaseDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getAddTestSuiteDialog() {
	return addTestSuiteDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return cbit.vcell.client.desktop.AddTestSuitePanel
 */
private AddTestSuitePanel getAddTestSuitePanel() {
	return addTestSuitePanel;
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 11:44:12 AM)
 * @return java.lang.String
 */
java.awt.Component getComponent() {
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return javax.swing.JOptionPane
 */
private javax.swing.JOptionPane getEditTestCriteriaDialog() {
	return editTestCriteriaDialog;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return cbit.vcell.client.desktop.EditTestCriteriaPanel
 */
private EditTestCriteriaPanel getEditTestCriteriaPanel() {
	return editTestCriteriaPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 11:44:12 AM)
 * @return java.lang.String
 */
public String getManagerID() {
	return ClientMDIManager.TESTING_FRAMEWORK_WINDOW_ID;
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */
 
// Used to obtain the testCriteria (from the testCriterias array) matching the simulation passed as argument.

private TestCriteriaNew getMatchingTestCriteria(Simulation sim, TestCriteriaNew[] tcriterias){
	for (int i = 0; i < tcriterias.length; i++){
		if (tcriterias[i].getSimInfo().getVersion().getVersionKey().equals(sim.getVersion().getVersionKey())) {
			return tcriterias[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */
public TestCaseNew getNewTestCase(){
	// invoke the testCaseAddPanel to define testCaseInfo parameters.
	// This is where we invoke the TestCaseAddPanel to define a testCase for the test suite ...
	getTestCaseAddPanel().resetTextFields();
	Object choice = showAddTestCaseDialog(getTestCaseAddPanel(), null);
	
	if (choice != null && choice.equals("OK")) {
		return getTestCaseAddPanel().getNewTestCase();
	}
	return null;	
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 1:31:08 PM)
 * @return cbit.vcell.numericstestingframework.TestSuiteInfo
 */
public TestCriteriaNew getNewTestCriteriaFromUser(String solutionType, TestCriteriaNew origTestCriteria)throws UserCancelException {

	// Reset the text fields in the EditCriteriaPanel.	
	getEditTestCriteriaPanel().setExistingTestCriteria(origTestCriteria);
	getEditTestCriteriaPanel().setSolutionType(solutionType);
	getEditTestCriteriaPanel().resetTextFields();

	// display the editCriteriaPanel.
	Object choice = showEditTestCriteriaDialog(getEditTestCriteriaPanel(), null);

	if (choice != null && choice.equals("OK")) {
		return getEditTestCriteriaPanel().getNewTestCriteria();
	}

	throw UserCancelException.CANCEL_GENERIC;
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public TestSuiteInfoNew getNewTestSuiteInfoFromUser() throws UserCancelException{

	getAddTestSuitePanel().resetTextFields(true);
	Object choice = showAddTestSuiteDialog(getAddTestSuitePanel(), null);

	if (choice != null && choice.equals("OK")) {
		return getAddTestSuitePanel().getTestSuiteInfo();
	}
	
	throw UserCancelException.CANCEL_DB_SELECTION;

}


/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:08:56 PM)
 * @return cbit.sql.KeyValue
 * @param bmInfo cbit.vcell.biomodel.BioModelInfo
 * @param appName java.lang.String
 */
public KeyValue getSimContextKey(BioModelInfo bmInfo, String appName) throws DataAccessException {
	BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bmInfo);
	if (bioModel!=null){
		cbit.vcell.mapping.SimulationContext simContexts[] = bioModel.getSimulationContexts();
		for (int i = 0; i < simContexts.length; i++){
			if (simContexts[i].getName().equals(appName)){
				return simContexts[i].getVersion().getVersionKey();
			}
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return cbit.vcell.client.desktop.TestCaseAddPanel
 */
private TestCaseAddPanel getTestCaseAddPanel() {
	return testCaseAddPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2004 4:44:02 PM)
 * @return cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public cbit.vcell.client.desktop.TestingFrameworkWindowPanel getTestingFrameworkWindowPanel() {
	return testingFrameworkWindowPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 1:53:11 PM)
 * @return java.lang.String[]
 * @param sim1 cbit.vcell.solver.Simulation
 * @param sim2 cbit.vcell.solver.Simulation
 */
private String[] getVariableNamesToCompare(Simulation sim1, Simulation sim2) {
	java.util.HashSet hashSet = new java.util.HashSet();

	//
	// get Variables from Simulation 1
	//	
	cbit.vcell.math.Variable simVars[] = sim1.getVariables();
	for (int i = 0;simVars!=null && i < simVars.length; i++){
		if (simVars[i] instanceof cbit.vcell.math.VolVariable ||
			simVars[i] instanceof cbit.vcell.math.MemVariable ||
			simVars[i] instanceof cbit.vcell.math.VolumeRegionVariable ||
			simVars[i] instanceof cbit.vcell.math.MembraneRegionVariable ||
			simVars[i] instanceof cbit.vcell.math.FilamentVariable ||
			simVars[i] instanceof cbit.vcell.math.FilamentRegionVariable){

			hashSet.add(simVars[i].getName());
		}
		if (sim1.getSolverTaskDescription().getSensitivityParameter() != null) {
			if (simVars[i] instanceof cbit.vcell.math.VolVariable) {
				hashSet.add(cbit.vcell.solver.ode.SensVariable.getSensName((cbit.vcell.math.VolVariable)simVars[i], sim1.getSolverTaskDescription().getSensitivityParameter()));
			}
		}
	}

	//
	// add Variables from Simulation 2
	//	
	simVars = sim2.getVariables();
	for (int i = 0;simVars!=null && i < simVars.length; i++){
		if (simVars[i] instanceof cbit.vcell.math.VolVariable ||
			simVars[i] instanceof cbit.vcell.math.MemVariable ||
			simVars[i] instanceof cbit.vcell.math.VolumeRegionVariable ||
			simVars[i] instanceof cbit.vcell.math.MembraneRegionVariable ||
			simVars[i] instanceof cbit.vcell.math.FilamentVariable ||
			simVars[i] instanceof cbit.vcell.math.FilamentRegionVariable){

			hashSet.add(simVars[i].getName());
		}
		if (sim2.getSolverTaskDescription().getSensitivityParameter() != null) {
			if (simVars[i] instanceof cbit.vcell.math.VolVariable) {
				hashSet.add(cbit.vcell.solver.ode.SensVariable.getSensName((cbit.vcell.math.VolVariable)simVars[i], sim2.getSolverTaskDescription().getSensitivityParameter()));
			}
		}			
	}
	
	return (String[])hashSet.toArray(new String[hashSet.size()]);
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 5:28:23 PM)
 */
public void initializeAllPanels() {
	try {
		DocumentManager documentManager = getRequestManager().getDocumentManager();
		getTestingFrameworkWindowPanel().setDocumentManager(documentManager);
		getAddTestSuitePanel().setTestingFrameworkWindowManager(this);
		getTestCaseAddPanel().setTestingFrameworkWindowManager(this);
		getEditTestCriteriaPanel().setTestingFrameworkWindowManager(this);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 11:44:12 AM)
 * @return boolean
 */
public boolean isRecyclable() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 1:31:08 PM)
 * @return cbit.vcell.numericstestingframework.TestSuiteInfo
 */
public void loadModel(TestCaseNew testCase) throws DataAccessException{
	
	cbit.util.VCDocumentInfo vcDocInfo = null;
	if (testCase instanceof TestCaseNewMathModel) {
		TestCaseNewMathModel mathTestCase = (TestCaseNewMathModel)testCase;
		vcDocInfo = getRequestManager().getDocumentManager().getMathModelInfo(mathTestCase.getMathModelInfo().getVersion().getVersionKey());
	} else if (testCase instanceof TestCaseNewBioModel) {
		TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCase;
		vcDocInfo = getRequestManager().getDocumentManager().getBioModelInfo(bioTestCase.getBioModelInfo().getVersion().getVersionKey());
	}			
	getRequestManager().openDocument(vcDocInfo, this, true);
}


/**
 * Remove a cbit.vcell.desktop.controls.DataListener.
 */
public void removeDataListener(cbit.vcell.desktop.controls.DataListener newListener) {}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 1:31:08 PM)
 * @return cbit.vcell.numericstestingframework.TestSuiteInfo
 */
public void removeTestCase(TestCaseNew testCase) throws DataAccessException{

	getRequestManager().getDocumentManager().doTestSuiteOP(
			new RemoveTestCasesOP(new BigDecimal[] {testCase.getTCKey()}));
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void removeTestSuite(TestSuiteInfoNew tsInfo) throws DataAccessException{

	TestSuiteNew testSuite = getRequestManager().getDocumentManager().getTestSuite(tsInfo.getTSKey());
	TestCaseNew[] testCases = testSuite.getTestCases();
	if (testCases != null) {
		throw new RuntimeException(
			"Cannot delete TestSuite that contains test cases. Remove test cases before deleting test suite!");
	}
	
	getRequestManager().getDocumentManager().doTestSuiteOP(new RemoveTestSuiteOP(tsInfo.getTSKey()));
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void saveNewTestSuiteInfo(TestSuiteInfoNew newTestSuiteInfo) throws DataAccessException{

	checkNewTestSuiteInfo(newTestSuiteInfo);

	AddTestSuiteOP testSuiteOP =
		new AddTestSuiteOP(
			newTestSuiteInfo.getTSID(),
			newTestSuiteInfo.getTSVCellBuild(),
			newTestSuiteInfo.getTSNumericsBuild(),
			null);
	getRequestManager().getDocumentManager().doTestSuiteOP(testSuiteOP);
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public BioModelInfo selectBioModelInfo() {
	return getRequestManager().selectBioModelInfo(this);
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public MathModelInfo selectMathModelInfo() {
	return getRequestManager().selectMathModelInfo(this);
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public Object[] selectRefSimInfo(BioModelInfo bmInfo,String appName) throws DataAccessException {
	if (bmInfo == null || appName == null || appName.length() == 0) {
		PopupGenerator.showErrorDialog("Selected Reference BioModel is null, choose a reference BioModel before choosing simulation!");
		return null;
	}

	// code for obtaining siminfos from Biomodel and displaying it as a list
	// and displaying the siminfo in the label

	cbit.vcell.mapping.SimulationContext simContext = null;
	//try {
		BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bmInfo);
		for(int i=0;i<bioModel.getSimulationContexts().length;i+= 1){
			if(bioModel.getSimulationContexts()[i].getName().equals(appName)){
				simContext = bioModel.getSimulationContexts()[i];
				break;
			}
		}
		if(simContext != null){
			cbit.vcell.simulation.SimulationInfo simInfo = selectSimInfoPrivate(bioModel.getSimulations(simContext));
			return new Object[] {simContext.getName(),simInfo};
		}else{
			PopupGenerator.showErrorDialog("No simcontext found for biomodel "+bmInfo+" app="+appName);
			return null;
		}
	//} catch (cbit.vcell.server.DataAccessException e) {
	//	e.printStackTrace(System.out);
	//}
	
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public SimulationInfo selectRefSimInfo(MathModelInfo mmInfo) {
	if (mmInfo == null) {
		PopupGenerator.showErrorDialog("Selected Reference MathModel is null, choose a reference MathModel before choosing simulation!");
		return null;
	}

	// code for obtaining siminfos from mathmodel and displaying it as a list
	// and displaying the siminfo in the label

	// Get MathModel from MathModelInfo
	MathModel mathModel = null;
	try {
		 mathModel = getRequestManager().getDocumentManager().getMathModel(mmInfo);
	} catch (cbit.util.DataAccessException e) {
		e.printStackTrace(System.out);
	}
	return selectSimInfoPrivate(mathModel.getSimulations());
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2004 1:52:50 PM)
 * @return cbit.vcell.solver.SimulationInfo
 * @param sims cbit.vcell.solver.Simulation[]
 */
private SimulationInfo selectSimInfoPrivate(Simulation[] sims) {
	
	// code for obtaining siminfos from Biomodel and displaying it as a list
	// and displaying the siminfo in the label

	//Sort
	if(sims.length > 0){
		java.util.Arrays.sort(sims,
			new java.util.Comparator (){
					public int compare(Object o1,Object o2){
						Simulation si1 = (Simulation)o1;
						Simulation si2 = (Simulation)o2;
						return si1.getName().compareTo(si2.getName());
					}
					public boolean equals(Object obj){
						return false;
					}
				}
		);
	}
	
	String simInfoNames[] = new String[sims.length];
	for (int i = 0; i < simInfoNames.length; i++){
		simInfoNames[i] = sims[i].getSimulationInfo().getName();
	}

	// Display the list of simInfo names in a list for user to choose the simulationInfo to compare with
	// in the case of regression testing.
	String selectedRefSimInfoName = (String)PopupGenerator.showListDialog(this, simInfoNames, "Please select reference simulation");
	if (selectedRefSimInfoName == null) {
		// PopupGenerator.showErrorDialog("Reference SimInfo not selected");
		return null;
	}
	int simIndex = -1;
	for (int i=0;i<simInfoNames.length;i++){
		if (simInfoNames[i].equals(selectedRefSimInfoName)){
			simIndex = i;
		}
	}
	if (simIndex == -1){
		PopupGenerator.showErrorDialog("No such SimInfo Exists : "+selectedRefSimInfoName);
		return null;
	}

	SimulationInfo simInfo = (SimulationInfo)sims[simIndex].getSimulationInfo();
	return simInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
private void setTestingFrameworkWindowPanel(cbit.vcell.client.desktop.TestingFrameworkWindowPanel newTestingFrameworkWindowPanel) {
	testingFrameworkWindowPanel = newTestingFrameworkWindowPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showAddTestCaseDialog(JComponent addTCPanel, Component requester) {

	addTCPanel.setPreferredSize(new java.awt.Dimension(500, 200));
	getAddTestCaseDialog().setMessage("");
	getAddTestCaseDialog().setMessage(addTCPanel); 
	getAddTestCaseDialog().setValue(null);
	JDialog d = getAddTestCaseDialog().createDialog(requester, "Add New TestCase:");
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d);
	return getAddTestCaseDialog().getValue();
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showAddTestSuiteDialog(JComponent addTSPanel, Component requester) {

	addTSPanel.setPreferredSize(new java.awt.Dimension(350, 175));
	getAddTestSuiteDialog().setMessage("");
	getAddTestSuiteDialog().setMessage(addTSPanel);
	getAddTestSuiteDialog().setValue(null);
	JDialog d = getAddTestSuiteDialog().createDialog(requester, "Add New TestSuite:");
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d);
	return getAddTestSuiteDialog().getValue();
	
}


/**
 * Insert the method's description here.
 * Creation date: (6/14/2004 10:55:40 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
private void showDataViewerPlotsFrame(final javax.swing.JInternalFrame plotFrame) {
	dataViewerPlotsFramesVector.add(plotFrame);
	DocumentWindowManager.showFrame(plotFrame, getTestingFrameworkWindowPanel().getJDesktopPane1());
	plotFrame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
		public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
			dataViewerPlotsFramesVector.remove(plotFrame);
		}
	});
}
	
/**
 * Insert the method's description here.
 * Creation date: (11/18/2004 4:44:45 PM)
 * @param newDocument cbit.vcell.document.VCDocument
 */
public void showDataViewerPlotsFrames(javax.swing.JInternalFrame[] plotFrames) {
	for (int i = 0; i < plotFrames.length; i++){
		showDataViewerPlotsFrame(plotFrames[i]);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showEditTestCriteriaDialog(JComponent editTCrPanel, Component requester) {
	editTCrPanel.setPreferredSize(new java.awt.Dimension(400, 300));
	getEditTestCriteriaDialog().setMessage("");
	getEditTestCriteriaDialog().setMessage(editTCrPanel);
	getEditTestCriteriaDialog().setValue(null);
	JDialog d = getEditTestCriteriaDialog().createDialog(requester, "Edit Test Criteria:");
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	cbit.gui.ZEnforcer.showModalDialogOnTop(d);
	return getEditTestCriteriaDialog().getValue();
}


/**
 * Insert the method's description here.
 * Creation date: (11/18/2004 4:44:45 PM)
 * @param newJobStatus cbit.vcell.messaging.db.SimulationJobStatus
 * @param progress java.lang.Double
 * @param timePoint java.lang.Double
 */
public void simStatusChanged(SimStatusEvent simStatusEvent) {

	//KeyValue simKey = simStatusEvent.getVCSimulationIdentifier().getSimulationKey();
	//SimulationJobStatus jobStatus = simStatusEvent.getJobStatus();
	//if (simKey == null || jobStatus == null) {
		//// we don't know what it's all about...
		//return;
	//}
	//Simulation[] sims = getBioModel().getSimulations();
	//if (sims == null) {
		//// don't care
		//return;
	//}
	//Simulation simulation = null;
	//for (int i = 0; i < sims.length; i++){
		//if (simKey.equals(sims[i].getKey()) || simKey.equals(sims[i].getSimulationVersion().getParentSimulationReference())) {
			//simulation = sims[i];
			//break;
		//}	
	//}
	//if (simulation == null) {
		//// don't care
		//return;
	//}
	//// we have it - if failure message, notify
	//if (simStatusEvent.getJobStatus().isFailed()) {
		//PopupGenerator.showErrorDialog(this, "Simulation '" + simulation.getName() + "' failed\n" + simStatusEvent.getStatusMessage());
	//}
	//// was the gui on it ever opened? - then update list
	//SimulationContext simContext = null;
	//simulation = null;
	//Enumeration en = getApplicationsHash().keys();
	//while (en.hasMoreElements()) {
		//SimulationContext sc = (SimulationContext)en.nextElement();
		//sims = sc.getSimulations();
		//if (sims != null) {
		//}
		//for (int i = 0; i < sims.length; i++){
			//if (simKey.equals(sims[i].getKey()) || simKey.equals(sims[i].getSimulationVersion().getParentSimulationReference())) {
				//simulation = sims[i];
				//break;
			//}	
		//}
		//if (simulation != null) {
			//simContext = sc;
			//break;
		//}
	//}
	//if (simulation == null || simContext == null) {
		//return;
	//}
	//// the gui was opened...
	//ApplicationComponents appComponents = (ApplicationComponents)getApplicationsHash().get(simContext);
	//ClientSimManager simManager = appComponents.getAppEditor().getSimulationWorkspace().getClientSimManager();
	//simManager.updateStatus(simManager.getSimulationStatus(simulation).fromMessage(simStatusEvent.getJobStatus(), simStatusEvent.getProgress()), simulation);
	//// is there new data?
	//if (simStatusEvent.getTimepoint() != null) {
		//fireNewData(new DataEvent(this, simulation.getSimulationInfo().getAuthoritativeVCSimulationIdentifier()));
	//}
}


/**
 * Insert the method's description here.
 * Creation date: (11/18/2004 4:44:45 PM)
 */
public void startExport(cbit.vcell.export.ExportSpecs exportSpecs) {
	getRequestManager().startExport(this, exportSpecs);
}


/**
 * Insert the method's description here.
 * Creation date: (11/16/2004 6:38:33 AM)
 * @param simInfos cbit.vcell.solver.SimulationInfo[]
 */
public String startSimulations(TestCriteriaNew[] tcrits,AsynchProgressPopup pp) {

	if(tcrits == null || tcrits.length == 0){
		throw new IllegalArgumentException("startSimulations: No TestCriteria arguments");
	}
	
	StringBuffer errors = new StringBuffer();
	for(int i=0;i<tcrits.length;i+= 1){
		try{
			pp.setProgress((int)(1+(((double)i/(double)tcrits.length)*100)));
			pp.setMessage("Trying to run sim "+tcrits[i].getSimInfo().getName());
			getRequestManager().runSimulation(tcrits[i].getSimInfo());
			updateTCritStatus(tcrits[i],TestCriteriaNew.TCRIT_STATUS_SIMRUNNING,null);
		}catch(Throwable e){
			e.printStackTrace();
			errors.append("Failed to start sim "+tcrits[i].getSimInfo().getVersion().getName()+" "+e.getClass().getName()+" mesg="+e.getMessage()+"\n");
			try{
				updateTCritStatus(tcrits[i],TestCriteriaNew.TCRIT_STATUS_SIMFAILED,e.getClass().getName()+" "+e.getMessage());
			}catch(Throwable e2){
				e.printStackTrace();
				errors.append("Failed to start sim "+
					tcrits[i].getSimInfo().getVersion().getName()+" "+e2.getClass().getName()+" mesg="+e2.getMessage()+"\n");				
			}
		}
	}

	if(errors.length() > 0){
		errors.insert(0,"Error starting simulations\n");
		return errors.toString();
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:52:18 AM)
 * @return boolean
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public String startTestSuiteSimulations(TestSuiteInfoNew testSuiteInfo,AsynchProgressPopup pp){

	StringBuffer errors = new StringBuffer();
	try{
		pp.setProgress(1);
		pp.setMessage("Getting TestSuite "+testSuiteInfo.getTSID());
		TestSuiteNew testSuite =
			getRequestManager().getDocumentManager().getTestSuite(testSuiteInfo.getTSKey());
		
		Vector tcritVector = new Vector();
		cbit.vcell.numericstest.TestCaseNew[] testCases = testSuite.getTestCases();
		if(testCases != null){
			for (int i = 0; i < testCases.length; i++){
				TestCriteriaNew[] tCriteria = testCases[i].getTestCriterias();
				if(tCriteria != null){
					for (int j = 0; j < tCriteria.length; j++) {
						tcritVector.add(tCriteria[j]);
					}
				}
			}
			if(tcritVector.size() > 0){
				TestCriteriaNew[] tcritArray = (TestCriteriaNew[])BeanUtils.getArray(tcritVector, TestCriteriaNew.class);
				String errorString = startSimulations(tcritArray,pp);
				if(errorString != null){
					errors.append(errorString+"\n");
				}
			}
		}
	}catch(Throwable e){
		errors.append(e.getClass().getName()+" "+e.getMessage());
	}
	
	if(errors.length() > 0){
		errors.insert(0,"Error starting TestSuite simulations\n");
		return errors.toString();
	}
	return null;
	
}


/**
 * Insert the method's description here.
 * Creation date: (11/16/2004 7:44:27 AM)
 * 
 */
public String updateSimRunningStatus(AsynchProgressPopup pp){

	StringBuffer errors = new StringBuffer();
	
	Vector runningTCrits = new Vector();
	try{
		TestSuiteInfoNew[] tsinfos = getRequestManager().getDocumentManager().getTestSuiteInfos();
		if(tsinfos != null && tsinfos.length > 0){
			for(int i=0;i<tsinfos.length;i+= 1){
				try{
					pp.setProgress(i*50/tsinfos.length);
					pp.setMessage("Update SimsRunning, Getting Testsuite "+tsinfos[i].getTSID());
					TestSuiteNew tsn = getRequestManager().getDocumentManager().getTestSuite(tsinfos[i].getTSKey());
					TestCaseNew[] tcnArr = tsn.getTestCases();
					if(tcnArr != null){
						for(int j=0;tcnArr != null && j<tcnArr.length;j+= 1){
							TestCriteriaNew[] tcritArr = tcnArr[j].getTestCriterias();
							if(tcritArr != null){
								for(int k=0;tcritArr != null && k<tcritArr.length;k+= 1){
									if(tcritArr[k].getReportStatus() != null &&
										tcritArr[k].getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_SIMRUNNING) ||
										tcritArr[k].getReportStatus().equals(TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE)){
											runningTCrits.add(tcritArr[k]);
										}
								}
							}
						}
					}
				}catch(Throwable e){
					e.printStackTrace();
					errors.append(e.getClass().getName()+" "+e.getMessage());
				}
			}
			for(int i=0;i<runningTCrits.size();i+= 1){
				try{
					TestCriteriaNew tcn = (TestCriteriaNew)runningTCrits.elementAt(i);
					cbit.vcell.simulation.SimulationInfo simInfo = tcn.getSimInfo();
					pp.setProgress((int)(50+(i*50/runningTCrits.size())));
					pp.setMessage("Update SimsRunning, Setting Status "+simInfo.getName());
					//Check if there is some status different from "running"
					if(simInfo != null){
						SimulationStatus simStatus = getRequestManager().getServerSimulationStatus(simInfo);
						if(simStatus != null){
							if (simStatus.isFailed()){
								updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_SIMFAILED,"Sim msg="+simStatus.getJob0StatusMessage());
							}else if(simStatus.isJob0Completed()){
								updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null);
							}else if(!simStatus.isRunning()){
								updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE,
									"Sim jobstatus "+simStatus.toString()+" "+simStatus.getJob0StatusMessage());
							}
						}else{
							updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE,
								"Can't get sim job status "+(simStatus == null?"jobStatus is null":""));
						}
					}
				}catch(Throwable e){
					e.printStackTrace();
					errors.append(e.getClass().getName()+" "+e.getMessage());		
				}
			}
		}
	}catch(Throwable e){
		e.printStackTrace();
		errors.append(e.getClass().getName()+" "+e.getMessage());		
	}

	if(errors.length() > 0){
		errors.insert(0,"Error updating simstatus\n");
		return errors.toString();
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 6:13:58 AM)
 * @param tcrit cbit.vcell.numericstest.TestCriteriaNew
 * @param status java.lang.String
 * @param statusmessage java.lang.String
 */
private void updateTCritStatus(TestCriteriaNew tcrit, String status, String statusMessage)throws DataAccessException{
	
	//try{
		getRequestManager().getDocumentManager().doTestSuiteOP(
				new EditTestCriteriaOPReportStatus(tcrit.getTCritKey(),status,statusMessage)
			);
	//}catch(Throwable e){
		//e.printStackTrace();
		//return e.getClass().getName()+" "+e.getMessage();
	//}
	//return null;
	
	
}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 1:31:08 PM)
 * @return cbit.vcell.numericstestingframework.TestSuiteInfo
 */
public void updateTestCriteria(TestCriteriaNew origTestCriteria,TestCriteriaNew newTestCriteria)throws DataAccessException{

	EditTestCriteriaOP testCriteriaOP = null;
	if(newTestCriteria instanceof TestCriteriaNewMathModel){
		MathModelInfo regrMMInfo = ((TestCriteriaNewMathModel)newTestCriteria).getRegressionMathModelInfo();
		SimulationInfo regrsimInfo = ((TestCriteriaNewMathModel)newTestCriteria).getRegressionSimInfo();
		testCriteriaOP =
			new EditTestCriteriaOPMathModel(
				origTestCriteria.getTCritKey(),
				(regrMMInfo != null?regrMMInfo.getVersion().getVersionKey():null),
				(regrsimInfo != null?regrsimInfo.getVersion().getVersionKey():null),
				newTestCriteria.getMaxAbsError(),
				newTestCriteria.getMaxRelError()
			);
	}else if(newTestCriteria instanceof TestCriteriaNewBioModel){
		BioModelInfo regrBMInfo = ((TestCriteriaNewBioModel)newTestCriteria).getRegressionBioModelInfo();
		SimulationInfo regrsimInfo = ((TestCriteriaNewBioModel)newTestCriteria).getRegressionSimInfo();
		testCriteriaOP =
			new EditTestCriteriaOPBioModel(
				origTestCriteria.getTCritKey(),
				(regrBMInfo != null?regrBMInfo.getVersion().getVersionKey():null),
				(regrsimInfo != null?regrsimInfo.getVersion().getVersionKey():null),
				newTestCriteria.getMaxAbsError(),
				newTestCriteria.getMaxRelError()
			);
	}
	getRequestManager().getDocumentManager().doTestSuiteOP(testCriteriaOP);
	RemoveTestResultsOP removeTestResults = new RemoveTestResultsOP(new BigDecimal[] {origTestCriteria.getTCritKey()});
	getRequestManager().getDocumentManager().doTestSuiteOP(removeTestResults);
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:52:18 AM)
 * @return boolean
 * @param mathDesc cbit.vcell.math.MathDescription
 * 
 */
public void viewResults(TestCriteriaNew testCriteria) {
	
	VCDataIdentifier vcdID = new VCSimulationDataIdentifier(testCriteria.getSimInfo().getAuthoritativeVCSimulationIdentifier(), 0);

	// get the data manager and wire it up
	try {
		Simulation sim = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(testCriteria.getSimInfo());
		DataManager dataManager = getRequestManager().getDataManager(vcdID, sim.getIsSpatial());
		
		DynamicDataManager dynamicDataMgr = getRequestManager().getDynamicDataManager(sim);
		addDataListener(dynamicDataMgr);
		// make the viewer
		boolean expectODEdata = sim.getMathDescription().getGeometry().getDimension() == 0;
		DataViewer viewer = dynamicDataMgr.createViewer(expectODEdata);
		viewer.setDataViewerManager(this);
		addExportListener(viewer);
		
		// create the simCompareWindow - this is just a lightweight window to display the simResults. 
		// It was created originally to compare 2 sims, it can also be used here instead of creating the mor eheavy-weight SimWindow.
		SimulationCompareWindow simCompareWindow = new SimulationCompareWindow(vcdID, viewer);
		if (simCompareWindow != null) {
			// just show it right now...
			final JInternalFrame existingFrame = simCompareWindow.getFrame();
			DocumentWindowManager.showFrame(existingFrame, getTestingFrameworkWindowPanel().getJDesktopPane1());
			
			//SwingUtilities.invokeLater(new Runnable() {
				//public void run() {
					//DocumentWindowManager.showFrame(existingFrame, desktopPane);
				//}
			//});
		}
	} catch (Throwable e) {
		PopupGenerator.showErrorDialog(e.getMessage());
	}
}


}