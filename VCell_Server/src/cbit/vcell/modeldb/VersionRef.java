package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.Field;
import cbit.sql.VersionableType;
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
