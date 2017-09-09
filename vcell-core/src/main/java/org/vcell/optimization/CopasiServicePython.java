package org.vcell.optimization;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.sbml.jsbml.SBMLException;
import org.vcell.optimization.thrift.CopasiOptimizationMethod;
import org.vcell.optimization.thrift.DataRow;
import org.vcell.optimization.thrift.DataSet;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptimizationMethodType;
import org.vcell.optimization.thrift.OptimizationParameterDataType;
import org.vcell.optimization.thrift.OptimizationParameterType;
import org.vcell.optimization.thrift.ParameterDescription;
import org.vcell.optimization.thrift.ReferenceVariable;
import org.vcell.optimization.thrift.ReferenceVariableType;
import org.vcell.sbml.vcell.MathModel_SBMLExporter;
import org.vcell.util.exe.Executable2;
import org.vcell.util.exe.ExecutableException;
import org.vcell.util.exe.IExecutable;
import org.vcell.vis.vtk.VtkService;

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
import cbit.vcell.resource.CondaSupport;
import cbit.vcell.resource.CondaSupport.InstallStatus;
import cbit.vcell.resource.CondaSupport.PythonPackage;
import cbit.vcell.resource.ResourceUtil;

public class CopasiServicePython {
	
	public static CopasiServicePython copasiService = null;
	protected static final Logger lg = Logger.getLogger(VtkService.class);

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
		
	public static OptProblem makeOptProblem(ParameterEstimationTask parameterEstimationTask) throws IOException, ExpressionException, SBMLException, XMLStreamException{
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

		OptProblem optProblem = new OptProblem();
		optProblem.setMathModelSbmlContents(sbmlString);
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
		DataSet dataset = new DataSet();
		for (int rowIndex=0; rowIndex<refData.getNumDataRows(); rowIndex++){
			DataRow dataRow = new DataRow();
			double[] array = refData.getDataByRow(rowIndex);
			for (double d : array){
				dataRow.addToData(d);
			}
			dataset.addToRows(dataRow);
		}
        optProblem.setExperimentalDataSet(dataset);
        
        
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
			org.vcell.optimization.thrift.OptimizationParameterDataType optDataType = null;
			switch(optParam.getType()) {
				case Cooling_Factor:
					optParmType = OptimizationParameterType.Cooling_Factor;
					optDataType = OptimizationParameterDataType.DOUBLE;
					break;
				case IterationLimit:
					optParmType = OptimizationParameterType.IterationLimit;
					optDataType = OptimizationParameterDataType.INT;
					break;
				case Number_of_Generations:
					optParmType = OptimizationParameterType.Number_of_Generations;
					optDataType = OptimizationParameterDataType.INT;
					break;
				case Number_of_Iterations:
					optParmType = OptimizationParameterType.Number_of_Iterations;
					optDataType = OptimizationParameterDataType.INT;
					break;
				case Pf:
					optParmType = OptimizationParameterType.Pf;
					optDataType = OptimizationParameterDataType.DOUBLE;
					break;
				case Population_Size:
					optParmType = OptimizationParameterType.Population_Size;
					optDataType = OptimizationParameterDataType.INT;
					break;
				case Random_Number_Generator:
					optParmType = OptimizationParameterType.Random_Number_Generator;
					optDataType = OptimizationParameterDataType.INT;
					break;
				case Rho:
					optParmType = OptimizationParameterType.Rho;
					optDataType = OptimizationParameterDataType.DOUBLE;
					break;
				case Scale:
					optParmType = OptimizationParameterType.Scale;
					optDataType = OptimizationParameterDataType.DOUBLE;
					break;
				case Seed:
					optParmType = OptimizationParameterType.Seed;
					optDataType = OptimizationParameterDataType.INT;
					break;
				case Start_Temperature:
					optParmType = OptimizationParameterType.Start_Temperature;
					optDataType = OptimizationParameterDataType.DOUBLE;
					break;
				case Std_Deviation:
					optParmType = OptimizationParameterType.Std_Deviation;
					optDataType = OptimizationParameterDataType.DOUBLE;
					break;
				case Swarm_Size:
					optParmType = OptimizationParameterType.Swarm_Size;
					optDataType = OptimizationParameterDataType.INT;
					break;
				case Tolerance:
					optParmType = OptimizationParameterType.Tolerance;
					optDataType = OptimizationParameterDataType.DOUBLE;
					break;
				default:
					throw new RuntimeException("unsupported parameter type :"+optParam.getType().name()+" in COPASI optimization solver");
			}
			p.setParamType(optParmType);
			p.setDataType(optDataType);
			thriftOptMethod.addToOptimizationParameterList(p);
		}
		optProblem.setOptimizationMethod(thriftOptMethod);

		return optProblem;
	}

	public static void runCopasiPython(File copasiOptProblemFile, File copasiResultsFile) throws IOException {
		//It's 2015 -- forward slash works for all operating systems
		File PYTHON = CondaSupport.getPythonExe();
		InstallStatus copasiInstallStatus = CondaSupport.getPythonPackageStatus(PythonPackage.COPASI);
		if (copasiInstallStatus==InstallStatus.FAILED){
			throw new RuntimeException("failed to install COPASI python package, consider re-installing VCell-managed python\n ...see Preferences->Python->Re-install");
		}
		if (copasiInstallStatus==InstallStatus.INITIALIZING){
			throw new RuntimeException("VCell is currently installing or verifying the COPASI python package ... please try again in a minute");
		}
		File visToolDir = ResourceUtil.getVisToolDir();
		File optServicePythonFile = new File(visToolDir,"optService.py");
		if (PYTHON==null || !PYTHON.exists()){
			throw new RuntimeException("python executable not specified, set python location in VCell menu File->Preferences...->Python Properties");
		}
		String[] cmd = new String[] { PYTHON.getAbsolutePath(),optServicePythonFile.getAbsolutePath(),copasiOptProblemFile.getAbsolutePath(), copasiResultsFile.getAbsolutePath()};
		IExecutable exe = prepareExecutable(cmd);
		try {
			exe.start( new int[] { 0 });
			if (exe.getExitValue() != 0){
				throw new RuntimeException("copasi python solver (optService.py) failed with return code "+exe.getExitValue()+": "+exe.getStderrString());
			}
		} catch (ExecutableException e) {
			e.printStackTrace();
			throw new RuntimeException("optService.py invocation failed: "+e.getMessage(),e);
		}
	}

	private static IExecutable prepareExecutable(String[] cmd) {
		if (lg.isInfoEnabled()) {
			lg.info("python command string:" + StringUtils.join(cmd," "));
		}
		System.out.println("python command string:" + StringUtils.join(cmd," "));
		Executable2 exe = new Executable2(cmd);
		return exe;
	}
}
