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
	private final static String PREVIOUS_DATABASE_VALUE_ADMIN = "special0";
	private final static String PREVIOUS_DATABASE_VALUE_POWERUSER = "special1";
	private final static String PREVIOUS_DATABASE_VALUE_PUBLICATION = "publication";

	public static org.vcell.restclient.model.User userToDTO(User user) {
		org.vcell.restclient.model.User userDTO = new org.vcell.restclient.model.User();
		userDTO.setUserName(user.userName);
		userDTO.setKey(KeyValue.keyValueToDTO(user.key));
		return userDTO;
	}

	public enum SPECIAL_CLAIM {
		admins/*special0*/,
		powerUsers/*special1*/,
		publicationEditors /*publication*/;  // users allowed to modify publications

		public static SPECIAL_CLAIM fromDatabase(String databaseString){
			if (databaseString.equals(PREVIOUS_DATABASE_VALUE_ADMIN)){
				return admins;
			}
			if (databaseString.equals(PREVIOUS_DATABASE_VALUE_POWERUSER)){
				return powerUsers;
			}
			if (databaseString.equals(PREVIOUS_DATABASE_VALUE_PUBLICATION)){
				return publicationEditors;
			}
			return SPECIAL_CLAIM.valueOf(databaseString);
		}

		public String toDatabaseString(){
			return name();
		}
	};//Must match a name 'special' column of 'vc_specialusers' table
	private String userName = null;
	private KeyValue key = null;
	public static final String VCellTestAccountName = "vcelltestaccount";

	public static final User tempUser = new User("temp",new KeyValue("123"));
	public static final String VCELL_GUEST_NAME = "vcellguest";
	public static final User VCELL_GUEST = new User(VCELL_GUEST_NAME,new KeyValue("140220477"));
	
	public static class UserNameComparator implements Serializable,Comparator<User>{
		@Override
		public int compare(User o1, User o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	};

	public static User fromSubject(String subject) {
		String[] parts = subject.split(":");
		return new User(parts[0],new KeyValue(parts[1]));
	}

	public String toSubject() {
		return userName+":"+key;
	}

	public static class SpecialUser extends User implements Serializable, Matchable, Immutable{
		private SPECIAL_CLAIM[] mySpecials;
		public SpecialUser(String userid, KeyValue key,SPECIAL_CLAIM[] mySpecials) {
			super(userid, key);
			this.mySpecials = mySpecials;
		}
		public SPECIAL_CLAIM[] getMySpecials() {
			return mySpecials;
		}

		public boolean isAdmin() {
			return Arrays.asList(mySpecials).contains(SPECIAL_CLAIM.admins);
		}

		public boolean isPublisher() {
			return Arrays.asList(mySpecials).contains(SPECIAL_CLAIM.publicationEditors);
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
	return VCELL_GUEST_NAME +" not allowed to do '"+theOffendingOp+"'.  Register for free during login to use all VCell features.";
}
public static boolean isGuest(String checkThisName) {
	return VCELL_GUEST_NAME.equals(checkThisName);
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
