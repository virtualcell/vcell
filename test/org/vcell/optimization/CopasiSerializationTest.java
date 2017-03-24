package org.vcell.optimization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.vcell.optimization.thrift.CopasiOptimizationMethod;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptimizationMethodType;
import org.vcell.optimization.thrift.OptimizationParameterType;
import org.vcell.optimization.thrift.ParameterDescription;
import org.vcell.optimization.thrift.ReferenceVariable;
import org.vcell.optimization.thrift.ReferenceVariableType;
import org.vcell.sbml.vcell.MathModel_SBMLExporter;

import cbit.util.xml.XmlUtil;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.parser.ExpressionException;

public class CopasiSerializationTest {
	
	public static void writeOptProblem(File optProblemFile,  OptProblem optProblem) throws IOException {
		TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
		try {
			byte[] blob = serializer.serialize(optProblem);
			FileUtils.writeByteArrayToFile(optProblemFile, blob);
		} catch (TException e) {
			e.printStackTrace();
			throw new IOException("error writing optProblem to file "+optProblemFile.getPath()+": "+e.getMessage(),e);
		}
	}
	
	
	public static OptProblem makeOptProblem(ParameterEstimationTask parameterEstimationTask, File outputModelSbmlFile, File outputDataFile) throws IOException, ExpressionException{
		OptimizationSpec optimizationSpec = parameterEstimationTask.getModelOptimizationMapping().getOptimizationSpec();			

		SimulationContext simulationContext = parameterEstimationTask.getSimulationContext();
		MathMappingCallback callback = new MathMappingCallback() {
			@Override
			public void setProgressFraction(float fractionDone) {
				Thread.dumpStack();
				System.out.println("---> stdout mathMapping: progress = "+(fractionDone*100.0)+"% done");
			}
			
			@Override
			public void setMessage(String message) {
				Thread.dumpStack();
				System.out.println("---> stdout mathMapping: message = "+message);
			}
			
			@Override
			public boolean isInterrupted() {
				return false;
			}
		};
		simulationContext.refreshMathDescription(callback,NetworkGenerationRequirements.ComputeFullStandardTimeout);
        MathModel vcellMathModel = new MathModel(null);
        vcellMathModel.setMathDescription(simulationContext.getMathDescription());
        //get math model string
        String sbmlString = MathModel_SBMLExporter.getSBMLString(vcellMathModel, 2, 4);

//		String modelSbml = XmlHelper.exportSBML(simulationContext.getBioModel(), 3, 1, 0, false, simulationContext, null);
        
        XmlUtil.writeXMLStringToFile(sbmlString, outputModelSbmlFile.getAbsolutePath(), true);
        
		OptProblem optProblem = new OptProblem();
		optProblem.setMathModelSbmlFile(outputModelSbmlFile.getAbsolutePath());
        optProblem.setNumberOfOptimizationRuns(parameterEstimationTask.getOptimizationSolverSpec().getNumOfRuns());
        
        for (Parameter p : optimizationSpec.getParameters()){
        	ParameterDescription pdesc = new ParameterDescription(p.getName(), p.getScale(), p.getLowerBound(), p.getUpperBound(), p.getInitialGuess());
        	optProblem.addToParameterDescriptionList(pdesc);
        }
        
        SimpleReferenceData refData = (SimpleReferenceData)parameterEstimationTask.getModelOptimizationSpec().getReferenceData();
        // check if t is at the first column
		int timeIndex = refData.findColumn(ReservedVariable.TIME.getName());
		if (timeIndex != 0) {
			throw new RuntimeException("t must be the first column");
		}
        FileUtils.write(outputDataFile, refData.getCSV());
        optProblem.setExperimentalDataFile(outputDataFile.getAbsolutePath());
        
        
        optProblem.addToReferenceVariableList(new ReferenceVariable(ReservedVariable.TIME.getName(), ReferenceVariableType.independent));
		// add all other dependent variables, recall that the dependent variables start from 2nd column onward in reference data
		for (int i = 1; i < refData.getNumDataColumns(); i++) {
			ReferenceDataMappingSpec rdms = parameterEstimationTask.getModelOptimizationSpec().getReferenceDataMappingSpec(refData.getColumnNames()[i]);
			optProblem.addToReferenceVariableList(new ReferenceVariable(rdms.getModelObject().getName(), ReferenceVariableType.dependent));
		}
		
		cbit.vcell.opt.CopasiOptimizationMethod vcellCopasiOptimizationMethod = parameterEstimationTask.getOptimizationSolverSpec().getCopasiOptimizationMethod();
		OptimizationMethodType optMethodType = OptimizationMethodType.valueOf(vcellCopasiOptimizationMethod.getType().name());
		CopasiOptimizationMethod thriftOptMethod = new CopasiOptimizationMethod();
		thriftOptMethod.setOptimizationMethodType(optMethodType);

		for (cbit.vcell.opt.CopasiOptimizationParameter optParam : vcellCopasiOptimizationMethod.getParameters()) {
			org.vcell.optimization.thrift.CopasiOptimizationParameter p = new org.vcell.optimization.thrift.CopasiOptimizationParameter();
			p.setValue(optParam.getValue());
			org.vcell.optimization.thrift.OptimizationParameterType optParmType = null;
			switch(optParam.getType()) {
				case Cooling_Factor:
					optParmType = OptimizationParameterType.Cooling_Factor;
					break;
				case IterationLimit:
					optParmType = OptimizationParameterType.IterationLimit;
					break;
				case Number_of_Generations:
					optParmType = OptimizationParameterType.Number_of_Generations;
					break;
				case Number_of_Iterations:
					optParmType = OptimizationParameterType.Number_of_Iterations;
					break;
				case Pf:
					optParmType = OptimizationParameterType.Pf;
					break;
				case Population_Size:
					optParmType = OptimizationParameterType.Population_Size;
					break;
				case Random_Number_Generator:
					optParmType = OptimizationParameterType.Random_Number_Generator;
					break;
				case Rho:
					optParmType = OptimizationParameterType.Rho;
					break;
				case Scale:
					optParmType = OptimizationParameterType.Scale;
					break;
				case Seed:
					optParmType = OptimizationParameterType.Seed;
					break;
				case Start_Temperature:
					optParmType = OptimizationParameterType.Start_Temperature;
					break;
				case Std_Deviation:
					optParmType = OptimizationParameterType.Std_Deviation;
					break;
				case Swarm_Size:
					optParmType = OptimizationParameterType.Swarm_Size;
					break;
				case Tolerance:
					optParmType = OptimizationParameterType.Tolerance;
					break;
				default:
					throw new RuntimeException("unsupported parameter type :"+optParam.getType().name()+" in COPASI optimization solver");
			}
			p.setDataType(optParmType);
			thriftOptMethod.addToOptimizationParameterList(p);
		}
		optProblem.setOptimizationMethod(thriftOptMethod);

		return optProblem;
	}


	public static void main(String[] args) {
		try {
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
