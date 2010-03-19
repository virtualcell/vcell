package org.vcell.sybil.actions.graph.manipulations;

/*   GraphManipProcessesOnlyAction  --- by Oliver Ruebenacker, UCHC --- January 2008 to March 2010
 *   A SybilGraphAction for showing only nodes containing a PaxNode.PROCESS
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.GraphAction;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.manipulator.GraphManipulationException;
import org.vcell.sybil.models.graph.manipulator.categorizer.ProcessesOnly;
import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.util.exception.CatchUtil;

public class ProcessesOnlyAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = 7949279607260156892L;

	public ProcessesOnlyAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public void actionPerformed(ActionEvent event) {
		if(graph() != null) {
			ProcessesOnly<S, G> manip = new ProcessesOnly<S, G>();
			Evaluator evaluator = coreManager().fileManager().evaluator();
			manip.setEvaluator(evaluator);
			try { manip.applyToGraph(graph()); } 
			catch (GraphManipulationException e) { CatchUtil.handle(e); }
			updateUI();
		} 
	}

}
