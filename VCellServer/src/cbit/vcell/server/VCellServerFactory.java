package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
