package org.vcell.util;


/**
 * Insert the type's description here.
 * Creation date: (4/1/2004 10:12:47 AM)
 * @author: Jim Schaff
 */
public class Issue implements java.io.Serializable, Matchable {
	private java.util.Date date = new java.util.Date();
	private String message = null;
	private String category = null;
	private Object source = null;
	private int severity = -1;

	public static final int SEVERITY_INFO = 0;
	public static final int SEVERITY_TIP = 1;
	public static final int SEVERITY_BUILTIN_CONSTRAINT = 2;
	public static final int SEVERITY_WARNING = 3;
	public static final int SEVERITY_ERROR = 4;
	private static final int MAX_SEVERITY = 4;

	private final static String severityName[] = { "info", "tip", "constraint", "warning", "error" };

	//
	// categories
	//
	//public static final int CATEGORY_KineticsApplicability		= 0;
	//public static final int CATEGORY_ParameterLoop				= 1;
	//public static final int CATEGORY_InconsistentUnits			= 2;

/**
 * AbstractIssue constructor comment.
 */
public Issue(Object argSource, String argCategory, String argMessage, int argSeverity) {
	super();
	if (argSeverity<0 || argSeverity>MAX_SEVERITY){
		throw new IllegalArgumentException("unexpected severity="+argSeverity);
	}
	this.source = argSource;
	this.message = argMessage;
	this.category = argCategory;
	this.severity = argSeverity;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof Issue){
		Issue other = (Issue)obj;
		if (other.source!=this.source){
			return false;
		}
		if (!other.category.equals(category)){
			return false;
		}
		if (!other.message.equals(message)){
			return false;
		}
		if (other.severity != severity){
			return false;
		}
		return true;
	}else{
		return false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 11:11:46 AM)
 * @return boolean
 * @param object java.lang.Object
 */
public boolean equal(Object object) {
	if (this == object){
		return true;
	}
	if (object instanceof Issue){
		Issue otherIssue = (Issue)object;
		if (!source.equals(otherIssue.source)){
			return false;
		}
		if (!category.equals(otherIssue.category)){
			return false;
		}
		if (!message.equals(otherIssue.message)){
			return false;
		}
		if (severity != otherIssue.severity){
			return false;
		}
		//if (!date.equals(otherIssue.date)){
			//return false;
		//}
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 11:11:07 AM)
 * @return java.lang.String
 */
public java.lang.String getCategory() {
	return category;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return java.util.Date
 */
public java.util.Date getDate() {
	return date;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return java.lang.String
 */
public java.lang.String getMessage() {
	return message;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return int
 */
public int getSeverity() {
	return severity;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2004 8:37:29 AM)
 * @return java.lang.String
 */
public String getSeverityName() {
	return severityName[severity];
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 10:32:40 AM)
 * @return cbit.util.IssueSource
 */
public Object getSource() {
	return source;
}


/**
 * Insert the method's description here.
 * Creation date: (4/1/2004 11:17:07 AM)
 * @return int
 */
public int hashCode() {
	return source.hashCode()+category.hashCode()+message.hashCode()+severity;
}


/**
 * Insert the method's description here.
 * Creation date: (4/2/2004 8:33:57 AM)
 * @return java.lang.String
 */
public String toString() {
	//return getClass().getName() + "@" + Integer.toHexString(hashCode()) + ": "+getSeverityName()+", "+getCategory()+", '"+getMessage()+"', source="+getSource();
	return getSeverityName()+": '"+getMessage()+"'";
}
}