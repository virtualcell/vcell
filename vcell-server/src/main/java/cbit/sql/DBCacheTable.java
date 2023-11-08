/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.sql;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.*;
import org.vcell.util.document.KeyValue;

import cbit.vcell.resource.PropertyLoader;
/**
 * This type was created in VisualAge.
 */
public class DBCacheTable implements Pingable {
	private final static Logger lg = LogManager.getLogger(DBCacheTable.class);

	public static final long minute = 60000;

	private double costRatio_MegaBytes_per_Minute = 1.0;
	private double costRatio_Bytes_per_MilliSecond = costRatio_MegaBytes_per_Minute * 1e6 / minute;

	private double costPerMilliSecond = 1.0;
	private double costPerByte = costRatio_Bytes_per_MilliSecond*costPerMilliSecond;

	private long expireTimeMillisec;
	private Ping cleaner;
	private Hashtable hashTable = null;
	private long currMemSize = 0;
	private long maxMemSize = 0;
	private double cleanupFraction = 0.8;

public DBCacheTable(long expireTimeMillisec) {
	this(expireTimeMillisec,0);
}

public DBCacheTable(long expireTimeMillisec, long maxMemSize) {
	//
	// if max size not specified, use half of memory
	//
	if (maxMemSize==0){
		try {
			String cacheSize = PropertyLoader.getRequiredProperty(PropertyLoader.databaseCacheSizeProperty);
			maxMemSize = Long.parseLong(cacheSize);
			lg.info("Database cache size="+maxMemSize+" (given by property file)");
		}catch (Exception e){
			maxMemSize = Runtime.getRuntime().totalMemory()/4;
			lg.info("Database cache size="+maxMemSize+" (given by 1/4 of Runtime.getRuntime().totalMemory())");
		}
	}else{
		lg.info("Database cache size="+maxMemSize+" (forced by application)");
	}

	lg.info(" database object expiration="+(expireTimeMillisec/1000.0)+" seconds");
	
	this.expireTimeMillisec = expireTimeMillisec;

	//
	// if expire time not specified, then do not timeout the entries.
	//
	if (expireTimeMillisec>0){
		cleaner = new Ping(this, ((long) (expireTimeMillisec * 0.75)),"DatabaseCacheReaper");
		cleaner.start();
	}
	
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
 * Creation date: (6/25/01 9:18:03 AM)
 */
protected void finalize() {
	if (cleaner!=null){
		cleaner.kill();
	}
}
public synchronized Cacheable get(KeyValue key) {
	TimeWrapper timeWrapper = (TimeWrapper) hashTable.get(key);
	if (timeWrapper != null) {
		DbObjectWrapper objWrapper = (DbObjectWrapper) timeWrapper.getObject();
		Cacheable cacheable = objWrapper.getWorkingCopy();
//lg.trace("DBCacheTable.get: "+cacheable.getClass().getName());
		return cacheable;
	} else {
//lg.trace("DBCacheTable.get: null");
		return null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/30/00 12:03:56 AM)
 * @return cbit.sql.KeyValue
 */
public synchronized KeyValue[] getAllKeys() {
	
	KeyValue keys[] = new KeyValue[hashTable.size()];

	int count = 0;
	Enumeration enum1 = hashTable.keys();
	while (enum1.hasMoreElements()){
		keys[count++] = (KeyValue)enum1.nextElement();
	}
	
	return keys;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.simdata.CacheStatus
 */
public CacheStatus getCacheStatus() {
	return new CacheStatus(hashTable.size(),currMemSize,maxMemSize);
}
public synchronized Cacheable getCloned(KeyValue key) {
	TimeWrapper timeWrapper = (TimeWrapper) hashTable.get(key);
	if (timeWrapper != null) {
		DbObjectWrapper objWrapper = (DbObjectWrapper) timeWrapper.getObject();
		Cacheable cacheable = objWrapper.getClonedCopy();
//lg.trace("DBCacheTable.get: "+cacheable.getClass().getName());
		return cacheable;
	} else {
//lg.trace("DBCacheTable.get: null");
		return null;
	}
}
public synchronized void ping() {
	boolean bRemovedAny = false;
	Enumeration e = hashTable.keys();
	while (e.hasMoreElements()) {
		Object key = e.nextElement();
		TimeWrapper tw = (TimeWrapper) hashTable.get(key);
		if ((tw != null) && (tw.getAge() > expireTimeMillisec)) {
			remove0(key);
			bRemovedAny = true;
//			lg.trace("Cachetable.ping(), expiring key="+key.toString());
		}
	}
	if (bRemovedAny){
		show();
	}
//	System.gc();
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

	return oldTimeWrapper;
}
public void putProtected(KeyValue key, Cacheable cacheable) throws CacheException {

	if(!((cacheable instanceof Cloneable) || (cacheable instanceof Immutable) ||(cacheable instanceof Serializable))){
		throw new CacheException("put:Object not Cloneable, Immutable or Serializable");
	}
	if(key ==null){
		throw new CacheException("put: key == null");
	}
	long dataSize = 1000;
	byte[] objData = null;
	try {
		objData = CompressionUtils.toSerialized(cacheable);
		//
		// dataSize is length*3 because three copies are stored in DbObjectWrapper (reference/bytes/working).
		//
		dataSize = objData.length * 3;
	}catch (IOException e){
		throw new CacheException(e.getMessage(), e);
	}
	TimeWrapper oldTimeWrapper = put(key, new TimeWrapper(new DbObjectWrapper(cacheable,objData),dataSize,key));

//
// checking to see if another object has the same key
//
if (oldTimeWrapper!=null){
lg.info("replacing object ALREADY IN DATABASE_CACHE "+oldTimeWrapper.getObject()+" at key "+key);
}
//
// checking to see if same object (using compareEqual) already in hash
//
//if (cacheable instanceof cbit.vcell.model.Species){
//Enumeration enum1 = hashTable.elements();
//while (enum1.hasMoreElements()){
//TimeWrapper timeWrapper = (TimeWrapper) enum1.nextElement();
//DbObjectWrapper objWrapper = (DbObjectWrapper) timeWrapper.getObject();
//Cacheable cacheObj = objWrapper.getWorkingCopy();
//if (cacheable != cacheObj && cacheable.compareEqual(cacheObj)){
////	throw new RuntimeException("DBCacheTable.put("+cacheable+"), already in cache as ("+cacheObj+")");
//lg.trace("DBCacheTable.put("+cacheable+"), already in cache as ("+cacheObj+")");
//}
//}
//}
	
//	lg.trace("put(cacheable="+cacheable+") ");
	show();	
}
public void putUnprotected(KeyValue key, Cacheable cacheable) throws CacheException {

	if(!((cacheable instanceof Cloneable) || (cacheable instanceof Immutable) ||(cacheable instanceof Serializable))){
		throw new CacheException("put:Object not Cloneable, Immutable or Serializable");
	}
	if(key ==null){
		throw new CacheException("put: key == null");
	}
	long dataSize = 1000;
	try {
		byte[] objData = CompressionUtils.toSerialized(cacheable);
		dataSize = objData.length;
	}catch (IOException e){
		throw new CacheException(e.getMessage(), e);
	}
	TimeWrapper oldTimeWrapper = put(key, new TimeWrapper(new DbObjectWrapper(cacheable),dataSize,key));

//
// checking to see if another object has the same key
//
if (oldTimeWrapper!=null){
lg.info("replacing object ALREADY IN DATABASE_CACHE "+oldTimeWrapper.getObject()+" at key "+key);
}
//
// checking to see if same object (using compareEqual) already in hash
//
//if (cacheable instanceof cbit.vcell.model.Species){
//Enumeration enum1 = hashTable.elements();
//while (enum1.hasMoreElements()){
//TimeWrapper timeWrapper = (TimeWrapper) enum1.nextElement();
//DbObjectWrapper objWrapper = (DbObjectWrapper) timeWrapper.getObject();
//Cacheable cacheObj = objWrapper.getWorkingCopy();
//if (cacheable != cacheObj && cacheable.compareEqual(cacheObj)){
////	throw new RuntimeException("DBCacheTable.put("+cacheable+"), already in cache as ("+cacheObj+")");
//lg.trace("DBCacheTable.put("+cacheable+"), already in cache as ("+cacheObj+")");
//}
//}
//}
	
//	lg.trace("put(cacheable="+cacheable+") ");
	show();	
}
/**
 * This method was created in VisualAge.
 * @param key cbit.sql.KeyValue
 */
public void remove(KeyValue key) {
	remove0(key);
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
		lg.info("resize(): desiredMemSize="+desiredMemSize+"  cost min="+minCost+", max="+maxCost+", avg="+avgCost+" thresholdCost="+thresholdCost);
		show();
	}
}
/**
 * This method was created in VisualAge.
 */
public void show() {
	lg.trace("DBCacheTable ("+hashTable.size()+" entries): currMemSize="+currMemSize+" maxMemSize="+maxMemSize);
}
}
