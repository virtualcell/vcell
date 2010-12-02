package org.vcell.util.document;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import org.vcell.util.Immutable;
import org.vcell.util.Matchable;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class User implements java.io.Serializable, Matchable, Immutable {
	private String userName = null;
	private KeyValue key = null;
	private static final String VCellTestAccountName = "vcelltestaccount";

	public static final String[] publishers = {"frm","schaff","ion"};

/**
 * User constructor comment.
 */
public User(String userid, KeyValue key) {
	this.userName = userid;
	this.key = key;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj == this){
		return true;
	}
	
	User user = null;
	if (!(obj instanceof User)){
		return false;
	}
	user = (User)obj;

	//
	// since this is immutable, just check key
	//
//	if (!user.getName().equals(getName())){
//		return false;
//	}

	if (!org.vcell.util.Compare.isEqual(key,user.key)){
		return false;
	}
	
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/01 5:28:12 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof User){
		User user = (User)object;
		if (!getName().equals(user.getName())){
			return false;
		}
		return true;
	}
	return false;
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
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public boolean isAlphaTester() {
	final String[] alphaTesters = new String[] { "schaff","ion","les","frm", "boris", "dgross" };
	for (int i = 0; i < alphaTesters.length; i++){
		if (getName().equals(alphaTesters[i])){
			return true;
		}
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 8:33:53 AM)
 * @return boolean
 */
public boolean isPublisher() {
	for(int i=0;i<publishers.length;i+= 1){
		if(userName.equals(publishers[i])){
			return true;
		}
	}

	return false;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public boolean isTestAccount() {
	if (getName().equals(VCellTestAccountName)) {
		return true;
	} else {
		return false;
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return userName+"("+key+")";
}
}