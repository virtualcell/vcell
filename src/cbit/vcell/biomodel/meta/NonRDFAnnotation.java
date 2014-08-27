/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel.meta;

import java.util.TreeSet;

import org.jdom.Element;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.util.xml.XmlUtil;

public class NonRDFAnnotation implements Matchable {
	private Element xhtmlNotes;
	private Element[] xmlAnnotations;
	private String freeTextAnnotation;
	
	public Element getXhtmlNotes() {
		return xhtmlNotes;
	}
	public void setXhtmlNotes(Element xhtmlNotes) {
		this.xhtmlNotes = xhtmlNotes;
	}
	public Element[] getXmlAnnotations() {
		return xmlAnnotations;
	}
	public void setXmlAnnotations(Element[] xmlAnnotations) {
		this.xmlAnnotations = xmlAnnotations;
	}
	public String getFreeTextAnnotation() {
		return freeTextAnnotation;
	}
	public void setFreeTextAnnotation(String freeTextAnnotation) {
		this.freeTextAnnotation = freeTextAnnotation;
	}
	public boolean isEmpty(){
		if (xhtmlNotes!=null){
			return false;
		}
		if (xmlAnnotations!=null && xmlAnnotations.length>0){
			return false;
		}
		if (freeTextAnnotation!=null && freeTextAnnotation.length()>0){
			return false;
		}
		return true;
	}
	public boolean compareEqual(Matchable obj) {
		if (!(obj instanceof NonRDFAnnotation)) {
			return false;
		}
		NonRDFAnnotation nonRDFAnnotation = (NonRDFAnnotation)obj;
		
		String annot1 = freeTextAnnotation == null || freeTextAnnotation.length() == 0 ? null : freeTextAnnotation;
		String annot2 = nonRDFAnnotation.freeTextAnnotation == null || nonRDFAnnotation.freeTextAnnotation.length() == 0 ? null : nonRDFAnnotation.freeTextAnnotation;
		if (!Compare.isEqualOrNull(annot1, annot2)) {
			return false;
		}
		if (xhtmlNotes != null || nonRDFAnnotation.xhtmlNotes != null) {
			if (xhtmlNotes == null || nonRDFAnnotation.xhtmlNotes == null) {
				return false;
			}			
			if (!Compare.isEqualOrNull(XmlUtil.xmlToString(xhtmlNotes), XmlUtil.xmlToString(nonRDFAnnotation.xhtmlNotes))) {
				return false;
			}			
		} 
		if (xmlAnnotations != null || nonRDFAnnotation.xmlAnnotations != null) {
			if (xmlAnnotations == null || nonRDFAnnotation.xmlAnnotations == null) {
				return false;
			}
			if (xmlAnnotations.length != nonRDFAnnotation.xmlAnnotations.length) {
				return false;
			}
			TreeSet<String> myXmlAnnots = new TreeSet<String>();
			TreeSet<String> otherXmlAnnots = new TreeSet<String>();
			for (int i = 0; i < xmlAnnotations.length; i ++) {
				myXmlAnnots.add(XmlUtil.xmlToString(xmlAnnotations[i]));
				otherXmlAnnots.add(XmlUtil.xmlToString(nonRDFAnnotation.xmlAnnotations[i]));
			}
			return myXmlAnnots.equals(otherXmlAnnots);
		} 
		return true;
	}
}
