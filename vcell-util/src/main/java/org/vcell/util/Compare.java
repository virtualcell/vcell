/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * This type was created in VisualAge.
 */
public class Compare {

	public interface CompareLogger {
		public void compareFailed();
	}
	public static class CompareLoggerException extends Exception {
	}
	public static CompareLogger logger = null;//User sets logger implementation
	public static boolean loggingEnabled = false;//User sets logging on/off
	private static Object logInternalDisable = null;//logging off for intermediate comparisons
	
	private static Object getLogInternalDisableLock(){
		return (loggingEnabled?new Object():null);
	}
	private static void logInternalDisable(Object lock){
		//Turns off logging internally.  Needed when unordered lists are being compared so
		//inappropriate 'false' logging doesn't occur.
		//'lock' ensures that nested Compare.xxx comparisons can't re-enable logging before finishing
		//a temporarily logging-disabled compare operation
		if(logInternalDisable == null){
			logInternalDisable = lock;
		}
	}
	private static void logInternalEnable(Object lock){
		//Only the lock owner can turn logging back on after
		//being internally disabled
		if(lock != null && lock.equals(logInternalDisable)){
			logInternalDisable = null;
		}
	}
	public static boolean logFailure(){
		return Compare.logFailure(null);
	}
	public static boolean logFailure(Object internalDisableLock){
		if(internalDisableLock != null){
			//try our lock to re-enable logging
			logInternalEnable(internalDisableLock);
		}
		if ((logInternalDisable == null) && Compare.logger != null && Compare.loggingEnabled){
			logger.compareFailed();
		}
		return false;
	}
	public static Compare.CompareLogger DEFAULT_COMPARE_LOGGER =
		new Compare.CompareLogger() {
			public void compareFailed() {
				Thread.dumpStack();
//				try{
//					Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
//					StackTraceElement[] thisThreadStackTraceElements = allStackTraces.get(Thread.currentThread());
//					for (int i = 0; i < thisThreadStackTraceElements.length; i++) {
//						System.out.println(VCMLComparator.class.getName()+" --- "+thisThreadStackTraceElements[i].getClassName());
//						if(thisThreadStackTraceElements[i].getClassName().equals(MathVerifier.class.getName())){
//							throw new Compare.CompareLoggerException();
//						}
//					}
//				}catch(CompareLoggerException e){
//					e.printStackTrace();
//				}
			}
		};



/**
 * compatibility method for enums
 *  calls {@link Enum#equals(Object)}
 * @param e1 one enum (not null)
 * @param e2 another enum (not null)
 * @return true if they're the same
 * @throws IllegalArgumentException if either null
 */
public static <T extends Enum<?> >boolean isEqual(T e1, T e2) {
	if (e1==null || e2==null){
		throw new IllegalArgumentException("null argument " + e1 + " and/or " + e2) ;
	}
	return e1.equals(e2);
}
public static <T extends Enum<?> >boolean isEqualOrNullStrict(T[] e1, T[] e2) {
	if (e1==null && e2==null){
		return true;
	}
	if(e1 != null && e2 != null) {
		if(e1.length == e2.length) {
			if(e1.length == 0 && e2.length == 0) {
				return true;
			}
			String[] e1Str = new String[e1.length];
			String[] e2Str = new String[e2.length];
			for (int i = 0; i < e1.length; i++) {
				e1Str[i] = e1[i].name();
				e2Str[i] = e2[i].name();
			}
			return isEqual(e1Str, e2Str);
		}
	}
	if((e1==null && e2!=null && e2.length==0) || (e2==null && e1 !=null && e1.length==0)) {
		return true;
	}
	return false;
}
/**
 * @return boolean
 * @param v1 double[]
 * @param v2 double[]
 */
public static boolean isEqual(double v1[], double v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(double[],double[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return logFailure();
	}

	//
	// check that every int is the same in v1[] and v2[]
	//
	for (int i=0;i<v1.length;i++){
		if (v1[i] != v2[i]){
			return logFailure();
		}
	}
	return true;
}

public static boolean isEqual(int v1[], int v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(int[],int[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return logFailure();
	}

	//
	// check that every int is the same in v1[] and v2[]
	//
	for (int i=0;i<v1.length;i++){
		if (v1[i] != v2[i]){
			return logFailure();
		}
	}
	return true;
}

public static boolean isEqual(short v1[], short v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(short[],short[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return logFailure();
	}

	//
	// check that every int is the same in v1[] and v2[]
	//
	for (int i=0;i<v1.length;i++){
		if (v1[i] != v2[i]){
			return logFailure();
		}
	}
	return true;
}

public static boolean isEqual(float v1[], float v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(float[],float[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return logFailure();
	}

	//
	// check that every int is the same in v1[] and v2[]
	//
	for (int i=0;i<v1.length;i++){
		if (v1[i] != v2[i]){
			return logFailure();
		}
	}
	return true;
}

	public static boolean isEqual(Matchable[] v1, Matchable[] v2, BiPredicate<Matchable,Matchable> predicate) {
		if (v1==null || v2==null){
			throw new RuntimeException("Compare.isEqual(Matchable[],Matchable[]) received null argument(s)");
		}
		if (v1.length != v2.length){
			return logFailure();
		}

		int arrayLen = v1.length;
		//
		// check that every element v1[i] == v2[i]
		//
		Object internalDisableLock = getLogInternalDisableLock();Compare.logInternalDisable(internalDisableLock);
		boolean bSame = true;
		int ii = 0;
		for (ii=0;ii<arrayLen;ii++){
			if (!predicate.test(v1[ii],v2[ii])){
				bSame = false;
				break;
			}
		}
		if (bSame) {
			Compare.logInternalEnable(internalDisableLock);
			return true;
		}

		//
		// check that every element in v1 is in v2
		//
		for (int i=ii;i<arrayLen;i++){
			Matchable c1 = (Matchable)v1[i];
			boolean bFound = false;
			for (int j=ii;j<arrayLen;j++){
				Matchable c2 = (Matchable)v2[j];
				if (predicate.test(c2,c1)){
					bFound = true;
					break;
				}
			}
			if (!bFound){
				return logFailure(internalDisableLock);
			}
		}
		//
		// check that every element in v2 is in v1
		//
		for (int i=ii;i<arrayLen;i++){
			Matchable c2 = (Matchable)v2[i];
			boolean bFound = false;
			for (int j=ii;j<arrayLen;j++){
				Matchable c1 = (Matchable)v1[j];
				if (predicate.test(c1,c2)){
					bFound = true;
					break;
				}
			}
			if (!bFound){
				return logFailure(internalDisableLock);
			}
		}
		Compare.logInternalEnable(internalDisableLock);
		return true;
	}



public static boolean isEqual(Matchable v1[], Matchable v2[]) {
	BiPredicate<Matchable,Matchable> predicate = (vv1, vv2) -> vv1.compareEqual(vv2);
	return isEqual(v1, v2, predicate);
}

public static boolean isEqual(String[] v1, String[] v2) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(String[],String[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return logFailure();
	}

	int arrayLen = v1.length;
	//
	// check that every element v1[i] == v2[i]
	//
	Object internalDisableLock = getLogInternalDisableLock();Compare.logInternalDisable(internalDisableLock);
	boolean bSame = true;
	int ii = 0;
	for (ii=0;ii<arrayLen;ii++){
		if (!v1[ii].equals(v2[ii])){
			bSame = false;
			break;
		}
	}
	if (bSame) {
		Compare.logInternalEnable(internalDisableLock);
		return true;
	}
	
	//
	// check that every element in v1 is in v2
	//
	for (int i=ii;i<arrayLen;i++){
		String c1 = v1[i];
		boolean bFound = false;
		for (int j=ii;j<arrayLen;j++){
			String c2 = v2[j];
			if (c2.equals(c1)){
				bFound = true;
				break;
			}
		}
		if (!bFound){
			return logFailure(internalDisableLock);
		}
	}
	//
	// check that every element in v2 is in v1
	//
	for (int i=ii;i<arrayLen;i++){
		String c2 = v2[i];
		boolean bFound = false;
		for (int j=ii;j<arrayLen;j++){
			String c1 = v1[j];
			if (c1.equals(c2)){
				bFound = true;
				break;
			}
		}
		if (!bFound){
			return logFailure(internalDisableLock);
		}
	}
	Compare.logInternalEnable(internalDisableLock);
	return true;
}

public static boolean isEqual(Matchable obj1, Matchable obj2) {
	if (obj1==null && obj2==null){
		return true;
	}
	if (obj1==null || obj2==null){
		return logFailure();
	}
	if (!obj1.compareEqual(obj2)){
		return logFailure();
	}
	return true;
}

public static boolean isEqual(java.lang.Number obj1, java.lang.Number obj2) {
	return isEqual0(obj1, obj2);
}

private static <T> boolean isEqual0(T obj1, T obj2) {
	if (obj1==null && obj2==null){
		return true;
	}
	if (obj1==null || obj2==null){
		return logFailure();
	}
	if (!obj1.equals(obj2)){
		return logFailure();
	}
	return true;
}

public static boolean isEqual(String obj1, String obj2) {	
	return isEqual0(obj1, obj2);
}

public static boolean isEqual(java.util.Date obj1, java.util.Date obj2) {
	return isEqual0(obj1, obj2);
}

public static boolean isEqualOrNull(List<? extends Matchable> v1, List<? extends Matchable> v2) {
	if (v1 == null && v2 == null){
		return true;
	}
	if (v1 == null || v2 == null){
		return logFailure();
	}
	return isEqual(v1,v2);
}

/**
 * @return boolean
 * @param v1 java.util.AbstractList
 * @param v2 java.util.AbstractList
 */
public static boolean isEqual(List<? extends Matchable> v1, List<? extends Matchable> v2) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(AbstractList,AbstractList) received null argument(s)");
	}
	if (v1.size() != v2.size()){
		return logFailure();
	}

	int arrayLen = v1.size();
	//
	// check that every element v1[i] == v2[i]
	//
	Object internalDisableLock = getLogInternalDisableLock();Compare.logInternalDisable(internalDisableLock);
	boolean bSame = true;
	int ii=0;
	for (ii=0;ii<arrayLen;ii++){
		Matchable c1 = v1.get(ii);
		Matchable c2 = v2.get(ii);
		if (!c2.compareEqual(c1)){
			bSame = false;
			break;
		}
	}
	if (bSame) {
		Compare.logInternalEnable(internalDisableLock);
		return true;
	}
	//
	// check that every element in v1 is in v2
	//
	for (int i=ii;i<arrayLen;i++){
		Matchable c1 = v1.get(i);
		boolean bFound = false;
		for (int j=ii;j<arrayLen;j++){
			Matchable c2 = v2.get(j);
			if (c2.compareEqual(c1)){
				bFound = true;
				break;
			}
		}
		if (!bFound){
			return logFailure(internalDisableLock);
		}
	}
	//
	// check that every element in v2 is in v1
	//
	for (int i=ii;i<arrayLen;i++){
		Matchable c2 = v2.get(i);
		boolean bFound = false;
		for (int j=ii;j<arrayLen;j++){
			Matchable c1 = v1.get(j);
			if (c1.compareEqual(c2)){
				bFound = true;
				break;
			}
		}
		if (!bFound){
			return logFailure(internalDisableLock);
		}
	}
	Compare.logInternalEnable(internalDisableLock);
	return true;
}

public static boolean isEqualOrNull(double v1[], double v2[]) {
	if (v1==null && v2==null){
		return true;
	}else{
		return isEqual(v1,v2);
	}
}

public static boolean isEqualOrNull(int v1[], int v2[]) {
	if (v1==null && v2==null){
		return true;
	}else{
		return isEqual(v1,v2);
	}
}

public static boolean isEqualOrNull(Matchable v1[], Matchable v2[]) {
	if (v1==null && v2==null){
		return true;
	}else if (v1==null || v2==null){
		return logFailure();
	}else{
		return isEqual(v1,v2);
	}
}

public static boolean isEqualOrNull(Matchable obj1, Matchable obj2) {
	if (obj1==null && obj2==null){
		return true;
	}
	if (obj1==null || obj2==null){
		return logFailure();
	}
	if (!obj1.compareEqual(obj2)){
		return logFailure();
	}
	return true;
}

public static boolean isEqualOrNull(java.lang.Number obj1, java.lang.Number obj2) {
	return isEqualOrNull0(obj1, obj2);
}

private static <T> boolean isEqualOrNull0(T obj1, T obj2) {
	if (obj1==null && obj2==null){
		return true;
	}
	if (obj1==null || obj2==null){
		return logFailure();
	}
	if (!obj1.equals(obj2)){
		return logFailure();
	}
	return true;
}

public static boolean isEqualOrNull(String obj1, String obj2) {
	return isEqualOrNull0(obj1, obj2);
}

public static boolean isEqualOrNullStrict(Matchable v1[], Matchable v2[]) {
	if (v1==null && v2==null){
		return true;
	}else{
		return isEqualStrict(v1,v2);
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqualStrict(Matchable v1[], Matchable v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqualStrict(Matchable[],Matchable[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return logFailure();
	}

	//
	// check that each array is the same (and elements are in the same order)
	//
	for (int i=0;i<v1.length;i++){
		if (!v1[i].compareEqual(v2[i])){
			return logFailure();
		}
	}

	return true;
}

/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqualStrict(List<? extends Matchable> v1, List<? extends Matchable> v2) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqualStrict(List<Matchable>,List<Matchable>) received null argument(s)");
	}
	if (v1.size() != v2.size()){
		return logFailure();
	}

	//
	// check that each array is the same (and elements are in the same order)
	//
	for (int i=0;i<v1.size();i++){
		if (!v1.get(i).compareEqual(v2.get(i))){
			return logFailure();
		}
	}

	return true;
}

public static <KeyType extends Matchable, ValueType extends Matchable> boolean isEqual(HashMap<KeyType, ValueType> map1, HashMap<KeyType, ValueType> map2) {
	//
	// compare entries from map1 to map2
	//
	for (KeyType key1 : map1.keySet()){
		boolean bFoundOtherKey = false;
		for (KeyType key2 : map2.keySet()){
			if (Compare.isEqual(key1, key2)){
				bFoundOtherKey = true;
				if (Compare.isEqual(map1.get(key1), map2.get(key2))){
					break;
				}else{
					// keys are the same but values are different.
					return false;
				}
			}
		}
		if (!bFoundOtherKey){
			// can't find other key
			return false;
		}
	}
	//
	// compare entries from map2 to map1
	//
	for (KeyType key2 : map2.keySet()){
		boolean bFoundOtherKey = false;
		for (KeyType key1 : map1.keySet()){
			if (Compare.isEqual(key1, key2)){
				bFoundOtherKey = true;
				if (Compare.isEqual(map1.get(key1), map2.get(key2))){
					break;
				}else{
					// keys are the same but values are different.
					return false;
				}
			}
		}
		if (!bFoundOtherKey){
			// can't find other key
			return false;
		}
	}
	return true;
}

/**
 * compares list for same numbers of items and elements that {@link Matchable#compareEqual(Matchable)}, in any order 
 * @param lhs
 * @param rhs
 * @return true if criteria met
 */
public static <T extends Matchable> boolean areEquivalent(Collection<T> lhs, Collection<T> rhs ) {
	if (lhs.size() == rhs.size( )) {
		List<Matchable> punchList = new LinkedList<Matchable>(rhs);
		for (Matchable m : lhs) {
			boolean found = false;
			Iterator<T> iter = rhs.iterator();
			while (iter.hasNext() && !found) {
				if (m.compareEqual(iter.next( ))) {
					iter.remove();
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		VCAssert.assertTrue(punchList.isEmpty(), "logic error");
	}
	
	return false;	
}

/**
 * see if all fields which implement {@link Matchable} are equal (both null or {@link Matchable#compareEqual(Matchable)} is true
 * @param lhs null is okay
 * @param rhs null is okay
 * @return true if both null or all fields equal 
 */
public static boolean isEqualFields(Matchable lhs, Matchable rhs) {
	try {
		if ((lhs == null) != (rhs == null)) {
			return false;
		}
		Class<? extends Matchable> lClass = lhs.getClass();
		Class<? extends Matchable> rClass = lhs.getClass();
		if (lClass != rClass) {
			return false;
		}
		for ( Field f : lClass.getDeclaredFields()) {
			f.setAccessible(true);
			Matchable lMatch = BeanUtils.downcast(Matchable.class, f.get(lhs)  );
			if (lMatch != null) {
				Matchable rMatch = BeanUtils.downcast(Matchable.class, f.get(rhs) );
				if (!isEqual(lMatch, rMatch)) {
					return false;
				}
			}
		}
		return true;
	}
	catch (Exception e) {
		e.printStackTrace();
	}
	return false;

}

/**
 * whether two objects are null or not
 */
public static enum Nullness {
	BOTH_NULL,
	NEITHER_NULL,
	FIRST_NULL,
	SECOND_NULL,
}

/**
 * find what's null 
 * @param first
 * @param second
 * @return non-null enum
 */
public static Nullness nullState(Object first, Object second) {
	if (first == null) {
		return second == null ? Nullness.BOTH_NULL : Nullness.FIRST_NULL;
	}
	return second == null ? Nullness.SECOND_NULL : Nullness.NEITHER_NULL;
}

}