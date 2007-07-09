package cbit.vcell.bionetgen.server;

import java.rmi.RemoteException;

import org.vcell.util.DataAccessException;


/**
 * Insert the type's description here.
 * Creation date: (6/23/2005 1:48:27 PM)
 * @author: Anuradha Lakshminarayana
 */
public interface BNGService extends java.rmi.Remote {
/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 3:42:56 PM)
 */
public BNGOutput executeBNG(BNGInput bngRulesInput) throws DataAccessException, RemoteException;
}
