package cbit.vcell.client.server;

import java.io.Serializable;

import org.vcell.util.document.VCDataIdentifier;

public class DataOperation implements Serializable{
	
	public static class DataProcessingOutputInfoOP extends DataOperation{
		public DataProcessingOutputInfoOP(VCDataIdentifier vcDataIdentifier){
			super(vcDataIdentifier);
		}
	}
	public static class DataProcessingOutputDataValuesOP extends DataOperation{
		private String variableName;
		private Double timePoint;
		private Integer slice;
		private DataProcessingOutputDataValuesOP(VCDataIdentifier vcDataIdentifier,String variableName,Double timePoint,Integer slice){
			super(vcDataIdentifier);
			this.variableName = variableName;
			this.timePoint = timePoint;
			this.slice = slice;
		}
		public DataProcessingOutputDataValuesOP(VCDataIdentifier vcDataIdentifier,String variableName,Double timePoint){
			this(vcDataIdentifier,variableName,timePoint,null);
		}
		public DataProcessingOutputDataValuesOP(VCDataIdentifier vcDataIdentifier,String variableName,Integer slice){
			this(vcDataIdentifier,variableName,null,slice);
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
	}

	private VCDataIdentifier vcDataIdentifier;
	
	private DataOperation(VCDataIdentifier vcDataIdentifier){
		this.vcDataIdentifier = vcDataIdentifier;
	}
	
	public VCDataIdentifier getVCDataIdentifier(){
		return vcDataIdentifier;
	}
	
}
