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

/*   PCTextModelResponse  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Response from a web request from Pathway Commons consisting of text converted to a Jena model
 */

import org.openrdf.model.Graph;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;

public class PCTextModelResponse extends PCTextResponse {

	protected Graph model;
	
	public PCTextModelResponse(PathwayCommonsRequest requestNew, String textNew, Graph modelNew) {
		super(requestNew, textNew);
		model = modelNew;
	}
	
	public Graph model() { return model; }
	
}
