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
