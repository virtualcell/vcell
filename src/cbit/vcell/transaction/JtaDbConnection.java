package cbit.vcell.transaction;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 8:35:21 AM)
 * @author: Fei Gao
 */
public interface JtaDbConnection {
	public void close() throws java.sql.SQLException;
	public void closeOnFailure() throws java.sql.SQLException;
	public java.sql.Connection getConnection() throws java.sql.SQLException;
	public boolean joinTransaction(javax.transaction.TransactionManager tm) throws java.sql.SQLException;
}
