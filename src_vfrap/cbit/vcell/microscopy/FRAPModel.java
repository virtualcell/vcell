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

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;

public class FRAPModel implements Matchable
{
	public static final String[] MODEL_TYPE_ARRAY = new String[]{"Diffusion with One Diffusing Component", 
		                                                       "Diffusion with Two Diffusing Components",
		                                                       "Reaction Dominant Off Rate",
//		                                                       "Effective Diffusion",
		                                                       "Diffusion plus Binding"};
	//different model types
	public static final int NUM_MODEL_TYPES = 4;
	public static final int IDX_MODEL_DIFF_ONE_COMPONENT = 0;
	public static final int IDX_MODEL_DIFF_TWO_COMPONENTS = 1;
	public static final int IDX_MODEL_REACTION_OFF_RATE = 2;
//	public static final int IDX_MODEL_EFFECTIVE_DIFFUSION = 3;
	public static final int IDX_MODEL_DIFF_BINDING = 3;
	
	//different model parameters
	public static String[] MODEL_PARAMETER_NAMES = new String[]{"Primary_diffusion_rate",
																"Primary_mobile_fraction",
																"Bleach_while_monitoring_rate",
																"Secondary_diffusion_rate",
																"Secondary_mobile_fraction",
																"Binding_site_concentration",
																"Reaction_on_rate",
																"Reaction_off_rate"};
	//intended to use VCUnitDefinition, however couldn't find units for BWMR(intensity/s), and units for fractions (no unit)
	//have to use String at the time being.
	private static FRAPUnitSystem frapUnitSystem = new FRAPUnitSystem(); 
	public static VCUnitDefinition[] MODEL_PARAMETER_UNITS = new VCUnitDefinition[]{frapUnitSystem.getDiffusionRateUnit(),
																frapUnitSystem.getInstance_DIMENSIONLESS(),
																frapUnitSystem.getBleachingWhileMonitoringRateUnit(),//bwm = Kon for single molecule reaction
																frapUnitSystem.getDiffusionRateUnit(),
																frapUnitSystem.getInstance_DIMENSIONLESS(),
																frapUnitSystem.getInstance_DIMENSIONLESS(),//ratio of total fluorescence?
																frapUnitSystem.getReactionOnRateUnit(),
																frapUnitSystem.getReactionOffRateUnit()};
	//referenced parameters
	public static final Parameter REF_DIFFUSION_RATE_PARAM =
		new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_DIFF_RATE], 0, 200, 1.0, 1.0);
	public static final Parameter REF_MOBILE_FRACTION_PARAM =
		new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_PRIMARY_FRACTION], 0, 1, 1.0, 1.0);
	public static final Parameter REF_BLEACH_WHILE_MONITOR_PARAM =
		new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE], 0, 1, 1.0,  0);
	public static final Parameter REF_SECOND_DIFFUSION_RATE_PARAM =
		new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_DIFF_RATE], 0, 100, 1.0, 1.0);
	public static final Parameter REF_SECOND_MOBILE_FRACTION_PARAM =
		new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_SECONDARY_FRACTION], 0, 1, 1.0, 1.0);
	//concentration parameter is used for reaction off rate model to store unuseful parameter A.
	public static final Parameter REF_BS_CONCENTRATION_OR_A = 
		new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION], 0, 1000, 1.0, 1.0);
	public static final Parameter REF_REACTION_ON_RATE = 
		new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION], 0.01, 100, 1.0, 1.0);
	public static final Parameter REF_REACTION_OFF_RATE = 
		new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE], 0.01, 100, 1.0, 1.0);
	//used in parameter panels to show log scale of the bleach monitoring rate slider.
	public static final int REF_BWM_LOG_VAL_MIN = -5;
	public static final int REF_BWM_LOG_VAL_MAX = 0;
	
	//other contants
	public static int NUM_MODEL_PARAMETERS_ONE_DIFF = 3;
	public static int NUM_MODEL_PARAMETERS_TWO_DIFF = 5;
	//reaction off rate model will use full diffusion-reaction model parameter. 
	//only bwmRate, offRate and concentration(store value for fitting param A) totally 3 parameters will be used.
	public static int NUM_MODEL_PARAMETERS_REACTION_OFF_RATE = 8;
	public static int NUM_MODEL_PARAMETERS_BINDING = 8;
	public static int INDEX_PRIMARY_DIFF_RATE = 0;
	public static int INDEX_PRIMARY_FRACTION = 1;
	public static int INDEX_BLEACH_MONITOR_RATE = 2;
	public static int INDEX_SECONDARY_DIFF_RATE = 3;
	public static int INDEX_SECONDARY_FRACTION = 4;
	public static int INDEX_BINDING_SITE_CONCENTRATION = 5;
	public static int INDEX_ON_RATE = 6;
	public static int INDEX_OFF_RATE = 7;

	private String modelIdentifer = null;
	private double[][] data = null;
	private double[] timepoints = null;
	

	private Parameter[] modelParameters = null;
	
	public FRAPModel(String arg_id, Parameter[] arg_parameters, double[][] arg_data, double[] arg_timePoints)
	{
		modelIdentifer = arg_id;
		modelParameters = arg_parameters;
		data = arg_data;
		timepoints = arg_timePoints;
	}

	public String getModelIdentifer() {
		return modelIdentifer;
	}

	public void setModelIdentifer(String modelIdentifer) {
		this.modelIdentifer = modelIdentifer;
	}

	public double[][] getData() {
		return data;
	}

	public void setData(double[][] data) {
		this.data = data;
	}

	public double[] getTimepoints() {
		return timepoints;
	}

	public void setTimepoints(double[] timepoints) {
		this.timepoints = timepoints;
	}
	
	public Parameter[] getModelParameters() {
		return modelParameters;
	}

	public void setModelParameters(Parameter[] modelParameters) {
		this.modelParameters = modelParameters;
	}
	
	public static Parameter[] getInitialParameters(FRAPData frapData, String modelIdentifier,int startIndexForRecovery) throws ExpressionException, OptimizationException, IOException
	{
		Parameter[] params = null;
		//get estimated bleach type
		double bleachFraction = FRAPDataAnalysis.getCellAreaBleachedFraction(frapData);
		int bleachType = (bleachFraction > FRAPDataAnalysis.THRESHOLD_BLEACH_TYPE)? 
				         FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults.BleachType_HalfCell : FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults.BleachType_GaussianSpot;
		//get analytical results 
		FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults analysisResults = FRAPDataAnalysis.fitRecovery_diffusionOnly(frapData, bleachType,startIndexForRecovery);
		
		//constrain the parameters in upper and lower bounds, analytic solution may get weird results sometimes(e.g. once got diffRate = 2819000.674223344)
		double diffusionRate = analysisResults.getRecoveryDiffusionRate();
		diffusionRate = (diffusionRate > REF_DIFFUSION_RATE_PARAM.getUpperBound())?REF_DIFFUSION_RATE_PARAM.getUpperBound():diffusionRate;
		diffusionRate = (diffusionRate < REF_DIFFUSION_RATE_PARAM.getLowerBound())?REF_DIFFUSION_RATE_PARAM.getLowerBound():diffusionRate;
		
		double mobileFraction = analysisResults.getMobilefraction();
		mobileFraction = (mobileFraction > REF_MOBILE_FRACTION_PARAM.getUpperBound())?REF_MOBILE_FRACTION_PARAM.getUpperBound():mobileFraction;
		mobileFraction = (mobileFraction < REF_MOBILE_FRACTION_PARAM.getLowerBound())?REF_MOBILE_FRACTION_PARAM.getLowerBound():mobileFraction;
		
		double bwmRate = analysisResults.getBleachWhileMonitoringTau();
		bwmRate = (bwmRate > REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound())?REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound():bwmRate;
		bwmRate = (bwmRate < REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound())?REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound():bwmRate;
		
		//create parameter array
		if(modelIdentifier.equals(MODEL_TYPE_ARRAY[IDX_MODEL_DIFF_ONE_COMPONENT]))
		{
			Parameter diff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE],
			                    REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
			                    REF_DIFFUSION_RATE_PARAM.getUpperBound(),
			                    REF_DIFFUSION_RATE_PARAM.getScale(),diffusionRate);
			Parameter mobileFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION],
			                    REF_MOBILE_FRACTION_PARAM.getLowerBound(),
			                    REF_MOBILE_FRACTION_PARAM.getUpperBound(),
			                    REF_MOBILE_FRACTION_PARAM.getScale(),mobileFraction);
			Parameter bleachWhileMonitoringRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE],
			                    REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
			                    REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
			                    REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),bwmRate);
			
			params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF];
			params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = diff;
			params[FRAPModel.INDEX_PRIMARY_FRACTION] = mobileFrac;
			params[FRAPModel.INDEX_BLEACH_MONITOR_RATE]= bleachWhileMonitoringRate;
		}
		else if(modelIdentifier.equals(MODEL_TYPE_ARRAY[IDX_MODEL_DIFF_TWO_COMPONENTS]))
		{
			Parameter diff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE], 
			                    REF_DIFFUSION_RATE_PARAM.getLowerBound(),
			                    REF_DIFFUSION_RATE_PARAM.getUpperBound(),
			                    REF_DIFFUSION_RATE_PARAM.getScale(), diffusionRate);
			Parameter mobileFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION],
                                REF_MOBILE_FRACTION_PARAM.getLowerBound(),
                                REF_MOBILE_FRACTION_PARAM.getUpperBound(),
                                REF_MOBILE_FRACTION_PARAM.getScale(), mobileFraction);
			Parameter monitorRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE], 
                                REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
                                REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
                                REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), bwmRate);
			Parameter secDiffRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE],
			                    REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(),
			                    REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(),
			                    REF_SECOND_DIFFUSION_RATE_PARAM.getScale(), 0);
			Parameter secMobileFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION],
			                    REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(),
			                    REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(),
			                    REF_SECOND_MOBILE_FRACTION_PARAM.getScale(), 0);
			
			params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF];
			params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = diff;
			params[FRAPModel.INDEX_PRIMARY_FRACTION] = mobileFrac;
			params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = monitorRate;
			params[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = secDiffRate;
			params[FRAPModel.INDEX_SECONDARY_FRACTION] = secMobileFrac;
		}
		
		return params;
	}

	public static Parameter[] createReacBindingParametersFromDiffusionParameters(Parameter[] origParams)
	{
		Parameter[] params = new Parameter[NUM_MODEL_PARAMETERS_BINDING];
		
		Parameter primaryDiff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE],
		                REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
		                REF_DIFFUSION_RATE_PARAM.getUpperBound(),
		                REF_DIFFUSION_RATE_PARAM.getScale(),
		                origParams[INDEX_PRIMARY_DIFF_RATE].getInitialGuess());
		Parameter primaryFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION],
		                REF_MOBILE_FRACTION_PARAM.getLowerBound(),
		                REF_MOBILE_FRACTION_PARAM.getUpperBound(),
		                REF_MOBILE_FRACTION_PARAM.getScale(),
		                origParams[INDEX_PRIMARY_FRACTION].getInitialGuess());
		Parameter bleachWhileMonitoringRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE],
		                REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
		                REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
		                REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
		                origParams[INDEX_BLEACH_MONITOR_RATE].getInitialGuess());
		
		Parameter secondaryDiff = null;
		Parameter secondaryFrac = null;
		
		secondaryDiff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE], 
                REF_DIFFUSION_RATE_PARAM.getLowerBound(),
                REF_DIFFUSION_RATE_PARAM.getUpperBound(),
                REF_DIFFUSION_RATE_PARAM.getScale(), 
                0);
		secondaryFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION],
				REF_MOBILE_FRACTION_PARAM.getLowerBound(),
                REF_MOBILE_FRACTION_PARAM.getUpperBound(),
                REF_MOBILE_FRACTION_PARAM.getScale(), 
                0);
		
		Parameter bsConcentration = new Parameter(MODEL_PARAMETER_NAMES[INDEX_BINDING_SITE_CONCENTRATION],
		                0,
		                1,
		                1, 
		                0);
		Parameter onReacRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_ON_RATE], 
		                0,
		                1e6,
		                1, 
		                0);
		Parameter offReacRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_OFF_RATE], 
		                0,
		                1e6,
		                1, 
		                0);
		
		params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_BINDING];
		params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = primaryDiff;
		params[FRAPModel.INDEX_PRIMARY_FRACTION] = primaryFrac;
		params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = bleachWhileMonitoringRate;
		params[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = secondaryDiff;
		params[FRAPModel.INDEX_SECONDARY_FRACTION] = secondaryFrac;
		params[FRAPModel.INDEX_BINDING_SITE_CONCENTRATION] = bsConcentration;
		params[FRAPModel.INDEX_ON_RATE] = onReacRate;
		params[FRAPModel.INDEX_OFF_RATE] = offReacRate;
		
		
		return params;
	}
	
	//In this mehtod, the data are not compared, because
	//if the parameters and the timepoints are the same, the data should be the same
	public boolean compareEqual(Matchable obj) 
	{
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof FRAPModel) 
		{
			FRAPModel frapModel = (FRAPModel) obj;
			if (!modelIdentifer.equals(frapModel.getModelIdentifer()))
			{
				return false;
			}
			//there are modelparameters null, so we cannot directly use Compare.isEqualOrNull(Machable[] v1, Machable[] v2)
			if((modelParameters == null && frapModel.getModelParameters() != null)
			   ||(modelParameters != null && frapModel.getModelParameters() == null))
			{
				return false;
			}
			if(modelParameters != null && frapModel.getModelParameters() != null)
			{
				if(modelParameters.length != frapModel.getModelParameters().length)
				{
					return false;
				}
				for(int i = 0; i < modelParameters.length; i++)
				{
					if(!Compare.isEqualOrNull(modelParameters[i], frapModel.getModelParameters()[i]))
					{
						return false;
					}
				}
			}
			if(!Compare.isEqualOrNull(timepoints, frapModel.getTimepoints()))
			{
				return false;
			}
			
			return true;
		}
		return false;
	}
	
	public Parameter[] duplicateParameters()
	{
		if(getModelParameters() != null)
		{
			Parameter[] result = new Parameter[this.getModelParameters().length];
			for(int i=0; i<this.getModelParameters().length; i++)
			{
				if(this.getModelParameters()[i] == null)
				{
					result[i] = null;
				}
				else
				{
					result[i] = this.getModelParameters()[i].duplicate();
				}
			}
			return result;
		}
		return null;
	}
	
	public double[] duplicateTimePoints()
	{
		if(getTimepoints() != null)
		{
			double[] result = new double[this.getTimepoints().length]; 
			System.arraycopy(this.getTimepoints(), 0, result, 0, this.getTimepoints().length);
			return result;
		}
		return null;
	}
}
