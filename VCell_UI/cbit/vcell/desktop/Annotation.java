package cbit.vcell.desktop;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (1/15/01 4:07:26 PM)
 * @author: Jim Schaff
 */
public class Annotation {
	private String str = null;
/**
 * Annotation constructor comment.
 */
public Annotation(String argString) {
	if (argString==null){
		str = "";
	}else{
		str = argString;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/01 4:08:55 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof Annotation){
		return str.equals(((Annotation)obj).str);
	}
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/01 4:09:17 PM)
 * @return int
 */
public int hashCode() {
	return str.hashCode();
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/01 4:08:25 PM)
 * @return java.lang.String
 */
public String toString() {
	return str;
}
}
