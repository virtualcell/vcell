package org.vcell.api.common;




public class AccessTokenRepresentation {
	
	public String token;
	public long creationDateSeconds;
	public long expireDateSeconds;
	public String userId;
	public String userKey;
		
	public AccessTokenRepresentation(String token) {
		this.token = token;
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
