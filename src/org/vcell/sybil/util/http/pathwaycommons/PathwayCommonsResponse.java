/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.pathwaycommons;

/*   PathwayCommonsResponse  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response to a web request from Pathway Commons
 */

public class PathwayCommonsResponse {

	protected PathwayCommonsRequest request;
	
	public PathwayCommonsResponse(PathwayCommonsRequest request) {
		this.request = request;
	}
	
	public PathwayCommonsRequest request() { return request; }
	
}
