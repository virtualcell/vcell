package cbit.vcell.xml.merge;
import cbit.util.xml.XmlParseException;
import cbit.util.xml.XmlUtil;

import org.jdom.Attribute;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Arrays;


/**
This utility class encapsulates the functionality of comparing VCML documents. Contrary to what the class name might indicate,
this class does not extend java.util.Comparator

 * Creation date: (9/21/2004 4:04:50 PM)
 * @author: Rashad Badrawi
 */
public class XmlComparator {

	private XmlComparePolicy xmlComparePolicy = null;
		
	public XmlComparator(XmlComparePolicy xmlComparePolicy) {
		super();
		this.xmlComparePolicy = xmlComparePolicy;
	}


	private boolean compareAtts(ArrayList list1, ArrayList list2) {

	    int result;
	    boolean attFlag = true;
	    //AttComparator attComp = new AttComparator();

	    Attribute atts1[] = (Attribute[]) list1.toArray(new Attribute[list1.size()]);
	    Attribute atts2[] = (Attribute[]) list2.toArray(new Attribute[list2.size()]);

	    if (atts1.length != atts2.length) {
		    return false;
	    }
	    
	    for (int i = 0; i < atts1.length; i++) {
	        //result = Arrays.binarySearch(atts2, atts1[i], attComp);
	        result = -1;
	        for (int j = 0; j < atts2.length; j++) {
	            if (atts2[j].getName().equals(atts1[i].getName())
	                && atts2[j].getValue().equals(atts1[i].getValue())) {
	                result = 0;
	                break;
	            }
	        }
	        if (result < 0) {
	            System.out.println(
	                "Attribute: "
	                    + atts1[i].getName()
	                    + " for element: "
	                    + atts1[i].getParent()
	                    + " not equal/found.");
	            attFlag = false;
	        }
	    }

	    return attFlag;
	}


	public boolean compareEquals(String xmlStr1, String xmlStr2) throws XmlParseException {

		if (xmlStr1 == null || xmlStr1.length() == 0 ||
			xmlStr2 == null || xmlStr2.length() == 0) {
			throw new XmlParseException("Invalid values for the xml strings.");
		}
		return compareXML(xmlStr1, xmlStr2, false);
	}


	//the testAll boolean indicate whether to cover all elements in the test, even if the test fails. 
	public boolean compareXML(Element source, Element target, boolean testAll) {

		boolean elementFlag = true, attFlag = true, textFlag = true;
	    XmlElementSorter elementSorter = new XmlElementSorter();

	    if (!source.getName().equals(target.getName())) {           //wrong element.
	        System.out.println("Element: "  + source.getName() + " with parent: " + source.getParent() + " is lost.");
			elementFlag = false;
			if (!testAll) {
				return elementFlag;
			}
	    }
	    if (!source.getTextTrim().equals(target.getTextTrim())) {
	    	System.out.println("Element: "
	                    + source.getName()
	                    + " with parent: "
	                    + getPathToRoot(source)
	                    + " with text: "
	                    + source.getTextTrim()
	                    + " is lost.");
	        textFlag = false;
	        if (!testAll) {
				return textFlag;
			}
	    }
	    ArrayList atts1 = new ArrayList(source.getAttributes());
	    ArrayList atts2 = new ArrayList(target.getAttributes());
	    attFlag = compareAtts(atts1, atts2);

	    ArrayList children1 = new ArrayList(source.getChildren());
	    ArrayList children2 = new ArrayList(target.getChildren());
	    if (children1.size() != children2.size()) {
	        String pkName = xmlComparePolicy.getKeyAttributeName(source.getName());
	        //sometimes will fail, but better than nothing
	        if (pkName == null)
	            pkName = xmlComparePolicy.getDefaultKeyAttributeName();
	        System.out.println("Element's children: " + source.getName() + ": "
	                + source.getAttributeValue(pkName)
	                + " are partially/completely lost");
	        return false;
	    }
	    Element e1[] = (Element[]) children1.toArray(new Element[children1.size()]);
	    Element e2[] = (Element[]) children2.toArray(new Element[children2.size()]);
	    if (e1.length > 1) {
	        Arrays.sort(e1, elementSorter);
	        Arrays.sort(e2, elementSorter);
	    }
	    boolean bChildrenSame = true;
	    for (int j = 0; j < e1.length; j++) {
	        Element child1 = e1[j];
	        Element child2 = e2[j];
	        if (!compareXML(child1, child2, testAll)){
		        bChildrenSame = false;
				if (!testAll){
		     	   return false;
				}
	        }
	    }
	    return bChildrenSame && elementFlag && attFlag && textFlag;
	}


	public boolean compareXML(String xmlStr1, String xmlStr2, boolean testAll) throws XmlParseException {

		if (xmlStr1.equals(xmlStr2)) {
			System.out.println("The xml strings are identical.");
			return true;
		}
		Element sRoot = XmlUtil.stringToXML(xmlStr1, null);
		Element tRoot = XmlUtil.stringToXML(xmlStr2, null);
		if (compareXML(sRoot, tRoot, testAll)) {
			System.out.println("The two xml trees: " + sRoot.getName() + " are identical with different ordering.");
			return true;
		}
		return false;
	}


	//utility method, can be moved in the xml util class/package
	private String getPathToRoot(Element e) {

		StringBuffer buf = new StringBuffer();
		String attName, attVal;
		
		while (e != null) {
			//will not work for compound pks, but ok for now.
			attName = xmlComparePolicy.getKeyAttributeName(e.getName());
			if (attName == null)
				attName = xmlComparePolicy.getDefaultKeyAttributeName();
			attVal = e.getAttributeValue(attName);
			if (attVal != null) {
				buf.append(e.getName() + ": " +  attVal + "/");
			}
			e = e.getParent();
		}

		return buf.toString();
	}
}