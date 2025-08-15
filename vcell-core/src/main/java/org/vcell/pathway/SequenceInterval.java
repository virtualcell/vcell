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

public class SequenceInterval extends SequenceLocation {
	private SequenceSite sequenceIntervalBegin;
	private SequenceSite sequenceIntervalEnd;
	
	public SequenceSite getSequenceIntervalBegin() {
		return sequenceIntervalBegin;
	}
	public SequenceSite getSequenceIntervalEnd() {
		return sequenceIntervalEnd;
	}
	public void setSequenceIntervalBegin(SequenceSite sequenceIntervalBegin) {
		this.sequenceIntervalBegin = sequenceIntervalBegin;
	}
	public void setSequenceIntervalEnd(SequenceSite sequenceIntervalEnd) {
		this.sequenceIntervalEnd = sequenceIntervalEnd;
	}

	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);

		if (objectProxy == null || concreteObject == null) {
			return;
		}

		if (sequenceIntervalBegin == objectProxy && concreteObject instanceof SequenceSite) {
			sequenceIntervalBegin = (SequenceSite) concreteObject;
		}
		if (sequenceIntervalEnd == objectProxy && concreteObject instanceof SequenceSite) {
			sequenceIntervalEnd = (SequenceSite) concreteObject;
		}
	}
	
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects){
		super.replace(resourceMap, replacedBPObjects);

		if (sequenceIntervalBegin instanceof RdfObjectProxy) {
			RdfObjectProxy proxy = (RdfObjectProxy) sequenceIntervalBegin;
			BioPaxObject concrete = resourceMap.get(proxy.getID());
			if (concrete instanceof SequenceSite) {
				sequenceIntervalBegin = (SequenceSite) concrete;
			}
		}
		if (sequenceIntervalEnd instanceof RdfObjectProxy) {
			RdfObjectProxy proxy = (RdfObjectProxy) sequenceIntervalEnd;
			BioPaxObject concrete = resourceMap.get(proxy.getID());
			if (concrete instanceof SequenceSite) {
				sequenceIntervalEnd = (SequenceSite) concrete;
			}
		}
	}

	public boolean isValidInterval() {
		if (sequenceIntervalBegin == null || sequenceIntervalEnd == null) {
			return false;
		}
		Integer beginPos = sequenceIntervalBegin.getSequencePosition();
		Integer endPos = sequenceIntervalEnd.getSequencePosition();

		if (beginPos == null || endPos == null) {
			return false;
		}
		return beginPos <= endPos;
	}

	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObject(sb, "sequenceIntervalBegin",sequenceIntervalBegin,level);
		printObject(sb, "sequenceIntervalEnd",sequenceIntervalEnd,level);
	}
}
