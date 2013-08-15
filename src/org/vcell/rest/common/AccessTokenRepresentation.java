package org.vcell.rest.common;

import cbit.vcell.modeldb.ApiAccessToken;



public class AccessTokenRepresentation {
	
	public String token;
	public long creationDateSeconds;
	public long expireDateSeconds;
	public String userId;
	public String userKey;
		
	public AccessTokenRepresentation(String token) {
		this.token = token;
	}

	public AccessTokenRepresentation(ApiAccessToken apiAccessToken) {
		token = apiAccessToken.getToken();
		creationDateSeconds = apiAccessToken.getCreationDate().getTime()/1000;
		expireDateSeconds = apiAccessToken.getExpiration().getTime()/1000;
		userId = apiAccessToken.getUser().getName();
		userKey = apiAccessToken.getUser().getID().toString();
	}

	public String getToken() {
		return token;
	}
	
	public long getExpireDateSeconds(){
		return expireDateSeconds;
	}

	public long getCreationDateSeconds() {
		return creationDateSeconds;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserKey() {
		return userKey;
	}
	
	
}
