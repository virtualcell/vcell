package cbit.vcell.simdata;

import java.io.Serializable;

import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VCDataJobID;

public class DataOperation implements Serializable{
	private static final long serialVersionUID = 6857219802563294204L;
	
	public static class DataProcessingOutputInfoOP extends DataOperation{
		private boolean bIncludeStatValuesInInfo;
		public DataProcessingOutputInfoOP(VCDataIdentifier vcDataIdentifier,boolean bIncludeStatValuesInInfo,OutputContext outputContext){
			super(vcDataIdentifier,outputContext);
			this.bIncludeStatValuesInInfo = bIncludeStatValuesInInfo;
		}
		public boolean shouldIncludeStatValuesInInfo(){
			return bIncludeStatValuesInInfo;
		}
	}
	public static class DataProcessingOutputDataValuesOP extends DataOperation{
		public static class DataIndexHelper implements Serializable{
			private boolean isSingleSlice;
			private int[] dataIndexes;
			private DataIndexHelper(){
				this(false,null);
			}
			private DataIndexHelper(boolean isSingleSlice, int[] dataIndexes) {
				if((isSingleSlice && (dataIndexes==null || dataIndexes.length != 1))){
					throw new IllegalArgumentException("Wrong ars for DataIndexHelper");
				}
				this.isSingleSlice = isSingleSlice;
				this.dataIndexes = dataIndexes;
			}
			public static DataIndexHelper createSliceDataIndexHelper(int sliceIndex){
				return new DataIndexHelper(true,new int[] {sliceIndex});
			}
			public static DataIndexHelper createAllDataIndexesDataIndexHelper(){
				return new DataIndexHelper(false,null);
			}
			public static DataIndexHelper createSpecificDataIndexHelper(int[] specificIndexes){
				return new DataIndexHelper(false,specificIndexes);
			}
			public boolean isSingleSlice(){
				return isSingleSlice;
			}
			public boolean isAllDataIndexes(){
				return dataIndexes==null;
			}
			public int[] getDataIndexes(){
				if(isSingleSlice || dataIndexes == null){
					throw new RuntimeException("Call isAllDataIndexes before using this method");
				}
				return dataIndexes;
			}
			public int getSliceIndex(){
				return dataIndexes[0];
			}
		}
		public static class TimePointHelper implements Serializable{
			private double[] specificTimePoints;
			private TimePointHelper(double[] specificTimePoints){
				this.specificTimePoints = specificTimePoints;
			}
			public static TimePointHelper createAllTimeTimePointHelper(){
				return new TimePointHelper(null);
			}
			public static TimePointHelper createSingleTimeTimePointHelper(double timePoint){
				return new TimePointHelper(new double[] {timePoint});
			}
			public static TimePointHelper createSpecificTimePointHelper(double[] specificTimepoints){
				return new TimePointHelper(specificTimepoints);
			}
			public double[] getTimePoints(){
				if(specificTimePoints == null){
					throw new RuntimeException("Call isAllTimePoints or isSpecificTimePoints before using this method");
				}
				return specificTimePoints;
			}
			public boolean isAllTimePoints(){
				return specificTimePoints == null;
			}
			public boolean isSingleTimePoint(){
				return specificTimePoints!= null && specificTimePoints.length==1;
			}
			public boolean isSpecificTimePoints(){
				return specificTimePoints!= null && specificTimePoints.length>1;
			}
		}
		private String variableName;
		private TimePointHelper timePointHelper;
		private DataIndexHelper dataIndexHelper;
		private VCDataJobID vcDataJobID;
		public DataProcessingOutputDataValuesOP(VCDataIdentifier vcDataIdentifier,String variableName,TimePointHelper timePointHelper,DataIndexHelper dataIndexHelper,OutputContext outputContext,VCDataJobID vcDataJobID){
			super(vcDataIdentifier,outputContext);
			this.variableName = variableName;
			this.timePointHelper = timePointHelper;
			this.dataIndexHelper = dataIndexHelper;
			this.vcDataJobID =  vcDataJobID;
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
		public VCDataJobID getVCDataJobID(){
			return vcDataJobID;
		}
	}
	
	
	public static class DataProcessingOutputTimeSeriesOP extends DataOperation{
		private TimeSeriesJobSpec timeSeriesJobSpec;
		private double[] allDatasetTimes;
		public DataProcessingOutputTimeSeriesOP(VCDataIdentifier vcDataIdentifier,TimeSeriesJobSpec timeSeriesJobSpec,OutputContext outputContext,double[] allDatasetTimes){
			super(vcDataIdentifier,outputContext);
			this.timeSeriesJobSpec = timeSeriesJobSpec;
			this.allDatasetTimes = allDatasetTimes;
		}
		public TimeSeriesJobSpec getTimeSeriesJobSpec(){
			return timeSeriesJobSpec;
		}
		public double[] getAllDatasetTimes(){
			return allDatasetTimes;
		}
	}
	
	
	private VCDataIdentifier vcDataIdentifier;
	private OutputContext outputContext;
	private DataOperation(VCDataIdentifier vcDataIdentifier,OutputContext outputContext){
		this.vcDataIdentifier = vcDataIdentifier;
		this.outputContext = outputContext;
	}
	
	public VCDataIdentifier getVCDataIdentifier(){
		return vcDataIdentifier;
	}
	public OutputContext getOutputContext(){
		return outputContext;
	}
}
