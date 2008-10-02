package cbit.vcell.solver.ode;

import cbit.vcell.server.PropertyLoader;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.SolverException;
/**
 * Insert the type's description here.
 * Creation date: (10/23/2004 8:07:49 AM)
 * @author: Jim Schaff
 */
public class CVodeSolverStandalone extends SundialsSolver {
/**
 * IDASolverStandalone constructor comment.
 * @param simulation cbit.vcell.solver.Simulation
 * @param directory java.io.File
 * @param sessionLog cbit.vcell.server.SessionLog
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public CVodeSolverStandalone(cbit.vcell.solver.SimulationJob simulationJob, java.io.File directory, cbit.vcell.server.SessionLog sessionLog) throws cbit.vcell.solver.SolverException {
	super(simulationJob, directory, sessionLog);
}
/**
 *  This method takes the place of the old runUnsteady()...
 */
protected void initialize() throws cbit.vcell.solver.SolverException {	
	fireSolverStarting("CVODE solver initializing...");
	super.initialize();

	String inputFilename = getBaseName() + CVODEINPUT_DATA_EXTENSION;
	String outputFilename = getBaseName() + IDA_DATA_EXTENSION;

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, "CVODE solver generating input file..."));
	fireSolverStarting("CVODE solver generating input file...");

	try {
		CVodeFileWriter cvodeFileWriter = new CVodeFileWriter(getSimulation(), getJobIndex(), true);
		java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(inputFilename);
		cvodeFileWriter.writeInputFile(new java.io.PrintWriter(fileOutputStream));
		fileOutputStream.close();
	} catch (Exception e) {
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, "CVODE solver could not generate input file: " + e.getMessage()));
		e.printStackTrace(System.out);
		throw new SolverException("CVODE solver could not generate input file: " + e.getMessage());
	}

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,"CVODE solver starting"));	
	
	String executableName = PropertyLoader.getRequiredProperty(PropertyLoader.sundialsSolverExecutableProperty);
	setMathExecutable(new cbit.vcell.solvers.MathExecutable(executableName + " " + inputFilename + " " + outputFilename));
}
}
