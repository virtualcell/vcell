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

import cbit.vcell.math.*;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext.ContextType;

import java.util.*;

/**
 * Constant expressions that override those specified in the MathDescription
 * for a given Simulation.  These expressions are to be bound to the Simulation
 * to which they belong.
 * Creation date: (8/17/2000 3:47:33 PM)
 * @author: John Wagner
 */
public class MathOverrides implements Matchable, java.io.Serializable {

	private final static Logger logger = LogManager.getLogger(MathOverrides.class);
	private Simulation simulation = null;
	//
	// key = constant name (String)
	// value = wrapper containing expression and flag(s) (MathOverrides.Element)
	//
	private java.util.Hashtable<String, Element> overridesHash = new java.util.Hashtable<String, Element>();
	protected transient cbit.vcell.solver.MathOverridesListener aMathOverridesListener = null;
	
	public static class Element implements java.io.Serializable, Matchable {
		private Expression actualValue;
		private String name;
		private ConstantArraySpec spec;
		private Element(String argName, Expression act) {
			// replace expressions with clones (so that they may be separately bound/resolved/evaluated).
			this.name = argName;
			Expression clonedExpression = new Expression(act);
			actualValue = clonedExpression;
		}
		private Element(String argName, ConstantArraySpec argSpec) {
			// replace expressions with clones (so that they may be separately bound/resolved/evaluated).
			this.name = argName;
			this.spec = ConstantArraySpec.clone(argSpec);
		}
		public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
			if (actualValue != null) {
				actualValue.bindExpression(symbolTable);
			}
		}
		public boolean compareEqual(Matchable obj){
			if (obj instanceof MathOverrides.Element){
				MathOverrides.Element element = (MathOverrides.Element)obj;
				if (!Compare.isEqualOrNull(actualValue,element.actualValue) ||
					!Compare.isEqual(name,element.name) ||
					!Compare.isEqualOrNull(spec,element.spec)){
					return false;
				}
				return true;
			}
			return false;
		}
		public Expression getActualValue() {
			return actualValue;
		}
		public String getName() {
			return name;
		}
		public ConstantArraySpec getSpec() {
			return spec;
		}
		
	}

/**
 * One of three ways to construct a MathOverrides.  This constructor
 * is used when creating a new MathOverrides.
 */
public MathOverrides(Simulation simulation) {
	setSimulation(simulation);
}


/**
 * One of three ways to construct a MathOverrides.  This constructor
 * is used when creating a new MathOverrides from XML (we read in Constants to be overriden from XML).
 */
public MathOverrides(Simulation simulation, Constant[] constants, ConstantArraySpec[] specs) throws cbit.vcell.parser.ExpressionException {
	setSimulation(simulation);
	for (int i = 0; i < constants.length; i++){
		putConstant(constants[i], false);
	}
	for (int i = 0; i < specs.length; i++){
		putConstantArraySpec(specs[i], false);
	}
}


/**
 * One of three ways to construct a MathOverrides.  This constructor
 * is used when creating a MathOverrides from the database.
 */
public MathOverrides(Simulation simulation, CommentStringTokenizer tokenizer) throws DataAccessException {
	setSimulation(simulation);
	readVCML(tokenizer);
}


/**
 * One of three ways to construct a MathOverrides.  This constructor
 * is used when copying a MathOverrides from an existing one.
 */
public MathOverrides(Simulation simulation, MathOverrides mathOverrides) {
	setSimulation(simulation);
	java.util.Set<String> keySet = mathOverrides.overridesHash.keySet();
	java.util.Iterator<String> keyIter = keySet.iterator();
	while (keyIter.hasNext()) {
		String constantName = keyIter.next();
		MathOverrides.Element element = mathOverrides.getOverridesElement(constantName);
		if(element.actualValue != null) {
			// regular override
			getOverridesHash().put(constantName, new MathOverrides.Element(element.name,element.actualValue));
		} else {
			// scan override
			getOverridesHash().put(constantName, new MathOverrides.Element(element.name,element.spec));
		}
	}
	updateFromMathDescription();
}


/**
 * 
 * @param newListener cbit.vcell.solver.MathOverridesListener
 */
public void addMathOverridesListener(MathOverridesListener newListener) {
	aMathOverridesListener = MathOverridesEventMulticaster.add(aMathOverridesListener, newListener);
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:23:14 PM)
 * @return boolean
 */
public boolean compareEqual(Matchable obj) {
	if (!(obj instanceof MathOverrides)){
		return false;
	}
	boolean returnValue = Compare.isEqual(toVector(getOverridesHash().elements()),toVector(((MathOverrides)obj).getOverridesHash().elements()));
	return returnValue;
}


public boolean compareEquivalent(MathOverrides oldMathOverrides) {
	// first see if they are equal
	if (compareEqual(oldMathOverrides)){
		return true;
	}
	// if not, see if they are equivalent (calls updateFromMathDescription() which corrects obsolete overridden names).
	MathOverrides updatedMathOverrides = new MathOverrides(getSimulation(), oldMathOverrides);
	return compareEqual(updatedMathOverrides);
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
protected void fireConstantAdded(cbit.vcell.solver.MathOverridesEvent event) {
	if (aMathOverridesListener == null) {
		return;
	};
	aMathOverridesListener.constantAdded(event);
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
protected void fireConstantChanged(cbit.vcell.solver.MathOverridesEvent event) {
	if (aMathOverridesListener == null) {
		return;
	};
	aMathOverridesListener.constantChanged(event);
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
protected void fireConstantRemoved(cbit.vcell.solver.MathOverridesEvent event) {
	if (aMathOverridesListener == null) {
		return;
	};
	aMathOverridesListener.constantRemoved(event);
}


/**
 * Returns the value to which the specified key is mapped in this hashtable.
 *
 * @param   key   a key in the hashtable.
 * @return  the value to which the key is mapped in this hashtable;
 *          <code>null</code> if the key is not mapped to any value in
 *          this hashtable.
 */
public Expression getActualExpression(String key, int index) {
	MathOverrides.Element element = getOverridesElement(key);
	if (element==null){
		// not overriden
		return getDefaultExpression(key);
	} else if (!isScan(key)){
		// regular override
		return element.actualValue;
	} else{
		// scanned parameter
		String[] names = getScannedConstantNames();
		java.util.Arrays.sort(names); // must do things in a consistent way
		int[] bounds = new int[names.length]; // bounds of scanning matrix
		for (int i = 0; i < names.length; i++){
			bounds[i] = getConstantArraySpec(names[i]).getNumValues() - 1;
		}
		int[] coordinates = BeanUtils.indexToCoordinate(index, bounds);
		int localIndex = coordinates[java.util.Arrays.binarySearch(names, key)];
		return getConstantArraySpec(key).getConstants()[localIndex].getExpression();
	}
}


public String[] getAllConstantNames() {
	Enumeration<Constant> en = getSimulation().getMathDescription().getConstants();
	java.util.Vector<String> v = new java.util.Vector<String>();
	while (en.hasMoreElements()) {
		v.add((en.nextElement()).getName());
	}
	//
	// add in the overridden parameters that don't exist in the math (these are errors, but have to be displayed).
	//
	for (String overridden : getOverridesHash().keySet()){
		if (!v.contains(overridden)){
			v.add(overridden);
		}
	}

	return (String[])BeanUtils.getArray(v, String.class);
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2006 4:35:43 PM)
 * @return cbit.vcell.math.Constant
 * @param constantName java.lang.String
 */
public Constant getConstant(String constantName) {
	 Variable variable = getSimulation().getMathDescription().getVariable(constantName);
	 if(variable instanceof Constant){
		 return (Constant)variable;
	 }

	 return null;
}


/**
 * Returns the value to which the specified key is mapped in this hashtable.
 *
 * @param   key   a key in the hashtable.
 * @return  the value to which the key is mapped in this hashtable;
 *          <code>null</code> if the key is not mapped to any value in
 *          this hashtable.
 */
public ConstantArraySpec getConstantArraySpec(String key) {
	if(isScan(key)) {
		return getOverridesElement(key).spec;
	} else {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 9:12:17 AM)
 * @return cbit.vcell.parser.Expression
 * @param key java.lang.String
 */
public Expression getDefaultExpression(String key) {
	Variable var = getSimulation().getMathDescription().getVariable(key);
	if (var instanceof Constant){
		Constant c = (Constant)var;
		return new Expression(c.getExpression()); // always returning clones...
	}else{
		return null;
	}
}


public String[] getOverridenConstantNames() {
	String keys[] = new String[getOverridesHash().size()];
	getOverridesHash().keySet().toArray(keys);
	return keys;
}


/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 8:07:27 PM)
 * @return cbit.vcell.solver.MathOverrides.Element
 * @param key java.lang.String
 */
private MathOverrides.Element getOverridesElement(String key) {
	return (MathOverrides.Element)getOverridesHash().get(key);
}


/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 7:39:57 PM)
 * @return java.util.Hashtable
 */
private java.util.Hashtable<String, Element> getOverridesHash() {
	return overridesHash;
}


/**
 * Insert the method's description here.
 * Creation date: (9/26/2005 2:54:25 PM)
 * @return int
 */
public int getScanCount() {
	int num = 1;
	String[] scannedParams = getScannedConstantNames();
	for (int i = 0; i < scannedParams.length; i++){
		num *= getConstantArraySpec(scannedParams[i]).getNumValues();
	}
	return num;
}


public String[] getScannedConstantNames() {
	String[] overrides = getOverridenConstantNames();
	java.util.Vector<String> v = new java.util.Vector<String>();
	for (int i = 0; i < overrides.length; i++){
		if (isScan(overrides[i])) v.add(overrides[i]);
	}
	return (String[])BeanUtils.getArray(v, String.class);
}


/**
 * Insert the method's description here.
 * Creation date: (9/30/2004 8:48:43 PM)
 * @return cbit.vcell.solver.Simulation
 */
public Simulation getSimulation() {
	return simulation;
}


public int getSize() {
	return(getOverridesHash().size());
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 12:41:09 PM)
 * @return java.lang.String
 */
public String getVCML() {

	//
	// write format as follows:
	//
	//   MathOverrides {
	//       Constant K1 200;
	//       Constant K2 400;
	//       Constant K3 494.0;
	//   }
	//
	//	
	StringBuffer buffer = new StringBuffer();

	buffer.append(VCML.MathOverrides+" "+VCML.BeginBlock+"\n");
	
	java.util.Enumeration<String> enum1 = getOverridesHash().keys();
	while (enum1.hasMoreElements()){
		String name = enum1.nextElement();
		MathOverrides.Element element = (MathOverrides.Element)getOverridesHash().get(name);
		Expression exp = element.actualValue;
		if (exp != null) {
			// regular override
			Constant constant = new Constant(name,exp);
			buffer.append("   "+constant.getVCML());
		} else {
			// scan override
			buffer.append("   "+element.spec.getVCML());
		}
	}

	buffer.append("\n"+VCML.EndBlock+"\n");
		
	return buffer.toString();
}

/**
 * MathOverrides has overrides if the overridesHash is not empty.
 * Returns <true> if overridesHash is not empty (has overrides) and <false> is overridesHash is empty (no overrides)
 */
public boolean hasOverrides() {
	if (getOverridesHash().isEmpty()) {
		return false;
	} else {
		return true;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 9:14:00 AM)
 * @return boolean
 * @param key java.lang.String
 */
public boolean isDefaultExpression(String key) {
	return !getOverridesHash().containsKey(key);
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2005 3:20:48 PM)
 * @return boolean
 * @param parameterName java.lang.String
 */
public boolean isScan(String parameterName) {
	return !isDefaultExpression(parameterName) && (getOverridesElement(parameterName).spec != null);
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/01 11:44:22 AM)
 * @return boolean
 */
public boolean isValid(MathDescription mathDescription) {
	//
	// returns true if MathOverrides has exactly the same list of Constants as MathDescription
	//
	
	//
	// look for Constants from MathDescription not present in Overrides
	//
	Enumeration<Constant> enumeration = mathDescription.getConstants();
	java.util.HashSet<String> mathDescriptionHash = new java.util.HashSet<String>();
	while (enumeration.hasMoreElements()) {
		Constant constant = enumeration.nextElement();
		mathDescriptionHash.add(constant.getName());
		if (!getOverridesHash().containsKey(constant.getName())){
			return false;
		}
	}
	//
	//  look for Constants from Overrides not present in MathDescription
	//
	Enumeration<String> mathOverrideNamesEnum = getOverridesHash().keys();
	while (mathOverrideNamesEnum.hasMoreElements()){
		String name = mathOverrideNamesEnum.nextElement();
		if (!mathDescriptionHash.contains(name)){
			return false;
		}
	}
	return true;
}


/**
 * Maps the specified <code>key</code> to the specified 
 * <code>value</code> . Neither the key nor the 
 * value can be <code>null</code>. <p>
 */
public void putConstant(Constant value) throws ExpressionException {
	putConstant(value, true);
}

private void removeConstant(String name) {
	getOverridesHash().remove(name);	
	fireConstantRemoved(new MathOverridesEvent(name));
}
/**
 * Maps the specified <code>key</code> to the specified 
 * <code>value</code> . Neither the key nor the 
 * value can be <code>null</code>. <p>
 */
private void putConstant(Constant value, boolean bFireEvent) throws ExpressionException {
	//
	// verify that new expression can be bound properly
	//
	verifyExpression(value, false);
	// put new element in the hash if value different from default
	// if same as default, remove element if we had it in hash, otherwise do nothing
	String name = value.getName();
	Expression act = value.getExpression();
	Expression def = null;
	Variable var = getSimulation().getMathDescription().getVariable(name);
	if (var != null && var instanceof Constant) {
		def = ((Constant)var).getExpression();
	} else {
		// ignore
		logger.error("Math does not have constant with name: "+name);
		return;
	}
	if (act.compareEqual(def)) {
		if (getOverridesHash().containsKey(name)) {
			removeConstant(name);
		}
	} else {
		getOverridesHash().put(name, new MathOverrides.Element(name, act));
	}
	if (bFireEvent) {
		fireConstantChanged(new MathOverridesEvent(value.getName()));
	}
}


/**
 * Maps the specified <code>key</code> to the specified 
 * <code>value</code> . Neither the key nor the 
 * value can be <code>null</code>. <p>
 */
public void putConstantArraySpec(ConstantArraySpec spec) throws cbit.vcell.parser.ExpressionException {
	putConstantArraySpec(spec, true);
}


/**
 * Maps the specified <code>key</code> to the specified 
 * <code>value</code> . Neither the key nor the 
 * value can be <code>null</code>. <p>
 */
private void putConstantArraySpec(ConstantArraySpec spec, boolean bFireEvent) throws cbit.vcell.parser.ExpressionException {
	//
	// verify that expressions can be bound properly and make Expression array
	//
	String name = spec.getName();
	Constant[] constants = spec.getConstants();
	for (int i = 0; i < constants.length; i++){
		verifyExpression(constants[i], true); // this will throw if there's a problem with the expression
	}
	// put new element in the hash
	// always different from default, since it's a scan list
	getOverridesHash().put(name, new MathOverrides.Element(name, spec));
	if (bFireEvent) {
		fireConstantChanged(new MathOverridesEvent(name));
	}
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
private void readVCML(CommentStringTokenizer tokens) throws DataAccessException {

	List<MathOverrides.Element> parsedElements = parseOverrideElementsFromVCML(tokens);
	for (Element element : parsedElements) {
		if (element.actualValue!=null){
			Expression act = element.actualValue;
			Expression def = getDefaultExpression(element.name);
			//
			// ignore override if not present in math or if it is the same
			//
			if (def!=null && !act.compareEqual(def)){
				getOverridesHash().put(element.name, element);
			}
		}else{
			getOverridesHash().put(element.name, element);
		}
	}
}


/**
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public static List<Element> parseOverrideElementsFromVCML(CommentStringTokenizer tokens) throws DataAccessException {


	//  Read format as follows:
	//
	//   MathOverrides {
	//       Constant K1 200;
	//       Constant K2 400;
	//       Constant K3 494.0;
	//   }
	ArrayList<Element> elements = new ArrayList<Element>();
	try {
		String token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCML.MathOverrides)) {
			token = tokens.nextToken();
			if (!token.equalsIgnoreCase(VCML.BeginBlock)) {
				throw new DataAccessException(
					"unexpected token " + token + " expecting " + VCML.BeginBlock); 
			}
		}
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			if (token.equalsIgnoreCase(VCML.EndBlock)) {
				break;
			}
			if (token.equalsIgnoreCase(VCML.Constant)) {
				String name = tokens.nextToken();
				Expression act = MathFunctionDefinitions.fixFunctionSyntax(tokens);
				elements.add(new MathOverrides.Element(name, act));
				continue;
			}
			if (token.equalsIgnoreCase(VCML.ConstantArraySpec)) {
				String name = tokens.nextToken();
				int type = Integer.parseInt(tokens.nextToken());
				String description = "";
				boolean bDone = false;
				while (!bDone) {
					String t = tokens.nextToken();
					if (t.equals(";")) {
						bDone = true;
					} else if (t.endsWith(";") && (t.length() > 1)) {
						description += t.substring(0,t.length()-1);
						bDone = true;
					} else {
						description += t + " ";
					}
				}
				elements.add(new MathOverrides.Element(name, ConstantArraySpec.createFromString(name, description.trim(), type)));
				continue;
			}
			throw new DataAccessException("unexpected identifier " + token);
		}
		return elements;
	} catch (Exception e) {
		throw new DataAccessException(
			"line #" + (tokens.lineIndex()+1) + " Exception: " + e.getMessage(),e); 
	}
}


/**
 * 
 * @param newListener cbit.vcell.solver.MathOverridesListener
 */
public void removeMathOverridesListener(MathOverridesListener newListener) {
	aMathOverridesListener = MathOverridesEventMulticaster.remove(aMathOverridesListener, newListener);
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (9/30/2004 8:48:43 PM)
 * @param newSimulation cbit.vcell.solver.Simulation
 */
void setSimulation(Simulation newSimulation) {
	simulation = newSimulation;
}


private static java.util.Vector<Element> toVector (java.util.Enumeration<Element> enumeration) {
	java.util.Vector<Element> vector = new java.util.Vector<Element>();
	while (enumeration.hasMoreElements()) {
		vector.add(enumeration.nextElement());
	}
	return (vector);
}

void updateFromMathDescription() {
	MathDescription mathDescription = getSimulation().getMathDescription();
	//
	// get list of names of constants in this math
	//
	Enumeration<Constant> enumeration = mathDescription.getConstants();
	Set<String> mathDescriptionHash = new LinkedHashSet<>();
	while (enumeration.hasMoreElements()) {
		Constant constant = enumeration.nextElement();
		mathDescriptionHash.add(constant.getName());
	}
	LinkedHashSet<String> referencedNames = new LinkedHashSet<>();
	Enumeration<String> mathOverrideNamesEnum = getOverridesHash().keys();
	while (mathOverrideNamesEnum.hasMoreElements()) {
		String name = mathOverrideNamesEnum.nextElement();
		referencedNames.add(name);
		Element element = getOverridesHash().get(name);
		if (element.getSpec() != null) {
			for (Constant constant : element.getSpec().getConstants()){
				String[] symbols = constant.getExpression().getSymbols();
				if (symbols!=null) {
					referencedNames.addAll(Arrays.asList(symbols));
				}
			}
		}
		if (element.getActualValue() != null) {
			String[] symbols = element.getActualValue().getSymbols();
			if (symbols!=null) {
				referencedNames.addAll(Arrays.asList(symbols));
			}
		}
	}

	LinkedHashSet<String> allFactorSymbols = new LinkedHashSet<>();

	HashMap<String, MathOverridesResolver.SymbolReplacement> renamedMap = new HashMap<>();
	for (String name : referencedNames){
		if (!mathDescriptionHash.contains(name)){
			MathOverridesResolver mathOverridesResolver = getSimulation().getSimulationOwner().getMathOverridesResolver();
			if (mathOverridesResolver != null) {
				MathOverridesResolver.SymbolReplacement replacement = mathOverridesResolver.getSymbolReplacement(name);
				if (replacement != null) {
					allFactorSymbols.addAll(replacement.getFactorSymbols());
					Element element = overridesHash.remove(name);
					if (element != null) {
						element.name = replacement.newName;
						if (!replacement.factor.isOne()) {
							element.actualValue = Expression.mult(element.actualValue, replacement.factor);
						}
						overridesHash.put(replacement.newName, element);
						removeConstant(name);
					}
					renamedMap.put(name, replacement);
				}else{
					logger.error("didn't find a replacement for math override symbol " + name);
				}
			}
		}
	}
	
	//
	// for repaired constants, go through all entries and rename expressions where they refer to the renamed constant.
	//
	for (Element element : overridesHash.values()){
		if (element.actualValue!=null) {
			for (String replacedSymbol : renamedMap.keySet()) {
				if (element.actualValue.hasSymbol(replacedSymbol)) {
					try {
						MathOverridesResolver.SymbolReplacement replacement = renamedMap.get(replacedSymbol);
						Expression replacementExp = new Expression(mathDescription.getVariable(replacement.newName), null);
						if (!replacement.factor.isOne()) {
							replacementExp = Expression.mult(Expression.invert(replacement.factor), replacementExp);
						}
						element.actualValue = element.actualValue.getSubstitutedExpression(new Expression(replacedSymbol), replacementExp);

					} catch (ExpressionException e) {
						String msg = "failed to process expression substitution for " + element.name + ", value=" + element.actualValue.infix();
						throw new RuntimeException(msg, e);
					}
				}
			}
		}
	}

	//
	// loop through all expressions and try to flatten factors.
	//
	for (String overriddenName : getOverridesHash().keySet()) {
		Element element = overridesHash.get(overriddenName);
		for (String factorSymbol : allFactorSymbols) {
			try {
				element.actualValue = element.actualValue.flattenFactors(factorSymbol);
			} catch (ExpressionException e) {
				String msg = "failed to simplify unit converted Math Override " + overriddenName + "=" + element.actualValue.infix();
				logger.error(msg, e);
				throw new RuntimeException(msg, e);
			}
		}
		try {
			Expression simplifiedExp = ExpressionUtils.simplifyUsingJSCL(element.actualValue);
			simplifiedExp.bindExpression(mathDescription);
			element.actualValue = simplifiedExp;
		} catch (Exception e){
			logger.error(e);
		}
	}

	refreshDependencies();
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2005 3:24:36 PM)
 */
private void verifyExpression(Constant value, boolean checkScan) throws ExpressionBindingException {
	Expression exp = value.getExpression();
	String symbols[] = exp.getSymbols();
	MathDescription mathDescription = getSimulation().getMathDescription();
	exp.bindExpression(mathDescription);
	if (symbols != null) {
		for (int i = 0; i < symbols.length; i++){
			//
			// expression must be a function of another Simulation parameter
			//
			Variable variable = mathDescription.getVariable(symbols[i]);
			if (!(variable != null && variable instanceof Constant)){
				throw new ExpressionBindingException("identifier " + symbols[i] + " is not a constant. " +
						"Parameter overrides should only refer to constants.");
			}
			if (checkScan && isScan(symbols[i])) {
				throw new ExpressionBindingException("Parameter overrides cannot depend on another scanned parameter "+symbols[i]);
			}
			//
			// look for trivial algebraic loops (x = f(x)).
			//
			if (symbols[i].equals(value.getName())){
				throw new ExpressionBindingException("Parameter overrides can not be recursive definition, can't use identifier "+value.getName()+" in expression for "+value.getName());
			}
		}
	}
}


public void refreshDependencies() {
	try {
		Iterator<MathOverrides.Element> iter = getOverridesHash().values().iterator();
		while (iter.hasNext()){
			iter.next().bind(getSimulation().getMathDescription());
		}
	} catch (ExpressionBindingException e) {
		logger.error("failed to bind a math override: "+e.getMessage(), e);
		throw new RuntimeException(e.getMessage());
	}	
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
	
	MathDescription mathDescription = getSimulation().getMathDescription();
	//
	// get list of names of constants in this math
	//
	Enumeration<Constant> enumeration = mathDescription.getConstants();
	java.util.HashSet<String> mathDescriptionHash = new java.util.HashSet<String>();
	while (enumeration.hasMoreElements()) {
		Constant constant = enumeration.nextElement();
		mathDescriptionHash.add(constant.getName());
	}
	//
	//  for any elements in this MathOverrides but not in the new MathDescription, add an "error" issue
	//
	Enumeration<String> mathOverrideNamesEnum = getOverridesHash().keys();
	while (mathOverrideNamesEnum.hasMoreElements()){
		String name = mathOverrideNamesEnum.nextElement();
		if (!mathDescriptionHash.contains(name)){
			Issue issue = new Issue(getSimulation(), issueContext, IssueCategory.Simulation_Override_NotFound, VCellErrorMessages.getErrorMessage(VCellErrorMessages.SIMULATION_OVERRIDE_NOTFOUND, name, getSimulation().getName()), Issue.SEVERITY_ERROR);
			issueList.add(issue);
		}
		Variable var = mathDescription.getVariable(name);
		if (getSimulation().getSimulationOwner()!=null){
			Issue issue = getSimulation().getSimulationOwner().gatherIssueForMathOverride(issueContext, getSimulation(),name);
			if (issue!=null){
				issueList.add(issue);
			}
		}
	}	
}

/**
 * explicit user action invokes this method (currently pressing a button), this is not automatic cleanup of old/obsolete models ... we want the user to address the issues.
 */
public void removeUnusedOverrides(){
	MathDescription mathDescription = getSimulation().getMathDescription();
	//
	// get list of names of constants in this math
	//
	Enumeration<Constant> enumeration = mathDescription.getConstants();
	java.util.HashSet<String> mathDescriptionHash = new java.util.HashSet<String>();
	while (enumeration.hasMoreElements()) {
		Constant constant = enumeration.nextElement();
		mathDescriptionHash.add(constant.getName());
	}
	//
	//  for any elements in this MathOverrides but not in the new MathDescription, add an "error" issue
	//
	ArrayList<String> overridesToDelete = new ArrayList<String>();
	Enumeration<String> mathOverrideNamesEnum = getOverridesHash().keys();
	while (mathOverrideNamesEnum.hasMoreElements()){
		String name = mathOverrideNamesEnum.nextElement();
		if (!mathDescriptionHash.contains(name)){
			// constant 'name' no longer part of mathDescription
			overridesToDelete.add(name);
		}else if (getSimulation().getSimulationOwner()!=null){
			IssueContext issueContext = new IssueContext(ContextType.Simulation,getSimulation(),null);
			if (getSimulation().getSimulationOwner().gatherIssueForMathOverride(issueContext,getSimulation(),name)!=null){
				// constant 'name' is part of math, but simulation owner doesn't want you to override it.
				overridesToDelete.add(name);
			}
		}
	}
	for (String deletedName : overridesToDelete) {
		removeConstant(deletedName);
	}
}

public boolean hasUnusedOverrides() {
	ArrayList<Issue> issueList = new ArrayList<Issue>();
	IssueContext issueContext = new IssueContext(ContextType.Simulation,getSimulation(),null);
	gatherIssues(issueContext,issueList);
	for (Issue issue : issueList) {
		if (issue.getSeverity()==Issue.SEVERITY_ERROR && (issue.getCategory().equals(IssueCategory.Simulation_Override_NotFound) || issue.getCategory().equals(IssueCategory.Simulation_Override_NotSupported))){
			return true;
		}
	}
	return false;
}


public boolean isUnusedParameter(String name) {
	Variable var = getSimulation().getMathDescription().getVariable(name);
	if (var instanceof Constant){
		if (getSimulation().getSimulationOwner()!=null){
			IssueContext issueContext = new IssueContext(ContextType.Simulation,getSimulation(),null);
			if (getSimulation().getSimulationOwner().gatherIssueForMathOverride(issueContext,getSimulation(),name)!=null){
				return true;
			}
		}
		return false;
	}else{
		return true;
	}
}


public String[] getFilteredConstantNames() {
	//
	// try to ask SimulationContext for Constants which map to unit conversions and physical constants (exclude these).
	//
	SimulationOwner simulationOwner = simulation.getSimulationOwner();
	//
	// MathModels don't provide MathOverrides resolvers, and in context of Simulation editor, cloned Simulation doesn't have an owner (transient field)
	//
	if (simulationOwner != null && simulationOwner.getMathOverridesResolver() != null) {
		Set<String> nonOverridableConstantNames = simulationOwner.getMathOverridesResolver().getNonOverridableConstantNames();
		if (nonOverridableConstantNames!=null) { // returns null if MathSymbolMapping is missing from MathDescription.
			List<String> allConstants = Arrays.asList(getAllConstantNames());
			allConstants.removeAll(nonOverridableConstantNames);
			return allConstants.toArray(new String[0]);
		}
	}

	//
	// Simulation owner cannot provide intelligent choices (MathModel or MathSymbolMapping is missing)
	//
	List<String> reservedConstants = Arrays.asList("KMOLE", "_T_", "_F_", "F_nmol_", "_N_pmol_", "_PI_", "_R_", "_K_GHK", "K_millivolts_per_volt", "param_K_millivolts_per_volt");
	List<String> allConstants = Arrays.asList(getAllConstantNames());
	ArrayList<String> filteredConstants = new ArrayList<String>();
	for (String constant : allConstants) {
		if (reservedConstants.stream().anyMatch(constant::contains)) continue;
		if (constant.startsWith("UnitFactor")) continue;
		if (constant.startsWith("param__")) continue;
		filteredConstants.add(constant);
	}
	return filteredConstants.toArray(new String[filteredConstants.size()]);
}

}
