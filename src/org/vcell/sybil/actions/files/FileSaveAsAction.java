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

/*   FileSaveAsAction  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2010
 *   An action to display a dialog, let the user select a file location and save the file in that location
 */

import java.awt.event.ActionEvent;
import java.io.File;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.util.ui.UIFileChooserSpace;
import org.vcell.sybil.workers.files.FileSaveAsWorker;

import org.vcell.sybil.actions.files.FileFilterOWL;
import org.vcell.sybil.actions.files.FileSaveAsAction;

public class FileSaveAsAction extends BaseAction {

	private static final long serialVersionUID = 5290420090644416617L;

	public FileSaveAsAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}	
	
	public void actionPerformed(ActionEvent event) {
		
		UIFileChooserSpace fileChooserSpace = coreManager().ui().createFileChooserSpace();
		fileChooserSpace.addChoosableFileFilter(new FileFilterOWL());
		fileChooserSpace.selectFileToSave();
		File file = fileChooserSpace.selectedFile();
		if(file != null) {
			coreManager().frameSpace().updateUI();
			(new FileSaveAsWorker(coreManager().fileManager(), file)).run(requester(event));			
		}
	}

}

