package org.vcell.sybil.models.updater;

/*   UIGlobalNameUpdater  --- by Oliver Ruebenacker, UCHC --- November 2007 to June 2009
 *   Receives events from a FileMan and updates a global name on a UserInterface.
 *   Typical use is to display the name of the currently open file on the frame
 */

import java.io.File;

import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.util.ui.UserInterface;


public class UIGlobalNameUpdater implements FileEvent.Listener {
	
	protected UserInterface ui;

	public UIGlobalNameUpdater(UserInterface ui) { 
		this.ui = ui; 
	}
	
	public void fileEvent(FileEvent event) { 
		File file = event.fileManager().file();
		String name = "SyBil"; // the title of the Sybil panel
		if(file != null) { name = file.getName(); }
		ui.setTitle(name); 
	}

}
