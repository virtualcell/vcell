/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf;

/*   RDFFormat  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   Available formats for Jena model input and output
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.util.keys.KeyOfOne;

public class RDFFormat extends KeyOfOne<String>{

	public RDFFormat(String name) { super(name); }
	@Override
	public String toString() { return a(); }

	public static final Set<RDFFormat> all = new HashSet<RDFFormat>();
	
	static protected RDFFormat newRDFFormat(String name) { 
		RDFFormat format = new RDFFormat(name);
		all.add(format);
		return format;
	}
	
	public static final RDFFormat RDF_XML = new RDFFormat("RDF/XML");
	public static final RDFFormat RDF_XML_ABBREV = new RDFFormat("RDF/XML-ABBREV");
	public static final RDFFormat N_TRIPLE = new RDFFormat("N-TRIPLE");
	public static final RDFFormat N3 = new RDFFormat("N3");

}
