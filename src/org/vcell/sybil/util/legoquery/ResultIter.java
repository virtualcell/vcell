package org.vcell.sybil.util.legoquery;

/*   ResultIter  --- by Oliver Ruebenacker, UCHC --- March 2009 to November 2009
 *   Iterator over solutions of a query
 */

import java.util.Iterator;

import org.vcell.sybil.models.sbbox.SBBox;

import com.hp.hpl.jena.sparql.engine.QueryIterator;

public class ResultIter<V extends QueryVars> implements Iterator<QueryResult<V>> {

	protected SBBox box;
	protected V vars;
	protected QueryIterator qIter;

	public ResultIter(SBBox box, V varsNew, QueryIterator qIterNew) {
		this.box = box;
		vars = varsNew;
		qIter = qIterNew;
	}
	
	public V vars() { return vars; }

	public boolean hasNext() { return qIter.hasNext(); }

	public QueryResult<V> next() { return new QueryResult<V>(box, vars, qIter.nextBinding()); }

	public void remove() { qIter.remove(); }
	
}
