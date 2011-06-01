/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions.graph;

/*   GraphManager  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Provides an environment in which the models live
 */

import org.vcell.sybil.util.ui.UIGraphSpace;
import org.vcell.sybil.models.graph.GraphModel;
import org.vcell.sybil.models.ontology.Evaluator;

public interface GraphManager {

	public Evaluator.Event.Listener graphModelUpdater();
	
	public GraphModel.Listener graph();
	public UIGraphSpace graphSpace();

} // end class 
