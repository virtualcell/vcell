/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel.meta;

import java.util.StringTokenizer;

import org.vcell.util.TokenMangler;


public class VCID {
	public static final String CLASS_BIOMODEL = "BioModel";
	public static final String CLASS_REACTION_STEP = "ReactionStep";
	public static final String CLASS_SPECIES = "Species";
	public static final String CLASS_SPECIES_CONTEXT = "SpeciesContext";
	public static final String CLASS_STRUCTURE = "Structure";
	
	public static final String CLASS_MOLECULE = "MolecularType";
	public static final String CLASS_REACTION_RULE = "ReactionRule";
	public static final String CLASS_OBSERVABLE = "RbmObservable";

	public static final String CLASS_BIOPAX_OBJECT = "BioPaxObject";
	public static final String CLASS_APPLICATION = "Application";


	private String id;
	private String className;
	private String localName;
	
	@SuppressWarnings("serial")
	public static class InvalidVCIDException extends Exception {
		public InvalidVCIDException(String message){
			super(message);
		}
	};
	
	private VCID(String id){
		this.id = id;
	}
	
	public static VCID fromString(String id) throws InvalidVCIDException {
		VCID vcid = new VCID(id);
		vcid.parse(id);
		return vcid;
	}
	
	public String toASCIIString(){
		return id;
	}
	
	private void parse(String id) throws InvalidVCIDException {
		// parse class
		StringTokenizer token = new StringTokenizer(id,"()",true);
		String class_Name = token.nextToken();
		String beginParentheses = token.nextToken();
		String name = token.nextToken();
		name = TokenMangler.unmangleVCId(name);
		String endParentheses = token.nextToken();
		if (!(beginParentheses.equals("(") && endParentheses.equals(")"))){
			throw new InvalidVCIDException("illegal syntax");
		}
		this.localName = name;
		if (class_Name.length()<1){
			throw new InvalidVCIDException("Invalid VCID: unable to parse class");
		}
		this.className = class_Name;
	}

	public String getClassName(){
		return className;
	}
	
	public String getLocalName(){
		return localName;
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof VCID){
			VCID vcid = (VCID)obj;
			if (vcid.id.equals(id)){
				return true;
			}
		}
		return false;
	}

}
