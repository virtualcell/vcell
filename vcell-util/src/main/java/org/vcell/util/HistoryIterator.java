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
 * Creation date: (2/3/2001 8:51:30 PM)
 * @author: Ion Moraru
 */
public interface HistoryIterator extends java.util.Iterator {
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:57:10 PM)
 * @param newElement java.lang.Object
 */
void add(Object newElement);
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:58:55 PM)
 * @return java.lang.Object[]
 */
Object[] allEntries();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:57:57 PM)
 * @return java.lang.Object
 */
Object current();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:58:55 PM)
 * @return java.lang.Object[]
 */
Object[] fullList();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:53:43 PM)
 * @return boolean
 */
boolean hasPrevious();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:58:55 PM)
 * @return java.lang.Object[]
 */
Object[] nextList();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:53:18 PM)
 * @return java.lang.Object
 */
Object previous();
/**
 * Insert the method's description here.
 * Creation date: (2/3/2001 8:58:55 PM)
 * @return java.lang.Object[]
 */
Object[] previousList();
}
