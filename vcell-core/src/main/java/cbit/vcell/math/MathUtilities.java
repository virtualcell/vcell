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

import java.util.*;
import java.util.stream.Collectors;

import cbit.vcell.parser.*;
import cbit.vcell.solver.SimulationSymbolTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.test.Fast;

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
	return substituteModelParameters(exp, symbolTable, true, true);
}


public static Expression substituteModelParameters(Expression exp, SymbolTable symbolTable, boolean bToNumbers, boolean bBind) throws ExpressionException {
	Expression exp2 = new Expression(exp);
	//
	// do until no more functions to substitute; optionally stop last step before numeric
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
						if (!((steExp.getSymbols() == null || steExp.getSymbols().length == 0) && !bToNumbers)) {
							exp2.substituteInPlace(new Expression(ste.getName()),steExp);
							bSubstituted = true;
						}
					}
				}
			}
		}
	}
	if (bBind) exp2.bindExpression(symbolTable);
	return exp2;
}

/**
 * Using information from both math descriptions
 * 1. identify the union state variables from both math descriptions to get a consensus set for expansion of both maths
 * 2. expand one or both math descriptions to rehydrate the same set of differential equations in both maths
 * 3. substitute all functions in both maths.
 */
public static MathDescription[] getCanonicalMathDescriptions(MathDescription referenceMathDesc, MathDescription testMathDesc) throws MathException, ExpressionException {
	MathSymbolTableFactory mathSymbolTableFactory = SimulationSymbolTable.createMathSymbolTableFactory();
	HashSet<String> indepVarNames1 = referenceMathDesc.getStateVariableNames();
	HashSet<String> indepVarNames2 = testMathDesc.getStateVariableNames();
	HashSet<String> unionOfVarNames = new HashSet<>(indepVarNames1);
	unionOfVarNames.addAll(indepVarNames2);
	Map<String, Variable.Domain> indepVars1 = referenceMathDesc.getStateVariables().stream().filter(v -> v.getDomain() != null).collect(Collectors.toMap(v -> v.getName(), v -> v.getDomain()));
	Map<String, Variable.Domain> indepVars2 = testMathDesc.getStateVariables().stream().filter(v -> v.getDomain() != null).collect(Collectors.toMap(v -> v.getName(), v -> v.getDomain()));
	Map<String, Variable.Domain> unionMap = new HashMap<>();
	for (String varName : unionOfVarNames){
		Variable.Domain domain = null;
		if (indepVars1.get(varName) != null){
			domain = indepVars1.get(varName);
		}
		if (indepVars2.get(varName) != null){
			domain = indepVars2.get(varName);
		}
		if (domain != null){
			unionMap.put(varName,domain);
		}
	}
	
	HashSet<String> depVarsToSubstitute = new HashSet<String>(unionOfVarNames);
	depVarsToSubstitute.removeAll(indepVarNames1);
	
	MathDescription canonicalMath1 = new MathDescription(createMathWithExpandedEquations(referenceMathDesc,unionOfVarNames,unionMap));
	canonicalMath1.makeCanonical(mathSymbolTableFactory);
	MathDescription canonicalMath2 = new MathDescription(createMathWithExpandedEquations(testMathDesc,unionOfVarNames,unionMap));
	canonicalMath2.makeCanonical(mathSymbolTableFactory);

	if (depVarsToSubstitute.size()>0){
		String depVarNames[] = (String[])depVarsToSubstitute.toArray(new String[depVarsToSubstitute.size()]);
		Function functionsToSubstitute[] = MathDescription.getFlattenedFunctions(mathSymbolTableFactory, referenceMathDesc,depVarNames);
		canonicalMath1.substituteInPlace(mathSymbolTableFactory, functionsToSubstitute);
		canonicalMath2.substituteInPlace(mathSymbolTableFactory, functionsToSubstitute);
	}

	// flatten the maths by substitution of all remaining functions and constants - leaving expressions with only state variables and numeric literals.
	canonicalMath1.makeCanonical(mathSymbolTableFactory);
	canonicalMath2.makeCanonical(mathSymbolTableFactory);
	
	MathDescription[] canonicalMathDescs = {canonicalMath1, canonicalMath2};
	return canonicalMathDescs;
}

	public static MathDescription createCanonicalMathDescription(MathSymbolTableFactory mathSymbolTableFactory, MathDescription originalMathDescription) throws MathException, ExpressionException {
		//
		// clone current mathdescription
		//
		MathDescription newMath = new MathDescription(originalMathDescription);

		newMath.makeCanonical(mathSymbolTableFactory);

		return newMath;
	}

	public static void substituteCommonDiscontinuitiesInPlace(Expression exp1, Expression exp2, String varPrefix) throws ExpressionException {
		Vector<Discontinuity> v1 = exp1.getDiscontinuities();
		Vector<Discontinuity> v2 = exp2.getDiscontinuities();
		if (!(v1.isEmpty() && v2.isEmpty())) {
			HashSet<String> allDisc = new HashSet<String>();
			for (int l = 0; l<v1.size(); l++) {
				allDisc.add(v1.get(l).getDiscontinuityExp().infix());
			}
			for (int l = 0; l<v2.size(); l++) {
				allDisc.add(v2.get(l).getDiscontinuityExp().infix());
			}
			int l = 0;
			for (String discExp : allDisc) {
				Optional<Discontinuity> discontinuity;
				discontinuity = exp1.getDiscontinuities().stream()
						.filter(disc -> disc.getDiscontinuityExp().infix().equals(discExp)).findFirst();
				if (discontinuity.isPresent()) {
					exp1.substituteInPlace(discontinuity.get().getDiscontinuityExp(), new Expression(varPrefix + l));
				}

				discontinuity = exp2.getDiscontinuities().stream()
						.filter(disc -> disc.getDiscontinuityExp().infix().equals(discExp)).findFirst();
				if (discontinuity.isPresent()) {
					exp2.substituteInPlace(discontinuity.get().getDiscontinuityExp(), new Expression(varPrefix + l));
				}
				exp2.substituteInPlace(new Expression(discExp),
						new Expression(varPrefix + l));
				l++;
			}
		}
	}

	public static boolean compareEquivalent(FastInvariant fastInvariant1, FastInvariant fastInvariant2) throws ExpressionException {
		//
		// It is ok if the two expressions are different by a scale factor (e.g. if X+Y is conserved, so is 2*X+2*Y, or 2*(X+Y))
		//    1. estimate proportionality factor (average of 5 evaluations - with discontinuities removed)
		//    2. scale one of the expressions by the proportionality factor to correct for the scaling.
		//    3. compare the two expressions for equivalence
		//
		Expression oldFastInvExp = new Expression(fastInvariant1.getFunction());
		Expression newFastInvExp = new Expression(fastInvariant2.getFunction());
		MathUtilities.substituteCommonDiscontinuitiesInPlace(oldFastInvExp, newFastInvExp, "BOOLEAN_");

		Set<String> symbolSet = new LinkedHashSet<>(Arrays.asList(oldFastInvExp.getSymbols()));
		symbolSet.addAll(Arrays.asList(newFastInvExp.getSymbols()));
		String[] symbols = symbolSet.toArray(new String[symbolSet.size()]);
		SimpleSymbolTable symbolTable = new SimpleSymbolTable(symbols);
		oldFastInvExp.bindExpression(symbolTable);
		newFastInvExp.bindExpression(symbolTable);

		Random random = new Random(0);
		List<Double> ratios = new ArrayList<>();
		final int NUM_SUCCESSFUL_TRIALS = 5;
		final int MAX_TRIALS = 500;
		Double estimatedRatio;
		double[] values = new double[symbols.length];
		for (int m = 0; m < MAX_TRIALS || ratios.size() < NUM_SUCCESSFUL_TRIALS; m++) {
			for (int j = 0; j < values.length; j++) {
				values[j] = random.nextDouble() + 1.0;
			}
			try {
				double oldFastInvValue = oldFastInvExp.evaluateVector(values);
				double newFastInvValue = newFastInvExp.evaluateVector(values);
				if (oldFastInvValue != 0.0 && newFastInvValue != 0.0) {
					ratios.add(oldFastInvValue / newFastInvValue);
				}
			} catch (ExpressionException e) {
			}
		}
		if (ratios.size() >= NUM_SUCCESSFUL_TRIALS){
			DoubleSummaryStatistics stats = ratios.stream().mapToDouble(r -> r).summaryStatistics();
			estimatedRatio = stats.getAverage();
		}else{
			String msg = "fast invariant expressions could not be evaluated, exp1='"+fastInvariant1.getFunction().infix()+"', "+
					"exp2='"+fastInvariant2.getFunction().infix()+"'";
			lg.debug(msg);
			return false;
		}
		Expression scaled_fastInvariant2 = Expression.mult(new Expression(estimatedRatio),fastInvariant2.getFunction());
		System.out.println("MathDescription.compareEquivalent(): comparing "+fastInvariant1.getFunction().infix()+" with "+scaled_fastInvariant2.infix());
		if (!ExpressionUtils.functionallyEquivalent(fastInvariant1.getFunction(), scaled_fastInvariant2)){
			String msg = "fast invariant expressions are different, exp1='"+fastInvariant1.getFunction().infix()+"', "+
					"exp2='"+fastInvariant2.getFunction().infix()+"'";
			lg.debug(msg);
			return false;
		}
		return true;
	}


	public static MathDescription createMathWithExpandedEquations(MathDescription originalMathDescription, Set<String> varNamesToKeep, Map<String, Variable.Domain> varDomainMap) throws MathException, ExpressionException {
		//
		// clone current mathdescription
		//
		MathDescription newMath = new MathDescription(originalMathDescription);

		//
		// for any dependent variables in the "varNamesToKeep" list, create appropriate Variable/Equation
		//
		// this assumes that the dependent 'Function' is a linear combination of existing state variables
		//  e.g. Function depVar = K0 + K1*indepVar1 + K2*indepVar2 + ... + Kn*indepVarN
		// if it doesn't fit this form, then math's are not equivalent.
		//
		HashSet<String> stateVarSet = newMath.getStateVariableNames();
		// Build the list of variables to be added
		ArrayList<Function> varsToAdd = new ArrayList<Function>();
		for (Variable var : newMath.getVariableList()){
			if (varNamesToKeep.contains(var.getName()) && var instanceof Function){
				varsToAdd.add((Function)var);
			}
		}
		for (Function function : varsToAdd){
				//
				// get list of symbols that are state variables
				//
				ArrayList<Variable> indepVarList = new ArrayList<Variable>();         // holds the "indepVar's"
				ArrayList<Expression> coefficientList = new ArrayList<Expression>();      // holds the "K's"
				Expression exp = function.getExpression();
				exp.bindExpression(null);
				Expression K0 = new Expression(exp);
				K0.bindExpression(null);
				String symbols[] = exp.getSymbols();
				for (int j = 0; j < symbols.length; j++){
					if (stateVarSet.contains(symbols[j])){
						//
						// store the independent variable (indepVar_i)
						//
						indepVarList.add(newMath.getVariable(symbols[j]));
						Expression differential = exp.differentiate(symbols[j]);
						differential = differential.flatten();
						//
						// store the coefficient (K_i)
						//
						coefficientList.add(differential);
						//
						// remove this term from the "constant" term (K0)
						// (e.g. for expression "K0 + K1*V1 + ... + Ki*Vi + ... + Kn*Vn", Vi set to 0.0 and flattened)
						// after each term is removed, only K0 is left
						//
						K0.substituteInPlace(new Expression(symbols[j]),new Expression(0.0));
						K0 = K0.flatten();
					}
				}
				//
				// compute fast invariant expression, may be needed later
				//
				Expression fastInvariantExp = new Expression(0.0);
				for (int k = 0; k < indepVarList.size(); k++){
					Variable indepVar = indepVarList.get(k);
					Expression coefficient = coefficientList.get(k);
					fastInvariantExp = Expression.add(fastInvariantExp,Expression.mult(new Expression(coefficient),new Expression(indepVar.getName())));
				}

				//
				// either all independent vars should be Volume, all should be Membrane, or all should be Filament
				//
				int countVolumeVars = 0;
				int countMembraneVars = 0;
				int countFilamentVars = 0;
				int countPointVars = 0;
				for (int j = 0; j < indepVarList.size(); j++){
					if (indepVarList.get(j) instanceof VolVariable){
						countVolumeVars++;
					}else if (indepVarList.get(j) instanceof MemVariable){
						countMembraneVars++;
					}else if (indepVarList.get(j) instanceof FilamentVariable){
						countFilamentVars++;
					}else if (indepVarList.get(j) instanceof PointVariable){
						countPointVars++;
					}else{
						throw new RuntimeException("create canonicalMath cannot handle dependent vars of type '"+indepVarList.get(j).getClass().getName()+"'");
					}
				}
				Variable.Domain domain = varDomainMap.get(function.getName());
				SubDomain functionSubdomain = (domain != null) ? newMath.getSubDomain(domain.getName()) : null;

				//
				// case: Volume Variable
				// create VolVariable
				// for each CompartmentSubDomain, create OdeEquation
				// remove Function
				//
				if ((countVolumeVars > 0 && countVolumeVars == indepVarList.size()) || functionSubdomain instanceof CompartmentSubDomain){
					VolVariable volVariable = new VolVariable(function.getName(),function.getDomain());
					newMath.getVariableList().remove(function);
					newMath.getVariableList().add(volVariable);
					newMath.getVariableMap().remove(function.getName());
					newMath.getVariableMap().put(volVariable.getName(), volVariable);
					//
					// determine which volume subdomains (CompartmentSubDomains) to add equations to
					//    if domain information is available, then restrict the creation of equations to that subdomain,
					//    else add equation to all subdomains of the same type (here: any CompartmentSubDomain)
					//
					Set<CompartmentSubDomain> compartmentSubDomains = new LinkedHashSet<>();
					if (functionSubdomain != null){
						compartmentSubDomains.add((CompartmentSubDomain) functionSubdomain);
					}else if (countMembraneVars > 0){
						for (Variable indepVar : indepVarList){
							if (indepVar instanceof VolVariable && indepVar.getDomain() != null){
								compartmentSubDomains.add((CompartmentSubDomain) newMath.getSubDomain(indepVar.getDomain().getName()));
							}
						}
					}else{
						for (SubDomain subDomain : newMath.getSubDomainCollection()){
							if (subDomain instanceof CompartmentSubDomain){
								compartmentSubDomains.add((CompartmentSubDomain) subDomain);
							}
						}
					}
					for (CompartmentSubDomain compartmentSubDomain : compartmentSubDomains){
						//
						// add an ODE where
						//    initial value = K0 + Sum(coefficient_i*Var_i.init)
						//    rate value = Sum(coefficient_i*Var_i.rate)
						//
						Expression initExp = new Expression(K0);
						Expression rateExp = new Expression(0.0);
						for (int k = 0; k < indepVarList.size(); k++){
							Variable indepVar = indepVarList.get(k);
							Equation indepVarEqu = compartmentSubDomain.getEquation(indepVar);
							Expression coefficient = coefficientList.get(k);
							initExp = Expression.add(initExp,Expression.mult(new Expression(coefficient),new Expression(indepVarEqu.getInitialExpression())));
							rateExp = Expression.add(rateExp,Expression.mult(new Expression(coefficient),new Expression(indepVarEqu.getRateExpression())));
						}
						for (Variable var : newMath.getVariableList()){
							if (var.getName().startsWith(function.getName()+ MathDescription.MATH_FUNC_INIT_SUFFIX_PREFIX)){
								Variable initVariable = var;
								initExp = new Expression(initVariable,null);
								break;
							}
						}
						OdeEquation odeEquation = new OdeEquation(volVariable, initExp.flatten(), rateExp.flatten());
						compartmentSubDomain.addEquation(odeEquation);
						if (compartmentSubDomain.getFastSystem()!=null){
							compartmentSubDomain.getFastSystem().addFastInvariant(new FastInvariant(fastInvariantExp));
						}
					}
				//
				// case: Membrane Variable
				//
				}else if ((countMembraneVars > 0 && countMembraneVars == indepVarList.size()) || functionSubdomain instanceof MembraneSubDomain){
					MemVariable memVariable = new MemVariable(function.getName(),function.getDomain());
					newMath.getVariableList().remove(function);
					newMath.getVariableList().add(memVariable);
					newMath.getVariableMap().remove(function.getName());
					newMath.getVariableMap().put(memVariable.getName(), memVariable);
					//
					// determine which membrane subdomains to add equations to
					//    if domain information is available, then restrict the creation of equations to that subdomain,
					//    else add equation to all subdomains of the same type (here: any MembraneSubDomain)
					//
					Set<MembraneSubDomain> membraneSubDomains = new LinkedHashSet<>();
					if (functionSubdomain != null){
						membraneSubDomains.add((MembraneSubDomain) functionSubdomain);
					}else if (countMembraneVars > 0){
						for (Variable indepVar : indepVarList){
							if (indepVar instanceof MemVariable && indepVar.getDomain() != null){
								membraneSubDomains.add((MembraneSubDomain) newMath.getSubDomain(indepVar.getDomain().getName()));
							}
						}
					}else{
						for (SubDomain subDomain : newMath.getSubDomainCollection()){
							if (subDomain instanceof MembraneSubDomain){
								membraneSubDomains.add((MembraneSubDomain) subDomain);
							}
						}
					}
					for (MembraneSubDomain membraneSubDomain : membraneSubDomains){
						//
						// add an ODE where
						//    initial value = K0 + Sum(coefficient_i*Var_i.init)
						//    rate value = Sum(coefficient_i*Var_i.rate)
						//
						Expression initExp = new Expression(K0);
						Expression rateExp = new Expression(0.0);
						for (int k = 0; k < indepVarList.size(); k++){
							Variable indepVar = indepVarList.get(k);
							Equation indepVarEqu = membraneSubDomain.getEquation(indepVar);
							Expression coefficient = coefficientList.get(k);
							initExp = Expression.add(initExp,Expression.mult(new Expression(coefficient),new Expression(indepVarEqu.getInitialExpression())));
							rateExp = Expression.add(rateExp,Expression.mult(new Expression(coefficient),new Expression(indepVarEqu.getRateExpression())));
						}
						for (Variable var : newMath.getVariableList()){
							if (var.getName().startsWith(function.getName()+ MathDescription.MATH_FUNC_INIT_SUFFIX_PREFIX)){
								Variable initVariable = var;
								initExp = new Expression(initVariable,null);
								break;
							}
						}
						OdeEquation odeEquation = new OdeEquation(memVariable, initExp.flatten(), rateExp.flatten());
						membraneSubDomain.addEquation(odeEquation);
						if (membraneSubDomain.getFastSystem()!=null){
							membraneSubDomain.getFastSystem().addFastInvariant(new FastInvariant(fastInvariantExp));
						}
					}
				//
				// case: Filament Variable
				//
				}else if ((countFilamentVars > 0 && countFilamentVars == indepVarList.size()) || functionSubdomain instanceof FilamentSubDomain){
					FilamentVariable filamentVariable = new FilamentVariable(function.getName(),function.getDomain());
					newMath.getVariableList().remove(function);
					newMath.getVariableList().add(filamentVariable);
					newMath.getVariableMap().remove(function.getName());
					newMath.getVariableMap().put(filamentVariable.getName(), filamentVariable);
					//
					// determine which filament subdomains to add equations to
					//    if domain information is available, then restrict the creation of equations to that subdomain,
					//    else add equation to all subdomains of the same type (here: any FilamentSubDomain)
					//
					Set<FilamentSubDomain> filamentSubDomains = new LinkedHashSet<>();
					if (functionSubdomain != null){
						filamentSubDomains.add((FilamentSubDomain) functionSubdomain);
					}else if (countFilamentVars > 0){
						for (Variable indepVar : indepVarList){
							if (indepVar instanceof FilamentVariable && indepVar.getDomain() != null){
								filamentSubDomains.add((FilamentSubDomain) newMath.getSubDomain(indepVar.getDomain().getName()));
							}
						}
					}else{
						for (SubDomain subDomain : newMath.getSubDomainCollection()){
							if (subDomain instanceof FilamentSubDomain){
								filamentSubDomains.add((FilamentSubDomain) subDomain);
							}
						}
					}
					for (FilamentSubDomain filamentSubDomain : filamentSubDomains){
						//
						// add an ODE where
						//    initial value = K0 + Sum(coefficient_i*Var_i.init)
						//    rate value = Sum(coefficient_i*Var_i.rate)
						//
						Expression initExp = new Expression(K0);
						Expression rateExp = new Expression(0.0);
						for (int k = 0; k < indepVarList.size(); k++){
							Variable indepVar = indepVarList.get(k);
							Equation indepVarEqu = filamentSubDomain.getEquation(indepVar);
							Expression coefficient = coefficientList.get(k);
							initExp = Expression.add(initExp,Expression.mult(new Expression(coefficient),new Expression(indepVarEqu.getInitialExpression())));
							rateExp = Expression.add(rateExp,Expression.mult(new Expression(coefficient),new Expression(indepVarEqu.getRateExpression())));
						}
						for (Variable var : newMath.getVariableList()){
							if (var.getName().startsWith(function.getName()+ MathDescription.MATH_FUNC_INIT_SUFFIX_PREFIX)){
								Variable initVariable = var;
								initExp = new Expression(initVariable,null);
								break;
							}
						}
						OdeEquation odeEquation = new OdeEquation(filamentVariable, initExp.flatten(), rateExp.flatten());
						filamentSubDomain.addEquation(odeEquation);
					}

				//
				// case: Point Variable
				//
				}else if ((countPointVars > 0 && countPointVars == indepVarList.size()) || functionSubdomain instanceof PointSubDomain){
					PointVariable pointVariable = new PointVariable(function.getName(),function.getDomain());
					newMath.getVariableList().remove(function);
					newMath.getVariableList().add(pointVariable);
					newMath.getVariableMap().remove(function.getName());
					newMath.getVariableMap().put(pointVariable.getName(), pointVariable);
					//
					// determine which point subdomains to add equations to
					//    if domain information is available, then restrict the creation of equations to that subdomain,
					//    else add equation to all subdomains of the same type (here: any PointSubDomain)
					//
					Set<PointSubDomain> pointSubDomains = new LinkedHashSet<>();
					if (functionSubdomain != null){
						pointSubDomains.add((PointSubDomain) functionSubdomain);
					}else if (countPointVars > 0){
						for (Variable indepVar : indepVarList){
							if (indepVar instanceof PointVariable && indepVar.getDomain() != null){
								pointSubDomains.add((PointSubDomain) newMath.getSubDomain(indepVar.getDomain().getName()));
							}
						}
					}else{
						for (SubDomain subDomain : newMath.getSubDomainCollection()){
							if (subDomain instanceof PointSubDomain){
								pointSubDomains.add((PointSubDomain) subDomain);
							}
						}
					}
					for (PointSubDomain pointSubDomain : pointSubDomains){
						//
						// add an ODE where
						//    initial value = K0 + Sum(coefficient_i*Var_i.init)
						//    rate value = Sum(coefficient_i*Var_i.rate)
						//
						Expression initExp = new Expression(K0);
						Expression rateExp = new Expression(0.0);
						for (int k = 0; k < indepVarList.size(); k++){
							Variable indepVar = indepVarList.get(k);
							Equation indepVarEqu = pointSubDomain.getEquation(indepVar);
							Expression coefficient = coefficientList.get(k);
							initExp = Expression.add(initExp,Expression.mult(new Expression(coefficient),new Expression(indepVarEqu.getInitialExpression())));
							rateExp = Expression.add(rateExp,Expression.mult(new Expression(coefficient),new Expression(indepVarEqu.getRateExpression())));
						}
						for (Variable var : newMath.getVariableList()){
							if (var.getName().startsWith(function.getName()+ MathDescription.MATH_FUNC_INIT_SUFFIX_PREFIX)){
								Variable initVariable = var;
								initExp = new Expression(initVariable,null);
								break;
							}
						}
						OdeEquation odeEquation = new OdeEquation(pointVariable, initExp.flatten(), rateExp.flatten());
						pointSubDomain.addEquation(odeEquation);
					}

				//
				//
				//
				}else{
					throw new RuntimeException("create canonicalMath cannot handle mixture of dependent vars types");
				}
		}

		return newMath;
	}
}
