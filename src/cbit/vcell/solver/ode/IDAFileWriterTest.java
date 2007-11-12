package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
import java.util.*;
import cbit.vcell.model.*;
import java.io.*;
import cbit.vcell.math.*;
import cbit.vcell.mapping.*;
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
		cbit.vcell.solver.Simulation simulation = new cbit.vcell.solver.Simulation(mathDescription);
		
		IDAFileWriter idaFileWriter = new IDAFileWriter(simulation);
		idaFileWriter.initialize();
		idaFileWriter.writeInputFile(new java.io.PrintWriter (System.out));
		//
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}