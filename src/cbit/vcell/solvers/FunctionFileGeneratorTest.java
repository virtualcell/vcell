package cbit.vcell.solvers;
import java.util.Vector;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelTest;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;

/**
 * Insert the type's description here.
 * Creation date: (1/12/2004 4:40:38 PM)
 * @author: Anuradha Lakshminarayana
 */
public class FunctionFileGeneratorTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	// Insert code to start the application here.

	try {
		BioModel model = BioModelTest.getExample();
		Simulation[] sims = model.getSimulations();

		SimulationSymbolTable simulationSymbolTable = new SimulationSymbolTable(sims[1], 0);
		Vector <AnnotatedFunction> annotatedFunctionList = simulationSymbolTable.createAnnotatedFunctionsList(null);
		
		FunctionFileGenerator funcFileGen = new FunctionFileGenerator("\\\\C:\\"+sims[1].getName()+".functions", annotatedFunctionList);
		System.out.println("\n\n FILE NAME : "+funcFileGen.getBasefileName()+"\n\n");

		java.io.FileOutputStream osFunc = null;
		try {
			osFunc = new java.io.FileOutputStream(funcFileGen.getBasefileName());
		}catch (java.io.IOException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("error opening code file '"+funcFileGen.getBasefileName()+": "+e.getMessage());
		}		
		
		funcFileGen.generateFunctionFile();
	} catch (Throwable throwable) {
		throwable.printStackTrace();
	}	
}
}