package org.vcell.restq;

import cbit.vcell.modeldb.DBBackupAndClean;
import cbit.vcell.resource.PropertyLoader;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises DBBackupAndClean.cleanupDatabase() -- the sweep the db service runs every 15
 * minutes -- against a real database with the real VCell schema.
 *
 * This is the PostgreSQL half of the dialect coverage; the ORACLE branch of the generated
 * SQL is covered by DatabaseCleanupOracleSyntaxTest in vcell-server.
 */
@QuarkusTest
public class DatabaseCleanupSqlTest {

	@Inject
	AgroalConnectionFactory connectionFactory;

	// well away from the key sequence any other test uses
	private static final long USER_KEY   = 990000001L;
	private static final long EXTENT_KEY = 990000002L;
	private static final long GEOM_KEY   = 990000003L;
	private static final long MODEL_KEY  = 990000004L;
	private static final long OLD_SC_KEY = 990000005L;
	private static final long NEW_SC_KEY = 990000006L;

	@BeforeAll
	public static void setupConfig(){
		PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
	}

	@AfterEach
	public void removeFixture() throws Exception {
		Object lock = new Object();
		Connection con = connectionFactory.getConnection(lock);
		try (Statement stmt = con.createStatement()) {
			stmt.executeUpdate("DELETE FROM vc_simcontext WHERE id IN ("+OLD_SC_KEY+","+NEW_SC_KEY+")");
			stmt.executeUpdate("DELETE FROM vc_model WHERE id = "+MODEL_KEY);
			stmt.executeUpdate("DELETE FROM vc_geometry WHERE id = "+GEOM_KEY);
			stmt.executeUpdate("DELETE FROM vc_geomextent WHERE id = "+EXTENT_KEY);
			stmt.executeUpdate("DELETE FROM vc_userinfo WHERE id = "+USER_KEY);
			con.commit();
		} finally {
			connectionFactory.release(con, lock);
		}
	}

	/**
	 * Every statement the sweep issues has to be accepted by the database it is pointed at.
	 * A dialect slip in DBBackupAndClean is invisible until the db service runs in production,
	 * where it aborts the whole sweep.
	 */
	@Test
	public void cleanupSweepIsAcceptedByTheDatabase() throws Exception {
		Object lock = new Object();
		Connection con = connectionFactory.getConnection(lock);
		try {
			StringBuffer log = new StringBuffer();
			DBBackupAndClean.cleanupDatabase(con, log, connectionFactory.getDatabaseSyntax());
			// cleanupDatabase() records each statement it ran; five deletes plus the
			// version-parent updates and report queries.
			assertTrue(log.indexOf("DELETE FROM vc_simcontext") >= 0, "sweep did not reach the simcontext delete");
		} finally {
			connectionFactory.release(con, lock);
		}
	}

	/**
	 * saveBioModel commits each child in its own transaction and writes the
	 * vc_biomodelsimcontext link row in a later one, so a simulation context is briefly
	 * committed and referenced by nothing. Collecting it there breaks the save with
	 * ORA-02291 (issues #1992, #1961) -- the age guard is what stops that.
	 */
	@Test
	public void ageGuardSparesRowsThatASaveIsStillWriting() throws Exception {
		Object lock = new Object();
		Connection con = connectionFactory.getConnection(lock);
		try {
			Instant now = Instant.now();
			insertFixture(con, Timestamp.from(now.minus(Duration.ofHours(2))), Timestamp.from(now));

			// neither simulation context is linked to a BioModel: by the sweep's own
			// definition both are unreferenced.
			assertTrue(exists(con, OLD_SC_KEY));
			assertTrue(exists(con, NEW_SC_KEY));

			StringBuffer log = new StringBuffer();
			DBBackupAndClean.cleanupDatabase(con, log, connectionFactory.getDatabaseSyntax());

			assertFalse(exists(con, OLD_SC_KEY), "an orphan two hours old should still be collected");
			assertTrue(exists(con, NEW_SC_KEY), "a row a save is still writing must not be collected");
		} finally {
			connectionFactory.release(con, lock);
		}
	}

	private boolean exists(Connection con, long simContextKey) throws Exception {
		try (PreparedStatement stmt = con.prepareStatement("SELECT id FROM vc_simcontext WHERE id = ?")) {
			stmt.setLong(1, simContextKey);
			try (ResultSet rset = stmt.executeQuery()) {
				return rset.next();
			}
		}
	}

	private void insertFixture(Connection con, Timestamp oldDate, Timestamp newDate) throws Exception {
		exec(con, "INSERT INTO vc_userinfo (id,USERID,PASSWORD,EMAIL,FIRSTNAME,NOTIFY,insertDate,DIGESTPW)"
				+ " VALUES (?,?,?,?,?,?,?,?)",
				USER_KEY, "cleanupSweepTestUser", "x", "x@example.org", "cleanup", "N", newDate, "x");
		exec(con, "INSERT INTO vc_geomextent (id,extentX,extentY,extentZ) VALUES (?,?,?,?)",
				EXTENT_KEY, 1, 1, 1);
		exec(con, "INSERT INTO vc_geometry (id,name,ownerRef,privacy,versionDate,versionFlag,versionBranchID,"
				+ "dimension,originX,originY,originZ,extentRef) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
				GEOM_KEY, "cleanupSweepTestGeometry", USER_KEY, 0, newDate, 0, 0, 0, 0, 0, 0, EXTENT_KEY);
		exec(con, "INSERT INTO vc_model (id,name,ownerRef,privacy,versionDate,versionFlag,versionBranchID)"
				+ " VALUES (?,?,?,?,?,?,?)",
				MODEL_KEY, "cleanupSweepTestModel", USER_KEY, 0, newDate, 0, 0);
		insertSimContext(con, OLD_SC_KEY, "cleanupSweepTestOldOrphan", oldDate);
		insertSimContext(con, NEW_SC_KEY, "cleanupSweepTestSaveInFlight", newDate);
		con.commit();
	}

	private void insertSimContext(Connection con, long key, String name, Timestamp versionDate) throws Exception {
		exec(con, "INSERT INTO vc_simcontext (id,name,ownerRef,privacy,versionDate,versionFlag,versionBranchID,"
				+ "modelRef,geometryRef) VALUES (?,?,?,?,?,?,?,?,?)",
				key, name, USER_KEY, 0, versionDate, 0, 0, MODEL_KEY, GEOM_KEY);
	}

	private void exec(Connection con, String sql, Object... values) throws Exception {
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			for (int i = 0; i < values.length; i++) {
				stmt.setObject(i+1, values[i]);
			}
			stmt.executeUpdate();
		}
	}
}
