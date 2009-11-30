package cbit.vcell.modelopt;
import java.util.Vector;

import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
/**
 * Insert the type's description here.
 * Creation date: (8/22/2005 9:26:10 AM)
 * @author: Jim Schaff
 */
public class ModelOptimizationMapping {
	private ModelOptimizationSpec modelOptimizationSpec = null;
	private OptimizationSpec optimizationSpec = null;
	private ParameterMapping[] parameterMappings = null;
	private cbit.vcell.mapping.MathSymbolMapping mathSymbolMapping = null;

/**
 * ModelOptimizationMapping constructor comment.
 */
public ModelOptimizationMapping(ModelOptimizationSpec argModelOptimizationSpec) {
	super();
	modelOptimizationSpec = argModelOptimizationSpec;
}


/**
 * Insert the method's description here.
 * Creation date: (8/30/2005 3:16:33 PM)
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 * @param optResultSet cbit.vcell.opt.OptimizationResultSet
 */
public void applySolutionToMathOverrides(cbit.vcell.solver.Simulation simulation, cbit.vcell.opt.OptimizationResultSet optResultSet) throws cbit.vcell.parser.ExpressionException {
	if (simulation==null){
		throw new RuntimeException("simulation is null");
	}
	//
	// load initial guesses for all parameters into MathOverrides
	// (Include those being optimized as well as those not being optimized)
	//
	if (mathSymbolMapping==null){
		try {
			computeOptimizationSpec();
		}catch (cbit.vcell.mapping.MappingException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("couldn't generate math to map parameters to simulation");
		}catch (cbit.vcell.math.MathException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("couldn't generate math to map parameters to simulation");
		}
	}
	ParameterMappingSpec[] parameterMappingSpecs = getModelOptimizationSpec().getParameterMappingSpecs();
	for (int i = 0; i < parameterMappingSpecs.length; i++){
		cbit.vcell.math.Variable var = mathSymbolMapping.getVariable(parameterMappingSpecs[i].getModelParameter());
		if (var instanceof cbit.vcell.math.Constant){
			simulation.getMathOverrides().putConstant(new cbit.vcell.math.Constant(var.getName(),new cbit.vcell.parser.Expression(parameterMappingSpecs[i].getCurrent())));
		}
	}

	//
	// if solution exists, override initial guesses with solution
	//
	if (optResultSet != null){
		String[] solutionNames = optResultSet.getSolutionNames();
		if (solutionNames!=null && solutionNames.length>0){
			//
			// correct math overrides with parameter solution
			//
			for (int i = 0; i < optResultSet.getParameterNames().length; i++){
				simulation.getMathOverrides().putConstant(new cbit.vcell.math.Constant(optResultSet.getParameterNames()[i],new cbit.vcell.parser.Expression(optResultSet.getParameterValues()[i])));
			}
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 9:26:52 AM)
 * @return cbit.vcell.opt.OptimizationSpec
 * @param modelOptimizationSpec cbit.vcell.modelopt.ModelOptimizationSpec
 */
cbit.vcell.mapping.MathSymbolMapping computeOptimizationSpec() throws cbit.vcell.math.MathException, cbit.vcell.mapping.MappingException {

	if (getModelOptimizationSpec().getReferenceData()==null){
		System.out.println("no referenced data defined");
		return null;
	}
	OptimizationSpec optSpec = new OptimizationSpec();
	parameterMappings = null;

	//
	// get original MathDescription (later to be substituted for local/global parameters).
	//
	cbit.vcell.mapping.SimulationContext simContext = modelOptimizationSpec.getSimulationContext();
	cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContext);
	MathDescription origMathDesc = null;
	mathSymbolMapping = null;
	try {
		origMathDesc = mathMapping.getMathDescription();
		mathSymbolMapping = mathMapping.getMathSymbolMapping();
	}catch (cbit.vcell.matrix.MatrixException e){
		e.printStackTrace(System.out);
		throw new cbit.vcell.mapping.MappingException(e.getMessage());
	}catch (cbit.vcell.model.ModelException e){
		e.printStackTrace(System.out);
		throw new cbit.vcell.mapping.MappingException(e.getMessage());
	}catch (cbit.vcell.parser.ExpressionException e){
		e.printStackTrace(System.out);
		throw new cbit.vcell.math.MathException(e.getMessage());
	}

	//
	// create objective function (mathDesc and data)
	//
	StructureMapping structureMapping = simContext.getGeometryContext().getStructureMappings()[0];
	ReferenceData referenceData = getRemappedReferenceData(mathMapping,structureMapping);
	if (referenceData==null){
		throw new RuntimeException("no referenced data defined");
	}
	cbit.vcell.opt.OdeObjectiveFunction odeObjectiveFunction = new cbit.vcell.opt.OdeObjectiveFunction(origMathDesc,referenceData);
	optSpec.setObjectiveFunction(odeObjectiveFunction);

	//
	// get parameter mappings
	//
	ParameterMappingSpec[] parameterMappingSpecs = modelOptimizationSpec.getParameterMappingSpecs();
	Vector parameterMappingList = new Vector();
	cbit.vcell.math.Variable allVars[] = (cbit.vcell.math.Variable[])org.vcell.util.BeanUtils.getArray(origMathDesc.getVariables(),cbit.vcell.math.Variable.class);
	for (int i = 0; i < parameterMappingSpecs.length; i++){
		cbit.vcell.model.Parameter modelParameter = parameterMappingSpecs[i].getModelParameter();
		String mathSymbol = mathMapping.getMathSymbol(modelParameter,structureMapping);
		cbit.vcell.math.Variable mathVariable = origMathDesc.getVariable(mathSymbol);
		cbit.vcell.opt.Parameter optParameter = new cbit.vcell.opt.Parameter(
								mathSymbol,
								parameterMappingSpecs[i].getLow(),
								parameterMappingSpecs[i].getHigh(),
								Math.abs(parameterMappingSpecs[i].getCurrent()) < 1.0E-10 ? 1.0 : Math.abs(parameterMappingSpecs[i].getCurrent()),
								parameterMappingSpecs[i].getCurrent());
		ParameterMapping parameterMapping = new ParameterMapping(modelParameter,optParameter,mathVariable);
		//
		// replace constant values with "initial guess"
		//
		if (mathVariable instanceof cbit.vcell.math.Constant){
			cbit.vcell.math.Constant origConstant = (cbit.vcell.math.Constant)mathVariable;
			for (int j = 0; j < allVars.length; j++){
				if (allVars[j].equals(origConstant)){
					if (parameterMappingSpecs[i].isSelected()) {
						allVars[j] = new cbit.vcell.math.ParameterVariable(origConstant.getName());
					} else {
						allVars[j] = new cbit.vcell.math.Constant(origConstant.getName(),new cbit.vcell.parser.Expression(optParameter.getInitialGuess()));
					}
				}
			}
		}
		//
		// add to list if "selected" for optimization.
		//
		if (parameterMappingSpecs[i].isSelected()){
			parameterMappingList.add(parameterMapping);
		}
	}
	parameterMappings = (ParameterMapping[])org.vcell.util.BeanUtils.getArray(parameterMappingList,ParameterMapping.class);
	try {
		origMathDesc.setAllVariables(allVars);
	}catch (cbit.vcell.parser.ExpressionBindingException e){
		e.printStackTrace(System.out);
		throw new cbit.vcell.math.MathException(e.getMessage());
	}

	//
	// set optimization parameters
	//
	for (int i = 0; i < parameterMappings.length; i++){
		optSpec.addParameter(parameterMappings[i].getOptParameter());
	}

	Vector issueList = new Vector();
	optSpec.gatherIssues(issueList);
	for (int i = 0; i < issueList.size(); i++){
		org.vcell.util.Issue issue = (org.vcell.util.Issue)issueList.elementAt(i);
		System.out.println(issue.toString());
		if (issue.getSeverity()==org.vcell.util.Issue.SEVERITY_ERROR){
			throw new RuntimeException(issue.getMessage());
		}
	}
	//
	//
	//
	optimizationSpec = optSpec;
	return mathSymbolMapping;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2005 10:32:23 AM)
 * @return cbit.vcell.modelopt.ModelOptimizationSpec
 */
public ModelOptimizationSpec getModelOptimizationSpec() {
	return modelOptimizationSpec;
}


/**
 * Insert the method's description here.
 * Creation date: (8/30/2005 3:16:33 PM)
 * @return cbit.vcell.solver.ode.ODESolverResultSet
 * @param optResultSet cbit.vcell.opt.OptimizationResultSet
 */
public static cbit.vcell.solver.ode.ODESolverResultSet getOdeSolverResultSet(OptimizationSpec optSpec, cbit.vcell.opt.OptimizationResultSet optResultSet) throws cbit.vcell.parser.ExpressionException {
	if (optResultSet==null || optResultSet.getParameterNames()==null || optResultSet.getSolutionNames()==null){
		return null;
	}
	String[] solutionNames = optResultSet.getSolutionNames();
	if (solutionNames!=null && solutionNames.length>0){
		ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
		//
		// add data column descriptions
		//
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
		MathDescription mathDesc = ((cbit.vcell.opt.OdeObjectiveFunction)optSpec.getObjectiveFunction()).getMathDescription();
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
		for (int i = 0; i < optResultSet.getParameterNames().length; i++){
			simulation.getMathOverrides().putConstant(new Constant(optResultSet.getParameterNames()[i],new Expression(optResultSet.getParameterValues()[i])));
		}

		//
		// add functions (evaluating them at optimal parameter)
		//
		Vector <AnnotatedFunction> annotatedFunctions = simSymbolTable.createAnnotatedFunctionsList();
		for (AnnotatedFunction f: annotatedFunctions){
			Expression funcExp = f.getExpression();
			for (int j = 0; j < optResultSet.getParameterNames().length; j ++) {
				funcExp.substituteInPlace(new Expression(optResultSet.getParameterNames()[j]), new Expression(optResultSet.getParameterValues()[j]));
			}
			odeSolverResultSet.addFunctionColumn(new FunctionColumnDescription(funcExp,f.getName(),null,f.getName(),false));
		}

		return odeSolverResultSet;
	}else{
		return null;
	}

}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 9:26:52 AM)
 * @return cbit.vcell.opt.OptimizationSpec
 * @param modelOptimizationSpec cbit.vcell.modelopt.ModelOptimizationSpec
 */
public OptimizationSpec getOptimizationSpec() {
	return optimizationSpec;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2005 10:26:34 AM)
 * @return cbit.vcell.modelopt.ParameterMapping[]
 */
public cbit.vcell.modelopt.ParameterMapping[] getParameterMappings() {
	return parameterMappings;
}


/**
 * Gets the constraintData property (cbit.vcell.opt.ConstraintData) value.
 * @return The constraintData property value.
 * @see #setConstraintData
 */
private cbit.vcell.opt.ReferenceData getRemappedReferenceData(cbit.vcell.mapping.MathMapping mathMapping, StructureMapping structureMapping) throws cbit.vcell.mapping.MappingException {
	if (modelOptimizationSpec.getReferenceData()==null){
		return null;
	}
	//
	// make sure time is mapped
	//
	ReferenceData refData = modelOptimizationSpec.getReferenceData();
	ReferenceDataMappingSpec refDataMappingSpecs[] = modelOptimizationSpec.getReferenceDataMappingSpecs();
	cbit.vcell.util.RowColumnResultSet rowColResultSet = new cbit.vcell.util.RowColumnResultSet();
	Vector modelObjectList = new Vector();
	Vector dataList = new Vector();
	
	//
	// find bound columns, (time is always mapped to the first column)
	//
	int mappedColumnCount=0;
	for (int i = 0; i < refDataMappingSpecs.length; i++){
		cbit.vcell.parser.SymbolTableEntry modelObject = refDataMappingSpecs[i].getModelObject();
		if (modelObject!=null){
			int mappedColumnIndex = mappedColumnCount;
			if (modelObject.equals(cbit.vcell.model.ReservedSymbol.TIME)){
				mappedColumnIndex = 0;
			}
			String origRefDataColumnName = refDataMappingSpecs[i].getReferenceDataColumnName();
			int origRefDataColumnIndex = refData.findColumn(origRefDataColumnName);
			if (origRefDataColumnIndex<0){
				throw new RuntimeException("reference data column named '"+origRefDataColumnName+"' not found");
			}
			double columnData[] = refData.getColumnData(origRefDataColumnIndex);
			if (modelObjectList.contains(modelObject)){
				throw new RuntimeException("multiple reference data columns mapped to same model object '"+modelObject.getName()+"'");
			}
			modelObjectList.insertElementAt(modelObject,mappedColumnIndex);
			dataList.insertElementAt(columnData,mappedColumnIndex);
			mappedColumnCount++;
		}
	}

	//
	// must have time and at least one other, else return null
	//
	if (modelObjectList.size()==0){
		throw new RuntimeException("reference data was not associated with model");
	}
	if (modelObjectList.size()==1){
		throw new RuntimeException("reference data was not associated with model, must map time and at least one other column");
	}
	if (!modelObjectList.contains(cbit.vcell.model.ReservedSymbol.TIME)){
		throw new RuntimeException("must map time column of reference data to model");
	}

	//
	// create data columns (time and rest)
	//
	for (int i = 0; i < modelObjectList.size(); i++){
		cbit.vcell.parser.SymbolTableEntry modelObject = (cbit.vcell.parser.SymbolTableEntry)modelObjectList.elementAt(i);
		String symbol = mathMapping.getMathSymbol(modelObject,structureMapping);
		rowColResultSet.addDataColumn(new cbit.vcell.solver.ode.ODESolverResultSetColumnDescription(symbol));
	}

	//
	// populate data columns (time and rest)
	//
	double[] weights = new double[rowColResultSet.getColumnDescriptionsCount()];
	weights[0] = 1.0;	
	
	int numRows = ((double[])dataList.elementAt(0)).length;
	int numColumns = modelObjectList.size();
	for (int j = 0; j < numRows; j++){
		double row[] = new double[numColumns];
		for (int i = 0; i < numColumns; i++){
			row[i] = ((double[])dataList.elementAt(i))[j];
			if (i > 0) {
				weights[i] += row[i] * row[i];
			}
		}
		rowColResultSet.addRow(row);
	}

	for (int i = 0; i < numColumns; i++){
		if (weights[i] == 0) {	
			weights[i] = 1;
		} else {
			weights[i] = 1/weights[i];
		}
	}
	
	cbit.vcell.opt.SimpleReferenceData remappedRefData = new cbit.vcell.opt.SimpleReferenceData(rowColResultSet,weights);
	return remappedRefData;
}


/**
 * Insert the method's description here.
 * Creation date: (5/2/2006 5:17:34 PM)
 */
public void refreshDependencies() {
}
}