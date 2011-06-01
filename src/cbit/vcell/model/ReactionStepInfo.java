/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;
/**
 * Insert the type's description here.
 * Creation date: (9/15/2003 3:03:19 PM)
 * @author: Jim Schaff
 */
public class ReactionStepInfo implements java.io.Serializable {
	private org.vcell.util.document.KeyValue reactionKey = null;
	private org.vcell.util.document.User owner = null;
	private String bioModelName = null;
	private String reactionName = null;
	private java.util.Date bioModelVersionDate = null;
	private String descriptiveText = null;

/**
 * ReactionStepInfo constructor comment.
 */
public ReactionStepInfo(org.vcell.util.document.KeyValue argReactionKey, org.vcell.util.document.User argOwner, String argBioModelName, String argReactionName, java.util.Date argBioModelVersionDate) {
	super();
	this.reactionKey = argReactionKey;
	this.owner = argOwner;
	this.bioModelName = argBioModelName;
	this.reactionName = argReactionName;
	this.bioModelVersionDate = argBioModelVersionDate;
	refreshDescriptiveText();
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:08:12 PM)
 * @return java.lang.String
 */
public java.lang.String getBioModelName() {
	return bioModelName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:08:12 PM)
 * @return java.util.Date
 */
public java.util.Date getBioModelVersionDate() {
	return bioModelVersionDate;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 4:19:14 PM)
 * @return java.lang.String
 */
public String getDescriptiveText() {
	return this.descriptiveText;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:08:12 PM)
 * @return cbit.vcell.server.User
 */
public org.vcell.util.document.User getOwner() {
	return owner;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:08:12 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getReactionKey() {
	return reactionKey;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 3:08:12 PM)
 * @return java.lang.String
 */
public java.lang.String getReactionName() {
	return reactionName;
}


/**
 * Insert the method's description here.
 * Creation date: (9/15/2003 4:16:36 PM)
 * @return java.lang.String
 */
private void refreshDescriptiveText() {
	descriptiveText = owner.getName()+"_"+bioModelName+"_("+reactionName+")_"+bioModelVersionDate.toString();
}
}
