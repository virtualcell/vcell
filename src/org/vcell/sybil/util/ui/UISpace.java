package org.vcell.sybil.util.ui;

/*   UISpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to January 2009
 *   An interface for wrappers for user interface components such as java.awt.Container
 */

public interface UISpace extends UIComponent {
	
	void add(UIComponent newSubSpace);
		
}
