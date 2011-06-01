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

/*   ReactionTransportNodeShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shape for nodes representing a reaction with a transport
 */

import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public class NodeShapeReactionTransport extends NodeShape {
	
	int radius = 12;

	public NodeShapeReactionTransport(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
		setColorFGSelected(java.awt.Color.black);
		setColorBG(java.awt.Color.pink);
	}

}
