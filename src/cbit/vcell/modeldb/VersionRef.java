/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import org.vcell.util.document.VersionableType;

import cbit.sql.Field;
/**
 * This type was created in VisualAge.
 */
public class VersionRef {
	private VersionableType vType = null;
	private Field refField = null;
	private Field linkField = null;
/**
 * VersionRef constructor comment.
 */
public VersionRef(VersionableType argVType,Field argRefField) {
	super();
	vType = argVType;
	refField = argRefField;
	linkField = null;
}
/**
 * VersionRef constructor comment.
 */
public VersionRef(VersionableType argVType,Field argRefField, Field argLinkField) {
	super();
	vType = argVType;
	refField = argRefField;
	linkField = argLinkField;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Field
 */
public Field getLinkField() {
	return linkField;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Field
 */
public Field getRefField() {
	return refField;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.VersionableType
 */
public VersionableType getVType() {
	return vType;
}
}
