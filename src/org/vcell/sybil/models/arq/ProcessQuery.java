package org.vcell.sybil.models.arq;

/*   ProcessQuery  --- by Oliver Ruebenacker, UCHC --- June 2008 to March 2009
 *   A query for processes and participants in SBPAX
 */

import org.vcell.sybil.models.bpimport.edges.ProcessVars;
import org.vcell.sybil.rdf.JenaUtil;
import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.vocabulary.RDF;

public class ProcessQuery {

	public static Op op() {
		ElementGroup group = new ElementGroup();
		ElementTriplesBlock blockProcessPart = new ElementTriplesBlock();
		blockProcessPart.addTriple(JenaUtil.triple(ProcessVars.PROCESS, RDF.type, SBPAX.Process));
		blockProcessPart.addTriple(JenaUtil.triple(ProcessVars.PARTICIPANT, RDF.type, SBPAX.ProcessParticipant));
		blockProcessPart.addTriple(JenaUtil.triple(ProcessVars.PROCESS, SBPAX.hasParticipant, 
				ProcessVars.PARTICIPANT));
		group.addElement(blockProcessPart);
		ElementGroup speciesGroup = new ElementGroup();
		ElementTriplesBlock speciesBlock = new ElementTriplesBlock();
		speciesBlock.addTriple(JenaUtil.triple(ProcessVars.SPECIES, RDF.type, SBPAX.Species));
		speciesBlock.addTriple(JenaUtil.triple(ProcessVars.PARTICIPANT, SBPAX.involves, ProcessVars.SPECIES));
		speciesGroup.addElement(speciesBlock);
		ElementTriplesBlock substanceBlock = new ElementTriplesBlock();
		substanceBlock.addTriple(JenaUtil.triple(ProcessVars.SUBSTANCE, RDF.type, SBPAX.Substance));
		substanceBlock.addTriple(
				JenaUtil.triple(ProcessVars.SPECIES, SBPAX.consistsOf, ProcessVars.SUBSTANCE));
		speciesGroup.addElement(new ElementOptional(substanceBlock));
		ElementTriplesBlock speciesLocationBlock = new ElementTriplesBlock();
		speciesLocationBlock.addTriple(JenaUtil.triple(ProcessVars.LOCATION, RDF.type, SBPAX.Location));
		speciesLocationBlock.addTriple(
				JenaUtil.triple(ProcessVars.SPECIES, SBPAX.locatedAt, ProcessVars.LOCATION));
		speciesGroup.addElement(new ElementOptional(speciesLocationBlock));
		group.addElement(new ElementOptional(speciesGroup));
		ElementGroup entityGroup = new ElementGroup();
		ElementTriplesBlock entityBlock = new ElementTriplesBlock();
		entityBlock.addTriple(JenaUtil.triple(ProcessVars.ENTITY, RDF.type, BioPAX2.entity));
		entityBlock.addTriple(
				JenaUtil.triple(ProcessVars.PARTICIPANT, BioPAX2.PHYSICAL_ENTITY, ProcessVars.ENTITY));
		entityGroup.addElement(entityBlock);
		ElementTriplesBlock entityTypeBlock = new ElementTriplesBlock();
		entityTypeBlock.addTriple(JenaUtil.triple(ProcessVars.ENTITY, RDF.type, ProcessVars.ENTITY_TYPE));
		entityGroup.addElement(new ElementOptional(entityTypeBlock));		
		group.addElement(new ElementOptional(entityGroup));		
		ElementTriplesBlock locBlock = new ElementTriplesBlock();
		locBlock.addTriple(
				JenaUtil.triple(ProcessVars.LOCATION, RDF.type, BioPAX2.openControlledVocabulary));
		locBlock.addTriple(
				JenaUtil.triple(ProcessVars.PARTICIPANT, BioPAX2.CELLULAR_LOCATION, ProcessVars.LOCATION));
		group.addElement(new ElementOptional(locBlock));		
		ElementTriplesBlock stoichioBlock = new ElementTriplesBlock();
		stoichioBlock.addTriple(JenaUtil.triple(ProcessVars.STOICHIOMETRY, RDF.type, SBPAX.Stoichiometry));
		stoichioBlock.addTriple(
				JenaUtil.triple(ProcessVars.PARTICIPANT, SBPAX.hasStoichiometry, ProcessVars.STOICHIOMETRY));
		stoichioBlock.addTriple(
				JenaUtil.triple(ProcessVars.STOICHIOMETRY, SBPAX.hasNumber, ProcessVars.STOICHCOEFF));
		group.addElement(new ElementOptional(stoichioBlock));		
		ElementTriplesBlock bpStoichioGroup = new ElementTriplesBlock();
		bpStoichioGroup.addTriple(
				JenaUtil.triple(ProcessVars.PARTICIPANT, BioPAX2.STOICHIOMETRIC_COEFFICIENT, ProcessVars.STOICHCOEFF));
		group.addElement(new ElementOptional(bpStoichioGroup));		
		return Algebra.optimize(Algebra.compile(group));
	}
	
}
