package cbit.vcell.modeldb;
/**
 * Insert the type's description here.
 * Creation date: (11/6/2005 9:37:17 AM)
 * @author: Frank Morgan
 */
public class ReferenceQuerySpec implements java.io.Serializable{

	private cbit.sql.VersionableType versionableType;
	private cbit.sql.KeyValue keyValue;

/**
 * ReferenceQuesrySpec constructor comment.
 */
public ReferenceQuerySpec(cbit.sql.VersionableType vType,cbit.sql.KeyValue key) {
	
	versionableType = vType;
	keyValue = key;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:31:20 AM)
 * @return cbit.sql.KeyValue
 */
public cbit.sql.KeyValue getKeyValue() {
	return keyValue;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:31:20 AM)
 * @return cbit.sql.VersionableType
 */
public cbit.sql.VersionableType getVersionableType() {
	return versionableType;
}
}