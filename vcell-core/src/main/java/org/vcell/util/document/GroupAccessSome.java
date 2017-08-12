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

import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (11/15/2001 3:34:49 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class GroupAccessSome extends GroupAccess {
		private java.math.BigDecimal    hash			= null;
		private User[] 					groupMembers 	= null;
		private boolean[]				hiddenMembers	= null;

/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 3:35:22 PM)
 */
public GroupAccessSome(java.math.BigDecimal parmGroupid,java.math.BigDecimal parmHash,User[] parmGroupMembers,boolean[] parmHiddenMembers) {
	super(parmGroupid);
	
	if(	(parmGroupid == null) || 
		(parmGroupid.equals(GROUPACCESS_ALL)) || 
		(parmGroupid.equals(GROUPACCESS_NONE)) || 
		(parmHash == null) || (parmHiddenMembers == null) || 
		(parmGroupMembers == null) || (parmGroupMembers.length == 0) ||
		(parmGroupMembers.length != parmHiddenMembers.length)){
			throw new IllegalArgumentException("GroupAccessSome Constructor Error");
	}
	org.vcell.util.document.KeyValue[] userRef = new org.vcell.util.document.KeyValue[parmGroupMembers.length];
	for(int i = 0;i<parmGroupMembers.length;i+= 1){
		userRef[i] = parmGroupMembers[i].getID();
	}
	if(!parmHash.equals(calculateHash(userRef,parmHiddenMembers))){
		throw new IllegalArgumentException("Hash does not match groupMembers and hiddenMembers");
	}
    hash = parmHash;
    groupMembers = parmGroupMembers;
    hiddenMembers = parmHiddenMembers;
}
/**
 * Insert the method's description here.
 * Creation date: (11/26/2001 3:03:46 PM)
 * @return java.math.BigDecimal
 * @param user cbit.vcell.server.User
 */
public java.math.BigDecimal calculateHashWithNewMember(User newMember,boolean isHiddenFromOwner) {
	org.vcell.util.document.KeyValue[] newMemberSet = new org.vcell.util.document.KeyValue[groupMembers.length+1];
	boolean[] newHidden = new boolean[newMemberSet.length];
	newMemberSet[0] = newMember.getID();
	newHidden[0] = isHiddenFromOwner;
	//
	for(int i = 0;i<groupMembers.length;i+= 1){
		newMemberSet[i+1] = groupMembers[i].getID();
		newHidden[i+1] = hiddenMembers[i];
	}
	return calculateHash(newMemberSet,newHidden);
}
/**
 * Insert the method's description here.
 * Creation date: (11/26/2001 3:03:46 PM)
 * @return java.math.BigDecimal
 * @param user cbit.vcell.server.User
 */
public java.math.BigDecimal calculateHashWithoutMember(User newMember,boolean bHidden){
	if(bHidden && !isHiddenMember(newMember)){
		throw new IllegalArgumentException("This group id="+getGroupid().toString()+" does not contain hidden user="+newMember);
	}
	if(!bHidden && !isNormalMember(newMember)){
		throw new IllegalArgumentException("This group id="+getGroupid().toString()+" does not contain normal user="+newMember);
	}
	org.vcell.util.document.KeyValue[] newMemberSet = new org.vcell.util.document.KeyValue[groupMembers.length-1];
	boolean[] newHidden = new boolean[newMemberSet.length];
	int count = 0;
	for(int i = 0;i<groupMembers.length;i+= 1){
		if(!(groupMembers[i].compareEqual(newMember) && (hiddenMembers[i]==bHidden))){
			newMemberSet[count] = groupMembers[i].getID();
			newHidden[count] = hiddenMembers[i];
			count+= 1;
		}
	}
	return calculateHash(newMemberSet,newHidden);
}
/**
 * Insert the method's description here.
 * Creation date: (12/8/2001 9:52:08 PM)
 * @return java.lang.String
 */
public String getDescription() {
	StringBuffer sb = new StringBuffer();
	sb.append("Access[");
	boolean bFirst = true;
	for(int i = 0; i<groupMembers.length;i+= 1){
		if(hiddenMembers[i]){
			continue;
		}
		if(!bFirst){
			sb.append(",");
		}
		sb.append(groupMembers[i].getName());
		bFirst = false;
	}
	sb.append("]");
 	return sb.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 4:05:36 PM)
 * @return java.math.BigDecimal
 */
public java.math.BigDecimal getHash() {
	return hash;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 4:04:55 PM)
 * @return cbit.vcell.server.User[]
 */
public User[] getHiddenGroupMembers() {
	if(hiddenMembers == null){
		return null;
	}
	Vector<User> v = new Vector<User>();
	for(int c = 0;c < groupMembers.length;c+= 1){
		if(hiddenMembers[c] == true){
			v.add(groupMembers[c]);
		}
	}
	if(v.size() == 0){
		return null;
	}
	User[] retVal = new User[v.size()];
	return (User[]) v.toArray(retVal);
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 4:04:55 PM)
 * @return cbit.vcell.server.User[]
 */
public User[] getNormalGroupMembers() {
	if(hiddenMembers == null){
		return groupMembers;
	}
	Vector<User> v = new Vector<User>();
	for(int c = 0;c < groupMembers.length;c+= 1){
		if(hiddenMembers[c] == false){
			v.add(groupMembers[c]);
		}
	}
	if(v.size() == 0){
		return null;
	}
	User[] retVal = new User[v.size()];
	return (User[]) v.toArray(retVal);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isHiddenMember(User user) {
    for (int c = 0; c < groupMembers.length; c += 1) {
        if (user.compareEqual(groupMembers[c]) && hiddenMembers[c]) {
            return true;
        }
    }
    return false;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isMember(User user) {
	return isHiddenMember(user) || isNormalMember(user);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isNormalMember(User user) {
    for (int c = 0; c < groupMembers.length; c += 1) {
        if (user.compareEqual(groupMembers[c]) && !hiddenMembers[c]) {
            return true;
        }
    }
    return false;
}
/**
 * Insert the method's description here.
 * Creation date: (12/8/2001 9:52:08 PM)
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("Group id="+getGroupid()+" Hash="+getHash()+" Members= ");
	for(int i = 0; i<groupMembers.length;i+= 1){
		if(hiddenMembers[i]){
			sb.append("*");
		}
		sb.append(groupMembers[i]);
		if(i < (groupMembers.length-1)){
			sb.append(",");
		}
	}
 	return sb.toString();
}
}
