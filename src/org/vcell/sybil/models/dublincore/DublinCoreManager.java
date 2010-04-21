package org.vcell.sybil.models.dublincore;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cbit.vcell.biomodel.meta.Identifiable;

public interface DublinCoreManager {

	public void addDate(Identifiable identifiable, DublinCoreQualifier.DateQualifier dateQualifier,
			String dateString);
	
	public TreeMap<Identifiable, Map<DublinCoreQualifier.DateQualifier, Set<String>>> getTreeMap();
	
}
