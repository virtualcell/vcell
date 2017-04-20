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

import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class BiochemicalPathwayStep extends PathwayStep {
	private Conversion stepConversion;
	private String stepDirection;
	
	public Conversion getStepConversion() {
		return stepConversion;
	}
	public String getStepDirection() {
		return stepDirection;
	}
	public void setStepConversion(Conversion stepConversion) {
		this.stepConversion = stepConversion;
	}
	public void setStepDirection(String stepDirection) {
		this.stepDirection = stepDirection;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(stepConversion == objectProxy) {
			stepConversion = (Conversion) concreteObject;
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		if(stepConversion instanceof RdfObjectProxy) {
			RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)stepConversion;
			if (rdfObjectProxy.getID() != null){
				BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
				if (concreteObject != null){
					stepConversion = (Conversion) concreteObject;
				}
			}
		}
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"stepConversion",stepConversion,level);
		printString(sb,"stepDirection",stepDirection,level);
	}
}
