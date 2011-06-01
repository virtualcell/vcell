/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.ui;

/*   UIGraphSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2010
 *   Any SybilAction targetting a GraphPane
 */

import org.vcell.sybil.models.graph.GraphListener;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.util.event.Bounded;
import org.vcell.sybil.util.graphlayout.LayoutType;


public interface UIModelGraphSpace<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends UIGraphSpace {

	public G graph();
	public void layoutGraph(LayoutType newLayout);
	public void setZoomPercent(int newZoomPercent);
	public int zoomPercent();
	public Bounded<GraphListener<S, G>> graphViewer();
	
}
