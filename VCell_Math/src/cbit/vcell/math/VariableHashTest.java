package cbit.vcell.math;

import org.vcell.expression.ExpressionFactory;

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
		hash.addVariable(new Constant("D",ExpressionFactory.createExpression("4")));
		hash.addVariable(new Constant("E",ExpressionFactory.createExpression("D")));
		hash.addVariable(new Constant("B",ExpressionFactory.createExpression("C")));
		hash.addVariable(new Constant("F",ExpressionFactory.createExpression("A+B+C+E")));
		hash.addVariable(new Constant("C",ExpressionFactory.createExpression("D+5")));
		hash.addVariable(new Constant("A",ExpressionFactory.createExpression("B+C")));
		hash.addVariable(new VolVariable("V1"));
		hash.addVariable(new VolVariable("V2"));
		hash.addVariable(new Function("B1",ExpressionFactory.createExpression("C1+V3")));
		hash.addVariable(new Function("F1",ExpressionFactory.createExpression("A+B+C+E/V1+V2-C1+B1")));
		hash.addVariable(new Function("C1",ExpressionFactory.createExpression("D+5+B1")));
		hash.addVariable(new VolVariable("V3"));

		//
		// unsorted list
		//
		Variable unsortedVars[] = hash.getVariables();
		for (int i = 0; i < unsortedVars.length; i++){
			System.out.println(unsortedVars[i]);
		}
		
		//
		// sorted list
		//
		Variable sortedVars[] = hash.getReorderedVariables();
		for (int i = 0; i < sortedVars.length; i++){
			System.out.println(sortedVars[i]);
		}
		
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
