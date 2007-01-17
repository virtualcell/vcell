package cbit.vcell.modelopt;
import cbit.vcell.mapping.SimulationContext;
/**
 * Insert the type's description here.
 * Creation date: (5/2/2006 11:16:58 PM)
 * @author: Jim Schaff
 */
public class ParameterEstimationTaskTest {
/**
 * Insert the method's description here.
 * Creation date: (5/2/2006 11:17:15 PM)
 * @return cbit.vcell.modelopt.ParameterEstimationTask
 */
public static ParameterEstimationTask getExample() {
	try {
		SimulationContext simContext = cbit.vcell.mapping.SimulationContextTest.getExample(0);
		ParameterEstimationTask parameterEstimationTask = new ParameterEstimationTask(simContext);
		ParameterMappingSpec[] parameterMappingSpecs = parameterEstimationTask.getModelOptimizationSpec().getParameterMappingSpecs();

		parameterMappingSpecs[0].setSelected(true);
		parameterMappingSpecs[1].setSelected(true);

		String dataString = "SimpleReferenceData { 3 2 t Ca_er 1 1 0 1 1 2 2 3 }";
		parameterEstimationTask.getModelOptimizationSpec().setReferenceData(cbit.vcell.opt.SimpleReferenceData.fromVCML(new cbit.vcell.math.CommentStringTokenizer(dataString)));
		
		return parameterEstimationTask;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
}