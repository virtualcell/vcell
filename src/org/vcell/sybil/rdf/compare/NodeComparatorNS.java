package org.vcell.sybil.rdf.compare;

/*   NodeComparatorNS  --- by Oliver Ruebenacker, UCHC --- July 2009
 *   A comparator for RDF nodes by type and preferring a default name space
 */

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.vcell.sybil.util.comparator.ComparatorScore;

public class NodeComparatorNS extends ComparatorScore<Value> {
	
	protected String namespace;
	
	public NodeComparatorNS(String namespace) { this.namespace = namespace; }
	
	@Override
	public int score(Value node) {
		int score = 0;
		if(node instanceof Resource) {
			Resource resource = (Resource) node;
			if(resource instanceof URI) {
				if(namespace.equals(((URI) resource).getNamespace())) { score = 3; } 
				else { score = 2; }
			} else {
				score = 1;
			}
		}
		return score;
	}
}