/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.xml.merge;

import org.jdom.Attribute;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;
/**
 * Insert the type's description here.
 * Creation date: (8/2/2000 4:05:52 PM)
 * @author: 
 */
public class XmlElementComparator implements java.util.Comparator {
	private int status = 0;
	public static int DIFNAMETAG = 1;
	public static int DIFCONTENT = 2;
	public static int DIFSUBELEMENTS = 3;
	public static int DIFATTRIBUTES = 4;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private XmlTreeDiff diffTree = null;
/**
 * This is the default constructor to be used where a reference to the XmlTreeDiff object is passed and stored.
 * Creation date: (8/29/2001 4:11:02 PM)
 * @param param cbit.xml.merge.XmlTreeDiff
 */
public XmlElementComparator(XmlTreeDiff param) {
	super();
	diffTree = param;
}
/**
 * compare method comment.
 */
public int compare(Object o1, Object o2) {
	if (o1 instanceof Element && o2 instanceof Element) {
		return (compare((Element) o1, (Element) o2));
	} else
		if (o1 instanceof Attribute && o2 instanceof Attribute) {
			return (compare((Attribute) o1, (Attribute) o2));
		} else {
			throw new IllegalArgumentException("This comparator can only process Element objects!");
		}
}
/**
 * Insert the method's description here.
 * Creation date: (8/15/2000 3:42:23 PM)
 * @return int
 * @param baselist java.util.List
 * @param modlist java.util.List
 */
public int compare(List baselist, List modifiedlist) {
	int differences = 0;
	
	if ( baselist.size() != modifiedlist.size() ) {
		this.status= this.DIFATTRIBUTES;		
		return -( baselist.size() * modifiedlist.size() );
	}
	Iterator baseiterator = baselist.iterator();
	Iterator moditerator = modifiedlist.iterator();
	while ( baseiterator.hasNext() && moditerator.hasNext() ) {
		int result = compare( baseiterator.next(),moditerator.next() );
		if ( result != 0) {
			this.status= this.DIFATTRIBUTES;
			//return result;
			differences++;
		}
	}		

	return -differences;
}
/**
 * compare method comment.
 */
public int compare(Attribute o1, Attribute o2) {
	if ( o1 == null || o2 == null ){
		throw new IllegalArgumentException("Cannot compare against null's!");
	}
	if ( !o1.getName().equalsIgnoreCase(o2.getName()) ) {				//compare their names
		this.status = this.DIFNAMETAG;
		return java.lang.Integer.MIN_VALUE;
	}
	//compare their content
	//
	// ignore differences in "KeyValue" attributes
	//
	if ( !(o1.getName().equals(cbit.vcell.xml.XMLTags.KeyValueAttrTag) && diffTree.isIgnoringVersionInfo()) && !o1.getValue().equalsIgnoreCase(o2.getValue()) ) {			//compare their value
		this.status = this.DIFCONTENT;
		return -1; //o1.getValue().compareTo( o2.getValue() );
	}
	
	return 0;
}
/**
 * compare method comment.
 */
public int compare(Element o1, Element o2) {
	if ( o1 == null || o2 == null ){
		throw new IllegalArgumentException("Cannot compare against null's!");
	}
	//compare their names
	if ( !diffTree.getMangledName(o1).equalsIgnoreCase(diffTree.getMangledName(o2)) ) {
		this.status = this.DIFNAMETAG;
		return java.lang.Integer.MIN_VALUE;
	}
	//compare their attributes
	List baselist = o1.getAttributes();
	List modifiedlist = o2.getAttributes();
	if ( baselist.size() != modifiedlist.size() ) {
		this.status= this.DIFATTRIBUTES;		
		return -1 ;
	}
	Iterator baseiterator = baselist.iterator();
	Iterator moditerator = modifiedlist.iterator();
	//
	// ignore comparison of Version elements.
	//
	if (o1.getName().equals(cbit.vcell.xml.XMLTags.VersionTag) && diffTree.isIgnoringVersionInfo()){
		return 0;
	}
	while ( baseiterator.hasNext() && moditerator.hasNext() ) {
		if ( compare((Attribute)(baseiterator.next()),(Attribute)(moditerator.next())) != 0) {
			this.status= this.DIFATTRIBUTES;
			return -1;
		}
	}		
	//compare their subelements
	List list1 = o1.getChildren();
	List list2 = o2.getChildren();
	if ( list1.size() != list2.size() ) {
		this.status = this.DIFSUBELEMENTS;
		return (list1.size() - list2.size());
	}
	int i = 0;
	int differences = 0;
	while (i<list1.size()) {
		if ( compare(list1.get(i), list2.get(i)) != 0) {
			differences++;
		}
		i++;
	}
	if ( differences != 0 ) {
		this.status = this.DIFSUBELEMENTS;
		return differences ;
	}
	//compare content
	if ( !o1.getText().equalsIgnoreCase(o2.getText()) ) {			//compare their content
		this.status = this.DIFCONTENT;
		return o1.getText().compareTo(o2.getText());
	}
	
	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2000 10:13:23 AM)
 * @return boolean
 * @param newelement org.jdom.Element
 * @param newlist java.util.List
 */
public boolean contains(Element newelement, List newlist) {
	return (getindexof(newelement,newlist)>= 0 ? true: false );
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2001 4:13:44 PM)
 * @return cbit.xml.merge.XmlTreeDiff
 */
public XmlTreeDiff getDiffTree() {
	return diffTree;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2000 6:04:14 PM)
 * @return int
 * @param newelement org.jdom.Element
 * @param newlist java.util.List
 */
public int getguess(Element baseelement, List newlist) {
	if ( newlist == null || baseelement == null ) {
		return -1;
	}
	if ( !(newlist.isEmpty()) ) {
		Iterator i = newlist.iterator();
		List templist = new java.util.LinkedList();
		while ( i.hasNext() ) {											//try to find one similar 
			Element tempelement = (Element)i.next();					//with the same attributes
			if ( diffTree.getMangledName(baseelement).equalsIgnoreCase(diffTree.getMangledName(tempelement)) ) {
					List baseattributes = baseelement.getAttributes();
					List tempattributes = tempelement.getAttributes();
					int result = compare(baseattributes, tempattributes);
					if ( result == 0 ) {
						return ( newlist.indexOf(tempelement) );
					} else if ( -result < baseattributes.size() ) {
						templist.add(tempelement);
					}	
			}
		}

		
/*		i = templist.iterator();
		while ( i.hasNext() ) {
			Element tempelement = (Element)i.next();
			String basestring = baseelement.getName() + ":" + baseelement.getContent()
			String questionstring = "Should I considerer similar:\n"+
			javax.swing.JOptionPane.showConfirmDialog(null,questionstring);
			System.out.println("temp list size=" + templist.size());
		}*/
	}

	return -1;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2000 10:13:23 AM)
 * @return boolean
 * @param newelement org.jdom.Element
 * @param newlist java.util.List
 */
public int getindexof(Attribute newattribute, List newlist) {
	if (newattribute == null || newlist == null) {
		return -1;
	}
	if (newlist.size() > 0) {
		Iterator i = newlist.iterator();
		while (i.hasNext()) {
			Attribute temp = (Attribute) i.next();
			if ( compare(newattribute, temp) == 0){
				return newlist.indexOf(temp);
			}
		}
	}
	return -1;
}
/**
 * This method searches for an exact match of "newelement" inside "newlist".
 * Creation date: (8/3/2000 10:13:23 AM)
 * @return boolean
 * @param newelement org.jdom.Element
 * @param newlist java.util.List
 */
public int getindexof(Element newelement, List newlist) {
    if (newelement == null || newlist == null) {
        throw new IllegalArgumentException("Invalid NULL parameter received!");
    }
    Iterator i = newlist.iterator();
    while (i.hasNext()) {
        Element temp = (Element) i.next();
        if (compare(newelement, temp) == 0) {
            return newlist.indexOf(temp);
        }
    }

    return -1;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2000 6:04:14 PM)
 * @return int
 * @param newelement org.jdom.Element
 * @param newlist java.util.List
 */
public int getsimilarAttr(Attribute newattr, List newlist) {
	if ( newlist == null || newattr == null ) {
		return -1;
	}
	if ( !(newlist.isEmpty()) ) {
		Iterator i = newlist.iterator();

		while ( i.hasNext() ) {
			Attribute temp = (Attribute)i.next();
			if ( newattr.getName().equalsIgnoreCase(temp.getName()) ) {
				//return ( newlist.indexOf(temp) );
				return 0;                              //or any positive integer. No two attributes (under one element) can have the same name.
			}
		}
	}

	return -1;
}
/**
 * This method looks for a similar object in "newlist" of the "baselement".
 * Creation date: (8/3/2000 6:04:14 PM)
 * @return int
 * @param newelement org.jdom.Element
 * @param newlist java.util.List
 */
public int getsimilarElement(Element baseelement, List newlist) {
	if ( newlist == null || baseelement == null ) {
		throw new IllegalArgumentException("Invalid NULL parameter received!");
	}
	if ( !(newlist.isEmpty()) ) {
		Iterator i = newlist.iterator();
		List templist = new java.util.LinkedList();
		while ( i.hasNext() ) {											//try to find one similar 
			Element tempelement = (Element)i.next();					//with the same attributes
			if ( diffTree.getMangledName(baseelement).equalsIgnoreCase(diffTree.getMangledName(tempelement)) ) {
					templist.add(tempelement);
					List baseattributes = baseelement.getAttributes();
					List tempattributes = tempelement.getAttributes();
					int result = compare(baseattributes, tempattributes);
					if ( result == 0 ) {
						return ( newlist.indexOf(tempelement) );
					}	
			}
		}
		if (templist.size()==1) {
			//System.out.println("Considering elements " + baseelement.getName() + " and " + ((Element)(templist.get(0))).getName());
			return ( newlist.indexOf(templist.get(0)) );
		}
	}
	return -1;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2000 2:28:25 PM)
 * @return int
 */
public int getStatus() {
	return status;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/2001 4:13:44 PM)
 * @param newDiffTree cbit.xml.merge.XmlTreeDiff
 */
public void setDiffTree(XmlTreeDiff newDiffTree) {
	diffTree = newDiffTree;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2000 2:28:25 PM)
 * @param newStatus int
 */
void setStatus(int newStatus) {
	status = newStatus;
}
}
