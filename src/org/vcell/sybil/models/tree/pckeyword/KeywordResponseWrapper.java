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

import org.vcell.sybil.util.http.pathwaycommons.search.Hit;
import org.vcell.sybil.util.http.pathwaycommons.search.PCKeywordResponse;

public class KeywordResponseWrapper extends ResponseWrapper {

	public KeywordResponseWrapper(PCKeywordResponse response) {
		super(response);
		for(Hit hit : response.hits()) { append(new HitWrapper(hit)); }
	}
	
	public PCKeywordResponse data() { return (PCKeywordResponse) super.data(); }
	public PCKeywordResponse response() { return (PCKeywordResponse) super.data(); }

	public String toString() {
		String keyword = response().request().keyword();
		int nHitsReported = response().hits().size();
		int nHitsTotal = response().totalNumHits();
		String numberMessage = nHitsReported == nHitsTotal ? nHitsTotal + "" : 
			nHitsReported + " of " + nHitsTotal;
		return numberMessage + " matches for \"" + keyword + "\"";
	}
	
}
