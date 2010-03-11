package org.vcell.sybil.models.miriam;

/*   MIRIAMizer  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Handling MIRIAM annotations
 */

import java.util.Map;
import java.util.Set;

import org.vcell.sybil.models.miriam.MIRIAMQualifier.BioQualifier;
import org.vcell.sybil.models.miriam.MIRIAMQualifier.ModelQualifier;
import org.vcell.sybil.rdf.RDFBox;
import org.vcell.sybil.rdf.RDFBox.RDFThing;

public interface MIRIAMizer {
	
	public <B extends RDFBox> RefGroup<B> newRefGroup(RDFThing<B> thing, MIRIAMQualifier qualifier);
	
	public <B extends RDFBox> 
	RefGroup<B> newRefGroup(RDFThing<B> thing, MIRIAMQualifier qualifier, MIRIAMRef ref);
	
	public <B extends RDFBox> RefGroup<B> 
	newRefGroup(RDFThing<B> thing, MIRIAMQualifier qualifier, Set<MIRIAMRef> refs);
	
	public <B extends RDFBox> Set<RefGroup<B>> getRefGroups(RDFThing<B> thing, MIRIAMQualifier qualifier);
	public <B extends RDFBox> Map<RefGroup<B>, ModelQualifier> getModelRefGroups(RDFThing<B> thing);
	public <B extends RDFBox> Map<RefGroup<B>, BioQualifier> getBioRefGroups(RDFThing<B> thing);
	public <B extends RDFBox> Map<RefGroup<B>, MIRIAMQualifier> getAllRefGroups(RDFThing<B> thing);
	public <B extends RDFBox> void detachRefGroups(RDFThing<B> thing, MIRIAMQualifier qualifier);
	public <B extends RDFBox> void detachRefGroup(RDFThing<B> thing, RefGroup<B> group);
	public <B extends RDFBox> void detachModelRefGroups(RDFThing<B> thing);
	public <B extends RDFBox> void detachBioRefGroups(RDFThing<B> thing);
	public <B extends RDFBox> void detachAllRefGroups(RDFThing<B> thing);
	public <B extends RDFBox> void deleteRefGroups(RDFThing<B> thing, MIRIAMQualifier qualifier);
	public <B extends RDFBox> void deleteRefGroup(RDFThing<B> thing, RefGroup<B> group);
	public <B extends RDFBox> void deleteModelRefGroups(RDFThing<B> thing);
	public <B extends RDFBox> void deleteBioRefGroups(RDFThing<B> thing);
	public <B extends RDFBox> void deleteAllRefGroups(RDFThing<B> thing);
}
