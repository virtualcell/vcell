/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modelopt;
/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:19:30 PM)
 * @author: Jim Schaff
 */
public class DataReference {
	private DataSource dataSource = null;
	private String identifier = null;

/**
 * DataReference constructor comment.
 */
public DataReference(DataSource argDataSource, String argIdentifier) {
	this.dataSource = argDataSource;
	this.identifier = argIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:22:16 PM)
 * @return java.lang.Object
 */
public DataSource getDataSource() {
	return dataSource;
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:22:16 PM)
 * @return java.lang.String
 */
public java.lang.String getIdentifier() {
	return identifier;
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:22:33 PM)
 * @return java.lang.String
 */
public String toString() {
	if (dataSource.getName() == null || dataSource.getName().length() == 0) {
		return identifier;
	}
	return dataSource.getName()+": "+identifier;
}
}
