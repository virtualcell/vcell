/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.vcell.util.BeanUtils;

import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.mapping.DiffEquMathMapping;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.FilamentRegionVariable;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.Function;
import cbit.vcell.math.InconsistentDomainException;
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.MathSymbolTable;
import cbit.vcell.math.MathSymbolTableFactory;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.FunctionInvocation;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.AnnotatedFunction.FunctionCategory;
/**
 * Specifies the problem to be solved by a solver.
 * It is subclassed for each type of problem/solver.
 * Creation date: (8/16/2000 11:08:33 PM)
 * @author: John Wagner
 */
public class SimulationSymbolTable implements ScopedSymbolTable, MathSymbolTable {
	private Simulation simulation = null;
	//	
	private transient HashMap<String, Variable> localVariableHash = null;	
	/**
	 * Field for multiplexing and spawning job arrays
	 * Working sims created when necessarry with appropriate index
	 */
	private int index = 0;	
	private NameScope nameScope = new SimulationNameScope();

	@SuppressWarnings("serial")
	public class SimulationNameScope extends AbstractNameScope {
		public SimulationNameScope(){
			super();
		}

		@Override
		public NameScope[] getChildren() {
			return null;
		}

		@Override
		public String getName() {
			return simulation.getName();
		}

		@Override
		public NameScope getParent() {
			return null;
		}

		@Override
		public ScopedSymbolTable getScopedSymbolTable() {
			return SimulationSymbolTable.this;
		}

	}
	
/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a new Simulation.
 */
public SimulationSymbolTable(Simulation arg_simulation, int arg_index) {
	super();
	simulation = arg_simulation;
	index = arg_index;
	rebindAll();  // especially needed to bind Constants so that .substitute() will eliminate Constants that are functions of other Constants.
}

public final Simulation getSimulation() {
	return simulation;
}

public final MathDescription getMathDescription(){
	return simulation.getMathDescription();
}

/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(java.lang.String identifierString) {
	//
	// use MathDescription as the primary SymbolTable, just replace the Constants with the overrides.
	//
	SymbolTableEntry ste = simulation.getMathDescription().getEntry(identifierString);
	if (ste instanceof Constant){
		try {
			Constant constant = getLocalConstant((Constant)ste);
			return constant;
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("Simulation.getEntry(), error getting local Constant (math override)"+identifierString);
		}
	}else if (ste instanceof Function){
		try {
			Function function = getLocalFunction((Function)ste);
			return function;
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("Simulation.getEntry(), error getting local Function "+identifierString+", "+e.getMessage());
		}
	}else{
		return ste;
	}
}

public void applyOverrides(MathDescription newMath) throws ExpressionException, MappingException, MathException {
	
	//
	// replace original constants with "Simulation" constants
	//
	Variable newVarArray[] = (Variable[])BeanUtils.getArray(newMath.getVariables(),Variable.class);
	for (int i = 0; i < newVarArray.length; i++){
		if (newVarArray[i] instanceof Constant){
			Constant origConstant = (Constant)newVarArray[i];
			Constant simConstant = getLocalConstant(origConstant);
			newVarArray[i] = new Constant(origConstant.getName(),new Expression(simConstant.getExpression()));
		}
	}
	newMath.setAllVariables(newVarArray);
}

/**
 * Insert the method's description here.
 * Creation date: (5/25/01 11:34:08 AM)
 * @return cbit.vcell.math.Variable[]
 */
public Function[] getFunctions() {
	
	Vector<Function> functList = new Vector<Function>();
	
	//
	// get all variables from MathDescription, but replace MathOverrides
	//
	Variable variables[] = getVariables();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof Function){
			functList.addElement((Function)variables[i]);
		}
	}

	return (Function[])BeanUtils.getArray(functList,Function.class);
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2001 7:52:15 PM)
 * @return cbit.vcell.math.Function
 * @param functionName java.lang.String
 */
private Constant getLocalConstant(Constant referenceConstant) throws ExpressionException {
	if (localVariableHash == null) {
		localVariableHash = new HashMap<String, Variable>();
	}
	Variable var = localVariableHash.get(referenceConstant.getName());
	if (var instanceof Constant) {
		Constant localConstant = (Constant)var;
	
		//
		// make sure expression for localConstant is still up to date with MathOverrides table
		//
		Expression exp = simulation.getMathOverrides().getActualExpression(referenceConstant.getName(), index);
		if (exp.compareEqual(localConstant.getExpression())){
			//localConstant.bind(this); // update bindings to latest mathOverrides
			return localConstant;
		} else {
			//
			// MathOverride's Expression changed for this Constant, remove and create new one
			//
			localVariableHash.remove(localConstant.getName());
		}	
	} else if (var != null) {
		throw new RuntimeException("Variable " + var + " expected to be a Constant");
	}
	//
	// if local Constant not found, create new one, bind it to the Simulation (which ensures MathOverrides), and add to list
	//
	String name = referenceConstant.getName();
	Constant newLocalConstant = new Constant(name,simulation.getMathOverrides().getActualExpression(name, index));
	//newLocalConstant.bind(this);	
	localVariableHash.put(newLocalConstant.getName(), newLocalConstant);	
	return newLocalConstant;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2001 7:52:15 PM)
 * @return cbit.vcell.math.Function
 * @param functionName java.lang.String
 */
private Function getLocalFunction(Function referenceFunction) throws ExpressionException {
	if (localVariableHash == null) {
		localVariableHash = new HashMap<String, Variable>();
	}
	Variable var = localVariableHash.get(referenceFunction.getName());
	if (var instanceof Function) {
		Function localFunction = (Function)var;
		if (localFunction.compareEqual(referenceFunction)){
			//localFunction.bind(this); // update bindings to latest mathOverrides
			return localFunction;
		}
	} else if (var != null) {
		throw new RuntimeException("Variable " + var + " expected to be a Function");
	}
	//
	// if local Function not found, create new one, bind it to the Simulation (which ensures MathOverrides), and add to list
	//
	Function newLocalFunction = new Function(referenceFunction.getName(),referenceFunction.getExpression(),referenceFunction.getDomain());
	//newLocalFunction.bind(this);
	localVariableHash.put(newLocalFunction.getName(), newLocalFunction);
	
	return newLocalFunction;
}


/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 * @param exp cbit.vcell.parser.Expression
 */
public Enumeration<Variable> getRequiredVariables(Expression exp) throws MathException, ExpressionException {
	return MathUtilities.getRequiredVariables(exp,this);
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/01 12:31:53 PM)
 * @return cbit.vcell.math.Variable
 * @param variableName java.lang.String
 */
public Variable getVariable(String variableName) {
	return (Variable)getEntry(variableName);
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/01 11:34:08 AM)
 * @return cbit.vcell.math.Variable[]
 */
public Variable[] getVariables() {
	
	Vector<Variable> varList = new Vector<Variable>();
	
	//
	// get all variables from MathDescription, but replace MathOverrides
	//
	Enumeration<Variable> enum1 = simulation.getMathDescription().getVariables();
	while (enum1.hasMoreElements()) {
		Variable mathDescriptionVar = enum1.nextElement();
		//
		// replace all Constants with math overrides
		//
		if (mathDescriptionVar instanceof Constant){
			try {
				Constant overriddenConstant = getLocalConstant((Constant)mathDescriptionVar);
				varList.addElement(overriddenConstant);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("local Constant "+mathDescriptionVar.getName()+" not found for Simulation");
			}
		//
		// replace all Functions with local Functions that are bound to this Simulation
		//
		}else if (mathDescriptionVar instanceof Function){
			try {
				Function overriddenFunction = getLocalFunction((Function)mathDescriptionVar);
				varList.addElement(overriddenFunction);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("local Function "+mathDescriptionVar.getName()+" not found for Simulation");
			}
		//
		// pass all other Variables through
		//
		}else{
			varList.addElement(mathDescriptionVar);
		}
	}

	Variable variables[] = (Variable[])BeanUtils.getArray(varList,Variable.class);

	return variables;
}


/**
 * Insert the method's description here.
 * Creation date: (6/20/01 12:35:46 PM)
 */
private void rebindAll() {
	//
	// cleanup
	//
	if (localVariableHash != null) {
		localVariableHash.clear();
	}
	
	// reload
	getVariables();

	//
	// bind all Variables, since now all the variables are sorted alphabetically
	// a constant might be function of other constants which have not bound yet.
	// so we need to first bind all then evaluate.
	//
	if (localVariableHash != null) {
		for (Variable variable : localVariableHash.values()){
			try {
				variable.bind(this); // update bindings to latest mathOverrides
			}catch (ExpressionBindingException e){
				e.printStackTrace(System.out);
			}
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 * @param exp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public Expression substituteFunctions(Expression exp) throws MathException, ExpressionException {
	return MathUtilities.substituteFunctions(exp,this);
}


public void getEntries(Map<String, SymbolTableEntry> entryMap) {		
		simulation.getMathDescription().getEntries(entryMap);
		entryMap.putAll(localVariableHash);
	}


	public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
		getEntries(entryMap);		
	}


	public SymbolTableEntry getLocalEntry(String identifier) {
		return getEntry(identifier);
	}


	public NameScope getNameScope() {
		return nameScope;
	}


	public boolean hasTimeVaryingDiffusionOrAdvection() throws MathException, ExpressionException {
		for (Variable var : getVariables()) {
			if (hasTimeVaryingDiffusionOrAdvection(var)) {
				return true;
			}
		}
		return false;
	}
		
	public boolean hasTimeVaryingDiffusionOrAdvection(Variable variable) throws MathException, ExpressionException {
	
		Enumeration<SubDomain> enum1 = simulation.getMathDescription().getSubDomains();
		while (enum1.hasMoreElements()){
			SubDomain subDomain = enum1.nextElement();
			Equation equation = subDomain.getEquation(variable);
			//
			// get diffusion expressions, see if function of time or volume variables
			//
			if (equation instanceof PdeEquation){
				Vector<Expression> spatialExpressionList = new Vector<Expression>();
				spatialExpressionList.add(((PdeEquation)equation).getDiffusionExpression());
				if (((PdeEquation)equation).getVelocityX()!=null){
					spatialExpressionList.add(((PdeEquation)equation).getVelocityX());
				}
				if (((PdeEquation)equation).getVelocityY()!=null){
					spatialExpressionList.add(((PdeEquation)equation).getVelocityY());
				}
				if (((PdeEquation)equation).getVelocityZ()!=null){
					spatialExpressionList.add(((PdeEquation)equation).getVelocityZ());
				}
				for (int i = 0; i < spatialExpressionList.size(); i++){
					Expression spatialExp = spatialExpressionList.elementAt(i);
					spatialExp = substituteFunctions(spatialExp);
					String symbols[] = spatialExp.getSymbols();
					if (symbols!=null){
						for (int j=0;j<symbols.length;j++){
							SymbolTableEntry entry = spatialExp.getSymbolBinding(symbols[j]);
							if (entry instanceof ReservedVariable){
								if (((ReservedVariable)entry).isTIME()){
									return true;
								}
							}
							if (entry instanceof VolVariable){
								return true;
							}
							if (entry instanceof VolumeRegionVariable){
								return true;
							}
							if (entry instanceof MemVariable || entry instanceof MembraneRegionVariable) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;		
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/6/2001 10:57:51 AM)
	 * @return boolean
	 * @param function cbit.vcell.math.Function
	 */
	public static boolean isFunctionSaved(Function function) {
		String name = function.getName();
		if (!name.startsWith("Kflux_") &&
//			!name.endsWith(MathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_molecule_per_um2) && 
//			!name.endsWith(MathMapping.MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_uM) && 
			!name.endsWith(DiffEquMathMapping.PARAMETER_MASS_CONSERVATION_SUFFIX) &&
			!name.equals(Simulation.PSF_FUNCTION_NAME)){
			return true;
		}else{
			return false;
		}
	}

	public Vector<AnnotatedFunction> createAnnotatedFunctionsList(MathDescription mathDescription) throws InconsistentDomainException {	
		// Get the list of (volVariables) in the simulation. Needed to determine 'type' of  functions
		boolean bSpatial = getSimulation().isSpatial();
		String[] variableNames = null;
		VariableType[] variableTypes = null;
		
		if (bSpatial) {
			Variable[] allVariables = getVariables();
			Vector<Variable> varVector = new Vector<Variable>();
			for (int i = 0; i < allVariables.length; i++){
				if ((allVariables[i] instanceof VolVariable) 
					|| (allVariables[i] instanceof VolumeRegionVariable) 
					|| (allVariables[i] instanceof MemVariable) 
					|| (allVariables[i] instanceof MembraneRegionVariable) 
					|| (allVariables[i] instanceof FilamentVariable) 
					|| (allVariables[i] instanceof FilamentRegionVariable) 
					|| (allVariables[i] instanceof InsideVariable) 
					|| (allVariables[i] instanceof OutsideVariable)) {
					varVector.addElement(allVariables[i]);
				}
			}
			variableNames = new String[varVector.size()];
			for (int i = 0; i < variableNames.length; i++){
				variableNames[i] = varVector.get(i).getName();
			}
		
			// Lookup table for variableType for each variable in 'variables' array.
			variableTypes = new VariableType[variableNames.length];
			for (int i = 0; i < variableNames.length; i++){
				variableTypes[i] = VariableType.getVariableType(varVector.get(i));
			}
		}	
		//
		// Bind and substitute functions to simulation before storing them in the '.functions' file
		//
		Function[] functions = getFunctions();
		Vector<AnnotatedFunction> annotatedFunctionVector = new Vector<AnnotatedFunction>();
		for (int i = 0; i < functions.length; i++){
			if (isFunctionSaved(functions[i])) {
				String errString = "";
				VariableType funcType = null;		
				try {
					Expression substitutedExp = substituteFunctions(functions[i].getExpression());
					substitutedExp.bindExpression(this);
					functions[i].setExpression(substitutedExp.flatten());
				}catch (MathException e){
					e.printStackTrace(System.out);
					errString = errString+", "+e.getMessage();	
					// throw new RuntimeException(e.getMessage());
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					errString = errString+", "+e.getMessage();				
					// throw new RuntimeException(e.getMessage());
				}
	
				//
				// get function's data type from the types of it's identifiers
				//
				funcType = bSpatial ? getFunctionVariableType(functions[i], mathDescription, variableNames, variableTypes, bSpatial) : VariableType.NONSPATIAL;
	
				AnnotatedFunction annotatedFunc = new AnnotatedFunction(functions[i].getName(), functions[i].getExpression(), functions[i].getDomain(), errString, funcType, FunctionCategory.PREDEFINED);
				annotatedFunctionVector.addElement(annotatedFunc);
			}
		}
	
		
		return annotatedFunctionVector;	
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (2/19/2004 11:17:15 AM)
	 * @return cbit.vcell.simdata.VariableType
	 * @param function cbit.vcell.math.Function
	 * @param variableNames java.lang.String[]
	 * @param variableTypes cbit.vcell.simdata.VariableType[]
	 */
	public static VariableType getFunctionVariableType(Function function, MathDescription mathDescription, 
			String[] variableNames, VariableType[] variableTypes, boolean isSpatial) throws InconsistentDomainException {
		if (!isSpatial) {
			return VariableType.NONSPATIAL;
		}
		VariableType domainFuncType = null;
		// initial guess, restrict variable type to be consistent with domain.
		if (function.getDomain() != null) {
			String domainName = function.getDomain().getName();
			if (mathDescription != null) {
				SubDomain subdomain = mathDescription.getSubDomain(domainName);
				if (subdomain instanceof MembraneSubDomain) {
					domainFuncType = VariableType.MEMBRANE_REGION;
				} else {
					domainFuncType = VariableType.VOLUME_REGION;
				}
			}
		}
		
		Expression exp = function.getExpression();
		String symbols[] = exp.getSymbols();
		ArrayList<VariableType> varTypeList = new ArrayList<VariableType>();
		boolean bExplicitFunctionOfSpace = false;
		if (symbols != null) {
			for (int j = 0; j < symbols.length; j ++){
				if (symbols[j].equals(ReservedVariable.X.getName()) ||
					symbols[j].equals(ReservedVariable.Y.getName()) ||
					symbols[j].equals(ReservedVariable.Z.getName())) {
					bExplicitFunctionOfSpace = true;
					continue;
				}				
				for (int k = 0; k < variableNames.length; k ++){
					if (symbols[j].equals(variableNames[k])) {
						varTypeList.add(variableTypes[k]);
						break;
					} else if (symbols[j].equals(variableNames[k]+InsideVariable.INSIDE_VARIABLE_SUFFIX) 
							|| symbols[j].equals(variableNames[k]+OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)){						
						if (variableTypes[k].equals(VariableType.VOLUME)){
							varTypeList.add(VariableType.MEMBRANE);
						}else if (variableTypes[k].equals(VariableType.VOLUME_REGION)){
							varTypeList.add(VariableType.MEMBRANE_REGION);
						}
						break;
					}
				}
			}
		}
		
		// Size Functions
		Set<FunctionInvocation> sizeFunctionInvocationSet = SolverUtilities.getSizeFunctionInvocations(function.getExpression());
		for (FunctionInvocation fi : sizeFunctionInvocationSet) {
			String functionName = fi.getFunctionName();
			if (functionName.equals(MathFunctionDefinitions.Function_regionArea_current.getFunctionName())) {
				varTypeList.add(VariableType.MEMBRANE_REGION);
			} else if (functionName.equals(MathFunctionDefinitions.Function_regionVolume_current.getFunctionName())) {
				varTypeList.add(VariableType.VOLUME_REGION);
			}
		}
		
		// Membrane Normal Functions
		FunctionInvocation[] functionInvocations = function.getExpression().getFunctionInvocations(null);
		for (FunctionInvocation fi : functionInvocations) {
			String functionName = fi.getFunctionName();
			if (functionName.equals(MathFunctionDefinitions.Function_normalX.getFunctionName())
				|| functionName.equals(MathFunctionDefinitions.Function_normalY.getFunctionName())){
				varTypeList.add(VariableType.MEMBRANE);
			}
		}
			
		FieldFunctionArguments[] fieldFuncArgs = FieldUtilities.getFieldFunctionArguments(function.getExpression());
		if (fieldFuncArgs != null && fieldFuncArgs.length > 0) {
			varTypeList.add(fieldFuncArgs[0].getVariableType());
		}
		
		VariableType funcType = domainFuncType;
		for (VariableType vt : varTypeList) {
			if (funcType == null){
				funcType = vt;
			} else {
				//
				// example: if VOLUME_REGION and VOLUME data are used in same function,
				// then function must be evaluated at each volume index (hence VOLUME wins).
				//
				if (vt.isExpansionOf(funcType)){
					funcType = vt;
				} else if (vt.equals(VariableType.VOLUME)) {
					if (funcType.equals(VariableType.MEMBRANE_REGION)) {
						funcType = VariableType.MEMBRANE;
					}
				} else if (vt.equals(VariableType.VOLUME_REGION)) {
					
				} else if (vt.equals(VariableType.MEMBRANE)) {
					if (domainFuncType != null && domainFuncType.getVariableDomain().equals(VariableDomain.VARIABLEDOMAIN_VOLUME)) {
						throw new InconsistentDomainException("Function '" + function.getName() + "' defined on a volume subdomain '" + function.getDomain().getName() 
							+ "' references a variable or a function defined on a membrane subdomain");
					}
				} else if (vt.equals(VariableType.MEMBRANE_REGION)) {
					if (funcType.equals(VariableType.VOLUME)) {
						if (domainFuncType != null && domainFuncType.getVariableDomain().equals(VariableDomain.VARIABLEDOMAIN_VOLUME)) {
							throw new InconsistentDomainException("Function '" + function.getName() + "' defined on '" + function.getDomain().getName() 
								+ "' references a size function defined on a membrane");
						}
						funcType = VariableType.MEMBRANE;
					} else if (funcType.equals(VariableType.VOLUME_REGION)) {
						if (domainFuncType != null && domainFuncType.getVariableDomain().equals(VariableDomain.VARIABLEDOMAIN_VOLUME)) {
							throw new InconsistentDomainException("Function '" + function.getName() + "' defined on '" + function.getDomain().getName() 
								+ "' references a size function defined on a membrane");
						}
						funcType = VariableType.MEMBRANE_REGION;
					}
				} else if (vt.incompatibleWith(funcType)){
					throw new InconsistentDomainException("Function domains conflict between variable domains '"+vt.getDefaultLabel()+"' and '"+funcType.getDefaultLabel()+" for function " + function.getName());
				}
			}
		}
		
		//
		// if determined to be a volume region or membrane region function, 
		// then if it is an explicit function of space, promote type to corresponding non-region type (e.g. volRegion --> volume)
		//	
		if (funcType != null && bExplicitFunctionOfSpace) {
			if (funcType.equals(VariableType.MEMBRANE_REGION)){
				funcType = VariableType.MEMBRANE;
			}else if (funcType.equals(VariableType.VOLUME_REGION)){
				funcType = VariableType.VOLUME;
			}else if (funcType.equals(VariableType.CONTOUR_REGION)){
				funcType = VariableType.CONTOUR;
			}
		}
		
		if (funcType == null) {
			return VariableType.VOLUME; // no knowledge from expression, default variable type
		}
		
		return funcType;
	}

	public static MathSymbolTableFactory createMathSymbolTableFactory() {
		return new MathSymbolTableFactory() {
			public MathSymbolTable createMathSymbolTable(MathDescription newMath) {
				return new SimulationSymbolTable(new Simulation(newMath),0);
			}
		};
	}
}
