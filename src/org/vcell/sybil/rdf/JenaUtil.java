package org.vcell.sybil.rdf;

/*   JenaUtil  --- by Oliver Ruebenacker, UCHC --- June 2008
 *   Methods useful with Jena
 */

import java.util.Set;

import org.vcell.sybil.util.sets.SetOfThree;
import org.vcell.sybil.util.sets.SetOfTwo;

import com.hp.hpl.jena.graph.FrontsNode;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class JenaUtil {
	
	public static Triple triple(Node node1, Node node2, Node node3) {
		return new Triple(node1, node2, node3);
	}
	
	public static Triple triple(Node node1, Node node2, FrontsNode front3) {
		return new Triple(node1, node2, front3.asNode());
	}
	
	public static Triple triple(Node node1, FrontsNode front2, Node node3) {
		return new Triple(node1, front2.asNode(), node3);
	}
	
	public static Triple triple(Node node1, FrontsNode front2, FrontsNode front3) {
		return new Triple(node1, front2.asNode(), front3.asNode());
	}
	
	public static Triple triple(FrontsNode front1, Node node2, Node node3) {
		return new Triple(front1.asNode(), node2, node3);
	}
	
	public static Triple triple(FrontsNode front1, Node node2, FrontsNode front3) {
		return new Triple(front1.asNode(), node2, front3.asNode());
	}
	
	public static Triple triple(FrontsNode front1, FrontsNode front2, Node node3) {
		return new Triple(front1.asNode(), front2.asNode(), node3);
	}
	
	public static Triple triple(FrontsNode front1, FrontsNode front2, FrontsNode front3) {
		return new Triple(front1.asNode(), front2.asNode(), front3.asNode());
	}
	
	public static Set<Resource> resourcesFromStatement(Statement statement) {
		RDFNode object = statement.getObject();
		if(object instanceof Resource) { 
			return new SetOfThree<Resource>(statement.getSubject(), statement.getPredicate(), 
					(Resource) object);
		} else {
			return new SetOfTwo<Resource>(statement.getSubject(), statement.getPredicate());
		}
		
	}
	
}
