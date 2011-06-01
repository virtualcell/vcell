/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.tree.pckeyword;

/*   ErrorResponseWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Wrapper for a tree node with a PCKeywordResponse
 */

import org.vcell.sybil.util.http.pathwaycommons.search.Error;
import org.vcell.sybil.util.http.pathwaycommons.search.PCErrorResponse;

public class ErrorResponseWrapper extends ResponseWrapper {

	public ErrorResponseWrapper(PCErrorResponse response) {
		super(response);
		Error error = response.error();
		append("Code: " + error.code());
		append("Message: " + error.msg());
		append("Details: " + error.details());
	}
	
	public String toString() {
		Error error = data().error();
		return "Error " + error.code() + ": " + error.msg();
	}
	
	public PCErrorResponse data() { return (PCErrorResponse) super.data(); }

}
