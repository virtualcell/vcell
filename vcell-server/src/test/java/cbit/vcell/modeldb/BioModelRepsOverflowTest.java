package cbit.vcell.modeldb;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.vcell.db.DatabaseSyntax;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * issue #2036: one biomodel with enough simulations used to fail the ENTIRE biomodel listing.
 *
 * The listing aggregated each row's child keys with three correlated LISTAGG subqueries. Oracle
 * LISTAGG returns VARCHAR2, capped at 4000 bytes in SQL, so a user with one large model got
 * ORA-01489 and could not list ANY of their models - which is what happened in production to
 * owner mblinov.
 *
 * This has to run against a real Oracle: the cap is Oracle's, and PostgreSQL's string_agg
 * returns text with no such limit, so the bug is invisible on the dev/test database. Both
 * dialects are exercised here because the same Java path serves both and the fix must not
 * change what either returns.
 *
 * Tagged Oracle_IT (regression group, not the fast lane) because it needs the Oracle image -
 * same reasoning as DatabaseCleanupOracleSqlTest.
 */
@Tag("Oracle_IT")
public class BioModelRepsOverflowTest {

	private static final String ORACLE_IMAGE = "gvenzl/oracle-free:23-slim-faststart";
	private static final String POSTGRES_IMAGE = "postgres:15";
	private static final String PASSWORD = "vcelltest";

	/** comfortably past the 4000-byte LISTAGG cap: ~700 nine-digit keys plus separators */
	private static final int BIG_MODEL_SIM_COUNT = 700;
	private static final long BIG_BM_ID = 100_000_001L;
	private static final long SMALL_BM_ID = 100_000_002L;
	private static final long OWNER_ID = 555_000_001L;
	private static final long SIM_KEY_BASE = 900_000_000L;

	private static GenericContainer<?> oracle;
	private static PostgreSQLContainer<?> postgres;
	private static String oracleUrl;

	@BeforeAll
	public static void startDatabases() throws Exception {
		Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(),
				"needs Docker for the Oracle and PostgreSQL containers");

		oracle = new GenericContainer<>(ORACLE_IMAGE)
				.withExposedPorts(1521)
				.withEnv("ORACLE_PASSWORD", PASSWORD)
				.waitingFor(Wait.forLogMessage(".*DATABASE IS READY TO USE!.*\\n", 1)
						.withStartupTimeout(Duration.ofMinutes(10)));
		oracle.start();
		oracleUrl = "jdbc:oracle:thin:@//" + oracle.getHost() + ":" + oracle.getMappedPort(1521) + "/FREEPDB1";
		try (Connection con = oracleConnection()) {
			seed(con, DatabaseSyntax.ORACLE);
		}

		postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
				.withUsername("vcell").withPassword(PASSWORD).withDatabaseName("vcelltest");
		postgres.start();
		try (Connection con = postgresConnection()) {
			seed(con, DatabaseSyntax.POSTGRES);
		}
	}

	@AfterAll
	public static void stopDatabases() {
		if (oracle != null) oracle.stop();
		if (postgres != null) postgres.stop();
	}

	private static Connection oracleConnection() throws Exception {
		Connection con = DriverManager.getConnection(oracleUrl, "system", PASSWORD);
		con.setAutoCommit(false);
		return con;
	}

	private static Connection postgresConnection() throws Exception {
		Connection con = DriverManager.getConnection(postgres.getJdbcUrl(), "vcell", PASSWORD);
		con.setAutoCommit(false);
		return con;
	}

	/**
	 * Only the columns the listing query touches. What is under test is the query and the Java
	 * that stitches child keys onto its rows, not the real schema.
	 */
	private static void seed(Connection con, DatabaseSyntax dbSyntax) throws SQLException {
		boolean oracleSyntax = dbSyntax == DatabaseSyntax.ORACLE;
		String num = oracleSyntax ? "NUMBER" : "NUMERIC";
		String str = oracleSyntax ? "VARCHAR2(255)" : "VARCHAR(255)";
		String ts = oracleSyntax ? "DATE" : "TIMESTAMP";
		String now = oracleSyntax ? "SYSDATE" : "CURRENT_TIMESTAMP";

		try (Statement stmt = con.createStatement()) {
			stmt.executeUpdate("CREATE TABLE vc_userinfo (id "+num+" PRIMARY KEY, userid "+str+")");
			stmt.executeUpdate("CREATE TABLE vc_group (groupid "+num+", userRef "+num+")");
			stmt.executeUpdate("CREATE TABLE vc_biomodel (id "+num+" PRIMARY KEY, name "+str+", privacy "+num+
					", versionFlag "+num+", versionDate "+ts+", versionAnnot "+str+
					", versionBranchID "+num+", modelRef "+num+", ownerRef "+num+")");
			stmt.executeUpdate("CREATE TABLE vc_biomodelsim (biomodelRef "+num+", simRef "+num+")");
			stmt.executeUpdate("CREATE TABLE vc_biomodelsimcontext (biomodelRef "+num+", simContextRef "+num+")");
			stmt.executeUpdate("CREATE TABLE vc_softwareversion (versionableRef "+num+", softwareVersion "+str+")");

			stmt.executeUpdate("INSERT INTO vc_userinfo (id, userid) VALUES ("+OWNER_ID+", 'mblinov_test')");
			// privacy 0 == public; group membership is only consulted for privacy > 1
			stmt.executeUpdate("INSERT INTO vc_group (groupid, userRef) VALUES (0, "+OWNER_ID+")");

			for (long bmId : new long[]{BIG_BM_ID, SMALL_BM_ID}) {
				stmt.executeUpdate("INSERT INTO vc_biomodel " +
						"(id, name, privacy, versionFlag, versionDate, versionAnnot, versionBranchID, modelRef, ownerRef) " +
						"VALUES ("+bmId+", 'model_"+bmId+"', 0, 0, "+now+", 'annot', 1, 1, "+OWNER_ID+")");
			}
			// the model that overflowed LISTAGG, and a small one alongside it to prove the
			// listing still returns the others rather than failing wholesale
			for (int i = 0; i < BIG_MODEL_SIM_COUNT; i++) {
				stmt.executeUpdate("INSERT INTO vc_biomodelsim (biomodelRef, simRef) VALUES ("+BIG_BM_ID+", "+(SIM_KEY_BASE+i)+")");
				stmt.executeUpdate("INSERT INTO vc_biomodelsimcontext (biomodelRef, simContextRef) VALUES ("+BIG_BM_ID+", "+(SIM_KEY_BASE+i)+")");
			}
			stmt.executeUpdate("INSERT INTO vc_biomodelsim (biomodelRef, simRef) VALUES ("+SMALL_BM_ID+", 1)");
			stmt.executeUpdate("INSERT INTO vc_biomodelsim (biomodelRef, simRef) VALUES ("+SMALL_BM_ID+", 2)");
		}
		con.commit();
	}

	/** The production path: build the statement, read the rows, stitch the child keys on. */
	private static List<BioModelRep> listReps(Connection con, DatabaseSyntax dbSyntax) throws SQLException {
		User user = new User("mblinov_test", new KeyValue(Long.toString(OWNER_ID)));
		String sql = BioModelTable.table.getPreparedStatement_BioModelReps(null, null, 1, 1000, dbSyntax);
		List<BioModelTable.BioModelRepRow> rows = new ArrayList<>();
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			BioModelTable.table.setPreparedStatement_BioModelReps(stmt, user, 1, 1000, dbSyntax);
			try (ResultSet rset = stmt.executeQuery()) {
				while (rset.next()) {
					rows.add(BioModelTable.table.getBioModelRepRow(rset, dbSyntax));
				}
			}
		}
		return Arrays.asList(BioModelTable.attachChildKeys(con, dbSyntax, rows));
	}

	private static BioModelRep byKey(List<BioModelRep> reps, long id) {
		return reps.stream().filter(r -> r.getBmKey().toString().equals(Long.toString(id))).findFirst().orElse(null);
	}

	private static void assertListingIsComplete(Connection con, DatabaseSyntax dbSyntax) throws SQLException {
		List<BioModelRep> reps = listReps(con, dbSyntax);
		assertEquals(2, reps.size(), "both models should be listed");

		BioModelRep big = byKey(reps, BIG_BM_ID);
		assertNotNull(big, "the large model should be listed");
		assertEquals(BIG_MODEL_SIM_COUNT, big.getSimKeyList().length,
				"every simulation key should come back, with no 4000-byte ceiling");
		assertEquals(BIG_MODEL_SIM_COUNT, big.getSimContextKeyList().length);

		Set<String> expected = new HashSet<>();
		for (int i = 0; i < BIG_MODEL_SIM_COUNT; i++) expected.add(Long.toString(SIM_KEY_BASE + i));
		Set<String> actual = new HashSet<>();
		for (KeyValue k : big.getSimKeyList()) actual.add(k.toString());
		assertEquals(expected, actual, "the keys themselves should be right, not merely the count");

		BioModelRep small = byKey(reps, SMALL_BM_ID);
		assertNotNull(small, "the small model should be listed alongside the large one");
		assertEquals(2, small.getSimKeyList().length);
	}

	@Test
	public void listingSurvivesAModelWithManySimulations_oracle() throws Exception {
		try (Connection con = oracleConnection()) {
			assertListingIsComplete(con, DatabaseSyntax.ORACLE);
		}
	}

	@Test
	public void listingSurvivesAModelWithManySimulations_postgres() throws Exception {
		try (Connection con = postgresConnection()) {
			assertListingIsComplete(con, DatabaseSyntax.POSTGRES);
		}
	}

	/**
	 * The bug itself, so this test is known to be capable of catching it.
	 *
	 * Runs the aggregate the listing used to use. If Oracle ever stopped raising ORA-01489 here,
	 * the test above would still pass but would no longer be testing anything.
	 */
	@Test
	public void theOldListaggStillOverflows_oracle() throws Exception {
		String listagg =
				"select (select '['||listagg(SQ1_vc_biomodelsim.simRef, ',')||']' " +
				"          from vc_biomodelsim SQ1_vc_biomodelsim " +
				"         where SQ1_vc_biomodelsim.biomodelRef = vc_biomodel.id) simKeys " +
				"  from vc_biomodel where vc_biomodel.id = " + BIG_BM_ID;
		try (Connection con = oracleConnection(); Statement stmt = con.createStatement()) {
			SQLException e = assertThrows(SQLException.class, () -> {
				try (ResultSet rset = stmt.executeQuery(listagg)) {
					rset.next();
					rset.getString("simKeys");
				}
			}, "this many simulations should still overflow Oracle's LISTAGG");
			assertTrue(e.getMessage().contains("ORA-01489"),
					"expected ORA-01489, got: " + e.getMessage());
		}
	}
}
