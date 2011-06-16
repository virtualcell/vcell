/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.graph.nodes;

/*   ProteinNodeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Node shape for proteins
 */

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public class NodeShapeProtein extends NodeShape {
	
	int radius = 8;

	public NodeShapeProtein(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
		setColorFGSelected(java.awt.Color.black);
		setColorBG(java.awt.Color.orange);
	}

}
