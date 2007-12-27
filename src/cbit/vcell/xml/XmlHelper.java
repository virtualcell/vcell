package cbit.vcell.xml;
import cbit.image.VCImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.document.VCDocument;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.math.MathDescription;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.util.Extent;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.xml.merge.NodeInfo;
import cbit.xml.merge.TMLPanel;
import cbit.xml.merge.XmlTreeDiff;

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
import org.vcell.cellml.VCQualCellTranslator;
import org.vcell.sbml.vcell.MathModel_SBMLExporter;
import org.vcell.sbml.vcell.SBMLExporter;

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
			element = XmlUtil.setDefaultNamespace(element, Namespace.getNamespace(XMLTags.VCML_NS));		
			xmlString = XmlUtil.xmlToString(element);
		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new XmlParseException("Unable to generate Biomodel XML: " + e.getMessage());
		} 
		
		return xmlString;
	}


//default is to include version information in the comparison.
	public static XmlTreeDiff compareMerge(String xmlBaseString, String xmlModifiedString, String comparisonSetting) throws XmlParseException {

		return compareMerge(xmlBaseString, xmlModifiedString, comparisonSetting, false);
	}


/**
 * compareMerge method comment.
 */
public static cbit.xml.merge.XmlTreeDiff compareMerge(String xmlBaseString, String xmlModifiedString, 
	                          String comparisonSetting, boolean ignoreVersionInfo) throws XmlParseException {
	try {
		if (xmlBaseString == null || xmlModifiedString == null || xmlBaseString.length() == 0 || xmlModifiedString.length() == 0 ||
		    (!TMLPanel.COMPARE_DOCS_SAVED.equals(comparisonSetting) && !TMLPanel.COMPARE_DOCS_OTHER.equals(comparisonSetting)) ) {
	        throw new XmlParseException("Invalid XML comparison params.");
	    }
	    Element baselineRoot = XmlUtil.stringToXML(xmlBaseString, null);            //default setting, no validation
	    Element modifiedRoot = XmlUtil.stringToXML(xmlModifiedString, null);
	    //Merge the Documents
	    XmlTreeDiff merger = new XmlTreeDiff(ignoreVersionInfo);
	    NodeInfo top = merger.merge(baselineRoot.getDocument(), modifiedRoot.getDocument(), comparisonSetting);     

	    // Return the result
	    return merger;                                               //return the tree-diff instead of the root node.
	} catch (Exception e) {
		e.printStackTrace(System.out);
		throw new XmlParseException(e.getMessage());
	}

}

/**
 * Exports VCML format to another supported format (currently: SBML or CellML). It allows 
   choosing a specific Simulation Spec to export.
 * Creation date: (4/8/2003 12:30:27 PM)
 * @return java.lang.String
 */
public static String exportSBML(VCDocument vcDoc, int level, int version, SimulationContext simContext, SimulationJob simJob) throws XmlParseException {

	if (vcDoc == null) {
        throw new XmlParseException("Invalid arguments for exporting SBML.");
    } 
	if (vcDoc instanceof BioModel) {
	    SBMLExporter sbmlExporter = new SBMLExporter((BioModel)vcDoc, level, version);
//	    sbmlExporter.setVcPreferredSimContextName(appName);
	    sbmlExporter.setSelectedSimContext(simContext);
	    sbmlExporter.setSelectedSimulationJob(simJob);
	    return sbmlExporter.getSBMLFile();
	} else if (vcDoc instanceof MathModel) {
		try {
			return MathModel_SBMLExporter.getSBML((MathModel)vcDoc).toSBML();
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());
		}
	} else{
		throw new RuntimeException("unsupported Document Type "+vcDoc.getClass().getName()+" for SBML export");
	}
}

/**
 * Exports VCML format to another supported format (currently: SBML or CellML). It allows 
   choosing a specific Simulation Spec to export.
 * Creation date: (4/8/2003 12:30:27 PM)
 * @return java.lang.String
 */
public static String exportCellML(VCDocument vcDoc, String appName) throws XmlParseException {
	throw new RuntimeException("CellML support has been disabled");
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
		element = XmlUtil.setDefaultNamespace(element, Namespace.getNamespace(XMLTags.VCML_NS));		
		geometryString = XmlUtil.xmlToString(element);
		
		return geometryString;
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
		container = XmlUtil.setDefaultNamespace(container, Namespace.getNamespace(XMLTags.VCML_NS));		
		xmlString = XmlUtil.xmlToString(container);
		
		return xmlString;
	}

/**
Allows the translation process to interact with the user via TranslationMessager
*/
public static VCDocument importSBML(VCLogger vcLogger, String xmlString) throws Exception {

	//checks that the string is not empty
    if (xmlString == null || xmlString.length() == 0 || vcLogger == null) {
        throw new XmlParseException("Invalid params for importing sbml.");
    }
    VCDocument vcDoc = null;
	org.vcell.sbml.vcell.SBMLImporter sbmlImporter = new org.vcell.sbml.vcell.SBMLImporter(xmlString, vcLogger);
	vcDoc = sbmlImporter.getBioModel();
	vcDoc.refreshDependencies();
    return vcDoc;
}

public static VCDocument importBioCellML(VCLogger vcLogger, String xmlString) throws Exception {
	throw new Exception("CellML support has been disabled.");
}

public static VCDocument importMathCellML(VCLogger vcLogger, String xmlString) throws Exception {
	throw new Exception("CellML support has been disabled.");
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
		element = XmlUtil.setDefaultNamespace(element, Namespace.getNamespace(XMLTags.VCML_NS));		
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
		container = XmlUtil.setDefaultNamespace(container, Namespace.getNamespace(XMLTags.VCML_NS));		
		simString = XmlUtil.xmlToString(container);
		
		return simString;
	}


	public static BioModel XMLToBioModel(String xmlString) throws XmlParseException {

		return XMLToBioModel(xmlString, true);
	}


	static BioModel XMLToBioModel(String xmlString, boolean printkeys) throws XmlParseException {

long l0 = System.currentTimeMillis();
		BioModel bioModel = null;
		Namespace ns = Namespace.getNamespace(XMLTags.VCML_NS);
		
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
		doc = XmlHelper.importSBML(vcLogger, xmlString);
	} else if (xmlType.equals(XMLTags.CellmlRootNodeTag)) {
		doc = XmlHelper.importMathCellML(vcLogger, xmlString);
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
		Namespace ns = Namespace.getNamespace(XMLTags.VCML_NS);
		
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
		Namespace ns = Namespace.getNamespace(XMLTags.VCML_NS);
		
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
		Namespace ns = Namespace.getNamespace(XMLTags.VCML_NS);
		
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
		Namespace ns = Namespace.getNamespace(XMLTags.VCML_NS);
		
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