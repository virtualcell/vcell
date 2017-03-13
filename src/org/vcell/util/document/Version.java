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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.Serializable;
import java.math.BigDecimal;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

@SuppressWarnings("serial")
public class Version implements Serializable, Matchable{
private KeyValue		versionKey = null;
private String			versionName = null;
private User			versionOwner = null;
private GroupAccess		versionGroupAccess = null;

private KeyValue 		versionBranchPointRef = null;
private BigDecimal		versionBranchID = null;
private java.util.Date	versionDate = null;
private VersionFlag		versionFlag = null;
private String			versionAnnot= null;

public Version(KeyValue versionKeyNew, String versionNameNew, User versionOwnerNew,
	GroupAccess versionGroupAccessNew, KeyValue versionBranchPointRefNew,
	BigDecimal versionBranchIDNew, Date versionDateNew,VersionFlag versionFlagNew,
	String versionAnnotNew) {
    this.versionKey = versionKeyNew;
    this.versionName = versionNameNew;
    this.versionOwner= versionOwnerNew;
    this.versionGroupAccess= versionGroupAccessNew;
    this.versionBranchPointRef = versionBranchPointRefNew;
    this.versionBranchID = versionBranchIDNew;
    if (versionDateNew!=null){
	this.versionDate = new java.util.Date(versionDateNew.getTime());
    }else{
	this.versionDate = null;
    }
    this.versionFlag = versionFlagNew;
    this.versionAnnot= versionAnnotNew;
}

public Version(String name, User owner) {
    this.versionName = name;
    this.versionOwner = owner;
}

public List<Object> compare(Object obj) {

    List<Object> diff = new ArrayList<Object>();

    if (!(obj instanceof Version)) {
	diff.add(obj);
    } else {
	Version v = (Version) obj;
	if (!Compare.isEqual(versionKey,v.getVersionKey())) {
	    diff.add(v.getVersionKey());
	}
	if (!Compare.isEqual(versionName,v.getName())) {
	    diff.add(v.getName());
	}
	if (!Compare.isEqual(versionOwner,v.getOwner())) {
	    diff.add(v.getOwner());
	}
	if (!Compare.isEqual(versionGroupAccess,v.getGroupAccess())) {
	    diff.add(v.getGroupAccess());
	}
	if (!Compare.isEqualOrNull(versionBranchPointRef,v.getBranchPointRefKey())) {
	    diff.add(v.getBranchPointRefKey());
	}
	if (!Compare.isEqual(versionBranchID,v.getBranchID())) {
	    diff.add(v.getBranchID());
	}
	if (!Compare.isEqual(versionDate,v.getDate())) {
	    diff.add(v.getDate());
	}
	if (!Compare.isEqual(versionFlag,v.getFlag())) {
	    diff.add(v.getFlag());
	}
	if (!Compare.isEqualOrNull(getAnnot(),v.getAnnot())) {
	    diff.add(v.getAnnot());
	}
    }
    if(diff.size() == 0){
	return null;
    }
    return diff;
}

public boolean compareEqual(Matchable obj) {
    if (!(obj instanceof Version)){
	return false;
    }
    Version v = (Version)obj;

    if (!Compare.isEqual(versionKey,v.getVersionKey())) {
	return false;
    }
    if (!Compare.isEqual(versionName,v.getName())) {
	return false;
    }
    if (!Compare.isEqual(versionOwner,v.getOwner())) {
	return false;
    }
    if (!Compare.isEqual(versionGroupAccess,v.getGroupAccess())) {
	return false;
    }
    if (!Compare.isEqualOrNull(versionBranchPointRef,v.getBranchPointRefKey())) {
	return false;
    }
    if (!Compare.isEqual(versionBranchID,v.getBranchID())) {
	return false;
    }
    if (!Compare.isEqual(versionDate,v.getDate())) {
	return false;
    }
    if (!Compare.isEqual(versionFlag,v.getFlag())) {
	return false;
    }
    if (!Compare.isEqualOrNull(getAnnot(),v.getAnnot())) {
	return false;
    }

    return true;
}

public String getAnnot() {
    if (versionAnnot==null){
	return "";
    }else{
	return versionAnnot;
    }
}

public BigDecimal getBranchID() {
    return versionBranchID;
}

public KeyValue getBranchPointRefKey() {
    return versionBranchPointRef;
}

public Date getDate() {
    return versionDate;
}

public  VersionFlag getFlag() {
    return versionFlag;
}

public GroupAccess getGroupAccess() {
    return versionGroupAccess;
}

public String getName() {
    return versionName;
}

public User getOwner() {
    return versionOwner;
}

public KeyValue getVersionKey() {
    return versionKey;
}

/**
 * return String for identification in log and email 
 * messages
 * @return key elements of version
 */
public String identificationString( ) { 
    StringBuffer buffer = new StringBuffer();
    buffer.append("[");
    buffer.append("Name(" + versionName + "), ");
    buffer.append("key(" + versionKey + "), ");
    buffer.append("Owner(" + versionOwner + "), ");
    buffer.append("GroupAccess(" + versionGroupAccess + "), ");
    buffer.append("BranchPointRef(" + versionBranchPointRef + "), ");
    buffer.append("BranchID(" + versionBranchID + "), ");
    buffer.append("Flag(" + ((versionFlag!=null)?(versionFlag.toString()):"null") + "), ");
    buffer.append("Date("+((versionDate!=null)?(BeanUtils.vcDateFormatter.format(versionDate)):"null")+ "), ");
    if (getAnnot().length() < 25) {
		buffer.append("Annot(" + getAnnot() + ")");
    } else {
		buffer.append("Annot(" + "Length=" + getAnnot().length() + ")");
    }
    buffer.append("]");

    return buffer.toString();
}

/**
 * current implementation returns {@link #identificationString()}
 */
@Override
public String toString() {
	return identificationString();
}

}
