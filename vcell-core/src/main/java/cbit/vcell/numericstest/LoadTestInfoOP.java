/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.numericstest;

import java.util.Date;

import cbit.vcell.numericstest.LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp;

public class LoadTestInfoOP extends TestSuiteOP {
	public enum LoadTestOpFlag {info,delete};
	private LoadTestOpFlag opFlag;
	private LoadTestSoftwareVersionTimeStamp[] loadTestSoftwareVersionTimeStamp;
	private Integer slowLoadThresholdMilliSec;
	private String sql;
	private Date beginDate;
	private Date endDate;
	
	public LoadTestInfoOP(LoadTestOpFlag opFlag,Integer slowLoadThresholdMilliSec,String sql){
		super(null);
		this.opFlag = opFlag;
		this.slowLoadThresholdMilliSec = slowLoadThresholdMilliSec;
		this.sql = sql;
	}
	public LoadTestInfoOP(Date beginDate, Date endDate){
		super(null);
		this.opFlag = LoadTestOpFlag.info;
		this.beginDate = beginDate;
		this.endDate = endDate;
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
	public Date getBeginDate() {
		return beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
}
