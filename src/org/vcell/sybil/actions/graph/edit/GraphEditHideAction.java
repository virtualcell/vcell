package org.vcell.sybil.actions.graph.edit;

/*   GraphEditHideAction  --- by Oliver Ruebenacker, UCHC --- July 2007 to March 2010
 *   Create a node for a complex 
 */

import java.awt.event.ActionEvent;
import java.util.Iterator;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.GraphAction;
import org.vcell.sybil.gui.graph.GraphShape;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.Visibility;
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.util.iterators.BufferedIterator;

public class GraphEditHideAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = 6196135297016895140L;

	public GraphEditHideAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public void actionPerformed(ActionEvent event) {
		if(! (graph().model().selectedComps().isEmpty())) {
			Iterator<RDFGraphComponent> compIter = 
				new BufferedIterator<RDFGraphComponent>(graph().model().selectedComps().iterator());
			while(compIter.hasNext()) {
				graph().hideComp(compIter.next(), Visibility.hiderSelection);
			}
			updateUI();
		} else {
			S shape = graph().chosenShape();
			if(shape instanceof GraphShape) {
				graph().hideComp(shape.graphComp(), Visibility.hiderSelection);
				updateUI();					
			}
		} 
	}

}
