package org.vcell.sbml.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.SBMLWriter;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.copasi.CopasiSBMLSolver;
import org.vcell.sbml.mathsbml.MathSBMLSolver;
import org.vcell.sbml.vcell.VCellSBMLSolver;
import org.vcell.util.PropertyLoader;

import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServices;
import uk.ac.ebi.www.biomodels_main.services.BioModelsWebServices.BioModelsWebServicesServiceLocator;
import cbit.util.xml.XmlUtil;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;

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
		outputter.setNewlines(true);
		outputter.setLineSeparator("\n");
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
			if (args.length!=1){
				System.out.println("usage: BiomodelsDB_TestSuite outputDirectory");
				System.exit(-1);
			}
			File outDir = new File(args[0]);
			if (!outDir.exists()){
				outDir.mkdirs();
			}
			
			PropertyLoader.loadProperties();
			/**
			 * example properties
			 * 
			 * vcell.COPASI.executable = "C:\\Program Files\\COPASI\\bin\\CopasiSE.exe"
			 * vcell.mathSBML.directory = "c:\\developer\\eclipse\\workspace\\mathsbml\\"
			 * vcell.mathematica.kernel.executable = "C:\\Program Files\\Wolfram Research\\Mathematica\\7.0\\MathKernel.exe"
			 */
			
			File reportFile = new File(outDir,"report.xml");
			ResourceUtil.loadlibSbmlLibray();

			BioModelsWebServicesServiceLocator locator = new BioModelsWebServicesServiceLocator();
			BioModelsWebServices service = locator.getBioModelsWebServices();
			String[] modelIDs = service.getAllCuratedModelsId();
			Arrays.sort(modelIDs);
			
			PrintWriter printWriter = new PrintWriter(new FileWriter(new File(outDir, "summary.log")));
			try {
				printWriter.println(" | *BIOMODEL ID* | *BioModel name* | *PASS* | *Rel Error (VC/COP)(VC/MSBML)(COP/MSBML)* | *Exception* | ");
				for (String modelID : modelIDs){
					String modelName = service.getModelNameById(modelID);
					String modelSBML = service.getModelById(modelID);
					
					Element bioModelInfo = new Element("BioModelInfo");
					bioModelInfo.setAttribute("ID", modelID);
					bioModelInfo.setAttribute("Supported","false");
					bioModelInfo.setAttribute("vcell_ran","false");
					bioModelInfo.setAttribute("COPASI_ran","false");
					bioModelInfo.setAttribute("mSBML_ran","false");
					bioModelInfo.setAttribute("Name", modelName);
					
					boolean bUseUTF8 = true;
					File sbmlFile = new File(outDir,modelID+".sbml");
					XmlUtil.writeXMLStringToFile(modelSBML, sbmlFile.getAbsolutePath(), bUseUTF8);

					PrintStream saved_sysout = System.out;
					PrintStream saved_syserr = System.err;
					PrintStream new_sysout = null;
					PrintStream new_syserr = null;
					try {
						String filePrefix = modelID;
						String sbmlText = modelSBML;
						
						File logFile = new File(outDir,filePrefix+".log");
						new_sysout = new PrintStream(logFile);
						new_syserr = new_sysout;
						System.setOut(new_sysout);
						System.setErr(new_syserr);
						StringBuffer combinedErrorBuffer = new StringBuffer();
						
						SimSpec simSpec = SimSpec.fromSBML(modelSBML);
						String[] varsToTest = simSpec.getVarsList();
						
						printWriter.println("ModelId: " + modelID);
						try {
							//
							// get COPASI solution (time and species concentrations)
							//
							ODESolverResultSet copasiResults = null;
							
							try {
								CopasiSBMLSolver copasiSBMLSolver = new CopasiSBMLSolver();
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
							ODESolverResultSet mSBMLResults = null;
							try {
								MathSBMLSolver mSBMLSolver = new MathSBMLSolver();
								String columnDelimiter = mSBMLSolver.getResultsFileColumnDelimiter();
								SBMLDocument sbmlDocument = new SBMLReader().readSBML(sbmlFile.getAbsolutePath());
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
										bCOPASI_VCELL_matched = false;
										Element solverComparison = new Element("SolverComparison");
										solverComparison.setAttribute("solver1","vcell");
										solverComparison.setAttribute("solver2","COPASI");
										VariableComparisonSummary[] failedVCSummaries = summary.getFailingVariableComparisonSummaries(1e-5, 1e-5);
										for (VariableComparisonSummary vcSummary : failedVCSummaries){
											Element failedVariableSummary = getVariableSummary(vcSummary);
											solverComparison.addContent(failedVariableSummary);
											System.out.println(vcSummary.toShortString());
										}
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
							if ((bCOPASI_VCELL_matched!=null && bCOPASI_VCELL_matched.booleanValue()) ||
								(bmSBML_VCELL_matched!=null && bmSBML_VCELL_matched.booleanValue())){
								bioModelInfo.setAttribute("Supported","true");
							}else{
								bioModelInfo.setAttribute("Supported","false");
							}
						}catch (Exception e){
							e.printStackTrace(printWriter);
							combinedErrorBuffer.append(" *UNKNOWN* _"+e.getMessage()+"_ ");
							bioModelInfo.setAttribute("Supported","false");
							bioModelInfo.setAttribute("exception",e.getMessage());
						}
						printWriter.flush();
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
					write(bioModelInfo,new File(outDir,modelID+"_report.xml")); // write for each model just in case files get corrupted (it happened).
				}
			}finally{
				printWriter.close();
			}
			reportFile = new File(outDir,"report.xml");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		System.exit(0);
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

}
