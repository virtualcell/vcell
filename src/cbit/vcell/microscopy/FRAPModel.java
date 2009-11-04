package cbit.vcell.microscopy;

import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.units.VCUnitDefinition;

public class FRAPModel 
{
	public static final String[] MODEL_TYPE_ARRAY = new String[]{"Diffusion with One Diffusing Component", 
		                                                       "Diffusion with Two Diffusing Components",
		                                                       "Diffusion plus Binding"};
	//different model types
	public static final int NUM_MODEL_TYPES = 3;
	public static final int IDX_MODEL_DIFF_ONE_COMPONENT = 0;
	public static final int IDX_MODEL_DIFF_TWO_COMPONENTS = 1;
	public static final int IDX_MODEL_DIFF_BINDING = 2;
	
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
	public static VCUnitDefinition[] MODEL_PARAMETER_UNITS = new VCUnitDefinition[]{VCUnitDefinition.UNIT_um2_per_s,
																VCUnitDefinition.UNIT_DIMENSIONLESS,
																VCUnitDefinition.UNIT_per_s,//bwm = Kon for single molecule reaction
																VCUnitDefinition.UNIT_um2_per_s,
																VCUnitDefinition.UNIT_DIMENSIONLESS,
																VCUnitDefinition.UNIT_DIMENSIONLESS,//ratio of total fluorescence?
																VCUnitDefinition.UNIT_per_um_per_s,
																VCUnitDefinition.UNIT_per_s};
	public static int NUM_MODEL_PARAMETERS_ONE_DIFF = 3;
	public static int NUM_MODEL_PARAMETERS_TWO_DIFF = 5;
	public static int NUM_MODEL_PARAMETERS_BINDING = 8;
	public static int INDEX_PRIMARY_DIFF_RATE = 0;
	public static int INDEX_PRIMARY_FRACTION = 1;
	public static int INDEX_BLEACH_MONITOR_RATE = 2;
	public static int INDEX_SECONDARY_DIFF_RATE = 3;
	public static int INDEX_SECONDARY_FRACTION = 4;
//	public static int INDEX_IMMOBILE_FRACTION = 5;
	public static int INDEX_BINDING_SITE_CONCENTRATION = 5;
	public static int INDEX_ON_RATE = 6;
	public static int INDEX_OFF_RATE = 7;
	
	private String modelIdentifer = null;
	private double[][] data = null;
	private double[] timepoints = null;
	

	private Parameter[] modelParameters = null;
	
	public FRAPModel(String arg_id, Parameter[] arg_parameters, double[][] arg_result)
	{
		modelIdentifer = arg_id;
		modelParameters = arg_parameters;
		data = arg_result;
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

	public void setData(double[][] result) {
		this.data = result;
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
	
	public static Parameter[] getInitialParameters(FRAPData frapData, String modelIdentifier) throws ExpressionException
	{
		Parameter[] params = null;
		//get estimated bleach type
		double bleachFraction = FRAPDataAnalysis.getCellAreaBleachedFraction(frapData);
		int bleachType = (bleachFraction > FRAPDataAnalysis.THRESHOLD_BLEACH_TYPE)? 
				         FrapDataAnalysisResults.BleachType_HalfCell : FrapDataAnalysisResults.BleachType_GaussianSpot;
		//get analytical results 
		FrapDataAnalysisResults analysisResults = FRAPDataAnalysis.fitRecovery(frapData, bleachType);
		
		//create parameter array
		if(modelIdentifier.equals(MODEL_TYPE_ARRAY[IDX_MODEL_DIFF_ONE_COMPONENT]))
		{
			Parameter diff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE],
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getScale(),analysisResults.getRecoveryDiffusionRate());
			Parameter mobileFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION],
			                    FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
			                    FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
			                    FRAPOptData.REF_MOBILE_FRACTION_PARAM.getScale(),analysisResults.getMobilefraction());
			Parameter bleachWhileMonitoringRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE],
			                    FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
			                    FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
			                    FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),analysisResults.getBleachWhileMonitoringTau());
			
			params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF];
			params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = diff;
			params[FRAPModel.INDEX_PRIMARY_FRACTION] = mobileFrac;
			params[FRAPModel.INDEX_BLEACH_MONITOR_RATE]= bleachWhileMonitoringRate;
		}
		else if(modelIdentifier.equals(MODEL_TYPE_ARRAY[IDX_MODEL_DIFF_TWO_COMPONENTS]))
		{
			Parameter diff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE], 
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound(),
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getScale(), analysisResults.getRecoveryDiffusionRate());
			Parameter mobileFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION],
                                FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
                                FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
                                FRAPOptData.REF_MOBILE_FRACTION_PARAM.getScale(), analysisResults.getMobilefraction());
			Parameter monitorRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE], 
                                FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
                                FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
                                FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(), analysisResults.getBleachWhileMonitoringTau());
			Parameter secDiffRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE],
			                    FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getLowerBound(),
			                    FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getUpperBound(),
			                    FRAPOptData.REF_SECOND_DIFFUSION_RATE_PARAM.getScale(), 0);
			Parameter secMobileFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION],
			                    FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getLowerBound(),
			                    FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getUpperBound(),
			                    FRAPOptData.REF_SECOND_MOBILE_FRACTION_PARAM.getScale(), 0);
			
			params = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF];
			params[FRAPModel.INDEX_PRIMARY_DIFF_RATE] = diff;
			params[FRAPModel.INDEX_PRIMARY_FRACTION] = mobileFrac;
			params[FRAPModel.INDEX_BLEACH_MONITOR_RATE] = monitorRate;
			params[FRAPModel.INDEX_SECONDARY_DIFF_RATE] = secDiffRate;
			params[FRAPModel.INDEX_SECONDARY_FRACTION] = secMobileFrac;
		}
		else if(modelIdentifier.equals(MODEL_TYPE_ARRAY[IDX_MODEL_DIFF_BINDING]))
		{
			Parameter primaryDiff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE],
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getScale(),
			                    analysisResults.getRecoveryDiffusionRate());
			Parameter primaryFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION],
			                    FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
			                    FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
			                    FRAPOptData.REF_MOBILE_FRACTION_PARAM.getScale(),
			                    analysisResults.getMobilefraction());
			Parameter bleachWhileMonitoringRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE],
			                    FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
			                    FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
			                    FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
			                    analysisResults.getBleachWhileMonitoringTau());
			Parameter secondaryDiff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE], 
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound(),
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
			                    FRAPOptData.REF_DIFFUSION_RATE_PARAM.getScale(), 
			                    0);
			Parameter secondaryFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION],
								FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
			                    FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
			                    FRAPOptData.REF_MOBILE_FRACTION_PARAM.getScale(), 
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
		}
		
		return params;
	}

	public static Parameter[] createReacBindingParametersFromDiffusionParameters(Parameter[] origParams)
	{
		Parameter[] params = new Parameter[NUM_MODEL_PARAMETERS_BINDING];
		
		Parameter primaryDiff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_DIFF_RATE],
		                FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound(), 
		                FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
		                FRAPOptData.REF_DIFFUSION_RATE_PARAM.getScale(),
		                origParams[INDEX_PRIMARY_DIFF_RATE].getInitialGuess());
		Parameter primaryFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_PRIMARY_FRACTION],
		                FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
		                FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
		                FRAPOptData.REF_MOBILE_FRACTION_PARAM.getScale(),
		                origParams[INDEX_PRIMARY_FRACTION].getInitialGuess());
		Parameter bleachWhileMonitoringRate = new Parameter(MODEL_PARAMETER_NAMES[INDEX_BLEACH_MONITOR_RATE],
		                FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
		                FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
		                FRAPOptData.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
		                origParams[INDEX_BLEACH_MONITOR_RATE].getInitialGuess());
		
		Parameter secondaryDiff = null;
		Parameter secondaryFrac = null;
		
		secondaryDiff = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_DIFF_RATE], 
                FRAPOptData.REF_DIFFUSION_RATE_PARAM.getLowerBound(),
                FRAPOptData.REF_DIFFUSION_RATE_PARAM.getUpperBound(),
                FRAPOptData.REF_DIFFUSION_RATE_PARAM.getScale(), 
                0);
		secondaryFrac = new Parameter(MODEL_PARAMETER_NAMES[INDEX_SECONDARY_FRACTION],
				FRAPOptData.REF_MOBILE_FRACTION_PARAM.getLowerBound(),
                FRAPOptData.REF_MOBILE_FRACTION_PARAM.getUpperBound(),
                FRAPOptData.REF_MOBILE_FRACTION_PARAM.getScale(), 
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
	
	
}
