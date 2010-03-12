package org.vcell.sybil.models.miriam.imp;

/*   MIRIAMizerImp  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Handling MIRIAM annotations
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef;
import org.vcell.sybil.models.miriam.MIRIAMizer;
import org.vcell.sybil.models.miriam.RefGroup;
import org.vcell.sybil.models.miriam.MIRIAMQualifier.BioQualifier;
import org.vcell.sybil.models.miriam.MIRIAMQualifier.ModelQualifier;
import org.vcell.sybil.rdf.RDFBox;
import org.vcell.sybil.rdf.RDFBox.RDFThing;

import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class MIRIAMizerImp implements MIRIAMizer {

	public <B extends RDFBox> RefGroupImp<B> newRefGroup(RDFThing<B> thing, MIRIAMQualifier qualifier) {
		B box = thing.box();
		Bag bag = box.getRdf().createBag();
		box.getRdf().add(thing.resource(), qualifier.property(), bag);
		RefGroupImp<B> group = new RefGroupImp<B>(box, bag);
		return group;		
	}
	
	public <B extends RDFBox> 
	RefGroupImp<B> newRefGroup(RDFThing<B> thing, MIRIAMQualifier qualifier, MIRIAMRef ref) {
		B box = thing.box();
		Bag bag = box.getRdf().createBag();
		box.getRdf().add(thing.resource(), qualifier.property(), bag);
		RefGroupImp<B> group = new RefGroupImp<B>(box, bag);
		group.add(ref);
		return group;		
	}
	
	public <B extends RDFBox> RefGroupImp<B> 
	newRefGroup(RDFThing<B> thing, MIRIAMQualifier qualifier, Set<MIRIAMRef> refs) {
		B box = thing.box();
		Bag bag = box.getRdf().createBag();
		box.getRdf().add(thing.resource(), qualifier.property(), bag);
		RefGroupImp<B> group = new RefGroupImp<B>(box, bag);
		for(MIRIAMRef ref : refs) { group.add(ref);	}
		return group;		
	}
	
	public <B extends RDFBox> 
	Set<RefGroup<B>> getRefGroups(RDFThing<B> thing, MIRIAMQualifier qualifier) {
		Set<RefGroup<B>> groups = new HashSet<RefGroup<B>>();
		B box = thing.box();
		NodeIterator nodeIter = 
			box.getRdf().listObjectsOfProperty(thing.resource(), qualifier.property());
		while(nodeIter.hasNext()) {
			RDFNode node = nodeIter.nextNode();
			if(node instanceof Resource) {
				Bag bag = box.getRdf().getBag((Resource) node);
				groups.add(new RefGroupImp<B>(box, bag));
			}
		}
		return groups;
	}
	
	public <B extends RDFBox> Map<RefGroup<B>, ModelQualifier> getModelRefGroups(RDFThing<B> thing) {
		Map<RefGroup<B>, ModelQualifier> map = new HashMap<RefGroup<B>, ModelQualifier>();
		for(ModelQualifier qualifier : ModelQualifier.all) {
			Set<RefGroup<B>> groups = getRefGroups(thing, qualifier);
			for(RefGroup<B> group : groups) { map.put(group, qualifier); }
		}
		return map;
	}

	public <B extends RDFBox> Map<RefGroup<B>, BioQualifier> getBioRefGroups(RDFThing<B> thing) {
		Map<RefGroup<B>, BioQualifier> map = new HashMap<RefGroup<B>, BioQualifier>();
		for(BioQualifier qualifier : BioQualifier.all) {
			Set<RefGroup<B>> groups = getRefGroups(thing, qualifier);
			for(RefGroup<B> group : groups) { map.put(group, qualifier); }
		}
		return map;
	}

	public <B extends RDFBox> Map<RefGroup<B>, MIRIAMQualifier> getAllRefGroups(RDFThing<B> thing) {
		Map<RefGroup<B>, MIRIAMQualifier> map = 
			new HashMap<RefGroup<B>, MIRIAMQualifier>();
		for(MIRIAMQualifier qualifier : MIRIAMQualifier.all) {
			Set<RefGroup<B>> groups = getRefGroups(thing, qualifier);
			for(RefGroup<B> group : groups) { map.put(group, qualifier); }
		}
		return map;
	}

	public <B extends RDFBox> void detachRefGroup(RDFThing<B> thing, RefGroup<B> group) {
		thing.box().getRdf().removeAll(thing.resource(), null, group.bag());
	}
	
	public <B extends RDFBox> void detachRefGroups(RDFThing<B> thing, MIRIAMQualifier qualifier) {
		thing.box().getRdf().removeAll(thing.resource(), qualifier.property(), (RDFNode) null);
	}

	public <B extends RDFBox> void detachModelRefGroups(RDFThing<B> thing) {
		for(ModelQualifier qualifier : ModelQualifier.all) { detachRefGroups(thing, qualifier); }
	}

	public <B extends RDFBox> void detachBioRefGroups(RDFThing<B> thing) {
		for(BioQualifier qualifier : BioQualifier.all) { detachRefGroups(thing, qualifier); }
	}

	public <B extends RDFBox> void detachAllRefGroups(RDFThing<B> thing) {
		for(MIRIAMQualifier qualifier : MIRIAMQualifier.all) { detachRefGroups(thing, qualifier); }
	}

	public <B extends RDFBox> void deleteRefGroup(RDFThing<B> thing, RefGroup<B> group) {
		detachRefGroup(thing, group);
		group.delete();
	}

	public <B extends RDFBox> void deleteRefGroups(RDFThing<B> thing, MIRIAMQualifier qualifier) {
		Set<RefGroup<B>> groups = getRefGroups(thing, qualifier);
		for(RefGroup<B> group : groups) { deleteRefGroup(thing, group); }
	}

	public <B extends RDFBox> void deleteModelRefGroups(RDFThing<B> thing) {
		for(ModelQualifier qualifier : ModelQualifier.all) { deleteRefGroups(thing, qualifier); }		
	}

	public <B extends RDFBox> void deleteBioRefGroups(RDFThing<B> thing) {
		for(BioQualifier qualifier : BioQualifier.all) { deleteRefGroups(thing, qualifier); }		
	}

	public <B extends RDFBox> void deleteAllRefGroups(RDFThing<B> thing) {
		for(MIRIAMQualifier qualifier : MIRIAMQualifier.all) { deleteRefGroups(thing, qualifier); }				
	}

}
