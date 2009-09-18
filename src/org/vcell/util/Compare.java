package org.vcell.util;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.AbstractList;

/**
 * This type was created in VisualAge.
 */
public class Compare {

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
		return false;
	}

	//
	// check that every int is the same in v1[] and v2[]
	//
	for (int i=0;i<v1.length;i++){
		if (v1[i] != v2[i]){
			return false;
		}
	}
	return true;
}

public static boolean isEqual(int v1[], int v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(int[],int[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return false;
	}

	//
	// check that every int is the same in v1[] and v2[]
	//
	for (int i=0;i<v1.length;i++){
		if (v1[i] != v2[i]){
			return false;
		}
	}
	return true;
}

public static boolean isEqual(short v1[], short v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(short[],short[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return false;
	}

	//
	// check that every int is the same in v1[] and v2[]
	//
	for (int i=0;i<v1.length;i++){
		if (v1[i] != v2[i]){
			return false;
		}
	}
	return true;
}

public static boolean isEqual(float v1[], float v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(float[],float[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return false;
	}

	//
	// check that every int is the same in v1[] and v2[]
	//
	for (int i=0;i<v1.length;i++){
		if (v1[i] != v2[i]){
			return false;
		}
	}
	return true;
}

public static boolean isEqual(Matchable v1[], Matchable v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(Matchable[],Matchable[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return false;
	}
	
	int arrayLen = v1.length;
	//
	// check that every element v1[i] == v2[i]
	//
	boolean bSame = true;
	int ii = 0;
	for (ii=0;ii<arrayLen;ii++){
		if (!v1[ii].compareEqual(v2[ii])){
			bSame = false;
			break;
		}
	}

	if (bSame) {
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
			if (c2.compareEqual(c1)){
				bFound = true;
				break;
			}
		}
		if (!bFound){
			return false;
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
			if (c1.compareEqual(c2)){
				bFound = true;
				break;
			}
		}
		if (!bFound){
			return false;
		}
	}
	
	return true;
}

public static boolean isEqual(String[] v1, String[] v2) {
	if (v1.length != v2.length){
		return false;
	}

	int arrayLen = v1.length;
	//
	// check that every element v1[i] == v2[i]
	//
	boolean bSame = true;
	int ii = 0;
	for (ii=0;ii<arrayLen;ii++){
		if (!v1[ii].equals(v2[ii])){
			bSame = false;
			break;
		}
	}

	if (bSame) {
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
			return false;
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
			return false;
		}
	}
	
	return true;
}

public static boolean isEqual(Matchable obj1, Matchable obj2) {
	if (obj1==null || obj2==null){
		return false;
	}
	if (!obj1.compareEqual(obj2)){
		return false;
	}
	return true;
}

public static boolean isEqual(java.lang.Number obj1, java.lang.Number obj2) {
	return isEqual0(obj1, obj2);
}

private static <T> boolean isEqual0(T obj1, T obj2) {
	if (obj1==null || obj2==null){
		return false;
	}
	if (!obj1.equals(obj2)){
		return false;
	}
	return true;
}

public static boolean isEqual(String obj1, String obj2) {	
	return isEqual0(obj1, obj2);
}

public static boolean isEqual(java.util.Date obj1, java.util.Date obj2) {
	return isEqual0(obj1, obj2);
}

public static boolean isEqualOrNull(AbstractList<? extends Matchable> v1, AbstractList<? extends Matchable> v2) {
	if (v1 == null && v2 == null){
		return true;
	}
	if (v1 == null || v2 == null){
		return false;
	}
	return isEqual(v1,v2);
}

/**
 * @return boolean
 * @param v1 java.util.AbstractList
 * @param v2 java.util.AbstractList
 */
public static boolean isEqual(AbstractList<? extends Matchable> v1, AbstractList<? extends Matchable> v2) {
	if (v1.size() != v2.size()){
		return false;
	}

	int arrayLen = v1.size();
	//
	// check that every element v1[i] == v2[i]
	//
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
			return false;
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
			return false;
		}
	}
	
	return true;
}

public static boolean isEqualOrdered(Matchable v1[], Matchable v2[]) {
	if (v1==null || v2==null){
		throw new RuntimeException("Compare.isEqual(Matchable[],Matchable[]) received null argument(s)");
	}
	if (v1.length != v2.length){
		return false;
	}

	//
	// check that every element in v1 is in v2
	//
	for (int i=0;i<v1.length;i++){
		Matchable c1 = (Matchable)v1[i];
		Matchable c2 = (Matchable)v2[i];
		if (!c2.compareEqual(c1)){
			return false;
		}
	}
	
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
		return false;
	}else{
		return isEqual(v1,v2);
	}
}

public static boolean isEqualOrNull(Matchable obj1, Matchable obj2) {
	if (obj1==null && obj2==null){
		return true;
	}
	if (obj1==null || obj2==null){
		return false;
	}
	if (!obj1.compareEqual(obj2)){
		return false;
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
		return false;
	}
	if (!obj1.equals(obj2)){
		return false;
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
		return false;
	}

	//
	// check that each array is the same (and elements are in the same order)
	//
	for (int i=0;i<v1.length;i++){
		if (!v1[i].compareEqual(v2[i])){
			return false;
		}
	}

	return true;
}
}
