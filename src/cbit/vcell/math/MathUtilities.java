package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.util.*;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionUtils;
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
					var = ReservedMathSymbolEntries.getReservedVariableEntry(identifiers[i]);
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
		if (symbols != null) {
			for (int i = 0; i < symbols.length; i++) {
				SymbolTableEntry ste = exp2.getSymbolBinding(symbols[i]);
				if (ste != null && !(ste instanceof SymbolTableFunctionEntry)) {
					Expression steExp = ste.getExpression();
					if (steExp != null) {
						exp2.substituteInPlace(new Expression(ste.getName()),steExp);
						bSubstituted = true;
					}
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

/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 11:32:46 PM)
 * @param oldMath cbit.vcell.math.MathDescription
 * @param newMath cbit.vcell.math.MathDescription
 */
public static boolean testIfSame(MathDescription oldMathDesc, MathDescription newMathDesc, StringBuffer reasonForDecision) {
	final String NATIVE_MATHS_ARE_SAME =			" MathsEquivalent:Native ";
	final String FLATTENED_MATHS_ARE_SAME =			" MathsEquivalent:Flattened ";
	final String MATHS_ARE_NUMERICALLY_EQUIVALENT =	" MathsEquivalent:Numerically ";
	final String DIFFERENT_NUMBER_OF_VARIABLES =	" MathsDifferent:DifferentNumberOfVariables ";
	final String VARIABLES_DONT_MATCH =				" MathsDifferent:VariablesDontMatch ";
	final String DIFFERENT_NUMBER_OF_EXPRESSIONS =	" MathsDifferent:DifferentNumberOfExpressions ";
	final String EQUATION_ADDED =					" MathsDifferent:EquationAdded ";
	final String EQUATION_REMOVED =					" MathsDifferent:EquationRemoved ";
	final String EXPRESSION_IS_DIFFERENT =			" MathsDifferent:ExpressionIsDifferent ";
	final String FAST_EXPRESSION_IS_DIFFERENT =		" MathsDifferent:FastExpressionIsDifferent ";
	final String UNKNOWN_DIFFERENCE_IN_EQUATION =	" MathsDifferent:UnknownDifferenceInEquation ";
	final String DIFFERENT_NUMBER_OF_SUBDOMAINS =	" MathsDifferent:DifferentNumberOfSubdomains ";
	final String FAILURE_FLATTENING_DIV_BY_ZERO = 	" MathsDifferent:FailedFlatteningDivideByZero ";
	final String FAILURE_FLATTENING_UNKNOWN = 		" MathsDifferent:FailedFlatteningUnknown ";
	final String UNKNOWN_DIFFERENCE_IN_MATH =		" MathsDifferent:Unknown ";
	try {
	    if (oldMathDesc.compareEqual(newMathDesc)){
		    reasonForDecision.append(NATIVE_MATHS_ARE_SAME);
		    return true;
		}else{
		    //System.out.println("------NATIVE MATHS ARE DIFFERENT----------------------");
			//System.out.println("------old native MathDescription:\n"+oldMathDesc.getVCML_database());
			//System.out.println("------new native MathDescription:\n"+newMathDesc.getVCML_database());
			MathDescription strippedOldMath = MathDescription.createCanonicalMathDescription(oldMathDesc);
			MathDescription strippedNewMath = MathDescription.createCanonicalMathDescription(newMathDesc);
			if (strippedOldMath.compareEqual(strippedNewMath)){
				reasonForDecision.append(FLATTENED_MATHS_ARE_SAME);
			    return true;
			}else{
				Variable oldVars[] = (Variable[])org.vcell.util.BeanUtils.getArray(strippedOldMath.getVariables(),Variable.class);
				Variable newVars[] = (Variable[])org.vcell.util.BeanUtils.getArray(strippedNewMath.getVariables(),Variable.class);
				if (oldVars.length != newVars.length){
					//
					// number of state variables are not equal (canonical maths only have state variables)
					//
					reasonForDecision.append(DIFFERENT_NUMBER_OF_VARIABLES);
					return false;
				}
				if (!org.vcell.util.Compare.isEqual(oldVars,newVars)){
					//
					// variable names are not equivalent (nothing much we can do)
					//
					reasonForDecision.append(VARIABLES_DONT_MATCH);
					return false;
				}
				//
				// go through the list of SubDomains, and compare equations one by one and "correct" new one if possible
				//
				SubDomain subDomainsOld[] = (SubDomain[])org.vcell.util.BeanUtils.getArray(strippedOldMath.getSubDomains(),SubDomain.class);
				SubDomain subDomainsNew[] = (SubDomain[])org.vcell.util.BeanUtils.getArray(strippedNewMath.getSubDomains(),SubDomain.class);
				if (subDomainsOld.length != subDomainsNew.length){
					reasonForDecision.append(DIFFERENT_NUMBER_OF_SUBDOMAINS);
					return false;
				}
				for (int i = 0; i < subDomainsOld.length; i++){
					for (int j = 0; j < oldVars.length; j++){
						//
						// test equation for this subdomain and variable
						//
						{
						Equation oldEqu = subDomainsOld[i].getEquation(oldVars[j]);
						Equation newEqu = subDomainsNew[i].getEquation(oldVars[j]);
						if (!org.vcell.util.Compare.isEqualOrNull(oldEqu,newEqu)){
							boolean bFoundDifference = false;
							//
							// equation didn't compare exactly, lets try to evaluate some instead
							//
							if (oldEqu==null){
								//
								// only one MathDescription had Equation for this Variable.
								//
								reasonForDecision.append(EQUATION_ADDED);
								return false;
							}
							if (newEqu==null){
								//
								// only one MathDescription had Equation for this Variable.
								//
								reasonForDecision.append(EQUATION_REMOVED);
								return false;
							}
							Expression oldExps[] = (Expression[])org.vcell.util.BeanUtils.getArray(oldEqu.getExpressions(strippedOldMath),Expression.class);
							Expression newExps[] = (Expression[])org.vcell.util.BeanUtils.getArray(newEqu.getExpressions(strippedNewMath),Expression.class);
							if (oldExps.length != newExps.length){
								reasonForDecision.append(DIFFERENT_NUMBER_OF_EXPRESSIONS);
								return false;
							}
							for (int k = 0; k < oldExps.length; k++){
								if (!oldExps[k].compareEqual(newExps[k])){
									bFoundDifference = true;
									if (!cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(oldExps[k],newExps[k])){
										//
										// difference couldn't be reconciled
										//
										System.out.println("expressions are different Old: '"+oldExps[k].infix()+"'\n"+
														   "expressions are different New: '"+newExps[k].infix()+"'");
										reasonForDecision.append(EXPRESSION_IS_DIFFERENT);
										return false;
									}else{
										//System.out.println("expressions are equivalent Old: '"+oldExps[k].infix()+"'\n"+
														   //"expressions are equivalent New: '"+newExps[k].infix()+"'");
									}
								}
							}
							//
							// equation was not strictly "equal" but passed all tests, replace with old equation and move on
							//
							if (bFoundDifference){
								subDomainsNew[i].replaceEquation(oldEqu);
							}else{
								//
								// couldn't find the smoking gun, just plain bad
								//
								System.out.println("couldn't find problem with equation for "+oldVars[j].getName()+" in compartment "+subDomainsOld[i].getName());
								reasonForDecision.append(UNKNOWN_DIFFERENCE_IN_EQUATION);
								return false;
							}
						}
						}
						//
						// if a membrane, test jumpCondition for this subdomain and variable
						//
						if (subDomainsOld[i] instanceof MembraneSubDomain && oldVars[j] instanceof VolVariable){
							JumpCondition oldJumpCondition = ((MembraneSubDomain)subDomainsOld[i]).getJumpCondition((VolVariable)oldVars[j]);
							JumpCondition newJumpCondition = ((MembraneSubDomain)subDomainsNew[i]).getJumpCondition((VolVariable)oldVars[j]);
							if (!org.vcell.util.Compare.isEqualOrNull(oldJumpCondition,newJumpCondition)){
								boolean bFoundDifference = false;
								//
								// equation didn't compare exactly, lets try to evaluate some instead
								//
								if (oldJumpCondition==null){
									//
									// only one MathDescription had Equation for this Variable.
									//
									reasonForDecision.append(EQUATION_ADDED);
									return false;
								}
								if (newJumpCondition==null){
									//
									// only one MathDescription had Equation for this Variable.
									//
									reasonForDecision.append(EQUATION_REMOVED);
									return false;
								}
								Expression oldExps[] = (Expression[])org.vcell.util.BeanUtils.getArray(oldJumpCondition.getExpressions(strippedOldMath),Expression.class);
								Expression newExps[] = (Expression[])org.vcell.util.BeanUtils.getArray(newJumpCondition.getExpressions(strippedNewMath),Expression.class);
								if (oldExps.length != newExps.length){
									reasonForDecision.append(DIFFERENT_NUMBER_OF_EXPRESSIONS);
									return false;
								}
								for (int k = 0; k < oldExps.length; k++){
									if (!oldExps[k].compareEqual(newExps[k])){
										bFoundDifference = true;
										if (!cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(oldExps[k],newExps[k])){
											//
											// difference couldn't be reconciled
											//
											System.out.println("expressions are different Old: '"+oldExps[k]+"'\n"+
															   "expressions are different New: '"+newExps[k]+"'");
											reasonForDecision.append(EXPRESSION_IS_DIFFERENT);
											return false;
										}else{
											//System.out.println("expressions are equivalent Old: '"+oldExps[k]+"'\n"+
															   //"expressions are equivalent New: '"+newExps[k]+"'");
										}
									}
								}
								//
								// equation was not strictly "equal" but passed all tests, replace with old equation and move on
								//
								if (bFoundDifference){
									((MembraneSubDomain)subDomainsNew[i]).replaceJumpCondition(oldJumpCondition);
								}else{
									//
									// couldn't find the smoking gun, just plain bad
									//
									System.out.println("couldn't find problem with jumpCondition for "+oldVars[j].getName()+" in compartment "+subDomainsOld[i].getName());
									reasonForDecision.append(UNKNOWN_DIFFERENCE_IN_EQUATION);
									return false;
								}
							}
						}
					}
					//
					// test fast system for subdomain
					//
					FastSystem oldFastSystem = subDomainsOld[i].getFastSystem();
					FastSystem newFastSystem = subDomainsNew[i].getFastSystem();
					if (!org.vcell.util.Compare.isEqualOrNull(oldFastSystem,newFastSystem)){
						boolean bFoundDifference = false;
						//
						// fastSystems didn't compare exactly, lets try to evaluate some expressions instead
						//
						if (oldFastSystem==null){
							//
							// only one MathDescription had Equation for this Variable.
							//
							reasonForDecision.append(EQUATION_ADDED);
							return false;
						}
						if (newFastSystem==null){
							//
							// only one MathDescription had Equation for this Variable.
							//
							reasonForDecision.append(EQUATION_REMOVED);
							return false;
						}
						Expression oldExps[] = oldFastSystem.getExpressions();
						Expression newExps[] = newFastSystem.getExpressions();
						if (oldExps.length != newExps.length){
							reasonForDecision.append(DIFFERENT_NUMBER_OF_EXPRESSIONS);
							return false;
						}
						for (int k = 0; k < oldExps.length; k++){
							if (!oldExps[k].compareEqual(newExps[k])){
								bFoundDifference = true;
								if (!cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(oldExps[k],newExps[k])){
									//
									// difference couldn't be reconciled
									//
									System.out.println("FastSystem expressions are different Old: '"+oldExps[k]+"'\n"+
													   "FastSystem expressions are different New: '"+newExps[k]+"'");
									reasonForDecision.append(FAST_EXPRESSION_IS_DIFFERENT);
									return false;
								}else{
									//System.out.println("expressions are equivalent Old: '"+oldExps[k]+"'\n"+
													   //"expressions are equivalent New: '"+newExps[k]+"'");
								}
							}
						}
						//
						// equation was not strictly "equal" but passed all tests, replace with old equation and move on
						//
						if (bFoundDifference){
							subDomainsNew[i].setFastSystem(oldFastSystem);
						}else{
							//
							// couldn't find the smoking gun, just plain bad
							//
							System.out.println("couldn't find problem with FastSystem for compartment "+subDomainsOld[i].getName());
							reasonForDecision.append(UNKNOWN_DIFFERENCE_IN_EQUATION);
							return false;
						}
					}
				}

				//
				// after repairing aspects of MathDescription, now see if same
				//
				if (strippedOldMath.compareEqual(strippedNewMath)){
					reasonForDecision.append(MATHS_ARE_NUMERICALLY_EQUIVALENT);
					return true;
				}else{
				    //System.out.println("------UNKNOWN DIFFERENCE IN MATH----------------------");
					//System.out.println("------old flattened MathDescription:\n"+strippedOldMath.getVCML_database());
					//System.out.println("------new flattened MathDescription:\n"+strippedNewMath.getVCML_database());
					reasonForDecision.append(UNKNOWN_DIFFERENCE_IN_MATH);
					return false;
				}
			}
		}
	}catch (cbit.vcell.parser.DivideByZeroException e){
		System.out.println("-------DIVIDE BY ZERO EXCEPTION-------------------------");
		reasonForDecision.append(FAILURE_FLATTENING_DIV_BY_ZERO);
		return false;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		reasonForDecision.append(FAILURE_FLATTENING_UNKNOWN);
		return false;
	}
}

}
