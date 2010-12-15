package cbit.vcell.microscopy;

import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.opt.Parameter;

public class FRAPOptFunctions 
{
	public static String SYMBOL_A = "A";
	public static String SYMBOL_KOFF = FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE];
	public static String SYMBOL_BWM_RATE = FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE];
	public static String SYMBOL_I_inibleached = "I_inibleached";
	public static String FUNC_RECOVERY_BLEACH_REACTION_DOMINANT = SYMBOL_I_inibleached + "+" +SYMBOL_A + "*(1-exp(-1*"+SYMBOL_KOFF+"*"+ReservedSymbol.TIME.getName()+"))" + "*exp(-1*"+ SYMBOL_BWM_RATE+"*"+ ReservedSymbol.TIME.getName() +")";
	public static String SYMBOL_I_inicell = "I_inicell";
	public static String FUNC_CELL_INTENSITY = SYMBOL_I_inicell + "*(exp(-1*"+SYMBOL_BWM_RATE+"*"+ReservedSymbol.TIME.getName()+"))";
	
	private double leastError = 0;
	//used for optimization when taking measurement error into account.
	//first dimension length 11, according to the order in FRAPData.VFRAP_ROI_ENUM
	//second dimension time, total time length - starting index for recovery 
//	private double[][] measurementErrors = null;
	private FRAPStudy expFrapStudy = null;
	private FrapDataAnalysisResults.ReactionOnlyAnalysisRestults offRateResults = null;
	
	public FRAPOptFunctions(FRAPStudy argExpFrapStudy)
	{
		expFrapStudy = argExpFrapStudy;
	}
	
	public FRAPStudy getExpFrapStudy() {
		return expFrapStudy;
	}
	
	public double getLeastError() {
		return leastError;
	}

	public void setLeastError(double leastError) {
		this.leastError = leastError;
	}
	
	public FrapDataAnalysisResults.ReactionOnlyAnalysisRestults getOffRateResults() {
		return offRateResults;
	}

	public void setOffRateResults(FrapDataAnalysisResults.ReactionOnlyAnalysisRestults offRateResults) {
		this.offRateResults = offRateResults;
	}

	
	public double[][] getFitData(Parameter[] newParams) throws Exception
	{
		double[][] result = null;
		if(newParams.length != FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE)
		{
			throw new Exception("Wrong parameter size in FRAP optimazition for reaction off rate.");
		}
		else
		{
			
		}
		
		return result;
	}
	
	//The best parameters will return a whole set of reaction off rate parameters (totally 8 parameters)
	public Parameter[] getBestParamters(FRAPData frapData) throws Exception
	{
//		if(measurementErrors == null)
//		{
//			FRAPOptimizationUtils.refreshNormalizedMeasurementError(getExpFrapStudy());
//		}
		Parameter[] outputParams = new Parameter[FRAPModel.NUM_MODEL_PARAMETERS_REACTION_OFF_RATE];
		FrapDataAnalysisResults.ReactionOnlyAnalysisRestults offRateResults = FRAPDataAnalysis.fitRecovery_reacOffRateOnly(frapData);
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
		
		return outputParams;
	}
	public static void main(String argv[])
	{
		System.out.println(FUNC_RECOVERY_BLEACH_REACTION_DOMINANT);
		System.out.println(FUNC_CELL_INTENSITY);
	}
}
