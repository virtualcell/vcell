package cbit.vcell.vcml;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlParseException;
import cbit.util.xml.XmlUtil;
import cbit.vcell.cellml.CellQualVCTranslator;
import cbit.vcell.cellml.CellQuanVCTranslator;
import cbit.vcell.cellml.VCQualCellTranslator;
import cbit.vcell.cellml.VCQuanCellTranslator;

public abstract class Translator {

	//supported translations
	public static final String VCSB_1 = "VCSB_1";
	public static final String VCSB_2 = "VCSB_2";
	public static final String SBVC_1 = "SBVC_1";
	public static final String SBVC_2 = "SBVC_2";
	public static final String VC_QUAL_CELL = "VCQualCell";
	public static final String VC_QUAN_CELL = "VCQuanCell";
	public static final String CELL_QUAL_VC = "CellQualVC";
	public static final String CELL_QUAN_VC = "CellQuanVC";
	
	//namespaces
	public static final String SBML_NS_1 = "http://www.sbml.org/sbml/level1";
	public static final String SBML_NS_2 = "http://www.sbml.org/sbml/level2";
	public static final String MATHML_NS = "http://www.w3.org/1998/Math/MathML";
	public static final String XHTML_NS = "http://www.w3.org/1999/xhtml";
	public static final String SBML_VCML_NS = "http://www.sbml.org/2001/ns/vcell";
	public static final String VCML_NS = "";
	//public static final String CELLML_NS = "http://www.cellml.org/cellml/1.0#";
	public static final String CELLML_NS_PREFIX = "cellml";
	//schema instance
	protected static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

	//default schema Locations
	protected static final String DEF_SBML1_SL = "http://www.nrcam.uchc.edu/xml/sbml1.xsd";
	protected static final String DEF_SBML2_SL = "http://www.nrcam.uchc.edu/xml/sbml2.xsd";
	protected static final String DEF_VCML_SL = "http://www.nrcam.uchc.edu/xml/biomodel.xsd";
	protected static final String DEF_CELLML_SL = "http://www.nrcam.uchc.edu/xml/cellml.xsd";   		//for the future.
	//SBML stuff
	public static final String SBML_VERSION = "1";
	
	protected Element sRoot, tRoot;
	protected String schemaLocation;
	protected String schemaLocationPropName;
	protected VCLogger vcLogger;
	
  	//needed for the test suite
  	public Document getSource() throws IllegalStateException {

		return new Document((Element)sRoot.clone()); //return a copy of it  	
  	}


	public static Translator getTranslator(String transType) throws IllegalArgumentException {

		if (VC_QUAN_CELL.equals(transType)) {
			return new VCQuanCellTranslator();
		} else if (VC_QUAL_CELL.equals(transType)) {
			return new VCQualCellTranslator();
		} else if (CELL_QUAN_VC.equals(transType)) {
			return new CellQuanVCTranslator();
		} else if (CELL_QUAL_VC.equals(transType)) {
			return new CellQualVCTranslator();
		} else
			throw new IllegalArgumentException("Invalid translation request: " + transType);
	}


	public VCLogger getVCLogger() {

		return vcLogger;
	}


	private void print(Writer outStream, String printedDoc) throws IllegalStateException {
		
		if ( (printedDoc.equals("source") && sRoot == null) ||
			 (printedDoc.equals("target") && tRoot == null) )
		    throw new IllegalStateException ("Nothing to print.");
	    XMLOutputter xmlOut = new XMLOutputter("   ");
	    xmlOut.setNewlines(true);
	  	try {
		  	if (printedDoc.equals("source")) {
			  	xmlOut.setTrimAllWhite(true);
		        xmlOut.output(sRoot, outStream);		        
		  	} else { 
		        xmlOut.output(tRoot, outStream);
		  	}
	  	} catch (IOException e) {
			System.err.println("Unable to write out XML file.");
			e.printStackTrace();
	  	}
	}


	public void printSource(Writer outStream) throws IllegalStateException {

		print(outStream, "source");
	}


	public void printTarget(Writer outStream) throws IllegalStateException {

		print(outStream, "target");
	}


	public void setVCLogger (VCLogger newVCLogger) {

		if (newVCLogger == null) {
			throw new IllegalArgumentException("Invalid TranslationMessager: " + newVCLogger);
		}
		vcLogger = newVCLogger;
	}


	protected abstract void translate() throws Exception;


  	public Document translate (Reader reader, boolean validationOn) throws Exception {

		if (validationOn) {
      		this.sRoot = XmlUtil.readXML(reader, schemaLocation, null, schemaLocationPropName);
      		String errorLog = XmlUtil.getErrorLog();
      		if (errorLog.length() > 0) {
				System.err.println(errorLog);
				if (vcLogger != null) {
					vcLogger.sendMessage(VCLogger.LOW_PRIORITY, TranslationMessage.SCHEMA_VALIDATION_ERROR);
					//vcLogger.sendMessage(VCLogger.LOW_PRIORITY, TranslationMessage.SCHEMA_VALIDATION_ERROR, 
					//                          "The source model has invalid elements/attributes:\n" + errorLog)
				}
      		}
		} else {
			this.sRoot = XmlUtil.readXML(reader, null, null, null);
		} 
  		translate();
		if (vcLogger != null && vcLogger.hasMessages()) {
			vcLogger.sendAllMessages();
		}
  		return new Document(tRoot);
    }


  	//twisted approach, if requesting to validate an existing JDOM tree	
  	public Document translate (Document sDoc, boolean validationOn) throws Exception {
	
		if (sDoc == null)
	  		throw new IllegalArgumentException ("Invalid source document.");
	  	if (!validationOn) {
	  		this.sRoot = sDoc.getRootElement();
	  		translate();
	  		return new Document(tRoot);
	  	} else {
			XMLOutputter xmlOut = new XMLOutputter("   ", true);
			StringWriter sw = new StringWriter();
			try {
				xmlOut.output(sDoc, sw);
				sw.flush();
				return translate(new StringReader(sw.toString()), validationOn);
			} catch (IOException e) {
				e.printStackTrace(System.out);
				throw new XmlParseException("Unable to parse translate document."+" : "+e.getMessage());
			} 				
	  	}
  	}
}