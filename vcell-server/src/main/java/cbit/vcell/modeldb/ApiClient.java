package cbit.vcell.modeldb;

import org.vcell.util.document.KeyValue;

public class ApiClient {

	private KeyValue key;
	private String clientName;
	private String clientId;
	private String clientSecret;

	public ApiClient(KeyValue key, String clientName, String clientId, String clientSecret) {
		super();
		this.key = key;
		this.clientName = clientName;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public String getClientSecret() {
		return clientSecret;
	}
	
	public KeyValue getKey(){
		return key;
	}

	public boolean isValid() {
		return true;
	}

}
