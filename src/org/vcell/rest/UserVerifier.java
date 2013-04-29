package org.vcell.rest;

import java.sql.SQLException;
import java.util.HashMap;

import org.restlet.security.SecretVerifier;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.modeldb.AdminDBTopLevel;


public class UserVerifier extends SecretVerifier {
	private HashMap<String,UserInfo> userMap = new HashMap<String,UserInfo>();
	private AdminDBTopLevel adminDbTopLevel = null;
	private long lastQueryTimestampMS = 0;
	private final static long MIN_QUERY_TIME_MS = 1000*5; // 5 seconds
	
	public UserVerifier(AdminDBTopLevel adminDbTopLevel){
		this.adminDbTopLevel = adminDbTopLevel;
	}

	public User authenticate(String userid, char[] secret){
		DigestedPassword digestedPassword = new UserLoginInfo.DigestedPassword(new String(secret));
		UserInfo userInfo = userMap.get(userid);
		if (userInfo!=null){
			if (userInfo.digestedPassword0.equals(digestedPassword)){
				return new User(userInfo.userid,userInfo.id);
			}
		}
		if ((System.currentTimeMillis()-lastQueryTimestampMS)>MIN_QUERY_TIME_MS){
			synchronized (adminDbTopLevel) {
				UserInfo[] userInfos = null;
				try {
					userInfos = adminDbTopLevel.getUserInfos(true);
				} catch (ObjectNotFoundException e) {
					e.printStackTrace();
				} catch (DataAccessException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				// refresh stored list of user infos (for authentication)
				if (userInfos!=null){
					userMap.clear();
					for (UserInfo userInfo2 : userInfos) {
						userMap.put(userInfo2.userid,userInfo2);
					}
				}
				lastQueryTimestampMS = System.currentTimeMillis();
				userInfo = userMap.get(userid);
				if (userInfo!=null){
					if (userInfo.digestedPassword0.equals(digestedPassword)){
						return new User(userInfo.userid,userInfo.id);
					}else{
						return null;
					}
				}else{
					return null;
				}
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
