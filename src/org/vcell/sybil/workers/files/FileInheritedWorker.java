package org.vcell.sybil.workers.files;

/*   FileInheritedWorker  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2010
 *   A worker to create a new empty file in the background, read it, create model and its views
 */

import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.state.SystemWorker;

public class FileInheritedWorker extends SystemWorker {
	
	protected FileManager fileManager;
	protected FileEvent event;
	
	public FileInheritedWorker(FileManager fileMan) {
		this.fileManager = fileMan;
	}
	
	@Override
	public Object doConstruct() {
		try { event = fileManager.inheritedFile(); } 
		catch (Exception e) { CatchUtil.handle(e); }
		return null;
	}
	
	@Override
	public void doFinished() {
		if(event != null) { fileManager.listeners().fileEvent(event); }
	}

	@Override
	public String getNonSwingTaskName() { return "fetching data"; }
	@Override
	public String getSwingTaskName() { return "done fetching data"; }

};
