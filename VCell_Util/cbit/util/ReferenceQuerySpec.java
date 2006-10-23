package cbit.util;
/**
 * Insert the type's description here.
 * Creation date: (11/6/2005 9:37:17 AM)
 * @author: Frank Morgan
 */
public class ReferenceQuerySpec implements java.io.Serializable{

	private cbit.util.document.VersionableType versionableType;
	private cbit.util.document.KeyValue keyValue;

/**
 * ReferenceQuesrySpec constructor comment.
 */
public ReferenceQuerySpec(cbit.util.document.VersionableType vType,cbit.util.document.KeyValue key) {
	
	versionableType = vType;
	keyValue = key;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:31:20 AM)
 * @return cbit.sql.KeyValue
 */
public cbit.util.document.KeyValue getKeyValue() {
	return keyValue;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:31:20 AM)
 * @return cbit.sql.VersionableType
 */
public cbit.util.document.VersionableType getVersionableType() {
	return versionableType;
}
}