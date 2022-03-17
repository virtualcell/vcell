/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml.vcell;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.util.TreeNodeChangeEvent;
import org.sbml.jsbml.xml.XMLAttributes;
import org.sbml.jsbml.xml.XMLNamespaces;
import org.sbml.jsbml.xml.XMLNode;
import org.sbml.jsbml.xml.XMLTriple;
import org.sbpax.impl.HashGraph;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.schemas.util.NameSpace;
import org.sbpax.util.SesameRioUtil;
import org.vcell.sybil.rdf.RDFChopper;
import org.vcell.sybil.rdf.smelt.NamespaceAssimilator;
import org.vcell.sybil.rdf.smelt.SameAsCrystalizer;
import org.vcell.util.document.Identifiable;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.registry.Registry;
import cbit.vcell.biomodel.meta.registry.Registry.Entry;
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDFWriter;
import cbit.vcell.model.Species;
import cbit.vcell.xml.XMLTags;

/**
 * Creates annotation xml elements from meta data
 * Creation date: thu, jun 25, 2009
 * @author: ruebenacker
 */

public class SBMLAnnotationUtil {

	protected VCMetaData metaData;
	protected RDFChopper chopper;
	protected NamespaceAssimilator namespaceAssimilator;
	protected Identifiable root;
	protected Graph rdfSmelted;
	protected String nsSBML;
	protected XMLTriple tripleAnnotation;
	protected XMLTriple tripleRDF = new XMLTriple("RDF", DefaultNameSpaces.RDF.uri, DefaultNameSpaces.RDF.prefix);
	protected XMLTriple tripleFreeText = 
		new XMLTriple(XMLTags.FreeTextAnnotationTag, XMLTags.SBML_VCELL_NS, XMLTags.VCELL_NS_PREFIX);
	protected XMLTriple tripleImportRelated = 
		new XMLTriple(XMLTags.VCellRelatedInfoTag, XMLTags.SBML_VCELL_NS, XMLTags.VCELL_NS_PREFIX);
	protected XMLTriple tripleVCellInfo = 
		new XMLTriple(XMLTags.VCellInfoTag, XMLTags.SBML_VCELL_NS, XMLTags.VCELL_NS_PREFIX);
	protected XMLTriple tripleXHTML = 
		new XMLTriple(XMLTags.HTML_XHTML_ATTR_TAG, XMLTags.XHTML_URI, XMLTags.HTML_XHTML_ATTR_TAG);
	
	public SBMLAnnotationUtil(VCMetaData metaData, Identifiable root, String nsSBML) { 
		this(metaData, nsSBML);
		this.root = root;
	}
	
	public SBMLAnnotationUtil(VCMetaData metaData, String nsSBML) { 
		this.metaData = metaData; 
		nsSBML = nsSBML + "/";
		this.nsSBML = nsSBML;

		tripleAnnotation = new XMLTriple("annotation", nsSBML, "");
		Set<Resource> resources = metaData.getRegistry().getResources();
		namespaceAssimilator = 
			new NamespaceAssimilator(resources, nsSBML);
		rdfSmelted = namespaceAssimilator.smelt(metaData.getRdfDataCopy());
		SameAsCrystalizer sameAsCrystalizer = new SameAsCrystalizer(nsSBML);
		rdfSmelted = sameAsCrystalizer.smelt(rdfSmelted);
		chopper = new RDFChopper(rdfSmelted, resources);
	}
	
	public void writeMetaID(Identifiable identifiable, SBase sBase) {
		Resource resource = metaData.getRegistry().getEntry(identifiable).getResource();
		if (resource != null) {
			Resource mappedResource = namespaceAssimilator.map(resource);
			if(mappedResource instanceof URI) {
				String metaID = ((URI) mappedResource).getLocalName();
				// old meta id is badly formatted (numeric only), it needs to start with a letter
				// the new format (set in generateFreeURI() in Registry.java) we have a "metaid_" prefix before the unique number
				if(metaID != null && !metaID.isEmpty() && Character.isDigit(metaID.charAt(0))) {
					// we attempt a correction here for the existing old metaid (if it starts with a number)
					// and hope for the best, no guarantees that it will work in all cases
					metaID = "metaid_" + metaID;
				}
				sBase.setMetaId(metaID);
			}
		}
	}
	
	public void readMetaID(Identifiable identifiable, SBase sBase) {
		 if (sBase.isSetMetaId()) {
			String metaID = sBase.getMetaId();
			if (metaID.startsWith("#")) {
				metaID = metaID.substring(1);
			}
			// TODO: this does not work - separator missing - fix
			String uri = nsSBML + metaID;
			metaData.getRegistry().getEntry(identifiable).setURI(metaData.getRdfData(), uri);
		 } 
	}
	
	public void writeAnnotation(Identifiable identifiable, SBase sBase, 
			Element vcellImportRelatedElement) throws XMLStreamException {
		// Deal with RDF annotation 
		XMLNode rootAnnotation = new XMLNode(tripleAnnotation, new XMLAttributes());
		Registry registry = metaData.getRegistry();
		boolean found = registry.containsKey(identifiable);
		Entry entry;
		if(found == false) {
			entry = registry.getComparableEntry(identifiable);
		} else {
			entry = registry.getEntry(identifiable);
		}
		Resource resource = entry.getResource();
		Graph rdfChunk = null;
		if (resource != null) { 
			rdfChunk = chopper.getChops().get(resource);
		}
		if(identifiable == root && rdfChunk != null) { 
			rdfChunk.addAll(chopper.getRemains());
		}
		String strRdfAnnotations = null;
		XMLNode rootRDF = null;
		if (rdfChunk != null && metaData.getBaseURIExtended() != null) {
			Element element = XMLRDFWriter.createElement(rdfChunk, nsSBML);
//			XMLOutputter o = new XMLOutputter();
//			String ss1 = o.outputString(element);
//			System.out.println(ss1);
			if(element.getAttributes().isEmpty() && element.getContent().isEmpty()) {
				// an empty element with just a namespace (as below) will be interpreted wrongly as a note and will fail with a null pointer exception
				// <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" />
				// hence this test, we just ignore such elements
				;
			} else {
				XMLNamespaces xmlnss = new XMLNamespaces();
				xmlnss.add(DefaultNameSpaces.RDF.uri, DefaultNameSpaces.RDF.prefix);
				strRdfAnnotations = XmlUtil.xmlToString(element);
				// COPASI doesn't understand our metaid namespace, so we just replace it with a #
				String olds = "http://sourceforge.net/projects/vcell/vcml/cbit.vcell.model.Species/metaid_";
				String news = "#metaid_";
				strRdfAnnotations = strRdfAnnotations.replace(olds, news);
				// TODO: for some reason, converting strRdfAnnotations directly to rootRDF node through XMLNode.convertStringToXMLNode isn't working
				// we need to use the trick below to create a temp root annotation and extract the rootRdf node from it
				strRdfAnnotations = "<annotation>" + strRdfAnnotations;
				strRdfAnnotations += "</annotation>";
				XMLNode tempRootAnnotation = XMLNode.convertStringToXMLNode(strRdfAnnotations, xmlnss);
				int numChildren = tempRootAnnotation.getNumChildren();		// it's supposed to be exactly one, the rdf root node
				rootRDF = tempRootAnnotation.getChild(0);
			}
		}
		if (rootRDF != null && rootRDF.getNumChildren() > 0) {
			rootAnnotation.addChild(rootRDF);		// add the rdf root node which may contain multiple rdf annotations
		}
		
		// Deal with the non-RDF; VCell free-text annotations
		// get free text annotation from NonRDFAnnotation (associated with identifiable); create XMLNode
		XMLNode rootVCellInfo = new XMLNode(tripleVCellInfo, new XMLAttributes());
		rootVCellInfo.addNamespace(XMLTags.SBML_VCELL_NS, XMLTags.VCELL_NS_PREFIX);
// TODO: old way of writing the text annotations. Now we use Notes
//		String freeTextStr = metaData.getFreeTextAnnotation(identifiable);
//		if (freeTextStr != null && freeTextStr.length() > 0) {
//			XMLNode contentFreeText = new XMLNode(freeTextStr);
//			XMLNode rootFreeText = new XMLNode(tripleFreeText, new XMLAttributes());
//			rootFreeText.addChild(contentFreeText);
//			rootVCellInfo.addChild(rootFreeText);
//		}
		// VCell specific info to be exported to SBML as annotation - used for import, not needed for metadata
		if (vcellImportRelatedElement != null) {
			XMLNode xn = elementToXMLNode(vcellImportRelatedElement);
			if (xn != null){
				xn.removeNamespace(XMLTags.VCELL_NS_PREFIX);
				rootVCellInfo.addChild(xn);
			}
		}
		if (rootVCellInfo.getNumChildren() > 0) {
			rootAnnotation.addChild(rootVCellInfo);		// add the vcellinfo root node which may contain the free text annotation (not anymore!  dan aug 2019)
		}
		
		// Deal with the non-RDF (other tool-related?) annotation
//		Element[] elementsXML = metaData.getXmlAnnotations(identifiable);
//		if (elementsXML != null) {
//			for(Element elementXML : elementsXML) {
//				XMLTriple tripleXML = new XMLTriple(elementXML.getName(), elementXML.getNamespaceURI(),
//					elementXML.getNamespacePrefix());
//				XMLNode contentXML = elementToXMLNode(elementXML);
//				XMLNode rootXML = new XMLNode(tripleXML, new XMLAttributes());
//				rootXML.addChild(contentXML);
//				rootAnnotation.addChild(rootXML);
//			}
//		}
		
		if (rootAnnotation.getNumChildren() > 0) {
			sBase.setAnnotation(rootAnnotation);
		}
		writeMetaID(identifiable, sBase);
	}

	/**
	 * readVCellSpecificAnnotation : separate method to handle only the VCellRelatedInfo annotation stored in VCellInfo element of 
	 * 				SBML <annotation>. This is currently used only for species and reactions, since the information stored in these
	 * 				elements in the SBML annotation is required for building the corresponding VCell element.  
	 * @param sBase - the corresponding SBML element
	 * @return - the <VcellRelatedInfo> element
	 * @throws XMLStreamException 
	 */
	public Element readVCellSpecificAnnotation(SBase sBase) throws XMLStreamException {
		Element vcellSpecificElement = null;
		Annotation annotation = sBase.getAnnotation();
		XMLNode annotationRoot = annotation.getXMLNode();
		if (annotationRoot != null) {
			long childCount = annotationRoot.getNumChildren();
			for(int i = 0; i < childCount; ++i) {
				XMLNode annotationBranch = annotationRoot.getChild(i);
				String namespace = annotationBranch.getNamespaceURI(annotationBranch.getPrefix());
				if((namespace != null) && (namespace.equals(tripleVCellInfo.getURI()) || namespace.equals(XMLTags.VCML_NS_OLD) ||
						namespace.equals(XMLTags.VCML_NS)) ) {
					int numChildren = (int)annotationBranch.getNumChildren();
					for (int j = 0; j < numChildren; j++) {
						XMLNode child = annotationBranch.getChild(j);
						if (!child.isElement()){
							continue;
						}
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
	 * @throws XMLStreamException 
	 * @throws  
	 * @throws RDFHandlerException 
	 * @throws RDFParseException 
	 */
	public void readAnnotation(Identifiable identifiable, SBase sBase) throws XMLStreamException {
		readMetaID(identifiable, sBase);
		XMLNode annotationRoot = sBase.getAnnotation().getFullAnnotation();
		if (annotationRoot != null) {
			long childCount = annotationRoot.getNumChildren();
			for(int i = 0; i < childCount; ++i) {
				XMLNode annotationBranch = annotationRoot.getChild(i);
				String namespace = annotationBranch.getNamespaceURI(annotationBranch.getPrefix());
				if(namespace != null) {
					if(namespace.equals(DefaultNameSpaces.RDF.uri)) {
						// read in RDF annotation
						String text = annotationBranch.toXMLString();

						// TODO this is a hack to be replaced by proper URI management.
						text = text.replace("about=\"#","about=\"");
						Graph rdfNew = new HashGraph();
						Map<String, String> nsMap = new HashMap<String, String>();
						try {
							SesameRioUtil.readRDFFromString(text, rdfNew, nsMap, RDFFormat.RDFXML, nsSBML);
						} catch (RDFParseException e) { e.printStackTrace(); } 
						catch (RDFHandlerException e) { e.printStackTrace(); } 
						catch (IOException e) { e.printStackTrace(); }
//						System.out.println("SBML NS :\n" + MIRIAMAnnotationViewer.prettyPrintJenaModel(rdfNew, nsSBML));
						metaData.add(rdfNew);
//						System.out.println("VCML NS :\n" + MIRIAMAnnotationViewer.prettyPrintJenaModel(metaData.getRdfData(), XMLTags.VCML_NS));
//						System.out.println("SBBox Data :\n" + MIRIAMAnnotationViewer.prettyPrintJenaModel(metaData.getSBbox().getData(), XMLTags.VCML_NS));
//						System.out.println(MIRIAMAnnotationViewer.printResourceMappings(metaData));
						
// TODO: old way of reading the text annotations, as application-specific (vCell) annotations. Now we use Notes, which is more compatible with SBML standard
//					} else if(namespace.equals(tripleVCellInfo.getURI()) || namespace.equals(XMLTags.VCML_NS_OLD) ||
//							namespace.equals(XMLTags.VCML_NS)) {
//						int numChildren = (int)annotationBranch.getNumChildren();
//						for (int j = 0; j < numChildren; j++) {
//							XMLNode child = annotationBranch.getChild(j);
//							if (child.isElement() && child.getName().equals(XMLTags.FreeTextAnnotationTag)) {
//								XMLNode contentFreeText = child.getChild(0);						
//								// read in the string (not XML string, but character string) from the XMLNode;
//								// set free text annotation for identifiable in metadata. 
//								String freeText = contentFreeText.getCharacters();
//								metaData.setFreeTextAnnotation(identifiable, freeText);
//							}
//						}
					} else {
						// other (tool-specific, non-RDF, XML) annotations 
						Element elementXML = null;
//						String xmlString = annotationBranch.toXMLString();
//						Element annotationElement = null;
						try {
							XMLNode clonedAnnotationBranch = annotationBranch.clone();
							String annotationBranchString = clonedAnnotationBranch.toXMLString();
							annotationBranchString = annotationBranchString.replace("\t", "");
							annotationBranchString = annotationBranchString.replace("\r", "");
							annotationBranchString = annotationBranchString.replace("\n", "");
							annotationBranchString = annotationBranchString.trim();
							if(annotationBranchString.isEmpty()) {
								// we ignore some parasitic empty annotation blocs which were causing crashes during roundtrip tests
								// TODO: why (and where) are they being generated anyway
								continue;
							}
							XMLNode clonedAnnotRoot = new XMLNode(annotationRoot);
							clonedAnnotRoot.setNamespaces(annotationRoot.getNamespaces());
							clonedAnnotRoot.removeChildren();
							clonedAnnotRoot.addChild(annotationBranch.clone());
							String str = clonedAnnotRoot.toXMLString();
							elementXML = (XmlUtil.stringToXML(str, null)).getRootElement();//(XmlUtil.stringToXML(xmlString, null)).getRootElement();
						} catch (Exception e) {
//							e.printStackTrace(System.out);
							// don't do anything .... we want to continue reading in the model, we cannot fail import because annotation is not well-formed.
							//try wrap in namespace element
//							System.out.println(sBase.toSBML()+"\n\n"+annotationRoot.toXMLString());
//							XMLNamespaces xmlNamespaces = sBase.getNamespaces();
//							if(xmlNamespaces != null && !xmlNamespaces.isEmpty()){
//								String xmlnsStr = "";
//								for (int j = 0; j < xmlNamespaces.getNumNamespaces(); j++) {
//									xmlnsStr+= "\nxmlns"+(xmlNamespaces.getPrefix(j).length()==0?"":":"+xmlNamespaces.getPrefix(j))+"=\""+xmlNamespaces.getURI(j)+"\"";
//								}
//								String wrap = "<annotation "+xmlnsStr+">\n"+annotationBranch.toXMLString()+"\n</annotation>";
//								System.out.println(wrap);
//								try{
//									elementXML = (XmlUtil.stringToXML(wrap, null)).getRootElement();
//									System.out.println("-----PROBLEM FIXED-----");
//								}catch(Exception e2){
//									e.printStackTrace();
//								}
//							}
						}
						
//						Element elementXML = xmlNodeToElement(annotationBranch);
						Element[] xmlAnnotations = metaData.getXmlAnnotations(identifiable);
						Vector<Element> xmlAnnotList = new Vector<Element>();
						if (xmlAnnotations != null && xmlAnnotations.length > 0) {
							xmlAnnotList.addAll(Arrays.asList(xmlAnnotations));
						} 
						if (elementXML != null) {
							xmlAnnotList.add(elementXML);
							metaData.setXmlAnnotations(identifiable, xmlAnnotList.toArray(new Element[0]));
						}
					}
				}
			}
		}
	}
	
/*
  // Something like this is what we read:
  <notes>
    <html xmlns="http://www.w3.org/1999/xhtml">
	  <head>
	  </head>
      <body>
		my text
      </body>
    </html>
  </notes>
 */
	public void readNotes(Identifiable identifiable, SBase sBaseObj) throws XMLStreamException {
		XMLNode notesNode = sBaseObj.getNotes();
		if (notesNode != null) {
			String txt = notesNode.toXMLString();
			metaData.setFreeTextAnnotation(identifiable, txt);
		}
	}
	public void writeNotes(Identifiable identifiable, SBase sBaseObj) throws XMLStreamException {
		String notes = metaData.getFreeTextAnnotation(identifiable);
		if(notes != null) {
			if(notes.startsWith("<html>")) {
				notes = notes.substring("<html>".length());
				// hack to add the proper namespace, without it importing will fail
				notes = "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + notes;
			}
			sBaseObj.setNotes(notes);
			writeMetaID(identifiable, sBaseObj);
		}
	}	

	// Converts from libSBML XMLNode to JDOM element (used in VCML) 
 	private static Element xmlNodeToElement(XMLNode xmlNode) throws XMLStreamException {
		String xmlString = xmlNode.toXMLString();
		Element annotationElement = null;
		try {
			annotationElement = (XmlUtil.stringToXML(xmlString, null)).getRootElement();
		} catch (RuntimeException e) {
			e.printStackTrace(System.out);
			// don't do anything .... we want to continue reading in the model, we cannot fail import because annotation is not well-formed.
		}
		return annotationElement;
	}
	
	// Converts from JDOM element (used in VCML) to libSBML XMLNode
	private static XMLNode elementToXMLNode(Element element) throws XMLStreamException {
		String xmlString = XmlUtil.xmlToString(element);
		XMLNode xmlNode = XMLNode.convertStringToXMLNode(xmlString);
		if (xmlNode==null){
			System.err.println("failed to parse annotation from "+xmlString);
			return null;
		}
		// When XMLNode is constructed from xml String (JDOM element), if the top level XML element is not 
		// <html>, <body>, <annotation> , <notes>, the xmlNode root returned is a dummy node and xml elements in the
		// xml string are added as xmlNode children to the dummy root. So loop through the children of the created 
		// xmlnode and return the child which is an 'element'
		if(!xmlNode.isElement()) {
			int numChildren = xmlNode.getNumChildren();
			for(int iChild = 0; iChild < numChildren; ++iChild) {
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
	 * @throws XMLStreamException 
	 */
	private Element processOldStyleVCellInfo(XMLNode node) throws XMLStreamException {
		Element vcellElement = xmlNodeToElement(node);
		if (vcellElement != null && (vcellElement.getName().equals(XMLTags.ReactionRateTag) || vcellElement.getName().equals(XMLTags.SpeciesTag) ||
									 vcellElement.getName().equals(XMLTags.SimpleReactionTag) || vcellElement.getName().equals(XMLTags.FluxStepTag))) {
			return vcellElement;
		}
		return null;
	}

	private static final String sameAs = "owl:sameAs";
	private static final String startDesc = "<rdf:Description";
	private static final String endDesc = "</rdf:Description>";
	public static String postProcessCleanup(String ret) {
		if(ret == null || ret.isEmpty()) {
			return ret;
		}
//		ret = ret.replace("&lt;", "<");
		String result = "";
		while(true) {
			if(!ret.contains(sameAs)) {
				break;							// exit condition
			}
			String prefix = ret.substring(0, ret.indexOf(sameAs));
			String suffix = ret.substring(ret.indexOf(sameAs) + sameAs.length());
			prefix = prefix.substring(0, prefix.lastIndexOf(startDesc));
			int index = suffix.indexOf(endDesc) + endDesc.length();
			suffix = suffix.substring(index);
			result += prefix;
			ret = suffix;
		}
		result += ret;
		return result;
	}
}
