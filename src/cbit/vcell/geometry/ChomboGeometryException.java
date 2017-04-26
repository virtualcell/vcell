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
 * {@link ChomboGeometryException} represents a problem when geometry is not compatible with EBChombo solver
 * it's not necessarily fatal. For example, the mesh size is not good, but good mesh sizes are available.
 *  
 * @author developer
 *
 */
public class ChomboGeometryException extends Exception {
/**
 * GeometryException constructor comment.
 */
public ChomboGeometryException() {
	super();
}
/**
 * GeometryException constructor comment.
 * @param s java.lang.String
 */
public ChomboGeometryException(String s) {
	super(s);
}
}
