package cbit.vcell.mapping;

import org.vcell.expression.SymbolTableEntry;

import cbit.vcell.math.Variable;

/**
 * Insert the type's description here.
 * Creation date: (5/3/2006 3:47:33 PM)
 * @author: Jim Schaff
 */
public class MathSymbolMapping {
	private java.util.HashMap<SymbolTableEntry,String> biologicalToMathSymbolNameHash = new java.util.HashMap<SymbolTableEntry,String>();
	private java.util.HashMap<SymbolTableEntry,Variable> biologicalToMathHash = new java.util.HashMap<SymbolTableEntry,Variable>();
	private java.util.HashMap<Variable,SymbolTableEntry[]> mathToBiologicalHash = new java.util.HashMap<Variable,SymbolTableEntry[]>();

/**
 * MathSymbolMapping constructor comment.
 */
MathSymbolMapping() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/17/2006 12:38:53 PM)
 * @return java.lang.Object
 */
public cbit.vcell.math.Variable findVariableByName(String variableName) {
	
	java.util.Iterator iter = mathToBiologicalHash.keySet().iterator();
	while (iter.hasNext()){
		cbit.vcell.math.Variable variable = (cbit.vcell.math.Variable)iter.next();
		if(variable.getName().equals(variableName)){
			return variable;
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:49:12 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param var cbit.vcell.math.Variable
 */
public SymbolTableEntry[] getBiologicalSymbol(cbit.vcell.math.Variable var) {
	return (SymbolTableEntry[])mathToBiologicalHash.get(var);

}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:50:22 PM)
 * @return cbit.vcell.math.Variable
 * @param biologicalSymbol cbit.vcell.parser.SymbolTableEntry
 */
public cbit.vcell.math.Variable getVariable(SymbolTableEntry biologicalSymbol) {
	return (cbit.vcell.math.Variable)biologicalToMathHash.get(biologicalSymbol);
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:48:47 PM)
 * @param ste cbit.vcell.parser.SymbolTableEntry
 * @param var cbit.vcell.math.Variable
 */
void put(SymbolTableEntry biologicalSymbol, String varName) {
	if(varName.endsWith("_OUTSIDE") || varName.endsWith("_INSIDE")){
		return;
	}
	String previousVarName = (String)biologicalToMathSymbolNameHash.get(biologicalSymbol);
	if (previousVarName != null && !varName.equals(previousVarName)){
		throw new RuntimeException("biological symbol '"+biologicalSymbol.getName()+"' mapped to two math symbols, '"+varName+"' and '"+previousVarName+"'");
	}
	//System.out.println(cbit.util.BeanUtils.forceStringSize(biologicalSymbol.getName(),25," ",true)+" ---> "+varName);
	biologicalToMathSymbolNameHash.put(biologicalSymbol,varName);
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 4:03:51 PM)
 * @param mathDesc cbit.vcell.math.MathDescription
 */
void reconcileVarNames(cbit.vcell.math.MathDescription mathDesc) {

	//
	// clear secondary hashmaps in case called multiple times.
	//
	biologicalToMathHash.clear();
	mathToBiologicalHash.clear();
	
	java.util.Set keyset = biologicalToMathSymbolNameHash.keySet();
	java.util.Iterator keysetIter = keyset.iterator();
	while (keysetIter.hasNext()){
		SymbolTableEntry biologicalSymbol = (SymbolTableEntry)keysetIter.next();
		String mathVarName = (String)biologicalToMathSymbolNameHash.get(biologicalSymbol);
		cbit.vcell.math.Variable var = mathDesc.getVariable(mathVarName);
		if(var != null){
			biologicalToMathHash.put(biologicalSymbol,var);
			SymbolTableEntry[] previousBiologicalSymbolArr =
				(SymbolTableEntry[])mathToBiologicalHash.put(var,new SymbolTableEntry[] {biologicalSymbol});
			if(previousBiologicalSymbolArr != null){
				SymbolTableEntry[] steArr =
					(SymbolTableEntry[])org.vcell.util.BeanUtils.addElement(previousBiologicalSymbolArr,biologicalSymbol);
				mathToBiologicalHash.put(var,steArr);
			}
		}
	}
}
}
