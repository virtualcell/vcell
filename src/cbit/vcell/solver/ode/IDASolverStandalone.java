package cbit.vcell.solver.ode;

import cbit.vcell.server.SessionLog;
import cbit.vcell.server.PropertyLoader;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.SolverException;
/**
 * Insert the type's description here.
 * Creation date: (10/23/2004 8:07:49 AM)
 * @author: Jim Schaff
 */
public class IDASolverStandalone extends SundialsSolver {
/**
 * IDASolverStandalone constructor comment.
 * @param simulation cbit.vcell.solver.Simulation
 * @param directory java.io.File
 * @param sessionLog cbit.vcell.server.SessionLog
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public IDASolverStandalone(cbit.vcell.solver.SimulationJob simulationJob, java.io.File directory, cbit.vcell.server.SessionLog sessionLog) throws cbit.vcell.solver.SolverException {
	super(simulationJob, directory, sessionLog);
}
/**
 *  This method takes the place of the old runUnsteady()...
 */
protected void initialize() throws cbit.vcell.solver.SolverException {
	SessionLog sessionLog = getSessionLog();
	sessionLog.print("IDASolver.initialize()");
	fireSolverStarting("IDASolver initializing...");
	//
	String inputFilename = getBaseName() + ".idaInput";
	String ExeFilename = getBaseName() + System.getProperty(PropertyLoader.exesuffixProperty);
	//
	sessionLog.print("IDASolver.initialize() baseName = " + getBaseName());
	//
	IDAFileWriter idaFileWriter = new IDAFileWriter(getSimulation());
	try {
		idaFileWriter.initialize();
	} catch (Exception e) {
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, "Could not initialize IDAFileWriter..."));
		e.printStackTrace(System.out);
		throw new SolverException("autocode init exception: " + e.getMessage());
	}
	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, "Generating input file..."));
	fireSolverStarting("generating input file...");
	//
	try {
		java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(inputFilename);
		idaFileWriter.writeInputFile(new java.io.PrintWriter(fileOutputStream));
		fileOutputStream.close();
	} catch (Exception e) {
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, "Could not generate input file: " + e.getMessage()));
		e.printStackTrace(System.out);
		throw new SolverException("solver input file exception: " + e.getMessage());
	}
	//
	//
	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,"IDASolver starting"));	
	
	String executableName = PropertyLoader.getRequiredProperty(PropertyLoader.idaExecutableProperty);
	setMathExecutable(new cbit.vcell.solvers.MathExecutable(executableName + " " + getBaseName() + ".idaInput" + " " + getBaseName() + ".ida"));
}
}
