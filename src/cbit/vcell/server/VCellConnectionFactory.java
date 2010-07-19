package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public interface VCellConnectionFactory {
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 12:02:55 PM)
 * @param userID java.lang.String
 * @param password java.lang.String
 */
void changeUser(UserLoginInfo userLoginInfo);
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellConnection
 */
VCellConnection createVCellConnection() throws ConnectionException, AuthenticationException;
}
