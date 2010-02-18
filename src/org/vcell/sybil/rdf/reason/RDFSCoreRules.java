package org.vcell.sybil.rdf.reason;

/*   SYBREAMRules  --- by Oliver Ruebenacker, UCHC --- April to May 2008
 *   Rules for System Biological Reasoning Engine for Analysis and Modeling
 */

import java.util.List;
import java.util.Vector;

import org.vcell.sybil.rdf.reason.rules.RuleSpec;

import com.hp.hpl.jena.reasoner.rulesys.Node_RuleVariable;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class RDFSCoreRules {

	static public List<Rule> RULES = new Vector<Rule>();
	
	static public Rule RULE_TRANSITIVE;
	
	static {
		RuleSpec rs = new RuleSpec("RULE_TRANSITIVE");
		Node_RuleVariable xVar = rs.var("x");
		Node_RuleVariable yVar = rs.var("y");
		Node_RuleVariable zVar = rs.var("z");
		Node_RuleVariable pVar = rs.var("p");		
		rs.body().add(xVar, pVar, yVar);
		rs.body().add(yVar, pVar, zVar);
		rs.body().add(pVar, RDFS.subPropertyOf, OWL.TransitiveProperty);
		rs.head().add(xVar, pVar, zVar);
		RULE_TRANSITIVE = rs.createRule();
	}

	static public Rule RULE_SUBCLASS_DEFINITION;

	static {
		RuleSpec rs = new RuleSpec("RULE_SUBCLASS_DEFINITION");
		rs.head().add(RDFS.subClassOf, RDFS.subPropertyOf, OWL.TransitiveProperty);
		rs.head().add(RDFS.subClassOf, RDFS.domain, RDFS.Class);
		rs.head().add(RDFS.subClassOf, RDFS.range, RDFS.Class);
		RULE_SUBCLASS_DEFINITION = rs.createRule();
	}

	static public Rule RULE_SUBPROPERTY_DEFINITION;

	static {
		RuleSpec rs = new RuleSpec("RULE_SUBPROPERTY_DEFINITION");
		rs.head().add(RDFS.subPropertyOf, RDFS.subPropertyOf, OWL.TransitiveProperty);
		rs.head().add(RDFS.subPropertyOf, RDFS.domain, RDF.Property);
		rs.head().add(RDFS.subPropertyOf, RDFS.range, RDF.Property);
		RULE_SUBPROPERTY_DEFINITION = rs.createRule();
	}

	static public Rule RULE_RDFS2;
	
	static {
		RuleSpec rs = new RuleSpec("RDFS2");
		Node_RuleVariable xVar = rs.var("x");
		Node_RuleVariable pVar = rs.var("p");
		Node_RuleVariable yVar = rs.var("y");
		Node_RuleVariable cVar = rs.var("c");		
		rs.body().add(xVar, pVar, yVar);
		rs.body().add(pVar, RDFS.domain, cVar);
		rs.head().add(xVar, RDF.type, cVar);
		RULE_RDFS2 = rs.createRule();
	}

	static public Rule RULE_RDFS3;
	
	static {
		RuleSpec rs = new RuleSpec("RDFS3");
		Node_RuleVariable xVar = rs.var("x");
		Node_RuleVariable pVar = rs.var("p");
		Node_RuleVariable yVar = rs.var("y");
		Node_RuleVariable cVar = rs.var("c");		
		rs.body().add(xVar, pVar, yVar);
		rs.body().add(pVar, RDFS.range, cVar);
		rs.head().add(yVar, RDF.type, cVar);
		RULE_RDFS3 = rs.createRule();
	}

	static public Rule RULE_RDFS6;
	
	static {
		RuleSpec rs = new RuleSpec("RDFS6");
		Node_RuleVariable aVar = rs.var("a");
		Node_RuleVariable bVar = rs.var("b");
		Node_RuleVariable pVar = rs.var("p");
		Node_RuleVariable qVar = rs.var("q");		
		rs.body().add(aVar, pVar, bVar);
		rs.body().add(pVar, RDFS.subPropertyOf, qVar);
		rs.head().add(aVar, qVar, bVar);
		RULE_RDFS6 = rs.createRule();
	}

	static public Rule RULE_RDFS9;
	
	static {
		RuleSpec rs = new RuleSpec("RDFS9");
		Node_RuleVariable aVar = rs.var("a");
		Node_RuleVariable xVar = rs.var("x");
		Node_RuleVariable yVar = rs.var("y");
		rs.body().add(aVar, RDF.type, xVar);
		rs.body().add(xVar, RDFS.subClassOf, yVar);
		rs.head().add(aVar, RDF.type, yVar);
		RULE_RDFS9 = rs.createRule();
	}
	
	static {
		RULES.add(RULE_TRANSITIVE);
		RULES.add(RULE_SUBCLASS_DEFINITION);
		RULES.add(RULE_SUBPROPERTY_DEFINITION);
		RULES.add(RULE_RDFS2);
		RULES.add(RULE_RDFS3);
		RULES.add(RULE_RDFS6);
		RULES.add(RULE_RDFS9);
	}
	
	
}
