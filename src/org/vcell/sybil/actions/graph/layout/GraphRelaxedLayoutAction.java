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

/*   GraphRelaxedLayoutAction  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Perform relaxed layout
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.util.graphlayout.LayoutType;

public class GraphRelaxedLayoutAction extends BaseAction {

	private static final long serialVersionUID = 3185114990608665398L;

	public GraphRelaxedLayoutAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}
	public void actionPerformed(ActionEvent event) {
		coreManager().graphSpace().layoutGraph(LayoutType.Relaxer);
	}


}
