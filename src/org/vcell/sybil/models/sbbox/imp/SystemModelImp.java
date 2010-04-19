package org.vcell.sybil.models.sbbox.imp;

/*   SystemModelImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX SystemModel
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class SystemModelImp extends SBWrapper implements SBBox.MutableSystemModel {

	public SystemModelImp(SBBox box, Resource resource) { super(box, resource); }

	public Set<SBBox.ProcessModel> processModels() {
		Set<SBBox.ProcessModel> processModels = new HashSet<SBBox.ProcessModel>();
		NodeIterator procModIter = box().getRdf().listObjectsOfProperty(resource(), SBPAX.hasProcessModel);
		while(procModIter.hasNext()) {
			RDFNode procModNode = procModIter.nextNode();
			if(procModNode instanceof Resource) {
				processModels.add(box().factories().processModelFactory().open((Resource) procModNode));
			}
		}
		return processModels;
	}

	public SBBox.MutableSystemModel addProcessModel(SBBox.ProcessModel processModel) {
		box().getRdf().add(resource(), SBPAX.hasProcessModel, processModel.resource());
		return this;
	}

	public SBBox.MutableSystemModel removeProcessModel(SBBox.ProcessModel processModel) {
		box().getRdf().remove(resource(), SBPAX.hasProcessModel, processModel.resource());
		return this;
	}

	public SBBox.MutableSystemModel removeAllProcessModels() {
		for(SBBox.ProcessModel processModel : processModels()) {
			removeProcessModel(processModel);
		}
		return this;
	}

	public Set<SBBox.Substance> substances() {
		Set<SBBox.Substance> substances = new HashSet<SBBox.Substance>();
		NodeIterator subsIter = box().getRdf().listObjectsOfProperty(resource(), SBPAX.modelsSubstance);
		while(subsIter.hasNext()) {
			RDFNode subsNode = subsIter.nextNode();
			if(subsNode instanceof Resource) {
				substances.add(box().factories().substanceFactory().open((Resource) subsNode));
			}
		}
		return substances;
	}

	public SBBox.MutableSystemModel addSubstance(SBBox.Substance substance) {
		box().getRdf().add(resource(), SBPAX.modelsSubstance, substance.resource());
		return this;
	}

	public SBBox.MutableSystemModel removeSubstance(SBBox.Substance substance) {
		box().getRdf().remove(resource(), SBPAX.modelsSubstance, substance.resource());
		return this;
	}

	public SBBox.MutableSystemModel removeAllSubstances() {
		for(SBBox.Substance substance : substances()) {
			removeSubstance(substance);
		}
		return this;
	}

	public Set<SBBox.Species> specieses() {
		Set<SBBox.Species> specieses = new HashSet<SBBox.Species>();
		NodeIterator specIter = box().getRdf().listObjectsOfProperty(resource(), SBPAX.modelsSpecies);
		while(specIter.hasNext()) {
			RDFNode specNode = specIter.nextNode();
			if(specNode instanceof Resource) {
				specieses.add(box().factories().speciesFactory().open((Resource) specNode));
			}
		}
		return specieses;
	}

	public SBBox.MutableSystemModel addSpecies(SBBox.Species species) {
		box().getRdf().add(resource(), SBPAX.modelsSpecies, species.resource());
		return this;
	}

	public SBBox.MutableSystemModel removeSpecies(SBBox.Species species) {
		box().getRdf().remove(resource(), SBPAX.modelsSpecies, species.resource());
		return this;
	}

	public SBBox.MutableSystemModel removeAllSpecieses() {
		for(SBBox.Species species : specieses()) {
			removeSpecies(species);
		}
		return this;
	}

}