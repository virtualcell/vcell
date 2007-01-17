package cbit.vcell.server;

/**
 * Insert the type's description here.
 * Creation date: (11/15/2001 3:34:49 PM)
 * @author: Frank Morgan
 */
public class GroupAccessNone extends GroupAccess {
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 3:35:22 PM)
 */
public GroupAccessNone() {
    super(GROUPACCESS_NONE);
}
/**
 * Insert the method's description here.
 * Creation date: (12/8/2001 9:51:13 PM)
 * @return java.lang.String
 */
public String getDescription() {
	return "Private";
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isMember(User user) {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (12/8/2001 9:51:13 PM)
 * @return java.lang.String
 */
public String toString() {
	return "Private";
}
}
