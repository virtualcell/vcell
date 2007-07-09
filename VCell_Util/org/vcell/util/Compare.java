package org.vcell.util;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Vector;
/**
 * This type was created in VisualAge.
 */
public class Compare {
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqual(double v1[], double v2[]) {
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
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
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
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqual(Matchable v1[], Matchable v2[]) {
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
		boolean bFound = false;
		for (int j=0;j<v2.length;j++){
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
	for (int i=0;i<v2.length;i++){
		Matchable c2 = (Matchable)v2[i];
		boolean bFound = false;
		for (int j=0;j<v1.length;j++){
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
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqual(String[] v1, String[] v2) {
	if (v1.length != v2.length){
		return false;
	}

	//
	// check that every element in v1 is in v2
	//
	for (int i=0;i<v1.length;i++){
		String c1 = (String)v1[i];
		boolean bFound = false;
		for (int j=0;j<v2.length;j++){
			String c2 = (String)v2[j];
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
	for (int i=0;i<v2.length;i++){
		String c2 = (String)v2[i];
		boolean bFound = false;
		for (int j=0;j<v1.length;j++){
			String c1 = (String)v1[j];
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
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj1 java.lang.Object
 * @param obj2 java.lang.Object
 */
public static boolean isEqual(Matchable obj1, Matchable obj2) {
	if (obj1==null || obj2==null){
		return false;
	}
	if (!obj1.compareEqual(obj2)){
		return false;
	}
	return true;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj1 java.lang.Object
 * @param obj2 java.lang.Object
 */
public static boolean isEqual(java.lang.Number obj1, java.lang.Number obj2) {
	if (obj1==null || obj2==null){
		return false;
	}
	if (!obj1.equals(obj2)){
		return false;
	}
	return true;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj1 java.lang.Object
 * @param obj2 java.lang.Object
 */
public static boolean isEqual(String obj1, String obj2) {
	if (obj1==null || obj2==null){
		return false;
	}
	if (!obj1.equals(obj2)){
		return false;
	}
	return true;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj1 java.lang.Object
 * @param obj2 java.lang.Object
 */
public static boolean isEqual(java.util.Date obj1, java.util.Date obj2) {
	if (obj1==null || obj2==null){
		return false;
	}
	if (!obj1.equals(obj2)){
		return false;
	}
	return true;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqual(Vector v1, Vector v2) {
	if (v1.size() != v2.size()){
		return false;
	}

	//
	// check that every element in v1 is in v2
	//
	for (int i=0;i<v1.size();i++){
		Matchable c1 = (Matchable)v1.elementAt(i);
		boolean bFound = false;
		for (int j=0;j<v2.size();j++){
			Matchable c2 = (Matchable)v2.elementAt(j);
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
	for (int i=0;i<v2.size();i++){
		Matchable c2 = (Matchable)v2.elementAt(i);
		boolean bFound = false;
		for (int j=0;j<v1.size();j++){
			Matchable c1 = (Matchable)v1.elementAt(j);
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
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqualByReference(Vector v1, Vector v2) {
	if (v1 == null || v2 == null) {
		return false;
	}
	if (v1.size() != v2.size()) {
		return false;
	}
	for (int i = 0; i < v1.size(); i++) {
		if (!v2.contains(v1.elementAt(i))) {
			return false;
		}
	}
	for (int i = 0; i < v1.size(); i++) {
		if (!v1.contains(v2.elementAt(i))) {
			return false;
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
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqualOrNull(double v1[], double v2[]) {
	if (v1==null && v2==null){
		return true;
	}else{
		return isEqual(v1,v2);
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqualOrNull(int v1[], int v2[]) {
	if (v1==null && v2==null){
		return true;
	}else{
		return isEqual(v1,v2);
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
public static boolean isEqualOrNull(Matchable v1[], Matchable v2[]) {
	if (v1==null && v2==null){
		return true;
	}else if (v1==null || v2==null){
		return false;
	}else{
		return isEqual(v1,v2);
	}
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj1 java.lang.Object
 * @param obj2 java.lang.Object
 */
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
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj1 java.lang.Object
 * @param obj2 java.lang.Object
 */
public static boolean isEqualOrNull(java.lang.Number obj1, java.lang.Number obj2) {
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
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj1 java.lang.Object
 * @param obj2 java.lang.Object
 */
public static boolean isEqualOrNull(String obj1, String obj2) {
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
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param v1 java.util.Vector
 * @param v2 java.util.Vector
 */
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
