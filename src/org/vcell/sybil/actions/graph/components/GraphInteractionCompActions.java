package org.vcell.sybil.actions.graph.components;

/*  GraphInteractionCompActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to January 2009
 *  Actions related to graph interaction components
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public class GraphInteractionCompActions<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends ActionTree {
	private static final long serialVersionUID = -9206101559336496213L;
	public GraphInteractionCompActions(CoreManager coreManager, ModelGraphManager<S, G> graphManager) {
		add(new GraphReactionAction<S, G>(new ActionSpecs
				("Reaction", "Biochemical Reaction", "Biochemical Reaction", 
						"biopax/biochemicalReaction.gif"), 
				coreManager, graphManager));
		add(new GraphTransportAction<S, G>(new ActionSpecs
				("Transport", "Transport", "Transport", "biopax/transport.gif"), 
				coreManager, graphManager));
		add(new GraphReactionWithTransportAction<S, G>(new ActionSpecs
				("Reaction WT", "Biochemical Reaction with Transport", 
						"Biochemical Reaction with Transport", 
				"biopax/transportWithBiochemicalReaction.gif"), 
				coreManager, graphManager));
	}
}