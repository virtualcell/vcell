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
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import org.vcell.util.Immutable;
import org.vcell.util.Matchable;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class User implements java.io.Serializable, Matchable, Immutable {
	public static enum SPECIALS { special0,special1,special2,special3,special4};//Must match a name 'special' column of 'specialusers' table
	private String userName = null;
	private KeyValue key = null;
	private static final String VCellTestAccountName = "vcelltestaccount";

	public static final String[] publishers = {"frm","schaff","ion"};

	public static final User tempUser = new User("temp",new KeyValue("123"));
	public static final String VCELL_GUEST = "vcellguest";
	
	public static class UserNameComparator implements Serializable,Comparator<User>{
		@Override
		public int compare(User o1, User o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	};

	public static class SpecialUser extends User implements Serializable, Matchable, Immutable{
		private SPECIALS[] mySpecials;
		public SpecialUser(String userid, KeyValue key,SPECIALS[] mySpecials) {
			super(userid, key);
			this.mySpecials = mySpecials;
		}
		public SPECIALS[] getMySpecials() {
			return mySpecials;
		}
//		@Override
//		public boolean compareEqual(Matchable obj) {
//			// TODO Auto-generated method stub
//			if(obj == this) {
//				return true;
//			}
//			boolean superCompare = super.compareEqual(obj);
//			if(obj instanceof SpecialUser) {
//				return superCompare && Compare.isEqualOrNullStrict(((SpecialUser)obj).getMySpecials(), getMySpecials());
//			}
//			return superCompare;
//		}
	}

	/**
 * User constructor comment.
 */
public User(String userid, KeyValue key) {
	this.userName = userid;
	this.key = key;
}

public static String createGuestErrorMessage(String theOffendingOp) {
	return VCELL_GUEST+" not allowed to do '"+theOffendingOp+"'.  Register for free during login to use all VCell features.";
}
public static boolean isGuest(String checkThisName) {
	return VCELL_GUEST.equals(checkThisName);
}
/**
 * @return {@link #equals(Object)}
 */
public boolean compareEqual(Matchable obj) {
	return equals(obj);
}

/**
 * @return true if {@link #key}s match
 */
public boolean equals(Object obj) {
	if (obj == this){
		return true;
	}

	User user = null;
	if (!(obj instanceof User)){
		return false;
	}
	user = (User)obj;

	return org.vcell.util.Compare.isEqual(key,user.key);
}


/**
 * This method was created in VisualAge.
 * @return long
 */
public KeyValue getID() {
	return key;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getName() {
	return userName;
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/01 5:31:05 PM)
 * @return int
 */
public int hashCode() {
	return getName().hashCode();
}

/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 8:33:53 AM)
 * @return boolean
 */
public boolean isPublisher() {
	return Arrays.asList(publishers).contains(userName);
}


/**
 * @return true if this is test account
 */
public boolean isTestAccount() {
	return isTestAccount(getName( ));
}

/**
 * @param accountName non null
 * @return true if accountName is test account
 */
public static boolean isTestAccount(String accountName) {
	return accountName.equals(VCellTestAccountName);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return userName+"("+key+")";
}
}
