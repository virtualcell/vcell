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

public class BindingFeatureImpl extends EntityFeatureImpl implements BindingFeature {
	private BindingFeature bindsTo;
	private Boolean intraMolecular;
	
	public BindingFeature getBindsTo() {
		return bindsTo;
	}
	public Boolean getIntraMolecular() {
		return intraMolecular;
	}
	public void setBindsTo(BindingFeature bindsTo) {
		this.bindsTo = bindsTo;
	}
	public void setIntraMolecular(Boolean intraMolecular) {
		this.intraMolecular = intraMolecular;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(bindsTo == objectProxy) {
			bindsTo = (BindingFeature) concreteObject;
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);
		if(bindsTo instanceof RdfObjectProxy) {
			RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)bindsTo;
			if (rdfObjectProxy.getID() != null){
				BioPaxObject concreteObject = resourceMap.get(rdfObjectProxy.getID());
				if (concreteObject != null){
					bindsTo = (BindingFeature) concreteObject;
				}
			}
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"bindsTo",bindsTo,level);
		printBoolean(sb,"intraMolecular",intraMolecular,level);
	}

}
