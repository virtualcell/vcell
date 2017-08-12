package cbit.vcell.mapping;

import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.math.VolVariable;
import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (4/12/2002 4:26:51 PM)
 * @author: Jim Schaff
 */
public class VariableHashTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		VariableHash hash = new VariableHash();
		hash.addVariable(new Constant("D",new Expression("4")));
		hash.addVariable(new Constant("E",new Expression("D")));
		hash.addVariable(new Constant("B",new Expression("C")));
		hash.addVariable(new Constant("F",new Expression("A+B+C+E")));
		hash.addVariable(new Constant("C",new Expression("D+5")));
		hash.addVariable(new Constant("A",new Expression("B+C")));
		Domain domain = null;
		hash.addVariable(new VolVariable("V1",domain));
		hash.addVariable(new VolVariable("V2",domain));
		hash.addVariable(new Function("B1",new Expression("C1+V3"),domain));
		hash.addVariable(new Function("F1",new Expression("A+B+C+E/V1+V2-C1+B1"),domain));
		hash.addVariable(new Function("C1",new Expression("D+5+B1"),domain));
		hash.addVariable(new VolVariable("V3",domain));

//		//
//		// unsorted list
//		//
//		Variable unsortedVars[] = hash.getTopologicallyReorderedVariables();
//		for (int i = 0; i < unsortedVars.length; i++){
//			System.out.println(unsortedVars[i]);
//		}
		
		//
		// Topologically sorted list
		//
		Variable sortedVars[] = hash.getTopologicallyReorderedVariables();
		for (int i = 0; i < sortedVars.length; i++){
			System.out.println(sortedVars[i]);
		}
		//
		// Alphabetically sorted list
		//
		sortedVars = hash.getAlphabeticallyOrderedVariables();
		for (int i = 0; i < sortedVars.length; i++){
			System.out.println(sortedVars[i]);
		}
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
