package cbit.vcell.client.task;
/**
 * Insert the type's description here.
 * Creation date: (6/1/2004 12:05:40 AM)
 * @author: Ion Moraru
 */
public class UserCancelException extends RuntimeException {
	public static final UserCancelException CANCEL_NEW_NAME = new UserCancelException("User canceled when prompted for a new name");
	public static final UserCancelException WARN_UNABLE_CHECK = new UserCancelException("User canceled on warning: unable to check for changes");
	public static final UserCancelException WARN_NO_CHANGES = new UserCancelException("User canceled on warning: no changes");
	public static final UserCancelException CHOOSE_SAVE_AS = new UserCancelException("User chose to save as on warning: no changes");
	public static final UserCancelException CANCEL_DELETE_OLD = new UserCancelException("User chose to cancel deletion of old version");
	public static final UserCancelException CANCEL_FILE_SELECTION = new UserCancelException("User canceled selecting a file");
	public static final UserCancelException CANCEL_DB_SELECTION = new UserCancelException("User canceled selecting from database");
	public static final UserCancelException CANCEL_EDIT_IMG_ATTR = new UserCancelException("User canceled Edit Image Attributes");
	public static final UserCancelException CANCEL_GENERIC = new UserCancelException("User canceled (Generic)");
	public static final UserCancelException CANCEL_XML_TRANSLATION = new UserCancelException("User canceled XML translation process.");

/**
 * UserCancelException constructor comment.
 * @param s java.lang.String
 */
private UserCancelException(String s) {
	super(s);
}
}