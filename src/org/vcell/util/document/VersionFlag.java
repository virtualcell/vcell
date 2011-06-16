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
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class VersionFlag implements java.io.Serializable, Matchable {
	
	private Integer versionFlag = null;

	//
	// don't change numerical values of below, these are used in the database
	//
	private static final int CURRENT = 0;
	private static final int ARCHIVED  = 1;
	private static final int UNREACHABLE  = 2;  // for backward compatability
	private static final int PUBLISHED = 3;
	
	private static final String FLAG_STRINGS[] = { "Current", "Archived", "UNREACHABLE","Publish" };

	public static final VersionFlag Current = new VersionFlag(CURRENT);
	public static final VersionFlag Archived = new VersionFlag(ARCHIVED);
	public static final VersionFlag Published = new VersionFlag(PUBLISHED);
	
/**
 * VersionFlag constructor comment.
 */
private VersionFlag(int flag) {
	super();
	if ((flag != CURRENT) && (flag != ARCHIVED) && (flag != PUBLISHED)) {
		throw new IllegalArgumentException("No Such Flag "+flag);
	}
	versionFlag = new Integer(flag);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param flag int
 */
public boolean compareEqual(Matchable obj) {
	if (obj == this){
		return true;
	}
	if (!(obj instanceof VersionFlag)){
		return false;
	}
	VersionFlag v = (VersionFlag)obj;
	if (Compare.isEqual(versionFlag,v.versionFlag)){
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/00 2:02:39 PM)
 * @return cbit.sql.VersionFlag
 * @param flag int
 */
public static VersionFlag fromInt(int flag) {
	switch (flag){
		case PUBLISHED:{
			return Published;
		}
		case ARCHIVED:{
			return Archived;
		}
		case CURRENT:{
			return Current;
		}
		case UNREACHABLE:{
			return Current;
		}
		default:{
			throw new IllegalArgumentException(
				"expecting '"+CURRENT+"' or '"+UNREACHABLE+"' for "+FLAG_STRINGS[CURRENT]+
				" or '"+ARCHIVED+"' for "+FLAG_STRINGS[ARCHIVED]+
				" or '"+PUBLISHED+"' for "+FLAG_STRINGS[PUBLISHED]);
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getIntValue() {
	return versionFlag.intValue();
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/00 1:58:03 PM)
 * @return boolean
 */
public boolean isArchived() {
	return (this.getIntValue() == ARCHIVED);
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/00 1:58:03 PM)
 * @return boolean
 */
public boolean isCurrent() {
	return (this.getIntValue() == CURRENT);
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/00 1:58:03 PM)
 * @return boolean
 */
public boolean isPublished() {
	return (this.getIntValue() == PUBLISHED);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return FLAG_STRINGS[versionFlag.intValue()];
}
}
