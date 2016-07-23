/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;
import java.util.StringTokenizer;

import org.vcell.util.Matchable;


/**
 * Insert the type's description here.
 * Creation date: (9/18/2006 12:55:46 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ExternalDataIdentifier implements SimResampleInfoProvider,java.io.Serializable,Matchable{
	private org.vcell.util.document.KeyValue key;
	private org.vcell.util.document.User owner;
	private String name;

/**
 * FieldDataIdentifier constructor comment.
 */
public ExternalDataIdentifier(KeyValue arg_key, org.vcell.util.document.User argOwner,String argName) {
	super();
	key = arg_key;
	owner = argOwner;
	name = argName;
}

public static ExternalDataIdentifier fromTokens(StringTokenizer st){
	return
		new ExternalDataIdentifier(
				KeyValue.fromString(st.nextToken()),
				new User(st.nextToken(),KeyValue.fromString(st.nextToken())),
				st.nextToken()
				);
}
public String toCSVString(){
	return key.toString()+","+owner.getName()+","+owner.getID().toString()+","+name;
}
public String getID() {
	return "SimID_"+getKey().toString()+"_0_";
}


public String getName(){
	return name;
}

/**
 * Insert the method's description here.
 * Creation date: (9/18/2006 12:56:35 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getKey() {
	return key;
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 12:33:44 PM)
 * @return cbit.util.Extent
 */
public org.vcell.util.document.User getOwner() {
	return owner;
}


@Override
public String toString() {
	return getName()+" "+getKey().toString()+" "+getOwner().toString();
}

@Override
public boolean equals(Object obj) {

	if(obj instanceof ExternalDataIdentifier){
		return
		((ExternalDataIdentifier)obj).getKey().equals(getKey())
		&&
		((ExternalDataIdentifier)obj).getName().equals(getName())
		&&
		((ExternalDataIdentifier)obj).getOwner().equals(getOwner());
	}
	return false;
		
}

@Override
public int hashCode() {
	return getKey().hashCode();
}

public boolean compareEqual(Matchable obj) {
	if(this == obj){
		return true;
	}
	if(obj instanceof ExternalDataIdentifier){
		ExternalDataIdentifier compareToExtDataID = (ExternalDataIdentifier)obj;
		if(key.compareEqual(compareToExtDataID.key)){
			if(owner.compareEqual(compareToExtDataID.owner)){
				if(name.equals(compareToExtDataID.name)){
					return true;
				}
			}
		}
	}
	return false;
}
@Override
public int getJobIndex(){
	return 0;
}
@Override
public KeyValue getSimulationKey(){
	return getKey();
}
@Override
public boolean isParameterScanType() {
	return true;//???check this
}
}
