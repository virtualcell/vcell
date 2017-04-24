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

import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellServer;

/**
 * This type was created in VisualAge.
 */
public interface VCellServerFactory {
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellConnection
 */
VCellServer getVCellServer() throws ConnectionException, AuthenticationException;
}
