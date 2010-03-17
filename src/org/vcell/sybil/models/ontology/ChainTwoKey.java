package org.vcell.sybil.models.ontology;

/*   ChainTwoKey  --- by Oliver Ruebenacker, UCHC --- January 2008 to March 2009
 *   The parameters of a ChainTwoQuery, also used as a has key to store results
 */

import org.vcell.sybil.util.keys.KeyOfFive;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class ChainTwoKey extends KeyOfFive<Resource, Property, Resource, Property, Resource> {

	public ChainTwoKey(Resource type1, Property relation1, Resource type2, 
			Property relation2, Resource type3) { 
		super(type1, relation1, type2, relation2, type3); 
	}

	public Resource type1() { return a(); }
	public Property relation1() { return b(); }
	public Resource type2() { return c(); }
	public Property relation2() { return d(); }
	public Resource type3() { return e(); }
}