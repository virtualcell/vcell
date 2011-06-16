/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
