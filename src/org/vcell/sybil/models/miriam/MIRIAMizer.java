package org.vcell.sybil.models.miriam;

/*   MIRIAMizer  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Handling MIRIAM annotations
 */

import java.util.Map;
import java.util.Set;

import org.vcell.sybil.rdf.RDFBox.RDFThing;

public interface MIRIAMizer {
	
	public RefGroup newRefGroup(RDFThing thing, MIRIAMQualifier qualifier);
	public RefGroup newRefGroup(RDFThing thing, MIRIAMQualifier qualifier, MIRIAMRef ref);	
	public RefGroup newRefGroup(RDFThing thing, MIRIAMQualifier qualifier, Set<MIRIAMRef> refs);
	
	public Set<RefGroup> getRefGroups(RDFThing thing, MIRIAMQualifier qualifier);
	public Map<RefGroup, MIRIAMQualifier> getModelRefGroups(RDFThing thing);
	public Map<RefGroup, MIRIAMQualifier> getBioRefGroups(RDFThing thing);
	public Map<RefGroup, MIRIAMQualifier> getAllRefGroups(RDFThing thing);
	public void detachRefGroups(RDFThing thing, MIRIAMQualifier qualifier);
	public void detachRefGroup(RDFThing thing, RefGroup group);
	public void detachModelRefGroups(RDFThing thing);
	public void detachBioRefGroups(RDFThing thing);
	public void detachAllRefGroups(RDFThing thing);
	public void deleteRefGroups(RDFThing thing, MIRIAMQualifier qualifier);
	public void deleteRefGroup(RDFThing thing, RefGroup group);
	public void deleteModelRefGroups(RDFThing thing);
	public void deleteBioRefGroups(RDFThing thing);
	public void deleteAllRefGroups(RDFThing thing);
}
