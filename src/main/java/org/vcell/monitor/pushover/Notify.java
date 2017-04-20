package org.vcell.monitor.pushover;

import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverException;
import net.pushover.client.PushoverMessage;
import net.pushover.client.PushoverRestClient;

public class Notify {

final static String pushoverAppToken = "BIXClgID9APkUcgktlwCzKMbVi8zEZ";	// Specific to VCell 
final static String pushoverUserIdToken = "xA8q15JFqi3GYuPqK5zcsv1Q183bI9"; // Specific to Ed Boyce; others may be added.

public static void pushMessage(String message){
	
	PushoverClient pushoverClient = new PushoverRestClient();
	PushoverMessage pushoverMessage = PushoverMessage.builderWithApiToken(pushoverAppToken)
			.setUserId(pushoverUserIdToken)
			.setMessage(message)
			.build();
	try {
		pushoverClient.pushMessage(pushoverMessage);
	} catch (PushoverException e) {
		e.printStackTrace();
	}
}


public static void main(String[] args) {
	if (args.length > 0){
		String message = args[0];
		pushMessage(message);
	} else {
		pushMessage("No message specified");
	}
}
}
