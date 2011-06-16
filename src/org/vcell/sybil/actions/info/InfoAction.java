/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions.info;

/*   InfoAction  --- by Oliver Ruebenacker, UCHC --- September 2008 to January 2009
 *   Display info about Sybil, including version
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.models.specs.SybilSpecs;

public class InfoAction extends BaseAction {

	private static final long serialVersionUID = -4196679916462335017L;
	
	public InfoAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}	
	
	public void actionPerformed(ActionEvent event) {
		System.out.println(SybilSpecs.longText);
		coreManager.ui().createInfoDialogSpace().showInfoDialog();
	}

}

