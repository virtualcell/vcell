package org.vcell.sybil.actions.files;

/*  FileActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to February 2010
 *  Actions relating to files or the application in its entirety
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;

public class FileActions extends ActionTree {
	private static final long serialVersionUID = 8170923334410657865L;
	public FileActions(CoreManager coreManager) {
		add(new SingleFileActions(coreManager));
		add(new ExitAction(
				new ActionSpecs("Exit", "Exit", "Exit Application", "files/exit.gif"), 
				coreManager));
	}
}