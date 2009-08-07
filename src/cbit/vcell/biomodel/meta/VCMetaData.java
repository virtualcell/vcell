package cbit.vcell.biomodel.meta;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.Text;
import org.vcell.sybil.rdf.RDFBox;
import org.vcell.util.document.KeyValue;

import sun.misc.VM;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.registry.OpenRegistry;
import cbit.vcell.biomodel.meta.registry.OpenRegistry.OpenEntry;
import cbit.vcell.biomodel.meta.xml.XMLMetaData;
import cbit.vcell.xml.XMLTags;

/**
 * manager for Notes, Annotations, SBOTerms, and all other meta data regarding 
 * biomodel entities as well as pathway models.
 * @author schaff, ruebenacker
 *
 */

public class VCMetaData implements RDFBox {
	
	protected BioModel bioModel;
	protected RDFBox rdfBox = new RDFBox.Default();
	protected String baseURI;
	protected OpenRegistry registry = new OpenRegistry();
	private IdentityHashMap<OpenEntry, NonRDFAnnotation> nonRDFAnnotationMap =
				new IdentityHashMap<OpenEntry, NonRDFAnnotation>();
	private KeyValue keyValue = null;
	
	public VCMetaData(BioModel bioModel, String baseURI, KeyValue key){
		this.bioModel = bioModel;
		this.baseURI = baseURI;
		this.keyValue = key;
	}

	public Model getRdf() { return rdfBox.getRdf(); }
	public String getBaseURI() { return baseURI; }
	public OpenRegistry getRegistry() { return registry; }
	
	public boolean compareEquals(VCMetaData vcMetaData) {
		return getRdf().isIsomorphicWith(vcMetaData.getRdf()) && 
		registry.compareEquals(vcMetaData.registry);
	}
	
	private Resource getResource(Identifiable identifiable){
		return registry.forObject(identifiable).resource();
	}
	
	private Property getProperty(URI propertyURI){
		Property property = getRdf().getProperty(propertyURI.getPath());
		if (property == null){
			property = getRdf().createProperty(propertyURI.getPath());
		}
		// statement
		return property;
	}
	
	public List<Statement> getStatements(Identifiable identifiable){
		System.out.println("looking for statements for identifiable : "+VCID.getVCID(bioModel, identifiable).toASCIIString());
		final Resource resource = getResource(identifiable);
		if (resource==null){
			System.out.println(".....no resouce found");
			return null;
		}
		System.out.println(".....resouce \""+resource+"\" ("+resource.hashCode()+")");
		Selector selector = new Selector() {
			public RDFNode getObject() {return null;}
			public Property getPredicate() {return null;}
			public Resource getSubject() {return null;}
			public boolean isSimple() {return false;}
			public boolean test(Statement arg0) {
				Resource subject = arg0.getSubject();
				if (subject.getNameSpace()!=null && !subject.getNameSpace().toString().equals("#") && !subject.getNameSpace().equals(resource.getNameSpace())){
					System.out.println("resouce mismatch: "+subject+"/"+resource+":   namespace "+subject.getNameSpace()+" differs from "+resource.getNameSpace());
					return false;
				}
				if (subject.toString().startsWith("#") && resource.getNameSpace().equals("http://vcell.org/data/")){
					String rest = subject.toString().substring(1);
					if (resource.toString().endsWith(rest)){
						return true;
					}
				}
				return false;
			}
			
		};
		StmtIterator stmtIter = getRdf().listStatements(selector);
//		StmtIterator stmtIter = getRdf().listStatements(resource,null,(RDFNode)null);
//		StmtIterator stmtIter = getRdf().listStatements(null,null,(RDFNode)null);
		List<Statement> statements = new ArrayList<Statement>();
if (statements.size()==0){
	System.out.println(".....no statements found");
}
		while (stmtIter.hasNext()){
			Statement statement = stmtIter.nextStatement();
			System.out.println("....."+statement.getSubject()+"("+statement.getSubject().hashCode()+")"+
								" : "+statement.getPredicate()+"("+statement.getPredicate().hashCode()+")"+
								" : "+statement.getObject()+"("+statement.getObject().hashCode()+")");
			statements.add(statement);
		}
		System.out.println("");
		return statements;
	}
	
	public void deleteStatement(Statement statement){
		getRdf().remove(statement);
	}

	public void addRDFStatement(Identifiable identifiable, URI propertyURI, URI objectURI) {
		OpenEntry entry = registry.forObject(identifiable);
		Resource resource = entry.resource();
		if (resource==null){
			resource = getRdf().createResource(XMLMetaData.nsVCML + "/" + identifiable.getClass().getName()
					+ "/" +(Math.abs((new Random()).nextInt())));
			entry.setResource(resource);
		}
		Property predicate = getProperty(propertyURI);
		RDFNode object = getProperty(objectURI);
		Statement statement = getRdf().createStatement(resource, predicate, object);
		System.out.println("VCMetaData.addRDFStatement(): "+statement.toString());
		getRdf().add(statement);
	}

	public NonRDFAnnotation getNonRDFAnnotation(Identifiable identifiable){
		OpenEntry entry = registry.forObject(identifiable);
		NonRDFAnnotation nonRDFAnnotation = nonRDFAnnotationMap.get(entry);
		if (nonRDFAnnotation==null){
			nonRDFAnnotation = new NonRDFAnnotation();
			nonRDFAnnotationMap.put(entry, nonRDFAnnotation);
		}
		return nonRDFAnnotation;
	}
		
	public Set<Entry<OpenEntry, NonRDFAnnotation>> getAllNonRDFAnnotations(){
		Set<Entry<OpenEntry, NonRDFAnnotation>> entrySet = nonRDFAnnotationMap.entrySet();
		return Collections.unmodifiableSet(entrySet);
	}
	
	public void addDateToAnnotation(Identifiable identifiable, String text, String selectedItem) {
		// TODO Auto-generated method stub
	}

	public void addCreatorToAnnotation(Identifiable identifiable,
			String familyName, String givenName, String email,
			String organization) {
		// TODO Auto-generated method stub
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
	}

	/** creating XMLMetaData element for nonRDFAnnotation element here since nonRDFAnnotation was made package level
	 *	this is similar to XMLRDFWriter.createElement(metaData)
	 * 
	 * @return the created NonRDFAnnotationListElement
	 */
	public Element createNonRDFAnnotationElement() {
		Set<Entry<OpenEntry, NonRDFAnnotation>> allNonRdfAnnotations = getAllNonRDFAnnotations();
		Element nonRDFAnnotationListElement = new Element(XMLMetaData.NONRDF_ANNOTATION_LIST_TAG);
		Iterator<Entry<OpenEntry, NonRDFAnnotation>> iter = allNonRdfAnnotations.iterator();
		while (iter.hasNext()){
			Entry<OpenEntry, NonRDFAnnotation> entry = iter.next();
			OpenEntry openEntry = entry.getKey();
			NonRDFAnnotation nonRDFAnnotation = entry.getValue();
			if (!nonRDFAnnotation.isEmpty()){
				Element nonRDFAnnotationElement = new Element(XMLMetaData.NONRDF_ANNOTATION_TAG);
				nonRDFAnnotationElement.setAttribute(XMLMetaData.VCID_ATTR_TAG, VCID.getVCID(bioModel, (Identifiable)openEntry.object()).toASCIIString(), XMLMetaData.nsVCML);
				nonRDFAnnotationListElement.addContent(nonRDFAnnotationElement);
				String freeTextAnnotation = nonRDFAnnotation.getFreeTextAnnotation();
				if (freeTextAnnotation!=null && freeTextAnnotation.length()>0){
					Element freeTextAnnotationElement = new Element(XMLMetaData.FREETEXT_TAG);
					freeTextAnnotationElement.addContent(new Text(freeTextAnnotation));
					nonRDFAnnotationElement.addContent(freeTextAnnotationElement);
				}
				Element xhtmlNotes = nonRDFAnnotation.getXhtmlNotes();
				if (xhtmlNotes!=null){
					Element notesElement = new Element(XMLMetaData.NOTES_TAG);
					notesElement.addContent(new Text(freeTextAnnotation));
					nonRDFAnnotationElement.addContent(notesElement);
				}
				Element[] otherAnnotations = nonRDFAnnotation.getXmlAnnotations();
				if (otherAnnotations!=null && otherAnnotations.length>0){
					Element annotationListElement = new Element(XMLMetaData.ANNOTATION_LIST_TAG);
					nonRDFAnnotationElement.addContent(annotationListElement);
					for (int i = 0; i < otherAnnotations.length; i++) {
						annotationListElement.addContent(otherAnnotations[i]);
					}
				}
			}
		}
		return nonRDFAnnotationListElement;
	}
}
