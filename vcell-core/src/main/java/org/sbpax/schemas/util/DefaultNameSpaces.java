/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas.util;

/*   NameSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to May 2009
 *   A number of popular name spaces and their usual shortcuts
 */

import org.sbpax.schemas.BioPAX3;
import org.sbpax.schemas.SBPAX3;
import org.vcell.pathway.sbo.SBOTerm;

public class DefaultNameSpaces {

	public static NSMap defaultMap = new NSMap();
	
	public static final NameSpace RDF = defaultMap.register("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	public static final NameSpace RDFS = defaultMap.register("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	public static final NameSpace OWL = defaultMap.register("owl", "http://www.w3.org/2002/07/owl#");
	public static final NameSpace XMLS = defaultMap.register("xmls", "http://www.w3.org/2001/XMLSchema#");
	public static final NameSpace XSD = defaultMap.register("xsd", "http://www.w3.org/2001/XMLSchema#");
	public static final NameSpace DUBLIN_CORE = defaultMap.register("dc", "http://purl.org/dc/elements/1.1/");
	public static final NameSpace DC_TERMS = defaultMap.register("dcterms", "http://purl.org/dc/terms/");	
	public static final NameSpace FOAF = defaultMap.register("foaf", "http://xmlns.com/foaf/0.1/");
	public static final NameSpace VCARD = defaultMap.register("vcard", "http://www.w3.org/2001/vcard-rdf/3.0#");
	public static final NameSpace BQBIOL = defaultMap.register("bqbiol", "http://biomodels.net/biology-qualifiers/");
	public static final NameSpace BQMODEL = defaultMap.register("bqmodel", "http://biomodels.net/model-qualifiers/");
	public static final NameSpace BP3 = defaultMap.register("bp3", BioPAX3.ns.uri);
	public static final NameSpace SBX3 = defaultMap.register("sbx3", SBPAX3.ns.uri);
	public static final NameSpace UOME_CORE = defaultMap.register("uome-core", "http://www.sbpax.org/uome/core.owl#");
	public static final NameSpace UOME_LIST = defaultMap.register("uome-list", "http://www.sbpax.org/uome/list.owl#");
	public static final NameSpace SBO = defaultMap.register("sbo", SBOTerm.SBO_BASE_URI);
	public static final NameSpace EX = defaultMap.register("ex", "http://example.org/");

}
