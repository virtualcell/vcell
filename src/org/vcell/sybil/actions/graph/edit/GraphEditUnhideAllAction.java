package org.vcell.sybil.actions.graph.edit;

/*   GraphEditUnhideAction  --- by Oliver Ruebenacker, UCHC --- July 2007 to March 2010
 *   Create a node for a complex 
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.GraphAction;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.Visibility;

public class GraphEditUnhideAllAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = 546611420445311234L;

	public GraphEditUnhideAllAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public void actionPerformed(ActionEvent event) {
		graph().unhideAllComps(Visibility.hiderSelection);
		updateUI();					
	}

}
