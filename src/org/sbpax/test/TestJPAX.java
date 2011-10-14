package org.sbpax.test;

import java.util.Map;

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.BioPAX3;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.util.SesameRioUtil;

public class TestJPAX {
	
	public static void main(String[] args) {
		Graph graph = new HashGraph();
		String ns = DefaultNameSpaces.EX.uri;
		URI r1 = graph.getValueFactory().createURI(ns + "r1");
		URI pe1 = graph.getValueFactory().createURI(ns + "pe1");
		URI pe2 = graph.getValueFactory().createURI(ns + "pe2");
		graph.add(r1, RDF.TYPE, BioPAX3.BiochemicalReaction);
		graph.add(pe1, RDF.TYPE, BioPAX3.Protein);
		graph.add(pe2, RDF.TYPE, BioPAX3.Protein);
		graph.add(r1, BioPAX3.left, pe1);
		graph.add(r1, BioPAX3.right, pe2);
		try {
			Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
			SesameRioUtil.writeRDFToStream(System.out, graph, nsMap, RDFFormat.RDFXML);
		} catch (RDFHandlerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
