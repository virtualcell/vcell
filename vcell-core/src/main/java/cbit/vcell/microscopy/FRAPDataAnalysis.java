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


import java.io.IOException;

import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.microscopy.server.FrapDataUtils;
import cbit.vcell.opt.ElementWeights;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

/**
 */
public class FRAPDataAnalysis {

	public final static String symbol_u = "u";
	public final static String symbol_fB = "fB";
	public final static String symbol_r = "r";
	public final static String symbol_tau = "tau";
	public final static String symbol_Pi = "Pi";
	public final static String symbol_w = "w";
	public final static String symbol_bwmRate = "monitorBleachRate";
	public final static String symbol_preBlchAvg = "preBlchAvg";
	
	public final static Parameter para_Ii = new Parameter("Ii", -1, 1, 1.0, 0.0); 
	public final static Parameter para_A = new Parameter("A", 0.1, 4, 1.0, 1.0); 
//	public final static Parameter para_CicularDisk_Tau = new cbit.vcell.opt.Parameter("Tau",0.1, 50.0, 1.0, 1.0);
	public final static Parameter para_If = new Parameter("If", 0, 1, 1.0, 0.9);
	public final static Parameter para_Io = new Parameter("Io", 0, 1, 1.0, 0.1);
	public final static Parameter para_tau = new Parameter("tau", 0.1, 50.0, 1.0, 1.0);
//	public final static Parameter para_R = new cbit.vcell.opt.Parameter("R", 0.01, 1.0, 1.0, 0.1);
	
	public final static String circularDisk_IntensityFunc = "Ii + A*(1-exp(-t/"+symbol_tau+"))";
	public final static String circularDisk_IntensityFunc_display = "If-(If-Io)*exp(-t/"+symbol_tau+")";
	public final static String circularDisk_DiffFunc = symbol_w+"^2/(4*"+symbol_tau+")";
	public final static String gaussianSpot_IntensityFunc = "(Io+(If-Io)*(1-"+symbol_u+"))*exp(-t*"+symbol_bwmRate+")";
	public final static String gaussianSpot_MuFunc = "(1+t/"+symbol_tau+")^-1";
	public final static String gaussianSpot_DiffFunc = symbol_w+"^2/(4*"+symbol_tau+")" ;
	public final static String gaussianSpot_mobileFracFunc = "(If-Io)/(("+symbol_preBlchAvg+"-Io)*(1-"+symbol_fB+"))";
	public final static String halfCell_IntensityFunc = "(Io+(If-Io)*(1-"+symbol_u+"))*exp(-t*"+symbol_bwmRate+")";
	public final static String halfCell_MuFunc = "exp(-t/"+symbol_tau+")";
	public final static String halfCell_DiffFunc = symbol_r+"^2/("+symbol_tau+"*"+symbol_Pi+"^2)";
	public final static String halfCell_mobileFracFunc = "(If-Io)/(("+symbol_preBlchAvg+"-Io)*(1-"+symbol_fB+"))";
	
	public final static double THRESHOLD_BLEACH_TYPE = 0.4;
	
	public static double[] getAverageROIIntensity(FRAPData frapData, ROI roi, double[] normFactor,double[] preNormalizeOffset) {
		if(frapData == null)
		{
			throw new RuntimeException("FRAP data is null. Image data set must be loaded before conducting any analysis.");
		}
		if (frapData.getImageDataset().getSizeC()>1){
			throw new RuntimeException("FRAPDataAnalysis.getAverageROIIntensity(): multiple image channels not supported");
		}
		double[] averageROIIntensity = new double[frapData.getImageDataset().getSizeT()];
		
		for (int tIndex = 0; tIndex < averageROIIntensity.length; tIndex++) {
			averageROIIntensity[tIndex] = frapData.getAverageUnderROI(tIndex, roi, normFactor,(preNormalizeOffset == null?0:preNormalizeOffset[tIndex]));
		}

		return averageROIIntensity;
	}
	
	/**
	 * Method fitRecovery2.
	 * @param frapData, the original image info.
	 * @param arg_bleachType, the gaussian spot or half cell bleaching types.
	 * @return FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults
	 * @throws ExpressionException
	 */
	public static FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults fitRecovery_diffusionOnly(FRAPData frapData, int arg_bleachType,int startIndexForRecovery) throws ExpressionException, OptimizationException, IOException, IllegalArgumentException
	{
		
//		int startIndexForRecovery = getRecoveryIndex(frapData);
		//
		// get unnormalized average background fluorescence at each time point
		//
		double[] temp_background = frapData.getAvgBackGroundIntensity();
		//the prebleachAvg has backgroud subtracted.
		double[] preBleachAvgXYZ = FrapDataUtils.calculatePreBleachAverageXYZ(frapData, startIndexForRecovery);
		//temp_fluor has subtracted background and divided by prebleach average.
		double[] temp_fluor = getAverageROIIntensity(frapData,frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()),preBleachAvgXYZ,temp_background); //get average intensity under the bleached area according to each time point
		double[] temp_time = frapData.getImageDataset().getImageTimeStamps();

		//get nomalized preBleachAverage under bleached area.
		double preBleachAverage_bleachedArea = 0.0;
		for (int i = 0; i < startIndexForRecovery; i++) {
			preBleachAverage_bleachedArea += temp_fluor[i];
		}
		preBleachAverage_bleachedArea /= startIndexForRecovery;

		
		//get number of pixels in bleached region(non ROI region pixels are saved as 0)
		ROI bleachedROI_2D = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		long numPixelsInBleachedROI = bleachedROI_2D.getRoiImages()[0].getNumXYZ() - bleachedROI_2D.getRoiImages()[0].countPixelsByValue((short)0);
		// assume ROI is a circle, A = Pi*R^2
		// so R = sqrt(A/Pi)
		double imagePixelArea = frapData.getImageDataset().getAllImages()[0].getPixelAreaXY();
		double area = imagePixelArea * numPixelsInBleachedROI;
		double bleachRadius = Math.sqrt(area/Math.PI);// Radius of ROI(assume that ROI is a circle)
		// assume cell is a circle, A = Pi*R^2
		// so R = sqrt(A/Pi)
		area = frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getRoiImages()[0].getPixelAreaXY() * (frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getRoiImages()[0].getNumXYZ()- bleachedROI_2D.getRoiImages()[0].countPixelsByValue((short)0));
		double cellRadius = Math.sqrt(area/Math.PI);// Radius of ROI(assume that ROI is a circle)
		
		
		//average intensity under bleached area. The time points start from the first post bleach
		double[] fluor = new double[temp_fluor.length-startIndexForRecovery];
		//Time points stat from the first post bleach
		double[] time = new double[temp_time.length-startIndexForRecovery];
		System.arraycopy(temp_fluor, startIndexForRecovery, fluor, 0, fluor.length);
		System.arraycopy(temp_time, startIndexForRecovery, time, 0, time.length);
		
		FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults diffAnalysisResults = new FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults();
//		frapDataAnalysisResults.setBleachWidth(bleachRadius);
//		frapDataAnalysisResults.setStartingIndexForRecovery(startIndexForRecovery);
//		frapDataAnalysisResults.setBleachRegionData(temp_fluor);// average intensity under bleached region accroding to time points
		diffAnalysisResults.setBleachType(arg_bleachType);
		//curve fitting according to different bleach types
		double[] inputParamValues = null;
		double[] outputParamValues = null;
		
		//
		//Bleach while monitoring fit
		//
		double[] tempCellROIAverage = getAverageROIIntensity(frapData,frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()),preBleachAvgXYZ,temp_background);
		double[] cellROIAverage = new double[tempCellROIAverage.length-startIndexForRecovery];
		//Cell Avg. points start from the first post bleach
		System.arraycopy(tempCellROIAverage, startIndexForRecovery, cellROIAverage, 0, cellROIAverage.length);

		outputParamValues = new double[1];
		Expression bleachWhileMonitorFitExpression = CurveFitting.fitBleachWhileMonitoring(time, cellROIAverage, outputParamValues);
		double bleachWhileMonitoringRate = outputParamValues[0];
		diffAnalysisResults.setBleachWhileMonitoringTau(bleachWhileMonitoringRate);
		diffAnalysisResults.setFitBleachWhileMonitorExpression(bleachWhileMonitorFitExpression.flatten());
		//to fit If, Io and recovery tau by fitting expressions based on bleaching types
		if(arg_bleachType == FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults.BleachType_GaussianSpot)
		{
			double cellAreaBleached = getCellAreaBleachedFraction(frapData);
			inputParamValues = new double[]{bleachWhileMonitoringRate}; // the input parameter is the bleach while monitoring rate
			outputParamValues = new double[3];// the array is used to get If, Io, and tau back.
			Expression fittedCurve = CurveFitting.fitRecovery_diffOnly(time, fluor, FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults.BleachType_GaussianSpot, inputParamValues, outputParamValues);
			//get diffusion rate
			double fittedRecoveryTau = outputParamValues[2];
			Expression diffExp = new Expression(FRAPDataAnalysis.gaussianSpot_DiffFunc);
			diffExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_w), new Expression(bleachRadius));
			diffExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_tau), new Expression(fittedRecoveryTau));
			double fittedDiffusionRate =  diffExp.evaluateConstant();
//			double fittedDiffusionRate = bleachRadius*bleachRadius/(4.0*fittedRecoveryTau);
			//get mobile fraction
			double If = outputParamValues[0];
			double Io = outputParamValues[1];
			Expression mFracExp = new Expression(FRAPDataAnalysis.gaussianSpot_mobileFracFunc);
			mFracExp.substituteInPlace(new Expression(FRAPDataAnalysis.para_If.getName()), new Expression(If));
			mFracExp.substituteInPlace(new Expression(FRAPDataAnalysis.para_Io.getName()), new Expression(Io));
			mFracExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_preBlchAvg), new Expression(preBleachAverage_bleachedArea));
			mFracExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_fB), new Expression(cellAreaBleached));
			double mobileFrac = mFracExp.evaluateConstant();
			//sometimes the mobile fraction goes beyond [0,1], we have to restrict the mobile fraction value. 
			mobileFrac = Math.min(1,mobileFrac);
			mobileFrac = Math.max(0, mobileFrac);
			//set frap data analysis results
			diffAnalysisResults.setDiffFitExpression(fittedCurve.flatten());
			diffAnalysisResults.setRecoveryTau(fittedRecoveryTau);
			diffAnalysisResults.setRecoveryDiffusionRate(fittedDiffusionRate);
			diffAnalysisResults.setMobilefraction(mobileFrac);
		}
		else if(arg_bleachType == FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults.BleachType_HalfCell)
		{
			
			double cellAreaBleached = getCellAreaBleachedFraction(frapData);
			inputParamValues = new double[]{bleachWhileMonitoringRate}; // the input parameter is the bleach while monitoring rate
			outputParamValues = new double[3];// the array is used get If, Io, and tau back.
			Expression fittedCurve = CurveFitting.fitRecovery_diffOnly(time, fluor, FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults.BleachType_HalfCell, inputParamValues, outputParamValues);
			//get diffusion rate
			double fittedRecoveryTau = outputParamValues[2];
			Expression diffExp = new Expression(FRAPDataAnalysis.halfCell_DiffFunc);
			diffExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_r), new Expression(cellRadius));
			diffExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_tau), new Expression(fittedRecoveryTau));
			diffExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_Pi), new Expression(Math.PI));
			double fittedDiffusionRate = diffExp.evaluateConstant();
//			double fittedDiffusionRate = (cellRadius*cellRadius)/(fittedRecoveryTau*Math.PI*Math.PI);
			//get mobile fraction
			double If = outputParamValues[0];
			double Io = outputParamValues[1];
			Expression mFracExp = new Expression(FRAPDataAnalysis.gaussianSpot_mobileFracFunc);
			mFracExp.substituteInPlace(new Expression(FRAPDataAnalysis.para_If.getName()), new Expression(If));
			mFracExp.substituteInPlace(new Expression(FRAPDataAnalysis.para_Io.getName()), new Expression(Io));
			mFracExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_preBlchAvg), new Expression(preBleachAverage_bleachedArea));
			mFracExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_fB), new Expression(cellAreaBleached));
			double mobileFrac = mFracExp.evaluateConstant();
			//set diffusion only analysis results
			diffAnalysisResults.setDiffFitExpression(fittedCurve.flatten());
			diffAnalysisResults.setRecoveryTau(fittedRecoveryTau);
			diffAnalysisResults.setRecoveryDiffusionRate(fittedDiffusionRate);
			diffAnalysisResults.setMobilefraction(mobileFrac);
		}else{
			throw new IllegalArgumentException("Unknown Bleach Type "+arg_bleachType);
		}
		return diffAnalysisResults;
	}
	
	/**
	 * Method fitRecovery2.
	 * @param frapData, the original image info.
	 * @param fixedParameter: the fixed parameter from profile likelihood distribution analysis.
	 * @param measurementError: the measurementError is used as weights for calculating objectiveFunction errors.
	 * @return FrapDataAnalysisResults.ReactionOnlyAnalysisRestults
	 * @throws ExpressionException
	 */
	public static FrapDataAnalysisResults.ReactionOnlyAnalysisRestults fitRecovery_reacOffRateOnly(FRAPData frapData, Parameter fixedParam, double[][] measurementError,int startIndexForRecovery) throws ExpressionException, OptimizationException, IOException
	{
		
//		int startIndexForRecovery = getRecoveryIndex(frapData);
		//
		// get unnormalized average background fluorescence at each time point
		//
		double[] temp_background = frapData.getAvgBackGroundIntensity();
		//the prebleachAvg has backgroud subtracted.
		double[] preBleachAvgXYZ = FrapDataUtils.calculatePreBleachAverageXYZ(frapData, startIndexForRecovery);
		//tempBeachedAverage and tempCellROIAverage have subtracted background and divided by prebleach average. the following array has data for the full time duration.
		double[] tempBeachedAverage = getAverageROIIntensity(frapData,frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()),preBleachAvgXYZ,temp_background); 
		double[] tempCellROIAverage = getAverageROIIntensity(frapData,frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()),preBleachAvgXYZ,temp_background);
		double[] temp_time = frapData.getImageDataset().getImageTimeStamps();

		//get bleached and cell roi data starting from first post bleach
		double[] bleachedAverage = new double[tempBeachedAverage.length-startIndexForRecovery];//The time points start from the first post bleach
		double[] cellROIAverage = new double[tempCellROIAverage.length-startIndexForRecovery];//time points start from the first post bleach
		double[] time = new double[temp_time.length-startIndexForRecovery]; //Time points stat from the first post bleach
		System.arraycopy(tempBeachedAverage, startIndexForRecovery, bleachedAverage, 0, bleachedAverage.length);
		System.arraycopy(tempCellROIAverage, startIndexForRecovery, cellROIAverage, 0, cellROIAverage.length);
		System.arraycopy(temp_time, startIndexForRecovery, time, 0, time.length);
		
		//initialize reaction off rate analysis results
		FrapDataAnalysisResults.ReactionOnlyAnalysisRestults offRateAnalysisResults = new FrapDataAnalysisResults.ReactionOnlyAnalysisRestults();

		/**curve fitting*/ 
		//index 0: cell ROI intensity average at time 0, I_cell_ini. index 1: bleached intensity average at time 0, I_bleached_ini
		double[] inputParamValues = new double[]{cellROIAverage[0], bleachedAverage[0]};
		double[] outputParamValues = null; // if fixed parameter is null, then outputs are bwm rate, koff rate, fitting parameter A. Otherwise outputs are two out of three.
		//create data array,first col is cell average, second col is bleached average. 
		double[][] fitData = new double[2][];
		fitData[0] = cellROIAverage;
		fitData[1] = bleachedAverage;
		//create element weights array, first col is cell data weights, second col is bleached data weights
		double[][] weightData = new double[time.length][2];
		double[] cellROIMeasurementError = measurementError[FRAPData.VFRAP_ROI_ENUM.ROI_CELL.ordinal()];
		double[] bleachedROIMeasurementError = measurementError[FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.ordinal()];
		for(int i=0; i<time.length; i++)//elementWeight, first dimension is number of rows(time points), second dimension is number of variables(fitDatasets)
		{
			weightData[i][0] = 1/(cellROIMeasurementError[i]* cellROIMeasurementError[i]);
			weightData[i][1] = 1/(bleachedROIMeasurementError[i]* bleachedROIMeasurementError[i]);
		}
		ElementWeights eleWeights = new ElementWeights(weightData);
		if(fixedParam == null)
		{
			//call curvefitting
			outputParamValues = new double[3]; //bwmrate, fitting parameter A & reaction off rate
			double error = CurveFitting.fitRecovery_reacKoffRateOnly(time, fitData, inputParamValues, outputParamValues, fixedParam, eleWeights);
			//set objective function value
			offRateAnalysisResults.setObjectiveFunctionValue(error);
			//set reaction off rate analysis results
			offRateAnalysisResults.setBleachWhileMonitoringTau(outputParamValues[0]);
			offRateAnalysisResults.setFittingParamA(outputParamValues[1]);
			offRateAnalysisResults.setOffRate(outputParamValues[2]);
			//set cell intensity expression ( only t left in expression)
			Expression cellIntensityExp = new Expression(FRAPOptFunctions.FUNC_CELL_INTENSITY);
			cellIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_I_inicell), new Expression(cellROIAverage[0]));
			cellIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(outputParamValues[0]));//subsitute bwmRate
			cellIntensityExp.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(ReservedVariable.TIME.getName()+"-"+time[0]));//undo time shift
			offRateAnalysisResults.setFitBleachWhileMonitorExpression(cellIntensityExp);
			//set bleached region intensity expression ( only t left in expression)
			Expression bleachIntensityExp = new Expression(FRAPOptFunctions.FUNC_RECOVERY_BLEACH_REACTION_DOMINANT);
			bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_I_inibleached), new Expression(bleachedAverage[0]));
			bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(outputParamValues[0]));//subsitute bwmRate
			bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_A), new Expression(outputParamValues[1]));//subsitute parameter A
			bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_KOFF), new Expression(outputParamValues[2]));//reaction off rate
			bleachIntensityExp.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(ReservedVariable.TIME.getName()+"-"+time[0]));//undo time shift
			offRateAnalysisResults.setOffRateFitExpression(bleachIntensityExp);
		}
		else
		{
			if(fixedParam != null && fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
			{
				outputParamValues = new double[2];//fitting parameter A & reaction off rate
				double error = CurveFitting.fitRecovery_reacKoffRateOnly(time, fitData, inputParamValues, outputParamValues, fixedParam, eleWeights);
				//set objective function value
				offRateAnalysisResults.setObjectiveFunctionValue(error);
				//set reaction off rate analysis results
				offRateAnalysisResults.setBleachWhileMonitoringTau(fixedParam.getInitialGuess());
				offRateAnalysisResults.setFittingParamA(outputParamValues[0]);
				offRateAnalysisResults.setOffRate(outputParamValues[1]);
				//set cell intensity expression ( only t left in expression)
				Expression cellIntensityExp = new Expression(FRAPOptFunctions.FUNC_CELL_INTENSITY);
				cellIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_I_inicell), new Expression(cellROIAverage[0]));
				cellIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(fixedParam.getInitialGuess()));//subsitute bwmRate
				cellIntensityExp.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(ReservedVariable.TIME.getName()+"-"+time[0]));//undo time shift
				offRateAnalysisResults.setFitBleachWhileMonitorExpression(cellIntensityExp);
				//set bleached region intensity expression ( only t left in expression)
				Expression bleachIntensityExp = new Expression(FRAPOptFunctions.FUNC_RECOVERY_BLEACH_REACTION_DOMINANT);
				bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_I_inibleached), new Expression(bleachedAverage[0]));
				bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(fixedParam.getInitialGuess()));//subsitute bwmRate
				bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_A), new Expression(outputParamValues[0]));//subsitute parameter A
				bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_KOFF), new Expression(outputParamValues[1]));//reaction off rate
				bleachIntensityExp.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(ReservedVariable.TIME.getName()+"-"+time[0]));//undo time shift
				offRateAnalysisResults.setOffRateFitExpression(bleachIntensityExp);
			}
			else if(fixedParam != null && fixedParam.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE]))
			{
				outputParamValues = new double[2];//bwmRate & fitting parameter A
				double error = CurveFitting.fitRecovery_reacKoffRateOnly(time, fitData, inputParamValues, outputParamValues, fixedParam, eleWeights);
				//set objective function value
				offRateAnalysisResults.setObjectiveFunctionValue(error);
				//set reaction off rate analysis results
				offRateAnalysisResults.setBleachWhileMonitoringTau(outputParamValues[0]);
				offRateAnalysisResults.setFittingParamA(outputParamValues[1]);
				offRateAnalysisResults.setOffRate(fixedParam.getInitialGuess());
				//set cell intensity expression ( only t left in expression)
				Expression cellIntensityExp = new Expression(FRAPOptFunctions.FUNC_CELL_INTENSITY);
				cellIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_I_inicell), new Expression(cellROIAverage[0]));
				cellIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(outputParamValues[0]));//subsitute bwmRate
				cellIntensityExp.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(ReservedVariable.TIME.getName()+"-"+time[0]));//undo time shift
				offRateAnalysisResults.setFitBleachWhileMonitorExpression(cellIntensityExp);
				//set bleached region intensity expression ( only t left in expression)
				Expression bleachIntensityExp = new Expression(FRAPOptFunctions.FUNC_RECOVERY_BLEACH_REACTION_DOMINANT);
				bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_I_inibleached), new Expression(bleachedAverage[0]));
				bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(outputParamValues[0]));//subsitute bwmRate
				bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_A), new Expression(outputParamValues[1]));//subsitute parameter A
				bleachIntensityExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_KOFF), new Expression(fixedParam.getInitialGuess()));//reaction off rate
				bleachIntensityExp.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(ReservedVariable.TIME.getName()+"-"+time[0]));//undo time shift
				offRateAnalysisResults.setOffRateFitExpression(bleachIntensityExp);
			}
			else
			{
				throw new OptimizationException("Unknown fixed parameter:" + fixedParam.getName());
			}
		}
		
		return offRateAnalysisResults;
	}
	
	public static int calculateRecoveryIndex(FRAPData frapData)
	{
		double[] temp_fluor = getAverageROIIntensity(frapData,frapData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()),null,null);
		
		int startIndexForRecovery = 0;
		double minFluorValue = Double.MAX_VALUE;
		for (int i = 0; i < temp_fluor.length; i++) {
			if (minFluorValue>temp_fluor[i]){
				minFluorValue = temp_fluor[i];
				startIndexForRecovery = i;
			}
		}
		return startIndexForRecovery;
	}
	
	
	
	
//	/**
//	 * Method main.
//	 * @param args String[]
//	 */
//	public static void main(String args[]){
//		try {
//			//ImageDataset imageDataset = VirtualFrapTest.readImageDataset("\\\\fs3\\ccam\\Public\\VirtualMicroscopy\\FRAP data from Ann\\ran bleach in nucleus\\bleach3.lsm");
//			ImageDataset imageDataset = ImageDatasetReader.readImageDataset("\\temp\\lsm\\bleach3.lsm", null);
//			ImageDataset roiImageDataset = ImageDatasetReader.readImageDataset("\\temp\\lsm\\BleachingRegionMask.tif", null);
//			if (roiImageDataset.getSizeC()>0 || roiImageDataset.getSizeT()>0){
//				throw new RuntimeException("roi data must be single channel and single time");
//			}
//			ROI[] rois = new ROI[] { new ROI(roiImageDataset.getAllImages(),RoiType.ROI_BLEACHED) };
//			FRAPData frapData = new FRAPData(imageDataset, rois);
//			WindowListener windowListener = new WindowAdapter(){
//				@Override
//				public void windowClosing(java.awt.event.WindowEvent e) {
//					System.exit(0);
//				};
//			};
//			double[] roiRecovery = FRAPDataAnalysis.getAverageROIIntensity(frapData,RoiType.ROI_BLEACHED);
//			double[] fakeCurveData = new double[roiRecovery.length];
//			for (int i = 0; i < fakeCurveData.length; i++) {
//				fakeCurveData[i] = 100*Math.sin(i/10.0);
//			}
//			FRAPDataPanel.showCurve(windowListener, new String[] { "f", "fakeCurve" }, frapData.getImageDataset().getImageTimeStamps(),new double[][] { roiRecovery, fakeCurveData });
//		}catch (Exception e){
//			e.printStackTrace(System.out);
//		}
//	}
	/*
	 * Added to try the new function
	 */
	public static double getCellAreaBleachedFraction(FRAPData fdata)
	{
		ROI bleachedROI = fdata.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name());
		ROI cellROI = fdata.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
		double fraction = 1;
		double bleachedLen = 0;
		double cellLen = 0;
		for(int i =0; i < cellROI.getRoiImages()[0].getPixels().length; i++ )//bleachedROI and cellROI images have same number of pixels
		{
			if(cellROI.getRoiImages()[0].getPixels()[i] != 0)
			{
				cellLen ++;
			}
			if(bleachedROI.getRoiImages()[0].getPixels()[i] != 0)
			{
				bleachedLen ++;
			}
		}
		if(cellLen > 0)
		{
			fraction = bleachedLen/cellLen;
		}
		
		return fraction;		
	}
}
