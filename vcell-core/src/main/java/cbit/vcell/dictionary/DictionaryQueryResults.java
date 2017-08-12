/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.dictionary;

import cbit.vcell.model.DBFormalSpecies;
import cbit.vcell.model.DBSpecies;

/**
 * Insert the type's description here.
 * Creation date: (2/22/2003 3:09:39 PM)
 * @author: Frank Morgan
 */
public class DictionaryQueryResults implements java.io.Serializable{

	private String fieldQuery;
	private org.vcell.util.document.User fieldUser;
	private DBFormalSpecies[] fieldDBFormalSpecies;
	private boolean fieldBound;
	private int[] fieldSelection;
/**
 * DictionaryQueryResults constructor comment.
 */
public DictionaryQueryResults(String argQuery,DBFormalSpecies[] argDBFormalSpecies) {
	
	this(argQuery,null,argDBFormalSpecies,null);
}
/**
 * DictionaryQueryResults constructor comment.
 */
public DictionaryQueryResults(String argQuery,DBFormalSpecies[] argDBFormalSpecies,int[] argSelection) {
	
	this(argQuery,null,argDBFormalSpecies,argSelection);
}
/**
 * DictionaryQueryResults constructor comment.
 */
private DictionaryQueryResults(String argQuery,org.vcell.util.document.User argUser,DBFormalSpecies[] argDBFormalSpecies,int[] argSelection) {

	if(argQuery == null || argQuery.length() == 0){
		throw new IllegalArgumentException(this.getClass().getName());
	}
	if(argDBFormalSpecies == null || argDBFormalSpecies.length == 0 || (argSelection != null && argSelection.length > argDBFormalSpecies.length)){
		throw new IllegalArgumentException(this.getClass().getName());
	}
	boolean bAllDBSpecies = (argDBFormalSpecies[0] instanceof DBSpecies);
	for(int i = 0; i < argDBFormalSpecies.length;i+= 1){
		if( (bAllDBSpecies && !(argDBFormalSpecies[0] instanceof DBSpecies)) || (!bAllDBSpecies && (argDBFormalSpecies[0] instanceof DBSpecies))){
			throw new IllegalArgumentException(this.getClass().getName()+ "Must all be of same type");
		}
	}
	if(argUser != null && !(bAllDBSpecies)){
		throw new IllegalArgumentException(this.getClass().getName()+" User must be associated with DBSpecies only");
	}

	this.fieldDBFormalSpecies = argDBFormalSpecies;
	this.fieldQuery = argQuery;
	this.fieldUser = argUser;
	this.fieldBound = bAllDBSpecies;
	this.fieldSelection = argSelection;
}
/**
 * DictionaryQueryResults constructor comment.
 */
public DictionaryQueryResults(String argQuery,org.vcell.util.document.User argUser,DBSpecies[] argDBSpecies) {

	this(argQuery,argUser,(DBFormalSpecies[])argDBSpecies,null);
}
/**
 * DictionaryQueryResults constructor comment.
 */
public DictionaryQueryResults(String argQuery,org.vcell.util.document.User argUser,DBSpecies[] argDBSpecies,int[] argSelection) {

	this(argQuery,argUser,(DBFormalSpecies[])argDBSpecies,argSelection);
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2003 3:12:11 PM)
 * @return cbit.vcell.dictionary.DBFormalSpecies
 */
public DBFormalSpecies[] getDBFormalSpecies() {
	return fieldDBFormalSpecies;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2003 3:12:11 PM)
 * @return java.lang.String
 */
public java.lang.String getQuery() {
	return fieldQuery;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2003 3:12:11 PM)
 * @return cbit.vcell.server.User
 */
public int[] getSelection() {
	return fieldSelection;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2003 3:12:11 PM)
 * @return cbit.vcell.server.User
 */
public org.vcell.util.document.User getUser() {
	return fieldUser;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2003 3:16:56 PM)
 * @return boolean
 */
public boolean isBound() {
	return fieldBound;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2003 3:16:56 PM)
 * @return boolean
 */
public boolean isBoundOnlyUser() {
	return (isBound() && (fieldUser != null));
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2003 6:03:27 PM)
 * @return java.lang.String
 */
public String toString() {
	String selection = "{none}";
	if(getSelection() != null){
		selection = "{";
		for(int i = 0;i<getSelection().length;i+= 1){
			selection = (i != 0?",":"") + selection + getSelection()[i];
		}
		selection = selection + "}";
	}
	
	return "\""+getQuery()+ "\""+
			"("+getDBFormalSpecies().length+")"+ selection +
			" isBound="+isBound()+
			" onlyUser="+isBoundOnlyUser()+
			" user="+getUser();
}
}
