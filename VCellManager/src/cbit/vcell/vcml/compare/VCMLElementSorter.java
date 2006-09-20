/**
 * 
 */
package cbit.vcell.vcml.compare;

import java.util.Comparator;

import org.jdom.Element;

import cbit.vcell.xml.XMLTags;

public class VCMLElementSorter implements Comparator {

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
		if (eName1.equals(XMLTags.CoordinateTag)) {                         //or eName2, no re-ordering for Coordinate elements. 
			return result; 
		}
		//if they belong to the same element, sort by their 'primary key' attribute.
		String pkName = (String)VCMLComparator.map.get(eName1);                       //or eName2
		if (pkName == null)
			pkName = XMLTags.NameAttrTag;
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