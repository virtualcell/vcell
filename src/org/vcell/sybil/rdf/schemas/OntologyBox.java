/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.schemas;

import java.util.List;

import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.RDFBox;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * a container for an ontology
 * @author ruebenacker
 *
 */

public interface OntologyBox extends RDFBox {

	public Model getRdf();
	public String uri();	
	public NameSpace ns();
	public String label();
	public List<DatatypeProperty> labelProperties();

}
