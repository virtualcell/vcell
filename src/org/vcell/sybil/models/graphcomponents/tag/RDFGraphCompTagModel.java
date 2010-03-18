package org.vcell.sybil.models.graphcomponents.tag;

/*   SyCoTagModel  --- by Oliver Ruebenacker, UCHC --- March 2008
 *   SyCoTag for SyCo created to support a model
 */

import org.vcell.sybil.models.graph.GraphModel;

public class RDFGraphCompTagModel implements RDFGraphCompTag {

	protected GraphModel model;
	
	public RDFGraphCompTagModel(GraphModel model) { this.model = model; }
	
	public GraphModel model() { return model; }
	
}
