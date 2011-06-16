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

/*   FileOpenAction  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2010
 *   An action to display a dialog, let the user select a file and open that file as a data file
 */

import java.awt.event.ActionEvent;
import java.io.File;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.util.ui.UIFileChooserSpace;
import org.vcell.sybil.workers.files.FileOpenWorker;

import org.vcell.sybil.actions.files.FileFilterOWL;
import org.vcell.sybil.actions.files.FileOpenAction;

public class FileOpenAction extends BaseAction {

	private static final long serialVersionUID = 1448712953485801574L;

	public FileOpenAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}	

	public void actionPerformed(ActionEvent event) {
		UIFileChooserSpace fileChooserSpace = coreManager().ui().createFileChooserSpace();
		fileChooserSpace.addChoosableFileFilter(new FileFilterOWL());
		fileChooserSpace.selectFileToOpen();
		File file = fileChooserSpace.selectedFile();
		if(file != null && file.canRead()) { 
			(new FileOpenWorker(coreManager().fileManager(), file)).run(requester(event)); 
		}
	}

}
