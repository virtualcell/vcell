package org.vcell.sybil.util.legoquery;

/*   QueryResult  --- by Oliver Ruebenacker, UCHC --- March 2009 to November 2009
 *   Solution of a query. Every property var is a resource var. Every resource var is a node var
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.models.sbbox.imp.SBWrapper;
import org.vcell.sybil.util.legoquery.QueryVars.ObjectKey;
import org.vcell.sybil.util.legoquery.QueryVars.PredicateKey;
import org.vcell.sybil.util.legoquery.QueryVars.SubjectKey;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class QueryResult<V extends QueryVars> {

	protected SBBox box;
	protected V vars;
	protected Binding binding;
	
	public QueryResult(SBBox box, V varsNew, Binding bindingNew) {
		this.box = box;
		vars = varsNew;
		binding = bindingNew;
	}
	
	public V vars() { return vars; }
	public NamedThing thing(SubjectKey key) { return new SBWrapper(box, resource(key)); }
	public RDFNode node(ObjectKey key) { return node(vars.nodeMap.get(key)); }
	public Resource resource(SubjectKey key) { return resource(vars.resourceMap.get(key)); }
	public Property property(PredicateKey key) { return property(vars.propertyMap.get(key)); }
	public RDFNode node(RDFNodeVar var) { return var.node(binding); }
	public Resource resource(ResourceVar var) { return var.node(binding); }
	public Property property(PropertyVar var) { return var.node(binding); }
	
}
