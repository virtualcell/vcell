/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.baptizer;

/*   RDFLocalName  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   Creates a new local name for (e.g. anonymous) resources
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class SimpleSBPAXLocalNamer implements RDFLocalNamer {
	
	public String newLocalName(Resource resource) {
		Set<Resource> types = new HashSet<Resource>();
		StmtIterator stmtIter = resource.listProperties(RDF.type);
		while(stmtIter.hasNext()) {
			RDFNode object = stmtIter.nextStatement().getObject();
			if(object instanceof Resource) { types.add((Resource) object); }
		}
		if(types.contains(SBPAX.Process)) { return "Process"; }
		if(types.contains(SBPAX.Species)) { return "Species"; }
		if(types.contains(SBPAX.Substance)) { return "Substance"; }
		if(types.contains(SBPAX.Location)) { return "Location"; }
		if(types.contains(SBPAX.SystemModel)) { return "Model"; }
		if(types.contains(SBPAX.ProcessModel)) { return "PM"; }
		if(types.contains(SBPAX.ProcessParticipantCatalyst)) { return "Catalyst"; }
		if(types.contains(SBPAX.ProcessParticipantLeft)) { return "Left"; }
		if(types.contains(SBPAX.ProcessParticipantRight)) { return "Right"; }
		if(types.contains(SBPAX.ProcessParticipant)) { return "Part"; }
		return "Item";
	}

}
