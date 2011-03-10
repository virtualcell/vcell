package org.vcell.pathway;

import org.jdom.Attribute;
import org.jdom.Element;

public class PathwayXMLHelper {

//	public static final Namespace vcns = Namespace.getNamespace("vcns", "vcns-something");
	public static final String schemaString = new String("http://www.w3.org/2001/XMLSchema#string");
	public static final String schemaInt = new String("http://www.w3.org/2001/XMLSchema#int");
	public static final String schemaBoolean = new String("http://www.w3.org/2001/XMLSchema#boolean");
	public static final String schemaDouble = new String("http://www.w3.org/2001/XMLSchema#double");
	
//	private static final String SIM_CONTAINER = XMLTags.SimulationSpecTag;
//	private static final Namespace rdf = Namespace.getNamespace("rdf",NameSpace.RDF.uri);

	private PathwayXMLHelper() {}		//no instances allowed

	private static String getElementPathString(Element childElement) {
		StringBuffer buffer = new StringBuffer();
		Element element = childElement;
		while (element!=null){
			if (buffer.length()==0){
				buffer.append(element.getName());
			}else{
				buffer.insert(0,element.getName()+"/");
			}
			element = element.getParent();
		}
		return buffer.toString();
	}
	public static void showUnexpected(Attribute attribute) {
		System.out.println("Unexpected attribute " + getElementPathString(attribute.getParent()) + " << " + attribute.getQualifiedName());
	}
	public static void showUnexpected(Object object) {
		System.out.println("Unexpected object " + object.toString());
	}
	public static void showIgnored(Attribute attribute) {
		System.out.println("Ignored attribute " + getElementPathString(attribute.getParent()) + " << " + attribute.getQualifiedName());
	}
	public static void showUnexpected(Element childElement) {
		System.out.println("Unexpected element " + getElementPathString(childElement));
	}
	public static void showIgnored(Element childElement, String reason) {
		if (!reason.contains("?")){
			return;
		}
		System.out.println("Ignoring element " + getElementPathString(childElement) + "   " + reason);
	}
	
}
