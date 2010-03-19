package org.vcell.sybil.actions.graph.manipulations;

/*   ShowAllAction  --- by Oliver Ruebenacker, UCHC --- January 2008 to March 2010
 *   A SybilGraphAction for showing all nodes
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.graph.ModelGraphManager;
import org.vcell.sybil.actions.graph.GraphAction;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;
import org.vcell.sybil.models.graph.manipulator.categorizer.MakeAllVisible;

public class ShowAllAction<S extends UIShape<S>, G extends UIGraph<S, G>> 
extends GraphAction<S, G> {

	private static final long serialVersionUID = 7949279607260156892L;

	public ShowAllAction(ActionSpecs newSpecs, CoreManager coreManager,
			ModelGraphManager<S, G> graphManager) {
		super(newSpecs, coreManager, graphManager);
	}

	public void actionPerformed(ActionEvent event) {
		if(graph() != null) {
			MakeAllVisible<S, G> manip = new MakeAllVisible<S, G>();
			manip.applyToGraph(graph());
			updateUI();
		}
	} 

}
