/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
