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

/**
 * Insert the type's description here.
 * Creation date: (9/15/2003 3:15:48 PM)
 * @author: Jim Schaff
 */
public class ReactionQuerySpec implements java.io.Serializable {

	private String anyReactionParticipantLikeString = null;
	private DBFormalSpecies anyReactionParticipantBoundSpecies = null;

	private String reactantLikeString = null;
	private DBFormalSpecies reactantBoundSpecies = null;

	private String catalystLikeString = null;
	private DBFormalSpecies catalystBoundSpecies = null;

	private String productLikeString = null;
	private DBFormalSpecies productBoundSpecies = null;

/**
 * ReactionQuerySpec constructor comment.
 */
public ReactionQuerySpec(String argAnyReactionParticipantLikeString, DBFormalSpecies argAnyReactionParticipantBoundSpecies) {
	super();
	this.anyReactionParticipantLikeString = argAnyReactionParticipantLikeString;
	this.anyReactionParticipantBoundSpecies = argAnyReactionParticipantBoundSpecies;
}


/**
 * ReactionQuerySpec constructor comment.
 */
public ReactionQuerySpec(String argReactantLikeString, DBFormalSpecies argReactantBoundSpecies,
    					String argCatalystLikeString, DBFormalSpecies argCatalystBoundSpecies,
    					String argProductLikeString, DBFormalSpecies argProductBoundSpecies) {
    super();
    this.reactantLikeString = argReactantLikeString;
    this.reactantBoundSpecies = argReactantBoundSpecies;
    this.catalystLikeString = argCatalystLikeString;
    this.catalystBoundSpecies = argCatalystBoundSpecies;
    this.productLikeString = argProductLikeString;
    this.productBoundSpecies = argProductBoundSpecies;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:30:32 PM)
 * @return cbit.vcell.dictionary.DBFormalSpecies
 */
public cbit.vcell.model.DBFormalSpecies getAnyReactionParticipantBoundSpecies() {
	return anyReactionParticipantBoundSpecies;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:30:32 PM)
 * @return java.lang.String
 */
public java.lang.String getAnyReactionParticipantLikeString() {
	return anyReactionParticipantLikeString;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:30:32 PM)
 * @return cbit.vcell.dictionary.DBFormalSpecies
 */
public cbit.vcell.model.DBFormalSpecies getCatalystBoundSpecies() {
	return catalystBoundSpecies;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:30:32 PM)
 * @return java.lang.String
 */
public java.lang.String getCatalystLikeString() {
	return catalystLikeString;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:30:32 PM)
 * @return cbit.vcell.dictionary.DBFormalSpecies
 */
public cbit.vcell.model.DBFormalSpecies getProductBoundSpecies() {
	return productBoundSpecies;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:30:32 PM)
 * @return java.lang.String
 */
public java.lang.String getProductLikeString() {
	return productLikeString;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:30:32 PM)
 * @return cbit.vcell.dictionary.DBFormalSpecies
 */
public cbit.vcell.model.DBFormalSpecies getReactantBoundSpecies() {
	return reactantBoundSpecies;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:30:32 PM)
 * @return java.lang.String
 */
public java.lang.String getReactantLikeString() {
	return reactantLikeString;
}
}
