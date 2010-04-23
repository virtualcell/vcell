package cbit.vcell.biomodel.meta;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vcell.sybil.models.dublincore.DublinCoreDate;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;

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
	
	void remove(Identifiable identifiable, MIRIAMQualifier miriamQualifier, MiriamRefGroup miriamRefGroup);

	Set<URL> getStoredCrossReferencedLinks(MiriamResource miriamResource);
	
	MiriamResource createMiriamResource(String urnString) throws URNParseFailureException;
	
	Map<String,DataType> getAllDataTypes();
	
	//
	// Dublin Core Utilities
	//
	public void addDate(Identifiable identifiable, DublinCoreQualifier.DateQualifier dateQualifier,	DublinCoreDate dateString);
	
	public Map<Identifiable, Map<DublinCoreQualifier.DateQualifier, Set<DublinCoreDate>>> getDublinCoreDateMap();

	void addCreatorToAnnotation(Identifiable identifiable, String familyName, String givenName, String email, String organization);

}
