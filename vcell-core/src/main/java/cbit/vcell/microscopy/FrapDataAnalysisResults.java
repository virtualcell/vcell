/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package cbit.vcell.microscopy;

import cbit.vcell.parser.Expression;

/**
 */
public class FrapDataAnalysisResults /*implements Matchable*/ 
{
	//variable below is declared to check if parameters are within their bounds. Used by the static method validateParameter()
	public static final double boundTolerance = 0.01;//if the estimated parameter is out of bounds but within 1% of the violation, we will clamp it to the bounds
	public static final int WITHIN_BOUNDS = 0;
	public static final int LOWER_BOUND_CLAMPED = 1;//exceed lower bound but within tolerance
	public static final int UPPER_BOUND_CLAMPED = 2;//exceed upper bound but within tolerance
	public static final int EXCEED_LOWER_BOUND = 3;
	public static final int EXCEED_UPPER_BOUND = 4;
	
	
	//nested class
	//for diffusion only, explicit expression fitting
	public static class DiffusionOnlyAnalysisRestults 
	{
		//public static int BleachType_CirularDisk = 0;
		public static int BleachType_GaussianSpot = 0;
		public static int BleachType_HalfCell = 1;
		public static final String[] BLEACH_TYPE_NAMES = new String[] {/*"Circular Disk",*/"Gaussian Spot","Half Cell"};
		
		private Double recoveryTau = null;
		private Double bleachWhileMonitoringTau = null;
		private Double recoveryDiffusionRate = null;
		private Double mobilefraction = null;
		private Integer bleachType = null;
		private Expression diffFitExpression = null;
		private Expression fitBleachWhileMonitorExpression = null;
		
		public static final int getBleachTypeFromBleachTypeName(String bleachTypeName){
			for (int i = 0; i < BLEACH_TYPE_NAMES.length; i++) {
				if(BLEACH_TYPE_NAMES[i].equals(bleachTypeName)){
					return i;
				}
			}
			throw new IllegalArgumentException("Unknown bleach type name "+bleachTypeName);
		}
		
		public Double getBleachWhileMonitoringTau() {
			return bleachWhileMonitoringTau;
		}

		public void setBleachWhileMonitoringTau(Double bleachWhileMonitoringTau) {
			if(bleachWhileMonitoringTau != null)
			{
				double low = FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound();
				double high = FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound();
				int returnCode = FrapDataAnalysisResults.validateParameter(bleachWhileMonitoringTau, low, high);
								
				if(returnCode == WITHIN_BOUNDS)
				{
					this.bleachWhileMonitoringTau = bleachWhileMonitoringTau;
				}
				else if(returnCode == LOWER_BOUND_CLAMPED)
				{
					this.bleachWhileMonitoringTau = low;
				}
				else if(returnCode == UPPER_BOUND_CLAMPED)
				{
					this.bleachWhileMonitoringTau = high;
				}
				else if(returnCode == EXCEED_LOWER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + bleachWhileMonitoringTau + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE] + "' is below lower bound("+ low +").");
				}
				else if(returnCode == EXCEED_UPPER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + bleachWhileMonitoringTau + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE] + "' is above upper bound("+ high +").");
				}
			}
			else
			{
				this.bleachWhileMonitoringTau = null;
			}
		}

		public Double getRecoveryDiffusionRate() {
			return recoveryDiffusionRate;
		}

		public void setRecoveryDiffusionRate(Double recoveryDiffusionRate) {
			if(recoveryDiffusionRate != null)
			{
				double low = FRAPModel.REF_DIFFUSION_RATE_PARAM.getLowerBound();
				double high = FRAPModel.REF_DIFFUSION_RATE_PARAM.getUpperBound();
				int returnCode = FrapDataAnalysisResults.validateParameter(recoveryDiffusionRate, low, high);
								
				if(returnCode == WITHIN_BOUNDS)
				{
					this.recoveryDiffusionRate = recoveryDiffusionRate;
				}
				else if(returnCode == LOWER_BOUND_CLAMPED)
				{
					this.recoveryDiffusionRate = low;
				}
				else if(returnCode == UPPER_BOUND_CLAMPED)
				{
					this.recoveryDiffusionRate = high;
				}
				else if(returnCode == EXCEED_LOWER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + recoveryDiffusionRate + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE] + "' is below lower bound("+ low +").");
				}
				else if(returnCode == EXCEED_UPPER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + recoveryDiffusionRate + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE] + "' is above upper bound("+ high +").");
				}
			}
			else
			{
				this.recoveryDiffusionRate = null;
			}
		}

		public Double getRecoveryTau() {
			return recoveryTau;
		}

		void setRecoveryTau(Double recoveryTau) {
			this.recoveryTau = recoveryTau;
		}

		public Double getMobilefraction() {
			return mobilefraction;
		}

		public void setMobilefraction(Double mobilefraction) {
			if(mobilefraction != null)
			{
				double low = FRAPModel.REF_MOBILE_FRACTION_PARAM.getLowerBound();
				double high = FRAPModel.REF_MOBILE_FRACTION_PARAM.getUpperBound();
				int returnCode = FrapDataAnalysisResults.validateParameter(mobilefraction, low, high);
								
				if(returnCode == WITHIN_BOUNDS)
				{
					this.mobilefraction = mobilefraction;
				}
				else if(returnCode == LOWER_BOUND_CLAMPED)
				{
					this.mobilefraction = low;
				}
				else if(returnCode == UPPER_BOUND_CLAMPED)
				{
					this.mobilefraction = high;
				}
				else if(returnCode == EXCEED_LOWER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + mobilefraction + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION] + "' is below lower bound("+ low +").");
				}
				else if(returnCode == EXCEED_UPPER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + mobilefraction + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION] + "' is above upper bound("+ high +").");
				}
			}
			else
			{
				this.mobilefraction = null;
			}
		}

		public Integer getBleachType() {
			return bleachType;
		}

		public void setBleachType(Integer bleachType) {
			this.bleachType = bleachType;
		}

		public Expression getFitBleachWhileMonitorExpression() {
			return fitBleachWhileMonitorExpression;
		}

		public void setFitBleachWhileMonitorExpression(
				Expression fitBleachWhileMonitorExpression) {
			this.fitBleachWhileMonitorExpression = fitBleachWhileMonitorExpression;
		}
		
		public Expression getDiffFitExpression() {
			return diffFitExpression;
		}

		public void setDiffFitExpression(Expression fitExpression) {
			this.diffFitExpression = fitExpression;
		}
	}
	//nested class
	//for reaction only, explicit expressions fitting
	public static class ReactionOnlyAnalysisRestults 
	{
		private Double fittingParamA = null;
		private Double offRate = null;
		private Expression offRateFitExpression = null;
		private Double bleachWhileMonitoringTau = null;
		private Expression fitBleachWhileMonitorExpression = null;
		private Double objFuncValue = null;
		
		public Double getObjectiveFunctionValue() {
			return objFuncValue;
		}

		public void setObjectiveFunctionValue(Double objFuncValue) {
			this.objFuncValue = objFuncValue;
		}

		public Double getBleachWhileMonitoringTau() {
			return bleachWhileMonitoringTau;
		}

		public void setBleachWhileMonitoringTau(Double bleachWhileMonitoringTau) {
			if(bleachWhileMonitoringTau != null)
			{
				double low = FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound();
				double high = FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound();
				int returnCode = FrapDataAnalysisResults.validateParameter(bleachWhileMonitoringTau, low, high);
								
				if(returnCode == WITHIN_BOUNDS)
				{
					this.bleachWhileMonitoringTau = bleachWhileMonitoringTau;
				}
				else if(returnCode == LOWER_BOUND_CLAMPED)
				{
					this.bleachWhileMonitoringTau = low;
				}
				else if(returnCode == UPPER_BOUND_CLAMPED)
				{
					this.bleachWhileMonitoringTau = high;
				}
				else if(returnCode == EXCEED_LOWER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + bleachWhileMonitoringTau + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE] + "' is below lower bound("+ low +").");
				}
				else if(returnCode == EXCEED_UPPER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + bleachWhileMonitoringTau + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE] + "' is above upper bound("+ high +").");
				}
			}
			else
			{
				this.bleachWhileMonitoringTau = null;
			}
		}

		public Expression getFitBleachWhileMonitorExpression() {
			return fitBleachWhileMonitorExpression;
		}

		public void setFitBleachWhileMonitorExpression(
				Expression fitBleachWhileMonitorExpression) {
			this.fitBleachWhileMonitorExpression = fitBleachWhileMonitorExpression;
		}
		
		public Expression getOffRateFitExpression() {
			return offRateFitExpression;
		}

		public void setOffRateFitExpression(Expression offRateFitExpression) {
			this.offRateFitExpression = offRateFitExpression;
		}
		public Double getFittingParamA() {
			return fittingParamA;
		}
		//we don't check validity of the fitting parameterA, it's just a parameter that will be thrown away after fitting.
		public void setFittingParamA(Double fittingParamA) {
			this.fittingParamA = fittingParamA;
		}

		public Double getOffRate() {
			return offRate;
		}

		public void setOffRate(Double offRate) 
		{
			if(offRate != null)
			{
				double low = FRAPModel.REF_REACTION_OFF_RATE.getLowerBound();
				double high = FRAPModel.REF_REACTION_OFF_RATE.getUpperBound();
				int returnCode = FrapDataAnalysisResults.validateParameter(offRate, low, high);
								
				if(returnCode == WITHIN_BOUNDS)
				{
					this.offRate = offRate;
				}
				else if(returnCode == LOWER_BOUND_CLAMPED)
				{
					this.offRate = low;
				}
				else if(returnCode == UPPER_BOUND_CLAMPED)
				{
					this.offRate = high;
				}
				else if(returnCode == EXCEED_LOWER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + offRate + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE] + "' is below lower bound("+ low +").");
				}
				else if(returnCode == EXCEED_UPPER_BOUND)
				{
					throw new RuntimeException("Estimated value(" + offRate + ") for parameter '" + FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE] + "' is above upper bound("+ high +").");
				}
			}
			else
			{
				this.offRate = null;
			}
		}
		
	}
	
	public static int validateParameter(double paramVal, double low, double high)
	{
		if(paramVal < low)
		{
			double deviation = 0;
			
			if(low == 0)
			{
				deviation = Math.abs(paramVal - low);
			}
			else
			{
				deviation = Math.abs((paramVal - low)/low);
			}
			if(deviation <= boundTolerance)
			{
				return LOWER_BOUND_CLAMPED;
			}
			else
			{
				return EXCEED_LOWER_BOUND;
			}
		}
		else if(paramVal > high)
		{
			double deviation = 0;
			
			if(high == 0)
			{
				deviation = Math.abs(paramVal - high);
			}
			else
			{
				deviation = Math.abs((paramVal - high)/high);
			}
			if(deviation <= boundTolerance)
			{
				return UPPER_BOUND_CLAMPED;
			}
			else
			{
				return EXCEED_UPPER_BOUND;
			}
		}
		return WITHIN_BOUNDS;
	}

	
	
//	public boolean compareEqual(Matchable obj) {
//		
//		if (this == obj) {
//			return true;
//		}
//		if (obj != null && obj instanceof FrapDataAnalysisResults)
//		{
//			FrapDataAnalysisResults fdar = (FrapDataAnalysisResults)obj;
//			if((getRecoveryTau()!= null && fdar.getRecoveryTau() == null) || (getRecoveryTau()== null && fdar.getRecoveryTau() != null))
//			{
//				return false;
//			}
//			if(getRecoveryTau()!= null && fdar.getRecoveryTau()!= null && getRecoveryTau().doubleValue() != fdar.getRecoveryTau().doubleValue())
//			{
//				return false;
//			}
//			if((getBleachWhileMonitoringTau()!= null && fdar.getBleachWhileMonitoringTau() == null) || (getBleachWhileMonitoringTau()== null && fdar.getBleachWhileMonitoringTau() != null))
//			{
//				return false;
//			}
//			if(getBleachWhileMonitoringTau()!= null && fdar.getBleachWhileMonitoringTau()!= null && getBleachWhileMonitoringTau().doubleValue() != fdar.getBleachWhileMonitoringTau().doubleValue())
//			{
//				return false;
//			}
//			if((getRecoveryDiffusionRate()!= null && fdar.getRecoveryDiffusionRate() == null) || (getRecoveryDiffusionRate()== null && fdar.getRecoveryDiffusionRate() != null))
//			{
//				return false;
//			}
//			if(getRecoveryDiffusionRate()!= null && fdar.getRecoveryDiffusionRate()!= null && getRecoveryDiffusionRate().doubleValue() != fdar.getRecoveryDiffusionRate().doubleValue())
//			{
//				return false;
//			}
//			if((getMobilefraction()!= null && fdar.getMobilefraction() == null) || (getMobilefraction()== null && fdar.getMobilefraction() != null))
//			{
//				return false;
//			}
//			if(getMobilefraction()!= null && fdar.getMobilefraction()!= null && getMobilefraction().doubleValue() != fdar.getMobilefraction().doubleValue())
//			{
//				return false;
//			}
//			if(!Compare.isEqualOrNull(getDiffFitExpression(), fdar.getDiffFitExpression()))
//			{
//				return false;
//			}
//			if(!Compare.isEqualOrNull(getFitBleachWhileMonitorExpression(), fdar.getFitBleachWhileMonitorExpression())){
//				return false;
//			}
//			if((getBleachType()!= null && fdar.getBleachType() == null) || (getBleachType()== null && fdar.getBleachType() != null))
//			{
//				return false;
//			}
//			if(getBleachType()!= null && fdar.getBleachType()!= null && getBleachType().intValue() != fdar.getBleachType().intValue())
//			{
//				return false;
//			}
//			if((getFittingParamA()!= null && fdar.getFittingParamA() == null) || (getFittingParamA() == null && fdar.getFittingParamA() != null))
//			{
//				return false;
//			}
//			if(getFittingParamA()!= null && fdar.getFittingParamA()!= null && getFittingParamA().intValue() != fdar.getFittingParamA().intValue())
//			{
//				return false;
//			}
//			if((getOffRate() != null && fdar.getOffRate() == null) || (getOffRate() == null && fdar.getOffRate() != null))
//			{
//				return false;
//			}
//			if(getOffRate()!= null && fdar.getOffRate()!= null && getOffRate().intValue() != fdar.getOffRate().intValue())
//			{
//				return false;
//			}
//			return true;
//		}
//		return false;	
//	}
}
