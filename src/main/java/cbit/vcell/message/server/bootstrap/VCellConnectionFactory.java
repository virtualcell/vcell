/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;

import org.vcell.util.AuthenticationException;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellConnection;

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
