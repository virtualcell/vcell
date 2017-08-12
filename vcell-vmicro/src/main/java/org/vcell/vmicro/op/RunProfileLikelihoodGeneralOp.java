package org.vcell.vmicro.op;

import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;
import org.vcell.vmicro.workflow.data.OptContext;
import org.vcell.vmicro.workflow.data.OptContextSolver;

import cbit.vcell.opt.Parameter;

public class RunProfileLikelihoodGeneralOp {
	
	private static final double CONFIDENCE_INTERVAL_STEP = 0.004;
	private static final int MAX_ITERATION = 1000;
	private static final double MIN_LIKELIHOOD_CHANGE = 0.01;
	private static double epsilon = 1e-8;

	private double leastError = Double.MAX_VALUE;

	public ProfileData[] runProfileLikihood(OptContext optContext, final ClientTaskStatusSupport clientTaskStatusSupport) throws Exception {
		Parameter[] bestParameters = getBestParameters(optContext, optContext.getParameters(), null);
		ProfileData[] profileData = evaluateParameters(optContext,bestParameters,clientTaskStatusSupport);
		return profileData;
	}
	
	private Parameter[] getBestParameters(OptContext optContext, Parameter[] inParams, Parameter fixedParam) throws Exception
	{
		int numFixedParam = (fixedParam == null)? 0:1;
		Parameter[] outputParams = new Parameter[inParams.length + numFixedParam];//return to the caller, parameter should be a whole set including the fixed parameter
		String[] outParaNames = new String[inParams.length];//send to optimizer
		double[] outParaVals = new double[inParams.length];//send to optimizer
		//optimization
		leastError = OptContextSolver.estimate(optContext, inParams, outParaNames, outParaVals);
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
	
	private ProfileData[] evaluateParameters(OptContext optContext, Parameter[] currentParams, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
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
				
				if (clientTaskStatusSupport!=null && clientTaskStatusSupport.isInterrupted())
				{
					throw UserCancelException.CANCEL_GENERIC;
				}

				lastError = error;
				iterationCount++;
				if (clientTaskStatusSupport!=null){
					clientTaskStatusSupport.setProgress((int)((iterationCount*1.0/MAX_ITERATION) * 0.5 * 100));
				}
			}
			if (clientTaskStatusSupport!=null){
				clientTaskStatusSupport.setProgress(50);//half way through evaluation of a parameter.
			}
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
				
				if (clientTaskStatusSupport!=null && clientTaskStatusSupport.isInterrupted())
				{
					throw UserCancelException.CANCEL_GENERIC;
				}
				lastError = error;
				iterationCount++;
				if (clientTaskStatusSupport!=null){
					clientTaskStatusSupport.setProgress((int)(((iterationCount+MAX_ITERATION)*1.0/MAX_ITERATION) * 0.5 * 100));
				}
			}
			resultData[j] = profileData;
			if (clientTaskStatusSupport!=null){
				clientTaskStatusSupport.setProgress(100);//finish evaluation of a parameter
			}
		}
		optContext.clearFixedParameter();
		//this message is specifically set for batchrun, the message will stay in the status panel. It doesn't affect single run,which disappears quickly that user won't notice.
		if (clientTaskStatusSupport!=null){
			clientTaskStatusSupport.setMessage("Evaluating confidence intervals ...");
		}
//		long endTime =System.currentTimeMillis();
//		System.out.println("total time used:" + (endTime - startTime));
		return resultData;
	}
}
