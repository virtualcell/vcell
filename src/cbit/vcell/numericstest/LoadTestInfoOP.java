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

import java.math.BigDecimal;

import cbit.vcell.numericstest.LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp;

public class LoadTestInfoOP extends TestSuiteOP {
	public enum LoadTestOpFlag {info,delete};
	private LoadTestOpFlag opFlag;
	private LoadTestSoftwareVersionTimeStamp[] loadTestSoftwareVersionTimeStamp;
	private Integer slowLoadThresholdMilliSec;
	
	public LoadTestInfoOP(LoadTestOpFlag opFlag,Integer slowLoadThresholdMilliSec){
		this((BigDecimal)null);
		this.opFlag = opFlag;
		this.slowLoadThresholdMilliSec = slowLoadThresholdMilliSec;
	}
	protected LoadTestInfoOP(BigDecimal argTSKey) {
		super(argTSKey);
		// TODO Auto-generated constructor stub
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
}
