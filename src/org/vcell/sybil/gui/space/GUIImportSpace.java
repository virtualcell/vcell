package org.vcell.sybil.gui.space;

/*   GUIImportSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2009
 *   GUI for data import
 */

import org.vcell.sybil.gui.bpimport.ImportPanel;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.ui.UIImportSpace;

public class GUIImportSpace<P extends ImportPanel> extends GUISpace<P> implements UIImportSpace {
	
	protected GUIImportSpace(P newPanel) { super(newPanel); }

	public P importPanel() { return component(); }
	
	public FileManager fileManager() { return component().fileManager(); }
	public void requestFocusForThis() { component().focusOnInput(); }

}
