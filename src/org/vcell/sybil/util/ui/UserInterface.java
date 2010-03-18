package org.vcell.sybil.util.ui;

/*   UserInterface  --- by Oliver Ruebenacker, UCHC --- August 2007 to March 2010
 *   An interface for GUIBase or equvalent
 */

public abstract class UserInterface {

	protected UIFrameSpace frameSpace;
	
	abstract public UIFrameSpace frameSpace();
	
	public void setTitle(String newName) { frameSpace().setTitle(newName); }
	public String title() { return frameSpace().title(); }
	
	abstract public UITabbedSpace createTabbedSpace();
	abstract public UIScrollSpace createScrollSpace(UIComponent comp);
	abstract public UIImportSpace createImportSpace();
	abstract public UITextSpace createTextSpace();
	abstract public UIFileChooserSpace createFileChooserSpace();
	abstract public UIPortSpace createPortSpace();
	abstract public UIInfoDialogSpace createInfoDialogSpace();
	abstract public UIInfoDialogSpace createSystemMonitorDialogSpace();
}
