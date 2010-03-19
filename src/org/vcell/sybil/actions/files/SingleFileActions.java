package org.vcell.sybil.actions.files;

/*  SingleFileActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to January 2009
 *  Actions relating to a single file in its entirety
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;

public class SingleFileActions extends ActionTree {
	private static final long serialVersionUID = 8170923334410657865L;
	public SingleFileActions(CoreManager coreManager) {
		add(new FileNewAction(
				new ActionSpecs("New", "New file", "Start a new file", "files/new.gif"), 
				coreManager));
		add(new FileOpenAction(
				new ActionSpecs("Open", "Open file", "Open file", "files/open.gif"), 
				coreManager));
		add(new FileSaveAction(
				new ActionSpecs("Save", "Save file", "Save file under same name", 
						"files/save.gif"), 
						coreManager));
		add(new FileSaveAsAction(
				new ActionSpecs("Save As", "Save file as", "Save file under different name", 
						"files/saveas.gif"), 
						coreManager));
	}
}