/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

/**
 * Insert the type's description here.
 * Creation date: (9/7/2004 9:48:34 AM)
 * @author: Jim Schaff
 */
public class SimpleUserMessage {
	private String message = null;
	private String options[] = null;
	private String defaultSelection = null;
	public final static String TEXT_REPLACE = "<<<REPLACE>>>";

	public final static String OPTION_REVERT_TO_SAVED = "Revert to saved";
	public final static String OPTION_YES = "Yes";
	public final static String OPTION_NO = "No";
	public final static String OPTION_OK = "OK";
	public final static String OPTION_CANCEL = "Cancel";
	public final static String OPTION_DISCARD_CHANGES = "Discard changes";
	public final static String OPTION_OVERWRITE_FILE = "Overwrite file";
	public final static String OPTION_SAVE_AS_NEW_EDITION = "Save as new edition";
	public final static String OPTION_DELETE = "Delete";
	public final static String OPTION_CONTINUE= "Continue";
	public final static String OPTION_CLOSE = "Close";
	public final static String OPTION_SAVE_AS_NEW = "Save As New...";


/**
 * UserMessage constructor comment.
 */
public SimpleUserMessage(String argMessage, String[] argOptions, String argDefaultSelection) {
	super();
	this.message = argMessage;
	this.options = argOptions;
	this.defaultSelection = argDefaultSelection;
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/2004 2:42:37 PM)
 * @return java.lang.String
 */
public java.lang.String getDefaultSelection() {
	return defaultSelection;
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/2004 10:17:05 AM)
 * @return java.lang.String
 */
public java.lang.String getMessage(String replacementText) {
	if (replacementText!=null){
		if (message.indexOf(TEXT_REPLACE)==-1){
			throw new RuntimeException("not expecting replacement text");
		}else{
			return org.vcell.util.TokenMangler.replaceSubString(message,TEXT_REPLACE,replacementText);
		}
	}else if (message.indexOf(TEXT_REPLACE) > -1){
		throw new RuntimeException("expecting non-null replacement text");
	}
	return message;
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/2004 10:17:05 AM)
 * @return java.lang.String[]
 */
public java.lang.String[] getOptions() {
	return options;
}
}
