package org.vcell.sybil.workers.files;

/*   OpenFileWorker  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   A worker to open a file in the background, read it, create model and its views
 */

import java.io.File;

import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.state.SystemWorker;

public class FileOpenWorker extends SystemWorker {
	
	protected FileManager fileManager;
	protected File file;
	protected FileEvent event;
	
	public FileOpenWorker(FileManager fileMan, File file) {
		this.fileManager = fileMan;
		this.file = file;
	}
	
	public Object doConstruct() {
		try { event = fileManager.openFile(file, null); } 
		catch (Exception e) { CatchUtil.handle(e); }
		return null;
	}
	
	public void doFinished() {
		if(event != null) { fileManager.listeners().fileEvent(event); }
	}

	public String getNonSwingTaskName() { return "start opening file"; }
	public String getSwingTaskName() { return "finish opening file"; }

};
