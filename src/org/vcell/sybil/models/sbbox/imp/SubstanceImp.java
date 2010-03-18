package org.vcell.sybil.models.sbbox.imp;

/*   SubstanceImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX Substance
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.Substance;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class SubstanceImp extends SBWrapper implements SBBox.MutableSubstance {

	public SubstanceImp(SBBox man, Resource resource) { super(man, resource); }

	public Set<Substance> subSubstances() {
		Set<Substance> subSubstances = new HashSet<Substance>();
		ResIterator resIter = box().getRdf().listResourcesWithProperty(SBPAX.subSetOf, resource());
		while(resIter.hasNext()) { subSubstances.add(box().factories().substance().open(resIter.nextResource())); }
		return subSubstances;
	}

	public boolean hasSubSubstance(Substance substance) {
		return box().getRdf().contains(substance.resource(), SBPAX.subSetOf, resource());
	}

	public void addSubSubstance(Substance substance) {
		box().getRdf().add(substance.resource(), SBPAX.subSetOf, resource());
	}

	public void removeSubSubstance(Substance substance) {
		box().getRdf().remove(substance.resource(), SBPAX.subSetOf, resource());
	}

	public void removeAllSubSubstances() {
		for(Substance substance : subSubstances()) { removeSubSubstance(substance); }
	}

	public Set<Substance> superSubstances() {
		Set<Substance> superSubstances = new HashSet<Substance>();
		NodeIterator nodeIter = box().getRdf().listObjectsOfProperty(resource(), SBPAX.subSetOf);
		while(nodeIter.hasNext()) {
			RDFNode node = nodeIter.nextNode();
			if(node instanceof Resource) {
				superSubstances.add(box().factories().substance().open((Resource) node));
			}
		}
		return superSubstances;
	}

	public boolean hasSuperSubstance(Substance substance) {
		return box().getRdf().contains(resource(), SBPAX.subSetOf, substance.resource());
	}
		
	public void addSuperSubstance(Substance substance) {
		box().getRdf().add(resource(), SBPAX.subSetOf, substance.resource());
	}

	public void removeSuperSubstance(Substance substance) {
		box().getRdf().remove(resource(), SBPAX.subSetOf, substance.resource());
	}

	public void removeAllSuperSubstances() {
		for(Substance substance : superSubstances()) { removeSuperSubstance(substance); }
	}

	public Set<RDFType> types() {
		Set<RDFType> types = new HashSet<RDFType>();
		NodeIterator nodeIter = box().getRdf().listObjectsOfProperty(resource(), RDF.type);
		while(nodeIter.hasNext()) {
			RDFNode node = nodeIter.nextNode();
			if(node instanceof Resource) { types.add(box().factories().type().create((Resource)node)); }
		}
		return types;
	}

	public boolean hasType(RDFType type) {
		return box().getRdf().contains(resource(), RDF.type, type.resource());
	}

	public void addType(RDFType type) {
		box().getRdf().add(resource(), RDF.type, type.resource());
	}

	public void removeType(RDFType type) {
		box().getRdf().remove(resource(), RDF.type, type.resource());			
	}

	public void removeAllTypesButSubstance() {
		for(RDFType type : types()) {
			if(!SBPAX.Substance.equals(type.resource())) { removeType(type); }
		}
	}

}