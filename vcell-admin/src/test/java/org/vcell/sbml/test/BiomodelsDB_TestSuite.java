package org.vcell.sbml.test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.output.NullWriter;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import org.vcell.sbml.SBMLSolver;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImportException.Category;
import org.vcell.util.SkipCommentLineNumberReader;
import org.vcell.util.logging.Logging;

import cbit.util.xml.XmlUtil;
import cbit.vcell.client.desktop.biomodel.BioModelsNetPanel;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.server.bionetgen.BNGException;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesServiceLocator;

public class BiomodelsDB_TestSuite {

	private static void write(Element bioModelInfoElement, File file) throws IOException{
		Document reportDocument = new Document();
		Element bioModelInfos = new Element("BioModelInfos");
		reportDocument.setRootElement(bioModelInfos);
		bioModelInfos.addContent(bioModelInfoElement);
		write(reportDocument,file);
	}

	private static void write(Document doc, File file) throws IOException{
		PrintWriter reportFileWriter = new PrintWriter(file);
		XMLOutputter outputter = new XMLOutputter();
		outputter.getFormat().setLineSeparator("\n");
		outputter.output(doc, reportFileWriter);
		reportFileWriter.flush();
		reportFileWriter.close();
	}

	private static Element getVariableSummary(VariableComparisonSummary vcSummary){
		Element failedVariableSummary = new Element("variableComparisonSummary");
		failedVariableSummary.setAttribute("name", vcSummary.getName());
		failedVariableSummary.setAttribute("relError", Double.toString(vcSummary.getRelativeError()));
		failedVariableSummary.setAttribute("absError", Double.toString(vcSummary.getAbsoluteError()));
		failedVariableSummary.setAttribute("maxRef", Double.toString(vcSummary.getMaxRef()));
		failedVariableSummary.setAttribute("minRef", Double.toString(vcSummary.getMinRef()));
		failedVariableSummary.setAttribute("meanSqError", Double.toString(vcSummary.getMeanSqError()));
		failedVariableSummary.setAttribute("indexRelError", Double.toString(vcSummary.getIndexRelativeError()));
		failedVariableSummary.setAttribute("indexAbsError", Double.toString(vcSummary.getIndexAbsoluteError()));
		failedVariableSummary.setAttribute("timeRelError", Double.toString(vcSummary.getTimeRelativeError()));
		failedVariableSummary.setAttribute("timeAbsError", Double.toString(vcSummary.getTimeAbsoluteError()));
		return failedVariableSummary;
	}

	public static void main(String[] args){
		try {
			Logging.init();
			/*sanity check -- we currently only have a copasi_java dll for 32-bit, so let's make sure 
			 * we're running on the right JVM. (Note we can run this on 64 bit machine, just have to install
			 * a 32 bit JVM...)
			 */
			OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
			if (!osi.isWindows() || osi.is64bit()) {
				System.err.println("run on 32 bit JVM");
				System.exit(99);
			}
			

			//following are set in command line processing
			SortedSet<BiomodelsNetEntry> modelIDs = new TreeSet<BiomodelsNetEntry>( );
			BioModelsWebServices service = null; 
			File outDir = null;
			boolean isDetailed;

			{ //scope for command line processing

				Options commandOptions = new Options();
				Option help = new Option("help",false,"show help");
				commandOptions.addOption(help);
				Option detailed = new Option("detailed",false,"record detailed information");
				commandOptions.addOption(detailed);
				Option output = new Option("output",true,"output directory");
				output.setRequired(true);
				commandOptions.addOption(output);

				OptionGroup primary = new OptionGroup();

				LOption min = new LOption("min",true,"minimum number of model to import",true);
				min.setType(Number.class);

				LOption only = new LOption("only",true,"run only this model",false);
				only.setType(Number.class);

				LOption include = new LOption("include",true,"run on models in specified file",false);
				include.setType(String.class);

				LOption exclude = new LOption("exclude",true,"exclude models in specified file",true);
				exclude.setType(String.class);

				primary.addOption(min).addOption(only).addOption(include).addOption(exclude);
				for (Object obj : primary.getOptions()) { //CLI old, non-generic API
					commandOptions.addOption((Option) obj);
				}

				CommandLine cmdLine = null;
				try {
					CommandLineParser parser = new DefaultParser( );
					cmdLine = parser.parse(commandOptions, args);
				} catch (ParseException e1) {
					e1.printStackTrace();
					HelpFormatter hf = new HelpFormatter();
					hf.printHelp("BiomodelsDB_TestSuite", commandOptions);
					System.exit(2);
				}
				Option present[] = cmdLine.getOptions();
				Set<Option> optionSet = new HashSet<Option>(Arrays.asList(present));
				if (optionSet.contains(help)) {
					HelpFormatter hf = new HelpFormatter();
					hf.printHelp("BiomodelsDB_TestSuite", commandOptions);
					System.exit(0);
				}

				LOption primaryOpt =new LOption("",false,"",true); //placeholder, avoid messing with nulls
				@SuppressWarnings("unchecked")
				Collection<? extends Option> priOpts = primary.getOptions();
				priOpts.retainAll(optionSet);
				if (!priOpts.isEmpty()) {
					assert(priOpts.size( ) == 1);
					primaryOpt = (LOption)priOpts.iterator().next( );
				}


				outDir = new File(cmdLine.getOptionValue(output.getOpt()));
				if (!outDir.exists()){
					outDir.mkdirs();
				}
				isDetailed = optionSet.contains(detailed);
			

				BioModelsWebServicesServiceLocator locator = new BioModelsWebServicesServiceLocator();
				service = locator.getBioModelsWebServices();

				if (primaryOpt.downloads) {
					String[] modelStrings = service.getAllCuratedModelsId();
					for (String s : modelStrings) {
						modelIDs.add(new BiomodelsNetEntry(s));
					}
				}
				if (primaryOpt == only) {
					Long only1 = (Long) cmdLine.getParsedOptionValue(only.getOpt( ));
					modelIDs.add(new BiomodelsNetEntry(only1.intValue()));
				}
				else if (primaryOpt == min) {
					Long lv = (Long) cmdLine.getParsedOptionValue(min.getOpt( ));
					int minimumModel = lv.intValue();
					for (int m = 0 ; m < minimumModel; m++) {
						modelIDs.remove(new BiomodelsNetEntry(m));
					}
				}
				else if (primaryOpt == include) {
					FileBaseFilter fbf = new FileBaseFilter(cmdLine.getOptionValue(include.getOpt()));
					modelIDs.addAll(fbf.models);
				}
				else if (primaryOpt == exclude) {
					FileBaseFilter fbf = new FileBaseFilter(cmdLine.getOptionValue(exclude.getOpt()));
					modelIDs.removeAll(fbf.models);
				}
			} //end command line processing 
			
			WriterFlusher flusher = new WriterFlusher(10);
			
			PrintWriter detailWriter;
			PrintWriter bngWriter;
			PrintWriter sbmlWriter;
			SBMLExceptionSorter sbmlExceptions;
			if (isDetailed) {
				detailWriter = new PrintWriter( new File(outDir, "compareDetail.txt") );
				bngWriter = new PrintWriter( new File(outDir, "bngErrors.txt") );
				sbmlWriter = new PrintWriter( new File(outDir, "sbmlErrors.txt") );
				sbmlExceptions = new LiveSorter(); 
				flusher.add(detailWriter);
				flusher.add(bngWriter);
				flusher.add(sbmlWriter);
			}
			else {
				detailWriter = new PrintWriter(new NullWriter());
				bngWriter = new PrintWriter(new NullWriter());
				sbmlWriter = new PrintWriter(new NullWriter());
				sbmlExceptions = new NullSorter(); 
			}
			PropertyLoader.loadProperties();
			/**
			 * example properties
			 * 
			 * vcell.COPASI.executable = "C:\\Program Files\\COPASI\\bin\\CopasiSE.exe"
			 * vcell.mathSBML.directory = "c:\\developer\\eclipse\\workspace\\mathsbml\\"
			 * vcell.mathematica.kernel.executable = "C:\\Program Files\\Wolfram Research\\Mathematica\\7.0\\MathKernel.exe"
			 */
			ResourceUtil.setNativeLibraryDirectory();

			PrintWriter printWriter = new PrintWriter(new FileWriter(new File(outDir, "summary.log"),true));
			flusher.add(printWriter);
			
			listModels(printWriter, modelIDs);
			
			//SBMLSolver copasiSBMLSolver = new CopasiSBMLSolver();
			SBMLSolver copasiSBMLSolver = new SBMLSolver(){

				@Override
				public File solve(String filePrefix, File outDir, String sbmlText, SimSpec testSpec)
						throws IOException, SolverException, SbmlException {
					throw new RuntimeException("COPASI SOLVER REMOVED, REIMPLEMENT");
				}

				@Override
				public String getResultsFileColumnDelimiter() {
					throw new RuntimeException("COPASI SOLVER REMOVED, REIMPLEMENT");
				}
				
			};
			try {
				printWriter.println(" | *BIOMODEL ID* | *BioModel name* | *PASS* | *Rel Error (VC/COP)(VC/MSBML)(COP/MSBML)* | *Exception* | ");
				removeToxic(modelIDs, printWriter);
				for (BiomodelsNetEntry modelID : modelIDs){


					String modelName = service.getModelNameById(modelID.toString());
					String modelSBML = service.getModelById(modelID.toString());

					Element bioModelInfo = new Element(BioModelsNetPanel.BIOMODELINFO_ELEMENT_NAME);
					bioModelInfo.setAttribute(BioModelsNetPanel.ID_ATTRIBUTE_NAME, modelID.toString());
					bioModelInfo.setAttribute(BioModelsNetPanel.SUPPORTED_ATTRIBUTE_NAME,"false");
					bioModelInfo.setAttribute("vcell_ran","false");
					bioModelInfo.setAttribute("COPASI_ran","false");
					bioModelInfo.setAttribute("mSBML_ran","false");
					bioModelInfo.setAttribute(BioModelsNetPanel.MODELNAME_ATTRIBUTE_NAME, modelName);

					boolean bUseUTF8 = true;
					File sbmlFile = new File(outDir,modelID+".sbml");
					XmlUtil.writeXMLStringToFile(modelSBML, sbmlFile.getAbsolutePath(), bUseUTF8);

					PrintStream saved_sysout = System.out;
					PrintStream saved_syserr = System.err;
					PrintStream new_sysout = null;
					PrintStream new_syserr = null;
					try {
						String filePrefix = modelID.toString();
						String sbmlText = modelSBML;

						File logFile = new File(outDir,filePrefix+".log");
						new_sysout = new PrintStream(logFile);
						new_syserr = new_sysout;
						System.setOut(new_sysout);
						System.setErr(new_syserr);
						StringBuffer combinedErrorBuffer = new StringBuffer();

						SimSpec simSpec = SimSpec.fromSBML(modelSBML);
						String[] varsToTest = simSpec.getVarsList();

						printWriter.print("ModelId: " + modelID + ": ");
						
						//if a model crashes out the libSBML dll, we will terminate abruptly. Flush
						//summary log before that happens
						printWriter.flush( ); 
						try {
							//
							// get VCell solution with an embedded "ROUND TRIP" (time and species concentrations)
							//
							ODESolverResultSet vcellResults_RT = null;
							try {
								VCellSBMLSolver vcellSBMLSolver_RT = new VCellSBMLSolver();
								vcellSBMLSolver_RT.setRoundTrip(false);
								// TODO try with round-trip later.
								String columnDelimiter = vcellSBMLSolver_RT.getResultsFileColumnDelimiter();
								File resultFile = vcellSBMLSolver_RT.solve(filePrefix, outDir, sbmlFile.getAbsolutePath(), simSpec);
								vcellResults_RT = readResultFile(resultFile, columnDelimiter); 
								bioModelInfo.setAttribute("vcell_ran","true");
							}catch (BNGException e){
								bngWriter.println(modelID + " " + e.getMessage());
								throw e;
							}catch (SBMLImportException e) {
								ModelException me = new ModelException(modelID,e);
								write(sbmlWriter,me);
								sbmlExceptions.add(me);
								throw e;
							}catch (Exception e){
								printWriter.println("vcell solve(roundtrip=true) failed");
								e.printStackTrace(printWriter);
								System.out.println("vcell solve(roundtrip=true) failed");
								e.printStackTrace(System.out);
								combinedErrorBuffer.append(" *VCELL_RT* _"+e.getMessage()+"_ ");

								Element vcellSolverReport = new Element("SolverReport");
								vcellSolverReport.setAttribute("solverName","vcell");
								vcellSolverReport.setAttribute("errorMessage",e.getMessage());
								bioModelInfo.addContent(vcellSolverReport);

								bioModelInfo.setAttribute("vcell_ran","false");
							}
							//
							// get COPASI solution (time and species concentrations)
							//
							ODESolverResultSet copasiResults = null;


							try {
								String columnDelimiter = copasiSBMLSolver.getResultsFileColumnDelimiter();
								File resultFile = copasiSBMLSolver.solve(filePrefix, outDir, sbmlText, simSpec);
								copasiResults = readResultFile(resultFile, columnDelimiter);
								bioModelInfo.setAttribute("COPASI_ran","true");
							}catch (Exception e){
								printWriter.println("Copasi solve() failed");
								e.printStackTrace(printWriter);
								System.out.println("Copasi solve() failed");
								e.printStackTrace(System.out);
								combinedErrorBuffer.append(" *COPASI* _"+e.getMessage()+"_ ");

								Element copasiSolverReport = new Element("SolverReport");
								copasiSolverReport.setAttribute("solverName","COPASI");
								copasiSolverReport.setAttribute("errorMessage",e.getMessage());
								bioModelInfo.addContent(copasiSolverReport);

								bioModelInfo.setAttribute("COPASI_ran","false");
							}


							//
							// get mSBML solution (time and species concentrations)
							//
							/*
							ODESolverResultSet mSBMLResults = null;
							if (idInt != 246 && idInt != 250 && idInt != 285 && idInt != 301) {
								try {
									MathSBMLSolver mSBMLSolver = new MathSBMLSolver();
									String columnDelimiter = mSBMLSolver.getResultsFileColumnDelimiter();
									org.sbml.libsbml.SBMLDocument sbmlDocument = new SBMLReader().readSBML(sbmlFile.getAbsolutePath());
									long level = sbmlDocument.getLevel();
									long version = sbmlDocument.getVersion();
									String mathsbmlFilePrefix = filePrefix;
									if (level!=2 || (level==2 && version>3)){
	//									sbmlDocument.setConsistencyChecksForConversion(libsbmlConstants.LIBSBML_CAT_MODELING_PRACTICE, false);
										long numErrors = sbmlDocument.checkL2v3Compatibility();

										if (numErrors==0){
											boolean bConversionWorked = sbmlDocument.setLevelAndVersion(2, 3, false);
											SBMLDocument doc = new SBMLDocument(sbmlDocument);
											doc.setLevelAndVersion(2,3,false);
											if (bConversionWorked){
												mathsbmlFilePrefix = filePrefix+"_L2V3";
												long newVersion = doc.getVersion();
												SBMLWriter sbmlWriter = new SBMLWriter();
												sbmlText = sbmlWriter.writeToString(doc);
												try {
													XmlUtil.writeXMLStringToFile(sbmlText, mathsbmlFilePrefix+".sbml", true);
												} catch (IOException e1) {
													e1.printStackTrace(System.out);
												} 
											}else{
												throw new RuntimeException("couldn't convert SBML from L"+level+"V"+version+" to L2V3");
											}
										}else{
											throw new RuntimeException("couldn't convert SBML from L"+level+"V"+version+" to L2V3, not compatible: "+sbmlDocument.getError(0));
										}
									}
									File resultFile = mSBMLSolver.solve(mathsbmlFilePrefix, outDir, sbmlText, simSpec);
									mSBMLResults = readResultFile(resultFile, columnDelimiter); 
									bioModelInfo.setAttribute("mSBML_ran","true");
								}catch (Exception e){
									printWriter.println("mSBML solve() failed");
									e.printStackTrace(printWriter);
									System.out.println("mSBML solve() failed");
									e.printStackTrace(System.out);
									combinedErrorBuffer.append(" *mSBML* _"+e.getMessage()+"_ ");

									Element mSBMLSolverReport = new Element("SolverReport");
									mSBMLSolverReport.setAttribute("solverName","mSBML");
									mSBMLSolverReport.setAttribute("errorMessage",e.getMessage());
									bioModelInfo.addContent(mSBMLSolverReport);

									bioModelInfo.setAttribute("mSBML_ran","false");
								}
							}
							 */
						
							//
							// compare results from COPASI and VCELL_RT
							//
							Boolean bCOPASI_VCELL_matched = null;
							if (copasiResults!=null && vcellResults_RT!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareResultSets(copasiResults, vcellResults_RT, varsToTest, TestCaseNew.REGRESSION, 1e-5, 1e-5);
									double maxRelError = summary.getMaxRelativeError();
									bioModelInfo.setAttribute("COPASI_VCELL_maxRelErr", Double.toString(maxRelError));

									if (maxRelError<1){
										bCOPASI_VCELL_matched = true;
									}else{
										detailWriter.println(modelID + " " + modelName);
										bCOPASI_VCELL_matched = false;
										Element solverComparison = new Element("SolverComparison");
										solverComparison.setAttribute("solver1","vcell");
										solverComparison.setAttribute("solver2","COPASI");
										VariableComparisonSummary[] failedVCSummaries = summary.getFailingVariableComparisonSummaries(1e-5, 1e-5);
										for (VariableComparisonSummary vcSummary : failedVCSummaries){
											Element failedVariableSummary = getVariableSummary(vcSummary);
											solverComparison.addContent(failedVariableSummary);
											String ss = vcSummary.toShortString();
											detailWriter.print('\t');
											detailWriter.println(ss);
											System.out.println(ss);
										}
										detailWriter.println( );
										bioModelInfo.addContent(solverComparison);
									}
								} catch (Exception e) {
									printWriter.println("COMPARE VC/COPASI failed");
									e.printStackTrace(printWriter);
									System.out.println("COMPARE VC/COPASI failed");
									e.printStackTrace(System.out);
									combinedErrorBuffer.append(" *COMPARE VC/COPASI* _"+e.getMessage()+"_ ");

									Element solverComparison = new Element("SolverComparison");
									solverComparison.setAttribute("solver1","vcell");
									solverComparison.setAttribute("solver2","COPASI");
									solverComparison.setAttribute("error",e.getMessage());
									bioModelInfo.addContent(solverComparison);
								}
							}
							//
							// compare results from mSBML and VCELL
							//
							/*						
							Boolean bmSBML_VCELL_matched = null;
							if (mSBMLResults!=null && vcellResults_RT!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(mSBMLResults, vcellResults_RT, varsToTest, 1e-5, 1e-5, 1);
									double maxRelError = summary.getMaxRelativeError();
									bioModelInfo.setAttribute("mSBML_VCELL_maxRelErr", Double.toString(maxRelError));

									if (maxRelError<1){
										bmSBML_VCELL_matched = true;
									}else{
										bmSBML_VCELL_matched = false;
										Element solverComparison = new Element("SolverComparison");
										solverComparison.setAttribute("solver1","vcell");
										solverComparison.setAttribute("solver2","mSBML");
										VariableComparisonSummary[] failedVCSummaries = summary.getFailingVariableComparisonSummaries(1e-5, 1e-5);
										for (VariableComparisonSummary vcSummary : failedVCSummaries){
											Element failedVariableSummary = getVariableSummary(vcSummary);
											solverComparison.addContent(failedVariableSummary);
											System.out.println(vcSummary.toShortString());
										}
										bioModelInfo.addContent(solverComparison);
									}
								} catch (Exception e) {
									printWriter.println("COMPARE VCRT/mSBML failed");
									e.printStackTrace(printWriter);
									System.out.println("COMPARE VCRT/mSBML failed");
									e.printStackTrace(System.out);
									combinedErrorBuffer.append(" *COMPARE VCRT/mSBML* _"+e.getMessage()+"_ ");

									Element solverComparison = new Element("SolverComparison");
									solverComparison.setAttribute("solver1","vcell");
									solverComparison.setAttribute("solver2","mSBML");
									solverComparison.setAttribute("error",e.getMessage());
									bioModelInfo.addContent(solverComparison);
								}
							}

							//
							// compare results from COPASI and mSBML
							//
							Boolean bCOPASI_mSBML_matched = null;
							if (copasiResults!=null && mSBMLResults!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(copasiResults, mSBMLResults, varsToTest, 1e-5, 1e-5, 1);
									double maxRelError = summary.getMaxRelativeError();
									bioModelInfo.setAttribute("COPASI_mSBML_maxRelErr", Double.toString(maxRelError));

									if (maxRelError<1){
										bCOPASI_mSBML_matched = true;
									}else{
										bCOPASI_mSBML_matched = false;
										Element solverComparison = new Element("SolverComparison");
										solverComparison.setAttribute("solver1","COPASI");
										solverComparison.setAttribute("solver2","mSBML");
										solverComparison.setAttribute("result","failed");
										VariableComparisonSummary[] vcSummaries = summary.getVariableComparisonSummaries();
										for (VariableComparisonSummary vcSummary : vcSummaries){
											Element failedVariableSummary = getVariableSummary(vcSummary);
											solverComparison.addContent(failedVariableSummary);
											System.out.println(vcSummary.toShortString());
										}
										bioModelInfo.addContent(solverComparison);
									}
								} catch (Exception e) {
									printWriter.println("COMPARE COPASI/mSBML failed");
									e.printStackTrace(printWriter);
									System.out.println("COMPARE COPASI/mSBML failed");
									e.printStackTrace(System.out);
									combinedErrorBuffer.append(" *COMPARE COPASI/mSBML* _"+e.getMessage()+"_ ");

									Element solverComparison = new Element("SolverComparison");
									solverComparison.setAttribute("solver1","COPASI");
									solverComparison.setAttribute("solver2","mSBML");
									solverComparison.setAttribute("error",e.getMessage());
								}
							}
							 */						
							if ((bCOPASI_VCELL_matched!=null && bCOPASI_VCELL_matched.booleanValue())) {
								//								|| /*(bmSBML_VCELL_matched!=null && bmSBML_VCELL_matched.booleanValue()) */ )  
								bioModelInfo.setAttribute(BioModelsNetPanel.SUPPORTED_ATTRIBUTE_NAME,"true");
								printWriter.println("PASS");
							}else{
								bioModelInfo.setAttribute(BioModelsNetPanel.SUPPORTED_ATTRIBUTE_NAME,"false");
								printWriter.println("FAIL");
							}
						}catch (Exception e){
							e.printStackTrace(printWriter);
							combinedErrorBuffer.append(" *UNKNOWN* _"+e.getMessage()+"_ ");
							bioModelInfo.setAttribute(BioModelsNetPanel.SUPPORTED_ATTRIBUTE_NAME,"false");
							bioModelInfo.setAttribute("exception",e.getMessage());
						}
						printWriter.flush();
						write(bioModelInfo,new File(outDir,modelID+"_report.xml")); // write for each model just in case files get corrupted (it happened).
					}finally{
						if (new_sysout!=null){
							new_sysout.close();
							new_sysout=null;
						}
						if (new_syserr!=null){
							new_syserr.close();
							new_syserr=null;
						}
						System.setOut(saved_sysout);
						System.setOut(saved_syserr);
					}
				}
				
				//this writes out the SBML import exceptions grouped by type
				if (!sbmlExceptions.isEmpty()) {
					Map<Category, Collection<ModelException>> map = sbmlExceptions.getMap();
					try (PrintWriter pw = new PrintWriter(new File(outDir,"sbmlSorted.txt")) ) {
						//SBMLImportException.Category
						for (Category c : Category.values()) {
							Collection<ModelException> meCollection = map.get(c);
							for (ModelException me : meCollection) {
								write(pw,me);
							}
						}
					}
				}
				
			}
			finally{
				printWriter.close();
				detailWriter.close( );
				bngWriter.close( );
			}
		}catch (Throwable e){
			e.printStackTrace(System.out);
			e.printStackTrace(System.err);
		}
		System.exit(0);
	}
	
	/**
	* see {@link BiomodelsDB_TestSuite#listModels(PrintWriter, SortedSet) }
	 */
	private enum LState {
		BEGIN,
		ONE_LISTED,
		COMMAS,
		RANGE
	}
	/**
	 * pretty print models 
	 * @param printWriter destination
	 * @param modelIDs
	 */
	private static void listModels(PrintWriter printWriter, SortedSet<BiomodelsNetEntry> modelIDs) {
		printWriter.println("Models to evaluate:");
		LState st = LState.BEGIN;
		int lastModel = -1;
		int modelsOnLine = -1;
		for (BiomodelsNetEntry m : modelIDs) {
			final int id = m.number();
			
			switch (st)  {
			case BEGIN:
				lastModel = id; 
				printWriter.print(id);
				st = LState.ONE_LISTED;
				break;
			case ONE_LISTED:
				if (id == lastModel + 1){
					lastModel = id;
					st = LState.RANGE;
				}
				else {
					printWriter.print(", ");
					printWriter.print(id);
					modelsOnLine = 1;
					st = LState.COMMAS;
				}
				break;
			case COMMAS:
				printWriter.print(", ");
				printWriter.print(id);
				if (++modelsOnLine == 5) {
					printWriter.println( );
					st = LState.BEGIN;
				}
				break;
			case RANGE:
				if (id != lastModel + 1){
					printWriter.print(" - ");
					printWriter.println(id);
					st = LState.BEGIN;
				}
				break;
			}
		}
	}

	/**
	 * remove models known to crash JVM due to libSBML dll error
	 * @param models set to remove from
	 * @param note place to record removal so we don't forget
	 */
	private static void removeToxic(Collection<BiomodelsNetEntry> models, PrintWriter note) {
		//int toxic[] = {510,511,515,516}; //models known to crash libsbml ...
		int toxic[] = {}; //models known to crash libsbml ...
		if (toxic.length > 0) {
			note.print("Removing models which crash JVM due to libsbml errors:  ");
			for (int t : toxic) {
				BiomodelsNetEntry bne = new BiomodelsNetEntry(t);
				note.print(bne);
				note.print(", ");
				models.remove(bne);
			}
			note.println( );
		}
	}
	
	/**
	 * write exception info, balancing informativeness with conciseness
	 * @param sbmlWriter
	 * @param me
	 */
	private static void write(PrintWriter sbmlWriter, ModelException me) {
			SBMLImportException e = me.importException;
		
			switch (me.importException.category) {
			case NON_INTEGER_STOICH:
			case RATE_RULE:
			case RESERVED_SPATIAL:
			case RESERVED_SPECIES:
			case INCONSISTENT_UNIT:
		    case  DELAY:
				sbmlWriter.println(me.entry +  ":  " + e.category  + " not supported by Virtual Cell" ); 
				break;
			case UNSPECIFIED:
				sbmlWriter.println(me.entry + ":  " + e.category  + " " + e.getAllCauses());
				e.printStackTrace(sbmlWriter);
				break;
				default:
					throw new UnsupportedOperationException("value " + me.importException.category +  " missing from switch");
			}
	}

	public static ODESolverResultSet readResultFile(File resultFile, String delimiter) throws IOException{
		String reportText = XmlUtil.getXMLString(resultFile.getAbsolutePath());
		//System.out.println(reportText);
		ODESolverResultSet odeResultSet = new ODESolverResultSet();
		StringTokenizer lineTokenizer = new StringTokenizer(reportText,"\r\n",false);
		int lineCount = 0;
		while (lineTokenizer.hasMoreElements()){
			lineCount++;
			String line = lineTokenizer.nextToken();
			StringTokenizer columnTokenizer = new StringTokenizer(line,delimiter,false);
			if (lineCount==1){
				int wordCount = 0;
				while (columnTokenizer.hasMoreTokens()){
					String label = columnTokenizer.nextToken();
					if (wordCount == 0) {						
						label = "t";
					}
					odeResultSet.addDataColumn(new ODESolverResultSetColumnDescription(label));
					wordCount++;
				}
			}else{
				double[] values = new double[odeResultSet.getDataColumnCount()];
				for (int i = 0; i < values.length; i++) {
					values[i] = Double.parseDouble(columnTokenizer.nextToken());
				}
				odeResultSet.addRow(values);
			}
		}		
		return odeResultSet;
	}

	public static Document writeSupportedModelsReport(File supportInfoDir,File saveSupportedXMLPathname) throws Exception{
		if(!supportInfoDir.isDirectory()){
			throw new IllegalArgumentException("File "+supportInfoDir.getAbsolutePath()+" must be a directory.");
		}
		File[] xmlReportFiles = supportInfoDir.listFiles(
				new FileFilter() {
					public boolean accept(File pathname) {
						if(pathname.isFile()){
							if(pathname.getName().endsWith("_report.xml")){
								return true;
							}
						}
						return false;
					}
				}
				);
		Document supportedDocument = new Document(new Element("Supported_BioModelsNet"));
		List<Element> supportedElements = new ArrayList<Element>();
		for (int i = 0; i < xmlReportFiles.length; i++) {
			byte[] xmlBytes = new byte[(int)xmlReportFiles[i].length()];
			FileInputStream fis = new FileInputStream(xmlReportFiles[i]);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);
			dis.readFully(xmlBytes);
			dis.close();
			Document document = XmlUtil.stringToXML(new String(xmlBytes), null);
			Element bioModelElement = document.getRootElement().getChild(BioModelsNetPanel.BIOMODELINFO_ELEMENT_NAME);
			Attribute supportedAttribute = bioModelElement.getAttribute(BioModelsNetPanel.SUPPORTED_ATTRIBUTE_NAME);
			if(supportedAttribute.getBooleanValue()){
				Element newBioModelElement = new Element(BioModelsNetPanel.BIOMODELINFO_ELEMENT_NAME);
				@SuppressWarnings("unchecked")
				List<Attribute> attrList = bioModelElement.getAttributes();
				Iterator<Attribute> iterAttr = attrList.iterator();
				while(iterAttr.hasNext()){
					newBioModelElement.setAttribute((Attribute)iterAttr.next().clone());
				}
				supportedElements.add(newBioModelElement);
			}
		}
		//Collections.sort(supportedElements, new ElementComparer());
		Element root = supportedDocument.getRootElement();
		for (Element e: supportedElements) {
			root.addContent(e);
		}
		if(saveSupportedXMLPathname != null){
			try (FileWriter fw = new FileWriter(saveSupportedXMLPathname.getAbsolutePath( ))) {
				Format format = Format.getPrettyFormat( );

				XMLOutputter out = new XMLOutputter(format);
				out.output(supportedDocument,fw);
			}
		}
		return supportedDocument;
	}

	@SuppressWarnings("unused")
	private static class ElementComparer implements Comparator<Element> {

		@Override
		public int compare(Element lhs, Element rhs) {
			Attribute left = lhs.getAttribute("Name");
			Attribute right= rhs.getAttribute("Name");
			return left.getValue().compareTo(right.getValue());
		}
	}

	private static class FileBaseFilter {
		Collection<BiomodelsNetEntry> models;

		FileBaseFilter(String name) throws FileNotFoundException, IOException {
			models = new ArrayList<BiomodelsNetEntry>( );
			try (SkipCommentLineNumberReader br = new SkipCommentLineNumberReader(new FileReader(name))) {
				while (br.ready()) {
					String s = br.readLine().trim( );
					try {
						models.add(new BiomodelsNetEntry(s));
					} catch (NumberFormatException nfe) {
						throw new RuntimeException("Error parsing line " + br.getLineNumber() + ": " + s,nfe);
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	private static class LOption extends Option {
		final boolean downloads;
		LOption(String opt, boolean hasArg, String description, boolean downloads) {
			super(opt,hasArg, description);
			this.downloads = downloads;
		}

	}

	@Test
	public void writeXml( ) throws Exception {
		File dir = new File("../VCell_5.2/SBMLOutputDirectory52d");
		File out = new File("../VCell_5.2/bioModelsNetInfo.xml");
		writeSupportedModelsReport(dir, out);
	}
	
	/**
	 * modelId String and {@link SBMLImportException}
	 */
	private static class ModelException{
		final BiomodelsNetEntry entry; 
		final SBMLImportException importException;
		public ModelException(BiomodelsNetEntry entry, SBMLImportException sie) {
			super();
			this.entry = entry;
			this.importException = sie;
		}
	}
	
	interface SBMLExceptionSorter {
		void add(ModelException me);
		Map<SBMLImportException.Category,Collection<ModelException> > getMap( );
		boolean isEmpty( );
	}
	
	static class NullSorter implements SBMLExceptionSorter {
		@Override
		public void add(ModelException me) { }

		@Override
		public Map<Category, Collection<ModelException>> getMap() {
			return Collections.emptyMap(); 
		}

		@Override
		public boolean isEmpty() {
			return true;
		}
	}
	static class LiveSorter implements SBMLExceptionSorter {
		Map<SBMLImportException.Category,Collection<ModelException> > map;
		boolean empty = true;
		LiveSorter( ) {
			map = new HashMap<>();
			//load up map collection
			for (SBMLImportException.Category c : SBMLImportException.Category.values()) {
				map.put(c, new ArrayList<ModelException>( ) );
			}
		}

		@Override
		public void add(ModelException me) {
			empty = false;
			map.get(me.importException.category).add(me);
		}

		@Override
		public Map<Category, Collection<ModelException>> getMap() {
			return map; 
		}

		public boolean isEmpty() {
			return empty;
		}
		
	}
	
	
	

}
