package org.vcell.rest.db;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.db.oracle.OracleKeyFactory;

import java.sql.Connection;
import java.sql.SQLException;

@ApplicationScoped
public class OracleAgroalConnectionFactory implements ConnectionFactory {

    @Inject
    private AgroalDataSource ds;

    private OracleKeyFactory keyFactory = new OracleKeyFactory();

    @Override
    public void close() {
    }

    @Override
    public void failed(Connection con, Object lock) {
    }

    @Override
    public Connection getConnection(Object lock) throws SQLException {
        return ds.getConnection();
    }

    @Override
    public void release(Connection con, Object lock) throws SQLException {
        con.close();
    }

    @Override
    public KeyFactory getKeyFactory() {
        return keyFactory;
    }

    @Override
    public DatabaseSyntax getDatabaseSyntax() {
        return DatabaseSyntax.ORACLE;
    }
}
