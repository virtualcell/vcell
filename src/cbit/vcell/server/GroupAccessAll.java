package cbit.vcell.server;

import org.vcell.util.document.User;

/**
 * Insert the type's description here.
 * Creation date: (11/15/2001 3:34:49 PM)
 * @author: Frank Morgan
 */
public class GroupAccessAll extends GroupAccess {
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 3:35:22 PM)
 */
public GroupAccessAll() {
    super(GROUPACCESS_ALL);
}
/**
 * Insert the method's description here.
 * Creation date: (12/8/2001 9:50:15 PM)
 * @return java.lang.String
 */
public String getDescription() {
	return "Public";
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isMember(User user) {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (12/8/2001 9:50:15 PM)
 * @return java.lang.String
 */
public String toString() {
	return "Public";
}
}
