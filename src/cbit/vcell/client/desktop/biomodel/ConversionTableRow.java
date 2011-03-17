package cbit.vcell.client.desktop.biomodel;

import org.vcell.pathway.BioPaxObject;

import cbit.vcell.model.Structure;

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
