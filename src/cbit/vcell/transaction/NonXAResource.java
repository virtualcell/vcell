package cbit.vcell.transaction;

/**
 * Insert the type's description here.
 * Creation date: (7/29/2003 1:06:24 PM)
 * @author: Fei Gao
 */
public interface NonXAResource {
	public void commit() throws javax.transaction.SystemException;
	public void rollback() throws javax.transaction.SystemException;
}
