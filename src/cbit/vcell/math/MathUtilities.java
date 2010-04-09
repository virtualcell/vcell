package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.util.*;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;
/**
 * Insert the type's description here.
 * Creation date: (1/29/2002 3:22:16 PM)
 * @author: Jim Schaff
 */
public class MathUtilities {
/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 * @param exp cbit.vcell.parser.Expression
 */
public static Enumeration<Variable> getRequiredVariables(Expression exp, SymbolTable symbolTable) throws MathException, ExpressionException {
	if (exp != null){
		Expression exp2 = substituteFunctions(exp,symbolTable);
		return getRequiredVariablesExplicit(exp2,symbolTable);
	}else{
		//
		// return an empty enumerator
		//
		return (new Vector<Variable>()).elements();
	}
}

/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 * @param exp cbit.vcell.parser.Expression
 */
private static Enumeration<Variable> getRequiredVariablesExplicit(Expression exp, SymbolTable symbolTable) throws ExpressionException {
	Vector requiredVarList = new Vector();
	if (exp != null){
		String identifiers[] = exp.getSymbols();
		if (identifiers != null){
			for (int i=0;i<identifiers.length;i++){
				//
				// look for globally bound variables
				//
				Variable var = (Variable)symbolTable.getEntry(identifiers[i]);
				//
				// look for reserved symbols
				//
				if (var == null){
					var = ReservedVariable.fromString(identifiers[i]);
				}
				//
				// PseudoConstant's are locally bound variables, look for existing binding
				//
				if (var==null){
					SymbolTableEntry ste = exp.getSymbolBinding(identifiers[i]);
					if (ste instanceof PseudoConstant){
						var = (Variable)ste;
					}
				}
				if (var==null){
					throw new ExpressionBindingException("unresolved symbol "+identifiers[i]+" in expression "+exp);
				}		
				requiredVarList.addElement(var);
			}
		}		
	}	
	return requiredVarList.elements();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 * @param exp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression substituteFunctions(Expression exp, SymbolTable symbolTable) throws ExpressionException {
	Expression exp2 = new Expression(exp);
	//
	// do until no more functions to substitute
	//
	int count = 0;
	while (true){
		if (count++ > 30){
			throw new ExpressionBindingException("infinite loop in eliminating function nesting");
		}
		//
		// get All symbols (identifiers), make list of functions
		//
//System.out.println("substituteFunctions() exp2 = '"+exp2+"'");
		Enumeration enum1 = getRequiredVariablesExplicit(exp2, symbolTable);
		Vector functionList = new Vector();
		while (enum1.hasMoreElements()){
			Variable var = (Variable)enum1.nextElement();
			if (var instanceof Function){
				functionList.addElement(var);
			}
		}
		//
		// if no more functions, done!
		//
		if (functionList.size()==0){
			break;
		}
		//
		// substitute out all functions at this level
		//
		for (int i=0;i<functionList.size();i++){
			Function funct = (Function)functionList.elementAt(i);
			Expression functExp = new Expression(funct.getName()+";");
//System.out.println("flattenFunctions(pass="+count+"), substituting '"+funct.getExpression()+"' for function '"+functExp+"'");
			exp2.substituteInPlace(functExp,new Expression(funct.getExpression()));
//System.out.println(".......substituted exp2 = '"+exp2+"'");
		}
	}
//	exp2 = exp2.flatten();
	exp2.bindExpression(symbolTable);
	return exp2;
}

/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 * @param exp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression substituteModelParameters(Expression exp, SymbolTable symbolTable) throws ExpressionException {
	Expression exp2 = new Expression(exp);
	//
	// do until no more functions to substitute
	//
	int count = 0;
	boolean bSubstituted = true;
	while (bSubstituted){
		bSubstituted = false;
		if (count++ > 30){
			throw new ExpressionBindingException("infinite loop in eliminating function nesting");
		}
		String[] symbols = exp2.getSymbols();
		for (int i = 0; i < symbols.length; i++) {
			SymbolTableEntry ste = exp2.getSymbolBinding(symbols[i]);
			if (ste != null && !(ste instanceof SymbolTableFunctionEntry)){
				Expression steExp = ste.getExpression();
				if (steExp != null){
					exp2.substituteInPlace(new Expression(ste.getName()), steExp);
					bSubstituted = true;
				}
			}
		}
	}
	exp2.bindExpression(symbolTable);
	return exp2;
}

public static MathDescription[] getCanonicalMathDescriptions(MathDescription referenceMathDesc, MathDescription testMathDesc) throws PropertyVetoException, MathException, ExpressionException, MappingException {
	HashSet<String> indepVars1 = referenceMathDesc.getStateVariableNames();
	HashSet<String> indepVars2 = testMathDesc.getStateVariableNames();
	HashSet<String> union = new HashSet<String>(indepVars1);
	union.addAll(indepVars2);

//	setStatus("union of state variables: ");
//	for (String varName : union){
//		addStatus(varName+" ");
//	}
//	addStatus("\n");
	
	HashSet<String> depVarsToSubstitute = new HashSet<String>(union);
	depVarsToSubstitute.removeAll(indepVars1);
	
	MathDescription canonicalMath1 = new MathDescription(MathDescription.createMathWithExpandedEquations(referenceMathDesc,union));
	canonicalMath1.makeCanonical();
	MathDescription canonicalMath2 = new MathDescription(MathDescription.createMathWithExpandedEquations(testMathDesc,union));
	canonicalMath2.makeCanonical();

	if (depVarsToSubstitute.size()>0){
		String depVarNames[] = (String[])depVarsToSubstitute.toArray(new String[depVarsToSubstitute.size()]);
		Function functionsToSubstitute[] = MathDescription.getFlattenedFunctions(referenceMathDesc,depVarNames);
		canonicalMath1.substituteInPlace(functionsToSubstitute);
		canonicalMath2.substituteInPlace(functionsToSubstitute);
	}
	// flatten again
	canonicalMath1.makeCanonical();
	canonicalMath2.makeCanonical();
	
	MathDescription[] canonicalMathDescs = {canonicalMath1, canonicalMath2};
	return canonicalMathDescs;

}

}
