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

/*   FileNewWorker  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2010
 *   A worker to create a new empty file in the background, read it, create model and its views
 */

import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.state.SystemWorker;

public class FileNewWorker extends SystemWorker {
	
	protected FileManager fileManager;
	protected FileEvent event;
	
	public FileNewWorker(FileManager fileMan) { this.fileManager = fileMan; }
	
	public Object doConstruct() {
		try { event = fileManager.newFile(); } 
		catch (Exception e) { CatchUtil.handle(e); }
		return null;
	}
	
	public void doFinished() {
		if(event != null) { fileManager.listeners().fileEvent(event); }
	}

	public String getNonSwingTaskName() { return "start creating blank data set"; }
	public String getSwingTaskName() { return "done creating blank data set"; }

};
