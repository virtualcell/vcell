package cbit.vcell.xml;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.StringReader;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.vcell.cellml.CellQuanVCTranslator;
import org.vcell.cellml.Translator;
import org.vcell.sbml.vcell.MathModel_SBMLExporter;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.util.Extent;
import org.vcell.util.document.VCDocument;

import cbit.image.VCImage;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.xml.merge.NodeInfo;
import cbit.xml.merge.TMLPanel;
import cbit.xml.merge.XmlTreeDiff;

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
			// NEW WAY, with XML declaration, vcml element, namespace, version #, etc.
			String vcmlVersion = "0.4";
			// create root vcml element 
			Element vcmlElement = new Element(XMLTags.VcmlRootNodeTag);
			vcmlElement.setAttribute(XMLTags.VersionTag, vcmlVersion);
			// get biomodel element from xmlProducer and add it to vcml root element
			Xmlproducer xmlProducer = new Xmlproducer(printkeys);
			Element biomodelElement = xmlProducer.getXML(bioModel);
			vcmlElement.addContent(biomodelElement);
			//set namespace for vcmlElement
			vcmlElement = XmlUtil.setDefaultNamespace(vcmlElement, Namespace.getNamespace(XMLTags.VCML_NS));	
			// create xml doc with vcml root element and convert to string
			Document bioDoc = new Document();
			Comment docComment = new Comment("This biomodel was generated in VCML Version 0.4"); 
			bioDoc.addContent(docComment);
			bioDoc.setRootElement(vcmlElement);
			xmlString = XmlUtil.xmlToString(bioDoc, false);

//			// OLD WAY
//			Element element = xmlProducer.getXML(bioModel);
//			element = XmlUtil.setDefaultNamespace(element, Namespace.getNamespace(XMLTags.VCML_NS));		
//			xmlString = XmlUtil.xmlToString(element);
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

		// NEW WAY, with XML declaration, vcml element, namespace, version #, etc.
		String vcmlVersion = "0.4";
		// create the root vcml element
		Element vcmlElement = new Element(XMLTags.VcmlRootNodeTag);
		vcmlElement.setAttribute(XMLTags.VersionTag, vcmlVersion);
		// get the geometry element from xmlProducer
		Xmlproducer xmlProducer = new Xmlproducer(printkeys);
		Element geometryElement = xmlProducer.getXML(geometry);
		// add it to root vcml element
		vcmlElement.addContent(geometryElement);
		//set default namespace for vcmlElemebt
		vcmlElement = XmlUtil.setDefaultNamespace(vcmlElement, Namespace.getNamespace(XMLTags.VCML_NS));
		// create xml doc using vcml root element and convert to string
		Document geoDoc = new Document();
		Comment docComment = new Comment("This geometry was generated in VCML Version 0.4"); 
		geoDoc.addContent(docComment);
		geoDoc.setRootElement(vcmlElement);
		geometryString = XmlUtil.xmlToString(geoDoc, false);

//		// OLD WAY
//		Element element = xmlProducer.getXML(geometry);
//		element = XmlUtil.setDefaultNamespace(element, Namespace.getNamespace(XMLTags.VCML_NS));		
//		geometryString = XmlUtil.xmlToString(element);
		
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
	throw new Exception("CellML import to a Biomodel has been disabled.");
}

public static VCDocument importMathCellML(VCLogger vcLogger, String xmlString) throws Exception {
	// throw new Exception("CellML support has been disabled.");

	//checks that the string is not empty
    if (xmlString == null || xmlString.length() == 0 || vcLogger == null) {
        throw new XmlParseException("Invalid params for importing sbml.");
    }
	Translator cellmlTranslator = new CellQuanVCTranslator();
	VCDocument vcDoc = cellmlTranslator.translate(new StringReader(xmlString), false);
	vcDoc.refreshDependencies();
    return vcDoc;
}

public static String mathModelToXML(MathModel mathModel) throws XmlParseException {
	return mathModelToXML(mathModel, true);
}

	static String mathModelToXML(MathModel mathModel, boolean printkeys) throws XmlParseException {

		String xmlString = null;
		
		if (mathModel == null){
			throw new XmlParseException("Invalid input for BioModel: " + mathModel);
		}
		// NEW WAY, with XML declaration, vcml element, namespace, version #, etc.
		String vcmlVersion = "0.4";
		// create root vcml element 
		Element vcmlElement = new Element(XMLTags.VcmlRootNodeTag);
		vcmlElement.setAttribute(XMLTags.VersionTag, vcmlVersion);
		// get mathmodel element from xmlProducer and add it to vcml root element
		Xmlproducer xmlProducer = new Xmlproducer(printkeys);
		Element mathElement = xmlProducer.getXML(mathModel);
		vcmlElement.addContent(mathElement);
		//set namespace for vcmlElement
		vcmlElement = XmlUtil.setDefaultNamespace(vcmlElement, Namespace.getNamespace(XMLTags.VCML_NS));
		// create xml doc with vcml root element and convert to string
		Document mathDoc = new Document();
		Comment docComment = new Comment("This mathmodel was generated in VCML Version 0.4"); 
		mathDoc.addContent(docComment);
		mathDoc.setRootElement(vcmlElement);
		xmlString = XmlUtil.xmlToString(mathDoc, false);

//		// OLD WAY
//		Element element = xmlProducer.getXML(mathModel);
//		element = XmlUtil.setDefaultNamespace(element, Namespace.getNamespace(XMLTags.VCML_NS_ALT));		
//		xmlString = XmlUtil.xmlToString(element);
		
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
		
		if (xmlString == null || xmlString.length() == 0){
			throw new XmlParseException("Invalid xml for BioModel: " + xmlString);
		}

		// NOTES:
		//	* The root element can be <Biomodel> (old-style vcml) OR <vcml> (new-style vcml)
		//	* With the old-style vcml, the namespace was " "
		// 	* With the new-style vcml, there is an intermediate stage where the namespace for <vcml> root 
		//		was set to "http://sourceforge.net/projects/VCell/version0.4" for some models and
		//		"http://sourceforge.net/projects/vcell/vcml" for some models; and the namespace for child element 
		//	 	<biomdel>, etc. was " "
		// 	* The final new-style vcml has (should have) the namespace "http://sourceforge.net/projects/vcell/vcml"
		//		for <vcml> and all children elements.
		// The code below attempts to take care of this situation.
		Element root = XmlUtil.stringToXML(xmlString, null);      
		Namespace ns = null;
		if (root.getName().equals(XMLTags.VcmlRootNodeTag)) {
			// NEW WAY - with xml string containing xml declaration, vcml element, namespace, etc ...
			ns = root.getNamespace();
			Element bioRoot = root.getChild(XMLTags.BioModelTag, ns);
			if (bioRoot == null) {
				bioRoot = root.getChild(XMLTags.BioModelTag);
				//	bioRoot was null, so obtained the <Biomodel> element with namespace " ";
				//	Re-set the namespace so that the correct XMLReader constructor is invoked.
				ns = null;		
			}
			root = bioRoot;
		} 	// else - root is assumed to be old-style vcml with <Biomodel> as root. 

		// common for both new way (with xml declaration, vcml element, etc) and existing way (biomodel is root)
		// If namespace is null, xml is the old-style xml with biomodel as root, so invoke XMLReader without namespace argument.
		XmlReader reader = null;
		if (ns == null) {
			reader = new XmlReader(printkeys);
		} else {
			reader = new XmlReader(printkeys, ns);
		}
		bioModel = reader.getBioModel(root);

		//long l1 = System.currentTimeMillis();
		bioModel.refreshDependencies();
		//long l2 = System.currentTimeMillis();
		//System.out.println("refresh-------- "+((double)(l2-l1))/1000);
		//System.out.println("total-------- "+((double)(l2-l0))/1000);

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
	if (xmlType.equals(XMLTags.VcmlRootNodeTag)) {
		// For now, assuming that <vcml> element has only one child (biomodel, mathmodel or geometry). 
		// Will deal with multiple children of <vcml> Element when we get to model composition.
		java.util.List childElementList = rootElement.getChildren();
		Element modelElement = (Element)childElementList.get(0);	// assuming first child is the biomodel, mathmodel or geometry.
		xmlType = modelElement.getName();
	}
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
	
	if (xmlString == null || xmlString.length() == 0){
		throw new XmlParseException("Invalid xml for Geometry: " + xmlString);
	}

	// NOTES:
	//	* The root element can be <Biomodel> (old-style vcml) OR <vcml> (new-style vcml)
	//	* With the old-style vcml, the namespace was " "
	// 	* With the new-style vcml, there is an intermediate stage where the namespace for <vcml> root 
	//		was set to "http://sourceforge.net/projects/VCell/version0.4" for some models and
	//		"http://sourceforge.net/projects/vcell/vcml" for some models; and the namespace for child element 
	//	 	<biomdel>, etc. was " "
	// 	* The final new-style vcml has (should have) the namespace "http://sourceforge.net/projects/vcell/vcml"
	//		for <vcml> and all children elements.
	// The code below attempts to take care of this situation.
	Element root = XmlUtil.stringToXML(xmlString, null);
	Namespace ns = null;
	if (root.getName().equals(XMLTags.VcmlRootNodeTag)) {
		// NEW WAY - with xml string containing xml declaration, vcml element, namespace, etc ...
		ns = root.getNamespace();
		Element geoRoot = root.getChild(XMLTags.GeometryTag, ns);
		if (geoRoot == null) {
			geoRoot = root.getChild(XMLTags.GeometryTag);
			//	geoRoot was null, so obtained the <Geometry> element with namespace " ";
			//	Re-set the namespace so that the correct XMLReader constructor is invoked.
			ns = null;		
		}
		root = geoRoot;
	} 	// else - root is assumed to be old-style vcml with <Geometry> as root. 

	// common for both new-style (with xml declaration, vcml element, etc) and old-style (geometry is root)
	// If namespace is null, xml is the old-style xml with geometry as root, so invoke XMLReader without namespace argument.
	XmlReader reader = null;
	if (ns == null) {
		reader = new XmlReader(printkeys);
	} else {
		reader = new XmlReader(printkeys, ns);
	}
	geometry = reader.getGeometry(root);
	geometry.refreshDependencies();

	return geometry;		
}


public static VCImage XMLToImage(String xmlString) throws XmlParseException {

	return XMLToImage(xmlString, true);
}


static VCImage XMLToImage(String xmlString, boolean printKeys) throws XmlParseException {

	Namespace ns = Namespace.getNamespace(XMLTags.VCML_NS);
	
	if (xmlString == null || xmlString.length() == 0) {
		throw new XmlParseException("Invalid xml for Image: " + xmlString);
	}
	Element root = XmlUtil.stringToXML(xmlString, null);     //default parser and no validation
	Element extentElement = root.getChild(XMLTags.ExtentTag, ns);
	Element imageElement = root.getChild(XMLTags.ImageTag, ns);
//		Element extentElement = root.getChild(XMLTags.ExtentTag);
//		Element imageElement = root.getChild(XMLTags.ImageTag);
	XmlReader reader = new XmlReader(printKeys, ns);
	Extent extent = reader.getExtent(extentElement);
	VCImage vcImage = reader.getVCImage(imageElement,extent);

	vcImage.refreshDependencies();

	return vcImage;		
}


public static MathModel XMLToMathModel(String xmlString) throws XmlParseException {

	return XMLToMathModel(xmlString, true);
}


static MathModel XMLToMathModel(String xmlString, boolean printkeys) throws XmlParseException {

	MathModel mathModel = null;
	
	if (xmlString == null || xmlString.length() == 0){
		throw new XmlParseException("Invalid xml for MathModel: " + xmlString);
	}

	// NOTES:
	//	* The root element can be <Biomodel> (old-style vcml) OR <vcml> (new-style vcml)
	//	* With the old-style vcml, the namespace was " "
	// 	* With the new-style vcml, there is an intermediate stage where the namespace for <vcml> root 
	//		was set to "http://sourceforge.net/projects/VCell/version0.4" for some models and
	//		"http://sourceforge.net/projects/vcell/vcml" for some models; and the namespace for child element 
	//	 	<biomdel>, etc. was " "
	// 	* The final new-style vcml has (should have) the namespace "http://sourceforge.net/projects/vcell/vcml"
	//		for <vcml> and all children elements.
	// The code below attempts to take care of this situation.
	Element root = XmlUtil.stringToXML(xmlString, null);
	Namespace ns = null;
	if (root.getName().equals(XMLTags.VcmlRootNodeTag)) {
		// NEW WAY - with xml string containing xml declaration, vcml element, namespace, etc ...
		ns = root.getNamespace();
		Element mathRoot = root.getChild(XMLTags.MathModelTag, ns);
		if (mathRoot == null) {
			mathRoot = root.getChild(XMLTags.MathModelTag);
			//	mathRoot was null, so obtained the <Mathmodel> element with namespace " ";
			//	Re-set the namespace so that the correct XMLReader constructor is invoked.
			ns = null;		
		}
		root = mathRoot;
	} 	// else - root is assumed to be old-style vcml with <Mathmodel> as root. 

	// common for both new-style (with xml declaration, vcml element, etc) and old-style (mathmodel is root)
	// If namespace is null, xml is the old-style xml with mathmodel as root, so invoke XMLReader without namespace argument.
	XmlReader reader = null;
	if (ns == null) {
		reader = new XmlReader(printkeys);
	} else {
		reader = new XmlReader(printkeys, ns);
	}
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
		Element simElement = root.getChild(XMLTags.SimulationTag, ns);
		Element mdElement = root.getChild(XMLTags.MathDescriptionTag, ns);
		Element geomElement = root.getChild(XMLTags.GeometryTag, ns);
		XmlReader reader = new XmlReader(true, ns);
		MathDescription md = reader.getMathDescription(mdElement);
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