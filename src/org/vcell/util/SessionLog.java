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
public interface SessionLog {
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 */
public void alert(String message);
/**
 * This method was created in VisualAge.
 * @param e java.lang.Throwable
 */
public void exception(Throwable e);
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 */
public void print(String message);
}
