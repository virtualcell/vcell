package cbit.vcell.modeldb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SQL here is assembled by string concatenation, so a secret passed as a value ends up in the
 * statement text — and this class logs whole statements. Measured on the alpha site: 40 live
 * API access tokens in seven days of logs, in plaintext, shipped to Loki and retained.
 *
 * The per-statement logging moved to TRACE, so it is off unless an operator opts in. Anything
 * still logged at a level that is on in a deployed system has its values masked instead, because
 * demoting those would throw away a real diagnostic — "N records changed" where N is not 1 is an
 * anomaly worth reporting.
 */
@Tag("Fast")
public class DbDriverSqlRedactionTest {

	/** The exact statement shape that leaked. */
	@Test
	public void anAccessTokenInsertIsMasked() {
		String jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.dBjftJeZ4CVP";
		String sql = "INSERT INTO vc_apiaccesstoken (id,accesstoken,clientRef,userRef,creationDate) "
				+ "VALUES (321768025,'" + jwt + "',83858129,17,TO_DATE('11-Aug-2026 12:20:47','DD-MON-YYYY HH24:MI:SS'))";

		String redacted = DbDriver.redactSqlForLog(sql);

		assertFalse(redacted.contains(jwt), "the token must not survive: " + redacted);
		assertFalse(redacted.contains("eyJ"), "not even a fragment of it: " + redacted);
		assertTrue(redacted.contains("INSERT INTO vc_apiaccesstoken"),
				"the statement shape is what makes the log useful, and must survive");
		assertTrue(redacted.contains("321768025"),
				"unquoted numeric keys are not secrets and stay readable");
	}

	/** Every quoted literal goes, not just the long or suspicious-looking ones. */
	@Test
	public void allQuotedLiteralsAreMasked() {
		assertEquals("UPDATE t SET a='<redacted>', b='<redacted>' WHERE id=7",
				DbDriver.redactSqlForLog("UPDATE t SET a='secret', b='x' WHERE id=7"));
	}

	/** A doubled quote is SQL's escape for a literal quote, and must not end the literal early. */
	@Test
	public void theSqlQuoteEscapeDoesNotTerminateALiteral() {
		String redacted = DbDriver.redactSqlForLog("INSERT INTO t VALUES ('O''Brien secret', 42)");
		assertFalse(redacted.contains("Brien"), "the escaped-quote literal must be fully masked: " + redacted);
		assertTrue(redacted.contains("42"));
	}

	/** Nothing to mask must not become something to debug. */
	@Test
	public void statementsWithoutLiteralsAreUnchanged() {
		String sql = "DELETE FROM vc_biomodel WHERE id=12345";
		assertEquals(sql, DbDriver.redactSqlForLog(sql));
	}

	@Test
	public void nullIsTolerated() {
		assertNull(DbDriver.redactSqlForLog(null));
	}
}
