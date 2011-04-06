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
	
	@Override
	public Object doConstruct() {
		try { event = fileManager.newFile(); } 
		catch (Exception e) { CatchUtil.handle(e); }
		return null;
	}
	
	@Override
	public void doFinished() {
		if(event != null) { fileManager.listeners().fileEvent(event); }
	}

	@Override
	public String getNonSwingTaskName() { return "start creating blank data set"; }
	@Override
	public String getSwingTaskName() { return "done creating blank data set"; }

};
