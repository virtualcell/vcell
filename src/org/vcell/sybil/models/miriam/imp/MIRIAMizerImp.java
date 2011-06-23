package org.vcell.sybil.models.miriam.imp;

/*   MIRIAMizerImp  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Handling MIRIAM annotations
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.vcell.sybil.models.AnnotationQualifiers;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef;
import org.vcell.sybil.models.miriam.MIRIAMizer;
import org.vcell.sybil.models.miriam.RefGroup;
import org.vcell.sybil.rdf.RDFBox;
import org.vcell.sybil.rdf.RDFBox.RDFThing;

public class MIRIAMizerImp implements MIRIAMizer {

	public RefGroupImp newRefGroup(RDFThing thing, MIRIAMQualifier qualifier) {
		RDFBox box = thing.box();
		Resource bag = box.getRdf().getValueFactory().createBNode();
		box.getRdf().add(bag, RDF.TYPE, RDF.BAG);
		box.getRdf().add(thing.resource(), qualifier.property(), bag);
		RefGroupImp group = new RefGroupImp(box, bag);
		return group;		
	}
	
	public RefGroupImp newRefGroup(RDFThing thing, MIRIAMQualifier qualifier, MIRIAMRef ref) {
		RDFBox box = thing.box();
		Resource bag = box.getRdf().getValueFactory().createBNode();
		box.getRdf().add(bag, RDF.TYPE, RDF.BAG);
		box.getRdf().add(thing.resource(), qualifier.property(), bag);
		RefGroupImp group = new RefGroupImp(box, bag);
		group.add(ref);
		return group;		
	}
	
	public RefGroupImp newRefGroup(RDFThing thing, MIRIAMQualifier qualifier, Set<MIRIAMRef> refs) {
		RDFBox box = thing.box();
		Resource bag = box.getRdf().getValueFactory().createBNode();
		box.getRdf().add(bag, RDF.TYPE, RDF.BAG);
		box.getRdf().add(thing.resource(), qualifier.property(), bag);
		RefGroupImp group = new RefGroupImp(box, bag);
		for(MIRIAMRef ref : refs) { group.add(ref);	}
		return group;		
	}
	
	public Set<RefGroup> getRefGroups(RDFThing thing, MIRIAMQualifier qualifier) {
		Set<RefGroup> groups = new HashSet<RefGroup>();
		RDFBox box = thing.box();
		Iterator<Statement> iter = box.getRdf().match(thing.resource(), qualifier.property(), null);
		while(iter.hasNext()) {
			Statement statement = iter.next();
			Value node = statement.getObject();
			if(node instanceof Resource) {
				groups.add(new RefGroupImp(box, (Resource) node));
			}
		}
		return groups;
	}
	
	public Map<RefGroup, MIRIAMQualifier> getModelRefGroups(RDFThing thing) {
		Map<RefGroup, MIRIAMQualifier> map = new HashMap<RefGroup, MIRIAMQualifier>();
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMMODEL_all) {
			Set<RefGroup> groups = getRefGroups(thing, qualifier);
			for(RefGroup group : groups) { map.put(group, qualifier); }
		}
		return map;
	}

	public Map<RefGroup, MIRIAMQualifier> getBioRefGroups(RDFThing thing) {
		Map<RefGroup, MIRIAMQualifier> map = new HashMap<RefGroup, MIRIAMQualifier>();
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMBIO_all) {
			Set<RefGroup> groups = getRefGroups(thing, qualifier);
			for(RefGroup group : groups) { map.put(group, qualifier); }
		}
		return map;
	}

	public Map<RefGroup, MIRIAMQualifier> getAllRefGroups(RDFThing thing) {
		Map<RefGroup, MIRIAMQualifier> map = 
			new HashMap<RefGroup, MIRIAMQualifier>();
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAM_all) {
			Set<RefGroup> groups = getRefGroups(thing, qualifier);
			for(RefGroup group : groups) { map.put(group, qualifier); }
		}
		return map;
	}

	public void detachRefGroup(RDFThing thing, RefGroup group) {
		Iterator<Statement> iter = thing.box().getRdf().match(thing.resource(), null, group.resource());
		while(iter.hasNext()) { iter.next(); iter.remove(); }
	}
	
	public void detachRefGroups(RDFThing thing, MIRIAMQualifier qualifier) {
		Iterator<Statement> iter = thing.box().getRdf().match(thing.resource(), qualifier.property(), null);
		while(iter.hasNext()) { iter.next(); iter.remove(); }
	}

	public void detachModelRefGroups(RDFThing thing) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMMODEL_all) { detachRefGroups(thing, qualifier); }
	}

	public void detachBioRefGroups(RDFThing thing) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMBIO_all) { detachRefGroups(thing, qualifier); }
	}

	public void detachAllRefGroups(RDFThing thing) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAM_all) { detachRefGroups(thing, qualifier); }
	}

	public void deleteRefGroup(RDFThing thing, RefGroup group) {
		detachRefGroup(thing, group);
		group.delete();
	}

	public void deleteRefGroups(RDFThing thing, MIRIAMQualifier qualifier) {
		Set<RefGroup> groups = getRefGroups(thing, qualifier);
		for(RefGroup group : groups) { deleteRefGroup(thing, group); }
	}

	public void deleteModelRefGroups(RDFThing thing) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMMODEL_all) { deleteRefGroups(thing, qualifier); }		
	}

	public void deleteBioRefGroups(RDFThing thing) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMBIO_all) { deleteRefGroups(thing, qualifier); }		
	}

	public void deleteAllRefGroups(RDFThing thing) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAM_all) { deleteRefGroups(thing, qualifier); }				
	}

}
