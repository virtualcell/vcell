package cbit.vcell.solvers;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.solver.*;
import cbit.vcell.geometry.*;
import java.io.*;

import org.vcell.util.PropertyLoader;

import cbit.vcell.math.*;
/**
 * This type was created in VisualAge.
 */
public class FVSolverTest {
/**
 * This method was created by a SmartGuide.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {
	try {
		new PropertyLoader();
		MathDescription mathDescription = null;
		File directoryFile = new File(PropertyLoader.getRequiredProperty(PropertyLoader.tempDirProperty));
		org.vcell.util.SessionLog sessionLog = new org.vcell.util.StdoutSessionLog("unknown");
		new org.vcell.util.PropertyLoader();
		//
		// get current directory
		//
		try {
			if (!directoryFile.isDirectory()) {
				System.err.println("destination-directory '" + directoryFile.getPath() + "' not valid directory");
				System.exit(1);
			}
		} catch (Exception e) {
			System.err.println("PDESolverTest.main() : uncaught exception reading directory '" + directoryFile + "'");
			e.printStackTrace(System.err);
			System.exit(1);
		}

		//
		// check the '-nocompile' flag (just generate the code)
		//
		boolean bNoCompile = false;
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("-nocompile")) {
				throw new Exception("-nocompile currently not supported");
			} else {
				System.err.println("FVSolverTest.main() : 1st argument, expecting optional flag '-nocompile'");
				System.exit(1);
			}
		}

		//
		// read in vcml file and initialize MathDescription
		//
		mathDescription = MathDescriptionTest.getExample();
		cbit.vcell.solver.Simulation simulation = new cbit.vcell.solver.Simulation(mathDescription) {
			public String getSimulationIdentifier() {
				return ("NewSIMULATION");
			}
			public void constantAdded(cbit.vcell.solver.MathOverridesEvent e){
			}
			public void constantRemoved(cbit.vcell.solver.MathOverridesEvent e){
			}
			public void constantChanged(cbit.vcell.solver.MathOverridesEvent e){
			}
			public void clearVersion(){
			}
		};
		System.out.println(mathDescription.getVCML_database());
		new org.vcell.util.PropertyLoader();
		FVSolver solver = new FVSolver(new SimulationJob(simulation, null, 0), directoryFile, sessionLog);
		solver.startSolver();

		while (solver.getSolverStatus().getStatus() != cbit.vcell.solver.SolverStatus.SOLVER_STOPPED){
			try {
				Thread.sleep(1000);
			}catch(InterruptedException e){
			}
		}
	} catch (Exception e) {
		e.printStackTrace(System.err);
	}
}
}
