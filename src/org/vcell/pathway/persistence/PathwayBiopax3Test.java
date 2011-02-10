package org.vcell.pathway.persistence;

import java.io.File;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import org.vcell.pathway.PathwayModel;
import org.vcell.sybil.rdf.NameSpace;

import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.XmlParseException;

public class PathwayBiopax3Test {

	private static final Namespace rdf = Namespace.getNamespace("rdf",NameSpace.RDF.uri);

	private PathwayBiopax3Test() {}		//no instances allowed
	
	public static void main(String args[]){
		try {
			Document document = XmlUtil.readXML(new File("C:\\dan\\reactome biopax\\Reactome3_189445.owl"));
			PathwayReaderBiopax3 pathwayReader = new PathwayReaderBiopax3();
			System.out.println("starting parsing");
			PathwayModel pathwayModel = pathwayReader.parse(document.getRootElement());
			System.out.println("ending parsing");
			pathwayModel.reconcileReferences();
			
			bioModelToXML(pathwayModel);
			
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	static String bioModelToXML(PathwayModel pathwayModel) throws XmlParseException {

		String xmlString = null;
		
		try {
			if (pathwayModel == null){
				throw new IllegalArgumentException("Invalid input for pathwayModel: " + pathwayModel);
			}
			String biopaxVersion = "3.0";
			// create root element 
			Element rootElement = new Element("RDF", rdf);
			rootElement.setAttribute("version", biopaxVersion);
			
			// get element from producer and add it to root element
			PathwayProducerBiopax3 xmlProducer = new PathwayProducerBiopax3();
			xmlProducer.getXML(pathwayModel, rootElement);	// here is work done

			// create xml doc and convert to string
			Document bioDoc = new Document();
			Comment docComment = new Comment("This pathway model was generated in Biopax Version " + biopaxVersion); 
			bioDoc.addContent(docComment);
			bioDoc.setRootElement(rootElement);
			xmlString = XmlUtil.xmlToString(bioDoc, false);
			System.out.println(xmlString);

		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlParseException("Unable to generate PathwayModel XML: " + e.getMessage());
		} 
		
		return xmlString;
	}
	
}
