package cbit.vcell.solvers;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.parser.Expression;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.math.Constant;
import java.util.Vector;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Function;
import cbit.vcell.solver.Simulation;
import cbit.vcell.math.MathDescriptionTest;
import java.io.File;

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

		Vector <AnnotatedFunction> annotatedFunctionList = FVSolver.createAnnotatedFunctionsList(sims[1]);
		
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