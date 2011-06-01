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
 *   Panel to display the response to one request
 */

import javax.swing.JPanel;

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;

public class ResponsePanel extends JPanel {
	private static final long serialVersionUID = -8902003828215137461L;
	protected PathwayCommonsResponse response;	
	public ResponsePanel(PathwayCommonsResponse responseNew) { response = responseNew; }
	public PathwayCommonsResponse response() { return response; }
}
