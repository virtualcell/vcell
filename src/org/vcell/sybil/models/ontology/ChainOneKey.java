/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.ontology;

/*   ChainOneKey  --- by Oliver Ruebenacker, UCHC --- January 2008 to March 2009
 *   The parameters of a ChainOneQuery, also used as a has key to store results
 */

import org.vcell.sybil.util.keys.KeyOfThree;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class ChainOneKey extends KeyOfThree<Resource, Property, Resource> {
	public ChainOneKey(Resource type1, Property relation, Resource type2) { 
		super(type1, relation, type2); 
	}
	public Resource type1() { return a(); }
	public Property relation() { return b(); }
	public Resource type2() { return c(); }
}
