package org.vcell.sybil.workers.files;

/*   FileSaveAsWorker  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2010
 *   A SwingWorker to save the current file in a different location
 */

import java.io.File;
import java.io.FileNotFoundException;
import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.state.SystemWorker;

public class FileSaveAsWorker extends SystemWorker {

	protected FileManager fileManager;
	protected File file;
	protected FileEvent event;

	public FileSaveAsWorker(FileManager fileMan, File newFile) {
		this.fileManager = fileMan;
		file = newFile;
	}
	
	public Object doConstruct() {
		try { event = fileManager.saveFileAs(file); } 
		catch (FileNotFoundException e) { CatchUtil.handle(e); }
		return null;
	}

	public void doFinished() { 
		if(event != null) { fileManager.listeners().fileEvent(event); }
	}
	
	public String getNonSwingTaskName() { return "start saving file under new name"; }
	public String getSwingTaskName() { return "finish saving file under new name"; }
};


