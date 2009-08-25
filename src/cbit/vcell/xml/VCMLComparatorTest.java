package cbit.vcell.xml;

import cbit.vcell.parser.ExpressionException;
import cbit.util.xml.XmlUtil;

import org.jdom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Tester for VCMLComparator as well as for the XmlUtil parser comparison.
 
 * Creation date: (5/21/2004 11:27:22 AM)
 * @author: Rashad Badrawi
 */
public class VCMLComparatorTest {

	public static final String XML_RT = "-XML_RT";                             //round trip test
	public static final String XML_COMP = "-XML_COMP";                         //comparing two files for equality
	public static final String XML_PC = "-XML_PC";                             //test parsers on a set of files/directory.
	
	private static ArrayList parsers;
	private static ArrayList parserCompRes = new ArrayList();						//carries the points of the graph display
	private static ArrayList fileSizeList = new ArrayList();            
	private static ArrayList xercesList = new ArrayList();
	private static ArrayList crimsonList = new ArrayList();
	private static ArrayList aelfredList = new ArrayList();
	private static ArrayList piccoloList = new ArrayList();
	
	static {
		parsers = new ArrayList(4);
		parsers.add(XmlUtil.PARSER_XERCES);
		parsers.add(XmlUtil.PARSER_CRIMSON);
		parsers.add(XmlUtil.PARSER_AELFRED);
		parsers.add(XmlUtil.PARSER_PICCOLO);
		parserCompRes = new ArrayList(5);
		parserCompRes.add(fileSizeList);
		parserCompRes.add(xercesList);
		parserCompRes.add(crimsonList);
		parserCompRes.add(aelfredList);
		parserCompRes.add(piccoloList);
	 }


private VCMLComparatorTest() {}

		public static boolean compareXML(String fileName1, String fileName2, boolean expectedResult) throws XmlParseException {

		String xmlStr1, xmlStr2;
		try {
			xmlStr1 = XmlUtil.xmlToString(XmlUtil.readXML(new File(fileName1)), false);
			xmlStr2 = XmlUtil.xmlToString(XmlUtil.readXML(new File(fileName2)), false);
			boolean testResult = VCMLComparator.compareEquals(xmlStr1, xmlStr2);
			if (testResult == expectedResult) {
				System.out.println("Tested succeeded for the files: " + fileName1 + " " + fileName2);
				return true;
			} else {
				System.out.println("Tested failed for the files: " + fileName1 + " " + fileName2);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlParseException("Unable to read the source xml files:"+" : "+e.getMessage());
		}
	}
	private static void compareXMLParsers(String xmlFileName) throws XmlParseException {

		long fileSize = new File(xmlFileName).length();
		String xmlStr = XmlUtil.xmlToString(XmlUtil.readXML(new File(xmlFileName)), false);
		//System.out.println("File: " + xmlFileName + " " + fileSize);
		fileSizeList.add(new Long(fileSize));
		for (int i = 0; i < parsers.size(); i++) {
			String curParser = (String)parsers.get(i);
			long startTime = System.currentTimeMillis();
			Element root = XmlUtil.stringToXML(xmlStr, null, curParser).getRootElement();
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			if (XmlUtil.PARSER_XERCES.equals(curParser)) {
				xercesList.add(new Long(duration));
			} else if (XmlUtil.PARSER_CRIMSON.equals(curParser)) {
				crimsonList.add(new Long(duration));
			} else if (XmlUtil.PARSER_AELFRED.equals(curParser)) {
				aelfredList.add(new Long(duration));
			} else if (XmlUtil.PARSER_PICCOLO.equals(curParser)) {
				piccoloList.add(new Long(duration));
			}
			//System.out.println(duration + " " + curParser);
		}
	}
	public static void main (String [] args) throws XmlParseException {

		StringBuffer usage = new StringBuffer();
		usage.append("Usage: -XML_COMP XML_File_Name_1 XML_File_Name_2 [ExpectedResult]\n");
		usage.append("XML_FILE_NAME_1, XML_FILE_NAME_2: URIs of xml files to compare.\n");
	    usage.append("ExpectedResult: true or false, if the two files are expected to be identical or not, respectively\n");
	    usage.append("OR\n");
	    usage.append("Usage: -XML_RT XML_File_Name");
		usage.append("XML_File_Name: name of the XML file to roundtrip: XML-JavaObject-XML");
		System.out.println("XmlUtilTest...");
		if (XML_RT.equals(args[0])) {                  //roundtrip test on a file in the local file system. 
			if (args.length != 2) {
				System.err.println(usage.toString());
			}
			VCMLComparatorTest.processRequest(args[1], XML_RT);
		} else if (XML_COMP.equals(args[0])) {         //comparing two XML files.
			if (args.length < 3 || args.length > 4) {
				System.err.println(usage.toString());
			}
			boolean expectedResult = true;
			if (args.length == 4) {
				expectedResult = new Boolean(args[3]).booleanValue();
			}
			VCMLComparatorTest.compareXML(args[1], args[2], expectedResult);
		} else if (XML_PC.equals(args[0])) {                       //maybe allow a custom parser list in the future
			if (args.length != 2) {
				System.err.println(usage.toString());
			}
			VCMLComparatorTest.processRequest(args[1], XML_PC);
	    } else {
			System.err.println("Invalid mode");
			System.err.println(usage.toString());
		}
		System.exit(0);
	}
	private static void printXMLParseCompRes() {

		int numOfPoints = fileSizeList.size();         //set once, since all should have the same num of points. 
		//print in a tab delimited form
		for (int i = 0; i < numOfPoints; i++) {
			if (i == 0) {
				System.out.println("XML File Size\tXerces\tCrimson\tAelfred\tPiccolo");
			}
			for (int j = 0; j < parserCompRes.size(); j++) {
				ArrayList temp = (ArrayList)parserCompRes.get(j);
				System.out.print(temp.get(i) + "\t");
			}
			System.out.println();
		}
		
	}
	//utility method used by XML roundtrip and XML parser compare
	private static void processRequest(String xmlFileName, String requestType) throws XmlParseException {
	
		final String XML_SUFFIX = ".xml";
		File files = new File(xmlFileName);
		if (files.isDirectory()) {
			FilenameFilter xmlFileFilter = new FilenameFilter () {
				public boolean accept(File dir, String fileName) {

					if (fileName.endsWith(XML_SUFFIX)) {
						return true;
					}

					return false;
				}	
			};
			File [] xmlFiles = files.listFiles(xmlFileFilter);
			for (int i = 0; i < xmlFiles.length; i++) {
				if (requestType.equals(XML_RT)) {
				    testXMLFileRoundTrip(xmlFiles[i].getAbsolutePath());
				} else if (requestType.equals(XML_PC)) {
					compareXMLParsers(xmlFiles[i].getAbsolutePath());
				}
			}
			
		} else {
			if (requestType.equals(XML_RT)) {
				testXMLFileRoundTrip(xmlFileName);
			} else if (requestType.equals(XML_PC)) {
				compareXMLParsers(xmlFileName);
			}
		}
		if (requestType.equals(XML_PC)) {
			printXMLParseCompRes();
		}
	}
//file or directory. Assumes the xml files in that directory are VCML.
	private static void testRoundTrip(String xmlFileName) throws XmlParseException {

		final String XML_SUFFIX = ".xml";
		File files = new File(xmlFileName);
		if (files.isDirectory()) {
			FilenameFilter xmlFileFilter = new FilenameFilter () {
				public boolean accept(File dir, String fileName) {

					if (fileName.endsWith(XML_SUFFIX)) {
						return true;
					}

					return false;
				}	
			};
			File [] xmlFiles = files.listFiles(xmlFileFilter);
			for (int i = 0; i < xmlFiles.length; i++) {
				testXMLFileRoundTrip(xmlFiles[i].getAbsolutePath());
			}
		} else {
			testXMLFileRoundTrip(xmlFileName);
		}
	}
	private static void testXMLFileRoundTrip(String xmlFileName) throws XmlParseException {
    
		System.out.println("Roundtrip test for File: " + xmlFileName);
		String xmlStr1, xmlStr2;
		try {
			xmlStr1 = XmlUtil.xmlToString(XmlUtil.readXML(new File(xmlFileName)), false);
			Element root = XmlUtil.stringToXML(xmlStr1, null).getRootElement();
			XmlReader reader = new XmlReader(true);
			cbit.vcell.biomodel.BioModel biomodel = reader.getBioModel(root);
			Xmlproducer producer = new Xmlproducer(true);
			Element root2 = producer.getXML(biomodel);
			xmlStr2 = XmlUtil.xmlToString(root2);
			boolean testResult = VCMLComparator.compareEquals(xmlStr1, xmlStr2);
			if (testResult) {
				System.out.println("Roundtrip test succeeded for the file: " + xmlFileName);
			} else {
				System.out.println("Roundtrip test failed for the file: " + xmlFileName);
				//System.out.println(xmlStr1);
				System.out.println(xmlStr2);
			}
		} catch (Exception e) {                   //IOException, ExpressionException
			e.printStackTrace();
			throw new XmlParseException("Unable to test xml file: " + xmlFileName+" : "+e.getMessage());
		}
	}
}
