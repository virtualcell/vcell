package org.vcell.util.gui;
/**
 * Insert the type's description here.
 * Creation date: (6/1/2004 12:05:40 AM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class UtilCancelException extends Exception {
	public static final UtilCancelException CANCEL_GENERIC = new UtilCancelException("User canceled (Generic)");

/**
 * UserCancelException constructor comment.
 * @param s java.lang.String
 */
private UtilCancelException(String s) {
	super(s);
}
}