/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graph;

/*   GraphListener  --- by Oliver Ruebenacker, UCHC --- October 2007 to March 2010
 *   Interface for classes displaying a graph, such as GraphPane
 */

import org.vcell.sybil.util.graphlayout.LayoutType;

public interface GraphListener<S extends UIShape<S>, G extends UIGraph<S, G>> {

	public void setGraph(G graph);
	public G graph();
	public void layoutGraph(LayoutType newLayout);
	public void updateView();

}
