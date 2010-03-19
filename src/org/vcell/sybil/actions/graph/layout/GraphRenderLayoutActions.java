package org.vcell.sybil.actions.graph.layout;

/*  GraphRenderLayoutActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to February 2009
 *  Actions to change the global layout of the graph (location pattern of graph elements)
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;

public class GraphRenderLayoutActions extends ActionTree {
	private static final long serialVersionUID = -4874908976203139334L;
	public GraphRenderLayoutActions(CoreManager coreManager) {
		add(new GraphRandomLayoutAction(new ActionSpecs
				("Random", "Random Layout", "Reconfigure graph randomly", "layout/random.gif"), 
				coreManager)); 
		add(new GraphCircularLayoutAction(new ActionSpecs
				("Circular", "Circular Layout", "Reconfigure graph circular", 
						"layout/circular.gif"), coreManager)); 
		add(new GraphAnnealedLayoutAction(new ActionSpecs
				("Annealed", "Annealed Layout", "Reconfigure graph by annealing", 
						"layout/annealed.gif"), 
				coreManager));
		add(new GraphLevelledLayoutAction(new ActionSpecs
				("Levelled", "Levelled Layout", "Reconfigure graph in levels", 
						"layout/levelled.gif"), 
				coreManager));
		add(new GraphRelaxedLayoutAction(new ActionSpecs
				("Relaxed", "Relaxed Layout", "Reconfigure graph by relaxing", 
						"layout/relaxed.gif"), coreManager));
	}
}