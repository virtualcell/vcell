package org.vcell.sybil.models.dublincore;

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.rdf.schemas.ProtegeDC;
import org.vcell.sybil.util.sets.SetUtil;

public class DublinCoreQualifiers {

	public static DublinCoreQualifier.DateQualifier created = 
		new DublinCoreQualifier.DateQualifier(ProtegeDC.created);

	public static Set<DublinCoreQualifier.DateQualifier> dateQualifiers = SetUtil.newSet(created);
	public static Set<DublinCoreQualifier> allQualifiers = new HashSet<DublinCoreQualifier>();
	
	static {
		allQualifiers.addAll(dateQualifiers);
	}
}
