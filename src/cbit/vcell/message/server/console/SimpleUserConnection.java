/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.console;
import java.util.Date;

import org.vcell.util.ComparableObject;
import org.vcell.util.document.User;
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
