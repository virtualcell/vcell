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
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBMeasurable;

import cbit.vcell.biomodel.meta.Identifiable;

public class EntityImpl extends BioPaxObjectImpl implements Entity, Identifiable {
	
	public static final String TYPE_GENE = "Gene";
	public static final String TYPE_PATHWAY = "Pathway";
	public static final String TYPE_INTERACTION = "Interaction";
	public static final String TYPE_PHYSICALENTITY = "PhysicalEntity";
	
	private ArrayList<String> name = new ArrayList<String>();
	private ArrayList<String> availability = new ArrayList<String>();
	private ArrayList<Xref> xRef = new ArrayList<Xref>();
	private ArrayList<Provenance> dataSource = new ArrayList<Provenance>();
	private ArrayList<Evidence> evidence = new ArrayList<Evidence>();
	
	private ArrayList<SBEntity> sbSubEntity = new ArrayList<SBEntity>();
	public ArrayList<SBEntity> getSBSubEntity() {
		return sbSubEntity;
	}
	public void setSBSubEntity(ArrayList<SBEntity> sbSubEntity) {
		this.sbSubEntity = sbSubEntity;
	}
	
	public ArrayList<String> getName() {
		return name;
	}
	public void setName(ArrayList<String> name) {
		this.name = name;
	}

	public ArrayList<Xref> getxRef() {
		return xRef;
	}
	public void setxRef(ArrayList<Xref> xRef) {
		this.xRef = xRef;
	}

	public ArrayList<Provenance> getDataSource() {
		return dataSource;
	}
	public void setDataSource(ArrayList<Provenance> dataSource) {
		this.dataSource = dataSource;
	}

	public ArrayList<Evidence> getEvidence() {
		return evidence;
	}
	public void setEvidence(ArrayList<Evidence> evidence) {
		this.evidence = evidence;
	}

	public ArrayList<String> getAvailability() {
		return this.availability;
	}
	public void setAvailability(ArrayList<String> availability) {
		this.availability = availability;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing == objectProxy) {
				xRef.set(i, (Xref)concreteObject);
			}
		}
		for (int i=0; i<dataSource.size(); i++) {		// Provenance
			Provenance thing = dataSource.get(i);
			if(thing == objectProxy) {
				dataSource.set(i, (Provenance)concreteObject);
			}
		}
		for (int i=0; i<evidence.size(); i++) {
			Evidence thing = evidence.get(i);
			if(thing == objectProxy) {
				evidence.set(i, (Evidence)concreteObject);
			}
		}
		for (int i=0; i<sbSubEntity.size(); i++) {
			SBEntity thing = sbSubEntity.get(i);
			if(thing == objectProxy) {
				sbSubEntity.set(i, (SBEntity)concreteObject);
			}
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		for (int i=0; i<xRef.size(); i++) {
			Xref thing = xRef.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getResource() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getResourceName());
					if (concreteObject != null){
						xRef.set(i, (Xref)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<dataSource.size(); i++) {		// Provenance
			Provenance thing = dataSource.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getResource() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getResourceName());
					if (concreteObject != null){
						dataSource.set(i, (Provenance)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<evidence.size(); i++) {
			Evidence thing = evidence.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getResource() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getResourceName());
					if (concreteObject != null){
						evidence.set(i, (Evidence)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<sbSubEntity.size(); i++) {
			SBEntity thing = sbSubEntity.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getResource() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getResourceName());
					if (concreteObject != null){
						sbSubEntity.set(i, (SBEntity)concreteObject);
					}
				}
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printStrings(sb, "name",name,level);
		printStrings(sb, "availability",availability,level);
		printObjects(sb, "xRef",xRef,level);
		printObjects(sb, "dataSource",dataSource,level);
		printObjects(sb, "evidence",evidence,level);
		printObjects(sb, "sbMeasurable",sbSubEntity,level);
	}
}
