package cbit.vcell.modeldb;

import cbit.vcell.model.ReactionQuerySpec;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.db.DatabaseSyntax;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SpecialUser;
import org.vcell.util.document.User;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Search values a client supplies stay inside SQL string literals or are refused, and test-suite operations other than
 * finding a model's geometry need the test account or an administrator listed in the database.
 */
@Tag("Fast")
public class SearchQueryLiteralTest {

	private static final String HOSTILE = "x') OR 1=1 OR ('";

	/** The SQL with every well-formed string literal removed: what is left is the query's own structure. */
	private static String outsideLiterals(String sql) {
		return sql.replaceAll("'(?:[^']|'')*'", "''");
	}

	@Test
	public void reactionSearchStringsStayInsideLiterals() {
		User user = new User("someone", new KeyValue("17"));
		ReactionQuerySpec byParticipant = new ReactionQuerySpec(HOSTILE, null, HOSTILE, null, HOSTILE, null);
		ReactionQuerySpec byWildCard = new ReactionQuerySpec(HOSTILE, null);
		for (ReactionQuerySpec spec : new ReactionQuerySpec[] { byParticipant, byWildCard }) {
			for (DatabaseSyntax syntax : DatabaseSyntax.values()) {
				String sql = ReactStepTable.table.getSQLUserReactionListQuery(spec, user, syntax);
				assertTrue(sql.contains("'x'') OR 1=1 OR ('''"), "the value is quoted with its quotes doubled: " + sql);
				assertFalse(outsideLiterals(sql).contains("1=1"), "nothing of the value reaches the query structure: " + sql);
			}
		}
	}

	@Test
	public void idSearchAcceptsOnlyNumbers() {
		assertEquals("123", DictionaryDbDriver.numericIdLiteral("123"));
		assertEquals("123", DictionaryDbDriver.numericIdLiteral(" 123 "));
		assertThrows(IllegalArgumentException.class, () -> DictionaryDbDriver.numericIdLiteral("0 OR 1=1"));
		assertThrows(IllegalArgumentException.class, () -> DictionaryDbDriver.numericIdLiteral("1; DROP TABLE x"));
		assertThrows(IllegalArgumentException.class, () -> DictionaryDbDriver.numericIdLiteral("'1'"));
	}

	private static TreeMap<SpecialUser.SPECIAL_CLAIM, TreeMap<User, String>> adminsAre(User... admins) {
		TreeMap<User, String> adminMap = new TreeMap<>(new User.UserNameComparator());
		for (User admin : admins) {
			adminMap.put(admin, "");
		}
		TreeMap<SpecialUser.SPECIAL_CLAIM, TreeMap<User, String>> specials = new TreeMap<>();
		specials.put(SpecialUser.SPECIAL_CLAIM.admins, adminMap);
		return specials;
	}

	@Test
	public void testSuiteNeedsTheTestAccountOrADatabaseAdmin() {
		User admin = new User("anadmin", new KeyValue("5"));
		var specials = adminsAre(admin);

		assertTrue(DBTopLevel.mayRunTestSuiteOP(new User(User.VCellTestAccountName, new KeyValue("9")), specials));
		assertTrue(DBTopLevel.mayRunTestSuiteOP(new User("anadmin", new KeyValue("5")), specials));

		assertFalse(DBTopLevel.mayRunTestSuiteOP(new User("someone", new KeyValue("17")), specials));
		assertFalse(DBTopLevel.mayRunTestSuiteOP(null, specials));
		assertFalse(DBTopLevel.mayRunTestSuiteOP(new User("someone", new KeyValue("17")), new TreeMap<>()));
		// the admin's name with another account's key is not the admin
		assertFalse(DBTopLevel.mayRunTestSuiteOP(new User("anadmin", new KeyValue("17")), specials));
		// claims carried by the caller's own User object count for nothing; only the database's list does
		SpecialUser claimsAdmin = new SpecialUser("someone", new KeyValue("17"),
				new SpecialUser.SPECIAL_CLAIM[] { SpecialUser.SPECIAL_CLAIM.admins });
		assertFalse(DBTopLevel.mayRunTestSuiteOP(claimsAdmin, specials));
	}
}
