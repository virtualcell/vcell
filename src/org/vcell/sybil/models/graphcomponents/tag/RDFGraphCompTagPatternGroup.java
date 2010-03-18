package org.vcell.sybil.models.graphcomponents.tag;

/*   SyCoTagNodeGroup  --- by Oliver Ruebenacker, UCHC --- March 2008 to March 2009
 *   Tag for a SyCo created as a group (e.g. a ResourceNodeGroup matching a pattern)
 */

import org.vcell.sybil.models.ontology.ChainOneKey;

public class RDFGraphCompTagPatternGroup implements RDFGraphCompTag {

	protected ChainOneKey key;
	
	public RDFGraphCompTagPatternGroup(ChainOneKey keyNew) { this.key = keyNew; }
	
	public ChainOneKey key() { return key; }
}
