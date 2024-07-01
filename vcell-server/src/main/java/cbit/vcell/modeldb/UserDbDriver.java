/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;

import cbit.vcell.modeldb.ApiAccessToken.AccessTokenStatus;
import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.lang.JoseException;
import org.vcell.auth.JWTUtils;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * This type was created in VisualAge.
 */
public class UserDbDriver /*extends DbDriver*/ {
	public static final UserTable userTable = UserTable.table;
	private Random passwordGen = null; 
	private static final Logger lg = LogManager.getLogger(UserDbDriver.class);
/**
 * LocalDBManager constructor comment.
 */
public UserDbDriver() {
	super();
}


public User.SpecialUser getUserFromUserid(Connection con, String userid) throws SQLException {
	PreparedStatement pstmt;
	String sql;
	ResultSet rset;
	if (lg.isTraceEnabled()) {
		lg.trace("UserDbDriver.getUserFromUserid(userid=" + userid + ")");
	}
	sql = 	"SELECT DISTINCT " + UserTable.table.id.getQualifiedColName()+" userkey" + "," + SpecialUsersTable.table.special.getQualifiedColName() +" as special" +
			" FROM " + userTable.getTableName() +
			" LEFT JOIN " + SpecialUsersTable.table.getTableName() +
			" ON " + SpecialUsersTable.table.userRef.getQualifiedColName()+"="+userTable.id.getQualifiedColName() +
			" WHERE " + UserTable.table.userid + " = ?";
			
	if (lg.isTraceEnabled()) {
		lg.trace(sql);
	}
	pstmt = con.prepareStatement(sql);
	pstmt.setString(1, userid);
	BigDecimal userKey = null;
	ArrayList<User.SPECIAL_CLAIM> specials = new ArrayList<>();
	try {
		rset = pstmt.executeQuery();
		while (rset.next()) {
			BigDecimal bigDecimal = rset.getBigDecimal("userkey");
			if(userKey == null) {
				userKey = bigDecimal;
			}else if(!bigDecimal.equals(userKey)) {
				throw new SQLException("Not expecting more than 1 id for userid='"+userid+"'");
			}
			String special = rset.getString("special");
			if(!rset.wasNull()) {
				try {
					specials.add(User.SPECIAL_CLAIM.fromDatabase(special));
				}catch(Exception e) {
					//keep going
					lg.error(e.getMessage(), e);
				}
			}
		}
	} finally {
		pstmt.close();
	}
	if(userKey == null) {
		return null;
	}
	return new User.SpecialUser(userid, new KeyValue(userKey),specials.toArray(new User.SPECIAL_CLAIM[0]));
}

	public boolean removeUserIdentity(Connection con, User user, String authSubject, String authIssuer) throws SQLException {
		String sql = "DELETE FROM " + UserIdentityTable.table.getTableName() +
				" WHERE " +
					UserIdentityTable.authSubject + " = " + "'"+authSubject+"'" + " AND " +
					UserIdentityTable.authIssuer + " = " + "'"+authIssuer+"'" + " AND " +
					UserIdentityTable.userRef + " = " + user.getID().toString();
		int numRowsDeleted = DbDriver.updateCleanSQL(con, sql, DbDriver.UpdateExpectation.ROW_UPDATE_IS_POSSIBLE);
		return numRowsDeleted > 0;
	}

	public void mapUserIdentity(Connection con, User user, String authSubject, String authIssuer, KeyFactory keyFactory) throws SQLException {
		String sql;

		List<UserIdentity> identities = getUserIdentitiesFromUser(con, user);
		for (UserIdentity identity : identities) {
			if (identity.subject().equals(authSubject) && identity.issuer().equals(authIssuer)) {
				if (lg.isTraceEnabled()) {
					lg.trace("UserDbDriver.addUserIdentity. Identity already exists with (user, issuer, subject) = ("+user.getName()+", "+authIssuer+", "+authSubject+")");
				}
				return;
			}
		}
		if(lg.isTraceEnabled()){
			lg.trace("UserDbDriver.setUserIdentity. Creating new entry with (issuer, subject) = ("+authIssuer+", "+authSubject+")");
		}
		sql = "INSERT INTO " + UserIdentityTable.table.getTableName() +
				" VALUES (" +
				keyFactory.nextSEQ() + "," +
				user.getID().toString() + "," +
				"'"+authSubject+"'" + "," +
				"'"+authIssuer+"'" + "," +
				"CURRENT_TIMESTAMP)";
		DbDriver.updateCleanSQL(con, sql, DbDriver.UpdateExpectation.ROW_UPDATE_IS_EXPECTED);
	}

	public List<UserIdentity> getUserIdentitiesFromSubjectAndIssuer(Connection con, String subject, String issuer) throws SQLException {
		Statement stmt;
		String sql;
		ResultSet rset;
		if (lg.isTraceEnabled()) {
			lg.trace("UserDbDriver.getUserIdentityFromSubjectAndIssuer(userid=" + subject + ")");
		}
		sql = 	"SELECT " + UserTable.table.userid.getUnqualifiedColName() + "," +
				UserIdentityTable.userRef.getUnqualifiedColName() + "," +
				UserIdentityTable.authSubject.getUnqualifiedColName() + "," +
				UserIdentityTable.authIssuer.getUnqualifiedColName() + "," +
				UserIdentityTable.table.id.getQualifiedColName() + " as user_identity_key " + "," +
				UserIdentityTable.insertDate.getQualifiedColName() +
				" FROM " + userTable.getTableName() +
				" JOIN " + UserIdentityTable.table.getTableName() +
				" ON " + UserIdentityTable.table.userRef.getUnqualifiedColName()+"="+userTable.id.getQualifiedColName() +
				" WHERE " + UserIdentityTable.authSubject.getUnqualifiedColName() + " = '" + subject + "'" +
				" AND " + UserIdentityTable.authIssuer.getUnqualifiedColName() + " = '" + issuer + "'";

		if (lg.isTraceEnabled()) {
			lg.trace(sql);
		}
		stmt = con.createStatement();
		ArrayList<UserIdentity> userIdentities = new ArrayList<>();
		try {
			rset = stmt.executeQuery(sql);
			while (rset.next()) {
				BigDecimal userBD = rset.getBigDecimal(UserIdentityTable.userRef.getUnqualifiedColName());
				String userID = rset.getString(UserTable.table.userid.getUnqualifiedColName());
				User userFromDB = new User(userID, new KeyValue(userBD));
				UserIdentity userIdentity = UserIdentityTable.table.getUserIdentity(rset, userFromDB, "user_identity_key");
				userIdentities.add(userIdentity);
			}
		} finally {
			stmt.close();
		}
		return userIdentities;
	}

	public List<UserIdentity> getUserIdentitiesFromUser(Connection con, User user) throws SQLException {
		Statement stmt;
		String sql;
		ResultSet rset;
		if (lg.isTraceEnabled()) {
			lg.trace("UserDbDriver.getIdentitiesFromUser(userid=" + user.getName() + ")");
		}
		sql = 	"SELECT " + UserTable.table.userid.getUnqualifiedColName() + "," +
				UserIdentityTable.userRef.getUnqualifiedColName() + "," +
				UserIdentityTable.authSubject.getUnqualifiedColName() + "," +
				UserIdentityTable.authIssuer.getUnqualifiedColName() + "," +
				UserIdentityTable.table.id.getQualifiedColName() + " as user_identity_key " + "," +
				UserIdentityTable.insertDate.getQualifiedColName() +
				" FROM " + userTable.getTableName() +
				" JOIN " + UserIdentityTable.table.getTableName() +
				" ON " + UserIdentityTable.table.userRef.getUnqualifiedColName()+"="+userTable.id.getQualifiedColName() +
				" WHERE " + UserIdentityTable.userRef.getUnqualifiedColName() + "=" + user.getID().toString();

		if (lg.isTraceEnabled()) {
			lg.trace(sql);
		}
		stmt = con.createStatement();
		ArrayList<UserIdentity> userIdentities = new ArrayList<>();
		try {
			rset = stmt.executeQuery(sql);
			while (rset.next()) {
				BigDecimal userBD = rset.getBigDecimal(UserIdentityTable.userRef.getUnqualifiedColName());
				String userID = rset.getString(UserTable.table.userid.getUnqualifiedColName());
				User userFromDB = new User(userID, new KeyValue(userBD));
				UserIdentity userIdentity = UserIdentityTable.table.getUserIdentity(rset, userFromDB, "user_identity_key");
				userIdentities.add(userIdentity);
			}
		} finally {
			stmt.close();
		}
		return userIdentities;
	}
/**
 * @param con database connection
 * @param userid
 * @param digestedPassword 
 * @param internal allow use of master password
 * @return User object or null if userid incorrect or password invalid
 * @throws SQLException
 */
public User getUserFromUseridAndPassword(Connection con, String userid, UserLoginInfo.DigestedPassword digestedPassword, boolean internal) throws SQLException {
	Statement stmt = null;
	String sql;
	ResultSet rset;
	if (lg.isTraceEnabled()) {
		lg.trace("UserDbDriver.getUserFromUseridAndPassword(userid='" + userid+",xxx)");
	}
	sql = 	"SELECT " + UserTable.table.id + ","+UserTable.table.digestPW+","+UserTable.table.password+
			" FROM " + userTable.getTableName() + 
			" WHERE " + UserTable.table.userid + " = '" + userid + "'";
			
	//lg.info(sql);
	
	User user = null;
	try {
		stmt = con.createStatement();
		rset = stmt.executeQuery(sql);
		if (rset.next()) {
			boolean bUserAuthenticated = false;
			KeyValue userKey = new KeyValue(rset.getBigDecimal(UserTable.table.id.toString()));
			UserLoginInfo.DigestedPassword userDBDigestedPassword = null;
			String dbpwStr = rset.getString(UserTable.table.digestPW.toString());
			if(rset.wasNull() || dbpwStr == null || dbpwStr.length() == 0){
				//user created account with VCell version that didn't make digestPassword, so make one
				String clearTextPassword = rset.getString(UserTable.table.password.toString());
				if(rset.wasNull() || clearTextPassword == null || clearTextPassword.length() == 0){
					//this should never happen
					throw new SQLException("Database contains no password for user "+userid);
				}
				//create digestedPassword
				userDBDigestedPassword = updatePasswords(con, userKey, clearTextPassword);
			}else{
				userDBDigestedPassword = UserLoginInfo.DigestedPassword.createAlreadyDigested(dbpwStr);
			}
			if(digestedPassword.equals(userDBDigestedPassword)){
				bUserAuthenticated = true;
				if (lg.isInfoEnabled()) {
					lg.info("userid " + userid + " logged in using own password");
				}
			}else  {
				if (internal){
					//lookup administrator password and match for any user
					rset.close();
					sql = "SELECT "+UserTable.table.digestPW +" FROM "+userTable.getTableName()+" WHERE "+UserTable.table.id +" = "+PropertyLoader.ADMINISTRATOR_ID;
					ResultSet adminRset = stmt.executeQuery(sql);
					if(adminRset.next()){
						String adminDBDigestPassword = adminRset.getString(UserTable.table.digestPW.toString());
						bUserAuthenticated = digestedPassword.equals(UserLoginInfo.DigestedPassword.createAlreadyDigested(adminDBDigestPassword));
					}
					if (lg.isInfoEnabled()) {
						lg.info("userid " + userid + " master password check returns " + bUserAuthenticated); 
					}
				} else {
					if (lg.isInfoEnabled()) {
						lg.info("userid " + userid + " not running locally, master password not checked"); 
					}
				}
			}
			if(bUserAuthenticated){
				user = new User(userid, userKey);
			}
		}
	} finally {
		if(stmt != null){try{stmt.close();}catch(Exception e){lg.error(e.getMessage(), e);}}
	}
	return user;
}
public UserLoginInfo.DigestedPassword updatePasswords(Connection con,KeyValue id,String clearTextPassword) throws SQLException{
	UserLoginInfo.DigestedPassword dbDigestedPassword = new UserLoginInfo.DigestedPassword(clearTextPassword);
	DbDriver.updateCleanSQL(
			con,"UPDATE " + userTable.getTableName() +
			" SET " + 
				userTable.digestPW.getUnqualifiedColName()+" = '" + dbDigestedPassword.getString() + "',"+
				userTable.password.getUnqualifiedColName()+" = '" + clearTextPassword +"' WHERE " + userTable.id + " = " + id);

	return dbDigestedPassword;
}

public void sendLostPassword(Connection con, String userid) throws SQLException, DataAccessException, ObjectNotFoundException {
	User user = getUserFromUserid(con, userid);
	if(user == null){
		throw new ObjectNotFoundException("User name "+userid+" not found.");
	}
	UserInfo userInfo = getUserInfo(con, user.getID());
	String clearTextPassword = randomPassword(); 
	try {
		//Reset User Password
		updatePasswords(con,userInfo.id,clearTextPassword);

		String subject = "re: VCell Info";
		String content = "Your password has been reset to '"+clearTextPassword+"'.  Login with the new password and change your password as soon as possible.  To change your password, log into VCell and select Account->'Update Registration Info...' from the Top Menu.  Enter a new password where indicated.";
		//Send new password to user
		BeanUtils.sendSMTP(
			PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPHostName),
			Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPPort)),
			PropertyLoader.getRequiredProperty(PropertyLoader.vcellSMTPEmailAddress),
			userInfo.email,
			subject,
			content
		);
	} catch (Exception e) {
		lg.error(e.getMessage(), e);
		throw new DataAccessException("Error sending lost password\n"+e.getMessage(),e);
	}
	
}
/**
 * getModel method comment.
 */
public UserInfo getUserInfo(Connection con, KeyValue key) throws SQLException, DataAccessException, ObjectNotFoundException {
	if (lg.isTraceEnabled()) {
		lg.trace("UserDbDriver.getUserInfo(key=" + key + ")");
	}
	String sql;
	sql = 	" SELECT " + " * " + 
			" FROM " + userTable.getTableName() + 
			" WHERE " + userTable.id + " = " + key;

	//lg.info(sql);

	//Connection con = conFact.getConnection();
	Statement stmt = con.createStatement();
	UserInfo userInfo = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//  	ResultSetMetaData metaData = rset.getMetaData();
		//  	for (int i=1;i<=metaData.getColumnCount();i++){
		//	  	lg.info("column("+i+") = "+metaData.getColumnName(i));
		//  	}
		if (rset.next()){
			userInfo = userTable.getUserInfo(rset);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	if (userInfo == null) {
		throw new org.vcell.util.ObjectNotFoundException("UserInfo with id = '" + key + "' not found");
	}
	return userInfo;
}
/**
 * getModel method comment.
 */
public UserInfo[] getUserInfos(Connection con) throws SQLException, DataAccessException, ObjectNotFoundException {
	if (lg.isTraceEnabled()) {
		lg.trace("UserDbDriver.getUserInfos()");
	}
	String sql;
	sql = " SELECT " + " * " + " FROM " + userTable.getTableName();

	//lg.info(sql);

	//Connection con = conFact.getConnection();
	Statement stmt = con.createStatement();
	java.util.Vector<UserInfo> userList = null;
	try {
		ResultSet rset = stmt.executeQuery(sql);

		//  	ResultSetMetaData metaData = rset.getMetaData();
		//  	for (int i=1;i<=metaData.getColumnCount();i++){
		//	  	lg.info("column("+i+") = "+metaData.getColumnName(i));
		//  	}
		userList = new java.util.Vector<UserInfo>();
		UserInfo userInfo;
		while (rset.next()){
			userInfo = userTable.getUserInfo(rset);
			userList.addElement(userInfo);
		}
	} finally {
		stmt.close(); // Release resources include resultset
	}
	if (userList.size() > 0) {
		UserInfo users[] = new UserInfo[userList.size()];
		userList.copyInto(users);
		return users;
	} else {
		return null;
	}
}
/**
 * addModel method comment.
 */
public KeyValue insertUserInfo(Connection con, KeyFactory keyFactory, UserInfo userInfo) throws SQLException {
	if (userInfo == null){
		throw new SQLException("Improper parameters for insertModel");
	}
	if (lg.isTraceEnabled()) {
		lg.trace("UserDbDriver.insertUserInfo(userinfo="+userInfo+")");
	}
	//Connection con = conFact.getConnection();
	KeyValue key = keyFactory.getNewKey(con);
	insertUserInfoSQL(con,keyFactory,key,userInfo);
	return key;
}

private void insertUserInfoSQL(Connection con, KeyFactory keyFactory, KeyValue key, UserInfo userInfo) throws SQLException {
	if (userInfo == null || con == null){
		throw new IllegalArgumentException("Improper parameters for insertUserSQL");
	}

	String sql;
	sql = "INSERT INTO " + userTable.getTableName() + " " +
			userTable.getSQLColumnList() + " VALUES " +
			userTable.getSQLValueList(key, userInfo);
	DbDriver.updateCleanSQL(con,sql);
	
	sql = "INSERT INTO " + UserStatTable.table.getTableName() + " " +
		UserStatTable.table.getSQLColumnList() + " VALUES " +
		UserStatTable.table.getSQLValueList(key,keyFactory);
	DbDriver.updateCleanSQL(con,sql);
}
/**
 * removeModel method comment.
 */
public void removeUserInfo(Connection con, User user) throws SQLException {
	if (user == null){
		throw new IllegalArgumentException("Improper parameters for removeUser");
	}
	if (lg.isTraceEnabled()) {
		lg.trace("UserDbDriver.removeUserInfo(user="+user+")");
	}
	//Connection con = conFact.getConnection();
	removeUserInfoSQL(con,user);
}
/**
 * only the owner can delete a Model
 */
private void removeUserInfoSQL(Connection con, User user) throws SQLException {
	if (user == null){
		throw new IllegalArgumentException("Improper parameters for removeUserInfoSQL");
	}

	//Delete geomname row from GeomMainTable
	String sql;
	sql = "DELETE FROM " + userTable.getTableName() +
			" WHERE " + userTable.id+" = "+user.getID();
			
//lg.info(sql);

	DbDriver.updateCleanSQL(con, sql);
}
/**
 * addModel method comment.
 */
public void updateUserInfo(Connection con, UserInfo userInfo) throws SQLException {
	if (userInfo == null){
		throw new SQLException("Improper parameters for insertModel");
	}
	if (lg.isTraceEnabled()) {
		lg.trace("UserDbDriver.updateUserInfo(userinfo="+userInfo+")");
	}
	//Connection con = conFact.getConnection();
	updateUserInfoSQL(con,userInfo);
}

private void updateUserInfoSQL(Connection con, UserInfo userInfo) throws SQLException {
	if (userInfo == null || con == null){
		throw new IllegalArgumentException("Improper parameters for updateUserInfoSQL");
	}

	String sql;
	sql =	"UPDATE " + userTable.getTableName() +
			" SET "   + userTable.getSQLUpdateList(userInfo) + 
			" WHERE " + userTable.id     + " = " + userInfo.id;

//lg.info(sql);
			
	DbDriver.updateCleanSQL(con,sql);
}

public void updateUserStat(Connection con,KeyFactory keyFactory, UserLoginInfo userLoginInfo) throws SQLException {
	User user = getUserFromUserid(con, userLoginInfo.getUserName());
	String sql;
	sql =	"UPDATE " + UserStatTable.table.getTableName() +
			" SET "   +
				UserStatTable.table.loginCount +" = "+
				UserStatTable.table.loginCount+" + 1"+","+
				UserStatTable.table.lastLogin + " = current_timestamp"+
			" WHERE " + UserStatTable.table.userRef + " = " + user.getID();
	DbDriver.updateCleanSQL(con,sql);
	//
	//Find if we have UserLoginInfo entry
	//
	sql = 	" SELECT " + UserLoginInfoTable.table.id.getUnqualifiedColName()+ 
			" FROM " + UserLoginInfoTable.table.getTableName() + 
			" WHERE " + UserLoginInfoTable.getSQLWhereCondition(user.getID(), userLoginInfo);

	Statement stmt = null;
	BigDecimal userLoginInfoID = null;
	try {
		stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()){
			userLoginInfoID = rset.getBigDecimal(UserLoginInfoTable.table.id.getUnqualifiedColName());
		}
	} finally {
		if(stmt != null){stmt.close();} // Release resources include resultset
	}
	if(userLoginInfoID == null){
		sql = 
			"INSERT INTO " + UserLoginInfoTable.table.getTableName() + " " +
			UserLoginInfoTable.table.getSQLColumnList() + " VALUES " +
			UserLoginInfoTable.getSQLValueList(user.getID(), 1,userLoginInfo, keyFactory);
		DbDriver.updateCleanSQL(con, sql);
	}else{
		DbDriver.updateCleanSQL(con, UserLoginInfoTable.getSQLUpdateLoginCount(user.getID(), userLoginInfo));
	}
}

public ApiAccessToken generateApiAccessToken(Connection con, KeyFactory keyFactory, KeyValue apiClientKey, User user, Date expirationDate) throws SQLException, JoseException {
	String sql;
	KeyValue key = keyFactory.getNewKey(con);

	NumericDate expirationTime = NumericDate.fromMilliseconds(expirationDate.getTime());
	String jwt = JWTUtils.createToken(user, expirationTime);

	try {
		boolean valid = JWTUtils.verifyJWS(jwt);
		if (!valid){
			throw new RuntimeException("generated JWT token is invalid");
		}
		JwtContext jwtContext = new JwtConsumerBuilder()
				.setSkipAllValidators()
				.setDisableRequireSignature()
				.setSkipSignatureVerification()
				.build().process(jwt);

		NumericDate issuedAt = jwtContext.getJwtClaims().getIssuedAt();
		Date creationDate = new Date(issuedAt.getValueInMillis());

		AccessTokenStatus accessTokenStatus = AccessTokenStatus.created;

		sql =	"INSERT INTO " + ApiAccessTokenTable.table.getTableName() + " " +
				ApiAccessTokenTable.table.getSQLColumnList() + " VALUES " +
				ApiAccessTokenTable.table.getSQLValueList(key,jwt,apiClientKey,user,creationDate,expirationDate,accessTokenStatus);

		DbDriver.updateCleanSQL(con,  sql);
		return new ApiAccessToken(key,jwt,apiClientKey,user,creationDate,expirationDate,accessTokenStatus);

	} catch (MalformedClaimException | InvalidJwtException e) {
		throw new RuntimeException(e);
	}

}

public void setApiAccessTokenStatus(Connection con, KeyValue apiAccessTokenKey, AccessTokenStatus accessTokenStatus) throws SQLException, DataAccessException {
	String sql;
	
	sql =	"UPDATE " + ApiAccessTokenTable.table.getTableName() + " " +
			"SET " + ApiAccessTokenTable.table.status.getUnqualifiedColName() + " = " + "'" + accessTokenStatus.getDatabaseString() + "'" +
			"WHERE " + ApiAccessTokenTable.table.id.getUnqualifiedColName() + " = " + apiAccessTokenKey.toString();
	int numRecordsChanged = DbDriver.updateCleanSQL(con,  sql);
	
	if (numRecordsChanged!=1){
		throw new DataAccessException("failed to update ApiAccessCode(key="+apiAccessTokenKey+")");
	}
}


public ApiAccessToken getApiAccessToken(Connection con, DatabaseSyntax dbSyntax, String accessToken) throws SQLException, DataAccessException {
	Statement stmt;
	String sql;
	ResultSet rset;
	ApiAccessTokenTable tokenTable = ApiAccessTokenTable.table;

	if (lg.isTraceEnabled()) {
		lg.trace("UserDbDriver.getApiAccessToken(token=" + accessToken + ")");
	}
	
	sql = 	"SELECT " + userTable.userid.getUnqualifiedColName()+", "+tokenTable.getTableName()+".* " + 
			" FROM " + userTable.getTableName() + ", " + tokenTable.getTableName() + 
			" WHERE " + userTable.id.getQualifiedColName() + " = " + tokenTable.userref +
			" AND " + tokenTable.accesstoken + " = '" + accessToken + "'";
			
	//lg.info(sql);
	stmt = con.createStatement();
	ApiAccessToken apiAccessToken = null;
	try {
		rset = stmt.executeQuery(sql);
		if (rset.next()) {
			apiAccessToken = tokenTable.getApiAccessToken(rset,dbSyntax);
		}
	} finally {
		stmt.close();
	}
	return apiAccessToken;
}

public ApiClient getApiClient(Connection con, String clientId) throws SQLException, DataAccessException {
	Statement stmt;
	String sql;
	ResultSet rset;
	ApiClientTable clientTable = ApiClientTable.table;

	if (lg.isTraceEnabled()) {
		lg.trace("UserDbDriver.getApiClient(clientId=" + clientId + ")");
	}
	
	sql = 	"SELECT " + clientTable.getTableName()+".* " + 
			" FROM " + clientTable.getTableName() + 
			" WHERE " + clientTable.clientId + " = '" + clientId + "'";
			
	//lg.info(sql);
	stmt = con.createStatement();
	ApiClient apiClient = null;
	try {
		rset = stmt.executeQuery(sql);
		if (rset.next()) {
			apiClient = clientTable.getApiClient(rset);
		}
	} finally {
		stmt.close();
	}
	return apiClient;
}

/**
 * @return pseudo-random password
 */
private String randomPassword( ) {
	if (passwordGen != null) {
		return "VC" + Math.abs(passwordGen.nextLong());
	}
	passwordGen = new SecureRandom(); 
	return "VC" + passwordGen.nextLong();
}



}
