/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

/**
 * Insert the type's description here.
 * Creation date: (10/11/00 11:38:25 AM)
 * @author: 
 */
public interface WorldCoordinateCalculator {
/**
 * Insert the method's description here.
 * Creation date: (10/11/00 11:39:09 AM)
 * @return cbit.vcell.geometry.Coordinate
 */
org.vcell.util.Coordinate getWorldCoordinateFromUnitized2D(double x,double y);
/**
 * Insert the method's description here.
 * Creation date: (7/12/2004 12:42:16 PM)
 */
org.vcell.util.Coordinate snapWorldCoordinate(org.vcell.util.Coordinate targetC);
/**
 * Insert the method's description here.
 * Creation date: (7/13/2004 4:26:44 PM)
 */
org.vcell.util.Coordinate snapWorldCoordinateFace(org.vcell.util.Coordinate targetC);
}
