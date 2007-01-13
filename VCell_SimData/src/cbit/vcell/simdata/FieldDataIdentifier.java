package cbit.vcell.simdata;
import cbit.util.ISize;
import cbit.util.Extent;
import cbit.util.Origin;
import cbit.util.document.FieldDataIdentifierSpec;
import cbit.util.document.KeyValue;
/**
 * Insert the type's description here.
 * Creation date: (9/18/2006 12:55:46 PM)
 * @author: Jim Schaff
 */
public class FieldDataIdentifier implements java.io.Serializable  {
	private KeyValue key = null;
	private java.lang.String fieldName = null;
	private java.lang.String variableName = null;
	private java.lang.String dataFilePath = null;
	private cbit.util.Origin origin;
	private cbit.util.Extent extent;
	private cbit.util.ISize size;

/**
 * FieldDataIdentifier constructor comment.
 */
public FieldDataIdentifier(KeyValue arg_key, String arg_fieldName, String arg_variableName, String arg_dataFilePath, Origin arg_origin, Extent arg_extent, ISize arg_size) {
	super();
	key = arg_key;
	fieldName = arg_fieldName;
	variableName = arg_variableName;
	dataFilePath = arg_dataFilePath;
	origin = arg_origin;
	extent = arg_extent;
	size = arg_size;
}


/**
 * Insert the method's description here.
 * Creation date: (9/20/2006 8:51:30 AM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (!(obj instanceof FieldDataIdentifier)) {
		return false;
	}
	
	FieldDataIdentifier fdi = (FieldDataIdentifier)obj;
	if (!fieldName.equals(fdi.fieldName)) {
		return false;
	}
	
	if (!variableName.equals(fdi.variableName)) {
		return false;
	}
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 11:18:33 AM)
 * @return java.lang.String
 */
public static FieldDataIdentifier fromCSVString(String inputString) {
	java.util.StringTokenizer st = new java.util.StringTokenizer(inputString, ",");
	if (st.countTokens() != 4 + 3 * 3) {
		throw new RuntimeException("FieldDataIdentifer::fromString(), Missing arguments for FieldDataIdentifier");
	}
	return new FieldDataIdentifier(new KeyValue(st.nextToken()), st.nextToken(), st.nextToken(), st.nextToken(), 
		new Origin(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())),
		new Extent(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())),
		new ISize(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())));
}


/**
 * Insert the method's description here.
 * Creation date: (9/20/2006 9:53:51 AM)
 * @return java.lang.String
 */
public java.lang.String getDataFilePath() {
	return dataFilePath;
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public String getDefaultFieldDataFileNameForSimulation() {
	return getFieldName() + "_" + getVariableName() + ".fdat";
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 12:33:44 PM)
 * @return cbit.util.Extent
 */
public cbit.util.Extent getExtent() {
	return extent;
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
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public String getGlobalVariableName_C() {
	return getGlobalVariableName_C(fieldName, variableName);
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public static String getGlobalVariableName_C(FieldDataIdentifierSpec fdis) {
	return getGlobalVariableName_C(fdis.getFieldName(), fdis.getVariableName());
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
private static String getGlobalVariableName_C(String fieldname, String varname) {
	return "field_" + fieldname + "_" + varname;
}


/**
 * Insert the method's description here.
 * Creation date: (9/18/2006 12:56:35 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getKey() {
	return key;
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public static String getLocalVariableName_C(FieldDataIdentifierSpec fdis) {
	return fdis.getFieldName() + "_" + fdis.getVariableName();
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 12:33:29 PM)
 * @return cbit.util.Origin
 */
public cbit.util.Origin getOrigin() {
	return origin;
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 12:33:59 PM)
 * @return cbit.util.ISize
 */
public cbit.util.ISize getSize() {
	return size;
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
 * Creation date: (9/21/2006 11:18:33 AM)
 * @return java.lang.String
 */
public String toCSVString() {
	return key + "," + fieldName + "," + variableName + "," + dataFilePath + "," + origin.getX() + "," + origin.getY() + "," + origin.getZ() + ","
		+ extent.getX() + "," + extent.getY() + "," + extent.getZ() + "," + size.getX() + "," + size.getY() + "," + size.getZ();
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 11:18:33 AM)
 * @return java.lang.String
 */
public String toString() {
	return "FieldDataIdentifer[" + key + "," + fieldName + "," + variableName + "," + dataFilePath + "," + origin.getX() + "," + origin.getY() + "," + origin.getZ() + ","
		+ extent.getX() + "," + extent.getY() + "," + extent.getZ() + "," + size.getX() + "," + size.getY() + "," + size.getZ() + "]";
}
}