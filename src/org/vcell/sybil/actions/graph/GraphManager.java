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
