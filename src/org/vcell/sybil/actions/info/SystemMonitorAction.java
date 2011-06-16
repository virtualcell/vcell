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

/*   InfoAction  --- by Oliver Ruebenacker, UCHC --- September 2008 to February 2010
 *   Display info about Sybil, including version
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;

public class SystemMonitorAction extends BaseAction {

	private static final long serialVersionUID = -4196679916462335017L;
	
	public SystemMonitorAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}	
	
	public void actionPerformed(ActionEvent event) {
		coreManager.ui().createSystemMonitorDialogSpace().showInfoDialog();
	}

}

