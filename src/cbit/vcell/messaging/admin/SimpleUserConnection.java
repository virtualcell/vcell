package cbit.vcell.messaging.admin;
import java.util.Date;
import cbit.vcell.server.User;
/**
 * Insert the type's description here.
 * Creation date: (4/5/2006 9:39:11 AM)
 * @author: Fei Gao
 */
public class SimpleUserConnection implements ComparableObject {
	private User user = null;
	private Date connectedTime = null;
	private int elapsedTime = 0;

/**
 * SimpleUserConnection constructor comment.
 */
public SimpleUserConnection(User arg_user, Date arg_connectedTime) {
	super();
	user = arg_user;
	connectedTime = arg_connectedTime;
	elapsedTime = (int)(System.currentTimeMillis() - connectedTime.getTime());
}


/**
 * toObjects method comment.
 */
public java.lang.Object[] toObjects() {
	return new Object[] {user.getName(), connectedTime, new Integer(elapsedTime)};
}
}