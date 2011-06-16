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

/*   GUIMain  --- by Oliver Ruebenacker, UCHC --- January 2009
 *   Provides the universe in which the graph-related GUI lives. To be instantiated exactly once.
 */

import org.vcell.sybil.actions.ActionsGraphInit;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.gui.GUIMainInit;
import org.vcell.sybil.util.ui.UserInterfaceGraph;

public class GUIGraphInit extends ActionsGraphInit<Shape, Graph> implements GUIMainInit.SubInitGraph {

	protected ModelGraphManager<Shape, Graph> graphManager;
	
	public GUIGraphInit() { 
		graphManager = new ModelGraphManager<Shape, Graph>();
	}
	
	@Override
	public ModelGraphManager<Shape, Graph> graphManager() {
		return graphManager;
	}

	public void setUI(UserInterfaceGraph<Shape, Graph> newUI) {
		graphManager.setUI(newUI);
	}
	
}
