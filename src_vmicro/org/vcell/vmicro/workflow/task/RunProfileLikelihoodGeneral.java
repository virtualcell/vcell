package org.vcell.vmicro.workflow.task;

import org.vcell.optimization.DefaultOptSolverCallbacks;
import org.vcell.optimization.OptSolverCallbacks;
import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptContextObjectiveFunction;
import org.vcell.workflow.DataHolder;
import org.vcell.workflow.DataInput;
import org.vcell.workflow.Task;

import cbit.function.DefaultScalarFunction;
import cbit.vcell.opt.ImplicitObjectiveFunction;
import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.solvers.PowellOptimizationSolver;

public class RunProfileLikelihoodGeneral extends Task {
	
	private static final double CONFIDENCE_INTERVAL_STEP = 0.004;
	private static final int MAX_ITERATION = 1000;
	private static final double MIN_LIKELIHOOD_CHANGE = 0.01;
	private static double FTOL = 1.0e-6;
	private static double epsilon = 1e-8;

	//
	// inputs
	//
	public final DataInput<OptContext> optContext;
//	public final DataInput<RowColumnResultSet> refSimData;
//	public final DataInput<Double> refSimDiffusionRate;
//	public final DataInput<RowColumnResultSet> normExpData;
//	public final DataInput<RowColumnResultSet> normalizedMeasurementErrors;
//	public final DataInput<String> modelType;
	//
	// outputs
	//
	public final DataHolder<ProfileData[]> profileData;
	
	private double leastError = Double.MAX_VALUE;
	private int totalParamLen = -1;
	

	public RunProfileLikelihoodGeneral(String id){
		super(id);
		optContext = new DataInput<OptContext>(OptContext.class,"optContext",this);
//		refSimData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"refSimData",this);
//		refSimDiffusionRate = new DataInput<Double>(Double.class,"refSimDiffusionRate",this);
//		normExpData = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"normExpData",this);
//		normalizedMeasurementErrors = new DataInput<RowColumnResultSet>(RowColumnResultSet.class,"normalizedMeasurementErrors",this);
//		modelType = new DataInput<String>(String.class,"modelType",this);
		profileData = new DataHolder<ProfileData[]>(ProfileData[].class,"profileData",this);
		addInput(optContext);
//		addInput(refSimData);
//		addInput(refSimDiffusionRate);
//		addInput(normExpData);
//		addInput(normalizedMeasurementErrors);
//		addInput(modelType);
		addOutput(profileData);
	}

	@Override
	protected void compute0(final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
//		RowColumnResultSet refSimDataset = refSimData.getData();
//		double[] refSimTimePoints = refSimDataset.extractColumn(0);
//
//		int numRois = refSimDataset.getDataColumnCount()-1;
//		int numRefSimTimes = refSimDataset.getRowCount();
//		double[][] refSimData = new double[numRois][numRefSimTimes];
//		for (int roi=0; roi<numRois; roi++){
//			double[] roiData = refSimDataset.extractColumn(roi+1);
//			for (int t=0; t<numRefSimTimes; t++){
//				refSimData[roi][t] = roiData[t];
//			}
//		}
//		
//		RowColumnResultSet normExpDataset = normExpData.getData();
//		double[] normExpTimePoints = normExpDataset.extractColumn(0);
//
////		int numRois = normExpDataset.getDataColumnCount()-1;
//		int numNormExpTimes = normExpDataset.getRowCount();
//		double[][] normExpData = new double[numRois][numNormExpTimes];
//		for (int roi=0; roi<numRois; roi++){
//			double[] roiData = normExpDataset.extractColumn(roi+1);
//			for (int t=0; t<numNormExpTimes; t++){
//				normExpData[roi][t] = roiData[t];
//			}
//		}
//		
//		RowColumnResultSet measurementErrorDataset = normalizedMeasurementErrors.getData();
////		int numRois = measurementErrorDataset.getDataColumnCount()-1;
////		int numNormMeasurementErrorTimes = measurementErrorDataset.getRowCount();
//		double[][] measurementErrors = new double[numRois][numNormExpTimes];
//		for (int roi=0; roi<numRois; roi++){
//			double[] roiData = measurementErrorDataset.extractColumn(roi+1);
//			for (int t=0; t<numNormExpTimes; t++){
//				measurementErrors[roi][t] = roiData[t];
//			}
//		}
//		
//		ModelType modelType = ModelType.valueOf(this.modelType.getData());
//		Parameter[] parameters = null;
//		OptModel optModel = null;
//		switch (modelType){
//			case DiffOne: {
//				parameters = new Parameter[] { 
//						FRAPModel.REF_DIFFUSION_RATE_PARAM, 
//						FRAPModel.REF_MOBILE_FRACTION_PARAM, 
//						FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM };
//				optModel = new OptModelOneDiff(refSimData, refSimTimePoints, refSimDiffusionRate.getData());
//				break;
//			}
//	//		case DiffTwo: {
//	//			parameters = new Parameter[] { 
//	//					FRAPModel.REF_DIFFUSION_RATE_PARAM, 
//	//					FRAPModel.REF_MOBILE_FRACTION_PARAM, 
//	//					FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM, 
//	//					FRAPModel.REF_SECOND_DIFFUSION_RATE_PARAM,
//	//					FRAPModel.REF_SECOND_MOBILE_FRACTION_PARAM};
//	//			optModel = new OptModelTwoDiff(refSimData, refSimTimePoints, refSimDiffusionRate.getData());
//	//			break;
//	//		}
//			default:{
//				throw new RuntimeException("model type "+modelType+" not supported");
//			}
//		}
//		
//		OptContext optContext = new OptContext(optModel,normExpData,normExpTimePoints,measurementErrors); 
		OptContext context = optContext.getData();
		Parameter[] bestParameters = getBestParameters(context, context.getParameters(), null);
		ProfileData[] profileData = evaluateParameters(context,bestParameters,clientTaskStatusSupport);
		this.profileData.setData(profileData);
	}
	
	//for second run of optimization for diffusion with two diffusing components
	private static Parameter[] generateInParamSet(Parameter[] inputParams, double newValues[])
	{
		Parameter[] result = new Parameter[inputParams.length];

		for (int i=0;i<inputParams.length;i++){
			Parameter parameter = inputParams[i];
			if (newValues[i] < parameter.getLowerBound()){
				newValues[i] = parameter.getLowerBound();
			}
			if (newValues[i] > parameter.getUpperBound()){
				newValues[i] = parameter.getUpperBound();
			}
			result[i] = new Parameter(parameter.getName(),parameter.getLowerBound(),parameter.getUpperBound(),parameter.getScale(),newValues[i]);
		}       
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
		for (int fullParamIndex=0; fullParamIndex < optContext.getParameters().length; fullParamIndex++){
			Parameter p = optContext.getParameters()[fullParamIndex];
			for(int estParamIndex=0; estParamIndex < outParaNames.length; estParamIndex++){
				if (outParaNames[estParamIndex].equals(p.getName())){
					double value = outParaVals[estParamIndex];
					if (value > p.getUpperBound()){
						value = p.getUpperBound();
					}else if (value < p.getLowerBound()){
						value = p.getLowerBound();
					}
					outputParams[fullParamIndex] = new Parameter(p.getName(), p.getLowerBound(), p.getUpperBound(), 1.0, value);
				}
			}
			//add fixed parameter to the best parameter output to make a whole set
			if (fixedParam!=null && p.getName().equals(fixedParam.getName())){
				outputParams[fullParamIndex] = fixedParam;
			}
		}
		//for debug purpose
//		for(int i = 0; i < outParams.length; i++)
//		{
//			System.out.println("Estimate result for "+outParams[i].getName()+ " is: "+outParams[i].getInitialGuess());
//		}
		return outputParams;
	}
	
	public ProfileData[] evaluateParameters(OptContext optContext, Parameter[] currentParams, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
//		long startTime =System.currentTimeMillis();
		int totalParamLen = currentParams.length;
		ProfileData[] resultData = new ProfileData[totalParamLen];

		for(int j=0; j<totalParamLen; j++)
		{
			ProfileData profileData = new ProfileData();
			//add the fixed parameter to profileData, output exp data and opt results
			setNumEstimatedParams(totalParamLen);
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
			if(clientTaskStatusSupport != null)	{
				clientTaskStatusSupport.setMessage("<html>Evaluating confidence intervals of \'" + fixedParam.getName() + "\' <br> of model '"+optContext.getModelName()+"'.</html>");
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
			double incrementStep = CONFIDENCE_INTERVAL_STEP;
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
					paramVal = fixedParam.getUpperBound() - epsilon;
					paramLogVal = Math.log10(fixedParam.getUpperBound());
					isBoundReached = true;
				}
				Parameter increasedParam = new Parameter (fixedParam.getName(),
	                                                      fixedParam.getLowerBound(),
	                                                      fixedParam.getUpperBound(),
	                                                      fixedParam.getScale(),
	                                                      paramVal);
				//getBestParameters returns the whole set of parameters including the fixed parameters
				setNumEstimatedParams(totalParamLen-1);
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
					incrementStep = CONFIDENCE_INTERVAL_STEP * Math.pow(2, stepIncreaseCount);
				}
				else
				{
					if(stepIncreaseCount > 1)
					{
						incrementStep = CONFIDENCE_INTERVAL_STEP / Math.pow(2, stepIncreaseCount);
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
			double decrementStep = incrementStep;
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
					paramVal = fixedParam.getLowerBound() + epsilon;
					paramLogVal = Math.log10(epsilon);
					isBoundReached = true;
				}
				Parameter decreasedParam = new Parameter (fixedParam.getName(),
	                                            fixedParam.getLowerBound(),
	                                            fixedParam.getUpperBound(),
	                                            fixedParam.getScale(),
	                                            paramVal);
				//getBestParameters returns the whole set of parameters including the fixed parameters
				setNumEstimatedParams(totalParamLen-1);
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
					decrementStep = CONFIDENCE_INTERVAL_STEP * Math.pow(2, stepIncreaseCount);
				}
				else
				{
					if(stepIncreaseCount > 1)
					{
						incrementStep = CONFIDENCE_INTERVAL_STEP / Math.pow(2, stepIncreaseCount);
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

	
	private void setNumEstimatedParams(int totalParamLen) {
		this.totalParamLen = totalParamLen;
	}
}
