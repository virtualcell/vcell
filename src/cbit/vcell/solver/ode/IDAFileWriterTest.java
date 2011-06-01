/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode;
import java.io.PrintWriter;

import cbit.vcell.math.*;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 11:10:26 PM)
 * @author: John Wagner
 */
public class IDAFileWriterTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		MathDescription mathDescription = MathDescriptionTest.getOdeExample();
		Simulation simulation = new Simulation(mathDescription);
		
		IDAFileWriter idaFileWriter = new IDAFileWriter(new PrintWriter(System.out), new SimulationJob(simulation, 0, null));
		idaFileWriter.write();
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
