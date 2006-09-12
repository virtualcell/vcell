package cbit.vcell.solvers;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.solver.Simulation;

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
		cbit.vcell.biomodel.BioModel model = cbit.vcell.biomodel.BioModelTest.getExample();
		Simulation[] sims = model.getSimulations();

		AnnotatedFunction[] annotatedFunctionList = FVSolver.createAnnotatedFunctionsList(sims[1]);
		
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