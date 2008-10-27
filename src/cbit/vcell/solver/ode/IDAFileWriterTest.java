package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.PrintWriter;

import cbit.vcell.math.*;
import cbit.vcell.solver.Simulation;
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
		
		IDAFileWriter idaFileWriter = new IDAFileWriter(new PrintWriter(System.out), simulation);
		idaFileWriter.write();
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}