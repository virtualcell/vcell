package org.vcell.rest.users;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;

public final class LoginRestlet extends Restlet {
	public LoginRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request request, Response response) {
	    String html = 
	    	"<html>" +
	    		"/login ... should have been redirected."+
	    	"</html>";
	    response.setEntity(html, MediaType.TEXT_HTML);
	}
}