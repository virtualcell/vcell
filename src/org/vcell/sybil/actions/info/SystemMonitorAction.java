package org.vcell.sybil.actions.info;

/*   InfoAction  --- by Oliver Ruebenacker, UCHC --- September 2008 to February 2010
 *   Display info about Sybil, including version
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;

public class SystemMonitorAction extends BaseAction {

	private static final long serialVersionUID = -4196679916462335017L;
	
	public SystemMonitorAction(ActionSpecs newSpecs, CoreManager coreManager) {
		super(newSpecs, coreManager);
	}	
	
	public void actionPerformed(ActionEvent event) {
		coreManager.ui().createSystemMonitorDialogSpace().showInfoDialog();
	}

}

