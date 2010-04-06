package org.vcell.sybil.models.updater;

/*   GraphModelUpdater  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2010
 *   Listens to events from an Evaluator and updates a graphModel if necessary
 */

import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.graphlayout.LayoutType;

import com.hp.hpl.jena.rdf.model.Model;
import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.SybilGraphFactory;
import org.vcell.sybil.models.graph.manipulator.GraphManipulationException;
import org.vcell.sybil.models.graph.manipulator.categorizer.GraphGrouper;
import org.vcell.sybil.models.graph.manipulator.categorizer.ReactionsManipulator;

public class GraphModelUpdater<S extends UIShape<S>, G extends UIGraph<S, G>> 
implements Evaluator.Event.Listener {

	protected G graph;

	public GraphModelUpdater(G graph) { this.graph = graph; }

	public void evaluatorEvent(Evaluator.Event event) {
		Evaluator evaluator = event.evaluator();
		if(evaluator != null) {
			SBWorkView view = evaluator.view();
			Model model = view.box().getData();
			if(model == null) { graph.startNewGraph(); }
			else { 
				try { 
					SybilGraphFactory.createGraph(graph, view); 
					GraphGrouper<S, G> grouper = new GraphGrouper<S, G>(evaluator);
					grouper.applyToGraph(graph);
					ReactionsManipulator<S, G> manip = new ReactionsManipulator<S, G>(evaluator);
					manip.setCollapseParticipants(true);
					manip.applyToGraph(graph);
					graph.layoutGraph(LayoutType.Randomizer);
					graph.layoutGraph(LayoutType.Annealer);
					graph.updateView();
				} 
				catch (InterruptedException e) { CatchUtil.handle(e); } 
				catch (GraphManipulationException e) { CatchUtil.handle(e); }
			}
		} else {
			graph.startNewGraph();
		}
	}

}
