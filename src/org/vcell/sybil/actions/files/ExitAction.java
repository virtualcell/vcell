package org.vcell.sybil.actions.files;

/*   ExitAction  --- by Oliver Ruebenacker, UCHC --- July 2007 to January 2009
 *   Exit application
 */

import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;


public class ExitAction extends BaseAction {

	private static final long serialVersionUID = 1665002722181083384L;

	public ExitAction(ActionSpecs newSpecs, CoreManager coreManager) { super(newSpecs, coreManager); }
	
	public void actionPerformed(ActionEvent arg0) { 
		this.coreManager().ui().stopSyBiL();
	}
	
}
