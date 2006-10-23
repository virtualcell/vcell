package cbit.util;

import cbit.util.document.VersionableFamily;

/**
 * Insert the type's description here.
 * Creation date: (11/6/2005 9:38:17 AM)
 * @author: Frank Morgan
 */
public class ReferenceQueryResult implements java.io.Serializable{

	VersionableFamily versionableFamily;

/**
 * ReferenceQueryResult constructor comment.
 */
public ReferenceQueryResult(VersionableFamily vf) {
	
	versionableFamily = vf;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2005 10:41:51 AM)
 * @return cbit.vcell.modeldb.VersionableFamily
 */
public VersionableFamily getVersionableFamily() {
	return versionableFamily;
}
}