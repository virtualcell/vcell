/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.langevin;

import java.io.PrintWriter;

import cbit.vcell.messaging.server.StandardSimulationTask;
import cbit.vcell.solver.server.SolverFileWriter;

/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 */
public class LangevinFileWriter extends SolverFileWriter 
{
	private long randomSeed = 0; //value assigned in the constructor
	
public LangevinFileWriter(PrintWriter pw, StandardSimulationTask simTask, boolean bMessaging)
{
	super(pw, simTask, bMessaging);
}

@Override
public void write(String[] parameterNames) throws Exception {	
	String langevinLngvString = LangevinLngvWriter.writeLangevinLngv(simTask.getSimulation(), randomSeed);
	
	printWriter.write(langevinLngvString);
	printWriter.flush();
}

public static void main(String[] args) {
	try {
		
		System.out.println("Done");
	} catch (Exception e) {
		e.printStackTrace();
	}
}


}
