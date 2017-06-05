/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.relationship;

import org.vcell.pathway.BioPaxObject;

public class ConversionTableRow {
	private BioPaxObject bioPaxObject;
//	private Boolean modifiable = new Boolean(false);
	private double stoich;
	private String interactionId;
	private String interactionLabel;
	private String participantType;
	private String id;
	private String location;
	// indicates how to deal with Physical Entities when brought in the Pathway Diagram
	// applies to non-complexes, mainly proteins and small molecules
	// false means to create both a species and a molecular type, true means to create only molecular type
	private boolean moleculeOnly = false;
	
	
	public ConversionTableRow(BioPaxObject bpObject, boolean moleculeOnly) {
		this.bioPaxObject = bpObject;
		this.moleculeOnly = moleculeOnly;
	}
	public ConversionTableRow(BioPaxObject bpObject) {
		this(bpObject, false);
	}
	
	public String interactionId() {
		return interactionId;
	}
	public String interactionLabel() {
		return interactionLabel;
	}
	public void setInteractionId(String newValue) {
		interactionId = newValue;
	}
	public void setInteractionLabel(String newValue) {
		interactionLabel = newValue;
	}
	
	public String participantType() {
		return participantType;
	}
	public void setParticipantType(String newValue) {
		participantType = newValue;
	}
	
//	public Boolean modifiable() {
//		return modifiable;
//	}
//	public void setModifiable(Boolean modifiableNew) {
//		modifiable = modifiableNew;
//	}
	
	public Double stoich() {
		return stoich;
	}
	public void setStoich(Double stoichNew) {
		stoich = stoichNew;
	}
	
	public String id() {
		return id;
	}
	public void setId(String newValue) {
		id = newValue;
	}
	
	public String location() {
		return location;
	}
	public void setLocation(String newValue) {
		location = newValue;
	}
	
	public BioPaxObject getBioPaxObject(){
		return bioPaxObject;
	}
	
	public boolean isMoleculeOnly() {
		return moleculeOnly;
	}
}
