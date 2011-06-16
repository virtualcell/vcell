/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.schemas.util;

/*   BioPAX2Util  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   Useful stuff for dealing with BioPAX Level 2
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.rdf.crawl.TypeCrawler;
import org.vcell.sybil.rdf.schemas.BioPAX2;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class BioPAX2Util {
	
	public static final Set<Resource> physicalEntityClasses = 
		TypeCrawler.subTypes(BioPAX2.schema, BioPAX2.physicalEntity);

	public static Set<Resource> physicalEntitiesDirect(Model model) {
		HashSet<Resource> entities = new HashSet<Resource>();
		for(Resource entityClass : physicalEntityClasses) {
			ResIterator entityIter = model.listSubjectsWithProperty(RDF.type, entityClass);
			while(entityIter.hasNext()) {
				entities.add(entityIter.nextResource());
			}
		}
		return entities;
	}
	
}
