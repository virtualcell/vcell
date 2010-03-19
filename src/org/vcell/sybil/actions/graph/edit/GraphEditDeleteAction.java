package org.vcell.sybil.actions.graph.edit;

/*  GraphEditDeleteAction  ---  Oliver Ruebenacker, UCHC  ---  August 2007 to March 2010
 *  Deletes selected shapes
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
import org.vcell.sybil.models.graphcomponents.RDFGraphComponent;
import org.vcell.sybil.util.iterators.BufferedIterator;

public class GraphEditDeleteAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = -7879075958296944998L;

	public GraphEditDeleteAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public void actionPerformed(ActionEvent event) {
		if(graph() != null) {
			if(! (graph().model().selectedComps().isEmpty())) {
				Iterator<RDFGraphComponent> compIter = 
					new BufferedIterator<RDFGraphComponent>(graph().model().selectedComps().iterator());
				while(compIter.hasNext()) {
					graph().removeComp(compIter.next());
				}
				updateUI();
			} else {
				S shape = graph().chosenShape();
				if(shape instanceof GraphShape) {
					graph().removeShape(shape);
					updateUI();					
				}
			}
		} 
	}

}
