/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions.graph.layout;

/*  GraphZoomActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to January 2009
 *  Actions to change the zoom level of the graph
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;

public class GraphZoomActions extends ActionTree {
	private static final long serialVersionUID = -391504335023090244L;
	public GraphZoomActions(CoreManager coreManager) {
		add(new GraphZoomInAction(new ActionSpecs
				("Zoom In", "Zoom In", "Make graph look bigger", "layout/zoomin.gif"), 
				coreManager));
		add(new GraphZoomOutAction(new ActionSpecs
				("Zoom Out", "Zoom Out", "Make graph look smaller", "layout/zoomout.gif"), 
				coreManager));
	}
}
