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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class DataAccessException extends Exception {
/**
 * DataAccessException constructor comment.
 */
public DataAccessException() {
	super();
	createNotify( );
}
/**
 * DataAccessException constructor comment.
 * @param s java.lang.String
 */
public DataAccessException(String s) {
	super(s);
	createNotify( );
}

public DataAccessException(String message,Throwable cause) {
	super(message,cause);
	createNotify( );
}

public DataAccessException(Throwable cause) {
	super(cause);
	createNotify( );
}

public interface Listener {
	/**
	 * {@link DataAccessException} created
	 */
	public void created(DataAccessException dae);
}

/**
 * listeners
 */
private static Set<Listener>  listeners;

/**
 * add listener
 * @param lstnr
 */
public static void addListener(Listener lstnr) {
	if (listeners == null) {
		listeners = Collections.newSetFromMap(new IdentityHashMap<Listener,Boolean>());
	}
	listeners.add(lstnr);
}

public static void removeListener(Listener lstnr) {
	if (listeners != null) {
		listeners.remove(lstnr);
	}
}

private void createNotify( )  {
	if (listeners != null) {
		listeners.stream().forEach((lstnr) -> lstnr.created(this));
	}
}

}
