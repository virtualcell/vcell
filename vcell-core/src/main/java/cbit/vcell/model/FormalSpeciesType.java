/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import java.io.Serializable;

/**
 * Insert the type's description here.
 * Creation date: (2/25/2003 3:22:05 PM)
 * @author: Frank Morgan
 */
public class FormalSpeciesType implements Serializable{
	
	private int type;
	    
    private static final int SPECIES_TYPE_COMPOUND = 0;
    private static final int SPECIES_TYPE_ENZYME = 1;
    private static final int SPECIES_TYPE_PROTEIN = 2;
//    private static final int SPECIES_TYPE_UNKNOWN = 3;
    private static final int SPECIES_TYPE_MATCHSEARCH = 4;
    
    private static final String[] names = {"compound","enzyme","protein","unknown"};
    
    public static final FormalSpeciesType compound = new FormalSpeciesType(SPECIES_TYPE_COMPOUND);
    public static final FormalSpeciesType enzyme = new FormalSpeciesType(SPECIES_TYPE_ENZYME);
    public static final FormalSpeciesType protein = new FormalSpeciesType(SPECIES_TYPE_PROTEIN);
//    public static final FormalSpeciesType unknown = new FormalSpeciesType(SPECIES_TYPE_UNKNOWN);
    public static final MatchSearchFormalSpeciesType speciesMatchSearch = new MatchSearchFormalSpeciesType();
    
    public static class MatchSearchFormalSpeciesType extends FormalSpeciesType{
    	private String[] matchCriterias;
    	public MatchSearchFormalSpeciesType(){
    		super(SPECIES_TYPE_MATCHSEARCH);
    	}
    	public void setSQLMatchCriteria(String[] matchCriterias){
    		this.matchCriterias = matchCriterias;
    	}
    	public String[] getMatchCriterias(){
    		return matchCriterias;
    	}
    }
    //public static final int ANY_PROPERTY = 0xFFFFFFFF;
    
    public static final int COMPOUND_ALIAS = 		    0x01;
    public static final int COMPOUND_KEGGID =		    0x02;
    public static final int COMPOUND_CASID = 		    0x04;
    public static final int COMPOUND_FORMULA =		    0x08;

    public static final int ENZYME_ALIAS = 			    0x10;
    public static final int ENZYME_ECNUMBER =		    0x20;
    public static final int ENZYME_SYSNAME = 		    0x40;
    public static final int ENZYME_REACTION =		    0x80;

    public static final int PROTEIN_ALIAS = 		  0x0100;
    public static final int PROTEIN_ORGANISM =		  0x0200;
    public static final int PROTEIN_ACCESSION = 	  0x0400;
    public static final int PROTEIN_SWISSPROTID =	  0x0800;
    public static final int PROTEIN_KEYWORD	=		  0x1000;

    public static final int COMPOUND_ID	=			  0x2000;
    public static final int ENZYME_ID	=			  0x4000;
    public static final int PROTEIN_ID	=			  0x8000;

    public static final int ENZYME_CASID =		  	0x010000;
    public static final int PROTEIN_DESCR =		  	0x020000;

/**
 * FormalSpeciesType constructor comment.
 */
private FormalSpeciesType(int argType){

	this.type = argType;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2003 4:48:19 PM)
 * @return boolean
 */
public boolean bValidProperties(int properties) {
	
	int allProperties = 0x00;
	
	if(type == this.SPECIES_TYPE_COMPOUND){
		
		allProperties = allProperties |
						this.COMPOUND_ALIAS |
						this.COMPOUND_CASID |
						this.COMPOUND_FORMULA |
						this.COMPOUND_KEGGID |
						this.COMPOUND_ID;
						
	}else if (type == this.SPECIES_TYPE_ENZYME){

		allProperties = allProperties |
						this.ENZYME_ALIAS |
						this.ENZYME_ECNUMBER |
						this.ENZYME_REACTION |
						this.ENZYME_SYSNAME |
						this.ENZYME_ID |
						this.ENZYME_CASID;
						
	}else if (type == this.SPECIES_TYPE_PROTEIN){
		
		allProperties = allProperties |
						this.PROTEIN_ALIAS |
						this.PROTEIN_SWISSPROTID |
						this.PROTEIN_ORGANISM |
						this.PROTEIN_ACCESSION |
						this.PROTEIN_KEYWORD |
						this.PROTEIN_DESCR |
						this.PROTEIN_ID;
	}else{
		throw new RuntimeException(type+" Unsupported");
	}
	return ((properties & allProperties) == properties);// || (properties == this.ANY_PROPERTY*/);
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 3:28:08 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if(!(obj instanceof FormalSpeciesType)){
		return false;
	}
	FormalSpeciesType fst = (FormalSpeciesType)obj;
	if(type != fst.type){
		return false;
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 3:50:27 PM)
 * @return java.lang.String
 */
public String getName() {
	return names[type];
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 3:27:39 PM)
 * @return int
 */
public int hashCode() {
	return type;
}
}
