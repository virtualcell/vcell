/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.cellml;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;

import cbit.util.xml.JDOMTreeWalker;
import cbit.vcell.parser.MathMLTags;
/**
 * Utility translation class, mainly trims the source XML document to the required elements that are of interest in the
 translation.
 * Creation date: (8/12/2003 10:34:56 AM)
 * @author: Rashad Badrawi
 */
public class TransFilter {

	public static final String QUANCELLVC_MANGLE = "1";
	public static final String QUALCELLVC_MANGLE = "2";
	public static final String VCQUALCELL_MANGLE = "3";
	public static final String VCQUANCELL_MANGLE = "4";
	private String [] elements;
	private String [] atts;
	private boolean mangle;
	private String mangleType;
	private Hashtable<String, String> hash;
	private ArrayList<String> ignoredAtts;
	private ArrayList<String> ignoredElements;
	//a temporary variable for reaction names longer than 30 characters. 

	public TransFilter(String [] elements, String [] atts) {

		this(elements, atts, null);
	}


	public TransFilter(String [] elements, String [] atts, String mangleType) {

		if (mangleType == null) {
			mangle = false;
		} else {
			mangle = true;
			this.mangleType = mangleType;
		}
		if (elements != null) {
			Arrays.sort(elements);
			this.elements = elements;
		}
		if (atts != null) {
			Arrays.sort(atts);
			this.atts = atts;
		} 
		hash = new Hashtable<String, String>();
		ignoredAtts = new ArrayList<String>();
		ignoredElements = new ArrayList<String>();
	}


	public void filter(Element e) {

		filterTree(e);
		if (mangle)
			mangle(e);
	}


/**
 @deprecated - need to revisit CellML
**/
@Deprecated
private void filterTree(Element e) {

	@SuppressWarnings("unchecked")
	ArrayList<Attribute> alist = new ArrayList<Attribute>(e.getAttributes());
	for (int i = 0; i < alist.size(); i++) {
		Attribute att = alist.get(i);
		if (!matchesAttribute(att)) {
			if (!ignoredAtts.contains(e.getName() + "." + att.getName())) {
				ignoredAtts.add(e.getName() + "." + att.getName());
			}
			e.removeAttribute(att);
			//System.out.println("\tRemoved att: " + att.getName());
		} else {
			if (mangle) {
				mangle(att);
			}
		}
	}
	//skip contents of math/annotation elements in CELLML.
	@SuppressWarnings("unchecked")
	ArrayList<Element> elist = new ArrayList<Element>(e.getChildren());		
	for (int j = 0; j < elist.size(); j++) { 
		Element child = elist.get(j);
		//also works for the CellML mathML.
		/*if (child.getName().equals(CELLMLTags.MATH) || child.getName().equals(CELLMLTags.ANNOTATION) ||
			child.getName().equals(SBMLTags.NOTES) || child.getName().equals(XMLTags.AnnotationTag))             */
//		if (child.getName().equals(XMLTags.AnnotationTag)) {
//			continue;
//		}
		if (matchesElement(child)) {
			filterTree(child);
		} else { 
			//System.out.println("Removed el.: " + child.getName());
			if (!ignoredElements.contains(child.getName())) {
				ignoredElements.add(child.getName());
			}
			e.removeContent(child);
		}
	}
}


	protected String [] getIgnoredAtts() {

		return ignoredAtts.toArray(new String[ignoredAtts.size()]);
	}


	protected String [] getIgnoredElements() {

		return ignoredElements.toArray(new String[ignoredElements.size()]);		
	}


	 protected static boolean isFloat(String value) {

        boolean bool = true;
        StringBuffer temp = new StringBuffer(value.trim());
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) == ' ')
                temp = temp.deleteCharAt(i);
        }
        try {
            // a hack for formats like 1f or 2D
            int len = temp.length();
            if (Character.isLetter(temp.charAt(len - 1)))
            	bool = false;
        } catch (NumberFormatException e) {
            //System.out.println("exception: Not a number" + temp);
            //needed because of purely numeric expressions
        	for (int i = 0; i < temp.length(); i++) {
        		if (Character.isLetter(temp.charAt(i))) {
            		bool = false;
					break;
            	}
        	}
        }
        
       	return bool;
    }


//currently limited to mangling VCSB, and SBVC
//dropped from original List for VCSB:  XMLTags.StoichiometryAttrTag,XMLTags.KineticsTypeAttrTag, 
//XMLTags.FluxCarrierValenceAttrTag, XMLTags.FluxOptionAttrTag, XMLTags.ForceConstantAttrTag, XMLTags.HasOverrideAttrTag,
//XMLTags.DimensionAttrTag
	private void mangle(Attribute att) {

		if (mangleType.equals(QUANCELLVC_MANGLE) || mangleType.equals(QUALCELLVC_MANGLE))
			mangleCELLVC(att);
		else
			return;
	}


	//This method is needed, in case an identifier in a formula was mangled, Annotations and Initial also have text, 
	//but are not mangled (in case of SBML)
	private void mangle(Element e) {

    	if (mangleType.equals(QUANCELLVC_MANGLE) || mangleType.equals(QUALCELLVC_MANGLE))
			mangleCELLVC(e);
		else
			return;	
	}


	private void mangleCELLVC(Attribute att) {

		String oldValue = att.getValue().trim();
		String newValue;
		
		if (isFloat(oldValue))
			return;
		
		if (Character.isDigit(oldValue.charAt(0))) {
			newValue = "_" + oldValue;
			att.setValue(newValue);
			hash.put(oldValue, newValue);
		} else if (oldValue.equals("x") || oldValue.equals("y") || oldValue.equals("z")) {
			newValue = "_" + oldValue;
			att.setValue(newValue);
			hash.put(oldValue, newValue);
		}
	}


	//mainly mangles the ci element.
	private void mangleCELLVC(Element e) {

    	JDOMTreeWalker walker;
    	Element temp;
    	String key, value;
    	Iterator<String> i = hash.keySet().iterator();
    	while (i.hasNext()) {
	    	key = i.next();
	    	value = hash.get(key);
    		walker = new JDOMTreeWalker(e, new ElementFilter(MathMLTags.IDENTIFIER, Namespace.getNamespace(Translator.MATHML_NS)));
    		while (walker.hasNext()) {
				temp = walker.next();
	      		//System.out.println(key + " " + value + " " + temp.getName() + " " + temp.getTextTrim());
      			if (temp.getTextTrim().equals(key))     
					temp.setText(value);
			}
    	}
	}


	private boolean matchesAttribute(org.jdom.Attribute a) {

		if (atts == null)
			return true;
			
		String aName = a.getName();
		
		return Arrays.binarySearch(atts, aName) >= 0;
	}


	private boolean matchesElement(Element e) {

		if (elements == null)
			return true;
			
		String eName = e.getName();

		return Arrays.binarySearch(elements, eName) >= 0 ;
	}


	//similar to the jdk1.4 String.replaceAll()
	protected static String replaceString(String value, String oldName, String newName) {

		StringBuffer buf; 
		int index = 0; 

		if (isFloat(value) || oldName.equals(newName))
			return value;
		//keep starting from index to avoid infinite loops if there is a problem
		//System.out.println("Before:" + value + " " + oldName + " " + newName);
		while ((index = value.indexOf(oldName, index)) != -1) {
			if ( (index == 0 || !Character.isJavaIdentifierPart(value.charAt(index - 1))) &&
				 ((index + oldName.length() == value.length()) ||
				   !Character.isJavaIdentifierPart(value.charAt(index + oldName.length()))) ){
				buf = new StringBuffer(value);
				buf.replace(index, index + oldName.length(), newName);
				value = buf.toString();
				//System.out.println(value);
				 index += newName.length() - oldName.length();        //new name is longer
			} else {
				 index += oldName.length();   
			}
		}
		//System.out.println("After:" + value);
		
		return value;
	}
}
