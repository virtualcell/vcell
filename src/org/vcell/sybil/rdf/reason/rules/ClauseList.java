package org.vcell.sybil.rdf.reason.rules;

/*   ClauseList  --- by Oliver Ruebenacker, UCHC --- May 2008
 *   A convenience class for providing a List of ClauseEntries for RuleSpec
 */

import java.util.List;
import java.util.Vector;

import org.vcell.sybil.util.lists.ListOfNone;
import org.vcell.sybil.util.lists.ListOfOne;
import org.vcell.sybil.util.lists.ListOfThree;
import org.vcell.sybil.util.lists.ListOfTwo;

import com.hp.hpl.jena.graph.FrontsNode;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.reasoner.TriplePattern;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.reasoner.rulesys.ClauseEntry;
import com.hp.hpl.jena.reasoner.rulesys.Functor;

public class ClauseList extends Vector<ClauseEntry> {

	private static final long serialVersionUID = 4118014291279340366L;

	public static ClauseList create() { return new ClauseList(); }
	
	public ClauseList add(Node subject, Node predicate, Node object) {
		add(new TriplePattern(subject, predicate, object));
		return this;
	}

	public ClauseList add(FrontsNode subject, Node predicate, Node object) {
		return add(subject.asNode(), predicate, object);
	}

	public ClauseList add(Node subject, FrontsNode predicate, Node object) {
		return add(subject, predicate.asNode(), object);
	}

	public ClauseList add(FrontsNode subject, FrontsNode predicate, Node object) {
		return add(subject.asNode(), predicate.asNode(), object);
	}

	public ClauseList add(Node subject, Node predicate, FrontsNode object) {
		return add(subject, predicate, object.asNode());
	}

	public ClauseList add(FrontsNode subject, Node predicate, FrontsNode object) {
		return add(subject.asNode(), predicate, object.asNode());
	}

	public ClauseList add(Node subject, FrontsNode predicate, FrontsNode object) {
		return add(subject, predicate.asNode(), object.asNode());
	}

	public ClauseList add(FrontsNode subject, FrontsNode predicate, FrontsNode object) {
		return add(subject.asNode(), predicate.asNode(), object.asNode());
	}
	
	public ClauseList add(Builtin builtin, List<Node> nodes) {
		add(new Functor(builtin.getName(), nodes));
		return this;
	}
	
	public ClauseList add(Builtin builtin) { return add(builtin, new ListOfNone<Node>()); }
	
	public ClauseList add(Builtin builtin, Node n1) { 
		return add(builtin, new ListOfOne<Node>(n1)); 
	}
	
	public ClauseList add(Builtin builtin, Node n1, Node n2) { 
		return add(builtin, new ListOfTwo<Node>(n1, n2)); 
	}
	
	public ClauseList add(Builtin builtin, Node n1, Node n2, Node n3) { 
		return add(builtin, new ListOfThree<Node>(n1, n2, n3)); 
	}
	
	
		
}
