package org.vcell.sybil.models.ontology;

/*   Evaluator  --- by Oliver Ruebenacker, UCHC --- January 2008 to October 2009
 *   Stores and evaluates information on reactions, participants and physical entities
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.util.legoquery.QueryResult;
import org.vcell.sybil.util.legoquery.chains.ChainOneQuery;
import org.vcell.sybil.util.legoquery.chains.ChainTwoQuery;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Evaluator {

	public static class Event {
		
		protected Evaluator evaluator;
		public Event(Evaluator evaluator) { this.evaluator = evaluator; }
		public Evaluator evaluator() { return evaluator; }
		
		public static interface Listener {
			public void evaluatorEvent(Event event);
			
			public static class FireSet extends HashSet<Listener> implements Listener {
				private static final long serialVersionUID = -2959718724487712404L;

				public void evaluatorEvent(Event event) {
					for(Listener listener : this) { listener.evaluatorEvent(event); }
				}
			}
		}
				
	}
	
	protected SBWorkView view;
	protected Event.Listener.FireSet listeners = new Event.Listener.FireSet();

	protected Map<ChainOneKey, Set<QueryResult<ChainOneQuery.Vars>>> chainOneResults = 
		new HashMap<ChainOneKey, Set<QueryResult<ChainOneQuery.Vars>>>();

	protected Map<ChainTwoKey, Set<QueryResult<ChainTwoQuery.Vars>>> chainTwoResults = 
		new HashMap<ChainTwoKey, Set<QueryResult<ChainTwoQuery.Vars>>>();

	public Evaluator(SBWorkView view) { 
		this.createDataSet(view);
	}
	
	public Event createDataSet(SBWorkView view) {
	    this.view = view;
	    view.box().performSYBREAMReasoning();
		chainOneResults.clear();
		chainTwoResults.clear();
		return new Event(this);
	}
	
	public SBWorkView view() { return view; }
	public Event.Listener.FireSet listeners() { return listeners; }
	
	public Set<QueryResult<ChainOneQuery.Vars>> 
	chainOneResults(Resource cat1, Property predicate, Resource cat2) {
		return chainOneResults(new ChainOneKey(cat1, predicate, cat2));
	}
	
	public Set<QueryResult<ChainOneQuery.Vars>> chainOneResults(ChainOneKey key) {
		Set<QueryResult<ChainOneQuery.Vars>> results = chainOneResults.get(key);
		if(results == null) { 
			ChainOneQuery query = new ChainOneQuery(key.type1(), key.relation(), key.type2());
			results = query.results(view.box()); 
			chainOneResults.put(key, results);
		}
		return results;
	}
	
	public Set<QueryResult<ChainTwoQuery.Vars>> 
	chainTwoResults(Resource cat1, Property pred1, Resource cat2, Property pred2, Resource cat3) {
		return chainTwoResults(new ChainTwoKey(cat1, pred1, cat2, pred2, cat3));
	}
	
	public Set<QueryResult<ChainTwoQuery.Vars>> 
	chainTwoResults(ChainTwoKey key) {
		Set<QueryResult<ChainTwoQuery.Vars>> results = chainTwoResults.get(key);
		if(results == null) { 
			ChainTwoQuery query = new ChainTwoQuery(key.type1(), key.relation1(), key.type2(), 
					key.relation2(), key.type3());
			results = query.results(view.box()); 
			chainTwoResults.put(key, results);
		}
		return results;
	}
	
}
