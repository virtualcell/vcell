package cbit.vcell.simdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.Range;
import org.vcell.util.document.TimeSeriesJobResults;
import org.vcell.util.document.VCDataIdentifier;

import cbit.image.SourceDataInfo;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.DataIndexHelper;
import cbit.vcell.simdata.DataOperation.DataProcessingOutputDataValuesOP.TimePointHelper;

public class DataOperationResults implements Serializable{
	private static final long serialVersionUID = -656370653400895175L;

	public static class DataProcessingOutputTimeSeriesValues extends DataOperationResults{
		private TimeSeriesJobResults timeSeriesJobResults;
		public DataProcessingOutputTimeSeriesValues(VCDataIdentifier vcDataIdentifier,TimeSeriesJobResults timeSeriesJobResults){
			super(vcDataIdentifier);
			this.timeSeriesJobResults = timeSeriesJobResults;
		}
		public TimeSeriesJobResults getTimeSeriesJobResults(){
			return timeSeriesJobResults;
		}
	}
	
	public static class DataProcessingOutputDataValues extends DataOperationResults{
		private String variableName;
		private TimePointHelper timePointHelper;
		private DataIndexHelper dataIndexHelper;
		private double[][] dataValues;//[time][data]
		public DataProcessingOutputDataValues(VCDataIdentifier vcDataIdentifier,String variableName,TimePointHelper timePointHelper,DataIndexHelper dataIndexHelper,double[][] dataValues){
			super(vcDataIdentifier);
			this.variableName = variableName;
			this.timePointHelper = timePointHelper;
			this.dataValues = dataValues;
			this.dataIndexHelper = dataIndexHelper;
		}
		public String getVariableName() {
			return variableName;
		}
		public TimePointHelper getTimePointHelper() {
			return timePointHelper;
		}
		public DataIndexHelper getDataIndexHelper(){
			return dataIndexHelper;
		}
		public double[][] getDataValues(){
			return dataValues;
		}
		public ArrayList<SourceDataInfo> createSourceDataInfos(ISize iSize,Origin origin,Extent extent){//[time][slice]
			ArrayList<SourceDataInfo> sdiInfoArr = new ArrayList<SourceDataInfo>();
			for (int i = 0; i < dataValues.length; i++) {
				double min = dataValues[i][0];
				double max = min;
				for (int j = 0; j < dataValues[i].length; j++) {
					min = Math.min(min, dataValues[i][j]);
					max = Math.max(max, dataValues[i][j]);
				}
				int xySize = iSize.getX()*iSize.getY();
				Range range = new Range(min,max);
//				System.out.println("isize="+iSize.getXYZ()+" datalength="+dataValues[i].length+" range="+range);
				SourceDataInfo sdi =
						new SourceDataInfo(SourceDataInfo.RAW_VALUE_TYPE, dataValues[i], extent, origin, range, 0, iSize.getX(), 1, iSize.getY(), iSize.getX(), (dataIndexHelper.isAllDataIndexes()?iSize.getZ():1), (dataIndexHelper.isAllDataIndexes()?xySize:0));
				sdiInfoArr.add(sdi);
			}
			return sdiInfoArr;
		}
	}
	public static class DataProcessingOutputInfo extends DataOperationResults{
		public static enum PostProcessDataType {image,statistic}
		private String[] variableNames;
		private ISize[] variableISize;
		private Extent[] variableExtents;
		private Origin[] variableOrigins;
		private double[] variableTimePoints;
		private String[] variableUnits;
		private PostProcessDataType[] postProcessDataTypes;
		private HashMap<String, double[]> variableStatValues;
		private HashMap<String, String> mapFunctionNameToStateVarName;
		public DataProcessingOutputInfo(VCDataIdentifier vcDataIdentifier,String[] variableNames,ISize[] variableISize,double[] variableTimePoints,String[] variableUnits,PostProcessDataType[] postProcessDataTypes,Origin[] variableOrigins,Extent[] variableExtents,HashMap<String, double[]> variableStatValues){
			super(vcDataIdentifier);
			this.variableNames = variableNames;
			this.variableISize = variableISize;
			this.variableTimePoints = variableTimePoints;//Assume all data share same timepoints
			this.variableUnits = variableUnits;
			this.postProcessDataTypes = postProcessDataTypes;
			this.variableOrigins = variableOrigins;
			this.variableExtents = variableExtents;
			this.variableStatValues = variableStatValues;
		}
		public DataProcessingOutputInfo(DataProcessingOutputInfo dataProcessingOutputInfo,HashMap<String, String> mapFunctionNameToStateVarName){
			this(
					dataProcessingOutputInfo.getVCDataIdentifier(),
					dataProcessingOutputInfo.variableNames,
					dataProcessingOutputInfo.variableISize,
					dataProcessingOutputInfo.variableTimePoints,
					dataProcessingOutputInfo.variableUnits,
					dataProcessingOutputInfo.postProcessDataTypes,
					dataProcessingOutputInfo.variableOrigins,
					dataProcessingOutputInfo.variableExtents,
					dataProcessingOutputInfo.variableStatValues
			);
			this.mapFunctionNameToStateVarName = mapFunctionNameToStateVarName;
		}
		public HashMap<String, double[]> getVariableStatValues(){
			return variableStatValues;
		}
		public String[] getVariableNames(){
			return variableNames;
		}
		public ISize getVariableISize(String variableName) {
			return variableISize[getVariableIndex(variableName)];
		}
		public Extent getVariableExtent(String variableName) {
			return variableExtents[getVariableIndex(variableName)];
		}
		public Origin getVariableOrigin(String variableName) {
			return variableOrigins[getVariableIndex(variableName)];
		}		
		public double[] getVariableTimePoints() {
			return variableTimePoints;
		}
		public String getVariableUnits(String variableName){
			if(variableUnits == null){
				return null;
			}
			String varUnits = variableUnits[getVariableIndex(variableName)];
			return (varUnits.length()==0?null:varUnits);
		}
//		public boolean areAllTimesEqual(){
//			for (int i = 0; i < variableTimePoints.length; i++) {
//				if(variableTimePoints[i].length != variableTimePoints[0].length){
//					return false;
//				}
//				for (int j = 0; j < variableTimePoints[i].length; j++) {
//					if(variableTimePoints[i][j] != variableTimePoints[0][j]){
//						return false;
//					}
//				}
//			}
//			return true;
//		}
		private int getVariableIndex(String variableName){
			if(mapFunctionNameToStateVarName != null && mapFunctionNameToStateVarName.keySet().contains(variableName)){
				variableName = mapFunctionNameToStateVarName.get(variableName);
			}
			for (int i = 0; i < variableNames.length; i++) {
				if(variableNames[i].equals(variableName)){
					return i;
				}
			}
			throw new RuntimeException("Error DataProcessingOutputInfo: Couldn't find variable name "+variableName);
		}
		public PostProcessDataType getPostProcessDataType(String variableName){
			return postProcessDataTypes[getVariableIndex(variableName)];
		}
//		public int getVariableDimension(String variableName){
//			int variableIndex = getVariableIndex(variableName);
//			return 1+(variableDimensions[variableIndex].getY()>1?1:0)+(variableDimensions[variableIndex].getZ()>1?1:0);
//		}
	}
	
	private VCDataIdentifier vcDataIdentifier;

	private DataOperationResults(VCDataIdentifier vcDataIdentifier){
		this.vcDataIdentifier = vcDataIdentifier;
	}

	public VCDataIdentifier getVCDataIdentifier(){
		return vcDataIdentifier;
	}
	

}
