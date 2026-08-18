package cbit.vcell.modeldb;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.db.DatabaseSyntax;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The cleanup sweep collects rows that no document references. A row a save is still writing
 * looks exactly like that for a few hundred milliseconds, because ServerDocumentManager
 * commits each child in its own transaction and writes the link row in a later one -- so every
 * one of these deletes needs the age guard, or it breaks the save (issues #1992, #1961).
 *
 * These assertions hold the invariant "no unreferenced-row delete goes out unguarded" for both
 * dialects without needing a database. That the guarded statements are actually *accepted* is
 * checked against a real database elsewhere: PostgreSQL by
 * org.vcell.restq.DatabaseCleanupSqlTest (runs in CI against the testcontainers schema), and
 * Oracle by hand -- `SELECT (SYSDATE - INTERVAL '1' HOUR) FROM DUAL` plus the full sweep on a
 * gvenzl/oracle-free container.
 */
@Tag("Fast")
public class DatabaseCleanupSqlDialectTest {

	@Test
	public void everyDeleteIsAgeGuardedOnOracle() throws Exception {
		assertGuarded(DatabaseSyntax.ORACLE, "SYSDATE - INTERVAL '1' HOUR");
	}

	@Test
	public void everyDeleteIsAgeGuardedOnPostgres() throws Exception {
		assertGuarded(DatabaseSyntax.POSTGRES, "CURRENT_TIMESTAMP - INTERVAL '1' HOUR");
	}

	private void assertGuarded(DatabaseSyntax syntax, String expectedGuard) throws Exception {
		List<String> statements = capture(syntax);

		// vc_simulation, vc_math, vc_geometry, vc_simcontext, vc_model, vc_softwareversion
		List<String> deletes = statements.stream().filter(s -> s.startsWith("DELETE FROM")).toList();
		assertEquals(6, deletes.size(), "unexpected number of deletes in the sweep: " + deletes);

		for (String delete : deletes) {
			if (delete.startsWith("DELETE FROM " + SoftwareVersionTable.table.getTableName())) {
				// vc_softwareversion rows are written in the same transaction as the versionable
				// they describe (insertVersionableInit), so there is no window to guard -- and the
				// table has no versionDate to guard on.
				continue;
			}
			assertTrue(delete.contains(expectedGuard),
					"delete is not age-guarded for " + syntax + ": " + delete);
		}

		// the report query and the vc_simdelfromdisk hand-off have to select exactly what the
		// delete removes, or disk cleanup chases simulations that are still in the database.
		for (String statement : statements) {
			if (statement.startsWith("SELECT") || statement.startsWith("insert into")) {
				assertTrue(statement.contains(expectedGuard),
						"report query does not match its guarded delete: " + statement);
			}
		}

		assertFalse(statements.isEmpty());
	}

	/** Runs the sweep against a Connection that records statements instead of executing them. */
	private List<String> capture(DatabaseSyntax syntax) throws Exception {
		List<String> statements = new ArrayList<>();
		Connection con = (Connection) stub(Connection.class, statements);
		DBBackupAndClean.cleanupDatabase(con, new StringBuffer(), syntax);
		return statements;
	}

	private Object stub(Class<?> iface, List<String> statements) {
		return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{iface}, (proxy, method, args) -> {
			switch (method.getName()) {
				case "createStatement":
					return stub(Statement.class, statements);
				case "getMetaData":
					return stub(ResultSetMetaData.class, statements);
				case "executeQuery":
					statements.add((String) args[0]);
					return stub(ResultSet.class, statements);
				case "executeUpdate":
					statements.add((String) args[0]);
					return 0;
				default:
					Class<?> returnType = method.getReturnType();
					if (returnType == boolean.class) return false;
					if (returnType == int.class) return 0;
					if (returnType == long.class) return 0L;
					return null;
			}
		});
	}
}
