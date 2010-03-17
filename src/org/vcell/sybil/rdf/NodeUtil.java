package org.vcell.sybil.rdf;

/*   NodeUtil  --- by Oliver Ruebenacker, UCHC --- July 2008 to January 2010
 *   Finding a label for a node, from certain properties or from local Name
 */

import java.util.Vector;

import org.vcell.sybil.rdf.schemas.BioPAX2;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

public class NodeUtil {

	// TODO switch to using SBWrapper instead
	
	public static String toString(RDFNode node) {
		if(node.isResource()) { return toString((Resource) node); }
		else { return toString((Literal) node); }
	}
	
	public static String toString(Resource resource) {
		if(resource.getModel() != null) {
			for(Property prop : defaultNameProps()) {
				StmtIterator stmtIter = resource.listProperties(prop);
				while(stmtIter.hasNext()) {
					Statement statement = stmtIter.nextStatement();
					RDFNode name = statement.getObject();
					if(name.isLiteral()) { return ((Literal) name).getLexicalForm(); }
				}
			}
			StmtIterator stmtIter = resource.listProperties(RDFS.label);
			while(stmtIter.hasNext()) {
				Statement statement = stmtIter.nextStatement();
				RDFNode name = statement.getObject();
				if(name.isLiteral()) { return ((Literal) name).getLexicalForm(); }
			}			
		}
		return resource.getLocalName();
	}
	
	public static String toString(Literal literal) {
		return literal.getLexicalForm();
	}
	
	public static final Vector<Property> nameProps = defaultNameProps();
	
	public static Vector<Property> defaultNameProps() {
		Vector<Property> props = new Vector<Property>();
		props.add(BioPAX2.SHORT_NAME);
		props.add(BioPAX2.NAME);
		props.add(BioPAX2.SYNONYMS);
		props.add(BioPAX2.TERM);
		props.add(BioPAX2.TITLE);
		props.add(BioPAX2.ID);
		props.add(RDFS.label);
		return props;
	}

	public static Resource fromString(String newURI) {
		Resource node = ResourceFactory.createResource(newURI);
		String nameSpace = node.getNameSpace();
		if(nameSpace.length() < 2) { 
			node = ResourceFactory.createResource("defaultNS/" + newURI);
		}
		return node;
	}
	
}
