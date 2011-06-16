/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions.files;

/*   FileOpenAction  --- by Oliver Ruebenacker, UCHC --- April 2007 to May 2009
 *   An action to save the current file
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.workers.files.FileSaveWorker;

import org.vcell.sybil.actions.files.FileSaveAction;

public class FileSaveAction extends BaseAction {

	private static final long serialVersionUID = 5290420090644416617L;

	public FileSaveAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}	
	
	public void actionPerformed(ActionEvent event) {
		(new FileSaveWorker(coreManager().fileManager())).run(requester(event));
	}

}

