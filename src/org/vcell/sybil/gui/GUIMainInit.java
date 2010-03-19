package org.vcell.sybil.gui;

/*   GUIMain  --- by Oliver Ruebenacker, UCHC --- April 2007 to April 2009
 *   Provides the universe in which the GUI lives. To be instantiated exactly once.
 */

import org.vcell.sybil.actions.ActionsMainInit;
import org.vcell.sybil.gui.graph.GUIGraphInit;

import cbit.vcell.biomodel.BioModel;

public class GUIMainInit extends ActionsMainInit {

	protected GUIGraphInit guiGraphInit = new GUIGraphInit();
	protected GUIManager myGUI;
	
	public static interface SubInitGraph extends ActionsMainInit.SubInitGraph {}
	
	public GUIMainInit(BioModel bioModel, SubInitGraph subInitGraph) { super(bioModel, subInitGraph); }
	
	protected GUIGraphInit guiGraphInit() {
		if(guiGraphInit == null) {
			guiGraphInit = new GUIGraphInit();
		}
		return guiGraphInit;
	}
	
}
