package cbit.vcell.biomodel.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.factories.SBBoxFactory;
import org.vcell.util.Compare;
import org.vcell.util.document.KeyValue;

import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.registry.OpenRegistry;
import cbit.vcell.biomodel.meta.registry.Registry;
import cbit.vcell.biomodel.meta.registry.VCellThingFactory;
import cbit.vcell.biomodel.meta.registry.OpenRegistry.OpenEntry;
import cbit.vcell.xml.XMLTags;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * manager for Notes, Annotations, SBOTerms, and all other meta data regarding 
 * biomodel entities as well as pathway models.
 * @author schaff, ruebenacker
 *
 */

public class VCMetaData implements Serializable {
	public interface AnnotationEventListener {
		void annotationChanged(AnnotationEvent annotationEvent);
	}
	public static class AnnotationEvent extends java.util.EventObject {
		public AnnotationEvent(Identifiable identifiable) {
			super(identifiable);
		}
		public Identifiable getIdentifiable(){
			return (Identifiable)getSource();
		}
	}
	private ArrayList<AnnotationEventListener> annotationEventListeners = new ArrayList<AnnotationEventListener>();
	
	public static final Namespace nsVCML = Namespace.getNamespace("vcml",XMLTags.VCML_NS);

	protected IdentifiableProvider identifiableProvider;

	protected SBBox rdfBox = SBBoxFactory.create();
	protected OpenRegistry registry;
	private IdentityHashMap<OpenEntry, NonRDFAnnotation> nonRDFAnnotationMap =
				new IdentityHashMap<OpenEntry, NonRDFAnnotation>();
	private KeyValue keyValue = null;
	
	public VCMetaData(IdentifiableProvider arg_IdentifiableProvider, KeyValue key){
		this.identifiableProvider = arg_IdentifiableProvider;
		this.keyValue = key;
		registry = new OpenRegistry(new VCellThingFactory(rdfBox), identifiableProvider);
	}

	public SBBox getSBbox() { return rdfBox; }
	
	public Model getRdfData() { return rdfBox.getData(); }
	public String getBaseURI() { return XMLTags.METADATA_NS; }
	public String getBaseURIExtended() { return XMLTags.METADATA_NS_EXTENDED; }
	public OpenRegistry getRegistry() { return registry; }
	
	public VCMetaDataMiriamManager miriamManager = new VCMetaDataMiriamManager(this);
	
	public boolean compareEquals(VCMetaData vcMetaData) {
		if (!getRdfData().isIsomorphicWith(vcMetaData.getRdfData())) {
			return false; 
		}
		if (!registry.compareEquals(vcMetaData.registry)) {
			return false;
		}
		
		Set<OpenEntry> oeSet = nonRDFAnnotationMap.keySet();
		Set<VCID> vcidSet = new HashSet<VCID>();
		for (OpenEntry oe : oeSet) {
			VCID vcid = identifiableProvider.getVCID(oe.getIdentifiable());
			vcidSet.add(vcid);
		}
		Set<OpenEntry> otherOeSet =  vcMetaData.nonRDFAnnotationMap.keySet();
		for (OpenEntry oe : otherOeSet) {
			VCID vcid = vcMetaData.identifiableProvider.getVCID(oe.getIdentifiable());
			vcidSet.add(vcid);
		}		
		NonRDFAnnotation emtpyAnnotation = new NonRDFAnnotation();
		for (VCID vcid : vcidSet) {
			Identifiable myIdentifiable = identifiableProvider.getIdentifiableObject(vcid);
			Identifiable otherIdentifiable = vcMetaData.identifiableProvider.getIdentifiableObject(vcid);
			if (otherIdentifiable == null && myIdentifiable != null || myIdentifiable == null && otherIdentifiable != null) {				
				return false;
			}
			
			NonRDFAnnotation nonRDFAnnotation = nonRDFAnnotationMap.get(registry.getEntry(myIdentifiable));
			if (nonRDFAnnotation == null) {
				nonRDFAnnotation = emtpyAnnotation;
			}
			NonRDFAnnotation otherNonRDFAnnotation = vcMetaData.nonRDFAnnotationMap.get(vcMetaData.registry.getEntry(otherIdentifiable));
			if (otherNonRDFAnnotation == null) {
				otherNonRDFAnnotation = emtpyAnnotation;
			}
			if (!Compare.isEqual(nonRDFAnnotation, otherNonRDFAnnotation)) {
				return false;
			}
		}
		return true;
	}
	
	public IdentifiableProvider getIdentifiableProvider() {
		return identifiableProvider;
	}

	public NonRDFAnnotation getNonRDFAnnotation(Identifiable identifiable){
		OpenEntry entry = registry.getEntry(identifiable);
		NonRDFAnnotation nonRDFAnnotation = nonRDFAnnotationMap.get(entry);
		if (nonRDFAnnotation==null){
			nonRDFAnnotation = new NonRDFAnnotation();
			nonRDFAnnotationMap.put(entry, nonRDFAnnotation);
		}
		return nonRDFAnnotation;
	}
		
	public void cleanupMetadata() {
		Set<Registry.Entry> entries = registry.getAllEntries();
		for (Registry.Entry entry : entries) {
			Identifiable entryIdentifiable = entry.getIdentifiable();
			VCID vcid = identifiableProvider.getVCID(entryIdentifiable);
			Identifiable identifiable = identifiableProvider.getIdentifiableObject(vcid);
			if (identifiable == null) {
				// use miriamManager to remove RDF statements from resource for identifiable
				try {
					Map<MiriamRefGroup, MIRIAMQualifier> miriamRefGps = getMiriamManager().getAllMiriamRefGroups(entryIdentifiable);
					if (miriamRefGps != null) {
						for (Entry<MiriamRefGroup, MIRIAMQualifier> groupEntry :  miriamRefGps.entrySet()) {
							MiriamRefGroup refGroup = groupEntry.getKey();
							MIRIAMQualifier qualifier = groupEntry.getValue();
							getMiriamManager().remove(entryIdentifiable, qualifier, refGroup);
						}
					}
				} catch (URNParseFailureException e) {
					e.printStackTrace(System.out);
				}
				// set nonRDF annotatoins to null
				NonRDFAnnotation nonRDFAnnotation = getNonRDFAnnotation(entryIdentifiable);
				nonRDFAnnotation.setFreeTextAnnotation(null);
				nonRDFAnnotation.setXhtmlNotes(null);
				nonRDFAnnotation.setXmlAnnotations(null);
				System.err.println("Deleting resource for identifiable '" + entryIdentifiable.toString() + "' since it is not foind in " + identifiableProvider.getClass().getName());
			}
		}
	}
	
	public Set<Entry<OpenEntry, NonRDFAnnotation>> getAllNonRDFAnnotations(){
		Set<Entry<OpenEntry, NonRDFAnnotation>> entrySet = nonRDFAnnotationMap.entrySet();
		return Collections.unmodifiableSet(entrySet);
	}
	
	public void addAnnotationEventListener(AnnotationEventListener listener){
		if (annotationEventListeners.contains(listener)){
			return;
		}
		this.annotationEventListeners.add(listener);
	}
	public void removeAnnotationEventListener(AnnotationEventListener listener){
		annotationEventListeners.remove(listener);
	}
	void fireAnnotationEventListener(AnnotationEvent annotationEvent){
		for (AnnotationEventListener listener : annotationEventListeners){
			listener.annotationChanged(annotationEvent);
		}
	}
	public KeyValue getKey(){
		return this.keyValue;
	}

	// Accessors for fields of NonRDFAnootations
	public Element getXhtmlNotes(Identifiable identifiable) {
		NonRDFAnnotation nonRDFAnnotation = getNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			return nonRDFAnnotation.getXhtmlNotes();
		}
		return null;
	}

	public void setXhtmlNotes(Identifiable identifiable, Element xhtmlNotes) {
		NonRDFAnnotation nonRDFAnnotation = getNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			nonRDFAnnotation.setXhtmlNotes(xhtmlNotes);
			fireAnnotationEventListener(new AnnotationEvent(identifiable));
		}
	}
	
	public Element[] getXmlAnnotations(Identifiable identifiable) {
		NonRDFAnnotation nonRDFAnnotation = getNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			nonRDFAnnotation.getXmlAnnotations();
		}
		return null;
	}
	
	public void setXmlAnnotations(Identifiable identifiable, Element[] xmlAnnotations) {
		NonRDFAnnotation nonRDFAnnotation = getNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			nonRDFAnnotation.setXmlAnnotations(xmlAnnotations);
			fireAnnotationEventListener(new AnnotationEvent(identifiable));
		}
	}

	public String getFreeTextAnnotation(Identifiable identifiable) {
		NonRDFAnnotation nonRDFAnnotation = getNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			return nonRDFAnnotation.getFreeTextAnnotation();
		}
		return null;
	}
	
	public void setFreeTextAnnotation(Identifiable identifiable, String text) {
		NonRDFAnnotation nonRDFAnnotation = getNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			nonRDFAnnotation.setFreeTextAnnotation(text);
		}
		fireAnnotationEventListener(new AnnotationEvent(identifiable));
	}
	
	public MiriamManager getMiriamManager(){
		return miriamManager;
	}
	
}
