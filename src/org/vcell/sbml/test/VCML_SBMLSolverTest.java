package org.vcell.sbml.test;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import org.vcell.sbml.SimSpec;
import org.vcell.sbml.copasi.CopasiSBMLSolver;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.StructureSizeSolver;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathException;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class VCML_SBMLSolverTest {
	public static void main(String[] args){
		try {
			String filePrefix = "VCMLTesting";
			File outDir = new File("c:\\temp\\copasi_testing");
			PrintWriter printWriter = new PrintWriter(new FileWriter(new File(outDir,"summary.log")));
			try {
				File dataDir = new File("c:\\temp\\vcml\\les\\");
				File[] vcmlFiles = dataDir.listFiles();
				for (File vcmlFile : vcmlFiles){
					float duration = 10f;
					int numTimeSteps = 100;
					float timeStep = 0.1f;
					try {
						// org.jdom.Element vcmlRoot = XmlUtil.readXML(vcmlFile);
						// String vcmlText = XmlUtil.xmlToString(vcmlRoot);
						XMLSource vcmlSrc = new XMLSource(vcmlFile);
						BioModel bioModel = XmlHelper.XMLToBioModel(vcmlSrc);
						// simulate non-spatial applications (that are not stochastic).
						SimulationContext[] simContexts = bioModel.getSimulationContexts();
						for (SimulationContext simContext : simContexts){
							if (simContext.isStoch() || simContext.getGeometry().getDimension()>0){
								if (simContext.getMathDescription().hasFastSystems()){
									System.out.println("warning: no support for fast systems in our stiff solvers.");
								}
								Structure topStructure = simContext.getModel().getTopFeature();
								StructureMapping.StructureMappingParameter sizeParm = simContext.getGeometryContext().getStructureMapping(topStructure).getSizeParameter();
								if (sizeParm==null || sizeParm.getExpression()==null || sizeParm.getExpression().isZero()){
									StructureSizeSolver structureSizeSolver = new StructureSizeSolver();
									structureSizeSolver.updateAbsoluteStructureSizes(simContext, topStructure, 1000.0, VCUnitDefinition.UNIT_um3);
								}
								SBMLExporter sbmlExporter = new SBMLExporter(bioModel);
//								sbmlExporter.setVcPreferredSimContextName(simContext.getName());
								sbmlExporter.setSelectedSimContext(simContext);
								String sbmlText = sbmlExporter.getSBMLFile();
								CopasiSBMLSolver copasiSBMLSolver = new CopasiSBMLSolver();
								String columnDelimiter = copasiSBMLSolver.getResultsFileColumnDelimiter();
								SimSpec simSpec = SimSpec.fromSBML(sbmlText);
								File resultsFile = copasiSBMLSolver.solve(filePrefix,outDir, sbmlText, simSpec);
								ODESolverResultSet copasiResults = BiomodelsDB_SBMLSolverTest.readResultFile(resultsFile, columnDelimiter); 
								Vector<String> varNames = new Vector<String>();					
								for (int i=1; i<copasiResults.getDataColumnCount();i++){
									varNames.add(copasiResults.getColumnDescriptions(i).getName());
								}
								String[] varsToTest = varNames.toArray(new String[varNames.size()]);
								ODESolverResultSet vcellResults = solveVCell(simContext, numTimeSteps, timeStep, duration);
								SimulationComparisonSummary summary = MathTestingUtilities.compareUnEqualResultSets(copasiResults, vcellResults, varsToTest, 1e-5, 1e-5, 1);
								double maxRelError = summary.getMaxRelativeError();
								if (maxRelError<1){
									printWriter.println(vcmlFile.getName()+" passed, maxRelError="+maxRelError);
								}else{
									VariableComparisonSummary[] vcSummaries = summary.getVariableComparisonSummaries();
									for (VariableComparisonSummary vcSummary : vcSummaries){
										System.out.println(vcSummary.toShortString());
									}
									printWriter.println(vcmlFile.getName()+" failed, maxRelError="+maxRelError);
								}
							}
						}
					}catch (Exception e){
						e.printStackTrace(printWriter);
						printWriter.println("exception while processing file: "+vcmlFile.getName());
					}
					printWriter.flush();
				}
			}finally{
				printWriter.close();
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	

	public static ODESolverResultSet solveVCell(SimulationContext simContext, int stepNumber, float stepSize, float duration) throws Exception{
		//
	    // select only Application, generate math, and create a single Simulation.
		//
	    cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContext);
	    cbit.vcell.math.MathDescription mathDesc = mathMapping.getMathDescription();
	    simContext.setMathDescription(mathDesc);
	    org.vcell.util.document.SimulationVersion simVersion =
	        new org.vcell.util.document.SimulationVersion(
	            new org.vcell.util.document.KeyValue("100"),
	            "simulation1",
	            null,
	            null,
	            null,
	            null,
	            null,
	            null,
	            null,
	            null);
	    cbit.vcell.solver.Simulation sim = new cbit.vcell.solver.Simulation(simVersion, mathDesc);
	    sim.setName("simulation1");
	    // if time factor from SBML is not 1 (i.e., it is not in secs but in minutes or hours), convert endTime to min/hr as : endTime*timeFactor
	    	
	    sim.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0, duration));
	    cbit.vcell.solver.TimeStep timeStep = new cbit.vcell.solver.TimeStep();
	    sim.getSolverTaskDescription().setTimeStep(new cbit.vcell.solver.TimeStep(timeStep.getMinimumTimeStep(),timeStep.getDefaultTimeStep(),duration/10000));
	    sim.getSolverTaskDescription().setOutputTimeSpec(new cbit.vcell.solver.UniformOutputTimeSpec(stepNumber));
	    sim.getSolverTaskDescription().setErrorTolerance(new cbit.vcell.solver.ErrorTolerance(1e-10, 1e-12));

	    //        
		// solve simulation - USING NativeIDASolver ....
		//
		StringWriter stringWriter = new StringWriter();
		SimulationJob simJob = new SimulationJob(sim, 0, null);
		IDAFileWriter idaFileWriter = new IDAFileWriter(new PrintWriter(stringWriter,true), simJob);
		idaFileWriter.write();
		stringWriter.close();
		StringBuffer buffer = stringWriter.getBuffer();
		String idaInputString = buffer.toString();

		final cbit.vcell.solvers.NativeIDASolver nativeIDASolver = new cbit.vcell.solvers.NativeIDASolver();
		System.out.println(idaInputString);
		cbit.vcell.util.RowColumnResultSet rcResultSet = nativeIDASolver.solve(idaInputString);

		//
		// get simulation results - copy from RowColumnResultSet into OdeSolverResultSet
		//
		
		ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
		for (int i = 0; i < rcResultSet.getDataColumnCount(); i++){
			odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(rcResultSet.getColumnDescriptions(i).getName()));
		}
		for (int i = 0; i < rcResultSet.getRowCount(); i++){
			odeSolverResultSet.addRow(rcResultSet.getRow(i));
		}
		//
		// add appropriate Function columns to result set
		//
		SimulationSymbolTable simSymbolTable = simJob.getSimulationSymbolTable();
		Function functions[] = simSymbolTable.getFunctions();
		for (int i = 0; i < functions.length; i++){
			if (SimulationSymbolTable.isFunctionSaved(functions[i])){
				Expression exp1 = new Expression(functions[i].getExpression());
				try {
					exp1 = simSymbolTable.substituteFunctions(exp1);
				} catch (MathException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				} catch (ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				}
				
				try {
					FunctionColumnDescription cd = new FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
					odeSolverResultSet.addFunctionColumn(cd);
				}catch (cbit.vcell.parser.ExpressionException e){
					e.printStackTrace(System.out);
				}
			}
		}
		
		return odeSolverResultSet;
	}
		
}
