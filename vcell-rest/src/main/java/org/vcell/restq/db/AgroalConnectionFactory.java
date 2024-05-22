package org.vcell.restq.db;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.db.oracle.OracleKeyFactory;
import org.vcell.db.postgres.PostgresKeyFactory;

import java.sql.Connection;
import java.sql.SQLException;

@ApplicationScoped
public class AgroalConnectionFactory implements ConnectionFactory {


    @Inject
    @DataSource("oracle")
    AgroalDataSource oracle_ds;

    @Inject
    @DataSource("postgresql")
    AgroalDataSource postgresql_ds;

    @ConfigProperty(name = "quarkus.profile")
    String activeProfile;

    @ConfigProperty(name = "vcell.prod.database.postgresql")
    boolean usePostgresqlInProd;


    public AgroalConnectionFactory() {
    }

    @Override
    public void close() {
    }

    @Override
    public void failed(Connection con, Object lock) {
    }

    @Override
    public Connection getConnection(Object lock) throws SQLException {
        if (usePostgresql()) {
            Connection conn = postgresql_ds.getConnection();
            conn.setAutoCommit(false);
            return conn;
        } else {
            Connection conn = oracle_ds.getConnection();
            conn.setAutoCommit(false);
            return conn;
        }
    }

    @Override
    public void release(Connection con, Object lock) throws SQLException {
        con.close();
    }

    @Override
    public KeyFactory getKeyFactory() {
        if (usePostgresql()) {
            return new PostgresKeyFactory();
        }else{
            return new OracleKeyFactory();
        }
    }

    private boolean usePostgresql() {
        switch (activeProfile) {
            case "test", "dev": {
                return true;
            }
            case "prod": {
                return false;
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + activeProfile);
            }
        }
    }

    @Override
    public DatabaseSyntax getDatabaseSyntax() {
        if (usePostgresql()) {
            return DatabaseSyntax.POSTGRES;
        } else {
            return DatabaseSyntax.ORACLE;
        }
    }
}
