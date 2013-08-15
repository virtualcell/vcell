package org.vcell.rest;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.security.Verifier;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.ApiClient;


public class UserVerifier implements Verifier {
	private static class AuthenticationInfo {
		final User user;
		final DigestedPassword digestedPassword;
		AuthenticationInfo(User user, DigestedPassword digestedPassword){
			this.user = user;
			this.digestedPassword = digestedPassword;
		}
	}
	private HashMap<String,AuthenticationInfo> useridMap = new HashMap<String,AuthenticationInfo>();
	private HashMap<String,ApiAccessToken> accessTokenMap = new HashMap<String,ApiAccessToken>();
	private HashMap<String,ApiClient> clientidMap = new HashMap<String,ApiClient>();
	private AdminDBTopLevel adminDbTopLevel = null;
	private long lastQueryTimestampMS = 0;
	private final static long MIN_QUERY_TIME_MS = 1000*5; // 5 seconds
	
	public UserVerifier(AdminDBTopLevel adminDbTopLevel){
		this.adminDbTopLevel = adminDbTopLevel;
	}

	public User authenticateUser(String userid, char[] secret){
		DigestedPassword digestedPassword = UserLoginInfo.DigestedPassword.createAlreadyDigested(new String(secret));
		AuthenticationInfo authInfo = useridMap.get(userid);
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
					useridMap.put(userid,new AuthenticationInfo(user,digestedPassword));
				}
				lastQueryTimestampMS = System.currentTimeMillis();
				return user;
			}
		}else{
			return null;
		}
	}
	
	public ApiAccessToken generateApiAccessToken(KeyValue apiClientKey, User user, Date expirationDate) throws ObjectNotFoundException, DataAccessException, SQLException{
		return adminDbTopLevel.generateApiAccessToken(apiClientKey, user, expirationDate, true);
	}

	public ApiAccessToken getApiAccessToken(String accessToken) throws SQLException, DataAccessException{
		ApiAccessToken apiAccessToken = this.accessTokenMap.get(accessToken);
		if (apiAccessToken==null){
			apiAccessToken = adminDbTopLevel.getApiAccessToken(accessToken, true);
			if (apiAccessToken!=null){
				accessTokenMap.put(accessToken, apiAccessToken);
			}
		}
		if (apiAccessToken!=null){
			if (apiAccessToken.isValid()){
				return apiAccessToken;
			}
		}
		throw new RuntimeException("invalid access token");
	}
	
	public ApiClient getApiClient(String clientId) throws SQLException, DataAccessException{
		ApiClient apiClient = this.clientidMap.get(clientId);
		if (apiClient==null){
			apiClient = adminDbTopLevel.getApiClient(clientId, true);
			if (apiClient!=null){
				clientidMap.put(clientId, apiClient);
			}
		}
		if (apiClient!=null){
			if (apiClient.isValid()){
				return apiClient;
			}
		}
		throw new RuntimeException("invalid client");
	}

	@Override
	public int verify(Request request, Response response) {
		ChallengeResponse challengeResponse = request.getChallengeResponse();
		if (challengeResponse != null && challengeResponse.getIdentifier().equals("access_token") && challengeResponse.getSecret()!=null){
			try {
				ApiAccessToken accessToken = getApiAccessToken(new String(challengeResponse.getSecret()));
				if (accessToken != null){
					return RESULT_VALID;
				}else{
					return RESULT_INVALID;
				}
			}catch (Exception e){
				e.printStackTrace(System.out);
				return RESULT_INVALID;
			}
		}else{
			return RESULT_MISSING;
		}
	}
}
