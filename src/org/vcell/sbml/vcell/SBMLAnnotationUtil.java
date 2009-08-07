package org.vcell.sbml.vcell;

import java.util.Arrays;
import java.util.Vector;

import org.jdom.Element;
import org.sbml.libsbml.SBase;
import org.sbml.libsbml.XMLAttributes;
import org.sbml.libsbml.XMLNamespaces;
import org.sbml.libsbml.XMLNode;
import org.sbml.libsbml.XMLTriple;
import org.vcell.sybil.rdf.JenaIOUtil;
import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.RDFChopper;
import org.vcell.sybil.rdf.baptizer.RDFLocalNamer;
import org.vcell.sybil.rdf.baptizer.SimpleSBPAXLocalNamer;
import org.vcell.sybil.rdf.smelt.NamespaceAssimilator;
import org.vcell.sybil.rdf.smelt.SameAsCrystalizer;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDFWriter;
import cbit.vcell.xml.XMLTags;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Creates annotation xml elements from meta data
 * Creation date: thu, jun 25, 2009
 * @author: ruebenacker
 */

public class SBMLAnnotationUtil {

	protected VCMetaData metaData;
	protected RDFChopper chopper;
	protected RDFLocalNamer localNamer = new SimpleSBPAXLocalNamer();
	protected NamespaceAssimilator namespaceAssimilator;
	protected Identifiable root;
	protected Model rdfSmelted;
	protected XMLTriple tripleAnnotation;
	protected XMLTriple tripleRDF = new XMLTriple("RDF", NameSpace.RDF.uri, NameSpace.RDF.prefix);
	protected XMLTriple tripleFreeText = 
		new XMLTriple(XMLTags.FreeTextAnnotationTag, XMLTags.VCML_NS, XMLTags.VCML_NS_PREFIX);
	protected XMLTriple tripleImportRelated = 
		new XMLTriple(XMLTags.VCellRelatedInfoTag, XMLTags.VCML_NS, XMLTags.VCML_NS_PREFIX);
	protected XMLTriple tripleVCellInfo = 
		new XMLTriple(XMLTags.VCellInfoTag, XMLTags.VCML_NS, XMLTags.VCML_NS_PREFIX);
	protected XMLTriple tripleXHTML = 
		new XMLTriple(XMLTags.HTML_XHTML_ATTR_TAG, XMLTags.XHTML_URI, XMLTags.HTML_XHTML_ATTR_TAG);
	
	public SBMLAnnotationUtil(VCMetaData metaData, Identifiable root, String nsSBML) { 
		this(metaData, nsSBML);
		this.root = root;
	}
	
	public SBMLAnnotationUtil(VCMetaData metaData, String nsSBML) { 
		this.metaData = metaData; 
		tripleAnnotation = new XMLTriple("annotation", nsSBML, "");
		namespaceAssimilator = 
			new NamespaceAssimilator(metaData.getRegistry().getResources(), metaData.getBaseURI(), 
					localNamer);
		rdfSmelted = namespaceAssimilator.smelt(metaData.getRdf());
		SameAsCrystalizer sameAsCrystalizer = new SameAsCrystalizer(nsSBML);
		rdfSmelted = sameAsCrystalizer.smelt(rdfSmelted);
		chopper = new RDFChopper(rdfSmelted, metaData.getRegistry().getResources());
	}
	
	public void writeMetaID(Identifiable identifiable, SBase sBase) {
		Resource resource = metaData.getRegistry().forObject(identifiable).resource();
		if (resource != null) {
			String metaID = namespaceAssimilator.map(resource).getLocalName();
			sBase.setMetaId(metaID);
		}
	}
	
	public void readMetaID(Identifiable identifiable, SBase sBase) {
		String metaID = sBase.getMetaId();
		metaData.getRegistry().forObject(identifiable).setURI(metaData.getBaseURI() + metaID);
	}
	
	public void writeAnnotation(Identifiable identifiable, SBase sBase, 
			Element vcellImportRelatedElement) {
		// Deal with RDF annotation 
		XMLNode rootAnnotation = new XMLNode(tripleAnnotation, new XMLAttributes());
		Resource resource = metaData.getRegistry().forObject(identifiable).resource();
		Model rdfChunk = null;
		if (resource != null) { 
			rdfChunk = chopper.getChops().get(resource);
		}
		if(identifiable == root && rdfChunk != null) { 
			rdfChunk.add(chopper.getRemains());
		}
		XMLNode rootRDF = null;
		if (rdfChunk != null && metaData.getBaseURI() != null) {
			Element element = XMLRDFWriter.createElement(rdfChunk, metaData.getBaseURI());
			XMLNamespaces xmlnss = new XMLNamespaces();
			xmlnss.add(NameSpace.RDF.uri, NameSpace.RDF.prefix);
			rootRDF = XMLNode.convertStringToXMLNode(XmlUtil.xmlToString(element), xmlnss);
		}
		if (rootRDF != null && rootRDF.getNumChildren() > 0) {
			rootAnnotation.addChild(rootRDF);
		}
		
		// Deal with the non-RDF; VCell free-text annotations
		// get free text annotation from NonRDFAnnotation (associated with identifiable); create XMLNode
		XMLNode rootVCellInfo = new XMLNode(tripleVCellInfo, new XMLAttributes());
		rootVCellInfo.addNamespace(XMLTags.VCML_NS, XMLTags.VCML_NS_PREFIX);
		String freeTextStr = metaData.getFreeTextAnnotation(identifiable);
		if (freeTextStr != null && freeTextStr.length() > 0) {
			XMLNode contentFreeText = new XMLNode(freeTextStr);
			XMLNode rootFreeText = new XMLNode(tripleFreeText, new XMLAttributes());
			rootFreeText.addChild(contentFreeText);
			rootVCellInfo.addChild(rootFreeText);
		}
		// VCell specific info to be exported to SBML as annotation - used for import, not needed for metadata
		if (vcellImportRelatedElement != null) {
			XMLNode xn = elementToXMLNode(vcellImportRelatedElement);
			xn.removeNamespace(XMLTags.VCML_NS_PREFIX);
			rootVCellInfo.addChild(xn);
		}
		if (rootVCellInfo.getNumChildren() > 0) {
			rootAnnotation.addChild(rootVCellInfo);
		}
		
		// Deal with the non-RDF (other tool-related?) annotation
		Element[] elementsXML = metaData.getXmlAnnotations(identifiable);
		if (elementsXML != null) {
			for(Element elementXML : elementsXML) {
				XMLTriple tripleXML = new XMLTriple(elementXML.getName(), elementXML.getNamespaceURI(),
					elementXML.getNamespacePrefix());
				XMLNode contentXML = elementToXMLNode(elementXML);
				XMLNode rootXML = new XMLNode(tripleXML, new XMLAttributes());
				rootXML.addChild(contentXML);
				rootAnnotation.addChild(rootXML);
			}
		}
		
		if (rootAnnotation.getNumChildren() > 0) {
			sBase.setAnnotation(rootAnnotation);
		}
		writeMetaID(identifiable, sBase);
	}

	public void writeNotes(Identifiable identifiable, SBase sBaseObj) {
		// get XHTML notes for 'identifiable' from NonRDFAnnotation from VCMetadata
		Element notesElement = metaData.getXhtmlNotes(identifiable);
		if (notesElement != null) {
			sBaseObj.setNotes(elementToXMLNode(notesElement));
		}
	}	

	/**
	 * readVCellSpecificAnnotation : separate method to handle only the VCellRelatedInfo annotation stored in VCellInfo element of 
	 * 				SBML <annotation>. This is currently used only for species and reactions, since the information stored in these
	 * 				elements in the SBML annotation is required for building the corresponding VCell element.  
	 * @param sBase - the corresponding SBML element
	 * @return - the <VcellRelatedInfo> element
	 */
	public Element readVCellSpecificAnnotation(SBase sBase) {
		Element vcellSpecificElement = null;
		XMLNode annotationRoot = sBase.getAnnotation();
		if (annotationRoot != null) {
			long childCount = annotationRoot.getNumChildren();
			for(long i = 0; i < childCount; ++i) {
				XMLNode annotationBranch = annotationRoot.getChild(i);
				String namespace = annotationBranch.getNamespaceURI(annotationBranch.getPrefix());
				if((namespace != null) && (namespace.equals(tripleVCellInfo.getURI()) || namespace.equals(XMLTags.VCML_NS_OLD))) {
					int numChildren = (int)annotationBranch.getNumChildren();
					for (int j = 0; j < numChildren; j++) {
						XMLNode child = annotationBranch.getChild(j);
						// if this child has a prefix, but no explicit namespace (it is defined in its parent); try getting prefix from parent
						String childPrefix = child.getPrefix();
						if (childPrefix != null) {
							String childNamespaceURI = child.getNamespaceURI(childPrefix);
							if (childNamespaceURI == null || childNamespaceURI.length() == 0) {
								childNamespaceURI = annotationBranch.getNamespaceURI(childPrefix);
								child.addNamespace(childNamespaceURI, childPrefix);
							}
						}
						if (child.getName().equals(XMLTags.VCellRelatedInfoTag)) {
							// new style VCellInfo element (with <FreeText> and <SbmlImportRelated> subelements
							vcellSpecificElement = xmlNodeToElement(child);
						} else {
							// check if 'child' is oldStyle VCellInfo element.
							vcellSpecificElement = processOldStyleVCellInfo(child);
							if (vcellSpecificElement == null) {
								System.out.println("Unknown VCellInfo annotation type : '" + child.getName());
							}
						}	// if - else (child is VCellRelatedInfoTag)
					}	// for - numChildren
				}	// nameSpace != null & equals VCell_NS
			}	// for - childCount
		}	// annotationRoot != null
		return vcellSpecificElement;
	}
	
	/**
	 * readAnnotation : reads SBML annotations and writes it to corresponding 'identifiables' in vcMetaData. Everything except <VCellRelatedInfo> 
	 * 				is read here.
	 * @param identifiable - vcReaction, vcSpecies, vcCompartment, vcBiomodel
	 * @param sBase - corresponding SBML elements
	 */
	public void readAnnotation(Identifiable identifiable, SBase sBase) {
		readMetaID(identifiable, sBase);
		XMLNode annotationRoot = sBase.getAnnotation();
//		System.err.println(annotationRoot.toXMLString());
		if (annotationRoot != null) {
			long childCount = annotationRoot.getNumChildren();
			for(long i = 0; i < childCount; ++i) {
				XMLNode annotationBranch = annotationRoot.getChild(i);
				String namespace = annotationBranch.getNamespaceURI(annotationBranch.getPrefix());
				if(namespace != null) {
					if(namespace.equals(NameSpace.RDF.uri)) {
						// read in RDF annotation
						Model rdfNew = JenaIOUtil.modelFromText(annotationBranch.toXMLString());
						metaData.getRdf().add(rdfNew);
					} else if(namespace.equals(tripleVCellInfo.getURI()) || 
							namespace.equals(XMLTags.VCML_NS_OLD)) {
						int numChildren = (int)annotationBranch.getNumChildren();
						for (int j = 0; j < numChildren; j++) {
							XMLNode child = annotationBranch.getChild(j);
							if (child.getName().equals(XMLTags.FreeTextAnnotationTag)) {
								XMLNode contentFreeText = child.getChild(0);						
								// read in the string (not XML string, but character string) from the XMLNode;
								// set free text annotation for identifiable in metadata. 
								String freeText = contentFreeText.getCharacters();
								metaData.setFreeTextAnnotation(identifiable, freeText);
							}
						}
					} else {
						// other (tool-specific, non-RDF, XML) annotations 
						Element elementXML = xmlNodeToElement(annotationBranch);
						Element[] xmlAnnotations = metaData.getXmlAnnotations(identifiable);
						Vector<Element> xmlAnnotList = null;
						if (xmlAnnotations != null && xmlAnnotations.length > 0) {
							xmlAnnotList = new Vector<Element>(Arrays.asList(xmlAnnotations));
						} else {
							xmlAnnotList = new Vector<Element>();
						}
						if (elementXML != null) {
							xmlAnnotList.add(elementXML);
							metaData.setXmlAnnotations(identifiable, xmlAnnotList.toArray(xmlAnnotations));
						}
					}
				}
			}
		}
	}
	
	public void readNotes(Identifiable identifiable, SBase sBaseObj) {
 		// convert XMLNode of xhtml notes to JDOM element, set it on the non-RDFAnnotation of 
		// identifiable in metaData
		XMLNode notesNode = sBaseObj.getNotes();
		if (notesNode != null) {
			Element notesElement = xmlNodeToElement(notesNode);
			if (notesElement != null) {
				metaData.setXhtmlNotes(identifiable, notesElement);
			}
		}
	}

	// Converts from libSBML XMLNode to JDOM element (used in VCML) 
 	private static Element xmlNodeToElement(XMLNode xmlNode) {
		String xmlString = xmlNode.toXMLString();
		Element annotationElement = null;
		try {
			annotationElement = XmlUtil.stringToXML(xmlString, null);
		} catch (RuntimeException e) {
			e.printStackTrace(System.out);
			// don't do anything .... we want to continue reading in the model, we cannot fail import because annotation is not well-formed.
		}
		return annotationElement;
	}
	
	// Converts from JDOM element (used in VCML) to libSBML XMLNode
	private static XMLNode elementToXMLNode(Element element) {
		String xmlString = XmlUtil.xmlToString(element);
		XMLNode xmlNode = XMLNode.convertStringToXMLNode(xmlString);

		// When XMLNode is constructed from xml String (JDOM element), if the top level XML element is not 
		// <html>, <body>, <annotation> , <notes>, the xmlNode root returned is a dummy node and xml elements in the
		// xml string are added as xmlNode children to the dummy root. So loop through the children of the created 
		// xmlnode and return the child which is an 'element'
		if(!xmlNode.isElement()) {
			long numChildren = xmlNode.getNumChildren();
			for(long iChild = 0; iChild < numChildren; ++iChild) {
				XMLNode child = xmlNode.getChild(iChild);
				if(child.isElement()) {
					xmlNode = child;
					break;
				}
			}
		}
		return xmlNode;		
	}
	
	/**
	 * processOldStyleVCellInfo : Oldstyle <VCellInfo> does not have any additional 
	 * tags/subelements (<FreeText>, <VCellImportRelated>,
	 * etc). For species the only subelement is <Compound>, for reactions : <SimpleReaction>, <FluxStep>, 
	 *  <ReactionRate>, etc. This needs to be read in. If any other element is encountered, return null.
	 * @param node - the XMLNode to parse into JDOM element.
	 * @return
	 */
	private Element processOldStyleVCellInfo(XMLNode node) {
		Element vcellElement = xmlNodeToElement(node);
		if (vcellElement != null && (vcellElement.getName().equals(XMLTags.ReactionRateTag) || vcellElement.getName().equals(XMLTags.SpeciesTag) ||
									 vcellElement.getName().equals(XMLTags.SimpleReactionTag) || vcellElement.getName().equals(XMLTags.FluxStepTag))) {
			return vcellElement;
		}
		return null;
	}
}
