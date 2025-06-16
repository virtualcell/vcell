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
import java.util.ArrayList;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.vcell.util.Immutable;
import org.vcell.util.Matchable;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
@Schema(
		discriminatorProperty = "isSpecial",
		discriminatorMapping = {
				@DiscriminatorMapping(value = "no", schema = User.class),
				@DiscriminatorMapping(value = "yes", schema = SpecialUser.class)
		},
		requiredProperties = {"isSpecial"},
		properties = {@SchemaProperty(name = "isSpecial", defaultValue = "no", type = SchemaType.STRING)}
)
public class User implements java.io.Serializable, Matchable, Immutable {
	@JsonProperty
	private String userName = null;
	@JsonProperty
	private KeyValue key = null;

	public final String isSpecial = "no";

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
@JsonIgnore
public KeyValue getID() {
	return key;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
@JsonIgnore
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
@JsonIgnore
public boolean isTestAccount() {
	return isTestAccount(getName( ));
}

/**
 * @param accountName non null
 * @return true if accountName is test account
 */
@JsonIgnore
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
