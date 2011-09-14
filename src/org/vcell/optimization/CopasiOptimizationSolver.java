/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.jdom.Element;
import org.vcell.util.FileUtils;

import cbit.util.xml.XmlUtil;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.MathException;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.ResourceUtil;
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


public class CopasiOptimizationSolver {	
	static {
		ResourceUtil.loadCopasiSolverLibrary();
	}
	private static final String DataType_int = "int";
	private static final String DataType_float = "float";
	private static final String Label_Progress = "Progress";
	
	public enum CopasiOptimizationParameterType {
		Number_of_Generations("Number of Generations", DataType_int),
		Number_of_Iterations("Number of Iterations", DataType_int),
		Population_Size("Population Size", DataType_int),
		Random_Number_Generator("Random Number Generator", DataType_int),
		Seed("Seed", DataType_int),
		IterationLimit("Iteration Limit", DataType_int),
		Tolerance("Tolerance", DataType_float),
		Rho("Rho", DataType_float),
		Scale("Scale", DataType_float),
		Swarm_Size("Swarm Size", DataType_int),
		Std_Deviation("Std Deviation", DataType_float),
		Start_Temperature("Start Temperature", DataType_float),
		Cooling_Factor("Cooling Factor", DataType_float),
		Pf("Pf", DataType_float);
		
		private String displayName;
		private String dataType;
		CopasiOptimizationParameterType(String displayName, String dataType) {
			this.displayName = displayName;
			this.dataType = dataType;
		}
		public final String getDisplayName() {
			return displayName;
		}
		public final String getDataType(){
			return dataType;
		}
	}
	
	public static class CopasiOptimizationParameter {
		private CopasiOptimizationParameterType type;
		private double value;
		
		CopasiOptimizationParameter(CopasiOptimizationParameterType type, double dv) {
			this.type = type;
			value = dv;
		}

		CopasiOptimizationParameter(CopasiOptimizationParameter anotherParameter) {
			this.type = anotherParameter.type;
			value = anotherParameter.value;
		}
		
		public final double getValue() {
			return value;
		}

		public final void setValue(double value) {
			this.value = value;
		}

		public final CopasiOptimizationParameterType getType() {
			return type;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CopasiOptimizationParameter) {
				CopasiOptimizationParameter cop = (CopasiOptimizationParameter)obj;
				if (cop.type != type) {
					return false;
				}
				if (cop.value != value) {
					return false;
				}
				return true;
			}
			return false;
		}
	}
	
	public static class CopasiOptimizationMethod {
		private CopasiOptimizationMethodType type;
		private CopasiOptimizationParameter[] realParameters;
		
		public CopasiOptimizationMethod(CopasiOptimizationMethodType type) {
			this.type = type;
			CopasiOptimizationParameter[] defaultParameters = type.getDefaultParameters();
			this.realParameters = new CopasiOptimizationParameter[defaultParameters.length];
			for (int i = 0; i < defaultParameters.length; i ++) {
				realParameters[i] = new CopasiOptimizationParameter(defaultParameters[i]);
			}
		}
		public final CopasiOptimizationMethodType getType() {
			return type;
		}
		public final CopasiOptimizationParameter[] getParameters() {
			return realParameters;
		}
		public final Double getEndValue() {
			for (CopasiOptimizationParameter cop : realParameters) {
				if (cop.getType() == CopasiOptimizationParameterType.Number_of_Generations
						|| cop.getType() == CopasiOptimizationParameterType.IterationLimit)
				return new Double(cop.getValue());
			}
			return null;
		}
				
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CopasiOptimizationMethod) {
				CopasiOptimizationMethod com = (CopasiOptimizationMethod)obj;
				if (com.type != type) {
					return false;
				}
				if (realParameters.length != com.realParameters.length) {
					return false;
				}
				for (int i = 0; i < realParameters.length; i ++) {
					if (!realParameters[i].equals(com.realParameters[i])) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
	}
	
	public enum CopasiOptimizationMethodType {
		Statistics("Current Solution Statistics", new CopasiOptimizationParameter[0], null, CopasiOptProgressType.NO_Progress),
		EvolutionaryProgram("Evolutionary Programming", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0)},
				Label_Progress,
				CopasiOptProgressType.Progress
		),
		SRES("Evolution Strategy (SRES)", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Pf, 0.475),},
				Label_Progress,
				CopasiOptProgressType.Progress
		),
		GeneticAlgorithm("Genetic Algorithm", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),},
				Label_Progress,
				CopasiOptProgressType.Progress
		),
		GeneticAlgorithmSR("Genetic Algorithm SR", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Generations, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Population_Size, 20),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Pf, 0.475),},
				Label_Progress,
				CopasiOptProgressType.Progress
		),
		HookeJeeves("Hooke & Jeeves", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 50),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Rho, 0.2),},
				null,
				CopasiOptProgressType.NO_Progress
		),
		LevenbergMarquardt("Levenberg - Marquardt", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),},
				null,
				CopasiOptProgressType.NO_Progress
		),
		NelderMead("Nelder - Mead", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 200),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Scale, 10),},
				null,
				CopasiOptProgressType.NO_Progress
		),
		ParticleSwarm("Particle Swarm", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 2000),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Swarm_Size, 50),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Std_Deviation, 1e-6),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),},
				Label_Progress,
				CopasiOptProgressType.Progress),
	    RandomSearch("Random Search", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Number_of_Iterations, 100000),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),},
				null,
				CopasiOptProgressType.NO_Progress
		),
	    SimulatedAnnealing("Simulated Annealing", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Start_Temperature, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Cooling_Factor, 0.85),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-6),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Random_Number_Generator, 1),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Seed, 0),},
				"Current Temperature",
				CopasiOptProgressType.Current_Value
		),
	    SteepestDescent("Steepest Descent", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.IterationLimit, 100),
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-6),},
				null,
				CopasiOptProgressType.NO_Progress
		),
	    Praxis("Praxis", new CopasiOptimizationParameter[]{
				new CopasiOptimizationParameter(CopasiOptimizationParameterType.Tolerance, 1e-5),},
				null,
				CopasiOptProgressType.NO_Progress
		),
	    TruncatedNewton("Truncated Newton", new CopasiOptimizationParameter[0],
				null,
				CopasiOptProgressType.NO_Progress
		);
		
		private String name;
		private String displayName;
		private CopasiOptimizationParameter[] defaultParameters;
		private String progressLabel;
		private CopasiOptProgressType progressType;
		
		CopasiOptimizationMethodType(String name, CopasiOptimizationParameter[] parameters, String progressLabel, CopasiOptProgressType progressType) {
			this.name = name;
			displayName = name;
			this.defaultParameters = parameters;
			this.progressLabel = progressLabel;
			this.progressType = progressType;
		}
		public final String getName() {
			return name;
		}
		public final String getDisplayName() {
			return displayName;
		}
		public final CopasiOptimizationParameter[] getDefaultParameters() {
			return defaultParameters;
		}
		public String getProgressLabel() {
			return progressLabel;
		}
		public CopasiOptProgressType getProgressType() {
			return progressType;
		}
		 
	}
	
	public enum CopasiOptProgressType {
		NO_Progress,
		Progress,
		Current_Value
	}
	
	private static native String solve(String optProblemXml, CopasiOptSolverCallbacks optSolverCallbacks);
	
public static OptimizationResultSet solve(ParameterEstimationTask parameterEstimationTask) 
						throws IOException, ExpressionException, OptimizationException {
	CopasiOptSolverCallbacks optSolverCallbacks = parameterEstimationTask.getOptSolverCallbacks();
	try {		
		Element optProblemXML = OptXmlWriter.getCoapsiOptProblemDescriptionXML(parameterEstimationTask);
		String inputXML = XmlUtil.xmlToString(optProblemXML);
		System.out.println(inputXML);
		String optResultsXML = solve(inputXML, optSolverCallbacks);
		OptSolverResultSet newOptResultSet = OptXmlReader.getOptimizationResultSet(optResultsXML);
		//create a temp simulation based on math description
		Simulation simulation = new Simulation(parameterEstimationTask.getSimulationContext().getMathDescription());
		
		String[] parameterNames = newOptResultSet.getParameterNames();
		double[] parameterVals = newOptResultSet.getBestEstimates();
		ReferenceData refData = parameterEstimationTask.getModelOptimizationSpec().getReferenceData();
		double[] times = refData.getDataByColumn(0);
		double endTime = times[times.length-1];
		ExplicitOutputTimeSpec exTimeSpec = new ExplicitOutputTimeSpec(times);
		//set simulation ending time and output interval
		simulation.getSolverTaskDescription().setTimeBounds(new TimeBounds(0, endTime));
		simulation.getSolverTaskDescription().setOutputTimeSpec(exTimeSpec);
		//set parameters as math overrides
		MathOverrides mathOverrides = simulation.getMathOverrides();
		for (int i = 0; i < parameterNames.length; i++){
			mathOverrides.putConstant(new Constant(parameterNames[i],new Expression(parameterVals[i])));
		}
		//get input model string
		StringWriter stringWriter = new StringWriter();
		IDAFileWriter idaFileWriter = new IDAFileWriter(new PrintWriter(stringWriter,true), new SimulationJob(simulation, 0, null));
		idaFileWriter.write();
		stringWriter.close();
		StringBuffer buffer = stringWriter.getBuffer();
		String idaInputString = buffer.toString();
		
		RowColumnResultSet rcResultSet = null;
		NativeIDASolver nativeIDASolver = new NativeIDASolver();
		rcResultSet = nativeIDASolver.solve(idaInputString);
		
		OptimizationResultSet optResultSet = new OptimizationResultSet(newOptResultSet, rcResultSet);
		return optResultSet;
	} catch (Throwable e){
		e.printStackTrace(System.out);
		throw new OptimizationException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());	
	}
}
	
private static ODESolverResultSet getOdeSolverResultSet(RowColumnResultSet rcResultSet, SimulationSymbolTable simSymbolTable, String[] parameterNames, double[] parameterValues){
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
		if (SimulationSymbolTable.isFunctionSaved(functions[i])){
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

public static void main(String[] args) {
	try{
		String fileName = "D:\\COPASI\\copasiOptXml.txt";
		String optXML = FileUtils.readFileToString(new File(fileName));
		System.out.println(optXML);
		CopasiOptSolverCallbacks coc = new CopasiOptSolverCallbacks();
		solve(optXML, coc);
	}catch(Throwable t)
	{
		t.printStackTrace(System.err);
	}
}
}
