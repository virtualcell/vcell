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

	@Override
	public Object doConstruct() {
		try { event = fileManager.saveFile(); } 
		catch (Exception e) { CatchUtil.handle(e); }
		return null;
	}

	@Override
	public void doFinished() { 
		if(event != null) { fileManager.listeners().fileEvent(event); }
	}
	
	@Override
	public String getNonSwingTaskName() { return "start saving file"; }
	@Override
	public String getSwingTaskName() { return "finish saving file"; }
};


