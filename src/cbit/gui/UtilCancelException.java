package cbit.gui;
/**
 * Insert the type's description here.
 * Creation date: (6/1/2004 12:05:40 AM)
 * @author: Ion Moraru
 */
public class UtilCancelException extends Exception {
	//public static final UtilCancelException CANCEL_NEW_NAME = new UtilCancelException("User canceled when prompted for a new name");
	//public static final UtilCancelException WARN_UNABLE_CHECK = new UtilCancelException("User canceled on warning: unable to check for changes");
	//public static final UtilCancelException WARN_NO_CHANGES = new UtilCancelException("User canceled on warning: no changes");
	//public static final UtilCancelException CHOOSE_SAVE_AS = new UtilCancelException("User chose to save as on warning: no changes");
	//public static final UtilCancelException CANCEL_DELETE_OLD = new UtilCancelException("User chose to cancel deletion of old version");
	//public static final UtilCancelException CANCEL_FILE_SELECTION = new UtilCancelException("User canceled selecting a file");
	//public static final UtilCancelException CANCEL_DB_SELECTION = new UtilCancelException("User canceled selecting from database");
	//public static final UtilCancelException CANCEL_EDIT_IMG_ATTR = new UtilCancelException("User canceled Edit Image Attributes");
	public static final UtilCancelException CANCEL_GENERIC = new UtilCancelException("User canceled (Generic)");
	//public static final UtilCancelException CANCEL_XML_TRANSLATION = new UtilCancelException("User canceled XML translation process.");

/**
 * UserCancelException constructor comment.
 * @param s java.lang.String
 */
private UtilCancelException(String s) {
	super(s);
}
}