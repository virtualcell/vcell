/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.smelt;

/*   BioPAX2Smelter  --- by Oliver Ruebenacker, UCHC --- July 2009
 *   Cleans up BioPAX Level 2 data
 */

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class BioPAX2Smelter implements RDFSmelter {
	
	SameAsCrystalizer sameAsCrystalizer = new SameAsCrystalizer();
	BP2XRefSmelter xRefSmelter = new BP2XRefSmelter();
	BP2OCVSmelter ocvSmelter = new BP2OCVSmelter();
	BP2ParticipantSplitter participantSplitter = new BP2ParticipantSplitter();
	
	public Model smelt(Model rdf) {
		Model rdfNew = ModelFactory.createDefaultModel();
		rdfNew.add(rdf);
		rdfNew.setNsPrefixes(rdf);
		rdfNew = sameAsCrystalizer.smelt(rdfNew);
		rdfNew = xRefSmelter.smelt(rdfNew);
		rdfNew = sameAsCrystalizer.smelt(rdfNew);
		rdfNew = ocvSmelter.smelt(rdfNew);
		rdfNew = sameAsCrystalizer.smelt(rdfNew);
		return rdfNew;
	}	
	
}
