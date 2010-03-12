package org.vcell.sybil.rdf.reason;

/*   SYBREAMCore  --- by Oliver Ruebenacker, UCHC --- April 2008 to March 2010
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

	static public List<Rule> rules = new Vector<Rule>();
	
	static public Rule ruleBioPAX2Catalyst;
	
	static {
		RuleSpec rs = new RuleSpec("BioPAX2 Catalyst");
		Node_RuleVariable catalystVar = rs.var("catalyst");
		Node_RuleVariable participantVar = rs.var("participant");
		Node_RuleVariable processVar = rs.var("process");
		rs.body().add(catalystVar, RDF.type, BioPAX2.catalysis);
		rs.body().add(processVar, RDF.type, BioPAX2.conversion);
		rs.body().add(participantVar, RDF.type, BioPAX2.physicalEntityParticipant);
		rs.body().add(catalystVar, BioPAX2.CONTROLLED, processVar);
		rs.body().add(catalystVar, BioPAX2.CONTROLLER, participantVar);
		rs.head().add(processVar, RDF.type, SBPAX.Process);
		rs.head().add(participantVar, RDF.type, SBPAX.ProcessParticipantCatalyst);
		rs.head().add(processVar, SBPAX.hasParticipantCatalyst, participantVar);
		ruleBioPAX2Catalyst = rs.createRule();
	}
		
	static public Rule ruleUnmodifiableSubstanceClass;
	
	static {
		RuleSpec rs = new RuleSpec("Unmodifiable Substance Class");
		Node_RuleVariable uscAssumptionVar = rs.var("uscAssumption");
		Node_RuleVariable unmodSubstanceClassVar = rs.var("unmodSubstanceClass");
		Node_RuleVariable usAssumptionVar = rs.var("usAssumption");
		Node_RuleVariable substanceVar = rs.var("substance");
		rs.body().add(uscAssumptionVar, RDF.type, SYBREAMO.UnmodifiableSubstancesClass);
		rs.body().add(uscAssumptionVar, SYBREAMO.appliesToClass, unmodSubstanceClassVar);
		rs.body().add(unmodSubstanceClassVar, RDFS.subClassOf, SBPAX.Substance);
		rs.body().add(substanceVar, RDF.type, unmodSubstanceClassVar);
		rs.body().add(new MakeTemp(), usAssumptionVar);
		rs.head().add(usAssumptionVar, RDF.type, SYBREAMO.SubstanceUnmodifiable);
		rs.head().add(usAssumptionVar, SYBREAMO.appliesToSubstance, substanceVar);
		ruleUnmodifiableSubstanceClass = rs.createRule();
	}
		
	static public Rule ruleUnmodifiableSubstance;
	
	static {
		RuleSpec rs = new RuleSpec("Unmodifiable Substance");
		Node_RuleVariable assumptionVar = rs.var("assumption");
		Node_RuleVariable participantVar = rs.var("participant");
		Node_RuleVariable substanceVar = rs.var("substance");
		Node_RuleVariable speciesVar = rs.var("species");
		rs.body().add(assumptionVar, RDF.type, SYBREAMO.SubstanceUnmodifiable);
		rs.body().add(assumptionVar, SYBREAMO.appliesToSubstance, substanceVar);
		rs.body().add(participantVar, RDF.type, SBPAX.ProcessParticipant);
		rs.body().add(participantVar, BioPAX2.PHYSICAL_ENTITY, substanceVar);
		rs.body().add(new MakeAndKeep(), participantVar, BioPAX2.PHYSICAL_ENTITY.asNode(), speciesVar);
		rs.head().add(participantVar, SBPAX.involves, speciesVar);
		rs.head().add(speciesVar, SBPAX.consistsOf, substanceVar);
		ruleUnmodifiableSubstance = rs.createRule();
	}
		
	static public Rule ruleStoichiometry;
	
	static {
		RuleSpec rs = new RuleSpec("Stoichiometry");
		Node_RuleVariable participantVar = rs.var("participant");
		Node_RuleVariable stoichiometryVar = rs.var("stoichiometry");
		Node_RuleVariable numberVar = rs.var("number");
		rs.body().add(participantVar, RDF.type, SBPAX.ProcessParticipant);
		rs.body().add(participantVar, BioPAX2.STOICHIOMETRIC_COEFFICIENT, numberVar);
		rs.body().add(new MakeAndKeep(), 
				participantVar, BioPAX2.STOICHIOMETRIC_COEFFICIENT.asNode(), stoichiometryVar);
		rs.head().add(stoichiometryVar, RDF.type, SBPAX.Stoichiometry);
		rs.head().add(participantVar, SBPAX.hasStoichiometry, stoichiometryVar);
		rs.head().add(stoichiometryVar, SBPAX.hasNumber, numberVar);
		ruleStoichiometry = rs.createRule();
	}
		
	static public Rule ruleLocation;
	
	static {
		RuleSpec rs = new RuleSpec("Location");
		Node_RuleVariable participantVar = rs.var("participant");
		Node_RuleVariable locationVar = rs.var("location");
		rs.body().add(participantVar, BioPAX2.CELLULAR_LOCATION, locationVar);
		rs.head().add(locationVar, RDF.type, SBPAX.Location);
		ruleLocation = rs.createRule();
	}
		
	static public Rule ruleSpeciesLocation;
	
	static {
		RuleSpec rs = new RuleSpec("Species Location");
		Node_RuleVariable participantVar = rs.var("participant");
		Node_RuleVariable locationVar = rs.var("location");
		Node_RuleVariable speciesVar = rs.var("species");
		rs.body().add(participantVar, RDF.type, SBPAX.ProcessParticipant);
		rs.body().add(participantVar, BioPAX2.CELLULAR_LOCATION, locationVar);
		rs.body().add(participantVar, SBPAX.involves, speciesVar);
		rs.head().add(locationVar, RDF.type, SBPAX.Location);
		rs.head().add(speciesVar, SBPAX.locatedAt, locationVar);
		ruleSpeciesLocation = rs.createRule();
	}
		
	static {
		rules.add(RDFSCoreRules.ruleTransitive);
		rules.add(RDFSCoreRules.ruleSubClassDefinition);
		rules.add(RDFSCoreRules.ruleSubPropertyDefinition);
		rules.add(RDFSCoreRules.ruleRDFS2);
		rules.add(RDFSCoreRules.ruleRDFS3);
		rules.add(RDFSCoreRules.ruleRDFS6);
		rules.add(RDFSCoreRules.ruleRDFS9);
		rules.add(ruleBioPAX2Catalyst);
		rules.add(ruleUnmodifiableSubstanceClass);
		rules.add(ruleUnmodifiableSubstance);
		rules.add(ruleStoichiometry);
		rules.add(ruleLocation);
		rules.add(ruleSpeciesLocation);
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
		entailedClasses.add(SYBREAMO.SubstanceUnmodifiable);
		entailedClasses.add(SBPAX.Stoichiometry);
		entailedClasses.add(SBPAX.Location);
	}
	
	public static boolean potentialEntailment(Statement statement) {
		return entailedProperties.contains(statement.getPredicate()) ||
		(RDF.type.equals(statement.getPredicate()) && 
				entailedClasses.contains(statement.getObject()));
	}
	
}
