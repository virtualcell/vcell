package org.vcell.sybil.rdf.reason;

/*   SYBREAMCore  --- by Oliver Ruebenacker, UCHC --- April 2008 to June 2009
 *   Rules for System Biological Reasoning Engine for Analysis and Modeling
 */

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.vcell.sybil.rdf.reason.builtin.MakeAndKeep;
import org.vcell.sybil.rdf.reason.rules.RuleSpec;
import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.rulesys.Node_RuleVariable;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.reasoner.rulesys.builtins.MakeTemp;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SYBREAMRules {

	static public List<Rule> RULES = new Vector<Rule>();
	
	static public Rule RULE_BIOPAX2_CATALYST;
	
	static {
		RuleSpec rs = new RuleSpec("RULE_BIOPAX2_CATALYST");
		Node_RuleVariable cVar = rs.var("c");
		Node_RuleVariable eVar = rs.var("e");
		Node_RuleVariable pVar = rs.var("p");
		rs.body().add(cVar, RDF.type, BioPAX2.catalysis);
		rs.body().add(pVar, RDF.type, BioPAX2.conversion);
		rs.body().add(eVar, RDF.type, BioPAX2.physicalEntityParticipant);
		rs.body().add(cVar, BioPAX2.CONTROLLED, pVar);
		rs.body().add(cVar, BioPAX2.CONTROLLER, eVar);
		rs.head().add(pVar, RDF.type, SBPAX.Process);
		rs.head().add(eVar, RDF.type, SBPAX.ProcessParticipantCatalyst);
		rs.head().add(pVar, SBPAX.hasParticipantCatalyst, eVar);
		RULE_BIOPAX2_CATALYST = rs.createRule();
	}
		
	static public Rule RULE_UNITED_STUFF_CLASS;
	
	static {
		RuleSpec rs = new RuleSpec("RULE_UNITED_STUFF_CLASS");
		Node_RuleVariable scuVar = rs.var("scu");
		Node_RuleVariable scVar = rs.var("sc");
		Node_RuleVariable suVar = rs.var("su");
		Node_RuleVariable sVar = rs.var("s");
		rs.body().add(scuVar, RDF.type, SYBREAMO.SubstanceClassUnited);
		rs.body().add(scuVar, SYBREAMO.appliesToClass, scVar);
		rs.body().add(scVar, RDFS.subClassOf, SBPAX.Substance);
		rs.body().add(sVar, RDF.type, scVar);
		rs.body().add(new MakeTemp(), suVar);
		rs.head().add(suVar, RDF.type, SYBREAMO.SubstanceUnited);
		rs.head().add(suVar, SYBREAMO.appliesToSubstance, sVar);
		RULE_UNITED_STUFF_CLASS = rs.createRule();
	}
		
	static public Rule RULE_POOL_OF_UNITED_STUFF;
	
	static {
		RuleSpec rs = new RuleSpec("RULE_POOL_OF_UNITED_STUFF");
		Node_RuleVariable assumVar = rs.var("assum");
		Node_RuleVariable anyputVar = rs.var("anyput");
		Node_RuleVariable stuffVar = rs.var("stuff");
		Node_RuleVariable poolVar = rs.var("pool");
		rs.body().add(assumVar, RDF.type, SYBREAMO.SubstanceUnited);
		rs.body().add(assumVar, SYBREAMO.appliesToSubstance, stuffVar);
		rs.body().add(anyputVar, RDF.type, SBPAX.ProcessParticipant);
		rs.body().add(anyputVar, BioPAX2.PHYSICAL_ENTITY, stuffVar);
		rs.body().add(new MakeAndKeep(), anyputVar, BioPAX2.PHYSICAL_ENTITY.asNode(), poolVar);
		rs.head().add(anyputVar, SBPAX.involves, poolVar);
		rs.head().add(poolVar, SBPAX.consistsOf, stuffVar);
		RULE_POOL_OF_UNITED_STUFF = rs.createRule();
	}
		
	static public Rule RULE_STOICHIOMETRY;
	
	static {
		RuleSpec rs = new RuleSpec("RULE_STOICHIOMETRY");
		Node_RuleVariable anyputVar = rs.var("anyput");
		Node_RuleVariable stoichioVar = rs.var("stoichio");
		Node_RuleVariable numberVar = rs.var("number");
		rs.body().add(anyputVar, RDF.type, SBPAX.ProcessParticipant);
		rs.body().add(anyputVar, BioPAX2.STOICHIOMETRIC_COEFFICIENT, numberVar);
		rs.body().add(new MakeAndKeep(), 
				anyputVar, BioPAX2.STOICHIOMETRIC_COEFFICIENT.asNode(), stoichioVar);
		rs.head().add(stoichioVar, RDF.type, SBPAX.Stoichiometry);
		rs.head().add(anyputVar, SBPAX.hasStoichiometry, stoichioVar);
		rs.head().add(stoichioVar, SBPAX.hasNumber, numberVar);
		RULE_STOICHIOMETRY = rs.createRule();
	}
		
	static public Rule RULE_LOCATION;
	
	static {
		RuleSpec rs = new RuleSpec("RULE_LOCATION");
		Node_RuleVariable anyputVar = rs.var("anyput");
		Node_RuleVariable locVar = rs.var("loc");
		rs.body().add(anyputVar, BioPAX2.CELLULAR_LOCATION, locVar);
		rs.head().add(locVar, RDF.type, SBPAX.Location);
		RULE_LOCATION = rs.createRule();
	}
		
	static public Rule RULE_POOL_LOCATION;
	
	static {
		RuleSpec rs = new RuleSpec("RULE_POOL_LOCATION");
		Node_RuleVariable anyputVar = rs.var("anyput");
		Node_RuleVariable locVar = rs.var("loc");
		Node_RuleVariable poolVar = rs.var("pool");
		rs.body().add(anyputVar, RDF.type, SBPAX.ProcessParticipant);
		rs.body().add(anyputVar, BioPAX2.CELLULAR_LOCATION, locVar);
		rs.body().add(anyputVar, SBPAX.involves, poolVar);
		rs.head().add(locVar, RDF.type, SBPAX.Location);
		rs.head().add(poolVar, SBPAX.locatedAt, locVar);
		RULE_POOL_LOCATION = rs.createRule();
	}
		
	static {
		RULES.add(RDFSCoreRules.RULE_TRANSITIVE);
		RULES.add(RDFSCoreRules.RULE_SUBCLASS_DEFINITION);
		RULES.add(RDFSCoreRules.RULE_SUBPROPERTY_DEFINITION);
		RULES.add(RDFSCoreRules.RULE_RDFS2);
		RULES.add(RDFSCoreRules.RULE_RDFS3);
		RULES.add(RDFSCoreRules.RULE_RDFS6);
		RULES.add(RDFSCoreRules.RULE_RDFS9);
		RULES.add(RULE_BIOPAX2_CATALYST);
		RULES.add(RULE_UNITED_STUFF_CLASS);
		RULES.add(RULE_POOL_OF_UNITED_STUFF);
		RULES.add(RULE_STOICHIOMETRY);
		RULES.add(RULE_LOCATION);
		RULES.add(RULE_POOL_LOCATION);
	}
	
	public static Set<Property> entailedProperties = new HashSet<Property>();
	public static Set<Resource> entailedClasses = new HashSet<Resource>();
	
	static {
		entailedProperties.add(SBPAX.hasParticipantCatalyst);
		entailedProperties.add(SYBREAMO.appliesToSubstance);
		entailedProperties.add(SBPAX.involves);
		entailedProperties.add(SBPAX.consistsOf);
		entailedProperties.add(SBPAX.hasStoichiometry);
		entailedProperties.add(SBPAX.locatedAt);
		entailedClasses.add(SBPAX.Process);
		entailedClasses.add(SBPAX.ProcessParticipantCatalyst);
		entailedClasses.add(SYBREAMO.SubstanceUnited);
		entailedClasses.add(SBPAX.Stoichiometry);
		entailedClasses.add(SBPAX.Location);
	}
	
	public static boolean potentialEntailment(Statement statement) {
		return entailedProperties.contains(statement.getPredicate()) ||
		(RDF.type.equals(statement.getPredicate()) && 
				entailedClasses.contains(statement.getObject()));
	}
	
}
