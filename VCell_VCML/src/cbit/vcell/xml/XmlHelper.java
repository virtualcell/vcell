package cbit.vcell.xml;
import cbit.image.VCImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.cellml.VCQualCellTranslator;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.math.MathDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.vcml.Translator;
import cbit.util.DataAccessException;
import cbit.util.Extent;
import cbit.util.VCDocument;
import cbit.util.graph.NodeInfo;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlParseException;
import cbit.util.xml.XmlUtil;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;

/**
This class represents the 'API' of the XML framework for all VC classes, outside that framework. Most of the methods of
this class throw an XmlParseException:
- XMLTo*() and *ToXML() methods: for biomodel, mathmodel, geometry (with the option of including version info)
		          				 for simulation, image.
- exportXML() methods: exports VCML (as BioModel, MathModel) to either SBML or CellML, 
                       with the option of specifying an application.
- importXML() methods: imports XML (as BioModel, MathModel) from either SBML or CellML.
- compareMerge() methods: compares two VCML documents, with the option of comparing version info.
 
 * Creation date: (2/26/2004 10:13:28 AM)
 * @author: Rashad Badrawi
 */
public class XmlHelper {

	//represent the containers XML element for the simulation/image data to be imported/exported. 
	//For now, same as their VCML counterparts.
	private static final String SIM_CONTAINER = XMLTags.SimulationSpecTag;
	private static final String IMAGE_CONTAINER = XMLTags.GeometryTag;

	//no instances allowed
	private XmlHelper() {}


	public static String bioModelToXML(BioModel bioModel) throws XmlParseException {

		return bioModelToXML(bioModel, true);
	}


	static String bioModelToXML(BioModel bioModel, boolean printkeys) throws XmlParseException {

		String xmlString = null;
		
		try {
			if (bioModel == null){
				throw new IllegalArgumentException("Invalid input for BioModel: " + bioModel);
			}
			Xmlproducer xmlProducer = new Xmlproducer(printkeys);
			Element element = xmlProducer.getXML(bioModel);
			element = XmlUtil.setDefaultNamespace(element, Namespace.getNamespace(Translator.VCML_NS));		
			xmlString = XmlUtil.xmlToString(element);
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("Unable to generate Biomodel XML: " + e.getMessage());
		} 
		
		return xmlString;
	}


/**
 * Exports VCML format to another supported format (currently: SBML or CellML).
 * Creation date: (4/4/2003 3:59:37 PM)
 * @return java.lang.String
 */
public static String exportXML(VCDocument vcDoc, XmlDialect toDialect) throws XmlParseException {

	return exportXML(vcDoc, toDialect, null);
}


/**
 * Exports VCML format to another supported format (currently: SBML or CellML). It allows 
   choosing a specific Simulation Spec to export.
 * Creation date: (4/8/2003 12:30:27 PM)
 * @return java.lang.String
 */
public static String exportXML(VCDocument vcDoc, XmlDialect toDialect, String appName) throws XmlParseException {

	if (vcDoc == null) {
        throw new XmlParseException("Invalid arguments for exporting XML.");
    } 
	String resultXML = null;

	org.vcell.sbml.SBMLExporter sbmlExporter = null;
	if (toDialect.equals(XmlDialect.SBML_L1V1) || toDialect.equals(XmlDialect.SBML_L2V1)) {
		int level = -1;
		if (toDialect.equals(XmlDialect.SBML_L1V1)) {
			level = 1;
		} else if (toDialect.equals(XmlDialect.SBML_L2V1)) {
			level = 2;
		}
		if (vcDoc instanceof BioModel) {
		    sbmlExporter = new org.vcell.sbml.SBMLExporter((BioModel)vcDoc, level);
		    sbmlExporter.setVcPreferredSimContextName(appName);
		    resultXML = sbmlExporter.getSBMLFile();
		} else if (vcDoc instanceof MathModel) {
			// Invoke SBMLExporter_alt ...
		}
    } else {   
	    String sourceXML; 
		if (vcDoc instanceof BioModel) {
			sourceXML = XmlHelper.bioModelToXML((BioModel)vcDoc, false);
		} else if (vcDoc instanceof MathModel) {
			sourceXML = XmlHelper.mathModelToXML((MathModel)vcDoc, false);
		} else {
			throw new XmlParseException("Invalid document type to translate: " + vcDoc.toString());
		}
		resultXML = exportXML(sourceXML, toDialect, appName);
    }

	return resultXML;
}


	private static String exportXML(String sourceXml, XmlDialect toDialect) throws XmlParseException {

		return exportXML(sourceXml, toDialect, null);
	}


	private static String exportXML(String sourceXml, XmlDialect toDialect, String appName) throws XmlParseException {

		String transType = getTranslatorType(XmlDialect.VCML, toDialect);
		if (transType == null) {
			throw new XmlParseException("Invalid translation request from VCML to " + toDialect.getName());
		}
		try {
			String result;
	    	BufferedReader br = new BufferedReader(new StringReader(sourceXml));
		    StringWriter sw = new StringWriter();
		    Translator t = Translator.getTranslator(transType);
		    if (appName != null && appName.length() > 0 ) {           			
		    	if (t instanceof VCQualCellTranslator) {
		    		((VCQualCellTranslator)t).setPreferedSimSpec(appName);
		    	}
		    }
		    Document doc = t.translate(br, false);                            //no validation for VCML.
			t.printTarget(sw);
			sw.flush();
			result = sw.toString();
			sw.close();

			return result;
		} catch (Exception e){
			e.printStackTrace();
			throw new XmlParseException(e.getMessage());
		}
	}


	public static String geometryToXML(Geometry geometry) throws XmlParseException {

		return geometryToXML(geometry, true);
	}


	static String geometryToXML(Geometry geometry, boolean printkeys) throws XmlParseException {

		String geometryString = null;
		
		if (geometry == null){
			throw new XmlParseException("Invalid input for Geometry: " + geometry);
		}
		Xmlproducer xmlProducer = new Xmlproducer(printkeys);
		Element element = xmlProducer.getXML(geometry);
		element = XmlUtil.setDefaultNamespace(element, Namespace.getNamespace(Translator.VCML_NS));		
		geometryString = XmlUtil.xmlToString(element);
		
		return geometryString;
	}


	//switches from XmlDialect lingo to Translator lingo
	private static String getTranslatorType(XmlDialect fromDialect, XmlDialect toDialect) {

		String transType = null;
		
		if (fromDialect.equals(XmlDialect.VCML)) {
			if (toDialect.equals(XmlDialect.SBML_L1V1)) {
				transType = Translator.VCSB_1;
			} else if (toDialect.equals(XmlDialect.SBML_L2V1)) {
				transType = Translator.VCSB_2;
			} else if (toDialect.equals(XmlDialect.QUAL_CELLML)) {
				transType = Translator.VC_QUAL_CELL;
			} else if (toDialect.equals(XmlDialect.QUAN_CELLML)) {
				transType = Translator.VC_QUAN_CELL;
			}
		} else if (toDialect.equals(XmlDialect.VCML)) {
			if (fromDialect.equals(XmlDialect.SBML_L1V1)) {
		    	transType = Translator.SBVC_1;
	    	}else if (fromDialect.equals(XmlDialect.SBML_L2V1)) {
				transType = Translator.SBVC_2;				  	
	    	} else if (fromDialect.equals(XmlDialect.QUAL_CELLML)) {
		    	transType = Translator.CELL_QUAL_VC;
	    	} else if (fromDialect.equals(XmlDialect.QUAN_CELLML)) {
				transType = Translator.CELL_QUAN_VC;
	    	}
		}

		return transType;
	}


	public static String imageToXML(VCImage vcImage) throws XmlParseException {

		return imageToXML(vcImage, true);
	}


	static String imageToXML(VCImage vcImage, boolean printKeys) throws XmlParseException {

		String xmlString = null;
		
		if (vcImage == null){
			throw new XmlParseException("Invalid input for VCImage: " + vcImage);
		}
		Xmlproducer xmlProducer = new Xmlproducer(printKeys);
		Extent extent = vcImage.getExtent();
		Element container = new Element(IMAGE_CONTAINER); 
		Element imageElement = xmlProducer.getXML(vcImage);
		Element extentElement = xmlProducer.getXML(extent);
		container.addContent(imageElement);
		container.addContent(extentElement);
		container = XmlUtil.setDefaultNamespace(container, Namespace.getNamespace(Translator.VCML_NS));		
		xmlString = XmlUtil.xmlToString(container);
		
		return xmlString;
	}


public static VCDocument importXML(String xmlString, XmlDialect fromDialect, VCLogger vcLogger) throws Exception {

	//checks that the string is not empty
    if (xmlString == null || xmlString.length() == 0 || fromDialect == null) {
        throw new XmlParseException("Invalid params for importing xml.");
    }
	String transType = getTranslatorType(fromDialect, XmlDialect.VCML);
	if (transType == null) {
		throw new XmlParseException("Invalid translation request to VCML from " + fromDialect.getName());
	}
    VCDocument vcDoc = null;
	if (transType.equals(Translator.SBVC_1) || transType.equals(Translator.SBVC_2)) {
		org.vcell.sbml.SBMLImporter sbmlImporter = new org.vcell.sbml.SBMLImporter(xmlString, vcLogger);
		vcDoc = sbmlImporter.getBioModel();
	} else {
	    Translator t = Translator.getTranslator(transType);
		Document tDoc;
		tDoc = t.translate(new BufferedReader(new StringReader(xmlString)), false); 
		//Read the VCML into a BioModel or a MathModel
	    XmlReader xmlReader = new XmlReader(false);
	    if (!transType.equals(Translator.CELL_QUAN_VC)){
	    	vcDoc = xmlReader.getBioModel(tDoc.getRootElement());
	    }else {
	    	vcDoc = xmlReader.getMathModel(tDoc.getRootElement());
	    }
	}

	vcDoc.refreshDependencies();
    
    return vcDoc;
    /*
	if ( rootElement.getNamespaceURI().length()==0) {
    	//Fix the defaultnamespace
    	Namespace namespace =  Namespace.getNamespace(fromDialect.getUri());
    	rootElement = XmlUtil.setDefaultNamespace(rootElement, namespace);
	}
	*/ 
}


	/**
	Allows the translation process to interact with the user via TranslationMessager
	*/
	public static VCDocument importXMLVerbose(VCLogger vcLogger, String xmlString, XmlDialect fromDialect) throws Exception {

		//checks that the string is not empty
	    if (xmlString == null || xmlString.length() == 0 || fromDialect == null || vcLogger == null) {
	        throw new XmlParseException("Invalid params for importing xml.");
	    }
		String transType = getTranslatorType(fromDialect, XmlDialect.VCML);
		if (transType == null) {
			throw new XmlParseException("Invalid translation request to VCML from " + fromDialect.getName());
		}
	    VCDocument vcDoc = null;
		if (transType.equals(Translator.SBVC_1) || transType.equals(Translator.SBVC_2)) {
			org.vcell.sbml.SBMLImporter sbmlImporter = new org.vcell.sbml.SBMLImporter(xmlString, vcLogger);
			vcDoc = sbmlImporter.getBioModel();
		} else {
			Translator t = Translator.getTranslator(transType);
			t.setVCLogger(vcLogger);
			Document tDoc;
			tDoc = t.translate(new BufferedReader(new StringReader(xmlString)), false); 
			//Read the VCML into a BioModel or a MathModel
		    XmlReader xmlReader = new XmlReader(false);
		    if (!transType.equals(Translator.CELL_QUAN_VC)){
		    	vcDoc = xmlReader.getBioModel(tDoc.getRootElement());
		    }else {
		    	vcDoc = xmlReader.getMathModel(tDoc.getRootElement());
		    }
		}

		vcDoc.refreshDependencies();
	    
	    return vcDoc;
	    /*
		if ( rootElement.getNamespaceURI().length()==0) {
	    	//Fix the defaultnamespace
	    	Namespace namespace =  Namespace.getNamespace(fromDialect.getUri());
	    	rootElement = XmlUtil.setDefaultNamespace(rootElement, namespace);
		}
		*/ 
	}


	public static String mathModelToXML(MathModel mathModel) throws XmlParseException {

		return mathModelToXML(mathModel, true);
	}


	static String mathModelToXML(MathModel mathModel, boolean printkeys) throws XmlParseException {

		String xmlString = null;
		
		if (mathModel == null){
			throw new XmlParseException("Invalid input for BioModel: " + mathModel);
		}
		Xmlproducer xmlProducer = new Xmlproducer(printkeys);
		Element element = xmlProducer.getXML(mathModel);
		element = XmlUtil.setDefaultNamespace(element, Namespace.getNamespace(Translator.VCML_NS));		
		xmlString = XmlUtil.xmlToString(element);
		
		return xmlString;
	}


	public static String simToXML(Simulation sim) throws XmlParseException {

		String simString = null;
		
		if (sim == null) {
			throw new XmlParseException("Invalid input for Simulation: " + sim);
		}
		Xmlproducer xmlProducer = new Xmlproducer(true);
		MathDescription md = sim.getMathDescription();           //cannot be null
		Geometry geom = md.getGeometry();    
		Element container = new Element(SIM_CONTAINER); 
		Element mathElement = xmlProducer.getXML(md);
		Element simElement = xmlProducer.getXML(sim);
		if (geom != null) {
			Element geomElement = xmlProducer.getXML(geom);
			container.addContent(geomElement);
		} else {
			System.err.println("No corresponding geometry for the simulation: " + sim.getName());
		} 
		container.addContent(mathElement);
		container.addContent(simElement);
		container = XmlUtil.setDefaultNamespace(container, Namespace.getNamespace(Translator.VCML_NS));		
		simString = XmlUtil.xmlToString(container);
		
		return simString;
	}


	public static BioModel XMLToBioModel(String xmlString) throws XmlParseException {

		return XMLToBioModel(xmlString, true);
	}


	static BioModel XMLToBioModel(String xmlString, boolean printkeys) throws XmlParseException {

long l0 = System.currentTimeMillis();
		BioModel bioModel = null;
		Namespace ns = Namespace.getNamespace(Translator.VCML_NS);
		
		if (xmlString == null || xmlString.length() == 0){
			throw new XmlParseException("Invalid xml for BioModel: " + xmlString);
		}
		Element root = XmlUtil.stringToXML(xmlString, null);      //default parser and no validation
		XmlReader reader = new XmlReader(printkeys);
		bioModel = reader.getBioModel(root);

long l1 = System.currentTimeMillis();
		bioModel.refreshDependencies();
long l2 = System.currentTimeMillis();
System.out.println("refresh-------- "+((double)(l2-l1))/1000);
System.out.println("total-------- "+((double)(l2-l0))/1000);

		return bioModel;		
	}


/**
 * Insert the method's description here.
 * Creation date: (2/7/2006 4:45:26 PM)
 * @return cbit.vcell.document.VCDocument
 * @param xmlString java.lang.String
 */
public static VCDocument XMLToDocument(VCLogger vcLogger, String xmlString) throws Exception {
	VCDocument doc = null;
	org.jdom.Element rootElement = cbit.util.xml.XmlUtil.stringToXML(xmlString, null);         //some overhead.
	String xmlType = rootElement.getName();
	if (xmlType.equals(XMLTags.BioModelTag)) {
		doc = XmlHelper.XMLToBioModel(xmlString);
	} else if (xmlType.equals(XMLTags.MathModelTag)) {
		doc = XmlHelper.XMLToMathModel(xmlString);
	} else if (xmlType.equals(XMLTags.GeometryTag)) {
		doc = XmlHelper.XMLToGeometry(xmlString);
	} else if (xmlType.equals(XMLTags.SbmlRootNodeTag)) {
		cbit.vcell.xml.XmlDialect fromDialect = cbit.vcell.xml.XmlDialect.getTargetDialect(rootElement.getName(), rootElement.getNamespaceURI(), XMLTags.BioModelTag);
		doc = XmlHelper.importXMLVerbose(vcLogger, xmlString, fromDialect);
	} else if (xmlType.equals(XMLTags.CellmlRootNodeTag)) {
		cbit.vcell.xml.XmlDialect fromDialect = cbit.vcell.xml.XmlDialect.getTargetDialect(rootElement.getName(), rootElement.getNamespaceURI(), XMLTags.BioModelTag);
		doc = XmlHelper.importXMLVerbose(vcLogger, xmlString, fromDialect);
	} else { // unknown XML format
		throw new RuntimeException("unsupported XML format, first element tag is <"+rootElement.getName()+">");
	}
	return doc;
}


/**
  *
  * for any VCDocument, must refreshDependencies().
  *
  */

public static Geometry XMLToGeometry(String xmlString) throws XmlParseException {
	
	return XMLToGeometry(xmlString, true);
}


	static Geometry XMLToGeometry(String xmlString, boolean printkeys) throws XmlParseException {

		Geometry geometry = null;
		Namespace ns = Namespace.getNamespace(Translator.VCML_NS);
		
		if (xmlString == null || xmlString.length() == 0){
			throw new XmlParseException("Invalid xml for Geometry: " + xmlString);
		}
		Element root = XmlUtil.stringToXML(xmlString, null);      //default parser and no validation
		XmlReader reader = new XmlReader(printkeys);
		geometry = reader.getGeometry(root);

		geometry.refreshDependencies();

		return geometry;		
	}


	public static VCImage XMLToImage(String xmlString) throws XmlParseException {

		return XMLToImage(xmlString, true);
	}


	static VCImage XMLToImage(String xmlString, boolean printKeys) throws XmlParseException {

		VCImage vcImage = null;
		Namespace ns = Namespace.getNamespace(Translator.VCML_NS);
		
		if (xmlString == null || xmlString.length() == 0) {
			throw new XmlParseException("Invalid xml for Image: " + xmlString);
		}
		Element root = XmlUtil.stringToXML(xmlString, null);     //default parser and no validation
		XmlReader reader = new XmlReader(printKeys);
		Element extentElement = root.getChild(XMLTags.ExtentTag, ns);
		Element imageElement = root.getChild(XMLTags.ImageTag, ns);
		cbit.util.Extent extent = reader.getExtent(extentElement);
		vcImage = reader.getVCImage(imageElement,extent);

		vcImage.refreshDependencies();

		return vcImage;		
	}


	public static MathModel XMLToMathModel(String xmlString) throws XmlParseException {

		return XMLToMathModel(xmlString, true);
	}


	static MathModel XMLToMathModel(String xmlString, boolean printkeys) throws XmlParseException {

		MathModel mathModel = null;
		Namespace ns = Namespace.getNamespace(Translator.VCML_NS);
		
		if (xmlString == null || xmlString.length() == 0){
			throw new XmlParseException("Invalid xml for MathModel: " + xmlString);
		}
		Element root = XmlUtil.stringToXML(xmlString, null);      //default parser and no validation
		XmlReader reader = new XmlReader(printkeys);
		mathModel = reader.getMathModel(root);

		mathModel.refreshDependencies();

		return mathModel;		
	}


	public static Simulation XMLToSim(String xmlString) throws XmlParseException {

		Simulation sim = null;
		Namespace ns = Namespace.getNamespace(Translator.VCML_NS);
		
		try {
			if (xmlString == null || xmlString.length() == 0) {
				throw new XmlParseException("Invalid xml for Simulation: " + xmlString);
			}
			Element root =  XmlUtil.stringToXML(xmlString, null);     //default parser and no validation
			XmlReader reader = new XmlReader(true);
			Element simElement = root.getChild(XMLTags.SimulationTag, ns);
			Element mdElement = root.getChild(XMLTags.MathDescriptionTag, ns);
			MathDescription md = reader.getMathDescription(mdElement);
			Element geomElement = root.getChild(XMLTags.GeometryTag, ns);
			if (geomElement != null) {
				Geometry geom = reader.getGeometry(geomElement);
				md.setGeometry(geom);
			}
			sim = reader.getSimulation(simElement, md);
		} catch (PropertyVetoException pve) {
			pve.printStackTrace();
			throw new XmlParseException("Unable to parse simulation string."+" : "+pve.getMessage());
		}

		sim.refreshDependencies();

		return sim;		
	}
}