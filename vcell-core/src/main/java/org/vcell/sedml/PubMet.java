package org.vcell.sedml;

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.schemas.util.OntUtil;

/*
 * Publication Info Metadata (in RDF)
 */
public class PubMet {
	
	public static final Graph schema = new HashGraph();
	
	public static final URI rdfURI = ValueFactoryImpl.getInstance().createURI(DefaultNameSpaces.RDF.uri);
	public static final URI rdfsURI = ValueFactoryImpl.getInstance().createURI(DefaultNameSpaces.RDFS.uri);
	public static final URI bqmodelURI = ValueFactoryImpl.getInstance().createURI(DefaultNameSpaces.BQMODEL.uri);
	public static final URI foafURI = ValueFactoryImpl.getInstance().createURI(DefaultNameSpaces.FOAF.uri);

	public static final URI Description = OntUtil.createDatatypeProperty(schema, DefaultNameSpaces.RDF.uri + "Description");
	public static final URI Creator = OntUtil.createObjectProperty(schema, DefaultNameSpaces.DUBLIN_CORE.uri + "creator");
	public static final URI Contributor = OntUtil.createObjectProperty(schema, DefaultNameSpaces.DUBLIN_CORE.uri + "contributor");
	public static final URI Title = OntUtil.createObjectProperty(schema, DefaultNameSpaces.DC_TERMS.uri + "title");
	public static final URI name = OntUtil.createObjectProperty(schema, DefaultNameSpaces.FOAF.uri + "name");
	public static final URI label = OntUtil.createObjectProperty(schema, DefaultNameSpaces.RDFS.uri + "label");

	public static final URI ContributorDescription = OntUtil.createObjectProperty(schema, DefaultNameSpaces.RDF.uri + "Description");

	
	public static final String CommentTaxon = "\n\n\t<!-- taxon -->";
	public static final String CommentOther = "\n\n\t<!-- other identifiers -->";
	public static final String CommentCreator = "\n\n\t<!-- creators -->";
	public static final String CommentContributor = "\n\n\t<!-- contributors (e.g., curators) -->";
	public static final String CommentCitations = "\n\n\t<!-- citations -->";
	public static final String CommentLicense = "\n\n\t<!-- license -->";
	public static final String CommentCreated = "\n\n\t<!-- created -->";
	public static final String CommentModified = "\n\n\t<!-- modified -->";
	
	public static final String EndDescription0 = "</rdf:Description>";

	
	public static final String StartDescription = "\n\t\t<rdf:Description>";
	public static final String EndDescription = "\n\t\t</rdf:Description>";
	public static final String StartName = "\n\t\t\t<foaf:name>";
	public static final String EndName = "</foaf:name>";
	public static final String StartLabel = "\n\t\t\t<rdfs:label>";
	public static final String EndLabel = "</rdfs:label>";
	public static final String StartIdentifier = "\n\t\t\t<dc:identifier rdf:resource=\"";
	public static final String EndIdentifier = "\"/>";
	public static final String ResourceIdentifier = "http://identifiers.org/";
	
	public static final String StartIs = "\n\t<bqmodel:is>";
	public static final String EndIs = "\n\t</bqmodel:is>";
	public static final String StartIsDescribedBy = "\n\t<bqmodel:isDescribedBy>";
	public static final String EndIsDescribedBy = "\n\t</bqmodel:isDescribedBy>";
	
	public static final String StartCreator = "\n\t<dc:creator>";
	public static final String EndCreator = "\n\t</dc:creator>";
	public static final String StartContributor = "\n\t<dc:contributor>";
	public static final String EndContributor = "\n\t</dc:contributor>";

	public static final String StartLicense = "\n\t<dc:license>";
	public static final String EndLicense = "\n\t</dc:license>";
	public static final String StartCreated = "\n\t<dc:created>";
	public static final String EndCreated = "\n\t</dc:created>";
	public static final String PrefixCreated = "\n\t\t\t<dc:W3CDTF>";
	public static final String SuffixCreated = "</dc:W3CDTF>";
	public static final String StartModified = "\n\t<dc:modified>";
	public static final String EndModified = "\n\t</dc:modified>";
	public static final String PrefixModified = "\n\t\t\t<dc:W3CDTF>";
	public static final String SuffixModified = "</dc:W3CDTF>";

	
	public static final String EndRdf = "</rdf:RDF>";
	
}
