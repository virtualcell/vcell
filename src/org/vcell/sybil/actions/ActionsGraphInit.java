/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions;

/*   ActionsGraphInit  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2009
 *   Initializes the graph subsystem on the actions level
 */

import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.components.GraphCompActions;
import org.vcell.sybil.actions.graph.edit.GraphPopupActions;
import org.vcell.sybil.actions.graph.layout.GraphLayoutActions;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public abstract class ActionsGraphInit<S extends UIShape<S>, G extends UIGraph<S, G>> 
{

	public abstract ModelGraphManager<S, G> graphManager();
	
	public void addGraphActions(ActionMap actionMapNew, CoreManager coreManager) {
		actionMapNew.put(new GraphLayoutActions<S, G>(coreManager, graphManager()));
		actionMapNew.put(new GraphCompActions<S, G>(coreManager, graphManager()));
		actionMapNew.put(new GraphPopupActions<S, G>(coreManager, graphManager()));

	}

}
