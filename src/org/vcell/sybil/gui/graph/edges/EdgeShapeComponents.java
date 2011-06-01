/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.graph.edges;

/*   EdgeShapeComponents  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Shape for edge representing BioPAX Level 2 property COMPONENTS
 */

import java.awt.Graphics2D;
import java.awt.Stroke;
import org.vcell.sybil.gui.graph.Graph;
import org.vcell.sybil.models.graphcomponents.RDFGraphCompEdge;
import org.vcell.sybil.util.gui.ArrowPainter;


public class EdgeShapeComponents extends EdgeShape {

	public EdgeShapeComponents(Graph graphNew, RDFGraphCompEdge sybCompNew) { 
		super(graphNew, sybCompNew); 
	}

	public void paint(Graphics2D g2D) {
		if((!haveHooks) || start == null || end == null) { return; }
		Stroke oldStroke = g2D.getStroke();
		g2D.setStroke(DASHED_STROKE);
		super.paintEdge(g2D);
		g2D.setStroke(oldStroke);
		ArrowPainter.paintArrow(g2D, start, end, ARROW_LENGTH, ARROW_WIDTH);
	}
	
}
