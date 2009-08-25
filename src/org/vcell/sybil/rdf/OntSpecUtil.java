package org.vcell.sybil.rdf;

/*   OntSpecUtil  --- by Oliver Ruebenacker, UCHC --- May to September 2008
 *   Methods for supporting the creation of specifications for ontologies
 */

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.rdf.JenaIOUtil.Style;

import cbit.util.xml.XmlUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OntSpecUtil {

	public static void writeToFile(Model schema, String fileName, Style style) {
		System.out.println("Writing SBPAX to " + fileName);
		try { 
			java.io.StringWriter strWriter = new StringWriter();
			JenaIOUtil.writeModel(schema, strWriter, style);
			XmlUtil.writeXMLStringToFile(strWriter.getBuffer().toString(), fileName, true);
		} catch (IOException e) { 
			e.printStackTrace(System.out); 
		}
		System.out.println("Done");
	}
	
	public static void writeToFiles(Model schema) {
		String baseFileName = "sbpax";
		writeToFile(schema, baseFileName + ".owl", JenaIOUtil.RDF_XML_ABBREV);
		writeToFile(schema, baseFileName + ".owl2", JenaIOUtil.RDF_XML);
		writeToFile(schema, baseFileName + ".rdf", JenaIOUtil.N_TRIPLE);
		writeToFile(schema, baseFileName + ".n3", JenaIOUtil.N3);
	}

	public static boolean isMissingComment(RDFNode node) {
		return node.isURIResource() && !((Resource) node).hasProperty(RDFS.comment);
	}
	
	public static Set<Resource> findMissingComment(Model schema) {
		Set<Resource> resources = new HashSet<Resource>();
		StmtIterator stmtIter = schema.listStatements();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			Resource subject = statement.getSubject();
			if(isMissingComment(subject)) { resources.add(subject); }
			Property predicate = statement.getPredicate();
			if(isMissingComment(predicate)) { resources.add(predicate); };
			RDFNode object = statement.getObject();
			if(isMissingComment(object)) { resources.add((Resource) object); }
		}
		return resources;
	}
	
	public static void listMissingComments(Model schema) {
		Set<Resource> resources = findMissingComment(schema);
		for(Resource resource : resources) {
			System.out.println("Comment missing for: " + resource);
		}
	}
	
}
