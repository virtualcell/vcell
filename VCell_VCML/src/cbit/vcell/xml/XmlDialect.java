package cbit.vcell.xml;
import java.io.Serializable;

import cbit.util.xml.XmlParseException;
import cbit.vcell.vcml.Translator;

/**
 * This class contains all the constant and data for general XML operations.
   The FileChooser attribute is included here for convenience only, as it is more of a GUI type element. 
 * Creation date: (2/4/2002 12:43:18 PM)
 * @author: Daniel Lucio
 */
/*-------------------------------------------------------------------------------------------------*
 |       \  TO     |       VCML         |         VCML        |       SBML        |     CELLML     |
 |  FROM  \        |    (BioModel)      |      (MathModel)    | (version1,level1) |                |
 |-------------------------------------------------------------------------------------------------|
 |      VCML       |                    |                     |   VCML2SBML_L1V1  |  VCML2CELLML   |
 |   (BioModel)    |                    |                     |vcml2sbml_l1v1.xslt|vcml2cellml.xslt|
 |-------------------------------------------------------------------------------------------------|
 |      VCML       |                    |                     |                   |  VCML2CELLML   |
 |   (MathModel)   |                    |                     |                   |vcml2cellml.xslt|
 |-------------------------------------------------------------------------------------------------|
 |      SBML       | SBML_L1V1_TO_VCML  |                     |                   |                |
 |(version1,level1)|sbml_l1v1_2vcml.xslt|                     |                   |                |
 |-------------------------------------------------------------------------------------------------|
 |     CELLML      |   CELLML2BIOVCML   |   CELLML2MATHVCML   |                   |                |
 |                 |cellml2biomodel.xslt|cellml2mathmodel.xslt|                   |                |
 *-------------------------------------------------------------------------------------------------*/
public class XmlDialect implements Serializable{
	
	private String name		= null;
	private String rootNode = null;
	private String uri		= null;
	
	//import/export controlled list.
	public static final XmlDialect VCML		= new XmlDialect("vcml", XMLTags.BioModelTag, Translator.VCML_NS);
	public static final XmlDialect SBML_L1V1= new XmlDialect("sbml_l1v1", XMLTags.SbmlRootNodeTag, XMLTags.SBML_L1_V1_NAMESPACE_URI);
	public static final XmlDialect SBML_L2V1= new XmlDialect("sbml_l2v1",XMLTags.SbmlRootNodeTag, XMLTags.SBML_L2_V1_NAMESPACE_URI);
	public static final XmlDialect QUAN_CELLML = new XmlDialect("QuanCellml", XMLTags.CellmlRootNodeTag, XMLTags.CELLML_NAMESPACE_URI);
	public static final XmlDialect QUAL_CELLML = new XmlDialect("QualCellml", XMLTags.CellmlRootNodeTag, XMLTags.CELLML_NAMESPACE_URI);

/**
 * XmlDialect constructor comment.
 */
private XmlDialect(String nameArg, String root, String uriArg) {
	super();
	this.name			= nameArg;
	this.rootNode		= root;
	this.uri			= uriArg;
}


/**
 * Insert the method's description here.
 * Creation date: (2/4/2002 1:09:11 PM)
 * @return boolean
 * @param param java.lang.Object
 */
public boolean equals(Object param) {
	if (param instanceof XmlDialect) {
		if (((XmlDialect)param).getName().equals(this.getName())) {
			return true;
		}
	}
	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (2/4/2002 1:05:23 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}


/**
 * This method returns the name of the root node associated with this dialect.
 * Creation date: (2/4/2002 1:05:23 PM)
 * @return java.lang.String
 */
public java.lang.String getRootNode() {
	return rootNode;
}


/**
 * This method returns the 'from' dialect object based on the source root node and uri namespace and the target rootNode.
 * Creation date: (5/15/2003 12:17:13 PM)
 * @return cbit.vcell.xml.XmlDialect
 * @param rootNode java.lang.String
 * @param uri java.lang.String
 */
public static XmlDialect getTargetDialect(String sourceRootNode, String sourceUri, String targetRootNode) throws XmlParseException{
	//Only if they have content do something
	if (sourceRootNode!=null && sourceRootNode.length() !=0 && sourceUri!=null) {

		if ( sourceUri.equalsIgnoreCase(XMLTags.SBML_L1_V1_NAMESPACE_URI)) {           //(sourceRootNode.equalsIgnoreCase(XMLTags.SbmlRootNodeTag) {
			return XmlDialect.SBML_L1V1;
		} else if (sourceUri.equalsIgnoreCase(XMLTags.SBML_L2_V1_NAMESPACE_URI)) {     // || sourceRootNode.equalsIgnoreCase(XMLTags.SbmlRootNodeTag)) {
			return XmlDialect.SBML_L2V1;
		} else if (sourceUri.equalsIgnoreCase(XMLTags.CELLML_NAMESPACE_URI)) {
			if (targetRootNode.equals(XMLTags.BioModelTag))
				return XmlDialect.QUAL_CELLML;
			else
				return XmlDialect.QUAN_CELLML;
		}
	}

	return null;
}


/**
 * This method returns the associated URI.
 * Creation date: (2/4/2002 1:05:23 PM)
 * @return java.lang.String
 */
public java.lang.String getUri() {
	return uri;
}


/**
 * Insert the method's description here.
 * Creation date: (2/4/2002 1:05:23 PM)
 * @return java.lang.String
 */
public int hashCode() {
	return getName().hashCode();
}


/**
 * Insert the method's description here.
 * Creation date: (2/4/2002 1:05:23 PM)
 * @return java.lang.String
 */
public String toString() {
	return ("XmlDialect_"+getName());
}
}