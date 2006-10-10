package cbit.vcell.math;

import java.util.*;

import org.vcell.expression.ExpressionBindingException;
import org.vcell.expression.ExpressionException;
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;
import org.vcell.expression.SymbolTable;
import org.vcell.expression.SymbolTableEntry;

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
public static Enumeration getRequiredVariables(IExpression exp, SymbolTable symbolTable) throws MathException, ExpressionException {
	if (exp != null){
		IExpression exp2 = substituteFunctions(exp,symbolTable);
		return getRequiredVariablesExplicit(exp2,symbolTable);
	}else{
		//
		// return an empty enumerator
		//
		return (new Vector()).elements();
	}
}
/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 * @param exp cbit.vcell.parser.Expression
 */
private static Enumeration getRequiredVariablesExplicit(IExpression exp, SymbolTable symbolTable) throws ExpressionException {
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
public static IExpression substituteFunctions(IExpression exp, SymbolTable symbolTable) throws ExpressionException {
	IExpression exp2 = ExpressionFactory.createExpression(exp);
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
			IExpression functExp = ExpressionFactory.createExpression(funct.getName()+";");
//System.out.println("flattenFunctions(pass="+count+"), substituting '"+funct.getExpression()+"' for function '"+functExp+"'");
			exp2.substituteInPlace(functExp,ExpressionFactory.createExpression(funct.getExpression()));
//System.out.println(".......substituted exp2 = '"+exp2+"'");
		}
	}
//	exp2 = exp2.flatten();
	exp2.bindExpression(symbolTable);
	return exp2;
}
}
