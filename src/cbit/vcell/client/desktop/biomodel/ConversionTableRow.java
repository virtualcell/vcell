/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import org.vcell.pathway.BioPaxObject;

public class ConversionTableRow {
	private BioPaxObject bioPaxObject;
//	private Boolean modifiable = new Boolean(false);
	private double stoich;
	private String interactionName;
	private String participantType;
	private String id;
	private String location;
	
	
	public ConversionTableRow(BioPaxObject bpObject) {
		this.bioPaxObject = bpObject;
	}
	
	public String interactionName() {
		return interactionName;
	}
	public void setInteractionName(String newValue) {
		interactionName = newValue;
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
}
