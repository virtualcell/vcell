/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class BioSource extends BioPaxObjectImpl implements UtilityClass {
	
	private CellVocabulary cellType;
	private ArrayList<String> name = new ArrayList<String>();
	private TissueVocabulary tissue;
	private ArrayList<Xref> xRef = new ArrayList<Xref>();
	
	public ArrayList<Xref> getxRef() {
		return xRef;
	}
	public void setxRef(ArrayList<Xref> xRef) {
		this.xRef = xRef;
	}
	public CellVocabulary getCellType() {
		return cellType;
	}
	public ArrayList<String> getName() {
		return name;
	}
	public TissueVocabulary getTissue() {
		return tissue;
	}
	public void setCellType(CellVocabulary cellType) {
		this.cellType = cellType;
	}
	public void setName(ArrayList<String> name) {
		this.name = name;
	}
	public void setTissue(TissueVocabulary tissue) {
		this.tissue = tissue;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(cellType == objectProxy) {
			cellType = (CellVocabulary) concreteObject;
		}
		if(tissue == objectProxy) {
			tissue = (TissueVocabulary) concreteObject;
		}
		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing == objectProxy) {
				xRef.set(i, (Xref)concreteObject);
			}
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);

		if(cellType instanceof RdfObjectProxy) {
			RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)cellType;
			if (rdfObjectProxy.getID() != null){
				BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
				if (concreteObject != null){
					cellType = (CellVocabulary) concreteObject;
				}
			}
		}
		if(tissue instanceof RdfObjectProxy) {
			RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)tissue;
			if (rdfObjectProxy.getID() != null){
				BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
				if (concreteObject != null){
					tissue = (TissueVocabulary) concreteObject;
				}
			}
		}
		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						xRef.set(i, (Xref)concreteObject);
					}
				}
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"cellType",cellType,level);
		printObject(sb,"tissue",tissue,level);
		printStrings(sb,"name",name,level);
		printObjects(sb,"xRef",xRef,level);
	}
}
