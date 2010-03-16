package org.vcell.sybil.util.legoquery.chains;

/*   ChainTwoQuery  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   A query of a one step chain
 */

import java.util.Arrays;

import org.vcell.sybil.rdf.JenaUtil;
import org.vcell.sybil.util.legoquery.LegoQuery;
import org.vcell.sybil.util.legoquery.QueryVars;
import org.vcell.sybil.util.legoquery.QueryVars.SubjectKey;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.vocabulary.RDF;

public class ChainTwoQuery extends LegoQuery<ChainTwoQuery.Vars> {

	public static final SubjectKey Node1 = new SubjectKey("node1");
	public static final SubjectKey Node2 = new SubjectKey("node2");
	public static final SubjectKey Node3 = new SubjectKey("node3");
	
	public static class Vars extends QueryVars {
		public Vars() { super(Arrays.asList(Node1, Node2, Node3)); }
	}
	
	protected Resource type1, type2, type3;
	protected Property superRelation1, superRelation2;
	
	public ChainTwoQuery(Resource type1New, Property superRelation1New, Resource type2New, 
			Property superRelation2New, Resource type3New) {
		super(new Vars());
		type1 = type1New; superRelation1 = superRelation1New; type2 = type2New; 
		superRelation2 = superRelation2New; type3 = type3New;
	}
	
	public ElementTriplesBlock element() {
		ElementTriplesBlock block = new ElementTriplesBlock();
		block.addTriple(JenaUtil.triple(vars.coreVar(Node1), RDF.type, type1));
		block.addTriple(JenaUtil.triple(vars.coreVar(Node1), superRelation1, vars.coreVar(Node2)));
		block.addTriple(JenaUtil.triple(vars.coreVar(Node2), RDF.type, type2));
		block.addTriple(JenaUtil.triple(vars.coreVar(Node2), superRelation2, vars.coreVar(Node3)));
		block.addTriple(JenaUtil.triple(vars.coreVar(Node3), RDF.type, type3));
		return block;
	}
	
	public boolean equals(Object object) {
		if(object instanceof ChainTwoQuery) {
			ChainTwoQuery query = (ChainTwoQuery) object;
			return type1.equals(query.type1) && superRelation1.equals(query.superRelation1) 
			&& type2.equals(query.type2) && superRelation2.equals(query.superRelation2) 
			&& type3.equals(query.type3);
		}
		return false;
	}
	
	public int hashCode() { 
		return type1.hashCode() + superRelation1.hashCode() + type2.hashCode() + superRelation2.hashCode() + type3.hashCode(); 
	}
		
}
