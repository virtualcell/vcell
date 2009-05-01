package cbit.vcell.transaction;
import oracle.jdbc.driver.OracleDriver;
import javax.transaction.xa.XAException;
import javax.transaction.*;
import java.sql.SQLException;
import oracle.jdbc.xa.client.OracleXADataSource;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import org.vcell.util.PropertyLoader;

import java.sql.DriverManager;

/**
 * Insert the type's description here.
 * Creation date: (7/21/2003 1:54:33 PM)
 * @author: Fei Gao
 */
public class JtaXAOracleConnection implements JtaDbConnection {
	private OracleXADataSource oraXADataSource = null;
	private XAConnection  xaConnection = null;
	private java.sql.Connection connection = null;

/**
 * OracleJtaDataSource constructor comment.
 * @param ur java.lang.String
 * @param uname java.lang.String
 * @param pw java.lang.String
 */
public JtaXAOracleConnection() throws SQLException {
	oraXADataSource = new OracleXADataSource();
	oraXADataSource.setURL(PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL));
	oraXADataSource.setUser(PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid));
	oraXADataSource.setPassword(PropertyLoader.getRequiredProperty(PropertyLoader.dbPassword));
	xaConnection = oraXADataSource.getXAConnection();
}


/**
 * close method comment.
 */
public void close() throws SQLException {
	if (connection != null) {
		connection.close();
		connection = null;
	}
}


/**
 * closeOnFailure method comment.
 */
public void closeOnFailure() throws java.sql.SQLException {
	close(); // close the connection handle
	if (xaConnection != null) {
		xaConnection.close(); // close the physical connection
		xaConnection = null;
	}
}


/**
 * This method was created in VisualAge.
 * @return java.sql.Connection
 * @exception java.sql.SQLException The exception description.
 * @exception java.lang.ClassNotFoundException The exception description.
 */
public java.sql.Connection getConnection() throws SQLException {
	if (xaConnection == null) {
		xaConnection = oraXADataSource.getXAConnection(); // get another physical connection
	}
	// From http://download-west.oracle.com/docs/cd/B14117_01/java.101/b10979/xadistra.htm#sthref552
	// Each time an XA connection instance getConnection() method is called, it returns a new connection instance that 
	// exhibits the default behavior, and closes any previous connection instance that still exists and had been returned 
	// by the same XA connection instance. It is advisable to explicitly close any previous connection 
	// instance before opening a new one, however.
	
	close();
	connection = xaConnection.getConnection();	
 	return connection;
}


/**
 * joinTransaction method comment.
 */
public boolean joinTransaction(TransactionManager tm) throws java.sql.SQLException {
	try {
		XAResource xaRes = xaConnection.getXAResource();
		if (xaRes == null || !tm.getTransaction().enlistResource(xaRes)) {
			return false;
		}

		return true;
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
		throw new SQLException(ex.getMessage());
	}

}
}