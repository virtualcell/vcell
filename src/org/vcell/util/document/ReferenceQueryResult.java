/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;


/**
 * Insert the type's description here.
 * Creation date: (11/6/2005 9:38:17 AM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
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
