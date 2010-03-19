package org.vcell.sybil.gui.bpimport;

/*   TextResponsePanel  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Panel to display the response to one request by simple conversion to String
 */

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.vcell.sybil.util.http.pathwaycommons.search.PCTextResponse;

public class TextResponsePanel extends ResponsePanel {
	
	private static final long serialVersionUID = 5569708707128075620L;

	public TextResponsePanel(PCTextResponse responseNew) {
		super(responseNew);
		String responseText = responseNew != null ? responseNew.text() : "null";
		JTextArea textArea = new JTextArea();
		textArea.setText(responseText);
		setLayout(new BorderLayout());
		add(new JScrollPane(textArea));
	}
	
	public PCTextResponse response() { return (PCTextResponse) super.response(); }
	
}