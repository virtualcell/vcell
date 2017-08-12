package org.vcell.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.modeldb.UserDbDriver;
import cbit.vcell.modeldb.UserTable;

public class UserDbDriverExtended extends UserDbDriver {

	public UserDbDriverExtended(SessionLog sessionLog) {
		super(sessionLog);
	}

	/**
	 *
	 * @param con not null
	 * @param email not null; lower case used
	 * @return existing user or null
	 * @throws SQLException
	 */
	public List<User> getUserFromEmail(Connection con, String email) throws SQLException {
		String sql = "SELECT " + UserTable.table.id + ','  + UserTable.table.userid +
				" FROM " + userTable.getTableName() +
				" WHERE lower(" + UserTable.table.email + ") = lower('" + email + "')";


		List<User> rval = new ArrayList<>();
		User user;
		try (Statement stmt = con.createStatement() ) {
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				KeyValue userKey = new KeyValue(rset.getBigDecimal(UserTable.table.id.toString()));
				String userid = rset.getString(UserTable.table.userid.toString()) ;
				user = new User(userid, userKey);
				rval.add(user);
			}
		}

		return rval;
	}



}
