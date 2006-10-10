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
 * Creation date: (5/3/2006 3:49:12 PM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param var cbit.vcell.math.Variable
 */
public org.vcell.expression.SymbolTableEntry getBiologicalSymbol(cbit.vcell.math.Variable var) {
	return (org.vcell.expression.SymbolTableEntry)mathToBiologicalHash.get(var);

}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:50:22 PM)
 * @return cbit.vcell.math.Variable
 * @param biologicalSymbol cbit.vcell.parser.SymbolTableEntry
 */
public cbit.vcell.math.Variable getVariable(org.vcell.expression.SymbolTableEntry biologicalSymbol) {
	return (cbit.vcell.math.Variable)biologicalToMathHash.get(biologicalSymbol);
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 3:48:47 PM)
 * @param ste cbit.vcell.parser.SymbolTableEntry
 * @param var cbit.vcell.math.Variable
 */
void put(org.vcell.expression.SymbolTableEntry biologicalSymbol, String varName) {
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
		org.vcell.expression.SymbolTableEntry biologicalSymbol = (org.vcell.expression.SymbolTableEntry)keysetIter.next();
		String mathVarName = (String)biologicalToMathSymbolNameHash.get(biologicalSymbol);
		cbit.vcell.math.Variable var = mathDesc.getVariable(mathVarName);
		biologicalToMathHash.put(biologicalSymbol,var);
		//System.out.println(var+" ---> "+biologicalSymbol);
		org.vcell.expression.SymbolTableEntry previousBiologicalSymbol = (org.vcell.expression.SymbolTableEntry)mathToBiologicalHash.put(var,biologicalSymbol);
		if (previousBiologicalSymbol!=null && !biologicalSymbol.equals(previousBiologicalSymbol)){
			throw new RuntimeException("mapping not unique, math symbol '"+var.getName()+" mapped to two biological symbols, '"+biologicalSymbol.getName()+"' and '"+previousBiologicalSymbol.getName()+"'");
		}
	}
}
}