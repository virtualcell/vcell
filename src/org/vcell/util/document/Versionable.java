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

import org.vcell.util.Cacheable;
import org.vcell.util.Matchable;
/**
 * This type was created in VisualAge.
 */
public interface Versionable extends Cacheable,Matchable,Cloneable{
/**
 * Insert the method's description here.
 * Creation date: (4/24/2003 3:27:46 PM)
 */
void clearVersion();
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 10:39:30 AM)
 * @return java.lang.String
 */
String getDescription();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getName();
/**
 * This method was created in VisualAge.
 * @return Version
 */
Version getVersion();
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 11:02:05 AM)
 * @param description java.lang.String
 */
void setDescription(String description) throws java.beans.PropertyVetoException;
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 11:03:59 AM)
 * @param name java.lang.String
 */
void setName(String name) throws java.beans.PropertyVetoException;
}
