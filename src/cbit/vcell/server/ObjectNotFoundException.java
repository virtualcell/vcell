package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.server.DataAccessException;
/**
 * This type was created in VisualAge.
 */
public class ObjectNotFoundException extends DataAccessException {
/**
 * ObjectNotFoundException constructor comment.
 * @param s java.lang.String
 */
public ObjectNotFoundException(String s) {
	super(s);
}

public ObjectNotFoundException(String message,Throwable cause) {
	super(message,cause);
}

}
