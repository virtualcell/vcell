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
 * This type was created in VisualAge.
 */
public class ObjectNotFoundException extends DataAccessException {
/**
 * ObjectNotFoundException constructor comment.
 * @param s java.lang.String
 */
public ObjectNotFoundException(String s) {
	super(s);
}

public ObjectNotFoundException(String message,Throwable cause) {
	super(message,cause);
}

}
