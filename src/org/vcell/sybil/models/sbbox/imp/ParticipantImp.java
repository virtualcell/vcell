package org.vcell.sybil.models.sbbox.imp;

/*   ParticipantImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX process participant
 */

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class ParticipantImp extends SBWrapper implements SBBox.MutableParticipant {

	public ParticipantImp(SBBox man, Resource resource) { super(man, resource); }

	public SBBox.MutableSpecies species() { 
		SBBox.MutableSpecies species = null;
		StmtIterator stmtIter = 
			box().getRdf().listStatements(this.resource(), SBPAX.involves, (RDFNode) null);
		while(stmtIter.hasNext() && species == null) { 
			RDFNode speNode = stmtIter.nextStatement().getObject(); 
			if(speNode instanceof Resource) {
				species = box().factories().speciesFactory().open((Resource)speNode);
			}
		}
		return species; 
	}

	public SBBox.MutableStoichiometry stoichiometry() { 
		SBBox.MutableStoichiometry stoichiometry = null;
		StmtIterator stmtIter = 
			box().getRdf().listStatements(this.resource(), SBPAX.hasStoichiometry, (RDFNode) null);
		while(stmtIter.hasNext() && stoichiometry == null) { 
			RDFNode stoNode = stmtIter.nextStatement().getObject(); 
			if(stoNode instanceof Resource) { 
				stoichiometry = box().factories().stoichiometryFactory().open((Resource) stoNode);
			}
		}
		return stoichiometry; 
	}

	public SBBox.MutableParticipant setSpecies(SBBox.Species species) { 
		box().getRdf().removeAll(this.resource(), SBPAX.involves, (RDFNode) null);
		if(species != null) {
			box().getRdf().add(this.resource(), SBPAX.involves, species.resource());			
		}
		return this;
	}

	public SBBox.MutableParticipant setStoichiometry(SBBox.Stoichiometry stoichiometry) {
		box().getRdf().removeAll(this.resource(), SBPAX.hasStoichiometry, (RDFNode) null);
		if(stoichiometry != null) { 
			box().getRdf().add(this.resource(), SBPAX.hasStoichiometry, stoichiometry.resource()); 
		}
		return this;
	}
	
	public String shortName() {
		String name = "";
		if(box().getRdf().contains(resource(), RDF.type, SBPAX.ProcessParticipantLeft)) {
			name = "left";
		} else if(box().getRdf().contains(resource(), RDF.type, SBPAX.ProcessParticipantRight)) {
			name = "right";
		} else if(box().getRdf().contains(resource(), RDF.type, SBPAX.ProcessParticipantCatalyst)) {
			name = "catalyst";
		} else {
			name = super.shortName();
		}
		return name;
	}

}