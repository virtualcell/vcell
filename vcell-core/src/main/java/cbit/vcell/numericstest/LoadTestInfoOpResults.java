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
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.document.KeyValue;


public class LoadTestInfoOpResults extends TestSuiteOPResults {

	public static final String MODELTYPE_MATH = "MATH";
	public static final String MODELTYPE_BIO = "BIO";
	
	public static class LoadTestDetails {
		public String permission;
		public String userid;
		public String modelType;
		public String modelName;
		public KeyValue modelKeyValue;
		public String versionDate;
		
		public LoadTestDetails(String permission,String userid,String modelType,String modelName,KeyValue modelKeyValue,String versionDate){
			this.permission = permission;
			this.userid = userid;
			this.modelType = modelType;
			this.modelName = modelName;
			this.modelKeyValue = modelKeyValue;
			this.versionDate = versionDate;
		}

	}
	public static class LoadTestFailDetails extends LoadTestDetails{
		public String errorMessage;
		
		public LoadTestFailDetails(String permission,String userid,String modelType,String modelName,KeyValue modelKeyValue,String versionDate,String errorMessage){
			super(permission, userid, modelType, modelName, modelKeyValue, versionDate);
			this.errorMessage = errorMessage;
		}

	}
	public static class LoadTestSlowDetails extends LoadTestDetails{
		public Integer loadTime;
		
		public LoadTestSlowDetails(String permission,String userid,String modelType,String modelName,KeyValue modelKeyValue,String versionDate,Integer loadTime){
			super(permission, userid, modelType, modelName, modelKeyValue, versionDate);
			this.loadTime = loadTime;
		}

	}

	public static class LoadTestSoftwareVersionTimeStamp{
		private String softwareVersion;
		private String runTimeStamp;
		public LoadTestSoftwareVersionTimeStamp(String softwareVersion,String runTimeStamp){
			this.softwareVersion = softwareVersion;
			this.runTimeStamp = runTimeStamp;
		}
		public String getSoftwareVersion(){
			return softwareVersion;
		}
		public String getRunTimeStamp(){
			return runTimeStamp;
		}
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof LoadTestSoftwareVersionTimeStamp)){
				return false;
			}
			return
			((LoadTestSoftwareVersionTimeStamp)obj).softwareVersion.equals(softwareVersion) &&
			((LoadTestSoftwareVersionTimeStamp)obj).runTimeStamp.equals(runTimeStamp);
		}
		@Override
		public int hashCode() {
			return runTimeStamp.hashCode();
		}
		
	}
	
	private Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestFailDetails>>
		loadTestFailHash;
	private Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestSlowDetails>>
		loadTestSlowHash;
	private Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestDetails>>
		loadTestUserQueryHash;

	private LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp[] loadTestSoftwareVersionTimeStamps;
	private int totalBioAndMathmodelCount;
	private Integer[] loadTestInfoCounts;
	private Integer[] loadTestInfoEmptyCounts;
	private Integer slowLoadThresholdMillisec;
	public LoadTestInfoOpResults(
			Integer[] loadTestInfoCounts,
			Integer[] loadTestInfoEmptyCounts,
			int totalBioAndMathmodelCount,
			LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp[] loadTestSoftwareVersionTimeStamps,
			Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestFailDetails>> loadTestFailHash,
			Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestSlowDetails>> loadTestSlowHash,
			Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestDetails>> loadTestUserQueryHash,
			Integer slowLoadThresholdMillisec){
		super(null);
		this.loadTestFailHash = loadTestFailHash;
		this.loadTestSlowHash = loadTestSlowHash;
		this.loadTestSoftwareVersionTimeStamps = loadTestSoftwareVersionTimeStamps;
		this.totalBioAndMathmodelCount = totalBioAndMathmodelCount;
		this.loadTestInfoCounts = loadTestInfoCounts;
		this.loadTestInfoEmptyCounts = loadTestInfoEmptyCounts;
		this.slowLoadThresholdMillisec = slowLoadThresholdMillisec;
		this.loadTestUserQueryHash = loadTestUserQueryHash;
	}
	public Integer getSlowLoadThresholdMillisec(){
		return slowLoadThresholdMillisec;
	}
	public Integer[] getLoadTestInfoCounts(){
		return loadTestInfoCounts;
	}
	public Integer[] getLoadTestInfoEmptyCounts(){
		return loadTestInfoEmptyCounts;
	}
	public int getTotalBioAndMathmodelCount(){
		return totalBioAndMathmodelCount;
	}
	public Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestFailDetails>>
		getLoadTestFailHash(){
			return loadTestFailHash;
	}
	public Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestSlowDetails>>
	getLoadTestSlowHash(){
		return loadTestSlowHash;
	}
	public Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestDetails>>
	getLoadTestUserQueryHash(){
		return loadTestUserQueryHash;
	}

	public LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp[] getLoadTestSoftwareVersionTimeStamps(){
		return loadTestSoftwareVersionTimeStamps;
	}
	
	
	private Date beginDate;
	private Date endDate;
	private KeyValue[] keyValuesBetweenDates;
	private String[] uniqueUserIDsBetweenDates;
	public LoadTestInfoOpResults(Date beginDate,Date endDate,KeyValue[] keyValuesBetweenDates,String[] uniqueUserIDsBetweenDates){
		super(null);
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.keyValuesBetweenDates = keyValuesBetweenDates;
		this.uniqueUserIDsBetweenDates = uniqueUserIDsBetweenDates;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public KeyValue[] getKeyValuesBetweenDates() {
		return keyValuesBetweenDates;
	}
	public String[] getUniqueUserIDsBetweenDates() {
		return uniqueUserIDsBetweenDates;
	}
}
