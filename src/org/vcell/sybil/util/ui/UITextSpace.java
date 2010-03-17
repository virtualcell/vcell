package org.vcell.sybil.util.ui;

/*   GUITextSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to January 2009
 *   An interface for text components
 */

import javax.swing.text.Document;

public interface UITextSpace extends UIComponent {
	
	public Document document();
	public void setDocument(Document newDocument);
	

}
