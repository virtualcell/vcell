package org.vcell.util.document;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/13/00 4:54:24 PM)
 * @author: Jim Schaff
 */
public class BioModelInfo implements org.vcell.util.document.VCDocumentInfo {
	private Version version = null;
	private KeyValue modelKey = null;
	private BioModelChildSummary bioModelChildSummary = null;
/**
 * BioModelInfo constructor comment.
 */
public BioModelInfo(Version argVersion, KeyValue argModelKey, BioModelChildSummary argBioModelChildSummary) {
	this.version = argVersion;
	this.modelKey = argModelKey;
	this.bioModelChildSummary = argBioModelChildSummary;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof BioModelInfo){
		if (!getVersion().getVersionKey().equals(((BioModelInfo)object).getVersion().getVersionKey())){
			return false;
		}
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (8/21/2004 10:35:26 AM)
 * @return cbit.vcell.biomodel.BioModelChildSummary
 */
public BioModelChildSummary getBioModelChildSummary() {
	return bioModelChildSummary;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:57:53 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getModelKey() {
	return modelKey;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
public org.vcell.util.document.Version getVersion() {
	return version;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:28:06 PM)
 * @return int
 */
public int hashCode() {
	return getVersion().getVersionKey().hashCode();
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 7:44:59 PM)
 * @return java.lang.String
 */
public String toString() {
	return "BioModelInfo(modelKey="+modelKey+",Version="+version+")";
}
}
