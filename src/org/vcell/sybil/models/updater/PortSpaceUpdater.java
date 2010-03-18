package org.vcell.sybil.models.updater;

/*   PortSpaceUpdater  --- by Oliver Ruebenacker, UCHC --- November 2007 to October 2009
 *   Listens to events from an Evaluator and updates a portSpace if necessary
 */

import org.vcell.sybil.util.ui.UIPortSpace;
import org.vcell.sybil.models.ontology.Evaluator;

public class PortSpaceUpdater implements Evaluator.Event.Listener {

	protected UIPortSpace portSpace;

	public PortSpaceUpdater(UIPortSpace portSpaceNew) { 
		portSpace = portSpaceNew;
	}

	protected void update(Evaluator evaluator) {
		if(evaluator != null) { portSpace.update(evaluator.view()); } 
	}

	public void evaluatorEvent(Evaluator.Event event) { 
		portSpace.update(event.evaluator().view()); 
	}

}
