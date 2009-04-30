package org.vcell.sbml.test;
import java.beans.PropertyVetoException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Calendar;

import org.vcell.sbml.SBMLUtils;
import org.vcell.util.document.KeyValue;

import cbit.sql.SimulationVersion;
import cbit.util.xml.VCLogger;
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
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.parser.Expression;
import cbit.vcell.server.SessionLog;
import cbit.vcell.server.StdoutSessionLog;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolver;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solver.test.MathTestingUtilities;
import cbit.vcell.solver.test.SimulationComparisonSummary;
import cbit.vcell.solver.test.VariableComparisonSummary;
import cbit.vcell.util.RowColumnResultSet;


public class SBVCVerifier implements VCDatabaseVisitor {

public SBVCVerifier(){
}

public static void main(java.lang.String[] args) {
	try {
		SBVCVerifier sbvcVerifier = new SBVCVerifier();
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
private static ODESolverResultSet solveSimulation(Simulation sim, boolean hasFastSystems) {

	//
	// If hasFastSystems in true, use Forward Euler solver, else use NativeIDA solver
	//

	ODESolverResultSet odeSolverResultSet = null;
	if (hasFastSystems) {
		// Forward Euler solver

		// reset time step for Forward Euler solver from 0.1 to 1e-4
		double FE_Solver_timeStep = 1e-4;
		try {
			sim.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(FE_Solver_timeStep));
			// sim.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));
		} catch (PropertyVetoException pve) {
			pve.printStackTrace(System.out);
		}
		
        java.io.File directory = new java.io.File("C:\\VCell\\SBMLValidationSuiteTests");
        SessionLog sessionLog = new StdoutSessionLog("VCELL");
        ODESolver odeSolver = null;
        try {
	        odeSolver = new cbit.vcell.solver.ode.ForwardEulerSolver(new SimulationJob(sim, null, 0), directory, sessionLog);
        } catch (SolverException e) {
	        e.printStackTrace(System.out);
	        throw new RuntimeException("Error initializing RK-Fehlberg solver : " + e.getMessage());
        }
        odeSolver.startSolver();
        while (odeSolver.getSolverStatus().isRunning()) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        while (odeSolver.getSolverStatus().getStatus() == SolverStatus.SOLVER_READY
            || odeSolver.getSolverStatus().getStatus() == SolverStatus.SOLVER_RUNNING
            || odeSolver.getSolverStatus().getStatus() == SolverStatus.SOLVER_STARTING) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        if (odeSolver.getSolverStatus().getStatus() == SolverStatus.SOLVER_ABORTED) {
            System.err.println("Simulation failed: " + odeSolver.getSolverStatus());
            System.exit(1);
        }

		// get simulation results 
		odeSolverResultSet = odeSolver.getODESolverResultSet();
	} else {
		// NativeIDA solver
		RowColumnResultSet rcResultSet = null;
		try {
			java.io.StringWriter stringWriter = new java.io.StringWriter();
			IDAFileWriter idaFileWriter = new IDAFileWriter(new PrintWriter(stringWriter,true), sim);
			idaFileWriter.write();
			stringWriter.close();
			StringBuffer buffer = stringWriter.getBuffer();
			String idaInputString = buffer.toString();

			final cbit.vcell.solvers.NativeIDASolver nativeIDASolver = new cbit.vcell.solvers.NativeIDASolver();
			System.out.println(idaInputString);
			rcResultSet = nativeIDASolver.solve(idaInputString);
		} catch (Exception e) {
	        e.printStackTrace(System.out);
	        throw new RuntimeException("Error running NativeIDA solver : " + e.getMessage());
		}

		// get simulation results - copy from RowColumnResultSet into OdeSolverResultSet
		odeSolverResultSet = new ODESolverResultSet();
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
	}
	
	return odeSolverResultSet;
}


public boolean filterBioModel(BioModelInfo bioModelInfo) {
	Calendar calendar = Calendar.getInstance();
	int month = calendar.get(Calendar.MONTH);
	calendar.set(Calendar.MONTH, month-6);
//	if (bioModelInfo.getVersion().getDate().after(calendar.getTime())){
//		System.err.println("BM date : " + bioModelInfo.getVersion().getDate().toString() + ";\t cutoff date : " + calendar.getTime().toString());
	boolean bAllowedUsers = false;
	if (bioModelInfo.getVersion().getOwner().getName().equals("anu")) {
//			|| bioModelInfo.getVersion().getOwner().getName().equals("schaff") ||
//			bioModelInfo.getVersion().getOwner().getName().equals("ion") ||
//			bioModelInfo.getVersion().getOwner().getName().equals("fgao") ||
//			bioModelInfo.getVersion().getOwner().getName().equals("frm") ||
//			bioModelInfo.getVersion().getOwner().getName().equals("liye") ||
//			bioModelInfo.getVersion().getOwner().getName().equals("boris") ) {
		bAllowedUsers = true;
	}
	if (bAllowedUsers && (bioModelInfo.getVersion().getDate().after(calendar.getTime()))) {
		System.err.println("BM name : " + bioModelInfo.getVersion().getName() + "\tdate : " + bioModelInfo.getVersion().getDate().toString());
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
		
		SimulationContext simContexts[] = bioModel_1.getSimulationContexts();
		for (int k = 0; k < simContexts.length; k++){
			if (simContexts[k].getGeometry().getDimension() > 0 || simContexts[k].isStoch()) {
				continue;
			} else {

				// Roundtrip (export -> import) this application to SBML (L2V1) and back ...
				
				// Export this application from the biomodel into SBML

				// First get a structure from the model, and set its size (thro' the structureMapping in the geometry of the simContext)
				// invoke the structureSizeEvaluator to compute and set the sizes of the remaining structures.
				Structure structure = simContexts[k].getModel().getStructures(0);
				double structureSize = 1.0;
				org.vcell.sbml.vcell.StructureSizeSolver ssEvaluator = new org.vcell.sbml.vcell.StructureSizeSolver();
				StructureMapping structMapping = simContexts[k].getGeometryContext().getStructureMapping(structure); 
				ssEvaluator.updateAbsoluteStructureSizes(simContexts[k], structure, structureSize, structMapping.getSizeParameter().getUnitDefinition());

				// now export to SBML
				logFilePrintStream.println("User : " + bioModel_1.getVersion().getOwner().getName() + ";\tBiomodel : " + bioModel_1.getName() + ";\tDate : " + bioModel_1.getVersion().getDate().toString() + ";\tAppln : " + simContexts[k].getName());
//				String exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportSBML(bioModel_1, 2, 1, simContexts[k].getName());
				String exportedSBMLStr = cbit.vcell.xml.XmlHelper.exportSBML(bioModel_1, 2, 1, simContexts[k], null);
				SBMLUtils.writeStringToFile(exportedSBMLStr, "C:\\VCell\\SBML_Testing\\SBMLValidationSuiteTests\\"+bioModel_1.getName()+".xml");

				// Import the exported model
				BioModel bioModel_2 = (BioModel) cbit.vcell.xml.XmlHelper.importSBML(logger, exportedSBMLStr);

				//
		        // Generate math and create a single simulation for the original simContext and for the model that was exported to SBML
		        // and imported back into the VCell. If there is a fast system in the application, use the RungeKatta-Fehlberg solver,
		        // else use the NativeIDA solver.
				//
				double endTime = 1.0;
				int numTimeSteps = 50;

				// 
				// For the original simContext
				// 
		        MathMapping mathMapping_1 = new MathMapping(simContexts[k]);
		        MathDescription mathDesc_1 = mathMapping_1.getMathDescription();
		        simContexts[k].setMathDescription(mathDesc_1);
		        SimulationVersion simVersion_1 = new SimulationVersion(new KeyValue("100"), "sim_1_1", null, null, null, null, null, null, null, null);
		        Simulation sim_11 = new Simulation(simVersion_1, mathDesc_1);
		        sim_11.setName("sim_1_1");
		        sim_11.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0, endTime));
		        TimeStep timeStep_1 = new TimeStep();
		        sim_11.getSolverTaskDescription().setTimeStep(new TimeStep(timeStep_1.getMinimumTimeStep(),timeStep_1.getDefaultTimeStep(),endTime/10000));
		        sim_11.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec((endTime-0)/numTimeSteps));
		        sim_11.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-10, 1e-12));

				// solve the original simulation - if math has fast system, use RK-Fehlberg solver, else use NativeIDA solver
				boolean hasFastSystems = false;
				if (mathDesc_1.hasFastSystems()) {
					hasFastSystems = true;
				}
				ODESolverResultSet odeSolverResultSet_1 = solveSimulation(sim_11, hasFastSystems);

				// 
				// For the imported simContext
				//
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

				// solve the imported simulation - if math has fast system, use RK-Fehlberg solver, else use NativeIDA solver
				hasFastSystems = false;
				if (mathDesc_2.hasFastSystems()) {
					hasFastSystems = true;
				}
				ODESolverResultSet odeSolverResultSet_2 = solveSimulation(sim_12, hasFastSystems);

				//
				// Now compare the 2 result sets - one from simulation from original biomodel, the other from the round-tripped biomodel.
				//

				// If the # of columns in the 2 result sets are not equal, no point comparing
				if (odeSolverResultSet_1.getColumnDescriptionsCount() != odeSolverResultSet_2.getColumnDescriptionsCount()) {
					throw new RuntimeException("The 2 results sets do not match (unequal columns). Cannot compare the 2 simulations");	
				}

				// Get the variable names to compare in the 2 result sets - needed for MathTestingUtilities.compareResultSets()
//				SpeciesContext[] spContexts = simContexts[k].getModel().getSpeciesContexts();
//				String[] varsToCompare = new String[spContexts.length];
//				for (int j = 0; j < spContexts.length; j++){
//					varsToCompare[j] = spContexts[j].getName();
//				}

				String[] varsToCompare = new String[odeSolverResultSet_1.getDataColumnCount()];
				for (int j = 0; j < odeSolverResultSet_1.getDataColumnCount(); j++){
					varsToCompare[j] = odeSolverResultSet_1.getDataColumnDescriptions()[j].getName();
				}

				double absErr = 1e-8;
				double relErr = 1e-8;
				// Compare the result sets ...
				SimulationComparisonSummary simComparisonSummary = MathTestingUtilities.compareResultSets(odeSolverResultSet_2, odeSolverResultSet_1, varsToCompare,TestCaseNew.EXACT,absErr,relErr);

				// Get comparison report ...
				String comparisonReport = printComparisonReport(simComparisonSummary, absErr, relErr);
				logFilePrintStream.println("Comparison Report : \n\n\t" + comparisonReport);
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