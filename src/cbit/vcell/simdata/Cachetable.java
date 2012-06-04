/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;

import java.util.Hashtable;
import java.util.Enumeration;

import org.vcell.util.CacheException;
import org.vcell.util.CacheStatus;
import org.vcell.util.Ping;
import org.vcell.util.Pingable;
import org.vcell.util.PropertyLoader;
import org.vcell.util.TimeWrapper;
import org.vcell.util.document.VCDataIdentifier;
/**
 * This type was created in VisualAge.
 */
public class Cachetable implements Pingable {
	public static final long minute = 60000;

	private double costRatio_MegaBytes_per_Minute = 1.0;
	private double costRatio_Bytes_per_MilliSecond = costRatio_MegaBytes_per_Minute * 1e6 / minute;

	private double costPerMilliSecond = 1.0;
	private double costPerByte = costRatio_Bytes_per_MilliSecond*costPerMilliSecond;

	private long expireTime;
	private Ping cleaner;
	private Hashtable hashTable = null;
	private long currMemSize = 0;
	private long maxMemSize = 0;
	private double cleanupFraction = 0.8;

	private boolean bQuiet = true;
/**
 * This method was created in VisualAge.
 * @param expireTime long
 */
public Cachetable(long expireTime) {
	this(expireTime,0);
}
/**
 * This method was created in VisualAge.
 * @param expireTime long
 */
private Cachetable(long expireTime, long maxMemSize) {
	//
	// if not specified, use half of memory
	//
	if (maxMemSize==0){
		try {
			String cacheSize = System.getProperty(PropertyLoader.simdataCacheSizeProperty);
			maxMemSize = Long.parseLong(cacheSize);
			System.out.print("SimData cache size="+maxMemSize+" (given by property file)");
		}catch (Exception e){
			maxMemSize = Runtime.getRuntime().totalMemory()/4;
			System.out.println("SimData cache size="+maxMemSize+" (given by 1/4 of Runtime.getRuntime().totalMemory())");
		}
	}else{
		System.out.println("SimData cache size="+maxMemSize+" (forced by application)");
	}
	
	System.out.println(" SimData object expiration="+(expireTime/1000.0)+" seconds");
	
	this.expireTime = expireTime;
	cleaner = new Ping(this, ((long) (expireTime * 0.75)),"SimResultsCacheReaper");
	cleaner.start();
	this.hashTable = new Hashtable();
	this.maxMemSize = maxMemSize;
}
/**
 * This method was created in VisualAge.
 * @return long
 */
private synchronized long calcSize() {
	long count = 0;
	Enumeration enum1 = hashTable.elements();
	while (enum1.hasMoreElements()){
		TimeWrapper timeWrapper = (TimeWrapper)enum1.nextElement();
		count += timeWrapper.getSize();
	}
	return count;
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/01 9:22:19 AM)
 */
protected void finalize() {
	if (cleaner!=null){
		cleaner.kill();
	}
}
public VCData get(VCDataIdentifier vcDataId) {
	TimeWrapper timeWrapper = get0(vcDataId);
	if (timeWrapper!=null){
		return (VCData)timeWrapper.getObject();
	}else{
		return null;
	}
}
public ODEDataBlock get(ODEDataInfo odeDataInfo) {
	TimeWrapper timeWrapper = get0(odeDataInfo);
	if (timeWrapper!=null){
		return (ODEDataBlock)timeWrapper.getObject();
	}else{
		return null;
	}
}
public ParticleDataBlock get(ParticleDataInfo particleDataInfo) {
	TimeWrapper timeWrapper = get0(particleDataInfo);
	if (timeWrapper!=null){
		return (ParticleDataBlock)timeWrapper.getObject();
	}else{
		return null;
	}
}
public SimDataBlock get(PDEDataInfo pdeDataInfo) {
	TimeWrapper timeWrapper = get0(pdeDataInfo);
	if (timeWrapper!=null){
		return (SimDataBlock)timeWrapper.getObject();
	}else{
		return null;
	}
}
private synchronized TimeWrapper get0(Object key) {
	TimeWrapper timeWrapper = (TimeWrapper)hashTable.get(key);
	if (timeWrapper!=null){
//System.out.println("........Cachetable.get(key="+key+") <<<returning object>>>");
		return timeWrapper;
	}else{
//System.out.println("........Cachetable.get(key="+key+") <<<missed>>>");
		return null;
	}
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.CacheStatus
 */
public CacheStatus getCacheStatus() {
	return new CacheStatus(hashTable.size(),currMemSize,maxMemSize);
}
public synchronized void ping() {
	boolean bRemovedAny = false;
	Enumeration e = hashTable.keys();
	while (e.hasMoreElements()) {
		Object key = e.nextElement();
		TimeWrapper tw = (TimeWrapper) hashTable.get(key);
		if ((tw != null) && (tw.getAge() > expireTime)) {
			remove0(key);
			bRemovedAny = true;
//			System.out.println("Cachetable.ping(), expiring key="+key.toString());
		}
	}
	if (bRemovedAny){
		show();
	}
//	System.gc();
}
public VCData put(VCDataIdentifier vcdID, VCData data) throws CacheException {

	VCData oldData = null;
			
	TimeWrapper oldTimeWrapper = put(vcdID, new TimeWrapper(data,data.getSizeInBytes(),vcdID));

	if (oldTimeWrapper!=null){
		oldData = (VCData)oldTimeWrapper.getObject();
	}

	show();	
	
	return oldData;
}
public ODEDataBlock put(ODEDataInfo odeDataInfo, ODEDataBlock odeDataBlock) throws CacheException {

	ODEDataBlock oldODEDataBlock = null;
	
	TimeWrapper oldTimeWrapper = put(odeDataInfo, new TimeWrapper(odeDataBlock,odeDataBlock.getEstimatedSizeInBytes(), odeDataInfo));

	if (oldTimeWrapper!=null){
		oldODEDataBlock = (ODEDataBlock)oldTimeWrapper.getObject();
	}

	show();	
	
	return oldODEDataBlock;
}
public ParticleDataBlock put(ParticleDataInfo particleDataInfo, ParticleDataBlock particleDataBlock) throws CacheException {

	ParticleDataBlock oldParticleDataBlock = null;
			
	TimeWrapper oldTimeWrapper = put(particleDataInfo, new TimeWrapper(particleDataBlock, particleDataBlock.getSizeInBytes(), particleDataInfo));

	if (oldTimeWrapper!=null){
		oldParticleDataBlock = (ParticleDataBlock)oldTimeWrapper.getObject();
	}

	show();	
	
	return oldParticleDataBlock;
}
public SimDataBlock put(PDEDataInfo pdeDataInfo, SimDataBlock simData) throws CacheException {

	SimDataBlock oldSimDataBlock = null;
			
	TimeWrapper oldTimeWrapper = put(pdeDataInfo, new TimeWrapper(simData,simData.getSizeInBytes(),pdeDataInfo));

	if (oldTimeWrapper!=null){
		oldSimDataBlock = (SimDataBlock)oldTimeWrapper.getObject();
	}

	show();	
	
	return oldSimDataBlock;
}
private synchronized TimeWrapper put(Object key, TimeWrapper timeWrapper) throws CacheException {

	long dataMemSize = timeWrapper.getSize();
	
	if (dataMemSize>=maxMemSize){
		throw new CacheException("data item "+key+" with memSize="+dataMemSize+" too large, maxCacheSize="+maxMemSize);
	}
	
	//
	// request to free cleanupFraction of the total size
	//
	if (dataMemSize+currMemSize >= maxMemSize){
		resize(Math.min((long)(maxMemSize*cleanupFraction),maxMemSize-dataMemSize));
	}
		
	TimeWrapper oldTimeWrapper = (TimeWrapper)hashTable.put(key, timeWrapper);

	currMemSize += dataMemSize;
	if (oldTimeWrapper!=null){
		currMemSize -= oldTimeWrapper.getSize();
	}

	if (currMemSize<0 || currMemSize>=maxMemSize){
		throw new CacheException("Error: adding data item "+key+". currMemSize="+currMemSize+" maxMemSize="+maxMemSize);
	}
	//System.out.println("........Cachetable.put(key="+key+")");
	return oldTimeWrapper;
}
/**
 * This method was created in VisualAge.
 * @param key java.lang.Double
 */
private synchronized Object remove0(Object key) {

	TimeWrapper oldTimeWrapper = (TimeWrapper)hashTable.remove(key);
	
	if (oldTimeWrapper!=null){
		currMemSize -= oldTimeWrapper.getSize();
	}

	return oldTimeWrapper;
}
/**
 * This method was created in VisualAge.
 * @param key java.lang.Double
 */
public synchronized void removeAll(VCDataIdentifier vcDataID) {
	
	System.out.println("removeAll(vcDataID="+vcDataID+")");

	
	remove0(vcDataID);
	
	Enumeration enum1 = hashTable.elements();
	while (enum1.hasMoreElements()){
		TimeWrapper tw = (TimeWrapper)enum1.nextElement();
		if (tw.getKey() instanceof PDEDataInfo){
			PDEDataInfo pdeDataInfo = (PDEDataInfo)tw.getKey();
			if (pdeDataInfo.belongsTo(vcDataID)){
				remove0(pdeDataInfo);
			}
		}
	}
	
	show();
}
/**
 * This method was created in VisualAge.
 * @param key java.lang.Double
 */
public synchronized void removeVariable(VCDataIdentifier vcDataID, String varName) {
	
	System.out.println("Cachetable.removeVariable(vcDataID="+vcDataID+",varName="+varName+")");

	Enumeration enum1 = hashTable.elements();
	while (enum1.hasMoreElements()){
		TimeWrapper tw = (TimeWrapper)enum1.nextElement();
		if (tw.getKey() instanceof PDEDataInfo){
			PDEDataInfo pdeDataInfo = (PDEDataInfo)tw.getKey();
			if (pdeDataInfo.belongsTo(vcDataID) && pdeDataInfo.getVarName().equals(varName)){
				remove0(pdeDataInfo);
			}
		}
	}
	show();
}
/**
 * This method was created in VisualAge.
 * @param desiredMemSize long
 */
private synchronized void resize(long desiredMemSize) {

	//
	// repeat until the cache is less than the desired memory size
	//
	while (currMemSize>desiredMemSize){
		//
		// calculate the maximum, avg cost
		// decide on a cost threshold for deletion
		//
		double maxCost = -1e10;
		double minCost = 1e10;
		double totalCost = 0;
		long count = 0;
		Enumeration enum1 = hashTable.elements();
		while (enum1.hasMoreElements()){
			TimeWrapper timeWrapper = (TimeWrapper)enum1.nextElement();
			double cost = timeWrapper.getCost(costPerMilliSecond,costPerByte);
			maxCost = Math.max(cost,maxCost);
			minCost = Math.min(cost,minCost);
			totalCost += cost;
			count ++;
		}
		double avgCost = totalCost/count;
		double thresholdCost = avgCost + (maxCost-avgCost)*0.5;
		//
		// delete all records with a cost greater than the threshold
		//
		enum1 = hashTable.elements();
		while (enum1.hasMoreElements()){
			TimeWrapper timeWrapper = (TimeWrapper)enum1.nextElement();
			double cost = timeWrapper.getCost(costPerMilliSecond,costPerByte);
			if (cost>thresholdCost){
				remove0(timeWrapper.getKey());
			}
		}

		//
		// evaluate how we did (may need to increase 'costPerByte' if poor convergence)
		//
		System.out.println("resize(): desiredMemSize="+desiredMemSize+"  cost min="+minCost+", max="+maxCost+", avg="+avgCost+" thresholdCost="+thresholdCost);
		show();
	}
}
/**
 * This method was created in VisualAge.
 */
public void show() {
	if (!bQuiet){
		System.out.println("...simdata.Cachetable ("+hashTable.size()+" entries): currMemSize="+currMemSize+" maxMemSize="+maxMemSize);
	}
}
}
