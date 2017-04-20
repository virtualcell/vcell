/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

import java.util.Arrays;

import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;

import cbit.vcell.math.ReservedVariable;
import cbit.vcell.microscopy.server.FrapDataUtils;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.DivideByZeroException;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class FRAPOptFunctions 
{
	public static String SYMBOL_A = "A";
	public static String SYMBOL_KOFF = "k_off";
	public static String SYMBOL_BWM_RATE = "beta";
	public static String SYMBOL_I_inibleached = "I_inibleached";
	public static String FUNC_RECOVERY_BLEACH_REACTION_DOMINANT = "("+ SYMBOL_I_inibleached + "+" +SYMBOL_A + "*(1-exp(-1*"+SYMBOL_KOFF+"*"+ReservedVariable.TIME.getName()+")))" + "*exp(-1*"+ SYMBOL_BWM_RATE+"*"+ ReservedVariable.TIME.getName() +")";
	public static String SYMBOL_I_inicell = "I_inicell";
	public static String FUNC_CELL_INTENSITY = SYMBOL_I_inicell + "*(exp(-1*"+SYMBOL_BWM_RATE+"*"+ReservedVariable.TIME.getName()+"))";
	
	private static int NUM_PARAM_ESTIMATED = 2;//reaction off rate and bleaching while monitoring rate
	
	//used for optimization when taking measurement error into account.
	//first dimension length 11, according to the order in FRAPData.VFRAP_ROI_ENUM
	//(to estimate reaction off rate, we use bleached ROI only)
	//second dimension time, total time length - starting index for recovery 
	private boolean bApplyMeasurementError = false;
	private double[][] measurementErrors = null;
	
	private FRAPStudy expFrapStudy = null;
	private FrapDataAnalysisResults.ReactionOnlyAnalysisRestults offRateResults = null;
	
	public FRAPOptFunctions(FRAPStudy argExpFrapStudy)
	{
		expFrapStudy = argExpFrapStudy;
	}
	
	public FRAPStudy getExpFrapStudy() {
		return expFrapStudy;
	}
	
	public FrapDataAnalysisResults.ReactionOnlyAnalysisRestults getOffRateResults() {
		return offRateResults;
	}

	public void setOffRateResults(FrapDataAnalysisResults.ReactionOnlyAnalysisRestults offRateResults) {
		this.offRateResults = offRateResults;
	}

	public boolean isApplyMeasurementError() {
		return bApplyMeasurementError;
	}

	public void setIsApplyMeasurementError(boolean bApplyMeasurementError) {
		this.bApplyMeasurementError = bApplyMeasurementError;
	}
	
	public double[][] getMeasurementErrors() {
		return measurementErrors;
	}

	public void setMeasurementErrors(double[][] measurementErrors) {
		this.measurementErrors = measurementErrors;
	}

	//The best parameters will return a whole set of reaction off rate parameters (totally 8 parameters)
	public Parameter[] getBestParamters(FRAPData frapData, Parameter fixedParam, boolean bApplyMeasurementError) throws Exception
	{
		if(measurementErrors == null)
		{
			measurementErrors = FRAPOptimizationUtils.refreshNormalizedMeasurementError(getExpFrapStudy());
		}
		setIsApplyMeasurementError(bApplyMeasurementError);
		Parameter[] outputParams = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE];
		FrapDataAnalysisResults.ReactionOnlyAnalysisRestults offRateResults = FRAPDataAnalysis.fitRecovery_reacOffRateOnly(frapData, fixedParam, measurementErrors,getExpFrapStudy().getStartingIndexForRecovery());
		setOffRateResults(offRateResults);
		
		outputParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE],
															    FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
															    FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
															    FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
															    offRateResults.getBleachWhileMonitoringTau());
		outputParams[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION] = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION],
																FRAPModel.REF_BS_CONCENTRATION_OR_A.getLowerBound(),
																FRAPModel.REF_BS_CONCENTRATION_OR_A.getUpperBound(),
																FRAPModel.REF_BS_CONCENTRATION_OR_A.getScale(),
																offRateResults.getFittingParamA());
		outputParams[FRAPModel.INDEX_OFF_RATE] = new Parameter (FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE],
															    FRAPModel.REF_REACTION_OFF_RATE.getLowerBound(),
															    FRAPModel.REF_REACTION_OFF_RATE.getUpperBound(),
															    FRAPModel.REF_REACTION_OFF_RATE.getScale(),
															    offRateResults.getOffRate());
//		System.out.println("best exp:" + offRateResults.getOffRateFitExpression());
//		System.out.println("error" + getWeightedError(offRateResults.getOffRateFitExpression()));
		return outputParams;
	}
	
	public Expression getRecoveryExpressionWithCurrentParameters(Parameter[] currentParams) throws ExpressionException
	{
		FRAPData frapData = getExpFrapStudy().getFrapData();
		double[] frapDataTimeStamps = frapData.getImageDataset().getImageTimeStamps();
		//Experiment - Cell ROI Average
		double[] temp_background = frapData.getAvgBackGroundIntensity();
		int startIndexRecovery = getExpFrapStudy().getStartingIndexForRecovery();
		double[] preBleachAvgXYZ = FrapDataUtils.calculatePreBleachAverageXYZ(frapData, startIndexRecovery);
		double[] bleachRegionData = FRAPDataAnalysis.getAverageROIIntensity(frapData, frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()),preBleachAvgXYZ,temp_background);;
		Expression bleachedAvgExp = new Expression(FRAPOptFunctions.FUNC_RECOVERY_BLEACH_REACTION_DOMINANT);
		// substitute parameter values 
		bleachedAvgExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_I_inibleached), new Expression(bleachRegionData[startIndexRecovery]));
		bleachedAvgExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(currentParams[FRAPModel.INDEX_BLEACH_MONITOR_RATE].getInitialGuess()));
		bleachedAvgExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_A), new Expression(currentParams[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION].getInitialGuess()));
		bleachedAvgExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_KOFF), new Expression(currentParams[FRAPModel.INDEX_OFF_RATE].getInitialGuess()));
		// time shift
		bleachedAvgExp.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(ReservedVariable.TIME.getName()+"-"+frapDataTimeStamps[startIndexRecovery]));
		
		return bleachedAvgExp;
	}
	
	//with all rois in first dimension and reduced time points in second dimension.
	public double[][] createData(Expression bleachedAvgExp, double[] time) throws DivideByZeroException, ExpressionException
	{
		double[][] result = null;
		FRAPData frapData = getExpFrapStudy().getFrapData();
		int roiLen = frapData.getROILength();
		result = new double[roiLen][time.length];
		
		for(int i=0; i< FRAPData.VFRAP_ROI_ENUM.values().length; i++)
		{
			if(FRAPData.VFRAP_ROI_ENUM.values()[i].equals(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED))
			{
				for(int j=0; j<time.length; j++)
				{
					Expression tempExp = new Expression(bleachedAvgExp);
					double tempData;
					tempExp.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(time[j]));
					tempData = tempExp.evaluateConstant();
					result[i][j] = tempData;
				}
			}
			else
			{
				Arrays.fill(result[i], FRAPOptimizationUtils.largeNumber);
			}
		}
		return result;
	}
	
	//fitExp contains time parameter only, all other parameter are substituted with parameter values.
	public double[][] getFitData(Parameter[] currentParams) throws DivideByZeroException, ExpressionException
	{
		Expression fitExp = getRecoveryExpressionWithCurrentParameters(currentParams);
		double[] frapDataTimeStamps = getExpFrapStudy().getFrapData().getImageDataset().getImageTimeStamps();
		int startIndexRecovery = getExpFrapStudy().getStartingIndexForRecovery();
		double[] truncatedTimes = new double[frapDataTimeStamps.length - startIndexRecovery];
		for (int i = startIndexRecovery; i < frapDataTimeStamps.length; i++) 
		{
			truncatedTimes[i-startIndexRecovery] = frapDataTimeStamps[i];
		}
//		System.out.println("fit exp:" + fitExp.flatten());
//		System.out.println("error" + getWeightedError(fitExp.flatten()));
		return createData(fitExp.flatten(), truncatedTimes);
	}
	
	//profileData array contains only two profile distribution, one for bleachWhileMonitoringRate and another for reaction off rate
	public ProfileData[] evaluateParameters(Parameter[] currentParams, ClientTaskStatusSupport clientTaskStatusSupport) throws Exception
	{
		int totalParamLen = currentParams.length;
		int resultDataCounter = 0;
		ProfileData[] resultData = new ProfileData[NUM_PARAM_ESTIMATED];
		FRAPStudy frapStudy = getExpFrapStudy();
		for(int j=0; j<totalParamLen; j++)
		{	//only bleach while monitoring rate and reaction off rate need to evaluated
			if(currentParams[j] != null && (currentParams[j].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]) ||
			   currentParams[j].getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE])))
			{
				ProfileData profileData = new ProfileData();
				//add the fixed parameter to profileData, output exp data and opt results
				Parameter[] newBestParameters = getBestParamters(frapStudy.getFrapData(), null, true);
				double iniError = getOffRateResults().getObjectiveFunctionValue();
				//fixed parameter(make sure parameter shall not be smaller than epsilon)
				Parameter fixedParam = newBestParameters[j];
				if(fixedParam.getInitialGuess() == 0)//log function cannot take 0 as parameter
				{
					fixedParam = new Parameter (fixedParam.getName(),
		                        fixedParam.getLowerBound(),
		                        fixedParam.getUpperBound(),
		                        fixedParam.getScale(),
		                        FRAPOptimizationUtils.epsilon);
				}
				if(clientTaskStatusSupport != null)
				{
					clientTaskStatusSupport.setMessage("<html>Evaluating confidence intervals of \'" + fixedParam.getName() + "\' <br> of finding reaction off rate model.</html>");
					clientTaskStatusSupport.setProgress(0);//start evaluation of a parameter.
				}
				ProfileDataElement pde = new ProfileDataElement(fixedParam.getName(), Math.log10(fixedParam.getInitialGuess()), iniError, newBestParameters);
				profileData.addElement(pde);
				//increase
				int iterationCount = 1;
				double paramLogVal = Math.log10(fixedParam.getInitialGuess());
				double lastError = iniError;
				boolean isBoundReached = false;
				double incrementStep = FRAPOptData.DEFAULT_CI_STEPS_OFF_RATE[resultDataCounter];
				int stepIncreaseCount = 0;
				while(true)
				{
					if(iterationCount > FRAPOptData.MAX_ITERATION)//if exceeds the maximum iterations, break;
					{
						break;
					}
					if(isBoundReached)
					{
						break;
					}
					paramLogVal = paramLogVal + incrementStep ;
					double paramVal = Math.pow(10,paramLogVal);
					if(paramVal > (fixedParam.getUpperBound() - FRAPOptimizationUtils.epsilon))
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
					Parameter[] newParameters = getBestParamters(frapStudy.getFrapData(), increasedParam, true);
					double error = getOffRateResults().getObjectiveFunctionValue();
					pde = new ProfileDataElement(increasedParam.getName(), paramLogVal, error, newParameters);
					profileData.addElement(pde);
					//check if the we run enough to get confidence intervals(99% @6.635, we plus 10 over the min error)
					if(error > (iniError+10))
					{
						break;
					}
					if(Math.abs((error-lastError)/lastError) < FRAPOptData.MIN_LIKELIHOOD_CHANGE)
					{
						stepIncreaseCount ++;
						incrementStep = FRAPOptData.DEFAULT_CI_STEPS_OFF_RATE[resultDataCounter] * Math.pow(2, stepIncreaseCount);
					}
					else
					{
						if(stepIncreaseCount > 1)
						{
							incrementStep = FRAPOptData.DEFAULT_CI_STEPS_OFF_RATE[resultDataCounter] / Math.pow(2, stepIncreaseCount);
							stepIncreaseCount --;
						}
					}
					
					if (clientTaskStatusSupport.isInterrupted())
					{
						throw UserCancelException.CANCEL_GENERIC;
					}
	
					lastError = error;
					iterationCount++;
					clientTaskStatusSupport.setProgress((int)((iterationCount*1.0/FRAPOptData.MAX_ITERATION) * 0.5 * 100));
				}
				clientTaskStatusSupport.setProgress(50);//half way through evaluation of a parameter.
				//decrease
				iterationCount = 1;
				paramLogVal = Math.log10(fixedParam.getInitialGuess());
				lastError = iniError;
				isBoundReached = false;
				double decrementStep = FRAPOptData.DEFAULT_CI_STEPS_OFF_RATE[resultDataCounter];
				stepIncreaseCount = 0;
				while(true)
				{
					if(iterationCount > FRAPOptData.MAX_ITERATION)//if exceeds the maximum iterations, break;
					{
						break;
					}
					if(isBoundReached)
					{
						break;
					}
					paramLogVal = paramLogVal - decrementStep;
					double paramVal = Math.pow(10,paramLogVal);
//					System.out.println("paramVal:" + paramVal);
					if(paramVal < (fixedParam.getLowerBound() + FRAPOptimizationUtils.epsilon))
					{
						paramVal = fixedParam.getLowerBound();
						paramLogVal = Math.log10(FRAPOptimizationUtils.epsilon);
						isBoundReached = true;
					}
					Parameter decreasedParam = new Parameter (fixedParam.getName(),
		                                            fixedParam.getLowerBound(),
		                                            fixedParam.getUpperBound(),
		                                            fixedParam.getScale(),
		                                            paramVal);
					//getBestParameters returns the whole set of parameters including the fixed parameters
					Parameter[] newParameters = getBestParamters(frapStudy.getFrapData(), decreasedParam, true);
					double error = getOffRateResults().getObjectiveFunctionValue();
					pde = new ProfileDataElement(decreasedParam.getName(), paramLogVal, error, newParameters);
					profileData.addElement(0,pde);
					if(error > (iniError+10))
					{
						break;
					}
					if(Math.abs((error-lastError)/lastError) < FRAPOptData.MIN_LIKELIHOOD_CHANGE)
					{
						stepIncreaseCount ++;
						decrementStep = FRAPOptData.DEFAULT_CI_STEPS_OFF_RATE[resultDataCounter] * Math.pow(2, stepIncreaseCount);
					}
					else
					{
						if(stepIncreaseCount > 1)
						{
							incrementStep = FRAPOptData.DEFAULT_CI_STEPS_OFF_RATE[resultDataCounter] / Math.pow(2, stepIncreaseCount);
							stepIncreaseCount --;
						}
					}
					if (clientTaskStatusSupport.isInterrupted())
					{
						throw UserCancelException.CANCEL_GENERIC;
					}
					lastError = error;
					iterationCount++;
					clientTaskStatusSupport.setProgress((int)(((iterationCount+FRAPOptData.MAX_ITERATION)*1.0/FRAPOptData.MAX_ITERATION) * 0.5 * 100));
				}
				resultData[resultDataCounter++] = profileData;
				clientTaskStatusSupport.setProgress(100);//finish evaluation of a parameter
			}
			else
			{
				continue;
			}
		}
		//this message is specifically set for batchrun, the message will stay in the status panel. It doesn't affect single run,which disappears quickly that user won't notice.
		clientTaskStatusSupport.setMessage("Evaluating confidence intervals ...");
		return resultData;
	}
	
	public static void main(String argv[])
	{
		System.out.println(FUNC_RECOVERY_BLEACH_REACTION_DOMINANT);
		System.out.println(FUNC_CELL_INTENSITY);
	}
}
