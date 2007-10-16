package org.vcell.sbml.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbml.libsbml.Model;
import org.sbml.libsbml.SBMLDocument;
import org.sbml.libsbml.SBMLReader;
import org.sbml.libsbml.Species;
import org.vcell.sbml.SBMLUtils;
import org.vcell.sbml.SimSpec;
import org.vcell.sbml.copasi.CopasiSBMLSolver;
import org.vcell.sbml.mathsbml.MathSBMLSolver;
import org.vcell.sbml.vcell.VCellSBMLSolver;

import cbit.util.Executable;
import cbit.util.ExecutableException;
import cbit.util.ExecutableStatus;
import cbit.util.TokenMangler;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.util.xml.XmlUtil;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
import cbit.vcell.solvers.MathExecutable;
import cbit.vcell.util.ColumnDescription;

public class BiomodelsDB_SBMLSolverTest {
	static
	{
		System.loadLibrary("sbmlj");
	}

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
			PrintWriter printWriter = new PrintWriter(new FileWriter(new File(outDir, "summary.log")));
			try {
				printWriter.println(" | *BIOMODEL ID* | *BioModel name* | *PASS* | *Rel Error (VC/COP):(VC/MSBML):(COP/MSBML)* | *Exception* | *Comments* |");
				File[] sbmlFiles = dataDir.listFiles();
				for (File sbmlFile : sbmlFiles){
					StringTokenizer stringTokenizer = new StringTokenizer(sbmlFile.getName(),".",false);
					String biomodelsAccessionNumber = stringTokenizer.nextToken();
					String sbmlModelName = biomodelsAccessionNumber; // should be replaced with  later after reading from SBML file.
					String filePrefix = biomodelsAccessionNumber;
					String bioModelsPrefix = "http://www.ebi.ac.uk/compneur-srv/biomodels-main/publ-model.do?mid=";
					String sbmlLink = "[["+bioModelsPrefix+biomodelsAccessionNumber+"]["+biomodelsAccessionNumber+"]]";
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
						Namespace sbmlNamespace = Namespace.getNamespace("http://www.sbml.org/sbml/level2");
						Element sbmlModelElement = rootSBML.getChild("model",sbmlNamespace);
						sbmlModelName = sbmlModelElement.getAttributeValue("name");
						if (sbmlModelName==null || sbmlModelName.length()==0){
							sbmlModelName = sbmlModelElement.getAttributeValue("id");
						}
						
						SimSpec simSpec = SimSpec.fromSBML(sbmlText);
						String[] varsToTest = simSpec.getVarsList();
						
						
						String passedString = "%NO%";
						String relErrorVCCopasiString = "";
						String relErrorVCMathSBMLString = "";
						String relErrorCopasiMathSBMLString = "";
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
								MathSBMLSolver mathSBMLSolver = new MathSBMLSolver(new File("C:\\Program Files\\Wolfram Research\\Mathematica\\6.0\\mathkernel.exe"));
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
							ODESolverResultSet vcellResults = null;
							try {
								VCellSBMLSolver vcellSBMLSolver = new VCellSBMLSolver();
								vcellSBMLSolver.setRoundTrip(true);
								String columnDelimiter = vcellSBMLSolver.getResultsFileColumnDelimiter();
								File resultFile = vcellSBMLSolver.solve(filePrefix, outDir, sbmlText, simSpec);
								vcellResults = readResultFile(resultFile, columnDelimiter); 
							}catch (Exception e){
								printWriter.println("vcell_simulate failed");
								e.printStackTrace(printWriter);
								System.out.println("vcell_simulate failed");
								e.printStackTrace(System.out);
								combinedErrorBuffer.append(" *VCELL* _"+e.getMessage()+"_ ");
							}
							//
							// compare results from COPASI and VCELL
							//
							if (copasiResults!=null && vcellResults!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(copasiResults, vcellResults, varsToTest, 1e-5, 1e-5);
									double maxRelError = summary.getMaxRelativeError();
									if (maxRelError<1){
										passedString = "%Y%";
										relErrorVCCopasiString = Double.toString(maxRelError);
									}else{
										relErrorVCCopasiString = "*"+Double.toString(maxRelError)+"*";
										VariableComparisonSummary[] vcSummaries = summary.getVariableComparisonSummaries();
										for (VariableComparisonSummary vcSummary : vcSummaries){
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
							// compare results from MathSBML and VCELL
							//
							if (mathSBMLResults!=null && vcellResults!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(mathSBMLResults, vcellResults, varsToTest, 1e-5, 1e-5);
									double maxRelError = summary.getMaxRelativeError();
									if (maxRelError<1){
										relErrorVCMathSBMLString = Double.toString(maxRelError);
										passedString = "%Y%";
									}else{
										relErrorVCMathSBMLString = "*"+Double.toString(maxRelError)+"*";
										VariableComparisonSummary[] vcSummaries = summary.getVariableComparisonSummaries();
										for (VariableComparisonSummary vcSummary : vcSummaries){
											System.out.println(vcSummary.toShortString());
										}
									}
								} catch (Exception e) {
									printWriter.println("COMPARE VC/mSBML failed");
									e.printStackTrace(printWriter);
									System.out.println("COMPARE VC/mSBML failed");
									e.printStackTrace(System.out);
									combinedErrorBuffer.append(" *COMPARE VC/mSBML* _"+e.getMessage()+"_ ");
								}
							}
							//
							// compare results from COPASI and MathSBML
							//
							if (copasiResults!=null && mathSBMLResults!=null){
								try {
									SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(copasiResults, mathSBMLResults, varsToTest, 1e-5, 1e-5);
									double maxRelError = summary.getMaxRelativeError();
									if (maxRelError<1){
										relErrorCopasiMathSBMLString = Double.toString(maxRelError);
//										passedString = "%Y%";
									}else{
										relErrorCopasiMathSBMLString = "*"+Double.toString(maxRelError)+"*";
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
						printWriter.println(" | "+sbmlLink+" | "+sbmlModelName+" | "+passedString+" | ("+relErrorVCCopasiString+") : ("+relErrorVCMathSBMLString+") : ("+relErrorCopasiMathSBMLString+") | "+combinedErrorBuffer.toString()+" | |");
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
		StringTokenizer lineTokenizer = new StringTokenizer(reportText,"\n",false);
		int lineCount = 0;
		while (lineTokenizer.hasMoreElements()){
			lineCount++;
			String line = lineTokenizer.nextToken();
			StringTokenizer columnTokenizer = new StringTokenizer(line,delimiter,false);
			if (lineCount==1){
				while (columnTokenizer.hasMoreTokens()){
					String label = columnTokenizer.nextToken();
					odeResultSet.addDataColumn(new ODESolverResultSetColumnDescription(label));
				}
			}else{
				double[] values = new double[odeResultSet.getDataColumnCount()];
				for (int i = 0; i < values.length; i++) {
					values[i] = Double.parseDouble(columnTokenizer.nextToken());
				}
				odeResultSet.addRow(values);
			}
		}		
		//
		// replace column header for time (force name to "t")
		//
		odeResultSet.getColumnDescriptions()[0] = new ODESolverResultSetColumnDescription("t");
		return odeResultSet;
	}

}
