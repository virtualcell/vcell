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

/*   ContainerShape  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2009
 *   Shape of container in Sybil
 */

import java.awt.Dimension;

import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;


public abstract class ContainerShape extends RectangleShape {
	
	public ContainerShape(Graph graphNew, RDFGraphComponent sybCompNew) {
		super(graphNew, sybCompNew);
		size = new Dimension(700, 700);
		location.setP(size.width/2, size.height/2);
		preferedSize = new Dimension(700, 700);
		updateScreenSize = false;
		updatePreferedSize = false;
	}
	
	public PaintLevel paintLevel() { return PaintLevel.Container; }

}
