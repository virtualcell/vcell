/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;
/**
 * Insert the type's description here.
 * Creation date: (6/1/2004 12:05:40 AM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class UserCancelException extends RuntimeException {
	public static final UserCancelException CANCEL_NEW_NAME = new UserCancelException("User canceled when prompted for a new name");
	public static final UserCancelException WARN_UNABLE_CHECK = new UserCancelException("User canceled on warning: unable to check for changes");
	public static final UserCancelException WARN_NO_CHANGES = new UserCancelException("User canceled on warning: no changes");
	public static final UserCancelException CHOOSE_SAVE_AS = new UserCancelException("User chose to save as on warning: no changes");
	public static final UserCancelException CANCEL_DELETE_OLD = new UserCancelException("User chose to cancel deletion of old version");
	public static final UserCancelException CANCEL_FILE_SELECTION = new UserCancelException("User canceled selecting a file");
	public static final UserCancelException CANCEL_DB_SELECTION = new UserCancelException("User canceled selecting from database");
	public static final UserCancelException CANCEL_EDIT_IMG_ATTR = new UserCancelException("User canceled Edit Image Attributes");
	public static final UserCancelException CANCEL_GENERIC = new UserCancelException("User canceled (Generic)");
	public static final UserCancelException CANCEL_XML_TRANSLATION = new UserCancelException("User canceled XML translation process.");
	public static final UserCancelException CANCEL_UNAPPLIED_CHANGES = new UserCancelException("Canceled saving due to unapplied changes to VCMDL");

/**
 * UserCancelException constructor comment.
 * @param s java.lang.String
 */
public UserCancelException(String s) {
	super(s);
}
public static UserCancelException createCustomUserCancelException(String message){
	return new UserCancelException(message)	;
}
}
