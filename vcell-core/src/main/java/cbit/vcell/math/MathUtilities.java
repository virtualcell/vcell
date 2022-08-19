/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.math.MathCompareResults.Decision;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;
/**
 * Insert the type's description here.
 * Creation date: (1/29/2002 3:22:16 PM)
 * @author: Jim Schaff
 */
public class MathUtilities {
	
	
	
//	final String NATIVE_MATHS_ARE_SAME =			" MathsEquivalent:Native ";
//	final String MATHS_ARE_NUMERICALLY_EQUIVALENT =	" MathsEquivalent:Numerically ";
//	final String DIFFERENT_NUMBER_OF_VARIABLES =	" MathsDifferent:DifferentNumberOfVariables ";
//	final String VARIABLES_DONT_MATCH =				" MathsDifferent:VariablesDontMatch ";
//	final String DIFFERENT_NUMBER_OF_EXPRESSIONS =	" MathsDifferent:DifferentNumberOfExpressions ";
//	final String EQUATION_ADDED =					" MathsDifferent:EquationAdded ";
//	final String EQUATION_REMOVED =					" MathsDifferent:EquationRemoved ";
//	final String EXPRESSION_IS_DIFFERENT =			" MathsDifferent:ExpressionIsDifferent ";
//	final String FASTRATE_EXPRESSION_IS_DIFFERENT =	" MathsDifferent:FastRateExpressionIsDifferent ";
//	final String FASTINVARIANT_EXPRESSION_IS_DIFFERENT = " MathsDifferent:FastInvariantExpressionIsDifferent ";
//	final String UNKNOWN_DIFFERENCE_IN_EQUATION =	" MathsDifferent:UnknownDifferenceInEquation ";
//	final String DIFFERENT_NUMBER_OF_SUBDOMAINS =	" MathsDifferent:DifferentNumberOfSubdomains ";
//	final String FAILURE_DIV_BY_ZERO =			 	" MathsDifferent:FailedDivideByZero ";
//	final String FAILURE_UNKNOWN = 					" MathsDifferent:FailedUnknown ";
//	final String UNKNOWN_DIFFERENCE_IN_MATH =		" MathsDifferent:Unknown ";
	private static final Logger lg = LogManager.getLogger(MathUtilities.class);

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
	
	Vector<Variable> requiredVarList = new Vector<Variable>();
	if (exp != null){
		String identifiers[] = exp.getSymbols();
		if (lg.isTraceEnabled()) {
			lg.trace("from expression " + exp + " parsing identifiers " + Arrays.toString(identifiers) );
		}
		if (identifiers != null){
			for (int i=0;i<identifiers.length;i++){
				String id = identifiers[i];
				//
				// look for globally bound variables
				//
				SymbolTableEntry entry = symbolTable.getEntry(id);
				//
				// look for reserved symbols
				//
				if (entry == null){
					entry = ReservedMathSymbolEntries.getReservedVariableEntry(id);
					if (lg.isTraceEnabled()) {
						lg.trace("id " + id + "not in symbol table looked for reserved symbols,found = " + (entry !=null));
					}
				}
				else if (lg.isTraceEnabled()){
					lg.trace("symbolTable.getEntry( ) returned " + entry + " for " + id);
				}
				//
				// PseudoConstant's are locally bound variables, look for existing binding
				//
				if (entry == null){
					SymbolTableEntry ste = exp.getSymbolBinding(id);
					if (ste instanceof PseudoConstant){
						entry = ste;
					}
				if (entry == null){
					ExpressionBindingException ebe = new ExpressionBindingException("unresolved symbol "+id+" in expression "+exp);
					lg.debug("found " + ste + "but it's not a PseudoConstant; throwing ", ebe);
					
					throw ebe;
				}		
				}				
				if (!(entry instanceof Variable)) {
					throw new RuntimeException("MathUtilities.getRequiredVariablesExplicit() only gets required math variable. Use math side symbol table, e.g. MathDescription, SimulationSymbolTable, etc.");
				}
				requiredVarList.addElement((Variable)entry);
			}
		}		
	}	
	return requiredVarList.elements();
}

public static Expression substituteFunctions(Expression exp, SymbolTable symbolTable) throws ExpressionException {
	return substituteFunctions(exp, symbolTable, false);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 * @param exp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public static Expression substituteFunctions(Expression exp, SymbolTable symbolTable,boolean bFlattenAgressive) throws ExpressionException {
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
		if (lg.isTraceEnabled()) {
			lg.trace("substituteFunctions() exp2 = '"+exp2+"'");
		}
		if(bFlattenAgressive) {
			exp2 = exp2.flatten();		
		}

		Enumeration<Variable> enum1 = getRequiredVariablesExplicit(exp2, symbolTable);
		Vector<Variable> functionList = new Vector<Variable>();
		while (enum1.hasMoreElements()){
			Variable var = enum1.nextElement();
			if (var instanceof Function){
				functionList.addElement(var);
				if (lg.isTraceEnabled()) {
					lg.trace("added " + var + " to function list");
				}
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
			if (lg.isTraceEnabled()) {
				lg.trace("flattenFunctions(pass="+count+"), substituting '"+funct.getExpression()+"' for function '"+functExp+"'");
			}
			exp2.substituteInPlace(functExp,new Expression(funct.getExpression()));
			if (lg.isTraceEnabled()) {
				lg.trace(".......substituted exp2 = '"+exp2+"'");
			}
		}
	}
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

public static MathDescription[] getCanonicalMathDescriptions(MathSymbolTableFactory mathSymbolTableFactory, MathDescription referenceMathDesc, MathDescription testMathDesc) throws PropertyVetoException, MathException, ExpressionException {
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
	canonicalMath1.makeCanonical(mathSymbolTableFactory);
	MathDescription canonicalMath2 = new MathDescription(MathDescription.createMathWithExpandedEquations(testMathDesc,union));
	canonicalMath2.makeCanonical(mathSymbolTableFactory);

	if (depVarsToSubstitute.size()>0){
		String depVarNames[] = (String[])depVarsToSubstitute.toArray(new String[depVarsToSubstitute.size()]);
		Function functionsToSubstitute[] = MathDescription.getFlattenedFunctions(mathSymbolTableFactory, referenceMathDesc,depVarNames);
		canonicalMath1.substituteInPlace(mathSymbolTableFactory, functionsToSubstitute);
		canonicalMath2.substituteInPlace(mathSymbolTableFactory, functionsToSubstitute);
	}
	// flatten again
	canonicalMath1.makeCanonical(mathSymbolTableFactory);
	canonicalMath2.makeCanonical(mathSymbolTableFactory);
	
	MathDescription[] canonicalMathDescs = {canonicalMath1, canonicalMath2};
	return canonicalMathDescs;

}

/**
 * Insert the method's description here.
 * Creation date: (10/10/2002 11:32:46 PM)
 * @param oldMath cbit.vcell.math.MathDescription
 * @param newMath cbit.vcell.math.MathDescription
 * @deprecated
 */
public static MathCompareResults testIfSame(MathSymbolTableFactory mathSymbolTableFactory, MathDescription oldMathDesc, MathDescription newMathDesc) {
	try {
	    if (oldMathDesc.compareEqual(newMathDesc)){
		    return new MathCompareResults(Decision.MathEquivalent_NATIVE);
		}else{
		    //System.out.println("------NATIVE MATHS ARE DIFFERENT----------------------");
			//System.out.println("------old native MathDescription:\n"+oldMathDesc.getVCML_database());
			//System.out.println("------new native MathDescription:\n"+newMathDesc.getVCML_database());
			MathDescription strippedOldMath = MathDescription.createCanonicalMathDescription(mathSymbolTableFactory, oldMathDesc);
			MathDescription strippedNewMath = MathDescription.createCanonicalMathDescription(mathSymbolTableFactory, newMathDesc);
			if (strippedOldMath.compareEqual(strippedNewMath)){
				return new MathCompareResults(Decision.MathEquivalent_FLATTENED);
			}else{
				Variable oldVars[] = (Variable[])org.vcell.util.BeanUtils.getArray(strippedOldMath.getVariables(),Variable.class);
				Variable newVars[] = (Variable[])org.vcell.util.BeanUtils.getArray(strippedNewMath.getVariables(),Variable.class);
				if (oldVars.length != newVars.length){
					//
					// number of state variables are not equal (canonical maths only have state variables)
					//
					return new MathCompareResults(Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
				}
				if (!org.vcell.util.Compare.isEqual(oldVars,newVars)){
					//
					// variable names are not equivalent (nothing much we can do)
					//
					return new MathCompareResults(Decision.MathDifferent_VARIABLES_DONT_MATCH);
				}
				//
				// go through the list of SubDomains, and compare equations one by one and "correct" new one if possible
				//
				SubDomain subDomainsOld[] = (SubDomain[])org.vcell.util.BeanUtils.getArray(strippedOldMath.getSubDomains(),SubDomain.class);
				SubDomain subDomainsNew[] = (SubDomain[])org.vcell.util.BeanUtils.getArray(strippedNewMath.getSubDomains(),SubDomain.class);
				if (subDomainsOld.length != subDomainsNew.length){
					return new MathCompareResults(Decision.MathDifferent_DIFFERENT_NUMBER_OF_SUBDOMAINS);
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
								return new MathCompareResults(Decision.MathDifferent_EQUATION_ADDED);

							}
							if (newEqu==null){
								//
								// only one MathDescription had Equation for this Variable.
								//
								return new MathCompareResults(Decision.MathDifferent_EQUATION_REMOVED);

							}
							Expression oldExps[] = (Expression[])org.vcell.util.BeanUtils.getArray(oldEqu.getExpressions(strippedOldMath),Expression.class);
							Expression newExps[] = (Expression[])org.vcell.util.BeanUtils.getArray(newEqu.getExpressions(strippedNewMath),Expression.class);
							if (oldExps.length != newExps.length){
								return new MathCompareResults(Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS);
							}
							for (int k = 0; k < oldExps.length; k++){
								if (!oldExps[k].compareEqual(newExps[k])){
									bFoundDifference = true;
									if (!cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(oldExps[k],newExps[k])){
										//
										// difference couldn't be reconciled
										//
										return new MathCompareResults(Decision.MathDifferent_DIFFERENT_EXPRESSION,
												"expressions are different Old: '"+oldExps[k].infix()+"'\n"+
												"expressions are different New: '"+newExps[k].infix()+"'");
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
								return new MathCompareResults(Decision.MathDifferent_UNKNOWN_DIFFERENCE_IN_EQUATION,
										"couldn't find problem with equation for "+oldVars[j].getName()+" in compartment "+subDomainsOld[i].getName());
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
									return new MathCompareResults(Decision.MathDifferent_EQUATION_ADDED);
								}
								if (newJumpCondition==null){
									//
									// only one MathDescription had Equation for this Variable.
									//
									return new MathCompareResults(Decision.MathDifferent_EQUATION_REMOVED);
								}
								Expression oldExps[] = (Expression[])org.vcell.util.BeanUtils.getArray(oldJumpCondition.getExpressions(strippedOldMath),Expression.class);
								Expression newExps[] = (Expression[])org.vcell.util.BeanUtils.getArray(newJumpCondition.getExpressions(strippedNewMath),Expression.class);
								if (oldExps.length != newExps.length){
									return new MathCompareResults(Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS);
								}
								for (int k = 0; k < oldExps.length; k++){
									if (!oldExps[k].compareEqual(newExps[k])){
										bFoundDifference = true;
										if (!cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(oldExps[k],newExps[k])){
											//
											// difference couldn't be reconciled
											//
											return new MathCompareResults(Decision.MathDifferent_DIFFERENT_EXPRESSION,
													"expressions are different Old: '"+oldExps[k]+"'\n"+
													"expressions are different New: '"+newExps[k]+"'");
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
									return new MathCompareResults(Decision.MathDifferent_UNKNOWN_DIFFERENCE_IN_EQUATION,
											"couldn't find problem with jumpCondition for "+oldVars[j].getName()+" in compartment "+subDomainsOld[i].getName());
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
							return new MathCompareResults(Decision.MathDifferent_EQUATION_ADDED);
						}
						if (newFastSystem==null){
							//
							// only one MathDescription had Equation for this Variable.
							//
							return new MathCompareResults(Decision.MathDifferent_EQUATION_REMOVED);
						}
						Expression oldExps[] = oldFastSystem.getExpressions();
						Expression newExps[] = newFastSystem.getExpressions();
						if (oldExps.length != newExps.length){
							return new MathCompareResults(Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS);
						}
						for (int k = 0; k < oldExps.length; k++){
							if (!oldExps[k].compareEqual(newExps[k])){
								bFoundDifference = true;
								if (!cbit.vcell.parser.ExpressionUtils.functionallyEquivalent(oldExps[k],newExps[k])){
									//
									// difference couldn't be reconciled
									//
									return new MathCompareResults(Decision.MathDifferent_DIFFERENT_FASTRATE_EXPRESSION,
											"FastSystem expressions are different Old: '"+oldExps[k]+"'\n"+
											"FastSystem expressions are different New: '"+newExps[k]+"'");
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
							return new MathCompareResults(Decision.MathDifferent_UNKNOWN_DIFFERENCE_IN_EQUATION,
									"couldn't find problem with FastSystem for compartment "+subDomainsOld[i].getName());
						}
					}
				}

				//
				// after repairing aspects of MathDescription, now see if same
				//
				if (strippedOldMath.compareEqual(strippedNewMath)){
					return new MathCompareResults(Decision.MathEquivalent_NUMERICALLY);
				}else{
				    //System.out.println("------UNKNOWN DIFFERENCE IN MATH----------------------");
					//System.out.println("------old flattened MathDescription:\n"+strippedOldMath.getVCML_database());
					//System.out.println("------new flattened MathDescription:\n"+strippedNewMath.getVCML_database());
					return new MathCompareResults(Decision.MathDifferent_UNKNOWN_DIFFERENCE_IN_MATH);
				}
			}
		}
	}catch (cbit.vcell.parser.DivideByZeroException e){
		System.out.println("-------DIVIDE BY ZERO EXCEPTION-------------------------");
		return new MathCompareResults(Decision.MathDifferent_FAILURE_FLATTENING_DIV_BY_ZERO);
	}catch (Throwable e){
		e.printStackTrace(System.out);
		return new MathCompareResults(Decision.MathDifferent_FAILURE_FLATTENING_UNKNOWN,
				"Exception: '"+e.getMessage()+"'");
	}
}

}
