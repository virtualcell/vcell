/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;
import org.vcell.client.logicalwindow.LWNamespace;
import org.vcell.sbml.vcell.StructureSizeSolver;
import org.vcell.util.BeanUtils;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.Severity;
import org.vcell.util.IssueContext;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.VCellThreadChecker.SuppressRemote;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.rmi.event.DataJobEvent;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.data.DataViewer;
import cbit.vcell.client.data.DataViewerController;
import cbit.vcell.client.data.MergedDatasetViewerController;
import cbit.vcell.client.data.PDEDataViewer;
import cbit.vcell.client.data.SimulationWorkspaceModelInfo;
import cbit.vcell.client.desktop.TestingFrameworkWindow;
import cbit.vcell.client.desktop.TestingFrameworkWindowPanel;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.client.desktop.testingframework.AddTestSuitePanel;
import cbit.vcell.client.desktop.testingframework.EditTestCriteriaPanel;
import cbit.vcell.client.desktop.testingframework.TestCaseAddPanel;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.client.task.TFGenerateReport;
import cbit.vcell.client.task.TFRefresh;
import cbit.vcell.client.task.TFUpdateRunningStatus;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Constant;
import cbit.vcell.math.FilamentRegionVariable;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.StochVolVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Structure;
import cbit.vcell.numericstest.AddTestCasesOP;
import cbit.vcell.numericstest.AddTestCasesOPBioModel;
import cbit.vcell.numericstest.AddTestCasesOPMathModel;
import cbit.vcell.numericstest.AddTestCriteriaOPBioModel;
import cbit.vcell.numericstest.AddTestCriteriaOPMathModel;
import cbit.vcell.numericstest.AddTestResultsOP;
import cbit.vcell.numericstest.AddTestSuiteOP;
import cbit.vcell.numericstest.ChangeTestCriteriaErrorLimitOP;
import cbit.vcell.numericstest.EditTestCasesOP;
import cbit.vcell.numericstest.EditTestCriteriaOP;
import cbit.vcell.numericstest.EditTestCriteriaOPBioModel;
import cbit.vcell.numericstest.EditTestCriteriaOPMathModel;
import cbit.vcell.numericstest.EditTestCriteriaOPReportStatus;
import cbit.vcell.numericstest.EditTestSuiteOP;
import cbit.vcell.numericstest.LoadTestInfoOP;
import cbit.vcell.numericstest.LoadTestInfoOP.LoadTestOpFlag;
import cbit.vcell.numericstest.LoadTestInfoOpResults;
import cbit.vcell.numericstest.QueryTestCriteriaCrossRefOP;
import cbit.vcell.numericstest.RemoveTestCasesOP;
import cbit.vcell.numericstest.RemoveTestCriteriaOP;
import cbit.vcell.numericstest.RemoveTestResultsOP;
import cbit.vcell.numericstest.RemoveTestSuiteOP;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestCaseNewMathModel;
import cbit.vcell.numericstest.TestCriteriaCrossRefOPResults;
import cbit.vcell.numericstest.TestCriteriaCrossRefOPResults.CrossRefData;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestCriteriaNewBioModel;
import cbit.vcell.numericstest.TestCriteriaNewMathModel;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestSuiteOPResults;
import cbit.vcell.parser.Expression;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataListener;
import cbit.vcell.simdata.DataManager;
import cbit.vcell.simdata.MergedDataInfo;
import cbit.vcell.simdata.ODEDataManager;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.PDEDataContext;
import cbit.vcell.simdata.PDEDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.AnnotatedFunction.FunctionCategory;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.NonspatialStochSimOptions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.SensVariable;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
/**
 * Insert the type's description here.
 * Creation date: (7/15/2004 11:44:12 AM)
 * @author: Anuradha Lakshminarayana
 */
public class TestingFrameworkWindowManager extends TopLevelWindowManager implements DataViewerManager {
	private static Logger lg = Logger.getLogger(TestingFrameworkWindowManager.class);

	public static final int COPY_REGRREF = 0;
	public static final int ASSIGNORIGINAL_REGRREF = 1;
	public static final int ASSIGNNEW_REGRREF = 2;

	public static class NewTestSuiteUserInformation{
		public TestSuiteInfoNew testSuiteInfoNew;
		public int regrRefFlag;
		public NewTestSuiteUserInformation(TestSuiteInfoNew argTestSuiteInfoNew,int argRegrRefFlag){
			testSuiteInfoNew = argTestSuiteInfoNew;
			regrRefFlag = argRegrRefFlag;
		}
	};
	private TestingFrameworkWindowPanel testingFrameworkWindowPanel;
	private EditTestCriteriaPanel editTestCriteriaPanel =
		new EditTestCriteriaPanel();
	private TestCaseAddPanel testCaseAddPanel = new TestCaseAddPanel();
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
public void addDataListener(DataListener newListener) {}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void addNewTestSuiteToTF() throws Exception {

	NewTestSuiteUserInformation newTestSuiteUserInformation = getNewTestSuiteInfoFromUser(null,null);
	if (newTestSuiteUserInformation != null && newTestSuiteUserInformation.testSuiteInfoNew != null) {
		saveNewTestSuiteInfo(newTestSuiteUserInformation.testSuiteInfoNew);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */
public String addTestCases(final TestSuiteInfoNew tsInfo, final TestCaseNew[] testCaseArray,int regrRefFlag,ClientTaskStatusSupport pp){

	if (tsInfo == null) {
		throw new IllegalArgumentException("TestSuiteInfo cannot be null");
	}

	if (testCaseArray == null || testCaseArray.length == 0) {
		throw new IllegalArgumentException("TestCases cannot be null / empty");
	}
	List<TestCaseNew> testCases = new ArrayList<>( Arrays.asList(testCaseArray) ); //make modifiable list

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
	if(testSuite != null && testSuite.getTSInfoNew().isLocked()){
		throw new RuntimeException("Cannot addTestCases to locked table");
	}
	if(testSuite != null){
		//Saving BioModels
		TestCaseNew existingTestCases[] = testSuite.getTestCases();
		java.util.HashMap<KeyValue, BioModel> bioModelHashMap = new java.util.HashMap<KeyValue, BioModel>();
		//if(existingTestCases != null){
		// Find BioModels, Using the same BM reference for sibling Applications
		int pcounter = 0;
		Iterator<TestCaseNew> iter = testCases.iterator(); //use iterator to allow removal of test case from collection if exception
		while(iter.hasNext()) {
			TestCaseNew testCase = iter.next();
			pp.setProgress(Math.max(1,((int)((pcounter++/(double)(testCases.size( )*3))*100))));
			pp.setMessage("Checking "+testCase.getVersion().getName());
			try{
				if (testCase instanceof TestCaseNewBioModel) {
					TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCase;
					//
					// re-save only once for each BioModel/TestSuite combination
					//
					if (bioModelHashMap.get(bioTestCase.getBioModelInfo().getVersion().getVersionKey())==null){
						pp.setMessage("Getting BM "+testCase.getVersion().getName());
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
							pp.setMessage("Saving BM "+testCase.getVersion().getName());

							//
							// some older models have membrane voltage variable names which are not unique
							// (e.g. membranes 'pm' and 'nm' both have membrane voltage variables named 'Voltage_Membrane0')
							//
							// if this is the case, we will try to repair the conflict (for math testing purposes only) by renaming the voltage variables to their default values.
							//
							// Ordinarily, the conflict will be identified as an "Error" issue and the user will be prompted to repair before saving or math generation.
							//
							bioModel.refreshDependencies();
							boolean bFoundIdentifierConflictUponLoading = hasDuplicateIdentifiers(bioModel);
							if (bFoundIdentifierConflictUponLoading){
								//
								// look for two MembraneVoltage instances with same variable name, rename all
								//
								HashSet<String> membraneVoltageVarNames = new HashSet<String>();
								ArrayList<MembraneVoltage> membraneVoltageVars = new ArrayList<MembraneVoltage>();
								for (Structure struct : bioModel.getModel().getStructures()){
									if (struct instanceof Membrane){
										MembraneVoltage membraneVoltage = ((Membrane)struct).getMembraneVoltage();
										if (membraneVoltage != null){
											membraneVoltageVars.add(membraneVoltage);
											membraneVoltageVarNames.add(membraneVoltage.getName());
										}
									}
								}
								if (membraneVoltageVars.size() != membraneVoltageVarNames.size()){
									// rename them all to the default names
									for (MembraneVoltage memVoltage : membraneVoltageVars){
										memVoltage.setName(Membrane.getDefaultMembraneVoltageName(memVoltage.getMembrane().getName()));
									}
								}
							}
							SimulationContext[] simContexts = bioModel.getSimulationContexts();
							for (int j = 0; j < simContexts.length; j++){
								simContexts[j].clearVersion();
								GeometrySurfaceDescription gsd = simContexts[j].getGeometry().getGeometrySurfaceDescription();
								if(gsd != null){
									GeometricRegion[] grArr = gsd.getGeometricRegions();
									if(grArr == null){
										gsd.updateAll();
									}
								}
								MathMapping mathMapping = simContexts[j].createNewMathMapping();

								// for older models that do not have absolute compartment sizes set, but have relative sizes (SVR/VF); or if there is only one compartment with size not set,
								// compute absolute compartment sizes using relative sizes and assuming a default value of '1' for one of the compartments.
								// Otherwise, the math generation will fail, since for the relaxed topology (VCell 5.3 and later) absolute compartment sizes are required.
								GeometryContext gc = simContexts[j].getGeometryContext();
								if (simContexts[j].getGeometry().getDimension() == 0 &&
										((gc.isAllSizeSpecifiedNull() && !gc.isAllVolFracAndSurfVolSpecifiedNull()) || (gc.getModel().getStructures().length == 1 && gc.isAllSizeSpecifiedNull())) ) {
									// choose the first structure in model and set its size to '1'.
									Structure struct = simContexts[j].getModel().getStructure(0);
									double structSize = 1.0;
									StructureSizeSolver.updateAbsoluteStructureSizes(simContexts[j], struct, structSize, struct.getStructureSize().getUnitDefinition());
								}

								simContexts[j].setMathDescription(mathMapping.getMathDescription());
							}
							Simulation[] sims = bioModel.getSimulations();
							String[] simNames = new String[sims.length];
							for (int j = 0; j < sims.length; j++){
								// prevents parent simulation (from the original mathmodel) reference connection
								// Otherwise it will refer to data from previous (parent) simulation.
								sims[j].clearVersion();
								simNames[j] = sims[j].getName();
								//									if(sims[j].getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)){
								//										sims[j].getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
								//									}
							}

							newBioModel = getRequestManager().getDocumentManager().save(bioModel, simNames);
						}

						bioModelHashMap.put(bioTestCase.getBioModelInfo().getVersion().getVersionKey(),newBioModel);
					}
				}
			}catch(Throwable e){
				String identifier =testCase.getVersion() != null?"Name="+testCase.getVersion().getName():"TCKey="+testCase.getTCKey();
				if (lg.isInfoEnabled()) {
					lg.info(identifier,e);
				}
				errors.append("Error collecting BioModel for TestCase "+ identifier + '\n' +
						e.getClass().getName()+" "+e.getMessage()+'\n');
				iter.remove();// remove to avoid further processing attempts
			}
		}
		//}
		// then process each BioModelTestCase individually
		//if(bioModelHashMap != null){
		pcounter = 0;
		for (TestCaseNew testCase : testCases) {
			pp.setProgress(Math.max(1,((int)((pcounter++/(double)(testCases.size( )*3))*100))));
			pp.setMessage("Checking "+testCase.getVersion().getName());
			try{
				AddTestCasesOP testCaseOP = null;
				if (testCase instanceof TestCaseNewBioModel) {
					pp.setMessage("Processing BM "+testCase.getVersion().getName());
					TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCase;

					BioModel newBioModel = (BioModel)bioModelHashMap.get(bioTestCase.getBioModelInfo().getVersion().getVersionKey());
					if(newBioModel == null){
						throw new Exception("BioModel not found");
					}
					SimulationContext simContext = null;
					for (int j = 0; j < newBioModel.getSimulationContexts().length; j++){
						if (newBioModel.getSimulationContext(j).getName().equals(bioTestCase.getSimContextName())){
							simContext = newBioModel.getSimulationContext(j);
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

						KeyValue regressionBioModelKey = null;
						KeyValue regressionBioModelSimKey = null;
						if(bioTestCase.getType().equals(TestCaseNew.REGRESSION)){
							if(regrRefFlag == TestingFrameworkWindowManager.COPY_REGRREF){
								regressionBioModelKey = (tcritOrigForSimName != null && tcritOrigForSimName.getRegressionBioModelInfo() != null?tcritOrigForSimName.getRegressionBioModelInfo().getVersion().getVersionKey():null);
								regressionBioModelSimKey = (tcritOrigForSimName != null && tcritOrigForSimName.getRegressionSimInfo() != null?tcritOrigForSimName.getRegressionSimInfo().getVersion().getVersionKey():null);
							}else if(regrRefFlag == TestingFrameworkWindowManager.ASSIGNORIGINAL_REGRREF){
								regressionBioModelKey = (tcritOrigForSimName != null?bioTestCase.getBioModelInfo().getVersion().getVersionKey():null);
								regressionBioModelSimKey = (tcritOrigForSimName != null?tcritOrigForSimName.getSimInfo().getVersion().getVersionKey():null);
							}else if(regrRefFlag == TestingFrameworkWindowManager.ASSIGNNEW_REGRREF){
								regressionBioModelKey = newBioModel.getVersion().getVersionKey();
								regressionBioModelSimKey = newSimulations[j].getVersion().getVersionKey();
							}else{
								throw new IllegalArgumentException(this.getClass().getName()+".addTestCases(...) BIOMODEL Unknown Regression Operation Flag");
							}
						}
						testCriteriaOPs[j] =
							new AddTestCriteriaOPBioModel(testCase.getTCKey(),
								newSimulations[j].getVersion().getVersionKey(),
								regressionBioModelKey,regressionBioModelSimKey,
								(tcritOrigForSimName != null?tcritOrigForSimName.getMaxAbsError():new Double(1e-16)),
								(tcritOrigForSimName != null?tcritOrigForSimName.getMaxRelError():new Double(1e-9)),
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
							(testCase.getVersion() != null?"Name="+testCase.getVersion().getName():"TCKey="+testCase.getTCKey())+"\n"+
							e.getClass().getName()+" "+e.getMessage()+"\n");
			}
		}
		//}

		// Process MathModels
		pcounter = 0;
		for (TestCaseNew testCase : testCases) {
			pp.setProgress(Math.max(1,((int)((pcounter++/(double)(testCases.size( )*3))*100))));
			pp.setMessage("Checking "+testCase.getVersion().getName());
			try{
				AddTestCasesOP testCaseOP = null;
				if (testCase instanceof TestCaseNewMathModel) {
					TestCaseNewMathModel mathTestCase = (TestCaseNewMathModel)testCase;
					pp.setMessage("Getting MathModel "+testCase.getVersion().getName());
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
//						if(sims[j].getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)){
//							sims[j].getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolumeStandalone);
//						}
					}

					pp.setMessage("Saving MathModel "+testCase.getVersion().getName());
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

						KeyValue regressionMathModelKey = null;
						KeyValue regressionMathModelSimKey = null;
						if(mathTestCase.getType().equals(TestCaseNew.REGRESSION)){
							if(regrRefFlag == TestingFrameworkWindowManager.COPY_REGRREF){
								regressionMathModelKey = (tcritOrigForSimName != null && tcritOrigForSimName.getRegressionMathModelInfo() != null?tcritOrigForSimName.getRegressionMathModelInfo().getVersion().getVersionKey():null);
								regressionMathModelSimKey = (tcritOrigForSimName != null && tcritOrigForSimName.getRegressionSimInfo() != null?tcritOrigForSimName.getRegressionSimInfo().getVersion().getVersionKey():null);
							}else if(regrRefFlag == TestingFrameworkWindowManager.ASSIGNORIGINAL_REGRREF){
								regressionMathModelKey = (tcritOrigForSimName != null?mathTestCase.getMathModelInfo().getVersion().getVersionKey():null);
								regressionMathModelSimKey = (tcritOrigForSimName != null?tcritOrigForSimName.getSimInfo().getVersion().getVersionKey():null);
							}else if(regrRefFlag == TestingFrameworkWindowManager.ASSIGNNEW_REGRREF){
								regressionMathModelKey = newMathModel.getVersion().getVersionKey();
								regressionMathModelSimKey = newSimulations[j].getVersion().getVersionKey();
							}else{
								throw new IllegalArgumentException(this.getClass().getName()+".addTestCases(...) MATHMODEL Unknown Regression Operation Flag");
							}
						}
						testCriteriaOPs[j] =
						new AddTestCriteriaOPMathModel(
							testCase.getTCKey(),
							newSimulations[j].getVersion().getVersionKey(),
							regressionMathModelKey,regressionMathModelSimKey,
							(tcritOrigForSimName != null?tcritOrigForSimName.getMaxAbsError():new Double(1e-16)),
							(tcritOrigForSimName != null?tcritOrigForSimName.getMaxRelError():new Double(1e-9)),
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
								(testCase.getVersion() != null?"Name="+testCase.getVersion().getName():"TCKey="+testCase.getTCKey())+"\n"+
								e.getClass().getName()+" "+e.getMessage()+"\n");
			}
		}
	}

	if(errors.length() > 0){
		return errors.toString();
	}
	return null;

}


private boolean hasDuplicateIdentifiers(BioModel bioModel) {
	ArrayList<Issue> issueList = new ArrayList<Issue>();
	IssueContext issueContext = new IssueContext();
	bioModel.gatherIssues(issueContext, issueList);
	boolean bFoundIdentifierConflictUponLoading = false;
	for (Issue issue : issueList){
		if (issue.getCategory() == IssueCategory.Identifiers && issue.getSource() == bioModel.getModel() && issue.getSeverity() == Severity.ERROR){
			bFoundIdentifierConflictUponLoading = true;
			break;
		}
	}
	return bFoundIdentifierConflictUponLoading;
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
public void compare(final TestCriteriaNew testCriteria,final SimulationInfo userDefinedRegrSimInfo){
	final String KEY_MERGEDDATAINFO	 = "KEY_MERGEDDATAINFO";
	final String KEY_MERGEDDATASETVIEWERCNTRLR = "KEY_MERGEDDATASETVIEWERCNTRLR";
	AsynchClientTask gatherDataTask = new AsynchClientTask("Gathering compare Dta...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// create the merged data for the simulationInfo in testCriteria and the regression simInfo
			SimulationInfo simInfo = testCriteria.getSimInfo();
			SimulationInfo regrSimInfo = null;
			if(userDefinedRegrSimInfo != null){
				regrSimInfo = userDefinedRegrSimInfo;
			}else{
				regrSimInfo = testCriteria.getRegressionSimInfo();
			}

			if (regrSimInfo == null) {
				return;
			}

			VCDataIdentifier vcSimId1 = new VCSimulationDataIdentifier(simInfo.getAuthoritativeVCSimulationIdentifier(), 0);
			VCDataIdentifier vcSimId2 = new VCSimulationDataIdentifier(regrSimInfo.getAuthoritativeVCSimulationIdentifier(), 0);
			User user = simInfo.getOwner();
			VCDataIdentifier[] vcIdentifierArray = new VCDataIdentifier[] {vcSimId2,vcSimId1};
			MergedDataInfo mergedDataInfo = new MergedDataInfo(user, vcIdentifierArray, MergedDataInfo.createDefaultPrefixNames(vcIdentifierArray.length));
			hashTable.put(KEY_MERGEDDATAINFO, mergedDataInfo);
			// get the data manager and wire it up
			//
			// get all "Data1.XXX" data identifiers ... and remove those which are functions
			// add functions of the form DIFF_XXX = (Data1.XXX - Data2.XXX) for convenience in comparing results.
			//
			Simulation sim1 = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(simInfo);
			Simulation sim2 = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(regrSimInfo);
			boolean isSpatial = sim1.isSpatial();
			if (sim2.isSpatial() != isSpatial) {
				throw new RuntimeException("Cannot compare spatial and non-spatial data sets : " + simInfo + "& " + regrSimInfo);
			}
			DataManager mergedDataManager = getRequestManager().getDataManager(null,mergedDataInfo, isSpatial);
			DataManager data1Manager = getRequestManager().getDataManager(null,vcSimId1, isSpatial);
			DataManager data2Manager = getRequestManager().getDataManager(null,vcSimId2, isSpatial);

			Vector<AnnotatedFunction> functionList = new Vector<AnnotatedFunction>();
			AnnotatedFunction data1Functions[] = data1Manager.getFunctions();
			AnnotatedFunction existingFunctions[] = mergedDataManager.getFunctions();
			DataIdentifier data1Identifiers[] = data1Manager.getDataIdentifiers();
			DataIdentifier data2Identifiers[] = data2Manager.getDataIdentifiers();
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
				VariableType varType = data1Identifiers[i].getVariableType();
				Expression exp = new Expression(data1Name+"-"+data2Name);
				AnnotatedFunction newFunction = new AnnotatedFunction(functionName,exp,data1Identifiers[i].getDomain(),"",varType,FunctionCategory.OUTPUTFUNCTION);

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

			OutputContext outputContext = null;
			if (functionList.size()>0){
				AnnotatedFunction[] newDiffFunctions = (AnnotatedFunction[])BeanUtils.getArray(functionList,AnnotatedFunction.class);
				outputContext = new OutputContext(newDiffFunctions);
			}
			MergedDatasetViewerController mergedDatasetViewerCtr = getRequestManager().getMergedDatasetViewerController(outputContext,mergedDataInfo, !isSpatial);
			hashTable.put(KEY_MERGEDDATASETVIEWERCNTRLR, mergedDatasetViewerCtr);
		}
	};
	AsynchClientTask showResultsTask = new AsynchClientTask("Showing Compare Results",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			// make the viewer
			MergedDatasetViewerController mergedDatasetViewerCtr = (MergedDatasetViewerController)hashTable.get(KEY_MERGEDDATASETVIEWERCNTRLR);
			addDataListener(mergedDatasetViewerCtr);
			DataViewer viewer = mergedDatasetViewerCtr.createViewer();
			viewer.setDataViewerManager(TestingFrameworkWindowManager.this);
			addExportListener(viewer);

			VCDataIdentifier vcDataIdentifier = (MergedDataInfo)hashTable.get(KEY_MERGEDDATAINFO);
			ChildWindowManager childWindowManager =TFWFinder.findChildWindowManager(getComponent());
			ChildWindow childWindow = childWindowManager.addChildWindow(viewer, vcDataIdentifier, "Comparing ... "+vcDataIdentifier.getID());
			childWindow.pack();
//			childWindow.setSize(450, 450);
			childWindow.setIsCenteredOnParent();
			childWindow.show();
		}
	};
	ClientTaskDispatcher.dispatch(getComponent(), new Hashtable<String, Object>(), new AsynchClientTask[] {gatherDataTask,showResultsTask}, false);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2006 4:13:02 PM)
 * @param event cbit.rmi.event.ExportEvent
 */
public void dataJobMessage(DataJobEvent event) {

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
		int regrRefFlag,
		ClientTaskStatusSupport pp) throws DataAccessException{

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
			newTestSuiteInfo.getTSNumericsBuild(),null,newTestSuiteInfo.getTSAnnotation());

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
		 return addTestCases(tsin,newTestCases,regrRefFlag,pp);
	}else{
		return null;
	}
}


public void updateTestCaseAnnotation(TestCaseNew testCase,String newAnnotation) throws DataAccessException{
	EditTestCasesOP etcop =
		new EditTestCasesOP(new BigDecimal[] {testCase.getTCKey()},new String[] {newAnnotation});
	getRequestManager().getDocumentManager().doTestSuiteOP(etcop);

}

public void updateTestSuiteAnnotation(TestSuiteInfoNew tsInfoNew,String newAnnotation) throws DataAccessException{
	EditTestSuiteOP etsop =
		new EditTestSuiteOP(new BigDecimal[] {tsInfoNew.getTSKey()},new String[] {newAnnotation});
	getRequestManager().getDocumentManager().doTestSuiteOP(etsop);

}

public void lockTestSuite(TestSuiteInfoNew tsInfoNew) throws DataAccessException{
	EditTestSuiteOP etsop =
		new EditTestSuiteOP(new BigDecimal[] {tsInfoNew.getTSKey()},true);
	getRequestManager().getDocumentManager().doTestSuiteOP(etsop);

}
private void updateReports(final Hashtable<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>> genReportHash){
	new Thread(
	new Runnable() {
		public void run() {
			Set<java.util.Map.Entry<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>>> tsInfoEntry = genReportHash
					.entrySet();
			Iterator<java.util.Map.Entry<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>>> tsInfoIter = tsInfoEntry
					.iterator();
			while (tsInfoIter.hasNext()) {
				try {
					Entry<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>> entry = tsInfoIter.next();
					TestSuiteInfoNew tsInfo = entry.getKey();
					Vector<TestCriteriaCrossRefOPResults.CrossRefData> xrefDataV = entry.getValue();
					//
					Vector<AsynchClientTask> tasksVLocal = new java.util.Vector<AsynchClientTask>();
					tasksVLocal.add(new TFUpdateRunningStatus(TestingFrameworkWindowManager.this, tsInfo));
					TestSuiteNew tsNew = getTestingFrameworkWindowPanel().getDocumentManager().getTestSuite(tsInfo.getTSKey());
					for (int i = 0; i < xrefDataV.size(); i++) {
						CrossRefData crossRefData = xrefDataV.elementAt(i);
						boolean bDone = false;
						for (int j = 0; j < tsNew.getTestCases().length; j++) {
							TestCaseNew testCaseNew = tsNew.getTestCases()[j];
							if (testCaseNew.getTCKey().equals(crossRefData.tcaseKey)) {
								for (int k = 0; k < testCaseNew.getTestCriterias().length; k++) {
									TestCriteriaNew testCriteria = testCaseNew.getTestCriterias()[k];
									if (testCriteria.getTCritKey().equals(crossRefData.tcritKey)) {
										tasksVLocal.add(new TFGenerateReport(TestingFrameworkWindowManager.this,
														testCaseNew, testCriteria,null));
										bDone = true;
										break;
									}
								}
							}
							if (bDone) {
								break;
							}
						}
					}
					final String END_NOTIFIER = "END NOTIFIER";
					tasksVLocal.add(new AsynchClientTask(END_NOTIFIER, AsynchClientTask.TASKTYPE_NONSWING_BLOCKING, false, false) {
						public void run(Hashtable<String, Object> hashTable) throws Exception {
							hashTable.put(END_NOTIFIER, END_NOTIFIER);
						}

					});
					tasksVLocal.add(new TFRefresh(
							TestingFrameworkWindowManager.this, tsInfo));

					AsynchClientTask[] tasksArr = new AsynchClientTask[tasksVLocal
							.size()];
					tasksVLocal.copyInto(tasksArr);
					java.util.Hashtable<String, Object> hashLocal = new java.util.Hashtable<String, Object>();
					ClientTaskDispatcher.dispatch(
							getTestingFrameworkWindowPanel(), hashLocal,
							tasksArr, true);
					//Wait for each report to complete before going on to next because report methods are not thread safe?
					while (!hashLocal.contains(END_NOTIFIER)) {
						Thread.sleep(100);
					}
				} catch (Exception e) {
					PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Error updating reports\n"
							+ e.getMessage());
					return;
				}
			}
		}
	}).start();
}

public void toggleTestCaseSteadyState(TestCaseNew[] testCases) throws DataAccessException{
	BigDecimal[] testCaseKeys = new BigDecimal[testCases.length];
	boolean[] isSteadyState = new boolean[testCases.length];
	for (int i = 0; i < testCaseKeys.length; i++) {
		testCaseKeys[i] = testCases[i].getTCKey();
		String type = testCases[i].getType();
		if(type.equals(TestCaseNew.EXACT) || type.equals(TestCaseNew.EXACT_STEADY)){
			isSteadyState[i] = (type.equals(TestCaseNew.EXACT)?true:false);
		}else{
			throw new IllegalArgumentException("TestCase "+testCases[i].getVersion().getName()+" MUST be EXACT type");
		}
	}

	EditTestCasesOP etcop = new EditTestCasesOP(testCaseKeys,isSteadyState);
	getRequestManager().getDocumentManager().doTestSuiteOP(etcop);
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

public SimulationInfo getUserSelectedRefSimInfo(RequestManager currentRequstManager,VCDocumentInfo vcDocInfo) throws Exception{
	final int MODELTYPE_INDEX = 0;
	final int MODELKEY_INDEX = 2;
	final int SIMNAME_INDEX = 5;
	final String[] ROWDATACOLNAMES = new String[] {"Type","Model","Model Key","Owner","App","Sim","Date"};
	Vector<Object[]> simInfoV = new Vector<Object[]>();
//	BioModelMetaData[] bmMetaDataArr = getRequestManager().getDocumentManager().getSessionManager().getUserMetaDbServer().getBioModelMetaDatas(true);
//	bmMetaDataArr[0].
	BioModelInfo[] bioModelInfoArr = null;
	if(vcDocInfo == null){
		bioModelInfoArr = currentRequstManager.getDocumentManager().getBioModelInfos();
	}else if(vcDocInfo instanceof BioModelInfo){
		bioModelInfoArr = new BioModelInfo[] {(BioModelInfo)vcDocInfo};
	}
	for (int i = 0; bioModelInfoArr != null && i < bioModelInfoArr.length; i++) {
		if(bioModelInfoArr[i].getBioModelChildSummary() != null){
			String[] bmSimContextNamesArr = bioModelInfoArr[i].getBioModelChildSummary().getSimulationContextNames();
			for (int j = 0; bmSimContextNamesArr != null && j < bmSimContextNamesArr.length; j++) {
				String[] bmSimNamesArr = bioModelInfoArr[i].getBioModelChildSummary().getSimulationNames(bmSimContextNamesArr[j]);
				for (int k = 0; bmSimNamesArr != null && k < bmSimNamesArr.length; k++) {
//					System.out.println("BM "+
//							bioModelInfoArr[i].getVersion().getOwner().getName()+" "+
//							bioModelInfoArr[i].getVersion().getName()+
//							" app="+bmSimContextNamesArr[j]+
//							" sim="+bmSimNamesArr[k]);
					simInfoV.add(new Object[] {"BM",
							bioModelInfoArr[i].getVersion().getName(),
							bioModelInfoArr[i].getVersion().getVersionKey(),
							bioModelInfoArr[i].getVersion().getOwner().getName(),
							bmSimContextNamesArr[j],
							bmSimNamesArr[k],
							bioModelInfoArr[i].getVersion().getDate()}
					);
				}
			}
		}
	}
	MathModelInfo[] mathModelInfoArr =null;
	if(vcDocInfo == null){
		mathModelInfoArr = currentRequstManager.getDocumentManager().getMathModelInfos();
	}else if(vcDocInfo instanceof MathModelInfo){
		mathModelInfoArr = new MathModelInfo[] {(MathModelInfo)vcDocInfo};
	}
	for (int i = 0; mathModelInfoArr != null && i < mathModelInfoArr.length; i++) {
		if(mathModelInfoArr[i].getMathModelChildSummary() != null){
			String[] mathSimNamesArr = mathModelInfoArr[i].getMathModelChildSummary().getSimulationNames();
			for (int j = 0; mathSimNamesArr != null && j < mathSimNamesArr.length; j++) {
//				System.out.println("MM "+
//						mathModelInfoArr[i].getVersion().getOwner().getName()+" "+
//						mathModelInfoArr[i].getVersion().getName()+
//						" sim="+mathSimNamesArr[j]);
				simInfoV.add(new Object[] {"MM",
						mathModelInfoArr[i].getVersion().getName(),
						mathModelInfoArr[i].getVersion().getVersionKey(),
						mathModelInfoArr[i].getVersion().getOwner().getName(),
						null,
						mathSimNamesArr[j],
						mathModelInfoArr[i].getVersion().getDate()}
				);

			}
		}
	}
	Object[][] rowData = new Object[simInfoV.size()][ROWDATACOLNAMES.length];
	simInfoV.copyInto(rowData);
		int[] simSelection = DialogUtils.showComponentOKCancelTableList(
			getComponent(), "Choose Ref Simulation",
			ROWDATACOLNAMES,
			rowData, ListSelectionModel.SINGLE_SELECTION);
	if(simSelection == null || simSelection.length == 0){
		throw UserCancelException.CANCEL_GENERIC;
	}
	Simulation[] simArr = null;
	if(rowData[simSelection[0]][MODELTYPE_INDEX].equals("BM")){
		BioModel bm = currentRequstManager.getDocumentManager().getBioModel((KeyValue)rowData[simSelection[0]][MODELKEY_INDEX]);
		simArr = bm.getSimulations();
	}else{
		MathModel mm = currentRequstManager.getDocumentManager().getMathModel((KeyValue)rowData[simSelection[0]][MODELKEY_INDEX]);
		simArr = mm.getSimulations();
	}
	for (int i = 0; simArr != null && i < simArr.length; i++) {
		if(simArr[i].getName().equals(rowData[simSelection[0]][SIMNAME_INDEX])){
			return simArr[i].getSimulationInfo();
		}
	}
	throw new Exception("Couldn't find selected simulation");
}

/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 5:36:47 PM)
 */
public String generateTestCaseReport(TestCaseNew testCase,TestCriteriaNew onlyThisTCrit,ClientTaskStatusSupport pp,TFGenerateReport.VCDocumentAndSimInfo userDefinedRefSimInfo) {

	StringBuffer reportTCBuffer = new StringBuffer();
	if (testCase == null) {
		reportTCBuffer.append("\n\tTEST CASE :\tERROR: Test Case is NULL\n");
	}else{

		pp.setMessage(testCase.getVersion().getName()+" "+testCase.getType()+" Getting Simulations");
		// Get the Simulations
		Simulation[] sims = null;
		reportTCBuffer.append("\n\tTEST CASE : "+(testCase.getVersion() != null?testCase.getVersion().getName():"Null")+"\n\tAnnotation : "+testCase.getAnnotation()+"\n");
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
				SimulationContext[] simContextArr = bioModel.getSimulationContexts();
				if(simContextArr != null && simContextArr.length > 0){
					SimulationContext simContext = null;
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
				reportTCBuffer.append("\tNote "+"Num sims="+sims.length+" does not match testCriteria length="+
							(testCase.getTestCriterias() != null?testCase.getTestCriterias().length+"":"null")+
					" for TestCase "+(testCase.getVersion() != null?"name="+testCase.getVersion().getName():"key="+testCase.getTCKey())+"\n");
			}

			//Sort
			if(sims.length > 0){
				java.util.Arrays.sort(sims,
					new java.util.Comparator<Simulation> (){
							public int compare(Simulation si1,Simulation si2){
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
					pp.setMessage((testCase instanceof TestCaseNewMathModel?"(MM)":"(BM)")+" "+
						(onlyThisTCrit == null?"sim "+(k+1)+" of "+sims.length:"sim="+onlyThisTCrit.getSimInfo().getName())+"  "+testCase.getVersion().getName()+" "+testCase.getType());
					reportTCBuffer.append(generateTestCriteriaReport(testCase,testCriteria,sims[k],userDefinedRefSimInfo));
				}
			}
		}catch(UserCancelException e){
			throw e;
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


private PDEDataViewer.DataInfoProvider getDataInfoProvider(VCDocument document,PDEDataContext pdeDataContext,String refSimName) throws ObjectNotFoundException{

	SimulationWorkspaceModelInfo simulationWorkspaceModelInfo = null;
	if(document instanceof MathModel){
		MathModel mathModel = (MathModel)document;
		Simulation[] sims = mathModel.getSimulations();
		for (int i = 0; i < sims.length; i++) {
			if(refSimName.equals(sims[i].getName())){
				simulationWorkspaceModelInfo =
					new SimulationWorkspaceModelInfo(mathModel,sims[i].getName());
				break;
			}
		}
	}else{
		BioModel bioModel = (BioModel)document;
		Simulation[] sims = bioModel.getSimulations();
		for (int i = 0; i < sims.length; i++) {
			if(refSimName.equals(sims[i].getName())){
				simulationWorkspaceModelInfo =
					new SimulationWorkspaceModelInfo(bioModel.getSimulationContext(sims[i]),sims[i].getName());
				break;
			}
		}
	}
	PDEDataViewer.DataInfoProvider dataInfoProvider =
		new PDEDataViewer.DataInfoProvider(pdeDataContext, simulationWorkspaceModelInfo);
	return dataInfoProvider;
}
/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 5:36:47 PM)
 *
 */
private String generateTestCriteriaReport(TestCaseNew testCase,TestCriteriaNew testCriteria,Simulation testSim,TFGenerateReport.VCDocumentAndSimInfo userSelectedRefSimInfo/*,VCDocument refDoc,VCDocument testDocument*/) {

	if (testSim.getScanCount() != 1) {
		throw new RuntimeException("paramater scan is not supported in Math Testing Framework");
	}
	SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(testSim, 0);

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

	try {
		VCDocument testDoc = null;
		if(testCase instanceof TestCaseNewMathModel){
			MathModelInfo mmInfo = ((TestCaseNewMathModel)testCase).getMathModelInfo();
			MathModel mathModel = getRequestManager().getDocumentManager().getMathModel(mmInfo);
			testDoc = mathModel;
		}else if(testCase instanceof TestCaseNewBioModel){
			TestCaseNewBioModel bioTestCase = (TestCaseNewBioModel)testCase;
			//bioTestCase.
			BioModelInfo bmInfo = bioTestCase.getBioModelInfo();
			BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bmInfo);
			testDoc = bioModel;
		}

		TFGenerateReport.VCDocumentAndSimInfo refVCDocumentAndSimInfo = null;
		if(userSelectedRefSimInfo == null){
			SimulationInfo refSimInfo = testCriteria.getRegressionSimInfo();
			if(refSimInfo != null){
				VCDocument refDoc = null;
				if(testCriteria instanceof TestCriteriaNewMathModel){
					MathModelInfo mmInfo = ((TestCriteriaNewMathModel)testCriteria).getRegressionMathModelInfo();
					MathModel mathModel = getRequestManager().getDocumentManager().getMathModel(mmInfo);
					refDoc = mathModel;
				}else if(testCriteria instanceof TestCriteriaNewBioModel){
					BioModelInfo bmInfo = ((TestCriteriaNewBioModel)testCriteria).getRegressionBioModelInfo();
					BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bmInfo);
					refDoc = bioModel;
				}
				refVCDocumentAndSimInfo = new TFGenerateReport.VCDocumentAndSimInfo(refSimInfo, refDoc);
			}
			reportTCBuffer.append("\t\t"+testSim.getName() + (refVCDocumentAndSimInfo != null?" (Using TestCrit RegrRefSim)":"")+" : "+"\n");
		}else{
			refVCDocumentAndSimInfo = userSelectedRefSimInfo;
			reportTCBuffer.append("\t\t"+testSim.getName() + " (Using UserDefined RegrRefSim '"+userSelectedRefSimInfo.getSimInfo().getAuthoritativeVCSimulationIdentifier()+"') : "+"\n");
		}
		if (testCase.getType().equals(TestCaseNew.REGRESSION) && refVCDocumentAndSimInfo == null) {
			reportTCBuffer.append("\t\t\tNo reference SimInfo, SimInfoKey="+testCriteria.getSimInfo().getVersion().getName()+". Cannot perform Regression Test!\n");
			simReportStatus = TestCriteriaNew.TCRIT_STATUS_NOREFREGR;
		}else{
			VCDataIdentifier vcdID = new VCSimulationDataIdentifier(testSim.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), 0);
			DataManager simDataManager = getRequestManager().getDataManager(null,vcdID, testSim.isSpatial());

			double timeArray[] = null;
			// can be histogram, so there won't be time array
			try {
				timeArray = simDataManager.getDataSetTimes();
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}
			NonspatialStochSimOptions stochOpt = testSim.getSolverTaskDescription().getStochOpt();
			if ((stochOpt == null || stochOpt.getNumOfTrials() == 1)  && (timeArray == null || timeArray.length == 0)) {
				reportTCBuffer.append("\t\t\tNO DATA : Simulation not run yet.\n");
				simReportStatus = TestCriteriaNew.TCRIT_STATUS_NODATA;
			} else {
				// SPATIAL simulation
				if (testSim.getMathDescription().isSpatial()){
					PDEDataManager pdeDataManager = (PDEDataManager)simDataManager;
					// Get EXACT solution if test case type is EXACT, Compare with numerical
					if (testCase.getType().equals(TestCaseNew.EXACT) || testCase.getType().equals(TestCaseNew.EXACT_STEADY)) {
						SimulationComparisonSummary simCompSummary = MathTestingUtilities.comparePDEResultsWithExact(simSymbolTable, pdeDataManager,testCase.getType(),testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());
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
							if (!BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					// Get CONSTRUCTED solution if test case type is CONSTRUCTED, Compare with numerical
					} else if (testCase.getType().equals(TestCaseNew.CONSTRUCTED)) {
						SimulationComparisonSummary simCompSummary = MathTestingUtilities.comparePDEResultsWithExact(simSymbolTable, pdeDataManager,testCase.getType(),testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());
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
							if (!BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					} else if (testCase.getType().equals(TestCaseNew.REGRESSION)) {
						Simulation refSim = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(refVCDocumentAndSimInfo.getSimInfo());
						VCDataIdentifier refVcdID = new VCSimulationDataIdentifier(refVCDocumentAndSimInfo.getSimInfo().getAuthoritativeVCSimulationIdentifier(), 0);
						PDEDataManager refDataManager = (PDEDataManager)getRequestManager().getDataManager(null,refVcdID, refSim.isSpatial());

						if (refSim.getScanCount() != 1) {
							throw new RuntimeException("paramater scan is not supported in Math Testing Framework");
						}
						SimulationSymbolTable refSimSymbolTable = new SimulationSymbolTable(refSim, 0);

						String varsToCompare[] = getVariableNamesToCompare(simSymbolTable,refSimSymbolTable);
						SimulationComparisonSummary simCompSummary =
							MathTestingUtilities.comparePDEResults(simSymbolTable, pdeDataManager, refSimSymbolTable, refDataManager, varsToCompare,testCriteria.getMaxAbsError(),testCriteria.getMaxRelError(),
									refVCDocumentAndSimInfo.getVCDocument(),getDataInfoProvider(refVCDocumentAndSimInfo.getVCDocument(), refDataManager.getPDEDataContext(), refSim.getName()),
									testDoc,getDataInfoProvider(testDoc, pdeDataManager.getPDEDataContext(), testSim.getName()));
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
							if (!BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					}
				}else{  // NON-SPATIAL CASE
					ODEDataManager odeDataManager = (ODEDataManager)simDataManager;
					ODESolverResultSet numericalResultSet = odeDataManager.getODESolverResultSet();
					// Get EXACT result set if test case type is EXACT, Compare with numerical
					if (testCase.getType().equals(TestCaseNew.EXACT) || testCase.getType().equals(TestCaseNew.EXACT_STEADY)) {
						ODESolverResultSet exactResultSet = MathTestingUtilities.getExactResultSet(testSim.getMathDescription(), timeArray, testSim.getSolverTaskDescription().getSensitivityParameter());
						String varsToCompare[] = getVariableNamesToCompare(simSymbolTable,simSymbolTable);
						SimulationComparisonSummary simCompSummary_exact = MathTestingUtilities.compareResultSets(numericalResultSet,exactResultSet,varsToCompare,testCase.getType(),testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());

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
							if (!BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					// Get CONSTRUCTED result set if test case type is CONSTRUCTED , compare with numerical
					} else if (testCase.getType().equals(TestCaseNew.CONSTRUCTED)) {
						ODESolverResultSet constructedResultSet = MathTestingUtilities.getConstructedResultSet(testSim.getMathDescription(), timeArray);
						String varsToCompare[] = getVariableNamesToCompare(simSymbolTable,simSymbolTable);
						SimulationComparisonSummary simCompSummary_constr = MathTestingUtilities.compareResultSets(numericalResultSet,constructedResultSet,varsToCompare,testCase.getType(),testCriteria.getMaxAbsError(),testCriteria.getMaxRelError());

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
							if (!BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
								reportTCBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
							}
						}
					} else if (testCase.getType().equals(TestCaseNew.REGRESSION)) {
						Simulation refSim = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(testCriteria.getRegressionSimInfo());
						if (refSim.getScanCount() != 1) {
							throw new RuntimeException("paramater scan is not supported in Math Testing Framework");
						}
						SimulationSymbolTable refSimSymbolTable = new SimulationSymbolTable(refSim, 0);

						String varsToTest[] = getVariableNamesToCompare(simSymbolTable,refSimSymbolTable);

						VCDataIdentifier refVcdID = new VCSimulationDataIdentifier(refVCDocumentAndSimInfo.getSimInfo().getAuthoritativeVCSimulationIdentifier(), 0);
						ODEDataManager refDataManager = (ODEDataManager)getRequestManager().getDataManager(null,refVcdID, refSim.isSpatial());
						ODESolverResultSet referenceResultSet = refDataManager.getODESolverResultSet();
						SimulationComparisonSummary simCompSummary_regr = null;
						int interpolationOrder = 1;
						SolverTaskDescription solverTaskDescription = refSim.getSolverTaskDescription();
						if (solverTaskDescription.getOutputTimeSpec().isDefault() && ((DefaultOutputTimeSpec)solverTaskDescription.getOutputTimeSpec()).getKeepEvery() == 1) {
							SolverDescription solverDescription = solverTaskDescription.getSolverDescription();
							if ((!solverDescription.supportsAll(SolverDescription.DiscontinutiesFeatures)) || !refSim.getMathDescription().hasDiscontinuities()) {
								interpolationOrder = solverDescription.getTimeOrder();
							}
						}
						simCompSummary_regr = MathTestingUtilities.compareUnEqualResultSets(numericalResultSet, referenceResultSet,varsToTest,testCriteria.getMaxAbsError(),testCriteria.getMaxRelError(), interpolationOrder);
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
							if (!BeanUtils.arrayContains(failVarSummaries, allVarSummaries[m])) {
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

	if(userSelectedRefSimInfo == null){
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
			try{
				getRequestManager().getDocumentManager().doTestSuiteOP(
					new EditTestCriteriaOPReportStatus(testCriteria.getTCritKey(),TestCriteriaNew.TCRIT_STATUS_RPERROR,e.getClass().getName()+" "+e.getMessage())
					);
			}catch(Throwable e2){
				//Nothing more can be done
			}
		}
	}

	//}

	return reportTCBuffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (8/18/2003 5:36:47 PM)
 */
public String generateTestSuiteReport(TestSuiteInfoNew testSuiteInfo, ClientTaskStatusSupport pp) {

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
			sb.append(generateTestCaseReport(testCases[j],null,pp,null));
		}

	}catch(UserCancelException e){
		throw e;
	}catch(Throwable e){
		e.printStackTrace();
		sb.append("ERROR "+e.getClass().getName()+" mesg="+e.getMessage());
	}
	return sb.toString();
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
public java.awt.Component getComponent() {
	return getTestingFrameworkWindowPanel();
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
public TestCaseNew[] getNewTestCaseArr() throws UserCancelException{
	// invoke the testCaseAddPanel to define testCaseInfo parameters.
	// This is where we invoke the TestCaseAddPanel to define a testCase for the test suite ...
	getTestCaseAddPanel().resetTextFields();
	while(true){
		Object choice = showAddTestCaseDialog(getTestCaseAddPanel(), getComponent());

		if (choice != null && choice.equals("OK")) {
			try{
				return getTestCaseAddPanel().getNewTestCaseArr();
			}catch(Exception e){
				PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Error getting New TestCase:\n"+e.getMessage(), e);
				continue;
			}
		}
		throw UserCancelException.CANCEL_GENERIC;
	}
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

	while(true){
		// display the editCriteriaPanel.
		Object choice = showEditTestCriteriaDialog(getEditTestCriteriaPanel(), getComponent());

		if (choice != null && choice.equals("OK")) {
			TestCriteriaNew tcritNew = getEditTestCriteriaPanel().getNewTestCriteria();
			if(tcritNew instanceof TestCriteriaNewMathModel){
				TestCriteriaNewMathModel tcritNewMM = (TestCriteriaNewMathModel)tcritNew;
				if((tcritNewMM.getRegressionMathModelInfo() == null && tcritNewMM.getRegressionSimInfo() != null)
						||
					(tcritNewMM.getRegressionMathModelInfo() != null && tcritNewMM.getRegressionSimInfo() == null)){
					PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Must specify both Reference MathModel and Simulation");
					continue;
				}
			}else if(tcritNew instanceof TestCriteriaNewBioModel){
				TestCriteriaNewBioModel tcritNewBM = (TestCriteriaNewBioModel)tcritNew;
				if((tcritNewBM.getRegressionBioModelInfo() == null && tcritNewBM.getRegressionSimInfo() != null)
						||
					(tcritNewBM.getRegressionBioModelInfo() != null && tcritNewBM.getRegressionSimInfo() == null)){
					PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Must specify both Reference BioModel App and Simulation");
					continue;
				}
			}else{

			}
			return tcritNew;
		}

		throw UserCancelException.CANCEL_GENERIC;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public NewTestSuiteUserInformation getNewTestSuiteInfoFromUser(String tsAnnotation,String duplicateTestSuiteName) throws Exception{

	getAddTestSuitePanel().resetTextFields(tsAnnotation,duplicateTestSuiteName != null);
	while(true){
		Object choice = showAddTestSuiteDialog(getAddTestSuitePanel(), getComponent(), duplicateTestSuiteName);

		if (choice != null && choice.equals("OK")) {
			return getAddTestSuitePanel().getTestSuiteInfo();
		}
		throw UserCancelException.CANCEL_DB_SELECTION;
	}

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
		SimulationContext simContexts[] = bioModel.getSimulationContexts();
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
public TestingFrameworkWindowPanel getTestingFrameworkWindowPanel() {
	return testingFrameworkWindowPanel;
}


public static class VariablePair{
	public Variable refVariable;
	public Variable testVariable;
	public Domain domain;
}
///**
// * Insert the method's description here.
// * Creation date: (11/23/2004 1:53:11 PM)
// * @return java.lang.String[]
// * @param sim1 cbit.vcell.solver.Simulation
// * @param sim2 cbit.vcell.solver.Simulation
// */
//private VariablePair[] getVariableNamesToCompare(BioModel testBioModel,SimulationSymbolTable testSymboltable, SimulationSymbolTable refSymbolTable){
//	Vector<VariablePair> variablePairs = new Vector<VariablePair>();
//
//	//
//	// get Variables from Simulation 1
//	//
//	Variable refVars[] = refSymbolTable.getVariables();
//	for (int i = 0;refVars!=null && i < refVars.length; i++){
//		if (refVars[i] instanceof VolVariable ||
//			refVars[i] instanceof StochVolVariable ||
//			refVars[i] instanceof MemVariable ||
//			refVars[i] instanceof VolumeRegionVariable ||
//			refVars[i] instanceof MembraneRegionVariable ||
//			refVars[i] instanceof FilamentVariable ||
//			refVars[i] instanceof FilamentRegionVariable){
//
//			VariablePair varPair = new VariablePair();
//			varPair.refVariable = refVars[i];
//			varPair.domain = refVars[i].getDomain();
//			varPair.testVariable = null;
//			variablePairs.add(varPair);
//		}
////		Constant sensitivityParameter = simSymbolTable1.getSimulation().getSolverTaskDescription().getSensitivityParameter();
////		if (sensitivityParameter != null) {
////			if (simVars[i] instanceof VolVariable) {
////				hashSet.add(SensVariable.getSensName((VolVariable)simVars[i], sensitivityParameter));
////			}
////		}
//	}
//
//	//
//	// add Variables from Simulation 2
//	//
//	Variable[] testVars = testSymboltable.getVariables();
//	for (int i = 0;testVars!=null && i < testVars.length; i++){
//		if (testVars[i] instanceof VolVariable ||
//			testVars[i] instanceof MemVariable ||
//			testVars[i] instanceof VolumeRegionVariable ||
//			testVars[i] instanceof MembraneRegionVariable ||
//			testVars[i] instanceof FilamentVariable ||
//			testVars[i] instanceof FilamentRegionVariable){
//
//			if(testVars[i].getDomain() == null){
//				boolean BFoundMatchingName = false;
//				for (int j = 0; j < variablePairs.size(); j++) {
//					VariablePair varPair = variablePairs.elementAt(j);
//					if(varPair.refVariable.getName().equals(testVars[i].getName())){
//						varPair.testVariable = testVars[i];
//						BFoundMatchingName = true;
//						break;
//					}
//				}
//				if(!BFoundMatchingName){
//					//Try to find other matching variable types (e.g. functions)
//					Variable dataSet1Match = refSymbolTable.getVariable(testVars[i].getName());
//					if(dataSet1Match != null){
//						VariablePair varPair = new VariablePair();
//						varPair.refVariable = dataSet1Match;
//						varPair.testVariable = testVars[i];
//						varPair.domain = varPair.refVariable.getDomain();
//						variablePairs.add(varPair);
//					}else{
//						SpeciesContext testSpeciescontext0 = testBioModel.getModel().getSpeciesContext(testVars[i].getName());
//						if(testSpeciescontext0 != null){
//							Species refspecies = testSpeciescontext0.getSpecies();
//							Variable refVariable = refSymbolTable.getVariable(refspecies.getCommonName());
//							if(refVariable != null){
//								VariablePair varPair = new VariablePair();
//								varPair.refVariable = refVariable;
//								varPair.testVariable = testVars[i];
//								varPair.domain = varPair.refVariable.getDomain();
//								variablePairs.add(varPair);
//							}
//						}else{
//							Species testSpecies = testBioModel.getModel().getSpecies(testVars[i].getName());
//							if(testSpecies != null){
//								for (int j = 0; j < testBioModel.getModel().getSpeciesContexts().length; j++) {
//									if(testBioModel.getModel().getSpeciesContexts()[j].getSpecies() == testSpecies){
//										Variable refVar = refSymbolTable.getVariable(testBioModel.getModel().getSpeciesContexts()[j].getName());
//										if(refVar != null){
//											VariablePair varPair = new VariablePair();
//											varPair.refVariable = refVar;
//											varPair.testVariable = testVars[i];
//											varPair.domain = varPair.refVariable.getDomain();
//											variablePairs.add(varPair);
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}else{
//
//			}
//			hashSet.add(simVars[i].getName());
//		}
//		Constant sensitivityParameter = refSymbolTable.getSimulation().getSolverTaskDescription().getSensitivityParameter();
//		if (sensitivityParameter != null) {
//			if (simVars[i] instanceof VolVariable) {
//				hashSet.add(SensVariable.getSensName((VolVariable)simVars[i], sensitivityParameter));
//			}
//		}
//	}
//
//	return (String[])hashSet.toArray(new String[hashSet.size()]);
//}

/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 1:53:11 PM)
 * @return java.lang.String[]
 * @param sim1 cbit.vcell.solver.Simulation
 * @param sim2 cbit.vcell.solver.Simulation
 */
private String[] getVariableNamesToCompare(SimulationSymbolTable simSymbolTable1, SimulationSymbolTable simSymbolTable2) {
	java.util.HashSet<String> hashSet = new java.util.HashSet<String>();

	//
	// get Variables from Simulation 1
	//
	Variable simVars[] = simSymbolTable1.getVariables();
	for (int i = 0;simVars!=null && i < simVars.length; i++){
		if (simVars[i] instanceof VolVariable ||
			simVars[i] instanceof StochVolVariable ||
			simVars[i] instanceof MemVariable ||
			simVars[i] instanceof VolumeRegionVariable ||
			simVars[i] instanceof MembraneRegionVariable ||
			simVars[i] instanceof FilamentVariable ||
			simVars[i] instanceof FilamentRegionVariable){

			hashSet.add(simVars[i].getName());
		}
		Constant sensitivityParameter = simSymbolTable1.getSimulation().getSolverTaskDescription().getSensitivityParameter();
		if (sensitivityParameter != null) {
			if (simVars[i] instanceof VolVariable) {
				hashSet.add(SensVariable.getSensName((VolVariable)simVars[i], sensitivityParameter));
			}
		}
	}

	//
	// add Variables from Simulation 2
	//
	simVars = simSymbolTable2.getVariables();
	for (int i = 0;simVars!=null && i < simVars.length; i++){
		if (simVars[i] instanceof VolVariable ||
			simVars[i] instanceof MemVariable ||
			simVars[i] instanceof VolumeRegionVariable ||
			simVars[i] instanceof MembraneRegionVariable ||
			simVars[i] instanceof FilamentVariable ||
			simVars[i] instanceof FilamentRegionVariable){

			hashSet.add(simVars[i].getName());
		}
		Constant sensitivityParameter = simSymbolTable2.getSimulation().getSolverTaskDescription().getSensitivityParameter();
		if (sensitivityParameter != null) {
			if (simVars[i] instanceof VolVariable) {
				hashSet.add(SensVariable.getSensName((VolVariable)simVars[i], sensitivityParameter));
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
	AsynchClientTask task1 = new AsynchClientTask("initializeAllPanels", AsynchClientTask.TASKTYPE_SWING_NONBLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			try {
				DocumentManager documentManager = getRequestManager().getDocumentManager();
				getTestingFrameworkWindowPanel().setDocumentManager(documentManager);
				getAddTestSuitePanel().setTestingFrameworkWindowManager(TestingFrameworkWindowManager.this);
				getTestCaseAddPanel().setTestingFrameworkWindowManager(TestingFrameworkWindowManager.this);
				getEditTestCriteriaPanel().setTestingFrameworkWindowManager(TestingFrameworkWindowManager.this);
			} catch (Throwable exc) {
				exc.printStackTrace(System.out);
			}
		}
	};
	ClientTaskDispatcher.dispatch(null, new Hashtable<String, Object>(), new AsynchClientTask[] {task1});
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
public void loadModel(VCDocumentInfo vcDocumentInfo) throws DataAccessException{

	VCDocumentInfo vcDocInfo = null;
	if (vcDocumentInfo instanceof MathModelInfo) {
		vcDocInfo = getRequestManager().getDocumentManager().getMathModelInfo(vcDocumentInfo.getVersion().getVersionKey());
	} else if (vcDocumentInfo instanceof BioModelInfo) {
		vcDocInfo = getRequestManager().getDocumentManager().getBioModelInfo(vcDocumentInfo.getVersion().getVersionKey());
	}
	getRequestManager().openDocument(vcDocInfo, this, true);
}


@SuppressWarnings("serial")
public void queryTCritCrossRef(final TestSuiteInfoNew tsin,final TestCriteriaNew tcrit,final String varName){

	try {
		QueryTestCriteriaCrossRefOP queryTestCriteriaCrossRefOP =
			new QueryTestCriteriaCrossRefOP(tsin.getTSKey(),tcrit.getTCritKey(),varName);
		TestCriteriaCrossRefOPResults testCriteriaCrossRefOPResults =
			(TestCriteriaCrossRefOPResults)getRequestManager().getDocumentManager().doTestSuiteOP(queryTestCriteriaCrossRefOP);

		final Vector<TestCriteriaCrossRefOPResults.CrossRefData> xrefDataV = testCriteriaCrossRefOPResults.getCrossRefData();
		final TestSuiteInfoNew[] testSuiteInfos = getRequestManager().getDocumentManager().getTestSuiteInfos();
		Vector<TestSuiteInfoNew> missingTestSuites = new Vector<TestSuiteInfoNew>();
		for (int i = 0; i < testSuiteInfos.length; i++) {
			boolean bFound = false;
			for (int j = 0; j < xrefDataV.size(); j++) {
				if(xrefDataV.elementAt(j).tsVersion.equals(testSuiteInfos[i].getTSID())){
					bFound = true;
					break;
				}
			}
			if(!bFound){
				missingTestSuites.add(testSuiteInfos[i]);
			}
		}
		TestCriteriaCrossRefOPResults.CrossRefData xrefDataSource = null;
		for (int i = 0; i < xrefDataV.size(); i++) {
			if(xrefDataV.elementAt(i).tcritKey.equals(tcrit.getTCritKey())){
				xrefDataSource = xrefDataV.elementAt(i);
				break;
			}
		}
		if(xrefDataSource == null){
			throw new RuntimeException("Couldn't find source Test Criteria in query results.");
		}
		final int numColumns = 8;
		final int XREFDATA_ALLOWANCE = 1;
		final int TSKEY_ALLOWANCE = 1;
		final int XREFDATA_OFFSET = numColumns;
		final int TSDATE_OFFSET = 1;
		final int VARNAME_OFFSET = 3;
		final int TSKEYMISSING_OFFSET = numColumns+1;
		final String[] colNames = new String[numColumns];
		final Object[][] sourceRows = new Object[xrefDataV.size()+missingTestSuites.size()][numColumns+XREFDATA_ALLOWANCE+TSKEY_ALLOWANCE];
		String sourceTestSuite = null;
		colNames[0] = "tsVersion";
		colNames[1] = "tsDate";
		colNames[2] = "tsBaseVersion";
		colNames[3] = "varName";
		colNames[4] = "RelErorr";
		colNames[5] = "limitRelErorr";
		colNames[6] = "limitAbsErorr";
		colNames[7] = "AbsErorr";

		for (int i = 0; i < xrefDataV.size(); i++) {
			sourceRows[i][colNames.length] = xrefDataV.elementAt(i);
			if(xrefDataV.elementAt(i).tcritKey.equals(queryTestCriteriaCrossRefOP.getTestCriterium())){
				sourceTestSuite = xrefDataV.elementAt(i).tsVersion;
			}
			sourceRows[i][0] = xrefDataV.elementAt(i).tsVersion;
			sourceRows[i][2] =
				(xrefDataV.elementAt(i).tsRefVersion == null?
						(xrefDataV.elementAt(i).regressionModelID == null/* && xrefDataV.elementAt(i).regressionMMref==null*/?"":"Ref Model exist BUT outside of TestSuites")
						:xrefDataV.elementAt(i).tsRefVersion);
			sourceRows[i][6] = xrefDataV.elementAt(i).maxAbsErorr;
			sourceRows[i][5] = xrefDataV.elementAt(i).maxRelErorr;
			if(xrefDataV.elementAt(i).varName != null){
				sourceRows[i][VARNAME_OFFSET] = xrefDataV.elementAt(i).varName;
				sourceRows[i][4] = xrefDataV.elementAt(i).varCompSummary.getRelativeError();
				sourceRows[i][7] = xrefDataV.elementAt(i).varCompSummary.getAbsoluteError();
			}else{
				sourceRows[i][VARNAME_OFFSET] = "-No Report-";
				sourceRows[i][4] = null;//"No Report";
				sourceRows[i][7] = null;//"No Report";
			}
			for (int j = 0; j < testSuiteInfos.length; j++) {
				if(xrefDataV.elementAt(i).tsVersion.equals(testSuiteInfos[j].getTSID())){
					sourceRows[i][1] = testSuiteInfos[j].getTSDate();
					break;
				}
			}
		}

		for (int i = xrefDataV.size(); i < sourceRows.length; i++) {
			sourceRows[i][0] = missingTestSuites.elementAt(i-xrefDataV.size()).getTSID();
			sourceRows[i][TSDATE_OFFSET] = missingTestSuites.elementAt(i-xrefDataV.size()).getTSDate();
			sourceRows[i][TSKEYMISSING_OFFSET] = missingTestSuites.elementAt(i-xrefDataV.size()).getTSKey();
		}

//		Arrays.sort(rows,
//				new Comparator<Object[]>(){
//					public int compare(Object[] o1, Object[] o2) {
//						return ((String)o1[0]).compareToIgnoreCase((String)o2[0]);
////						if(o1[0].equals(o2[0])){
////							return o1[3].compareToIgnoreCase(o2[3]);
////						}
////						return o1[0].compareToIgnoreCase(o2[0]);
//					}
//				}
//			);

		final VCellSortTableModel<Object[]> tableModel = new VCellSortTableModel<Object[]>(colNames){
			public Class<?> getColumnClass(int columnIndex) {
				if(columnIndex==TSDATE_OFFSET){
					return Date.class;
				}else if(columnIndex >=4 && columnIndex<= 7){
					return Double.class;
				}
				return String.class;
			}
			public boolean isCellEditable(int row, int column) {
		        return false;
		    }
			public Object getValueAt(int rowIndex, int columnIndex) {
				return getValueAt(rowIndex)[columnIndex];
			}
			public Comparator<Object[]> getComparator(final int col, final boolean ascending) {
				return new Comparator<Object[]>(){
						public int compare(Object[] o1, Object[] o2) {
							if(o1[col] == null && o2[col] == null){
								return 0;
							}
//								if(ascending){
								if(o1[col] == null){
									return 1;
								}
								if(o2[col] == null){
									return -1;
								}
//								}else{
//									if(o1[col] == null){
//										return -1;
//									}
//									if(o2[col] == null){
//										return 1;
//									}
//								}
							if(getColumnClass(col).equals(String.class)){
								if(ascending){
									return ((String)o1[col]).compareToIgnoreCase(((String)o2[col]));
								}else{
									return ((String)o2[col]).compareToIgnoreCase(((String)o1[col]));
								}
							}else if(getColumnClass(col).equals(Date.class)){
								if(ascending){
									return ((Date)o1[col]).compareTo(((Date)o2[col]));
								}
								return ((Date)o2[col]).compareTo(((Date)o1[col]));
							}else if(getColumnClass(col).equals(Double.class)){
								if(ascending){
									return ((Double)o1[col]).compareTo(((Double)o2[col]));
								}
								return ((Double)o2[col]).compareTo(((Double)o1[col]));

							}
							throw new RuntimeException("TestSuite XRef Query unexpecte column class "+getColumnClass(col).getName());
						}
					};
			};

		};
		tableModel.setData(Arrays.asList(sourceRows));

		//Create table
		final JSortTable table = new JSortTable();
		table.setModel(tableModel);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		final JScrollPane scrollPaneContentPane = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(500, 250));

		table.getColumnModel().getColumn(TSDATE_OFFSET).setCellRenderer(
				new DefaultTableCellRenderer(){
//					DateFormat formatter = DateFormat.getDateTimeInstance();
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						return super.getTableCellRendererComponent(table,(value == null?null:((Date)value).toString())/*formatter.format((Date)value)*/, isSelected, hasFocus, row, column);
					}
				}
			);
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(){
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				return super.getTableCellRendererComponent(table,(value == null?null:((Double)value).toString())/*formatter.format((Date)value)*/, isSelected, hasFocus, row, column);
			}
		};
		table.getColumnModel().getColumn(4).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(5).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(6).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(7).setCellRenderer(dtcr);
//		table.getColumnModel().getColumn(4).setCellRenderer(
//				new DefaultTableCellRenderer(){
//					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//						return super.getTableCellRendererComponent(table,(value == null?null:((Double)value).toString())/*formatter.format((Date)value)*/, isSelected, hasFocus, row, column);
//					}
//				}
//			);

//		table.getTableHeader().setReorderingAllowed(false);

		//Popup Menu
		final TestCriteriaCrossRefOPResults.CrossRefData xrefDataSourceFinal = xrefDataSource;
		final JPopupMenu queryPopupMenu = new JPopupMenu();
		final JMenuItem changeLimitsMenuItem = new JMenuItem("Change Selected Error Limits...");
		final String OPEN_MODEL = "Open Model(s)";
		final JMenuItem openModelMenuItem = new JMenuItem(OPEN_MODEL);
		final String OPEN_REGRREFMODEL = "Open Regr Ref Model(s)";
		final JMenuItem openRegrRefModelMenuItem = new JMenuItem(OPEN_REGRREFMODEL);
		final String SELECT_REF_IN_TREE = "Select in Tree View";
		final JMenuItem showInTreeMenuItem = new JMenuItem(SELECT_REF_IN_TREE);
		final String SELECT_REGR_REF_IN_TREE = "Select RegrRef TCase in Tree View";
		final JMenuItem showRegrRefInTreeMenuItem = new JMenuItem(SELECT_REGR_REF_IN_TREE);

		queryPopupMenu.add(changeLimitsMenuItem);
		queryPopupMenu.add(openModelMenuItem);
		queryPopupMenu.add(openRegrRefModelMenuItem);
		queryPopupMenu.add(showInTreeMenuItem);
		queryPopupMenu.add(showRegrRefInTreeMenuItem);

		ActionListener showInTreeActionListener =
			new ActionListener(){
				public void actionPerformed(ActionEvent actionEvent) {
					int[] selectedRows = table.getSelectedRows();
					if(selectedRows == null || selectedRows.length != 1){
						PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Action "+actionEvent.getActionCommand()+" accepts only single selection!");
						return;
					}
					TestCriteriaCrossRefOPResults.CrossRefData xrefData =
						(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(selectedRows[0], XREFDATA_OFFSET);
					BigDecimal missingTSKey = (BigDecimal)tableModel.getValueAt(selectedRows[0], TSKEYMISSING_OFFSET);
					if(actionEvent.getActionCommand().equals(SELECT_REF_IN_TREE)){
						getTestingFrameworkWindowPanel().selectInTreeView((xrefData != null?xrefData.tsKey:missingTSKey),(xrefData != null?xrefData.tcaseKey:null),(xrefData != null?xrefData.tcritKey:null));
					}else if(actionEvent.getActionCommand().equals(SELECT_REGR_REF_IN_TREE)){
						if(xrefData == null){
							PopupGenerator.showErrorDialog(getComponent(), "No Regression Reference info available.");
							return;
						}
						getTestingFrameworkWindowPanel().selectInTreeView((xrefData != null?xrefData.regressionModelTSuiteID:null),(xrefData != null?xrefData.regressionModelTCaseID:null),(xrefData != null?xrefData.regressionModelTCritID:null));
					}
					ChildWindow childWindow =TFWFinder.findChildWindowManager(getComponent()).getChildWindowFromContentPane(scrollPaneContentPane);
					if (childWindow!=null){
						childWindow.show();
					}
				}
			};
		showInTreeMenuItem.addActionListener(showInTreeActionListener);
		showRegrRefInTreeMenuItem.addActionListener(showInTreeActionListener);

		ActionListener openModelsActionListener =
		new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent) {
				int[] selectedRows = table.getSelectedRows();
				String failureS = "";
				TestCriteriaCrossRefOPResults.CrossRefData xrefData = null;
				int openCount = 0;
				for (int i = 0; i < selectedRows.length; i++) {
					try {
						xrefData =
							(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(selectedRows[i], XREFDATA_OFFSET);
						if(xrefData != null && (actionEvent.getActionCommand().equals(OPEN_REGRREFMODEL)?xrefData.regressionModelID != null:true)){
							openCount+= 1;
							VCDocumentInfo vcDocInfo = null;
							if(xrefData.isBioModel){
								vcDocInfo = getRequestManager().getDocumentManager().getBioModelInfo(new KeyValue((actionEvent.getActionCommand().equals(OPEN_REGRREFMODEL)?xrefData.regressionModelID:xrefData.modelID)));
							}else{
								vcDocInfo = getRequestManager().getDocumentManager().getMathModelInfo(new KeyValue((actionEvent.getActionCommand().equals(OPEN_REGRREFMODEL)?xrefData.regressionModelID:xrefData.modelID)));
							}
							getRequestManager().openDocument(vcDocInfo, TestingFrameworkWindowManager.this, true);
						}
					} catch (Exception e) {
						failureS+= failureS+"key="+xrefData.modelID+" "+e.getMessage()+"\n";
						e.printStackTrace();
					}
				}
				if(failureS.length() > 0 || openCount == 0){
					PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Failed to open some models\n"+failureS+(openCount == 0?"Selection(s) had no model(s)":""));
				}
				ChildWindow childWindow =TFWFinder.findChildWindowManager(getComponent()).getChildWindowFromContentPane(scrollPaneContentPane);
				if (childWindow!=null){
					childWindow.show();
				}
			}
		};
		openModelMenuItem.addActionListener(openModelsActionListener);
		openRegrRefModelMenuItem.addActionListener(openModelsActionListener);

		changeLimitsMenuItem.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent actionEvent) {
					int[] selectedRows = table.getSelectedRows();
					Vector<TestCriteriaCrossRefOPResults.CrossRefData> changeTCritV = new Vector<TestCriteriaCrossRefOPResults.CrossRefData>();
					for (int i = 0; i < selectedRows.length; i++) {
						TestCriteriaCrossRefOPResults.CrossRefData xrefData =
							(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(selectedRows[i], XREFDATA_OFFSET);
						if(xrefData != null){
							boolean bFound = false;
							for (int j = 0; j < changeTCritV.size(); j++) {
								if(changeTCritV.elementAt(j).tcritKey.equals(xrefData.tcritKey)){
									bFound = true;
									break;
								}
							}
							if(!bFound){
								changeTCritV.add(xrefData);
							}
						}
					}
					if(changeTCritV.size() > 0){
						Double relativeErrorLimit = null;
						Double absoluteErrorLimit = null;
						while(true){
							try{
								String ret = PopupGenerator.showInputDialog(getComponent(),
										"Enter new TestCriteria Error Limits for '"+xrefDataSourceFinal.simName+"'.  '-'(dash) to keep original value.",
										"RelativeErrorLimit,AbsoluteErrorLimit");
								int commaPosition = ret.indexOf(',');
								if(commaPosition == -1){
									throw new Exception("No comma found separating RelativeErrorLimit AbsoluteErrorLimit");
								}
								if(commaPosition != ret.lastIndexOf(',')){
									throw new Exception("Only 1 comma allowed separating RelativeErrorLimit and AbsoluteErrorLimit");
								}
								final String KEEP_ORIGINAL_VALUE = "-";
								String relativeErrorS = ret.substring(0, commaPosition);
								String absoluteErrorS = ret.substring(commaPosition+1,ret.length());
								if(!relativeErrorS.equals(KEEP_ORIGINAL_VALUE)){
									relativeErrorLimit = Double.parseDouble(relativeErrorS);
								}
								if(!absoluteErrorS.equals(KEEP_ORIGINAL_VALUE)){
									absoluteErrorLimit = Double.parseDouble(absoluteErrorS);
								}
								if((relativeErrorLimit != null && relativeErrorLimit <= 0) || (absoluteErrorLimit != null && absoluteErrorLimit <= 0)){
									throw new Exception("Error limits must be greater than 0");
								}
								break;
							}catch(UserCancelException e){
								ChildWindow childWindow =TFWFinder.findChildWindowManager(getComponent()).getChildWindowFromContentPane(scrollPaneContentPane);
								if (childWindow!=null){
									childWindow.show();
								}
								return;
							}catch(Exception e){
								PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Error parsing Error Limits\n"+e.getMessage());
							}
						}
						double[] relErrorLimitArr = new double[changeTCritV.size()];
						double[] absErrorLimitArr = new double[changeTCritV.size()];
						Object[][] rows = new Object[changeTCritV.size()][5];
						for (int j = 0; j < changeTCritV.size(); j++) {
							relErrorLimitArr[j] = (relativeErrorLimit != null?relativeErrorLimit.doubleValue():changeTCritV.elementAt(j).maxRelErorr);
							absErrorLimitArr[j] = (absoluteErrorLimit != null?absoluteErrorLimit.doubleValue():changeTCritV.elementAt(j).maxAbsErorr);
							rows[j][2] = new Double(relErrorLimitArr[j]);
							rows[j][4] = new Double(absErrorLimitArr[j]);
							rows[j][1] = new Double(changeTCritV.elementAt(j).maxRelErorr);
							rows[j][3] = new Double(changeTCritV.elementAt(j).maxAbsErorr);
							rows[j][0] = changeTCritV.elementAt(j).tsVersion;
						}
						try{
							PopupGenerator.showComponentOKCancelTableList(
								getComponent(), "Confirm Error Limit Changes",
								new String[] {"TSVersion","Orig RelErrorLimit","New RelErrorLimit","Orig AbsErrorLimit","New AbsErrorLimit"},
								rows,
								null);
						}catch(UserCancelException e){
							ChildWindow childWindow =TFWFinder.findChildWindowManager(getComponent()).getChildWindowFromContentPane(scrollPaneContentPane);
							if (childWindow!=null){
								childWindow.show();
							}
							return;
						}

						//Get information needed to generate new TestCriteria Reports
						final String YES_ANSWER = "Yes";
						Hashtable<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>> genReportHash = null;
						String genRepResult = PopupGenerator.showWarningDialog(getComponent(), "Generate Reports for changed Test Criterias?", new String[] {YES_ANSWER,"No"}, YES_ANSWER);
						if(genRepResult != null && genRepResult.equals(YES_ANSWER)){
							genReportHash = new Hashtable<TestSuiteInfoNew, Vector<TestCriteriaCrossRefOPResults.CrossRefData>>();
							for (int i = 0; i < changeTCritV.size(); i++) {
								boolean bFound = false;
								for (int j = 0; j < testSuiteInfos.length; j++) {
									if(changeTCritV.elementAt(i).tsVersion.equals(testSuiteInfos[j].getTSID())){
										bFound = true;
										Vector<TestCriteriaCrossRefOPResults.CrossRefData> tempV = genReportHash.get(testSuiteInfos[j]);
										if(tempV == null){
											tempV = new Vector<TestCriteriaCrossRefOPResults.CrossRefData>();
											genReportHash.put(testSuiteInfos[j],tempV);
										}
										tempV.add(changeTCritV.elementAt(i));
									}
								}
								if(!bFound){
									PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Couldn't find testsuiteinfo for testcriteria");
									return;
								}
							}
						}




						BigDecimal[] changeTCritBDArr = new BigDecimal[changeTCritV.size()];
						for (int i = 0; i < changeTCritV.size(); i++) {
							changeTCritBDArr[i] = changeTCritV.elementAt(i).tcritKey;
						}
						ChangeTestCriteriaErrorLimitOP changeTestCriteriaErrorLimitOP =
							new ChangeTestCriteriaErrorLimitOP(changeTCritBDArr,absErrorLimitArr,relErrorLimitArr);
						try{
							getTestingFrameworkWindowPanel().getDocumentManager().doTestSuiteOP(changeTestCriteriaErrorLimitOP);
						}catch(Exception e){
							PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Failed Changing Error limits for selected "+xrefDataSourceFinal.simName+"\n"+e.getMessage());
							return;
						}
						ChildWindow childWindow =TFWFinder.findChildWindowManager(getComponent()).getChildWindowFromContentPane(scrollPaneContentPane);
						if (childWindow!=null){
							childWindow.close();
						}
						getTestingFrameworkWindowPanel().refreshTree((TestSuiteInfoNew)null);
						if(genReportHash != null){
							updateReports(genReportHash);
						}else{
							new Thread(new Runnable(){
								public void run() {
									TestingFrameworkWindowManager.this.queryTCritCrossRef(tsin, tcrit, varName);
								}
							}).start();
						}
					}else{
						PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "No selected rows contain Test Criteria.");
					}
				}
			}
		);
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				checkPopup(e);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				checkPopup(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				checkPopup(e);
			}
			private void checkPopup(MouseEvent mouseEvent){
				if(mouseEvent.isPopupTrigger()){
//Not use because popupmenu will not show at edge
//					if(table.getSelectedRowCount() <= 1){
//						table.getSelectionModel().setSelectionInterval(table.rowAtPoint(mouseEvent.getPoint()),table.rowAtPoint(mouseEvent.getPoint()));
//					}
					doPopup(mouseEvent);
				}
				else{
					queryPopupMenu.setVisible(false);
				}
			}
			private void doPopup(MouseEvent mouseEvent){
//				int selectedRow = table.getSelectedRow();
//				TestCriteriaCrossRefOPResults.CrossRefData xrefData =
//					(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(selectedRow, numColumns);
//				queryPopupMenu.add(changeLimitsMenuItem);
//				queryPopupMenu.add(openModelMenuItem);
//				queryPopupMenu.add(openRegrRefModelMenuItem);
//				queryPopupMenu.add(showInTreeMenuItem);
				if(table.getSelectedRowCount() == 0){
					changeLimitsMenuItem.setEnabled(false);
					openModelMenuItem.setEnabled(false);
					openRegrRefModelMenuItem.setEnabled(false);
					showInTreeMenuItem.setEnabled(false);
					showRegrRefInTreeMenuItem.setEnabled(false);
				}else{
					changeLimitsMenuItem.setEnabled(true);
					openModelMenuItem.setEnabled(true);
					openRegrRefModelMenuItem.setEnabled(true);
					showInTreeMenuItem.setEnabled(true);
					if(table.getSelectedRowCount() == 1){
						TestCriteriaCrossRefOPResults.CrossRefData xrefData =
							(TestCriteriaCrossRefOPResults.CrossRefData)tableModel.getValueAt(table.getSelectedRow(), numColumns);
						showRegrRefInTreeMenuItem.setEnabled(xrefData != null && xrefData.regressionModelID != null && xrefData.tsRefVersion != null);
					}
				}
				queryPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
			}
		});

		String title = (xrefDataSource.isBioModel?"BM":"MM")+
				" "+xrefDataSource.tcSolutionType+
				" ("+sourceTestSuite+") "+
				" \""+(xrefDataSource.isBioModel?xrefDataSource.bmName:xrefDataSource.mmName)+
				"\"  ::  "+(xrefDataSource.isBioModel?"app=\""+xrefDataSource.bmAppName+"\"  ::  sim=\""+xrefDataSource.simName+"\"":"sim=\""+xrefDataSource.simName+"\"");

		ChildWindow childWindow =TFWFinder.findChildWindowManager(getComponent()).addChildWindow(scrollPaneContentPane,scrollPaneContentPane,title);
//		childWindow.setSize(600,400);
		childWindow.setIsCenteredOnParent();
		childWindow.pack();
		childWindow.setResizable(true);
		childWindow.show();

	} catch (DataAccessException e) {
		e.printStackTrace();
		PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Error Query TestCriteria Cross Ref:\n"+e.getMessage());
	}

}

/**
 * Remove a cbit.vcell.desktop.controls.DataListener.
 */
public void removeDataListener(DataListener newListener) {}


/**
 * Insert the method's description here.
 * Creation date: (4/9/2003 1:31:08 PM)
 * @return cbit.vcell.numericstestingframework.TestSuiteInfo
 */
public void removeTestCase(TestCaseNew testCase) throws DataAccessException{

	getRequestManager().getDocumentManager().doTestSuiteOP(
			new RemoveTestCasesOP(new BigDecimal[] {testCase.getTCKey()}));
}

public void refreshLoadTest(LoadTestInfoOpResults loadTestInfoOpResults){
	getTestingFrameworkWindowPanel().refreshTree(loadTestInfoOpResults);
}
public LoadTestInfoOpResults getLoadTestInfoBetweenDates(Date beginDate,Date endDate) throws DataAccessException{
	LoadTestInfoOP loadTestInfoOP = new LoadTestInfoOP(beginDate,endDate);
	TestSuiteOPResults testSuiteOPResults = getRequestManager().getDocumentManager().doTestSuiteOP(loadTestInfoOP);
	if(testSuiteOPResults instanceof LoadTestInfoOpResults){
		LoadTestInfoOpResults loadTestInfoOpResults =
			(LoadTestInfoOpResults)testSuiteOPResults;
		return loadTestInfoOpResults;
	}
	throw new IllegalArgumentException("getLoadTestDetails Expecting LoadTestInfoOpResults");
}
public LoadTestInfoOpResults getLoadTestDetails(Integer slowLoadThreshold,String loadTestSQLCondition) throws DataAccessException{
	LoadTestInfoOP loadTestInfoOP = new LoadTestInfoOP(LoadTestOpFlag.info,slowLoadThreshold,loadTestSQLCondition);
	TestSuiteOPResults testSuiteOPResults = getRequestManager().getDocumentManager().doTestSuiteOP(loadTestInfoOP);
	if(testSuiteOPResults instanceof LoadTestInfoOpResults){
		LoadTestInfoOpResults loadTestInfoOpResults =
			(LoadTestInfoOpResults)testSuiteOPResults;
		return loadTestInfoOpResults;
	}
	throw new IllegalArgumentException("getLoadTestDetails Expecting LoadTestInfoOpResults");
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/2003 11:27:32 AM)
 * @param testCase cbit.vcell.numericstestingframework.TestCase
 */

public void removeTestSuite(TestSuiteInfoNew tsInfo) throws DataAccessException{

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
			null,newTestSuiteInfo.getTSAnnotation());
	getRequestManager().getDocumentManager().doTestSuiteOP(testSuiteOP);

	getTestingFrameworkWindowPanel().refreshTree(newTestSuiteInfo);
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
		PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Selected Reference BioModel is null, choose a reference BioModel before choosing simulation!");
		return null;
	}

	// code for obtaining siminfos from Biomodel and displaying it as a list
	// and displaying the siminfo in the label

	SimulationContext simContext = null;
	BioModel bioModel = getRequestManager().getDocumentManager().getBioModel(bmInfo);
	for(int i=0;i<bioModel.getSimulationContexts().length;i+= 1){
		if(bioModel.getSimulationContexts()[i].getName().equals(appName)){
			simContext = bioModel.getSimulationContexts()[i];
			break;
		}
	}
	if(simContext != null){
		SimulationInfo simInfo = selectSimInfoPrivate(bioModel.getSimulations(simContext));
		return new Object[] {simContext.getName(),simInfo};
	}else{
		PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "No simcontext found for biomodel "+bmInfo+" app="+appName);
		return null;
	}

}


/**
 * Insert the method's description here.
 * Creation date: (7/15/2004 4:48:36 PM)
 * @param newTestingFrameworkWindowPanel cbit.vcell.client.desktop.TestingFrameworkWindowPanel
 */
public SimulationInfo selectRefSimInfo(MathModelInfo mmInfo) {
	if (mmInfo == null) {
		PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "Selected Reference MathModel is null, choose a reference MathModel before choosing simulation!");
		return null;
	}

	// code for obtaining siminfos from mathmodel and displaying it as a list
	// and displaying the siminfo in the label

	// Get MathModel from MathModelInfo
	MathModel mathModel = null;
	try {
		 mathModel = getRequestManager().getDocumentManager().getMathModel(mmInfo);
	} catch (DataAccessException e) {
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
			new java.util.Comparator<Simulation> (){
					public int compare(Simulation si1,Simulation si2){
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
		PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, "No such SimInfo Exists : "+selectedRefSimInfoName);
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
private void setTestingFrameworkWindowPanel(TestingFrameworkWindowPanel newTestingFrameworkWindowPanel) {
	testingFrameworkWindowPanel = newTestingFrameworkWindowPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showAddTestCaseDialog(JComponent addTCPanel, Component requester) {

	addTCPanel.setPreferredSize(new java.awt.Dimension(600, 200));
	getAddTestCaseDialog().setMessage("");
	getAddTestCaseDialog().setMessage(addTCPanel);
	getAddTestCaseDialog().setValue(null);
	JDialog d = getAddTestCaseDialog().createDialog(requester, "New TestCase:");
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	d.setVisible(true);
	return getAddTestCaseDialog().getValue();

}


/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
private Object showAddTestSuiteDialog(JComponent addTSPanel, Component requester,String duplicateTestSuiteName) {

	addTSPanel.setPreferredSize(new java.awt.Dimension(350, 250));
	getAddTestSuiteDialog().setMessage("");
	getAddTestSuiteDialog().setMessage(addTSPanel);
	getAddTestSuiteDialog().setValue(null);
	JDialog d = getAddTestSuiteDialog().createDialog(requester, (duplicateTestSuiteName != null?"Duplicate TestSuite '"+duplicateTestSuiteName+"'":"New TestSuite"));
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	d.setVisible(true);
	return getAddTestSuiteDialog().getValue();

}

/**
 * Insert the method's description here.
 * Creation date: (5/14/2004 6:11:35 PM)
 */
@SuppressWarnings("deprecation")
private Object showEditTestCriteriaDialog(JComponent editTCrPanel, Component requester) {
	editTCrPanel.setPreferredSize(new java.awt.Dimension(400, 300));
	getEditTestCriteriaDialog().setMessage("");
	getEditTestCriteriaDialog().setMessage(editTCrPanel);
	getEditTestCriteriaDialog().setValue(null);
	JDialog d = getEditTestCriteriaDialog().createDialog(requester, "Edit Test Criteria:");
	d.setResizable(true);
	d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	DialogUtils.showModalJDialogOnTop(d, requester);
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
public void startExport(Component requester,OutputContext outputContext,ExportSpecs exportSpecs) {
	getRequestManager().startExport(outputContext, requester, exportSpecs);
}


/**
 * Insert the method's description here.
 * Creation date: (11/16/2004 6:38:33 AM)
 * @param simInfos cbit.vcell.solver.SimulationInfo[]
 */
public String startSimulations(TestCriteriaNew[] tcrits,ClientTaskStatusSupport pp) {

	if(tcrits == null || tcrits.length == 0){
		throw new IllegalArgumentException("startSimulations: No TestCriteria arguments");
	}

	StringBuffer errors = new StringBuffer();
	for(int i=0;i<tcrits.length;i+= 1){
		try{
			pp.setProgress((int)(1+(((double)i/(double)tcrits.length)*100)));
			pp.setMessage("Trying to run sim "+tcrits[i].getSimInfo().getName());
			updateTCritStatus(tcrits[i],TestCriteriaNew.TCRIT_STATUS_SIMRUNNING,null);
			getRequestManager().runSimulation(tcrits[i].getSimInfo(),tcrits[i].getScanCount());
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
public String startTestSuiteSimulations(TestSuiteInfoNew testSuiteInfo,ClientTaskStatusSupport pp){

	StringBuffer errors = new StringBuffer();
	try{
		pp.setProgress(1);
		pp.setMessage("Getting TestSuite "+testSuiteInfo.getTSID());
		TestSuiteNew testSuite =
			getRequestManager().getDocumentManager().getTestSuite(testSuiteInfo.getTSKey());

		Vector<TestCriteriaNew> tcritVector = new Vector<TestCriteriaNew>();
		TestCaseNew[] testCases = testSuite.getTestCases();
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
public String updateSimRunningStatus(ClientTaskStatusSupport pp,TestSuiteInfoNew tsin){

	if(tsin.isLocked()){
		return null;
	}
	StringBuffer errors = new StringBuffer();

	Vector<TestCriteriaNew> runningTCrits = new Vector<TestCriteriaNew>();
	try{
		TestSuiteInfoNew[] tsinfos = getRequestManager().getDocumentManager().getTestSuiteInfos();
		if(tsinfos != null && tsinfos.length > 0){
			for(int i=0;i<tsinfos.length;i+= 1){
				try{
					if(tsin != null && !tsinfos[i].getTSKey().equals(tsin.getTSKey())){
						continue;
					}
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
					SimulationInfo simInfo = tcn.getSimInfo();
					pp.setProgress((int)(50+(i*50/runningTCrits.size())));
					pp.setMessage("Update SimsRunning, Setting Status "+simInfo.getName());
					//Check if there is some status different from "running"
					if(simInfo != null){
						SimulationStatus simStatus = getRequestManager().getServerSimulationStatus(simInfo);
						if(simStatus != null){
							if (simStatus.isFailed()){
								updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_SIMFAILED,"Sim msg="+simStatus.getJob0SimulationMessage().getDisplayMessage());
							}else if(simStatus.isJob0Completed()){
								updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null);
							}else if(!simStatus.isRunning()){
								updateTCritStatus(tcn,TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE,
									"Sim jobstatus "+simStatus.toString()+" "+simStatus.getJob0SimulationMessage().getDisplayMessage());
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

public void removeTestCriteria(TestCriteriaNew[] tcritArr) throws DataAccessException{
//	new RemoveTestCriteriaOP(selTestCritKeysArr.toArray(new BigDecimal[0]))
	getRequestManager().getDocumentManager().doTestSuiteOP(new RemoveTestCriteriaOP(tcritArr));
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
	try (SuppressRemote sr = new VCellThreadChecker.SuppressRemote()){ //okay to call remote from Swing for testing
		Simulation sim = ((ClientDocumentManager)getRequestManager().getDocumentManager()).getSimulation(testCriteria.getSimInfo());

		DataViewerController dataViewerCtr = getRequestManager().getDataViewerController(null,sim, 0);
		addDataListener(dataViewerCtr);
		// make the viewer
		DataViewer viewer = dataViewerCtr.createViewer();
		viewer.setDataViewerManager(this);
		addExportListener(viewer);

		// create the simCompareWindow - this is just a lightweight window to display the simResults.
		// It was created originally to compare 2 sims, it can also be used here instead of creating the more heavy-weight SimWindow.
		ChildWindowManager childWindowManager =TFWFinder.findChildWindowManager(getComponent());
		ChildWindow childWindow = childWindowManager.addChildWindow(viewer, vcdID, "Comparing ... "+vcdID, true);
		childWindow.setIsCenteredOnParent();
		childWindow.pack();
		childWindow.show();

	} catch (Throwable e) {
		PopupGenerator.showErrorDialog(TestingFrameworkWindowManager.this, e.getMessage());
	}
}


public User getUser() {
	return getRequestManager().getDocumentManager().getUser();
}

/**
 * helper class to find {@link ChildWindowManager} from {@link TestingFrameworkWindow} child
 */
private static class TFWFinder {

	/**
	 * @param component child of {@link TestingFrameworkWindow} e.g. {@link TestingFrameworkWindowPanel}
	 * @return ChildWindowManager
	 * @throws NullPointerException if null or not child of {@link TestingFrameworkWindow}
	 */
	public static ChildWindowManager findChildWindowManager(Component component) {
		Objects.requireNonNull(component);
		TestingFrameworkWindow tfw = LWNamespace.findOwnerOfType(TestingFrameworkWindow.class, component);
		return tfw.getChildWindowManager();
	}

}


}
