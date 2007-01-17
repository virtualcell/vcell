package cbit.sql;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class ObjectReferenceWrapper {
	private Object internalObject = null;
/**
 * InsertObjectWrapper constructor comment.
 */
public ObjectReferenceWrapper(Object object) {
	if (object == null){
		throw new IllegalArgumentException("null object");
	}
	this.internalObject = object;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof ObjectReferenceWrapper){
		return (internalObject == ((ObjectReferenceWrapper)object).internalObject);
	}else{
		return false;
	}
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 */
public Object getObject() {
	return internalObject;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int hashCode() {
	//
	// equals() has been overridden to support reference equates
	// hashCode should also be overridden, but as long as it gives the
	// same value every time (immutability), it will work. 
	// because equals() will only match the same object.
	//
	return internalObject.hashCode();  // either uses Object.hashCode or based on value 
}
}
