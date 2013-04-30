package org.vcell.rest;

import java.sql.SQLException;
import java.util.HashMap;

import org.restlet.security.SecretVerifier;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.modeldb.AdminDBTopLevel;


public class UserVerifier extends SecretVerifier {
	public static class AuthenticationInfo {
		final User user;
		final DigestedPassword digestedPassword;
		AuthenticationInfo(User user, DigestedPassword digestedPassword){
			this.user = user;
			this.digestedPassword = digestedPassword;
		}
	}
	private HashMap<String,AuthenticationInfo> userMap = new HashMap<String,AuthenticationInfo>();
	private AdminDBTopLevel adminDbTopLevel = null;
	private long lastQueryTimestampMS = 0;
	private final static long MIN_QUERY_TIME_MS = 1000*5; // 5 seconds
	
	public UserVerifier(AdminDBTopLevel adminDbTopLevel){
		this.adminDbTopLevel = adminDbTopLevel;
	}

	public User authenticate(String userid, char[] secret){
		DigestedPassword digestedPassword = new UserLoginInfo.DigestedPassword(new String(secret));
		AuthenticationInfo authInfo = userMap.get(userid);
		if (authInfo!=null){
			if (authInfo.digestedPassword.equals(digestedPassword)){
				return authInfo.user;
			}
		}
		if ((System.currentTimeMillis()-lastQueryTimestampMS)>MIN_QUERY_TIME_MS){
			synchronized (adminDbTopLevel) {
				User user = null;
				try {
					user = adminDbTopLevel.getUser(userid, digestedPassword,true);
				} catch (ObjectNotFoundException e) {
					e.printStackTrace();
				} catch (DataAccessException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				// refresh stored list of user infos (for authentication)
				if (user!=null){
					userMap.put(userid,new AuthenticationInfo(user,digestedPassword));
				}
				lastQueryTimestampMS = System.currentTimeMillis();
				return user;
			}
		}else{
			return null;
		}
	}

	@Override
	public int verify(String identifier, char[] secret) {
		User user = authenticate(identifier, secret);
		if (user!=null){
			return RESULT_VALID;
		}else{
			return RESULT_INVALID;
		}
	}
}
