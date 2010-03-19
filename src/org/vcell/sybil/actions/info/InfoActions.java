package org.vcell.sybil.actions.info;

/*  FileActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to February 2010
 *  Actions relating to files or the application in its entirety
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;

public class InfoActions extends ActionTree {
	private static final long serialVersionUID = 8170923334410657865L;
	public InfoActions(CoreManager modSysNew) {
		add(new InfoAction(
				new ActionSpecs("SyBiL Info", "SyBiL Info", "Info about SyBiL", "files/exit.gif"), 
				modSysNew));
		add(new SystemMonitorAction(
				new ActionSpecs("System Monitor", "Show System Monitor", "Show various system information", 
						"files/exit.gif"), modSysNew));
	}
}