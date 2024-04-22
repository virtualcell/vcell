package org.vcell.restq.auth;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.ApiAccessToken.AccessTokenStatus;
import cbit.vcell.modeldb.ApiClient;
import cbit.vcell.modeldb.DatabaseServerImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.lang.JoseException;
import org.vcell.auth.JWTUtils;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;


@ApplicationScoped
public class OldUserService {
	private final static Logger lg = LogManager.getLogger(OldUserService.class);

	private static class AuthenticationInfo {
		final User user;
		final DigestedPassword digestedPassword;
		AuthenticationInfo(User user, DigestedPassword digestedPassword){
			this.user = user;
			this.digestedPassword = digestedPassword;
		}
	}
	public final static String DEFAULT_CLIENTID = "85133f8d-26f7-4247-8356-d175399fc2e6";
	public enum AuthenticationStatus {
		missing,
		invalid,
		stale,
		valid
	};
	private HashMap<String,AuthenticationInfo> useridMap = new HashMap<String,AuthenticationInfo>();
	private HashMap<String,ApiAccessToken> accessTokenMap = new HashMap<String,ApiAccessToken>();
	private HashMap<String,ApiClient> clientidMap = new HashMap<String,ApiClient>();
	private AdminDBTopLevel adminDbTopLevel = null;
	private long lastQueryTimestampMS = 0;
	private final static long MIN_QUERY_TIME_MS = 1000*5; // 5 seconds

	@Inject
	public OldUserService(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException {
		DatabaseServerImpl databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
		this.adminDbTopLevel = databaseServer.getAdminDBTopLevel();
	}

	public User authenticateUser(String userid, char[] secret){
		DigestedPassword digestedPassword = DigestedPassword.createAlreadyDigested(new String(secret));
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
					user = adminDbTopLevel.getUser(userid, digestedPassword,true,false);
				} catch (DataAccessException | SQLException  e) {
					lg.error(e);
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
	
	public ApiAccessToken generateApiAccessToken(KeyValue apiClientKey, User user) throws ObjectNotFoundException, DataAccessException, SQLException{
		return adminDbTopLevel.generateApiAccessToken(apiClientKey, user, getNewExpireDate(), true);
	}

	public ApiAccessToken getApiAccessToken(String jwtToken) throws SQLException, DataAccessException {
		return getApiAccessToken(jwtToken, true);
	}

	public ApiAccessToken getApiAccessToken(String jwtToken, boolean bValidateJWT) throws SQLException, DataAccessException{
		try {
			if (bValidateJWT && !JWTUtils.verifyJWS(jwtToken)){
				lg.warn("token was not valid");
				return null;
			}
		} catch (MalformedClaimException | JoseException e) {
			lg.error("token was not valid", e);
			return null;
		}

		ApiAccessToken apiAccessToken = this.accessTokenMap.get(jwtToken);
		if (apiAccessToken==null){
			apiAccessToken = adminDbTopLevel.getApiAccessToken(jwtToken, true);
			if (apiAccessToken!=null){
				accessTokenMap.put(jwtToken, apiAccessToken);
			}
		}
		return apiAccessToken;
	}
	
	public void invalidateApiAccessToken(String accessToken) throws SQLException, DataAccessException{
		ApiAccessToken apiAccessToken = getApiAccessToken(accessToken);
		
		if (apiAccessToken!=null){
			adminDbTopLevel.setApiAccessTokenStatus(apiAccessToken, AccessTokenStatus.invalidated, true);
			accessTokenMap.remove(accessToken);
		}
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

	private Date getNewExpireDate() {
		long oneHourMs = 1000*60*60;
		long oneDayMs = oneHourMs * 24;
		long tokenLifetimeMs = oneDayMs * 30;
		Date expireTime = new Date(System.currentTimeMillis() + tokenLifetimeMs);
		return expireTime;
	}
}
