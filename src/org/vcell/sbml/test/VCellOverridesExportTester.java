package org.vcell.sbml.test;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import org.vcell.util.document.KeyValue;

import cbit.sql.SimulationVersion;
import cbit.util.BeanUtils;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.mathmodel.MathModelInfo;
import cbit.vcell.model.Structure;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
import cbit.vcell.util.RowColumnResultSet;


public class VCellOverridesExportTester implements VCDatabaseVisitor {

public VCellOverridesExportTester(){
}

public static void main(java.lang.String[] args) {
	try {
		VCellOverridesExportTester sbvcVerifier = new VCellOverridesExportTester();
		boolean bAbortOnDataAccessException = false;
		VCDatabaseScanner.scanBioModels(args, sbvcVerifier, bAbortOnDataAccessException);
	} catch (Throwable exception) {
		System.out.println("Exception occurred in main() of SBVCVerifier");
		exception.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
}


/**
 * Scans biomodels in the database and retrieves the applications that are 
 */
private String printComparisonReport(SimulationComparisonSummary simCompSummary, double absError, double relError) {

	StringBuffer reportStrBuffer = new StringBuffer();
	
	// Get all the variable comparison summaries and the failed ones from simComparisonSummary to print out report.
	VariableComparisonSummary[] failedVarSummaries = simCompSummary.getFailingVariableComparisonSummaries(absError, relError);
	VariableComparisonSummary[] allVarSummaries = simCompSummary.getVariableComparisonSummaries();

	if (failedVarSummaries.length>0){
		// Failed simulation
		reportStrBuffer.append("\t\tTolerance test FAILED \n");
		reportStrBuffer.append("\t\tFailed Variables : \n");
		for (int m = 0; m < failedVarSummaries.length; m++){
			reportStrBuffer.append("\t\t\t"+failedVarSummaries[m].toShortString()+"\n");
		}							
	} else {
		reportStrBuffer.append("\t\tTolerance test PASSED \n");
	}

	reportStrBuffer.append("\t\tPassed Variables : \n");
	// Check if varSummary exists in failed summaries list. If not, simulation passed.
	for (int m = 0; m < allVarSummaries.length; m++) {
		if (!cbit.util.BeanUtils.arrayContains(failedVarSummaries, allVarSummaries[m])) {
			reportStrBuffer.append("\t\t\t"+allVarSummaries[m].toShortString()+"\n");
		}
	}
	
	return reportStrBuffer.toString();
}




/**
 * Scans biomodels in the database and retrieves the applications that are 
 */
private static ODESolverResultSet solveSimulation(Simulation sim) {
	// NativeIDA solver
	RowColumnResultSet rcResultSet = null;
	try {
		StringWriter stringWriter = new StringWriter();
		IDAFileWriter idaFileWriter = new IDAFileWriter(new PrintWriter(stringWriter,true), sim);
		idaFileWriter.write();
		stringWriter.close();
		StringBuffer buffer = stringWriter.getBuffer();
		String idaInputString = buffer.toString();

		final cbit.vcell.solvers.NativeIDASolver nativeIDASolver = new cbit.vcell.solvers.NativeIDASolver();
		rcResultSet = nativeIDASolver.solve(idaInputString);
	} catch (Exception e) {
        e.printStackTrace(System.out);
        throw new RuntimeException("Error running NativeIDA solver : " + e.getMessage());
	}

	// get simulation results - copy from RowColumnResultSet into OdeSolverResultSet
	if (rcResultSet != null) {
		ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
		for (int i = 0; i < rcResultSet.getDataColumnCount(); i++){
			odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(rcResultSet.getColumnDescriptions(i).getName()));
		}
		for (int i = 0; i < rcResultSet.getRowCount(); i++){
			odeSolverResultSet.addRow(rcResultSet.getRow(i));
		}
		// add appropriate Function columns to result set
		cbit.vcell.math.Function functions[] = sim.getFunctions();
		for (int i = 0; i < functions.length; i++){
			if (cbit.vcell.solvers.AbstractSolver.isFunctionSaved(functions[i])){
				Expression exp1 = new Expression(functions[i].getExpression());
				try {
					exp1 = sim.substituteFunctions(exp1);
				} catch (cbit.vcell.math.MathException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
				} catch (cbit.vcell.parser.ExpressionException e) {
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
	} else {
		return null;
	}
}


public boolean filterBioModel(BioModelInfo bioModelInfo) {
	if (bioModelInfo.getVersion().getOwner().getName().equals("anu")) {
		// System.err.println("BM name : " + bioModelInfo.getVersion().getName() + "\tdate : " + bioModelInfo.getVersion().getDate().toString());
		return true;
	}
	return false;
}


public boolean filterGeometry(GeometryInfo geometryInfo) {
	// TODO Auto-generated method stub
	return false;
}


public void visitBioModel(BioModel bioModel_1, PrintStream logFilePrintStream) {
	try {
		//
		// Check each application to see if it is a compartmental application. Grab only the compartmental simContexts.
		//

		// inline a VCLogger - to be used later while importing an exported biomodel.
	    cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
	        private StringBuffer buffer = new StringBuffer();
	        public void sendMessage(int messageLevel, int messageType) {
	            String message = cbit.util.xml.VCLogger.getDefaultMessage(messageType);
	            sendMessage(messageLevel, messageType, message);	
	        }
	        public void sendMessage(int messageLevel, int messageType, String message) {
	            System.out.println("LOGGER: msgLevel="+messageLevel+", msgType="+messageType+", "+message);
	            if (messageLevel==VCLogger.HIGH_PRIORITY){
	            	throw new RuntimeException("SBML Import Error: "+message);
	            }
	        }
	        public void sendAllMessages() {
	        }
	        public boolean hasMessages() {
	            return false;
	        }
	    };
		
//	    if (!bioModel_1.getName().equals("ParamScanExportTest")) {
//	    	return;
//	    }
		SimulationContext simContexts[] = bioModel_1.getSimulationContexts();
		for (int k = 0; k < simContexts.length; k++){
			if (simContexts[k].getGeometry().getDimension() > 0 || simContexts[k].isStoch()) {
				continue;
			} else {

				/* Roundtrip (export -> import) this application to SBML (L2V1) and back ... Export this application from the biomodel into SBML */

				// Find out if the sizes of compartments are set. If not, get a structure from the model, and set its size 
				// (thro' the structureMapping in the geometry of the simContext); invoke the structureSizeEvaluator 
				// to compute and set the sizes of the remaining structures.
				if (simContexts[k].getGeometryContext().isAllSizeSpecifiedNull() && simContexts[k].getGeometryContext().isAllVolFracAndSurfVolSpecified()) {
					Structure structure = simContexts[k].getModel().getStructures(0);
					double structureSize = 1.0;
					org.vcell.sbml.vcell.StructureSizeSolver ssEvaluator = new org.vcell.sbml.vcell.StructureSizeSolver();
					StructureMapping structMapping = simContexts[k].getGeometryContext().getStructureMapping(structure); 
					ssEvaluator.updateAbsoluteStructureSizes(simContexts[k], structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());
				}
				// Generate math and create a single simulation for the original simContext and for the model that was exported to SBML
		        // and imported back into the VCell. Use the NativeIDA solver.
				double endTime = 1.0;
				int numTimeSteps = 50;
				
				// now export to SBML
				logFilePrintStream.println("User : " + bioModel_1.getVersion().getOwner().getName() + ";\tBiomodel : " + bioModel_1.getName() + ";\tDate : " + bioModel_1.getVersion().getDate().toString() + ";\tAppln : " + simContexts[k].getName());
				Simulation[] sims = bioModel_1.getSimulations(simContexts[k]);
				String exportedSBMLStr = null;
				BioModel bioModel_2 = null;
				
				if (sims == null || sims.length == 0) { 
					exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportSBML(bioModel_1, 2, 1, simContexts[k], null);
					//exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportSBML(bioModel_1, 2, 1, simContexts[k].getName());
					XmlUtil.writeXMLString(exportedSBMLStr, "C:\\VCell\\SBML_Testing\\SBMLValidationSuiteTests\\SBMLOverridesExportTests\\" + bioModel_1.getName()+"_"+simContexts[k].getName()+".xml");
					// Import the exported model
					bioModel_2 = (BioModel) cbit.vcell.xml.XmlHelper.importSBML(logger, exportedSBMLStr);
					// Create a simulation from mathmapping, mathDescription, simContext for the original model.
					// For the original simContext
			        MathMapping mathMapping_1 = new MathMapping(simContexts[k]);
			        MathDescription mathDesc_1 = mathMapping_1.getMathDescription();
			        simContexts[k].setMathDescription(mathDesc_1);
					// MathDescription mathDesc_1 = simContexts[k].getMathDescription();
			        SimulationVersion simVersion_1 = new SimulationVersion(new KeyValue("100"), "sim_1_1", null, null, null, null, null, null, null, null);
			        Simulation sim_11 = new Simulation(simVersion_1, mathDesc_1);
			        sim_11.setName("sim_1_1");
			        sim_11.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0, endTime));
			        TimeStep timeStep_1 = new TimeStep();
			        sim_11.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_1.getMinimumTimeStep(),timeStep_1.getDefaultTimeStep(),endTime/10000));
			        sim_11.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/numTimeSteps));
			        sim_11.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));
					
					ODESolverResultSet odeSolverResultSet_1 = solveSimulation(sim_11);

					// For the imported simContext
					SimulationContext simContext_2 = bioModel_2.getSimulationContexts(0);
			        MathMapping mathMapping_2 = new MathMapping(simContext_2);
			        MathDescription mathDesc_2 = mathMapping_2.getMathDescription();
			        simContext_2.setMathDescription(mathDesc_2);
			        SimulationVersion simVersion_2 = new SimulationVersion(new KeyValue("100"), "sim_1_2", null, null, null, null, null, null, null, null);
			        Simulation sim_12 = new Simulation(simVersion_2, mathDesc_2);
			        sim_12.setName("sim_1_2");
			        sim_12.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0, endTime));
			        TimeStep timeStep_2 = new TimeStep();
			        sim_12.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_2.getMinimumTimeStep(),timeStep_2.getDefaultTimeStep(),endTime/10000));
			        sim_12.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/numTimeSteps));
			        sim_12.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));
			        // solve the 'roundtipped' model's simulation.
					ODESolverResultSet odeSolverResultSet_2 = solveSimulation(sim_12);

					// Now compare the 2 result sets - one from simulation from original biomodel, the other from the round-tripped biomodel.
					// If the # of columns in the 2 result sets are not equal, no point comparing
					// 	if (odeSolverResultSet_1.getColumnDescriptionsCount() != odeSolverResultSet_2.getColumnDescriptionsCount()) {
					// 		throw new RuntimeException("The 2 results sets do not match (unequal columns). Cannot compare the 2 simulations");	
					// 	}

					Vector<String> varsVector = new Vector<String>();
					for (int j = 0; j < odeSolverResultSet_1.getDataColumnCount(); j++){
						if (odeSolverResultSet_1.getDataColumnDescriptions()[j].getName().equals("t")) {
							continue;
						}
						varsVector.addElement(odeSolverResultSet_1.getDataColumnDescriptions()[j].getName());
					}
					for (int j = 0; j < odeSolverResultSet_2.getDataColumnCount(); j++){
						String varName = odeSolverResultSet_2.getDataColumnDescriptions()[j].getName(); 
						if (varName.equals("t")) {
							continue;
						}
						if (!varsVector.contains(varName)) {
							varsVector.addElement(varName);
						}
					}
					String[] varsToCompare = (String[])BeanUtils.getArray(varsVector, String.class);

					double absErr = 1e-8;
					double relErr = 1e-8;
					// Compare the result sets ...
					SimulationComparisonSummary simComparisonSummary = MathTestingUtilities.compareResultSets(odeSolverResultSet_2, odeSolverResultSet_1, varsToCompare,TestCaseNew.EXACT,absErr,relErr);

					// Get comparison report ...
					String comparisonReport = printComparisonReport(simComparisonSummary, absErr, relErr);
					logFilePrintStream.println("Comparison Report : \n\n\t" + comparisonReport);
					
				} else {
					for (int i = 0; i < sims.length; i++) {
						for (int j = 0; j < sims[i].getScanCount(); j++) {
							logFilePrintStream.println("Simulation : " + sims[i].getName() + ":\tscan # : " + j);
							SimulationJob simJob = new SimulationJob(sims[i], null, j);
							exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportSBML(bioModel_1, 2, 1, simContexts[k], simJob);
							//exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportSBML(bioModel_1, 2, 1, simContexts[k].getName());
							XmlUtil.writeXMLString(exportedSBMLStr, "C:\\SBMLRelated\\SBML_Testing\\SBMLValidationSuiteTests\\SBMLOverridesExportTests\\Alpha_Results_4_22_08\\" + bioModel_1.getName()+"_"+simContexts[k].getName()+"_"+sims[i].getName()+"_"+j+".xml");
							// Import the exported model
							bioModel_2 = (BioModel) cbit.vcell.xml.XmlHelper.importSBML(logger, exportedSBMLStr);
							
							// solve the original vcell model simulation.
							simJob.getWorkingSim().getSolverTaskDescription().setSolverDescription(SolverDescription.IDA);
							simJob.getWorkingSim().getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0, endTime));
					        TimeStep timeStep_1 = new TimeStep();
					        simJob.getWorkingSim().getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_1.getMinimumTimeStep(),timeStep_1.getDefaultTimeStep(),endTime/10000));
					        simJob.getWorkingSim().getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/numTimeSteps));
					        simJob.getWorkingSim().getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));
							ODESolverResultSet odeSolverResultSet_1 = solveSimulation(simJob.getWorkingSim());
	
							// For the imported simContext, create new simulation
							SimulationContext simContext_2 = bioModel_2.getSimulationContexts(0);
					        MathMapping mathMapping_2 = new MathMapping(simContext_2);
					        MathDescription mathDesc_2 = mathMapping_2.getMathDescription();
					        simContext_2.setMathDescription(mathDesc_2);
					        SimulationVersion simVersion_2 = new SimulationVersion(new KeyValue("100"), "sim_1_2", null, null, null, null, null, null, null, null);
					        Simulation sim_12 = new Simulation(simVersion_2, mathDesc_2);
					        sim_12.setName("sim_1_2");
					        sim_12.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0, endTime));
					        TimeStep timeStep_2 = new TimeStep();
					        sim_12.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_2.getMinimumTimeStep(),timeStep_2.getDefaultTimeStep(),endTime/10000));
					        sim_12.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/numTimeSteps));
					        sim_12.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));
					        // solve the 'roundtipped' model's simulation.
							ODESolverResultSet odeSolverResultSet_2 = solveSimulation(sim_12);
	
							// Now compare the 2 result sets - one from simulation from original biomodel, the other from the round-tripped biomodel.
							// If the # of columns in the 2 result sets are not equal, no point comparing
							// 	if (odeSolverResultSet_1.getColumnDescriptionsCount() != odeSolverResultSet_2.getColumnDescriptionsCount()) {
							// 		throw new RuntimeException("The 2 results sets do not match (unequal columns). Cannot compare the 2 simulations");	
							// 	}

							Vector<String> varsVector = new Vector<String>();
							for (int rs = 0; rs < odeSolverResultSet_1.getDataColumnCount(); rs++){
								if (odeSolverResultSet_1.getDataColumnDescriptions()[rs].getName().equals("t")) {
									continue;
								}
								varsVector.addElement(odeSolverResultSet_1.getDataColumnDescriptions()[rs].getName());
							}
							for (int rs = 0; rs < odeSolverResultSet_2.getDataColumnCount(); rs++){
								String varName = odeSolverResultSet_2.getDataColumnDescriptions()[rs].getName(); 
								if (varName.equals("t")) {
									continue;
								}
								if (!varsVector.contains(varName)) {
									varsVector.addElement(varName);
								}
							}
							String[] varsToCompare = (String[])BeanUtils.getArray(varsVector, String.class);
	
							double absErr = 1e-8;
							double relErr = 1e-8;
							// Compare the result sets ...
							SimulationComparisonSummary simComparisonSummary = MathTestingUtilities.compareResultSets(odeSolverResultSet_2, odeSolverResultSet_1, varsToCompare,TestCaseNew.EXACT,absErr,relErr);
	
							// Get comparison report ...
							String comparisonReport = printComparisonReport(simComparisonSummary, absErr, relErr);
							logFilePrintStream.println("Comparison Report : \n\n\t" + comparisonReport);
						}	// end for - sims.scancount
					}	// end for sims
				}
			} // else
		} // for - simcontexts - k

	}catch (Throwable e){
        e.printStackTrace(logFilePrintStream);
	}
}


public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
	// TODO Auto-generated method stub
	
}

// required for interface implementation

public boolean filterMathModel(MathModelInfo mathModelInfo) {
	return false;
}
public void visitMathModel(MathModel mathModel, PrintStream logFilePrintStream) {
}


}