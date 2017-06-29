/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;
import java.io.Serializable;

import org.vcell.util.Matchable;
/**
 * Insert the type's description here.
 * Creation date: (6/4/2004 3:34:42 PM)
 * @author: Ion Moraru
 */
public class ExportJobStatus implements Matchable, Serializable {
/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	return false;
}
}
