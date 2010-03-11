package org.vcell.sybil.util.legoquery;

/*   LegoQuery  --- by Oliver Ruebenacker, UCHC --- March to November 2009
 *   A query build from elements
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.arq.InferenceDataset;
import org.vcell.sybil.models.sbbox.SBBox;

import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.syntax.Element;

public abstract class LegoQuery<V extends QueryVars> {

	protected V vars;
		
	public LegoQuery(V varsNew) { vars = varsNew; }

	public V vars() { return vars; }
	public abstract Element element();
	public Op op() { return Algebra.optimize(Algebra.compile(element()));}
	
	public ResultIter<V> resultIter(SBBox box, InferenceDataset dataset) {
		return new ResultIter<V>(box, vars(), Algebra.exec(op(), dataset.asDatasetGraph()));
	}
		
	public Set<QueryResult<V>> results(SBBox box) {
		Set<QueryResult<V>> results = new HashSet<QueryResult<V>>();
		ResultIter<V> resultIter = 
			resultIter(box, new InferenceDataset(box.getRdf(), box.data(), box.schema()));
		while(resultIter.hasNext()) { results.add(resultIter.next()); }
		return results;
	}
	
	
}
