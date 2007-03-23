/**
 * 
 */
package cbit.vcell.xml.merge;

import java.util.Comparator;

import org.jdom.Element;


public class XmlElementSorter implements Comparator {
	private XmlComparePolicy xmlComparePolicy = null;
	

	public XmlElementSorter(XmlComparePolicy argXmlComparePolicy){
		this.xmlComparePolicy = argXmlComparePolicy;
	}
	
	public int compare(Object o1, Object o2) {

		int result;
		Element e1 = (Element)o1;
		Element e2 = (Element)o2;

		//sort by their element name
		String eName1 = e1.getName();
		String eName2 = e2.getName();
		result = eName1.compareTo(eName2);
		if (result != 0) {
			return result;
		}
		if (xmlComparePolicy.skipOrdering(eName1)){
			return result; 
		}
		//if they belong to the same element, sort by their 'primary key' attribute.
		String pkName = xmlComparePolicy.getKeyAttributeName(eName1);                       //or eName2
		if (pkName == null)
			pkName = xmlComparePolicy.getDefaultKeyAttributeName();
		int index = pkName.indexOf("&");
		if (index == -1) {
			if (pkName.equals("TEXT")) {
				result = e1.getTextTrim().compareTo(e2.getTextTrim());
			} else {
				result = e1.getAttributeValue(pkName).compareTo(e2.getAttributeValue(pkName));
			}
		} else {         
			java.util.StringTokenizer tokens = new java.util.StringTokenizer(pkName,"&");
			while (tokens.hasMoreElements()){
 				String token = tokens.nextToken();
 				result = e1.getAttributeValue(token).compareTo(e2.getAttributeValue(token));
 				if (result != 0){
  					break;
 				}
			}
		}

		return result;
	}	
}