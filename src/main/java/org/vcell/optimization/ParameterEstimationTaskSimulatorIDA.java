package org.vcell.optimization;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import cbit.vcell.math.Constant;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solvers.NativeIDASolver;

public class ParameterEstimationTaskSimulatorIDA {
	
	public RowColumnResultSet getRowColumnRestultSetByBestEstimations(ParameterEstimationTask parameterEstimationTask, String[] paramNames, double[] paramValues) throws Exception
	{
		//create a temp simulation based on math description
		Simulation simulation = new Simulation(parameterEstimationTask.getSimulationContext().getMathDescription());
		
		ReferenceData refData = parameterEstimationTask.getModelOptimizationSpec().getReferenceData();
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

	public ODESolverResultSet getOdeSolverResultSet(ParameterEstimationTask parameterEstimationTask) throws Exception {
		return getOdeSolverResultSet(parameterEstimationTask, parameterEstimationTask.getModelOptimizationMapping().getOptimizationSpec(),parameterEstimationTask.getOptimizationResultSet());
	}

	private ODESolverResultSet getOdeSolverResultSet(ParameterEstimationTask parameterEstimationTask, OptimizationSpec optSpec, OptimizationResultSet optResultSet) throws Exception {
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
			RowColumnResultSet rcResultSet = getRowColumnRestultSetByBestEstimations(parameterEstimationTask,parameterNames, bestEstimates);
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
			MathDescription mathDesc = parameterEstimationTask.getSimulationContext().getMathDescription();
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
}