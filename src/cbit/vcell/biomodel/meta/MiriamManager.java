/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel.meta;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vcell.sybil.models.dublincore.DublinCoreDate;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.util.document.Identifiable;

public interface MiriamManager {
	

	public interface DataType {
		public String getDataTypeName();  // provider name
		public String getBaseURL();
		public String getBaseURN();
		public String getDataTypeURL();
		public String getDescription();
	}
	
	public interface MiriamRefGroup {
		Set<MiriamResource> getMiriamRefs();
	}
	
	public interface MiriamResource {
		String getMiriamURN();
		DataType getDataType();  // database (e.g. uniprot).
		String getIdentifier();
	}
		
	TreeMap<Identifiable,Map<MiriamRefGroup,MIRIAMQualifier>> getMiriamTreeMap();
	
	Set<MiriamRefGroup> getMiriamRefGroups(Identifiable identifiable, MIRIAMQualifier miriamQualifier);
	
	Map<MiriamRefGroup,MIRIAMQualifier> getAllMiriamRefGroups(Identifiable identifiable);
	
	void addMiriamRefGroup(Identifiable identifiable, MIRIAMQualifier miriamQualifier, Set<MiriamResource> miriamResources) throws URNParseFailureException;
	
	void remove(Identifiable identifiable, MIRIAMQualifier miriamQualifier, MiriamRefGroup miriamRefGroup) throws URNParseFailureException;

	List<URL> getStoredCrossReferencedLinks(MiriamResource miriamResource) throws MalformedURLException;
	void addStoredCrossReferencedLink(MiriamResource miriamResource, URL url);
	void removeStoredCrossReferencedLink(MiriamResource miriamResource, URL url);
	void setPrettyName(MiriamResource miriamResource, String prettyName);
	String getPrettyName(MiriamResource miriamResource);
	
	MiriamResource createMiriamResource(String urnString) throws URNParseFailureException;
	
	Map<String,DataType> getAllDataTypes();
	
	//
	// Dublin Core Utilities
	//
	public void addDate(Identifiable identifiable, DublinCoreQualifier.DateQualifier dateQualifier,	DublinCoreDate dateString);
	
	public Map<Identifiable, Map<DublinCoreQualifier.DateQualifier, Set<DublinCoreDate>>> getDublinCoreDateMap();

	void addCreatorToAnnotation(Identifiable identifiable, String familyName, String givenName, String email, String organization);

}
