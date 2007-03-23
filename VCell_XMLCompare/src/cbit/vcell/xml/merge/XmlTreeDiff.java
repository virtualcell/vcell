/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cbit.vcell.xml.merge;
import cbit.vcell.xml.merge.NodeInfo;
import cbit.util.xml.XmlParseException;
import cbit.util.xml.XmlUtil;

import org.jdom.Element;
import org.jdom.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/**
 * This class performs the comparision and merge of two XML documents.
 * Creation date: (8/29/2001 12:33:52 PM)
 * @author: Daniel Lucio
 */
public class XmlTreeDiff {

	private static final String BASELINE_NODE = "baseline_node";
	private static final String MODIFIED_NODE = "modified_node";
//	private java.util.HashMap elementTable;
//	private boolean ignoreVersionInfo = false;         //allows ignoring version info from the modified. 
	private XmlComparePolicy xmlComparePolicy = null;
	private NodeInfo fieldRootNode;
	public static final String COMPARE_DOCS_SAVED = "compare_wth_saved_version";
	public static final String COMPARE_DOCS_OTHER = "compare_with_other";

/**
 * This class performs the comparision and merge of two XML documents..
 * Creation date: (8/29/2001 2:26:52 PM)
 */
public XmlTreeDiff(XmlComparePolicy argXmlComparePolicy) throws java.io.IOException {
	super();
	this.xmlComparePolicy = argXmlComparePolicy;
//	final String RULES_FILENAME = "/rulesTable.xml";
//	elementTable = new java.util.HashMap();
//	java.io.InputStream tableInputStream = getClass().getResourceAsStream(RULES_FILENAME);
//	if (tableInputStream==null){
//		throw new java.io.FileNotFoundException(RULES_FILENAME+" not found");
//	}
//	readTable(tableInputStream);
}


//	public XmlTreeDiff(boolean ignoreVersionInfo) throws java.io.IOException {
//
//		this();
//		this.ignoreVersionInfo = ignoreVersionInfo;
//	}


//	default is to include version information in the comparison.
		public static XmlTreeDiff compareMerge(String xmlBaseString, String xmlModifiedString, String comparisonSetting) throws XmlParseException {

			return compareMerge(xmlBaseString, xmlModifiedString, comparisonSetting, new DefaultXmlComparePolicy());
		}


	/**
	 * compareMerge method comment.
	 */
	public static XmlTreeDiff compareMerge(String xmlBaseString, String xmlModifiedString, 
		                          String comparisonSetting, XmlComparePolicy xmlComparePolicy) throws XmlParseException {
		try {
			if (xmlBaseString == null || xmlModifiedString == null || xmlBaseString.length() == 0 || xmlModifiedString.length() == 0 ||
			    (!XmlTreeDiff.COMPARE_DOCS_SAVED.equals(comparisonSetting) && !XmlTreeDiff.COMPARE_DOCS_OTHER.equals(comparisonSetting)) ) {
		        throw new XmlParseException("Invalid XML comparison params.");
		    }
		    Element baselineRoot = XmlUtil.stringToXML(xmlBaseString, null);            //default setting, no validation
		    Element modifiedRoot = XmlUtil.stringToXML(xmlModifiedString, null);
		    //Merge the Documents
		    XmlTreeDiff merger = new XmlTreeDiff(xmlComparePolicy);
		    NodeInfo top = merger.merge(baselineRoot.getDocument(), modifiedRoot.getDocument(), comparisonSetting);     

		    // Return the result
		    return merger;                                               //return the tree-diff instead of the root node.
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new XmlParseException(e.getMessage());
		}

	}


/**
 * Insert the method's description here.
 * Creation date: (8/29/2001 2:41:50 PM)
 * @return cbit.xml.merge.NodeInfo
 * @param parsed org.jdom.Element
 * @param status int
 */
private NodeInfo createNodes(org.jdom.Element parsed, int status, String nodeSource) {
	NodeInfo parent = null;
	NodeInfo temp;

	if ( parsed != null ){
		parent = new NodeInfo( parsed, status );
		
	    //get all the attributes children
		List children = parsed.getAttributes();
		if ( !children.isEmpty()) {
			Iterator i = children.iterator();
			while ( i.hasNext() ) {
				Attribute atchild = (Attribute) i.next();
				if (xmlComparePolicy.ignoreAttribute(parsed.getName(),atchild.getName())) {   //keep the read-only key values for the baseline.
					if (nodeSource.equals(XmlTreeDiff.BASELINE_NODE)) {
						parent.add(new NodeInfo(atchild, NodeInfo.STATUS_NORMAL));
					} else {
						continue;                        //ignore all the modified key values.
					}
				} else {
					parent.add( new NodeInfo(atchild, status) );
				}
			}
		}
	    //get all the element children
		List elementchildren = parsed.getChildren();
		if ( !elementchildren.isEmpty() ) {
			Iterator i = elementchildren.iterator();			
			while ( i.hasNext() ) {
				Element child = (Element) i.next();
				//ignore all version info.
				if (xmlComparePolicy.ignoreElement(child.getName())) {         
					if (nodeSource.equals(BASELINE_NODE)) {         //keep the baseline versions, but they are all 'normal'
						temp = createNodes (child, NodeInfo.STATUS_NORMAL, nodeSource);
						parent.add( temp);
					} else if (nodeSource.equals(MODIFIED_NODE)) {   //ignore version info for the modified. 
						continue;
					}
				} else {
					temp = createNodes (child, status, nodeSource);
					parent.add( temp);
				}
			}
		}
	}

	return parent;
}


/**
 * This method can return a mangled name for the given Element as: Tagname + _ + Value of a specific attribute if it belongs to a user specific list.
 * Creation date: (8/29/2001 3:15:42 PM)
 * @return java.lang.String
 * @param param org.jdom.Element
 * @exception java.lang.IllegalArgumentException The exception description.
 */
public String getMangledName(Element param) throws java.lang.IllegalArgumentException {
	//check that is not null
	if (param == null)
		throw new IllegalArgumentException("The given Element should not be null!");
	
	String name = param.getName();
	//Check if this element name should be mangled
	if (xmlComparePolicy.getKeyAttributeName(name)!=null){
		String temp = xmlComparePolicy.getKeyAttributeName(name);
		//return mangled name
		//System.out.println(name + "_" + param.getAttributeValue(temp)+"\n");
		return  (name + "_" + param.getAttributeValue(temp));
	}
	
	//just return the name by default
	return name;
}


	public NodeInfo getMergedRootNode() {

		return fieldRootNode;
	}


	/**
 * This method merges two XML Documents and returns a NodeInfo with the differences included.
 * Creation date: (8/29/2001 3:02:38 PM)
 * @return cbit.xml.merge.NodeInfo
 * @param docA org.jdom.Document
 * @param docB org.jdom.Document
 */
public NodeInfo merge(org.jdom.Document docA, org.jdom.Document docB, String comparisonSetting) throws java.io.IOException {

	if (XmlTreeDiff.COMPARE_DOCS_SAVED.equals(comparisonSetting)) {
		fieldRootNode = this.merge(docA.getRootElement(), docB.getRootElement(), comparisonSetting);
	} else if (XmlTreeDiff.COMPARE_DOCS_OTHER.equals(comparisonSetting)) {
		fieldRootNode = this.merge(docB.getRootElement(), docA.getRootElement(), comparisonSetting);
	} else {
		throw new IllegalArgumentException("Invalid comparison setting: " + comparisonSetting);
	}
	
	return fieldRootNode;
}


/**
 * This method merges two XML elements and returns a new one with the differences included.
 * Creation date: (8/29/2001 2:28:42 PM)
 * @return cbit.xml.merge.NodeInfo
 * @param parsedA org.jdom.Element
 * @param parsedB org.jdom.Element
 */
private cbit.vcell.xml.merge.NodeInfo merge(  
    org.jdom.Element parsedA,
    org.jdom.Element parsedB, String comparisonSetting) throws java.io.IOException {
    NodeInfo parent = null; 

    if (parsedA == null || parsedB == null) {
        throw new IllegalArgumentException("An invalid NULL argument was received!");
    }
    String parsedAStr = XmlUtil.xmlToString(parsedA);
    String parsedBStr = XmlUtil.xmlToString(parsedB);
 //   if (getMangledName(parsedA).equalsIgnoreCase(getMangledName(parsedB))) {
 	if ( parsedA.getName().equalsIgnoreCase(parsedB.getName()) ) {
        //ok, they have te same name but...
        if (parsedA.getTextTrim().equalsIgnoreCase(parsedB.getTextTrim())) {
            //check if they have the same content
            //they different content
            parent = new NodeInfo(parsedA, NodeInfo.STATUS_NORMAL);
        } else {
            parent = new ChangedNodeInfo(parsedA, parsedB);
        }
         
        //check for the attributes
        ArrayList templistA = new ArrayList(parsedA.getAttributes());
        ArrayList templistB = new ArrayList(parsedB.getAttributes());
        List restemp = mergeAttrList(templistA, templistB, comparisonSetting);
        Iterator i = restemp.iterator();
        while (i.hasNext()) {
	        NodeInfo temp = (NodeInfo) i.next();
	        parent.add(temp);
	        if (temp.getStatus()!=NodeInfo.STATUS_NORMAL){
		        parent.setStatus(NodeInfo.STATUS_PROBLEM);
	        }
        }

        //check for the subelements
        templistA = sortElementList(parsedA.getChildren());
        templistB = sortElementList(parsedB.getChildren());
        templistA = new ArrayList(parsedA.getChildren());
        templistB = new ArrayList(parsedB.getChildren());
        restemp = mergeElementList(templistA, templistB, comparisonSetting);
        i = restemp.iterator();
        while (i.hasNext()) {
	        NodeInfo temp = (NodeInfo) i.next();
            parent.add(temp);
	        if (temp.getStatus()!=NodeInfo.STATUS_NORMAL){
		        parent.setStatus(NodeInfo.STATUS_PROBLEM);
	        }
        }
    }
 	
    return parent;
}


/**
 * This method merges to attributes list into one.
 * Creation date: (8/29/2001 2:51:33 PM)
 * @return java.util.List
 * @param listA java.util.List
 * @param listB java.util.List
 */
private List mergeAttrList(List listA, List listB, String comparisonSetting) {
    List result = new java.util.ArrayList();    
    XmlElementComparator comparator = new XmlElementComparator(this, xmlComparePolicy);

    Iterator i = listA.iterator();
    while (i.hasNext()) {
        Attribute temp = (Attribute) (i.next());
        if (xmlComparePolicy.ignoreAttribute(null, temp.getName())) {
	        if (XmlTreeDiff.COMPARE_DOCS_SAVED.equals(comparisonSetting)) { 
	        	result.add(new NodeInfo(temp, NodeInfo.STATUS_NORMAL));
	        }
	        i.remove();
			continue;
        } else {
	        int index = comparator.getindexof(temp, listB);
	        if (index >= 0) {
	            //this attribute exists in both!
	            result.add(new NodeInfo(temp, NodeInfo.STATUS_NORMAL));
	            i.remove();
	            listB.remove(index);
	        }
        }
    }
    //If there are already Attributes in ListA, try to find similars in ListB
    i = listA.iterator();
    while (i.hasNext()) {
        Attribute temp = (Attribute) (i.next());

        int index = comparator.getsimilarAttr(temp, listB);
        if (index >= 0) {
            result.add(new ChangedNodeInfo(temp, (Attribute) listB.get(index)));
            //I found one similar
            listB.remove(index);
        } else {
            //It seems that this nodes only exist in ListA
            result.add(new NodeInfo(temp, NodeInfo.STATUS_REMOVED));
        }
    }

    //Add the remaining of listB
    i = listB.iterator();
    while (i.hasNext()) {
        //these nodes only exists in ListB
        Attribute tempAtt = (Attribute)i.next();
        if (xmlComparePolicy.ignoreAttribute(null,tempAtt.getName())) {          //ignore keys in the modified
	        if (XmlTreeDiff.COMPARE_DOCS_OTHER.equals(comparisonSetting)) {
				result.add(new NodeInfo((tempAtt), NodeInfo.STATUS_NORMAL));
	        }
			continue;
        }
        result.add(new NodeInfo((tempAtt), NodeInfo.STATUS_NEW));
    }

    return result;
}


/**
 * This method merges to lists of elements.
 * Creation date: (8/29/2001 2:52:51 PM)
 * @return java.util.List
 * @param listA java.util.List
 * @param listB java.util.List
 */
private List mergeElementList(List list1, List list2, String comparisonSetting) throws java.io.IOException {

	//preliminary sorting.
	ArrayList listA = new ArrayList(sortElementList(list1));
	ArrayList listB = new ArrayList(sortElementList(list2));
	
    List result = new java.util.ArrayList();      
    XmlElementComparator comparator = new XmlElementComparator(this,xmlComparePolicy);
	Iterator i = listA.iterator();
	
    //process all that are equal
    while (i.hasNext()) {
        Element temp = (Element) (i.next());
        //exclude all version nodes from comparison, and load the version of the baseline based on the comparison setting
	 	if (xmlComparePolicy.ignoreElement(temp.getName())) {
		 	if (XmlTreeDiff.COMPARE_DOCS_SAVED.equals(comparisonSetting)) { 
		    	result.add(createNodes(temp, NodeInfo.STATUS_NORMAL, BASELINE_NODE));
		 	}
		 	i.remove();	
		 	continue;
		}
        int index = comparator.getindexof(temp, listB);
        if (index >= 0) { //try to get one equal
	        if (XmlTreeDiff.COMPARE_DOCS_SAVED.equals(comparisonSetting)) {
            	result.add(createNodes(temp, NodeInfo.STATUS_NORMAL, BASELINE_NODE));
        	} else {
            	result.add(createNodes((Element)listB.get(index), NodeInfo.STATUS_NORMAL, BASELINE_NODE));
        	}
            i.remove(); //listA.remove(temp);
            listB.remove(index);
        }
    }

    //try to find similar objects to the remaining in ListA
    i = listA.iterator();
    while (i.hasNext()) {
        Element temp = (Element) (i.next());
        int index = comparator.getsimilarElement(temp, listB);
        if (index >= 0) {
            NodeInfo sharedNode = this.merge(temp, (Element) (listB.get(index)), comparisonSetting);
			result.add(sharedNode);
            listB.remove(index);
        } else {
            //It seems that this Element only exists in ListA
            result.add(createNodes(temp, NodeInfo.STATUS_REMOVED, BASELINE_NODE));
        }
    }
    //
    //Insert the remaining objects of ListB that seems to be unique
    i = listB.iterator();
    while (i.hasNext()) {
	    Element temp = (Element)i.next();
	    //ignore the 'modified' version nodes.
	 	if (xmlComparePolicy.ignoreElement(temp.getName())) { 
		 	if (XmlTreeDiff.COMPARE_DOCS_OTHER.equals(comparisonSetting)) { 
		    	result.add(createNodes(temp, NodeInfo.STATUS_NORMAL, BASELINE_NODE)); 
		 	} 
		 	continue; 
		}
        result.add(createNodes(temp, NodeInfo.STATUS_NEW, MODIFIED_NODE));
    }

    return result;
}


///**
// * This method reads the table from the specified location.
// * Creation date: (8/29/2001 6:37:13 PM)
// * @param location java.lang.String
// */
//private void readTable(java.io.InputStream tableInputStream) throws java.io.IOException {
//
//	//Process the rules files
//	org.jdom.input.SAXBuilder saxparser = new org.jdom.input.SAXBuilder(false);
//	org.jdom.Document doctable = null;
//	try {
//		doctable = saxparser.build(tableInputStream);
//	} catch (org.jdom.JDOMException e) {
//		e.printStackTrace();
//		throw new java.io.IOException("An error occurred when trying to parse the rules file ");
//	}
//	Iterator ruleiterator = doctable.getRootElement().getChildren().iterator();
//	while (ruleiterator.hasNext()) {
//		Element temp = (Element) ruleiterator.next();
//		//System.out.println(temp.getAttributeValue("TagName") + ":" + temp.getAttributeValue("AttrName"));
//		elementTable.put(temp.getAttributeValue("TagName"), temp.getAttributeValue("AttrName"));
//	}
//}


	private ArrayList sortElementList(List list) {

		Element listArray [] = (Element [])list.toArray(new Element[list.size()]);
		Arrays.sort(listArray, new XmlElementSorter(xmlComparePolicy));
		ArrayList sortedList = new ArrayList(Arrays.asList(listArray));

		return sortedList;
	}


	public XmlComparePolicy getXmlComparePolicy() {
		return xmlComparePolicy;
	}


	public void setXmlComparePolicy(XmlComparePolicy xmlComparePolicy) {
		this.xmlComparePolicy = xmlComparePolicy;
	}
}