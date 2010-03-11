package org.vcell.sybil.util.legoquery;

/*   QueryVars  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Variables for query. Every property var is a resource var. Every resource var is a node var
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.sparql.core.Var;

public class QueryVars {

	public static class ObjectKey {
		protected String name;
		public ObjectKey(String nameNew) { name = nameNew; }
		String name() { return name; }
	};
	
	public static class SubjectKey extends ObjectKey { public SubjectKey(String nameNew) { super(nameNew); }; }
	public static class PredicateKey extends SubjectKey { public PredicateKey(String nameNew) { super(nameNew); }; };
	
	protected Map<ObjectKey, RDFNodeVar> nodeMap = new HashMap<ObjectKey, RDFNodeVar>();
	protected Map<SubjectKey, ResourceVar> resourceMap = new HashMap<SubjectKey, ResourceVar>();
	protected Map<PredicateKey, PropertyVar> propertyMap = new HashMap<PredicateKey, PropertyVar>();
	
	public QueryVars(Collection<? extends ObjectKey> keysNew) {
		for(ObjectKey key : keysNew) {
			if(key instanceof PredicateKey) { setPropertyVar((PredicateKey) key); }
			else if(key instanceof SubjectKey) { setResourceVar((SubjectKey) key); }
			else { setRDFNodeVar(key); }
		}
	}
	
	public RDFNodeVar setRDFNodeVar(ObjectKey key) {
		RDFNodeVar var = new RDFNodeVar(key.name());
		addToMaps(key, var);
		return var;
	}
	
	public RDFNodeVar setRDFNodeVar(ObjectKey key, Var coreVar) {
		RDFNodeVar var = new RDFNodeVar(coreVar);
		addToMaps(key, var);
		return var;
	}
	
	public RDFNodeVar setRDFNodeVar(ObjectKey key, RDFNodeVar var) {
		addToMaps(key, var);
		return var;
	}
	
	public ResourceVar setResourceVar(SubjectKey key) {
		ResourceVar var = new ResourceVar(key.name());
		addToMaps(key, var);
		return var;
	}
	
	public ResourceVar setResourceVar(SubjectKey key, Var coreVar) {
		ResourceVar var = new ResourceVar(coreVar);
		addToMaps(key, var);
		return var;
	}
	
	public ResourceVar setResourceVar(SubjectKey key, ResourceVar var) {
		addToMaps(key, var);
		return var;
	}
	
	public PropertyVar setPropertyVar(PredicateKey key) {
		PropertyVar var = new PropertyVar(key.name());
		addToMaps(key, var);
		return var;
	}
	
	public PropertyVar setPropertyVar(PredicateKey key, Var coreVar) {
		PropertyVar var = new PropertyVar(coreVar);
		addToMaps(key, var);
		return var;
	}
	
	public PropertyVar setPropertyVar(PredicateKey key, PropertyVar var) {
		addToMaps(key, var);
		return var;
	}
	
	protected void addToMaps(ObjectKey key, RDFNodeVar var) {
		nodeMap.put(key, var);
		if(key instanceof SubjectKey && var instanceof ResourceVar) {
			resourceMap.put((SubjectKey) key, (ResourceVar) var);
			if(key instanceof PredicateKey && var instanceof PropertyVar) {
				propertyMap.put((PredicateKey) key, (PropertyVar) var);
			}
		}
	}
	
	public RDFNodeVar var(ObjectKey key) { return nodeMap.get(key); }
	public ResourceVar var(SubjectKey key) { return resourceMap.get(key); }
	public PropertyVar var(PredicateKey key) { return propertyMap.get(key); }
	
	public Var coreVar(ObjectKey key) { return nodeMap.get(key).var(); }
	public Var coreVar(SubjectKey key) { return resourceMap.get(key).var(); }
	public Var coreVar(PredicateKey key) { return propertyMap.get(key).var(); }
	
	public Map<ObjectKey, RDFNodeVar> nodeMap() { return nodeMap; }
	public Map<SubjectKey, ResourceVar> resourceMap() { return resourceMap; }
	public Map<PredicateKey, PropertyVar> propertyMap() { return propertyMap; }
	
}
