/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.graph;

/*   GraphShape  --- by Oliver Ruebenacker, UCHC --- August 2007 to January 2009
 *   Shapes to build a graph, most notably with the ability to be invisible
 */

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public abstract class GraphShape extends Shape {

	public GraphShape(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
	}
	
}
