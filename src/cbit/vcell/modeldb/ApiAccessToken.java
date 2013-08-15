package cbit.vcell.modeldb;

import java.io.Serializable;
import java.util.Date;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class ApiAccessToken implements Serializable {
	
	private KeyValue key;
	private String token;
	private KeyValue apiClientKey;
	private User user;
	private Date creationDate;
	private Date expiration;
	
	public ApiAccessToken(KeyValue key, String token, KeyValue apiClientKey, User user, Date creationDate, Date expiration) {
		super();
		this.key = key;
		this.token = token;
		this.apiClientKey = apiClientKey;
		this.user = user;
		this.creationDate = creationDate;
		this.expiration = expiration;
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

	public boolean isValid() {
		return expiration.after(new Date());
	}


}
