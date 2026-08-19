package cbit.vcell.modeldb;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.vcell.db.DatabaseSyntax;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The Oracle half of the cleanup sweep's dialect coverage.
 *
 * The sweep only ever runs in the db service against production Oracle, and its SQL is
 * assembled by string concatenation, so a dialect slip is invisible until it aborts a whole
 * cleanup run in production. PostgreSQL is covered on every push by
 * org.vcell.restq.DatabaseCleanupSqlTest against the Quarkus testcontainers database; Oracle
 * needs its own image, which is why this is a regression group (merge queue + nightly) rather
 * than part of the fast lane.
 *
 * A stub schema rather than the real one: what is under test is whether Oracle accepts these
 * statements and honours the age guard, not the schema.
 */
@Tag("Oracle_IT")
public class DatabaseCleanupOracleSqlTest {

	private static final String IMAGE = "gvenzl/oracle-free:23-slim-faststart";
	private static final String PASSWORD = "vcelltest";

	private static GenericContainer<?> oracle;
	private static String jdbcUrl;

	private static final String[] STUB_SCHEMA = {
		"CREATE TABLE vc_userinfo (id NUMBER PRIMARY KEY, userid VARCHAR2(255))",
		"CREATE TABLE vc_simulation (id NUMBER PRIMARY KEY, name VARCHAR2(255), ownerRef NUMBER, versionDate DATE, versionPRef NUMBER, parentSimRef NUMBER, mathRef NUMBER)",
		"CREATE TABLE vc_biomodelsim (id NUMBER, simRef NUMBER)",
		"CREATE TABLE vc_mathmodelsim (id NUMBER, simRef NUMBER)",
		"CREATE TABLE vc_simdelfromdisk (deldate VARCHAR2(64), userid VARCHAR2(255), userkey NUMBER, simid NUMBER, simpref NUMBER, simdate VARCHAR2(64), simname VARCHAR2(255), status VARCHAR2(32), numfiles NUMBER)",
		"CREATE TABLE vc_math (id NUMBER PRIMARY KEY, name VARCHAR2(255), ownerRef NUMBER, versionDate DATE, versionPRef NUMBER, geometryRef NUMBER)",
		"CREATE TABLE vc_mathmodel (id NUMBER PRIMARY KEY, mathRef NUMBER)",
		"CREATE TABLE vc_geometry (id NUMBER PRIMARY KEY, name VARCHAR2(255), ownerRef NUMBER, versionDate DATE, dimension NUMBER)",
		"CREATE TABLE vc_simcontext (id NUMBER PRIMARY KEY, name VARCHAR2(255), ownerRef NUMBER, versionDate DATE, versionPRef NUMBER, mathRef NUMBER, modelRef NUMBER, geometryRef NUMBER)",
		"CREATE TABLE vc_biomodelsimcontext (id NUMBER, biomodelRef NUMBER, simContextRef NUMBER)",
		"CREATE TABLE vc_model (id NUMBER PRIMARY KEY, name VARCHAR2(255), ownerRef NUMBER, versionDate DATE)",
		"CREATE TABLE vc_biomodel (id NUMBER PRIMARY KEY, modelRef NUMBER)",
		"CREATE TABLE vc_image (id NUMBER PRIMARY KEY)",
		"CREATE TABLE vc_softwareversion (versionableRef NUMBER)",
	};

	@BeforeAll
	public static void startOracle() throws Exception {
		Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(),
				"needs Docker for the Oracle container");
		oracle = new GenericContainer<>(IMAGE)
				.withExposedPorts(1521)
				.withEnv("ORACLE_PASSWORD", PASSWORD)
				.waitingFor(Wait.forLogMessage(".*DATABASE IS READY TO USE!.*\\n", 1)
						.withStartupTimeout(Duration.ofMinutes(10)));
		oracle.start();
		jdbcUrl = "jdbc:oracle:thin:@//" + oracle.getHost() + ":" + oracle.getMappedPort(1521) + "/FREEPDB1";

		try (Connection con = connect(); Statement stmt = con.createStatement()) {
			for (String ddl : STUB_SCHEMA) {
				stmt.executeUpdate(ddl);
			}
			con.commit();
		}
	}

	@AfterAll
	public static void stopOracle() {
		if (oracle != null) {
			oracle.stop();
		}
	}

	@BeforeEach
	public void emptyTheTables() throws Exception {
		try (Connection con = connect(); Statement stmt = con.createStatement()) {
			stmt.executeUpdate("DELETE FROM vc_simcontext");
			con.commit();
		}
	}

	private static Connection connect() throws Exception {
		Connection con = DriverManager.getConnection(jdbcUrl, "system", PASSWORD);
		con.setAutoCommit(false);
		return con;
	}

	/** Every statement the sweep issues has to be one Oracle accepts. */
	@Test
	public void cleanupSweepIsAcceptedByOracle() throws Exception {
		try (Connection con = connect()) {
			StringBuffer log = new StringBuffer();
			DBBackupAndClean.cleanupDatabase(con, log, DatabaseSyntax.ORACLE);
			assertTrue(log.indexOf("DELETE FROM vc_simcontext") >= 0, "sweep did not reach the simcontext delete");
		}
	}

	/** The guard expression itself, isolated from the statements that use it. */
	@Test
	public void oracleEvaluatesTheAgeGuardExpression() throws Exception {
		try (Connection con = connect();
			 Statement stmt = con.createStatement();
			 ResultSet rset = stmt.executeQuery(
					 "SELECT (SYSDATE - (SYSDATE - INTERVAL '1' HOUR)) * 24 AS hours_back FROM DUAL")) {
			assertTrue(rset.next());
			assertEquals(1.0d, rset.getDouble("hours_back"), 1e-6);
		}
	}

	/**
	 * saveBioModel commits each child in its own transaction and writes the
	 * vc_biomodelsimcontext link row in a later one, so a simulation context is briefly
	 * committed and referenced by nothing. Collecting it there breaks the save with
	 * ORA-02291 (issues #1992, #1961).
	 */
	@Test
	public void ageGuardSparesRowsThatASaveIsStillWriting() throws Exception {
		try (Connection con = connect()) {
			try (Statement stmt = con.createStatement()) {
				stmt.executeUpdate("INSERT INTO vc_simcontext (id,name,ownerRef,versionDate)"
						+ " VALUES (1,'old orphan',10, SYSDATE - INTERVAL '2' HOUR)");
				stmt.executeUpdate("INSERT INTO vc_simcontext (id,name,ownerRef,versionDate)"
						+ " VALUES (2,'save in flight',10, SYSDATE)");
				con.commit();
			}

			// neither is linked to a BioModel: by the sweep's own definition both are unreferenced
			assertTrue(exists(con, 1));
			assertTrue(exists(con, 2));

			DBBackupAndClean.cleanupDatabase(con, new StringBuffer(), DatabaseSyntax.ORACLE);

			assertFalse(exists(con, 1), "an orphan two hours old should still be collected");
			assertTrue(exists(con, 2), "a row a save is still writing must not be collected");
		}
	}

	private boolean exists(Connection con, long simContextKey) throws Exception {
		try (Statement stmt = con.createStatement();
			 ResultSet rset = stmt.executeQuery("SELECT id FROM vc_simcontext WHERE id = " + simContextKey)) {
			return rset.next();
		}
	}
}
