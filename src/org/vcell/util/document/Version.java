package org.vcell.util.document;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.util.Date;
import java.math.BigDecimal;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class Version implements java.io.Serializable, Matchable{
	private KeyValue		versionKey = null;
	private String			versionName = null;
	private User			versionOwner = null;
	private GroupAccess		versionGroupAccess = null;
	
	private KeyValue 		versionBranchPointRef = null;
	private BigDecimal		versionBranchID = null;
	private java.util.Date	versionDate = null;
	private VersionFlag		versionFlag = null;
	private String			versionAnnot= null;

/**
 * Version constructor comment.
 */
public Version(KeyValue versionKeyNew,String versionNameNew,User versionOwnerNew,GroupAccess versionGroupAccessNew,
				KeyValue versionBranchPointRefNew,BigDecimal versionBranchIDNew,
				java.util.Date versionDateNew,VersionFlag versionFlagNew,String versionAnnotNew) {
	super();
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
/**
 * This method was created in VisualAge.
 * @param name java.lang.String
 */
public Version(String name, User owner) {
	this.versionName = name;
	this.versionOwner = owner;
	//
	// default constructor for file based persistence
	//
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public java.util.Vector compare(Object obj) {
	
	java.util.Vector diff = new java.util.Vector();
	
	if (!(obj instanceof Version)) {
		diff.addElement(obj);
	} else {
		Version v = (Version) obj;
		if (!Compare.isEqual(versionKey,v.getVersionKey())) {
			diff.addElement(v.getVersionKey());
		}
		if (!Compare.isEqual(versionName,v.getName())) {
			diff.addElement(v.getName());
		}
		if (!Compare.isEqual(versionOwner,v.getOwner())) {
			diff.addElement(v.getOwner());
		}
		if (!Compare.isEqual(versionGroupAccess,v.getGroupAccess())) {
			diff.addElement(v.getGroupAccess());
		}
		if (!Compare.isEqualOrNull(versionBranchPointRef,v.getBranchPointRefKey())) {
			diff.addElement(v.getBranchPointRefKey());
		}
		if (!Compare.isEqual(versionBranchID,v.getBranchID())) {
			diff.addElement(v.getBranchID());
		}
		if (!Compare.isEqual(versionDate,v.getDate())) {
			diff.addElement(v.getDate());
		}
		if (!Compare.isEqual(versionFlag,v.getFlag())) {
			diff.addElement(v.getFlag());
		}
		if (!Compare.isEqualOrNull(getAnnot(),v.getAnnot())) {
			diff.addElement(v.getAnnot());
		}
	}
	if(diff.size() == 0){
		return null;
	}
	return diff;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
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
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getAnnot() {
	if (versionAnnot==null){
		return "";
	}else{
		return versionAnnot;
	}
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public BigDecimal getBranchID() {
	return versionBranchID;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getBranchPointRefKey() {
	return versionBranchPointRef;
}
/**
 * This method was created in VisualAge.
 * @return java.util.Date
 */
public Date getDate() {
	return versionDate;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public  VersionFlag getFlag() {
	return versionFlag;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.AccessInfo
 */
public GroupAccess getGroupAccess() {
	return versionGroupAccess;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getName() {
	return versionName;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.User
 */
public User getOwner() {
	return versionOwner;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public KeyValue getVersionKey() {
	return versionKey;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer buffer = new StringBuffer();
	java.text.SimpleDateFormat newDateFormatter = new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", java.util.Locale.US);

	buffer.append("[");
	buffer.append("Name(" + versionName + "), ");
	buffer.append("key(" + versionKey + "), ");
	buffer.append("Owner(" + versionOwner + "), ");
	buffer.append("GroupAccess(" + versionGroupAccess + "), ");
	buffer.append("BranchPointRef(" + versionBranchPointRef + "), ");
	buffer.append("BranchID(" + versionBranchID + "), ");
	buffer.append("Flag(" + ((versionFlag!=null)?(versionFlag.toString()):"null") + "), ");
	buffer.append("Date("+((versionDate!=null)?(newDateFormatter.format(versionDate)):"null")+ "), ");
	if (getAnnot().length() < 25) {
		buffer.append("Annot(" + getAnnot() + ")");
	} else {
		buffer.append("Annot(" + "Length=" + getAnnot().length() + ")");
	}
	buffer.append("]");
	
	return buffer.toString();
}
}
