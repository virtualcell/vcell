package org.vcell.sybil.rdf;

/*   NameSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to May 2009
 *   A number of popular name spaces and their usual shortcuts
 */

public class NameSpace {

	public final String prefix;
	public final String uri;
	
	public NameSpace(String prefix, String uri) { 
		this.prefix = prefix; this.uri = uri; 
	}
	
	public String prefixLine() { 
		return "PREFIX " + prefix + ": <" + uri + ">\n"; 
	}

	public String toString() { return uri; }

	public static NSMap defaultMap = new NSMap();
	
	public static final NameSpace RDF = 
		defaultMap.register("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	public static final NameSpace RDFS = 
		defaultMap.register("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	public static final NameSpace OWL = 
		defaultMap.register("owl", "http://www.w3.org/2002/07/owl#");
	public static final NameSpace XMLS = 
		defaultMap.register("xmls", "http://www.w3.org/2001/XMLSchema#");
	public static final NameSpace XSD = 
		defaultMap.register("xsd", "http://www.w3.org/2001/XMLSchema#");
	public static final NameSpace DUBLIN_CORE = 
		defaultMap.register("dc", "http://purl.org/dc/elements/1.1/");
	public static final NameSpace DC_TERMS = 
		defaultMap.register("dcterms", "http://purl.org/dc/terms/");
	public static final NameSpace FOAF = 
		defaultMap.register("foaf", "http://xmlns.com/foaf/0.1/");
	public static final NameSpace VCARD = 
		defaultMap.register("vcard", "http://www.w3.org/2001/vcard-rdf/3.0#");
	public static final NameSpace BQBIOL = 
		defaultMap.register("bqbiol", "http://biomodels.net/biology-qualifiers/");
	public static final NameSpace BQMODEL = 
		defaultMap.register("bqmodel", "http://biomodels.net/model-qualifiers/");

}
