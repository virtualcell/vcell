package org.vcell.rest.client;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
//			ClientResource resource = new ClientResource("http://restlet.org");  
//			  
//			// Customize the referrer property  
//			resource.setReferrerRef("http://www.mysite.org");  
//			  
//			// Write the response entity on the console  
//			resource.get().write(System.out);  
			
			
			// Prepare the request  
			ClientResource resource = new ClientResource("http://localhost:8182/");  
			  
			// Add the client authentication to the call  
			ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;  
			ChallengeResponse authentication = new ChallengeResponse(scheme,  
			        "scott", "tiger");  
			resource.setChallengeResponse(authentication);  
			  
			// Send the HTTP GET request  
			resource.get();  
			  
			if (resource.getStatus().isSuccess()) {  
			    // Output the response entity on the JVM console  
			    resource.getResponseEntity().write(System.out);  
			} else if (resource.getStatus()  
			        .equals(Status.CLIENT_ERROR_UNAUTHORIZED)) {  
			    // Unauthorized access  
			    System.out  
			            .println("Access authorized by the server, check your credentials");  
			} else {  
			    // Unexpected status  
			    System.out.println("An unexpected status was returned: "  
			            + resource.getStatus());  
			}  


		} catch (Throwable e){
			e.printStackTrace(System.out);
		}
	}

}
