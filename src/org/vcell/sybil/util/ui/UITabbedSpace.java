package org.vcell.sybil.util.ui;

/*   UITabbedSpace  --- by Oliver Ruebenacker, UCHC --- August 2007 to January 2009
 *   An interface for a wrappers of JTabbedPane or similar
 */


public interface UITabbedSpace extends UISpace {

    public void addTab(String newName, UIComponent newComp);
	public void removeTab(UIComponent someComp);
	public void selectTab(UIComponent comp);
}
