package cbit.vcell.biomodel.meta;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.sybil.rdf.RDFBox;
import org.vcell.util.document.KeyValue;

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
import cbit.vcell.xml.XMLTags;

/**
 * manager for Notes, Annotations, SBOTerms, and all other meta data regarding 
 * biomodel entities as well as pathway models.
 * @author schaff, ruebenacker
 *
 */

public class VCMetaData implements RDFBox {
	
	public static final Namespace nsVCML = Namespace.getNamespace("vcml",XMLTags.VCML_NS);

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
		// Property property = getRdf().getProperty(propertyURI.getPath());
		Property property = getRdf().getProperty(propertyURI.toString());
		if (property == null){
			// property = getRdf().createProperty(propertyURI.getPath());
			property = getRdf().createProperty(propertyURI.toString());
		}
		// statement
		return property;
	}
	
	public List<Statement> getStatements(Identifiable identifiable){
//		System.out.println("looking for statements for identifiable : "+VCID.getVCID(bioModel, identifiable).toASCIIString());
		final Resource resource = getResource(identifiable);
		if (resource==null){
//			System.out.println(".....no resouce found");
			return null;
		}
//		System.out.println(".....resouce \""+resource+"\" ("+resource.hashCode()+")");
		Selector selector = new Selector() {
			public RDFNode getObject() {return null;}
			public Property getPredicate() {return null;}
			public Resource getSubject() {return null;}
			public boolean isSimple() {return false;}
			public boolean test(Statement arg0) {
				Resource subject = arg0.getSubject();
				if (subject.getNameSpace()!=null && !subject.getNameSpace().toString().equals("#") && !subject.getNameSpace().equals(resource.getNameSpace())){
					System.out.println("resouce mismatch: "+subject+" :: "+resource+":   namespace S: "+subject.getNameSpace()+" differs from R: "+resource.getNameSpace());
					return false;
				}
				if (subject.toString().startsWith("#") && resource.getNameSpace().equals("http://vcell.org/data/")){
					String rest = subject.toString().substring(1);
					if (resource.toString().endsWith(rest)){
						return true;
					}
				}
				if (subject != null && subject.equals(resource)) {
					return true;
				}
				return false;
			}
		};
		
//		Selector selector = new Selector() {
//			public RDFNode getObject() {return null;}
//			public Property getPredicate() {return null;}
//			public Resource getSubject() {return resource;}
//			public boolean isSimple() {return true;}
//			public boolean test(Statement arg0) {
//				throw new RuntimeException("TEST Should never get called");
//				// return false;
//			}
//		};

		StmtIterator stmtIter = getRdf().listStatements(selector);
//		StmtIterator stmtIter = getRdf().listStatements(resource,null,(RDFNode)null);
//		StmtIterator stmtIter = getRdf().listStatements(null,null,(RDFNode)null);
		List<Statement> statements = new ArrayList<Statement>();
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
			resource = getRdf().createResource(nsVCML + "/" + identifiable.getClass().getName()
					+ "/" +(Math.abs((new Random()).nextInt())));
			entry.setResource(resource);
		}
		Property predicate = getProperty(propertyURI);
		RDFNode object = getProperty(objectURI);
		Statement statement = getRdf().createStatement(resource, predicate, object);
		System.out.println("VCMetaData.addRDFStatement(): "+statement.toString());
		System.out.println("RDF contains statement before adding: " + getRdf().contains(statement));
		getRdf().add(statement);
		System.out.println("RDF contains statement after adding: " + getRdf().contains(statement));
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

}
