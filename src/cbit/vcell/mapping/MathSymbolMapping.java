package cbit.vcell.mapping;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2006 3:47:33 PM)
 * @author: Jim Schaff
 */
public class MathSymbolMapping {
	private java.util.HashMap biologicalToMathSymbolNameHash = new java.util.HashMap();
	private java.util.HashMap biologicalToMathHash = new java.util.HashMap();
	private java.util.HashMap mathToBiologicalHash = new java.util.HashMap();

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
public cbit.vcell.parser.SymbolTableEntry[] getBiologicalSymbol(cbit.vcell.math.Variable var) {
	return (cbit.vcell.parser.SymbolTableEntry[])mathToBiologicalHash.get(var);

}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:50:22 PM)
 * @return cbit.vcell.math.Variable
 * @param biologicalSymbol cbit.vcell.parser.SymbolTableEntry
 */
public cbit.vcell.math.Variable getVariable(cbit.vcell.parser.SymbolTableEntry biologicalSymbol) {
	return (cbit.vcell.math.Variable)biologicalToMathHash.get(biologicalSymbol);
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:48:47 PM)
 * @param ste cbit.vcell.parser.SymbolTableEntry
 * @param var cbit.vcell.math.Variable
 */
void put(cbit.vcell.parser.SymbolTableEntry biologicalSymbol, String varName) {
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
		cbit.vcell.parser.SymbolTableEntry biologicalSymbol = (cbit.vcell.parser.SymbolTableEntry)keysetIter.next();
		String mathVarName = (String)biologicalToMathSymbolNameHash.get(biologicalSymbol);
		cbit.vcell.math.Variable var = mathDesc.getVariable(mathVarName);
		if(var != null){
			biologicalToMathHash.put(biologicalSymbol,var);
			cbit.vcell.parser.SymbolTableEntry[] previousBiologicalSymbolArr =
				(cbit.vcell.parser.SymbolTableEntry[])mathToBiologicalHash.put(var,new cbit.vcell.parser.SymbolTableEntry[] {biologicalSymbol});
			if(previousBiologicalSymbolArr != null){
				cbit.vcell.parser.SymbolTableEntry[] steArr =
					(cbit.vcell.parser.SymbolTableEntry[])cbit.util.BeanUtils.addElement(previousBiologicalSymbolArr,biologicalSymbol);
				mathToBiologicalHash.put(var,steArr);
			}
		}
	}
}
}