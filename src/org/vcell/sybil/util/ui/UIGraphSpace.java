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

import org.vcell.sybil.models.graph.GraphModel;
import org.vcell.sybil.util.graphlayout.LayoutType;

public interface UIGraphSpace extends UISpace {

	public GraphModel.Listener graph();
	public void layoutGraph(LayoutType newLayout);
	public void setZoomPercent(int newZoomPercent);
	public int zoomPercent();
	public void updateUI();
	
}
