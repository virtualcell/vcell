package org.vcell.sybil.rdf.compare;

/*   NodeComparatorByClass  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A comparator for RDF nodes by type (URI node, anon node, literal)
 */

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.vcell.sybil.util.comparator.ComparatorScore;

public class NodeComparatorByType extends ComparatorScore<Value> {
	@Override
	public int score(Value node) {
		if(node instanceof URI) { return 2; }
		else if(node instanceof Resource) { return 1; }
		else { return 0; }
	}
}