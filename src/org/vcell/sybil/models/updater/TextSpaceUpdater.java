package org.vcell.sybil.models.updater;

/*   TextSpaceUpdater  --- by Oliver Ruebenacker, UCHC --- November 2007 to June 2009
 *   Receives events from a FileMan and updates a textSpace
 */

import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.util.io.DocumentOutputStream;
import org.vcell.sybil.util.ui.UITextSpace;


public class TextSpaceUpdater implements FileEvent.Listener {

	protected UITextSpace textSpace;
	
	public TextSpaceUpdater(UITextSpace textSpace) { 
		this.textSpace = textSpace; 
	}
	
	public void fileEvent(FileEvent event) {
		if(event.modelHasChanged()) {
			DocumentOutputStream out = new DocumentOutputStream();
			event.fileManager().box().getRdf().write(out);
			textSpace.setDocument(out.document());			
		}
	}

}
