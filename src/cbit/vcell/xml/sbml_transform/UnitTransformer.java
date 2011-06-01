/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package cbit.vcell.xml.sbml_transform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Changes unit definitions of SBML document
 * @author mlevin
 *
 */
class UnitTransformer extends ASbmlTransformer {
	public static final String Name = "setUnit";
	public static final String All = "all";
	public static final String Default = "default";
	
	private static final Element listOfUnitDefs;
	static {
		Document doc = XmlTools.parseDom(SbmlElements.ListOfUnitDefs);
		listOfUnitDefs = doc.getDocumentElement();		
	}

	public int countParameters() {	return 2;}

	public void addTransformation(String[] parameters, String comment) {
		super.storeTransformationInfo(parameters, comment);
		
		if( All.equals(parameters[0]) ) {
			
		} else {
			String msg = "unknown type \"" + parameters[0] + "\"";
			throw new SbmlTransformException(msg);
		}
		
		if( Default.equals(parameters[1]) ) {
			
		} else {
			String msg = "unknown unit \"" + parameters[1] + "\"";
			throw new SbmlTransformException(msg);
		}
		
		
	}

	public void transform(Document doc) {
		if( null == listOfUnitDefs ) return;
		NodeList nl;
		nl = doc.getElementsByTagName(SbmlElements.Model_tag);
		Element model = (Element)nl.item(0);
		
		nl = model.getElementsByTagName(SbmlElements.ListOfUnitDefs_tag);
		Node listOfUnits = nl.item(0);
		Node listOfUnitsNew = doc.importNode(listOfUnitDefs, true);
		
		if( null != listOfUnits ) {
			model.removeChild(listOfUnits);
		}
		model.insertBefore(listOfUnitsNew, model.getFirstChild());
		doc.normalizeDocument();
	}

	public int countTransformations() {
		return 0;
	}

	public String[] getTransformation(int i) {
		return new String[0];
	}

	public void setDefaultTransformations() {
	}

	public void removeTransformation(int i) {
		throw new IndexOutOfBoundsException("no transformations stored");
	}

	public String getName() {return Name;}


}
