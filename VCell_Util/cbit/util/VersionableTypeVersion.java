package cbit.util;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/

/**
 * This type was created in VisualAge.
 */
public class VersionableTypeVersion implements java.io.Serializable {
	private VersionableType vType = null;
	private Version version = null;
/**
 * VersionRef constructor comment.
 */
public VersionableTypeVersion(VersionableType vt,Version v) {
	super();
	vType = vt;
	version = v;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if(this == obj){
		return true;
	}
	if (obj instanceof VersionableTypeVersion){
		VersionableTypeVersion vtv = (VersionableTypeVersion)obj;
		if (vtv.getVersion().getVersionKey().equals(getVersion().getVersionKey())){
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Field
 */
public Version getVersion() {
	return version;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.VersionableType
 */
public VersionableType getVType() {
	return vType;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int hashCode() {
	return getVersion().getVersionKey().hashCode();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return getVType().getTypeName()+":"+getVersion().getName()+":"+getVersion().getVersionKey();
}
}
