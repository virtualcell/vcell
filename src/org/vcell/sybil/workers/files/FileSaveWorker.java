/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.workers.files;

/*   FileSaveWorker  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2010
 *   A SwingWorker to save the current file in its original location
 */

import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.state.SystemWorker;

public class FileSaveWorker extends SystemWorker {

	protected FileManager fileManager;
	protected FileEvent event;
	
	public FileSaveWorker(FileManager fileMan) {
		this.fileManager = fileMan;
	}

	public Object doConstruct() {
		try { event = fileManager.saveFile(); } 
		catch (Exception e) { CatchUtil.handle(e); }
		return null;
	}

	public void doFinished() { 
		if(event != null) { fileManager.listeners().fileEvent(event); }
	}
	
	public String getNonSwingTaskName() { return "start saving file"; }
	public String getSwingTaskName() { return "finish saving file"; }
};


