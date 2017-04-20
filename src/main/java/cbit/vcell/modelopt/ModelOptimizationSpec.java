/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;

import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.util.graph.Tree;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.Equation;
import cbit.vcell.math.FilamentRegionEquation;
import cbit.vcell.math.FilamentRegionVariable;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Structure;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
/**
 * Insert the type's description here.
 * Creation date: (8/22/2005 9:21:42 AM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ModelOptimizationSpec implements java.io.Serializable, Matchable, PropertyChangeListener, IssueSource {
	public static final String PROPERTY_NAME_REFERENCE_DATA = "referenceData";
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private ParameterMappingSpec[] fieldParameterMappingSpecs = null;
	private ReferenceData fieldReferenceData = null;
	private ReferenceDataMappingSpec[] fieldReferenceDataMappingSpecs = null;
	
	private boolean bComputeProfileDistributions = false;
	private ParameterEstimationTask parameterEstimationTask;
	
	protected List<Issue> localIssueList = new Vector<Issue>();
	protected final IssueContext localIssueContext;

/**
 * ModelOptimizationSpec constructor comment.
 */
public ModelOptimizationSpec(ParameterEstimationTask pet) throws ExpressionException {
	super();
	parameterEstimationTask = pet;
	localIssueContext = new IssueContext(ContextType.SimContext,pet.getSimulationContext(),null).newChildContext(ContextType.ParameterEstimationTask, pet);
	// ModelOptSpec should listen to changes in model (addition/deletion of model params, species, reaction params, etc.)
	// ModelOptSpec should listen to changes in simContext MathDesc
	if (getSimulationContext() != null) {
		updateListenersList(getSimulationContext().getModel(), true);
	}
	refreshParameterMappingSpecs();
}

public void gatherIssues(IssueContext issueContext, java.util.List<Issue> issueList) {
	if (!hasSelectedParameters()) {
		issueList.add(new Issue(this,issueContext,IssueCategory.ParameterEstimationNoParameterSelected,"No parameters are selected for optimization.",Issue.SEVERITY_INFO));
	}
	
	boolean bExperimentalDataMapped = true;
	if (fieldReferenceDataMappingSpecs != null) {
		for (ReferenceDataMappingSpec rms : fieldReferenceDataMappingSpecs) {
			if (rms.getModelObject() == null) {
				bExperimentalDataMapped = false;
				break;
			}
		}
	}
	if (!bExperimentalDataMapped) {
		issueList.add(new Issue(this,issueContext,IssueCategory.ParameterEstimationRefereceDataNotMapped,"There is unmapped experimental data column.",Issue.SEVERITY_WARNING));
	}
	
	issueList.addAll(localIssueList);
}

public boolean hasSelectedParameters()
{
	if (fieldParameterMappingSpecs != null) {
		for (ParameterMappingSpec pms : fieldParameterMappingSpecs) {
			if (pms.isSelected()) {
				return true;
			}
		}
	}
	return false;
}

/**
 * ModelOptimizationSpec constructor comment.
 */
public ModelOptimizationSpec(ParameterEstimationTask task, ModelOptimizationSpec modelOptimizationSpecToCopy) throws ExpressionException {
	super();
	parameterEstimationTask = task;
	localIssueContext = new IssueContext(ContextType.SimContext,task.getSimulationContext(),null).newChildContext(ContextType.ParameterEstimationTask, task);
	Parameter[] modelParameters = getModelParameters();
	fieldParameterMappingSpecs = new ParameterMappingSpec[modelParameters.length];
	ParameterMappingSpec[] parameterMappingSpecsCopy = modelOptimizationSpecToCopy.getParameterMappingSpecs();
	for (int i = 0; i < fieldParameterMappingSpecs.length; i++){
		fieldParameterMappingSpecs[i] = new ParameterMappingSpec(modelParameters[i]);
		for (ParameterMappingSpec parameterMappingSpecCopy : parameterMappingSpecsCopy) {
			Parameter modelParameterCopy = parameterMappingSpecCopy.getModelParameter();
			if (modelParameterCopy == null) {
				break;
			}
			if (modelParameterCopy.getName().equals(modelParameters[i].getName()) && 
				modelParameterCopy.getNameScope() != null && modelParameters[i].getNameScope() != null && modelParameterCopy.getNameScope().getName().equals(modelParameters[i].getNameScope().getName())) {
				fieldParameterMappingSpecs[i].setCurrent(parameterMappingSpecCopy.getCurrent());
				fieldParameterMappingSpecs[i].setLow(parameterMappingSpecCopy.getLow());
				fieldParameterMappingSpecs[i].setHigh(parameterMappingSpecCopy.getHigh());
				fieldParameterMappingSpecs[i].setSelected(parameterMappingSpecCopy.isSelected());
				break;
			} 
		}
	}
	if (modelOptimizationSpecToCopy.fieldReferenceData!=null){
		fieldReferenceData = new SimpleReferenceData(modelOptimizationSpecToCopy.fieldReferenceData);
	}
	if (modelOptimizationSpecToCopy.fieldReferenceDataMappingSpecs!=null){
		fieldReferenceDataMappingSpecs = new ReferenceDataMappingSpec[modelOptimizationSpecToCopy.fieldReferenceDataMappingSpecs.length];
		for (int i = 0; i < fieldReferenceDataMappingSpecs.length; i++){
			fieldReferenceDataMappingSpecs[i] = new ReferenceDataMappingSpec(modelOptimizationSpecToCopy.fieldReferenceDataMappingSpecs[i]);
		}
	}
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
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}

/**
 * Insert the method's description here.
 * Creation date: (11/29/2005 5:10:51 PM)
 * @return cbit.vcell.parser.SymbolTableEntry[]
 */
public static SymbolTableEntry[] calculateTimeDependentModelObjects(SimulationContext simulationContext) {

	Graph digraph = new Graph();

	//
	// add time
	//
	Model model = simulationContext.getModel();
	Node timeNode = new Node("t", model.getTIME());
	digraph.addNode(timeNode);
	
	//
	// add all species concentrations (that are not fixed with a constant initial condition).
	//
	SpeciesContextSpec scs[] = simulationContext.getReactionContext().getSpeciesContextSpecs();
	for (int i = 0;scs!=null && i < scs.length; i++){
		SpeciesContextSpecParameter initParam = scs[i].getInitialConditionParameter();
		Expression iniExp = initParam == null? null : initParam.getExpression();
		if (!scs[i].isConstant() || (iniExp != null && !iniExp.isNumeric())){
			String speciesContextScopedName = scs[i].getSpeciesContext().getNameScope().getAbsoluteScopePrefix()+scs[i].getSpeciesContext().getName();
			Node speciesContextNode = new Node(speciesContextScopedName,scs[i].getSpeciesContext());
			digraph.addNode(speciesContextNode);
			digraph.addEdge(new Edge(speciesContextNode,timeNode));
		}
	}

	//
	// add all model (global) parameters that are not simple constants
	//
	ModelParameter[] modelParams = model.getModelParameters();
	for (int i = 0; modelParams!=null && i < modelParams.length; i++){
		Expression exp = modelParams[i].getExpression();
		if (exp!=null){
			String symbols[] = exp.getSymbols();
			if (symbols!=null && symbols.length>0){
				//
				// add parameter to graph as a node (if not already there).
				//
				String parameterScopedName = modelParams[i].getNameScope().getAbsoluteScopePrefix()+modelParams[i].getName();
				Node parameterNode = digraph.getNode(parameterScopedName);
				if (parameterNode==null){
					parameterNode = new Node(parameterScopedName,modelParams[i]);
					digraph.addNode(parameterNode);
				}
				//
				// add all dependencies to graph also (if not already there).
				//
				for (int k = 0; symbols!=null && k < symbols.length; k++){
					SymbolTableEntry ste = exp.getSymbolBinding(symbols[k]);
					if (ste==null){
						throw new RuntimeException("Error, symbol '"+symbols[k]+"' not bound in parameter '"+modelParams[i].getName()+"'");
					}
					String symbolScopedName = ste.getNameScope().getAbsoluteScopePrefix()+ste.getName();
					Node symbolNode = digraph.getNode(symbolScopedName);
					if (symbolNode==null){
						symbolNode = new Node(symbolScopedName,ste);
						digraph.addNode(symbolNode);
					}
					digraph.addEdge(new Edge(parameterNode,symbolNode));
				}
			}
		}
	}
	
	//
	// add all reaction parameters that are not simple constants
	//
	ReactionStep[] reactionSteps = model.getReactionSteps();
	for (int i = 0; reactionSteps!=null && i < reactionSteps.length; i++){
		Parameter[] parameters = reactionSteps[i].getKinetics().getKineticsParameters();
		for (int j = 0; parameters!=null && j < parameters.length; j++){
			Expression exp = parameters[j].getExpression();
			if (exp!=null){
				String symbols[] = exp.getSymbols();
				if (symbols!=null && symbols.length>0){
					//
					// add parameter to graph as a node (if not already there).
					//
					String parameterScopedName = parameters[j].getNameScope().getAbsoluteScopePrefix()+parameters[j].getName();
					Node parameterNode = digraph.getNode(parameterScopedName);
					if (parameterNode==null){
						parameterNode = new Node(parameterScopedName,parameters[j]);
						digraph.addNode(parameterNode);
					}
					//
					// add all dependencies to graph also (if not already there).
					//
					for (int k = 0; symbols!=null && k < symbols.length; k++){
						SymbolTableEntry ste = exp.getSymbolBinding(symbols[k]);
						if (ste==null){
							throw new RuntimeException("Error, symbol '"+symbols[k]+"' not bound in parameter '"+parameters[j].getName()+"'");
						}
						String symbolScopedName = ste.getNameScope().getAbsoluteScopePrefix()+ste.getName();
						Node symbolNode = digraph.getNode(symbolScopedName);
						if (symbolNode==null){
							symbolNode = new Node(symbolScopedName,ste);
							digraph.addNode(symbolNode);
						}
						digraph.addEdge(new Edge(parameterNode,symbolNode));
					}
				}
			}
		}
	}

    //
    //  add dependences for calculated voltages
    //
    for (Structure structure : model.getStructures()){
           if (structure instanceof Membrane && ((MembraneMapping)simulationContext.getGeometryContext().getStructureMapping(structure)).getCalculateVoltage()){
                  MembraneVoltage membraneVoltage = ((Membrane)structure).getMembraneVoltage();
                  String membraneVoltageScopedName = membraneVoltage.getNameScope().getAbsoluteScopePrefix()+membraneVoltage.getName();
                  Node membraneVoltageNode = digraph.getNode(membraneVoltageScopedName);
                  if (membraneVoltageNode==null){
                        membraneVoltageNode = new Node(membraneVoltageScopedName,membraneVoltage);
                        digraph.addNode(membraneVoltageNode);
                  }
                  digraph.addEdge(new Edge(membraneVoltageNode,timeNode));
           }
    }

	Node[] timeDependentNodes = digraph.getDigraphAttractorSet(timeNode);
	
	SymbolTableEntry steArray[] = new SymbolTableEntry[timeDependentNodes.length];
	for (int i = 0; i < steArray.length; i++){
		steArray[i] = (SymbolTableEntry)timeDependentNodes[i].getData();
	}

	return steArray;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof ModelOptimizationSpec){
		ModelOptimizationSpec mos = (ModelOptimizationSpec)obj;

		if (!Compare.isEqual(fieldParameterMappingSpecs,mos.fieldParameterMappingSpecs)){
			return false;
		}

		if (!Compare.isEqualOrNull(fieldReferenceData,mos.fieldReferenceData)){
			return false;
		}

		if (!Compare.isEqualOrNull(fieldReferenceDataMappingSpecs,mos.fieldReferenceDataMappingSpecs)){
			return false;
		}


		return true;
	}
	return false;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 10:38:04 AM)
 * @return cbit.vcell.model.Parameter[]
 */
private Parameter[] getModelParameters() {
	java.util.Vector<Parameter> modelParameterList = new java.util.Vector<Parameter>();
	Model model = getSimulationContext().getModel();
	//
	// get Model (global) parameters
	//
	ModelParameter[] globalParams = model.getModelParameters();
	for (int i = 0; i < globalParams.length; i++) {
		if (globalParams[i] != null && globalParams[i].getExpression()!= null && globalParams[i].getExpression().isNumeric()){
			modelParameterList.add(globalParams[i]);
		}
	}
	
	//
	// get kinetic parameters that are numbers
	//
	ReactionStep[] reactionSteps = model.getReactionSteps();
	for (int i = 0; i < reactionSteps.length; i++){
		//
		// make sure ReactionSteps are "enabled"
		//
		ReactionSpec reactionSpec = getSimulationContext().getReactionContext().getReactionSpec(reactionSteps[i]);
		if (reactionSpec == null || reactionSpec.isExcluded()){
			continue;
		}
		Kinetics.KineticsParameter[] kineticsParameters = reactionSteps[i].getKinetics().getKineticsParameters();
		for (int j = 0; j < kineticsParameters.length; j++){
			if (kineticsParameters[j].getExpression()!=null && kineticsParameters[j].getExpression().isNumeric()){
				if (((kineticsParameters[j].getRole() == Kinetics.ROLE_CurrentDensity)||(kineticsParameters[j].getRole() == Kinetics.ROLE_LumpedCurrent)) && 
					reactionSteps[i].getPhysicsOptions() == ReactionStep.PHYSICS_MOLECULAR_ONLY){
					continue;
				}
				if (((kineticsParameters[j].getRole() == Kinetics.ROLE_ReactionRate)||(kineticsParameters[j].getRole() == Kinetics.ROLE_LumpedReactionRate)) && 
					reactionSteps[i].getPhysicsOptions() == ReactionStep.PHYSICS_ELECTRICAL_ONLY){
					continue;
				}
				modelParameterList.add(kineticsParameters[j]);
			}
		}
	}
	//
	// get initial conditions that are numbers
	//
	SpeciesContextSpec[] speciesContextSpecs = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpec.SpeciesContextSpecParameter initParam = speciesContextSpecs[i].getInitialConditionParameter();
		if (initParam != null && initParam.getExpression() != null && initParam.getExpression().isNumeric()){
			modelParameterList.add(initParam);
		}
	}

	//
	// get structure parameters
	//
	StructureMapping[] structureMappings = getSimulationContext().getGeometryContext().getStructureMappings();
	for (int i = 0; i < structureMappings.length; i++){
		StructureMapping.StructureMappingParameter[] parameters = structureMappings[i].getParameters();
		for (int j = 0; j < parameters.length; j++){
			if (parameters[j].getRole() == StructureMapping.ROLE_SpecificCapacitance &&
				structureMappings[i] instanceof MembraneMapping &&
				!((MembraneMapping)structureMappings[i]).getCalculateVoltage()){
				continue;
			}
			if (parameters[j].getExpression() != null && parameters[j].getExpression().isNumeric()) {
				modelParameterList.add(parameters[j]);
			}
		}
	}
	
	Parameter[] modelParameters = (Parameter[])BeanUtils.getArray(modelParameterList,Parameter.class);
	return modelParameters;
}


public void removeUncoupledParameters() {
	try {
		localIssueList.clear();
		MathMapping mathMapping = getSimulationContext().createNewMathMapping();
		MathDescription mathDesc = mathMapping.getMathDescription();
		MathSystemHash mathSystemHash = fromMath(mathDesc);
		Graph graph = mathSystemHash.getDependencyGraph(mathSystemHash.getSymbols());
		Tree[] spanningTrees = graph.getSpanningForest();
		//
		// remove trees without any State Variables
		//
		for (int i = 0; i < spanningTrees.length; i++){
			Node[] treeNodes = spanningTrees[i].getNodes();
			boolean bHasStateVariables = false;
			for (int j = 0; j < treeNodes.length; j++){
				Node node = treeNodes[j];
				Variable var = mathDesc.getVariable(node.getName());
				if (var instanceof VolVariable || var instanceof MemVariable || var instanceof FilamentVariable ||
					var instanceof VolumeRegionVariable || var instanceof MembraneRegionVariable || var instanceof FilamentRegionVariable){
					bHasStateVariables = true;
					break;
				}
			}
			if (!bHasStateVariables){
				spanningTrees = (Tree[])BeanUtils.removeElement(spanningTrees,spanningTrees[i]);
				i--;
			}
		}

		//
		// remove parameters not mapped to a surviving tree (not coupled to any state variables
		//
		ArrayList<ParameterMappingSpec> paramMappingSpecsList = new ArrayList<ParameterMappingSpec>(); 
		paramMappingSpecsList.addAll(Arrays.asList(fieldParameterMappingSpecs));
		for (int i = 0; i < paramMappingSpecsList.size(); i++){			
			Parameter parameter = paramMappingSpecsList.get(i).getModelParameter();
			String mathName = mathMapping.getMathSymbolMapping().getVariable(parameter).getName();
			boolean bFoundInTree = false;
			for (int j = 0; j < spanningTrees.length; j++){
				Node node = spanningTrees[j].getNode(mathName);
				if (node != null){
					bFoundInTree = true;
				}
			}
			if (!bFoundInTree){
				paramMappingSpecsList.remove(i);
				i--;
			}
		}
		ParameterMappingSpec[] parameterMappingSpecs = new ParameterMappingSpec[paramMappingSpecsList.size()];
		paramMappingSpecsList.toArray(parameterMappingSpecs);
		setParameterMappingSpecs(parameterMappingSpecs);
	} catch (Exception e){
		e.printStackTrace(System.out);
		localIssueList.add(new Issue(this, localIssueContext, IssueCategory.ParameterEstimationGeneralWarning, e.getMessage(),Issue.SEVERITY_WARNING));
		// throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 5:18:19 PM)
 * @return cbit.vcell.modelopt.ParameterMappingSpec
 * @param parameter cbit.vcell.model.Parameter
 */
public ParameterMappingSpec getParameterMappingSpec(Parameter parameter) {
	for (int i = 0;fieldParameterMappingSpecs!=null && i < fieldParameterMappingSpecs.length; i++){
		if (fieldParameterMappingSpecs[i].getModelParameter().equals(parameter)){
			return fieldParameterMappingSpecs[i];
		}
	}
	return null;
}

private ParameterMappingSpec getParameterMappingSpecByCompareEqual(Parameter parameter) {
	for (int i = 0;fieldParameterMappingSpecs!=null && i < fieldParameterMappingSpecs.length; i++){
		if (fieldParameterMappingSpecs[i].getModelParameter().compareEqual(parameter)) 
		{
			return fieldParameterMappingSpecs[i];
		}
	}
	return null;
}

/**
 * Gets the parameterMapping property (cbit.vcell.modelopt.ParameterMapping[]) value.
 * @return The parameterMapping property value.
 * @see #setParameterMapping
 */
public ParameterMappingSpec[] getParameterMappingSpecs() {
	return fieldParameterMappingSpecs;
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
 * Gets the constraintData property (cbit.vcell.opt.ConstraintData) value.
 * @return The constraintData property value.
 * @see #setConstraintData
 */
public ReferenceData getReferenceData() {
	return fieldReferenceData;
}


/**
 * Gets the referenceDataMappingSpecs property (cbit.vcell.modelopt.ReferenceDataMappingSpec[]) value.
 * @return The referenceDataMappingSpecs property value.
 * @see #setReferenceDataMappingSpecs
 */
public ReferenceDataMappingSpec getReferenceDataMappingSpec(String dataColumnName) {
	for (int i = 0; fieldReferenceDataMappingSpecs!=null && i < fieldReferenceDataMappingSpecs.length; i++){
		if (fieldReferenceDataMappingSpecs[i].getReferenceDataColumnName().equals(dataColumnName)){
			return fieldReferenceDataMappingSpecs[i];
		}
	}
	return null;
}


/**
 * Gets the referenceDataMappingSpecs property (cbit.vcell.modelopt.ReferenceDataMappingSpec[]) value.
 * @return The referenceDataMappingSpecs property value.
 * @see #setReferenceDataMappingSpecs
 */
public ReferenceDataMappingSpec[] getReferenceDataMappingSpecs() {
	return fieldReferenceDataMappingSpecs;
}


/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public SimulationContext getSimulationContext() {
	return parameterEstimationTask.getSimulationContext();
}


/**
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 10:35:28 AM)
 */
private void refreshParameterMappingSpecs() throws ExpressionException {
	Parameter modelParameters[] = getModelParameters();

	ParameterMappingSpec[] newParameterMappingSpecs = new ParameterMappingSpec[modelParameters.length];
	
	for (int i = 0; i < newParameterMappingSpecs.length; i++){
		newParameterMappingSpecs[i] = new ParameterMappingSpec(modelParameters[i]);
		//check if parameter mapping spec already exist
		ParameterMappingSpec  memoryParameterMappingSpec = null;
		if(this.getParameterMappingSpecs() != null && this.getParameterMappingSpecs().length > 0)
		{
			memoryParameterMappingSpec = this.getParameterMappingSpecByCompareEqual(modelParameters[i]);
		}
		
		double modelValue = modelParameters[i].getConstantValue();
		//parameter mapping spec already exist
		if(memoryParameterMappingSpec != null)
		{
			newParameterMappingSpecs[i].setCurrent(memoryParameterMappingSpec.getCurrent());
			if(memoryParameterMappingSpec.getLow() == Double.NEGATIVE_INFINITY) //make sure not to show infinite for low and high
			{
				newParameterMappingSpecs[i].setLow(modelValue * 0.1);
			}
			else
			{
				newParameterMappingSpecs[i].setLow(memoryParameterMappingSpec.getLow());
			}
			if(memoryParameterMappingSpec.getHigh() == Double.POSITIVE_INFINITY) //make sure not to show infinite for low and high
			{
				newParameterMappingSpecs[i].setHigh(modelValue * 10);
			}
			else
			{
				newParameterMappingSpecs[i].setHigh(memoryParameterMappingSpec.getHigh());
			}
			newParameterMappingSpecs[i].setSelected(memoryParameterMappingSpec.isSelected());
		}
		else //not found
		{
			newParameterMappingSpecs[i].setLow(modelValue * 0.1);//set lower bound as 10% or the model value
			newParameterMappingSpecs[i].setHigh(modelValue * 10);//set upper bound as 10 times of the model value
		}
	}
	try {
		setParameterMappingSpecs(newParameterMappingSpecs);
//		removeUncoupledParameters();
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/19/2005 3:20:34 PM)
 */
public void refreshDependencies(boolean isRemoveUncoupledParameters) {
	if (getSimulationContext() != null) {
		//remove listeners - simContext, mathDesc, model, spContextSpec, reactionSteps & kinetics
		getSimulationContext().removePropertyChangeListener(this);
		Model model = getSimulationContext().getModel();
		updateListenersList(model, false);
		// add listeners - simContext, mathDesc, model, spContextSpec, reactionSteps & kinetics
		getSimulationContext().addPropertyChangeListener(this);
		updateListenersList(model, true);
	}
	if(isRemoveUncoupledParameters) {
		System.err.println("not calling removeUncoupledParameters() during ModelOptimizationSpec.refreshDependencies() ... need to invoke removeUncoupledParameters functionality a different way");
		//removeUncoupledParameters();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 10:35:28 AM)
 */
private void refreshReferenceDataMappingSpecs() {
	if (fieldReferenceData == null || fieldReferenceData.getColumnNames()==null || fieldReferenceData.getColumnNames().length == 0){
		setReferenceDataMappingSpecs(null);
		return;
	}

	String refDataColumnNames[] = fieldReferenceData.getColumnNames();

	//
	// 1. create new mappingSpecs array (one new mappingSpec for each column in referenceData)
	// 2. copy existing mappings (model objects from previous mappingSpec) if already mapped.
	//
	ReferenceDataMappingSpec[] newReferenceDataMappingSpecs = new ReferenceDataMappingSpec[getReferenceData().getColumnNames().length];
	int numUnmapped = 0;
	for (int i = 0; i < newReferenceDataMappingSpecs.length; i++){
		newReferenceDataMappingSpecs[i] = new ReferenceDataMappingSpec(refDataColumnNames[i]);
		for (int j = 0; fieldReferenceDataMappingSpecs!=null && j < fieldReferenceDataMappingSpecs.length; j++){
			if (fieldReferenceDataMappingSpecs[j].getReferenceDataColumnName().equals(refDataColumnNames[i])){
				try {
					newReferenceDataMappingSpecs[i].setModelObject(fieldReferenceDataMappingSpecs[j].getModelObject());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					System.out.println("exception consumed, refresh mapping");
				}
			}
		}
		if (newReferenceDataMappingSpecs[i].getModelObject()==null){
			numUnmapped++;
		}
	}
	//
	// 3. for referenceData columns that remain unmapped, see if there are obvious mappings
	//
	if (numUnmapped>0){
		SymbolTableEntry[] stes = calculateTimeDependentModelObjects(getSimulationContext());
		for (int i = 0; i < newReferenceDataMappingSpecs.length; i++){
			if (newReferenceDataMappingSpecs[i].getModelObject()==null){
				for (int j = 0;stes!=null && j < stes.length; j++){
					if (stes[j].getName().equals(newReferenceDataMappingSpecs[i].getReferenceDataColumnName())){
						try {
							newReferenceDataMappingSpecs[i].setModelObject(stes[j]);
						}catch (java.beans.PropertyVetoException e){
							e.printStackTrace(System.out);
							System.out.println("exception consumed, refresh mapping");
						}
					}
				}
			}
		}
	}
	
	
	setReferenceDataMappingSpecs(newReferenceDataMappingSpecs);
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
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 * Sets the parameterMapping property (cbit.vcell.modelopt.ParameterMapping[]) value.
 * @param parameterMapping The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getParameterMapping
 */
public void setParameterMappingSpecs(ParameterMappingSpec[] parameterMappingSpecs) throws java.beans.PropertyVetoException {
	ParameterMappingSpec[] oldValue = fieldParameterMappingSpecs;
	fireVetoableChange("parameterMappingSpecs", oldValue, parameterMappingSpecs);
	fieldParameterMappingSpecs = parameterMappingSpecs;
	firePropertyChange("parameterMappingSpecs", oldValue, parameterMappingSpecs);
}


/**
 * Sets the constraintData property (cbit.vcell.opt.ConstraintData) value.
 * @param constraintData The new value for the property.
 * @see #getConstraintData
 */
public void setReferenceData(ReferenceData referenceData) {
	ReferenceData oldValue = fieldReferenceData;
	fieldReferenceData = referenceData;

	refreshReferenceDataMappingSpecs();
	
	firePropertyChange(PROPERTY_NAME_REFERENCE_DATA, oldValue, referenceData);
}


/**
 * Sets the referenceDataMappingSpecs property (cbit.vcell.modelopt.ReferenceDataMappingSpec[]) value.
 * @param referenceDataMappingSpecs The new value for the property.
 * @see #getReferenceDataMappingSpecs
 */
private void setReferenceDataMappingSpecs(ReferenceDataMappingSpec[] referenceDataMappingSpecs) {
	ReferenceDataMappingSpec[] oldValue = fieldReferenceDataMappingSpecs;
	fieldReferenceDataMappingSpecs = referenceDataMappingSpecs;
	firePropertyChange("referenceDataMappingSpecs", oldValue, referenceDataMappingSpecs);
}

public int getReferenceDataTimeColumnIndex() {
	int timeIndex = -1;
	ReferenceDataMappingSpec[] mappingSpecs = getReferenceDataMappingSpecs();
	if (mappingSpecs != null) {
		for (int i = 0; i < mappingSpecs.length; i ++) {
			SymbolTableEntry modelObject = mappingSpecs[i].getModelObject();
			if (modelObject != null && (modelObject.equals(parameterEstimationTask.getSimulationContext().getModel().getTIME()))) {
				timeIndex = i;
				break;
			}
		}
	}
	return timeIndex;
}


public void propertyChange(PropertyChangeEvent event) {
	// remove ModelOptSpec as listener to model (and reactions, kinetic params, etc)
	updateListenersList(getSimulationContext().getModel(), false);
	// re-add modelOptSpec as listener to model (and reactions, kinetic params, etc); since changes in added/deleted reactions need to be listened to.
	updateListenersList(getSimulationContext().getModel(), true);

	// for all propChangeEvents, initialize ParamMapppingSpecs only then changes in params (expr or numeric) will be recorded properly
	try {
		refreshParameterMappingSpecs();
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
	}
}

/**
 * If bAdd is <false>, method removes ModelOptSpec as PropertyChangeListener to object. If bAdd is <true>, 
 * ModelOptSpec is added as PropertyChangeListener to object
 * @param model
 * @param bAdd
 */
private void updateListenersList(Model model, boolean bAdd) {
	// remove listeners - simContext, mathDesc, model, spContextSpecs, reactionSteps & kinetics (since kinetic parameters are paramMappingSpecs)
	ReactionStep[] reactionSteps = model.getReactionSteps();
	SpeciesContextSpec[] scsArray = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	ModelParameter[] modelParams = model.getModelParameters();
	if (!bAdd) {
		model.removePropertyChangeListener(this);
		for (int i = 0; i < modelParams.length; i++) {
			modelParams[i].removePropertyChangeListener(this);
		}
		// since only spContextSpec initCondition Param is being added to paramMappingSpecs, only remove/add listener for initCondnParam?
		for (int i = 0; i < scsArray.length; i++) {
			scsArray[i].getInitialConditionParameter().removePropertyChangeListener(this);
		}
		for (int i = 0; reactionSteps != null && i < reactionSteps.length; i++) {
			reactionSteps[i].removePropertyChangeListener(this);
			reactionSteps[i].getKinetics().removePropertyChangeListener(this);
			KineticsParameter[] kps = reactionSteps[i].getKinetics().getKineticsParameters();
			for (int j = 0; kps != null && j < kps.length; j++) {
				kps[j].removePropertyChangeListener(this);
			}
			KineticsProxyParameter[] kpps = reactionSteps[i].getKinetics().getProxyParameters();
			for (int j = 0; kpps != null && j < kpps.length; j++) {
				kpps[j].removePropertyChangeListener(this);
			}
		}
	} else {
	// add listeners - simContext, mathDesc, model, spContextSpecs, reactionSteps & kinetics & its params
		model.addPropertyChangeListener(this);
		for (int i = 0; i < modelParams.length; i++) {
			modelParams[i].addPropertyChangeListener(this);
		}
		for (int i = 0; i < scsArray.length; i++) {
			scsArray[i].getInitialConditionParameter().addPropertyChangeListener(this);
		}
		for (int i = 0; i < reactionSteps.length; i++) {
			reactionSteps[i].addPropertyChangeListener(this);
			reactionSteps[i].getKinetics().addPropertyChangeListener(this);
			KineticsParameter[] kps = reactionSteps[i].getKinetics().getKineticsParameters();
			for (int j = 0; j < kps.length; j++) {
				kps[j].addPropertyChangeListener(this);
			}
			KineticsProxyParameter[] kpps = reactionSteps[i].getKinetics().getProxyParameters();
			for (int j = 0; j < kpps.length; j++) {
				kpps[j].addPropertyChangeListener(this);
			}
		}
	}
}


public void setComputeProfileDistributions(boolean bComputeProfileDistributions) {
	this.bComputeProfileDistributions = bComputeProfileDistributions;
}


public boolean isComputeProfileDistributions() {
	return bComputeProfileDistributions;
}

/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 8:11:32 PM)
 * @return cbit.vcell.mapping.MathSystemHash
 * @param mathDesc cbit.vcell.math.MathDescription
 */
private static MathSystemHash fromMath(cbit.vcell.math.MathDescription mathDesc) {
	MathSystemHash hash = new MathSystemHash();

	hash.addSymbol(new MathSystemHash.IndependentVariable("t"));
	hash.addSymbol(new MathSystemHash.IndependentVariable("x"));
	hash.addSymbol(new MathSystemHash.IndependentVariable("y"));
	hash.addSymbol(new MathSystemHash.IndependentVariable("z"));

	Variable vars[] = (Variable[])BeanUtils.getArray(mathDesc.getVariables(),Variable.class);
	for (int i = 0; i < vars.length; i++){
		hash.addSymbol(new MathSystemHash.Variable(vars[i].getName(),vars[i].getExpression()));
	}

	SubDomain subDomains[] = (SubDomain[])BeanUtils.getArray(mathDesc.getSubDomains(),SubDomain.class);
	for (int i = 0; i < subDomains.length; i++){

		Equation[] equations = (Equation[])BeanUtils.getArray(subDomains[i].getEquations(),Equation.class);
		for (int j = 0; j < equations.length; j++){
			MathSystemHash.Variable var = (MathSystemHash.Variable)hash.getSymbol(equations[j].getVariable().getName());
			hash.addSymbol(new MathSystemHash.VariableInitial(var,equations[j].getInitialExpression()));
			if (equations[j] instanceof PdeEquation){
				//cbit.vcell.math.PdeEquation pde = (cbit.vcell.math.PdeEquation)equations[j];
				//hash.addSymbol(new MathSystemHash.VariableDerivative(var,pde.getRateExpression()));
				throw new RuntimeException("MathSystemHash doesn't yet support spatial models");
			}else if (equations[j] instanceof VolumeRegionEquation){
				//cbit.vcell.math.VolumeRegionEquation vre = (cbit.vcell.math.VolumeRegionEquation)equations[j];
				//hash.addSymbol(new MathSystemHash.VariableDerivative(var,vre.getRateExpression()));
				throw new RuntimeException("MathSystemHash doesn't yet support spatial models");
			}else if (equations[j] instanceof MembraneRegionEquation){
				//cbit.vcell.math.MembraneRegionEquation mre = (cbit.vcell.math.MembraneRegionEquation)equations[j];
				//hash.addSymbol(new MathSystemHash.VariableDerivative(var,mre.getRateExpression()));
				throw new RuntimeException("MathSystemHash doesn't yet support spatial models");
			}else if (equations[j] instanceof FilamentRegionEquation){
				//cbit.vcell.math.FilamentRegionEquation fre = (cbit.vcell.math.FilamentRegionEquation)equations[j];
				//hash.addSymbol(new MathSystemHash.VariableDerivative(var,fre.getRateExpression()));
				throw new RuntimeException("MathSystemHash doesn't yet support spatial models");
				
			}else if (equations[j] instanceof OdeEquation){
				OdeEquation ode = (OdeEquation)equations[j];
				hash.addSymbol(new MathSystemHash.VariableDerivative(var,ode.getRateExpression()));
			}
		}
	}
	return hash;
}

public final ParameterEstimationTask getParameterEstimationTask() {
	return parameterEstimationTask;
}

public boolean isEmpty()
{
	if((fieldParameterMappingSpecs == null || getNumberSelectedParameters() ==0)  && 
		(fieldReferenceData == null || (fieldReferenceData.getNumDataColumns() == 0 && fieldReferenceData.getNumDataRows() == 0)) && 
		(fieldReferenceDataMappingSpecs == null || fieldReferenceDataMappingSpecs.length == 0))
	{
		return true;
	}
	else
	{
		return false;
	}
}

public int getNumberSelectedParameters()
{
	int count = 0;
	for(ParameterMappingSpec pms : fieldParameterMappingSpecs)
	{
		if(pms.isSelected())
		{
			count ++;
		}
	}
	return count;
}

}
