package org.vcell.sybil.util.legoquery.chains;

/*   ChainOneQuery  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   A query of a one step chain
 */

import java.util.Arrays;

import org.vcell.sybil.rdf.JenaUtil;
import org.vcell.sybil.rdf.schemas.DatasetOntology;
import org.vcell.sybil.util.legoquery.LegoQuery;
import org.vcell.sybil.util.legoquery.QueryVars;
import org.vcell.sybil.util.legoquery.QueryVars.PredicateKey;
import org.vcell.sybil.util.legoquery.QueryVars.SubjectKey;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ChainOneQuery extends LegoQuery<ChainOneQuery.Vars> {

	public static final SubjectKey Node1 = new SubjectKey("node1");
	public static final PredicateKey Relation = new PredicateKey("relation1");
	public static final SubjectKey Node2 = new SubjectKey("node2");
	
	public static class Vars extends QueryVars {
		public Vars() { super(Arrays.asList(Node1, Relation, Node2)); }
	}
	
	protected Resource type1, type2;
	protected Property superRelation;
	
	public ChainOneQuery(Resource category1New, Property predicateNew, Resource category2New) {
		super(new Vars());
		type1 = category1New;
		superRelation = predicateNew;
		type2 = category2New;
	}
	
	public ElementGroup element() {
		ElementGroup group = new ElementGroup();
		ElementTriplesBlock block = new ElementTriplesBlock();
		block.addTriple(JenaUtil.triple(vars.coreVar(Node1), RDF.type, type1));
		block.addTriple(JenaUtil.triple(vars.coreVar(Node2), RDF.type, type2));
		block.addTriple(JenaUtil.triple(vars.coreVar(Relation), RDFS.subPropertyOf, superRelation));
		group.addElement(block);
		ElementTriplesBlock blockNamed = new ElementTriplesBlock();
		blockNamed.addTriple(JenaUtil.triple(vars.coreVar(Node1), vars.coreVar(Relation), vars.coreVar(Node2)));
		ElementNamedGraph named = new ElementNamedGraph(DatasetOntology.Data.asNode(), blockNamed);
		group.addElement(named);
		return group;
	}
	
	public boolean equals(Object object) {
		if(object instanceof ChainOneQuery) {
			ChainOneQuery query = (ChainOneQuery) object;
			return type1.equals(query.type1) && superRelation.equals(query.superRelation) 
			&& type2.equals(query.type2);
		}
		return false;
	}
	
	public int hashCode() { return type1.hashCode() + superRelation.hashCode() + type2.hashCode(); }
	
}
