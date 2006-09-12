package cbit.vcell.transaction;
/**
 * Insert the type's description here.
 * Creation date: (7/29/2003 9:27:42 AM)
 * @author: Fei Gao
 */
public class JtaOracleConnection implements NonXAResource, JtaDbConnection {
	private Object lock = null;
	private java.sql.Connection oracleConnection = null;
	private cbit.sql.ConnectionFactory connFactory = null;

/**
 * JtaOracleConnectionFactory constructor comment.
 * @param dbUrl java.lang.String
 * @param username java.lang.String
 * @param password java.lang.String
 * @param ttm javax.transaction.TransactionManager
 * @exception javax.transaction.RollbackException The exception description.
 * @exception java.sql.SQLException The exception description.
 * @exception javax.transaction.xa.XAException The exception description.
 * @exception javax.transaction.SystemException The exception description.
 */
public JtaOracleConnection(cbit.sql.ConnectionFactory oracleFactory) throws java.sql.SQLException {
	lock = new Object();
	connFactory = oracleFactory;
	oracleConnection = oracleFactory.getConnection(lock);
}


/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 1:52:35 PM)
 */
public void close() throws java.sql.SQLException {
	connFactory.release(oracleConnection, lock);
}


/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 1:52:35 PM)
 */
public void closeOnFailure() throws java.sql.SQLException {
	connFactory.failed(oracleConnection, lock);
}


/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 1:52:35 PM)
 */
public void commit() throws javax.transaction.SystemException {
	try {
		oracleConnection.commit();
	} catch (java.sql.SQLException ex) {
		throw new javax.transaction.SystemException(ex.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 1:11:30 PM)
 * @return cbit.sql.ConnectionFactory
 */
public java.sql.Connection getConnection() {
	return oracleConnection;
}


/**
 * Insert the method's description here.
 * Creation date: (7/29/2003 1:52:35 PM)
 */
public boolean joinTransaction(javax.transaction.TransactionManager tm) throws java.sql.SQLException {
	try {
		((JtaTransactionImpl)tm.getTransaction()).enlistNonXAResource(this);
		return true;
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		throw new java.sql.SQLException(ex.getMessage());		
	}
}


/**
 * rollback method comment.
 */
public void rollback() throws javax.transaction.SystemException {
	try {
		oracleConnection.rollback();
	} catch (java.sql.SQLException ex) {
		throw new javax.transaction.SystemException(ex.getMessage());
	}	
}
}