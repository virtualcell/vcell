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
import java.beans.PropertyVetoException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import org.vcell.optimization.CopasiOptSolverCallbacks;
import org.vcell.util.Compare;
import org.vcell.util.Issue;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Constant;
import cbit.vcell.math.InconsistentDomainException;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.Variable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.Parameter;
import cbit.vcell.opt.OdeObjectiveFunction;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solvers.NativeIDASolver;
import cbit.vcell.util.RowColumnResultSet;
/**
 * Insert the type's description here.
 * Creation date: (5/2/2006 4:35:50 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ParameterEstimationTask extends AnalysisTask {
	
	public static final String defaultTaskName = "DefaultTask"; 
	
	public static final String PROPERTY_NAME_OPTIMIZATION_RESULT_SET = "optimizationResultSet";
	private ModelOptimizationSpec fieldModelOptimizationSpec = null; //parameters to be estimated, reference data and referece data/model var mapping
	private OptimizationSolverSpec fieldOptimizationSolverSpec = null; //solver selection and setting
	private OptimizationResultSet fieldOptimizationResultSet = null; //contains best paramters and least function value and number of evaluations
	
	private transient ModelOptimizationMapping fieldModelOptimizationMapping = null; //objective function, constraints, model vars/parameters mapping, math symbol mapping
	private transient MathSymbolMapping fieldMathSymbolMapping = null; //MathSymbolMapping is a field in ModelOptimizationMapping too.
	private transient CopasiOptSolverCallbacks fieldOptSolverCallbacks = new CopasiOptSolverCallbacks();
	private transient java.lang.String fieldSolverMessageText = new String();
	private SimulationContext simulationContext = null;

/**
 * ParameterEstimationTask constructor comment.
 */
public ParameterEstimationTask(SimulationContext simContext) throws ExpressionException {
	super();
	simulationContext = simContext;
	fieldModelOptimizationSpec = new ModelOptimizationSpec(this);
	fieldModelOptimizationMapping = new ModelOptimizationMapping(fieldModelOptimizationSpec);
	fieldOptimizationSolverSpec = new OptimizationSolverSpec(OptimizationSolverSpec.SOLVERTYPE_CFSQP);
}


/**
 * ParameterEstimationTask constructor comment.
 */
public ParameterEstimationTask(SimulationContext simContext, ParameterEstimationTask taskToCopy) throws ExpressionException, PropertyVetoException {
	super();
	this.simulationContext = simContext;
	setName(taskToCopy.getName()); 
	fieldModelOptimizationSpec = new ModelOptimizationSpec(this, taskToCopy.getModelOptimizationSpec());
	fieldModelOptimizationMapping = new ModelOptimizationMapping(fieldModelOptimizationSpec);
	fieldOptimizationSolverSpec = new OptimizationSolverSpec(taskToCopy.getOptimizationSolverSpec());
}

/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 5:12:57 PM)
 * @param message java.lang.String
 */
public void appendSolverMessageText(String message) {
	setSolverMessageText(getSolverMessageText()+message);
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof ParameterEstimationTask){
		ParameterEstimationTask task = (ParameterEstimationTask)obj;

		//
		// only compare non-transient fields.
		//

		if (!getModelOptimizationSpec().compareEqual(task.getModelOptimizationSpec())){
			return false;
		}

		if (!Compare.isEqual(getOptimizationSolverSpec(),task.getOptimizationSolverSpec())){
			return false;
		}
		
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/2/2006 11:04:39 PM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(java.util.List<Issue> issueList) {
	getModelOptimizationSpec().gatherIssues(issueList);
	if (getModelOptimizationMapping().getOptimizationSpec()!=null){
		getModelOptimizationMapping().getOptimizationSpec().gatherIssues(issueList);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/2006 6:44:13 PM)
 * @return java.lang.String
 */
public java.lang.String getAnalysisTypeDisplayName() {
	return "Parameter Estimation";
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2006 11:20:12 AM)
 * @return cbit.vcell.mapping.MathSymbolMapping
 */
public cbit.vcell.mapping.MathSymbolMapping getMathSymbolMapping() {
	if(fieldMathSymbolMapping == null)
	{
		try {
			fieldMathSymbolMapping = getModelOptimizationMapping().computeOptimizationSpec();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	return fieldMathSymbolMapping;
}


/**
 * Gets the modelOptimizationMapping property (cbit.vcell.modelopt.ModelOptimizationMapping) value.
 * @return The modelOptimizationMapping property value.
 * @see #setModelOptimizationMapping
 */
public ModelOptimizationMapping getModelOptimizationMapping() {
	return fieldModelOptimizationMapping;
}


/**
 * Gets the modelOptimizationSpec property (cbit.vcell.modelopt.ModelOptimizationSpec) value.
 * @return The modelOptimizationSpec property value.
 * @see #setModelOptimizationSpec
 */
public ModelOptimizationSpec getModelOptimizationSpec() {
	return fieldModelOptimizationSpec;
}

public ODESolverResultSet getOdeSolverResultSet() throws Exception {
	return getOdeSolverResultSet(getModelOptimizationMapping().getOptimizationSpec(),getOptimizationResultSet());
}

public ODESolverResultSet getOdeSolverResultSet(OptimizationSpec optSpec, OptimizationResultSet optResultSet) throws Exception {
	if (optResultSet==null) {
		return null;
	}
	String[] parameterNames = optResultSet.getOptSolverResultSet().getParameterNames();
	double[] bestEstimates = optResultSet.getOptSolverResultSet().getBestEstimates();
	//if we don't have parameter names or best estimates, return null. if we have them, we can run a simulation and generate a solution
	if (parameterNames == null || parameterNames.length == 0  || bestEstimates ==null || bestEstimates.length == 0)
	{
		return null;
	}
	//check if we have solution or not, if not, generate a solution since we have the best estimates
	if(optResultSet.getSolutionNames() == null)
	{
		RowColumnResultSet rcResultSet = getRowColumnRestultSetByBestEstimations(parameterNames, bestEstimates);
		optResultSet.setSolutionFromRowColumnResultSet(rcResultSet);
	}
	
	String[] solutionNames = optResultSet.getSolutionNames();
	if (solutionNames!=null && solutionNames.length>0){
		ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
		// add data column descriptions
		for (int i = 0; i < solutionNames.length; i++){
			odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(solutionNames[i]));
		}
		
		
		
		
		//
		// add row data
		//
		int numRows = optResultSet.getSolutionValues(0).length;
		for (int i = 0; i < numRows; i++){
			odeSolverResultSet.addRow(optResultSet.getSolutionRow(i));
		}
		//
		// make temporary simulation (with overrides for parameter values)
		//
		MathDescription mathDesc = simulationContext.getMathDescription();
		Simulation simulation = new Simulation(mathDesc);
		SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(simulation, 0);
		//
		// set math overrides with initial guess
		//
		for (int i = 0; i < optSpec.getParameters().length; i++){
			cbit.vcell.opt.Parameter parameter = optSpec.getParameters()[i];
			simulation.getMathOverrides().putConstant(new Constant(parameter.getName(),new Expression(parameter.getInitialGuess())));
		}
		
		//
		// correct math overrides with parameter solution
		//
		for (int i = 0; i < parameterNames.length; i++){
			simulation.getMathOverrides().putConstant(new Constant(parameterNames[i],new Expression(optResultSet.getOptSolverResultSet().getBestEstimates()[i])));
		}

		//
		// add functions (evaluating them at optimal parameter)
		//
		Vector <AnnotatedFunction> annotatedFunctions = simSymbolTable.createAnnotatedFunctionsList(mathDesc);
		for (AnnotatedFunction f: annotatedFunctions){
			Expression funcExp = f.getExpression();
			for (int j = 0; j < parameterNames.length; j ++) {
				funcExp.substituteInPlace(new Expression(parameterNames[j]), new Expression(optResultSet.getOptSolverResultSet().getBestEstimates()[j]));
			}
			odeSolverResultSet.addFunctionColumn(new FunctionColumnDescription(funcExp,f.getName(),null,f.getName(),false));
		}

		return odeSolverResultSet;
	}else{
		return null;
	}

}



/**
 * Gets the optimizationResultSet property (cbit.vcell.opt.OptimizationResultSet) value.
 * @return The optimizationResultSet property value.
 * @see #setOptimizationResultSet
 */
public cbit.vcell.opt.OptimizationResultSet getOptimizationResultSet() {
	return fieldOptimizationResultSet;
}


/**
 * Gets the optimizationSolverSpec property (cbit.vcell.opt.OptimizationSolverSpec) value.
 * @return The optimizationSolverSpec property value.
 * @see #setOptimizationSolverSpec
 */
public cbit.vcell.opt.OptimizationSolverSpec getOptimizationSolverSpec() {
	return fieldOptimizationSolverSpec;
}


/**
 * Gets the optSolverCallbacks property (cbit.vcell.opt.solvers.OptSolverCallbacks) value.
 * @return The optSolverCallbacks property value.
 * @see #setOptSolverCallbacks
 */
public CopasiOptSolverCallbacks getOptSolverCallbacks() {
	return fieldOptSolverCallbacks;
}


/**
 * Gets the solverMessageText property (java.lang.String) value.
 * @return The solverMessageText property value.
 * @see #setSolverMessageText
 */
public java.lang.String getSolverMessageText() {
	return fieldSolverMessageText;
}


/**
 * Insert the method's description here.
 * Creation date: (5/2/2006 5:00:50 PM)
 */
public void refreshDependencies() {
	getModelOptimizationSpec().refreshDependencies();
	if (getModelOptimizationMapping() != null) {
		getModelOptimizationMapping().refreshDependencies();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/2/2006 11:01:21 PM)
 */
public void refreshMappings() throws MappingException, MathException {
	if(!fieldModelOptimizationSpec.hasSelectedParameters())
	{
		throw new RuntimeException("No parameters are selected for optimization.");
	}
	if (fieldModelOptimizationSpec.getReferenceData()==null){
		throw new RuntimeException("reference data not specified");
	}
	ReferenceDataMappingSpec[] refDataMappingSpecs = fieldModelOptimizationSpec.getReferenceDataMappingSpecs();
	for (int i = 0; i < refDataMappingSpecs.length; i++){
		if (refDataMappingSpecs[i].getModelObject()==null){
			throw new RuntimeException("In your reference data, '"+refDataMappingSpecs[i].getReferenceDataColumnName()+"' has not been assigned a Model Association. " +
					"Go to \"Reference Data\" to assign model associations to all reference data.");
		}
	}
	
	fieldMathSymbolMapping = getModelOptimizationMapping().computeOptimizationSpec();
}


/**
 * Sets the optimizationResultSet property (cbit.vcell.opt.OptimizationResultSet) value.
 * @param optimizationResultSet The new value for the property.
 * @see #getOptimizationResultSet
 */
public void setOptimizationResultSet(OptimizationResultSet optimizationResultSet) {
	OptimizationResultSet oldValue = fieldOptimizationResultSet;
	fieldOptimizationResultSet = optimizationResultSet;
	firePropertyChange(PROPERTY_NAME_OPTIMIZATION_RESULT_SET, oldValue, optimizationResultSet);
}


/**
 * Sets the optimizationSolverSpec property (cbit.vcell.opt.OptimizationSolverSpec) value.
 * @param optimizationSolverSpec The new value for the property.
 * @see #getOptimizationSolverSpec
 */
public void setOptimizationSolverSpec(OptimizationSolverSpec optimizationSolverSpec) {
	OptimizationSolverSpec oldValue = fieldOptimizationSolverSpec;
	fieldOptimizationSolverSpec = optimizationSolverSpec;
	firePropertyChange("optimizationSolverSpec", oldValue, optimizationSolverSpec);
}

/**
 * Sets the solverMessageText property (java.lang.String) value.
 * @param solverMessageText The new value for the property.
 * @see #getSolverMessageText
 */
public void setSolverMessageText(java.lang.String solverMessageText) {
	String oldValue = fieldSolverMessageText;
	fieldSolverMessageText = solverMessageText;
	firePropertyChange("solverMessageText", oldValue, solverMessageText);
}

public Double getCurrentSolution(ParameterMappingSpec parameterMappingSpec) {
	if (getOptimizationResultSet() != null){
		//
		// find mapping from this parameterMappingSpec to the optimization parameter.  Then lookup the opt parameter in the result set.
		//
		for (int i = 0; i < getModelOptimizationMapping().getParameterMappings().length; i++){
			if (getModelOptimizationMapping().getParameterMappings()[i].getModelParameter() == parameterMappingSpec.getModelParameter()){
				ParameterMapping parameterMapping = getModelOptimizationMapping().getParameterMappings()[i];
				String[] parameterNames = getOptimizationResultSet().getOptSolverResultSet().getParameterNames();
				if (parameterNames != null) {
					for (int j = 0; j < parameterNames.length; j++){
						if (parameterNames[j].equals(parameterMapping.getOptParameter().getName())){
							return new Double(getOptimizationResultSet().getOptSolverResultSet().getBestEstimates()[j]);
						}
					}
				}
			}
		}
	}
	return null;
}


public Parameter getModelParameterByMathName(String mathParamName)
{
	ParameterMappingSpec[] paraMappingSpecs = getModelOptimizationSpec().getParameterMappingSpecs();
	for(ParameterMappingSpec pms : paraMappingSpecs)
	{
		Variable var = getMathSymbolMapping().getVariable(pms.getModelParameter());
		//if(var.getName().equals(mathParamName))
		if((var!=null) && (var.getName().equals(mathParamName)))
		{
			return pms.getModelParameter();
		}
	}
	
	return null;
}

public final SimulationContext getSimulationContext() {
	return simulationContext;
}

public boolean isEmpty(){
	if(fieldModelOptimizationSpec == null && fieldOptimizationSolverSpec == null &&
	   fieldModelOptimizationMapping == null && fieldMathSymbolMapping == null && fieldOptimizationResultSet == null)
	{
		return true;
	}
	if(fieldModelOptimizationSpec != null) //check empty or not by checking parameters length and reference data.
	{
		return fieldModelOptimizationSpec.isEmpty();
	}
	//optimizationSolverSpec is not going to be null, it has all defaults.
	//modeloptimizationmapping requires parameters and data, checking parameters is enough
	//math symboldmapping requires parameters, checking parameters is enough
	//optimizationResultSet, without parameters it's not meaningful.
	return false;
}

public RowColumnResultSet getRowColumnRestultSetByBestEstimations(String[] paramNames, double[] paramValues) throws Exception
{
	//create a temp simulation based on math description
	Simulation simulation = new Simulation(getSimulationContext().getMathDescription());
	
	ReferenceData refData = getModelOptimizationSpec().getReferenceData();
	double[] times = refData.getDataByColumn(0);
	double endTime = times[times.length-1];
	ExplicitOutputTimeSpec exTimeSpec = new ExplicitOutputTimeSpec(times);
	//set simulation ending time and output interval
	simulation.getSolverTaskDescription().setTimeBounds(new TimeBounds(0, endTime));
	simulation.getSolverTaskDescription().setOutputTimeSpec(exTimeSpec);
	//set parameters as math overrides
	MathOverrides mathOverrides = simulation.getMathOverrides();
	for (int i = 0; i < paramNames.length; i++){
		mathOverrides.putConstant(new Constant(paramNames[i],new Expression(paramValues[i])));
	}
	//get input model string
	StringWriter stringWriter = new StringWriter();
	IDAFileWriter idaFileWriter = new IDAFileWriter(new PrintWriter(stringWriter,true), new SimulationTask(new SimulationJob(simulation, 0, null),0));
	idaFileWriter.write();
	stringWriter.close();
	StringBuffer buffer = stringWriter.getBuffer();
	String idaInputString = buffer.toString();
	
	RowColumnResultSet rcResultSet = null;
	NativeIDASolver nativeIDASolver = new NativeIDASolver();
	rcResultSet = nativeIDASolver.solve(idaInputString);
	return rcResultSet;
}

}
