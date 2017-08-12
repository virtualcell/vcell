/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas;

/*   SBPAX  --- by Oliver Ruebenacker, UCHC --- April 2008 to May 2011
 *   The SBPAX Level 3 schema
 */

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.util.NameSpace;
import org.sbpax.schemas.util.OntUtil;


public class SBPAX3 {

	public static final String uri = "http://vcell.org/sbpax3";

	public static final NameSpace ns = new NameSpace("sbx3", uri + "#");

	public static final Graph schema = new HashGraph();

	public static final URI ontology = OntUtil.createOntologyNode(schema, uri);

	static {
		schema.add(ontology, OWL.IMPORTS, BioPAX3.ontology);
		schema.add(ontology, OWL.IMPORTS, UOMECore.ontology);
	}

	public static final URI SBVocabulary = OntUtil.createClass(schema, ns + "SBVocabulary");
	public static final URI SBEntity = OntUtil.createClass(schema, ns + "SBEntity");
	public static final URI SBMeasurable = OntUtil.createClass(schema, ns + "SBMeasurable");
	public static final URI SBState = OntUtil.createClass(schema, ns + "SBState");

	public static final URI sbTerm = OntUtil.createObjectProperty(schema, ns + "sbTerm");
	public static final URI sbSubEntity = OntUtil.createObjectProperty(schema, ns + "sbSubEntity");

	public static final URI hasNumber = OntUtil.createDatatypeProperty(schema, ns + "hasNumber");
	public static final URI hasUnit = OntUtil.createObjectProperty(schema, ns + "hasUnit");

	static {

		schema.add(SBVocabulary, RDFS.SUBCLASSOF, BioPAX3.ControlledVocabulary);

		schema.add(BioPAX3.Entity, RDFS.SUBCLASSOF, SBEntity);
		schema.add(SBMeasurable, RDFS.SUBCLASSOF, SBEntity);
		schema.add(SBState, RDFS.SUBCLASSOF, SBEntity);

		schema.add(sbTerm, RDFS.DOMAIN, SBEntity);
		schema.add(sbTerm, RDFS.RANGE, SBVocabulary);
		schema.add(sbSubEntity, RDFS.DOMAIN, SBEntity);
		schema.add(sbSubEntity, RDFS.RANGE, SBEntity);
		schema.add(hasUnit, RDFS.DOMAIN, SBMeasurable);
		schema.add(hasUnit, RDFS.RANGE, UOMECore.UnitOfMeasurement);
		schema.add(hasNumber, RDFS.DOMAIN, SBEntity);
		schema.add(hasNumber, RDFS.RANGE, XMLSchema.FLOAT);
	}

	static {

		// OntUtil.addTypedComment(schema, , "");

		OntUtil.addTypedComment(schema, hasNumber, 
				"The number representing this measurable systems biology entity.\n" +
				"Example: a concentration of 5 mol/l would have the number 5 together with " +
		"the unit string \"mol/l\"");

		OntUtil.addTypedComment(schema, hasUnit, 
				"An instance of uome-core:UnitOfMeasurement representing the unit connected to " + 
				"this measurable systems biology entity.\n" +
				"Examples: uome-list:Metre, uome-list:Kilogram, uome-list:MicrogramPerMillilitre");

		OntUtil.addTypedComment(schema, sbTerm, 
				"The systems biology term that this entity is an instance of.\n" +
		"Examples: an SBO term; an SBML element name.");

		OntUtil.addTypedComment(schema, sbSubEntity, 
				"A systems biology entity that is a part, attribute or subset of this " + 
				"systems biology entity.\n" +
		"Examples: rate law of an interaction; parameters of a rate law.");

		OntUtil.addTypedComment(schema, SBVocabulary, 
				"Controlled vocabulary that describes a systems biology concept, such as a " + 
				"Systems Biology Ontology term.\n" +
		"Examples: an SBO term; an SBML element name");

		OntUtil.addTypedComment(schema, SBEntity, 
				"An entity that can be characterized by one or more systems biology terms.\n" +
		"Examples: a BioPAX entity; a rate law or a parameter in a particular instance.");

		OntUtil.addTypedComment(schema, SBMeasurable, 
				"An entity that can be characterized by one or more systems biology terms and that " + 
				"can be represented by a number and a unit.\n" +
		"Example: a parameter in a particular instance.");

		OntUtil.addTypedComment(schema, SBState, 
				"An entity that describes the state of a system. This state can be described " + 
				"by sub elements, whcih makes it usefull to group data that correspond to the " + 
		"same state.");

		// OntUtil.addTypedComment(schema, , "");

	}

}
