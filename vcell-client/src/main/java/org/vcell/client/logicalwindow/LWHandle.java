package org.vcell.client.logicalwindow;

import java.awt.Frame;
import java.awt.Window;
import java.util.Iterator;

import javax.swing.JMenuItem;

/**
 * represent logical connection between windows. May be implemented by frames or modal dialogs
 */
public interface LWHandle extends Iterable<LWHandle>{
	/**
	 * supported modalities. <i>ANCESTOR</i> could be added but omitted per YAGNI
	 */
	public enum LWModality {
		/**
		 * blocks nothing
		 */
		MODELESS,
		/**
		 * only blocks logical parent
		 */
		PARENT_ONLY,
		//ANCESTOR
		;
	}
	
	public LWModality getLWModality( );
	
	/**
	 * @return corresponding Window (not null)
	 */
	public Window getWindow( );
	/**
	 * @return iterator of handles corresponding to visible windows (includes iconified)
	 */
	public Iterator<LWHandle> iterator( );
	
	/**
	 * @return parent, if any, or null
	 */
	public LWContainerHandle getlwParent( );
	
	/**
	 * if iconified, return to normal state. If not, no op 
	 */
	public void unIconify();
	
	/**
	 * @param level indentation level of title 
	 * @return menu item which supports this
	 */
	public JMenuItem menuItem(int level);
	
	/**
	 * close children recursively
	 */
	public void closeRecursively( );
	
	/**
	 * get String for menu. Indentation not desired 
	 * @return String for use in menu
	 */
	public String menuDescription( );
	
	/**
	 * @return non-null Traits
	 */
	public LWTraits getTraits();
	
	
	/**
	 * restore Frame to normal if it's currently iconified
	 * @param frame not null
	 */
	public static void unIconify(Frame frame) {
		int st = frame.getState();
		if (st == Frame.ICONIFIED) {
			frame.setState(Frame.NORMAL);
		}
	}
}
