package cbit.vcell.client.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.Range;
import org.vcell.util.document.VCDataIdentifier;

import cbit.image.gui.SourceDataInfo;

public class DataOperationResults implements Serializable{

	public static class DataProcessingOutputDataValues extends DataOperationResults{
		private String variableName;
		private Double timePoint;
		private Integer slice;
		private double[][] dataValues;//[all Times][1 slice] or [1 time][all slices]
		public DataProcessingOutputDataValues(VCDataIdentifier vcDataIdentifier,String variableName,Double timePoint,double[][] dataValues){
			//mode 1:  timepoint != null return all slices for var at time==timepoint
			//mode 2:  slice != null return all slice data for all times
			super(vcDataIdentifier);
			this.variableName = variableName;
			this.timePoint = timePoint;
			this.dataValues = dataValues;
			this.slice = null;
		}
		public DataProcessingOutputDataValues(VCDataIdentifier vcDataIdentifier,String variableName,Integer slice,double[][] dataValues){
			//mode 1:  timepoint != null return all slices for var at time==timepoint
			//mode 2:  slice != null return all slice data for all times
			super(vcDataIdentifier);
			this.variableName = variableName;
			this.timePoint = null;
			this.dataValues = dataValues;
			this.slice = slice;
		}
		public String getVariableName() {
			return variableName;
		}
		public Double getTimePoint() {
			return timePoint;
		}
		public Integer getSlice(){
			return slice;
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
						new SourceDataInfo(SourceDataInfo.RAW_VALUE_TYPE, dataValues[i], extent, origin, range, 0, iSize.getX(), 1, iSize.getY(), iSize.getX(), (slice==null?iSize.getZ():1), (slice==null?xySize:0));
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
