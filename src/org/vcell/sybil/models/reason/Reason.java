package org.vcell.sybil.models.reason;

/*   Reason  --- by Oliver Ruebenacker, UCHC --- November 2007 to February 2009
 *   Creates inference models out of models and schemas
 */

import java.util.Vector;

import org.vcell.sybil.rdf.reason.SYBREAMFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

public class Reason {

	public static abstract class Choice {
		public abstract String name();
		public abstract Model infModel(Model data, Model schema);
	}
	
	public static abstract class ReasonerChoice extends Choice {

		public abstract Reasoner reasoner();
		
		public InfModel infModel(Model data, Model schema) {
			return ModelFactory.createInfModel(reasoner(), schema, data);
		}

	}
	
	public static final Choice None = new Choice() {
		public String name() { return "None";}
		public Model infModel(Model data, Model schema) { return ModelFactory.createUnion(data, schema); }
	};
	
	public static final ReasonerChoice SYBREAM = new ReasonerChoice() {
		public String name() { return "SYBREAM"; }
		public Reasoner reasoner() { return SYBREAMFactory.defaultSYBREAM(); }
	};
	
	public static final ReasonerChoice Transitive = new ReasonerChoice() {
		public String name() { return "Transitive"; }
		public Reasoner reasoner() { return ReasonerRegistry.getTransitiveReasoner(); }
	};
	
	public static final ReasonerChoice RDFSSimple = new ReasonerChoice() {
		public String name() { return "RDFS (Simple)"; }
		public Reasoner reasoner() { return ReasonerRegistry.getRDFSSimpleReasoner(); }
	};
	
	public static final ReasonerChoice RDFS = new ReasonerChoice() {
		public String name() { return "RDFS (Default)"; }
		public Reasoner reasoner() { return ReasonerRegistry.getRDFSReasoner(); }
	};
	
	public static final ReasonerChoice RDFSFull = new ReasonerChoice() {
		public String name() { return "RDFS (Full)"; }

		public Reasoner reasoner() {
			Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
			reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_FULL);
			return reasoner;
		}
		
	};
	
	public static final ReasonerChoice OWLMicro = new ReasonerChoice() {
		public String name() { return "OWL (Micro)"; }
		public Reasoner reasoner() { return ReasonerRegistry.getOWLMicroReasoner(); }
	};
	
	public static final ReasonerChoice OWLMini = new ReasonerChoice() {
		public String name() { return "OWL (Mini)"; }
		public Reasoner reasoner() { return ReasonerRegistry.getOWLMiniReasoner(); }
	};
	
	public static final ReasonerChoice OWL = new ReasonerChoice() {
		public String name() { return "OWL (Standard)"; }
		public Reasoner reasoner() { return ReasonerRegistry.getOWLReasoner(); }
	};
	
	public static final Vector<Choice> choices = initChoices();
	
	public static Vector<Choice> initChoices() {
		Vector<Choice> choices = new Vector<Choice>();
		choices.add(None);
		choices.add(SYBREAM);
		choices.add(Transitive);
		choices.add(RDFSSimple);
		choices.add(RDFS);
		choices.add(RDFSFull);
		choices.add(OWLMicro);
		choices.add(OWLMini);
		choices.add(OWL);
		return choices;
	}
	
	public static enum ChoiceOld { }

}
