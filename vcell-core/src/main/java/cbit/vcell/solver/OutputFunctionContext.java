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

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.math.Constant;
import cbit.vcell.math.DataGenerator;
import cbit.vcell.math.Function;
import cbit.vcell.math.InconsistentDomainException;
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.PseudoConstant;
import cbit.vcell.math.ReservedMathSymbolEntries;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.AnnotatedFunction.FunctionCategory;

@SuppressWarnings("serial")
public class OutputFunctionContext implements ScopedSymbolTable, Matchable, Serializable, VetoableChangeListener, PropertyChangeListener {
	
	public static final String PROPERTY_OUTPUT_FUNCTIONS = "outputFunctions";

	public class OutputFunctionNameScope extends AbstractNameScope  {
		public OutputFunctionNameScope(){
			super();
		}

		@Override
		public NameScope[] getChildren() {
			return null;
		}

		@Override
		public String getName() {
			// return OutputFunctionContext.this.getName();
			return "OutputFunctionsContext";
		}

		@Override
		public NameScope getParent() {
			return null;
		}

		@Override
		public ScopedSymbolTable getScopedSymbolTable() {
			return OutputFunctionContext.this;
		}
	}

	private ArrayList<AnnotatedFunction> outputFunctionsList = new ArrayList<AnnotatedFunction>();
	private SimulationOwner simulationOwner = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	private OutputFunctionNameScope nameScope = new OutputFunctionNameScope();
	
	public OutputFunctionContext(SimulationOwner argSimOwner) {
		super();
		addVetoableChangeListener(this);
		if (argSimOwner != null) {
			simulationOwner = argSimOwner;
		} else {
			throw new RuntimeException("SimulationOwner cannot be null for outputFunctionContext.");
		}
		simulationOwner.addPropertyChangeListener(this);
	}

	public ArrayList<AnnotatedFunction> getOutputFunctionsList() {
		return outputFunctionsList;
	}

	public boolean compareEqual(Matchable obj){
		if (obj instanceof OutputFunctionContext) {
			if (!Compare.isEqualOrNull(outputFunctionsList, ((OutputFunctionContext)obj).outputFunctionsList)){
				return false;
			}
			return true;
		}
		return false;
	}

	public SimulationOwner getSimulationOwner() {
		return simulationOwner;
	}

	public void refreshDependencies() {
		removeVetoableChangeListener(this);
		addVetoableChangeListener(this);
		simulationOwner.removePropertyChangeListener(this);
		simulationOwner.addPropertyChangeListener(this);
		rebindAll();
	}
	
	public void rebindAll() {
		AnnotatedFunction func = null;
		try {
			for (int i = 0; i < outputFunctionsList.size(); i++) {
				func = outputFunctionsList.get(i);
				func.getExpression().bindExpression(this);
			}
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Error parsing the following output function in '" + getSimulationOwner().getName() + "' \n\n" 
					+ func.getName() + " = " + func.getExpression().infix() + "\n\n" + e.getMessage());
		}
	}

	public void addOutputFunction(AnnotatedFunction obsFunction) throws PropertyVetoException {
		if (obsFunction == null){
			return;
		}	
		try {
			obsFunction.getExpression().bindExpression(this);
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}

		ArrayList<AnnotatedFunction> newFunctionsList = new ArrayList<AnnotatedFunction>(outputFunctionsList);
		newFunctionsList.add(obsFunction);
		setOutputFunctions0(newFunctionsList);
	}   

	public void removeOutputFunction(AnnotatedFunction obsFunction) throws PropertyVetoException {
		if (obsFunction == null){
			return;
		}	
		if (outputFunctionsList.contains(obsFunction)){
			ArrayList<AnnotatedFunction> newFunctionsList = new ArrayList<AnnotatedFunction>(outputFunctionsList);
			newFunctionsList.remove(obsFunction);
			setOutputFunctions0(newFunctionsList);
		}
	}         

	public AnnotatedFunction getOutputFunction(String obsFunctionName){
		for (int i=0;i<outputFunctionsList.size();i++){
			AnnotatedFunction function = outputFunctionsList.get(i); 
			if (function.getName().equals(obsFunctionName)){
				return function;
			}
		}
		return null;
	}
	
	public void propertyChange(java.beans.PropertyChangeEvent event) {
		if (event.getSource() == simulationOwner && event.getPropertyName().equals("mathDescription")) {
			rebindAll();
		}
		if (event.getPropertyName().equals("geometry")) {
			Geometry oldGeometry = (Geometry)event.getOldValue();
			Geometry newGeometry = (Geometry)event.getNewValue();
			// changing from ode to pde
			if (oldGeometry != null && oldGeometry.getDimension() == 0 && newGeometry.getDimension() > 0) {
				ArrayList<AnnotatedFunction> newFuncList = new ArrayList<AnnotatedFunction>();
				for (AnnotatedFunction function : outputFunctionsList) {
					try {
						Expression newexp = new Expression(function.getExpression());
						// making sure that output function is not direct function of constant.
						newexp.bindExpression(this);			
						
						// here use math description as symbol table because we allow 
						// new expression itself to be function of constant.
						MathDescription mathDescription = getSimulationOwner().getMathDescription();
						newexp = MathUtilities.substituteFunctions(newexp, mathDescription).flatten();
						VariableType newFuncType = VariableType.VOLUME;
						
						String[] symbols = newexp.getSymbols();
						if (symbols != null) {
							// figure out the function type
							VariableType[] varTypes = new VariableType[symbols.length];
							for (int i = 0; i < symbols.length; i++) {
								Variable var = mathDescription.getVariable(symbols[i]);
								varTypes[i] = VariableType.getVariableType(var);
							}
							// check with flattened expression to find out the variable type of the new expression
							Function flattenedFunction = new Function(function.getName(), newexp, function.getDomain());
							newFuncType = SimulationSymbolTable.getFunctionVariableType(flattenedFunction, getSimulationOwner().getMathDescription(), symbols, varTypes, true);			
						}
						AnnotatedFunction newFunc = new AnnotatedFunction(function.getName(), function.getExpression(), function.getDomain(), "", newFuncType, FunctionCategory.OUTPUTFUNCTION);
						newFuncList.add(newFunc);
						newFunc.bind(this);
					} catch (ExpressionException ex) {
						ex.printStackTrace();
						throw new RuntimeException(ex.getMessage());
					} catch (InconsistentDomainException ex) {
						ex.printStackTrace();
						throw new RuntimeException(ex.getMessage());
					}
				}
				try {
					setOutputFunctions0(newFuncList);
				} catch (PropertyVetoException e) {					
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
			}
		}
	}
	
	private void setOutputFunctions0(ArrayList<AnnotatedFunction> outputFunctions) throws java.beans.PropertyVetoException {
		ArrayList<AnnotatedFunction> oldValue = outputFunctionsList;
		fireVetoableChange(PROPERTY_OUTPUT_FUNCTIONS, oldValue, outputFunctions);
		outputFunctionsList = outputFunctions;
		firePropertyChange(PROPERTY_OUTPUT_FUNCTIONS, oldValue, outputFunctions);
	}
	
	public void setOutputFunctions(ArrayList<AnnotatedFunction> outputFunctions) throws java.beans.PropertyVetoException {
		setOutputFunctions0(outputFunctions);
		rebindAll();		
	}

	/**
	 * The addPropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	/**
	 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
	 */
	public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
		getVetoPropertyChange().addVetoableChangeListener(listener);
	}

	/**
	 * The firePropertyChange method was generated to support the propertyChange field.
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
	 */
	public void fireVetoableChange(String propertyName, Object oldValue, Object newValue)
			throws PropertyVetoException {
				getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
			}

	/**
	 * Accessor for the propertyChange field.
	 */
	protected java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}

	/**
	 * Accessor for the vetoPropertyChange field.
	 */
	protected VetoableChangeSupport getVetoPropertyChange() {
		if (vetoPropertyChange == null) {
			vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
		};
		return vetoPropertyChange;
	}

	/**
	 * The hasListeners method was generated to support the vetoPropertyChange field.
	 */
	public synchronized boolean hasListeners(String propertyName) {
		return getVetoPropertyChange().hasListeners(propertyName);
	}

	/**
	 * The removePropertyChangeListener method was generated to support the propertyChange field.
	 */
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	/**
	 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
	 */
	public synchronized void removeVetoableChangeListener(VetoableChangeListener listener) {
		getVetoPropertyChange().removeVetoableChangeListener(listener);
	}

	public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
		if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_OUTPUT_FUNCTIONS)){
			ArrayList<AnnotatedFunction> newOutputFnsList = (ArrayList<AnnotatedFunction>)evt.getNewValue();
			if(newOutputFnsList == null){
				throw new IllegalArgumentException("outputFunctions cannot be null");
			}
			//
			// while adding a function: check that names are not duplicated and that no common names are mathSymbols
			//
			HashSet<String> namesSet = new HashSet<String>();
			for (int i=0;i<newOutputFnsList.size();i++){
				String fnName = newOutputFnsList.get(i).getName();
				if (namesSet.contains(fnName)){
					throw new PropertyVetoException(" Cannot define multiple output functions with same name '"+fnName+"'.",evt);
				} 
				namesSet.add(fnName);
				// now see if the symbol is a math symbol - cannot use that name for new output function.
				SymbolTableEntry ste  = getEntry(fnName);
				if (ste != null) {
					if (!(ste instanceof AnnotatedFunction)) {
						throw new PropertyVetoException(" '"+fnName+"' conflicts with existing symbol. Cannot define function with name '" + fnName +".",evt);
					}
				}
			}

			// while deleting an output function: check if it present in expression of other output functions.
			ArrayList<AnnotatedFunction> oldOutputFnsList = (ArrayList<AnnotatedFunction>)evt.getOldValue();
			if (oldOutputFnsList.size() > newOutputFnsList.size()) {
				// if 'newOutputFnList' is smaller than 'oldOutputFnList', one of the functions has been removed, find the missing one
				AnnotatedFunction missingFn = null;
				for (int i = 0; i < oldOutputFnsList.size(); i++) {
					if (!newOutputFnsList.contains(oldOutputFnsList.get(i))) {
						missingFn = oldOutputFnsList.get(i);
					}
				}
				// use this missing output fn (to be deleted) to determine if it is used in any other output fn expressions. 
				if (missingFn != null) {
					// find out if the missing fn is used in other functions in outputFnsList
					Vector<String> referencingOutputFnsVector = new Vector<String>();
					for (int i = 0; i < newOutputFnsList.size(); i++) {
						AnnotatedFunction function = newOutputFnsList.get(i);
						if (function.getExpression().hasSymbol(missingFn.getName())) {
							referencingOutputFnsVector.add(function.getName());
						}
					}
					// if there are any output fns referencing the given 'missingFn', list them all in error msg.
					if (referencingOutputFnsVector.size() > 0) {
						String msg = "Output Function '" + missingFn.getName() + "' is used in expression of other output function(s): ";
						for (int i = 0; i < referencingOutputFnsVector.size(); i++) {
							msg = msg + "'" + referencingOutputFnsVector.elementAt(i) + "'";
							if (i < referencingOutputFnsVector.size()-1) {
								msg = msg + ", "; 
							} else {
								msg = msg + ". ";
							}
						}
						msg = msg + "\n\nCannot delete '" + missingFn.getName() + "'.";
						throw new PropertyVetoException(msg,evt);
					}
				}
			}
		}
	}
	
//	public abstract void validateNamingConflicts(String symbolDescription, Class<?> newSymbolClass, String newSymbolName, PropertyChangeEvent e)  throws PropertyVetoException ;

	public void getEntries(Map<String, SymbolTableEntry> entryMap) {	
		// add all valid entries (variables) from mathdescription
		MathDescription mathDescription = simulationOwner.getMathDescription();
		if (mathDescription != null) {
			Enumeration<Variable> varEnum = mathDescription.getVariables();
			while(varEnum.hasMoreElements()) {
				Variable var = varEnum.nextElement();
				if (!(var instanceof PseudoConstant) && !(var instanceof Constant)) {
					entryMap.put(var.getName(), var);
				} 
			}
			for (DataGenerator dataGenerator : mathDescription.getPostProcessingBlock().getDataGeneratorList()){
				entryMap.put(dataGenerator.getName(), dataGenerator);
			}
		}
		entryMap.put(ReservedVariable.TIME.getName(), ReservedVariable.TIME);
		int dimension = mathDescription.getGeometry().getDimension();
		if (dimension > 0) {
			entryMap.put(ReservedVariable.X.getName(), ReservedVariable.X);
			if (dimension > 1) {
				entryMap.put(ReservedVariable.Y.getName(), ReservedVariable.Y);
				if (dimension > 2) {
					entryMap.put(ReservedVariable.Z.getName(), ReservedVariable.Z);
				}
			}
		}
		// then add list of output functions.
		for (SymbolTableEntry ste : outputFunctionsList) {
			entryMap.put(ste.getName(), ste);
		}
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

	public SymbolTableEntry getEntry(java.lang.String identifierString) {
		//
		// use MathDescription as the primary SymbolTable, just replace the Constants with the overrides.
		//
		SymbolTableEntry ste = null;
		MathDescription mathDescription = simulationOwner.getMathDescription();
		if (mathDescription != null) {
			ste = mathDescription.getEntry(identifierString);
			if (ste != null && !(ste instanceof PseudoConstant) && !(ste instanceof Constant)) {
				return ste;
			}
			ste = mathDescription.getPostProcessingBlock().getDataGenerator(identifierString);
			if (ste instanceof DataGenerator){
				return ste;
			}
		}
		// see if it is an output function.
		ste = getOutputFunction(identifierString);
		return ste;
	}

	// check if the new expression is valid for outputFunction of functionType 
	public VariableType computeFunctionTypeWRTExpression(AnnotatedFunction outputFunction, Expression exp) throws ExpressionException, InconsistentDomainException {
		MathDescription mathDescription = getSimulationOwner().getMathDescription();
		boolean bSpatial = getSimulationOwner().getGeometry().getDimension() > 0;
		if (!bSpatial) {
			return VariableType.NONSPATIAL;
		}
		Expression newexp = new Expression(exp);
		// making sure that output function is not direct function of constant.
		newexp.bindExpression(this);
		
		// here use math description as symbol table because we allow 
		// new expression itself to be function of constant.
		try {
			newexp = MathUtilities.substituteFunctions(newexp, this).flatten();
		} catch (ExpressionBindingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			newexp = MathUtilities.substituteFunctions(newexp, this,true).flatten();
		}
		String[] symbols = newexp.getSymbols();
		VariableType functionType = outputFunction.getFunctionType();
		String funcName = outputFunction.getName();
		Domain funcDomain = outputFunction.getDomain();
		VariableType[] varTypes = null;
		if (symbols != null && symbols.length > 0) {
			// making sure that new expression is defined in the same domain
			varTypes = new VariableType[symbols.length];
			for (int i = 0; i < symbols.length; i++) {
				if (ReservedMathSymbolEntries.getReservedVariableEntry(symbols[i]) != null) {
					varTypes[i] = functionType;
				} else {
					Variable var = mathDescription.getVariable(symbols[i]);
					if (var == null){
						var = mathDescription.getPostProcessingBlock().getDataGenerator(symbols[i]);
					}
					varTypes[i] = VariableType.getVariableType(var);
					if (funcDomain != null) {
						if (var.getDomain() == null) {
							continue; // OK
						}
						GeometryClass funcGeoClass = simulationOwner.getGeometry().getGeometryClass(funcDomain.getName());
						GeometryClass varGeoClass = simulationOwner.getGeometry().getGeometryClass(var.getDomain().getName());
				
						if (varGeoClass instanceof SubVolume && funcGeoClass instanceof SurfaceClass) {
							// seems ok if membrane refereces volume
							if (!((SurfaceClass)funcGeoClass).isAdjacentTo((SubVolume)varGeoClass)) {
								// but has to be adjacent
								String errMsg = "'" + funcName + "' defined on Membrane '" + funcDomain.getName() + "' directly or indirectly references "
									+  " variable '" + symbols[i] + "' defined on Volume '" + var.getDomain().getName() + " which is not adjacent to Membrane '" + funcDomain.getName() + "'."; 
								throw new ExpressionException(errMsg);
							}
						} else if (!var.getDomain().compareEqual(funcDomain)) {
							String errMsg = "'" + funcName + "' defined on '" + funcDomain.getName() + "' directly or indirectly references "
								+  " variable '" + symbols[i] + "' defined on '" + var.getDomain().getName() + "."; 
							throw new ExpressionException(errMsg);
						}
					}
				}
			}
		}
		// if there are no variables (like built in function, vcRegionArea), check with flattened expression to find out the variable type of the new expression
		VariableDomain functionVariableDomain = functionType.getVariableDomain();
		Function flattenedFunction = new Function(funcName, newexp, funcDomain);
		flattenedFunction.bind(this);
		VariableType newVarType = SimulationSymbolTable.getFunctionVariableType(flattenedFunction, getSimulationOwner().getMathDescription(), symbols, varTypes, bSpatial);
		if (!newVarType.getVariableDomain().equals(functionVariableDomain)) {
			String errMsg = "The expression for '" + funcName + "' includes at least one " 
				+ newVarType.getVariableDomain().getName() + " variable. Please make sure that only " + functionVariableDomain.getName() + " variables are " +
						"referenced in " + functionVariableDomain.getName() + " output functions.";
			throw new ExpressionException(errMsg);
		}
		return newVarType;
	}
	
	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter() {
		AutoCompleteSymbolFilter stef = new AutoCompleteSymbolFilter() {		
			public boolean accept(SymbolTableEntry ste) {
				MathDescription math = getSimulationOwner().getMathDescription();
				Variable var = math.getVariable(ste.getName());
				return (!(var instanceof InsideVariable || var instanceof OutsideVariable));
			}
			public boolean acceptFunction(String funcName) {
				return true;
			}
		};
		return stef;
	}

	public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter(final Domain functionDomain) {
		AutoCompleteSymbolFilter stef = new AutoCompleteSymbolFilter() {		
			public boolean accept(SymbolTableEntry ste) {
				if (simulationOwner.getGeometry().getDimension() > 0) {
					if (functionDomain == null) {
						return true;
					}
					if (ste.getName().endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX) || ste.getName().endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)) {
						return false;
					}
					if (ste instanceof ReservedVariable) {
						return true;
					}
					if (ste instanceof AnnotatedFunction) {								
						return functionDomain.compareEqual(((AnnotatedFunction)ste).getDomain());
					}
					if (ste instanceof Variable) {
						Variable var = (Variable)ste;
						if (var.getDomain() == null) {
							return true;
						}
						GeometryClass gc = simulationOwner.getGeometry().getGeometryClass(functionDomain.getName());
						GeometryClass vargc = simulationOwner.getGeometry().getGeometryClass(var.getDomain().getName());						
						if (gc instanceof SurfaceClass && vargc instanceof SubVolume) {
							if (((SurfaceClass)gc).isAdjacentTo((SubVolume)vargc)) {
								return true;
							} else {
								return false;
							}
						} else {
							return var.getDomain().compareEqual(functionDomain);
						}
					}
					
				}				
				return true;
			}
			public boolean acceptFunction(String funcName) {
				return true;
			}
		};
		return stef;
	}

	public static class OutputFunctionIssueSource implements IssueSource {
		private OutputFunctionContext outputFunctionContext;
		private AnnotatedFunction function;
		private OutputFunctionIssueSource(
				OutputFunctionContext outputFunctionContext,
				AnnotatedFunction function) {
			super();
			this.outputFunctionContext = outputFunctionContext;
			this.function = function;
		}
		public final OutputFunctionContext getOutputFunctionContext() {
			return outputFunctionContext;
		}
		public final AnnotatedFunction getAnnotatedFunction() {
			return function;
		}		
	}
	
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		for (AnnotatedFunction af : outputFunctionsList) {
			try {
				af.bind(this);
			} catch (ExpressionException ex) {
				issueList.add(new Issue(new OutputFunctionIssueSource(this, af), issueContext, IssueCategory.OUTPUTFUNCTIONCONTEXT_FUNCTION_EXPBINDING, ex.getMessage(), Issue.SEVERITY_ERROR));
			}
			if(getSimulationOwner().getGeometry() != null && getSimulationOwner().getGeometry().getDimension() > 0) {
				GeometryClass[] geomClasses = getSimulationOwner().getGeometry().getGeometryClasses();
				boolean bFound = false;
				for (int i = 0; i < geomClasses.length; i++) {
					if(af.getDomain() != null && af.getDomain().getName().equals(geomClasses[i].getName())) {
						bFound = true;
						break;
					}							
				}
				if(!bFound) {
					issueList.add(new Issue(new OutputFunctionIssueSource(this, af), issueContext, IssueCategory.InternalError, "OutputFunction '"+af.getName()+"' domain='"+(af.getDomain() == null?"NULL":af.getDomain().getName())+"' not found in geometry", Issue.Severity.WARNING));
				}
			}
		}
		
	}

}
