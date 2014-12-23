package org.vcell.vmicro.workflow.task;

import org.vcell.optimization.DefaultOptSolverCallbacks;
import org.vcell.optimization.OptSolverCallbacks;
import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;
import org.vcell.vmicro.workflow.data.ModelType;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptContextObjectiveFunction;
import org.vcell.vmicro.workflow.data.OptModel;
import org.vcell.vmicro.workflow.data.OptModelOneDiff;
import org.vcell.vmicro.workflow.data.OptModelTwoDiffWithPenalty;
import org.vcell.vmicro.workflow.data.OptModelTwoDiffWithoutPenalty;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.DataOutput;
import org.vcell.workflow.Task;
import org.vcell.workflow.TaskContext;

import cbit.function.DefaultScalarFunction;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.opt.ImplicitObjectiveFunction;
import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.solvers.PowellOptimizationSolver;

public class RunProfileLikelihood extends Task {
	
	//different model parameters
	private static String[] MODEL_PARAMETER_NAMES = new String[]{"Primary_diffusion_rate",
																"Primary_mobile_fraction",
																"Bleach_while_monitoring_rate",
																"Secondary_diffusion_rate",
																"Secondary_mobile_fraction",
																"Binding_site_concentration",
																"Reaction_on_rate",
																"Reaction_off_rate"};
	//other contants
	private static int NUM_MODEL_PARAMETERS_ONE_DIFF = 3;
	private static int NUM_MODEL_PARAMETERS_TWO_DIFF = 5;
	private static int INDEX_PRIMARY_DIFF_RATE = 0;
	private static int INDEX_PRIMARY_FRACTION = 1;
	private static int INDEX_BLEACH_MONITOR_RATE = 2;
	private static int INDEX_SECONDARY_DIFF_RATE = 3;
	private static int INDEX_SECONDARY_FRACTION = 4;

	//referenced parameters
	private static final Parameter REF_DIFFUSION_RATE_PARAM = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE], 0, 200, 1.0, 1.0);
	private static final Parameter REF_MOBILE_FRACTION_PARAM = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION], 0, 1, 1.0, 1.0);
	private static final Parameter REF_BLEACH_WHILE_MONITOR_PARAM = new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE], 0, 1, 1.0,  0);
	private static final Parameter REF_SECOND_DIFFUSION_RATE_PARAM = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE], 0, 100, 1.0, 1.0);
	private static final Parameter REF_SECOND_MOBILE_FRACTION_PARAM = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION], 0, 1, 1.0, 1.0);
	//concentration parameter is used for reaction off rate model to store unuseful parameter A.
	
	private static final double[] DEFAULT_CI_STEPS = new double[]{0.04, 0.004, 0.04, 0.04, 0.04};
	private static final int MAX_ITERATION = 100;
	private static final double MIN_LIKELIHOOD_CHANGE = 0.01;
	private static double epsilon = 1e-8;
	private static double FTOL = 1.0e-6;
	
	//
	// inputs
	//
	public final DataInput<RowColumnResultSet> refSimData;
	public final DataInput<Double> refSimDiffusionRate;
	public final DataInput<RowColumnResultSet> normExpData;
	public final DataInput<RowColumnResultSet> normalizedMeasurementErrors;
	public final DataInput<String> modelType;
	//
	// outputs
	//
	public final DataOutput<ProfileData[]> profileData;
	
	private double leastError = Double.MAX_VALUE;
	

	public RunProfileLikelihood(String id){
		super(id);
		refSimData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"refSimData",this);
		refSimDiffusionRate = new DataInput<Double>(Double.class,"refSimDiffusionRate",this);
		normExpData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"normExpData",this);
		normalizedMeasurementErrors = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"normalizedMeasurementErrors",this);
		modelType = new DataInput<String>(String.class,"modelType",this);
		profileData = new DataOutput<ProfileData[]>(ProfileData[].class,"profileData",this);
		addInput(refSimData);
		addInput(refSimDiffusionRate);
		addInput(normExpData);
		addInput(normalizedMeasurementErrors);
		addInput(modelType);
		addOutput(profileData);
	}

	@Override
	protected void compute0(TaskContext context, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		RowColumnResultSet refSimDataset = context.getData(refSimData);
		double[] refSimTimePoints = refSimDataset.extractColumn(0);

		int numRois = refSimDataset.getDataColumnCount()-1;
		int numRefSimTimes = refSimDataset.getRowCount();
		double[][] refSimData = new double[numRois][numRefSimTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = refSimDataset.extractColumn(roi+1);
			for (int t=0; t<numRefSimTimes; t++){
				refSimData[roi][t] = roiData[t];
			}
		}
		
		RowColumnResultSet normExpDataset = context.getData(normExpData);
		double[] normExpTimePoints = normExpDataset.extractColumn(0);

//		int numRois = normExpDataset.getDataColumnCount()-1;
		int numNormExpTimes = normExpDataset.getRowCount();
		double[][] normExpData = new double[numRois][numNormExpTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = normExpDataset.extractColumn(roi+1);
			for (int t=0; t<numNormExpTimes; t++){
				normExpData[roi][t] = roiData[t];
			}
		}
		
		RowColumnResultSet measurementErrorDataset = context.getData(normalizedMeasurementErrors);
//		int numRois = measurementErrorDataset.getDataColumnCount()-1;
//		int numNormMeasurementErrorTimes = measurementErrorDataset.getRowCount();
		double[][] measurementErrors = new double[numRois][numNormExpTimes];
		for (int roi=0; roi<numRois; roi++){
			double[] roiData = measurementErrorDataset.extractColumn(roi+1);
			for (int t=0; t<numNormExpTimes; t++){
				measurementErrors[roi][t] = roiData[t];
			}
		}
		
		ModelType modelType = ModelType.valueOf(context.getData(this.modelType));
		OptModel optModel = null;
		switch (modelType){
			case DiffOne: {
				optModel = new OptModelOneDiff(refSimData, refSimTimePoints, context.getData(refSimDiffusionRate));
				break;
			}
			case DiffTwoWithPenalty: {
				optModel = new OptModelTwoDiffWithPenalty(refSimData, refSimTimePoints, context.getData(refSimDiffusionRate));
				break;
			}
			case DiffTwoWithoutPenalty: {
				optModel = new OptModelTwoDiffWithoutPenalty(refSimData, refSimTimePoints, context.getData(refSimDiffusionRate));
				break;
			}
			default:{
				throw new RuntimeException("model type "+modelType+" not supported");
			}
		}
		
		OptContext optContext = new OptContext(optModel,normExpData,normExpTimePoints,measurementErrors);
		Parameter[] bestParameters = getBestParameters(optContext,optContext.getParameters(), null);
		ProfileData[] profileData = evaluateParameters(optContext,modelType,bestParameters,clientTaskStatusSupport);
		context.setData(this.profileData,profileData);
	}
	
	//for second run of optimization for diffusion with two diffusing components
	private static Parameter[] generateInParamSet(Parameter[] inputParams, double newValues[])
	{
		Parameter[] result = new Parameter[inputParams.length];
		Parameter primaryRate = inputParams[INDEX_PRIMARY_DIFF_RATE];
		Parameter primaryFrac = inputParams[INDEX_PRIMARY_FRACTION];
		Parameter bwmRate = inputParams[INDEX_BLEACH_MONITOR_RATE];
		Parameter secondaryRate = inputParams[INDEX_SECONDARY_DIFF_RATE];
		Parameter secondaryFrac = inputParams[INDEX_SECONDARY_FRACTION];
		
		if(newValues[INDEX_PRIMARY_DIFF_RATE] < primaryRate.getLowerBound())
		{
			newValues[INDEX_PRIMARY_DIFF_RATE] = primaryRate.getLowerBound();
		}
		if(newValues[INDEX_PRIMARY_DIFF_RATE] > primaryRate.getUpperBound())
		{
			newValues[INDEX_PRIMARY_DIFF_RATE] = primaryRate.getUpperBound();
		}
		if(newValues[INDEX_PRIMARY_FRACTION] < primaryFrac.getLowerBound())
		{
			newValues[INDEX_PRIMARY_FRACTION] = primaryFrac.getLowerBound();
		}
		if(newValues[INDEX_PRIMARY_FRACTION] > primaryFrac.getUpperBound())
		{
			newValues[INDEX_PRIMARY_FRACTION] = primaryFrac.getUpperBound();
		}
		if(newValues[INDEX_BLEACH_MONITOR_RATE] < bwmRate.getLowerBound())
		{
			newValues[INDEX_BLEACH_MONITOR_RATE] = bwmRate.getLowerBound();
		}	
		if(newValues[INDEX_BLEACH_MONITOR_RATE] > bwmRate.getUpperBound())
		{
			newValues[INDEX_BLEACH_MONITOR_RATE] = bwmRate.getUpperBound();
		}
		if(newValues[INDEX_SECONDARY_DIFF_RATE] < secondaryRate.getLowerBound())
		{
			newValues[INDEX_SECONDARY_DIFF_RATE] = secondaryRate.getLowerBound();
		}
		if(newValues[INDEX_SECONDARY_DIFF_RATE] > secondaryRate.getUpperBound())
		{
			newValues[INDEX_SECONDARY_DIFF_RATE] = secondaryRate.getUpperBound();
		}
		if(newValues[INDEX_SECONDARY_FRACTION] < secondaryFrac.getLowerBound())
		{
			newValues[INDEX_SECONDARY_FRACTION] = secondaryFrac.getLowerBound();
		}
		if(newValues[INDEX_SECONDARY_FRACTION] > secondaryFrac.getUpperBound())
		{
			newValues[INDEX_SECONDARY_FRACTION] = secondaryFrac.getUpperBound();
		}
		
		
		result[INDEX_PRIMARY_DIFF_RATE] = new Parameter(primaryRate.getName(), 
                                                    primaryRate.getLowerBound(), 
                                                    primaryRate.getUpperBound(),
                                                    primaryRate.getScale(),
                                                    newValues[INDEX_PRIMARY_DIFF_RATE]);
		
		result[INDEX_PRIMARY_FRACTION] = new Parameter(primaryFrac.getName(), 
													primaryFrac.getLowerBound(), 
													primaryFrac.getUpperBound(),
													primaryFrac.getScale(),
													newValues[INDEX_PRIMARY_FRACTION]);
		result[INDEX_BLEACH_MONITOR_RATE] = new Parameter(bwmRate.getName(),
												    bwmRate.getLowerBound(),
												    bwmRate.getUpperBound(),
												    bwmRate.getScale(),
												    newValues[INDEX_BLEACH_MONITOR_RATE]);
		result[INDEX_SECONDARY_DIFF_RATE] = new Parameter(secondaryRate.getName(), 
													secondaryRate.getLowerBound(), 
													secondaryRate.getUpperBound(),
													secondaryRate.getScale(),
													newValues[INDEX_SECONDARY_DIFF_RATE]);
		result[INDEX_SECONDARY_FRACTION] = new Parameter(secondaryFrac.getName(), 
													secondaryFrac.getLowerBound(), 
													secondaryFrac.getUpperBound(),
													secondaryFrac.getScale(),
													newValues[INDEX_SECONDARY_FRACTION]);
       
 		return result;
	}
	public double estimate(OptContext optContext, Parameter[] inParams, String[] outParaNames, double[] outParaVals) throws Exception
	{
		//long startTime =System.currentTimeMillis();
		// create optimization solver 
		PowellOptimizationSolver optSolver = new PowellOptimizationSolver();
		// create optimization spec
		OptimizationSpec optSpec = new OptimizationSpec();
		DefaultScalarFunction scalarFunc = new OptContextObjectiveFunction(optContext);
		optSpec.setObjectiveFunction(new ImplicitObjectiveFunction(scalarFunc));
		// create solver spec 
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(OptimizationSolverSpec.SOLVERTYPE_POWELL, FTOL);
		// create solver call back
		OptSolverCallbacks optSolverCallbacks = new DefaultOptSolverCallbacks();
		// create optimization result set
		OptimizationResultSet optResultSet = null;
		for (int i = 0; i < inParams.length; i++) { //add parameters
			optSpec.addParameter(inParams[i]);
		}
		optResultSet = optSolver.solve(optSpec, optSolverSpec, optSolverCallbacks);
		OptSolverResultSet optSolverResultSet = optResultSet.getOptSolverResultSet();
		//if the parameters are 5, we have to go over again to see if we get the best answer.
		if(inParams.length == 5)//5 parameters
		{
			OptimizationSpec optSpec2 = new OptimizationSpec();
			optSpec2.setObjectiveFunction(new ImplicitObjectiveFunction(scalarFunc));
			Parameter[] inParamsFromResult = generateInParamSet(inParams, optSolverResultSet.getBestEstimates());
			for (int i = 0; i < inParamsFromResult.length; i++) { //add parameters
				optSpec2.addParameter(inParamsFromResult[i]);
			}
			OptimizationResultSet tempOptResultSet = optSolver.solve(optSpec2, optSolverSpec, optSolverCallbacks);
			OptSolverResultSet  tempOptSolverResultSet = tempOptResultSet.getOptSolverResultSet();
			if(optSolverResultSet.getLeastObjectiveFunctionValue() > tempOptSolverResultSet.getLeastObjectiveFunctionValue())
			{
				optSolverResultSet = tempOptSolverResultSet;
			}
		}
		//System.out.println("obj function value:"+optResultSet.getObjectiveFunctionValue());
		//System.out.println("");
		// copy results to output parameters
		String[] names = optSolverResultSet.getParameterNames();
		double[] values = optSolverResultSet.getBestEstimates();
		for (int i = 0; i < names.length; i++) 
		{
			outParaNames[i] = names[i];
			outParaVals[i] = values[i];
		}
		//long endTime =System.currentTimeMillis();
		//System.out.println("total: " + ( endTime - startTime) );
		return  optSolverResultSet.getLeastObjectiveFunctionValue();
	}

	public Parameter[] getBestParameters(OptContext optContext, Parameter[] inParams, Parameter fixedParam) throws Exception
	{
		int numFixedParam = (fixedParam == null)? 0:1;
		Parameter[] outputParams = new Parameter[inParams.length + numFixedParam];//return to the caller, parameter should be a whole set including the fixed parameter
		String[] outParaNames = new String[inParams.length];//send to optimizer
		double[] outParaVals = new double[inParams.length];//send to optimizer
		//optimization
		leastError = estimate(optContext, inParams, outParaNames, outParaVals);
		//get results as Parameters (take fixed parameter into account)
		for(int i = 0; i < outParaNames.length; i++)
		{
			if(outParaNames[i].equals(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE]))
			{
				double primaryDiffRate = outParaVals[i];
				if(primaryDiffRate > REF_DIFFUSION_RATE_PARAM.getUpperBound())
				{
					primaryDiffRate = REF_DIFFUSION_RATE_PARAM.getUpperBound(); 
				}
				if(primaryDiffRate < REF_DIFFUSION_RATE_PARAM.getLowerBound())
				{
					primaryDiffRate = REF_DIFFUSION_RATE_PARAM.getLowerBound();
				}
	
				outputParams[INDEX_PRIMARY_DIFF_RATE] = new Parameter(outParaNames[i], REF_DIFFUSION_RATE_PARAM.getLowerBound(), REF_DIFFUSION_RATE_PARAM.getUpperBound(), 1.0, primaryDiffRate);
			}
			else if(outParaNames[i].equals(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION]))
			{
				double primaryFraction = outParaVals[i];
				if(primaryFraction > REF_MOBILE_FRACTION_PARAM.getUpperBound())
				{
					primaryFraction = REF_MOBILE_FRACTION_PARAM.getUpperBound(); 
				}
				if(primaryFraction < REF_MOBILE_FRACTION_PARAM.getLowerBound())
				{
					primaryFraction = REF_MOBILE_FRACTION_PARAM.getLowerBound();
				}
	
				outputParams[INDEX_PRIMARY_FRACTION] = new Parameter(outParaNames[i], REF_MOBILE_FRACTION_PARAM.getLowerBound(), REF_MOBILE_FRACTION_PARAM.getUpperBound(), 1.0, primaryFraction);
			}
			else if(outParaNames[i].equals(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE]))
			{
				double bwmRate = outParaVals[i];
				if(bwmRate > REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound())
				{
					bwmRate = REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(); 
				}
				if(bwmRate < REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound())
				{
					bwmRate = REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound();
				}
	
				outputParams[INDEX_BLEACH_MONITOR_RATE] = new Parameter(outParaNames[i], REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(), REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(), 1.0, bwmRate);
			}
			else if(outParaNames[i].equals(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE]))
			{
				double secDiffRate = outParaVals[i];
				if(secDiffRate > REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound())
				{
					secDiffRate = REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(); 
				}
				if(secDiffRate < REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound())
				{
					secDiffRate = REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound();
				}
	
				outputParams[INDEX_SECONDARY_DIFF_RATE] = new Parameter(outParaNames[i], REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(), REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(), 1.0, secDiffRate);
			}
			else if(outParaNames[i].equals(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION]))
			{
				double secFraction = outParaVals[i];
				if(secFraction > REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound())
				{
					secFraction = REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(); 
				}
				if(secFraction < REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound())
				{
					secFraction = REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound();
				}
	
				outputParams[INDEX_SECONDARY_FRACTION] = new Parameter(outParaNames[i], REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(), REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(), 1.0, secFraction);
			}
		}
		//add fixed parameter to the best parameter output to make a whole set
		if(fixedParam != null)
		{
			if(fixedParam.getName().equals(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE]))
			{
				outputParams[INDEX_PRIMARY_DIFF_RATE] = fixedParam;
			}
			else if(fixedParam.getName().equals(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION]))
			{
				outputParams[INDEX_PRIMARY_FRACTION] = fixedParam;
			}
			else if(fixedParam.getName().equals(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE]))
			{
				outputParams[INDEX_BLEACH_MONITOR_RATE] = fixedParam;
			}
			else if(fixedParam.getName().equals(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE]))
			{
				outputParams[INDEX_SECONDARY_DIFF_RATE] = fixedParam;
			}
			else if(fixedParam.getName().equals(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION]))
			{
				outputParams[INDEX_SECONDARY_FRACTION] = fixedParam;
			}
		}
		//for debug purpose
//		for(int i = 0; i < outParams.length; i++)
//		{
//			System.out.println("Estimate result for "+outParams[i].getName()+ " is: "+outParams[i].getInitialGuess());
//		}
		return outputParams;
	}
	
	public ProfileData[] evaluateParameters(OptContext optContext, ModelType modelType, Parameter[] currentParams, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
//		long startTime =System.currentTimeMillis();
		int totalParamLen = currentParams.length;
		ProfileData[] resultData = new ProfileData[totalParamLen];

		for(int j=0; j<totalParamLen; j++)
		{
			ProfileData profileData = new ProfileData();
			//add the fixed parameter to profileData, output exp data and opt results
			Parameter[] newBestParameters = getBestParameters(optContext, currentParams, null);
			double iniError = leastError;
			//fixed parameter
			Parameter fixedParam = newBestParameters[j];
			if(fixedParam.getInitialGuess() == 0)//log function cannot take 0 as parameter
			{
				fixedParam = new Parameter (fixedParam.getName(),
	                        fixedParam.getLowerBound(),
	                        fixedParam.getUpperBound(),
	                        fixedParam.getScale(),
	                        epsilon);
			}
			if(clientTaskStatusSupport != null)
			{
				if(totalParamLen == NUM_MODEL_PARAMETERS_ONE_DIFF)
				{
					clientTaskStatusSupport.setMessage("<html>Evaluating confidence intervals of \'" + fixedParam.getName() + "\' <br> of diffusion with one diffusing component model.</html>");
				}
				else if(totalParamLen == NUM_MODEL_PARAMETERS_TWO_DIFF)
				{
					clientTaskStatusSupport.setMessage("<html>Evaluating confidence intervals of \'" + fixedParam.getName() + "\' <br> of diffusion with two diffusing components model.</html>");
				}
				clientTaskStatusSupport.setProgress(0);//start evaluation of a parameter.
			}
			ProfileDataElement pde = new ProfileDataElement(fixedParam.getName(), Math.log10(fixedParam.getInitialGuess()), iniError, newBestParameters);
			profileData.addElement(pde);
			
			Parameter[] unFixedParams = new Parameter[totalParamLen - 1];
			int indexCounter = 0;
			for(int i=0; i<totalParamLen; i++)
			{
				if(!newBestParameters[i].getName().equals(fixedParam.getName()))
				{
					unFixedParams[indexCounter] = newBestParameters[i];
					indexCounter++;
				}
				else continue;
			}
			//increase
			int iterationCount = 1;
			double paramLogVal = Math.log10(fixedParam.getInitialGuess());
			double lastError = iniError;
			boolean isBoundReached = false;
			double incrementStep = DEFAULT_CI_STEPS[j];
			int stepIncreaseCount = 0;
			while(true)
			{
				if(iterationCount > MAX_ITERATION)//if exceeds the maximum iterations, break;
				{
					break;
				}
				if(isBoundReached)
				{
					break;
				}
				paramLogVal = paramLogVal + incrementStep ;
				double paramVal = Math.pow(10,paramLogVal);
				if(paramVal > (fixedParam.getUpperBound() - epsilon))
				{
					paramVal = fixedParam.getUpperBound();
					paramLogVal = Math.log10(fixedParam.getUpperBound());
					isBoundReached = true;
				}
				Parameter increasedParam = new Parameter (fixedParam.getName(),
	                                                      fixedParam.getLowerBound(),
	                                                      fixedParam.getUpperBound(),
	                                                      fixedParam.getScale(),
	                                                      paramVal);
				//getBestParameters returns the whole set of parameters including the fixed parameters
				optContext.setFixedParameter(fixedParam, paramVal);
				Parameter[] newParameters = getBestParameters(optContext, unFixedParams, increasedParam);
				for(int i=0; i < newParameters.length; i++)//use last step unfixed parameter values to optimize
				{
					for(int k=0; k<unFixedParams.length; k++)
					{
						if(newParameters[i].getName().equals(unFixedParams[k].getName()))
						{
							Parameter tempParameter = new Parameter(unFixedParams[k].getName(),
																	unFixedParams[k].getLowerBound(),
																	unFixedParams[k].getUpperBound(),
																	unFixedParams[k].getScale(),
									                                newParameters[i].getInitialGuess());
							unFixedParams[k] = tempParameter;
						}
					}
				}
				double error = leastError;
				pde = new ProfileDataElement(increasedParam.getName(), paramLogVal, error, newParameters);
				profileData.addElement(pde);
				//check if the we run enough to get confidence intervals(99% @6.635, we plus 10 over the min error)
				if(error > (iniError+10))
				{
					break;
				}
				if(Math.abs((error-lastError)/lastError) < MIN_LIKELIHOOD_CHANGE)
				{
					stepIncreaseCount ++;
					incrementStep = DEFAULT_CI_STEPS[j] * Math.pow(2, stepIncreaseCount);
				}
				else
				{
					if(stepIncreaseCount > 1)
					{
						incrementStep = DEFAULT_CI_STEPS[j] / Math.pow(2, stepIncreaseCount);
						stepIncreaseCount --;
					}
				}
				//use first derivative
//				double yPrime = Math.abs((error-lastError)/(paramLogVal - lastLogVal));
//				if(yPrime < (0.1763+FRAPOptimization.epsilon) /*< 10 degree angle*/|| yPrime > (56.9168 + FRAPOptimization.epsilon) /*>89 degree angle*/)
//				{
//					stepIncreaseCount ++;
//					incrementStep = DEFAULT_CI_STEPS[j] * Math.pow(2, stepIncreaseCount);
//				}
//				else
//				{
//					stepIncreaseCount = 0;
//					incrementStep = DEFAULT_CI_STEPS[j];
//				}
				
				if (clientTaskStatusSupport.isInterrupted())
				{
					throw UserCancelException.CANCEL_GENERIC;
				}

				lastError = error;
				iterationCount++;
				clientTaskStatusSupport.setProgress((int)((iterationCount*1.0/MAX_ITERATION) * 0.5 * 100));
			}
			clientTaskStatusSupport.setProgress(50);//half way through evaluation of a parameter.
			//decrease
			iterationCount = 1;
			paramLogVal = Math.log10(fixedParam.getInitialGuess());;
			;
			lastError = iniError;
			isBoundReached = false;
			double decrementStep = DEFAULT_CI_STEPS[j];
			stepIncreaseCount = 0;
			while(true)
			{
				if(iterationCount > MAX_ITERATION)//if exceeds the maximum iterations, break;
				{
					break;
				}
				if(isBoundReached)
				{
					break;
				}
				paramLogVal = paramLogVal - decrementStep;
				double paramVal = Math.pow(10,paramLogVal);
				if(paramVal < (fixedParam.getLowerBound() + epsilon))
				{
					paramVal = epsilon;
					paramLogVal = Math.log10(epsilon);
					isBoundReached = true;
				}
				Parameter decreasedParam = new Parameter (fixedParam.getName(),
	                                            fixedParam.getLowerBound(),
	                                            fixedParam.getUpperBound(),
	                                            fixedParam.getScale(),
	                                            paramVal);
				//getBestParameters returns the whole set of parameters including the fixed parameters
				optContext.setFixedParameter(fixedParam, paramVal);
				Parameter[] newParameters = getBestParameters(optContext, unFixedParams, decreasedParam);
				for(int i=0; i < newParameters.length; i++)//use last step unfixed parameter values to optimize
				{
					for(int k=0; k<unFixedParams.length; k++)
					{
						if(newParameters[i].getName().equals(unFixedParams[k].getName()))
						{
							Parameter tempParameter = new Parameter(unFixedParams[k].getName(),
																	unFixedParams[k].getLowerBound(),
																	unFixedParams[k].getUpperBound(),
																	unFixedParams[k].getScale(),
									                                newParameters[i].getInitialGuess());
							unFixedParams[k] = tempParameter;
						}
					}
				}
				double error = leastError;
				pde = new ProfileDataElement(decreasedParam.getName(), paramLogVal, error, newParameters);
				profileData.addElement(0,pde);
				if(error > (iniError+10))
				{
					break;
				}
				if(Math.abs((error-lastError)/lastError) < MIN_LIKELIHOOD_CHANGE)
				{
					stepIncreaseCount ++;
					decrementStep = DEFAULT_CI_STEPS[j] * Math.pow(2, stepIncreaseCount);
				}
				else
				{
					if(stepIncreaseCount > 1)
					{
						incrementStep = DEFAULT_CI_STEPS[j] / Math.pow(2, stepIncreaseCount);
						stepIncreaseCount --;
					}
				}
				// use first derivative
//				double yPrime = Math.abs((error-lastError)/(paramLogVal -lastLogVal));
//				if(yPrime < (0.0875 + FRAPOptimization.epsilon)/*5 degree angle*/ || yPrime > (56.9168 + FRAPOptimization.epsilon)/*89 degree angle*/ )
//				{
//					stepIncreaseCount++;
//					decrementStep = DEFAULT_CI_STEPS[j] * Math.pow(2, stepIncreaseCount);
//				}
//				else
//				{
//					stepIncreaseCount = 0;
//					decrementStep = DEFAULT_CI_STEPS[j];
//				}
				
				if (clientTaskStatusSupport.isInterrupted())
				{
					throw UserCancelException.CANCEL_GENERIC;
				}
				lastError = error;
				iterationCount++;
				clientTaskStatusSupport.setProgress((int)(((iterationCount+MAX_ITERATION)*1.0/MAX_ITERATION) * 0.5 * 100));
			}
			resultData[j] = profileData;
			clientTaskStatusSupport.setProgress(100);//finish evaluation of a parameter
		}
		optContext.clearFixedParameter();
		//this message is specifically set for batchrun, the message will stay in the status panel. It doesn't affect single run,which disappears quickly that user won't notice.
		clientTaskStatusSupport.setMessage("Evaluating confidence intervals ...");
//		long endTime =System.currentTimeMillis();
//		System.out.println("total time used:" + (endTime - startTime));
		return resultData;
	}
}
