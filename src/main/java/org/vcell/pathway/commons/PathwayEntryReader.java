/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.commons;

import static org.vcell.pathway.PathwayXMLHelper.showUnexpected;

import java.io.File;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import cbit.util.xml.XmlUtil;

public class PathwayEntryReader {

	public int numDuplicates = 0;
	private PathwayEntryModel pathwayModel = new PathwayEntryModel();

	public static void main(String args[]){
		try {
			Document document = XmlUtil.readXML(new File("C:\\dan\\reactome biopax\\PathwayCommons.xml"));
			PathwayEntryReader pathwayReader = new PathwayEntryReader();
			System.out.println("starting parsing");
			PathwayEntryModel pathwayModel = pathwayReader.parse(document.getRootElement());
			System.out.println("ending parsing");
			System.out.println(pathwayModel.show(true));
			System.out.println("Summary: ");
			System.out.println("   " + pathwayModel.getPathwayEntryObjects().size() + " elements.");
			System.out.println("   " + pathwayReader.numDuplicates + " duplicates.");
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	
	public PathwayEntryModel parse(Element rootElement) {
		
		for (Object child : rootElement.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (childElement.getName().equals("search_hit")){
					parseObjectSearchHit(childElement);
				} else {
					showUnexpected(childElement);
				}
			}
		}
		return pathwayModel;
	}
	
	
	private void parseObjectSearchHit(Element element) {
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
//				System.out.println(childElement.getName());
				if (childElement.getName().equals("pathway_list")){
					parseObjectPathwayList(childElement);
				}
			}
		}
	}

	private void parseObjectPathwayList(Element element) {
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
//				System.out.println("    " + childElement.getName());
				if (childElement.getName().equals("pathway")){
					addObjectPathwayEntryObject(childElement);
				}
			}
		}
	}

	private void addObjectPathwayEntryObject(Element element) {
		
		PathwayEntryObject pathwayEntryObject = new PathwayEntryObject();
		addAttributes(pathwayEntryObject, element);
		
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPathwayEntryObject(pathwayEntryObject, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		if(!pathwayModel.contains(pathwayEntryObject)) {
			pathwayModel.add(pathwayEntryObject);
		} else {
			numDuplicates++;
		}
	}

	private boolean addContentPathwayEntryObject(
			PathwayEntryObject pathwayEntryObject, Element element, Element childElement) {

		if (childElement.getName().equals("name")){
			pathwayEntryObject.setName(childElement.getTextTrim());
			return true;
		}
		return false;
	}
	private void addAttributes(PathwayEntryObject bioPaxObject, Element element){
		for (Object attr : element.getAttributes()){
			Attribute attribute = (Attribute)attr;
			if (attribute.getName().equals("primary_id")){
				bioPaxObject.setID(attribute.getValue());
			}
		}
	}
	
	
	
	
}





