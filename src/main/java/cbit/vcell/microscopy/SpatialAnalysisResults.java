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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.test.MathTestingUtilities;

public class SpatialAnalysisResults{
	public final AnalysisParameters[] analysisParameters;
	public final double[] shiftedSimTimes;
	public final Hashtable<CurveInfo, double[]> curveHash;
	public static String[] ORDERED_ROINAMES =
		new String[] {
			FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(),	
			FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING1.name(),	
			FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING2.name(),	
			FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING3.name(),	
			FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING4.name(),	
			FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING5.name(),
			FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING6.name(),
			FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING7.name(),
			FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED_RING8.name()
		};
	
	public static final int ANALYSISPARAMETERS_COLUMNS_COUNT = 3;
	public static final int COLUMN_INDEX_DIFFUSION_RATE = 0;
	public static final int COLUMN_INDEX_MOBILE_FRACTION = 1;
	public static final int COLUMN_INDEX_MONITOR_BLEACH_RATE = 2;
	
	public static final int ARRAY_INDEX_EXPDATASOURCE = 0;
	public static final int ARRAY_INDEX_SIMDATASOURCE = 1;
	
	private static final String ENDOFLINE = "\r\n";
	private static final String SEPCHAR = "\t";


	public SpatialAnalysisResults(AnalysisParameters[] analysisParameters,double[] shiftedSimTimes,	Hashtable<CurveInfo, double[]> curveHash)
	{
		this.analysisParameters = analysisParameters;
		this.shiftedSimTimes = shiftedSimTimes;
		this.curveHash = curveHash;
	}
	public AnalysisParameters[] getAnalysisParameters()
	{
		return analysisParameters;
	}
	//This function put exp data and sim data in a hashtable, the key is analysisParameters(right now only 1)
	public Hashtable<AnalysisParameters, DataSource[]> createSummaryReportSourceData(
			final double[] frapDataTimeStamps,int startIndexForRecovery, boolean[] selecredROIs, boolean isSimData) throws Exception{
		
		Hashtable<AnalysisParameters, DataSource[]> allDataHash = new Hashtable<AnalysisParameters, DataSource[]>();
		
		Hashtable<CurveInfo, double[]> ROIInfoHash = curveHash;
		Set<CurveInfo> roiInfoSet = ROIInfoHash.keySet();
		Iterator<CurveInfo> roiInfoIter = roiInfoSet.iterator();
		//exp data are stored by ROI type.
		Hashtable<String, double[]> expROIData = new Hashtable<String, double[]>();
		//sim data are stored by AnalysisParameters and then by ROI type.
		Hashtable<AnalysisParameters, Hashtable<String, double[]>> simROIData = new Hashtable<AnalysisParameters, Hashtable<String,double[]>>();
		int roiCount = 0;
		int analysisParametersCount = 0;// how many set of parameters are used for sim. 1 for now. 
		while(roiInfoIter.hasNext()){
			CurveInfo roiCurveInfo = roiInfoIter.next();
			if(roiCurveInfo.isExperimentInfo()){
				expROIData.put(roiCurveInfo.getROIName(), ROIInfoHash.get(roiCurveInfo));
				roiCount++;
			}else{
				Hashtable<String,double[]> simROIDataHash = simROIData.get(roiCurveInfo.getAnalysisParameters());
				if(simROIDataHash == null){
					simROIDataHash  = new Hashtable<String, double[]>();
					simROIData.put(roiCurveInfo.getAnalysisParameters(), simROIDataHash);
					analysisParametersCount++;
				}
				simROIDataHash.put(roiCurveInfo.getROIName(), ROIInfoHash.get(roiCurveInfo));
			}
		}
		//this is for exp data. each row of reference data contains time + intensities under 9 ROIs(bleached + ring1..8). totally 10 cols for each row.		
		ReferenceData referenceData = createReferenceData(frapDataTimeStamps,startIndexForRecovery,"", selecredROIs);
		//loop only 1 time, right now the analysisParameters[]'s length is 1.
		for (int analysisParametersRow = 0; analysisParametersRow < analysisParametersCount; analysisParametersRow++) 
		{
			AnalysisParameters currentAnalysisParameters = analysisParameters[analysisParametersRow];
			
			DataSource[] newDataSourceArr = new DataSource[2];
			final DataSource expDataSource = new DataSource.DataSourceReferenceData("exp", referenceData); // rows for time points and cols for t + roibleached + ring1--8 (10 cols)
			newDataSourceArr[ARRAY_INDEX_EXPDATASOURCE] = expDataSource;
			DataSource simDataSource = null;
			if(isSimData)
			{
				ODESolverResultSet odeSolverResultSet = createODESolverResultSet(currentAnalysisParameters,null,"");
				simDataSource = new DataSource.DataSourceRowColumnResultSet("sim", odeSolverResultSet); //rows for time points and cols for t + roibleached + ring1--8 (10 cols)
			}
			newDataSourceArr[ARRAY_INDEX_SIMDATASOURCE] = simDataSource;
			allDataHash.put(currentAnalysisParameters,newDataSourceArr);
		}
		return allDataHash;
	}


	public Object[][] createSummaryReportTableData(double[] frapDataTimeStamps,int startTimeIndex){
		String[] summaryReportColumnNames = SpatialAnalysisResults.getSummaryReportColumnNames();
		final Object[][] tableData = new Object[analysisParameters.length][summaryReportColumnNames.length];
		for (int analysisParametersRow = 0; analysisParametersRow < analysisParameters.length; analysisParametersRow++) {
			AnalysisParameters currentAnalysisParameters = analysisParameters[analysisParametersRow];
			
			//need to change!
//			tableData[analysisParametersRow][COLUMN_INDEX_DIFFUSION_RATE] = Double.parseDouble(currentAnalysisParameters.freeDiffusionRate);
//			tableData[analysisParametersRow][COLUMN_INDEX_MOBILE_FRACTION] = Double.parseDouble(currentAnalysisParameters.freeMobileFraction);
//			tableData[analysisParametersRow][COLUMN_INDEX_MONITOR_BLEACH_RATE] = currentAnalysisParameters.bleachWhileMonitorRate;
			
			for (int roiColumn = 0; roiColumn < SpatialAnalysisResults.ORDERED_ROINAMES.length; roiColumn++) {
				ODESolverResultSet simDataSource =
					createODESolverResultSet(currentAnalysisParameters,SpatialAnalysisResults.ORDERED_ROINAMES[roiColumn],"");
				ReferenceData expDataSource =
					createReferenceData(frapDataTimeStamps,startTimeIndex,"", null);//TODO to remove null to put SpatialAnalysisResults.ORDERED_ROINAMES[roiColumn]

				int numSamples = expDataSource.getNumDataRows();
				double sumSquaredError = MathTestingUtilities.calcWeightedSquaredError(simDataSource, expDataSource);
				tableData[analysisParametersRow][roiColumn+ANALYSISPARAMETERS_COLUMNS_COUNT] =
					Math.sqrt(sumSquaredError)/(numSamples-1);//unbiased estimator is numsamples-1
			}
		}
		return tableData;
	}
	
	public static String createCSVSummaryReport(String[] summaryReportColumnNames,Object[][] summaryData){
		StringBuffer reportSB = new StringBuffer();
		for (int i = 0; i < summaryReportColumnNames.length; i++) {
			reportSB.append((i != 0?SEPCHAR:"")+summaryReportColumnNames[i]);
		}
		reportSB.append(ENDOFLINE);
		for (int j = 0; j < summaryData.length; j++) {
			for (int k = 0; k < summaryReportColumnNames.length; k++) {
				reportSB.append((k != 0?SEPCHAR:"")+summaryData[j][k].toString());
			}
			reportSB.append(ENDOFLINE);
		}
		return reportSB.toString();
	}

	public static String createCSVTimeData(
			String[] summaryReportColumnNames,int selectedColumn,
			double[] expTimes,double[] expColumnData,
			double[] simTimes,double[] simColumnData){
		StringBuffer dataSB = new StringBuffer();
		dataSB.append("Exp Time"+SEPCHAR+" Exp Data (norm):"+summaryReportColumnNames[selectedColumn]+SEPCHAR+
				"Sim Time"+SEPCHAR+" Sim Data (norm):"+summaryReportColumnNames[selectedColumn]);
		dataSB.append(ENDOFLINE);
		for (int j = 0; j < Math.max(expTimes.length, simTimes.length); j++) {
			if(j <expTimes.length){
				dataSB.append(expTimes[j]+SEPCHAR+expColumnData[j]);
			}else{
				dataSB.append("NULL"+SEPCHAR+"NULL");
			}
			dataSB.append(SEPCHAR);
			if(j <simTimes.length){
				dataSB.append(simTimes[j]+SEPCHAR+simColumnData[j]);
			}else{
				dataSB.append("NULL"+SEPCHAR+"NULL");
			}
			dataSB.append(ENDOFLINE);
		}
		return dataSB.toString();
	}
	public static String[] getSummaryReportColumnNames(){
		String[] columnNames = new String[SpatialAnalysisResults.ORDERED_ROINAMES.length+ANALYSISPARAMETERS_COLUMNS_COUNT];
		for (int i = 0; i < columnNames.length; i++) {
			if(i==COLUMN_INDEX_DIFFUSION_RATE){
				columnNames[i] = "Diffusion Rate";
			}else if(i==COLUMN_INDEX_MOBILE_FRACTION){
				columnNames[i] = "Mobile Fraction";
			}else if(i==COLUMN_INDEX_MONITOR_BLEACH_RATE){
				columnNames[i] = "Monitor Bleach Rate";
			}else{
				final String ROI_PREFIX = "ROI_";
				final String ROI_TYPENAME_PREFIX = ROI_PREFIX+"BLEACHED_";
				String roiTypeName = SpatialAnalysisResults.ORDERED_ROINAMES[i-ANALYSISPARAMETERS_COLUMNS_COUNT];
				if(roiTypeName.startsWith(ROI_TYPENAME_PREFIX)){
					columnNames[i] = roiTypeName.substring(ROI_TYPENAME_PREFIX.length());
				}else{
					columnNames[i] = roiTypeName.substring(ROI_PREFIX.length());
				}
				columnNames[i]+= " (se)";
			}
		}
		return columnNames;
	}


	public ReferenceData[] createReferenceDataForAllDiffusionRates(double[] frapDataTimeStamps){
		ReferenceData[] referenceDataArr = new ReferenceData[analysisParameters.length];
		for (int i = 0; i < analysisParameters.length; i++) {
			referenceDataArr[i] = createReferenceData(frapDataTimeStamps,0,"",null);//TODO: to remove null add selected ROI types new SimpleReferenceData(expRefDataLabels, expRefDataWeights, expRefDataColumns);
		}
		return referenceDataArr;
	}
	private boolean isROITypeOK(String argROIName){
		boolean bFound = false;
		for (int i = 0; i < ORDERED_ROINAMES.length; i++) {
			if(ORDERED_ROINAMES[i].equals(argROIName)){
				bFound = true;
				break;
			}
		}
		return bFound;
	}
	public ReferenceData createReferenceData(double[] frapDataTimeStamps,int startTimeIndex,String description, boolean[] selectedROIs){
		
		int numSelectedROITypes = 0;
		for(int i=0; i<selectedROIs.length; i++)
		{
			if(selectedROIs[i])
			{
				numSelectedROITypes ++;
			}
		}
		String[] expRefDataLabels = new String[numSelectedROITypes+1];
		double[] expRefDataWeights = new double[numSelectedROITypes+1];
		double[][] expRefDataColumns = new double[numSelectedROITypes+1][];//store exp data, first column t, then columns for data according to selected ROItype
		expRefDataLabels[0] = "t";
		expRefDataWeights[0] = 1.0;
		double[] truncatedTimes = new double[frapDataTimeStamps.length-startTimeIndex];
		System.arraycopy(frapDataTimeStamps, startTimeIndex, truncatedTimes, 0, truncatedTimes.length);
		expRefDataColumns[0] = truncatedTimes;
		int colCounter =  0;//already take "t" colume into account, "t" column is at the column index 0.
		for (int j = 0; j < FRAPData.VFRAP_ROI_ENUM.values().length; j++) {
			if(!selectedROIs[j])//skip unselected ROIs
			{
				continue;
			}
			colCounter ++; 
			String currentROIName = FRAPData.VFRAP_ROI_ENUM.values()[j].name();
			expRefDataLabels[colCounter] = (description == null?"":description)+currentROIName;
			expRefDataWeights[colCounter] = 1.0;
			double[] allTimesData = curveHash.get(new CurveInfo(null,currentROIName));//get exp data in curveHash for current ROItype
			double[] truncatedTimesData = new double[allTimesData.length-startTimeIndex];
			System.arraycopy(allTimesData, startTimeIndex, truncatedTimesData, 0, truncatedTimesData.length);
			expRefDataColumns[colCounter] = truncatedTimesData;
		}
		return new SimpleReferenceData(expRefDataLabels, expRefDataWeights, expRefDataColumns);
	}
	public ODESolverResultSet[] createODESolverResultSetForAllDiffusionRates(){
		ODESolverResultSet[] odeSolverResultSetArr = new ODESolverResultSet[analysisParameters.length];
		for (int i = 0; i < analysisParameters.length; i++) {
			odeSolverResultSetArr[i] = createODESolverResultSet(analysisParameters[i],null,"");//fitOdeSolverResultSet;
		}
		return odeSolverResultSetArr;
	}
	public ODESolverResultSet createODESolverResultSet(AnalysisParameters argAnalysisParameters,String argROIName,String description){
		if(argROIName != null){
			if(!isROITypeOK(argROIName)){
				throw new IllegalArgumentException("couldn't find ROIType "+argROIName);
			}
		}
		int analysisParametersIndex = -1;
		for (int i = 0; i < analysisParameters.length; i++) {
			if(analysisParameters[i].equals(argAnalysisParameters)){
				analysisParametersIndex = i;
				break;
			}
		}
		if(analysisParametersIndex == -1){
			throw new IllegalArgumentException("couldn't find AnalysisParameteers "+analysisParametersIndex);
		}
		int numROITypes = (argROIName == null? SpatialAnalysisResults.ORDERED_ROINAMES.length:1);
		ODESolverResultSet fitOdeSolverResultSet = new ODESolverResultSet();
		fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
		for (int j = 0; j < numROITypes; j++) {
			String currentROIName = (argROIName == null? SpatialAnalysisResults.ORDERED_ROINAMES[j]:argROIName);
			String name = (description == null?/*"sim D="+diffusionRates[diffusionRateIndex]+"::"*/"":description)+currentROIName;
			fitOdeSolverResultSet.addDataColumn(new ODESolverResultSetColumnDescription(name));
		}
		//
		// populate time
		//
		for (int j = 0; j < shiftedSimTimes.length; j++) {
			double[] row = new double[numROITypes+1];
			row[0] = shiftedSimTimes[j];
			fitOdeSolverResultSet.addRow(row);
		}
		//
		// populate values
		//
		for (int j = 0; j < numROITypes; j++) {
			String currentROIName = (argROIName == null? SpatialAnalysisResults.ORDERED_ROINAMES[j]:argROIName);
			double[] values = curveHash.get(new CurveInfo(analysisParameters[analysisParametersIndex],currentROIName)); // get simulated data for this ROI
			for (int k = 0; k < values.length; k++) {
				fitOdeSolverResultSet.setValue(k, j+1, values[k]);
			}
		}
		return fitOdeSolverResultSet;
	}
}//end of class SpatialAnalysisResults
