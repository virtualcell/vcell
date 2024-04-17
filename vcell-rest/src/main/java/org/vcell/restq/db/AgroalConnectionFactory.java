package org.vcell.restq.db;

import io.agroal.api.AgroalDataSource;
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
    AgroalDataSource ds;

    @ConfigProperty(name = "vcell.quarkus.db-kind")
    String db_kind;


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
        Connection conn = ds.getConnection();
        conn.setAutoCommit(false);
        return conn;
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
        return switch (db_kind) {
            case "postgresql" -> true;
            case "oracle" -> false;
            default -> throw new IllegalStateException("Unexpected value: " + db_kind);
        };
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
