package org.vcell.sybil.init;

/*   InitMain  --- by Oliver Ruebenacker, UCHC --- April to November 2007
 *   Provides the universe in which the application runs, meaning this class
 *   will be instantiated exactly once under any circumstances.
 */

import org.vcell.sybil.gui.GUIMainInit;

import cbit.vcell.biomodel.BioModel;

public class MainInit extends GUIMainInit {

	public static interface SubInitGraph extends GUIMainInit.SubInitGraph {}
	
	protected MainInit(BioModel bioModel, SubInitGraph subInitGraphNew) { 
		super(bioModel, subInitGraphNew); 
	}
	
}
