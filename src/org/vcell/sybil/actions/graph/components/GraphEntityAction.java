package org.vcell.sybil.actions.graph.components;

/*   GraphEntityAction  --- by Oliver Ruebenacker, UCHC --- July 2007 to February 2009
 *   Create a node for an entity 
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.GraphAction;
import org.vcell.sybil.gui.graph.nodes.NodeShapeComplex;
import org.vcell.sybil.gui.graph.nodes.NodeShapeDefault;
import org.vcell.sybil.gui.graph.nodes.NodeShapeProtein;
import org.vcell.sybil.gui.graph.nodes.NodeShapeSmallMolecule;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

public class GraphEntityAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = -7879075958296944998L;

	public GraphEntityAction(ActionSpecs newSpecs, CoreManager modSysNew,
			ModelGraphManager<S, G> modSysModelGraphNew) {
		super(newSpecs, modSysNew, modSysModelGraphNew);
	}

	public void actionPerformed(ActionEvent event) {
		// TODO simplify
		GraphCompSelector.select(graph(), NodeShapeDefault.class);
		GraphCompSelector.select(graph(), NodeShapeComplex.class);
		GraphCompSelector.select(graph(), NodeShapeProtein.class);
		GraphCompSelector.select(graph(), NodeShapeSmallMolecule.class);
	}

}
