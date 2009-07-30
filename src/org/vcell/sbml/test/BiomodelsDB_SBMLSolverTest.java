package org.vcell.sbml.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.copasi.CopasiSBMLSolver;
import org.vcell.sbml.mathsbml.MathSBMLSolver;
import org.vcell.sbml.vcell.VCellSBMLSolver;
import org.vcell.util.NumberUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;

public class BiomodelsDB_SBMLSolverTest {
	
	public static void main(String[] args){
		
		try {
			if (args.length!=2){
				System.out.println("usage: CopasiSBMLSolver sbmlDirectory outputDirectory");
				System.exit(-1);
			}
			File dataDir = new File(args[0]);
			if (!dataDir.exists()){
				throw new RuntimeException("inputDirectory "+dataDir.getAbsolutePath()+" doesn't exist");
			}
			File outDir = new File(args[1]);
			if (!outDir.exists()){
				outDir.mkdirs();
			}
			ResourceUtil.loadlibSbmlLibray();
			
			PrintWriter printWriter = new PrintWriter(new FileWriter(new File(outDir, "summary.log")));
			try {
				printWriter.println(" | *BIOMODEL ID* | *BioModel name* | *PASS* | *Rel Error (VC/COP)(VC/MSBML)(COP/MSBML)* | *Exception* | ");
				File[] sbmlFiles = dataDir.listFiles();
				for (File sbmlFile : sbmlFiles){
					StringTokenizer stringTokenizer = new StringTokenizer(sbmlFile.getName(),".",false);
					String biomodelsAccessionNumber = stringTokenizer.nextToken();
					String sbmlModelName = biomodelsAccessionNumber; // should be replaced with  later after reading from SBML file.
					String filePrefix = biomodelsAccessionNumber;
					// *** BIOMD_24, 25, 34, 154, 155 : Copasi simulation fails; VCell doesn't import (csymbol not handled); 
					// so ignore model for present *** 
					if (filePrefix.equals("BIOMD0000000024") || filePrefix.equals("BIOMD0000000025") || filePrefix.equals("BIOMD0000000034") || 
						filePrefix.equals("BIOMD0000000154") || filePrefix.equals("BIOMD0000000155") || filePrefix.equals("BIOMD0000000196")) {
						printWriter.println("Ignoring model : " + filePrefix + " because it has <csymbol> delay term nota handled in VCell.");
						continue;
					}
					String bioModelsPrefix = "http://www.ebi.ac.uk/compneur-srv/biomodels-main/publ-model.do?mid=";
					String sbmlLink = "[["+bioModelsPrefix+biomodelsAccessionNumber+"]["+biomodelsAccessionNumber+"]]";
//					String sbmlLink = "[" + biomodelsAccessionNumber + "]";
					PrintStream saved_sysout = System.out;
					PrintStream saved_syserr = System.err;
					PrintStream new_sysout = null;
					PrintStream new_syserr = null;
					try {
						File logFile = new File(outDir,filePrefix+".log");
						new_sysout = new PrintStream(logFile);
						new_syserr = new_sysout;
						System.setOut(new_sysout);
						System.setErr(new_syserr);
						StringBuffer combinedErrorBuffer = new StringBuffer();
						String sbmlText = XmlUtil.getXMLString(sbmlFile.getAbsolutePath());
						//
						// get SBML model "name" (or "id")
						//
						Element rootSBML = SBMLUtils.readXML(new StringReader(sbmlText));
						Namespace sbmlNamespace = rootSBML.getNamespace();
						// Namespace sbmlNamespace = Namespace.getNamespace("http://www.sbml.org/sbml/level2");
						Element sbmlModelElement = rootSBML.getChild("model",sbmlNamespace);
						sbmlModelName = sbmlModelElement.getAttributeValue("name");
						if (sbmlModelName==null || sbmlModelName.length()==0){
							sbmlModelName = sbmlModelElement.getAttributeValue("id");
						}
						
						SimSpec simSpec = SimSpec.fromSBML(sbmlText);
						String[] varsToTest = simSpec.getVarsList();
						
						
						String cv_passedString = "%NO%";	// Copasi, VCell roundtrip
						String mv_passedString = "%NO%";	// MathSBML, Vcell roundtrip
						String cm_passedString = "%NO%";	// Copasi, MathSBML roundtrip
//						String rt_passedString = "%NO%";	// Vcell, Vcell roundtrip
						
						String relErrorVCCopasiString = "";
						String relErrorVCMathSBMLString = "";
						String relErrorCopasiMathSBMLString = "";
//						String relErrorVCRoundtripString = "";
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
							}catch (Exception e){
								printWriter.println("Copasi solve() failed");
								e.printStackTrace(printWriter);
								System.out.println("Copasi solve() failed");
								e.printStackTrace(System.out);
								combinedErrorBuffer.append(" *COPASI* _"+e.getMessage()+"_ ");
							}

							//
							// get mathSBML solution (time and species concentrations)
							//
							ODESolverResultSet mathSBMLResults = null;
							try {
								MathSBMLSolver mathSBMLSolver = new MathSBMLSolver(new File("C:\\Program Files\\Wolfram Research\\Mathematica\\7.0\\mathkernel.exe"));
								String columnDelimiter = mathSBMLSolver.getResultsFileColumnDelimiter();
								File resultFile = mathSBMLSolver.solve(filePrefix, outDir, sbmlText, simSpec);
								mathSBMLResults = readResultFile(resultFile, columnDelimiter); 
							}catch (Exception e){
								printWriter.println("mathSBML solve() failed");
								e.printStackTrace(printWriter);
								System.out.println("mathSBML solve() failed");
								e.printStackTrace(System.out);
								combinedErrorBuffer.append(" *mathSBML* _"+e.getMessage()+"_ ");
							}

							//
							// get VCell solution (time and species concentrations)
							//
//							ODESolverResultSet vcellResults = null;
//							try {
//								VCellSBMLSolver vcellSBMLSolver = new VCellSBMLSolver();
//								vcellSBMLSolver.setRoundTrip(false);
//								String columnDelimiter = vcellSBMLSolver.getResultsFileColumnDelimiter();
//								File resultFile = vcellSBMLSolver.solve(filePrefix, outDir, sbmlText, simSpec);
//								vcellResults = readResultFile(resultFile, columnDelimiter); 
//							}catch (Exception e){
//								printWriter.println("vcell solve() failed");
//								e.printStackTrace(printWriter);
//								System.out.println("vcell solve() failed");
//								e.printStackTrace(System.out);
//								combinedErrorBuffer.append(" *VCELL* _"+e.getMessage()+"_ ");
//							}

							//
							// get VCell solution with an embedded "ROUND TRIP" (time and species concentrations)
							//
							ODESolverResultSet vcellResults_RT = null;
							try {
								VCellSBMLSolver vcellSBMLSolver_RT = new VCellSBMLSolver();
								vcellSBMLSolver_RT.setRoundTrip(true);
								String columnDelimiter = vcellSBMLSolver_RT.getResultsFileColumnDelimiter();
								File resultFile = vcellSBMLSolver_RT.solve(filePrefix, outDir, sbmlText, simSpec);
								vcellResults_RT = readResultFile(resultFile, columnDelimiter); 
							}catch (Exception e){
								printWriter.println("vcell solve(roundtrip=true) failed");
								e.printStackTrace(printWriter);
								System.out.println("vcell solve(roundtrip=true) failed");
								e.printStackTrace(System.out);
								combinedErrorBuffer.append(" *VCELL_RT* _"+e.getMessage()+"_ ");
							}
							//
							// compare results from COPASI and VCELL_RT
							//
							if (copasiResults!=null && vcellResults_RT!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareResultSets(copasiResults, vcellResults_RT, varsToTest, TestCaseNew.REGRESSION, 1e-5, 1e-5);
									double maxRelError = summary.getMaxRelativeError();
									if (maxRelError<1){
										cv_passedString = "%Y%";
										relErrorVCCopasiString = NumberUtils.formatNumber(maxRelError,3);
									}else{
										relErrorVCCopasiString = "*"+NumberUtils.formatNumber(maxRelError,3)+"*";
//										VariableComparisonSummary[] vcSummaries = summary.getVariableComparisonSummaries();
										VariableComparisonSummary[] failedVCSummaries = summary.getFailingVariableComparisonSummaries(1e-5, 1e-5);
										for (VariableComparisonSummary vcSummary : failedVCSummaries){
											System.out.println(vcSummary.toShortString());
										}
									}
								} catch (Exception e) {
									printWriter.println("COMPARE VC/COPASI failed");
									e.printStackTrace(printWriter);
									System.out.println("COMPARE VC/COPASI failed");
									e.printStackTrace(System.out);
									combinedErrorBuffer.append(" *COMPARE VC/COPASI* _"+e.getMessage()+"_ ");
								}
							}
							//
							// compare results from VCELL and VCELL_RT
							//
//							if (vcellResults!=null && vcellResults_RT!=null){
//								try {
//									SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(vcellResults, vcellResults_RT, varsToTest, 1e-5, 1e-5);
//									double maxRelError = summary.getMaxRelativeError();
//									if (maxRelError<1){
//										relErrorVCRoundtripString = NumberUtils.formatNumber(maxRelError,3);
//										rt_passedString = "%Y%";
//									}else{
//										relErrorVCRoundtripString = "*"+NumberUtils.formatNumber(maxRelError,3)+"*";
//										VariableComparisonSummary[] vcSummaries = summary.getVariableComparisonSummaries();
//										for (VariableComparisonSummary vcSummary : vcSummaries){
//											System.out.println(vcSummary.toShortString());
//										}
//									}
//								} catch (Exception e) {
//									printWriter.println("COMPARE VC/VCRT failed");
//									e.printStackTrace(printWriter);
//									System.out.println("COMPARE VC/VCRT failed");
//									e.printStackTrace(System.out);
//									combinedErrorBuffer.append(" *COMPARE VC/VCRT* _"+e.getMessage()+"_ ");
//								}
//							}
							//
							// compare results from MathSBML and VCELL
							//
							if (mathSBMLResults!=null && vcellResults_RT!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(mathSBMLResults, vcellResults_RT, varsToTest, 1e-5, 1e-5, 1);
									double maxRelError = summary.getMaxRelativeError();
									if (maxRelError<1){
										relErrorVCMathSBMLString = NumberUtils.formatNumber(maxRelError,3);
										mv_passedString = "%Y%";
									}else{
										relErrorVCMathSBMLString = "*"+NumberUtils.formatNumber(maxRelError,3)+"*";
//										VariableComparisonSummary[] vcSummaries = summary.getVariableComparisonSummaries();
										VariableComparisonSummary[] failedVCSummaries = summary.getFailingVariableComparisonSummaries(1e-5, 1e-5);
										for (VariableComparisonSummary vcSummary : failedVCSummaries){
											System.out.println(vcSummary.toShortString());
										}
									}
								} catch (Exception e) {
									printWriter.println("COMPARE VCRT/mSBML failed");
									e.printStackTrace(printWriter);
									System.out.println("COMPARE VCRT/mSBML failed");
									e.printStackTrace(System.out);
									combinedErrorBuffer.append(" *COMPARE VCRT/mSBML* _"+e.getMessage()+"_ ");
								}
							}
							//
							// compare results from COPASI and MathSBML
							//
							if (copasiResults!=null && mathSBMLResults!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(copasiResults, mathSBMLResults, varsToTest, 1e-5, 1e-5, 1);
									double maxRelError = summary.getMaxRelativeError();
									if (maxRelError<1){
										relErrorCopasiMathSBMLString = NumberUtils.formatNumber(maxRelError,3);
										cm_passedString = "%Y%";
									}else{
										relErrorCopasiMathSBMLString = "*"+NumberUtils.formatNumber(maxRelError,3)+"*";
										VariableComparisonSummary[] vcSummaries = summary.getVariableComparisonSummaries();
										for (VariableComparisonSummary vcSummary : vcSummaries){
											System.out.println(vcSummary.toShortString());
										}
									}
								} catch (Exception e) {
									printWriter.println("COMPARE COPASI/mSBML failed");
									e.printStackTrace(printWriter);
									System.out.println("COMPARE COPASI/mSBML failed");
									e.printStackTrace(System.out);
									combinedErrorBuffer.append(" *COMPARE COPASI/mSBML* _"+e.getMessage()+"_ ");
								}
							}
						}catch (Exception e){
							e.printStackTrace(printWriter);
							combinedErrorBuffer.append(" *UNKNOWN* _"+e.getMessage()+"_ ");
						}
//						printWriter.println(" | "+sbmlLink+" | "+sbmlModelName+" | "+passedString+" | ("+relErrorVCCopasiString+") | "+combinedErrorBuffer.toString()+" | |");
						printWriter.println(" | "+sbmlLink+" | "+sbmlModelName+" | "+cv_passedString+" "+mv_passedString+" "+cm_passedString+" | ("+relErrorVCCopasiString+")("+relErrorVCMathSBMLString+")("+relErrorCopasiMathSBMLString+") | "+combinedErrorBuffer.toString()+" |");
//						printWriter.println(" | "+sbmlLink+" | "+sbmlModelName+" | "+cv_passedString+" | ("+relErrorVCCopasiString+")("+relErrorVCMathSBMLString+")("+relErrorCopasiMathSBMLString+") | "+combinedErrorBuffer.toString()+" |");
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
				}
			}finally{
				printWriter.close();
			}
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
