package cbit.util;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (11/13/00 4:54:24 PM)
 * @author: Jim Schaff
 */
public class MathModelInfo implements cbit.util.VCDocumentInfo {
	private Version version = null;
	private KeyValue mathKey = null;
	private MathModelChildSummary mathModelChildSummary = null;
/**
 * BioModelInfo constructor comment.
 */
public MathModelInfo(Version argVersion, KeyValue argMathKey, MathModelChildSummary argMathModelChildSummary) {
	this.version = argVersion;
	this.mathKey = argMathKey;
	this.mathModelChildSummary = argMathModelChildSummary;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/01 12:24:41 PM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equals(Object object) {
	if (object instanceof MathModelInfo){
		if (!getVersion().getVersionKey().equals(((MathModelInfo)object).getVersion().getVersionKey())){
			return false;
		}
		return true;
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/00 4:57:53 PM)
 * @return cbit.sql.KeyValue
 */
public cbit.util.KeyValue getMathKey() {
	return mathKey;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 10:08:56 PM)
 * @return cbit.vcell.mathmodel.MathModelChildSummary
 */
public MathModelChildSummary getMathModelChildSummary() {
	return mathModelChildSummary;
}
/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
public cbit.util.Version getVersion() {
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
	return "MathModelInfo(mathKey="+mathKey+",Version="+version+")";
}
}
