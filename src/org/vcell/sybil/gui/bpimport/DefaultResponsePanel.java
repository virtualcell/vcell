/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.bpimport;

/*   ResponsePanel  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Panel to display the response to one request by simple conversion to String
 */

import java.awt.BorderLayout;

import javax.swing.JTextArea;

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;

public class DefaultResponsePanel extends ResponsePanel {
	
	private static final long serialVersionUID = 5569708707128075620L;

	public DefaultResponsePanel(PathwayCommonsResponse responseNew) {
		super(responseNew);
		String responseText = responseNew != null ? responseNew.toString() : "null";
		JTextArea textArea = new JTextArea();
		textArea.setText(responseText);
		setLayout(new BorderLayout());
		add(textArea);
	}
	
}
