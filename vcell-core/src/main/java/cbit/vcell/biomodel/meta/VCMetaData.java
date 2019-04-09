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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jdom.Element;
import org.jdom.Namespace;
import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.util.SesameRioUtil;
import org.vcell.relationship.AnnotationMapping;
import org.vcell.sybil.models.annotate.JDOM2Model;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.util.Compare;
import org.vcell.util.document.Identifiable;
import org.vcell.util.document.KeyValue;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.biomodel.meta.registry.Registry;
import cbit.vcell.biomodel.meta.registry.Registry.Entry;
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDFWriter;
import cbit.vcell.xml.XMLTags;

/**
 * manager for Notes, Annotations, SBOTerms, and all other meta data regarding 
 * biomodel entities as well as pathway models.
 * @author schaff, ruebenacker
 *
 */

@SuppressWarnings("serial")
public class VCMetaData implements Serializable {
	public interface AnnotationEventListener {
		void annotationChanged(AnnotationEvent annotationEvent);
	}
	public static class AnnotationEvent extends EventObject {
		private boolean bPathwayChange = false;
		public AnnotationEvent(Identifiable identifiable) {
			this(identifiable, false);
		}
		public AnnotationEvent(Identifiable identifiable, boolean bPathwayChange) {
			super(identifiable);
			this.bPathwayChange = bPathwayChange;
		}
		public Identifiable getIdentifiable(){
			return (Identifiable)getSource();
		}
		public boolean isPathwayChange() {
			return bPathwayChange;
		}
	}
	
	private List<AnnotationEventListener> annotationEventListeners = 
		new ArrayList<AnnotationEventListener>();
	
	public static final Namespace nsVCML = Namespace.getNamespace("vcml",XMLTags.VCML_NS);

	protected IdentifiableProvider identifiableProvider;

	protected Graph graph = new HashGraph();
	protected Registry registry;
	private IdentityHashMap<Entry, NonRDFAnnotation> nonRDFAnnotationMap =
				new IdentityHashMap<Entry, NonRDFAnnotation>();
	private KeyValue keyValue = null;
	
	public VCMetaData(IdentifiableProvider arg_IdentifiableProvider, KeyValue key){
		this.identifiableProvider = arg_IdentifiableProvider;
		this.keyValue = key;
		registry = new Registry(identifiableProvider);
	}

	public Graph getRdfData() { return graph; }
	
	public Graph getRdfDataCopy() {
		Graph rdfModelCopy = new HashGraph();
		rdfModelCopy.addAll(getRdfData());
		return rdfModelCopy;
	}
	
	public String getBaseURI() { return XMLTags.METADATA_NS; }
	public String getBaseURIExtended() { return XMLTags.METADATA_NS_EXTENDED; }
	public Registry getRegistry() { return registry; }
	
	public VCMetaDataMiriamManager miriamManager = new VCMetaDataMiriamManager(this);
	
	public boolean compareEquals(VCMetaData vcMetaData) {
		if (!getRdfData().equals(vcMetaData.getRdfData())) {
			return false; 
		}
		if (!registry.compareEquals(vcMetaData.registry)) {
			return false;
		}
		
		Set<Entry> oeSet = nonRDFAnnotationMap.keySet();
		Set<VCID> vcidSet = new HashSet<VCID>();
		for (Entry oe : oeSet) {
			VCID vcid = identifiableProvider.getVCID(oe.getIdentifiable());
			vcidSet.add(vcid);
		}
		Set<Entry> otherOeSet =  vcMetaData.nonRDFAnnotationMap.keySet();
		for (Entry oe : otherOeSet) {
			VCID vcid = vcMetaData.identifiableProvider.getVCID(oe.getIdentifiable());
			vcidSet.add(vcid);
		}		
		NonRDFAnnotation emtpyAnnotation = new NonRDFAnnotation();
		for (VCID vcid : vcidSet) {
			Identifiable myIdentifiable = identifiableProvider.getIdentifiableObject(vcid);
			Identifiable otherIdentifiable = vcMetaData.identifiableProvider.getIdentifiableObject(vcid);
			if ((otherIdentifiable == null && myIdentifiable != null) || 
					(myIdentifiable == null && otherIdentifiable != null)) {				
				return false;
			}
			
			NonRDFAnnotation nonRDFAnnotation = nonRDFAnnotationMap.get(registry.getEntry(myIdentifiable));
			if (nonRDFAnnotation == null) {
				nonRDFAnnotation = emtpyAnnotation;
			}
			NonRDFAnnotation otherNonRDFAnnotation = 
				vcMetaData.nonRDFAnnotationMap.get(vcMetaData.registry.getEntry(otherIdentifiable));
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

	private NonRDFAnnotation getExistingNonRDFAnnotation(Identifiable identifiable){
		Entry entry = registry.getEntry(identifiable);
		NonRDFAnnotation nonRDFAnnotation = nonRDFAnnotationMap.get(entry);
		return nonRDFAnnotation;
	}
		
	private NonRDFAnnotation getOrCreateNonRDFAnnotation(Identifiable identifiable){
		Entry entry = registry.getEntry(identifiable);
		NonRDFAnnotation nonRDFAnnotation = nonRDFAnnotationMap.get(entry);
		if (nonRDFAnnotation==null){
			nonRDFAnnotation = new NonRDFAnnotation();
			nonRDFAnnotationMap.put(entry, nonRDFAnnotation);
		}
		return nonRDFAnnotation;
	}
		
	public void add(Graph rdfModel){
		getRdfData().addAll(rdfModel);
		miriamManager.invalidateCache();
	}
	
	public void cleanupMetadata() {
		Set<Registry.Entry> entries = registry.getAllEntries();
		for (Registry.Entry entry : entries) {
			Identifiable entryIdentifiable = entry.getIdentifiable();
			VCID vcid = identifiableProvider.getVCID(entryIdentifiable);
			if(vcid == null) {
				continue;
			}
			Identifiable identifiable = identifiableProvider.getIdentifiableObject(vcid);
			if (identifiable == null) {
				// use miriamManager to remove RDF statements from resource for identifiable
				try {
					Map<MiriamRefGroup, MIRIAMQualifier> miriamRefGps = 
						getMiriamManager().getAllMiriamRefGroups(entryIdentifiable);
					if (miriamRefGps != null) {
						for (Map.Entry<MiriamRefGroup, MIRIAMQualifier> groupEntry :  
							miriamRefGps.entrySet()) {
							MiriamRefGroup refGroup = groupEntry.getKey();
							MIRIAMQualifier qualifier = groupEntry.getValue();
							getMiriamManager().remove(entryIdentifiable, qualifier, refGroup);
						}
					}
				} catch (URNParseFailureException e) {
					e.printStackTrace(System.out);
				}
				// set nonRDF annotations to null
				NonRDFAnnotation nonRDFAnnotation = getExistingNonRDFAnnotation(entryIdentifiable);
				if(nonRDFAnnotation != null) {
					nonRDFAnnotation.setFreeTextAnnotation(null);
					nonRDFAnnotation.setXhtmlNotes(null);
					nonRDFAnnotation.setXmlAnnotations(null);					
				}
				System.err.println("Deleting resource for identifiable '" + entryIdentifiable.toString() 
						+ "' since it is not found in " + identifiableProvider.getClass().getName());
			}
		}
	}
	
	public Set<Map.Entry<Entry, NonRDFAnnotation>> getAllNonRDFAnnotations(){
		Set<Map.Entry<Entry, NonRDFAnnotation>> entrySet = nonRDFAnnotationMap.entrySet();
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
		NonRDFAnnotation nonRDFAnnotation = getExistingNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			return nonRDFAnnotation.getXhtmlNotes();
		}
		return null;
	}

	public void setXhtmlNotes(Identifiable identifiable, Element xhtmlNotes) {
		NonRDFAnnotation nonRDFAnnotation = getOrCreateNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			nonRDFAnnotation.setXhtmlNotes(xhtmlNotes);
			fireAnnotationEventListener(new AnnotationEvent(identifiable));
		}
	}
	
	public Element[] getXmlAnnotations(Identifiable identifiable) {
		NonRDFAnnotation nonRDFAnnotation = getExistingNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			return nonRDFAnnotation.getXmlAnnotations();
		}
		return null;
	}
	
	public void setXmlAnnotations(Identifiable identifiable, Element[] xmlAnnotations) {
		NonRDFAnnotation nonRDFAnnotation = getOrCreateNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			nonRDFAnnotation.setXmlAnnotations(xmlAnnotations);
			fireAnnotationEventListener(new AnnotationEvent(identifiable));
		}
	}

	public String getFreeTextAnnotation(Identifiable identifiable) {
		NonRDFAnnotation nonRDFAnnotation = getExistingNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			return nonRDFAnnotation.getFreeTextAnnotation();
		}
		return null;
	}
	
	public void setFreeTextAnnotation(Identifiable identifiable, String text) {
		NonRDFAnnotation nonRDFAnnotation = getOrCreateNonRDFAnnotation(identifiable);
		if (nonRDFAnnotation != null){
			nonRDFAnnotation.setFreeTextAnnotation(text);
		}
		fireAnnotationEventListener(new AnnotationEvent(identifiable));
	}
	
	public MiriamManager getMiriamManager(){
		return miriamManager;
	}

	public Element createElement() {
		return XMLRDFWriter.createElement(getRdfData(), getBaseURI());
	}

	public void addToModelFromElement(Element element) throws RDFParseException, RDFHandlerException, IOException {
		JDOM2Model jdom2model = new JDOM2Model(getRdfData());
		jdom2model.addJDOM(element, getBaseURI());
	}
	
	public String printRdfStatements(){
		StringBuffer strBuffer = new StringBuffer();
		for(Statement st : getRdfData()) {
			strBuffer.append(st.getSubject()+";\t" + st.getPredicate()+";\t" + st.getObject()+"\n");
		}
		return strBuffer.toString();
	}
	
	public String printRdfPretty() throws RDFHandlerException{
		Map<String, String> nsMap = DefaultNameSpaces.defaultMap.convertToMap();
		return SesameRioUtil.writeRDFToString(getRdfData(), nsMap, RDFFormat.N3);
	}

	public void addPathwayModel(BioModel bioModel, Graph model) {
		getRdfData().addAll(model);
		fireAnnotationEventListener(new AnnotationEvent(bioModel, true));
	}
	
	public void createBioPaxObjects(BioModel bioModel){
		AnnotationMapping annoMapping = new AnnotationMapping();
		VCMetaData vcMetaData = bioModel.getVCMetaData();
		Set<Identifiable> identifiables = vcMetaData.getIdentifiableProvider().getAllIdentifiables();
		TreeMap<Identifiable, Map<MiriamRefGroup, MIRIAMQualifier>> miriamDescrHeir = miriamManager.getMiriamTreeMap();
		for (Identifiable identifiable : identifiables){
			Map<MiriamRefGroup, MIRIAMQualifier> refGroupMap = miriamDescrHeir.get(identifiable);
			if (refGroupMap!=null){
				String info = annoMapping.annotation2BioPaxObject(bioModel, identifiable);
				boolean printInfo = false;
				if(printInfo) { System.out.println(info); }
			}
		}
	}

}
