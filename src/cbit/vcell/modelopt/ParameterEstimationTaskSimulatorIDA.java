package cbit.vcell.modelopt;

import java.io.PrintWriter;
import java.io.StringWriter;

import cbit.vcell.math.Constant;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.modelopt.ParameterEstimationTask.ParameterEstimationTaskSimulator;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solvers.NativeIDASolver;

public class ParameterEstimationTaskSimulatorIDA implements ParameterEstimationTaskSimulator {
	
	@Override
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
}