package cbit.vcell.modeldb;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Account rows are written with bind parameters, so caller-supplied text never becomes SQL.
 */
@Tag("Fast")
public class UserSqlBindingTest {

	private static final String HOSTILE = "x', EMAIL=(SELECT DIGESTPW FROM vc_userinfo WHERE ROWNUM=1), TITLE='";

	private static UserInfo hostileUserInfo() {
		UserInfo userInfo = new UserInfo();
		userInfo.id = new KeyValue("100");
		userInfo.userid = HOSTILE;
		userInfo.digestedPassword0 = new UserLoginInfo.DigestedPassword("secret");
		userInfo.email = HOSTILE;
		userInfo.wholeName = "O'Brien";
		userInfo.title = HOSTILE;
		userInfo.company = HOSTILE;
		userInfo.country = HOSTILE;
		userInfo.notify = true;
		return userInfo;
	}

	private static long placeholders(String sql) {
		return sql.chars().filter(c -> c == '?').count();
	}

	@Test
	public void testInsertBindsEveryValue() {
		String values = UserTable.table.getPreparedSQLValueList();
		Object[] params = UserTable.table.getSQLValueParameters(new KeyValue("100"), hostileUserInfo());
		assertEquals(placeholders(values), params.length);
		assertFalse(values.contains("O'Brien") || values.contains(HOSTILE));
	}

	@Test
	public void testUpdateBindsEveryValue() {
		String setList = UserTable.table.getPreparedSQLUpdateList();
		Object[] params = UserTable.table.getSQLUpdateParameters(hostileUserInfo());
		assertEquals(placeholders(setList), params.length);
		assertFalse(setList.contains("O'Brien") || setList.contains(HOSTILE));
	}

	@Test
	public void testMissingRequiredTextStoredAsBefore() {
		// the concatenated SQL stored a null email/name/country as the text "null"; registrations without
		// those claims relied on it to satisfy the NOT NULL constraints
		UserInfo userInfo = hostileUserInfo();
		userInfo.email = null;
		userInfo.wholeName = null;
		userInfo.title = null;
		Object[] params = UserTable.table.getSQLValueParameters(new KeyValue("100"), userInfo);
		assertEquals("null", params[3]);
		assertEquals("null", params[4]);
		assertEquals(null, params[5]);
	}

	@Test
	public void testSqlLiteralDoublesQuotes() {
		assertEquals("'O''Brien'", DbDriver.sqlLiteral("O'Brien"));
		assertEquals("'x'', EMAIL=(SELECT 1), TITLE='''", DbDriver.sqlLiteral("x', EMAIL=(SELECT 1), TITLE='"));
		assertEquals("NULL", DbDriver.sqlLiteral(null));
	}
}
