package org.vcell.sybil.actions.graph.components;

/*  GraphOtherCompActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to January 2009
 *  Action related to other (non-interaction) graph componentss
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public class GraphOtherCompActions<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends ActionTree {
	private static final long serialVersionUID = -9206101559336496213L;
	public GraphOtherCompActions(CoreManager coreManager, ModelGraphManager<S, G> graphManager) {
		add(new GraphEntityAction<S, G>(new ActionSpecs
				("Entity", "Physical Entity", "Physical Entity", "biopax/entity.gif"), 
				coreManager, graphManager));
		add(new GraphSmallMoleculeAction<S, G>(new ActionSpecs
				("Small Molecule", "Small Molecule", "Small Molecule", "biopax/smallMolecule.gif"), 
						coreManager, graphManager));
		add(new GraphProteinAction<S, G>(new ActionSpecs
				("Protein", "Protein", "Protein", "biopax/protein.gif"), 
				coreManager, graphManager));
		add(new GraphComplexAction<S, G>(new ActionSpecs
				("Complex", "Complex", "Complex", "biopax/complex.gif"), 
				coreManager, graphManager));
		add(new GraphParticipantAction<S, G>(new ActionSpecs
				("Participant", "Participant", "Physical Entity Participant", "biopax/modification.gif"), 
						coreManager, graphManager));
	}
}