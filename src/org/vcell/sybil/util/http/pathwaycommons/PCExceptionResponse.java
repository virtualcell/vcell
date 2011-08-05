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

/*   Response  --- by Oliver Ruebenacker, UCHC --- March to December 2009
 *   Response from a web request from Pathway Commons consisting of an exception
 */

public class PCExceptionResponse extends PathwayCommonsResponse {

	protected Exception exception;
	
	public PCExceptionResponse(PathwayCommonsRequest requestNew, Exception exceptionNew) {
		super(requestNew);
		exception = exceptionNew;
	}
	
	public Exception exception() { return exception; }

}
