package org.vcell.sybil.actions.graph.components;

/*   GraphReactionAction  --- by Oliver Ruebenacker, UCHC --- July 2007 to mARCH 2010
 *   Create a node for a complex 
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.GraphAction;
import org.vcell.sybil.gui.graph.nodes.NodeShapeReaction;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public class GraphReactionAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = -7879075958296944998L;

	public GraphReactionAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public void actionPerformed(ActionEvent event) {
		GraphCompSelector.select(graph(), NodeShapeReaction.class);
	}

}
