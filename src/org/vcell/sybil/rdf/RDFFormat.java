package org.vcell.sybil.rdf;

/*   RDFFormat  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   Available formats for Jena model input and output
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.util.keys.KeyOfOne;

public class RDFFormat extends KeyOfOne<String>{

	public RDFFormat(String name) { super(name); }
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