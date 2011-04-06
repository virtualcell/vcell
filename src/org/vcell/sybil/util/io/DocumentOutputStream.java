package org.vcell.sybil.util.io;

/*   DocumentOutputStream  --- by Oliver Ruebenacker, UCHC --- February 2007 to February 2009
 *   An output stream coupled to a document, useful to print to a JTextArea
 */

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class DocumentOutputStream extends OutputStream {

	protected Document document;
	
	public DocumentOutputStream() { document = new PlainDocument(); }
	public DocumentOutputStream(Document newDocument) { document = newDocument; }
	
	@Override
	public void write(int character) throws IOException {
		try { document.insertString(document.getLength(), "" + ((char) character), null);
		} catch (BadLocationException e) { e.printStackTrace(); } // Should never happen
	}
	
	public Document document() { return document; }

}
