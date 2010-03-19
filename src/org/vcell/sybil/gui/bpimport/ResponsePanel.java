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