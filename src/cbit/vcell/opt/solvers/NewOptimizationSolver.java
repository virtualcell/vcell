package cbit.vcell.opt.solvers;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.jdom.CDATA;
import org.jdom.Element;
import org.vcell.optimization.NativeOptSolver;
import org.vcell.optimization.OptXmlReader;
import org.vcell.optimization.OptXmlTags;
import org.vcell.util.BeanUtils;
import org.vcell.util.ISize;
import org.vcell.util.document.KeyValue;

import cbit.util.xml.XmlUtil;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.Variable;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.opt.Constraint;
import cbit.vcell.opt.ConstraintType;
import cbit.vcell.opt.ExplicitFitObjectiveFunction;
import cbit.vcell.opt.ExplicitObjectiveFunction;
import cbit.vcell.opt.ObjectiveFunction;
import cbit.vcell.opt.OdeObjectiveFunction;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.OptimizationStatus;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.PdeObjectiveFunction;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SpatialReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.ode.CVodeFileWriter;
import cbit.vcell.solver.ode.FunctionColumnDescription;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.ODESolverResultSetColumnDescription;
import cbit.vcell.solvers.FiniteVolumeFileWriter;
import cbit.vcell.solvers.NativeCVODESolver;
import cbit.vcell.solvers.NativeIDASolver;
import cbit.vcell.util.RowColumnResultSet;


public class NewOptimizationSolver implements OptimizationSolver {

public OptimizationResultSet solve(OptimizationSpec os,	OptimizationSolverSpec optSolverSpec, final OptSolverCallbacks optSolverCallbacks) 
						throws IOException, ExpressionException, OptimizationException {
	Element optProblemXML = getOptProblemDescriptionXML(os);
	org.vcell.optimization.OptSolverCallbacks callbacks = new org.vcell.optimization.OptSolverCallbacks(){
		
		public void addEvaluation(double[] paramValues,	double objectiveFuncValue) {
			optSolverCallbacks.addEvaluation(paramValues, objectiveFuncValue);
		}

		public Evaluation getBestEvaluation() {
			return null;
		}

		public long getEvaluationCount() {
			return optSolverCallbacks.getEvaluationCount();
		}

		public boolean getStopRequested() {
			return optSolverCallbacks.getStopRequested();
		}

		public void setStopRequested(boolean stopRequested) {
			optSolverCallbacks.setStopRequested(stopRequested);
		}
		
	};
	NativeOptSolver newNativeOptSolver = new NativeOptSolver();
	try {		
		String inputXML = XmlUtil.xmlToString(optProblemXML);
//		PrintWriter pw = new PrintWriter("c:\\test11.xml");
//		pw.println(inputXML);
//		pw.close();
		String optResultsXML = newNativeOptSolver.nativeSolve_CFSQP(inputXML, callbacks);		
		OptXmlReader optXMLReader = new OptXmlReader();
		org.vcell.optimization.OptimizationResultSet newOptResultSet = optXMLReader.getOptimizationResultSet(optResultsXML);
		OptimizationStatus optimizationStatus = new OptimizationStatus(
				newOptResultSet.getOptimizationStatus().getStatusCode(),
				newOptResultSet.getOptimizationStatus().getStatusString());
		ODESolverResultSet odeSolverResultSet = null;
		if (os.getObjectiveFunction() instanceof OdeObjectiveFunction){
			RowColumnResultSet rcResultSet = null;
			OdeObjectiveFunction odeObjFunc = (OdeObjectiveFunction)os.getObjectiveFunction();
			Element objFuncElement = optProblemXML.getChild(OptXmlTags.ObjectiveFunction_Tag);
			Element modelElement = objFuncElement.getChild(OptXmlTags.Model_Tag);
			String modelType = modelElement.getAttributeValue(OptXmlTags.ModelType_Attr);
			String modelInput = modelElement.getText();
			if (modelType.equals(OptXmlTags.ModelType_Attr_IDA)){
				NativeIDASolver nativeIDASolver = new NativeIDASolver();
				rcResultSet = nativeIDASolver.solve(modelInput,newOptResultSet.getParameterValues());
			}else if (modelType.equals(OptXmlTags.ModelType_Attr_CVODE)){
				NativeCVODESolver nativeCVODESolver = new NativeCVODESolver();
				rcResultSet = nativeCVODESolver.solve(modelInput,newOptResultSet.getParameterValues());
			}
			MathDescription mathDesc = odeObjFunc.getMathDescription();
			Simulation sim = new Simulation(mathDesc);
			SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(sim, 0);
			MathOverrides mathOverrides = sim.getMathOverrides();
			String[] parameterNames = newOptResultSet.getParameterNames();
			double[] parameterValues = newOptResultSet.getParameterValues();
			for (int i = 0; i < parameterValues.length; i++) {
				mathOverrides.putConstant(new Constant(parameterNames[i],new Expression(parameterValues[i])));
			}
			odeSolverResultSet = getOdeSolverResultSet(rcResultSet, simSymbolTable, parameterNames, parameterValues);
		}
		OptimizationResultSet optResultSet = new OptimizationResultSet(
				newOptResultSet.getParameterNames(),
				newOptResultSet.getParameterValues(),
				new Double(newOptResultSet.getObjectiveFunctionValue()),
				newOptResultSet.getObjFunctionEvaluations(),
				odeSolverResultSet,
				optimizationStatus);
		return optResultSet;
		
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new OptimizationException(e.getMessage());
	}
}
	
public static ODESolverResultSet getOdeSolverResultSet(RowColumnResultSet rcResultSet, SimulationSymbolTable simSymbolTable, String[] parameterNames, double[] parameterValues){
	//
	// get simulation results - copy from RowColumnResultSet into OdeSolverResultSet
	//
	
	ODESolverResultSet odeSolverResultSet = new ODESolverResultSet();
	for (int i = 0; i < rcResultSet.getDataColumnCount(); i++){
		odeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(rcResultSet.getColumnDescriptions(i).getName()));
	}
	for (int i = 0; i < rcResultSet.getRowCount(); i++){
		odeSolverResultSet.addRow(rcResultSet.getRow(i));
	}
	//
	// add appropriate Function columns to result set
	//
	Function functions[] = simSymbolTable.getFunctions();
	for (int i = 0; i < functions.length; i++){
		if (cbit.vcell.solver.SimulationSymbolTable.isFunctionSaved(functions[i])){
			Expression exp1 = new Expression(functions[i].getExpression());
			try {
				exp1 = simSymbolTable.substituteFunctions(exp1).flatten();
				//
				// substitute in place all "optimization parameter" values.
				//
				for (int j = 0; parameterNames!=null && j < parameterNames.length; j++) {
					exp1.substituteInPlace(new Expression(parameterNames[j]), new Expression(parameterValues[j]));
				}
			} catch (MathException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
			} catch (ExpressionException e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Substitute function failed on function "+functions[i].getName()+" "+e.getMessage());
			}
			
			try {
				FunctionColumnDescription cd = new FunctionColumnDescription(exp1.flatten(),functions[i].getName(), null, functions[i].getName(), false);
				odeSolverResultSet.addFunctionColumn(cd);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
			}
		}
	}
	return odeSolverResultSet;
}

public String getOptProblemXMLString(OptimizationSpec optimizationSpec){
	return XmlUtil.xmlToString(getOptProblemDescriptionXML(optimizationSpec));
}
	
public static Element getOptProblemDescriptionXML(OptimizationSpec optimizationSpec){
	Element optProblemDescriptionElement = new Element(OptXmlTags.OptProblemDescription_Tag);
	optProblemDescriptionElement.addContent(getParameterDescriptionXML(optimizationSpec));
	optProblemDescriptionElement.addContent(getConstraintDescriptionXML(optimizationSpec));
	optProblemDescriptionElement.addContent(getObjectiveFunctionXML(optimizationSpec));
	return optProblemDescriptionElement;
}

public static Element getParameterDescriptionXML(OptimizationSpec optimizationSpec){
	Element parameterDescriptionElement = new Element(OptXmlTags.ParameterDescription_Tag);
	Parameter[] parameters = optimizationSpec.getParameters();
	for (int i = 0; i < parameters.length; i++) {
		Element parameterElement = new Element(OptXmlTags.Parameter_Tag);
		parameterElement.setAttribute(OptXmlTags.ParameterName_Attr, parameters[i].getName());
		parameterElement.setAttribute(OptXmlTags.ParameterLow_Attr, Double.toString(parameters[i].getLowerBound()));
		parameterElement.setAttribute(OptXmlTags.ParameterHigh_Attr, Double.toString(parameters[i].getUpperBound()));
		parameterElement.setAttribute(OptXmlTags.ParameterInit_Attr, Double.toString(parameters[i].getInitialGuess()));
		parameterElement.setAttribute(OptXmlTags.ParameterScale_Attr, Double.toString(parameters[i].getScale()));
		parameterDescriptionElement.addContent(parameterElement);
	}
	return parameterDescriptionElement;
}


public static Element getConstraintDescriptionXML(OptimizationSpec optimizationSpec){
	Element constraintDescriptionElement = new Element(OptXmlTags.ConstraintDescription_Tag);
	Constraint[] constraints = optimizationSpec.getConstraints();
	for (int i = 0; i < constraints.length; i++) {
		Element constraintElement = new Element(OptXmlTags.Constraint_Tag);
		if (constraints[i].getConstraintType().equals(ConstraintType.LinearEquality)){
			constraintElement.setAttribute(OptXmlTags.ConstraintType_Attr, OptXmlTags.ConstraintType_Attr_LinearEquality);
		}else if (constraints[i].getConstraintType().equals(ConstraintType.LinearInequality)){
			constraintElement.setAttribute(OptXmlTags.ConstraintType_Attr, OptXmlTags.ConstraintType_Attr_LinearInequality);
		}else if (constraints[i].getConstraintType().equals(ConstraintType.NonlinearEquality)){
			constraintElement.setAttribute(OptXmlTags.ConstraintType_Attr, OptXmlTags.ConstraintType_Attr_NonlinearEquality);
		}else if (constraints[i].getConstraintType().equals(ConstraintType.NonlinearInequality)){
			constraintElement.setAttribute(OptXmlTags.ConstraintType_Attr, OptXmlTags.ConstraintType_Attr_NonlinearInequality);
		}else {
			throw new RuntimeException("unsupported constraint type "+constraints[i].getConstraintType());
		}
		constraintElement.addContent(constraints[i].getExpression().infix());
		constraintDescriptionElement.addContent(constraintElement);
	}
	return constraintDescriptionElement;
}

public static Element getObjectiveFunctionXML(OptimizationSpec optimizationSpec) {
	Element objectiveFunctionElement = new Element(OptXmlTags.ObjectiveFunction_Tag);
	ObjectiveFunction objectiveFunction = optimizationSpec.getObjectiveFunction();
	
	if (objectiveFunction instanceof ExplicitObjectiveFunction){
		
		ExplicitObjectiveFunction explicitObjectiveFunction = (ExplicitObjectiveFunction)objectiveFunction;
		objectiveFunctionElement.setAttribute(OptXmlTags.ObjectiveFunctionType_Attr,OptXmlTags.ObjectiveFunctionType_Attr_Explicit);
		Element expressionElement = new Element(OptXmlTags.Expression_Tag);
		expressionElement.addContent(explicitObjectiveFunction.getExpression().infix());		
		objectiveFunctionElement.addContent(expressionElement);
		
	}else if (objectiveFunction instanceof ExplicitFitObjectiveFunction){
		
		ExplicitFitObjectiveFunction explicitFitObjectiveFunction = (ExplicitFitObjectiveFunction)objectiveFunction;
		objectiveFunctionElement.setAttribute(OptXmlTags.ObjectiveFunctionType_Attr,OptXmlTags.ObjectiveFunctionType_Attr_ExplicitFit);
		Element expressionElement = new Element(OptXmlTags.Expression_Tag);
		expressionElement.addContent(explicitFitObjectiveFunction.getFunctionExpression().infix());
		objectiveFunctionElement.addContent(expressionElement);
		Element dataElement = getDataXML(explicitFitObjectiveFunction.getReferenceData());
		objectiveFunctionElement.addContent(dataElement);

	}else if (objectiveFunction instanceof OdeObjectiveFunction){
		
		OdeObjectiveFunction odeObjectiveFunction = (OdeObjectiveFunction)objectiveFunction;
		objectiveFunctionElement.setAttribute(OptXmlTags.ObjectiveFunctionType_Attr,OptXmlTags.ObjectiveFunctionType_Attr_ODE);
		ReferenceData refData = odeObjectiveFunction.getReferenceData();

		Element dataElement = getDataXML(refData);
		objectiveFunctionElement.addContent(dataElement);

		Element modelElement = getModelXML(odeObjectiveFunction, optimizationSpec.getParameterNames());
		objectiveFunctionElement.addContent(modelElement);
		
		Simulation tempSim = new Simulation(odeObjectiveFunction.getMathDescription());
		SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(tempSim, 0);
		//
		// write data/model variable mappings
		//
		for (int i = 1; i < refData.getNumColumns(); i ++) {
			Element modelMappingElement = new Element(OptXmlTags.ModelMapping_Tag);
			modelMappingElement.setAttribute(OptXmlTags.ModelMappingDataColumn_Attr,refData.getColumnNames()[i]);
			modelMappingElement.setAttribute(OptXmlTags.ModelMappingWeight_Attr,String.valueOf(refData.getColumnWeights()[i]));

			Expression mappingExpression = null;
			try {
				cbit.vcell.math.Variable var = odeObjectiveFunction.getMathDescription().getVariable(refData.getColumnNames()[i]);
				if (var instanceof cbit.vcell.math.Function) {
					Expression exp = new Expression(var.getExpression());
					exp.bindExpression(simSymbolTable);
					exp = MathUtilities.substituteFunctions(exp, simSymbolTable);
					mappingExpression = exp.flatten();
				} else {
					mappingExpression = new Expression(var.getName());
				}
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new OptimizationException(e.getMessage());
			}
			modelMappingElement.addContent(mappingExpression.infix());
			objectiveFunctionElement.addContent(modelMappingElement);
		}	
	} else if (objectiveFunction instanceof PdeObjectiveFunction){
		
		PdeObjectiveFunction pdeObjectiveFunction = (PdeObjectiveFunction)objectiveFunction;
		objectiveFunctionElement.setAttribute(OptXmlTags.ObjectiveFunctionType_Attr,OptXmlTags.ObjectiveFunctionType_Attr_PDE);
		SpatialReferenceData refData = pdeObjectiveFunction.getReferenceData();

		Element dataElement = getDataXML(refData);
		objectiveFunctionElement.addContent(dataElement);

		Element modelElement = getModelXML(pdeObjectiveFunction, optimizationSpec.getParameterNames());
		objectiveFunctionElement.addContent(modelElement);
		
		Simulation tempSim = new Simulation(pdeObjectiveFunction.getMathDescription());
		SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(tempSim, 0); 
		//
		// write data/model variable mappings
		//
		for (int i = 1; i < refData.getNumVariables(); i ++) {
			Element modelMappingElement = new Element(OptXmlTags.ModelMapping_Tag);
			modelMappingElement.setAttribute(OptXmlTags.ModelMappingDataColumn_Attr,refData.getVariableNames()[i]);
			modelMappingElement.setAttribute(OptXmlTags.ModelMappingWeight_Attr,String.valueOf(refData.getVariableWeights()[i]));

			Expression mappingExpression = null;
			try {
				Variable var = pdeObjectiveFunction.getMathDescription().getVariable(refData.getVariableNames()[i]);
				if (var instanceof Function) {
					Expression exp = new Expression(var.getExpression());
					exp.bindExpression(simSymbolTable);
					exp = MathUtilities.substituteFunctions(exp, simSymbolTable);
					mappingExpression = exp.flatten();
				} else {
					mappingExpression = new Expression(var.getName());
				}
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new OptimizationException(e.getMessage());
			}
			modelMappingElement.addContent(mappingExpression.infix());
			objectiveFunctionElement.addContent(modelMappingElement);
		}
	}
	return objectiveFunctionElement;
}

public static Element getDataXML(ReferenceData refData){	
	Element dataElement = new Element(OptXmlTags.Data_Tag);

	//
	// write variable declarations
	//
	Element timeVarElement = new Element(OptXmlTags.Variable_Tag);
	timeVarElement.setAttribute(OptXmlTags.VariableType_Attr,OptXmlTags.VariableType_Attr_Independent);
	timeVarElement.setAttribute(OptXmlTags.VariableName_Attr,ReservedSymbol.TIME.getName());
	timeVarElement.setAttribute(OptXmlTags.VariableDimension_Attr,"1");
	dataElement.addContent(timeVarElement);
	
	int timeIndex = refData.findColumn(ReservedSymbol.TIME.getName());
	if (timeIndex != 0) {
		throw new RuntimeException("t must be the first column");
	}
	for (int i = 1; i < refData.getNumColumns(); i++) {
		Element variableElement = new Element(OptXmlTags.Variable_Tag);
		variableElement.setAttribute(OptXmlTags.VariableType_Attr,OptXmlTags.VariableType_Attr_Dependent);
		variableElement.setAttribute(OptXmlTags.VariableName_Attr,refData.getColumnNames()[i]);
		variableElement.setAttribute(OptXmlTags.VariableDimension_Attr,refData.getDataSize() + "");
		dataElement.addContent(variableElement);
	}
	//
	// write data
	//
	for (int i = 0; i < refData.getNumRows(); i ++) {
		double[] data = refData.getRowData(i);
		Element rowElement = new Element(OptXmlTags.Row_Tag);
		StringBuffer rowText = new StringBuffer();
		for (int j = 0; j < data.length; j++) {
			rowText.append(data[j]+" ");
		}
		rowElement.addContent(rowText.toString());
		dataElement.addContent(rowElement);
	}
	
	return dataElement;
}

public static Element getModelXML(OdeObjectiveFunction odeObjectiveFunction, String[] parameterNames){
	Element modelElement = new Element(OptXmlTags.Model_Tag);

	ReferenceData refData = odeObjectiveFunction.getReferenceData();
	double refDataEndTime = refData.getColumnData(0)[refData.getNumRows()-1];

	//
	// post the problem either as an IDA or CVODE model
	//
	org.vcell.util.document.SimulationVersion simVersion = new org.vcell.util.document.SimulationVersion(
			new KeyValue("12345"),
			"name",
			new org.vcell.util.document.User("user",new KeyValue("123")),
			new org.vcell.util.document.GroupAccessNone(),
			null, // versionBranchPointRef
			new java.math.BigDecimal(1.0), // branchID
			new java.util.Date(),
			org.vcell.util.document.VersionFlag.Archived,
			"",
			null);
	Simulation simulation = new Simulation(simVersion,odeObjectiveFunction.getMathDescription());
	int timeIndex = refData.findColumn("t");
	if (timeIndex>=0){
		double[] refTimeData = refData.getColumnData(timeIndex);
		OutputTimeSpec outputTimeSpec = new ExplicitOutputTimeSpec(refTimeData);
		try {
			simulation.getSolverTaskDescription().setOutputTimeSpec(outputTimeSpec);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			throw new OptimizationException("failed to set output times for simulation: "+e.getMessage());
		}
	}

	if (odeObjectiveFunction.getMathDescription().hasFastSystems()){
		//
		// has fast systems, must use IDA
		//
		try {
			simulation.getSolverTaskDescription().setTimeBounds(new TimeBounds(0.0, refDataEndTime));
			simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.IDA);
			StringWriter simulationInputStringWriter = new StringWriter();
			IDAFileWriter idaFileWriter = new IDAFileWriter(new PrintWriter(simulationInputStringWriter,true), new SimulationJob(simulation, 0, null));
			idaFileWriter.write(parameterNames);
			simulationInputStringWriter.close();
			modelElement.setAttribute(OptXmlTags.ModelType_Attr,OptXmlTags.ModelType_Attr_IDA);
			CDATA simulationInputText = new CDATA(simulationInputStringWriter.getBuffer().toString());
			modelElement.addContent(simulationInputText);
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new OptimizationException("failed to create IDA input file: "+e.getMessage());
		}
	}else{
		//
		// no fast systems, use CVODE
		//
		try {
			simulation.getSolverTaskDescription().setTimeBounds(new TimeBounds(0.0, refDataEndTime));
			simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.CVODE);
			StringWriter simulationInputStringWriter = new StringWriter();
			CVodeFileWriter cvodeFileWriter = new CVodeFileWriter(new PrintWriter(simulationInputStringWriter,true), new SimulationJob(simulation, 0, null));
			cvodeFileWriter.write(parameterNames);
			simulationInputStringWriter.close();
			modelElement.setAttribute(OptXmlTags.ModelType_Attr,OptXmlTags.ModelType_Attr_CVODE);
			CDATA simulationInputText = new CDATA(simulationInputStringWriter.getBuffer().toString());
			modelElement.addContent(simulationInputText);
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new OptimizationException("failed to create CVODE input file: "+e.getMessage());
		}
	}
	return modelElement;
}

public static Element getModelXML(PdeObjectiveFunction pdeObjectiveFunction, String[] parameterNames){
	Element modelElement = new Element(OptXmlTags.Model_Tag);
	try {
		SpatialReferenceData refData = pdeObjectiveFunction.getReferenceData();
		double refDataEndTime = refData.getRowData(refData.getNumRows()-1)[0];

		//
		// post the problem either as an IDA or CVODE model
		//
		org.vcell.util.document.SimulationVersion simVersion = new org.vcell.util.document.SimulationVersion(
				new KeyValue("12345"),
				"name",
				new org.vcell.util.document.User("user",new KeyValue("123")),
				new org.vcell.util.document.GroupAccessNone(),
				null, // versionBranchPointRef
				new java.math.BigDecimal(1.0), // branchID
				new java.util.Date(),
				org.vcell.util.document.VersionFlag.Archived,
				"",
				null);
		Simulation simulation = new Simulation(simVersion,pdeObjectiveFunction.getMathDescription());
		simulation.getMeshSpecification().setSamplingSize(refData.getDataISize());	

		double[] times = refData.getColumnData(0);
		double minDt = Double.POSITIVE_INFINITY;
		for (int i = 1; i < times.length; i ++) {
			minDt = Math.min(minDt, times[i] - times[i-1]);
		}
		simulation.getSolverTaskDescription().setTimeBounds(new cbit.vcell.solver.TimeBounds(0.0, refDataEndTime));
		simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.FiniteVolume);
		simulation.getSolverTaskDescription().setTimeStep(new TimeStep(minDt/5, minDt/5, minDt/5));
		
		// clone and resample geometry
		Geometry resampledGeometry = null;
		try {
			resampledGeometry = (Geometry) BeanUtils.cloneSerializable(simulation.getMathDescription().getGeometry());
			GeometrySurfaceDescription geoSurfaceDesc = resampledGeometry.getGeometrySurfaceDescription();
			ISize newSize = simulation.getMeshSpecification().getSamplingSize();
			geoSurfaceDesc.setVolumeSampleSize(newSize);
			geoSurfaceDesc.updateAll();		
		} catch (Exception e) {
			e.printStackTrace();
			throw new SolverException(e.getMessage());
		}	
		SimulationJob simJob = new SimulationJob(simulation, 0, pdeObjectiveFunction.getFieldDataIDSs());
		
		StringWriter simulationInputStringWriter = new StringWriter();
		FiniteVolumeFileWriter fvFileWriter = new FiniteVolumeFileWriter(new PrintWriter(simulationInputStringWriter,true), simJob, resampledGeometry, pdeObjectiveFunction.getWorkingDirectory());		
		fvFileWriter.write(parameterNames);
		simulationInputStringWriter.close();
		modelElement.setAttribute(OptXmlTags.ModelType_Attr,OptXmlTags.ModelType_Attr_FVSOLVER);
		CDATA simulationInputText = new CDATA(simulationInputStringWriter.getBuffer().toString());
		modelElement.addContent(simulationInputText);
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new OptimizationException("failed to create fv input file: "+e.getMessage());
	}
	return modelElement;
}

}