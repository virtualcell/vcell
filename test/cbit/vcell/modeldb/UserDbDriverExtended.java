package cbit.vcell.modeldb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class UserDbDriverExtended extends UserDbDriver {

	public UserDbDriverExtended(SessionLog sessionLog) {
		super(sessionLog);
	}
	
	public User getUserFromEmail(Connection con, String email) throws SQLException {
		String sql = "SELECT " + UserTable.table.id + ','  + UserTable.table.userid + 
				" FROM " + userTable.getTableName() + 
				" WHERE " + UserTable.table.email + " = '" + email + "'";
				
		try (Statement stmt = con.createStatement() ) {
			ResultSet rset = stmt.executeQuery(sql);
			if (rset.next()) {
				KeyValue userKey = new KeyValue(rset.getBigDecimal(UserTable.table.id.toString()));
				String userid = rset.getString(UserTable.table.userid.toString()) ;
				User user = new User(userid, userKey);
				return user;
			}
		} 
		
		return null;
	}
	
	

}
