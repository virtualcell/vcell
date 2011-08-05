/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.pathwaycommons.search;

/*   PCTextResponse  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response from a web request from Pathway Commons consisting of text
 */

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;

public class PCTextResponse extends PathwayCommonsResponse {

	protected String text;
	
	public PCTextResponse(PathwayCommonsRequest requestNew, String textNew) {
		super(requestNew);
		text = textNew;
	}
	
	public String text() { return text; }

}
