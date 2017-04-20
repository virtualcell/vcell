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
/**
 * Insert the type's description here.
 * Creation date: (5/23/2006 9:32:49 AM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class CurateSpec implements java.io.Serializable{

	public static final int ARCHIVE = 0;
	public static final int PUBLISH = 1;

	public static final String[] CURATE_TYPE_NAMES = {"ARCHIVE","PUBLISH"};
	public static final String[] CURATE_TYPE_ACTIONS = {"ARCHIVING","PUBLISHING"};
	public static final String[] CURATE_TYPE_STATES = {"ARCHIVED","PUBLISHED"};
	
	private int curateType;
	private org.vcell.util.document.VCDocumentInfo vcDocumentInfo;

/**
 * CurateSpec constructor comment.
 */
public CurateSpec(org.vcell.util.document.BioModelInfo bioModelInfo,int argCurateType) {
	this((org.vcell.util.document.VCDocumentInfo)bioModelInfo,argCurateType);
	
}


/**
 * CurateSpec constructor comment.
 */
private CurateSpec(org.vcell.util.document.VCDocumentInfo argVCDocumentInfo,int argCurateType) {
	curateType = argCurateType;
	vcDocumentInfo = argVCDocumentInfo;
	
}


/**
 * CurateSpec constructor comment.
 */
public CurateSpec(org.vcell.util.document.MathModelInfo mathModelInfo,int argCurateType) {
	this((org.vcell.util.document.VCDocumentInfo)mathModelInfo,argCurateType);
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 9:37:10 AM)
 * @return int
 */
public int getCurateType() {
	return curateType;
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 10:19:10 AM)
 * @return cbit.vcell.document.VCDocumentInfo
 */
public org.vcell.util.document.VCDocumentInfo getVCDocumentInfo() {
	return vcDocumentInfo;
}
}
