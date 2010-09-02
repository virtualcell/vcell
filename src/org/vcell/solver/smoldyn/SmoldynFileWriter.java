package org.vcell.solver.smoldyn;


import java.io.File;
import java.io.PrintWriter;

import org.vcell.smoldyn.converter.SimulationJobToSmoldyn;
import org.vcell.smoldyn.inputfile.smoldynwriters.SimulationWriter;

import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverFileWriter;


/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 * Creation date: (6/22/2006 4:22:59 PM)
 * @author: Tracy LI
 */
public class SmoldynFileWriter extends SolverFileWriter 
{

	private File outputFile = null;
	
/**
 * StochFileWriter constructor comment.
 */
public SmoldynFileWriter(PrintWriter pw, File outputFile, SimulationJob arg_simulationJob, boolean bMessaging) 
{
	super(pw, arg_simulationJob, bMessaging);
	this.outputFile = outputFile;
}




@Override
public void write(String[] parameterNames) throws Exception {
	SimulationWriter.write(SimulationJobToSmoldyn.convertSimulationJob(simulationJob, outputFile), printWriter, simulationJob);
}

}