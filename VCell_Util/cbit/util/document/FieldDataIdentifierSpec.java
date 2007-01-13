package cbit.util.document;
/**
 * Insert the type's description here.
 * Creation date: (9/18/2006 12:55:46 PM)
 * @author: Jim Schaff
 */
public class FieldDataIdentifierSpec implements java.io.Serializable  {
	private java.lang.String fieldName = null;
	private java.lang.String variableName = null;

/**
 * FieldDataIdentifier constructor comment.
 */
public FieldDataIdentifierSpec(String arg_fieldName, String arg_variableName) {
	super();
	fieldName = arg_fieldName;
	variableName = arg_variableName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/20/2006 8:51:30 AM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (!(obj instanceof FieldDataIdentifierSpec)) {
		return false;
	}
	
	FieldDataIdentifierSpec fdis = (FieldDataIdentifierSpec)obj;
	if (!fieldName.equals(fdis.fieldName)) {
		return false;
	}
	
	if (!variableName.equals(fdis.variableName)) {
		return false;
	}
	return true;
}

public int hashCode(){
	return fieldName.hashCode()+variableName.hashCode();
}

/**
 * Insert the method's description here.
 * Creation date: (9/18/2006 12:56:50 PM)
 * @return java.lang.String
 */
public java.lang.String getFieldName() {
	return fieldName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2006 3:15:24 PM)
 * @return java.lang.String
 */
public java.lang.String getVariableName() {
	return variableName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 8:39:22 AM)
 * @return java.lang.String
 */
public String toString() {
	return "[" + fieldName + "," + variableName + "]";
}
}