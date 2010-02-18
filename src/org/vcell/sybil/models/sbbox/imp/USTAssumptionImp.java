package org.vcell.sybil.models.sbbox.imp;

/*   SubstanceImp  --- by Oliver Ruebenacker, UCHC --- June to October 2009
 *   A view of a resource representing an SBPAX Substance
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.MutableUSTAssumption;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;
import org.vcell.sybil.rdf.reason.SYBREAMO;

import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class USTAssumptionImp extends SBWrapper 
implements MutableUSTAssumption {

	public USTAssumptionImp(SBBox man, Resource resource) { super(man, resource); }

	public Set<RDFType> typesItAppliesTo() {
		Set<RDFType> types = new HashSet<RDFType>();
		NodeIterator nodeIter = box().getRdf().listObjectsOfProperty(resource(), SYBREAMO.appliesToClass);
		while(nodeIter.hasNext()) {
			RDFNode node = nodeIter.nextNode();
			if(node instanceof Resource) {
				types.add(box().factories().type().open((Resource) node));
			}
		}
		return types;
	}

	public boolean appliesTo(RDFType type) {
		return box().getRdf().contains(resource(), SYBREAMO.appliesToClass, type.resource());
	}
		
	public MutableUSTAssumption addTypeItAppliesTo(RDFType type) {
		box().getRdf().add(resource(), SYBREAMO.appliesToClass, type.resource());
		return this;
	}

	public MutableUSTAssumption removeTypeItAppliesTo(RDFType type) {
		box().getRdf().remove(resource(), SYBREAMO.appliesToClass, type.resource());
		return this;
	}

	public MutableUSTAssumption removeAllTypesItAppliesTo() {
		for(RDFType type : typesItAppliesTo()) { removeTypeItAppliesTo(type); }
		return this;
	}

}