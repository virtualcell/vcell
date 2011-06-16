/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions;

/*   SpecAction  --- by Oliver Ruebenacker, UCHC --- May 2007 to January 2010
 *   Actions created based on ActionSpecs
 */

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public abstract class SpecAction extends AbstractAction {

	private static final long serialVersionUID = -2832534614701076090L;
	protected ActionSpecs specs;
	
	public SpecAction(ActionSpecs newSpecs) {
		super(newSpecs.getName());
		specs = newSpecs;
		putValue(Action.SHORT_DESCRIPTION, specs.getShortSpec());
		putValue(Action.LONG_DESCRIPTION, specs.getLongSpec());
		if(specs.getIcon() != null) { putValue(Action.SMALL_ICON, specs.getIcon()); }
	}
	
	public ActionSpecs getSpecs() { return specs; }

	public Component requester(ActionEvent event) { return RequesterProvider.requester(event); }
	
}
