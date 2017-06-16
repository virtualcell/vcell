package cbit.vcell.modelopt;

import org.vcell.optimization.CopasiOptSolverCallbacks;
import org.vcell.optimization.CopasiOptimizationSolver;
import org.vcell.optimization.ParameterEstimationTaskSimulatorIDA;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.MathDescription;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.resource.OperatingSystemInfo;

public class OptimizationService {

	private static MathMappingCallback callback = new MathMappingCallback() {
		@Override
		public void setProgressFraction(float fractionDone) {
			System.out.println("---> stdout mathMapping: progress = "+(fractionDone*100.0)+"% done");
		}
		
		@Override
		public void setMessage(String message) {
			System.out.println("---> stdout mathMapping: message = "+message);
		}
		
		@Override
		public boolean isInterrupted() {
			return false;
		}
	};
	
	private static CopasiOptSolverCallbacks copasiOptCallbacks = new CopasiOptSolverCallbacks();

	public static OptimizationResultSet optimize(ParameterEstimationTask parameterEstimationTask) throws Exception {
		
		if (OperatingSystemInfo.getInstance().isMac()){
			throw new RuntimeException("parameter estimation not currently available on Mac\n\n   try Windows or Linux.\n\n   coming soon on Mac.");
		}
		copasiOptCallbacks.reset();

		updateMath(parameterEstimationTask.getSimulationContext(), NetworkGenerationRequirements.ComputeFullStandardTimeout);
		
		StringBuffer issueText = new StringBuffer();				
		java.util.Vector<Issue> issueList = new java.util.Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		parameterEstimationTask.gatherIssues(issueContext,issueList);
		boolean bFailed = false;
		for (int i = 0; i < issueList.size(); i++){
			Issue issue = (Issue)issueList.elementAt(i);
			issueText.append(issue.getMessage()+"\n");
			if (issue.getSeverity() == Issue.SEVERITY_ERROR){
				bFailed = true;
				break;
			}
		}
		if (bFailed){
			throw new OptimizationException(issueText.toString());
		}
		parameterEstimationTask.refreshMappings();

		OptimizationResultSet optResultSet = CopasiOptimizationSolver.solve(new ParameterEstimationTaskSimulatorIDA(),parameterEstimationTask,copasiOptCallbacks,callback);
		return optResultSet;

	}

	private static void updateMath(SimulationContext simulationContext, final NetworkGenerationRequirements networkGenerationRequirements) throws Exception {
		Geometry geometry = simulationContext.getGeometry();
		if (geometry.getDimension()>0 && geometry.getGeometrySurfaceDescription().getGeometricRegions()==null){
			geometry.getGeometrySurfaceDescription().updateAll();
		}

		simulationContext.checkValidity();

		MathMapping mathMapping = simulationContext.createNewMathMapping(callback, networkGenerationRequirements);
		MathDescription mathDesc = mathMapping.getMathDescription(callback);
		callback.setProgressFraction(1.0f/3.0f*2.0f);
		if (mathDesc != null) {
			simulationContext.setMathDescription(mathDesc);
		}
	}

}
