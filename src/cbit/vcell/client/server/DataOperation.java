package cbit.vcell.client.server;

import java.io.Serializable;

import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.client.data.OutputContext;
import cbit.vcell.client.server.DataOperationResults.DataProcessingOutputInfo;

public class DataOperation implements Serializable{
	
	public static class DataProcessingOutputInfoOP extends DataOperation{
		private boolean bIncludeStatValuesInInfo;
		private OutputContext outputContext;
		public DataProcessingOutputInfoOP(VCDataIdentifier vcDataIdentifier,boolean bIncludeStatValuesInInfo,OutputContext outputContext){
			super(vcDataIdentifier);
			this.bIncludeStatValuesInInfo = bIncludeStatValuesInInfo;
			this.outputContext = outputContext;
		}
		public boolean shouldIncludeStatValuesInInfo(){
			return bIncludeStatValuesInInfo;
		}
		public OutputContext getOutputContext(){
			return outputContext;
		}
	}
	public static class DataProcessingOutputDataValuesOP extends DataOperation{
		public static class DataIndexHelper{
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
		public static class TimePointHelper{
			private Double timePoint;
			private TimePointHelper(Double timePoint){
				this.timePoint = timePoint;
			}
			public static TimePointHelper createAllTimeTimePointHelper(){
				return new TimePointHelper(null);
			}
			public static TimePointHelper createSingletimetimePointHelper(double timePoint){
				return new TimePointHelper(timePoint);
			}
			public double getTimePoint(){
				if(timePoint == null){
					throw new RuntimeException("Call isAllTimePoints before using this method");
				}
				return timePoint;
			}
			public boolean isAllTimePoints(){
				return timePoint == null;
			}
			public boolean isSingleTimePoint(){
				return !isAllTimePoints();
			}
		}
		private String variableName;
		private TimePointHelper timePointHelper;
		private DataIndexHelper dataIndexHelper;
		private OutputContext outputContext;
		public DataProcessingOutputDataValuesOP(VCDataIdentifier vcDataIdentifier,String variableName,TimePointHelper timePointHelper,DataIndexHelper dataIndexHelper,OutputContext outputContext){
			super(vcDataIdentifier);
			this.variableName = variableName;
			this.timePointHelper = timePointHelper;
			this.dataIndexHelper = dataIndexHelper;
			this.outputContext = outputContext;
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
		public OutputContext getOutputContext(){
			return outputContext;
		}
	}

	private VCDataIdentifier vcDataIdentifier;
	
	private DataOperation(VCDataIdentifier vcDataIdentifier){
		this.vcDataIdentifier = vcDataIdentifier;
	}
	
	public VCDataIdentifier getVCDataIdentifier(){
		return vcDataIdentifier;
	}
	
}
