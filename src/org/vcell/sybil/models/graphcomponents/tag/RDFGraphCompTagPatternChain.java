package org.vcell.sybil.models.graphcomponents.tag;

/*   SyCoTagPatternChain  --- by Oliver Ruebenacker, UCHC --- April 2008
 *   Tag for a SyCo created as a chain matching a pattern
 */

import org.vcell.sybil.models.ontology.ChainTwoKey;

public class RDFGraphCompTagPatternChain implements RDFGraphCompTag {

	protected ChainTwoKey key;
	
	public RDFGraphCompTagPatternChain(ChainTwoKey keyNew) { key = keyNew; }
	
	public ChainTwoKey key() { return key; }
}
