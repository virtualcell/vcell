package org.vcell.sybil.util.ui;

/*   UIFileChooserSpace  --- by Oliver Ruebenacker, UCHC --- October 2007 to January 2009
 *   A space for a file chooser
 */

import java.io.File;

public interface UIFileChooserSpace extends UIComponent {

	public void selectFileToOpen();
	public void selectFileToSave();
	public void selectFile(String purpose);
	public void addChoosableFileFilter(UIFileFilter newFilter);
	public File selectedFile();
}
