package org.vcell.sybil.gui.space;

/*   GUITextSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2010
 *   An interface for text components
 */

import javax.swing.JTextArea;
import javax.swing.text.Document;

import org.vcell.sybil.util.ui.UITextSpace;

public class GUITextSpace<A extends JTextArea> extends GUISpace<A> implements UITextSpace {
	
	protected GUITextSpace(A newArea) { super(newArea); }
	public A textArea() { return component(); }
	public Document document() { return textArea().getDocument(); }
	public void setDocument(final Document newDocument) { textArea().setDocument(newDocument); }

}
