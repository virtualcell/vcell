package cbit.vcell.modeldb;

import java.io.Serializable;
import java.util.Date;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class ApiAccessToken implements Serializable {
	
	public enum AccessTokenStatus {
		created("created"),
		invalidated("invalidated");
		
		String databaseString = null;
		
		private AccessTokenStatus(String databaseString){
			if (databaseString.length()>20){
				throw new IllegalArgumentException("cannot have more than 20 characters is databaseString, current database table column size limit.");
			}
			this.databaseString = databaseString;
		}
		
		public String getDatabaseString(){
			return databaseString;
		}
		
		public static AccessTokenStatus fromDatabaseString(String databaseString){
			for (AccessTokenStatus status : values()){
				if (status.getDatabaseString().equals(databaseString)){
					return status;
				}
			}
			return null;
		}
		
	};
	
	private KeyValue key;
	private String token;
	private KeyValue apiClientKey;
	private User user;
	private Date creationDate;
	private Date expiration;
	private AccessTokenStatus status;
	
	public ApiAccessToken(KeyValue key, String token, KeyValue apiClientKey, User user, Date creationDate, Date expiration, AccessTokenStatus status) {
		super();
		this.key = key;
		this.token = token;
		this.apiClientKey = apiClientKey;
		this.user = user;
		this.creationDate = creationDate;
		this.expiration = expiration;
		this.status = status;
	}

	public KeyValue getKey() {
		return key;
	}

	public String getToken() {
		return token;
	}

	public KeyValue getApiClientKey() {
		return apiClientKey;
	}

	public User getUser() {
		return user;
	}
	
	public Date getCreationDate(){
		return creationDate;
	}

	public Date getExpiration() {
		return expiration;
	}

	public boolean isExpired() {
		return expiration.before(new Date());
	}

	public AccessTokenStatus getStatus() {
		return status;
	}


}
