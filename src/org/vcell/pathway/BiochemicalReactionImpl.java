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

public class BiochemicalReactionImpl extends ConversionImpl implements BiochemicalReaction {

	private ArrayList<DeltaG> deltaG = new ArrayList<DeltaG>();
	private ArrayList<Double> deltaH = new ArrayList<Double>();
	private ArrayList<Double> deltaS = new ArrayList<Double>();
	private ArrayList<String> ECNumber = new ArrayList<String>();
	private ArrayList<KPrime> kEQ = new ArrayList<KPrime>();

	public ArrayList<DeltaG> getDeltaG() {
		return deltaG;
	}
	public ArrayList<Double> getDeltaH() {
		return deltaH;
	}
	public ArrayList<Double> getDeltaS() {
		return deltaS;
	}
	public ArrayList<String> getECNumber() {
		return ECNumber;
	}
	public ArrayList<KPrime> getkEQ() {
		return kEQ;
	}
	public void setDeltaG(ArrayList<DeltaG> deltaG) {
		this.deltaG = deltaG;
	}
	public void setDeltaH(ArrayList<Double> deltaH) {
		this.deltaH = deltaH;
	}
	public void setDeltaS(ArrayList<Double> deltaS) {
		this.deltaS = deltaS;
	}
	public void setECNumber(ArrayList<String> eCNumber) {
		ECNumber = eCNumber;
	}
	public void setkEQ(ArrayList<KPrime> kEQ) {
		this.kEQ = kEQ;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		for (int i=0; i<deltaG.size(); i++) {
			DeltaG thing = deltaG.get(i);
			if(thing == objectProxy) {
				deltaG.set(i, (DeltaG)concreteObject);
			}
		}
		for (int i=0; i<kEQ.size(); i++) {
			KPrime thing = kEQ.get(i);
			if(thing == objectProxy) {
				kEQ.set(i, (KPrime)concreteObject);
			}
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		
		for (int i=0; i<deltaG.size(); i++) {
			DeltaG thing = deltaG.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						deltaG.set(i, (DeltaG)concreteObject);
					}
				}
			}
		}
		for (int i=0; i<kEQ.size(); i++) {
			KPrime thing = kEQ.get(i);
			if(thing instanceof RdfObjectProxy) {
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)thing;
				if (rdfObjectProxy.getID() != null){
					BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
					if (concreteObject != null){
						kEQ.set(i, (KPrime)concreteObject);
					}
				}
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObjects(sb,"deltaG",deltaG,level);
		printDoubles(sb,"deltaH",deltaH,level);
		printDoubles(sb,"deltaS",deltaS,level);
		printStrings(sb,"ECNumber",ECNumber,level);
		printObjects(sb,"kEQ",kEQ,level);
	}

}
