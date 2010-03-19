package org.vcell.sybil.actions.graph.edit;

/*  GraphEditExplodeAction  ---  Oliver Ruebenacker, UCHC  ---  August 2007 to May 2010
 *  Explodes selected shapes
 */

import java.awt.event.ActionEvent;
import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.GraphAction;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.Visibility;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;

public class GraphEditExplodeAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = -3019578673653734856L;

	public GraphEditExplodeAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public void actionPerformed(ActionEvent event) {
		for(RDFGraphComponent comp: graph().model().selectedComps()) {
			graph().explode(comp, Visibility.hiderSelection);
		}
		updateUI();
	}

}
