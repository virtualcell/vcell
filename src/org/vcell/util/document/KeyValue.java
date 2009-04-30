package org.vcell.util.document;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.Matchable;
/**
 * This class is Immutable (must stay immutable).
 *
 * encapsuates primary keys in the database (oracle)
 */
public class KeyValue implements java.io.Serializable, Matchable {
	private java.math.BigDecimal value = null;
/**
 * KeyValue constructor comment.
 */
public KeyValue(String value) throws NumberFormatException {
	this.value = new java.math.BigDecimal(value);
}
/**
 * KeyValue constructor comment.
 */
public KeyValue(java.math.BigDecimal key) throws IllegalArgumentException {
	if (key==null){
		throw new IllegalArgumentException("key is null");
	}
	this.value = key;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj == this){
		return true;
	}
	
	if (obj != null && obj instanceof KeyValue){
		KeyValue kv = (KeyValue)obj;
		if (kv.value.equals(value)){
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof KeyValue){
		return compareEqual((Matchable)obj);
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param strValue java.lang.String
 */
public static KeyValue fromString(String strValue) throws NumberFormatException {
	return new KeyValue(strValue);
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int hashCode() {
	return value.hashCode();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return value.toString();
}
}
