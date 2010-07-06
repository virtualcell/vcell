package cbit.vcell.biomodel.meta;

import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vcell.sybil.models.AnnotationQualifiers;
import org.vcell.sybil.models.dublincore.DublinCoreDate;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier;
import org.vcell.sybil.models.dublincore.DublinCoreQualifier.DateQualifier;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef;
import org.vcell.sybil.models.miriam.MIRIAMizer;
import org.vcell.sybil.models.miriam.RefGroup;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.models.miriam.imp.MIRIAMizerImp;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;

import cbit.vcell.biomodel.meta.registry.OpenRegistry.OpenEntry;
import cbit.vcell.biomodel.meta.registry.Registry.Entry;
import cbit.vcell.xml.gui.MiriamTreeModel;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class VCMetaDataMiriamManager implements MiriamManager, Serializable {
	
	public static class VCMetaDataDataType implements DataType {
		
		private final static HashMap<String,DataType> all = new HashMap<String,DataType>();
		
		public final static DataType DataType_PIR 			= new VCMetaDataDataType("PIRSF",				"http://pir.georgetown.edu/pirsf/",	null,
				"urn:miriam:pirsf",			"The PIR SuperFamily concept is being used as a guiding principle to provide comprehensive and non-overlapping clustering of UniProtKB sequences into a hierarchical order to reflect their evolutionary relationships.");

		public final static DataType DataType_DOI 			= new VCMetaDataDataType("DOI",					"http://www.doi.org/",				null,
				"urn:miriam:doi",			"The Digital Object Identifier System is for identifying content objects in the digital environment.");
		
		public final static DataType DataType_BIOMODELS 	= new VCMetaDataDataType("BioModels Database",	"http://www.ebi.ac.uk/biomodels/",	"http://www.ebi.ac.uk/biomodels-main/",
				"urn:miriam:biomodels.db",	"BioModels Database is a data resource that allows biologists to store, search and retrieve published mathematical models of biological interests.");
		
		public final static DataType DataType_Chebi 		= new VCMetaDataDataType("ChEBI",				"http://www.ebi.ac.uk/chebi/",		"http://www.ebi.ac.uk/chebi/searchFreeText.do?searchString=",
				"urn:miriam:obo.chebi",		"Chemical Entities of Biological Interest (ChEBI) is a freely available dictionary of molecular entities focused on 'small' chemical compounds.");
		
		public final static DataType DataType_IntAct 		= new VCMetaDataDataType("IntAct",				"http://www.ebi.ac.uk/intact/",		null,
				"urn:miriam:intact",		"IntAct provides a freely available, open source database system and analysis tools for protein interaction data.");
		
		public final static DataType DataType_InterPro 		= new VCMetaDataDataType("InterPro",			"http://www.ebi.ac.uk/interpro/",	"http://www.ebi.ac.uk/interpro/DisplayIproEntry?ac=",
				"urn:miriam:interpro",		"InterPro is a database of protein families, domains and functional sites in which identifiable features found in known proteins can be applied to unknown protein sequences.");
		
		public final static DataType DataType_ECCODE 		= new VCMetaDataDataType("Enzyme Nomenclature",	"http://www.ec-code.org/",			"http://www.ebi.ac.uk/intenz/query?cmd=SearchEC&ec=",
				"urn:miriam:ec-code",		"The Enzyme Classification contains the recommendations of the Nomenclature Committee of the International Union of Biochemistry and Molecular Biology on the nomenclature and classification of enzyme-catalysed reactions.");
		
		public final static DataType DataType_ENSEMBLE 		= new VCMetaDataDataType("Ensembl",				"http://www.ensembl.org/",			null,
				"urn:miriam:ensembl",		"Ensembl is a joint project between EMBL - EBI and the Sanger Institute to develop a software system which produces and maintains automatic annotation on selected eukaryotic genomes.");
		
		public final static DataType DataType_GO 			= new VCMetaDataDataType("Gene Ontology",		"http://www.geneontology.org/",		"http://www.ebi.ac.uk/ego/GTerm?id=",
				"urn:miriam:obo.go",		"The Gene Ontology project provides a controlled vocabulary to describe gene and gene product attributes in any organism.");
		
		public final static DataType DataType_KEGGCOMPOUND 	= new VCMetaDataDataType("KEGG Compound",		"http://www.genome.jp/kegg/compound/",	"http://www.genome.jp/dbget-bin/www_bget?cpd:",
				"urn:miriam:kegg.compound",	"KEGG compound contains our knowledge on the universe of chemical substances that are relevant to life.");
		
		public final static DataType DataType_KEGGPATHWAY 	= new VCMetaDataDataType("KEGG Pathway",		"http://www.genome.jp/kegg/pathway/",	"http://www.genome.ad.jp/dbget-bin/www_bget?pathway+",
				"urn:miriam:kegg.pathway",	"KEGG PATHWAY is a collection of manually drawn pathway maps representing our knowledge on the molecular interaction and reaction networks.");
		
		public final static DataType DataType_KEGGREACTION 	= new VCMetaDataDataType("KEGG Reaction",		"http://www.genome.jp/kegg/reaction/",	null,
				"urn:miriam:kegg.reaction",	"KEGG reaction contains our knowledge on the universe of reactions that are relevant to life.");
		
		public final static DataType DataType_PUBMED 		= new VCMetaDataDataType("PubMed",				"http://www.pubmed.gov/",			"http://www.ncbi.nlm.nih.gov/pubmed/",
				"urn:miriam:pubmed",		"PubMed is a service of the U.S. National Library of Medicine that includes citations from MEDLINE and other life science journals for biomedical articles back to the 1950s.");
		
		public final static DataType DataType_TAXONOMY 		= new VCMetaDataDataType("Taxonomy",			"http://www.taxonomy.org/",			"http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=",
				"urn:miriam:taxonomy",		"The taxonomy contains the relationships between all living forms for which nucleic acid or protein sequence have been determined.");
		
		public final static DataType DataType_REACTOME 		= new VCMetaDataDataType("Reactome",			"http://www.reactome.org/",			"http://www.reactome.org/cgi-bin/eventbrowser_st_id?FROM_REACTOME=1&ST_ID=",
				"urn:miriam:reactome",		"The Reactome project is a collaboration to develop a curated resource of core pathways and reactions in human biology.");
		
		public final static DataType DataType_UNIPROT 		= new VCMetaDataDataType("UniProt",				"http://www.uniprot.org/",			"http://www.ebi.uniprot.org/entry/",
				"urn:miriam:uniprot",		"UniProt (Universal Protein Resource) is the world's most comprehensive catalog of information on proteins. It is a central repository of protein sequence and function created by joining the information contained in Swiss-Prot, TrEMBL, and PIR.");
		
		public final static DataType DataType_ICD 			= new VCMetaDataDataType("ICD",					"http://www.who.int/classifications/icd/",		null,
				"urn:miriam:icd",			"The International Classification of Diseases is the international standard diagnostic classification for all general epidemiological and many health management purposes.");
		
		private String name = null;
		private String siteUrl = null;
		private String urnPrefix = null;
		private String description = null;
		private String urlPrefix = null;
		
		public VCMetaDataDataType(String name, String siteUrl, String urlPrefix, String urnPrefix, String description){
			this.name = name;
			this.siteUrl = siteUrl;
			this.urnPrefix = urnPrefix;
			this.description = description;
			this.urlPrefix = urlPrefix;
			all.put(urnPrefix,this);
		}
		
		public int hashCode(){
			return urnPrefix.hashCode();
		}
		
		public boolean equals(Object object){
			if (object instanceof VCMetaDataDataType){
				VCMetaDataDataType dataType = (VCMetaDataDataType)object;
				if (urnPrefix.equals(dataType.urnPrefix)){
					return true;
				}
			}
			return false;
		}
		
		public String getBaseURN() {
			return urnPrefix;
		}
		
		public String getBaseURL() {
			return urlPrefix;
		}
		
		public String getDataTypeName() {
			return name;
		}
		
		public String getDataTypeURL() {
			return siteUrl;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	private class VCMetaDataMiriamResource implements MiriamResource {
		private MIRIAMRef sybilRef = null;
		public VCMetaDataMiriamResource(MIRIAMRef argSybilRef){
			this.sybilRef = argSybilRef;
		}
		public DataType getDataType() {
			DataType dataType = VCMetaDataDataType.all.get("urn:miriam:"+sybilRef.type());
			return dataType;
		}
		public String getMiriamURN() {
			return sybilRef.urn();
		}
		public String getIdentifier() {
			return sybilRef.id();
		}
	}
	
	private class VCMetaDataMiriamRefGroup implements MiriamRefGroup {
		private RefGroup sybilRefGroup = null;
		
		public VCMetaDataMiriamRefGroup(RefGroup argSybilRefGroup){
			this.sybilRefGroup = argSybilRefGroup;
		}
		
		public Set<MiriamResource> getMiriamRefs() {
			HashSet<MiriamResource> set = new HashSet<MiriamResource>();
			for (MIRIAMRef miriamRef : sybilRefGroup.refs()){
				set.add(new VCMetaDataMiriamResource(miriamRef));
			}
			return set;
		}

		public RefGroup getSybilRefGroup() {
			return sybilRefGroup;
		}

	}
	

	private final VCMetaData vcMetaData;
	private TreeMap<Identifiable,Map<MiriamRefGroup,MIRIAMQualifier>> miriamTreeMap = null;
	
	public VCMetaDataMiriamManager(VCMetaData arg_vcmetadata){
		this.vcMetaData = arg_vcmetadata;
	}

	public void addMiriamRefGroup(Identifiable identifiable, MIRIAMQualifier miriamQualifier, Set<MiriamResource> miriamResources) throws URNParseFailureException {
		OpenEntry entry = vcMetaData.getRegistry().getEntry(identifiable);
		NamedThing namedThing = entry.getNamedThing();
		if (entry.getNamedThing() == null){
			String newURI = vcMetaData.getRegistry().generateFreeURI(identifiable);
			namedThing = entry.setNamedThingFromURI(newURI);
		}
		MIRIAMizer miriamizer = new MIRIAMizerImp();
		RefGroup sybilRefGroup = miriamizer.newRefGroup(namedThing, miriamQualifier);
		Set<MIRIAMRef> sybilMiriamRefs = new HashSet<MIRIAMRef>();
		for(MiriamResource miriamResource : miriamResources) {
			sybilMiriamRefs.add(MIRIAMRef.createFromURN(miriamResource.getMiriamURN()));
		}
		for(MIRIAMRef sybilMiriamRef : sybilMiriamRefs) {
			sybilRefGroup.add(sybilMiriamRef);			
		}
		invalidateCache(identifiable);
		vcMetaData.fireAnnotationEventListener(new VCMetaData.AnnotationEvent(identifiable));
	}
	
	public Map<String,DataType> getAllDataTypes() {
		return Collections.unmodifiableMap(VCMetaDataDataType.all);
	}


	public synchronized TreeMap<Identifiable,Map<MiriamRefGroup,MIRIAMQualifier>> getMiriamTreeMap(){
		if (miriamTreeMap==null){
			final IdentifiableProvider identifiableProvider = vcMetaData.getIdentifiableProvider();
			miriamTreeMap = new TreeMap<Identifiable,Map<MiriamRefGroup,MIRIAMQualifier>>(
				new MiriamTreeModel.IdentifiableComparator(identifiableProvider)
			);
			Set<Entry> allEntries = vcMetaData.getRegistry().getAllEntries();
			for (Entry entry : allEntries){
				if (entry.getNamedThing()!=null){
					Map<MiriamRefGroup,MIRIAMQualifier> refGroupMap = queryAllMiriamRefGroups(entry.getIdentifiable());
					if (refGroupMap.size()>0){
						miriamTreeMap.put(entry.getIdentifiable(), refGroupMap);
					}
				}
			}
		}
		return miriamTreeMap;
	}

	public Map<MiriamRefGroup,MIRIAMQualifier> getAllMiriamRefGroups(Identifiable identifiable) {
		return getMiriamTreeMap().get(identifiable);
	}
	

	public Set<MiriamRefGroup> getMiriamRefGroups(Identifiable identifiable, MIRIAMQualifier miriamQualifier) {
		// get from cache.
		Map<MiriamRefGroup,MIRIAMQualifier> map = getMiriamTreeMap().get(identifiable);
		HashSet<MiriamRefGroup> matchingRefGroups = new HashSet<MiriamRefGroup>();
		if (map!=null && map.size()>0){
			Set<MiriamRefGroup> allStoredRefGroups = map.keySet();
			if (map!=null && map.size()>0){
				for (MiriamRefGroup storedRefGroup : allStoredRefGroups){
					MIRIAMQualifier storedQualifier = map.get(storedRefGroup);
					if (storedQualifier.equals(miriamQualifier)){
						matchingRefGroups.add(storedRefGroup);
					}
				}
			}
		}
		return matchingRefGroups;
	}
		
	private Set<MiriamRefGroup> queryMiriamRefGroups(Identifiable identifiable, MIRIAMQualifier miriamQualifier) {
		// get from jena. (not used currently).
		OpenEntry entry = vcMetaData.getRegistry().getEntry(identifiable);
		if (entry.getNamedThing() == null){
			return null;
		}
		MIRIAMizer miriamizer = new MIRIAMizerImp();
		Set<RefGroup> refGroups = miriamizer.getRefGroups(entry.getNamedThing(),miriamQualifier);
		Set<MiriamRefGroup> newRefGroups = new HashSet<MiriamRefGroup>();
		for (RefGroup sybilRefGroup : refGroups){
			newRefGroups.add(new VCMetaDataMiriamRefGroup(sybilRefGroup));
		}
		return newRefGroups;
	}
	
	private Map<MiriamRefGroup,MIRIAMQualifier> queryAllMiriamRefGroups(Identifiable identifiable) {
		OpenEntry entry = vcMetaData.getRegistry().getEntry(identifiable);
		if (entry.getNamedThing() == null){
			return null;
		}
		MIRIAMizer miriamizer = new MIRIAMizerImp();
		Map<RefGroup, MIRIAMQualifier> allRefGroups = miriamizer.getAllRefGroups(entry.getNamedThing());
		Map<MiriamRefGroup,MIRIAMQualifier> wrappedRefGroups = new HashMap<MiriamRefGroup,MIRIAMQualifier>();
		for (RefGroup refGroup : allRefGroups.keySet()){
			MiriamRefGroup miriamRefGroup = new VCMetaDataMiriamRefGroup(refGroup);
			MIRIAMQualifier miriamQualifier = allRefGroups.get(refGroup);
			wrappedRefGroups.put(miriamRefGroup, miriamQualifier);
		}
		return wrappedRefGroups;
	}

	public void remove(Identifiable identifiable, MIRIAMQualifier miriamQualifier, MiriamRefGroup miriamRefGroup) throws URNParseFailureException {
		OpenEntry openEntry = vcMetaData.getRegistry().getEntry(identifiable);
		NamedThing namedThing = openEntry.getNamedThing();
		MIRIAMizer miriamizer = new MIRIAMizerImp();
		RefGroup sybilRefGroup = miriamizer.newRefGroup(namedThing, miriamQualifier);
		Set<MIRIAMRef> sybilMiriamRefs = new HashSet<MIRIAMRef>();
		Set<MiriamResource> miriamResources = miriamRefGroup.getMiriamRefs();
		for(MiriamResource miriamResource : miriamResources) {
			sybilMiriamRefs.add(MIRIAMRef.createFromURN(miriamResource.getMiriamURN()));
		}
		for(MIRIAMRef sybilMiriamRef : sybilMiriamRefs) {
			sybilRefGroup.add(sybilMiriamRef);			
		}
		miriamizer.deleteRefGroup(namedThing, sybilRefGroup);
		invalidateCache(identifiable);
		vcMetaData.fireAnnotationEventListener(new VCMetaData.AnnotationEvent(identifiable));
	}

	public Set<URL> getStoredCrossReferencedLinks(MiriamResource miriamResource) {
		throw new RuntimeException("not yet implemented");
	}

	public MiriamResource createMiriamResource(String urnString) throws URNParseFailureException{
		return new VCMetaDataMiriamResource(MIRIAMRef.createFromURN(urnString));
	}

	private void invalidateCache() {
		miriamTreeMap = null;
	}

	private void invalidateCache(Identifiable identifiable) {
		if (miriamTreeMap==null){
			return; 
		}else{
			miriamTreeMap.put(identifiable, queryAllMiriamRefGroups(identifiable));
		}
	}

	public void addDate(Identifiable identifiable, DateQualifier dateQualifier,	DublinCoreDate date) {
		OpenEntry entry = vcMetaData.getRegistry().getEntry(identifiable);
		if (entry.getNamedThing() == null){
			String newURI = vcMetaData.getRegistry().generateFreeURI(identifiable);
			entry.setNamedThingFromURI(newURI);
		}
		Model rdfData = vcMetaData.getRdfData();
		Literal dateLiteral = rdfData.createLiteral(date.getDateString());
		rdfData.add(entry.getNamedThing().resource(), dateQualifier.property(), dateLiteral);
		vcMetaData.fireAnnotationEventListener(new VCMetaData.AnnotationEvent(identifiable));
	}

	// this is not yet cached.
	public Map<Identifiable, Map<DateQualifier, Set<DublinCoreDate>>> getDublinCoreDateMap() {
		Map<Identifiable, Map<DateQualifier, Set<DublinCoreDate>>> map =
			new HashMap<Identifiable, Map<DateQualifier, Set<DublinCoreDate>>>();
		Set<Entry> allEntries = vcMetaData.getRegistry().getAllEntries();
		Model rdfData = vcMetaData.getRdfData();
		for (Entry entry : allEntries){
			NamedThing sbThing = entry.getNamedThing();
			if (sbThing != null){
				Identifiable identifiable = entry.getIdentifiable();
				Map<DateQualifier, Set<DublinCoreDate>> qualifierDateMap = new HashMap<DateQualifier, Set<DublinCoreDate>>();
				for(DublinCoreQualifier.DateQualifier dateQualifier : AnnotationQualifiers.DC_date_all){
					StmtIterator stmtIter = 
						rdfData.listStatements(sbThing.resource(), dateQualifier.property(), 
								(RDFNode) null);
					Set<DublinCoreDate> dateStrings = new HashSet<DublinCoreDate>();
					while(stmtIter.hasNext()) {
						Statement statement = stmtIter.nextStatement();
						RDFNode dateObject = statement.getObject();
						if(dateObject instanceof Literal) {
							Literal dateLiteral = (Literal) dateObject;
							String dateString = dateLiteral.getLexicalForm();
							dateStrings.add(new DublinCoreDate(dateString));
						}
					}
					if(!dateStrings.isEmpty()) {
						qualifierDateMap.put(dateQualifier, dateStrings);
					}
				}
				if(!qualifierDateMap.isEmpty()) {
					map.put(identifiable, qualifierDateMap);
				}
			}
		}

		return map;
	}

	public void addCreatorToAnnotation(Identifiable identifiable, String familyName, String givenName, String email, String organization) {
		throw new RuntimeException("support for 'Creator' annotation not yet implemented");
		//vcMetaData.fireAnnotationEventListener(new AnnotationEvent(identifiable));
	}

}
