package cbit.vcell.microscopy;


import cbit.vcell.microscopy.ROI.RoiType;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;

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
	
	public final static Parameter para_Ii = new cbit.vcell.opt.Parameter("Ii", -1, 1, 1.0, 0.0); 
	public final static Parameter para_A = new cbit.vcell.opt.Parameter("A", 0.1, 4, 1.0, 1.0); 
//	public final static Parameter para_CicularDisk_Tau = new cbit.vcell.opt.Parameter("Tau",0.1, 50.0, 1.0, 1.0);
	public final static Parameter para_If = new cbit.vcell.opt.Parameter("If", 0, 1, 1.0, 0.1);
	public final static Parameter para_Io = new cbit.vcell.opt.Parameter("Io", 0, 1, 1.0, 0.1);
	public final static Parameter para_tau = new cbit.vcell.opt.Parameter("tau", 0.1, 50.0, 1.0, 1.0);
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
	
//	/**
//	 * Method fitRecovery.
//	 * @param frapData FRAPData
//	 * @throws ExpressionException
//	 */
//	public static void fitRecovery(FRAPData frapData,double[] normFactor) throws ExpressionException{
//		double[] temp_fluor = getAverageROIIntensity(frapData,frapData.getRoi(RoiType.ROI_BLEACHED),normFactor); 
//		double[] temp_background = getAverageROIIntensity(frapData,frapData.getRoi(RoiType.ROI_BACKGROUND),normFactor); 
//		double[] temp_time = frapData.getImageDataset().getImageTimeStamps();
//		//
//		// get average background fluorescence
//		//
//		double averageBackground = 0.0;
//		for (int i = 0; i < temp_background.length; i++) {
//			averageBackground += temp_background[i];
//		}
//		averageBackground /= temp_background.length;
//		
//		//
//		// background subtract the bleached region
//		//
//		for (int i = 0; i < temp_fluor.length; i++) {
//			temp_fluor[i] -= averageBackground;
//		}
//		//
//		// determine index of first post bleach (index of smallest fluorescence)
//		//
//		int startIndexForRecovery = 0;
//		double minFluorValue = Double.MAX_VALUE;
//		for (int i = 0; i < temp_fluor.length; i++) {
//			if (minFluorValue>temp_fluor[i]){
//				minFluorValue = temp_fluor[i];
//				startIndexForRecovery = i;
//			}
//		}
//		double[] fluor = new double[temp_fluor.length-startIndexForRecovery];
//		double[] time = new double[temp_time.length-startIndexForRecovery];
//		System.arraycopy(temp_fluor, startIndexForRecovery, fluor, 0, fluor.length);
//		System.arraycopy(temp_time, startIndexForRecovery, time, 0, time.length);
//		double[] paramValues = new double[3];
//		double[] inputParams = null;
//		Expression fittedCurve = CurveFitting.fitRecovery(time,fluor,FrapDataAnalysisResults.BleachType_CirularDisk, inputParams, paramValues);
//		SimpleSymbolTable symbolTable = new SimpleSymbolTable(new String[] { "t" });
//		fittedCurve.bindExpression(symbolTable);
//		double[] values = new double[1];
//		double[] curveValues = new double[fluor.length];
//		double[] asymptote = new double[fluor.length];
//		for (int i = 0; i < curveValues.length; i++) {
//			values[0] = time[i];
//			curveValues[i] = fittedCurve.evaluateVector(values);
//			values[0] = time[time.length-1]*100;
//			asymptote[i] = fittedCurve.evaluateVector(values);
//		}
//	}
	
	/**
	 * Method fitRecovery2.
	 * @param frapData FRAPData
	 * @return FrapDataAnalysisResults
	 * @throws ExpressionException
	 */
	public static FrapDataAnalysisResults fitRecovery2(FRAPData frapData, int arg_bleachType) throws ExpressionException{
		
		int startIndexForRecovery = getRecoveryIndex(frapData);
		//
		// get unnormalized average background fluorescence at each time point
		//
		double[] temp_background = getAverageROIIntensity(frapData,frapData.getRoi(RoiType.ROI_BACKGROUND),null,null);
		
		double[] preBleachAvgXYZ = FRAPStudy.calculatePreBleachAverageXYZ(frapData, startIndexForRecovery);
		
		double[] temp_fluor = getAverageROIIntensity(frapData,frapData.getRoi(RoiType.ROI_BLEACHED),preBleachAvgXYZ,temp_background); //get average intensity under the bleached area according to each time point
		double[] temp_time = frapData.getImageDataset().getImageTimeStamps();

		//get nomalized preBleachAverage under bleached area.
		double preBleachAverage_bleachedArea = 0.0;
		for (int i = 0; i < startIndexForRecovery; i++) {
			preBleachAverage_bleachedArea += temp_fluor[i];
		}
		preBleachAverage_bleachedArea /= startIndexForRecovery;

		
		//get number of pixels in bleached region(non ROI region pixels are saved as 0)
		ROI bleachedROI_2D = frapData.getRoi(RoiType.ROI_BLEACHED);
		long numPixelsInBleachedROI = bleachedROI_2D.getRoiImages()[0].getNumXYZ() - bleachedROI_2D.getRoiImages()[0].countPixelsByValue((short)0);
		// assume ROI is a circle, A = Pi*R^2
		// so R = sqrt(A/Pi)
		double area = bleachedROI_2D.getRoiImages()[0].getPixelAreaXY() * numPixelsInBleachedROI;
		double bleachRadius = Math.sqrt(area/Math.PI);// Radius of ROI(assume that ROI is a circle)
		// assume cell is a circle, A = Pi*R^2
		// so R = sqrt(A/Pi)
		area = frapData.getRoi(RoiType.ROI_CELL).getRoiImages()[0].getPixelAreaXY() * (frapData.getRoi(RoiType.ROI_CELL).getRoiImages()[0].getNumXYZ()- bleachedROI_2D.getRoiImages()[0].countPixelsByValue((short)0));
		double cellRadius = Math.sqrt(area/Math.PI);// Radius of ROI(assume that ROI is a circle)
		
		
		//average intensity under bleached area. The time points start from the first post bleach
		double[] fluor = new double[temp_fluor.length-startIndexForRecovery];
		//Time points stat from the first post bleach
		double[] time = new double[temp_time.length-startIndexForRecovery];
		System.arraycopy(temp_fluor, startIndexForRecovery, fluor, 0, fluor.length);
		System.arraycopy(temp_time, startIndexForRecovery, time, 0, time.length);
		
		FrapDataAnalysisResults frapDataAnalysisResults = new FrapDataAnalysisResults();
		frapDataAnalysisResults.setBleachWidth(bleachRadius);
		frapDataAnalysisResults.setStartingIndexForRecovery(startIndexForRecovery);
		frapDataAnalysisResults.setBleachRegionData(temp_fluor);// average intensity under bleached region accroding to time points
		frapDataAnalysisResults.setBleachType(arg_bleachType);
		//curve fitting according to different bleach types
		double[] inputParamValues = null;
		double[] outputParamValues = null;
		
		//
		//Bleach while monitoring fit
		//
		double[] tempCellROIAverage =
			getAverageROIIntensity(frapData,frapData.getRoi(RoiType.ROI_CELL),preBleachAvgXYZ,temp_background);
//		for (int i = 0; i < temp_fluor.length; i++) {
//			tempCellROIAverage[i] -= averageBackground;
//		}
		double[] cellROIAverage = new double[tempCellROIAverage.length-startIndexForRecovery];
		//Cell Avg. points start from the first post bleach
		System.arraycopy(tempCellROIAverage, startIndexForRecovery, cellROIAverage, 0, cellROIAverage.length);

		outputParamValues = new double[1];
		Expression bleachWhileMonitorFitExpression =
			CurveFitting.fitBleachWhileMonitoring(time, cellROIAverage, outputParamValues);
		double bleachWhileMonitoringRate = outputParamValues[0];
		frapDataAnalysisResults.setBleachWhileMonitoringTau(bleachWhileMonitoringRate);
		frapDataAnalysisResults.setFitBleachWhileMonitorExpression(bleachWhileMonitorFitExpression.flatten());
		frapDataAnalysisResults.setCellRegionData(tempCellROIAverage);
		
		//
		//Recovery after Bleach fit
		//
//		if(arg_bleachType == FrapDataAnalysisResults.BleachType_CirularDisk)
//		{
//			outputParamValues = new double[4];// the array is used to get Ii, A, fitRecoveryTau back, unnormalized Ii + A. (Note: Ii=Io, A=If-Io, Ii+A = If)
//			Expression fittedCurve = CurveFitting.fitRecovery(time, fluor, FrapDataAnalysisResults.BleachType_CirularDisk, inputParamValues, outputParamValues);
//			double fittedRecoveryTau = outputParamValues[2];
//			Expression diffExp = new Expression(FRAPDataAnalysis.circularDisk_DiffFunc);
//			diffExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_w), new Expression(bleachRadius));
//			diffExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_tau), new Expression(fittedRecoveryTau));
//			double fittedDiffusionRate =  diffExp.evaluateConstant();
////			double fittedDiffusionRate = bleachRadius*bleachRadius/(4.0*fittedRecoveryTau);
//			frapDataAnalysisResults.setFitExpression(fittedCurve.flatten());
//			frapDataAnalysisResults.setRecoveryTau(fittedRecoveryTau);
//			frapDataAnalysisResults.setRecoveryDiffusionRate(fittedDiffusionRate);
////			//calculate mobile fraction
////			// get pre bleach average under bleach ROI region, if no prebleach available
////			double preBleachAvg = 0;
////			if(startIndexForRecovery > 0)
////			{
////				for(int i=0; i<startIndexForRecovery; i++)
////				{
////					preBleachAvg = preBleachAvg + temp_fluor[i];
////				}
////				preBleachAvg = preBleachAvg/startIndexForRecovery;
////			}
////			else //if there is not prebleach images, we'll use the last recovery image * 1.2 as prebleach value if user agrees.
////			{
////				preBleachAvg = temp_fluor[temp_fluor.length - 1];//* ratio
////				System.err.println("need to determine factor for prebleach average if no pre bleach images.");
////			}
////			// get unnormalized Ii and A
////			double intensityFinal = outputParamValues[3]; // Ii+A
////			// calculate mobile fraction
////			double mobileFrac = intensityFinal/preBleachAvg;
////			frapDataAnalysisResults.setMobilefraction(mobileFrac);
//			double mobileFrac = outputParamValues[3];
//			frapDataAnalysisResults.setMobilefraction(mobileFrac);
//
//			
//		}
//		else 
		if(arg_bleachType == FrapDataAnalysisResults.BleachType_GaussianSpot)
		{
			double cellAreaBleached = getCellAreaBleachedFraction(frapData);
			inputParamValues = new double[]{bleachWhileMonitoringRate}; // the input parameter is the bleach while monitoring rate
			outputParamValues = new double[3];// the array is used to get If, Io, and tau back.
			Expression fittedCurve = CurveFitting.fitRecovery(time, fluor, FrapDataAnalysisResults.BleachType_GaussianSpot, inputParamValues, outputParamValues);
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
			//set frap data analysis results
			frapDataAnalysisResults.setFitExpression(fittedCurve.flatten());
			frapDataAnalysisResults.setRecoveryTau(fittedRecoveryTau);
			frapDataAnalysisResults.setRecoveryDiffusionRate(fittedDiffusionRate);
			frapDataAnalysisResults.setMobilefraction(mobileFrac);
		}
		else if(arg_bleachType == FrapDataAnalysisResults.BleachType_HalfCell)
		{
			
			double cellAreaBleached = getCellAreaBleachedFraction(frapData);
			inputParamValues = new double[]{bleachWhileMonitoringRate}; // the input parameter is the bleach while monitoring rate
			outputParamValues = new double[3];// the array is used get If, Io, and tau back.
			Expression fittedCurve = CurveFitting.fitRecovery(time, fluor, FrapDataAnalysisResults.BleachType_HalfCell, inputParamValues, outputParamValues);
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
			//set frap data analysis results
			frapDataAnalysisResults.setFitExpression(fittedCurve.flatten());
			frapDataAnalysisResults.setRecoveryTau(fittedRecoveryTau);
			frapDataAnalysisResults.setRecoveryDiffusionRate(fittedDiffusionRate);
			frapDataAnalysisResults.setMobilefraction(mobileFrac);
		}else{
			throw new IllegalArgumentException("Unknown Bleach Type "+arg_bleachType);
		}
		return frapDataAnalysisResults;
	}
	
	public static int getRecoveryIndex(FRAPData frapData)
	{
		double[] temp_fluor = getAverageROIIntensity(frapData,frapData.getRoi(RoiType.ROI_BLEACHED),null,null);
		
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
	private static double getCellAreaBleachedFraction(FRAPData fdata)
	{
		ROI bleachedROI = fdata.getRoi(RoiType.ROI_BLEACHED);
		ROI cellROI = fdata.getRoi(RoiType.ROI_CELL);
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
