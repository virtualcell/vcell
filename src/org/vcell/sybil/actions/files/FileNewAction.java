package org.vcell.sybil.actions.files;

/*   FileNewAction  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2010
 *   Action to create a new file
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;

import org.vcell.sybil.actions.files.FileNewAction;
import org.vcell.sybil.gui.space.GUIFrameSpace;
import org.vcell.sybil.workers.files.FileNewWorker;

public class FileNewAction extends BaseAction {

	private static final long serialVersionUID = -2069822872096376519L;

	public FileNewAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}
	
	public void actionPerformed(ActionEvent event) {
		GUIFrameSpace frameSpace = (GUIFrameSpace) coreManager.frameSpace();
		new FileNewWorker(coreManager.fileManager()).run(frameSpace.frame());
	}

}
