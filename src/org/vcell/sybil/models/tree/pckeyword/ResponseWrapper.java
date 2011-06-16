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

/*   KeywordResponseWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Wrapper for a tree node with a PCKeywordResponse
 */

import org.vcell.sybil.models.tree.NodeDataWrapper;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;

public class ResponseWrapper extends NodeDataWrapper<PathwayCommonsResponse> {

	public ResponseWrapper(PathwayCommonsResponse response) {
		super(response);
	}

}
