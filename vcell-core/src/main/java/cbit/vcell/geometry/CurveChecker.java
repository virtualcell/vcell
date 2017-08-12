/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;

/**
 * Insert the type's description here.
 * Creation date: (10/16/00 4:20:37 PM)
 * @author: 
 */
public interface CurveChecker {
/**
 * Insert the method's description here.
 * Creation date: (10/16/00 4:21:09 PM)
 */
boolean curveSatisfyWorldConstraints(Curve curve);
}
