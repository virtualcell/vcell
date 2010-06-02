package cbit.vcell.numericstest;

import java.math.BigDecimal;

import cbit.vcell.numericstest.LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp;

public class LoadTestInfoOP extends TestSuiteOP {
	public enum LoadTestOpFlag {info,delete};
	private LoadTestOpFlag opFlag;
	private LoadTestSoftwareVersionTimeStamp[] loadTestSoftwareVersionTimeStamp;
	private Integer slowLoadThresholdMilliSec;
	private String sql;
	
	public LoadTestInfoOP(LoadTestOpFlag opFlag,Integer slowLoadThresholdMilliSec,String sql){
		super(null);
		this.opFlag = opFlag;
		this.slowLoadThresholdMilliSec = slowLoadThresholdMilliSec;
		this.sql = sql;
	}
	public LoadTestOpFlag getLoadTestOpFlag(){
		return opFlag;
	}
	public void setDeleteInfo(LoadTestSoftwareVersionTimeStamp[] loadTestSoftwareVersionTimeStamp){
		if(opFlag != LoadTestOpFlag.delete){
			throw new IllegalArgumentException("Not a delete LoadTestInfoOp");
		}
		this.loadTestSoftwareVersionTimeStamp = loadTestSoftwareVersionTimeStamp;
	}
	public LoadTestSoftwareVersionTimeStamp[] getLoadTestSoftwareVersionTimeStamps(){
		return loadTestSoftwareVersionTimeStamp;
	}
	public Integer getSlowLoadThresholdMilliSec(){
		return slowLoadThresholdMilliSec;
	}
	public String getUserQueryCondition(){
		return sql;
	}
}
