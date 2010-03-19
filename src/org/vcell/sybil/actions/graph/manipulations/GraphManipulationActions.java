package org.vcell.sybil.actions.graph.manipulations;

/*  GraphManipActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to March 2010
 *  Actions related to graph manipulations (primarily filtering what is shown)
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.systemproperty.SystemPropertyDevelMode;

public class GraphManipulationActions <S extends UIShape<S>, G extends UIGraph<S, G>>
extends ActionTree {
	private static final long serialVersionUID = -4426413957172826579L;
	public GraphManipulationActions(CoreManager coreManager, ModelGraphManager<S, G> graphManager) {
		add(new ReactionsProcessesOnlyAction<S, G>(new ActionSpecs
				("Reactions Only", "Reactions Only", "Show only Reactions", 
						"layout/level1.gif"), coreManager, graphManager)); 
		add(new ReactionsEntitiesAction<S, G>(new ActionSpecs
				("Reaction Network", "Reaction Network", "Reaction Network", "layout/level2.gif"), 
				coreManager, graphManager)); 
		//add(new ReactionsEntitiesPartAction<S,G>(new ActionSpecs
		//		("Entities + Part.", "Entities and Participants", 
		//				"Reactions, entities and participants", "images/layout/level3.gif"), 
		//				modSysNew, modSysModelGraphNew)); 
		add(new ReactionsComponentsAction<S, G>(new ActionSpecs
				("Components", "Components", "Reactions, entities and components", 
						"layout/level3.gif"), 
						coreManager, graphManager)); 
		//add(new ReactionsComponentsPartAction<S, G>(new ActionSpecs
		//		("Comp. and Part.", "Components and Participants", "Components and participants", 
		//		"images/layout/level5.gif"), 
		//		modSysNew, modSysModelGraphNew)); 
		if(SystemPropertyDevelMode.develMode()) {
			add(new ShowAllAction<S, G>(new ActionSpecs
			("Show Raw OWL", "Show Raw OWL", "Show All Nodes", "layout/random.gif"), 
			coreManager, graphManager)); 				
		}
	}
}