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
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.vcell.api.client.VCellApiClient;
import org.vcell.optimization.thrift.OptParameterValue;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptResultSet;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.optimization.thrift.OptRunStatus;
import org.vcell.util.UserCancelException;

import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.math.Function;
import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.opt.OptSolverResultSet.OptRunResultSet;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationStatus;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.ode.ODESolverResultSet;


public class CopasiOptimizationSolver {	
	
	public static OptimizationResultSet solveLocalPython(ParameterEstimationTaskSimulatorIDA parestSimulator, ParameterEstimationTask parameterEstimationTask, CopasiOptSolverCallbacks optSolverCallbacks, MathMappingCallback mathMappingCallback) 
							throws IOException, ExpressionException, OptimizationException {
		
		File dir = Files.createTempDirectory("parest",new FileAttribute<?>[] {}).toFile();
		try {
			String prefix = "testing_"+Math.abs(new Random().nextInt(10000));
			
			File optProblemThriftFile = new File(dir,prefix+".optprob.bin");
			File optRunFile = new File(dir,prefix+".optrun.bin");
			
			//
			// Setup Python COPASI opt problem and write to disk
			//
			OptProblem optProblem = CopasiServicePython.makeOptProblem(parameterEstimationTask);
			CopasiServicePython.writeOptProblem(optProblemThriftFile, optProblem);

			//
			// run Python COPASI opt problem
			//
			CopasiServicePython.runCopasiPython(optProblemThriftFile, optRunFile);
			if (!optRunFile.exists()){
				throw new RuntimeException("COPASI optimization output file not found:\n"+optRunFile.getAbsolutePath());
			}
			OptRun optRun = CopasiServicePython.readOptRun(optRunFile);
			OptResultSet optResultSet = optRun.getOptResultSet();
			int numFittedParameters = optResultSet.getOptParameterValues().size();
			String[] paramNames = new String[numFittedParameters];
			double[] paramValues = new double[numFittedParameters];
			for (int pIndex=0; pIndex<numFittedParameters; pIndex++){
				OptParameterValue optParamValue = optResultSet.getOptParameterValues().get(pIndex);
				paramNames[pIndex] = optParamValue.parameterName;
				paramValues[pIndex] = optParamValue.bestValue;
			}
			
			OptimizationStatus status = new OptimizationStatus(OptimizationStatus.NORMAL_TERMINATION, optRun.statusMessage);
			OptRunResultSet optRunResultSet = new OptRunResultSet(paramValues,optResultSet.objectiveFunction,optResultSet.numFunctionEvaluations,status);
			OptSolverResultSet copasiOptSolverResultSet = new OptSolverResultSet(paramNames, optRunResultSet);
			RowColumnResultSet copasiRcResultSet = parestSimulator.getRowColumnRestultSetByBestEstimations(parameterEstimationTask, paramNames, paramValues);
			OptimizationResultSet copasiOptimizationResultSet = new OptimizationResultSet(copasiOptSolverResultSet, copasiRcResultSet);

			System.out.println("-----------SOLUTION FROM PYTHON---------------\n"+optResultSet.toString());
			
			
			return copasiOptimizationResultSet;
		} catch (Throwable e){
			e.printStackTrace(System.out);
			throw new OptimizationException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
		} finally {
			if (dir!=null && dir.exists()){
				FileUtils.deleteDirectory(dir);
			}
		}
	}

	private static final String STOP_REQUESTED = "stop requested";
	public static OptimizationResultSet solveRemoteApi(
			ParameterEstimationTaskSimulatorIDA parestSimulator,
			ParameterEstimationTask parameterEstimationTask, 
			CopasiOptSolverCallbacks optSolverCallbacks,
			MathMappingCallback mathMappingCallback) 
					throws IOException, ExpressionException, OptimizationException {

		try {
			OptProblem optProblem = CopasiServicePython.makeOptProblem(parameterEstimationTask);
			
			boolean bIgnoreCertProblems = true;
			boolean bIgnoreHostMismatch = true;
			
			// e.g. vcell.serverhost=vcellapi.cam.uchc.edu:8080
			String serverHost = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerHost);
			String[] parts = serverHost.split(":");
			String host = parts[0];
			int port = Integer.parseInt(parts[1]);
			VCellApiClient apiClient = new VCellApiClient(host, port, bIgnoreCertProblems, bIgnoreHostMismatch);

			TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
			String optProblemJson = serializer.toString(optProblem);

			String optimizationId = apiClient.submitOptimization(optProblemJson);
			
			final long TIMEOUT_MS = 1000*20; // 20 second minute timeout
			long startTime = System.currentTimeMillis();
			OptRun optRun = null;
			while ((System.currentTimeMillis()-startTime)<TIMEOUT_MS){
				if (optSolverCallbacks.getStopRequested()){
					throw new RuntimeException(STOP_REQUESTED);
				}
				String optRunJson = apiClient.getOptRunJson(optimizationId);
				TDeserializer deserializer = new TDeserializer(new TJSONProtocol.Factory());
				optRun = new OptRun();
				deserializer.deserialize(optRun, optRunJson.getBytes());
				OptRunStatus status = optRun.status;
				if (status==OptRunStatus.Complete){
					System.out.println("job "+optimizationId+": status "+status+" "+optRun.getOptResultSet().toString());
					break;
				}
				if (status==OptRunStatus.Failed){
					throw new RuntimeException("optimization failed, message="+optRun.statusMessage);
				}
				
				System.out.println("job "+optimizationId+": status "+status);
				try {
					Thread.sleep(1000);
				}catch (InterruptedException e){}
			}
			System.out.println("done with optimization");
			OptResultSet optResultSet = optRun.getOptResultSet();
			int numFittedParameters = optResultSet.getOptParameterValues().size();
			String[] paramNames = new String[numFittedParameters];
			double[] paramValues = new double[numFittedParameters];
			for (int pIndex = 0; pIndex < numFittedParameters; pIndex++) {
				OptParameterValue optParamValue = optResultSet.getOptParameterValues().get(pIndex);
				paramNames[pIndex] = optParamValue.parameterName;
				paramValues[pIndex] = optParamValue.bestValue;
			}

			OptimizationStatus status = new OptimizationStatus(OptimizationStatus.NORMAL_TERMINATION,optRun.statusMessage);
			OptRunResultSet optRunResultSet = new OptRunResultSet(paramValues, optResultSet.objectiveFunction,optResultSet.numFunctionEvaluations, status);
			OptSolverResultSet copasiOptSolverResultSet = new OptSolverResultSet(paramNames, optRunResultSet);
			RowColumnResultSet copasiRcResultSet = parestSimulator.getRowColumnRestultSetByBestEstimations(parameterEstimationTask, paramNames, paramValues);
			OptimizationResultSet copasiOptimizationResultSet = new OptimizationResultSet(copasiOptSolverResultSet,copasiRcResultSet);

			System.out.println("-----------SOLUTION FROM VCellAPI---------------\n" + optResultSet.toString());

			return copasiOptimizationResultSet;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			if(e.getMessage() != null && e.getMessage().equals(STOP_REQUESTED)){
				throw UserCancelException.CANCEL_GENERIC;
			}
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
	
}
