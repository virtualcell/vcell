/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;
/**
 * Insert the type's description here.
 * Creation date: (6/1/2004 12:05:40 AM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class UtilCancelException extends Exception {
	public static final UtilCancelException CANCEL_GENERIC = new UtilCancelException("User canceled (Generic)");

/**
 * UserCancelException constructor comment.
 * @param s java.lang.String
 */
private UtilCancelException(String s) {
	super(s);
}
}
