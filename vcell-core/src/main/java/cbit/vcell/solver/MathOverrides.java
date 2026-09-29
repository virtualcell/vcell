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

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.*;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext.ContextType;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Constant expressions that override those specified in the MathDescription
 * for a given Simulation.  These expressions are to be bound to the Simulation
 * to which they belong.
 * <br/>
 * This class functions by operating a hashmap that maps the name of `Constant`s to
 * `Element`s (wrappers containing expressions, scans, and/or flags).
 * <br/>
 * Creation date: (8/17/2000 3:47:33 PM) by John Wagner
 *
 * @see Constant
 * @see MathOverrides.Element
 */
public class MathOverrides implements Matchable, java.io.Serializable {
	private final static Logger logger = LogManager.getLogger(MathOverrides.class);

	//TODO: Two different types of sorting are performed within this class; find a way to optimize this sorting
	private final Map<String, Element> overridesHash = new HashMap<>();
	private Simulation simulation = null;
	protected transient cbit.vcell.solver.MathOverridesListener mathOverridesListener = null;

	public record ScanIndex(int index) implements Serializable {
		public final static ScanIndex ZERO = new ScanIndex(0);

		public ScanIndex {
			if (index < 0) throw new IllegalArgumentException("index must be non-negative");
		}

		@NotNull
		@Override
		public String toString() {
			return "ScanIndex(" + this.index + ")";
		}
	}

	public static class Element implements java.io.Serializable, Matchable {
		private Expression actualValue;
		private String name;
		private ConstantArraySpec spec;

		private Element(String argName, Expression act) {
			// replace expressions with clones (so that they may be separately bound/resolved/evaluated).
			this.name = argName;
			this.actualValue = new Expression(act);
		}

		private Element(String argName, ConstantArraySpec argSpec) {
			// replace expressions with clones (so that they may be separately bound/resolved/evaluated).
			this.name = argName;
			this.spec = ConstantArraySpec.clone(argSpec);
		}

		public void bind(SymbolTable symbolTable) throws ExpressionBindingException {
			if (this.actualValue != null) {
				this.actualValue.bindExpression(symbolTable);
			}
		}

		@Override
		public boolean compareEqual(Matchable obj) {
			if (this == obj) return true;
			if (!(obj instanceof Element element)) return false;
			return Compare.isEqualOrNull(this.actualValue, element.actualValue) &&
					Compare.isEqual(this.name, element.name) &&
					Compare.isEqualOrNull(this.spec, element.spec);
		}

		public boolean compareEquivalent(Element element) {
			if (!Compare.isEqual(this.name, element.name)) return false;

			// check simple override
			if (this.actualValue != null && element.actualValue != null) {
				if (!ExpressionUtils.functionallyEquivalent(this.actualValue, element.actualValue)) return false;
			} else if (this.actualValue != null || element.actualValue != null) {
				return false; // only one is non-null - mismatch
			}

			// check constant array spec
			if (this.spec != null && element.spec != null) {
				if (!Compare.isEqual(this.spec.getName(), element.spec.getName()) ) return false;
                if (this.spec.getType() != element.spec.getType()) return false;

				return Compare.isEqual(
						Arrays.stream(this.spec.getConstants()).map(Constant::getExpression).collect(Collectors.toList()),
						Arrays.stream(element.spec.getConstants()).map(Constant::getExpression).collect(Collectors.toList()),
						new ExpressionUtils.ExpressionEquivalencePredicate());
			} else return this.spec == null && element.spec == null;
		}

		public Expression getActualValue() {
			return this.actualValue;
		}

		public String getName() {
			return this.name;
		}

		public ConstantArraySpec getSpec() {
			return this.spec;
		}

//		public void applySymbolReplacement(MathOverridesResolver.SymbolReplacement replacement) {
//			this.name = replacement.newName;
//			if (!replacement.factor.isOne()) {
//				if (this.actualValue != null) {
//					this.actualValue = Expression.mult(this.actualValue, replacement.factor);
//				} else if (this.getSpec() != null) {
//					ConstantArraySpec oldSpec = this.getSpec();
//					if (this.getSpec().getType() == ConstantArraySpec.TYPE_INTERVAL) {
//						Expression newMinValueExp = Expression.mult(oldSpec.getMinValue(), replacement.factor);
//						Expression newMaxValueExp = Expression.mult(oldSpec.getMaxValue(), replacement.factor);
//						ConstantArraySpec newSpec = ConstantArraySpec.createIntervalSpec(oldSpec.getName(), newMinValueExp.infix(), newMaxValueExp.infix(), oldSpec.getNumValues(), oldSpec.isLogInterval());
//						this.spec = newSpec;
//					}else if (this.getSpec().getType() == ConstantArraySpec.TYPE_LIST) {
//						ArrayList<String> newExpStrings = new ArrayList<>();
//						for (int i=0; i<oldSpec.getNumValues(); i++){
//							Constant c = oldSpec.getConstants()[i];
//							newExpStrings.add(Expression.mult(c.getExpression(),replacement.factor).infix());
//						}
//						ConstantArraySpec newSpec = null;
//						try {
//							newSpec = ConstantArraySpec.createListSpec(oldSpec.getName(), newExpStrings.toArray(new String[0]));
//						} catch (ExpressionException e) {
//							String msg = "failed to transform list type math override for '"+name+"' with nonconstant factor "+replacement.factor.infix();
//							throw new RuntimeException(msg, e);
//						}
//						this.spec = newSpec;
//					}
//				}
//			}
//		}

		public void applyFunctionToExpressions(Function<Expression, Expression> expressionFunction) {
			if (this.actualValue != null) {
				this.actualValue = expressionFunction.apply(this.actualValue);
			} else if (this.spec != null) {
				this.spec.applyFunctionToExpressions(expressionFunction);
			}
		}

		public void changeName(String newName) {
			this.name = newName;
			if (this.spec != null) {
				this.spec.changeName(newName);
			}
		}
	}

	/**
	 * Used to look up MathOverrides parameter scan indices from simulation Job indices
	 *
	 * @param scanIndex  the index to look for within the scan bounds
	 * @param scanBounds highest zero-based index in each dimension (e.g. 4,5,6 for 3D matrix of size 5x6x7)
	 * @return the parameter scan coordinates mapped to the provided scanIndex + bounds
	 * @see #parameterScanCoordinateToScanIndex(int[], int[])
	 */
	static int[] scanIndexToScanParameterCoordinate(ScanIndex scanIndex, int[] scanBounds) {
		/// !!! IMPORTANT !!!: PLEASE DO NOT CHANGE MAPPING - it is immortalized by stored simulation job datasets
		int index = scanIndex.index;
		int[] parameterScanCoordinates = new int[scanBounds.length];
		for (int i = 0; i < scanBounds.length; i++) {
			int offset = 1;
			for (int j = i + 1; j < scanBounds.length; j++) {
				offset *= scanBounds[j] + 1;
			}
			parameterScanCoordinates[i] = index / offset;
			if (parameterScanCoordinates[i] > scanBounds[i])
				throw new RuntimeException("invalid index, number too large");
			index -= offset * parameterScanCoordinates[i];
		}
		return parameterScanCoordinates;
	}

	/**
	 * Used to look up simulation job index from parameter scans in MathOverrides
	 *
	 * @param parameterScanCoordinate the coordinates to the parameter scan desired.
	 * @param scanBounds              scanBounds is the highest zero-based index in each dimension (e.g. 4,5,6 for 3D matrix of size 5x6x7)
	 * @return the ScanIndex that maps to the provided coordinates
	 */
	public static ScanIndex parameterScanCoordinateToScanIndex(int[] parameterScanCoordinate, int[] scanBounds) {
		/// !!! IMPORTANT !!!: PLEASE DO NOT CHANGE MAPPING - it is immortalized by stored simulation job datasets
		if (parameterScanCoordinate.length != scanBounds.length)
			throw new RuntimeException("coordinates and bounds arrays have different lengths");
		int index = 0;
		for (int i = 0; i < scanBounds.length; i++) {
			if (parameterScanCoordinate[i] < 0 || scanBounds[i] < parameterScanCoordinate[i])
				throw new RuntimeException("invalid coordinate");
			int offset = 1;
			for (int j = i + 1; j < scanBounds.length; j++) {
				offset *= scanBounds[j] + 1;
			}
			index += parameterScanCoordinate[i] * offset;
		}
		return new ScanIndex(index);
	}

	/**
	 * One of four ways to construct a MathOverrides.  This constructor
	 * is used when creating a new MathOverrides.
	 */
	public MathOverrides(Simulation simulation) {
		this.setSimulation(simulation);
	}


	/**
	 * One of four ways to construct a MathOverrides.  This constructor
	 * is used when creating a new MathOverrides from XML (we read in Constants to be overriden from XML).
	 */
	public MathOverrides(Simulation simulation, Constant[] constants, ConstantArraySpec[] specs) throws cbit.vcell.parser.ExpressionException {
		this(simulation);
		for (Constant constant : constants) {
			this.putConstant(constant, false);
		}
		for (ConstantArraySpec spec : specs) {
			this.putConstantArraySpec(spec, false);
		}
	}

	/**
	 * One of four ways to construct a MathOverrides.  This constructor
	 * is used when creating a MathOverrides from the database.
	 */
	public MathOverrides(Simulation simulation, CommentStringTokenizer tokenizer) throws DataAccessException {
		this(simulation);
		this.readVCML(tokenizer);
	}


	/**
	 * One of four ways to construct a MathOverrides.  This constructor
	 * is used when copying a MathOverrides from an existing one.
	 */
	public MathOverrides(Simulation simulation, MathOverrides mathOverrides) {
		this(simulation);
		Set<String> keySet = mathOverrides.overridesHash.keySet();
		for (String constantName : keySet) {
			Element elementFromOverrides = mathOverrides.getOverridesElement(constantName);
            Element newElement = elementFromOverrides.actualValue != null ?
                    new Element(elementFromOverrides.name, elementFromOverrides.actualValue): // regular override
                    new Element(elementFromOverrides.name, elementFromOverrides.spec); // scan override
            this.overridesHash.put(constantName, newElement);
		}
		this.updateFromMathDescription(false);
	}


	public void addMathOverridesListener(MathOverridesListener newListener) {
		this.mathOverridesListener = MathOverridesEventMulticaster.add(this.mathOverridesListener, newListener);
	}

	public boolean compareEqual(Matchable obj) {
        if (this == obj) return true;
		if (!(obj instanceof MathOverrides otherMathOverrides)) return false;
		List<Element> elements = new ArrayList<>(this.overridesHash.values());
		List<Element> otherElements = new ArrayList<>(otherMathOverrides.overridesHash.values());
		// sort elements by name of overridden constant
		elements.sort(Comparator.comparing(el -> el.name));
		otherElements.sort(Comparator.comparing(el -> el.name));
		return Compare.isEqual(elements, otherElements);
	}


	public boolean compareEquivalent(MathOverrides otherMathOverrides) {
		// first see if they are equal
		if (this.compareEqual(otherMathOverrides)) {
			return true;
		}

		List<Element> elements = new ArrayList<>(this.overridesHash.values());
		List<Element> otherElements = new ArrayList<>(otherMathOverrides.overridesHash.values());
		// sort elements by name of overridden constant
		elements.sort(Comparator.comparing(el -> el.name));
		otherElements.sort(Comparator.comparing(el -> el.name));

		if (elements.size() != otherElements.size()) return false;

		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).compareEquivalent(otherElements.get(i))) continue;
			return false;
		}
		return true;
	}

	protected void fireConstantAdded(cbit.vcell.solver.MathOverridesEvent event) {
		if (this.mathOverridesListener == null) return;
		this.mathOverridesListener.constantAdded(event);
	}


	protected void fireConstantChanged(cbit.vcell.solver.MathOverridesEvent event) {
		if (this.mathOverridesListener == null) return;
		this.mathOverridesListener.constantChanged(event);
	}

	protected void fireConstantRemoved(cbit.vcell.solver.MathOverridesEvent event) {
		if (this.mathOverridesListener == null) return;
		this.mathOverridesListener.constantRemoved(event);
	}

	/**
	 * Returns the value to which the specified key is mapped in this hashtable.
	 *
	 * @param key a key in the hashtable.
	 * @return the value to which the key is mapped in this hashtable;
	 * <code>null</code> if the key is not mapped to any value in
	 * this hashtable.
	 */
	public Expression getActualExpression(String key, ScanIndex parameterIndex) {
		MathOverrides.Element element = this.getOverridesElement(key);
		if (element == null) return this.getDefaultExpression(key);  // not overridden
		if (!this.isScan(key)) return element.actualValue; // regular override
		// scanned parameter
		String[] names = this.getScannedConstantNames();
		int[] bounds = this.getScanBounds(names);
		int[] coordinates = scanIndexToScanParameterCoordinate(parameterIndex, bounds);
		int localIndex = coordinates[java.util.Arrays.binarySearch(names, key)];
		return this.getConstantArraySpec(key).getConstants()[localIndex].getExpression();
	}

	// make this.getScannedConstantNames auto-sort.
	public int[] getScanBounds() {
		return this.getScanBounds(this.getScannedConstantNames());
	}

	private int[] getScanBounds(String[] names){
		java.util.Arrays.sort(names); // must do things in a consistent way
		int[] bounds = new int[names.length]; // bounds of scanning matrix
		for (int i = 0; i < names.length; i++) {
			bounds[i] = this.getConstantArraySpec(names[i]).getNumValues() - 1;
		}
		return bounds;
	}

	public String[] getAllConstantNames() {
		Enumeration<Constant> en = this.getSimulation().getMathDescription().getConstants();
		List<String> constantNames = Collections.list(en).stream().map(Constant::getName).collect(Collectors.toCollection(ArrayList::new));

		// add in the overridden parameters that don't exist in the math (these are errors, but have to be displayed).
		for (String overridden : this.overridesHash.keySet()) {
			if (constantNames.contains(overridden)) continue;
			constantNames.add(overridden);
		}

		return constantNames.toArray(String[]::new);
	}

	public Constant getConstant(String constantName) {
		Variable variable = this.getSimulation().getMathDescription().getVariable(constantName);
		if (variable instanceof Constant constant) return constant;
		return null;
	}


	/**
	 * Returns the value to which the specified key is mapped in this hashtable.
	 *
	 * @param key a key in the hashtable.
	 * @return the value to which the key is mapped in this hashtable;
	 * <code>null</code> if the key is not mapped to any value in
	 * this hashtable.
	 */
	public ConstantArraySpec getConstantArraySpec(String key) {
		return this.isScan(key) ? this.getOverridesElement(key).spec : null;
	}

	public Expression getDefaultExpression(String key) {
		Variable var = this.getSimulation().getMathDescription().getVariable(key);
		return var instanceof Constant c ? new Expression(c.getExpression()) : null;
	}


	public String[] getOverridenConstantNames() {
		String[] keys = new String[this.overridesHash.size()];
		this.overridesHash.keySet().toArray(keys);
		return keys;
	}

	private MathOverrides.Element getOverridesElement(String key) {
		return this.overridesHash.get(key);
	}
	
	public java.util.Set<String> getOverrideKeys(){
		return this.overridesHash.keySet();
	}

	public int getScanCount() {
		int num = 1;
		for (String scannedParam : this.getScannedConstantNames()) {
			num *= this.getConstantArraySpec(scannedParam).getNumValues();
		}
		return num;
	}


	public String[] getScannedConstantNames() {
		return Arrays.stream(this.getOverridenConstantNames()).filter(this::isScan).toArray(String[]::new);
	}

	public Simulation getSimulation() {
		return this.simulation;
	}

	public int getSize() {
		return this.overridesHash.size();
	}

	public String getVCML() {
		// write format as follows:
		//
		//   MathOverrides {
		//       Constant K1 200;
		//       Constant K2 400;
		//       Constant K3 494.0;
		//   }
		StringBuilder buffer = new StringBuilder();

		buffer.append(VCML.MathOverrides + " " + VCML.BeginBlock + "\n");

		for (String name : this.overridesHash.keySet()) {
			MathOverrides.Element element = this.overridesHash.get(name);
			Expression exp = element.actualValue; // regular override if not null, else scan override
			buffer.append("   ").append(exp != null ? new Constant(name, exp).getVCML() : element.spec.getVCML());
		}

		buffer.append("\n" + VCML.EndBlock + "\n");

		return buffer.toString();
	}

	/**
	 * MathOverrides has overrides if the overridesHash is not empty.
	 * Returns <true> if overridesHash is not empty (has overrides) and <false> is overridesHash is empty (no overrides)
	 */
	public boolean hasOverrides() {
		return !this.overridesHash.isEmpty();
	}

	public boolean isDefaultExpression(String key) {
		return !this.overridesHash.containsKey(key);
	}


	public boolean isScan(String parameterName) {
		return !this.isDefaultExpression(parameterName) && (this.getOverridesElement(parameterName).spec != null);
	}

	/**
	 * Checks if the current configuration for MathOverrides is valid in terms of the provided math description.
	 * @param mathDescription the math
	 * @return true if MathOverrides has exactly the same list of Constants as MathDescription
	 */
	public boolean isValid(MathDescription mathDescription) {
		// look for Constants from MathDescription not present in Overrides
		Enumeration<Constant> enumeration = mathDescription.getConstants();
		Set<String> mathDescriptionHash = new java.util.HashSet<>();
		while (enumeration.hasMoreElements()) {
			Constant constant = enumeration.nextElement();
			mathDescriptionHash.add(constant.getName());
			if (!this.overridesHash.containsKey(constant.getName())) {
				return false;
			}
		}

		//  look for Constants from Overrides not present in MathDescription
		for (String name : this.overridesHash.keySet()) {
			if (mathDescriptionHash.contains(name)) continue;
			return false;
		}

		return true;
	}


	/**
	 * Maps the specified <code>key</code> to the specified
	 * <code>value</code> . Neither the key nor the
	 * value can be <code>null</code>. <p>
	 */
	public void putConstant(Constant value) throws ExpressionException {
		this.putConstant(value, true);
	}

	private void removeConstant(String name) {
		this.overridesHash.remove(name);
		this.fireConstantRemoved(new MathOverridesEvent(name));
	}

	/**
	 * Maps the specified <code>key</code> to the specified
	 * <code>value</code> . Neither the key nor the
	 * value can be <code>null</code>. <p>
	 */
	private void putConstant(Constant value, boolean bFireEvent) throws ExpressionException {
		// verify that new expression can be bound properly
		this.verifyExpression(value, false);
		// put new element in the hash if value different from default
		// if same as default, remove element if we had it in hash, otherwise do nothing
		String name = value.getName();
		Expression act = value.getExpression();
		Expression def = null;
		Variable var = this.getSimulation().getMathDescription().getVariable(name);
		if (var instanceof Constant) {
			def = var.getExpression();
		} else { // ignore
			logger.warn("Math override '{}' not found, adding to overrides anyway", name);
		}

		if (!act.compareEqual(def)){
			this.overridesHash.put(name, new MathOverrides.Element(name, act));
		} else if (this.overridesHash.containsKey(name)){
			this.removeConstant(name);
		}

		if (bFireEvent) this.fireConstantChanged(new MathOverridesEvent(value.getName()));
	}


	/**
	 * Maps the specified <code>key</code> to the specified
	 * <code>value</code> . Neither the key nor the
	 * value can be <code>null</code>. <p>
	 */
	public void putConstantArraySpec(ConstantArraySpec spec) throws cbit.vcell.parser.ExpressionException {
		this.putConstantArraySpec(spec, true);
	}


	/**
	 * Maps the specified <code>key</code> to the specified
	 * <code>value</code> . Neither the key nor the
	 * value can be <code>null</code>. <p>
	 */
	private void putConstantArraySpec(ConstantArraySpec spec, boolean bFireEvent) throws cbit.vcell.parser.ExpressionException {
		// verify that expressions can be bound properly and make Expression array
		String name = spec.getName();
		Constant[] constants = spec.getConstants();
		for (Constant constant : constants) this.verifyExpression(constant, true); // this will throw if there's a problem with the expression
		// put new element in the hash
		// always different from default, since it's a scan list
		this.overridesHash.put(name, new Element(name, spec));
		if (bFireEvent) this.fireConstantChanged(new MathOverridesEvent(name));
	}

	private void readVCML(CommentStringTokenizer tokens) throws DataAccessException {
		for (Element element : MathOverrides.parseOverrideElementsFromVCML(tokens)) {
			if (null == element.actualValue){
				this.overridesHash.put(element.name, element);
				continue;
			}

			Expression act = element.actualValue;
			Expression def = this.getDefaultExpression(element.name);
			// ignore override if not present in math or if it is the same
			if (def == null || act.compareEqual(def)) continue;
			this.overridesHash.put(element.name, element);
		}
	}

	public static List<Element> parseOverrideElementsFromVCML(CommentStringTokenizer tokens) throws DataAccessException {
		//  Read format as follows:
		//
		//   MathOverrides {
		//       Constant K1 200;
		//       Constant K2 400;
		//       Constant K3 494.0;
		//   }
		List<Element> elements = new ArrayList<>();
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
					StringBuilder description = new StringBuilder();
					boolean bDone = false;
					while (!bDone) {
						String t = tokens.nextToken();
						if (t.equals(";")) {
							bDone = true;
						} else if (t.endsWith(";") && (t.length() > 1)) {
							description.append(t, 0, t.length() - 1);
							bDone = true;
						} else {
							description.append(t).append(" ");
						}
					}
					elements.add(new MathOverrides.Element(name, ConstantArraySpec.createFromString(name, description.toString().trim(), type)));
					continue;
				}
				throw new DataAccessException("unexpected identifier " + token);
			}
			return elements;
		} catch (Exception e) {
			throw new DataAccessException(
					"line #" + (tokens.lineIndex() + 1) + " Exception: " + e.getMessage(), e);
		}
	}


	/**
	 * @param newListener cbit.vcell.solver.MathOverridesListener
	 */
	public void removeMathOverridesListener(MathOverridesListener newListener) {
		this.mathOverridesListener = MathOverridesEventMulticaster.remove(this.mathOverridesListener, newListener);
	}

	void setSimulation(Simulation newSimulation) {
		this.simulation = newSimulation;
	}

	void updateFromMathDescription(boolean bTransformUnits) {
		MathDescription mathDescription = this.getSimulation().getMathDescription();
		// get list of names of constants in this math
		Enumeration<Constant> enumeration = mathDescription.getConstants();
		Set<String> mathDescriptionHash = new LinkedHashSet<>();
		while (enumeration.hasMoreElements()) {
			Constant constant = enumeration.nextElement();
			mathDescriptionHash.add(constant.getName());
		}

		// get list of referenced names (in overridden constant names or those referenced in overridden expressions)
		LinkedHashSet<String> referencedNames = new LinkedHashSet<>();

		for (String name : this.overridesHash.keySet()) {
			referencedNames.add(name);
			Element element = this.overridesHash.get(name);
			if (element.getSpec() != null) {
				for (Constant constant : element.getSpec().getConstants()) {
					String[] symbols = constant.getExpression().getSymbols();
					if (symbols != null) {
						referencedNames.addAll(Arrays.asList(symbols));
					}
				}
			}
			if (element.getActualValue() != null) {
				String[] symbols = element.getActualValue().getSymbols();
				if (symbols != null) {
					referencedNames.addAll(Arrays.asList(symbols));
				}
			}
		}


		LinkedHashSet<String> allFactorSymbols = new LinkedHashSet<>();


		HashMap<String, MathOverridesResolver.SymbolReplacement> renamedMap = new HashMap<>();
		for (String name : referencedNames) {
			SimulationOwner so = this.getSimulation().getSimulationOwner();
			if (null == so) continue;
			MathOverridesResolver mathOverridesResolver = so.getMathOverridesResolver();
			if (null == mathOverridesResolver) continue;
			MathOverridesResolver.SymbolReplacement replacement = mathOverridesResolver.getSymbolReplacement(name, bTransformUnits);
			if (replacement != null) {
				this.replace(allFactorSymbols, renamedMap, name, replacement, false);
				continue;
			}
			// try again with function name for clamped variables previous math naming style
			MathSymbolMapping mathSymbolMapping = (MathSymbolMapping) mathDescription.getSourceSymbolMapping();
			if (mathSymbolMapping != null) {
				Variable var = mathSymbolMapping.findVariableByName(name);
				if (var != null && var.isConstant()) {
					name = var.getName();
					MathOverridesResolver.SymbolReplacement replacement2 = mathOverridesResolver
							.getSymbolReplacement(name + "_init", bTransformUnits);
					if (replacement2 != null) {
						// we need to force this one since it is the one that was actually used in the old maths
						this.replace(allFactorSymbols, renamedMap, name, replacement2, true);
					}
				}
			}
			if (!mathDescriptionHash.contains(name)) {
				logger.error("didn't find a replacement for math override symbol {}", name);
			}
		}

		java.util.function.Function<Expression, Expression> substituteWithFactors = exp -> {
			exp = new Expression(exp);
			for (String replacedSymbol : renamedMap.keySet()) {
				if (exp.hasSymbol(replacedSymbol)) {
					try {
						MathOverridesResolver.SymbolReplacement replacement = renamedMap.get(replacedSymbol);
						Expression replacementExp = new Expression(mathDescription.getVariable(replacement.newName), null);
						if (!replacement.factor.isOne()) {
							replacementExp = Expression.mult(Expression.invert(replacement.factor), replacementExp);
						}
						exp = exp.getSubstitutedExpression(new Expression(replacedSymbol), replacementExp);

					} catch (ExpressionException e) {
						String msg = "failed to process expression substitution for exp '" + exp.infix() + "'";
						throw new RuntimeException(msg, e);
					}
				}
			}
			return exp;
		};

		// for repaired constants, go through all entries and rename expressions where they refer to the renamed constant.
		for (Element element : this.overridesHash.values()) {
			element.applyFunctionToExpressions(substituteWithFactors);
		}

		java.util.function.Function<Expression, Expression> simplifyFunction = (exp) -> {
			try {
				Expression simplifiedExp = exp.simplifyJSCL();
				simplifiedExp.bindExpression(mathDescription);
				exp = simplifiedExp;
			} catch (Exception e) {
				logger.error("unable to simplify expression: '{}': {}", exp.infix(), e.getMessage(), e);
			}
			return exp;
		};


		// loop through all expressions and try to flatten factors.
		for (String overriddenName : this.overridesHash.keySet()) {
			Element element = this.overridesHash.get(overriddenName);
			element.applyFunctionToExpressions(simplifyFunction);
		}

		this.refreshDependencies();
	}


	private void replace(LinkedHashSet<String> allFactorSymbols,
	                     HashMap<String, MathOverridesResolver.SymbolReplacement> renamedMap, String name,
	                     MathOverridesResolver.SymbolReplacement replacement, boolean bForceDuplicate) {
		allFactorSymbols.addAll(replacement.getFactorSymbols());
		Element element = this.overridesHash.remove(name);
		if (element != null) {
			java.util.function.Function<Expression, Expression> scaleExpressionsByUnitFactor = exp -> {
				Expression substitutedExp = Expression.mult(exp, replacement.factor);
				try {
					substitutedExp = substitutedExp.simplifyJSCL();
				} catch (ExpressionException e) {
					logger.warn("failed to simplify '" + substitutedExp.infix() + "': " + e.getMessage(), e);
				}
				return substitutedExp;
			};
			element.applyFunctionToExpressions(scaleExpressionsByUnitFactor);
			element.changeName(replacement.newName);
			if (!this.overridesHash.containsKey(replacement.newName) || bForceDuplicate) {
				this.overridesHash.put(replacement.newName, element);
			}
			if (!name.equals(replacement.newName)) {
				this.removeConstant(name);
			}
		}
		renamedMap.put(name, replacement);
	}


	private void verifyExpression(Constant value, boolean checkScan) throws ExpressionBindingException {
		Expression exp = value.getExpression();
		String[] symbols = exp.getSymbols();
		MathDescription mathDescription = this.getSimulation().getMathDescription();
		exp.bindExpression(mathDescription);
		if (null == symbols) return;
		for (String symbol : symbols) {
			// expression must be a function of another Simulation parameter
			Variable variable = mathDescription.getVariable(symbol);
			if (!(variable instanceof Constant)) throw new ExpressionBindingException(String.format("identifier %s is of type '%s'. Parameter overrides should only refer to constants.", symbol, variable.getClass().getSimpleName()));
			if (checkScan && this.isScan(symbol)) throw new ExpressionBindingException("Parameter overrides cannot depend on another scanned parameter " + symbol);

			// look for trivial algebraic loops (x = f(x)).
			if (symbol.equals(value.getName())) throw new ExpressionBindingException(String.format("Parameter overrides can not be recursive definition, can't use identifier %s in expression for %s", value.getName(), value.getName()));
		}
	}


	public void refreshDependencies() {
		for (Element element : this.overridesHash.values()) {
			try {
				element.bind(this.getSimulation().getMathDescription());
			} catch (ExpressionBindingException e) {
				logger.error("failed to bind a math override: {}", e.getMessage(), e);
				throw new RuntimeException("failed to bind a math override", e);
			}
		}
	}

	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		MathDescription mathDescription = this.getSimulation().getMathDescription();
		// get list of names of constants in this math
		List<Constant> constants = Collections.list(mathDescription.getConstants());
		Set<String> mathDescriptionHash = constants.stream().map(Constant::getName).collect(Collectors.toSet());

		//  for any elements in this MathOverrides but not in the new MathDescription, add an "error" issue
		for (String name : this.overridesHash.keySet()) {
			if (!mathDescriptionHash.contains(name)) {
				Issue issue = new Issue(this.getSimulation(), issueContext, IssueCategory.Simulation_Override_NotFound, VCellErrorMessages.getErrorMessage(VCellErrorMessages.SIMULATION_OVERRIDE_NOTFOUND, name, this.getSimulation().getName()), Issue.Severity.ERROR);
				issueList.add(issue);
			}

			if (null == this.getSimulation().getSimulationOwner()) continue;
			Issue issue = this.getSimulation().getSimulationOwner().gatherIssueForMathOverride(issueContext, this.getSimulation(), name);
			if (null == issue) continue;
			issueList.add(issue);
		}
	}

	/**
	 * explicit user action invokes this method (currently pressing a button), this is not automatic cleanup of old/obsolete models ... we want the user to address the issues.
	 */
	public void removeUnusedOverrides() {
		MathDescription mathDescription = this.getSimulation().getMathDescription();
		// get list of names of constants in this math
		List<Constant> constants = Collections.list(mathDescription.getConstants());
		Set<String> mathDescriptionHash = constants.stream().map(Constant::getName).collect(Collectors.toSet());

		//  for any elements in this MathOverrides but not in the new MathDescription, add an "error" issue
		List<String> overridesToDelete = new ArrayList<>();
		for (String name : this.overridesHash.keySet()) {
			if (!mathDescriptionHash.contains(name)) { // constant 'name' no longer part of mathDescription
				overridesToDelete.add(name);
				continue;
			}
			SimulationOwner owner = this.getSimulation().getSimulationOwner();
			if (null == owner) continue;
			IssueContext issueContext = new IssueContext(ContextType.Simulation, this.getSimulation(), null);
			if (null == owner.gatherIssueForMathOverride(issueContext, this.getSimulation(), name)) continue;
			// constant 'name' is part of math, but simulation owner doesn't want you to override it.
			overridesToDelete.add(name);
		}

		for (String deletedName : overridesToDelete) this.removeConstant(deletedName);
	}

	public boolean hasUnusedOverrides() {
		ArrayList<Issue> issueList = new ArrayList<>();
		IssueContext issueContext = new IssueContext(ContextType.Simulation, this.getSimulation(), null);
		this.gatherIssues(issueContext, issueList);
		for (Issue issue : issueList) {
			if (Issue.Severity.ERROR != issue.getSeverity()) continue;
			if (IssueCategory.Simulation_Override_NotFound.equals(issue.getCategory())) return true;
			if (IssueCategory.Simulation_Override_NotSupported.equals(issue.getCategory())) return true;
		}
		return false;
	}


	public boolean isUnusedParameter(String name) {
		Variable var = this.getSimulation().getMathDescription().getVariable(name);
		if (!(var instanceof Constant)) return true;
		if (null == this.getSimulation().getSimulationOwner()) return false;
		IssueContext issueContext = new IssueContext(ContextType.Simulation, this.getSimulation(), null);
		return this.getSimulation().getSimulationOwner().gatherIssueForMathOverride(issueContext, this.getSimulation(), name) != null;
	}


	/**
	 * Physical constants and unit conversions, which must never be offered as overridable.
	 * <p>
	 * Matched EXACTLY. This was previously matched with String.contains(), which silently
	 * swallowed any user parameter whose NAME merely CONTAINED one of these as a substring:
	 * a reaction parameter called "kon_R_R_in" contains "_R_", the gas constant, and so
	 * vanished from the math overrides table with no error and no way to tell why.
	 * <p>
	 * Two old entries were also misspelled - "F_nmol_" missing its leading underscore and
	 * "_K_GHK" missing its trailing one. Substring matching hid those typos; exact matching
	 * would not, so they are spelled here as Model.createReservedSymbols() actually names them.
	 * <p>
	 * NOTE, possibly a separate bug: SBMLImporter prefixes "param_" onto an imported parameter
	 * whose id collides with a reserved symbol but whose VALUE differs - meaning it is deliberately
	 * NOT the reserved symbol but a real user parameter. Those are still hidden here and by the
	 * startsWith("param__") rule below, so such a parameter cannot be overridden. That behaviour
	 * is pre-existing and is left alone rather than changed as a side effect of this fix.
	 * <p>
	 * Only reached when the MathDescription carries no MathSymbolMapping, which is the case
	 * for a model freshly loaded from the database - i.e. exactly what a user opening a saved
	 * BioModel hits. When the mapping is present, SimulationContext's MathOverridesResolver
	 * answers from the mapping and this list is never consulted.
	 */
	private static final Set<String> RESERVED_CONSTANT_NAMES = Set.of(
			"KMOLE", "_T_", "_F_", "_F_nmol_", "_N_pmol_", "_PI_", "_R_", "_K_GHK_",
			"K_millivolts_per_volt",
			// carried over verbatim from the old list rather than dropped, to keep this change
			// to the substring bug alone. See the note above about "param_" names.
			"param_K_millivolts_per_volt");

	public String[] getFilteredConstantNames() {
		// try to ask SimulationContext for Constants which map to unit conversions and physical constants (exclude these).
		SimulationOwner simulationOwner = this.simulation.getSimulationOwner();
		// MathModels don't provide MathOverrides resolvers, and in context of Simulation editor, cloned Simulation doesn't have an owner (transient field)
		if (simulationOwner != null && simulationOwner.getMathOverridesResolver() != null) {
			Set<String> nonOverridableConstantNames = simulationOwner.getMathOverridesResolver().getNonOverridableConstantNames();
			if (nonOverridableConstantNames != null) { // returns null if MathSymbolMapping is missing from MathDescription.
				Stream<String> ss = Arrays.stream(this.getAllConstantNames().clone()).filter(elem -> !nonOverridableConstantNames.contains(elem));
				return ss.toArray(String[]::new);
			}
		}

		// Simulation owner cannot provide intelligent choices (MathModel or MathSymbolMapping is missing)
		String[] allConstants = this.getAllConstantNames();
		ArrayList<String> filteredConstants = new ArrayList<>();
		for (String constant : allConstants) {
			if (RESERVED_CONSTANT_NAMES.contains(constant)) continue;
			if (constant.startsWith("UnitFactor")) continue;
			if (constant.startsWith("param__")) continue;
			filteredConstants.add(constant);
		}
		return filteredConstants.toArray(String[]::new);
	}

}
