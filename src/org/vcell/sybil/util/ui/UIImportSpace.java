package org.vcell.sybil.util.ui;

/*   UIImportSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2009
 *   An interface for import UI
 */

import org.vcell.sybil.models.io.FileManager;

public interface UIImportSpace extends UIComponent {
	
	public FileManager fileManager();
	public void requestFocusForThis();


}
