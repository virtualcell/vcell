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
	public static final URI Title = OntUtil.createObjectProperty(schema, DefaultNameSpaces.DUBLIN_CORE.uri + "title");
	public static final URI name = OntUtil.createObjectProperty(schema, DefaultNameSpaces.FOAF.uri + "name");
	public static final URI label = OntUtil.createObjectProperty(schema, DefaultNameSpaces.RDFS.uri + "label");

	public static final URI ContributorDescription = OntUtil.createObjectProperty(schema, DefaultNameSpaces.RDF.uri + "Description");

	
}
